package com.aplos.ecommerce.templates.emailtemplates;

import java.util.List;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.enums.EmailTemplateEnum;
import com.aplos.ecommerce.beans.RealizedProductReturn;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;
import com.aplos.ecommerce.templates.printtemplates.ReturnsAuthorisationNoteTemplate;

@Entity
public class ReturnsAuthorisationNoteEmail extends ReturnsEmail {
	private static final long serialVersionUID = 2494307734788615306L;

	public ReturnsAuthorisationNoteEmail() {
	}
	
	public String getDefaultName() {
		return "Returns authorisation";
	}
	
	@Override
	public List<FileDetails> getAttachments(RealizedProductReturn realizedProductReturn) {
		List<FileDetails> attachments = super.getAttachments(realizedProductReturn);
		addCreatedPrintTemplate(attachments, realizedProductReturn, new ReturnsAuthorisationNoteTemplate( realizedProductReturn ) );
		return attachments;
	}

	@Override
	public String getDefaultSubject() {
		return "{RETURNS_COMPANY_ID} - {TODAYS_DATE} {CUSTOMER_NAME} {CUSTOMER_COMPANY_NAME} {RETURNS_NUMBER} Returns Authorisation Note";
	}

	@Override
	public String getDefaultContent() {
		return loadBodyFromFile( "returnsAuthorisationNoteEmailBody.html" );
	}

	@Override
	public void addContentJDynamiTeKeys(JDynamiTe jDynamiTe, RealizedProductReturn realizedProductReturn ) {
		jDynamiTe.setVariable("ITEM_CODE", realizedProductReturn.determineReturnProduct().getItemCode() );
	}


	@Override
	public EmailTemplateEnum getEmailTemplateEnum() {
		return EcommerceEmailTemplateEnum.RETURNS_AUTH;
	}
}
