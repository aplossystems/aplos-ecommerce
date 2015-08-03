package com.aplos.ecommerce.developermodulebacking.frontend;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.beans.ShoppingCart;
import com.aplos.common.enums.CartAbandonmentIssue;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.EcommerceShoppingCart;
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.utils.EcommerceUtil;

@ManagedBean
@ViewScoped
public class CustomerWishlistFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = -4130476300059976184L;

	@Override
	public boolean genericPageLoad( DeveloperCmsAtom developerCmsAtom ) {
		super.genericPageLoad(developerCmsAtom);
		Customer customer = JSFUtil.getBeanFromScope(Customer.class);
		EcommerceUtil.getEcommerceUtil().checkCustomerLogin(customer, "the checkout process" );
		return true;
	}

	public void addToCart() {
		EcommerceShoppingCart ecommerceShoppingCart = JSFUtil.getBeanFromScope(ShoppingCart.class);
		if (ecommerceShoppingCart==null) {
			ecommerceShoppingCart = EcommerceUtil.getEcommerceUtil().createShoppingCart();
			ecommerceShoppingCart.addToScope();
		}
		RealizedProduct realizedProduct = (RealizedProduct)JSFUtil.getRequest().getAttribute("wlItem");
		realizedProduct.addToScope();

		if( realizedProduct.getQuantity() > 0 ) {

			ecommerceShoppingCart.addToCart( realizedProduct, true );
			ecommerceShoppingCart.saveDetails();
			JSFUtil.addMessage("Item has been added to cart.", FacesMessage.SEVERITY_INFO);

		} else {
			EcommerceUtil.getEcommerceUtil().addAbandonmentIssueToCart( CartAbandonmentIssue.PRODUCT_OUT_OF_STOCK );
			JSFUtil.addMessage( "This product is out stock" );
		}


	}

}
