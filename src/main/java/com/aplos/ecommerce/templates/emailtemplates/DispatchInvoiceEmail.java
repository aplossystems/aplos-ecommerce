package com.aplos.ecommerce.templates.emailtemplates;

import java.util.Date;
import java.util.List;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.enums.EmailTemplateEnum;
import com.aplos.common.utils.CommonUtil;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;
import com.aplos.ecommerce.templates.printtemplates.DispatchInvoiceTemplate;

@Entity
public class DispatchInvoiceEmail extends TransactionEmail {
	private static final long serialVersionUID = 2494307734788615306L;

	public DispatchInvoiceEmail() {
	}
	
	public String getDefaultName() {
		return "Dispatch invoice email";
	}
	
	@Override
	public List<FileDetails> getAttachments(Transaction transaction) {
		List<FileDetails> attachments = super.getAttachments(transaction);
		addCreatedPrintTemplate( attachments, transaction, new DispatchInvoiceTemplate(transaction) );
		return attachments;
	}

	@Override
	public String getDefaultSubject() {
		return "{TODAYS_DATE} {CUSTOMER_NAME} {CUSTOMER_COMPANY_NAME} - {COMPANY_NAME} Invoice {CUSTOMER_REFERENCE}";
	}

	@Override
	public String getDefaultContent() {
		return loadBodyFromFile( "dispatchInvoiceEmailBody.html" );
	}

	@Override
	public void addContentJDynamiTeKeys(JDynamiTe jDynamiTe, Transaction transaction ) {
		jDynamiTe.setVariable("INVOICE_NUMBER", String.valueOf( transaction.getInvoiceNumber() ) );
		jDynamiTe.setVariable("CUSTOMER_REFERENCE", CommonUtil.getStringOrEmpty( transaction.getCustomerReference() ) );

		if( transaction.getFirstInvoicedDate() == null  ) {
			transaction.setFirstInvoicedDate( new Date() );
			transaction.saveDetails();
		}
	}

	@Override
	public EmailTemplateEnum getEmailTemplateEnum() {
		return EcommerceEmailTemplateEnum.DISPATCH_INVOICE;
	}
	
}
