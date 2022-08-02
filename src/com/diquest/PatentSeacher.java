package com.diquest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.diquest.bean.PatentFullData;
import com.diquest.export.XMLWrite;
import com.diquest.search.SearchAuthority;

public class PatentSeacher {

	static Logger logger = LoggerFactory.getLogger(PatentSeacher.class);
	public static Map<String, PatentFullData> threadAuthroMap = new LinkedHashMap<String, PatentFullData>();
	/**
	 * 
	 * 사용자가 입력한 Authorty에 대해서 해당 target 디렉토리에 데이터를 Export를 수행한다.
	 *
	 * @author 이관재
	 * @date 2015. 8. 13.
	 * @version 1.0
	 * @param args
	 *            0. au, <br>
	 *            1. target Directory = > ex) /home/data/patent/{AU}
	 * @throws Exception
	 */
	private static final String[] AUTHORITIES = { "US", "EP", "WO", "CN", "JP", "KR", "DE", "FR", "GB", "CA", "AR", "AT", "AU", "BE", "BR", "CH",
			"DD", "DK", "EA", "ES", "FI", "IE", "IN", "IT", "LU", "MC", "MX", "NL", "PT", "RU", "SE", "SU", "TW" };

	public static void main(String[] args) throws Exception {
		
//		-cn1000
//		-au
//		-tgi:/kisti/
//		-ctt:/release/KISTI_PATENT_XML_EXPORT_2015/result
//		-ftTEXT
//		특허번호, 등록일, 출원인(회사명), 출원인국가, 특허분류코드(IPC), CPC, 피인용회수(Forward)
//		-efpno,pndate,assignee,app_gp,basic_count,ipc,cpc
//
//		-ru((US).au. (Y).aplt. (1976-2017).py. (G).PUBL_TYPE. )
//
//		-ket:/release/KISTI_PATENT_XML_EXPORT_2015
//		-bwy
		args = new String[]{
				"-cn5000",
				"-au",
				"-tgc:/data/",
				"-ctt:/release/KISTI_PATENT_XML_EXPORT_2015/result",
				"-ftTEXT",
				"-efpno,pndate,assignee,app_gp,basic_count,ipc,cpc",
				"-ru((US).au. (Y).aplt. (1990-2017).py. (G).PUBL_TYPE. )",
				"-ket:/release/KISTI_PATENT_XML_EXPORT_2015",
				"-bwy"
		};
//				end"E:/eclipse_workspace/workspace_luna/public/KISTI_PATENT_XML_EXPORT_2015_1/searchOption.20180502.txt",
//				end"E:/eclipse_workspace/workspace_luna/public/KISTI_PATENT_XML_EXPORT_2015_1/searchOption.20180502_3.txt",
//				end"E:/eclipse_workspace/workspace_luna/public/KISTI_PATENT_XML_EXPORT_2015_1/searchOption.20180502_7.txt",
//				"E:/eclipse_workspace/workspace_luna/public/KISTI_PATENT_XML_EXPORT_2015_1/searchOption.20180502_6.txt",
//		"E:/eclipse_workspace/workspace_luna/public/KISTI_PATENT_XML_EXPORT_2015_1/searchOption.20180502_6-1.txt",
//		"E:/eclipse_workspace/workspace_luna/public/KISTI_PATENT_XML_EXPORT_2015_1/searchOption.20180502_6-2.txt",
//		"E:/eclipse_workspace/workspace_luna/public/KISTI_PATENT_XML_EXPORT_2015_1/searchOption.20180502_5-1.txt",
//		"E:/eclipse_workspace/workspace_luna/public/KISTI_PATENT_XML_EXPORT_2015_1/searchOption.20180502_5-2.txt",
//		"E:/eclipse_workspace/workspace_luna/public/KISTI_PATENT_XML_EXPORT_2015_1/searchOption.20180502_5.txt",
//		"E:/eclipse_workspace/workspace_luna/public/KISTI_PATENT_XML_EXPORT_2015_1/searchOption.20180502_4-1.txt",
		//"E:/eclipse_workspace/workspace_luna/public/KISTI_PATENT_XML_EXPORT_2015_1/searchOption.20180502_1.txt",				
		//"E:/eclipse_workspace/workspace_luna/public/KISTI_PATENT_XML_EXPORT_2015_1/searchOption.20180502_2.txt",
		//"E:/eclipse_workspace/workspace_luna/public/KISTI_PATENT_XML_EXPORT_2015_1/searchOption.20180502_2-2.txt",
		//"E:/eclipse_workspace/workspace_luna/public/KISTI_PATENT_XML_EXPORT_2015_1/searchOption.20180502_4.txt",
//		"E:/eclipse_workspace/workspace_luna/public/KISTI_PATENT_XML_EXPORT_2015_1/searchOption.20180502_2-1-1.txt",
//				"E:/eclipse_workspace/workspace_luna/public/KISTI_PATENT_XML_EXPORT_2015_1/searchOption.20180502_2-1.txt",
		
//		"E:/eclipse_workspace/workspace_luna/public/KISTI_PATENT_XML_EXPORT_2015_1/searchOption.20180611_kor.txt",
//		"E:/eclipse_workspace/workspace_luna/public/KISTI_PATENT_XML_EXPORT_2015_1/외국기업_searchOption3.txt",
//		"E:/eclipse_workspace/workspace_luna/public/KISTI_PATENT_XML_EXPORT_2015_1/외국기업_searchOption2.txt",
//		"E:/eclipse_workspace/workspace_luna/public/KISTI_PATENT_XML_EXPORT_2015_1/외국기업_searchOption4.txt",
//		"E:/eclipse_workspace/workspace_luna/public/KISTI_PATENT_XML_EXPORT_2015_1/외국기업_searchOption.txt",
//		"E:/eclipse_workspace/workspace_luna/public/KISTI_PATENT_XML_EXPORT_2015_1/외국기업_searchOption10.txt",
//		"E:/eclipse_workspace/workspace_luna/public/KISTI_PATENT_XML_EXPORT_2015_1/외국기업_searchOption11.txt",
//		"E:/eclipse_workspace/workspace_luna/public/KISTI_PATENT_XML_EXPORT_2015_1/외국기업_searchOption12.txt",
//		"E:/eclipse_workspace/workspace_luna/public/KISTI_PATENT_XML_EXPORT_2015_1/외국기업_searchOption6.txt",
//		"E:/eclipse_workspace/workspace_luna/public/KISTI_PATENT_XML_EXPORT_2015_1/외국기업_searchOption8.txt",
//		"E:/eclipse_workspace/workspace_luna/public/KISTI_PATENT_XML_EXPORT_2015_1/외국기업_searchOption9.txt",
//		"E:/eclipse_workspace/workspace_luna/public/KISTI_PATENT_XML_EXPORT_2015_1/외국기업_searchOption13.txt",
//		"E:/eclipse_workspace/workspace_luna/public/KISTI_PATENT_XML_EXPORT_2015_1/외국기업_searchOption5.txt",
//		"E:/eclipse_workspace/workspace_luna/public/KISTI_PATENT_XML_EXPORT_2015_1/외국기업_searchOption7.txt",
		String[] fileList = new String[]{
//				"E:/eclipse_workspace/workspace_luna/public/KISTI_PATENT_XML_EXPORT_2015_1/권오진/20181207/20181207_patent.txt",
				"D:/eclipse_workspace/workspace_luna/public/KISTI_PATENT_XML_EXPORT_2015_1/김응도/20191023/searchOption.20191022.txt",
		};
		for(String fileName : fileList){
			BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
			System.out.println("Search File " + fileName);
			StringBuilder buff = new StringBuilder();
			String line = null;
			LinkedList<String> list = new LinkedList<String>();
			while((line=br.readLine())!=null){
				line = line.trim();
				if("".equals(line)) continue;
				
				if(line.startsWith("-")){
					if(buff.length() > 0 ){
						list.add(buff.toString().trim());
						buff.setLength(0);
					}
				}
				buff.append(line);
				buff.append(" ");
			}
			if(buff.length()>0){
				list.add(buff.toString().trim());
				buff.setLength(0);
			}
			br.close();
			
			
			args = new String[list.size()];
			int idx = 0;
			for(String l : list){
				args[idx++] = l;
				System.out.println(l);
			}
			autoriySearch(args);
			
		}
	}

	private static void autoriySearch(String[] args) throws Exception {
		InputParameter param = new InputParameter(args);
		String[] aues = param.authority.split(",");
		if ("ALL".equals(param.authority)) {
			aues = AUTHORITIES;
		}

		String keywordExtractPath = param.keywordExtrctPath;
		String searchRule = param.searchRule;
		String exportType = param.exportType;
		if (searchRule == null) {
			searchRule = "";
		}

		if (searchRule.length() < 1) {
			for (String au : aues) {
				String target = param.targetPath;
				String compress = param.compressFile;
				// AutorityMongoDBExport search = new
				// AutorityMongoDBExport(null, target, compress,
				// param.resultCount, exportType, false, param.exportField,
				// keywordExtractPath, param.multiLineValue, false);
				if (!param.backWard) {
					SearchAuthority search = new SearchAuthority(null, au, target, compress, param.resultCount, exportType, false, param.exportField,
							keywordExtractPath, param.multiLineValue, false);
//					search.
					search.run();
//					search.close();
				} else {
					PatentFullData queue = new PatentFullData();
					SearchAuthority search = new SearchAuthority(queue, au, target, compress, param.resultCount, exportType, false, param.exportField,
							keywordExtractPath, param.multiLineValue, true);
					Thread pt = new Thread(search);
					XMLWrite writer = new XMLWrite(queue, searchRule, target, exportType, param.exportField, param.multiLineValue,
							keywordExtractPath);
					Thread ct = new Thread(writer);
					pt.start();
					ct.start();
				}
			}
		} else {
//			String target = "";
//			String compress = param.compressFile + File.separator + System.nanoTime() + ".zip";
//			if ("XML".equals(exportType)) {
//				target = param.targetPath + File.separator + "PATENT_" + System.nanoTime();
//			} else if ("EXCEL".equals(exportType)) {
//				target = param.targetPath + File.separator + "PATENT_" + System.nanoTime() + ".xlsx";
//			} else {
//				target = param.targetPath + File.separator + "PATENT_" + System.nanoTime() + ".txt";
//			}

			// AutorityMongoDBExport search = new AutorityMongoDBExport(null,
			// target, compress, param.resultCount, exportType, false,
			// param.exportField, keywordExtractPath, param.multiLineValue,
			// false);
			// search.run();
			// search.close();
			if (searchRule.startsWith("\"")) {
				searchRule = searchRule.substring(1);
			}

			if (searchRule.endsWith("\"")) {
				searchRule = searchRule.substring(0, (searchRule.length() - 1));
			}
			String target = "";
			String compress = param.compressFile + File.separator + System.nanoTime() + ".zip";
			if ("XML".equals(exportType)) {
				target = param.targetPath + File.separator + "PATENT_" + System.nanoTime();
			} else if ("EXCEL".equals(exportType)) {
				target = param.targetPath + File.separator + "PATENT_" + System.nanoTime() + ".xlsx";
			} else {
				target = param.targetPath + File.separator + "PATENT_" + System.nanoTime() + ".txt";
			}

			if (!param.backWard) {
				SearchAuthority search = new SearchAuthority(null, searchRule, target, compress, param.resultCount, exportType, true,
						param.exportField, keywordExtractPath, param.multiLineValue, false);
				search.run();
			} else {
				PatentFullData queue = new PatentFullData();
				SearchAuthority search = new SearchAuthority(queue, searchRule, target, compress, param.resultCount, exportType, true,
						param.exportField, keywordExtractPath, param.multiLineValue, true);
				// search.run();
				Thread pt = new Thread(search);
				XMLWrite writer = new XMLWrite(queue, searchRule, target, exportType, param.exportField, param.multiLineValue, keywordExtractPath);
				Thread ct = new Thread(writer);
				pt.start();
				ct.start();
			}
//			SearchAuthority search = new SearchAuthority(queue, searchRule, target, compress, param.resultCount, exportType, true, param.exportField,
//					keywordExtractPath, false);
//			search.run();
		}
	}
	

	/**
	 * 사용자 입력 파라미터.
	 * 
	 * @author neon
	 * 
	 */
	public static class InputParameter {
		String targetPath;
		String authority = "ALL";
		String searchRule;
		String exportType;
		String compressFile;
		String exportField = "PNO";
		String multiLineValue = "N";
		int resultCount = 1000;
		String keywordExtrctPath;
		boolean backWard = false;
		
//		미국등록 특허 1976년부터 2017년까지 (등록년도기준)
//		특허번호, 등록일, 출원인(회사명), 출원인국가, 특허분류코드(IPC), CPC, 피인용회수(Forward)

		public InputParameter(String[] args) throws Exception {
			if (args != null) {
				for (String a : args) {
					parseParameter(a);
				}
			}
			verify();
		}

		private void verify() throws Exception {
			if (targetPath == null) {
				throw new Exception("데이터를 저장할 경로를 입력하지 않았습니다. -tg 옵션으로 해당 경로 정보를 입력해주세요.");
			}
			if (exportType == null) {
				exportType = "EXCEL";
			} else {
				if ("XML".equalsIgnoreCase(exportType)) {
					// if (compressFile == null) {
					// throw new Exception("압축파일을 저장할 경로를 입력하지 않았습니다. -ct 옵션으로
					// 해당 경로 정보를 입력해주세요.");
					// }
				} else {
					if (keywordExtrctPath == null) {
						throw new Exception("키워드 추출을 위한 사전d 경로를 입력하지 않았습니다. -ke 옵션으로 키워드 사전 경로 홈정보를 입력해주세요.");
					}
				}
			}

			if (exportField == null) {
				exportField = "PNO";
			}

			if (exportField.length() < 1) {
				exportField = "PNO";
			}

			if (authority == null) {
				authority = "ALL";
			}
		}

		private void parseParameter(String s) {
			String param = s.substring(3);
			if (s.startsWith("-tg")) {
				logger.info("TARGET : {}", param.trim());
				this.targetPath = param.trim();
			} else if (s.startsWith("-cn")) {
				logger.info("SEARCH COUNT : {}", param.trim());
				if (param.length() > 0) {
					this.resultCount = Integer.parseInt(param);
				}
			} else if (s.startsWith("-au")) {
				logger.info("AUTHORITY : {}", param.trim());
				this.authority = param.trim();
			} else if (s.startsWith("-ru")) {
				logger.info("SEARCH RULE : {}", param.trim());
				this.searchRule = param.trim();
			} else if (s.startsWith("-ft")) {
				logger.info("EXPORT TYPE : {}", param.trim());
				this.exportType = param.trim();
			} else if (s.startsWith("-ct")) {
				logger.info("COMPRESS FILE PATH : {}", param.trim());
				this.compressFile = param.trim();
			} else if (s.startsWith("-ef")) {
				logger.info("EXPORT FILED : {}", param.trim());
				this.exportField = param.trim();
			} else if (s.startsWith("-ke")) {
				logger.info("KEYWORD EXTRACT HOME : {}", param.trim());
				this.keywordExtrctPath = param.trim();
			} else if (s.startsWith("-bw")) {
				logger.info("Run to Thread : {}", param.trim());
				if ("N".equalsIgnoreCase(param.trim())) {
					backWard = false;
				} else {
					backWard = true;
				}
			} else if (s.startsWith("-mv")) {
				logger.info("Multi Values Per Line : {}", param.trim());
				multiLineValue = param.trim();
			}
		}

	}
}
