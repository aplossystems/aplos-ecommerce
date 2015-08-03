package com.aplos.ecommerce.backingpage.product;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.beans.ebay.EbayManager;
import com.aplos.ecommerce.beans.product.ProductInfo;
import com.aplos.ecommerce.beans.product.ProductSizeCategory;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=RealizedProduct.class)
public class RealizedProductListPage extends ListPage  {

	private static final long serialVersionUID = -6160436410534015694L;

	@Override
	public boolean responsePageLoad() {
		final ProductInfo productInfo = JSFUtil.getBeanFromScope( ProductInfo.class );
		if (productInfo != null) {
			BeanDao rpDAO = new BeanDao( RealizedProduct.class ).addWhereCriteria( "bean.productInfo.product.id="+productInfo.getProduct().getId() );
			getDataTableState().getLazyDataModel().setBeanDao( rpDAO );
		} else {
			getDataTableState().getLazyDataModel().setBeanDao( new BeanDao( RealizedProduct.class ) );
		}
		return true;
	}

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new RealizedProductLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class RealizedProductLazyDataModel extends AplosLazyDataModel {

		private static final long serialVersionUID = -8442959475104365618L;

		public RealizedProductLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
		}

		public String openEbayPropogationPage() {
			AplosBean aplosBean = (AplosBean) JSFUtil.getRequest().getAttribute( "tableBean" );
//			AplosBean loadedAplosBean = (AplosBean) HibernateUtil.getCurrentSession().load( aplosBean.getClass(), aplosBean.getId() );
//			HibernateUtil.initialise( loadedAplosBean, true );
			EbayManager ebayManager = new EbayManager();
			ebayManager.setWorkingProduct(ebayManager.realizedProductToEbayItemType((RealizedProduct)aplosBean));
			//ebayManager.setWorkingProduct(ebayManager.getItemFromForm());
			ebayManager.addToScope();
			return "ebayProductEdit";
		}

		@Override
		public void deleteBean() {
			AplosBean deleteBean = getDeleteBean();
			AplosBean loadedBean = (AplosBean) new BeanDao( deleteBean.getClass() ).get( deleteBean.getId() );
			ProductInfo prodInfo = ((RealizedProduct)loadedBean).getProductInfo();
			if (prodInfo.getDefaultRealizedProduct().getId() == loadedBean.getId()) {
				prodInfo.takeNewDefaultRealizedProduct();
			}
			loadedBean.delete();
		}

	}

}














