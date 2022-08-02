package com.diquest.util.xml;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;

import com.diquest.util.xml.PatentSchema.EXMLSchema;

/**
 * 
 * XML에서 추출한 특허를 Map으로 변환 한다.
 * 
 * @author coreawin
 * @date 2015. 4. 3.
 * @version 1.0
 * @filename PatentDataHandler.java
 */
public class PatentDataMapsHandler {

	static Map<String, String> map = new HashMap<String, String>();

	/**
	 * XML의 속성값을 가져온다.
	 * 
	 * @author 이관재
	 * @date 2015. 4. 3.
	 * @version 1.0
	 * @param attr
	 * @return
	 */
	public static Map<String, String> getAttribute(Attributes attr) {
		map.clear();
		int length = attr.getLength();
		for (int i = 0; i < length; i++) {
			String name = attr.getQName(i);
			String v = attr.getValue(i);
			map.put(name, v);
		}
		return map;
	}

	/**
	 * 
	 * 특허의 발행 번호를 생성한다.
	 *
	 * @author 이관재
	 * @date 2015. 4. 3.
	 * @version 1.0
	 * @param data
	 *            데이터
	 * @param qname
	 *            태그명
	 * @param value
	 *            Value
	 */
	public static void makePublicationNumber(PatentDataMaps data, String qname, String value) {
		EXMLSchema se = null;
		if (PatentSchema.schema.get(EXMLSchema.pncn).equalsIgnoreCase(qname)) {
			se = EXMLSchema.pncn;
		} else if (PatentSchema.schema.get(EXMLSchema.pndate).equalsIgnoreCase(qname)) {
			se = EXMLSchema.pndate;
		} else if (PatentSchema.schema.get(EXMLSchema.pndno).equalsIgnoreCase(qname)) {
			se = EXMLSchema.pndno;
		} else if (PatentSchema.schema.get(EXMLSchema.pnkind).equalsIgnoreCase(qname)) {
			se = EXMLSchema.pnkind;
		}
		data.setDatas(se, value);
	}

	/**
	 * 
	 * 출원 번호를 생성한다.
	 *
	 * @author 이관재
	 * @date 2015. 4. 3.
	 * @version 1.0
	 * @param data
	 *            데이터
	 * @param qname
	 *            태그명
	 * @param value
	 *            Value
	 */
	public static void makeApplicationNumber(PatentDataMaps data, String qname, String value) {
		EXMLSchema se = null;
		if (PatentSchema.schema.get(EXMLSchema.appcn).equalsIgnoreCase(qname)) {
			se = EXMLSchema.appcn;
		} else if (PatentSchema.schema.get(EXMLSchema.appdno).equalsIgnoreCase(qname)) {
			se = EXMLSchema.appdno;
		} else if (PatentSchema.schema.get(EXMLSchema.appkind).equalsIgnoreCase(qname)) {
			se = EXMLSchema.appkind;
		} else if (PatentSchema.schema.get(EXMLSchema.appdate).equalsIgnoreCase(qname)) {
			se = EXMLSchema.appdate;
		}
		data.setDatas(se, value);
	}

	/**
	 * 
	 * 출원인 정보 필드를 전달한다.
	 *
	 * @author 이관재
	 * @date 2015. 4. 3.
	 * @version 1.0
	 * @param qname
	 *            태그명
	 */
	public static EXMLSchema getApplicantsSchemaEnum(String qname) {
		if (PatentSchema.schema.get(EXMLSchema.assignee).equalsIgnoreCase(qname)) {
			return EXMLSchema.assignee;
		} else if (PatentSchema.schema.get(EXMLSchema.assfn).equalsIgnoreCase(qname)) {
			return EXMLSchema.assfn;
		} else if (PatentSchema.schema.get(EXMLSchema.assln).equalsIgnoreCase(qname)) {
			return EXMLSchema.assln;
		} else if (PatentSchema.schema.get(EXMLSchema.asscity).equalsIgnoreCase(qname)) {
			return EXMLSchema.asscity;
		} else if (PatentSchema.schema.get(EXMLSchema.assstate).equalsIgnoreCase(qname)) {
			return EXMLSchema.assstate;
		} else if (PatentSchema.schema.get(EXMLSchema.asscn).equalsIgnoreCase(qname)) {
			return EXMLSchema.asscn; 
		} else if (PatentSchema.schema.get(EXMLSchema.assignee_lang).equalsIgnoreCase(qname)) {
			return EXMLSchema.assignee_lang;
		}  else if (PatentSchema.schema.get(EXMLSchema.app_standardized).equalsIgnoreCase(qname)) {
			return EXMLSchema.app_standardized;
		} else if (PatentSchema.schema.get(EXMLSchema.app_normalized).equalsIgnoreCase(qname)) {
			return EXMLSchema.app_normalized;
		} else if (qname.indexOf(PatentSchema.schema.get(EXMLSchema.assaddress)) == 0) {
			if(qname.equalsIgnoreCase("address")) {
				return null;
			}
			return EXMLSchema.assaddress;
		}
		return null;
	}

	/**
	 *
	 * IPC 분류 코드를 생성한다.
	 * 
	 * @author 이관재
	 * @date 2015. 4. 3.
	 * @version 1.0
	 * @param data
	 *            데이터
	 * @param qname
	 *            태그 명
	 * @param value
	 *            Value
	 */
	public static void makeIPC(PatentDataMaps data, String qname, String value) {
		if (PatentSchema.schema.get(EXMLSchema.ipc).equalsIgnoreCase(qname)) {
			data.setDatas(EXMLSchema.ipc, value);
		} else if (PatentSchema.schema.get(EXMLSchema.ipce).equalsIgnoreCase(qname)) {
			data.setDatas(EXMLSchema.ipce, value);
		} else if (PatentSchema.schema.get(EXMLSchema.ipcs).equalsIgnoreCase(qname)) {
			data.setDatas(EXMLSchema.ipcs, value);
		} else if (PatentSchema.schema.get(EXMLSchema.ipcc).equalsIgnoreCase(qname)) {
			data.setDatas(EXMLSchema.ipcc, value);
		} else if (PatentSchema.schema.get(EXMLSchema.ipcsc).equalsIgnoreCase(qname)) {
			data.setDatas(EXMLSchema.ipcsc, value);
		} else if (PatentSchema.schema.get(EXMLSchema.ipcmg).equalsIgnoreCase(qname)) {
			data.setDatas(EXMLSchema.ipcmg, value);
		} else if (PatentSchema.schema.get(EXMLSchema.ipcsg).equalsIgnoreCase(qname)) {
			data.setDatas(EXMLSchema.ipcsg, value);
		} else if (PatentSchema.schema.get(EXMLSchema.ipcqc).equalsIgnoreCase(qname)) {
			data.setDatas(EXMLSchema.ipcqc, value);
		}
	}

	/**
	 *
	 * CPC 분류 코드 필드를 전달한다.
	 * 
	 * @author 이관재
	 * @date 2015. 4. 3.
	 * @version 1.0
	 * @param qname
	 *            태그 명
	 */
	public static EXMLSchema getCPCSchemaEnum(String qname) {
		if (PatentSchema.schema.get(EXMLSchema.cpc).equalsIgnoreCase(qname)) {
			return EXMLSchema.cpc;
		} else if (PatentSchema.schema.get(EXMLSchema.cpcs).equalsIgnoreCase(qname)) {
			return EXMLSchema.cpcs;
		} else if (PatentSchema.schema.get(EXMLSchema.cpcc).equalsIgnoreCase(qname)) {
			return EXMLSchema.cpcc;
		} else if (PatentSchema.schema.get(EXMLSchema.cpcsc).equalsIgnoreCase(qname)) {
			return EXMLSchema.cpcsc;
		} else if (PatentSchema.schema.get(EXMLSchema.cpcmg).equalsIgnoreCase(qname)) {
			return EXMLSchema.cpcmg;
		} else if (PatentSchema.schema.get(EXMLSchema.cpcsg).equalsIgnoreCase(qname)) {
			return EXMLSchema.cpcsg;
		} else if (PatentSchema.schema.get(EXMLSchema.cpcsp).equalsIgnoreCase(qname)) {
			return EXMLSchema.cpcsp;
		} else if (PatentSchema.schema.get(EXMLSchema.cpccv).equalsIgnoreCase(qname)) {
			return EXMLSchema.cpccv;
		} else if (PatentSchema.schema.get(EXMLSchema.cpcad).equalsIgnoreCase(qname)) {
			return EXMLSchema.cpcad;
		} else if (PatentSchema.schema.get(EXMLSchema.cpccn).equalsIgnoreCase(qname)) {
			return EXMLSchema.cpccn;
		} else if (PatentSchema.schema.get(EXMLSchema.cpcgo).equalsIgnoreCase(qname)) {
			return EXMLSchema.cpcgo;
		} else if (PatentSchema.schema.get(EXMLSchema.cpcstatus).equalsIgnoreCase(qname)) {
			return EXMLSchema.cpcstatus;
		} else if (PatentSchema.schema.get(EXMLSchema.cpcdatasource).equalsIgnoreCase(qname)) {
			return EXMLSchema.cpcdatasource;
		}

		return null;
	}

	/**
	 *
	 * IPC 분류 코드 필드를 전달한다.
	 * 
	 * @author 이관재
	 * @date 2015. 4. 3.
	 * @version 1.0
	 * @param qname
	 *            태그 명
	 */
	public static EXMLSchema getIPCFSchemaEnum(String qname) {

		if (PatentSchema.schema.get(EXMLSchema.cpc).equalsIgnoreCase(qname)) {
			return EXMLSchema.ipcf;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcfe).equalsIgnoreCase(qname)) {
			return EXMLSchema.ipcfe;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcfs).equalsIgnoreCase(qname)) {
			return EXMLSchema.ipcfs;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcfc).equalsIgnoreCase(qname)) {
			return EXMLSchema.ipcfc;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcfsc).equalsIgnoreCase(qname)) {
			return EXMLSchema.ipcfsc;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcfmg).equalsIgnoreCase(qname)) {
			return EXMLSchema.ipcfmg;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcfsg).equalsIgnoreCase(qname)) {
			return EXMLSchema.ipcfsg;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcfqc).equalsIgnoreCase(qname)) {
			return EXMLSchema.ipcfqc;
		}
		return null;
	}

	/**
	 *
	 * IPCR 분류 코드 필드를 전달한다.
	 * 
	 * @author 이관재
	 * @date 2015. 4. 3.
	 * @version 1.0
	 * @param qname
	 *            태그 명
	 */
	public static EXMLSchema getIPCRSchemaEnum(String qname) {
		if (PatentSchema.schema.get(EXMLSchema.ipcr).equalsIgnoreCase(qname)) {
			return EXMLSchema.ipcr;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcrlevel).equalsIgnoreCase(qname)) {
			return EXMLSchema.ipcrlevel;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcrs).equalsIgnoreCase(qname)) {
			return EXMLSchema.ipcrs;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcrividate).equalsIgnoreCase(qname)) {
			return EXMLSchema.ipcrividate;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcrc).equalsIgnoreCase(qname)) {
			return EXMLSchema.ipcrc;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcrsc).equalsIgnoreCase(qname)) {
			return EXMLSchema.ipcrsc;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcrmg).equalsIgnoreCase(qname)) {
			return EXMLSchema.ipcrmg;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcrsg).equalsIgnoreCase(qname)) {
			return EXMLSchema.ipcrsg;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcrcv).equalsIgnoreCase(qname)) {
			return EXMLSchema.ipcrcv;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcradate).equalsIgnoreCase(qname)) {
			return EXMLSchema.ipcradate;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcrsp).equalsIgnoreCase(qname)) {
			return EXMLSchema.ipcrsp;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcrcn).equalsIgnoreCase(qname)) {
			return EXMLSchema.ipcrcn;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcrstatus).equalsIgnoreCase(qname)) {
			return EXMLSchema.ipcrstatus;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcrcds).equalsIgnoreCase(qname)) {
			return EXMLSchema.ipcrcds;
		}
		return null;
	}

	/**
	 *
	 * 미국 특허 분류 코드 필드를 전달한다.
	 * 
	 * @author 이관재
	 * @date 2015. 4. 3.
	 * @version 1.0
	 * @param qname
	 *            태그 명
	 */
	public static EXMLSchema getNationMainSchemaEnum(String qname) {
		if (PatentSchema.schema.get(EXMLSchema.classn).equalsIgnoreCase(qname)) {
			return EXMLSchema.classn;
		} else if (PatentSchema.schema.get(EXMLSchema.classc).equalsIgnoreCase(qname)) {
			return EXMLSchema.classc;
		} else if (PatentSchema.schema.get(EXMLSchema.classsc).equalsIgnoreCase(qname)) {
			return EXMLSchema.classsc;
		}
		return null;
	}

	/**
	 *
	 * Nation Further 코드 필드를 전달한다.
	 * 
	 * @author 이관재
	 * @date 2015. 4. 3.
	 * @version 1.0
	 * @param qname
	 *            태그 명
	 */
	public static EXMLSchema getNationFurtherSchemaEnum(String qname) {
		if (PatentSchema.schema.get(EXMLSchema.classnf).equalsIgnoreCase(qname)) {
			return EXMLSchema.classnf;
		} else if (PatentSchema.schema.get(EXMLSchema.classfc).equalsIgnoreCase(qname)) {
			return EXMLSchema.classfc;
		} else if (PatentSchema.schema.get(EXMLSchema.classfsc).equalsIgnoreCase(qname)) {
			return EXMLSchema.classfsc;
		}
		return null;
	}

	/**
	 *
	 * ECLA 코드 필드를 전달한다.
	 * 
	 * @author 이관재
	 * @date 2015. 4. 3.
	 * @version 1.0
	 * @param qname
	 *            태그 명
	 */
	public static EXMLSchema getECLASchemaEnum(String qname) {
		if (PatentSchema.schema.get(EXMLSchema.ecla).equalsIgnoreCase(qname)) {
			return EXMLSchema.ecla;
		} else if (PatentSchema.schema.get(EXMLSchema.eclas).equalsIgnoreCase(qname)) {
			return EXMLSchema.eclas;
		} else if (PatentSchema.schema.get(EXMLSchema.eclac).equalsIgnoreCase(qname)) {
			return EXMLSchema.eclac;
		} else if (PatentSchema.schema.get(EXMLSchema.eclasc).equalsIgnoreCase(qname)) {
			return EXMLSchema.eclasc;
		} else if (PatentSchema.schema.get(EXMLSchema.eclamg).equalsIgnoreCase(qname)) {
			return EXMLSchema.eclamg;
		} else if (PatentSchema.schema.get(EXMLSchema.eclasg).equalsIgnoreCase(qname)) {
			return EXMLSchema.eclasg;
		} else if (PatentSchema.schema.get(EXMLSchema.eclaasg).equalsIgnoreCase(qname)) {
			return EXMLSchema.eclaasg;
		}
		return null;
	}

	/**
	 *
	 * F-Term 코드 필드를 전달한다.
	 * 
	 * @author 이관재
	 * @date 2015. 4. 3.
	 * @version 1.0
	 * @param qname
	 *            태그 명
	 */
	public static EXMLSchema getFTermSchemaEnum(String qname) {
		if (PatentSchema.schema.get(EXMLSchema.fcode).equalsIgnoreCase(qname)) {
			return EXMLSchema.fcode;
		} else if (PatentSchema.schema.get(EXMLSchema.ftheme).equalsIgnoreCase(qname)) {
			return EXMLSchema.ftheme;
		} else if (PatentSchema.schema.get(EXMLSchema.fviewpoint).equalsIgnoreCase(qname)) {
			return EXMLSchema.fviewpoint;
		} else if (PatentSchema.schema.get(EXMLSchema.ffigure).equalsIgnoreCase(qname)) {
			return EXMLSchema.ffigure;
		} else if (PatentSchema.schema.get(EXMLSchema.faddcode).equalsIgnoreCase(qname)) {
			return EXMLSchema.faddcode;
		}
		return null;
	}

	/**
	 *
	 * 다중 데이터 필드에 대해서 설정한다.
	 * 
	 * @author 이관재
	 * @date 2015. 4. 3.
	 * @version 1.0
	 * @param data
	 *            태그 명
	 * @param se
	 *            필드명
	 * @param value
	 *            태그 Value
	 */
	public static void setMultiData(PatentDataMaps data, EXMLSchema se, String value) {
		if (data != null) {
			data.setMultiDatas(se, value);
		}
	}

	/**
	 *
	 * 우선권 번호 관련 필드를 전달한다.
	 * 
	 * @author 이관재
	 * @date 2015. 4. 3.
	 * @version 1.0
	 * @param qname
	 *            태그 명
	 */
	public static EXMLSchema getPRISchemaEnum(String qname) {
		if (PatentSchema.schema.get(EXMLSchema.pricn).equalsIgnoreCase(qname)) {
			return EXMLSchema.pricn;
		} else if (PatentSchema.schema.get(EXMLSchema.pridate).equalsIgnoreCase(qname)) {
			return EXMLSchema.pridate;
		} else if (PatentSchema.schema.get(EXMLSchema.pridno).equalsIgnoreCase(qname)) {
			return EXMLSchema.pridno;
		} else if (PatentSchema.schema.get(EXMLSchema.prikind).equalsIgnoreCase(qname)) {
			return EXMLSchema.prikind;
		}
		return null;
	}

	/**
	 *
	 * 피 인용 특허 필드를 전달한다.
	 * 
	 * @author 이관재
	 * @date 2015. 4. 3.
	 * @version 1.0
	 * @param qname
	 *            태그 명
	 */
	public static EXMLSchema getCitationSchemaEnum(String qname) {
		if (PatentSchema.schema.get(EXMLSchema.citcn).equalsIgnoreCase(qname)) {
			return EXMLSchema.citcn;
		} else if (PatentSchema.schema.get(EXMLSchema.citdate).equalsIgnoreCase(qname)) {
			return EXMLSchema.citdate;
		} else if (PatentSchema.schema.get(EXMLSchema.citdn).equalsIgnoreCase(qname)) {
			return EXMLSchema.citdn;
		} else if (PatentSchema.schema.get(EXMLSchema.citkind).equalsIgnoreCase(qname)) {
			return EXMLSchema.citkind;
		} else if (PatentSchema.schema.get(EXMLSchema.citname).equalsIgnoreCase(qname)) {
			return EXMLSchema.citname;
		}
		return null;
	}

	/**
	 *
	 * 피 인용 특허의 출원일 필드를 전달한다.
	 * 
	 * @author 이관재
	 * @date 2015. 4. 3.
	 * @version 1.0
	 * @param qname
	 *            태그 명
	 */
	public static EXMLSchema getCitationAppDateSchemaEnum(String qname) {
		if (PatentSchema.schema.get(EXMLSchema.citappdate).equalsIgnoreCase(qname)) {
			return EXMLSchema.citappdate;
		}
		return null;
	}

	/**
	 *
	 * 인용 특허의 출원일 필드를 전달한다.
	 * 
	 * @author 이관재
	 * @date 2015. 4. 3.
	 * @version 1.0
	 * @param qname
	 *            태그 명
	 */
	public static EXMLSchema getReferenceAppDateSchemaEnum(String qname) {
		if (PatentSchema.schema.get(EXMLSchema.refappdate).equalsIgnoreCase(qname)) {
			return EXMLSchema.refappdate;
		}
		return null;
	}

	/**
	 *
	 * 인용 특허의 필드를 전달한다.
	 * 
	 * @author 이관재
	 * @date 2015. 4. 3.
	 * @version 1.0
	 * @param qname
	 *            태그 명
	 */
	public static EXMLSchema getReferenceSchemaEnum(String qname) {
		if (PatentSchema.schema.get(EXMLSchema.refcn).equalsIgnoreCase(qname)) {
			return EXMLSchema.refcn;
		} else if (PatentSchema.schema.get(EXMLSchema.refdate).equalsIgnoreCase(qname)) {
			return EXMLSchema.refdate;
		} else if (PatentSchema.schema.get(EXMLSchema.refdn).equalsIgnoreCase(qname)) {
			return EXMLSchema.refdn;
		} else if (PatentSchema.schema.get(EXMLSchema.refkind).equalsIgnoreCase(qname)) {
			return EXMLSchema.refkind;
		} else if (PatentSchema.schema.get(EXMLSchema.refname).equalsIgnoreCase(qname)) {
			return EXMLSchema.refname;
		}
		return null;
	}

	/**
	 *
	 * 비특허 필드를 전달한다.
	 * 
	 * @author 이관재
	 * @date 2015. 4. 3.
	 * @version 1.0
	 * @param qname
	 *            태그 명
	 */
	public static EXMLSchema getNonPatent(String qname) {
		if ("text".equalsIgnoreCase(qname)) {
			return EXMLSchema.nonpatent;
		} else if ("scopus-url".equalsIgnoreCase(qname)) {
			// Logger l = LoggerFactory.getLogger(PatentDataMapsHandler.class);
			// l.debug("nonpatent scopus url > qname : " + qname);
			return EXMLSchema.scopusurl;
		} else {
			// Logger l = LoggerFactory.getLogger(PatentDataMapsHandler.class);
			// l.debug("nonpatent scopus url > qname : " + qname);

		}
		return null;
	}

	/**
	 *
	 * Main Family 관련 필드를 전달한다.
	 * 
	 * @author 이관재
	 * @date 2015. 4. 3.
	 * @version 1.0
	 * @param qname
	 *            태그 명
	 */
	public static EXMLSchema getMainFamilySchemaEnum(String qname) {
		if (PatentSchema.schema.get(EXMLSchema.mfcn).equalsIgnoreCase(qname)) {
			return EXMLSchema.mfcn;
		} else if (PatentSchema.schema.get(EXMLSchema.mfdate).equalsIgnoreCase(qname)) {
			return EXMLSchema.mfdate;
		} else if (PatentSchema.schema.get(EXMLSchema.mfdno).equalsIgnoreCase(qname)) {
			return EXMLSchema.mfdno;
		} else if (PatentSchema.schema.get(EXMLSchema.mfkind).equalsIgnoreCase(qname)) {
			return EXMLSchema.mfkind;
		}
		return null;
	}

	/**
	 *
	 * Extended Family 관련 필드를 전달한다.
	 * 
	 * @author 이관재
	 * @date 2015. 4. 3.
	 * @version 1.0
	 * @param qname
	 *            태그 명
	 */
	public static EXMLSchema getCompleteFamilySchemaEnum(String qname) {
		if (PatentSchema.schema.get(EXMLSchema.cfcn).equalsIgnoreCase(qname)) {
			return EXMLSchema.cfcn;
		} else if (PatentSchema.schema.get(EXMLSchema.cfdate).equalsIgnoreCase(qname)) {
			return EXMLSchema.cfdate;
		} else if (PatentSchema.schema.get(EXMLSchema.cfdno).equalsIgnoreCase(qname)) {
			return EXMLSchema.cfdno;
		} else if (PatentSchema.schema.get(EXMLSchema.cfkind).equalsIgnoreCase(qname)) {
			return EXMLSchema.cfkind;
		}
		return null;
	}

	/**
	 *
	 * Extended Family 출원일 필드 전달한다.
	 * 
	 * @author 이관재
	 * @date 2015. 4. 3.
	 * @version 1.0
	 * @param qname
	 *            태그 명
	 */
	public static EXMLSchema getCompleteFamilyAppDateSchemaEnum(String qname) {
		if (PatentSchema.schema.get(EXMLSchema.cfappdate).equalsIgnoreCase(qname)) {
			return EXMLSchema.cfappdate;
		}
		return null;
	}

	/**
	 *
	 * Main Family 출원일 필드 전달한다.
	 * 
	 * @author 이관재
	 * @date 2015. 4. 3.
	 * @version 1.0
	 * @param qname
	 *            태그 명
	 */
	public static EXMLSchema getMainFamilyAppDateSchemaEnum(String qname) {
		if (PatentSchema.schema.get(EXMLSchema.mfappdate).equalsIgnoreCase(qname)) {
			return EXMLSchema.mfappdate;
		}
		return null;
	}

	/**
	 *
	 * 특허 변리사 정보 필드 전달한다.
	 * 
	 * @author 이관재
	 * @date 2015. 4. 3.
	 * @version 1.0
	 * @param qname
	 *            태그 명
	 */
	public static EXMLSchema getAgentSchemaEnum(String qname) {
		if (PatentSchema.schema.get(EXMLSchema.agent).equalsIgnoreCase(qname)) {
			return EXMLSchema.agent;
		}
		return null;
	}

	/**
	 *
	 * 심사관 관련 필드를 전달한다.
	 * 
	 * @author 이관재
	 * @date 2015. 4. 3.
	 * @version 1.0
	 * @param qname
	 *            태그 명
	 */
	public static EXMLSchema getPrimaryExaminerSchemaEnum(String qname) {
		if (PatentSchema.schema.get(EXMLSchema.pexam).equalsIgnoreCase(qname)) {
			return EXMLSchema.pexam;
		} else if (PatentSchema.schema.get(EXMLSchema.pexam_firstname).equalsIgnoreCase(qname)) {
			return EXMLSchema.pexam_firstname;
		} else if (PatentSchema.schema.get(EXMLSchema.pexam_lastname).equalsIgnoreCase(qname)) {
			return EXMLSchema.pexam_lastname;
		} else if (PatentSchema.schema.get(EXMLSchema.pexam_department).equalsIgnoreCase(qname)) {
			return EXMLSchema.pexam_department;
		}
		return null;
	}
	
	/**
	 *
	 * 발명인 관련 필드를 전달한다.
	 * 
	 * @author 이관재
	 * @date 2015. 4. 3.
	 * @version 1.0
	 * @param qname
	 *            태그 명
	 */
	public static EXMLSchema getInventorSchemaEnum(String qname) {
		if (PatentSchema.schema.get(EXMLSchema.inventor).equalsIgnoreCase(qname)) {
			return EXMLSchema.inventor;
		} else if (PatentSchema.schema.get(EXMLSchema.inventor_lang).equalsIgnoreCase(qname)) {
			return EXMLSchema.inventor_lang;
		} else if (PatentSchema.schema.get(EXMLSchema.invfn).equalsIgnoreCase(qname)) {
			return EXMLSchema.invfn;
		} else if (PatentSchema.schema.get(EXMLSchema.invln).equalsIgnoreCase(qname)) {
			return EXMLSchema.invln;
		} else if (PatentSchema.schema.get(EXMLSchema.invcity).equalsIgnoreCase(qname)) {
			return EXMLSchema.invcity;
		} else if (PatentSchema.schema.get(EXMLSchema.invstate).equalsIgnoreCase(qname)) {
			return EXMLSchema.invstate;
		} else if (PatentSchema.schema.get(EXMLSchema.invcn).equalsIgnoreCase(qname)) {
			return EXMLSchema.invcn;
		}
		return null;
	}
	
	/**
	 * 
	 * 특허의 prime status 정보를 생성한다.
	 *
	 * @author 한태호
	 * @date 2018. 9. 18.
	 * @version 1.0
	 * @param data
	 *            데이터
	 * @param qname
	 *            태그명
	 * @param value
	 *            Value
	 */
	public static void makePrimeStatus(PatentDataMaps data, String qname, String value) {
		EXMLSchema se = null;
		if (PatentSchema.schema.get(EXMLSchema.date).equalsIgnoreCase(qname)) {
			se = EXMLSchema.status_date;
		} else if (PatentSchema.schema.get(EXMLSchema.detail).equalsIgnoreCase(qname)) {
//			se = EXMLSchema.detail;
		} else if (PatentSchema.schema.get(EXMLSchema.detail_normalized).equalsIgnoreCase(qname)) {
//			se = EXMLSchema.detail_normalized;
		} else if (PatentSchema.schema.get(EXMLSchema.detail_standardized).equalsIgnoreCase(qname)) {
			se = EXMLSchema.status;
		}
		data.setMultiDatas(se, value);
//		data.setDatas(se, value);
	}
	
	/**
	 * 
	 * 특허의 prime fee 정보를 생성한다.
	 *
	 * @author 한태호
	 * @date 2018. 9. 18.
	 * @version 1.0
	 * @param data
	 *            데이터
	 * @param qname
	 *            태그명
	 * @param value
	 *            Value
	 */
	public static void makePrimeFees(PatentDataMaps data, String qname, String value) {
		EXMLSchema se = null;
		if (PatentSchema.schema.get(EXMLSchema.date).equalsIgnoreCase(qname)) {
			se = EXMLSchema.fees_date;
		} else if (PatentSchema.schema.get(EXMLSchema.detail).equalsIgnoreCase(qname)) {
			se = EXMLSchema.detail;
		} else if (PatentSchema.schema.get(EXMLSchema.detail_normalized).equalsIgnoreCase(qname)) {
//			se = EXMLSchema.detail_normalized;
		} else if (PatentSchema.schema.get(EXMLSchema.detail_standardized).equalsIgnoreCase(qname)) {
			se = EXMLSchema.fees_detail;
		}
//		data.setDatas(se, value);
		data.setMultiDatas(se, value);
	}
	
	/**
	 * 
	 * 특허의 prime Statements 정보를 생성한다.
	 *
	 * @author 한태호
	 * @date 2019. 10. 10.
	 * @version 1.0
	 * @param data
	 *            데이터
	 * @param qname
	 *            태그명
	 * @param value
	 *            Value
	 */
	public static void makePrimeStatements(PatentDataMaps data, String qname, String value) {
		EXMLSchema se = null;
		if (PatentSchema.schema.get(EXMLSchema.date).equalsIgnoreCase(qname)) {
			se = EXMLSchema.statements_date;
		} else if (PatentSchema.schema.get(EXMLSchema.detail).equalsIgnoreCase(qname)) {
			se = EXMLSchema.detail;
		} else if (PatentSchema.schema.get(EXMLSchema.detail_normalized).equalsIgnoreCase(qname)) {
//			se = EXMLSchema.detail_normalized;
		} else if (PatentSchema.schema.get(EXMLSchema.detail_standardized).equalsIgnoreCase(qname)) {
			se = EXMLSchema.statements_detail;
		}
//		data.setDatas(se, value);
		data.setMultiDatas(se, value);
	}
	/**
	 *
	 * Prime 관련 필드를 전달한다.
	 * 
	 * @author 한태호
	 * @date 2018. 9. 18.
	 * @version 1.0
	 * @param qname
	 *            태그 명
	 */
	public static EXMLSchema getPrimeSchemaEnum(String qname) {
		if (PatentSchema.schema.get(EXMLSchema.date).equalsIgnoreCase(qname)) {
			return EXMLSchema.date;
		} else if (PatentSchema.schema.get(EXMLSchema.date_from).equalsIgnoreCase(qname)) {
			return EXMLSchema.date_from;
		} else if (PatentSchema.schema.get(EXMLSchema.date_to).equalsIgnoreCase(qname)) {
			return EXMLSchema.date_to;
		} else if (PatentSchema.schema.get(EXMLSchema.detail).equalsIgnoreCase(qname)) {
			return EXMLSchema.detail;
		} else if (PatentSchema.schema.get(EXMLSchema.detail_normalized).equalsIgnoreCase(qname)) {
			return EXMLSchema.detail_normalized;
		} else if (PatentSchema.schema.get(EXMLSchema.detail_standardized).equalsIgnoreCase(qname)) {
			return EXMLSchema.detail_standardized;
		}
		return null;
	}
}
