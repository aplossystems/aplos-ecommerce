package com.aplos.ecommerce.beans;

import com.aplos.cms.enums.CmsWorkingDirectory;
import com.aplos.common.FileDetailsOwnerHelper;
import com.aplos.common.SaveableFileDetailsOwnerHelper;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.interfaces.FileDetailsOwnerInter;
import com.aplos.common.interfaces.PositionedBean;
import com.aplos.common.servlets.MediaServlet;
import com.aplos.common.servlets.MediaServlet.MediaFileType;
import com.aplos.common.utils.ImageUtil;

@Entity
@DynamicMetaValueKey(oldKey="BROCHURE")
public class Brochure extends AplosBean implements PositionedBean, FileDetailsOwnerInter {
	private static final long serialVersionUID = -8211443554073771762L;

	private String brochureTitle = "New brochure";
	@Column(columnDefinition="LONGTEXT")
	private String brochureDescription;
	//private String imageUrl;
//	private String pdfUrl;
	private Integer positionIdx;
	
	@ManyToOne
	private FileDetails imageDetails;
	@ManyToOne
	private FileDetails pdfDetails;
	
	@Transient
	private BrochureFdoh brochureFdoh = new BrochureFdoh(this);

	@Override
	public void saveBean(SystemUser currentUser) {
		FileDetails.saveFileDetailsOwner(this, BrochureImageKey.values(), currentUser);
	}
	
	@Override
	public FileDetailsOwnerHelper getFileDetailsOwnerHelper() {
		return brochureFdoh;
	}

	public String getFullImageUrl( boolean addContextRoot ) {
		return ImageUtil.getFullFileUrl( getImageDetails(), addContextRoot );
	}

	public String getFullPdfUrl( boolean addContextRoot ) {
		return MediaServlet.getFileUrl( getPdfDetails(), CmsWorkingDirectory.PDF_FILE_DIR.getDirectoryPath(true), addContextRoot, MediaFileType.PDF );
	}

	@Override
	public String getDisplayName() {
		if( brochureTitle == null ) {
			return "New brochure";
		} else {
			return brochureTitle;
		}
	}

	@Override
	public void setPositionIdx(Integer positionIdx) {
		this.positionIdx = positionIdx;
	}

	@Override
	public Integer getPositionIdx() {
		return positionIdx;
	}

	public void setBrochureTitle(String brochureTitle) {
		this.brochureTitle = brochureTitle;
	}

	public String getBrochureTitle() {
		return brochureTitle;
	}

	public void setBrochureDescription(String brochureDescription) {
		this.brochureDescription = brochureDescription;
	}

	public String getBrochureDescription() {
		return brochureDescription;
	}

	public FileDetails getImageDetails() {
		return imageDetails;
	}

	public void setImageDetails(FileDetails imageDetails) {
		this.imageDetails = imageDetails;
	}

//#################
	
	public enum BrochureImageKey {
		IMAGE,
		PDF;
	}
	
	@Override
	public void superSaveBean(SystemUser currentUser) {
		super.saveBean(currentUser);
	}

	public FileDetails getPdfDetails() {
		return pdfDetails;
	}

	public void setPdfDetails(FileDetails pdfDetails) {
		this.pdfDetails = pdfDetails;
	}
	
	private class BrochureFdoh extends SaveableFileDetailsOwnerHelper {
		public BrochureFdoh( Brochure brochure ) {
			super( brochure );
		}

		@Override
		public String getFileDetailsDirectory(String fileDetailsKey, boolean includeServerWorkPath) {
			if (BrochureImageKey.IMAGE.name().equals(fileDetailsKey)) {
				return CmsWorkingDirectory.BROCHURE_IMAGE_DIR.getDirectoryPath(includeServerWorkPath);
			} else if (BrochureImageKey.PDF.name().equals(fileDetailsKey)) {
				return CmsWorkingDirectory.PDF_FILE_DIR.getDirectoryPath(includeServerWorkPath);
			}
			return null;
		}

		@Override
		public void setFileDetails(FileDetails fileDetails, String fileDetailsKey, Object collectionKey) {
			if (BrochureImageKey.IMAGE.name().equals(fileDetailsKey)) {
				setImageDetails(fileDetails);		
			} else if (BrochureImageKey.PDF.name().equals(fileDetailsKey)) {
				setPdfDetails(fileDetails);
			}
		}

		@Override
		public FileDetails getFileDetails(String fileDetailsKey, Object collectionKey) {
			if ( BrochureImageKey.IMAGE.name().equals( fileDetailsKey ) ) {
				return getImageDetails();
			} else if ( BrochureImageKey.PDF.name().equals( fileDetailsKey ) ) {
				return getPdfDetails();
			}
			return null;
		}
	}
	
}
