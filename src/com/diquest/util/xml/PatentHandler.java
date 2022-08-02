package com.diquest.util.xml;

import java.util.LinkedList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.diquest.util.xml.PatentSchema.EXMLSchema;

public class PatentHandler extends DefaultHandler {
	public static final char MULTI_VALUE_DELIM = '↔';
	private PatentData data = new PatentData();
	private static StringBuilder currValue;

	private String xml;

	public void setXML(String xml) {
		this.xml = xml;
	}

	public PatentData getData() {
		data.makePostWorking();
		return data;
	}

	LinkedList<String> stack = new LinkedList<String>();

	public PatentHandler() {
		if (currValue == null) {
			currValue = new StringBuilder();
		} else {
			currValue.setLength(0);
		}
	}

	public void startDocument() {
		data.clear();
		currValue.setLength(0);
	}

	public void endDocument() {
		data.xml = this.xml;
		data.makePostWorking();
	}

	public void startElement(String uri, String localName, String qname, Attributes attr) throws SAXException {
		stack.addLast(qname);

		if (PatentPathInfo.PUBLICATION_REFERENCE.equalsIgnoreCase(qname)) {
			data.dockind = PatentDataHandler.getAttribute(attr).get(PatentPathInfo.DOCUMENT_KIND);
		}
		if (PatentPathInfo.ABSTRACT.equals(qname)) {
			data.abslang += MULTI_VALUE_DELIM + PatentDataHandler.getAttribute(attr).get(PatentPathInfo.LANG);
		}
		if (PatentPathInfo.TITLE.equals(qname)) {
			data.tilang += MULTI_VALUE_DELIM + PatentDataHandler.getAttribute(attr).get(PatentPathInfo.TITLE_LANG);
		}

		if (PatentPathInfo.CLASSIFICATION_ECLA.equals(qname)) {
			data.eclaschema += MULTI_VALUE_DELIM + PatentDataHandler.getAttribute(attr).get(PatentPathInfo.ECLA_SCHEMA);
			data.eclacn += MULTI_VALUE_DELIM + PatentDataHandler.getAttribute(attr).get(PatentPathInfo.ECLA_COUNTRY);
		}

		currValue.setLength(0);
	}

	public void endElement(String uri, String localName, String qname) throws SAXException {
		String sElement = stack.pollLast();
		if (sElement.equalsIgnoreCase(qname)) {

			String value = currValue.toString();

			if (PatentPathInfo.TITLE.equals(qname)) {
				data.ti += MULTI_VALUE_DELIM + value;
				return;
			}

			if (PatentPathInfo.ABSTRACT.equals(qname)) {
				data.abs += MULTI_VALUE_DELIM + value;
				return;
			}

			if (stack.containsAll(PatentPathInfo.publicationNumber)) {
				PatentDataHandler.makePublicationNumber(data, qname, value);
				return;
			}
			if (stack.containsAll(PatentPathInfo.applicationNumber)) {
				PatentDataHandler.makeApplicationNumber(data, qname, value);
				return;
			}

			if (stack.containsAll(PatentPathInfo.path_applicants)) {
				PatentDataHandler.makeApplicants(data, qname, value);
				return;
			}

			if (stack.containsAll(PatentPathInfo.path_inventor)) {
				PatentDataHandler.makeInventor(data, qname, value);
				return;
			}

			if (stack.containsAll(PatentPathInfo.path_ipc)) {
				PatentDataHandler.makeIPC(data, qname, value);
				return;
			}

			if (stack.containsAll(PatentPathInfo.path_ipcf)) {
				PatentDataHandler.makeIPCF(data, qname, value);
				return;
			}

			if (stack.containsAll(PatentPathInfo.path_ipcr)) {
				PatentDataHandler.makeIPCR(data, qname, value);
				return;
			}

			if (stack.contains(PatentPathInfo.CLASSIFICATION_NATIONAL)) {
				if (PatentSchema.schema.get(EXMLSchema.classncn).equalsIgnoreCase(qname)) {
					data.classncn += PatentHandler.MULTI_VALUE_DELIM + value;
					return;
				}
			}

			if (stack.containsAll(PatentPathInfo.path_national_main)) {
				PatentDataHandler.makeNationMain(data, qname, value);
				return;
			}
			if (stack.containsAll(PatentPathInfo.path_national_further)) {
				PatentDataHandler.makeNationFurther(data, qname, value);
				return;
			}

			if (stack.containsAll(PatentPathInfo.path_ecla)) {
				PatentDataHandler.makeECLA(data, qname, value);
				return;
			}

			if (stack.containsAll(PatentPathInfo.path_pri)) {
				PatentDataHandler.makePRI(data, qname, value);
				return;
			}

			if (stack.containsAll(PatentPathInfo.path_citation)) {
				PatentDataHandler.makeCitation(data, qname, value);
				return;
			}
			if (stack.containsAll(PatentPathInfo.path_reference)) {
				PatentDataHandler.makeReference(data, qname, value);
				return;
			}

			if (PatentSchema.schema.get(EXMLSchema.numclaims).equalsIgnoreCase(qname)) {
				data.numclaims += PatentHandler.MULTI_VALUE_DELIM + value;
				return;
			}
		}
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		currValue.append(ch, start, length);
	}

}
