package com.aplos.ecommerce.backingpage.shipping;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.DataTableState;
import com.aplos.ecommerce.beans.couriers.ShippingBox;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=ShippingBox.class)
public class ShippingBoxListPage extends ListPage {
	private static final long serialVersionUID = 1174035050740586492L;

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new ShippingBoxLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class ShippingBoxLazyDataModel extends AplosLazyDataModel {

		private static final long serialVersionUID = 1033664520579217695L;

		public ShippingBoxLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
		}

		@Override
		public String getSearchCriteria() {
			return "bean.name LIKE :similarSearchText";
		}

	}
}
