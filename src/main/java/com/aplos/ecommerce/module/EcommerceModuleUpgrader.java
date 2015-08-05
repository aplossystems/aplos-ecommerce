package com.aplos.ecommerce.module;

import com.aplos.common.module.AplosModule;
import com.aplos.common.module.ModuleUpgrader;

public class EcommerceModuleUpgrader extends ModuleUpgrader {
//	ModuleConfiguration moduleConfiguration;

	public EcommerceModuleUpgrader(AplosModule aplosModule) {
		super(aplosModule, EcommerceConfiguration.class);
	}

	@Override
	protected void upgradeModule() {

		//don't use break, allow the rules to cascade
		switch (getMajorVersionNumber()) {
			case 1:

				switch (getMinorVersionNumber()) {
					case 9:
						switch (getPatchVersionNumber()) {
						
						}

				}
		}
	}
}