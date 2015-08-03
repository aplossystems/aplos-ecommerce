package com.aplos.ecommerce.module;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import com.aplos.cms.enums.UnconfigurableAtomEnum;
import com.aplos.cms.module.CmsConfiguration;
import com.aplos.common.ScheduledJob;
import com.aplos.common.annotations.persistence.Cascade;
import com.aplos.common.annotations.persistence.CascadeType;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.FetchType;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.OneToOne;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosAbstractBean;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.communication.MailRecipientFinder;
import com.aplos.common.enums.BulkMessageFinderEnum;
import com.aplos.common.enums.ScheduledJobEnum;
import com.aplos.common.enums.SslProtocolEnum;
import com.aplos.common.interfaces.BulkMessageFinderInter;
import com.aplos.common.listeners.AplosContextListener;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.module.ModuleConfiguration;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.ecommerce.beans.Company;
import com.aplos.ecommerce.beans.CompanyContact;
import com.aplos.ecommerce.beans.CompanyType;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.FriendReferral;
import com.aplos.ecommerce.beans.PaymentMethod;
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.beans.RealizedProductReturn;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.beans.couriers.CourierService;
import com.aplos.ecommerce.beans.couriers.FreeShippingService;
import com.aplos.ecommerce.beans.couriers.StaticShippingService;
import com.aplos.ecommerce.beans.product.ProductColour;
import com.aplos.ecommerce.beans.product.ProductSize;
import com.aplos.ecommerce.beans.product.ProductType;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;
import com.aplos.ecommerce.enums.TransactionType;
import com.aplos.ecommerce.scheduledjobs.AbandonedOrderEmailerJob;
import com.aplos.ecommerce.scheduledjobs.RecordReportStatsJob;
import com.aplos.ecommerce.scheduledjobs.RepeatCustomIncentiveEmailer;

@Entity
public class EcommerceConfiguration extends ModuleConfiguration {
	private static final long serialVersionUID = 8307954302058891326L;
	private boolean isProductVisibleWhenOutOfStock = true;

	/**
	 * We still need the freeShippingService for postage notices
	 */
	
	@OneToOne(fetch=FetchType.LAZY)
	private CourierService freeCourierService;

	@OneToOne(fetch=FetchType.LAZY)
	private StaticShippingService notRequiredShippingService;

	@OneToOne(fetch=FetchType.LAZY)
	private StaticShippingService customShippingService;

	@OneToOne(fetch=FetchType.LAZY)
	private StaticShippingService customersShippingService;

	@OneToOne(fetch=FetchType.LAZY)
	private CourierService collectFromShopCourierService;
	
	public enum EcommerceScheduledJobEnum implements ScheduledJobEnum {
		ABANDONED_ORDER_EMAILER_JOB ( AbandonedOrderEmailerJob.class ),
		RECORD_REPORT_STATS_JOB ( RecordReportStatsJob.class ),
		REPEAT_CUSTOM_INCENTIVE_EMAILER ( RepeatCustomIncentiveEmailer.class );
		Class<? extends ScheduledJob<?>> scheduledJobClass;
		boolean isActive = true;
		
		private EcommerceScheduledJobEnum( Class<? extends ScheduledJob<?>> scheduledJobClass ) {
			this.scheduledJobClass = scheduledJobClass;
		}
		
		@Override
		public Class<? extends ScheduledJob<?>> getScheduledJobClass() {
			return scheduledJobClass;
		}
		
		@Override
		public boolean isActive() {
			return isActive;
		}
		
		@Override
		public void setActive(boolean isActive) {
			this.isActive = isActive;
		}
	}
	
	public enum EcommerceUnconfigurableAtomEnum implements UnconfigurableAtomEnum {
		SINGLE_PRODUCT ( "singleProduct", "View single products", SslProtocolEnum.DONT_CHANGE ),
		SHOPPING_CART ( "cart", "Shopping cart", SslProtocolEnum.DONT_CHANGE ),
		CHECKOUT_BILLING ( "checkoutBilling", "Checkout billing address", SslProtocolEnum.FORCE_SSL ),
		CHECKOUT_CONFIRMATION ( "checkoutConfirm", "Checkout confirmation", SslProtocolEnum.FORCE_SSL ),
		CHECKOUT_SHIPPING ( "checkoutShipping", "Checkout shipping address ", SslProtocolEnum.FORCE_SSL ),
		CUSTOMER_BILLING ( "customerBilling", "Customer billing details", SslProtocolEnum.FORCE_SSL ),
		CUSTOMER_ORDERS ( "customerOrders", "Customer previous orders", SslProtocolEnum.FORCE_SSL ),
		CUSTOMER_CHANGE_PASSWORD ( "customerPassword", "Customer change password", SslProtocolEnum.FORCE_SSL ),
		CUSTOMER_CHANGE_EMAIL ( "customerChangeEmail", "Customer change email", SslProtocolEnum.FORCE_SSL ),
		CUSTOMER_SHIPPING ( "customerShipping", "Customer shipping details", SslProtocolEnum.FORCE_SSL ),
		CUSTOMER_SIGN_IN ( "customerSignIn", "Customer sign in", SslProtocolEnum.DONT_CHANGE ),
		CUSTOMER_WISH_LIST ( "customerWishList", "Customer wish list", SslProtocolEnum.FORCE_SSL ),
		CUSTOMER_DETAILS ( "customerDetails", "Customer details", SslProtocolEnum.FORCE_SSL ),
		EMAIL_A_FRIEND ( "emailAFriend", "Email a friend", SslProtocolEnum.DONT_CHANGE ),
		NEWSLETTER_SIGN_UP ( "newsletterSignup", "Newsletter sign up", SslProtocolEnum.DONT_CHANGE ),
		CHECKOUT_SIGN_UP ( "newCustomerSignUp", "Checkout sign up", SslProtocolEnum.FORCE_SSL ),
		CHECKOUT_SIGN_IN_OR_SIGN_UP ( "checkoutSignInOrSignUp", "Checkout sign in or sign up", SslProtocolEnum.FORCE_SSL ),
		CHECKOUT_PAYMENT_SUCCESSFUL ( "paymentSuccess", "Checkout payment success", SslProtocolEnum.FORCE_SSL ),
		CHECKOUT_WIZARD_BUTTONS ( "checkoutWizard", "Checkout wizard buttons", SslProtocolEnum.DONT_CHANGE ),
		REQUEST_CATALOGUE ("requestCatalogue", "Request Catalogue", SslProtocolEnum.DONT_CHANGE ),
		NEWXTPREVIOUS_PRODUCT ("nextPreviousProduct", "Next/Previous Product", SslProtocolEnum.DONT_CHANGE),
		CUSTOMER_FORUM ( "customerForum", "Customer Forum", SslProtocolEnum.DONT_CHANGE ), 	// I've called this customer forum in case
																// we want forum capabilities in cms later
																// in which case customer forum could extend
																// forum. (we are limited by needing customer)
		ADDED_TO_BAG_POPUP ( "addedToBagPopup", "Added To Bag Popup", SslProtocolEnum.DONT_CHANGE ),
		OFFSITE_PAYPAL_FORM ( "offsitePayPal", "Offsite PayPal form", SslProtocolEnum.FORCE_SSL ),
		PAYPAL_IPN_LISTENER ( "payPalIpnListener", "PayPal IPN listener", SslProtocolEnum.FORCE_SSL );

		String cmsAtomIdStr;
		String cmsAtomName;
		SslProtocolEnum sslProtocolEnum;
		private boolean isActive = true;

		private EcommerceUnconfigurableAtomEnum( String cmsAtomIdStr, String cmsAtomName, SslProtocolEnum sslProtocolEnum ) {
			this.cmsAtomIdStr = cmsAtomIdStr;
			this.cmsAtomName = cmsAtomName;
			this.sslProtocolEnum = sslProtocolEnum;
		}

		@Override
		public String getCmsAtomIdStr() {
			return cmsAtomIdStr;
		}

		@Override
		public String getCmsAtomName() {
			return cmsAtomName;
		}
		
		@Override
		public SslProtocolEnum getSslProtocolEnum() {
			return sslProtocolEnum;
		}

		public boolean isActive() {
			return isActive;
		}

		public void setActive(boolean isActive) {
			this.isActive = isActive;
		}
	}

	public enum EcommerceBulkMessageFinderEnum implements BulkMessageFinderEnum {
		CUSTOMER ( Customer.class, "Customer" ),
		TRANSACTION ( Transaction.class, "Transaction" ),
		FRIEND_REFERRAL ( FriendReferral.class, "Friend Referral" ),
		REALIZED_PRODUCT_RETURN ( RealizedProductReturn.class, "Realized Product Return" );
		
		Class<? extends BulkMessageFinderInter> bulkMessageFinderClass; 
		String bulkMessageFinderName;
		boolean isActive = true;
		
		private EcommerceBulkMessageFinderEnum( Class<? extends BulkMessageFinderInter> bulkMessageFinderClass, String bulkMessageFinderName ) {
			this.bulkMessageFinderClass = bulkMessageFinderClass;
			this.bulkMessageFinderName = bulkMessageFinderName;
		}
		
		@Override
		public Class<? extends BulkMessageFinderInter> getBulkMessageFinderClass() {
			return bulkMessageFinderClass;
		}
		
		@Override
		public String getBulkMessageFinderName() {
			return bulkMessageFinderName;
		}

		@Override
		public boolean isActive() {
			return isActive;
		}

		@Override
		public void setActive(boolean isActive) {
			this.isActive = isActive;
		}
	}

	private String cartDisplayName = "cart"; // eg cart / shopping bag / basket

	@OneToOne(fetch=FetchType.LAZY)
	private PaymentMethod creditCardPaymentMethod;

	@OneToOne(fetch=FetchType.LAZY)
	private PaymentMethod bankTransferPaymentMethod;

	@OneToOne(fetch=FetchType.LAZY)
	private PaymentMethod chequePaymentMethod;

	@OneToOne(fetch=FetchType.LAZY)
	private PaymentMethod accountPaymentMethod;

	@OneToOne(fetch=FetchType.LAZY)
	private PaymentMethod defaultFrontendPaymentMethod;

	@OneToOne(fetch=FetchType.LAZY)
	private PaymentMethod notRequiredPaymentMethod;

	@OneToOne(fetch=FetchType.LAZY)
	private PaymentMethod debitCardPaymentMethod;

	@OneToOne(fetch=FetchType.LAZY)
	private PaymentMethod payPalPaymentMethod;

	@OneToOne(fetch=FetchType.LAZY)
	private PaymentMethod cashPaymentMethod;

	@OneToOne(fetch=FetchType.LAZY)
	private MailRecipientFinder companyContactMailRecipientFinder;
	@OneToOne(fetch=FetchType.LAZY)
	private MailRecipientFinder customerMailRecipientFinder;

	@OneToOne(fetch=FetchType.LAZY)
	private RealizedProduct adminChargeRealizedProduct;

	@OneToOne(fetch=FetchType.LAZY)
	private RealizedProduct hourlyRateRealizedProduct;
	@OneToOne(fetch=FetchType.LAZY)
	private RealizedProduct partsRealizedProduct;
	@OneToOne(fetch=FetchType.LAZY)
	private ProductType giftVoucherProductType;
	@OneToOne(fetch=FetchType.LAZY)
	private ProductColour standardProductColour; //standard colour/size can be treated as 'not applicable'
	@OneToOne(fetch=FetchType.LAZY)
	private ProductSize standardProductSize;

	@ManyToOne(fetch=FetchType.LAZY)
	@Cascade({CascadeType.ALL})
	private EcommerceSettings ecommerceSettings;

	@ManyToOne(fetch=FetchType.LAZY)
	private EcommerceCmsPageRevisions ecommerceCmsPageRevisions;
	
	public EcommerceConfiguration() {
	}
	
	@Override
	public <T> T initialiseNewBean() {
		super.initialiseNewBean();
		setEcommerceSettings(new EcommerceSettings());

		setNotRequiredShippingService(new StaticShippingService());
		getNotRequiredShippingService().setName( "Not required" );
		getNotRequiredShippingService().setDeletable(false);
		getNotRequiredShippingService().setCharge( new BigDecimal( 0 ) );
		getNotRequiredShippingService().saveDetails();

		setCustomShippingService(new StaticShippingService());
		getCustomShippingService().setName( "Custom postage" );
		getCustomShippingService().setDeletable(false);
		getCustomShippingService().setCharge( new BigDecimal( 0 ) );
		getCustomShippingService().saveDetails();

		setCustomersShippingService(new StaticShippingService());
		getCustomersShippingService().setName( "Customers shipping service" );
		getCustomersShippingService().setCharge( new BigDecimal( 0 ) );
		getCustomersShippingService().saveDetails();

		StaticShippingService staticShippingService = new StaticShippingService();
		staticShippingService.setCharge( new BigDecimal( 0 ) );
		staticShippingService.setName( "" );
		staticShippingService.saveDetails();
		setCollectFromShopCourierService(new CourierService());
		getCollectFromShopCourierService().setName( "Customer to collect" );
		getCollectFromShopCourierService().setShippingServiceClass( StaticShippingService.class );
		getCollectFromShopCourierService().getShippingServices().add( staticShippingService );
		getCollectFromShopCourierService().saveDetails();

		FreeShippingService freeShippingService = new FreeShippingService();
		freeShippingService.setForAllCountries( true );
		freeShippingService.setName( "" );
		freeShippingService.saveDetails();
		setFreeCourierService(new CourierService());
		getFreeCourierService().setName( "Free postage" );
		getFreeCourierService().setShippingServiceClass( FreeShippingService.class );
		getFreeCourierService().getShippingServices().add( freeShippingService );
		getFreeCourierService().saveDetails();

		setEcommerceCmsPageRevisions( new EcommerceCmsPageRevisions() );
		getEcommerceCmsPageRevisions().saveDetails();

		setCreditCardPaymentMethod(new PaymentMethod());
		getCreditCardPaymentMethod().setName( "Credit card" );
		getCreditCardPaymentMethod().saveDetails();

		PaymentMethod bankTransferPaymentMethod = new PaymentMethod();
		bankTransferPaymentMethod.setName( "Bank Transfer" );
		bankTransferPaymentMethod.saveDetails();

		PaymentMethod chequePaymentMethod = new PaymentMethod();
		chequePaymentMethod.setName( "Cheque" );
		chequePaymentMethod.saveDetails();

		PaymentMethod loanPaymentMethod = new PaymentMethod();
		loanPaymentMethod.setName( "Loan" );
		loanPaymentMethod.setVisibleFrontend( false );
		loanPaymentMethod.saveDetails();

		PaymentMethod creditNotePaymentMethod = new PaymentMethod();
		creditNotePaymentMethod.setName( "Credit note" );
		loanPaymentMethod.setVisibleFrontend( false );
		creditNotePaymentMethod.saveDetails();

		setAccountPaymentMethod(new PaymentMethod());
		getAccountPaymentMethod().setName( "On Account" );
		getAccountPaymentMethod().saveDetails();

		setPayPalPaymentMethod(new PaymentMethod());
		getPayPalPaymentMethod().setName( "PayPal" );
		getPayPalPaymentMethod().saveDetails();

		setDebitCardPaymentMethod(new PaymentMethod());
		getDebitCardPaymentMethod().setName( "Debit card" );
		getDebitCardPaymentMethod().saveDetails();

		setNotRequiredPaymentMethod(new PaymentMethod());
		getNotRequiredPaymentMethod().setName( "Not Required" );
		getNotRequiredPaymentMethod().saveDetails();
		getNotRequiredPaymentMethod().setDeletable(false);

		setCashPaymentMethod(new PaymentMethod());
		getCashPaymentMethod().setName( "Cash" );
		getCashPaymentMethod().setVisibleFrontend( false );
		getCashPaymentMethod().saveDetails();

		setCompanyContactMailRecipientFinder(new MailRecipientFinder("Company contacts", "FROM " + AplosAbstractBean.getTableName( CompanyContact.class ) + " bean" ));
		getCompanyContactMailRecipientFinder().saveDetails();
		
		setCustomerMailRecipientFinder(new MailRecipientFinder("Customers", "FROM " + AplosAbstractBean.getTableName( Customer.class ) + " bean WHERE bean.id NOT IN ( SELECT " + CommonUtil.getBinding(CompanyContact.class) + ".id FROM " + AplosAbstractBean.getTableName( CompanyContact.class ) + " " + CommonUtil.getBinding(CompanyContact.class) +" )" ));
		getCustomerMailRecipientFinder().saveDetails();
		return (T) this;
	}

	@Override
	public int getMaximumModuleVersionMajor() {
		return 1;
	}

	@Override
	public int getMaximumModuleVersionMinor() {
		return 9;
	}

	@Override
	public int getMaximumModuleVersionPatch() {
		return 0;
	}

	@Override
	public boolean recursiveModuleConfigurationInit(
			AplosContextListener aplosContextListener, int loopCount) {
		super.recursiveModuleConfigurationInit(aplosContextListener, loopCount);

		if( loopCount == 0 ) {
			CmsConfiguration.getCmsConfiguration().registerUnconfigurableCmsAtomEnums( CmsConfiguration.getActiveUnconfigurableAtomEnums( EcommerceUnconfigurableAtomEnum.values() ) );
			CommonConfiguration commonConfiguration = CommonConfiguration.getCommonConfiguration();
			commonConfiguration.registerEmailTemplateEnums( CommonConfiguration.getActiveEmailTemplateEnums( EcommerceEmailTemplateEnum.values() ) );
			commonConfiguration.registerBulkMessageFinderEnums( CommonConfiguration.getActiveBulkMessageFinderEnums( EcommerceBulkMessageFinderEnum.values() ) );
			CommonConfiguration.getCommonConfiguration().registerScheduledJobEnums( CommonConfiguration.getActiveScheduledJobEnums( EcommerceScheduledJobEnum.values() ) );
			checkMailRecipientFindersExistAndCreate();
			return true;
		} else if( loopCount == 1 ) {
			return true;
		} 

		return false;
	}

	public void checkMailRecipientFindersExistAndCreate() {
		EcommerceConfiguration saveableEcommerceConfiguration = null; 
		getMailRecipientFinders().add( getCompanyContactMailRecipientFinder() );
		getMailRecipientFinders().add( getCustomerMailRecipientFinder() );

		BeanDao companyTypeBeanDao = new BeanDao(CompanyType.class);
		List<CompanyType> companyTypes = companyTypeBeanDao.getAll();

		for ( CompanyType companyType : companyTypes ) {
			if (companyType.getMailRecipientFinder() == null) {
				if( saveableEcommerceConfiguration == null ) {
					saveableEcommerceConfiguration = this.getSaveableBean();
				}
				companyType.setMailRecipientFinder(new MailRecipientFinder(companyType.getName(), "FROM " + AplosAbstractBean.getTableName( Company.class ) + " as c WHERE c.companyType.id = " + companyType.getId() ));
				companyType.getMailRecipientFinder().saveDetails();
				companyType.saveDetails();
			}
			getMailRecipientFinders().add( companyType.getMailRecipientFinder() );
		}
		
		if( saveableEcommerceConfiguration != null ) {
			saveableEcommerceConfiguration.saveDetails();
		}
	}

	public static EcommerceSettings getEcommerceSettingsStatic() {
		return getEcommerceConfiguration().getEcommerceSettings();
	}

	@Override
	public ModuleConfiguration getModuleConfiguration() {
		return EcommerceConfiguration.getEcommerceConfiguration();
	}

	public static EcommerceConfiguration getEcommerceConfiguration() {
		return (EcommerceConfiguration) getModuleConfiguration( EcommerceConfiguration.class );
	}

	public void setProductVisibleWhenOutOfStock(boolean isProductVisibleWhenOutOfStock) {
		this.isProductVisibleWhenOutOfStock = isProductVisibleWhenOutOfStock;
	}

	public boolean isProductVisibleWhenOutOfStock() {
		return isProductVisibleWhenOutOfStock;
	}

	public void setCompanyContactMailRecipientFinder(
			MailRecipientFinder companyContactMailRecipientFinder) {
		this.companyContactMailRecipientFinder = companyContactMailRecipientFinder;
	}

	public MailRecipientFinder getCompanyContactMailRecipientFinder() {
		return companyContactMailRecipientFinder;
	}

	public void setCustomerMailRecipientFinder(
			MailRecipientFinder customerMailRecipientFinder) {
		this.customerMailRecipientFinder = customerMailRecipientFinder;
	}

	public MailRecipientFinder getCustomerMailRecipientFinder() {
		return customerMailRecipientFinder;
	}

	public void setCreditCardPaymentMethod(PaymentMethod creditCardPaymentMethod) {
		this.creditCardPaymentMethod = creditCardPaymentMethod;
	}

	public PaymentMethod getCreditCardPaymentMethod() {
		return creditCardPaymentMethod;
	}

	public void setAccountPaymentMethod(PaymentMethod accountPaymentMethod) {
		this.accountPaymentMethod = accountPaymentMethod;
	}

	public PaymentMethod getAccountPaymentMethod() {
		return accountPaymentMethod;
	}

	public void setHourlyRateRealizedProduct(RealizedProduct hourlyRateRealizedProduct) {
		this.hourlyRateRealizedProduct = hourlyRateRealizedProduct;
	}

	public RealizedProduct getHourlyRateRealizedProduct() {
		return hourlyRateRealizedProduct;
	}

	public void setPartsRealizedProduct(RealizedProduct partsRealizedProduct) {
		this.partsRealizedProduct = partsRealizedProduct;
	}

	public RealizedProduct getPartsRealizedProduct() {
		return partsRealizedProduct;
	}

	public void setFreeCourierService(CourierService freeCourierService) {
		this.freeCourierService = freeCourierService;
	}

	public CourierService getFreeCourierService() {
		return freeCourierService;
	}

	public void setEcommerceSettings(EcommerceSettings ecommerceSettings) {
		this.ecommerceSettings = ecommerceSettings;
	}

	public EcommerceSettings getEcommerceSettings( boolean reloadConfiguration ) {
		if( reloadConfiguration ) {
			return getEcommerceConfiguration().getEcommerceSettings();
		} else {
			return ecommerceSettings;
		}
	}

	public EcommerceSettings getEcommerceSettings() {
		return getEcommerceSettings( false );
	}

	public synchronized Long getNextMaxInvoiceNumber( Transaction transaction ) {
		try {
			String sql;
			if( TransactionType.REFUND.equals( transaction.getTransactionType() ) ) {
				sql = "SELECT MAX(invoiceNumber) FROM " + AplosBean.getTableName( Transaction.class ) + " WHERE transactionType = " + TransactionType.REFUND.ordinal();
			} else {
				sql = "SELECT MAX(invoiceNumber) FROM " + AplosBean.getTableName( Transaction.class ) + " WHERE transactionType != " + TransactionType.REFUND.ordinal();
			}
			Object tempMaxInvoiceNumber = ApplicationUtil.getFirstResult( sql )[ 0 ];
			Long maxInvoiceNumber;
			if( tempMaxInvoiceNumber == null ) {
				maxInvoiceNumber = 0l;
			} else {
				if( tempMaxInvoiceNumber instanceof BigInteger ) {
					maxInvoiceNumber = ((BigInteger) tempMaxInvoiceNumber).longValue();
				} else if( tempMaxInvoiceNumber instanceof Long ) {
					maxInvoiceNumber = (Long) tempMaxInvoiceNumber;
				} else {
					maxInvoiceNumber = ((Integer) tempMaxInvoiceNumber).longValue();
				}
			}


			return maxInvoiceNumber + 1;
		} catch( Exception ex ) {
			ApplicationUtil.getAplosContextListener().handleError( ex );
		}

		return null;
	}

	public void setNotRequiredPaymentMethod(PaymentMethod notRequiredPaymentMethod) {
		this.notRequiredPaymentMethod = notRequiredPaymentMethod;
	}

	public PaymentMethod getNotRequiredPaymentMethod() {
		return notRequiredPaymentMethod;
	}

	public boolean equalsBankTransferPaymentMethod(PaymentMethod paymentMethod) {
		if( paymentMethod != null &&
				getBankTransferPaymentMethod() != null &&
				paymentMethod.equals( getBankTransferPaymentMethod() ) ) {
			return true;
		}
		return false;
	}

	public void setBankTransferPaymentMethod(PaymentMethod bankTransferPaymentMethod) {
		this.bankTransferPaymentMethod = bankTransferPaymentMethod;
	}

	public PaymentMethod getBankTransferPaymentMethod() {
		return bankTransferPaymentMethod;
	}

	public boolean equalsChequePaymentMethod(PaymentMethod paymentMethod) {
		if( paymentMethod != null &&
				getBankTransferPaymentMethod() != null &&
				paymentMethod.equals( getChequePaymentMethod() ) ) {
			return true;
		}
		return false;
	}

	public void setChequePaymentMethod(PaymentMethod chequePaymentMethod) {
		this.chequePaymentMethod = chequePaymentMethod;
	}

	public PaymentMethod getChequePaymentMethod() {
		return chequePaymentMethod;
	}

	public void setAdminChargeRealizedProduct(RealizedProduct adminChargeRealizedProduct) {
		this.adminChargeRealizedProduct = adminChargeRealizedProduct;
	}

	public RealizedProduct getAdminChargeRealizedProduct() {
		return adminChargeRealizedProduct;
	}

	public static EcommerceCmsPageRevisions getEcommerceCprsStatic() {
		return getEcommerceConfiguration().getEcommerceCmsPageRevisions();
	}

	public void setEcommerceCmsPageRevisions(EcommerceCmsPageRevisions ecommerceCmsPageRevisions) {
		this.ecommerceCmsPageRevisions = ecommerceCmsPageRevisions;
	}

	public EcommerceCmsPageRevisions getEcommerceCmsPageRevisions( ) {
		return getEcommerceCmsPageRevisions( false );
	}

	public EcommerceCmsPageRevisions getEcommerceCmsPageRevisions( boolean reloadConfiguration ) {
		if( reloadConfiguration ) {
			return getEcommerceConfiguration().getEcommerceCmsPageRevisions();
		} else {
			return ecommerceCmsPageRevisions;
		}
	}

	public void setCartDisplayName(String cartDisplayName) {
		this.cartDisplayName = cartDisplayName;
	}

	public String getCartDisplayName() {
		return cartDisplayName;
	}

	public void setGiftVoucherProductType(ProductType giftVoucherProductType) {
		this.giftVoucherProductType = giftVoucherProductType;
	}

	public ProductType getGiftVoucherProductType() {
		return giftVoucherProductType;
	}

	public void setStandardProductColour(ProductColour standardProductColour) {
		this.standardProductColour = standardProductColour;
	}

	public ProductColour getStandardProductColour() {
		return standardProductColour;
	}

	public void setDefaultFrontendPaymentMethod(
			PaymentMethod defaultFrontendPaymentMethod) {
		this.defaultFrontendPaymentMethod = defaultFrontendPaymentMethod;
	}

	public PaymentMethod getDefaultFrontendPaymentMethod() {
		return defaultFrontendPaymentMethod;
	}

	public void setDebitCardPaymentMethod(PaymentMethod debitCardPaymentMethod) {
		this.debitCardPaymentMethod = debitCardPaymentMethod;
	}

	public PaymentMethod getDebitCardPaymentMethod() {
		return debitCardPaymentMethod;
	}

	public void setPayPalPaymentMethod(PaymentMethod payPalPaymentMethod) {
		this.payPalPaymentMethod = payPalPaymentMethod;
	}

	public PaymentMethod getPayPalPaymentMethod() {
		return payPalPaymentMethod;
	}

	public void setCashPaymentMethod(PaymentMethod cashPaymentMethod) {
		this.cashPaymentMethod = cashPaymentMethod;
	}

	public PaymentMethod getCashPaymentMethod() {
		return cashPaymentMethod;
	}

	public void setStandardProductSize(ProductSize standardProductSize) {
		this.standardProductSize = standardProductSize;
	}

	public ProductSize getStandardProductSize() {
		return standardProductSize;
	}

	public StaticShippingService getNotRequiredShippingService() {
		return notRequiredShippingService;
	}

	public void setNotRequiredShippingService(StaticShippingService notRequiredShippingService) {
		this.notRequiredShippingService = notRequiredShippingService;
	}

	public StaticShippingService getCustomShippingService() {
		return customShippingService;
	}

	public void setCustomShippingService(StaticShippingService customShippingService) {
		this.customShippingService = customShippingService;
	}

	public StaticShippingService getCustomersShippingService() {
		return customersShippingService;
	}

	public void setCustomersShippingService(StaticShippingService customersShippingService) {
		this.customersShippingService = customersShippingService;
	}

	public CourierService getCollectFromShopCourierService() {
		return collectFromShopCourierService;
	}

	public void setCollectFromShopCourierService(
			CourierService collectFromShopCourierService) {
		this.collectFromShopCourierService = collectFromShopCourierService;
	}

}
