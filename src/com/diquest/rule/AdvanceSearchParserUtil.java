/**
 * 
 */
package com.diquest.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.diquest.util.Utility;

/**
 * 고급검색식에서 필터 정보를 파싱한다.
 * 
 * @author neon
 * @date 2013. 7. 5.
 * @Version 1.0
 */
public class AdvanceSearchParserUtil {

	static Logger logger = LoggerFactory.getLogger(AdvanceSearchParserUtil.class);

	/**
	 * != 조건을 나타내는 필드.
	 */
	public static final String NOT_EXPRESSION = "@OP_NOT@";
	
	
	/**
	 * 고급 검색식에서 필터 항목만 추출한다.<br>
	 * 
	 * @author neon
	 * @date 2013. 7. 5.
	 * @param query
	 * @return
	 */
	public static List<String> getExtranctFilterItems(String query) {
		StringBuffer buf = new StringBuffer();
		char[] ca = query.toCharArray();
//		logger.debug("query {} ", query);
		List<String> stack = new ArrayList<String>();
//		Matcher m = filterRangePattern.matcher(query);
//		while(m.find()){
//			String filterQuery = m.group();
//			stack.add(filterQuery);
//			query.replaceAll(filterQuery, "");
//		}
		
		boolean startFilter = false;
		boolean endFilter = false;
		boolean openBrace = false;
		int dupBrace = 0;
		for (char c : ca) {
			switch (c) {
			case '@':
				// 필터에서 count, date정보에서 괄호없이 데이터를 넣어도 추출할 수 있게 해준다.
				if(buf.length()> 0){
					stack.add(buf.toString());
				}
				if(endFilter){
				}
				startFilter = true;
				buf.setLength(0);
				break;
			case ')':
				if (startFilter && openBrace && dupBrace > 0) {
					// 이미 열려있는데 또 있다면 애는 검색에 포함되는 괄호이다.
					buf.append(c);
					dupBrace -= 1;
					break;
				}
//				System.out.println(startFilter +"," + dupBrace +"," + openBrace +","+ buf +","+endFilter);
				if (startFilter && openBrace) {
					buf.append(c);
				}
				endFilter = true;
				openBrace = false;
				break;
			case '(':
				if (startFilter && openBrace) {
					// 이미 열려있는데 또 있다면 애는 검색에 포함되는 괄호이다.
					buf.append(c);
					dupBrace += 1;
					break;
				}
				if (startFilter) {
					openBrace = true;
					buf.append(c);
					endFilter = false;
				}
				break;
			case '<':
				buf.append(c);
				break;
			case '>':
				buf.append(c);
				break;
			case '=':
				buf.append(c);
				break;
			default:
				if (c == ' ' && !openBrace && startFilter) {
					String f = buf.toString().trim();
					buf.setLength(0);
					if (f.length() > 0) {
						stack.add(f);
					}
					endFilter = false;
					startFilter = false;
					break;
				}

				if (endFilter) {
					String filter = buf.toString().trim();
					if (filter.length() > 0) {
						stack.add(filter);
						buf.setLength(0);
					}
					endFilter = false;
					startFilter = false;
					break;
				}

				if (startFilter) {
					buf.append(c);
					endFilter = false;
				}

				break;
			}
		}
		if (buf.length() > 0) {
			stack.add(buf.toString());
		}
//		 logger.debug(stack.toString());
		return stack;
	}

	static final Pattern filterRangePattern = Pattern.compile("(\\([0-9a-zA-Z]{0,}-[0-9a-zA-Z]{0,}\\)\\.[a-zA-Z]{1,}\\.)");
	/**
	 * 고급 검색식에서 필터 항목을 제외하고 추출한다..<br>
	 * 
	 * @author neon
	 * @date 2013. 8. 9.
	 * @param query
	 * @return
	 */
	public static String removeFilterSearchRule(String query) {
		StringBuffer removeFilter = new StringBuffer();
		
//		Matcher m = filterRangePattern.matcher(query);
//		while(m.find()){
//			String filterQuery = m.group();
//			removeFilter.append(filterQuery);
//			removeFilter.append(" ");
//			logger.debug("remove filter query (range filter) {}", filterQuery);
//		}
//		
//		query = query.replaceAll("(\\([0-9a-zA-Z]{0,}-[0-9a-zA-Z]{0,}\\)\\.[a-zA-Z]{1,}\\.)", " ").replaceAll("\\(\\s{0,}\\)", " ");
//		String _query = query.replaceAll("\\)|\\(", "").replaceAll("\\s{1,}", "").trim();
//		System.out.println(_query);
//		if("".equals(_query)){
//			return removeFilter.toString();
//		}
		
		String[] _bquery = new String[]{query};
//		String[] _bquery = query.split(" ");
//		String[] _bquery = query.split("((\\.\\s{1,}))");
		for(String _qu : _bquery){
			logger.debug("query split {}", _qu);
			char[] ca = _qu.toCharArray();
			boolean startFilter = false;
			boolean endFilter = false;
			boolean openBrace = false;
			boolean startDoubleQuater= false;
			int dupBrace = 0;
			// int i = 0;
			for (char c : ca) {
				// i++;
				// logger.debug("{}, {}, {}, {}, {}", String.valueOf(c), startFilter
				// + "\t" + openBrace + "\t" + endFilter);
				switch (c) {
				case '@':
					// 필터에서 count, date정보에서 괄호없이 데이터를 넣어도 추출할 수 있게 해준다.
					startFilter = true;
					break;
//				case '"':
//					// 필터에서 count, date정보에서 괄호없이 데이터를 넣어도 추출할 수 있게 해준다.
//					if(startDoubleQuater){
//						startDoubleQuater = false;
//					}else{
//						startDoubleQuater = true;
//					}
//					break;
				case ')':
					if (openBrace && dupBrace > 0) {
						dupBrace -= 1;
						break;
					}
					endFilter = true;
//					System.out.println("CCCC " + openBrace);
					if (openBrace == false) {
						// 필터를 감싼 괄호가 아니므로 제거대상에 넣어야 한다.
						removeFilter.append(c);
//						System.out.println("AAAAAA");
						startFilter = false;
						break;
					}
					
					openBrace = false;
					if (!startFilter) {
						removeFilter.append(c);
					}
					break;
				case '(':
					if (openBrace) {
						// 이미 필터 괄호가 열려있는데 또 괄호가 출현하면 애는 검색 대상 괄호
						dupBrace += 1;
						break;
					}
					openBrace = true;
					if (startFilter) {
						endFilter = false;
//						System.out.println("BBBB " + openBrace);
					} else {
						openBrace = false;
						removeFilter.append(c);
					}
					break;
				case '<':
					break;
				case '>':
					break;
				case '=':
					break;
				default:
					if(startDoubleQuater){
						removeFilter.append(c);
						break;
					}
					
					if (c == ' ' && startFilter == false) {
						// 필터에 포함된 공백이 아니면 포함
						removeFilter.append(c);
					}
					
					if (c == ' ' && !openBrace && startFilter) {
						endFilter = false;
						if(!startDoubleQuater){
							startFilter = false;
						}
						break;
					}
					
					if (startFilter == false || endFilter == true) {
						// FILTER 검색 식이 아닌 것들 담는다.
						removeFilter.append(c);
					}
					
					if (endFilter) {
						endFilter = false;
						startFilter = false;
						break;
					}
					
					if (startFilter) {
						endFilter = false;
					}
					
					break;
				}
			}
			removeFilter.append(' ');
		}
		
		 logger.debug("============= remove Filter : {}", removeFilter.toString());
//		 return "((-20060819).apdate.)  AND  (2010).ay. ";
		return removeFilter.toString().trim();
	}

	/**
	 * 고급검색식에서 필터에 해당하는 내용만을 추출한다.<br>
	 * 
	 * @author neon
	 * @date 2013. 7. 5.
	 * @param advancedQuery
	 *            고급 검색식
	 * @return key : 필터 필드명 <br>
	 *         value : 필드 데이터 <br>
	 * 
	 */
	public static Map<String, String[]> getFilterData(String advancedQuery) {
		List<String> filterData = getExtranctFilterItems(advancedQuery);
		 logger.debug("src {} ", filterData.toString());
		Map<String, List<String>> tmp = new HashMap<String, List<String>>();
		/*
		 * key : 필드명, value : 원래 검색식.
		 */
		Map<String, List<String>> filterDataMap = new HashMap<String, List<String>>();
		for (String s : filterData) {
			String fieldName = getFilterFieldName(s);
			fieldName = FieldConversion.conversionField(fieldName);
//			 logger.debug("fieldName {} ", fieldName);
			// 원본 검색식을 저장한다. == START ==
			List<String> filterDataList = filterDataMap.get(fieldName);
			if (filterDataList == null) {
				filterDataList = new LinkedList<String>();
			}
			filterDataList.add(s);
			filterDataMap.put(fieldName, filterDataList);
			// 원본 검색식을 저장한다. == END ==

			List<String> datas = getFilteringData(s, fieldName);
			if (datas.size() == 0)
				continue;
//			logger.debug("datas : {} ", datas.toString());
			List<String> tDatas = tmp.get(fieldName);
			if (tDatas == null) {
				tDatas = new LinkedList<String>();
			}

			String srcValue = "";
			if (checkDateField(fieldName)) {
				List<String> opList = getOp(s);
//				System.out.println("filter @" + s + " , " + fieldName + "\t" + datas + "\t" + opList);
				if(opList.size() == 1){
					String op = opList.get(0);
					srcValue = datas.get(0);
					LinkedList<String> tmpData = new LinkedList<String>();
					if(op.equals("<=")){
						tmpData.add(0, "19600101");
						tmpData.add(1, datas.get(0));
						datas = tmpData;
					}else if(op.equals("<")){
						tmpData.add(0, "19600101");
						tmpData.add(1, Utility.getMinusDateConvertor(datas.get(0), -1));
						Utility.getDateConvertor(datas.get(0));
						datas = tmpData;
					}else if(op.equals(">")){
						tmpData.add(0, Utility.getMinusDateConvertor(datas.get(0), 1));
						tmpData.add(1, Utility.getConvertCurrentDate());
						datas = tmpData;
					}else if(op.equals(">=")){
						tmpData.add(0, datas.get(0));
						tmpData.add(1, Utility.getConvertCurrentDate());
						datas = tmpData;
					}else if(op.equals("=")){
						tmpData.add(0, datas.get(0));
						tmpData.add(1, datas.get(0));
						datas = tmpData;
					}
				}
			} else if (checkCountField(fieldName)) {
				// 개수 필드일때 > < 일때 정보를 증감해 주어야 한다.
				// OPList는 최대 두개이다.
				List<String> opList = getOp(s);
				if (opList.size() == 1) {
					String op = opList.get(0);
					srcValue = datas.get(0).trim();
					String v = "";
//					logger.debug("갯수 필드 OP:{}, srcValue:{}",op, srcValue);
					if (op.equals(">")) {
						try {
							int value = Integer.parseInt(srcValue) + 1;
							v = String.valueOf(value);
						} catch (NumberFormatException nfe) {
							nfe.printStackTrace();
						}
						datas.set(0, v);
						datas.add(1, "");
					} else if (op.equals(">=")) {
						try {
							int value = Integer.parseInt(srcValue);
							v = String.valueOf(value);
						} catch (NumberFormatException nfe) {
						}
						datas.add(0, v);
						datas.set(1, "");
					}else if (op.equals("<")) {
						try {
							int value = Integer.parseInt(srcValue) - 1;
							v = String.valueOf(value);
						} catch (NumberFormatException nfe) {
						}
						datas.set(0, String.valueOf(0));
						datas.add(1, v);
					} else if (op.equals("<=")) {
						try {
							int value = Integer.parseInt(srcValue);
							v = String.valueOf(value);
						} catch (NumberFormatException nfe) {
						}
						datas.set(0, String.valueOf(0));
						datas.add(1, v);
					}else if (op.equals("=")) {
						datas.set(0, srcValue);
						datas.add(1, srcValue);
					}
				} else {
					// System.out.println(opList);
					// System.out.println(datas);
					for (int idx = 0; idx < opList.size(); idx++) {
						String op = opList.get(idx);
						srcValue = datas.get(idx).trim();
						String v = "";
						if (op.equals(">")) {
							try {
								int value = Integer.parseInt(srcValue) + 1;
								v = String.valueOf(value);
							} catch (NumberFormatException nfe) {
							}
//							System.out.println("==>" + v);
							datas.set(idx, v);
						}
						if (op.equals("<")) {
							try {
								int value = Integer.parseInt(srcValue) - 1;
								v = String.valueOf(value);
							} catch (NumberFormatException nfe) {
							}
							datas.set(idx, v);
						}
					}
				}
			}
			for (String d : datas) {
				tDatas.add(d);
			}
//			System.out.println(tDatas);
			tmp.put(fieldName, tDatas);
		}
		Set<String> keySet = tmp.keySet();
		Map<String, String[]> result = new HashMap<String, String[]>();
		for (String key : keySet) {
			List<String> fieldData = tmp.get(key);
			List<String> srcFilter = filterDataMap.get(key);

			String[] v = new String[fieldData.size()];
			for (int idx = 0; idx < v.length; idx++) {
				String sourceFilter = "";
				if (srcFilter.size() > idx) {
					sourceFilter = srcFilter.get(idx);
				}
				String data = "";
				if (sourceFilter != null) {
					if (sourceFilter.indexOf("!=") != -1) {
						// NOT 조건이 포함되어 있다면 해당 표현식을 필드 데이터에 붙이고 이를 검색식 만들때 참고한다.
						data = NOT_EXPRESSION;
					}
				}
				v[idx] = data + fieldData.get(idx);
//				 logger.debug("{}, value:{}", key, v[idx]);
			}
			if (v.length != 0) {
				result.put(key, v);
			}
		}
		return result;
	}

	/**
	 * 일반 문자 검색용 필터 추출 패턴
	 */
	static final Pattern _stringPattern = Pattern.compile("(?<=\\().+(?=\\))");
	static final Pattern _numberPattern = Pattern.compile("(?<=\\().*?(?=\\))");

	private static List<String> getFilteringData(String query, String fieldName) {
		Pattern p = _stringPattern;
		LinkedList<String> fieldDatas = new LinkedList<String>();
		// logger.debug("filtering query : {}", query);
		// logger.debug("filtering fieldName : {}", fieldName);
		if (query.indexOf("year") != -1 || query.indexOf("date") != -1 || query.indexOf("cnt") != -1
				|| query.indexOf("count") != -1) {
			p = _numberPattern;
			if (query.indexOf("(") == -1 && query.indexOf(")") == -1) {
				// 괄호가 없어도 날짜 ,카운트정보에서 데이터를 추출해준다.
				query = query.replaceAll(fieldName, "");
				char[] ca = query.toCharArray();
				StringBuilder sb = new StringBuilder();
				boolean checkOp = false;
				for (char c : ca) {
					if (c == '>' || c == '=' || c == '<') {
						checkOp = true;
						continue;
					} else {
						if (checkOp) {
							if (sb.length() > 0) {
								// System.out.println("put : " + sb);
								fieldDatas.add(sb.toString());
								sb.setLength(0);
							}
							checkOp = false;
						}
						sb.append(c);
						checkOp = false;
					}
				}
				if (sb.length() > 0) {
					fieldDatas.add(sb.toString());
				}
			}
		}

		if (fieldDatas.size() == 0) {
			Matcher m = p.matcher(query);
			while (m.find()) {
				String fieldData = m.group();
				// System.out.println(query + " 정규 추출 " + fieldData);
				fieldDatas.add(fieldData);
			}
		}

		List<String> s = new LinkedList<String>();
		int cnt = 0;
		for (String f : fieldDatas) {
			s.add(cnt++, f);
		}
		if (checkDateField(fieldName)) {
			if (cnt == 1) {
				s.add(cnt, "");
			}
		}
		return s;
	}

	/**
	 * 날짜 필드인가?
	 * 
	 * @author neon
	 * @date 2013. 7. 5.
	 * @param fieldName
	 * @return
	 */
	private static boolean checkDateField(String fieldName) {
		if (fieldName.indexOf("date") != -1) {
			return true;
		}
		return false;
	}

	/**
	 * 카운트 필드인가?
	 * 
	 * @author neon
	 * @date 2013. 7. 5.
	 * @param fieldName
	 * @return
	 */
	private static boolean checkCountField(String fieldName) {
		if (fieldName.indexOf("cnt") != -1 || fieldName.indexOf("count") != -1) {
			return true;
		}
		return false;
	}

	private static String getFilterFieldName(String query) {
		char[] ca = query.toCharArray();
		StringBuilder sb = new StringBuilder();
		for (char c : ca) {
			if (checkStartField(c)) {
				break;
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * 입력받은 쿼리에서 연산기호만을 추출한다.<br>
	 * 한개 이상의 검색식에서 순차적으로 추출한 연산기호를 리턴한다. <br>
	 * 
	 * @author neon
	 * @date 2013. 7. 9.
	 * @param query
	 * @return
	 */
	public static List<String> getOp(String query) {
		char[] ca = query.toCharArray();
		StringBuilder sb = new StringBuilder();
		List<String> opList = new LinkedList<String>();
		if (query.indexOf("(") == -1 && query.indexOf(")") == -1) {
			for (char c : ca) {
				if (checkStartField(c)) {
					sb.append(c);
				} else {
					String s = sb.toString().trim();
					if (!"".equals(s)) {
						opList.add(s);
						sb.setLength(0);
					}
				}
			}
		} else {
			for (char c : ca) {
				if (checkStartField(c)) {
					sb.append(c);
				} else if (c == '(' || c == ')') {
					String s = sb.toString().trim();
					if (!"".equals(s)) {
						opList.add(s);
						sb.setLength(0);
					}
				}
			}
		}
		return opList;
	}

	/**
	 * 필드명을 추출하기 위한 체크섬.<br>
	 * 고급검색식에서 = > < ! 은 반드시 필드명 다음에 나타나는 문자열이다.<ㅠㄱ>
	 * 
	 * @author neon
	 * @date 2013. 7. 9.
	 * @param c
	 *            체크하고자 하는 캐릭터
	 * @return
	 */
	private static boolean checkStartField(char c) {
		if (c == '=' || c == '>' || c == '<' || c == '!') {
			return true;
		}
		return false;
	}

}
