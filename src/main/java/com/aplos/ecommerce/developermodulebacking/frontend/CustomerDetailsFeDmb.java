package com.aplos.ecommerce.developermodulebacking.frontend;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.Customer;

@ManagedBean
@ViewScoped
public class CustomerDetailsFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = 505220354900568454L;

	public void saveDetails() {
		Customer customer = JSFUtil.getBeanFromScope( Customer.class );
		customer.saveDetails();
		JSFUtil.addMessage( "Your details have been saved", FacesMessage.SEVERITY_INFO );
	}

}
