package com.aplos.ecommerce.beans.couriers;

import java.math.BigDecimal;

import com.aplos.common.annotations.persistence.DiscriminatorValue;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.beans.PostalZone;

@Entity
@DiscriminatorValue("PostalZoneAso")
public class PostalZoneAdditionalShippingOption extends AdditionalShippingOption {
	private static final long serialVersionUID = -7136954535080631293L;

	@ManyToOne
	private PostalZone postalZone;

	public PostalZoneAdditionalShippingOption() {}

	public PostalZoneAdditionalShippingOption( String name, BigDecimal cost, String deliveryTime ) {
		super( name, cost, deliveryTime );
	}

	public void setPostalZone(PostalZone postalZone) {
		this.postalZone = postalZone;
	}

	public PostalZone getPostalZone() {
		return postalZone;
	}
}
