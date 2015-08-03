package com.aplos.ecommerce.backingpage.shipping;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.PostalZone;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.couriers.CourierService;
import com.aplos.ecommerce.beans.couriers.FreeShippingService;

@ManagedBean
@SessionScoped
@AssociatedBean(beanClass=FreeShippingService.class)
public class FreeShippingServiceEditPage extends EditPage {
	private static final long serialVersionUID = -162517171883682886L;

	public FreeShippingServiceEditPage() {
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
		FreeShippingService freeShippingService = JSFUtil.getBeanFromScope( FreeShippingService.class );
		boolean wasNew = freeShippingService.isNew();
		super.applyBtnAction();
		if( wasNew ) {
			CourierService courierService = JSFUtil.getBeanFromScope( CourierService.class );
			courierService.getShippingServices().add( freeShippingService );
			courierService.saveDetails();
		}
	}

	public SelectItem[] getPostalZoneSelectItemBeans() {
		return AplosBean.getSelectItemBeans( PostalZone.class );
	}
}
