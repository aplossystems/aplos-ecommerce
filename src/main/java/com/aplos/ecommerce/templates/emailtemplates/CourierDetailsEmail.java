package com.aplos.ecommerce.templates.emailtemplates;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.CompanyDetails;
import com.aplos.common.enums.EmailTemplateEnum;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.beans.Transaction.TransactionStatus;
import com.aplos.ecommerce.beans.couriers.CourierService;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;
import com.aplos.ecommerce.module.EcommerceConfiguration;

@Entity
public class CourierDetailsEmail extends TransactionEmail {
	private static final long serialVersionUID = 5533996401804618479L;

	public CourierDetailsEmail() {
	}
	
	public String getDefaultName() {
		return "Business Dispatch Notification";
	}

	@Override
	public String getDefaultSubject() {
		return "{TODAYS_DATE} {CUSTOMER_NAME} {CUSTOMER_COMPANY_NAME} - {COMPANY_NAME} Courier Details {CUSTOMER_REFERENCE}";
	}

	@Override
	public String getDefaultContent() {
		return loadBodyFromFile( "courierDetailsEmailBody.html" );
	}

	@Override
	public EmailTemplateEnum getEmailTemplateEnum() {
		return EcommerceEmailTemplateEnum.COURIER_DETAILS;
	}
	
	@Override
	public void addSubjectJDynamiTeValues(JDynamiTe jDynamiTe,
			Transaction transaction) {
		super.addSubjectJDynamiTeValues(jDynamiTe, transaction);
		Customer customer = transaction.getEcommerceShoppingCart().getCustomer();
		jDynamiTe.setVariable( "CUSTOMER_ID", CommonUtil.getStringOrEmpty( customer.getId() ) );
		jDynamiTe.setVariable( "UNIVERSAL_DATE", FormatUtil.getDBSimpleDateFormat().format( new Date() ) );
		jDynamiTe.setVariable( "CUSTOMER_FULLNAME", customer.getFullName() );
		jDynamiTe.setVariable( "COMPANY_NAME", "" );
		jDynamiTe.setVariable( "ORDER_NUMBER", transaction.getId().toString() );
	}
	
	@Override
	public void addContentJDynamiTeValues(JDynamiTe jDynamiTe,
			Transaction transaction) {
		super.addContentJDynamiTeValues(jDynamiTe, transaction);
		CompanyDetails companyDetails = CommonConfiguration.getCommonConfiguration().getDefaultCompanyDetails();
		jDynamiTe.setVariable("CUSTOMER_FIRSTNAME", transaction.getEcommerceShoppingCart().getCustomer().getSubscriber().getFirstName() );
		jDynamiTe.setVariable("ORDER_NUMBER", transaction.getId().toString() );
		jDynamiTe.setVariable("CUSTOMER_REFERENCE", CommonUtil.getStringOrEmpty( transaction.getCustomerReference() ) );
		jDynamiTe.setVariable( "COMPANY_NAME", CommonUtil.getStringOrEmpty( companyDetails.getCompanyName() ) );
		jDynamiTe.setVariable("DISPATCH_DATE", FormatUtil.getStdSimpleDateFormat().format( transaction.getDispatchDate() ) );
		jDynamiTe.setVariable("DISPATCH_TIME", FormatUtil.getStdHourMinuteFormat().format( transaction.getDispatchDate() ) );
		CourierService collectFromShopCourierService = EcommerceConfiguration.getEcommerceConfiguration().getCollectFromShopCourierService();
		if( collectFromShopCourierService != null && transaction.getEcommerceShoppingCart().getAvailableShippingService() != null &&
				collectFromShopCourierService.equals( transaction.getEcommerceShoppingCart().getAvailableShippingService().getCourierService() ) ) {
			jDynamiTe.setVariable("COMPANY_OPENING_HOURS", FormatUtil.replaceNewLineCharForHtml( companyDetails.getOpeningHours() ) );
			jDynamiTe.parseDynElem( "customerCollection" );
		} else {
			if( transaction.getEcommerceShoppingCart().getAvailableShippingService() != null ) {
				CourierService courierService = transaction.getEcommerceShoppingCart().getAvailableShippingService().getCourierService();
				jDynamiTe.setVariable("COLLECTION_DAY_NAME", courierService.getCollectionDayName( new Date() ) );
				jDynamiTe.setVariable("COURIER_COLLECTION_TIME", courierService.getCollectionTimeStr() );
				jDynamiTe.setVariable("COURIER_SERVICE_NAME", courierService.getName() );
				jDynamiTe.setVariable("COURIER_TRACKING_URL", courierService.getTrackingUrl() );
				jDynamiTe.setVariable("COURIER_TRACKING_NUMBER", transaction.getTrackingNumber() );
				jDynamiTe.setVariable("COURIER_TRACKING_INSTRUCTIONS", FormatUtil.replaceNewLineCharForHtml( courierService.getTrackingInstructions() ) );
				jDynamiTe.parseDynElem( "courierCollection" );
			}
		}
		jDynamiTe.setVariable("COMPANY_DETAILS_SALUTATION", companyDetails.getSalutation() );
		jDynamiTe.setVariable("CURRENT_USER_NAME", JSFUtil.getLoggedInUser().getFullName() );
		jDynamiTe.setVariable("COMPANY_EMAIL_SIGNATURE", companyDetails.getEmailSignature() );
		if (transaction.getTransactionStatus() == TransactionStatus.DISPATCHED || transaction.getTransactionStatus() == TransactionStatus.RETURNED) {
			jDynamiTe.setVariable( "COMPLETE_OR_CONTINUE", "This dispatch completes your order. We hope you will be very happy with your purchases." );
			jDynamiTe.setVariable("PART_OR_Y", "Y");
		} else {
			jDynamiTe.setVariable( "COMPLETE_OR_CONTINUE", "This is a partial order dispatchment - we will be sending additional packages soon. You will be notified when additional items are dispatched." );
			jDynamiTe.setVariable("PART_OR_Y", "Part of y");
		}
	}
}
