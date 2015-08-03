package com.aplos.ecommerce.beans.developercmsmodules;

import com.aplos.cms.beans.developercmsmodules.ConfigurableDeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.interfaces.PlaceholderContent;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Entity;

@Entity
@DynamicMetaValueKey(oldKey="FORGOTTEN_PASSWORD_RESET_MODULE")
public class ForgottenPasswordResetModule extends ConfigurableDeveloperCmsAtom implements PlaceholderContent {

	private static final long serialVersionUID = 567930458459784765L;

	public ForgottenPasswordResetModule() {
		super();
	}

	@Override
	public String getName() {
		return "Forgotten Password : Reset";
	}

	@Override
	public DeveloperCmsAtom getCopy() {
		return new ForgottenPasswordResetModule();
	}
}
