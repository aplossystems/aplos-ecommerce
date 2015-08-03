package com.aplos.ecommerce.backingpage.customer;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.backingpage.OkBtnListener;
import com.aplos.common.backingpage.SaveBtnListener;
import com.aplos.common.beans.Subscriber;
import com.aplos.common.beans.Website;
import com.aplos.common.enums.CommonBundleKey;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.backingpage.realizedProductReturn.RealizedProductReturnEditPage;
import com.aplos.ecommerce.backingpage.transaction.TransactionEditPage;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.RealizedProductReturn;
import com.aplos.ecommerce.beans.Transaction;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=Customer.class)
public class CustomerEditPage extends EditPage {
	private static final long serialVersionUID = 525235112936861266L;

	public CustomerEditPage() {
		getEditPageConfig().setApplyBtnActionListener(new SaveBtnListener( this ) {

			private static final long serialVersionUID = -2557613305473837242L;

			@Override
			public void actionPerformed(boolean redirect) {
				saveCustomer( (Customer) getBackingPage().resolveAssociatedBean() );
			}
		});
		getEditPageConfig().setOkBtnActionListener(new OkBtnListener( this ) {

			private static final long serialVersionUID = 1708106457065567926L;

			@Override
			public void actionPerformed(boolean redirect) {
				if( saveCustomer( (Customer) getBackingPage().resolveAssociatedBean() ) ) {
					super.actionPerformed(redirect);
				}
			}
		});
	}

	// This method should be used to get the customer as this can be overidden by classes
	// like CompanyContactEditPage
	public Customer getCustomerFromSession() {
		return JSFUtil.getBeanFromScope( Customer.class );
	}

	public boolean isValidationRequired() {
		return validationRequired( "editPageOkBtn" ) || validationRequired( "newTransactionBtn" );
	}

	public String createReturnAndRedirect( Customer customer ) {
		RealizedProductReturn realizedProductReturn = RealizedProductReturnEditPage.createNewReturn(customer);
		realizedProductReturn.updateReturnAddress(customer);
		JSFUtil.redirect(Website.getCurrentWebsiteFromTabSession().getPackageName(), RealizedProductReturnEditPage.class);
		return null;
	}

	public void createNewTransaction(Customer customer) {
		if( saveCustomer( customer ) ) {
			Transaction transaction = Transaction.createNewTransaction(customer, true);
			transaction.addToScope();
			JSFUtil.redirect(TransactionEditPage.class);
		}
	}

	public boolean saveCustomer( Customer customer ) {
		return saveCustomerStatic( customer );
	}

	public static boolean saveCustomerStatic( Customer customer ) {
		Subscriber subscriber = customer.getSubscriber();
		if (CommonUtil.validateEmailAddressFormat(subscriber.getEmailAddress())) {
			if( ApplicationUtil.checkUniqueValue( subscriber, "emailAddress" ) ) {
				customer.saveDetails();
				JSFUtil.addMessage(ApplicationUtil.getAplosContextListener().translateByKey( CommonBundleKey.SAVED_SUCCESSFULLY ),FacesMessage.SEVERITY_INFO);
				return true;
			} else {
				Customer loadedCustomer = (Customer) new BeanDao( Customer.class ).addWhereCriteria( "bean.subscriber.emailAddress = '" + subscriber.getEmailAddress() + "' and bean.id != " + customer.getId() ).getFirstBeanResult();
				if( loadedCustomer == null ) {
					if( customer.isNew() ) {
						customer.setSubscriber( (Subscriber) ApplicationUtil.loadBeanByUniqueValue(subscriber, "emailAddress" ) );
						JSFUtil.addMessage( "Existing subscriber transfered to customer" );
						customer.saveDetails();
						JSFUtil.addMessage(ApplicationUtil.getAplosContextListener().translateByKey( CommonBundleKey.SAVED_SUCCESSFULLY ),FacesMessage.SEVERITY_INFO);
						return true;
					} else {
						BeanDao subscriberDao = (BeanDao) new BeanDao( Subscriber.class ).addWhereCriteria( "bean.emailAddress = :emailAddress" );
						subscriberDao.setNamedParameter( "emailAddress", customer.getSubscriber().getEmailAddress() );
						Subscriber duplicateSubscriber = subscriberDao.getFirstBeanResult();
						JSFUtil.addMessage( "The subscriber " + duplicateSubscriber.getDisplayName() + " already has this email address please update either the customers email address or the subscribers." );
						return false;
					}
				} else {
					JSFUtil.addMessage( "Email address already exists for a customer" );
					return false;
				}
			}
		} else {
			JSFUtil.addMessage( "Please re-enter email address in a valid format.",FacesMessage.SEVERITY_ERROR);
			return false;
		}
	}

	public void setUsingBillingAddressForShipping(
			boolean isUsingBillingAddressForShipping) {
		Customer customer = getCustomerFromSession();
		if( isUsingBillingAddressForShipping ) {
			customer.setDuplicateAddresses( Customer.DuplicateAddresses.BILLING_ADDRESS_FOR_SHIPPING );
		} else {
			customer.setDuplicateAddresses( Customer.DuplicateAddresses.NONE );
		}
	}

	public boolean isUsingBillingAddressForShipping() {
		return getCustomerFromSession().getDuplicateAddresses().equals( Customer.DuplicateAddresses.BILLING_ADDRESS_FOR_SHIPPING );
	}

	public void setUsingShippingAddressForBilling(
			boolean isUsingShippingAddressForBilling) {
		Customer customer = getCustomerFromSession();
		if( isUsingShippingAddressForBilling ) {
			customer.setDuplicateAddresses( Customer.DuplicateAddresses.SHIPPING_ADDRESS_FOR_BILLING );
		} else {
			customer.setDuplicateAddresses( Customer.DuplicateAddresses.NONE );
		}
	}

	public boolean isUsingShippingAddressForBilling() {
		
		return getCustomerFromSession().getDuplicateAddresses().equals( Customer.DuplicateAddresses.SHIPPING_ADDRESS_FOR_BILLING );
	}



}