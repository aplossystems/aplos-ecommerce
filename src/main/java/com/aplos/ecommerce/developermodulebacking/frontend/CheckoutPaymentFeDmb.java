package com.aplos.ecommerce.developermodulebacking.frontend;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.cms.module.CmsModule;
import com.aplos.common.AplosUrl;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.BackingPage;
import com.aplos.common.beans.Address;
import com.aplos.common.beans.AplosAbstractBean;
import com.aplos.common.beans.CreditCardDetails;
import com.aplos.common.beans.Currency;
import com.aplos.common.beans.ShoppingCart;
import com.aplos.common.enums.CartAbandonmentIssue;
import com.aplos.common.enums.CheckoutPageEntry;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.core.interfaces.CreditCardEntryListener;
import com.aplos.ecommerce.beans.CompanyContact;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.EcommerceShoppingCart;
import com.aplos.ecommerce.beans.PaymentMethod;
import com.aplos.ecommerce.beans.Promotion;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.beans.couriers.AvailableShippingService;
import com.aplos.ecommerce.beans.couriers.CourierService;
import com.aplos.ecommerce.beans.couriers.FreeShippingService;
import com.aplos.ecommerce.beans.couriers.StaticShippingService;
import com.aplos.ecommerce.beans.developercmsmodules.CheckoutPaymentCmsAtom;
import com.aplos.ecommerce.enums.EcommerceBundleKey;
import com.aplos.ecommerce.module.EcommerceConfiguration;
import com.aplos.ecommerce.utils.EcommerceUtil;

@ManagedBean
@ViewScoped
public class CheckoutPaymentFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = -163875007167590151L;
	private boolean backButtonTakesToCart = false;
	private boolean deliverGiftsViaEmail = false;
	private boolean deliverGiftsToBuyer = true;
	private Address giftDeliveryAddress = new Address();
	private String promotionalCode;

	private List<SelectItem> shippingSelectItems;
	private AvailableShippingService availableShippingService;

	public CheckoutPaymentFeDmb() {
		Transaction transaction = (Transaction) JSFUtil.getBeanFromScope( Transaction.class );
		if (transaction != null && transaction.getEcommerceShoppingCart() != null) {
			availableShippingService = transaction.getEcommerceShoppingCart().getAvailableShippingService();
		}
	}
	
	@Override
	public void addAssociatedBackingsToScope() {
		DeveloperModuleBacking developerModuleBacking = CmsModule.getDeveloperModuleBacking( "cartFeDmb", null );
		DeveloperCmsAtom.addModuleBackingToScope( "cartFeDmb", developerModuleBacking );
	}
	
	public boolean isShowingContinueShoppingBtn() {
		Transaction transaction = (Transaction) JSFUtil.getBeanFromScope( Transaction.class );
		return isViewingPaymentPage() && !transaction.getEcommerceShoppingCart().isShoppingCartChangesDisabled();
	}

	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		super.responsePageLoad(developerCmsAtom);
		EcommerceUtil.getEcommerceUtil().addCheckoutPageEntry(CheckoutPageEntry.PAYMENT);
		
		Transaction transaction = (Transaction) JSFUtil.getBeanFromScope( Transaction.class );
		Customer customer =  JSFUtil.getBeanFromScope(Customer.class);
		
		if ( EcommerceUtil.getEcommerceUtil().checkCheckoutAccess(customer, "the checkout process" ) ) {
			if( !EcommerceUtil.getEcommerceUtil().isValidForPaymentDetailsPage(transaction) ) {
				EcommerceUtil.getEcommerceUtil().redirectNotValidForPaymentDetailsTransaction( true );
				return false;
			}
			if( transaction.getCreditCardDetails() == null ) {
				transaction.setCreditCardDetails( new CreditCardDetails() );
			}
			transaction.removeShippingServiceIfNotApplicable( true );

			shippingSelectItems = new ArrayList<SelectItem>();
			if( !customer.isCompanyConnectionRequested() ) {
				populateShippingSelectItems( transaction );
			}
		} else {
			return false;
		}
		return true;
	}
	
	public boolean isShowingEditShippingAddress() {
		return ((CheckoutPaymentCmsAtom) getDeveloperCmsAtom()).isShowingAddressEditBtns();
	}
	
	public boolean isShowingEditBillingAddress() {
		return ((CheckoutPaymentCmsAtom) getDeveloperCmsAtom()).isShowingAddressEditBtns();
	}

	public static boolean isViewingPaymentPage() {
		StringBuffer strBuf = new StringBuffer( EcommerceConfiguration.getEcommerceCprsStatic().getCheckoutPaymentCpr().getCmsPage().getMapping() );
		strBuf.append( ".aplos" );
		strBuf.insert( 0, "/" );
		return JSFUtil.getAplosContextOriginalUrl().equals( strBuf.toString() );
	}

	public boolean getIsViewingPaymentPage() {
		return isViewingPaymentPage();
	}

	public boolean isShippingRequired() {
		boolean isShippingRequired = false;
		EcommerceShoppingCart ecommerceShoppingCart = JSFUtil.getBeanFromScope( ShoppingCart.class );
		if (ecommerceShoppingCart != null) {
			isShippingRequired = ecommerceShoppingCart.isItemsRequireShipping();
		}
		return isShippingRequired;
	}
	
	public void processOffsitePayPalPayment() {
		Transaction transaction = (Transaction) JSFUtil.getBeanFromScope( Transaction.class );
		if (EcommerceUtil.getEcommerceUtil().isValidForPaymentConfirmation( transaction, true )) {
			EcommerceConfiguration.getEcommerceCprsStatic().redirectToOffsizePayPalCpr();
		}
	}

	public void populateShippingSelectItems( Transaction transaction ) {
		BeanDao aqlBeanDao = new BeanDao(CourierService.class);
		List<CourierService> couriers = aqlBeanDao.setIsReturningActiveBeans(true).getAll();

		Currency currency = transaction.getEcommerceShoppingCart().getCurrency();
		List<AvailableShippingService> availableShippingServices = new ArrayList<AvailableShippingService>();

		if ( isShippingRequired() ) {
			for( CourierService tempCourier : couriers ) {
				availableShippingServices.addAll( tempCourier.getAvailableShippingServices(transaction, true) );
			}

			availableShippingServices = AvailableShippingService.sortByCachedCost( availableShippingServices );

			shippingSelectItems.add( new SelectItem( null, CommonConfiguration.getCommonConfiguration().getDefaultNotSelectedText() ) );
		} else {
			StaticShippingService notRequiredShippingService = EcommerceConfiguration.getEcommerceConfiguration().getNotRequiredShippingService();
			if( notRequiredShippingService != null ) {
				AvailableShippingService notRequiredAss = notRequiredShippingService.createAvailableShippingService(); 
				availableShippingServices.add( notRequiredShippingService.createAvailableShippingService() );

				if (transaction.getEcommerceShoppingCart().getAvailableShippingService() == null) {
					availableShippingService = notRequiredAss;
					transaction.getEcommerceShoppingCart().setAvailableShippingService(notRequiredAss);
					shippingServiceUpdated();
				}

			}
		}

		boolean isShowingVolumetricWeight = EcommerceConfiguration.getEcommerceSettingsStatic().isShowingVolumetricPricesFrontend();
		for ( AvailableShippingService tempService : availableShippingServices ) {
			if( tempService.getShippingService() instanceof FreeShippingService || availableShippingServices.size() == 1) {
				if( transaction.getEcommerceShoppingCart().getAvailableShippingService() == null || availableShippingServices.size() == 1) {
					availableShippingService = tempService;
					shippingServiceUpdated();
					transaction.saveDetails();
				}
			}
			
			String label = tempService.getCachedServiceName();
			if( !isShowingVolumetricWeight && tempService.getShippingService() != null && tempService.getShippingService().isUsingVolumetricWeight() ) {
				label = "TBA " + label;
			} else {
				label = currency.appendSign( tempService.getConvertedCachedCostString( currency ) ) + " " + label;
			}
			shippingSelectItems.add(new SelectItem( tempService, label ) );
		}
	}

	public void continueShopping() {
		JSFUtil.redirect(new AplosUrl("/"), true );
	}

	public boolean isCreditCardPaymentMethodSelected() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		return EcommerceConfiguration.getEcommerceConfiguration().getCreditCardPaymentMethod().equals( transaction.getPaymentMethod() );
	}

	public boolean isAccountCustomer() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		if( transaction.getCustomer() instanceof CompanyContact && ((CompanyContact) transaction.getCustomer()).getCompany().isCreditAllowed() ) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isOrderNumberRequired() {
		return isValidationRequired() && isAccountCustomer();
	}

	public List<SelectItem> getPaymentMethodSelectItems() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		List<PaymentMethod> paymentMethodList;

		if ( transaction.getEcommerceShoppingCart().getGrandTotal(true).compareTo( new BigDecimal( 0 ) ) == 0 ) {

			paymentMethodList = new ArrayList<PaymentMethod>();
			paymentMethodList.add( EcommerceConfiguration.getEcommerceConfiguration().getNotRequiredPaymentMethod() );

			return Arrays.asList( AplosAbstractBean.getSelectItemBeans( paymentMethodList, true ) );

		} else {
			BeanDao paymentMethodDao = new BeanDao( PaymentMethod.class );
			paymentMethodDao.setOrderBy( "bean.positionIdx" ).setIsReturningActiveBeans(true);
			paymentMethodDao.addWhereCriteria( "bean.isVisibleFrontend = true" );
			paymentMethodList = paymentMethodDao.getAll();
			
			boolean removeAccountPaymentMethod = true;
			if( transaction.getEcommerceShoppingCart().getCustomer() instanceof CompanyContact ) {
				if( ((CompanyContact)transaction.getEcommerceShoppingCart().getCustomer()).getCompany().isCreditAllowed() ) {
					removeAccountPaymentMethod = false;
				}
			}
			if( removeAccountPaymentMethod ) {
				paymentMethodList.remove( EcommerceConfiguration.getEcommerceConfiguration().getAccountPaymentMethod() );
			}
			paymentMethodList.remove( EcommerceConfiguration.getEcommerceConfiguration().getNotRequiredPaymentMethod() );

			List<SelectItem> selectItemList = new ArrayList<SelectItem>();
			if( EcommerceConfiguration.getEcommerceConfiguration().getDefaultFrontendPaymentMethod() == null && paymentMethodList.size() > 1 ) {
				selectItemList.add( new SelectItem( null, CommonConfiguration.getCommonConfiguration().getDefaultNotSelectedText() ) );
			}

			for( PaymentMethod tempPaymentMethod : paymentMethodList ) {
				selectItemList.add( new SelectItem( tempPaymentMethod, tempPaymentMethod.getDisplayName() ) );
			}

			if (selectItemList.size() == 1) {
				transaction.setPaymentMethod((PaymentMethod) selectItemList.get(0).getValue());
			}

			return selectItemList;
		}
	}

	public void shippingServiceUpdated() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		if( availableShippingService == null || !availableShippingService.equals( transaction.getEcommerceShoppingCart().getAvailableShippingService() ) ) {
			transaction.getEcommerceShoppingCart().setAvailableShippingService( availableShippingService );
			transaction.shippingServiceUpdated();
		}
	}

	public String getPreviousBtnText() {
		return ApplicationUtil.getAplosContextListener().translateByKey( EcommerceBundleKey.GO_TO_BILLING ); // "Go to invoice address";
	}

	public String getNextBtnText() {
		return "Go to confirmation";
	}

	public List<SelectItem> getShippingSelectItems() {
		return shippingSelectItems;
	}

	public boolean isValidationRequired() {
		return  BackingPage.validationRequired("continueBtn") ||
				BackingPage.validationRequired("transactionFrontEndMenuStep") ||
				BackingPage.validationRequired("confirmBtn") ||
				BackingPage.validationRequired("payPalBtn") ||
				BackingPage.validationRequired("offsitePayPalBtn");
	}

	public boolean isPaymentMethodValidationRequired() {
		return  BackingPage.validationRequired("continueBtn") ||
				BackingPage.validationRequired("transactionFrontEndMenuStep") ||
				BackingPage.validationRequired("confirmBtn");
	}

	public void onChange() {

	}

	public boolean isPayPalAvailable() {
		PaymentMethod payPalPaymentMethod = EcommerceConfiguration.getEcommerceConfiguration().getPayPalPaymentMethod();
		if( payPalPaymentMethod == null ) {
			return false;
		} else {
			payPalPaymentMethod = new BeanDao( PaymentMethod.class ).setIsReturningActiveBeans( true ).get( payPalPaymentMethod.getId() );
			if( payPalPaymentMethod == null ) {
				return false;
			} else {
				return true;
			}
		}
	}

	public boolean isPayPalSelected() {
		Transaction ecommerceOrder = (Transaction) JSFUtil.getBeanFromScope( Transaction.class );
		if( ecommerceOrder.getCreditCardDetails().getCardType() != null && ecommerceOrder.getCreditCardDetails().getCardType().getSagePayTag().equals( "PAYPAL" ) ) {
			return true;
		} else {
			return false;
		}
	}

	public String goToShipping() {
		EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutShippingAddressCpr();
		CheckoutShippingFeDmb checkoutShippingFeDmb = (CheckoutShippingFeDmb) JSFUtil.getTabSessionAttribute("checkoutShippingFeDmb");
		if (checkoutShippingFeDmb != null) {
			checkoutShippingFeDmb.setContinueToPayment(true);
		}
		else {
			checkoutShippingFeDmb = new CheckoutShippingFeDmb();
			checkoutShippingFeDmb.setContinueToPayment(true);
			JSFUtil.addToTabSession("checkoutShippingFeDmb", checkoutShippingFeDmb);
		}
		return null;
	}

	public void editBillingAddress() {
		EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutBillingAddressCpr();
		CheckoutBillingFeDmb checkoutBillingFeDmb = (CheckoutBillingFeDmb) JSFUtil.getTabSessionAttribute("checkoutBillingFeDmb");
		if (checkoutBillingFeDmb != null) {
			checkoutBillingFeDmb.setComingFromPaymentPage(true);
		}
		else {
			checkoutBillingFeDmb = new CheckoutBillingFeDmb();
			checkoutBillingFeDmb.setComingFromPaymentPage(true);
			JSFUtil.addToTabSession("checkoutBillingFeDmb", checkoutBillingFeDmb);
		}
	}

	public String goToBillingOrCart() {
		if (backButtonTakesToCart && EcommerceConfiguration.getEcommerceCprsStatic().getCheckoutCartCpr() != null ) {
			EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutCartCpr();
		}
		else {
			EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutBillingAddressCpr();
		}

		return null;
	}

	private boolean handleGiftDelivery(Transaction transaction, Customer customer) {
		transaction.setGiftDeliveryAddressTheSame(deliverGiftsToBuyer);
		transaction.setGiftsSentViaEmail(deliverGiftsViaEmail);
		if (deliverGiftsToBuyer) {
			if (transaction.getGiftDeliveryAddress() != null) {
				transaction.setGiftDeliveryAddress(null);
				transaction.saveDetails();
			}
			if (deliverGiftsViaEmail) {
				if ( CommonUtil.validateEmailAddressFormat(customer.getSubscriber().getEmailAddress()) ) {
					return true;
				} else {
					JSFUtil.addMessageForError("Your email address is invalid, you will need to correct this in order to have gift vouchers delivered to yourself.");
					return false;
				}
			} else {
				return true;
			}
		} else {
			if (deliverGiftsViaEmail) {
				giftDeliveryAddress.setCountry(null);
				giftDeliveryAddress.setCountryArea(null);
				giftDeliveryAddress.setLine1(null);
				giftDeliveryAddress.setLine2(null);
				giftDeliveryAddress.setLine3(null);
				giftDeliveryAddress.setPostcode(null);
				giftDeliveryAddress.setState(null);
				if ( CommonUtil.validateEmailAddressFormat(giftDeliveryAddress.getEmailAddress()) ) {
					transaction.setGiftDeliveryAddress(giftDeliveryAddress);
					transaction.saveDetails();
					return true;
				} else {
					JSFUtil.addMessageForError("The email address for the gift voucher recipient is not valid");
					return false;
				}
			} else {
				giftDeliveryAddress.setEmailAddress(null);
				transaction.setGiftDeliveryAddress(giftDeliveryAddress);
				transaction.saveDetails();
				return true; // we will have validated empty fields from the view
			}
		}
	}

	public boolean isCardPaymentMethod(Transaction transaction) {
		return EcommerceConfiguration.getEcommerceConfiguration().getCreditCardPaymentMethod().equals( transaction.getPaymentMethod() )
			|| EcommerceConfiguration.getEcommerceConfiguration().getDebitCardPaymentMethod().equals( transaction.getPaymentMethod() );
	}

	public String goToConfirmation() {
		shippingServiceUpdated();
		Transaction transaction = (Transaction) JSFUtil.getBeanFromScope( Transaction.class );
//		if ( transaction.getEcommerceShoppingCart().getAvailableShippingService() == null ) {
//			EcommerceUtil.addAbandonmentIssueToCart( CartAbandonmentIssue.SHIPPING_METHOD );
//			JSFUtil.addMessage( "You must select a shipping method to checkout", FacesMessage.SEVERITY_WARN );
//			return null;
//		}

		Customer customer = (Customer) JSFUtil.getBeanFromScope(Customer.class);
		if (EcommerceUtil.getEcommerceUtil().isValidForPaymentConfirmation( transaction, true )) {
			shippingServiceUpdated();  // This may not have been called if the user didn't change from the default.
			if ( isCardPaymentMethod(transaction) ) {
				CreditCardDetails creditCardDetails = transaction.determineCreditCardDetails();

				if( !transaction.isUsingStoredCreditCardDetails() ) {
					if (transaction.getEcommerceShoppingCart().getCustomer().isRememberCreditCardDetails()) {
						String cvv = creditCardDetails.getCvv();
						customer.setCreditCardDetails(creditCardDetails);
						customer.saveDetails();
						//  need to re-set the transient cvv value as hibernate will remove it.
						customer.getCreditCardDetails().setCvv(cvv);
					}
				}
			}
			if (handleGiftDelivery(transaction, customer)) {
				EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutConfirmationCpr();
			}
		}
		return null;
	}
	
	public CreditCardEntryListener getCreditCardEntryListener() {
		return new CreditCardEntryListener() {
			
			@Override
			public void cvvNumericFailed() {
				EcommerceUtil.getEcommerceUtil().addAbandonmentIssueToCart(CartAbandonmentIssue.CVV_INCORRECT);
			}
			
			@Override
			public void cvvLengthFailed() {
				EcommerceUtil.getEcommerceUtil().addAbandonmentIssueToCart(CartAbandonmentIssue.CVV_INCORRECT_LENGTH);
			}
		};
	}

	public String payWithPaypal() {
		EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutConfirmationCpr();
		return null;
	}

	public void setBackButtonTakesToCart(boolean backButtonTakesToCart) {
		this.backButtonTakesToCart = backButtonTakesToCart;
	}

	public boolean isBackButtonTakesToCart() {
		return backButtonTakesToCart;
	}

	public void setAvailableShippingService(AvailableShippingService availableShippingService) {
		this.availableShippingService = availableShippingService;
	}

	public AvailableShippingService getAvailableShippingService() {
		return availableShippingService;
	}

	public void setDeliverGiftsViaEmail(boolean deliverGiftsViaEmail) {
		this.deliverGiftsViaEmail = deliverGiftsViaEmail;
	}

	public boolean isDeliverGiftsViaEmail() {
		return deliverGiftsViaEmail;
	}

	public void setDeliverGiftsToBuyer(boolean deliverGiftsToBuyer) {
		this.deliverGiftsToBuyer = deliverGiftsToBuyer;
	}

	public boolean isDeliverGiftsToBuyer() {
		return deliverGiftsToBuyer;
	}

	public void setGiftDeliveryAddress(Address giftDeliveryAddress) {
		this.giftDeliveryAddress = giftDeliveryAddress;
	}

	public Address getGiftDeliveryAddress() {
		return giftDeliveryAddress;
	}
	
	public boolean isShowingPromotions() {
		int promotionCount = new BeanDao( Promotion.class ).getCountAll();
		if( promotionCount > 0 ) {
			return true;
		} else {
			return false;
		}
	}

	public void applyPromotionalCode() {
		if (promotionalCode != null && !promotionalCode.equals("")) {
			EcommerceShoppingCart ecommerceShoppingCart = JSFUtil.getBeanFromScope(ShoppingCart.class);
			if (ecommerceShoppingCart != null) {
				BeanDao promotionDao = new BeanDao(Promotion.class);
				promotionDao.setWhereCriteria("bean.code LIKE :promotionalCode" );
				promotionDao.addWhereCriteria("bean.isCodeRequired=true");
				promotionDao.setIsReturningActiveBeans(true);
				promotionDao.setNamedParameter( "promotionalCode", promotionalCode );
				Promotion promotion = promotionDao.getFirstBeanResult();
				if (promotion != null) {
					if (promotion.isValidFor(ecommerceShoppingCart,true)) {
						ecommerceShoppingCart.applyPromotion(promotion);
						promotionalCode = null;
					} //no need for else, isValidFor will give feedback
				} else {
					EcommerceUtil.getEcommerceUtil().addAbandonmentIssueToCart( CartAbandonmentIssue.PROMOTION_NOT_RECOGNISED );
					JSFUtil.addMessageForError("The promotional code you have entered has not been recognised.");
				}
			} else {
				JSFUtil.addMessageForError("Sorry, an error has occurred and we could not add your promotion.");
			}
		} else {
			JSFUtil.addMessageForError("Please enter the promotional code you wish to add to your order.");
		}
	}

	public void setPromotionalCode(String promotionalCode) {
		this.promotionalCode = promotionalCode;
	}

	public String getPromotionalCode() {
		return promotionalCode;
	}
}
