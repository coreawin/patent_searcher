package com.diquest.rule;

import java.util.Map;
import java.util.Set;

import com.diquest.ir.common.msg.protocol.query.FilterSet;
import com.diquest.ir.common.msg.protocol.query.WhereSet;
import com.diquest.util.EDQDocField;

public abstract class AbstractQueryConvertor {

	
	public abstract WhereSet[] getWhereSet(); 

	public abstract FilterSet[] getFilterSet();

	public abstract Map<String, Set<String>> getHighlightSearchTerms();

	public abstract Map<String, Set<String>> getHighlightWildCardTerms(); 
	
	public abstract Set<EDQDocField> getHighlightField();
	
}
