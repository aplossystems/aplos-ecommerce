package com.aplos.ecommerce.enums;

import com.aplos.common.LabeledEnumInter;

public enum AddressStatus implements LabeledEnumInter {

	WAITING( "Waiting" ),
	EMAIL_OK ( "Email OK" ),
	POSTAL_OK ( "Postal OK" );

	String displayName;

	private AddressStatus (String nameToDisplay) {
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
