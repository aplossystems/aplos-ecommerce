package com.aplos.ecommerce.developermodulebacking.frontend;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.AplosUrl;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.developercmsmodules.CategoryOverviewModule;

@ManagedBean
@ViewScoped
public class CategoryOverviewFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = -8386993848789271793L;

	public CategoryOverviewFeDmb() {}

	public void viewCollection() {
		//String mapping = ((CmsPageRevision)JSFUtil.getSession().getAttribute("cmsPageRevision")).getCmsPage().getMapping();
		//JSFUtil.redirect( mapping + "-products.aplos", true);
		String productBrand = ((CategoryOverviewModule) getDeveloperCmsAtom()).getProductBrand().getName();
		String url = "designers/" + CommonUtil.makeSafeUrlMapping(productBrand) + "/products.aplos";
		JSFUtil.redirect(new AplosUrl(url), true);
	}
}

