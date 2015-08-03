package com.aplos.ecommerce.templates.emailtemplates;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import cb.jdynamite.JDynamiTe;

import com.aplos.cms.CmsPageUrl;
import com.aplos.common.AplosUrl;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.CompanyDetails;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.beans.communication.SourceGeneratedEmailTemplate;
import com.aplos.common.enums.EmailTemplateEnum;
import com.aplos.common.interfaces.BulkEmailSource;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;
import com.aplos.ecommerce.module.EcommerceConfiguration;

@Entity
public class ForgottenPasswordEmail extends SourceGeneratedEmailTemplate<Customer> {
	private static final long serialVersionUID = -7545905434158031652L;

	public ForgottenPasswordEmail() {
	}
	
	public String getDefaultName() {
		return "Ecommerce forgotten password";
	}

	@Override
	public String getDefaultSubject() {
		return "{COMPANY_NAME} password reset request";
	}

	@Override
	public String getDefaultContent() {
		return loadBodyFromFile( "forgottenPassword.html" );
	}

	@Override
	public BulkEmailSource getTestSource(SystemUser adminUser) {
		Customer customer = new Customer();
		//TODO need to set an email address without creating new subscribers in the database.
		customer.setPasswordResetCode("R35eTc0DE");
		return customer;
	}

	@Override
	public EmailTemplateEnum getEmailTemplateEnum() {
		return EcommerceEmailTemplateEnum.FORGOTTEN_PASSWORD;
	}
	
	@Override
	public void addSubjectJDynamiTeValues(JDynamiTe subjectDynamite,
			Customer bulkEmailRecipient) {
		super.addSubjectJDynamiTeValues(subjectDynamite, bulkEmailRecipient);
		CompanyDetails companyDetails = CommonConfiguration.getCommonConfiguration().getDefaultCompanyDetails();
		String companyName = companyDetails.getCompanyName();
		if (companyName == null) {
			companyName = ApplicationUtil.getAplosContextListener().getImplementationModule().getPackageDisplayName();
		}
		subjectDynamite.setVariable( "COMPANY_NAME", companyName );
	}
	
	@Override
	public void addContentJDynamiTeValues(JDynamiTe jDynamiTe,
			Customer customer) {
		super.addContentJDynamiTeValues(jDynamiTe, customer);
		CompanyDetails companyDetails = CommonConfiguration.getCommonConfiguration().getDefaultCompanyDetails();
		jDynamiTe.setVariable("CUSTOMER_FIRST_NAME", customer.getFullName() );
		String companyName = companyDetails.getCompanyName();
		if (companyName == null) {
			companyName = ApplicationUtil.getAplosContextListener().getImplementationModule().getPackageDisplayName();
		}
		jDynamiTe.setVariable("COMPANY_NAME", companyName );
		if( companyDetails.getLogoDetails() != null ) {
			jDynamiTe.setVariable("COMPANY_LOGO", CommonUtil.getStringOrEmpty(companyDetails.getLogoDetails().getFilename()) );
		}
		jDynamiTe.setVariable("COMPANY_EMAIL", CommonUtil.getStringOrEmpty(companyDetails.getAddress().getEmailAddress()) );
		jDynamiTe.setVariable("COMPANY_PHONE", CommonUtil.getStringOrEmpty(companyDetails.getAddress().getPhone()) );
		AplosUrl aplosUrl = CommonUtil.getExternalPageUrl( new CmsPageUrl( EcommerceConfiguration.getEcommerceCprsStatic().getCheckoutPasswordResetCpr().getCmsPage() ) );
		aplosUrl.addQueryParameter( "passwordResetCode", customer.getPasswordResetCode() );
		jDynamiTe.setVariable("RESET_PASSWORD_URL", aplosUrl.toString() );
		jDynamiTe.setVariable("RESET_DATE", FormatUtil.formatDate( customer.getPasswordResetDate() ) );
	}
}
