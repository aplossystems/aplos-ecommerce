package com.aplos.ecommerce.enums;

import com.aplos.common.LabeledEnumInter;

public enum AmazonProductType implements LabeledEnumInter {
	ACCESSORY ("Accessory"),
	BLAZER ("Blazer"),
	BRA ("Bra"),
	DRESS ("Dress"),
	HAT ("Hat"),
	OUTERWEAR ("Outerwear"),
	PANTS ("Pants"),
	SHIRT ( "Shirt" ),
	SHORTS ("Shorts"),
	SKIRT ("Skirt"),
	SLEEPWEAR ("Sleepwear"),
	SOCKSHOSIERY ("SocksHosiery"),
	SUIT ("Suit"),
	SWEATER ("Sweater"),
	SWIMWEAR ("Swimwear"),
	UNDERWEAR ("Underwear"),
	SHOES ("Shoes"),
	BAG ("Bag"),
	JEWELRY ("Jewelry"),
	PERSONAL_BODY_CARE ("PersonalBodyCare"),
	HOME_ACCESSORY ("HomeAccessory"),
	NON_APPAREL_MISC ("NonApparelMisc"),
	KIMONO ("Kimono"),
	OBI ("Obi"),
	CHANCHANKO ("Chanchanko"),
	JINBEI ("Jinbei"),
	YUKATA ("Yukata");
	
	private String label;
	
	private AmazonProductType( String label ) {
		this.label = label;
	}
	
	@Override
	public String getLabel() {
		return label;
	}
}
