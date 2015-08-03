package com.aplos.ecommerce.backingpage.transaction;

import com.aplos.common.AplosUrl;
import com.aplos.common.beans.MenuStep;
import com.aplos.common.beans.MenuWizard;
import com.aplos.common.interfaces.MenuStepBacking;
import com.aplos.common.utils.JSFUtil;

public class TransactionFrontEndMenuStep extends MenuStep {
	public TransactionFrontEndMenuStep( MenuWizard menuWizard, int idx, String label, String viewUrl, Class<? extends MenuStepBacking> menuStepBackingClass ) {
		super(menuWizard, idx, label, viewUrl, menuStepBackingClass);
	}

	@Override
	public boolean isClickable() {
		if( getIdx() == getMenuWizard().getCurrentStepIdx() ) {
			return false;
		} else {
			return super.isClickable();
		}
	}

	@Override
	public void redirectToMenuStep() {
		super.redirectToMenuStep();
		JSFUtil.redirect(new AplosUrl(getMenuWizard().getMenuSteps().get( getMenuWizard().getCurrentStepIdx() ).getViewUrl()), false );
	}
}
