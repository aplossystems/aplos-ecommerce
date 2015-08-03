package com.aplos.ecommerce.templates.emailtemplates;

import java.util.List;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.enums.EmailTemplateEnum;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;
import com.aplos.ecommerce.templates.printtemplates.LoanFormTemplate;

@Entity
public class LoanEmail extends TransactionEmail {
	private static final long serialVersionUID = 2494307734788615306L;

	public LoanEmail() {
	}
	
	public String getDefaultName() {
		return "Loan email";
	}
	
	@Override
	public List<FileDetails> getAttachments(Transaction transaction) {
		List<FileDetails> attachments = super.getAttachments(transaction);
		addCreatedPrintTemplate( attachments, transaction, new LoanFormTemplate(transaction) );
		return attachments;
	}

	@Override
	public String getDefaultSubject() {
		return "{TODAYS_DATE} {CUSTOMER_NAME} {CUSTOMER_COMPANY_NAME} - {COMPANY_NAME} {TRANSACTION_TYPE_LABEL} {CUSTOMER_REFERENCE}";
	}

	@Override
	public String getDefaultContent() {
		return loadBodyFromFile( "loanEmailBody.html" );
	}

	@Override
	public void addContentJDynamiTeKeys(JDynamiTe jDynamiTe, Transaction transaction ) {
	}

	@Override
	public EmailTemplateEnum getEmailTemplateEnum() {
		return EcommerceEmailTemplateEnum.LOAN;
	}
	
	@Override
	public boolean isSendToAdminAlsoByDefault(Transaction bulkEmailRecipient) {
		return true;
	}
	
}
