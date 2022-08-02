package com.diquest.export;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.diquest.tmp.KeywordExtractorWeb;
import com.diquest.util.PatentsDataRefiner;
import com.diquest.util.cleansing.AssigneeCleansing;
import com.diquest.util.xml.PatentDataFormat;
import com.diquest.util.xml.PatentDataMaps;
import com.diquest.util.xml.PatentSchema.EXMLSchema;

/**
 * 엑셀 파일로 데이터를 Export한다. <br>
 * 
 * @author neon
 * @date 2013. 6. 20.
 * @Version 1.0
 */
public class ExportExcel implements ExportInfo{

	BufferedOutputStream bos = null;
	Sheet sh = null;
	SXSSFWorkbook wb = null;
	CellStyle style = null;
	Font font = null;
	protected PatentDataMaps data = null;

	public void setData(PatentDataMaps data) {
		this.data = data;
		setRowInfo();
		write();
	}

	AtomicInteger counter = null;

	private AtomicInteger rowIndex = new AtomicInteger(0);
	private AtomicInteger columnIndex = new AtomicInteger(0);
	private Set<String> selectedCheck;
	protected AssigneeCleansing cleansing = AssigneeCleansing.getInstance();
	private String keywordExtractPath;

	public ExportExcel(String makeFileName, String keywordExtractPath, Set<String> selectedCheck) {
		this.selectedCheck = selectedCheck;
		counter = new AtomicInteger(0);
		this.keywordExtractPath = keywordExtractPath;
		// keep 100 rows in memory, exceeding rows will be flushed to disk
		wb = new SXSSFWorkbook(100);
		sh = wb.createSheet();
		setupStyle();
		setupFont();
		
		try {
			bos = new BufferedOutputStream(new FileOutputStream(makeFileName));
			createHeader();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void setFileName(String makeFileName) {
		try {
			bos = new BufferedOutputStream(new FileOutputStream(makeFileName));
			createHeader();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setupStyle() {
		style = wb.createCellStyle();
	}

	private void setupFont() {
		Font font = wb.createFont();
		font.setFontName("Consolas");
	}

	protected CellStyle getDefaultHeaderCellStyle(short align) {
		return getDefaultHeaderCellStyle(align, (short) -1);
	}

	protected CellStyle getDefaultHeaderCellStyle(short align, short color) {
		// Font font = wb.createFont();
		// font.setFontName("Consolas");
		// CellStyle style = wb.createCellStyle();
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setAlignment(align);
		if (color != -1) {
			style.setFillBackgroundColor(color);
		}
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setFont(font);
		return style;
	}

	protected CellStyle getDefaultCellStyle(short align) {
		// Font font = wb.createFont();
		// font.setFontName("Consolas");
		// CellStyle style = wb.createCellStyle();
		style.setAlignment(align);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setFont(font);
		return style;
	}

	protected CellStyle getNumberCellStyle(short align) {
		// Font font = wb.createFont();
		// font.setFontName("Consolas");
		// CellStyle style = wb.createCellStyle();
		style.setAlignment(align);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		style.setFont(font);
		return style;
	}

	private Cell createCell(Row row, int column, CellStyle style, String value) {
		Cell cell = row.createCell(column);
		cell.setCellStyle(style);
		cell.setCellValue(value);
		return cell;
	}

	private Cell createNumberCell(Row row, int column, CellStyle style, String value) {
		Cell cell = row.createCell(column);
		cell.setCellStyle(style);
		try {
			cell.setCellType(Cell.CELL_TYPE_NUMERIC);
			cell.setCellValue(Integer.parseInt(value));
		} catch (Exception e) {
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue(value);
		}
		return cell;
	}

	Row createHeader() {
		Row r = sh.createRow(rowIndex.getAndIncrement());
		columnIndex.set(0);

		int piStartIdx = 0;
		int riStartIdx = 25;
		int ciStartIdx = 32;
		int fiStartIdx = 35;
		int totalIdx = 39;

		sh.addMergedRegion(new CellRangeAddress(0, 0, piStartIdx, riStartIdx - 1));
		sh.addMergedRegion(new CellRangeAddress(0, 0, riStartIdx, ciStartIdx - 1));
		sh.addMergedRegion(new CellRangeAddress(0, 0, ciStartIdx, fiStartIdx - 1));
		sh.addMergedRegion(new CellRangeAddress(0, 0, fiStartIdx, totalIdx));
		r.setHeightInPoints(20);
		createCell(r, piStartIdx, getDefaultHeaderCellStyle(CellStyle.ALIGN_CENTER), "Patent Info");
		createCell(r, riStartIdx, getDefaultHeaderCellStyle(CellStyle.ALIGN_CENTER), "Reference Info");
		createCell(r, ciStartIdx, getDefaultHeaderCellStyle(CellStyle.ALIGN_CENTER), "Classification Info");
		createCell(r, fiStartIdx, getDefaultHeaderCellStyle(CellStyle.ALIGN_CENTER), "Family Info");
		sh.setZoom(4, 5);

		r = sh.createRow(rowIndex.getAndIncrement());
		columnIndex.set(0);
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL), "seq");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL),
				"publication-number");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL), "documents-kind");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL), "publication-year");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL), "publication-date");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL), "kind");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL), "authority");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL), "ti");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL), "abs");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL),
				"application-number");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL), "application-year");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL), "application-date");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL),
				"first-priority-years");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL), "priority-years");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL), "priority-numbers");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL), "inventor");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL), "inventor-country");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL), "inventor-count");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL), "assignee");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL), "assignee-country");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL), "assignee-count");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL), "examiner");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL), "keyword");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL), "claims-count");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL),
				"independent-claims-count");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL), "non-patent");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL), "references");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL), "citations");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL),
				"total-reference-count");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL), "reference-count");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL), "citation-count");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL), "non-patent-count");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL), "ipc");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL), "cpc");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL), "ecla");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL), "main family");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL), "extended family");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL),
				"total-family-count");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL), "main-family-count");
		createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL),
				"extended-family-count");
		if (selectedCheck.contains("app_gp")) {
			createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL), "app_gp");
		}

		if (selectedCheck.contains("usipc")) {
			createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL), "usipc");
		}

		if (selectedCheck.contains(EXMLSchema.hcp_rank.name())) {
			createCell(r, columnIndex.getAndIncrement(), getDefaultHeaderCellStyle(CellStyle.ALIGN_GENERAL), "hcp_rank");
		}
		return r;
	}

	void writeCell(Row r, String v) {
		createCell(r, columnIndex.getAndIncrement(), getDefaultCellStyle(CellStyle.ALIGN_GENERAL), v);
	}

	void writeNumberCell(Row r, String v) {
		createNumberCell(r, columnIndex.getAndIncrement(), getDefaultCellStyle(CellStyle.ALIGN_GENERAL), v);
	}

	StringBuffer sb = new StringBuffer();

	void writeCell(Row r, Set<String> set) {
		sb.setLength(0);
		for (String s : set) {
			sb.append(s);
			sb.append(";");
		}
		if (sb.length() > 1) {
			writeCell(r, sb.substring(0, sb.length() - 1));
		} else {
			writeCell(r, "");
		}
	}

	public void setRowInfo() {
		int row = sh.getLastRowNum();
		if (row == 1000001) {
			rowIndex.set(0);
			sh = wb.createSheet();
			createHeader();
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
			keywordSet = KeywordExtractorWeb.getInstance(keywordExtractPath).getKeywordSet(new String[] { ti, abs },
					new int[] { 10000, 1 });
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

	protected String get(EXMLSchema se) {
		return this.data.getDatas(se);
	}

	protected void write() {
		if (this.data == null)
			return;
		try {
			Row r = sh.createRow(rowIndex.getAndIncrement());
			columnIndex.set(0);
			writeCell(r, NumberFormat.getInstance().format(counter.incrementAndGet()));
			writeCell(r, get(EXMLSchema.pno));
			if (selectedCheck.contains(EXMLSchema.dockind.name())) {
				writeCell(r, get(EXMLSchema.dockind));
			} else {
				writeBlackCell(r);
			}
			if (selectedCheck.contains(EXMLSchema.pndate.name())) {
				writeCell(r, PatentDataFormat.extractDateYear(get(EXMLSchema.pndate)));
				writeCell(r, PatentDataFormat.convertDateHippen(get(EXMLSchema.pndate)));
			} else {
				writeBlackCell(r);
				writeBlackCell(r);
			}
			if (selectedCheck.contains(EXMLSchema.pnkind.name())) {
				writeCell(r, get(EXMLSchema.pnkind));
			} else {
				writeBlackCell(r);
			}
			if (selectedCheck.contains(EXMLSchema.pncn.name())) {
				writeCell(r, get(EXMLSchema.pncn));
			} else {
				writeBlackCell(r);
			}

			Map<String, String> titleData = PatentsDataRefiner.getLangTextInfo(this.data, new EXMLSchema[] {
					EXMLSchema.tilang, EXMLSchema.ti }, EXMLSchema.tilang, EXMLSchema.ti);
			Map<String, String> absData = PatentsDataRefiner.getLangTextInfo(this.data, new EXMLSchema[] {
					EXMLSchema.abslang, EXMLSchema.abs }, EXMLSchema.abslang, EXMLSchema.abs);
			String engTitle = titleData.remove("eng");
			String engAbs = absData.remove("eng");
			if (selectedCheck.contains("original_lang")) {
				StringBuffer _buf = new StringBuffer();
				if (selectedCheck.contains(EXMLSchema.ti.name())) {
					_buf.setLength(0);
					if (engTitle != null) {
						_buf.append(engTitle);
						_buf.append("\n");
					}
					writeCell(r, getDatas(_buf, titleData, "\n"));
				} else {
					writeBlackCell(r);
				}

				if (selectedCheck.contains(EXMLSchema.abs.name())) {
					_buf.setLength(0);
					if (engAbs != null) {
						_buf.append(engAbs);
						_buf.append("\n");
					}
					writeCell(r, getDatas(_buf, absData, "\n"));
				} else {
					writeBlackCell(r);
				}
				_buf.setLength(0);
			} else {
				if (selectedCheck.contains(EXMLSchema.ti.name())) {
					writeCell(r, engTitle);
				} else {
					writeBlackCell(r);
				}
				if (selectedCheck.contains(EXMLSchema.abs.name())) {
					writeCell(r, engAbs);
				} else {
					writeBlackCell(r);
				}
			}

			/*
			 * String engTitle = getDefaultLangData("eng", EXMLSchema.tilang,
			 * EXMLSchema.ti); if (selectedCheck.contains(EXMLSchema.ti.name()))
			 * { writeCell(r, engTitle); } else { writeBlackCell(r); } String
			 * engAbs = getDefaultLangData("eng", EXMLSchema.abslang,
			 * EXMLSchema.abs); if
			 * (selectedCheck.contains(EXMLSchema.abs.name())) { writeCell(r,
			 * engAbs); } else { writeBlackCell(r); }
			 */
			if (selectedCheck.contains(EXMLSchema.appno.name())) {
				writeCell(r, get(EXMLSchema.appno));
			} else {
				writeBlackCell(r);
			}
			if (selectedCheck.contains(EXMLSchema.appdate.name())) {
				writeCell(r, PatentDataFormat.extractDateYear(get(EXMLSchema.appdate)));
				writeCell(r, PatentDataFormat.convertDateHippen(get(EXMLSchema.appdate)));
			} else {
				writeBlackCell(r);
				writeBlackCell(r);
			}
			if (selectedCheck.contains(EXMLSchema.prino.name())) {
				TreeSet<String> priYears = PatentsDataRefiner.getPriorityDateInfoForExport(this.data);
				String fpy = "";
				if (priYears.size() > 0) {
					fpy = priYears.first();
				}
				writeCell(r, fpy);
				writeCell(r, priYears);
				writeCell(r, PatentsDataRefiner.getPriorityInfoForExport(this.data));
			} else {
				writeBlackCell(r);
				writeBlackCell(r);
				writeBlackCell(r);
			}
			if (selectedCheck.contains(EXMLSchema.inventor.name())) {
				Set<String> set = PatentsDataRefiner.getLangData(PatentsDataRefiner.getAssigneeInventorMultiMapInfo(
						this.data, EXMLSchema.inventor, EXMLSchema.inventor, EXMLSchema.invcn));
				writeCell(r, set);
				writeCell(r, assigneeInventorCountry(set));
				writeNumberCell(r, String.valueOf(set.size()));
			} else {
				writeBlackCell(r);
				writeBlackCell(r);
				writeBlackCell(r);
			}
			Set<String> assigneeCnSet = null;
			String assigneeCountryList = null;
			if (selectedCheck.contains(EXMLSchema.assignee.name())) {
				assigneeCnSet = PatentsDataRefiner.getLangData(PatentsDataRefiner.getAssigneeInventorMultiMapInfo(this.data,
						EXMLSchema.assignee, EXMLSchema.assignee, EXMLSchema.asscn));
				assigneeCountryList = assigneeInventorCountry(assigneeCnSet);
				writeCell(r, assigneeCnSet);
				writeCell(r, assigneeCountryList);
				writeNumberCell(r, String.valueOf(assigneeCnSet.size()));
			} else {
				writeBlackCell(r);
				writeBlackCell(r);
				writeBlackCell(r);
			}
			if (selectedCheck.contains(EXMLSchema.pexam.name())) {
				writeCell(r, PatentsDataRefiner.getPrimaryExaminerInfo(this.data));
			} else {
				writeBlackCell(r);
			}
			if (selectedCheck.contains("keyword")) {
				writeCell(r, getKeyword(get(EXMLSchema.ti), get(EXMLSchema.abs)));
			} else {
				writeBlackCell(r);
			}
			if (selectedCheck.contains("basic_count")) {
				writeNumberCell(r, String.valueOf(getInt(EXMLSchema.numclaims)));
				writeNumberCell(r, String.valueOf(getInt(EXMLSchema.numindclaims)));
			} else {
				writeBlackCell(r);
				writeBlackCell(r);
			}
			if (selectedCheck.contains(EXMLSchema.nonpatent.name())) {
				writeCell(r, PatentsDataRefiner.getNonPatentForExport(this.data));
			} else {
				writeBlackCell(r);
			}
			if (selectedCheck.contains(EXMLSchema.references.name())) {
				writeCell(r, PatentsDataRefiner.getReferenceInfoForExport(this.data));
			} else {
				writeBlackCell(r);
			}
			if (selectedCheck.contains(EXMLSchema.citations.name())) {
				writeCell(r, PatentsDataRefiner.getCitationInfoForExport(this.data));
			} else {
				writeBlackCell(r);
			}
			if (selectedCheck.contains("ref_count")) {
				int non = PatentsDataRefiner.getNonPatentForExport(this.data).size();
				int ref = PatentsDataRefiner.getReferenceInfoForExport(this.data).size();
				int cit = PatentsDataRefiner.getCitationInfoForExport(this.data).size();
				writeNumberCell(r, String.valueOf(non + ref + cit));
				writeNumberCell(r, String.valueOf(ref));
				writeNumberCell(r, String.valueOf(cit));
				writeNumberCell(r, String.valueOf(non));
			} else {
				writeBlackCell(r);
				writeBlackCell(r);
				writeBlackCell(r);
				writeBlackCell(r);
			}
			if (selectedCheck.contains(EXMLSchema.ipc.name())) {
				writeCell(r, PatentsDataRefiner.getIPCFullInfo(this.data));
			} else {
				writeBlackCell(r);
			}
			if (selectedCheck.contains(EXMLSchema.cpc.name())) {
				writeCell(r, PatentsDataRefiner.getCPCFullInfo(this.data));
			} else {
				writeBlackCell(r);
			}
			if (selectedCheck.contains(EXMLSchema.ecla.name())) {
				writeCell(r, PatentsDataRefiner.getECLAFullInfo(this.data));
			} else {
				writeBlackCell(r);
			}
			if (selectedCheck.contains(EXMLSchema.mf.name())) {
				writeCell(r, PatentsDataRefiner.getMainFamilyInfoForExport(this.data));
			} else {
				writeBlackCell(r);
			}
			if (selectedCheck.contains(EXMLSchema.cf.name())) {
				writeCell(r, PatentsDataRefiner.getCompleteFamilyInfoForExport(this.data));
			} else {
				writeBlackCell(r);
			}
			if (selectedCheck.contains("family_count")) {
				int mf = PatentsDataRefiner.getMainFamilyInfoForExport(this.data).size();
				int cf = PatentsDataRefiner.getCompleteFamilyInfoForExport(this.data).size();
				writeNumberCell(r, String.valueOf(cf));
				writeNumberCell(r, String.valueOf(mf));
				writeNumberCell(r, String.valueOf(cf));
			} else {
				writeBlackCell(r);
				writeBlackCell(r);
				writeBlackCell(r);
			}

			if (selectedCheck.contains("app_gp")) {
				if (assigneeCnSet == null) {
					assigneeCnSet = PatentsDataRefiner.getLangData(PatentsDataRefiner.getAssigneeInventorMultiMapInfo(
							this.data, EXMLSchema.assignee, EXMLSchema.assignee, EXMLSchema.asscn));
					assigneeCountryList = assigneeInventorCountry(assigneeCnSet);
				}
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
					writeCell(r, PatentsDataRefiner.getLangData(PatentsDataRefiner.getAssigneeInventorMultiMapInfo(
							this.data, EXMLSchema.assignee, EXMLSchema.assignee, EXMLSchema.asscn)));
				} else {
					writeCell(r, mdd);
				}

			}

			if (selectedCheck.contains("usipc")) {
				writeCell(r, PatentsDataRefiner.getUSMainInfoExport(this.data));
			}

			if (selectedCheck.contains(EXMLSchema.hcp_rank.name())) {
				// double hcpRanking = getHcpRankInfo(get(EXMLSchema.pno));
				writeNumberCell(r, "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected int getInt(EXMLSchema se) {
		return this.data.getDatasInt(se);
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
	 * @author neon
	 * @date 2013. 7. 4.
	 * @param r
	 */
	private void writeBlackCell(Row r) {
		writeCell(r, "");
	}

	public void flush() {
		if (bos != null) {
			try {
				bos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void close() {
		if (bos != null) {
			try {
				wb.write(bos);
				bos.flush();
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
		return _buf.toString();
	}

}
