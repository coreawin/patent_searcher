package com.diquest.util;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utility {

	static Logger logger = LoggerFactory.getLogger(Utility.class);
	static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	static final SimpleDateFormat convertorFormat = new SimpleDateFormat("yyyyMMdd");
	static final NumberFormat numberFormat = NumberFormat.getInstance();
	static final java.sql.Date sqlDate = new java.sql.Date(System.currentTimeMillis());

	static final NumberFormat pointFormat = new DecimalFormat("###,###.#####");

	public static String pointCutFormat(double d) {
		if (d == 0.0) {
			return pointFormat.format(d);
		}
		return pointFormat.format(100d - (d * 100));
	}

	/**
	 * @return 현재 날짜를 얻어온다.<br>
	 *         yyyy-MM-dd
	 */
	public static String getCurrentDate() {
		return simpleDateFormat.format(new Date());
	}

	/**
	 * @return 현재 연도를 얻어온다.<br>
	 *         yyyy-MM-dd
	 */
	public static int getCurrentYear() {
		return GregorianCalendar.getInstance().get(GregorianCalendar.YEAR);
	}

	/**
	 * @return 현재 날짜를 얻어온다.<br>
	 *         yyyyMMdd
	 */
	public static String getConvertCurrentDate() {
		return convertorFormat.format(new Date());
	}

	/**
	 * @return 문자열의 날짜를 얻어온다.<br>
	 *         yyyy-MM-dd
	 */
	public static String getCurrentDate(Date d) {
		return simpleDateFormat.format(d);
	}

	/**
	 * @return 현재 날짜를 얻어온다.<br>
	 *         yyyy-MM-dd
	 * @author neon
	 * @date 2013. 9. 13.
	 * @param d
	 *            금일 이전
	 * @return
	 */
	public static String getCurrentDate(int d) {
		Calendar c = GregorianCalendar.getInstance();
		c.set(Calendar.MONTH, c.get(Calendar.MONTH));
		c.set(Calendar.DATE, c.get(Calendar.DATE) - d);
		return simpleDateFormat.format(c.getTime());
	}

	/**
	 * 특허 문서에 입력된 날짜 정보를 Date 객체형으로 변환한다.
	 * 
	 * @author neon
	 * @date 2013. 5. 22.
	 * @param date
	 *            20120304
	 * @return
	 * @throws ParseException
	 */
	public static java.sql.Date getDateConvertor(String date) {
		if (date == null)
			return new java.sql.Date(0);

		Date d = null;
		if (date.matches("^([0-9]{1,4}\\-[0-9]{1,2}\\-[0-9]{1,2})")) {
			try {
				d = simpleDateFormat.parse(date);
				sqlDate.setTime(d.getTime());
			} catch (ParseException e) {
				return new java.sql.Date(0);
			}
		} else {
			try {
				d = convertorFormat.parse(date);
				sqlDate.setTime(d.getTime());
			} catch (ParseException e) {
				return new java.sql.Date(0);
			}
		}
		return sqlDate;
	}

	// public static long getTimeMonth(String date){
	// long time = 0;
	//
	//
	// }

	/**
	 * 특허 문서에 입력된 날짜 정보를 Date 객체형으로 변환한다.
	 * 
	 * @author neon
	 * @date 2013. 5. 22.
	 * @param date
	 *            20120304
	 * @return
	 * @throws ParseException
	 */
	public static String getMinusDateConvertor(String date, int d) {
		Calendar c = GregorianCalendar.getInstance();
		c.setTime(getDateConvertor(date));
		c.set(Calendar.DATE, c.get(Calendar.DATE) + d);
		return getCurrentDate(c.getTime());
	}

	public static int getYear() {
		return Calendar.getInstance().get(GregorianCalendar.YEAR);
	}

	public static int getMonth() {
		return Calendar.getInstance().get(GregorianCalendar.MONTH) + 1;
	}

	public static int getDate() {
		return Calendar.getInstance().get(GregorianCalendar.DATE);
	}

	public static int getDate(int minusDate) {
		return Calendar.getInstance().get(GregorianCalendar.DATE) - minusDate;
	}

	public static void main(String... args) {
		System.out.println(Utility.getOneOfYear(2013));
	}

	public static String getMakeNumberFormat(int number) {
		return numberFormat.format(number);
	}

	public static boolean makeDirectory(String repo) {
		File f = new File(repo);
		if (!f.isDirectory()) {
			logger.debug("다음 폴더를 생성합니다. " + repo);
			return f.mkdirs();
		}
		return false;
	}

	public static boolean deleteDirectory(String path) throws IOException {
		File f = new File(path);
		if (f.isDirectory()) {
			File[] fs = f.listFiles();
			if (fs.length == 0) {
				f.delete();
				logger.info("데이터가 존재하지 않아 [" + path + "] 를 삭제합니다.");
				return true;
			} else {
				for (File s : fs) {
					deleteDirectory(s.getCanonicalPath());
				}
			}
		}
		return false;
	}

	public static int getFileCounting(String repo) {
		File f = new File(repo);
		int maxIndex = 0;
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			String name = "";
			for (File file : files) {
				name = file.getName();
				int index = maxIndex + 1;
				try {
					int fi = name.lastIndexOf("_") + 1;
					int li = name.lastIndexOf(".zip");
					String s = name.substring(fi, li);
					index = Integer.parseInt(s);
				} catch (Exception e) {

				}
				maxIndex = Math.max(index, maxIndex);
			}
			return maxIndex;
		} else {
		}
		return 0;
	}

	public static boolean checkFileName(String fileName) {
		return new File(fileName).isFile();
	}

	public static String convertMillisecond(long time) {
		int s = (int) (time / 1000);
		int day = s / (60 * 60 * 24);
		int daym = s % (60 * 60 * 24);
		int hour = daym / (60 * 60);
		int hm = s % (60 * 60);
		int minute = hm / 60;
		int mm = s % 60;
		int sec = mm;
		return day + "\t" + hour + ":" + minute + ":" + sec;
	}

	/**
	 * 입력된 문자가 영문인가?
	 * 
	 * @author coreawin
	 * @date 2014. 11. 4.
	 * @param s
	 * @return 영문이면 true.
	 */
	public static boolean isEnglish(String s) {
		boolean result = true;
		if (s == null)
			return false;
		char[] cs = s.toCharArray();
		for (char c : cs) {
			int type = Character.getType(c);
			if (type == 1 || type > 9) {
			} else {
				return false;
			}
		}

		return result;
	}

	public static Set<String> getOneOfYear(int year) {
		Set<String> yearMonthSet = new LinkedHashSet<String>();

		Calendar beforeCal = Calendar.getInstance();
		beforeCal.add(Calendar.YEAR, -1);

		int beforeYearMonth = beforeCal.get(Calendar.MONTH) + 2;
		int currentMonth = getMonth();

		if (year == getYear()) {
			if (currentMonth < 12) {
				for (int i = beforeYearMonth; i <= 12; i++) {
					String month = String.valueOf(i);
					if (i < 10) {
						month = "0" + i;
					} else {
						month = String.valueOf(i);
					}
					String s = beforeCal.get(Calendar.YEAR) + "-" + month;
					yearMonthSet.add(s);
				}
				for (int i = 1; i <= currentMonth; i++) {
					String month = String.valueOf(i);
					if (i < 10) {
						month = "0" + i;
					} else {
						month = String.valueOf(i);
					}
					String s = getCurrentYear() + "-" + month;
					yearMonthSet.add(s);
				}
				return yearMonthSet;
			}
		}

		for (int i = 1; i <= 12; i++) {
			String month = String.valueOf(i);
			if (i < 10) {
				month = "0" + i;
			} else {
				month = String.valueOf(i);
			}
			String s = year + "-" + month;
			yearMonthSet.add(s);
		}
		return yearMonthSet;
	}

	public static Set<String> getSelectYear(int year) {
		Set<String> yearMonthSet = new LinkedHashSet<String>();
		for (int i = 1; i <= 12; i++) {
			String month = String.valueOf(i);
			if (i < 10) {
				month = "0" + i;
			} else {
				month = String.valueOf(i);
			}
			String s = year + "-" + month;
			yearMonthSet.add(s);
		}
		return yearMonthSet;
	}

	public static Set<String> getOneOfYear() {
		Set<String> yearMonthSet = new LinkedHashSet<String>();

		Calendar beforeCal = Calendar.getInstance();
		beforeCal.add(Calendar.YEAR, -1);

		int beforeYearMonth = beforeCal.get(Calendar.MONTH) + 2;
		int currentMonth = getMonth();

		if (currentMonth < 12) {
			for (int i = beforeYearMonth; i <= 12; i++) {
				String month = String.valueOf(i);
				if (i < 10) {
					month = "0" + i;
				} else {
					month = String.valueOf(i);
				}
				String s = beforeCal.get(Calendar.YEAR) + ". " + month + ".";
				yearMonthSet.add(s);
			}
		}
		for (int i = 1; i <= currentMonth; i++) {
			String month = String.valueOf(i);
			if (i < 10) {
				month = "0" + i;
			} else {
				month = String.valueOf(i);
			}
			String s = getCurrentYear() + ". " + month + ".";
			yearMonthSet.add(s);
		}
		return yearMonthSet;
	}

	public static Set<String> getHipenOneOfYear(int selectYear) {
		Set<String> yearMonthSet = new LinkedHashSet<String>();

		Calendar beforeCal = Calendar.getInstance();
		switch (selectYear) {
		case -1:
			beforeCal.add(Calendar.YEAR, -1);
			break;
		case -2:
			beforeCal.add(Calendar.MONTH, -6);
			break;
		case -3:
			beforeCal.add(Calendar.MONTH, -3);
			break;
		}

		int beforeYearMonth = beforeCal.get(Calendar.MONTH) + 2;
		logger.debug("SELECT YEAR : {}, BEFORE YEAR MONTH  : {}", selectYear, beforeYearMonth);
		int currentMonth = getMonth();
		int beforeYear = beforeCal.get(Calendar.YEAR);
		if (beforeYear < getCurrentYear()) {
			if (currentMonth < 12) {
				for (int i = beforeYearMonth; i <= 12; i++) {
					String month = String.valueOf(i);
					if (i < 10) {
						month = "0" + i;
					} else {
						month = String.valueOf(i);
					}
					String s = beforeCal.get(Calendar.YEAR) + "-" + month;
					yearMonthSet.add(s);
				}
			}

			for (int i = 1; i <= currentMonth; i++) {
				String month = String.valueOf(i);
				if (i < 10) {
					month = "0" + i;
				} else {
					month = String.valueOf(i);
				}
				String s = getCurrentYear() + "-" + month;
				yearMonthSet.add(s);
			}
		} else {
			for (int i = beforeYearMonth; i <= currentMonth; i++) {
				String month = String.valueOf(i);
				if (i < 10) {
					month = "0" + i;
				} else {
					month = String.valueOf(i);
				}
				String s = getCurrentYear() + "-" + month;
				yearMonthSet.add(s);
			}
		}

		logger.debug("DATE RANGE : {}", yearMonthSet);
		return yearMonthSet;
	}

	public static String[] getIntYearMonth(int currentYear) {

		String[] getIntYearMonth = new String[2];
		Calendar beforeCal = Calendar.getInstance();

		int currentMonth = getMonth();
		int beforeYear = 0;
		int beforeYearMonth = 0;

		if ((currentYear != -1) && (currentYear != -3) && (currentYear != -2)) {
			getIntYearMonth[0] = currentYear + "-01";
			getIntYearMonth[1] = currentYear + "-12";

			logger.debug("START DATE : " + getIntYearMonth[0]);
			logger.debug("END DATE : " + getIntYearMonth[1]);
			return getIntYearMonth;
		} else if (currentYear == -2) {
			beforeCal.add(Calendar.MONTH, -6);
			beforeYearMonth = beforeCal.get(Calendar.MONTH) + 2;
			beforeYear = beforeCal.get(Calendar.YEAR);
			if (currentMonth < 6) {
				if (beforeYearMonth < 10) {
					getIntYearMonth[0] = String.valueOf(beforeYear) + "-0" + String.valueOf(beforeYearMonth);
				} else {
					getIntYearMonth[0] = String.valueOf(beforeYear) + "-" + String.valueOf(beforeYearMonth);
				}

				if (currentMonth < 10) {
					getIntYearMonth[1] = String.valueOf(getYear()) + "-0" + String.valueOf(currentMonth);
				} else {
					getIntYearMonth[1] = String.valueOf(getYear()) + "-" + String.valueOf(currentMonth);
				}
			} else {
				if (beforeYearMonth < 10) {
					getIntYearMonth[0] = String.valueOf(beforeYear) + "-0" + String.valueOf(beforeYearMonth);
				} else {
					getIntYearMonth[0] = String.valueOf(beforeYear) + "-" + String.valueOf(beforeYearMonth);
				}

				if (currentMonth < 10) {
					getIntYearMonth[1] = String.valueOf(getYear()) + "-0" + String.valueOf(currentMonth);
				} else {
					getIntYearMonth[1] = String.valueOf(getYear()) + "-" + String.valueOf(currentMonth);
				}
			}

			logger.debug("START DATE : " + getIntYearMonth[0]);
			logger.debug("END DATE : " + getIntYearMonth[1]);
			return getIntYearMonth;
		} else if (currentYear == -3) {
			beforeCal.add(Calendar.MONTH, -3);
			beforeYear = beforeCal.get(Calendar.YEAR);
			if (currentMonth < 3) {
				if (beforeYearMonth < 10) {
					getIntYearMonth[0] = String.valueOf(beforeYear) + "-0" + String.valueOf(beforeYearMonth);
				} else {
					getIntYearMonth[0] = String.valueOf(beforeYear) + "-" + String.valueOf(beforeYearMonth);
				}

				if (currentMonth < 10) {
					getIntYearMonth[1] = String.valueOf(getYear()) + "-0" + String.valueOf(currentMonth);
				} else {
					getIntYearMonth[1] = String.valueOf(getYear()) + "-" + String.valueOf(currentMonth);
				}
			} else {
				if (beforeYearMonth < 10) {
					getIntYearMonth[0] = String.valueOf(beforeYear) + "-0" + String.valueOf(beforeYearMonth);
				} else {
					getIntYearMonth[0] = String.valueOf(beforeYear) + "-" + String.valueOf(beforeYearMonth);
				}

				if (currentMonth < 10) {
					getIntYearMonth[1] = String.valueOf(getYear()) + "-0" + String.valueOf(currentMonth);
				} else {
					getIntYearMonth[1] = String.valueOf(getYear()) + "-" + String.valueOf(currentMonth);
				}
			}

			logger.debug("START DATE : " + getIntYearMonth[0]);
			logger.debug("END DATE : " + getIntYearMonth[1]);
			return getIntYearMonth;
		}

		beforeCal.add(Calendar.YEAR, -1);
		beforeYearMonth = beforeCal.get(Calendar.MONTH) + 2;
		beforeYear = beforeCal.get(Calendar.YEAR);
		if (beforeYearMonth < 10) {
			getIntYearMonth[0] = String.valueOf(beforeYear) + "-0" + String.valueOf(beforeYearMonth);
		} else {
			getIntYearMonth[0] = String.valueOf(beforeYear) + "-" + String.valueOf(beforeYearMonth);
		}

		if (currentMonth < 10) {
			getIntYearMonth[1] = String.valueOf(getYear()) + "-0" + String.valueOf(currentMonth);
		} else {
			getIntYearMonth[1] = String.valueOf(getYear()) + "-" + String.valueOf(currentMonth);
		}

		logger.debug("START DATE : " + getIntYearMonth[0]);
		logger.debug("END DATE : " + getIntYearMonth[1]);
		return getIntYearMonth;
	}

	public static String[] getIntYearMonth() {

		String[] getIntYearMonth = new String[2];

		Calendar beforeCal = Calendar.getInstance();
		beforeCal.add(Calendar.YEAR, -1);

		int beforeYear = beforeCal.get(Calendar.YEAR);
		int beforeYearMonth = beforeCal.get(Calendar.MONTH) + 2;

		if (beforeYearMonth < 10) {
			getIntYearMonth[0] = String.valueOf(beforeYear) + "0" + String.valueOf(beforeYearMonth);
		} else {
			getIntYearMonth[0] = String.valueOf(beforeYear) + String.valueOf(beforeYearMonth);
		}

		int currentMonth = getMonth();
		if (currentMonth < 10) {
			getIntYearMonth[1] = String.valueOf(getYear()) + "0" + String.valueOf(currentMonth);
		} else {
			getIntYearMonth[1] = String.valueOf(getYear()) + String.valueOf(currentMonth);
		}

		return getIntYearMonth;
	}

	/**
	 * 분류코드의 Group을 포맷팅 한다.< br>
	 * 
	 * @author coreawin
	 * @date 2014. 11. 21.
	 * @param mg
	 * @return
	 */
	public static String formatterGroup(String mg) {
		if ("".equals(mg))
			return mg;

		StringBuffer buf = new StringBuffer();
		try {
			int t = Integer.parseInt(mg);
			if (t == 0)
				return mg;
			return String.valueOf(t);
		} catch (NumberFormatException ne) {
			// ignore
		}

		char[] cs = mg.toCharArray();
		boolean isStart = true;
		for (char c : cs) {
			switch (c) {
			case '0':
				if (isStart) {
					break;
				}
				buf.append(c);
				break;

			default:
				if (isStart) {
					isStart = false;
				}
				buf.append(c);
				break;
			}
		}
		return buf.toString();
	}


	/**
	 * 
	 * 현재 날짜로 부터 기준일까지의 D-DAY 정보를 계산한다.
	 *
	 * @author 이관재
	 * @date 2015. 5. 21.
	 * @version 1.0
	 * @return
	 */
	public static long getDDay(Calendar today, Calendar nextday) {

		int toDayYear = today.get(Calendar.YEAR);
		int toDayMonth = today.get(Calendar.MONTH);
		int toDayDATE = today.get(Calendar.DAY_OF_MONTH);

		Calendar to = new GregorianCalendar(toDayYear, toDayMonth, toDayDATE);

		int nextDayYear = nextday.get(Calendar.YEAR);
		int nextDayMonth = nextday.get(Calendar.MONTH);
		int nextDayDATE = nextday.get(Calendar.DAY_OF_MONTH);

		Calendar from = new GregorianCalendar(nextDayYear, nextDayMonth, nextDayDATE);

		long toTime = to.getTimeInMillis();
		long fromTime = from.getTimeInMillis();

		long dDayTime = fromTime - toTime;
		if (dDayTime != 0) {
			dDayTime = dDayTime / (1000 * 60 * 60 * 24);
		}
		return dDayTime;
	}

	private static final List<String> patent1Aues = new ArrayList<String>();
	private static final List<String> patent2Aues = new ArrayList<String>();
	private static final List<String> patent3Aues = new ArrayList<String>();

	static {
		patent1Aues.add("JP");
		patent2Aues.add("US");
		patent2Aues.add("WO");
		patent2Aues.add("GB");
		patent3Aues.add("KR");
		patent3Aues.add("CN");
		patent3Aues.add("EP");
	}

	/**
	 * 
	 * 검색식에서 출현한 Authority에 대해서 조합 컬렉션 명칭을 가져온다.
	 *
	 * @author 이관재
	 * @date 2015. 8. 17.
	 * @version 1.0
	 * @param searchRule
	 * @return
	 */
	public static String getCollectionName(String searchRule, String collectionName) {

//		QueryConverter qc = new QueryConverter(searchRule);
//		WhereSet[] ws = qc.getWhereSet();
//		Set<Integer> collectionNo = new TreeSet<Integer>();
//		for (WhereSet w : ws) {
//			if (w.getField() == null) {
//				continue;
//			}
//
//			String field = new String(w.getField());
//
//			if (EDQDocField.au.getConvertIndexField().equals(field)) {
//				char[][] data = w.getKeywords();
//				for (int i = 0; i < data.length; i++) {
//					String k = new String(data[i]);
//					String[] kArr = k.split("\\s");
//					for (String au : kArr) {
//						au = au.trim();
//						au = au.replaceAll("\"", "");
//						if (patent1Aues.contains(au)) {
//							collectionNo.add(1);
//						}
//
//						if (patent2Aues.contains(au)) {
//							collectionNo.add(2);
//						}
//
//						if (patent3Aues.contains(au)) {
//							collectionNo.add(3);
//						}
//
//						if (!patent1Aues.contains(au) && !patent2Aues.contains(au) && !patent3Aues.contains(au)) {
//							collectionNo.add(4);
//						}
//					}
//				}
//			}
//		}
//		logger.debug("COLLECTION NO INFO : {}", collectionNo);
//		String collectionNos = "";
//		if (collectionNo.size() == 1) {
//			for (Integer no : collectionNo) {
//				collectionNos += String.valueOf(no);
//			}
//		} else {
//			// if (collectionNo.size() < 4) {
//			// for (Integer no : collectionNo) {
//			// collectionNos += String.valueOf(no);
//			// }
//			// }
//		}
//
//		if (collectionNos.length() > 0) {
//			collectionName = collectionName + "_" + collectionNos;
//		}
//		logger.debug("COLLECTION NAME : {}", collectionName);
		return collectionName;
	}

	/**
	 * 
	 * 검색식에서 출현한 Authority에 대해서 조합 컬렉션 명칭을 가져온다.
	 *
	 * @author 이관재
	 * @date 2015. 8. 17.
	 * @version 1.0
	 * @param searchRule
	 * @return
	 */
	public static String getCollectionNamePno(String pno, String collectionName) {

		String au = pno.substring(0, 2);

//		String collectionNos = "";
//		if (patent1Aues.contains(au.trim())) {
//			collectionNos = "1";
//		}
//
//		if (patent2Aues.contains(au.trim())) {
//			collectionNos = "2";
//		}
//
//		if (patent3Aues.contains(au.trim())) {
//			collectionNos = "3";
//		}
//
//		if (!patent1Aues.contains(au.trim()) && !patent2Aues.contains(au.trim()) && !patent3Aues.contains(au.trim())) {
//			collectionNos = "4";
//		}
//
//		if (collectionNos.length() > 0) {
//			collectionName = collectionName + "_" + collectionNos;
//		}
		return collectionName;
	}

}
