package com.aplos.ecommerce.templates.emailtemplates;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.CompanyDetails;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.enums.EmailTemplateEnum;
import com.aplos.common.interfaces.BulkEmailSource;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.EnticementEmailPromotion;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;
import com.aplos.ecommerce.module.EcommerceConfiguration;

@Entity
@DynamicMetaValueKey(oldKey="FIRST_HOOK")
public class FirstHookEmail extends EnticementEmailTemplate {

	private static final long serialVersionUID = 6911079707175380449L;

	public FirstHookEmail() {
	}
	
	public String getDefaultName() {
		return "Repeat Custom Incentive (Initial Email)";
	}

	@Override
	public String getDefaultSubject() {
		return "A discount just for you...";
	}

	@Override
	public String getDefaultContent() {
		return loadBodyFromFile( "repeatCustomIncentiveInitial.html" );
	}

	@Override
	public String getDisplayName() {
		return getName();
	}

	@Override
	public BulkEmailSource getTestSource(SystemUser adminUser) {
		EnticementEmailPromotion source = new EnticementEmailPromotion();
		source.setCode("T35TC0DE");
		Customer customer = new Customer();
		//TODO need to set an email address without creating new subscribers in the database.
		source.setCustomer(customer);
		source.setExpires(new Date());
		source.setInitialPurchaseDate(new Date());
		source.setDaysSinceInitialEmailSent(0l);
		source.setPercentage(new BigDecimal(5));
		source.setName("Test Source Enticement");
		return source;
	}

	@Override
	public EmailTemplateEnum getEmailTemplateEnum() {
		return EcommerceEmailTemplateEnum.FIRST_HOOK;
	}
	
	@Override
	public void addContentJDynamiTeValues(JDynamiTe jDynamiTe,
			EnticementEmailPromotion enticementEmailPromotion) {
		super.addContentJDynamiTeValues(jDynamiTe, enticementEmailPromotion);
		CompanyDetails companyDetails = CommonConfiguration.getCommonConfiguration().getDefaultCompanyDetails();

		jDynamiTe.setVariable("CUSTOMER_NAME", enticementEmailPromotion.getCustomer().getDisplayName() );
		String companyName = companyDetails.getCompanyName();
		if (companyName == null) {
			companyName = ApplicationUtil.getAplosContextListener().getImplementationModule().getPackageDisplayName();
		}
		jDynamiTe.setVariable("COMPANY_NAME", companyName );
		jDynamiTe.setVariable("DISCOUNT_PERCENTAGE", FormatUtil.formatTwoDigit(enticementEmailPromotion.getPercentage()) );
		jDynamiTe.setVariable("TIME_LIMIT_DAYS", String.valueOf(EcommerceConfiguration.getEcommerceSettingsStatic().getEnticementValidForDays()) );
		jDynamiTe.setVariable("PROMOTIONAL_CODE", enticementEmailPromotion.getCode() );
		jDynamiTe.setVariable("HOMEPAGE_URL", companyDetails.getWeb() );

		jDynamiTe.setVariable("CUSTOMER_NAME", enticementEmailPromotion.getCustomer().getDisplayName() );
		jDynamiTe.setVariable("DISCOUNT_PERCENTAGE", FormatUtil.formatTwoDigit(enticementEmailPromotion.getPercentage()) );
		jDynamiTe.setVariable("TIME_LIMIT_DAYS", String.valueOf(EcommerceConfiguration.getEcommerceSettingsStatic().getEnticementValidForDays()) );
		jDynamiTe.setVariable("PROMOTIONAL_CODE", enticementEmailPromotion.getCode() );
		jDynamiTe.setVariable("HOMEPAGE_URL", companyDetails.getWeb() );

		if (enticementEmailPromotion.getCustomer() != null) {
			jDynamiTe.setVariable("TIED_TO_ACCOUNT_OR_SHARE", "This is an exclusive promotional code and can only be used by you." );
		} else {
			jDynamiTe.setVariable("TIED_TO_ACCOUNT_OR_SHARE", "Could your friends might benefit from this promotion? please forward this code onto them." );
		}
	}
}
