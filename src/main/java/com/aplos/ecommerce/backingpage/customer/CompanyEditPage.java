package com.aplos.ecommerce.backingpage.customer;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.beans.Address;
import com.aplos.common.beans.AplosAbstractBean;
import com.aplos.common.beans.Country;
import com.aplos.common.beans.CountryArea;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.Company;
import com.aplos.ecommerce.beans.CompanyType;
import com.aplos.ecommerce.enums.AddressStatus;
import com.aplos.ecommerce.enums.FollowUpStatus;
import com.aplos.ecommerce.enums.PromotionType;

@ManagedBean
@SessionScoped
@AssociatedBean(beanClass=Company.class)
public class CompanyEditPage extends EditPage {

	private String shippingStateSearchStr;
	private List<String> shippingStateResults;

	private String billingStateSearchStr;
	private List<String> billingStateResults;

	private boolean isUsingBillingAddressForShipping;
	private boolean isUsingShippingAddressForBilling;

	private static final long serialVersionUID = 6041507027979432030L;

	@Override
	public boolean responsePageLoad() {
		super.responsePageLoad();
		Company company = resolveAssociatedBean();
		if ( company != null ) {

			setUsingBillingAddressForShipping(company.getDuplicateAddresses().equals( Company.DuplicateAddresses.BILLING_ADDRESS_FOR_SHIPPING ));
			setUsingShippingAddressForBilling(company.getDuplicateAddresses().equals( Company.DuplicateAddresses.SHIPPING_ADDRESS_FOR_BILLING ));
		}
		return true;
	}

	public void usingBillingAddressForShippingChanged() {
		Company company = JSFUtil.getBeanFromScope( Company.class );
		if( isUsingBillingAddressForShipping() ) {
			company.setDuplicateAddresses( Company.DuplicateAddresses.BILLING_ADDRESS_FOR_SHIPPING );
		} else {
			company.setDuplicateAddresses( Company.DuplicateAddresses.NONE );
		}
	}

	public void usingShippingAddressForBillingChanged() {
		Company company = JSFUtil.getBeanFromScope( Company.class );
		if( isUsingShippingAddressForBilling() ) {
			company.setDuplicateAddresses( Company.DuplicateAddresses.SHIPPING_ADDRESS_FOR_BILLING );
		} else {
			company.setDuplicateAddresses( Company.DuplicateAddresses.NONE );
		}
	}

	public SelectItem[] getCompanyTypeSelectItemBeans() {
		return AplosAbstractBean.getSelectItemBeansStatic( CompanyType.class, "Not Selected");
	}

	public List<SelectItem> getAddressStatusSelectItemBeans() {
		return CommonUtil.getEnumSelectItems(AddressStatus.class, null);
	}

	public List<SelectItem> getFollowUpStatusSelectItemBeans() {
		return CommonUtil.getEnumSelectItems(FollowUpStatus.class, null);
	}

	public List<SelectItem> getPromotionTypeSelectItemBeans() {
		return CommonUtil.getEnumSelectItems(PromotionType.class, null);
	}

	@SuppressWarnings("unchecked")
	public SelectItem[] getAreaSelectItemBeans() {
		SelectItem[] selectItems;

		List<CountryArea> lookupBeanList = new ArrayList<CountryArea>();
		Country country = ((Company) JSFUtil.getBeanFromScope(Company.class)).determineShippingAddress().getCountry();

		if( country == null ) {
			country = (Country) ApplicationUtil.getAplosModuleFilterer().getCountrySelectItems()[ 0 ].getValue();
		}

		if (!(country==null)) {
			lookupBeanList.add( (CountryArea) new BeanDao( CountryArea.class ).get( country.getId() ) );
		}

		selectItems = new SelectItem[ lookupBeanList.size() + 1 ];
		selectItems[ 0 ] = new SelectItem( null, "Please select an area" );

		for( int i = 0, n = lookupBeanList.size(); i < n; i++ ) {
			selectItems[ i + 1 ] = new SelectItem( lookupBeanList.get( i ), lookupBeanList.get( i ).getDisplayName() );
		}

		return selectItems;
	}

	public void suggestShippingStates() {
		shippingStateResults = suggestStates(getShippingStateSearchStr());
	}

	public List<String> suggestStates( String searchStr ) {
		Company company = JSFUtil.getBeanFromScope(Company.class);
		BeanDao addressDao = new BeanDao( Address.class );
		addressDao.setSelectCriteria( "DISTINCT bean.state" );
		if( company.getBillingAddress().getCountry() != null ) {
			addressDao.addWhereCriteria( "bean.country.id = " + company.getBillingAddress().getCountry().getId() );
		}
		addressDao.addWhereCriteria( "bean.state LIKE :similarSearchText" );
		addressDao.setNamedParameter( "similarSearchText", "%" + searchStr + "%" );
		addressDao.setMaxResults( 20 );

		return addressDao.getAll();
	}

	public void suggestBillingStates() {
		billingStateResults = suggestStates(getBillingStateSearchStr());
	}

	public void setShippingStateSearchStr(String shippingStateSearchStr) {
		this.shippingStateSearchStr = shippingStateSearchStr;
	}

	public String getShippingStateSearchStr() {
		return shippingStateSearchStr;
	}

	public void setShippingStateResults(List<String> shippingStateResults) {
		this.shippingStateResults = shippingStateResults;
	}

	public List<String> getShippingStateResults() {
		return shippingStateResults;
	}

	public void setBillingStateSearchStr(String billingStateSearchStr) {
		this.billingStateSearchStr = billingStateSearchStr;
	}

	public String getBillingStateSearchStr() {
		return billingStateSearchStr;
	}

	public void setBillingStateResults(List<String> billingStateResults) {
		this.billingStateResults = billingStateResults;
	}

	public List<String> getBillingStateResults() {
		return billingStateResults;
	}

	public void setUsingBillingAddressForShipping(
			boolean isUsingBillingAddressForShipping) {
		this.isUsingBillingAddressForShipping = isUsingBillingAddressForShipping;
	}

	public boolean isUsingBillingAddressForShipping() {
		return isUsingBillingAddressForShipping;
	}

	public void setUsingShippingAddressForBilling(
			boolean isUsingShippingAddressForBilling) {
		this.isUsingShippingAddressForBilling = isUsingShippingAddressForBilling;
	}

	public boolean isUsingShippingAddressForBilling() {
		return isUsingShippingAddressForBilling;
	}
}