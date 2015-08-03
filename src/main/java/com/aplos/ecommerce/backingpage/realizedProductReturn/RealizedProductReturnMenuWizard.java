package com.aplos.ecommerce.backingpage.realizedProductReturn;

import java.util.ArrayList;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.MenuStep;
import com.aplos.common.beans.MenuWizard;
import com.aplos.common.interfaces.MenuStepBacking;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.CompanyContact;
import com.aplos.ecommerce.beans.RealizedProductReturn;

@Entity
public class RealizedProductReturnMenuWizard extends MenuWizard {
	private static final long serialVersionUID = 8822584808009775293L;

	@Override
	protected void createMenuSteps() {
		setMenuSteps( new ArrayList<MenuStep>() );
		int idx = 0;
		getMenuSteps().add( new MenuStep( this, idx++, "Set Product", "returnsSetProductEdit", null ) );
		getMenuSteps().add( new TechnicalSupportMenuStep( this, idx++, "Offer Technical Support", "returnsTechinalSupportEdit", null ) );

		RealizedProductReturn realizedProductReturn = (RealizedProductReturn)getParent();
		if ( realizedProductReturn.getSerialNumber() != null && (realizedProductReturn.getSerialNumber().getOriginalCustomer() == null || realizedProductReturn.getSerialNumber().getOriginalCustomer() instanceof CompanyContact)) {
			getMenuSteps().add( new MenuStep( this, idx++, "Current customer", "returnsEndUserCompanyEdit", null ) );
		}
		getMenuSteps().add( new DeliveryAddressMenuStep( this, idx++, "Contact & Delivery Details", "returnsDeliveryAddressEdit", null ));

		getMenuSteps().add( new MenuStep( this, idx++, "Returns Authorisation Note", "returnsAuthorisationNoteEdit", null) );
		getMenuSteps().add( new ConditionRecievedReportMenuStep( this, idx++, "Condition Received Report", "returnsConditionRecievedReportEdit", null));
		getMenuSteps().add( new MenuStep( this, idx++, "Repair the Product", "returnsRepairProductEdit", null));
		getMenuSteps().add( new MenuStep( this, idx++, "Repair Report", "returnsRepairReportEdit", null) );
		getMenuSteps().add( new MenuStep( this, idx++, "Calibration Certificate", "returnsCalibrationCertificateEdit", null));
		getMenuSteps().add( new MenuStep( this, idx++, "Dispatch Details", "returnsDispatchDetailsEdit", null) );
	}

	public class DeliveryAddressMenuStep extends MenuStep {

		public DeliveryAddressMenuStep(MenuWizard menuWizard, int idx, String label, String viewUrl, Class<? extends MenuStepBacking> menuStepBackingClass) {
			super(menuWizard, idx, label, viewUrl, menuStepBackingClass);
		}

//		@Override
//		public void initStep() {
//			RealizedProductReturn realizedProductReturn = (RealizedProductReturn)JSFUtil.getBeanFromScope(RealizedProductReturn.class);
//			if (realizedProductReturn.getReturnAddress() != null && realizedProductReturn.getReturnAddress().getContactFirstName() == null) {
//				realizedProductReturn.setReturnAddressFromEndCustomer();
//			}
//		};
	}

	public class ConditionRecievedReportMenuStep extends MenuStep {

		public ConditionRecievedReportMenuStep(MenuWizard menuWizard, int idx, String label, String viewUrl, Class<? extends MenuStepBacking> menuStepBackingClass) {
			super(menuWizard, idx, label, viewUrl, menuStepBackingClass);
		}

		@Override
		public void redirectToMenuStep() {
			RealizedProductReturn realizedProductReturn = (RealizedProductReturn)JSFUtil.getBeanFromScope(RealizedProductReturn.class);
			if( realizedProductReturn.getSerialNumber() == null ) {
				JSFUtil.addMessage( "Please select a serial number before continuing" );
			} else {
				super.redirectToMenuStep();
			}
		}
	}

	public class TechnicalSupportMenuStep extends MenuStep {

		public TechnicalSupportMenuStep(MenuWizard menuWizard, int idx, String label, String viewUrl, Class<? extends MenuStepBacking> menuStepBackingClass) {
			super(menuWizard, idx, label, viewUrl, menuStepBackingClass);
		}

		@Override
		public void redirectToMenuStep() {
			RealizedProductReturn realizedProductReturn = (RealizedProductReturn)JSFUtil.getBeanFromScope(RealizedProductReturn.class);
			if( realizedProductReturn.determineReturnProduct() == null ) {
				JSFUtil.addMessage( "Please select a serial number or product before continuing" );
			} else {
				super.redirectToMenuStep();
			}
		}
	}
}
