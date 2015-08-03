package com.aplos.ecommerce.backingpage.product;

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
import com.aplos.ecommerce.beans.ProductVersion;
import com.aplos.ecommerce.beans.product.Product;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=ProductVersion.class)
public class ProductVersionListPage extends ListPage  {

	private static final long serialVersionUID = -8253621509985402906L;

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new ProductVersionLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class ProductVersionLazyDataModel extends AplosLazyDataModel {

		private static final long serialVersionUID = -8396935008752711629L;

		public ProductVersionLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
		}

		@Override
		public void goToNew() {
			super.goToNew();
			ProductVersion productVersion = JSFUtil.getBeanFromScope( ProductVersion.class );
			Product product = JSFUtil.getBeanFromScope( Product.class );
			productVersion.setProduct( product );
			BeanDao versionNumberDao = new BeanDao( ProductVersion.class ).addWhereCriteria("bean.product.id = " + product.getId());
			versionNumberDao.setSelectCriteria( "MAX( bean.versionMajor )" );
			versionNumberDao.setIsReturningActiveBeans( true );
			Integer lastVersionNumber = (Integer) versionNumberDao.getFirstResult();
			if( lastVersionNumber != null ) {
				productVersion.setVersionMajor( lastVersionNumber + 1 );
			} else {
				productVersion.setVersionMajor( 1 );
			}
		}

		@Override
		public List<Object> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
			getBeanDao().clearWhereCriteria();
			Product product = JSFUtil.getBeanFromScope( Product.class );
			getBeanDao().addWhereCriteria( "bean.product.id = " + product.getId() );
			return super.load(first, pageSize, sortField, sortOrder, filters);
		}

	}

//	public class ProductVersionTblCom extends DaoTableComponent {
//
//		public ProductVersionTblCom(AqlBeanDao dao) {
//			super(dao);
//		}
//
//		@Override
//		public void refreshDaoTableComponent() {
//			super.refreshDaoTableComponent();
//			getBeanDao().clearWhereCriteria();
//			Product product = JSFUtil.getBeanFromScope( Product.class );
//			getBeanDao().addWhereCriteria( "bean.product.id = " + product.getId() );
//		}
//
//		@Override
//		public void goToNew() {
//			String navStr = super.goToNew();
//			ProductVersion productVersion = JSFUtil.getBeanFromScope( ProductVersion.class );
//			Product product = JSFUtil.getBeanFromScope( Product.class );
//			productVersion.setProduct( product );
//			AqlBeanDao versionNumberDao = new AqlBeanDao( ProductVersion.class ).addWhereCriteria("bean.product.id = " + product.getId());
//			versionNumberDao.setSelectCriteria( "MAX( bean.versionMajor )" );
//			Integer lastVersionNumber = (Integer) versionNumberDao.setIsReturningActiveBeans(true).getAllQuery().uniqueResult();
//			if( lastVersionNumber != null ) {
//				productVersion.setVersionMajor( lastVersionNumber + 1 );
//			} else {
//				productVersion.setVersionMajor( 1 );
//			}
//
//			return navStr;
//		}
//
//		@Override
//		public String getSearchCriteria() {
//			return "bean.major LIKE :exactSearchText";
//		}
//	}
}
