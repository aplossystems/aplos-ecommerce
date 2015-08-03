package com.aplos.ecommerce.beans;

import java.math.BigDecimal;
import java.util.List;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.utils.FormatUtil;
import com.aplos.ecommerce.beans.product.ProductBrand;
import com.aplos.ecommerce.beans.product.ProductType;
import com.aplos.ecommerce.module.EcommerceConfiguration;

@Entity
public class SitewideDiscount extends AplosBean {

	private static final long serialVersionUID = 344235800428180095L;

	private BigDecimal discountPercentage = new BigDecimal(0);
	@ManyToOne
	private ProductBrand productBrand;
	@ManyToOne
	private ProductType productType;

	public SitewideDiscount() {	}
	@Override
	public String getDisplayName() {
		String displayName = FormatUtil.formatTwoDigit(discountPercentage.doubleValue()) + "% Off ";
		if (productBrand != null) {
			displayName += productBrand.getDisplayName();
		} else if (productType != null) {
			displayName += productType.getDisplayName();
		} else {
			displayName += "Everything";
		}
		return displayName;
	}

	public void setDiscountPercentage(BigDecimal discountPercentage) {
		this.discountPercentage = discountPercentage;
	}

	public BigDecimal getDiscountPercentage() {
		return discountPercentage;
	}

	public void setProductBrand(ProductBrand productBrand) {
		this.productBrand = productBrand;
	}

	public ProductBrand getProductBrand() {
		return productBrand;
	}

	public void setProductType(ProductType productType) {
		this.productType = productType;
	}

	public ProductType getProductType() {
		return productType;
	}

	public static void updateSitewideDiscounts() {
		BeanDao discountDao = new BeanDao(SitewideDiscount.class);
		BeanDao realizedDao = new BeanDao(RealizedProduct.class);
		discountDao.setSelectCriteria("bean.discountPercentage, bean.productBrand.id, bean.productType.id");
		List<SitewideDiscount> discounts = discountDao.getAll();
		List<RealizedProduct> products = realizedDao.getAll();
		ProductType giftVoucherProductType = EcommerceConfiguration.getEcommerceConfiguration().getGiftVoucherProductType();
		for (RealizedProduct product : products) {
			//TODO: inefficient looping.
			//not sure how to fix it with a join to only select where no pi.p.producttype is the gift voucher type
			boolean canTakeDiscount = true;
			for (ProductType type : product.getProductInfo().getProduct().getProductTypes()) {
				if (type.equals(giftVoucherProductType)) {
					canTakeDiscount=false;
				}
			}
			if (canTakeDiscount) {
				product.takeSitewideDiscount(discounts);
				product.saveDetails();
			}
		}
	}

}
