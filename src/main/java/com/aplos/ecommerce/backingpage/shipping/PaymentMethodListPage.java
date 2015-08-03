package com.aplos.ecommerce.backingpage.shipping;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.DataTableState;
import com.aplos.ecommerce.beans.PaymentMethod;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=PaymentMethod.class)
public class PaymentMethodListPage extends ListPage  {
	private static final long serialVersionUID = -6495596746669248518L;

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new PaymentMethodLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class PaymentMethodLazyDataModel extends AplosLazyDataModel {

		private static final long serialVersionUID = 4982972132319611770L;

		public PaymentMethodLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
		}

		@Override
		public String getSearchCriteria() {
			return "bean.name LIKE :similarSearchText OR bean.positionIdx LIKE :similarSearchText";
		}

	}
}
