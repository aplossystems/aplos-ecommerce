package com.aplos.ecommerce.backingpage.transaction;

import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.SortOrder;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.DataTableState;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.beans.Transaction.TransactionStatus;
import com.aplos.ecommerce.beans.listbeans.TransactionListBean;

@ManagedBean
@SessionScoped
public class AbandonedTransactionListPage extends TransactionListPage  {

	private static final long serialVersionUID = -8576098994921803678L;

	@Override
	public BeanDao getListBeanDao() {
		return new BeanDao( Transaction.class );
	}

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new AbandonedTransactionLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class AbandonedTransactionLazyDataModel extends TransactionLazyDataModel {

		private static final long serialVersionUID = 4039958173987992696L;

		public AbandonedTransactionLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
			setEndDate(null);
			setStartDate(null);
			getDataTableState().setShowingNewBtn(false);
			getBeanDao().setSelectCriteria( "bean.id, bean.ecommerceShoppingCart.customer.id, bean.ecommerceShoppingCart.customer.subscriber.firstName, bean.ecommerceShoppingCart.customer.subscriber.surname, bean.ecommerceShoppingCart.customer.subscriber.emailAddress, bean.transactionStatus, bean.transactionType, bean.ecommerceShoppingCart.lastRecordedPageEntry, bean.transactionDate, bean.deletable, bean.dateCreated, bean.active" );
		}

		@Override
		public List<Object> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
			getBeanDao().clearWhereCriteria();
			getBeanDao().addWhereCriteria("bean.transactionStatus=" + TransactionStatus.INCOMPLETE.ordinal());
			getBeanDao().addWhereCriteria("bean.ecommerceShoppingCart!=null AND bean.ecommerceShoppingCart.customer!=null AND bean.ecommerceShoppingCart.customer.subscriber!=null");
			//addFilteringWhereCriteria();
			List<Object> results = super.load(first, pageSize, sortField, sortOrder, filters, false);
			TransactionListBean.determineCompanyNames(results);
			return results;
		}

		@Override
		public void addSpecificWhereCriteria() {
			/*
			 *  don't call the super method otherwise it won't include
			 *  incomplete orders.
			 */
		}

	}


}
