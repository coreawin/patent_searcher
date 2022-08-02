package com.diquest.bean;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import com.diquest.ir.common.msg.protocol.result.ResultSet;
import com.diquest.util.xml.PatentDataMaps;

/**
 * 
 * 검색된 특허 데이터 정보를 설정합니다.
 * 
 * @author 이관재
 * @date 2015. 8. 13.
 * @version 1.0
 * @filename PatentFullData.java
 */
public class PatentFullData {

	LinkedBlockingQueue<ResultSet> queue = new LinkedBlockingQueue<ResultSet>(2);
	
	LinkedBlockingQueue<List<PatentDataMaps>> queue1 = new LinkedBlockingQueue<List<PatentDataMaps>>(3);

	
	/**
	 * 
	 * 검색결과 항목에서 특허 데이터를 설정한다.
	 *
	 * @author 이관재
	 * @date 2015. 8. 13.
	 * @version 1.0
	 * @param pno
	 * @param pnDate
	 * @param apDate
	 * @param xml
	 */
	public void putPatentResult(List<PatentDataMaps> rs) {
		try {
			queue1.put(rs);
		} catch (InterruptedException e) {
		}
	}

	/**
	 * 
	 * Export 대상 특허 데이터를 가져옵니다.
	 *
	 * @author 이관재
	 * @date 2015. 8. 13.
	 * @version 1.0
	 * @param pno
	 * @return
	 */
	public List<PatentDataMaps> getData1() {
		try {
			return queue1.take();
		} catch (InterruptedException e) {
		}
		return null;
	}
	
	
	/**
	 * 
	 * 검색결과 항목에서 특허 데이터를 설정한다.
	 *
	 * @author 이관재
	 * @date 2015. 8. 13.
	 * @version 1.0
	 * @param pno
	 * @param pnDate
	 * @param apDate
	 * @param xml
	 */
	public void putPatentResult(ResultSet rs) {
		try {
			queue.put(rs);
		} catch (InterruptedException e) {
		}
	}

	/**
	 * 
	 * Export 대상 특허 데이터를 가져옵니다.
	 *
	 * @author 이관재
	 * @date 2015. 8. 13.
	 * @version 1.0
	 * @param pno
	 * @return
	 */
	public ResultSet getData() {
		try {
			return queue.take();
		} catch (InterruptedException e) {
		}
		return null;
	}

	public boolean isEmptyQueue() {
		return queue.isEmpty();
	}
}
