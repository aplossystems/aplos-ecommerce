package com.aplos.ecommerce.backingpage.reports.graphical;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aplos.common.aql.BeanDao;
import com.aplos.common.utils.FormatUtil;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.beans.listbeans.TransactionListBean;

public class CustomerChartData extends ChartData {
	private List<Transaction> cachedTransactions = null;
	private Map<Integer, List<Transaction>> cachedDayToTransactionMap = null;	
	
	@Override
	public String getVerticalAxisLabel() {
		return "Customers"; 
	}
	
	@Override
	public String getTitleWithoutDate() {
		return "New vs. Returning customer numbers";
	}
	
	@Override
	public String[] getSeriesTitles() {
		return new String[] {"New customers", "Returning customers" };
	}
	
	@Override
	public String getTypeName() {
		return "Customers";
	}
	
	@Override
	protected boolean isAveragingRequired() {
		return false;
	}
	
	@Override
	protected void cacheData( Date startDate, Date endDate ) {
		BeanDao transDao = new BeanDao(Transaction.class);
		transDao.setListBeanClass(TransactionListBean.class);
		transDao.setSelectCriteria( "bean.id, bean.ecommerceShoppingCart.customer.id,  bean.transactionDate, bean.active" );
		transDao.setWhereCriteria("bean.transactionDate >= '" + FormatUtil.formatDateForDB( startDate ) + "'");
		transDao.addWhereCriteria("bean.transactionDate < '" + FormatUtil.formatDateForDB( endDate ) + "'");
		transDao.addWhereCriteria("bean.ecommerceShoppingCart.customer.id != null");
		List<Transaction> tempTransactions = transDao.setIsReturningActiveBeans(true).getAll();
		List<Object> objects = new ArrayList<Object>();
		for (Transaction transTemp : tempTransactions) {
			objects.add(transTemp);
		}
		TransactionListBean.determinePreviousOrderCounts( objects );
		cachedTransactions = new ArrayList<Transaction>();
		for (Object transTemp : objects) {
			cachedTransactions.add( (Transaction) transTemp);
		}	
	}
	
	@Override
	protected long cacheCumulativeMax() {
		long cachedHighest = 0;
		Calendar cal = Calendar.getInstance();
		
		//reports using Transaction objects
		setCachedDayToTransactionMap(new HashMap<Integer, List<Transaction>>());
			
		for (Transaction transaction : cachedTransactions) {
			//first map the transaction against the day it was created
			cal.setTime( FormatUtil.removeDatesTime( transaction.getTransactionDate() ) );
			Integer dayOfPeriod = ChartPage.chartDateInteger(cal); 
			if (getCachedDayToTransactionMap().get(dayOfPeriod) == null) {
				getCachedDayToTransactionMap().put(dayOfPeriod, new ArrayList<Transaction>());
			}
			getCachedDayToTransactionMap().get(dayOfPeriod).add(transaction);
			//then work out how many transactions we had in total for that day, to work out the max we need for y axis
			long numberCreatedToday = getCachedDayToTransactionMap().get(dayOfPeriod).size();
			if (numberCreatedToday > cachedHighest) {
				cachedHighest = numberCreatedToday;
			}
		}	
		return cachedHighest;
	}

	protected BigDecimal[] calculateIntervalValues( int daysToMerge, int commencementDay, int lastDayOfPeriod ) {
		BigDecimal[] intervalValues = new BigDecimal[ 2 ];
		intervalValues[ 0 ] = new BigDecimal( 0 );
		intervalValues[ 1 ] = new BigDecimal( 0 );
		int count = 0;
		for (int day=commencementDay; (day-commencementDay) < daysToMerge && day < lastDayOfPeriod; day++) {
    		List<Transaction> daysTransactions = getCachedDayToTransactionMap().get( day );
        	if (daysTransactions != null) {
        		count++;
        		int[] counts = countNewVsReturn(daysTransactions);
        		intervalValues[ 0 ] = intervalValues[ 0 ].add( new BigDecimal( counts[0] ) );
        		intervalValues[ 1 ] = intervalValues[ 1 ].add( new BigDecimal( counts[1] ) );
        	}
		}
		if( count > 0 ) {
			intervalValues[ 0 ] = intervalValues[ 0 ].divide( new BigDecimal( count ), RoundingMode.HALF_DOWN );
			intervalValues[ 1 ] = intervalValues[ 1 ].divide( new BigDecimal( count ), RoundingMode.HALF_DOWN );
		}
		return intervalValues;
	}

	private int[] countNewVsReturn(List<Transaction> daysTransactions) {
		int newCustomerCount = 0;
		int returnCustomerCount = 0;
		for (Transaction tempTransaction : daysTransactions) {
			if ( ((TransactionListBean)tempTransaction).getTransientPreviousOrderCount() > 1 ) { 
				returnCustomerCount ++;
			} else {
				newCustomerCount ++;
			}
		}
		return new int[] { newCustomerCount, returnCustomerCount };
	}

	public Map<Integer, List<Transaction>> getCachedDayToTransactionMap() {
		return cachedDayToTransactionMap;
	}

	public void setCachedDayToTransactionMap(
			Map<Integer, List<Transaction>> cachedDayToTransactionMap) {
		this.cachedDayToTransactionMap = cachedDayToTransactionMap;
	}
}
