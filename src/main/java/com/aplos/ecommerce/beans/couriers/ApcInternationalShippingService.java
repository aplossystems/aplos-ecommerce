package com.aplos.ecommerce.beans.couriers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.beans.Country;
import com.aplos.ecommerce.beans.EcommerceShoppingCart;
import com.aplos.ecommerce.beans.Transaction;

@Entity
@PluralDisplayName(name="APC International shipping services")
public class ApcInternationalShippingService extends ShippingService {
	private static final long serialVersionUID = 3642655900816782239L;

	@ManyToOne
	private Country country;
	private BigDecimal extraPoint5KgCost;


	@Override
	public void initService() {
		getShippingSteps().add( new ShippingStep( new BigDecimal( "500" ), new BigDecimal( "0" ) ) );
		getShippingSteps().add( new ShippingStep( new BigDecimal( "1000" ), new BigDecimal( "0" ) ) );
		getShippingSteps().add( new ShippingStep( new BigDecimal( "1500" ), new BigDecimal( "0" ) ) );
		getShippingSteps().add( new ShippingStep( new BigDecimal( "2000" ), new BigDecimal( "0" ) ) );
		getShippingSteps().add( new ShippingStep( new BigDecimal( "2500" ), new BigDecimal( "0" ) ) );
		getShippingSteps().add( new ShippingStep( new BigDecimal( "3000" ), new BigDecimal( "0" ) ) );
		getShippingSteps().add( new ShippingStep( new BigDecimal( "3500" ), new BigDecimal( "0" ) ) );
		getShippingSteps().add( new ShippingStep( new BigDecimal( "4000" ), new BigDecimal( "0" ) ) );
		getShippingSteps().add( new ShippingStep( new BigDecimal( "4500" ), new BigDecimal( "0" ) ) );
		getShippingSteps().add( new ShippingStep( new BigDecimal( "5000" ), new BigDecimal( "0" ) ) );
	}

	@Override
	public BigDecimal calculateCost(EcommerceShoppingCart ecommerceShoppingCart,
			AdditionalShippingOption additionalShippingOption) {
		BigDecimal volumetricWeight = ecommerceShoppingCart.getCachedBoxVolume().divide( new BigDecimal( 6 ), RoundingMode.CEILING );
		BigDecimal cost = new BigDecimal( 0 );
		boolean weightBracketFound = false;
		List<ShippingStep> weightSortedShippingSteps = getWeightSortedShippingSteps();
		for( ShippingStep tempShippingSetStep : weightSortedShippingSteps ) {
			if( volumetricWeight.compareTo( tempShippingSetStep.getMaxWeight() ) <= 0 ) {
				cost = tempShippingSetStep.getCost();
				weightBracketFound = true;
				break;
			}
		}

		if( !weightBracketFound ) {
			ShippingStep maxShippingStep = weightSortedShippingSteps.get( weightSortedShippingSteps.size() - 1 );
			cost = cost.add( maxShippingStep.getCost() );
			BigDecimal extraHalfKilograms = volumetricWeight.subtract( maxShippingStep.getMaxWeight() ).divide( new BigDecimal( 500 ), RoundingMode.CEILING ).setScale( 0, RoundingMode.UP );
			cost = cost.add( extraHalfKilograms.multiply( extraPoint5KgCost ) );
		}

		if( additionalShippingOption != null ) {
			cost = cost.add( additionalShippingOption.getCost() );
		}
		return cost;
	}

	@Override
	public boolean isApplicable( Transaction transaction, AdditionalShippingOption additionalShippingOption, boolean isFrontEnd ) {
		
		if( transaction.getShippingAddress().getCountry().equals( country ) ) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isUsingVolumetricWeight() {
		return true;
	}

	@Override
	public String getDisplayName() {
		return getName();
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public Country getCountry() {
		return country;
	}

	public void setExtraPoint5KgCost(BigDecimal extraPoint5KgCost) {
		this.extraPoint5KgCost = extraPoint5KgCost;
	}

	public BigDecimal getExtraPoint5KgCost() {
		return extraPoint5KgCost;
	}

}
