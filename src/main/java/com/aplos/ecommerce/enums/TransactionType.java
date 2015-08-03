package com.aplos.ecommerce.enums;

import com.aplos.common.LabeledEnumInter;
import com.aplos.common.enums.EmailTemplateEnum;

public enum TransactionType implements LabeledEnumInter {
	 PURCHASE_ORDER("Purchase order", "red", null ),
	 PRO_FORMA("Pro forma", "blue", EcommerceEmailTemplateEnum.PRO_FORMA ),
	 QUOTE("Quote", "yellow", EcommerceEmailTemplateEnum.QUOTE ),
	 LOAN("Loan form", "green", EcommerceEmailTemplateEnum.LOAN ),
	 ECOMMERCE_ORDER("Ecommerce order", "orange", EcommerceEmailTemplateEnum.INVOICE ),
	 PROMOTION ("Promotion", "pink", null ),
	 REFUND ("Refund", "cyan", null );

	 private String label;
	 private EmailTemplateEnum emailTemplateEnum;
	 private String colourCode;

	 private TransactionType(String label, String colourCode, EmailTemplateEnum emailTemplateEnum ) {
		 this.label = label;
		 this.colourCode = colourCode;
		 this.emailTemplateEnum = emailTemplateEnum;
	 }

	 @Override
	 public String getLabel() {
		 return label;
	 }
	 public String getColourCode() {
		 return colourCode;
	 }
	 public EmailTemplateEnum getEmailTemplateEnum() {
	    return emailTemplateEnum;
	 }
}