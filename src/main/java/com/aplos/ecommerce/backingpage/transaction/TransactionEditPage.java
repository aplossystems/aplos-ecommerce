package com.aplos.ecommerce.backingpage.transaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.aplos.common.AplosUrl;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.backingpage.communication.AplosEmailEditPage;
import com.aplos.common.beans.AplosAbstractBean;
import com.aplos.common.beans.CreditCardDetails;
import com.aplos.common.beans.CreditCardType;
import com.aplos.common.beans.ShoppingCartItem;
import com.aplos.common.beans.Website;
import com.aplos.common.beans.communication.AplosEmail;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.backingpage.customer.CompanyContactEditPage;
import com.aplos.ecommerce.backingpage.customer.CompanyEditPage;
import com.aplos.ecommerce.backingpage.customer.CustomerEditPage;
import com.aplos.ecommerce.backingpage.realizedProductReturn.RealizedProductReturnEditPage;
import com.aplos.ecommerce.backingpage.serialNumbers.BookInPage;
import com.aplos.ecommerce.backingpage.serialNumbers.SerialNumberEditPage;
import com.aplos.ecommerce.beans.Company;
import com.aplos.ecommerce.beans.CompanyContact;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.EcommerceShoppingCartItem;
import com.aplos.ecommerce.beans.GiftVoucher;
import com.aplos.ecommerce.beans.PaymentMethod;
import com.aplos.ecommerce.beans.SerialNumber;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.beans.Transaction.TransactionStatus;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;
import com.aplos.ecommerce.enums.TransactionType;
import com.aplos.ecommerce.interfaces.PrintSendActionInter;
import com.aplos.ecommerce.module.EcommerceConfiguration;
import com.aplos.ecommerce.templates.printtemplates.BookingOutSheetTemplate;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=Transaction.class)
public class TransactionEditPage extends EditPage {
	private static final long serialVersionUID = 3998392944322375460L;
	// This is a bit of a hack so that the repeater in the user
	// interface can have a value to check against the row index to
	// decide whether to place a separator or not
	private int serialNumberAssignmentSize;
	private int sentEmailsSize;
	private PrintSendActionInter selectedPrintSendAction;
	private List<SelectItem> creditCardSelectItems;

	public enum PrintSendAction implements PrintSendActionInter {
		DISPATCH_NOTE ("Dispatch note"),
		BOOKING_OUT_SHEET ("Booking out sheet"),
		INVOICE ("Invoice"),
		ACKNOWLEDGEMENT ("Acknowledgement"),
		TRANSACTION ("Transaction"),
		DISPATCH_INVOICE ("Dispatch invoice"),
		COURIER_DETAILS ("Courier details"),
		CANCELLATION_NOTICE ("Cancellation notice");

		public String label;

		private PrintSendAction( String label ) {
			this.label = label;
		}
		
		public String getLabel() {
			return label;
		}
	}

	public void raiseCreditNoteAndRedirect() {
		Transaction duplicateTransaction = getDuplicateTransaction();
		duplicateTransaction.setTransactionType( TransactionType.REFUND );
		duplicateTransaction.getBackEndMenuWizard().setCurrentStepIdx(0);
		for( ShoppingCartItem shoppingCartItem : duplicateTransaction.getEcommerceShoppingCart().getItems() ) {
			shoppingCartItem.setQuantity( shoppingCartItem.getQuantity() * -1 );
			shoppingCartItem.saveDetails();
		}
		duplicateTransaction.getEcommerceShoppingCart().updateCachedValues( true );
		duplicateTransaction.getEcommerceShoppingCart().saveDetails();
		JSFUtil.addMessage( "Credit note has been raised, you are now viewing the credit note" );
		duplicateTransaction.saveDetails();
		duplicateTransaction.redirectToEditPage();
	}

	public String getGiftVoucherCode() {
		EcommerceShoppingCartItem cartItem = (EcommerceShoppingCartItem) JSFUtil.getRequest().getAttribute("cartItem");
		if (cartItem != null) {
			BeanDao giftVoucherDao = new BeanDao(GiftVoucher.class);
			giftVoucherDao.addWhereCriteria("bean.shoppingCartItem.id=" + + cartItem.getId());
			giftVoucherDao.setMaxResults(1);
			GiftVoucher giftVoucher = giftVoucherDao.setIsReturningActiveBeans(true).getFirstBeanResult();
			if (giftVoucher != null) {
				return giftVoucher.getUniqueVoucherCode();
			}
		}
		return null;
	}

	public void changeToQuoteType() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		transaction.setTransactionType( TransactionType.QUOTE );
		transaction.saveDetails();
	}

	public boolean isAuthorisationCodeRequired() {
		return validationRequired( "processPaymentBtn" ) || validationRequired( "acknowledgeBtn" );
	}

	public void takePaymentOnline() {
		Transaction transaction = JSFUtil.getBeanFromScope( Transaction.class );
		transaction.makeOnlinePaymentCall( true, null, false );
	}

	public boolean isTransactionALoan() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		return transaction.getTransactionType() == TransactionType.LOAN;
	}

	public Transaction getDuplicateTransaction() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		Transaction duplicateTransaction = transaction.getCopy( false );
		duplicateTransaction.setDuplicateTransaction( transaction );
		duplicateTransaction.setId( null );
		duplicateTransaction.setInvoiceNumber( null );
		duplicateTransaction.setTransactionDate( new Date() );
		duplicateTransaction.setDateCreated( new Date() );
		duplicateTransaction.setUserIdCreated( JSFUtil.getLoggedInUser().getId() );
		duplicateTransaction.setFirstInvoicedDate( null );
		duplicateTransaction.setTransactionStatus( TransactionStatus.NEW );

		return duplicateTransaction;
	}

	public void duplicateTransactionAndRedirect() {
		Transaction duplicateTransaction = getDuplicateTransaction();
		JSFUtil.addMessage( "Transaction has been duplicated, you are now viewing the duplicate transaction" );
		duplicateTransaction.getEcommerceShoppingCart().saveDetails();
		duplicateTransaction.saveDetails();
		duplicateTransaction.redirectToEditPage();
	}

	public void extendValidUntilDate( boolean updateCachedValues ) {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		transaction.extendValidUntilDate( updateCachedValues );
		transaction.saveDetails();
	}

	public void markAsCancelled() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		transaction.setTransactionStatus(TransactionStatus.CANCELLED);
		transaction.getBackEndMenuWizard().setCurrentStepIdx( 4 );
		transaction.getBackEndMenuWizard().setLatestStepIdx( 4 );
		transaction.saveDetails();
	}

	public void markAsNew() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		transaction.setTransactionStatus(TransactionStatus.NEW);
		transaction.saveDetails();
	}

	@Override
	public boolean responsePageLoad() {
		boolean continueLoad = super.responsePageLoad();
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);

		if( transaction.getCreditCardDetails() == null ) {
			transaction.setCreditCardDetails( new CreditCardDetails() );
		}

		if( transaction.getBackEndMenuWizard() == null ) {
			transaction.setBackEndMenuWizard( new TransactionBackEndMenuWizard() );
		}
		
		if( getCreditCardSelectItems() == null ) {
			createCreditCardSelectItems();
		}

		transaction.getBackEndMenuWizard().refreshMenu( transaction );
		transaction.getBackEndMenuWizard().getMenuSteps().get( transaction.getBackEndMenuWizard().getCurrentStepIdx() ).responsePageLoad();
		return continueLoad;
	}

	public void createCreditCardSelectItems() {
		setCreditCardSelectItems(new ArrayList<SelectItem>());
		getCreditCardSelectItems().add( new SelectItem(true, "Use stored credit card" ) );
		getCreditCardSelectItems().add( new SelectItem(false, "Enter new credit card details" ) );
	}

	public SelectItem[] getPaymentMethodSelectItems() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		List<PaymentMethod> paymentMethodList;

		if( transaction.getEcommerceShoppingCart().getCachedNetTotalAmount().compareTo( new BigDecimal( 0 ) ) == 0 ) {
			paymentMethodList = new ArrayList<PaymentMethod>();
			paymentMethodList.add( EcommerceConfiguration.getEcommerceConfiguration().getNotRequiredPaymentMethod() );
			return AplosAbstractBean.getSelectItemBeans( paymentMethodList, true );
		} else {
			paymentMethodList = new BeanDao( PaymentMethod.class ).setIsReturningActiveBeans(true).getAll();
			boolean removeAccountPaymentMethod = true;
			if( transaction.getEcommerceShoppingCart().getCustomer() instanceof CompanyContact ) {
				if( ((CompanyContact)transaction.getEcommerceShoppingCart().getCustomer()).getCompany().isCreditAllowed() ) {
					removeAccountPaymentMethod = false;
				}
			}
			if( removeAccountPaymentMethod ) {
				paymentMethodList.remove( EcommerceConfiguration.getEcommerceConfiguration().getAccountPaymentMethod() );
			}
			return AplosAbstractBean.getSelectItemBeans( paymentMethodList, "Not Selected", true );
		}
	}

	public void redirectToBookOutPage() {
		JSFUtil.redirect(Website.getCurrentWebsiteFromTabSession().getPackageName(), TransactionBookOutPage.class);
	}

	public String getFormattedDate(Date date) {
		if (date != null) {
			return FormatUtil.formatDate(date);
		}
		else {
			return "";
		}
	}

	public void trackingNumberEntered() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		if (transaction.getTrackingNumber() != null && !transaction.getTrackingNumber().equals("")) {
			if (transaction.getTransactionType() == TransactionType.LOAN) {
				transaction.setTransactionStatus(TransactionStatus.ON_LOAN);
			}
			else {
				transaction.setTransactionStatus(TransactionStatus.DISPATCHED);
			}
			transaction.saveDetails();

		}
		else {
			JSFUtil.addMessage("This tracking number is not correct.", FacesMessage.SEVERITY_ERROR);
		}
	}

	public void markAsReturned() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		transaction.setTransactionStatus(TransactionStatus.RETURNED);
		transaction.saveDetails();
	}

	public String redirectToPrintBookingOutSheetUrl() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		JSFUtil.redirect(new AplosUrl(BookingOutSheetTemplate.getTemplateUrl( transaction )), true);
		return null;
	}

	public String redirectToBookInPage() {
		BookInPage bookInPage = (BookInPage) JSFUtil.resolveVariable(AplosAbstractBean.getBinding(BookInPage.class));
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		bookInPage.setTransaction(transaction);

		for (ShoppingCartItem shoppingCartItem : transaction.getEcommerceShoppingCart().getItems()) {
			bookInPage.initSerialNumberAssignmentListBeans((EcommerceShoppingCartItem) shoppingCartItem);
		}

		JSFUtil.redirect(Website.getCurrentWebsiteFromTabSession().getPackageName(), BookInPage.class);
		return null;
	}

	public SelectItem[] getCreditCardTypeSelectItems() {
		return AplosAbstractBean.getSelectItemBeans(CreditCardType.class);
	}

	public void markAsPaymentRequired() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
//		transaction.getCreditCardDetails().saveDetails();
//
//		// You have to save it in the customer because the payment may not have been taken yet
//		// it will be removed at a later stage if required. AM.
//		transaction.getCustomer().setCreditCardDetails(transaction.getCreditCardDetails());
//		transaction.getCustomer().saveDetails();


		transaction.makeOnlinePaymentCall( false, null, false );
		if( transaction.getAuthenticatedPaymentGatewayDirectPost() != null ) {
			transaction.setTransactionStatus(TransactionStatus.PAYMENT_REQUIRED);
		}
		transaction.saveDetails();
	}

	public String getAdminChargeText() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		BigDecimal adminCharge = transaction.getEcommerceShoppingCart().getAdminCharge();
		if( adminCharge.compareTo( new BigDecimal( 0 ) ) <= 0 ) {
			return "No admin charge";
		} else {
			return "An admin charge of £" + transaction.getEcommerceShoppingCart().getAdminChargeString() + " applies";
		}
	}

//	public void markAsAuthorised() {
//		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
//		if( EcommerceConfiguration.getEcommerceConfiguration().getCreditCardPaymentMethod().equals( transaction.getPaymentMethod() ) ) {
//			EcommerceShoppingCartItem cartItem = transaction.getEcommerceShoppingCart().addToCart( EcommerceConfiguration.getEcommerceConfiguration().getAdminChargeRealizedProduct(), true );
//			BigDecimal creditCardFeePercentage = EcommerceConfiguration.getEcommerceSettingsStatic().getCreditCardFeePercentage();
//			cartItem.setCustomisable(true);
//			cartItem.setSingleItemBasePrice( creditCardFeePercentage.multiply( transaction.getEcommerceShoppingCart().getCachedGrossTotalAmount() ).divide( new BigDecimal( 100 ) ) );
//			cartItem.updateAllValues();
//			JSFUtil.addMessage( "Admin charge has been added" );
//		} else if( transaction.getBillingAddress().getCountry().getVatType() != null &&
//				!transaction.getBillingAddress().getCountry().getVatType().equals( CommonConfiguration.getCommonConfiguration().getUkVatType() ) &&
//						(EcommerceConfiguration.getEcommerceConfiguration().getChequePaymentMethod().equals( transaction.getPaymentMethod() ) ||
//						EcommerceConfiguration.getEcommerceConfiguration().getBankTransferPaymentMethod().equals( transaction.getPaymentMethod() )) ) {
//			EcommerceShoppingCartItem cartItem = transaction.getEcommerceShoppingCart().addToCart( EcommerceConfiguration.getEcommerceConfiguration().getAdminChargeRealizedProduct(), true );
//			cartItem.setSingleItemBasePrice( EcommerceConfiguration.getEcommerceSettingsStatic().getOverseasAdminCharge() );
//			cartItem.setCustomisable(true);
//			cartItem.updateAllValues();
//			JSFUtil.addMessage( "Admin charge has been added" );
//		}
//		transaction.setTransactionStatus(TransactionStatus.AUTHORISED);
//		transaction.saveDetails();
//	}

	public void markAsAcknowledged() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		if( TransactionType.REFUND.equals( transaction.getTransactionType() ) ) {
			transaction.setTransactionStatus(TransactionStatus.DISPATCHED);
			transaction.createInvoiceNumber();
		} else {
			Customer customer = transaction.getCustomer();

			if( transaction.getPaymentMethod() == null ) {
				JSFUtil.addMessage("Please select a payment method", FacesMessage.SEVERITY_ERROR);
				return;
			} else if( EcommerceConfiguration.getEcommerceConfiguration().getCreditCardPaymentMethod().equals( transaction.getPaymentMethod() ) ) {
				transaction.getCreditCardDetails().saveDetails();

				if( customer.isRememberCreditCardDetails() ) {
					customer.setCreditCardDetails(transaction.getCreditCardDetails());
					customer.saveDetails();
				} else {
					customer.setCreditCardDetails( null );
					customer.saveDetails();
				}

				transaction.saveDetails();
			} else if( EcommerceConfiguration.getEcommerceConfiguration().getAccountPaymentMethod().equals( transaction.getPaymentMethod() ) ) {
				boolean isCustomerACompanyContact = customer instanceof CompanyContact;

				if (isCustomerACompanyContact) {
					if ( ((CompanyContact)customer).getCompany().getCreditLimit() == null || ((CompanyContact)customer).getCompany().getCreditLimit().compareTo( transaction.getEcommerceShoppingCart().getGrandTotal(true) ) < 0 ) {
						JSFUtil.addMessage("That company does not have enough credit to acknowledge this transaction (£" +
								((CompanyContact)customer).getCompany().getCreditLimit() + " remaining).", FacesMessage.SEVERITY_ERROR);
						// TODO Need to calculate the what is already on the account
						return;
					}
				}
			}

			//transaction.setTransactionStatus(TransactionStatus.ACKNOWLEDGED);
			transaction.paymentComplete(false, null);

		}
		transaction.saveDetails();
	}

	public boolean isSummaryValidationRequired() {
		if( validationRequired( "saveCommandBtn" ) || validationRequired( "editPageOkBtn" ) || validationRequired("acknowledgeBtn") || validationRequired("processPaymentBtn") ) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isValidationRequired() {
		Transaction transaction = JSFUtil.getBeanFromScope( Transaction.class );
		if( (validationRequired("acknowledgeBtn") || validationRequired("processPaymentBtn") || validationRequired("paymentRequiredBtn") ) && transaction.isShowingCardDetails() ) {
			return true;
		} else {
			return false;
		}
	}

	public List<SerialNumber> getSerialNumberAssignments(EcommerceShoppingCartItem ecommerceShoppingCartItem) {
		List<SerialNumber> serialNumberAssignments = new ArrayList<SerialNumber>();
		if( ecommerceShoppingCartItem != null ) {
			int listSize = ecommerceShoppingCartItem.getSerialNumberAssignments().size();
			for (int i=0 ; i<ecommerceShoppingCartItem.getQuantity() ; i++) {
				if (i >= listSize) {
					break;
				}
				serialNumberAssignments.add(ecommerceShoppingCartItem.getSerialNumberAssignments().get(i));
			}
			serialNumberAssignmentSize = serialNumberAssignments.size();
		}
		return serialNumberAssignments;
	}

	public int getSerialNumberAssignmentSize() {
		return serialNumberAssignmentSize;
	}

	public int getSentEmailsSize() {
		return sentEmailsSize;
	}

	public void goToSerialNumberAssignment() {
		SerialNumber serialNumberAssignment = (SerialNumber) JSFUtil.getRequest().getAttribute( "serialNumberAssignment" );
		SerialNumber loadedSerialNumberAssignment = new BeanDao( SerialNumber.class ).get( serialNumberAssignment.getId() );
		loadedSerialNumberAssignment.addToScope();
		JSFUtil.redirect( SerialNumberEditPage.class );
	}

	public String createNewReturn(SerialNumber serialNumber) {
		SerialNumber loadedSerialNumber = new BeanDao( SerialNumber.class ).get( serialNumber.getId() );
//		HibernateUtil.initialise( loadedSerialNumber, true );
		RealizedProductReturnEditPage.createNewReturn(loadedSerialNumber);
		JSFUtil.redirect(Website.getCurrentWebsiteFromTabSession().getPackageName(), RealizedProductReturnEditPage.class);
		return null;
	}

	public String redirectToCompanyEdit() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		Company company = ((CompanyContact) transaction.getCustomer()).getCompany();
		company.addToScope();

		JSFUtil.redirect(Website.getCurrentWebsiteFromTabSession().getPackageName(), CompanyEditPage.class);
		return null;
	}

	public List<SelectItem> getPrintSendSelectItems() {
		List<SelectItem> printSendSelectItems = new ArrayList<SelectItem>();
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		if( !transaction.getIsCancelled() ) {
			if( transaction.getIsAwaitingDispatchOrFurther() ) {
				printSendSelectItems.add( new SelectItem( PrintSendAction.DISPATCH_NOTE, "Dispatch Note") );
				printSendSelectItems.add( new SelectItem( PrintSendAction.INVOICE, "Invoice") );
				printSendSelectItems.add( new SelectItem( PrintSendAction.DISPATCH_INVOICE, "Dispatch/Invoice") );
			}

			if( transaction.getIsAcknowledgedOrFurther() ) {
				printSendSelectItems.add( new SelectItem( PrintSendAction.ACKNOWLEDGEMENT, "Acknowledgement") );
			}

			if( transaction.getIsDispatchedOrFurther() ) {
				printSendSelectItems.add( new SelectItem( PrintSendAction.COURIER_DETAILS, "Courier details") );
			}

			if( !transaction.isNew() && transaction.getTransactionType().getEmailTemplateEnum() != null ) {
				printSendSelectItems.add( new SelectItem( PrintSendAction.TRANSACTION, transaction.getTransactionTypeLabel()) );
			}
		} else {
			printSendSelectItems.add( new SelectItem( PrintSendAction.CANCELLATION_NOTICE, PrintSendAction.CANCELLATION_NOTICE.label) );
		}
		if( selectedPrintSendAction == null && printSendSelectItems.size() > 0 ) {
			setSelectedPrintSendAction( (PrintSendAction) printSendSelectItems.get( 0 ).getValue() );
		}

		return printSendSelectItems;
	}

	public void redirectToAplosEmail() {
		AplosEmail aplosEmail = (AplosEmail) JSFUtil.getRequest().getAttribute( "aplosEmail" );
		aplosEmail.addToScope();
		JSFUtil.redirect( AplosEmailEditPage.class );
//		PrintSendPage.redirect( TransactionPrintSendPage.class, printedSentStage );
	}

	public void performPrintSendAction() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		transaction.saveDetails();
		if( selectedPrintSendAction != null ) {
			AplosEmail aplosEmail = null; 
			if( selectedPrintSendAction.equals( PrintSendAction.DISPATCH_NOTE ) ) {
				aplosEmail = new AplosEmail( EcommerceEmailTemplateEnum.DISPATCH_NOTE, transaction, transaction );
			} else if( selectedPrintSendAction.equals( PrintSendAction.BOOKING_OUT_SHEET ) ) {
				redirectToPrintBookingOutSheetUrl();
				return;
			} else if( selectedPrintSendAction.equals( PrintSendAction.INVOICE ) ) {
				aplosEmail = new AplosEmail( EcommerceEmailTemplateEnum.INVOICE, transaction, transaction );
			} else if( selectedPrintSendAction.equals( PrintSendAction.ACKNOWLEDGEMENT ) ) {
				aplosEmail = new AplosEmail( EcommerceEmailTemplateEnum.ACKNOWLEDGEMENT, transaction, transaction );
			} else if( selectedPrintSendAction.equals( PrintSendAction.TRANSACTION ) ) {
				aplosEmail = new AplosEmail( transaction.getTransactionType().getEmailTemplateEnum(), transaction, transaction );
			} else if( selectedPrintSendAction.equals( PrintSendAction.DISPATCH_INVOICE ) ) {
				aplosEmail = new AplosEmail( EcommerceEmailTemplateEnum.DISPATCH_INVOICE, transaction, transaction );
			} else if( selectedPrintSendAction.equals( PrintSendAction.COURIER_DETAILS ) ) {
				aplosEmail = new AplosEmail( EcommerceEmailTemplateEnum.COURIER_DETAILS, transaction, transaction );
			} else if( selectedPrintSendAction.equals( PrintSendAction.CANCELLATION_NOTICE ) ) {
				aplosEmail = new AplosEmail( EcommerceEmailTemplateEnum.CANCELLATION_NOTICE, transaction, transaction );
			}

			if( aplosEmail != null ) {
				aplosEmail.addEmailFolder(transaction);
				aplosEmail.addToScope();
				JSFUtil.redirect(AplosEmailEditPage.class);
			}

//			PrintSendPage.redirect( TransactionPrintSendPage.class, transactionTemplate, transactionEmail, printedSentStage );
		} else {
			JSFUtil.addMessage( "Please select a print send option" );
		}
	}

	public boolean isCustomerACompanyContact() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		return isCustomerACompanyContact( transaction.getEcommerceShoppingCart().getCustomer() );
	}

	public boolean isCustomerACompanyContact( Customer customer ) {
		if (customer instanceof CompanyContact) {
			return true;
		} else {
			return false;
		}
	}
	
	public Company getCustomerCompany() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		if (transaction != null && transaction.getCustomer() != null && transaction.getCustomer() instanceof CompanyContact) {
			return ((CompanyContact)transaction.getCustomer()).getCompany();
		}
		return null;
	}

	public void redirectToTransactionEditPage() {
		JSFUtil.redirect(TransactionEditPage.class);
	}

	public void redirectToCustomerEditPage() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		Customer customer = transaction.getEcommerceShoppingCart().getCustomer();
		customer.addToScope();
		if (customer instanceof CompanyContact) {
			JSFUtil.redirect(CompanyContactEditPage.class);
		}
		else {
			JSFUtil.redirect(CustomerEditPage.class);
		}
	}

	public void setSelectedPrintSendAction(PrintSendActionInter selectedPrintSendAction) {
		this.selectedPrintSendAction = selectedPrintSendAction;
	}

	public PrintSendActionInter getSelectedPrintSendAction() {
		return selectedPrintSendAction;
	}

	public List<SelectItem> getCreditCardSelectItems() {
		return creditCardSelectItems;
	}

	public void setCreditCardSelectItems(List<SelectItem> creditCardSelectItems) {
		this.creditCardSelectItems = creditCardSelectItems;
	}
}
