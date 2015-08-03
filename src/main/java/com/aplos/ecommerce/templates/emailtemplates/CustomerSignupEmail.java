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
import com.aplos.common.utils.CommonUtil;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;

@Entity
public class CustomerSignupEmail extends SourceGeneratedEmailTemplate<Customer> {
	private static final long serialVersionUID = -939101865265005129L;

	public CustomerSignupEmail() {
	}
	
	public String getDefaultName() {
		return "Customer Signup";
	}

	@Override
	public String getDefaultSubject() {
		return "Thank you for joining Us";
	}
	@Override
	public String getDefaultContent() {
		return loadBodyFromFile( "customerSignup.html" );
	}


	@Override
	public BulkEmailSource getTestSource(SystemUser adminUser) {
		Customer customer = new Customer();
		//TODO need to set an email address without creating new subscribers in the database.
		customer.setPassword("T3stPassW0rD");
		return customer;
	}

	@Override
	public EmailTemplateEnum getEmailTemplateEnum() {
		return EcommerceEmailTemplateEnum.CUSTOMER_SIGNUP;
	}
	
	@Override
	public void addContentJDynamiTeValues(JDynamiTe jDynamiTe,
			Customer customer) {
		super.addContentJDynamiTeValues(jDynamiTe, customer);
		CompanyDetails companyDetails = CommonConfiguration.getCommonConfiguration().getDefaultCompanyDetails();

		if( companyDetails.getLogoDetails() != null ) {
			jDynamiTe.setVariable("COMPANY_LOGO", CommonUtil.getStringOrEmpty(companyDetails.getLogoDetails().getFilename()) );
		}

		jDynamiTe.setVariable("FIRST_NAME", CommonUtil.getStringOrEmpty(customer.getFirstName()) );
		jDynamiTe.setVariable("COMPANY_WEBSITE", CommonUtil.getStringOrEmpty(companyDetails.getWeb()) );
		jDynamiTe.setVariable("PASSWORD", customer.getPassword() );
	}
}
