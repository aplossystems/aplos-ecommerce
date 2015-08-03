package com.aplos.ecommerce.developermodulebacking.frontend;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;

import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.appconstants.EcommerceAppConstants;
import com.aplos.ecommerce.module.EcommerceConfiguration;

@ManagedBean
@CustomScoped(value="#{ tabSession }")
public class SearchProductListFeDmb extends ProductListFeDmb {
	private static final long serialVersionUID = 5429912396632041436L;
	private String searchText = "Enter Keyword / Product";

	public SearchProductListFeDmb() { super(); }

	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		return super.responsePageLoad(developerCmsAtom);
	}

	public void doSearchAction() {
		if (!searchText.equals("Enter Keyword / Product")) {
			JSFUtil.addToTabSession( EcommerceAppConstants.SEARCH_TERM, searchText );
			EcommerceConfiguration.getEcommerceCprsStatic().redirectToSearchResultsCpr();
		}
	}

	public void setSearchTerm(String searchTerm) {
		JSFUtil.addToTabSession(EcommerceAppConstants.SEARCH_TERM, searchTerm);
//		SearchProductListModule searchModule = (SearchProductListModule) getProductListModule();
//		if (searchModule != null) {
//			searchModule.setSearchTerm(searchTerm);
//		}
	}

	@Override
	public boolean isShowingDescriptionPanel() {
		return false;
	}

	@Override
	public String getSearchTerm() {
		return (String)JSFUtil.getFromTabSession(EcommerceAppConstants.SEARCH_TERM);
	}

	@Override
	public String getPageTitle() {
		return "Search Results";
	}

	public void doSearch() {
		productListFilter.clearSets();
		EcommerceConfiguration.getEcommerceCprsStatic().redirectToSearchResultsCpr();
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	public String getSearchText() {
		return searchText;
	}

}














