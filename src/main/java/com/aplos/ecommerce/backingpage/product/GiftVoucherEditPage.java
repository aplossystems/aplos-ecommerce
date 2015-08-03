package com.aplos.ecommerce.backingpage.product;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.product.ProductInfo;
import com.aplos.ecommerce.beans.product.ProductSizeType;
import com.aplos.ecommerce.beans.product.ProductType;
import com.aplos.ecommerce.module.EcommerceConfiguration;

@ManagedBean
@ViewScoped
public class GiftVoucherEditPage extends FullProductStackEditPage {

	private static final long serialVersionUID = -791452874934926663L;

	public GiftVoucherEditPage() {
		super();
	}
	
	@Override
	public boolean responsePageLoad() {
		ProductInfo productInfo = JSFUtil.getBeanFromScope( ProductInfo.class );
		if (productInfo.isNew() && getSelectedColour() == null ) {
			setSelectedColour(EcommerceConfiguration.getEcommerceConfiguration().getStandardProductColour());
			addColour();
		}
		boolean continueLoad = super.responsePageLoad();
		return continueLoad;
	}
	

	@Override
	public void okBtnAction() {
		ProductInfo productInfo = JSFUtil.getBeanFromScope( ProductInfo.class );
		if (getPrice() != null && productInfo.getProduct().getName() == null || productInfo.getProduct().getName().equals("")) {
			productInfo.getProduct().setName(CommonConfiguration.getCommonConfiguration().getDefaultCurrency().getPrefixOrSuffix() + getPrice() + " Gift Voucher");
		}
		productInfo.setActive(true);
		productInfo.getProduct().setProductBrand(null);
		saveAction();
		JSFUtil.redirect(GiftVoucherListPage.class);

	}

	@Override
	public void cancelBtnAction() {
		JSFUtil.redirect(GiftVoucherListPage.class);
	}

	@Override
	public void updateSizes() {
		ProductInfo productInfo = JSFUtil.getBeanFromScope( ProductInfo.class );
		if (productInfo.getProduct().getAdditionalProductSizeTypes() == null ||
			productInfo.getProduct().getAdditionalProductSizeTypes().size() < 1) {
			List<ProductSizeType> sizeTypes = new ArrayList<ProductSizeType>();
			for (ProductType type : productInfo.getProduct().getProductTypes()) {
				sizeTypes.add(type.getProductSizeType());
			}
			productInfo.getProduct().setAdditionalProductSizeTypes(sizeTypes);
		}
		super.updateSizes();
	}

}
