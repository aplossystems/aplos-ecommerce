package com.aplos.ecommerce.beans.developercmsmodules;

import com.aplos.cms.beans.developercmsmodules.ConfigurableDeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.interfaces.PlaceholderContent;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Entity;

@Entity
@DynamicMetaValueKey(oldKey="FORGOTTEN_PASSWORD_MODULE")
public class ForgottenPasswordModule extends ConfigurableDeveloperCmsAtom implements PlaceholderContent {

	private static final long serialVersionUID = -6999303445192435586L;

	public ForgottenPasswordModule() {
		super();
	}

	@Override
	public String getName() {
		return "Forgotten Password : Request";
	}

	@Override
	public DeveloperCmsAtom getCopy() {
		return new ForgottenPasswordModule();
	}

}
