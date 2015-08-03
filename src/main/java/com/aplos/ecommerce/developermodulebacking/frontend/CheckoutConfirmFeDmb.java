package com.aplos.ecommerce.developermodulebacking.frontend;

import java.math.BigDecimal;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.beans.CreditCardDetails;
import com.aplos.common.beans.communication.AplosEmail;
import com.aplos.common.enums.CartAbandonmentIssue;
import com.aplos.common.enums.CheckoutPageEntry;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.CompanyContact;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.EcommerceShoppingCartItem;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.beans.Transaction.TransactionStatus;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;
import com.aplos.ecommerce.enums.TransactionType;
import com.aplos.ecommerce.module.EcommerceConfiguration;
import com.aplos.ecommerce.utils.EcommerceUtil;

@ManagedBean
@ViewScoped
public class CheckoutConfirmFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = 375811172503243915L;

	@Override
	public boolean genericPageLoad( DeveloperCmsAtom developerCmsAtom ) {
		super.genericPageLoad(developerCmsAtom);
		EcommerceUtil.getEcommerceUtil().addCheckoutPageEntry( CheckoutPageEntry.CONFIRM );
		Customer customer = JSFUtil.getBeanFromScope(Customer.class);
		return EcommerceUtil.getEcommerceUtil().checkCheckoutAccess(customer, "the checkout process" );
	}

	protected void processPaymentWhenValidForFrontEndPayment(Transaction transaction) {
		processPaymentWhenValidForFrontEndPayment(transaction, true, null );
	}
	
	protected void processPaymentWhenValidForFrontEndPayment(Transaction transaction, boolean isPaymentMadeNow, BigDecimal partPayment ) {
		transaction.makeOnlinePaymentCall(isPaymentMadeNow, partPayment, true);
	}
	
	protected void processPaymentWhenPaymentNotRequired(Transaction transaction) {
		EcommerceUtil.getEcommerceUtil().executePaymentCompleteRoutine(transaction, true, null);
	}
	
	protected void processPaymentFallback(Transaction transaction) {
		boolean customerHasCreditAccount = transaction.getCustomer() instanceof CompanyContact &&
		((CompanyContact) transaction.getCustomer()).getCompany().isCreditAllowed();
		transaction.setTransactionType( TransactionType.PRO_FORMA );

		transaction.setTransactionStatus( TransactionStatus.NEW );
		if( transaction.getBackEndMenuWizard().getLatestStepIdx() < 4 ) {
			transaction.getBackEndMenuWizard().setLatestStepIdx( 4 );
			transaction.getBackEndMenuWizard().setCurrentStepIdx( 4 );
		}

		transaction.saveDetails();

		if( customerHasCreditAccount && EcommerceConfiguration.getEcommerceConfiguration().getAccountPaymentMethod().equals( transaction.getPaymentMethod() ) &&
				((CompanyContact)transaction.getCustomer()).getCompany().getCreditLimit().compareTo( transaction.getEcommerceShoppingCart().getCachedNetTotalAmount() ) > 0 ) {
			transaction.setTransactionStatus( TransactionStatus.ACKNOWLEDGED );
			
			transaction.sendConfirmationEmail();
		} else {
			AplosEmail.sendEmail( EcommerceEmailTemplateEnum.AWAITING_AUTHORISATION, transaction );
		}
		transaction.reevaluateOrderObjectsSession();
		EcommerceUtil.getEcommerceUtil().getOrCreateEcommerceShoppingCart();
		EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutAwaitingAuthorisationCpr();
	}
	
	protected void processPaymentWhenAlreadyPaid(Transaction transaction) {
		EcommerceUtil.getEcommerceUtil().addAbandonmentIssueToCart( CartAbandonmentIssue.PAYMENT_ALREADY_MADE );
		transaction.reevaluateOrderObjectsSession();
		EcommerceUtil.getEcommerceUtil().getOrCreateEcommerceShoppingCart();
		ApplicationUtil.getAplosContextListener().handleError( new Exception( "Payment already made" ) );
		EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutPaymentAlreadyMadeCpr();
	}

	public void processPayPalPayment() {
		Transaction transaction = getLoadedTransaction();
		transaction.setPaymentMethod(EcommerceConfiguration.getEcommerceConfiguration().getPayPalPaymentMethod());
		processPayment( transaction );
	}

	public void processPayment() {
		processPayment( getLoadedTransaction() );
	}
	
	public Transaction getLoadedTransaction() {
		Transaction transaction = (Transaction)JSFUtil.getBeanFromScope(Transaction.class);
		/*
		 *  This is reloaded as otherwise the email will crash when you try to save it as the
		 *  subscriber is not in the session and a duplicate id error occurs. 
		 */
		CreditCardDetails creditCardDetails = transaction.determineCreditCardDetails();
		/*
		 * You have to save the transaction to make sure that the changes made are reloaded
		 * from the database.
		 */
		transaction.saveDetails();
		
		if( transaction.isUsingStoredCreditCardDetails() ) {
			transaction.getCustomer().getCreditCardDetails().setCvv(creditCardDetails.getCvv());
		} else {
			transaction.setCreditCardDetails(creditCardDetails);
		}
		return transaction;
	}

	public void processPayment( Transaction transaction ) {
		if( !transaction.isAlreadyPaid() ) {
			//we cant put abandonment code into the 3d page so we just record that we went there
			//in the assumption that if it gets abandoned after clicking through to payment
			//then its because of a payment error
			if( EcommerceUtil.getEcommerceUtil().isValidForFrontEndPayment( transaction ) ) {
				processPaymentWhenValidForFrontEndPayment(transaction);
			} else if (EcommerceConfiguration.getEcommerceConfiguration().getNotRequiredPaymentMethod().equals( transaction.getPaymentMethod() )
					//handle cases where we pay fully in store credit
					&& transaction.getEcommerceShoppingCart().getCachedGrossTotalAmount().compareTo(transaction.getEcommerceShoppingCart().getStoreCreditAvailable()) == 0) {
					//alternatively check total to pay  is 0 && transaction.getEcommerceShoppingCart().getGrandTotal(true).compareTo(new BigDecimal(0)) == 0) {
				processPaymentWhenPaymentNotRequired(transaction);
			} else {
				processPaymentFallback(transaction);
			}
		} else {
			processPaymentWhenAlreadyPaid(transaction);
		}
	}

	public static boolean isViewingConfirmPage() {
		if (EcommerceConfiguration.getEcommerceCprsStatic().getCheckoutConfirmationCpr() == null) {
			return false;
		}
		return JSFUtil.getAplosContextOriginalUrl().equals("/" + EcommerceConfiguration.getEcommerceCprsStatic().getCheckoutConfirmationCpr().getCmsPage().getMapping() + ".aplos");
	}

	public boolean getIsViewingConfirmPage() {
		return isViewingConfirmPage();
	}

	public String goToCheckoutPayment() {
		EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutPaymentCpr();
		return null;
	}

	public boolean getIsAllStockAvailable() {
		boolean available = ((Transaction)JSFUtil.getBeanFromScope(Transaction.class)).checkAllStockIsAvailable();
		if (!available) {
			EcommerceUtil.getEcommerceUtil().addAbandonmentIssueToCart( CartAbandonmentIssue.INSUFFICIENT_STOCK_WARNING );
		}
		return available;
	}

	public String getPreviousBtnText() {
		Transaction transaction = JSFUtil.getBeanFromScope( Transaction.class );
		if( transaction.getGrandTotal(true).compareTo( new BigDecimal( 0 ) ) == 0 ) {
			return "Back";
		} else {
			return "Return to payment";
		}
	}

	public String getNextBtnText() {
		return "Confirm order";
	}

	public ArrayList<EcommerceShoppingCartItem> getInsufficientStockForOrderList() {
		return ((Transaction)JSFUtil.getBeanFromScope(Transaction.class)).getInsufficientStockForOrderList();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void removeFromOrder() {
		EcommerceShoppingCartItem esci = (EcommerceShoppingCartItem)JSFUtil.getRequest().getAttribute("issueItem");
		if (esci != null) {
			Transaction ecomOrd = JSFUtil.getBeanFromScope(Transaction.class);
			if (ecomOrd != null) {
				ArrayList<EcommerceShoppingCartItem> cartItems = new ArrayList(ecomOrd.getEcommerceShoppingCart().getItems());
				for (int i=0; i < cartItems.size(); i++) {
					if (cartItems.get(i).getRealizedProduct().getId() == esci.getRealizedProduct().getId()) {
						ecomOrd.getEcommerceShoppingCart().getItems().remove(i);
						ecomOrd.saveDetails();
						break;
					}
				}
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void reduceOrderToStockLevel() {
		EcommerceShoppingCartItem esci = (EcommerceShoppingCartItem)JSFUtil.getRequest().getAttribute("issueItem");
		if (esci != null) {
			Transaction ecomOrd = JSFUtil.getBeanFromScope(Transaction.class);
			if (ecomOrd != null) {
				ArrayList<EcommerceShoppingCartItem> cartItems = new ArrayList(ecomOrd.getEcommerceShoppingCart().getItems());
				for (int i=0; i < cartItems.size(); i++) {
					if (cartItems.get(i).getRealizedProduct().getId() == esci.getRealizedProduct().getId()) {
						ecomOrd.getEcommerceShoppingCart().getItems().get(i).setQuantity(esci.getRealizedProduct().getQuantity());
						ecomOrd.saveDetails();
						break;
					}
				}
			}
		}
	}

}










