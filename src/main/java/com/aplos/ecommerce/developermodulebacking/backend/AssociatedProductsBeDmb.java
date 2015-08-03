package com.aplos.ecommerce.developermodulebacking.backend;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.ecommerce.beans.developercmsmodules.AssociatedProductsModule;

@ManagedBean
@ViewScoped
public class AssociatedProductsBeDmb extends DeveloperModuleBacking {

	/**
	 *
	 */
	private static final long serialVersionUID = 6982301331309834447L;
	private AssociatedProductsModule associatedProductsModule;

	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		super.responsePageLoad(developerCmsAtom);
		setAssociatedProductsModule((AssociatedProductsModule) developerCmsAtom);
		return true;
	}

	public void setAssociatedProductsModule(AssociatedProductsModule associatedProductsModule) {
		this.associatedProductsModule = associatedProductsModule;
	}

	public AssociatedProductsModule getAssociatedProductsModule() {
		return associatedProductsModule;
	}

}
