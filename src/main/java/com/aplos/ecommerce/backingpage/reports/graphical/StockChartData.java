package com.aplos.ecommerce.backingpage.reports.graphical;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.aplos.common.aql.BeanDao;
import com.aplos.common.utils.FormatUtil;
import com.aplos.ecommerce.beans.ReportStats;

public class StockChartData extends ReportStatsChartData {
	@Override
	public String getVerticalAxisLabel() {
		return "Stock Values"; 
	}
	
	@Override
	public String getTitleWithoutDate() {
		return "Stock value"; 
	}
	
	@Override
	public String[] getSeriesTitles() {
		return new String[] { "Resale value", "Sales margin", "Stock cost" };
	}
	
	@Override
	public String getTypeName() {
		return "Stock";
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
			todaysValue = tempStats.getTotalStockSalesValue().longValue();
			if (todaysValue > cachedHighest) {
				cachedHighest = todaysValue;
			}
		}
		return cachedHighest;	
	}
	
	protected BigDecimal[] calculateIntervalValues( int daysToMerge, int commencementDay, int lastDayOfPeriod ) {
		BigDecimal[] intervalValues = new BigDecimal[ 3 ];
		for( int i = 0, n = intervalValues.length; i < n; i++ ) {
			intervalValues[ i ] = new BigDecimal( 0 );	
		}
		intervalValues[ 1 ] = new BigDecimal( 0 );
		intervalValues[ 2 ] = new BigDecimal( 0 );
		int count = 0;
		for (int day=commencementDay; (day-commencementDay) < daysToMerge && day < lastDayOfPeriod; day++) {
			ReportStats daysStats = getDayToStatsMap().get( day );
			if (daysStats != null) {
				count++;
        		intervalValues[ 0 ] = intervalValues[ 0 ].add( getDayToStatsMap().get( day ).getTotalStockSalesValue() );
    			intervalValues[ 1 ] = intervalValues[ 1 ].add( getDayToStatsMap().get( day ).getTotalStockSalesValue().subtract( getDayToStatsMap().get( day ).getTotalStockValue() ) );
    			intervalValues[ 2 ] = intervalValues[ 2 ].add( getDayToStatsMap().get( day ).getTotalStockValue() );
			}
		}
		if( count > 0 ) {
			for( int i = 0, n = intervalValues.length; i < n; i++ ) {
				intervalValues[ i ] = intervalValues[ i ].divide( new BigDecimal( count ), RoundingMode.HALF_DOWN );
			}
		}
		return intervalValues;
	}
}
