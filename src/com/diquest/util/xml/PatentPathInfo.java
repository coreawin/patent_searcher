package com.diquest.util.xml;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author coreawin
 * @date 2014. 11. 3.
 */
public class PatentPathInfo {

	public static Set<String> path_title = new HashSet<String>();
	public static Set<String> path_abstract = new HashSet<String>();
	
	public static Set<String> path_publicationReference = new HashSet<String>();
	public static Set<String> publicationNumber = new HashSet<String>();
	public static Set<String> applicationNumber = new HashSet<String>();
	
	public static Set<String> path_app = new HashSet<String>();
	public static Set<String> path_applicants = new HashSet<String>();
	public static Set<String> path_applicants_lang = new HashSet<String>();
	public static Set<String> path_applicants_nationality = new HashSet<String>();
	public static Set<String> path_applicants_residence = new HashSet<String>();
	
	public static Set<String> path_inv = new HashSet<String>();
	public static Set<String> path_inventor = new HashSet<String>();
	public static Set<String> path_inventor_nationality = new HashSet<String>();
	public static Set<String> path_inventor_residence = new HashSet<String>();
	public static Set<String> path_inventor_lang = new HashSet<String>();

	public static Set<String> path_cpc = new HashSet<String>();
	public static Set<String> path_ipc = new HashSet<String>();
	public static Set<String> path_ipcf = new HashSet<String>();
	public static Set<String> path_ipcr = new HashSet<String>();
	public static Set<String> path_national_main = new HashSet<String>();
	public static Set<String> path_national_further = new HashSet<String>();
	public static Set<String> path_ecla = new HashSet<String>();
	public static Set<String> path_fterm = new HashSet<String>();
	public static Set<String> path_pri = new HashSet<String>();
	
	public static Set<String> path_citation_srep = new HashSet<String>();
	public static Set<String> path_citation = new HashSet<String>();
	public static Set<String> path_reference = new HashSet<String>();
	public static Set<String> path_nonpatent = new HashSet<String>();

	public static Set<String> path_citation_app = new HashSet<String>();
	public static Set<String> path_reference_app = new HashSet<String>();

	public static Set<String> path_mf = new HashSet<String>();
	public static Set<String> path_mfapp = new HashSet<String>();
	public static Set<String> path_cf = new HashSet<String>();
	public static Set<String> path_cfapp = new HashSet<String>();

	public static Set<String> path_pexam = new HashSet<String>();
	public static Set<String> path_agent = new HashSet<String>();
	
	public static Set<String> path_claimtext = new HashSet<String>();
	
	public static Set<String> path_description = new HashSet<String>();
	public static Set<String> path_description_summary = new HashSet<String>();
	public static Set<String> path_description_related_apps = new HashSet<String>();
	public static Set<String> path_description_drawings = new HashSet<String>();
	public static Set<String> path_description_detaild_desc = new HashSet<String>();
	
	/**
	 * 지정국
	 */
	public static Set<String> path_designation_of_states = new HashSet<String>();
	public static Set<String> path_designation_pct = new HashSet<String>();
	public static Set<String> path_designation_pct_national = new HashSet<String>();
	public static Set<String> path_designation_pct_regional = new HashSet<String>();
	public static Set<String> path_designation_pct_region = new HashSet<String>();
	public static Set<String> path_designation_epc = new HashSet<String>();
	
	public static Set<String> path_pct_application_filing = new HashSet<String>();
	public static Set<String> path_pct_application_publishing = new HashSet<String>();
	
	/**
	 * Prime 
	 */
	public static Set<String> path_prime = new HashSet<String>();
	public static Set<String> path_prime_date = new HashSet<String>();
	public static Set<String> path_prime_detail = new HashSet<String>();
	
	/**
	 * related-documents 
	 */
	public static Set<String> path_related_documents = new HashSet<String>();
	public static Set<String> path_related_documents_publication = new HashSet<String>();
	public static Set<String> path_related_documents_pro_app = new HashSet<String>();
	public static Set<String> path_related_documents_relation = new HashSet<String>();
	
//	public static Set<String> path_related_documents_parent = new HashSet<String>();
//	public static Set<String> path_related_documents_parent_id = new HashSet<String>();
//	public static Set<String> path_related_documents_parent_grant = new HashSet<String>();
//	public static Set<String> path_related_documents_parent_pct = new HashSet<String>();
//	public static Set<String> path_related_documents_child = new HashSet<String>();
	
	public static Set<String> path_bibliographic = new HashSet<String>();

	public static final String PUBLICATION_REFERENCE = "publication-reference";
	public static final String CLASSIFICATION_NATIONAL = "classification-national";
	public static final String CLASSIFICATION_ECLA = "classification-ecla";
	public static final String NUMBER_OF_CLAIM = "number-of-claims";
	public static final String AGENT = "agent";
	public static final String AGENT_REPTYPE = "rep-type";
	
	public static final String SEQUENCE = "sequence";
	
	public static final String SREP_PHASE = "srep-phase";

	public static final String TITLE = "invention-title";
	public static final String TITLE_LANG = "lang";
	public static final String PRIORITY_CLAIMS = "priority-claim";
	public static final String PRIORITY_CLAIMS_TKIND = "kind";
	public static final String PRIORITY_DATA_FORMAT = "data-format";

	public static final String ECLA_SCHEMA = "classification-scheme";
	public static final String ECLA_COUNTRY = "country";

	public static final String ABSTRACT = "abstract";
	public static final String DESCRIPTION = "description"; /*2014-05 추가*/
	public static final String DESCRIPTION_SUMMARY = "summary"; /*2014-05 추가*/
	public static final String DESCRIPTION_RELATED_APPS = "related-apps"; /*2014-05 추가*/
	public static final String DESCRIPTION_OF_DRAWINGS = "description-of-drawings"; /*2014-05 추가*/
	public static final String DESCRIPTION_DETAILED_DESC = "detailed-desc"; /*2014-05 추가*/
	public static final String CLAIMS = "claims"; /*2014-05 추가*/
	public static final String CLAIM = "claim"; /*2014-05 추가*/
	public static final String INDEPENDENT_CLAIM = "independent-claim";
	public static final String CLAIM_TEXT = "claim-text"; /*2014-05 추가*/
	public static final String CLAIM_REF = "claim-ref"; /*2014-10 추가*/
	public static final String LANG = "lang";

	public static final String DOCUMENT_KIND = "publ-type";
	public static final String DOCUMENT_DESC = "publ-desc";
	
	public static final String LEXIS_PATENT_DOCUMENT = "lexisnexis-patent-document";
	public static final String BIGLIOGRAPHIC = "bibliographic-data";
	
	public static final String PRIME_NOTIFIED_EVENTS = "prime-notified-events";	/*2018-09 추가*/
	public static final String PRIME_NOTIFIED_SECTION = "prime-notified-section";	/*2018-09 추가*/
	public static final String PRIME_NOTIFIED_EVENT = "prime-notified-event";	/*2018-09 추가*/
	
	public static final String RELATED_DOCUMENTS = "related-documents"; /*2018-10 추가*/ 

	static {
		
		path_bibliographic.add(LEXIS_PATENT_DOCUMENT);
		path_bibliographic.add(BIGLIOGRAPHIC);
		
		path_pct_application_filing.addAll(path_bibliographic);
		path_pct_application_filing.add("pct-or-regional-filing-data");
		
		path_pct_application_publishing.addAll(path_bibliographic);
		path_pct_application_publishing.add("pct-or-regional-publishing-data");
		
		path_designation_of_states.addAll(path_bibliographic);
		path_designation_of_states.add("designation-of-states");
		path_designation_pct.addAll(path_designation_of_states);
		path_designation_pct.add("designation-pct");
		
		path_designation_pct_national.addAll(path_designation_pct);
		path_designation_pct_national.add("national");
		
		path_designation_pct_regional.addAll(path_designation_pct);
		path_designation_pct_regional.add("regional");
		
		path_designation_pct_region.addAll(path_designation_pct_regional);
		path_designation_pct_region.add("region");
		
		path_designation_epc.addAll(path_designation_of_states);
		path_designation_epc.add("designation-epc");
		
		path_abstract.add("abstract");
		
		path_claimtext.add(LEXIS_PATENT_DOCUMENT);
		path_claimtext.add(CLAIMS);
		path_claimtext.add(CLAIM);
		path_claimtext.add(CLAIM_TEXT);
		path_claimtext.add(CLAIM_REF);
		
		
		path_description.add(DESCRIPTION);
		
		path_description_summary.addAll(path_description);
		path_description_summary.add(DESCRIPTION_SUMMARY);
		
		path_description_related_apps.addAll(path_description);
		path_description_related_apps.add(DESCRIPTION_RELATED_APPS);
		
		path_description_drawings.addAll(path_description);
		path_description_drawings.add(DESCRIPTION_OF_DRAWINGS);
		
		path_description_detaild_desc.addAll(path_description);
		path_description_detaild_desc.add(DESCRIPTION_DETAILED_DESC);
		
		path_publicationReference.add("publication-reference");

		publicationNumber.add("publication-reference");
		publicationNumber.add("document-id");

		applicationNumber.add("application-reference");
		applicationNumber.add("document-id");

		path_app.addAll(path_bibliographic);
		path_app.add("parties");
		path_app.add("applicants");
		path_app.add("applicant");
		
		path_applicants.addAll(path_app);
		path_applicants.add("addressbook");

		path_applicants_lang.addAll(path_applicants);
		path_applicants_lang.add("addressbook");
		
		path_applicants_nationality.addAll(path_app);
		path_applicants_nationality.add("nationality");
		
		path_applicants_residence.addAll(path_app);
		path_applicants_residence.add("residence");

		path_inv.add("parties");
		path_inv.add("inventors");
		path_inv.add("inventor");
		
		path_inventor.addAll(path_inv);
		path_inventor.add("addressbook");
		
		path_inventor_lang.addAll(path_inventor);
		path_inventor_lang.add("addressbook");
		
		path_inventor_nationality.addAll(path_inv);
		path_inventor_nationality.add("nationality");
		
		path_inventor_residence.addAll(path_inv);
		path_inventor_residence.add("residence");

		path_cpc.add("classifications-cpc");
		path_cpc.add("classification-cpc");
		
		path_ipc.add("classification-ipc");
		path_ipc.add("main-classification");

		path_ipcf.add("classification-ipc");
		path_ipcf.add("further-classification");

		path_ipcr.add("classification-ipcr");

		path_national_main.add("classification-national");
		path_national_main.add("main-classification");

		path_national_further.add("classification-national");
		path_national_further.add("further-classification");

		path_ecla.add("classifications-ecla");
		path_ecla.add("classification-ecla");
		
		path_fterm.add("classifications-f-term");
		path_fterm.add("classification-f-term");

		path_pri.add("priority-claims");

		path_citation_srep.add("lexisnexis-patent-document");
		path_citation_srep.add("bibliographic-data");
		path_citation_srep.add("references-cited");
		path_citation_srep.add("citation");
		
		path_citation.add("references-cited");
		path_citation.add("citation");
		path_citation.add("fwdcit");
		path_citation.add("document-id");
		path_citation_app.addAll(path_citation);
		path_citation_app.add("application-date");
		path_citation_app.remove("document-id");

		path_reference.add("references-cited");
		path_reference.add("citation");
		path_reference.add("patcit");
		path_reference.add("document-id");
		
		path_nonpatent.add("references-cited");
		path_nonpatent.add("citation");
		path_nonpatent.add("nplcit");

		path_reference_app.addAll(path_reference);
		path_reference_app.add("application-date");
		path_reference_app.remove("document-id");

		path_mf.add("document-id");
		path_mf.add("main-family");
		path_mf.add("family-member");
		path_mfapp.addAll(path_mf);
		path_mfapp.add("application-date");
		path_mfapp.remove("document-id");

		path_cf.add("document-id");
		path_cf.add("complete-family");
		path_cf.add("family-member");
		path_cfapp.addAll(path_cf);
		path_cfapp.add("application-date");
		path_cfapp.remove("document-id");

		path_agent.add("parties");
		path_agent.add("agents");
		path_agent.add("agent");

		path_pexam.add("examiners");
		path_pexam.add("primary-examiner");
		
		path_prime.add("prime-notified-events");
		path_prime.add("prime-notified-section");
		path_prime.add("prime-notified-event");
		
		path_prime_date.addAll(path_prime);
		path_prime_date.add("event-date");
		
		path_prime_detail.addAll(path_prime);
		path_prime_detail.add("event-details");
		
		path_related_documents.add(RELATED_DOCUMENTS);
//		path_related_documents.add("continuation");
//		path_related_documents.add("relation");
		
		path_related_documents_publication.addAll(path_related_documents);
		path_related_documents_publication.add("related-publication");
		
		path_related_documents_pro_app.addAll(path_related_documents);
		path_related_documents_pro_app.add("provisional-application");
		
		path_related_documents_relation.addAll(path_related_documents);
		path_related_documents_relation.add("relation");
	}

}
