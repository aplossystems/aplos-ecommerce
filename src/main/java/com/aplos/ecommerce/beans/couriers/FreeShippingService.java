package com.aplos.ecommerce.beans.couriers;

import java.math.BigDecimal;
import java.util.List;

import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.beans.Country;
import com.aplos.common.beans.PostalZone;
import com.aplos.common.beans.ShoppingCart;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.EcommerceShoppingCart;
import com.aplos.ecommerce.beans.Transaction;

@Entity
@PluralDisplayName(name="Postal Zone International shipping services")
public class FreeShippingService extends ShippingService {
	private static final long serialVersionUID = 3642655900816782239L;
	
	@ManyToOne
	private PostalZone postalZone;
	private boolean isForAllCountries = true;
	private BigDecimal threshold = new BigDecimal( 0 );
	private BigDecimal noticePercentage = new BigDecimal( 75 );

	@Override
	public void initService() {
	}

	@Override
	public BigDecimal calculateCost(EcommerceShoppingCart ecommerceShoppingCart,
			AdditionalShippingOption additionalShippingOption) {
		return new BigDecimal( 0 );
	}
	
	public static FreeShippingService getApplicableShippingService( CourierService courierService, boolean isFrontEnd ) {
		if( courierService != null && courierService.isActive() ) {
			List<ShippingService> shippingServices = courierService.getShippingServices();
			Transaction transaction = JSFUtil.getBeanFromScope( Transaction.class );
			EcommerceShoppingCart ecommerceShoppingCart;
			Country shippingCountry;
			if( transaction == null ) {
				ecommerceShoppingCart = JSFUtil.getBeanFromScope( ShoppingCart.class );
				shippingCountry = CommonConfiguration.getCommonConfiguration().getDefaultCompanyDetails().getAddress().getCountry();
			} else {
				ecommerceShoppingCart = transaction.getEcommerceShoppingCart();
				shippingCountry = transaction.getShippingAddress().getCountry();
			}
			for( int i = 0, n = shippingServices.size(); i < n; i++ ) {
				if( ((FreeShippingService)shippingServices.get(i)).isApplicable( ecommerceShoppingCart, shippingCountry, isFrontEnd ) ) {
					return (FreeShippingService)shippingServices.get(i);
				}
			}
		}
		return null;
	}

	@Override
	public boolean isApplicable( Transaction transaction, AdditionalShippingOption additionalShippingOption, boolean isFrontEnd ) {
		return isApplicable( transaction.getEcommerceShoppingCart(), transaction.getShippingAddress().getCountry(), isFrontEnd );
	}

	public boolean isApplicable( EcommerceShoppingCart ecommerceShoppingCart, Country shippingCountry, boolean isFrontEnd ) {
		if( ecommerceShoppingCart.getPromotion() != null && ecommerceShoppingCart.getPromotion().isFreePostage() ) {
			return true;
		}
		if( !super.isApplicable( null, null, isFrontEnd) ) {
			return false;
		}
		if ( isForAllCountries() || (postalZone != null && postalZone.getCountries().contains( shippingCountry )) ) {
			if( !isBelowThreshold( ecommerceShoppingCart.getCachedGrossTotalAmount() ) ) {
				return true;
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
	
	public boolean isPostageNoticeRequired( BigDecimal transactionTotal ) {
		BigDecimal freeShippingNoticePercentage = getNoticePercentage().divide( new BigDecimal( 100 ) );

		if ( threshold.compareTo( new BigDecimal( 0 ) ) != 0 && getThreshold().multiply(freeShippingNoticePercentage).compareTo( transactionTotal ) <= 0 ) {
			return true;
		}
		return false;
	}
	
	public boolean isBelowThreshold( BigDecimal transactionTotal ) {
		if ( getThreshold().compareTo(transactionTotal) > 0 ) {
			return true;
		}
		return false;
	}

	public void setPostalZone(PostalZone postalZone) {
		this.postalZone = postalZone;
	}

	public PostalZone getPostalZone() {
		return postalZone;
	}

	public BigDecimal getThreshold() {
		return threshold;
	}

	public void setThreshold(BigDecimal threshold) {
		this.threshold = threshold;
	}

	public BigDecimal getNoticePercentage() {
		return noticePercentage;
	}

	public void setNoticePercentage(BigDecimal noticePercentage) {
		this.noticePercentage = noticePercentage;
	}

	public boolean isForAllCountries() {
		return isForAllCountries;
	}

	public void setForAllCountries(boolean isForAllCountries) {
		this.isForAllCountries = isForAllCountries;
	}

}
