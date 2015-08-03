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
import com.aplos.ecommerce.templates.printtemplates.EcommerceInvoiceTemplate;

@Entity
public class EcommerceInvoiceEmail extends TransactionEmail {
	private static final long serialVersionUID = 2494307734788615306L;

	public EcommerceInvoiceEmail() {
	}
	
	public String getDefaultName() {
		return "Ecommerce invoice email";
	}
	
	@Override
	public List<FileDetails> getAttachments(Transaction transaction) {
		List<FileDetails> attachments = super.getAttachments(transaction);
		addCreatedPrintTemplate( attachments, transaction, new EcommerceInvoiceTemplate(transaction) );
		return attachments;
	}

	@Override
	public String getDefaultSubject() {
		return "{TODAYS_DATE} {CUSTOMER_NAME} {CUSTOMER_COMPANY_NAME} - {COMPANY_NAME} Invoice {CUSTOMER_REFERENCE}";
	}

	@Override
	public String getDefaultContent() {
		return loadBodyFromFile( "invoiceEmailBody.html" );
	}

	@Override
	public void addContentJDynamiTeKeys(JDynamiTe jDynamiTe, Transaction transaction ) {
		jDynamiTe.setVariable("ORDER_ID", String.valueOf( transaction.getId() ) );
		jDynamiTe.setVariable("COURIER_NAME", CommonUtil.getStringOrEmpty( transaction.getCourierServiceName() ) );

		if( transaction.getFirstInvoicedDate() == null  ) {
			transaction.setFirstInvoicedDate( new Date() );
			transaction.saveDetails();
		}
	}

	@Override
	public EmailTemplateEnum getEmailTemplateEnum() {
		return EcommerceEmailTemplateEnum.INVOICE;
	}
	
}
