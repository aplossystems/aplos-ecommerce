package com.aplos.ecommerce.beans.developercmsmodules;

import com.aplos.cms.beans.developercmsmodules.ConfigurableDeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.ecommerce.beans.product.ProductBrand;

@Entity
@DynamicMetaValueKey(oldKey="CATEGORY_OVERVIEW_MODULE")
public class CategoryOverviewModule extends ConfigurableDeveloperCmsAtom {

	private static final long serialVersionUID = 2904446120034709149L;
	@ManyToOne
	private ProductBrand productBrand;

	public CategoryOverviewModule() {
		super();
	}

	@Override
	public String getName() {
		return "Brand overview";
	}

	public void setProductBrand(ProductBrand productBrand) {
		this.productBrand = productBrand;
	}

	public ProductBrand getProductBrand() {
		return productBrand;
	}

	@Override
	public DeveloperCmsAtom getCopy() {
		CategoryOverviewModule categoryOverviewModule = new CategoryOverviewModule();
		categoryOverviewModule.setProductBrand(productBrand);
		return categoryOverviewModule;
	}
}
