package com.aplos.ecommerce.developermodulebacking.frontend;

import java.util.ArrayList;
import java.util.List;

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
import com.aplos.common.enums.CheckoutPageEntry;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.CompanyContact;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.enums.EcommerceBundleKey;
import com.aplos.ecommerce.module.EcommerceConfiguration;
import com.aplos.ecommerce.utils.EcommerceUtil;

@ManagedBean
@CustomScoped(value="#{ tabSession }")
public class CheckoutShippingFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = -5034851674554022825L;

	private boolean continueToPayment = false;
	private boolean isShowingSameAsShippingLabel = false;


	@Override
	public boolean responsePageLoad( DeveloperCmsAtom developerCmsAtom ) {
		super.responsePageLoad(developerCmsAtom);
		EcommerceUtil.getEcommerceUtil().addCheckoutPageEntry( CheckoutPageEntry.SHIPPING );
		Customer customer = (Customer)JSFUtil.getBeanFromScope( Customer.class );

		if( !EcommerceUtil.getEcommerceUtil().checkCheckoutAccess(customer, "the checkout process" ) ) {
			return false;
		}
		return true;
	}
	
	// Allows Overridding
	public boolean isShowingUseDefaultAddress() {
		return true;
	}

	public boolean isUsingAlternativeShippingAddress() {
		Customer customer = ((Transaction) JSFUtil.getBeanFromScope( Transaction.class )).getCustomer();
		if( customer instanceof CompanyContact ) {
			return ((CompanyContact)customer).getCompany().isUsingAlternativeAddress();
		} else {
			return customer.isUsingAlternativeAddress();
		}
	}

	public void useDefaultShippingAddress() {
		Transaction transaction = (Transaction)JSFUtil.getBeanFromScope(Transaction.class);
		transaction.getShippingAddress().copy( transaction.getEcommerceShoppingCart().getCustomer().determineShippingAddress() );
		transaction.getShippingAddress().setContactFirstName(transaction.getCustomer().getSubscriber().getFirstName());
		transaction.getShippingAddress().setContactSurname(transaction.getCustomer().getSubscriber().getSurname());
	}

	public void useAltShippingAddress() {
		Transaction transaction = (Transaction)JSFUtil.getBeanFromScope(Transaction.class);
		transaction.getShippingAddress().copy( transaction.getEcommerceShoppingCart().getCustomer().determineAltShippingAddress() );
		transaction.getShippingAddress().setContactFirstName(transaction.getCustomer().getSubscriber().getFirstName());
		transaction.getShippingAddress().setContactSurname(transaction.getCustomer().getSubscriber().getSurname());
	}

	public void clearShippingAddress() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		transaction.getShippingAddress().copy( new Address() );
	}

	public boolean isValidationRequired() {
		return BackingPage.validationRequired("checkoutBtn") || BackingPage.validationRequired("transactionFrontEndMenuStep");
	}

	public String goToPaymentOrBilling() {
		Customer customer = JSFUtil.getBeanFromScope(Customer.class);
		Transaction transaction = JSFUtil.getBeanFromScope( Transaction.class );
		if (transaction.getShippingAddress() != null &&
			!CommonUtil.isNullOrEmpty( transaction.getShippingAddress().getCity() ) &&
			!CommonUtil.validatePositiveInteger(transaction.getShippingAddress().getCity())) {
			if( CommonUtil.isNullOrEmpty( customer.getSubscriber().getFirstName() ) ||
					CommonUtil.isNullOrEmpty( customer.getSubscriber().getSurname() ) ) {
				customer.getSubscriber().setFirstName( transaction.getShippingAddress().getContactFirstName() );
				customer.getSubscriber().setSurname( transaction.getShippingAddress().getContactSurname() );
				customer.getSubscriber().saveDetails();
			}

			if( CommonUtil.getStringOrEmpty( customer.determineShippingAddress().getLine1()).equals( "" ) ||
					EcommerceConfiguration.getEcommerceSettingsStatic().isUpdatingCustomerAddressesFromTransaction() ) {
				customer.determineShippingAddress().copy( transaction.getShippingAddress() );
				customer.determineShippingAddress().saveDetails();
			}

			transaction.updateVatExemption( true );
			transaction.getEcommerceShoppingCart().updateCachedValues( true );
			if ( transaction.isDeliveryAddressTheSame() ) {
				transaction.getBillingAddress().copy(transaction.getShippingAddress());

				if( CommonUtil.getStringOrEmpty( customer.determineBillingAddress().getLine1() ).equals( "" ) ||
						EcommerceConfiguration.getEcommerceSettingsStatic().isUpdatingCustomerAddressesFromTransaction() ) {
					customer.determineBillingAddress().copy( transaction.getShippingAddress() );
					customer.determineBillingAddress().saveDetails();
				}
			}

			if (continueToPayment || transaction.isDeliveryAddressTheSame()) {
				EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutPaymentCpr();
				continueToPayment = false;
			} else {
				EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutBillingAddressCpr();
			}

			transaction.saveDetails();
		} else {
			JSFUtil.addMessageForError("You must enter a city. City must include at least one non-numeric character.");
		}
		return null;
	}

	public String getPreviousBtnText() {
		return "Go to " + EcommerceConfiguration.getEcommerceConfiguration().getCartDisplayName();
	}

	public String getNextBtnText() {
		Transaction transaction = JSFUtil.getBeanFromScope( Transaction.class );
		if( continueToPayment || transaction.isDeliveryAddressTheSame() ) {
			return "Go to payment";
		} else {
			return ApplicationUtil.getAplosContextListener().translateByKey( EcommerceBundleKey.GO_TO_BILLING );
		}
	}

	public boolean isShowingUsStates() {
		Transaction transaction = JSFUtil.getBeanFromScope( Transaction.class );

		if( transaction.getShippingAddress().getCountry() != null && transaction.getShippingAddress().getCountry().getId().equals( 840l ) ) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isShowingCanadianStates() {
		Transaction transaction = JSFUtil.getBeanFromScope( Transaction.class );

		if( transaction.getShippingAddress().getCountry() != null && transaction.getShippingAddress().getCountry().getId().equals( 124l ) ) {
			return true;
		} else {
			return false;
		}
	}

	public SelectItem[] getCanadianStateSelectItems() {
		List<CountryArea> canadianCountryAreas = new BeanDao( CountryArea.class ).addWhereCriteria( "country.id = 124" ).setIsReturningActiveBeans(true).getAll();
		return AplosBean.getSelectItemBeans( canadianCountryAreas );
	}

	public SelectItem[] getUsStateSelectItems() {
		List<CountryArea> usCountryAreas = new BeanDao( CountryArea.class ).addWhereCriteria( "country.id = 840" ).setIsReturningActiveBeans(true).getAll();
		return AplosBean.getSelectItemBeans( usCountryAreas );
	}

	public boolean isShowingBackButton() {
		if( isContinueToPayment() ||  EcommerceConfiguration.getEcommerceCprsStatic().getCheckoutCartCpr() == null ) {
			return false;
		} else {
			return true;
		}
	}

	public void goToCart() {
		EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutCartCpr();
	}

	public SelectItem[] getCountrySelectItems() {
		return ApplicationUtil.getAplosModuleFilterer().getCountrySelectItems();
	}

	public List<SelectItem> getBillingAddressOptions() {
		List<SelectItem> items = new ArrayList<SelectItem>();

		String billingAddressTheSameStr = ApplicationUtil.getAplosContextListener().translateByKey( EcommerceBundleKey.BILLING_ADDRESS_IS_THE_SAME );
		String enterABillingAddressStr = ApplicationUtil.getAplosContextListener().translateByKey( EcommerceBundleKey.I_NEED_TO_ENTER_A_BILLING_ADDRESS );
		items.add( new SelectItem(true, CommonUtil.firstLetterToUpperCase( billingAddressTheSameStr ) ) );
		items.add( new SelectItem(false, CommonUtil.firstLetterToUpperCase( enterABillingAddressStr ) ) );
		return items;
	}

	public void setContinueToPayment(boolean continueToPayment) {
		this.continueToPayment = continueToPayment;
	}

	public boolean isContinueToPayment() {
		return continueToPayment;
	}

	public void setShowingSameAsShippingLabel(boolean isShowingSameAsShippingLabel) {
		this.isShowingSameAsShippingLabel = isShowingSameAsShippingLabel;
	}

	public boolean isShowingSameAsShippingLabel() {
		return isShowingSameAsShippingLabel;
	}
	
	public String getIsDeliveryPostcodeRequiredString() {
		if (EcommerceConfiguration.getEcommerceSettingsStatic().isDeliveryPostcodeRequired()) {
			return "required"; //include and require field
		} else {
			return "true"; //include field
		}
	}

}
