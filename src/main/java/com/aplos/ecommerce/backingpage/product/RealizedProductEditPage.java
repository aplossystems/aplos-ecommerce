package com.aplos.ecommerce.backingpage.product;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.appconstants.AplosScopedBindings;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.BackingPage;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.backingpage.OkBtnListener;
import com.aplos.common.beans.AplosAbstractBean;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.beans.product.ProductSize;
import com.aplos.ecommerce.beans.product.ProductSizeType;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=RealizedProduct.class)
public class RealizedProductEditPage extends EditPage {

	private static final long serialVersionUID = -3980313162779801512L;
	private boolean isDefaultRealizedProduct;

	public RealizedProductEditPage() {
		getEditPageConfig().setOkBtnActionListener( new RealizedProductOkBtnListener(this) );
		setPrependMainFormId( true );
	}

	@SuppressWarnings("unchecked")
	public SelectItem[] getProductSizeSelectItems() {
		RealizedProduct realizedProduct = (RealizedProduct) resolveAssociatedBean();
		List<ProductSizeType> productSizeTypes = realizedProduct.getProductInfo().getProduct().getProductSizeTypes();

		if (productSizeTypes.size() > 0) {
			BeanDao productSizeDao = new BeanDao( ProductSize.class );
			productSizeDao.addWhereCriteria( "bean.productSizeType IN (" + AplosAbstractBean.getIdListStrWithCommas( productSizeTypes ) + ")" );
			productSizeDao.setOrderBy( "bean.positionIdx" );
			List<? extends AplosAbstractBean> productSizeList = productSizeDao.setIsReturningActiveBeans(true).getAll();
			return AplosAbstractBean.getSelectItemBeans( productSizeList );
		} else {
			return new SelectItem[0];
		}
	}

	public void removeImageDetailsFromList() {
		RealizedProduct realizedProduct = (RealizedProduct) resolveAssociatedBean();
		// Mark the files for deletion
		FileDetails imageDetails = (FileDetails) JSFUtil.getRequest().getAttribute( AplosScopedBindings.TABLE_BEAN );
		realizedProduct.getImageDetailsList().remove( imageDetails );
		if( realizedProduct.getDefaultImageDetails().equals( imageDetails ) ) {
			if( realizedProduct.getImageDetailsList().size() > 0 ) {
				realizedProduct.setDefaultImageDetails( realizedProduct.getImageDetailsList().get( 0 ) );
			}
		}
		if( !realizedProduct.isNew() ) {
			realizedProduct.saveDetails();
		}
	}

	public void setDefaultImageDetails(FileDetails defaultImageDetails) {
		RealizedProduct realizedProduct = (RealizedProduct) JSFUtil.getBeanFromScope( RealizedProduct.class );
		if( defaultImageDetails != null || realizedProduct.getImageDetailsList().size() == 0 ) {
			realizedProduct.setDefaultImageDetails( defaultImageDetails );
		}
	}

	public FileDetails getDefaultImageDetails() {
		RealizedProduct realizedProduct = (RealizedProduct) JSFUtil.getBeanFromScope( RealizedProduct.class );
		return realizedProduct.getDefaultImageDetails();
	}

	public SelectItem[] getDefaultImageSelectItems() {
		List<FileDetails> realizedProductList = ((RealizedProduct) resolveAssociatedBean()).getImageDetailsList();
		SelectItem[] selectItems;

		selectItems = new SelectItem[ realizedProductList.size() ];

		for( int i = 0, n = selectItems.length; i < n; i++ ) {
			selectItems[ i ] = new SelectItem( realizedProductList.get( i ), "" );
		}

		return selectItems;
	}

	public void setDefaultRealizedProduct(boolean isDefaultRealizedProduct) {
		this.isDefaultRealizedProduct = isDefaultRealizedProduct;
	}

	public boolean isDefaultRealizedProduct() {
		return isDefaultRealizedProduct;
	}

	public class RealizedProductOkBtnListener extends OkBtnListener {
		private static final long serialVersionUID = 6238951132967856231L;

		public RealizedProductOkBtnListener( BackingPage backingPage ) {
			super( backingPage );
		}

		@Override
		public void actionPerformed(boolean redirect) {
			RealizedProduct realizedProduct = resolveAssociatedBean();
			if( isDefaultRealizedProduct && !realizedProduct.isDefault() ) {
				realizedProduct.getProductInfo().setDefaultRealizedProduct( realizedProduct );
				realizedProduct.getProductInfo().saveDetails();
			}
			super.actionPerformed(redirect);
		}
	}
}
