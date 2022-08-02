package com.diquest.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.diquest.util.xml.PatentDataFormat;
import com.diquest.util.xml.PatentDataMaps;
import com.diquest.util.xml.PatentHandler;
import com.diquest.util.xml.PatentSchema.EXMLSchema;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * SAX로 파싱된 특허 데이터에서 이용가능한 형태의 데이터로 정제한다.<br>
 * 
 * @author neon
 * @date 2013. 6. 19.
 * @Version 1.0
 */
public class PatentsDataRefiner {

	public static final String INVENTOR_DELIM = "##";
//	public static final char INVENTOR_DELIM = '`';

	public static LinkedHashSet<String> multiValue(String data) {
		LinkedHashSet<String> datas = new LinkedHashSet<String>();
		String[] results = data.split(String.valueOf(PatentHandler.MULTI_VALUE_DELIM));
		for (String r : results) {
			if ("".equals(r.trim())) {
				continue;
			}
			datas.add(r);
		}
		return datas;
	}

	/**
	 * 발명인 정보의 정제
	 * 
	 * @author neon
	 * @date 2013. 6. 19.
	 * @param dataMaps
	 * @return
	 */
	public static Set<String> getInventorInfo(PatentDataMaps dataMaps) {
		Set<String> r = new LinkedHashSet<String>();
		EXMLSchema[] ses = new EXMLSchema[] { EXMLSchema.invseq, EXMLSchema.inventor_lang, EXMLSchema.inventor, EXMLSchema.invcn };
		Map<Integer, Map<EXMLSchema, String>> result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		Map<String, String> cnInfoMap = getNationalityResidenceInfo(dataMaps, new EXMLSchema[] { EXMLSchema.invcnn, EXMLSchema.invcnr });
		// for (int rank : result.keySet()) {
		// Map<EXMLSchema, String> datas = result.get(rank);
		// String lang = nullCheck(datas.get(EXMLSchema.inventor_lang));
		// String invseq = nullCheck(datas.get(EXMLSchema.invseq));
		// String inventor =
		// nullCheck(datas.get(EXMLSchema.inventor)).replaceAll(";",
		// " ").replaceAll(
		// QueryConverter.REGX3, " ");
		//
		// /*
		// * 국적정보 채우기 우선순위 1)nationality 2) address-country 3)residence
		// */
		// String invcn = nullCheck(cnInfoMap.get("N" + invseq));
		// if ("".equals(invcn)) {
		// invcn = nullCheck(datas.get(EXMLSchema.invcn));
		// if ("".equals(invcn)) {
		// invcn = nullCheck(cnInfoMap.get("R" + invseq));
		// }
		// }
		// if (!"".equals(inventor)) {
		// r.add(lang + INVENTOR_DELIM + invcn + INVENTOR_DELIM + inventor);
		// System.out.println("==1> " + lang + INVENTOR_DELIM + invcn +
		// INVENTOR_DELIM + inventor);
		// }
		// }

		ses = new EXMLSchema[] { EXMLSchema.inventor_lang, EXMLSchema.invfn, EXMLSchema.invln, EXMLSchema.invcn, EXMLSchema.invcity, EXMLSchema.inventor };
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String lang = nullCheck(datas.get(EXMLSchema.inventor_lang));
			String invseq = nullCheck(datas.get(EXMLSchema.invseq));

			/*
			 * 국적정보 채우기 우선순위 1)nationality 2) address-country 3)residence
			 */
			String invcn = nullCheck(cnInfoMap.get("N" + invseq));
			if ("".equals(invcn)) {
				invcn = nullCheck(datas.get(EXMLSchema.invcn));
				if ("".equals(invcn)) {
					invcn = nullCheck(cnInfoMap.get("R" + invseq));
				}
			}
			String invfn = nullCheck(datas.get(EXMLSchema.invfn));
			String invln = nullCheck(datas.get(EXMLSchema.invln));
			String invcity = nullCheck(datas.get(EXMLSchema.invcity));
			String inventor = nullCheck(datas.get(EXMLSchema.inventor));
			if (!"".equals(invfn) && !"".equals(invln)) {
				r.add(lang + INVENTOR_DELIM + invcn + INVENTOR_DELIM + removeChracter(invln + " " + invfn).toUpperCase());
				// System.out.println("==2> " + lang + INVENTOR_DELIM + invcn +
				// INVENTOR_DELIM + invln + " " + invfn +":" + inventor);
			} else {
				r.add(lang + INVENTOR_DELIM + invcn + INVENTOR_DELIM + removeChracter(inventor).toUpperCase());
				// System.out.println("==1> " + lang + INVENTOR_DELIM + invcn +
				// INVENTOR_DELIM + removeChracter(inventor).toUpperCase());
			}
		}
		return r;
	}

	/**
	 * 클레임 정보
	 * 
	 * @author coreawin
	 * @date 2014. 6. 5.
	 * 
	 * @param dataMaps
	 * @return Map&lt;claims_lang, Map&lt;claims|claims_id|claims_lang,
	 *         string&gt;
	 */
	public static Map<String, Map<String, String>> getClaimInfo(PatentDataMaps dataMaps) {
		EXMLSchema[] ses = new EXMLSchema[] { EXMLSchema.claims_lang, EXMLSchema.claims_format, EXMLSchema.claims_id, EXMLSchema.claims };
		Map<Integer, Map<EXMLSchema, String>> multiValues = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		Set<Integer> set = multiValues.keySet();
		Map<String, Map<String, String>> r = new HashMap<String, Map<String, String>>();
		for (Integer no : set) {
			Map<EXMLSchema, String> values = multiValues.get(no);
			Set<EXMLSchema> shemaKeys = values.keySet();
			Map<String, String> data = new HashMap<String, String>();
			for (EXMLSchema schema : shemaKeys) {
				data.put(schema.name(), values.get(schema));
			}
			r.put(values.get(EXMLSchema.claims_lang).toLowerCase(), data);
		}
		return r;
	}

	public static Map<String, String> getClaimInfo(String selectedLang, PatentDataMaps dataMaps) {
		selectedLang = selectedLang.toLowerCase();
		Map<String, Map<String, String>> result = getClaimInfo(dataMaps);

		if (result.containsKey(selectedLang)) {
			return result.get(selectedLang);
		}
		Set<String> s = result.keySet();
		for (String ss : s) {
			return result.get(ss);
		}
		return Collections.emptyMap();
	}

	public static String[] getClaimsData(String claims) {
		if (claims == null)
			return new String[] {};
		claims = claims.trim();
		String[] src = claims.split("(\\{clm-\\d{5,}-[a-zA-Z]{1,3}\\,\\d{1,}\\})");
		String[] dest = new String[src.length - 1];
		System.arraycopy(src, 1, dest, 0, dest.length);
		return dest;
	}

	public static Set<String> getIndependendClaimsData(String independent_claims) {
		Set<String> set = new HashSet<String>();
		if (independent_claims == null)
			return set;
		
		independent_claims = independent_claims.trim();
		if (independent_claims.length() > 0) {
			String[] ics = independent_claims.split(String.valueOf(PatentHandler.MULTI_VALUE_DELIM));
			if (ics == null)
				return set;
			for (String i : ics) {
				set.add(i);
			}
		}
		return set;
	}
	
	public static Set<String> getIndependendClaimsData(String indepedent_claims_index, String[] claims) {
		Set<String> set = new HashSet<String>();
		if (claims == null)
			return set;
		
		Set<Integer> indexSet = new HashSet<Integer>();
		String[] indexes = indepedent_claims_index.split(String.valueOf(PatentHandler.MULTI_VALUE_DELIM));
		for(String idx : indexes){
			try{
			indexSet.add(Integer.parseInt(idx.trim()));
			}catch(Exception e){
//				logger.error(e.getMessage(), idx);
			}
		}
		for(int idx = 0; idx < claims.length ; idx++){
			if(indexSet.contains(idx)){
				set.add(claims[idx].replaceAll("<claim-text>|</claim-text>", ""));
			}
		}
		return set;
	}

	public static final String CLAIM_PATTERN = "((?=\\{clm-\\d{5,}-[a-zA-Z]{1,3}\\,\\d{1,}\\}))";

	/**
	 * 두개 이상의 빈 문자열을 찾는 정규식
	 */
	public static final String FIND_BLANK_TWO = "[\\s]{1,}";
	public static String SPECIAL_CHARACTER = "[\\\\.,&-@#$%\\\\^&\\\\*\\!]";

	public static String removeChracter(String src) {
		if (src == null)
			return "";
		return src.replaceAll(SPECIAL_CHARACTER, " ").replaceAll(FIND_BLANK_TWO, " ").trim();
	}

	/**
	 * 발명자 혹은 출원자의 국정정보 (Nationality와 Residence) 정보를 구한다. <br>
	 * 
	 * @author neon
	 * @date 2013. 7. 15.
	 * @param dataMaps
	 * @return key : nationality N + sequence value : nationality <br>
	 *         key : residence N + sequence value : residence <br>
	 */
	private static Map<String, String> getNationalityResidenceInfo(PatentDataMaps dataMaps, EXMLSchema[] ses) {
		Map<String, String> r = new HashMap<String, String>();
		Map<Integer, Map<EXMLSchema, String>> result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String cnn = nullCheck(datas.get(ses[0]));
			String cnr = nullCheck(datas.get(ses[1]));
			String[] ns = cnn.split("_");
			String[] rs = cnr.split("_");
			if (!"".equals(cnn)) {
				/*
				 * key : nationality N + sequence value : nationality
				 */
				if (ns.length > 1) {
					r.put("N" + ns[0], ns[1]);
				} else {
					r.put("N" + ns[0], "");
				}

			}
			if (!"".equals(cnr)) {
				/*
				 * key : residence N + sequence value : residence
				 */
				if (rs.length > 1) {
					r.put("R" + rs[0], rs[1]);
				} else {
					r.put("R" + rs[0], "");
				}
			}
		}
		return r;
	}

	/**
	 * 언어별로 작성된 발명자를 구한다. <br>
	 * 
	 * @author neon
	 * @date 2013. 7. 10.
	 * @param dataMaps
	 * @return
	 */
	@Deprecated
	public static Map<String, Set<String>> getInventorInfoMap(PatentDataMaps dataMaps) {
		return setupLangDatas(getInventorInfo(dataMaps));
	}

	/**
	 * 언어별로 작성된 출원일을 구한다. <br>
	 * 
	 * @author neon
	 * @date 2013. 7. 10.
	 * @param dataMaps
	 * @return
	 */
	@Deprecated
	public static Map<String, Set<String>> getAssigneeInfoMap(PatentDataMaps dataMaps) {
		return setupLangDatas(getAssigneesInfo(dataMaps));

		// return setupLangDatas(getAssigneesInfo(dataMaps)); /* {kor=[US`허어세이
		// 후드스 코포에이숀 배리 엘 조우아스], eng=[`허어세이 후드스 코포에이숀 배리 엘 조우아스]} */
	}

	static Logger logger = LoggerFactory.getLogger("PatentsDataRefiner.java");

	public static Map<String, Set<String>> getAssigneeInventorMultiMapInfo(PatentDataMaps dataMaps, EXMLSchema dataType, EXMLSchema nameField, EXMLSchema countryField) {
		Map<Integer, Map<String, Map<EXMLSchema, String>>> seqMap = dataMaps.dataMultiMap.get(dataType);
		Map<String, Set<String>> resultMap = new HashMap<String, Set<String>>();
		Set<String> resultData = new LinkedHashSet<String>();
		if (seqMap == null)
			return resultMap;

		Set<Integer> seqMaps = seqMap.keySet();
		for (Integer i : seqMaps) {
			Map<String, Map<EXMLSchema, String>> langMap = seqMap.get(i);
			Set<String> langSet = langMap.keySet();
			for (String lang : langSet) {
				Map<EXMLSchema, String> fieldMap = langMap.get(lang);
				// logger.debug("lang {} / {}", lang, fieldMap);
				if (resultMap.containsKey(lang)) {
					resultData = resultMap.get(lang);
				} else {
					resultData = new LinkedHashSet<String>();
				}
				if (fieldMap != null) {
					String name = StringUtil.nullCheck(fieldMap.get(nameField));
					if ("".equals(name)) {
						switch (dataType) {
						case inventor:
							name = StringUtil.nullCheck(fieldMap.get(EXMLSchema.invln)) + StringUtil.nullCheck(fieldMap.get(EXMLSchema.invfn));
							break;
						case assignee:
							name = StringUtil.nullCheck(fieldMap.get(EXMLSchema.assln)) + StringUtil.nullCheck(fieldMap.get(EXMLSchema.assfn));
							break;
						}
					}
					String country = StringUtil.nullCheck(fieldMap.get(countryField));
					if ("".equals(country)) {
						String state = "";
						String city = "";
						String findCn = "";
						switch (dataType) {
						case inventor:
							city = StringUtil.nullCheck(fieldMap.get(EXMLSchema.invcity));
							state = StringUtil.nullCheck(fieldMap.get(EXMLSchema.invstate));
							country = findCountryCodeInfo(state, city);
							if ("".equals(country)) {
								country = StringUtil.nullCheck(fieldMap.get(EXMLSchema.invcity)) + ("".equals(state) ? "" : " in " + state);
							}
							break;
						case assignee:
							city = StringUtil.nullCheck(fieldMap.get(EXMLSchema.asscity));
							state = StringUtil.nullCheck(fieldMap.get(EXMLSchema.assstate));
							country = findCountryCodeInfo(state, city);
							if ("".equals(country)) {
								country = StringUtil.nullCheck(fieldMap.get(EXMLSchema.asscity)) + ("".equals(state) ? "" : " in " + state);
							}
							break;
						}
					}
					resultData.add(country + INVENTOR_DELIM + name);
				}
				langMap.get(lang).get(nameField);
				resultMap.put(lang, resultData);
			}
		}
		// logger.debug("result {}", resultMap);
		// System.out.println(resultMap);
		return resultMap;
		// return setupLangDatas(getAssigneesInfo(dataMaps)); /* {kor=[US`허어세이
		// 후드스 코포에이숀 배리 엘 조우아스], eng=[`허어세이 후드스 코포에이숀 배리 엘 조우아스]} */
	}

	/**
	 * 주 정보와 도시 이름정보를 통해 국가정보를 찾는다.<r> 현재는 US에 대한 정보만 구축.
	 * 
	 * @author coreawin
	 * @date 2014. 7. 23.
	 * @param state
	 *            주 이름 (대문자)
	 * @param city
	 *            도시 이름. (대문자)
	 * @return
	 */
	private static String findCountryCodeInfo(String state, String city) {
		// Map<String, Set<String>> usInfo =
		// DescriptionData.getUsStateCityInfo();
		String result = "";
		// if (usInfo == null)
		// return result;
		// if (usInfo.containsKey(state.toUpperCase())) {
		// if (usInfo.get(state.toUpperCase()).contains(city.toUpperCase())) {
		// result = "US";
		// }
		// }
		return result;
	}

	/**
	 * 언어별로 데이터를 설정한다.
	 * 
	 * @author neon
	 * @date 2013. 7. 10.
	 * @param dataMaps
	 * @param resultMaps
	 */
	private static Map<String, Set<String>> setupLangDatas(Set<String> datas) {
		Map<String, Set<String>> result = new LinkedHashMap<String, Set<String>>();
		for (String s : datas) {
			int firstDelimIndex = s.indexOf(INVENTOR_DELIM);
			String lang = s.substring(0, firstDelimIndex);
			String d = s.substring(firstDelimIndex + 1, s.length());
			Set<String> rs = result.get(lang);
			if (rs == null) {
				rs = new LinkedHashSet<String>();
			}
			rs.add(d);
			result.put(lang, rs);
		}
		return result;
	}

	/**
	 * 언어에 따른 정보 형태로 가공.
	 * 
	 * @author neon
	 * @date 2013. 6. 20.
	 * @param dataMaps
	 * @param lang
	 * @param langField
	 * @param select
	 * @return
	 */
	public static String getLangTextInfo(PatentDataMaps dataMaps, String lang, EXMLSchema langField, EXMLSchema select) {
		EXMLSchema[] ses = new EXMLSchema[] { langField, select };
		Map<Integer, Map<EXMLSchema, String>> result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		StringBuffer buf = new StringBuffer();
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String lf = datas.get(langField);
			if (lf == null)
				continue;
			if (lf.equalsIgnoreCase(lang)) {
				buf.append(datas.get(select));
				buf.append(INVENTOR_DELIM);
			}
		}
		if (buf.length() > 0) {
			buf.deleteCharAt(buf.length() - 1);
		}
		return buf.toString();
	}

	/**
	 * 언어에 따른 정보 형태로 가공.
	 * 
	 * @author neon
	 * @date 2013. 6. 19.
	 * @param dataMaps
	 * @param ses
	 * @param langField
	 * @param select
	 * @return
	 */
	public static Map<String, String> getLangTextInfo(PatentDataMaps dataMaps, EXMLSchema[] ses, EXMLSchema langField, EXMLSchema select) {
		Map<Integer, Map<EXMLSchema, String>> result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		Map<String, String> re = new LinkedHashMap<String, String>();
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			re.put(datas.get(langField), datas.get(select));
		}
		return re;
	}

	/**
	 * desigination-of-states > designation-pct - regional
	 * 
	 * @author coreawin
	 * @date 2014. 11. 3.
	 * @param dataMaps
	 * @param ses
	 * @param langField
	 * @param select
	 * @return
	 */
	public static Map<String, Set<String>> getDesignaionPCTRegional(PatentDataMaps dataMaps) {
		Map<String, Set<String>> re = new LinkedHashMap<String, Set<String>>();

		try {
			String region = nullCheck(dataMaps.getDatas(EXMLSchema.designaion_pct_region));
			if ("".equals(region))
				return re;

			String[] regions = region.split(String.valueOf(PatentHandler.MULTI_VALUE_DELIM));
			String regional = nullCheck(dataMaps.getDatas(EXMLSchema.designaion_pct_regional));
			// System.out.println("region : " + region);
			// System.out.println("regional : " + regional);
			String[] regionals = regional.split("@");
			for (int idx = 0; idx < regions.length; idx += 1) {
				String r = nullCheck(regions[idx]).trim();
				String rv = nullCheck(regionals[idx + 1]).trim();
				String[] rvs = rv.split(String.valueOf(PatentHandler.MULTI_VALUE_DELIM));
				Set<String> rvSet = new TreeSet<String>();
				for (String rvsi : rvs) {
					String v = rvsi.trim();
					if ("".equals(v))
						continue;
					rvSet.add(v);
				}
				re.put(r, rvSet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return re;
	}

	public static Set<String> getLangData(Map<String, Set<String>> langData) {
		Set<String> set = new HashSet<String>();
		if (langData.containsKey("eng")) {
			set = langData.get("eng");
		} else {
			if (langData.size() > 0) {
				Set<String> s = langData.keySet();
				for (String key : s) {
					set = langData.get(key);
					break;
				}
			}
		}
		return set;
	}

	/**
	 * 출원인 정보 정제
	 * 
	 * @author neon
	 * @date 2013. 6. 20.
	 * @param dataMaps
	 * @return
	 */
	public static Set<String> getAssigneesInfo(PatentDataMaps dataMaps) {
		EXMLSchema[] ses = new EXMLSchema[] { EXMLSchema.assseq, EXMLSchema.assignee_lang, EXMLSchema.assignee, EXMLSchema.asscn };
		Map<Integer, Map<EXMLSchema, String>> result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		Set<String> r = new LinkedHashSet<String>();
		// result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		Map<String, String> cnInfoMap = getNationalityResidenceInfo(dataMaps, new EXMLSchema[] { EXMLSchema.asscnn, EXMLSchema.asscnr });
		// for (int rank : result.keySet()) {
		// Map<EXMLSchema, String> datas = result.get(rank);
		// String assseq = nullCheck(datas.get(EXMLSchema.assseq));
		// String lang = nullCheck(datas.get(EXMLSchema.assignee_lang));
		// /*
		// * 국적정보 채우기 우선순위 1)nationality 2) address-country 3)residence
		// */
		// String asscn = nullCheck(cnInfoMap.get("N" + assseq));
		// if ("".equals(asscn)) {
		// asscn = nullCheck(datas.get(EXMLSchema.asscn));
		// if ("".equals(asscn)) {
		// asscn = nullCheck(cnInfoMap.get("R" + assseq));
		// }
		// }
		//
		// String assignee =
		// nullCheck(datas.get(EXMLSchema.assignee)).replaceAll(";",
		// " ").replaceAll(
		// QueryConverter.REGX3, " ");
		// if (lang == null) {
		// lang = "";
		// }
		// if (!"".equals(assignee)) {
		// // r.add(lang + INVENTOR_DELIM + asscn + INVENTOR_DELIM + assignee);
		// }
		// }
		ses = new EXMLSchema[] { EXMLSchema.assseq, EXMLSchema.assignee_lang, EXMLSchema.asscn, EXMLSchema.assfn, EXMLSchema.assln, EXMLSchema.assignee };
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		// System.out.println("result 2 " + result);
		Map<String, String> m = new HashMap<String, String>();
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String assseq = nullCheck(datas.get(EXMLSchema.assseq));
			String assignee = nullCheck(datas.get(EXMLSchema.assignee));
			if (m.containsKey(assseq)) {
				String n = m.get(assseq);
				if (n.length() < assignee.length()) {
					m.put(assseq, assignee.trim());
				}
			} else {
				m.put(assseq, assignee);
			}
		}
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String assseq = nullCheck(datas.get(EXMLSchema.assseq));
			String lang = nullCheck(datas.get(EXMLSchema.assignee_lang));
			// String assignee = nullCheck(m.get(assseq));
			String assignee = nullCheck(
					datas.get(EXMLSchema.assignee)); /*
														 * 2014.11 .04 영문이 eng로
														 * 언어태킹이 안되어 있는 경우.
														 */

			// System.out.println(datas);
			// System.out.println(assseq + "---" + assignee);
			if (Utility.isEnglish(assignee) && !"eng".equalsIgnoreCase(lang.trim())) {
				lang = "eng";
			}
			/*
			 * 국적정보 채우기 우선순위 1)nationality 2) address-country 3)residence
			 */
			String asscn = nullCheck(cnInfoMap.get("N" + assseq));
			if ("".equals(asscn)) {
				asscn = nullCheck(datas.get(EXMLSchema.asscn));
				if ("".equals(asscn)) {
					asscn = nullCheck(cnInfoMap.get("R" + assseq));
				}
			}
			String assfn = nullCheck(datas.get(EXMLSchema.assfn));
			String assln = nullCheck(datas.get(EXMLSchema.assln));
			if (!"".equals(assfn) && !"".equals(assln)) {
				r.add(lang + INVENTOR_DELIM + asscn + INVENTOR_DELIM + removeChracter(assln + " " + assfn).toUpperCase());
				// System.out.println("==>1 " + lang + INVENTOR_DELIM + asscn +
				// INVENTOR_DELIM
				// + removeChracter(assln + " " + assfn).toUpperCase());
			} else {
				r.add(lang + INVENTOR_DELIM + asscn + INVENTOR_DELIM + removeChracter(assignee).toUpperCase());
				// System.out.println("==>2 " + lang + INVENTOR_DELIM + asscn +
				// INVENTOR_DELIM
				// + removeChracter(assignee).toUpperCase());
			}
		}
		// System.out.println(m);
		return r;
	}

	public static Set<String> getPriorityInfo(PatentDataMaps dataMaps) {
		EXMLSchema[] ses = null;
		Map<Integer, Map<EXMLSchema, String>> result = null;
		if (result == null) {
			ses = new EXMLSchema[] {
					// SchemaEnum.prino,
					EXMLSchema.pridno, EXMLSchema.pricn, EXMLSchema.prikind, EXMLSchema.pridate, EXMLSchema.pritkind, EXMLSchema.pridataformat };
		}
		Set<String> r = new LinkedHashSet<String>();
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String pricn = nullCheck(datas.get(EXMLSchema.pricn));
			String pridno = datas.get(EXMLSchema.pridno);
			String prikind = nullCheck(datas.get(EXMLSchema.prikind));
			String pridate = datas.get(EXMLSchema.pridate);
			String pritkind = nullCheck(datas.get(EXMLSchema.pritkind));
			String pridataformat = nullCheck(datas.get(EXMLSchema.pridataformat));
			// logger.info("pritkind {} ", pritkind);
			// logger.info("pridataformat {} ", pridataformat);
			r.add(pricn + "" + pridno + ", " + prikind + ", " + PatentDataFormat.convertDateHippen(pridate) + "," + pritkind + pridataformat);
			boolean a = true;
			if (a)
				continue;
			if ("national".equalsIgnoreCase(pritkind)) {
				r.add(pricn + pridno + ", " + prikind + ", " + PatentDataFormat.convertDateHippen(pridate) + "," + pritkind);
			} else if ("international".equalsIgnoreCase(pritkind)) {
				r.add(pricn + " " + pridno + ", " + prikind + ", " + PatentDataFormat.convertDateHippen(pridate) + "," + pritkind);
			} else {
				if (pricn == null) {
					r.add(pridno + " , , , " + pridataformat);
				} else {
					try {
						if (pridno.substring(0, 2).equalsIgnoreCase(pricn)) {
							r.add(pridno + ", " + prikind + ", " + PatentDataFormat.convertDateHippen(pridate) + "," + pridataformat);
						} else {
							r.add(pricn + pridno + ", " + prikind + ", " + PatentDataFormat.convertDateHippen(pridate) + "," + pridataformat);
						}
					} catch (Exception e) {
						r.add(pricn + pridno + ", " + prikind + ", " + PatentDataFormat.convertDateHippen(pridate) + "," + pridataformat);
					}
				}
			}
		}
		return r;
	}

	public static final String PRI_NO_DETAIL_DELIM = "ⓒⓑ";

	public static Set<String> getPriorityInfoDetailInfo(PatentDataMaps dataMaps) {
		EXMLSchema[] ses = null;
		Map<Integer, Map<EXMLSchema, String>> result = null;
		if (result == null) {
			ses = new EXMLSchema[] {
					// SchemaEnum.prino,
					EXMLSchema.pridno, EXMLSchema.pricn, EXMLSchema.prikind, EXMLSchema.pridate, EXMLSchema.pritkind, EXMLSchema.pridataformat };
		}
		Set<String> r = new LinkedHashSet<String>();
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String pricn = nullCheck(datas.get(EXMLSchema.pricn));
			String pridno = datas.get(EXMLSchema.pridno);
			String prikind = nullCheck(datas.get(EXMLSchema.prikind));
			String pridate = datas.get(EXMLSchema.pridate);
			String pritkind = nullCheck(datas.get(EXMLSchema.pritkind));
			String pridataformat = nullCheck(datas.get(EXMLSchema.pridataformat));
			// logger.info("pritkind {} ", pritkind);
			// logger.info("pridataformat {} ", pridataformat);
			r.add(pricn + "" + pridno + PRI_NO_DETAIL_DELIM + " " + prikind + PRI_NO_DETAIL_DELIM + " " + PatentDataFormat.convertDateHippen(pridate) + PRI_NO_DETAIL_DELIM + " " + pritkind + pridataformat);
			boolean a = true;
			if (a)
				continue;
			if ("national".equalsIgnoreCase(pritkind)) {
				r.add(pricn + pridno + PRI_NO_DETAIL_DELIM + " " + prikind + PRI_NO_DETAIL_DELIM + " " + PatentDataFormat.convertDateHippen(pridate) + PRI_NO_DETAIL_DELIM + " " + pritkind);
			} else if ("international".equalsIgnoreCase(pritkind)) {
				r.add(pricn + " " + pridno + PRI_NO_DETAIL_DELIM + " " + prikind + PRI_NO_DETAIL_DELIM + " " + PatentDataFormat.convertDateHippen(pridate) + PRI_NO_DETAIL_DELIM + " " + pritkind);
			} else {
				if (pricn == null) {
					r.add(pridno + " " + PRI_NO_DETAIL_DELIM + " " + PRI_NO_DETAIL_DELIM + " " + PRI_NO_DETAIL_DELIM + " " + pridataformat);
				} else {
					try {
						if (pridno.substring(0, 2).equalsIgnoreCase(pricn)) {
							r.add(pridno + PRI_NO_DETAIL_DELIM + " " + prikind + PRI_NO_DETAIL_DELIM + " " + PatentDataFormat.convertDateHippen(pridate) + PRI_NO_DETAIL_DELIM + " " + pridataformat);
						} else {
							r.add(pricn + pridno + PRI_NO_DETAIL_DELIM + " " + prikind + PRI_NO_DETAIL_DELIM + " " + PatentDataFormat.convertDateHippen(pridate) + PRI_NO_DETAIL_DELIM + " " + pridataformat);
						}
					} catch (Exception e) {
						r.add(pricn + pridno + PRI_NO_DETAIL_DELIM + " " + prikind + PRI_NO_DETAIL_DELIM + " " + PatentDataFormat.convertDateHippen(pridate) + PRI_NO_DETAIL_DELIM + " " + pridataformat);
					}
				}
			}
		}
		return r;
	}

	public static final String PRINO_DELIM = "_";

	public static Set<String> getPriorityInfoForExport(PatentDataMaps dataMaps) {
		EXMLSchema[] ses = null;
		Map<Integer, Map<EXMLSchema, String>> result = null;
		if (result == null) {
			ses = new EXMLSchema[] { EXMLSchema.prino, EXMLSchema.pridno, EXMLSchema.pricn, EXMLSchema.prikind, EXMLSchema.pridate, EXMLSchema.pritkind, EXMLSchema.pridataformat };
		}
		Set<String> r = new LinkedHashSet<String>();
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String pricn = nullCheck(datas.get(EXMLSchema.pricn));
			String pridno = datas.get(EXMLSchema.pridno);
			String prikind = nullCheck(datas.get(EXMLSchema.prikind));
			String pridate = datas.get(EXMLSchema.pridate);
			String pritkind = nullCheck(datas.get(EXMLSchema.pritkind));
			String pridataformat = nullCheck(datas.get(EXMLSchema.pridataformat));
			// logger.debug("pritkind " + pritkind);
			// logger.debug("pridataformat " + pridataformat);

			r.add(pricn + pridno + PRINO_DELIM + prikind + PRINO_DELIM + PatentDataFormat.convertDateHippen(pridate) + PRINO_DELIM + pritkind + pridataformat);

			boolean a = true;
			if (a)
				continue;

			if ("national".equalsIgnoreCase(pritkind)) {
				r.add(pricn + pridno + PRINO_DELIM + prikind + PRINO_DELIM + PatentDataFormat.convertDateHippen(pridate) + PRINO_DELIM + pritkind);
				continue;
			} else if ("international".equalsIgnoreCase(pritkind)) {
				r.add(pricn + " " + pridno + PRINO_DELIM + prikind + PRINO_DELIM + PatentDataFormat.convertDateHippen(pridate) + PRINO_DELIM + pritkind);
				continue;
			} else if ("regional".equalsIgnoreCase(pritkind)) {
				r.add(pricn + " " + pridno + PRINO_DELIM + prikind + PRINO_DELIM + PatentDataFormat.convertDateHippen(pridate) + PRINO_DELIM + pritkind);
				continue;
			}

			if ("original".equalsIgnoreCase(pridataformat)) {
				r.add(pricn + "" + pridno + PRINO_DELIM + prikind + PRINO_DELIM + PatentDataFormat.convertDateHippen(pridate) + PRINO_DELIM + pridataformat);
			} else if ("docdb".equalsIgnoreCase(pridataformat)) {
				r.add(pricn + "" + pridno + PRINO_DELIM + prikind + PRINO_DELIM + PatentDataFormat.convertDateHippen(pridate) + PRINO_DELIM + pridataformat);
			} else if ("epodoc".equalsIgnoreCase(pridataformat)) {
				r.add(pricn + "" + pridno + PRINO_DELIM + prikind + PRINO_DELIM + PatentDataFormat.convertDateHippen(pridate) + PRINO_DELIM + pridataformat);
			} else {
				// 아래 정보는 export시 포함시키지 않는다.
				/*
				 * if (pricn == null) { r.add(pridno + " , , , " +
				 * pridataformat); } else { try { if (pridno.substring(0,
				 * 2).equalsIgnoreCase(pricn)) { r.add(pridno + ", " + prikind +
				 * ", " + PatentDataFormat.convertDateHippen(pridate) + "," +
				 * pridataformat); } else { r.add(pricn + pridno + ", " +
				 * prikind + ", " + PatentDataFormat.convertDateHippen(pridate)
				 * + "," + pridataformat); } } catch (Exception e) { r.add(pricn
				 * + pridno + ", " + prikind + ", " +
				 * PatentDataFormat.convertDateHippen(pridate) + "," +
				 * pridataformat); } }
				 */
			}
		}
		return r;
	}

	public static TreeSet<String> getPriorityDateInfoForExport(PatentDataMaps dataMaps) {
		EXMLSchema[] ses = null;
		Map<Integer, Map<EXMLSchema, String>> result = null;
		if (result == null) {
			ses = new EXMLSchema[] { EXMLSchema.pridate };
		}
		TreeSet<String> r = new TreeSet<String>();
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String pridate = datas.get(EXMLSchema.pridate);
			r.add(PatentDataFormat.extractDateYear(pridate));
		}
		return r;
	}

	public static Set<String> getUSMainInfo(PatentDataMaps dataMaps) {
		EXMLSchema[] ses = null;
		Map<Integer, Map<EXMLSchema, String>> result = null;
		if (result == null) {
			ses = new EXMLSchema[] {
					// SchemaEnum.prino,
					EXMLSchema.classn };
		}
		Set<String> r = new LinkedHashSet<String>();
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String classn = datas.get(EXMLSchema.classn);
			r.add(classn);
		}
		return r;
	}

	public static String getUSMainInfoExport(PatentDataMaps dataMaps) {
		StringBuffer sb = new StringBuffer();
		EXMLSchema[] ses = null;
		Map<Integer, Map<EXMLSchema, String>> result = null;
		if (result == null) {
			ses = new EXMLSchema[] {
					// SchemaEnum.prino,
					EXMLSchema.classn };
		}
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		int i = 0;
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String classn = datas.get(EXMLSchema.classn);
			sb.append(classn);
			if (i < (result.size() - 1)) {
				sb.append(";");
			}
		}
		return sb.toString();
	}

	public static Set<String> getUSFurtherInfo(PatentDataMaps dataMaps) {
		EXMLSchema[] ses = null;
		Map<Integer, Map<EXMLSchema, String>> result = null;
		if (result == null) {
			ses = new EXMLSchema[] {
					// SchemaEnum.prino,
					EXMLSchema.classnf };
		}
		Set<String> r = new LinkedHashSet<String>();
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String classn = datas.get(EXMLSchema.classnf);
			r.add(classn);
		}
		return r;
	}

	public static Set<String> getECLAInfo(PatentDataMaps dataMaps) {
		EXMLSchema[] ses = null;
		Map<Integer, Map<EXMLSchema, String>> result = null;
		if (result == null) {
			ses = new EXMLSchema[] {
					// SchemaEnum.prino,
					EXMLSchema.eclaasg, EXMLSchema.eclas, EXMLSchema.eclac, EXMLSchema.eclasc, EXMLSchema.eclamg, EXMLSchema.eclasg, EXMLSchema.eclaschema };
		}
		Set<String> r = new LinkedHashSet<String>();
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String eclas = nullCheck(datas.get(EXMLSchema.eclas));
			String eclac = nullCheck(datas.get(EXMLSchema.eclac));
			String eclasc = nullCheck(datas.get(EXMLSchema.eclasc));
			String eclamg = nullCheck(datas.get(EXMLSchema.eclamg));
			String eclasg = nullCheck(datas.get(EXMLSchema.eclasg));

			String ecla = eclas + eclac + eclasc + eclamg + "/" + eclasg;
			if ("/".equals(ecla.trim()))
				continue;
			String eclaschema = nullCheck(datas.get(EXMLSchema.eclaschema));
			if ("EC".equalsIgnoreCase(eclaschema)) {
				r.add(ecla.trim());
			}
		}
		return r;
	}

	public static Set<String> getFTermInfo(PatentDataMaps dataMaps) {
		EXMLSchema[] ses = null;
		Map<Integer, Map<EXMLSchema, String>> result = null;
		if (result == null) {
			ses = new EXMLSchema[] { EXMLSchema.fcode };
		}
		Set<String> r = new LinkedHashSet<String>();
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String fcode = nullCheck(datas.get(EXMLSchema.fcode));

			if ("".equals(fcode.trim()))
				continue;
			r.add(fcode.trim());
		}
		return r;
	}

	public static Set<String> getECLAFullInfo(PatentDataMaps dataMaps) {
		EXMLSchema[] ses = null;
		Map<Integer, Map<EXMLSchema, String>> result = null;
		if (result == null) {
			ses = new EXMLSchema[] {
					// SchemaEnum.prino,
					EXMLSchema.eclas, EXMLSchema.eclac, EXMLSchema.eclasc, EXMLSchema.eclamg, EXMLSchema.eclasg, EXMLSchema.ecla, EXMLSchema.eclaschema };
		}
		Set<String> r = new LinkedHashSet<String>();
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String ecla = datas.get(EXMLSchema.ecla);
			if (ecla == null)
				continue;
			String eclaschema = nullCheck(datas.get(EXMLSchema.eclaschema));
			if ("EC".equalsIgnoreCase(eclaschema)) {
				r.add(ecla.trim());
			}
		}
		return r;
	}

	public static Set<String> getECLA4InfoForExport(PatentDataMaps dataMaps) {
		EXMLSchema[] ses = null;
		Map<Integer, Map<EXMLSchema, String>> result = null;
		if (result == null) {
			ses = new EXMLSchema[] { EXMLSchema.ecla, EXMLSchema.eclas, EXMLSchema.eclac, EXMLSchema.eclasc, EXMLSchema.eclaschema };
		}
		Set<String> r = new LinkedHashSet<String>();
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String ecla = datas.get(EXMLSchema.ecla);
			if (ecla == null)
				continue;
			String eclas = nullCheck(datas.get(EXMLSchema.eclas));
			String eclac = nullCheck(datas.get(EXMLSchema.eclac));
			String eclasc = nullCheck(datas.get(EXMLSchema.eclasc));
			String eclaschema = nullCheck(datas.get(EXMLSchema.eclaschema));
			if ("EC".equalsIgnoreCase(eclaschema)) {
				r.add(eclas + eclac + eclasc);
			}
		}
		return r;
	}

	public static final String DELIMETER_INFO = ",";
	public static final String DELIMETER_INFO_NEW = "@@";
	public static final String DELIMETER_INFO_NONPATENT = "↔";

	public static Set<String> getReferenceInfo(PatentDataMaps dataMaps) {
		EXMLSchema[] ses = null;
		Map<Integer, Map<EXMLSchema, String>> result = null;
		if (result == null) {
			ses = new EXMLSchema[] {
					// SchemaEnum.prino,
					EXMLSchema.refcn, EXMLSchema.refdn, EXMLSchema.refkind, EXMLSchema.refdate, EXMLSchema.refname, EXMLSchema.refappdate, EXMLSchema.ref_srep_phase };
		}
		Set<String> r = new LinkedHashSet<String>();
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String refcn = datas.get(EXMLSchema.refcn);
			String refdn = nullCheck(datas.get(EXMLSchema.refdn));
			String refkind = nullCheck(datas.get(EXMLSchema.refkind));

			String refNo = PatentDataFormat.convertPublicationNumber(refcn, refdn, refkind);
			String refdate = PatentDataFormat.convertDateHippen(datas.get(EXMLSchema.refdate));
			String refappdate = PatentDataFormat.convertDateHippen(datas.get(EXMLSchema.refappdate));
			String refname = nullCheck(datas.get(EXMLSchema.refname));
			String ref_srep_phase = nullCheck(datas.get(EXMLSchema.ref_srep_phase));
			if ("none".equalsIgnoreCase(refNo)) {
				continue;
			}
			if ("no".equalsIgnoreCase(refNo)) {
				continue;
			}
			r.add(refNo + DELIMETER_INFO_NEW + refdate + DELIMETER_INFO_NEW + refappdate + DELIMETER_INFO_NEW + refname + DELIMETER_INFO_NEW + ref_srep_phase);
		}
		return r;
	}

	public static Set<String> getBackWardPnos(PatentDataMaps dataMaps) {
		EXMLSchema[] ses = null;
		Map<Integer, Map<EXMLSchema, String>> result = null;
		if (result == null) {
			ses = new EXMLSchema[] {
					// SchemaEnum.prino,
					EXMLSchema.refcn, EXMLSchema.refdn, EXMLSchema.refkind };
		}
		Set<String> r = new LinkedHashSet<String>();
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String refcn = datas.get(EXMLSchema.refcn);
			String refdn = nullCheck(datas.get(EXMLSchema.refdn));
			String refkind = nullCheck(datas.get(EXMLSchema.refkind));
			String refNo = PatentDataFormat.convertPublicationNumber(refcn, refdn, refkind);
			if ("none".equalsIgnoreCase(refNo)) {
				continue;
			}
			if ("no".equalsIgnoreCase(refNo)) {
				continue;
			}
			r.add(refNo);
		}
		return r;
	}

	public static Set<String> getReferenceInfoForExport(PatentDataMaps dataMaps) {
		EXMLSchema[] ses = null;
		Map<Integer, Map<EXMLSchema, String>> result = null;
		if (result == null) {
			ses = new EXMLSchema[] {
					// SchemaEnum.prino,
					EXMLSchema.refcn, EXMLSchema.refdn, EXMLSchema.refkind, EXMLSchema.refdate, EXMLSchema.refname, EXMLSchema.refappdate, EXMLSchema.ref_srep_phase };
		}
		Set<String> r = new LinkedHashSet<String>();
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String refcn = datas.get(EXMLSchema.refcn);
			String refdn = nullCheck(datas.get(EXMLSchema.refdn));
			String refkind = nullCheck(datas.get(EXMLSchema.refkind));
			String refdate = nullCheck(datas.get(EXMLSchema.refdate));
			String refappdate = nullCheck(datas.get(EXMLSchema.refappdate));
			String ref_srep_phase = nullCheck(datas.get(EXMLSchema.ref_srep_phase));
			String refNo = PatentDataFormat.convertPublicationNumber(refcn, refdn, refkind);
			if ("none".equalsIgnoreCase(refNo)) {
				continue;
			}
			if ("no".equalsIgnoreCase(refNo)) {
				continue;
			}

//			if (!"e".equalsIgnoreCase(ref_srep_phase) && !"o".equalsIgnoreCase(ref_srep_phase)) {
//				continue;
//			}
			r.add(refNo);
//			r.add(refNo + DELIMETER_INFO_NEW + refdate + DELIMETER_INFO_NEW + refappdate + DELIMETER_INFO_NEW + ref_srep_phase);
		}
		return r;
	}

	public static Set<String> getCitationInfo(PatentDataMaps dataMaps) {
		EXMLSchema[] ses = null;
		Map<Integer, Map<EXMLSchema, String>> result = null;
		if (result == null) {
			ses = new EXMLSchema[] {
					// SchemaEnum.prino,
					EXMLSchema.citcn, EXMLSchema.citdn, EXMLSchema.citkind, EXMLSchema.citdate, EXMLSchema.citname, EXMLSchema.citappdate, EXMLSchema.cit_srep_phase };
		}
		Set<String> r = new LinkedHashSet<String>();
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String citcn = datas.get(EXMLSchema.citcn);
			String citdn = nullCheck(datas.get(EXMLSchema.citdn));
			String citkind = nullCheck(datas.get(EXMLSchema.citkind));

			String citNo = PatentDataFormat.convertPublicationNumber(citcn, citdn, citkind);
			String citdate = PatentDataFormat.convertDateHippen(datas.get(EXMLSchema.citdate));
			String citappdate = PatentDataFormat.convertDateHippen(datas.get(EXMLSchema.citappdate));
			String citname = nullCheck(datas.get(EXMLSchema.citname));
			String cit_srep_phase = nullCheck(datas.get(EXMLSchema.cit_srep_phase));
			if ("none".equalsIgnoreCase(citNo)) {
				continue;
			}
			if ("no".equalsIgnoreCase(citNo)) {
				continue;
			}
			r.add(citNo + DELIMETER_INFO_NEW + citdate + DELIMETER_INFO_NEW + citappdate + DELIMETER_INFO_NEW + citname + DELIMETER_INFO_NEW + cit_srep_phase);
		}
		return r;
	}

	public static Set<String> getForwardPnos(PatentDataMaps dataMaps) {
		EXMLSchema[] ses = null;
		Map<Integer, Map<EXMLSchema, String>> result = null;
		if (result == null) {
			ses = new EXMLSchema[] {
					// SchemaEnum.prino,
					EXMLSchema.citcn, EXMLSchema.citdn, EXMLSchema.citkind };
		}
		Set<String> r = new LinkedHashSet<String>();
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String citcn = datas.get(EXMLSchema.citcn);
			String citdn = nullCheck(datas.get(EXMLSchema.citdn));
			String citkind = nullCheck(datas.get(EXMLSchema.citkind));

			String citNo = PatentDataFormat.convertPublicationNumber(citcn, citdn, citkind);

			if ("none".equalsIgnoreCase(citNo)) {
				continue;
			}
			if ("no".equalsIgnoreCase(citNo)) {
				continue;
			}
			r.add(citNo);
		}
		return r;
	}

	public static Set<String> getCitationInfoForExport(PatentDataMaps dataMaps) {
		EXMLSchema[] ses = null;
		Map<Integer, Map<EXMLSchema, String>> result = null;
		if (result == null) {
			ses = new EXMLSchema[] {
					// SchemaEnum.prino,
					EXMLSchema.citcn, EXMLSchema.citdn, EXMLSchema.citkind, EXMLSchema.citdate, EXMLSchema.citname, EXMLSchema.citappdate };
		}
		Set<String> r = new LinkedHashSet<String>();
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String citcn = datas.get(EXMLSchema.citcn);
			String citdn = nullCheck(datas.get(EXMLSchema.citdn));
			String citkind = nullCheck(datas.get(EXMLSchema.citkind));
			String citdate = nullCheck(datas.get(EXMLSchema.citdate));
			String citappdate = nullCheck(datas.get(EXMLSchema.citappdate));
			String citNo = PatentDataFormat.convertPublicationNumber(citcn, citdn, citkind);
			if ("none".equalsIgnoreCase(citNo)) {
				continue;
			}
			if ("no".equalsIgnoreCase(citNo)) {
				continue;
			}
			r.add(citNo + DELIMETER_INFO_NEW + citdate + DELIMETER_INFO_NEW + citappdate);
		}
		return r;
	}

	@Deprecated
	public static Set<String> getIPC(PatentDataMaps dataMaps) {
		Set<String> r = new LinkedHashSet<String>();
		EXMLSchema[] ses = null;
		Map<Integer, Map<EXMLSchema, String>> result = null;
		if (result == null) {
			ses = new EXMLSchema[] { EXMLSchema.ipcrs, EXMLSchema.ipcrc, EXMLSchema.ipcrsc, EXMLSchema.ipcrmg, EXMLSchema.ipcradate, EXMLSchema.ipcrsp, EXMLSchema.ipcrlevel, EXMLSchema.ipcrcds, EXMLSchema.ipcrstatus, EXMLSchema.ipcrcv,
					EXMLSchema.ipcrcn, EXMLSchema.ipcrividate, EXMLSchema.ipcrsg };
		}

		return r;
	}

	public static Set<String>[] getIPCRInfo(PatentDataMaps dataMaps) {
		EXMLSchema[] ses = null;
		Map<Integer, Map<EXMLSchema, String>> result = null;
		if (result == null) {
			ses = new EXMLSchema[] { EXMLSchema.ipcrs, EXMLSchema.ipcrc, EXMLSchema.ipcrsc, EXMLSchema.ipcrmg, EXMLSchema.ipcradate, EXMLSchema.ipcrsp, EXMLSchema.ipcrlevel, EXMLSchema.ipcrcds, EXMLSchema.ipcrstatus, EXMLSchema.ipcrcv,
					EXMLSchema.ipcrcn, EXMLSchema.ipcrividate, EXMLSchema.ipcrsg };
		}
		Set<String>[] sets = new Set[2];
		Set<String> r = new LinkedHashSet<String>();
		Set<String> rCode = new LinkedHashSet<String>();
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		// Map<String, IPCBean> ipcDescription =
		// DescriptionData.getIPCDescription();
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String ipcrs = datas.get(EXMLSchema.ipcrs);
			String ipcrlevel = datas.get(EXMLSchema.ipcrlevel);
			String ipcrc = datas.get(EXMLSchema.ipcrc);
			String ipcrsc = datas.get(EXMLSchema.ipcrsc);
			String ipcrmg = datas.get(EXMLSchema.ipcrmg);
			String ipcrsg = datas.get(EXMLSchema.ipcrsg);
			String ipcradate = datas.get(EXMLSchema.ipcradate);
			String ipcrividate = datas.get(EXMLSchema.ipcrividate);
			String ipcrcv = datas.get(EXMLSchema.ipcrcv);
			String ipcrsp = datas.get(EXMLSchema.ipcrsp);
			String ipcrcds = datas.get(EXMLSchema.ipcrcds);
			String ipcrcn = datas.get(EXMLSchema.ipcrcn);
			String ipcrstatus = datas.get(EXMLSchema.ipcrstatus);

			String fcode = ipcrs + ipcrc + ipcrsc;
			String scode = nullCheck(ipcrmg) + "/" + nullCheck(ipcrsg);
			if (scode.length() == 1)
				scode = "";
			String code = fcode + scode;
			r.add(code + " (" + PatentDataFormat.convertDateHippen(ipcrividate) + "; " + nullCheck(ipcrlevel) + ", " + nullCheck(ipcrsp) + ", " + nullCheck(ipcrcv) + ", " + PatentDataFormat.convertDateHippen(ipcradate) + "; " + nullCheck(ipcrstatus)
					+ ", " + nullCheck(ipcrcds) + ", " + nullCheck(ipcrcn) + ") ");

			// if (ipcDescription != null) {
			// // IPCBean bean = ipcDescription.get(fcode);
			// if (bean != null) {
			// rCode.add(fcode + ":" + bean.getDescription() + " : " +
			// bean.getDescriptionKor());
			// } else {
			// rCode.add(fcode + ": : ");
			// }
			// } else {
			rCode.add(fcode);
			// }
		}

		ses = new EXMLSchema[] { EXMLSchema.ipcs, EXMLSchema.ipcc, EXMLSchema.ipcsc, EXMLSchema.ipcmg, EXMLSchema.ipcsg };
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String ipcs = nullCheck(datas.get(EXMLSchema.ipcs));
			String ipcc = nullCheck(datas.get(EXMLSchema.ipcc));
			String ipcsc = nullCheck(datas.get(EXMLSchema.ipcsc));
			String ipcmg = nullCheck(datas.get(EXMLSchema.ipcmg));
			String ipcsg = nullCheck(datas.get(EXMLSchema.ipcsg));
			String fcode = ipcs + ipcc + ipcsc;
			String scode = nullCheck(ipcmg) + "/" + nullCheck(ipcsg);
			if (scode.length() == 1)
				scode = "";
			String code = fcode + scode;
			r.add(code);

			// if (ipcDescription != null) {
			// IPCBean bean = ipcDescription.get(fcode);
			// if (bean != null) {
			// rCode.add(fcode + ":" + bean.getDescription() + " : " +
			// bean.getDescriptionKor());
			// } else {
			// rCode.add(fcode + ": : ");
			// }
			// } else {
			rCode.add(fcode);
			// }
		}

		sets[0] = r;
		sets[1] = rCode;
		return sets;
	}

	public static Set<String> getIPCFullInfo(PatentDataMaps dataMaps) {
		EXMLSchema[] ses = null;
		Map<Integer, Map<EXMLSchema, String>> result = null;
		if (result == null) {
			ses = new EXMLSchema[] { EXMLSchema.ipcrs, EXMLSchema.ipcrc, EXMLSchema.ipcrsc, EXMLSchema.ipcrmg, EXMLSchema.ipcradate, EXMLSchema.ipcrsp, EXMLSchema.ipcrlevel, EXMLSchema.ipcrcds, EXMLSchema.ipcrstatus, EXMLSchema.ipcrcv,
					EXMLSchema.ipcrcn, EXMLSchema.ipcrividate, EXMLSchema.ipcrsg };
		}
		Set<String> r = new LinkedHashSet<String>();
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String ipcrs = datas.get(EXMLSchema.ipcrs);
			String ipcrc = datas.get(EXMLSchema.ipcrc);
			String ipcrsc = datas.get(EXMLSchema.ipcrsc);
			String ipcrmg = datas.get(EXMLSchema.ipcrmg);
			String ipcrsg = datas.get(EXMLSchema.ipcrsg);

			String fcode = ipcrs + ipcrc + ipcrsc;
			String scode = nullCheck(ipcrmg) + "/" + nullCheck(ipcrsg);
			if (scode.length() == 1)
				scode = "";
			String code = fcode + " " + scode;
			r.add(code);
		}

		ses = new EXMLSchema[] { EXMLSchema.ipcs, EXMLSchema.ipcc, EXMLSchema.ipcsc, EXMLSchema.ipcmg, EXMLSchema.ipcsg };
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String ipcs = nullCheck(datas.get(EXMLSchema.ipcs));
			String ipcc = nullCheck(datas.get(EXMLSchema.ipcc));
			String ipcsc = nullCheck(datas.get(EXMLSchema.ipcsc));
			String ipcmg = nullCheck(datas.get(EXMLSchema.ipcmg));
			String ipcsg = nullCheck(datas.get(EXMLSchema.ipcsg));
			String fcode = ipcs + ipcc + ipcsc;
			String scode = nullCheck(ipcmg) + "/" + nullCheck(ipcsg);
			if (scode.length() == 1)
				scode = "";
			String code = fcode + " " + scode;
			r.add(code);
		}

		return r;
	}

	public static Set<String> getIPC4InfoForExport(PatentDataMaps dataMaps) {
		EXMLSchema[] ses = null;
		Map<Integer, Map<EXMLSchema, String>> result = null;
		ses = new EXMLSchema[] { EXMLSchema.ipcs, EXMLSchema.ipcc, EXMLSchema.ipcsc };
		Set<String> rCode = new LinkedHashSet<String>();
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String ipcs = nullCheck(datas.get(EXMLSchema.ipcs));
			String ipcc = nullCheck(datas.get(EXMLSchema.ipcc));
			String ipcsc = nullCheck(datas.get(EXMLSchema.ipcsc));
			String fcode = ipcs + ipcc + ipcsc;
			rCode.add(fcode);
		}

		ses = new EXMLSchema[] { EXMLSchema.ipcrs, EXMLSchema.ipcrc, EXMLSchema.ipcrsc, EXMLSchema.ipcrmg, EXMLSchema.ipcradate, EXMLSchema.ipcrsp, EXMLSchema.ipcrlevel, EXMLSchema.ipcrcds, EXMLSchema.ipcrstatus, EXMLSchema.ipcrcv, EXMLSchema.ipcrcn,
				EXMLSchema.ipcrividate, EXMLSchema.ipcrsg };
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String ipcrs = nullCheck(datas.get(EXMLSchema.ipcrs));
			String ipcrc = nullCheck(datas.get(EXMLSchema.ipcrc));
			String ipcrsc = nullCheck(datas.get(EXMLSchema.ipcrsc));
			String fcode = ipcrs + ipcrc + ipcrsc;
			rCode.add(fcode);
		}
		return rCode;
	}

	public static Set<String> getIPCFullInfoForExport(PatentDataMaps dataMaps) {
		EXMLSchema[] ses = null;
		Map<Integer, Map<EXMLSchema, String>> result = null;
		if (result == null) {
			ses = new EXMLSchema[] { EXMLSchema.ipcrs, EXMLSchema.ipcrc, EXMLSchema.ipcrsc, EXMLSchema.ipcrmg, EXMLSchema.ipcradate, EXMLSchema.ipcrsp, EXMLSchema.ipcrlevel, EXMLSchema.ipcrcds, EXMLSchema.ipcrstatus, EXMLSchema.ipcrcv,
					EXMLSchema.ipcrcn, EXMLSchema.ipcrividate, EXMLSchema.ipcrsg };
		}
		Set<String> rCode = new LinkedHashSet<String>();
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String ipcrs = nullCheck(datas.get(EXMLSchema.ipcrs));
			String ipcrc = nullCheck(datas.get(EXMLSchema.ipcrc));
			String ipcrsc = nullCheck(datas.get(EXMLSchema.ipcrsc));
			String fcode = ipcrs + ipcrc + ipcrsc;
			rCode.add(fcode);
		}
		ses = new EXMLSchema[] { EXMLSchema.ipcs, EXMLSchema.ipcc, EXMLSchema.ipcsc };
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String ipcs = nullCheck(datas.get(EXMLSchema.ipcs));
			String ipcc = nullCheck(datas.get(EXMLSchema.ipcc));
			String ipcsc = nullCheck(datas.get(EXMLSchema.ipcsc));
			String fcode = ipcs + ipcc + ipcsc;
			rCode.add(fcode);
		}
		return rCode;
	}

	public static Set<String>[] getCPCInfo(PatentDataMaps dataMaps) {
		EXMLSchema[] ses = null;
		Map<Integer, Map<EXMLSchema, String>> result = null;
		if (result == null) {
			ses = new EXMLSchema[] { EXMLSchema.cpc, EXMLSchema.cpcs, EXMLSchema.cpcc, EXMLSchema.cpcsc, EXMLSchema.cpcmg, EXMLSchema.cpcsg, EXMLSchema.cpcsp, EXMLSchema.cpccv, EXMLSchema.cpcad, EXMLSchema.cpccn, EXMLSchema.cpcgo,
					EXMLSchema.cpcstatus, EXMLSchema.cpcdatasource };
		}
		Set<String>[] sets = new Set[2];
		Set<String> r = new LinkedHashSet<String>();
		Set<String> rCode = new LinkedHashSet<String>();
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		// Map<String, CPCBean> cpcDescription =
		// DescriptionData.getCPCDescription();
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String cpc = nullCheck(datas.get(EXMLSchema.cpc));
			String cpcs = nullCheck(datas.get(EXMLSchema.cpcs));
			String cpcc = nullCheck(datas.get(EXMLSchema.cpcc));
			String cpcsc = nullCheck(datas.get(EXMLSchema.cpcsc));
			String cpcmg = nullCheck(datas.get(EXMLSchema.cpcmg));
			String cpcsg = nullCheck(datas.get(EXMLSchema.cpcsg));
			String cpcsp = nullCheck(datas.get(EXMLSchema.cpcsp));
			String cpccv = nullCheck(datas.get(EXMLSchema.cpccv));
			String cpcad = nullCheck(datas.get(EXMLSchema.cpcad));
			String cpccn = nullCheck(datas.get(EXMLSchema.cpccn));
			String cpcgo = nullCheck(datas.get(EXMLSchema.cpcgo));
			String cpcstatus = nullCheck(datas.get(EXMLSchema.cpcstatus));
			String cpcdatasource = nullCheck(datas.get(EXMLSchema.cpcdatasource));

			String fcode = cpcs + cpcc + cpcsc;
			String scode = nullCheck(cpcmg) + "/" + nullCheck(cpcsg);
			if (scode.length() == 1)
				scode = "";
			if (fcode.length() < 1) {
				if (cpc.indexOf(" ") != -1) {
					fcode = cpc.substring(0, cpc.indexOf(" "));
				} else {
					fcode = cpc;
				}
			}

			String code = fcode + " " + scode;
			code = code.trim();

			r.add(code + " (" + PatentDataFormat.convertDateHippen(cpcad) + ", " + nullCheck(cpcsp) + ", " + nullCheck(cpccv) + ", " + nullCheck(cpcstatus) + ", " + nullCheck(cpcdatasource) + ", " + nullCheck(cpccn) + ") ");

			// try {
			// CPCBean bean = cpcDescription.get(fcode);
			// if (bean != null) {
			// rCode.add(fcode + ":" + bean.getDescription() + " : " +
			// bean.getDescriptionKor());
			// } else {
			rCode.add(fcode + ": : ");
			// }
			// } catch (Exception e) {
			// }
		}
		sets[0] = r;
		sets[1] = rCode;
		return sets;
	}

	public static Set<String> getCPCFullInfo(PatentDataMaps dataMaps) {
		EXMLSchema[] ses = null;
		Map<Integer, Map<EXMLSchema, String>> result = null;
		if (result == null) {
			ses = new EXMLSchema[] { EXMLSchema.cpc, EXMLSchema.cpcs, EXMLSchema.cpcc, EXMLSchema.cpcsc, EXMLSchema.cpcmg, EXMLSchema.cpcsg, EXMLSchema.cpcsp, EXMLSchema.cpccv, EXMLSchema.cpcad, EXMLSchema.cpccn, EXMLSchema.cpcgo,
					EXMLSchema.cpcstatus, EXMLSchema.cpcdatasource };
		}
		Set<String> r = new LinkedHashSet<String>();
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String cpc = nullCheck(datas.get(EXMLSchema.cpc));
			String cpcs = nullCheck(datas.get(EXMLSchema.cpcs));
			String cpcc = nullCheck(datas.get(EXMLSchema.cpcc));
			String cpcsc = nullCheck(datas.get(EXMLSchema.cpcsc));
			String cpcmg = nullCheck(datas.get(EXMLSchema.cpcmg));
			String cpcsg = nullCheck(datas.get(EXMLSchema.cpcsg));

			String fcode = cpcs + cpcc + cpcsc;
			String scode = nullCheck(cpcmg) + "/" + nullCheck(cpcsg);

			if (fcode.length() < 1) {
				fcode = cpc;
			}
			if (scode.length() == 1)
				scode = "";
			String code = fcode + " " + scode;
			r.add(code);
		}
		return r;
	}

	public static Set<String> getCPC4InfoForExport(PatentDataMaps dataMaps) {
		EXMLSchema[] ses = null;
		Map<Integer, Map<EXMLSchema, String>> result = null;
		if (result == null) {
			ses = new EXMLSchema[] { EXMLSchema.cpc, EXMLSchema.cpcs, EXMLSchema.cpcc, EXMLSchema.cpcsc, EXMLSchema.cpcmg, EXMLSchema.cpcsg, EXMLSchema.cpcsp, EXMLSchema.cpccv, EXMLSchema.cpcad, EXMLSchema.cpccn, EXMLSchema.cpcgo,
					EXMLSchema.cpcstatus, EXMLSchema.cpcdatasource };
		}
		Set<String> rCode = new LinkedHashSet<String>();
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		// System.out.println(result);
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String cpcs = nullCheck(datas.get(EXMLSchema.cpcs));
			String cpcc = nullCheck(datas.get(EXMLSchema.cpcc));
			String cpcsc = nullCheck(datas.get(EXMLSchema.cpcsc));
			String fcode = cpcs + cpcc + cpcsc;
			if ("".equals(fcode)) {
				String cpc = nullCheck(datas.get(EXMLSchema.cpc));
				if (fcode.length() < 1) {
					if (cpc.indexOf(" ") != -1) {
						fcode = cpc.substring(0, cpc.indexOf(" "));
					} else {
						fcode = cpc;
					}
				}
			}
			rCode.add(fcode);
		}
		return rCode;
	}

	public static Set<String> getCompleteFamilyInfo(PatentDataMaps dataMaps) {
		EXMLSchema[] ses = null;
		Map<Integer, Map<EXMLSchema, String>> result = null;
		if (result == null) {
			ses = new EXMLSchema[] {
					// SchemaEnum.prino,
					EXMLSchema.cfcn, EXMLSchema.cfdno, EXMLSchema.cfkind, EXMLSchema.cfdate, EXMLSchema.cfappdate };
		}
		Set<String> r = new LinkedHashSet<String>();
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String cfcn = datas.get(EXMLSchema.cfcn);
			String cfdno = datas.get(EXMLSchema.cfdno);
			String cfkind = nullCheck(datas.get(EXMLSchema.cfkind));
			String cfdate = PatentDataFormat.convertDateHippen(nullCheck(datas.get(EXMLSchema.cfdate)));
			String cfappdate = PatentDataFormat.convertDateHippen(nullCheck(datas.get(EXMLSchema.cfappdate)));
			String cfNo = PatentDataFormat.convertPublicationNumber(cfcn, cfdno, cfkind);
			r.add(cfNo + DELIMETER_INFO_NEW + cfdate + DELIMETER_INFO_NEW + cfappdate);
		}
		return r;
	}

	public static Set<String> getExtendFamilyPnos(PatentDataMaps dataMaps) {
		EXMLSchema[] ses = null;
		Map<Integer, Map<EXMLSchema, String>> result = null;
		if (result == null) {
			ses = new EXMLSchema[] {
					// SchemaEnum.prino,
					EXMLSchema.cfcn, EXMLSchema.cfdno, EXMLSchema.cfkind };
		}
		Set<String> r = new LinkedHashSet<String>();
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String cfcn = datas.get(EXMLSchema.cfcn);
			String cfdno = datas.get(EXMLSchema.cfdno);
			String cfkind = nullCheck(datas.get(EXMLSchema.cfkind));
			String cfNo = PatentDataFormat.convertPublicationNumber(cfcn, cfdno, cfkind);
			r.add(cfNo);
		}
		return r;
	}

	public static Set<String> getCompleteFamilyInfoForExport(PatentDataMaps dataMaps) {
		EXMLSchema[] ses = null;
		Map<Integer, Map<EXMLSchema, String>> result = null;
		if (result == null) {
			ses = new EXMLSchema[] {
					// SchemaEnum.prino,
					EXMLSchema.cfcn, EXMLSchema.cfdno, EXMLSchema.cfkind };
		}
		Set<String> r = new LinkedHashSet<String>();
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String cfcn = nullCheck(datas.get(EXMLSchema.cfcn));
			String cfdno = nullCheck(datas.get(EXMLSchema.cfdno));
			String cfkind = nullCheck(datas.get(EXMLSchema.cfkind));
			String cfNo = PatentDataFormat.convertPublicationNumber(cfcn, cfdno, cfkind);
			r.add(cfNo);
		}
		return r;
	}

	static Pattern extractEIDPattern = Pattern.compile("(?<=eid=2-s2.0-).+(?=&)");
	static Pattern extractTitlePattern = Pattern.compile("(?<=“|\").+(?=”|\")");
	static Pattern extractXPPattern = Pattern.compile(",\\sXP[0-9]{1,},|(,\\sXP[0-9]{1,})$|,\\sXP[0-9]{1,}\\s|\\sXP[0-9]{1,}\\s");

	public static Set<String> getNonPatent(PatentDataMaps dataMaps) {
		EXMLSchema[] ses = null;
		Map<Integer, Map<EXMLSchema, String>> result = null;
		if (result == null) {
			ses = new EXMLSchema[] { EXMLSchema.nonpatent, EXMLSchema.scopusurl };
		}
		Set<String> r = new LinkedHashSet<String>();
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String nonpatent = nullCheck(datas.get(EXMLSchema.nonpatent));
			String scopusurl = nullCheck(datas.get(EXMLSchema.scopusurl));
			String title = "";
			Matcher m = extractTitlePattern.matcher(nonpatent);
			while (m.find()) {
				title = m.group();
			}

			String xp = "";

			m = extractXPPattern.matcher(nonpatent);
			while (m.find()) {
				xp = m.group();
			}

			logger.debug("XP INDEX : {}", xp);

			if (xp == null) {
				xp = "";
			}

			if (xp.length() > 0) {
				xp = xp.substring(1);
				xp = xp.trim();
				if (xp.endsWith(",")) {
					xp = xp.substring(0, (xp.length() - 1));
				}
			}

			if (scopusurl.length() > 0) {
				m = extractEIDPattern.matcher(scopusurl);
				String eid = "";
				while (m.find()) {
					eid = m.group();
				}

				r.add(rank + DELIMETER_INFO_NONPATENT + nonpatent.replaceAll("[“”]", "\"") + DELIMETER_INFO_NONPATENT + title + DELIMETER_INFO_NONPATENT + scopusurl + DELIMETER_INFO_NONPATENT + eid + DELIMETER_INFO_NONPATENT + xp);
			} else {

				if (title.length() < 1) {
					title = " ";
				}

				r.add(rank + DELIMETER_INFO_NONPATENT + nonpatent.replaceAll("[“”]", "\"") + DELIMETER_INFO_NONPATENT + title + DELIMETER_INFO_NONPATENT + " " + DELIMETER_INFO_NONPATENT + xp);
			}
		}
		return r;
	}

	public static Set<String> getNonPatentForExport(PatentDataMaps dataMaps) {
		EXMLSchema[] ses = null;
		Map<Integer, Map<EXMLSchema, String>> result = null;
		if (result == null) {
			ses = new EXMLSchema[] { EXMLSchema.nonpatent, EXMLSchema.scopusurl };
		}
		Set<String> r = new LinkedHashSet<String>();
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String nonpatent = nullCheck(datas.get(EXMLSchema.nonpatent)).replaceAll("[\\\t|;]", " ");
			String scopusurl = nullCheck(datas.get(EXMLSchema.scopusurl)).replaceAll("[\\\t|;]", " ");
			// r.add(nonpatent + DELIMETER_INFO_NONPATENT);
			nonpatent = nonpatent.replaceAll("`", "'");

			r.add(nonpatent + "`" + scopusurl);
			// r.add(nonpatent + DELIMETER_INFO_NONPATENT + scopusurl);
		}
		return r;
	}

	public static Set<String> getMainFamilyInfo(PatentDataMaps dataMaps) {
		EXMLSchema[] ses = null;
		Map<Integer, Map<EXMLSchema, String>> result = null;
		if (result == null) {
			ses = new EXMLSchema[] {
					// SchemaEnum.prino,
					EXMLSchema.mfcn, EXMLSchema.mfdno, EXMLSchema.mfkind, EXMLSchema.mfdate, EXMLSchema.mfappdate };
		}
		Set<String> r = new LinkedHashSet<String>();
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String mfcn = datas.get(EXMLSchema.mfcn);
			String mfdno = datas.get(EXMLSchema.mfdno);
			String mfkind = nullCheck(datas.get(EXMLSchema.mfkind));
			String mfdate = PatentDataFormat.convertDateHippen(nullCheck(datas.get(EXMLSchema.mfdate)));
			String mfappdate = PatentDataFormat.convertDateHippen(nullCheck(datas.get(EXMLSchema.mfappdate)));
			String mfNo = PatentDataFormat.convertPublicationNumber(mfcn, mfdno, mfkind);
			r.add(mfNo + DELIMETER_INFO_NEW + mfdate + DELIMETER_INFO_NEW + mfappdate);
		}
		return r;
	}

	// public static Set<String> getMainFamilyNumber(PatentDataMaps dataMaps) {
	// EXMLSchema[] ses = null;
	// Map<Integer, Map<EXMLSchema, String>> result = null;
	// if (result == null) {
	// ses = new EXMLSchema[] {
	// // SchemaEnum.prino,
	// EXMLSchema.mfcn, EXMLSchema.mfdno, EXMLSchema.mfkind, EXMLSchema.mfdate,
	// EXMLSchema.mfappdate };
	// }
	// Set<String> r = new LinkedHashSet<String>();
	// result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
	// for (int rank : result.keySet()) {
	// Map<EXMLSchema, String> datas = result.get(rank);
	// String mfcn = datas.get(EXMLSchema.mfcn);
	// String mfdno = datas.get(EXMLSchema.mfdno);
	// String mfkind = nullCheck(datas.get(EXMLSchema.mfkind));
	// String mfNo = PatentDataFormat.convertPublicationNumber(mfcn, mfdno,
	// mfkind);
	// r.add(mfNo);
	// }
	// return r;
	// }

	public static Set<String> getMainFamilyInfoForExport(PatentDataMaps dataMaps) {
		EXMLSchema[] ses = null;
		Map<Integer, Map<EXMLSchema, String>> result = null;
		if (result == null) {
			ses = new EXMLSchema[] {
					// SchemaEnum.prino,
					EXMLSchema.mfcn, EXMLSchema.mfdno, EXMLSchema.mfkind };
		}
		Set<String> r = new LinkedHashSet<String>();
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String mfcn = nullCheck(datas.get(EXMLSchema.mfcn));
			String mfdno = nullCheck(datas.get(EXMLSchema.mfdno));
			String mfkind = nullCheck(datas.get(EXMLSchema.mfkind));
			String mfNo = PatentDataFormat.convertPublicationNumber(mfcn, mfdno, mfkind);
			r.add(mfNo);
		}
		return r;
	}

	public static Set<String> getPrimaryExaminerInfo(PatentDataMaps dataMaps) {
		EXMLSchema[] ses = null;
		Map<Integer, Map<EXMLSchema, String>> result = null;
		if (result == null) {
			ses = new EXMLSchema[] {
					// SchemaEnum.prino,
					EXMLSchema.pexam, EXMLSchema.pexam_firstname, EXMLSchema.pexam_lastname, EXMLSchema.pexam_department };
		}
		Set<String> r = new LinkedHashSet<String>();
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		// System.out.println(result);
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String pexam = nullCheck(datas.get(EXMLSchema.pexam));
			String pexam_firstname = nullCheck(datas.get(EXMLSchema.pexam_firstname));
			String pexam_lastname = nullCheck(datas.get(EXMLSchema.pexam_lastname));
			if ("".equals(pexam)) {
				pexam = pexam_firstname + " " + pexam_lastname;
			}
			r.add(pexam);
		}
		return r;
	}

	public static Multimap<String, String> getAgentInfo(PatentDataMaps dataMaps) {
		EXMLSchema[] ses = null;
		Map<Integer, Map<EXMLSchema, String>> result = null;
		if (result == null) {
			ses = new EXMLSchema[] {
					// SchemaEnum.prino,
					EXMLSchema.agent, EXMLSchema.agrepType };
		}
		Multimap<String, String> r = HashMultimap.create();
		result = PatentDataParser.getMultiValueDatas(dataMaps, ses);
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String agent = nullCheck(datas.get(EXMLSchema.agent));
			String agrepType = nullCheck(datas.get(EXMLSchema.agrepType));
			r.put(agrepType, agent);
		}
		return r;
	}

	public static String nullCheck(String s) {
		return s == null ? "" : s;
	}

	public static Map<String, Map<String, String>> getDescriptionInfoALL(PatentDataMaps dataMaps) {
		EXMLSchema[] ses = new EXMLSchema[] { EXMLSchema.description_lang, EXMLSchema.description_id, EXMLSchema.description_format, EXMLSchema.description_detailed_desc, EXMLSchema.description_drawings, EXMLSchema.description_related_apps,
				EXMLSchema.description_summary, EXMLSchema.description };
		Map<Integer, Map<EXMLSchema, String>> result = PatentDataParser.getMultiValueDatas(dataMaps, ses);

		Map<String, Map<String, String>> r = new HashMap<String, Map<String, String>>();
		for (int rank : result.keySet()) {
			Map<EXMLSchema, String> datas = result.get(rank);
			String lang = StringUtil.nullCheck(datas.get(EXMLSchema.description_lang)).toLowerCase().trim();
			if ("".equals(lang))
				lang = String.valueOf(rank);
			Map<String, String> m = new HashMap<String, String>();
			m.put(EXMLSchema.description_lang.name(), datas.get(EXMLSchema.description_lang));
			m.put(EXMLSchema.description_id.name(), datas.get(EXMLSchema.description_id));
			m.put(EXMLSchema.description_format.name(), datas.get(EXMLSchema.description_format));
			m.put(EXMLSchema.description_detailed_desc.name(), datas.get(EXMLSchema.description_detailed_desc));
			m.put(EXMLSchema.description_drawings.name(), datas.get(EXMLSchema.description_drawings));
			m.put(EXMLSchema.description_related_apps.name(), datas.get(EXMLSchema.description_related_apps));
			m.put(EXMLSchema.description_summary.name(), datas.get(EXMLSchema.description_summary));
			m.put(EXMLSchema.description.name(), datas.get(EXMLSchema.description));
			r.put(lang, m);
		}
		return r;
	}

	public static Map<String, String> getDescriptionInfo(String selectedLang, PatentDataMaps dataMaps) {
		selectedLang = selectedLang.toLowerCase();
		Map<String, Map<String, String>> datas = getDescriptionInfoALL(dataMaps);
		if (datas.containsKey(selectedLang)) {
			return datas.get(selectedLang);
		}
		Set<String> ks = datas.keySet();
		for (String k : ks) {
			return datas.get(k);
		}
		return Collections.emptyMap();
	}

}
