package com.diquest.search;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.diquest.coreawin.common.divisible.DivisionFileWriter;
import com.diquest.util.db.ConnectionFactory;
import com.tmax.tibero.jdbc.TbClob;

public class DBFileWriter {
	DivisionFileWriter br = null;

	public DBFileWriter() {

	}

	public void writeRefIPCInfo() {
		try {
			br = new DivisionFileWriter("E:/PATENT_REF_IPC.txt");
		} catch (IOException e) {
		}
		String sql = "SELECT R.PNO, I.* FROM DQ_4_ITA_REFERENCE R, DQ_4_ITA_IPC I WHERE R.REF_PNO = I.PNO";
		ConnectionFactory fac = ConnectionFactory.getInstance();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = fac.getConnection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			StringBuffer sb = new StringBuffer();
			sb.append("REF_PNO\tIPC");
			br.write(sb.toString() + "\n");
			int i = 0;
			char[] ch = new char[1024];
			StringBuffer tmp = new StringBuffer();
			while (rs.next()) {
				sb.setLength(0);
				String p = rs.getString(1);
				TbClob ip = (TbClob) rs.getClob(2);
				Reader wr = ip.getCharacterStream();
				tmp.setLength(0);
				StringWriter sw = new StringWriter();
				int nbytes = 0; 
				while((nbytes = wr.read(ch)) != -1){
					sw.write(ch, 0, nbytes);
				}
				sb.append(p + "\t" + sw.toString());
				sw.close();
				br.write(sb.toString() + "\n");
				i++;
				if (i % 10000 == 0) {
					System.out.println("10000건의 인용특허 ID 데한 IPC 정보 추출을 완료하였습니다.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			fac.release(rs, pstmt, conn);
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {
		new DBFileWriter().writeRefIPCInfo();
	}
}
