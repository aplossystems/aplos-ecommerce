package com.aplos.ecommerce.backingpage.shipping;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import com.aplos.common.annotations.FrontendBackingPage;
import com.aplos.common.annotations.GlobalAccess;
import com.aplos.common.backingpage.payments.paypal.PayPalValidatePage;
import com.aplos.ecommerce.module.EcommerceConfiguration;

@ManagedBean
@RequestScoped
@GlobalAccess
@FrontendBackingPage
public class EcommercePayPalValidatePage extends PayPalValidatePage {
	private static final long serialVersionUID = 6108493845787456824L;

	public EcommercePayPalValidatePage() {
		super();
	}

	@Override
	public void redirectToPaymentAlreadyMade() {
		EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutPaymentAlreadyMadeCpr();
	}

	@Override
	public void redirectToPaymentFailed() {
		EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutFailureCpr();
	}

	@Override
	public void redirectToPaymentSuccessful() {
		EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutSuccessCpr();
	}


}
