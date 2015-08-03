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
import com.aplos.common.beans.Address;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.CountryArea;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.CompanyContact;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.module.EcommerceConfiguration;
import com.aplos.ecommerce.utils.EcommerceUtil;

@ManagedBean
@CustomScoped(value="#{ tabSession }")
public class CustomerShippingFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = -2752334101579909864L;
	private CountryArea stateCountryArea;
	private CountryArea companyStateCountryArea;
	private boolean isUsingBillingAddressForShipping;
	private Address shippingAddress;
	private Address companyShippingAddress;

	@Override
	public boolean responsePageLoad( DeveloperCmsAtom developerCmsAtom ) {
		super.responsePageLoad(developerCmsAtom);
		Customer customer = JSFUtil.getBeanFromScope(Customer.class);
		if( EcommerceUtil.getEcommerceUtil().checkCustomerLogin(customer, "the checkout process" ) ) {
			isUsingBillingAddressForShipping = customer.getDuplicateAddresses().equals(Customer.DuplicateAddresses.BILLING_ADDRESS_FOR_SHIPPING);
			setShippingAddress( (Address) customer.getShippingAddress().getSaveableBean() );
			if( isCompanyUser() ) {
				setCompanyShippingAddress( (Address) ((CompanyContact) customer).getCompany().getShippingAddress().getSaveableBean() );
			}
			if( customer.getShippingAddress().getState() != null ) {
				BeanDao countryAreaDao = new BeanDao( CountryArea.class ).addWhereCriteria( "areaCode = :countryAreaName AND (country.id = 840 OR country.id = 124)" );
				countryAreaDao.setNamedParameter( "countryAreaName", customer.getShippingAddress().getState() );
				List<CountryArea> countryAreaList = countryAreaDao.getAll();
				if( countryAreaList.size() > 0 ) {
					stateCountryArea = countryAreaList.get( 0 );
				}
			}
		}
		return true;
	}

	public void usingBillingAddressForShippingChanged() {
		Customer customer = JSFUtil.getBeanFromScope( Customer.class );
		if( isUsingBillingAddressForShipping() ) {
			customer.setDuplicateAddresses( Customer.DuplicateAddresses.BILLING_ADDRESS_FOR_SHIPPING );
		} else {
			customer.setDuplicateAddresses( Customer.DuplicateAddresses.NONE );
		}
	}

	public String getIsDeliveryPostcodeRequiredString() {
		if (EcommerceConfiguration.getEcommerceSettingsStatic().isDeliveryPostcodeRequired()) {
			return "required";
		} else {
			return "true";
		}
	}
	
	public boolean isValidationRequired() {
		return BackingPage.validationRequired("saveBtn");
	}

	public boolean isCompanyUser() {
		Customer cust = JSFUtil.getBeanFromScope(Customer.class);
		if (cust != null && cust instanceof CompanyContact) {
			return true;
		}
		return false;
	}

	public String saveChanges() {
		Customer customer = JSFUtil.getBeanFromScope(Customer.class).getSaveableBean();
		if( customer.isNew() ) {
			customer.getSubscriber().setFirstName( customer.getShippingAddress().getContactFirstName() );
			customer.getSubscriber().setSurname( customer.getShippingAddress().getContactSurname() );
			customer.saveDetails();
		}
		JSFUtil.addMessage("Changes have been saved.", FacesMessage.SEVERITY_INFO);
		return null;
	}

	public boolean isCustomerUsingCompanyAddressForShipping() {
		Customer cust = JSFUtil.getBeanFromScope(Customer.class);
		if (cust != null && cust instanceof CompanyContact) {
			return ((CompanyContact)cust).isUsingCompanyAddressForShipping();
		}
		return false;
	}

	public void setCustomerUsingCompanyAddressForShipping( boolean isUsingCompanyAddressForShipping ) {
		Customer cust = JSFUtil.getBeanFromScope(Customer.class);
		if (cust != null && cust instanceof CompanyContact) {
			((CompanyContact)cust).setUsingCompanyAddressForShipping( isUsingCompanyAddressForShipping );
		}
	}

	public boolean isShowingUsStates() {
		Customer customer = JSFUtil.getBeanFromScope(Customer.class);

		if( customer.getShippingAddress().getCountry() != null && customer.getShippingAddress().getCountry().getId().equals( 840l ) ) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isShowingCompanyUsStates() {
		CompanyContact customer = JSFUtil.getBeanFromScope(Customer.class);

		if( customer.getCompany().getShippingAddress().getCountry() != null && customer.getCompany().getShippingAddress().getCountry().getId().equals( 840l ) ) {
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

		if( customer.getShippingAddress().getCountry() != null && customer.getShippingAddress().getCountry().getId().equals( 124l ) ) {
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

	public SelectItem[] getCanadianStateSelectItems() {
		List<CountryArea> canadianCountryAreas = new BeanDao( CountryArea.class ).addWhereCriteria( "country.id = 124" ).setIsReturningActiveBeans(true).getAll();
		return AplosBean.getSelectItemBeans( canadianCountryAreas );
	}

	public SelectItem[] getCountrySelectItems() {
		return ApplicationUtil.getAplosModuleFilterer().getCountrySelectItems();
	}

	public List<SelectItem> getBillingAddressOptions() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		items.add( new SelectItem(true, "Use my billing address") );
		items.add( new SelectItem(false, "Use my shipping address entered here ...") );
		return items;
	}

	public List<SelectItem> getCompanyBillingAddressOptions() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		items.add( new SelectItem(true, "Use the company billing address") );
		items.add( new SelectItem(false, "Use the shipping address entered here ...") );
		return items;
	}

	public List<SelectItem> getCompanyAddressOptions() {
		List<SelectItem> items = new ArrayList<SelectItem>();
		items.add( new SelectItem(true, "Use company addresses (left)") );
		items.add( new SelectItem(false, "Use my addresses (right)") );
		return items;
	}

	public void setStateCountryArea(CountryArea stateCountryArea) {
		this.stateCountryArea = stateCountryArea;
	}

	public CountryArea getStateCountryArea() {
		return stateCountryArea;
	}

	public void setCompanyStateCountryArea(CountryArea companyStateCountryArea) {
		this.companyStateCountryArea = companyStateCountryArea;
	}

	public CountryArea getCompanyStateCountryArea() {
		return companyStateCountryArea;
	}

	public void setUsingBillingAddressForShipping(
			boolean isUsingBillingAddressForShipping) {
		this.isUsingBillingAddressForShipping = isUsingBillingAddressForShipping;
	}

	public boolean isUsingBillingAddressForShipping() {
		return isUsingBillingAddressForShipping;
	}

	public Address getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(Address shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public Address getCompanyShippingAddress() {
		return companyShippingAddress;
	}

	public void setCompanyShippingAddress(Address companyShippingAddress) {
		this.companyShippingAddress = companyShippingAddress;
	}


}
