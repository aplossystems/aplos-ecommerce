package com.aplos.ecommerce.beans.couriers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.common.annotations.RemoveEmpty;
import com.aplos.common.annotations.persistence.Cascade;
import com.aplos.common.annotations.persistence.CascadeType;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.OneToMany;
import com.aplos.common.beans.Address;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.utils.FormatUtil;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.module.EcommerceConfiguration;

@Entity
@SessionScoped
@ManagedBean
public class CourierService extends AplosBean {
	private static final long serialVersionUID = 6201717421330676435L;

	private String name;
	private Date collectionTime;
	private String trackingUrl;

	@Column(columnDefinition="LONGTEXT")
	private String trackingInstructions;

	@ManyToOne
	@RemoveEmpty
	@Cascade({CascadeType.ALL})
	private Address address;

	@Column(columnDefinition="LONGTEXT")
	private String notes;
	
	@OneToMany
	private List<ShippingService> shippingServices = new ArrayList<ShippingService>();

	private Class<? extends ShippingService> shippingServiceClass;

	public CourierService() {}

	@Override
	public String getDisplayName() {
		return name;
	}

	public List<AvailableShippingService> getAvailableShippingServices( Transaction transaction, boolean isFrontEnd ) {
		List<AvailableShippingService> availableShippingServices = new ArrayList<AvailableShippingService>();
		AvailableShippingService tempAvailableShippingService;
		BigDecimal additionalShippingCost = EcommerceConfiguration.getEcommerceSettingsStatic().getAdditionalShippingCost();
		for ( ShippingService tempShippingService : getShippingServices() ) {
			boolean applicabilityAssessedIndividually = tempShippingService.isApplicabilityAssessedIndividually();
			if ( tempShippingService.isApplicable( transaction, null, isFrontEnd ) ) {
				tempAvailableShippingService = new AvailableShippingService();
				tempAvailableShippingService.setCourierService( this );
				tempAvailableShippingService.setShippingService( tempShippingService );
				tempAvailableShippingService.updateCachedServiceName();
				tempAvailableShippingService.setAdditionalShippingCost( additionalShippingCost );
				tempAvailableShippingService.updateCachedCost( transaction.getEcommerceShoppingCart() );
				availableShippingServices.add( tempAvailableShippingService );
			}
			boolean parentServiceApplicable = tempShippingService.isParentApplicable( transaction, isFrontEnd );
			if ( applicabilityAssessedIndividually || parentServiceApplicable ) {
				for ( AdditionalShippingOption tempOption : tempShippingService.getAdditionalShippingOptions() ) {
					if ( (parentServiceApplicable && !applicabilityAssessedIndividually) ||
							tempShippingService.isApplicable( transaction, tempOption, isFrontEnd )	) {
						tempAvailableShippingService = new AvailableShippingService();
						tempAvailableShippingService.setCourierService( this );
						tempAvailableShippingService.setShippingService( tempShippingService );
						tempAvailableShippingService.setAdditionalShippingOption(tempOption);
						tempAvailableShippingService.setAdditionalShippingCost( additionalShippingCost );
						tempAvailableShippingService.updateCachedCost( transaction.getEcommerceShoppingCart() );
						tempAvailableShippingService.updateCachedServiceName();
						availableShippingServices.add( tempAvailableShippingService );
					}
				}
			}
		}

		return availableShippingServices;
	}

	public String getCollectionDayName( Date fromDate ) {
		Calendar cal = new GregorianCalendar();
		cal.setTime( fromDate );
		// This is indexed 0 for Sunday
		int dayOfTheWeekIdx = cal.get( Calendar.DAY_OF_WEEK );
		int courierDateIdx = getCollectionDayIdx(fromDate);

		if( dayOfTheWeekIdx == courierDateIdx ) {
			return "today";
		} else if( ((dayOfTheWeekIdx+1) % 7) == courierDateIdx ) {
			return "tomorrow";
		} else if( dayOfTheWeekIdx == 1 ) {
			return "on Monday";
		} else {
			cal.set( Calendar.DAY_OF_WEEK, courierDateIdx );
			return cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.UK );
		}

	}

	public int getCollectionDayIdx( Date fromDate ) {
		Calendar fromDateCal = new GregorianCalendar();
		Calendar courierCollectionDateCal = new GregorianCalendar();
		fromDateCal.setTime( fromDate );
		courierCollectionDateCal.setTime( getCollectionTime() );
		// This is indexed 1 for Sunday
		int dayOfTheWeekIdx = fromDateCal.get( Calendar.DAY_OF_WEEK );

		if( fromDateCal.get( Calendar.HOUR_OF_DAY ) > courierCollectionDateCal.get( Calendar.HOUR_OF_DAY ) ) {
			dayOfTheWeekIdx++;
			dayOfTheWeekIdx = dayOfTheWeekIdx % 7;
		} else if( fromDateCal.get( Calendar.HOUR_OF_DAY ) == courierCollectionDateCal.get( Calendar.HOUR_OF_DAY )
						&& fromDateCal.get( Calendar.MINUTE ) > courierCollectionDateCal.get( Calendar.MINUTE ) ) {
			dayOfTheWeekIdx++;
			dayOfTheWeekIdx = dayOfTheWeekIdx % 7;
		}

		if( dayOfTheWeekIdx == 7 || dayOfTheWeekIdx == 1 ) { // Saturday or Sunday
			return 1;
		}

		return dayOfTheWeekIdx;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setCollectionTime(Date collectionTime) {
		this.collectionTime = collectionTime;
	}

	public Date getCollectionTime() {
		return collectionTime;
	}

	public String getCollectionTimeStr() {
		if( collectionTime != null ) {
			return FormatUtil.getStdHourMinuteFormat().format( collectionTime );
		} else {
			return null;
		}
	}

	public void setTrackingUrl(String trackingUrl) {
		this.trackingUrl = trackingUrl;
	}

	public String getTrackingUrl() {
		return trackingUrl;
	}

	public void setTrackingInstructions(String trackingInstructions) {
		this.trackingInstructions = trackingInstructions;
	}

	public String getTrackingInstructions() {
		return trackingInstructions;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Address getAddress() {
		return address;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getNotes() {
		return notes;
	}

	public void setShippingServices(List<ShippingService> shippingServices) {
		this.shippingServices = shippingServices;
	}

	public List<ShippingService> getShippingServices() {
		return shippingServices;
	}

	public void setShippingServiceClass(Class<? extends ShippingService> shippingServiceClass) {
		this.shippingServiceClass = shippingServiceClass;
	}

	public Class<? extends ShippingService> getShippingServiceClass() {
		return shippingServiceClass;
	}

}
