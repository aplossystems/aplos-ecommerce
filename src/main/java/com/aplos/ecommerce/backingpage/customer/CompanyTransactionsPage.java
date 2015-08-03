package com.aplos.ecommerce.backingpage.customer;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.DataTableState;
import com.aplos.ecommerce.backingpage.transaction.TransactionEditPage;
import com.aplos.ecommerce.backingpage.transaction.TransactionListPage;
import com.aplos.ecommerce.beans.Transaction;

@ManagedBean
@ViewScoped
public class CompanyTransactionsPage extends TransactionListPage {

	private static final long serialVersionUID = 3516753427774743704L;

	public CompanyTransactionsPage() {
		super();
	}

	@Override
	public BeanDao getListBeanDao() {
		return new BeanDao(Transaction.class,TransactionEditPage.class );
	}

	@Override
	public AplosLazyDataModel getAplosLazyDataModel( DataTableState dataTableState, BeanDao aqlBeanDao ) {
		return new CompanyTransactionLdm( dataTableState, aqlBeanDao );
	}
}
