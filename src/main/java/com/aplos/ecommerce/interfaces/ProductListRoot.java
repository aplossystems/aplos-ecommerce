package com.aplos.ecommerce.interfaces;


public interface ProductListRoot {
	public String getDisplayName();
	public String getName();
	public String getProductListWhereClause();
	public String getProductListUnfilteredWhereClause();
	public String getMetaTitle();
	public String getMetaDescription();
	public String getMetaKeywords();
	public String getDescription();
	public String getProductListRootTitleImageUrl();
}
