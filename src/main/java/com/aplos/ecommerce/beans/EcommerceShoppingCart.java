package com.aplos.ecommerce.beans;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.aplos.common.annotations.persistence.Cascade;
import com.aplos.common.annotations.persistence.CascadeType;
import com.aplos.common.annotations.persistence.Embedded;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.OneToMany;
import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.Currency;
import com.aplos.common.beans.ShoppingCart;
import com.aplos.common.beans.ShoppingCartItem;
import com.aplos.common.enums.CommonBundleKey;
import com.aplos.common.enums.JsfScope;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.couriers.AvailableShippingService;
import com.aplos.ecommerce.interfaces.RealizedProductBandSelector;
import com.aplos.ecommerce.module.EcommerceConfiguration;
import com.aplos.ecommerce.utils.EcommerceUtil;

@Entity
public class EcommerceShoppingCart extends ShoppingCart {
	private static final long serialVersionUID = 6387166950953445278L;

	@ManyToOne
	private Customer customer;

	@Embedded
	private AvailableShippingService availableShippingService;

	private BigDecimal additionalWeight = new BigDecimal( 0 );
	private BigDecimal cachedWeight = new BigDecimal( 0 );
	private BigDecimal cachedVolume = new BigDecimal( 0 );
	private BigDecimal cachedBoxVolume = new BigDecimal( 0 );

	private BigDecimal deliveryCost = new BigDecimal( 0 );
	private BigDecimal cachedNetDeliveryCost = new BigDecimal( 0 );
	private BigDecimal cachedDeliveryVat = new BigDecimal( 0 );

	@OneToMany
	@Cascade({CascadeType.ALL})
	private List<GiftVoucher> giftVouchersUsed = new ArrayList<GiftVoucher>();
	private BigDecimal storeCreditUsed = null;
	@Transient //this is called a lot of times and needs calculating, we use it in the 'updateCachedValues lifecycle'
	private BigDecimal storeCreditAvailable = new BigDecimal(0);

	@OneToMany
	@Cascade({CascadeType.ALL})
	private List<ShippingBoxOrder> shippingBoxOrderList;

	@ManyToOne
	@Cascade({CascadeType.ALL})
	private Promotion promotion;

	private BigDecimal promotionPercentage;

	@Transient //this can be used to show what has just been updated in the added to bag popup etc
	private List<EcommerceShoppingCartItem> contentsUpdated = null;
	
	public EcommerceShoppingCart() {
		
	}

	public void depleteStockQuantities() {
		for( EcommerceShoppingCartItem cartItem : getEcommerceShoppingCartItems() ) {
			cartItem.depleteStockQuantities();
		}
	}

	public boolean isItemsRequireShipping() {
		boolean postageRequired = false;
		for (EcommerceShoppingCartItem item : getEcommerceShoppingCartItems()) {
			if ( item.getRealizedProduct() == null || item.getRealizedProduct().isShippingRequired(item) ){
				postageRequired = true;
				break;
			}
		}
		return postageRequired;
	}

	@Override
	public BigDecimal getGrandTotalVatAmount(boolean convert) {
		return super.getGrandTotalVatAmount(convert).add( getCachedDeliveryVat( convert ) );
	}

	public boolean getIsPromotionApplied() {
		return promotion != null;
	}

	public void applyPromotion(Promotion promotion) {
		if (promotion == null) {
			this.promotion=null;
			promotionPercentage=null;
		} else {
			this.promotion = promotion;
			promotionPercentage=promotion.getPercentage();
		}
		//apply the discount
		for (EcommerceShoppingCartItem shoppingCartItem : getEcommerceShoppingCartItems()) {
			shoppingCartItem.updateAllValues();
		}
		//recache
		updateCachedValues(true);
	}

	public EcommerceShoppingCart getCopy( boolean includeUniqueFields ) {
		try {
			EcommerceShoppingCart destCart = (EcommerceShoppingCart) this.clone();
			if( !includeUniqueFields ) {
				destCart.setId( null );
				destCart.setItems( new ArrayList<ShoppingCartItem>() );
				EcommerceShoppingCartItem destCartItem;
				for( EcommerceShoppingCartItem cartItem : this.getEcommerceShoppingCartItems() ) {
					destCartItem = cartItem.getClone();
					destCartItem.setShoppingCart( destCart );
					destCartItem.setId( null );
					destCart.getItems().add( destCartItem );
				}
			}
			return destCart;
		} catch ( CloneNotSupportedException cnsEx ) {
			ApplicationUtil.getAplosContextListener().handleError( cnsEx );
		}
		return null;
	}
	
	@Override
	public void addToScope(JsfScope associatedBeanScope) {
		super.addToScope(associatedBeanScope);
		addToScope( CommonUtil.getBinding( ShoppingCart.class ), this, associatedBeanScope );
	}

	public boolean containsGiftItems() {
		return containsGiftItems(true);
	}

	public boolean containsGiftItems(boolean includeGiftVouchers) {
		if (includeGiftVouchers) {
			for (ShoppingCartItem item : getItems()) {
				if (((EcommerceShoppingCartItem)item).determineIsGiftItem()) {
					return true;
				}
			}
		} else {
			for (ShoppingCartItem item : getItems()) {
				if (((EcommerceShoppingCartItem)item).isGiftItem()) {
					return true;
				}
			}
		}
		return false;
	}

	public void addShippingBoxOrder( ShippingBoxOrder shippingBoxOrder ) {
		if( getShippingBoxOrderList() == null ) {
			setShippingBoxOrderList( new ArrayList<ShippingBoxOrder>() );
		}
		getShippingBoxOrderList().add( shippingBoxOrder );
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer newCustomer) {
		this.customer = newCustomer;
	}

	public void updateBaseRealizedProductValues() {
		for( ShoppingCartItem tempCartItem : getItems() ) {
			((EcommerceShoppingCartItem) tempCartItem).updateBaseClassValues();
		}
	}

	public List<EcommerceShoppingCartItem> getEcommerceShoppingCartItems() {
		List<EcommerceShoppingCartItem> ecommerceShoppingCartItems = new ArrayList<EcommerceShoppingCartItem>();
		for( int i = 0, n = getItems().size(); i < n; i++ ) {
			ecommerceShoppingCartItems.add( (EcommerceShoppingCartItem) getItems().get( i ) );
		}

		return ecommerceShoppingCartItems;
	}

	public EcommerceShoppingCartItem getEcommerceShoppingCartItem( RealizedProduct realizedProduct ) {
		for (int i=0, n=getItems().size() ; i<n ; i++) {
			if ( realizedProduct.equals( ((EcommerceShoppingCartItem)getItems().get(i)).getRealizedProduct() ) ) {
				return (EcommerceShoppingCartItem) getItems().get(i);
			}
		}

		return null;
	}

	@Override
	public void updateVatCachedValues() {
		super.updateVatCachedValues();
		updateCachedDeliveryVatValue();
	}

	public void updateCachedBoxVolume() {
		BigDecimal boxVolume = new BigDecimal( 0 );
		for( ShippingBoxOrder shippingBoxOrder : shippingBoxOrderList ) {
			boxVolume = boxVolume.add( new BigDecimal( shippingBoxOrder.calculateTotalVolume() ) );
		}

		setCachedBoxVolume( boxVolume );
	}

	@Override
	public BigDecimal getCachedTotalVatAmount() {
		return getCachedTotalVatAmount(false);
	}

	@Override
	public BigDecimal getCachedTotalVatAmount(boolean convert) {
		return super.getCachedTotalVatAmount(convert);
	}

	public void updateCachedDeliveryVatValue() {
		if (CommonConfiguration.getCommonConfiguration().isDeliveryVatInclusive()) {
			setCachedDeliveryVat( getDeliveryCost().subtract( getCachedNetDeliveryCost() ) );
		} else {
			setCachedDeliveryVat( CommonUtil.getPercentageAmountAndRound( getDeliveryCost(), getDeliveryVatPercentage() ).setScale( 2, RoundingMode.HALF_DOWN ) );
		}
	}

	public BigDecimal getDeliveryVatPercentage() {
		if( isVatExempt() ) {
			return new BigDecimal( 0 );
		} else {
			return CommonConfiguration.getCommonConfiguration().getUkVatType().getPercentage();
		}
	}

	public void updateWeightCachedValue() {
		BigDecimal totalWeight = new BigDecimal( 0 );
		BigDecimal totalVolume = new BigDecimal( 0 );
		BigDecimal tempItemWeight;
		BigDecimal tempItemVolume;

		for ( int i=0 ; i< getItems().size() ; i++) {
			tempItemWeight = ((EcommerceShoppingCartItem) getItems().get(i)).getCachedTotalWeight();
			tempItemVolume = ((EcommerceShoppingCartItem) getItems().get(i)).getCachedTotalVolume();
			if( tempItemWeight != null ) {
				totalWeight = totalWeight.add( tempItemWeight );
				totalVolume = totalVolume.add( tempItemVolume );
			}
		}

		BigDecimal additionalShippingPercentage = EcommerceConfiguration.getEcommerceSettingsStatic().getAdditionalShippingWeightPercentage();
		if( additionalShippingPercentage != null ) {
			totalWeight = totalWeight.add( totalWeight.multiply( additionalShippingPercentage.divide( new BigDecimal( 100 ) ) ) );
		}
		if (getAdditionalWeight() != null) {
			totalWeight = totalWeight.add( getAdditionalWeight() );
		}

		totalWeight = totalWeight.setScale( 0, RoundingMode.CEILING );
		setCachedWeight( totalWeight );
		setCachedVolume( totalVolume );
	}

	@Override
	public void updateCachedValues( boolean updateChildren ) {
		super.updateCachedValues( updateChildren );
		updateWeightCachedValue();
		updateStoreCreditAvailability();
	}

	/**
	 * TODO: this may no longer be required
	 * @deprecated
	 */
	@Deprecated
	protected BigDecimal getGiftVoucherCreditUsedAmount() {
		BigDecimal total = new BigDecimal( 0 );
		for (int i=0, n = giftVouchersUsed.size(); i < n; i++) {
			BigDecimal remaining = getCachedGrossTotalAmount().subtract(total);
			if ( remaining.doubleValue() > 0 ) {
				if (remaining.subtract(giftVouchersUsed.get(i).getVoucherCredit()).doubleValue() >= 0) {
					total = total.add( giftVouchersUsed.get(i).getVoucherCredit() );
				} else {
					total = total.add(remaining);
					break;
				}
			}
		}
		return total;
	}

	public void setAdditionalWeight(BigDecimal additionalWeight) {
		this.additionalWeight = additionalWeight;
	}

	public BigDecimal getAdditionalWeight() {
		return additionalWeight;
	}

	public boolean getHasCustomer() {
		return (customer != null && customer.getSubscriber() != null);
	}

	public BigDecimal getGrandTotal( boolean includingVat ) {
		return getGrandTotal( includingVat, false );
	}

	public BigDecimal getGrandTotal( boolean includingVat, boolean convert ) {
		BigDecimal total;
		if( includingVat ) {
			total = getCachedGrossTotalAmount(convert);
		} else {
			total = getCachedNetTotalAmount(convert);
		}
		if( includingVat ) {
			total = total.add( getCachedDeliveryVat( convert ) );
		}
		total = total.add( getCachedNetDeliveryCost(convert) );
		total = total.add( getAdminCharge(convert) );
		total = total.subtract( determineStoreCredit(convert) );
		return total;
	}

	public String getGrandTotalString( boolean includingVat ) {
		return getGrandTotalString( includingVat, false );
	}

	public String getGrandTotalString( boolean includingVat, boolean convert ) {
		//0 is lowest it will display
		return FormatUtil.formatTwoDigit( getGrandTotal(includingVat, convert).doubleValue() );
	}

	public void updateDeliveryCost() {
		setDeliveryCost( getAvailableShippingService().updateCachedCost(this) );
		updateCachedNetDeliveryCost();
		updateStoreCreditAvailability();
	}

	public void updateCachedNetDeliveryCost() {
		if( CommonConfiguration.getCommonConfiguration().isDeliveryVatInclusive() ) {
			setCachedNetDeliveryCost( getDeliveryCost().subtract( CommonUtil.getInclusivePercentageAmountAndRound( getDeliveryCost(), getDeliveryVatPercentage() )).setScale( 2, RoundingMode.HALF_DOWN ) );
		} else {
			setCachedNetDeliveryCost( deliveryCost );
		}
	}

	public EcommerceShoppingCartItem addToCart(RealizedProduct realizedProduct, boolean saveDetails ) {
		return addToCart(realizedProduct, saveDetails, null);
	}

	public EcommerceShoppingCartItem addToCart(RealizedProduct realizedProduct, boolean saveDetails, boolean ignoreQuantity) {
		return addToCart(realizedProduct, saveDetails, null, false, ignoreQuantity);
	}
	
	public EcommerceShoppingCartItem addToCart(RealizedProduct realizedProduct, boolean saveDetails, List<RealizedProduct> kitItems) {
		return addToCart(realizedProduct, saveDetails, kitItems, false);
	}
	
	public EcommerceShoppingCartItem addToCart(RealizedProduct realizedProduct, boolean saveDetails, List<RealizedProduct> kitItems, boolean recordUpdatedContents) {
		return addToCart(realizedProduct, saveDetails, kitItems, recordUpdatedContents, false);
	}

	public EcommerceShoppingCartItem addToCart(RealizedProduct realizedProduct, boolean saveDetails, List<RealizedProduct> kitItems, boolean recordUpdatedContents, boolean ignoreQuantity) {
		if( getCurrency() == null ) {
			initialiseNewBean();
		}
		List<EcommerceShoppingCartItem> itemList = getEcommerceShoppingCartItems();
		synchronized( itemList ) {
			EcommerceShoppingCartItem ecommerceShoppingCartItem = null;
			for (int i=0; i < itemList.size(); i++) {
				if (realizedProduct.equals((itemList.get(i)).getRealizedProduct())) {
					if (kitItems == null || kitItems.equals(( itemList.get(i)).getKitItems())) {
						ecommerceShoppingCartItem = itemList.get(i);
					}
				}
			}
			if (ecommerceShoppingCartItem == null) {
				if (realizedProduct.getId() != null) {
					RealizedProduct loadedRealizedProduct = new BeanDao( RealizedProduct.class ).get( realizedProduct.getId() );
//					HibernateUtil.initialise( loadedRealizedProduct, true );
					realizedProduct = loadedRealizedProduct;
				}
				ecommerceShoppingCartItem = createEcommerceShoppingCartItem(this,realizedProduct);
				ecommerceShoppingCartItem.setKitItems(kitItems);
				return addEcommerceShoppingCartItemToCart(true, ecommerceShoppingCartItem, saveDetails, ignoreQuantity);
			} else {
				return addEcommerceShoppingCartItemToCart(false, ecommerceShoppingCartItem, saveDetails, ignoreQuantity);
			}
		}
	}
	
	public EcommerceShoppingCartItem createEcommerceShoppingCartItem( EcommerceShoppingCart ecommerceShoppingCart, RealizedProduct realizedProduct ) {
		return new EcommerceShoppingCartItem(this,realizedProduct);
	}
	
	public void checkAutomaticPromotions() {
		Promotion previousPromotion = getPromotion();
		BeanDao promotionDao = new BeanDao(Promotion.class);
		promotionDao.setWhereCriteria("bean.isCodeRequired=false");
		List<Promotion> promotionList = promotionDao.setIsReturningActiveBeans(true).getAll();
		Promotion tempPromotion = null;
		Promotion choosenPromotion = null;
		for( int i = 0, n = promotionList.size(); i < n; i++ ) {
			tempPromotion = promotionList.get( i );
//			HibernateUtil.initialise( tempPromotion, true );
			if (tempPromotion.isValidFor(this,false)) {
				choosenPromotion = tempPromotion;
				if( previousPromotion == null || !tempPromotion.getId().equals(previousPromotion.getId() ) ) {
					applyPromotion(tempPromotion);
					JSFUtil.addMessage( tempPromotion.getName() + " promotion has been applied" );
				}
				break;
			} //no need for else, isValidFor will give feedback
		}
		if( previousPromotion != null && (choosenPromotion == null || !choosenPromotion.getId().equals(previousPromotion.getId() )) ) {
			applyPromotion( null );
			JSFUtil.addMessage( previousPromotion.getName() + " promotion has been removed" );
		}
	}
	
	@Override
	public void removeCartItem(ShoppingCartItem shoppingCartItem) {
		super.removeCartItem(shoppingCartItem);
		checkAutomaticPromotions();
	}
	
	@Override
	public void removeAllItems() {
		super.removeAllItems();

		checkAutomaticPromotions();
	}
	
	public RealizedProductBandSelector getRealizedProductBandSelector() {
		return null;
	}
	
	public EcommerceShoppingCartItem addEcommerceShoppingCartItemToCart(boolean newToCart, EcommerceShoppingCartItem ecommerceShoppingCartItem, boolean saveDetails) {
		return addEcommerceShoppingCartItemToCart(newToCart, ecommerceShoppingCartItem, saveDetails, false);
	}

	public EcommerceShoppingCartItem addEcommerceShoppingCartItemToCart(boolean newToCart, EcommerceShoppingCartItem ecommerceShoppingCartItem, boolean saveDetails, boolean checkAgainstStock) {
		List<ShoppingCartItem> itemList = getItems();
		synchronized( itemList ) {
			if (newToCart) {
//				ecommerceShoppingCartItem.setShoppingCart(this);
				calculateItemDiscount( ecommerceShoppingCartItem );
				ecommerceShoppingCartItem.calculateVatAmount( true, false );
				itemList.add(ecommerceShoppingCartItem);
			}
			recordContentsUpdated(ecommerceShoppingCartItem.getRealizedProduct(), ecommerceShoppingCartItem.getKitItems());
			ecommerceShoppingCartItem.setQuantity(ecommerceShoppingCartItem.getQuantity()+1);
			ecommerceShoppingCartItem.updateCachedValues(false);
			if ( saveDetails ) {
				this.saveDetails();
				ecommerceShoppingCartItem.saveDetails();
			}
			updateCachedValues( false );
			checkAutomaticPromotions();
			//let the user know about availability unless there's already a message showing its a preorder item
			if (!checkAgainstStock && ecommerceShoppingCartItem.getQuantity() > 1 && ecommerceShoppingCartItem.getRealizedProduct().getStockAvailableFromDate() == null) {
				if (ecommerceShoppingCartItem.getRealizedProduct().getQuantity() < ecommerceShoppingCartItem.getQuantity()) {
					if (EcommerceConfiguration.getEcommerceSettingsStatic().isPreOrderAllowedOnOrder()) {
						String message = "There ";
						if (ecommerceShoppingCartItem.getRealizedProduct().getQuantity()==1) {
							message += "is";
						} else {
							message += "are";
						}
						message += " only " + ecommerceShoppingCartItem.getRealizedProduct().getQuantity() + " '";
						message += ecommerceShoppingCartItem.getRealizedProduct().getDisplayName() + "' in stock. ";
						message += "The additional " + (ecommerceShoppingCartItem.getQuantity()-ecommerceShoppingCartItem.getRealizedProduct().getQuantity());
						message += " you have ordered will be placed on order and will be delivered once available.";
						JSFUtil.addMessage(message);
					} else {
						ecommerceShoppingCartItem.setQuantity(ecommerceShoppingCartItem.getRealizedProduct().getQuantity());
						String message = "There ";
						if (ecommerceShoppingCartItem.getRealizedProduct().getQuantity()==1) {
							message += "is";
						} else {
							message += "are";
						}
						message += " only " + ecommerceShoppingCartItem.getRealizedProduct().getQuantity() + " ";
						message += ecommerceShoppingCartItem.getRealizedProduct().getDisplayName() + "s in stock. You cannot order any more of this item.";
						JSFUtil.addMessage(message);
					}
				}
			}
		}
		return ecommerceShoppingCartItem;
	}

	private void recordContentsUpdated(RealizedProduct realizedProduct, List<RealizedProduct> kitItems) {
		if (contentsUpdated == null) {
			contentsUpdated = new ArrayList<EcommerceShoppingCartItem>();
		}

		EcommerceShoppingCartItem ecommerceShoppingCartItem = null;
		for (int i=0; i < contentsUpdated.size(); i++) {
			if (realizedProduct.equals(contentsUpdated.get(i).getRealizedProduct())) {
				ecommerceShoppingCartItem = contentsUpdated.get(i);
			}
		}
		if (ecommerceShoppingCartItem == null) {
			ecommerceShoppingCartItem = createEcommerceShoppingCartItem(this,realizedProduct);
			ecommerceShoppingCartItem.setKitItems(kitItems);
			contentsUpdated.add(ecommerceShoppingCartItem);
		}

		ecommerceShoppingCartItem.setQuantity(ecommerceShoppingCartItem.getQuantity()+1);
	}

	//  Although this method is in here for now this could be somewhere
	//  where the implementation module could override to provide discounts more
	//  specific to the company.
	public void calculateItemDiscount( EcommerceShoppingCartItem ecommerceShoppingCartItem ) {
		Customer customer = getCustomer();
		EcommerceUtil.getEcommerceUtil().calculateItemDiscount( ecommerceShoppingCartItem, customer );
	}

	public void recalculateAllItemDiscounts() {
		Customer customer = getCustomer();
		for (EcommerceShoppingCartItem cartItem : getEcommerceShoppingCartItems() ) {
			calculateItemDiscount(cartItem);
		}
		updateCachedValues( false );
	}
	
	public void increaseQuantity( EcommerceShoppingCartItem ecommerceShoppingCartItem ) {
		//do a check on stock levels to see whether we can add a sale
		if (EcommerceConfiguration.getEcommerceSettingsStatic().isOutOfStockProductAllowedOnOrder() || 
				ecommerceShoppingCartItem.getRealizedProduct().getStockAvailableFromDate() != null || 
				ecommerceShoppingCartItem.getRealizedProduct().getQuantity() >= ecommerceShoppingCartItem.getQuantity()+1) {
			ecommerceShoppingCartItem.setQuantity(ecommerceShoppingCartItem.getQuantity()+1);
			ecommerceShoppingCartItem.updateCachedValues(true);
		}
		checkAutomaticPromotions();
	}
	
	public void decreaseQuantity( EcommerceShoppingCartItem ecommerceShoppingCartItem ) {
		//do a check to see whether to remove the item rather than decrement (at 0 or less)
		if (ecommerceShoppingCartItem.getQuantity() > 1) {
			ecommerceShoppingCartItem.setQuantity(ecommerceShoppingCartItem.getQuantity()-1);
			ecommerceShoppingCartItem.updateCachedValues(true);
		} else {
			getItems().remove( ecommerceShoppingCartItem );
			updateCachedValues(false);
		}
		checkAutomaticPromotions();
	}

	public void setDeliveryCost(BigDecimal deliveryCost) {
		this.deliveryCost = deliveryCost;
	}

	public BigDecimal getDeliveryCost() {
		return getDeliveryCost(false);
	}

	public BigDecimal getDeliveryCost(boolean convert) {
		if( deliveryCost == null ) {
			return new BigDecimal( 0 );
		} else {
			if (convert) {
				return Currency.convert(deliveryCost, getCurrencyBaseRate());
			} else {
				return deliveryCost;
			}
		}
	}

	public String getDeliveryCostString() {
		return getDeliveryCostString(false);
	}

	public String getDeliveryCostString(boolean convert) {
		return FormatUtil.formatTwoDigit( getDeliveryCost(convert).doubleValue() );
	}

	public String getShippingCostOrTbaString() {
		return getDeliveryCostOrTbaString(!EcommerceConfiguration.getEcommerceSettingsStatic().isShowingVat(), true);
	}

	public String getDeliveryCostOrTbaString(boolean includingVat, boolean convert) {
		if (getDeliveryCost(convert) != null && availableShippingService != null) {
			String costStr;
			if( includingVat ) {
				costStr = getDeliveryCostString(convert);
			} else {
				costStr = getCachedNetDeliveryCostString(convert);
			}
			return getCurrency().getPrefix() + costStr + getCurrency().getSuffix();
		} else {
			return " " + ApplicationUtil.getAplosContextListener().translateByKey(CommonBundleKey.TBA).toUpperCase();
		}
	}

	public void setCachedWeight(BigDecimal cachedWeight) {
		this.cachedWeight = cachedWeight;
	}

	public BigDecimal getCachedWeight() {
		return cachedWeight;
	}

	public void setAvailableShippingService(AvailableShippingService availableShippingService) {
		this.availableShippingService = availableShippingService;
	}

	public AvailableShippingService getAvailableShippingService() {
		return availableShippingService;
	}

	public void setCachedDeliveryVat(BigDecimal cachedDeliveryVat) {
		this.cachedDeliveryVat = cachedDeliveryVat;
	}

	public BigDecimal getCachedDeliveryVat() {
		return getCachedDeliveryVat(false);
	}

	public BigDecimal getCachedDeliveryVat(boolean convert) {
		if( cachedDeliveryVat == null ) {
			return new BigDecimal( 0 );
		} else if (convert) {
			return Currency.convert(cachedDeliveryVat, getCurrencyBaseRate());
		} else {
			return cachedDeliveryVat;
		}
	}

	public void setCachedVolume(BigDecimal cachedVolume) {
		this.cachedVolume = cachedVolume;
	}

	public BigDecimal getCachedVolume() {
		return cachedVolume;
	}

	public void setShippingBoxOrderList(List<ShippingBoxOrder> shippingBoxOrderList) {
		this.shippingBoxOrderList = shippingBoxOrderList;
	}

	public List<ShippingBoxOrder> getShippingBoxOrderList() {
		return shippingBoxOrderList;
	}

	public void setCachedBoxVolume(BigDecimal cachedBoxVolume) {
		this.cachedBoxVolume = cachedBoxVolume;
	}

	public BigDecimal getCachedBoxVolume() {
		return cachedBoxVolume;
	}

	public void setGiftVouchersUsed(List<GiftVoucher> giftVouchersUsed) {
		this.giftVouchersUsed = giftVouchersUsed;
	}

	public List<GiftVoucher> getGiftVouchersUsed() {
		return giftVouchersUsed;
	}

	public void addGiftVoucherUsed(GiftVoucher giftVoucher) {
		if (getGiftVouchersUsed().contains(giftVoucher)) {
			getGiftVouchersUsed().set(getGiftVouchersUsed().indexOf(giftVoucher), giftVoucher);
		} else {
			getGiftVouchersUsed().add(giftVoucher);
		}

		//re-apply credit from vouchers
		for (EcommerceShoppingCartItem shoppingCartItem : getEcommerceShoppingCartItems()) {
			shoppingCartItem.updateAllValues();
		}
		//recache
		updateCachedValues(true);
	}

	public void setPromotionPercentage(BigDecimal promotionPercentage) {
		this.promotionPercentage = promotionPercentage;
	}

	public BigDecimal getPromotionPercentage() {
		return promotionPercentage;
	}

	public void setStoreCreditUsed(BigDecimal storeCreditUsed) {
		this.storeCreditUsed = storeCreditUsed;
	}

	public BigDecimal getStoreCreditUsed() {
		return getStoreCreditUsed(false);
	}

	public BigDecimal getStoreCreditUsed(boolean convert) {
		if (convert) {
			return Currency.convert(storeCreditUsed, getCurrencyBaseRate());
		} else {
			return storeCreditUsed;
		}
	}

	/**
	 * Correctly return the store credit that applies to this cart
	 * we only set storeCreditUsed once transaction.paymentComplete() is called
	 */
	public BigDecimal determineStoreCredit() {
		return determineStoreCredit(false);
	}

	public BigDecimal determineStoreCredit(boolean convert) {
		if (getStoreCreditUsed(convert) != null) {
			return getStoreCreditUsed(convert);
		} else {
			return getStoreCreditAvailable(convert);
		}
	}

	public String getStoreCreditString() {
		return getStoreCreditString(false);
	}

	public String getStoreCreditString(boolean convert) {
		BigDecimal credit = determineStoreCredit(convert);
		return FormatUtil.formatTwoDigit(credit);
	}

	public void setContentsUpdated(List<EcommerceShoppingCartItem> contentsUpdated) {
		this.contentsUpdated = contentsUpdated;
	}

	public List<EcommerceShoppingCartItem> getContentsUpdated() {
		return contentsUpdated;
	}

	@Override
	public void updateAdminCharge(BigDecimal newAdminCharge) {
		super.updateAdminCharge(newAdminCharge);
		updateStoreCreditAvailability();
	}

	//convenience method(s)
	public void updateStoreCreditAvailability() {
		BigDecimal total = new BigDecimal( 0 );
		if (EcommerceConfiguration.getEcommerceSettingsStatic().isUsingStoreCredit()) {
			BigDecimal subTotal = getCachedGrossTotalAmount(false).add(getDeliveryCost(false)).add(getAdminCharge(false));
			if (subTotal.doubleValue() > 0) {
				if (customer != null) {
					total = customer.getAvailableStoreCredit(subTotal);
				}
			}
		}
		setStoreCreditAvailable(total);
	}

	public void setStoreCreditAvailable(BigDecimal storeCreditAvailable) {
		this.storeCreditAvailable = storeCreditAvailable;
	}

	public BigDecimal getStoreCreditAvailable() {
		return getStoreCreditAvailable(false);
	}

	public BigDecimal getStoreCreditAvailable(boolean convert) {
		updateStoreCreditAvailability();
		if (convert) {
			return Currency.convert(storeCreditAvailable, getCurrencyBaseRate());
		} else {
			return storeCreditAvailable;
		}
	}

	public void setPromotion(Promotion promotion) {
		this.promotion = promotion;
	}

	public Promotion getPromotion() {
		return promotion;
	}

	public String getCachedNetDeliveryCostString() {
		return getCachedNetDeliveryCostString( false );
	}

	public String getCachedNetDeliveryCostString( boolean convert ) {
		return FormatUtil.formatTwoDigit( getCachedNetDeliveryCost( convert ), new BigDecimal( 0 ) );
	}

	public BigDecimal getCachedNetDeliveryCost( boolean convert ) {
		if( cachedNetDeliveryCost == null ) {
			return new BigDecimal( 0 );
		} else if (convert) {
			return Currency.convert(cachedNetDeliveryCost, getCurrencyBaseRate());
		} else {
			return cachedNetDeliveryCost;
		}
	}

	public BigDecimal getCachedNetDeliveryCost() {
		return cachedNetDeliveryCost;
	}

	public void setCachedNetDeliveryCost(BigDecimal cachedNetDeliveryCost) {
		this.cachedNetDeliveryCost = cachedNetDeliveryCost;
	}
}





