/**
 * 
 */
package com.diquest.util;

/**
 * 
 * DQDOC 필드
 * 
 * @author neon
 * @date 2013. 6. 11.
 * @Version 1.0
 */
public enum EDQDocField {

	// KPASS, GPASS 공통 사용 필드
	ti, abs, pno, au, pndate, apcn, apdate, apk, pricn, pridate, priyear, claims_count, cpc, ipc, main_family_count, extended_family_count, family_total_count, keyword, app, inv, independent_claims_count, xml,
	// G-PASS 단독 사용 검색 필드
	abs_l, abs_e, ti_l, ti_e, pnk, publ_type, pyear, apo, apfcn, apyear, prino, pridate_last, pridate_first, ipcms, ipcmc, ipcmsc, ipcm, ipcrs, ipcrc, ipcrsc, ipcr, ipcs, ipcc, ipcsc, cpcs, cpcc, cpcsc, eclas, eclac, eclasc, ecla, national_cn, national_c, national_sc, national, app_l, inv_l, mfpno, mfpdate, mfapdate, cfpno, cfpdate, cfapdate, citation_total_count, forward_citation_count, fcpno, fcpdate, fcapdate, backward_citation_count, bcpno, bcpdate, bcapdate, non_citation_count, non_patent, fcode, ftheme, keyword2, idx_ti, idx_ti_e, idx_abs, idx_abs_e, idx_keyword, idx_keyword2, idx_all, group_pyear, group_publ_type, group_apfcn, group_ipcsc, group_cpcsc, group_app, group_inv, sort_pndate, sort_pridate_last, sort_pridate_first, sort_claims_count, sort_independent_claims_count, sort_family_total_count, sort_forward_citation_count, sort_backward_citation_count, sort_citation_total_count, total_count, real_count, search_time, group_id, group_value, group_keyword, group_au, group_app_cn, group_inv_cn, app_id, app_cn, inv_cn, app_gp, inv_gp, app_latest,
	// KPASS 전용 검색 필드
	// 공고번호, 등록번호
	pno2, pno3,
	// 공개연도, 공고연도, 등록연도
	py, py2, py3,
	// 공고일, 등록일
	pndate2, pndate3,
	// 공보코드(공개, 공고)
	pk, pk2,
	// 공개분류, 공고분류, 등록구분
	pncat, pncat2, pncat3,
	// 등록 회복 우뮤
	pno3res,
	// 공고발행국가, 등록 국가
	au2, au3,
	// 출원번호, 출원연도
	an, ay,
	// KPASS 출원인
	appcn, appid, appen, appfn, appsn, app_sido, app_sigungu,
	// KPASS 출원인 대표화
	appgp, appgpcn, appgpe, appgpid, appidtype,
	// KPASS 출원인 대표화 XML
	appgpxml,
	// KPASS 권리자 변동 XML
	backxml,
	// 우선권 번호
	pridno,
	// 창작자 정보 (디자인)
	cd, cdcn, cden, cdfn, cd_sido, cd_sigungu,
	// 인용 정보(명세서 항목의 - citationBag)
	citation_count, citbag,
	// 청구항 정보
	claims,
	// 특허 분류 코드 (4자리)
	cpc4, ipc4,
	// 현재 법적상태 정보 - 향휴 필드명 변경 예정 - 각 코드별 유형 파악이 필요하다.
	curlegalcode, curlegaldesc,
	// 도면정보 향후 썸네일 항목 처리
	drawings,
	// 패밀리, 패밀리 id,
	family, familyid,
	// 패밀리 XML
	familyxml,
	// 권리자
	hl, hlcn, hlen, hlfn, hlrc, hl_sido, hl_sigungu,
	// 국제 출원 정보
	ian, iapcn, iapdate, iay,
	// 국제 발행 정보
	ipno, iau, ipndate, ipy,
	// 국제 등록 정보
	ipno3, ipndate3, ipy3,
	// 발명인 정보
	invcn, inven, invfn, invsn, ivnid, inv_sido, inv_sigungu,
	// 출원언어
	lang,
	// 국제 디자인 분류코드
	lcadesign,
	// 법적상태정보 XML
	legalxml,
	// 국내디자인분류 정보
	natdesign, natdesigntype,
	// 원출원 정보
	oan, oapdate, oay, oapk, oapexamdate, oacmmt,
	// 국가 RND 과제 정보
	rndid, rndorg, rndpn, rndsn, rndyn, rndgov,
	// 선행기술 정보
	searchfield,
	// 발명의 명칭 영문
	tie,
	// 발명제목 인접검색 추가
	ti_n, tie_n,
	// 초록, 청구항 인접검색 추가
	abs_n, claims_n,
	// 발명인 출원인 통합 필드
	ci, ah,
	// 특허 유형
	type, dappgp, xml_type, dappgpk,
	
	description, claims_e, claims_en, claims_p,
	description_e,description_en, description_p,
	ti_en, ti_p, abs_en, abs_p, detail_desc_p, desc_summary_p, related_apps_p, 
	detail_desc_en, desc_summary_en, related_apps_en, app_gp_p, app_p, app_en,
	inv_p, inv_en, detail_desc, desc_summary, related_apps,
	detail_desc_e, desc_summary_e, related_apps_e,
	non_patent_p, non_patent_e, non_patent_en,
	aplt,
	APP_LATEST
	;
	

	/**
	 * 
	 * @author neon
	 * @date 2013. 6. 26.
	 * @return
	 */
	public String getUpperCase() {
		return this.name().toUpperCase();
	}

	/**
	 * 마리너3에서 사용하는 인덱스 필드명을 리턴한다.<br>
	 * 
	 * @author neon
	 * @date 2013. 6. 26.
	 * @return
	 */
	public String getConvertIndexField() {
		return "IDX_" + getUpperCase();
	}

	/**
	 * 마리너3에서 사용하는 필터 필드명을 리턴한다.<br>
	 * 
	 * @author neon
	 * @date 2013. 8. 9.
	 * @return
	 */
	public String getFilterField() {
		return "FILTER_" + getUpperCase();
	}

	/**
	 * 마리너3에서 사용하는 그룹 필드명을 리턴한다.<br>
	 * 
	 * @author neon
	 * @date 2013. 8. 14.
	 * @return
	 */
	public String getGroupField() {
		return "GROUP_" + getUpperCase();
	}

	/**
	 * 마리너3에서 사용하는 정렬 필드명을 리턴한다.<br>
	 * 
	 * @author neon
	 * @date 2013. 8. 14.
	 * @return
	 */
	public String getSortField() {
		return "SORT_" + getUpperCase();
	}

	public static EDQDocField[] resultInfoFields = new EDQDocField[] { EDQDocField.total_count, EDQDocField.real_count, EDQDocField.search_time, EDQDocField.group_id, EDQDocField.group_value };
	
	public static EDQDocField[] exportXMLField = new EDQDocField[] { EDQDocField.pno, EDQDocField.xml, EDQDocField.app_gp };

	public static EDQDocField[] exportIPCField = new EDQDocField[] { EDQDocField.pno, EDQDocField.ipc };

	public static final EDQDocField[] _SELECT_EXTEND_PNO = new EDQDocField[] { EDQDocField.pno, EDQDocField.forward_citation_count, EDQDocField.fcpno, EDQDocField.backward_citation_count, EDQDocField.bcpno, EDQDocField.extended_family_count,
			EDQDocField.cfpno };

	public static final EDQDocField[] _SELECT_FIELDS = new EDQDocField[] { EDQDocField.abs_l, EDQDocField.abs_e, EDQDocField.abs, EDQDocField.ti_l, EDQDocField.ti, EDQDocField.pno, EDQDocField.au, EDQDocField.pndate, EDQDocField.pnk,
			EDQDocField.publ_type, EDQDocField.pyear, EDQDocField.apo, EDQDocField.apcn, EDQDocField.apdate, EDQDocField.apk, EDQDocField.apfcn, EDQDocField.apyear, EDQDocField.prino, EDQDocField.pricn, EDQDocField.pridate, EDQDocField.pridate_first,
			EDQDocField.priyear, EDQDocField.ipc, EDQDocField.ipcsc, EDQDocField.cpc, EDQDocField.ecla, EDQDocField.national, EDQDocField.app_l, EDQDocField.app, EDQDocField.inv_l, EDQDocField.inv, EDQDocField.family_total_count,
			EDQDocField.main_family_count, EDQDocField.extended_family_count, EDQDocField.citation_total_count, EDQDocField.forward_citation_count, EDQDocField.backward_citation_count, EDQDocField.non_citation_count, EDQDocField.claims_count,
			EDQDocField.independent_claims_count, EDQDocField.ti_e, EDQDocField.app_gp, EDQDocField.inv_gp, EDQDocField.fcpno, EDQDocField.bcpno, EDQDocField.mfpno, EDQDocField.cfpno
			// , EDQDocField.pridate_last,
			// EDQDocField.ipcmsc, EDQDocField.ipcm, EDQDocField.ipcrsc,
			// EDQDocField.ipcr, EDQDocField.ipcsc,
			// EDQDocField.cpcsc,
			// EDQDocField.eclasc,
			// EDQDocField.national_c,
			// EDQDocField.mfpno, EDQDocField.mfpdate, EDQDocField.mfapdate,
			// EDQDocField.cfpno, EDQDocField.cfpdate, EDQDocField.cfapdate,

			// EDQDocField.fcpdate, EDQDocField.fcapdate,

			// EDQDocField.bcpdate, EDQDocField.bcapdate,
			// EDQDocField.non_patent,
			// EDQDocField.keyword
	};

	public static final EDQDocField[] _SELECT_KPASS_FIELDS = new EDQDocField[] { EDQDocField.an, EDQDocField.ay, EDQDocField.apdate, EDQDocField.pno, EDQDocField.py, EDQDocField.pndate, EDQDocField.pno2, EDQDocField.py2, EDQDocField.pndate2,
			EDQDocField.pno3, EDQDocField.py3, EDQDocField.pndate3, EDQDocField.inv, EDQDocField.app, EDQDocField.appgpe, EDQDocField.appen, EDQDocField.appen, EDQDocField.hl, EDQDocField.cd, EDQDocField.ipc4, EDQDocField.cpc4, EDQDocField.abs,
			EDQDocField.ti, EDQDocField.tie, EDQDocField.curlegalcode, EDQDocField.curlegaldesc, EDQDocField.type, EDQDocField.xml_type, EDQDocField.dappgp, EDQDocField.lcadesign, EDQDocField.natdesign, EDQDocField.drawings };

	/**
	 * 출원번호 중복 제거를 위한 SelectSet 설정.
	 */
	public static final EDQDocField[] _SELECT_FIELD_REMOVE_APPLICATION = new EDQDocField[] { EDQDocField.pno, EDQDocField.pndate, EDQDocField.pnk, EDQDocField.apdate, EDQDocField.apo, };

	public static final EDQDocField[] SETTING_BASIC_FIELD = new EDQDocField[] { EDQDocField.pyear, EDQDocField.publ_type, EDQDocField.au, EDQDocField.apfcn, EDQDocField.ipcsc, EDQDocField.cpcsc, EDQDocField.app_gp, EDQDocField.app_cn,
			EDQDocField.inv_gp, EDQDocField.inv_cn, };

	// 선택 되어진 정보중에서 가장 기본적인 정보
	public static final EDQDocField[] SETTING_DEFAULT_FIELD_SELECT = new EDQDocField[] { EDQDocField.pyear, EDQDocField.publ_type, EDQDocField.au, EDQDocField.ipcsc };

	// 선택 되어진 정보중에서 가장 기본적인 정보
	public static final EDQDocField[] SETTING_DEFAULT_FIELD_GUEST = new EDQDocField[] { EDQDocField.pyear, EDQDocField.publ_type, };

	public static final EDQDocField[] _GROUP_FIELDS = new EDQDocField[] { EDQDocField.pyear, EDQDocField.publ_type };

	public static final EDQDocField[] KPASS_SETTING_BASIC_FIELD = new EDQDocField[] { EDQDocField.type, EDQDocField.ay, EDQDocField.ipc4, EDQDocField.cpc4,
			EDQDocField.curlegaldesc, EDQDocField.appidtype,  EDQDocField.appgpe, EDQDocField.dappgp,
			EDQDocField.appen, EDQDocField.inven, EDQDocField.hlen, EDQDocField.cdfn, EDQDocField.app_sido, EDQDocField.app_sigungu,
			EDQDocField.hl_sido, EDQDocField.hl_sigungu, EDQDocField.rndyn, EDQDocField.rndgov, EDQDocField.rndorg };

	// 선택 되어진 정보중에서 가장 기본적인 정보
	public static final EDQDocField[] KPASS_SETTING_DEFAULT_FIELD_SELECT = new EDQDocField[] {EDQDocField.type, EDQDocField.ay, EDQDocField.curlegaldesc, EDQDocField.appidtype, EDQDocField.app_sido};

	// 선택 되어진 정보중에서 가장 기본적인 정보
	public static final EDQDocField[] KPASS_SETTING_DEFAULT_FIELD_GUEST = new EDQDocField[] { EDQDocField.type, EDQDocField.ay };

	// 기본 KPASS 검색 필드 정보
	public static final EDQDocField[] _GROUP_KPASS_FIELDS = new EDQDocField[] { EDQDocField.type, EDQDocField.ay };

	public static EDQDocField[] _SORT_FIELDS = new EDQDocField[] { EDQDocField.pndate, EDQDocField.pridate_last, EDQDocField.pridate_first, EDQDocField.claims_count, EDQDocField.independent_claims_count, EDQDocField.family_total_count,
			EDQDocField.forward_citation_count, EDQDocField.backward_citation_count, EDQDocField.citation_total_count };

	public static EDQDocField[] _SORT_KPASS_FIELDS = new EDQDocField[] { EDQDocField.apdate, EDQDocField.pndate, EDQDocField.pndate2, EDQDocField.pndate3, EDQDocField.pridate, EDQDocField.claims_count };

	public static EDQDocField[] getResultInfoFields() {
		return resultInfoFields;
	}

	public static void setResultInfoFields(EDQDocField[] resultInfoFields) {
		EDQDocField.resultInfoFields = resultInfoFields;
	}

	public static EDQDocField[] getOrderBySetFields() {
		return _SORT_FIELDS;
	}

	public static void setOrderBySetFields(EDQDocField[] orderBySetFields) {
		EDQDocField._SORT_FIELDS = orderBySetFields;
	}

	public static EDQDocField[] getFields() {
		return values();
	}

	public static void main(String[] args) {
		for (EDQDocField e : EDQDocField.getFields()) {
			System.out.println(e.getConvertIndexField());
		}
	}

	static int groupCPCIndex = 0;
	static int groupIPCIndex = 0;
	static int groupPyearIndex = 0;
	private static int groupAyearIndex = 0;
	static {
		int len = EDQDocField._GROUP_FIELDS.length;
		for (int i = 0; i < len; i++) {
			switch (EDQDocField._GROUP_FIELDS[i]) {
			case ipcsc:
				groupIPCIndex = i;
				break;
			case cpcsc:
				groupCPCIndex = i;
				break;
			case pyear:
				groupPyearIndex = i;
			default:
				break;
			}
		}

		len = EDQDocField._GROUP_KPASS_FIELDS.length;
		for (int i = 0; i < len; i++) {
			switch (EDQDocField._GROUP_KPASS_FIELDS[i]) {
			case ay:
				groupAyearIndex = i;
			default:
				break;
			}
		}

	}

	public static int getGroupCPCFieldIndex() {
		return groupCPCIndex;
	}

	public static int getGroupIPCFieldIndex() {
		return groupIPCIndex;
	}

	public static int getGroupPyearIndex() {
		return groupPyearIndex;
	}

	public static int getGroupAyearIndex() {
		return groupAyearIndex;
	}
}