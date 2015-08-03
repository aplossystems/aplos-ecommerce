package com.aplos.ecommerce.beans.payment;

import java.math.BigDecimal;

import com.aplos.common.backingpage.payments.cardsave.CardSaveThreeDCompletePage;
import com.aplos.common.beans.PaymentGatewayPost;
import com.aplos.common.beans.cardsave.CardSaveConfigurationDetails;
import com.aplos.common.beans.cardsave.CardSaveConfigurationDetails.CardSaveUrlType;
import com.aplos.common.beans.cardsave.CardSavePost;
import com.aplos.common.beans.cardsave.directintegration.CardSaveDirectPost;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.Transaction;

public class CardSaveTransactionPayment extends TransactionPayment {
	public PaymentGatewayPost createPaymentGatewayDirectPost( Transaction transaction ) {
		CardSaveConfigurationDetails cardSaveConfigurationDetails = CommonConfiguration.getCommonConfiguration().determineCardSaveCfgDetails();
		CardSaveDirectPost cardSaveDirectPost = new CardSaveDirectPost(); 
		cardSaveDirectPost.initialiseNewBean();
		cardSaveDirectPost.init(cardSaveConfigurationDetails, transaction);
		cardSaveDirectPost.setDeliveryCost( transaction.getEcommerceShoppingCart().getDeliveryCost() );
		cardSaveDirectPost.setDecTotal(transaction.getGrandTotal(true));
		return cardSaveDirectPost;
	}
	
	@Override
	public PaymentGatewayPost sendOnlinePaymentRequest( Transaction transaction, boolean isPaymentMadeNow, BigDecimal partPayment, boolean isFrontEnd ) {
		CardSaveDirectPost cardSaveDirectPost;
		
		if ( isPaymentMadeNow && transaction.getAuthenticatedPaymentGatewayDirectPost() != null ) {
			cardSaveDirectPost = (CardSaveDirectPost) transaction.getAuthenticatedPaymentGatewayDirectPost();
			cardSaveDirectPost.setTransactionType(com.aplos.common.beans.cardsave.CardSavePost.TransactionType.COLLECTION);
			cardSaveDirectPost.createCrossReferenceTransaction(JSFUtil.getRequest().getHeader("user-agent"), partPayment);
			cardSaveDirectPost.setRequestPostUrl(cardSaveDirectPost.getCardSaveDetails().getSystemUrl(CardSaveConfigurationDetails.IntegrationType.DIRECT, CardSaveUrlType.AUTHORISE));
			cardSaveDirectPost.postRequest();	
		} else {
			cardSaveDirectPost = (CardSaveDirectPost) createPaymentGatewayDirectPost( transaction );
			cardSaveDirectPost.setApply3dSecure(isFrontEnd);
			if( !isPaymentMadeNow ) {
				cardSaveDirectPost.setTransactionType( CardSavePost.TransactionType.PREAUTH );
			} else {
				cardSaveDirectPost.setTransactionType( CardSavePost.TransactionType.SALE );
			}
			if( partPayment != null ) {
				cardSaveDirectPost.setDecTotal( partPayment );
			}
			cardSaveDirectPost.createRequestMessage( CommonUtil.getStringOrEmpty(JSFUtil.getRequest().getHeader("user-agent")), transaction.getEcommerceShoppingCart().getItems(), transaction.determineCreditCardDetails(), transaction.getCurrency() );
			cardSaveDirectPost.saveDetails();
			transaction.saveDetails();
			cardSaveDirectPost.setRequestPostUrl( cardSaveDirectPost.getCardSaveDetails().getSystemUrl(CardSaveConfigurationDetails.IntegrationType.DIRECT, CardSaveUrlType.PURCHASE) );
			cardSaveDirectPost.postRequest();	
		
		}
		
		if( isFrontEnd ) {
			CardSaveThreeDCompletePage.redirectAfterRequestPost( transaction, cardSaveDirectPost );
		} else {
			if ( !cardSaveDirectPost.isProcessed() ) {
				//EcommerceUtil.addAbandonmentIssueToCart( CartAbandonmentIssue.PAYMENT_ISSUE );
				JSFUtil.addMessage( cardSaveDirectPost.getPageError() );
			} else {
				transaction.paymentComplete( false, cardSaveDirectPost );
				if( cardSaveDirectPost.getStatus().equals( "REGISTERED" ) ) {
					JSFUtil.addMessage( "Payment has been authorised" );
				} else {
					JSFUtil.addMessage( "Payment has been made" );
				}
			}	
		}
		return cardSaveDirectPost;
	}
	
	@Override
	public void paymentComplete(Transaction transaction, PaymentGatewayPost paymentGatewayDirectPost) {
		if( paymentGatewayDirectPost != null ) {
			if( "REGISTERED".equals( paymentGatewayDirectPost.getStatus() ) ) {
				transaction.setAuthenticatedPaymentGatewayDirectPost( paymentGatewayDirectPost );
			}
		}
	}
}
