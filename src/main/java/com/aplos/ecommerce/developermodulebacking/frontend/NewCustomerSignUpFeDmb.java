package com.aplos.ecommerce.developermodulebacking.frontend;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.AplosUrl;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.Subscriber;
import com.aplos.common.beans.communication.AplosEmail;
import com.aplos.common.enums.CartAbandonmentIssue;
import com.aplos.common.enums.SubscriptionHook;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.appconstants.EcommerceAppConstants;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.FriendReferral;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;
import com.aplos.ecommerce.module.EcommerceConfiguration;
import com.aplos.ecommerce.utils.EcommerceUtil;

@ManagedBean
@ViewScoped //changed from session
public class NewCustomerSignUpFeDmb extends DeveloperModuleBacking {

	private static final long serialVersionUID = 5600997601615078333L;

	private Date dateOfBirth;
	private Boolean overSixteen = true;
	private boolean hasPermission = false; //only matters if overSixteen == false
	private boolean subscribe = true;

	private String firstName;
	private String surname;
	private String emailAddress;
	private String password;
	private String passwordConfirm;

	private String referralCode;
	
	private boolean isGoToCheckout = false;
	
	public NewCustomerSignUpFeDmb() {
		Calendar cal = new GregorianCalendar();
		cal.setTime( new Date() );
		cal.add( Calendar.YEAR, -25 );
		dateOfBirth = cal.getTime();
	}
	
	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {

		String isGoToCheckoutStr = JSFUtil.getRequestParameter(EcommerceAppConstants.GO_TO_CHECKOUT);
		if ( isGoToCheckoutStr != null && isGoToCheckoutStr.equals("true") ) {
			setGoToCheckout(true);
		}
		return super.responsePageLoad(developerCmsAtom);
	}
	
	public String getSignupBtnText() {
		if( isGoToCheckout() ) {
			return "Go to shipping address";
		} else {
			return "Register";
		}
	}

	public String goToForgottenPassword() {
		JSFUtil.addToFlashViewMap( EcommerceAppConstants.CUSTOMER_EMAIL_ADDRESS, getEmailAddress() );
		EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutForgottenPasswordCpr();
		return null;
	}

	public String goToSignIn() {
		EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutSignInOrSignUpCpr();
		return null;
	}

	public String signup() {
		if (CommonConfiguration.getCommonConfiguration().isDatabaseOnHold()) {
			JSFUtil.addMessage("Sorry, you cannot currently sign up as we are undertaking database maintenance");
			return null;
		}
		//overSixteen will always be true unless we provide an interface which can set it false
		//so this wont be a problem in projects not implementing it, the first if statement will
		//effectively be ignored - its important to use the field and not the getter here, for
		//that reason as the getter will dynamically calculate the answer
		if (!overSixteen && !hasPermission) {
			EcommerceUtil.getEcommerceUtil().addAbandonmentIssueToCart( CartAbandonmentIssue.UNDERAGE );
			JSFUtil.addMessage( "Sorry, you can't proceed without your parent/guardian's permission if you are under 16.", FacesMessage.SEVERITY_ERROR );
		} else if ( !CommonUtil.validateEmailAddressFormat(getEmailAddress()) ) {
			EcommerceUtil.getEcommerceUtil().addAbandonmentIssueToCart( CartAbandonmentIssue.INVALID_EMAIL_ADDRESS );
			JSFUtil.addMessage( "Your Email address is not in a valid format. Please re-enter it.", FacesMessage.SEVERITY_ERROR );
		} else if ( getEmailAddress() == null || getEmailAddress().equals( "" ) ) {
			EcommerceUtil.getEcommerceUtil().addAbandonmentIssueToCart( CartAbandonmentIssue.NO_EMAIL_ADDRESS );
			JSFUtil.addMessage( "Please enter an email address", FacesMessage.SEVERITY_ERROR );
		} else {
			BeanDao customerDao = new BeanDao(Customer.class)
			.addWhereCriteria( "bean.subscriber.emailAddress=:emailAddress" );
			customerDao.setNamedParameter( "emailAddress", getEmailAddress() );

			int customerCount = customerDao.getCountAll();

			if ( customerCount > 0 ) {
				EcommerceUtil.getEcommerceUtil().addAbandonmentIssueToCart( CartAbandonmentIssue.EMAIL_ADDRESS_TAKEN );
				JSFUtil.addMessage( getEmailAddress() + " is already associated with an account", FacesMessage.SEVERITY_ERROR );
				JSFUtil.getViewMap().put( "emailAlreadyInUse", true );
			} else if( getPassword() == null || getPassword().equals( "" ) ) {
				EcommerceUtil.getEcommerceUtil().addAbandonmentIssueToCart( CartAbandonmentIssue.NO_PASSWORD );
				JSFUtil.addMessage( "Please enter a password", FacesMessage.SEVERITY_ERROR );
			} else if( !getPassword().equals( getPasswordConfirm() ) ) {
				EcommerceUtil.getEcommerceUtil().addAbandonmentIssueToCart( CartAbandonmentIssue.PASSWORD_MISMATCH );
				JSFUtil.addMessage( "The passwords that you entered do not match", FacesMessage.SEVERITY_ERROR );
			} else {
				Customer customer = createNewCustomer();
				customer.setPassword( getPassword() );
				customer.getShippingAddress().setContactFirstName( firstName );
				customer.getShippingAddress().setContactSurname( surname );
				customer.getBillingAddress().setContactFirstName( firstName );
				customer.getBillingAddress().setContactSurname( surname );
				Subscriber subscriber = Subscriber.getOrCreateSubscriber( getEmailAddress() ).getSaveableBean();
				subscriber.setFirstName(firstName);
				subscriber.setSurname(surname);
				if( subscriber.isNew() ) {
					subscriber.setSubscriptionHook(SubscriptionHook.FRONTEND_ORDER);
				}
				//'subscribe' is usually true, unless we provide an interface to disable it (as in big matts)
				subscriber.setSubscribed(subscribe);
				customer.setSubscriber( subscriber );
				customerCreated( customer );
				// Need to save before adding to the cart
				customer.saveDetails();
				if (EcommerceConfiguration.getEcommerceSettingsStatic().isSendRegistrationEmails()) {
					sendSignupEmail( customer );
				}

				//add customer to friend referral so we can give credit when they place a transaction
				if (referralCode != null && !referralCode.equals("")) {
					BeanDao dao = new BeanDao(FriendReferral.class);
					dao.setWhereCriteria("bean.uniqueCode='" + referralCode + "'");
					dao.setMaxResults(1);
					FriendReferral ref = dao.setIsReturningActiveBeans(true).getFirstBeanResult();
					if (ref != null) {
						ref.setReferee(customer);
						ref.setSent(true); //just in case it was a postal address and the staff hadnt updated the system
						ref.saveDetails();
					}
				}

				EcommerceUtil.getEcommerceUtil().addCustomerToCart( customer );
				if( EcommerceConfiguration.getEcommerceSettingsStatic().isCustomerEmailConfirmationRequired() ) {
					EcommerceUtil.getEcommerceUtil().sendCustomerEmailConfirmationEmail( customer, true );
					JSFUtil.redirect(new AplosUrl("/"), true );
				} else {
					customer.login();
					customer.addToScope();

					if( EcommerceUtil.getEcommerceUtil().isBasketEmpty() ) {
						JSFUtil.addMessage( "Thank you for registering", FacesMessage.SEVERITY_INFO );
						JSFUtil.redirect(new AplosUrl("/"), true );
					} else {
						/*
						 * The transaction may have been created beforehand with all the values set
						 * so we don't want to create a new one if there's already one created.
						 */
						Transaction transaction = JSFUtil.getBeanFromScope( Transaction.class );
						if( transaction == null ) {
							getNewTransaction(customer);
						} 
						EcommerceUtil.getEcommerceUtil().redirectToCheckoutIfBasketNotEmpty(false);
					}
				}
			}
		}
		return null;
	}
	
	public Transaction getNewTransaction(Customer customer) {
		return EcommerceUtil.getEcommerceUtil().createTransaction( customer, true );
	}

	private void sendSignupEmail( Customer customer ) {
		AplosEmail.sendEmail( EcommerceEmailTemplateEnum.CUSTOMER_SIGNUP, customer );
	}

	//Hook for overriding classes to implement (like Teletest - TeletestNewCustomerSignUpFeDmb)
	public void customerCreated( Customer customer ) {}

	public Customer createNewCustomer() {
		Customer customer = new Customer();
		customer.initialiseNewBean();
		return customer;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}

	public String getPasswordConfirm() {
		return passwordConfirm;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getSurname() {
		return surname;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setHasPermission(boolean hasPermission) {
		this.hasPermission = hasPermission;
	}

	public boolean isHasPermission() {
		return hasPermission;
	}

	public void setOverSixteen(Boolean overSixteen) {
		if (overSixteen == null) {
			overSixteen = false;
		}
		this.overSixteen = overSixteen;
	}

	public Boolean getOverSixteen() {
		if (dateOfBirth != null) {
			//if dob selector is active, then work out our age dynamically
			Calendar sixteenCutoffCal = Calendar.getInstance();
			sixteenCutoffCal.add(Calendar.YEAR, -16);
			FormatUtil.resetTime( sixteenCutoffCal );
			Calendar dateOfBirthCal = Calendar.getInstance();
			dateOfBirthCal.setTime(dateOfBirth);
			FormatUtil.resetTime( dateOfBirthCal );
			overSixteen = !(dateOfBirthCal.compareTo(sixteenCutoffCal) > 0);
		}
		return overSixteen;
	}

	public void setSubscribe(boolean subscribe) {
		this.subscribe = subscribe;
	}

	public boolean isSubscribe() {
		return subscribe;
	}

	public void setReferralCode(String referralCode) {
		this.referralCode = referralCode;
	}

	public String getReferralCode() {
		return referralCode;
	}

	public boolean isGoToCheckout() {
		return isGoToCheckout;
	}

	public void setGoToCheckout(boolean isGoToCheckout) {
		this.isGoToCheckout = isGoToCheckout;
	}

}
