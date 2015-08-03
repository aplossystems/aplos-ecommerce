package com.aplos.ecommerce.backingpage.product;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.ListPage;
import com.aplos.ecommerce.beans.product.ProductColour;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=ProductColour.class)
public class ProductColourListPage extends ListPage  {
	private static final long serialVersionUID = -290371883380902824L;

}
