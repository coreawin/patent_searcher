package com.diquest.rule;

import java.util.HashSet;
import java.util.Set;

import com.diquest.util.EDQDocField;

/**
 * 검색 항목을 정의한다.<br>
 * @author neon
 * @date   2013. 8. 9.
 * @Version 1.0
 */
public class SearchDefinition {
	
	public static final Set<EDQDocField> _FILTER_FIELD_LIST = new HashSet<EDQDocField>();
	public static final Set<EDQDocField> _NOT_FILTER_FIELD_LIST = new HashSet<EDQDocField>();
	static{
		setupFilterField();
	}
	
	/**
	 * 필드 정보를 매칭한다.
	 * @author neon
	 * @date   2013. 8. 9.
	 */
	private static void setupFilterField(){
		
		_NOT_FILTER_FIELD_LIST.add(EDQDocField.dappgp);
		_NOT_FILTER_FIELD_LIST.add(EDQDocField.appgpe);
		_NOT_FILTER_FIELD_LIST.add(EDQDocField.dappgpk);
		_NOT_FILTER_FIELD_LIST.add(EDQDocField.app_gp);
		_NOT_FILTER_FIELD_LIST.add(EDQDocField.app);
		_NOT_FILTER_FIELD_LIST.add(EDQDocField.inv);
		_NOT_FILTER_FIELD_LIST.add(EDQDocField.keyword);
		_NOT_FILTER_FIELD_LIST.add(EDQDocField.app_cn);
		_NOT_FILTER_FIELD_LIST.add(EDQDocField.inv_cn);
		_NOT_FILTER_FIELD_LIST.add(EDQDocField.inv_gp);
		_NOT_FILTER_FIELD_LIST.add(EDQDocField.keyword);
		
		
		_FILTER_FIELD_LIST.add(EDQDocField.iay);
		_FILTER_FIELD_LIST.add(EDQDocField.ipy);
		_FILTER_FIELD_LIST.add(EDQDocField.ipy3);
		_FILTER_FIELD_LIST.add(EDQDocField.ay);
		_FILTER_FIELD_LIST.add(EDQDocField.py);
		_FILTER_FIELD_LIST.add(EDQDocField.py2);
		_FILTER_FIELD_LIST.add(EDQDocField.py3);
		_FILTER_FIELD_LIST.add(EDQDocField.pndate2);
		_FILTER_FIELD_LIST.add(EDQDocField.pndate3);
		_FILTER_FIELD_LIST.add(EDQDocField.iapdate);
		_FILTER_FIELD_LIST.add(EDQDocField.ipndate);
		_FILTER_FIELD_LIST.add(EDQDocField.ipndate3);
		
//		_FILTER_FIELD_LIST.add(EDQDocField.pyear);
//		_FILTER_FIELD_LIST.add(EDQDocField.priyear);
		_FILTER_FIELD_LIST.add(EDQDocField.pndate);
		_FILTER_FIELD_LIST.add(EDQDocField.apdate);
		_FILTER_FIELD_LIST.add(EDQDocField.pridate);
		_FILTER_FIELD_LIST.add(EDQDocField.family_total_count);
		_FILTER_FIELD_LIST.add(EDQDocField.main_family_count);
		_FILTER_FIELD_LIST.add(EDQDocField.extended_family_count);
		_FILTER_FIELD_LIST.add(EDQDocField.citation_total_count);
		_FILTER_FIELD_LIST.add(EDQDocField.forward_citation_count);
		_FILTER_FIELD_LIST.add(EDQDocField.backward_citation_count);
		_FILTER_FIELD_LIST.add(EDQDocField.non_citation_count);
		_FILTER_FIELD_LIST.add(EDQDocField.claims_count);
		_FILTER_FIELD_LIST.add(EDQDocField.independent_claims_count);
	}
	
}
