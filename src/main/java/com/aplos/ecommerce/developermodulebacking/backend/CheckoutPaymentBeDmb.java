package com.aplos.ecommerce.developermodulebacking.backend;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.ecommerce.beans.developercmsmodules.CheckoutPaymentCmsAtom;

@ManagedBean
@ViewScoped
public class CheckoutPaymentBeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = 4934762863849133655L;
	private CheckoutPaymentCmsAtom checkoutPaymentCmsAtom;

	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		super.responsePageLoad(developerCmsAtom);
		setCheckoutPaymentCmsAtom((CheckoutPaymentCmsAtom) developerCmsAtom);
		return true;
	}

	public void setCheckoutPaymentCmsAtom(CheckoutPaymentCmsAtom checkoutPaymentCmsAtom) {
		this.checkoutPaymentCmsAtom = checkoutPaymentCmsAtom;
	}


	public CheckoutPaymentCmsAtom getCheckoutPaymentCmsAtom() {
		return checkoutPaymentCmsAtom;
	}
}
