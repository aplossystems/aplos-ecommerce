package com.aplos.ecommerce.backingpage.customer;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.annotations.GlobalAccess;
import com.aplos.common.annotations.WindowId;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.BackingPage;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.module.EcommerceConfiguration;

@ManagedBean
@ViewScoped
@GlobalAccess
@WindowId(required=false)
public class CustomerEmailConfirmationPage extends BackingPage {

	private static final long serialVersionUID = 2532206077176999156L;

	public CustomerEmailConfirmationPage() {}

	@Override
	public boolean responsePageLoad() {
		try {
			Long customerId = Long.parseLong(JSFUtil.getRequest().getParameter("cid"));
			if( customerId != null ) {
				String verifcationCode = JSFUtil.getRequest().getParameter("code");
				if (verifcationCode != null && !verifcationCode.equals("")) {
					Customer customer = (Customer) new BeanDao(Customer.class).get(customerId);
					if (customer != null) {
						if( !customer.isVerified() ) {
							if (customer.getEmailVerificationCode() != null && verifcationCode != null && customer.getEmailVerificationCode().equals(verifcationCode)) {
								customer.putTemporaryTransactionInSession();
								customer.setVerified( true );
								customer.saveDetails();
								customer.addToScope();
								JSFUtil.addMessage( customer.getSubscriber().getEmailAddress() + " has been verified." );
							} else {
								JSFUtil.addMessage( "This link does not contain the correct information for a confirmation, please try again" );
							}
						} else {
							JSFUtil.addMessage( customer.getSubscriber().getEmailAddress() + " is already verified" );
						}
					} else {
						JSFUtil.addMessage( "The customer for this link does not exist" );
					}
				} else {
					JSFUtil.addMessage( "This link does not contain the correct information for a confirmation, please try again" );
				}
			} else {
				JSFUtil.addMessage( "This link does not contain the correct information for a confirmation, please try again" );
			}
		} catch (Exception e) {
			e.printStackTrace();
			JSFUtil.addMessage( "This link does not contain the correct information for a confirmation, please try again" );
		}
		EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutSignInOrSignUpCpr();
		
		return true;
	}
}






















