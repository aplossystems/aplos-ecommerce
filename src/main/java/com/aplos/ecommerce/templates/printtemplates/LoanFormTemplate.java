package com.aplos.ecommerce.templates.printtemplates;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.ecommerce.beans.Transaction;

@Entity
public class LoanFormTemplate extends TransactionTemplate {
	private static final long serialVersionUID = -7023021523897428611L;
	
	public LoanFormTemplate() {
	}
	
	public LoanFormTemplate(Transaction transaction) {
		super(transaction);
	}
	
	@Override
	public String getName() {
		return "Loan form";
	}

	@Override
	public void setSpecificJdynamiTeKeys(JDynamiTe jDynamiTe,
			Transaction transaction) {
		super.setSpecificJdynamiTeKeys(jDynamiTe, transaction);
		jDynamiTe.setVariable("BOX_PACKAGING_IMAGE", "http://www.teletest.tv/images/packingDiagram1.jpg");
		jDynamiTe.setVariable("DISPATCH_DATE", transaction.getDispatchDateStr() );
		jDynamiTe.setVariable("LOAN_RETURN_DATE", transaction.getLoanReturnDateStr() );
	}

	@Override
	public String getTransactionTemplateFileName() {
		return "loanForm.xhtml";
	}


}
