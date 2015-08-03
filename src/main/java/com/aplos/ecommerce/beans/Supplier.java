package com.aplos.ecommerce.beans;

import com.aplos.common.annotations.RemoveEmpty;
import com.aplos.common.annotations.persistence.Cascade;
import com.aplos.common.annotations.persistence.CascadeType;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.beans.Address;
import com.aplos.common.beans.AplosBean;

@Entity
public class Supplier extends AplosBean {
	private static final long serialVersionUID = 5154731735048102788L;

	private String name;
	@ManyToOne
	private SupplierType supplierType;
	private String shortDescription;
	private String accountNumber;

	@ManyToOne
	@RemoveEmpty
	@Cascade({CascadeType.ALL})
	private Address salesAddress;

	private String returnsContactFirstName;
	private String returnsContactSurname;
	private String returnsPhone;
	private String returnsFax;

	private String accountsContactFirstName;
	private String accountsContactSurname;
	private String accountsPhone;
	private String accountsFax;

	@Column(columnDefinition="LONGTEXT")
	private String notes;

	private String salesUrl;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setSupplierType(SupplierType supplierType) {
		this.supplierType = supplierType;
	}

	public SupplierType getSupplierType() {
		return supplierType;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setSalesAddress(Address salesAddress) {
		this.salesAddress = salesAddress;
	}

	public Address getSalesAddress() {
		return salesAddress;
	}

	public void setReturnsPhone(String returnsPhone) {
		this.returnsPhone = returnsPhone;
	}

	public String getReturnsPhone() {
		return returnsPhone;
	}

	public void setReturnsFax(String returnsFax) {
		this.returnsFax = returnsFax;
	}

	public String getReturnsFax() {
		return returnsFax;
	}

	public void setAccountsPhone(String accountsPhone) {
		this.accountsPhone = accountsPhone;
	}

	public String getAccountsPhone() {
		return accountsPhone;
	}

	public void setAccountsFax(String accountsFax) {
		this.accountsFax = accountsFax;
	}

	public String getAccountsFax() {
		return accountsFax;
	}

	public void setReturnsContactFirstName(String returnsContactFirstName) {
		this.returnsContactFirstName = returnsContactFirstName;
	}

	public String getReturnsContactFirstName() {
		return returnsContactFirstName;
	}

	public void setReturnsContactSurname(String returnsContactSurname) {
		this.returnsContactSurname = returnsContactSurname;
	}

	public String getReturnsContactSurname() {
		return returnsContactSurname;
	}

	public void setAccountsContactFirstName(String accountsContactFirstName) {
		this.accountsContactFirstName = accountsContactFirstName;
	}

	public String getAccountsContactFirstName() {
		return accountsContactFirstName;
	}

	public void setAccountsContactSurname(String accountsContactSurname) {
		this.accountsContactSurname = accountsContactSurname;
	}

	public String getAccountsContactSurname() {
		return accountsContactSurname;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getNotes() {
		return notes;
	}

	public void setSalesUrl(String salesUrl) {
		this.salesUrl = salesUrl;
	}

	public String getSalesUrl() {
		return salesUrl;
	}

}
