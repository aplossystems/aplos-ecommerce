package com.aplos.ecommerce.developermodulebacking.backend;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.beans.AplosAbstractBean;
import com.aplos.ecommerce.beans.product.ProductBrand;

@ManagedBean
@ViewScoped
public class CategoryOverviewBeDmb extends DeveloperModuleBacking {

	private static final long serialVersionUID = -6293078288556927968L;

	public CategoryOverviewBeDmb() {
	}

	public SelectItem[] getBrandSelectItems() {
	    return AplosAbstractBean.getSelectItemBeansStatic( ProductBrand.class,"Select the brand to display");
	}
}
