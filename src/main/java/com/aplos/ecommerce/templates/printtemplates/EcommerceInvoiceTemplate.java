package com.aplos.ecommerce.templates.printtemplates;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.AplosUrl;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.servlets.MediaServlet;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.ecommerce.beans.PaymentMethod;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.enums.TransactionType;
import com.aplos.ecommerce.module.EcommerceConfiguration;

@Entity
public class EcommerceInvoiceTemplate extends TransactionTemplate {
	private static final long serialVersionUID = -3208719704200732104L;
	
	public EcommerceInvoiceTemplate() {
	}

	
	public EcommerceInvoiceTemplate(Transaction transaction) {
		super(transaction);
	}
	
	@Override
	public String getName() {
		return "Ecommerce invoice";
	}

	@Override
	public void setSpecificJdynamiTeKeys(JDynamiTe jDynamiTe, Transaction transaction) {
		super.setSpecificJdynamiTeKeys(jDynamiTe, transaction);
		if ( transaction.getFirstInvoicedDate() != null ) {
			jDynamiTe.setVariable("INVOICE_DATE", FormatUtil.formatDate( transaction.getFirstInvoicedDate() ) );
		} else {
			jDynamiTe.setVariable("INVOICE_DATE", FormatUtil.formatDate( new Date() ) );
		}
		jDynamiTe.setVariable( "INVOICE_NUMBER", String.valueOf( transaction.getInvoiceNumber() ) );
		if ( transaction.getTransactionType().equals( TransactionType.REFUND ) ) {
			jDynamiTe.setVariable( "INVOICE_HEADER", "Refund" );
			jDynamiTe.setVariable( "INVOICE_HEADER_CAPTIALS", "REFUND" );
		} else {
			jDynamiTe.setVariable( "INVOICE_HEADER", "Invoice" );
			jDynamiTe.setVariable( "INVOICE_HEADER_CAPTIALS", "INVOICE" );
		}

		PaymentMethod bankTransferPaymentMethod =EcommerceConfiguration.getEcommerceConfiguration().getBankTransferPaymentMethod();
		if( bankTransferPaymentMethod != null && bankTransferPaymentMethod.isActive() ) {
			jDynamiTe.parseDynElem( "bankTransferPayment" );
		}

		try {
			String barcodeImageUrl = MediaServlet.getBarcodeUrl( String.valueOf( transaction.getInvoiceNumber() ) );
			jDynamiTe.setVariable( "INVOICE_NUMBER_BARCODE_URL", barcodeImageUrl );

			if( transaction.getShippingAddress().getPostcode() != null ) {
				barcodeImageUrl = MediaServlet.getBarcodeUrl( transaction.getShippingAddress().getPostcode() );
				jDynamiTe.setVariable( "DA_ADDRESS_BARCODE_TAG", "<img src=\"" + barcodeImageUrl + "\" />" );
			}

			if( transaction.getBillingAddress().getPostcode() != null ) {
				barcodeImageUrl = MediaServlet.getBarcodeUrl( transaction.getBillingAddress().getPostcode() );
				jDynamiTe.setVariable( "BA_ADDRESS_BARCODE_TAG", "<img src=\"" + barcodeImageUrl + "\" />" );
			}
		} catch( UnsupportedEncodingException useEx ) {
			ApplicationUtil.getAplosContextListener().handleError( useEx );
		}
	}

	@Override
	public String getTransactionTemplateFileName() {
		return "ecommerceInvoice.html";
	}

	public static String getTemplateUrl( Transaction transaction ) {
		AplosUrl aplosUrl = getBaseTemplateUrl( EcommerceInvoiceTemplate.class );
		aplosUrl.addQueryParameter( "id", transaction );
		return aplosUrl.toString();
	}
}
