package com.aplos.ecommerce.templates.printtemplates;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.ecommerce.beans.Transaction;

@Entity
public class ProFormaTemplate extends TransactionTemplate {
	private static final long serialVersionUID = -5249356836362581796L;
	
	public ProFormaTemplate() {
	}
	
	public ProFormaTemplate(Transaction transaction) {
		super(transaction);
	}
	
	@Override
	public String getName() {
		return "Pro Forma";
	}

	@Override
	public String getTransactionTemplateFileName() {
		return "proForma.xhtml";
	}

}
