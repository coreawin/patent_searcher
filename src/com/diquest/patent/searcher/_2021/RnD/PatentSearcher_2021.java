package com.diquest.patent.searcher._2021.RnD;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.diquest.bean.PatentFullData;
import com.diquest.export.XMLWrite;
import com.diquest.search.SearchAuthorityWOS;

/**
 * <P>
 * A. 작업방법 기술검색식을 이용하여 먼저 SCOPUS, PATENT 다운로드를 시도한다.<br>
 * 1. NEON_ECLIPSE_KISTI_SCOPUS_SEARCHER<br>
 * 2. NEON_ECLIPSE_KISTI_PATENT_SEARCHER<br>
 * _2021.Rnd package.ScopusSearcher_2021.java <br>
 * </P>
 *
 * <P>
 * B. 정량분석 지표를 추출한다. <br>
 * pe.neon 프로젝트의 아래 클래스를 이용해 논문/특허 정량지표를 추출한다.<br>
 * pe.neon.여운동._202101.Launcher4Patent <br>
 * pe.neon.여운동._202101.Launcher4Scopus<br>
 * 결과물 : <br>
 * 1. RESULT_SCOPUS_기술명_작업일.txt <br>
 * 2. RESULT_PATENT_기술명_작업일.txt<br>
 * </P>
 *
 * <P>
 * C. pe.eclipse.neon 프로젝트를 이용하여 경제적 특성 정보를 최종 취합한다.<br>
 * pe.eclipse.neon.yeo._2021.Extract경제적특성_2021
 * </P>
 *
 * 안녕하세요 미소테크 박진현입니다. <br/>
 *
 * R&D PIE 시스템 데이터 관련 메일 드립니다.<br/>
 * 우선 정리 완료된 4개 분과(전체 16개분과 중) 논문, 특허검색식 전달 해드립니다.<br/>
 * 검색식 부분에 KISTI에서 검색시 결과 건수도 같이 표기 했습니다. 건수가 비슷하게 나오면 될듯합니다.<br/>
 * 이 부분에 대한 작년과 동일한 작업 요청 드리겠습니다.<br/>
 * (분과별 기술군별 논문/특허 결과, 10대 지표값 등)<br/>
 * 일전 말씀드린바와 같이 아래와 같은 필드 추가 요청 드립니다.<br/>
 *
 * <논문><br/>
 *
 * - 피인용수: Number of Citation<br/>
 * Check - 저자 국가 식별: Author Country<br/>
 * Check - 저자명 식별: Author ID<br/>
 * Check - 저자명: Author Name<br/>
 * Check - 저자 통합 정보: Author Info<br/>
 * Check - 기관명 국가 식별: Affiliation Country<br/>
 * Check - 기관명 식별: Affiliation IDs<br/>
 * Check - 기관명: Affiliation Name<br/>
 * Check
 *
 * <특허><br/>
 *
 * - 출원인 국적: assignee-country<br/>
 * check - 대표출원인(정제된 것?): app_gp<br/>
 * check - 출원인 국적+명: assignee<br/>
 * check - 피인용수; citation-count<br/>
 * check - 등록번호:<br/>
 * ==> KIND 항목이 Grant 인것 check
 *
 * @author coreawin
 * @date 2021. 1. 9.
 */
public class PatentSearcher_2021 {
	static final Logger logger = LoggerFactory.getLogger(PatentSearcher_2021.class.getName());

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
		//2021.01.09 브로커 사용방법이 변경된것 같음 (작년대비)... 한태호차장과 확인필요.
		String downloadDateFormat = "20210423";
		
		String downloadPath = "d:/data/2021/박진현-미소/download";
//		String[] 분과명정보 = new String[] { "고기능무인기", "수소에너지", "스마트팜", "연료전지", "친환경차", "태양광" };
		String[] 분과명정보 = new String[] {  "고기능무인기", "수소에너지", "스마트그리드", "스마트팜", "시스템반도체", "연료전지", "지능형로봇", "친환경차", "태양광" };
		 분과명정보 = new String[] {
				 "스마트시티", "인공지능"
		 };
		 
		 분과명정보 = new String[] {
				 "정밀의료"
		 };
		 분과명정보 = new String[] {
				 "태양광"
		 };
		 분과명정보 = new String[] {
				 "ERROR"
		 };
		 
		 분과명정보 = new String[] {
				 "미세먼지"
		 };
		 
//		 분과명정보 = new String[] {  "자율주행차" };
		// 다운로드 폴더 미리 생성.
		new File(downloadPath).mkdirs();

		args = new String[] {
				"-cn2000",
				"-auUS",
				"-tg" + downloadPath + File.separator + downloadDateFormat + "\\%s\\patent\\%s.txt",
				"-ctd:/release/KISTI_PATENT_XML_EXPORT_2015/result",
				"-ftTEXT",
				"-efpno,dockind,pyear,pndate,pnkind,pncn,ti,appno,appdate,prino,inventor,assignee,app_gp,basic_count,ipc,cpc,ref_count,cur_assignee",
				"-ru%s", "-ked:/release/KISTI_PATENT_XML_EXPORT_2015", "-bwn" };

		for (String 분과명 : 분과명정보) {
			String searchRuleFilePath = "D:/eclipse_workspace/workspace_luna/public/NEON_ECLIPSE_KISTI_PATENT_SEARCHER/2021/0109/"
					+ 분과명 + File.separator;

			File f = new File(searchRuleFilePath);
			File[] dirs = f.listFiles();
			for (File dir : dirs) {
				logger.info("read path {}", dir.getAbsolutePath());
				검색식파서 searchParser = new 검색식파서(dir.getAbsolutePath());
				Map<String, String> searchParsers = searchParser.keys;

				Set<String> 검색식아이디정보 = searchParsers.keySet();

				logger.info(검색식아이디정보.size() + "");
				for (String 검색식아이디 : 검색식아이디정보) {
					String dp = downloadPath;
					 logger.info("검색식아이디 : {} => {}", 검색식아이디, searchParsers.get(검색식아이디));
//					 if(true) continue;
					String[] 검색식아이디분절 = 검색식아이디.split(":");
					String techID = 검색식아이디분절[0];

					String techName = 검색식아이디분절[1].replaceAll("[\\r|\\n|\\<]", "").replaceAll("\\s{1,}", " ")
							.replaceAll("/", "-").replaceAll(" ", "_").trim();
					String 검색식 = searchParsers.get(검색식아이디).replaceAll("“|”","\"").replaceAll("[\\r|\\n]", "").replaceAll("\\s{1,}", " ").trim();
					if(검색식.indexOf("APLT=(") ==-1){
						logger.info("줄원번호 중복 제거를 위한 검색식을 추가한다. APLT=(Y) ");
						검색식 = "APLT=(Y) " + 검색식;
					}
					
					
//					dp = String.format(dp + File.separator + dateFormat.format(new Date()) + "\\%s\\patent\\%s.txt", 분과명,
//							techName.replaceAll("\\s{1,}", "").replaceAll("[-_\\.\\<]", "").replaceAll("\\.txt", "").trim()
//									+ techID+ "_" + searchParser.scount.get(검색식아이디));

					// 파라미터 정보를 검색식에 맞게 재 설정한다.
					String[] t = new String[args.length];
					for (int idx = 0; idx < t.length; idx++) {
						t[idx] = args[idx];
					}
					
//					logger.info(args[2]);
//					t[2] = String.format(args[2],분과명, techID+ "_" +  techName
//							.replaceAll("\\s{1,}", "").replaceAll("[-_\\.\\<]", "").replaceAll("\\.txt", "").trim()
//							+ "_"+ searchParser.scount.get(검색식아이디));
					
					t[2] = String.format(args[2],분과명,  techID+ "_" +  techName
							.replaceAll("\\s{1,}", "").replaceAll("[-_\\.\\<]", "").replaceAll("\\.txt", "").trim());
					t[6] = String.format(t[6], 검색식);
					
					File downloadFile = new File(t[2].replaceAll("-tg", ""));
					downloadFile.getParentFile().mkdirs();
					if (downloadFile.isFile()) {
						if (downloadFile.length() < 15000) {

						} else {
							if(검색식.indexOf("APLT=(Y) ") !=-1 ){
								logger.info("이미 만들 파일은 건너 뛴다. {}", downloadFile.getAbsolutePath());
								continue;
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
//						logger.info("{} / {} / {}", techID, techName, 검색식);
						PatentSearcher_2021 ps = new PatentSearcher_2021();
						ps.autoriySearch(t);
//						try {
//							Thread.sleep(1000 * 10);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
					} catch (Exception e) {
						e.printStackTrace();
						// System.exit(1);
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
