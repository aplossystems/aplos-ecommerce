package com.aplos.ecommerce.developermodulebacking.frontend;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.ProductTypeTechSupport;
import com.aplos.ecommerce.beans.product.ProductType;

@ManagedBean
@ViewScoped
public class ProductTypeTechSupportFeDmb extends DeveloperModuleBacking {

	private static final long serialVersionUID = 4267962864581307537L;
	private ProductType selectedProductType;

	public void productTypeSelected() {
		ProductTypeTechSupport productTypeTechSupport = JSFUtil.getBeanFromRequest( "productTypeTechSupport" );
		ProductType loadedProductType = new BeanDao( ProductType.class ).get( productTypeTechSupport.getProductType().getId() );
		selectedProductType = loadedProductType;
	}

	public void setSelectedProductType(ProductType selectedProductType) {
		this.selectedProductType = selectedProductType;
	}

	public ProductType getSelectedProductType() {
		return selectedProductType;
	}
}
