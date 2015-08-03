package com.aplos.ecommerce.beans.developercmsmodules;

import com.aplos.cms.beans.developercmsmodules.ConfigurableDeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.enums.SslProtocolEnum;

@Entity
@DynamicMetaValueKey(oldKey="CHECKOUT_PAYMENT_ATOM")
public class CheckoutPaymentCmsAtom extends ConfigurableDeveloperCmsAtom {
	private static final long serialVersionUID = -9012328130118583294L;
	private boolean isShowingAddressEditBtns = true;

	@Override
	public DeveloperCmsAtom getCopy() {
		CheckoutPaymentCmsAtom checkoutPaymentCmsAtom = new CheckoutPaymentCmsAtom();
		checkoutPaymentCmsAtom.setShowingAddressEditBtns(isShowingAddressEditBtns());
		return checkoutPaymentCmsAtom;
	}

	@Override
	public String getName() {
		return "Checkout payment";
	}

	public void setShowingAddressEditBtns(boolean isShowingAddressEditBtns) {
		this.isShowingAddressEditBtns = isShowingAddressEditBtns;
	}

	public boolean isShowingAddressEditBtns() {
		return isShowingAddressEditBtns;
	}
	
	@Override
	public SslProtocolEnum getSslProtocolEnum() {
		return SslProtocolEnum.FORCE_SSL;
	}
}
