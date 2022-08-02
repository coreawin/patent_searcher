/**
 * 
 */
package com.diquest.util;

/**
 * @author neon
 * @date 2013. 4. 23.
 * @Version 1.0
 */
public class MongoDB_Option {

	public static final String FIELD_ID = "_id";
	public static final String FIELD_XML = "xml";

	/**
	 * mongodb ip
	 */
	public static String _IP = "203.250.207.75";
	/**
	 * mongodb port
	 */
	public static int _PORT = 27017;

	/**
	 * mongoDB에 인서트시 일괄적으로 입력할 크기를 지정합니다.
	 */
	public static int _INSERTDATASIZE = 2000;

	/**
	 * SCOPUS mongoDB의 DB 명.
	 */
	public static String _SCOPUS_DBNAME = "KISTI_PATENT";

	/**
	 * SCOPUS mongoDB의 collection name.
	 */
	public static String _KISTI_SCOPUS_COLLECTIONNAME = "KISTI_COLL_PATENT";


}
