package com.diquest.search;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.diquest.ir.common.exception.IRException;
import com.diquest.ir.common.msg.protocol.Protocol;
import com.diquest.ir.common.msg.protocol.query.Query;
import com.diquest.ir.common.msg.protocol.query.QuerySet;
import com.diquest.ir.common.msg.protocol.query.SelectSet;
import com.diquest.ir.common.msg.protocol.query.WhereSet;
import com.diquest.ir.common.msg.protocol.result.Result;
import com.diquest.ir.common.msg.protocol.result.ResultSet;
import com.diquest.util.AESEncrypt;
import com.diquest.util.EDQDocField;
import com.diquest.util.msg.Definition;
import com.diquest.util.xml.PatentsXMLParse;

public class SearchLineIpc {

	private String filePath;
	private String resultPath;
	BufferedReader br;
	BufferedWriter bw;

	Set<String> pnos = new LinkedHashSet<String>();
	Map<String, List<String>> pnoIPCMap = new LinkedHashMap<String, List<String>>();

	public SearchLineIpc(String filePath, String resultPath) {
		this.filePath = filePath;
		this.resultPath = resultPath;
		init();
	}

	private void init() {
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				pnos.add(line.trim());
			}
		} catch (Exception e) {
			return;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}
		}

		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultPath), "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void execute() {
		SelectSet[] ss = { new SelectSet(EDQDocField.pno.getUpperCase()), new SelectSet(EDQDocField.ipc.getUpperCase()) };
		try {
			for (String pno : pnos) {
				String refinePno = pno.replaceAll("[-]", "");
				refinePno = pno.replaceAll(" ", "");
				WhereSet[] ws = { new WhereSet(EDQDocField.pno.getConvertIndexField(), Protocol.WhereSet.OP_HASANY, refinePno) };

				byte searchOption = Protocol.SearchOption.PHRASEEXACT | Protocol.SearchOption.CACHE;
				String marinerIp = AESEncrypt.getConnectIPInfo(Definition.MARINER_CONNECTION_GPASS_INFO_ENCRYPT);
				int marinerPort = AESEncrypt.getConnectPortInfo(Definition.MARINER_CONNECTION_GPASS_INFO_ENCRYPT);
				SearchService service = new SearchService();
				int totalSize = 0;
				int realSize = 0;
				int startNum = 0;
				int endNum = 3;
				PatentsXMLParse parser = new PatentsXMLParse();
				QuerySet qs = new QuerySet(1);
				Query q = new Query();
				q.setSearchOption(searchOption);
				q.setSelect(ss);
				q.setWhere(ws);
				q.setResult(startNum, endNum);
				q.setFrom("PATENT");
				q.setSearch(true);
				q.setDebug(true);
				q.setFaultless(true);
				q.setValue("searchType", "export-result");
				qs.addQuery(q);
				try {
					bw.write(pno);
					bw.newLine();
					ResultSet rs = service.requestSearch(marinerIp, marinerPort, qs);
					if (rs != null) {
						Result[] rArr = rs.getResultList();
						for (Result r : rArr) {
							totalSize = r.getTotalSize();
							realSize = r.getRealSize();
							for (int i = 0; i < realSize; i++) {
								String ipc = new String(r.getResult(i, 1));
								if (ipc.trim().length() > 0) {
									String[] ar = ipc.split(",");
									for (String s : ar) {
										if (s.endsWith("/")) {
											s = s.substring(0, s.length() - 1);
										}
										bw.write(s);
										bw.newLine();
									}
								}
							}
						}
					}
					bw.newLine();
				} catch (IRException e) {
					e.printStackTrace();
				}
			}
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
		SearchLineIpc s = new SearchLineIpc("E:/20161117.referencelist.1420.txt", "e:/20161117.1420.referencelist.ipc.result.txt");
		s.execute();
	}
}
