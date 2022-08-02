package com.diquest.patent.searcher._2020.ThePhi;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.diquest.bean.PatentFullData;
import com.diquest.export.XMLWrite;
import com.diquest.search.SearchAuthorityWOS;

/**
 * The Phi용 데이터를 받는다.
 * 
 * @author coreawin
 * @date 2020. 3. 17.
 */
public class PatentSearcherThePhi_2020 {
	static final Logger logger = LoggerFactory.getLogger(PatentSearcherThePhi_2020.class.getName());

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
		args = new String[] {
				"-cn2000",
				"-auUS",
				"-tgd:\\data\\2020\\lee\\" + dateFormat.format(new Date()) + "\\patent\\%s.txt",
				"-ctd:/release/KISTI_PATENT_XML_EXPORT_2015/result",
				"-ftPHI",
				"-efpno,pnkind,ti,cpc,ipc,appdate,prino,assignee, app_standardized,cur_assignee,cur_assignee_standard,status,ref_count,family_count,basic_count, ",
				"-ru%s", "-ked:/release/KISTI_PATENT_XML_EXPORT_2015", "-bwn" };
//		제목, IPC, CPC, 인용특허정보, 패미리특허정보, 번호, 출원연도, 우선권, 독립항 총속항, 법적정보, 기관 표준화된 정보, 현재 출원인 정보, 커런트 스탠다드 정보
						String searchRule = "APDATE=(20130101-20201231) AU=(US) DTYPE=(P OR U) AB=(solar OR sunlight OR \"sun light\" OR \"solar energy\" OR \"solar power\" OR \"solar radiation\" OR \"solar heat\" OR \"solar heating\" OR \"solar heater\" OR \"solar generate\" OR \"solar generator\" OR \"solar cell\" OR \"solar farm\" OR photovoltaic OR photovoltaics OR PHV) AND TI=(((MW OR megawatt OR \"mega watt\") and (test OR assessment OR examination OR check OR substantiation OR demonstration OR exemplification OR empirical OR explanation)) OR (recycle OR recycling OR reuse OR recyclable OR reutilization OR \"double deal\" OR \"re-use\" OR reusable OR reclaim OR reprocess) OR (DB OR database \"data base\") OR (guideline OR \"guide line\" OR manual OR instructions OR instruct OR handbook OR explanation OR explain) OR (\"waste material\" OR \"useless article\" OR \"useless thing\" OR detritus OR scrappage OR rejectamenta OR arisings OR rubbish OR refuse OR debris OR scrap OR garbage OR trash) OR (\"idle land\" OR \"land lying idle\" OR \"fallowed field\" OR \"land in fallow\" OR \"fallow field\" OR \"fallow land\" OR \"fallow ground\" OR \"land resting\") OR (\"value chain\"))) OR (APDATE=(20130101-20201231) AB=((solar OR sunlight OR \"sun light\" OR \"solar energy\" OR \"solar power\" OR \"solar radiation\" OR \"solar heat\" OR \"solar heating\" OR \"solar heater\" OR \"solar generate\" OR \"solar generator\" OR \"solar cell\" OR \"solar farm\" OR photovoltaic OR photovoltaics OR PHV) AND (revenue OR profit OR benefit OR margin OR earnings OR beneficial OR gainings OR income OR earn OR BM OR BusinessModel OR \"Business Model\" OR Business OR share OR sharing OR rent OR lease)) AND TI=((ground OR land OR field) OR (farming OR agriculture OR agricultural OR agronomy OR farm OR husbandry OR cultivation OR cultivate OR grow) OR (\"large scale\" OR \"grand scale\" OR \"big scale\" OR extensive OR macroscale OR extensiveness OR substantial OR considerable OR great OR \"wide range\") OR (BAPV OR \"Building Integrated Photovoltaic\" OR \"Building Integrated Photovoltaics\" OR \"Building Integrated Photo voltaic\" OR \"Building Integrated Photovoltaic system\" OR Building OR \"building PHV\"))) OR (APDATE=(20130101-20201231) AB=(( solar or \"solar energy\" OR \"solar power\" OR \"Solar Thermal energy\" OR \"Solar Thermal Power\" OR \"solar fuel\" OR \"solar resource\" OR \"solar radiation\" OR \"solar heat\" OR \"solar heating\" OR \"solar heater\" OR \"solar generate\" OR \"solar generator\") AND (public OR community OR society OR common OR supply OR provide OR furnish OR diffusion OR spread OR coverage)) AND TI=((\"idle land\" OR \"land lying idle\" OR \"fallowed field\" OR \"land in fallow\" OR \"fallow field\" OR \"fallow land\" OR \"fallow ground\" OR \"land resting\") OR (\"salt pond\" OR \"salt pan\" OR \"salt field\" OR \"salt farm\" OR ocean OR sea OR marine OR maritime OR \"aquatic products\" OR \"marine products\" OR \"sea product\") OR (farming OR agriculture OR agricultural OR agronomy OR farm OR husbandry OR cultivation OR cultivate OR grow) OR (BAPV OR \"Building Integrated Photovoltaic\" OR \"Building Integrated Photovoltaics\" OR \"Building Integrated Photo voltaic\" OR \"Building Integrated Photovoltaic system\" OR Building OR \"building PHV\") OR (recycle OR recycling OR reuse OR recyclable OR reutilization OR \"double deal\" OR \"re-use\" OR reusable OR reclaim OR reprocess)))";
//						searchRule  = "PNO=(US7668340B2)";
						/* 파라미터 정보를 검색식에 맞게 재 설정한다. */
						String[] t = new String[args.length];
						for (int idx = 0; idx < t.length; idx++) {
							t[idx] = args[idx];
						}
						t[2] = String.format(args[2], "ThePhi");
						t[6] = String.format(t[6], searchRule);
						
						File downloadFile = new File(t[2].replaceAll("-tg", ""));
						downloadFile.getParentFile().mkdirs();

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
							PatentSearcherThePhi_2020 ps = new PatentSearcherThePhi_2020();
							ps.autoriySearch(t);
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
