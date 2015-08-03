package com.aplos.ecommerce.backingpage;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.EditPage;
import com.aplos.ecommerce.beans.RealizedProductBand;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=RealizedProductBand.class)
public class RealizedProductBandEditPage extends EditPage {
	private static final long serialVersionUID = -4516532551681245611L;

}
