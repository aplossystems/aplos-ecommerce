package com.aplos.ecommerce.templates.printtemplates;

import java.util.Calendar;
import java.util.Map;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.AplosUrl;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.appconstants.AplosScopedBindings;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.Address;
import com.aplos.common.beans.CompanyDetails;
import com.aplos.common.listeners.AplosContextListener;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.templates.PrintTemplate;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.CompanyContact;
import com.aplos.ecommerce.beans.EcommerceShoppingCartItem;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.module.EcommerceConfiguration;
import com.aplos.ecommerce.module.EcommerceSettings;

@Entity
public class DispatchNoteTemplate extends TransactionTemplate {
	private static final long serialVersionUID = -815936482217398047L;
	
	public DispatchNoteTemplate() {
	}
	
	public DispatchNoteTemplate(Transaction transaction) {
		super(transaction);
	}
	
	@Override
	public void initialise(Map<String, String[]> params ) {
		setTransaction( (Transaction) new BeanDao( Transaction.class ).get( Long.parseLong( params.get( AplosScopedBindings.ID )[ 0 ] ) ) );
	}

	@Override
	public String getTransactionTemplateFileName() {
		return "dispatchNote.html";
	}
	
	@Override
	public String getName() {
		return "Dispatch note";
	}

	@Override
	public String getTemplateContent() {
		try {
			JDynamiTe jDynamiTe;

			if ((jDynamiTe = CommonUtil.loadContentInfoJDynamiTe( getTransactionTemplateFileName(), PrintTemplate.printTemplatePath, ApplicationUtil.getAplosContextListener() )) != null) {
				addContentDynamicKeys(jDynamiTe, ApplicationUtil.getAplosContextListener(), getTransaction(), JSFUtil.getContextPath());

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


	public void addContentDynamicKeys(JDynamiTe jDynamiTe, AplosContextListener aplosContextListener, Transaction transaction, String contextPath ) {
		CompanyDetails companyDetails = CommonConfiguration.getCommonConfiguration().getDefaultCompanyDetails();
		Calendar.getInstance();
		
		if ( companyDetails.getLogoDetails() != null && !CommonUtil.getStringOrEmpty( companyDetails.getLogoDetails().getFilename() ).equals("") ) {
			String serverAndContext = (String) aplosContextListener.getContext().getAttribute(AplosScopedBindings.SERVER_URL) + contextPath.replace("/", "");
			jDynamiTe.setVariable("COMPANY_LOGO", serverAndContext + "/" + companyDetails.getLogoDetails().getFilename() );
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
		if ( companyDetails.getAddress().getPhone() != null && companyDetails.getAddress().getPhone().length() > 0 ) {
			jDynamiTe.setVariable("COMPANY_INTERNATIONAL_PHONE", companyDetails.getAddress().getPhone().substring(1, companyDetails.getAddress().getPhone().length()));;
		}
		else {
			jDynamiTe.setVariable("COMPANY_INTERNATIONAL_PHONE", "");
		}
		jDynamiTe.setVariable("COMPANY_EMAIL", CommonUtil.getStringOrEmpty(companyDetails.getAddress().getEmailAddress()));
		jDynamiTe.setVariable("COMPANY_WEB", CommonUtil.getStringOrEmpty(companyDetails.getWeb()));
		jDynamiTe.setVariable("COMPANY_VATNO", CommonUtil.getStringOrEmpty(companyDetails.getVatNo()));
		jDynamiTe.setVariable("COMPANY_DIRECTOR", CommonUtil.getStringOrEmpty(companyDetails.getDirector()));
		jDynamiTe.setVariable("COMPANY_REGNO", CommonUtil.getStringOrEmpty(companyDetails.getRegNo()));
		jDynamiTe.setVariable("DISPATCHED_DATE", transaction.getDispatchedDateStr() );

		transaction.getEcommerceShoppingCart().getCustomer();
		jDynamiTe.setVariable("CUSTOMER_PHONE", FormatUtil.formatPhoneNumber( transaction.getShippingAddress().getPhone() ) );

		jDynamiTe.setVariable("SHIPPING_METHOD", CommonUtil.getStringOrEmpty( transaction.getCourierServiceName() ) );

		Address billingAddress = transaction.getBillingAddress();
		jDynamiTe.setVariable("BA_CUSTOMER_NAME", CommonUtil.getStringOrEmpty(billingAddress.getContactFullName()));
		jDynamiTe.setVariable("BA_ADDRESS", billingAddress.toHtmlFull( "", false ) );
		if( billingAddress.getCountry() != null ) {
			jDynamiTe.setVariable("BA_COUNTRY", CommonUtil.getStringOrEmpty(billingAddress.getCountry().getDisplayName()));
		}

		Address shippingAddress = transaction.getShippingAddress();
		if( transaction.getCustomer() instanceof CompanyContact ) {
			jDynamiTe.setVariable("DA_CUSTOMER_COMPANY", CommonUtil.getStringOrEmpty(((CompanyContact)transaction.getCustomer()).getCompany().getCompanyName()) + "<br/>" );
		}
		jDynamiTe.setVariable("DA_CUSTOMER_NAME", CommonUtil.getStringOrEmpty(shippingAddress.getContactFullName()));
		String shippingAddressHtml = shippingAddress.toHtmlFull( "", false );
		jDynamiTe.setVariable("DA_ADDRESS", shippingAddressHtml );
		
		if( shippingAddress.getCountry() != null ) {
			jDynamiTe.setVariable("DA_COUNTRY", CommonUtil.getStringOrEmpty(shippingAddress.getCountry().getDisplayName()));
		}

		jDynamiTe.setVariable("CUSTOMER_REFERENCE", CommonUtil.getStringOrEmpty( transaction.getCustomerReference() ) );
		jDynamiTe.setVariable( "INVOICE_NUMBER", String.valueOf( transaction.getInvoiceNumber() ) );

		if( transaction.getCustomer() instanceof CompanyContact ) {
			jDynamiTe.setVariable("COMPANY_ID", CommonUtil.getStringOrEmpty( ((CompanyContact) transaction.getEcommerceShoppingCart().getCustomer()).getCompany().getId().toString()));
			jDynamiTe.setVariable("CONTACT_ID", CommonUtil.getStringOrEmpty(transaction.getEcommerceShoppingCart().getCustomer().getId().toString()));
			jDynamiTe.parseDynElem( "companyIds" );
		} else {
			jDynamiTe.setVariable("CUSTOMER_ID", CommonUtil.getStringOrEmpty(transaction.getEcommerceShoppingCart().getCustomer().getId().toString()));
			jDynamiTe.parseDynElem( "customerId" );
		}

		if( transaction.getDeliveryRequiredByDate() != null ) {
			jDynamiTe.setVariable( "DELIVERY_REQUIRED_BY", transaction.getDeliveryRequiredByDateString().replace( "\n", "<br/>" ) );
			jDynamiTe.parseDynElem( "deliveryRequiredBy" );
		}

		if( transaction.getSpecialDeliveryInstructions() != null && !transaction.getSpecialDeliveryInstructions().equals( "" ) ) {
			jDynamiTe.setVariable( "SPECIAL_DELIVERY_INSTRUCTIONS_LABEL", "Special delivery instructions" );
			jDynamiTe.setVariable( "SPECIAL_DELIVERY_INSTRUCTIONS", transaction.getSpecialDeliveryInstructions().replace( "\n", "<br/>" ) );
			jDynamiTe.parseDynElem( "specialDeliveryInstructions" );
		}

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
		EcommerceSettings ecommerceSettings = EcommerceConfiguration.getEcommerceSettingsStatic();
		boolean isUsingCustomerReference = ecommerceSettings.isUsingCustomerReference();
		boolean isUsingSerialNumbers = ecommerceSettings.isUsingSerialNumbers();
		boolean isUsingItemCodes = ecommerceSettings.isUsingItemCodes();
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
		for (EcommerceShoppingCartItem shoppingItem : transaction.getEcommerceShoppingCart().getEcommerceShoppingCartItems()) {
			if( isUsingCustomerReference ) {
				jDynamiTe.setDynElemValue( "customerReferenceRow", "" );
				jDynamiTe.setVariable( "CUSTOMER_REFERENCE_REPEATED", CommonUtil.getStringOrEmpty( shoppingItem.getCustomerReference() ) );
				jDynamiTe.parseDynElem( "customerReferenceRow" );
			}
			if( isUsingSerialNumbers ) {
				jDynamiTe.setDynElemValue( "serialNumberRow", "" );
				jDynamiTe.setVariable( "PRODUCT_SERIAL_NUMBERS_REPEATED", shoppingItem.getSerialNumberAssignmentsStr() );
				jDynamiTe.parseDynElem( "serialNumberRow" );
			}
			if( isUsingItemCodes ) {
				jDynamiTe.setDynElemValue( "itemCodeRow", "" );
				jDynamiTe.setVariable( "PRODUCT_CODE_REPEATED", CommonUtil.getStringOrEmpty(shoppingItem.getItemCode() ) );
				jDynamiTe.parseDynElem( "itemCodeRow" );
			}

			if( !isKitItemsFixed && shoppingItem.getRealizedProduct() != null && shoppingItem.getRealizedProduct().getProductInfo().isKitItem() ) {
				jDynamiTe.setVariable( "PRODUCT_NAME_REPEATED", CommonUtil.getStringOrEmpty( shoppingItem.getProductNameText( true ) ) );
			} else {
				jDynamiTe.setVariable( "PRODUCT_NAME_REPEATED", CommonUtil.getStringOrEmpty( shoppingItem.getItemName() ) );
			}
			jDynamiTe.setVariable( "PRODUCT_QUANTITY_REPEATED", (FormatUtil.formatTwoDigit(shoppingItem.getQuantity())).substring(0, FormatUtil.formatTwoDigit(shoppingItem.getQuantity()).indexOf(".")));
			addProductJDynamiTeVariables(jDynamiTe, shoppingItem );
			jDynamiTe.parseDynElem( "productList" );
		}
		
		setSpecificJdynamiTeKeys(jDynamiTe, transaction);
	}

	public static String getTemplateUrl( Transaction transaction ) {
		AplosUrl aplosUrl = getBaseTemplateUrl( DispatchNoteTemplate.class );
		aplosUrl.addQueryParameter("id",transaction);
		return aplosUrl.toString();
	}
}
