package com.aplos.ecommerce.backingpage.product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosAbstractBean;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.IncludedProduct;
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.beans.product.ProductColour;
import com.aplos.ecommerce.beans.product.ProductInfo;
import com.aplos.ecommerce.beans.product.ProductSize;
import com.aplos.ecommerce.beans.product.ProductType;
import com.aplos.ecommerce.module.EcommerceConfiguration;

@ManagedBean
@ViewScoped
public class FullKitStackEditPage extends FullProductStackEditPage {

	private static final long serialVersionUID = 8027797180815135241L;

	public FullKitStackEditPage() {
		super();
		ProductInfo productInfo = JSFUtil.getBeanFromScope( ProductInfo.class );
		productInfo.setKitItem(true);
	}

	//we wont show a grid so updatesizes doesn't need overriding because they will never be called
	@Override
	public void updateSizesAndColours() {
		ProductInfo productInfo = JSFUtil.getBeanFromScope( ProductInfo.class );
		ProductSize standardSize = EcommerceConfiguration.getEcommerceConfiguration().getStandardProductSize();
		ProductColour standardColour = EcommerceConfiguration.getEcommerceConfiguration().getStandardProductColour();
		if (productInfo.isNew()) {
			//sizes
			setSizeList(new ArrayList<ProductSize>());
			getSizeList().add(standardSize);
			//colours
			setColourList(new ArrayList<ProductColour>());
			getColourList().add(standardColour);
			if (getLightboxColourMap().get(standardColour) == null) {
				getLightboxColourMap().put(standardColour, new RealizedProduct().<RealizedProduct>initialiseNewBean() );
			}
		} else {
			super.updateSizesAndColours();
		}
		//triggers creation of the required realizedproduct if it doesn't exist and keeps it active if it does
		getGridRealizedProduct(standardColour, standardSize, null);
	}

	@Override
	public void addSelectedIncludedProduct() {
		super.addSelectedIncludedProduct();
		updateColours();
	}

	@Override //colours on kits also run off of included products as well as the rp's themselves
	public void updateColours() {
		//first call super to find out what colours we already have
		super.updateColours();
		List<ProductColour> currentColourList = getColourList();
		List<ProductColour> validColourList = new ArrayList<ProductColour>();
		ProductColour standardColour = EcommerceConfiguration.getEcommerceConfiguration().getStandardProductColour();
		ProductInfo productInfo = JSFUtil.getBeanFromScope( ProductInfo.class );
		//now REMOVE the colours we don't have available on our kit items
			// get the list of all colours available to our kit items
			Set<ProductColour> colourSet = getKitItemColourSet(productInfo);
			// check each item in kitColourList against the list we just made
			for (ProductColour currentColour : currentColourList) {
				if (colourSet.contains(currentColour) || currentColour.equals(standardColour)) {
					validColourList.add(currentColour);
				}
			}
			// add standard if we have nothing else left
			if (validColourList.size() < 1) {
				validColourList.add(standardColour);
			}
		Collections.sort(validColourList);
		setColourList(validColourList);
	}

	@Override
	public String addColour() {
		super.addColour();
		//triggers creation of the required realizedproduct if it doesn't exist and keeps it active if it does
		ProductSize standardSize = EcommerceConfiguration.getEcommerceConfiguration().getStandardProductSize();
		getGridRealizedProduct(super.getSelectedColour(), standardSize, null);
		return null;
	}

	@Override
	public void okBtnAction() {
		ProductInfo productInfo = JSFUtil.getBeanFromScope( ProductInfo.class );
		productInfo.setKitItem(true);
		super.okBtnAction();
	}

	@Override
	public void redirectToPage( AplosBean aplosBean ) {
		ProductInfo loadedAplosBean  = new BeanDao( ProductInfo.class ).get( aplosBean.getId() );
		loadedAplosBean.addToScope();
//		if (loadedAplosBean.isKitItem()) {
//			return new BackingPageUrl(FullKitStackEditPage.class);
//		} else {
//			JSFUtil.redirect(getBeanDao().getEditPageClass());
//		}
		JSFUtil.redirect( FullProductStackEditPage.class );
	}

	public SelectItem[] getStandardProductTypeSelectItemBeansWithNotSelected() {
		if( EcommerceConfiguration.getEcommerceConfiguration().getStandardProductSize().getProductSizeType() == null ) {
			return new SelectItem[ 0 ];
		} else {
			BeanDao dao = new BeanDao(ProductType.class);
			dao.setWhereCriteria("bean.productSizeType.id=" + EcommerceConfiguration.getEcommerceConfiguration().getStandardProductSize().getProductSizeType().getId());
			return AplosAbstractBean.getSelectItemBeansWithNotSelected(dao.setIsReturningActiveBeans(true).getAll(), "Not Selected");
		}
	}

	public List<SelectItem> getKitProductColourSelectItemBeansWithNotSelected() {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		Set<ProductColour> colourSet = new HashSet<ProductColour>();
		SelectItem nullItem = new SelectItem(null, "Not Selected");;
		ProductInfo productInfo = JSFUtil.getBeanFromScope( ProductInfo.class );
		if (productInfo.getIncludedProducts() == null || productInfo.getIncludedProducts().size() < 1) {
			nullItem = new SelectItem(null, "Please add items to the kit before selecting colours");
		}
		if (productInfo.getIncludedProducts() != null) {
			colourSet = getKitItemColourSet(productInfo);
			if (colourSet.size() < 1) {
				nullItem = new SelectItem(null, "The Kit's items contain no valid colours");
			}
		}
		selectItems.add(nullItem);
		for (ProductColour productColour : colourSet) {
			selectItems.add(new SelectItem(productColour, productColour.getDisplayName()));
		}
		return selectItems;
	}

	//TODO: any future RealizedProductRetrievers need to be added here
	public Set<ProductColour> getKitItemColourSet(ProductInfo productInfo) {
		Set<ProductColour> colourSet = new HashSet<ProductColour>();
		if (productInfo.getIncludedProducts() != null) {
			for (IncludedProduct includedProduct : productInfo.getIncludedProducts()) {
				if (includedProduct.getRealizedProductRetriever() instanceof ProductInfo) {
					Long productInfoId = ((ProductInfo)includedProduct.getRealizedProductRetriever()).getId();
					BeanDao realizedProductDao = new BeanDao(RealizedProduct.class);
					realizedProductDao.setWhereCriteria("bean.productInfo.id=" + productInfoId);
					List<RealizedProduct> realizedProducts = realizedProductDao.setIsReturningActiveBeans(true).getAll();
					for (RealizedProduct realizedProduct : realizedProducts) {
						colourSet.add(realizedProduct.getProductColour());
					}
				} else {
					colourSet.add(((RealizedProduct)includedProduct.getRealizedProductRetriever()).getProductColour());
				}
			}
		}
		return colourSet;
	}

}












