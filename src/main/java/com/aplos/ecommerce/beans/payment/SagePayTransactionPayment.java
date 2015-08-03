package com.aplos.ecommerce.beans.payment;

import java.math.BigDecimal;

import com.aplos.common.backingpage.payments.sagepay.SagePayThreeDCompletePage;
import com.aplos.common.beans.PaymentGatewayPost;
import com.aplos.common.beans.sagepay.SagePayConfigurationDetails;
import com.aplos.common.beans.sagepay.SagePayConfigurationDetails.SagePayUrlType;
import com.aplos.common.beans.sagepay.SagePayPost;
import com.aplos.common.beans.sagepay.directintegration.SagePayDirectPost;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.Transaction;

public class SagePayTransactionPayment extends TransactionPayment {

	@Override
	public PaymentGatewayPost createPaymentGatewayDirectPost( Transaction transaction ) {
		SagePayConfigurationDetails sagePayConfigurationDetails = CommonConfiguration.getCommonConfiguration().determineSagePayCfgDetails();
		SagePayDirectPost sagePayDirectPost = new SagePayDirectPost( sagePayConfigurationDetails, transaction );
		sagePayDirectPost.initialiseNewBean();
		sagePayDirectPost.setDeliveryCost( transaction.getEcommerceShoppingCart().getDeliveryCost() );
		sagePayDirectPost.setDecTotal( transaction.getEcommerceShoppingCart().getGrandTotal(true) );
		return sagePayDirectPost;
	}
	
	@Override
	public PaymentGatewayPost sendOnlinePaymentRequest( Transaction transaction, boolean isPaymentMadeNow, BigDecimal partPayment, boolean isFrontEnd ) {
		SagePayDirectPost sagePayDirectPost = null;
		if( isPaymentMadeNow && transaction.getAuthenticatedPaymentGatewayDirectPost() != null ) {
			sagePayDirectPost = (SagePayDirectPost) transaction.getAuthenticatedPaymentGatewayDirectPost();
			sagePayDirectPost.authorise(partPayment);
			sagePayDirectPost.postRequest( sagePayDirectPost.getAuthoriseRequestPost(), sagePayDirectPost.getSagePayDetails().getSystemUrl(SagePayConfigurationDetails.IntegrationType.DIRECT, SagePayUrlType.AUTHORISE) );
		} else {
			sagePayDirectPost = (SagePayDirectPost) createPaymentGatewayDirectPost( transaction );

			if( !isPaymentMadeNow ) {
				sagePayDirectPost.setTransactionType( SagePayPost.TransactionType.AUTHENTICATE );
			}
			if( !isFrontEnd ) {
				sagePayDirectPost.setApply3dSecure(SagePayDirectPost.Apply3DSecure.OFF);
			}

			if( partPayment != null ) {
				sagePayDirectPost.setDecTotal( partPayment );
			}
			sagePayDirectPost.createRequestStr( transaction.getEcommerceShoppingCart().getEcommerceShoppingCartItems(), transaction.determineCreditCardDetails() );
			sagePayDirectPost.saveDetails();
			transaction.saveDetails();
			sagePayDirectPost.postRequest( sagePayDirectPost.getSagePayDetails().getSystemUrl(SagePayConfigurationDetails.IntegrationType.DIRECT, SagePayUrlType.PURCHASE) );	
		}
		
		if( isFrontEnd ) {
			SagePayThreeDCompletePage.redirectAfterRequestPost( transaction, sagePayDirectPost );
		} else {
			if( !sagePayDirectPost.isProcessed() ) {
				//EcommerceUtil.addAbandonmentIssueToCart( CartAbandonmentIssue.PAYMENT_ISSUE );
				JSFUtil.addMessage( sagePayDirectPost.getPageError() );
			} else { 
				transaction.paymentComplete( false, sagePayDirectPost );
				if( sagePayDirectPost.getStatus().equals( "REGISTERED" ) ) {
					JSFUtil.addMessage( "Payment has been authorised" );
				} else {
					JSFUtil.addMessage( "Payment has been made" );
				}
			}
		}
		return sagePayDirectPost;
	}
	
	@Override
	public void paymentComplete( Transaction transaction, PaymentGatewayPost paymentGatewayDirectPost ) {
		if( paymentGatewayDirectPost != null ) {
			if ( "REGISTERED".equals( paymentGatewayDirectPost.getStatus() ) ) {
				transaction.setAuthenticatedPaymentGatewayDirectPost( paymentGatewayDirectPost );
			}	
		}
	}	
}
