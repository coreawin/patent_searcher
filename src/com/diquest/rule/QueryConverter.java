package com.diquest.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.diquest.ir.common.msg.protocol.Protocol;
import com.diquest.ir.common.msg.protocol.query.FilterSet;
import com.diquest.ir.common.msg.protocol.query.WhereSet;
import com.diquest.util.EDQDocField;
import com.diquest.util.Utility;

/**
 * 쿼리 식을 변환하는 클래스 <br>
 * 고급 검색식을 DQ 식으로 변환한다.<br>
 * 
 * @author neon
 * @date 2013. 8. 9.
 * @Version 1.0
 */
public class QueryConverter  {

	/**
	 * 필드간 검색 방법 <br>
	 * (*).ti.abs. 인 경우 기본적으로 필드간은 OP_OR로 검색한다.
	 */
	private byte FIELD_OP = Protocol.WhereSet.OP_OR;

	Logger logger = LoggerFactory.getLogger(getClass());

	String advancedQuery;

	/**
	 * WhereSet 조건이 먼저 생성되었는가?
	 */
	boolean executeMakeWhere = false;

	/**
	 * 검색조건 (app, inv)인 경우 검색식으로 들어오면 Filter or 식으로 변환한다.<br>
	 * 2013-09-09
	 */
	LinkedList<List<String>> appWhereSetFilter = new LinkedList<List<String>>();
	LinkedList<List<String>> invWhereSetFilter = new LinkedList<List<String>>();

	LinkedList<FilterSet> filterSetList;
	LinkedList<WhereSet> whereSetList;

	Set<String> searchTerms;
	Set<String> wildCardSearchTerms;
	Set<EDQDocField> highlightField;

	private WhereSet[] whereSetResult = null;
	private FilterSet[] filterSetResult = null;

	List<String> filterQueries = new LinkedList<String>();
	Map<String, Set<String>> highlightSearchTerms = new HashMap<String, Set<String>>();
	Map<String, Set<String>> highlightWildCardTerms = new HashMap<String, Set<String>>();

	private boolean isKpass = false;

	private String kehome;

//	EJianaUtil util = null;

	/**
	 * 변환식을 위한 인스턴스를 생성한다.
	 * 
	 * @param advancedQuery
	 *            고급 검색식
	 */
	public QueryConverter(String advancedQuery) {
		this.wildCardSearchTerms = new LinkedHashSet<String>();
		this.searchTerms = new LinkedHashSet<String>();
		this.highlightField = new LinkedHashSet<EDQDocField>();
		this.advancedQuery = queryRefiner(advancedQuery);
		appWhereSetFilter.clear();
		invWhereSetFilter.clear();
//		util = EJianaUtil.getInstance();
		logger.debug("input query : {} ", advancedQuery);
		this.whereSetResult = makeWhereSet();
		logger.debug("refin query : {} ", this.advancedQuery);
		this.filterSetResult = makeFiterSet();
		// this.filterSetResult = makeFilterSets(queryInfo);
	}

	/**
	 * 변환식을 위한 인스턴스를 생성한다.
	 * 
	 * @param advancedQuery
	 *            고급 검색식
	 */
	public QueryConverter(String advancedQuery, boolean isKpass) {
		this.isKpass = isKpass;
		this.wildCardSearchTerms = new LinkedHashSet<String>();
		this.highlightField = new LinkedHashSet<EDQDocField>();
		this.searchTerms = new LinkedHashSet<String>();
		this.advancedQuery = queryRefiner(advancedQuery);
		appWhereSetFilter.clear();
		invWhereSetFilter.clear();
//		util = EJianaUtil.getInstance();
		logger.debug("input query : {} ", advancedQuery);
		this.whereSetResult = makeWhereSet();
		logger.debug("refin query : {} ", this.advancedQuery);
		this.filterSetResult = makeFiterSet();
		// this.filterSetResult = makeFilterSets(queryInfo);
	}

	/**
	 * 변환식을 위한 인스턴스를 생성한다.
	 * 
	 * @param advancedQuery
	 *            고급 검색식
	 */
	public QueryConverter(String advancedQuery, String kehome, boolean isKpass) {
		this.kehome = kehome;
		this.isKpass = isKpass;
		this.wildCardSearchTerms = new LinkedHashSet<String>();
		this.highlightField = new LinkedHashSet<EDQDocField>();
		this.searchTerms = new LinkedHashSet<String>();
		this.advancedQuery = queryRefiner(advancedQuery);
		appWhereSetFilter.clear();
		invWhereSetFilter.clear();
		logger.debug("input query : {} ", advancedQuery);
//		util = EJianaUtil.getInstance(kehome);
		this.whereSetResult = makeWhereSet();
		logger.debug("refin query : {} ", this.advancedQuery);
		this.filterSetResult = makeFiterSet();
		// this.filterSetResult = makeFilterSets(queryInfo);
	}

	/**
	 * 정제된 검색식을 리턴한다.
	 * 
	 * @author neon
	 * @date 2013. 8. 14.
	 * @return
	 */
	public String getRefineQuery() {
		return this.advancedQuery;
	}

	static final String REGX1 = "\\.[\\s]{1,}\\)";
	static final String REGX2 = "\\.[\\s]{1,}\\(";
	/**
	 * 두개 이상의 빈 문자열을 찾는 정규식
	 */
	public static final String REGX3 = "[\\s]{1,}";
	/**
	 * 캐리지 리턴 문자열을 찾는 정규식
	 */
	public static final String REGX4 = "[\r\n]";

	/**
	 * 필드가 아닌 검색어 부분에 필드 구분자가 입력되어 있는 경우 제거한다.
	 */
	public static final String REGX5 = "\\.[\\s]{1,}(?![\\(\\)@(OR)])";

	/**
	 * 검색식에서 OPEN 괄호 연산자 사이에 공백이 없게 한다.<br>
	 * 2014-05-28
	 */
	public static final String REGX6 = "(\\(\\s{1,}\\()";
	/**
	 * 검색식에서 CLOSE 괄호 연산자 사이에 공백이 없게 한다.<br>
	 * 2014-05-28
	 */
	public static final String REGX7 = "(\\)\\s{1,}\\))";
	/**
	 * 검색식에서 () 형태의 빈 괄호 공백이 없게 한다.<br>
	 * 2016-11-14
	 */
	public static final String REGX8 = "(\\(\\s{0,}\\))";

	/**
	 * 검색식을 정리한다.<br>
	 * 
	 * @author neon
	 * @date 2013. 8. 14.
	 * @return
	 */
	private static String queryRefiner(String query) {
		return query.replaceAll(REGX4, " ").replaceAll(REGX1, ".\\)").replaceAll(REGX2, ". (").replaceAll(REGX3, " ").replaceAll(REGX6, "((")
				.replaceAll(REGX7, "))").replaceAll(REGX8, " ");
	}

	/**
	 * 검색용 필드명을 사용자 정의 쿼리명으로 교체한 후 리턴한다.<br>
	 * 
	 * @author neon
	 * @date 2013. 9. 13.
	 * @return
	 */
	public static String queryConversion(String advancedQuery) {
		Map<String, String> rfield = FieldConversion._CUSTOM_FIELD_INFO_REVERSE;
		Set<Entry<String, String>> es = rfield.entrySet();
		String q = queryRefiner(advancedQuery);
		for (Entry<String, String> e : es) {
			q = q.replaceAll("(?<=\\.)(" + e.getKey() + ")(?=\\.)", e.getValue());
		}
		return q;
	}

	/**
	 * 검색용 필드명을 사용자 정의 쿼리명으로 교체한 후 리턴한다.<br>
	 * 
	 * @author neon
	 * @date 2013. 9. 13.
	 * @return
	 */
	public String queryConversion() {
		Map<String, String> rfield = FieldConversion._CUSTOM_FIELD_INFO_REVERSE;
		Set<Entry<String, String>> es = rfield.entrySet();
		String q = this.advancedQuery;
		for (Entry<String, String> e : es) {
			q = q.replaceAll("(?<=\\.)(" + e.getKey() + ")", e.getValue());
		}
		return q;
	}

	/**
	 * 고급 검색식을 DQ의 WhereSet으로 변환한다.
	 * 
	 * @author neon
	 * @date 2013. 8. 9.
	 * @param advancedQuery
	 *            고급 검색식
	 * @return
	 */
	private LinkedList<WhereSet> getWhereSetList() {
		String q = AdvanceSearchParserUtil.removeFilterSearchRule(this.advancedQuery);
		logger.debug("getWhereSetList input query : {} ", q);
		q = queryRefiner(q);
		logger.debug("getWhereSetList reiner query : {} ", q);
		// return null;

		LinkedList<WhereSet> result = new LinkedList<WhereSet>();
		if (q.indexOf("(") == -1 || q.indexOf(")") == -1 || q.indexOf(".") == -1) {
			// whereset 식이 포함된 것이다.
			FilterSet[] f = makeFilterSet();
			for (FilterSet _f : f) {
				logger.debug("{}", _f);
			}
			if (f.length > 0) {
				// 필터식만 있을경우 이 식으로 대체한다.<br>
				result.add(new WhereSet("IDX_ALL", Protocol.WhereSet.OP_HASALL, "Y", 150));
			}
			return result;
		}

		char[] charArr = q.toCharArray();
		char prev = ' ';
		StringBuilder sb = new StringBuilder();
		boolean isFieldDelim = false;
		boolean startDoubleQuater = false;
		String query = "";
		for (char cur : charArr) {
			if (prev == '(' && cur == ')') {
				sb.deleteCharAt(sb.length() - 1);
				continue;
			}
			sb.append(cur);
			if (cur == '"') {
				// 기관명은 기본적으로 "로 감싸져서 들어온다.
				if (startDoubleQuater) {
					startDoubleQuater = false;
				} else {
					startDoubleQuater = true;
				}
			}

			if ((prev == '.' || prev == ')') && cur == ' ' && isFieldDelim) {
				if (!startDoubleQuater) {
					// logger.debug("test 1 {}", sb);
					if (result.size() > 0) {
					}
					query = sb.toString();
					List<String>[] queryArray = getDivFilterWhereQuery(query);
					this.filterQueries.addAll(queryArray[0]);
					List<String> queryList = queryArray[1];

					if (queryArray[0].size() == 0) {
						logger.debug("test1 ===> " + query);
						refineWhereSetList(result, query);
					} else {
						for (String qr : queryList) {
							logger.debug("QR : {}", 1);
							refineWhereSetList(result, qr);
						}
					}
					sb.setLength(0);
					isFieldDelim = false;
				}
			}
			if (cur == '.') {
				isFieldDelim = true;
			}
			prev = cur;
		}
		query = sb.toString();
		logger.debug("getWhereSetListn 2 input query : {} ", query);
		// logger.debug("test 2 {}", query);
		List<String>[] queryArray = getDivFilterWhereQuery(query);
		this.filterQueries.addAll(queryArray[0]);
		List<String> queryList = queryArray[1];
		/* 동일 쿼리 중복 문제 */
		// else {

		if (queryArray[0].size() == 0) {
			logger.debug("test1 ===> 2" + query);
			refineWhereSetList(result, query);
		} else {
			for (String qr : queryList) {
				logger.debug("QR : {}", qr);
				refineWhereSetList(result, qr);
			}
		}

		if (result.size() > 0) {
			byte fop = result.getFirst().getOperation();
			if (fop == ((byte) Protocol.WhereSet.OP_AND) || fop == ((byte) Protocol.WhereSet.OP_OR)) {
				result.removeFirst();
			}
		}
		// logger.debug("test {} ",result);
		return result;
	}

	/**
	 * @author coreawin
	 * @date 2014. 7. 2.
	 * @param wslist
	 * @param qr
	 * @return
	 */
	private void refineWhereSetList(LinkedList<WhereSet> wslist, String qr) {

		List<WhereSet> whereSetList = convertBasisRuleWhereSet(qr);
		if (whereSetList.size() == 0)
			return;
		logger.debug("input query : {} --- {}", qr, whereSetList);

		if (wslist.size() > 0) {
			WhereSet ws = whereSetList.get(0);
			byte os = ws.getOperation();
			switch (os) {
			case Protocol.WhereSet.OP_AND:
				break;
			case Protocol.WhereSet.OP_OR:
				break;
			case Protocol.WhereSet.OP_NOT:
				break;
			default:
				wslist.add(new WhereSet(Protocol.WhereSet.OP_AND));
				break;
			}
		}
		wslist.addAll(whereSetList);
	}

	static Pattern queryPattern = Pattern.compile("(?<=\\()[^()]*.(?=\\))");
	public static Pattern fieldPattern = Pattern.compile("(?<=\\.)([a-zA-Z_0-9]{1,})");
	static Pattern phrasePattern = Pattern.compile("(?<=\")[^\"\"]*(?=\")");

	StringBuffer stBuf = new StringBuffer();
	StringBuilder strBuf = new StringBuilder();

	/**
	 * 검색식 최하위 단계의 식을 파싱한다.<br>
	 * 식 패턴은 (blu~ blu~ OR (blu) AND (blu)).field.field.
	 * 
	 * @author neon
	 * @date 2013. 8. 9.
	 * @param query
	 * @param prevList
	 *            앞에 연산자
	 */
	private List<WhereSet> makeWhereSet(String query, List<String> prevList) {
		query = queryRefiner(query);
//		 logger.debug("makeWhereSet query : {} ", query);
		if (query.length() < 1)
			return Collections.EMPTY_LIST;

		Set<String> field = new HashSet<String>();

		String fieldInfo = query.substring(query.lastIndexOf(")") + 1, query.length());
//		 logger.info("query : {}, fieldInfo : {}", query, fieldInfo);
		Matcher m = fieldPattern.matcher(fieldInfo);
		while (m.find()) {
			String f = m.group().trim().toLowerCase();
			f = FieldConversion.conversionField(f, isKpass);
			field.add(f);
		}

		String q = query.substring(1, query.lastIndexOf(")."));
		List<String> l = searchRuleExtract(q);

		boolean isNotFilter = false;
		if (prevList.contains("NOT")) {
			isNotFilter = true;
		}
		if (isNotFilter) {
			List<String> bl = new ArrayList<String>(l.size());
			for (String a : l) {
				bl.add(AdvanceSearchParserUtil.NOT_EXPRESSION + a);
			}
			l = bl;
		}
		if (field.contains("app")) {
			// logger.debug("---add appWhereSetFilter---------- {} : {}",
			// appWhereSetFilter, field +"\t"+ l);
			this.appWhereSetFilter.add(new ArrayList<String>(l));
		}
		if (field.contains("inv")) {
			// logger.debug("---add invWhereSetFilter---------- {} : {}",
			// appWhereSetFilter, field +"\t"+ l);
			this.invWhereSetFilter.add(new ArrayList<String>(l));
		}
		List<WhereSet> resultList = makeWhereSetList(l, field);
		// logger.debug("makeWhereSet result : {}", resultList);
		return resultList;
	}

	List<String> termList = new LinkedList<String>();

	/**
	 * 하위 검색식에서 검색 텀과 괄호를 구분한다.
	 * 
	 * @author neon
	 * @date 2013. 8. 12.
	 * @param q
	 * @return
	 */
	private List<String> searchRuleExtract(String q) {
		char[] ca = q.toCharArray();
		// logger.debug("searchRuleExtract {}", q);
		termList.clear();
		boolean openBrace = false;
		boolean reOpenBrace = false;
		boolean reCloseBrace = false;
		boolean closeBrace = false;
		boolean dquaStart = false;
		boolean dquaEnd = false;
		int openBraceCnt = 0;
		char prevC = ' ';
		for (char c : ca) {
			switch (c) {
			case '(':
				// if(openBrace){
				// termList.add("(");
				// reOpenBrace = true;
				// break;
				// }
				openBrace = true;
				if (dquaStart) {
					// 인접용 검색어에 괄호가 포함될 수 있다. 2013.09.09
					stBuf.append(c);
					break;
				}

				if (openBrace) {
					/* 이하 한줄 하나의 필드 안에도 괄호로 연산될 수 있다 2013.10.14 */
					termList.add("(");

					// stBuf.append(c);
					openBraceCnt += 1;
					break;
				}
				termList.add("(");
				stBuf.setLength(0);
				break;
			case ')':
				closeBrace = true;
				if (dquaStart) {
					// 인접용 검색어에 괄호가 포함될 수 있다. 2013.09.09
					stBuf.append(c);
					break;
				}
				if (openBrace && openBraceCnt > 0) {
					// stBuf.append(c);
					/* 이하 세줄 하나의 필드 안에도 괄호로 연산될 수 있다 2013.10.14 */
					termList.add(stBuf.toString());
					stBuf.setLength(0);
					termList.add(")");

					openBraceCnt -= 1;
					break;
				}
				openBrace = false;
				termList.add(stBuf.toString());
				stBuf.setLength(0);
				termList.add(")");
				break;
			case '"':
				if (dquaStart) {
					dquaStart = false;
					dquaEnd = true;
					String _t = stBuf.toString();
					if ("and".equalsIgnoreCase(_t) || "or".equalsIgnoreCase(_t) || "not".equalsIgnoreCase(_t)) {
						// 연산자가 더블쿼터로 감싸져 있다면 이 연산자 자체를 검색하도록 한다.
						stBuf.setLength(0);
						stBuf.append("\"");
						stBuf.append(_t);
						stBuf.append("\"");
					}
				} else {
					dquaStart = true;
					dquaEnd = false;
				}
				break;
			default:
				stBuf.append(c);
				if (dquaEnd) {
					termList.add(stBuf.toString().trim());
					stBuf.setLength(0);
					dquaEnd = false;
					break;
				}

				if ((c == ' ' && prevC != ' ') && !dquaStart) {
					termList.add(stBuf.toString().trim());
					stBuf.setLength(0);
					break;
				}
				break;
			}
			prevC = c;
		}

		termList.add(stBuf.toString().trim());
		// logger.debug(termList.toString());
		return termList;
	}

	/**
	 * 번호 형태로 들어오는 항목에 대해서는 추가적으로 쿼리를 다르게 변환한다.<br>
	 * pno, apo, prino, au, mfpno, cfpno, fcpno, bcpno <br>
	 * 
	 * @author coreawin
	 * @date 2014. 7. 3.
	 * @return
	 */
	private List<WhereSet> makeWhereSetNumberField(List<String> terms, String field) {
		List<WhereSet> result = new LinkedList<WhereSet>();
		if (field.equalsIgnoreCase("pn") || field.equalsIgnoreCase("pno") || field.equalsIgnoreCase("apo") || field.equalsIgnoreCase("prino")
				|| field.equalsIgnoreCase("mfpno") || field.equalsIgnoreCase("cfpno") || field.equalsIgnoreCase("fcpno")
				|| field.equalsIgnoreCase("bcpno")) {
			List<String> l = new LinkedList<String>();
			_buf.setLength(0);
			for (String t : terms) {
				if (t.contains(AdvanceSearchParserUtil.NOT_EXPRESSION)) {
					t = t.replaceAll(AdvanceSearchParserUtil.NOT_EXPRESSION, "");
				}

				if ("and".equalsIgnoreCase(t)) {
					l.add(_buf.toString());
					l.add("and");
					_buf.setLength(0);
					continue;
				} else if ("not".equalsIgnoreCase(t)) {
					l.add(_buf.toString());
					l.add("not");
					_buf.setLength(0);
					continue;
				} else if ("or".equalsIgnoreCase(t)) {
					continue;
				}
				_buf.append(t);
				_buf.append(" ");
			}
			if (_buf.length() > 0) {
				l.add(_buf.toString());
			}
			logger.debug("number : query {} ", l);
			if (l.size() > 0) {
				// result.add(new WhereSet(Protocol.WhereSet.OP_BRACE_OPEN));
			}
			for (String st : l) {
				if ("".equals(st)) {
					continue;
				} else if (st.equalsIgnoreCase("AND")) {
					result.add(new WhereSet(Protocol.WhereSet.OP_AND));
					continue;
				} else if (st.equalsIgnoreCase("NOT")) {
					result.add(new WhereSet(Protocol.WhereSet.OP_NOT));
					continue;
				}

				byte op = Protocol.WhereSet.OP_HASALL;
				if (st.contains("*")) {
					op = Protocol.WhereSet.OP_HASANY;
					st = st.replaceAll("[*]", "");
				}
				EDQDocField efield = EDQDocField.valueOf(field.toLowerCase());
				result.add(new WhereSet(efield.getConvertIndexField(), op, st.replaceAll("\"", ""), 150));
			}
			if (l.size() > 0) {
				// result.add(new WhereSet(Protocol.WhereSet.OP_BRACE_CLOSE));
			}
		}
		return result;
	}

	StringBuffer _buf = new StringBuffer();

	private List<WhereSet> makeWhereSetList(List<String> l, Set<String> fieldSet) {
		List<WhereSet> result = new LinkedList<WhereSet>();
		WhereSet ws = null;
		// logger.debug("input term {} ", l);
		boolean isNot = false;

		if (fieldSet != null) {
			Set<String> removeField = new HashSet<String>();
			for (String field : fieldSet) {
				List<WhereSet> _r = makeWhereSetNumberField(l, field);
				if (_r.size() > 0) {
					removeField.add(field);
					if (result.size() > 0) {
						result.add(new WhereSet(Protocol.WhereSet.OP_OR));
					}
					result.addAll(_r);
				}
			}
			fieldSet.removeAll(removeField);
			if (fieldSet.size() == 0) {
				return result;
			}
		}
		for (String st : l) {
			// logger.debug("search term {} : {}, ", st, fieldSet);
			if (st.contains(AdvanceSearchParserUtil.NOT_EXPRESSION)) {
				isNot = true;
				st = st.replaceAll(AdvanceSearchParserUtil.NOT_EXPRESSION, "");
			}
			// logger.debug("search term {} : {}, ", st, fieldSet);
			if (result.size() > 0) {
				ws = result.get(result.size() - 1);
			}
			if ("".equals(st)) {
				continue;
			} else if (st.equalsIgnoreCase("OR")) {
				result.add(new WhereSet(Protocol.WhereSet.OP_OR));
			} else if (st.equalsIgnoreCase("AND")) {
				result.add(new WhereSet(Protocol.WhereSet.OP_AND));
			} else if (st.equalsIgnoreCase("NOT")) {
				if (result.size() < 1) {
					result.add(new WhereSet("IDX_ALL", Protocol.WhereSet.OP_HASALL, "y", 150));
				}
				result.add(new WhereSet(Protocol.WhereSet.OP_NOT));
			} else if (st.equals("(")) {
				if (ws != null) {
					if (ws.getOperation() == Protocol.WhereSet.OP_BRACE_CLOSE) {
						result.add(new WhereSet(Protocol.WhereSet.OP_AND));
					} else if (ws.getOperation() == Protocol.WhereSet.OP_HASALL || ws.getOperation() == Protocol.WhereSet.OP_HASANY) {
						result.add(new WhereSet(Protocol.WhereSet.OP_AND));
					}
				}
				result.add(new WhereSet(Protocol.WhereSet.OP_BRACE_OPEN));
			} else if (st.equals(")")) {
				result.add(new WhereSet(Protocol.WhereSet.OP_BRACE_CLOSE));
			} else {
				if (fieldSet != null) {
					int fieldSize = fieldSet.size();
					if (ws != null) {
						if (ws.getOperation() == Protocol.WhereSet.OP_BRACE_CLOSE) {
							result.add(new WhereSet(Protocol.WhereSet.OP_AND));
						}
					}
					if (fieldSize > 1) {
						// 2개 이상의 필드인 경우 필드간은 괄호로 감싸야 한다.
						result.add(new WhereSet(Protocol.WhereSet.OP_BRACE_OPEN));
					}
					int fieldCnt = 1;
					if (fieldSize == 1) {
						// field가 하나라면
						if (ws != null) {
							if (ws.getOperation() == Protocol.WhereSet.OP_HASALL || ws.getOperation() == Protocol.WhereSet.OP_HASANY) {
								result.add(new WhereSet(Protocol.WhereSet.OP_AND));
							}
						}
					}
					for (String f : fieldSet) {
						// logger.debug("field {}, sentense {} ", f, st);
						EDQDocField efield = EDQDocField.valueOf(f.toLowerCase());
						/** 제목, 초록, 영문 제목에서 검색어 하이라이팅을 위한 검색어 추출 - 2016-09-23 */
						String refineSt = st.trim();
//						if (!st.contains(" ")) {
//							if (!st.contains("-") || st.contains("*")) {
//								switch (efield) {
//								case au:
//								case apcn:
//								case pricn:
//								case inv_cn:
//								case app_cn:
//								case apfcn:
//								case publ_type:
//									break;
//								default:
//									String _lp = util.getStem(refineSt);
//									if (!"".equals(refineSt)) {
//										if (refineSt.equalsIgnoreCase("(she)") | refineSt.equalsIgnoreCase("(a)")
//												| refineSt.equalsIgnoreCase("(the)")) {
//										} else {
//											if ("".equals(_lp.trim())) {
//												continue;
//											}
//											st = _lp;
//										}
//									}
//								}
//							} else {
//								st = st.replaceAll("\\-", " ");
//							}
//						}

						setSearchTerm(efield, st);
						byte op = Protocol.WhereSet.OP_HASALL;
						// if (isNot) {
						// op = Protocol.WhereSet.OP_NOT;
						// }
						if (st.contains(" ")) {
							// 인접 검색을 할 수 있도록 변환해 준다.
							switch (efield) {
							case abs:
								// if (!isNot) {
								// op = Protocol.WhereSet.OP_HASALL;
								// }
								// result.add(new
								// WhereSet(EDQDocField.abs_e.getConvertIndexField(),
								// op, st
								// .replaceAll("-", " "), 150));

								if (st.contains("*")) {
									if (isKpass) {
										/** KPASS 검색 - 초록 인접검색 2016-09-23 */
										result.addAll(withinSearchExceptWildCard(new EDQDocField[] { EDQDocField.abs },
												new EDQDocField[] { EDQDocField.abs_n }, st.replaceAll("-", " ")));
									} else {
										result.addAll(withinSearchExceptWildCard(new EDQDocField[] { EDQDocField.abs_p },
												new EDQDocField[] { EDQDocField.abs_en }, st.replaceAll("-", " ")));
									}
								} else {
									/** 인접검색 넣는다. 2016.03.15 */
									if (isKpass) {
										/** KPASS 검색 - 초록 인접검색 2016-09-23 */
										result.addAll(withinSearch(EDQDocField.abs_n, st.replaceAll("-", " ")));
									} else {
										result.addAll(withinSearch(EDQDocField.abs_en, st.replaceAll("-", " ")));
									}
								}
								break;
							case ti:
								// if (!isNot) {
								// op = Protocol.WhereSet.OP_HASALL;
								// }
								// result.add(new
								// WhereSet(EDQDocField.ti_e.getConvertIndexField(),
								// op,
								// st.replaceAll("-", " "), 150));
								/** 인접검색 넣는다. 2016.03.15 */
								/** KPASS 인접검색 필드 추가에 따른 KPASS 구분 조건 추가 */
								if (st.contains("*")) {
									if (isKpass) {
										/** KPASS 검색 - 초록 인접검색 2016-09-23 */
										result.addAll(withinSearchExceptWildCard(new EDQDocField[] { EDQDocField.ti },
												new EDQDocField[] { EDQDocField.ti_n }, st.replaceAll("-", " ")));
									} else {
										result.addAll(withinSearchExceptWildCard(new EDQDocField[] { EDQDocField.ti_p },
												new EDQDocField[] { EDQDocField.ti_en }, st.replaceAll("-", " ")));
									}

								} else {
									/** 인접검색 넣는다. 2016.03.15 */
									if (isKpass) {
										/** KPASS 검색 - 초록 인접검색 2016-09-23 */
										result.addAll(withinSearch(EDQDocField.ti_n, st.replaceAll("-", " ")));
									} else {
										result.addAll(withinSearch(EDQDocField.ti_en, st.replaceAll("-", " ")));
									}
								}
								break;
							case tie:
								// if (!isNot) {
								// op = Protocol.WhereSet.OP_HASALL;
								// }
								// result.add(new
								// WhereSet(EDQDocField.ti_e.getConvertIndexField(),
								// op,
								// st.replaceAll("-", " "), 150));
								/** 인접검색 넣는다. 2016.03.15 */
								/** KPASS 검색 - 영문 제목 인접검색 2016-09-23 */
								if (st.contains("*")) {
									result.addAll(withinSearchExceptWildCard(new EDQDocField[] { EDQDocField.tie },
											new EDQDocField[] { EDQDocField.tie_n }, st.replaceAll("-", " ")));
								} else {
									/** 인접검색 넣는다. 2016.03.15 */
									result.addAll(withinSearch(EDQDocField.tie_n, st.replaceAll("-", " ")));
								}
								break;
							case claims:
								// if (!isNot) {
								// op = Protocol.WhereSet.OP_HASALL;
								// }
								// result.add(new
								// WhereSet(EDQDocField.ti_e.getConvertIndexField(),
								// op,
								// st.replaceAll("-", " "), 150));
								if (st.contains("*")) {
									if (isKpass) {
										/** KPASS 검색 - 청구항 인접검색 2016-09-23 */
										result.addAll(withinSearchExceptWildCard(new EDQDocField[] { EDQDocField.claims },
												new EDQDocField[] { EDQDocField.claims_n }, st.replaceAll("-", " ")));
									} else {
										result.addAll(withinSearchExceptWildCard(new EDQDocField[] { EDQDocField.claims_p },
												new EDQDocField[] { EDQDocField.claims_en }, st.replaceAll("-", " ")));
									}
								} else {
									/** KPASS 검색 - 청구항 인접검색 2016-09-23 */
									if (isKpass) {
										result.addAll(withinSearch(EDQDocField.claims_n, st.replaceAll("-", " ")));
									} else {
										/** 인접검색 넣는다. 2016.03.15 */
										result.addAll(withinSearch(EDQDocField.claims_en, st.replaceAll("-", " ")));
									}
								}
								break;
							case description:
								/** KPASS 검색 - 청구항 인접검색 2016-09-23 */
								if (st.contains("*")) {
									result.addAll(withinSearchExceptWildCard(
											new EDQDocField[] { EDQDocField.detail_desc_p, EDQDocField.desc_summary_p, EDQDocField.related_apps_p },
											new EDQDocField[] { EDQDocField.detail_desc_en, EDQDocField.desc_summary_en,
													EDQDocField.related_apps_en },
											st.replaceAll("-", " ")));
								} else {
									/** 인접검색 넣는다. 2016.03.15 */
									result.add(new WhereSet(Protocol.WhereSet.OP_BRACE_OPEN));
									result.addAll(withinSearch(EDQDocField.detail_desc_en, st.replaceAll("-", " ")));
									result.add(new WhereSet(Protocol.WhereSet.OP_OR));
									result.addAll(withinSearch(EDQDocField.desc_summary_en, st.replaceAll("-", " ")));
									result.add(new WhereSet(Protocol.WhereSet.OP_OR));
									result.addAll(withinSearch(EDQDocField.related_apps_en, st.replaceAll("-", " ")));
									result.add(new WhereSet(Protocol.WhereSet.OP_BRACE_CLOSE));
								}
								break;
							case detail_desc:
								if (st.contains("*")) {
									result.addAll(withinSearchExceptWildCard(new EDQDocField[] { EDQDocField.detail_desc_p },
											new EDQDocField[] { EDQDocField.detail_desc_en }, st.replaceAll("-", " ")));
								} else {
									/** 인접검색 넣는다. 2016.03.15 */
									result.addAll(withinSearch(EDQDocField.detail_desc_en, st.replaceAll("-", " ")));
								}
								break;
							case desc_summary:
								if (st.contains("*")) {
									result.addAll(withinSearchExceptWildCard(new EDQDocField[] { EDQDocField.desc_summary_p },
											new EDQDocField[] { EDQDocField.desc_summary_en }, st.replaceAll("-", " ")));
								} else {
									/** 인접검색 넣는다. 2016.03.15 */
									result.addAll(withinSearch(EDQDocField.desc_summary_en, st.replaceAll("-", " ")));
								}
								break;
							case related_apps:
								if (st.contains("*")) {
									result.addAll(withinSearchExceptWildCard(new EDQDocField[] { EDQDocField.related_apps_p },
											new EDQDocField[] { EDQDocField.related_apps_en }, st.replaceAll("-", " ")));
								} else {
									/** 인접검색 넣는다. 2016.03.15 */
									result.addAll(withinSearch(EDQDocField.related_apps_en, st.replaceAll("-", " ")));
								}
								break;
							case non_patent:
								if (st.contains("*")) {
									result.addAll(withinSearchExceptWildCard(new EDQDocField[] { EDQDocField.non_patent_p },
											new EDQDocField[] { EDQDocField.non_patent_en }, st.replaceAll("-", " ")));
								} else {
									/** 인접검색 넣는다. 2016.03.15 */
									result.addAll(withinSearch(EDQDocField.non_patent_en, st.replaceAll("-", " ")));
								}
								break;
							case inv_gp:
								// if (!isNot) {
								// op = Protocol.WhereSet.OP_HASALL;
								// }
								// result.add(new
								// WhereSet(EDQDocField.inv_gp.getConvertIndexField(),
								// op, st, 150));
								/** 인접검색 넣는다. 2016.03.15 */
								result.addAll(withinSearch(EDQDocField.inv_gp, st.replaceAll("-", " ")));
								break;
							case app_gp:
								// if (!isNot) {
								// op = Protocol.WhereSet.OP_HASALL;
								// }
								// result.add(new
								// WhereSet(EDQDocField.app_gp.getConvertIndexField(),
								// op, st, 150));
								if (st.contains("*")) {
									result.addAll(withinSearchExceptWildCard(new EDQDocField[] { EDQDocField.app_gp_p },
											new EDQDocField[] { EDQDocField.app_gp }, st.replaceAll("-", " ")));
								} else {
									result.addAll(withinSearch(EDQDocField.app_gp, st.replaceAll("-", " ")));
								}
								/** 인접검색 넣는다. 2016.03.15 */
								break;
							case appgpe:
							case app_sido:
							case app_sigungu:
							case hl_sido:
							case hl_sigungu:
								/** KPASS 검색 검색 필드 - 출원인 정보 2016-09-23 */
								result.addAll(withinSearch(efield, st.replaceAll("-", " ")));
								break;
							case dappgp:
								if (!isNot) {
									op = Protocol.WhereSet.OP_HASALL;
								}
								result.add(new WhereSet(EDQDocField.dappgp.getConvertIndexField(), op, st, 150));
								// logger.debug("1111");
								break;
							case inv:
								if (!isNot) {
									op = Protocol.WhereSet.OP_HASALL;
								}
								if (isKpass) {
									result.add(new WhereSet(EDQDocField.inv.getConvertIndexField(), op, st, 150));
								} else {
									if (st.contains("*")) {
										result.addAll(withinSearchExceptWildCard(new EDQDocField[] { EDQDocField.inv_p },
												new EDQDocField[] { EDQDocField.inv_en }, st.replaceAll("-", " ")));
									} else {
										result.addAll(withinSearch(EDQDocField.inv_en, st.replaceAll("-", " ")));
									}
								}
								// result.add(new
								// WhereSet(EDQDocField.inv.getConvertIndexField(),
								// op, st, 150));
								break;
							case cd:
								if (!isNot) {
									op = Protocol.WhereSet.OP_HASALL;
								}
								result.add(new WhereSet(EDQDocField.cd.getConvertIndexField(), op, st, 150));
								break;
							case hl:
								if (!isNot) {
									op = Protocol.WhereSet.OP_HASALL;
								}
								result.add(new WhereSet(EDQDocField.hl.getConvertIndexField(), op, st, 150));
								break;
							case app:
								if (!isNot) {
									op = Protocol.WhereSet.OP_HASALL;
								}
								if (isKpass) {
									result.add(new WhereSet(EDQDocField.app.getConvertIndexField(), op, st, 150));
								} else {
									if (st.contains("*")) {
										result.addAll(withinSearchExceptWildCard(new EDQDocField[] { EDQDocField.app_p },
												new EDQDocField[] { EDQDocField.app_en }, st.replaceAll("-", " ")));
									} else {
										result.addAll(withinSearch(EDQDocField.app_en, st.replaceAll("-", " ")));
									}
								}
								break;
							case rndgov:
							case rndorg:
								result.add(new WhereSet(efield.getConvertIndexField(), op, st, 150));
								break;
							case ci:
								if (!isNot) {
									op = Protocol.WhereSet.OP_HASALL;
								}
								result.add(new WhereSet(EDQDocField.cd.getConvertIndexField(), op, st, 150));
								result.add(new WhereSet(Protocol.WhereSet.OP_OR));
								result.add(new WhereSet(EDQDocField.inv.getConvertIndexField(), op, st, 150));
								break;
							case ah:
								if (!isNot) {
									op = Protocol.WhereSet.OP_HASALL;
								}
								result.add(new WhereSet(Protocol.WhereSet.OP_BRACE_OPEN));
								result.add(new WhereSet(EDQDocField.app.getConvertIndexField(), op, st, 150));
								result.add(new WhereSet(Protocol.WhereSet.OP_OR));
								result.add(new WhereSet(EDQDocField.hl.getConvertIndexField(), op, st, 150));
								result.add(new WhereSet(Protocol.WhereSet.OP_OR));
								result.add(new WhereSet(EDQDocField.appgpe.getConvertIndexField(), op, st, 150));
								result.add(new WhereSet(Protocol.WhereSet.OP_BRACE_CLOSE));
								break;
							case ipc:
								if (!isNot) {
									op = Protocol.WhereSet.OP_HASANY;
								}

								if (st.indexOf("*") != -1) {
									st = st.replaceAll("[*]", "");
									System.out.println(st);
								}
								// if (!st.contains("*")) {
								// st = st.trim() + "*";
								// }
								result.add(new WhereSet(EDQDocField.ipc.getConvertIndexField(), op, st, 150));
								break;
							case cpc:
								if (!isNot) {
									op = Protocol.WhereSet.OP_HASANY;
								}

								if (st.indexOf("*") != -1) {
									st = st.replaceAll("[*]", "");
								}
								// if (!st.contains("*")) {
								// st = st.trim() + "*";
								// }
								result.add(new WhereSet(EDQDocField.cpc.getConvertIndexField(), op, st, 150));
								break;
							case ecla:
								if (!isNot) {
									op = Protocol.WhereSet.OP_HASANY;
								}

								if (st.indexOf("*") != -1) {
									st = st.replaceAll("[*]", "");
								}

								result.add(new WhereSet(EDQDocField.ecla.getConvertIndexField(), op, st, 150));
								// // TODO 기관명 검색.
								// String[] term = st.split("[\\s|-]");
								// int termCnt = 1;
								// int termSize = term.length;
								// if (termSize > 0) {
								// result.add(new
								// WhereSet(Protocol.WhereSet.OP_BRACE_OPEN));
								// for (String t : term) {
								// if (!t.contains("*")) {
								// t = t.trim() + "*";
								// }
								// result.add(new
								// WhereSet(EDQDocField.app.getConvertIndexField(),
								// Protocol.WhereSet.OP_HASANY, t, 150));
								// if (termCnt != termSize) {
								// result.add(new
								// WhereSet(Protocol.WhereSet.OP_AND));
								// }
								// termCnt += 1;
								// }
								// result.add(new
								// WhereSet(Protocol.WhereSet.OP_BRACE_CLOSE));
								// }
								break;
							case curlegaldesc:
								result.add(new WhereSet(EDQDocField.curlegaldesc.getConvertIndexField(), Protocol.WhereSet.OP_HASALL, st, 150));
								break;
							case pyear:
							case apyear:
							case priyear:
								if (st.contains("-")) {
									String[] terms = st.split("-");
									int start = Integer.parseInt(terms[0]);
									int end = Utility.getCurrentYear();
									try {
										end = Integer.parseInt(terms[1]);
									} catch (Exception e) {
										end = Utility.getCurrentYear();
									}

									// result.add(new
									// WhereSet(Protocol.WhereSet.OP_BRACE_OPEN));
									for (int idx = start; idx <= end; idx += 1) {
										result.add(
												new WhereSet(efield.getConvertIndexField(), Protocol.WhereSet.OP_HASALL, String.valueOf(idx), 150));
										if (idx != end)
											result.add(new WhereSet(Protocol.WhereSet.OP_OR));
									}
									// result.add(new
									// WhereSet(Protocol.WhereSet.OP_BRACE_CLOSE));
								} else {
									result.add(new WhereSet(efield.getConvertIndexField(), Protocol.WhereSet.OP_HASALL, st, 150));
								}
								break;
							case citation_total_count:
							case backward_citation_count:
							case forward_citation_count:
							case family_total_count:
							case main_family_count:
							case extended_family_count:
							case claims_count:
							case independent_claims_count:
								if (!st.contains("-")) {
									result.add(new WhereSet(efield.getConvertIndexField(), Protocol.WhereSet.OP_HASALL, st, 150));
								}
								break;
							default:
								break;
							}
						} else {
							op = Protocol.WhereSet.OP_HASALL;
							if (st.trim().contains("*")) {
								op = Protocol.WhereSet.OP_HASANY;
							}

							if (isNot) {
								op = Protocol.WhereSet.OP_NOT;
							}

							// System.out.println(efield);
							switch (efield) {
							case ti:
								/*
								 * [2017-02-22] 변경식과 기존식의 괄호차이로 인하여 결과 건수가 다르게
								 * 나오는 부분 수정 쿼리 생성 방식을 변경식 방식으로 처리 BY 이관재
								 */
								if (isKpass) {
									setWhereSet(result, new EDQDocField[] { EDQDocField.ti }, new EDQDocField[] { EDQDocField.ti },
											st.replaceAll("\"", ""), 150);
								} else {
									setWhereSet(result, new EDQDocField[] { EDQDocField.ti_p }, new EDQDocField[] { EDQDocField.ti_e },
											st.replaceAll("\"", ""), 150);
								}
								// result.add(new
								// WhereSet(EDQDocField.ti.getConvertIndexField(),
								// op, st.replaceAll("\"", ""), 150));
								break;
							case tie:
								/*
								 * [2017-02-22] 변경식과 기존식의 괄호차이로 인하여 결과 건수가 다르게
								 * 나오는 부분 수정 쿼리 생성 방식을 변경식 방식으로 처리 BY 이관재
								 */
								setWhereSet(result, new EDQDocField[] { EDQDocField.tie }, new EDQDocField[] { EDQDocField.tie },
										st.replaceAll("\"", ""), 150);
								// result.add(new
								// WhereSet(EDQDocField.tie.getConvertIndexField(),
								// op, st.replaceAll("\"", ""), 150));
								break;
							case claims:
								/*
								 * [2017-02-22] 변경식과 기존식의 괄호차이로 인하여 결과 건수가 다르게
								 * 나오는 부분 수정 쿼리 생성 방식을 변경식 방식으로 처리 BY 이관재
								 */
								if (isKpass) {
									setWhereSet(result, new EDQDocField[] { EDQDocField.claims }, new EDQDocField[] { EDQDocField.claims },
											st.replaceAll("\"", ""), 150);
								} else {
									setWhereSet(result, new EDQDocField[] { EDQDocField.claims_p }, new EDQDocField[] { EDQDocField.claims_e },
											st.replaceAll("\"", ""), 150);
								}

								break;
							case description:
								setWhereSet(result,
										new EDQDocField[] { EDQDocField.detail_desc_p, EDQDocField.desc_summary_p, EDQDocField.related_apps_p },
										new EDQDocField[] { EDQDocField.detail_desc_e, EDQDocField.desc_summary_e, EDQDocField.related_apps_e },
										st.replaceAll("\"", ""), 150);
								break;
							case detail_desc:
								setWhereSet(result, new EDQDocField[] { EDQDocField.detail_desc_p }, new EDQDocField[] { EDQDocField.detail_desc_e },
										st.replaceAll("\"", ""), 150);
								break;
							case desc_summary:
								setWhereSet(result, new EDQDocField[] { EDQDocField.desc_summary_p },
										new EDQDocField[] { EDQDocField.desc_summary_e }, st.replaceAll("\"", ""), 150);
								break;
							case related_apps:
								setWhereSet(result, new EDQDocField[] { EDQDocField.related_apps_p },
										new EDQDocField[] { EDQDocField.related_apps_e }, st.replaceAll("\"", ""), 150);
								break;
							case non_patent:
								setWhereSet(result, new EDQDocField[] { EDQDocField.non_patent_p }, new EDQDocField[] { EDQDocField.non_patent_e },
										st.replaceAll("\"", ""), 150);
								break;
							case abs:
								if (isKpass) {
									/*
									 * [2017-02-22] 변경식과 기존식의 괄호차이로 인하여 결과 건수가
									 * 다르게 나오는 부분 수정 쿼리 생성 방식을 변경식 방식으로 처리 BY
									 * 이관재
									 */
									setWhereSet(result, new EDQDocField[] { EDQDocField.abs }, new EDQDocField[] { EDQDocField.abs },
											st.replaceAll("\"", ""), 150);
								} else {
									/*
									 * [2017-02-22] 변경식과 기존식의 괄호차이로 인하여 결과 건수가
									 * 다르게 나오는 부분 수정 쿼리 생성 방식을 변경식 방식으로 처리 BY
									 * 이관재
									 */
									setWhereSet(result, new EDQDocField[] { EDQDocField.abs_p }, new EDQDocField[] { EDQDocField.abs_e },
											st.replaceAll("\"", ""), 150);
								}
								break;
							case app:
								if (isKpass) {
									result.add(new WhereSet(efield.getConvertIndexField(), op, st.replaceAll("\"", ""), 150));
								} else {
									setWhereSet(result, new EDQDocField[] { EDQDocField.app_p }, new EDQDocField[] { EDQDocField.app_p },
											st.replaceAll("\"", ""), 150);
								}
								break;
							case app_gp:
								setWhereSet(result, new EDQDocField[] { EDQDocField.app_gp_p }, new EDQDocField[] { EDQDocField.app_gp },
										st.replaceAll("\"", ""), 150);
								break;
							case inv:
								if (isKpass) {
									result.add(new WhereSet(efield.getConvertIndexField(), op, st.replaceAll("\"", ""), 150));
								} else {
									setWhereSet(result, new EDQDocField[] { EDQDocField.inv_p }, new EDQDocField[] { EDQDocField.inv_p },
											st.replaceAll("\"", ""), 150);
								}
								break;
							case hl:
							case cd:
								// 2016-09-27 인명 PREFIX 검색으로 인한 버그 발생으로 수정
								// if (!st.contains("*")) {
								// st = st.trim() + "*";
								// }
								result.add(new WhereSet(efield.getConvertIndexField(), op, st.replaceAll("\"", ""), 150));
								break;
							case ah:
								// 2016-09-27 인명 PREFIX 검색으로 인한 버그 발생으로 수정(이관재)
								// 2016-09-27 출원인 + 권리자 통합 검색시 괄호처리가 되지 않아 검색시
								// 검색 순차식으로 처리가 되었던 버그 수정(이관재)
								// if (!st.contains("*")) {
								// st = st.trim() + "*";
								// }
								result.add(new WhereSet(Protocol.WhereSet.OP_BRACE_OPEN));
								result.add(new WhereSet(EDQDocField.app.getConvertIndexField(), op, st.replaceAll("\"", ""), 150));
								result.add(new WhereSet(Protocol.WhereSet.OP_OR));
								result.add(new WhereSet(EDQDocField.hl.getConvertIndexField(), op, st.replaceAll("\"", ""), 150));
								result.add(new WhereSet(Protocol.WhereSet.OP_OR));
								result.add(new WhereSet(EDQDocField.appgpe.getConvertIndexField(), op, st.replaceAll("\"", ""), 150));
								result.add(new WhereSet(Protocol.WhereSet.OP_BRACE_CLOSE));
								break;
							case ci:
								// 2016-09-27 PREFIX 검색으로 인한 버그 발생으로 수정
								// if (!st.contains("*")) {
								// st = st.trim() + "*";
								// }
								// 2016-09-27 인명 PREFIX 검색으로 인한 버그 발생으로 수정(이관재)
								// 2016-09-27 창작자 + 발명인 통합 검색시 괄호처리가 되지 않아 검색시
								// 검색 순차식으로 처리가 되었던 버그 수정(이관재)
								result.add(new WhereSet(Protocol.WhereSet.OP_BRACE_OPEN));
								result.add(new WhereSet(EDQDocField.inv.getConvertIndexField(), op, st.replaceAll("\"", ""), 150));
								result.add(new WhereSet(Protocol.WhereSet.OP_OR));
								result.add(new WhereSet(EDQDocField.cd.getConvertIndexField(), op, st.replaceAll("\"", ""), 150));
								result.add(new WhereSet(Protocol.WhereSet.OP_BRACE_CLOSE));
								break;
							case ecla:
							case ipc:
							case cpc:
								logger.debug("- 2016.03.23 ECLA, IPC, CPC는 PREPOST에서 PRE_MATCH로 색인 방법을 변환한다. {} ", st);
								/**
								 * 2016.03.23 <br>
								 * ECLA, IPC, CPC는 PREPOST에서 PRE_MATCH로 색인 방법을
								 * 변환한다.
								 */
								// if (!st.contains("*")) {
								// st = st.trim() + "*";
								// }
								// if(st.contains("*")){
								// op = Protocol.WhereSet.OP_HASANY;
								// } else {
								// op = Protocol.WhereSet.OP_HASALL;
								// }

								result.add(new WhereSet(efield.getConvertIndexField(), op, st.replaceAll("\"", ""), 150));
								break;
							case curlegaldesc:
								result.add(new WhereSet(EDQDocField.curlegaldesc.getConvertIndexField(), Protocol.WhereSet.OP_HASALL, st, 150));
								break;
							case pyear:
							case apyear:
							case priyear:
								if (st.contains("-")) {
									String[] terms = st.split("-");
									int start = Integer.parseInt(terms[0]);
									int end = Utility.getCurrentYear();
									try {
										end = Integer.parseInt(terms[1]);
									} catch (Exception e) {
										end = Utility.getCurrentYear();
									}

									// result.add(new
									// WhereSet(Protocol.WhereSet.OP_BRACE_OPEN));
									for (int idx = start; idx <= end; idx += 1) {
										result.add(
												new WhereSet(efield.getConvertIndexField(), Protocol.WhereSet.OP_HASALL, String.valueOf(idx), 150));
										if (idx != end)
											result.add(new WhereSet(Protocol.WhereSet.OP_OR));
									}
									// result.add(new
									// WhereSet(Protocol.WhereSet.OP_BRACE_CLOSE));
								} else {
									result.add(new WhereSet(efield.getConvertIndexField(), Protocol.WhereSet.OP_HASALL, st, 150));
								}
								break;
							case citation_total_count:
							case backward_citation_count:
							case forward_citation_count:
							case family_total_count:
							case main_family_count:
							case extended_family_count:
							case claims_count:
							case independent_claims_count:
								if (!st.contains("-")) {
									result.add(new WhereSet(efield.getConvertIndexField(), Protocol.WhereSet.OP_HASALL, st, 150));
								}
								break;
							default:
								if (st.trim().contains("*")) {
									st = st.replaceAll("[*]", "");
								}
								result.add(new WhereSet(efield.getConvertIndexField(), op, st.replaceAll("\"", ""), 150));
							}
						}
						// logger.info("fieldCnt {} , fieldSize {}", fieldCnt,
						// fieldSize);
						if (fieldCnt != fieldSize) {
							// WhereSet Field간 검색 조건을 설정한다.
							result.add(new WhereSet(FIELD_OP));
						}
						fieldCnt += 1;

					}
					// if (ws != null) {
					// if (ws.getOperation() == Protocol.WhereSet.OP_OR ||
					// ws.getOperation() == Protocol.WhereSet.OP_AND) {
					// result.remove(result.size() - 1);
					// }
					// }
					if (fieldSize > 1) {
						// 2개 이상의 필드인 경우 필드간은 괄호로 감싸야 한다.
						result.add(new WhereSet(Protocol.WhereSet.OP_BRACE_CLOSE));
					}
				}
			}
		}
		return result;
	}

	private void setWhereSet(List<WhereSet> result, EDQDocField[] wildCardFields, EDQDocField[] generalFields, String term, int weight) {
		if (wildCardFields == null) {
			wildCardFields = new EDQDocField[0];
		}
		if (generalFields == null) {
			generalFields = new EDQDocField[0];
		}
		String[] hippenTermsDelim = null;
		if (term.contains("-")) {
			term = term.replaceAll("-", " ");
			if (term.contains("*")) {
				hippenTermsDelim = term.split(" ");
			}
		}

		result.add(new WhereSet(Protocol.WhereSet.OP_BRACE_OPEN));
		if (hippenTermsDelim != null) {
			for (String _term : hippenTermsDelim) {
				result.add(new WhereSet(Protocol.WhereSet.OP_BRACE_OPEN));
				if (_term.contains("*")) {
					if (!isKpass) {
						_term = _term.replaceAll("\\*", "");
					}
					setSearchWhereSet(result, wildCardFields, _term, weight, Protocol.WhereSet.OP_HASANY);
					// setSearchWhereSet(result, wildCardFields,
					// _term.replaceAll("\\*", ""), weight,
					// Protocol.WhereSet.OP_HASANY);
				} else {
					setSearchWhereSet(result, generalFields, _term, weight, Protocol.WhereSet.OP_HASALL);
				}
				result.add(new WhereSet(Protocol.WhereSet.OP_BRACE_CLOSE));
			}
		} else {
			result.add(new WhereSet(Protocol.WhereSet.OP_BRACE_OPEN));
			if (term.contains("*")) {
				if (!isKpass) {
					term = term.replaceAll("\\*", "");
				}
				setSearchWhereSet(result, wildCardFields, term, weight, Protocol.WhereSet.OP_HASANY);
				// setSearchWhereSet(result, wildCardFields,
				// term.replaceAll("\\*", ""), weight,
				// Protocol.WhereSet.OP_HASANY);
			} else {
				setSearchWhereSet(result, generalFields, term, weight, Protocol.WhereSet.OP_HASALL);
			}
			result.add(new WhereSet(Protocol.WhereSet.OP_BRACE_CLOSE));
		}
		result.add(new WhereSet(Protocol.WhereSet.OP_BRACE_CLOSE));
	}

	private void setSearchWhereSet(List<WhereSet> result, EDQDocField[] fields, String term, int weight, byte op) {
		for (EDQDocField field : fields) {
			result.add(new WhereSet(field.getConvertIndexField(), op, term, weight));
			result.add(new WhereSet(Protocol.WhereSet.OP_OR));
		}

		if (fields.length > 0) {
			result.remove(result.size() - 1);
		}
	}

	private void setSearchTerm(EDQDocField field, String st) {
		switch (field) {
		case claims:
		case claims_n:
			break;
		case abs:
		case tie:
		case ti:
		case ti_e:
		case tie_n:
		case ti_n:
		case abs_e:
			String fStr = field.name();
			if (field == EDQDocField.ti_e || field == EDQDocField.ti_n) {
				fStr = EDQDocField.ti.getUpperCase();
				highlightField.add(EDQDocField.ti);
			} else if (field == EDQDocField.tie_n) {
				fStr = EDQDocField.tie.getUpperCase();
				highlightField.add(EDQDocField.tie);
			} else if (field == EDQDocField.abs_e) {
				fStr = EDQDocField.abs.getUpperCase();
				highlightField.add(EDQDocField.abs);
			} else {
				fStr = fStr.toUpperCase();
				highlightField.add(field);
			}

			Set<String> hTerms = Collections.emptySet();
			Set<String> hwTerms = Collections.emptySet();
			if (highlightSearchTerms.containsKey(fStr)) {
				hTerms = highlightSearchTerms.get(fStr);
			} else {
				hTerms = new HashSet<String>();
				highlightSearchTerms.put(fStr, hTerms);
			}

			if (highlightWildCardTerms.containsKey(fStr)) {
				hwTerms = highlightWildCardTerms.get(fStr);
			} else {
				hwTerms = new HashSet<String>();
				highlightWildCardTerms.put(fStr, hwTerms);
			}

			if (st.contains("*")) {
				if (st.contains("-")) {
					String[] sArr = st.split("-");
					for (String s : sArr) {
						if (s.contains("*")) {
							hwTerms.add(s.replaceAll("[*]", "").toLowerCase());
						}
					}
					hwTerms.add(st.toLowerCase());
				} else {
					hwTerms.add(st.replaceAll("[*]", "").toLowerCase());
				}
			} else {
				String[] terms = st.split("\\s");
				for (String t : terms) {
					if (t.contains("-")) {
						String[] sArr = t.split("-");
						for (String s : sArr) {
							hTerms.add(s.toLowerCase());
						}
						hTerms.add(t.toLowerCase());
					} else {
						hTerms.add(t.toLowerCase());
					}
				}
			}
			break;
		default:
			break;
		}
	}

	/**
	 * SCOPUS에서 지원하는 within 검색을 지원한다.<br>
	 * 이를 통해 ""완전일치 검색을 구현한다.
	 * 
	 * @author coreawin
	 * @date 2016. 3. 15.
	 */
	public List<WhereSet> withinSearch(EDQDocField field, String terms) {
		// logger.debug("FIELD : " + field);
		// logger.debug("TERMS : " + terms);
		// terms = terms.replaceAll("\\s{1,}", " ");
		terms = terms.replaceAll("[:/,]", " ").replaceAll("\\s{1,}", " ").trim();
		String[] termA = terms.split(" ");
		List<WhereSet> result = new LinkedList<WhereSet>();
		if (termA.length == 1) {
			result.add(new WhereSet(field.getConvertIndexField(), Protocol.WhereSet.OP_HASALL, terms, 150));
		} else {
			result.add(new WhereSet(Protocol.WhereSet.OP_BRACE_OPEN));
			int termASize = termA.length;
			for (int idx = 0; idx < termASize; idx++) {
				for (int jdx = 0; jdx < termASize; jdx++) {
					if (jdx > idx) {
						String iTerm = termA[idx].trim();
						String jTerm = termA[jdx].trim();
						if (iTerm.equalsIgnoreCase(jTerm)) {
							result.add(new WhereSet(field.getConvertIndexField(), Protocol.WhereSet.OP_HASALL, jTerm, 300));
						} else {
							String[] q = new String[] { iTerm, jTerm };
							result.add(new WhereSet(field.getConvertIndexField(), Protocol.WhereSet.OP_PROXIMITY_WITHIN, q, new int[] { 300, 300 },
									(jdx - idx) * 1));
						}
						result.add(new WhereSet(Protocol.WhereSet.OP_AND));
					}
				}
			}
			// for (int idx = 0; idx < termA.length - 1; idx++) {
			// String[] q = new String[] { termA[idx].trim(), termA[idx +
			// 1].trim() };
			// result.add(new WhereSet(field.getConvertIndexField(),
			// Protocol.WhereSet.OP_PROXIMITY_WITHIN, q, new int[] { 300, 300 },
			// 1));
			// result.add(new WhereSet(Protocol.WhereSet.OP_AND));
			// }
			result.remove(result.size() - 1);
			result.add(new WhereSet(Protocol.WhereSet.OP_BRACE_CLOSE));
		}
		return result;
	}

	/**
	 * 인접검색시 *가 들어오면 *를 제외한 나머지 부분에 대한 인접검색을 지원한다.<br>
	 * 
	 * @author coreawin
	 * @date 2015. 9. 30.
	 */
	/**
	 * @author pc
	 * @date 2016. 9. 1.
	 * @param fields
	 *            prepost 필드.
	 * @param enfields
	 *            인접기능은 있고 스테밍이 되지 않은 필드.
	 * @param terms
	 * @return
	 */
	public List<WhereSet> withinSearchExceptWildCard(EDQDocField[] fields, EDQDocField[] enfields, String terms) {
		terms = terms.replaceAll("\\s{1,}", " ");
		String[] termA = terms.split(" ");
		LinkedList<WhereSet> result = new LinkedList<WhereSet>();
		if (termA.length == 1) {
			if (terms.indexOf("*") != -1) {
				for (EDQDocField _fields : fields) {
					if (isKpass) {
						result.add(new WhereSet(_fields.getConvertIndexField(), Protocol.WhereSet.OP_HASANY, terms, 150));
					} else {
						result.add(new WhereSet(_fields.getConvertIndexField(), Protocol.WhereSet.OP_HASANY, terms.replaceAll("\\*", ""), 150));
					}
					result.add(new WhereSet(Protocol.WhereSet.OP_AND));
				}
			} else {
				for (EDQDocField _fields : enfields) {
					result.add(new WhereSet(_fields.getConvertIndexField(), Protocol.WhereSet.OP_HASALL, terms, 150));
					result.add(new WhereSet(Protocol.WhereSet.OP_AND));
				}
			}
			if (result.size() > 0) {
				result.removeLast();
			}
		} else {
			result.add(new WhereSet(Protocol.WhereSet.OP_BRACE_OPEN));
			int termASize = termA.length;
			int wildTermsCnt = 0;
			Set<Integer> check = new HashSet<Integer>();
			for (int idx = 0; idx < termASize; idx++) {
				String ia = termA[idx].trim();
				if (ia.indexOf("*") != -1 || ia.indexOf("-") != -1) {
					if (ia.indexOf("*") != -1) {
						if (!isKpass) {
							ia = ia.replaceAll("\\*", "");
						}
					}
					for (EDQDocField _fields : fields) {
						result.add(new WhereSet(_fields.getConvertIndexField(), Protocol.WhereSet.OP_HASANY, ia, 150));
						result.add(new WhereSet(Protocol.WhereSet.OP_AND));
					}
					wildTermsCnt += 1;
					continue;
				}
				check.add(idx);
				for (int jdx = 0; jdx < termASize; jdx++) {
					if (jdx > idx) {
						String ja = termA[jdx].trim();
						if (ja.indexOf("*") != -1)
							continue;
						String[] q = new String[] { ia, ja };
						for (EDQDocField _fields : enfields) {
							byte prevOperation = result.getLast().getOperation();
							if (prevOperation != Protocol.WhereSet.OP_AND && prevOperation != Protocol.WhereSet.OP_BRACE_OPEN) {
								/**
								 * 2016.09.01 위에서 wildcard 식 작성후 AND 가 없으므로
								 * 넣어준다.
								 */
								result.add(new WhereSet(Protocol.WhereSet.OP_AND));
							}
							result.add(new WhereSet(_fields.getConvertIndexField(), Protocol.WhereSet.OP_PROXIMITY_WITHIN, q, new int[] { 300, 300 },
									(jdx - idx)));
							result.add(new WhereSet(Protocol.WhereSet.OP_AND));
						}
					}
				}
			}
			// logger.debug("terms {} {}", (termASize),( wildTermsCnt));
			if (termASize - wildTermsCnt < 2) {
				for (EDQDocField _fields : enfields) {
					for (int _idx : check) {
						String s = termA[_idx];
						if (!isKpass) {
							s = s.replaceAll("\\*", "");
						}
						result.add(new WhereSet(_fields.getConvertIndexField(), Protocol.WhereSet.OP_HASALL, s, 300));
						result.add(new WhereSet(Protocol.WhereSet.OP_AND));
					}
				}
			}
			if (fields.length > 0 && termASize > 0) {
				result.removeLast();
			}
			result.add(new WhereSet(Protocol.WhereSet.OP_BRACE_CLOSE));
		}
		return result;
	}

	/**
	 * 인접 검색 문법에 wildcar가 입력될 경우 인접 검색이 아닌 white space단위로 구분되어 검색한다.<br>
	 * 
	 * @author coreawin
	 * @date 2014. 6. 23.
	 * @param fields
	 *            일반 필드
	 * @param efields
	 *            인접 검색 필드.
	 * @param term
	 * @return
	 */
	private List<WhereSet> nearWildCardMakeWhereSet(EDQDocField[] fields, EDQDocField[] efields, String term) {
		List<WhereSet> result = new LinkedList<WhereSet>();
		result.add(new WhereSet(Protocol.WhereSet.OP_BRACE_OPEN));
		String[] terms = term.split(" ");
		LinkedList<String> termList = new LinkedList<String>();
		StringBuffer _buf = new StringBuffer();
		for (String t : terms) {
			if (t.indexOf("*") != -1) {
				termList.add(_buf.toString());
				_buf.setLength(0);
				termList.add(t);
			} else {
				_buf.append(t);
				_buf.append(" ");
			}
		}
		termList.add(_buf.toString());

		for (String t : termList) {
			t = t.trim();
			if ("".equals(t))
				continue;

			if (t.indexOf("*") != -1) {
				// wildcard가 입력되어 있다면...
				if (fields.length > 1)
					result.add(new WhereSet(Protocol.WhereSet.OP_BRACE_OPEN));
				for (EDQDocField f : fields) {
					// FIELD간은 OR 검색이다.
					logger.debug(f + " : " + t);
					/**
					 * [2017-02-06] Prepost 색인된 필드에 한하여 검색어에 Wildcard('*') 문자열이
					 * 포함되어 있다면 해당 필드에는 검색어에는 WildCard 문자열을 제거하지 않는다. by 이관재
					 * <br />
					 */
					if (isKpass) {
						result.add(new WhereSet(f.getConvertIndexField(), Protocol.WhereSet.OP_HASANY, t, 150));
					} else {
						result.add(new WhereSet(f.getConvertIndexField(), Protocol.WhereSet.OP_HASANY, t.replaceAll("\\*", ""), 150));
					}
					result.add(new WhereSet(Protocol.WhereSet.OP_OR));
				}
				result.remove(result.size() - 1);
				if (fields.length > 1)
					result.add(new WhereSet(Protocol.WhereSet.OP_BRACE_CLOSE));
			} else {
				// wildcard가 입력되어 있지 않다면...
				if (efields.length > 1)
					result.add(new WhereSet(Protocol.WhereSet.OP_BRACE_OPEN));
				for (EDQDocField f : efields) {
					// FIELD간은 OR 검색이다.
					result.add(new WhereSet(f.getConvertIndexField(), Protocol.WhereSet.OP_HASALL, t, 150));
					result.add(new WhereSet(Protocol.WhereSet.OP_OR));
				}
				result.remove(result.size() - 1);
				if (efields.length > 1)
					result.add(new WhereSet(Protocol.WhereSet.OP_BRACE_CLOSE));
			}
			result.add(new WhereSet(Protocol.WhereSet.OP_AND));
		}
		result.remove(result.size() - 1);
		result.add(new WhereSet(Protocol.WhereSet.OP_BRACE_CLOSE));
		return result;
	}

	/**
	 * 필드간의 검색 방법을 설정한다. <br>
	 * (*).ti.abs. 인 경우 기본적으로 필드간은 OP_OR로 검색한다.
	 * 
	 * @author neon
	 * @date 2013. 8. 27.
	 * @param op
	 *            {@link Protocol.WhereSet.OP_OR}
	 *            {@link Protocol.WhereSet.OP_AND}
	 */
	public void setFieldOP(byte op) {
		this.FIELD_OP = op;
	}

	/**
	 * 입력된 쿼리로부터 Filter 쿼리인지 WhereSet 쿼리인지 분리한다.
	 * 
	 * @author coreawin
	 * @date 2014. 7. 2.
	 * @param query
	 *            입력된 쿼리
	 * @return
	 */
	private List<String>[] getDivFilterWhereQuery(String query) {
		logger.info("getDivFilterWhereQuery : {}", query);
		List<String>[] result = new LinkedList[2];
		List<String> _filterQuery = new LinkedList<String>();
		List<String> _whereQuery = new LinkedList<String>();
		query = query.trim();
		Matcher m = fieldPattern.matcher(query);
		String term = "";
		if (query.indexOf("(") != -1) {
			// term = query.substring(query.lastIndexOf("("), query.indexOf(")")
			// + 1).trim();
			term = query.substring(0, query.indexOf(")") + 1).trim();
		} else {
			term = query.substring(0, query.indexOf(")") + 1).trim();
		}
		// String term = query.substring(0, query.indexOf(")") + 1).trim();
		logger.info("getDivFilterWhereQuery TERM AAAAAAAAAAA : {}", term);

		System.out.println(query);
		while (m.find()) {
			String f = m.group().trim().toLowerCase();
			 logger.info("FIELD : {} / {}", f, m.group());
			 if(f.length() < 2) continue;
			f = FieldConversion.conversionField(f, isKpass);
			if (f.indexOf("count") == -1 && term.indexOf("-") == -1) {
				// whereset에 담겨야 한다.
				logger.debug("필터쿼리지만 성능상 WhereSet으로 만든다. {}", query);
				// System.out.println(term);
			} else if (f.indexOf("count") != -1 && term.indexOf("-") == -1) {
				logger.debug("필터쿼리지만 성능상 WhereSet으로 만든다. {}", query);
			} else if (SearchDefinition._NOT_FILTER_FIELD_LIST.contains(EDQDocField.valueOf(f))) {
				logger.debug("@2016.09.13 @coreawin KPASS에서 출원인대표명 Grouping 결과내 재검색시 원본 문자열에 hippen이 들어올 수 있으나 이는 필터조건 식이 아니다.");
			} else {
				if (SearchDefinition._FILTER_FIELD_LIST.contains(EDQDocField.valueOf(f))) {
					if (term.indexOf("-") != -1) {
						_filterQuery.add(f + DELIM_FIELD_RULE + term);
					} else {
						logger.debug("필터쿼리지만 성능상 WhereSet으로 만든다. {} => {}", f, term);
					}
				} else {
					// logger.debug("test filter patten query {}", f);

					if ("abs".equals(f) || "ti".equals(f) || "inv".equals(f) || "app".equals(f)) {
						/**
						 * 2016.03.23 <br>
						 * 초록 혹은 제목에 다음과 같은 식인 경우 filter 대신 whereset으로 변환하지 않아도
						 * 된다.<br>
						 * hipen은 구간 레인지 검색인데, <br>
						 * 초록이나 제목에 이 하이픈이 들어온경우 구간 검색으로 인식하는 부분인데 이를 이 필드들에
						 * 대해서는 지원하지 않도록 수정.<br>
						 * ((anti-cheat).ti. and (anti-cheat).ab.) <br>
						 */
					} else {
						_whereQuery.add(term + "." + f + ".");
					}
				}
			}
		}
		if (_filterQuery.size() > 0) {
			logger.debug(query);
			logger.debug("{}------------ {} --------- {}", query.substring(query.lastIndexOf(".") + 1), query.substring(0, query.lastIndexOf("(")));
			char[] arr = query.substring(query.lastIndexOf(".") + 1).toCharArray();
			char[] arr2 = query.substring(0, query.lastIndexOf("(")).toCharArray();
			char[] total = new char[arr.length + arr2.length];
			System.arraycopy(arr, 0, total, 0, arr.length);
			System.arraycopy(arr2, 0, total, arr.length, arr2.length);

			System.arraycopy(arr2, 0, total, 0, arr2.length);
			System.arraycopy(arr, 0, total, arr2.length, arr.length);
			for (char c : total) {
				switch (c) {
				case '(':
				case ')':
					_whereQuery.add(String.valueOf(c));
					break;
				default:
					break;
				}
			}
		}
		result[0] = _filterQuery;
		result[1] = _whereQuery;
		logger.debug("getDivFilterWhereQuery result : {} / {} ", _filterQuery, _whereQuery);
		return result;
	}

	/**
	 * @author neon
	 * @date 2013. 8. 9.
	 * @param query
	 *            단위 검색식
	 * @param braceCnt
	 *            필드 이후에 출현하는 괄호의 수
	 */
	private List<WhereSet> convertBasisRuleWhereSet(String query) {
		logger.debug("convertBasisRuleWhereSet input query : {} ", query);
		strBuf.setLength(0);
		String prevQuery = "";
		String postQuery = "";
		query = queryRefiner(query).trim();
		logger.debug("convertBasisRuleWhereSet refine 쿼리 : {} ", query);
		if ((query.endsWith(".") && query.startsWith("(")) && query.endsWith(".")) {
			// TEST 완료
			// logger.debug(".query 이상적인 : {} ", query);
			char[] ca = query.toCharArray();
			LinkedList<Integer> braceIndexList = new LinkedList<Integer>();
			for (int idx = 0; idx < ca.length; idx += 1) {
				char c = ca[idx];
				// System.out.print(c);
				if (c == '(') {
					braceIndexList.push(idx);
					// System.out.println("push " + idx);
				} else if (c == ')') {
					// System.out.println(") : " + list);
					int p = braceIndexList.pop();
					if (p == 0) {
					}
					// System.out.println("pop " + p);
				}
			}
			String q = query.substring(query.indexOf("("), query.length());
			// logger.debug("q {}", braceIndexList);
			if (braceIndexList.size() > 0) {
				int lastbraceidx = braceIndexList.getFirst() + 1;
				if (lastbraceidx != 0) {
					prevQuery = query.substring(0, lastbraceidx);
				}
				// logger.info(".query test :{}, {} ", lastbraceidx,
				// query.substring(0, lastbraceidx));
				q = query.substring(lastbraceidx, query.length());
			}
			strBuf.append(q);
			// logger.debug("input query {}", strBuf);
		} else if (query.endsWith("11.")) {
			// Deprecated
			// logger.debug(".Query 이상한 : {} ", query);
			// String q = query.substring(query.indexOf("("), query.length());
			// strBuf.append(q);
			// prevQuery = query.substring(0, query.indexOf(q));
			// postQuery = query.substring(query.lastIndexOf(".") + 1,
			// query.length());
		} else {
			// logger.debug("기타 쿼리식 : {} ", query);
			char[] qa = query.toCharArray();
			char prevC = ' ';
			boolean isQuery = false;
			for (char q : qa) {
				switch (q) {
				case '(':
					/* 2017-02-20 검색식 필드 내부 중첩괄호 처리 */
					/*
					 * (("DSLR CAMERA" OR "MIRRORLESS CAMERA") AND ((
					 * "Telephoto Lens" OR "Super Telephoto Lens") OR (
					 * "Wide-angle Lens" OR "Ultra Wide Lens"))).ab.
					 */
					if (prevC == '(') {
						isQuery = true;
						strBuf.append(prevC);
					}
					break;
				default:
					if (prevC == '(') {
						isQuery = true;
						strBuf.append(prevC);
					}
					if (prevC == '.' && q == ')') {
						isQuery = false;
					}
					if (isQuery) {
						strBuf.append(q);
					}
					break;
				}
				prevC = q;
			}
			String q = strBuf.toString();
			// System.out.println(query + "\t" + q);
			// System.out.println("QUER INDEX : " + query.indexOf(q));
			prevQuery = query.substring(0, query.indexOf(q));
			postQuery = query.substring(query.lastIndexOf(".") + 1, query.length());
		}
		if (prevQuery.trim().equals("(") && postQuery.trim().equals(")")) {
			prevQuery = "";
			postQuery = "";
		}
		// logger.debug("=> pasing query : {}", strBuf);
		// logger.debug("prev {}, post {}", prevQuery, postQuery);
		List<String> prevList = termExtract(prevQuery);
		List<String> postList = termExtract(postQuery);
		// logger.debug("list prev {}, post {}", prevList, postList);

		List<WhereSet> result = new LinkedList<WhereSet>();
		result.addAll(makeWhereSetList(prevList, null));
		if (strBuf.length() > 0) {
			result.add(new WhereSet(Protocol.WhereSet.OP_BRACE_OPEN));
			result.addAll(makeWhereSet(strBuf.toString(), prevList));
			result.add(new WhereSet(Protocol.WhereSet.OP_BRACE_CLOSE));
		}
		result.addAll(makeWhereSetList(postList, null));
		// for (WhereSet w : result) {
		// logger.debug("단위쿼리 WhereSet {}", w);
		// }

		return result;
	}

	private List<String> termExtract(String term) {
		stBuf.setLength(0);
		char[] ca = term.trim().toCharArray();
		List<String> l = new LinkedList<String>();
		if (ca.length == 0)
			return l;
		for (char c : ca) {
			switch (c) {
			case '(':
				if (stBuf.length() > 0) {
					l.add(stBuf.toString());
					stBuf.setLength(0);
				}
				l.add("(");
				break;
			case ' ':
				if (stBuf.length() > 0) {
					l.add(stBuf.toString());
					stBuf.setLength(0);
				}
				break;
			case ')':
				if (stBuf.length() > 0) {
					l.add(stBuf.toString());
					stBuf.setLength(0);
				}
				l.add(")");
				break;
			default:
				stBuf.append(c);
				break;
			}
		}
		if (stBuf.length() > 0) {
			l.add(stBuf.toString());
			stBuf.setLength(0);
		}
		return l;
	}

	/**
	 * 고급 검색식을 DQ의 WhereSet으로 변환한다.
	 * 
	 * @author neon
	 * @date 2013. 8. 9.
	 * @param advancedQuery
	 *            고급 검색식
	 * @return
	 */
	private WhereSet[] makeWhereSet() {
		// logger.debug("input query : {} ", advancedQuery);
		appWhereSetFilter.clear();
		invWhereSetFilter.clear();
		executeMakeWhere = true;
		LinkedList<WhereSet> l = getWhereSetList();
		// logger.debug("input query : {} ", advancedQuery);
		LinkedList<WhereSet> result = new LinkedList<WhereSet>();
		int openBraceCnt = 0;
		int closeBraceCnt = 0;

		byte prev = Protocol.WhereSet.OP_HASALLONE;
		byte next = Protocol.WhereSet.OP_HASALLONE;

		final byte _OPEN = Protocol.WhereSet.OP_BRACE_OPEN;
		final byte _CLOSE = Protocol.WhereSet.OP_BRACE_CLOSE;
		final byte _AND = Protocol.WhereSet.OP_AND;
		final byte _OR = Protocol.WhereSet.OP_OR;
		for (int i = 0; i < l.size(); i++) {
			WhereSet ws = l.get(i);
			byte o = ws.getOperation();
			if (i < l.size() - 1) {
				next = l.get(i + 1).getOperation();
			}

			// if(prev == Protocol.WhereSet.OP_BRACE_OPEN && o ==
			// Protocol.WhereSet.OP_BRACE_CLOSE){
			// prev = o;
			// result.removeLast();
			// continue;
			// }
			// if(o == Protocol.WhereSet.OP_BRACE_OPEN && next ==
			// Protocol.WhereSet.OP_BRACE_CLOSE){
			// prev = o;
			// continue;
			// }

			// if(prev == _OPEN && (o == _AND || o == _OR)){
			// prev = o;
			// System.out.println("ccc");
			// result.removeLast();
			// if(result.size()>0){
			// prev = result.getLast().getOperation();
			// }else{
			// prev = Protocol.WhereSet.OP_HASALLONE;
			// }
			// continue;
			// }
			//
			// if(prev == _OPEN && (o == _CLOSE)){
			// prev = o;
			// System.out.println("AAAAAAAAAAA");
			//// result.removeLast();
			// continue;
			// }

			if (prev == _OPEN && (o == _AND || o == _OR) && next == _OPEN) {
				prev = o;
				continue;
			}
			if (prev == _CLOSE && (o == _AND || o == _OR) && next == _CLOSE) {
				prev = o;
				continue;
			}
			result.add(ws);

			switch (o) {
			case Protocol.WhereSet.OP_BRACE_OPEN:
				openBraceCnt += 1;
				break;
			case Protocol.WhereSet.OP_BRACE_CLOSE:
				closeBraceCnt += 1;
				break;
			default:
				break;
			}
			prev = o;
		}

		LinkedList<WhereSet> verifyResult = new LinkedList<WhereSet>();
		for (int i = 0; i < result.size(); i++) {
			WhereSet ws = result.get(i);
			byte w = ws.getOperation();
			byte n = -1;
			if (i + 1 < result.size()) {
				n = result.get(i + 1).getOperation();
				if (w == _OPEN & (n == _AND || n == _OR || n == _CLOSE)) {
					continue;
				}
				if ((w == _AND || w == _OR) & (n == _CLOSE)) {
					continue;
				}
			}

			if (verifyResult.size() == 0) {
				if (w == _CLOSE || w == _AND || w == _OR) {
					continue;
				}
			}
			verifyResult.add(ws);
		}

		int cntIndex = 0;
		WhereSet[] r = new WhereSet[verifyResult.size()];
		for (WhereSet w : verifyResult) {
			r[cntIndex++] = w;
		}
		if (openBraceCnt != closeBraceCnt) {
			logger.error("open cnt {}, close cnt {}", openBraceCnt, closeBraceCnt);
			for (WhereSet w : r) {
				logger.error("error info: {}", w.toString());
			}
			throw new IllegalArgumentException("입력 검색식에서 괄호 연산자의 개수가 일치하지 않습니다.");
		}
		logger.debug("input query : {} ", advancedQuery);
		this.whereSetResult = r;
		return this.whereSetResult;
	}

	public WhereSet[] getWhereSet() {
		if (this.filterSetResult.length > 0 && this.whereSetResult.length == 0) {
			if (this.whereSetResult.length == 0) {
				this.whereSetResult = new WhereSet[] { new WhereSet("IDX_ALL", Protocol.WhereSet.OP_HASALL, "y", 150) };
			}
		}
		return this.whereSetResult;
	}

	/**
	 * 고급 검색식을 DQ의 FilterSet으로 변환한다.
	 * 
	 * @author neon
	 * @date 2013. 8. 9.
	 * @param advancedQuery
	 *            고급 검색식
	 * @return
	 */
	public FilterSet[] getFilterSet() {
		return this.filterSetResult;
	}

	private FilterSet[] makeFiterSet() {
		FilterSet[] result = null;
		FilterSet[] fs = makeFilterSet();
		FilterSet[] fs2 = makeFilterSets(filterQueries);
		LinkedList<FilterSet> _fs = new LinkedList<FilterSet>();
		for (FilterSet f : fs) {
			logger.debug("filter1 {}", f);
			_fs.add(f);
		}
		for (FilterSet f : fs2) {
			// logger.debug("filter2 {}", f);
			_fs.add(f);
		}
		result = new FilterSet[_fs.size()];
		for (int idx = 0; idx < _fs.size(); idx += 1) {
			result[idx] = _fs.get(idx);
		}
		return result;
	}

	private final String DELIM_FIELD_RULE = "@DQ@";

	private FilterSet[] makeFilterSets(List<String> queryList) {
		List<FilterSet> result = new LinkedList<FilterSet>();
		if (queryList.size() == 0)
			return new FilterSet[0];
		boolean isNot = false;
		for (String e : queryList) {
			e = e.trim();
			if ("".equals(e))
				continue;
			// logger.debug("new filter query : {} ", e);
			String[] values = e.split(DELIM_FIELD_RULE);
			if (values.length == 2) {
				String field = values[0];
				String value = values[1];
				EDQDocField efield = null;
				field = field.replaceAll("\\(", "").trim();
				field = FieldConversion.conversionField(field, isKpass);
				logger.debug("new filter field : {} ", field);
				if (!"".equals(field)) {
					efield = EDQDocField.valueOf(field.toLowerCase().trim());
					if (!SearchDefinition._FILTER_FIELD_LIST.contains(efield)) {
						continue;
					}
					result.addAll(makeFilterSet(efield, value));
				}
			}
		}
		FilterSet[] r = new FilterSet[result.size()];
		int idx = 0;
		for (FilterSet f : result) {
			// logger.debug("make filter set {}", f);
			r[idx++] = f;
		}
		return r;
	}

	private List<FilterSet> makeFilterSet(EDQDocField field, String value) {
		List<FilterSet> r = new LinkedList<FilterSet>();
		if (value == null)
			return r;
		value = value.replaceAll("\\(", "").replaceAll("\\)", "");
		switch (field) {
		case pndate:
		case pndate2:
		case pndate3:
		case ipndate:
		case ipndate3:
		case py:
		case py2:
		case py3:
		case apdate:
		case iapdate:
		case pridate:
//		case priyear:
//		case pyear:
//		case apyear:
		case ay:
		case iay:
		case ipy:
		case ipy3:
		case pridate_first:
		case pridate_last:
			if (value.contains("-")) {
				String[] values = value.split("-");
				if (values.length == 1) {
					values = new String[] { values[0], "" };
				}
				logger.debug("filter value {} / {}", values[0], values[1]);
				if ("".equals(values[0])) {
					values[0] = "";
				}
				if ("".equals(values[1])) {
					values[1] = "";
				}

				values[0] = values[0].toLowerCase().replaceAll("(and|or)", "");
				byte filterOP = Protocol.FilterSet.OP_RANGE;
				if (values[0].indexOf("not") != -1) {
					// @2016.11.14 NOT 연산자가 포함되어 있다면 NOT Filter식이다.
					logger.info("NOT Filter rule {} / {}", this.advancedQuery, values);
					values[0] = values[0].replaceAll("not", "").replaceAll("\\s{1,}", "");
					filterOP = Protocol.FilterSet.OP_NOT;
				}

				r.add(new FilterSet(filterOP, field.getFilterField(), new String[] { values[0], values[1] }));
			} else {
				// 단일 속성 WhereSet으로 만들어 져야 한다.
			}
			break;
		case claims_count:
		case independent_claims_count:
		case family_total_count:
		case main_family_count:
		case extended_family_count:
		case citation_total_count:
		case forward_citation_count:
		case backward_citation_count:
		case non_citation_count:
			// logger.debug("make filter {} / {} ", field, value);
			value = value.toUpperCase();
			System.out.println(value.contains("-"));
			if (value.contains("-")) {
				String[] values = value.split("-");
				if (values.length == 1) {
					values = new String[] { values[0], "" };
				}
				if ("".equals(values[0])) {
					values[0] = "0";
				}
				if ("".equals(values[1])) {
					values[1] = "";
				}
				// logger.debug("{} / {}", values[0], values[1]);
				if (value.indexOf("NOT") != -1) {
					values[0] = values[0].replaceAll("NOT", "").trim();
					values[1] = values[1].replaceAll("NOT", "").trim();
					if ("".equals(values[0])) {
						values[0] = "0";
					}
					logger.debug("start {} / end {}", values[0], values[1]);
					r.add(new FilterSet(Protocol.FilterSet.OP_NOT, field.getFilterField(), new String[] { values[0], values[1] }));
				} else {
					r.add(new FilterSet(Protocol.FilterSet.OP_RANGE, field.getFilterField(), new String[] { values[0], values[1] }));
				}
			} else {
				// 없어도 필터셋으로 만들어 져야 한다.
				if (value.indexOf("NOT") != -1) {
					// NOT 키워드가 있다면
					value = value.replaceAll("NOT", "").trim();
					r.add(new FilterSet(Protocol.FilterSet.OP_NOT, field.getFilterField(), value));
				} else {
					r.add(new FilterSet(Protocol.FilterSet.OP_MATCH, field.getFilterField(), value));
				}
			}
			break;
		default:
			break;
		}
		// logger.debug("make filter set {}", r);
		return r;
	}

	private FilterSet[] makeFilterSet() {
		filterSetList = new LinkedList<FilterSet>();
		Map<String, String[]> filterData = AdvanceSearchParserUtil.getFilterData(this.advancedQuery);
		// logger.debug("query : {}", this.advancedQuery);
		// logger.debug("getFilterSet {}", filterData.toString());
		Set<String> filterFieldSet = filterData.keySet();
		// logger.debug("filterFieldSet {}", filterFieldSet);
		for (String field : filterFieldSet) {
			field = FieldConversion.conversionField(field, isKpass);
			EDQDocField eField = null;
			try {
				eField = EDQDocField.valueOf(field.toLowerCase());
			} catch (IllegalArgumentException iae) {
				iae.printStackTrace();
				logger.error(iae.getMessage());
				continue;
			}
			// logger.debug("Filter Field EDQDOC={}, INPUT={}", eField.name(),
			// field);
			if (SearchDefinition._FILTER_FIELD_LIST.contains(eField)) {
				String[] result = filterData.get(field);
				// logger.debug("FIELD {}", field);
				for (String s : result) {
					// logger.debug("\t FIELD DATA {}, {}", s, result.length);
				}
				if (field.indexOf("year") != -1 || field.indexOf("date") != -1 || field.indexOf("cnt") != -1 || field.indexOf("count") != -1) {
					// filterSetList.add(new
					// FilterSet(Protocol.FilterSet.OP_RANGE,
					// eField.getFilterField(), result));
					for (int idx = 0; idx < result.length; idx = idx + 2) {
						filterSetList.add(
								new FilterSet(Protocol.FilterSet.OP_RANGE, eField.getFilterField(), new String[] { result[idx], result[idx + 1] }));
					}
				} else {
					for (String r : result) {
						if (r.indexOf("\"") != -1) {
							r = r.replaceAll("\"", "");
						}
						// logger.debug("data {} ", r);
						byte filterOption = Protocol.FilterSet.OP_PARTIAL;
						if (r.startsWith(AdvanceSearchParserUtil.NOT_EXPRESSION)) {
							filterOption = Protocol.FilterSet.OP_NOT;
							r = r.replaceAll(AdvanceSearchParserUtil.NOT_EXPRESSION, "");
						}
						// System.out.println("================> " + new
						// FilterSet(filterOption, eField.getFilterField(), r));
						this.filterSetList.add(new FilterSet(filterOption, eField.getFilterField(), r));
					}
				}
			}
		}
		additionalFilterSet();

		FilterSet[] fs = new FilterSet[filterSetList.size()];
		// logger.debug("filterSetList.size() {}",filterSetList.size());
		int cnt = 0;
		for (FilterSet f : filterSetList) {
			fs[cnt++] = f;
		}
		this.filterSetResult = fs;
		return this.filterSetResult;
	}

	/**
	 * 출원 기관 or 발명인 정보에 대해서는 사용자가 whereSet으로만 검색할 경우 Filtering을 통해 보다 정확한 검색 결과를
	 * 유도할 수 있게 한다.<br>
	 * 이유 : 색인 필드는 구분자에 의한 텀 단위의 색인이 지원하지 않으므로 이를 위해 filtering을 한다.
	 * 
	 * @author neon
	 * @date 2013. 9. 9.
	 */
	private void additionalFilterSet() {
		// makeAddFilter(EDQDocField.app, this.appWhereSetFilter);
		// makeAddFilter(EDQDocField.inv, this.invWhereSetFilter);
		// makeAddFilter(EDQDocField.app_gp, this.appWhereSetFilter);
		// makeAddFilter(EDQDocField.inv_gp, this.invWhereSetFilter);
	}

	private void makeAddFilter(EDQDocField field, LinkedList<List<String>> whereSetFilter) {
		logger.debug("add Filter {}", whereSetFilter);
		// if(true) return;
		List<String> matchTerm = new LinkedList<String>();
		List<String> prefixTerm = new LinkedList<String>();
		List<String> suffixTerm = new LinkedList<String>();
		for (List<String> l : whereSetFilter) {
			for (String term : l) {
				// logger.debug("whereSetFilter {}", term);
				term = term.replaceAll("\"", "");
				if (term.equalsIgnoreCase("OR") || term.equalsIgnoreCase("AND") || term.equalsIgnoreCase("NOT")) {
					continue;
				}
				if (term.endsWith("*")) {
					term = term.replaceAll("\\*", "");
					prefixTerm.add(term);
				} else if (term.startsWith("*")) {
					suffixTerm.add(term);
				} else {
					matchTerm.add(term);
				}
			}
		}
		String[] matchTermArr = new String[matchTerm.size()];
		boolean isNotFilter = false;
		for (int i = 0; i < matchTerm.size(); i++) {
			String s = matchTerm.get(i);
			if (s.contains("NOT")) {
				s = s.replaceAll(AdvanceSearchParserUtil.NOT_EXPRESSION, "");
				isNotFilter = true;
			}
			matchTermArr[i] = s;
		}

		if (matchTermArr.length > 0) {
			if (isNotFilter) {
				filterSetList.add(new FilterSet(Protocol.FilterSet.OP_NOT, field.getFilterField(), matchTermArr));
			} else {
				filterSetList.add(new FilterSet(Protocol.FilterSet.OP_PARTIAL, field.getFilterField(), matchTermArr));
			}
		}

		String[] prefixTermArr = new String[prefixTerm.size()];
		for (int i = 0; i < prefixTerm.size(); i++) {
			prefixTermArr[i] = prefixTerm.get(i);
		}
		if (prefixTermArr.length > 0) {
			filterSetList.add(new FilterSet(Protocol.FilterSet.OP_PARTIAL, field.getFilterField(), prefixTermArr));
			// filterSetList.add(new
			// FilterSet(Protocol.FilterSet.OP_PREFIX_MATCH,
			// field.getFilterField(), prefixTermArr));
		}
		String[] suffixTermArr = new String[suffixTerm.size()];
		for (int i = 0; i < suffixTerm.size(); i++) {
			suffixTermArr[i] = suffixTerm.get(i);
		}
		if (suffixTermArr.length > 0) {
			filterSetList.add(new FilterSet(Protocol.FilterSet.OP_PARTIAL, field.getFilterField(), suffixTermArr));
			// filterSetList.add(new
			// FilterSet(Protocol.FilterSet.OP_SUFFIX_MATCH,
			// field.getFilterField(), suffixTermArr));
		}
	}

	/**
	 * 가이드 검색에서 들어온 고급 검색식을 가이드 검색 폼 형태에 맞추어 데이터를 꺼낸다.<br>
	 * 
	 * @author neon
	 * @date 2013. 9. 23.
	 * @param searchAdvancedRule
	 * @return
	 */
	public static Map<String, String[]> extractGuideSearch(String searchAdvancedRule) {
		searchAdvancedRule = searchAdvancedRule.replaceAll(REGX3, " ");
		Map<String, String[]> r = new HashMap<String, String[]>();
		r.putAll(AdvanceSearchParserUtil.getFilterData(searchAdvancedRule));
		String removeFilterRule = AdvanceSearchParserUtil.removeFilterSearchRule(searchAdvancedRule);
		if ((removeFilterRule.indexOf(".ti.") != -1 && removeFilterRule.indexOf(".tie.") != -1)
				|| (removeFilterRule.indexOf(".ti.") != -1 && removeFilterRule.indexOf(".ab.") != -1)
				|| (removeFilterRule.indexOf(".ti.") != -1 && removeFilterRule.indexOf(".tie.") != -1 && removeFilterRule.indexOf(".ab.") != -1)) {
			removeFilterRule = removeFilterRule.replaceAll("\\(\\(", "\\(").replaceAll("\\.\\)", ". ").replaceAll(REGX3, " ");
		}
		// for (String k : r.keySet()) {
		// String[] a = r.get(k);
		// for (String g : a) {
		// System.out.println("key : " + k + "\t" + g);
		// }
		// }
		String[] rule = removeFilterRule.split("\\.\\s");
		for (String ru : rule) {
			// System.out.println("RULE : " + ru);
			if ("".equals(ru.trim()))
				continue;
			Matcher m = fieldPattern.matcher(ru);
			int startBrace = ru.indexOf("(") + 1;
			int closeBrace = ru.lastIndexOf(")");
			// System.out.println(closeBrace);
			if (startBrace == -1 || closeBrace == -1) {
				continue;
			}
			String s = ru.substring(startBrace, closeBrace);
			// System.out.println("RULE : " + s);
			// System.out.println(s);
			while (m.find()) {
				String f = m.group().trim();
				f = FieldConversion.conversionField(f);
				// System.out.println(f + " : " + s);
				r.put(f, new String[] { s });
			}
		}

		// char[] rules =

		return r;
	}

	public static void main(String... args) {
		System.setProperty("EJIANA_HOME", "E:\\project\\2014\\KISTI_SCOPUS_IBS_SEARCH\\resources");
		// String advancedSearch = " ((2010 OR 2009).pyear. ) ";
		String advancedSearch = "(@pyear>=2008)";
		// String advancedSearch =
		// "(@pyear>2008 (G).publ_type. @pyear<2010) AND (A).publ_type.";
		// String advancedSearch =
		// "(@pyear>2008 (G).publ_type. @pyear<2010) AND (A).publ_type.";
		// String advancedSearch =
		// "((\"touch display screen\").ti.) AND ((A).publ_type. (\"APPLE INC.\"
		// OR \"SHAMBAYATI MAZY\").app. ) ";
		// advancedSearch = "(@pyear>2008 @pyear<2010) AND ((KR).au. ) ";

		// advancedSearch =
		// " ((touch).ti. (KR).au. (G).publ_type. ) AND (film touch OR oled AND
		// (blue water) OR \"nano gre\").ti. OR (film touch OR oled AND (blue
		// water) OR \"nano gre\").abs. ";
		// advancedSearch =
		// "((touch).ti.ti_e. AND (KR).au. AND (G).publ_type. AND (G06F).ipc.)
		// NOT ((\"엘지.필립스 엘시디 주식회사\").app. )";
		advancedSearch = " (US OR EP OR WO OR JP OR KR OR GB).au. (\"LG ELECTRONICS INC. AND LG INNOTEK CO., LTD\" OR \"LG Electronices Inc.\").app.   ";
		// String advancedSearch =
		// " ((touch).ti. (KR).au. (G).publ_type.) AND ((2012 OR 2011).pyear. )
		// ";
		// String advancedSearch = " (film touch OR oled).ti.abs. ";
		advancedSearch = " (US OR EP OR WO OR JP OR KR OR GB).au. (film touch OR oled AND (blue water) OR \"nano ., inc. AND gre\").ti.abs.  ";
		// advancedSearch = " (A AND Bistom-Main).ti.abs. ";
		// advancedSearch =
		// "@appdate>=19960630 (\"the file\" OR (naver cmo)).abs.ti.
		// @appdate>=19960630<=19960701 @non_count>86520 @app=(1HOECHST
		// AKTIENGESELLSCHAFT -2UHDE GMBH) @non_count<1000 @non_count>3500<4000
		// @app=(neon sysnoym) @app=(title) @pndate>=(19960630)<=(19960701)
		// @ref_count>1500 (((first AND second).ti.abs.) AND ((local
		// history).ti.keyword.)) @pndate>=(20120630)<=(20130701)
		// @family_total_count>=(20)<(60) @claims_count=(5)
		// @independent_claims_count>(3) @cit_count<(300) @app=(GENERAL ELECTRIC
		// COMPANY) @app!=(CANON KABUSHIKI KAISHA) (\"daum net\" OR \"mbc
		// com\").abs. AND (\"olde display\" OR \"blue oled\").ti.";

		advancedSearch = "(((sound* OR noise* OR acoustic*) (apart* OR build*))).ti. @au=USA @pby>=1900<=2012 @kw=Buildings,Structural Design,Building,Office Buildings,Design,Project Management,Building Codes,Structural Analysis,Methodology,Optimization,Research,Tall Buildings,Architectural Design,In-buildings,Construction Industry,Civil Engineering,Building Materials @asjc=2215,2205,2200,2216,1909,1507,2213,2611,2500,1606,1600,2308,1408";
		advancedSearch = "(HIGHLY LUMINESCENT COLOR-SELECTIVE NANOCRYSTALLINE MATERIALS).ti.abs";
		advancedSearch = "((Traffic barr) AND (ier deployment system)).ti";
		advancedSearch = "(US OR EP OR WO OR JP OR KR OR GB).au. (\"ISHII SEIJI\").app.";
		advancedSearch = "(\"(주) 아성\").app.";
		advancedSearch = "(\"김승*\").inv.";
		advancedSearch = "(US OR EP OR WO OR JP OR KR OR GB).au. (\"NINOMIYA MAKOTO\").inv.";
		advancedSearch = "((US OR EP OR WO OR JP OR KR OR GB).au. (\"에릭슨 엘지 주식회사\" OR \"엘지 전자 주식회사\").app.)  AND ((\"IM, JIN SEOK\").app. ) ";
		// advancedSearch = "(엘지전자 (주)).app. ";
		// advancedSearch = "@app=(\"주식회사 엘지\" OR \"엘지 이노베이션즈 에이비\")";
		// advancedSearch = "@app=(\"(주) 아성\") (\"엘지전자 (주)\").app.";
		advancedSearch = "@app=(\"(주) (아성)\") (\"엘지전자 (주)\").app.";
		advancedSearch = "@app=(\"(주) 아성\") (\"엘지전자 (주)\").app.";
		advancedSearch = "(\"엘지전자 (주)\" AND \"(주) 아성\").app.";
		// advancedSearch = "@app=((주) (아성)) (\"엘지전자 (주)\").app.";
		// advancedSearch = "@app=((주) 아성) (엘지전자 (주)).app.";
		// advancedSearch = "@app=((주) 아성) (\"엘지전자 (주)\").app.";
		advancedSearch = "(((US OR EP OR WO OR JP OR KR OR GB).au. (B*).pnk.) AND ((KR OR (US)).au.)) AND ((삼성전자주식회사).app.)";
		// advancedSearch =
		// "(((US OR EP OR WO OR JP OR KR OR GB).au. (B*).pnk.) AND ((KR).au.))
		// AND ((삼성전자주식회사).app.)";
		// advancedSearch =
		// "((US OR EP OR WO OR JP OR KR OR GB).au. (B*).pnk.) AND ((\"SAMSUNG
		// ELECTRONICS CO., LTD.\").app.)";
		// advancedSearch =
		// "((US OR EP OR WO OR JP OR KR OR GB).au. (B*).pnk.) AND ((\"SAMSUNG
		// ELECTRONICS CO., LTD.\").app.)";
		// advancedSearch = "((\"home button\" OR touch)).ti.";
		// advancedSearch = "( ((\"home button\") OR (touch))).ti.";
		// advancedSearch = "(((\"home button\") OR (touch))).ti.";
		// advancedSearch ="(\"home button\" OR touch).ti.";

		// advancedSearch =
		// "((\"touch display screen\").ti.ab.) AND ((A).publ_type. (\"APPLE
		// INC.\" OR \"SHAMBAYATI MAZY\").app. ) ";
		// advancedSearch =
		// " ((touch).ti.ti_e. (KR).au. (G).publ_type. (G06F).ipc.) NOT ((2012
		// OR 2008).pyear. (G02F).ipc. )";
		advancedSearch = "@app=(\"(주) 아성\") (\"엘지전자 (주)\").app.inv.";
		advancedSearch = "((((US OR EP OR WO OR JP OR KR OR GB).au. (B*).pk.) AND ((KR).au.)) AND ((삼성전자주식회사).ap.)) AND ((US OR JP).an.)";
		// advancedSearch = "(KR).PN. (\"보던 케미칼 인코포레이티드\").APP.";
		// advancedSearch =
		// "(-20140505).pridate. (2005-2013).pr. @claims_count=5 (Y).andr.";
		// advancedSearch =
		// "(20140505-).pridate. (5-10).independent_claims_count. not
		// (-5).claims_count.";
		advancedSearch = "(-5).claims_count.au.";
		advancedSearch = "(title \"and\" oled).ti.";
		advancedSearch = "(USD0331058S OR USD0331059S and USD0331060S OR USD0331061S).pn.an.";
		advancedSearch = "(cell).ti. (US OR EP OR WO OR JP OR KR OR GB).au.";
		advancedSearch = "((touch).ti. (20130729-20140101).pndate.) AND ((2014).py.)";
		advancedSearch = " (USD0331058S OR USD0331059S and USD0331060S OR USD0331061S).pn.an. NOT ((WO).au.)";
		advancedSearch = " (((\"samsung electronic\").ap.) NOT ((CN).au.)) NOT ((\"SAMSUNG ELECTRONIC CO LTD\").ap.)";
		advancedSearch = "((\"opened home button\" OR touch)).ti.";
		advancedSearch = "(((anti-cheat*).ti. OR (anti-cheat).ab.) (US OR EP OR WO OR JP OR KR OR GB).au.) ((2016 OR 2015).pyear.)";
		// advancedSearch = "(2016 and 2015).py.";
		advancedSearch = "(oled).ti.";
		advancedSearch = "(\"Diesel Engine\").ab. OR (\"COMMON RAILS\").ab.";
		advancedSearch = "((anti-cheat*).ti. and (anti-cheat*).abs.)";
		advancedSearch = "(KR1020100087201).an. (20130729-).pndate.  (20130729-).pridate.  (20130729-).pndate3.";
		advancedSearch = "(H01L OR G06F).ipc. (\"SAMSUNG ELECTRONICS CO., LTD.\" OR \"SNU R&DB FOUNDATION\").appgpe. (\"경기도 수원시\" OR \"경기도 용인시\").app_sigungu. (\"경기도 수원시\" OR \"서울특별시 성동구\").hl_sigungu. AND ((삼성전자*).appgp.)";
		// advancedSearch = "(DM/088662).ipno3.";
		// advancedSearch = "((P).type.) AND ((등록공고).curlegaldesc.)";
		advancedSearch = "((touch).ti. and (20130729-20140101).pndate. ) AND ((2014).py.)";
		advancedSearch = "(touch).ti. not (20130729-20140101).pndate.";
		advancedSearch = "(KR).au. (\"SAMSUNG ELECT*\").ap.";

		advancedSearch = "(((touch).ti. OR (touch).abs.)  ((20130729-20140101).pridate. AND (20130729-20140101).pndate.)) AND ((2014).py.)";
		advancedSearch = "(((\"DSLR CAMERA\" OR \"MIRRORLESS CAMERA\") AND (( \"Telephoto Lens\" OR \"Super Telephoto Lens\") OR (\"Wide-angle Lens\" OR \"Ultra Wide Lens\"))).ti. OR ((\"DSLR CAMERA\" OR \"MIRRORLESS CAMERA\") AND (( \"Telephoto Lens\" OR \"Super Telephoto Lens\") OR (\"Wide-angle Lens\" OR \"Ultra Wide Lens\"))).ab.) (20000101-20161231).pndate.   (US OR EP OR WO OR CN OR JP OR KR OR DE OR FR OR GB OR CA).au. (NIKON* OR SONY* OR CANON* OR OLYMPUS* OR \"Eastman Kodak Company\").app.";
		advancedSearch = "((\"DSLR CAMERA\" OR \"MIRRORLESS CAMERA\").ti. OR (\"DSLR CAMERA\" OR \"MIRRORLESS CAMERA\").ab.) (20000101-20161231).pndate. (US OR EP OR WO OR CN OR JP OR KR OR DE OR FR OR GB OR CA).au.";
		advancedSearch = "((\"deep learn*\" or \"deep* network*\").ti. OR (\"deep learn*\" or \"deep* network*\").ab.) (US OR EP OR WO OR JP OR KR OR GB).au.";
		advancedSearch = "(((((\"IMAGE SENSOR\" AND Pixels) OR (LENS AND (( \"Telephoto Lens\" OR \"Super Telephoto Lens\") OR (\"Wide-angle Lens\" OR \"Ultra Wide Lens\")))).ti. OR ((\"IMAGE SENSOR\" AND Pixels) OR (LENS AND ((\"Telephoto Lens\" OR \"Super Telephoto Lens\") OR (\"Wide-angle Lens\" OR \"Ultra Wide Lens\")))).ab.) (19960101-).pndate.   (US OR EP OR WO OR CN OR JP OR KR OR DE OR FR OR GB OR CA OR DD OR TW).au. (G).publ_type. (KODAK* OR SONY* OR OLYMPUS* OR CANNON* OR NIKON* OR CASIO* OR PANASONIC* OR fujifilm* OR leica* OR RICHO* OR PENTAX* OR Hasselblad*).app_gp.) (3-).forward_citation_count.) AND (2000-2016).ay. AND (20150615).pndate.";
		advancedSearch = "(((oled).oled.).ti. not (20100101-20111231).pndate.) AND ((2011 OR 2010).py.)";
		advancedSearch = "((\"satellite\" AND (\"GPS\" OR (\"global positioning system\"))) OR (\"satellite navigation system\") OR \"GPS\" OR \"GNSS\").ab. (US).au. (G).publ_type.)";
		advancedSearch = QueryConverter.queryConversion(advancedSearch);

		System.out.println(advancedSearch);
		QueryConverter qc = new QueryConverter(advancedSearch, false);
		System.out.println("Query Conversion : " + qc.extractGuideSearch(advancedSearch));

		// String search =
		// AdvanceSearchParserUtil.removeFilterSearchRule(advancedSearch);
		// System.out.println("remove filter " + search);
		// List<String> fm =
		// AdvanceSearchParserUtil.getExtranctFilterItems(advancedSearch);
		// for (String s : fm) {
		// System.out.println("filter : " + s);
		// }
		//
		WhereSet[] ws = qc.getWhereSet();
		for (WhereSet f : ws) {
			System.out.println("WHERE SET => " + f.toString());
		}

		FilterSet[] fs = qc.getFilterSet();
		for (FilterSet f : fs) {
			System.out.println("FilterSet => " + f.toString());
		}

//		System.out.println(qc.getHighlightSearchTerms());
//		System.out.println(qc.getHighlightWildCardTerms());
//		System.out.println(qc.getHighlightField());
	}

//	@Override
//	public Set<EDQDocField> getHighlightField() {
//		return highlightField;
//	}
//
//	@Override
//	public Map<String, Set<String>> getHighlightSearchTerms() {
//		return highlightSearchTerms;
//	}
//
//	@Override
//	public Map<String, Set<String>> getHighlightWildCardTerms() {
//		return highlightWildCardTerms;
//	}

}
