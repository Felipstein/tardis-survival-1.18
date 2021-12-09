package br.felipstein.tardis.utils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public enum Cryptography {

	SHA_256("SHA-256", 64), SHA_512("SHA-512", 128);
	
	private String string;
	private int id;
	
	private Cryptography(String string, int id) {
		this.string = string;
		this.id = id;
	}
	
	public String getString() {
		return string;
	}
	
	public int getId() {
		return id;
	}
	
	public String encrypt(String value) {
		String hash = hex(getSHA(value));
		while(hash.length() < id) {
			hash = "0" + hash;
		}
		return hash;
	}
	
	private byte[] getSHA(String input) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance(string);
		} catch(NoSuchAlgorithmException e) {
			throw new AssertionError("No Provider supports a MessageDigestSpi implementation for the specified algorithm.");
		}
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    } 
	
    private String hex(byte[] hash) { 
    	BigInteger number = new BigInteger(1, hash);
    	StringBuffer hexString = new StringBuffer(number.toString(16));
    	while(hexString.length() < 32) {
    		hexString.insert(0, '0');
    	}
        return hexString.toString();
    }
}