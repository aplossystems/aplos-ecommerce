package com.aplos.ecommerce.beans.ebay;
import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.beans.AplosBean;

@Entity
@PluralDisplayName(name="Ebay persistent categories")
public class EbayPersistentCategory extends AplosBean {

	private static final long serialVersionUID = 6205039322199654661L;
	@Column(columnDefinition="LONGTEXT")
	private String categoryName = "";
	private int categoryId = -1;
	//private int parentId = -1;
	//private String keywords = "";

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public int getCategoryId() {
		return categoryId;
	}

	/*public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public int getParentId() {
		return parentId;
	}*/

	/*public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getKeywords() {
		return keywords;
	}*/

}
