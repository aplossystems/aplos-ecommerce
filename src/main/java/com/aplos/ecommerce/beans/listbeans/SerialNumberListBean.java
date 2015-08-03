package com.aplos.ecommerce.beans.listbeans;

import java.util.ArrayList;
import java.util.List;

import com.aplos.ecommerce.beans.RealizedProductReturn;
import com.aplos.ecommerce.beans.SerialNumber;

public class SerialNumberListBean extends SerialNumber {
	private static final long serialVersionUID = -2337160479806895634L;

	private String name;
	private String itemCode;
	private Boolean bookedOut;
	private boolean isSelected;
	private List<RealizedProductReturn> optimisedReturns = new ArrayList<RealizedProductReturn>();

	public SerialNumberListBean() {}

	public SerialNumberListBean(Long id, String name, String itemCode, Boolean bookedOut) {
		this.setId( id );
		this.name = name;
		this.setItemCode(itemCode);
		this.bookedOut = bookedOut;
	}

	public void loadOptimisedReturns() {
		setOptimisedReturns(RealizedProductReturn.getOptimisedReturns( getId() ));
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getItemCode() {
		return itemCode;
	}
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	public Boolean getBookedOut() {
		return bookedOut;
	}
	public void setBookedOut(Boolean bookedOut) {
		this.bookedOut = bookedOut;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public List<RealizedProductReturn> getOptimisedReturns() {
		return optimisedReturns;
	}

	public void setOptimisedReturns(List<RealizedProductReturn> optimisedReturns) {
		this.optimisedReturns = optimisedReturns;
	}
}
