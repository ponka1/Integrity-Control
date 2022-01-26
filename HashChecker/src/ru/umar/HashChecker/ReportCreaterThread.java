package ru.umar.HashChecker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import ru.umar.HashChecker.ReportPanel.State;

public class ReportCreaterThread implements Runnable{
	
	private enum PROCESS{
		BeforeStart, CountFiles, CalculateHashes
	}
	
	private String ext = ".snpt";
	public Thread t;
	public Configuration config;
	private ReportPanel reportPanel = null;
	private byte[] encrypt_key = null;
	private HashManager.HashTypes hashType = null;
	private int counter = 0;
	private int current = 0;
	private long sumSize = 0;
	private long timeStart = 0;
	private File result = null;
	BufferedWriter writer = null;
	
	private PROCESS process = PROCESS.BeforeStart;
	private boolean pause = false;
	private boolean finished = false;
	
	public ReportCreaterThread(Configuration config, ReportPanel reportPanel) {
		t = new Thread(this);
		this.config = config;
		this.encrypt_key = MainHashChecker.encrypt_key;
		this.reportPanel = reportPanel;
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
			reportPanel.setState(ReportPanel.State.Empty, null);
			return;
		}
		
		timeStart = System.currentTimeMillis();
		
		reportPanel.setState(ReportPanel.State.Working, null);
		
		File dir = new File(MainHashChecker.workspace_directory+File.separator+MainHashChecker.reportDirectory);
		dir.mkdirs();
		reportPanel.setConfigName("Конфигурация: "+config.getName());
		reportPanel.setHash("Алгоритм хеширования: "+config.getHash());
		
		
		Date nowDate = new Date();
		DateFormat dateFormat = new SimpleDateFormat("y-MM-dd kk-mm-ss", Locale.ENGLISH);
		String dateName = dateFormat.format(nowDate);
		String configName = config.getName();
		String fileName = configName+"."+dateName+ext;
		if (encrypt_key!=null) {
			try {
				fileName = enctyptString(fileName);
			} catch (Exception e) {e.printStackTrace();}
		}
		
		result = new File(dir.getAbsolutePath()+File.separator+fileName);
		try {
			if (result.exists()) {
				result.delete();
			}
			result.createNewFile();
			writer = new BufferedWriter(new FileWriter(result));
			
			counter = 0;
			process = PROCESS.CountFiles;
			for (String directory: config.getDirectories()) {
				File loop = new File(directory);
				if (!loop.isDirectory()) {
					if (!countFilesCom(loop)) {
						reportPanel.setState(ReportPanel.State.Empty, null);
						return;
					}
				}else {
					if (!countFilesInDir(loop)) {
						reportPanel.setState(ReportPanel.State.Empty, null);
						return;
					}
				}
			}
			reportPanel.setNumFilesToLabel("Всего файлов: "+counter);
			double mbSize = (double)sumSize/1024/1024;
			reportPanel.setSumSize("Суммарный размер: "+String.format("%.2f", mbSize)+" МБ");
			current = 0;
			process = PROCESS.CalculateHashes;
			for (String directory: config.getDirectories()) {
				File cont_dir = new File(directory);
				if (!cont_dir.isDirectory()) {
					if (!printLineToFile(cont_dir, writer)) {
						reportPanel.setState(ReportPanel.State.Empty, null);
						return;
					}
				}else {
					if (!printLinesToFile(cont_dir, writer)) {
						reportPanel.setState(ReportPanel.State.Empty, null);
						return;
					}
				}
			}
			writer.close();
			
			long finish = System.currentTimeMillis();
			double time = ((double)(finish-timeStart))/1000;
			
			reportPanel.setState(ReportPanel.State.Finished, time);
			finished = true;
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	private boolean printLineToFile(File fl, BufferedWriter writer) throws NoSuchAlgorithmException, InterruptedException {
			//==================== Check pause-interrupt ==================\\
			if (isInterrupted()) {
				reportPanel.setState(State.Empty, null);
				return false;
			}
			checkPause();
			//=============================================================\\	
			String adress = fl.getAbsolutePath();
			try {
				String hash = HashManager.getStringHash(fl, hashType);
				String line = adress+"|"+hash;
				if (encrypt_key!=null) {
					line = enctyptString(line);
				}
				writer.write(line+"\n");
				current++;
				int progress = (int) (((double)current/counter)*1000);
				reportPanel.setProgress(progress);
			}catch (Exception e) {
				e.printStackTrace();
				reportPanel.errorPanel.addError(fl, e);
				reportPanel.errorPanel.showPanel();
				current++;
				int progress = (int) (((double)current/counter)*1000);
				reportPanel.setProgress(progress);
			}
		return true;
	}
	private boolean printLinesToFile(File dir, BufferedWriter writer) throws Exception {
		try {
			for (File fl: dir.listFiles()) {
		//==================== Check pause-interrupt ==================\\
				if (isInterrupted()) {
					reportPanel.setState(State.Empty, null);
					return false;
				}
				checkPause();
		//=============================================================\\		
				if (fl.isDirectory()) {
					if (!printLinesToFile(fl, writer)) {
						return false;
					}
				}else {
					String adress = fl.getAbsolutePath();
					try {
						String hash = HashManager.getStringHash(fl, hashType);
						String line = adress+"|"+hash;
						if (encrypt_key!=null) {
							line = enctyptString(line);
						}
						writer.write(line+"\n");
						current++;
						int progress = (int) (((double)current/counter)*1000);
						reportPanel.setProgress(progress);
					}catch (Exception e) {
						e.printStackTrace();
						reportPanel.errorPanel.addError(fl, e);
						reportPanel.errorPanel.showPanel();
						current++;
						int progress = (int) (((double)current/counter)*1000);
						reportPanel.setProgress(progress);
					}
				}
			}
			return true;
		}catch (Exception e) {
			e.printStackTrace();
			reportPanel.errorPanel.addError(dir, e);
			reportPanel.errorPanel.showPanel();
			return true;
		}
	}
	private boolean countFilesCom(File fl) {
		try {
			sumSize +=fl.length();
			this.counter=this.counter+1;
			return true;
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	private boolean countFilesInDir(File dir) {
		for (File fl: dir.listFiles()) {
			try {
				//================== check Pause-Interrupt =================\\
				if (this.counter%100==0) {
					if (isInterrupted()) {
						reportPanel.setState(State.Empty, null);
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
						reportPanel.errorPanel.addError(fl, e);
						reportPanel.errorPanel.showPanel();
					}
				}else {
					sumSize +=fl.length();
					this.counter=this.counter+1;
				}
			}catch (Exception e) {
				e.printStackTrace();
				reportPanel.errorPanel.addError(fl, e);
				reportPanel.errorPanel.showPanel();
				return true;
			}
		}
		return true;
	}
	private String enctyptString(String str) throws Exception {
		String target = str;
		Cipher aes = Cipher.getInstance("AES");
		SecretKeySpec key = new SecretKeySpec(encrypt_key, "AES");
		aes.init(Cipher.ENCRYPT_MODE, key);
		byte[] bytes = aes.doFinal(target.getBytes());
		String res = javax.xml.bind.DatatypeConverter.printHexBinary(bytes);
		return res;
	}
	
	//=========================================================================
	public synchronized void checkPause() throws InterruptedException {
		if (this.pause==true) {
			long finish = System.currentTimeMillis();
			double time = ((double)(finish-timeStart))/1000;
			reportPanel.setState(ReportPanel.State.Pause, time);
			if (process == PROCESS.CountFiles) {
				reportPanel.setNumFilesToLabel("Посчитано файлов: "+counter+"+");
				double mbSize = (double)sumSize/1024/1024;
				reportPanel.setSumSize("Посчитанный размер: "+String.format("%.2f", mbSize)+"+ МБ");
			}
			wait();
		}
	}
	public synchronized boolean isInterrupted() {
		return t.isInterrupted();
	}
	
	public synchronized void interrupt() {
		if (pause) {
			try {
				writer.close();
			} catch (IOException e) {e.printStackTrace();}
			result.delete();
			reportPanel.setState(ReportPanel.State.Empty, null);
			return;
		}
		if (finished) {
			reportPanel.setState(ReportPanel.State.Empty, null);
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
		reportPanel.setState(ReportPanel.State.Working, null);
		if (process == PROCESS.CountFiles) {
			reportPanel.setNumFilesToLabel("Всего файлов:");
			reportPanel.setSumSize("Суммарный размер:");
		}
		notify();
	}
}
