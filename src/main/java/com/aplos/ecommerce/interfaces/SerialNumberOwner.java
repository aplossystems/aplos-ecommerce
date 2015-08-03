package com.aplos.ecommerce.interfaces;

import com.aplos.ecommerce.beans.Transaction;

public interface SerialNumberOwner {
	public Transaction getAssociatedTransaction();
	public Long getAssociatedTransactionId();
}
