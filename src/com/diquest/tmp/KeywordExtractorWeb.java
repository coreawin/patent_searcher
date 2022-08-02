/**
 * 
 */
package com.diquest.tmp;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.diquest.kisti.patent.keyword.KeywordExtractor;
import com.diquest.kisti.patent.keyword.db.StopWord;
import com.diquest.kisti.patent.keyword.extract.NPExtractor;

//import com.diquest.kisti.patent.keyword.KeywordExtractor;

/**
 * 
 * 키워드 추출
 * 
 * @author neon
 * @date 2013. 8. 23.
 * @Version 1.0
 */
public class KeywordExtractorWeb {

	private static KeywordExtractorWeb instance = null;
	KeywordExtractor ke = null;

	private KeywordExtractorWeb(String homeDir) throws FileNotFoundException, SQLException, IOException {
		ke = new KeywordExtractor(homeDir);
	}

	public static KeywordExtractorWeb getInstance(String homeDir) throws FileNotFoundException, SQLException, IOException {
		Logger logger = LoggerFactory.getLogger(KeywordExtractorWeb.class);
		if (instance == null) {
			logger.debug("KeywordExtractorWeb home dir " + homeDir);
			instance = new KeywordExtractorWeb(homeDir);
		}
		return instance;
	}

	public static KeywordExtractorWeb getInstance() throws Exception {
		if (instance == null) {
			throw new InstantiationException("홈 디렉토리가 설정되어 있지 않아 인스턴스를 얻을 수 없습니다.");
		}
		return instance;
	}

	public void setMinWordNum(int len) {
		synchronized (ke) {
			if (ke != null) {
				ke.setMinWordNum(len);
			}
		}
	}

	public void setOption(int op) {
		if (ke != null) {
			ke.setOption(op);
		}
	}

	public void setMaxWordNum(int len) {
		synchronized (ke) {
			if (ke != null) {
				ke.setMaxWordNum(len);
			}
		}
	}

	public void setThreshold(float threshold) {
		synchronized (ke) {
			if (ke != null) {
				ke.setThreshold(threshold);
			}
		}
	}

	public void setMinNPLen(int len) {
		synchronized (ke) {
			if (ke != null) {
				ke.setMinNPLen(len);
			}
		}
	}

	public void setStopWord(HashMap<String, String> r) {
		synchronized (ke) {
			if(ke!=null){
				ke.setStopWord(r);
			}
		}
	}

	public Entry<String, Float>[] extract(String[] inputs, int[] weights) {
		synchronized (ke) {
			if (ke != null) {
				return ke.search(inputs, weights);
			}
		}
		return null;
	}

	/**
	 * 분석 DB에 등록된 불용어를 재 로딩한다.<br>
	 * 
	 * @author neon
	 * @date 2013. 10. 8.
	 */
	public void reloadStopWordData() {
		try {
			StopWord.getInstance().setInitInstance();
			StopWord.getInstance();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 키워드를 추출한다.
	 * 
	 * @author neon
	 * @date 2013. 9. 27.
	 * @param kehome
	 *            키워드 추출 모듈 홈 디렉토리
	 * @param inputs
	 *            입력 문자열
	 * @param weights
	 *            입력 문자열에 대한 가중치
	 * @param cutOff
	 *            cut off threshold
	 * @param sizeCutOff
	 *            최대 추출 개수.
	 * @return
	 * @throws Exception
	 */
	public Set<String> getKeywordSet(String[] inputs, int[] weights, float cutOff, int sizeCutOff) throws Exception {
		Set<String> r = new LinkedHashSet<String>();
		KeywordExtractorWeb ke = null;
		try {
			ke = KeywordExtractorWeb.getInstance();
			ke.setMinNPLen(1);
			ke.setMinWordNum(1);
			ke.setMaxWordNum(4);
			ke.setOption(NPExtractor.OPT_TOSTEM | NPExtractor.OPT_EXTEND);
			Entry<String, Float>[] result = ke.extract(inputs, weights);
			Set<String> keywordSet = new LinkedHashSet<String>();
			for (int i = 0; i < result.length; i++) {
				float v = result[i].getValue();
				if (v >= cutOff && keywordSet.size() <= sizeCutOff) {
					keywordSet.add(result[i].getKey());
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return r;
	}

	/**
	 * 키워드를 추출한다.<br>
	 * 이미 KeywordExtractorWeb 인스턴스가 생성되어야 사용할 수 있다.<br>
	 * 최소 1단어 이상 추출 <br>
	 * threshold = 1<br>
	 * 최대 추출개수 = 10<br>
	 * 
	 * @author neon
	 * @date 2013. 9. 27.
	 * @param inputs
	 *            입력 문자열
	 * @param weights
	 *            입력 문자열에 대한 가중치
	 * @return
	 * @throws Exception
	 */
	public Set<String> getKeywordSet(String[] inputs, int[] weights) throws Exception {
		float cutOff = 1;
		int sizeCutOff = 10;
		Set<String> keywordSet = new LinkedHashSet<String>();
		KeywordExtractorWeb ke = KeywordExtractorWeb.getInstance();
		try {
			ke.setMinNPLen(1);
			ke.setMinWordNum(1);
			ke.setMaxWordNum(4);
			ke.setOption(NPExtractor.OPT_TOSTEM | NPExtractor.OPT_EXTEND);
			Entry<String, Float>[] result = ke.extract(inputs, weights);
			for (int i = 0; i < result.length; i++) {
				float v = result[i].getValue();
				// System.out.println(result[i].getKey() +"\t" + v);
				// if (v >= cutOff && keywordSet.size() < sizeCutOff) {
				if (keywordSet.size() < sizeCutOff) {
					keywordSet.add(result[i].getKey() + "_" + v);
				}
			}
		} catch (Exception e) {
			System.out.println("==============================");
			for (String s : inputs) {
				System.out.println(s);
				System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");
			}
			throw e;
		}
		return keywordSet;
	}

	public static void main(String[] args) throws Exception {
		String[] inputs = new String[2];
		inputs[0] = "Industrial stator vane with sequential impingement cooling inserts";
		inputs[1] = "A turbine stator vane for an industrial engine, the vane having two impingement cooling inserts that produce a series of impingement cooling from the pressure side to the suction side of the vane walls. Each insert includes a spar with a row of alternating impingement cooling channels and return air channels extending in a radial direction. Impingement cooling plates cover the two sides of the insert and having rows of impingement cooling holes aligned with the impingement cooling channels and return air openings aligned with the return air channel.";
		inputs[0] = "Reactor vessel for minimizing ECC bypass";
		inputs[1] = "PURPOSE: A reactor vessel with an ECC(emergency core cooling) bypass reduction function is provided to prevent an LOCA(Loss-Of-Coolant Accident) due to the separation of an installation structure by removing an additional installation structure.<br>CONSTITUTION: A reactor vessel(100) has a preset thickness. A nozzle part(110) is installed in the reactor vessel and supplies ECC water into the reactor vessel. A flow path(111) is vertically engraved in an inner wall(101) of the reactor vessel. A transverse section of the flow path is formed with a curved surface.<br>COPYRIGHT KIPO 2013<br>";
		inputs[0] = "METHOD FOR THE PRE-PROCESSING OF A THREE-DIMENSIONAL IMAGE OF THE SURFACE OF A TYRE USING SUCCESSIVE B-SPLINE DEFORMATIONS";
		inputs[1] = "<p>A method for inspecting a tyre surface involves comparison with an image of a three-dimensional (“3D”) reference surface. The method includes: extracting contours of graphic elements of an image of a 3D profile of a tyre surface to be inspected; locating characteristic points on the image of the tyre surface, and pairing the characteristic points with corresponding reference characteristic points on the image of the reference surface; associating a first reset B-spline surface with the reference surface by associating the reference characteristic points of the image of the reference surface with control points of the first reset B-spline surface; and deforming the reference surface by moving the control points of the first reset B-spline surface so as to superpose the control points on the characteristic points of the tyre surface, in accordance with the reference characteristic points of the reference surface paired with the characteristic points of the tyre surface.</p>";
		inputs[0] = "COMPOSITION FOR FORMATION OF CARBON DIOXIDE SEPARATION MEMBRANE, CARBON DIOXIDE SEPARATION MEMBRANE AND PROCESS FOR PRODUCTION THEREOF, AND CARBON DIOXIDE SEPARATION APPARATUS";
		inputs[1] = "";
		inputs[0] = "On the basis of flue gas carbon dioxide capture membrane contactor compounded absorbent and method of use";
		inputs[1] = "";
		KeywordExtractorWeb i = KeywordExtractorWeb.getInstance("t:\\release\\keyword_extract\\");
		Set<String> sets = i.getKeywordSet(inputs, new int[] { 10000, 1 });
		for (String s : sets) {
			System.out.println(s);
		}
	}

}
