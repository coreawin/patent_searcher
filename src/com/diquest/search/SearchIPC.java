package com.diquest.search;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import com.diquest.ir.common.exception.IRException;
import com.diquest.ir.common.msg.protocol.Protocol;
import com.diquest.ir.common.msg.protocol.query.Query;
import com.diquest.ir.common.msg.protocol.query.QuerySet;
import com.diquest.ir.common.msg.protocol.query.SelectSet;
import com.diquest.ir.common.msg.protocol.result.Result;
import com.diquest.ir.common.msg.protocol.result.ResultSet;
import com.diquest.k.web.util.searchrule.refine.rule.QueryConverterGPass;
import com.diquest.util.EDQDocField;

public class SearchIPC {

	private String resultPath;
	private int resultSize = 10000;
	BufferedReader br;
	BufferedWriter bw;

	public SearchIPC(String resultPath) {
		this.resultPath = resultPath;
		init();
	}

	private void init() {
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultPath), "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void execute() {
		String searchRule = "AU=(US) APLT=(Y) AY=(2003-2010)";
		String collectionName = "PATENT_2_2017_US";
		String marinerIp = "203.250.207.73";
		int marinerPort = 6666;

		char[] brokerPageInfo = new char[0];

		SelectSet[] ss = { new SelectSet(EDQDocField.pno.getUpperCase()),
				new SelectSet(EDQDocField.ipc.getUpperCase()) };
		try {
			int startNum = 0;
			int endNum = startNum + resultSize - 1;
			byte searchOption = Protocol.SearchOption.PHRASEEXACT | Protocol.SearchOption.CACHE;
			// String marinerIp =
			// AESEncrypt.getConnectIPInfo(Definition.MARINER_CONNECTION_GPASS_INFO_ENCRYPT);
			// int marinerPort =
			// AESEncrypt.getConnectPortInfo(Definition.MARINER_CONNECTION_GPASS_INFO_ENCRYPT);

			SearchService service = new SearchService();
			int totalSize = 0;
			int realSize = 0;
			QueryConverterGPass qc = new QueryConverterGPass(searchRule);
			int cnt = 0;
			do {
				QuerySet qs = new QuerySet(1);
				Query q = new Query();
				q.setSearchOption(searchOption);
				q.setSelect(ss);
				q.setWhere(qc.getWhereSet());
				q.setResult(startNum, endNum);
				q.setFrom(collectionName);
				q.setFaultless(true);
				q.setPrintQuery(false);
				q.setBrokerPagingInfo(brokerPageInfo);
				q.setValue("searchType", "export-bulk-result");
				qs.addQuery(q);
				try {
					ResultSet rs = service.requestSearch(marinerIp, marinerPort, qs);
					if (rs != null) {
						Result[] rArr = rs.getResultList();
						for (Result r : rArr) {
							totalSize = r.getTotalSize();
							realSize = r.getRealSize();
							brokerPageInfo = r.getBrokerPagingInfo();
							for (int i = 0; i < realSize; i++) {
								String pno = new String(r.getResult(i, 0));
								String ipc = new String(r.getResult(i, 1));
								bw.write(pno);
								bw.write("\t");
								bw.write(ipc);
								bw.newLine();
								cnt++;
							}
							
						}
						System.out.println("[" + (cnt) + " / " + totalSize + "] 의 데이터 검색이 완료되었습니다.");
						bw.flush();
					}
					startNum += resultSize;
					endNum += resultSize;
				} catch (IRException e) {
					e.printStackTrace();
					break;
				}
			} while (resultSize == realSize);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static void main(String[] args) {
		SearchIPC s = new SearchIPC("d:/data/yeo.us.ipc.result.txt");
		s.execute();
	}
}
