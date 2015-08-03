package com.aplos.ecommerce.backingpage.payments.paypal;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import com.aplos.common.annotations.BackingPageOverride;
import com.aplos.common.annotations.FrontendBackingPage;
import com.aplos.common.annotations.GlobalAccess;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.payments.paypal.PayPalIpnListenerPage;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.ecommerce.beans.Transaction;

@ManagedBean
@RequestScoped
@GlobalAccess
@FrontendBackingPage
@BackingPageOverride(backingPageClass=PayPalIpnListenerPage.class)
public class EcommercePayPalIpnListenerPage extends PayPalIpnListenerPage{
	private static final long serialVersionUID = 7579580730339554175L;

	public EcommercePayPalIpnListenerPage() {
	}
	
	@Override
	public void verify( String txnId ) {
		Transaction transaction = new BeanDao( Transaction.class ).get( Long.parseLong( txnId ) );
		if( !transaction.isFullyPaid() ) {
			// no need to redirect just need the object to be updated.
			// Put the first website into memory so that emails can be loaded.
			ApplicationUtil.getAplosContextListener().getWebsiteList().get( 0 ).markAsCurrentWebsite();
			transaction.executePaymentCompleteRoutine(false, null);
		}
	}
}
