package com.aplos.ecommerce.beans.payment;

import java.math.BigDecimal;

import com.aplos.common.beans.PaymentGatewayPost;
import com.aplos.ecommerce.beans.Transaction;

public abstract class TransactionPayment {
	public abstract PaymentGatewayPost createPaymentGatewayDirectPost( Transaction transaction );
	public abstract PaymentGatewayPost sendOnlinePaymentRequest( Transaction transaction, boolean isPaymentMadeNow, BigDecimal partPayment, boolean isFrontEnd );
	public abstract void paymentComplete( Transaction transaction, PaymentGatewayPost paymentGatewayDirectPost );
}
