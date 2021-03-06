package com.aplos.ecommerce.backingpage.shipping;

import java.math.BigDecimal;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.appconstants.AplosScopedBindings;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.couriers.AdditionalShippingOption;
import com.aplos.ecommerce.beans.couriers.ApcUkShippingService;
import com.aplos.ecommerce.beans.couriers.CourierService;

@ManagedBean
@SessionScoped
@AssociatedBean(beanClass=ApcUkShippingService.class)
public class ApcUkShippingServiceEditPage extends EditPage {

	private static final long serialVersionUID = -2966284916231518450L;
	private String optionName;
	private BigDecimal optionCost;

	public ApcUkShippingServiceEditPage() {
		getBeanDao().setListPageClass( CourierShippingServiceListPage.class );
	}

	@Override
	public void applyBtnAction() {
		saveAction();
	}

	@Override
	public void okBtnAction() {
		saveAction();
		super.cancelBtnAction();
	}

	public void saveAction() {
		ApcUkShippingService apcUkShippingService = JSFUtil.getBeanFromScope( ApcUkShippingService.class );
		boolean wasNew = apcUkShippingService.isNew();
		super.applyBtnAction();
		if( wasNew ) {
			CourierService courierService = JSFUtil.getBeanFromScope( CourierService.class );
			courierService.getShippingServices().add( apcUkShippingService );
			courierService.saveDetails();
		}
	}

	public void addAdditonalOption() {
		ApcUkShippingService apcUkShippingService = (ApcUkShippingService) resolveAssociatedBean();
		AdditionalShippingOption additionalShippingOption = new AdditionalShippingOption( optionName, optionCost, "" );
		apcUkShippingService.getAdditionalShippingOptions().add( additionalShippingOption );

		//maxWeight = cost = 0d;

		if( !apcUkShippingService.isNew() ) {
			apcUkShippingService.saveDetails();
		}
	}

	public void removeAdditionalOption() {
		ApcUkShippingService apcUkShippingService = (ApcUkShippingService) resolveAssociatedBean();
		apcUkShippingService.getAdditionalShippingOptions().remove( JSFUtil.getRequest().getAttribute( AplosScopedBindings.TABLE_BEAN ) );
		apcUkShippingService.saveDetails();
	}

	public void setOptionName(String optionName) {
		this.optionName = optionName;
	}

	public String getOptionName() {
		return optionName;
	}

	public void setOptionCost(BigDecimal optionCost) {
		this.optionCost = optionCost;
	}

	public BigDecimal getOptionCost() {
		return optionCost;
	}
}
