package com.aplos.ecommerce.beans;

import java.math.BigDecimal;

import com.aplos.common.annotations.persistence.Cache;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.FetchType;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.Address;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.enums.VatExemption;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;

@Entity
@Cache
public class CompanyContact extends Customer {

	private static final long serialVersionUID = 7511371565440882149L;

	private BigDecimal discountPercentage;

	@ManyToOne(fetch=FetchType.LAZY)
	private Company company;

	private String roomNumber;

	private boolean isUsingCompanyAddressForBilling = true;
	private boolean isUsingCompanyAddressForShipping = true;
	private boolean isUsingCompanyAddressForAltShipping = true;
	private boolean isAdminUser = false;

	// Update the importCustomer method if any primitives are added to
	// this class

	public CompanyContact() {}

	public CompanyContact( Customer customer ) {
		copy( customer );
	}

	public BigDecimal determineDiscountPercentage() {
		if ( getDiscountPercentage() != null ) {
			return getDiscountPercentage();
		}
		else {
			return company.determineDiscountPercentage();
		}
	}

	public static CompanyContact getCompanyContactOrCreate( String emailAddress ) {
		Customer customer = getCustomerOrCreate( emailAddress, CompanyContact.class );
		if( !(customer instanceof CompanyContact) ) {
			customer = convertFromCustomer( customer );
		}

		return (CompanyContact) customer;
	}

	@Override
	public boolean isVatExempt(VatExemption vatExemption) {
		if( VatExemption.EU_EXEMPT.equals( vatExemption ) ) {
			if( CommonUtil.getStringOrEmpty( getCompany().getVatNumber() ).equals( "" ) ) {
				return false;
			} else {
				return true;
			}
		} else {
			return super.isVatExempt(vatExemption);
		}
	}

	@Override
	public boolean isCompanyConnectionRequested() {
		// This is just a safeguard, but it should be set to false anyway.
		return false;
	}

	public static CompanyContact convertFromCustomer( Customer customer ) {
		ApplicationUtil.executeSql( "INSERT INTO " + AplosBean.getTableName( CompanyContact.class ) + "(id, isUsingCompanyAddressForBilling, isUsingCompanyAddressForShipping, isUsingCompanyAddressForAltShipping, isAdminUser ) VALUES (" + customer.getId() + ", true, true, true, false )" );
		CompanyContact companyContact = new BeanDao( CompanyContact.class ).get( customer.getId() );
		companyContact.setCompanyConnectionRequested( false );
		companyContact.saveDetails();
		return companyContact;
	}

	@Override
	public void redirectToEditPage() {
		super.redirectToEditPage();
		CompanyContact companyContact = JSFUtil.getBeanFromScope( this.getClass() );
		companyContact.getCompany().addToScope();
	}

	@Override
	public Address determineBillingAddress() {
		if( isUsingCompanyAddressForBilling ) {
			return company.determineBillingAddress();
		} else {
			return super.determineBillingAddress();
		}
	}

	@Override
	public Address determineShippingAddress() {
		if( isUsingCompanyAddressForShipping ) {
			return company.determineShippingAddress();
		} else {
			return super.determineShippingAddress();
		}
	}

	@Override
	public Address determineAltShippingAddress() {
		if( isUsingCompanyAddressForAltShipping ) {
			return company.getAltShippingAddress();
		} else {
			return super.determineAltShippingAddress();
		}
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Company getCompany() {
		return company;
	}

	@Override
	public BeanDao getRealizedProductDao() {
		return new BeanDao( RealizedProduct.class )
					.addWhereCriteria( "bean.isHiddenFromCompanyContact = false" );
	}

	public void setDiscountPercentage(BigDecimal discountPercentage) {
		this.discountPercentage = discountPercentage;
	}

	public BigDecimal getDiscountPercentage() {
		return discountPercentage;
	}

	public void setUsingCompanyAddressForBilling(
			boolean isUsingCompanyAddressForBilling) {
		this.isUsingCompanyAddressForBilling = isUsingCompanyAddressForBilling;
	}

	public boolean isUsingCompanyAddressForBilling() {
		return isUsingCompanyAddressForBilling;
	}

	public void setUsingCompanyAddressForShipping(
			boolean isUsingCompanyAddressForShipping) {
		this.isUsingCompanyAddressForShipping = isUsingCompanyAddressForShipping;
	}

	public boolean isUsingCompanyAddressForShipping() {
		return isUsingCompanyAddressForShipping;
	}

	public void setRoomNumber(String roomNumber) {
		this.roomNumber = roomNumber;
	}

	public String getRoomNumber() {
		return roomNumber;
	}

	public void setUsingCompanyAddressForAltShipping(
			boolean isUsingCompanyAddressForAltShipping) {
		this.isUsingCompanyAddressForAltShipping = isUsingCompanyAddressForAltShipping;
	}

	public boolean isUsingCompanyAddressForAltShipping() {
		return isUsingCompanyAddressForAltShipping;
	}

	public void setAdminUser(boolean isAdminUser) {
		this.isAdminUser = isAdminUser;
	}

	public boolean isAdminUser() {
		return isAdminUser;
	}
}
