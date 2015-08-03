package com.aplos.ecommerce.templates.emailtemplates;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.enums.EmailTemplateEnum;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;

@Entity
public class CancellationNoticeEmail extends TransactionEmail {
	private static final long serialVersionUID = 2494307734788615306L;

	public CancellationNoticeEmail() {
	}
	
	public String getDefaultName() {
		return "Cancellation notice email";
	}

	@Override
	public String getDefaultSubject() {
		return "{TODAYS_DATE} {CUSTOMER_NAME} {CUSTOMER_COMPANY_NAME} - {COMPANY_NAME} {TRANSACTION_TYPE_LABEL} {CUSTOMER_REFERENCE}";
	}

	@Override
	public String getDefaultContent() {
		return loadBodyFromFile( "cancellationNotice.html" );
	}

	@Override
	public EmailTemplateEnum getEmailTemplateEnum() {
		return EcommerceEmailTemplateEnum.CANCELLATION_NOTICE;
	}

	@Override
	public void addContentJDynamiTeKeys(JDynamiTe jDynamiTe,
			Transaction transaction) {
		super.addContentJDynamiTeKeys(jDynamiTe, transaction);
		jDynamiTe.setVariable("ORDER_ID", String.valueOf( getId() ));
		jDynamiTe.setVariable("CUSTOMER_REFERENCE", CommonUtil.getStringOrEmpty( transaction.getCustomerReference() ) );
		jDynamiTe.setVariable("GRAND_TOTAL", FormatUtil.formatTwoDigit( transaction.getGrandTotal( true ) ) );
	}
	
	@Override
	public boolean isSendToAdminAlsoByDefault(Transaction bulkEmailRecipient) {
		return true;
	}

}
