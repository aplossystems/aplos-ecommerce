package com.aplos.ecommerce.backingpage.transaction;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.beans.Address;
import com.aplos.common.beans.CountryArea;
import com.aplos.common.beans.MenuStep;
import com.aplos.common.interfaces.MenuStepBacking;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.CompanyContact;
import com.aplos.ecommerce.beans.Transaction;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=Transaction.class)
public class TransactionEditDeliveryDetailsPage extends EditPage implements MenuStepBacking {

	private static final long serialVersionUID = -3658193934029668778L;
	private CountryArea stateCountryArea;

	@Override
	public boolean responsePageLoad() {
		super.responsePageLoad();
		Transaction transaction = JSFUtil.getBeanFromScope( Transaction.class );
		if ( transaction.getShippingAddress().getState() != null ) {
			BeanDao countryAreaDao = new BeanDao( CountryArea.class ).addWhereCriteria( "areaCode = :countryAreaName AND (country.id = 840 OR country.id = 124)" );
			countryAreaDao.setNamedParameter( "countryAreaName", transaction.getShippingAddress().getState() );
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
		transaction.updateVatExemption( true );
		updateState(transaction);
	}

	private void updateState(Transaction transaction) {
		if( transaction.getShippingAddress().getCountry() != null && (transaction.getShippingAddress().getCountry().getId().equals( 840l ) || transaction.getShippingAddress().getCountry().getId().equals( 124l )) ) {
			transaction.getShippingAddress().setState( getStateCountryArea().getAreaCode() );
		}
	}

	@Override
	public void okBtnAction() {
		updateState((Transaction) JSFUtil.getBeanFromScope( Transaction.class ));
		super.okBtnAction();
	}

	@Override
	public void applyBtnAction() {
		updateState((Transaction) JSFUtil.getBeanFromScope( Transaction.class ));
		super.applyBtnAction();
	}

	public void useDefaultDeliveryDetails() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		transaction.getShippingAddress().copy( transaction.getEcommerceShoppingCart().getCustomer().determineShippingAddress() );
		addAddressContactInformation( transaction );
	}

	public void useAlternativeDeliveryDetails() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		transaction.getShippingAddress().copy( transaction.getEcommerceShoppingCart().getCustomer().determineAltShippingAddress() );
		addAddressContactInformation( transaction );
	}

	public void addAddressContactInformation( Transaction transaction ) {
		if( transaction.getCustomer() instanceof CompanyContact ) {
			transaction.getShippingAddress().setCompanyName( ((CompanyContact) transaction.getCustomer()).getCompany().getCompanyName() );
		}
		transaction.getShippingAddress().setContactFirstName(transaction.getCustomer().getSubscriber().getFirstName());
		transaction.getShippingAddress().setContactSurname(transaction.getCustomer().getSubscriber().getSurname());
	}

	public String getCustomerCompanyName() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		if( transaction.getCustomer() instanceof CompanyContact ) {
			return ((CompanyContact) transaction.getCustomer()).getCompany().getCompanyName();
		}
		return "";
	}

	public void clearDeliveryDetails() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		transaction.getShippingAddress().copy(new Address());
	}

	public SelectItem[] getCountrySelectItems() {
		return ApplicationUtil.getAplosModuleFilterer().getCountrySelectItems();
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
