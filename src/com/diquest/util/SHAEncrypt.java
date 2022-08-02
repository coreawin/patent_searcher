/**
 * 
 */
package com.diquest.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 사용자 암호는 암호화 하여 저장한다. <br>
 * SHA1 알고리즘을 사용. <br>
 * 비밀번호는 최소 6자리 이상을 받아야 한다.<br>
 * 
 * @author neon
 * @date 2013. 9. 12.
 * @Version 1.0
 */
public class SHAEncrypt {

	/**
	 * 문자열을 암호화 한다.<br>
	 * 
	 * @author neon
	 * @date 2013. 9. 12.
	 * @param src
	 * @return
	 */
	public static String getEncrypt(String src) {
		return encrypt(src.getBytes());
	}

	private static String encrypt(byte[] input) {
		byte[] digest = null;
		digest = getHash(input);
		for (int i = 0; i < 10; i++) {
			digest = getHash(digest);
		}
		StringBuilder sb = new StringBuilder(digest.length);
		sb.append(toHexString(digest));
		return sb.toString();
	}

	/**
	 * <p>
	 * unsigned byte(바이트) 배열을 16진수 문자열로 바꾼다.
	 * </p>
	 * 
	 * @param bytes
	 *            unsigned byte's array
	 * @return
	 */
	public static String toHexString(byte[] bytes) {
		if (bytes == null) {
			return null;
		}

		StringBuffer result = new StringBuffer();
		for (byte b : bytes) {
			result.append(Integer.toString((b & 0xF0) >> 4, 16));
			result.append(Integer.toString(b & 0x0F, 16));
		}
		return result.toString();
	}

	/**
	 * <p>
	 * 입력한 데이터(바이트 배열)을 SHA1 알고리즘으로 처리하여 해쉬값을 도출한다.
	 * </p>
	 * 
	 * @param input
	 *            입력 데이터(<code>null</code>이면 안된다.)
	 * @return 해쉬값
	 */
	public static byte[] getHash(byte[] input) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			return md.digest(input);
		} catch (NoSuchAlgorithmException e) {
			// 일어날 경우가 없다고 보지만 만약을 위해 Exception 발생
			throw new RuntimeException("SHA1" + " Algorithm Not Found", e);
		}
	}

	/**
	 * 8자리 임시 비밀번호를 암호화 하여 발급한다.
	 * 
	 * @author neon
	 * @date 2013. 9. 12.
	 * @return
	 */
	public static String generatedTempPassword(String src) {
		String pwd = SHAEncrypt.getEncrypt(src);
		StringBuffer buf = new StringBuffer();
		buf.append(pwd.charAt(3));
		buf.append(pwd.charAt(6));
		buf.append(pwd.charAt(8));
		buf.append(pwd.charAt(11));
		buf.append(pwd.charAt(13));
		buf.append(pwd.charAt(15));
		buf.append(pwd.charAt(18));
		buf.append(pwd.charAt(22));
		System.out.println(11111);
		return buf.toString();
	}

	public static void main(String[] args) {
//		System.out.println(SHAEncrypt.getEncrypt("coreawin@diquest.com"));
//		System.out.println(SHAEncrypt.getEncrypt("coreawin@gmail.com"));
//		System.out.println(SHAEncrypt.getEncrypt("gpadmin@kisti.re.kr"));
		System.out.println(SHAEncrypt.getEncrypt("kisti"));
//		System.out.println(SHAEncrypt.getEncrypt("tester"));
//		System.out.println(SHAEncrypt.generatedTempPassword("tester@kisti.re.kr"));
	}
}
