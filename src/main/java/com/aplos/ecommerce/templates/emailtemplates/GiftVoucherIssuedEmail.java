package com.aplos.ecommerce.templates.emailtemplates;

import java.io.ByteArrayInputStream;
import java.io.IOException;

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
import com.aplos.common.utils.JSFUtil;
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
public class GiftVoucherIssuedEmail extends SourceGeneratedEmailTemplate<Transaction> {

	private static final long serialVersionUID = 8643039154199484799L;

	public GiftVoucherIssuedEmail() {
	}
	
	public String getDefaultName() {
		return "Gift Voucher Issued";
	}

	@Override
	public String getDefaultSubject() {
		return "{COMPANY_NAME} gift vouchers from {BUYER_NAME}";
	}

	@Override
	public String getDefaultContent() {
		return loadBodyFromFile( "giftVoucherIssued.html" );
	}

	@Override
	public BulkEmailSource getTestSource(SystemUser adminUser) {
		Transaction transaction = new Transaction();
		transaction.initialiseNewBean();
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
		transaction.setEcommerceShoppingCart(ecommerceShoppingCart);
		Address address = new Address();
		address.setLine1("New Test Address 1");
		transaction.setGiftDeliveryAddress(address);
		return transaction;
	}

	@Override
	public EmailTemplateEnum getEmailTemplateEnum() {
		return EcommerceEmailTemplateEnum.GIFT_VOUCHER_ISSUED;
	}
	
	@Override
	public void addSubjectJDynamiTeValues(JDynamiTe jDynamiTe,
			Transaction transaction) {
		super.addSubjectJDynamiTeValues(jDynamiTe, transaction);
		jDynamiTe.setVariable( "BUYER_NAME", transaction.getCustomer().getFullName() );
	}
	
	@Override
	public void addContentJDynamiTeValues(JDynamiTe jDynamiTe,
			Transaction transaction) {
		super.addContentJDynamiTeValues(jDynamiTe, transaction);
		EcommerceShoppingCart ecommerceShoppingCart = transaction.getEcommerceShoppingCart();
		jDynamiTe.setVariable( "RECIPIENT_NAME", CommonUtil.getStringOrEmpty(transaction.getGiftDeliveryAddress().getContactFirstName()) );
		jDynamiTe.setVariable( "BUYER_NAME", transaction.getCustomer().getFullName() );

		for(EcommerceShoppingCartItem shoppingItem : ecommerceShoppingCart.getEcommerceShoppingCartItems()) {
			if (shoppingItem.determineIsGiftItem()) {
				if (shoppingItem.getRealizedProduct().isGiftVoucher()) {
					jDynamiTe.setVariable( "VOUCHER_VALUE_REPEATED", shoppingItem.getRealizedProduct().getDisplayName() );
				} else {
					jDynamiTe.setVariable( "VOUCHER_VALUE_REPEATED", "Voucher for " + shoppingItem.getRealizedProduct().getDisplayName() );
				}
				jDynamiTe.parseDynElem( "voucherList" );
			}
		}
		jDynamiTe.setVariable( "REDEEM_URL", JSFUtil.getServerUrl() );
	}
}
