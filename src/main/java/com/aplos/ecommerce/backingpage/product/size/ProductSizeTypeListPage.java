package com.aplos.ecommerce.backingpage.product.size;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.ListPage;
import com.aplos.ecommerce.beans.product.ProductSizeType;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=ProductSizeType.class)
public class ProductSizeTypeListPage extends ListPage  {
	private static final long serialVersionUID = -579406189042737278L;
}
