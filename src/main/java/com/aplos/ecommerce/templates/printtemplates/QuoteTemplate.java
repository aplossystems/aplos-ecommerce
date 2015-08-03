package com.aplos.ecommerce.templates.printtemplates;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.ecommerce.beans.Transaction;

@Entity
public class QuoteTemplate extends TransactionTemplate {
	private static final long serialVersionUID = -5362648963080827963L;
	
	public QuoteTemplate() {
	}
	
	public QuoteTemplate(Transaction transaction) {
		super(transaction);
	}
	
	@Override
	public String getName() {
		return "Quote";
	}

	@Override
	public String getTransactionTemplateFileName() {
		return "quote.xhtml";
	}

}
