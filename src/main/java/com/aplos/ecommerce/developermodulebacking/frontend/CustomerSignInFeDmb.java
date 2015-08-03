package com.aplos.ecommerce.developermodulebacking.frontend;

import javax.faces.application.FacesMessage;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;

import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.ShoppingCart;
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
public class CustomerSignInFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = 2113119857751859909L;
	private String password;
	private String email;

	@Override
	public boolean genericPageLoad(DeveloperCmsAtom developerCmsAtom) {
		super.genericPageLoad(developerCmsAtom);
		Customer sessionCustomer = JSFUtil.getBeanFromScope( Customer.class );
		if ( sessionCustomer != null && sessionCustomer.isLoggedIn() ) {
			EcommerceConfiguration.getEcommerceCprsStatic().redirectToCustomerOrdersCpr();
		}
		return true;
	}

	public String goToOrders() {
		if (CommonUtil.validateEmailAddressFormat(email)) {
			Customer customer = (Customer) new BeanDao(Customer.class)
			.addWhereCriteria( "subscriber.emailAddress='" + email + "'" )
			.addWhereCriteria( "password='" + password + "'" ).getFirstResult();
			if (customer == null) {
				JSFUtil.addMessage( "Incorrect password for this email address", FacesMessage.SEVERITY_ERROR );
			} else {
				customer.login();
				customer.addToScope();
				EcommerceUtil.getEcommerceUtil().addCustomerToCart( customer ); //creates the cart if needed
				EcommerceShoppingCart shoppingCart = (EcommerceShoppingCart) JSFUtil.getBeanFromScope( ShoppingCart.class );
				shoppingCart.recalculateAllItemDiscounts(); //if the cart existed before we added our customer
				Transaction transaction = EcommerceUtil.getEcommerceUtil().checkOrCreateOrder( !shoppingCart.isPayPalExpressRequested(), false );
				transaction.updateVatExemption( true );
				EcommerceConfiguration.getEcommerceCprsStatic().redirectToCustomerOrdersCpr();
			}
		} else {
			JSFUtil.addMessage( "Email address is not in a valid format. Please re-enter it.", FacesMessage.SEVERITY_ERROR );
		}
		return null;
	}
	public String goToForgottenPassword() {
		JSFUtil.addToFlashViewMap( EcommerceAppConstants.CUSTOMER_EMAIL_ADDRESS, email );
		EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutForgottenPasswordCpr();
		return null;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
