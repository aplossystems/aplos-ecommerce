package com.aplos.ecommerce.templates.emailtemplates;

import java.util.List;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.CreatedPrintTemplate;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.enums.EmailTemplateEnum;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;
import com.aplos.ecommerce.templates.printtemplates.QuoteTemplate;

@Entity
public class QuoteEmail extends TransactionEmail {
	private static final long serialVersionUID = 2494307734788615306L;

	public QuoteEmail() {
	}
	
	public String getDefaultName() {
		return "Quote email";
	}
	
	@Override
	public List<FileDetails> getAttachments(Transaction transaction) {
		List<FileDetails> attachments = super.getAttachments(transaction);
		attachments.add( new CreatedPrintTemplate( new QuoteTemplate(transaction)));
		return attachments;
	}

	@Override
	public String getDefaultSubject() {
		return "{TODAYS_DATE} {CUSTOMER_NAME} {CUSTOMER_COMPANY_NAME} - {COMPANY_NAME} {TRANSACTION_TYPE_LABEL} {CUSTOMER_REFERENCE}";
	}

	@Override
	public String getDefaultContent() {
		return loadBodyFromFile( "quoteEmailBody.html" );
	}

	@Override
	public void addContentJDynamiTeKeys(JDynamiTe jDynamiTe, Transaction transaction ) {
	}

	@Override
	public EmailTemplateEnum getEmailTemplateEnum() {
		return EcommerceEmailTemplateEnum.QUOTE;
	}
	
	@Override
	public boolean isSendToAdminAlsoByDefault(Transaction bulkEmailRecipient) {
		return true;
	}
	
}
