package com.aplos.ecommerce.developermodulebacking.backend;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.ecommerce.beans.developercmsmodules.NewsModule;

@ManagedBean
@ViewScoped
public class NewsBeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = -6938451460788026499L;
	private NewsModule newsModule;

	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		super.responsePageLoad(developerCmsAtom);
		setNewsAtom((NewsModule) developerCmsAtom);
		return true;
	}

	public void setNewsAtom(NewsModule newsModule) {
		this.newsModule = newsModule;
	}

	public NewsModule getNewsAtom() {
		return newsModule;
	}

}
