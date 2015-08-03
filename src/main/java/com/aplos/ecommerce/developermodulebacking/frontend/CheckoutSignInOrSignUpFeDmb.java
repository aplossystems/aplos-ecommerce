package com.aplos.ecommerce.developermodulebacking.frontend;

import javax.faces.application.FacesMessage;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;

import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.AplosUrl;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.enums.CartAbandonmentIssue;
import com.aplos.common.enums.CommonBundleKey;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.appconstants.EcommerceAppConstants;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.EcommerceShoppingCart;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.module.EcommerceConfiguration;
import com.aplos.ecommerce.utils.EcommerceUtil;

@ManagedBean
@CustomScoped(value="#{ tabSession }")
public class CheckoutSignInOrSignUpFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = 5588799219555655277L;
	private String emailAddress;
	private String password;

	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		String preSuppliedUser = JSFUtil.getRequestParameter("user");
		if ( preSuppliedUser != null && !preSuppliedUser.equals("") ) {
			emailAddress = preSuppliedUser;
		}

		Customer customer = (Customer) JSFUtil.getBeanFromScope( Customer.class );
		if( customer != null && customer.isLoggedIn() ) {
			if( customer.getShippingAddress().getContactFirstName() != null ) {
				EcommerceUtil.getEcommerceUtil().checkOrCreateOrder( true, true );
			} else {
				EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutShippingAddressCpr();
			}
		}
		return super.responsePageLoad(developerCmsAtom);
	}
	
	public boolean isShowingWeAcceptPanel() {
		return false;
	}

	public String goToNewCustomerSignUp() {
		EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutSignUpCpr();
		return null;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String logout() {
		Customer customer = (Customer) JSFUtil.getBeanFromScope( Customer.class );
		if( customer != null ) {
			customer.logout();
		}
		Transaction transaction = JSFUtil.getBeanFromScope( Transaction.class );
		if ( transaction != null ) {
			transaction.reevaluateOrderObjectsSession(true);
		}
		JSFUtil.redirect(new AplosUrl("/"),true);
		return null;
	}

	public String getPassword() {
		return password;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public String signin() {
		if (CommonConfiguration.getCommonConfiguration().isDatabaseOnHold()) {
			JSFUtil.addMessage("Sorry, you cannot currently login as we are undertaking database maintenance");
			return null;
		}
		BeanDao customerDao = new BeanDao(Customer.class)
			.addWhereCriteria( "subscriber.emailAddress=:emailAddress" )
			.addWhereCriteria( "password=:password" );
		customerDao.setNamedParameter( "emailAddress", getEmailAddress() );
		customerDao.setNamedParameter( "password", password );
		Customer customer = (Customer) customerDao.getFirstBeanResult();

		if ( !CommonUtil.validateEmailAddressFormat( getEmailAddress() ) ) {
			EcommerceUtil.getEcommerceUtil().addAbandonmentIssueToCart( CartAbandonmentIssue.INVALID_EMAIL_ADDRESS );
			JSFUtil.addMessage( "Email address is not in a valid format. Please re-enter it.", FacesMessage.SEVERITY_ERROR );
		} else if (customer == null) {
			EcommerceUtil.getEcommerceUtil().addAbandonmentIssueToCart( CartAbandonmentIssue.INCORRECT_PASSWORD );
			JSFUtil.addMessage( "Incorrect password or email address", FacesMessage.SEVERITY_ERROR );
			JSFUtil.getViewMap().put( "checkoutPasswordIncorrect", true);
		} else if ( EcommerceConfiguration.getEcommerceSettingsStatic().isCustomerEmailConfirmationRequired() && !customer.isVerified()) {
			customer.addToScope();
			JSFUtil.getViewMap().put( "emailVerificationRequired", true);
		} else if (customer.isActive()) {
			signInCustomer(customer);
		} else {
			JSFUtil.addMessage( "The account for " + getEmailAddress() + " is currently deactivated, please contact support to have this reactivated.", FacesMessage.SEVERITY_ERROR );
			EcommerceUtil.getEcommerceUtil().addAbandonmentIssueToCart( CartAbandonmentIssue.CUSTOMER_ACCOUNT_DEACTIVATED );
		}
		return null;
	}

	public void signInCustomer(Customer customer) {
		customer.login();
		customer.addToScope();
		//check if we have a valid basket, then do checkout redirect, otherwise hit the homepage
		EcommerceShoppingCart shoppingCart = EcommerceUtil.getEcommerceUtil().getOrCreateEcommerceShoppingCart();
		if (shoppingCart == null || shoppingCart.getItems() == null || shoppingCart.getItems().size() < 1) {
			EcommerceUtil.getEcommerceUtil().addCustomerToCart( customer );
			shoppingCart.recalculateAllItemDiscounts();
			String redirectLocation = JSFUtil.getRequestParameter("location");
			if ("".equals(redirectLocation)) {
				redirectLocation = null;
			}
			if (redirectLocation == null) {
				JSFUtil.redirect(new AplosUrl("/"), true);
			} else {
				JSFUtil.redirect(new AplosUrl(redirectLocation), true);
			}
			//JSFUtil.addMessage( "You are now signed in, your " + EcommerceConfiguration.getEcommerceConfiguration().getCartDisplayName() + " is empty", FacesMessage.SEVERITY_INFO );
			JSFUtil.addMessage( ApplicationUtil.getAplosContextListener().translateByKey( CommonBundleKey.NOW_SIGNED_IN ), FacesMessage.SEVERITY_INFO );
		} else {
			EcommerceUtil.getEcommerceUtil().addCustomerToCart( customer );
			shoppingCart.recalculateAllItemDiscounts();
			Transaction transaction = EcommerceUtil.getEcommerceUtil().checkOrCreateOrder( !shoppingCart.isPayPalExpressRequested(), shoppingCart.isPayPalExpressRequested() );
			transaction.updateVatExemption( true );
			if( shoppingCart.isPayPalExpressRequested() ) {
				EcommerceConfiguration.getEcommerceCprsStatic().redirectToPayPalDirectPostCpr();
			}

		}
	}

	public String goToForgottenPassword() {
		JSFUtil.addToFlashViewMap( EcommerceAppConstants.CUSTOMER_EMAIL_ADDRESS, getEmailAddress() );
		EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutForgottenPasswordCpr();
		return null;
	}

	//user is only presupplied when we know they are already registered - see newsletter for example
	public boolean isUserPreSupplied() {
		String preSuppliedUser = JSFUtil.getRequestParameter("user");
		return preSuppliedUser != null && !preSuppliedUser.equals("");
	}

	public String getSignInClass() {
		if (isUserPreSupplied()) {
			return "aplos-signin-pad-left";
		} else {
			return "aplos-signin-signup-left-half";
		}
	}
}
