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
@DiscriminatorValue( "ReturnsAuthorisation" )
public class ReturnsAuthorisationNoteTemplate extends ReturnsTemplate {
	private static final long serialVersionUID = -5738217501488441476L;
	
	public ReturnsAuthorisationNoteTemplate() {
	}
	
	public ReturnsAuthorisationNoteTemplate(RealizedProductReturn realizedProductReturn) {
		super(realizedProductReturn);
	}

	@Override
	public String getName() {
		return "Returns Authorisation Note";
	}

	@Override
	public String getTemplateContent() {
		try {
			JDynamiTe jDynamiTe;

			if ((jDynamiTe = CommonUtil.loadContentInfoJDynamiTe( "returnsAuthorisationNote.html", PrintTemplate.printTemplatePath, ApplicationUtil.getAplosContextListener() )) != null) {
				populateReturnsHeader( jDynamiTe, getRealizedProductReturn(), JSFUtil.getContextPath() );
//				dynamiTe.setVariable("TELETEST_LOGO", aplosContextListener.getContext().getContextPath() + "/images/teletest_logo.png");

				jDynamiTe.setVariable("PURCHASED_BY", ""); // 'proactive'?
				jDynamiTe.setVariable("REPORT_DATE", getRealizedProductReturn().getDateCreatedStdStr() );

//				dynamiTe.setVariable("BOX_PACKAGING_IMAGE", aplosContextListener.getContext().getContextPath() + "/images/lcdBoxPackaging.png");
				jDynamiTe.setVariable("BOX_PACKAGING_IMAGE", "http://www.teletest.tv/images/packingDiagram1.jpg");

				String returnsAuthorisationNoteDetails = getRealizedProductReturn().getReturnsAuthorisationNoteDetails();

				returnsAuthorisationNoteDetails += "<p>" + CommonUtil.getStringOrEmpty( getRealizedProductReturn().determineReturnsAuthorisationNoteAppendage() ) + "</p>";
				jDynamiTe.setVariable("RETURNS_AUTHORISATION_NOTE_DETAILS", XmlEntityUtil.replaceCharactersAndEntitiesWithUnicode(returnsAuthorisationNoteDetails));

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
