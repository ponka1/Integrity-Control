package ru.umar.HashChecker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class Configuration {
	private String name;
	private ArrayList<String>files;
	private String hashName;
	
	private byte[] encrypt_key = null;
	
	private static String welcome_string = "Configuration HashChecker";
	
	public Configuration(String name, ArrayList<String>files, String hashName) {
		this.name = name;
		this.files = files;
		this.hashName = hashName;
		
		this.encrypt_key = MainHashChecker.encrypt_key;
	}
	
	public void createFile() {
		try {
			File conf = creatingFile(name);
			BufferedWriter writer = new BufferedWriter(new FileWriter(conf));
			writeLine(writer, welcome_string);
			writeLine(writer, hashName);
			for (String str: files) {
				writeLine(writer, str);
			}
			writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private File creatingFile(String name) throws Exception {
		File conf;
		if (encrypt_key!=null) {
			File dir = new File(MainHashChecker.workspace_directory.getAbsolutePath()+File.separator+MainHashChecker.configDirectory);
			dir.mkdirs();
			String nameC = enctyptString(name);
			conf = new File(dir.getAbsolutePath()+File.separator+nameC+".cnfg");
			if (conf.exists()) {
				conf.delete();
			}
			conf.createNewFile();
		}else {
			File dir = new File(MainHashChecker.workspace_directory.getAbsolutePath()+File.separator+MainHashChecker.configDirectory);
			dir.mkdirs();
			conf = new File(dir.getAbsolutePath()+File.separator+name+".cnfg");
			if (conf.exists()) {
				conf.delete();
			}
			conf.createNewFile();
		}
		return conf;
	}
	private void writeLine(BufferedWriter writer, String line) throws Exception {
		if (encrypt_key!=null) {
			String target = line;
			Cipher aes = Cipher.getInstance("AES");
			SecretKeySpec key = new SecretKeySpec(encrypt_key, "AES");
			aes.init(Cipher.ENCRYPT_MODE, key);
			byte[] bytes = aes.doFinal(target.getBytes());
			String res = javax.xml.bind.DatatypeConverter.printHexBinary(bytes);
			writer.write(res+"\n");
		}else {
			writer.write(line+"\n");
		}
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
	public static boolean validConfiguration(File file, byte[] encrypt_key) {
		try {
			BufferedReader checker = new BufferedReader(new FileReader(file));
			String line = checker.readLine();
			checker.close();
			if (encrypt_key!=null) {
				byte[] target = DatatypeConverter.parseHexBinary(line);;
				Cipher aes = Cipher.getInstance("AES");
				SecretKeySpec key = new SecretKeySpec(encrypt_key, "AES");
				aes.init(Cipher.DECRYPT_MODE, key);
				byte[] bytes = aes.doFinal(target);
				String res = new String(bytes);
				if (res.equals(welcome_string)) {
					return true;
				}
			}else {
				if (line.equals(welcome_string)) {
					return true;
				}
			}
		}catch (Exception e) {
			//e.printStackTrace();
		}
		return false;
	}
	
	public static Configuration downloadConfiguration(File file, byte[] encrypt_key) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			reader.readLine();
			if (encrypt_key!=null) {
				String name = decryptLine(file.getName().substring(0, file.getName().length()-5), encrypt_key);
				String hashName = decryptLine(reader.readLine(), encrypt_key);
				ArrayList<String>files = new ArrayList<String>();
				String str;
				while ((str = reader.readLine())!=null) {
					if (str.length()!=0) {
						String ln = decryptLine(str, encrypt_key);
						ln = ln.substring(0, ln.length()-1);
						files.add(ln);
					}
				}
				reader.close();
				Configuration config = new Configuration(name, files, hashName);
				return config;
			}else {
				String name = file.getName().substring(0, file.getName().length()-5);
				String hashName = reader.readLine();
				ArrayList<String>files = new ArrayList<String>();
				String str;
				while ((str = reader.readLine())!=null) {
					if (str.length()!=0) {
						files.add(str);
					}
				}
				reader.close();
				Configuration config = new Configuration(name, files, hashName);
				return config;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	public static boolean validSnapshot(File file, String configName, byte[] encrypt_key) {
		try {
			String name = file.getName();
			if (encrypt_key!=null) {
				name = decryptLine(name, encrypt_key);
			}
			String extF = name.substring(name.length()-5, name.length());
			String confN = name.substring(0, name.indexOf("."));
			if (extF.equals(".snpt")) {
				if (confN.equals(configName)) {
					return true;
				}
			}
		}catch (Exception e) {}
		return false;
	}
//======================== GETTERS ==========================\\
	public static String decryptLine(String line, byte[] encrypt_key) throws Exception {
		byte[] target = DatatypeConverter.parseHexBinary(line);
		Cipher aes = Cipher.getInstance("AES");
		SecretKeySpec key = new SecretKeySpec(encrypt_key, "AES");
		aes.init(Cipher.DECRYPT_MODE, key);
		byte[] bytes = aes.doFinal(target);
		String res = new String(bytes);
		return res;
	}
	public static String encryptLine(String str, byte[] encrypt_key) throws Exception {
		String target = str;
		Cipher aes = Cipher.getInstance("AES");
		SecretKeySpec key = new SecretKeySpec(encrypt_key, "AES");
		aes.init(Cipher.ENCRYPT_MODE, key);
		byte[] bytes = aes.doFinal(target.getBytes());
		String res = javax.xml.bind.DatatypeConverter.printHexBinary(bytes);
		return res;
	}
	public ArrayList<String> getDirectories(){
		return files;
	}
	public String getHash() {
		return hashName;
	}
	public String getName() {
		return name;
	}
}
