package com.aplos.ecommerce.backingpage.customer;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.EditPage;
import com.aplos.ecommerce.beans.DiscountAllowance;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=DiscountAllowance.class)
public class DiscountAllowanceEditPage extends EditPage {
	private static final long serialVersionUID = -1632366803967462351L;
}