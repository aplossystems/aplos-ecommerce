package com.aplos.ecommerce.templates.printtemplates;

import java.util.Date;
import java.util.Map;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.MappedSuperclass;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.CompanyDetails;
import com.aplos.common.beans.CreatedPrintTemplate;
import com.aplos.common.interfaces.AplosWorkingDirectoryInter;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.templates.PrintTemplate;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.RealizedProductReturn;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.enums.EcommerceWorkingDirectory;

@MappedSuperclass
public abstract class ReturnsTemplate extends PrintTemplate {
	private static final long serialVersionUID = 8692520269394588325L;
	@ManyToOne
	private RealizedProductReturn realizedProductReturn;

	public ReturnsTemplate() {
	}

	public ReturnsTemplate( RealizedProductReturn realizedProductReturn ) {
		setRealizedProductReturn(realizedProductReturn);
	}
	
	@Override
	public void initialise(Map<String, String[]> params ) {
		Long realizedProductReturnId = Long.parseLong( params.get( "realizedProductReturnId" )[ 0 ] );
		setRealizedProductReturn( (RealizedProductReturn) new BeanDao( RealizedProductReturn.class ).get( realizedProductReturnId ) );
	}

	public abstract String getName();

	protected static void populateReturnsHeader( JDynamiTe jDynamiTe, RealizedProductReturn realizedProductReturn, String contextPath ) {
		jDynamiTe.setVariable("TELETEST_LOGO", "http://www.myro.com/images/teletest_logo.png");

		CompanyDetails companyDetails = CommonConfiguration.getCommonConfiguration().getDefaultCompanyDetails();

		jDynamiTe.setVariable("COMPANY_LOGO", contextPath + "/images/logo.jpg" );
		jDynamiTe.setVariable("COMPANY_NAME", CommonUtil.getStringOrEmpty(companyDetails.getCompanyName()));
		jDynamiTe.setVariable("COMPANY_ADDRESS_1", CommonUtil.getStringOrEmpty(companyDetails.getAddress().getLine1()) );
		if (companyDetails.getAddress().getLine2()!= null && !companyDetails.getAddress().getLine2().equals("")) {
			jDynamiTe.setVariable("COMPANY_ADDRESS_2", ", " + companyDetails.getAddress().getLine2());
		} else {
			jDynamiTe.setVariable("COMPANY_ADDRESS_2", "");
		}
		if (companyDetails.getAddress().getLine3()!= null && !companyDetails.getAddress().getLine3().equals("")) {
			jDynamiTe.setVariable("COMPANY_ADDRESS_3", ", " + companyDetails.getAddress().getLine3());
		} else {
			jDynamiTe.setVariable("COMPANY_ADDRESS_3", "");
		}

		jDynamiTe.setVariable("SITE_COMPANY_NAME", companyDetails.getCompanyName() );
		jDynamiTe.setVariable("SITE_COMPANY_ADDRESS", companyDetails.getAddress().toHtmlFull( "", false ));
		if( companyDetails.getAddress().getCountry() != null ) {
			jDynamiTe.setVariable("SITE_COMPANY_COUNTRY", CommonUtil.getStringOrEmpty(companyDetails.getAddress()
					.getCountry().getDisplayName()));
		}

		jDynamiTe.setVariable("COMPANY_TOWN", CommonUtil.getStringOrEmpty(companyDetails.getAddress().getCity()));
		jDynamiTe.setVariable("COMPANY_COUNTY", CommonUtil.getStringOrEmpty(companyDetails.getAddress().getState()));
		jDynamiTe.setVariable("COMPANY_POSTCODE", CommonUtil.getStringOrEmpty(companyDetails.getAddress().getPostcode()));
		if( companyDetails.getAddress().getCountry() != null ) {
			jDynamiTe.setVariable("COMPANY_COUNTRY", CommonUtil.getStringOrEmpty(companyDetails.getAddress().getCountry().getDisplayName()));
		}
		jDynamiTe.setVariable("COMPANY_PHONE", CommonUtil.getStringOrEmpty(companyDetails.getAddress().getPhone()));
		jDynamiTe.setVariable("COMPANY_FAX", CommonUtil.getStringOrEmpty(companyDetails.getAddress().getFax()));
		if ( companyDetails.getAddress().getPhone() != null ) {
			if( companyDetails.getAddress().getPhone() != null && companyDetails.getAddress().getPhone().length() > 1 ) {
				jDynamiTe.setVariable("COMPANY_INTERNATIONAL_PHONE", CommonUtil.getStringOrEmpty(companyDetails.getAddress().getPhone().substring(1, companyDetails.getAddress().getPhone().length())));;
			}
		}
		else {
			jDynamiTe.setVariable("COMPANY_INTERNATIONAL_PHONE", "");
		}
		jDynamiTe.setVariable("COMPANY_EMAIL", CommonUtil.getStringOrEmpty(companyDetails.getAddress().getEmailAddress()));
		jDynamiTe.setVariable("COMPANY_WEB", CommonUtil.getStringOrEmpty(companyDetails.getWeb()));
		jDynamiTe.setVariable("COMPANY_VATNO", CommonUtil.getStringOrEmpty(companyDetails.getVatNo()));
		jDynamiTe.setVariable("COMPANY_DIRECTOR", CommonUtil.getStringOrEmpty(companyDetails.getDirector()));
		jDynamiTe.setVariable("COMPANY_REGNO", CommonUtil.getStringOrEmpty(companyDetails.getRegNo()));
		if ( realizedProductReturn.getReturnAddress() != null) {
			jDynamiTe.setVariable("RETURNS_CONTACT_FULL_NAME", CommonUtil.getStringOrEmpty( realizedProductReturn.getReturnAddress().getContactFullName() ));
			jDynamiTe.setVariable("RETURNS_ADDRESS", realizedProductReturn.getReturnAddress().toHtmlFull( "", false ) );
			jDynamiTe.setVariable("RETURNS_COMPANY", realizedProductReturn.getReturnAddress().getCompanyName() );
			if( realizedProductReturn.getReturnAddress().getCountry() != null ) {
				jDynamiTe.setVariable("RETURNS_COUNTRY", CommonUtil.getStringOrEmpty(realizedProductReturn.getReturnAddress().getCountry().getDisplayName()));
			}

			jDynamiTe.setVariable("RETURNS_PHONE", CommonUtil.getStringOrEmpty( realizedProductReturn.getReturnAddress().getPhone() ));
			jDynamiTe.setVariable("RETURNS_MOBILE", CommonUtil.getStringOrEmpty( realizedProductReturn.getReturnAddress().getMobile() ));
			jDynamiTe.setVariable("RETURNS_FAX", CommonUtil.getStringOrEmpty( realizedProductReturn.getReturnAddress().getFax() ));
			jDynamiTe.setVariable("RETURNS_EMAIL", CommonUtil.getStringOrEmpty( realizedProductReturn.getReturnAddress().getEmailAddress() ));
		}


		jDynamiTe.setVariable("RMA_ISSUE_DATE", FormatUtil.formatDate( realizedProductReturn.getDateCreated() ) );

		jDynamiTe.setVariable("RETURNS_ID", "RMA" + realizedProductReturn.getId().toString() );
		jDynamiTe.setVariable("CONTACT_ID", realizedProductReturn.determineEndCustomer().getId().toString());

		if( realizedProductReturn.getSerialNumber() != null ) {
			jDynamiTe.setVariable("SERIAL_NUMBER", String.valueOf( realizedProductReturn.getSerialNumber().getId() ) );
			if( realizedProductReturn.getSerialNumber().getProductVersion() != null ) {
				jDynamiTe.setVariable("PRODUCT_VERSION", CommonUtil.getStringOrEmpty( realizedProductReturn.getSerialNumber().getProductVersion().getDisplayName() ) );
			}

			Transaction transaction = realizedProductReturn.getSerialNumber().getAssociatedTransaction();
			if( transaction != null ) {
				jDynamiTe.setVariable("ORDER_ID", CommonUtil.getStringOrEmpty( transaction.getInvoiceNumber() ) );
				jDynamiTe.setVariable("TRANSACTION_ID", String.valueOf( realizedProductReturn.getSerialNumber().getAssociatedTransaction().getId() ) );
				if( transaction.getFirstInvoicedDate() != null ) {
					jDynamiTe.setVariable("INVOICE_DATE", FormatUtil.formatDate( transaction.getFirstInvoicedDate() ) );
				} else {
					jDynamiTe.setVariable("INVOICE_DATE", FormatUtil.formatDate( new Date() ) );
				}
			}
		}
		jDynamiTe.setVariable("PART_NUMBER", realizedProductReturn.determineReturnProduct().determineItemCode());
	}

	public void redirectToPdf( RealizedProductReturn realizedProductReturn, CreatedPrintTemplate createdPrintTemplate ) {
		try {
			realizedProductReturn.saveDetails();

			JSFUtil.redirect( createdPrintTemplate.getAplosUrl() );
		}
		catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	@Override
	public AplosWorkingDirectoryInter getAplosWorkingDirectoryInter() {
		return EcommerceWorkingDirectory.REALIZED_PRODUCT_RETURN_PDFS_DIR;
	}

	public RealizedProductReturn getRealizedProductReturn() {
		return realizedProductReturn;
	}

	public void setRealizedProductReturn(RealizedProductReturn realizedProductReturn) {
		this.realizedProductReturn = realizedProductReturn;
	}
}
