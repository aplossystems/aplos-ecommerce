package com.aplos.ecommerce.developermodulebacking.frontend;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.backingpage.BackingPage;
import com.aplos.common.beans.Address;
import com.aplos.common.beans.communication.AplosEmail;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.FriendReferral;
import com.aplos.ecommerce.beans.developercmsmodules.FriendReferralModule;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;
import com.aplos.ecommerce.module.EcommerceConfiguration;

@ManagedBean
@ViewScoped
public class FriendReferralFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = 6407395584611818830L;
	private boolean isEmailReferral=false;
	private boolean isAddressReferral=false;
	private FriendReferral currentFriend = null;
	private List<FriendReferral> friendList = new ArrayList<FriendReferral>();

	public FriendReferralFeDmb() {
		addAnother();
	}

	public String selectEmailReferral() {
		isEmailReferral = true;
		isAddressReferral = false;
		currentFriend.getAddress().setLine1(null);
		currentFriend.getAddress().setLine2(null);
		currentFriend.getAddress().setLine3(null);
		currentFriend.getAddress().setCity(null);
		currentFriend.getAddress().setCountry(null);
		currentFriend.getAddress().setPostcode(null);
		return null;
	}

	public String selectAddressReferral() {
		isEmailReferral = false;
		isAddressReferral = true;
		currentFriend.getAddress().setEmailAddress(null);
		return null;
	}

	public String completeReferral() {
		boolean go = true;
		if (validateFriend(currentFriend)) {
			addFriend(currentFriend);
		}
		for (FriendReferral friend : friendList) {
			if (!validateFriend(friend)) {
				go = false;
				JSFUtil.addMessageForError("You need to complete details for " + friend.getAddress().getContactFullName());
			}
		}
		if (go) {
			for (FriendReferral friend : friendList) {
				friend.setUniqueCode(friend.generateUniqueCode());
				FriendReferralModule module = JSFUtil.getBeanFromScope(FriendReferralModule.class);
				//take a snapshot as we wont be able to find the correct attom later
				friend.setReferralPayoutPercentage(module.isReferralPayoutPercentage());
				friend.setReferralPayout(module.getReferralPayout());
				friend.setReferreeMinimumSpend(module.getReferreeMinimumSpend());
				friend.setReferralBonus(module.getReferralBonus());
				friend.setReferralBonusThreshold(module.getReferralBonusThreshold());
				friend.setReferralLimitPerCalendarMonth(module.getReferralLimitPerCalendarMonth());
				friend.saveDetails();
				if (friend.isEmailReferral()) {
					AplosEmail.sendEmail( EcommerceEmailTemplateEnum.FRIEND_REFERRAL, friend );
					friend.setSent(true);
					friend.saveDetails();
				} else {
					AplosEmail aplosEmail = new AplosEmail( EcommerceEmailTemplateEnum.FRIEND_REFER_ADMIN, friend, friend );
					aplosEmail.setToAddress( CommonUtil.getAdminUser().getEmailAddress() );
					aplosEmail.sendAplosEmailToQueue();
				}
			}
			JSFUtil.addMessage("Thanks for your referrals! We'll be in touch whenever we credit your account.");
		}
		return null;
	}





	public String switchFriend() {
		if (validateFriend(currentFriend)) {
			addFriend(currentFriend);
		}
		FriendReferral friend = (FriendReferral) JSFUtil.getRequest().getAttribute("friend");
		if (friend != null) {
			currentFriend = friend;
			isEmailReferral = friend.isEmailReferral();
			isAddressReferral = !isEmailReferral;
		}
		return null;
	}

	private boolean validateFriend(FriendReferral friend) {
		Address addr = friend.getAddress();
		return 	addr.getContactFirstName() != null && !addr.getContactFirstName().equals("") &&
				((addr.getEmailAddress() != null && !addr.getEmailAddress().equals("")) ||
				(addr.getLine1() != null && !addr.getLine1().equals("") &&
				addr.getCity() != null && !addr.getCity().equals("") &&
				addr.getCountry() != null &&
				addr.getPostcode() != null && !addr.getPostcode().equals("")));
	}

	public void deleteFriend() {
		FriendReferral friend = (FriendReferral) JSFUtil.getRequest().getAttribute("friend");
		if (friend != null) {
			friendList.remove(friend);
		}
	}

	public String addAnother() {
		Customer customer = JSFUtil.getBeanFromScope(Customer.class);
		if (customer == null) {
			EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutSignInOrSignUpCpr();
		} else {
			if (currentFriend != null) {
				addFriend(currentFriend);
			}
			currentFriend = new FriendReferral();
			currentFriend.setReferrer(customer);

			isEmailReferral = false;
			isAddressReferral = false;

			FriendReferralModule module = JSFUtil.getBeanFromScope(FriendReferralModule.class);
			if (module != null) {
				if (module.isAllowEmailReferrals()) {
					isEmailReferral = true;
				} else {
					isAddressReferral = true;
				}
			}

		}
		return null;
	}

	private void addFriend(FriendReferral friend) {
		if (!friendList.contains(friend)) {
			friendList.add(friend);
		}
	}

	public boolean isValidationRequired(){
		if (friendList.size() > 0 && (currentFriend.getAddress().getContactFirstName() == null || currentFriend.getAddress().getContactFirstName().equals(""))) {
			return BackingPage.validationRequired("anotherReferralBtn");
		} else {
			return  BackingPage.validationRequired("finishedReferralBtn")
					|| BackingPage.validationRequired("anotherReferralBtn");
				 //	|| BackingPage.validationRequired("switchReferralBtn");
		}
	}

	public void setEmailReferral(boolean isEmailReferral) {
		this.isEmailReferral = isEmailReferral;
	}

	public boolean isEmailReferral() {
		return isEmailReferral;
	}

	public void setAddressReferral(boolean isAddressReferral) {
		this.isAddressReferral = isAddressReferral;
	}

	public boolean isAddressReferral() {
		return isAddressReferral;
	}

	public void setCurrentFriend(FriendReferral currentFriend) {
		this.currentFriend = currentFriend;
	}

	public FriendReferral getCurrentFriend() {
		return currentFriend;
	}

	public void setFriendList(List<FriendReferral> friendList) {
		this.friendList = friendList;
	}

	public List<FriendReferral> getFriendList() {
		return friendList;
	}

}

