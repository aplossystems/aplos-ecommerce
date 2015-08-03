package com.aplos.ecommerce.templates.printtemplates;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.AplosUrl;
import com.aplos.common.appconstants.AplosAppConstants;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.interfaces.AplosWorkingDirectoryInter;
import com.aplos.common.listeners.AplosContextListener;
import com.aplos.common.templates.PrintTemplate;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.ecommerce.beans.EcommerceShoppingCartItem;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.beans.Transaction.TransactionStatus;
import com.aplos.ecommerce.beans.product.ProductType;
import com.aplos.ecommerce.enums.EcommerceWorkingDirectory;

public class SalesReport extends PrintTemplate {
	private static final long serialVersionUID = 7966989828713761594L;
	private Date weekCommencing;
	private List<EcommerceShoppingCartItem> weeksValues;
	private long timeInMillis;
	
	@Override
	public void initialise(Map<String, String[]> params) {
		setTimeInMillis( Long.parseLong( params.get( "commencingDateInMillis" )[ 0 ] ) );
	}
	
	@Override
	public String getName() {
		return "Sales Report";
	}
	
	@Override
	public AplosWorkingDirectoryInter getAplosWorkingDirectoryInter() {
		return EcommerceWorkingDirectory.REPORTING_PDFS_DIR;
	}

	@Override
	public String getTemplateContent() {
		AplosContextListener aplosContextListener = ApplicationUtil.getAplosContextListener();

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(getTimeInMillis());
		weekCommencing = cal.getTime();

		BeanDao dao = new BeanDao(ProductType.class);
		dao.setSelectCriteria( "bean.id, bean.active, bean.name" );
		@SuppressWarnings("unchecked")
		ArrayList<ProductType> productTypes = (ArrayList<ProductType>) dao.getAll();

		try {
			JDynamiTe jDynamiTe;
			if ((jDynamiTe = CommonUtil.loadContentInfoJDynamiTe( "salesReport.html", PrintTemplate.printTemplatePath, aplosContextListener )) != null) {

				jDynamiTe.setVariable("WEEK_COMMENCING_DATE", FormatUtil.formatDate(weekCommencing) );
				for (long i=0; i < 7; i++) {
					jDynamiTe.setVariable("CELL_TEXT", getDayDateText(i));
					jDynamiTe.parseDynElem("header_cell");
				}
				for (ProductType productType : productTypes) {
					jDynamiTe.setVariable("TYPE_NAME", productType.getName() );
					jDynamiTe.setVariable("MONDAY", getDayStatistics(0l, productType) );
					jDynamiTe.setVariable("TUESDAY", getDayStatistics(1l, productType) );
					jDynamiTe.setVariable("WEDNESDAY", getDayStatistics(2l, productType) );
					jDynamiTe.setVariable("THURSDAY", getDayStatistics(3l, productType) );
					jDynamiTe.setVariable("FRIDAY", getDayStatistics(4l, productType) );
					jDynamiTe.setVariable("SATURDAY", getDayStatistics(5l, productType) );
					jDynamiTe.setVariable("SUNDAY", getDayStatistics(6l, productType) );
					jDynamiTe.setVariable("WEEK", getWeeksStatistics(productType) );
					jDynamiTe.parseDynElem("type_values");
				}
				for (long i=0; i < 7; i++) {
					jDynamiTe.setVariable("CELL_TEXT", getDayStatisticsTotal(i));
					jDynamiTe.parseDynElem("footer_cell");
				}
				jDynamiTe.setVariable("GRAND_TOTAL", getWeeksStatisticsTotal());
			}
			jDynamiTe.parse();

			return jDynamiTe.toString();
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
		return null;

	}

	public static String getTemplateUrl( long timeInMillis ) {
		AplosUrl aplosUrl = getBaseTemplateUrl( SalesReport.class );
		aplosUrl.addQueryParameter( AplosAppConstants.CREATE_SIZED_PDF, "true" );
		aplosUrl.addQueryParameter( "commencingDateInMillis", timeInMillis );
		return  aplosUrl.toString();
	}

	public String getDayDateText(Long daysFromWeekCommencing) {
		return FormatUtil.formatDate(new SimpleDateFormat("EEE dd/MM"), getDayDate(daysFromWeekCommencing));
	}

	public String getDayStatistics(Long daysFromWeekCommencing, ProductType productType) {
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


	public String getWeeksStatistics(ProductType productType) {
		if (weeksValues == null) {
			cacheWeeksValues();
		}

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
		for (EcommerceShoppingCartItem item : weeksValues) {
			if (item.getRealizedProduct() != null && item.getRealizedProduct().getProductInfo() != null &&
				item.getRealizedProduct().getProductInfo().getProduct() != null && item.getRealizedProduct().getProductInfo().getProduct().getProductTypes() != null) {
//				HibernateUtil.initialiseList(item.getRealizedProduct().getProductInfo().getProduct().getProductTypes(), true);
			}

		}
//		CommonUtil.timeTrial("Optimised Sales List");
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

	private Date getDayDate(Long daysFromWeekCommencing) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(weekCommencing);
		cal.add(Calendar.DATE, daysFromWeekCommencing.intValue());
		return cal.getTime();
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

	public long getTimeInMillis() {
		return timeInMillis;
	}

	public void setTimeInMillis(long timeInMillis) {
		this.timeInMillis = timeInMillis;
	}

}
