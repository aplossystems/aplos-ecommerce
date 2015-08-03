package com.aplos.ecommerce.templates.emailtemplates;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.CompanyDetails;
import com.aplos.common.beans.ShoppingCartItem;
import com.aplos.common.enums.EmailTemplateEnum;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.ecommerce.beans.EcommerceShoppingCartItem;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;

@Entity
public class DispatchNotificationEmail extends TransactionEmail {
	private static final long serialVersionUID = -2115594135655065177L;

	public DispatchNotificationEmail() {
	}
	
	public String getDefaultName() {
		return "Dispatch Notification";
	}

	@Override
	public String getDefaultSubject() {
		return "{TODAYS_DATE} {CUSTOMER_NAME} {CUSTOMER_COMPANY_NAME} - {COMPANY_NAME} Dispatch {CUSTOMER_REFERENCE}";
	}

	@Override
	public String getDefaultContent() {
		return loadBodyFromFile( "dispatchNotification.html" );
	}

	@Override
	public EmailTemplateEnum getEmailTemplateEnum() {
		return EcommerceEmailTemplateEnum.DISPATCH_NOTIFICATION;
	}
	
	@Override
	public void addSubjectJDynamiTeValues(JDynamiTe jDynamiTe,
			Transaction transaction) {
		super.addSubjectJDynamiTeValues(jDynamiTe, transaction);
		CompanyDetails companyDetails = CommonConfiguration.getCommonConfiguration().getDefaultCompanyDetails();
		jDynamiTe.setVariable( "COMPANY_NAME", companyDetails.getCompanyName() );
		jDynamiTe.setVariable( "ORDER_NUMBER", transaction.getId().toString() );
	}
	
	@Override
	public void addContentJDynamiTeValues(JDynamiTe jDynamiTe,
			Transaction transaction) {
		super.addContentJDynamiTeValues(jDynamiTe, transaction);
		jDynamiTe.setVariable("CUSTOMER_FULLNAME", transaction.getEcommerceShoppingCart().getCustomer().getDisplayName() );
		CompanyDetails companyDetails = CommonConfiguration.getCommonConfiguration().getDefaultCompanyDetails();
		jDynamiTe.setVariable("COMPANY_NAME", companyDetails.getAddress().getContactFirstName() );
		if( companyDetails.getLogoDetails() != null ) {
			jDynamiTe.setVariable("COMPANY_LOGO", companyDetails.getLogoDetails().getFilename() );
		}
		jDynamiTe.setVariable("COMPANY_WEBSITE", companyDetails.getWeb() );
		jDynamiTe.setVariable("ORDER_NUMBER", transaction.getId().toString() );
		jDynamiTe.setVariable("DISPATCH_DATETIME", new Date().toString() );
		String shippingMethodName = "";
		if( transaction.getEcommerceShoppingCart().getAvailableShippingService() != null ) {
			shippingMethodName = transaction.getEcommerceShoppingCart().getAvailableShippingService().getCachedServiceName();
		}
		jDynamiTe.setVariable("SHIPPING_METHOD_NAME", shippingMethodName );
		jDynamiTe.setVariable("SHIPPING_ADDRESS_STRING", transaction.getShippingAddress().getAddressString() );
		jDynamiTe.setVariable("ORDER_STATUS", transaction.getTransactionStatus().getLabel() );
		List<ShoppingCartItem> shoppingCartItems = transaction.getEcommerceShoppingCart().getItems();
		for(int i=0, n = shoppingCartItems.size(); i < n; i++) {
			EcommerceShoppingCartItem shoppingItem = (EcommerceShoppingCartItem) shoppingCartItems.get(i);
			jDynamiTe.setVariable( "PRODUCT_CODE_REPEATED", CommonUtil.getStringOrEmpty(shoppingItem.getItemCode() ) );
			jDynamiTe.setVariable( "PRODUCT_SIZE_REPEATED", shoppingItem.getProductSize() );
			jDynamiTe.setVariable( "PRODUCT_BRAND_AND_NAME_REPEATED", shoppingItem.getItemName() );
			jDynamiTe.setVariable( "PRODUCT_QUANTITY_REPEATED", FormatUtil.formatTwoDigit(shoppingItem.getQuantity()) );
			jDynamiTe.parseDynElem( "productList" );
		}
		jDynamiTe.setVariable( "COMPLETE_OR_CONTINUE", "This dispatch completes your order. We hope you will be very happy with your purchases." );
		jDynamiTe.setVariable("PART_OR_Y", "Y");
	}
	
}
