package com.aplos.ecommerce.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.aplos.common.annotations.DynamicMetaValues;
import com.aplos.common.annotations.persistence.Any;
import com.aplos.common.annotations.persistence.AnyMetaDef;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.FetchType;
import com.aplos.common.annotations.persistence.Inheritance;
import com.aplos.common.annotations.persistence.InheritanceType;
import com.aplos.common.annotations.persistence.JoinColumn;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.OneToMany;
import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosAbstractBean;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.ecommerce.interfaces.SerialNumberOwner;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public class SerialNumber extends AplosBean implements SerialNumberOwner {
	private static final long serialVersionUID = -4501454730242224440L;

	@ManyToOne(fetch=FetchType.LAZY)
	private RealizedProduct realizedProduct;
	private boolean isVoidStickerRequired;
	private Date buildDate;
	@Column(columnDefinition="LONGTEXT")
	private String buildNotes;
	private String oemSerialNumber;
	@Column(columnDefinition="LONGTEXT")
	private String technicalHistory;
	@Column(columnDefinition="LONGTEXT")
	private String serialNumberHistory;
	@Column(columnDefinition="LONGTEXT")
	private String comments;

	@ManyToOne(fetch=FetchType.LAZY)
	private Customer currentCustomer = null;
	@OneToMany(fetch=FetchType.LAZY)
	private List<CustomerHistory> customerHistory = new ArrayList<CustomerHistory>();
	
	@ManyToOne(fetch=FetchType.LAZY)
	private ProductVersion productVersion;

	@Any( metaColumn = @Column( name = "serialNumberOwner_type" ) )
    @AnyMetaDef( idType = "long", metaType = "string", metaValues = {
    		// meta values added at runtime
    })
    @JoinColumn(name="serialNumberOwner_id")
	@DynamicMetaValues
	private SerialNumberOwner serialNumberOwner;

	@Transient
	private boolean bookedIn;

	private static Integer maxSerialNumber;
	private boolean isAddedToWaste = false;
	private boolean isReassigned = false; //nease job (teletest) 3410
	
	public SerialNumber() {
	}
	
	public Customer getOriginalCustomer() {
		if (customerHistory != null && customerHistory.size() > 0) {
			return customerHistory.get(0).getCustomer();
		} else {
			return null;
		}
	}
	
	public List<CustomerHistory> getSortedCustomerHistory() {
		List<CustomerHistory> sortedCustomerHistoryList = getCustomerHistory(); 
		Collections.sort( sortedCustomerHistoryList, new CustomerHistoryComparator() );
		return sortedCustomerHistoryList;
	}

	// TSN41036
	public static void initMaxSerialNumber() {
		String maxNo = (String)ApplicationUtil.getFirstUniqueResult("SELECT max( serialNumber ) FROM " + AplosAbstractBean.getTableName(SerialNumber.class) );
		if (maxNo != null) {
			maxSerialNumber = Integer.valueOf( maxNo );
		}
		else {
			maxSerialNumber = 48356;
		}
	}

	public String isVoidStickerRequiredStr() {
		if( isVoidStickerRequired ) {
			return "yes";
		} else {
			return "no";
		}
	}

	public void addToSerialNumberHistory( String newNote ) {
		StringBuffer strBuf = new StringBuffer( getSerialNumberHistory() );
		strBuf.insert( 0, newNote + "\n\r" );
		setSerialNumberHistory( strBuf.toString() );

	}

	@SuppressWarnings("unchecked")
	public static List<SerialNumber> searchOnSerialNumber( String s ) {
		BeanDao aqlBeanDao = new BeanDao(SerialNumber.class);
		aqlBeanDao.addWhereCriteria("bean.id like '" + s + "%'");
		aqlBeanDao.addWhereCriteria("bean.realizedProduct != null");
		return aqlBeanDao.setIsReturningActiveBeans(true).getAll();
	}
	
	public void updateCurrentCustomer( Customer customer ) {
		if (getCurrentCustomer() != null && !getCurrentCustomer().equals(customer)) {
			CustomerHistory newCustomerHistory = new CustomerHistory(getCurrentCustomer());
			newCustomerHistory.saveDetails();
			getCustomerHistory().add(newCustomerHistory);
		}
		setCurrentCustomer( customer );
	}

	@Override
	public Transaction getAssociatedTransaction() {
		if( getSerialNumberOwner() != null ) {
			return getSerialNumberOwner().getAssociatedTransaction();
		} else {
			return null;
		}
	}
	
	public void updateStockQuantity() {
		if( getRealizedProduct() != null ) {
			getRealizedProduct().updateStockQuantity();
			getRealizedProduct().saveDetails();
		}
	}

	@Override
	public Long getAssociatedTransactionId() {
		if( getSerialNumberOwner() != null ) {
			return getSerialNumberOwner().getAssociatedTransactionId();
		} else {
			return null;
		}
	}

	public static Integer getMaxSerialNumber() {
		return maxSerialNumber;
	}

	public static void setMaxSerialNumber(Integer maxSerialNumber2) {
		maxSerialNumber = maxSerialNumber2;
	}

	public void setRealizedProduct(RealizedProduct realizedProduct) {
		this.realizedProduct = realizedProduct;
	}

	public RealizedProduct getRealizedProduct() {
		return realizedProduct;
	}
	
	public String getBuildNotes() {
		return buildNotes;
	}
	
	public void setBuildNotes(String buildNotes) {
		this.buildNotes = buildNotes;
	}
	
	public String getOemSerialNumber() {
		return oemSerialNumber;
	}
	
	public void setOemSerialNumber(String oemSerialNumber) {
		this.oemSerialNumber = oemSerialNumber;
	}
	
	public String getTechnicalHistory() {
		return technicalHistory;
	}
	
	public void setTechnicalHistory(String technicalHistory) {
		this.technicalHistory = technicalHistory;
	}
	
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public String getComments() {
		return comments;
	}
	
	public void setBuildDate(Date buildDate) {
		this.buildDate = buildDate;
	}
	
	public Date getBuildDate() {
		return buildDate;
	}
	
	public void setSerialNumberHistory(String serialNumberHistory) {
		this.serialNumberHistory = serialNumberHistory;
	}
	
	public String getSerialNumberHistory() {
		return serialNumberHistory;
	}
	
	public void setIsVoidStickerRequired(boolean isVoidStickerRequired) {
		this.isVoidStickerRequired = isVoidStickerRequired;
	}
	
	public boolean getIsVoidStickerRequired() {
		return isVoidStickerRequired;
	}
	
	public void setBookedIn(boolean bookedIn) {
		this.bookedIn = bookedIn;
	}
	
	public boolean isBookedIn() {
		return bookedIn;
	}

	public void setSerialNumberOwner(SerialNumberOwner serialNumberOwner) {
		this.serialNumberOwner = serialNumberOwner;
	}
	
	public SerialNumberOwner getSerialNumberOwner() {
		return serialNumberOwner;
	}
	
	public void setAddedToWaste(boolean isAddedToWaste) {
		this.isAddedToWaste = isAddedToWaste;
	}
	
	public boolean isAddedToWaste() {
		return isAddedToWaste;
	}

	public void setProductVersion(ProductVersion productVersion) {
		this.productVersion = productVersion;
	}

	public ProductVersion getProductVersion() {
		return productVersion;
	}

	public Customer getCurrentCustomer() {
		return currentCustomer;
	}

	public void setCurrentCustomer(Customer currentCustomer) {
		this.currentCustomer = currentCustomer;
	}

	public List<CustomerHistory> getCustomerHistory() {
		return customerHistory;
	}

	public void setCustomerHistory(List<CustomerHistory> customerHistory) {
		this.customerHistory = customerHistory;
	}

	public boolean isReassigned() {
		return isReassigned;
	}

	public void setReassigned(boolean isReassigned) {
		this.isReassigned = isReassigned;
	}
	
	private class CustomerHistoryComparator implements Comparator<CustomerHistory> {
		@Override
		public int compare(CustomerHistory o1, CustomerHistory o2) {
			return o2.getId().compareTo( o1.getId() );
		}
	}

}
