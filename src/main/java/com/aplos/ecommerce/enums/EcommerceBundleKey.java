package com.aplos.ecommerce.enums;

import com.aplos.common.interfaces.BundleKey;

// PLEASE KEEP KEYS IN ALPHABETICAL ORDER
public enum EcommerceBundleKey implements BundleKey {
	BILLING_ADDRESS ("billing address"),
	BILLING_ADDRESS_IS_THE_SAME ("billing address is the same as the above"),
	CONFIRMATION ("confirmation"),
	CUSTOMER_ID ( "customer id" ),
	CUSTOMER_SHIPPING_PHONE ( "phone" ),
	CUSTOMER_SHIPPING_PHONE_2 ( "phone 2" ),
	DELIVERY_ADDRESS ("delivery address"),
	EMAIL_TO_FRIENDS ( "email to friends" ),
	EMAIL_SHARE_FRIENDS ( "email or share with friends" ),
	GO_TO_BILLING ("go to billing address"),
	GO_TO_SHIPPING ("go to shipping address"),
	I_NEED_TO_ENTER_A_BILLING_ADDRESS ("I need to enter a separate billing address"),
	SHIPPING_ADDRESS ("shipping address"),
	SHIPPING_METHOD ( "shipping method" ),
	SHIPPING_COST ( "shipping cost" ),
	_DM_PAYMENT_SUCCESS_THANK_YOU_MESSAGE ("Thank you for your purchase.<br/><br/>We have sent you an email about the delivery of your products.");

	private String defaultValue;

	private EcommerceBundleKey( String defaultValue ) {
		this.defaultValue = defaultValue;
	}

	@Override
	public String getName() {
		return this.name();
	}

	@Override
	public String getDefaultValue() {
		return defaultValue;
	}
}
