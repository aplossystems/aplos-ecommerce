package com.aplos.ecommerce.backingpage.transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.beans.ShoppingCartItem;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.EcommerceShoppingCartItem;
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.beans.SerialNumber;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.beans.Transaction.TransactionStatus;
import com.aplos.ecommerce.module.EcommerceConfiguration;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=Transaction.class)
public class TransactionBookOutPage extends EditPage {
	private static final long serialVersionUID = -8845137311638666294L;

	private Map<EcommerceShoppingCartItem, List<SerialNumber>> scannedProducts;
	private String scannedSerialNumberStr;

	public TransactionBookOutPage() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		scannedProducts = new HashMap<EcommerceShoppingCartItem, List<SerialNumber>>();
		for (int i=0, n=transaction.getEcommerceShoppingCart().getItems().size() ; i<n ; i++) {
			scannedProducts.put((EcommerceShoppingCartItem)transaction.getEcommerceShoppingCart().getItems().get(i), new ArrayList<SerialNumber>());
		}
	}

	@Override
	public void registerScan() {
		super.registerScan();
		scannedSerialNumberStr = getScannedStr();
		serialNumberScanned();
	}

	public void serialNumberScanned() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		String tempScannedSerialNumberStr = scannedSerialNumberStr.replaceAll( "[a-zA-Z]", "" );
		BeanDao aqlBeanDao = new BeanDao(SerialNumber.class);
		aqlBeanDao.setWhereCriteria("bean.id = :scannedSerialNumber");
		long scannedSerialNumber = 0;
		try {
			scannedSerialNumber = Long.parseLong( tempScannedSerialNumberStr );
		} catch ( NumberFormatException nfex ) {
			JSFUtil.addMessage("The serial number needs to be an integer", FacesMessage.SEVERITY_ERROR);
		}
		aqlBeanDao.setIsReturningActiveBeans(true);
		aqlBeanDao.setNamedParameter( "scannedSerialNumber", String.valueOf( scannedSerialNumber ) );
		SerialNumber serialNumberAssignment = (SerialNumber) aqlBeanDao.getFirstBeanResult();

		if( serialNumberAssignment == null ) {
			JSFUtil.addMessage("This serial number cannot be found on the system", FacesMessage.SEVERITY_ERROR);
			return;
		}
		if( serialNumberAssignment.getRealizedProduct() == null ) {
			JSFUtil.addMessage("This serial number is not associated with a product", FacesMessage.SEVERITY_ERROR);
			return;
		}
		if( serialNumberAssignment.isAddedToWaste()) {
			JSFUtil.addMessage("This serial number has been added to waste", FacesMessage.SEVERITY_ERROR);
			return;
		}
		if (serialNumberAssignment.isReassigned()) {
			JSFUtil.addMessage("This serial number has been reassigned", FacesMessage.SEVERITY_ERROR);
			return;
		}

		EcommerceShoppingCartItem ecommerceShoppingCartItem = transaction.getEcommerceShoppingCart().getEcommerceShoppingCartItem( serialNumberAssignment.getRealizedProduct() );
		if ( ecommerceShoppingCartItem != null ) {
			if ( scannedProducts.get( ecommerceShoppingCartItem ).size() < ecommerceShoppingCartItem.getQuantity()) {
				if ( !isAlreadyScanned( scannedProducts.get( ecommerceShoppingCartItem ), serialNumberAssignment) ) {
					scannedProducts.get( ecommerceShoppingCartItem ).add( serialNumberAssignment );
					setScannedSerialNumberStr( "" );
				}
				else {
					JSFUtil.addMessage("This serial number has already been assigned to an order.", FacesMessage.SEVERITY_ERROR);
				}
			}
			else {
				JSFUtil.addMessage("The expected quantity has already been reached for this product.", FacesMessage.SEVERITY_ERROR);
			}
		}
		else {
			JSFUtil.addMessage("This product is not part of the current order.", FacesMessage.SEVERITY_ERROR);
		}
	}

	public boolean isAlreadyScanned( List<SerialNumber> currentSerialNumbers, SerialNumber serialNumber ) {
		for( int i = 0, n = currentSerialNumbers.size(); i < n; i++ ) {
			if( currentSerialNumbers.get( i ).getId().equals( serialNumber.getId() ) ) {
				return true;
			}
		}
		return false;
	}

	public void addNonSerialisedProduct() {
		EcommerceShoppingCartItem ecommerceShoppingCartItem = JSFUtil.getBeanFromRequest( "cartItem" );
		scannedProducts.get(ecommerceShoppingCartItem).add( null );
	}

	public void addAllNonSerialisedProduct() {
		EcommerceShoppingCartItem ecommerceShoppingCartItem = JSFUtil.getBeanFromRequest( "cartItem" );
		List<SerialNumber> assignmentList = scannedProducts.get( ecommerceShoppingCartItem );
		for( int i = assignmentList.size(), n = ecommerceShoppingCartItem.getQuantity(); i < n; i++ ) {
			scannedProducts.get( ecommerceShoppingCartItem ).add( null );
		}
	}

	public boolean getNonSerialBookOutAvailable() {
		EcommerceShoppingCartItem ecommerceShoppingCartItem = JSFUtil.getBeanFromRequest( "cartItem" );
		RealizedProduct realizedProduct = ecommerceShoppingCartItem.getRealizedProduct();
		if( realizedProduct != null && realizedProduct.isSerialNumberRequired()) {
			return false;
		} else {
			if( ecommerceShoppingCartItem.getQuantity() > scannedProducts.get( ecommerceShoppingCartItem ).size() ) {
				return true;
			} else {
				return false;
			}
		}
	}

	public String getScannedQuantityColour() {
		EcommerceShoppingCartItem requestEcommerceShoppingCartItem = (EcommerceShoppingCartItem) JSFUtil.getRequest().getAttribute("cartItem");
		Integer scannedProductQty = scannedProducts.get(requestEcommerceShoppingCartItem).size();
		if (scannedProductQty == 0) {
			return "red";
		} else if( scannedProductQty == requestEcommerceShoppingCartItem.getQuantity() ) {
			return "green";
		} else {
			return "orange";
		}
	}

	public String getBookOutBtnDisabled() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		for( ShoppingCartItem cartItem : transaction.getEcommerceShoppingCart().getItems() ) {
			if ( cartItem.getQuantity() > scannedProducts.get(cartItem).size() ) {
				return "true";
			}
		}
		return "false";
	}

	public String redirectToTransactionBookedOutPage() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);

		for( EcommerceShoppingCartItem cartItem : scannedProducts.keySet() ) {
			if( cartItem.getRealizedProduct() != null && cartItem.getRealizedProduct().isSerialNumberRequired() ) {
				for( SerialNumber serialNumber : scannedProducts.get( cartItem ) ) {
					serialNumber.addToSerialNumberHistory("Serial # " + serialNumber.getId() + " booked-out on Transaction #: " + transaction.getId() + " , " + new Date() );
					serialNumber.setSerialNumberOwner( cartItem );
					serialNumber.saveDetails();
					serialNumber.updateStockQuantity();
				}
			}
		}

		if( transaction.getInvoiceNumber() == null ) {
			transaction.setInvoiceNumber( EcommerceConfiguration.getEcommerceConfiguration().getNextMaxInvoiceNumber( transaction ) );
		}
		transaction.setTransactionStatus(TransactionStatus.AWAITING_DISPATCH);
		transaction.setDispatchedDate(new Date());
		transaction.saveDetails();
		JSFUtil.redirect(TransactionEditPage.class);
		return null;
	}

	public int getNumberOfScanned( EcommerceShoppingCartItem ecommerceShoppingCartItem ) {
		return scannedProducts.get(ecommerceShoppingCartItem).size();
	}

	public void setScannedSerialNumberStr(String scannedSerialNumber) {
		this.scannedSerialNumberStr = scannedSerialNumber;
	}

	public String getScannedSerialNumberStr() {
		return scannedSerialNumberStr;
	}

	public Map<EcommerceShoppingCartItem, List<SerialNumber>> getScannedProducts() {
		return scannedProducts;
	}

	public void setScannedProducts(Map<EcommerceShoppingCartItem, List<SerialNumber>> scannedProducts) {
		this.scannedProducts = scannedProducts;
	}

}
