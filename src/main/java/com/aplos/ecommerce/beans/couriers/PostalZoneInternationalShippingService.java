package com.aplos.ecommerce.beans.couriers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.beans.PostalZone;
import com.aplos.ecommerce.beans.EcommerceShoppingCart;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.enums.TransactionType;

@Entity
@PluralDisplayName(name="Postal Zone International shipping services")
public class PostalZoneInternationalShippingService extends ShippingService {
	private static final long serialVersionUID = 3642655900816782239L;

	private BigDecimal maxWeight = new BigDecimal( 10000.00 );
	private BigDecimal extra20gCost = new BigDecimal( 0 );
	private BigDecimal tariff = new BigDecimal( 0 );
	
	@ManyToOne
	private PostalZone postalZone;

	@Override
	public void initService() {
		getShippingSteps().add( new ShippingStep( new BigDecimal( "100" ), new BigDecimal( "0" ) ) );
		getShippingSteps().add( new ShippingStep( new BigDecimal( "240" ), new BigDecimal( "0" ) ) );
		getShippingSteps().add( new ShippingStep( new BigDecimal( "500" ), new BigDecimal( "0" ) ) );
		getShippingSteps().add( new ShippingStep( new BigDecimal( "740" ), new BigDecimal( "0" ) ) );
		getShippingSteps().add( new ShippingStep( new BigDecimal( "1000" ), new BigDecimal( "0" ) ) );
	}

	@Override
	public BigDecimal calculateCost(EcommerceShoppingCart ecommerceShoppingCart,
			AdditionalShippingOption additionalShippingOption) {
		BigDecimal weight = ecommerceShoppingCart.getCachedWeight();
		BigDecimal cost = null;
		List<ShippingStep> sortedShippingSteps = getWeightSortedShippingSteps();
		for( ShippingStep tempShippingStep : sortedShippingSteps ) {
			if( weight.compareTo( tempShippingStep.getMaxWeight() ) <= 0 ) {
				cost = tempShippingStep.getCost();
				break;
			}
		}
		if( cost == null ) {
			ShippingStep maxWeightStep = sortedShippingSteps.get( 0 );
			cost = maxWeightStep.getCost();
			BigDecimal multiplier = weight.subtract( maxWeightStep.getMaxWeight() ).divide( new BigDecimal( 20 ) ).setScale( 0, RoundingMode.UP );
			cost = cost.add( extra20gCost.multiply( multiplier ) );
		}
		if( additionalShippingOption != null ) {
			cost = cost.add( additionalShippingOption.getCost() );
		}
		cost = cost.add( tariff );
		return cost;
	}

	@Override //applicable for what?
	public boolean isParentApplicable( Transaction transaction, boolean isFrontEnd ) {
		
		if( TransactionType.ECOMMERCE_ORDER.equals( transaction.getTransactionStatus() ) ) {
			if( !isShowingOnWebsite() ) {
				return false;
			}
		}
		
		if ( postalZone != null ) {
			if ( transaction.getEcommerceShoppingCart().getCachedWeight().compareTo( maxWeight ) <= 0 ) {
				if ( postalZone.getCountries().contains( transaction.getShippingAddress().getCountry() ) ) {
					return true;
				} else if ( getAdditionalShippingOptions().size() > 0 ) {
					if ( ((PostalZoneAdditionalShippingOption) getAdditionalShippingOptions().get( 0 )).getPostalZone().getCountries().contains(transaction.getShippingAddress().getCountry()) ) {
						return true;
					}
				}
			}
		}

		return false;
	}

	@Override
	public boolean isApplicable( Transaction transaction, AdditionalShippingOption additionalShippingOption, boolean isFrontEnd ) {
		if ( postalZone != null ) {
			if ( transaction.getEcommerceShoppingCart().getCachedWeight().compareTo( maxWeight ) <= 0 ) {
				if ( additionalShippingOption == null ) {
					if ( postalZone.getCountries().contains( transaction.getShippingAddress().getCountry() ) ) {
						return true;
					}
				} else {
					if ( ((PostalZoneAdditionalShippingOption) additionalShippingOption).getPostalZone().getCountries().contains(transaction.getShippingAddress().getCountry()) ) {
						return true;
					}
				}
			}
		}

		return false;
	}

	@Override
	public boolean isApplicabilityAssessedIndividually() {
		return true;
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

	public void setExtra20gCost(BigDecimal extra20gCost) {
		this.extra20gCost = extra20gCost;
	}

	public BigDecimal getExtra20gCost() {
		return extra20gCost;
	}

	public void setTariff(BigDecimal tariff) {
		this.tariff = tariff;
	}

	public BigDecimal getTariff() {
		return tariff;
	}

	public void setPostalZone(PostalZone postalZone) {
		this.postalZone = postalZone;
	}

	public PostalZone getPostalZone() {
		return postalZone;
	}
	
	@Override
	public boolean isUsingVolumetricWeight() {
		return true;
	}

}
