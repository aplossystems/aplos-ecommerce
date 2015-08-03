package com.aplos.ecommerce.backingpage.shipping;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.couriers.CourierService;
import com.aplos.ecommerce.beans.couriers.StaticShippingService;
import com.aplos.ecommerce.beans.ebay.EbayManager;
import com.ebay.soap.eBLBaseComponents.ShippingServiceCodeType;

@ManagedBean
@SessionScoped
@AssociatedBean(beanClass=StaticShippingService.class)
public class StaticShippingServiceEditPage extends EditPage {

	private static final long serialVersionUID = 986611496641158273L;

	public StaticShippingServiceEditPage() {
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
		StaticShippingService staticShippingService = JSFUtil.getBeanFromScope( StaticShippingService.class );
		boolean wasNew = staticShippingService.isNew();
		super.applyBtnAction();
		if( wasNew ) {
			CourierService courierService = JSFUtil.getBeanFromScope( CourierService.class );
			courierService.getShippingServices().add( staticShippingService );
			courierService.saveDetails();
		}
	}

	public SelectItem[] getEbayShippingSelectItems() {
		SelectItem[] selectItems = new SelectItem[ShippingServiceCodeType.values().length];
		for (int i=0; i < ShippingServiceCodeType.values().length; i++) {

			selectItems[i] = new SelectItem( (ShippingServiceCodeType.values())[i].name(), EbayManager.camelCaseToSplitString((ShippingServiceCodeType.values())[i].value().replace("_", "")) );
		}
		return selectItems;
	}

}
