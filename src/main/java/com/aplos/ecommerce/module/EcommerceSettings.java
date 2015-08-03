package com.aplos.ecommerce.module;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.aplos.common.annotations.persistence.Cascade;
import com.aplos.common.annotations.persistence.CascadeType;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.OneToMany;
import com.aplos.common.beans.AplosBean;
import com.aplos.ecommerce.beans.EnticementEmail;
import com.aplos.ecommerce.beans.Promotion;

@Entity
public class EcommerceSettings extends AplosBean {
	private static final long serialVersionUID = 3983721745614779297L;

	private boolean isRoundingShipping = false;
	
	private BigDecimal additionalShippingCost = new BigDecimal( 0 );
	private BigDecimal additionalShippingPercentage = new BigDecimal( 0 );
	private BigDecimal additionalShippingWeightPercentage = new BigDecimal( 0 );
	
	private BigDecimal creditCardFeePercentage;
	private BigDecimal overseasAdminCharge;
	private boolean isUsingPackagedWeight = false;
	private boolean isVatAddedToPackaging = false;
	private boolean isOutOfStockProductAllowedOnOrder = false;
	private boolean isPreOrderAllowedOnOrder = false;
	private boolean isEverythingFreeShipping = false;
	private boolean isShowingVat = false;
	private boolean isUsingCustomerReference = false;
	private boolean isUsingCommodityCodes = false;
	private boolean isUsingSerialNumbers = false;
	private boolean isUsingProductColours = true;
	private boolean isUsingProductSizes = true;
	private boolean isUsingStoreCredit = false;
	private boolean isUsingPlayCom = false;
	private boolean isUsingAmazon = false;
	private boolean isUsingBarcodes = false;  
	private boolean isUsingRealizedProductReturns = false;
	private boolean isBusinessToBusiness = false;
	private boolean isAllowGiftItems = false;
	private boolean isAllowGiftVouchers = false;
	private boolean isUsingItemCodes = true;
	private boolean cartShowingImages = true;
	private boolean isShowingDiscontinuedProducts = false;
	private boolean cartShowingDiscountColumn = true;
	private boolean isCustomerEmailConfirmationRequired = false;
	private boolean isOutOfStockTxnAuthRequired = false;
	private boolean isUpdatingCustomerAddressesFromTransaction = false;
	private boolean isKitItemsFixed = true; //do kit items always contain a set list of items
	private boolean isAllowingKitItems = false; //do kit items always contain a set list of items
	private boolean isProductListGroupByColour = true;
	private boolean isAlwaysChargedInDefaultCurrency = false;
	private boolean isUsingCategoriesInProductUrls = false;
	private boolean isSendRegistrationEmails = false;
	private boolean isShowingVolumetricPricesFrontend = true;
	private boolean isVolumetricWeightAuthorisationRequired = false;
	private boolean isTimeTilAvailabilityAutoDecrement = true; //nease issue 3016
	private boolean isEmailAlertAdminOnFrontendOrder = false; //nease job 3322
	private boolean isDeliveryPostcodeRequired = false; //nease issue 3310 (the beauty project)
	private int abandonedOrderAlertTimeout = 35; //nease job 3422

	//all of these following properties relate to customer re-order enticements
	//ie promotions the system automatically emails out to encourage a previous customer to return and buy more
	//changing isUsingRepeatCustomEnticements in the settings screen creates the initial enticementEmailPromotion
	private boolean isUsingRepeatCustomEnticements=false;
	private boolean isRepeatCustomEnticementForNewCustomersOnly=true;
	@ManyToOne
	private Promotion enticementEmailPromotion; //the promotion we copy values from
	@OneToMany
	@Cascade({CascadeType.ALL})
	private List<EnticementEmail> enticementEmailList = new ArrayList<EnticementEmail>();
	private int enticementValidForDays = 14;

	@Column(columnDefinition="LONGTEXT")
	private String defaultTransactionCcAddresses;

	@Column(columnDefinition="LONGTEXT")
	private String defaultReturnsCcAddresses;
	
	private Date playComLastUpdated;

	//#######################################################################################################//

	public void setRoundingShipping(boolean isRoundingShipping) {
		this.isRoundingShipping = isRoundingShipping;
	}

	public boolean isRoundingShipping() {
		return isRoundingShipping;
	}

	public void setAdditionalShippingCost(BigDecimal additionalShippingCost) {
		this.additionalShippingCost = additionalShippingCost;
	}

	public BigDecimal getAdditionalShippingCost() {
		return additionalShippingCost;
	}

	public void setAdditionalShippingPercentage(
			BigDecimal additionalShippingPercentage) {
		this.additionalShippingPercentage = additionalShippingPercentage;
	}

	public BigDecimal getAdditionalShippingPercentage() {
		return additionalShippingPercentage;
	}

	public void setUsingPackagedWeight(boolean isUsingPackagedWeight) {
		this.isUsingPackagedWeight = isUsingPackagedWeight;
	}

	public boolean isUsingPackagedWeight() {
		return isUsingPackagedWeight;
	}

	public void setDefaultTransactionCcAddresses(
			String defaultTransactionCcAddresses) {
		this.defaultTransactionCcAddresses = defaultTransactionCcAddresses;
	}

	public String getDefaultTransactionCcAddresses() {
		return defaultTransactionCcAddresses;
	}

	public void setDefaultReturnsCcAddresses(String defaultReturnsCcAddresses) {
		this.defaultReturnsCcAddresses = defaultReturnsCcAddresses;
	}

	public String getDefaultReturnsCcAddresses() {
		return defaultReturnsCcAddresses;
	}

	public void setVatAddedToPackaging(boolean isVatAddedToPackaging) {
		this.isVatAddedToPackaging = isVatAddedToPackaging;
	}

	public boolean isVatAddedToPackaging() {
		return isVatAddedToPackaging;
	}

	public void setCreditCardFeePercentage(BigDecimal creditCardFeePercentage) {
		this.creditCardFeePercentage = creditCardFeePercentage;
	}

	public BigDecimal getCreditCardFeePercentage() {
		return creditCardFeePercentage;
	}

	public void setOverseasAdminCharge(BigDecimal overseasAdminCharge) {
		this.overseasAdminCharge = overseasAdminCharge;
	}

	public BigDecimal getOverseasAdminCharge() {
		return overseasAdminCharge;
	}

	public void setOutOfStockProductAllowedOnOrder(
			boolean isOutOfStockProductAllowedOnOrder) {
		this.isOutOfStockProductAllowedOnOrder = isOutOfStockProductAllowedOnOrder;
	}

	public boolean isOutOfStockProductAllowedOnOrder() {
		return isOutOfStockProductAllowedOnOrder;
	}

	public void setEverythingFreeShipping(boolean isEverythingFreeShipping) {
		this.isEverythingFreeShipping = isEverythingFreeShipping;
	}

	public boolean isEverythingFreeShipping() {
		return isEverythingFreeShipping;
	}

	public void setAdditionalShippingWeightPercentage(
			BigDecimal additionalShippingWeightPercentage) {
		this.additionalShippingWeightPercentage = additionalShippingWeightPercentage;
	}

	public BigDecimal getAdditionalShippingWeightPercentage() {
		return additionalShippingWeightPercentage;
	}

	public void setShowingVat(boolean isShowingVat) {
		this.isShowingVat = isShowingVat;
	}

	public boolean isShowingVat() {
		return isShowingVat;
	}

	public void setBusinessToBusiness(boolean isBusinessToBusiness) {
		this.isBusinessToBusiness = isBusinessToBusiness;
	}

	public boolean isBusinessToBusiness() {
		return isBusinessToBusiness;
	}

	public void setUsingProductColours(boolean isUsingProductColours) {
		this.isUsingProductColours = isUsingProductColours;
	}

	public boolean isUsingProductColours() {
		return isUsingProductColours;
	}

	public void setUsingProductSizes(boolean isUsingProductSizes) {
		this.isUsingProductSizes = isUsingProductSizes;
	}

	public boolean isUsingProductSizes() {
		return isUsingProductSizes;
	}

	public void setPreOrderAllowedOnOrder(boolean isPreOrderAllowedOnOrder) {
		this.isPreOrderAllowedOnOrder = isPreOrderAllowedOnOrder;
	}

	public boolean isPreOrderAllowedOnOrder() {
		return isPreOrderAllowedOnOrder;
	}

	public void setUsingItemCodes(boolean isUsingItemCodes) {
		this.isUsingItemCodes = isUsingItemCodes;
	}

	public boolean isUsingItemCodes() {
		return isUsingItemCodes;
	}

	public void setCartShowingImages(boolean cartShowingImages) {
		this.cartShowingImages = cartShowingImages;
	}

	public boolean isCartShowingImages() {
		return cartShowingImages;
	}

	public void setAllowGiftItems(boolean isAllowGiftItems) {
		this.isAllowGiftItems = isAllowGiftItems;
	}

	public boolean isAllowGiftItems() {
		return isAllowGiftItems;
	}

	public void setCustomerEmailConfirmationRequired(
			boolean isCustomerEmailConfirmationRequired) {
		this.isCustomerEmailConfirmationRequired = isCustomerEmailConfirmationRequired;
	}

	public boolean isCustomerEmailConfirmationRequired() {
		return isCustomerEmailConfirmationRequired;
	}

	public void setOutOfStockTxnAuthRequired(boolean isOutOfStockTxnAuthRequired) {
		this.isOutOfStockTxnAuthRequired = isOutOfStockTxnAuthRequired;
	}

	public boolean isOutOfStockTxnAuthRequired() {
		return isOutOfStockTxnAuthRequired;
	}

	public void setAllowGiftVouchers(boolean isAllowGiftVouchers) {
		this.isAllowGiftVouchers = isAllowGiftVouchers;
	}

	public boolean isAllowGiftVouchers() {
		return isAllowGiftVouchers;
	}

	public void setCartShowingDiscountColumn(boolean cartShowingDiscountColumn) {
		this.cartShowingDiscountColumn = cartShowingDiscountColumn;
	}

	public boolean isCartShowingDiscountColumn() {
		return cartShowingDiscountColumn;
	}

	public void setUsingRepeatCustomEnticements(
			boolean isUsingRepeatCustomEnticements) {
		this.isUsingRepeatCustomEnticements = isUsingRepeatCustomEnticements;
	}

	public boolean isUsingRepeatCustomEnticements() {
		return isUsingRepeatCustomEnticements;
	}

	public void setRepeatCustomEnticementForNewCustomersOnly(
			boolean isRepeatCustomEnticementForNewCustomersOnly) {
		this.isRepeatCustomEnticementForNewCustomersOnly = isRepeatCustomEnticementForNewCustomersOnly;
	}

	public boolean isRepeatCustomEnticementForNewCustomersOnly() {
		return isRepeatCustomEnticementForNewCustomersOnly;
	}

	public boolean isUpdatingCustomerAddressesFromTransaction() {
		return isUpdatingCustomerAddressesFromTransaction;
	}

	public void setUpdatingCustomerAddressesFromTransaction(
			boolean isUpdatingCustomerAddressesFromTransaction) {
		this.isUpdatingCustomerAddressesFromTransaction = isUpdatingCustomerAddressesFromTransaction;
	}

	public void setUsingStoreCredit(boolean isUsingStoreCredit) {
		this.isUsingStoreCredit = isUsingStoreCredit;
	}

	public boolean isUsingStoreCredit() {
		return isUsingStoreCredit;
	}

	public void setKitItemsFixed(boolean isKitItemsFixed) {
		this.isKitItemsFixed = isKitItemsFixed;
	}

	public boolean isKitItemsFixed() {
		return isKitItemsFixed;
	}

//	public BigDecimal getFreeShippingNoticePercentage() {
//		return freeShippingNoticePercentage;
//	}
//
//	public void setFreeShippingNoticePercentage(
//			BigDecimal freeShippingNoticePercentage) {
//		this.freeShippingNoticePercentage = freeShippingNoticePercentage;
//	}

	public void setProductListGroupByColour(boolean isProductListGroupByColour) {
		this.isProductListGroupByColour = isProductListGroupByColour;
	}

	public boolean isProductListGroupByColour() {
		return isProductListGroupByColour;
	}

	public void setAlwaysChargedInDefaultCurrency(
			boolean isAlwaysChargedInDefaultCurrency) {
		this.isAlwaysChargedInDefaultCurrency = isAlwaysChargedInDefaultCurrency;
	}

	public boolean isAlwaysChargedInDefaultCurrency() {
		return isAlwaysChargedInDefaultCurrency;
	}

	public boolean isUsingCategoriesInProductUrls() {
		return isUsingCategoriesInProductUrls;
	}

	public void setUsingCategoriesInProductUrls(
			boolean isUsingCategoriesInProductUrls) {
		this.isUsingCategoriesInProductUrls = isUsingCategoriesInProductUrls;
	}

	public void setEnticementEmailPromotion(Promotion enticementEmailPromotion) {
		this.enticementEmailPromotion = enticementEmailPromotion;
	}

	public Promotion getEnticementEmailPromotion() {
		return enticementEmailPromotion;
	}

	public void setEnticementEmailList(List<EnticementEmail> enticementEmailList) {
		this.enticementEmailList = enticementEmailList;
	}

	public List<EnticementEmail> getEnticementEmailList() {
		enticementEmailList = EnticementEmail.sortByDaysDelay(enticementEmailList);
		return enticementEmailList;
	}

	public void setEnticementValidForDays(int enticementValidForDays) {
		this.enticementValidForDays = enticementValidForDays;
	}

	public int getEnticementValidForDays() {
		return enticementValidForDays;
	}

	public void setSendRegistrationEmails(boolean isSendRegistrationEmails) {
		this.isSendRegistrationEmails = isSendRegistrationEmails;
	}

	public boolean isSendRegistrationEmails() {
		return isSendRegistrationEmails;
	}

	public boolean isTimeTilAvailabilityAutoDecrement() {
		return isTimeTilAvailabilityAutoDecrement;
	}

	public void setTimeTilAvailabilityAutoDecrement(
			boolean isTimeTilAvailabilityAutoDecrement) {
		this.isTimeTilAvailabilityAutoDecrement = isTimeTilAvailabilityAutoDecrement;
	}

	public boolean isEmailAlertAdminOnFrontendOrder() {
		return isEmailAlertAdminOnFrontendOrder;
	}

	public void setEmailAlertAdminOnFrontendOrder(boolean isEmailAlertAdminOnFrontendOrder) {
		this.isEmailAlertAdminOnFrontendOrder = isEmailAlertAdminOnFrontendOrder;
	}

	public boolean isDeliveryPostcodeRequired() {
		return isDeliveryPostcodeRequired;
	}

	public void setDeliveryPostcodeRequired(boolean isDeliveryPostcodeRequired) {
		this.isDeliveryPostcodeRequired = isDeliveryPostcodeRequired;
	}

	public int getAbandonedOrderAlertTimeout() {
		return abandonedOrderAlertTimeout;
	}

	public void setAbandonedOrderAlertTimeout(int abandonedOrderAlertTimeout) {
		this.abandonedOrderAlertTimeout = abandonedOrderAlertTimeout;
	}

	public boolean isShowingVolumetricPricesFrontend() {
		return isShowingVolumetricPricesFrontend;
	}

	public void setShowingVolumetricPricesFrontend(
			boolean isShowingVolumetricPricesFrontend) {
		this.isShowingVolumetricPricesFrontend = isShowingVolumetricPricesFrontend;
	}

	public boolean isUsingSerialNumbers() {
		return isUsingSerialNumbers;
	}

	public void setUsingSerialNumbers(boolean isUsingSerialNumbers) {
		this.isUsingSerialNumbers = isUsingSerialNumbers;
	}

	public boolean isUsingCustomerReference() {
		return isUsingCustomerReference;
	}

	public void setUsingCustomerReference(boolean isUsingCustomerReference) {
		this.isUsingCustomerReference = isUsingCustomerReference;
	}

	public boolean isUsingCommodityCodes() {
		return isUsingCommodityCodes;
	}

	public void setUsingCommodityCodes(boolean isUsingCommodityCodes) {
		this.isUsingCommodityCodes = isUsingCommodityCodes;
	}

	public boolean isVolumetricWeightAuthorisationRequired() {
		return isVolumetricWeightAuthorisationRequired;
	}

	public void setVolumetricWeightAuthorisationRequired(
			boolean isVolumetricWeightAuthorisationRequired) {
		this.isVolumetricWeightAuthorisationRequired = isVolumetricWeightAuthorisationRequired;
	}

	public boolean isUsingPlayCom() {
		return isUsingPlayCom;
	}

	public void setUsingPlayCom(boolean isUsingPlayCom) {
		this.isUsingPlayCom = isUsingPlayCom;
	}

	public Date getPlayComLastUpdated() {
		return playComLastUpdated;
	}

	public void setPlayComLastUpdated(Date playComLastUpdated) {
		this.playComLastUpdated = playComLastUpdated;
	}

	public boolean isUsingRealizedProductReturns() {
		return isUsingRealizedProductReturns;
	}

	public void setUsingRealizedProductReturns(boolean isUsingRealizedProductReturns) {
		this.isUsingRealizedProductReturns = isUsingRealizedProductReturns;
	}

	/**
	 * @return the isUsingBarcodes
	 */
	public boolean isUsingBarcodes() {
		return isUsingBarcodes;
	}

	/**
	 * @param isUsingBarcodes the isUsingBarcodes to set
	 */
	public void setUsingBarcodes(boolean isUsingBarcodes) {
		this.isUsingBarcodes = isUsingBarcodes;
	}

	public boolean isUsingAmazon() {
		return isUsingAmazon;
	}

	public void setUsingAmazon(boolean isUsingAmazon) {
		this.isUsingAmazon = isUsingAmazon;
	}

	public boolean isShowingDiscontinuedProducts() {
		return isShowingDiscontinuedProducts;
	}

	public void setShowingDiscontinuedProducts(boolean isShowingDiscontinuedProducts) {
		this.isShowingDiscontinuedProducts = isShowingDiscontinuedProducts;
	}

	public boolean isAllowingKitItems() {
		return isAllowingKitItems;
	}

	public void setAllowingKitItems(boolean isAllowingKitItems) {
		this.isAllowingKitItems = isAllowingKitItems;
	}
}
