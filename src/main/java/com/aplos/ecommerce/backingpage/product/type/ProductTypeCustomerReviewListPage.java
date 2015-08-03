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
import com.aplos.common.beans.CustomerReview;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.product.ProductType;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=CustomerReview.class)
public class ProductTypeCustomerReviewListPage extends ListPage  {

	private static final long serialVersionUID = 775268997623101123L;

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new ProductReviewLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class ProductReviewLazyDataModel extends AplosLazyDataModel {

		private static final long serialVersionUID = 6756663710090992807L;

		public ProductReviewLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
		}

		@Override
		public void goToNew() {
			super.goToNew();
			CustomerReview customerReview = JSFUtil.getBeanFromScope( CustomerReview.class );
			ProductType productType = JSFUtil.getBeanFromScope( ProductType.class );
			customerReview.setParent( productType );
		}

		@Override
		public List<Object> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
			getBeanDao().clearWhereCriteria();
			ProductType productType = JSFUtil.getBeanFromScope( ProductType.class );
			getBeanDao().addWhereCriteria( "bean.parent.id = " + productType.getId() + " AND bean.parent.class = '" + AplosBean.getTableName( ProductType.class ) + "'" );
			return super.load(first, pageSize, sortField, sortOrder, filters);
		}

	}

//	public class ProductTypeCustomerReviewTblCom extends DaoTableComponent {
//
//		public ProductTypeCustomerReviewTblCom(AqlBeanDao dao) {
//			super(dao);
//			setEditPageNavigation(new BackingPageUrl( CustomerReviewEditPage.class ));
//		}
//
//		@Override
//		public void refreshDaoTableComponent() {
//			super.refreshDaoTableComponent();
//			getBeanDao().clearWhereCriteria();
//			ProductType productType = JSFUtil.getBeanFromScope( ProductType.class );
//			getBeanDao().addWhereCriteria( "bean.parent.id = " + productType.getId() + " AND bean.parent.class = '" + AplosBean.getTableName( ProductType.class ) + "'" );
//		}
//
//		@Override
//		public void goToNew() {
//			String navStr = super.goToNew();
//			CustomerReview customerReview = JSFUtil.getBeanFromScope( CustomerReview.class );
//			ProductType productType = JSFUtil.getBeanFromScope( ProductType.class );
//			customerReview.setParent( productType );
//			return navStr;
//		}
//
//		@Override
//		public String getSearchCriteria() {
//			return "bean.id LIKE :exactSearchText OR bean.reviewTitle LIKE :similarSearchText";
//		}
//	}
}
