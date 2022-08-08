package com.diquest.search;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import kr.co.tqk.web.db.dao.CnProgramDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.diquest.bean.PatentFullData;
import com.diquest.export.ExportExcel;
import com.diquest.export.ExportInfo;
import com.diquest.export.ExportMultiValueLine;
import com.diquest.export.ExportTabText;
import com.diquest.export.ExportText;
import com.diquest.export.XMLWrite;
import com.diquest.ir.common.exception.IRException;
import com.diquest.ir.common.msg.protocol.Protocol;
import com.diquest.ir.common.msg.protocol.query.FilterSet;
import com.diquest.ir.common.msg.protocol.query.Query;
import com.diquest.ir.common.msg.protocol.query.QuerySet;
import com.diquest.ir.common.msg.protocol.query.SelectSet;
import com.diquest.ir.common.msg.protocol.query.WhereSet;
import com.diquest.ir.common.msg.protocol.result.Result;
import com.diquest.ir.common.msg.protocol.result.ResultSet;
import com.diquest.k.web.util.searchrule.refine.rule.QueryConverterGPass;
import com.diquest.util.AESEncrypt;
import com.diquest.util.EDQDocField;
import com.diquest.util.msg.Definition;
import com.diquest.util.xml.PatentDataMaps;
import com.diquest.util.xml.PatentsXMLParse;

public class SearchAuthorityWOS implements Runnable {

	private PatentFullData queue;
	int cnt = 0;
	private String au;
//	String collectionName = "PATENT_1_2020";
	String collectionName = "PATENT_2020";
//	String collectionName = "PATENT_2_2020_US";
//	String collectionName = "US";
	
	Logger logger = LoggerFactory.getLogger(getClass());
	private String target;
	private int resultSize;
	private int limit = -1; // 결과수 제한하기 2022.03.28 coreawin
	private String compress;
	private String exportType;
	private boolean isSearchRule;
	ExportInfo excelEx;
	private String exportField;
	private String keywordExtractPath;
	private boolean isThread;
	private String multiValueLine;
	Set<String> duplicateCheck = new HashSet<String>();

	public SearchAuthorityWOS(PatentFullData queue, String au, String target, String compress, int resultSize, String exportType, boolean isSearchRule,
			String exportField, String keywordExtractPath, String multiValueLine, boolean isThread) {
		this.queue = queue;
		this.au = au;
		this.target = target;
		this.compress = compress;
		this.resultSize = resultSize;
		this.exportType = exportType;
		this.isSearchRule = isSearchRule;
		this.exportField = exportField;
		this.keywordExtractPath = keywordExtractPath;
		this.multiValueLine = multiValueLine;
		this.isThread = isThread;
		
	}
	
	public SearchAuthorityWOS(PatentFullData queue, String au, String target, String compress, int resultSize, String exportType, boolean isSearchRule,
			String exportField, String keywordExtractPath, String multiValueLine, boolean isThread, int limit) {
		this.queue = queue;
		this.au = au;
		this.target = target;
		this.compress = compress;
		this.resultSize = resultSize;
		this.exportType = exportType;
		this.isSearchRule = isSearchRule;
		this.exportField = exportField;
		this.keywordExtractPath = keywordExtractPath;
		this.multiValueLine = multiValueLine;
		this.isThread = isThread;
		this.limit = limit;
	}

	static {
		// patent1Aues.add("JP");
		// patent2Aues.add("US");
		// patent2Aues.add("WO");
		// patent2Aues.add("GB");
		// patent3Aues.add("KR");
		// patent3Aues.add("CN");
		// patent3Aues.add("EP");
	}

	/**
	 *
	 * AUthority의 검색을 실행한다.
	 *
	 * @author 이관재
	 * @date 2015. 8. 13.
	 * @version 1.0
	 */
	public void run() {
		char[] brokerPageInfo = new char[0];
		
//		logger.info("export field {}", exportField);

		SelectSet[] ss = new SelectSet[0];
		if ("EXCEL".equalsIgnoreCase(exportType) || "TEXT".equalsIgnoreCase(exportType)) {
			ss = new SelectSet[EDQDocField.exportXMLField.length];
			for (int i = 0; i < EDQDocField.exportXMLField.length; i++) {
				ss[i] = new SelectSet(EDQDocField.exportXMLField[i].getUpperCase());
//				logger.debug("SelectSet : {}" , ss[i]);
			}
			/*
			ss = new SelectSet[EDQDocField.exportIPCField.length];
			for (int i = 0; i < EDQDocField.exportIPCField.length; i++) {
				ss[i] = new SelectSet(EDQDocField.exportIPCField[i].getUpperCase());
				System.out.println(ss[i]);
			}*/
		} else {
			ss = new SelectSet[EDQDocField.exportXMLField.length];
			for (int i = 0; i < EDQDocField.exportXMLField.length; i++) {
				ss[i] = new SelectSet(EDQDocField.exportXMLField[i].name());
			}
		}
		// PatentsXMLParse parser = null;
		if (!isThread) {
			// parser = new PatentsXMLParse();
			if ("EXCEL".equalsIgnoreCase(exportType)) {
				String[] fields = exportField.toLowerCase().split(",");
//				for(String f : fields) {
//					System.out.println(f);
//					
//				}
				excelEx = new ExportExcel(target, keywordExtractPath, new LinkedHashSet<String>(Arrays.asList(fields)));
			} else if ("TEXT".equals(exportType)) {
				String[] fields = exportField.toLowerCase().split(",");
				try {
					if ("Y".equalsIgnoreCase(multiValueLine)) {
						excelEx = new ExportMultiValueLine(target, keywordExtractPath, new LinkedHashSet<String>(Arrays.asList(fields)));
					} else {
						excelEx = new ExportTabText(target, keywordExtractPath, new LinkedHashSet<String>(Arrays.asList(fields)));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else if ("PHI".equalsIgnoreCase(exportType)) {
				String[] fields = exportField.toLowerCase().split(",");
				try {
						excelEx = new ExportText(target, keywordExtractPath, new LinkedHashSet<String>(Arrays.asList(fields)));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		WhereSet[] ws = new WhereSet[0];
		FilterSet[] fs = new FilterSet[0];
		if (!isSearchRule) {
			ws = new WhereSet[] { new WhereSet(EDQDocField.au.getConvertIndexField(), Protocol.WhereSet.OP_HASANY, au) };
		} else {
			if ("(Y).all.".equalsIgnoreCase(au)) {
				ws = new WhereSet[] { new WhereSet("ALL", Protocol.WhereSet.OP_HASANY, "Y") };
			} else {
				QueryConverterGPass qc = null;
				try {
					qc = new QueryConverterGPass(au);
					ws = qc.getWhereSet();
				} catch (Exception e) {
					e.printStackTrace();
//					CustomQueue.bQueue.remove();
					logger.error("search rule : {}  ", au);
					System.exit(1);
					
				}
//				System.out.println("searchRule : " + au);
//				System.out.println("isSearchRule : " + ws);
				fs = qc.getFilterSet();
			}
		}

		int startNum = 0;
		int endNum = startNum + resultSize - 1;
		byte searchOption = Protocol.SearchOption.PHRASEEXACT | Protocol.SearchOption.CACHE;
		String marinerIp = AESEncrypt.getConnectIPInfo(Definition.MARINER_CONNECTION_GPASS_INFO_ENCRYPT);
		int marinerPort = AESEncrypt.getConnectPortInfo(Definition.MARINER_CONNECTION_GPASS_INFO_ENCRYPT);
		SearchService service = new SearchService();
		int searchCount = 0;
		
//		marinerIp = "203.250.207.73";
//		marinerPort = 6666;
		int totalSize = 0;
		int realSize = 0;
		PatentsXMLParse parser = new PatentsXMLParse();
		do {
			QuerySet qs = new QuerySet(1);
			Query q = new Query();
			q.setSearchOption(searchOption);
			q.setSelect(ss);
			q.setWhere(ws);
			for(WhereSet w : ws){
				logger.debug("whereSet 2 : {}", w);
			}
			q.setFilter(fs);
//			logger.info("filter {}", fs);
//			logger.info("where count {}", ws.length);
			//q.setOrderby(new OrderBySet[] { new OrderBySet(true, EDQDocField.pndate.getSortField()) });
			q.setResult(startNum, endNum);
			q.setFrom(collectionName);
//			q.setFrom("PATENT_2_2017_US");
//			q.setSearch(true);
			q.setDebug(true);
			q.setFaultless(true);
			q.setPrintQuery(false);
			q.setBrokerPagingInfo(brokerPageInfo);
			q.setValue("searchType", "export-result");
//			 logger.info("QUERY INFO broker page info: {}", brokerPageInfo);
//			logger.info(q.toString());
			qs.addQuery(q);
			try {
//				logger.info("[" + cnt + " / " + totalSize + "] " +target  + "의 데이터 검색을 진행중입니다.");
//				logger.info("{}:{}", marinerIp, marinerPort);
//				logger.info("{} ", collectionName);
				ResultSet rs = service.requestSearch(marinerIp, marinerPort, qs);
//				logger.info("limit : {}", limit);
				if (rs != null) {
					Result[] rArr = rs.getResultList();
					if (!isThread) {
						if ("PHI".equalsIgnoreCase(exportType) || "EXCEL".equalsIgnoreCase(exportType) || "TEXT".equalsIgnoreCase(exportType)) {
							ExportTabText ett = null;
							for (Result r : rArr) {
								totalSize = r.getTotalSize();
								realSize = r.getRealSize();
								cnt += realSize;
//								logger.info("검색결과가 나왔다! {}", totalSize );
								logger.info("[" + (cnt ) + " / " + totalSize + "] " + target  + "의 데이터 검색이 진행중입니다.");
								brokerPageInfo = r.getBrokerPagingInfo();
								for (int i = 0; i < realSize; i++) {
									String pno = new String(r.getResult(i, 0));
									if(duplicateCheck.contains(pno)){
										/*2020.02.13 원인을 모르지만 현재 GPASS에서 중복된 키값이 나타난다.<br>이를 제거하기 위해 추가함.*/
										continue;
									}else{
										duplicateCheck.add(pno);
									}
									
									if(searchCount >= limit && limit != -1){
										logger.info("searchCount : {} / limit : {}", searchCount, limit);
										break;
									}
									
									String xml = new String(r.getResult(i, 1));
									xml = xml.replaceAll(XMLWrite.REGX4, "");
									try {
										PatentDataMaps patentData = parser.parse(xml);
										String appgp = new String(r.getResult(i, 2));
										if (appgp != null) {
											patentData.additionDataMap.put("app_gp", appgp);
										}
										//System.out.println(excelEx);
//										System.out.println(patentData);
										excelEx.setData(patentData);
										searchCount += 1;
									} catch (Exception e1) {
										logger.error("EXCEPTION PNO : " + pno);
										logger.error(e1.getMessage(), e1);
										//System.out.print(excelEx);
										System.exit(1);
									}
								}
							}
							if(limit==-1){
								logger.info("총 {}의 데이터중에서 {}건 데이터가 다운로드 되었습니다.", NumberFormat.getInstance().format(totalSize), NumberFormat.getInstance().format(searchCount==0?0:searchCount));
							}else{
								logger.info("총 {}의 데이터중에서 검색결과 제한에 의해 {}건의 데이터가 다운로드 되었습니다.", NumberFormat.getInstance().format(totalSize), NumberFormat.getInstance().format(limit));
							}
							//아래 프로그램에서만 사용하는 코드... 웹 배포시에는 제거되어야 한다.
//							try {
//								new CnProgramDao().insertHistory(InetAddress.getLocalHost().getHostAddress(), au, totalSize, "PATENT-DOWNLOAD-PROGRAM");
//							} catch (UnknownHostException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
						} else {
							for (Result r : rArr) {
								totalSize = r.getTotalSize();
								realSize = r.getRealSize();
								brokerPageInfo = r.getBrokerPagingInfo();
							}
//							logger.info("[" + cnt + " / " + totalSize + "] " + target  + "의 데이터 검색이 완료되었습니다.");
							cnt += realSize;
							XMLWrite write = new XMLWrite(rs, au, target, compress);
							write.setSearchCount(cnt);
							write.exportXML();
						}
					} else {
						for (Result r : rArr) {
							totalSize = r.getTotalSize();
							realSize = r.getRealSize();
							cnt += realSize;
							brokerPageInfo = r.getBrokerPagingInfo();
						}
						// System.out.println(1233456);
//						logger.info("[" + cnt + " / " + totalSize + "] " + target + "의 데이터 검색이 완료되었습니다.");
						queue.putPatentResult(rs);
					}
				}
			} catch (IRException e) {
				logger.error("SEARCH AUTHORITY : {}", au);
				logger.error("SEARCH START INDEX : {}", startNum);
				logger.error("SEARCH END INDEX : {}", endNum);
				logger.error(e.getMessage(), e);
			}

			if (excelEx != null) {
				excelEx.flush();
//				logger.info("[" + cnt+ " / " + totalSize + "] " + target  + "의 데이터 EXPORT가 완료되었습니다.");
			}
			startNum += resultSize;
			endNum += resultSize;
			// break;
			////if(endNum >= 1200000) {
				//logger.info("1,200,000 건 이상이므로 강제 종료.");
				//isThread = false;
				//break;
			//}
			
			if(searchCount >= limit){
				break;
			}
		} while (resultSize == realSize);
		logger.info("해당 검색을 종료합니다. isThread : {}", isThread);
		if (!isThread) {
			if (excelEx != null) {
				excelEx.close();
			}
		}
//		logger.info("[" + (cnt ) + " / " + (cnt-duplicateCheck.size())+ "] 중복제거한 총 검색결과의 수" );
	}
}
