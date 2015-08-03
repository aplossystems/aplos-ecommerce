package com.aplos.ecommerce.backingpage.product;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.Currency;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=Currency.class)
public class CurrencyListPage extends ListPage  {

	private static final long serialVersionUID = 7908584202197463278L;
}
