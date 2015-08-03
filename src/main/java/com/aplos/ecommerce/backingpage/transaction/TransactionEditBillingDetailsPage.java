package com.aplos.ecommerce.backingpage.transaction;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.beans.Address;
import com.aplos.common.beans.CountryArea;
import com.aplos.common.beans.MenuStep;
import com.aplos.common.interfaces.MenuStepBacking;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.CompanyContact;
import com.aplos.ecommerce.beans.Transaction;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=Transaction.class)
public class TransactionEditBillingDetailsPage extends EditPage implements MenuStepBacking {
	private static final long serialVersionUID = 2374482439764129885L;
	private CountryArea stateCountryArea;

	@Override
	public boolean responsePageLoad() {
		super.responsePageLoad();
		Transaction transaction = JSFUtil.getBeanFromScope( Transaction.class );
		if ( transaction.getBillingAddress().getState() != null ) {
			BeanDao countryAreaDao = new BeanDao( CountryArea.class ).addWhereCriteria( "areaCode = :countryAreaName AND (country.id = 840 OR country.id = 124)" );
			countryAreaDao.setNamedParameter( "countryAreaName", transaction.getBillingAddress().getState() );
			List<CountryArea> countryAreaList = countryAreaDao.getAll();
			if( countryAreaList.size() > 0 ) {
				stateCountryArea = countryAreaList.get( 0 );
			}
		}
		return true;
	}

	@Override
	public void beforeLeavingMenuStep( MenuStep menuStep ) {
		Transaction transaction = JSFUtil.getBeanFromScope( Transaction.class );
		updateState(transaction);
	}

	private void updateState(Transaction transaction) {
		if ( transaction.getBillingAddress().getCountry() != null && (transaction.getBillingAddress().getCountry().getId().equals( 840l ) || transaction.getBillingAddress().getCountry().getId().equals( 124l )) ) {
			transaction.getBillingAddress().setState( getStateCountryArea().getAreaCode() );
		}
	}

	public String getCustomerCompanyName() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		if( transaction.getCustomer() instanceof CompanyContact ) {
			return ((CompanyContact) transaction.getCustomer()).getCompany().getCompanyName();
		}
		return "";
	}

	public void redirectToDeliveryDetails() {
		JSFUtil.redirect(TransactionEditDeliveryDetailsPage.class );
	}

	public void useDefaultBillingDetails() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		transaction.getBillingAddress().copy( transaction.getEcommerceShoppingCart().getCustomer().determineBillingAddress() );
		if( transaction.getCustomer() instanceof CompanyContact ) {
			transaction.getBillingAddress().setCompanyName( ((CompanyContact) transaction.getCustomer()).getCompany().getCompanyName() );
		}
	}

	public void clearBillingDetails() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		transaction.getBillingAddress().copy( new Address() );
	}

	public boolean isValidationRequired() {
		return validationRequired("transactionMenuSteps");
	}

	public String addParenthesesIfNecessary(String string) {
		if (string != null && !string.equals("")) {
			return "(" + string + ")";
		}
		else {
			return "";
		}
	}

	public void setStateCountryArea(CountryArea stateCountryArea) {
		this.stateCountryArea = stateCountryArea;
	}

	public CountryArea getStateCountryArea() {
		return stateCountryArea;
	}
}
