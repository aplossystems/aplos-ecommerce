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
import com.aplos.ecommerce.beans.product.ProductSize;
import com.aplos.ecommerce.beans.product.ProductSizeType;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=ProductSize.class)
public class ProductSizeListPage extends ListPage  {
	private static final long serialVersionUID = -8876973372547393306L;

	@Override
	public boolean responsePageLoad() {
		super.responsePageLoad();
		BeanDao productSizeDao = new BeanDao( ProductSize.class );
		ProductSizeType productSizeType = JSFUtil.getBeanFromScope( ProductSizeType.class );
		if (productSizeType != null) {
			productSizeDao.addWhereCriteria( "bean.productSizeType.id = " + productSizeType.getId() );
		}
		productSizeDao.setOrderBy("bean.positionIdx ASC");
		setBeanDao( productSizeDao );
		return true;
	}

	@Override
	public AplosLazyDataModel getAplosLazyDataModel( DataTableState dataTableState, BeanDao aqlBeanDao ) {
		return new ProductSizeLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class ProductSizeLazyDataModel extends AplosLazyDataModel {

		private static final long serialVersionUID = -8248787815350622296L;

		public ProductSizeLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
		}

		@Override
		public void goToNew() {
			super.goToNew();
			ProductSize productSize = JSFUtil.getBeanFromScope( ProductSize.class );
			productSize.setProductSizeType( (ProductSizeType) JSFUtil.getBeanFromScope( ProductSizeType.class ) );
			productSize.setOldPositionIdx(productSize.getPositionIdx());
		}

		@Override
		public List<Object> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
			ProductSizeType type = JSFUtil.getBeanFromScope(ProductSizeType.class);
			if (type != null) {
				getBeanDao().setWhereCriteria("bean.productSizeType.id = " + type.getId() );
			}
			return super.load(first, pageSize, sortField, sortOrder, filters);
		}

	}

}
