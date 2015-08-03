package com.aplos.ecommerce.backingpage.shipping;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.DataTableState;
import com.aplos.ecommerce.beans.couriers.CourierService;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=CourierService.class)
public class CourierServiceListPage extends ListPage {
	private static final long serialVersionUID = 3178983007395191452L;

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new CourierServiceLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class CourierServiceLazyDataModel extends AplosLazyDataModel {

		private static final long serialVersionUID = -2447162773146020323L;

		public CourierServiceLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
		}

		@Override
		public String getSearchCriteria() {
			return "bean.name LIKE :similarSearchText OR bean.collectionTime LIKE :similarSearchText";
		}

	}
}
