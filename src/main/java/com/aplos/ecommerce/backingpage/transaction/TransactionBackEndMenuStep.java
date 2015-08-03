package com.aplos.ecommerce.backingpage.transaction;

import com.aplos.common.beans.MenuStep;
import com.aplos.common.beans.MenuWizard;
import com.aplos.common.interfaces.MenuStepBacking;
import com.aplos.ecommerce.beans.Transaction;

public class TransactionBackEndMenuStep extends MenuStep {
	private static final long serialVersionUID = 2137834704764217501L;

	public TransactionBackEndMenuStep( MenuWizard menuWizard, int idx, String label, String viewUrl, Class<? extends MenuStepBacking> menuStepBackingClass ) {
		super(menuWizard, idx, label, viewUrl, menuStepBackingClass);
	}

	@Override
	public boolean isClickable() {
		if( ((Transaction) getMenuWizard().getParent()).getIsAwaitingDispatchOrFurther() && getIdx() < 4 ) {
			return false;
		} else if( ((Transaction) getMenuWizard().getParent()).getIsAcknowledgedOrFurther() && getIdx() < 2 ) {
			return false;
		} else {
			return super.isClickable();
		}
	}
}
