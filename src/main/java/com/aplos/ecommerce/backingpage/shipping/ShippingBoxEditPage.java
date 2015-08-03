package com.aplos.ecommerce.backingpage.shipping;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.EditPage;
import com.aplos.ecommerce.beans.couriers.ShippingBox;

@ManagedBean
@SessionScoped
@AssociatedBean(beanClass=ShippingBox.class)
public class ShippingBoxEditPage extends EditPage {
	private static final long serialVersionUID = 1994692775240665894L;
}
