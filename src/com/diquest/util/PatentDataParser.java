package com.diquest.util;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.diquest.util.xml.PatentDataMaps;
import com.diquest.util.xml.PatentHandler;
import com.diquest.util.xml.PatentSchema.EXMLSchema;

/**
 * 특허 데이터 파서기.
 * 
 * @author neon
 * @date 2013. 6. 20.
 * @Version 1.0
 */
public class PatentDataParser {

	static final String regex = "(?<=<)([0-9]{0,}.)(?=>)";
	static final Pattern p = Pattern.compile(regex);

	/**
	 * 데이터의 우선순위 정보를 얻는다.<br>
	 * 
	 * @param src
	 * @return
	 */
	public static int getRank(String src) {
		if (src == null) {
			src = "1";
		}
		Matcher m = p.matcher(src);
		while (m.find()) {
			String find = m.group();
			if(find.endsWith(">")){
				find = find.substring(0, (find.length() -1));
			}
			return Integer.parseInt(find);
		}
		return 0;
	}

	static final String regexRemoveTag = "<[0-9]{1,}>";

	public static String removeTag(String src) {
		return src.replaceAll(regexRemoveTag, "");
	}

	/**
	 * SAX로 파싱한 XML 데이터의 쌍을 찾아준다. <br>
	 * 
	 * @author neon
	 * @date 2013. 6. 20.
	 * @param dataMaps
	 * @param ses
	 * @return
	 */
	public static Map<Integer, Map<EXMLSchema, String>> getMultiValueDatas(PatentDataMaps dataMaps, EXMLSchema[] ses) {
		Map<Integer, Map<EXMLSchema, String>> result = new TreeMap<Integer, Map<EXMLSchema, String>>();
		for (EXMLSchema se : ses) {
			String data = dataMaps.getDatas(se);
			String[] datas = data.split(String.valueOf(PatentHandler.MULTI_VALUE_DELIM));
			for (String d : datas) {
				if ("".equals(d.trim()))
					continue;

				int rank = getRank(d);
				Map<EXMLSchema, String> value = null;
				value = result.get(rank);
				if (value == null) {
					value = new HashMap<EXMLSchema, String>();
				}
				value.put(se, removeTag(d));
				result.put(rank, value);
			}
		}
		return result;
	}
}
