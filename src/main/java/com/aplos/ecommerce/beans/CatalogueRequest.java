package com.aplos.ecommerce.beans;

import com.aplos.common.annotations.RemoveEmpty;
import com.aplos.common.annotations.persistence.Cascade;
import com.aplos.common.annotations.persistence.CascadeType;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.beans.Address;
import com.aplos.common.beans.AplosBean;

@Entity
public class CatalogueRequest extends AplosBean {

	private static final long serialVersionUID = -5646633874999983392L;
	@ManyToOne
	private Customer customer; //optional, just for reference really
	@ManyToOne
	@RemoveEmpty
	@Cascade({CascadeType.ALL})
	private Address address;
	private boolean isSent = false;

	public CatalogueRequest() { }

	@Override
	public String getDisplayName() {
		return getAddress().getContactFullName();
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setSent(boolean isSent) {
		this.isSent = isSent;
	}

	public boolean isSent() {
		return isSent;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Address getAddress() {
		return address;
	}

	public String getDeterminedEmail() {
		if (customer != null && customer.getSubscriber() != null && customer.getSubscriber().getEmailAddress() != null && !customer.getSubscriber().getEmailAddress().equals("")) {
			return customer.getSubscriber().getEmailAddress();
		} else if (address != null && address.getEmailAddress() != null) {
			return address.getEmailAddress();
		}
		//entries before 02-12-11 may reach here, new ones auto-register
		return "Not Registered";
	}
}