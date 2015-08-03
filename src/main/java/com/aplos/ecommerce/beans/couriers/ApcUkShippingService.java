package com.aplos.ecommerce.beans.couriers;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.beans.Country;
import com.aplos.ecommerce.beans.EcommerceShoppingCart;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.enums.TransactionType;

@Entity
@PluralDisplayName(name="APC UK shipping services")
public class ApcUkShippingService extends ShippingService {
	private static final long serialVersionUID = 3642655900816782239L;

	@ManyToOne
	private Country country;
	private BigDecimal tariff = new BigDecimal( 0 );
	private BigDecimal weightBand = new BigDecimal( 0 );
	private BigDecimal extraKgCost = new BigDecimal( 0 );


	@Override
	public void initService() {
		getShippingSteps().add( new ShippingStep( new BigDecimal( "100" ), new BigDecimal( ".25" ) ) );
	}

	@Override
	public BigDecimal calculateCost(EcommerceShoppingCart ecommerceShoppingCart,
			AdditionalShippingOption additionalShippingOption) {
		BigDecimal weight = ecommerceShoppingCart.getCachedWeight();
		BigDecimal cost = tariff;
		if( weight.compareTo( weightBand ) > 0 ) {
			ShippingStep tempShippingStep = getShippingSteps().get( 0 );
			BigDecimal kilogramsUnderMaxWeight = tempShippingStep.getMaxWeight().min( weight ).subtract( weightBand ).divide( new BigDecimal( 1000 ) ).setScale( 0, RoundingMode.UP );
			cost = cost.add( kilogramsUnderMaxWeight.multiply( tempShippingStep.getCost() ) );
			BigDecimal kilogramsOverMaxWeight = weight.subtract( tempShippingStep.getMaxWeight() ).max( new BigDecimal( 0 ) ).divide( new BigDecimal( 1000 ) ).setScale( 0, RoundingMode.UP );
			cost = cost.add( kilogramsOverMaxWeight.multiply( extraKgCost ) );
		}
		if( additionalShippingOption != null ) {
			cost = cost.add( additionalShippingOption.getCost() );
		}
		return cost;
	}

	@Override
	public boolean isApplicable( Transaction transaction, AdditionalShippingOption additionalShippingOption, boolean isFrontEnd ) {
		if( TransactionType.ECOMMERCE_ORDER.equals( transaction.getTransactionStatus() ) ) {
			if( !isShowingOnWebsite() ) {
				return false;
			}
		}
		
		if( transaction.getShippingAddress().getCountry().equals( getCountry() ) ) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String getDisplayName() {
		return getName();
	}

	public void setTariff(BigDecimal tariff) {
		this.tariff = tariff;
	}

	public BigDecimal getTariff() {
		return tariff;
	}

	public void setWeightBand(BigDecimal weightBand) {
		this.weightBand = weightBand;
	}

	public BigDecimal getWeightBand() {
		return weightBand;
	}

	public void setExtraKgCost(BigDecimal extraKgCost) {
		this.extraKgCost = extraKgCost;
	}

	public BigDecimal getExtraKgCost() {
		return extraKgCost;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public Country getCountry() {
		return country;
	}

}
