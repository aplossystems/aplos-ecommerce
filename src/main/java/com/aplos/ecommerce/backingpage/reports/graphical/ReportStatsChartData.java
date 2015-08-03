package com.aplos.ecommerce.backingpage.reports.graphical;

import java.util.List;
import java.util.Map;

import com.aplos.ecommerce.beans.ReportStats;

public abstract class ReportStatsChartData extends ChartData {
	private List<ReportStats> cachedStats = null;
	private Map<Integer, ReportStats> dayToStatsMap;
	
	public List<ReportStats> getCachedStats() {
		return cachedStats;
	}
	public void setCachedStats(List<ReportStats> cachedStats) {
		this.cachedStats = cachedStats;
	}
	public Map<Integer, ReportStats> getDayToStatsMap() {
		return dayToStatsMap;
	}
	public void setDayToStatsMap(Map<Integer, ReportStats> dayToStatsMap) {
		this.dayToStatsMap = dayToStatsMap;
	}
}
