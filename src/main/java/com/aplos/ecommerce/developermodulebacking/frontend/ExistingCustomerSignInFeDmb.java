package com.aplos.ecommerce.developermodulebacking.frontend;

import javax.faces.application.FacesMessage;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;

import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.AplosUrl;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.enums.CartAbandonmentIssue;
import com.aplos.common.module.CommonConfiguration;
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
public class ExistingCustomerSignInFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = -7219395543637665880L;
	private String emailAddress;
	private String password;

	public String goToForgottenPassword() {
		JSFUtil.addToFlashViewMap( EcommerceAppConstants.CUSTOMER_EMAIL_ADDRESS, getEmailAddress() );
		EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutForgottenPasswordCpr();
		return null;
	}

	public String goToNewCustomerSignUp() {
		EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutSignUpCpr();
		return null;
	}

	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		String preSuppliedUser = JSFUtil.getRequestParameter("user");
		if ( preSuppliedUser != null && !preSuppliedUser.equals("") ) {
			emailAddress = preSuppliedUser;
		}
		return super.responsePageLoad(developerCmsAtom);
	}

	//allows us to use a generic signin page which only
	//redirects to the checkout if the basket is valid
	public String signinHomeOrCheckout() {
		return signin(false);
	}

	public boolean isUserPreSupplied() {
		String preSuppliedUser = JSFUtil.getRequestParameter("user");
		return preSuppliedUser != null && !preSuppliedUser.equals("");
	}

	public String signin() {
		return signin(true);
	}

	public String signin(boolean forcedCheckoutRedirect) {
		if (CommonConfiguration.getCommonConfiguration().isDatabaseOnHold()) {
			JSFUtil.addMessage("Sorry, you cannot currently login as we are undertaking database maintenance");
			return null;
		}
		BeanDao customerDao = new BeanDao(Customer.class)
				.addWhereCriteria( "subscriber.emailAddress=:emailAddress" )
				.addWhereCriteria( "password=:password" ).setIsReturningActiveBeans(true);

		customerDao.setNamedParameter( "emailAddress", getEmailAddress() );
		customerDao.setNamedParameter( "password", password );
		Customer customer = (Customer) customerDao.getFirstBeanResult();

		if( !CommonUtil.validateEmailAddressFormat(getEmailAddress()) ) {
			EcommerceUtil.getEcommerceUtil().addAbandonmentIssueToCart( CartAbandonmentIssue.INVALID_EMAIL_ADDRESS );
			JSFUtil.addMessage( "Email address is not in a valid format. Please re-enter it.", FacesMessage.SEVERITY_ERROR );
		} else if (customer == null) {
			EcommerceUtil.getEcommerceUtil().addAbandonmentIssueToCart( CartAbandonmentIssue.INCORRECT_PASSWORD );
			JSFUtil.addMessage( "Incorrect password for this email address", FacesMessage.SEVERITY_ERROR );
			JSFUtil.getViewMap().put( "checkoutPasswordIncorrect", true);
			return null;
		} else if( EcommerceConfiguration.getEcommerceSettingsStatic().isCustomerEmailConfirmationRequired() && !customer.isVerified()) {
			customer.addToScope();
			JSFUtil.getViewMap().put( "emailVerificationRequired", true);
		} else {
			customer.login();
			customer.addToScope();
			String redirectLocation = JSFUtil.getRequestParameter("location");
			if ("".equals(redirectLocation)) {
				redirectLocation = null;
			}
			if (forcedCheckoutRedirect && redirectLocation == null) {
				checkOrCreateOrder( true, true );
			} else {
				//check if we have a valid basket, then do checkout redirect, otherwise hit the homepage
				EcommerceShoppingCart shoppingCart = EcommerceUtil.getEcommerceUtil().getOrCreateEcommerceShoppingCart();
				if (shoppingCart == null || shoppingCart.getItems() == null || shoppingCart.getItems().size() < 1) {
					EcommerceUtil.getEcommerceUtil().addCustomerToCart( customer );
					shoppingCart.recalculateAllItemDiscounts();
					if (redirectLocation == null) {
						JSFUtil.redirect(new AplosUrl("/"), true);
						JSFUtil.addMessage( "You are now signed in, your " + EcommerceConfiguration.getEcommerceConfiguration().getCartDisplayName() + " is empty", FacesMessage.SEVERITY_INFO );
					} else {
						JSFUtil.redirect(new AplosUrl(redirectLocation), true);
					}
				} else {
					EcommerceUtil.getEcommerceUtil().addCustomerToCart( customer );
					shoppingCart.recalculateAllItemDiscounts();
					Transaction transaction = checkOrCreateOrder( !shoppingCart.isPayPalExpressRequested(), shoppingCart.isPayPalExpressRequested() );
					transaction.updateVatExemption( true );
					if( shoppingCart.isPayPalExpressRequested() ) {
						EcommerceConfiguration.getEcommerceCprsStatic().redirectToPayPalDirectPostCpr();
					}
				}
			}
		}
		return null;
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

	public static Transaction checkOrCreateOrder( boolean populateValuesFromCustomer, boolean redirectToCheckout ) {
		// There is a similar method for this in the backend, perhaps they can be merged?
		Customer customer = JSFUtil.getBeanFromScope( Customer.class );
		EcommerceUtil.getEcommerceUtil().addCustomerToCart( customer );

		Transaction transaction = JSFUtil.getBeanFromScope( Transaction.class );
		if( transaction == null || !transaction.getCustomer().getId().equals( customer.getId() ) ) {
			transaction = EcommerceUtil.getEcommerceUtil().createTransaction( customer, populateValuesFromCustomer );
		}

		if ( EcommerceUtil.getEcommerceUtil().isValidForPaymentDetailsPage( transaction ) || customer.isCompanyConnectionRequested() ) {
			CheckoutPaymentFeDmb checkoutPaymentFeDmb = (CheckoutPaymentFeDmb) JSFUtil.getTabSessionAttribute("checkoutPaymentFeDmb");
			if (checkoutPaymentFeDmb == null) {
				checkoutPaymentFeDmb = new CheckoutPaymentFeDmb();
			}
			checkoutPaymentFeDmb.setBackButtonTakesToCart(true);
			JSFUtil.addToTabSession("checkoutPaymentFeDmb", checkoutPaymentFeDmb);
			EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutPaymentCpr();
			transaction.getFrontEndMenuWizard().setLatestStepIdx( 2 );
			transaction.getFrontEndMenuWizard().setCurrentStepIdx( 2 );
		}
		else {
			EcommerceUtil.getEcommerceUtil().redirectNotValidForPaymentDetailsTransaction( true );
		}
		return transaction;
	}

	public void setPassword(String password) {
		this.password = password;
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

}
