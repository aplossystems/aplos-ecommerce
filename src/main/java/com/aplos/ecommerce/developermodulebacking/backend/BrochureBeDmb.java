package com.aplos.ecommerce.developermodulebacking.backend;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.interfaces.PositionedBean;
import com.aplos.common.utils.PositionedBeanHelper;
import com.aplos.ecommerce.beans.Brochure;
import com.aplos.ecommerce.beans.developercmsmodules.BrochureCmsAtom;

@ManagedBean
@ViewScoped
public class BrochureBeDmb extends DeveloperModuleBacking {

	private static final long serialVersionUID = 1445408555211672616L;
	private BrochureCmsAtom brochureCmsAtom;
	private PositionedBeanHelper positionedBeanHelper;

	@SuppressWarnings("unchecked")
	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		super.responsePageLoad(developerCmsAtom);
		brochureCmsAtom = (BrochureCmsAtom) developerCmsAtom;
		if( positionedBeanHelper == null ) {
			setPositionedBeanHelper(new PositionedBeanHelper( (AplosBean) developerCmsAtom, (List<PositionedBean>) (List<? extends PositionedBean>) brochureCmsAtom.getBrochureList(), Brochure.class ));
		}
		return true;
	}

	@Override
	public void applyBtnAction() {
		super.applyBtnAction();
		getPositionedBeanHelper().saveCurrentPositionedBean();
	}

	public void saveJobOffersAndAtom() {
		getPositionedBeanHelper().saveCurrentPositionedBean();
		brochureCmsAtom.saveDetails();
	}

	public BrochureCmsAtom getBrochureCmsAtom() {
		return brochureCmsAtom;
	}

	public void setBrochureCmsAtom(BrochureCmsAtom brochureCmsAtom) {
		this.brochureCmsAtom = brochureCmsAtom;
	}

	public void setPositionedBeanHelper(PositionedBeanHelper positionedBeanHelper) {
		this.positionedBeanHelper = positionedBeanHelper;
	}

	public PositionedBeanHelper getPositionedBeanHelper() {
		return positionedBeanHelper;
	}
}
