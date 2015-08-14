package com.aplos.ecommerce.backingpage.transaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.primefaces.model.SortOrder;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.beans.Transaction.TransactionStatus;
import com.aplos.ecommerce.beans.listbeans.TransactionListBean;
import com.aplos.ecommerce.enums.TransactionType;

public class TransactionLazyDataModel extends AplosLazyDataModel {
	
	private static final long serialVersionUID = 4074839568224911732L;
	private TransactionStatus currentlyShowingTransactionStatus;
	private TransactionType currentlyShowingTransactionType;
	
	private Date startDate;
	private Date endDate;
	private BigDecimal subTotalTotal;
	private BigDecimal deliveryTotal;
	private BigDecimal totalTotal;

	public TransactionLazyDataModel( DataTableState dataTableState, BeanDao aqlBeanDao ) {
		super( dataTableState, aqlBeanDao );

		getDataTableState().setShowingNewBtn(false);

		Calendar cal = new GregorianCalendar();
		cal.setTime( new Date() );
		cal.add( Calendar.YEAR, -1);
		startDate = cal.getTime();
		cal.add( Calendar.YEAR, 1);
		endDate = cal.getTime();

		getBeanDao().setListBeanClass(TransactionListBean.class);
		getBeanDao().setSelectCriteria( "bean.id, bean.ecommerceShoppingCart.customer.id, bean.invoiceNumber, bean.ecommerceShoppingCart.customer.subscriber.firstName, bean.ecommerceShoppingCart.customer.subscriber.surname, bean.ecommerceShoppingCart.customer.subscriber.emailAddress, bean.transactionStatus, bean.transactionType, bean.ecommerceShoppingCart.cachedTotalVatAmount, bean.ecommerceShoppingCart.cachedNetTotalAmount, bean.ecommerceShoppingCart.deliveryCost, bean.ecommerceShoppingCart.cachedNetDeliveryCost, bean.transactionDate, bean.deletable, bean.active" );
	}

	public BigDecimal getSubTotalTotal() {
		return subTotalTotal;
	}

	public BigDecimal getDeliveryTotal() {
		return deliveryTotal;
	}

	public BigDecimal getTotalTotal() {
		return totalTotal;
	}

	public void calculateTotalValues() {
		BeanDao invoiceTotalDao = new BeanDao( Transaction.class ).copy( getBeanDao() );
		invoiceTotalDao.setSelectCriteria( "SUM(bean.ecommerceShoppingCart.cachedNetTotalAmount),SUM(bean.ecommerceShoppingCart.deliveryCost),SUM(bean.ecommerceShoppingCart.cachedNetTotalAmount + bean.ecommerceShoppingCart.deliveryCost)");
		invoiceTotalDao.setIsReturningActiveBeans(true);
		addSearchParameters( invoiceTotalDao, getDataTableState().getColumnFilters() );
		Object[] totalValues = (Object[]) invoiceTotalDao.getFirstResult();
		if( totalValues == null ) {
			subTotalTotal = new BigDecimal( 0 );
			deliveryTotal = new BigDecimal( 0 );
			totalTotal = new BigDecimal( 0 );
		} else {
			subTotalTotal = (BigDecimal) totalValues[ 0 ];
			deliveryTotal = (BigDecimal) totalValues[ 1 ];
			totalTotal = (BigDecimal) totalValues[ 2 ];
		}
	}

	public List<SelectItem> getTransactionStatusSelectItemBeans() {
		List<SelectItem> selectItemBeans = new ArrayList<SelectItem>();
		selectItemBeans.add(new SelectItem(null, "All"));
		selectItemBeans.addAll( CommonUtil.getEnumSelectItems(TransactionStatus.class, null) );

		Iterator<SelectItem> iterator = selectItemBeans.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().getLabel().equals(TransactionStatus.INCOMPLETE.getLabel())) {
				iterator.remove();
			}
		}

		return selectItemBeans;
	}

	@Override
	public List<Object> load(int first, int pageSize, String sortField,	SortOrder sortOrder, Map<String, String> filters) {
		return load(first, pageSize, sortField, sortOrder, filters, true );
	}

	public List<Object> load(int first, int pageSize, String sortField,	SortOrder sortOrder, Map<String, String> filters, boolean clearCriteria ) {

		if ( clearCriteria ) {
			getBeanDao().clearWhereCriteria();
		}

		getBeanDao().clearSearchCriteria();

		addSpecificWhereCriteria();

		addFilteringWhereCriteria();

		List<Object> results = super.load(first, pageSize, sortField, sortOrder, filters);

		TransactionListBean.determineCompanyNames(results);
		TransactionListBean.determinePreviousOrderCounts( results );
		calculateTotalValues();

		return results;

	}

	public void addSpecificWhereCriteria() {
		getBeanDao().addWhereCriteria("bean.transactionStatus != " + TransactionStatus.INCOMPLETE.ordinal());
		getBeanDao().addWhereCriteria("bean.ecommerceShoppingCart.customer.id IS NOT NULL");
	}

	public List<SelectItem> getTransactionTypeSelectItemBeans() {
		List<SelectItem> selectItemBeans = new ArrayList<SelectItem>();
		selectItemBeans.add(new SelectItem(null, "Transactions"));
		selectItemBeans.addAll( CommonUtil.getEnumSelectItems(TransactionType.class, null) );

		return selectItemBeans;
	}

	public String getRowTextColor() {
		Transaction transaction = (Transaction) JSFUtil.getRequest().getAttribute("tableBean");
		if (transaction != null && (transaction.getTransactionType() == TransactionType.LOAN) &&
			transaction.getIsDispatchedOrFurther() && (transaction.getLoanReturnDate() == null || 
			transaction.getLoanReturnDate().compareTo(new Date()) <= 0 ) ) {
			return "red";
		} else {
			return "grey";
		}
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public String getSearchCriteria() {
		return "bean.id LIKE :similarSearchText OR bean.ecommerceShoppingCart.customer.subscriber.firstName LIKE :similarSearchText OR bean.ecommerceShoppingCart.customer.subscriber.surname LIKE :similarSearchText OR bean.invoiceNumber LIKE :exactSearchText";
	}

	@Override
	public AplosBean selectBean() {
		TransactionListBean transactionListBean = JSFUtil.getBeanFromRequest( "tableBean" );
		Transaction transaction = (Transaction) super.selectBean(Transaction.class, transactionListBean.getId(), false );
		JSFUtil.redirect( TransactionEditPage.class );
		return transaction;
	}

	public void setCurrentlyShowingTransactionStatus(
			TransactionStatus currentlyShowingTransactionStatus) {
		this.currentlyShowingTransactionStatus = currentlyShowingTransactionStatus;
	}

	public TransactionStatus getCurrentlyShowingTransactionStatus() {
		return currentlyShowingTransactionStatus;
	}

	public void addFilteringWhereCriteria() {
		if (currentlyShowingTransactionStatus != null) {
			getBeanDao().addWhereCriteria("bean.transactionStatus = " + currentlyShowingTransactionStatus.ordinal());
		}
		if (currentlyShowingTransactionType != null) {
			getBeanDao().addWhereCriteria("bean.transactionType = " + currentlyShowingTransactionType.ordinal());
		}

		if (startDate != null && endDate != null ) {
			if( startDate.before(endDate)) {
				if ( startDate != null ) {
					getBeanDao().addWhereCriteria("bean.dateCreated > '" + FormatUtil.formatDateForDB(startDate) + "'");
				}
				if ( endDate != null ) {
					Calendar cal = new GregorianCalendar();
					cal.setTime( endDate );
					cal.add( Calendar.DAY_OF_MONTH, 1); // as <= doesnt work
					Date endDateForDB = cal.getTime();

					getBeanDao().addWhereCriteria("bean.dateCreated < '" + FormatUtil.formatDateForDB(endDateForDB) + "'");
				}
			} else {
				JSFUtil.addMessage("The start date is after the end date, please check their values.");
				return;
			}
		}
	}

	public void setCurrentlyShowingTransactionType(TransactionType currentlyShowingTransactionType) {
		this.currentlyShowingTransactionType = currentlyShowingTransactionType;
	}

	public TransactionType getCurrentlyShowingTransactionType() {
		return currentlyShowingTransactionType;
	}
	
}
