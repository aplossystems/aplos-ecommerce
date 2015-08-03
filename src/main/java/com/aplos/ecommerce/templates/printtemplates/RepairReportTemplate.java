package com.aplos.ecommerce.templates.printtemplates;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.templates.PrintTemplate;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.common.utils.XmlEntityUtil;
import com.aplos.ecommerce.beans.RealizedProductReturn;

@Entity
public class RepairReportTemplate extends ReturnsTemplate {
	private static final long serialVersionUID = 1151652589489971606L;
	
	public RepairReportTemplate() {
	}

	public RepairReportTemplate( RealizedProductReturn realizedProductReturn ) {
		super(realizedProductReturn);
	}

	@Override
	public String getName() {
		return "Repair Report";
	}

	@Override
	public String getTemplateContent() {
		try {
			JDynamiTe jDynamiTe;

			if ((jDynamiTe = CommonUtil.loadContentInfoJDynamiTe( "repairReport.html", PrintTemplate.printTemplatePath, ApplicationUtil.getAplosContextListener() )) != null) {
				populateReturnsHeader( jDynamiTe, getRealizedProductReturn(), JSFUtil.getContextPath() );

				jDynamiTe.setVariable("REPORT_DATE", getRealizedProductReturn().getDateCreatedStdStr() );
				jDynamiTe.setVariable("BOX_PACKAGING_IMAGE", "http://www.myro.com/images/lcdBoxPackaging.png");

				String originalRepairReportDetails = getRealizedProductReturn().getRepairReportDetails();

				jDynamiTe.setVariable("REPAIR_REPORT_DETAILS", XmlEntityUtil.replaceCharactersAndEntitiesWithUnicode(originalRepairReportDetails));

				String serverUrl = JSFUtil.getServerUrl();
				if ((serverUrl.charAt(serverUrl.length()-1) == '/')) {
					serverUrl = serverUrl.substring(0, serverUrl.length()-1);
				}

				jDynamiTe.parse();

				try {
					return jDynamiTe.toString();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch( Exception e ) {
			e.printStackTrace();
		}
		return null;
	}

}
