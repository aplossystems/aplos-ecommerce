package com.aplos.ecommerce.beans.developercmsmodules;

import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Entity;

@Entity
@DynamicMetaValueKey(oldKey="SALE_LIST_MODULE")
public class SaleProductListModule extends ProductListModule {

	/** TODO : This module and associated pages are UNFINISHED **/

	private static final long serialVersionUID = -1138320820849713111L;
	private Double percentageDiscount = new Double(25.0);

	/* This constructor is required by getDeveloperModuleCPHUrlList()
	 * in the Page class
	 */
	public SaleProductListModule() {
		super();
	}
	
	@Override
	public String getFrontEndBodyName() {
		return "productListBody";
	}

	@Override
	public String getName() {
		return "Sale room";
	}

	public void setPercentageDiscount(Double percentageDiscount) {
		this.percentageDiscount = percentageDiscount;
	}

	public Double getPercentageDiscount() {
		return percentageDiscount;
	}

}
