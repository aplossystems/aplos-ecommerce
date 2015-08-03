package com.aplos.ecommerce.backingpage;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.DataTableState;
import com.aplos.ecommerce.backingpage.reports.ProductSalesListPage;

@ManagedBean
@ViewScoped
public class CustomProductSalesListPage extends ProductSalesListPage {
	private static final long serialVersionUID = -8885575057080003528L;

	public CustomProductSalesListPage() {
	}

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new CustomProductSalesLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class CustomProductSalesLazyDataModel extends ProductSalesLazyDataModel {
		private static final long serialVersionUID = -8248787815350622296L;

		public CustomProductSalesLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
			aqlBeanDao.setGroupBy( "bean.itemName" );
			aqlBeanDao.setOrderBy( "bean.itemName" );
		}

		@Override
		public void addOverridableWhereCriteria() {
			getBeanDao().addWhereCriteria( "rp = null" );
		}

	}
}
