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
import com.aplos.ecommerce.templates.printtemplates.RepairReportTemplate;

@Entity
public class RepairReportEmail extends ReturnsEmail {
	private static final long serialVersionUID = 2494307734788615306L;

	public RepairReportEmail() {
	}
	
	public String getDefaultName() {
		return "Repair report";
	}
	
	@Override
	public List<FileDetails> getAttachments(RealizedProductReturn realizedProductReturn) {
		List<FileDetails> attachments = super.getAttachments(realizedProductReturn);
		addCreatedPrintTemplate(attachments, realizedProductReturn, new RepairReportTemplate( realizedProductReturn ) );
		return attachments;
	}

	@Override
	public String getDefaultSubject() {
		return "{RETURNS_COMPANY_ID} - {TODAYS_DATE} {CUSTOMER_NAME} {CUSTOMER_COMPANY_NAME} {RETURNS_NUMBER} Repair Report";
	}

	@Override
	public String getDefaultContent() {
		return loadBodyFromFile( "returnsRepairReportEmailBody.html" );
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
		jDynamiTe.setVariable("SERIAL_NUMBER", String.valueOf( realizedProductReturn.getSerialNumber().getId() ) );
	}

	@Override
	public EmailTemplateEnum getEmailTemplateEnum() {
		return EcommerceEmailTemplateEnum.REPAIR_REPORT;
	}
	
	@Override
	public boolean isSendToAdminAlsoByDefault(
			RealizedProductReturn bulkEmailRecipient) {
		return true;
	}

}
