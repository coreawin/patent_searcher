package com.diquest.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

/**
 * 
 * 데이터를 AES-128 알고리즘을 활용하여 암호화 한다.
 * 
 * @author 이관재
 * @date 2015. 2. 11.
 * @version 1.0
 * @filename AESEncrypt.java
 */
public class AESEncrypt {
	private static final String USER_ID_CRYPT_KEY = "SCOPUS_JSON_PATH";

	/**
	 * 데이터를 AES-128 알고리즘을 활용하여 암호화를 담당한다.
	 * 
	 * @author 이관재
	 * @date 2015. 2. 11.
	 * @version 1.0
	 * @param str
	 *            암호화 대상 문자열
	 * @return
	 */
	public static String encrypt(String str) {
		SecretKeySpec spec = new SecretKeySpec(generateKey(USER_ID_CRYPT_KEY), "AES");
		try {
			Cipher ch = Cipher.getInstance("AES");
			ch.init(1, spec);
			byte encrypted[] = ch.doFinal(str.getBytes());
			return Base64.encodeBase64String(encrypted);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Base64.encodeBase64String(str.getBytes());
	}

	/**
	 * 데이터를 AES-128 알고리즘을 활용하여 복호화를 담당한다.
	 * 
	 * @author 이관재
	 * @date 2015. 2. 11.
	 * @version 1.0
	 * @param str
	 *            암호화된 문자열
	 * @return
	 */
	public static String decrypt(String str) {
		SecretKeySpec spec = new SecretKeySpec(generateKey(USER_ID_CRYPT_KEY), "AES");
		byte strBytes[] = str.getBytes();
		try {
			strBytes = Base64.decodeBase64(strBytes);
			Cipher ch = Cipher.getInstance("AES");
			ch.init(2, spec);
			byte encrypted[] = ch.doFinal(strBytes);
			return new String(encrypted);
		} catch (Exception e) {
			return new String(Base64.decodeBase64(str.getBytes()));
		}
	}

	public static String convertConnectInfoEncrpty(String ip, int port) {
		return encrypt(ip + "." + port);
	}

	public static String getConnectIPInfo(String keys) {
		String s = decrypt(keys);
		return s.substring(0, s.lastIndexOf("."));
	}

	public static int getConnectPortInfo(String keys) {
		String s = decrypt(keys);
		return Integer.parseInt(s.substring(s.lastIndexOf(".") + 1, s.length()));
	}

	public static void main(String args1[]) throws Exception {
	}

	public static byte[] generateKey(String key) {
		String hexString = Hex.encodeHexString(key.getBytes());
		return hexString.substring(0, 16).getBytes();
	}
}
