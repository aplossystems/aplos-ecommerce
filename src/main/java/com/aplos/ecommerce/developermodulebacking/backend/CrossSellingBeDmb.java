package com.aplos.ecommerce.developermodulebacking.backend;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.ecommerce.beans.developercmsmodules.CrossSellingModule;

@ManagedBean
@ViewScoped
public class CrossSellingBeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = -9093451622549313907L;
	private CrossSellingModule crossSellingModule;

	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		super.responsePageLoad(developerCmsAtom);
		setCrossSellingModule((CrossSellingModule) developerCmsAtom);
		return true;
	}

	public void setCrossSellingModule(CrossSellingModule crossSellingModule) {
		this.crossSellingModule = crossSellingModule;
	}

	public CrossSellingModule getCrossSellingModule() {
		return crossSellingModule;
	}

	public SelectItem[] getReferenceObjectSelectItems() {
		SelectItem[] items = new SelectItem[2];
		items[0] = new SelectItem(true, "I'm using this with the shopping cart");
		items[1] = new SelectItem(false, "I'm using this with the individual product pages");
		return items;
	}
}
