package com.aplos.ecommerce.backingpage.serialNumbers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.BackingPage;
import com.aplos.common.beans.ShoppingCartItem;
import com.aplos.common.beans.Website;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.backingpage.transaction.TransactionEditPage;
import com.aplos.ecommerce.beans.CompanyContact;
import com.aplos.ecommerce.beans.EcommerceShoppingCartItem;
import com.aplos.ecommerce.beans.SerialNumber;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.beans.listbeans.SerialNumberListBean;
import com.aplos.ecommerce.interfaces.SerialNumberOwner;

@ManagedBean
@ViewScoped
public class BookInPage extends BackingPage {
	private static final long serialVersionUID = 1012932121718547547L;

	private SerialNumber selectedSerialNumber;
	private SerialNumber autoCompleteSerialNumber;

	private String scannedSerialNumber;

	private Transaction transaction = null;

	private Map<EcommerceShoppingCartItem, List<SerialNumberListBean>> serialNumberAssignmentListBeans;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<SerialNumber> suggestSerialNumbers( String searchStr ) {
		BeanDao serialNumberDao = new BeanDao( SerialNumber.class );
		serialNumberDao.setSelectCriteria( "bean.id, bean.active" );
		serialNumberDao.setWhereCriteria( "bean.id LIKE :similarText AND serialNumberOwner_type IS NOT NULL ");
		serialNumberDao.setNamedParameter( "similarText", searchStr + "%" );
		serialNumberDao.setMaxResults( 10 );

		return serialNumberDao.getAll();
	}

	public void checkSerialNumber( SelectEvent event ) {
		SerialNumber autoCompleteSerialNumber = (SerialNumber) event.getObject();
		BeanDao serialNumberAssignmentBeanDao = new BeanDao(SerialNumber.class);
		serialNumberAssignmentBeanDao.addWhereCriteria("bean.id = " + autoCompleteSerialNumber.getId());
		SerialNumber loadedSerialNumber = (SerialNumber) serialNumberAssignmentBeanDao.getFirstResult();
		if (loadedSerialNumber != null) {
			setSelectedSerialNumber(loadedSerialNumber);
			Transaction transaction = loadedSerialNumber.getAssociatedTransaction();
			if( transaction != null ) {
//				HibernateUtil.initialise(transaction, true);
				setTransaction( transaction );
			} else {
				JSFUtil.addMessage( "There is not a transaction associated with this serial number" );
			}
		}
		else {
			JSFUtil.addMessage("The serial number you have scanned is not recognized.", FacesMessage.SEVERITY_ERROR);
		}
	}
	
	public boolean isCustomerACompanyContact() {
		if( transaction.getCustomer() instanceof CompanyContact ) {
			return true;
		} else {
			return false;
		}
	}

	public void bookInSerialNumber(SerialNumber serialNumber) {
		SerialNumberOwner serialNumberOwner = serialNumber.getSerialNumberOwner();
		serialNumber.addToSerialNumberHistory( FormatUtil.formatDate( new Date() ) + " - Serial Number removed from transaction " + serialNumberOwner.getAssociatedTransactionId() );
		serialNumber.setSerialNumberOwner( null );
		serialNumber.saveDetails();
		serialNumber.updateStockQuantity();
		setSelectedSerialNumber(null);
		setTransaction(null);
		JSFUtil.addMessage( "This item has been booked back in" );
	}

	public void bookInSerialNumber() {
		SerialNumberListBean serialNumberAssignmentListBean = (SerialNumberListBean) JSFUtil.getRequest().getAttribute("sna");
		for (ShoppingCartItem shoppingCartItem : transaction.getEcommerceShoppingCart().getItems()) {
			for (SerialNumber serialNumberAssignment : ((EcommerceShoppingCartItem)shoppingCartItem).getSerialNumberAssignments()) {
				if (serialNumberAssignment.getId().equals(serialNumberAssignmentListBean.getId())) {
					serialNumberAssignment.setBookedIn(true);
					serialNumberAssignmentListBean.setBookedIn(true);
				}
			}
		}
	}

	public boolean isBookedOut(SerialNumber serialNumberAssignment) {
		return !serialNumberAssignment.isBookedIn();
	}

	public String redirectToSerialNumbers() {
		if (transaction != null) {
			for (ShoppingCartItem shoppingCartItem : transaction.getEcommerceShoppingCart().getItems()) {
				if( serialNumberAssignmentListBeans.get(shoppingCartItem) != null ) {
					for (SerialNumberListBean serialNumberAssignmentListBean : serialNumberAssignmentListBeans.get(shoppingCartItem)) {
						if (serialNumberAssignmentListBean.isBookedIn()) {
							for (SerialNumber serialNumberAssignment : ((EcommerceShoppingCartItem)shoppingCartItem).getSerialNumberAssignments()) {
								if (serialNumberAssignment.getId().equals(serialNumberAssignmentListBean.getId())) {
									((EcommerceShoppingCartItem)shoppingCartItem).getSerialNumberAssignments().remove(serialNumberAssignment);
									serialNumberAssignment.setSerialNumberHistory("Serial # " + serialNumberAssignment.getId() + " booked-in on " + new Date() + "\n\r" + serialNumberAssignment.getSerialNumberHistory());
									serialNumberAssignment.saveDetails();
									break;
								}
							}
						}
					}
				}
				shoppingCartItem.saveDetails();
			}
		}

		JSFUtil.redirect(Website.getCurrentWebsiteFromTabSession().getPackageName(), SerialNumberListPage.class);
		return null;
	}

	public void initSerialNumberAssignmentListBeans(EcommerceShoppingCartItem ecommerceShoppingCartItem) {
		serialNumberAssignmentListBeans = new HashMap<EcommerceShoppingCartItem, List<SerialNumberListBean>>();
		serialNumberAssignmentListBeans.put(ecommerceShoppingCartItem, new ArrayList<SerialNumberListBean>());
		for (int i=0, n=ecommerceShoppingCartItem.getQuantity() ; i<n ; i++) {
			if (i < ecommerceShoppingCartItem.getSerialNumberAssignments().size()) {
				serialNumberAssignmentListBeans.get(ecommerceShoppingCartItem).add(
						new SerialNumberListBean(
						ecommerceShoppingCartItem.getSerialNumberAssignments().get(i).getId(),
						ecommerceShoppingCartItem.getRealizedProduct().getProductInfo().getProduct().getName(),
						ecommerceShoppingCartItem.getRealizedProduct().determineItemCode(),
						true));
			}
			else {
				serialNumberAssignmentListBeans.get(ecommerceShoppingCartItem).add(
						new SerialNumberListBean(
						null,
						ecommerceShoppingCartItem.getRealizedProduct().getProductInfo().getProduct().getName(),
						ecommerceShoppingCartItem.getRealizedProduct().determineItemCode(),
						false));
			}
		}
	}

	public String redirectToTransactionSummaryPage() {
		transaction.addToScope();
		JSFUtil.redirect(Website.getCurrentWebsiteFromTabSession().getPackageName(), TransactionEditPage.class);
		return null;
	}

	public List<SerialNumberListBean> getSerialNumberAssignmentListBeans( EcommerceShoppingCartItem ecommerceShoppingCartItem ) {
		if( ecommerceShoppingCartItem != null ) {
			return serialNumberAssignmentListBeans.get(ecommerceShoppingCartItem);
		} else {
			return new ArrayList<SerialNumberListBean>( 0 );
		}
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public void setScannedSerialNumber(String scannedSerialNumber) {
		this.scannedSerialNumber = scannedSerialNumber;
	}

	public String getScannedSerialNumber() {
		return scannedSerialNumber;
	}

	public SerialNumber getSelectedSerialNumber() {
		return selectedSerialNumber;
	}

	public void setSelectedSerialNumber(SerialNumber selectedSerialNumber) {
		this.selectedSerialNumber = selectedSerialNumber;
	}

	public SerialNumber getAutoCompleteSerialNumber() {
		return autoCompleteSerialNumber;
	}

	public void setAutoCompleteSerialNumber(SerialNumber selectedSerialNumber) {
		this.autoCompleteSerialNumber = autoCompleteSerialNumber;
	}
}
