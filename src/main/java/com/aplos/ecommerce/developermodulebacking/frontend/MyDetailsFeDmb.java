package com.aplos.ecommerce.developermodulebacking.frontend;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.CompanyContact;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.developercmsmodules.MyDetailsModule;

@ManagedBean
@ViewScoped
public class MyDetailsFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = 5415294249792528489L;
	private MyDetailsModule myDetailsAtom;

	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		super.responsePageLoad(developerCmsAtom);
		setMyDetailsAtom((MyDetailsModule) developerCmsAtom);
		return true;
	}

	public void setMyDetailsAtom(MyDetailsModule myDetailsAtom) {
		this.myDetailsAtom = myDetailsAtom;
	}

	public MyDetailsModule getMyDetailsAtom() {
		return myDetailsAtom;
	}

	public Customer getUserToDetail() {
		if (myDetailsAtom.getUserToDetail() == null) {
			return JSFUtil.getBeanFromScope(Customer.class);
		}
		return myDetailsAtom.getUserToDetail();
	}

	public boolean isCompanyRelevant() {
		if (getUserToDetail() instanceof CompanyContact) {
			return true;
		}
		return false;
	}

	public String saveChanges() {
		Customer customer = getUserToDetail();
		if (!myDetailsAtom.getIncludeEmail() || CommonUtil.validateEmailAddressFormat(customer.getSubscriber().getEmailAddress())) {
			boolean login = customer.isLoggedIn();
			customer.saveDetails();
			if (login) {
				customer.login();
			}
			JSFUtil.addMessage("Changes have been saved.", FacesMessage.SEVERITY_INFO);
		} else {
			JSFUtil.addMessageForError("Changes have not been saved - Your email is not in a valid format");
		}
		return null;
	}

}
