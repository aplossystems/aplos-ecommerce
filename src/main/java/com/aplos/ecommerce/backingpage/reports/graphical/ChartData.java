package com.aplos.ecommerce.backingpage.reports.graphical;

import java.math.BigDecimal;
import java.util.Date;

public abstract class ChartData {



	public String getHorizontalAxisLabel() {
		return "Interval";
	}
	
	public abstract String getTypeName();
	public abstract String[] getSeriesTitles();
	public abstract String getVerticalAxisLabel();
	public abstract String getTitleWithoutDate();
	protected abstract boolean isAveragingRequired();
	protected abstract void cacheData( Date startDate, Date endDate );
	protected abstract long cacheCumulativeMax();
	protected abstract BigDecimal[] calculateIntervalValues( int daysToMerge, int commencementDay, int lastDayOfPeriod );
}
