package com.aplos.ecommerce.beans.couriers;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.beans.Currency;
import com.aplos.common.utils.FormatUtil;
import com.aplos.ecommerce.beans.EcommerceShoppingCart;

public class AvailableShippingService implements Serializable {

	private static final long serialVersionUID = -3849097908877163096L;
	
	@ManyToOne
	private CourierService courierService;
	@ManyToOne
	private ShippingService shippingService;
	@ManyToOne
	private AdditionalShippingOption additionalShippingOption;
	private String cachedServiceName;
	private BigDecimal cachedCost = new BigDecimal( 0 );
	private BigDecimal additionalShippingCost = new BigDecimal( 0 );
	@Transient
	private boolean isHibernateInitialised; 

	public void updateCachedServiceName() {
		cachedServiceName = "";
		if( courierService != null ) {
			cachedServiceName += getCourierService().getDisplayName();
		}

		if( additionalShippingOption != null ) {
			cachedServiceName += " " + additionalShippingOption.getDisplayName();
		} else if( shippingService != null ) {
			cachedServiceName += " " + shippingService.getDisplayName();
		}
	}
	
	public void loadAndInitialise() {
		if( getCourierService() != null ) {
			setCourierService( getCourierService() );
		}
		if( getShippingService() != null ) {
			setShippingService( getShippingService() );
		}
		if( getAdditionalShippingOption() != null ) {
			setAdditionalShippingOption( getAdditionalShippingOption() );
		}
	}
	
	public boolean hasShippingService( ShippingService shippingService ) {
		if( getShippingService() != null && getShippingService().equals( shippingService ) ) {
			return true;
		} else if( getShippingService() == null && shippingService == null ) {
			return true;
		} else {
			return false;
		}
		
	}
	
	public String getDisplayName() {
		if (cachedServiceName == null) {
			return getClass().getSimpleName().replaceAll("([a-z])([A-Z])", "$1 $2");
		}
		return cachedServiceName;
	}

	public static List<AvailableShippingService> sortByCachedCost( List<AvailableShippingService> unsortedList ) {
		Collections.sort( unsortedList, new Comparator<AvailableShippingService>() {
			@Override
			public int compare(AvailableShippingService o1,
					AvailableShippingService o2) {
				return o1.getCachedCost().compareTo( o2.getCachedCost() );
			}
		});

		return unsortedList;
	}

	public boolean isUsingVolumetricWeight() {
		if( getShippingService() != null && getShippingService().isUsingVolumetricWeight() ) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean equals(Object o) {
		if( o == null ) {
			return false;
		} else if( o instanceof AvailableShippingService ) {
			if( ((AvailableShippingService)o).getShippingService() == null && getShippingService() == null ) {
				return true;
			} else if( ((AvailableShippingService)o).getShippingService() != null &&
					((AvailableShippingService)o).getShippingService().equals( getShippingService() )) {
				if( ((AvailableShippingService)o).getAdditionalShippingOption() == null && getAdditionalShippingOption() == null ) {
					return true;
				} else if( ((AvailableShippingService)o).getAdditionalShippingOption() != null &&
						((AvailableShippingService)o).getAdditionalShippingOption().equals( getAdditionalShippingOption() )) {
					return true;
				}
			} else if (((AvailableShippingService)o).getCachedServiceName() != null) {
				return ((AvailableShippingService)o).getCachedServiceName().equals(this.getCachedServiceName());
			}
		}
		return false;
	}

	public BigDecimal getConvertedCachedCost( Currency currency ) {
		return currency.getConvertedPrice( getCachedCost() );
	}

	public String getConvertedCachedCostString( Currency currency ) {
		return FormatUtil.formatTwoDigit( getConvertedCachedCost(currency).setScale( 2, RoundingMode.HALF_UP ).doubleValue() );
	}

	public BigDecimal updateCachedCost( EcommerceShoppingCart shoppingCart ) {
		if( shippingService != null ) {
			cachedCost = shippingService.calculateCost( shoppingCart, additionalShippingOption );
		}
		if( getAdditionalShippingCost() != null ) {
			cachedCost = cachedCost.add( getAdditionalShippingCost() );
		}
		return cachedCost;
	}

	public void setAdditionalShippingOption(AdditionalShippingOption additionalShippingOption) {
		this.additionalShippingOption = additionalShippingOption;
	}
	public AdditionalShippingOption getAdditionalShippingOption() {
		return additionalShippingOption;
	}

	public void setCachedServiceName(String cachedServiceName) {
		this.cachedServiceName = cachedServiceName;
	}

	public String getCachedServiceName() {
		return cachedServiceName;
	}

	public void setCourierService(CourierService courierService) {
		this.courierService = courierService;
	}

	public CourierService getCourierService() {
		return courierService;
	}

	public void setShippingService(ShippingService shippingService) {
		this.shippingService = shippingService;
	}

	public ShippingService getShippingService() {
		return shippingService;
	}

	public void setCachedCost(BigDecimal cachedCost) {
		this.cachedCost = cachedCost;
	}

	public BigDecimal getCachedCost() {
		return cachedCost;
	}

	public BigDecimal getAdditionalShippingCost() {
		return additionalShippingCost;
	}

	public void setAdditionalShippingCost(BigDecimal additionalShippingCost) {
		this.additionalShippingCost = additionalShippingCost;
	}
}
