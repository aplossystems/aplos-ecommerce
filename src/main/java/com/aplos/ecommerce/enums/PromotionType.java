package com.aplos.ecommerce.enums;

import com.aplos.common.LabeledEnumInter;

public enum PromotionType implements LabeledEnumInter {

	FULL ( "Full" ),
	BASIC ( "Basic" ),
	NONE ( "None" );

	String displayName;

	private PromotionType (String nameToDisplay) {
		this.displayName = nameToDisplay;
	}

	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String getLabel() {
		return displayName;
	}
}
