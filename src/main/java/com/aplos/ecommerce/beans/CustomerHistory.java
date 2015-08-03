package com.aplos.ecommerce.beans;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.beans.AplosBean;

@Entity
public class CustomerHistory extends AplosBean {

	private static final long serialVersionUID = 4061672553656279899L;
	
	@ManyToOne
	private Customer customer = null;
	
	public CustomerHistory() {
		
	}
	
	public CustomerHistory(Customer customer) {
		this.customer = customer;
	}
	
	public Customer getCustomer() {
		return customer;
	}
	
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
}
