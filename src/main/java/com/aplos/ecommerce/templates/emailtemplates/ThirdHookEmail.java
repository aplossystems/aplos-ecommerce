package com.aplos.ecommerce.templates.emailtemplates;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.CompanyDetails;
import com.aplos.common.enums.EmailTemplateEnum;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.ecommerce.beans.EnticementEmailPromotion;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;
import com.aplos.ecommerce.module.EcommerceConfiguration;

@Entity
@DynamicMetaValueKey(oldKey="THIRD_HOOK")
public class ThirdHookEmail extends FirstHookEmail {

	private static final long serialVersionUID = 8441874038963514500L;

	public ThirdHookEmail() {
	}
	
	public String getDefaultName() {
		return "Repeat Custom Incentive (Second Follow Up)";
	}

	@Override
	public String getDefaultSubject() {
		return "Your personal discount will expire soon...";
	}

	@Override
	public String getDefaultContent() {
		return loadBodyFromFile( "repeatCustomIncentiveSecondFollowUp.html" );
	}

	@Override
	public String getDisplayName() {
		return getName();
	}

	@Override
	public void addContentJDynamiTeValues(JDynamiTe jDynamiTe,
			EnticementEmailPromotion enticementEmailPromotion) {
		super.addContentJDynamiTeValues(jDynamiTe, enticementEmailPromotion);
		CompanyDetails companyDetails = CommonConfiguration.getCommonConfiguration().getDefaultCompanyDetails();

		jDynamiTe.setVariable("CUSTOMER_NAME", enticementEmailPromotion.getCustomer().getDisplayName() );
		jDynamiTe.setVariable("DISCOUNT_PERCENTAGE", FormatUtil.formatTwoDigit(enticementEmailPromotion.getPercentage()) );
		jDynamiTe.setVariable("DAYS_ELAPSED", String.valueOf(enticementEmailPromotion.getDaysSinceInitialEmailSent()) );
		jDynamiTe.setVariable("DAYS_REMAINING", String.valueOf(EcommerceConfiguration.getEcommerceSettingsStatic().getEnticementValidForDays() - enticementEmailPromotion.getDaysSinceInitialEmailSent()) );
		jDynamiTe.setVariable("PROMOTIONAL_CODE", enticementEmailPromotion.getCode() );
		jDynamiTe.setVariable("HOMEPAGE_URL", companyDetails.getWeb() );
	}

	@Override
	public EmailTemplateEnum getEmailTemplateEnum() {
		return EcommerceEmailTemplateEnum.THIRD_HOOK;
	}

}
