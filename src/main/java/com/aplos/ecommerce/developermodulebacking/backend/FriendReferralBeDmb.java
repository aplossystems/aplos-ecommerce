package com.aplos.ecommerce.developermodulebacking.backend;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.developercmsmodules.FriendReferralModule;

@ManagedBean
@ViewScoped
public class FriendReferralBeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = -8006343899582368297L;
	private FriendReferralModule friendReferralModule;
	private String newTermOrCondition;

	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		super.responsePageLoad(developerCmsAtom);
		setFriendReferralModule((FriendReferralModule) developerCmsAtom);
		return true;
	}

	public void setFriendReferralModule(FriendReferralModule friendReferralModule) {
		this.friendReferralModule = friendReferralModule;
	}

	public FriendReferralModule getFriendReferralModule() {
		return friendReferralModule;
	}

	public SelectItem[] getPaymentMethodSelectItems() {
		SelectItem[] items = new SelectItem[2];
		items[0] = new SelectItem(false, "Pay a fixed amount");
		items[1] = new SelectItem(true, "Pay a percentage of the referees first order");
		return items;
	}

	public String getCreditPayoutLabelText() {
		if (friendReferralModule.isReferralPayoutPercentage()) {
			return "Store credid per referral (percentage)";
		} else {
			return "Store credid per referral (fixed)";
		}
	}

	public String addTermOrCondition() {
		if ( CommonUtil.getStringOrEmpty( getNewTermOrCondition() ).equals("") ) {
			JSFUtil.addMessageForError("Please enter valid text for this term or condition");
		} else {
			friendReferralModule.addTermOrCondion(getNewTermOrCondition());
		}
		return null;
	}

	public String removeTermOrCondition() {
		String bullet = (String) JSFUtil.getRequest().getAttribute("tableBean");
		friendReferralModule.getTermsAndConditionsList().remove(bullet);
		return null;
	}

	public void setNewTermOrCondition(String newTermOrCondition) {
		this.newTermOrCondition = newTermOrCondition;
	}

	public String getNewTermOrCondition() {
		return newTermOrCondition;
	}

}
