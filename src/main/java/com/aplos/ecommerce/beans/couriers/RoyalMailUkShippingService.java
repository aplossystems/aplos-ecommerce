package com.aplos.ecommerce.beans.couriers;

import java.math.BigDecimal;

import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.ecommerce.beans.EcommerceShoppingCart;
import com.aplos.ecommerce.beans.Transaction;

@Entity
@PluralDisplayName(name="Royal Mail UK shipping services")
public class RoyalMailUkShippingService extends ShippingService {
	private static final long serialVersionUID = 3642655900816782239L;

	private BigDecimal maxWeight = new BigDecimal( 0 );

	@Override
	public void initService() {
		getShippingSteps().add( new ShippingStep( new BigDecimal( "100" ), new BigDecimal( "0" ) ) );
		getShippingSteps().add( new ShippingStep( new BigDecimal( "250" ), new BigDecimal( "0" ) ) );
		getShippingSteps().add( new ShippingStep( new BigDecimal( "500" ), new BigDecimal( "0" ) ) );
		getShippingSteps().add( new ShippingStep( new BigDecimal( "750" ), new BigDecimal( "0" ) ) );
		getShippingSteps().add( new ShippingStep( new BigDecimal( "1000" ), new BigDecimal( "0" ) ) );
		getShippingSteps().add( new ShippingStep( new BigDecimal( "1250" ), new BigDecimal( "0" ) ) );
		getShippingSteps().add( new ShippingStep( new BigDecimal( "1500" ), new BigDecimal( "0" ) ) );
		getShippingSteps().add( new ShippingStep( new BigDecimal( "1750" ), new BigDecimal( "0" ) ) );
		getShippingSteps().add( new ShippingStep( new BigDecimal( "2000" ), new BigDecimal( "0" ) ) );
		getShippingSteps().add( new ShippingStep( new BigDecimal( "10000" ), new BigDecimal( "0" ) ) );
	}

	@Override
	public BigDecimal calculateCost(EcommerceShoppingCart ecommerceShoppingCart,
			AdditionalShippingOption additionalShippingOption) {
		BigDecimal weight = ecommerceShoppingCart.getCachedWeight();
		BigDecimal cost = new BigDecimal( 0 );
		for( ShippingStep tempShippingSetStep : getWeightSortedShippingSteps() ) {
			if( weight.compareTo( tempShippingSetStep.getMaxWeight() ) <= 0 ) {
				cost = tempShippingSetStep.getCost();
				break;
			}
		}
		if( additionalShippingOption != null ) {
			cost = cost.add( additionalShippingOption.getCost() );
		}
		return cost;
	}

	@Override
	public boolean isApplicable( Transaction transaction, AdditionalShippingOption additionalShippingOption, boolean isFrontEnd ) {
		if( transaction.getShippingAddress().getCountry() != null && transaction.getShippingAddress().getCountry().getIso2().equals( "GB" ) && transaction.getEcommerceShoppingCart().getCachedWeight().compareTo( maxWeight ) <= 0 ) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String getDisplayName() {
		return getName();
	}

	public void setMaxWeight(BigDecimal maxWeight) {
		this.maxWeight = maxWeight;
	}

	public BigDecimal getMaxWeight() {
		return maxWeight;
	}

}
