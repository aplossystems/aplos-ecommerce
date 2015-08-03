package com.aplos.ecommerce.beans.developercmsmodules;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.aplos.cms.beans.developercmsmodules.ConfigurableDeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.CollectionOfElements;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.utils.FormatUtil;

@Entity
@DynamicMetaValueKey(oldKey="FRIEND_REFERRAL_MODULE")
public class FriendReferralModule extends ConfigurableDeveloperCmsAtom {

	private static final long serialVersionUID = -8095788573402796643L;

	private boolean isReferralPayoutPercentage = false;
	private boolean isAllowEmailReferrals = true;
	private boolean isAllowAddressReferrals = false;
	private BigDecimal referralPayout = new BigDecimal(5);
	private BigDecimal referreeMinimumSpend = new BigDecimal(0);
	private BigDecimal referralBonus = new BigDecimal(5);
	private Integer referralBonusThreshold = 0;
	private Integer referralLimitPerCalendarMonth = 0;
	@CollectionOfElements
	private List<String> termsAndConditionsList = new ArrayList<String>();

	public FriendReferralModule() {
		super();
		termsAndConditionsList.add("You must know the person that you recommend.");
		termsAndConditionsList.add("If asked, we will let your friend(s) know who recommended them.");
		termsAndConditionsList.add("For every successful referral, provided we do not already have the person\'s details, we will credit your account with the amount of credit stated.");
		termsAndConditionsList.add("We can only provide credit for new referrals. A new referral is defined as someone who has never shopped with us before, and therefore is not currently listed in our databases.");
		termsAndConditionsList.add("We reserve the right to cancel any credit given and ban you from participating in the Recommend a Friend scheme if we reasonably believe you have recommended individuals who are not known to you.");
		termsAndConditionsList.add("There is no cash alternative and no purchase is necessary.");
	}

	@Override
	public String getName() {
		return "Friend Referral";
	}

	@Override
	public DeveloperCmsAtom getCopy() {
		FriendReferralModule copiedAtom = new FriendReferralModule();
		copiedAtom.setReferralBonus(getReferralBonus());
		copiedAtom.setReferralBonusThreshold(getReferralBonusThreshold());
		copiedAtom.setReferralPayout(getReferralPayout());
		copiedAtom.setReferreeMinimumSpend(getReferreeMinimumSpend());
		copiedAtom.setReferralPayoutPercentage(isReferralPayoutPercentage());
		return copiedAtom;
	}

	public String getTermsAndConditionsHtmlListItems() {
		StringBuffer buff = new StringBuffer();
		for (String tandc : termsAndConditionsList) {
			buff.append("<li>");
			buff.append(tandc);
			buff.append("</li>");
		}

		if (referralLimitPerCalendarMonth > 0) {
			buff.append("<li>We are unable to provide credit for more than ");
			String[] numberText = {
			    "one","two","three","four","five","six","seven","eight","nine","ten",
			    "eleven","twelve","thirteen","fourteen","fifteen","sixteen","seventeen",
			    "eighteen","nineteen","twenty","twenty one","twenty two","twenty three",
			    "twenty four","twenty five"
			};
			String number=null;
			if (numberText.length >= referralLimitPerCalendarMonth-2) {
				number = numberText[referralLimitPerCalendarMonth-1];
			} else {
				number = String.valueOf(referralLimitPerCalendarMonth);
			}
			buff.append(number);
			buff.append(" referrals in one month from the same source. Any referrals over this limit will be excluded from our records, and the credit for this referral will not be issued.</li>");
		}
		return buff.toString();
	}

	public String getGeneratedDescription() {
		StringBuffer buff = new StringBuffer("For every friend you reccommend who places an order ");
		if (referreeMinimumSpend != null && referreeMinimumSpend.doubleValue() > 0) {
			buff.append("over " + FormatUtil.formatCurrentCurrency(referreeMinimumSpend.doubleValue()) + " in value, ");
		}
		buff.append("you will receive a voucher worth ");
		if (isReferralPayoutPercentage) {
			buff.append(referralPayout.intValue() + "% of their order total");
		} else {
			buff.append(FormatUtil.formatCurrentCurrency(referralPayout));
		}
		buff.append(" to spend in our store.");
		if (referralBonusThreshold > 0 && referralBonus.doubleValue() > 0) {
			buff.append("<br/><br/>Addtionally, for every ");
			buff.append(referralBonusThreshold);
			buff.append(" people you refer who successfully place an order, you will receive an additional ");
			buff.append(FormatUtil.formatCurrentCurrency(referralBonus));
			buff.append(" in store credit.");
		}
		return buff.toString();
	}

	public void setReferralPayout(BigDecimal referralPayout) {
		this.referralPayout = referralPayout;
	}

	public BigDecimal getReferralPayout() {
		return referralPayout;
	}

	public void setReferreeMinimumSpend(BigDecimal referreeMinimumSpend) {
		this.referreeMinimumSpend = referreeMinimumSpend;
	}

	public BigDecimal getReferreeMinimumSpend() {
		return referreeMinimumSpend;
	}

	public void setReferralBonus(BigDecimal referralBonus) {
		this.referralBonus = referralBonus;
	}

	public BigDecimal getReferralBonus() {
		return referralBonus;
	}

	public void setReferralBonusThreshold(Integer referralBonusThreshold) {
		this.referralBonusThreshold = referralBonusThreshold;
	}

	public Integer getReferralBonusThreshold() {
		return referralBonusThreshold;
	}

	public void setReferralPayoutPercentage(boolean isReferralPayoutPercentage) {
		this.isReferralPayoutPercentage = isReferralPayoutPercentage;
	}

	public boolean isReferralPayoutPercentage() {
		return isReferralPayoutPercentage;
	}

	public void setTermsAndConditionsList(List<String> termsAndConditionsList) {
		this.termsAndConditionsList = termsAndConditionsList;
	}

	public List<String> getTermsAndConditionsList() {
		return termsAndConditionsList;
	}

	public void addTermOrCondion(String newTerm) {
		if (getTermsAndConditionsList() == null) {
			setTermsAndConditionsList(new ArrayList<String>());
		}
		if (!getTermsAndConditionsList().contains(newTerm)) {
			getTermsAndConditionsList().add(newTerm);
		}
	}

	public void setReferralLimitPerCalendarMonth(
			Integer referralLimitPerCalendarMonth) {
		this.referralLimitPerCalendarMonth = referralLimitPerCalendarMonth;
	}

	public Integer getReferralLimitPerCalendarMonth() {
		return referralLimitPerCalendarMonth;
	}

	public void setAllowEmailReferrals(boolean isAllowEmailReferrals) {
		this.isAllowEmailReferrals = isAllowEmailReferrals;
	}

	public boolean isAllowEmailReferrals() {
		return isAllowEmailReferrals;
	}

	public void setAllowAddressReferrals(boolean isAllowAddressReferrals) {
		this.isAllowAddressReferrals = isAllowAddressReferrals;
	}

	public boolean isAllowAddressReferrals() {
		return isAllowAddressReferrals;
	}

}
