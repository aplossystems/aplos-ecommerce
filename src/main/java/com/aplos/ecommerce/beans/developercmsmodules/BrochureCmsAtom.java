package com.aplos.ecommerce.beans.developercmsmodules;

import java.util.ArrayList;
import java.util.List;

import com.aplos.cms.beans.developercmsmodules.ConfigurableDeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Cascade;
import com.aplos.common.annotations.persistence.CascadeType;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.OneToMany;
import com.aplos.common.interfaces.PositionedBean;
import com.aplos.common.utils.PositionedBeanHelper;
import com.aplos.ecommerce.beans.Brochure;

@Entity
@DynamicMetaValueKey(oldKey="BROCHURE_ATOM")
public class BrochureCmsAtom extends ConfigurableDeveloperCmsAtom {
	private static final long serialVersionUID = 2944843459547551823L;

	@OneToMany
	@Cascade({CascadeType.ALL})
	private List<Brochure> brochureList = new ArrayList<Brochure>();

	@Override
	public String getName() {
		return "Brochures";
	}

	@Override
	public boolean initBackend() {
		super.initBackend();
//		HibernateUtil.initialise( this, true );
		return true;
	}

	@Override
	public boolean initFrontend(boolean isRequestPageLoad) {
		super.initFrontend(isRequestPageLoad);
//		HibernateUtil.initialise( this, true );
		return true;
	}

	@SuppressWarnings("unchecked")
	public List<Brochure> getSortedBrochureList() {
		return (List<Brochure>) PositionedBeanHelper.getSortedPositionedBeanList( (List<PositionedBean>) (List<? extends PositionedBean>) getBrochureList() );
	}

	@Override
	public DeveloperCmsAtom getCopy() {
		BrochureCmsAtom brochureAtom = new BrochureCmsAtom();
		brochureAtom.setBrochureList(new ArrayList<Brochure>( getBrochureList() ));
		return brochureAtom;
	}

	public void setBrochureList(List<Brochure> brochureList) {
		this.brochureList = brochureList;
	}

	public List<Brochure> getBrochureList() {
		return brochureList;
	}
}
