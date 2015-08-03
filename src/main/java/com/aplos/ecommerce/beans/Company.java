package com.aplos.ecommerce.beans;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.aplos.common.FileDetailsOwnerHelper;
import com.aplos.common.SaveableFileDetailsOwnerHelper;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.annotations.RemoveEmpty;
import com.aplos.common.annotations.persistence.Cache;
import com.aplos.common.annotations.persistence.Cascade;
import com.aplos.common.annotations.persistence.CascadeType;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.FetchType;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.OneToMany;
import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.beans.Address;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.interfaces.FileDetailsOwnerInter;
import com.aplos.common.interfaces.MailRecipient;
import com.aplos.common.utils.ImageUtil;
import com.aplos.ecommerce.enums.AddressStatus;
import com.aplos.ecommerce.enums.EcommerceWorkingDirectory;
import com.aplos.ecommerce.enums.FollowUpStatus;
import com.aplos.ecommerce.enums.PromotionType;

@Entity
@PluralDisplayName(name="companies")
@Cache
@DynamicMetaValueKey(oldKey={"COMPANY"})
public class Company extends AplosBean implements MailRecipient, FileDetailsOwnerInter {
	private static final long serialVersionUID = -4418850615745966637L;

	@ManyToOne(fetch=FetchType.LAZY)
	private CompanyType companyType;
	private AddressStatus addressStatus;
	private String companyName;

	private String websiteUrl;

	@ManyToOne
	@RemoveEmpty
	@Cascade({CascadeType.ALL})
	private Address shippingAddress;

	@ManyToOne
	@RemoveEmpty
	@Cascade({CascadeType.ALL})
	private Address altShippingAddress;

	@ManyToOne
	@RemoveEmpty
	@Cascade({CascadeType.ALL})
	private Address billingAddress;

	private FollowUpStatus followUpStatus;
	private PromotionType promotionType;
	@Column(columnDefinition="LONGTEXT")
    private String notes;
//	private String imageUrl1;
//	private String imageUrl2;
	@Column(columnDefinition="LONGTEXT")
    private String description;
//	private String photo;
//	private String logo;
	private BigDecimal discountPercentage;

	/** @deprecated **/
	@Deprecated
	@Column(columnDefinition="LONGTEXT")
    private String companyHistory;

	@OneToMany( mappedBy = "company", fetch=FetchType.LAZY )
	@Cache
	private List<CompanyContact> companyContacts;
	private Boolean isShowingOnWebsite;
	private boolean isCreditAllowed = false;
	private BigDecimal creditLimit = new BigDecimal( 0 );
	private boolean isSoleTrader;
	private boolean isCompanyTypeDiscountAllowed = false;
	private String vatNumber;
	private String registrationNumber;
	private String courierReference;
	private DuplicateAddresses duplicateAddresses = DuplicateAddresses.SHIPPING_ADDRESS_FOR_BILLING;
	private boolean isUsingAlternativeAddress = false;

	@ManyToOne(fetch=FetchType.LAZY)
	private FileDetails image1Details;
	@ManyToOne(fetch=FetchType.LAZY)
	private FileDetails image2Details;
	@ManyToOne(fetch=FetchType.LAZY)
	private FileDetails logoDetails;
	@ManyToOne(fetch=FetchType.LAZY)
	private FileDetails photoDetails;
	
	@Transient
	private CompanyFdoh companyFdoh = new CompanyFdoh(this);
	
	public enum DuplicateAddresses {
		BILLING_ADDRESS_FOR_SHIPPING,
		SHIPPING_ADDRESS_FOR_BILLING,
		NONE;
	}
	
	public Company() {
		 companyContacts = new ArrayList<CompanyContact>();
	}
	
	@Override
	public FileDetailsOwnerHelper getFileDetailsOwnerHelper() {
		return companyFdoh;
	}

	@Override
	public String getDisplayName() {
		return companyName;
	}

	public Address determineShippingAddress() {
		if( getDuplicateAddresses().equals( DuplicateAddresses.BILLING_ADDRESS_FOR_SHIPPING ) ) {
			return determineBillingAddress();
		} else {
			return shippingAddress;
		}
	}

	public Address determineBillingAddress() {
		if( getDuplicateAddresses().equals( DuplicateAddresses.SHIPPING_ADDRESS_FOR_BILLING ) ) {
			return determineShippingAddress();
		} else {
			return billingAddress;
		}
	}

	public void setCompanyContacts(List<CompanyContact> companyContacts) {
		this.companyContacts = companyContacts;
	}

	public List<CompanyContact> getCompanyContacts() {
		return companyContacts;
	}

	public CompanyType getCompanyType() {
		return companyType;
	}

	public void setCompanyType(CompanyType companyType) {
		this.companyType = companyType;
	}

	public AddressStatus getAddressStatus() {
		return addressStatus;
	}

	public void setAddressStatus(AddressStatus addressStatus) {
		this.addressStatus = addressStatus;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getWebsiteUrl() {
		return websiteUrl;
	}

	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}

	@Override
	public Address getMailRecipientAddress() {
		return determineShippingAddress();
	}

	public FollowUpStatus getFollowUpStatus() {
		return followUpStatus;
	}

	public void setFollowUpStatus(FollowUpStatus followUpStatus) {
		this.followUpStatus = followUpStatus;
	}

	public PromotionType getPromotionType() {
		return promotionType;
	}

	public void setPromotionType(PromotionType promotionType) {
		this.promotionType = promotionType;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public void addNote(String newNote) {
		if (notes == null) {
			notes = "";
		}
		notes = newNote + "\n\n" + notes;
	}

	public String getFullImageUrl1() {
		return getFullImageUrl1(true);
	}

	public String getFullImageUrl2() {
		return getFullImageUrl2(true);
	}

	public String getFullPhotoUrl() {
		return getFullPhotoUrl(true);
	}

	public String getFullLogoUrl() {
		return getFullLogoUrl(true);
	}

	public String getFullImageUrl1(boolean addContextPath) {
		return ImageUtil.getFullFileUrl( getImage1Details(), addContextPath );
	}

	public String getFullImageUrl2(boolean addContextPath) {
		return ImageUtil.getFullFileUrl( getImage2Details(), addContextPath );
	}

	public String getFullPhotoUrl(boolean addContextPath) {
		return ImageUtil.getFullFileUrl( getPhotoDetails(), addContextPath );
	}

	public String getFullLogoUrl(boolean addContextPath) {
		return ImageUtil.getFullFileUrl( getLogoDetails(), addContextPath );
	}

	@Override
	public void saveBean(SystemUser currentUser) {
		FileDetails.saveFileDetailsOwner(this, CompanyImageKey.values(), currentUser);
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setIsShowingOnWebsite(Boolean isShowingOnWebsite) {
		this.isShowingOnWebsite = isShowingOnWebsite;
	}

	public Boolean getIsShowingOnWebsite() {
		if( isShowingOnWebsite == null ) {
			return false;
		} else {
			return isShowingOnWebsite;
		}
	}

	public void setCreditLimit(BigDecimal creditLimit) {
		this.creditLimit = creditLimit;
	}

	public BigDecimal getCreditLimit() {
		return creditLimit;
	}

	public void setDiscountPercentage(BigDecimal discountPercentage) {
		this.discountPercentage = discountPercentage;
	}

	public BigDecimal getDiscountPercentage() {
		return discountPercentage;
	}

	public BigDecimal determineDiscountPercentage() {
		if( discountPercentage == null ) {
			return new BigDecimal( 0 );
		} else {
			return discountPercentage;
		}
	}

	public void setSoleTrader(boolean isSoleTrader) {
		this.isSoleTrader = isSoleTrader;
	}

	public boolean isSoleTrader() {
		return isSoleTrader;
	}

	public void setCreditAllowed(boolean isCreditAllowed) {
		this.isCreditAllowed = isCreditAllowed;
	}

	public boolean isCreditAllowed() {
		return isCreditAllowed;
	}

	public void setBillingAddress(Address billingAddress) {
		this.billingAddress = billingAddress;
	}

	public Address getBillingAddress() {
		return billingAddress;
	}

	public void setShippingAddress(Address shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public Address getShippingAddress() {
		return shippingAddress;
	}

	public void setVatNumber(String vatNumber) {
		this.vatNumber = vatNumber;
	}

	public String getVatNumber() {
		return vatNumber;
	}

	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}

	public String getRegistrationNumber() {
		return registrationNumber;
	}

	public void setCourierReference(String courierReference) {
		this.courierReference = courierReference;
	}

	public String getCourierReference() {
		return courierReference;
	}


	/**
	 * @deprecated
	 * @param companyHistory
	 * This was part of the old teletest system but is not required in the new
	 * system.
	 */
	@Deprecated
	public void setCompanyHistory(String companyHistory) {
		this.companyHistory = companyHistory;
	}


	/**
	 * @deprecated
	 * @param companyHistory
	 * This was part of the old teletest system but is not required in the new
	 * system.
	 */
	@Deprecated
	public String getCompanyHistory() {
		return companyHistory;
	}

	public void setCompanyTypeDiscountAllowed(boolean isCompanyTypeDiscountAllowed) {
		this.isCompanyTypeDiscountAllowed = isCompanyTypeDiscountAllowed;
	}

	public boolean isCompanyTypeDiscountAllowed() {
		return isCompanyTypeDiscountAllowed;
	}

	public BigDecimal determineCompanyTypeDiscount() {
		if( isCompanyTypeDiscountAllowed() && getCompanyType() != null ) {
			return getCompanyType().getDiscountPercentage();
		} else {
			return new BigDecimal( 0 );
		}
	}

	public void setDuplicateAddresses(DuplicateAddresses duplicateAddresses) {
		this.duplicateAddresses = duplicateAddresses;
	}

	public DuplicateAddresses getDuplicateAddresses() {
		return duplicateAddresses;
	}

	public void setAltShippingAddress(Address altShippingAddress) {
		this.altShippingAddress = altShippingAddress;
	}

	public Address getAltShippingAddress() {
		return altShippingAddress;
	}

	public void setUsingAlternativeAddress(boolean isUsingAlternativeAddress) {
		this.isUsingAlternativeAddress = isUsingAlternativeAddress;
	}

	public boolean isUsingAlternativeAddress() {
		return isUsingAlternativeAddress;
	}

	public enum CompanyImageKey {
		IMAGE_ONE,
		IMAGE_TWO,
		LOGO,
		PHOTO;
	}
	
	@Override
	public void superSaveBean(SystemUser currentUser) {
		super.saveBean(currentUser);
	}

	public FileDetails getImage1Details() {
		return image1Details;
	}

	public void setImage1Details(FileDetails image1Details) {
		this.image1Details = image1Details;
	}

	public FileDetails getImage2Details() {
		return image2Details;
	}

	public void setImage2Details(FileDetails image2Details) {
		this.image2Details = image2Details;
	}

	public FileDetails getLogoDetails() {
		return logoDetails;
	}

	public void setLogoDetails(FileDetails logoDetails) {
		this.logoDetails = logoDetails;
	}

	public FileDetails getPhotoDetails() {
		return photoDetails;
	}

	public void setPhotoDetails(FileDetails photoDetails) {
		this.photoDetails = photoDetails;
	}
	
	private class CompanyFdoh extends SaveableFileDetailsOwnerHelper {
		public CompanyFdoh( Company company ) {
			super( company );
		}

		@Override
		public String getFileDetailsDirectory(String fileDetailsKey, boolean includeServerWorkPath) {
			if (CompanyImageKey.IMAGE_ONE.name().equals(fileDetailsKey)) {
				return EcommerceWorkingDirectory.COMPANY_PHOTO1_IMAGE_DIR.getDirectoryPath(includeServerWorkPath);
			} else if (CompanyImageKey.IMAGE_TWO.name().equals(fileDetailsKey)) {
				return EcommerceWorkingDirectory.COMPANY_PHOTO2_IMAGE_DIR.getDirectoryPath(includeServerWorkPath);
			} else if (CompanyImageKey.LOGO.name().equals(fileDetailsKey)) {
				return EcommerceWorkingDirectory.COMPANY_LOGO_IMAGE_DIR.getDirectoryPath(includeServerWorkPath);
			} else if (CompanyImageKey.PHOTO.name().equals(fileDetailsKey)) {
				return EcommerceWorkingDirectory.COMPANY_PHOTO_IMAGE_DIR.getDirectoryPath(includeServerWorkPath);
			}
			return null;
		}

		@Override
		public void setFileDetails(FileDetails fileDetails, String fileDetailsKey, Object collectionKey) {
			if (CompanyImageKey.IMAGE_ONE.name().equals(fileDetailsKey)) {
				setImage1Details(fileDetails);
			} else if (CompanyImageKey.IMAGE_TWO.name().equals(fileDetailsKey)) {
				setImage2Details(fileDetails);
			} else if (CompanyImageKey.LOGO.name().equals(fileDetailsKey)) {
				setLogoDetails(fileDetails);
			} else if (CompanyImageKey.PHOTO.name().equals(fileDetailsKey)) {
				setPhotoDetails(fileDetails);
			}
		}

		@Override
		public FileDetails getFileDetails(String fileDetailsKey, Object collectionKey) {
			if (CompanyImageKey.IMAGE_ONE.name().equals(fileDetailsKey)) {
				return getImage1Details();
			} else if (CompanyImageKey.IMAGE_TWO.name().equals(fileDetailsKey)) {
				return getImage2Details();
			} else if (CompanyImageKey.LOGO.name().equals(fileDetailsKey)) {
				return getLogoDetails();
			} else if (CompanyImageKey.PHOTO.name().equals(fileDetailsKey)) {
				return getPhotoDetails();
			}
			return null;
		}
	}
	
}
