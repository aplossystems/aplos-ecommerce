package com.aplos.ecommerce.backingpage.product.size;

import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.SortOrder;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.product.ProductSizeCategory;
import com.aplos.ecommerce.beans.product.ProductSizeType;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=ProductSizeCategory.class)
public class ProductSizeCategoryListPage extends ListPage  {

	private static final long serialVersionUID = 7861142202858447547L;


	@Override
	public boolean responsePageLoad() {
		super.responsePageLoad();
		BeanDao productCategoryDao = new BeanDao( ProductSizeCategory.class );
		ProductSizeType productSizeType = JSFUtil.getBeanFromScope( ProductSizeType.class );
		productCategoryDao.addWhereCriteria( "bean.productSizeType.id = " + productSizeType.getId() );
		productCategoryDao.setOrderBy("bean.positionIdx ASC");
		setBeanDao( productCategoryDao );
		return true;
	}

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new ProductSizeCategoryLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class ProductSizeCategoryLazyDataModel extends AplosLazyDataModel {

		private static final long serialVersionUID = -8248787815350622296L;

		public ProductSizeCategoryLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
		}

		@Override
		public void goToNew() {
			super.goToNew();
			ProductSizeCategory productCategory = JSFUtil.getBeanFromScope( ProductSizeCategory.class );
			productCategory.setProductSizeType( (ProductSizeType) JSFUtil.getBeanFromScope( ProductSizeType.class ) );
			productCategory.setOldPositionIdx(productCategory.getPositionIdx());
		}

		@Override
		public List<Object> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
			ProductSizeType type = JSFUtil.getBeanFromScope(ProductSizeType.class);
			getBeanDao().setWhereCriteria("bean.productSizeType.id = " + type.getId() );
			return super.load(first, pageSize, sortField, sortOrder, filters);
		}

	}

}
