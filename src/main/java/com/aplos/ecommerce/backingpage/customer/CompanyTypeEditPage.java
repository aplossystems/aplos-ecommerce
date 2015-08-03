package com.aplos.ecommerce.backingpage.customer;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.EditPage;
import com.aplos.ecommerce.beans.CompanyType;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=CompanyType.class)
public class CompanyTypeEditPage extends EditPage {
	private static final long serialVersionUID = -1094084184763466583L;

}
