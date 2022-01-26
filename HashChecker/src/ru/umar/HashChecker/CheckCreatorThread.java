package ru.umar.HashChecker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class CheckCreatorThread implements Runnable{
	
	private enum PROCESS{
		BeforeStart, CountFiles, CalculateHashes
	}
	
	public Thread t;
	public Configuration config;
	private CheckPanel checkPanel = null;
	private byte[] encrypt_key = null;
	private HashManager.HashTypes hashType = null;
	private int counter = 0;
	private int current = 0;
	private long sumSize = 0;
	private long timeStart = 0;
	
	private File snapshot;
	private File temp;
	//BufferedWriter writer = null;
	
	private PROCESS process = PROCESS.BeforeStart;
	private boolean pause = false;
	private boolean finished = false;
	
	public CheckCreatorThread(Configuration config, CheckPanel checkPanel, String snapshot) {
		t = new Thread(this);
		this.config = config;
		this.encrypt_key = MainHashChecker.encrypt_key;
		this.checkPanel = checkPanel;
		
		checkPanel.setSnapshot("Снимок: "+snapshot);
		String snapName = new String(snapshot);
		if (MainHashChecker.encrypt_key!=null) {
			try {
				snapName = Configuration.encryptLine(snapName, MainHashChecker.encrypt_key);
			} catch (Exception e1) {e1.printStackTrace();}
		}
		this.snapshot = new File(MainHashChecker.workspace_directory+File.separator+MainHashChecker.reportDirectory+File.separator+snapName);
		File tempDir = new File(MainHashChecker.workspace_directory+File.separator+MainHashChecker.tempDirecorty);
		tempDir.mkdirs();
		this.temp = new File(tempDir.getAbsolutePath()+File.separator+MainHashChecker.tempFile);
		if (temp.exists()) {temp.delete();}
		try {temp.createNewFile();} catch (IOException e) {e.printStackTrace();}
		
		switch (config.getHash()) {
		case "MD5":{
			hashType = HashManager.HashTypes.MD5;
			break;
		}
		case "SHA-256":{
			hashType = HashManager.HashTypes.SHA256;
			break;
		}
		case "SHA-512":{
			hashType = HashManager.HashTypes.SHA512;
			break;
		}
		case "ГОСТ Р 34.11-2012":{
			hashType = HashManager.HashTypes.GOST2012;
			break;
		}
		case "ГОСТ Р 34.11-94":{
			hashType = HashManager.HashTypes.GOST94;
			break;
		}
		default:{
			hashType = HashManager.HashTypes.MD5;
			break;
		}
		}
		t.start();
	}
	
	@Override
	public void run() {
		if (t.isInterrupted()) {
			checkPanel.setState(CheckPanel.State.Empty, null);
			return;
		}
		
		timeStart = System.currentTimeMillis();
		
		checkPanel.setState(CheckPanel.State.Working, null);
		
		File dir = new File(MainHashChecker.workspace_directory+File.separator+MainHashChecker.reportDirectory);
		dir.mkdirs();
		checkPanel.setConfigName("Конфигурация: "+config.getName());
		checkPanel.setHash("Алгоритм хеширования: "+config.getHash());
		
		try {
			counter = 0;
			process = PROCESS.CountFiles;
			for (String directory: config.getDirectories()) {
				File loop = new File(directory);
				if (!loop.isDirectory()) {
					if (!countFilesCom(loop)) {
						checkPanel.setState(CheckPanel.State.Empty, null);
						return;
					}
				}else {
					if (!countFilesInDir(loop)) {
						checkPanel.setState(CheckPanel.State.Empty, null);
						return;
					}
				}
			}
			checkPanel.setNumFilesToLabel("Всего файлов: "+counter);
			double mbSize = (double)sumSize/1024/1024;
			checkPanel.setSumSize("Суммарный размер: "+String.format("%.2f", mbSize)+" МБ");
			current = 0;
			process = PROCESS.CalculateHashes;
			for (String directory: config.getDirectories()) {
				File cont_dir = new File(directory);
				if (!cont_dir.isDirectory()) {
					if (!checkOneFile(cont_dir, snapshot, temp)) {
						checkPanel.setState(CheckPanel.State.Empty, null);
						return;
					}
				}else {
					if (!listAndCheckFiles(cont_dir, snapshot, temp)) {
						checkPanel.setState(CheckPanel.State.Empty, null);
						return;
					}
				}
			}
			if (!checkDeleted(snapshot, temp)) {
				checkPanel.setState(CheckPanel.State.Empty, null);
				return;
			}
			checkPanel.setProgress(1000);
			long finish = System.currentTimeMillis();
			double time = ((double)(finish-timeStart))/1000;
			
			checkPanel.setState(CheckPanel.State.Finished, time);
			finished = true;
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	private boolean checkDeleted(File latestFile, File tempfile) throws Exception {
		BufferedReader checker = new BufferedReader(new FileReader(latestFile));
		String str;
		while ((str = checker.readLine())!=null) {
			if (isInterrupted()) {
				checkPanel.setState(CheckPanel.State.Empty, null);
				checker.close();
				return false;
			}
			checkPause();
			//=============================================================\\
			if (encrypt_key!=null) {
				str = decryptLine(str, encrypt_key);
			}
			String strpath=str.substring(0, str.indexOf("|"));
			BufferedReader seeker = new BufferedReader(new FileReader(tempfile));
			String guess;
			boolean finded = false;
			while((guess=seeker.readLine())!=null) {
				if (guess.equals(strpath)) {
					finded=true;
					break;
				}
			}
			if (!finded) {
				checkPanel.addCheckResult("⚊ "+strpath);
				//System.out.println("-- "+strpath);
			}
			seeker.close();
		}
		checker.close();
		return true;
	}
	private boolean listAndCheckFiles(File dir, File latestFile, File tempfile) throws Exception {
		try {
			for (File fl: dir.listFiles()) {
				//==================== Check pause-interrupt ==================\\
				if (isInterrupted()) {
					checkPanel.setState(CheckPanel.State.Empty, null);
					return false;
				}
				checkPause();
				//=============================================================\\	
				if (fl.isDirectory()) {
					if (!listAndCheckFiles(fl, latestFile, tempfile)) {
						return false;
					}
				}else {
					try {
						if(!checkFile(fl, latestFile, tempfile)) {
							return false;
						}
						current++;
						int progress = (int) (((double)current/counter)*1000);
						if (progress==1000) {
							progress=990;
						}
						checkPanel.setProgress(progress);
					}catch (Exception e) {
						e.printStackTrace();
						checkPanel.errorPanel.addError(fl, e);
						checkPanel.errorPanel.showPanel();
						current++;
						int progress = (int) (((double)current/counter)*1000);
						checkPanel.setProgress(progress);
					}
				}
			}
			return true;
		}catch (Exception e) {
			e.printStackTrace();
			checkPanel.errorPanel.addError(dir, e);
			checkPanel.errorPanel.showPanel();
			return true;
		}
	}
	private boolean checkOneFile(File fl, File latestFile, File tempfile) throws Exception {
		if (!checkFile(fl, latestFile, tempfile)) {
			return false;
		}
		current++;
		int progress = (int) (((double)current/counter)*1000);
		if (progress==1000) {
			progress=990;
		}
		checkPanel.setProgress(progress);
		return true;
	}
	private boolean checkFile(File fl, File latestFile, File tempfile) throws Exception {
		BufferedReader reader = new BufferedReader (new FileReader(latestFile));
		BufferedWriter writer = new BufferedWriter (new FileWriter(tempfile, true));
		String path = fl.getAbsolutePath();
		String hash = HashManager.getStringHash(fl, hashType);
		
		String str;
		while ((str=reader.readLine())!=null) {
			//==================== Check pause-interrupt ==================\\
			if (isInterrupted()) {
				checkPanel.setState(CheckPanel.State.Empty, null);
				reader.close();
				writer.close();
				return false;
			}
			checkPause();
			//=============================================================\\
			
			if (encrypt_key!=null) {
				str = decryptLine(str, encrypt_key);
			}
			String strpath=str.substring(0, str.indexOf("|"));
			if (path.equals(strpath)) {
				writer.write(path+"\n");
				writer.close();
				String strhash=str.substring(str.indexOf("|")+1, str.length());
				if (!strhash.equals(hash)) {
					checkPanel.addCheckResult("✱ "+path);
					//System.out.println("** "+path);
				}
				reader.close();
				return true;
			}
		}
		checkPanel.addCheckResult("✛ "+path);
		//System.out.println("++ "+path);
		reader.close();
		writer.close();
		return true;
	}
	private boolean countFilesCom(File fl) {
		try {
			sumSize +=fl.length();
			counter=counter+1;
			return true;
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	private boolean countFilesInDir(File dir) {
		try {
			for (File fl: dir.listFiles()) {
				try {
					//================== check Pause-Interrupt =================\\
					if (this.counter%100==0) {
						if (isInterrupted()) {
							checkPanel.setState(CheckPanel.State.Empty, null);
							return false;
						}
						checkPause();
					}
					//================== Count files ========================\\
					if (fl.isDirectory()) {
						try{
							if (!countFilesInDir(fl)) {
								return false;
							}
						}catch (Exception e) {
							e.printStackTrace();
							checkPanel.errorPanel.addError(fl, e);
							checkPanel.errorPanel.showPanel();
						}
					}else {
						sumSize +=fl.length();
						counter=counter+1;
					}
				}catch (Exception e) {
					e.printStackTrace();
					checkPanel.errorPanel.addError(fl, e);
					checkPanel.errorPanel.showPanel();
					return false;
				}
			}
			return true;
		}catch (Exception e) {
			checkPanel.errorPanel.addError(dir, e);
			checkPanel.errorPanel.showPanel();
			e.printStackTrace();
			return true;
		}
	}
	private String decryptLine(String line, byte[] encrypt_key) throws Exception {
		byte[] target = DatatypeConverter.parseHexBinary(line);
		Cipher aes = Cipher.getInstance("AES");
		SecretKeySpec key = new SecretKeySpec(encrypt_key, "AES");
		aes.init(Cipher.DECRYPT_MODE, key);
		byte[] bytes = aes.doFinal(target);
		String res = new String(bytes);
		return res;
	}
	//===============================================================
	
	public synchronized void checkPause() throws InterruptedException {
		if (this.pause==true) {
			long finish = System.currentTimeMillis();
			double time = ((double)(finish-timeStart))/1000;
			checkPanel.setState(CheckPanel.State.Pause, time);
			if (process == PROCESS.CountFiles) {
				checkPanel.setNumFilesToLabel("Посчитано файлов: "+counter+"+");
				double mbSize = (double)sumSize/1024/1024;
				checkPanel.setSumSize("Посчитанный размер: "+String.format("%.2f", mbSize)+"+ МБ");
			}
			wait();
		}
	}
	public synchronized boolean isInterrupted() {
		return t.isInterrupted();
	}
	
	public synchronized void interrupt() {
		if (pause) {
			checkPanel.setState(CheckPanel.State.Empty, null);
			return;
		}
		if (finished) {
			checkPanel.setState(CheckPanel.State.Empty, null);
			return;
		}
		this.t.interrupt();
	}
	synchronized public void pause() {
		pause = true;
	}
	synchronized public void resume() {
		pause = false;
		timeStart = System.currentTimeMillis();
		checkPanel.setState(CheckPanel.State.Working, null);
		if (process == PROCESS.CountFiles) {
			checkPanel.setNumFilesToLabel("Всего файлов:");
			checkPanel.setSumSize("Суммарный размер:");
		}
		notify();
	}
	
}
