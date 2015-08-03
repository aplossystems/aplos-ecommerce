package com.aplos.ecommerce.beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.cms.beans.HostedVideo;
import com.aplos.cms.enums.CmsWorkingDirectory;
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
import com.aplos.ecommerce.enums.EcommerceWorkingDirectory;

@Entity
@ManagedBean
@SessionScoped
@PluralDisplayName(name="news entries")
@DynamicMetaValueKey(oldKey="NEWS_ENTRY")
public class NewsEntry extends AplosBean implements FileDetailsOwnerInter {

	private static final long serialVersionUID = -8224994853250576937L;

	private String title;
	@Column(columnDefinition="LONGTEXT")
	private String content;
	@Column(columnDefinition="LONGTEXT")
	private String contentMid;
	@Column(columnDefinition="LONGTEXT")
	private String contentBottom;
	
	@ManyToOne
	private FileDetails initialImageDetails;
	@ManyToOne
	private FileDetails middleImageDetails;
	@ManyToOne
	private FileDetails footerImageDetails;
	@ManyToOne
	private FileDetails pdfDetails;

	private Boolean showsInNewsRotator=true;
	private Boolean showsInNewsPage=true;

	@ManyToOne
	private RealizedProduct product;

	@ManyToOne
	private HostedVideo video;

	//These fields relate to the info pack
//	private String pdf;
	private String pdfTitle = "Info Pack";
	private String emailSubject = "News Info Pack";
	@Column(columnDefinition="LONGTEXT")
	private String emailBody = "Attached is the Information Pack you requested.";
	
	@Transient
	private NewsEntryFdoh newsEntryFdoh = new NewsEntryFdoh(this);
	
	public enum NewsImageKey {
		INITIAL,
		MIDDLE,
		FOOTER,
		PDF;
	}

	@Override
	public String getDisplayName() {
		return getTitle();
	}
	
	@Override
	public FileDetailsOwnerHelper getFileDetailsOwnerHelper() {
		return newsEntryFdoh;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}
	@Override
	public void saveBean( SystemUser currentUser ) {
		FileDetails.saveFileDetailsOwner(this, NewsImageKey.values(), currentUser);
		if( getPdfDetails() != null ) {
			getPdfDetails().setName( getPdfTitle() );
			getPdfDetails().saveDetails();
		}
	}

	public void setContentMid(String contentMid) {
		this.contentMid = contentMid;
	}

	public String getContentMid() {
		return contentMid;
	}

	public void setContentBottom(String contentBottom) {
		this.contentBottom = contentBottom;
	}

	public String getContentBottom() {
		return contentBottom;
	}

	public void setProduct(RealizedProduct product) {
		this.product = product;
	}

	public RealizedProduct getProduct() {
		return product;
	}

	public void setVideo(HostedVideo video) {
		this.video = video;
	}

	public HostedVideo getVideo() {
		return video;
	}

	public void setShowsInNewsRotator(Boolean showsInNewsRotator) {
		this.showsInNewsRotator = showsInNewsRotator;
	}

	public Boolean getShowsInNewsRotator() {
		return showsInNewsRotator;
	}

	public void setShowsInNewsPage(Boolean showsInNewsPage) {
		this.showsInNewsPage = showsInNewsPage;
	}

	public Boolean getShowsInNewsPage() {
		return showsInNewsPage;
	}

	public void setPdfTitle(String pdfTitle) {
		this.pdfTitle = pdfTitle;
	}

	public String getPdfTitle() {
		return pdfTitle;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public String getEmailSubject() {
		return emailSubject;
	}

	public void setEmailBody(String emailBody) {
		this.emailBody = emailBody;
	}

	public String getEmailBody() {
		return emailBody;
	}

	public FileDetails getInitialImageDetails() {
		return initialImageDetails;
	}

	public void setInitialImageDetails(FileDetails initialImageDetails) {
		this.initialImageDetails = initialImageDetails;
	}

	public FileDetails getMiddleImageDetails() {
		return middleImageDetails;
	}

	public void setMiddleImageDetails(FileDetails middleImageDetails) {
		this.middleImageDetails = middleImageDetails;
	}

	public FileDetails getFooterImageDetails() {
		return footerImageDetails;
	}

	public void setFooterImageDetails(FileDetails footerImageDetails) {
		this.footerImageDetails = footerImageDetails;
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
	
	private class NewsEntryFdoh extends SaveableFileDetailsOwnerHelper {
		public NewsEntryFdoh( NewsEntry newsEntry ) {
			super( newsEntry );
		}

		@Override
		public String getFileDetailsDirectory(String fileDetailsKey, boolean includeServerWorkPath) {
			if (NewsImageKey.INITIAL.name().equals(fileDetailsKey) ||
					NewsImageKey.MIDDLE.name().equals(fileDetailsKey) ||
						NewsImageKey.FOOTER.name().equals(fileDetailsKey)) {
				return EcommerceWorkingDirectory.NEWS_ENTRY_IMAGE_DIR.getDirectoryPath(includeServerWorkPath);
			} else if (NewsImageKey.PDF.name().equals(fileDetailsKey)) {
				return CmsWorkingDirectory.PDF_FILE_DIR.getDirectoryPath(includeServerWorkPath);
			}
			return null;
		}

		@Override
		public void setFileDetails(FileDetails fileDetails, String fileDetailsKey, Object collectionKey) {
			if (NewsImageKey.INITIAL.name().equals(fileDetailsKey)) {
				setInitialImageDetails(fileDetails);
			} else if (NewsImageKey.MIDDLE.name().equals(fileDetailsKey)) {
				setMiddleImageDetails(fileDetails);
			} else if (NewsImageKey.FOOTER.name().equals(fileDetailsKey)) {
				setFooterImageDetails(fileDetails);
			} else if (NewsImageKey.PDF.name().equals(fileDetailsKey)) {
				setPdfDetails(fileDetails);
			}
		}

		@Override
		public FileDetails getFileDetails(String fileDetailsKey, Object collectionKey) {
			if (NewsImageKey.INITIAL.name().equals(fileDetailsKey)) {
				return getInitialImageDetails();
			} else if (NewsImageKey.MIDDLE.name().equals(fileDetailsKey)) {
				return getMiddleImageDetails();
			} else if (NewsImageKey.FOOTER.name().equals(fileDetailsKey)) {
				return getFooterImageDetails();
			} else if (NewsImageKey.PDF.name().equals(fileDetailsKey)) {
				return getPdfDetails();
			}
			return null;
		}
	}

}
