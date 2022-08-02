package com.diquest.util.msg;

import com.diquest.util.AESEncrypt;

public class Definition {

	private static final String MARINER_SCOPUS_IP = "192.168.137.1";
//	private static final String MARINER_SCOPUS_IP = "203.250.207.72";
	private static final int MARINER_SCOPUS_PORT = 5555;

//	private static final String MARINER_GPASS_IP = "192.168.137.1";
//	private static final String MARINER_GPASS_IP = "203.250.207.73";
//	private static final int MARINER_GPASS_PORT = 6666;
	private static final String MARINER_GPASS_IP = "203.250.207.71";
	private static final int MARINER_GPASS_PORT = 9888;
	
	/**
	 * 
	 * AES-128로 암호화된 SCOOPUS 검색서버 접속 정보
	 * */
	public static final String MARINER_CONNECTION_SCOPUS_INFO_ENCRYPT = AESEncrypt.convertConnectInfoEncrpty(MARINER_SCOPUS_IP, MARINER_SCOPUS_PORT);
	
	/**
	 * 
	 * AES-128로 암호화된 G-PASS 검색서버 접속 정보
	 * 
	 * */
	public static final String MARINER_CONNECTION_GPASS_INFO_ENCRYPT = AESEncrypt.convertConnectInfoEncrpty(MARINER_GPASS_IP, MARINER_GPASS_PORT);
}
