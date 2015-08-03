package com.aplos.ecommerce.templates.emailtemplates;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.Address;
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
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.beans.product.Product;
import com.aplos.ecommerce.beans.product.ProductInfo;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;
import com.aplos.ecommerce.module.EcommerceConfiguration;
import com.aplos.ecommerce.utils.EcommerceUtil;

@Entity
public class PaymentFailedEmail extends SourceGeneratedEmailTemplate<Transaction> {
	private static final long serialVersionUID = -429267120518905713L;

	public PaymentFailedEmail() {
	}
	
	public String getDefaultName() {
		return "Payment Failed";
	}

	@Override
	public String getDefaultSubject() {
		return "{COMPANY_NAME} payment failed, no. {ORDER_NUMBER}";
	}

	@Override
	public String getDefaultContent() {
		return loadBodyFromFile( "paymentFailed.html" );
	}

	@Override
	public EmailTemplateEnum getEmailTemplateEnum() {
		return EcommerceEmailTemplateEnum.PAYMENT_FAILED;
	}
	
	@Override
	public void addSubjectJDynamiTeValues(JDynamiTe jDynamiTe,
			Transaction bulkEmailRecipient) {
		super.addSubjectJDynamiTeValues(jDynamiTe, bulkEmailRecipient);
		jDynamiTe.setVariable( "ORDER_NUMBER", this.getId().toString() );
	}
	
	@Override
	public void addContentJDynamiTeValues(JDynamiTe jDynamiTe,
			Transaction transaction) {
		super.addContentJDynamiTeValues(jDynamiTe, transaction);
		EcommerceShoppingCart ecommerceShoppingCart = transaction.getEcommerceShoppingCart();
		String prefixOrSuffix = ecommerceShoppingCart.getCurrency().getPrefixOrSuffix();
		CompanyDetails companyDetails = CommonConfiguration.getCommonConfiguration().getDefaultCompanyDetails();

		if( companyDetails.getLogoDetails() != null ) {
			jDynamiTe.setVariable("COMPANY_LOGO", CommonUtil.getStringOrEmpty(companyDetails.getLogoDetails().getFilename()) );
		}
		jDynamiTe.setVariable("COMPANY_WEBSITE", CommonUtil.getStringOrEmpty(companyDetails.getWeb()) );
		jDynamiTe.setVariable("ERROR_TXT", transaction.getPaymentFailedErrorMessage() );
		jDynamiTe.setVariable("ORDER_NUMBER", this.getId().toString() );
		jDynamiTe.setVariable("CUSTOMER_FULLNAME", ecommerceShoppingCart.getCustomer().getDisplayName() );
		jDynamiTe.setVariable("PRODUCT_TOTAL_PRICE_DOUBLE", ecommerceShoppingCart.getCachedNetTotalAmountString() );
		jDynamiTe.setVariable("ORDER_TOTAL_PRICE_DOUBLE", ecommerceShoppingCart.getGrandTotalString(true) );
		jDynamiTe.setVariable("ORDER_DATETIME", this.getDateLastModified().toString() );
		jDynamiTe.setVariable("CURRENCY_SYMBOL", ecommerceShoppingCart.getCurrency().getSymbol() );
		jDynamiTe.setVariable("CURRENCY_PREFIX_SUFFIX", prefixOrSuffix );
		String shippingMethodName = "";
		if( ecommerceShoppingCart.getAvailableShippingService() != null ) {
			shippingMethodName = ecommerceShoppingCart.getAvailableShippingService().getCachedServiceName();
		}
		jDynamiTe.setVariable("SHIPPING_METHOD_NAME", shippingMethodName );
		jDynamiTe.setVariable("SHIPPING_COST_DOUBLE", ecommerceShoppingCart.getDeliveryCostString() );
		jDynamiTe.setVariable("SHIPPING_ADDRESS_STRING", transaction.getShippingAddress().getAddressString() );
		jDynamiTe.setVariable("BILLING_ADDRESS_STRING", transaction.getBillingAddress().getAddressString() );
		//handle repetition here
		for(EcommerceShoppingCartItem shoppingItem : ecommerceShoppingCart.getEcommerceShoppingCartItems()) {
			jDynamiTe.setVariable( "PRODUCT_CODE_REPEATED", CommonUtil.getStringOrEmpty(shoppingItem.getItemCode() ) );
			jDynamiTe.setVariable( "PRODUCT_SIZE_REPEATED", shoppingItem.getProductSize() );
			jDynamiTe.setVariable( "PRODUCT_BRAND_AND_NAME_REPEATED", shoppingItem.getItemName() );
			jDynamiTe.setVariable( "PRODUCT_QUANTITY_REPEATED", FormatUtil.formatTwoDigit(shoppingItem.getQuantity()) );
			jDynamiTe.setVariable( "PRODUCT_PRICE_DOUBLE_REPEATED", shoppingItem.getSingleItemFinalPriceString(true) );
			jDynamiTe.setVariable( "PRODUCT_SUBTOTAL_DOUBLE_REPEATED", shoppingItem.getLinePriceString(true) );
			jDynamiTe.parseDynElem( "productList" );
		}
	}

	@Override
	public BulkEmailSource getTestSource(SystemUser adminUser) {
		Transaction transaction = new Transaction();
		transaction.initialiseNewBean();
		transaction.setId(88888l);
		EcommerceShoppingCart ecommerceShoppingCart = EcommerceUtil.getEcommerceUtil().createShoppingCart();
		Customer customer = new Customer();
		//TODO need to set an email address without creating new subscribers in the database.
		ecommerceShoppingCart.setCustomer(customer);
		RealizedProduct realizedProduct = new RealizedProduct().initialiseNewBean();
		ProductInfo productInfo = new ProductInfo().initialiseNewBean();
		Product product = new Product().<Product>initialiseNewBean();
		product.setName("Test Voucher");
		product.addProductType(EcommerceConfiguration.getEcommerceConfiguration().getGiftVoucherProductType());
		productInfo.setProduct(product);
		realizedProduct.setProductInfo(productInfo);
		ecommerceShoppingCart.addToCart(realizedProduct, false);
		ecommerceShoppingCart.setCurrency(CommonUtil.getCurrency());
		transaction.setEcommerceShoppingCart(ecommerceShoppingCart);
		transaction.setDateLastModified(new Date());
		Address address = new Address();
		address.setContactFirstName(adminUser.getFirstName());
		address.setContactSurname(adminUser.getSurname());
		address.setEmailAddress(adminUser.getEmail());
		transaction.setBillingAddress(address);
		transaction.setShippingAddress(address);
		return transaction;
	}
}
