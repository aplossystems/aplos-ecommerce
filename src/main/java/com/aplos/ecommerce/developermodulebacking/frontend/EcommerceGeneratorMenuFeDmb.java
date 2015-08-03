package com.aplos.ecommerce.developermodulebacking.frontend;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;

import com.aplos.cms.annotations.DmbOverride;
import com.aplos.cms.beans.developercmsmodules.CmsAtom;
import com.aplos.cms.beans.developercmsmodules.GeneratorMenuCmsAtom;
import com.aplos.cms.beans.pages.CmsPageGenerator;
import com.aplos.cms.beans.pages.CmsPageRevision;
import com.aplos.cms.developermodulebacking.frontend.GeneratorMenuFeDmb;
import com.aplos.cms.enums.GenerationType;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.utils.EcommerceUtil;

@ManagedBean
@CustomScoped(value="#{ tabSession }")
@DmbOverride(dmbClass=GeneratorMenuFeDmb.class)
public class EcommerceGeneratorMenuFeDmb extends GeneratorMenuFeDmb {
	private static final long serialVersionUID = 525985166845198629L;

	@Override
	public GeneratorMenuCmsAtom determineGeneratorMenuCmsAtom() {
		//check if we have the atom included in this current page
		GeneratorMenuCmsAtom generatorCmsAtom = CmsPageGenerator.checkForAtomInPage((CmsPageRevision)JSFUtil.getBeanFromScope(CmsPageRevision.class));
		if( generatorCmsAtom != null && !GenerationType.NONE.equals( generatorCmsAtom.getGenerationType() ) ) {
			return generatorCmsAtom;
		}
		
		String categoryMapping = EcommerceUtil.getEcommerceUtil().getCategoryName();
		if (!categoryMapping.equals("")) {
			BeanDao parentDao = new BeanDao(CmsPageRevision.class);
			parentDao.setWhereCriteria("bean.current=1");
			parentDao.addWhereCriteria("bean.cmsPage.mapping='" + categoryMapping + "'");
			parentDao.setMaxResults(1);
			CmsPageRevision loadedRevision = parentDao.getFirstBeanResult();
			if (loadedRevision != null) {
				//check for the atom on the page instance
				for (CmsAtom tempAtom : loadedRevision.getCmsAtomList()) {
					if (tempAtom instanceof GeneratorMenuCmsAtom) {
						return (GeneratorMenuCmsAtom) tempAtom;
					}
				}
				//check for the atom on the template
				for (CmsAtom tempAtom : loadedRevision.getCmsAtomPassedThroughMap().values()) {
					if (tempAtom instanceof GeneratorMenuCmsAtom) {
						return (GeneratorMenuCmsAtom) tempAtom;
					}
				}
			}
		}

		//if we still have nothing, try to return the one in the session (if there is one, otherwise null)
		return (GeneratorMenuCmsAtom)JSFUtil.getBeanFromScope(GeneratorMenuCmsAtom.class);

	}
}
