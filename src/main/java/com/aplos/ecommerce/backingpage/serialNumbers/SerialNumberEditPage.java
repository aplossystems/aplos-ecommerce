package com.aplos.ecommerce.backingpage.serialNumbers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.primefaces.event.SelectEvent;

import com.aplos.common.AplosUrl;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.Website;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.backingpage.product.RealizedProductEditPage;
import com.aplos.ecommerce.backingpage.realizedProductReturn.RealizedProductReturnEditPage;
import com.aplos.ecommerce.backingpage.transaction.TransactionEditPage;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.ProductVersion;
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.beans.RealizedProductReturn;
import com.aplos.ecommerce.beans.SerialNumber;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.beans.listbeans.RealizedProductListBean;
import com.aplos.ecommerce.templates.printtemplates.PackagingLabelTemplate;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=SerialNumber.class)
public class SerialNumberEditPage extends EditPage {

	private static final long serialVersionUID = -1901301699321866239L;
	private RealizedProductListBean selectedRealizedProductListBean;
	private boolean isShowingItemCodeSearch = false;
	private boolean isShowingCustomerSearch = false;
	private Customer selectedCurrentCustomer;

	public void redirectToTransactionEdit() {
		Transaction transaction = getAssociatedTransaction();
//		HibernateUtil.initialise( transaction, true );
		transaction.addToScope();
		JSFUtil.redirect( TransactionEditPage.class );
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<RealizedProductListBean> suggestProductsByItemCode( String searchStr ) {
		BeanDao realizedProductBeanDao = new BeanDao(RealizedProduct.class);
		realizedProductBeanDao.setListBeanClass(RealizedProductListBean.class);
		realizedProductBeanDao.setSelectCriteria( "bean.id, bean.itemCode, bean.productInfo.product.itemCode" );
		realizedProductBeanDao.addWhereCriteria("bean.productInfo.product.itemCode like :itemCodeSearchStr OR bean.itemCode like :itemCodeSearchStr");
		realizedProductBeanDao.setIsReturningActiveBeans( true );
		realizedProductBeanDao.setNamedParameter( "itemCodeSearchStr", "%" + searchStr + "%" );
		realizedProductBeanDao.setMaxResults( 15 );
		return realizedProductBeanDao.getAll();
	}


	@SuppressWarnings("unchecked")
	public List<Customer> suggestCustomers( String searchStr ) {
		BeanDao aqlBeanDao = new BeanDao(Customer.class);
		aqlBeanDao.setSelectCriteria( "bean.id, bean.subscriber.firstName, bean.subscriber.surname, bean.active, bean.deletable" );
		aqlBeanDao.addWhereCriteria("CONCAT( bean.subscriber.firstName, ' ', bean.subscriber.surname ) like :similarSearchText OR bean.id like :exactSearchText");
		aqlBeanDao.setMaxResults( 15 );
		aqlBeanDao.setNamedParameter( "exactSearchText",  searchStr + "%" );
		aqlBeanDao.setNamedParameter( "similarSearchText",  "%" + searchStr + "%" );
		return aqlBeanDao.getAll();
	}
	
	@Override
	public boolean saveBean() {
		boolean savedSuccessully = super.saveBean();
		SerialNumber serialNumber = resolveAssociatedBean();
		serialNumber.updateStockQuantity();
		return savedSuccessully;
	}

	public String getCustomerDisplayStr( Customer customer ) {
		return customer.getId() + " " + customer.getDisplayName();
	}

	public void setCurrentCustomer( SelectEvent event ) {
		Customer endCustomer = (Customer) event.getObject();
		SerialNumber serialNumber = JSFUtil.getBeanFromScope(SerialNumber.class);

		Customer loadedEndCustomer = new BeanDao( endCustomer.getClass() ).get( endCustomer.getId() );
//		HibernateUtil.initialise( loadedEndCustomer, true );
		serialNumber.updateCurrentCustomer(loadedEndCustomer);
		setSelectedCurrentCustomer(null);
		setShowingCustomerSearch( false );
	}

	public void showItemSearch() {
		isShowingItemCodeSearch = true;
	}

	public void showCustomerSearch() {
		setShowingCustomerSearch( true );
	}

	public void setRealizedProductFromItemCode( SelectEvent event ) {
		RealizedProductListBean autoCompleteRealizedProductListBean = (RealizedProductListBean) event.getObject();
		SerialNumber serialNumber = resolveAssociatedBean();

		RealizedProduct loadedRealizedProduct = new BeanDao( RealizedProduct.class ).get(autoCompleteRealizedProductListBean.getId());
//		HibernateUtil.initialise( loadedRealizedProduct, true );
		RealizedProduct oldRealizedProduct = serialNumber.getRealizedProduct();
		serialNumber.setRealizedProduct( loadedRealizedProduct );
		if( oldRealizedProduct != null ) {
			serialNumber.setSerialNumberHistory( serialNumber.getSerialNumberHistory() + "\n " + FormatUtil.formatDate( new Date() ) + " serial number changed from " + oldRealizedProduct.determineItemCode() + " to " + loadedRealizedProduct.determineItemCode() );
		}
		if( serialNumber.getBuildDate() == null ) {
			serialNumber.setBuildDate( new Date() );
		}
		serialNumber.saveDetails();
		setSelectedRealizedProductListBean( null );
		isShowingItemCodeSearch = false;
	}

	public String getItemCodeDisplayText( RealizedProductListBean realizedProductListBean ) {
		return realizedProductListBean.determineItemCode();
	}

	public Transaction getAssociatedTransaction() {
		SerialNumber serialNumber = resolveAssociatedBean();
		return serialNumber.getAssociatedTransaction();
	}

	public boolean getSerialIsKit() {
		return getKitContentsList().size() > 0;
	}

	public boolean getSerialIsPartOfKit() {
		SerialNumber serialNumber = resolveAssociatedBean();
		return serialNumber.getSerialNumberOwner() != null && serialNumber.getSerialNumberOwner() instanceof SerialNumber;
	}

	@SuppressWarnings("unchecked")
	public List<SerialNumber> getKitContentsList() {
		SerialNumber serialNumber = resolveAssociatedBean();
		if (serialNumber != null) {
			BeanDao serialDao = new BeanDao(SerialNumber.class);
			serialDao.setWhereCriteria("bean.serialNumberOwner.id=" + serialNumber.getId());
			//serialDao.addWhereCriteria("bean.id != " + serialNumber.getId());
			return serialDao.setIsReturningActiveBeans(true).getAll();
		}
		return new ArrayList<SerialNumber>();
	}

	public SerialNumber getAssociatedKit() {
		//this if is because now the link is in a quick view component
		//it is evaluated before the page (regardless of the rendered
		//attribute on the panel that wraps it) which means it is 
		//calling this method on non-kit items which cant be cast
		if (getSerialIsPartOfKit()) {
			SerialNumber serialNumber = resolveAssociatedBean();
			if (serialNumber != null) {
				Object serialNumberOwner = serialNumber.getSerialNumberOwner();
				if (serialNumberOwner instanceof SerialNumber) {
					return (SerialNumber) serialNumberOwner;
				}
			}
		}
		return null;
	}

	public String redirectToAssociatedKit() {
		SerialNumber kit = getAssociatedKit();
		kit.addToScope();
		JSFUtil.redirect( SerialNumberEditPage.class );
		return null;
	}

	public String redirectToKitItem() {
		SerialNumber kitItem = (SerialNumber) JSFUtil.getRequest().getAttribute("kitItem");
		kitItem.addToScope();
		JSFUtil.redirect( SerialNumberEditPage.class );
		return null;
	}

	public String redirectToProductEdit() {
		SerialNumber kitItem = (SerialNumber) JSFUtil.getRequest().getAttribute("kitItem");
		if (kitItem != null && kitItem.getRealizedProduct() != null) {
			kitItem.getRealizedProduct().addToScope();
			JSFUtil.redirect( RealizedProductEditPage.class );
		}
		return null;
	}

	public SelectItem[] getProductVersionSelectItemBeans() {
		SerialNumber serialNumber = resolveAssociatedBean();
		if( serialNumber.getRealizedProduct() != null ) {
			BeanDao productVersionDao = new BeanDao( ProductVersion.class ).addWhereCriteria( "bean.product.id = " + serialNumber.getRealizedProduct().getProductInfo().getProduct().getId() );
			return AplosBean.getSelectItemBeansWithNotSelected( productVersionDao.setIsReturningActiveBeans(true).getAll(), "Not Selected" );
		} else {
			return new SelectItem[0];
		}
	}

	public List<RealizedProductReturn> getOptimisedReturns( SerialNumber serialNumber ) {
		return RealizedProductReturn.getOptimisedReturns( serialNumber.getId() );
	}

	public String createReturnAndRedirect( SerialNumber serialNumber ) {
		RealizedProductReturnEditPage.createNewReturn(serialNumber);
		JSFUtil.redirect(Website.getCurrentWebsiteFromTabSession().getPackageName(), RealizedProductReturnEditPage.class);
		return null;
	}

	public String printPackagingLabel() {
		SerialNumber serialNumber = resolveAssociatedBean();
		JSFUtil.redirect(new AplosUrl(PackagingLabelTemplate.getTemplateUrl( serialNumber.getId() )), true );
		return null;
	}

	public void setShowingItemCodeSearch(boolean isShowingItemCodeSearch) {
		this.isShowingItemCodeSearch = isShowingItemCodeSearch;
	}

	public boolean isShowingItemCodeSearch() {
		return isShowingItemCodeSearch;
	}

	public RealizedProductListBean getSelectedRealizedProductListBean() {
		return selectedRealizedProductListBean;
	}

	public void setSelectedRealizedProductListBean(
			RealizedProductListBean selectedRealizedProductListBean) {
		this.selectedRealizedProductListBean = selectedRealizedProductListBean;
	}

	public Customer getSelectedCurrentCustomer() {
		return selectedCurrentCustomer;
	}

	public void setSelectedCurrentCustomer(Customer selectedCurrentCustomer) {
		this.selectedCurrentCustomer = selectedCurrentCustomer;
	}

	public boolean isShowingCustomerSearch() {
		return isShowingCustomerSearch;
	}

	public void setShowingCustomerSearch(boolean isShowingCustomerSearch) {
		this.isShowingCustomerSearch = isShowingCustomerSearch;
	}

}
