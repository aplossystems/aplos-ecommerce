package com.aplos.ecommerce.backingpage.shipping;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.couriers.ApcInternationalShippingService;
import com.aplos.ecommerce.beans.couriers.CourierService;

@ManagedBean
@SessionScoped
@AssociatedBean(beanClass=ApcInternationalShippingService.class)
public class ApcInternationalShippingServiceEditPage extends EditPage {
	private static final long serialVersionUID = 7037551228630358207L;

	public ApcInternationalShippingServiceEditPage() {
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
		ApcInternationalShippingService apcInternationalShippingService = JSFUtil.getBeanFromScope( ApcInternationalShippingService.class );
		boolean wasNew = apcInternationalShippingService.isNew();
		super.applyBtnAction();
		if( wasNew ) {
			CourierService courierService = JSFUtil.getBeanFromScope( CourierService.class );
			courierService.getShippingServices().add( apcInternationalShippingService );
			courierService.saveDetails();
		}
	}
}
