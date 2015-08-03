package com.aplos.ecommerce.templates.emailtemplates;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.beans.communication.SourceGeneratedEmailTemplate;
import com.aplos.common.enums.EmailTemplateEnum;
import com.aplos.common.interfaces.BulkEmailSource;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.XmlEntityUtil;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.EcommerceShoppingCart;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;
import com.aplos.ecommerce.utils.EcommerceUtil;

@Entity
public class AbandonedTransactionEmail extends SourceGeneratedEmailTemplate<Transaction> {
	
	private static final long serialVersionUID = 3478091281171149101L;

	public AbandonedTransactionEmail() {
		super();
	}
	
	@Override
	public String getDefaultName() {
		return "Abandoned transaction";
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
		//TODO: this class is extended, make sure the data required for extensions is set here
		return transaction;
	}

	@Override
	public EmailTemplateEnum getEmailTemplateEnum() {
		return EcommerceEmailTemplateEnum.ABANDONED_TRANSACTION; 
	}
	
	@Override
	public String getDefaultSubject() {
		return "Order Abandoned - {ABANDONMENT_DATETIME} - {CUSTOMER_NAME} {TRANSACTION_VALUE}";
	}

	@Override
	public String getDefaultContent() {
		return loadBodyFromFile( "abandonedTransactionEmailBody.html" );
	}
	
	@Override
	public void addSubjectJDynamiTeValues(JDynamiTe jDynamiTe,
			Transaction transaction) {
		super.addSubjectJDynamiTeValues(jDynamiTe, transaction);
		jDynamiTe.setVariable("TRANSACTION_VALUE", transaction.getGrandTotal(false).toPlainString());
		jDynamiTe.setVariable("ABANDONMENT_DATETIME", FormatUtil.formatDateTime(transaction.getDateLastModified(), true));
		jDynamiTe.setVariable("CUSTOMER_NAME", CommonUtil.getStringOrEmpty(transaction.getCustomer().getFullName()));
	}
	
	@Override
	public void addContentJDynamiTeValues(JDynamiTe jDynamiTe,
			Transaction transaction) {
		super.addContentJDynamiTeValues(jDynamiTe, transaction);
		jDynamiTe.setVariable("ADMIN_NAME", CommonUtil.getStringOrEmpty( CommonUtil.getAdminUser().getFirstName() ));
		jDynamiTe.setVariable("TRANSACTION_VALUE", transaction.getGrandTotal(false).toPlainString());
		jDynamiTe.setVariable("ABANDONMENT_DATETIME", FormatUtil.formatDateTime(transaction.getDateLastModified(), true));
		jDynamiTe.setVariable("CUSTOMER_NAME", CommonUtil.getStringOrEmpty(transaction.getCustomer().getFullName()));
	}
}
