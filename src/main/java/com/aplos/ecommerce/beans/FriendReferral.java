package com.aplos.ecommerce.beans;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.RemoveEmpty;
import com.aplos.common.annotations.persistence.Cascade;
import com.aplos.common.annotations.persistence.CascadeType;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.OneToMany;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.Address;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.InternationalNumber;
import com.aplos.common.beans.Subscriber;
import com.aplos.common.beans.communication.AplosEmail;
import com.aplos.common.beans.communication.SmsMessage;
import com.aplos.common.interfaces.BulkEmailFinder;
import com.aplos.common.interfaces.BulkEmailSource;
import com.aplos.common.interfaces.BulkSmsFinder;
import com.aplos.common.interfaces.BulkSmsSource;
import com.aplos.common.interfaces.BulkSubscriberSource;

@Entity
@DynamicMetaValueKey(oldKey="FRIEND_REFERRAL")
public class FriendReferral extends AplosBean implements BulkSmsSource,BulkEmailFinder,BulkSmsFinder,BulkSubscriberSource {

	private static final long serialVersionUID = -341309368325598770L;

	@ManyToOne
	@RemoveEmpty
	@Cascade({CascadeType.ALL})
	private Address address;
	private String uniqueCode; //to identify the referrer and referee
	@ManyToOne
	private Customer referrer; //who sent it
	@ManyToOne
	private Customer referee; //who used it
	//@ManyToOne
	//private GiftVoucher giftVoucher; //the voucher awarded
	@OneToMany
	private List<StoreCreditVoucher> creditVouchersAwarded = new ArrayList<StoreCreditVoucher>();
	private boolean isSent = false;

	private Date dateReferralValidated; //mark when this referral was or would have been issued credit (if limit reached)

	private boolean isReferralPayoutPercentage=false; //snapshot fields (because we never know which atom we are
	private BigDecimal referralPayout;  // using, because theoretically we could have multiple)
	private BigDecimal referreeMinimumSpend;
	private BigDecimal referralBonus;
	private Integer referralBonusThreshold;
	private Integer referralLimitPerCalendarMonth;

	public FriendReferral() { }
	
	@Override
	public Long getMessageSourceId() {
		return getId();
	}
	
	@Override
	public boolean determineIsSmsSubscribed(SmsMessage smsMessage) {
		return getAddress().isSmsSubscribed();
	}
	
	@Override
	public void setSmsSubscribed(boolean isSmsSubscribed) {
		getAddress().setSmsSubscribed(isSmsSubscribed);
	}
	
	@Override
	public String getJDynamiTeValue(String variableKey, AplosEmail aplosEmail) {
		return null;
	}
	
	@Override
	public String getBulkMessageFinderName() {
		return "All friend referrals";
	}

	@Override
	public String getDisplayName() {
		return uniqueCode;
	}

	public void referralComplete() {
		dateReferralValidated = new Date();
		saveDetails();
	}

	public String generateUniqueCode() {
		StringBuffer buff = new StringBuffer("RF");
		buff.append(String.valueOf(referrer.getId()));
		buff.append("A");
		buff.append(String.valueOf(address.getId()));
		return buff.toString();
	}

	public boolean isEmailReferral() {
		return address.getEmailAddress() != null && !address.getEmailAddress().equals("");
	}

	public String getContactDetailsText() {
		if (isEmailReferral()) {
			return address.getContactFullName() + "<br/><br/>" + address.getEmailAddress();
		} else {
			return address.getContactFullName() + "<br/><br/>" + address.getToHtmlFull();
		}
	}

	public boolean isVoucherAwarded() {
		return creditVouchersAwarded != null && creditVouchersAwarded.size() > 0;
	}

//	public boolean isVoucherRedeemed() {
//		return isVoucherAwarded() && giftVoucher.isUsed();
//	}

	/**
	 * @return whether the referral was followed up and a new customer created
	 */
	public boolean isReferralSuccessful() {
		return referee != null;
	}

	public void setUniqueCode(String uniqueCode) {
		this.uniqueCode = uniqueCode;
	}

	public String getUniqueCode() {
		return uniqueCode;
	}

	public void setReferrer(Customer referrer) {
		this.referrer = referrer;
	}

	public Customer getReferrer() {
		return referrer;
	}

	public void setReferee(Customer referee) {
		this.referee = referee;
	}

	public Customer getReferee() {
		return referee;
	}

//	public void setGiftVoucher(GiftVoucher giftVoucher) {
//		this.giftVoucher = giftVoucher;
//	}
//
//	public GiftVoucher getGiftVoucher() {
//		return giftVoucher;
//	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Address getAddress() {
		return address;
	}

	public void setSent(boolean isSent) {
		this.isSent = isSent;
	}

	public boolean isSent() {
		return isSent;
	}

	public void setReferralLimitPerCalendarMonth(
			Integer referralLimitPerCalendarMonth) {
		this.referralLimitPerCalendarMonth = referralLimitPerCalendarMonth;
	}

	public Integer getReferralLimitPerCalendarMonth() {
		return referralLimitPerCalendarMonth;
	}

	public void setReferralBonusThreshold(Integer referralBonusThreshold) {
		this.referralBonusThreshold = referralBonusThreshold;
	}

	public Integer getReferralBonusThreshold() {
		return referralBonusThreshold;
	}

	public void setReferralBonus(BigDecimal referralBonus) {
		this.referralBonus = referralBonus;
	}

	public BigDecimal getReferralBonus() {
		return referralBonus;
	}

	public void setReferreeMinimumSpend(BigDecimal referreeMinimumSpend) {
		this.referreeMinimumSpend = referreeMinimumSpend;
	}

	public BigDecimal getReferreeMinimumSpend() {
		return referreeMinimumSpend;
	}

	public void setReferralPayout(BigDecimal referralPayout) {
		this.referralPayout = referralPayout;
	}

	public BigDecimal getReferralPayout() {
		return referralPayout;
	}

	public void setReferralPayoutPercentage(boolean isReferralPayoutPercentage) {
		this.isReferralPayoutPercentage = isReferralPayoutPercentage;
	}

	public boolean isReferralPayoutPercentage() {
		return isReferralPayoutPercentage;
	}

	public void setDateReferralValidated(Date dateReferralValidated) {
		this.dateReferralValidated = dateReferralValidated;
	}

	public Date getDateReferralValidated() {
		return dateReferralValidated;
	}

	@Override
	public Subscriber getSourceSubscriber() {
		Subscriber subscriber = null;
		if (referee != null) {
			subscriber = referee.getSubscriber();
		}
		return subscriber;
	}

	@Override
	public String getSourceUniqueDisplayName() {
		return uniqueCode + ": " + address.getContactFullName();
	}

	@Override
	public List<BulkEmailSource> getEmailAutoCompleteSuggestions(String searchString, Integer limit) {
		BeanDao friendReferralDao = new BeanDao( FriendReferral.class );
		friendReferralDao.setIsReturningActiveBeans( true );
		List<BulkEmailSource> friendReferrals = null;
		if( searchString != null ) {
			friendReferralDao.addWhereCriteria( "CONCAT(address.contactFirstName,' ',address.contactSurname) like :similarSearchText OR uniqueCode like :similarSearchText" );
			if( limit != null ) {
				friendReferralDao.setMaxResults(limit);
			}
			friendReferralDao.setNamedParameter("similarSearchText", "%" + searchString + "%");
			friendReferrals = (List<BulkEmailSource>) friendReferralDao.getAll();
		} else {
			friendReferrals = friendReferralDao.getAll();
		}
		return friendReferrals;
	}

	@Override
	public List<BulkSmsSource> getSmsAutoCompleteSuggestions(String searchString, Integer limit ) {
		BeanDao friendReferralDao = new BeanDao( FriendReferral.class );
		friendReferralDao.setIsReturningActiveBeans( true );
		List<BulkSmsSource> friendReferrals = null;
		if( searchString != null ) {
			friendReferralDao.addWhereCriteria( "CONCAT(address.contactFirstName,' ',address.contactSurname) like :similarSearchText OR uniqueCode like :similarSearchText" );
			if( limit != null && limit > 0 ) {
				friendReferralDao.setMaxResults( limit );
			}
			friendReferralDao.setNamedParameter("similarSearchText", "%" + searchString + "%");
			friendReferrals = (List<BulkSmsSource>) friendReferralDao.getAll();
		} else {
			friendReferrals = friendReferralDao.getAll();
		}
		return friendReferrals;
	}

	public List<StoreCreditVoucher> getCreditVouchersAwarded() {
		return creditVouchersAwarded;
	}

	public void setCreditVouchersAwarded(List<StoreCreditVoucher> creditVouchersAwarded) {
		this.creditVouchersAwarded = creditVouchersAwarded;
	}

	@Override
	public InternationalNumber getInternationalNumber() {
		return new InternationalNumber("44",address.getMobile());
	}

	@Override
	public String getFirstName() {
		return referee.getSubscriber().getFirstName();
	}

	@Override
	public String getSurname() {
		return referee.getSubscriber().getSurname();
	}

	@Override
	public String getEmailAddress() {
		return referee.getSubscriber().getEmailAddress();
	}

	@Override
	public String getFinderSearchCriteria() {
		return "(CONCAT(bean.referee.subscriber.firstName,' ',bean.referee.subscriber.surname) LIKE :similarSearchText OR bean.referee.subscriber.emailAddress LIKE :similarSearchText)";
	}
	
	@Override
	public String getAlphabeticalSortByCriteria() {
		return "bean.referee.subscriber.firstName ASC, bean.referee.subscriber.surname ASC";
	}
}















