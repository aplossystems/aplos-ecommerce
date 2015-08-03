package com.aplos.ecommerce.backingpage.product;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.ecommerce.beans.Supplier;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=Supplier.class)
public class SupplierEditPage extends EditPage {
	private static final long serialVersionUID = -4868397374473645374L;

}
