package com.aplos.ecommerce.beans.listbeans;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.aplos.common.utils.FormatUtil;
import com.aplos.ecommerce.beans.EcommerceShoppingCartItem;

public class ProductSalesListBean extends EcommerceShoppingCartItem {
	private static final long serialVersionUID = 5999073930071836492L;

	private long salesQuantity = 0;
	private BigDecimal salesAmount = new BigDecimal( 0 );

	public long getSalesQuantity() {
		return salesQuantity;
	}

	public void setSalesQuantity(long salesQuantity) {
		this.salesQuantity = salesQuantity;
	}

	public String getSalesAmountStr() {
		return FormatUtil.formatTwoDigit( getSalesAmount() );
	}

	public BigDecimal getSalesAmount() {
		return salesAmount;
	}

	public void setSalesAmount(BigDecimal salesAmount) {
		this.salesAmount = salesAmount;
	}
	
	public BigDecimal getSingleItemDiscountedPrice() {
		return getSingleItemBasePrice().subtract( getSingleItemDiscountAmount() );
	}
	
	public String getSingleItemDiscountedPriceStr() {
		return FormatUtil.formatTwoDigit( getSingleItemDiscountedPrice() );
	}
	
	public BigDecimal getProfitPerUnit() {
		BigDecimal productCost = getRealizedProduct().getProductCost();
		if (productCost == null) {
			productCost = getSingleItemNetPrice();
		}
		return getSingleItemNetPrice().subtract( productCost );
	}
	
	public String getProfitPerUnitStr() {
		BigDecimal profit = getProfitPerUnit();
		if (profit.doubleValue() < 0) {
			return "<span style='color:#A83838'>" + FormatUtil.formatTwoDigit( profit ) + "</span>";
		} else if (profit.doubleValue() == 0) {
			return "<span style='color:#dddddd'>0.00</span>";
		} else {
			return FormatUtil.formatTwoDigit( profit );
		}
	}
	
	public BigDecimal getUnitMargin() {
		BigDecimal onePercent = getSingleItemNetPrice().divide( new BigDecimal(100), RoundingMode.HALF_UP );
		BigDecimal profitPerUnit = getProfitPerUnit();
		if (onePercent == null || onePercent.doubleValue() == 0d) {
			if (profitPerUnit.doubleValue() == 0d) {
				return profitPerUnit; //zero
			} else {
				onePercent = profitPerUnit;
			}
		}
		return profitPerUnit.divide( onePercent, RoundingMode.HALF_UP );
	}
	
	public String getUnitMarginStr() {
		BigDecimal margin = getUnitMargin();
		if (margin.doubleValue() < 0) {
			return "<span style='color:#A83838'>" + FormatUtil.formatTwoDigit( margin ) + "%</span>";
		} else if (margin.doubleValue() == 0) {
			return "<span style='color:#dddddd'>0.00%</span>";
		} else {
			return FormatUtil.formatTwoDigit( margin ) + "%";
		}
	}
	
	public String getSingleItemNetPriceStr() {
		return FormatUtil.formatTwoDigit( getSingleItemNetPrice() );
	}
	
	public String getSingleItemBasePriceStr() {
		return FormatUtil.formatTwoDigit( getSingleItemBasePrice() );
	}
	
}
