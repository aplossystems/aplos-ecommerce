package com.aplos.ecommerce.beans.product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.aplos.common.annotations.persistence.Cache;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.FetchType;
import com.aplos.common.annotations.persistence.ManyToMany;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.beans.AplosBean;

@Entity
@Cache
public class Product extends AplosBean {
	private static final long serialVersionUID = -5280191599521078730L;
	
	private String name;
	//  Name of play.com integration
	private String playComName;
	private String amazonName;
	private String itemCode;
	private boolean isShowingOnWebsite = true;
	@ManyToMany(fetch=FetchType.LAZY)
	private List<ProductType> productTypes;
	@ManyToMany(fetch=FetchType.LAZY)
	private List<ProductSizeType> additionalProductSizeTypes;
	@ManyToOne(fetch=FetchType.LAZY)
	private ProductBrand productBrand;

	private BigDecimal defaultWeight;

	@Column(columnDefinition="LONGTEXT")
	private String notes;

	@Column(columnDefinition="LONGTEXT")
	private String versionDetails;

	public Product() {
		name="";
		itemCode="";
		setProductTypes(new ArrayList<ProductType>());
		//productBrand = new ProductBrand();
	}
	
	@Override
	public <T> T initialiseNewBean() {
		T product = super.initialiseNewBean();
		productTypes = new ArrayList<ProductType>();
		additionalProductSizeTypes = new ArrayList<ProductSizeType>();
		return product;
	}

	public Product getCopy(){
		Product product = new Product().<Product>initialiseNewBean();
		product.setName(getName() + " - Duplicate");
		product.setItemCode("DUP-" + getItemCode());
		product.setShowingOnWebsite(isShowingOnWebsite());
		//if you dont create new lists you get shared references exceptions
		List<ProductType> newProductTypes = new ArrayList<ProductType>();
		for (ProductType type : getProductTypes()) {
			newProductTypes.add(type);
		}
		product.setProductTypes(newProductTypes);
		List<ProductSizeType> newAdditionalProductSizeTypes = new ArrayList<ProductSizeType>();
		for (ProductSizeType type : getAdditionalProductSizeTypes()) {
			newAdditionalProductSizeTypes.add(type);
		}
		product.setAdditionalProductSizeTypes(newAdditionalProductSizeTypes);
		product.setProductBrand(getProductBrand());
		product.setDefaultWeight(getDefaultWeight());
		product.setNotes(getNotes());
		product.setVersionDetails(getVersionDetails());
		return product;
	}

	@Override
	public String getDisplayName() {
		return name;
	}

	public void addProductType( ProductType productType ) {
		if (!productTypes.contains(productType)) {
			productTypes.add( productType );
		}
	}

	public void addAdditionalSizeType( ProductSizeType productSizeType ) {
		if (!additionalProductSizeTypes.contains(productSizeType)) {
			additionalProductSizeTypes.add( productSizeType );
		}
	}

	public boolean getHasVideos() {
		if (productTypes != null) {
			for (ProductType productType : productTypes) {
				if (productType.getHasVideos()) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean getHasReviews() {
		if (productTypes != null) {
			for (ProductType productType : productTypes) {
				if (productType.getHasReviews()) {
					return true;
				}
			}
		}
		return false;
	}

	public String getProductTypeSlashString() {
		if (productTypes == null) {
			return "";
		}
		StringBuffer buff = new StringBuffer();
		for (ProductType type : productTypes) {
			if (buff.length() > 0) {
				buff.append(" / ");
			}
			buff.append(type.getDisplayName());
		}
		return buff.toString();
	}

	public String getName() {
		return name;
	}

	public void setName(String newName) {
		this.name = newName;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String newItemCode) {
		this.itemCode = newItemCode;
	}

	public ProductBrand getProductBrand() {
		return productBrand;
	}

	public void setProductBrand(ProductBrand newProductBrand) {
		this.productBrand = newProductBrand;
	}

	public void setProductTypes(List<ProductType> productTypes) {
		this.productTypes = productTypes;
	}

	public List<ProductType> getProductTypes() {
		return productTypes;
	}

	public List<ProductSizeType> getProductSizeTypes() {
		List<ProductSizeType> productSizeTypeList = new ArrayList<ProductSizeType>();
		productSizeTypeList.addAll( additionalProductSizeTypes );
		for( int i = 0, n = productTypes.size(); i < n; i++ ) {
			ProductType thisType = productTypes.get( i );
			if (thisType.getProductSizeType() != null) {
				productSizeTypeList.add( thisType.getProductSizeType());
			} else {
				if ( thisType.getParentProductType() != null ) {
					productSizeTypeList.add( thisType.getParentProductType().getProductSizeType());
				}
			}
		}
		return productSizeTypeList;
	}

	public void setDefaultWeight(BigDecimal defaultWeight) {
		this.defaultWeight = defaultWeight;
	}

	public BigDecimal getDefaultWeight() {
		if ( defaultWeight != null ) {
			return defaultWeight;
		}
		else {
			return new BigDecimal( 0 );
		}
	}

	public void setAdditionalProductSizeTypes(
			List<ProductSizeType> additionalProductSizeTypes) {
		this.additionalProductSizeTypes = additionalProductSizeTypes;
	}

	public List<ProductSizeType> getAdditionalProductSizeTypes() {
		return additionalProductSizeTypes;
	}

	public void setShowingOnWebsite(boolean isShowingOnWebsite) {
		this.isShowingOnWebsite = isShowingOnWebsite;
	}

	public boolean isShowingOnWebsite() {
		return isShowingOnWebsite;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getNotes() {
		return notes;
	}

	public void setVersionDetails(String versionDetails) {
		this.versionDetails = versionDetails;
	}

	public String getVersionDetails() {
		return versionDetails;
	}

	public String getPlayComName() {
		return playComName;
	}

	public void setPlayComName(String playComName) {
		this.playComName = playComName;
	}

	public String getAmazonName() {
		return amazonName;
	}

	public void setAmazonName(String amazonName) {
		this.amazonName = amazonName;
	}
}















