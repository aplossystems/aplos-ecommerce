package com.aplos.ecommerce.developermodulebacking.frontend;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.RealizedProduct;

@ManagedBean
@ViewScoped //sessions scoping this will break it because of how we are caching
public class NextPreviousProductFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = -2041965281234795794L;
	private Integer index=-1;
	private int maximum=0;
	private RealizedProduct currentRealizedProduct;
	private List<RealizedProduct> categoryList;
	String currentCategoryMapping;

	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		super.responsePageLoad(developerCmsAtom);
		if (getCurrentRealizedProduct() == null) {
			setCurrentRealizedProduct((RealizedProduct) JSFUtil.getBeanFromScope(RealizedProduct.class));
			if ( getCurrentRealizedProduct() != null && categoryList == null) {
				ProductListFeDmb productListFeDmb = (ProductListFeDmb) JSFUtil.getFromTabSession(JSFUtil.getBinding(ProductListFeDmb.class));
				if (productListFeDmb != null) {
					categoryList = new ArrayList<RealizedProduct>( productListFeDmb.getProductListFilter().getFilteredRealizedProductList() );
					if (categoryList != null) {
						maximum=categoryList.size();
						currentCategoryMapping = JSFUtil.getAplosContextOriginalUrl().replaceAll( ".aplos", "" );
						currentCategoryMapping = currentCategoryMapping.replaceFirst( JSFUtil.getContextPath(), "" );
						int lastDivider = currentCategoryMapping.lastIndexOf("/")+1;
						currentCategoryMapping = currentCategoryMapping.substring(0,lastDivider);
					}
				}
			}
		}
		if (categoryList != null && (index == null || index.equals(-1))) {
			for (int i=0; i < categoryList.size(); i++) {
				if (categoryList.get(i).getProductInfo().equals(getCurrentRealizedProduct().getProductInfo()) &&
					categoryList.get(i).getProductColour().equals(getCurrentRealizedProduct().getProductColour())) {
					index=i;
					break;
				}
			}
		}
		return true;
	}

	public String previous() {
		if (index < 2) {
			index = maximum-1;
		} else {
			index = index-1;
		}
		return goToProductIdx();
	}

	public String next() {
		if (index >= maximum-1) {
			index=0;
		} else {
			index=index+1;
		}
		return goToProductIdx();
	}

	private String goToProductIdx() {
		RealizedProduct unloadedRealizedProduct = categoryList.get(index);
		if (unloadedRealizedProduct != null) {
			RealizedProduct loadedRealizedProduct = new BeanDao(RealizedProduct.class).get(unloadedRealizedProduct.getId());
			loadedRealizedProduct.addToScope();
			JSFUtil.redirect(currentCategoryMapping + loadedRealizedProduct.getProductInfo().getMappingOrId() + ".aplos?rpid=" + loadedRealizedProduct.getId(), true);
		}
		return null;
	}

	public void setMaximum(int maximum) {
		this.maximum = maximum;
	}

	public String getCategoryName() {
		String name = currentCategoryMapping;
		if (name != null) {
			if (name.startsWith("/")) {
				name=name.substring(1);
			}
			if (name.endsWith("/")) {
				name=name.substring(0, name.length()-1);
			}
			if (name.indexOf("/") >= 0) {
				name = name.substring(name.lastIndexOf("/")+1);
			}
		}
		return name;
	}

	public int getMaximum() {
		return maximum;
	}

	public int getMaximumDisplay() {
		if (maximum < 1) {
			maximum=1;
		}
		return maximum;
	}

	public void setIndex(int index) {
		this.index = index;
	}


	public int getIndex() {
		return index;
	}

	public int getIndexDisplay() {
		if (index == null || index < 0) {
			index=0;
		}
		return index+1;
	}


	public void setCurrentRealizedProduct(RealizedProduct currentRealizedProduct) {
		this.currentRealizedProduct = currentRealizedProduct;
	}


	public RealizedProduct getCurrentRealizedProduct() {
		return currentRealizedProduct;
	}

}

