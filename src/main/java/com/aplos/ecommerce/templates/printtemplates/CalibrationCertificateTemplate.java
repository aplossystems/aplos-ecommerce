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
@DiscriminatorValue( "CalibrationCertificate" )
public class CalibrationCertificateTemplate extends ReturnsTemplate {
	private static final long serialVersionUID = 464570265295343729L;
	
	public CalibrationCertificateTemplate() {
	}

	public CalibrationCertificateTemplate( RealizedProductReturn realizedProductReturn ) {
		super(realizedProductReturn);
	}

	@Override
	public String getName() {
		return "Calibration Certificate";
	}

	@Override
	public String getTemplateContent() {
		try {
			JDynamiTe jDynamiTe;

			if ((jDynamiTe = CommonUtil.loadContentInfoJDynamiTe( "calibrationCertificate.html", PrintTemplate.printTemplatePath, ApplicationUtil.getAplosContextListener() )) != null) {
				populateReturnsHeader( jDynamiTe, getRealizedProductReturn(), JSFUtil.getContextPath() );

				jDynamiTe.setVariable("REPORT_DATE", getRealizedProductReturn().getDateCreatedStdStr() );
				jDynamiTe.setVariable("BOX_PACKAGING_IMAGE", "http://www.myro.com/images/lcdBoxPackaging.png");

				String originalCalibratedSpecifications = getRealizedProductReturn().getCalibratedSpecifications();
				jDynamiTe.setVariable("CALIBRATED_SPECIFICATIONS", XmlEntityUtil.replaceCharactersWith(originalCalibratedSpecifications, XmlEntityUtil.EncodingType.ENTITY));


				String originalCalibrationCertificateComments = getRealizedProductReturn().getCalibrationCertificateComments();
				jDynamiTe.setVariable("CALIBRATION_CERTIFICATE_COMMENTS", XmlEntityUtil.replaceCharactersAndEntitiesWithUnicode(originalCalibrationCertificateComments));


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
