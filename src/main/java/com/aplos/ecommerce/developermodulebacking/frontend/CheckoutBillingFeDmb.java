package com.aplos.ecommerce.developermodulebacking.frontend;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;

import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.backingpage.BackingPage;
import com.aplos.common.beans.Address;
import com.aplos.common.enums.CheckoutPageEntry;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.enums.EcommerceBundleKey;
import com.aplos.ecommerce.module.EcommerceConfiguration;
import com.aplos.ecommerce.utils.EcommerceUtil;

@ManagedBean
@CustomScoped(value="#{ tabSession }")
public class CheckoutBillingFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = -5094523785631682561L;

	private boolean comingFromPaymentPage = false;

	@Override
	public boolean responsePageLoad( DeveloperCmsAtom developerCmsAtom ) {
		super.responsePageLoad(developerCmsAtom);
		EcommerceUtil.getEcommerceUtil().addCheckoutPageEntry( CheckoutPageEntry.BILLING );
		Customer customer = JSFUtil.getBeanFromScope(Customer.class);

		if( !EcommerceUtil.getEcommerceUtil().checkCheckoutAccess( customer, "checkout process" ) ) {
			return false;
		}
		return true;
	}
	
	// Allows Overridding
	public boolean isShowingUseDefaultAddress() {
		return true;
	}
	
	// Allows Overridding
	public boolean isShowingUseShippingAddress() {
		return true;
	}

	public boolean isValidationRequired() {
		return  BackingPage.validationRequired("checkoutBtn") ||
				BackingPage.validationRequired("transactionFrontEndMenuStep");
	}

	public void useDefaultBillingAddress() {
		Transaction transaction = (Transaction)JSFUtil.getBeanFromScope(Transaction.class);
		transaction.getBillingAddress().copy( transaction.getEcommerceShoppingCart().getCustomer().determineBillingAddress() );
		transaction.getBillingAddress().setContactFirstName(transaction.getCustomer().getSubscriber().getFirstName());
		transaction.getBillingAddress().setContactSurname(transaction.getCustomer().getSubscriber().getSurname());
	}

	public void clearBillingAddress() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		transaction.getBillingAddress().copy( new Address() );
	}

	public String goToPayment() {
		Customer customer = JSFUtil.getBeanFromScope(Customer.class);
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		if( transaction.isDeliveryAddressTheSame() ) {
			transaction.getBillingAddress().copy(transaction.getShippingAddress());
		}

		if( CommonUtil.isNullOrEmpty( customer.determineBillingAddress().getLine1() ) ||
				EcommerceConfiguration.getEcommerceSettingsStatic().isUpdatingCustomerAddressesFromTransaction() ) {
			customer.determineBillingAddress().copy( transaction.getBillingAddress() );
			customer.determineBillingAddress().saveDetails();
		}

		if (transaction.getBillingAddress() != null &&
			transaction.getBillingAddress().getCity() != null &&
			!transaction.getBillingAddress().getCity().equals("") &&
			!CommonUtil.validatePositiveInteger(transaction.getBillingAddress().getCity())) {
			if( customer.getSubscriber().getFirstName() == null ) {
				customer.getSubscriber().setFirstName( transaction.getBillingAddress().getContactFirstName() );
				customer.getSubscriber().setSurname( transaction.getBillingAddress().getContactSurname() );
			}

			if( customer.getBillingAddress().getLine1() == null ||
					EcommerceConfiguration.getEcommerceSettingsStatic().isUpdatingCustomerAddressesFromTransaction()) {
				customer.getBillingAddress().copy( transaction.getBillingAddress() );
				customer.getBillingAddress().saveDetails();
			}
			setComingFromPaymentPage(false);
			EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutPaymentCpr();
		} else {
			JSFUtil.addMessageForError("You must enter a city. City must include at least one non-numeric character.");
		}
		return null;
	}

	public String getPreviousBtnText() {
		return ApplicationUtil.getAplosContextListener().translateByKey( EcommerceBundleKey.GO_TO_SHIPPING );
	}

	public String getNextBtnText() {
		return "Go to payment";
	}

	public String goToShipping() {
		EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutShippingAddressCpr();
		return null;
	}
	
	public boolean isShowingGoToShippingAddress() {
		return !isComingFromPaymentPage();
	}

	public void setComingFromPaymentPage(boolean comingFromPaymentPage) {
		this.comingFromPaymentPage = comingFromPaymentPage;
	}

	public boolean isComingFromPaymentPage() {
		return comingFromPaymentPage;
	}

	/**
	 * @deprecated please use the aploscc:address component in place of address fields
	 */
	@Deprecated
	public SelectItem[] getUsStateSelectItems() {
		return EcommerceUtil.getEcommerceUtil().getUsStateSelectItems();
	}

	/**
	 * @deprecated please use the aploscc:address component in place of address fields
	 */
	@Deprecated
	public SelectItem[] getCanadianStateSelectItems() {
		return EcommerceUtil.getEcommerceUtil().getCanadianStateSelectItems();
	}

	/**
	 * @deprecated please use the aploscc:address component in place of address fields
	 */
	@Deprecated
	public boolean isShowingUsStates() {
		Transaction transaction = JSFUtil.getBeanFromScope( Transaction.class );
		return EcommerceUtil.getEcommerceUtil().isShowingUsStates(transaction.getBillingAddress());
	}

	/**
	 * @deprecated please use the aploscc:address component in place of address fields
	 */
	@Deprecated
	public boolean isShowingCanadianStates() {
		Transaction transaction = JSFUtil.getBeanFromScope( Transaction.class );
		return EcommerceUtil.getEcommerceUtil().isShowingCanadianStates(transaction.getBillingAddress());
	}

	/**
	 * @deprecated please use the aploscc:address component in place of address fields
	 */
	@Deprecated
	public SelectItem[] getCountrySelectItems() {
		return ApplicationUtil.getAplosModuleFilterer().getCountrySelectItems();
	}
}
