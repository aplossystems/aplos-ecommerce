package com.aplos.ecommerce.backingpage.product;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.DataTableState;
import com.aplos.ecommerce.beans.product.ProductBrand;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=ProductBrand.class)
public class ProductBrandListPage extends ListPage  {

	private static final long serialVersionUID = 1700718193569195710L;

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new ProductBrandLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class ProductBrandLazyDataModel extends AplosLazyDataModel {

		private static final long serialVersionUID = 7634587148778045714L;

		public ProductBrandLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
		}

		@Override
		public String getSearchCriteria() {
			return "bean.name LIKE :similarSearchText";
		}

	}
}
