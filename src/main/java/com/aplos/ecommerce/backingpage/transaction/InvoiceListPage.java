package com.aplos.ecommerce.backingpage.transaction;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.Transaction.TransactionStatus;
import com.aplos.ecommerce.enums.TransactionType;

@ManagedBean
@ViewScoped
public class InvoiceListPage extends TransactionListPage {
	private static final long serialVersionUID = -4303845023586830913L;

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new InvoiceListLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class InvoiceListLazyDataModel extends TransactionLazyDataModel {

		private static final long serialVersionUID = -8248787815350622296L;

		public InvoiceListLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
			dataTableState.setShowingSearch( false );
			getBeanDao().setSelectCriteria( "bean.id, bean.ecommerceShoppingCart.customer.id, bean.invoiceNumber, bean.ecommerceShoppingCart.customer.subscriber.firstName, bean.ecommerceShoppingCart.customer.subscriber.surname, bean.ecommerceShoppingCart.customer.subscriber.emailAddress, bean.transactionStatus, bean.transactionType, bean.ecommerceShoppingCart.cachedTotalVatAmount, bean.ecommerceShoppingCart.cachedNetTotalAmount, bean.ecommerceShoppingCart.deliveryCost, bean.transactionDate, bean.firstInvoicedDate, bean.deletable, bean.active" );
			getBeanDao().setOrderBy( "bean.firstInvoicedDate" );
			Calendar cal = new GregorianCalendar();
			cal.setTime( new Date() );
			setEndDate( cal.getTime() );
			cal.add( Calendar.MONTH, -1);
			setStartDate( cal.getTime() );
		}

		@Override
		public String getSearchCriteria() {
			return "bean.invoiceNumber LIKE :similarSearchText OR CONCAT(bean.ecommerceShoppingCart.customer.subscriber.firstName, ' ', bean.ecommerceShoppingCart.customer.subscriber.surname) LIKE :similarSearchText";
		}

		@Override
		public void addFilteringWhereCriteria() {
			getBeanDao().clearWhereCriteria();
			getBeanDao().addWhereCriteria("bean.transactionStatus != " + TransactionStatus.CANCELLED.ordinal());
			if (getCurrentlyShowingTransactionStatus() != null) {
				getBeanDao().addWhereCriteria("bean.transactionStatus = " + getCurrentlyShowingTransactionStatus().ordinal());
			}
			if (getCurrentlyShowingTransactionType() != null) {
				getBeanDao().addWhereCriteria("bean.transactionType = " + getCurrentlyShowingTransactionType().ordinal());
			} else {
				getBeanDao().addWhereCriteria("bean.transactionType != " + TransactionType.LOAN.ordinal());
			}

			if (getStartDate() != null && getEndDate() != null && getStartDate().compareTo(getEndDate()) <= 0 ) {
				if ( getStartDate() != null ) {
					getBeanDao().addWhereCriteria("bean.firstInvoicedDate >= '" + FormatUtil.formatDateForDB(getStartDate()) + "'");
				}
				if ( getEndDate() != null ) {
					Calendar cal = new GregorianCalendar();
					cal.setTime( getEndDate() );
					cal.add( Calendar.DAY_OF_MONTH, 1); // as <= doesnt work
					Date endDateForDB = cal.getTime();

					getBeanDao().addWhereCriteria("bean.firstInvoicedDate <= '" + FormatUtil.formatDateForDB(endDateForDB) + "'");
				}
				getBeanDao().addWhereCriteria( "bean.invoiceNumber IS NOT NULL" );
			} else {
				JSFUtil.addMessageForError("The start date is after the end date, please check their values.");
			}
		}

	}

}
