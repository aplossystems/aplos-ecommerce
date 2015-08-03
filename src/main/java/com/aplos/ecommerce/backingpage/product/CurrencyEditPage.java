package com.aplos.ecommerce.backingpage.product;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.beans.Currency;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=Currency.class)
public class CurrencyEditPage extends EditPage {
	private static final long serialVersionUID = 4075857793826882885L;
}
