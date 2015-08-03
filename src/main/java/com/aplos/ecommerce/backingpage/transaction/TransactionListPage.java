package com.aplos.ecommerce.backingpage.transaction;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.beans.communication.BulkMessageSourceGroup;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.backingpage.customer.CustomerTransactionListPage;
import com.aplos.ecommerce.beans.Transaction;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=Transaction.class)
public class TransactionListPage extends ListPage {
	private static final long serialVersionUID = 943664196962146594L;

	@Override
	public AplosLazyDataModel getAplosLazyDataModel( DataTableState dataTableState, BeanDao aqlBeanDao ) {
		return new TransactionLazyDataModel( dataTableState, aqlBeanDao );
	}

	public String viewCustomerOrderList() {
		Transaction transaction = (Transaction) JSFUtil.getTableBean();
		transaction.getCustomer().addToScope();
		JSFUtil.redirect(CustomerTransactionListPage.class);
		return null;
	}
	
	public boolean isCustomerView() {
		return false;
	}
	
}
