package com.aplos.ecommerce.beans.payment;

import java.math.BigDecimal;

import com.aplos.common.beans.PaymentGatewayPost;
import com.aplos.common.beans.paypal.PayPalConfigurationDetails;
import com.aplos.common.beans.paypal.directintegration.PayPalDirectPost;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.module.EcommerceConfiguration;
import com.aplos.ecommerce.utils.EcommerceUtil;

public class PayPalTransactionPayment extends TransactionPayment {

	@Override
	public PaymentGatewayPost createPaymentGatewayDirectPost( Transaction transaction ) {
		PayPalConfigurationDetails payPalConfigurationDetails = CommonConfiguration.getCommonConfiguration().determinePayPalCfgDetails();
		PayPalDirectPost payPalDirectPost = new PayPalDirectPost( payPalConfigurationDetails, transaction );
		payPalDirectPost.initialiseNewBean();
		payPalDirectPost.saveDetails();

		return payPalDirectPost;
	}
	
	@Override
	public PaymentGatewayPost sendOnlinePaymentRequest( Transaction transaction, boolean isPaymentMadeNow, BigDecimal partPayment, boolean isFrontEnd ) {
		PayPalDirectPost payPalDirectPost = (PayPalDirectPost) createPaymentGatewayDirectPost( transaction );
		if( partPayment != null ) {
			payPalDirectPost.setDecTotal( partPayment );
		}
		boolean isUsingPayPalExpress = EcommerceConfiguration.getEcommerceConfiguration().getPayPalPaymentMethod().equals( transaction.getPaymentMethod() );
		payPalDirectPost.setDeliveryAddress(transaction.getShippingAddress());
		payPalDirectPost.setUsingPayPalExpress(isUsingPayPalExpress);
		payPalDirectPost.postRequest();

		if( !isUsingPayPalExpress ) {
			if( isFrontEnd ) {
				if( payPalDirectPost != null && !payPalDirectPost.isProcessed() ) {
					//EcommerceUtil.addAbandonmentIssueToCart( CartAbandonmentIssue.PAYMENT_ISSUE );
					JSFUtil.addMessage( payPalDirectPost.getPageError() );
					transaction.sendPaymentFailureEmail(payPalDirectPost.getPageError());
				} else {
					EcommerceUtil.getEcommerceUtil().executePaymentCompleteRoutine(transaction, true, null);
				}
			} else {
				if( !payPalDirectPost.isProcessed() ) {
					//EcommerceUtil.addAbandonmentIssueToCart( CartAbandonmentIssue.PAYMENT_ISSUE );
					JSFUtil.addMessage( payPalDirectPost.getPageError() );
				} else {
					JSFUtil.addMessage( "Payment has been made" );
					transaction.paymentComplete( false, null );
				}
			}
		}

		return payPalDirectPost;
	}
	
	@Override
	public void paymentComplete(Transaction transaction,
			PaymentGatewayPost paymentGatewayDirectPost) {
	}
}
