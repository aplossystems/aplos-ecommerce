package com.aplos.ecommerce.beans;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.product.ProductBrand;
import com.aplos.ecommerce.beans.product.ProductInfo;
import com.aplos.ecommerce.beans.product.ProductType;

@Entity
public class PromotionConstraint extends AplosBean {

	private static final long serialVersionUID = 2686135056479656767L;
	
	private BigDecimal discountPercentage;
	
	@ManyToOne
	private ProductBrand productBrand;
	@ManyToOne
	private ProductInfo productInfo;
	@ManyToOne
	private ProductType productType;
	
	public PromotionConstraint() {}
	
	public PromotionConstraint getCopy() {
		
		PromotionConstraint promotionConstraint = new PromotionConstraint();
		promotionConstraint.setDiscountPercentage(getDiscountPercentage());
		promotionConstraint.setProductBrand(productBrand);
		promotionConstraint.setProductInfo(productInfo);
		promotionConstraint.setProductType(productType);
		return promotionConstraint;
		
	}
	
	public boolean doesCartItemSatisfyConstraint(EcommerceShoppingCartItem ecommerceShoppingCartItem, String code) {
		List<EcommerceShoppingCartItem> itemList = new ArrayList<EcommerceShoppingCartItem>();
		itemList.add(ecommerceShoppingCartItem);
		return getCartItemWhichSatisfiesConstraint(itemList, code, false) != null;
	}

	/**
	 * Checks if this promotion constraint is valid for the items in the cart - where each item can only satisfy one constraint
	 * @return
	 */
	public EcommerceShoppingCartItem getCartItemWhichSatisfiesConstraint(List<EcommerceShoppingCartItem> ecommerceShoppingCartItems, String code, boolean displayMessages ) {

		boolean containsRequiredBrand = false;
		boolean containsRequiredInfo = false;
		boolean containsRequiredType = false;
		
		boolean anyContainsRequiredBrand = false;
		boolean anyContainsRequiredInfo = false;
		boolean anyContainsRequiredType = false;

		for (EcommerceShoppingCartItem item : ecommerceShoppingCartItems) {
			if( item.getRealizedProduct() != null ) {
				ProductInfo tempPi = item.getRealizedProduct().getProductInfo();
				
				containsRequiredInfo = false;
				containsRequiredBrand = false;
				containsRequiredType = false;
				
				if (tempPi != null) {
								
					if (productInfo != null) {
						if (productInfo.equals(tempPi)) {
							containsRequiredInfo = true;
						}
					} else {
						containsRequiredInfo = true;
					}
					
					if (productBrand != null) {
						if (productBrand.equals(tempPi.getProduct().getProductBrand())) {
							containsRequiredBrand = true;
						}
					} else {
						containsRequiredBrand = true;
					}
					
					if (productType != null) {
						for (ProductType tempType : tempPi.getProduct().getProductTypes()) {
							if (productType.equals(tempType)) {
								containsRequiredType = true;
								break;
							}
						}
					} else {
						containsRequiredType = true;
					}
					
				}
				
				if (!anyContainsRequiredBrand && containsRequiredBrand) {
					anyContainsRequiredBrand = true;
				}
				
				if (!anyContainsRequiredInfo && containsRequiredInfo) {
					anyContainsRequiredInfo = true;
				}
				
				if (!anyContainsRequiredType && containsRequiredType) {
					anyContainsRequiredType = true;
				}
				
				if (containsRequiredBrand && containsRequiredInfo && containsRequiredType) {
					//stop when we have the first product that matches all criteria
					return item;
				}
			}
		}
		
		//if we reach here, no item satisfies this constraint
		
		if (displayMessages && (!anyContainsRequiredBrand || !anyContainsRequiredInfo || !anyContainsRequiredType)) {
			JSFUtil.addMessageForError("The " + code + " promotional code is only valid when purchasing " + getPurchaseRequired() + ".");
		}
		
		return null;
	}

	private String getPurchaseRequired() {
		
		if (productInfo != null) {
			
			return productInfo.getDisplayName();
			
		} else { 
			
			String ret = null;
			
			if (productBrand != null) {
				ret = productBrand.getDisplayName();
			}
			
			if (productType != null) {
				if (ret == null) {
					ret = productType.getDisplayName();
				} else {
					ret += " " + productType.getDisplayName();
				}
			}
			
			return ret;
		}
		
	}

	public ProductBrand getProductBrand() {
		return productBrand;
	}

	public void setProductBrand(ProductBrand newProductBrand) {
		this.productBrand = newProductBrand;
	}

	public void setProductInfo(ProductInfo productInfo) {
		this.productInfo = productInfo;
	}

	public ProductInfo getProductInfo() {
		return productInfo;
	}

	public void setProductType(ProductType productType) {
		this.productType = productType;
	}

	public ProductType getProductType() {
		return productType;
	}

	public String getDiscountPercentageString() {
		if( discountPercentage != null ) {
			return discountPercentage.toString() + "%";	
		} else {
			return "";
		}
	}

	public BigDecimal getDiscountPercentage() {
		return discountPercentage;
	}

	public void setDiscountPercentage(BigDecimal discountPercentage) {
		this.discountPercentage = discountPercentage;
	}
	
}
