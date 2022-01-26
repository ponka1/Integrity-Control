package ru.umar.HashChecker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import ru.zinal.gosthash.GostHash;

public class HashManager {
	public static enum HashTypes{
		MD5, SHA256, SHA512, GOST94, GOST2012
	}
	
	public static String hash(byte[] bytes, HashTypes hashType) throws NoSuchAlgorithmException {
		String result = null;
		
		MessageDigest md = null;
		switch (hashType) {
		case MD5:{
			md = MessageDigest.getInstance("MD5");
			break;
		}
		case SHA256:{
			md = MessageDigest.getInstance("SHA-256");
			break;
		}
		case SHA512:{
			md = MessageDigest.getInstance("SHA-512");
			break;
		}
		case GOST2012:{
			md = MessageDigest.getInstance("GOST3411-2012.512");
			break;
		}
		case GOST94:{
			String res = GostHash.hashStr(bytes);
			return res;
		}
		default:{
			
			break;
		}
		}
		
		byte[] thedigest = md.digest(bytes);
		result = javax.xml.bind.DatatypeConverter.printHexBinary(thedigest);
		
		return result;
	}
	
	public static String getStringHash(File file, HashTypes hashType) throws IOException, NoSuchAlgorithmException {	
		MessageDigest md = null;
		switch (hashType) {
		case MD5:{
			md = MessageDigest.getInstance("MD5");
			break;
		}
		case SHA256:{
			md = MessageDigest.getInstance("SHA-256");
			break;
		}
		case SHA512:{
			md = MessageDigest.getInstance("SHA-512");
			break;
		}
		case GOST2012:{
			md = MessageDigest.getInstance("GOST3411-2012.512");
			break;
		}
		case GOST94:{
			String res = GostHash.calcStr(file);
			return res;
		}
		default:{
			
			break;
		}
		}
		FileInputStream is = new FileInputStream(file);
		String result = null;
		byte[] buffer = new byte[1024];
		int numRead;
		do {
			numRead = is.read(buffer);
			if (numRead > 0) {
				md.update(buffer, 0, numRead);
			}
		} while (numRead != -1);
		is.close();
		
		byte[] thedigest = md.digest();
		result = javax.xml.bind.DatatypeConverter.printHexBinary(thedigest);
		
		return result;
	}
}
