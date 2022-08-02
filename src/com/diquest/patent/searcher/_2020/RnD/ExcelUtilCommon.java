package com.diquest.patent.searcher._2020.RnD;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtilCommon {

	FileOutputStream fs = null;
	FileInputStream inputStream = null;
	SXSSFWorkbook workbook;
	SXSSFSheet sheet = null;

	CellStyle style = null;
	private CreationHelper helper;

	public static void main(String... args) throws IOException {
		ExcelUtilCommon u = new ExcelUtilCommon();
		Map<Integer, LinkedList<Map<Integer, String>>> result = u.readExcel(new File(
				"D:\\eclipse_workspace\\workspace_luna\\public\\KISTI_PATENT_XML_EXPORT_2015_1\\2020.RnD_PIE_특허_논문_정량적_분석_지표\\(특허검색식)-소부장(기계금속)-이방래_20200109.xlsx"));
		
		
		for(Integer i : result.keySet()){
			LinkedList<Map<Integer, String>> list = result.get(i);
			for(Map<Integer, String> row : list ){
				System.out.println(row);
			}
		}
	}
	
	/**
	 * 검색식 대상이 되는 엑셀 파일을 읽는다
	 * 
	 * @author coreawin
	 * @date 2020. 1. 22.
	 * @param path
	 *            엑셀 경로
	 * @return Map 순서대로 키값 정의 FileName, SheetName, ColumnIdx <br>
	 */
	public Map<String, Map<Integer, LinkedList<Map<Integer, String>>>> readExcel(String path) {
		ExcelUtilCommon excelUtil = new ExcelUtilCommon();
		Map<String, Map<Integer, LinkedList<Map<Integer, String>>>> dataSet = new HashMap<String, Map<Integer, LinkedList<Map<Integer, String>>>>();
		Map<Integer, LinkedList<Map<Integer, String>>> result = null;
		File file = new File(path);
		File[] fileNameList = new File[] { file };
		if (file.isDirectory()) {
			fileNameList = file.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					if (name.endsWith("xlsx")) {
						return true;
					}
					return false;
				}
			});
		}

		for (File excelFile : fileNameList) {
			try {
				System.out.println(excelFile.getAbsolutePath());
				result = excelUtil.readExcel(excelFile);
				dataSet.put(excelFile.getName(), result);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(result!=null){
			for (Integer i : result.keySet()) {
				LinkedList<Map<Integer, String>> list = result.get(i);
				for (Map<Integer, String> row : list) {
					System.out.println(row);
				}
			}
		}
		return dataSet;
	}

	private void createHeaderStyle(XSSFWorkbook workbook) {
		style = workbook.createCellStyle();
		style.setWrapText(true);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
	}

	public LinkedHashMap<Integer, LinkedList<LinkedList<LinkedList<String>>>> generalReadExcel(File readFile)
			throws IOException {
		LinkedHashMap<Integer, LinkedList<LinkedList<LinkedList<String>>>> totalDataMap = new LinkedHashMap<Integer, LinkedList<LinkedList<LinkedList<String>>>>();

		LinkedList<LinkedList<LinkedList<String>>> rowDataMap = new LinkedList<LinkedList<LinkedList<String>>>();
		XSSFRow row;
		XSSFCell cell;
		XSSFWorkbook workbook = null;
		try {
			inputStream = new FileInputStream(readFile);
			System.out.println("read File " + readFile.getName());
			workbook = new XSSFWorkbook(inputStream);
			// sheet수 취득
			int sheetCn = workbook.getNumberOfSheets();
			System.out.println("sheet수 : " + sheetCn);
			for (int sheetIndex = 0; sheetIndex < sheetCn; sheetIndex++) {
				XSSFSheet sheet = workbook.getSheetAt(sheetIndex);
				// 취득된 sheet에서 rows수 취득
				int rows = sheet.getPhysicalNumberOfRows();
				System.out.println(workbook.getSheetName(sheetIndex) + " sheet의 row수 : " + rows);
				// 취득된 row에서 취득대상 cell수 취득
				rowDataMap = new LinkedList<LinkedList<LinkedList<String>>>();
				for (int rowIndex = 0; rowIndex < rows; rowIndex++) {
					row = sheet.getRow(rowIndex); // row 가져오기
					LinkedList<LinkedList<String>> rowData = new LinkedList<LinkedList<String>>();
					if (row != null) {
						int toatalColumnIndex = row.getLastCellNum();
						LinkedList<String> columnData = new LinkedList<String>();
						for (int columnIdx = 0; columnIdx <= toatalColumnIndex; columnIdx++) {
							cell = row.getCell(columnIdx);
							if (cell != null) {
								String value = null;
								switch (cell.getCellType()) {
								case XSSFCell.CELL_TYPE_FORMULA:
									value = cell.getCellFormula();
									break;
								case XSSFCell.CELL_TYPE_NUMERIC:
									value = "" + cell.getNumericCellValue();
									break;
								case XSSFCell.CELL_TYPE_STRING:
									value = "" + cell.getStringCellValue();
									break;
								case XSSFCell.CELL_TYPE_BLANK:
									value = "[null 아닌 공백]";
									break;
								case XSSFCell.CELL_TYPE_ERROR:
									value = "" + cell.getErrorCellValue();
									break;
								default:
								}
								if (value != null) {
									columnData.add(columnIdx, value.trim());
								} else {
								}
							}
						}
						rowData.add(columnData);
					}
					rowDataMap.add(rowData);
				}
				totalDataMap.put(sheetIndex, rowDataMap);
			}
			// System.out.println(eidMap);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
		return totalDataMap;
	}

	/**
	 * 엑셀에서 각 항목을 읽는다.<br>
	 * 
	 * @author coreawin
	 * @date 2020. 1. 22.
	 * @param readFile
	 * @return Key : 시트 인덱스<br>
	 *         value : Row <br>
	 *         key : Column Idx , value : Column Value<br>
	 * @throws IOException
	 */
	public Map<Integer, LinkedList<Map<Integer, String>>> readExcel(File readFile) throws IOException {
		/* key : sheetNo : value : row (column index , value) */
		Map<Integer, LinkedList<Map<Integer, String>>> sheetMap = new LinkedHashMap<Integer, LinkedList<Map<Integer, String>>>();
		XSSFRow row;
		XSSFCell cell;
		XSSFWorkbook workbook = null;
		try {
			inputStream = new FileInputStream(readFile);
			System.out.println("read File " + readFile.getName());
			workbook = new XSSFWorkbook(inputStream);
			// sheet수 취득
			int sheetCn = workbook.getNumberOfSheets();
			System.out.println("sheet수 : " + sheetCn);
			for (int sheetIndex = 0; sheetIndex < sheetCn; sheetIndex++) {
				XSSFSheet sheet = workbook.getSheetAt(sheetIndex);
				// 취득된 sheet에서 rows수 취득
				int rows = sheet.getPhysicalNumberOfRows();
				System.out.println(workbook.getSheetName(sheetIndex) + " sheet의 row수 : " + rows);

				// 취득된 row에서 취득대상 cell수 취득
				LinkedList<Map<Integer, String>> list = new LinkedList<Map<Integer, String>>();
				int idx = 0;
				for (int rowIndex = 1; rowIndex < rows; rowIndex++) {
					row = sheet.getRow(rowIndex); // row 가져오기
					if (row != null) {
						int columnNum = row.getLastCellNum();
						Map<Integer, String> rowData = new HashMap<Integer, String>();
						for (int colIdx = 0; colIdx < columnNum; colIdx++) {
							cell = row.getCell(colIdx);
							String value = "";
							if (cell != null) {
								switch (cell.getCellType()) {
								case XSSFCell.CELL_TYPE_FORMULA:
									value = cell.getCellFormula();
									break;
								case XSSFCell.CELL_TYPE_NUMERIC:
									value = "" + cell.getNumericCellValue();
									break;
								case XSSFCell.CELL_TYPE_STRING:
									value = "" + cell.getStringCellValue();
									break;
								case XSSFCell.CELL_TYPE_BLANK:
									value = "[null 아닌 공백]";
									break;
								case XSSFCell.CELL_TYPE_ERROR:
									value = "" + cell.getErrorCellValue();
									break;
								default:
								}
							}
							rowData.put(colIdx, value);
						}
						list.add(idx++, rowData);
					}
					sheetMap.put(sheetIndex, list);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
		return sheetMap;
	}

	public void flush() {
		try {
			workbook.write(fs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

	public void close() {
		if (fs != null)
			try {
				flush();
				fs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

}
