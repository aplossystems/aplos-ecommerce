package com.aplos.ecommerce.beans;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.aplos.common.annotations.persistence.Cache;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToMany;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.PaymentGatewayPost;
import com.aplos.common.beans.ShoppingCartItem;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.ecommerce.interfaces.SerialNumberOwner;
import com.aplos.ecommerce.module.EcommerceConfiguration;

@Entity
@Cache
public class EcommerceShoppingCartItem extends ShoppingCartItem implements SerialNumberOwner {

	private static final long serialVersionUID = 7341468149849846147L;

	// There doesn't need to be realizedProduct for an order as the details
	// of the shopping cart item are populated from a realized product but don't
	// have to be, this should only be stored as a reference and not relied upon.
	@ManyToOne
	private RealizedProduct realizedProduct;
	
	@ManyToMany
	private List<RealizedProduct> kitItems;
	private boolean isDispatched = false;
	private Date dispatchDate;
	private BigDecimal singleItemWeight = new BigDecimal( 0 );
	private BigDecimal singleItemVolume = new BigDecimal( 0 );
	private BigDecimal cachedTotalWeight = new BigDecimal( 0 );
	private BigDecimal cachedTotalVolume = new BigDecimal( 0 );
	private String customerReference;
	private boolean isGiftItem = false;

	public EcommerceShoppingCartItem() {}

	public EcommerceShoppingCartItem( EcommerceShoppingCart ecommerceShoppingCart, RealizedProduct realizedProduct) {
		setShoppingCart( ecommerceShoppingCart );
		this.realizedProduct = realizedProduct;
		updateBaseClassValues();
	}

	@Override
	public String getCachedLinePriceString() {
		return getCachedLinePriceString(false);
	}

	public EcommerceShoppingCartItem getClone() {
		try {
			return (EcommerceShoppingCartItem) this.clone();
		} catch ( CloneNotSupportedException cnsEx ) {
			ApplicationUtil.getAplosContextListener().handleError( cnsEx );
		}

		return null;
	}

	@Override
	public String getCachedLinePriceString(boolean convert) {
		if (CommonConfiguration.getCommonConfiguration().isVatInclusive()) {
			return FormatUtil.formatTwoDigit( getCachedGrossLinePrice(convert).doubleValue() );
		} else {
			return FormatUtil.formatTwoDigit( getCachedNetLinePrice(convert).doubleValue() );
		}
	}

	public void depleteStockQuantities() {
		if( getRealizedProduct() != null && !EcommerceConfiguration.getEcommerceSettingsStatic().isUsingSerialNumbers() ) {
			List<IncludedProduct> includedProducts = ((RealizedProduct) getRealizedProduct()).getProductInfo().getIncludedProducts();
			if( includedProducts.size() > 0 && EcommerceConfiguration.getEcommerceSettingsStatic().isKitItemsFixed() ) {
				for( IncludedProduct tempIncludedProduct : includedProducts ) {
					RealizedProduct includedRealizedProduct = tempIncludedProduct.getRealizedProductRetriever().retrieveRealizedProduct( ((RealizedProduct) getRealizedProduct()) );

					includedRealizedProduct.setQuantity( includedRealizedProduct.getQuantity() - (getQuantity() * tempIncludedProduct.getQuantity()) );
					includedRealizedProduct.saveDetails();
				}
			} else {
				((RealizedProduct) getRealizedProduct()).setQuantity( ((RealizedProduct) getRealizedProduct()).getQuantity() - getQuantity() );
				((RealizedProduct) getRealizedProduct()).saveDetails();
			}
		}
	}
	
	@Override
	public void itemPurchased(PaymentGatewayPost paymentGatewayPost) {
		if( getRealizedProduct() != null ) {
			getRealizedProduct().productPurchased( this );
		}
	}

	@Override
	public String getSingleItemFinalPriceString() {
		return getSingleItemFinalPriceString(false);
	}

	@Override
	public String getSingleItemFinalPriceString(boolean convert) {
		return getSingleItemFinalPriceString( !CommonConfiguration.getCommonConfiguration().isVatInclusive(), convert );
	}

	@Override
	public void updateVatPercentage() {
		super.updateVatPercentage();

		if( !getShoppingCart().isVatExempt() ) {
			if( getRealizedProduct() != null ) {
				if( ((RealizedProduct) getRealizedProduct()).getVatType() == null ) {
					if( CommonConfiguration.getCommonConfiguration().getDefaultVatType() != null ) {
						setVatPercentage( CommonConfiguration.getCommonConfiguration().getDefaultVatType().getPercentage() );
					} else {
						setVatPercentage( new BigDecimal( 0 ) );
					}
				} else {
					setVatPercentage( ((RealizedProduct) getRealizedProduct()).getVatType().getPercentage() );
				}
			} else {
				setVatPercentage( CommonConfiguration.getCommonConfiguration().getUkVatType().getPercentage() );
			}
		} else {
			setVatPercentage( new BigDecimal( 0 ) );
		}
	}

	@Override
	public void calculateVatAmount( boolean updateCachedValues, boolean updateCartCachedValues ) {
		/*
		 *  Need to do a special check to make sure that the net price and vat amount add up correctly
		 *  for the inclusive case, otherwise it's possible to get roundings to add or remove a penny.
		 */
		if (CommonConfiguration.getCommonConfiguration().isVatInclusive()  ) {
			setSingleItemVatAmount( CommonUtil.getInclusivePercentageAmountAndRound( getSingleItemBasePrice().subtract( getSingleItemDiscountAmount() ), getVatPercentage() ).setScale( 2, RoundingMode.HALF_DOWN ) );
		} else {
			BigDecimal itemAmountAfterDiscount = getSingleItemNetPrice().subtract( getSingleItemDiscountAmount() );
			setSingleItemVatAmount( CommonUtil.getPercentageAmountAndRound( itemAmountAfterDiscount, getVatPercentage() ).setScale( 2, RoundingMode.HALF_DOWN ) );
		}

		if( updateCachedValues ) {
			updateCachedValues( updateCartCachedValues );
		}
	}

	@Override
	public BigDecimal getSingleItemDiscountPercentage() {
		BigDecimal totalDiscount  = new BigDecimal(0);
		if( getRealizedProduct() != null && !((RealizedProduct) getRealizedProduct()).isGiftVoucher()) {
			totalDiscount = super.getSingleItemDiscountPercentage();
			if (((EcommerceShoppingCart)getShoppingCart()).getPromotionPercentage() != null) {
				if (((EcommerceShoppingCart)getShoppingCart()).getPromotion() != null) {
				
					Promotion promotion = ((EcommerceShoppingCart)getShoppingCart()).getPromotion();
					if (promotion.getConstraints().size() > 0) {
					
						BigDecimal promotionalDiscountToAdd = null;
						
						for (PromotionConstraint constraint : promotion.getConstraints()) {
							boolean satisfactory = constraint.doesCartItemSatisfyConstraint(this, promotion.getCode());
							
							if (satisfactory) {
								if ( constraint.getDiscountPercentage() != null && constraint.getDiscountPercentage().doubleValue() > 0 ) {
									//if we were going to add the default (all items) discount but found this one (more specific), switch to this one.
									if (promotionalDiscountToAdd == null || promotionalDiscountToAdd.equals( ((EcommerceShoppingCart)getShoppingCart()).getPromotionPercentage() ) ) {
										promotionalDiscountToAdd = constraint.getDiscountPercentage();
									}
								} else {
									if (promotionalDiscountToAdd == null) {
										promotionalDiscountToAdd = ((EcommerceShoppingCart)getShoppingCart()).getPromotionPercentage();
									}
								}
								
							}
							
						}
						
						if (promotionalDiscountToAdd != null) {
							totalDiscount = totalDiscount.add( promotionalDiscountToAdd );
						}
				
					} else {
						
						//take the cached value
						totalDiscount = totalDiscount.add( ((EcommerceShoppingCart)getShoppingCart()).getPromotionPercentage() );
					}
					
				}
			}
		} else {
			return super.getSingleItemDiscountPercentage();
		}
		return totalDiscount;
	}

	public void updateBaseClassValues() {
		RealizedProduct localRealizedProduct = (RealizedProduct) this.realizedProduct;
		if( localRealizedProduct != null ) {
			localRealizedProduct.updateCartItemValues( this );
		}
		updateVatPercentage();
		updateSingleItemNetPrice();
	}

	public RealizedProduct getRealizedProduct() {
		return (RealizedProduct)realizedProduct;
	}

	public void setRealizedProduct(RealizedProduct newRealizedProduct) {
		this.realizedProduct = newRealizedProduct;
	}

	public String getProductColour() {
		RealizedProduct localRealizedProduct = (RealizedProduct) this.realizedProduct;
		if( localRealizedProduct != null ) {
			return localRealizedProduct.getProductColour().getName();
		} else {
			return "";
		}
	}

	@Override
	public void updateAllValues() {
		updateWeightValue( false );
		super.updateAllValues();
	}

	@Override
	public void updateCachedValues(boolean updateCartValues) {
		updateWeightValue( false );
		super.updateCachedValues(updateCartValues);
	}

	public void updateWeightValue( boolean updateCartWeightValue ) {
		if( getSingleItemWeight() == null ) {
			setCachedTotalWeight( new BigDecimal( 0 ) );
		} else {
			setCachedTotalWeight( getSingleItemWeight().multiply( new BigDecimal( getQuantity() ) ) );
		}
		if( getSingleItemVolume() == null ) {
			setCachedTotalVolume( new BigDecimal( 0 ) );
		} else {
			setCachedTotalVolume( getSingleItemVolume().multiply( new BigDecimal( getQuantity() ) ) );
		}

		if( updateCartWeightValue ) {
			((EcommerceShoppingCart) getShoppingCart()).updateWeightCachedValue();
		}
	}

	public String getProductSize() {
		RealizedProduct localRealizedProduct = (RealizedProduct) this.realizedProduct;
		if( localRealizedProduct != null && localRealizedProduct.getProductSize() != null ) {
			return localRealizedProduct.getProductSize().getName();
		} else {
			return "";
		}
	}

	public String getProductImageUrl() {
		RealizedProduct localRealizedProduct = (RealizedProduct) this.realizedProduct;
		if( localRealizedProduct != null ) {
			return localRealizedProduct.getDefaultImageDetails().getFullFileUrl(true);
		} else {
			return "";
		}
	}

	public void setDispatched(boolean isDispatched) {
		this.isDispatched = isDispatched;
	}

	public boolean isDispatched() {
		return isDispatched;
	}

	public void setDispatchDate(Date dispatchDate) {
		this.dispatchDate = dispatchDate;
	}

	public Date getDispatchDate() {
		return dispatchDate;
	}

	@Override
	public Transaction getAssociatedTransaction() {
		BeanDao transactionDao = new BeanDao( Transaction.class );
		transactionDao.addQueryTable( "esci", "bean.ecommerceShoppingCart.items");
		transactionDao.addWhereCriteria( "esci.id = " + getId() );
		List<Transaction> transactionList = transactionDao.setIsReturningActiveBeans(true).getAll();
		if( transactionList.size() == 1 ) {
			return transactionList.get( 0 );
		} else {
			return null;
		}
	}

	@Override
	public Long getAssociatedTransactionId() {
		BeanDao transactionDao = new BeanDao( Transaction.class );
		transactionDao.addQueryTable( "esci", "bean.ecommerceShoppingCart.items");
		transactionDao.setSelectCriteria( "bean.id" );
		transactionDao.addWhereCriteria( "esci.id = " + getId() );
		List<Long> transactionList = transactionDao.getResultFields();
		if( transactionList.size() == 1 ) {
			return transactionList.get( 0 );
		} else {
			return null;
		}
	}

	public List<SerialNumber> getSerialNumberAssignments() {
		if( getId() != null ) {
			BeanDao assignmentDao = new BeanDao( SerialNumber.class );
			assignmentDao.addWhereCriteria( "bean.serialNumberOwner.id = " + getId() + " AND bean.serialNumberOwner.class = 'EcommerceShoppingCartItem'" );
			List<SerialNumber> assignmentList = assignmentDao.setIsReturningActiveBeans(true).getAll();
			return assignmentList;
		} else {
			return new ArrayList<SerialNumber>();
		}

	}

	public String getSerialNumberAssignmentsStr() {
		Long[] serialNumbers = new Long[ getSerialNumberAssignments().size() ];
		for( int i = 0, n = getSerialNumberAssignments().size(); i < n; i++ ) {
			serialNumbers[ i ] = getSerialNumberAssignments().get( i ).getId();
		}

		return StringUtils.join( serialNumbers, "," );
	}

	public void setCachedTotalWeight(BigDecimal cachedTotalWeight) {
		this.cachedTotalWeight = cachedTotalWeight;
	}

	public BigDecimal getCachedTotalWeight() {
		return cachedTotalWeight;
	}

	public void setCustomerReference(String customerReference) {
		this.customerReference = customerReference;
	}

	public String getCustomerReference() {
		return customerReference;
	}

	public void setSingleItemWeight(BigDecimal singleItemWeight) {
		this.singleItemWeight = singleItemWeight;
	}

	public BigDecimal getSingleItemWeight() {
		return singleItemWeight;
	}

	public void setCachedTotalVolume(BigDecimal cachedTotalVolume) {
		this.cachedTotalVolume = cachedTotalVolume;
	}

	public BigDecimal getCachedTotalVolume() {
		return cachedTotalVolume;
	}

	public void setSingleItemVolume(BigDecimal singleItemVolume) {
		this.singleItemVolume = singleItemVolume;
	}

	public BigDecimal getSingleItemVolume() {
		return singleItemVolume;
	}

	public void setGiftItem(boolean isGiftItem) {
		this.isGiftItem = isGiftItem;
	}

	/**
	 * Returns whether the ShoppingCartItem has been marked as a gift item
	 * protected as usually we should use {@link: EcommerceShoppingCartItem#determineIsGiftItem()}
	 * which accounts for gift vouchers and any future settings
	 * @return
	 */
	protected boolean isGiftItem() {
		return isGiftItem;
	}

	public boolean determineIsGiftItem() {
		return isGiftItem || (getRealizedProduct() != null && getRealizedProduct().isGiftVoucher());
	}

	public void setKitItems(List<RealizedProduct> kitItems) {
		this.kitItems = kitItems;
	}

	public List<RealizedProduct> getKitItems() {
		return kitItems;
	}

	/**
	 * @return the realized product display name with its kit items listed beneath it if applicable
	 */
	public String getProductNameText( boolean isUsingRealizedProductName ) {
		RealizedProduct localRealizedProduct = (RealizedProduct) this.realizedProduct;
		if (localRealizedProduct == null) {
			return null;
		}
		StringBuffer buff = new StringBuffer(localRealizedProduct.getProductInfo().getProduct().getName());
		if (!EcommerceConfiguration.getEcommerceSettingsStatic().isKitItemsFixed() && kitItems != null && kitItems.size() > 0) {
			buff.append("<ul class=\"apos-kit-contents\">");
			for (RealizedProduct item : kitItems) {
				buff.append("<li>");
				if( isUsingRealizedProductName ) {
					buff.append(item.getDisplayName());
				} else {
					buff.append(item.getProductInfo().getProduct().getName());
				}
				buff.append("</li>");
			}
			buff.append("</ul>");
		}
		return buff.toString();
	}

	public String getProductColourText() {
		RealizedProduct localRealizedProduct = (RealizedProduct) this.realizedProduct;
		if (localRealizedProduct == null) {
			return null;
		}
		StringBuffer buff = new StringBuffer();
		if (!EcommerceConfiguration.getEcommerceSettingsStatic().isKitItemsFixed() && kitItems != null && kitItems.size() > 0) {
			buff.append("<span>-</span>");
			buff.append("<ul class='apos-kit-contents'>");
			for (RealizedProduct item : kitItems) {
				buff.append("<li>");
				buff.append(item.getProductColour().getName());
				buff.append("</li>");
			}
			buff.append("</ul>");
		} else {
			buff.append(localRealizedProduct.getProductColour().getName());
		}
		return buff.toString();
	}

	public String getProductSizeText() {
		RealizedProduct localRealizedProduct = (RealizedProduct) this.realizedProduct;
		if (localRealizedProduct == null) {
			return null;
		}
		StringBuffer buff = new StringBuffer();

		if (!EcommerceConfiguration.getEcommerceSettingsStatic().isKitItemsFixed() && kitItems != null && kitItems.size() > 0) {
			buff.append("<span>-</span>");
			buff.append("<ul class='apos-kit-contents'>");
			for (RealizedProduct kitItem : kitItems) {
				buff.append("<li>");
				buff.append(kitItem.getProductSize().getName());
				if (kitItem.getProductSizeCategory() != null) {
					buff.append(" ");
					buff.append(kitItem.getProductSizeCategory().getSuffix());// suffix not Name()); - need it shorter backend
				}
				buff.append("</li>");
			}
			buff.append("</ul>");
		} else {
			buff.append(localRealizedProduct.getProductSize().getName());
			if (localRealizedProduct.getProductSizeCategory() != null) {
				buff.append(" ");
				buff.append(localRealizedProduct.getProductSizeCategory().getName());
			}
		}
		return buff.toString();
	}
}
