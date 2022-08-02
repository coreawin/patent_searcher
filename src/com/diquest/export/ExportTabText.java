package com.diquest.export;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import com.diquest.tmp.KeywordExtractorWeb;
import com.diquest.util.PatentsDataRefiner;
import com.diquest.util.StringUtil;
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
public class ExportTabText implements ExportInfo {

//	DivisionFileWriter br = null;
	BufferedWriter br = null;

	AtomicInteger counter = null;
	private static long FILE_SIZE = 1024 * 1024 * 1024;
	public static String DATA_DELIMITER = ";";
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
	public ExportTabText(String makeFileName, String keywordExtractPath, Set<String> selectedCheck) throws IOException {
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
		System.out.println("ExportTabText.java > DATA_DELIMITER 구분자를 ; => ##으로 변경 ");
		File file = new File(this.makeFileName);
		if(file.isFile()==false){
			file.getParentFile().mkdirs();
		}
		br = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(this.makeFileName)),"UTF-8"));
//		br = new DivisionFileWriter(this.makeFileName, FILE_SIZE);
		writeHead();
	}

	protected String get(EXMLSchema se) {
		return this.data.getDatas(se);
	}

	protected void write() {
		sb.setLength(0);
		if (this.data == null)
			return;
		try {
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
			if (selectedCheck.contains(EXMLSchema.cpcsc.name())) {
				w(EXMLSchema.cpcsc.name(), PatentsDataRefiner.getCPC4InfoForExport(this.data));
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
			if (selectedCheck.contains(EXMLSchema.independent_claims.name())) {
				w("independent_claims", PatentsDataRefiner.getIndependendClaimsData(get(EXMLSchema.independent_claims), PatentsDataRefiner.getClaimsData(get(EXMLSchema.claims))));
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
			
			String assigneeCountryList = null;
			if (selectedCheck.contains("app_gp") || selectedCheck.contains("APP_GP")) {
				if (assigneeCnSet == null) {
					assigneeCnSet = PatentsDataRefiner.getLangData(PatentsDataRefiner.getAssigneeInventorMultiMapInfo(this.data, EXMLSchema.assignee, EXMLSchema.assignee, EXMLSchema.asscn));
				}
				assigneeCountryList = assigneeInventorCountry(assigneeCnSet);
				String assignee = this.data.additionDataMap.get("app_gp");

				// if (assignee != null && assigneeCountryList.length() > 0) {
				// String[] assignees = assignee.split(";");
				// String[] countries = assigneeCountryList.split(";");
				// if (countries.length > assignees.length) {
				// assignee = assigneeInventorName(assigneeCnSet);
				// }
				// }

				String mdd = mergingDelimitedData(assigneeCountryList, assignee);
				if ("".equals(mdd)) {
					w("app_gp", assigneeCnSet);
				} else {
					w("app_gp", mdd);
				}
			}

			/*
			 * Multimap<String, String> multiMap =
			 * PatentsDataRefiner.getAgentInfo(this.data); for (String k :
			 * multiMap.keySet()) { tmpSet.clear();
			 * tmpSet.addAll(multiMap.get(k)); w(k, tmpSet); }
			 */
			w();
		} catch (Exception e) {
			System.out.println("PNO : " + get(EXMLSchema.pno));
			e.printStackTrace();
		}
	}

	protected int getInt(EXMLSchema se) {
		return this.data.getDatasInt(se);
	}

	private void writeHead() throws IOException {
		sb.setLength(0);
		if (br != null) {
			w(EXMLSchema.pno.name());

			if (selectedCheck.contains(EXMLSchema.dockind.name())) {
				w(EXMLSchema.dockind.name());
			}

			if (selectedCheck.contains(EXMLSchema.pndate.name())) {
				w("pnyear");
				w(EXMLSchema.pndate.name());
			}
			if (selectedCheck.contains(EXMLSchema.pnkind.name())) {
				w(EXMLSchema.pnkind.name());
			}
			if (selectedCheck.contains(EXMLSchema.pncn.name())) {
				w("authority");
			}

			if (selectedCheck.contains(EXMLSchema.ti.name())) {
				w(EXMLSchema.ti.name());
			}
			if (selectedCheck.contains(EXMLSchema.abs.name())) {
				w(EXMLSchema.abs.name());
			}
			if (selectedCheck.contains(EXMLSchema.appno.name())) {
				w(EXMLSchema.appno.name());
			}
			if (selectedCheck.contains(EXMLSchema.appdate.name())) {
				w("appyear");
				w(EXMLSchema.appdate.name());
			}
			if (selectedCheck.contains(EXMLSchema.prino.name())) {
				w("firstpriyear");
				w("priyear");
				w(EXMLSchema.prino.name());
			}
			if (selectedCheck.contains(EXMLSchema.inventor.name())) {
				w(EXMLSchema.inventor.name());
				w("inventor-count");
			}
			if (selectedCheck.contains(EXMLSchema.assignee.name())) {
				w(EXMLSchema.assignee.name());
				w("assignee-count");
			}

//			if (selectedCheck.contains("app_gp") || selectedCheck.contains("APP_GP")) {
//				w("app_gp");
//			}
			
			if (selectedCheck.contains(EXMLSchema.independent_claims.name())) {
				w(EXMLSchema.independent_claims.name());
			}

			if (selectedCheck.contains(EXMLSchema.pexam.name())) {
				w("examiner");
			}
			if (selectedCheck.contains("keyword")) {
				w("keyword");
			}
			if (selectedCheck.contains("basic_count")) {
				w("claims-count");
				w("independent-claims-count");
			}
			if (selectedCheck.contains(EXMLSchema.nonpatent.name())) {
				w(EXMLSchema.nonpatent.name());
			}
			if (selectedCheck.contains(EXMLSchema.references.name())) {
				w(EXMLSchema.references.name());
			}
			if (selectedCheck.contains(EXMLSchema.citations.name())) {
				w(EXMLSchema.citations.name());
			}
			if (selectedCheck.contains("ref_count")) {
				w("total-references-cited-count");
				w("reference-count");
				w("citation-count");
				w("non-patent-count");
			}
			if (selectedCheck.contains(EXMLSchema.ipc.name())) {
				w(EXMLSchema.ipc.name());
			}
			if (selectedCheck.contains(EXMLSchema.cpc.name())) {
				w(EXMLSchema.cpc.name());
			}
			if (selectedCheck.contains(EXMLSchema.cpcsc.name())) {
				w(EXMLSchema.cpcsc.name());
			}
			if (selectedCheck.contains(EXMLSchema.ecla.name())) {
				w(EXMLSchema.ecla.name());
			}
			
			if (selectedCheck.contains(EXMLSchema.classc.name())) {
				w(EXMLSchema.classc.name());
			}
			if (selectedCheck.contains(EXMLSchema.fcode.name())) {
				w(EXMLSchema.fcode.name());
			}
			
			if (selectedCheck.contains(EXMLSchema.mf.name())) {
				w(EXMLSchema.mf.name());
			}
			if (selectedCheck.contains(EXMLSchema.cf.name())) {
				w(EXMLSchema.cf.name());
			}
			
			if (selectedCheck.contains("family_count")) {
				w("total-family-count");
				w("main-family-count");
				w("extended-family-count");
			}
			
			if (selectedCheck.contains("app_gp") || selectedCheck.contains("APP_GP")) {
				w("app_gp");
			}

			if (selectedCheck.contains(EXMLSchema.hcp_rank.name())) {
				w(EXMLSchema.hcp_rank.name());
			}
		}
		try {
			w();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
			for (String s : contents) {
				s = s.trim();
				buf.append(s.replaceAll(DATA_DELIMITER, ","));
				buf.append(DATA_DELIMITER);
			}
			if (buf.length() > 0) {
				for(int idx =0; idx<DATA_DELIMITER.length(); idx++){
					buf.deleteCharAt(buf.length() - 1);
				}
			}
			buf.append(TAB_DELIMITER);
			sb.append(buf.toString());
		} catch (NullPointerException e) {
			throw new Exception(e);
		}
	}

	StringBuffer sb = new StringBuffer();

	private void w(String field, String contents) {
		sb.append(StringUtil.nullCheck(contents));
		sb.append(TAB_DELIMITER);
	}

	private void w(String contents) {
		sb.append(StringUtil.nullCheck(contents));
		sb.append(TAB_DELIMITER);
	}

	private void w() throws Exception {
		try {
			if (br != null) {
				br.write(sb.toString().trim() + ENTER);
			}
		} catch (NullPointerException e) {
			throw new Exception(e);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void w(EXMLSchema se) {
		sb.append(get(se));
		sb.append(TAB_DELIMITER);
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
