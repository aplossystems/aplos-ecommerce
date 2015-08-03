package com.aplos.ecommerce.backingpage.marketing;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.CatalogueRequest;
import com.aplos.ecommerce.beans.DiscountAllowance;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=DiscountAllowance.class)
public class CatalogueRequestListPage extends ListPage {
	private static final long serialVersionUID = 3960632934392169710L;

	public String getSentYesNoString() {
		CatalogueRequest catReq = (CatalogueRequest) JSFUtil.getRequest().getAttribute("tableBean");
		if (catReq != null && catReq.isSent()) {
			return "Yes";
		}
		return "No";
	}
}
