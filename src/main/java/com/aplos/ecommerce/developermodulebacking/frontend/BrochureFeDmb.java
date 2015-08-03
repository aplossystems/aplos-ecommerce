package com.aplos.ecommerce.developermodulebacking.frontend;

import java.io.File;
import java.net.URL;

import javax.faces.application.FacesMessage;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;

import cb.jdynamite.JDynamiTe;

import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.backingpage.BackingPage;
import com.aplos.common.beans.Address;
import com.aplos.common.beans.CompanyDetails;
import com.aplos.common.beans.communication.AplosEmail;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.Company;
import com.aplos.ecommerce.beans.CompanyContact;
import com.aplos.ecommerce.beans.Customer;

@ManagedBean
@CustomScoped(value="#{ tabSession }")
public class BrochureFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = 6289122330572791371L;
	private Address newAddress = new Address();
	private String comments;
	private String position;
	private boolean isBrochureRequestSent = false;

	public boolean isValidationRequired() {
		return BackingPage.validationRequired( "sendBrochureRequest" );
	}

	public void sendBrochureByPost() {
		Customer customer;

		if( !CommonUtil.getStringOrEmpty( newAddress.getCompanyName() ).equals( "" ) ) {
			customer = Customer.getCustomerOrCreate( getNewAddress().getEmailAddress(), Customer.class );
		} else {
			customer = CompanyContact.getCompanyContactOrCreate( getNewAddress().getEmailAddress() );
		}

		boolean createCompany = false;

		if( customer.getSubscriber().isNew() ||
				(CommonUtil.getStringOrEmpty( customer.getSubscriber().getFirstName()).equals("") &&
				CommonUtil.getStringOrEmpty( customer.getSubscriber().getSurname() ).equals("")) ) {
			customer.getSubscriber().setFirstName( getNewAddress().getContactFirstName() );
			customer.getSubscriber().setSurname( getNewAddress().getContactSurname() );
		}

		if( customer.isNew() ) {
			customer.getBillingAddress().copy( getNewAddress() );

			if( customer instanceof CompanyContact ) {
				createCompany = true;
			}
		}

		if( !(customer instanceof CompanyContact) ) {
			customer = CompanyContact.convertFromCustomer( customer );
			createCompany = true;
		}

		if( createCompany ) {
			Company company = new Company();
			company.determineBillingAddress().copy( getNewAddress() );
			company.saveDetails();
			((CompanyContact) customer).setCompany( company );
		}

		customer.saveDetails();


		try {
			JDynamiTe dynamiTe = new JDynamiTe();
			URL url = JSFUtil.checkFileLocations("brochuresByPost.html", "resources/templates/emailtemplates/", true );
			if (url!=null) {
				dynamiTe.setInput(new File( url.toURI() ).getAbsolutePath());
				CompanyDetails companyDetails = CommonConfiguration.getCommonConfiguration().getDefaultCompanyDetails();
				dynamiTe.setVariable("COMPANY_NAME", companyDetails.getCompanyName() );
				if( customer instanceof CompanyContact ) {
					dynamiTe.setVariable("CUSTOMER_TYPE", "Company Contact" );
				} else {
					dynamiTe.setVariable("CUSTOMER_TYPE", "Customer" );
				}
				dynamiTe.setVariable("CUSTOMER_NAME", customer.getFullName() );
				dynamiTe.setVariable("CUSTOMER_ID", String.valueOf( customer.getId() ) );
				dynamiTe.setVariable("ADDRESS_LINE_1", CommonUtil.getStringOrEmpty( getNewAddress().getLine1() ) );
				dynamiTe.setVariable("ADDRESS_LINE_2", CommonUtil.getStringOrEmpty( getNewAddress().getLine2() ) );
				dynamiTe.setVariable("CITY", CommonUtil.getStringOrEmpty( getNewAddress().getCity() ) );
				dynamiTe.setVariable("POSTCODE", CommonUtil.getStringOrEmpty( getNewAddress().getPostcode() ) );
				dynamiTe.setVariable("COUNTRY", CommonUtil.getStringOrEmpty( getNewAddress().getCountry().getName() ) );
				dynamiTe.setVariable("EMAIL", CommonUtil.getStringOrEmpty( getNewAddress().getEmailAddress() ) );
				dynamiTe.setVariable("PHONE", CommonUtil.getStringOrEmpty( getNewAddress().getPhone() ) );
				dynamiTe.parse();
				AplosEmail mail = new AplosEmail("Brochure Request", dynamiTe.toString());
				mail.setFromAddress(CommonUtil.getAdminUser().getEmail());
				mail.addToAddress( CommonUtil.getAdminUser().getEmail() );
				mail.sendAplosEmailToQueue();
				setBrochureRequestSent(true);
				JSFUtil.addMessage("An brochure request has been sent.",FacesMessage.SEVERITY_INFO);
			} else {
				JSFUtil.addMessage("Could not request brochures, please contact a member of staff.",FacesMessage.SEVERITY_ERROR);
			}
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}

	public void resetFields() {
		setNewAddress( new Address() );
		comments = "";
		position = "";
	}

	public void setNewAddress(Address newAddress) {
		this.newAddress = newAddress;
	}
	public Address getNewAddress() {
		return newAddress;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getComments() {
		return comments;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getPosition() {
		return position;
	}

	public void setBrochureRequestSent(boolean isBrochureRequestSent) {
		this.isBrochureRequestSent = isBrochureRequestSent;
	}

	public boolean isBrochureRequestSent() {
		return isBrochureRequestSent;
	}
}
