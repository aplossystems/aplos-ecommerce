package com.aplos.ecommerce.templates.emailtemplates;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.CompanyDetails;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.beans.communication.SourceGeneratedEmailTemplate;
import com.aplos.common.enums.EmailTemplateEnum;
import com.aplos.common.interfaces.BulkEmailSource;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;

@Entity
public class ResetPasswordEmail extends SourceGeneratedEmailTemplate<Customer> {
	private static final long serialVersionUID = 2494307734788615306L;

	public ResetPasswordEmail() {
	}
	
	public String getDefaultName() {
		return "Reset Password";
	}

	@Override
	public String getDefaultSubject() {
		return "Your New {COMPANY_NAME} Password";
	}

	@Override
	public String getDefaultContent() {
		return loadBodyFromFile( "resetPassword.html" );
	}

	@Override
	public BulkEmailSource getTestSource(SystemUser adminUser) {
		Customer customer = new Customer();
		//TODO need to set an email address without creating new subscribers in the database.
		customer.setPassword("R35eTPWD");
		return customer;
	}

	@Override
	public EmailTemplateEnum getEmailTemplateEnum() {
		return EcommerceEmailTemplateEnum.RESET_PASSWORD;
	}
	
	@Override
	public void addContentJDynamiTeValues(JDynamiTe jDynamiTe,
			Customer customer) {
		super.addContentJDynamiTeValues(jDynamiTe, customer);
		jDynamiTe.setVariable("CUSTOMER_FIRST_NAME", customer.getFullName() );
		CompanyDetails companyDetails = CommonConfiguration.getCommonConfiguration().getDefaultCompanyDetails();

		if( companyDetails.getLogoDetails() != null ) {
			jDynamiTe.setVariable("COMPANY_LOGO", companyDetails.getLogoDetails().getFilename() );
		}
		jDynamiTe.setVariable("COMPANY_WEBSITE", companyDetails.getWeb() );
		jDynamiTe.setVariable("PASSWORD", customer.getPassword() );
	}
}
