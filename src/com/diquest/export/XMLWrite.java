package com.diquest.export;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.LinkedHashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.diquest.bean.PatentFullData;
import com.diquest.ir.common.msg.protocol.result.Result;
import com.diquest.ir.common.msg.protocol.result.ResultSet;
import com.diquest.util.xml.PatentDataMaps;
import com.diquest.util.xml.PatentsXMLParse;

/**
 * 
 * 검색결과에서 Export 대상 특허 데이터를 XML로 내린다.
 * 
 * @author 이관재
 * @date 2015. 8. 13.
 * @version 1.0
 * @filename XMLWrite.java
 */
public class XMLWrite implements Runnable {

	private PatentFullData queue;
	int cnt = 0;
	private String au;
	private String target;
	Logger logger = LoggerFactory.getLogger(getClass());
	private ResultSet rs;
	ExportInfo excelEx;
	ExportInfo normalEx;
	/**
	 * 캐리지 리턴 문자열을 찾는 정규식
	 */
	public static final String REGX4 = "[\r\n]";
	private String compress;
	private String exportType;
	private String exportField;
	private String keywordExtractPath;
	private String multiLineValue;
	

	public XMLWrite(PatentFullData queue, String au, String target, String exportType, String exportField,
			String multiLineValue, String keywordExtractPath) {
		this.queue = queue;
		this.au = au;
		this.target = target;
		this.exportType = exportType;
		this.exportField = exportField;
		this.keywordExtractPath = keywordExtractPath;
		this.multiLineValue = multiLineValue;
	}

	public XMLWrite(ResultSet rs, String au, String target, String compress) {
		this.rs = rs;
		this.au = au;
		this.target = target;
		this.compress = compress;
	}

	public boolean exportXML() {
		ResultSet rs = null;
		if (queue != null) {
			rs = queue.getData();
		} else {
			if (this.rs == null) {
				return false;
			} else {
				rs = this.rs;
			}
		}

		if (rs == null) {
			return false;
		}

		String target = this.target + File.separator + cnt;
		File f = new File(target);
		if (!f.exists()) {
			f.mkdirs();
		}
		Result[] rArr = rs.getResultList();
		if (rArr != null && rArr.length > 0) {
			for (Result r : rArr) {
				int totalSize = r.getTotalSize();
				int searchSize = r.getRealSize();
				for (int i = 0; i < searchSize; i++) {
					String pno = new String(r.getResult(i, 0));
					String au = new String(r.getResult(i, 1));
					String pndate = new String(r.getResult(i, 2));
					String apdate = new String(r.getResult(i, 3));
					String xml = new String(r.getResult(i, 4));
					xml = xml.replaceAll(REGX4, "");
					String fileName = pno + "." + pndate + "." + apdate + ".xml";
					File writeF = new File(target, fileName);
					BufferedWriter writer = null;
					try {
						writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(writeF), "UTF-8"));
						writer.write(xml);
					} catch (Exception e) {
						logger.error("EXCEPTION PNO : " + pno);
						logger.error(e.getMessage(), e);
					} finally {
						if (writer != null) {
							try {
								writer.flush();
								writer.close();
							} catch (IOException e) {
							}
						}
					}
				}
				if (queue != null) {
					cnt += searchSize;
				}

				// 파일 압축을 실행합니다.
				// CompressUtil compress = new CompressUtil(target,
				// this.compress + "/" + au + "_" + cnt + ".zip");
				// compress.compress();
				// logger.info("[" + cnt + " / " + totalSize + "] " + au + "의
				// 데이터 Export가 완료되었습니다.");
				if (cnt == totalSize) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean exportFile(PatentsXMLParse parser) {
		ResultSet rs = null;
		if (queue != null) {
			rs = queue.getData();
		} else {
			if (this.rs == null) {
				return false;
			} else {
				rs = this.rs;
			}
		}

		if (rs == null) {
			return false;
		}

//		String target = this.target + File.separator + cnt;
		
		File f = new File(target);
		if (!f.exists()) {
			f.mkdirs();
		}
		Result[] rArr = rs.getResultList();
		if (rArr != null && rArr.length > 0) {
			for (Result r : rArr) {
				int totalSize = r.getTotalSize();
				int searchSize = r.getRealSize();
				for (int i = 0; i < searchSize; i++) {
					String pno = new String(r.getResult(i, 0));
					String xml = new String(r.getResult(i, 1));
					xml = xml.replaceAll(REGX4, "");
					try {
						PatentDataMaps patentData = parser.parse(xml);
						String appgp = new String(r.getResult(i, 2));
						if (appgp != null) {
							patentData.additionDataMap.put("app_gp", appgp);
						}
						excelEx.setData(patentData);
						if(normalEx != null){
							normalEx.setData(patentData);
						}
					} catch (Exception e1) {
						logger.error("EXCEPTION PNO : " + pno);
						logger.error(e1.getMessage(), e1);
					}
				}

				if (queue != null) {
					cnt += searchSize;
				}

				// 파일 압축을 실행합니다.
				// CompressUtil compress = new CompressUtil(target,
				// this.compress + "/" + au + "_" + cnt + ".zip");
				// compress.compress();
				logger.info("[" + cnt + " / " + totalSize + "] " + target  + "의 데이터 Export가 완료되었습니다.");
				if (cnt == totalSize) {
					return true;
				}
				//if (cnt >= 1200000) {
					//logger.info("1,200,000건 이상 이므로 export도 종료합니다.");
					//return true;
				//}
			}
		}

		if (excelEx != null) {
			excelEx.flush();
		}

		return false;
	}

	public void setSearchCount(int cnt) {
		this.cnt = cnt;
	}

	@Override
	public void run() {
		PatentsXMLParse parser = new PatentsXMLParse();
		if ("EXCEL".equalsIgnoreCase(exportType)) {
			String[] fields = exportField.toLowerCase().split(",");
			excelEx = new ExportExcel(target, keywordExtractPath, new LinkedHashSet<String>(Arrays.asList(fields)));
		} else if ("TEXT".equals(exportType)) {
			String[] fields = exportField.toLowerCase().split(",");
			try {
				if("Y".equals(multiLineValue)){
					excelEx = new ExportMultiValueLine(target, keywordExtractPath, new LinkedHashSet<String>(Arrays.asList(fields)));
				} else {
					excelEx = new ExportTabText(target, keywordExtractPath, new LinkedHashSet<String>(Arrays.asList(fields)));
//					normalEx = new ExportTabText(target.replaceAll("\\.txt", "_IPC.txt"), keywordExtractPath, new LinkedHashSet<String>(Arrays.asList("pno", "ipc")));
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
		while (true) {
			boolean flag = false;
			if ("EXCEL".equalsIgnoreCase(exportType)) {
				flag = exportFile(parser);
			} else if ("TEXT".equals(exportType)) {
				flag = exportFile(parser);
			} else {
				flag = exportXML();
			}

			if (flag) {
				break;
			}
		}
		if (excelEx != null) {
			excelEx.close();
		}
//		try {
//			CustomQueue.bQueue.take();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}
}
