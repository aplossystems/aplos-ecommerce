package com.aplos.ecommerce.templates.printtemplates;

import java.util.Date;
import java.util.Map;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.MappedSuperclass;
import com.aplos.common.appconstants.AplosScopedBindings;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.Address;
import com.aplos.common.beans.CompanyDetails;
import com.aplos.common.beans.CreatedPrintTemplate;
import com.aplos.common.beans.Currency;
import com.aplos.common.interfaces.AplosWorkingDirectoryInter;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.templates.PrintTemplate;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.CompanyContact;
import com.aplos.ecommerce.beans.EcommerceShoppingCartItem;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.enums.EcommerceWorkingDirectory;
import com.aplos.ecommerce.module.EcommerceConfiguration;
import com.aplos.ecommerce.module.EcommerceSettings;

@MappedSuperclass
public abstract class TransactionTemplate extends PrintTemplate {
	private static final long serialVersionUID = 104325529550848400L;
	@ManyToOne
	private Transaction transaction;

	public TransactionTemplate() {}

	public TransactionTemplate( Transaction transaction ) {
		setTransaction( transaction );
	}
	
	@Override
	public void initialise(Map<String, String[]> params) {
		setTransaction( (Transaction) new BeanDao( Transaction.class ).get( Long.parseLong( params.get( AplosScopedBindings.ID )[ 0 ] ) ) );
	}

	public abstract String getTransactionTemplateFileName();
//	public abstract String getDirectoryPath();
//	public abstract String getPdfTypeName();
//	public abstract String getTemplateContent( RealizedProductReturn realizedProductReturn, AplosContextListener aplosContextListener, String contextPath );

	public void setSpecificJdynamiTeKeys( JDynamiTe jDynamiTe, Transaction transaction ) {}

	// this is to allow overriding
	public void addProductJDynamiTeVariables(JDynamiTe jDynamiTe, EcommerceShoppingCartItem cartItem ) {}

	public String getTemplateContent() {
		try {
			JDynamiTe jDynamiTe;

			if ((jDynamiTe = CommonUtil.loadContentInfoJDynamiTe( getTransactionTemplateFileName(), PrintTemplate.printTemplatePath, ApplicationUtil.getAplosContextListener() )) != null) {

				CompanyDetails companyDetails = CommonConfiguration.getCommonConfiguration().getDefaultCompanyDetails();

				// Company Details
				if (companyDetails.getLogoDetails() != null && !CommonUtil.getStringOrEmpty( companyDetails.getLogoDetails().getFilename() ).equals("") ) {
					jDynamiTe.setVariable("COMPANY_LOGO", companyDetails.getLogoDetails().getExternalFileUrlByServerUrl() );
				}
				jDynamiTe.setVariable("COMPANY_NAME", CommonUtil.getStringOrEmpty(companyDetails.getCompanyName()));
				jDynamiTe.setVariable("COMPANY_ADDRESS_1", CommonUtil.getStringOrEmpty(companyDetails.getAddress().getLine1()) );
				if (companyDetails.getAddress().getLine2()!= null && !companyDetails.getAddress().getLine2().equals("")) {
					jDynamiTe.setVariable("COMPANY_ADDRESS_2", ", " + companyDetails.getAddress().getLine2());
				} else {
					jDynamiTe.setVariable("COMPANY_ADDRESS_2", "");
				}
				if (companyDetails.getAddress().getLine3()!= null && !companyDetails.getAddress().getLine3().equals("")) {
					jDynamiTe.setVariable("COMPANY_ADDRESS_3", ", " + companyDetails.getAddress().getLine3());
				} else {
					jDynamiTe.setVariable("COMPANY_ADDRESS_3", "");
				}

				jDynamiTe.setVariable("COMPANY_TOWN", CommonUtil.getStringOrEmpty(companyDetails.getAddress().getCity()));
				jDynamiTe.setVariable("COMPANY_COUNTY", CommonUtil.getStringOrEmpty(companyDetails.getAddress().getState()));
				jDynamiTe.setVariable("COMPANY_POSTCODE", CommonUtil.getStringOrEmpty(companyDetails.getAddress().getPostcode()));
				if( companyDetails.getAddress().getCountry() != null ) {
					jDynamiTe.setVariable("COMPANY_COUNTRY", CommonUtil.getStringOrEmpty(companyDetails.getAddress().getCountry().getDisplayName()));
				}
				jDynamiTe.setVariable("COMPANY_PHONE", CommonUtil.getStringOrEmpty(companyDetails.getAddress().getPhone()));
				jDynamiTe.setVariable("COMPANY_FAX", CommonUtil.getStringOrEmpty(companyDetails.getAddress().getFax()));
				if ( companyDetails.getAddress().getPhone() != null && companyDetails.getAddress().getPhone().length() > 0) {
					jDynamiTe.setVariable("COMPANY_INTERNATIONAL_PHONE", companyDetails.getAddress().getPhone().substring(1, companyDetails.getAddress().getPhone().length()));;
				}
				else {
					jDynamiTe.setVariable("COMPANY_INTERNATIONAL_PHONE", "");
				}
				jDynamiTe.setVariable("COMPANY_EMAIL", CommonUtil.getStringOrEmpty(companyDetails.getAddress().getEmailAddress()));
				jDynamiTe.setVariable("COMPANY_WEB", CommonUtil.getStringOrEmpty(companyDetails.getWeb()));
				if( !CommonUtil.isNullOrEmpty(companyDetails.getVatNo()) ) {
					jDynamiTe.setVariable("COMPANY_VATNO", CommonUtil.getStringOrEmpty(companyDetails.getVatNo()));
					jDynamiTe.parseDynElem("showVat");
				}
				jDynamiTe.setVariable("COMPANY_DIRECTOR", CommonUtil.getStringOrEmpty(companyDetails.getDirector()));
				jDynamiTe.setVariable("COMPANY_REGNO", CommonUtil.getStringOrEmpty(companyDetails.getRegNo()));


				jDynamiTe.setVariable("SITE_BANK_NAME", CommonUtil.getStringOrEmpty(companyDetails.getBankName()));
				jDynamiTe.setVariable("SITE_ACCOUNT_NAME", CommonUtil.getStringOrEmpty(companyDetails.getAccountName()));
				jDynamiTe.setVariable("SITE_IBAN_NUMBER", CommonUtil.getStringOrEmpty(companyDetails.getIbanNumber()));
				jDynamiTe.setVariable("SITE_ACCOUNT_NUMBER", CommonUtil.getStringOrEmpty(companyDetails.getAccountNumber()));
				jDynamiTe.setVariable("SITE_SORT_CODE", CommonUtil.getStringOrEmpty(companyDetails.getSortCode()));
				jDynamiTe.setVariable("SITE_SWIFTCODE", CommonUtil.getStringOrEmpty(companyDetails.getSwiftCode()));

				// Transaction details

				jDynamiTe.setVariable("TRANSACTION_DATE", FormatUtil.formatDate( new Date() ) );
				jDynamiTe.setVariable("VALID_UNTIL_DATE", getTransaction().getValidUntilDateStdStr());
				jDynamiTe.setVariable("TRANSACTION_NUMBER", getTransaction().getId().toString() );

				jDynamiTe.setVariable("CUSTOMER_REFERENCE", CommonUtil.getStringOrEmpty( getTransaction().getCustomerReference() ) );
				if( getTransaction().getPaymentMethod() != null ) {
					jDynamiTe.setVariable("PAYMENT_METHOD", getTransaction().getPaymentMethod().getName() );
					if (getTransaction().getEcommerceShoppingCart().getCustomer().getCreditCardDetails() != null
							&& getTransaction().getEcommerceShoppingCart().getCustomer().getCreditCardDetails().getCardNumber() != null) {
						String creditCardNumber = getTransaction().getEcommerceShoppingCart().getCustomer().getCreditCardDetails().getCardNumber();
						String lastFourDigits = creditCardNumber.substring(creditCardNumber.length()-4);
						jDynamiTe.setVariable("CARD_NUMBER_LAST_DIGITS", lastFourDigits );
					}
					else {
						jDynamiTe.setVariable("CARD_NUMBER_LAST_DIGITS", "****" );
					}
				}

				Address billingAddress = getTransaction().getBillingAddress();
				jDynamiTe.setVariable("BA_CUSTOMER_NAME", CommonUtil.getStringOrEmpty(billingAddress.getContactFullName()));
				jDynamiTe.setVariable("BA_ADDRESS", billingAddress.toHtmlFull( "", false ) );
				if( billingAddress.getCountry() != null ) {
					jDynamiTe.setVariable("BA_COUNTRY", CommonUtil.getStringOrEmpty(billingAddress.getCountry().getDisplayName()));
				}

				Address shippingAddress = getTransaction().getShippingAddress();
				jDynamiTe.setVariable("DA_CUSTOMER_NAME", CommonUtil.getStringOrEmpty(shippingAddress.getContactFullName()));
				jDynamiTe.setVariable("DA_ADDRESS", shippingAddress.toHtmlFull( "", false ) );
				if( shippingAddress.getCountry() != null ) {
					jDynamiTe.setVariable("DA_COUNTRY", CommonUtil.getStringOrEmpty(shippingAddress.getCountry().getDisplayName()));
				}
				jDynamiTe.setVariable("DA_PHONE", CommonUtil.getStringOrEmpty( shippingAddress.getPhone() ) );

				if( getTransaction().getCustomer() instanceof CompanyContact ) {
					jDynamiTe.setVariable("COMPANY_ID", CommonUtil.getStringOrEmpty( ((CompanyContact) getTransaction().getEcommerceShoppingCart().getCustomer()).getCompany().getId().toString()));
					jDynamiTe.setVariable("CONTACT_ID", CommonUtil.getStringOrEmpty(getTransaction().getEcommerceShoppingCart().getCustomer().getId().toString()));
					jDynamiTe.parseDynElem( "companyIds" );
				} else {
					jDynamiTe.setVariable("CUSTOMER_ID", CommonUtil.getStringOrEmpty(getTransaction().getEcommerceShoppingCart().getCustomer().getId().toString()));
					jDynamiTe.parseDynElem( "customerId" );
				}

				Currency currency = getTransaction().getEcommerceShoppingCart().getCurrency();
				if (currency == null) {
					BeanDao aqlBeanDao = new BeanDao(Currency.class);
					currency = (Currency) aqlBeanDao.get(1);
				}

				jDynamiTe.setVariable("INVOICE_CURRENCY", currency.getDisplayName());
				if (companyDetails.getAddress().getLine2()!= null && !companyDetails.getAddress().getLine2().equals("")) {
					jDynamiTe.setVariable("COMPANY_ADDRESS_2_2", companyDetails.getAddress().getLine2() + "<br />");
				} else {
					jDynamiTe.setVariable("COMPANY_ADDRESS_2_2", "");
				}
				if (companyDetails.getAddress().getLine3()!= null && !companyDetails.getAddress().getLine3().equals("")) {
					jDynamiTe.setVariable("COMPANY_ADDRESS_3_2", companyDetails.getAddress().getLine3() + "<br />");
				} else {
					jDynamiTe.setVariable("COMPANY_ADDRESS_3_2", "");
				}

//				if( getAssociatedEmailTemplate() != null ) {
//					//SystemUser currentUser;
//					//if ( FacesContext.getCurrentInstance() == null ) {
//					//	currentUser = CommonConfiguration.getCommonConfiguration().getDefaultAdminUser();
//					//} else {
//					//	currentUser = JSFUtil.getLoggedInUser();
//					//}
//					//TODO: take another look at this - fixed to sync - need to pass in current user
//					//jDynamiTe.setVariable("MAIN_TEMPLATE_TEXT", ((TransactionEmail) getAssociatedEmailTemplate()).parseContent(transaction, aplosContextListener, currentUser) );
//					jDynamiTe.setVariable("MAIN_TEMPLATE_TEXT", ((TransactionEmail) getAssociatedEmailTemplate()).compileContent(transaction, ((TransactionEmail) getAssociatedEmailTemplate()).determineContent(aplosContextListener), aplosContextListener) );
//				}

				EcommerceSettings ecommerceSettings = EcommerceConfiguration.getEcommerceSettingsStatic();
				boolean isUsingCustomerReference = ecommerceSettings.isUsingCustomerReference();
				boolean isUsingSerialNumbers = ecommerceSettings.isUsingSerialNumbers();
				boolean isUsingItemCodes = ecommerceSettings.isUsingItemCodes();
				boolean isUsingCommodityCodes = ecommerceSettings.isUsingCommodityCodes();
				boolean isKitItemsFixed = ecommerceSettings.isKitItemsFixed();
				if( isUsingCustomerReference ) {
					jDynamiTe.parseDynElem( "customerReferenceHead" );
				}
				if( isUsingSerialNumbers ) {
					jDynamiTe.parseDynElem( "serialNumberHead" );
				}
				if( isUsingItemCodes ) {
					jDynamiTe.parseDynElem( "itemCodeHead" );
				}
				if( isUsingCommodityCodes ) {
					jDynamiTe.parseDynElem( "commodityCodeHead" );
				}
				for (EcommerceShoppingCartItem shoppingItem : getTransaction().getEcommerceShoppingCart().getEcommerceShoppingCartItems()) {
					if( isUsingCustomerReference ) {
						jDynamiTe.setDynElemValue( "customerReferenceRow", "" );
						jDynamiTe.setVariable( "CUSTOMER_REFERENCE_REPEATED", CommonUtil.getStringOrEmpty( shoppingItem.getCustomerReference() ) );
						jDynamiTe.parseDynElem( "customerReferenceRow" );
					}
					if( isUsingItemCodes ) {
						jDynamiTe.setDynElemValue( "itemCodeRow", "" );
						jDynamiTe.setVariable( "PRODUCT_CODE_REPEATED", CommonUtil.getStringOrEmpty(shoppingItem.getItemCode() ) );
						jDynamiTe.parseDynElem( "itemCodeRow" );
					}
					if( isUsingCommodityCodes ) {
						jDynamiTe.setDynElemValue( "commodityCodeRow", "" );
						jDynamiTe.setVariable( "PRODUCT_COMMODITY_CODE_REPEATED", CommonUtil.getStringOrEmpty( shoppingItem.getCommodityCode() ) );
						jDynamiTe.parseDynElem( "commodityCodeRow" );
					}

					if( !isKitItemsFixed && shoppingItem.getRealizedProduct() != null && shoppingItem.getRealizedProduct().getProductInfo().isKitItem() ) {
						jDynamiTe.setVariable( "PRODUCT_NAME_REPEATED", CommonUtil.getStringOrEmpty( shoppingItem.getProductNameText( true ) ) );
					} else {
						jDynamiTe.setVariable( "PRODUCT_NAME_REPEATED", CommonUtil.getStringOrEmpty( shoppingItem.getItemName() ) );
					}
					jDynamiTe.setVariable( "PRODUCT_QUANTITY_REPEATED", (FormatUtil.formatTwoDigit(shoppingItem.getQuantity())).substring(0, FormatUtil.formatTwoDigit(shoppingItem.getQuantity()).indexOf(".")));
					jDynamiTe.setVariable( "PRODUCT_PRICE_REPEATED", shoppingItem.getSingleItemNetPriceString() );
					jDynamiTe.setVariable( "PRODUCT_DISCOUNT_REPEATED", shoppingItem.getSingleItemDiscountAmountString() );
					jDynamiTe.setVariable( "PRODUCT_TOTAL_REPEATED", shoppingItem.getLinePriceString( false ) );
					addProductJDynamiTeVariables(jDynamiTe, shoppingItem );
					jDynamiTe.parseDynElem( "productList" );
				}

				jDynamiTe.setVariable( "CURRENCY_PREFIX", currency.getPrefix());
				jDynamiTe.setVariable( "CURRENCY_SUFFIX", currency.getSuffix());
				if( getTransaction().getDeliveryRequiredByDate() != null ) {
					jDynamiTe.setVariable( "DELIVERY_REQUIRED_BY", getTransaction().getDeliveryRequiredByDateString().replace( "\n", "<br/>" ) );
					jDynamiTe.parseDynElem( "deliveryRequiredBy" );
				}

				if( getTransaction().getSpecialDeliveryInstructions() != null && !getTransaction().getSpecialDeliveryInstructions().equals( "" ) ) {
					jDynamiTe.setVariable( "SPECIAL_DELIVERY_INSTRUCTIONS_LABEL", "Special delivery instructions" );
					jDynamiTe.setVariable( "SPECIAL_DELIVERY_INSTRUCTIONS", getTransaction().getSpecialDeliveryInstructions().replace( "\n", "<br/>" ) );
				}
				jDynamiTe.setVariable( "ITEMS_TOTAL", getTransaction().getEcommerceShoppingCart().getCachedNetTotalAmountString() );
				jDynamiTe.setVariable( "DELIVERY_CHARGE", getTransaction().getEcommerceShoppingCart().getCachedNetDeliveryCostString() );
				String shippingMethodName = "";
				if( getTransaction().getEcommerceShoppingCart().getAvailableShippingService() != null ) {
					shippingMethodName = getTransaction().getEcommerceShoppingCart().getAvailableShippingService().getCachedServiceName();
				}
				jDynamiTe.setVariable( "DELIVERY_METHOD", shippingMethodName);
				jDynamiTe.setVariable( "VAT_CHARGE", getTransaction().getEcommerceShoppingCart().getGrandTotalVatAmountString(true) );
				jDynamiTe.setVariable( "ADMIN_CHARGE", getTransaction().getEcommerceShoppingCart().getAdminChargeString() );
				jDynamiTe.setVariable( "INVOICE_GROSS_TOTAL", getTransaction().getEcommerceShoppingCart().getGrandTotalString(true) );

				setAdditionalTemplateKeys(jDynamiTe, getTransaction());
				/*
				 *  Make sure this is done at the bottom in case it uses parseDynElem based on variables
				 *  set above, like bankTransferPayment in EcommerceInvoiceTemplate
				 */
				setSpecificJdynamiTeKeys(jDynamiTe,getTransaction());
				jDynamiTe.parse();

				try {
					return jDynamiTe.toString();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch( Exception e ) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public AplosWorkingDirectoryInter getAplosWorkingDirectoryInter() {
		return EcommerceWorkingDirectory.TRANSACTION_PDFS_DIR;
	}

	public void redirectToPdf( Transaction transaction, CreatedPrintTemplate createdPrintTemplate ) {
		try {
			transaction.saveDetails();
			JSFUtil.redirect( createdPrintTemplate.getAplosUrl(), false);
		}
		catch(Exception e) {
			e.printStackTrace();
		}

	}

	public void setAdditionalTemplateKeys( JDynamiTe jDynamiTe, Transaction transaction ) {}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}
}
