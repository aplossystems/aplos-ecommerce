package com.aplos.ecommerce.developermodulebacking.frontend;

import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.SortOrder;

import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.Address;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.beans.ShoppingCart;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.beans.Transaction.TransactionStatus;
import com.aplos.ecommerce.module.EcommerceConfiguration;
import com.aplos.ecommerce.utils.EcommerceUtil;

@ManagedBean
@ViewScoped
public class CustomerOrdersFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = 6874162219019083419L;
	private boolean isSingleOrderLoaded = false;
	private Transaction singleOrder = null;
	private AplosLazyDataModel customerOrderTblCom;

	public CustomerOrdersFeDmb() {
		setCustomerOrderTblCom(new CustomerOrderTblCom(CommonUtil.createDataTableState(this, getClass())));
	}

	@Override
	public boolean genericPageLoad( DeveloperCmsAtom developerCmsAtom ) {
		super.genericPageLoad(developerCmsAtom);
		Customer customer = JSFUtil.getBeanFromScope(Customer.class);
		EcommerceUtil.getEcommerceUtil().checkCustomerLogin(customer, "account information" );
		return true;
	}

	public boolean isShowingShippingRow() {
		Transaction transaction = getSingleOrder();
		if( transaction != null &&
				transaction.getEcommerceShoppingCart().getAvailableShippingService() != null &&
				transaction.getEcommerceShoppingCart().getAvailableShippingService().isUsingVolumetricWeight() ) {
			return false;
		} else {
			return true;
		}
	}

	public void viewOrder() {
		Transaction selectedTransaction = (Transaction)JSFUtil.getRequest().getAttribute("tableBean");
		singleOrder = new BeanDao( Transaction.class ).get( selectedTransaction.getId() );
		isSingleOrderLoaded = true;
	}

	public String getShipToString() {
		Transaction order = (Transaction) JSFUtil.getRequest().getAttribute("tableBean");
		if (order.getShippingAddress() != null) {
			if (order.getCustomer() != null) {
				String ordShipTo = order.getCustomer().determineShippingAddress().getContactFullName() + ", ";
				if (!ordShipTo.startsWith(", ") && !ordShipTo.equals(" , ")) {
					if (order.getCustomer().determineShippingAddress().getPostcode() != null && !order.getCustomer().determineShippingAddress().getPostcode().equals("")) {
						ordShipTo +=  order.getCustomer().determineShippingAddress().getPostcode();
					}
					return ordShipTo;
				} else if (order.getCustomer().determineShippingAddress().getPostcode() != null && !order.getCustomer().determineShippingAddress().getPostcode().equals("")) {
					return order.getCustomer().determineShippingAddress().getPostcode();
				}
			}
		}
		Customer customer = JSFUtil.getBeanFromScope(Customer.class);
		return customer.determineShippingAddress().getContactFullName() + ", " + customer.determineShippingAddress().getPostcode();
	}

	public Address getSingleOrderShippingAddress() {
		if (singleOrder != null) {
			if (singleOrder.getShippingAddress() != null) {
				if (singleOrder.getTransactionStatus().equals(TransactionStatus.INCOMPLETE)) {
					//if incomplete we may have an empty address
					if (singleOrder.getShippingAddress().getLine1() == null || singleOrder.getShippingAddress().getLine1().equals("")) {
						Customer customer = JSFUtil.getBeanFromScope(Customer.class);
						return customer.determineShippingAddress();
					}
				}
				return singleOrder.getShippingAddress();
			} else {
				Customer customer = JSFUtil.getBeanFromScope(Customer.class);
				return customer.determineShippingAddress();
			}
		}
		return null;
	}

	public Address getSingleOrderBillingAddress() {
		if (singleOrder != null) {
			if (singleOrder.getBillingAddress() != null) {
				if (singleOrder.getTransactionStatus().equals(TransactionStatus.INCOMPLETE)) {
					//if incomplete we may have an empty address
					if (singleOrder.getBillingAddress().getLine1() == null || singleOrder.getBillingAddress().getLine1().equals("")) {
						Customer customer = JSFUtil.getBeanFromScope(Customer.class);
						return customer.determineBillingAddress();
					}
				}
				return singleOrder.getBillingAddress();
			} else {
				Customer customer = JSFUtil.getBeanFromScope(Customer.class);
				return customer.determineBillingAddress();
			}
		}
		return null;
	}

	public void setSingleOrderLoaded(boolean isSingleOrderLoaded) {
		this.isSingleOrderLoaded = isSingleOrderLoaded;
	}

	public boolean isSingleOrderLoaded() {
		return isSingleOrderLoaded;
	}

	public void setSingleOrder(Transaction singleOrder) {
		this.singleOrder = singleOrder;
	}

	public Transaction getSingleOrder() {
		return singleOrder;
	}

	public String getShippingChargeString() {
		return FormatUtil.formatTwoDigit( singleOrder.getEcommerceShoppingCart().getDeliveryCost().doubleValue() );
	}

	public boolean isOrderIncomplete() {
		Transaction ord = (Transaction) JSFUtil.getRequest().getAttribute("tableBean");
		return (ord.getTransactionStatus() == TransactionStatus.INCOMPLETE && !isTheActiveOrder() );
	}

	public String getStatusLabel() {
		Transaction transaction = (Transaction) JSFUtil.getRequest().getAttribute("tableBean");
		if( transaction.getIsCancelled() ) {
			return "Cancelled";
		} else if( transaction.getIsDispatchedOrFurther() ) {
			return "Awaiting Authorisation > Authorised > <span class='highlightedStatus'>Dispatched</span>";
		} else if( transaction.getIsAcknowledgedOrFurther() ) {
			return "Awaiting Authorisation > <span class='highlightedStatus'>Authorised</span> > Dispatched";
		} else if( transaction.getTransactionStatus().equals( TransactionStatus.NEW ) ) {
			return "<span class='highlightedStatus'>Awaiting Authorisation</span> > Authorised > Dispatched";
		}
		return "Awaiting Authorisation > Authorised > Dispatched";
	}

	public boolean isTheActiveOrder() {
		Transaction ord = (Transaction) JSFUtil.getRequest().getAttribute("tableBean");
		if (ord == null || ord.getEcommerceShoppingCart() == null) {
			return false;
		} else {
			if (ord.equals(    JSFUtil.getBeanFromScope(ShoppingCart.class)   )) {
				return true;
			} else {
				return false;
			}
		}
	}

	public String loadOrder() {
		Transaction ord = (Transaction) JSFUtil.getRequest().getAttribute("tableBean");
		if (ord != null) {
			ord = ord.getSaveableBean();
			ord.getEcommerceShoppingCart().updateCachedValues(true);
			ord.addToScope();
			ord.getEcommerceShoppingCart().addToScope();
		}
		if( EcommerceConfiguration.getEcommerceCprsStatic().getCheckoutCartCpr() != null ) {
			EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutCartCpr();
		} else {
			EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutPaymentCpr();
		}
		return null;
	}

	public String deleteOrder() {
		Transaction order = (Transaction) JSFUtil.getRequest().getAttribute("tableBean");
		if (order != null) {
			if (!isTheActiveOrder()) {
				order = order.getSaveableBean();
				if (order.getEcommerceShoppingCart() != null) {
					order.getEcommerceShoppingCart().delete();
				}
				if (order.getBillingAddress() != null) {
					order.getBillingAddress().delete();
				}
				if (order.getShippingAddress() != null) {
					order.getShippingAddress().delete();
				}
				order.delete();
			} else {
				JSFUtil.addMessage("Sorry, you cannot delete the basket you are using, only older orders", FacesMessage.SEVERITY_INFO);
			}
		}
		return null;
	}
	
	public String getSingleOrderIdStr() {
		return String.valueOf( getSingleOrder().getId() );
	}

	public AplosLazyDataModel getCustomerOrderTblCom() {
		return customerOrderTblCom;
	}

	public void setCustomerOrderTblCom(AplosLazyDataModel customerOrderTblCom) {
		this.customerOrderTblCom = customerOrderTblCom;
	}
	
	public String getIncompleteText() {
		return "Incomplete";
	}

	protected class CustomerOrderTblCom extends AplosLazyDataModel {
		private static final long serialVersionUID = -5785396203797851842L;

		public CustomerOrderTblCom( DataTableState dataTableState ) {
			super( dataTableState, new BeanDao( Transaction.class ) );
			dataTableState.setShowingIdColumn( false );
			getBeanDao().setSelectCriteria( "bean.id, bean.dateCreated, bean.transactionStatus, bean.ecommerceShoppingCart, bean.active");
			getBeanDao().setOrderBy( "bean.dateCreated DESC" );
		}

		@Override
		public List<Object> load(int first, int pageSize, String sortField,
				SortOrder sortOrder, Map<String, String> filters) {
			getBeanDao().clearWhereCriteria();
			Customer customer = JSFUtil.getBeanFromScope( Customer.class );
			getBeanDao().addWhereCriteria( "bean.ecommerceShoppingCart.customer.id=" + customer.getId() );
			return super.load(first, pageSize, sortField, sortOrder, filters);
		}
	}

}
