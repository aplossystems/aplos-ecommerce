package com.aplos.ecommerce.templates.emailtemplates;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.CompanyDetails;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.enums.EmailTemplateEnum;
import com.aplos.common.interfaces.BulkEmailSource;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.EcommerceShoppingCart;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;
import com.aplos.ecommerce.enums.TransactionType;
import com.aplos.ecommerce.module.EcommerceConfiguration;
import com.aplos.ecommerce.templates.printtemplates.TransactionAcknowledgementTemplate;
import com.aplos.ecommerce.utils.EcommerceUtil;

@Entity
public class PdfConfirmPaymentEmail extends TransactionEmail {
	private static final long serialVersionUID = -7604396324285654820L;


	public PdfConfirmPaymentEmail() {
	}
	
	public String getDefaultName() {
		return "Pdf Confirm Payment";
	}

	@Override
	public String getDefaultSubject() {
		return "Your {COMPANY_NAME} order no.{ORDER_NUMBER}";
	}
	
	@Override
	public List<FileDetails> getAttachments(Transaction transaction) {
		List<FileDetails> attachments = super.getAttachments(transaction);
		addCreatedPrintTemplate( attachments, transaction, new TransactionAcknowledgementTemplate(transaction) );
		return attachments;
	}

	@Override
	public String getDefaultContent() {
		return loadBodyFromFile( "pdfConfirmPayment.html" );
	}

	@Override
	public BulkEmailSource getTestSource(SystemUser adminUser) {
		Transaction transaction = new Transaction();
		transaction.initialiseNewBean();
		EcommerceShoppingCart ecommerceShoppingCart = EcommerceUtil.getEcommerceUtil().createShoppingCart();
		Customer customer = new Customer();
		//TODO need to set an email address without creating new subscribers in the database.
		ecommerceShoppingCart.setCustomer(customer);
		transaction.setEcommerceShoppingCart(ecommerceShoppingCart);
		return transaction;
	}

	@Override
	public EmailTemplateEnum getEmailTemplateEnum() {
		return EcommerceEmailTemplateEnum.PDF_CONFIRM_PAYMENT;
	}
	
	@Override
	public void addSubjectJDynamiTeValues(JDynamiTe jDynamiTe,
			Transaction transaction) {
		super.addSubjectJDynamiTeValues(jDynamiTe, transaction);
		jDynamiTe.setVariable( "ORDER_NUMBER", transaction.getId().toString() );
	}
	
	@Override
	public void addContentJDynamiTeValues(JDynamiTe jDynamiTe,
			Transaction transaction) {
		super.addContentJDynamiTeValues(jDynamiTe, transaction);
		EcommerceShoppingCart ecommerceShoppingCart = transaction.getEcommerceShoppingCart();

		jDynamiTe.setVariable("CUSTOMER_FIRST_NAME", ecommerceShoppingCart.getCustomer().getDisplayName() );
	}
	
	@Override
	public boolean isSendToAdminAlsoByDefault(Transaction transaction) {
		if( TransactionType.ECOMMERCE_ORDER.equals( transaction.getTransactionType() ) ) {
        	if( EcommerceConfiguration.getEcommerceSettingsStatic().isEmailAlertAdminOnFrontendOrder() ) {
        		return true;
        	}
        }
		return false;
	}
}
