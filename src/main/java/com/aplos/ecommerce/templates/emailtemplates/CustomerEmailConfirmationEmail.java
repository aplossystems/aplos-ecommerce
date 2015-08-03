package com.aplos.ecommerce.templates.emailtemplates;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.AplosUrl;
import com.aplos.common.ExternalBackingPageUrl;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.CompanyDetails;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.beans.communication.SourceGeneratedEmailTemplate;
import com.aplos.common.enums.EmailTemplateEnum;
import com.aplos.common.interfaces.BulkEmailSource;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.XmlEntityUtil;
import com.aplos.ecommerce.backingpage.customer.CustomerEmailConfirmationPage;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;

@Entity
public class CustomerEmailConfirmationEmail extends SourceGeneratedEmailTemplate<Customer> {
	private static final long serialVersionUID = 2494307734788615306L;

	public CustomerEmailConfirmationEmail() {
	}
	
	public String getDefaultName() {
		return "Customer confirmation email";
	}

	@Override
	public String getDefaultSubject() {
		return "Please confirm your {COMPANY_NAME} account";
	}

	@Override
	public String getDefaultContent() {
		return loadBodyFromFile( "customerEmailConfirmation.html" );
	}

	@Override
	public BulkEmailSource getTestSource(SystemUser adminUser) {
		Customer custoemr = new Customer();
		//TODO need to set an email address without creating new subscribers in the database.
		return custoemr;
	}

	@Override
	public EmailTemplateEnum getEmailTemplateEnum() {
		return EcommerceEmailTemplateEnum.CUSTOMER_EMAIL_CONFIRMATION;
	}
	
	@Override
	public void addSubjectJDynamiTeValues(JDynamiTe jDynamiTe,
			Customer bulkEmailRecipient) {
		super.addSubjectJDynamiTeValues(jDynamiTe, bulkEmailRecipient);
		CompanyDetails companyDetails = CommonConfiguration.getCommonConfiguration().getDefaultCompanyDetails();
		jDynamiTe.setVariable("COMPANY_NAME", CommonUtil.getStringOrEmpty( companyDetails.getCompanyName() ) );
	}
	
	@Override
	public void addContentJDynamiTeValues(JDynamiTe jDynamiTe,
			Customer customer) {
		super.addContentJDynamiTeValues(jDynamiTe, customer);
		jDynamiTe.setVariable("CUSTOMER_FIRST_NAME", CommonUtil.getStringOrEmpty( customer.getSubscriber().getFirstName() ));
		AplosUrl aplosUrl = new ExternalBackingPageUrl( CustomerEmailConfirmationPage.class );
		aplosUrl.addQueryParameter( "cid", String.valueOf( customer.getId() ) );
		aplosUrl.addQueryParameter( "code", customer.getEmailVerificationCode() );
		jDynamiTe.setVariable("LINK_ADDRESS",  aplosUrl.toString() );
	}
}
