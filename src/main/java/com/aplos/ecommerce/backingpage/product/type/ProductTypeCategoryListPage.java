package com.aplos.ecommerce.backingpage.product.type;

import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.SortOrder;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.product.ProductSizeType;
import com.aplos.ecommerce.beans.product.ProductType;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=ProductType.class)
public class ProductTypeCategoryListPage extends ListPage  {
	private static final long serialVersionUID = 1671132575070280330L;

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new ProductSizeCategoryLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class ProductSizeCategoryLazyDataModel extends AplosLazyDataModel {

		private static final long serialVersionUID = -8248787815350622296L;

		public ProductSizeCategoryLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
			getBeanDao().addWhereCriteria( "bean.parentProductType = null" );
		}

		@Override
		public AplosBean selectBean() {
			ProductType productType = (ProductType) super.selectBean();
			JSFUtil.addToTabSession( ProductType.PARENT_PRODUCT_TYPE, productType );
			return productType;
		}

		@Override
		public void goToNew() {
			super.goToNew();
			AplosBean productType = (AplosBean) JSFUtil.getRequest().getAttribute( "tableBean" );
			JSFUtil.addToTabSession( ProductType.PARENT_PRODUCT_TYPE, productType );
		}

		@Override
		public List<Object> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
			ProductSizeType type = JSFUtil.getBeanFromScope(ProductSizeType.class);
			getBeanDao().setWhereCriteria("bean.productSizeType.id = " + type.getId() );
			return super.load(first, pageSize, sortField, sortOrder, filters);
		}

	}

}
