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
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.EmailAFriendFormSubmission;
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;

@Entity
public class EmailAFriendEmail extends SourceGeneratedEmailTemplate<EmailAFriendFormSubmission> {
	private static final long serialVersionUID = -3130744790183865233L;

	public EmailAFriendEmail() {
	}
	
	public String getDefaultName() {
		return "Email a Friend";
	}

	@Override
	public String getDefaultSubject() {
		return "Saw this...";
	}

	@Override
	public String getDefaultContent() {
		return loadBodyFromFile( "emailAFriend.html" );
	}

	@Override
	public BulkEmailSource getTestSource(SystemUser adminUser) {
		EmailAFriendFormSubmission submission = new EmailAFriendFormSubmission();
		submission.setMessage("This is a test message.");
		submission.setRecipientEmail(adminUser.getEmail());
		submission.setSender(adminUser.getFullName());
		return submission;
	}

	@Override
	public EmailTemplateEnum getEmailTemplateEnum() {
		return EcommerceEmailTemplateEnum.EMAIL_A_FRIEND;
	}

	@Override
	public void addContentJDynamiTeValues(JDynamiTe jDynamiTe,
			EmailAFriendFormSubmission submission) {
		super.addContentJDynamiTeValues(jDynamiTe, submission);
		CompanyDetails companyDetails = CommonConfiguration.getCommonConfiguration().getDefaultCompanyDetails();

		if (companyDetails.getLogoDetails() != null) {
			jDynamiTe.setVariable("COMPANY_LOGO", companyDetails.getLogoDetails().getFilename() );
		} else {
			jDynamiTe.setVariable("COMPANY_LOGO", "" );
		}
		if (companyDetails.getWeb() != null) {
			jDynamiTe.setVariable("COMPANY_WEBSITE", companyDetails.getWeb() );
		} else {
			jDynamiTe.setVariable("COMPANY_WEBSITE", JSFUtil.getServerUrl() + JSFUtil.getRequest().getContextPath() );
		}
		String url = companyDetails.getWeb()+ "/product/view/" + CommonUtil.makeSafeUrlMapping(((RealizedProduct) JSFUtil.getBeanFromScope(RealizedProduct.class)).getProductInfo().getMapping() ) + ".aplos";
		jDynamiTe.setVariable("LINK", url);
		jDynamiTe.setVariable("SENDERNAME", submission.getSender() );
		jDynamiTe.setVariable("MESSAGE", submission.getMessage() );
	}
}
