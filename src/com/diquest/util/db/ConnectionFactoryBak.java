package com.diquest.util.db;

import kr.co.topquadrant.common.db.connection.AConnectionPoolFactory;
import kr.co.topquadrant.common.db.connection.exception.TQKCommonDBException;
import kr.co.topquadrant.common.db.connection.prop.AConnectionPoolProperties;
import kr.co.topquadrant.common.db.connection.prop.ConnectionPropertiesCommon;

/**
 * 
 * DB 커넥션 가져온다.
 * 
 * @author coreawin
 * @date 2015. 4. 2.
 * @version 1.0
 * @filename ConnectionFactoryBak.java
 */
public class ConnectionFactoryBak extends AConnectionPoolFactory {

	private static ConnectionFactoryBak instance = null;

	public static synchronized ConnectionFactoryBak getInstance() throws TQKCommonDBException {
		if (instance == null) {
			instance = new ConnectionFactoryBak();
		}
		return instance;
	}

	private ConnectionFactoryBak() throws TQKCommonDBException {
		super();
	}

	protected AConnectionPoolProperties createConnectionProperty() {
		// return new ConnectionPropertiesCommon("kisti", "kisti",
		// "jdbc:oracle:thin:@192.168.0.60:1521:ORCL",
		// getOracleDBDriverName());
		// return new ConnectionPropertiesCommon("scopuskisti",
		// "scopuskistitqk",
		// "jdbc:oracle:thin:@192.168.0.60:1521:ORCL",
		// getOracleDBDriverName());
		return new ConnectionPropertiesCommon("rndnavi", "rndnavi", "jdbc:oracle:thin:@58.72.188.179:10000:ORCL",
				getOracleDBDriverName());
		// return new ConnectionPropertiesCommon("lexispatent",
		// "lexispatent+11",
		// "jdbc:oracle:thin:@58.72.188.25:1521:ORCL",
		// getOracleDBDriverName());
	}
}
