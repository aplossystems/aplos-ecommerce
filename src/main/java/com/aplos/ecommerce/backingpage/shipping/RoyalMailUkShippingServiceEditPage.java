package com.aplos.ecommerce.backingpage.shipping;

import java.math.BigDecimal;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.appconstants.AplosScopedBindings;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.couriers.AdditionalShippingOption;
import com.aplos.ecommerce.beans.couriers.CourierService;
import com.aplos.ecommerce.beans.couriers.RoyalMailUkShippingService;

@ManagedBean
@SessionScoped
@AssociatedBean(beanClass=RoyalMailUkShippingService.class)
public class RoyalMailUkShippingServiceEditPage extends EditPage {

	private static final long serialVersionUID = 8141701607138201970L;
	private String optionName;
	private BigDecimal optionCost;

	public RoyalMailUkShippingServiceEditPage() {
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
		RoyalMailUkShippingService royalMailUkShippingService = JSFUtil.getBeanFromScope( RoyalMailUkShippingService.class );
		boolean wasNew = royalMailUkShippingService.isNew();
		super.applyBtnAction();
		if( wasNew ) {
			CourierService courierService = JSFUtil.getBeanFromScope( CourierService.class );
			courierService.getShippingServices().add( royalMailUkShippingService );
			courierService.saveDetails();
		}
	}

	public void addAdditonalOption() {
		RoyalMailUkShippingService royalMailUkShippingService = (RoyalMailUkShippingService) resolveAssociatedBean();
		AdditionalShippingOption additionalShippingOption = new AdditionalShippingOption( optionName, optionCost, "" );
		royalMailUkShippingService.getAdditionalShippingOptions().add( additionalShippingOption );

		if( !royalMailUkShippingService.isNew() ) {
			royalMailUkShippingService.saveDetails();
		}
	}

	public void removeAdditionalOption() {
		RoyalMailUkShippingService royalMailUkShippingService = (RoyalMailUkShippingService) resolveAssociatedBean();
		royalMailUkShippingService.getAdditionalShippingOptions().remove( JSFUtil.getRequest().getAttribute( AplosScopedBindings.TABLE_BEAN ) );
		royalMailUkShippingService.saveDetails();
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
