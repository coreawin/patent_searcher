/**
 * 
 */
package com.diquest.util.xml;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * SAX 형태로 파싱한 특허 XML 데이터를 웹페이지에 뿌린다. 
 * @author neon
 * @date   2013. 6. 28.
 * @Version 1.0
 */
public class PrintPatentDataMaps {
	
	final String jsonMultiDelim = "&#13;&#10;";
	StringBuffer printDataBuffer = new StringBuffer();
	Set<String> tmpSet = new LinkedHashSet<String>();
	
	public String makeHref(String pno){
		String result = "<a href=\"./documentsView.jsp?pno="+pno+"\">";
		result += pno;
		result += "</a>";
		return result;
	}
	
	public String getMultiData(String datas){
		printDataBuffer.setLength(0);
		tmpSet.clear();
		String[] data = datas.split(jsonMultiDelim);
		for(String d : data){
			if("".equals(d.trim())) continue;
			tmpSet.add(d);
		}
		for(String d : tmpSet){
			printDataBuffer.append(d);
			printDataBuffer.append("<br class='br'>");
		}
		return printDataBuffer.toString();
	}

	
	public static String nullCheck(String s) {
		return s == null ? "" : s;
	
	}

}
