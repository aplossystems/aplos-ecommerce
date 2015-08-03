package com.aplos.ecommerce.backingpage.product.type;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.EditPage;
import com.aplos.ecommerce.beans.product.ProductType;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=ProductType.class)
public class ProductTypeCategoryEditPage extends EditPage {

	private static final long serialVersionUID = 6126068211591620693L;

	public ProductTypeCategoryEditPage() {
		getBeanDao().setListPageClass( ProductTypeCategoryListPage.class );
	}

}
