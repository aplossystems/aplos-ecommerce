package com.aplos.ecommerce.templates.emailtemplates;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.Address;
import com.aplos.common.beans.CreatedPrintTemplate;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.beans.communication.SourceGeneratedEmailTemplate;
import com.aplos.common.interfaces.BulkEmailSource;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.templates.PrintTemplate;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.ecommerce.beans.CompanyContact;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.RealizedProductReturn;

@Entity
public abstract class ReturnsEmail extends SourceGeneratedEmailTemplate<RealizedProductReturn> {
	private static final long serialVersionUID = 4469367620127096254L;
	
	public void addCreatedPrintTemplate( List<FileDetails> attachments, RealizedProductReturn realizedProductReturn, PrintTemplate printTemplate ) {
		CreatedPrintTemplate createdPrintTemplate = new CreatedPrintTemplate( printTemplate );
		String fileDetailsName = (getId() + " - " + printTemplate.getName()).trim() + " - " + FormatUtil.formatDate( new Date() );
	
		createdPrintTemplate.setName(fileDetailsName);
		attachments.add(createdPrintTemplate);
	}

	public void addContentJDynamiTeKeys( JDynamiTe jDynamiTe, RealizedProductReturn realizedProductReturn ) {}

	@Override
	public BulkEmailSource getTestSource(SystemUser adminUser) {
		RealizedProductReturn realizedProductReturn = new RealizedProductReturn();
		Customer cachedOriginalCustomer = new Customer();
		//TODO need to set an email address without creating new subscribers in the database.
		realizedProductReturn.getSerialNumber().setCurrentCustomer(cachedOriginalCustomer);
		Address returnAddress = new Address();
		returnAddress.setContactFirstName(adminUser.getFirstName());
		returnAddress.setContactSurname(adminUser.getSurname());
		returnAddress.setLine1("Test Address");
		realizedProductReturn.setReturnAddress(returnAddress);
		realizedProductReturn.setId(88888l);
		return realizedProductReturn;
	}

	public void addSubjectJDynamiTeValues(JDynamiTe jDynamiTe,
			RealizedProductReturn realizedProductReturn) {
		super.addSubjectJDynamiTeValues(jDynamiTe, realizedProductReturn);
		Customer customer = realizedProductReturn.determineEndCustomer();
		if( customer != null ) {
			if( customer instanceof CompanyContact ) {
				jDynamiTe.setVariable("RETURNS_COMPANY_ID", String.valueOf( ((CompanyContact) customer).getCompany().getId() ) );
				jDynamiTe.setVariable("CUSTOMER_COMPANY_NAME", String.valueOf( ((CompanyContact) customer).getCompany().getCompanyName() ) );
			} else {
				jDynamiTe.setVariable("RETURNS_COMPANY_ID", String.valueOf( customer.getId() ) );
			}
		}
		jDynamiTe.setVariable("TODAYS_DATE", FormatUtil.formatDate( new Date() ) );
		jDynamiTe.setVariable("CUSTOMER_NAME", realizedProductReturn.getReturnAddress().getContactFullName() );
		jDynamiTe.setVariable("RETURNS_NUMBER", "RMA" + realizedProductReturn.getId() );	
	}

	@Override
	public void addContentJDynamiTeValues(JDynamiTe jDynamiTe,
			RealizedProductReturn realizedProductReturn) {
		super.addContentJDynamiTeValues(jDynamiTe, realizedProductReturn);
		jDynamiTe.setVariable("CUSTOMER_FIRST_NAME", CommonUtil.getStringOrEmpty( realizedProductReturn.getReturnAddress().getContactFirstName() ));
		jDynamiTe.setVariable("COMPANY_NAME", CommonUtil.getStringOrEmpty( CommonConfiguration.getCommonConfiguration().getDefaultCompanyDetails().getCompanyName() ));
		jDynamiTe.setVariable("CURRENT_USER_FIRST_NAME", CommonUtil.getStringOrEmpty( getCurrentUser().getFirstName() ) );

	}
}
