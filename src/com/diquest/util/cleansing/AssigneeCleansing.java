/**
 * 
 */
package com.diquest.util.cleansing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.diquest.util.StringUtil;

/**
 * BUILDER 프로젝트에 있는 출원인 전거 규칙 클래스.LoadDic.class를 플랫폼에서 사용할 수 있도록 변환<br>
 * 
 * @author coreawin
 * @date 2014. 6. 13.
 * @Version 1.0
 * @see KISTI_PATENT-TECH-SENSING_2013_BUILDER 프로젝트 LoadDic.class
 */
public class AssigneeCleansing {

	public enum DIC_FILE {
		DEFAULT, TEMP, TEMP2
	}

	Logger logger = LoggerFactory.getLogger(getClass());
	private static AssigneeCleansing instance = new AssigneeCleansing();

	String _path = "./";

	private Set<String> _WORD = new HashSet<String>();
	private Set<String> _CHARACTER = new HashSet<String>();

	private Map<String, String> _DEFAULT_DIC = new HashMap<String, String>();
	private Map<String, String> _DEFAULT_DIC_APPEND = new HashMap<String, String>();

	private Map<String, String> _HANJA = new LinkedHashMap<String, String>();
	private Map<String, String> _KOREAN = new LinkedHashMap<String, String>();
	private Map<String, String> _KOREAN_PUBLIC = new LinkedHashMap<String, String>();
	/**
	 * 한국 기관명칭 추출 우선순위<br>
	 * 최대로 많이 출현한 빈도수 데이터에서 한-영 영-한 에 대한 정보가 서로 사이한경우 사용합니다. <br>
	 * 한국어 기준 빈도수가 더 높을 때 이 우선순위를 사용합니다.
	 */
	private Map<String, Integer> _KOREAN_COM_RANKING = new LinkedHashMap<String, Integer>();
	/**
	 * 공공기관 우선순위
	 */
	private Map<String, Integer> _KOREAN_PUBLIC_RANKING = new LinkedHashMap<String, Integer>();

	private final String DEFAULT_DIC_NAME = ".default.patent.dic";

	private String _WORD_REGEX = "(\\s)((?i)%s)($|\\s)";
	private String _CHARACTER_REGEX = "((?i)%s)";

	public String getDic(DIC_FILE dicName, String key) {
		String find = "";
		switch (dicName) {
		case DEFAULT:
			find = _DEFAULT_DIC.get(key);
			if (find == null)
				return "";
			int c = 0;
			while (find.indexOf("@") != -1) {
				find = _DEFAULT_DIC.get(find.replaceAll("@", ""));
				if (find == null) {
					break;
				}
				// 무한루프 방지
				if (find.replaceAll("@", "").equals(key)) {
					logger.debug("find infinity {} / {}", key, find);
					return find;
				}
				c += 1;
				if (c > 10) {
					logger.info("무한루프에 빠졌다. {}, {}", key, find);
					;
					return find;
				}
			}
			return find;
		default:
			break;
		}
		return find;
	}

	/**
	 * 사전을 반환한다.<br>
	 * 
	 * @author coreawin
	 * @date 2014. 10. 31.
	 * @return
	 */
	public Map<String, String> getDefaultDic() {
		return _DEFAULT_DIC;
	}

	public AssigneeCleansing init() {
		try {
			loadDefaultDictionay();
			loadSuffixDic();
			makeRegex();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}

	private AssigneeCleansing() {
	}

	public void loadNDSLDic(String path) throws IOException {
		File f = new File(path);
		if (f.isFile()) {
			// logger.info("NDSL 출원인 사전을 로딩합니다. {}", f.getCanonicalPath());
			// LoadNDSLAssigneeDic ld = new LoadNDSLAssigneeDic(path);
			// _DEFAULT_DIC.putAll(ld.getDic());
		} else {
			throw new FileNotFoundException(f.getPath());
		}
	}

	/**
	 * 사전을 로딩한다.<br>
	 * 
	 * @author coreawin
	 * @throws IOException
	 * @date 2014. 6. 25.
	 */
	private void loadDefaultDictionay() throws IOException {
		File files = new File(_path);
		logger.debug("dictionary path : {}", files.getCanonicalPath());
		files.mkdirs();
		File[] fs = files.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (name.endsWith(DEFAULT_DIC_NAME)) {
					return true;
				}
				return false;
			}
		});
		for (File default_dic : fs) {
			// File default_dic = new File(_path + File.separator +
			// DEFAULT_DIC_NAME);
			logger.debug("load dic {}", default_dic.getCanonicalPath());
			buildDefaultDic(default_dic, _DEFAULT_DIC);
		}
		logger.info("_DEFAULT_DIC size {}", NumberFormat.getInstance().format(_DEFAULT_DIC.size()));
	}

	private void buildDefaultDic(File file, Map<String, String> data) throws IOException {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				// TODO
				String[] values = line.split("\t");
				String k = StringUtil.nullCheck(values[0]).toUpperCase();
				String v = StringUtil.nullCheck(values[1]);
				if (_DEFAULT_DIC.containsKey(k) == false) {
					_DEFAULT_DIC.put(k, v);
				}
			}
			logger.info("load dic complete : {}", file.getCanonicalPath());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (br != null)
				br.close();
		}
	}

	StringBuffer _buf = new StringBuffer();

	private void makeRegex() {
		_buf.setLength(0);
		for (String _s : _WORD) {
			_buf.append("(");
			_buf.append(_s);
			_buf.append(")");
			_buf.append("|");
		}
		_WORD_REGEX = String.format(_WORD_REGEX, _buf.toString());

		_buf.setLength(0);
		for (String _s : _CHARACTER) {
			_buf.append("(");
			_buf.append(_s);
			_buf.append(")");
			_buf.append("|");
		}
		_CHARACTER_REGEX = String.format(_CHARACTER_REGEX, _buf.substring(0, _buf.length() - 1));
		logger.debug("_WORD_REGEX : {} ", _WORD_REGEX);
		logger.debug("_CHARACTER_REGEX : {} ", _CHARACTER_REGEX);
	}

	/**
	 * 기관명에서 제거할 대상 Suffix의 정규식을 반환한다.
	 * 
	 * @author coreawin
	 * @date 2014. 6. 13.
	 * @return
	 */
	public String getFindSuffixReg() {
		return _WORD_REGEX;
	}

	private void loadSuffixDic() {
		// TODO 2014-06-13 파일에서 사전을 읽어들일수 있어야 한다.
		_WORD.add("llc");
		_WORD.add("inc");
		_WORD.add("ltd");
		_WORD.add("co");
		_WORD.add("corp");
		_WORD.add("coporation");
		_WORD.add("Corporation");
		_WORD.add("CORPORATION");
		_WORD.add("limited");
		// _WORD.add("INSTITUTE"); /*국가 기관명칭에 포함될수 있다. */
		_WORD.add("Inc");
		_WORD.add("Incorporation");
		_WORD.add("Incorporated");
		_WORD.add("spa");
		_WORD.add("gmbh");
		_WORD.add("엘티디");
		_WORD.add("리미티드");
		_WORD.add("컴퍼니");
		_WORD.add("캄파니");
		_WORD.add("캄퍼니");
		_WORD.add("엘엘씨");
		_WORD.add("엘엘시");
		_WORD.add("엘티디");

		_CHARACTER.add("\\(");
		_CHARACTER.add("\\)");
		// _CHARACTER.add("\\&");
		_CHARACTER.add("/");
		_CHARACTER.add("\\+");
		_CHARACTER.add("\\?");
		_CHARACTER.add("˝");
		_CHARACTER.add("〃");
		_CHARACTER.add("\\–");
		_CHARACTER.add("―");
		_CHARACTER.add("\\“");
		_CHARACTER.add("\\”");
		_CHARACTER.add("\\＇");
		_CHARACTER.add("\\˝");
		_CHARACTER.add("\\＂");
		_CHARACTER.add("\\・");
		_CHARACTER.add("\\・");
		_CHARACTER.add("\\．");
		_CHARACTER.add("\\，");
		_CHARACTER.add("\\・");
		_CHARACTER.add("\\：");
		_CHARACTER.add("\\、");
		// _CHARACTER.add("\\ー");
		_CHARACTER.add("\\－");

		// 일본어
		_KOREAN.put("가부시끼가이야 ", " 가부시키 ");
		_KOREAN.put("가부시끼 가이야 ", " 가부시키 ");
		_KOREAN.put("가부시끼가아샤", " 가부시키 ");
		_KOREAN.put("가부시끼 가아샤", " 가부시키 ");
		_KOREAN.put("가부시기 가이샤", " 가부시키 ");
		_KOREAN.put("가부시기가이샤", " 가부시키 ");
		_KOREAN.put("가부시끼가이샤", " 가부시키 ");
		_KOREAN.put("가부시끼 가이샤", " 가부시키 ");
		_KOREAN.put("가부사키가이샤", " 가부시키 ");
		_KOREAN.put("가부사끼가이샤", " 가부시키 ");
		_KOREAN.put("가부사키 가이샤", " 가부시키 ");
		_KOREAN.put("가부사끼 가이샤", " 가부시키 ");
		_KOREAN.put("가부시키 가이샤", " 가부시키 ");
		_KOREAN.put("가부시키가이샤", " 가부시키 ");
		_KOREAN.put("가부시키 가이샤", " 가부시키 ");
		_KOREAN.put("가부끼가이샤", " 가부시키 ");
		_KOREAN.put("가부끼 가이샤", " 가부시키 ");
		_KOREAN.put("K K", " KK "); /* 일본 영문 주식회사 표현법 */
		_KOREAN.put("가부시끼", " 가부시키가이샤 ");
		_KOREAN.put("가부시키", " 가부시키가이샤 ");
		_KOREAN.put("고교", " 고교 ");
		_KOREAN.put("고요교", " 고교 ");
		_KOREAN.put("고오교오", " 고교 ");

		// 한국 기업명
		_KOREAN.put("\\&", " & ");
		_KOREAN.put("서어비시즈", " 서비스 ");
		_KOREAN.put("서어비스", " 서비스 ");
		_KOREAN.put("서어비시즈", " 서비스 ");
		_KOREAN.put("서어비시즈", " 서비스 ");
		_KOREAN.put("서비시스", " 서비스 ");

		_KOREAN.put("리서어치", " 리서치 ");
		_KOREAN.put("리서취", " 리서치 ");
		_KOREAN.put("리써치", " 리서치 ");

		_KOREAN.put("콤퍼니", " 컴퍼니");
		_KOREAN.put("컴파니", " 컴퍼니 ");
		_KOREAN.put("컴파니", " 컴퍼니 ");
		_KOREAN.put("콤파니", " 컴퍼니 ");
		_KOREAN.put("컴퍼니", " 컴퍼니 ");
		_KOREAN.put("컴패니", " 컴퍼니 ");
		_KOREAN.put("컴페니", " 컴퍼니 ");
		_KOREAN.put("캄파니", " 컴퍼니 ");

		_KOREAN.put("인터내셔널", " 인터내셔널 ");
		_KOREAN.put("인터내쇼날", " 인터내셔널 ");
		_KOREAN.put("인터네셔날", " 인터내셔널 ");

		_KOREAN.put("리미티드", " ");

		_KOREAN.put("코오포레이숀", " 코포레이션 ");
		_KOREAN.put("코포레이션", " 코포레이션 ");
		_KOREAN.put("코포레이션", " 코포레이션 ");
		_KOREAN.put("코오아퍼레이션", " 코포레이션 ");

		_KOREAN.put("지엠비에이취", " 게엠베하 ");
		_KOREAN.put("게엠베하", " 게엠베하 ");
		_KOREAN.put("게엠바하", " 게엠베하 ");
		_KOREAN.put("지엠비 에취", " 게엠베하 ");

		_KOREAN.put("인코포레이티드", " 인코퍼레이티드 ");
		_KOREAN.put("인코퍼레이티드", " 인코퍼레이티드 ");
		_KOREAN.put("인코오퍼레이티드", " 인코퍼레이티드 ");
		_KOREAN.put("인코오포레이티드", " 인코퍼레이티드 ");

		_KOREAN.put("인크", " 인코퍼레이티드 ");
		_KOREAN.put("아이엔씨", " 인코퍼레이티드 ");
		_KOREAN.put("인스티튜트", " 인스티튜트 ");

		_KOREAN.put("디벨로프먼트", " 디벨럽먼트 ");
		_KOREAN.put("디벨로프먼트", " 디벨럽먼트 ");
		_KOREAN.put("디벨롭먼트", " 디벨럽먼트 ");
		_KOREAN.put("디벨럽먼트", " 디벨럽먼트 ");
		_KOREAN.put("디벨럽먼트", " 디벨럽먼트 ");

		_KOREAN.put("유니버서티", " 유니버시티 ");
		_KOREAN.put("테크놀로지즈", " 테크놀러지 ");
		_KOREAN.put("테크놀로지스", " 테크놀러지 ");

		_KOREAN.put("주시회사", " 주식회사 ");
		_KOREAN.put("주식화사", " 주식회사 ");
		_KOREAN.put("주식회시", " 주식회사 ");
		_KOREAN.put("（주）", " 주식회사 ");
		_KOREAN.put("㈜", " 주식회사 ");
		_KOREAN.put("\\<주\\>", " 주식회사 ");
		_KOREAN.put("\\[주\\]", " 주식회사 ");
		_KOREAN.put("\\(주\\)", " 주식회사 ");
		_KOREAN.put("\\(주식회사\\)", " 주식회사 ");
		_KOREAN.put("\\(주식 회사\\)", " 주식회사 ");
		_KOREAN.put("주식회사", " 주식회사 ");
		_KOREAN.put("주식 회사", " 주식회사 ");
		_KOREAN.put("\\(유한회사\\)", " 유한회사 ");
		_KOREAN.put("\\(유한 회사\\)", " 유한회사 ");
		_KOREAN.put("유한회사", " 유한회사 ");
		_KOREAN.put("유한 회사", " 유한회사 ");
		_KOREAN.put("\\(유\\)", " 유한회사 ");
		_KOREAN.put("\\(합\\)", " 합자회사 ");
		_KOREAN.put("\\(합자\\)", " 합자회사 ");
		_KOREAN.put("합자회사", " 합자회사 ");
		_KOREAN.put("합자 회사", " 합자회사 ");
		_KOREAN.put("\\(특수법인\\)", " 특수법인 ");
		_KOREAN.put("\\(특수 법인\\)", " 특수법인 ");
		_KOREAN.put("\\(재단법인\\)", " 재단법인 ");
		_KOREAN.put("\\(재단 법인\\)", " 재단법인 ");
		_KOREAN.put("재단법인", " 재단법인 ");
		_KOREAN.put("재단 법인", " 재단법인 ");
		_KOREAN.put("학교 법인", " "); /*
									 * 연세대학교 와 학교법인 연세대학교는 같으므로 학교법인 삭제
									 * 2014-07-16
									 */
		_KOREAN.put("학교법인", " "); /* 연세대학교 와 학교법인 연세대학교는 같으므로 학교법인 삭제 2014-07-16 */
		_KOREAN.put("\\(재\\)", " 재단법인 ");

		_KOREAN_PUBLIC.put("서울특별시", " ");
		_KOREAN_PUBLIC.put("대학교", "대학교 ");
		_KOREAN_PUBLIC.put("대한민국", " ");
		_KOREAN_PUBLIC.put("관리부서", " ");
		_KOREAN_PUBLIC.put("총장", " ");
		_KOREAN_PUBLIC.put("원장", "원");
		_KOREAN_PUBLIC.put("소장", "소");
		_KOREAN_PUBLIC.put("관장", "관");
		_KOREAN_PUBLIC.put("장관", " ");
		_KOREAN_PUBLIC.put("청장", "청");
		_KOREAN_PUBLIC.put("처장", "처");
		_KOREAN_PUBLIC.put("센터장", "센터");
		_KOREAN_PUBLIC.put("센터소", "센터");
		_KOREAN_PUBLIC.put("센터소장", "센터");
		_KOREAN_PUBLIC.put("사업장장", "사업장");
		_KOREAN_PUBLIC.put("군청", "군");

		int idx = 0;
		_KOREAN_PUBLIC_RANKING.put("대학교", idx++);
		_KOREAN_PUBLIC_RANKING.put("대학", idx++);
		_KOREAN_PUBLIC_RANKING.put("부", idx++);
		_KOREAN_PUBLIC_RANKING.put("처", idx++);
		_KOREAN_PUBLIC_RANKING.put("청", idx++);
		_KOREAN_PUBLIC_RANKING.put("원", idx++);
		_KOREAN_PUBLIC_RANKING.put("연구소", idx++);
		_KOREAN_PUBLIC_RANKING.put("연구원", idx++);
		_KOREAN_PUBLIC_RANKING.put("센터", idx++);
		_KOREAN_PUBLIC_RANKING.put("사업소", idx++);
		_KOREAN_PUBLIC_RANKING.put("사업장", idx++);
		_KOREAN_PUBLIC_RANKING.put("도", idx++);
		_KOREAN_PUBLIC_RANKING.put("시", idx++);
		_KOREAN_PUBLIC_RANKING.put("군", idx++);

		idx = 0;

		// Collection<String> _c = _KOREAN.values();
		// Iterator<String> _citer = _c.iterator();
		// while (_citer.hasNext()) {
		// String v = _citer.next().trim();
		// if (!_KOREAN_COM_RANKING.containsKey(v)) {
		// _KOREAN_COM_RANKING.put(v, idx++);
		// }
		// }

		_KOREAN_COM_RANKING.put("주식회사", idx++);
		_KOREAN_COM_RANKING.put("재단법인", idx++);

		_KOREAN_COM_RANKING.put("가부끼가이샤", idx++);
		_KOREAN_COM_RANKING.put("가부시키", idx++);
		_KOREAN_COM_RANKING.put("가부시키가이샤", idx++);

		_KOREAN_COM_RANKING.put("게엠베하", idx++);
		_KOREAN_COM_RANKING.put("디벨럽먼트", idx++);
		_KOREAN_COM_RANKING.put("테크놀러지", idx++);

		_KOREAN_COM_RANKING.put("컴퍼니", idx++);
		_KOREAN_COM_RANKING.put("리미티드", idx++);
		_KOREAN_COM_RANKING.put("인코퍼레이티드", idx++);
		_KOREAN_COM_RANKING.put("유니버시티", idx++);
		_KOREAN_COM_RANKING.put("인스티튜트", idx++);
		_KOREAN_COM_RANKING.put("디벨럽먼트", idx++);

		_KOREAN_COM_RANKING.put("아이엔씨", idx++);
		_KOREAN_COM_RANKING.put("코포레이션", idx++);
		_KOREAN_COM_RANKING.put("엘엘씨", idx++);
		_KOREAN_COM_RANKING.put("센터", idx++);
		_KOREAN_COM_RANKING.put("인터내셔날", idx++);
		_KOREAN_COM_RANKING.put("인터내셔널", idx++);
		_KOREAN_COM_RANKING.put("리서치", idx++);
		_KOREAN_COM_RANKING.put("서비스", idx++);

		_KOREAN_COM_RANKING.put("유한회사", idx++);
		_KOREAN_COM_RANKING.put("합자회사", idx++);
		_KOREAN_COM_RANKING.put("특수법인", idx++);
		_KOREAN_COM_RANKING.put("재단법인", idx++);

		_HANJA.put("株式会社", " 株式会社 ");
		_HANJA.put("株式會社", " 株式會社 ");

		// for (String s : _KOREAN_PUBLIC_RANKING.keySet()) {
		// System.out.println(s + "\t" + _KOREAN_PUBLIC_RANKING.get(s));
		// }
	}

	public String removeSpecialCharacter(String name) {
		name = name.replaceAll("[“”]", " ");
		name = name.replaceAll("[\\.]", " ");
		name = name.replaceAll("[,:\"'\\-;]", " ");
		name = name.replaceAll("(\\(\\s{1,})", " (");
		name = name.replaceAll("(\\s{1,}\\))", ") ");
		name = name.replaceAll("(\\()", " (");
		name = name.replaceAll("(\\))", ") ");

		name = name.replaceAll("(\\))", " ");
		name = name.replaceAll("(\\()", " ");
		name = name.replaceAll("\\?", " ");
		name = name.replaceAll("&", " ");
		// logger.debug(this._CHARACTER_REGEX);
		// name = name.replaceAll(this._CHARACTER_REGEX, " ");
		// name = name.replaceAll(this._CHARACTER_REGEX, " ");
		name = name.replaceAll(this._CHARACTER_REGEX, " ");
		return name.trim();
	}

	public String cleansingPublic(String org) {
		// if (org.indexOf("대한민국") != -1) {
		Set<String> keySet = _KOREAN_PUBLIC.keySet();
		boolean 공공기관이냐 = false;
		for (String key : keySet) {
			if (org.indexOf(key) != -1) {
				공공기관이냐 = true;
				org = org.replaceAll(key, _KOREAN_PUBLIC.get(key)).trim();
			}
		}
		org = removeSpecialCharacter(org);
		if (공공기관이냐) {
			org = 공공기관우선순위(org);
		}
		if (org.indexOf(" ") == -1) {
			org = org.replaceAll("[\\|(\\)]", "");
		}
		// }
		return org;
	}

	Deque<String> _tempRaking = new LinkedList<String>();

	public String 공공기관우선순위(String org) {
		_tempRaking.clear();
		_tempRaking.add("1_10000");
		String[] values = org.split(" ");
		String r = org;
		for (String v : values) {
			Set<String> ks = _KOREAN_PUBLIC_RANKING.keySet();
			v = v.replaceAll("[\\(\\)]", "").trim();
			if (_KOREAN_COM_RANKING.containsKey(v)) {
				return org;
			}
			for (String k : ks) {
				// System.out.println("==> " + v + "\t" + k + "\t" +
				// v.indexOf(k) + "\t" + _tempRaking);
				if (v.endsWith(k)) {
					Integer ranking = _KOREAN_PUBLIC_RANKING.get(k);
					String prev = _tempRaking.getFirst();
					String up = v + "_" + ranking;
					int prevR = Integer.parseInt(prev.split("_")[1]);
					// System.out.println(ranking +"\t" + prevR);
					if (ranking < prevR) {
						_tempRaking.addFirst(up);
						break;
					}

				}
			}
		}
		r = _tempRaking.getFirst().split("_")[0];
		if ("1".equals(r)) {
			return org;
		} else {
			return r;
		}
	}

	public static AssigneeCleansing getInstance() {
		return instance;
	}

	/**
	 * 사전이 위치한 경로를 설정합니다.<br>
	 * default는 현재 디렉토리입니다.<br>
	 * 
	 * @author coreawin
	 * @date 2014. 6. 25.
	 * @param path
	 */
	public void setDicPath(String path) {
		logger.info("사전이 위치한 경로를 다음 경로로 설정합니다. {}", path);
		this._path = path;
	}

	public Set<String> getSUFFIXInfo() {
		return _WORD;
	}

	public BufferedWriter getWriter(String prefix, DIC_FILE dicFile) throws IOException {
		File default_dic_file = new File(this._path + File.separator + prefix + "." + dicFile.name().toLowerCase()
				+ ".patent.dic");
		return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(default_dic_file, false)));
	}

	public String compareKoreanComCompare(String src1, String src2) {
		String[] src1a = src1.split(" ");
		int src1r = Integer.MAX_VALUE;
		String[] src2a = src2.split(" ");
		int src2r = Integer.MAX_VALUE;

		int tm = src1r;
		boolean src1ch = false;
		for (String a1 : src1a) {
			if (_KOREAN_COM_RANKING.containsKey(a1)) {
				tm = _KOREAN_COM_RANKING.get(a1);
				if (tm < src1r) {
					src1r = tm;
					src1ch = true;
				}
			} else {

			}
		}

		tm = src2r;
		boolean src2ch = false;
		for (String a2 : src2a) {
			if (_KOREAN_COM_RANKING.containsKey(a2)) {
				tm = _KOREAN_COM_RANKING.get(a2);
				if (tm < src2r) {
					src2r = tm;
					src2ch = true;
				}
			} else {

			}
		}

		if (!src1ch & !src2ch) {
			return src1;
		}

		if (src1r < src2r) {
			return src1;
		} else if (src1r > src2r) {
			return src2;
		} else {
			return src1;
		}
	}

	public String getCleansingKoreanDic(String src) {
		Set<String> keySet = _KOREAN.keySet();
		for (String key : keySet) {
			src = src.replaceAll(key, _KOREAN.get(key));
		}
		src = cleansingPublic(src);
		return src;
	}

	public String getDic(String lang, String src) {
		String r = "";
		// logger.debug("lang {}, src {}", lang, src);
		src = ruleHanja(src);
		// logger.debug("result :  {}", r);
		if ("kor".equalsIgnoreCase(lang)) {
			r = ruleKor(src);
		} else {
			r = rule(src);
		}
		// logger.debug("r :  {}", r);
		String result = getDic(DIC_FILE.DEFAULT, r);
		// logger.debug("result3 :  {}", getDic(DIC_FILE.DEFAULT, r));
		if ("".equals(result)) {
			return r;
		} else {
			return result;
		}
	}

	public String getDic2(String lang, String src) {
		String r = "";
		if ("kor".equalsIgnoreCase(lang)) {
			r = ruleKor(src);
		} else {
			r = rule(src);
			// logger.debug("r {}", r);
		}
		return getDic(DIC_FILE.DEFAULT, r);
	}

	/**
	 * 괄호안에 들어 있는 문자는 제거한다.<br>
	 * 
	 * @author coreawin
	 * @date 2014. 8. 8.
	 * @param src
	 * @return
	 */
	private String removeBraceWord(String src) {
		return src.replaceAll("(\\(.*?\\))", " ");

	}

	public String rule(String orgName) {
		if (orgName == null)
			return orgName;
		orgName = removeBraceWord(orgName);
		orgName = removeSpecialCharacter(orgName);
		orgName = orgName.replaceAll(getFindSuffixReg(), " ");
		orgName = orgName.replaceAll(getFindSuffixReg(), " ");
		orgName = orgName.replaceAll(getFindSuffixReg(), " ");
		orgName = orgName.replaceAll("(\\s{1,})", " ");
		orgName = ruleHanja(orgName);
		return orgName.trim().toUpperCase();
	}

	public String ruleKor(String orgName) {
		if (orgName == null)
			return orgName;
		orgName = orgName.replaceAll(getFindSuffixReg(), " ");
		orgName = orgName.replaceAll("(,.*)", " ");
		orgName = getCleansingKoreanDic(orgName);
		orgName = removeSpecialCharacter(orgName);
		orgName = orgName.replaceAll(getFindSuffixReg(), " ");
		orgName = orgName.replaceAll(getFindSuffixReg(), " ");
		orgName = orgName.replaceAll(getFindSuffixReg(), " ");
		orgName = orgName.replaceAll("(\\s{1,})", " ");
		orgName = ruleHanja(orgName);
		return orgName.trim().toUpperCase();
	}

	public String ruleHanja(String orgName) {
		orgName = getCleansingHanjaDic(orgName);
		orgName = orgName.replaceAll("(\\s{1,})", " ");
		return orgName.trim();
	}

	private String getCleansingHanjaDic(String src) {
		Set<String> keySet = _HANJA.keySet();
		String r = src;
		for (String key : keySet) {
			r = r.replaceAll(key, _HANJA.get(key));
		}
		return r.toUpperCase();
	}

	public static void main(String... args) {
		AssigneeCleansing d = AssigneeCleansing.getInstance();
//		d.setDicPath("f:\\Documents\\Project\\2014\\논문특허 DB시스템 개선 운영\\특허 데이터 전거\\한국특허\\");
		d.init();
//		System.out.println(d.getDic("jpn", "三星電子株式会社"));
//		System.out.println(d.ruleHanja("三星電子株式会社"));
//		System.out.println(d.ruleHanja("三星ディスプレイ株式会社"));
//		System.out.println(d.ruleHanja("三星ディスプレイ株式會社"));
//		System.out.println(d.getDic("eng", "SK TELECOM CO., LTD."));
//		System.out.println(d.getDic("kor", "SK TELECOM CO., LTD."));
//		System.out.println(d.getDic("kor", "대한민국 (관리부서:요업기술원)"));
//		System.out.println(d.getDic("eng", "DEERE & CO"));
//		// 괄호 안의 문자는 삭제.
//		System.out.println(d.getDic("eng", "KINGSPAN (korea) INSULATED PANELS INC (USA)"));
//		System.out.println(d.getDic("eng", "KINGSPAN (korea)INSULATED PANELS INC (USA)"));
//		System.out.println(d.rule("Wuxi Imprint Nano Technology Co.,Ltd."));
//		System.out.println(d.rule("NANHAI OIL INDUSTRY (CHIWAN) CO., LTD."));
//
//		System.out.println(d.rule("グローバル・オーエルイーディー・テクノロジー・リミテッド・ライアビリティ・カンパニー"));
//		System.out.println(d.getDic("jpn", "グローバル・オーエルイーディー・テクノロジー・リミテッド・ライアビリティ・カンパニー"));
//		System.out.println(d.ruleKor("Wuxi Imprint Nano Technology Co.,Ltd."));
//		System.out.println(d.ruleKor("NANHAI OIL INDUSTRY (주식 회사)"));
		System.out.println("========");
		System.out.println(d.rule("Opticul Diagnostics Ltd."));
		System.out.println(d.ruleKor("Opticul Diagnostics Ltd."));
		System.out.println(d.rule("Opticul Diagnostics Inc."));
	}

}
