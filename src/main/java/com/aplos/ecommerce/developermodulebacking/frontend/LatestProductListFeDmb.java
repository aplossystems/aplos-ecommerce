package com.aplos.ecommerce.developermodulebacking.frontend;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class LatestProductListFeDmb extends ProductListFeDmb {
	private static final long serialVersionUID = 5073961263396901489L;

	public LatestProductListFeDmb() { super(); }

	@Override
	public String getPageTitle() {
		return "Latest Products";
	}
}














