package com.aplos.ecommerce.beans;

import java.util.Calendar;
import java.util.Date;

import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.beans.AplosAbstractBean;
import com.aplos.common.beans.Subscriber;
import com.aplos.common.beans.communication.AplosEmail;
import com.aplos.common.enums.JsfScope;
import com.aplos.common.interfaces.BulkSubscriberSource;
import com.aplos.common.listeners.AplosContextListener;
import com.aplos.common.utils.FormatUtil;
import com.aplos.ecommerce.module.EcommerceConfiguration;
import com.aplos.ecommerce.templates.emailtemplates.EnticementEmailTemplate;

@Entity
@DynamicMetaValueKey(oldKey="ENTICEMENT_PROMO")
public class EnticementEmailPromotion extends Promotion implements BulkSubscriberSource {

	private static final long serialVersionUID = 1998181997054269252L;

	private Integer lastEmailSentIdx; //not sure this is required now, good for debugging though
	private Date initialPurchaseDate;
	private boolean sendFurtherEmails = true; //once this is false the job ignores this promotion

	@Transient
	private long daysSinceInitialEmailSent = -1l;

	public void setLastEmailSentIdx(Integer lastEmailSentIdx) {
		this.lastEmailSentIdx = lastEmailSentIdx;
	}

	public Integer getLastEmailSentIdx() {
		return lastEmailSentIdx;
	}
	
	@Override
	public String getJDynamiTeValue(String variableKey, AplosEmail aplosEmail) {
		return null;
	}
	
	@Override
	public void registerUse() {
		super.registerUse();
		setSendFurtherEmails(false);
	}
	
	@Override
	public Long getMessageSourceId() {
		return getId();
	}

	public void sendNextEmail(AplosContextListener contextListener) {
		if (lastEmailSentIdx == null) {
			lastEmailSentIdx = -1;
		}
		if (EcommerceConfiguration.getEcommerceSettingsStatic().getEnticementEmailList().size() > (lastEmailSentIdx + 1)) {
			EnticementEmail enticementEmail = EcommerceConfiguration.getEcommerceSettingsStatic().getEnticementEmailList().get(lastEmailSentIdx + 1);
			if (enticementEmail != null) {
				Calendar todaysCal = Calendar.getInstance();
				FormatUtil.resetTime(todaysCal);
				Date todaysDate = todaysCal.getTime();
				Calendar initialCal = Calendar.getInstance();
				if (getInitialPurchaseDate() == null) {
					//we need to always have a starting reference point
					setInitialPurchaseDate(todaysDate);
				}
				initialCal.setTime(getInitialPurchaseDate());
				FormatUtil.resetTime(initialCal);
				Date initialDate = initialCal.getTime();
				setDaysSinceInitialEmailSent(todaysDate.getTime() - initialDate.getTime());
				setDaysSinceInitialEmailSent(getDaysSinceInitialEmailSent() / (1000 * 60 * 60 *24));
				if (getDaysSinceInitialEmailSent() >= enticementEmail.getSendDay()) {
					EnticementEmailTemplate emailTemplate = enticementEmail.getEmailTemplate();
					if ( emailTemplate != null ) {
						AplosEmail aplosEmail = new AplosEmail();
						aplosEmail.updateEmailTemplate(emailTemplate);
						aplosEmail.setSingleMessageSource(this, this);
						aplosEmail.sendAplosEmailToQueue();
						lastEmailSentIdx++;
						if (EcommerceConfiguration.getEcommerceSettingsStatic().getEnticementEmailList().size() <= (lastEmailSentIdx + 1)) {
							sendFurtherEmails = false;
						}
						saveDetails();
					}
				}
			}
		} else {
			sendFurtherEmails = false;
			saveDetails();
		}
	}
	
	@Override
	public void addToScope(JsfScope associatedBeanScope) {
		super.addToScope(associatedBeanScope);
		AplosAbstractBean.addToScope( getBinding( Promotion.class ), this, associatedBeanScope );
	}

	@Override
	public String generatePromoCode() {
		return "E" + super.generatePromoCode();
	}

	public void setSendFurtherEmails(boolean sendFurtherEmails) {
		this.sendFurtherEmails = sendFurtherEmails;
	}

	public boolean isSendFurtherEmails() {
		return sendFurtherEmails;
	}

	public void setInitialPurchaseDate(Date initialPurchaseDate) {
		this.initialPurchaseDate = initialPurchaseDate;
	}

	public Date getInitialPurchaseDate() {
		return initialPurchaseDate;
	}

	public long getDaysSinceInitialEmailSent() {
		return daysSinceInitialEmailSent;
	}

	public void setDaysSinceInitialEmailSent(long daysSinceInitialEmailSent) {
		this.daysSinceInitialEmailSent = daysSinceInitialEmailSent;
	}

	@Override
	public Subscriber getSourceSubscriber() {
		return getCustomer().getSourceSubscriber();
	}

	@Override
	public String getSourceUniqueDisplayName() {
		return "Enticement Email: " + getDisplayName();
	}
	
	@Override
	public String getFirstName() {
		return super.getCustomer().getSubscriber().getFirstName();
	}

	@Override
	public String getSurname() {
		return super.getCustomer().getSubscriber().getSurname();
	}

	@Override
	public String getEmailAddress() {
		return super.getCustomer().getSubscriber().getEmailAddress();
	}
}




