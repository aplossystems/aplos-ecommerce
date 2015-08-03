package com.aplos.ecommerce.backingpage.product;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.ecommerce.beans.SupplierType;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=SupplierType.class)
public class SupplierTypeEditPage extends EditPage {
	private static final long serialVersionUID = 8657399451134982217L;
}
