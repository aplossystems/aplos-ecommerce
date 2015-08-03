package com.aplos.ecommerce.backingpage.transaction;

import java.util.ArrayList;

import com.aplos.cms.beans.pages.CmsPageRevision;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.MenuStep;
import com.aplos.common.beans.MenuWizard;
import com.aplos.common.enums.CommonBundleKey;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.enums.EcommerceBundleKey;
import com.aplos.ecommerce.module.EcommerceCmsPageRevisions;
import com.aplos.ecommerce.module.EcommerceConfiguration;

@Entity
public class TransactionFrontEndMenuWizard extends MenuWizard {
	private static final long serialVersionUID = 5769912290610233165L;

	@Override
	protected void createMenuSteps() {
		setMenuSteps( new ArrayList<MenuStep>() );
		int idx = 0;

		EcommerceCmsPageRevisions ecommerceCprs = EcommerceConfiguration.getEcommerceCprsStatic();
//		getMenuSteps().add( new TransactionBackEndMenuStep( this, idx++, "Cart", "transactionEditAddItems", TransactionEditAddItemsPage.class ) );
		getMenuSteps().add( new TransactionFrontEndMenuStep( this, idx++, ApplicationUtil.getAplosContextListener().translateByKey(EcommerceBundleKey.DELIVERY_ADDRESS), getFullMapping( ecommerceCprs.getCheckoutShippingAddressCpr() ), null ));
		getMenuSteps().add( new TransactionFrontEndMenuStep( this, idx++, ApplicationUtil.getAplosContextListener().translateByKey(EcommerceBundleKey.BILLING_ADDRESS), getFullMapping( ecommerceCprs.getCheckoutBillingAddressCpr() ), null ));
		getMenuSteps().add( new TransactionFrontEndMenuStep( this, idx++, ApplicationUtil.getAplosContextListener().translateByKey(CommonBundleKey.PAYMENT), getFullMapping( ecommerceCprs.getCheckoutPaymentCpr() ), null ));
		getMenuSteps().add( new TransactionFrontEndMenuStep( this, idx++, ApplicationUtil.getAplosContextListener().translateByKey(EcommerceBundleKey.CONFIRMATION), getFullMapping( ecommerceCprs.getCheckoutConfirmationCpr() ), null ));
	}

	public String getFullMapping( CmsPageRevision cmsPageRevision ) {
		return JSFUtil.getContextPath() + cmsPageRevision.getCmsPage().getFullMapping(true) + ".aplos";
	}
}
