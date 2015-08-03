package com.aplos.ecommerce.developermodulebacking.backend;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.ecommerce.beans.developercmsmodules.ProductListModule;
import com.aplos.ecommerce.beans.product.ProductBrand;
import com.aplos.ecommerce.beans.product.ProductType;
import com.aplos.ecommerce.interfaces.ProductListRoot;

@ManagedBean
@ViewScoped
public class ProductListBeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = -6293078288556927968L;
	private String productListRootClassName;
	private Class<? extends ProductListRoot> productListRootClass;

	public ProductListBeDmb() {}
	
	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		super.responsePageLoad(developerCmsAtom);
		if( getProductListRootClass() == null ) {
			if( ((ProductListModule) developerCmsAtom).getProductListRoot() == null ) {
				setProductListRootClass(null);
			} else {
				setProductListRootClass( ((ProductListModule) developerCmsAtom).getProductListRoot().getClass() );
				setProductListRootClassName( getProductListRootClass().getName() );
			}
		}
		return true;
	}

	public SelectItem[] getCategorySelectItems() {
		if (getProductListRootClass() != null) {
			List<AplosBean> productListRoots = new BeanDao( (Class<? extends AplosBean>) getProductListRootClass() ).getAll();
			return AplosBean.getSelectItemBeans( productListRoots, CommonConfiguration.getCommonConfiguration().getDefaultNotSelectedText(), true );
		} else {
			return new SelectItem[0];
		}
	}

	public SelectItem[] getProductListRootTypeSelectItems() {
		SelectItem[] items = new SelectItem[ 3 ];
		items[ 0 ] = new SelectItem(null, "Select Page Display Type");
		items[ 1 ] = new SelectItem(ProductBrand.class.getName(), "Single Brand");
		items[ 2 ] = new SelectItem(ProductType.class.getName(), "Single Type of Item");
	    return items;
	}

	public ProductListRoot getCategorySelection() {
		ProductListModule productListModule = (ProductListModule) getDeveloperCmsAtom();
		if (productListModule == null) {
			return null;
		} else {
			return productListModule.getProductListRoot();
		}
	}
	
	public void productListRootClassNameUpdated() {
		try {
			if( getProductListRootClassName() == null ) {
				setProductListRootClass( null );
			} else {
				setProductListRootClass( (Class<? extends ProductListRoot>) Class.forName(getProductListRootClassName()) );
			}
		} catch (ClassNotFoundException e) {
			ApplicationUtil.getAplosContextListener().handleError( e );
		}
	}

	public String getProductListRootClassName() {
		return productListRootClassName;
	}

	public void setProductListRootClassName(String productListRootClassName) {
		this.productListRootClassName = productListRootClassName;
	}

	public Class<? extends ProductListRoot> getProductListRootClass() {
		return productListRootClass;
	}

	public void setProductListRootClass(Class<? extends ProductListRoot> productListRootClass) {
		this.productListRootClass = productListRootClass;
	}

}
