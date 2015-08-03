package com.aplos.ecommerce.developermodulebacking.frontend;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.utils.EcommerceUtil;

@ManagedBean
@ViewScoped
public class CustomerPasswordFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = 4801768590610046493L;
	private String password;
	private boolean isPasswordChanged = false;

	@Override
	public boolean genericPageLoad( DeveloperCmsAtom developerCmsAtom ) {
		super.genericPageLoad(developerCmsAtom);
		Customer customer = JSFUtil.getBeanFromScope(Customer.class);
		EcommerceUtil.getEcommerceUtil().checkCustomerLogin(customer, "account information" );
		return true;
	}

	public String saveChanges() {
		Customer customer = JSFUtil.getBeanFromScope(Customer.class);
		if (validatePasswordsMatch(customer)) {
			customer.saveDetails();
			setPasswordChanged( true );
			JSFUtil.addMessage("Changes have been saved.", FacesMessage.SEVERITY_INFO);
		}
		return null;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public boolean validatePasswordsMatch(Customer customer) {
		if (!password.equals(customer.getPassword())) {
			JSFUtil.addMessage("Types passwords do not match");
	      	return false;
	    } else {
	    	return true;
	    }
	}

	public void setPasswordChanged(boolean isPasswordChanged) {
		this.isPasswordChanged = isPasswordChanged;
	}

	public boolean isPasswordChanged() {
		return isPasswordChanged;
	}
}
