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
public class PatentDataHandler {

	static Map<String, String> map = new HashMap<String, String>();

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

	public static void makePublicationNumber(PatentData data, String qname, String value) {
		if (PatentSchema.schema.get(EXMLSchema.pncn).equalsIgnoreCase(qname)) {
			data.pncn = value;
		} else if (PatentSchema.schema.get(EXMLSchema.pndate).equalsIgnoreCase(qname)) {
			data.pndate = value;
		} else if (PatentSchema.schema.get(EXMLSchema.pndno).equalsIgnoreCase(qname)) {
			data.pndno = value;
		} else if (PatentSchema.schema.get(EXMLSchema.pnkind).equalsIgnoreCase(qname)) {
			data.pnkind = value;
		}
	}

	public static void makeApplicationNumber(PatentData data, String qname, String value) {
		if (PatentSchema.schema.get(EXMLSchema.appcn).equalsIgnoreCase(qname)) {
			data.appcn = value;
		} else if (PatentSchema.schema.get(EXMLSchema.appdno).equalsIgnoreCase(qname)) {
			data.appdno = value;
		} else if (PatentSchema.schema.get(EXMLSchema.appkind).equalsIgnoreCase(qname)) {
			data.appkind = value;
		} else if (PatentSchema.schema.get(EXMLSchema.appdate).equalsIgnoreCase(qname)) {
			data.appdate = value;
		}
	}

	public static void makeApplicants(PatentData data, String qname, String value) {
		if (PatentSchema.schema.get(EXMLSchema.assignee).equalsIgnoreCase(qname)) {
			data.assignee += PatentHandler.MULTI_VALUE_DELIM + value;
		}
	}

	public static void makeIPC(PatentData data, String qname, String value) {
		if (PatentSchema.schema.get(EXMLSchema.ipc).equalsIgnoreCase(qname)) {
			data.ipc = value;
		} else if (PatentSchema.schema.get(EXMLSchema.ipce).equalsIgnoreCase(qname)) {
			data.ipce = value;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcs).equalsIgnoreCase(qname)) {
			data.ipcs = value;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcc).equalsIgnoreCase(qname)) {
			data.ipcc = value;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcsc).equalsIgnoreCase(qname)) {
			data.ipcsc = value;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcmg).equalsIgnoreCase(qname)) {
			data.ipcmg = value;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcsg).equalsIgnoreCase(qname)) {
			data.ipcsg = value;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcqc).equalsIgnoreCase(qname)) {
			data.ipcqc = value;
		}
	}

	public static void makeIPCF(PatentData data, String qname, String value) {
		if (PatentSchema.schema.get(EXMLSchema.ipcf).equalsIgnoreCase(qname)) {
			data.ipcf += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcfe).equalsIgnoreCase(qname)) {
			data.ipcfe += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcfs).equalsIgnoreCase(qname)) {
			data.ipcfs += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcfc).equalsIgnoreCase(qname)) {
			data.ipcfc += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcfsc).equalsIgnoreCase(qname)) {
			data.ipcfsc += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcfmg).equalsIgnoreCase(qname)) {
			data.ipcfmg += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcfsg).equalsIgnoreCase(qname)) {
			data.ipcfsg += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcfqc).equalsIgnoreCase(qname)) {
			data.ipcfqc += PatentHandler.MULTI_VALUE_DELIM + value;
		}
	}

	public static void makeIPCR(PatentData data, String qname, String value) {
		if (PatentSchema.schema.get(EXMLSchema.ipcr).equalsIgnoreCase(qname)) {
			data.ipcr += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcrlevel).equalsIgnoreCase(qname)) {
			data.ipcrlevel += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcrs).equalsIgnoreCase(qname)) {
			data.ipcrs += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcrc).equalsIgnoreCase(qname)) {
			data.ipcrc += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcrsc).equalsIgnoreCase(qname)) {
			data.ipcrsc += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcrmg).equalsIgnoreCase(qname)) {
			data.ipcrmg += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcrsg).equalsIgnoreCase(qname)) {
			data.ipcrsg += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcrcv).equalsIgnoreCase(qname)) {
			data.ipcrcv += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcrcn).equalsIgnoreCase(qname)) {
			data.ipcrcn += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcrstatus).equalsIgnoreCase(qname)) {
			data.ipcrstatus += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.ipcrcds).equalsIgnoreCase(qname)) {
			data.ipcrcds += PatentHandler.MULTI_VALUE_DELIM + value;
		}
	}

	public static void makeNationMain(PatentData data, String qname, String value) {
		if (PatentSchema.schema.get(EXMLSchema.classn).equalsIgnoreCase(qname)) {
			data.classn += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.classc).equalsIgnoreCase(qname)) {
			data.classc += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.classsc).equalsIgnoreCase(qname)) {
			data.classsc += PatentHandler.MULTI_VALUE_DELIM + value;
		}
	}

	public static void makeNationFurther(PatentData data, String qname, String value) {
		if (PatentSchema.schema.get(EXMLSchema.classnf).equalsIgnoreCase(qname)) {
			data.classnf += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.classfc).equalsIgnoreCase(qname)) {
			data.classfc += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.classfsc).equalsIgnoreCase(qname)) {
			data.classfsc += PatentHandler.MULTI_VALUE_DELIM + value;
		}
	}

	public static void makeECLA(PatentData data, String qname, String value) {
		if (PatentSchema.schema.get(EXMLSchema.ecla).equalsIgnoreCase(qname)) {
			data.ecla += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.eclas).equalsIgnoreCase(qname)) {
			data.eclas += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.eclac).equalsIgnoreCase(qname)) {
			data.eclac += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.eclasc).equalsIgnoreCase(qname)) {
			data.eclasc += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.eclamg).equalsIgnoreCase(qname)) {
			data.eclamg += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.eclasg).equalsIgnoreCase(qname)) {
			data.eclasg += PatentHandler.MULTI_VALUE_DELIM + value;
		}
	}

	public static void makePRI(PatentData data, String qname, String value) {
		if (PatentSchema.schema.get(EXMLSchema.pricn).equalsIgnoreCase(qname)) {
			data.pricn += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.pridate).equalsIgnoreCase(qname)) {
			data.pridate += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.pridno).equalsIgnoreCase(qname)) {
			data.pridno += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.prikind).equalsIgnoreCase(qname)) {
			data.prikind += PatentHandler.MULTI_VALUE_DELIM + value;
		}
	}

	public static void makeCitation(PatentData data, String qname, String value) {
		if (PatentSchema.schema.get(EXMLSchema.citcn).equalsIgnoreCase(qname)) {
			data.citcn += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.citdate).equalsIgnoreCase(qname)) {
			data.citdate += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.citdn).equalsIgnoreCase(qname)) {
			data.citdn += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.citkind).equalsIgnoreCase(qname)) {
			data.citkind += PatentHandler.MULTI_VALUE_DELIM + value;
		}
	}

	public static void makeReference(PatentData data, String qname, String value) {
		if (PatentSchema.schema.get(EXMLSchema.refcn).equalsIgnoreCase(qname)) {
			data.refcn += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.refdate).equalsIgnoreCase(qname)) {
			data.refdate += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.refdn).equalsIgnoreCase(qname)) {
			data.refdn += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.refkind).equalsIgnoreCase(qname)) {
			data.refkind += PatentHandler.MULTI_VALUE_DELIM + value;
		}
	}

	public static void makeInventor(PatentData data, String qname, String value) {
		if (PatentSchema.schema.get(EXMLSchema.inventor).equalsIgnoreCase(qname)) {
			data.inventor += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.invfn).equalsIgnoreCase(qname)) {
			data.refdate += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.invln).equalsIgnoreCase(qname)) {
			data.refdate += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.invcity).equalsIgnoreCase(qname)) {
			data.refdate += PatentHandler.MULTI_VALUE_DELIM + value;
		} else if (PatentSchema.schema.get(EXMLSchema.invcn).equalsIgnoreCase(qname)) {
			data.refdate += PatentHandler.MULTI_VALUE_DELIM + value;
		}
	}
}
