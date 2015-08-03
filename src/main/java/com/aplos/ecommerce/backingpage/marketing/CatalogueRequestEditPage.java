package com.aplos.ecommerce.backingpage.marketing;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.backingpage.customer.CustomerEditPage;
import com.aplos.ecommerce.beans.CatalogueRequest;
import com.aplos.ecommerce.beans.DiscountAllowance;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=CatalogueRequest.class)
public class CatalogueRequestEditPage extends EditPage {
	private static final long serialVersionUID = 100918055234303855L;

	public String viewCustomer() {
		CatalogueRequest catalogueRequest = JSFUtil.getBeanFromScope(CatalogueRequest.class);
		if (catalogueRequest != null) {
			catalogueRequest.getCustomer().addToScope();
			JSFUtil.redirect(CustomerEditPage.class);
		}
		return null;
	}

}





