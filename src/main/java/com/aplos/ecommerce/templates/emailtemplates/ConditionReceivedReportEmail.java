package com.aplos.ecommerce.templates.emailtemplates;

import java.util.List;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.enums.EmailTemplateEnum;
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.beans.RealizedProductReturn;
import com.aplos.ecommerce.beans.product.ProductType;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;
import com.aplos.ecommerce.templates.printtemplates.ConditionReceivedReportTemplate;

@Entity
public class ConditionReceivedReportEmail extends ReturnsEmail {
	private static final long serialVersionUID = 2494307734788615306L;

	public ConditionReceivedReportEmail() {
	}
	
	public String getDefaultName() {
		return "Condition received report";
	}
	
	@Override
	public List<FileDetails> getAttachments( RealizedProductReturn realizedProductReturn) {
		List<FileDetails> attachments = super.getAttachments(realizedProductReturn);
		addCreatedPrintTemplate(attachments, realizedProductReturn, new ConditionReceivedReportTemplate( realizedProductReturn ) );
		return attachments;
	}

	@Override
	public String getDefaultSubject() {
		return "{RETURNS_COMPANY_ID} - {TODAYS_DATE} {CUSTOMER_NAME} {CUSTOMER_COMPANY_NAME} {RETURNS_NUMBER} Condition Received Report";
	}

	@Override
	public String getDefaultContent() {
		return loadBodyFromFile( "returnsConditionReceivedEmailBody.html" );
	}

	@Override
	public void addContentJDynamiTeKeys(JDynamiTe jDynamiTe, RealizedProductReturn realizedProductReturn ) {
		RealizedProduct realizedProduct = realizedProductReturn.determineReturnProduct();
		ProductType productType = null;
		if( realizedProduct.getProductInfo().getProduct().getProductTypes().size() > 0 ) {
			productType = realizedProduct.getProductInfo().getProduct().getProductTypes().get( 0 );
		}
		jDynamiTe.setVariable("ITEM_CODE", realizedProduct.getItemCode() );
		if( productType != null ) {
			jDynamiTe.setVariable("PRODUCT_CATEGORY", productType.getName() );
		}
		jDynamiTe.setVariable("PRODUCT_NAME", realizedProduct.getDisplayName() );
		if( realizedProductReturn.getSerialNumber() != null ) {
			jDynamiTe.setVariable("SERIAL_NUMBER", String.valueOf( realizedProductReturn.getSerialNumber().getId() ) );
		}
	}

	@Override
	public EmailTemplateEnum getEmailTemplateEnum() {
		return EcommerceEmailTemplateEnum.CONDITION_RECEIVED;
	}
	
	@Override
	public boolean isSendToAdminAlsoByDefault(RealizedProductReturn realizedProductReturn ) {
		return true;
	}

}
