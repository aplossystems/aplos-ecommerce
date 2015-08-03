package com.aplos.ecommerce.backingpage.product;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.ecommerce.beans.DiscountAllowance;
import com.aplos.ecommerce.beans.ebay.EbayManager;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=EbayManager.class)
public class EbayProductEditPage extends EditPage {

	private static final long serialVersionUID = 2424582352294901477L;

}
