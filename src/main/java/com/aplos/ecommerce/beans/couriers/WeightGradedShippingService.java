package com.aplos.ecommerce.beans.couriers;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.ecommerce.beans.EcommerceShoppingCart;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.enums.TransactionType;

@Entity
public class WeightGradedShippingService extends ShippingService {
	private static final long serialVersionUID = 3642655900816782239L;


	@Override
	public void initService() {
		

	}

	@Override
	public BigDecimal calculateCost(EcommerceShoppingCart ecommerceShoppingCart, AdditionalShippingOption additionalShippingOption ) {
		BigDecimal weight = ecommerceShoppingCart.getCachedWeight();

		List<ShippingStep> sortedShippingSteps = getWeightSortedShippingSteps();
		Collections.reverse(sortedShippingSteps);

		Iterator<ShippingStep> iterator = sortedShippingSteps.iterator();
		BigDecimal charge = iterator.next().getCost();

		ShippingStep tempShippinStep;
		while ( iterator.hasNext() ) {
			tempShippinStep = iterator.next();
			if ( weight.compareTo( tempShippinStep.getMaxWeight() ) < 0 ) {
				charge = tempShippinStep.getCost();
			}
		}
		return charge;
	}

	@Override
	public boolean isApplicable(Transaction transaction, AdditionalShippingOption additionalShippingOption, boolean isFrontEnd ) {
		if( TransactionType.ECOMMERCE_ORDER.equals( transaction.getTransactionStatus() ) ) {
			if( !isShowingOnWebsite() ) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String getDisplayName() {
		return getName();
	}
}
