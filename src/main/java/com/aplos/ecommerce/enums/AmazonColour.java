package com.aplos.ecommerce.enums;

import com.aplos.common.LabeledEnumInter;

public enum AmazonColour implements LabeledEnumInter {
	BEIGE ( "Beige" ),
	BLACK ("Black"),
	BLUE ("Blue"),
	BRONZE ("Bronze"),
	BROWN ("Brown"),
	GOLD ("Gold"),
	GREEN ("Green"),
	GREY ("Grey"),
	METALLIC ("Metallic"),
	MULTI_COLOURED ("Multicoloured"),
	OFF_WHITE ("Off-White"),
	ORANGE ("Orange"),
	PINK ("Pink"),
	PURPLE ("Purple"),
	RED ("Red"),
	SILVER ("Silver"),
	TRANSPARENT ("Transparent"),
	TURQUOISE ("Turquoise"),
	WHITE ("White"),
	YELLOW ("Yellow");
	
	private String label;
	
	private AmazonColour( String label ) {
		this.label = label;
	}
	
	@Override
	public String getLabel() {
		return label;
	}
}
