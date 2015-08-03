package com.aplos.ecommerce.templates.printtemplates;

import java.util.Date;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.utils.FormatUtil;
import com.aplos.ecommerce.beans.Transaction;

@Entity
public class TransactionAcknowledgementTemplate extends TransactionTemplate {
	private static final long serialVersionUID = 5419450300792489050L;
	
	public TransactionAcknowledgementTemplate() {
	}
	
	public TransactionAcknowledgementTemplate( Transaction transaction ) {
		super( transaction );
	}
	
	public String getName() {
		return "Acknowledgement";
	}

	@Override
	public void setSpecificJdynamiTeKeys(JDynamiTe jDynamiTe, Transaction transaction ) {
		super.setSpecificJdynamiTeKeys(jDynamiTe, transaction);
		jDynamiTe.setVariable( "ACKNOWLEDGEMENT_DATE", FormatUtil.formatDate( new Date() ) );
	}

	@Override
	public String getTransactionTemplateFileName() {
		return "transactionAcknowledgement.html";
	}
}