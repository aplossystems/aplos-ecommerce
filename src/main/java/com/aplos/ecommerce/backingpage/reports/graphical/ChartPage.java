package com.aplos.ecommerce.backingpage.reports.graphical;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.DonutChartModel;
import org.primefaces.model.chart.PieChartModel;

import com.aplos.common.backingpage.EditPage;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;

@ManagedBean
@ViewScoped
public abstract class ChartPage extends EditPage {

	private static final long serialVersionUID = 8791268308031846648L;

	private String dataDisplayType = "display_line";
	
	private Date originalStartDate = null;
	private Date startDate = null;
	private Date endDate = null;
	
	/* charts are separate because we cant just use graph model as a generic because the component 
	   still tests rendering every component ... (and thus fails at a cast). They are not left null for the same reason */
	
	//line and area charts
	private CartesianChartModel graphModel = new CartesianChartModel();
	
	//pie charts
	private PieChartModel graphModelPie = new PieChartModel();
	private PieChartModel graphModelFullMonth = new PieChartModel(); 
	
	//donut charts
	private DonutChartModel donutModel = new DonutChartModel();
	
	private ChartData chartData;
	private ChartData chartDataOptions[];
	
	private String chartDataClassName;
	private static DecimalFormat threeDig = new DecimalFormat( "000" );
	
	private Long cachedHighest = null; 

	public ChartPage() {
		super();
	}
	
	public static int chartDateInteger(Calendar cal) {
		//we have to concatenate with a sting, otherwise 2012 + 23 == 2011 + 24 so data gets overwritten
		return Integer.parseInt(cal.get(Calendar.YEAR) + "" + threeDig.format( cal.get(Calendar.DAY_OF_YEAR) ) );
	}
	
	public void createChartDataOptions() {
		setChartDataOptions(new ChartData[ 3 ]);
		getChartDataOptions()[0] = new CustomerChartData();
		getChartDataOptions()[1] = new StockChartData();
		getChartDataOptions()[2] = new SubscriberChartData();

		setChartData(getChartDataOptions()[1]);
		setChartDataClassName(getChartData().getClass().getSimpleName());
	}
	
	public SelectItem[] getChartDataSelectItems() {
		SelectItem[] items = new SelectItem[getChartDataOptions().length];
		for( int i = 0; i < items.length; i++ ) {
			items[i] = new SelectItem(getChartDataOptions()[ i ].getClass().getSimpleName(), getChartDataOptions()[ i ].getTypeName());	
		}
		return items;
	}
	
	/**
	 * Used to decide the max the chart will display in intervals so 
	 * the available space is always used to the max
	 */
	public int getCumulativeMax() {
		return (int) Math.floor(getCachedHighest() * 1.2);
	}
	
	protected abstract void createPieModel();
	protected abstract void createLineAreaModel();
	protected abstract void createDonutModel();
	
	public String getTitle() {
		return getChartData().getTitleWithoutDate() + " ( " + getMonthYearTitle( startDate ) + " )";
	}
	
	private String getMonthYearTitle( Date date ) {
		Calendar cal = Calendar.getInstance();
		cal.setTime( date );
		String additions = "";
		if (date.compareTo(originalStartDate) > 0) {
			additions = " - DATA INCOMPLETE";
		}
		return cal.getDisplayName(Calendar.MONTH, Calendar.LONG, CommonUtil.getContextLocale()) + " " + cal.get(Calendar.YEAR) + additions;
	}
	
	public String getOriginalMonthYear() { //used on a button
		return getMonthYearTitle( originalStartDate );
	}
	
	public String previousMonth() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.MONTH, -1);
		startDate = cal.getTime();
		cal.setTime(endDate);
		cal.add(Calendar.MONTH, -1);
		endDate = cal.getTime();
		return reCacheAndDraw();
	}

	public String thisMonth() {
		startDate = originalStartDate;
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		endDate = cal.getTime();
		return reCacheAndDraw();
	}

	public String nextMonth() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.MONTH, 1);
		startDate = cal.getTime();
		cal.setTime(endDate);
		cal.add(Calendar.MONTH, 1);
		endDate = cal.getTime();
		return reCacheAndDraw();
	}

	@Override
	public boolean responsePageLoad() {
		super.responsePageLoad();
		if( getChartDataOptions() == null ) {
			createChartDataOptions();
		}
		if (startDate == null) {
			Calendar cal = Calendar.getInstance();
			FormatUtil.resetTime(cal);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			startDate = cal.getTime();
			originalStartDate = startDate;
			cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
			setEndDate(cal.getTime());
			reCacheAndDraw();
		}
		return true;

	}
	
	public void chartDataTypeUpdated() {
		for( int i = 0, n = getChartDataOptions().length; i < n; i++ ) {
			if( getChartDataOptions()[ i ].getClass().getSimpleName().equals( getChartDataClassName() ) ) {
				setChartData(getChartDataOptions()[i]);
			}
		}
		reCacheAndDraw();
	}
	
	public String reDraw() {
		createGraphModel();
		return null;
	}
	
	public String reCacheAndDraw() {
		getChartData().cacheData(getStartDate(),getEndDate());
		setCachedHighest( getChartData().cacheCumulativeMax() );
		createGraphModel();
		return null;
	}
	
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public CartesianChartModel getGraphModel() {
		return graphModel;
	}

	public void setGraphModel(CartesianChartModel graphModel) {
		this.graphModel = graphModel;
	}

	public String getDataDisplayType() {
		return dataDisplayType;
	}

	public void setDataDisplayType(String dataDisplayType) {
		this.dataDisplayType = dataDisplayType;
	}

	private void createGraphModel() {
		if ( dataDisplayType.equals("display_pie") ) {
			createPieModel();
		} else if ( dataDisplayType.equals("display_donut") ) {
			createDonutModel();
		} else {
			createLineAreaModel();
		}
	}

	public PieChartModel getGraphModelFullMonth() {
		return graphModelFullMonth;
	}

	public void setGraphModelFullMonth(PieChartModel graphModelFullMonth) {
		this.graphModelFullMonth = graphModelFullMonth;
	}

	public PieChartModel getGraphModelPie() {
		return graphModelPie;
	}

	public void setGraphModelPie(PieChartModel graphModelPie) {
		this.graphModelPie = graphModelPie;
	}

	public DonutChartModel getDonutModel() {
		return donutModel;
	}

	public void setDonutModel(DonutChartModel donutModel) {
		this.donutModel = donutModel;
	}

	public Long getCachedHighest() {
		return cachedHighest;
	}

	public void setCachedHighest(Long cachedHighest) {
		this.cachedHighest = cachedHighest;
	}

	protected Date getOriginalStartDate() {
		return originalStartDate;
	}

	protected void setOriginalStartDate(Date originalStartDate) {
		this.originalStartDate = originalStartDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public ChartData getChartData() {
		return chartData;
	}

	public void setChartData(ChartData chartData) {
		this.chartData = chartData;
	}

	public ChartData[] getChartDataOptions() {
		return chartDataOptions;
	}

	public void setChartDataOptions(ChartData chartDataOptions[]) {
		this.chartDataOptions = chartDataOptions;
	}

	public String getChartDataClassName() {
		return chartDataClassName;
	}

	public void setChartDataClassName(String chartDataClassName) {
		this.chartDataClassName = chartDataClassName;
	}
}
