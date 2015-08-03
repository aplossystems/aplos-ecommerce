package com.aplos.ecommerce.templates.emailtemplates;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.enums.EmailTemplateEnum;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;
import com.aplos.ecommerce.module.EcommerceConfiguration;

@Entity
public class AwaitingAuthorisationEmail extends TransactionEmail {
	private static final long serialVersionUID = 2494307734788615306L;

	public AwaitingAuthorisationEmail() {
	}
	
	public String getDefaultName() {
		return "Awaiting Authorisation email";
	}

	@Override
	public String getDefaultSubject() {
		return "{TODAYS_DATE} {CUSTOMER_NAME} {CUSTOMER_COMPANY_NAME} - {COMPANY_NAME} {TRANSACTION_TYPE_LABEL} {CUSTOMER_REFERENCE}";
	}

	@Override
	public String getDefaultContent() {
		return loadBodyFromFile( "awaitingAuthorisation.html" );
	}

	@Override
	public EmailTemplateEnum getEmailTemplateEnum() {
		return EcommerceEmailTemplateEnum.AWAITING_AUTHORISATION;
	}
	
	@Override
	public boolean isSendToAdminAlsoByDefault( Transaction transaction ) {
        if( transaction.getUserIdCreated() == -1 ) {
        	if( EcommerceConfiguration.getEcommerceSettingsStatic().isEmailAlertAdminOnFrontendOrder() ) {
        		return true;
        	}
        }
        return false;
	}

}
