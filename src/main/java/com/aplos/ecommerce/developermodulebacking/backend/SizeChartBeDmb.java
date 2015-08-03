package com.aplos.ecommerce.developermodulebacking.backend;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.ecommerce.beans.developercmsmodules.SizeChartModule;

@ManagedBean
@ViewScoped
public class SizeChartBeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = -4438057390730819268L;
	private SizeChartModule sizeChartModule;

	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		super.responsePageLoad(developerCmsAtom);
		setSizeChartModule((SizeChartModule) developerCmsAtom);
		return true;
	}

	public void setSizeChartModule(SizeChartModule sizeChartModule) {
		this.sizeChartModule = sizeChartModule;
	}

	public SizeChartModule getSizeChartModule() {
		return sizeChartModule;
	}


}
