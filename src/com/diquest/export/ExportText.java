package com.diquest.export;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.NumberFormat;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import com.diquest.k.web.util.Utility;
import com.diquest.tmp.KeywordExtractorWeb;
import com.diquest.util.PatentsDataRefiner;
import com.diquest.util.cleansing.AssigneeCleansing;
import com.diquest.util.xml.PatentDataFormat;
import com.diquest.util.xml.PatentDataMaps;
import com.diquest.util.xml.PatentSchema.EXMLSchema;

/**
 * Text 파일로 데이터를 Export한다. <br>
 * 분석도구 KITAS에서 인식한다.
 * 
 * @author neon
 * @date 2013. 6. 20.
 * @Version 1.0
 */
public class ExportText implements ExportInfo {

//	DivisionFileWriter br = null;
	BufferedWriter br = null;

	private static final String MULTI_VALUE_DELIM = "↔";

	AtomicInteger counter = null;
	private static long FILE_SIZE = 1024 * 1024 * 1024;
	public static final String TAB_DELIMITER = "\t";
	public static final String ENTER = "\n";

	private String makeFileName;
	private String keywordExtractPath;

	private PatentDataMaps data;

	private Set<String> selectedCheck;
	protected AssigneeCleansing cleansing = AssigneeCleansing.getInstance();
	LinkedList<PatentDataMaps> addDataMaps = new LinkedList<PatentDataMaps>();

	public void setData(PatentDataMaps data) {
		this.data = data;
		write();
	}

	// public void writeData(PatentDataMaps data) {
	// this.data = data;
	// addDataMaps.add(data);
	// write();
	// }

	/**
	 * @param makeFileName
	 *            생성 파일 이름
	 * @param selectedCheck
	 *            생성할 필드.
	 * @param hcpInfo
	 * @throws IOException
	 */
	public ExportText(String makeFileName, String keywordExtractPath, Set<String> selectedCheck) throws IOException {
		this.makeFileName = makeFileName;
		this.selectedCheck = selectedCheck;

		this.keywordExtractPath = keywordExtractPath;
		counter = new AtomicInteger(0);
		initDownloadFile();
	}

	/**
	 * @throws IOException
	 */
	void initDownloadFile() throws IOException {
		File file = new File(this.makeFileName);
		if(file.isFile()==false){
			file.getParentFile().mkdirs();
		}
		br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(this.makeFileName)),"UTF-8"));
//		br = new DivisionFileWriter(this.makeFileName, FILE_SIZE);
	}

	protected String get(EXMLSchema se) {
		return this.data.getDatas(se);
	}

	protected void write() {
		sb.setLength(0);
		if (this.data == null)
			return;
		try {
			w("-SEQ", NumberFormat.getInstance().format(counter.incrementAndGet()));
			w(EXMLSchema.pno);
			if (selectedCheck.contains(EXMLSchema.dockind.name())) {
				w(EXMLSchema.dockind.name(), get(EXMLSchema.dockind));
			}

			if (selectedCheck.contains(EXMLSchema.pndate.name())) {
				w("pnyear", PatentDataFormat.extractDateYear(get(EXMLSchema.pndate)));
				w(EXMLSchema.pndate.name(), PatentDataFormat.convertDateHippen(get(EXMLSchema.pndate)));
			}
			if (selectedCheck.contains(EXMLSchema.pnkind.name())) {
				w(EXMLSchema.pnkind.name(), get(EXMLSchema.pnkind));
			}
			if (selectedCheck.contains(EXMLSchema.pncn.name())) {
				w("authority", get(EXMLSchema.pncn));
			}

			String engTitle = getDefaultLangData("eng", EXMLSchema.tilang, EXMLSchema.ti);
			String engAbs = getDefaultLangData("eng", EXMLSchema.abslang, EXMLSchema.abs);
			if (selectedCheck.contains(EXMLSchema.ti.name())) {
				w(EXMLSchema.ti.name(), engTitle);
			}
			if (selectedCheck.contains(EXMLSchema.abs.name())) {
				w(EXMLSchema.abs.name(), engAbs);
			}
			if (selectedCheck.contains(EXMLSchema.appno.name())) {
				w(EXMLSchema.appno);
			}
			
//			System.out.println("AA " + EXMLSchema.appdate.name() +"\t" + selectedCheck.contains(EXMLSchema.appdate.name()) +"\t" + selectedCheck);
			if (selectedCheck.contains(EXMLSchema.appdate.name())) {
				w("appyear", PatentDataFormat.extractDateYear(get(EXMLSchema.appdate)));
				w(EXMLSchema.appdate.name(), PatentDataFormat.convertDateHippen(get(EXMLSchema.appdate)));
			}
			if (selectedCheck.contains(EXMLSchema.prino.name())) {
				TreeSet<String> priYears = PatentsDataRefiner.getPriorityDateInfoForExport(this.data);
				String fpy = "";
				if (priYears.size() > 0) {
					fpy = priYears.first();
				}
				w("firstpriyear", fpy);
				w("priyear", priYears);
				w(EXMLSchema.prino.name(), PatentsDataRefiner.getPriorityInfoForExport(this.data));
			}
			if (selectedCheck.contains(EXMLSchema.inventor.name())) {
				Set<String> set = PatentsDataRefiner.getLangData(PatentsDataRefiner.getAssigneeInventorMultiMapInfo(this.data, EXMLSchema.inventor, EXMLSchema.inventor, EXMLSchema.invcn));
				w(EXMLSchema.inventor.name(), set);
				w("inventor-count", String.valueOf(set.size()));
			}
			Set<String> assigneeCnSet = null;
			if (selectedCheck.contains(EXMLSchema.assignee.name())) {
				Set<String> set = PatentsDataRefiner.getLangData(PatentsDataRefiner.getAssigneeInventorMultiMapInfo(this.data, EXMLSchema.assignee, EXMLSchema.assignee, EXMLSchema.asscn));
				w(EXMLSchema.assignee.name(), set);
				w("assignee-count", String.valueOf(set.size()));
			}

			if (selectedCheck.contains(EXMLSchema.pexam.name())) {
				w("examiner", PatentsDataRefiner.getPrimaryExaminerInfo(this.data));
			}
			if (selectedCheck.contains("keyword")) {
				w("keyword", getKeyword(engTitle, engAbs));
			}
			if (selectedCheck.contains("basic_count")) {
				w("claims-count", String.valueOf(getInt(EXMLSchema.numclaims)));
				w("independent-claims-count", String.valueOf(getInt(EXMLSchema.numindclaims)));
			}
			if (selectedCheck.contains(EXMLSchema.nonpatent.name())) {
				w(EXMLSchema.nonpatent.name(), PatentsDataRefiner.getNonPatentForExport(this.data));
			}
			if (selectedCheck.contains(EXMLSchema.references.name())) {
				w(EXMLSchema.references.name(), PatentsDataRefiner.getReferenceInfoForExport(this.data));
			}
			if (selectedCheck.contains(EXMLSchema.citations.name())) {
				w(EXMLSchema.citations.name(), PatentsDataRefiner.getCitationInfoForExport(this.data));
			}
			if (selectedCheck.contains("ref_count")) {
				int non = PatentsDataRefiner.getNonPatentForExport(this.data).size();
				int ref = PatentsDataRefiner.getReferenceInfoForExport(this.data).size();
				int cit = PatentsDataRefiner.getCitationInfoForExport(this.data).size();
				w("total-references-cited-count", String.valueOf(non + ref + cit));
				w("reference-count", String.valueOf(ref));
				w("citation-count", String.valueOf(cit));
				w("non-patent-count", String.valueOf(non));
			}
			if (selectedCheck.contains(EXMLSchema.ipc.name())) {
				w(EXMLSchema.ipc.name(), PatentsDataRefiner.getIPCFullInfo(this.data));
			}
			if (selectedCheck.contains(EXMLSchema.cpc.name())) {
				w(EXMLSchema.cpc.name(), PatentsDataRefiner.getCPCFullInfo(this.data));
			}
			if (selectedCheck.contains(EXMLSchema.ecla.name())) {
				w(EXMLSchema.ecla.name(), PatentsDataRefiner.getECLAFullInfo(this.data));
			}
			if (selectedCheck.contains(EXMLSchema.classc.name())) {
				Set<String> nationalMain = PatentsDataRefiner.getUSMainInfo(this.data);
				nationalMain.addAll(PatentsDataRefiner.getUSFurtherInfo(this.data));
				w("national", nationalMain);
			}
			if (selectedCheck.contains(EXMLSchema.fcode.name())) {
				w("fterm", PatentsDataRefiner.getFTermInfo(this.data));
			}
			if (selectedCheck.contains(EXMLSchema.mf.name())) {
				w(EXMLSchema.mf.name(), PatentsDataRefiner.getMainFamilyInfo(this.data));
			}
			if (selectedCheck.contains(EXMLSchema.cf.name())) {
				w(EXMLSchema.cf.name(), PatentsDataRefiner.getCompleteFamilyInfo(this.data));
			}
			if (selectedCheck.contains("family_count")) {
				int mf = PatentsDataRefiner.getMainFamilyInfoForExport(this.data).size();
				int cf = PatentsDataRefiner.getCompleteFamilyInfoForExport(this.data).size();
				w("total-family-count", String.valueOf(cf));
				w("main-family-count", String.valueOf(mf));
				w("extended-family-count", String.valueOf(cf));
			}
			
			if (selectedCheck.contains(EXMLSchema.hcp_rank.name())) {
				double hcpRanking = 0d;
				w(EXMLSchema.hcp_rank.name(), Utility.pointCutFormat(hcpRanking));
			}
			
			if (selectedCheck.contains(EXMLSchema.cur_assignee.name())) {
				w(EXMLSchema.cur_assignee.name(), get(EXMLSchema.cur_assignee).replaceAll(MULTI_VALUE_DELIM, ";"));
			}
			
			if (selectedCheck.contains(EXMLSchema.cur_assignee_standard.name())) {
				w(EXMLSchema.cur_assignee_standard.name(), get(EXMLSchema.cur_assignee_standard).replaceAll(MULTI_VALUE_DELIM, ";"));
			}
			
			if (selectedCheck.contains(EXMLSchema.cur_assignee_normalized.name())) {
				w(EXMLSchema.cur_assignee_normalized.name(), get(EXMLSchema.cur_assignee_normalized).replaceAll(MULTI_VALUE_DELIM, ";"));
			}
			
			if (selectedCheck.contains(EXMLSchema.cur_assignee_from_date.name())) {
				w(EXMLSchema.cur_assignee_from_date.name(), get(EXMLSchema.cur_assignee_from_date).replaceAll(MULTI_VALUE_DELIM, ";"));
			}
			
			if (selectedCheck.contains(EXMLSchema.is_change_assignee.name())) {
				String appStandard = get(EXMLSchema.app_standardized).replaceAll("<\\d>", "");
				String curStandard = get(EXMLSchema.cur_assignee_standard);
				String[] curStandards = curStandard.split(MULTI_VALUE_DELIM);
				
				String isChangeAssignee = "N";
				for(String cs : curStandards) {
					if(!appStandard.equals(cs)) {
						isChangeAssignee = "Y";
					}
				}
				
				w(EXMLSchema.is_change_assignee.name(), isChangeAssignee);
			}
			
			if (selectedCheck.contains(EXMLSchema.assignee_list.name())) {
				w(EXMLSchema.assignee_list.name(), get(EXMLSchema.assignee_list).replaceAll(MULTI_VALUE_DELIM, ";"));
			}
			
			if (selectedCheck.contains(EXMLSchema.status.name())) {
				w(EXMLSchema.status.name(), get(EXMLSchema.status).replaceAll(MULTI_VALUE_DELIM, ";"));
			}
			
			if (selectedCheck.contains(EXMLSchema.status_date.name())) {
				w(EXMLSchema.status_date.name(), get(EXMLSchema.status_date).replaceAll(MULTI_VALUE_DELIM, ";"));
			}
			
			if (selectedCheck.contains(EXMLSchema.fees.name())) {
				w(EXMLSchema.fees.name(), get(EXMLSchema.fees).replaceAll(MULTI_VALUE_DELIM, ";"));
			}
			
			if (selectedCheck.contains(EXMLSchema.fees_detail.name())) {
				w(EXMLSchema.fees_detail.name(), get(EXMLSchema.fees_detail).replaceAll(MULTI_VALUE_DELIM, ";"));
			}
			
			if (selectedCheck.contains(EXMLSchema.fees_date.name())) {
				w(EXMLSchema.fees_date.name(), get(EXMLSchema.fees_date).replaceAll(MULTI_VALUE_DELIM, ";"));
			}

		} catch (Exception e) {
			System.out.println("PNO : " + get(EXMLSchema.pno));
			e.printStackTrace();
		}
	}

	protected int getInt(EXMLSchema se) {
		return this.data.getDatasInt(se);
	}


	StringBuffer buf = new StringBuffer();
	
	public void write(String src){
		try {
			br.write(src);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void w(String field, Set<String> contents) throws Exception {
		buf.setLength(0);
		try {
			buf.append(field);
			buf.append("|");
			for (String s : contents) {
				buf.append(s.replaceAll(";", ","));
				buf.append(";");
			}
			if (buf.length() > 0) {
				buf.deleteCharAt(buf.length() - 1);
			}
			buf.append("\n");
			if (br != null) {
				br.write(buf.toString());
			}
		} catch (NullPointerException e) {
			throw new Exception(e);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	StringBuffer sb = new StringBuffer();

	private void w(String field, String contents) throws Exception {
		try {
			if (br != null) {
				br.write(field + "|" + contents + "\n");
			}
		} catch (NullPointerException e) {
			throw new Exception(e);
		} catch (IOException e) {
			e.printStackTrace();
		}
//		sb.append(StringUtil.nullCheck(contents));
//		sb.append(TAB_DELIMITER);
	}


	protected void w(EXMLSchema se) throws Exception {
		try {
			if (br != null) {
				br.write(se.name() + "|" + get(se) + "\n");
			}
		} catch (NullPointerException e) {
			throw new Exception(e);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 문서 데이터를 기록한다.<br>
	 */
	public void flush() {
		if (br != null) {
			try {
				br.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 문서 stream을 닫는다.<br>
	 */
	public void close() {
		if (br != null) {
			try {
				br.flush();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

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
			keywordSet = KeywordExtractorWeb.getInstance(keywordExtractPath).getKeywordSet(new String[] { ti, abs }, new int[] { 10000, 1 });
			for (String k : keywordSet) {
				buffer.append(k.split("_")[0]);
				buffer.append(";");
			}
			if (buffer.length() > 0) {
				buffer.deleteCharAt(buffer.length() - 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buffer.toString();
	}
}
