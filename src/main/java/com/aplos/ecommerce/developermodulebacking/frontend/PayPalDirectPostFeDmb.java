package com.aplos.ecommerce.developermodulebacking.frontend;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;

@ManagedBean
@ViewScoped
public class PayPalDirectPostFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = -1089827196280111709L;

	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		return super.responsePageLoad(developerCmsAtom);
	}
}

