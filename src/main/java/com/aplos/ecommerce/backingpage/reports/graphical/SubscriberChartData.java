package com.aplos.ecommerce.backingpage.reports.graphical;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.aplos.common.aql.BeanDao;
import com.aplos.common.utils.FormatUtil;
import com.aplos.ecommerce.beans.ReportStats;

public class SubscriberChartData extends ReportStatsChartData {
	
	@Override
	public String getVerticalAxisLabel() {
		return "Subscribers";
	}
	
	@Override
	public String getTitleWithoutDate() {
		return "Subscription numbers";
	}
	
	@Override
	public String[] getSeriesTitles() {
		return new String[] { "Active subscriptions", "Inactive subscribers" };
	}
	
	@Override
	public String getTypeName() {
		return "Subscribers";
	}
	
	@Override
	protected boolean isAveragingRequired() {
		return true;
	}
	
	@Override
	protected void cacheData( Date startDate, Date endDate ) {
		BeanDao transDao = new BeanDao(ReportStats.class);
		transDao.setWhereCriteria("bean.dateCreated >= '" + FormatUtil.formatDateForDB( startDate ) + "'");
		transDao.addWhereCriteria("bean.dateCreated < '" + FormatUtil.formatDateForDB( endDate ) + "'");
		setCachedStats( transDao.getAll() );	
	}
	
	@Override
	protected long cacheCumulativeMax() {
		long cachedHighest = 0;
		Calendar cal = Calendar.getInstance();
		//reports using ReportStat objects
		setDayToStatsMap( new HashMap<Integer, ReportStats>() );
		
		for (ReportStats tempStats : getCachedStats()) {
			//first map against the day it was created, this is more efficient than looping through
			//each stats object to find the one that matches the date when drawing the graph
			cal.setTime( FormatUtil.removeDatesTime( tempStats.getDateCreated() ) );
			Integer dayOfPeriod = ChartPage.chartDateInteger(cal); 
			getDayToStatsMap().put(dayOfPeriod, tempStats);
			//then work out how many transactions we had in total for that day, to work out the max we need for y axis
			long todaysValue;
			todaysValue = tempStats.getNumberOfSubscribers();
			if (todaysValue > cachedHighest) {
				cachedHighest = todaysValue;
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
			ReportStats daysStats = getDayToStatsMap().get( day );
			if (daysStats != null) {
				count++;
        		intervalValues[ 0 ] = intervalValues[ 0 ].add( new BigDecimal( daysStats.getNumberOfSubscribersSubscribed() ) );
        		intervalValues[ 1 ] = intervalValues[ 1 ].add( new BigDecimal( daysStats.getNumberOfSubscribers() ) );
			}
		}
		if( count > 0 ) {
			intervalValues[ 0 ] = intervalValues[ 0 ].divide( new BigDecimal( count ), RoundingMode.HALF_DOWN );
			intervalValues[ 1 ] = intervalValues[ 1 ].divide( new BigDecimal( count ), RoundingMode.HALF_DOWN );
		}
		return intervalValues;
	}
}
