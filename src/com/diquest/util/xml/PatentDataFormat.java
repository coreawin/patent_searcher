package com.diquest.util.xml;

/**
 * Row 데이터에서 고정된 형식(포맷)의 데이터를 만든다.
 * 
 * @author neon
 * @date 2013. 6. 12.
 * @Version 1.0
 */
public class PatentDataFormat {

	/**
	 * DE - Germany <br>
	 * Publication and Citation Numbers <br>
	 * 
	 * Dates Auth. Code Index Code Type Year Number Example<br>
	 * Prior to Oct. 1, 1968 DE N/A N/A N/A Variable: 1-6 digits DE980132<br>
	 * Oct. 1, 1968-1994 DE N/A N/A N/A Fixed: 7 digits DE0980003<br>
	 * 1995-2003 DE Fixed: 1 digit N/A Fixed: 2 digits Fixed: 6 digits
	 * DE232076B5<br>
	 * 2004-Present DE N/A Fixed: 2 digits Fixed: 4 digits Fixed: 6 digits
	 * DE102004036039<br>
	 * <p>
	 * EP - European Patent Office<br>
	 * Dates Auth. Code Number Example<br>
	 * All EP Variable: 1-7 digits EP876123<br>
	 * <p>
	 * FR - France<br>
	 * Dates Auth. Code Number Example<br>
	 * All FR Variable: 1-7 digits FR876123 <br>
	 * <p>
	 * GB - Great Britain<br>
	 * Dates Auth. Code Year Number Example<br>
	 * Prior to 1916 GB Fixed: 4 digits Fixed: 5 digits GB190507239<br>
	 * 1916-Present GB N/A Fixed: 7 digits GB2382549<br>
	 * <p>
	 * JP - Japan<br>
	 * Publication and Citation Numbers<br>
	 * Dates Auth. Code Year Number Example<br>
	 * Prior to 2000 JP Fixed: 2 digits, Emperor Year Fixed: 6 digits JP05000001
	 * <br>
	 * 2000-Present JP Fixed: 4 digits, International Standard Year Fixed: 6
	 * digits JP2<br>
	 * 
	 * Granted Patents (new law)<br>
	 * Dates Auth. Code Number Example<br>
	 * All JP Fixed: 7 digits JP2608465<br>
	 * 
	 * Design Patents <br>
	 * Dates Auth. Code Number Example<br>
	 * All JP Variable: 1-7 digits JP1024254<br>
	 * <P>
	 * US - United States of America<br>
	 * Granted Patents<br>
	 * Dates Auth. Code Number Example<br>
	 * All US Variable: 1-7 digits US54889<br>
	 * Design patents and Statutory Invention Registrations<br>
	 * 
	 * Dates Auth. Code Type Number Example<br>
	 * All US Variable: D, H* Fixed: 7 digits USD0518000<br>
	 * Plant patents and re-issue patents<br>
	 * 
	 * Dates Auth. Code Type Number Example<br>
	 * All US Variable: PP, RE** Fixed: 6 digits USPP017001<br>
	 * 
	 * Note:<br>
	 * Design and statutory invention registration types:<br>
	 * D = Design patent<br>
	 * H = Statutory Invention Registration<br>
	 * Plant and re-issue patent types:<br>
	 * PP = Plant patent<br>
	 * RE = Re-issue patent<br>
	 * <P>
	 * WO - World Intellectual Property Organization (WIPO)<br>
	 * 
	 * Dates Auth. Code Year Number Example<br>
	 * 1978-2002* WO Fixed: 2 digits Fixed: 5 digits WO7800001<br>
	 * 2002-2003* WO Fixed: 2 digits Fixed: 6 digits WO02052541<br>
	 * 2004-Present WO Fixed: 4 digits Fixed: 6 digits WO2005124589<br>
	 * <P>
	 */
	public static String convertPublicationNumber(String country, String docNumber, String kind) {
		if(docNumber==null){
			return country + kind;
		}
		
		if(country==null && docNumber!=null){
			return docNumber + kind;
		}
		return country + docNumber.replaceAll(country, "") + kind;
	}

	public static String convertApplicationNumber(String country, String docNumber) {
		if(docNumber==null){
			return country;
		}
		return country + docNumber.replaceAll(country, "");
	}

	public static String makeMultiPublicationNumber(String target, String country, String docNumber, String kind,
			boolean ignoreKind) {
		try {
			String[] as = country.split(String.valueOf(PatentHandler.MULTI_VALUE_DELIM));
			String[] bs = docNumber.split(String.valueOf(PatentHandler.MULTI_VALUE_DELIM));
			String[] cs = kind.split(String.valueOf(PatentHandler.MULTI_VALUE_DELIM));
			for (int idx = 0; idx < bs.length; idx++) {
				String cn = "";
				try {
					cn = as[idx];
				} catch (ArrayIndexOutOfBoundsException ai) {
					// System.err.println("PatentDataFormat.class makeMultiPublicationNumber : err> "
					// + ai.getMessage());
					cn = "";
				}
				if (cn == null)
					cn = "";
				String a = cn.trim();
				String b = bs[idx].trim();
				String c = "";
				if (!ignoreKind) {
					try {
						c = cs[idx].trim();
					} catch (Exception e) {
					}
				}
				if (ignoreKind) {
					if ("".equals(a) || "".equals(b)) {
						continue;
					}
				} else {
					if ("".equals(a) || "".equals(b) || "".equals(c)) {
						continue;
					}
				}
				target += PatentHandler.MULTI_VALUE_DELIM + convertPublicationNumber(a, b, c);
			}
			return target.trim();
		} catch (Exception e) {
			System.out.println("country " + country);
			System.out.println("docNumber " + docNumber);
			System.out.println("kind " + kind);
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * @param date
	 *            8 digit <br>
	 *            20000905
	 * @return 2000-09-05
	 */
	public static String convertDateHippen(String date) {
		if (date == null)
			return "";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < date.length(); i++) {
			sb.append(date.charAt(i));
			if (i == 3 || i == 5) {
				sb.append("-");
			}
		}
		return sb.toString();
	}

	/**
	 * @param date
	 *            8 digit <br>
	 *            20000905
	 * @return 2000-09-05
	 */
	public static String convertDateHippenDatetime(String date) {
		if (date == null)
			return "";
		if (date.length() < 10)
			return "";
		return date.substring(0, 10);
	}

	/**
	 * @param date
	 *            2000-09-05
	 * @return 20000905
	 */
	public static String convertDate8digit(String date) {
		return date.replaceAll("-", "");
	}

	/**
	 * @param date
	 *            2000-09-05, 20000905
	 * @return 2000
	 */
	public static String extractDateYear(String date) {
		if (date.length() > 4)
			return date.substring(0, 4);
		return date;
	}

}
