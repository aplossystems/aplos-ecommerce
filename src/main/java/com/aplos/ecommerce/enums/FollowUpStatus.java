package com.aplos.ecommerce.enums;

import com.aplos.common.LabeledEnumInter;

public enum FollowUpStatus implements LabeledEnumInter {

	NO_ANSWER ( "No answer" ),
	CALL_BACK ( "Call back" ),
	COMPLETED ( "Completed" );

	String displayName;

	private FollowUpStatus (String nameToDisplay) {
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
