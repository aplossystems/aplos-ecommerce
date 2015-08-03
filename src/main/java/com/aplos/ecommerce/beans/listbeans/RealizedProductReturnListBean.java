package com.aplos.ecommerce.beans.listbeans;

import com.aplos.ecommerce.beans.RealizedProductReturn;

public class RealizedProductReturnListBean extends RealizedProductReturn {
	private static final long serialVersionUID = 1263619513703146809L;
	private String customerOrCompanyName;

	public RealizedProductReturnListBean() {}

	public void setCustomerOrCompanyName(String customerOrCompanyName) {
		this.customerOrCompanyName = customerOrCompanyName;
	}

	public String getCustomerOrCompanyName() {
		return customerOrCompanyName;
	}
}
