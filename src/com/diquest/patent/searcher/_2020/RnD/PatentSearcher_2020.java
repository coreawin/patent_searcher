package com.diquest.patent.searcher._2020.RnD;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.diquest.bean.PatentFullData;
import com.diquest.export.XMLWrite;
import com.diquest.search.SearchAuthorityWOS;

/**
 * 2020.RnD_PIE_특허_논문_정량적_분석_지표에 대한 Excel 검색식 추출 및 결과 만들기.<br>
 * 특허 데이터를 받는다.
 * 
 * @author coreawin
 * @date 2020. 1. 22.
 */
public class PatentSearcher_2020 {
	static final Logger logger = LoggerFactory.getLogger(PatentSearcher_2020.class.getName());

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
	private static String[] AUTHORITIES = { "US", "EP", "WO", "CN", "JP", "KR", "DE", "FR", "GB", "CA", "AR", "AT", "AU", "BE",
			"BR", "CH", "DD", "DK", "EA", "ES", "FI", "IE", "IN", "IT", "LU", "MC", "MX", "NL", "PT", "RU", "SE", "SU", "TW" };

	static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

	public static void main(String[] args) throws Exception {
		String _분과명 = "신재생에너지";
		// 분과명 = "소부장";
		// 분과명 = "혁신성장";
		String[] 분과명정보 = new String[] { "혁신성장", "신재생에너지"

		};
		
		분과명정보 = new String[] { "0214"

		};
		args = new String[] {
				"-cn2000",
				"-auUS",
				"-tgd:\\data\\2020\\yeo\\" + dateFormat.format(new Date()) + "\\%s\\%s\\patent\\%s.txt",
				"-ctd:/release/KISTI_PATENT_XML_EXPORT_2015/result",
				"-ftTEXT",
				"-efpno,dockind,pyear,pndate,pnkind,pncn,ti,appno,appdate,prino,inventor,assignee,app_gp,basic_count,ipc,cpc,ref_count,cur_assignee",
				"-ru%s", "-ked:/release/KISTI_PATENT_XML_EXPORT_2015", "-bwn" };

		for (String 분과명 : 분과명정보) {

			String searchRuleFilePath = "D:/eclipse_workspace/workspace_luna/public/NEON_ECLIPSE_KISTI_PATENT_SEARCHER/2020/0212/"
					+ 분과명 + File.separator;

			File f = new File(searchRuleFilePath);
			File[] dirs = f.listFiles();
			for (File dir : dirs) {
				// 검색식을 엑셀파일로부터 읽는다.
//				if(!dir.getName().contains("지능형로봇")){
//					continue;
//				}
				Map<String, Map<Integer, LinkedList<Map<Integer, String>>>> readData = new ExcelUtilCommon().readExcel(dir
						.getAbsolutePath());

				Set<String> excelFileNameSet = readData.keySet();
				for (String excelFileName : excelFileNameSet) {
					logger.info("excel data {}", excelFileName);
					
					Map<Integer, LinkedList<Map<Integer, String>>> sheetData = readData.get(excelFileName);
					// 여기서는 0번 시트만 사용한다.

					LinkedList<Map<Integer, String>> rowData = sheetData.get(0);

					for (Map<Integer, String> row : rowData) {
						if(row.size() < 3) continue;
						String techNo = row.get(0);
						String techNo2 = techNo;
						String techName = row.get(1).replaceAll("[\\r|\\n|\\<]", "").replaceAll("\\s{1,}", " ")
								.replaceAll("/", "-").replaceAll(" ", "_").trim();
						String searchRule = row.get(2).replaceAll("[\\r|\\n]", "").replaceAll("\\s{1,}", " ").trim();

						/* 파라미터 정보를 검색식에 맞게 재 설정한다. */
						String[] t = new String[args.length];
						for (int idx = 0; idx < t.length; idx++) {
							t[idx] = args[idx];
						}

						/*
						 * 경제적 특성을 추출하려면 논문의 파일명과 동일해야 한다. @since
						 * ScopusSearcher_2020
						 */
						t[2] = String.format(args[2], 분과명, dir.getName().replaceAll("\\s{1,}", ""),
								techName + techNo);
//						if ("소부장".equals(분과명) && row.size() == 4) {
						if (row.size() == 4) {
							techNo = row.get(0);
							techNo2 = row.get(1);
							techName = row.get(2).replaceAll("[\\r|\\n|\\<]", "").replaceAll("\\s{1,}", " ")
									.replaceAll("/", "-").replaceAll(" ", "_").trim();
							searchRule = row.get(3).replaceAll("[\\r|\\n]", "").replaceAll("\\s{1,}", " ").trim();
							t[2] = String.format(args[2], 분과명, dir.getName().replaceAll("\\s{1,}", ""),
									techName.replaceAll("\\s{1,}", "").replaceAll("[-_\\.\\<]", "").trim() + "_" + techNo2);
						}
						searchRule = searchRule.replaceAll("“", "\"").replaceAll("”", "\"").trim();
						t[6] = String.format(t[6], searchRule);
						
						File downloadFile = new File(t[2].replaceAll("-tg", ""));
						downloadFile.getParentFile().mkdirs();
						if(downloadFile.isFile()){
							if(downloadFile.length() < 15000 ){
								
							}else{
								logger.info("이미 만들 파일은 건너 뛴다. {}", downloadFile.getAbsolutePath());
								continue;
							}
						}

						StringBuffer buf = new StringBuffer();
						// searchRule = "1234567890azAZ_+=-\"()?&*!#";
						for (char c : searchRule.trim().toCharArray()) {
							int cc = (int) c;
							if (cc >= 30 || 130 <= cc) {
								buf.append(c);
							}
						}
						searchRule = buf.toString();
						// logger.info("techName {} ", t[2]);
						try {
							// CustomQueue.bQueue.put(techNo);
							logger.info("{} / {} / {}", techNo, techName, searchRule);
							PatentSearcher_2020 ps = new PatentSearcher_2020();
							ps.autoriySearch(t);
						} catch (Exception e) {
							e.printStackTrace();
							// System.exit(1);
						}
					}
				}
			}
		}
	}

	private void autoriySearch(String[] args) throws Exception {
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
					SearchAuthorityWOS search = new SearchAuthorityWOS(null, au, target, compress, param.resultCount,
							exportType, true, param.exportField, keywordExtractPath, param.multiLineValue, false);
					// search.
					search.run();
					// search.close();
				} else {
					PatentFullData queue = new PatentFullData();
					SearchAuthorityWOS search = new SearchAuthorityWOS(queue, au, target, compress, param.resultCount,
							exportType, true, param.exportField, keywordExtractPath, param.multiLineValue, true);
					Thread pt = new Thread(search);
					XMLWrite writer = new XMLWrite(queue, searchRule, target, exportType, param.exportField,
							param.multiLineValue, keywordExtractPath);
					Thread ct = new Thread(writer);
					pt.start();
					ct.start();
				}
			}
		} else {
			// String target = "";
			// String compress = param.compressFile + File.separator +
			// System.nanoTime() + ".zip";
			// if ("XML".equals(exportType)) {
			// target = param.targetPath + File.separator + "PATENT_" +
			// System.nanoTime();
			// } else if ("EXCEL".equals(exportType)) {
			// target = param.targetPath + File.separator + "PATENT_" +
			// System.nanoTime() + ".xlsx";
			// } else {
			// target = param.targetPath + File.separator + "PATENT_" +
			// System.nanoTime() + ".txt";
			// }

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
				target = param.targetPath;
				// target = param.targetPath + File.separator + "PATENT_" +
				// System.nanoTime() +
				// ".txt";
			}

			if (!param.backWard) {
				SearchAuthorityWOS search = new SearchAuthorityWOS(null, searchRule, target, compress, param.resultCount,
						exportType, true, param.exportField, keywordExtractPath, param.multiLineValue, false);
				search.run();
			} else {
				PatentFullData queue = new PatentFullData();
				SearchAuthorityWOS search = new SearchAuthorityWOS(queue, searchRule, target, compress, param.resultCount,
						exportType, true, param.exportField, keywordExtractPath, param.multiLineValue, true);
				// search.run();
				Thread pt = new Thread(search);
				XMLWrite writer = new XMLWrite(queue, searchRule, target, exportType, param.exportField, param.multiLineValue,
						keywordExtractPath);
				Thread ct = new Thread(writer);
				pt.start();
				ct.start();
			}
			// SearchAuthorityWOS search = new SearchAuthorityWOS(queue,
			// searchRule,
			// target, compress, param.resultCount, exportType, true,
			// param.exportField,
			// keywordExtractPath, false);
			// search.run();
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

		// 미국등록 특허 1976년부터 2017년까지 (등록년도기준)
		// 특허번호, 등록일, 출원인(회사명), 출원인국가, 특허분류코드(IPC), CPC, 피인용회수(Forward)

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
			s = s.trim();
			System.out.println(s);
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
