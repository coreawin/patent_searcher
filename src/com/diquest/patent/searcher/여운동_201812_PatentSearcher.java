package com.diquest.patent.searcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.diquest.bean.PatentFullData;
import com.diquest.export.XMLWrite;
import com.diquest.search.SearchAuthorityWOS;

public class 여운동_201812_PatentSearcher {
	static Logger logger = LoggerFactory.getLogger("여운동_201812_PatentSearcher");
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
	private static final String[] AUTHORITIES = { "US", "EP", "WO", "CN", "JP", "KR", "DE", "FR", "GB", "CA", "AR",
			"AT", "AU", "BE", "BR", "CH", "DD", "DK", "EA", "ES", "FI", "IE", "IN", "IT", "LU", "MC", "MX", "NL", "PT",
			"RU", "SE", "SU", "TW" };

	public static void main(String[] args) throws Exception {

		args = new String[] { "-cn2000", "-au",
//				 "-tgd:/data/patent.20180105.txt",
				"-tg%s", 
				"-ctt:/release/KISTI_PATENT_XML_EXPORT_2015/result", "-ftTEXT",
//				"-efpno, ipc", 		
				"-efpno, dockind, pyear, pndate, pk, au, ti, abs, an,ay, apdate,firstpriyear,priyear,prino,inventor,inventor-count,assignee,assignee-count,app_gp,examiner,examiner,claims-count,independent-claims-count,ipc,cpc,ecla,fterm,mf,cf,total-family-count,main-family-count,extended-family-count" + 
				"c",
//				"-ru(AU=(US) APLT=(Y) AY=(2003-2010))",
				"-ru%s", 
				"-ket:/release/KISTI_PATENT_XML_EXPORT_2015", "-bw" };

/*		if(true){
			여운동_201812_PatentSearcher ps = new 여운동_201812_PatentSearcher();
			bQueue.put("1");
			ps.autoriySearch(args);
			return;
			//System.exit(-1);
		}
*/		
		String readfile = "./여운동/20181207/patent.searchrule.20190105";
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(readfile)), "utf-8"));
		String line = null;
		Map<String, LinkedList<String>> datas = new HashMap<String, LinkedList<String>>();
		try {
			LinkedList<String> list = null;
			String k = null;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if ("".equals(line)) {
					continue;
				}

				if (line.startsWith("@")) {
					if (list != null) {
						datas.put(k, list);
					}
					list = datas.get(line);
					k = line;
					if (list == null) {
						list = new LinkedList<String>();
					}
				} else {
					if(line.startsWith("#")) continue;
					list.add(line);
				}
			}

			if (list != null) {
				datas.put(k, list);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null)
				br.close();
		}
		
		Set<String> key = datas.keySet();
		for (String k : key) {
			List<String> l = datas.get(k);
			System.out.println(k + "\t" + l.size());
			for (String tl : l) {
				String[] t = new String[args.length];
				for(int idx=0; idx < t.length ; idx++) {
					t[idx] = args[idx];
				}
				String[] tls = tl.split("\t");
				String subtitle = tls[0];
				// System.out.println(subtitle + "\t|\t" + tls[1]);
				String searchRule = tls[1];
				String df = String.format("d:\\data\\yeo\\patent\\%s_%s.txt", k.replaceAll("@", ""),
						subtitle.replaceAll("/", "_").trim());
				t[5] = "-efpno,dockind,pyear,pndate,pnkind,pncn,ti,abs,appno,appdate,prino,inventor,assignee,app_gp,pexam,pexam,basic_count,ipc,cpc,ecla,fcode,mf,cf,family_count";
				t[2] = "-tg" + df;
				boolean a = new File(t[2]).getParentFile().mkdirs();
				t[6] = String.format(t[6], searchRule);
				// args[6] = "-ruAU=(US) and PNDATE=(-20001231) and PK=(B*)";
				logger.info("download {}", t[2]);
				 logger.info("searchRule {}", t[6]);
				try {
					CustomQueue.bQueue.put(df);
					여운동_201812_PatentSearcher ps = new 여운동_201812_PatentSearcher();
					ps.autoriySearch(t);
				} catch (Exception e) {
					e.printStackTrace();
					// System.exit(1);
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
				// target = param.targetPath + File.separator + "PATENT_" + System.nanoTime() +
				// ".txt";
			}

			if (!param.backWard) {
				SearchAuthorityWOS search = new SearchAuthorityWOS(null, searchRule, target, compress,
						param.resultCount, exportType, true, param.exportField, keywordExtractPath,
						param.multiLineValue, false);
				search.run();
			} else {
				PatentFullData queue = new PatentFullData();
				SearchAuthorityWOS search = new SearchAuthorityWOS(queue, searchRule, target, compress,
						param.resultCount, exportType, true, param.exportField, keywordExtractPath,
						param.multiLineValue, true);
				// search.run();
				Thread pt = new Thread(search);
				XMLWrite writer = new XMLWrite(queue, searchRule, target, exportType, param.exportField,
						param.multiLineValue, keywordExtractPath);
				Thread ct = new Thread(writer);
				pt.start();
				ct.start();
			}
			// SearchAuthorityWOS search = new SearchAuthorityWOS(queue, searchRule,
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
