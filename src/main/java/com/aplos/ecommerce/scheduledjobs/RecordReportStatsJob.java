package com.aplos.ecommerce.scheduledjobs;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.aplos.common.ScheduledJob;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.Subscriber;
import com.aplos.common.threads.JobScheduler;
import com.aplos.common.utils.FormatUtil;
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.beans.ReportStats;

@Entity
public class RecordReportStatsJob extends ScheduledJob<Boolean> {

	public RecordReportStatsJob() {	}
	
	@Override
	public Boolean executeCall() throws Exception {
		BeanDao dao = new BeanDao(Subscriber.class);
		dao.setSelectCriteria("bean.isSubscribed, count(bean.id)");
		dao.setGroupBy("bean.isSubscribed");
		List<Object[]> results = dao.getBeanResults();
		long numberOfSubscribers = 0;
		long numberOfSubscribersSubscribed = 0;
		//there will be 0, 1 or 2 results only. 0 is highly unlikely unless a new system.
		for (Object[] resultArr : results) {
			if ( ((Boolean) resultArr[0]) ) {
				numberOfSubscribersSubscribed =  (Long) resultArr[1];
			}
			numberOfSubscribers += (Long) resultArr[1];
		}
		dao = new BeanDao(RealizedProduct.class);
		dao.setSelectCriteria("sum(bean.productCost * bean.quantity), sum(bean.price * bean.quantity)");
		results = dao.getResultFields();
		//should only be one row this time
		Object[] resultsArr = results.get(0);
		BigDecimal totalStockValue = (BigDecimal) resultsArr[0];
		BigDecimal totalStockSalesValue = (BigDecimal) resultsArr[1];
		ReportStats reportStats = new ReportStats(numberOfSubscribers, numberOfSubscribersSubscribed, totalStockValue, totalStockSalesValue);
		reportStats.saveDetails();
		
//		//TODO: DEBUG: INSERT SOME TEST DATA FOR CHARTING/REPORTING
//		Calendar cal = Calendar.getInstance();
//		for (int i=0; i < 100; i ++) {
//			//vary the previous result by +/- 20% and store the result 100 times
//			int min = -20;
//			int max = 20;
//			int percent = min + (int)(Math.random() * ((max - min) + 1));
//			totalStockValue = totalStockValue.add(  (totalStockValue.divide(new BigDecimal(100), RoundingMode.HALF_UP)).multiply(new BigDecimal(percent)) );
//			percent = min + (int)(Math.random() * ((max - min) + 1));
//			totalStockSalesValue = totalStockSalesValue.add(  (totalStockSalesValue.divide(new BigDecimal(100), RoundingMode.HALF_UP)).multiply(new BigDecimal(percent)) );
//			percent = min + (int)(Math.random() * ((max - min) + 1));
//			numberOfSubscribers = numberOfSubscribers + (  (numberOfSubscribers / 100) * percent );		
//			reportStats = new ReportStats(numberOfSubscribers, numberOfSubscribersSubscribed, totalStockValue, totalStockSalesValue);
//			reportStats.saveDetails();
//			cal.setTime(reportStats.getDateCreated());
//			cal.add(Calendar.DATE, -1 * i); //add as previous days history;
//			reportStats.setDateCreated(cal.getTime());
//			reportStats.saveDetails(); //we cant set before fist save or it gets overwritten anyway
//		}
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
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		return cal;
	}

}
