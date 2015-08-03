package com.aplos.ecommerce.templates.emailtemplates;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.CreatedPrintTemplate;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.beans.communication.SourceGeneratedEmailTemplate;
import com.aplos.common.enums.EmailTemplateEnum;
import com.aplos.common.interfaces.BulkEmailSource;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.templates.PrintTemplate;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.XmlEntityUtil;
import com.aplos.ecommerce.beans.CompanyContact;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.EcommerceShoppingCart;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.utils.EcommerceUtil;

@Entity
public abstract class TransactionEmail extends SourceGeneratedEmailTemplate<Transaction> {
	private static final long serialVersionUID = 4469367620127096254L;
	
	public void addCreatedPrintTemplate( List<FileDetails> attachments, Transaction transaction, PrintTemplate printTemplate ) {
		CreatedPrintTemplate createdPrintTemplate = new CreatedPrintTemplate( printTemplate );
		String customerIdStr;
		if( transaction.getCustomer() instanceof CompanyContact ) {
			customerIdStr = String.valueOf( ((CompanyContact) transaction.getCustomer()).getCompany().getId() );
		} else {
			customerIdStr = "IND" + String.valueOf( transaction.getCustomer().getId() );
		}
	
		String fileDetailsName = (customerIdStr + " - " + getId() + " - " + printTemplate.getName()).trim() + " - " + FormatUtil.formatDate( new Date() ) + ".pdf";
		createdPrintTemplate.setName(fileDetailsName);
		attachments.add(createdPrintTemplate);
	}

	public void addContentJDynamiTeKeys( JDynamiTe jDynamiTe, Transaction transaction ) {}

	@Override
	public BulkEmailSource getTestSource(SystemUser adminUser) {
		Transaction transaction = new Transaction();
		transaction.initialiseNewBean();
		EcommerceShoppingCart ecommerceShoppingCart = EcommerceUtil.getEcommerceUtil().createShoppingCart();
		Customer customer = new Customer();
		//TODO need to set an email address without creating new subscribers in the database.
		ecommerceShoppingCart.setCustomer(customer);
		transaction.setEcommerceShoppingCart(ecommerceShoppingCart);
		//TODO: this class is extended, make sure the data required for extensions is set here
		return transaction;
	}

	@Override
	public EmailTemplateEnum getEmailTemplateEnum() {
		return null; //This should be overridden!
	}

	public String postProcessSubject(Transaction transaction,
			String subject) {
		return EcommerceUtil.getEcommerceUtil().postProcessSubject( transaction, subject );
	}
	
	@Override
	public void addSubjectJDynamiTeValues(JDynamiTe jDynamiTe,
			Transaction transaction) {
		super.addSubjectJDynamiTeValues(jDynamiTe, transaction);
		jDynamiTe.setVariable("ORDER_ID", String.valueOf( getId() ));
		jDynamiTe.setVariable("TODAYS_DATE", FormatUtil.formatDate( new Date() ) );
		jDynamiTe.setVariable("CUSTOMER_NAME", transaction.getCustomer().getFullName() );
		jDynamiTe.setVariable("CUSTOMER_REFERENCE", CommonUtil.getStringOrEmpty( transaction.getCustomerReference() ) );
		jDynamiTe.setVariable("TRANSACTION_TYPE_LABEL", transaction.getTransactionType().getLabel());

		if( transaction.getEcommerceShoppingCart().getCustomer() instanceof CompanyContact ) {
			CompanyContact companyContact = (CompanyContact) transaction.getEcommerceShoppingCart().getCustomer();
			jDynamiTe.setVariable("CUSTOMER_COMPANY_NAME", CommonUtil.getStringOrEmpty( companyContact.getCompany().getCompanyName() ) );
			jDynamiTe.setVariable("COMPANY_ID", String.valueOf( companyContact.getCompany().getId() ) );
		} else {
			jDynamiTe.setVariable("CUSTOMER_ID", String.valueOf( transaction.getEcommerceShoppingCart().getCustomer().getId() ) );
		}
		jDynamiTe.setVariable("COMPANY_NAME", CommonUtil.getStringOrEmpty( CommonConfiguration.getCommonConfiguration().getDefaultCompanyDetails().getCompanyName() ) );
	}
	
	@Override
	public void addContentJDynamiTeValues(JDynamiTe jDynamiTe,
			Transaction transaction) {
		super.addContentJDynamiTeValues(jDynamiTe, transaction);
		jDynamiTe.setVariable("CUSTOMER_FIRST_NAME", CommonUtil.getStringOrEmpty( transaction.getCustomer().getSubscriber().getFirstName() ));
		jDynamiTe.setVariable("COMPANY_NAME", CommonUtil.getStringOrEmpty( CommonConfiguration.getCommonConfiguration().getDefaultCompanyDetails().getCompanyName() ));
		SystemUser currentUser = getCurrentUser();
		jDynamiTe.setVariable("CURRENT_USER_FIRST_NAME", CommonUtil.getStringOrEmpty( currentUser.getFirstName() ) );
		addContentJDynamiTeKeys(jDynamiTe, transaction);
	}
}
