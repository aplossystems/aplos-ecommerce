package com.aplos.ecommerce.templates.emailtemplates;

import java.io.IOException;
import java.util.List;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.enums.EmailTemplateEnum;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;
import com.aplos.ecommerce.templates.printtemplates.DispatchNoteTemplate;

@Entity
public class DispatchNoteEmail extends TransactionEmail {
	private static final long serialVersionUID = -2115594135655065177L;

	public DispatchNoteEmail() {
	}
	
	public String getDefaultName() {
		return "Dispatch note";
	}
	
	@Override
	public List<FileDetails> getAttachments(Transaction transaction) {
		List<FileDetails> attachments = super.getAttachments(transaction);
		addCreatedPrintTemplate( attachments, transaction, new DispatchNoteTemplate(transaction) );
		return attachments;
	}

	@Override
	public String getDefaultSubject() {
		return "Dipatch note";
	}

	@Override
	public String getDefaultContent() {
		return loadBodyFromFile( "dispatchNote.html" );
	}

	@Override
	public EmailTemplateEnum getEmailTemplateEnum() {
		return EcommerceEmailTemplateEnum.DISPATCH_NOTIFICATION;
	}
}
