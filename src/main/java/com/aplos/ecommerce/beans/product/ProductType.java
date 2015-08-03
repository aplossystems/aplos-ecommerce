package com.aplos.ecommerce.beans.product;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.cms.beans.HostedVideo;
import com.aplos.cms.interfaces.GeneratorMenuItem;
import com.aplos.common.FileDetailsOwnerHelper;
import com.aplos.common.SaveableFileDetailsOwnerHelper;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Cache;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.FetchType;
import com.aplos.common.annotations.persistence.ManyToMany;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosSiteBean;
import com.aplos.common.beans.CustomerReview;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.beans.VatType;
import com.aplos.common.interfaces.FileDetailsOwnerInter;
import com.aplos.common.servlets.MediaServlet;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.ImageUtil;
import com.aplos.ecommerce.beans.Brochure;
import com.aplos.ecommerce.beans.playcom.PlayMainGenre;
import com.aplos.ecommerce.enums.AmazonProductType;
import com.aplos.ecommerce.enums.EcommerceWorkingDirectory;
import com.aplos.ecommerce.interfaces.ProductListRoot;
import com.aplos.ecommerce.utils.EcommerceUtil;

@Entity
@ManagedBean
@SessionScoped
@Cache
@DynamicMetaValueKey(oldKey={"PRODUCT_TYPE","P_TYPE"})
public class ProductType extends AplosSiteBean implements FileDetailsOwnerInter, GeneratorMenuItem, ProductListRoot {
	private static final long serialVersionUID = 2425968696731644388L;
	@Transient
	public static final String PARENT_PRODUCT_TYPE = "parentProductType";

	private String name;
	@ManyToOne(fetch=FetchType.LAZY)
	private ProductSizeType productSizeType;

	@ManyToOne(fetch=FetchType.LAZY)
	private ProductType parentProductType;

	@Column(columnDefinition="LONGTEXT")
	private String technicalSupport;

	@Column(columnDefinition="LONGTEXT")
	private String calibrationDetails;

	private String metaTitle;

	@Column(columnDefinition="LONGTEXT")
	private String metaDescription;

	@Column(columnDefinition="LONGTEXT")
	private String metaKeywords;

	private Boolean isVoidStickerRequired;

	private Boolean isShowingOnWebsite;
	@Column(columnDefinition="LONGTEXT")
	private String description;
	
//	private String smallPhotoUrl;
//	private String mediumPhotoUrl;
//	private String publicityPhoto1Url;
//	private String publicityPhoto2Url;

	private String videoClip1Url;
	private String videoClip1Title;
	private String videoClip2Url;
	private String videoClip2Title;
	@ManyToMany(fetch=FetchType.LAZY)
	private List<HostedVideo> videos = new ArrayList<HostedVideo>();

	private String mapping;
	@ManyToOne(fetch=FetchType.LAZY)
	private VatType defaultVatType;
	/* I have given this a generic name so it can be multi-purpose
	 * depending on the individual projects needs, for teletest it chooses
	 * which categories are available in the side menu
	 */
	private Boolean isFeatured=false;

	@ManyToOne(fetch=FetchType.LAZY)
	private FileDetails smallPhotoDetails;
	@ManyToOne(fetch=FetchType.LAZY)
	private FileDetails mediumPhotoDetails;
	@ManyToOne(fetch=FetchType.LAZY)
	private FileDetails publicity1Details;
	@ManyToOne(fetch=FetchType.LAZY)
	private FileDetails publicity2Details;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private PlayMainGenre playMainGenre;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private Brochure brochure;
	
	private AmazonProductType amazonProductType;
	
	@Transient 
	private ProductTypeFdoh productTypeFdoh = new ProductTypeFdoh(this);

	public ProductType() {

	}
	
	@Override
	public FileDetailsOwnerHelper getFileDetailsOwnerHelper() {
		return productTypeFdoh;
	}
	
	@Override
	public String getProductListWhereClause() {
		return "JOIN rp.productInfo.product.productTypes AS pt WHERE pt.id ";
	}
	
	@Override
	public String getProductListUnfilteredWhereClause() {
		return "pt.id=" + getId() + " OR pt.parentProductType.id="  + getId();
	}
	
	@Override
	public String getProductListRootTitleImageUrl() {
		return getFullMediumPhotoUrl(true);
	}
	
	@Override
	public String getGeneratorMenuUrl() {
		return EcommerceUtil.getEcommerceUtil().getProductTypeMapping( this );
	}

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
	public void saveBean( SystemUser systemUser ) {
		if (getMapping() == null || getMapping().equals( "" )) {
			setMapping( createMapping());
		} else {
			//remove anything nasty
			setMapping( CommonUtil.makeSafeUrlMapping( getMapping() ) );
			if (getMapping() == null || getMapping().equals( "" )) {
				setMapping( createMapping());
			}
		}
		FileDetails.saveFileDetailsOwner(this, ProductTypeImageKey.values(), systemUser);
	}
	
	public String createMapping() {
		String _mapping = CommonUtil.makeSafeUrlMapping(name);
		BeanDao productTypeDao = new BeanDao( ProductType.class );
		productTypeDao.setSelectCriteria( "bean.mapping" );
		productTypeDao.addWhereCriteria( "bean.mapping LIKE :mapping" );
		productTypeDao.setNamedParameter( "mapping", _mapping + "%'" );
		List<String> mappings = productTypeDao.getResultFields();
		int count = 0;
		String newmapping = _mapping;
		if( mappings != null ) {
			while (mappings.contains( newmapping )) {
				newmapping = _mapping.concat( String.valueOf(++count) );
			}
		}
		return newmapping;
	}
	
	@Override
	public String getFullMenuItemImageUrl() {
		return getFullSmallPhotoUrl( true );
	}

	@Override
	public String getDisplayName() {
		if (name != null) {
			return name;
		} else {
			return "Product Type";
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String newName) {
		this.name = newName;
	}

	public ProductSizeType getProductSizeType() {
		return productSizeType;
	}

	public void setProductSizeType(ProductSizeType newST) {
		this.productSizeType = newST;
	}

	public void setParentProductType(ProductType parentProductType) {
		this.parentProductType = parentProductType;
	}

	public ProductType getParentProductType() {
		return parentProductType;
	}

	public void setTechnicalSupport(String technicalSupport) {
		this.technicalSupport = technicalSupport;
	}

	public String getTechnicalSupport() {
		return technicalSupport;
	}

	public void setIsVoidStickerRequired(Boolean isVoidStickerRequired) {
		this.isVoidStickerRequired = isVoidStickerRequired;
	}

	public Boolean getIsVoidStickerRequired() {
		return isVoidStickerRequired;
	}

	public Boolean getIsShowingOnWebsite() {
		return isShowingOnWebsite;
	}

	public void setIsShowingOnWebsite(Boolean isShowingOnWebsite) {
		this.isShowingOnWebsite = isShowingOnWebsite;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFullSmallPhotoUrl( boolean addContextPath ) {
		return MediaServlet.getImageUrl( smallPhotoDetails, EcommerceWorkingDirectory.PRODUCT_TYPE_IMAGE_DIR.getDirectoryPath(true), addContextPath );
	}

	public String getFullMediumPhotoUrl( boolean addContextPath ) {
		return ImageUtil.getFullFileUrl( getMediumPhotoDetails(), addContextPath );
	}

	public String getFullPublicityPhoto1Url( boolean addContextPath ) {
		return ImageUtil.getFullFileUrl( getPublicity1Details(), addContextPath );
	}

	public String getFullPublicityPhoto2Url( boolean addContextPath ) {
		return ImageUtil.getFullFileUrl( getPublicity2Details(), addContextPath );
	}

	public String getFullSmallPhotoUrl( boolean addContextPath, boolean addRandom ) {
		return ImageUtil.getFullFileUrl( getSmallPhotoDetails(), addContextPath );
	}

	public String getFullMediumPhotoUrl( boolean addContextPath, boolean addRandom ) {
		String path = null;
		if (mediumPhotoDetails != null) {
			return mediumPhotoDetails.getFullFileUrl(addContextPath);
		} else {
			return null;
		}
	}

	public String getFullPublicityPhoto1Url( boolean addContextPath, boolean addRandom ) {
		String path = null;
		if (publicity1Details != null) {
			return publicity1Details.getFullFileUrl(addContextPath);
		} else {
			return null;
		}
	}

	public String getFullPublicityPhoto2Url( boolean addContextPath, boolean addRandom ) {
		String path = null;
		if (publicity2Details != null) {
			return publicity2Details.getFullFileUrl(addContextPath);
		} else {
			return null;
		}
	}

	public String getVideoClip1Title() {
		return videoClip1Title;
	}

	public void setVideoClip1Title(String videoClip1Title) {
		this.videoClip1Title = videoClip1Title;
	}

	public String getVideoClip2Title() {
		return videoClip2Title;
	}

	public void setVideoClip2Title(String videoClip2Title) {
		this.videoClip2Title = videoClip2Title;
	}

	public void setMapping(String mapping) {
		this.mapping = mapping;
	}

	public String getMapping() {
		return mapping;
	}

	public List<ProductType> getProductTypesList() {
		BeanDao dao = new BeanDao(ProductType.class);
		return dao.setIsReturningActiveBeans(true).getAll();
	}

	public List<ProductType> getFeaturedProductTypesList() {
		BeanDao dao = new BeanDao(ProductType.class);
		dao.setWhereCriteria("bean.isFeatured=1");
		return dao.setIsReturningActiveBeans(true).getAll();
	}

	public void setVideoClip1Url(String videoClip1Url) {
		this.videoClip1Url = videoClip1Url;
	}

	public String getVideoClip1Url() {
		return videoClip1Url;
	}

	public void setVideoClip2Url(String videoClip2Url) {
		this.videoClip2Url = videoClip2Url;
	}

	public String getVideoClip2Url() {
		return videoClip2Url;
	}

	public void setIsFeatured(Boolean isFeatured) {
		this.isFeatured = isFeatured;
	}

	public Boolean getIsFeatured() {
		return isFeatured;
	}

	public void setVideos(List<HostedVideo> videos) {
		this.videos = videos;
	}

	public List<HostedVideo> getVideos() {
		return videos;
	}

	public boolean getHasVideos() {
			if ((this.getVideoClip1Url() != null && !this.getVideoClip1Url().equals("")) || (this.getVideoClip2Url() != null && !this.getVideoClip2Url().equals(""))) {
				return true;
			}
			if (this.getVideos() != null && this.getVideos().size() > 0) {
				return true;
			}
		return false;
	}

	public boolean getHasReviews() {
		return (CustomerReview.retrieveCustomerReviewListStatic(this).size() > 0);
	}

	public String getMetaDescription() {
		return metaDescription;
	}

	public void setMetaDescription(String metaDescription) {
		this.metaDescription = metaDescription;
	}

	public String getMetaKeywords() {
		return metaKeywords;
	}

	public void setMetaKeywords(String metaKeywords) {
		this.metaKeywords = metaKeywords;
	}

	public VatType getDefaultVatType() {
		return defaultVatType;
	}

	public void setDefaultVatType(VatType defaultVatType) {
		this.defaultVatType = defaultVatType;
	}

	public String getCalibrationDetails() {
		return calibrationDetails;
	}

	public void setCalibrationDetails(String calibrationDetails) {
		this.calibrationDetails = calibrationDetails;
	}

	public FileDetails getSmallPhotoDetails() {
		return smallPhotoDetails;
	}

	public void setSmallPhotoDetails(FileDetails smallPhotoDetails) {
		this.smallPhotoDetails = smallPhotoDetails;
	}

	public FileDetails getMediumPhotoDetails() {
		return mediumPhotoDetails;
	}

	public void setMediumPhotoDetails(FileDetails mediumPhotoDetails) {
		this.mediumPhotoDetails = mediumPhotoDetails;
	}

	public FileDetails getPublicity1Details() {
		return publicity1Details;
	}

	public void setPublicity1Details(FileDetails publicity1Details) {
		this.publicity1Details = publicity1Details;
	}

	public FileDetails getPublicity2Details() {
		return publicity2Details;
	}

	public void setPublicity2Details(FileDetails publicity2Details) {
		this.publicity2Details = publicity2Details;
	}

	
	
	public enum ProductTypeImageKey {
		SMALL_IMAGE,
		MEDIUM_IMAGE,
		PUBLICITY_IMAGE_1,
		PUBLICITY_IMAGE_2;
	}
	
	@Override
	public void superSaveBean(SystemUser currentUser) {
		super.saveBean(currentUser);
	}

	public String getMetaTitle() {
		return metaTitle;
	}

	public void setMetaTitle(String metaTitle) {
		this.metaTitle = metaTitle;
	}
	
	public PlayMainGenre getPlayMainGenre() {
		return playMainGenre;
	}

	public void setPlayMainGenre(PlayMainGenre playMainGenre) {
		this.playMainGenre = playMainGenre;
	}

	public AmazonProductType getAmazonProductType() {
		return amazonProductType;
	}

	public void setAmazonProductType(AmazonProductType amazonProductType) {
		this.amazonProductType = amazonProductType;
	}

	public Brochure getBrochure() {
		return brochure;
	}

	public void setBrochure(Brochure brochure) {
		this.brochure = brochure;
	}

	private class ProductTypeFdoh extends SaveableFileDetailsOwnerHelper {
		public ProductTypeFdoh( ProductType productType ) {
			super( productType );
		}

		@Override
		public String getFileDetailsDirectory(String fileDetailsKey, boolean includeServerWorkPath) {
			if ( ProductTypeImageKey.SMALL_IMAGE.name().equals(fileDetailsKey) ||
					ProductTypeImageKey.MEDIUM_IMAGE.name().equals(fileDetailsKey) ||
						ProductTypeImageKey.PUBLICITY_IMAGE_1.name().equals(fileDetailsKey) ||
							ProductTypeImageKey.PUBLICITY_IMAGE_2.name().equals(fileDetailsKey)) {
				return EcommerceWorkingDirectory.PRODUCT_TYPE_IMAGE_DIR.getDirectoryPath(includeServerWorkPath);
			}
			return null;
		}

		@Override
		public void setFileDetails(FileDetails fileDetails, String fileDetailsKey, Object collectionKey) {
			if ( ProductTypeImageKey.SMALL_IMAGE.name().equals(fileDetailsKey) ) {
				setSmallPhotoDetails(fileDetails);
			} else if ( ProductTypeImageKey.MEDIUM_IMAGE.name().equals(fileDetailsKey) ) {
				setMediumPhotoDetails(fileDetails);
			} else if ( ProductTypeImageKey.PUBLICITY_IMAGE_1.name().equals(fileDetailsKey) ) {
				setPublicity1Details(fileDetails);
			} else if ( ProductTypeImageKey.PUBLICITY_IMAGE_2.name().equals(fileDetailsKey) ) {
				setPublicity2Details(fileDetails);
			}
		}

		@Override
		public FileDetails getFileDetails(String fileDetailsKey, Object collectionKey) {
			if ( ProductTypeImageKey.SMALL_IMAGE.name().equals(fileDetailsKey) ) {
				return getSmallPhotoDetails();
			} else if ( ProductTypeImageKey.MEDIUM_IMAGE.name().equals(fileDetailsKey) ) {
				return getMediumPhotoDetails();
			} else if ( ProductTypeImageKey.PUBLICITY_IMAGE_1.name().equals(fileDetailsKey) ) {
				return getPublicity1Details();
			} else if ( ProductTypeImageKey.PUBLICITY_IMAGE_2.name().equals(fileDetailsKey) ) {
				return getPublicity2Details();
			}
			return null;
		}
	}
	
}
