package com.aplos.ecommerce.developermodulebacking.frontend;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;

import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.BackingPage;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.CountryArea;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.CompanyContact;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.utils.EcommerceUtil;

@ManagedBean
@CustomScoped(value="#{ tabSession }")
public class CustomerBillingFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = -2147454290124184906L;
	private CountryArea stateCountryArea;
	private boolean isUsingShippingAddressForBilling;

	@Override
	public boolean responsePageLoad( DeveloperCmsAtom developerCmsAtom ) {
		super.responsePageLoad(developerCmsAtom);
		Customer customer = JSFUtil.getBeanFromScope(Customer.class);
		if( EcommerceUtil.getEcommerceUtil().checkCustomerLogin(customer, "the checkout process" ) ) {
			isUsingShippingAddressForBilling = customer.getDuplicateAddresses().equals(Customer.DuplicateAddresses.SHIPPING_ADDRESS_FOR_BILLING);
			if( customer.getBillingAddress().getState() != null ) {
				BeanDao countryAreaDao = new BeanDao( CountryArea.class ).addWhereCriteria( "areaCode = :countryAreaName AND (country.id = 840 OR country.id = 124)" );
				countryAreaDao.setNamedParameter( "countryAreaName", customer.getBillingAddress().getState() );
				List<CountryArea> countryAreaList = countryAreaDao.getAll();
				if( countryAreaList.size() > 0 ) {
					stateCountryArea = countryAreaList.get( 0 );
				}
			}
		}
		return true;
	}

	public void usingShippingAddressForBillingChanged() {
		Customer customer = JSFUtil.getBeanFromScope( Customer.class );
		if( isUsingShippingAddressForBilling() ) {
			customer.setDuplicateAddresses( Customer.DuplicateAddresses.SHIPPING_ADDRESS_FOR_BILLING );
		} else {
			customer.setDuplicateAddresses( Customer.DuplicateAddresses.NONE );
		}
	}

	public List<SelectItem> getCompanyAddressOptions() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		items.add( new SelectItem(true, "Use company addresses (left)") );
		items.add( new SelectItem(false, "Use my addresses (right)") );
		return items;
	}

	public boolean isShowingCompanyUsStates() {
		CompanyContact customer = JSFUtil.getBeanFromScope(Customer.class);

		if( customer.getCompany().getShippingAddress().getCountry() != null && customer.getCompany().getShippingAddress().getCountry().getId().equals( 840l ) ) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isShowingCompanyCanadianStates() {
		CompanyContact customer = JSFUtil.getBeanFromScope(Customer.class);

		if( customer.getCompany().getShippingAddress().getCountry() != null && customer.getCompany().getShippingAddress().getCountry().getId().equals( 124l ) ) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isCompanyUser() {
		Customer cust = JSFUtil.getBeanFromScope(Customer.class);
		if (cust != null && cust instanceof CompanyContact) {
			return true;
		}
		return false;
	}

	public boolean isCustomerUsingCompanyAddressForBilling() {
		Customer cust = JSFUtil.getBeanFromScope(Customer.class);
		if (cust != null && cust instanceof CompanyContact) {
			return ((CompanyContact)cust).isUsingCompanyAddressForBilling();
		}
		return false;
	}

	public void setCustomerUsingCompanyAddressForBilling( boolean isUsingCompanyAddressForBilling ) {
		Customer cust = JSFUtil.getBeanFromScope(Customer.class);
		if (cust != null && cust instanceof CompanyContact) {
			((CompanyContact)cust).setUsingCompanyAddressForBilling( isUsingCompanyAddressForBilling );
		}
	}

	public boolean isValidationRequired() {
		return BackingPage.validationRequired("saveBtn");
	}

	public String saveChanges() {
		Customer customer = JSFUtil.getBeanFromScope(Customer.class);
		if ( customer.getBillingAddress().getCountry() != null && (customer.getBillingAddress().getCountry().getId().equals( 840l ) || customer.getBillingAddress().getCountry().getId().equals( 124l )) ) {
			customer.getBillingAddress().setState( getStateCountryArea().getAreaCode() );
		}
		customer.saveDetails();
		JSFUtil.addMessage("Changes have been saved.", FacesMessage.SEVERITY_INFO);
		return null;
	}

	public boolean isShowingUsStates() {
		Customer customer = JSFUtil.getBeanFromScope(Customer.class);

		if( customer.getBillingAddress().getCountry() != null && customer.getBillingAddress().getCountry().getId().equals( 840l ) ) {
			return true;
		} else {
			return false;
		}
	}

	public SelectItem[] getUsStateSelectItems() {
		List<CountryArea> usCountryAreas = new BeanDao( CountryArea.class ).addWhereCriteria( "country.id = 840" ).setIsReturningActiveBeans(true).getAll();
		return AplosBean.getSelectItemBeans( usCountryAreas );
	}

	public boolean isShowingCanadianStates() {
		Customer customer = JSFUtil.getBeanFromScope(Customer.class);

		if( customer.getBillingAddress().getCountry() != null && customer.getBillingAddress().getCountry().getId().equals( 124l ) ) {
			return true;
		} else {
			return false;
		}
	}

	public SelectItem[] getCanadianStateSelectItems() {
		List<CountryArea> canadianCountryAreas = new BeanDao( CountryArea.class ).addWhereCriteria( "country.id = 124" ).setIsReturningActiveBeans(true).getAll();
		return AplosBean.getSelectItemBeans( canadianCountryAreas );
	}

	public List<SelectItem> getShippingAddressOptions() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		items.add( new SelectItem(true, "My " + CommonUtil.translate("SHIPPING_ADDRESS") + " is the same as above") );
		items.add( new SelectItem(false, "My " + CommonUtil.translate("SHIPPING_ADDRESS") + " is different") );
		return items;
	}

	public SelectItem[] getCountrySelectItems() {
		return ApplicationUtil.getAplosModuleFilterer().getCountrySelectItems();
	}

	public void setStateCountryArea(CountryArea stateCountryArea) {
		this.stateCountryArea = stateCountryArea;
	}

	public CountryArea getStateCountryArea() {
		return stateCountryArea;
	}

	public void setUsingShippingAddressForBilling(
			boolean isUsingShippingAddressForBilling) {
		this.isUsingShippingAddressForBilling = isUsingShippingAddressForBilling;
	}

	public boolean isUsingShippingAddressForBilling() {
		return isUsingShippingAddressForBilling;
	}

}
