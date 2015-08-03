package com.aplos.ecommerce.backingpage.marketing;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.DataTableState;
import com.aplos.ecommerce.beans.SitewideDiscount;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=SitewideDiscount.class)
public class SitewideDiscountListPage extends ListPage {
	private static final long serialVersionUID = 7578786839849856963L;

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new SitewideDiscountLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class SitewideDiscountLazyDataModel extends AplosLazyDataModel {

		private static final long serialVersionUID = 6119373791411182300L;

		public SitewideDiscountLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
		}

		@Override
		public void deleteBean() {
			super.deleteBean();
			SitewideDiscount.updateSitewideDiscounts();
		}
	}

}
