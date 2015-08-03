package com.aplos.ecommerce.beans.couriers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.aplos.common.annotations.persistence.Cascade;
import com.aplos.common.annotations.persistence.CascadeType;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.IndexColumn;
import com.aplos.common.annotations.persistence.Inheritance;
import com.aplos.common.annotations.persistence.InheritanceType;
import com.aplos.common.annotations.persistence.ManyToMany;
import com.aplos.common.annotations.persistence.OneToMany;
import com.aplos.common.beans.AplosBean;
import com.aplos.ecommerce.beans.EcommerceShoppingCart;
import com.aplos.ecommerce.beans.Transaction;
import com.ebay.soap.eBLBaseComponents.ShippingServiceCodeType;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class ShippingService extends AplosBean {
	private static final long serialVersionUID = 1065414330910770758L;

	private boolean isShowingOnWebsite = true;
	private String name;
	private String deliveryTime;
	private ShippingServiceCodeType ebayShippingServiceType = ShippingServiceCodeType.UK_PARCELFORCE_48;

	@ManyToMany
	@IndexColumn(name = "position")
	@Cascade({CascadeType.ALL})
	private List<ShippingStep> shippingSteps = new ArrayList<ShippingStep>();

	@OneToMany
	@Cascade({CascadeType.ALL})
	private List<AdditionalShippingOption> additionalShippingOptions = new ArrayList<AdditionalShippingOption>();

	
	
	public abstract void initService();

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public boolean isApplicabilityAssessedIndividually() {
		return false;
	}

	@Override
	public abstract String getDisplayName();

	public abstract BigDecimal calculateCost( EcommerceShoppingCart shoppingCart, AdditionalShippingOption additionalShippingOption );

	public boolean isParentApplicable( Transaction transaction, boolean isFrontEnd ) {
		return isApplicable(transaction, null, isFrontEnd );
	}

	public boolean isApplicable( Transaction transaction, AdditionalShippingOption additionalShippingOption, boolean isFrontEnd ) {
		if( !isShowingOnWebsite() && isFrontEnd ) {
			return false;
		}
		return true;
	}

	public void setShowingOnWebsite(boolean isShowingOnWebsite) {
		this.isShowingOnWebsite = isShowingOnWebsite;
	}
	public boolean isShowingOnWebsite() {
		return isShowingOnWebsite;
	}

	public void setDeliveryTime(String deliveryTime) {
		this.deliveryTime = deliveryTime;
	}

	public String getDeliveryTime() {
		return deliveryTime;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<ShippingStep> getWeightSortedShippingSteps() {
		ArrayList<ShippingStep> sortedList = new ArrayList( shippingSteps );
		Collections.sort( sortedList, new Comparator<ShippingStep>() {
			@Override
			public int compare(ShippingStep o1, ShippingStep o2) {
				if ( o1.getMaxWeight().compareTo( o2.getMaxWeight() ) < 0 ) {
					return -1;
				}
				else if ( o1.getMaxWeight().compareTo( o2.getMaxWeight() ) > 0 ) {
					return 1;
				}
				else {
					return 0;
				}
			}
		});

		return sortedList;
	}

	public void setAdditionalShippingOptions(
			List<AdditionalShippingOption> additionalShippingOptions) {
		this.additionalShippingOptions = additionalShippingOptions;
	}

	public List<AdditionalShippingOption> getAdditionalShippingOptions() {
		return additionalShippingOptions;
	}

	public void setShippingSteps(List<ShippingStep> shippingSteps) {
		this.shippingSteps = shippingSteps;
	}

	public List<ShippingStep> getShippingSteps() {
		return shippingSteps;
	}

	public void setEbayShippingServiceType(ShippingServiceCodeType ebayShippingServiceType) {
		this.ebayShippingServiceType = ebayShippingServiceType;
	}

	public ShippingServiceCodeType getEbayShippingServiceType() {
		return ebayShippingServiceType;
	}

	public boolean isUsingVolumetricWeight() {
		return false;
	}

}
