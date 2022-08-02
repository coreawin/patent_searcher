package com.diquest.unicode.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.Character.UnicodeBlock;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @see http://www.unicode.org/Public/8.0.0/ucd/
 * 
 * 
 * @author coreawin
 * @date 2015. 9. 1.
 */
public class UnicodeUtility {

	static Logger logger = LoggerFactory.getLogger(UnicodeUtility.class);

	private static UnicodeUtility instance = new UnicodeUtility();

	private Map<String, String> unicodeMap = new HashMap<String, String>();

	private UnicodeUtility() {
		loadData();
	}

	public static void main(String... args) {
		try {
			String a = UnicodeUtility.getInstance().convertUnicode("ＷＡＦＥＲ ＩＮＴＥＧＲＡＴＩＯＮ 株式会社	WAFER INTEGRATION");
			System.out.println(a);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static UnicodeUtility getInstance() {

		return instance;
	}

	private void loadData() {
		BufferedReader br = null;
		try {
			InputStream is = getClass().getResourceAsStream("/com/diquest/unicode/resources/unicodeData.txt");
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String line = null;
			try {
				while ((line = br.readLine()) != null) {
					String[] datas = line.split(";");
					unicodeMap.put(datas[0].trim().toUpperCase(), datas[5].replaceAll("<wide>", "").trim().toUpperCase());
				}
			} catch (Exception e) {
				logger.error("======= err line : {}", line);
				e.printStackTrace();
			}
			logger.info("load unicode mapping table {}", unicodeMap.size());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	/**
	 * ＶＩＥＷＳＥＮＤ 로 표기되는 문자를 viewsend로 변환하는 메소드.
	 * 
	 * @author pc
	 * @date 2015. 9. 1.
	 * @param src
	 *            입력 문자
	 * @return
	 * @throws Exception
	 */
	public String convertUnicode(String src) {
		StringBuffer buf = new StringBuffer();
		char[] t = src.toCharArray();
		for (int ic = 0; ic < t.length; ic++) {
			UnicodeBlock block = UnicodeBlock.of(t[ic]);
//			System.out.println(t[ic] + "\t" + block);
			if (block == UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
				char ch = t[ic];
				String compatiblityCode = Integer.toHexString(Character.codePointAt(new char[] { ch }, 0)).toUpperCase();
				String unifiedCode = unicodeMap.get(compatiblityCode);
				if (unifiedCode != null) {
					int cc = Integer.parseInt(unifiedCode, 16);
					buf.append((char) cc);
//					System.out.println(new String(new char[] { ch }) + ", " + compatiblityCode
//							+ "\t HALFWIDTH_AND_FULLWIDTH_FORMS : " + unifiedCode + "\t" + (char) cc);
				}
				continue;
			}
			buf.append(t[ic]);
		}
		return buf.toString();
	}

}
