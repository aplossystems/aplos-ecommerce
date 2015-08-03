package com.aplos.ecommerce.templates.printtemplates;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.CompanyDetails;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.servlets.MediaServlet;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.ecommerce.beans.Company;
import com.aplos.ecommerce.beans.CompanyContact;
import com.aplos.ecommerce.beans.EcommerceShoppingCartItem;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.enums.TransactionType;
import com.aplos.ecommerce.module.EcommerceConfiguration;
import com.aplos.ecommerce.module.EcommerceSettings;

@Entity
public class DispatchInvoiceTemplate extends TransactionTemplate {
	private static final long serialVersionUID = 6533412587610695907L;
	
	public DispatchInvoiceTemplate() {
	}

	public DispatchInvoiceTemplate(Transaction transaction) {
		super(transaction);
	}
	
	@Override
	public String getName() {
		return "Dispatch invoice";
	}

	@Override
	public void setSpecificJdynamiTeKeys(JDynamiTe jDynamiTe, Transaction transaction) {
		super.setSpecificJdynamiTeKeys(jDynamiTe, transaction);
		if( transaction.getInvoiceNumber() == null ) {
			transaction.setInvoiceNumber( EcommerceConfiguration.getEcommerceConfiguration().getNextMaxInvoiceNumber( transaction ) );
		}

		CompanyDetails companyDetails = CommonConfiguration.getCommonConfiguration().getDefaultCompanyDetails();
		if( companyDetails != null && companyDetails.getLogoDetails() != null ) {
			jDynamiTe.setVariable( "COMPANY_LOGO_URL",  companyDetails.getLogoDetails().getExternalFileUrl() );
		}
		if( transaction.getFirstInvoicedDate() != null ) {
			jDynamiTe.setVariable("INVOICE_DATE", FormatUtil.formatDate( transaction.getFirstInvoicedDate() ) );
		} else {
			jDynamiTe.setVariable("INVOICE_DATE", FormatUtil.formatDate( new Date() ) );
		}
		jDynamiTe.setVariable( "INVOICE_NUMBER", String.valueOf( transaction.getInvoiceNumber() ) );
		jDynamiTe.setVariable( "DISPATCHED_DATE", transaction.getDispatchedDateStr() );

		jDynamiTe.setVariable( "COURIER_SERVICE_NAME", CommonUtil.getStringOrEmpty( transaction.getCourierServiceName() ) );
		if( transaction.getTransactionType().equals( TransactionType.REFUND ) ) {
			jDynamiTe.setVariable( "INVOICE_HEADER", "REFUND" );
		} else {
			jDynamiTe.setVariable( "INVOICE_HEADER", "INVOICE" );
		}

		try {
			String barcodeImageUrl = MediaServlet.getBarcodeUrl( String.valueOf( transaction.getInvoiceNumber() ) );
			jDynamiTe.setVariable( "INVOICE_NUMBER_BARCODE_URL", barcodeImageUrl );

			if( transaction.getBillingAddress().getPostcode() != null ) {
				barcodeImageUrl = MediaServlet.getBarcodeUrl( transaction.getBillingAddress().getPostcode() );
				jDynamiTe.setVariable( "BA_ADDRESS_BARCODE_TAG", "<img src=\"" + barcodeImageUrl + "\" />" );
			}

			if( transaction.getShippingAddress().getPostcode() != null ) {
				barcodeImageUrl = MediaServlet.getBarcodeUrl( transaction.getShippingAddress().getPostcode() );
				jDynamiTe.setVariable( "DA_ADDRESS_BARCODE_TAG", "<img src=\"" + barcodeImageUrl + "\" />" );
			}
		} catch( UnsupportedEncodingException useEx ) {
			ApplicationUtil.getAplosContextListener().handleError( useEx );
		}


		EcommerceSettings ecommerceSettings = EcommerceConfiguration.getEcommerceSettingsStatic();
		boolean isUsingCustomerReference = ecommerceSettings.isUsingCustomerReference();
		boolean isUsingSerialNumbers = ecommerceSettings.isUsingSerialNumbers();
		boolean isUsingItemCodes = ecommerceSettings.isUsingItemCodes();
		boolean isUsingCommodityCodes = ecommerceSettings.isUsingCommodityCodes();
		boolean isKitItemsFixed = ecommerceSettings.isKitItemsFixed();
		if( isUsingCustomerReference ) {
			jDynamiTe.parseDynElem( "customerReferenceHead2" );
		}
		if( isUsingSerialNumbers ) {
			jDynamiTe.parseDynElem( "serialNumberHead2" );
		}
		if( isUsingItemCodes ) {
			jDynamiTe.parseDynElem( "itemCodeHead2" );
		}
		if( isUsingCommodityCodes ) {
			jDynamiTe.parseDynElem( "itemCodeHead2" );
		}
		for (EcommerceShoppingCartItem shoppingItem : transaction.getEcommerceShoppingCart().getEcommerceShoppingCartItems()) {
			if( isUsingCustomerReference ) {
				jDynamiTe.setDynElemValue( "customerReferenceRow2", "" );
				jDynamiTe.setVariable( "CUSTOMER_REFERENCE_REPEATED", CommonUtil.getStringOrEmpty( shoppingItem.getCustomerReference() ) );
				jDynamiTe.parseDynElem( "customerReferenceRow2" );
			}
			if( isUsingItemCodes ) {
				jDynamiTe.setDynElemValue( "itemCodeRow2", "" );
				jDynamiTe.setVariable( "PRODUCT_CODE_REPEATED", CommonUtil.getStringOrEmpty(shoppingItem.getItemCode() ) );
				jDynamiTe.parseDynElem( "itemCodeRow2" );
			}
			if( isUsingCommodityCodes ) {
				jDynamiTe.setDynElemValue( "commodityCodeRow2", "" );
				jDynamiTe.setVariable( "PRODUCT_COMMODITY_CODE_REPEATED", CommonUtil.getStringOrEmpty( shoppingItem.getCommodityCode() ) );
				jDynamiTe.parseDynElem( "commodityCodeRow2" );
			}

			if( !isKitItemsFixed && shoppingItem.getRealizedProduct() != null && shoppingItem.getRealizedProduct().getProductInfo().isKitItem() ) {
				jDynamiTe.setVariable( "PRODUCT_NAME_REPEATED", CommonUtil.getStringOrEmpty( shoppingItem.getProductNameText( true ) ) );
			} else {
				jDynamiTe.setVariable( "PRODUCT_NAME_REPEATED", CommonUtil.getStringOrEmpty( shoppingItem.getItemName() ) );
			}
			jDynamiTe.setVariable( "PRODUCT_QUANTITY_REPEATED", (FormatUtil.formatTwoDigit(shoppingItem.getQuantity())).substring(0, FormatUtil.formatTwoDigit(shoppingItem.getQuantity()).indexOf(".")));
			jDynamiTe.parseDynElem( "productList2" );
		}

		if( transaction.getCustomer() instanceof CompanyContact ) {
			jDynamiTe.setVariable("COMPANY_ID", CommonUtil.getStringOrEmpty( ((CompanyContact) transaction.getEcommerceShoppingCart().getCustomer()).getCompany().getId().toString()));
			jDynamiTe.setVariable("CONTACT_ID", CommonUtil.getStringOrEmpty(transaction.getEcommerceShoppingCart().getCustomer().getId().toString()));
			jDynamiTe.parseDynElem( "companyIds2" );
		} else {
			jDynamiTe.setVariable("CUSTOMER_ID", CommonUtil.getStringOrEmpty(transaction.getEcommerceShoppingCart().getCustomer().getId().toString()));
			jDynamiTe.parseDynElem( "customerId2" );
		}

		jDynamiTe.setVariable( "SITE_BANK_NAME", CommonUtil.getStringOrEmpty( companyDetails.getBankName() ) );
		jDynamiTe.setVariable( "SITE_ACCOUNT_NAME", CommonUtil.getStringOrEmpty( companyDetails.getAccountName() ) );
		jDynamiTe.setVariable( "SITE_IBAN_NUMBER", CommonUtil.getStringOrEmpty( companyDetails.getIbanNumber() ) );
		jDynamiTe.setVariable( "SITE_ACCOUNT_NUMBER", CommonUtil.getStringOrEmpty( companyDetails.getAccountNumber() ) );
		jDynamiTe.setVariable( "SITE_SORT_CODE", CommonUtil.getStringOrEmpty( companyDetails.getSortCode() ) );
		jDynamiTe.setVariable( "SITE_SWIFTCODE", CommonUtil.getStringOrEmpty( companyDetails.getSwiftCode() ) );


		jDynamiTe.setVariable( "SITE_VAT_NUMBER", CommonUtil.getStringOrEmpty( companyDetails.getVatNo() ) );
		if( transaction.getCustomer() instanceof CompanyContact ) {
			Company company = ((CompanyContact) transaction.getCustomer()).getCompany();
			if( company != null ) {
				jDynamiTe.setVariable( "CUSTOMER_VAT_NUMBER", CommonUtil.getStringOrEmpty( company.getVatNumber() ) );
			}
		}
	}

	@Override
	public String getTransactionTemplateFileName() {
		return "dispatchInvoice.html";
	}
}
