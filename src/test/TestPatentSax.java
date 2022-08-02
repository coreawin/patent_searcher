/**
 * 
 */
package test;

import java.io.File;
import java.util.LinkedHashSet;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.diquest.util.PatentsDataRefiner;
import com.diquest.util.xml.PatentDataMaps;
import com.diquest.util.xml.PatentSchema.EXMLSchema;
import com.diquest.util.xml.PatentsXMLParse;

/**
 * @author neon
 * @date 2013. 7. 1.
 * @Version 1.0
 */
public class TestPatentSax {
	Logger logger = LoggerFactory.getLogger(getClass());
	
	public String getDefaultLangData(PatentDataMaps data, String priLang, EXMLSchema langField, EXMLSchema dataField) {
		String data1 = PatentsDataRefiner.getLangTextInfo(data, priLang, langField, dataField);
		logger.info("data {} " , data1);
		if ("".equals(data1)) {
			LinkedHashSet<String> langSet = PatentsDataRefiner.multiValue(data.getDatas(langField));
			logger.info("language set  {} " , langSet);
			for (String lang : langSet) {
				lang = lang.substring(lang.length()-3);
				data1 = PatentsDataRefiner.getLangTextInfo(data, lang, langField, dataField);
				if (!"".equals(data1)) {
					break;
				}
			}
		}
		
		logger.info("result {}", data1);
		return data1 == null ? "" : data1;
	}

	@Test
	public void testSax() {
		File xml = new File("t:\\AR241907A1.xml");
//		File xml = new File("t:\\tmp\\patent\\xml\\US20060240443A1.xml");
//		File xml = new File("t:\\tmp\\patent\\wxml\\US8475645B2.xml");
//		File xml = new File("t:\\tmp\\patent\\wxml\\US20120308190A1.xml");
//		File xml = new File("t:\\tmp\\patent\\err\\US8417383B2.xml");
//		File xml = new File("T:\\download\\chrome\\AR034389A1 (1).xml");
//		File xml = new File("T:\\download\\chrome\\USD0495657S.xml");
//		File xml = new File("T:\\download\\chrome\\WO2007040200A1.xml");
//		File xml = new File("t:\\tmp\\patent\\wxml\\JP2013151063A.xml");
//		File xml = new File("T:\\download\\chrome\\KR1020130036407A.xml");
//		File xml = new File("T:\\download\\chrome\\KR1020050037791A.xml");
//		xml = new File("t:\\tmp\\patent\\wxml\\KR1020120075496A.xml");
//		File xml = new File("t:\\tmp\\patent\\wxml\\US5859197A.xml");
//		File xml = new File("t:\\tmp\\patent\\wxml\\WO2010088269A2.xml");
//		File xml = new File("T:/download/chrome/WO2013170528A1.xml"); /* designation-of-states */
//		File xml = new File("T:/download/chrome/JP2003042025A.xml"); /* assignee */
		
		xml = new File("T:/download/chrome/KR101456924B1.xml"); /* inventor 영문이 없는데 영문으로 나온다/ */
		
		xml = new File("T:/download/chrome/US8879607B2.xml"); /* CPC 코드 출력 오류 null로 나온다.*/
		
		xml = new File("T:/download/chrome/US20140193888A1.xml"); /* 독립항 건수가 0으로 나옴.*/
		xml = new File("T:/download/chrome/US20140223669A1.xml"); /* 독립항 건수가 0으로 나옴. 테스트*/
		
		xml = new File("T:/download/chrome/US20110220040A1.xml"); /* 우선권 format이 잘못 기재되어 나오는 현상.*/
		
		xml = new File("T:/download/chrome/US20110220040A1.xml"); /* PCT Application 추출. ECLA*/
		xml = new File("T:/download/chrome/JP2014173126A.xml"); /* F-term*/
		
		xml = new File("T:/download/chrome/US8892794B2.xml"); /* National */
		
		
		xml = new File("C:/Users/aurtoban/Documents/NEOBIZBOX 받은 파일/US6039984A.xml"); /* IPC 순서 확인 */
		
		
		
		
		
		
		
//		File xml = new File("t:\\tmp\\patent\\xml\\US8500360B1.xml");
//		File xml = new File("t:\\tmp\\patent\\xml\\KR1020130055339A.xml");
//		File xml = new File("t:\\tmp\\patent\\xml\\DE19542246C2.xml");
//		File xml = new File("t:\\tmp\\patent\\xml\\KR0142782B1.xml");
//		File xml = new File("t:\\tmp\\patent\\xml\\abs_sax_error\\US8368507B2.xml");
//		File xml = new File("T:\\download\\chrome\\DE102011077295A1.xml"); /*초록이 파싱되지 않는 문제.*/
//		File xml = new File("T:\\download\\chrome\\US8544482B2.xml"); /*examier.*/
//		File xml = new File("T:\\download\\chrome\\CN202523813U.xml"); /*publ type.*/
//		File xml = new File("T:\\download\\chrome\\JP4928562B2.xml"); /*national*/
//		File xml = new File("T:\\download\\chrome\\US20060206769A1.xml"); /*inventor*/
//		File xml = new File("T:\\download\\chrome\\JP5303477B2.xml"); /*inventor*/
//		File xml = new File("t:\\download\\chrome\\EP355246A1.xml"); /*applicants*/
//		File xml = new File("t:\\download\\chrome\\KR0184275B1.xml"); /*main ipc*/
//		File xml = new File("t:\\tmp\\patent\\US5164215A.xml"); /*description*/
//		File xml = new File("t:\\tmp\\patent\\US5164203A.xml"); /*description*/
//		File xml = new File("t:\\tmp\\patent\\US5164637A.xml"); /*claims*/
//		File xml = new File("t:\\tmp\\patent\\USD0331113S.xml"); /*claims*/
//		File xml = new File("T:\\download\\chrome\\EP2674601A1.xml"); /*description*/
//		File xml = new File("T:\\tmp\\patent\\US5164637A.xml"); /*description*/
//		File xml = new File("T:/download/chrome/USRE034126E.xml"); /*claims*/
//		File xml = new File("T:/download/chrome/USRE034121E.xml"); /*claims*/
//		File xml = new File("T:/download/chrome/KR19890004552B1.xml"); /*applicants*/
//		File xml = new File("C:\\Users\\user\\Downloads\\US4881259B1.xml"); /*description*/
//		File xml = new File("T:/download/chrome/WO2008073457A2.xml"); /*reference none*/
//		File xml = new File("T:/download/chrome/US20030123589A1.xml"); /*inventor*/
//		File xml = new File("T:/download/chrome/US6335063B1.xml"); /*assignee country */
//		File xml = new File("T:/download/chrome/US8680693B2.xml"); /*claims */
//		File xml = new File("T:/download/chrome/US8822400B2.xml"); /*claims */
//		File xml = new File("T:/download/chrome/US8763184B2.xml"); /*claims */
//		File xml = new File("T:/TEMP/US5898434A.xml"); /*independent-claim */
//		File xml = new File("T:/download/chrome/US2926465A.xml"); /*independent-claim */
//		File xml = new File("T:/download/chrome/US8129141B2.xml"); /*scopus-url */
//		File xml = new File("T:/download/chrome/US20110193677A1.xml"); /*scopus-url */
		try {
			PatentsXMLParse parser = new PatentsXMLParse();
			PatentDataMaps patentData = parser.parse(xml);
			logger.info("pno :  {} " , patentData.getDatas(EXMLSchema.pno));
			logger.info("pndate" , patentData.getDatas(EXMLSchema.pndate));
			logger.info("ipc4dight : {} " , PatentsDataRefiner.getIPC4InfoForExport(patentData));
			logger.info("cpc4dight : {} " , PatentsDataRefiner.getCPC4InfoForExport(patentData));
//			logger.info("designaion_pct_national {} " , patentData.getDatas(EXMLSchema.designaion_pct_national));
//			logger.info("designaion_pct_region {} " , patentData.getDatas(EXMLSchema.designaion_pct_region));
//			logger.info("designaion_pct_regional {} " , PatentsDataRefiner.getDesignaionPCTRegional(patentData));
			
			
//			Map<String, String> absData = PatentsDataRefiner.getLangTextInfo(patentData, new EXMLSchema[]{EXMLSchema.abslang, EXMLSchema.abs}, EXMLSchema.abslang, EXMLSchema.abs);
//			Map<String, String> titleData = PatentsDataRefiner.getLangTextInfo(patentData, new EXMLSchema[]{EXMLSchema.tilang, EXMLSchema.ti}, EXMLSchema.tilang, EXMLSchema.ti);
//			Map<String, Set<String>> assigneeData = PatentsDataRefiner.getAssigneeInfoMap(patentData);
//			Map<String, Set<String>> inventorData = PatentsDataRefiner.getInventorInfoMap(patentData);
//			logger.info("assignee lang {} " , assigneeData);
//			logger.info("inventor lang {} " , inventorData);
//			logger.info("multidata {} " , patentData.dataMultiMap);
//			Map<String, Set<String>> assigneeMultiData = PatentsDataRefiner.getAssigneeInventorMultiMapInfo(patentData, EXMLSchema.assignee, EXMLSchema.assignee, EXMLSchema.asscn);
//			Map<String, Set<String>> inventorMultiData = PatentsDataRefiner.getAssigneeInventorMultiMapInfo(patentData, EXMLSchema.inventor, EXMLSchema.inventor, EXMLSchema.invcn);
//			logger.info("assignee multi  {} " , assigneeMultiData);
//			logger.info("inventor multi  {} " , inventorMultiData);
			
//			logger.info("abs lang {} " , absData);
//			logger.info("ti lang {} " , titleData);
//			Set<String> independent_claims = PatentsDataRefiner.getIndependendClaimsData(patentData.getDatas(EXMLSchema.independent_claims));
//			logger.info("independent independent_claims {}", patentData.getDatas(EXMLSchema.independent_claims));
//			logger.info("independent numindclaims {}", patentData.getDatas(EXMLSchema.numindclaims));
			
//			for(String i : independent_claims){
//				logger.info("independent id :{} " , i);
//			}
			
//			logger.info("independent claims size :{} " , independent_claims.size());
//			logger.info("claims id :{} " , patentData.getDatas(EXMLSchema.claims_id));
//			logger.info("claims lang :{} " , patentData.getDatas(EXMLSchema.claims_lang));
//			logger.info("claims format :{} " , patentData.getDatas(EXMLSchema.claims_format));
//			Map<String, String> cInfoMap = PatentsDataRefiner.getClaimInfo("eng", patentData);
//			logger.info("claims cinfomap :{} " , cInfoMap.get(EXMLSchema.claims.name()));
//			String[] claims = PatentsDataRefiner.getClaimsData(cInfoMap.get(EXMLSchema.claims.name()));
//			for(String c : claims){
//				logger.info("claims :{} " , c);
//			}
//			
//			logger.info("description_summary :{} " , patentData.getDatas(EXMLSchema.description_summary));
//			logger.info("description_related_apps : {} " , patentData.getDatas(EXMLSchema.description_related_apps));
//			logger.info("description_drawings : {} " , patentData.getDatas(EXMLSchema.description_drawings));
//			logger.info("description_detailed_desc : {} " , patentData.getDatas(EXMLSchema.description_detailed_desc));
//			logger.info("description : {} " , patentData.getDatas(EXMLSchema.description).length());
//			logger.info("DescriptionInfo : {} " , PatentsDataRefiner.getDescriptionInfo("eng", patentData));
			
			
//			logger.info("Inventor {} ", PatentsDataRefiner.getInventorInfo(patentData));
//			logger.info("Assignee {} ", PatentsDataRefiner.getAssigneesInfo(patentData));
//			logger.info("Assignee view {} ", PatentsDataRefiner.getAssigneeInventorMultiMapInfo(patentData, EXMLSchema.assignee, EXMLSchema.assignee, EXMLSchema.asscn));
//			logger.info("Assignee export {} ", PatentsDataRefiner.getLangData(PatentsDataRefiner.getAssigneeInventorMultiMapInfo(patentData,EXMLSchema.assignee, EXMLSchema.assignee, EXMLSchema.asscn)));
//			logger.info("inventor export {} ", PatentsDataRefiner.getLangData(PatentsDataRefiner.getAssigneeInventorMultiMapInfo(patentData,EXMLSchema.inventor, EXMLSchema.inventor, EXMLSchema.invcn)));
			
			
			
//			logger.info("US Main {} ", PatentsDataRefiner.getUSMainInfo(patentData));
//			logger.info("US Further {} ", PatentsDataRefiner.getUSFurtherInfo(patentData));
//			logger.info("agrepType {} " , patentData.getDatas(EXMLSchema.dockind));
//			logger.info("publ_desc {} " , patentData.getDatas(EXMLSchema.publ_desc));
//			logger.info("title : {} " , PatentsDataRefiner.getLangTextInfo(patentData, new EXMLSchema[]{EXMLSchema.tilang,  EXMLSchema.ti}, EXMLSchema.tilang,  EXMLSchema.ti));
//			logger.info("abs : {} " , PatentsDataRefiner.getLangTextInfo(patentData, new EXMLSchema[]{EXMLSchema.abslang,  EXMLSchema.abs}, EXMLSchema.abslang,  EXMLSchema.abs));
//			Map<String, String> absMap = PatentsDataRefiner.getLangTextInfo(patentData, new EXMLSchema[]{EXMLSchema.abslang,  EXMLSchema.abs}, EXMLSchema.abslang,  EXMLSchema.abs);
//			logger.info("abs : {} " , absMap.get("eng"));
//			logger.info("examier : {} " , PatentsDataRefiner.getPrimaryExaminerInfo(patentData));
//			logger.info("srep ref : {} " , patentData.getDatas(EXMLSchema.ref_srep_phase));
//			logger.info("srep cit : {} " , patentData.getDatas(EXMLSchema.cit_srep_phase));
//			Set<String> npset = PatentsDataRefiner.getNonPatent(patentData);
//			for(String np : npset){
//				logger.info("non patent : {} " , np);
//			}
			
			
//			Set<String> refs = PatentsDataRefiner.getReferenceInfoForExport(patentData);
//			for(String s: refs){
//				logger.info("ref exp : {} ", s);
//			}
//			Set<String> refss = PatentsDataRefiner.getReferenceInfo(patentData);
//			for(String s: refss){
//				logger.info("ref info : {} ", s);
//			}
//			Set<String> priSet = PatentsDataRefiner.getPriorityInfo(patentData);
//			for(String s: priSet){
//				logger.info("pri list : {} ", s);
//			}
//			Set<String> priExportSet = PatentsDataRefiner.getPriorityInfoForExport(patentData);
//			for(String s: priExportSet){
//				logger.info("pri export list : {} ", s);
//			}
//			Set<String> refInfo = PatentsDataRefiner.getReferenceInfo(patentData);
//			for(String s: refInfo){
//				System.out.println(s);
//			}
//			Set<String> ns = PatentsDataRefiner.getNonPatent(patentData);
//			Set<String> ps = PatentsDataRefiner.getPriorityInfo(patentData);
//			for(String p : ns){
//				logger.info(p);
//			}
//			logger.info("inventor");
//			Set<String> s = PatentsDataRefiner.getInventorInfo(patentData);
//			logger.info(s.toString());7
//			logger.info("inventor map" + PatentsDataRefiner.getInventorInfoMap(patentData));
//			logger.info("assignee map " + PatentsDataRefiner.getAssigneeInfoMap(patentData));
//			logger.info("assignee set  " + PatentsDataRefiner.getAssigneesInfo(patentData));
//			logger.info(patentData.toString());
//			Map<String, Set<String>> langData = PatentsDataRefiner.getAssigneeInfoMap(patentData);
//			Set<String> set = new HashSet<String>();
//			if (langData.containsKey("eng")) {
//				set = langData.get("eng");
//			} else {
//				if (langData.size() > 0) {
//					Set<String> s = langData.keySet();
//					for (String key : s) {
//						set = langData.get(key);
//						break;
//					}
//				}
//			}
//			logger.info("assignee : " + set.toString());
//			logger.info("assignee : " + set.size());
//			getDefaultLangData(patentData, "eng", EXMLSchema.tilang, EXMLSchema.ti);
//			logger.info("assignee");
//			Set<String> a = PatentsDataRefiner.getAssigneesInfo(patentData);
//			logger.info(a.toString());
//			logger.info("IPC " + PatentsDataRefiner.getIPC4InfoForExport(patentData));
//			logger.info("IPC " + PatentsDataRefiner.getIPCFullInfo(patentData));
//			logger.info("IPC " + PatentsDataRefiner.getIPCFullInfoForExport(patentData));
//			logger.info("CPC export  " + PatentsDataRefiner.getCPC4InfoForExport(patentData));
//			logger.info("CPC full info " + PatentsDataRefiner.getCPCFullInfo(patentData));
//			logger.info("CPC info 0 " + PatentsDataRefiner.getCPCInfo(patentData)[0]);
//			logger.info("CPC info 1 " + PatentsDataRefiner.getCPCInfo(patentData)[1]);
//			logger.info("ECLA " + PatentsDataRefiner.getECLA4InfoForExport(patentData));
//			logger.info("ECLA" + PatentsDataRefiner.getECLAInfo(patentData));
//			logger.info("ECLA" + PatentsDataRefiner.getECLAFullInfo(patentData));
			
//			logger.info("FTERM " + PatentsDataRefiner.getFTermInfo(patentData));
//			logger.info("national main : " + PatentsDataRefiner.getUSMainInfo(patentData));
//			logger.info("national futther : " + PatentsDataRefiner.getUSFurtherInfo(patentData));
			
			
//			logger.info("PCT application filing date " + patentData.getDatas(EXMLSchema.pct_filing_cn) + patentData.getDatas(EXMLSchema.pct_filing_dn) + "\t" + patentData.getDatas(EXMLSchema.pct_filing_date));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
