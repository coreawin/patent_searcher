package com.diquest.bean;

import java.util.HashMap;
import java.util.Map;

import com.diquest.util.EDQDocField;

/**
 * 
 * 
 * Export할 특허 데이터를 저장하기 위한 Bean
 * 
 * @author 이관재
 * @date 2015. 8. 13.
 * @version 1.0
 * @filename ExportPatentData.java
 */
public class ExportPatentData {
	Map<String, String> resultData = new HashMap<String, String>();

	public void setPnDate(String pndate) {
		resultData.put(EDQDocField.pndate.getUpperCase(), pndate);
	}

	public void setXML(String xml) {
		resultData.put(EDQDocField.xml.getUpperCase(), xml);
	}

	public void setAppdate(String apdate) {
		resultData.put(EDQDocField.apdate.getUpperCase(), apdate);
	}

	public void setPno(String pno) {
		resultData.put(EDQDocField.pno.getUpperCase(), pno);
	}

	public String getPnDate() {
		return resultData.get(EDQDocField.pndate.getUpperCase()) == null ? "" : resultData.get(EDQDocField.pndate
				.getUpperCase());
	}

	public String getXML() {
		return resultData.get(EDQDocField.xml.getUpperCase()) == null ? "" : resultData.get(EDQDocField.xml.getUpperCase());
	}

	public String getAppdate() {
		return resultData.get(EDQDocField.apdate.getUpperCase()) == null ? "" : resultData.get(EDQDocField.apdate
				.getUpperCase());
	}

	public String getPno() {
		return resultData.get(EDQDocField.pno.getUpperCase()) == null ? "" : resultData.get(EDQDocField.pno.getUpperCase());
	}

	/**
	 * 
	 * XML의 파일 이름을 리턴합니다.
	 *
	 * @author 이관재
	 * @date 2015. 8. 13.
	 * @version 1.0
	 * @return
	 */
	public String getFileName() {
		String pno = getPno();
		String pnDate = getPnDate();
		String apDate = getAppdate();
		return pno + "." + pnDate + "." + apDate + ".xml";
	}
}
