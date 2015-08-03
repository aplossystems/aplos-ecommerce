package com.aplos.ecommerce.developermodulebacking.frontend;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class SaleProductListFeDmb extends ProductListFeDmb {
	private static final long serialVersionUID = -3789383255485328991L;

	public SaleProductListFeDmb() { super(); }

	@Override
	public String getPageTitle() {
		return "Sale Products";
	}
}














