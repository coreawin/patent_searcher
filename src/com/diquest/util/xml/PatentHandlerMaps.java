package com.diquest.util.xml;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.diquest.unicode.resources.UnicodeUtility;
import com.diquest.util.StringUtil;
import com.diquest.util.Utility;
import com.diquest.util.xml.PatentSchema.EXMLSchema;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 *
 * 특허 XML SAX Handler
 * 
 * @author 정승한
 * @date 2015. 4. 3.
 * @version 1.0
 * @filename PatentHandlerMaps.java
 */
public class PatentHandlerMaps extends DefaultHandler {
	Logger logger = LoggerFactory.getLogger(getClass());
	public static final char MULTI_VALUE_DELIM = '\n';
	private PatentDataMaps data = new PatentDataMaps();
	private StringBuffer currValue = new StringBuffer();

	protected Multiset<EXMLSchema> multiValueSet = HashMultiset.create();
	protected Multiset<EXMLSchema> mm = HashMultiset.create();
	UnicodeUtility unicode = UnicodeUtility.getInstance();
	/**
	 * 멀티 항목에 숫자로 넘버링 표시하겠다는 여부.
	 */
	private boolean mvn = false;

	private String xml;

	public void setXML(String xml) {
		this.xml = xml;
	}

	public PatentDataMaps getData() {
		return data;
	}

	LinkedList<String> stack = new LinkedList<String>();

	public PatentHandlerMaps() {
		this(false);
	}

	public PatentHandlerMaps(boolean multivalueNumbering) {
		this.mvn = multivalueNumbering;
	}

	private void multiValueNumberingInit() {
		multiValueSet.clear();
		mm.clear();
		for (EXMLSchema se : EXMLSchema.values()) {
			mm.add(se);
		}
	}

	public void startDocument() {
		data.clear();
		data.dataMultiMap.clear();
		dup.clear();
		multiValueNumberingInit();
		currValue.setLength(0);
	}

	public void endDocument() {
		data.setDatas(EXMLSchema.xml, this.xml);
		data.makePostWorking();
		data.makePostWorkingMultiMapValue();

		data.setDatas(EXMLSchema.numindclaims, String.valueOf(0));
		String in = data.getDatas(EXMLSchema.independent_claims);
		if (in != null) {
			in = in.trim();
			if (!"".equals(in)) {
				String[] numinv = in.split(String.valueOf(PatentHandler.MULTI_VALUE_DELIM));
				if (numinv != null) {
					data.setDatas(EXMLSchema.numindclaims, String.valueOf(numinv.length));
				}
			}
		}
	}

	private String abslang = "";
	private String pritkind = "";
	private String pridataformat = "";
	private String srep = "";
	private boolean isSrep = false;
	private String invseq = null;
	private String assseq = null;

	private String asslang = null;
	private String invlang = null;
	
	private String section = "";
	private String event = "";

	private static Set<String> exceptTag = new HashSet<String>();
	private static Set<String> skipTag = new HashSet<String>();
	
	private String o_eventDate = "";
	private String o_eventDateFrom = "";
	private String o_eventDateTo = "";
	
	boolean isFirst = false;
	
	public Map<EXMLSchema, String> ownerMap = new LinkedHashMap<EXMLSchema, String>();
	
	private boolean related_doc_start = false;
	private String related_doc_type = null;
	private String related_doc_gubun = "document-id";
	
	static {
		exceptTag.add("sup");
		exceptTag.add("sub");
		exceptTag.add("pre");
		exceptTag.add("strong");
		exceptTag.add("em");
		exceptTag.add("kbd");
		exceptTag.add("code");
		exceptTag.add("tt");
		exceptTag.add("dfn");
		exceptTag.add("cite");
		exceptTag.add("samp");
		exceptTag.add("var");
		exceptTag.add("basepoint");
		exceptTag.add("blinkd");
		exceptTag.add("u");
		exceptTag.add("b");
		exceptTag.add("p");
		exceptTag.add("br");
		exceptTag.add("img");
		exceptTag.add("i");
		exceptTag.add("heading");

		/* description */
		skipTag.add("tables");
		skipTag.add("table");
		skipTag.add("tgroup");
		skipTag.add("tbody");
		skipTag.add("row");
		skipTag.add("entry");

		/* claim */
		exceptTag.add("claim-text");
		// skipTag.add("claim-text");
		skipTag.add("claim-ref");
		skipTag.add("claim");
		skipTag.add("chemistry");
		skipTag.add("");
	}

	private boolean setExceptionStartTag(String qname) {
		if (skipTag.contains(qname))
			return true;
		if (exceptTag.contains(qname)) {
			currValue.append("<" + qname + ">");
			return true;
		} else {
			currValue.setLength(0);
			return false;
		}
	}

	private boolean setExceptionEndTag(String qname) {
		if (skipTag.contains(qname))
			return true;
		if (exceptTag.contains(qname)) {
			currValue.append("</" + qname + ">");
			return true;
		}
		return false;
	}

	private String currentPartiesLang = null;
	private Set<String> dup = new HashSet<String>();

	private void initVar() {
	}

	private Map<Integer, Map<String, Map<EXMLSchema, String>>> createMultiValueMap() {
		return new LinkedHashMap<Integer, Map<String, Map<EXMLSchema, String>>>();
	}

	private void putmvm(EXMLSchema multiMapType, String sequence, String lang, EXMLSchema schema, String value) {
		if (sequence == null)
			return;
		Map<Integer, Map<String, Map<EXMLSchema, String>>> coll = data.dataMultiMap.get(multiMapType);
		if (coll == null) {
			coll = createMultiValueMap();
		}

		if (Utility.isEnglish(value) && !"eng".equalsIgnoreCase(lang)) {
			switch (schema) {
			case invcn:
			case asscn:
				break;
			default:
				// logger.debug("lang {} / value {} ", lang, value);
				lang = "eng";
				break;
			}
		}

		if (lang == null) {
			lang = "";
		}

		int seq = Integer.parseInt(sequence);
		Map<String, Map<EXMLSchema, String>> seqMap = coll.get(seq);
		if (seqMap == null) {
			seqMap = new HashMap<String, Map<EXMLSchema, String>>();
		}

		Map<EXMLSchema, String> langMap = null;
		if (seqMap.containsKey(lang)) {
			langMap = seqMap.get(lang);
		} else {
			langMap = new HashMap<PatentSchema.EXMLSchema, String>();
		}
		langMap.put(schema, value);
		seqMap.put(lang, langMap);
		coll.put(seq, seqMap);
		data.dataMultiMap.put(multiMapType, coll);
	}

	public void startElement(String uri, String localName, String qname, Attributes attr) throws SAXException {
		stack.addLast(qname);
		initVar();
		boolean startTag = setExceptionStartTag(qname);
		// if(startTag) return;

		if (stack.containsAll(PatentPathInfo.path_citation_srep)) {
			if (stack.size() == PatentPathInfo.path_citation_srep.size()) {
				srep = PatentDataMapsHandler.getAttribute(attr).get(PatentPathInfo.SREP_PHASE);
			}
		} else {
			srep = null;
		}

		/**
		 * Description
		 * 
		 * */
		if (PatentPathInfo.DESCRIPTION.equals(qname)) {
			String lang = PatentDataMapsHandler.getAttribute(attr).get("lang");
			String id = PatentDataMapsHandler.getAttribute(attr).get("id");
			String format = PatentDataMapsHandler.getAttribute(attr).get("format");
			data.setMultiDatas(EXMLSchema.description_lang, multiValueNumbering(EXMLSchema.description_lang, lang));
			data.setMultiDatas(EXMLSchema.description_id, multiValueNumbering(EXMLSchema.description_id, id));
			data.setMultiDatas(EXMLSchema.description_format, multiValueNumbering(EXMLSchema.description_format, format));
			return;
		}
		/**
		 * 청구항 목록 시작
		 * */
		if (PatentPathInfo.CLAIMS.equals(qname)) {
			String lang = PatentDataMapsHandler.getAttribute(attr).get("lang");
			String id = PatentDataMapsHandler.getAttribute(attr).get("id");
			String format = PatentDataMapsHandler.getAttribute(attr).get("format");
			data.setMultiDatas(EXMLSchema.claims_lang, multiValueNumbering(EXMLSchema.claims_lang, lang));
			data.setMultiDatas(EXMLSchema.claims_id, multiValueNumbering(EXMLSchema.claims_id, id));
			data.setMultiDatas(EXMLSchema.claims_format, multiValueNumbering(EXMLSchema.claims_format, format));
			// logger.debug("claims id:{}, lang:{}, format:{}", id, lang);
			// logger.info("claims qname-[{}] : value {}", qname, currValue);
			return;
		}

		// System.out.println(stack);
		/**
		 * 청구항 Contents
		 * */
		if (PatentPathInfo.CLAIM.equals(qname)) {
			if (qname.equals(PatentPathInfo.LEXIS_PATENT_DOCUMENT)) {
				return;
			}
			String num = PatentDataMapsHandler.getAttribute(attr).get("num");
			String id = PatentDataMapsHandler.getAttribute(attr).get("id");
			currValue.append("{");
			if (id == null) {
				currValue.append("clm-00000-gg");
			} else {
				currValue.append(id);
			}
			currValue.append(",");
			if (num == null) {
				currValue.append("0");
			} else {
				currValue.append(num);
			}
			currValue.append("}");
			// logger.info("qname-[{}]", qname);
			// logger.info("qname-[{}] : value {}", qname, currValue);
			return;
		}

		/**
		 * 특허 제목
		 * */
		if (PatentPathInfo.TITLE.equals(qname)) {
			data.setMultiDatas(
					EXMLSchema.tilang,
					multiValueNumbering(EXMLSchema.tilang,
							PatentDataMapsHandler.getAttribute(attr).get(PatentPathInfo.TITLE_LANG)));
			return;
		}

		/**
		 * 특허 발행 Description
		 * */
		if (PatentPathInfo.PUBLICATION_REFERENCE.equalsIgnoreCase(qname)) {
			data.setDatas(EXMLSchema.dockind, PatentDataMapsHandler.getAttribute(attr).get(PatentPathInfo.DOCUMENT_KIND));
			data.setDatas(EXMLSchema.publ_desc, PatentDataMapsHandler.getAttribute(attr).get(PatentPathInfo.DOCUMENT_DESC));
			return;
		}
		
		/**
		 * 특허 초록
		 * */
		if (PatentPathInfo.ABSTRACT.equals(qname)) {
			abslang = PatentDataMapsHandler.getAttribute(attr).get(PatentPathInfo.LANG);
			data.setMultiDatas(EXMLSchema.abslang, multiValueNumbering(EXMLSchema.abslang, abslang));
			return;
		}

		/**
		 * 특허 우선권
		 * */
		if (PatentPathInfo.PRIORITY_CLAIMS.equals(qname)) {
			pritkind = PatentDataMapsHandler.getAttribute(attr).get(PatentPathInfo.PRIORITY_CLAIMS_TKIND);
			pridataformat = PatentDataMapsHandler.getAttribute(attr).get(PatentPathInfo.PRIORITY_DATA_FORMAT);
			// if (pritkind != null) {
			data.setMultiDatas(EXMLSchema.pritkind, multiValueNumbering(EXMLSchema.pritkind, StringUtil.nullCheck(pritkind)));
			// }
			// if (pridataformat != null) {
			data.setMultiDatas(EXMLSchema.pridataformat,
					multiValueNumbering(EXMLSchema.pridataformat, StringUtil.nullCheck(pridataformat)));
			// }
			return;
		}

		/**
		 * ECLA 분류
		 * */
		if (PatentPathInfo.CLASSIFICATION_ECLA.equals(qname)) {
			data.setMultiDatas(
					EXMLSchema.eclaschema,
					multiValueNumbering(EXMLSchema.eclaschema,
							PatentDataMapsHandler.getAttribute(attr).get(PatentPathInfo.ECLA_SCHEMA)));
			data.setMultiDatas(
					EXMLSchema.eclacn,
					multiValueNumbering(EXMLSchema.eclacn,
							PatentDataMapsHandler.getAttribute(attr).get(PatentPathInfo.ECLA_COUNTRY)));
			return;
		}
		
		/**
		 * 특허 대리인
		 * */
		if (PatentPathInfo.AGENT.equals(qname)) {
			data.setMultiDatas(
					EXMLSchema.agrepType,
					multiValueNumbering(EXMLSchema.agrepType,
							PatentDataMapsHandler.getAttribute(attr).get(PatentPathInfo.AGENT_REPTYPE)));
			return;
		}

		/**
		 * 특허 발명인 언어
		 * */
		if (stack.containsAll(PatentPathInfo.path_inventor_lang)) {
			if (stack.size() == 6) {
				invlang = PatentDataMapsHandler.getAttribute(attr).get(PatentPathInfo.LANG);
				data.setMultiDatas(EXMLSchema.inventor_lang, multiValueNumbering(EXMLSchema.inventor_lang, invlang));
				/* 발명자의 국적정보를 얻기 위해 시퀀스를 가져온다. */
				data.setMultiDatas(EXMLSchema.invseq, multiValueNumbering(EXMLSchema.invseq, invseq));
			}
			return;
		}
		
		/**
		 * 특허 발명인 정보
		 * */
		if (stack.containsAll(PatentPathInfo.path_inv)) {
			if (stack.size() == 5) {
				String seq = PatentDataMapsHandler.getAttribute(attr).get(PatentPathInfo.SEQUENCE);
				if (seq != null) {
					invseq = seq;
				}
			}
			return;
		}

		/**
		 * 특허 출원인 언어
		 * */
		if (stack.containsAll(PatentPathInfo.path_applicants_lang)) {
			if (stack.size() == 6) {
				asslang = PatentDataMapsHandler.getAttribute(attr).get(PatentPathInfo.LANG);
				// logger.debug("applicants lang : {}", lang);
				data.setMultiDatas(EXMLSchema.assignee_lang, multiValueNumbering(EXMLSchema.assignee_lang, asslang));
				/* 출원자의 국적정보를 얻기 위해 시퀀스를 가져온다. */
				data.setMultiDatas(EXMLSchema.assseq, multiValueNumbering(EXMLSchema.assseq, assseq));
			}
			if (stack.containsAll(PatentPathInfo.path_applicants)) {
				EXMLSchema se = PatentDataMapsHandler.getApplicantsSchemaEnum(qname);
				if(se == null)
					return;
				if (se.equals(EXMLSchema.app_standardized)) {
					String name = StringUtil.nullCheck(PatentDataMapsHandler.getAttribute(attr).get("type"));
					putmvm(EXMLSchema.assignee, assseq, asslang, EXMLSchema.app_standardized_type, name);
					data.setMultiDatas(EXMLSchema.app_standardized_type, name);
				} else if(se.equals(EXMLSchema.app_normalized)) {
					String key = StringUtil.nullCheck(PatentDataMapsHandler.getAttribute(attr).get("key"));
					putmvm(EXMLSchema.assignee, assseq, asslang, EXMLSchema.app_normalized_key, key);
					data.setMultiDatas(EXMLSchema.app_normalized_key, key);
				}
				return;
			}
			return;
		}
		/**
		 * 특허 출원인 정보
		 * */
		if (stack.containsAll(PatentPathInfo.path_app)) {
			if (stack.size() == 5) {
				String seq = PatentDataMapsHandler.getAttribute(attr).get(PatentPathInfo.SEQUENCE);
				if (seq != null) {
					assseq = seq;
				}
			}
			return;
		}
		
		/**
		 * Prime
		 * 
		 * */
		if (PatentPathInfo.PRIME_NOTIFIED_SECTION.equals(qname)) {
			String name = PatentDataMapsHandler.getAttribute(attr).get("name");
			section = name;
			return;
		}
		
		if (PatentPathInfo.PRIME_NOTIFIED_EVENT.equals(qname)) {
			String name = PatentDataMapsHandler.getAttribute(attr).get("name");
			event = name;
			
			if("Fees".equals(section)) {
				data.setMultiDatas(EXMLSchema.fees, name);
			} else if("Statements".equals(section)) {
				data.setMultiDatas(EXMLSchema.statements, name);
			}
			
			return;
		}
		
		if (stack.containsAll(PatentPathInfo.path_related_documents)) {
			if("document-id".equals(qname)) {
				related_doc_start = true;
			}
			
			if("parent-doc".equals(qname)) {
				related_doc_type = "PARENT";
			} else if("parent-grant-document".equals(qname)) {
				related_doc_type = "PARENT-GRANT";
			} else if("parent-pct-document".equals(qname)) {
				related_doc_type = "PARENT-PCT";
			} else if("child-doc".equals(qname)) {
				related_doc_type = "CHILD";
			}
			
			if("relation".equals(qname)) {
				related_doc_gubun = stack.get(stack.size()-2);
			}
		}
	}

	final String numberFormat = "<%s>";

	private String multiValueNumbering(EXMLSchema main, EXMLSchema se, String value) {
		if (mvn) {
			if (se == null)
				return value;
			multiValueSet.add(se);
			int c = multiValueSet.count(se);
			int mainc = mm.count(main);
			if (mainc == 0) {
				mm.add(main);
			}
			if (c > mainc && c > 1) {
				mm.add(main);
			}
			return String.format(numberFormat, mm.count(main)) + value;
		}
		return value;
	}

	private String multiValueNumbering(EXMLSchema se, String value) {
		return multiValueNumbering(se, se, value);
	}

	public void endElement(String uri, String localName, String qname) throws SAXException {
		String sElement = stack.pollLast().trim();
		if (sElement.equalsIgnoreCase(qname.trim())) {
			EXMLSchema se = null;
			boolean endTag = setExceptionEndTag(qname);
			// logger.debug(stack +":" + qname +":" + endTag);
			if (endTag)
				return;

			String value = currValue.toString().replaceAll("\n", " ");
			value = unicode.convertUnicode(value);
			/**
			 * PCT 특허 출원 정보
			 * */
			if (stack.containsAll(PatentPathInfo.path_pct_application_filing)) {
				if ("country".equals(qname)) {
					data.setDatas(EXMLSchema.pct_filing_cn, value);
				} else if ("doc-number".equals(qname)) {
					data.setDatas(EXMLSchema.pct_filing_dn, value);
				} else if ("date".equals(qname)) {
					data.setDatas(EXMLSchema.pct_filing_date, value);
				}
				return;
			}
			/**
			 * PCT 특허 발행 정보
			 * */
			if (stack.containsAll(PatentPathInfo.path_pct_application_publishing)) {
				if ("country".equals(qname)) {
					data.setDatas(EXMLSchema.pct_publishing_cn, value);
				} else if ("doc-number".equals(qname)) {
					data.setDatas(EXMLSchema.pct_publishing_dn, value);
				} else if ("date".equals(qname)) {
					data.setDatas(EXMLSchema.pct_publishing_date, value);
				}
				return;
			}

			/**
			 * PCT 특허 출원 지정국
			 * */
			if (stack.containsAll(PatentPathInfo.path_designation_pct_region)) {
				if ("country".equals(qname)) {
					// logger.info("EXMLSchema.designaion_pct_region {}",
					// value);
					data.setMultiDatas(EXMLSchema.designaion_pct_region, value);
					data.setMultiDatas(EXMLSchema.designaion_pct_regional, "@");
				}
				return;
			}
			/**
			 * PCT 특허 출원 지정국 - EP, EA, ARIPO, OAPI 등 소속 국가
			 * */
			if (stack.containsAll(PatentPathInfo.path_designation_pct_regional)) {
				if ("country".equals(qname)) {
					data.setMultiDatas(EXMLSchema.designaion_pct_regional, value);
				}
				return;
			}
			/**
			 * PCT 특허 출원 지정국 - 조약 개별 체결국
			 * */
			if (stack.containsAll(PatentPathInfo.path_designation_pct_national)) {
				if ("country".equals(qname)) {
					data.setMultiDatas(EXMLSchema.designaion_pct_national, value);
				}
				return;
			}

			/**
			 * 독립항 ID 정보
			 * */
			if (PatentPathInfo.INDEPENDENT_CLAIM.equalsIgnoreCase(qname)) {
//				 logger.info("independent-claims {}", value);
				data.setMultiDatas(EXMLSchema.independent_claims, value);
				return;
			}

			// System.out.println(qname +"\t" +sElement);

			/**
			 * 청구항 내용
			 * */
			if (PatentPathInfo.CLAIMS.equals(qname)) {
				data.setMultiDatas(EXMLSchema.claims, multiValueNumbering(EXMLSchema.claims, value));
				return;
			}

			/**
			 * 청구항 Contents
			 * */
			if (PatentPathInfo.path_claimtext.containsAll(stack)) {
				if (qname.equals(PatentPathInfo.LEXIS_PATENT_DOCUMENT)) {
					return;
				}
			}
			// if (PatentPathInfo.CLAIM_TEXT.equals(qname)) {
			// data.setMultiDatas(EXMLSchema.claims,
			// multiValueNumbering(EXMLSchema.claims, value));
			// return;
			// }

			// if(PatentPathInfo.CLAIM_TEXT.equals(qname)){
			// currValue.append("");
			// return;
			// }

			/**
			 * 특허 명세서 - 요약
			 * */
			if (stack.containsAll(PatentPathInfo.path_description) && PatentPathInfo.DESCRIPTION_SUMMARY.equals(qname)) {
				data.setMultiDatas(EXMLSchema.description_summary,
						multiValueNumbering(EXMLSchema.description_summary, value));
				currValue.setLength(0);
				return;
			}

			/**
			 * 특허 명세서 - 신청 특허 적용예(Ex. 발명품)
			 * */
			if (stack.containsAll(PatentPathInfo.path_description) && PatentPathInfo.DESCRIPTION_RELATED_APPS.equals(qname)) {
				data.setMultiDatas(EXMLSchema.description_related_apps,
						multiValueNumbering(EXMLSchema.description_related_apps, value));
				currValue.setLength(0);
				return;
			}

			/**
			 * 특허 명세서 - 도면 상세 설명
			 * */
			if (stack.containsAll(PatentPathInfo.path_description) && PatentPathInfo.DESCRIPTION_OF_DRAWINGS.equals(qname)) {
				data.setMultiDatas(EXMLSchema.description_drawings,
						multiValueNumbering(EXMLSchema.description_drawings, value));
				currValue.setLength(0);
				return;
			}

			/**
			 * 특허 명세서 - 상세 설명
			 * */
			if (stack.containsAll(PatentPathInfo.path_description) && PatentPathInfo.DESCRIPTION_DETAILED_DESC.equals(qname)) {
				data.setMultiDatas(EXMLSchema.description_detailed_desc,
						multiValueNumbering(EXMLSchema.description_detailed_desc, value));
				currValue.setLength(0);
				return;
			}

			/**
			 * 특허 명세서
			 * */
			if (stack.size() == 1 && PatentPathInfo.DESCRIPTION.equalsIgnoreCase(qname)) {
				if (value.length() > 1) {
					data.setMultiDatas(EXMLSchema.description, multiValueNumbering(EXMLSchema.description, value));
					currValue.setLength(0);
				}
				return;
			}

			/**
			 * 특허 제목
			 * */
			if (PatentPathInfo.TITLE.equals(qname)) {
				data.setMultiDatas(EXMLSchema.ti, multiValueNumbering(EXMLSchema.ti, value));
				return;
			}
			/**
			 * 특허 초록
			 * */
			if (PatentPathInfo.ABSTRACT.equals(qname)) {
				data.setMultiDatas(EXMLSchema.abs, multiValueNumbering(EXMLSchema.abs, value));
				return;
			}

			/**
			 * 특허 발행번호
			 * */
			if (stack.containsAll(PatentPathInfo.publicationNumber)) {
				PatentDataMapsHandler.makePublicationNumber(data, qname, value);
				return;
			}
			/**
			 * 특허 출원번호
			 * */
			if (stack.containsAll(PatentPathInfo.applicationNumber)) {
				PatentDataMapsHandler.makeApplicationNumber(data, qname, value);
				return;
			}

			/**
			 * 특허 출원인
			 * */
			if (stack.containsAll(PatentPathInfo.path_applicants)) {
				se = PatentDataMapsHandler.getApplicantsSchemaEnum(qname);
				if (se == null)
					return;
				putmvm(EXMLSchema.assignee, assseq, asslang, se, value);
				PatentDataMapsHandler.setMultiData(data, se, multiValueNumbering(EXMLSchema.assignee, se, value));
				return;
			}
			
			/**
			 * 특허 발명인
			 * */
			if (stack.containsAll(PatentPathInfo.path_inventor)) {
				se = PatentDataMapsHandler.getInventorSchemaEnum(qname);
				if (se == null)
					return;
				putmvm(EXMLSchema.inventor, invseq, invlang, se, value);
				PatentDataMapsHandler.setMultiData(data, se, multiValueNumbering(EXMLSchema.inventor, se, value));
				return;
			}
			
			/**
			 * 특허 발명인 국적
			 * */
			if (stack.containsAll(PatentPathInfo.path_inventor_nationality)) {
				EXMLSchema s = EXMLSchema.invcnn;
				PatentDataMapsHandler.setMultiData(data, s, multiValueNumbering(s, s, invseq + "_" + value));
				return;
			}
			
			/**
			 * 특허 발명인 국적
			 * */
			if (stack.containsAll(PatentPathInfo.path_inventor_residence)) {
				EXMLSchema s = EXMLSchema.invcnr;
				PatentDataMapsHandler.setMultiData(data, s, multiValueNumbering(s, s, invseq + "_" + value));
				return;
			}
			
			/**
			 * 특허 출원인 국적
			 * */
			if (stack.containsAll(PatentPathInfo.path_applicants_nationality)) {
				EXMLSchema s = EXMLSchema.asscnn;
				PatentDataMapsHandler.setMultiData(data, s, multiValueNumbering(s, s, assseq + "_" + value));
				return;
			}
			
			/**
			 * 특허 출원인 국적
			 * */
			if (stack.containsAll(PatentPathInfo.path_applicants_residence)) {
				EXMLSchema s = EXMLSchema.asscnr;
				PatentDataMapsHandler.setMultiData(data, s, multiValueNumbering(s, s, assseq + "_" + value));
				return;
			}

			/**
			 * 특허 CPC 분류 코드
			 * */
			if (stack.containsAll(PatentPathInfo.path_cpc)) {
				se = PatentDataMapsHandler.getCPCSchemaEnum(qname);
				if (se != null) {
					// System.out.println(qname +" : " + se +" : " + value
					// +"\t"+ stack);
					PatentDataMapsHandler.setMultiData(data, se, multiValueNumbering(EXMLSchema.cpc, se, value));
				}
			}

			/**
			 * 특허 IPC 분류 코드
			 * */
			if (stack.containsAll(PatentPathInfo.path_ipc)) {
				PatentDataMapsHandler.makeIPC(data, qname, value);
				return;
			}

			/**
			 * 특허 IPC 분류 코드
			 * */
			if (stack.containsAll(PatentPathInfo.path_ipcf)) {
				se = PatentDataMapsHandler.getIPCFSchemaEnum(qname);
				PatentDataMapsHandler.setMultiData(data, se, multiValueNumbering(EXMLSchema.ipcf, se, value));
				return;
			}

			/**
			 * 특허 IPCR 분류 코드
			 * */
			if (stack.containsAll(PatentPathInfo.path_ipcr)) {
				try {
					se = PatentDataMapsHandler.getIPCRSchemaEnum(qname);
				} catch (Exception e) {
					// System.out.println(stack);
					// System.out.println(qname);
					e.printStackTrace();
				}
				PatentDataMapsHandler.setMultiData(data, se, multiValueNumbering(EXMLSchema.ipcr, se, value));
				return;
			}

			/**
			 * 특허 National 분류 코드
			 * */
			if (stack.contains(PatentPathInfo.CLASSIFICATION_NATIONAL) & !stack.contains("references-cited")) {
				if (PatentSchema.schema.get(EXMLSchema.classncn).equalsIgnoreCase(qname)) {
					data.setMultiDatas(EXMLSchema.classncn, multiValueNumbering(EXMLSchema.classncn, value));
					return;
				}
			}

			/**
			 * 특허 National Main 분류 코드
			 * */
			if (stack.containsAll(PatentPathInfo.path_national_main) & !stack.contains("references-cited")) {
				se = PatentDataMapsHandler.getNationMainSchemaEnum(qname);
				PatentDataMapsHandler.setMultiData(data, se, multiValueNumbering(EXMLSchema.classc, se, value));
				return;
			}
			
			/**
			 * 특허 National futher 분류 코드
			 * */
			if (stack.containsAll(PatentPathInfo.path_national_further) & !stack.contains("references-cited")) {
				se = PatentDataMapsHandler.getNationFurtherSchemaEnum(qname);
				PatentDataMapsHandler.setMultiData(data, se, multiValueNumbering(EXMLSchema.classfc, se, value));
				return;
			}

			/**
			 * 특허 ECLA 분류 코드
			 * */
			if (stack.containsAll(PatentPathInfo.path_ecla)) {
				se = PatentDataMapsHandler.getECLASchemaEnum(qname);
				if (se == null)
					return;
				PatentDataMapsHandler.setMultiData(data, se, multiValueNumbering(EXMLSchema.ecla, se, value));
				return;
			}

			/**
			 * 특허 일본특허 F-term 분류 코드
			 * */
			if (stack.containsAll(PatentPathInfo.path_fterm)) {
				se = PatentDataMapsHandler.getFTermSchemaEnum(qname);
				if (se == null)
					return;
				// System.out.println(se +"\t" + value);
				PatentDataMapsHandler.setMultiData(data, se, multiValueNumbering(EXMLSchema.fcode, se, value));
				return;
			}

			/**
			 * 특허 우선권 번호
			 * */
			if (stack.containsAll(PatentPathInfo.path_pri)) {
				se = PatentDataMapsHandler.getPRISchemaEnum(qname);
				if (pritkind != null) {
					PatentDataMapsHandler.setMultiData(data, se, multiValueNumbering(EXMLSchema.prino, se, value));
				}
				if (pridataformat != null) {
					PatentDataMapsHandler.setMultiData(data, se, multiValueNumbering(EXMLSchema.prino, se, value));
				}
				return;
			}

			/**
			 * 특허 피인용 특허 발행 정보
			 * */
			if (stack.containsAll(PatentPathInfo.path_citation)) {
				se = PatentDataMapsHandler.getCitationSchemaEnum(qname);
				PatentDataMapsHandler.setMultiData(data, se, multiValueNumbering(EXMLSchema.citno, se, value));
				if (srep != null && se == EXMLSchema.citdn) {
					data.setMultiDatas(EXMLSchema.cit_srep_phase, multiValueNumbering(EXMLSchema.cit_srep_phase, srep));
				}
				return;
			}

			/**
			 * 특허 피인용 특허 출원 정보
			 * */
			if (stack.containsAll(PatentPathInfo.path_citation_app)) {
				se = PatentDataMapsHandler.getCitationAppDateSchemaEnum(qname);
				PatentDataMapsHandler.setMultiData(data, se, multiValueNumbering(EXMLSchema.citno, se, value));
				return;
			}

			/**
			 * 특허 인용 특허 출원 정보
			 * */
			if (stack.containsAll(PatentPathInfo.path_reference_app)) {
				se = PatentDataMapsHandler.getReferenceAppDateSchemaEnum(qname);
				// System.out.println(se +"\t" + value);
				PatentDataMapsHandler.setMultiData(data, se, multiValueNumbering(EXMLSchema.refno, se, value));
				return;
			}

			/**
			 * 특허 인용 특허 발행 출원 정보
			 * */
			if (stack.containsAll(PatentPathInfo.path_reference)) {
				se = PatentDataMapsHandler.getReferenceSchemaEnum(qname);
				// System.out.println(se +"\t" + value);
				PatentDataMapsHandler.setMultiData(data, se, multiValueNumbering(EXMLSchema.refno, se, value));
				if (srep != null && se == EXMLSchema.refdn) {
					data.setMultiDatas(EXMLSchema.ref_srep_phase, multiValueNumbering(EXMLSchema.ref_srep_phase, srep));
				}
				return;
			}

			/**
			 * 비특허 문헌 발행 출원 정보
			 * */
			if (stack.containsAll(PatentPathInfo.path_nonpatent)) {
				se = PatentDataMapsHandler.getNonPatent(qname);
				PatentDataMapsHandler.setMultiData(data, se, multiValueNumbering(EXMLSchema.nonpatent, se, value));
				return;
			}

			/**
			 * Main Family 특허 발행 정보
			 * */
			if (stack.containsAll(PatentPathInfo.path_mf)) {
				se = PatentDataMapsHandler.getMainFamilySchemaEnum(qname);
				PatentDataMapsHandler.setMultiData(data, se, multiValueNumbering(EXMLSchema.mf, se, value));
				return;
			}

			/**
			 * Extended Family 특허 발행 정보
			 * */
			if (stack.containsAll(PatentPathInfo.path_cf)) {
				se = PatentDataMapsHandler.getCompleteFamilySchemaEnum(qname);
				PatentDataMapsHandler.setMultiData(data, se, multiValueNumbering(EXMLSchema.cf, se, value));
				return;
			}

			/**
			 * Main Family 특허 출원 정보
			 * */
			if (stack.containsAll(PatentPathInfo.path_mfapp)) {
				se = PatentDataMapsHandler.getMainFamilyAppDateSchemaEnum(qname);
				PatentDataMapsHandler.setMultiData(data, se, multiValueNumbering(EXMLSchema.mfappdate, se, value));
				return;
			}

			/**
			 * Extended Family 특허 출원 정보
			 * */
			if (stack.containsAll(PatentPathInfo.path_cfapp)) {
				se = PatentDataMapsHandler.getCompleteFamilyAppDateSchemaEnum(qname);
				PatentDataMapsHandler.setMultiData(data, se, multiValueNumbering(EXMLSchema.cfappdate, se, value));
				return;
			}

			/**
			 * 특허 출원 대리인 - 특허변리사무소 정보
			 * */
			if (stack.containsAll(PatentPathInfo.path_agent)) {
				se = PatentDataMapsHandler.getAgentSchemaEnum(qname);
				PatentDataMapsHandler.setMultiData(data, se, multiValueNumbering(EXMLSchema.agent, se, value));
				return;
			}

			/**
			 * 특허 출원 심사관
			 * */
			if (stack.containsAll(PatentPathInfo.path_pexam)) {
				se = PatentDataMapsHandler.getPrimaryExaminerSchemaEnum(qname);
				if (se == null)
					return;
				PatentDataMapsHandler.setMultiData(data, se, multiValueNumbering(EXMLSchema.pexam, se, value));
				return;
			}
			/**
			 * 청구항 건수
			 * */
			if (PatentSchema.schema.get(EXMLSchema.numclaims).equalsIgnoreCase(qname)) {
				data.setDatas(EXMLSchema.numclaims, value);
				return;
			}

			/**
			 * 독립항 건수
			 * */
			if (PatentSchema.schema.get(EXMLSchema.numindclaims).equalsIgnoreCase(qname)) {
				data.setDatas(EXMLSchema.numindclaims, value);
				return;
			}
			
			/**
			 * Prime
			 * 
			 * */
			if (PatentPathInfo.PRIME_NOTIFIED_SECTION.equals(qname)) {
				section = "";
				o_eventDate = "";
				o_eventDateFrom = "";
				o_eventDateTo = "";
				isFirst = false;
				ownerMap.clear();
				return;
			}
			
			if (PatentPathInfo.PRIME_NOTIFIED_EVENT.equals(qname)) {
				if("Ownership".equalsIgnoreCase(section)) {
					if("".equals(o_eventDate) && "".equals(o_eventDateFrom) && "".equals(o_eventDateTo)) {
						PatentDataMapsHandler.setMultiData(data, EXMLSchema.first_assignee, StringUtil.nullCheck(ownerMap.get(EXMLSchema.detail)));
						PatentDataMapsHandler.setMultiData(data, EXMLSchema.first_assignee_standard, StringUtil.nullCheck(ownerMap.get(EXMLSchema.detail_standardized)));
						PatentDataMapsHandler.setMultiData(data, EXMLSchema.first_assignee_normalized, StringUtil.nullCheck(ownerMap.get(EXMLSchema.detail_normalized)));
						
						isFirst = true;
					} else if(isFirst && o_eventDateFrom.equals(ownerMap.get(EXMLSchema.date_from))) {
						PatentDataMapsHandler.setMultiData(data, EXMLSchema.first_assignee, StringUtil.nullCheck(ownerMap.get(EXMLSchema.detail)));
						PatentDataMapsHandler.setMultiData(data, EXMLSchema.first_assignee_standard, StringUtil.nullCheck(ownerMap.get(EXMLSchema.detail_standardized)));
						PatentDataMapsHandler.setMultiData(data, EXMLSchema.first_assignee_normalized, StringUtil.nullCheck(ownerMap.get(EXMLSchema.detail_normalized)));
					} else {
						isFirst = false;
					}
					
					o_eventDate = StringUtil.nullCheck(ownerMap.get(EXMLSchema.date));
					o_eventDateFrom = StringUtil.nullCheck(ownerMap.get(EXMLSchema.date_from));
					o_eventDateTo = StringUtil.nullCheck(ownerMap.get(EXMLSchema.date_to));
					
					if(!"".equals(StringUtil.nullCheck(o_eventDateFrom)) && "".equals(StringUtil.nullCheck(o_eventDateTo))) {
						String detailStandarzed = ownerMap.get(EXMLSchema.detail_standardized);
						
						PatentDataMapsHandler.setMultiData(data, EXMLSchema.cur_assignee, StringUtil.nullCheck(ownerMap.get(EXMLSchema.detail)));
						PatentDataMapsHandler.setMultiData(data, EXMLSchema.cur_assignee_standard, StringUtil.nullCheck(detailStandarzed));
						PatentDataMapsHandler.setMultiData(data, EXMLSchema.cur_assignee_normalized, StringUtil.nullCheck(ownerMap.get(EXMLSchema.detail_normalized)));
						PatentDataMapsHandler.setMultiData(data, EXMLSchema.cur_assignee_from_date, StringUtil.nullCheck(o_eventDateFrom));
					}
					
					PatentDataMapsHandler.setMultiData(data, EXMLSchema.assignee_list, StringUtil.nullCheck(ownerMap.get(EXMLSchema.detail_standardized))+"@@"+StringUtil.nullCheck(ownerMap.get(EXMLSchema.date_from))+"~"+StringUtil.nullCheck(ownerMap.get(EXMLSchema.date_to)));
					
					ownerMap.clear();
				}
				event = "";
				return;
			}
			
			if (stack.containsAll(PatentPathInfo.path_prime_date) || stack.containsAll(PatentPathInfo.path_prime_detail)) {
				se = PatentDataMapsHandler.getPrimeSchemaEnum(qname);
				if (se == null)
					return;
				if("Status".equalsIgnoreCase(section)) {
					PatentDataMapsHandler.makePrimeStatus(data, qname, value);
				} else if("Fees".equalsIgnoreCase(section)) {
					PatentDataMapsHandler.makePrimeFees(data, qname, value);
				} else if("Ownership".equalsIgnoreCase(section)) {
//					PatentDataMapsHandler.makePrimeFees(data, qname, value);
					ownerMap.put(se, value);
				} else if("Statements".equalsIgnoreCase(section)) {
					PatentDataMapsHandler.makePrimeStatements(data, qname, value);
				}
				return;
			}
			
//			if (stack.containsAll(PatentPathInfo.path_related_documents_parent)) {
			if (stack.containsAll(PatentPathInfo.path_related_documents)) {
				if(stack.containsAll(PatentPathInfo.path_related_documents_publication)) {
					se = EXMLSchema.related_doc_publication;
					related_doc_gubun = "document-id";
				} else if(stack.containsAll(PatentPathInfo.path_related_documents_pro_app)) {
					se = EXMLSchema.related_doc_pro_app;
					related_doc_gubun = "document-id";
				} else if(stack.containsAll(PatentPathInfo.path_related_documents_relation)) {
					/*if("related-documents".equalsIgnoreCase(stack.get(stack.size() - 2))) {
						related_doc_type = stack.getLast();
					}*/
					se = EXMLSchema.related_doc_other;
				}
				
				if (se == null)
					return;
				
				Map<Integer, Map<String, Map<EXMLSchema, String>>> coll = data.dataMultiMap.get(se);
				if (coll == null) {
					coll = createMultiValueMap();
				}
				
				Map<String, Map<EXMLSchema, String>> relMap = null;
				if(related_doc_start) {
					relMap = new HashMap<String, Map<EXMLSchema, String>>();
					coll.put(coll.size(), relMap);
					related_doc_start = false;
				} else {
					relMap = coll.get(coll.size()-1);
				}
				/*if (relMap == null) {
					relMap = new HashMap<String, Map<EXMLSchema, String>>();
				}*/
				
				Map<EXMLSchema, String> docMap = relMap.get(related_doc_gubun);
				if (docMap == null) {
					docMap = new HashMap<EXMLSchema, String>();
					relMap.put(related_doc_gubun, docMap);
				}
				
				if("country".equals(qname)) {
					docMap.put(EXMLSchema.related_doc_country, value);
					if(!"".equals(related_doc_type)) {
						docMap.put(EXMLSchema.related_doc_type, related_doc_type);
					}
//					docs[0] = value;
				} else if("doc-number".equals(qname)){
					docMap.put(EXMLSchema.related_doc_number, value);
//					docs[1] = value;
				} else if("date".equals(qname)){
					docMap.put(EXMLSchema.related_doc_date, PatentDataFormat.convertDateHippen(value));
//					docs[2] = PatentDataFormat.convertDateHippen(value);
				} else if("kind".equals(qname)){
					docMap.put(EXMLSchema.related_doc_kind, value);
//					docs[2] = PatentDataFormat.convertDateHippen(value);
				} else if("date".equals(qname)){
					docMap.put(EXMLSchema.related_doc_name, value);
//					docs[2] = PatentDataFormat.convertDateHippen(value);
				} else if("parent-status".equals(qname)) {
					docMap.put(EXMLSchema.related_doc_status, value);
				} else if("provisional-application-status".equals(qname)) {
					docMap.put(EXMLSchema.related_doc_status, value);
				}
				
//				relMap.put(schema.name(), docMap);
//				coll.put(1, relMap);
				data.dataMultiMap.put(se, coll);
				
				currValue.setLength(0);
				if("document-id".equals(qname)) {
					related_doc_start = false;
				}
				
				if("parent-doc".equals(qname)) {
					related_doc_type = "";
				} else if("parent-grant-document".equals(qname)) {
					related_doc_type = "";
				} else if("parent-pct-document".equals(qname)) {
					related_doc_type = "";
				} else if("child-doc".equals(qname)) {
					related_doc_type = "CHILD";
				}
			}
		}
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		currValue.append(ch, start, length);
	}

}
