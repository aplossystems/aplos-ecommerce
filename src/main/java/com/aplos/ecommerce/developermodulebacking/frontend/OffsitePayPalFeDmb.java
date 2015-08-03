package com.aplos.ecommerce.developermodulebacking.frontend;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.CmsPageUrl;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.beans.pages.CmsPageRevision;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.beans.Website;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.module.EcommerceConfiguration;

@ManagedBean
@ViewScoped
public class OffsitePayPalFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = 6688954602639496891L;
	
	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		boolean continueLoad = super.responsePageLoad(developerCmsAtom);
		((CmsPageRevision) JSFUtil.getBeanFromScope( CmsPageRevision.class )).setRenderingForm( false );
		return continueLoad;
	}
	
	public String getCheckoutFailureExternalUrl() {
		CmsPageUrl cmsPageUrl = new CmsPageUrl( EcommerceConfiguration.getEcommerceCprsStatic().getCheckoutFailureCpr() );
		cmsPageUrl.setHost( Website.getCurrentWebsiteFromTabSession() );
		return cmsPageUrl.toString();
	}
	
	public String getCheckoutSuccessExternalUrl() {
		CmsPageUrl cmsPageUrl = new CmsPageUrl( EcommerceConfiguration.getEcommerceCprsStatic().getCheckoutSuccessCpr() );
		cmsPageUrl.setHost( Website.getCurrentWebsiteFromTabSession() );
		return cmsPageUrl.toString();
	}

}

