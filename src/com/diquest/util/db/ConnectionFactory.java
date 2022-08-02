/**
 * 
 */
package com.diquest.util.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author neon
 * @date 2013. 5. 22.
 * @Version 1.0
 */
public class ConnectionFactory {

	Logger logger = LoggerFactory.getLogger(ConnectionFactory.class);

	private String jdbcurl = null;
	private String user = null;
	private String pwd = null;
	private static ConnectionFactory instance = new ConnectionFactory();

	private ConnectionFactory() {
		jdbcurl = "jdbc:tibero:thin:@203.250.206.62:8639:KIST02";
		user = "lexispatent";
		pwd = "lexispatent+0610";
		try {
			Class.forName("com.tmax.tibero.jdbc.TbDriver");
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static ConnectionFactory getInstance() {
		return instance;
	}

	public Connection getConnection() throws SQLException {
		System.out.println("tibero db get connect...");
		Connection con = DriverManager.getConnection(jdbcurl, user, pwd);
		con.setAutoCommit(false);
		System.out.println("tibero connected : " + con);
		return con;
	}

	/**
	 * 테이블 이름을 가져옵니다.<br>
	 * 
	 * @author neon
	 * @date 2013. 5. 22.
	 * @param conn
	 * @param tableNamePattern
	 * @return
	 * @throws SQLException
	 */
	public Set<String> retrieveTableName(Connection conn, String tableNamePattern) throws SQLException {
		Set<String> tableName = new HashSet<String>();
		DatabaseMetaData dmd = conn.getMetaData();
		ResultSet rs = dmd.getTables(null, null, tableNamePattern, new String[] { "TABLE" });
		while (rs.next()) {
			tableName.add(rs.getString("TABLE_NAME"));
		}
		return tableName;
	}

	/**
	 * 칼럼의 이름을 가져옵니다. <br>
	 * 
	 * @author neon
	 * @date 2013. 5. 22.
	 * @param conn
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	public Set<String> retrieveColumnName(Connection conn, String tableName) throws SQLException {
		Set<String> columnName = new HashSet<String>();
		DatabaseMetaData dmd = conn.getMetaData();
		ResultSet rs = dmd.getColumns(null, null, tableName, null);
		while (rs.next()) {
			columnName.add(rs.getString("COLUMN_NAME"));
		}
		return columnName;
	}

	/**
	 *
	 * DB 자원을 반환 한다.
	 *
	 * @author 이관재
	 * @date 2015. 11. 10.
	 * @version 1.0
	 * @param rs
	 * DB 결과 셋
	 * @param psmt
	 * DB 쿼리 Statement
	 * @param conn
	 * DB 커넥션
	 */
	public void release(ResultSet rs, PreparedStatement psmt, Connection conn) {
		if (rs != null)
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		if (psmt != null)
			try {
				psmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		if (conn != null)
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

	}

	/**
	 *
	 * DB 자원을 반환 한다.
	 *
	 * @author 이관재
	 * @date 2015. 11. 10.
	 * @version 1.0
	 * @param rs
	 * DB 결과 셋
	 * @param psmt
	 * DB 쿼리 Statement
	 */
	public void release(ResultSet rs, PreparedStatement psmt) {
		if (rs != null)
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		if (psmt != null)
			try {
				psmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}

	/**
	 *
	 * DB 자원을 반환 한다.
	 *
	 * @author 이관재
	 * @date 2015. 11. 10.
	 * @version 1.0
	 * @param psmt
	 * DB 쿼리 Statement
	 * @param conn
	 * DB 커넥션
	 */
	public void release(PreparedStatement psmt, Connection conn) {
		if (psmt != null)
			try {
				psmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		if (conn != null)
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}

}
