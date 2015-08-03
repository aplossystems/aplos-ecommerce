package com.aplos.ecommerce.beans.listbeans;

import java.math.BigDecimal;

import com.aplos.ecommerce.beans.RealizedProduct;

public class RealizedProductListBean extends RealizedProduct {
	private static final long serialVersionUID = 5911185332270593997L;
	private String productName;
	private String productItemCode;
	private String productColourName;
	private String productSizeName;
	private String productSizeCategorySuffix;
	private int stockQuantity;
	private Long optionalAccessoriesListSize;
	private Long includedProductsSize;

	public RealizedProductListBean() {}

	public RealizedProductListBean( String productName, BigDecimal price ) {
		setProductName( productName );
		setPrice( price );
	}

	public RealizedProductListBean( Long id, String productName ) {
		setId( id );
		setProductName( productName );
	}

	public RealizedProductListBean( Long id, String itemCode, String productItemCode  ) {
		setId( id );
		setItemCode( itemCode );
		setProductItemCode( productItemCode );
	}

	public RealizedProductListBean( Long id, String itemCode, String productItemCode, String productColourName, String productSizeName, String productSizeCategorySuffix ) {
		setId( id );
		setItemCode( itemCode );
		setProductItemCode( productItemCode );
		setProductColourName(productColourName);
		setProductSizeName(productSizeName);
		setProductSizeCategorySuffix(productSizeCategorySuffix);
	}
	
	@Override
	public boolean isReadOnly() {
		return true;
	}
	
	@Override
	public int calculateStockQuantity() {
		setStockQuantity( super.calculateStockQuantity());
		return getStockQuantity();
	}

	@Override
	public String determineItemCode() {
		if( getItemCode() == null ) {
			return productItemCode;
		} else {
			return getItemCode();
		}
	}

	public String getFirstProductTypeName() {
		if( getProductInfo().getProduct().getProductTypes().get( 0 ) != null ) {
			return getProductInfo().getProduct().getProductTypes().get( 0 ).getName();
		}

		return null;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductItemCode(String productItemCode) {
		this.productItemCode = productItemCode;
	}

	public String getProductItemCode() {
		return productItemCode;
	}

	public void setStockQuantity(int stockQuantity) {
		this.stockQuantity = stockQuantity;
	}

	public int getStockQuantity() {
		return stockQuantity;
	}

	public void setOptionalAccessoriesListSize(
			Long optionalAccessoriesListSize) {
		this.optionalAccessoriesListSize = optionalAccessoriesListSize;
	}

	public Long getOptionalAccessoriesListSize() {
		return optionalAccessoriesListSize;
	}

	public void setIncludedProductsSize(Long includedProductsSize) {
		this.includedProductsSize = includedProductsSize;
	}

	public Long getIncludedProductsSize() {
		return includedProductsSize;
	}

	public void setProductColourName(String productColourName) {
		this.productColourName = productColourName;
	}

	public String getProductColourName() {
		return productColourName;
	}

	public void setProductSizeName(String productSizeName) {
		this.productSizeName = productSizeName;
	}

	public String getProductSizeName() {
		return productSizeName;
	}

	public void setProductSizeCategorySuffix(String productSizeCategorySuffix) {
		this.productSizeCategorySuffix = productSizeCategorySuffix;
	}

	public String getProductSizeCategorySuffix() {
		return productSizeCategorySuffix;
	}
}
