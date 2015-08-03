package com.aplos.ecommerce.beans.product;

import com.aplos.cms.beans.ContentBox.ContentBoxLayout;
import com.aplos.common.FileDetailsOwnerHelper;
import com.aplos.common.SaveableFileDetailsOwnerHelper;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.interfaces.FileDetailsOwnerInter;
import com.aplos.common.utils.CommonUtil;
import com.aplos.ecommerce.enums.EcommerceWorkingDirectory;

@Entity
@PluralDisplayName(name="product FAQs")
@DynamicMetaValueKey(oldKey="P_FAQ")
public class ProductFaq extends AplosBean implements FileDetailsOwnerInter {
	private static final long serialVersionUID = -398349558503495777L;
	private String title;
	private String category;
	@Column(columnDefinition="LONGTEXT")
	private String description;
	private ContentBoxLayout contentBoxLayout;
	private String imageCaption;

	@ManyToOne
	private FileDetails imageDetails;
	
	@Transient
	private ProductFaqFdoh productFaqFdoh = new ProductFaqFdoh(this);
	
	public ProductFaq() {
		this.contentBoxLayout = ContentBoxLayout.IMAGE_TO_THE_RIGHT;
	}


	@Override
	public void saveBean(SystemUser currentUser) {
		FileDetails.saveFileDetailsOwner(this, FaqImageKey.values(), currentUser);
	}
	
	@Override
	public FileDetailsOwnerHelper getFileDetailsOwnerHelper() {
		return productFaqFdoh;
	}

	@Override
	public String getDisplayName() {
		if( CommonUtil.getStringOrEmpty( getTitle() ).equals( "" ) ) {
			return super.getDisplayName();
		} else {
			return getTitle();
		}
	}

	public String getFullImageUrl() {
		return getFullImageUrl( true, false );
	}

	public String getFullImageUrl( boolean addContextRoot, boolean addRandom ) {
		if (getImageDetails() != null) {
			return getImageDetails().getFullFileUrl(true);
		} else {
			return "";
		}
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCategory() {
		return category;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setContentBoxLayout(ContentBoxLayout contentBoxLayout) {
		this.contentBoxLayout = contentBoxLayout;
	}

	public ContentBoxLayout getContentBoxLayout() {
		return contentBoxLayout;
	}

	public void setImageCaption(String imageCaption) {
		this.imageCaption = imageCaption;
	}

	public String getImageCaption() {
		return imageCaption;
	}

	public enum FaqImageKey {
		IMAGE;
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
	
	private class ProductFaqFdoh extends SaveableFileDetailsOwnerHelper {
		public ProductFaqFdoh( ProductFaq productFaq ) {
			super( productFaq );
		}

		@Override
		public String getFileDetailsDirectory(String fileDetailsKey, boolean includeServerWorkPath) {
			if (FaqImageKey.IMAGE.name().equals(fileDetailsKey)) {
				return EcommerceWorkingDirectory.PRODUCT_FAQ_IMAGE_DIR.getDirectoryPath(includeServerWorkPath);
			}
			return null;
		}

		@Override
		public void setFileDetails(FileDetails fileDetails, String fileDetailsKey, Object collectionKey) {
			if (FaqImageKey.IMAGE.name().equals(fileDetailsKey)) {
				setImageDetails(fileDetails);		
			}
		}

		@Override
		public FileDetails getFileDetails(String fileDetailsKey, Object collectionKey) {
			if( FaqImageKey.IMAGE.name().equals( fileDetailsKey ) ) {
				return getImageDetails();
			}
			return null;
		}
	}
}
