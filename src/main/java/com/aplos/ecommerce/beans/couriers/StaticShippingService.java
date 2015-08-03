package com.aplos.ecommerce.beans.couriers;

import java.math.BigDecimal;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.ecommerce.beans.EcommerceShoppingCart;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.enums.TransactionType;

@Entity
public class StaticShippingService extends ShippingService {
	private static final long serialVersionUID = 1137481461821203077L;
	private BigDecimal charge;

	@Override
	public BigDecimal calculateCost(EcommerceShoppingCart ecommerceShoppingCart, AdditionalShippingOption additionalShippingOption ) {
		if (charge != null) {
			return charge;
		} else {
			return new BigDecimal( 0 );
		}
	}

	@Override
	public void initService() {
		

	}
	
	public AvailableShippingService createAvailableShippingService() {
		AvailableShippingService availableShippingService = new AvailableShippingService();
		availableShippingService.setCachedCost( calculateCost( null, null ) );
		availableShippingService.setCachedServiceName(getDisplayName());
		availableShippingService.setShippingService(this);
		return availableShippingService;
	}

	@Override
	public boolean isApplicable(Transaction transaction, AdditionalShippingOption additionalShippingOption, boolean isFrontEnd ) {
		if( !super.isApplicable( transaction, additionalShippingOption, isFrontEnd) ) {
			return false;
		}
		if( TransactionType.ECOMMERCE_ORDER.equals( transaction.getTransactionStatus() ) ) {
			if( !isShowingOnWebsite() ) {
				return false;
			}
		}
		return true;
	}

	public void setCharge(BigDecimal charge) {
		this.charge = charge;
	}

	public BigDecimal getCharge() {
		if (charge != null) {
			return charge;
		}
		else {
			return new BigDecimal( 0 );
		}
	}

	@Override
	public String getDisplayName() {
		return getName();
	}
}
