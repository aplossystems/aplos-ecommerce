package com.aplos.ecommerce.developermodulebacking.frontend;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.enums.CartAbandonmentIssue;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.EcommerceShoppingCart;
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.module.EcommerceConfiguration;
import com.aplos.ecommerce.utils.EcommerceUtil;

@ManagedBean
@ViewScoped
public class CrossSellingFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = 5917889355335038089L;

	public String addToCart() {
		RealizedProduct realizedProduct= (RealizedProduct) JSFUtil.getRequest().getAttribute("realProduct");
		if (realizedProduct != null) {
			if (realizedProduct.getQuantity() > 0 || EcommerceConfiguration.getEcommerceSettingsStatic().isOutOfStockProductAllowedOnOrder()  ||
				(realizedProduct.getStockAvailableFromDate() != null && EcommerceConfiguration.getEcommerceSettingsStatic().isPreOrderAllowedOnOrder())) {
				EcommerceShoppingCart ecommerceShoppingCart = EcommerceUtil.getEcommerceUtil().getOrCreateEcommerceShoppingCart();
				ecommerceShoppingCart.addToCart( realizedProduct, true );
				// We need to call this as hibernate screws up the bidirectional relationship between
				// the items in the cart and the cart.  It replaces the objects with different instances
				// which need to be replace to the right instance.
				ecommerceShoppingCart.saveDetails();
				ecommerceShoppingCart.refreshShoppingCartItemReferences();
			} else {
				EcommerceUtil.getEcommerceUtil().addAbandonmentIssueToCart( CartAbandonmentIssue.PRODUCT_OUT_OF_STOCK );
				JSFUtil.addMessage( "This product is out stock" );
				return null;
			}
		}
		return null;
	}

}

