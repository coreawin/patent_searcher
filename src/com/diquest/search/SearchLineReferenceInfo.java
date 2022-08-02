package com.diquest.search;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.LinkedHashMap;
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

public class SearchLineReferenceInfo {

	private String filePath;
	private String resultPath;
	BufferedReader br;
	BufferedWriter bw;

	Set<String> pnos = Collections.emptySet();
	Map<String, String> referenceInfo = new LinkedHashMap<String, String>();

	public SearchLineReferenceInfo(String filePath, String resultPath) {
		this.filePath = filePath;
		this.resultPath = resultPath;
		init();
	}

	private void init() {
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] sArr = line.split("\t");
				referenceInfo.put(sArr[0], sArr[1]);
			}
			pnos = referenceInfo.keySet();
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

				String refPnos = referenceInfo.get(pno.trim());
				String[] refPnoArr = refPnos.split(" ");
				bw.write(pno);
				bw.newLine();
				StringBuffer sb = new StringBuffer();
				int seq = 1;
				for (String refPno : refPnoArr) {
					sb.setLength(0);
					String refinePno = refPno.replaceAll("[-]", "");
					refinePno = refPno.replaceAll(" ", "");

					WhereSet[] ws = { new WhereSet(EDQDocField.pno.getConvertIndexField(), Protocol.WhereSet.OP_HASANY, refinePno) };

					byte searchOption = Protocol.SearchOption.PHRASEEXACT | Protocol.SearchOption.CACHE;
					String marinerIp = AESEncrypt.getConnectIPInfo(Definition.MARINER_CONNECTION_GPASS_INFO_ENCRYPT);
					int marinerPort = AESEncrypt.getConnectPortInfo(Definition.MARINER_CONNECTION_GPASS_INFO_ENCRYPT);
					SearchService service = new SearchService();
					int realSize = 0;
					int startNum = 0;
					int endNum = 3;
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
						ResultSet rs = service.requestSearch(marinerIp, marinerPort, qs);
						if (rs != null) {
							Result[] rArr = rs.getResultList();

							for (Result r : rArr) {
								realSize = r.getRealSize();
								for (int i = 0; i < realSize; i++) {
									String ipc = new String(r.getResult(i, 1));
									if (ipc.trim().length() > 0) {
										String[] ar = ipc.split(",");
										for (String s : ar) {
											if (s.length() >= 4) {
												String subClass = s.substring(0, 4);
												String etc = s.substring(4);
												s = subClass + " " + etc;
											}
											if (s.endsWith("/")) {
												s = s.substring(0, s.length() - 1);
											}
											sb.append(s);
											sb.append(";");
										}
									}
								}
							}
						}
					} catch (IRException e) {
						e.printStackTrace();
					}
					if (sb.toString().endsWith(";")) {
						sb.deleteCharAt(sb.length() - 1);
					}
					System.out.println(pno + "\t" + seq + "\t" + refPno + "\t" + sb.toString());
					bw.write(seq + "\t" + refPno + "\t" + sb.toString());
					bw.newLine();
					seq++;
				}
				bw.newLine();
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
		SearchLineReferenceInfo s = new SearchLineReferenceInfo("E:/20161117.referencelist.requet1-1.txt",
				"e:/20161117.referencelist.requet1-1.ipc.result.txt");
		s.execute();
	}
}
