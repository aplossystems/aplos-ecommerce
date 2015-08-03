package com.aplos.ecommerce.backingpage.shipping;

import java.math.BigDecimal;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.appconstants.AplosScopedBindings;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.PostalZone;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.couriers.CourierService;
import com.aplos.ecommerce.beans.couriers.PostalZoneAdditionalShippingOption;
import com.aplos.ecommerce.beans.couriers.PostalZoneInternationalShippingService;

@ManagedBean
@SessionScoped
@AssociatedBean(beanClass=PostalZoneInternationalShippingService.class)
public class PostalZoneInternationalShippingServiceEditPage extends EditPage {

	private static final long serialVersionUID = -6837882668015955211L;
	private String optionName;
	private BigDecimal optionCost;
	private String optionDeliveryTime;
	private PostalZone optionPostalZone;

	public PostalZoneInternationalShippingServiceEditPage() {
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
		PostalZoneInternationalShippingService postalZoneInternationalShippingService = JSFUtil.getBeanFromScope( PostalZoneInternationalShippingService.class );
		boolean wasNew = postalZoneInternationalShippingService.isNew();
		super.applyBtnAction();
		if( wasNew ) {
			CourierService courierService = JSFUtil.getBeanFromScope( CourierService.class );
			courierService.getShippingServices().add( postalZoneInternationalShippingService );
			courierService.saveDetails();
		}
	}
	
	public boolean isAddtionalOptionsRequired() {
		return validationRequired("additonalOptionAdd");
	}

	public SelectItem[] getPostalZoneSelectItemBeans() {
		return AplosBean.getSelectItemBeans( PostalZone.class );
	}

	public void addAdditonalOption() {
		PostalZoneInternationalShippingService postalZoneInternationalShippingService = (PostalZoneInternationalShippingService) resolveAssociatedBean();
		PostalZoneAdditionalShippingOption additionalShippingOption = new PostalZoneAdditionalShippingOption( optionName, optionCost, optionDeliveryTime );
		additionalShippingOption.setPostalZone( getOptionPostalZone() );
		postalZoneInternationalShippingService.getAdditionalShippingOptions().add( additionalShippingOption );

		//maxWeight = cost = 0d;

		if( !postalZoneInternationalShippingService.isNew() ) {
			postalZoneInternationalShippingService.saveDetails();
		}
	}

	public void removeAdditionalOption() {
		PostalZoneInternationalShippingService postalZoneInternationalShippingService = (PostalZoneInternationalShippingService) resolveAssociatedBean();
		postalZoneInternationalShippingService.getAdditionalShippingOptions().remove( JSFUtil.getRequest().getAttribute( AplosScopedBindings.TABLE_BEAN ) );
		postalZoneInternationalShippingService.saveDetails();
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

	public void setOptionDeliveryTime(String optionDeliveryTime) {
		this.optionDeliveryTime = optionDeliveryTime;
	}

	public String getOptionDeliveryTime() {
		return optionDeliveryTime;
	}

	public void setOptionPostalZone(PostalZone optionPostalZone) {
		this.optionPostalZone = optionPostalZone;
	}

	public PostalZone getOptionPostalZone() {
		return optionPostalZone;
	}
}
