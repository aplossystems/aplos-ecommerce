package com.aplos.ecommerce.beans;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.FileDetailsOwnerHelper;
import com.aplos.common.LabeledEnumInter;
import com.aplos.common.SaveableFileDetailsOwnerHelper;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.annotations.RemoveEmpty;
import com.aplos.common.annotations.persistence.Cascade;
import com.aplos.common.annotations.persistence.CascadeType;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.FetchType;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.Address;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.beans.Subscriber;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.beans.communication.AplosEmail;
import com.aplos.common.beans.communication.BasicEmailFolder;
import com.aplos.common.enums.EmailActionType;
import com.aplos.common.interfaces.BulkEmailFinder;
import com.aplos.common.interfaces.BulkEmailSource;
import com.aplos.common.interfaces.BulkSubscriberSource;
import com.aplos.common.interfaces.EmailFolder;
import com.aplos.common.interfaces.FileDetailsOwnerInter;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.ImageUtil;
import com.aplos.ecommerce.backingpage.realizedProductReturn.RealizedProductReturnMenuWizard;
import com.aplos.ecommerce.enums.EcommerceWorkingDirectory;

@Entity
@PluralDisplayName(name="returns")
@DynamicMetaValueKey(oldKey="RP_RETURN")
public class RealizedProductReturn extends AplosBean implements FileDetailsOwnerInter, BulkEmailFinder, BulkSubscriberSource, EmailFolder {
	private static final long serialVersionUID = -7237820602812707268L;

	public enum RealizedProductReturnStatus implements LabeledEnumInter {
		 FAULTY_UNIT_REPORTED("Faulty unit Reported", "blue" ),
		 UNIT_RECEIVED("Unit received", "red" ),
		 UNIT_NEVER_RETURNED("Unit never returned", "pink" ),
		 REPAIRED_UNIT("Repaired unit", "green"),
		 CALIBRATED_UNIT("Calibrated unit", "purple"),
		 UPGRADED_UNIT("Upgraded unit", "gray"),
		 REPLACED_UNIT("Replaced unit", "white"),
		 NOT_FAULTY("Not faulty", "yellow"),
		 MONEY_BACK_GUARANTEE("Money back guarantee", "orange"),
		 DISPATCHED ("Dispatched", "cyan"),
		 QUOTE_SENT ("Quote sent", "maroon" );

		 private String name;
		 private String colorName;

		 private RealizedProductReturnStatus(String name, String colorName) {
			 this.setName(name);
			 this.setColorName(colorName);
		 }

		 @Override
		 public String getLabel() {
			 return getName();
		 }

		public void setColorName(String colorName) {
			this.colorName = colorName;
		}

		public String getColorName() {
			return colorName;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	@ManyToOne(fetch=FetchType.LAZY)
	private RealizedProduct returnProductBeforeSerialNumberSet;

	@ManyToOne(fetch=FetchType.LAZY)
	private SerialNumber serialNumber;

	private RealizedProductReturnStatus realizedProductReturnStatus;

	@ManyToOne
	@RemoveEmpty
	@Cascade({CascadeType.ALL})
	private Address returnAddress;

	private String customerReference;

	@Column(columnDefinition="LONGTEXT")
	private String returnsAuthorisationNoteDetails = "";
	@Column(columnDefinition="LONGTEXT")
	private String returnsAuthorisationNoteAppendage;
	private static String getDefaultReturnsAuthorisationNoteAppendage = "Please note: A minimum £30 inspection and test charge applies for non-warranty or 'No Fault Found' items";
	@Column(columnDefinition="LONGTEXT")
	private String returnsNotes = "";
	@Column(columnDefinition="LONGTEXT")
	private String conditionReceivedReportDetails = "";
	@Column(columnDefinition="LONGTEXT")
	private String repairReportDetails = "";
	@Column(columnDefinition="LONGTEXT")
	private String calibrationCertificateComments = "";
	@Column(columnDefinition="LONGTEXT")
	private String dispatchDetails = "";
	
	@ManyToOne(fetch=FetchType.LAZY)
	private FileDetails photo1Details;
	@ManyToOne(fetch=FetchType.LAZY)
	private FileDetails photo2Details;
	@ManyToOne(fetch=FetchType.LAZY)
	private FileDetails photo3Details;
	@ManyToOne(fetch=FetchType.LAZY)
	private FileDetails photo4Details;

	@Column(columnDefinition="LONGTEXT")
	private String photo1Text;
	@Column(columnDefinition="LONGTEXT")
	private String photo2Text;
	@Column(columnDefinition="LONGTEXT")
	private String photo3Text;
	@Column(columnDefinition="LONGTEXT")
	private String photo4Text;

	@ManyToOne(fetch=FetchType.LAZY)
	private RealizedProductReturnMenuWizard menuWizard = new RealizedProductReturnMenuWizard();

	@Column(columnDefinition="LONGTEXT")
	private String calibratedSpecifications = "";
	
	@Transient
	private RealizedProductReturnFdoh realizedProductReturnFdoh = new RealizedProductReturnFdoh(this);

	public RealizedProductReturn() {}
	
	@Override
	public String getBulkMessageFinderName() {
		return "All realized product return customers";
	}
	
	@Override
	public String getEmailFolderSearchCriteria() {
		return "bean.returnAddress.contactFirstName LIKE :searchStr OR bean.returnAddress.surname LIKE :searchStr";
	}
	
	@Override
	public void aplosEmailAction(EmailActionType emailActionType, AplosEmail aplosEmail) {	
	}
	
	@Override
	public FileDetailsOwnerHelper getFileDetailsOwnerHelper() {
		return realizedProductReturnFdoh;
	}

	@Override
	public String getDisplayName() {
		if( determineEndCustomer() != null ) {
			if( determineEndCustomer() instanceof CompanyContact ) {
				return ((CompanyContact) determineEndCustomer()).getCompany().getCompanyName();
			} else {
				return determineEndCustomer().getFullName();
			}
		}

		return getClass().getSimpleName();
	}
	
	@Override
	public Long getMessageSourceId() {
		return getId();
	}

	public String getFirstLineOfNotes() {
		return CommonUtil.getFirstLine( getReturnsNotes() );
	}


	public boolean isEndCustomerACompanyContact() {
		if ( getSerialNumber().getCurrentCustomer() != null ) {
			if( getSerialNumber().getCurrentCustomer() instanceof CompanyContact ) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	@Override
	public String getJDynamiTeValue(String variableKey, AplosEmail aplosEmail) {
		return null;
	}

	public boolean isCustomerACompanyContact() {
		if (getSerialNumber().getCustomerHistory() != null && getSerialNumber().getCustomerHistory().size() > 0) {
			return getSerialNumber().getCustomerHistory().get(0).getCustomer() instanceof CompanyContact;
		} else {
			return false;
		}
	}

	public Customer determineEndCustomer() {
		if (getSerialNumber() != null) {
			if ( getSerialNumber().getCurrentCustomer() == null ) {
				if (getSerialNumber().getCustomerHistory() != null && getSerialNumber().getCustomerHistory().size() > 0) {
					return getSerialNumber().getCustomerHistory().get(0).getCustomer();
				}
			} else {
				return getSerialNumber().getCurrentCustomer();
			}
		}
		return null;
	}


	public List<AplosEmail> getSortedAplosEmailList() {
		return BasicEmailFolder.getEmailListFromFolder( this, true );
	}

	public String parseEmailSubject( String emailSubject ) throws IOException {
		JDynamiTe subjectDynamiTe = new JDynamiTe();
		subjectDynamiTe.setInput(new ByteArrayInputStream( emailSubject.getBytes() ));
		subjectDynamiTe.setVariable("TODAYS_DATE", FormatUtil.formatDate( new Date() ) );
		subjectDynamiTe.setVariable("CUSTOMER_NAME", CommonUtil.getStringOrEmpty( determineEndCustomer().getFullName() ) );

		if( determineEndCustomer() instanceof CompanyContact ) {
			CompanyContact companyContact = (CompanyContact) determineEndCustomer();
			subjectDynamiTe.setVariable("CUSTOMER_COMPANY_NAME", CommonUtil.getStringOrEmpty( companyContact.getCompany().getCompanyName() ) );
			subjectDynamiTe.setVariable("RETURNS_COMPANY_ID", CommonUtil.getStringOrEmpty( companyContact.getCompany().getId() ) );
		}
		subjectDynamiTe.setVariable("RETURNS_NUMBER", "RMA" + CommonUtil.getStringOrEmpty( getId() ) );
		subjectDynamiTe.parse();

		return subjectDynamiTe.toString();
	}

	public void setReturnAddress(Address returnAddress) {
		this.returnAddress = returnAddress;
	}

	public Address getReturnAddress() {
		return returnAddress;
	}

	public void setReturnAddressFromCachedOriginalCustomer() {
		Customer originalCustomer;
		if (getSerialNumber().getOriginalCustomer() != null) {
			originalCustomer = getSerialNumber().getOriginalCustomer();
		} else {
			originalCustomer = getSerialNumber().getCurrentCustomer();
		}
		updateReturnAddress( originalCustomer );
	}
	
	public void setReturnAddressFromEndCustomer() {
		updateReturnAddress(determineEndCustomer());
	}

	public void updateReturnAddress( Customer customer ) { 
		setReturnAddress(customer.determineShippingAddress().getCopy());
		getReturnAddress().setContactFirstName( customer.getSubscriber().getFirstName() );
		getReturnAddress().setContactSurname( customer.getSubscriber().getSurname() );
		getReturnAddress().setEmailAddress( customer.getSubscriber().getEmailAddress() );
		if( customer instanceof CompanyContact ) {
			getReturnAddress().setCompanyName( ((CompanyContact) customer).getCompany().getCompanyName() );
		}
	}

	public static List<RealizedProductReturn> getOptimisedReturns( Long serialNumber ) {
		BeanDao realizedProductReturnDao = new BeanDao( RealizedProductReturn.class );
		realizedProductReturnDao.setSelectCriteria( "bean.id" );
		realizedProductReturnDao.addWhereCriteria( "serialNumber.id = :serialNumber" );
		realizedProductReturnDao.setNamedParameter( "serialNumber", String.valueOf( serialNumber ) );
		return (List) realizedProductReturnDao.getAll();
	}

	public void setReturnsAuthorisationNoteDetails(
			String returnsAuthorisationNoteDetails) {
		this.returnsAuthorisationNoteDetails = returnsAuthorisationNoteDetails;
	}

	public String getReturnsAuthorisationNoteDetails() {
		return returnsAuthorisationNoteDetails;
	}

	public void setReturnsNotes(String returnsNotes) {
		this.returnsNotes = returnsNotes;
	}

	public String getReturnsNotes() {
		return returnsNotes;
	}

	public void setConditionReceivedReportDetails(
			String conditionReceivedReportDetails) {
		this.conditionReceivedReportDetails = conditionReceivedReportDetails;
	}

	public String getConditionReceivedReportDetails() {
		return conditionReceivedReportDetails;
	}

	public String getFullPhoto1Url(boolean contextPath) {
		return ImageUtil.getFullFileUrl( photo1Details, contextPath );
	}

	public String getFullPhoto2Url(boolean contextPath) {
		return ImageUtil.getFullFileUrl( photo2Details, contextPath );
	}

	public String getFullPhoto3Url(boolean contextPath) {
		return ImageUtil.getFullFileUrl( photo3Details, contextPath );
	}

	public String getFullPhoto4Url(boolean contextPath) {
		return ImageUtil.getFullFileUrl( photo4Details, contextPath );
	}

	public String getFullPhoto1Url() {
		return getFullPhoto1Url(true);
	}

	public String getFullPhoto2Url() {
		return getFullPhoto2Url(true);
	}

	public String getFullPhoto3Url() {
		return getFullPhoto3Url(true);
	}

	public String getFullPhoto4Url() {
		return getFullPhoto4Url(true);
	}

	@Override
	public void saveBean(SystemUser currentUser) {
		if( getMenuWizard() != null ) {
			getMenuWizard().saveDetails();
		}
		FileDetails.saveFileDetailsOwner(this, ReturnsImageKey.values(), currentUser);
	}
		
	public void updateSerialNumber( SerialNumber serialNumber ) {
		setSerialNumber(serialNumber);
		if( serialNumber.getRealizedProduct() != null ) {
			setCalibratedSpecifications( serialNumber.getRealizedProduct().getProductInfo().getProduct().getProductTypes().get( 0 ).getCalibrationDetails() );
		}
	}

	public void setPhoto1Text(String photo1Text) {
		this.photo1Text = photo1Text;
	}

	public String getPhoto1Text() {
		return photo1Text;
	}

	public void setPhoto2Text(String photo2Text) {
		this.photo2Text = photo2Text;
	}

	public String getPhoto2Text() {
		return photo2Text;
	}

	public void setPhoto3Text(String photo3Text) {
		this.photo3Text = photo3Text;
	}

	public String getPhoto3Text() {
		return photo3Text;
	}

	public void setPhoto4Text(String photo4Text) {
		this.photo4Text = photo4Text;
	}

	public String getPhoto4Text() {
		return photo4Text;
	}

	public void setRepairReportDetails(String repairReportDetails) {
		this.repairReportDetails = repairReportDetails;
	}

	public String getRepairReportDetails() {
		return repairReportDetails;
	}

	public void setCalibrationCertificateComments(
			String calibrationCertificateComments) {
		this.calibrationCertificateComments = calibrationCertificateComments;
	}

	public String getCalibrationCertificateComments() {
		return calibrationCertificateComments;
	}

	public void setCalibratedSpecifications(String calibratedSpecifications) {
		this.calibratedSpecifications = calibratedSpecifications;
	}

	public String getCalibratedSpecifications() {
		return calibratedSpecifications;
	}

	public void setSerialNumber(
			SerialNumber serialNumber) {
		this.serialNumber = serialNumber;
	}

	public SerialNumber getSerialNumber() {
		return serialNumber;
	}

	public void setRealizedProductReturnStatus(
			RealizedProductReturnStatus realizedProductReturnStatus) {
		this.realizedProductReturnStatus = realizedProductReturnStatus;
	}

	public RealizedProductReturnStatus getRealizedProductReturnStatus() {
		return realizedProductReturnStatus;
	}

	public void setDispatchDetails(String dispatchDetails) {
		this.dispatchDetails = dispatchDetails;
	}

	public String getDispatchDetails() {
		return dispatchDetails;
	}

	public void setCustomerReference(String customerReference) {
		this.customerReference = customerReference;
	}

	public String getCustomerReference() {
		return customerReference;
	}

	public void setMenuWizard(RealizedProductReturnMenuWizard menuWizard) {
		this.menuWizard = menuWizard;
	}


	public RealizedProductReturnMenuWizard getMenuWizard() {
		return menuWizard;
	}

	public String getDetermineReturnsAuthorisationNoteAppendage() {
		return determineReturnsAuthorisationNoteAppendage();
	}


	public void setDetermineReturnsAuthorisationNoteAppendage( String returnsAuthorisationNoteAppendage ) {
		if( !returnsAuthorisationNoteAppendage.equals( getDefaultReturnsAuthorisationNoteAppendage ) ) {
			this.returnsAuthorisationNoteAppendage = returnsAuthorisationNoteAppendage;
		}
	}

	public RealizedProduct determineReturnProduct() {
		if( getSerialNumber() != null ) {
			return getSerialNumber().getRealizedProduct();
		} else {
			return getReturnProductBeforeSerialNumberSet();
		}
	}

	public String determineReturnsAuthorisationNoteAppendage() {
		if( returnsAuthorisationNoteAppendage == null ) {
			return getDefaultReturnsAuthorisationNoteAppendage;
		} else {
			return getReturnsAuthorisationNoteAppendage();
		}
	}

	public void setReturnsAuthorisationNoteAppendage(
			String returnsAuthorisationNoteAppendage) {
		this.returnsAuthorisationNoteAppendage = returnsAuthorisationNoteAppendage;
	}

	public String getReturnsAuthorisationNoteAppendage() {
		return returnsAuthorisationNoteAppendage;
	}

	@Override
	public Subscriber getSourceSubscriber() {
		if (getSerialNumber().getCurrentCustomer() != null) {
			return getSerialNumber().getCurrentCustomer().getSourceSubscriber();
		} else {
			return null;
		}
	}

	@Override
	public String getSourceUniqueDisplayName() {
		return "Return " + serialNumber + " - " + getSerialNumber().getCurrentCustomer().getDisplayName();
	}

	@Override
	public List<BulkEmailSource> getEmailAutoCompleteSuggestions(String searchString, Integer limit) {
		BeanDao realizedProductReturnDao = new BeanDao( RealizedProductReturn.class );
		realizedProductReturnDao.setIsReturningActiveBeans( true );
		List<BulkEmailSource> realizedProductReturns = null;
		if( searchString != null ) {
			realizedProductReturnDao.addWhereCriteria( "CONCAT(serialNumber.currentCustomer.subscriber.firstName,' ',serialNumber.currentCustomer.subscriber.surname) like :similarSearchText OR serialNumber like :similarSearchText" );
			if( limit != null ) {
				realizedProductReturnDao.setMaxResults(limit);
			}
			realizedProductReturnDao.setNamedParameter("similarSearchText", "%" + searchString + "%");
			realizedProductReturns = (List<BulkEmailSource>) realizedProductReturnDao.getAll();
		} else {
			realizedProductReturns = realizedProductReturnDao.getAll();
		}
		return realizedProductReturns;
	}

	//for list page, handlign the logic that the list bean used to
	public String getCustomerOrCompanyName() {
		
		if( !CommonUtil.getStringOrEmpty( getReturnAddress().getCompanyName() ).equals( "" ) ) {
			return getReturnAddress().getCompanyName();
		} else if( determineEndCustomer() != null && !determineEndCustomer().getDisplayName().equals("")) {
			return determineEndCustomer().getDisplayName();
		} else if( !CommonUtil.getStringOrEmpty( getReturnAddress().getContactFirstName() ).equals( "" ) ) {
			return getReturnAddress().getContactFullName();
		}
		return null;
		
	}

	public RealizedProduct getReturnProductBeforeSerialNumberSet() {
		return returnProductBeforeSerialNumberSet;
	}

	public void setReturnProductBeforeSerialNumberSet(
			RealizedProduct returnProductBeforeSerialNumberSet) {
		this.returnProductBeforeSerialNumberSet = returnProductBeforeSerialNumberSet;
	}
	
	@Override
	public String getFirstName() {
		if( serialNumber != null && serialNumber.getCurrentCustomer() != null ) {
			return serialNumber.getCurrentCustomer().getSubscriber().getFirstName();
		} else {
			return getReturnAddress().getContactFirstName();
		}
	}

	@Override
	public String getSurname() {
		if( serialNumber != null && serialNumber.getCurrentCustomer() != null ) {
			return serialNumber.getCurrentCustomer().getSubscriber().getSurname();
		} else {
			return getReturnAddress().getContactSurname();
		}
	}

	@Override
	public String getEmailAddress() {
		if( serialNumber != null && serialNumber.getCurrentCustomer() != null ) {
			return serialNumber.getCurrentCustomer().getSubscriber().getEmailAddress();
		} else {
			return getReturnAddress().getEmailAddress();	
		}
	}

	@Override
	public String getFinderSearchCriteria() {
		return "(CONCAT(bean.serialNumber.currentCustomer.subscriber.firstName,' ',bean.serialNumber.currentCustomer.subscriber.surname) LIKE :similarSearchText OR bean.serialNumber.currentCustomer.subscriber.emailAddress LIKE :similarSearchText)";
	}
	
	@Override
	public String getAlphabeticalSortByCriteria() {
		return "bean.serialNumber.currentCustomer.subscriber.firstName ASC, bean.serialNumber.currentCustomer.subscriber.surname ASC";
	}

	public FileDetails getPhoto1Details() {
		return photo1Details;
	}

	public void setPhoto1Details(FileDetails photo1Details) {
		this.photo1Details = photo1Details;
	}

	public FileDetails getPhoto2Details() {
		return photo2Details;
	}

	public void setPhoto2Details(FileDetails photo2Details) {
		this.photo2Details = photo2Details;
	}

	public FileDetails getPhoto3Details() {
		return photo3Details;
	}

	public void setPhoto3Details(FileDetails photo3Details) {
		this.photo3Details = photo3Details;
	}

	public FileDetails getPhoto4Details() {
		return photo4Details;
	}

	public void setPhoto4Details(FileDetails photo4Details) {
		this.photo4Details = photo4Details;
	}
	
	public enum ReturnsImageKey {
		PHOTO_ONE,
		PHOTO_TWO,
		PHOTO_THREE,
		PHOTO_FOUR;
	}
	
	@Override
	public void superSaveBean(SystemUser currentUser) {
		super.saveBean(currentUser);
	}
	
	private class RealizedProductReturnFdoh extends SaveableFileDetailsOwnerHelper {
		public RealizedProductReturnFdoh( RealizedProductReturn realizedProductReturn ) {
			super( realizedProductReturn );
		}

		@Override
		public String getFileDetailsDirectory(String fileDetailsKey, boolean includeServerWorkPath) {
			if (ReturnsImageKey.PHOTO_ONE.name().equals(fileDetailsKey) ||
					ReturnsImageKey.PHOTO_TWO.name().equals(fileDetailsKey) ||
						ReturnsImageKey.PHOTO_THREE.name().equals(fileDetailsKey) ||
							ReturnsImageKey.PHOTO_FOUR.name().equals(fileDetailsKey) ) {
				return EcommerceWorkingDirectory.REALIZED_PRODUCT_RETURN_PHOTO_IMAGE_DIR.getDirectoryPath(includeServerWorkPath);
			}
			return null;
		}

		@Override
		public void setFileDetails(FileDetails fileDetails, String fileDetailsKey, Object collectionKey) {
			if (ReturnsImageKey.PHOTO_ONE.name().equals(fileDetailsKey)) {
				setPhoto1Details(fileDetails);
			} else if (ReturnsImageKey.PHOTO_TWO.name().equals(fileDetailsKey)) {
				setPhoto2Details(fileDetails);
			} else if (ReturnsImageKey.PHOTO_THREE.name().equals(fileDetailsKey)) {
				setPhoto3Details(fileDetails);
			} else if (ReturnsImageKey.PHOTO_FOUR.name().equals(fileDetailsKey)) {
				setPhoto4Details(fileDetails);
			}
		}

		@Override
		public FileDetails getFileDetails(String fileDetailsKey, Object collectionKey) {
			if (ReturnsImageKey.PHOTO_ONE.name().equals(fileDetailsKey)) {
				return getPhoto1Details();
			} else if (ReturnsImageKey.PHOTO_TWO.name().equals(fileDetailsKey)) {
				return getPhoto2Details();
			} else if (ReturnsImageKey.PHOTO_THREE.name().equals(fileDetailsKey)) {
				return getPhoto3Details();
			} else if (ReturnsImageKey.PHOTO_FOUR.name().equals(fileDetailsKey)) {
				return getPhoto4Details();
			}
			return null;
		}
	}
	
}
