package com.aplos.ecommerce.developermodulebacking.frontend;

import java.io.File;
import java.net.URL;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import cb.jdynamite.JDynamiTe;

import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.backingpage.BackingPage;
import com.aplos.common.beans.Address;
import com.aplos.common.beans.communication.AplosEmail;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.CatalogueRequest;
import com.aplos.ecommerce.beans.Customer;

@ManagedBean
@ViewScoped
public class RequestCatalogueFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = -8817302417873897324L;
	private CatalogueRequest catalogueRequest;
	private String firstName;
	private String surname;
	private String emailAddress;
	private Address address = new Address();
	private Customer currentCustomer;

	public RequestCatalogueFeDmb() {

	}

	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		super.responsePageLoad(developerCmsAtom);
		if( currentCustomer == null ) {
			currentCustomer = (Customer) JSFUtil.resolveVariable( Customer.class );
			if ( currentCustomer != null ) {
				if ( currentCustomer.isLoggedIn() ) {
					address = currentCustomer.determineShippingAddress();
					emailAddress = currentCustomer.getSubscriber().getEmailAddress();
					firstName = currentCustomer.getSubscriber().getFirstName();
					surname = currentCustomer.getSubscriber().getSurname();
				} else {
					currentCustomer = null;
				}
			}
		}
		return true;
	}

	public boolean isValidationRequired() {
		return BackingPage.validationRequired("requestCatalogueButton");
	}

	public String requestCatalogue() {
		Customer customer = currentCustomer;
		if (customer == null || customer.getSubscriber() == null || customer.getSubscriber().getEmailAddress() == null || !customer.getSubscriber().getEmailAddress().equals(getEmailAddress())) {
			customer = Customer.getCustomerByEmailAddress( getEmailAddress() );
			if ( customer == null || customer.getSubscriber() == null ) {
				//Subscriber subscriber = Subscriber.getSubscriberByEmailAddress( getEmailAddress() );
				customer = Customer.getCustomerOrCreate( getEmailAddress(), Customer.class );
				if ( customer.getSubscriber() != null ) {
					customer.getSubscriber().setFirstName( getFirstName() );
					customer.getSubscriber().setSurname( getSurname() );
					customer.getSubscriber().setEmailAddress( getEmailAddress() );
				}
				customer.setShippingAddress( getAddress() );
				customer.saveDetails();
			}
		}
		catalogueRequest = new CatalogueRequest();
		catalogueRequest.setCustomer( customer );
		getAddress().setEmailAddress(getEmailAddress());
		catalogueRequest.setAddress( getAddress() );
		catalogueRequest.saveDetails();

		sendRequestEmail();
		return null;
	}

	public void sendRequestEmail() {
		try {
			JDynamiTe bodyDynamiTe = new JDynamiTe();
			URL url = JSFUtil.checkFileLocations("requestCatalogue.html", "resources/templates/emailtemplates/", true );
			if (url!=null) {


				bodyDynamiTe.setInput(new File( url.toURI() ).getAbsolutePath());
				bodyDynamiTe.setVariable("ADMIN_NAME", CommonUtil.getStringOrEmpty(CommonUtil.getAdminUser().getFirstName()) + " (Admin)" );
				bodyDynamiTe.setVariable("CONTACT_NAME", catalogueRequest.getAddress().getContactFullName() );
				bodyDynamiTe.setVariable("CONTACT_EMAIL", catalogueRequest.getCustomer().getSubscriber().getEmailAddress() );
		        bodyDynamiTe.setVariable("ADDRESS_STRING", catalogueRequest.getAddress().getAddressString() );
		        bodyDynamiTe.parse();
				AplosEmail catalogeRequestEmail = new AplosEmail();
				catalogeRequestEmail.addToAddress( CommonUtil.getAdminUser().getEmail() );
				catalogeRequestEmail.setFromAddress( catalogueRequest.getCustomer().getSubscriber().getEmailAddress() );
				catalogeRequestEmail.setSubject("Catalogue Request");
				catalogeRequestEmail.setHtmlBody(bodyDynamiTe.toString());
				catalogeRequestEmail.sendAplosEmailToQueue();
				JSFUtil.addMessage( "Your request for a catalogue has been placed." );
			} else {
				JSFUtil.addMessage("Could not send request Template not found.",FacesMessage.SEVERITY_ERROR);
			}
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}

	public boolean isCatalogueAlreadyRequested() {
		return catalogueRequest != null && !catalogueRequest.isNew();
	}

	public void setCatalogueRequest(CatalogueRequest catalogueRequest) {
		this.catalogueRequest = catalogueRequest;
	}

	public CatalogueRequest getCatalogueRequest() {
		return catalogueRequest;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}
}
