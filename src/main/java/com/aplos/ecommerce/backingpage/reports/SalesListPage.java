package com.aplos.ecommerce.backingpage.reports;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.beans.communication.BulkMessageSourceGroup;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.EcommerceShoppingCartItem;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.beans.Transaction.TransactionStatus;
import com.aplos.ecommerce.beans.product.ProductType;
import com.aplos.ecommerce.templates.printtemplates.SalesReport;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=ProductType.class)
public class SalesListPage extends ListPage {

	private static final long serialVersionUID = 8316942377652746173L;
	private Date weekCommencing;
	private List<EcommerceShoppingCartItem> weeksValues;

	public SalesListPage() {
		setWeekCommencing(findThisMonday());
	}

	public String getPrintUrl() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(weekCommencing);
		return SalesReport.getTemplateUrl( cal.getTimeInMillis() );
	}

	public Date findThisMonday() {
		Calendar cal = Calendar.getInstance();
		FormatUtil.resetTime( cal );
		while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
			cal.add(Calendar.DATE, -1);
		}
		return cal.getTime();
	}

	public void setWeekCommencing(Date weekCommencing) {
		this.weekCommencing = weekCommencing;
	}

	public Date getWeekCommencing() {
		return weekCommencing;
	}

	public String getWeekCommencingTxt() {
		return FormatUtil.formatDate(weekCommencing);
	}

	private void cacheWeeksValues() {
		BeanDao dao = new BeanDao(Transaction.class);
//		CommonUtil.timeTrial("Optimised Sales List");
		//We can's optimise this (need child list)
		//dao.setOptimisedSearch(true);
		//dao.setSelectCriteria( "bean.id, bean.active, bean.transactionStatus, bean.ecommerceShoppingCart" );
		dao.setWhereCriteria("bean.dateCreated >= '" + FormatUtil.formatDateForDB(weekCommencing) + "'");
		Calendar cal = Calendar.getInstance();
		cal.setTime(weekCommencing);
		cal.add(Calendar.DATE, 7);
		dao.addWhereCriteria("bean.dateCreated < '" + FormatUtil.formatDateForDB(cal.getTime()) + "'");
		@SuppressWarnings("unchecked")
		List<Transaction> transactions = dao.setIsReturningActiveBeans(true).getAll();
		weeksValues = new ArrayList<EcommerceShoppingCartItem>();
		for (Transaction transaction : transactions) {
			if (!transaction.getTransactionStatus().equals(TransactionStatus.INCOMPLETE) &&
				!transaction.getTransactionStatus().equals(TransactionStatus.CANCELLED) &&
				!transaction.getTransactionStatus().equals(TransactionStatus.NEW) &&
				transaction.getEcommerceShoppingCart() != null &&
				transaction.getEcommerceShoppingCart().getEcommerceShoppingCartItems() != null) {
				//HibernateUtil.initialiseList(transaction.getEcommerceShoppingCart().getEcommerceShoppingCartItems());
				weeksValues.addAll(transaction.getEcommerceShoppingCart().getEcommerceShoppingCartItems());
			}
		}
//		for (EcommerceShoppingCartItem item : weeksValues) {
//			if (item.getRealizedProduct() != null && item.getRealizedProduct().getProductInfo() != null &&
//				item.getRealizedProduct().getProductInfo().getProduct() != null && item.getRealizedProduct().getProductInfo().getProduct().getProductTypes() != null) {
//				HibernateUtil.initialiseList(item.getRealizedProduct().getProductInfo().getProduct().getProductTypes(), true);
//			}
//
//		}
	}

	public String getDayStatistics(Long daysFromWeekCommencing) {
		if (weeksValues == null) {
			cacheWeeksValues();
		}
		ProductType productType = (ProductType) JSFUtil.getRequest().getAttribute("tableBean");
		Date relevantDate = getDayDate(daysFromWeekCommencing);
		Calendar cal = Calendar.getInstance();
		cal.setTime(relevantDate);
		cal.add(Calendar.DATE, 1);
		Date tomorrowsDate = cal.getTime();
		int salesCount = 0;
		BigDecimal value = new BigDecimal(0);
		for (EcommerceShoppingCartItem item : weeksValues) {
			if (item.getRealizedProduct() != null &&
				item.getRealizedProduct().getProductInfo() != null &&
				item.getRealizedProduct().getProductInfo().getProduct() != null &&
				item.getRealizedProduct().getProductInfo().getProduct().getProductTypes() != null &&
				item.getRealizedProduct().getProductInfo().getProduct().getProductTypes().contains(productType) &&
				item.getDateCreated().compareTo(tomorrowsDate) <= 0 &&
				item.getDateCreated().compareTo(relevantDate) >= 0) {
				salesCount += item.getQuantity();
				value = value.add(item.getCachedNetLinePrice());
			}
		}
		return getStatisticsCellText(salesCount, value);
	}

	public boolean isHasNextWeek() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(weekCommencing);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		weekCommencing = cal.getTime();
		return weekCommencing.before(findThisMonday());
	}

	public String previousOverviewWeek() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(weekCommencing);
		cal.add(Calendar.DATE, -7);
		weekCommencing = cal.getTime();
		weeksValues=null;
		return null;
	}

	public String followingOverviewWeek() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(weekCommencing);
		cal.add(Calendar.DATE, +7);
		weekCommencing = cal.getTime();
		weeksValues=null;
		return null;
	}

	public String getDayStatisticsTotal(Long daysFromWeekCommencing) {
		if (weeksValues == null) {
			cacheWeeksValues();
		}
		Date relevantDate = getDayDate(daysFromWeekCommencing);
		Calendar cal = Calendar.getInstance();
		cal.setTime(relevantDate);
		cal.add(Calendar.DATE, 1);
		Date tomorrowsDate = cal.getTime();
		int salesCount = 0;
		BigDecimal value = new BigDecimal(0);
		for (EcommerceShoppingCartItem item : weeksValues) {
			if (item.getDateCreated() != null && item.getDateCreated().compareTo(tomorrowsDate) <= 0 &&
				item.getDateCreated().compareTo(relevantDate) >= 0) {
				salesCount += item.getQuantity();
				value = value.add(item.getCachedNetLinePrice());
			}
		}
		return getStatisticsCellText(salesCount, value);
	}

	public String getWeeksStatisticsTotal() {
		if (weeksValues == null) {
			cacheWeeksValues();
		}
		int salesCount = 0;
		BigDecimal value = new BigDecimal(0);
		for (EcommerceShoppingCartItem item : weeksValues) {
			if (item.getRealizedProduct() != null &&
				item.getRealizedProduct().getProductInfo() != null &&
				item.getRealizedProduct().getProductInfo().getProduct() != null &&
				item.getRealizedProduct().getProductInfo().getProduct().getProductTypes() != null) {
				salesCount += item.getQuantity();
				value = value.add(item.getCachedNetLinePrice());
			}
		}
		return getStatisticsCellText(salesCount, value);
	}

	public String getWeeksStatistics() {
		if (weeksValues == null) {
			cacheWeeksValues();
		}
		ProductType productType = (ProductType) JSFUtil.getRequest().getAttribute("tableBean");
		int salesCount = 0;
		BigDecimal value = new BigDecimal(0);
		for (EcommerceShoppingCartItem item : weeksValues) {
			if (item.getRealizedProduct() != null &&
				item.getRealizedProduct().getProductInfo() != null &&
				item.getRealizedProduct().getProductInfo().getProduct() != null &&
				item.getRealizedProduct().getProductInfo().getProduct().getProductTypes() != null &&
				item.getRealizedProduct().getProductInfo().getProduct().getProductTypes().contains(productType)) {
				salesCount += item.getQuantity();
				value = value.add(item.getCachedNetLinePrice());
			}
		}
		return getStatisticsCellText(salesCount, value);
	}

	private String getStatisticsCellText(int salesCount, BigDecimal value) {
		StringBuffer buffer = new StringBuffer();
		if (salesCount == 0) {
			buffer.append("<span class=\"aplos-unimportant\">");
		} else {
			buffer.append("<span>");
		}
		buffer.append(salesCount);
		buffer.append("</span><br/>");
		if (value == null) {
			value = new BigDecimal(0);
		}
		if (value.intValue() == 0) {
			buffer.append("<span class=\"aplos-unimportant\">");
		} else {
			buffer.append("<span>");
		}
		buffer.append(FormatUtil.formatCurrentCurrency(value));
		buffer.append("</span>");
		return buffer.toString();
	}

	public String getDayDateText(Long daysFromWeekCommencing) {
		return FormatUtil.formatDate(new SimpleDateFormat("EEE dd/MM"), getDayDate(daysFromWeekCommencing));
	}

	private Date getDayDate(Long daysFromWeekCommencing) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(weekCommencing);
		cal.add(Calendar.DATE, daysFromWeekCommencing.intValue());
		return cal.getTime();
	}

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new SalesFiguresLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class SalesFiguresLazyDataModel extends AplosLazyDataModel {

		private static final long serialVersionUID = -8248787815350622296L;

		public SalesFiguresLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
			setPageSize(5000);
		}

	}
}
