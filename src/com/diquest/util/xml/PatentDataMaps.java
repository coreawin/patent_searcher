package com.diquest.util.xml;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.diquest.util.xml.PatentSchema.EXMLSchema;

/**
 * The <b>priority number</b> is made up of: <br>
 * • A country code (2 letters)<br>
 * • The year of filing (4 digits)<br>
 * • A serial number (variable, maximum 7 characters)<br>
 * Priority number examples:<br>
 * • GB19958026 or GB19950008026<br>
 * • FR19960006814<br>
 * • US19990260426<br>
 * • DE19961025214<br>
 * 
 * @author neon
 * @date 2013. 6. 11.
 * @Version 1.0
 */
public class PatentDataMaps {

	Logger logger = LoggerFactory.getLogger(getClass());

	// private static PatentDataMaps instance = new PatentDataMaps();

	// public static PatentDataMaps getInstance() {
	// return instance;
	// }

	public static final String REX1 = "\\s{1,}";

	public Map<EXMLSchema, String> dataMap = new LinkedHashMap<EXMLSchema, String>();

	public Map<EXMLSchema, Map<Integer, Map<String, Map<EXMLSchema, String>>>> dataMultiMap = new HashMap<EXMLSchema, Map<Integer, Map<String, Map<EXMLSchema, String>>>>();

	/**
	 * XML 스키마 이외의 데이터가 존재하는 경우.
	 */
	public Map<String, String> additionDataMap = new LinkedHashMap<String, String>();

	/**
	 * 단일 데이터를 가진 스키마에 적용.
	 * 
	 * @author neon
	 * @date 2013. 6. 11.
	 * @param se
	 *            특허 XML 스키마 명
	 * @param data
	 *            스키마 데이터.
	 */
	public void setDatas(EXMLSchema se, String data) {
		if (se == null)
			return;
		if (data == null)
			data = "";
		dataMap.put(se, data.replaceAll(REX1, " "));
	}

	/**
	 * 한 스키마에 구분된 여러 데이터를 가진 스키마에 적용
	 * 
	 * @author neon
	 * @date 2013. 6. 11.
	 * @param se
	 *            특허 XML 스키마 명
	 * @param data
	 *            스키마 데이터.
	 */
	public void setMultiDatas(EXMLSchema se, String data) {
		setMultiDatas(se, data, String.valueOf(PatentHandler.MULTI_VALUE_DELIM));
	}
	
	public void setMultiDatas(EXMLSchema se, String data, final String delim) {
		if (se == null)
			return;
		if (data == null)
			data = "";
		String v = dataMap.get(se);
		if (v == null) {
			v = data;
		} else {
			v += delim + data;
		}
		dataMap.put(se, v.replaceAll(REX1, " "));
	}

	/**
	 * 해당 스키마의 데이터를 얻는다.
	 * 
	 * @author neon
	 * @date 2013. 6. 11.
	 * @param se
	 *            특허 xml 스키마
	 * @return
	 */
	public String getDatas(EXMLSchema se) {
		String v = dataMap.get(se);
		return v == null ? "" : v;
	}

	/**
	 * 해당 스키마의 데이터를 얻는다.
	 * 
	 * @author neon
	 * @date 2013. 6. 11.
	 * @param se
	 *            특허 xml 스키마
	 * @return
	 */
	public int getDatasInt(EXMLSchema se) {
		String v = dataMap.get(se);
		return v == null ? 0 : Integer.parseInt(v);
	}

	public void clear() {
		dataMap.clear();
		additionDataMap.clear();
	}

	Set<String> tmpSet = new HashSet<String>();
	StringBuffer tmpBuffer = new StringBuffer();

	/**
	 * SAX로 파싱한 후 각 데이터 별 사용자가 확인 하기 쉽게 하기 위한 데이터 후처리<br>
	 * 데이터의 쌍을 맞춘다.
	 * 
	 * @author neon
	 * @date 2013. 6. 12.
	 */
	public void makePostWorking() {
		dataMap.put(
				EXMLSchema.pno,
				PatentDataFormat.convertPublicationNumber(getDatas(EXMLSchema.pncn), getDatas(EXMLSchema.pndno),
						getDatas(EXMLSchema.pnkind)).replaceAll(REX1, " "));
		dataMap.put(EXMLSchema.appno,
				PatentDataFormat.convertApplicationNumber(getDatas(EXMLSchema.appcn), getDatas(EXMLSchema.appdno))
						.replaceAll(REX1, " "));

		dataMap.put(
				EXMLSchema.prino,
				PatentDataFormat.makeMultiPublicationNumber(getDatas(EXMLSchema.prino), getDatas(EXMLSchema.pricn),
						getDatas(EXMLSchema.pridno), getDatas(EXMLSchema.prikind), false).replaceAll(REX1, " "));

		dataMap.put(
				EXMLSchema.citno,
				PatentDataFormat.makeMultiPublicationNumber(getDatas(EXMLSchema.citno), getDatas(EXMLSchema.citcn),
						getDatas(EXMLSchema.citdn), getDatas(EXMLSchema.citkind), true).replaceAll(REX1, " "));

		dataMap.put(
				EXMLSchema.refno,
				PatentDataFormat.makeMultiPublicationNumber(getDatas(EXMLSchema.refno), getDatas(EXMLSchema.refcn),
						getDatas(EXMLSchema.refdn), getDatas(EXMLSchema.refkind), true).replaceAll(REX1, " "));

		String citno = dataMap.get(EXMLSchema.citno);
		int citcnt = 0;
		if (citno != null) {
			tmpSet.clear();
			String[] r = citno.split(String.valueOf(PatentHandler.MULTI_VALUE_DELIM));
			for (String s : r) {
				s = s.trim();
				if ("".equals(s))
					continue;
				tmpSet.add(s);
			}
			citcnt = tmpSet.size();
		}
		dataMap.put(EXMLSchema.numcit, String.valueOf(citcnt));

		String refno = dataMap.get(EXMLSchema.refno);
		int refcnt = 0;
		if (refno != null) {
			tmpSet.clear();
			String[] r = refno.split(String.valueOf(PatentHandler.MULTI_VALUE_DELIM));
			for (String s : r) {
				s = s.trim();
				if ("".equals(s))
					continue;
				tmpSet.add(s);
			}
			refcnt = tmpSet.size();
		}
		dataMap.put(EXMLSchema.numref, String.valueOf(refcnt));

		makePostWorkingInventor();
		makePostWorkingAssignee();
		makePostWorking(EXMLSchema.ipc);
		makePostWorking(EXMLSchema.ipcr);
		makePostWorking(EXMLSchema.ecla);
	}

	public void makePostWorkingMultiMapValue() {
		Set<EXMLSchema> keysets = dataMultiMap.keySet();
//		logger.debug("dataMultiMap {}", dataMultiMap);
		for (EXMLSchema e : keysets) {
			Map<Integer, Map<String, Map<EXMLSchema, String>>> seqMap = dataMultiMap.get(e);
			Set<Integer> seqMaps = seqMap.keySet();
			for (Integer i : seqMaps) {
				fieldCopy(seqMap.get(i));
			}
//			logger.debug("{} multi data {}", e, seqMap);
		}
	}

	Map<String, Map<EXMLSchema, String>> cloneLangMap = new HashMap<String, Map<EXMLSchema, String>>();
	private void fieldCopy(Map<String, Map<EXMLSchema, String>> langMap) {
		cloneLangMap.clear();
		cloneLangMap.putAll(langMap);
		
		Set<EXMLSchema> schemaSet = new HashSet<PatentSchema.EXMLSchema>();
		Set<Entry<String, Map<EXMLSchema, String>>> entrySet = langMap.entrySet();
		for (Entry<String, Map<EXMLSchema, String>> e : entrySet) {
			schemaSet.addAll(e.getValue().keySet());
		}

		for (Entry<String, Map<EXMLSchema, String>> entryLang : entrySet) {
			Map<EXMLSchema, String> dataMap = entryLang.getValue();
			for (EXMLSchema schema : schemaSet) {
				if (!dataMap.containsKey(schema)) {
					String find = findData(cloneLangMap, schema);
					if (find != null) {
						// number40 2018-09-18 표준화된 기관명은 제외 
						if(!schema.equals(EXMLSchema.app_standardized) && !schema.equals(EXMLSchema.app_standardized_type) && !schema.equals(EXMLSchema.app_normalized) && !schema.equals(EXMLSchema.app_normalized_key)
								  && !schema.equals(EXMLSchema.related_doc) && !schema.equals(EXMLSchema.related_doc_parent)  && !schema.equals(EXMLSchema.related_doc_child)  && !schema.equals(EXMLSchema.related_doc_id)   && !schema.equals(EXMLSchema.related_doc_grant)   && !schema.equals(EXMLSchema.related_doc_pct)   && !schema.equals(EXMLSchema.related_doc_status)) {
							dataMap.put(schema, find);
						}
					}
				}
			}
		}
		
	}

	private String findData(Map<String, Map<EXMLSchema, String>> cloneLangMap, EXMLSchema findSchema) {
		String result = null;
		Set<Entry<String, Map<EXMLSchema, String>>> entrySet = cloneLangMap.entrySet();
		for (Entry<String, Map<EXMLSchema, String>> e : entrySet) {
			Map<EXMLSchema, String> dataMap = e.getValue();
			result = dataMap.get(findSchema);
			if (result != null) {
				return result;
			}
		}
		return result;
	}

	private void makePostWorking(EXMLSchema se) {
		String data = dataMap.get(se);
		if (data == null)
			return;
		tmpSet.clear();
		tmpBuffer.setLength(0);
		String[] d = data.split(String.valueOf(PatentHandler.MULTI_VALUE_DELIM));
		for (String v : d) {
			// try {
			// v = v.substring(0, 15);
			// } catch (Exception e) {
			// // ignore
			// }
			tmpSet.add(v.trim().replaceAll(" ", ""));
		}

		for (String t : tmpSet) {
			tmpBuffer.append(t);
			tmpBuffer.append(PatentHandler.MULTI_VALUE_DELIM);
		}
		dataMap.put(se, tmpBuffer.toString().replaceAll(REX1, " "));
	}

	private final String nameRegex = "[.,;]";
	static final String REGX3 = "[\\s]{1,}";

	private void makePostWorkingAssignee() {
		tmpSet.clear();
		tmpBuffer.setLength(0);
		String assignee = dataMap.get(EXMLSchema.assignee);

		if (assignee != null) {
			String[] d = assignee.split(String.valueOf(PatentHandler.MULTI_VALUE_DELIM));
			for (String v : d) {
				tmpSet.add(v.trim());
				// tmpSet.add(v.replaceAll(nameRegex, " ").replaceAll(REGX3,
				// " ").trim());
			}
		}
		for (String t : tmpSet) {
			tmpBuffer.append(t);
			tmpBuffer.append(PatentHandler.MULTI_VALUE_DELIM);
		}
//		logger.debug("assignee data {}", tmpBuffer.toString().replaceAll(REX1, " "));
		dataMap.put(EXMLSchema.assignee, tmpBuffer.toString().replaceAll(REX1, " "));
	}

	private void makePostWorkingInventor() {
		tmpSet.clear();
		tmpBuffer.setLength(0);
		String inventor = dataMap.get(EXMLSchema.inventor);
		if (inventor == null) {
			String invfn = dataMap.get(EXMLSchema.invfn);
			String invln = dataMap.get(EXMLSchema.invln);
			String[] invfns = null;
			if (invfn != null) {
				invfns = invfn.split(String.valueOf(PatentHandler.MULTI_VALUE_DELIM));
			}
			String[] invlns = null;
			if (invln != null) {
				invlns = invln.split(String.valueOf(PatentHandler.MULTI_VALUE_DELIM));
			}
			if (invfns != null && invlns != null) {
				for (int idx = 0; idx < invfns.length; idx++) {
					try {
						inventor = invfns[idx] + " " + invlns[idx];
						tmpSet.add(inventor);
					} catch (Exception e) {
					}
				}
			}
		} else {
			String[] d = inventor.split(String.valueOf(PatentHandler.MULTI_VALUE_DELIM));
			for (String v : d) {
				tmpSet.add(v.replaceAll(nameRegex, "").trim());
			}
		}
		for (String t : tmpSet) {
			tmpBuffer.append(t);
			tmpBuffer.append(PatentHandler.MULTI_VALUE_DELIM);
		}
		dataMap.put(EXMLSchema.inventor, tmpBuffer.toString().replaceAll(REX1, " "));
	}

	StringBuffer toString = new StringBuffer();

	public String toString() {
		toString.setLength(0);
		for (EXMLSchema se : EXMLSchema.values()) {
			toString.append("\n [" + se.name() + "] " + getDatas(se));
		}
		return toString.toString();
	}
}
