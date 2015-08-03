package com.aplos.ecommerce.developermodulebacking.frontend;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.beans.MenuStep;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.backingpage.transaction.TransactionFrontEndMenuWizard;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.utils.EcommerceUtil;

@ManagedBean
@ViewScoped
public class CheckoutWizardFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = 456327888534614016L;

	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		super.responsePageLoad(developerCmsAtom);
		
		Customer customer = JSFUtil.getBeanFromScope(Customer.class);
		if ( EcommerceUtil.getEcommerceUtil().checkCheckoutAccess(customer, "the checkout process" ) ) {

			Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
			if( transaction.getFrontEndMenuWizard() == null ) {
				transaction.setFrontEndMenuWizard( new TransactionFrontEndMenuWizard() );
			}

			transaction.getFrontEndMenuWizard().refreshMenu( transaction );

			for( MenuStep menuStep : transaction.getFrontEndMenuWizard().getMenuSteps() ) {
				if( menuStep.getViewUrl().contains( JSFUtil.getAplosContextOriginalUrl() ) ) {
					transaction.getFrontEndMenuWizard().setCurrentStepIdx( menuStep.getIdx() );
					if( menuStep.getIdx() > transaction.getFrontEndMenuWizard().getLatestStepIdx() ) {
						transaction.getFrontEndMenuWizard().setLatestStepIdx( menuStep.getIdx() );
					}
				}
			}
		} else {
			return false;
		}
		return true;
	}
}
