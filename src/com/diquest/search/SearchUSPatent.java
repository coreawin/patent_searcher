package com.diquest.search;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.diquest.export.ExportExcel;
import com.diquest.ir.common.exception.IRException;
import com.diquest.ir.common.msg.protocol.Protocol;
import com.diquest.ir.common.msg.protocol.query.FilterSet;
import com.diquest.ir.common.msg.protocol.query.Query;
import com.diquest.ir.common.msg.protocol.query.QuerySet;
import com.diquest.ir.common.msg.protocol.query.SelectSet;
import com.diquest.ir.common.msg.protocol.query.WhereSet;
import com.diquest.ir.common.msg.protocol.result.Result;
import com.diquest.ir.common.msg.protocol.result.ResultSet;
import com.diquest.util.AESEncrypt;
import com.diquest.util.EDQDocField;
import com.diquest.util.Utility;
import com.diquest.util.db.ConnectionFactory;
import com.diquest.util.msg.Definition;

public class SearchUSPatent {

	int cnt = 0;
	private String au;
	String collectionName = "PATENT_2_2017_US";
	Logger logger = LoggerFactory.getLogger(getClass());
	private int resultSize;
	ExportExcel excelEx;

	Map<String, Set<String>> pnoSet = new TreeMap<String, Set<String>>();

	private static Set<String> years = new TreeSet<String>();
	static {
		for (int i = 2000; i <= Utility.getCurrentYear(); i++) {
			years.add(String.valueOf(i));
		}
	}

	public SearchUSPatent(String au, int resultSize) {
		this.au = au;
		this.resultSize = resultSize;
	}

	/**
	 *
	 * AUthority의 검색을 실행한다.
	 *
	 * @author 이관재
	 * @date 2015. 8. 13.
	 * @version 1.0
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public void run() throws Exception {
		char[] brokerPageInfo = new char[0];
		SelectSet[] ss = new SelectSet[] { new SelectSet(EDQDocField.pno.getUpperCase()) };
		Connection conn = null;
		PreparedStatement pstmt = null;
		BufferedWriter writer = null;
		ConnectionFactory fac = ConnectionFactory.getInstance();
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("e:/United_State_Patent_pno.txt"),
					"UTF-8"));
			WhereSet[] ws = new WhereSet[0];
			FilterSet[] fs = new FilterSet[0];
			ws = new WhereSet[] { new WhereSet(EDQDocField.au.getConvertIndexField(), Protocol.WhereSet.OP_HASANY, "US") };

			int startNum = 0;
			int endNum = resultSize - 1;
			byte searchOption = Protocol.SearchOption.PHRASEEXACT | Protocol.SearchOption.CACHE;
			String marinerIp = AESEncrypt.getConnectIPInfo(Definition.MARINER_CONNECTION_GPASS_INFO_ENCRYPT);
			int marinerPort = AESEncrypt.getConnectPortInfo(Definition.MARINER_CONNECTION_GPASS_INFO_ENCRYPT);
			SearchService service = new SearchService();
			int totalSize = 0;
			int realSize = 0;
			Pattern p = Pattern.compile("(^[A-Za-z]{1,})");
			do {
				QuerySet qs = new QuerySet(1);
				Query q = new Query();
				q.setSearchOption(searchOption);
				q.setSelect(ss);
				q.setWhere(ws);
				q.setFilter(fs);
				q.setResult(startNum, endNum);
				q.setFrom(collectionName);
				q.setSearch(true);
				q.setDebug(true);
				q.setFaultless(true);
				q.setBrokerPagingInfo(brokerPageInfo);
				// logger.debug("QUERY INFO : {}", q);
				qs.addQuery(q);
				try {
					conn = fac.getConnection();
					conn.setAutoCommit(false);
					pstmt = conn.prepareStatement("INSERT INTO G_PASS_US_PATENT_INFO VALUES (? , ? , ?)");
					ResultSet rs = service.requestSearch(marinerIp, marinerPort, qs);
					if (rs != null) {
						Result[] rArr = rs.getResultList();
						for (Result r : rArr) {
							totalSize = r.getTotalSize();
							realSize = r.getRealSize();
							brokerPageInfo = r.getBrokerPagingInfo();
							for (int i = 0; i < realSize; i++) {
								String pno = new String(r.getResult(i, 0));
								String pnoGroup = "";
								String country = extractData(p, pno);
								String number = pno.substring(country.length());
								String num = number;
								if (number.length() >= 4) {
									number = number.substring(0, 4);
									if ("US".equals(country)) {
										if (years.contains(number)) {
											number = num.substring(0, 6);
										}
									}
								}
								Set<String> pnos = null;
								if (!pnoSet.containsKey(pnoGroup)) {
									pnos = new TreeSet<String>();
								} else {
									pnos = pnoSet.get(pnoGroup);
								}
								pnos.add(pno);
								pnoSet.put(pnoGroup, pnos);

								pstmt.setString(1, country);
								pstmt.setString(2, number);
								pstmt.setString(3, pno);
								pstmt.addBatch();
//								writer.write(pno + "\n");
							}
							pstmt.executeBatch();
							conn.commit();
//							writer.flush();
						}
						cnt += realSize;
						logger.info("[" + cnt + " / " + totalSize + "] " + au
								+ "의 데이터 검색이 완료되었습니다.\n START NUMBER : {}, END NUMBER {}", startNum, endNum);
					}
				} catch (IRException e) {
					logger.error("SEARCH AUTHORITY : {}", au);
					logger.error("SEARCH START INDEX : {}", startNum);
					logger.error("SEARCH END INDEX : {}", endNum);
					logger.error(e.getMessage(), e);
					if (conn != null) {
						conn.rollback();
					}
				} finally {
					fac.release(pstmt, conn);
				}
				startNum += resultSize;
				endNum += resultSize;
			} while (resultSize == realSize);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);

		} finally {
//			if (writer != null) {
//				writer.flush();
//				writer.close();
//			}
		}
	}

	/**
	 * @author 이관재
	 * @date 2013. 7. 9.
	 * @param p
	 *            정규식 패턴
	 * @param line
	 *            파일 1라인
	 * @return
	 */
	private static String extractData(Pattern p, String line) {
		Matcher m = p.matcher(line);
		String key = null;
		while (m.find()) {
			key = m.group();
			break;
		}
		return key;
	}

}
