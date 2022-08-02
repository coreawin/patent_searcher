package com.diquest.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * 문자열 관련 유틸
 * 
 * @author neon
 * @date 2013. 4. 8.
 * @Version 1.0
 */
public class StringUtil {

	public static String refine(String s) {
		if (s == null)
			return " ";
		s = s.trim();
		if ("null".equals(s)) {
			return "";
		}
		return s.replaceAll("[‘’]", "'").replaceAll("\n", " ").trim();
	}

	/**
	 * null이면 빈 문자열을 반환한다.
	 * 
	 * @param s
	 * @return
	 */
	public static String nullCheck(String s) {
		return nullCheck(s, " ");
	}

	public static String nullCheck(String s, String defaultValue) {
		return s == null ? defaultValue : refine(s);
	}
	
	public static String nullCheck(Object s, String defaultValue) {
		return s == null ? defaultValue : refine(s.toString());
	}

	public static String refine(String s, String defaultValue) {
		if (s == null)
			return defaultValue;
		s = s.trim();
		if ("null".equals(s)) {
			return " ";
		}
		return s;
	}

	public static List<String> splitRegex(String s, String regex) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(s);

		List<String> splitResult = new ArrayList<String>();
		String r = "";
		while (m.find()) {
			r = m.group();
			if (r != null) {
				splitResult.add(r.trim());
			}
		}

		return splitResult;
	}
}
