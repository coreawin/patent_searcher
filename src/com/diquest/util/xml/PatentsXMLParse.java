/**
 * 
 */
package com.diquest.util.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * 특허 데이터의 XML 을 SAX로 파싱한다.
 * 
 * @author neon
 * @date 2013. 6. 28.
 * @Version 1.0
 */
public class PatentsXMLParse {

	public PatentsXMLParse() {
	}

	public PatentDataMaps parse(String xml) throws Exception {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		xml = xml.replaceAll("\n", "");
		PatentHandlerMaps handler = new PatentHandlerMaps(true);
		SAXParser xr = spf.newSAXParser();
		InputStream input = new ByteArrayInputStream(xml.getBytes("UTF-8"));
		xr.parse(input, handler);
		return handler.getData();
	}

	public PatentDataMaps parse(File f) throws Exception {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		PatentHandlerMaps handler = new PatentHandlerMaps(true);
		SAXParser xr = spf.newSAXParser();
		xr.parse(f, handler);
		return handler.getData();
	}

}
