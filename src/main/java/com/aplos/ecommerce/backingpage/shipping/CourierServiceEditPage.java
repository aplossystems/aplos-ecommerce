package com.aplos.ecommerce.backingpage.shipping;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.EditPage;
import com.aplos.ecommerce.beans.couriers.ApcInternationalShippingService;
import com.aplos.ecommerce.beans.couriers.ApcUkShippingService;
import com.aplos.ecommerce.beans.couriers.CourierService;
import com.aplos.ecommerce.beans.couriers.FreeShippingService;
import com.aplos.ecommerce.beans.couriers.PostalZoneInternationalShippingService;
import com.aplos.ecommerce.beans.couriers.RoyalMailUkShippingService;
import com.aplos.ecommerce.beans.couriers.StaticShippingService;
import com.aplos.ecommerce.beans.couriers.WeightGradedShippingService;

@ManagedBean
@SessionScoped
@AssociatedBean(beanClass=CourierService.class)
public class CourierServiceEditPage extends EditPage {
	private static final long serialVersionUID = 5462919816135979255L;

	public SelectItem[] getShippingServiceSelectItems() {
		List<SelectItem> shippingServiceSelectSelectItems = new ArrayList<SelectItem>();
		shippingServiceSelectSelectItems.add( new SelectItem( StaticShippingService.class, "Static shipping" ) );
		shippingServiceSelectSelectItems.add( new SelectItem( WeightGradedShippingService.class, "Weight graded shipping" ) );
		shippingServiceSelectSelectItems.add( new SelectItem( ApcInternationalShippingService.class, "APC international shipping" ) );
		shippingServiceSelectSelectItems.add( new SelectItem( ApcUkShippingService.class, "APC UK shipping" ) );
		shippingServiceSelectSelectItems.add( new SelectItem( PostalZoneInternationalShippingService.class, "Postal zone international shipping" ) );
		shippingServiceSelectSelectItems.add( new SelectItem( RoyalMailUkShippingService.class, "Royal mail UK shipping" ) );
		shippingServiceSelectSelectItems.add( new SelectItem( FreeShippingService.class, "Free shipping" ) );

		return shippingServiceSelectSelectItems.toArray( new SelectItem[0] );
	}

}
