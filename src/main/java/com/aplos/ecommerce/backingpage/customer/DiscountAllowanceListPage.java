package com.aplos.ecommerce.backingpage.customer;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.DataTableState;
import com.aplos.ecommerce.beans.DiscountAllowance;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=DiscountAllowance.class)
public class DiscountAllowanceListPage extends ListPage  {
	private static final long serialVersionUID = 5897153815087018223L;

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new DiscountAllowanceLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class DiscountAllowanceLazyDataModel extends AplosLazyDataModel {

		private static final long serialVersionUID = 7634587148778045714L;

		public DiscountAllowanceLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
		}

		@Override
		public String getSearchCriteria() {
			return "bean.name LIKE :similarSearchText OR bean.discountPercentage LIKE :similarSearchText";
		}

	}
}
