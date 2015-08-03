package com.aplos.ecommerce.backingpage.customer;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.backingpage.transaction.TransactionLazyDataModel;
import com.aplos.ecommerce.backingpage.transaction.TransactionListPage;
import com.aplos.ecommerce.beans.Customer;

@ManagedBean
@ViewScoped
public class CustomerTransactionListPage extends TransactionListPage {
	private static final long serialVersionUID = -1935893595604913133L;

	public CustomerTransactionListPage() {
		super();
	}
	
	public boolean isCustomerView() {
		return true;
	}

	@Override
	public AplosLazyDataModel getAplosLazyDataModel( DataTableState dataTableState, BeanDao aqlBeanDao ) {
		return new CompanyContactTransactionsLazyDataModel( dataTableState, aqlBeanDao );
	}

	public class CompanyContactTransactionsLazyDataModel extends TransactionLazyDataModel {

		private static final long serialVersionUID = -4172410792363490385L;

		public CompanyContactTransactionsLazyDataModel( DataTableState dataTableState, BeanDao aqlBeanDao ) {
			super(dataTableState, aqlBeanDao);
			getDataTableState().setShowingNewBtn(true);
		}

		@Override
		public java.util.List<Object> load(int first, int pageSize, String sortField, org.primefaces.model.SortOrder sortOrder, java.util.Map<String,String> filters) {
			Customer customer = JSFUtil.getBeanFromScope(Customer.class);
			getBeanDao().clearWhereCriteria();
			getBeanDao().addWhereCriteria( "bean.ecommerceShoppingCart.customer.id = " + customer.getId() );

			return super.load(first, pageSize, sortField, sortOrder, filters, false);
		};
	}
}
