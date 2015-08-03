package com.aplos.ecommerce.beans;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.application.FacesMessage;

import com.aplos.common.FileDetailsOwnerHelper;
import com.aplos.common.SaveableFileDetailsOwnerHelper;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Cache;
import com.aplos.common.annotations.persistence.Cascade;
import com.aplos.common.annotations.persistence.CascadeType;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.FetchType;
import com.aplos.common.annotations.persistence.Inheritance;
import com.aplos.common.annotations.persistence.InheritanceType;
import com.aplos.common.annotations.persistence.ManyToMany;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.Currency;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.beans.ResizedBufferedImage;
import com.aplos.common.beans.ShoppingCart;
import com.aplos.common.beans.ShoppingCartItem;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.beans.VatType;
import com.aplos.common.enums.JsfScope;
import com.aplos.common.interfaces.FileDetailsOwnerInter;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.product.ProductColour;
import com.aplos.ecommerce.beans.product.ProductInfo;
import com.aplos.ecommerce.beans.product.ProductSize;
import com.aplos.ecommerce.beans.product.ProductSizeCategory;
import com.aplos.ecommerce.beans.product.ProductType;
import com.aplos.ecommerce.enums.EcommerceWorkingDirectory;
import com.aplos.ecommerce.interfaces.RealizedProductBandSelector;
import com.aplos.ecommerce.interfaces.RealizedProductRetriever;
import com.aplos.ecommerce.module.EcommerceConfiguration;
import com.aplos.ecommerce.utils.EcommerceUtil;

@Entity
@Cache
@Inheritance(strategy=InheritanceType.JOINED)
@DynamicMetaValueKey(oldKey={"REALIZED_PRODUCT","R_PRODUCT"})
public class RealizedProduct extends AplosBean implements FileDetailsOwnerInter,RealizedProductRetriever {
	private static final long serialVersionUID = 1573501955255830575L;
	private int quantity;
	private BigDecimal price;
	private BigDecimal crossoutPrice;
	private BigDecimal sitewideDiscount;
	@ManyToOne(fetch=FetchType.LAZY)
	private ProductSize productSize;
	@ManyToOne(fetch=FetchType.LAZY)
	private ProductSizeCategory productSizeCategory=null; //this is optional
	@ManyToOne(fetch=FetchType.LAZY)
	private ProductColour productColour;
	@ManyToOne(fetch=FetchType.LAZY)
	private ProductInfo productInfo;
	private Date dateListedOnEbay = null;
	private Date stockAvailableFromDate = null;
	@ManyToMany(fetch=FetchType.LAZY)
	@Cache
	private List<FileDetails> imageDetailsList = new ArrayList<FileDetails>();
	@ManyToMany(fetch=FetchType.LAZY)
	@Cascade({CascadeType.ALL})
	@Cache
	private Set<RealizedProductPriceBand> supportedPriceBands = new HashSet<RealizedProductPriceBand>();

	@ManyToOne(fetch=FetchType.LAZY)
	private FileDetails defaultImageDetails;
	private String itemCode;
	private String barcode;

	private int minStockLevel;
	private BigDecimal weight;
	private BigDecimal packagedWeight;
	private BigDecimal packagedVolume;
	private BigDecimal productCost;
	private BigDecimal tradeDiscount;
	private boolean isHiddenFromCustomer;
	private boolean isHiddenFromCompanyContact;
	private boolean isSerialNumberRequired = true;
	private boolean discontinued;
	private boolean isCustomisable = false;
	private boolean isDiscountAllowed = true;
	private boolean isVoidStickerRequired;
	private boolean isAddedToAmazon = false;

	@ManyToOne(fetch=FetchType.LAZY)
	private VatType vatType;

	//@Transient - not keeping this transient any longer because we dont always count down
	private Integer weeksUntilStock=null;
	
	@Transient
	private RealizedProductFdoh realizedProductFdoh = new RealizedProductFdoh(this);

	public enum RealizedProductImageKey {
		SMALL_IMAGE,
		MEDIUM_IMAGE,
		LARGE_IMAGE,
		PUBLICITY_IMAGE,
		DETAIL_IMAGE_1,
		DETAIL_IMAGE_2,
		DETAIL_IMAGE_3,
		DETAIL_IMAGE_4,
		SWATCH_IMAGE;
	}

	public RealizedProduct() {
	}
	
	@Override
	public <T> T initialiseNewBean() {
		T realizedProduct = super.initialiseNewBean();
		quantity=0;
		price= new BigDecimal( "0.0" );
		productSize = new ProductSize();
		productColour = new ProductColour();
		productInfo = new ProductInfo().initialiseNewBean();
		packagedWeight = new BigDecimal( 0 );
		if( CommonConfiguration.getCommonConfiguration() != null ) {
			setVatType( CommonConfiguration.getCommonConfiguration().getDefaultVatType() );
		}
		setSerialNumberRequired(EcommerceConfiguration.getEcommerceSettingsStatic().isUsingSerialNumbers());
		return realizedProduct; 
	}
	
	@Override
	public FileDetailsOwnerHelper getFileDetailsOwnerHelper() {
		return realizedProductFdoh;
	}
	
	public RealizedProduct getCopy() {
		try {
			RealizedProduct duplicateProduct = (RealizedProduct) clone();
			duplicateProduct.setId( null );
			duplicateProduct.clearFieldsAfterCopy();
			duplicateProduct.setQuantity( 0 );
			copyNonCloneableMembers(duplicateProduct);
			return duplicateProduct;
		} catch( CloneNotSupportedException cnsEx ) {
			ApplicationUtil.getAplosContextListener().handleError( cnsEx );
		}
		return null;
	}
	
	public void copyNonCloneableMembers( RealizedProduct duplicateProduct ) {
		duplicateProduct.setProductInfo( getProductInfo().getCopy() );
		duplicateProduct.setImageDetailsList( new ArrayList<FileDetails>() );
		FileDetails tempImageDetails;
		for( int i = 0, n = getImageDetailsList().size(); i < n; i++ ) {
			tempImageDetails = getImageDetailsList().get( i ).getCopy();
			duplicateProduct.getImageDetailsList().add( tempImageDetails );
			if( getImageDetailsList().get( i ).equals( getDefaultImageDetails() ) ) {
				duplicateProduct.setDefaultImageDetails( tempImageDetails );
			}
		}
		duplicateProduct.setDefaultImageDetails( defaultImageDetails );
		duplicateProduct.setSupportedPriceBands( new HashSet<RealizedProductPriceBand>() );
		RealizedProductPriceBand supportedPriceBand;
		for( RealizedProductPriceBand realizedProductPriceBand : getSupportedPriceBands() ) {
			supportedPriceBand = realizedProductPriceBand.getCopy();
			duplicateProduct.getSupportedPriceBands().add( supportedPriceBand );
		}
	}
	
	public void saveChildrenAfterCopy() {
		for( int i = 0, n = getImageDetailsList().size(); i < n; i++ ) {
			getImageDetailsList().get( i ).saveDetails();
		}
	}
	
	@Override
	public void addToScope(JsfScope associatedBeanScope) {
		super.addToScope(associatedBeanScope);
		addToScope( CommonUtil.getBinding( RealizedProduct.class ), this, associatedBeanScope );

		if( getProductInfo() != null ) {
			getProductInfo().addToScope();
			if( getProductInfo().getProduct() != null ) {
				getProductInfo().getProduct().addToScope();
			}
		}
	}

	public boolean isLowStock() {
		return quantity < productInfo.getLowStockThreshold();
	}

	@Override
	public String getDisplayName() {
		String displayName = "";
		if (isGiftVoucher()) {
			if (productInfo.getProduct().getName() == null || productInfo.getProduct().getName().equals("")) {
				Currency currency = JSFUtil.getBeanFromScope(Currency.class);
				displayName = currency.getPrefix() + getConvertedPriceStr() + currency.getSuffix() + " Gift Voucher";
			} else {
				displayName = productInfo.getProduct().getName();
			}
		} else {
			if (productInfo != null && productInfo.getProduct() != null) {
				displayName += productInfo.getProduct().getName();
			}
			if (EcommerceConfiguration.getEcommerceSettingsStatic().isUsingProductSizes()) {
				displayName += " ";
				if (productSize != null && productSize.getName() != null && !"Not Applicable".equals( productSize.getName() ) ) {
					displayName += productSize.getName();
				}
				if (productSizeCategory != null && productSizeCategory.getSuffix() != null) {
					displayName += " " + productSizeCategory.getSuffix();
				}
			}
			if (EcommerceConfiguration.getEcommerceSettingsStatic().isUsingProductColours()) {
				if (productColour != null && productColour.getName() != null && !"Not Applicable".equals( productColour.getName() ) ) {
					displayName += " " + productColour.getName();
				}
			}
		}
		return displayName;
	}

	public void updateCartItemValues( EcommerceShoppingCartItem cartItem ) {
		cartItem.setSingleItemBasePrice( getDeterminedPrice( ((EcommerceShoppingCart) cartItem.getShoppingCart()).getRealizedProductBandSelector() ) );
		cartItem.setItemCode( determineItemCode() );
		cartItem.setItemName( getDisplayName() );
		cartItem.setSingleItemWeight( determineWeight() );
		cartItem.setSingleItemVolume( getPackagedVolume() );
		cartItem.setCustomisable( isCustomisable() );
		productInfo.updateCartItemValues( cartItem );
	}

	public String getColourOrNa() {
		if (productColour != null) {
			return productColour.getDisplayName();
		}
		return "n/a";
	}

	public String getSizeOrNa() {
		if (productSize != null) {
			return productSize.getDisplayName();
		}
		return "n/a";
	}

	// This is overridden in sub classes
	public void productPurchased( ShoppingCartItem shoppingCartItem ) {}
/*
 * Temporary method used to convert from old calculated quantity
 */
	public int calculateStockQuantity() {
		return getQuantity();
	}

	public void updateStockQuantity() {
		if( EcommerceConfiguration.getEcommerceSettingsStatic().isUsingSerialNumbers() ) {
			BeanDao serialNumberDao = new BeanDao( SerialNumber.class );
			serialNumberDao.addWhereCriteria( "bean.realizedProduct.id = " + getId() + " AND bean.serialNumberOwner IS NULL AND isAddedToWaste = false AND isReassigned = false" );
			setQuantity( serialNumberDao.getCountAll() );
		}
	}

	public ProductInfo getProductInfo(){
		return productInfo;
	}

	public void setProductInfo(ProductInfo newProductInfo) {
		productInfo = newProductInfo;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int newQuantity) {
		this.quantity = newQuantity;
	}

	/**
	 * For normal usage you should use {@link RealizedProduct#getDeterminedPrice()}
	 * @return
	 */
	public BigDecimal getPrice() {
		//return price;
		return getPrice(null);
	}

	public BigDecimal getPrice(RealizedProductBandSelector realizedProductPriceBandSelector) {
		if (realizedProductPriceBandSelector == null) {
			return price;
		} else {
			if (getSupportedPriceBands() != null) {
				RealizedProductBand selectedBand = realizedProductPriceBandSelector.getRealizedProductBand();
				if (selectedBand != null) {
					for (RealizedProductPriceBand tempBand : getSupportedPriceBands()) {
						if (tempBand.getRealizedProductBand().equals(selectedBand)) {
							return tempBand.getPrice();
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Important: this is UNCONVERTED as required for shopping carts, conversion should be done after this point
	 * @return the base price for this item, altered by discounts as necessary
	 */
	public BigDecimal getDeterminedPrice() {
		return getDeterminedPrice(null);
	}

	public BigDecimal getDeterminedPrice(RealizedProductBandSelector realizedProductBandSelector) {
		if (sitewideDiscount != null && sitewideDiscount.doubleValue() > 0) {
			return getPrice(realizedProductBandSelector).multiply( (new BigDecimal(100).subtract(sitewideDiscount)).divide(new BigDecimal(100)) );
		} else {
			BigDecimal bandedPrice = getPrice(realizedProductBandSelector);
			if (bandedPrice == null) {
				return price; //base price;
			} else {
				return bandedPrice;
			}
		}
	}

	public String getDeterminedPriceStr(RealizedProductBandSelector realizedProductBandSelector) {
		return FormatUtil.formatTwoDigit(getDeterminedPrice(realizedProductBandSelector));
	}

	/**
	 * Important: this is UNCONVERTED as required for shopping carts, conversion should be done after this point
	 * @return output from {@link RealizedProduct.getDeterminedPrice()} minus additional customer or promotion specific discounts
	 */
	public BigDecimal calculateItemDiscountedPrice() {
		EcommerceShoppingCartItem cartItem = new EcommerceShoppingCartItem((EcommerceShoppingCart)JSFUtil.getBeanFromScope(ShoppingCart.class),this);
		
		EcommerceUtil.getEcommerceUtil().calculateItemDiscount( cartItem, (Customer)JSFUtil.getBeanFromScope(Customer.class) );
		return cartItem.getSingleItemFinalPrice(false);
	}

	public String getConvertedDiscountedPriceStr() {
		return FormatUtil.formatTwoDigit( Currency.getConvertedPriceBySession( calculateItemDiscountedPrice() ).doubleValue() );
	}

	public boolean isHasLowerDiscountedPrice() {
		Customer cust = JSFUtil.getBeanFromScope(Customer.class);
		return cust != null && cust instanceof CompanyContact && calculateItemDiscountedPrice().doubleValue() < getDeterminedPrice().doubleValue();
	}

	public BigDecimal determineCrossoutPrice() {
		if (crossoutPrice != null) {
			return crossoutPrice;
		}
		if (sitewideDiscount != null && sitewideDiscount.doubleValue() > 0) {
			return price;
		}
		return null;
	}

	public String getPriceStr() {
		return FormatUtil.formatTwoDigit( getPrice() );
	}

	public String getConvertedPriceStr() {
		return FormatUtil.formatTwoDigit( Currency.getConvertedPriceBySession( getPrice() ).doubleValue() );
	}

	public String getConvertedDeterminedPriceStr() {
		return getConvertedDeterminedPriceStr(null);
	}

	public String getConvertedDeterminedPriceStr(RealizedProductBandSelector realizedProductBandSelector) {
		return FormatUtil.formatTwoDigit( Currency.getConvertedPriceBySession( getDeterminedPrice(realizedProductBandSelector) ).doubleValue() );
	}

	public void setPrice(BigDecimal newPrice) {
		this.price = newPrice;
	}

	public ProductColour getProductColour() {
		return productColour;
	}

	public void setProductColour(ProductColour newColour) {
		this.productColour = newColour;
	}

	public ProductSize getProductSize() {
		return productSize;
	}

	public void setProductSize(ProductSize newSize) {
		this.productSize = newSize;
	}

	public boolean isDefault() {
		//  This is just to safe guard that the realized product is already set
		if( productInfo.getDefaultRealizedProduct() == null && !isNew() ) {
			productInfo.setDefaultRealizedProduct( this );
			productInfo.saveDetails();
		}
		if( productInfo.getDefaultRealizedProduct().equals( this ) ) {
			return true;
		} else {
			return false;
		}
	}

	public String getSymbolRepresentationOfIsDefault(){
		return (isDefault())? "\u2713":"";
	}

	public void setDateListedOnEbay(Date listedOnEbay) {
		this.dateListedOnEbay = listedOnEbay;
	}

	public String getDateListedOnEbayAsString() {
		if (dateListedOnEbay == null) {
			return "Never";
		}
		return DateFormat.getDateInstance().format(dateListedOnEbay);
	}

	public void setImageDetailsList(List<FileDetails> imageDetailsList) {
		this.imageDetailsList = imageDetailsList;
	}

	public List<FileDetails> getImageDetailsList() {
		return imageDetailsList;
	}

	public void setImageDetailsByKey(FileDetails rpid) {
		if (rpid != null) {
			if (rpid.getFileDetailsKey() != null && !rpid.getFileDetailsKey().equals("")) {
				setImageDetailsByKey(rpid.getFileDetailsKey(), rpid);
			} else {
				if (imageDetailsList == null) {
					imageDetailsList = new ArrayList<FileDetails>();
				}
				imageDetailsList.add(rpid);
			}
		}
	}

	public void setImageDetailsByKey(RealizedProductImageKey key, FileDetails rpid) {
		setImageDetailsByKey(key.name(), rpid);
	}

	public void setImageDetailsByKey(String key, FileDetails rpid) {
		if (imageDetailsList == null) {
			imageDetailsList = new ArrayList<FileDetails>();
		}
		//replace the current value assigned to that key if it exists
		if (imageDetailsList.size() > 0) {
			for (int i=0; i <  imageDetailsList.size(); i++) {
				if (imageDetailsList.get(i).getFileDetailsKey() != null) {
					if (imageDetailsList.get(i).getFileDetailsKey().equals(key)) {
						if (rpid == null) {
							imageDetailsList.remove(i);
						} else {
							imageDetailsList.set(i, rpid);
						}
						return;
					}
				}
			}
		}
		//otherwise add it normally, the key is in the rpid itself
		if (rpid != null) {
			imageDetailsList.add(rpid);
		}
	}

	public FileDetails getImageDetailsByKey(RealizedProductImageKey key) {
		return getImageDetailsByKey(key, false);
	}

	public FileDetails getImageDetailsOrDefaultByKey(RealizedProductImageKey key) {
		return getImageDetailsByKey(key, true);
	}

	private FileDetails getImageDetailsByKey(RealizedProductImageKey key, boolean returnDefault) {
		return getImageDetailsByKey(key.name(), returnDefault);
	}

	public FileDetails getImageDetailsByKey(String key) {
		return getImageDetailsByKey(key, false);
	}

	public FileDetails getImageDetailsOrDefaultByKey(String key) {
		return getImageDetailsByKey(key, true);
	}

	private FileDetails getImageDetailsByKey(String key, boolean returnDefault) {
		if (imageDetailsList != null && imageDetailsList.size() > 0) {
			for (FileDetails details : imageDetailsList) {
				if (details.getFileDetailsKey() != null) {
					if (details.getFileDetailsKey().equals(key)) {
						return details;
					}
				}
			}
		}
		if (returnDefault) {
			return defaultImageDetails;
		} else {
			return null;
		}
	}

	private void makeImageDetails(ResizedBufferedImage resizedBufferedImage, RealizedProductImageKey key) {
		//if a previous version exists get it, making sure we dont get the default back
		FileDetails imageDetails = getImageDetailsByKey(key, false);
		if (imageDetails == null) {
			imageDetails = new FileDetails();
			imageDetails.setFileDetailsKey(key.name());
		}
		//put in our new image
		imageDetails.setName(resizedBufferedImage.getName());
		imageDetails.setResizedBufferedImage(resizedBufferedImage);
		imageDetails.saveDetails();
		
		setImageDetailsByKey(imageDetails);
	}

	@Override
	public void saveBean( SystemUser currentUser ) {

		if (crossoutPrice != null && crossoutPrice.compareTo( price ) <= 0) {
			JSFUtil.addMessage("Warning - Crossout should be greater than the current price or empty", FacesMessage.SEVERITY_WARN);
		}
		
		/*
		 * This code has been put in to check an issue with Teletest 
		 * where the product codes are changing for some reason.  It turns
		 * out it was due to multiple tabs which I've asked them to stop
		 * using.
		 */
//		if( !CommonUtil.isNullOrEmpty( getItemCode() ) && !isNew() ) {
//			String oldItemCode = (String) HibernateUtil.getCurrentSession().createQuery( "SELECT itemCode FROM " + AplosBean.getTableName( RealizedProduct.class ) + " WHERE id = " + getId() ).uniqueResult();
//			if( !getItemCode().equals( oldItemCode ) ) {
//				AplosEmail aplosEmail = new AplosEmail( "Item code changed", "ID : " + getId() + ", Old item code : " + oldItemCode + ", New Item code: " + getItemCode() );
//				aplosEmail.addToAddress( "info@aplossystems.co.uk" );
//				aplosEmail.sendAplosEmailToQueue();
//			}
//		}
		if( !isNew() ) {
			updateStockQuantity();
		}
		FileDetails.saveFileDetailsOwner( this, RealizedProductImageKey.values(), currentUser);
	}
	
	@Override
	public void superSaveBean(SystemUser currentUser) {
		super.saveBean(currentUser);
	}

	public void setDefaultImageDetails(FileDetails defaultImageDetails) {
		this.defaultImageDetails = defaultImageDetails;
	}

	public FileDetails getDefaultImageDetails() {
		return defaultImageDetails;
	}

	public String getDefaultProductListImageUrl() {
		if( defaultImageDetails != null ) {
			return defaultImageDetails.getFullFileUrl(true);
		} else {
			return "";
		}
	}

	public void setCrossoutPrice(BigDecimal crossoutPrice) {
		this.crossoutPrice = crossoutPrice;
	}

	public BigDecimal getCrossoutPrice() {
		return crossoutPrice;
	}

	public boolean getHasCrossoutPrice() {
		return (determineCrossoutPrice() != null && determineCrossoutPrice().doubleValue() > 0); //|| getSalePercentageDiscount() != null);
	}

	// This is so it can be used in el expressions
	public String getDetermineItemCode() {
		return determineItemCode();
	}

	public String determineItemCode() {
		if( getItemCode() == null ) {
			return getProductInfo().getProduct().getItemCode();
		} else {
			return getItemCode();
		}
	}

	//TOOD: I think it will be safe to remove this now
	public Double getSalePercentageDiscount() {
		return null;
//		Product product = productInfo.getProduct();
//		List<Long> typeIds = HibernateUtil.getCurrentSession().createQuery("SELECT pt.id FROM " + Product.class.getSimpleName() + " p JOIN p.productTypes AS pt WHERE p.id=" + product.getId()).list();
//		StringBuffer typeIdStrBuff = new StringBuffer();
//		for (int i=0; i < typeIds.size(); i++) {
//			if (i!=0) { typeIdStrBuff.append(","); }
//			typeIdStrBuff.append(typeIds.get(i));
//		}
//		String sql = "SELECT MAX(percentageDiscount) FROM productlistmodule WHERE DTYPE='SaleProductListModule' " +
//				"AND ((productCategory=1 AND categoryId IN (" + typeIdStrBuff + ")) OR (productCategory=0 AND categoryId=" + product.getProductBrand().getId() + "))";
//		return (Double) HibernateUtil.getCurrentSession().createSQLQuery(sql).uniqueResult();
	}

	/**
	 * @deprecated please use {@link RealizedProduct#getConvertedDeterminedCrossoutPriceStr()}
	 * @return
	 */
	@Deprecated
	public String getConvertedCrossoutPriceStr() {
		return FormatUtil.formatTwoDigit( getConvertedCrossoutPrice().doubleValue() );
	}

	/**
	 * @deprecated please use {@link RealizedProduct#getConvertedDeterminedCrossoutPrice()}
	 * @return
	 */
	@Deprecated
	public BigDecimal getConvertedCrossoutPrice() {
		Currency currency = JSFUtil.getBeanFromScope(Currency.class);
		if (crossoutPrice != null) {
			return crossoutPrice.multiply( currency.getBaseRate() );
		} else {
			return new BigDecimal( 0 );
		}
	}

	public String getConvertedDeterminedCrossoutPriceStr() {
		return FormatUtil.formatTwoDigit( getConvertedDeterminedCrossoutPrice().doubleValue() );
	}

	public BigDecimal getConvertedDeterminedCrossoutPrice() {
		Currency currency = JSFUtil.getBeanFromScope(Currency.class);
		if (determineCrossoutPrice() != null) {
			return determineCrossoutPrice().multiply( currency.getBaseRate() );
		} else {
			return new BigDecimal( 0 );
		}
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}

	public BigDecimal getWeight() {
		return weight;
	}

	public BigDecimal determineWeight() {
		if ( EcommerceConfiguration.getEcommerceSettingsStatic().isUsingPackagedWeight() ) {
			return getPackagedWeight();
		}
		else if (weight != null) {
			return getWeight();
		}
		else {
			return this.getProductInfo().getProduct().getDefaultWeight();
		}
	}

	@Override
	public int hashCode() {
//		final int multiplier = 23;
//		int code = 133;
//		code = multiplier * code + quantity;
//		code = multiplier * code + getId().intValue();
//		return code;
		return super.hashCode();
	}

	@Override
	public boolean equals(Object o) {
//		if ( this == o ) {
//			return true;
//		}
//		if (! (o instanceof RealizedProduct) ) {
//			return false;
//		}
//		return this.getId().equals(((RealizedProduct)o).getId());
		return super.equals(o);
	}

	public void setPackagedWeight(BigDecimal packagedWeight) {
		this.packagedWeight = packagedWeight;
	}

	public BigDecimal getPackagedWeight() {
		return packagedWeight;
	}

	public void setPackagedVolume(BigDecimal packagedVolume) {
		this.packagedVolume = packagedVolume;
	}

	public BigDecimal getPackagedVolume() {
		return packagedVolume;
	}

	public BigDecimal getProductCost() {
		return productCost;
	}
	
	public String getProductCostStr() {
		if (productCost == null) {
			return "<span style='color:#A83838'>Not Entered</span>";
		}
		return FormatUtil.formatTwoDigit(productCost);
	}

	public void setProductCost(BigDecimal productCost) {
		this.productCost = productCost;
	}

	public void setTradeDiscount(BigDecimal tradeDiscount) {
		this.tradeDiscount = tradeDiscount;
	}

	public BigDecimal getTradeDiscount() {
		return tradeDiscount;
	}

	public void setSerialNumberRequired(boolean isSerialNumberRequired) {
		this.isSerialNumberRequired = isSerialNumberRequired;
	}

	public boolean isSerialNumberRequired() {
		return isSerialNumberRequired;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setHiddenFromCustomer(boolean isHiddenFromCustomer) {
		this.isHiddenFromCustomer = isHiddenFromCustomer;
	}

	public boolean isHiddenFromCustomer() {
		return isHiddenFromCustomer;
	}

	public void setHiddenFromCompanyContact(boolean isHiddenFromCompanyContact) {
		this.isHiddenFromCompanyContact = isHiddenFromCompanyContact;
	}

	public boolean isHiddenFromCompanyContact() {
		return isHiddenFromCompanyContact;
	}

	public void setDiscontinued(boolean discontinued) {
		this.discontinued = discontinued;
	}

	public boolean isDiscontinued() {
		return discontinued;
	}

	public void setVoidStickerRequired(boolean isVoidStickerRequired) {
		this.isVoidStickerRequired = isVoidStickerRequired;
	}

	public boolean isVoidStickerRequired() {
		return isVoidStickerRequired;
	}

	public void setMinStockLevel(int minStockLevel) {
		this.minStockLevel = minStockLevel;
	}

	public int getMinStockLevel() {
		return minStockLevel;
	}

	public void setCustomisable(boolean isCustomisable) {
		this.isCustomisable = isCustomisable;
	}

	public boolean isCustomisable() {
		return isCustomisable;
	}

	//physical items etc = true, downloads, services etc = false
	public boolean isShippingRequired( EcommerceShoppingCartItem ecommerceShoppingCartItem ) {
		return true;
	}

	@Override
	public RealizedProduct retrieveRealizedProduct(RealizedProduct product) {
		return this;
	}

	public void setProductSizeCategory(ProductSizeCategory productSizeCategory) {
		this.productSizeCategory = productSizeCategory;
	}

	public ProductSizeCategory getProductSizeCategory() {
		return productSizeCategory;
	}

	public void setStockAvailableFromDate(Date stockAvailableFromDate) {
		this.stockAvailableFromDate = stockAvailableFromDate;
	}

	public Date getStockAvailableFromDate() {
		return stockAvailableFromDate;
	}

	public Integer getWeeksUntilStock() {
		if ((weeksUntilStock != null && weeksUntilStock < 0) || stockAvailableFromDate != null) { //the check was == -1
			if (weeksUntilStock == null || EcommerceConfiguration.getEcommerceSettingsStatic().isTimeTilAvailabilityAutoDecrement()) {
				if (stockAvailableFromDate != null) {
					Calendar now = Calendar.getInstance();
					Calendar avail = Calendar.getInstance();
					avail.setTime(stockAvailableFromDate);
					long daysBetween = 0;
					while (now.before(avail)) {
						now.add(Calendar.DATE, 1);
						daysBetween++;
					}
					int weeksBetween = Math.round(daysBetween / 7);
					if (weeksUntilStock == null) {
						//this can happen when you first switch isTimeTilAvailabilityAutoDecrement on/off
						weeksUntilStock = weeksBetween;
					}
					return weeksBetween;
				}
			} else {
				return weeksUntilStock * -1;
			}
		}
		return weeksUntilStock;
	}

	public void setWeeksUntilStock(Integer weeksUntilStock) {
		this.weeksUntilStock = weeksUntilStock;
		if (weeksUntilStock == null) {
			setStockAvailableFromDate(null);
		} else {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, weeksUntilStock * -7); //add 7 days for each week
			FormatUtil.resetTime( cal );
			setStockAvailableFromDate(cal.getTime());
			setQuantity( 0 );
		}
	}

	public List<FileDetails> getDetailImageList() {
		List<FileDetails>  details = new ArrayList<FileDetails>();
		/*
		 * This was removed because BigMatts products weren't showing correctly as the suits
		 * had their own images that should have been showing.  
		 */
//		if (this.getProductInfo().isKitItem()) {
//			for (IncludedProduct product : getProductInfo().getIncludedProducts()) {
//				details.addAll(product.getRealizedProductRetriever().retrieveRealizedProduct(this).getDetailImageList());
//			}
//		} else {
			FileDetails detail = getImageDetailsByKey(RealizedProductImageKey.LARGE_IMAGE);
			details.add(detail);
			detail = getImageDetailsByKey(RealizedProductImageKey.DETAIL_IMAGE_1);
			if (detail != null) {
				details.add(detail);
			}
			detail = getImageDetailsByKey(RealizedProductImageKey.DETAIL_IMAGE_2);
			if (detail != null) {
				details.add(detail);
			}
			detail = getImageDetailsByKey(RealizedProductImageKey.DETAIL_IMAGE_3);
			if (detail != null) {
				details.add(detail);
			}
			detail = getImageDetailsByKey(RealizedProductImageKey.DETAIL_IMAGE_4);
			if (detail != null) {
				details.add(detail);
			}
//		}
		return details;
	}

	public boolean isGiftVoucher() {
		if (productInfo != null && productInfo.getProduct() != null) {
			List<ProductType> types = productInfo.getProduct().getProductTypes();
			return types != null && types.contains(EcommerceConfiguration.getEcommerceConfiguration().getGiftVoucherProductType());
		} else {
			return false;
		}
	}

	public void setSitewideDiscount(BigDecimal siteWideDiscount) {
		this.sitewideDiscount = siteWideDiscount;
	}

	public BigDecimal getSitewideDiscount() {
		return sitewideDiscount;
	}

	/**
	 * Used to have a RealizedProduct determine which sitewide discount it should use
	 * @param discountList
	 */
	public void takeSitewideDiscount(List<SitewideDiscount> discountList) {
		BigDecimal highestDiscount = null;
		for (SitewideDiscount discount : discountList) {
			if (discount.getProductBrand() != null) {
				if (discount.getProductBrand().equals(getProductInfo().getProduct().getProductBrand())) {
					if (discount.getProductType() != null) {
						for (ProductType rpType : getProductInfo().getProduct().getProductTypes()) {
							if (discount.getProductType().equals(rpType)) {
								if (highestDiscount == null || highestDiscount.doubleValue() < discount.getDiscountPercentage().doubleValue()) {
									highestDiscount = discount.getDiscountPercentage();
								}
							}
						}
					} else {
						if (highestDiscount == null || highestDiscount.doubleValue() < discount.getDiscountPercentage().doubleValue()) {
							highestDiscount = discount.getDiscountPercentage();
						}
					}
				}
			} else if (discount.getProductType() != null) {
				if (getProductInfo().getProduct() != null && getProductInfo().getProduct().getProductTypes() != null) {
					for (ProductType rpType : getProductInfo().getProduct().getProductTypes()) {
						if (discount.getProductType().equals(rpType)) {
							if (highestDiscount == null || highestDiscount.doubleValue() < discount.getDiscountPercentage().doubleValue()) {
								highestDiscount = discount.getDiscountPercentage();
							}
						}
					}
				}
			} else { //no brand or type limit - applies to everything
				if (highestDiscount == null || highestDiscount.doubleValue() < discount.getDiscountPercentage().doubleValue()) {
					highestDiscount = discount.getDiscountPercentage();
				}
			}
		}
		setSitewideDiscount(highestDiscount);
	}

	public void setDiscountAllowed(boolean isDiscountAllowed) {
		this.isDiscountAllowed = isDiscountAllowed;
	}

	public boolean isDiscountAllowed() {
		return isDiscountAllowed;
	}

	//used with email-a-friend atom
	//its here because theres no session in the popup window but we load the rp by id
	public String getShareUrl() {
		String url = JSFUtil.getServerUrl();
		if (url.endsWith("/")) {
			url = url.substring(0, url.length() - 1);
		}
		RealizedProduct realizedProduct = JSFUtil.getBeanFromScope( RealizedProduct.class );
		url += JSFUtil.getContextPath();
		if (!url.endsWith("/")) {
			url += "/";
		}
		if( productInfo != null ) {
			if (productInfo.getProduct().getProductBrand() != null) {
				url += productInfo.getProduct().getProductBrand().getMappingOrId() + "/";
			} else {
				url += "products/";
			}
			if (productInfo.getProduct().getProductTypes() != null && productInfo.getProduct().getProductTypes().size() > 0) {
				ProductType firstType = productInfo.getProduct().getProductTypes().get(0);
				url += firstType.getMappingOrId() + "/";
			} else {
				url += "all-products/";
			}
			url += productInfo.getMappingOrId() + ".aplos?type=product&rpid=" + getId();
		}
		return url;
	}

	public VatType getVatType() {
		return vatType;
	}

	public void setVatType(VatType vatType) {
		this.vatType = vatType;
	}

	public List<RealizedProductPriceBand> getSortedSupportedPriceBands() {
		List<RealizedProductPriceBand> sortedPriceBands = new ArrayList<RealizedProductPriceBand>(supportedPriceBands);
		RealizedProductPriceBand.sortByDisplayName( sortedPriceBands );
		return sortedPriceBands;
	}

	public Set<RealizedProductPriceBand> getSupportedPriceBands() {
		return supportedPriceBands;
	}

	public void setSupportedPriceBands(Set<RealizedProductPriceBand> supportedPriceBands) {
		this.supportedPriceBands = supportedPriceBands;
	}
	
	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public boolean isAddedToAmazon() {
		return isAddedToAmazon;
	}

	public void setAddedToAmazon(boolean isAddedToAmazon) {
		this.isAddedToAmazon = isAddedToAmazon;
	}

	private class RealizedProductFdoh extends SaveableFileDetailsOwnerHelper {
		public RealizedProductFdoh( RealizedProduct realizedProduct ) {
			super( realizedProduct );
		}

		@Override
		public FileDetails getFileDetails(String fileDetailsKey, Object collectionKey) {
			return getImageDetailsByKey( fileDetailsKey );
		}

		@Override
		public void setFileDetails(FileDetails fileDetails, String fileDetailsKey, Object collectionKey) {
			setImageDetailsByKey( fileDetailsKey, (FileDetails) fileDetails );
		}
		
		@Override
		public FileDetails createCustomFileDetails(String fileDetailsKey) {
			FileDetails realizedProductImageDetails = new FileDetails();
			realizedProductImageDetails.setFileDetailsKey(fileDetailsKey);
			return realizedProductImageDetails;
		}
		
		@Override
		public String getFileDetailsDirectory(String fileDetailsKey, boolean includeServerWorkPath) {
			return EcommerceWorkingDirectory.REALIZEDPRODUCT_IMAGE_DIR.getDirectoryPath(includeServerWorkPath);
		}
	}
}
