package com.aplos.ecommerce.backingpage.realizedProductReturn;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.primefaces.event.SelectEvent;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.backingpage.OkBtnListener;
import com.aplos.common.backingpage.SaveBtnListener;
import com.aplos.common.backingpage.communication.AplosEmailEditPage;
import com.aplos.common.beans.Address;
import com.aplos.common.beans.AplosAbstractBean;
import com.aplos.common.beans.Country;
import com.aplos.common.beans.ShoppingCartItem;
import com.aplos.common.beans.communication.AplosEmail;
import com.aplos.common.enums.EmailTemplateEnum;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.backingpage.serialNumbers.AssignSerialNumbersPage;
import com.aplos.ecommerce.backingpage.transaction.TransactionEditPage;
import com.aplos.ecommerce.beans.CompanyContact;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.beans.RealizedProductReturn;
import com.aplos.ecommerce.beans.RealizedProductReturn.RealizedProductReturnStatus;
import com.aplos.ecommerce.beans.SerialNumber;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.beans.listbeans.RealizedProductListBean;
import com.aplos.ecommerce.beans.product.Product;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;
import com.aplos.ecommerce.module.EcommerceConfiguration;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=RealizedProductReturn.class)
public class RealizedProductReturnEditPage extends EditPage {
	private static final long serialVersionUID = 5373312156828455407L;

	private Customer selectedEndCustomer;
	private SerialNumber selectedSerialNumber;
	private RealizedProduct selectedRealizedProduct;

	private int sentEmailsSize;

	public RealizedProductReturnEditPage() {

		getEditPageConfig().setApplyBtnActionListener( new SaveBtnListener( this ) {

			private static final long serialVersionUID = -5942090347854722358L;

			@Override
			public void actionPerformed(boolean redirect) {
				super.actionPerformed(redirect);
				((RealizedProductReturnEditPage) getBackingPage()).additionalSaveDetails();
			}
		});
		getEditPageConfig().setOkBtnActionListener( new OkBtnListener( this ) {

			private static final long serialVersionUID = -810348717230097768L;

			@Override
			public void actionPerformed(boolean redirect) {
				super.actionPerformed(redirect);
				((RealizedProductReturnEditPage) getBackingPage()).additionalSaveDetails();
			}
		});
	}

	public void additionalSaveDetails() {
		RealizedProductReturn realizedProductReturn = resolveAssociatedBean();
		if( realizedProductReturn.getSerialNumber() != null ) {
			realizedProductReturn.getSerialNumber().saveDetails();
		}
	}


	@Override
	public boolean responsePageLoad() {
		super.responsePageLoad();
		RealizedProductReturn realizedProductReturn = JSFUtil.getBeanFromScope(RealizedProductReturn.class);
		realizedProductReturn.getMenuWizard().refreshMenu( realizedProductReturn );
		return true;
	}

	public String getItemCodeDisplayText( RealizedProductListBean realizedProductListBean ) {
		return realizedProductListBean.determineItemCode();
	}

	public String getSerialNumberDisplayText( SerialNumber serialNumber ) {
		return serialNumber.getId() + " " + serialNumber.getRealizedProduct().determineItemCode() + " " + serialNumber.getRealizedProduct().getDisplayName();
	}

	public String getProductNameDisplayText( RealizedProductListBean realizedProductListBean ) {
		return realizedProductListBean.getProductName() + " (" + realizedProductListBean.getId() + ")";
	}

	@SuppressWarnings("unchecked")
	public List<RealizedProduct> suggestItemCodes( String searchStr ) {
		BeanDao realizedProductBeanDao = new BeanDao(RealizedProduct.class);
		realizedProductBeanDao.setListBeanClass(RealizedProductListBean.class);
		realizedProductBeanDao.setSelectCriteria( "new com.aplos.ecommerce.beans.listbeans.RealizedProductListBean( bean.id, bean.itemCode, bean.productInfo.product.itemCode )" );
		realizedProductBeanDao.addWhereCriteria("bean.productInfo.product.itemCode like :itemCodeSearchStr OR bean.itemCode like :itemCodeSearchStr");
		realizedProductBeanDao.setIsReturningActiveBeans( true );
		realizedProductBeanDao.setNamedParameter( "itemCodeSearchStr", "%" + searchStr + "%" );
		realizedProductBeanDao.setMaxResults( 15 );

		return realizedProductBeanDao.getAll();
	}

	@SuppressWarnings("unchecked")
	public List<RealizedProduct> suggestProductNames( String searchStr ) {
		BeanDao realizedProductBeanDao = new BeanDao(RealizedProductListBean.class);
		realizedProductBeanDao.setListBeanClass(RealizedProductListBean.class);
		realizedProductBeanDao.setSelectCriteria( "new com.aplos.ecommerce.beans.listbeans.RealizedProductListBean( bean.id, bean.productInfo.product.name )" );
		realizedProductBeanDao.addWhereCriteria("bean.productInfo.product.name like :productNameSearchStr");
		realizedProductBeanDao.setMaxResults( 15 );
		realizedProductBeanDao.setIsReturningActiveBeans( true );
		realizedProductBeanDao.setNamedParameter( "productNameSearchStr", "%" + searchStr + "%" );
		realizedProductBeanDao.setMaxResults( 15 );
		return realizedProductBeanDao.getAll();
	}

	@SuppressWarnings("unchecked")
	public List<SerialNumber> suggestSerialNumbers( String searchStr ) {
		BeanDao serialNumberBeanDao = new BeanDao(SerialNumber.class);
		serialNumberBeanDao.setSelectCriteria( "bean.id, bean.realizedProduct.productInfo.product.name, bean.realizedProduct.itemCode, bean.realizedProduct.productInfo.product.itemCode" );
		serialNumberBeanDao.addWhereCriteria("bean.id like :similarSearchText");
		serialNumberBeanDao.setMaxResults( 15 );
		serialNumberBeanDao.setNamedParameter( "similarSearchText", "%" + searchStr + "%" );
		serialNumberBeanDao.setMaxResults( 15 );
		return serialNumberBeanDao.getAll();
	}

	public String getTechnicalSupport() {
		RealizedProductReturn realizedProductReturn = JSFUtil.getBeanFromScope(RealizedProductReturn.class);
		String technicalSupport = "";

		if( realizedProductReturn.determineReturnProduct() != null ) {
			String productTypeSupport;
			Product product = realizedProductReturn.determineReturnProduct().getProductInfo().getProduct();
			for (int i=0, n= product.getProductTypes().size() ; i<n ; i++) {
				productTypeSupport = product.getProductTypes().get(i).getTechnicalSupport();
				if (productTypeSupport != null) {
					technicalSupport += productTypeSupport + "\n\r";
				}
			}
		}
		return technicalSupport;
	}

	public String getCustomerCompanyName() {
		RealizedProductReturn realizedProductReturn = (RealizedProductReturn)JSFUtil.getBeanFromScope(RealizedProductReturn.class);
		if( realizedProductReturn.getSerialNumber() != null && realizedProductReturn.getSerialNumber().getOriginalCustomer() instanceof CompanyContact ) {
			return ((CompanyContact)realizedProductReturn.getSerialNumber().getOriginalCustomer()).getCompany().getCompanyName();
		}
		return "";
	}

	public void useReturnsCustomer() {
		RealizedProductReturn realizedProductReturn = (RealizedProductReturn)JSFUtil.getBeanFromScope(RealizedProductReturn.class);
		realizedProductReturn.getSerialNumber().setCurrentCustomer( realizedProductReturn.getSerialNumber().getOriginalCustomer() );
	}

	public void useOriginalCustomerDeliveryDetails() {
		RealizedProductReturn realizedProductReturn = (RealizedProductReturn)JSFUtil.getBeanFromScope(RealizedProductReturn.class);
		realizedProductReturn.setReturnAddressFromCachedOriginalCustomer();
	}

	public void useCurrentCustomerDeliveryDetails() {
		RealizedProductReturn realizedProductReturn = (RealizedProductReturn)JSFUtil.getBeanFromScope(RealizedProductReturn.class);
		realizedProductReturn.setReturnAddressFromEndCustomer();
	}

	public void clearDeliveryDetails() {
		RealizedProductReturn realizedProductReturn = (RealizedProductReturn)JSFUtil.getBeanFromScope(RealizedProductReturn.class);
		Address address = new Address();
		address.setContactFirstName(""); // so that this field isnt null as this is checked in responsePageLoad to use the default or not
		realizedProductReturn.setReturnAddress(address);
	}

	public void redirectToAplosEmail( EmailTemplateEnum emailTemplateEnum ) {
		RealizedProductReturn realizedProductReturn = resolveAssociatedBean();
		if( realizedProductReturn.getSerialNumber() != null 
				&& realizedProductReturn.getSerialNumber().getCurrentCustomer() == null ) {
			JSFUtil.addMessage( "Please select a customer for the serial number via the 'Current Customer' tab" );
		} else {
			AplosEmail aplosEmail = new AplosEmail( emailTemplateEnum, realizedProductReturn, realizedProductReturn );
			aplosEmail.addEmailFolder(realizedProductReturn);
			aplosEmail.addToScope();
			JSFUtil.redirect(AplosEmailEditPage.class);
		}
	}

	public void redirectToReturnsAuthorisationNotePrintSend() {
		redirectToAplosEmail( EcommerceEmailTemplateEnum.RETURNS_AUTH );
	}

	public void redirectToConditionReceivedReportPrintSend() {
		redirectToAplosEmail( EcommerceEmailTemplateEnum.CONDITION_RECEIVED );
	}

	public void redirectToCalibrationCertificatePrintSend() {
		redirectToAplosEmail( EcommerceEmailTemplateEnum.CALIBRATION );
	}

	public void redirectToRepairReportPrintSend() {
		redirectToAplosEmail( EcommerceEmailTemplateEnum.REPAIR_REPORT );
	}

	public void redirectToAplosEmail() {
		AplosEmail aplosEmail = (AplosEmail) JSFUtil.getRequest().getAttribute( "aplosEmail" );
		aplosEmail.addToScope();
		JSFUtil.redirect( AplosEmailEditPage.class );
	}

	public boolean getShowCreateReturnTransactionButton() {
		RealizedProductReturn realizedProductReturn = JSFUtil.getBeanFromScope(RealizedProductReturn.class);
		if( !realizedProductReturn.isNew() && realizedProductReturn.getSerialNumber() != null ) {
			return getAssociatedReturnTransaction() == null;
		} else {
			return false;
		}
	}

	public Transaction getAssociatedReturnTransaction() {
		RealizedProductReturn realizedProductReturn = JSFUtil.getBeanFromScope(RealizedProductReturn.class);
		BeanDao aqlBeanDao = new BeanDao(Transaction.class);
		aqlBeanDao.addWhereCriteria("bean.realizedProductReturn.id = " + realizedProductReturn.getId());
		return aqlBeanDao.getFirstBeanResult();
	}

	public void createReturnTransaction() {
		RealizedProductReturn realizedProductReturn = JSFUtil.getBeanFromScope(RealizedProductReturn.class);
		if( realizedProductReturn.getSerialNumber().getCurrentCustomer() != null ) {
			Transaction transaction = Transaction.createNewTransaction(realizedProductReturn.determineEndCustomer(), true);
	
			transaction.addToScope();
			JSFUtil.redirect(TransactionEditPage.class);
	
			transaction.setSpecialDeliveryInstructions( realizedProductReturn.getDispatchDetails() );
			transaction.setRealizedProductReturn(realizedProductReturn);
	
			BeanDao realizedProductBeanDao = new BeanDao(RealizedProduct.class);
			RealizedProduct hourlyRate = realizedProductBeanDao.get(EcommerceConfiguration.getEcommerceConfiguration().getHourlyRateRealizedProduct().getId());
//			HibernateUtil.initialise( hourlyRate, true );
			transaction.getEcommerceShoppingCart().addToCart(hourlyRate, false);
	
			RealizedProduct parts = realizedProductBeanDao.get(EcommerceConfiguration.getEcommerceConfiguration().getPartsRealizedProduct().getId());
//			HibernateUtil.initialise( parts, true );
			ShoppingCartItem partsCartItem = transaction.getEcommerceShoppingCart().addToCart(parts, false);
			partsCartItem.setCustomisable( true );
			applyBtnAction();
		} else {
			JSFUtil.addMessage( "Please add a customer to the serial number" );
		}
	}

	public SelectItem[] getCountrySelectItems() {
		return AplosAbstractBean.getSelectItemBeans(Country.class);
	}

	public String addParenthesesIfNecessary(String string) {
		if (string != null && !string.equals("")) {
			return "(" + string + ")";
		}
		else {
			return "";
		}
	}

	public List<SelectItem> getRealizedProductReturnStatusSelectItems() {
		return CommonUtil.getEnumSelectItems(RealizedProductReturnStatus.class, null);
	}

	public static RealizedProductReturn createNewReturn(SerialNumber serialNumber ) {
		RealizedProductReturn realizedProductReturn = new RealizedProductReturn();
		realizedProductReturn.updateSerialNumber(serialNumber);
		if( serialNumber.getAssociatedTransaction() != null ) {
			realizedProductReturn.getSerialNumber().setCurrentCustomer(serialNumber.getAssociatedTransaction().getCustomer());
		}
		realizedProductReturn.addToScope();
		return realizedProductReturn;
	}

	public static RealizedProductReturn createNewReturn(Customer customer) {
		RealizedProductReturn realizedProductReturn = new RealizedProductReturn();
		realizedProductReturn.addToScope();
		return realizedProductReturn;
	}

	public String getEndCustomerDisplayStr( Customer customer ) {
		return customer.getId() + " " + customer.getDisplayName();
	}


	@SuppressWarnings("unchecked")
	public List<Customer> suggestEndCustomers( String searchStr ) {
		BeanDao aqlBeanDao = new BeanDao(Customer.class);
		aqlBeanDao.setSelectCriteria( "bean.id, bean.subscriber.firstName, bean.subscriber.surname, bean.active, bean.deletable" );
		aqlBeanDao.addWhereCriteria("CONCAT( bean.subscriber.firstName, ' ', bean.subscriber.surname ) like :similarSearchText OR bean.id like :exactSearchText");
		aqlBeanDao.setMaxResults( 15 );
		aqlBeanDao.setNamedParameter( "exactSearchText",  searchStr + "%" );
		aqlBeanDao.setNamedParameter( "similarSearchText",  "%" + searchStr + "%" );
		return aqlBeanDao.getAll();
	}

	public void setEndCustomer( SelectEvent event ) {
		Customer endCustomer = (Customer) event.getObject();
		RealizedProductReturn realizedProductReturn = JSFUtil.getBeanFromScope(RealizedProductReturn.class);

		Customer loadedEndCustomer = new BeanDao( endCustomer.getClass() ).get( endCustomer.getId() );
//		HibernateUtil.initialise( loadedEndCustomer, true );
		realizedProductReturn.getSerialNumber().updateCurrentCustomer(loadedEndCustomer);
		setSelectedEndCustomer(null);
	}

	public void clearSearchTexts() {
		setSelectedRealizedProduct( null );
		setSelectedSerialNumber( null );
	}

	public void setReturnProductFromItemCode( SelectEvent event ) {
		RealizedProduct realizedProduct = (RealizedProduct) event.getObject();
		RealizedProductReturn realizedProductReturn = JSFUtil.getBeanFromScope(RealizedProductReturn.class);

		RealizedProduct loadedRealizedProduct = new BeanDao( RealizedProduct.class ).get( realizedProduct.getId());
//		HibernateUtil.initialise( loadedRealizedProduct, true );
		realizedProductReturn.setReturnProductBeforeSerialNumberSet( loadedRealizedProduct );
		realizedProductReturn.saveDetails();
		clearSearchTexts();
	}

	public void setReturnProductFromProductName( SelectEvent event ) {
		RealizedProduct realizedProduct = (RealizedProduct) event.getObject();
		RealizedProductReturn realizedProductReturn = JSFUtil.getBeanFromScope(RealizedProductReturn.class);

		RealizedProduct loadedRealizedProduct = new BeanDao( RealizedProduct.class ).get(realizedProduct.getId());
//		HibernateUtil.initialise( loadedRealizedProduct, true );
		realizedProductReturn.setReturnProductBeforeSerialNumberSet( loadedRealizedProduct );
		realizedProductReturn.saveDetails();
		clearSearchTexts();
	}
	
	public void createSerialNumber() {
		RealizedProductReturn realizedProductReturn = resolveAssociatedBean();
		AssignSerialNumbersPage assignSerialNumbersPage = new AssignSerialNumbersPage();
		List<SerialNumber> serialNumberList = assignSerialNumbersPage.assignSerialNumbers( realizedProductReturn.determineReturnProduct(), null, 1, false );
		if( serialNumberList.size() > 0 ) {
			serialNumberList.get( 0 ).setReassigned( true );
			serialNumberList.get( 0 ).saveDetails();
			realizedProductReturn.updateSerialNumber( serialNumberList.get( 0 ) );
			realizedProductReturn.saveDetails();
		}
	}

	public void setSerialNumber( SelectEvent event ) {
		SerialNumber serialNumber = (SerialNumber) event.getObject();
		RealizedProductReturn realizedProductReturn = JSFUtil.getBeanFromScope(RealizedProductReturn.class);
		SerialNumber loadedSerialNumber = new BeanDao( SerialNumber.class ).get(serialNumber.getId());
//		HibernateUtil.initialise( loadedSerialNumber, true );
		realizedProductReturn.updateSerialNumber( loadedSerialNumber );
		realizedProductReturn.saveDetails();
		realizedProductReturn.getMenuWizard().createMenuSteps();
		clearSearchTexts();
	}

	public String getProductDisplayStr( SerialNumber serialNumber ) {
		return String.valueOf( serialNumber.getId() );
	}

	public void setSentEmailsSize(int sentEmailsSize) {
		this.sentEmailsSize = sentEmailsSize;
	}

	public int getSentEmailsSize() {
		return sentEmailsSize;
	}

	public Customer getSelectedEndCustomer() {
		return selectedEndCustomer;
	}

	public void setSelectedEndCustomer(Customer selectedEndCustomer) {
		this.selectedEndCustomer = selectedEndCustomer;
	}

	public SerialNumber getSelectedSerialNumber() {
		return selectedSerialNumber;
	}

	public void setSelectedSerialNumber(SerialNumber selectedSerialNumber) {
		this.selectedSerialNumber = selectedSerialNumber;
	}

	public RealizedProduct getSelectedRealizedProduct() {
		return selectedRealizedProduct;
	}

	public void setSelectedRealizedProduct(RealizedProduct selectedRealizedProduct) {
		this.selectedRealizedProduct = selectedRealizedProduct;
	}
}
