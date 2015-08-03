package com.aplos.ecommerce.backingpage.reports.graphical;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.DonutChartModel;
import org.primefaces.model.chart.PieChartModel;

import com.aplos.common.beans.Subscriber;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.RealizedProduct;

@ManagedBean
@ViewScoped
public class StatisticsChartPage extends ChartPage {
	private static final long serialVersionUID = 6925079146129246605L;
	 
	private int viewInterval = Calendar.DATE;

	public StatisticsChartPage() {
		super();
	}
	
	@Override
	protected void createLineAreaModel() {
		CartesianChartModel graphModel = new CartesianChartModel();
		//create our series' (in this case new and returning)
		String[] seriesTitles = getChartData().getSeriesTitles();
		ChartSeries[] chartSeries = new ChartSeries[ seriesTitles.length ];
		for( int i = 0, n = chartSeries.length; i < n; i++ ) {
			chartSeries[ i ] = new ChartSeries( seriesTitles[ i ] );
		}
		
		//first work out how far our x-axis extends
		Calendar cal = Calendar.getInstance();
		cal.setTime( getEndDate() );
		int lastDayOfPeriod = chartDateInteger(cal);
		cal.setTime( getStartDate() );
		int firstDayOfPeriod = chartDateInteger(cal);
					
		int commencing = firstDayOfPeriod;
		while ( chartDateInteger(cal) <= lastDayOfPeriod ) {

			int daysToMerge;
			String dayLabel;
			if (viewInterval == Calendar.DATE) {
				daysToMerge=1;
				//we are forced to include month (otherwise data overwrites) so dont try to remove it to save space
				dayLabel = FormatUtil.getTitledPosition( cal.get(Calendar.DAY_OF_MONTH) ) + " " + cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, CommonUtil.getContextLocale());
			} else if (viewInterval == Calendar.WEEK_OF_YEAR) {
				daysToMerge=7;
				dayLabel = "W/c " + FormatUtil.getTitledPosition( cal.get(Calendar.DAY_OF_MONTH) ) + " " + cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, CommonUtil.getContextLocale());
			} else {
				//because of this max we need to calc daysToMerge inside the loop, its negligably wasteful
				daysToMerge = cal.getMaximum(Calendar.DAY_OF_MONTH);
				dayLabel = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, CommonUtil.getContextLocale());
			}

			final BigDecimal[] values = getChartData().calculateIntervalValues(daysToMerge, commencing, lastDayOfPeriod);
			for( int i = 0, n = seriesTitles.length; i < n; i++ ) {
				if( values == null ) {
					chartSeries[ i ].set( dayLabel, 0 );
				} else {
					//because of the type of data we are viewing we want an average not a sum
					//if we add transaction to the dropdown we'll want to make sure we have a sum here
					chartSeries[ i ].set( dayLabel, values[ i ].divide( new BigDecimal( daysToMerge ), RoundingMode.HALF_DOWN ).setScale( 2, RoundingMode.HALF_DOWN ) );
				}
			}
			
			// this needs to work different for month
			if (viewInterval == Calendar.MONTH) {
				cal.add(Calendar.MONTH, +1);
			} else {
				cal.add(Calendar.DATE, daysToMerge);
			}
			commencing+=daysToMerge;
			
        }

		for( int i = 0, n = chartSeries.length; i < n; i++ ) {
			graphModel.addSeries(chartSeries[ i ]); 
		}
		setGraphModel(graphModel);
	}
	
	@Override
	protected void createDonutModel() {
		
		DonutChartModel donutModel = new DonutChartModel();  
		//populate the values for each series for each day (point on axis)       
		//first work out how far our x-axis extends
		Calendar cal = Calendar.getInstance();
		cal.setTime( getEndDate() );
		int lastDayOfPeriod = chartDateInteger(cal);
		cal.setTime( getStartDate() );
		int firstDayOfPeriod = chartDateInteger(cal);
	    //populate the values for each series for each day (point on axis)       
		cal.setTime( getStartDate() );
		String[] seriesTitles = getChartData().getSeriesTitles();
		
		int ringCount = 0;
		int commencing = firstDayOfPeriod;
		while ( chartDateInteger(cal) <= lastDayOfPeriod ) {
			
			Map<String, Number> ring = new LinkedHashMap<String, Number>();  
			
			int daysToMerge;
			String dayLabel;
			if (viewInterval == Calendar.DATE) {
				daysToMerge=1;
				//we are forced to include month (otherwise data overwrites) so dont try to remove it to save space
				dayLabel = FormatUtil.getTitledPosition( cal.get(Calendar.DAY_OF_MONTH) ) + " " + cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, CommonUtil.getContextLocale());
			} else if (viewInterval == Calendar.WEEK_OF_YEAR) {
				daysToMerge=7;
				dayLabel = "W/c " + FormatUtil.getTitledPosition( cal.get(Calendar.DAY_OF_MONTH) ) + " " + cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, CommonUtil.getContextLocale());
			} else {
				//because of this max we need to calc daysToMerge inside the loop, its negligably wasteful
				daysToMerge = cal.getMaximum(Calendar.DAY_OF_MONTH);
				dayLabel = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, CommonUtil.getContextLocale());
			}
			
			final BigDecimal[] values = getChartData().calculateIntervalValues(daysToMerge, commencing, lastDayOfPeriod);

			boolean valueGreaterThanZero = false;
			if( values != null ) {
				for( int i = 0, n = values.length; i < n; i++ ) {
					//because of the type of data we are viewing we want an average not a sum
					//if we add transaction to the dropdown we'll want to make sure we have a sum here
					BigDecimal pointValue = values[ i ].divide( new BigDecimal( daysToMerge ), RoundingMode.HALF_DOWN );
					if( pointValue.compareTo( new BigDecimal( 0 ) ) > 0 ) {
						valueGreaterThanZero = true;
					}
					
					ring.put( seriesTitles[ i ] + " (" + dayLabel + ")", pointValue.setScale( 2, RoundingMode.HALF_DOWN));  
				}
				
				//add one per cycle (week/month/date)
				if (valueGreaterThanZero) {
					donutModel.addCircle(ring);
					ringCount ++;
				}
			}
			
			int limit = 31;
			if (ringCount >= limit) {

				if (viewInterval == Calendar.DATE) {
					JSFUtil.addMessage("The donut model struggles a little with large numbers of rings. Display has been limited to " + limit +" rings (originally " + (lastDayOfPeriod-firstDayOfPeriod) + " days). This makes the last day " + FormatUtil.getTitledPosition( cal.get(Calendar.DAY_OF_MONTH) ) + " " + cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, CommonUtil.getContextLocale()) + " (zero values were excluded)");
				}
				
				limit = limit * 7;
				
				if (viewInterval == Calendar.WEEK_OF_YEAR) {
					JSFUtil.addMessage("The donut model struggles a little with large numbers of rings. Display has been limited to " + limit +" rings (originally " + ((lastDayOfPeriod-firstDayOfPeriod)/7) + " weeks). This makes the last week commencing " + FormatUtil.getTitledPosition( cal.get(Calendar.DAY_OF_MONTH) ) + " " + cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, CommonUtil.getContextLocale()) + " (zero values were excluded)");
				}
				
				break;
			}
			
			// this needs to work different for month
			if (viewInterval == Calendar.MONTH) {
				cal.add(Calendar.MONTH, +1);
			} else {
				cal.add(Calendar.DATE, daysToMerge);
			}
			commencing+=daysToMerge;

        }	
		setDonutModel(donutModel);
	}
	
	@Override
	protected void createPieModel() {
		PieChartModel graphModelFullMonth = new PieChartModel();

		Calendar cal = Calendar.getInstance();
		cal.setTime( getEndDate() );
		int lastDayOfPeriod = chartDateInteger(cal);
		cal.setTime( getStartDate() );
		int firstDayOfPeriod =Integer.parseInt( cal.get(Calendar.YEAR) + "" + cal.get(Calendar.DAY_OF_YEAR));
	    //populate the values for each series for each day (point on axis)       
		cal.setTime( getStartDate() );
		String[] seriesTitles = getChartData().getSeriesTitles();
				
		BigDecimal[] fullPeriodCount = new BigDecimal[ seriesTitles.length ];
		for( int i = 0, n = seriesTitles.length; i < n; i++ ) {
			fullPeriodCount[ i ] = new BigDecimal( 0 );
		}
		int commencing = firstDayOfPeriod;
		int count = 0;
		while ( chartDateInteger(cal) <= lastDayOfPeriod ) {

			int daysToMerge;
			if (viewInterval == Calendar.DATE) {
				daysToMerge=1;
			} else if (viewInterval == Calendar.WEEK_OF_YEAR) {
				daysToMerge=7;
			} else {
				daysToMerge = cal.getMaximum(Calendar.DAY_OF_MONTH);
			}

			final BigDecimal[] values = getChartData().calculateIntervalValues(daysToMerge, commencing, lastDayOfPeriod);


			if( values != null ) {
				count++;
				for( int i = 0, n = values.length; i < n; i++ ) {
					fullPeriodCount[ i ] = fullPeriodCount[ i ].add( values[ i ] );
				}
			}

			// this needs to work different for month
			if (viewInterval == Calendar.MONTH) {
				cal.add(Calendar.MONTH, +1);
			} else {
				cal.add(Calendar.DATE, daysToMerge);
			}
			commencing+=daysToMerge;
			
        }
		
		if( getChartData().isAveragingRequired() ) {
			for( int i = 0, n = seriesTitles.length; i < n; i++ ) {
				graphModelFullMonth.set(seriesTitles[ i ], fullPeriodCount[ i ].divide( new BigDecimal( count ), RoundingMode.HALF_DOWN ));
			}			
		}

		for( int i = 0, n = seriesTitles.length; i < n; i++ ) {
			graphModelFullMonth.set(seriesTitles[ i ], fullPeriodCount[ i ].setScale( 2, RoundingMode.HALF_DOWN));
		}
		
		setGraphModelFullMonth(graphModelFullMonth);
	}
	
	public SelectItem[] getStatisticsTypeSelectItems() {
		SelectItem[] items = new SelectItem[3];
		items[0] = new SelectItem(Customer.class, "Customers");
		items[1] = new SelectItem(RealizedProduct.class, "Products");
		items[2] = new SelectItem(Subscriber.class, "Subscribers");
		return items;
	}
	
	public int getViewInterval() {
		return viewInterval;
	}
	
	public void setViewInterval(int viewInterval) {
		this.viewInterval = viewInterval;
	}

	public SelectItem[] getIntervalSelectItems() {
		SelectItem[] items = new SelectItem[3];
		items[0] = new SelectItem(Calendar.DATE, "Day Intervals");
		items[1] = new SelectItem(Calendar.WEEK_OF_YEAR, "Week Intervals");
		items[2] = new SelectItem(Calendar.MONTH, "Month Intervals");
		return items;
	}
	
}
