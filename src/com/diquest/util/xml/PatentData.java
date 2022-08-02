package com.diquest.util.xml;


/**
 * The <b>priority number</b> is made up of: <br>
 * • A country code (2 letters)<br>
 * • The year of filing (4 digits)<br>
 * • A serial number (variable, maximum 7 characters)<br>
 * Priority number examples:<br>
 * • GB19958026 or GB19950008026<br>
 * • FR19960006814<br>
 * • US19990260426<br>
 * • DE19961025214<br>
 * */
public class PatentData {

	public String pno, pncn, pndno, pnkind, pndate, tilang, ti, abslang, abs, dockind, appdate, appno, appcn, appdno, appkind,
			assignee, inventor, ipc, ipce, ipcs, ipcc, ipcsc, ipcmg, ipcsg, ipcqc, ipcf, ipcfe, ipcfs, ipcfc, ipcfsc, ipcfmg,
			ipcfsg, ipcfqc, ipcr, ipcrlevel, ipcrs, ipcrc, ipcrsc, ipcrmg, ipcrsg, ipcrcv, ipcrcn, ipcrstatus, ipcrcds, classncn,
			classn, classc, classsc, classnf, classfc, classfsc, prino, pricn, pridate, pridno, prikind, numclaims, citations,
			references, ecla, eclaschema, eclacn, eclas, eclac, eclasc, eclamg, eclasg, mf, mfcn, mfdno, mfkind, mfdate,
			mfappdate, cf, cfcn, cfdno, cfkind, cfdate, cfappdate, citno, citcn, citdn, citkind, citdate, refno, refcn, refdn, refkind,
			refdate, xml;

	public void clear() {
		pno = "";
		pncn = "";
		pndno = "";
		pnkind = "";
		pndate = "";
		tilang = "";
		ti = "";
		abslang = "";
		abs = "";
		dockind = "";
		appdate = "";
		appno = "";
		appcn = "";
		appdno = "";
		appkind = "";

		assignee = "";
		inventor = "";

		ipc = "";
		ipce = "";
		ipcs = "";
		ipcc = "";
		ipcsc = "";
		ipcmg = "";
		ipcsg = "";
		ipcqc = "";

		ipcf = "";
		ipcfe = "";
		ipcfs = "";
		ipcfc = "";
		ipcfsc = "";
		ipcfmg = "";
		ipcfsg = "";
		ipcfqc = "";

		ipcr = "";
		ipcrlevel = "";
		ipcrs = "";
		ipcrc = "";
		ipcrsc = "";
		ipcrmg = "";
		ipcrsg = "";
		ipcrcv = "";
		ipcrcn = "";
		ipcrstatus = "";
		ipcrcds = "";

		classncn = "";
		classn = "";
		classc = "";
		classsc = "";
		classnf = "";
		classfc = "";
		classfsc = "";

		prino = "";
		pricn = "";
		pridate = "";
		pridno = "";
		prikind = "";
		numclaims = "";
		citations = "";
		references = "";

		ecla = "";
		eclaschema = "";
		eclacn = "";
		eclas = "";
		eclac = "";
		eclasc = "";
		eclamg = "";
		eclasg = "";

		mf = "";
		mfcn = "";
		mfdno = "";
		mfkind = "";
		mfdate = "";
		mfappdate = "";

		cf = "";
		cfcn = "";
		cfdno = "";
		cfkind = "";
		cfdate = "";
		cfappdate = "";

		citno = "";
		citcn = "";
		citdn = "";
		citkind = "";
		citdate = "";
		refno = "";
		refcn = "";
		refdn = "";
		refkind = "";
		refdate = "";
		
		xml = "";
	}

	public void makePostWorking() {
		this.pno = PatentDataFormat.convertPublicationNumber(this.pncn, this.pndno, this.pnkind);
		this.appno = PatentDataFormat.convertApplicationNumber(this.appcn, this.appdno);
		this.prino = PatentDataFormat.makeMultiPublicationNumber(this.prino, this.pricn, this.pridate, this.pridno, true);
		this.citno = PatentDataFormat.makeMultiPublicationNumber(this.citno, this.citcn, this.citdn, this.citkind, true);
		this.refno = PatentDataFormat.makeMultiPublicationNumber(this.refno, this.refcn, this.refdn, this.refkind, true);
	}
	

	public String toString() {
		makePostWorking();

		StringBuffer sb = new StringBuffer();
		sb.append("\n pno: " + pno);
		sb.append("\n pncn: " + pncn);
		sb.append("\n pndno: " + pndno);
		sb.append("\n pnkind: " + pnkind);
		sb.append("\n pndate: " + pndate);

		sb.append("\n appno: " + appno);
		sb.append("\n appcn: " + appcn);
		sb.append("\n appdno: " + appdno);
		sb.append("\n appkind: " + appkind);
		sb.append("\n appdate: " + appdate);

		sb.append("\n dockind: " + dockind);

		sb.append("\n ti: " + ti);
		sb.append("\n tilang: " + tilang);

		sb.append("\n abs: " + abs);
		sb.append("\n abslang: " + abslang);

		sb.append("\n assignee: " + assignee);
		sb.append("\n inventor: " + inventor);

		sb.append("\n ipc: " + ipc);
		sb.append("\n ipce: " + ipce);
		sb.append("\n ipcs: " + ipcs);
		sb.append("\n ipcc: " + ipcc);
		sb.append("\n ipcsc: " + ipcsc);
		sb.append("\n ipcmg: " + ipcmg);
		sb.append("\n ipcsg: " + ipcsg);
		sb.append("\n ipcqc: " + ipcqc);

		sb.append("\n ipcf: " + ipcf);
		sb.append("\n ipcfe: " + ipcfe);
		sb.append("\n ipcfs: " + ipcfs);
		sb.append("\n ipcfc: " + ipcfc);
		sb.append("\n ipcfsc: " + ipcfsc);
		sb.append("\n ipcfmg: " + ipcfmg);
		sb.append("\n ipcfsg: " + ipcfsg);
		sb.append("\n ipcfqc: " + ipcfqc);

		sb.append("\n ipcr: " + ipcr);
		sb.append("\n ipcrlevel: " + ipcrlevel);
		sb.append("\n ipcrs: " + ipcrs);
		sb.append("\n ipcrc: " + ipcrc);
		sb.append("\n ipcrsc: " + ipcrsc);
		sb.append("\n ipcrmg: " + ipcrmg);
		sb.append("\n ipcrsg: " + ipcrsg);
		sb.append("\n ipcrcv: " + ipcrcv);
		sb.append("\n ipcrcn: " + ipcrcn);
		sb.append("\n ipcrstatus: " + ipcrstatus);
		sb.append("\n ipcrcds: " + ipcrcds);

		sb.append("\n classncn: " + classncn);
		sb.append("\n classn: " + classn);
		sb.append("\n classc: " + classc);
		sb.append("\n classsc: " + classsc);
		sb.append("\n classnf: " + classnf);
		sb.append("\n classfc: " + classfc);
		sb.append("\n classfsc: " + classfsc);

		sb.append("\n ecla: " + ecla);
		sb.append("\n eclaschema: " + eclaschema);
		sb.append("\n eclacn: " + eclacn);
		sb.append("\n eclas: " + eclas);
		sb.append("\n eclac: " + eclac);
		sb.append("\n eclasc: " + eclasc);
		sb.append("\n eclamg: " + eclamg);
		sb.append("\n eclasg: " + eclasg);

		sb.append("\n numclaims: " + numclaims);

		sb.append("\n prino: " + prino);
		sb.append("\n pricn: " + pricn);
		sb.append("\n pridate: " + pridate);
		sb.append("\n pridno: " + pridno);
		sb.append("\n prikind: " + prikind);
		
		sb.append("\n citno: " + citno);
//		sb.append("\n citcn: " + citcn);
//		sb.append("\n citdate: " + citdate);
//		sb.append("\n citdn: " + citdn);
//		sb.append("\n citkind: " + citkind);
		
		sb.append("\n refno: " + refno);
//		sb.append("\n refcn: " + refcn);
//		sb.append("\n refdate: " + refdate);
//		sb.append("\n refdn: " + refdn);
//		sb.append("\n refkind: " + refkind);

		return sb.toString();
	}
}
