package com.aplos.ecommerce.backingpage.transaction;

import java.util.ArrayList;

import javax.faces.application.FacesMessage;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.MenuStep;
import com.aplos.common.beans.MenuWizard;
import com.aplos.common.interfaces.MenuStepBacking;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.Transaction;

@Entity
public class TransactionBackEndMenuWizard extends MenuWizard {
	private static final long serialVersionUID = 8822584808009775293L;

	@Override
	protected void createMenuSteps() {
		setMenuSteps( new ArrayList<MenuStep>() );
		int idx = 0;
		getMenuSteps().add( new TransactionEditAddItemsMenuStep( this, idx++, "Add items", "transactionEditAddItems", TransactionEditAddItemsPage.class ) );
		getMenuSteps().add( new TransactionBackEndMenuStep( this, idx++, "Billing address", "transactionEditBillingDetails", TransactionEditBillingDetailsPage.class ));
		getMenuSteps().add( new TransactionBackEndMenuStep( this, idx++, "Delivery address", "transactionEditDeliveryDetails", TransactionEditDeliveryDetailsPage.class ));
		getMenuSteps().add( new TransactionBackEndMenuStep( this, idx++, "Delivery method", "transactionEditDeliveryMethod", TransactionEditDeliveryMethodPage.class ));
		getMenuSteps().add( new TransactionBackEndMenuStep( this, idx++, "Summary", "transactionEditSummary", null ));
	}

	public class TransactionEditAddItemsMenuStep extends TransactionBackEndMenuStep {

		public TransactionEditAddItemsMenuStep(MenuWizard menuWizard, int idx, String label, String viewUrl, Class<? extends MenuStepBacking> menuStepBackingClass) {
			super(menuWizard, idx, label, viewUrl, menuStepBackingClass);
		}

		@Override
		public boolean validate( MenuStep nextMenuStep ) {
			Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
			if (transaction.getEcommerceShoppingCart().getItems().size() > 0 || transaction.getRealizedProductReturn() != null ) {
				return true;
			}
			else {
				JSFUtil.addMessage("The item list is empty", FacesMessage.SEVERITY_ERROR);
				return false;
			}
		}
	}
}
