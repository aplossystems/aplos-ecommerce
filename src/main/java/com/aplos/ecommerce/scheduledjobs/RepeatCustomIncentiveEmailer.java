package com.aplos.ecommerce.scheduledjobs;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.aplos.common.ScheduledJob;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.threads.JobScheduler;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.ecommerce.beans.EnticementEmailPromotion;

@Entity
public class RepeatCustomIncentiveEmailer extends ScheduledJob<Boolean> {

	public RepeatCustomIncentiveEmailer() {
	}
	
	@Override
	public Boolean executeCall() throws Exception {
		//Get list of EnticementEmailPromotion's where sendFurtherEmails is true
		BeanDao dao = new BeanDao(EnticementEmailPromotion.class);
		dao.setWhereCriteria("bean.sendFurtherEmails = true");
		List<EnticementEmailPromotion> enticementEmailPromotions = dao.setIsReturningActiveBeans(true).getAll();
		for (EnticementEmailPromotion enticementEmailPromotion : enticementEmailPromotions) {
			enticementEmailPromotion.sendNextEmail(ApplicationUtil.getAplosContextListener());
		}
		return true;
	}

	@Override
	public Long getIntervalQuantity(Date previousExecutionDate) {
		return JobScheduler.oneDayInMillis();
	}

	@Override
	public Integer getIntervalUnit() {
		return Calendar.MILLISECOND;
	}

	@Override
	public Calendar getFirstExecutionTime() {
		Calendar cal = Calendar.getInstance();
		FormatUtil.resetTime(cal);
		cal.set(Calendar.HOUR_OF_DAY, 8);
		cal.set(Calendar.MINUTE, 00);
		return cal;
	}

}
