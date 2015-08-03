package com.aplos.ecommerce.beans;

import java.math.BigDecimal;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.ShoppingCartItem;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;

@Entity
@ManagedBean
@SessionScoped
public class GiftVoucher extends AplosBean {

	private static final long serialVersionUID = -1016972043699063338L;

	//cascade causes org.hibernate.NonUniqueObjectException: a different object with the same identifier value was already associated with the session
	//in Transaction.createAndIssueGiftVouchers(Transaction.java:715)

	private String uniqueVoucherCode;
	@ManyToOne
	//@Cascade({CascadeType.ALL})
	private ShoppingCartItem shoppingCartItem;
	@ManyToOne
	//@Cascade({CascadeType.ALL})
	private Customer claimedBy;
	private boolean isUsed = false; //because it might be claimed to an account but the transaction not completed

	public GiftVoucher() {
		//generateVoucherCode();
	}

	public GiftVoucher(ShoppingCartItem shoppingCartItem, Customer assignVoucherTo) {
		//generateVoucherCode();
		this.shoppingCartItem = shoppingCartItem;
		this.claimedBy = assignVoucherTo;
	}

	public String getCartItemName() {
		if (shoppingCartItem != null) {
			RealizedProduct realizedProduct = ((EcommerceShoppingCartItem)shoppingCartItem).getRealizedProduct();
			if (realizedProduct != null) {
				return realizedProduct.getDisplayName();
			}
		}
		return "";
	}

	public boolean isStoreCreditVoucher() {
		RealizedProduct realizedProduct = ((EcommerceShoppingCartItem)shoppingCartItem).getRealizedProduct();
		return isStoreCreditVoucher(realizedProduct);
	}

	public boolean isStoreCreditVoucher(RealizedProduct realizedProduct) {

		if (realizedProduct != null) {
			return realizedProduct.isGiftVoucher();
		}
		return false;
	}

	public String getCartPriceString() {
		return FormatUtil.formatTwoDigit(getCartPrice());
	}

	public BigDecimal getCartPrice() {
		return getVoucherCredit().multiply(new BigDecimal(-1));
	}

	public BigDecimal getVoucherCredit() {
		RealizedProduct realizedProduct = ((EcommerceShoppingCartItem)shoppingCartItem).getRealizedProduct();
		if (isStoreCreditVoucher(realizedProduct)) {
			return realizedProduct.getPrice();
		}
		return new BigDecimal(0);
	}

	public GiftVoucher(ShoppingCartItem shoppingCartItem) {
		this.shoppingCartItem = shoppingCartItem;
	}

	@Override
	public String getDisplayName() {
		if (getShoppingCartItem() != null && ((EcommerceShoppingCartItem)getShoppingCartItem()).getRealizedProduct().isGiftVoucher()) {
			return getShoppingCartItem().getDisplayName();
		} else if (getShoppingCartItem() != null) {
			return getShoppingCartItem().getDisplayName() + " (Gift)";
		} else {
			return "";
		}
	}

	protected String calculateCoreVoucherCode() {
		//Started all gift vouchers with GV in case we want to accept
		//these via the payment page 'coupons' section in the future
		String completeCode;
		while (true) {
			completeCode = "GV" + CommonUtil.md5(new Date());
			if (completeCode.length() > 8) {
				//we trim from the beginning as this is time-in-millis - the end changes most, not the beginning
				completeCode = completeCode.substring(completeCode.length() - 8);
			}
			completeCode = completeCode.toUpperCase();
			//we now have a 10 digit code - GVxxxxxxxx
			if (getVoucherIdByCode(completeCode) == null){
				break;
			}
		}
		return completeCode;
	}

	public void generateVoucherCode() {
		//for overriding purposes
		setUniqueVoucherCode(calculateCoreVoucherCode());
	}

	public static Long getVoucherIdByCode(String uniqueCode) {
		return (Long) ApplicationUtil.getFirstUniqueResult("SELECT bean.id FROM " + AplosBean.getTableName(GiftVoucher.class) + " bean WHERE bean.uniqueVoucherCode='" + uniqueCode + "'");
	}

	public void setUsed(boolean isUsed) {
		this.isUsed = isUsed;
	}

	public boolean isUsed() {
		return isUsed;
	}

	public void setClaimedBy(Customer claimedBy) {
		this.claimedBy = claimedBy;
	}

	public Customer getClaimedBy() {
		return claimedBy;
	}

	public void setUniqueVoucherCode(String uniqueVoucherCode) {
		this.uniqueVoucherCode = uniqueVoucherCode;
	}

	public String getUniqueVoucherCode() {
		return uniqueVoucherCode;
	}

	public void setShoppingCartItem(ShoppingCartItem shoppingCartItem) {
		this.shoppingCartItem = shoppingCartItem;
	}

	public ShoppingCartItem getShoppingCartItem() {
		return shoppingCartItem;
	}

}

