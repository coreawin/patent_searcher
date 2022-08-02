package com.diquest.search.mongodb;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.diquest.bean.PatentFullData;
import com.diquest.export.ExportInfo;
import com.diquest.export.ExportTabText;
import com.diquest.util.CompressUtil;
import com.diquest.util.xml.PatentDataMaps;
import com.diquest.util.xml.PatentsXMLParse;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class AutorityMongoDBExport implements Runnable {

	private PatentFullData queue;
	int cnt = 0;
	private String au;
	String collectionName = "PATENT";
	Logger logger = LoggerFactory.getLogger(getClass());
	private String target;
	private int resultSize;
	private String compress;
	private String exportType;
	private boolean isSearchRule;
	ExportInfo excelEx;
	private String exportField;
	private String keywordExtractPath;
	private boolean isThread;
	private String multiValueLine;
	Map<String, PatentDataMaps> writeQuePatentInfo = new LinkedHashMap<String, PatentDataMaps>();
	List<PatentDataMaps> list = new LinkedList<PatentDataMaps>();
	MongoClient client;
	DB mongoDB;
	DBCollection col;
	private ExportTabText normalEx;
	NumberFormat format = NumberFormat.getInstance();

	public AutorityMongoDBExport(PatentFullData queue, String target, String compress, int resultSize, String exportType, boolean isSearchRule, String exportField, String keywordExtractPath, String multiValueLine, boolean isThread) {
		this.queue = queue;
		this.target = target;
		this.compress = compress;
		this.resultSize = resultSize;
		this.exportType = exportType;
		this.isSearchRule = isSearchRule;
		this.exportField = exportField;
		this.keywordExtractPath = keywordExtractPath;
		this.multiValueLine = multiValueLine;
		this.isThread = isThread;
		try {
			client = MongoDBConnector.getInstance("203.250.207.75", 27017);
			mongoDB = client.getDB("KISTI_PATENT");
			col = mongoDB.getCollection("KISTI_COLL_PATENT");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 *
	 * AUthority의 검색을 실행한다.
	 *
	 * @author 이관재
	 * @date 2015. 8. 13.
	 * @version 1.0
	 * @param au 
	 */
	public void run(String au) {
		String[] fields = exportField.toLowerCase().split(",");
		PatentsXMLParse parser = new PatentsXMLParse();
		DBCursor cursor = null;
		try {
			au = "US";
			if (au != null) {
				excelEx = new ExportTabText("E:/PATENT_PUB_MONGODB." + au + ".txt", keywordExtractPath, new LinkedHashSet<String>(Arrays.asList(fields)));
				normalEx = new ExportTabText("E:/PATENT_IPC_MONGODB." + au + ".txt", keywordExtractPath, new LinkedHashSet<String>(Arrays.asList("pno", "ipc")));
				BasicDBObject obj = new BasicDBObject();
				obj.append("authority", au);
				cursor = col.find(obj);
			} else {
				excelEx = new ExportTabText("E:/PATENT_PUB_MONGODB.txt", keywordExtractPath, new LinkedHashSet<String>(Arrays.asList(fields)));
				normalEx = new ExportTabText("E:/PATENT_IPC_MONGODB.txt", keywordExtractPath, new LinkedHashSet<String>(Arrays.asList("pno", "ipc")));
				cursor = col.find();
			}
			int cnt = 0;
			while (cursor.hasNext()) {
				DBObject object = cursor.next();
				if (object.containsField("xml")) {
					byte[] compXml = (byte[]) object.get("xml");
					String xml = CompressUtil.getInstance().unCompress(compXml);
					PatentDataMaps patentData;
					try {
						patentData = parser.parse(xml);
						excelEx.setData(patentData);
						if (normalEx != null) {
							normalEx.setData(patentData);
						}
						cnt++;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (cnt % 10000 == 0) {
					if(au != null){
						logger.info("{}건의 {} 특허 데이터를 Export를 완료하였습니다.", format.format(cnt), au);
					} else {
						logger.info("{}건의 특허 데이터를 Export를 완료하였습니다.", format.format(cnt));
					}
				}
			}
			
			if(au != null){
				logger.info("{}건의 {} 특허 데이터를 Export를 완료하였습니다.", format.format(cnt), au);
			} else {
				logger.info("{}건의 특허 데이터를 Export를 완료하였습니다.", format.format(cnt));
			}
			
			cursor.close();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (excelEx != null) {
				excelEx.flush();
				excelEx.close();
			}
			if (normalEx != null) {
				normalEx.flush();
				normalEx.close();
			}
		}
	}

	public void close() {
		if (client != null) {
			client.close();
		}
	}

	@Override
	public void run() {
		
	}

}
