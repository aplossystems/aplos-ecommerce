package com.aplos.ecommerce.module;

import com.aplos.cms.CmsPageUrl;
import com.aplos.cms.beans.pages.CmsPageRevision;
import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.FetchType;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.Customer;

@Entity
@PluralDisplayName(name="ecommerce CMS page revisions")
public class EcommerceCmsPageRevisions extends AplosBean {
	private static final long serialVersionUID = 4912517207120887421L;

	@ManyToOne(fetch=FetchType.LAZY)
	private CmsPageRevision checkoutSignInOrSignUpCpr;
	@ManyToOne(fetch=FetchType.LAZY)
	private CmsPageRevision checkoutSignUpCpr;
	@ManyToOne(fetch=FetchType.LAZY)
	private CmsPageRevision checkoutForgottenPasswordCpr;
	@ManyToOne(fetch=FetchType.LAZY)
	private CmsPageRevision checkoutPasswordResetCpr;
	@ManyToOne(fetch=FetchType.LAZY)
	private CmsPageRevision checkoutShippingAddressCpr;
	@ManyToOne(fetch=FetchType.LAZY)
	private CmsPageRevision checkoutBillingAddressCpr;
	@ManyToOne(fetch=FetchType.LAZY)
	private CmsPageRevision checkoutPaymentCpr;
	@ManyToOne(fetch=FetchType.LAZY)
	private CmsPageRevision checkoutConfirmationCpr;
	@ManyToOne(fetch=FetchType.LAZY)
	private CmsPageRevision checkoutCartCpr;
	@ManyToOne(fetch=FetchType.LAZY)
	private CmsPageRevision checkoutPaymentAlreadyMadeCpr;
	@ManyToOne(fetch=FetchType.LAZY)
	private CmsPageRevision checkoutSuccessCpr;
	@ManyToOne(fetch=FetchType.LAZY)
	private CmsPageRevision checkoutFailureCpr;
	@ManyToOne(fetch=FetchType.LAZY)
	private CmsPageRevision checkoutAwaitingAuthorisationCpr;
	@ManyToOne(fetch=FetchType.LAZY)
	private CmsPageRevision payPalDirectPostCpr;
	@ManyToOne(fetch=FetchType.LAZY)
	private CmsPageRevision searchResultsCpr;
	@ManyToOne(fetch=FetchType.LAZY)
	private CmsPageRevision singleProductCpr;
	@ManyToOne(fetch=FetchType.LAZY)
	private CmsPageRevision productListCpr;
	@ManyToOne(fetch=FetchType.LAZY)
	private CmsPageRevision customerBillingCpr;
	@ManyToOne(fetch=FetchType.LAZY)
	private CmsPageRevision customerShippingCpr;
	@ManyToOne(fetch=FetchType.LAZY)
	private CmsPageRevision customerPasswordCpr;
	@ManyToOne(fetch=FetchType.LAZY)
	private CmsPageRevision customerOrdersCpr;
	@ManyToOne(fetch=FetchType.LAZY)
	private CmsPageRevision customerSignInCpr;
	@ManyToOne(fetch=FetchType.LAZY)
	private CmsPageRevision newsCpr;
	@ManyToOne(fetch=FetchType.LAZY)
	private CmsPageRevision newsletterSignUpCpr;
	@ManyToOne(fetch=FetchType.LAZY)
	private CmsPageRevision emailAFriendCpr;
	@ManyToOne(fetch=FetchType.LAZY)
	private CmsPageRevision offsitePayPalCpr;

	public void redirectToOffsizePayPalCpr() {
		JSFUtil.redirect( new CmsPageUrl( getOffsitePayPalCpr() ), true );
	}

	public void redirectToNewsletterSignUpCpr() {
		JSFUtil.redirect( new CmsPageUrl( getNewsletterSignUpCpr() ), true );
	}

	public void redirectToEmailAFriendCpr() {
		JSFUtil.redirect( new CmsPageUrl( getEmailAFriendCpr() ), true );
	}

	public void redirectToCheckoutSignInOrSignUpCpr() {
		JSFUtil.secureRedirect( new CmsPageUrl( getCheckoutSignInOrSignUpCpr() ), false );
	}

	public void redirectToCheckoutSignUpCpr() {
		JSFUtil.secureRedirect( new CmsPageUrl( getCheckoutSignUpCpr() ) , false );
	}

	public void redirectToCheckoutForgottenPasswordCpr() {
		JSFUtil.secureRedirect( new CmsPageUrl( getCheckoutForgottenPasswordCpr() ) , false );
	}

	public void redirectToCheckoutShippingAddressCpr() {
		JSFUtil.secureRedirect( new CmsPageUrl( getShippingAddressCpr() ), false );
	}

	public void redirectToCheckoutBillingAddressCpr() {
		JSFUtil.secureRedirect( new CmsPageUrl( getCheckoutBillingAddressCpr() ), false );
	}

	public void redirectToCheckoutPaymentCpr() {
		JSFUtil.secureRedirect( new CmsPageUrl( getCheckoutPaymentCpr() ), false );
	}

	public void redirectToCheckoutConfirmationCpr() {
		JSFUtil.secureRedirect( new CmsPageUrl( getCheckoutConfirmationCpr() ), false );
	}

	public void redirectToCheckoutCartCpr() {
		JSFUtil.secureRedirect( new CmsPageUrl( getCheckoutCartCpr() ), false );
	}

	public void redirectToCheckoutCartCprWithLoggedInCheck() {
		Customer customer = JSFUtil.getBeanFromScope(Customer.class);
		if (customer != null && customer.isLoggedIn()) {
			redirectToCheckoutCartCpr();
		} else {
			redirectToCheckoutSignInOrSignUpCpr();
		}
	}

	public void redirectToCheckoutPaymentAlreadyMadeCpr() {
		JSFUtil.secureRedirect( new CmsPageUrl( getCheckoutPaymentAlreadyMadeCpr() ), false );
	}

	public void redirectToCheckoutSuccessCpr() {
		JSFUtil.secureRedirect( new CmsPageUrl( getCheckoutSuccessCpr() ), false );
	}

	public void redirectToCheckoutFailureCpr() {
		JSFUtil.secureRedirect( new CmsPageUrl( getCheckoutFailureCpr() ), false );
	}

	public void redirectToPayPalDirectPostCpr() {
		JSFUtil.secureRedirect( new CmsPageUrl( getPayPalDirectPostCpr() ), false );
	}

	public void redirectToCustomerOrdersCpr() {
		JSFUtil.secureRedirect( new CmsPageUrl( getCustomerOrdersCpr() ), false );
	}

	public void redirectToCustomerSignInCpr() {
		JSFUtil.redirect( new CmsPageUrl( getCustomerSignInCpr() ), true );
	}

	public void redirectToCheckoutAwaitingAuthorisationCpr() {
		JSFUtil.secureRedirect( new CmsPageUrl( getCheckoutAwaitingAuthorisationCpr() ), false );
	}

	public void redirectToSearchResultsCpr() {
		JSFUtil.secureRedirect( new CmsPageUrl( getSearchResultsCpr() ), false );
	}

	public void redirectToPasswordResetCpr() {
		JSFUtil.secureRedirect( new CmsPageUrl( getCheckoutPasswordResetCpr() ), false );
	}

	public void setCheckoutSignInOrSignUpCpr(CmsPageRevision signInOrSignUpCpr) {
		this.checkoutSignInOrSignUpCpr = signInOrSignUpCpr;
	}

	public CmsPageRevision getCheckoutSignInOrSignUpCpr() {
		return checkoutSignInOrSignUpCpr;
	}

	public void setCheckoutSignUpCpr(CmsPageRevision signUpCpr) {
		this.checkoutSignUpCpr = signUpCpr;
	}

	public CmsPageRevision getCheckoutSignUpCpr() {
		return checkoutSignUpCpr;
	}

	public void setCheckoutForgottenPasswordCpr(CmsPageRevision forgottenPasswordCpr) {
		this.checkoutForgottenPasswordCpr = forgottenPasswordCpr;
	}

	public CmsPageRevision getCheckoutForgottenPasswordCpr() {
		return checkoutForgottenPasswordCpr;
	}

	public void setCheckoutShippingAddressCpr(CmsPageRevision shippingAddressCpr) {
		this.checkoutShippingAddressCpr = shippingAddressCpr;
	}

	public CmsPageRevision getShippingAddressCpr() {
		return getCheckoutShippingAddressCpr();
	}

	public void setCheckoutBillingAddressCpr(CmsPageRevision billingAddressCpr) {
		this.checkoutBillingAddressCpr = billingAddressCpr;
	}

	public CmsPageRevision getCheckoutBillingAddressCpr() {
		return checkoutBillingAddressCpr;
	}

	public void setCheckoutPaymentCpr(CmsPageRevision paymentCpr) {
		this.checkoutPaymentCpr = paymentCpr;
	}

	public CmsPageRevision getCheckoutPaymentCpr() {
		return checkoutPaymentCpr;
	}

	public void setCheckoutConfirmationCpr(CmsPageRevision confirmationCpr) {
		this.checkoutConfirmationCpr = confirmationCpr;
	}

	public CmsPageRevision getCheckoutConfirmationCpr() {
		return checkoutConfirmationCpr;
	}

	public void setCheckoutCartCpr(CmsPageRevision checkoutCartCpr) {
		this.checkoutCartCpr = checkoutCartCpr;
	}

	public CmsPageRevision getCheckoutCartCpr() {
		return checkoutCartCpr;
	}

	public void setCheckoutPaymentAlreadyMadeCpr(
			CmsPageRevision checkoutPaymentAlreadyMadeCpr) {
		this.checkoutPaymentAlreadyMadeCpr = checkoutPaymentAlreadyMadeCpr;
	}

	public CmsPageRevision getCheckoutPaymentAlreadyMadeCpr() {
		return checkoutPaymentAlreadyMadeCpr;
	}

	public void setCheckoutSuccessCpr(CmsPageRevision checkoutSuccessCpr) {
		this.checkoutSuccessCpr = checkoutSuccessCpr;
	}

	public CmsPageRevision getCheckoutSuccessCpr() {
		return checkoutSuccessCpr;
	}

	public void setCheckoutAwaitingAuthorisationCpr(
			CmsPageRevision checkoutAwaitingAuthorisationCpr) {
		this.checkoutAwaitingAuthorisationCpr = checkoutAwaitingAuthorisationCpr;
	}

	public CmsPageRevision getCheckoutAwaitingAuthorisationCpr() {
		return checkoutAwaitingAuthorisationCpr;
	}

	public void setSearchResultsCpr(CmsPageRevision searchResultsCpr) {
		this.searchResultsCpr = searchResultsCpr;
	}

	public CmsPageRevision getSearchResultsCpr() {
		return searchResultsCpr;
	}

	public CmsPageRevision getCheckoutShippingAddressCpr() {
		return checkoutShippingAddressCpr;
	}

	public void setCheckoutPasswordResetCpr(CmsPageRevision checkoutPasswordResetCpr) {
		this.checkoutPasswordResetCpr = checkoutPasswordResetCpr;
	}

	public CmsPageRevision getCheckoutPasswordResetCpr() {
		return checkoutPasswordResetCpr;
	}

	public void setSingleProductCpr(CmsPageRevision singleProductCpr) {
		this.singleProductCpr = singleProductCpr;
	}

	public CmsPageRevision getSingleProductCpr() {
		return singleProductCpr;
	}

	public void setProductListCpr(CmsPageRevision productListCpr) {
		this.productListCpr = productListCpr;
	}

	public CmsPageRevision getProductListCpr() {
		return productListCpr;
	}

	public void setCustomerSignInCpr(CmsPageRevision customerSignInCpr) {
		this.customerSignInCpr = customerSignInCpr;
	}

	public CmsPageRevision getCustomerSignInCpr() {
		return customerSignInCpr;
	}

	public void setCustomerOrdersCpr(CmsPageRevision customerOrdersCpr) {
		this.customerOrdersCpr = customerOrdersCpr;
	}

	public CmsPageRevision getCustomerOrdersCpr() {
		return customerOrdersCpr;
	}

	public void setCustomerPasswordCpr(CmsPageRevision customerPasswordCpr) {
		this.customerPasswordCpr = customerPasswordCpr;
	}

	public CmsPageRevision getCustomerPasswordCpr() {
		return customerPasswordCpr;
	}

	public void setCustomerShippingCpr(CmsPageRevision customerShippingCpr) {
		this.customerShippingCpr = customerShippingCpr;
	}

	public CmsPageRevision getCustomerShippingCpr() {
		return customerShippingCpr;
	}

	public void setCustomerBillingCpr(CmsPageRevision customerBillingCpr) {
		this.customerBillingCpr = customerBillingCpr;
	}

	public CmsPageRevision getCustomerBillingCpr() {
		return customerBillingCpr;
	}

	public CmsPageRevision getPayPalDirectPostCpr() {
		return payPalDirectPostCpr;
	}

	public void setPayPalDirectPostCpr(CmsPageRevision payPalDirectPostCpr) {
		this.payPalDirectPostCpr = payPalDirectPostCpr;
	}

	public CmsPageRevision getCheckoutFailureCpr() {
		return checkoutFailureCpr;
	}

	public void setCheckoutFailureCpr(CmsPageRevision checkoutFailureCpr) {
		this.checkoutFailureCpr = checkoutFailureCpr;
	}

	public CmsPageRevision getNewsletterSignUpCpr() {
		return newsletterSignUpCpr;
	}

	public void setNewsletterSignUpCpr(CmsPageRevision newsletterSignUpCpr) {
		this.newsletterSignUpCpr = newsletterSignUpCpr;
	}

	public CmsPageRevision getEmailAFriendCpr() {
		return emailAFriendCpr;
	}

	public void setEmailAFriendCpr(CmsPageRevision emailAFriendCpr) {
		this.emailAFriendCpr = emailAFriendCpr;
	}

	public CmsPageRevision getOffsitePayPalCpr() {
		return offsitePayPalCpr;
	}

	public void setOffsitePayPalCpr(CmsPageRevision offsitePayPalCpr) {
		this.offsitePayPalCpr = offsitePayPalCpr;
	}

	public CmsPageRevision getNewsCpr() {
		return newsCpr;
	}

	public void setNewsCpr(CmsPageRevision newsCpr) {
		this.newsCpr = newsCpr;
	}
}
