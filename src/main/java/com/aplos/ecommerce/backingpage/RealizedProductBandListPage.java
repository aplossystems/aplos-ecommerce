package com.aplos.ecommerce.backingpage;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.ListPage;
import com.aplos.ecommerce.beans.RealizedProductBand;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=RealizedProductBand.class)
public class RealizedProductBandListPage extends ListPage {
	private static final long serialVersionUID = 2059655172457779218L;
}
