package com.aplos.ecommerce.templates.emailtemplates;

import java.util.List;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.enums.EmailTemplateEnum;
import com.aplos.common.utils.CommonUtil;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;
import com.aplos.ecommerce.templates.printtemplates.TransactionAcknowledgementTemplate;

@Entity
public class AcknowledgementEmail extends TransactionEmail {
	private static final long serialVersionUID = 2494307734788615306L;

	public AcknowledgementEmail() {
	}
	
	public String getDefaultName() {
		return "Acknowledgment email";
	}
	
	@Override
	public List<FileDetails> getAttachments(Transaction transaction) {
		List<FileDetails> attachments = super.getAttachments(transaction);
		addCreatedPrintTemplate( attachments, transaction, new TransactionAcknowledgementTemplate(transaction) );
		return attachments;
	}

	@Override
	public String getDefaultSubject() {
		return "{TODAYS_DATE} {CUSTOMER_NAME} {CUSTOMER_COMPANY_NAME} - {COMPANY_NAME} Acknowledgement {CUSTOMER_REFERENCE}";
	}

	@Override
	public String getDefaultContent() {
		return loadBodyFromFile( "acknowledgementEmailBody.html" );
	}

	@Override
	public void addContentJDynamiTeKeys(JDynamiTe jDynamiTe, Transaction transaction ) {
		jDynamiTe.setVariable("ORDER_ID", String.valueOf( transaction.getId() ) );
		jDynamiTe.setVariable("CUSTOMER_REFERENCE", CommonUtil.getStringOrEmpty( transaction.getCustomerReference() ));
		jDynamiTe.setVariable("DISPATCH_DATE", CommonUtil.getStringOrEmpty( transaction.getDispatchDateStr() ));
		jDynamiTe.setVariable("COURIER_NAME", CommonUtil.getStringOrEmpty( transaction.getCourierServiceName() ));
	}

	@Override
	public EmailTemplateEnum getEmailTemplateEnum() {
		return EcommerceEmailTemplateEnum.ACKNOWLEDGEMENT;
	}
	
}
