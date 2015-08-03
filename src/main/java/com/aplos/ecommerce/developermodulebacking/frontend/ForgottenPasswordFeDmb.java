package com.aplos.ecommerce.developermodulebacking.frontend;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import javax.faces.application.FacesMessage;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;

import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.communication.AplosEmail;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.appconstants.EcommerceAppConstants;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;
import com.aplos.ecommerce.module.EcommerceConfiguration;

@ManagedBean
@CustomScoped(value="#{ tabSession }")
public class ForgottenPasswordFeDmb extends DeveloperModuleBacking {

	private static final long serialVersionUID = 3110724513331332837L;
	private String emailAddress;

	public ForgottenPasswordFeDmb() {}

	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		super.responsePageLoad(developerCmsAtom);
		String customerEmailAddress = (String) JSFUtil.getFromFlashViewMap( EcommerceAppConstants.CUSTOMER_EMAIL_ADDRESS );
		if( customerEmailAddress != null ) {
			emailAddress = customerEmailAddress;
		}
		return true;
	}

	public String goToNewCustomerSignUp() {
		EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutSignUpCpr();
		//JSFUtil.redirect( "new-customer-signup.aplos", true );
		return null;
	}

	public String resetEmailAddressNotRecognised() {
		JSFUtil.getFacesContext().getViewRoot().getViewMap().put( "emailAddressNotRecognised", null );
		return null;
	}

	public void resetPassword() {
		setEmailAddress( getEmailAddress().trim() );
		if (CommonUtil.validateEmailAddressFormat(getEmailAddress())) {

			BeanDao customerDao = new BeanDao(Customer.class).addWhereCriteria( "bean.subscriber.emailAddress=:emailAddress" );
			customerDao.setNamedParameter( "emailAddress", getEmailAddress() );
			customerDao.setMaxResults(1);
			Customer customer = (Customer) customerDao.getFirstBeanResult();

			if (customer != null) {
				if (customer.isActive()) {
					customer = customer.getSaveableBean();
					//create a passwordResetCode for 24 hours validity
					Random rand = new Random();
					String passwordResetCode = Long.toString( Math.abs( rand.nextLong() ), 36 );
					Calendar cal = Calendar.getInstance();
					cal.setTime( new Date() );
					cal.add(Calendar.DATE, 1);
					customer.setPasswordResetCode(passwordResetCode);
					customer.setPasswordResetDate( cal.getTime() );
					sendResetEmail(customer);
					customer.createTemporaryTransaction();
					customer.saveDetails();
					JSFUtil.getFacesContext().getViewRoot().getViewMap().put( "forgottenPasswordSent", true );
				} else {
					JSFUtil.addMessage( "The account for " + getEmailAddress() + " is currently deactivated, please contact support to have this reactivated.", FacesMessage.SEVERITY_ERROR );
					JSFUtil.getFacesContext().getViewRoot().getViewMap().put( "emailAddressNotRecognised", true );
				}
			} else {
				JSFUtil.addMessage( getEmailAddress() + " is not registered with an account.", FacesMessage.SEVERITY_ERROR );
				JSFUtil.getFacesContext().getViewRoot().getViewMap().put( "emailAddressNotRecognised", true );
			}
		} else {
			JSFUtil.addMessage( "Email address is not in a valid format. Please re-enter it.", FacesMessage.SEVERITY_ERROR );
		}
	}

	public void sendResetEmail( Customer customer) {
		AplosEmail aplosEmail = new AplosEmail( EcommerceEmailTemplateEnum.FORGOTTEN_PASSWORD, customer, customer );
		aplosEmail.sendAplosEmailToQueue();
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getEmailAddress() {
		return emailAddress;
	}
}



















