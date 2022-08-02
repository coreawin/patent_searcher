package com.diquest.patent.searcher._2021.이창환;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.diquest.bean.PatentFullData;
import com.diquest.export.XMLWrite;
import com.diquest.search.SearchAuthorityWOS;

/**
 * 이창환 선배님 안녕하세요,
 * 
 * 요청하는 데이터 형태를 정리하여 다시 메일드립니다.
 * 
 * <미국등록특허>
 * 
 * - 테이블1 : 등록번호 / CPC 코드(4자리, 복수개) / CPC(full-digit, 복수개) / title / abstract /
 * 청구항(독립항들만, 복수개)
 * 
 * 복수개인 항목은 구분자 '##' 으로 부탁드립니다.
 * 
 * 감사합니다.
 * 
 * 이재민 드림
 *
 * @author coreawin
 * @date 2021. 1. 9.
 */
public class PatentSearcher_2021_이창환 {
	static final Logger logger = LoggerFactory.getLogger(PatentSearcher_2021_이창환.class.getName());

	public static Map<String, PatentFullData> threadAuthroMap = new LinkedHashMap<String, PatentFullData>();

	private static String[] AUTHORITIES = { "US", "EP", "WO", "CN", "JP", "KR", "DE", "FR", "GB", "CA", "AR", "AT", "AU", "BE",
			"BR", "CH", "DD", "DK", "EA", "ES", "FI", "IE", "IN", "IT", "LU", "MC", "MX", "NL", "PT", "RU", "SE", "SU", "TW" };

	static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

	public static void main(String[] args) throws Exception {
		// 2021.01.09 브로커 사용방법이 변경된것 같음 (작년대비)... 한태호차장과 확인필요.
//		ExportTabText.DATA_DELIMITER = "##"; /*데이터 구분자를 세팅한다.*/
		String downloadDateFormat = "20210126";

		String downloadPath = "d:/data/2021/이창환/이재민/patent/20210218/download/";
		// 다운로드 폴더 미리 생성.
				new File(downloadPath).getParentFile().mkdirs();
				
		String 검색식 = "APLT=(Y) PUBL_TYPE=(G) AU=(US) AND DTYPE=(P)";
//		검색식 = "PN=(US10893552B2)";

		args = new String[] {
				"-cn2000",
				"-auUS",
				"-tg" + downloadPath +"us_grant.txt",
				"-ctd:/release/KISTI_PATENT_XML_EXPORT_2015/result",
				"-ftTEXT",
				"-efpno,cpc,cpcsc,ti,abs,independent_claims",
				"-ru%s", "-ked:/release/KISTI_PATENT_XML_EXPORT_2015", "-bwn" };

					// 파라미터 정보를 검색식에 맞게 재 설정한다.
					String[] t = new String[args.length];
					for (int idx = 0; idx < t.length; idx++) {
						t[idx] = args[idx];
					}
					logger.info(args[2]);

					t[6] = String.format(t[6], 검색식);

					File downloadFile = new File(t[2].replaceAll("-tg", ""));
					downloadFile.getParentFile().mkdirs();
					if (downloadFile.isFile()) {
						if (downloadFile.length() < 15000) {
						} else {
							if (검색식.indexOf("APLT=(Y) ") != -1) {
								logger.info("이미 만들 파일은 건너 뛴다. {}", downloadFile.getAbsolutePath());
							} else {
								logger.info("줄원번호 중복 제거를 위한 검색식을 추가한다. APLT=(Y) ");
								검색식 = "APLT=(Y)   " + 검색식;
							}
						}
					}
					StringBuffer buf = new StringBuffer();
					// searchRule = "1234567890azAZ_+=-\"()?&*!#";
					for (char c : 검색식.trim().toCharArray()) {
						int cc = (int) c;
						if (cc >= 30 || 130 <= cc) {
							buf.append(c);
						}
					}
					검색식 = buf.toString();
					// logger.info("techName {} ", t[2]);
					try {
						// CustomQueue.bQueue.put(techNo);
						PatentSearcher_2021_이창환 ps = new PatentSearcher_2021_이창환();
						ps.autoriySearch(t);
						try {
							Thread.sleep(1000 * 10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} catch (Exception e) {
						e.printStackTrace();
						// System.exit(1);
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
				logger.debug("TARGET : {}", param.trim());
				this.targetPath = param.trim();
			} else if (s.startsWith("-cn")) {
				logger.debug("SEARCH COUNT : {}", param.trim());
				if (param.length() > 0) {
					this.resultCount = Integer.parseInt(param);
				}
			} else if (s.startsWith("-au")) {
				logger.debug("AUTHORITY : {}", param.trim());
				this.authority = param.trim();
			} else if (s.startsWith("-ru")) {
				logger.debug("SEARCH RULE : {}", param.trim());
				this.searchRule = param.trim();
			} else if (s.startsWith("-ft")) {
				logger.debug("EXPORT TYPE : {}", param.trim());
				this.exportType = param.trim();
			} else if (s.startsWith("-ct")) {
				logger.debug("COMPRESS FILE PATH : {}", param.trim());
				this.compressFile = param.trim();
			} else if (s.startsWith("-ef")) {
				logger.debug("EXPORT FILED : {}", param.trim());
				this.exportField = param.trim();
			} else if (s.startsWith("-ke")) {
				logger.debug("KEYWORD EXTRACT HOME : {}", param.trim());
				this.keywordExtrctPath = param.trim();
			} else if (s.startsWith("-bw")) {
				logger.debug("Run to Thread : {}", param.trim());
				if ("N".equalsIgnoreCase(param.trim())) {
					backWard = false;
				} else {
					backWard = true;
				}
			} else if (s.startsWith("-mv")) {
				logger.debug("Multi Values Per Line : {}", param.trim());
				multiLineValue = param.trim();
			}
		}

	}
}
