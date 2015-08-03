package com.aplos.ecommerce.beans.product;

import java.math.BigDecimal;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.cms.interfaces.GeneratorMenuItem;
import com.aplos.common.FileDetailsOwnerHelper;
import com.aplos.common.SaveableFileDetailsOwnerHelper;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Cache;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.FetchType;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosSiteBean;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.interfaces.FileDetailsOwnerInter;
import com.aplos.common.servlets.MediaServlet;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.ImageUtil;
import com.aplos.ecommerce.beans.Brochure.BrochureImageKey;
import com.aplos.ecommerce.enums.EcommerceWorkingDirectory;
import com.aplos.ecommerce.interfaces.ProductListRoot;
import com.aplos.ecommerce.utils.EcommerceUtil;

@Entity
@ManagedBean
@SessionScoped
@Cache
@DynamicMetaValueKey(oldKey={"PRODUCT_BRAND","P_BRAND"})
public class ProductBrand extends AplosSiteBean implements FileDetailsOwnerInter, GeneratorMenuItem, ProductListRoot {
	private static final long serialVersionUID = 5001258703031397115L;

	private String name;
//	private String imageUrl;
//	private String smallImageUrl;
	@Column(columnDefinition="LONGTEXT")
	private String description;
	private Boolean isFreePostage = false;
	private BigDecimal companyDiscountPercentage = new BigDecimal( 0 );
	private boolean allowExtraCompanyDiscount;
	private String mapping;

	private String metaTitle;

	@Column(columnDefinition="LONGTEXT")
	private String metaDescription;

	@Column(columnDefinition="LONGTEXT")
	private String metaKeywords;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private FileDetails imageDetails;
	@ManyToOne(fetch=FetchType.LAZY)
	private FileDetails smallImageDetails;
	
	@Transient
	private ProductBrandFdoh productBrandFdoh = new ProductBrandFdoh(this);
	
	private enum ProductBrandImageEnum {
		SMALL,
		MEDIUM;
	}
	
	public ProductBrand() {
	}
	
	@Override
	public FileDetailsOwnerHelper getFileDetailsOwnerHelper() {
		return productBrandFdoh;
	}
	
	@Override
	public String getProductListWhereClause() {
		return "WHERE bean.productInfo.product.productBrand.id ";
	}
	
	@Override
	public String getProductListUnfilteredWhereClause() {
		return "bean.productInfo.product.productBrand.id=" + getId();
	}
	
	@Override
	public String getProductListRootTitleImageUrl() {
		return getFullImageUrl();
	}
	
	@Override
	public void saveBean(SystemUser currentUser) {
		if (getMapping() == null || getMapping().equals( "" )) {
			setMapping( createMapping());
		} else {
			//remove anything nasty
			setMapping( CommonUtil.makeSafeUrlMapping( getMapping() ) );
			if (getMapping() == null || getMapping().equals( "" )) {
				setMapping( createMapping());
			}
		}
		FileDetails.saveFileDetailsOwner(this, BrochureImageKey.values(), currentUser);
	}

	public String createMapping() {
		String _mapping = CommonUtil.makeSafeUrlMapping(name);
		BeanDao productBrandDao = new BeanDao( ProductBrand.class );
		productBrandDao.setSelectCriteria( "bean.mapping" );
		productBrandDao.addWhereCriteria( "bean.mapping LIKE :mapping" );
		productBrandDao.setNamedParameter( "mapping", _mapping + "%" );
		List<String> mappings = productBrandDao.getResultFields();
		int count = 0;
		String newmapping = _mapping;
		while (mappings.contains( newmapping )) {
			newmapping = _mapping.concat( String.valueOf(++count) );
		}
		return newmapping;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public String getGeneratorMenuUrl() {
		return EcommerceUtil.getEcommerceUtil().getProductBrandMapping( this );
	}
	
	@Override
	public String getFullMenuItemImageUrl() {
		return getFullSmallImageUrl();
	}
	
	@Override
	public String getGeneratorMenuItemMapping() {
		return getMappingOrId();
	}

	public String getMappingOrId() {
		if (mapping != null && !mapping.equals("")) {
			return mapping;
		}
		return getId().toString();
	}

	@Override
	public String getDisplayName() {
		return name;
	}

	public void setName(String newName) {
		this.name = newName;
	}

	public String getFullImageUrl() {
		return getFullImageUrl(true) ;
	}

	public String getFullSmallImageUrl() {
		return getFullSmallImageUrl(true) ;
	}

	public String getFullSmallImageUrl(boolean addContext) {
		return MediaServlet.getImageUrl(getSmallImageDetails(), EcommerceWorkingDirectory.PRODUCT_BRAND_IMAGE_DIR.getDirectoryPath(true), addContext);
	}

	public String getFullMediumPhotoUrl( boolean addContextPath ) {
		return getFullImageUrl( addContextPath );
	}

	public String getFullImageUrl(boolean addContextPath) {
		return ImageUtil.getFullFileUrl( getImageDetails(), addContextPath );
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String newDescription) {
		this.description = newDescription;
	}

	public void setIsFreePostage(Boolean isFreePostage) {
		this.isFreePostage = isFreePostage;
	}

	public Boolean getIsFreePostage() {
		return isFreePostage;
	}

	public void setAllowExtraCompanyDiscount(boolean allowExtraCompanyDiscount) {
		this.allowExtraCompanyDiscount = allowExtraCompanyDiscount;
	}

	public boolean isAllowExtraCompanyDiscount() {
		return allowExtraCompanyDiscount;
	}

	public void setCompanyDiscountPercentage(BigDecimal companyDiscountPercentage) {
		this.companyDiscountPercentage = companyDiscountPercentage;
	}

	public BigDecimal getCompanyDiscountPercentage() {
		return companyDiscountPercentage;
	}

	public void setMapping(String mapping) {
		this.mapping = mapping;
	}

	public String getMapping() {
		return mapping;
	}

	public String getMetaKeywords() {
		return metaKeywords;
	}

	public void setMetaKeywords(String metaKeywords) {
		this.metaKeywords = metaKeywords;
	}

	public String getMetaDescription() {
		return metaDescription;
	}

	public void setMetaDescription(String metaDescription) {
		this.metaDescription = metaDescription;
	}

	public enum BrandImageKey {
		MEDIUM,
		SMALL;
	}
	
	@Override
	public void superSaveBean(SystemUser currentUser) {
		super.saveBean(currentUser);
	}

	public FileDetails getImageDetails() {
		return imageDetails;
	}

	public void setImageDetails(FileDetails imageDetails) {
		this.imageDetails = imageDetails;
	}

	public FileDetails getSmallImageDetails() {
		return smallImageDetails;
	}

	public void setSmallImageDetails(FileDetails smallImageDetails) {
		this.smallImageDetails = smallImageDetails;
	}

	public String getMetaTitle() {
		return metaTitle;
	}

	public void setMetaTitle(String metaTitle) {
		this.metaTitle = metaTitle;
	}
	
	private class ProductBrandFdoh extends SaveableFileDetailsOwnerHelper {
		public ProductBrandFdoh( ProductBrand productBrand ) {
			super( productBrand );
		}

		@Override
		public String getFileDetailsDirectory(String fileDetailsKey, boolean includeServerWorkPath) {
			if (BrandImageKey.MEDIUM.name().equals(fileDetailsKey) ||
					BrandImageKey.SMALL.name().equals(fileDetailsKey)) {
				return EcommerceWorkingDirectory.PRODUCT_BRAND_IMAGE_DIR.getDirectoryPath(includeServerWorkPath);
			}
			return null;
		}

		@Override
		public void setFileDetails(FileDetails fileDetails, String fileDetailsKey, Object collectionKey) {
			if (BrandImageKey.MEDIUM.name().equals(fileDetailsKey)) {
				setImageDetails(fileDetails);		
			} else if (BrandImageKey.SMALL.name().equals(fileDetailsKey)) {
				setSmallImageDetails(fileDetails);		
			}
		}

		@Override
		public FileDetails getFileDetails(String fileDetailsKey, Object collectionKey) {
			if( BrandImageKey.MEDIUM.name().equals( fileDetailsKey ) ) {
				return getImageDetails();
			} else if( BrandImageKey.SMALL.name().equals( fileDetailsKey ) ) {
				return getSmallImageDetails();
			}
			return null;
		}
	}

}