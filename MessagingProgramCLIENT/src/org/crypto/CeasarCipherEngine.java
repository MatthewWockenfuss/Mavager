package org.crypto;

import org.toast.Handler;

public class CeasarCipherEngine {
	
	@SuppressWarnings("unused")
	private Handler handler;
	private int key;
	
	public CeasarCipherEngine(int key, Handler handler) {
		this.handler = handler;
		this.key = key;
	}
	
	
	
	public String encrypt(String plainText) {
		char[] plainTextArray = plainText.toCharArray();
		int[] dec = new int[plainTextArray.length];
		char[] cipherChar = new char[dec.length];
		
		String build = "";
		
		for(int i = 0;i < plainTextArray.length;i++) {
			dec[i] = (int)plainTextArray[i];
			dec[i] += key;
			cipherChar[i] = (char)dec[i];
			build += (cipherChar[i] + "");
		}
		return build; 
	}
	
	public String decrypt(String cipherText) {
		char[] cipherTextArray = cipherText.toCharArray();
		int[] dec = new int[cipherTextArray.length];
		char[] plainChar = new char[dec.length];
		
		String build = "";
		
		for(int i = 0;i < cipherTextArray.length;i++) {
			dec[i] = (int)cipherTextArray[i];
			dec[i] -= key;
			plainChar[i] = (char)dec[i];
			build += (plainChar[i] + "");
		}
		return build; 
	}
	
	
	
	
	
	
	
	
	
	
}
