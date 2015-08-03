package com.aplos.ecommerce.backingpage.marketing;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.aplos.common.ScheduledJob;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosAbstractBean;
import com.aplos.common.enums.CommonBundleKey;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.EnticementEmail;
import com.aplos.ecommerce.beans.Promotion;
import com.aplos.ecommerce.module.EcommerceConfiguration;
import com.aplos.ecommerce.scheduledjobs.RepeatCustomIncentiveEmailer;
import com.aplos.ecommerce.templates.emailtemplates.EnticementEmailTemplate;

@ManagedBean
@ViewScoped //so that we call the constructor each time
public class EnticementPromotionEditPage extends PromotionEditPage {

	private static final long serialVersionUID = 6799954431628310646L;

	private EnticementEmailTemplate emailTemplate;
	private Integer sendDelayDays;

	public EnticementPromotionEditPage() {
		super();
	}

	public SelectItem[] getPromotionSelectItemBeans() {
		BeanDao promoDao = new BeanDao(Promotion.class);
		return AplosAbstractBean.getSelectItemBeansWithNotSelected(promoDao.setIsReturningActiveBeans(true).getAll() );
	}

	public SelectItem[] getEmailTemplateSelectItemBeans() {
		BeanDao dao = new BeanDao(EnticementEmailTemplate.class);
		return AplosAbstractBean.getSelectItemBeansWithNotSelected(dao.setIsReturningActiveBeans(true).getAll() );
	}

	public void setSendDelayDays(Integer sendDelayDays) {
		this.sendDelayDays = sendDelayDays;
	}

	public Integer getSendDelayDays() {
		return sendDelayDays;
	}

	public String addEmailToList() {
		if (sendDelayDays != null && sendDelayDays >= 0) {
			if (emailTemplate != null) {
				EnticementEmail enticementEmail = new EnticementEmail();
				enticementEmail.setEmailTemplate(emailTemplate);
				enticementEmail.setSendDay(sendDelayDays);
				EcommerceConfiguration.getEcommerceSettingsStatic().getEnticementEmailList().add(enticementEmail);
			} else {
				JSFUtil.addMessageForError("You must select the required email template");
			}
		} else {
			JSFUtil.addMessageForError("You must enter a days-delay value greater than 0");
		}
		return null;
	}

	public String removeEmailFromList() {
		EnticementEmail enticementEmail = (EnticementEmail) JSFUtil.getRequest().getAttribute("tableBean");
		if (enticementEmail != null) {
			EcommerceConfiguration.getEcommerceSettingsStatic().getEnticementEmailList().remove(enticementEmail);
		}
		return null;
	}

	public void setEmailTemplate(EnticementEmailTemplate emailTemplate) {
		this.emailTemplate = emailTemplate;
	}

	public EnticementEmailTemplate getEmailTemplate() {
		return emailTemplate;
	}

	@Override
	public void okBtnAction() {
		if (saveAction()) {
			JSFUtil.addMessage(ApplicationUtil.getAplosContextListener().translateByKey( CommonBundleKey.SAVED_SUCCESSFULLY ),FacesMessage.SEVERITY_INFO);
		}
		//dont call super - no associated bean, nothing to do in super except die
	}

	public boolean saveAction() {
		int emailPeriod = 0;
		for (EnticementEmail enticementEmail : EcommerceConfiguration.getEcommerceSettingsStatic().getEnticementEmailList()) {
			if (enticementEmail.getSendDay() > emailPeriod) {
				emailPeriod = enticementEmail.getSendDay() ;
			}
		}
		if (EcommerceConfiguration.getEcommerceSettingsStatic().getEnticementValidForDays() <= emailPeriod) {
			JSFUtil.addMessageForError("Your promotion will expire before all emails send, please set a valid-for period to a minimum of " + (emailPeriod+1)  + " days.");
			return false;
		} else {
			EcommerceConfiguration.getEcommerceSettingsStatic().saveDetails();
			//start or stop the enticement job
			if (EcommerceConfiguration.getEcommerceSettingsStatic().isUsingRepeatCustomEnticements()) {
				ScheduledJob scheduledJob = ApplicationUtil.getJobScheduler().findJob(RepeatCustomIncentiveEmailer.class);
				if (scheduledJob != null) {
					scheduledJob.setRunning(true);
				}
			} else {
				ScheduledJob scheduledJob = ApplicationUtil.getJobScheduler().findJob(RepeatCustomIncentiveEmailer.class);
				if (scheduledJob != null) {
					scheduledJob.setRunning(false);
				}
			}
			return true;
		}
	}

	@Override
	public void applyBtnAction() {
		if (saveAction()) {
			JSFUtil.addMessage(ApplicationUtil.getAplosContextListener().translateByKey( CommonBundleKey.SAVED_SUCCESSFULLY ),FacesMessage.SEVERITY_INFO);
		}
		//dont call super - no associated bean, nothing to do in super except die
	}

}





