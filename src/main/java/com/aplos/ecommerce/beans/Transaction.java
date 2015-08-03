package com.aplos.ecommerce.beans;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.faces.application.FacesMessage;

import com.aplos.cms.module.CmsConfiguration;
import com.aplos.common.LabeledEnumInter;
import com.aplos.common.annotations.BeanScope;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.DynamicMetaValues;
import com.aplos.common.annotations.RemoveEmpty;
import com.aplos.common.annotations.persistence.Any;
import com.aplos.common.annotations.persistence.AnyMetaDef;
import com.aplos.common.annotations.persistence.Cache;
import com.aplos.common.annotations.persistence.Cascade;
import com.aplos.common.annotations.persistence.CascadeType;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.FetchType;
import com.aplos.common.annotations.persistence.JoinColumn;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.Address;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.CreditCardDetails;
import com.aplos.common.beans.Currency;
import com.aplos.common.beans.PaymentGatewayPost;
import com.aplos.common.beans.ShoppingCart;
import com.aplos.common.beans.Subscriber;
import com.aplos.common.beans.communication.AplosEmail;
import com.aplos.common.beans.communication.BasicEmailFolder;
import com.aplos.common.enums.CheckoutPageEntry;
import com.aplos.common.enums.EmailActionType;
import com.aplos.common.enums.JsfScope;
import com.aplos.common.enums.PaymentGateway;
import com.aplos.common.interfaces.BulkEmailFinder;
import com.aplos.common.interfaces.BulkEmailSource;
import com.aplos.common.interfaces.BulkSubscriberSource;
import com.aplos.common.interfaces.CardSaveOrder;
import com.aplos.common.interfaces.EmailFolder;
import com.aplos.common.interfaces.PayPalOrder;
import com.aplos.common.interfaces.PaymentSystemCartItem;
import com.aplos.common.interfaces.SagePayOrder;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.appconstants.EcommerceAppConstants;
import com.aplos.ecommerce.backingpage.transaction.TransactionBackEndMenuWizard;
import com.aplos.ecommerce.backingpage.transaction.TransactionFrontEndMenuWizard;
import com.aplos.ecommerce.beans.RealizedProductReturn.RealizedProductReturnStatus;
import com.aplos.ecommerce.beans.couriers.AvailableShippingService;
import com.aplos.ecommerce.beans.payment.CardSaveTransactionPayment;
import com.aplos.ecommerce.beans.payment.PayPalTransactionPayment;
import com.aplos.ecommerce.beans.payment.SagePayTransactionPayment;
import com.aplos.ecommerce.beans.payment.TransactionPayment;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;
import com.aplos.ecommerce.enums.TransactionType;
import com.aplos.ecommerce.module.EcommerceConfiguration;
import com.aplos.ecommerce.utils.EcommerceUtil;

@Entity
@Cache
@DynamicMetaValueKey(oldKey="TRANSACTION")
@BeanScope(scope=JsfScope.TAB_SESSION)
public class Transaction extends AplosBean implements SagePayOrder, PayPalOrder, BulkEmailFinder, BulkSubscriberSource, CardSaveOrder, EmailFolder {
	private static final long serialVersionUID = 1164899081567910882L;

//	DELIMITER //
//	CREATE PROCEDURE sp_delete_transaction(IN transaction_id INT )
//
//	BEGIN
//
//	    DELETE FROM shoppingcartitem where shoppingcart_id = (select shoppingcart_id from `transaction` where id = transaction_id);
//	    DELETE FROM address where id = (select shippingAddress_id from `transaction` where id = transaction_id);
//	    DELETE FROM address where id = (select billingAddress_id from `transaction` where id = transaction_id);
//	    DELETE FROM menuWizard where id = (select menuWizard_id from `transaction` where id = transaction_id);
//	    DELETE FROM shoppingcart where id = (select shoppingcart_id from `transaction` where id = transaction_id);
//	    DELETE FROM transaction where id = transaction_id;
//	END
//		//DELIMITER ;


	@ManyToOne(fetch=FetchType.LAZY)
	@Cascade({CascadeType.ALL})
	private EcommerceShoppingCart ecommerceShoppingCart;
	@ManyToOne
	@RemoveEmpty
	@Cascade({CascadeType.ALL})
	private Address billingAddress;
	@ManyToOne
	@RemoveEmpty
	@Cascade({CascadeType.ALL})
	private Address shippingAddress;
	@ManyToOne
	@RemoveEmpty
	@Cascade({CascadeType.ALL})
	private Address giftDeliveryAddress = null;
	private boolean isDeliveryAddressTheSame = false;
	private boolean isGiftDeliveryAddressTheSame = false;
	private boolean giftsSentViaEmail = false;
	private boolean abandonedEmailSent = false;

	@Transient
	private String paymentFailedErrorMessage;
	
	private String courierServiceName;
	//  Do not save this into the database due to data protection
	//  If the customer wants to choose to store these details then
	//  they are persisted in the customer.
	//  also you probably don't want this in the session in case it's not cleared
	//  out and is read by another user.
	@Transient
	private CreditCardDetails creditCardDetails;
	private String authorisationCode;

	/* --------------------------------- */

	private TransactionType transactionType;
	private TransactionStatus transactionStatus;
	@Column(columnDefinition="LONGTEXT")
    private String specialDeliveryInstructions;
	@Column(columnDefinition="LONGTEXT")
    private String transactionNotes;
	/** @deprecated */
	@Deprecated
	@Column(columnDefinition="LONGTEXT")
    private String transactionHistory;
	private Date deliveryRequiredByDate;
	private String trackingNumber;
	private Long invoiceNumber;
	private Date loanReturnDate;
	@ManyToOne(fetch=FetchType.LAZY)
	private RealizedProductReturn realizedProductReturn;

	@ManyToOne(fetch=FetchType.LAZY)
	private PaymentMethod paymentMethod;
	private boolean isUsingStoredCreditCardDetails = false;
	
	//@ManyToOne
	@Any(metaColumn = @Column(name = "DIRECT_POST_TYPE"), fetch=FetchType.LAZY)
    @AnyMetaDef(idType = "long", metaType = "string",
            metaValues = { /* Meta Values added in at run-time */ })
    @JoinColumn(name="DIRECT_POST_ID")
	@DynamicMetaValues
	private PaymentGatewayPost authenticatedPaymentGatewayDirectPost;

	@ManyToOne(fetch=FetchType.LAZY)
	private Transaction duplicateTransaction;
	private boolean isFullyPaid = false;

	private String customerReference;
	private Date transactionDate;
	private Date dispatchDate;
	private Date dispatchedDate;
	private Date validUntilDate;
	private Date firstInvoicedDate;

	@ManyToOne(fetch=FetchType.LAZY)
	@Cascade({CascadeType.ALL})
	private TransactionBackEndMenuWizard backEndMenuWizard = new TransactionBackEndMenuWizard();

	@ManyToOne(fetch=FetchType.LAZY)
	@Cascade({CascadeType.ALL})
	private TransactionFrontEndMenuWizard frontEndMenuWizard = new TransactionFrontEndMenuWizard();


	public enum TransactionStatus implements LabeledEnumInter {
		INCOMPLETE("Incomplete"),
		NEW("New"),
		ACKNOWLEDGED("Acknowledged"),
		PAYMENT_REQUIRED("Payment Required"),
		BOOKED_OUT("Booked out"), // Is this used anymore?
		AWAITING_DISPATCH("Awaiting dispatch"),
		DISPATCHED("Dispatched"),
		ON_LOAN("On loan"),
		RETURNED("Returned"),
		CANCELLED("Cancelled");

		private String name;

		private TransactionStatus(String name) {
			this.name = name;
		}

		@Override
		public String getLabel() {
			return name;
		}
	}
	
	@Override
	public void aplosEmailAction(EmailActionType emailActionType, AplosEmail aplosEmail) {	
	}
	
	@Override
	public String getEmailFolderSearchCriteria() {
		return "bean.id LIKE :searchStr";
	}

	/* --------------------------------- */

	public Transaction() {}
	
	@Override
	public String getBulkMessageFinderName() {
		return "All transaction customers";
	}
	
	@Override
	public Long getMessageSourceId() {
		return getId();
	}
	
	@Override
	public String getJDynamiTeValue(String variableKey, AplosEmail aplosEmail) {
		return null;
	}

	public Transaction getCopy( boolean includeUniqueFields ) {
		try {
			Transaction destinationTransaction = (Transaction) this.clone();
			destinationTransaction.setEcommerceShoppingCart(getEcommerceShoppingCart().getCopy( includeUniqueFields ));
			destinationTransaction.setBillingAddress(getBillingAddress().getCopy());
			destinationTransaction.setShippingAddress(getShippingAddress().getCopy());
			if( getGiftDeliveryAddress() != null ) {
				destinationTransaction.setGiftDeliveryAddress(getGiftDeliveryAddress().getCopy());
			}
			destinationTransaction.setBackEndMenuWizard( (TransactionBackEndMenuWizard) getBackEndMenuWizard().getCopy() );
			destinationTransaction.setFrontEndMenuWizard( (TransactionFrontEndMenuWizard) getFrontEndMenuWizard().getCopy() );
			if( !includeUniqueFields ) {
				destinationTransaction.setId( null );
			}
			return destinationTransaction;
		} catch ( CloneNotSupportedException cnsEx ) {
			ApplicationUtil.getAplosContextListener().handleError( cnsEx );
		}
		return null;
	}
	
	public void removeShippingServiceIfNotApplicable( boolean isFrontEnd ) {
		if( TransactionStatus.INCOMPLETE.equals( getTransactionStatus() ) ) {
			AvailableShippingService availableShippingService = getEcommerceShoppingCart().getAvailableShippingService(); 
			if( availableShippingService != null && availableShippingService.getShippingService() != null &&
					!availableShippingService.getShippingService().isApplicable( this, null, isFrontEnd ) ) {
				getEcommerceShoppingCart().setAvailableShippingService( null );
			}
		}
	}

	public void setTransactionAddressDetailsFromCustomer() {
		Address customerShippingAddress = getCustomer().determineShippingAddress();
		getShippingAddress().copy( customerShippingAddress );
		getShippingAddress().setContactFirstName( getCustomer().getSubscriber().getFirstName() );
		getShippingAddress().setContactSurname( getCustomer().getSubscriber().getSurname() );
		getShippingAddress().setPhone( customerShippingAddress.getPhone() );
		getShippingAddress().setMobile( customerShippingAddress.getMobile() );
		getShippingAddress().setEmailAddress( customerShippingAddress.getEmailAddress() );

		if( JSFUtil.isLocalHost() && ApplicationUtil.getAplosContextListener().isDebugMode() && PaymentGateway.CARDSAVE.equals( CommonConfiguration.getCommonConfiguration().getDefaultPaymentGateway()) ) {
			if( getCreditCardDetails() == null ) {
				setCreditCardDetails( new CreditCardDetails() );
			}
			CommonConfiguration.getCommonConfiguration().getDefaultPaymentGateway().getDirectPost().insertTestCardDetails( getBillingAddress(), getCreditCardDetails(), true );
			JSFUtil.addMessageForWarning("Test card details have been added for debugging" );
		} else {
			getBillingAddress().copy( getCustomer().determineBillingAddress() );
			if( CommonUtil.isNullOrEmpty( getBillingAddress().getContactFirstName() ) ) {
				getBillingAddress().setContactFirstName( getCustomer().getSubscriber().getFirstName() );
			}
			if( CommonUtil.isNullOrEmpty( getBillingAddress().getContactSurname() ) ) {
				getBillingAddress().setContactSurname( getCustomer().getSubscriber().getSurname() );
			}
		}

		setDeliveryAddressTheSame(false);
	}

	public boolean calculateIsTaxExempt() {
		if( getShippingAddress().getCountry() == null
				|| getShippingAddress().getCountry().getVatExemption() == null
				|| getCustomer() == null ) {
			return false;
		} else {
			return getCustomer().isVatExempt( getShippingAddress().getCountry().getVatExemption() );
		}
	}

	@Override
	public void executePaymentCompleteRoutine(boolean redirectRequested, PaymentGatewayPost paymentGatewayDirectPost ) {
		setFullyPaid( true );
		EcommerceUtil.getEcommerceUtil().executePaymentCompleteRoutine( this, redirectRequested, paymentGatewayDirectPost );
	}

//	public CreatedPrintTemplate createCreatedPrintTemplate( TransactionTemplate transactionTemplate, PrintedSentStage printedSentStage ) {
//		try {
//			CreatedPrintTemplate createdPrintTemplate = new CreatedPrintTemplate();
//			createdPrintTemplate.setTemplateClass( transactionTemplate.getClass() );
//			createdPrintTemplate.setFileDetailsOwner( EcommerceWorkingDirectory.TRANSACTION_PDFS_DIR.getAplosWorkingDirectory() );
//			createdPrintTemplate.saveDetails();
//			
//			
//			String templateContent = transactionTemplate.getTemplateContent( this, ApplicationUtil.getAplosContextListener(), JSFUtil.getContextPath(), JSFUtil.getResponse() );
//			CommonUtil.saveContentAsPdf( createdPrintTemplate, templateContent );
//			String customerIdStr;
//			if( getCustomer() instanceof CompanyContact ) {
//				customerIdStr = String.valueOf( ((CompanyContact) getCustomer()).getCompany().getId() );
//			} else {
//				customerIdStr = "IND" + String.valueOf( getCustomer().getId() );
//			}
//
//			String fileDetailsName = (customerIdStr + " - " + getId() + " - " + printedSentStage.getStageName()).trim() + " - " + FormatUtil.formatDate( new Date() ) + ".pdf";
//			createdPrintTemplate.setName( fileDetailsName );
//			createdPrintTemplate.saveDetails();
//			String path = EcommerceWorkingDirectory.TRANSACTION_PDFS_DIR.getDirectoryPath(true);
//			
//			if( firstInvoicedDate == null && (transactionTemplate instanceof DispatchInvoiceTemplate || transactionTemplate instanceof EcommerceInvoiceTemplate) ) {
//				firstInvoicedDate = new Date();
//			}
//			if( createdPrintTemplate != null ) {
//				printedSentStage.setCreatedPrintTemplate(createdPrintTemplate);
////				savePrintedSentStage(printedSentStage);
//			}
//			return createdPrintTemplate;
//		} catch (Exception e) {
//			ApplicationUtil.getAplosContextListener().handleError( e );
//			return null;
//		}
//	}

	@Override
	public BigDecimal getGrandTotal( boolean includingVat ) {
		return getEcommerceShoppingCart().getGrandTotal( includingVat );
	}

	public String getFirstLineOfNotes() {
		return CommonUtil.getFirstLine( getTransactionNotes() );
	}

	public Integer getFirstProductQty() {
		if( getEcommerceShoppingCart() != null && getEcommerceShoppingCart().getItems().size() > 0 ) {
			return getEcommerceShoppingCart().getItems().get( 0 ).getQuantity();
		} else {
			return null;
		}
	}

	public String getFirstProductItemCode() {
		if( getEcommerceShoppingCart() != null && getEcommerceShoppingCart().getItems().size() > 0 &&
				getEcommerceShoppingCart().getEcommerceShoppingCartItems().get( 0 ).getRealizedProduct() != null) {
			return getEcommerceShoppingCart().getEcommerceShoppingCartItems().get( 0 ).getRealizedProduct().determineItemCode();
		} else {
			return null;
		}
	}

	public List<AplosEmail> getSortedAplosEmailList() {
		return BasicEmailFolder.getEmailListFromFolder( this, true );
	}

	public void paymentMethodChanged() {
		EcommerceUtil.getEcommerceUtil().reevaluateAdminCharge( this );
		saveDetails();
	}

	public static Transaction createNewTransaction(Customer customer, boolean populateValuesFromCustomer ) {
		return createNewTransaction(customer, populateValuesFromCustomer, true );
	}

	public static Transaction createNewTransaction(Customer customer, boolean populateValuesFromCustomer, boolean isLookingForExistingCart ) {
		Transaction transaction = new Transaction();
		transaction.initialiseNewBean();
		EcommerceShoppingCart ecommerceShoppingCart = EcommerceUtil.getEcommerceUtil().getOrCreateEcommerceShoppingCart(isLookingForExistingCart);
		transaction.setTransactionDate(new Date());
		transaction.setEcommerceShoppingCart(ecommerceShoppingCart);
		transaction.getEcommerceShoppingCart().setCustomer(customer);

		if( populateValuesFromCustomer ) {
			transaction.setTransactionAddressDetailsFromCustomer();

			if( customer instanceof CompanyContact ) {
				transaction.setPaymentMethod( EcommerceConfiguration.getEcommerceConfiguration().getAccountPaymentMethod() );
				transaction.getShippingAddress().setCompanyName(((CompanyContact)customer).getCompany().getCompanyName());
				transaction.getBillingAddress().setCompanyName(((CompanyContact)customer).getCompany().getCompanyName());
			}
		}

		Currency currency = JSFUtil.getBeanFromScope( Currency.class );
		if( currency != null && currency.getId() != null ) {
			ecommerceShoppingCart.setCurrency( currency );
		} else {
			currency = CommonConfiguration.getCommonConfiguration().getDefaultCurrency();
			if (currency != null) {
				ecommerceShoppingCart.setCurrency( currency );
			}
		}

		transaction.updateVatExemption( false );

		transaction.setTransactionStatus( TransactionStatus.NEW );
		transaction.extendValidUntilDate( false );

		return transaction;
	}

	public void updateVatExemption( boolean updateCachedVatValues ) {
		boolean wasVatExempt = getEcommerceShoppingCart().isVatExempt();
		getEcommerceShoppingCart().setVatExempt( calculateIsTaxExempt() );
		if( wasVatExempt != getEcommerceShoppingCart().isVatExempt() ) {
			getEcommerceShoppingCart().updateVatPercentageOnItems();
		}
		if( updateCachedVatValues ) {
			getEcommerceShoppingCart().updateVatCachedValues();
		}
	}

	public void shippingServiceUpdated() {
		AvailableShippingService availableShippingService = ecommerceShoppingCart.getAvailableShippingService();
		if (availableShippingService != null) {
			if( availableShippingService.hasShippingService( EcommerceConfiguration.getEcommerceConfiguration().getCustomersShippingService() ) ) {
				Customer customer = ecommerceShoppingCart.getCustomer();
				ecommerceShoppingCart.setDeliveryCost( new BigDecimal( 0 ) );
				if (customer instanceof CompanyContact) {
					CompanyContact companyContact = (CompanyContact) customer;
					setCourierServiceName( companyContact.getCompany().getCourierReference() );
				} else {
					setCourierServiceName( "Customer Collection" );
				}
			} else if( !availableShippingService.equals( EcommerceConfiguration.getEcommerceConfiguration().getCustomShippingService() ) ) {
				setCourierServiceName( availableShippingService.getCachedServiceName() );
				ecommerceShoppingCart.updateDeliveryCost();
			}
		} else {
			ecommerceShoppingCart.setDeliveryCost( new BigDecimal( 0 ) );
		}
		getEcommerceShoppingCart().updateCachedDeliveryVatValue();
	}

	public static Transaction getBySerialNumber( Long serialNumber ) {
		BeanDao transactionDao = new BeanDao( SerialNumber.class );
		transactionDao.addWhereCriteria( "id = :serialNumber" );
		transactionDao.setIsReturningActiveBeans(true);
		transactionDao.setNamedParameter( "serialNumber", String.valueOf( serialNumber ) );
		List<SerialNumber> assignmentList = transactionDao.getAll();
		if( assignmentList.size() == 1 ) {
			if( assignmentList.get( 0 ).getSerialNumberOwner() == null ) {
				return null;
			} else {
				return assignmentList.get( 0 ).getSerialNumberOwner().getAssociatedTransaction();
			}
		} else {
			return null;
		}
	}

	public Customer getCustomer() {
		return getEcommerceShoppingCart().getCustomer();
	}

	public void extendValidUntilDate( boolean updateCachedValues ) {
		Calendar cal = new GregorianCalendar();
		cal.setTime( new Date() );

		cal.add( GregorianCalendar.DAY_OF_YEAR, 30 );
		setValidUntilDate( cal.getTime() );

		if( updateCachedValues ) {
			getEcommerceShoppingCart().updateBaseRealizedProductValues();
			getEcommerceShoppingCart().updateCachedValues( true );
		}
	}

	@Override
	public void sendPaymentFailureEmail(String error) {
		paymentFailedErrorMessage = error;
		AplosEmail aplosEmail = new AplosEmail( EcommerceEmailTemplateEnum.PAYMENT_FAILED, this, this );
		aplosEmail.sendAplosEmailToQueue();
		EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutPaymentCpr();
		
		//########################################
		//adding to tab session here to fix there being no customer after cardsave 3d auth payment failure fails.
		// we're still part of the iframe and have diff tabsessions
		//TODO: this still feels like too much of a hack
		Customer customer = JSFUtil.getBeanFromScope(Customer.class);
		if (customer == null) {
			customer = this.getCustomer();
		}
		if (customer != null) {
			customer.login();
			JSFUtil.getSessionTemp().setAttribute("cardsaveCustomerReset", customer);
		}
		JSFUtil.getSessionTemp().setAttribute("cardsaveTransactionReset", this);
		//########################################
	}

	@Override
	public void threeDRedirect() {
		CmsConfiguration.getCmsConfiguration().redirectToThreeDAuthCpr();
	}

	@Override
	public void reevaluateOrderObjectsSession() {
		reevaluateOrderObjectsSession(false);
	}

	public void reevaluateOrderObjectsSession(boolean removeCustomerRatherThanCart) {
		JSFUtil.removeFromScope( Transaction.class );
		if (removeCustomerRatherThanCart) {
			ShoppingCart cart = JSFUtil.getBeanFromScope( ShoppingCart.class );
			if (cart instanceof EcommerceShoppingCart) {
				((EcommerceShoppingCart)cart).setCustomer(null);
				((EcommerceShoppingCart)cart).recalculateAllItemDiscounts();
			}
		} else {
			EcommerceShoppingCart ecommerceShoppingCart = EcommerceUtil.getEcommerceUtil().createShoppingCart();
			ecommerceShoppingCart.addToFrontEndScope();
		}
	}

	@Override
	public List<? extends PaymentSystemCartItem> getCartItems() {
		return getEcommerceShoppingCart().getItems();
	}

	@Override
	public CreditCardDetails determineCreditCardDetails() {
		if( isUsingStoredCreditCardDetails ) {
			return getCustomer().getCreditCardDetails();
		} else {
			return getCreditCardDetails();
		}
	}

	@Override
	public void sendConfirmationEmail() {
		AplosEmail aplosEmail = null;
		if( EcommerceConfiguration.getEcommerceSettingsStatic().isBusinessToBusiness() ) {
			aplosEmail = new AplosEmail( EcommerceEmailTemplateEnum.PDF_CONFIRM_PAYMENT, this, this );
		} else {
			aplosEmail = new AplosEmail( EcommerceEmailTemplateEnum.CONFIRM_PAYMENT, this, this );
		}
		processConfirmationEmail(aplosEmail);
        aplosEmail.sendAplosEmailToQueue();

		if (EcommerceConfiguration.getEcommerceSettingsStatic().isUsingRepeatCustomEnticements()) {
			Customer customer = getEcommerceShoppingCart().getCustomer();
			if (EcommerceConfiguration.getEcommerceSettingsStatic().isRepeatCustomEnticementForNewCustomersOnly()) {
				if (customer.isNew()) {
					sendRepeatCustomEnticement(customer);
				}
			} else {
				sendRepeatCustomEnticement(customer);
			}
		}
	}
	
	public void processConfirmationEmail( AplosEmail aplosEmail ) {
        if( TransactionType.ECOMMERCE_ORDER.equals( getTransactionType() ) ) {
        	if( EcommerceConfiguration.getEcommerceSettingsStatic().isEmailAlertAdminOnFrontendOrder() ) {
        		aplosEmail.addAdminEmailToBccAddresses();
        	}
        }
	}

	private void sendRepeatCustomEnticement(Customer customer) {
		boolean validTransaction = true;
		if (EcommerceConfiguration.getEcommerceSettingsStatic().isRepeatCustomEnticementForNewCustomersOnly()) {
			validTransaction = false;
			//only valid if we can't find a previous transaction for this customer
			BeanDao transactionDao = new BeanDao( Transaction.class );
			transactionDao.addWhereCriteria( "bean.customer.id = " + customer.getId() + " AND bean.transactionStatus=" + transactionStatus.ordinal() );
			transactionDao.setGroupBy( "bean.id" );
			int count = transactionDao.getCountAll();
			if (count < 1) {
				validTransaction = true;
			}
		}
		if (validTransaction) {
			Promotion enticement = EcommerceConfiguration.getEcommerceSettingsStatic().getEnticementEmailPromotion();
			if (enticement == null) {
				//should get logged
				System.err.println("No enticement promotion is set. Exiting transaction.sendRepeatCustomEnticement.");
				return;
			} else {
				//we take an individual copy so we can be more efficient in the scheduled job
				//and so if we change the promotion, the customer doesnt get a nasty surprise
				EnticementEmailPromotion enticementEmailPromotion = new EnticementEmailPromotion();
				Calendar cal = Calendar.getInstance();
				FormatUtil.resetTime(cal);
				cal.add(Calendar.DATE, EcommerceConfiguration.getEcommerceSettingsStatic().getEnticementValidForDays());
				enticementEmailPromotion.setExpires(cal.getTime());
				enticementEmailPromotion.setOneUsePerCustomer(true);
				//we dont need to send individual ones any longer but this is for traceability and the job
				enticementEmailPromotion.setCustomer(this.getEcommerceShoppingCart().getCustomer());
				enticementEmailPromotion.setMaxUseCount(enticement.getMaxUseCount());
				enticementEmailPromotion.setName(enticement.getName());
				enticementEmailPromotion.setPercentage(enticement.getPercentage());
//				enticementEmailPromotion.setProductBrand(enticement.getProductBrand());
//				enticementEmailPromotion.setProductInfo(enticement.getProductInfo());
//				enticementEmailPromotion.setProductType(enticement.getProductType());
				
				List<PromotionConstraint> constraintList = new ArrayList<PromotionConstraint>();
				for (PromotionConstraint oldConstraint : enticement.getConstraints()) {
					constraintList.add(oldConstraint.getCopy());
				}
				enticementEmailPromotion.setConstraints(constraintList);
				
				enticementEmailPromotion.generatePromoCode();
				enticementEmailPromotion.saveDetails();
				//this wont necessarily send anything, but it will setup our objects, so that
				//it is sent by the scheduled job, when it needs to be
				enticementEmailPromotion.sendNextEmail(ApplicationUtil.getAplosContextListener());
			}
		}
	}

	@Override
	public boolean isUsingPayPal() {
		return EcommerceConfiguration.getEcommerceConfiguration().getPayPalPaymentMethod().equals( getPaymentMethod() );
	}

	public void createInvoiceNumber() {
		if( getInvoiceNumber() == null ) {
			setInvoiceNumber( EcommerceConfiguration.getEcommerceConfiguration().getNextMaxInvoiceNumber( this ) );
		}
	}

	public String getInvoiceNumberHeading() {
		if( TransactionType.REFUND.equals( getTransactionType() ) ) {
			return "Returns number";
		} else {
			return "Invoice number";
		}
	}

	@Override
	public void paymentComplete( boolean isRedirectRequested, PaymentGatewayPost paymentGatewayDirectPost ) {
		getTransactionPayment().paymentComplete( this, paymentGatewayDirectPost );
		getBackEndMenuWizard().setCurrentStepIdx( 4 );
		getBackEndMenuWizard().setLatestStepIdx( 4 );
		createInvoiceNumber();

		JSFUtil.getSessionTemp().setAttribute(EcommerceAppConstants.POST_PAYMENT_ORDER, this );
		setTransactionStatus( TransactionStatus.ACKNOWLEDGED );
		saveDetails();
		useVouchers();
		takeStoreCreditFromCustomer();
		notifyCartItemsOfPurchase(paymentGatewayDirectPost);
		createAndIssueGiftVouchers(); //this handles gift voucher purchases and friend referral payouts
		EcommerceUtil.getEcommerceUtil().addCheckoutPageEntry( CheckoutPageEntry.SUCCESS, getEcommerceShoppingCart() );
		ecommerceShoppingCart.depleteStockQuantities();
		saveDetails();

		//triggers creation of a new cart now the previous one is removed from session
		EcommerceUtil.getEcommerceUtil().getOrCreateEcommerceShoppingCart();
		if( isRedirectRequested ) {
			EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutSuccessCpr();
		}
	}

	public void takeStoreCreditFromCustomer() {
		if (getEcommerceShoppingCart().getStoreCreditAvailable() != null && getEcommerceShoppingCart().getStoreCreditAvailable().doubleValue() > 0) {
			getEcommerceShoppingCart().getCustomer().takeStoreCredit(getEcommerceShoppingCart().getStoreCreditAvailable());
			getEcommerceShoppingCart().getCustomer().saveDetails();
			//replace the customer in session so the credit updates on screen
			getEcommerceShoppingCart().getCustomer().addToScope();
			//sets a 0 if none used - should never leave the field null
			getEcommerceShoppingCart().setStoreCreditUsed(getEcommerceShoppingCart().getStoreCreditAvailable());
			getEcommerceShoppingCart().saveDetails();
		}
	}

	public void useVouchers() {
		for (GiftVoucher used : getEcommerceShoppingCart().getGiftVouchersUsed()) {
			used.setUsed(true); // catches item vouchers (monetary ones are used when redeemed)
			used.saveDetails();
		}
		if (getEcommerceShoppingCart().getPromotion() != null) {
			getEcommerceShoppingCart().getPromotion().registerUse();
			getEcommerceShoppingCart().getPromotion().saveDetails();
			//HibernateUtil.getCurrentSession().createSQLQuery("UPDATE " + AplosBean.getTableName(Promotion.class) + " SET useCount=useCount+1 WHERE id='" + getEcommerceShoppingCart().getPromotionCode() + "'").executeUpdate();
		}
	}

	public boolean isShowingCardDetails() {
		if( getCustomer().isCompanyConnectionRequested() ) {
			return false;
		}
		if( (isIncomplete() || getIsStatusNew()) && 	
			(EcommerceConfiguration.getEcommerceConfiguration().getCreditCardPaymentMethod().equals( getPaymentMethod() ) ||
				EcommerceConfiguration.getEcommerceConfiguration().getDebitCardPaymentMethod().equals( getPaymentMethod() )) ) {
			return true;
		}

		return false;
	}

	public void notifyCartItemsOfPurchase(PaymentGatewayPost paymentGatewayDirectPost) {
		getEcommerceShoppingCart().notifyCartItemsOfPurchase(paymentGatewayDirectPost);
	}

	//this handles gift voucher purchases and friend referral payouts
	public void createAndIssueGiftVouchers() {
		/*
		 * TODO This should probably be done through the notifyCartItemsOfPurchase method
		 */
		List<GiftVoucher> createdVouchers = new ArrayList<GiftVoucher>();
		for (EcommerceShoppingCartItem item : getEcommerceShoppingCart().getEcommerceShoppingCartItems()) {
			if (item.determineIsGiftItem()) {
				for (int i=0; i < item.getQuantity(); i++) {
					GiftVoucher voucher = new GiftVoucher(item);
					voucher.generateVoucherCode();
					voucher.saveDetails();
					createdVouchers.add(voucher);
				}
			}
		}
		if (giftsSentViaEmail) {
			AplosEmail aplosEmail = new AplosEmail( EcommerceEmailTemplateEnum.GIFT_VOUCHER_ISSUED, this, this );
			aplosEmail.setToAddress(getGiftDeliveryAddress().getEmailAddress());
			aplosEmail.sendAplosEmailToQueue();
		}

		//handle friend referral payouts
		BeanDao referralDao = new BeanDao(FriendReferral.class);
		referralDao.setWhereCriteria("bean.referee.id=" + getEcommerceShoppingCart().getCustomer().getId());
		referralDao.setMaxResults(1);
		FriendReferral friendReferral = referralDao.setIsReturningActiveBeans(true).getFirstBeanResult();
		if( friendReferral != null ) {
			if (friendReferral.getReferreeMinimumSpend().compareTo(getEcommerceShoppingCart().getCachedGrossTotalAmount()) <= 0) {
				//we spent enough, now make sure we haven't gone over our referral payout limit
				boolean limitValid=true;
				Calendar cal = Calendar.getInstance();
				FormatUtil.resetTime( cal );
				cal.set(Calendar.DAY_OF_MONTH, 1);
				String startOfMonthStr = FormatUtil.formatDateForDB(cal.getTime());
				cal.set(Calendar.SECOND, 59);
				cal.set(Calendar.MINUTE, 59);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.add(Calendar.MONTH, 1);
				cal.add(Calendar.DATE, -1);
				String endOfMonthStr = FormatUtil.formatDateForDB(cal.getTime());
				BeanDao countDao = new BeanDao(FriendReferral.class);
				countDao.setWhereCriteria("bean.referrer.id=" + friendReferral.getReferrer().getId());
				countDao.addWhereCriteria("(bean.dateReferralValidated != null AND bean.dateReferralValidated <= '" + startOfMonthStr + "' AND bean.dateReferralValidated >= '" + endOfMonthStr + "')");
				int calendarMonthReferralCount = countDao.setIsReturningActiveBeans(true).getCountAll();

				if (friendReferral.getReferralLimitPerCalendarMonth() > 0 && friendReferral.getReferralLimitPerCalendarMonth() <= calendarMonthReferralCount) {
					limitValid = false;
				}

				if (limitValid) {

					//create a voucher, in case we want to add store credit section to customer accounts, this is historic
					StoreCreditVoucher referralVoucher = new StoreCreditVoucher(friendReferral.getReferralPayout(), friendReferral.getReferrer(), "referral of " + friendReferral.getReferee().getFullName());
					referralVoucher.generateVoucherCode();
					referralVoucher.setUsed(true);
					referralVoucher.saveDetails();
					BigDecimal totalCredit = friendReferral.getReferralPayout();

					List<StoreCreditVoucher> vouchersIssued = new ArrayList<StoreCreditVoucher>();
					vouchersIssued.add(referralVoucher);

					//payout bonus if necessary
					if (friendReferral.getReferralBonusThreshold() > 0 &&
						friendReferral.getReferralBonusThreshold() == calendarMonthReferralCount) {
						StoreCreditVoucher bonusVoucher = new StoreCreditVoucher(friendReferral.getReferralBonus(), friendReferral.getReferrer(), friendReferral.getReferralBonusThreshold() + " referral bonus");
						bonusVoucher.generateVoucherCode();
						bonusVoucher.setUsed(true);
						bonusVoucher.saveDetails();
						totalCredit.add(friendReferral.getReferralBonus());
						vouchersIssued.add(bonusVoucher);
					}

					friendReferral.getReferrer().addStoreCredit(totalCredit);
					friendReferral.getReferrer().saveDetails();

					//send an email so the customer is aware they have been credited
					friendReferral.setCreditVouchersAwarded(vouchersIssued);
					friendReferral.saveDetails();
					AplosEmail aplosEmail = new AplosEmail( EcommerceEmailTemplateEnum.REFERRAL_PAYOUT_ISSUED, friendReferral, friendReferral );
					aplosEmail.addToAddress( friendReferral.getReferrer().getSubscriber().getEmailAddress() );
					aplosEmail.sendAplosEmailToQueue();

				}

				//mark referral used (its saves it itself)
				friendReferral.referralComplete();
			}
		}
	}

	public boolean isAlreadyPaid() {
		BeanDao paymentGatewayPostDao = new BeanDao( PaymentGatewayPost.class );
		paymentGatewayPostDao.addWhereCriteria( "bean.onlinePaymentOrder.id = " + getId() );
		paymentGatewayPostDao.addWhereCriteria( "bean.isPaid = true" );
		paymentGatewayPostDao.addWhereCriteria( "bean.onlinePaymentOrder.class = '" + getClass().getSimpleName() + "'" );
		if( paymentGatewayPostDao.getCountAll() > 0 ) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String getPurchaseDescription() {
		return "Shopping Bag";
	}

	public double getSqlTotal() {
		String ecommerceShoppingCartId = ApplicationUtil.getFirstUniqueResult("select ecommerceshoppingcart_id from ecommerceorder where id=" + getId()).toString();
		BigDecimal bigDecimal = (BigDecimal) ApplicationUtil.getFirstUniqueResult("select sum(price*quantity)" +
				" from shoppingcartitem" +
				" where shoppingcart_id="+ecommerceShoppingCartId);
		double itemsTotal = bigDecimal.doubleValue();
//		double convertedShippingCharge = getEcommerceShoppingCart().getShippingSet().getConvertedCharge(getEcommerceShoppingCart());
		return itemsTotal;//+convertedShippingCharge;
	}

	@Override
	public Currency getCurrency() {
		return ecommerceShoppingCart.getCurrency();
	}

	public EcommerceShoppingCart getEcommerceShoppingCart() {
		return ecommerceShoppingCart;
	}

	public void setEcommerceShoppingCart(EcommerceShoppingCart ecommerceShoppingCart) {
		this.ecommerceShoppingCart = ecommerceShoppingCart;
	}

	public void setBillingAddress(Address billingAddress) {
		this.billingAddress = billingAddress;
	}

	public boolean makeOnlinePaymentCall( boolean isPaymentMadeNow, BigDecimal partPayment, boolean isFrontEnd ) {
		if( !isAlreadyPaid() ) {
			Transaction loadedTransaction = this;

			TransactionPayment transactionPayment = getTransactionPayment();
			if( transactionPayment != null ) {
				PaymentGatewayPost paymentGatewayDirectPost = transactionPayment.sendOnlinePaymentRequest(loadedTransaction, isPaymentMadeNow, partPayment, isFrontEnd);
				paymentGatewayDirectPost.saveDetails();
				return paymentGatewayDirectPost.isProcessed();
			}
		} else {
			JSFUtil.addMessage( "Payment has already been made on this order", FacesMessage.SEVERITY_WARN );
		}
		return false;
	}
	
	public TransactionPayment getTransactionPayment() {
		TransactionPayment transactionPayment = null;
		if( PaymentGateway.SAGEPAY.equals( CommonConfiguration.getCommonConfiguration().getDefaultPaymentGateway() ) ) {
			transactionPayment = new SagePayTransactionPayment();
		} else if ( PaymentGateway.CARDSAVE.equals( CommonConfiguration.getCommonConfiguration().getDefaultPaymentGateway() ) ) {
			transactionPayment = new CardSaveTransactionPayment();
		} else if ( PaymentGateway.PAYPAL.equals( CommonConfiguration.getCommonConfiguration().getDefaultPaymentGateway() ) ) {
			transactionPayment = new PayPalTransactionPayment();
		}
		return transactionPayment;
	}

	@Override
	public Address getBillingAddress() {
		// This check is only required due to a data transfer that went wrong
		// it should be possible to delete this at a later stage.
		if( billingAddress == null ) {
			billingAddress = new Address();
		}
		return billingAddress;
	}

	@Override
	public boolean isDeliveryAddressTheSame() {
		return isDeliveryAddressTheSame;
	}

	@Override
	public Address getDeliveryAddress() {
		return getShippingAddress();
	}

	public void setShippingAddress(Address shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public Address getShippingAddress() {
		return shippingAddress;
	}

	public void setDeliveryAddressTheSame(boolean isDeliveryAddressTheSame) {
		this.isDeliveryAddressTheSame = isDeliveryAddressTheSame;
	}

	/**
	 * @see getInsufficientStockForOrderList()
	 * **/
	public boolean checkAllStockIsAvailable() {
		boolean allStockIsAvailable = true;
		ArrayList<EcommerceShoppingCartItem> siList = new ArrayList<EcommerceShoppingCartItem>(ecommerceShoppingCart.getEcommerceShoppingCartItems());
		for (int i=0; i < siList.size(); i++) {
			EcommerceShoppingCartItem thisEsci = siList.get(i);
			int thisRpQty = (Integer) ApplicationUtil.getFirstUniqueResult("SELECT rp.quantity FROM " + RealizedProduct.class.getSimpleName() + " rp WHERE rp.id=" + thisEsci.getRealizedProduct().getId());
			if (thisEsci.getQuantity() > thisRpQty) {
				allStockIsAvailable = false;
				break;
			}
		}
		return allStockIsAvailable;
	}

	public ArrayList<EcommerceShoppingCartItem> getInsufficientStockForOrderList() {
		ArrayList<EcommerceShoppingCartItem> stockIssues = new ArrayList<EcommerceShoppingCartItem>();
		ArrayList<EcommerceShoppingCartItem> siList = new ArrayList<EcommerceShoppingCartItem>(ecommerceShoppingCart.getEcommerceShoppingCartItems());
		for (int i=0; i < siList.size(); i++) {
			EcommerceShoppingCartItem thisEsci = siList.get(i);
			int thisRpQty = (Integer) ApplicationUtil.getFirstUniqueResult("SELECT rp.quantity FROM " + RealizedProduct.class.getSimpleName() + " rp WHERE rp.id=" + thisEsci.getRealizedProduct().getId());
			if (thisEsci.getQuantity() > thisRpQty) {
				thisEsci.getRealizedProduct().setQuantity(thisRpQty);
				stockIssues.add(thisEsci);
			}
		}
		return stockIssues;
	}

	public boolean getHasCustomer() {
		return ecommerceShoppingCart.getHasCustomer();
	}

	public boolean getHasPhone() {
		Customer c = ecommerceShoppingCart.getCustomer();
		if (c.getBillingAddress() == null) {
			return false;
		} else {
			return (c.getBillingAddress().getPhone() != null);
		}
	}

	/* -------------------- */

	public void markAsDispatched() {
		if (trackingNumber != null && !trackingNumber.equals("")) {
			if( getRealizedProductReturn() != null ) {
				getRealizedProductReturn().setRealizedProductReturnStatus(RealizedProductReturnStatus.DISPATCHED);
			}
			transactionStatus = TransactionStatus.DISPATCHED;
			saveDetails();
			JSFUtil.addMessage("The transaction has been marked as dispatched.", FacesMessage.SEVERITY_INFO);
		}
		else {
			JSFUtil.addMessage("This tracking number is not correct.", FacesMessage.SEVERITY_ERROR);
		}
	}

	public boolean isDispatchedOrOnLoan() {
		if( getTransactionStatus() != null && (getTransactionStatus().equals( TransactionStatus.ON_LOAN ) ||
				getTransactionStatus().equals( TransactionStatus.DISPATCHED )) ) {
			return true;
		} else {
			return false;
		}
	}

	public boolean getIsCustomerTradeAccount() {
		return (getEcommerceShoppingCart().getCustomer() instanceof CompanyContact) && (((CompanyContact)getEcommerceShoppingCart().getCustomer()).getCompany().isCreditAllowed() );
	}

	public boolean isCustomerACompanyContact() {
		return getEcommerceShoppingCart().getCustomer() instanceof CompanyContact;
	}

	public String getTransactionTypeLabel() {
		return transactionType.getLabel();
	}

	public EcommerceShoppingCartItem addCustomProductToCart() {
		EcommerceShoppingCartItem ecommerceShoppingCartItem = new EcommerceShoppingCartItem();
		return addCustomProductToCart( ecommerceShoppingCartItem );
	}
	
	public EcommerceShoppingCartItem addCustomProductToCart( EcommerceShoppingCartItem ecommerceShoppingCartItem ) {
		ecommerceShoppingCartItem.setCustomisable( true );
		ecommerceShoppingCartItem.setItemName( "Custom" );
		ecommerceShoppingCartItem.setQuantity( 1 );
		ecommerceShoppingCartItem.setShoppingCart(getEcommerceShoppingCart());
		ecommerceShoppingCartItem.updateVatPercentage();
		getEcommerceShoppingCart().getItems().add(ecommerceShoppingCartItem);

		return ecommerceShoppingCartItem;
	}

	public boolean isValid() {
		if( validUntilDate == null ) {
			return false;
		} else {
			Long millisecondDifference = new Date().getTime() - validUntilDate.getTime();
			double dayDifference = millisecondDifference / (60*60*24d);
			if( dayDifference > 0 ) {
				return false;
			} else {
				return true;
			}
		}
	}

	public boolean getIsStatusNew() {
		return transactionStatus == TransactionStatus.NEW;
	}

	public boolean getIsPaymentRequired() {
		return transactionStatus == TransactionStatus.PAYMENT_REQUIRED;
	}
	public boolean getIsAcknowledged() {
		return transactionStatus == TransactionStatus.ACKNOWLEDGED;
	}
	public boolean getIsCancelled() {
		return transactionStatus == TransactionStatus.CANCELLED;
	}
	public boolean getIsAcknowledgedOrFurther() {
		if( transactionStatus != null ) {
			return transactionStatus.ordinal() >= TransactionStatus.ACKNOWLEDGED.ordinal();
		} else {
			return false;
		}
	}
	public boolean getIsBookedOut() {
		return transactionStatus == TransactionStatus.BOOKED_OUT;
	}
	public boolean getIsAwaitingDispatch() {
		return transactionStatus == TransactionStatus.AWAITING_DISPATCH;
	}
	public boolean getIsAwaitingDispatchOrFurther() {
		if( transactionStatus != null ) {
			return transactionStatus.ordinal() >= TransactionStatus.AWAITING_DISPATCH.ordinal();
		} else {
			return false;
		}
	}
	public boolean getIsDispatched() {
		return transactionStatus == TransactionStatus.DISPATCHED;
	}
	public boolean isIncomplete() {
		return transactionStatus == TransactionStatus.INCOMPLETE;
	}
	public boolean getIsDispatchedOrFurther() {
		if( transactionStatus != null ) {
			return transactionStatus.ordinal() >= TransactionStatus.DISPATCHED.ordinal();
		} else {
			return false;
		}
	}
	public boolean getIsOnLoan() {
		return transactionStatus == TransactionStatus.ON_LOAN;
	}
	public boolean getIsReturned() {
		return transactionStatus == TransactionStatus.RETURNED;
	}

	public void setCreditCardDetails(CreditCardDetails creditCardDetails) {
		this.creditCardDetails = creditCardDetails;
	}

	public CreditCardDetails getCreditCardDetails() {
		return creditCardDetails;
	}

	@Deprecated
	/**
	 * This has been replaced by getIsDeliveryAddressTheSame
	 */
	public boolean getIsDeliveryAddressTheSame() {
		return isDeliveryAddressTheSame;
	}


	@Deprecated
	/**
	 * This has been replaced by setDeliveryAddressTheSame
	 */
	public void setIsDeliveryAddressTheSame(boolean isDeliveryAddressTheSame) {
		this.isDeliveryAddressTheSame = isDeliveryAddressTheSame;
	}

	public TransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public TransactionStatus getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(TransactionStatus transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public String getSpecialDeliveryInstructions() {
		return specialDeliveryInstructions;
	}

	public void setSpecialDeliveryInstructions(String specialDeliveryInstructions) {
		this.specialDeliveryInstructions = specialDeliveryInstructions;
	}

	public Date getDeliveryRequiredByDate() {
		return deliveryRequiredByDate;
	}

	public void setDeliveryRequiredByDate(Date deliveryRequiredByDate) {
		this.deliveryRequiredByDate = deliveryRequiredByDate;
	}

	public String getDeliveryRequiredByDateString() {
		return FormatUtil.formatDate( getDeliveryRequiredByDate() );
	}

	public String getTrackingNumber() {
		return trackingNumber;
	}

	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}

	public Long getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(Long invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public Date getLoanReturnDate() {
		return loanReturnDate;
	}

	public String getLoanReturnDateStr() {
		return FormatUtil.formatDate(getLoanReturnDate());
	}

	public void setLoanReturnDate(Date loanReturnDate) {
		this.loanReturnDate = loanReturnDate;
	}

	public RealizedProductReturn getRealizedProductReturn() {
		return realizedProductReturn;
	}

	public void setRealizedProductReturn(RealizedProductReturn realizedProductReturn) {
		this.realizedProductReturn = realizedProductReturn;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setCustomerReference(String customerReference) {
		this.customerReference = customerReference;
	}

	public String getCustomerReference() {
		return customerReference;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public String getTransactionDateStdStr() {
		if( transactionDate != null ) {
			return FormatUtil.formatDate( transactionDate );
		} else {
			return "";
		}
	}

	public String getValidUntilDateStdStr() {
		if( validUntilDate != null ) {
			return FormatUtil.formatDate( validUntilDate );
		} else {
			return "";
		}
	}

	public void setTransactionNotes(String transactionNotes) {
		this.transactionNotes = transactionNotes;
	}

	public String getTransactionNotes() {
		return transactionNotes;
	}

	/**
	 * @deprecated
	 * @param transactionHistory
	 * This was part of the old teletest system but is not required in the new
	 * system.
	 */
	@Deprecated
	public void setTransactionHistory(String transactionHistory) {
		this.transactionHistory = transactionHistory;
	}

	/**
	 * @deprecated
	 * @param transactionHistory
	 * This was part of the old teletest system but is not required in the new
	 * system.
	 */
	@Deprecated
	public String getTransactionHistory() {
		return transactionHistory;
	}

	public void setValidUntilDate(Date validUntilDate) {
		this.validUntilDate = validUntilDate;
	}

	public Date getValidUntilDate() {
		return validUntilDate;
	}

	public void setDispatchDate(Date dispatchDate) {
		this.dispatchDate = dispatchDate;
	}

	public Date getDispatchDate() {
		return dispatchDate;
	}

	public String getDispatchDateStr() {
		return FormatUtil.formatDate(dispatchDate);
	}

	public void setCourierServiceName(String courierServiceName) {
		this.courierServiceName = courierServiceName;
	}

	public String getCourierServiceName() {
		return courierServiceName;
	}

	public void setDispatchedDate(Date dispatchedDate) {
		this.dispatchedDate = dispatchedDate;
	}

	public Date getDispatchedDate() {
		return dispatchedDate;
	}

	public String getDispatchedDateStr() {
		return FormatUtil.formatDate(dispatchedDate);
	}



	public void setBackEndMenuWizard(TransactionBackEndMenuWizard backEndMenuWizard) {
		this.backEndMenuWizard = backEndMenuWizard;
	}

	public TransactionBackEndMenuWizard getBackEndMenuWizard() {
		return backEndMenuWizard;
	}

	public void setAuthorisationCode(String authorisationCode) {
		this.authorisationCode = authorisationCode;
	}

	public String getAuthorisationCode() {
		return authorisationCode;
	}

	public void setFrontEndMenuWizard(TransactionFrontEndMenuWizard frontEndMenuWizard) {
		this.frontEndMenuWizard = frontEndMenuWizard;
	}

	public TransactionFrontEndMenuWizard getFrontEndMenuWizard() {
		return frontEndMenuWizard;
	}

	public void setGiftDeliveryAddressTheSame(boolean isGiftDeliveryAddressTheSame) {
		this.isGiftDeliveryAddressTheSame = isGiftDeliveryAddressTheSame;
	}

	public boolean isGiftDeliveryAddressTheSame() {
		return isGiftDeliveryAddressTheSame;
	}

	public void setGiftDeliveryAddress(Address giftDeliveryAddress) {
		this.giftDeliveryAddress = giftDeliveryAddress;
	}

	public Address getGiftDeliveryAddress() {
		return giftDeliveryAddress;
	}

	public void setGiftsSentViaEmail(boolean giftsSentViaEmail) {
		this.giftsSentViaEmail = giftsSentViaEmail;
	}

	public boolean isGiftsSentViaEmail() {
		return giftsSentViaEmail;
	}

	public void setFirstInvoicedDate(Date firstInvoicedDate) {
		this.firstInvoicedDate = firstInvoicedDate;
	}

	public Date getFirstInvoicedDate() {
		return firstInvoicedDate;
	}

	public boolean isUsingStoredCreditCardDetails() {
		return isUsingStoredCreditCardDetails;
	}

	public void setUsingStoredCreditCardDetails(
			boolean isUsingStoredCreditCardDetails) {
		this.isUsingStoredCreditCardDetails = isUsingStoredCreditCardDetails;
	}

	@Override
	public Subscriber getSourceSubscriber() {
		return getCustomer().getSubscriber();
	}

	@Override
	public String getSourceUniqueDisplayName() {
		return getCustomer().getFullName() + ": Transaction " + getId();
	}

	@Override
	public List<BulkEmailSource> getEmailAutoCompleteSuggestions(String searchString, Integer limit) {
		BeanDao transactionDao = new BeanDao( Transaction.class );
		transactionDao.setIsReturningActiveBeans( true );
		List<BulkEmailSource> transactions = null;
		if( searchString != null ) {
			transactionDao.addWhereCriteria( "CONCAT(ecommerceShoppingCart.customer.subscriber.firstName,' ',ecommerceShoppingCart.customer.subscriber.surname) like :similarSearchText OR id like :similarSearchText" );
			if( limit != null ) {
				transactionDao.setMaxResults(limit);
			}
			transactionDao.setNamedParameter("similarSearchText", "%" + searchString + "%");
			transactions = (List<BulkEmailSource>) transactionDao.getAll();
		} else {
			transactions = transactionDao.getAll();
		}
		return transactions;
	}

	public String getPaymentFailedErrorMessage() {
		return paymentFailedErrorMessage;
	}

	public void setPaymentFailedErrorMessage(String paymentFailedErrorMessage) {
		this.paymentFailedErrorMessage = paymentFailedErrorMessage;
	}

	public Transaction getDuplicateTransaction() {
		return duplicateTransaction;
	}

	public void setDuplicateTransaction(Transaction duplicateTransaction) {
		this.duplicateTransaction = duplicateTransaction;
	}

	public boolean isAbandonedEmailSent() {
		return abandonedEmailSent;
	}

	public void setAbandonedEmailSent(boolean abandonedEmailSent) {
		this.abandonedEmailSent = abandonedEmailSent;
	}
	
	@Override
	public String getFirstName() {
		return ecommerceShoppingCart.getCustomer().getSubscriber().getFirstName();
	}

	@Override
	public String getSurname() {
		return ecommerceShoppingCart.getCustomer().getSubscriber().getSurname();
	}

	@Override
	public String getEmailAddress() {
		return ecommerceShoppingCart.getCustomer().getSubscriber().getEmailAddress();
	}

	@Override
	public String getFinderSearchCriteria() {
		return "(CONCAT(bean.ecommerceShoppingCart.customer.subscriber.firstName,' ',bean.ecommerceShoppingCart.customer.subscriber.surname) LIKE :similarSearchText OR bean.ecommerceShoppingCart.customer.subscriber.emailAddress LIKE :similarSearchText)";
	}

	@Override
	public String getAlphabeticalSortByCriteria() {
		return "bean.ecommerceShoppingCart.customer.subscriber.firstName ASC, bean.ecommerceShoppingCart.customer.subscriber.surname ASC";
	}

	public PaymentGatewayPost getAuthenticatedPaymentGatewayDirectPost() {
		return authenticatedPaymentGatewayDirectPost;
	}

	public void setAuthenticatedPaymentGatewayDirectPost( PaymentGatewayPost authenticatedPaymentGatewayDirectPost ) {
		this.authenticatedPaymentGatewayDirectPost = authenticatedPaymentGatewayDirectPost;
	}

	public boolean isFullyPaid() {
		return isFullyPaid;
	}

	public void setFullyPaid(boolean isFullyPaid) {
		this.isFullyPaid = isFullyPaid;
	}

}
