package com.aplos.ecommerce.templates.printtemplates;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.annotations.persistence.DiscriminatorValue;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.templates.PrintTemplate;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.common.utils.XmlEntityUtil;
import com.aplos.ecommerce.beans.RealizedProductReturn;

@Entity
@DiscriminatorValue( "ConditionReceived" )
public class ConditionReceivedReportTemplate extends ReturnsTemplate {
	private static final long serialVersionUID = -589775044065422834L;
	
	public ConditionReceivedReportTemplate() {
	}

	public ConditionReceivedReportTemplate( RealizedProductReturn realizedProductReturn ) {
		setRealizedProductReturn(realizedProductReturn);
	}

	@Override
	public String getName() {
		return "Condition Received Report";
	}

	@Override
	public String getTemplateContent() {
		try {
			JDynamiTe jDynamiTe;

			if ((jDynamiTe = CommonUtil.loadContentInfoJDynamiTe( "conditionReceivedReport.html", PrintTemplate.printTemplatePath, ApplicationUtil.getAplosContextListener() )) != null) {
				populateReturnsHeader( jDynamiTe, getRealizedProductReturn(), JSFUtil.getContextPath() );

				jDynamiTe.setVariable("REPORT_DATE", getDateCreatedStdStr() );
				jDynamiTe.setVariable("BOX_PACKAGING_IMAGE", "http://www.myro.com/images/lcdBoxPackaging.png");

				String originalConditionReceivedReportDetails = getRealizedProductReturn().getConditionReceivedReportDetails();

				jDynamiTe.setVariable("CONDITION_RECEIVED_REPORT_DETAILS", XmlEntityUtil.replaceCharactersAndEntitiesWithUnicode(originalConditionReceivedReportDetails));

				String serverUrl = JSFUtil.getServerUrl();
				if ((serverUrl.charAt(serverUrl.length()-1) == '/')) {
					serverUrl = serverUrl.substring(0, serverUrl.length()-1);
				}

				jDynamiTe.setVariable("PHOTO1", serverUrl + getRealizedProductReturn().getFullPhoto1Url(true) + "&amp;maxWidth=280&amp;maxHeight=280");
				jDynamiTe.setVariable("PHOTO2", serverUrl + getRealizedProductReturn().getFullPhoto2Url(true) + "&amp;maxWidth=280&amp;maxHeight=280");
				jDynamiTe.setVariable("PHOTO3", serverUrl + getRealizedProductReturn().getFullPhoto3Url(true) + "&amp;maxWidth=280&amp;maxHeight=280");
				jDynamiTe.setVariable("PHOTO4", serverUrl + getRealizedProductReturn().getFullPhoto4Url(true) + "&amp;maxWidth=280&amp;maxHeight=280");

				jDynamiTe.setVariable("PHOTO1_TEXT", getRealizedProductReturn().getPhoto1Text() );
				jDynamiTe.setVariable("PHOTO2_TEXT", getRealizedProductReturn().getPhoto2Text() );
				jDynamiTe.setVariable("PHOTO3_TEXT", getRealizedProductReturn().getPhoto3Text() );
				jDynamiTe.setVariable("PHOTO4_TEXT", getRealizedProductReturn().getPhoto4Text() );

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
