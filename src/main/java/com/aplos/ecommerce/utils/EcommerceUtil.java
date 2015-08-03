package com.aplos.ecommerce.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;

import com.aplos.cms.utils.CmsUtil;
import com.aplos.common.AplosUrl;
import com.aplos.common.appconstants.AplosAppConstants;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.Address;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.CountryArea;
import com.aplos.common.beans.PaymentGatewayPost;
import com.aplos.common.beans.ShoppingCart;
import com.aplos.common.beans.communication.AplosEmail;
import com.aplos.common.enums.CartAbandonmentIssue;
import com.aplos.common.enums.CheckoutPageEntry;
import com.aplos.common.interfaces.OnlinePaymentOrder;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.CompanyContact;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.EcommerceShoppingCart;
import com.aplos.ecommerce.beans.EcommerceShoppingCartItem;
import com.aplos.ecommerce.beans.PaymentMethod;
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.beans.Transaction.TransactionStatus;
import com.aplos.ecommerce.beans.product.ProductBrand;
import com.aplos.ecommerce.beans.product.ProductInfo;
import com.aplos.ecommerce.beans.product.ProductType;
import com.aplos.ecommerce.developermodulebacking.frontend.CheckoutPaymentFeDmb;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;
import com.aplos.ecommerce.enums.TransactionType;
import com.aplos.ecommerce.module.EcommerceConfiguration;

@ManagedBean
@ApplicationScoped
public class EcommerceUtil {

	public static EcommerceUtil getEcommerceUtil() {
		// We resolve this as it may be an overriden class
		return (EcommerceUtil) JSFUtil.resolveVariable( EcommerceUtil.class );
	}

	public boolean isValidForFrontEndPayment( Transaction transaction ) {
		return isValidForFrontEndPayment( transaction, false );
	}
	
	public String postProcessSubject( Transaction transaction, String subject ) {
		if ( transaction.getCustomer() instanceof CompanyContact ) {
			CompanyContact companyContact = (CompanyContact) transaction.getEcommerceShoppingCart().getCustomer();
			subject = String.valueOf( companyContact.getCompany().getId() ) + " - " + subject;
		} else {
			subject = "IND" + String.valueOf( transaction.getEcommerceShoppingCart().getCustomer().getId() ) + " - " + subject;
		}
		return subject;
	}

	public final EcommerceShoppingCart getOrCreateEcommerceShoppingCart() {
		return getOrCreateEcommerceShoppingCart( true );
	}

	public EcommerceShoppingCart getOrCreateEcommerceShoppingCart( boolean isLookingForExistingCart ) {
		EcommerceShoppingCart ecommerceShoppingCart = null;
		if( isLookingForExistingCart ) {
			Transaction transaction = JSFUtil.getBeanFromScope( Transaction.class );
			if( transaction != null ) {
				ecommerceShoppingCart = transaction.getEcommerceShoppingCart();
				if( ecommerceShoppingCart != null ) {
					ecommerceShoppingCart.addToScope();
				}
			} else {
				ecommerceShoppingCart = JSFUtil.getBeanFromScope( ShoppingCart.class );
			}
		}
		if( ecommerceShoppingCart == null ) {
			ecommerceShoppingCart = EcommerceUtil.getEcommerceUtil().createShoppingCart();
			ecommerceShoppingCart.addToScope();
		}
		return ecommerceShoppingCart;
	}

	public static boolean cartContainsNonFreeShippingItems( EcommerceShoppingCart ecommerceShoppingCart ) {
		for (EcommerceShoppingCartItem item : ecommerceShoppingCart.getEcommerceShoppingCartItems()) {
			if (!item.getRealizedProduct().getProductInfo().getIsFreePostage() &&
				(item.getRealizedProduct().getProductInfo().getProduct().getProductBrand() == null ||
				!item.getRealizedProduct().getProductInfo().getProduct().getProductBrand().getIsFreePostage())) {
				return true;
			}
		}
		return false;
	}
	
	public void reevaluateAdminCharge( Transaction transaction ) {
		
	}


	public boolean isShowingDeliveryMethod() {
		return isShowingDeliveryMethod( (Transaction) JSFUtil.getBeanFromScope( Transaction.class ) );
	}

	public boolean isShowingDeliveryMethod( Transaction transaction ) {
		return !transaction.getCustomer().isCompanyConnectionRequested();
	}

	public boolean isShowingPaymentEntry() {
		return isShowingPaymentEntry( (Transaction) JSFUtil.getBeanFromScope( Transaction.class ) );
	}

	public boolean isShowingPaymentEntry( Transaction transaction ) {
		return !transaction.getCustomer().isCompanyConnectionRequested();
	}

	public boolean isShowingAddresses() {
		return isShowingAddresses( (Transaction) JSFUtil.getBeanFromScope( Transaction.class ) );
	}

	public boolean isShowingAddresses( Transaction transaction ) {
		return !transaction.getCustomer().isCompanyConnectionRequested();
	}

	public void calculateItemDiscount( EcommerceShoppingCartItem ecommerceShoppingCartItem, Customer customer ) {
		if( customer instanceof CompanyContact && ecommerceShoppingCartItem.getRealizedProduct() != null ) {
			if( ecommerceShoppingCartItem.getRealizedProduct().isDiscountAllowed() ) {
				CompanyContact companyContact = (CompanyContact) customer;
				ProductBrand productBrand = ecommerceShoppingCartItem.getRealizedProduct().getProductInfo().getProduct().getProductBrand();
				BigDecimal discountPercentage = new BigDecimal( 0 );

				if( productBrand != null && productBrand.isAllowExtraCompanyDiscount() ) {
					discountPercentage = companyContact.determineDiscountPercentage();
				}
				if( companyContact.getCompany().getCompanyType() != null ) {
					BigDecimal companyDiscount = new BigDecimal( 0 );

					companyDiscount = companyDiscount.add( companyContact.getCompany().determineCompanyTypeDiscount() );

					if( productBrand != null ) {
						companyDiscount = productBrand.getCompanyDiscountPercentage().min( companyDiscount );
						discountPercentage = discountPercentage.add( companyDiscount );
					}
				}
				ecommerceShoppingCartItem.setSingleItemDiscountPercentage( discountPercentage );
				ecommerceShoppingCartItem.calculateDiscountAmount( false, false );
			}
		}
	}

	public boolean isValidForPaymentConfirmation( Transaction transaction, boolean showMessages ) {
		boolean isValidForPayment = true;

		if( transaction.getEcommerceShoppingCart().getAvailableShippingService() == null ||
		    (transaction.getEcommerceShoppingCart().getAvailableShippingService().isUsingVolumetricWeight() && EcommerceConfiguration.getEcommerceSettingsStatic().isVolumetricWeightAuthorisationRequired()) ) {
			if( showMessages ) {
				JSFUtil.addMessage( "A suitable shipping service has not been selected" );
			}
			isValidForPayment = false;
		}

		if( !EcommerceUtil.getEcommerceUtil().isValidForPaymentDetailsPage( transaction ) ) {
			if( showMessages ) {
				JSFUtil.addMessage( "Required address fields are missing, first name, surname, city, state and country are required" );
			}
			isValidForPayment = false;
		}

		return isValidForPayment;
	}

	public boolean isValidForFrontEndPayment( Transaction transaction, boolean showMessages ) {
		boolean isValidForPayment = true;

		isValidForPayment = isValidForPaymentConfirmation( transaction, showMessages );

		if( transaction.getCustomer().isCompanyConnectionRequested() ) {
			if( showMessages ) {
				JSFUtil.addMessage( "Sorry, you cannot place this order while your company connection is pending." );
			}
			isValidForPayment = false;
		}

		if( transaction.getPaymentMethod() == null 
				|| !(transaction.getPaymentMethod().isSystemPaymentRequired()
				|| EcommerceConfiguration.getEcommerceConfiguration().getNotRequiredPaymentMethod().equals( transaction.getPaymentMethod() )) ) {
			if( showMessages ) {
				JSFUtil.addMessage( "Sorry, this payment method is not suitable for a direct payment, please contact us to place your order." );
			}
			isValidForPayment = false;
		}

		if( EcommerceConfiguration.getEcommerceSettingsStatic().isOutOfStockTxnAuthRequired() ) {
			for( EcommerceShoppingCartItem cartItem : transaction.getEcommerceShoppingCart().getEcommerceShoppingCartItems() ) {
				if( cartItem.getRealizedProduct() != null && cartItem.getRealizedProduct().calculateStockQuantity() < 1 ) {
					if( showMessages ) {
						JSFUtil.addMessage( cartItem.getItemName() + " is out of stock" );
					}
					isValidForPayment = false;
				}
			}
		}

		return isValidForPayment;
	}

	public void executePaymentCompleteRoutine( OnlinePaymentOrder onlinePaymentOrder, boolean redirectRequested, PaymentGatewayPost paymentGatewayDirectPost ) {
		JSFUtil.addToTabSession( AplosAppConstants.POST_PAYMENT_ORDER, onlinePaymentOrder );
//		HibernateUtil.startNewTransaction();
		onlinePaymentOrder.paymentComplete( redirectRequested, paymentGatewayDirectPost );
//		HibernateUtil.startNewTransaction();
		onlinePaymentOrder.sendConfirmationEmail();
		onlinePaymentOrder.reevaluateOrderObjectsSession();
	}

	public boolean redirectToCheckoutIfBasketNotEmpty(boolean suppressMessage) {
		if(isBasketEmpty()) {
			addAbandonmentIssueToCart( CartAbandonmentIssue.BASKET_EMPTY );
			if (!suppressMessage) {
				JSFUtil.addMessage( "You cannot checkout - Your shopping basket is empty" );
			}
			return false;
		} else {
			Customer customer = JSFUtil.getBeanFromScope( Customer.class );
			if( customer.isCompanyConnectionRequested() ) {
				EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutPaymentCpr();
			} else {
				Transaction transaction = (Transaction) JSFUtil.getBeanFromScope( Transaction.class );
				if( isValidForPaymentDetailsPage( transaction ) ) {
					EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutShippingAddressCpr();
				} else {	
					redirectNotValidForPaymentDetailsTransaction( true );
				}
			}
			return true;
		}
	}

	public Transaction checkOrCreateOrder( boolean populateValuesFromCustomer, boolean redirectToCheckout ) {
		// There is a similar method for this in the backend, perhaps they can be merged?
		Customer customer = JSFUtil.getBeanFromScope( Customer.class );
		
		return checkOrCreateOrder( populateValuesFromCustomer, redirectToCheckout, customer );
	}

	public Transaction checkOrCreateOrder( boolean populateValuesFromCustomer, boolean redirectToCheckout, Customer customer ) {
		// There is a similar method for this in the backend, perhaps they can be merged?
		EcommerceUtil.getEcommerceUtil().addCustomerToCart( customer );

		Transaction transaction = JSFUtil.getBeanFromScope( Transaction.class );
		if ( transaction == null || !transaction.getCustomer().getId().equals( customer.getId() ) ) {
			transaction = EcommerceUtil.getEcommerceUtil().createTransaction( customer, populateValuesFromCustomer );
		}

		if( transaction != null ) {
			if ( EcommerceUtil.getEcommerceUtil().isValidForPaymentDetailsPage( transaction ) || customer.isCompanyConnectionRequested() ) {
				CheckoutPaymentFeDmb checkoutPaymentFeDmb = (CheckoutPaymentFeDmb) JSFUtil.getTabSessionAttribute("checkoutPaymentFeDmb");
				if (checkoutPaymentFeDmb == null) {
					checkoutPaymentFeDmb = new CheckoutPaymentFeDmb();
					JSFUtil.addToTabSession("checkoutPaymentFeDmb", checkoutPaymentFeDmb);
				}
				checkoutPaymentFeDmb.setBackButtonTakesToCart(true);
				if (redirectToCheckout) {
					EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutPaymentCpr();
				}
				transaction.getFrontEndMenuWizard().setLatestStepIdx( 2 );
				transaction.getFrontEndMenuWizard().setCurrentStepIdx( 2 );
			} else {
				if (redirectToCheckout) {
					EcommerceUtil.getEcommerceUtil().redirectNotValidForPaymentDetailsTransaction( true );
				}
			}
		}
		return transaction;
	}

	public String getProductTypeMapping( ProductType productType ) {
		if( productType == null ) {
			return "";
		}

		String url = "/";
		if( EcommerceConfiguration.getEcommerceSettingsStatic().isUsingCategoriesInProductUrls() ) {
			String categoryMapping = getCategoryName();
			if (!categoryMapping.equals("")) {
				url += categoryMapping + "/";
			}
		}
		try {
			/*
			 * Use the same encoding as the database
			 */
			url += URLEncoder.encode( productType.getMappingOrId(), "Latin1" );
		} catch( UnsupportedEncodingException useEx ) {
			ApplicationUtil.getAplosContextListener().handleError( useEx );
		}
		url += ".aplos?type=category";

		return url;
	}

	public String getProductBrandMapping( ProductBrand productBrand ) {
		if( productBrand == null ) {
			return "";
		}

		String url = "/";
		if( EcommerceConfiguration.getEcommerceSettingsStatic().isUsingCategoriesInProductUrls() ) {
			String categoryMapping = getCategoryName();
			if (!categoryMapping.equals("")) {
				url += categoryMapping + "/";
			}
		}
		try {
			/*
			 * Use the same encoding as the database
			 */
			url += URLEncoder.encode( productBrand.getMappingOrId(), "Latin1" );
		} catch( UnsupportedEncodingException useEx ) {
			ApplicationUtil.getAplosContextListener().handleError( useEx );
		}
		url += ".aplos?type=category";

		return url;
	}

	public String getCategoryName() {
		String mapping = JSFUtil.getAplosContextOriginalUrl().replaceAll( ".aplos", "" );
		mapping = mapping.replaceFirst( JSFUtil.getContextPath(), "" );
		if (mapping.equals("/session-timeout") || mapping.equals("/issue-reported")) {
			mapping = "";
		} else {
			if (mapping.startsWith("/")) {
				mapping = mapping.substring(1);
			}
			while (mapping.indexOf("/") != mapping.lastIndexOf("/")) {
				mapping = mapping.substring(mapping.indexOf("/") + 1);
			}
			int lastMappingCharacterIndex = mapping.lastIndexOf('/');
			if (lastMappingCharacterIndex > 0) {
				mapping = mapping.substring(0,lastMappingCharacterIndex);
			}
		}
		try {
			/*
			 * Use the same encoding as the database
			 */
			mapping = URLEncoder.encode( mapping, "Latin1" );
		} catch( UnsupportedEncodingException useEx ) {
			ApplicationUtil.getAplosContextListener().handleError( useEx );
		}
		return mapping;
	}

	private String getInnerProductTypeMapping( RealizedProduct realizedProduct ) {
		ProductType productType = JSFUtil.getBeanFromScope( ProductType.class );
		List<ProductType> productTypeList = realizedProduct.getProductInfo().getProduct().getProductTypes();
		if( productType != null && productTypeList.contains( productType ) ) {
			return productType.getMapping();
		} else if( productTypeList.size() > 0 ) {
			return productTypeList.get( 0 ).getMapping();
		}
		return null;
	}

	public String getProductMapping(RealizedProduct realizedProduct ) {
		return getProductMapping( realizedProduct, EcommerceConfiguration.getEcommerceSettingsStatic().isUsingCategoriesInProductUrls() );
	}

	public String getProductMapping(RealizedProduct realizedProduct, boolean includeCategory) {
		String url="";

		String category = null;
		if( includeCategory ) {
			category = getCategoryName();
			if (category != null && !category.equals("/") && !category.equals("")) { //eg menswear
				url += category + "/";
			}
		}

		String type = getInnerProductTypeMapping( realizedProduct );
		if (type != null && !type.equals("/") && !type.equals("") && !type.equals(category)) { //eg trousers
			url += type + "/";
		}

		try {
			/*
			 * Use the same encoding as the database
			 */
			url += URLEncoder.encode( realizedProduct.getProductInfo().getMappingOrId(), "Latin1" );
		} catch( UnsupportedEncodingException useEx ) {
			ApplicationUtil.getAplosContextListener().handleError( useEx );
		}
		url += ".aplos?type=product&rpid=" + realizedProduct.getId();
		return url;
	}

	public boolean checkCheckoutAccess( Customer customer, String pageName ) {
		if( !checkCustomerLogin(customer, pageName) ) {
			return false;
		}
		Transaction transaction = JSFUtil.getBeanFromScope( Transaction.class );
		if( transaction == null ) {
			JSFUtil.redirect(new AplosUrl("/"), true);
			JSFUtil.addMessage("You cannot see the checkout pages as there is no current transaction, to see your previous transactions please go to your account section.");
			return false;
		}
		return true;
	}

	public boolean checkCustomerLogin( Customer customer, String pageName ) {
		if( customer==null || !customer.isLoggedIn() ) {
			EcommerceUtil.getEcommerceUtil().addAbandonmentIssueToCart( CartAbandonmentIssue.LOG_IN_NEEDED );
			JSFUtil.addMessage("You must login to access " + pageName + ".");
			EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutSignInOrSignUpCpr();
			return false;
		}
		return true;
	}

	public void sendCustomerEmailConfirmationEmail( Customer customer, boolean saveCustomer ) {
		JSFUtil.addMessage( "An email confirmation has been sent to " + customer.getSubscriber().getEmailAddress(), FacesMessage.SEVERITY_INFO );
		JSFUtil.addMessage( "Please click the link in this email to confirm your account.", FacesMessage.SEVERITY_INFO );
		customer.createTemporaryTransaction();
		customer.setEmailVerificationCode(CommonUtil.md5(new Date().toString()));
		customer.setEmailVerificationDate( new Date() );
		if( saveCustomer ) {
			customer.saveDetails();
		}
		AplosEmail.sendEmail(EcommerceEmailTemplateEnum.CUSTOMER_EMAIL_CONFIRMATION, customer);
//		CompanyDetails companyDetails = CommonConfiguration.getCommonConfiguration().getDefaultCompanyDetails();
//		try {
//			String body = customerEmailConfirmationEmail.parseContent( ApplicationUtil.getAplosContextListener(), customer );
//			AplosEmail aplosEmail = new AplosEmail( customerEmailConfirmationEmail.parseSubject( companyDetails.getCompanyName() ), body, customerEmailConfirmationEmail.getEmailType().name() );
//			aplosEmail.addToAddress( customer.getSubscriber().getEmailAddress() );
//			aplosEmail.setFromAddress( CommonConfiguration.getCommonConfiguration().getDefaultAdminUser().getEmail() );
//			aplosEmail.sendAplosEmailToQueue();
//		} catch( IOException ioex ) {
//			JSFUtil.addMessage( "Confirmation email could not be sent, please contact " + companyDetails.getCompanyName() );
//			ApplicationUtil.getAplosContextListener().handleError( ioex );
//		}
	}

	public String goToLoginPage() {
		EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutSignInOrSignUpCpr();
		return null;
	}

	public void addCustomerToCart( Customer customer ) {
		EcommerceShoppingCart shoppingCart = (EcommerceShoppingCart) JSFUtil.getBeanFromScope( ShoppingCart.class );
		if (shoppingCart==null) {
			//I noticed when browser crashed & restarted you would be taken
			//back to sign in screen however there's now no cart in session
			shoppingCart = EcommerceUtil.getEcommerceUtil().createShoppingCart();
			EcommerceUtil.getEcommerceUtil().addAbandonmentIssueToCart( CartAbandonmentIssue.NO_CART_IN_SESSION );
		}
		if( shoppingCart.isReadOnly() ) {
			shoppingCart = shoppingCart.getSaveableBean();
			shoppingCart.addToScope();
		}
		Transaction transaction = JSFUtil.getBeanFromScope( Transaction.class );
		if( transaction != null ) {
			transaction.updateVatExemption( true );
		} else {
			shoppingCart.updateVatCachedValues();
		}
		shoppingCart.setCustomer( customer );
		shoppingCart.saveDetails();
	}

	public boolean isValidForPaymentDetailsPage( Transaction transaction ) {
		if( transaction.getShippingAddress().getContactFirstName() != null &&
			transaction.getShippingAddress().getContactSurname() != null &&
			transaction.getShippingAddress().getCity() != null &&
			transaction.getShippingAddress().getState() != null &&
			transaction.getShippingAddress().getCountry() != null &&
			transaction.getBillingAddress().getContactFirstName() != null &&
			transaction.getBillingAddress().getContactSurname() != null &&
			transaction.getBillingAddress().getCity() != null &&
			transaction.getBillingAddress().getState() != null &&
			transaction.getBillingAddress().getCountry() != null && 
			!CommonUtil.validatePositiveInteger(transaction.getBillingAddress().getCity()) ) {
			return true;
		} else {
			return false;
		}
	}

	// This is overridden in at least the Teletest project
	public void redirectNotValidForPaymentDetailsTransaction( boolean isShowingMessages ) {
		EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutShippingAddressCpr();
		if( isShowingMessages ) {
			JSFUtil.addMessage( "Thank you for starting your first order, please set up your account details first, this will save you time on future purchases" );
		}
	}

	public boolean isBasketEmpty() {
		ShoppingCart shoppingCart = (ShoppingCart) JSFUtil.getBeanFromScope( ShoppingCart.class );
		if (shoppingCart == null || shoppingCart.getItems() == null || shoppingCart.getItems().size() < 1) {
			return true;
		} else {
			return false;
		}
	}

	public boolean validateMapping( ProductInfo productInfo, String mapping ) {
		if( !CmsUtil.validateMappingCharacters(mapping) ) {
			return false;
		}

		BeanDao productInfoDao = new BeanDao( ProductInfo.class ).addWhereCriteria( "bean.mapping = :mapping" );
		if( !productInfo.isNew() ) {
			productInfoDao.addWhereCriteria( "bean.id != " + productInfo.getId() );
		}
		productInfoDao.setNamedParameter("mapping", mapping);
		if( productInfoDao.getCountAll() > 0 ) {
			JSFUtil.addMessage("A product info with this mapping already exists", FacesMessage.SEVERITY_ERROR );
			return false;
		}
		return true;
	}

	public boolean isCustomerACompanyContact( Customer customer ) {
		if( customer instanceof CompanyContact ) {
			return true;
		} else {
			return false;
		}
	}

	public void addAbandonmentIssueToCart( CartAbandonmentIssue abandonmentIssue ) {
		ShoppingCart shoppingCart = (ShoppingCart) JSFUtil.getBeanFromScope( ShoppingCart.class );
		if (shoppingCart!=null) {
			shoppingCart.setAbandonmentIssue(abandonmentIssue);
			shoppingCart.saveDetails();
		}
	}

	public void addCheckoutPageEntry( CheckoutPageEntry checkoutPageEntry ) {
		ShoppingCart shoppingCart = (ShoppingCart) JSFUtil.getBeanFromScope( ShoppingCart.class );
		addCheckoutPageEntry(checkoutPageEntry,shoppingCart);
	}

	public void addCheckoutPageEntry( CheckoutPageEntry checkoutPageEntry, ShoppingCart shoppingCart ) {
		if (shoppingCart!=null) {
			shoppingCart.setLastRecordedPageEntry( checkoutPageEntry );
			if( shoppingCart.getFurthestRecordedPageEntry() == null || shoppingCart.getFurthestRecordedPageEntry().ordinal() < checkoutPageEntry.ordinal() ) {
				shoppingCart.setFurthestRecordedPageEntry( checkoutPageEntry );
			}
			shoppingCart.saveDetails();
		}
	}
	
	public EcommerceShoppingCart createShoppingCart() {
		return new EcommerceShoppingCart().<EcommerceShoppingCart>initialiseNewBean();
	}

	public Transaction createTransaction( Customer customer, boolean populateValuesFromCustomer ) {
		EcommerceShoppingCart ecommerceShoppingCart = JSFUtil.getBeanFromScope( ShoppingCart.class );
		Transaction transaction = Transaction.createNewTransaction(customer, populateValuesFromCustomer );
		transaction.setDeliveryAddressTheSame(true);

		if( ecommerceShoppingCart != null ) {
			ecommerceShoppingCart.setCustomer(customer);
			transaction.setEcommerceShoppingCart( ecommerceShoppingCart );
			transaction.getEcommerceShoppingCart().recalculateAllItemDiscounts();
			transaction.getEcommerceShoppingCart().updateVatCachedValues();
		}

		if( populateValuesFromCustomer && customer.getCreditCardDetails() != null ) {
			transaction.setUsingStoredCreditCardDetails( true );
		}

		transaction.setTransactionType(TransactionType.ECOMMERCE_ORDER);
		transaction.setTransactionStatus(TransactionStatus.INCOMPLETE);

		PaymentMethod loadedPaymentMethod = EcommerceConfiguration.getEcommerceConfiguration().getDefaultFrontendPaymentMethod();
		transaction.setPaymentMethod( loadedPaymentMethod );
		transaction.paymentMethodChanged();

		for( EcommerceShoppingCartItem cartItem : transaction.getEcommerceShoppingCart().getEcommerceShoppingCartItems() ) {
			transaction.getEcommerceShoppingCart().calculateItemDiscount( cartItem );
		}

		transaction.saveDetails();
		transaction.addToScope();
		return transaction;
	}

	//# Address country state methods

	public CountryArea getStateCountryAreaByAddress(Address address) {
		if ( address.getState() != null ) {
			BeanDao countryAreaDao = new BeanDao( CountryArea.class ).addWhereCriteria( "areaCode = :countryAreaName AND (country.id = 840 OR country.id = 124)" );
			countryAreaDao.setNamedParameter( "countryAreaName", address.getState() );
			List<CountryArea> countryAreaList = countryAreaDao.getAll();
			if( countryAreaList.size() > 0 ) {
				return countryAreaList.get( 0 );
			}
		}
		return null;
	}

	/**
	 * @deprecated please use the aploscc:address component in place of address fields
	 */
	@Deprecated
	public boolean isShowingUsStates(Address address) {
		if ( address.getCountry() != null && address.getCountry().getId().equals( 840l ) ) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @deprecated please use the aploscc:address component in place of address fields
	 */
	@Deprecated
	public boolean isShowingCanadianStates(Address address) {
		if ( address.getCountry() != null && address.getCountry().getId().equals( 124l ) ) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @deprecated please use the aploscc:address component in place of address fields
	 */
	@Deprecated
	public SelectItem[] getUsStateSelectItems() {
		List<CountryArea> usCountryAreas = new BeanDao( CountryArea.class ).addWhereCriteria( "country.id = 840" ).setIsReturningActiveBeans(true).getAll();
		return AplosBean.getSelectItemBeans( usCountryAreas );
	}

	/**
	 * @deprecated please use the aploscc:address component in place of address fields
	 */
	@Deprecated
	public SelectItem[] getCanadianStateSelectItems() {
		List<CountryArea> canadianCountryAreas = new BeanDao( CountryArea.class ).addWhereCriteria( "country.id = 124" ).setIsReturningActiveBeans(true).getAll();
		return AplosBean.getSelectItemBeans( canadianCountryAreas );
	}

}
