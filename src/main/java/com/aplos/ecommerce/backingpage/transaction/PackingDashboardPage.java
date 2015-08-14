package com.aplos.ecommerce.backingpage.transaction;

import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.SortOrder;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.beans.Transaction.TransactionStatus;
import com.aplos.ecommerce.beans.listbeans.TransactionListBean;

@ManagedBean
@ViewScoped
public class PackingDashboardPage extends ListPage {
	private static final long serialVersionUID = -9001987392390529334L;

	private PaymentRequiredAndAcknowledgedLazyDataModel paymentRequiredAndAcknowledgedLdm;
	private BookedOutAndAwaitingDispatchLazyDataModel bookedOutAndAwaitingDispatchLdm;
	private OnLoanLazyDataModel onLoanLdm;

	public PackingDashboardPage() {
		super();

		DataTableState dataTableState = CommonUtil.createDataTableState( this, PaymentRequiredAndAcknowledgedLazyDataModel.class );
		setPaymentRequiredAndAcknowledgedLdm(new PaymentRequiredAndAcknowledgedLazyDataModel( dataTableState, new BeanDao( Transaction.class ) ));
		dataTableState.setLazyDataModel( getPaymentRequiredAndAcknowledgedLdm() );

		DataTableState dataTableBookedOutState = CommonUtil.createDataTableState( this, BookedOutAndAwaitingDispatchLazyDataModel.class );
		setBookedOutAndAwaitingDispatchLdm(new BookedOutAndAwaitingDispatchLazyDataModel(dataTableBookedOutState, new BeanDao(Transaction.class)));
		dataTableBookedOutState.setLazyDataModel( getBookedOutAndAwaitingDispatchLdm() );

		DataTableState dataTableOnLoanState = CommonUtil.createDataTableState( this, OnLoanLazyDataModel.class );
		setOnLoanLdm(new OnLoanLazyDataModel(dataTableOnLoanState, new BeanDao(Transaction.class)));
		dataTableOnLoanState.setLazyDataModel( getOnLoanLdm() );
	}

	@Override
	public String getBeanPluralBinding() {
		return "transactions";
	}

	public PaymentRequiredAndAcknowledgedLazyDataModel getPaymentRequiredAndAcknowledgedLdm() {
		return paymentRequiredAndAcknowledgedLdm;
	}

	public void setPaymentRequiredAndAcknowledgedLdm(
			PaymentRequiredAndAcknowledgedLazyDataModel paymentRequiredAndAcknowledgedLdm) {
		this.paymentRequiredAndAcknowledgedLdm = paymentRequiredAndAcknowledgedLdm;
	}

	public BookedOutAndAwaitingDispatchLazyDataModel getBookedOutAndAwaitingDispatchLdm() {
		return bookedOutAndAwaitingDispatchLdm;
	}

	public void setBookedOutAndAwaitingDispatchLdm(
			BookedOutAndAwaitingDispatchLazyDataModel bookedOutAndAwaitingDispatchLdm) {
		this.bookedOutAndAwaitingDispatchLdm = bookedOutAndAwaitingDispatchLdm;
	}

	public OnLoanLazyDataModel getOnLoanLdm() {
		return onLoanLdm;
	}

	public void setOnLoanLdm(OnLoanLazyDataModel onLoanLdm) {
		this.onLoanLdm = onLoanLdm;
	}

	public class PaymentRequiredAndAcknowledgedLazyDataModel extends AplosLazyDataModel {

		/**
		 *
		 */
		private static final long serialVersionUID = 2351148358427009351L;

		public PaymentRequiredAndAcknowledgedLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
			getBeanDao().setListBeanClass(TransactionListBean.class);
			getBeanDao().setSelectCriteria( "bean.id, bean.ecommerceShoppingCart.customer.id, bean.invoiceNumber, bean.ecommerceShoppingCart.customer.subscriber.firstName, bean.ecommerceShoppingCart.customer.subscriber.surname, bean.ecommerceShoppingCart.customer.subscriber.emailAddress, bean.transactionStatus, bean.transactionType, bean.ecommerceShoppingCart.cachedTotalVatAmount, bean.ecommerceShoppingCart.cachedNetTotalAmount, bean.ecommerceShoppingCart.deliveryCost, bean.transactionDate, bean.dateCreated, bean.deliveryRequiredByDate, bean.deletable, bean.active" );
			getBeanDao().addWhereCriteria("bean.transactionStatus = " + TransactionStatus.PAYMENT_REQUIRED.ordinal() + " OR bean.transactionStatus = " + TransactionStatus.ACKNOWLEDGED.ordinal());
		}

		@Override
		public List<Object> load(int first, int pageSize, String sortField,	SortOrder sortOrder, Map<String, String> filters) {
			List<Object> objects = super.load(first, pageSize, sortField, sortOrder, filters);
			TransactionListBean.determineCompanyNames(objects);
			return objects;
		}

		@Override
		public AplosBean selectBean() {
			Transaction transaction = (Transaction) super.selectBean( false );
			JSFUtil.redirect( TransactionEditPage.class );
			return transaction;
		}

		@Override
		public String getSearchCriteria() {
			return "CONCAT(bean.ecommerceShoppingCart.customer.subscriber.firstName, ' ', bean.ecommerceShoppingCart.customer.subscriber.surname) LIKE :similarSearchText";
		}

	}

	public class BookedOutAndAwaitingDispatchLazyDataModel extends AplosLazyDataModel {

		/**
		 *
		 */
		private static final long serialVersionUID = 2123404150254407923L;

		public BookedOutAndAwaitingDispatchLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
			getBeanDao().setListBeanClass(TransactionListBean.class);
			getBeanDao().setSelectCriteria( "bean.id, bean.ecommerceShoppingCart.customer.id, bean.invoiceNumber, bean.ecommerceShoppingCart.customer.subscriber.firstName, bean.ecommerceShoppingCart.customer.subscriber.surname, bean.ecommerceShoppingCart.customer.subscriber.emailAddress, bean.transactionStatus, bean.transactionType, bean.ecommerceShoppingCart.cachedTotalVatAmount, bean.ecommerceShoppingCart.cachedNetTotalAmount, bean.ecommerceShoppingCart.deliveryCost, bean.transactionDate, bean.dateCreated, bean.deliveryRequiredByDate, bean.deletable, bean.active" );
			getBeanDao().addWhereCriteria("bean.transactionStatus = " + TransactionStatus.BOOKED_OUT.ordinal() + " OR bean.transactionStatus = " + TransactionStatus.AWAITING_DISPATCH.ordinal());
		}

		@Override
		public List<Object> load(int first, int pageSize, String sortField,	SortOrder sortOrder, Map<String, String> filters) {
			List<Object> objects = super.load(first, pageSize, sortField, sortOrder, filters);
			TransactionListBean.determineCompanyNames(objects);
			return objects;
		}

		@Override
		public AplosBean selectBean() {
			Transaction transaction = (Transaction) super.selectBean( false );
			JSFUtil.redirect( TransactionEditPage.class );
			return transaction;
		}

		@Override
		public String getSearchCriteria() {
			return "CONCAT(bean.ecommerceShoppingCart.customer.subscriber.firstName, ' ', bean.ecommerceShoppingCart.customer.subscriber.surname) LIKE :similarSearchText";
		}
	}

	public class OnLoanLazyDataModel extends AplosLazyDataModel {

		/**
		 *
		 */
		private static final long serialVersionUID = 5238606014690125937L;

		public OnLoanLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
			getBeanDao().setListBeanClass(TransactionListBean.class);
			getBeanDao().setSelectCriteria( "bean.id, bean.ecommerceShoppingCart.customer.id, bean.invoiceNumber, bean.ecommerceShoppingCart.customer.subscriber.firstName, bean.ecommerceShoppingCart.customer.subscriber.surname, bean.ecommerceShoppingCart.customer.subscriber.emailAddress, bean.transactionStatus, bean.transactionType, bean.ecommerceShoppingCart.cachedTotalVatAmount, bean.ecommerceShoppingCart.cachedNetTotalAmount, bean.ecommerceShoppingCart.deliveryCost, bean.transactionDate, bean.dateCreated, bean.deliveryRequiredByDate, bean.deletable, bean.active" );
			getBeanDao().addWhereCriteria("bean.transactionStatus = " + TransactionStatus.ON_LOAN.ordinal());
		}

		@Override
		public List<Object> load(int first, int pageSize, String sortField,	SortOrder sortOrder, Map<String, String> filters) {
			List<Object> objects = super.load(first, pageSize, sortField, sortOrder, filters);
			TransactionListBean.determineCompanyNames(objects);
			return objects;
		}

		@Override
		public AplosBean selectBean() {
			Transaction transaction = (Transaction) super.selectBean( false );
			JSFUtil.redirect( TransactionEditPage.class );
			return transaction;
		}

		@Override
		public String getSearchCriteria() {
			return "CONCAT(bean.ecommerceShoppingCart.customer.subscriber.firstName, ' ', bean.ecommerceShoppingCart.customer.subscriber.surname) LIKE :similarSearchText";
		}
	}
}
