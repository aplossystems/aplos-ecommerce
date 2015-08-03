package com.aplos.ecommerce.templates.emailtemplates;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.beans.communication.EmailTemplate;
import com.aplos.common.beans.communication.SingleEmailRecord;
import com.aplos.common.enums.EmailTemplateEnum;
import com.aplos.ecommerce.beans.InfoPackRequest;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;

@Entity
public class InfoPackAdminEmail extends EmailTemplate<SystemUser,InfoPackRequest> {
	private static final long serialVersionUID = -939101865265005129L;

	public InfoPackAdminEmail() {
	}
	
	public String getDefaultName() {
		return "Info pack admin";
	}

	@Override
	public String getDefaultSubject() {
		return "{INFO_PACK_NAME} Auto Info Pack downloaded";
	}
	@Override
	public String getDefaultContent() {
		return loadBodyFromFile( "infoPackAdmin.html" );
	}

	@Override
	public EmailTemplateEnum getEmailTemplateEnum() {
		return EcommerceEmailTemplateEnum.INFO_PACK_ADMIN;
	}
	
	@Override
	public void addSubjectJDynamiTeValues(JDynamiTe jDynamiTe,
			SystemUser bulkEmailRecipient, InfoPackRequest emailGenerator) {
		super.addSubjectJDynamiTeValues(jDynamiTe, bulkEmailRecipient, emailGenerator);
		jDynamiTe.setVariable( "INFO_PACK_NAME", emailGenerator.getNewsEntry().getPdfTitle() );
	}
	
	@Override
	public void addContentJDynamiTeValues(JDynamiTe jDynamiTe,
			SystemUser bulkEmailRecipient, InfoPackRequest infoPackRequest, SingleEmailRecord singleEmailRecord) {
		super.addContentJDynamiTeValues(jDynamiTe, bulkEmailRecipient, infoPackRequest, singleEmailRecord);
		jDynamiTe.setVariable( "INFO_PACK_NAME", infoPackRequest.getNewsEntry().getPdfTitle() );
		jDynamiTe.setVariable( "FIRST_NAME", infoPackRequest.getFirstName() );
		jDynamiTe.setVariable( "SURNAME", infoPackRequest.getSurname() );
		jDynamiTe.setVariable( "EMAIL", infoPackRequest.getEmailAddress() );
	}
}
