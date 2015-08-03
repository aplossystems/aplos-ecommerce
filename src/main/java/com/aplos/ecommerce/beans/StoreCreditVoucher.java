package com.aplos.ecommerce.beans;

import java.math.BigDecimal;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.utils.FormatUtil;

@Entity
@ManagedBean
@SessionScoped
public class StoreCreditVoucher extends GiftVoucher {

	private static final long serialVersionUID = 8512550504460345488L;

	private BigDecimal creditValue = new BigDecimal(0);
	private String reason = "";

	public StoreCreditVoucher() {
		super();
	}

	public StoreCreditVoucher(BigDecimal amount) {
		super();
		creditValue = amount;
	}

	public StoreCreditVoucher(BigDecimal amount, Customer customer) {
		super();
		creditValue = amount;
		super.setClaimedBy(customer);
	}

	public StoreCreditVoucher(BigDecimal amount, Customer customer, String reason) {
		super();
		creditValue = amount;
		this.reason = reason;
		super.setClaimedBy(customer);
	}

	@Override
	public String getCartItemName() {
		String name = FormatUtil.formatUkCurrency(getCreditValue().doubleValue()) + " Store Credit Voucher";
		if (reason != null && !reason.equals("")) {
			name += " for " + reason;
		}
		return name;
	}

	@Override
	public boolean isStoreCreditVoucher() {
		return true;
	}

	@Override
	public BigDecimal getCartPrice() {
		return getCreditValue().multiply(new BigDecimal(-1));
	}

	@Override
	public BigDecimal getVoucherCredit() {
		return getCreditValue();
	}

	@Override
	public String getDisplayName() {
		return getCartItemName();
	}

	@Override
	public void generateVoucherCode() {
		//add SC to the usual code format just so we can differentiate them in db if ever needed
		String completeCode = super.calculateCoreVoucherCode();
		setUniqueVoucherCode(completeCode + "SC");
	}

	public void setCreditValue(BigDecimal creditValue) {
		this.creditValue = creditValue;
	}

	public BigDecimal getCreditValue() {
		return creditValue;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getReason() {
		return reason;
	}

}

