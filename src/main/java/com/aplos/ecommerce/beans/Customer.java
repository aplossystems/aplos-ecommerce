package com.aplos.ecommerce.beans;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import com.aplos.common.AplosUrl;
import com.aplos.common.annotations.BeanScope;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.RemoveEmpty;
import com.aplos.common.annotations.persistence.Cache;
import com.aplos.common.annotations.persistence.Cascade;
import com.aplos.common.annotations.persistence.CascadeType;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.FetchType;
import com.aplos.common.annotations.persistence.Inheritance;
import com.aplos.common.annotations.persistence.InheritanceType;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.OneToMany;
import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.Address;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.CreditCardDetails;
import com.aplos.common.beans.Currency;
import com.aplos.common.beans.ShoppingCart;
import com.aplos.common.beans.Subscriber;
import com.aplos.common.beans.communication.AplosEmail;
import com.aplos.common.enums.JsfScope;
import com.aplos.common.enums.VatExemption;
import com.aplos.common.interfaces.BulkEmailFinder;
import com.aplos.common.interfaces.BulkEmailSource;
import com.aplos.common.interfaces.BulkSubscriberSource;
import com.aplos.common.interfaces.ForumUser;
import com.aplos.common.interfaces.MailRecipient;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.utils.EcommerceUtil;

@Entity
@ManagedBean
@SessionScoped
@Inheritance(strategy = InheritanceType.JOINED)
@Cache
@DynamicMetaValueKey(oldKey={"CUSTOMER","CUSTOMER_FU"})
@BeanScope(scope=JsfScope.TAB_SESSION)
public class Customer extends AplosBean implements MailRecipient, ForumUser, BulkEmailFinder, BulkSubscriberSource {
	private static final long serialVersionUID = -8991599132502938001L;

	@ManyToOne(fetch=FetchType.LAZY)
	private Currency currency;
	@ManyToOne(fetch=FetchType.LAZY)
	private CustomerType customerType;
	private BigDecimal storeCreditAmount = new BigDecimal(0);

	private int loyaltyPoints;
	@OneToMany(fetch=FetchType.LAZY)
	private List<RealizedProduct> wishList = new ArrayList<RealizedProduct>();
	@ManyToOne(fetch=FetchType.LAZY)
	@Cascade({CascadeType.ALL})
	private Subscriber subscriber = new Subscriber();
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
	private Address altShippingAddress;
	@Column(unique=true)
	private String username;
	private String password;

	private String position;

	private String roomNo;
	private String supplierReference;

	@ManyToOne(fetch=FetchType.LAZY)
	@Cascade({CascadeType.ALL})
	private CreditCardDetails creditCardDetails;

	@Column(columnDefinition="LONGTEXT")
    private String customerNotes;

	private boolean rememberCreditCardDetails = false;
	private boolean isUsingAlternativeAddress = false;

	// For password reset
	@Column(unique=true)
	private String passwordResetCode;
	private Date passwordResetDate;
	/*
	 *  This is used to store a transaction that may have bean in the session
	 *  before a password reset or email verification
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	private Transaction temporaryTransaction;

	@Column(unique=true)
	private String emailVerificationCode;
	private Date emailVerificationDate;

	private boolean isVerified = false;

	@Transient
	private boolean isLoggedIn;

	// This may be fairly specific to the Teletest project and may
	// have to be moved to the implementation module.
	private boolean isCompanyConnectionRequested;

	private DuplicateAddresses duplicateAddresses = DuplicateAddresses.SHIPPING_ADDRESS_FOR_BILLING;

	public enum DuplicateAddresses {
		BILLING_ADDRESS_FOR_SHIPPING,
		SHIPPING_ADDRESS_FOR_BILLING,
		NONE;
	}

	public Customer() {}

//	@Override
//	public void hibernateInitialiseAfterCheck( boolean fullInitialisation ) {
//		if( getTemporaryTransaction() != null && getTemporaryTransaction().getDateCreated() != null) {
//			// delete any temporaryTransactions over a day old
//			if( new Date().getTime() - getTemporaryTransaction().getDateCreated().getTime() > 24 * 3600 * 1000 ) {
//				setTemporaryTransaction( null );
//				this.saveDetails();
//			} else {
//				HibernateUtil.initialise( getTemporaryTransaction(), fullInitialisation );
//			}
//		}
//	}
	
	@Override
	public String getJDynamiTeValue(String variableKey, AplosEmail aplosEmail) {
		return null;
	}
	
	@Override
	public Long getMessageSourceId() {
		return getId();
	}

	public void copy( Customer customer ) {
		super.copy( customer );
		setCurrency( customer.getCurrency() );
		setWishList( customer.getWishList() );
		setSubscriber( customer.getSubscriber() );
		setBillingAddress( customer.getBillingAddress() );
		setShippingAddress( customer.getShippingAddress() );
		setUsername( customer.getUsername() );
		setPassword( customer.getPassword() );
		setPasswordResetCode( customer.getPasswordResetCode() );
		setPasswordResetDate( customer.getPasswordResetDate() );
		setCreditCardDetails( customer.getCreditCardDetails() );
		setCustomerNotes( customer.getCustomerNotes() );
		setRememberCreditCardDetails( customer.isRememberCreditCardDetails() );
		setDuplicateAddresses( customer.getDuplicateAddresses() );
	}

	public boolean isVatExempt( Address address ) {
		if( address == null || address.getCountry() == null ) {
			return false;
		} else {
			return isVatExempt( address.getCountry().getVatExemption() );
		}
	}

	public boolean isVatExempt( VatExemption vatExemption ) {
		if( vatExemption == null ||
				vatExemption.equals( VatExemption.NOT_EXEMPT ) ||
				vatExemption.equals( VatExemption.EU_EXEMPT ) ) {
			return false;
		} else {
			return true;
		}
	}

	public boolean createTemporaryTransaction() {
		ShoppingCart shoppingCart = JSFUtil.getBeanFromScope( ShoppingCart.class );
		if( shoppingCart != null && shoppingCart.getItems().size() > 0 ) {
			Transaction transaction = (Transaction) JSFUtil.getBeanFromScope( Transaction.class );
			EcommerceUtil.getEcommerceUtil().addCustomerToCart( this );
			if( transaction == null || !transaction.getCustomer().getId().equals( this.getId() ) ) {
				transaction = EcommerceUtil.getEcommerceUtil().createTransaction( this, true );
			}
			temporaryTransaction = transaction;
			return true;
		} else {
			return false;
		}
	}

	@Override
	//public SelectItem[] getSelectItemBeansWithNotSelected(String notSelectedStr) {
	public SelectItem[] getSelectItemBeans( String notSelectedStr ) {
		BeanDao customerDao = new BeanDao( getClass() );
		customerDao.setSelectCriteria( "bean.id, s.firstName, s.surname, s.emailAddress, bean.active" );
		customerDao.addQueryTable( "s", "bean.subscriber" );
		return super.getSelectItemBeansWithNotSelected( customerDao.getAll() ,notSelectedStr);
	}

	public String getFirstLineOfNotes() {
		return CommonUtil.getFirstLine( getCustomerNotes() );
	}

	public void putTemporaryTransactionInSession() {
		if( getTemporaryTransaction() != null ) {
			getTemporaryTransaction().addToScope();
			// Make sure that the shoppingCart binding is used
			getTemporaryTransaction().getEcommerceShoppingCart().addToScope();
		}
	}
	
	@Override
	public void addToScope(JsfScope associatedBeanScope) {
		super.addToScope(associatedBeanScope);
		addToScope( CommonUtil.getBinding( Customer.class ), this, associatedBeanScope );
	}

//	public String getTransactionPdfDirPath() {
//		return (EcommerceWorkingDirectory.TRANSACTION_PDFS_DIR.getDirectoryPath(true) + getId() + " - " + getDisplayName()).trim() + "/";
//	}
//
//	public String getReturnsPdfDirPath() {
//		return (EcommerceWorkingDirectory.REALIZED_PRODUCT_RETURN_PDFS_DIR.getDirectoryPath(true) + getId() + " - " + getDisplayName()).trim() + "/";
//	}

	public String getDetermineEmailAddress() {
		return subscriber.getEmailAddress();
	}

	public void setDetermineEmailAddress( String address ) {
		subscriber.setEmailAddress( address );
	}

	public BeanDao getRealizedProductDao() {
		return new BeanDao( RealizedProduct.class )
					.addWhereCriteria( "bean.isHiddenFromCustomer = false" );
	}

	public Address getDetermineBillingAddress() {
		return determineBillingAddress();
	}

	public Address determineBillingAddress() {
		if( DuplicateAddresses.SHIPPING_ADDRESS_FOR_BILLING.equals( getDuplicateAddresses() ) ) {
			return determineShippingAddress();
		} else {
			return getBillingAddress();
		}
	}

	public Address determineAltShippingAddress() {
		return getAltShippingAddress();
	}

	public void updateSubscriber( String emailAddress, String firstName, String surname ) {
		BeanDao subscriberDao = new BeanDao( Subscriber.class );
		subscriberDao.setWhereCriteria( "bean.emailAddress LIKE :email" );
		subscriberDao.setNamedParameter( "email", emailAddress );
		Subscriber existingSubscriber = subscriberDao.getFirstBeanResult();
		if( existingSubscriber != null ) {
			setSubscriber( existingSubscriber );
		} else {
			getSubscriber().setEmailAddress( emailAddress );
			getSubscriber().setFirstName( firstName );
			getSubscriber().setSurname( surname );
		}
	}

	public Address getDetermineShippingAddress() {
		return determineShippingAddress();
	}

	public Address determineShippingAddress() {
		if( DuplicateAddresses.BILLING_ADDRESS_FOR_SHIPPING.equals( getDuplicateAddresses() ) ) {
			return determineBillingAddress();
		} else {
			return getShippingAddress();
		}
	}

	@Override
	public String getDisplayName() {
		if( getSubscriber().getFirstName() != null ) {
			return getFullName();
		} else {
			return getSubscriber().getEmailAddress();
		}
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getPosition() {
		return position;
	}

	@Override
	public Address getMailRecipientAddress() {
		return shippingAddress;
	}

	@Override
	public String getFullName() {
		String fullName = subscriber.getFullName();
		if (fullName.toLowerCase().equals("subscriber")) {
			fullName = this.username;
		}
		if (fullName == null || fullName.toLowerCase().equals("")) {
			//fullName = subscriber.getEmailAddress();
			fullName = "User " + getId();
		}
		return fullName;
	}

	public static String getSubscriberFinderHql() {
		return "bean.subscriber";
	}

	public Subscriber getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(Subscriber newSubscriber) {
		this.subscriber = newSubscriber;
	}

	public List<RealizedProduct> getWishList() {
		return wishList;
	}

	public void setWishList(List<RealizedProduct> newWishList) {
		this.wishList = newWishList;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency newCurrency) {
		this.currency = newCurrency;
	}

	public int getLoyaltyPoints() {
		return loyaltyPoints;
	}

	public void setLoyaltyPoints(int newLoyaltyPointCount) {
		this.loyaltyPoints = newLoyaltyPointCount;
	}

	public void setShippingAddress(Address shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public Address getShippingAddress() {
		return shippingAddress;
	}

	public void setBillingAddress(Address billingAddress) {
		this.billingAddress = billingAddress;
	}

	public Address getBillingAddress() {
		return billingAddress;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setPassword(String password) {
		if (password != "") {
			this.password = password;
		}
	}

	public String getPassword() {
		return password;
	}

	public void setPasswordResetCode(String passwordResetCode) {
		this.passwordResetCode = passwordResetCode;
	}

	public String getPasswordResetCode() {
		return passwordResetCode;
	}

	public void setPasswordResetDate(Date passwordResetDate) {
		this.passwordResetDate = passwordResetDate;
	}

	public Date getPasswordResetDate() {
		return passwordResetDate;
	}

	public void login() {
		isLoggedIn = true;
	}

	public String getSignInBtnStr() {
		if (isLoggedIn) {
			return "ACCOUNT";
		} else {
			return "SIGN IN";
		}
	}

	public void logout() {
		isLoggedIn = false;
	}

	public String logoutAndRedirect() {
		this.logout();
		JSFUtil.redirect(new AplosUrl("/"), true);
		return null;
	}

	@Override
	public boolean isLoggedIn() {
		return isLoggedIn;
	}

	public void setCreditCardDetails(CreditCardDetails creditCardDetails) {
		this.creditCardDetails = creditCardDetails;
	}

	public CreditCardDetails getCreditCardDetails() {
		return creditCardDetails;
	}

	public boolean isWishListEmpty() {
		return wishList.size()==0;
	}

	public static Customer getCustomerByEmailAddress( String emailAddress ) {
		BeanDao customerDao = new BeanDao( Customer.class ).addWhereCriteria( "bean.subscriber.emailAddress = :emailAddress" );
		customerDao.setNamedParameter( "emailAddress", emailAddress );
		return (Customer) customerDao.getFirstBeanResult();
	}

	public static Customer getCustomerOrCreate( String emailAddress, Class<? extends Customer> customerClass ) {
		Customer customer = getCustomerByEmailAddress( emailAddress );

		if( customer == null ) {
			BeanDao subscriberBeanDao = new BeanDao(Subscriber.class);
			subscriberBeanDao.addWhereCriteria("bean.emailAddress = '" + emailAddress + "'");
			Subscriber subscriber = (Subscriber) subscriberBeanDao.getFirstResult();

			if( subscriber == null ) {
				subscriber = new Subscriber();
				subscriber.setEmailAddress( emailAddress );
			}

			customer = (Customer) CommonUtil.getNewInstance( customerClass, null );
			customer.setSubscriber(subscriber);
		}

		return customer;
	}

	public void checkForExistingChildObjectsAndSave( boolean addMessages ) {
		BeanDao subscriberBeanDao = new BeanDao(Subscriber.class);
		subscriberBeanDao.addWhereCriteria("bean.emailAddress = '" + getSubscriber().getEmailAddress() + "'");
		List<Subscriber> subscriberList = subscriberBeanDao.getAll();
		if (subscriberList.size() > 0 ) {
			subscriber = subscriberList.get( 0 );
		} else {
			subscriber.saveDetails();
		}
		saveDetails();
	}

	public void setRememberCreditCardDetails(boolean rememberCreditCardDetails) {
		this.rememberCreditCardDetails = rememberCreditCardDetails;
	}

	public boolean isRememberCreditCardDetails() {
		return rememberCreditCardDetails;
	}

	public void setCustomerNotes(String customerNotes) {
		this.customerNotes = customerNotes;
	}

	public String getCustomerNotes() {
		return customerNotes;
	}

	public void setDuplicateAddresses(DuplicateAddresses duplicateAddresses) {
		this.duplicateAddresses = duplicateAddresses;
	}

	public DuplicateAddresses getDuplicateAddresses() {
		return duplicateAddresses;
	}

	public void setAltShippingAddress(Address altShippingAddress) {
		this.altShippingAddress = altShippingAddress;
	}

	public Address getAltShippingAddress() {
		return altShippingAddress;
	}

	public void setCustomerType(CustomerType customerType) {
		this.customerType = customerType;
	}

	public CustomerType getCustomerType() {
		return customerType;
	}

	public void setCompanyConnectionRequested(boolean isCompanyConnectionRequested) {
		this.isCompanyConnectionRequested = isCompanyConnectionRequested;
	}

	public boolean isCompanyConnectionRequested() {
		return isCompanyConnectionRequested;
	}

	public void setRoomNo(String roomNo) {
		this.roomNo = roomNo;
	}

	public String getRoomNo() {
		return roomNo;
	}

	public void setUsingAlternativeAddress(boolean isUsingAlternativeAddress) {
		this.isUsingAlternativeAddress = isUsingAlternativeAddress;
	}

	public boolean isUsingAlternativeAddress() {
		return isUsingAlternativeAddress;
	}

	public void setStoreCreditAmount(BigDecimal accountCredit) {
		this.storeCreditAmount = accountCredit;
	}

	public BigDecimal getStoreCreditAmount() {
		return storeCreditAmount;
	}

	public String getStoreCreditAmountString() {
		return FormatUtil.formatTwoDigit(storeCreditAmount);
	}

	public boolean getHasStoreCredit() {
		return storeCreditAmount != null & storeCreditAmount.doubleValue() > 0;
	}

	public void addStoreCredit(BigDecimal credit) {
		if (storeCreditAmount == null) {
			storeCreditAmount = credit;
		} else {
			storeCreditAmount = storeCreditAmount.add(credit);
		}
	}

	public void takeStoreCredit(BigDecimal usedCredit) {
		if (storeCreditAmount == null) {
			storeCreditAmount = new BigDecimal(0);
		}
		storeCreditAmount = storeCreditAmount.subtract(usedCredit);
	}

//	/**
//	 * @param requiredAmount
//	 * @return Returns the amount of store credit available up to the requiredAmount
//	 * and also removes it from the customer
//	 */
//	public BigDecimal useStoreCredit(BigDecimal requiredAmount) {
//		if (requiredAmount.compareTo(storeCreditAmount) < 0) {
//			storeCreditAmount.subtract(requiredAmount);
//			return requiredAmount;
//		} else {
//			BigDecimal ret = new BigDecimal(storeCreditAmount.doubleValue());
//			storeCreditAmount = new BigDecimal(0);
//			return ret;
//		}
//	}

	/**
	 * @param requiredAmount
	 * @return Returns the amount of store credit available up to the requiredAmount
	 */
	public BigDecimal getAvailableStoreCredit(BigDecimal requiredAmount) {
		if (requiredAmount != null) {
			if (storeCreditAmount == null) {
				storeCreditAmount = new BigDecimal(0);
			}
			if (requiredAmount.compareTo(storeCreditAmount) < 0) {
				return requiredAmount;
			} else {
				return storeCreditAmount;
			}
		} else {
			return new BigDecimal(0);
		}
	}


	public void setEmailVerificationCode(String emailVerificationCode) {
		this.emailVerificationCode = emailVerificationCode;
	}

	public String getEmailVerificationCode() {
		return emailVerificationCode;
	}

	public void setEmailVerificationDate(Date emailVerificationDate) {
		this.emailVerificationDate = emailVerificationDate;
	}

	public Date getEmailVerificationDate() {
		return emailVerificationDate;
	}

	public void setVerified(boolean isVerified) {
		this.isVerified = isVerified;
	}

	public boolean isVerified() {
		return isVerified;
	}

	public void setTemporaryTransaction(Transaction temporaryTransaction) {
		this.temporaryTransaction = temporaryTransaction;
	}

	public Transaction getTemporaryTransaction() {
		return temporaryTransaction;
	}

	public String getSupplierReference() {
		return supplierReference;
	}

	public void setSupplierReference(String supplierReference) {
		this.supplierReference = supplierReference;
	}

	public void addCustomerNote(String newNote) {
		if (customerNotes == null) {
			customerNotes = "";
		}
		customerNotes = newNote + "\n\n" + customerNotes;
	}

	@Override
	public Subscriber getSourceSubscriber() {
		return subscriber;
	}

	@Override
	public String getSourceUniqueDisplayName() {
		return "Customer: " + getSubscriber().getSourceUniqueDisplayName();
	}

	@Override
	public String getFirstName() {
		return subscriber.getFirstName();
	}

	@Override
	public String getSurname() {
		return subscriber.getSurname();
	}

	@Override
	public String getEmailAddress() {
		return subscriber.getEmailAddress();
	}
	
	@Override
	public String getBulkMessageFinderName() {
		return "All customers";
	}

	@Override
	public String getFinderSearchCriteria() {
		return "(bean.username LIKE :similarSearchText OR CONCAT(bean.subscriber.firstName,' ',bean.subscriber.surname) LIKE :similarSearchText OR bean.subscriber.emailAddress LIKE :similarSearchText)";
	}

	@Override
	public String getAlphabeticalSortByCriteria() {
		return "bean.subscriber.firstName ASC, bean.subscriber.surname ASC";
	}
	
	@Override
	public List<BulkEmailSource> getEmailAutoCompleteSuggestions(String searchString, Integer limit) {
		BeanDao customerDao = new BeanDao( Customer.class );
		customerDao.setIsReturningActiveBeans( true );
		List<BulkEmailSource> customers = null;
		if( searchString != null ) {
			customerDao.addWhereCriteria( "CONCAT(subscriber.firstName,' ',subscriber.surname) like :similarSearchText OR subscriber.emailAddress like :similarSearchText" );
			if( limit != null ) {
				customerDao.setMaxResults(limit);
			}
			customerDao.setNamedParameter("similarSearchText", "%" + searchString + "%");
			customers = (List<BulkEmailSource>) customerDao.getAll();
		} else {
			customers = customerDao.getAll();
		}
		return customers;
	}
}
