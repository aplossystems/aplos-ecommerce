package com.aplos.ecommerce.templates.emailtemplates;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.CompanyDetails;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.beans.communication.SourceGeneratedEmailTemplate;
import com.aplos.common.enums.EmailTemplateEnum;
import com.aplos.common.interfaces.BulkEmailSource;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.EcommerceShoppingCart;
import com.aplos.ecommerce.beans.EcommerceShoppingCartItem;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;
import com.aplos.ecommerce.enums.TransactionType;
import com.aplos.ecommerce.module.EcommerceConfiguration;
import com.aplos.ecommerce.utils.EcommerceUtil;

@Entity
public class ConfirmPaymentEmail extends SourceGeneratedEmailTemplate<Transaction> {
	private static final long serialVersionUID = 8643039154199484799L;

	public ConfirmPaymentEmail() {
	}
	
	public String getDefaultName() {
		return "Confirm Payment";
	}

	@Override
	public String getDefaultSubject() {
		return "Your {COMPANY_NAME} order no.{ORDER_NUMBER}";
	}

	@Override
	public String getDefaultContent() {
		return loadBodyFromFile( "confirmPayment.html" );
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
		return EcommerceEmailTemplateEnum.CONFIRM_PAYMENT;
	}
	
	@Override
	public void addSubjectJDynamiTeValues(JDynamiTe jDynamiTe,
			Transaction transaction) {
		super.addSubjectJDynamiTeValues(jDynamiTe, transaction);
		CompanyDetails companyDetails = CommonConfiguration.getCommonConfiguration().getDefaultCompanyDetails();
		String companyName = companyDetails.getCompanyName();
		if (companyName == null) {
			companyName = ApplicationUtil.getAplosContextListener().getImplementationModule().getPackageDisplayName();
		}
		jDynamiTe.setVariable( "COMPANY_NAME", companyName );
		jDynamiTe.setVariable( "ORDER_NUMBER", transaction.getId().toString() );
	}
	
	@Override
	public void addContentJDynamiTeValues(JDynamiTe jDynamiTe,
			Transaction bulkEmailRecipient) {
		super.addContentJDynamiTeValues(jDynamiTe, bulkEmailRecipient);
		addContentDynamicKeys(jDynamiTe, bulkEmailRecipient);
	}
	
	public void addContentDynamicKeys(JDynamiTe contentDynamiTe, BulkEmailSource bulkEmailRecipient) {
		Transaction transaction = (Transaction) bulkEmailRecipient;
		EcommerceShoppingCart ecommerceShoppingCart = transaction.getEcommerceShoppingCart();
		CompanyDetails companyDetails = CommonConfiguration.getCommonConfiguration().getDefaultCompanyDetails();
		String prefixOrSuffix = ecommerceShoppingCart.getCurrency().getPrefixOrSuffix();
		String companyName = companyDetails.getCompanyName();
		if (companyName == null) {
			companyName = ApplicationUtil.getAplosContextListener().getImplementationModule().getPackageDisplayName();
		}
		contentDynamiTe.setVariable("COMPANY_NAME", companyName );
		contentDynamiTe.setVariable("VAT_NUMBER", CommonUtil.getStringOrEmpty( companyDetails.getVatNo() ) );
		if( companyDetails.getLogoDetails() != null ) {
			contentDynamiTe.setVariable("COMPANY_LOGO", CommonUtil.getStringOrEmpty( companyDetails.getLogoDetails().getFilename() ) );
		}
		contentDynamiTe.setVariable("COMPANY_WEBSITE", CommonUtil.getStringOrEmpty(companyDetails.getWeb()) );
		contentDynamiTe.setVariable("PAYMENT_METHOD", transaction.getPaymentMethod().getName() );
		if( transaction.getIsAcknowledgedOrFurther() ) {
			contentDynamiTe.setVariable("PAYMENT_STATUS", "Successful" );
		} else {
			contentDynamiTe.setVariable("PAYMENT_STATUS", "Awaiting confirmation" );
		}
		contentDynamiTe.setVariable("ORDER_NUMBER", transaction.getId().toString() );
		contentDynamiTe.setVariable("CUSTOMER_FULLNAME", ecommerceShoppingCart.getCustomer().getDisplayName() );
		contentDynamiTe.setVariable("PRODUCT_TOTAL_PRICE_DOUBLE", ecommerceShoppingCart.getCachedNetTotalAmountString() );
		contentDynamiTe.setVariable("ORDER_TOTAL_PRICE_DOUBLE", ecommerceShoppingCart.getGrandTotalString(true) );
		contentDynamiTe.setVariable("ORDER_DATETIME", FormatUtil.formatDateTime( transaction.getDateLastModified(), true ) );
		contentDynamiTe.setVariable("CURRENCY_SYMBOL", ecommerceShoppingCart.getCurrency().getSymbol() );
		contentDynamiTe.setVariable("CURRENCY_PREFIX_SUFFIX", CommonUtil.getUnicodeEntityStr( prefixOrSuffix ) );
		String shippingMethodName = "";
		if( ecommerceShoppingCart.getAvailableShippingService() != null ) {
			shippingMethodName = ecommerceShoppingCart.getAvailableShippingService().getCachedServiceName();
		}
		contentDynamiTe.setVariable("SHIPPING_METHOD_NAME", shippingMethodName );
		contentDynamiTe.setVariable("SHIPPING_COST_DOUBLE", ecommerceShoppingCart.getDeliveryCostString() );
		contentDynamiTe.setVariable("SHIPPING_ADDRESS_STRING", transaction.getShippingAddress().getAddressString() );
		contentDynamiTe.setVariable("BILLING_ADDRESS_STRING", transaction.getBillingAddress().getAddressString() );
		contentDynamiTe.setVariable("VAT_COST_DOUBLE", ecommerceShoppingCart.getCachedTotalVatAmountString() );
		//handle repetition here
		for(EcommerceShoppingCartItem shoppingItem : ecommerceShoppingCart.getEcommerceShoppingCartItems()) {
			contentDynamiTe.setVariable( "PRODUCT_CODE_REPEATED", CommonUtil.getStringOrEmpty(shoppingItem.getItemCode() ) );
			contentDynamiTe.setVariable( "PRODUCT_NAME_REPEATED", CommonUtil.getStringOrEmpty( shoppingItem.getItemName() ) );
			contentDynamiTe.setVariable( "PRODUCT_SIZE_REPEATED", shoppingItem.getProductSize() );
			contentDynamiTe.setVariable( "PRODUCT_BRAND_AND_NAME_REPEATED", shoppingItem.getItemName() );
			contentDynamiTe.setVariable( "PRODUCT_QUANTITY_REPEATED", FormatUtil.formatTwoDigit(shoppingItem.getQuantity()) );
			
			contentDynamiTe.setVariable( "PRODUCT_PRICE_DOUBLE_REPEATED", shoppingItem.getSingleItemFinalPriceString(false) );
			contentDynamiTe.setVariable( "PRODUCT_SUBTOTAL_DOUBLE_REPEATED", shoppingItem.getLinePriceString(true) );
			contentDynamiTe.parseDynElem( "productList" );
		}
	}
	
	@Override
	public boolean isSendToAdminAlsoByDefault(Transaction bulkEmailRecipient) {
		if( TransactionType.ECOMMERCE_ORDER.equals( ((Transaction) bulkEmailRecipient).getTransactionType() ) ) {
	    	if( EcommerceConfiguration.getEcommerceSettingsStatic().isEmailAlertAdminOnFrontendOrder() ) {
	    		return true;
	    	}
		}
    	return false;
	}
}
