package com.aplos.ecommerce.backingpage.product;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.product.Product;
import com.aplos.ecommerce.beans.product.ProductInfo;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=ProductInfo.class)
public class ProductInfoListPage extends ListPage  {

	private static final long serialVersionUID = 1815502016760432314L;

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new ProductInfoLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class ProductInfoLazyDataModel extends AplosLazyDataModel {

		private static final long serialVersionUID = 1775459244253119434L;

		public ProductInfoLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
		}

		@Override
		public void goToNew() {
			ProductInfo newBean = (ProductInfo)getBeanDao().getNew();
			Product newProduct = new Product().<Product>initialiseNewBean();
			newBean.setProduct(newProduct);

			newBean.addToScope();

			JSFUtil.redirect( determineEditPageClass( newBean ) );
		}

		public String openStockPage() {
			AplosBean aplosBean = (AplosBean) JSFUtil.getRequest().getAttribute( "tableBean" );
//			AplosBean loadedAplosBean = (AplosBean) HibernateUtil.getCurrentSession().load( aplosBean.getClass(), aplosBean.getId() );
//			HibernateUtil.initialise( loadedAplosBean, true );
			aplosBean.addToScope();
			return "realizedProductList";
		}

		@Override
		public void deleteBean() {
			AplosBean deleteBean = getDeleteBean();
			AplosBean loadedBean = (AplosBean) new BeanDao( deleteBean.getClass() ).get( deleteBean.getId() );

			if( loadedBean.checkForDelete() ) {
				loadedBean.delete();
				ApplicationUtil.executeSql("UPDATE realizedproduct SET active=false WHERE productInfo_id=" + loadedBean.getId());
			}
		}

	}

}


