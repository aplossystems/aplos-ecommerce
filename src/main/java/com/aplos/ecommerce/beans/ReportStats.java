package com.aplos.ecommerce.beans;

import java.math.BigDecimal;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.AplosBean;

@Entity
public class ReportStats extends AplosBean {

	private static final long serialVersionUID = 474417271720372601L;
	
	private long numberOfSubscribers;
	private long numberOfSubscribersSubscribed;
	private BigDecimal totalStockValue;
	private BigDecimal totalStockSalesValue;
	
	public ReportStats() { 	}
	
	public ReportStats(long numberOfSubscribers, long numberOfSubscribersSubscribed, BigDecimal totalStockValue, BigDecimal totalStockSalesValue) {
		this.numberOfSubscribers=numberOfSubscribers;
		this.numberOfSubscribersSubscribed=numberOfSubscribersSubscribed;
		this.totalStockValue=totalStockValue;
		this.totalStockSalesValue=totalStockSalesValue;
	}
	
	public BigDecimal getTotalStockSalesValue() {
		return totalStockSalesValue;
	}
	
	public void setTotalStockSalesValue(BigDecimal totalStockSalesValue) {
		this.totalStockSalesValue = totalStockSalesValue;
	}
	
	public long getNumberOfSubscribers() {
		return numberOfSubscribers;
	}
	
	public void setNumberOfSubscribers(long numberOfSubscribers) {
		this.numberOfSubscribers = numberOfSubscribers;
	}
	
	public long getNumberOfSubscribersSubscribed() {
		return numberOfSubscribersSubscribed;
	}
	
	public void setNumberOfSubscribersSubscribed(
			long numberOfSubscribersSubscribed) {
		this.numberOfSubscribersSubscribed = numberOfSubscribersSubscribed;
	}
	
	public BigDecimal getTotalStockValue() {
		return totalStockValue;
	}
	
	public void setTotalStockValue(BigDecimal totalStockValue) {
		this.totalStockValue = totalStockValue;
	}

}