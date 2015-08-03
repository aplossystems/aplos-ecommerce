package com.aplos.ecommerce.developermodulebacking.frontend;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.enums.CartAbandonmentIssue;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.utils.EcommerceUtil;

@ManagedBean
@ViewScoped
public class CustomerChangeEmailFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = -189451247764723385L;
	private String emailAddress;
	private boolean isEmailChanged = false;

	@Override
	public boolean genericPageLoad( DeveloperCmsAtom developerCmsAtom ) {
		super.genericPageLoad(developerCmsAtom);
		Customer customer = JSFUtil.getBeanFromScope(Customer.class);
		if( EcommerceUtil.getEcommerceUtil().checkCustomerLogin(customer, "account information" ) ) {
			if( emailAddress == null ) {
				emailAddress = customer.getSubscriber().getEmailAddress();
			}
		}
		return true;
	}

	public String saveChanges() {
		if (CommonConfiguration.getCommonConfiguration().isDatabaseOnHold()) {
			JSFUtil.addMessage("Sorry, you cannot currently sign up as we are undertaking database maintenance");
			return null;
		}
		if( !CommonUtil.validateEmailAddressFormat(getEmailAddress()) ) {
			EcommerceUtil.getEcommerceUtil().addAbandonmentIssueToCart( CartAbandonmentIssue.INVALID_EMAIL_ADDRESS );
			JSFUtil.addMessage( "Email address is not in a valid format. Please re-enter it.", FacesMessage.SEVERITY_ERROR );
		} else if( getEmailAddress() == null || getEmailAddress().equals( "" ) ) {
			EcommerceUtil.getEcommerceUtil().addAbandonmentIssueToCart( CartAbandonmentIssue.NO_EMAIL_ADDRESS );
			JSFUtil.addMessage( "Please enter an email address", FacesMessage.SEVERITY_ERROR );
		} else {
			BeanDao customerDao = new BeanDao(Customer.class).addWhereCriteria( "subscriber.emailAddress=:emailAddress" );
			customerDao.setNamedParameter( "emailAddress", getEmailAddress() );

			int customerCount = (Integer) customerDao.getFirstResult();

			if( customerCount > 0 ) {
				EcommerceUtil.getEcommerceUtil().addAbandonmentIssueToCart( CartAbandonmentIssue.EMAIL_ADDRESS_TAKEN );
				JSFUtil.addMessage( getEmailAddress() + " is already associated with an account", FacesMessage.SEVERITY_ERROR );
				JSFUtil.getViewMap().put( "emailAlreadyInUse", true );
			} else {
				Customer customer = JSFUtil.getBeanFromScope(Customer.class);
				customer.getSubscriber().setEmailAddress( getEmailAddress() );
				customer.saveDetails();
				setEmailChanged( true );
				JSFUtil.addMessage("Your email address has been changed", FacesMessage.SEVERITY_INFO);
			}
		}
		return null;
	}

	public void setEmailChanged(boolean isEmailChanged) {
		this.isEmailChanged = isEmailChanged;
	}

	public boolean isEmailChanged() {
		return isEmailChanged;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getEmailAddress() {
		return emailAddress;
	}
}
