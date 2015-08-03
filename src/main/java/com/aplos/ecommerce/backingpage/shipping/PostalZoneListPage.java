package com.aplos.ecommerce.backingpage.shipping;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.PostalZone;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=PostalZone.class)
public class PostalZoneListPage extends ListPage {

	private static final long serialVersionUID = -120880842247406994L;
	
	public String getVisibleAsACountryText() {
		PostalZone postalZone = (PostalZone) JSFUtil.getTableBean();
		if (postalZone != null && postalZone.isVisibleAsACountry()) {
			return "Yes";
		} else {
			return "No";
		}
	}
	
}
