package com.diquest.util.xml;

import java.util.HashMap;
import java.util.Map;

/**
 * lexisNexis 특허 데이터의 메타 데이터 스키마 정보를 표현<br>
 * 
 * @author neon
 * @date 2013. 6. 3.
 * @Version 1.0
 */
public class PatentSchema {

	/*
	 * invcn : inventor address country <br> invcnn : inventor nationality
	 * country <br> invcnr : inventor residence country <br> asscn : assignee
	 * address country <br> asscnn : assignee nationality country <br> asscnr :
	 * assignee residence country <br>
	 */
	public enum EXMLSchema {
		pno, pncn, pndno, pnkind, pndate, tilang, ti, abslang, abs, dockind, publ_desc, appdate, appno, appcn, appdno, appkind, assignee_lang, assignee, assfn, assln, asscity, asscn, asscnn, asscnr, assseq, assstate, assaddress, inventor_lang, inventor, invln, invfn, invcity, invcn, invcnn, invcnr, invseq, invstate, invaddress, ftheme, fcode, fviewpoint, ffigure, faddcode, cpc, cpcs, cpcc, cpcsc, cpcmg, cpcsg, cpcsp, cpccv, cpcad, cpccn, cpcgo, cpcstatus, cpcdatasource, ipc, ipce, ipcs, ipcc, ipcsc, ipcmg, ipcsg, ipcqc, ipcf, ipcfe, ipcfs, ipcfc, ipcfsc, ipcfmg, ipcfsg, ipcfqc, ipcr, ipcrividate, ipcrlevel, ipcrs, ipcrc, ipcrsc, ipcrmg, ipcrsp, ipcrsg, ipcrcv, ipcradate, ipcrcn, ipcrstatus, ipcrcds, classncn, classn, classc, classsc, classnf, classfc, classfsc, prino, pricn, pridate, pridno, prikind, pritkind, pridataformat, numindclaims, numclaims, citations, references, ecla, eclaschema, eclacn, eclas, eclac, eclasc, eclamg, eclasg, eclaasg, mf, mfcn, mfdno, mfkind, mfdate, mfappdate, cf, cfcn, cfdno, cfkind, cfdate, cfappdate, citno, citcn, citdn, citkind, citdate, citname, citappdate, numcit, refno, refcn, refdn, refkind, refdate, refname, refappdate, numref, nonpatent, scopusurl, cit_srep_phase, ref_srep_phase, extpnyear, extappyear, agrepType, agent,

		description_lang, description_format, description_id, description, description_summary, description_related_apps, description_drawings, description_detailed_desc, independent_claims, claims, claims_lang, claims_id, claims_format,

		pexam, pexam_lastname, pexam_firstname, pexam_department, designaion_pct_regional, designaion_pct_region, designaion_pct_national, pct_filing_cn, pct_filing_dn, pct_filing_date, pct_publishing_cn, pct_publishing_dn, pct_publishing_date, xml,
		hcp_rank, authority,
		date, date_from, date_to, detail, detail_standardized, detail_normalized,
		status, status_date, assignee_list, cur_assignee, cur_assignee_standard, cur_assignee_normalized, cur_assignee_from_date, first_assignee, first_assignee_standard, first_assignee_normalized, fees, fees_detail, fees_date, statements, statements_detail, statements_date, is_change_assignee,
		app_standardized, app_standardized_type, app_normalized, app_normalized_key, related_doc, related_doc_publication, related_doc_pro_app, related_doc_other, related_doc_country, related_doc_number, related_doc_date, related_doc_kind, related_doc_name, related_doc_parent, related_doc_child, related_doc_id, related_doc_status, related_doc_grant, related_doc_pct, related_doc_type,
		mf_m, fc_m, cf_m, bc_m, ipc_full, cpc_full; 
	}

	public static Map<EXMLSchema, String> schema = new HashMap<EXMLSchema, String>();

	static {
		schema.put(EXMLSchema.pno, "TOTAL");
		schema.put(EXMLSchema.authority, "authority");
		schema.put(EXMLSchema.pncn, "country");
		schema.put(EXMLSchema.pndno, "doc-number");
		schema.put(EXMLSchema.pnkind, "kind");
		schema.put(EXMLSchema.pndate, "date");

		schema.put(EXMLSchema.tilang, "date");
		// multi
		schema.put(EXMLSchema.ti, "invention-title");

		schema.put(EXMLSchema.abslang, "date");
		schema.put(EXMLSchema.abs, "date");

		schema.put(EXMLSchema.dockind, "date");

		schema.put(EXMLSchema.appno, "TOTAL");

		schema.put(EXMLSchema.appcn, "country");
		schema.put(EXMLSchema.appdno, "doc-number");
		schema.put(EXMLSchema.appkind, "kind");
		schema.put(EXMLSchema.appdate, "date");

		schema.put(EXMLSchema.app_standardized, "orgname-standardized");
		schema.put(EXMLSchema.app_normalized, "orgname-normalized");

		// multi
		schema.put(EXMLSchema.assignee_lang, "lang");
		schema.put(EXMLSchema.assignee, "orgname");
		schema.put(EXMLSchema.assfn, "first-name");
		schema.put(EXMLSchema.assln, "last-name");
		schema.put(EXMLSchema.asscity, "city");
		schema.put(EXMLSchema.asscn, "country");
		schema.put(EXMLSchema.assstate, "state");
		schema.put(EXMLSchema.assaddress, "address");

		// multi
		schema.put(EXMLSchema.inventor_lang, "lang");
		schema.put(EXMLSchema.inventor, "name");
		schema.put(EXMLSchema.invln, "last-name");
		schema.put(EXMLSchema.invfn, "first-name");
		schema.put(EXMLSchema.invcity, "city");
		schema.put(EXMLSchema.invcn, "country");
		schema.put(EXMLSchema.invstate, "state");
		schema.put(EXMLSchema.invaddress, "address");

		// cpc
		schema.put(EXMLSchema.cpc, "text");
		schema.put(EXMLSchema.cpcs, "section");
		schema.put(EXMLSchema.cpcc, "class");
		schema.put(EXMLSchema.cpcsc, "subclass");
		schema.put(EXMLSchema.cpcmg, "main-group");
		schema.put(EXMLSchema.cpcsg, "subgroup");
		schema.put(EXMLSchema.cpcsp, "symbol-position");
		schema.put(EXMLSchema.cpccv, "classification-value");
		schema.put(EXMLSchema.cpcad, "action-date");
		schema.put(EXMLSchema.cpccn, "country");
		schema.put(EXMLSchema.cpcgo, "generating-office");
		schema.put(EXMLSchema.cpcstatus, "classification-status");
		schema.put(EXMLSchema.cpcdatasource, "classification-data-source");

		// ipc
		schema.put(EXMLSchema.ipc, "text");
		schema.put(EXMLSchema.ipce, "edition");
		schema.put(EXMLSchema.ipcs, "section");
		schema.put(EXMLSchema.ipcc, "class");
		schema.put(EXMLSchema.ipcsc, "subclass");
		schema.put(EXMLSchema.ipcmg, "main-group");
		schema.put(EXMLSchema.ipcsg, "subgroup");
		schema.put(EXMLSchema.ipcqc, "qualifying-character");

		// ipc-further multi
		schema.put(EXMLSchema.ipcf, "text");
		schema.put(EXMLSchema.ipcfe, "edition");
		schema.put(EXMLSchema.ipcfs, "section");
		schema.put(EXMLSchema.ipcfc, "class");
		schema.put(EXMLSchema.ipcfsc, "subclass");
		schema.put(EXMLSchema.ipcfmg, "main-group");
		schema.put(EXMLSchema.ipcfsg, "subgroup");
		schema.put(EXMLSchema.ipcfqc, "qualifying-character");

		// ipcr multi
		schema.put(EXMLSchema.ipcr, "text");
		schema.put(EXMLSchema.ipcrlevel, "classification-level");
		schema.put(EXMLSchema.ipcrs, "section");
		schema.put(EXMLSchema.ipcrividate, "ipc-version-indicator");
		schema.put(EXMLSchema.ipcradate, "action-date");
		schema.put(EXMLSchema.ipcrc, "class");
		schema.put(EXMLSchema.ipcrsc, "subclass");
		schema.put(EXMLSchema.ipcrmg, "main-group");
		schema.put(EXMLSchema.ipcrsg, "subgroup");
		schema.put(EXMLSchema.ipcrsp, "symbol-position");
		schema.put(EXMLSchema.ipcrcv, "classification-value");
		schema.put(EXMLSchema.ipcrcn, "generating-office");
		schema.put(EXMLSchema.ipcrstatus, "classification-status");
		schema.put(EXMLSchema.ipcrcds, "classification-data-source");

		// classification-national
		schema.put(EXMLSchema.classncn, "country");
		schema.put(EXMLSchema.classn, "text");
		schema.put(EXMLSchema.classc, "class");
		schema.put(EXMLSchema.classsc, "subclass");

		schema.put(EXMLSchema.classnf, "text");
		schema.put(EXMLSchema.classfc, "class");
		schema.put(EXMLSchema.classfsc, "subclass");

		// priority
		// schema.put(SchemaEnum.prino, "TOTAL");
		schema.put(EXMLSchema.pricn, "country");
		schema.put(EXMLSchema.pridate, "date");
		schema.put(EXMLSchema.pridno, "doc-number");
		schema.put(EXMLSchema.prikind, "kind");

		schema.put(EXMLSchema.numclaims, "number-of-claims");
		schema.put(EXMLSchema.numindclaims, "independent-claim");

		// multi
		schema.put(EXMLSchema.citations, "pno TOTAL 형식에 따라 구성");
		schema.put(EXMLSchema.references, "pno TOTAL 형식에 따라 구성");

		// ecla
		schema.put(EXMLSchema.ecla, "text");
		schema.put(EXMLSchema.eclas, "section");
		schema.put(EXMLSchema.eclac, "class");
		schema.put(EXMLSchema.eclasc, "subclass");
		schema.put(EXMLSchema.eclamg, "main-group");
		schema.put(EXMLSchema.eclasg, "subgroup");
		schema.put(EXMLSchema.eclaasg, "additional-subgroup");

		// fcode
		schema.put(EXMLSchema.fcode, "text");
		schema.put(EXMLSchema.ftheme, "theme");
		schema.put(EXMLSchema.fviewpoint, "viewpoint");
		schema.put(EXMLSchema.ffigure, "figure");
		schema.put(EXMLSchema.faddcode, "additional-code");

		// citation
		// schema.put(SchemaEnum.citno, "TOTAL");
		schema.put(EXMLSchema.citcn, "country");
		schema.put(EXMLSchema.citdate, "date");
		schema.put(EXMLSchema.citdn, "doc-number");
		schema.put(EXMLSchema.citkind, "kind");
		schema.put(EXMLSchema.citname, "name");
		schema.put(EXMLSchema.citappdate, "date");

		// reference
		// schema.put(SchemaEnum.refno, "TOTAL");
		schema.put(EXMLSchema.refcn, "country");
		schema.put(EXMLSchema.refdate, "date");
		schema.put(EXMLSchema.refdn, "doc-number");
		schema.put(EXMLSchema.refkind, "kind");
		schema.put(EXMLSchema.refname, "name");
		schema.put(EXMLSchema.refappdate, "date");

		// main-family
		schema.put(EXMLSchema.mf, "");
		schema.put(EXMLSchema.mfcn, "country");
		schema.put(EXMLSchema.mfdno, "doc-number");
		schema.put(EXMLSchema.mfkind, "kind");
		schema.put(EXMLSchema.mfdate, "date");
		schema.put(EXMLSchema.mfappdate, "date");

		// complete-family
		schema.put(EXMLSchema.cf, "");
		schema.put(EXMLSchema.cfcn, "country");
		schema.put(EXMLSchema.cfdno, "doc-number");
		schema.put(EXMLSchema.cfkind, "kind");
		schema.put(EXMLSchema.cfdate, "date");
		schema.put(EXMLSchema.cfappdate, "date");

		// primary-examiner
		schema.put(EXMLSchema.pexam, "name");
		schema.put(EXMLSchema.pexam_firstname, "first-name");
		schema.put(EXMLSchema.pexam_lastname, "last-name");
		schema.put(EXMLSchema.pexam_department, "department");

		// agents
		schema.put(EXMLSchema.agrepType, "rep-type");
		schema.put(EXMLSchema.agent, "name");

		// prime
		schema.put(EXMLSchema.date, "date");
		schema.put(EXMLSchema.date_from, "date-from");
		schema.put(EXMLSchema.date_to, "date-to");
		schema.put(EXMLSchema.detail, "detail");
		schema.put(EXMLSchema.detail_normalized, "detail-normalized");
		schema.put(EXMLSchema.detail_standardized, "detail-standardized");

	}

}
