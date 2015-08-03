package com.aplos.ecommerce.backingpage.product;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.DiscountAllowance;
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.beans.listbeans.RealizedProductListBean;
import com.aplos.ecommerce.beans.product.ProductGroup;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=ProductGroup.class)
public class ProductGroupEditPage extends EditPage {
	private static final long serialVersionUID = -1487261036335962617L;

	private RealizedProduct selectedRealizedProductListBean;

	@SuppressWarnings("unchecked")
	public List<RealizedProductListBean> suggestRealizedProducts(String searchStr) {
		BeanDao realizedProductDao = new BeanDao( RealizedProduct.class );
		realizedProductDao.setListBeanClass(RealizedProductListBean.class);
		realizedProductDao.setSelectCriteria( "bean.id, bean.productInfo.product.name as productName, bean.price, bean.itemCode, bean.productInfo.product.itemCode, bean.discontinued, bean.active" );
		realizedProductDao.addWhereCriteria("bean.productInfo.product.name like :similarSearchText OR bean.itemCode like :similarSearchText");
		realizedProductDao.setMaxResults( 20 );
		realizedProductDao.setIsReturningActiveBeans( true );
		realizedProductDao.setNamedParameter( "similarSearchText", "%" + searchStr + "%" );
		return realizedProductDao.getAll();
	}

	public String getProductDisplayString( RealizedProductListBean realizedProductListBean ) {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append( CommonUtil.getStringOrEmpty( realizedProductListBean.determineItemCode() ) );
		strBuf.append( " " +  realizedProductListBean.getProductName() );

		return strBuf.toString();
	}

	public void setSelectedRealizedProduct( SelectEvent event ) {
		RealizedProductListBean realizedProductListBean = (RealizedProductListBean) event.getObject();
		RealizedProduct loadedRealizedProduct = (RealizedProduct) new BeanDao( RealizedProduct.class ).get( realizedProductListBean.getId() );
		setSelectedRealizedProductListBean( null );

		ProductGroup productGroup = JSFUtil.getBeanFromScope( ProductGroup.class );
		if( loadedRealizedProduct != null ) {
//			HibernateUtil.initialise( loadedRealizedProduct, true );
			if( productGroup.getProductRetrieverList().contains( loadedRealizedProduct ) ) {
				JSFUtil.addMessage( "This product is already included in the optional accessory list" );
			} else {
				productGroup.getProductRetrieverList().add( loadedRealizedProduct );
			}
		} else {
			JSFUtil.addMessage( "Please select a product to add to the optional accessory list" );
		}
	}

	public void removeRealizedProduct() {
		ProductGroup productGroup = JSFUtil.getBeanFromScope( ProductGroup.class );
		RealizedProduct realizedProductItem = (RealizedProduct) JSFUtil.getRequest().getAttribute( "tableBean" );
		productGroup.getProductRetrieverList().remove( realizedProductItem );
	}

	public RealizedProduct getSelectedRealizedProductListBean() {
		return selectedRealizedProductListBean;
	}

	public void setSelectedRealizedProductListBean(
			RealizedProduct selectedRealizedProductListBean) {
		this.selectedRealizedProductListBean = selectedRealizedProductListBean;
	}

}
