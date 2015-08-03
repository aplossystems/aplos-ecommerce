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
@DynamicMetaValueKey(oldKey="SECOND_HOOK")
public class SecondHookEmail extends FirstHookEmail {

	private static final long serialVersionUID = 3194333240600267269L;

	public SecondHookEmail() {
	}
	
	public String getDefaultName() {
		return "Repeat Custom Incentive (First Follow Up)";
	}

	@Override
	public String getDefaultSubject() {
		return "Your personal discount is still waiting...";
	}

	@Override
	public String getDefaultContent() {
		return loadBodyFromFile( "repeatCustomIncentiveFirstFollowUp.html" );
	}

	@Override
	public EmailTemplateEnum getEmailTemplateEnum() {
		return EcommerceEmailTemplateEnum.SECOND_HOOK;
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

}
