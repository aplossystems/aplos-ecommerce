package com.aplos.ecommerce.backingpage.product;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.DataTableState;
import com.aplos.ecommerce.beans.product.ProductFaq;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=ProductFaq.class)
public class ProductFaqListPage extends ListPage  {
	private static final long serialVersionUID = -2123136533392830513L;

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new ProductFaqLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class ProductFaqLazyDataModel extends AplosLazyDataModel {

		private static final long serialVersionUID = 7634587148778045714L;

		public ProductFaqLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
		}

		@Override
		public String getSearchCriteria() {
			return "bean.title LIKE :similarSearchText OR bean.category LIKE :similarSearchText";
		}

	}
}
