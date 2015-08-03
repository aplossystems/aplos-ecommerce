package com.aplos.ecommerce.backingpage.marketing;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.backingpage.marketing.CompetitionPrizeWinnerEditPage;
import com.aplos.cms.beans.CompetitionPrizeWinner;
import com.aplos.common.annotations.BackingPageOverride;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.backingpage.transaction.TransactionEditPage;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.Transaction;

@ManagedBean
@ViewScoped
@BackingPageOverride(backingPageClass=CompetitionPrizeWinnerEditPage.class)
public class EcommerceCompetitionPrizeWinnerEditPage extends CompetitionPrizeWinnerEditPage {
	private static final long serialVersionUID = 8961222130021273267L;

	public void createTransaction() {
		CompetitionPrizeWinner competitionPrizeWinner = resolveAssociatedBean();
		Customer customer = Customer.getCustomerByEmailAddress( competitionPrizeWinner.getSubscriber().getEmailAddress() );
		if( customer == null ) {
			customer = Customer.getCustomerOrCreate( competitionPrizeWinner.getSubscriber().getEmailAddress(), Customer.class );
			customer.getShippingAddress().copy( competitionPrizeWinner.getAddress() );
			customer.getBillingAddress().copy( competitionPrizeWinner.getAddress() );
			customer.saveDetails();
		}

		Transaction transaction = Transaction.createNewTransaction(customer, true);
		transaction.addToScope();
		JSFUtil.redirect(TransactionEditPage.class);
	}

}
