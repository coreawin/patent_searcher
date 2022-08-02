package com.diquest.export;

import java.io.File;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.diquest.k.web.management.bean.KindBean;
import com.diquest.k.web.management.common.DescriptionData;
import com.diquest.k.web.management.dao.HCPInfoDAO;
import com.diquest.k.web.tmp.KeywordExtractorWeb;
import com.diquest.k.web.util.PatentsDataRefiner;
import com.diquest.k.web.util.cleansing.AssigneeCleansing;
import com.diquest.k.web.util.xml.PatentDataMaps;
import com.diquest.k.web.util.xml.PatentSchema.EXMLSchema;

/**
 * 특허 문서 다운로드 <br>
 * 다운로드 문서를 위한 추상 클래스
 * 
 * @author neon
 * @date 2013. 6. 20.
 * @Version 1.0
 */
public abstract class ExportDocument {

	final static String TAB_DELIMITER = "\t";
	final static String ENTER = "\n";

	protected HashMap<String, Boolean> exportField = new HashMap<String, Boolean>();
	protected PatentDataMaps data = null;
	protected File writeFile = null;
	protected NumberFormat nf = NumberFormat.getInstance();

	protected Set<String> selectedCheck = null;
	protected Map<String, Map<String, KindBean>> kindDataMap = DescriptionData.getKindBeans();
	protected AssigneeCleansing cleansing = AssigneeCleansing.getInstance();
	private HCPInfoDAO hcpInfo;

	protected ExportDocument(String makeFileName) {
		this(makeFileName, null, null);
	}

	/**
	 * 
	 * 특허 발행 국가별 공보코드에 대한 상세설명을 가져온다.
	 *
	 * @author coreawin
	 * @date 2015. 11. 9.
	 * @version 1.0
	 * @param authority
	 *            특허 발행 국가
	 * @param kind
	 *            특허 공보 코드
	 * @return
	 */
	protected String getKindDesc(String authority, String kind) {
		String desc = "";
		try {
			Map<String, KindBean> kinds = kindDataMap.get(authority);
			desc = kinds.get(kind).getDifferenceDescription();
		} catch (Exception e) {
			// ignore
		}
		return desc;
	}

	/**
	 * @param makeFileName
	 * @param selectedCheck
	 *            소문자로 항목이 입력되어야 한다.
	 * @param hcpInfo
	 */
	protected ExportDocument(String makeFileName, Set<String> selectedCheck, HCPInfoDAO hcpInfo) {
		this.hcpInfo = hcpInfo;
		this.writeFile = new File(makeFileName);
		this.selectedCheck = selectedCheck;
		setFieldInit();
		setExportField();
	}

	/**
	 * 
	 * 특허 문서에 대해서 Export 대상 필드 항목을 초기화한다.
	 *
	 * @author coreawin
	 * @date 2015. 11. 9.
	 * @version 1.0
	 */
	private void setFieldInit() {
	}

	/**
	 * 익스포트할 필드를 설정한다.
	 */
	void setExportField() {
	}

	/**
	 * 각 포맷에 맞도록 해당 데이터를 기록한다.
	 */
	protected abstract void write();

	/**
	 * 
	 * Export 대상 특허 문서를 파일에 기록한다.
	 *
	 * @author coreawin
	 * @date 2015. 11. 9.
	 * @version 1.0
	 * @param d
	 *            파싱 완료된 특허 문헌 RAW-DATA 데이터
	 */
	public void exportData(PatentDataMaps d) {
		this.data = d;
		write();
	}

	protected String get(EXMLSchema se) {
		return this.data.getDatas(se).replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
	}

	protected String getAdditionData(String key) {
		return this.data.additionDataMap.get(key);
	}

	/**
	 * 키워드 목록을 얻는다.
	 * 
	 * @author neon
	 * @date 2013. 9. 27.
	 * @param ti
	 *            제목
	 * @param abs
	 *            초록
	 * @return
	 */
	protected String getKeyword(String ti, String abs) {
		Set<String> keywordSet = new LinkedHashSet<String>();
		StringBuffer buffer = new StringBuffer();
		try {
			String[] keywords = new String[0];
			if (abs == null && ti == null) {
				return buffer.toString();
			} else if (abs == null && ti != null) {
				keywords = new String[] { ti };
			} else if (abs != null && ti == null) {
				keywords = new String[] { abs };
			} else {
				keywords = new String[] { ti, abs };
			}

			keywordSet = KeywordExtractorWeb.getInstance().getKeywordSet(keywords, new int[] { 10000, 1 });
			for (String k : keywordSet) {
				buffer.append(k.split("_")[0]);
				buffer.append(";");
			}

			if (buffer.length() > 0) {
				if (buffer.toString().endsWith(";")) {
					buffer.deleteCharAt(buffer.length() - 1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer.toString();
	}

	protected int getInt(EXMLSchema se) {
		return this.data.getDatasInt(se);
	}

	/**
	 * 문서 데이터를 기록한다.<br>
	 */
	public abstract void flush();

	/**
	 * 문서 stream을 닫는다.<br>
	 */
	public abstract void close();

	/**
	 * 멀티 언어로 구성된 데이터에서 우선적으로 추출할 언어를 설정한다.<br>
	 * 우선적으로 추출할 언어가 없다면 랜덤으로 언어가 추출된다. <br>
	 * 
	 * @author neon
	 * @date 2013. 7. 4.
	 * @param priLang
	 *            우선적으로 추출할 언어
	 * @param langField
	 *            언어 필드
	 * @param dataField
	 *            데이터 필드
	 * @return
	 */
	protected String getDefaultLangData(String priLang, EXMLSchema langField, EXMLSchema dataField) {
		String data = PatentsDataRefiner.getLangTextInfo(this.data, priLang, langField, dataField);
		if ("".equals(data)) {
			LinkedHashSet<String> langSet = PatentsDataRefiner.multiValue(this.data.getDatas(langField));
			for (String lang : langSet) {
				lang = lang.substring(lang.length() - 3);
				data = PatentsDataRefiner.getLangTextInfo(this.data, lang, langField, dataField);
				if (!"".equals(data)) {
					break;
				}
			}
		}
		return data == null ? "" : data;
	}

	/**
	 * 
	 * 상호 분리된 2개의 데이터 항목을 하나의 필드 데이터 항목으로 합치는 작업을 수행한다.
	 *
	 * @author coreawin
	 * @date 2015. 11. 9.
	 * @version 1.0
	 * @param first
	 *            Target1 항목 데이터
	 * @param second
	 *            Target2 항목 데이터
	 * @return
	 */
	protected String mergingDelimitedData(String first, String second) {
		StringBuffer b = new StringBuffer();
		if (first == null && second != null) {
			String[] s = second.split(";");
			for (int idx = 0; idx < s.length; idx += 1) {
				b.append(s[idx]);
				b.append('`');
				b.append(s[idx]);
				b.append(';');
			}
		} else {
			if (second == null)
				return second;
			if ("".equals(first))
				return second;
			if ("".equals(second))
				return "";

			String[] f = first.split(";");
			String[] s = second.split(";");
			int size = Math.min(f.length, s.length);

			for (int idx = 0; idx < size; idx += 1) {
				b.append(f[idx]);
				b.append('`');
				b.append(s[idx]);
				b.append(';');
			}
		}
		if (b.length() > 0) {
			b.deleteCharAt(b.length() - 1);
		}
		return b.toString();
	}

	/**
	 * 
	 * 상호 분리된 2개의 데이터 항목을 하나의 필드 데이터 항목으로 합치는 작업을 수행한다.
	 *
	 * @author coreawin
	 * @date 2015. 11. 9.
	 * @version 1.0
	 * @param first
	 *            Target1 항목 데이터
	 * @param second
	 *            Target2 항목 데이터
	 * @return
	 */
	protected String mergingDelimitedDataForWord(String first, String second) {
		StringBuffer b = new StringBuffer();
		if (first == null) {
			String[] s = second.split(";");
			for (int idx = 0; idx < s.length; idx += 1) {
				b.append(s[idx]);
				b.append(';');
			}
		} else {
			if (second == null)
				return second;
			if ("".equals(first))
				return second;
			if ("".equals(second))
				return "";

			String[] f = first.split(";");
			String[] s = second.split(";");
			for (int idx = 0; idx < f.length; idx += 1) {
				b.append(f[idx]);
				b.append('`');
				b.append(s[idx]);
				b.append(';');
			}
		}
		if (b.length() > 0) {
			b.deleteCharAt(b.length() - 1);
		}
		return b.toString();
	}

	StringBuilder _tmp = new StringBuilder();

	/**
	 * 
	 * 특허 데이터의 발명인, 출원인 국적 정보를 출력한다.
	 *
	 * @author coreawin
	 * @date 2015. 11. 9.
	 * @version 1.0
	 * @param datas
	 *            특허 출원인, 발명인 항목(국적`발명인(또는 출원인))
	 * @return
	 */
	protected String assigneeInventorCountry(Set<String> datas) {
		// Set<String> sets = new LinkedHashSet<String>();
		_tmp.setLength(0);
		for (String d : datas) {
			String[] dcn = d.split(String.valueOf(PatentsDataRefiner.INVENTOR_DELIM));
			if (dcn.length > 0) {
				_tmp.append(dcn[0]);
				_tmp.append(";");
			}
		}
		if (_tmp.length() > 0) {
			_tmp.deleteCharAt(_tmp.length() - 1);
		}
		return getCheckDelimiter(_tmp.toString());
	}

	/**
	 * 
	 * 특허 데이터의 발명인, 출원인 이름 정보를 출력한다.
	 *
	 * @author coreawin
	 * @date 2015. 11. 9.
	 * @version 1.0
	 * @param datas
	 *            특허 출원인, 발명인 항목(국적`발명인(또는 출원인))
	 * @return
	 */
	protected String assigneeInventorName(Set<String> datas) {
		// Set<String> sets = new LinkedHashSet<String>();
		cleansing.init();
		_tmp.setLength(0);
		for (String d : datas) {
			String[] dcn = d.split(String.valueOf(PatentsDataRefiner.INVENTOR_DELIM));
			if (dcn.length > 1) {
				_tmp.append(cleansing.ruleKor(dcn[1]));
				_tmp.append(";");
			}
		}
		if (_tmp.length() > 0) {
			_tmp.deleteCharAt(_tmp.length() - 1);
		}
		return getCheckDelimiter(_tmp.toString());
	}

	String getCheckDelimiter(String src) {
		String r = src.replaceAll("[;`]", "");
		if ("".equals(r)) {
			return r;
		} else {
			return src;
		}
	}

	protected String getDatas(StringBuffer _buf, Map<String, String> data, String enter) {
		Set<String> keySet = data.keySet();
		for (String key : keySet) {
			String value = data.get(key);
			_buf.append(key.toUpperCase());
			_buf.append("=");
			_buf.append(value);
			_buf.append(enter);
		}
		String str = _buf.toString().replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "");
		return str;
	}

	/**
	 * 
	 * 특허 발행번호에 대해서 HCP Rank 정보를 가져온다. (미국특허 대상)
	 * 
	 * @author coreawin
	 * @date 2015. 11. 9.
	 * @version 1.0
	 * @param pno
	 *            특허 발행번호
	 * @return
	 */
	protected double getHcpRankInfo(String pno) {
		if (hcpInfo != null) {
			try {
				return hcpInfo.getHCPRanking(pno);
			} catch (Exception e) {
			}
		}
		return 0.0;
	}
}
