package com.diquest.export;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.diquest.coreawin.common.divisible.DivisionFileWriter;
import com.diquest.tmp.KeywordExtractorWeb;
import com.diquest.util.PatentsDataRefiner;
import com.diquest.util.StringUtil;
import com.diquest.util.cleansing.AssigneeCleansing;
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
public class ExportMultiValueLine implements ExportInfo {

	DivisionFileWriter br = null;

	AtomicInteger counter = null;
	private static int FILE_SIZE = 1024 * 1024 * 512;

	public static final String TAB_DELIMITER = "\t";
	public static final String ENTER = "\n";

	private String makeFileName;
	private String keywordExtractPath;

	private PatentDataMaps data;

	private Set<String> selectedCheck;
	protected AssigneeCleansing cleansing = AssigneeCleansing.getInstance();
	LinkedList<PatentDataMaps> addDataMaps = new LinkedList<PatentDataMaps>();
	StringBuffer sb = new StringBuffer();

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
	 * @throws Exception
	 */
	public ExportMultiValueLine(String makeFileName, String keywordExtractPath, Set<String> selectedCheck) throws Exception {
		if (!selectedCheck.contains(EXMLSchema.citations.name()) && !selectedCheck.contains(EXMLSchema.mf.name()) && !selectedCheck.contains(EXMLSchema.cf.name()) && !selectedCheck.contains(EXMLSchema.references.name())) {
			throw new Exception("MIULTI VALUE 정보는 CITATION, REFERENCE, Main Family, Extended Faimily만 지원합니다.");
		}

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
		br = new DivisionFileWriter(this.makeFileName, FILE_SIZE);
		writeHead();
	}

	protected String get(EXMLSchema se) {
		return this.data.getDatas(se);
	}

	protected void write() {
		if (this.data == null)
			return;
		try {
			Set<String> contents = Collections.emptySet();
			if (selectedCheck.contains(EXMLSchema.references.name())) {
				contents = PatentsDataRefiner.getReferenceInfoForExport(this.data);
				// w(EXMLSchema.references.name(),
				// PatentsDataRefiner.getReferenceInfoForExport(this.data));
			}
			if (selectedCheck.contains(EXMLSchema.citations.name())) {
				contents = PatentsDataRefiner.getCitationInfoForExport(this.data);
				// w(EXMLSchema.citations.name(),
				// PatentsDataRefiner.getCitationInfoForExport(this.data));
			}
			if (selectedCheck.contains(EXMLSchema.mf.name())) {
				contents = PatentsDataRefiner.getMainFamilyInfo(this.data);
				// w(EXMLSchema.mf.name(),
				// PatentsDataRefiner.getMainFamilyInfo(this.data));
			}
			if (selectedCheck.contains(EXMLSchema.cf.name())) {
				contents = PatentsDataRefiner.getCompleteFamilyInfo(this.data);
				// w(EXMLSchema.cf.name(),
				// PatentsDataRefiner.getCompleteFamilyInfo(this.data));
			}

			if (!contents.isEmpty()) {
				for (String content : contents) {
					sb.setLength(0);
					String[] contentInfo = content.split("@@", -1);
					w(EXMLSchema.pno);
					for (String c : contentInfo) {
						w(c);
					}
					w();
				}
			} else {
				sb.setLength(0);
				w(EXMLSchema.pno);
				w("");
				w("");
				w("");
				w();
			}
		} catch (Exception e) {
			System.out.println("PNO : " + get(EXMLSchema.pno));
			e.printStackTrace();
		}
	}

	protected int getInt(EXMLSchema se) {
		return this.data.getDatasInt(se);
	}

	private void writeHead() throws IOException {
		if (br != null) {
			sb.setLength(0);
			w(EXMLSchema.pno.name());
			if (selectedCheck.contains(EXMLSchema.citations.name())) {
				w("citDocPno");
				w("pub-date");
				w("app-date");
			} else if (selectedCheck.contains(EXMLSchema.mf.name())) {
				w("mfDocPno");
				w("pub-date");
				w("app-date");
			} else if (selectedCheck.contains(EXMLSchema.cf.name())) {
				w("cfDocPno");
				w("pub-date");
				w("app-date");
			} else if (selectedCheck.contains(EXMLSchema.references.name())) {
				w("refDocPno");
				w("pub-date");
				w("app-date");
				w("srep-phase");
			}
			try {
				w();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	StringBuffer buf = new StringBuffer();

	private void w(String field, Set<String> contents) throws Exception {
		buf.setLength(0);
		try {
			for (String s : contents) {
				buf.append(s.replaceAll(";", ","));
				buf.append(";");
			}
			if (buf.length() > 0) {
				buf.deleteCharAt(buf.length() - 1);
			}
			buf.append(TAB_DELIMITER);
			sb.append(buf.toString());
		} catch (NullPointerException e) {
			throw new Exception(e);
		}
	}

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
			sb.setLength(0);
		} catch (NullPointerException e) {
			throw new Exception(e);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void w(EXMLSchema se) throws Exception {
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
