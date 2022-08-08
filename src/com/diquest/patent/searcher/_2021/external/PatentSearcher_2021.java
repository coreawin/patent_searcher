package com.diquest.patent.searcher._2021.external;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.co.tqk.web.db.dao.CnProgramDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.diquest.bean.PatentFullData;
import com.diquest.export.XMLWrite;
import com.diquest.search.SearchAuthorityWOS;
import com.diquest.util.AReadFile;

/**
 * <P>
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
	 * @throws Exception
	 */
	private static String[] AUTHORITIES = { "US" };
//	private static String[] AUTHORITIES = { "US", "EP", "WO", "CN", "JP", "KR", "DE", "FR", "GB", "CA", "AR", "AT", "AU", "BE",
//		"BR", "CH", "DD", "DK", "EA", "ES", "FI", "IE", "IN", "IT", "LU", "MC", "MX", "NL", "PT", "RU", "SE", "SU", "TW" };

	static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

	static ConfProp cp = null;

	public static void loadConf() {
		try {
			cp = new ConfProp();
			logger.info("Execute Home : {}", cp.executeHomePath());
			System.setProperty("EJIANA_HOME", cp.executeHomePath());
		} catch (IOException e) {
			System.out.println("설정파일이 존재하지 않습니다. ");
			System.exit(-1);
		}
	}
	
	private static boolean 검색제한없애도되는날인가(){
		LocalDate to_date = LocalDate.now();
		DayOfWeek week = to_date.getDayOfWeek();
//		System.out.println("오늘 요일 (day_of_week) : " +  week);
		switch (week) {
		case SATURDAY:
		case SUNDAY:
			return true;
		default:
			return false;
		}
	}

	private static void restrict() {
		Dermy d = new Dermy();
		while (true) {
			synchronized (d) {
				try {
					// 1분마다 체크.
					// Calendar c = GregorianCalendar.getInstance();
					LocalTime now = LocalTime.now();
					int hour = now.getHour();
					// System.out.println(now + " \t " + hour);
					if(검색제한없애도되는날인가()==false){
						if ((hour >= 19 && hour <= 23) || (hour >= 0 && hour <= 6)) {
							break;
						} else {
							logger.warn("현재는 검색가능한 시간이 아닙니다 : 19:00~07:00 사이에만 검색 가능. (주말-토.일-에도 가능합니다.)");
							d.wait(1000 * 60 * 1);
							continue;
						}
					}else{
						break;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static void main(String[] args) throws Exception {
//    다운 가능한 필드.
//		pno
//		dockind
//		pnyear
//		pndate
//		pnkind
//		authority
//		ti
//		appno
//		appyear
//		appdate
//		firstpriyear
//		priyear
//		prino
//		inventor
//		inventor-count
//		assignee
//		assignee-count
//		examiner
//		claims-count
//		independent-claims-count
//		nonpatent
//		references
//		citations
//		total-references-cited-count
//		reference-count
//		citation-count
//		non-patent-count
//		ipc
//		cpc
//		ecla
//		classc
//		fcode
//		mf
//		cf
//		total-family-count
//		main-family-count
//		extended-family-count
//		app_gp

		
		/*
		 * parsingShopRule("test2 => APLT=(Y) TI=(oled) PY=(2020)  #ABS=Y #LIMIT=3"
		 * ); parsingShopRule("test2 => APLT=(Y) TI=(oled) PY=(2020)  #ABS=Y");
		 * parsingShopRule
		 * ("test2 => APLT=(Y) TI=(oled) PY=(2020)  #LIMIT= 334 #ABS=Y");
		 * parsingShopRule("test2 => APLT=(Y) TI=(oled) PY=(2020)  #LIMIT=3");
		 * parsingShopRule("test2 => APLT=(Y) TI=(oled) PY=(2020) ");
		 * parsingShopRule("test2 => APLT=(Y) TI=(oled) PY=(2020)  #ABS=B");
		 * parsingShopRule
		 * ("test2 => APLT=(Y) TI=(oled) PY=(2020)  #LIMIT= 13214 #ABS=N");
		 * 
		 * if (true) return;
		 */

		loadConf();
		 new Thread(new Checker()).start();
		String searchRuleFilePath = cp.getProperty("searchrule.path");
		logger.info("searchRule.path : {}", searchRuleFilePath);
		// logger.info("download.path : {}", downloadPath);

		if (searchRuleFilePath == null) {
			System.out.println("사용법 : conf 디렉토리에 있는 setup.conf 파일을 설정해 주세요.");
			System.out.println("searchrule.path : 검색식 파일(*.tsv)이 있는 디렉토리 ");
			System.out.println("설치 폴더의 result/ 폴더 밑에 검색 결과 파일들이 생성됩니다.");
			System.exit(-1);
		}

		String homePath = cp.executeHomePath();
		String downloadPath = homePath + File.separator + "result/";

		args = new String[] {
				"-cn2000",
				"-auUS",
				"-tg" + homePath + File.separator + "result/%s.txt",
				"-ct" + homePath,
				"-ftTEXT",
				"-efpno,pndate,pnkind,pncn,dockind,ti,%sappno,appdate,prino,inventor,assignee,app_gp,pexam,basic_count,nonpatent,references,citations,ref_count,cur_assignee,cur_assignee_standard,cur_assignee_normalized,cur_assignee_from_date,is_change_assignee,assignee_list,ipc,cpc,ecla,classc,fcode,status,status_date,fees,fees_detail,fees_date,mf,cf,family_count",
				"-ru%s", "-ke" + homePath, "-bwn", "-lm-1"};

		File f = new File(searchRuleFilePath);
		File[] dirs = f.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.lastIndexOf(".tsv") != -1) {
					return true;
				}
				return false;
			}
		});
		for (File dir : dirs) {
			logger.info("read path {}", dir.getAbsolutePath());

			PatentSearchRuleReader ssrr = new PatentSearchRuleReader(dir.getAbsolutePath());
			
//			logger.info("AAAAAAAAAAAAAA");
			LinkedHashMap<String, String> lhm = ssrr.getRules();
//			logger.info("BBBBBBBBBB {}", lhm);

			Set<String> 다운로드파일명 = lhm.keySet();
//			logger.info("CCCCCCCCCCC {}", 다운로드파일명);

//			for (String 파일명 : 다운로드파일명) {
//				logger.info("파일명 / 검색식  : {} => {}", 파일명, lhm.get(파일명));
//			}

			// if(true) System.exit(0);

			int count = 0;

			for (String 파일명 : 다운로드파일명) {
				restrict();
				logger.info("검색을 진행합니다. : {} => {}", 파일명, lhm.get(파일명));
				String dp = downloadPath + File.separator + 파일명 + (파일명.toLowerCase().lastIndexOf(".txt") == -1 ? ".txt" : "");
				String 검색식 = lhm.get(파일명).replaceAll("[\\r|\\n]", "").replaceAll("\\s{1,}", " ").trim();
				/*
				 * 경제적 특성을 추출하려면 특허의 파일명과 동일해야 한다. @since PatentSearcher_2020
				 */
				File downloadFile = new File(dp);
				downloadFile.getParentFile().mkdirs();
				if (downloadFile.isFile()) {
					if (downloadFile.length() < 15000) {

					} else {
						logger.warn("이미 만들 파일은 건너 뛴다. {}", dp);
						continue;
					}
				}

				검색식 = 검색식.replaceAll("“", "\"").replaceAll("”", "\"").trim();

				ExtSearchParam extSearchParam = parsingShopRule(검색식);
				검색식 = extSearchParam.getSearchRule();

				StringBuffer buf = new StringBuffer();
				for (char c : 검색식.toCharArray()) {
					int cc = (int) c;
					if (cc >= 30 || 130 <= cc) {
						// logger.info("{}", cc);
						buf.append(c);
					}
				}
				검색식 = buf.toString();
//				logger.info("searchRule	=>	{}", 검색식.trim());
				logger.info("download path	=>	{}", dp);

				String[] t = new String[args.length];
				for (int idx = 0; idx < t.length; idx++) {
					t[idx] = args[idx];
				}

				t[2] = String.format(args[2],
						파일명.replaceAll("\\s{1,}", "").replaceAll("[-_\\.\\<]", "").replaceAll("\\.txt", "").trim());
				t[5] = String.format(t[5], extSearchParam.isPrintAbs ? "abs," : "");
//				System.out.println("AAAAAAAAAAAA bbbb" + t[5]);
//				logger.info("export field : {}", t[5]);
				t[6] = String.format(t[6], 검색식);
				if(extSearchParam.limit!=-1){
					t[9] = "-lm" + String.valueOf(extSearchParam.limit);
				}
//				System.out.println(t[5]);

				downloadFile = new File(t[2].replaceAll("-tg", ""));
				downloadFile.getParentFile().mkdirs();
				if (downloadFile.isFile()) {
					if (downloadFile.length() < 15000) {

					} else {
						if (검색식.indexOf("APLT=(Y) ") != -1) {
							logger.info("이미 만들 파일은 건너 뛴다. {}", downloadFile.getAbsolutePath());
							continue;
						}
					}
				}

				try {
					if (count > 0) {
						// Dermy d = new Dermy();
						// synchronized (d) {
						// d.wait(1000 * 60 * (new
						// Random(System.nanoTime()).nextInt(5) + 1));
						// }
					}
					// //아래 프로그램에서만 사용하는 코드... 웹 배포시에는 제거되어야 한다.
					new CnProgramDao().insertHistory(InetAddress.getLocalHost().getHostAddress(), t[6], -1,
							"PATENT-DOWNLOAD-PROGRAM");
					PatentSearcher_2021 ps = new PatentSearcher_2021();
					ps.autoriySearch(t);
					count += 1;
				} catch (Exception e) {
					e.printStackTrace();
					// System.exit(1);
				}
			}
		}
		System.exit(1);
	}

	public static class ExtSearchParam {
		public int limit = -1;
		public boolean isPrintAbs = false;
		private String searchRule = null;

		public ExtSearchParam(String rule) {
			this.searchRule = rule;
		}

		public String getSearchRule() {
			final String regex = "(?<=#).+?(?=($|\\s))";
			String sr = this.searchRule.replaceAll(regex, "").replaceAll("#", "").trim().replaceAll("\\s{1,}", " ");
			return sr;
		}

		public String toString() {
			return "Extension Search Param Limit : " + limit + " , isPrintAbs : " + isPrintAbs;
		}
	}

	public static ExtSearchParam parsingShopRule(String src) {
		final String regex = "(?<=#).+?(?=($|\\s))";
		src = src.trim();
		src = src.replaceAll("=\\s{1,}", "=").replaceAll("\\s{1,}=", "=");
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(src);
		ExtSearchParam sp = new ExtSearchParam(src);
		while (m.find()) {
			String group = m.group().toUpperCase();
			// System.out.println(group);
			if (group.contains("LIMIT")) {
				try {
					sp.limit = Integer.parseInt(group.replaceAll("LIMIT=", ""));
//					logger.info("sp.limit : {}, ", sp.limit);
				} catch (NumberFormatException nfe) {
					throw new NumberFormatException("검색결과 제한 건수를 잘못 입력하셨습니다. 숫자만 입력해 주세요." + nfe.getMessage());
				}
			}
			if (group.contains("ABS")) {
				String abs = group.replaceAll("ABS=", "");
				if ("Y".equalsIgnoreCase(abs)) {
					sp.isPrintAbs = true;
				}
			}
		}
//		System.out.println("============> " + src + "\t" + sp + "\t\t : " + sp.getSearchRule());
		return sp;
	}

	private void autoriySearch(String[] args) throws Exception {
		InputParameter param = new InputParameter(args);
		String[] aues = param.authority.split(",");
		if ("ALL".equals(param.authority)) {
			aues = AUTHORITIES;
		}

		String keywordExtractPath = param.keywordExtrctPath;
		logger.info(new File(keywordExtractPath).getAbsolutePath());
		String searchRule = param.searchRule;
		String exportType = param.exportType;
		if (searchRule == null) {
			searchRule = "";
		}

		if (searchRule.length() < 1) {
			for (String au : aues) {
				logger.info("search AU : {}" , au);
				String target = param.targetPath;
				String compress = param.compressFile;
				// AutorityMongoDBExport search = new
				// AutorityMongoDBExport(null, target, compress,
				// param.resultCount, exportType, false, param.exportField,
				// keywordExtractPath, param.multiLineValue, false);
				if (!param.backWard) {
					SearchAuthorityWOS search = new SearchAuthorityWOS(null, au, target, compress, param.resultCount,
							exportType, true, param.exportField, keywordExtractPath, param.multiLineValue, false, param.limit);
					// search.
					search.run();
					// search.close();
				} else {
					PatentFullData queue = new PatentFullData();
					SearchAuthorityWOS search = new SearchAuthorityWOS(queue, au, target, compress, param.resultCount,
							exportType, true, param.exportField, keywordExtractPath, param.multiLineValue, true, param.limit);
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
						exportType, true, param.exportField, keywordExtractPath, param.multiLineValue, false, param.limit);
				search.run();
			} else {
				PatentFullData queue = new PatentFullData();
				SearchAuthorityWOS search = new SearchAuthorityWOS(queue, searchRule, target, compress, param.resultCount,
						exportType, true, param.exportField, keywordExtractPath, param.multiLineValue, true, param.limit);
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
		int limit = -1;
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
			// System.out.println(s);
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
			}else if (s.startsWith("-lm")) {
				logger.debug("Limit : {}", param.trim());
				this.limit= Integer.parseInt(param.trim());
			}
		}

	}

	public static class PatentSearchRuleReader extends AReadFile {

		public PatentSearchRuleReader(String path) throws IOException {
			super(path);
			super.loadFile();
		}

		private LinkedHashMap<String, String> rules = new LinkedHashMap<>();

		public LinkedHashMap<String, String> getRules() {
			return this.rules;
		}

		@Override
		public void readline(String line) {
			if (line == null)
				return;
			line = line.trim();
			if (line.startsWith("@")) {
				String[] datas = line.split("\t");
				if (datas.length > 1) {
					String fileName = datas[0].substring(1).trim();
					String searchRule = datas[1].trim();
					rules.put(fileName, searchRule);
//					System.out.println("put rule : " + fileName);
				}
			}
		}

	}

	public static class ConfProp extends Properties {

		public String executeHomePath() {
			return new File(new Dermy().getClass().getProtectionDomain().getCodeSource().getLocation().getPath())
					.getParentFile().getParent();
		}

		public ConfProp() throws IOException {
			String f = new File(new Dermy().getClass().getProtectionDomain().getCodeSource().getLocation().getPath())
					.getParentFile().getParent();
			String p = new File(f + File.separator + "conf/setup.conf").getAbsolutePath();
			// System.out.println(p);
			if (p.contains("NEON_ECLIPSE_KISTI_PATENT_SEARCHER")) {
				load(new FileInputStream(new File(p)));
			} else {
				System.out.println("설정파일의 위치 : " + new File(f + File.separator + "/conf/setup.conf").getAbsolutePath());
				// System.out.println(new File(f + File.separator +
				// "/conf/setup.conf").isFile());
				load(new FileInputStream(new File(f + File.separator + "/conf/setup.conf")));
			}
		}
	}

	public static class Checker implements Runnable {
		CnProgramDao dao = new CnProgramDao();

		@Override
		public void run() {
			while (true) {
				String ip;
				try {
					ip = InetAddress.getLocalHost().getHostAddress();
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
					ip = "127.0.0.1p";
				}
				if (dao.이미실행중인프로그램인가(ip, "PATENT")) {
					System.out.println("이미 프로그램이 실행중입니다. 기존 프로그램을 종료해주시거나 잠시후에 다시 시도해 주세요.");
					System.exit(-1);
				} else {
					System.out.println("프로그램을 실행하고 있습니다. 잠시만 기다려 주세요.");
					dao.updateChecker(ip, "PATENT");
				}
				synchronized (this) {
					try {
						this.wait(1000 * 60 * 5);
					} catch (InterruptedException e) {
						e.printStackTrace();
						break;
					}
				}
				break;
			}
		}

	}
}
