/**
 * 
 */
package com.diquest.rule;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 사용자가 입력한 고급 검색식의 항목을 검색이 가능한 항목으로 변경한다.<br>
 * 예 (home button).ab. => (home button).abs.
 * 
 * @author neon
 * @date 2013. 9. 13.
 * @Version 1.0
 */
public class FieldConversion {
	public static final Map<String, String> _CUSTOM_FIELD_INFO = new HashMap<String, String>();
	public static final Map<String, String> _CUSTOM_FIELD_INFO_REVERSE = new HashMap<String, String>();
	// abs_l, abs, abs_e, ti_l, ti, ti_e, pno, au, pndate, pnk, publ_type,
	// pyear, apo, apcn, apdate, apk, apfcn, apyear, prino, pricn, pridate,
	// pridate_last, pridate_first, priyear, ipcms, ipcmc, ipcmsc, ipcm, ipcrs,
	// ipcrc, ipcrsc, ipcr, ipcs, ipcc, ipcsc, ipc, cpcs, cpcc, cpcsc, cpc,
	// eclas, eclac, eclasc, ecla, national_cn, national_c, national_sc,
	// national, app_l, app, inv_l, inv, family_total_count, main_family_count,
	// mfpno, mfpdate, mfapdate, extended_family_count, cfpno, cfpdate,
	// cfapdate, citation_total_count, forward_citation_count, fcpno, fcpdate,
	// fcapdate, backward_citation_count, bcpno, bcpdate, bcapdate,
	// non_citation_count, non_patent, claims_count, independent_claims_count,
	// xml, fcode, ftheme, keyword, keyword2, idx_ti, idx_ti_e, idx_abs,
	// idx_abs_e, idx_keyword, idx_keyword2, idx_all, group_pyear,
	// group_publ_type, group_apfcn, group_ipcsc, group_cpcsc, group_app,
	// group_inv, sort_pndate, sort_pridate_last, sort_pridate_first,
	// sort_claims_count, sort_independent_claims_count,
	// sort_family_total_count, sort_forward_citation_count,
	// sort_backward_citation_count, sort_citation_total_count, total_count,
	// real_count, search_time, group_id, group_value, group_keyword, group_au,
	// group_app_cn, group_inv_cn, app_id, app_cn, inv_cn, app_gp, inv_gp;
	static {
		_CUSTOM_FIELD_INFO.put("anorm", "pno"); /* 출원번호 중복 제거 필드. */
		_CUSTOM_FIELD_INFO.put("ab", "abs");
		_CUSTOM_FIELD_INFO.put("pn", "pno");
		_CUSTOM_FIELD_INFO.put("pn2", "pno2");
		_CUSTOM_FIELD_INFO.put("pn3", "pno3");
		_CUSTOM_FIELD_INFO.put("py", "pyear");
		_CUSTOM_FIELD_INFO.put("pk", "pnk");
		_CUSTOM_FIELD_INFO.put("ip", "ipc");
		_CUSTOM_FIELD_INFO.put("cp", "cpc");
		_CUSTOM_FIELD_INFO.put("ec", "ecla");
		_CUSTOM_FIELD_INFO.put("ap", "app");
		_CUSTOM_FIELD_INFO.put("in", "inv");
		_CUSTOM_FIELD_INFO.put("pr", "priyear");
		_CUSTOM_FIELD_INFO.put("an", "apo");
		_CUSTOM_FIELD_INFO.put("ay", "apyear");
		_CUSTOM_FIELD_INFO.put("legal", "curlegalcode");
		_CUSTOM_FIELD_INFO.put("andr", "app_latest");
		

		Set<Entry<String, String>> es = _CUSTOM_FIELD_INFO.entrySet();
		for (Entry<String, String> e : es) {
			_CUSTOM_FIELD_INFO_REVERSE.put(e.getValue(), e.getKey());
		}
	}

	static Pattern fieldPattern = Pattern.compile("(?<=\\.)([a-z,A-Z,_]{1,})");

	public static void conversion(String aquery) {
		Matcher m = fieldPattern.matcher(aquery);
		while (m.find()) {
			System.out.println(m.group());
		}
	}

	/**
	 * 사용자 정의 필드명을 검색을 위한 검색식 필드명으로 교체한다.
	 * 
	 * @author neon
	 * @date 2013. 9. 13.
	 * @param field
	 * @return
	 */
	public static String conversionField(String field) {
		String s = _CUSTOM_FIELD_INFO.get(field);
		if (s == null) {
			s = field;
		}
		return s;
	}

	/**
	 * 사용자 정의 필드명을 검색을 위한 검색식 필드명으로 교체한다.
	 * 
	 * @author neon
	 * @date 2013. 9. 13.
	 * @param field
	 * @return
	 */
	public static String conversionField(String field, boolean isKpass) {
		String s = _CUSTOM_FIELD_INFO.get(field);

		if (s == null) {
			s = field;
		}
		if (isKpass) {
			if ("ay".equals(field) 
					|| "py".equals(field)
					|| "pk".equals(field)
					|| "an".equals(field)) {
				return field;
			}
		}
		return s;
	}

	/**
	 * 사용자 정의 필드명을 검색을 위한 검색식 필드명으로 교체한다.
	 * 
	 * @author neon
	 * @date 2013. 9. 13.
	 * @param field
	 * @return
	 */
	public static String conversionReverseField(String field) {
		String s = _CUSTOM_FIELD_INFO_REVERSE.get(field);
		if (s == null) {
			s = field;
		}
		return s;
	}

	public static void main(String... args) {
		String aquery = "((\"touch display screen\").TI.ab.) AND ((A).publ_type. (\"APPLE INC.\" OR \"SHAMBAYATI MAZY\").ap.  )  ";
		conversion(aquery);
	}

}
