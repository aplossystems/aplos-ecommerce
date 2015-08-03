package com.aplos.ecommerce.backingpage.serialNumbers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.primefaces.event.SelectEvent;

import com.aplos.common.AplosUrl;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.backingpage.OkBtnListener;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.IncludedProduct;
import com.aplos.ecommerce.beans.IncludedProductGroup;
import com.aplos.ecommerce.beans.ProductVersion;
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.beans.SerialNumber;
import com.aplos.ecommerce.beans.listbeans.RealizedProductListBean;
import com.aplos.ecommerce.beans.listbeans.SerialNumberListBean;
import com.aplos.ecommerce.templates.printtemplates.PackagingLabelTemplate;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=SerialNumber.class)
public class AssignSerialNumbersPage extends EditPage {
	private static final long serialVersionUID = -4717213572990808511L;

	private String itemCode;
	private String productName;
	private int quantity;
	private List<SerialNumberListBean> serialNumberListBeans;
	private Map<IncludedProduct, List<SerialNumber>> scannedProducts;
	private Map<IncludedProductGroup, List<SerialNumber>> scannedProductGroups;
	private String scannedSerialNumber;
	private boolean allSerialNumbersSelected;
	private boolean isReadyForPrinting = false;

	private RealizedProduct selectedRealizedProduct;
	private RealizedProduct selectedRealizedProductListBean;
	private ProductVersion selectedProductVersion;

	public AssignSerialNumbersPage() {
		if (getSerialNumberListBeans() == null) {
			setSerialNumberListBeans(new ArrayList<SerialNumberListBean>());
		}
		getEditPageConfig().setOkBtnActionListener(new OkBtnListener(this) {
			private static final long serialVersionUID = 8280652294365727235L;

			@Override
			public void actionPerformed(boolean redirect) {
				JSFUtil.redirect(getBeanDao().getListPageClass());
			}
		});
		getRequiredStateBindings().clear();
	}

	public SelectItem[] getProductVersionSelectItemBeans() {
		if (getSelectedRealizedProduct() != null) {
			BeanDao productVersionDao = new BeanDao(ProductVersion.class)
					.addWhereCriteria("bean.product.id = "
							+ getSelectedRealizedProduct().getProductInfo()
									.getProduct().getId());
			return AplosBean.getSelectItemBeansWithNotSelected(
					productVersionDao.setIsReturningActiveBeans(true).getAll(), "Not Selected");
		} else {
			return new SelectItem[0];
		}
	}

	public void assignSerialNumbers() {
		assignSerialNumbers( getSelectedRealizedProduct(), getSelectedProductVersion(), quantity, true );
	}
	
	public List<SerialNumber> assignSerialNumbers( RealizedProduct realizedProduct, ProductVersion productVersion, int quantity, boolean processKits ) {
		List<SerialNumber> createdSerialNumberList = new ArrayList<SerialNumber>();
		if (quantity > 0) {
			BeanDao serialNumberDao = new BeanDao(SerialNumber.class);
			serialNumberDao.setSelectCriteria("bean.id, bean.active");
			serialNumberDao.addWhereCriteria("bean.realizedProduct = null");
			serialNumberDao.addWhereCriteria("bean.isVoidStickerRequired = " + realizedProduct.isVoidStickerRequired());
			serialNumberDao.setOrderBy("bean.id");
			serialNumberDao.setListBeanClass(SerialNumberListBean.class);
			List<SerialNumberListBean> unusedSerialNumberAssignments = serialNumberDao.setIsReturningActiveBeans(true).getAll();
			if (unusedSerialNumberAssignments.size() >= quantity) {
				if (realizedProduct != null) {
					boolean isKit = realizedProduct.getProductInfo().getIncludedProducts().size() != 0 ||
							realizedProduct.getProductInfo().getIncludedProductGroups().size() != 0;
					if( !isKit || processKits ) {
						boolean serialNumbersSaved = false;
						
						for (int i = 0; i < quantity; i++) {
							getSerialNumberListBeans().add(unusedSerialNumberAssignments.get(i));
							if( !isKit ) {
								createdSerialNumberList.add( saveSerialNumberAssignmentDetails(realizedProduct,productVersion,unusedSerialNumberAssignments.get(i).getId()) );
								serialNumbersSaved = true;
								setReadyForPrinting(true);
							}
						}
	
						if( !serialNumbersSaved ) {
							scannedProducts = new HashMap<IncludedProduct, List<SerialNumber>>();
							if (realizedProduct.getProductInfo().getIncludedProducts().size() > 0) {
								for (IncludedProduct tempIncludedProduct : realizedProduct.getProductInfo().getIncludedProducts()) {
									scannedProducts.put(tempIncludedProduct,new ArrayList<SerialNumber>());
								}
							}
	
							scannedProductGroups = new HashMap<IncludedProductGroup, List<SerialNumber>>();
							if( realizedProduct.getProductInfo().getIncludedProductGroups().size() > 0 ) {
								for (IncludedProductGroup tempIncludedProductGroup : realizedProduct.getProductInfo().getIncludedProductGroups()) {
									scannedProductGroups.put(tempIncludedProductGroup,new ArrayList<SerialNumber>());
								}
							}
						} else {
							if( realizedProduct != null ) {
								realizedProduct.updateStockQuantity();
								realizedProduct.saveDetails();
							}
						}
					} else {
						JSFUtil.addMessage(
								"You cannot add a serial number to a kit from here.",
								FacesMessage.SEVERITY_WARN);
					}
				} else {
					JSFUtil.addMessage(
							"The part number you have entered is not recognized.",
							FacesMessage.SEVERITY_ERROR);
				}
			} else {
				JSFUtil.addMessage(
						"There are not enough available serial numbers, please generate some more to continue",
						FacesMessage.SEVERITY_ERROR);
			}
		} else {
			JSFUtil.addMessage("Please enter a quantity greater than 0.",
					FacesMessage.SEVERITY_ERROR);
		}
		return createdSerialNumberList;
	}

	public int getExpectedQuantity( IncludedProduct includedProduct ) {
		return includedProduct.getQuantity() * getQuantity();
	}

	public int getExpectedQuantity( IncludedProductGroup includedProductGroup ) {
		return includedProductGroup.getQuantity() * getQuantity();
	}

	public void updateSelectedSerialNumbers() {
		for (SerialNumberListBean serialNumberListBean : getSerialNumberListBeans()) {
			serialNumberListBean.setSelected(isAllSerialNumbersSelected());
		}
	}

	public String redirectToPrintPackagingLabels() {
		List<Long> selectedSerialNumberIds = new ArrayList<Long>();
		for (SerialNumberListBean serialNumberListBean : getSerialNumberListBeans()) {
			selectedSerialNumberIds.add(serialNumberListBean.getId());
		}

		JSFUtil.redirect(new AplosUrl(PackagingLabelTemplate.getTemplateUrl(selectedSerialNumberIds.toArray(new Long[0]))),true);

		return null;
	}

	public void addNonSerialisedProduct() {
		RealizedProduct includedProduct = JSFUtil
				.getBeanFromRequest("includedProduct");
		scannedProducts.get(includedProduct).add(null);
	}

	public void addAllNonSerialisedProduct() {
		RealizedProduct includedProduct = JSFUtil
				.getBeanFromRequest("includedProduct");
		List<SerialNumber> assignmentList = scannedProducts
				.get(includedProduct);
		for (int i = assignmentList.size() - 1, n = getQuantity(); i < n; i++) {
			scannedProducts.get(includedProduct).add(null);
		}
	}


	public void addNonSerialisedProductGroup() {
		IncludedProductGroup includedProductGroup = JSFUtil
				.getBeanFromRequest("includedProductGroup");
		scannedProductGroups.get(includedProductGroup).add(null);
	}

	public void addAllNonSerialisedProductGroup() {
		IncludedProductGroup includedProductGroup = JSFUtil
		.getBeanFromRequest("includedProductGroup");
		List<SerialNumber> assignmentList = scannedProductGroups
				.get(includedProductGroup);
		for (int i = assignmentList.size() - 1, n = getQuantity(); i < n; i++) {
			scannedProductGroups.get(includedProductGroup).add(null);
		}
	}

	public SerialNumber saveSerialNumberAssignmentDetails( RealizedProduct realizedProduct, ProductVersion productVersion, Long serialNumberId) {
		SerialNumber serialNumber = new BeanDao(SerialNumber.class)
				.get(serialNumberId);
		serialNumber.setRealizedProduct(realizedProduct);
		serialNumber.setProductVersion(productVersion);
		if( serialNumber.getBuildDate() == null ) {
			serialNumber.setBuildDate(new Date());
		}
		serialNumber.setSerialNumberHistory("Serial # " + serialNumber.getId()
				+ " registered as: "
				+ realizedProduct.determineItemCode() + " , built:"
				+ new Date());
		serialNumber.saveDetails();
		return serialNumber;
	}

	public boolean getNonSerialBookOutAvailable( IncludedProduct includedProduct ) {
		if (includedProduct != null && includedProduct.isSerialNumberRequired()) {
			return false;
		} else {
			if (getQuantity() > scannedProducts.get(includedProduct).size()) {
				return true;
			} else {
				return false;
			}
		}
	}

	public boolean getNonSerialBookOutAvailable( IncludedProductGroup includedProductGroup ) {
		if (includedProductGroup != null && includedProductGroup.isSerialNumberRequired()) {
			return false;
		} else {
			if (getQuantity() > scannedProducts.get(includedProductGroup).size()) {
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public boolean getIsValidationRequired() {
		return validationRequired("okBtn") || validationRequired("assignBtn");
	}

	public String getOkBtnLabel() {
		if (getSerialNumberListBeans().size() == 0) {
			return "Assign";
		} else {
			return "Ok";
		}
	}

	public String getItemCodeDisplayText(
			RealizedProductListBean realizedProductListBean) {
		return realizedProductListBean.determineItemCode();
	}

	public String getProductNameDisplayText(
			RealizedProductListBean realizedProductListBean) {
		return realizedProductListBean.getProductInfo().getProduct().getName() + " ("
				+ realizedProductListBean.getId() + ")";
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<RealizedProductListBean> suggestProductsByCode(String searchStr) {
		BeanDao realizedProductBeanDao = new BeanDao(RealizedProduct.class);
		realizedProductBeanDao.setListBeanClass(RealizedProductListBean.class);
		realizedProductBeanDao
				.setSelectCriteria("bean.id, bean.productInfo.product.name, bean.itemCode, bean.productInfo.product.itemCode");
		realizedProductBeanDao
				.addWhereCriteria("bean.productInfo.product.itemCode like :itemCodeSearchStr OR bean.itemCode like :itemCodeSearchStr");
		realizedProductBeanDao.setIsReturningActiveBeans( true );
		realizedProductBeanDao.setNamedParameter("itemCodeSearchStr", "%" + searchStr + "%");
		realizedProductBeanDao.setMaxResults(15);

		return realizedProductBeanDao.getAll();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<RealizedProductListBean> suggestProductsByName(String searchStr) {
		BeanDao realizedProductBeanDao = new BeanDao(RealizedProduct.class);
		realizedProductBeanDao.setListBeanClass(RealizedProductListBean.class);
		realizedProductBeanDao
				.setSelectCriteria("bean.id, bean.productInfo.product.name, bean.itemCode, bean.productInfo.product.itemCode");
		realizedProductBeanDao
				.addWhereCriteria("bean.productInfo.product.name like :productNameSearchStr");
		realizedProductBeanDao.setMaxResults(15);
		realizedProductBeanDao.setIsReturningActiveBeans( true );
		realizedProductBeanDao.setNamedParameter("productNameSearchStr", "%" + searchStr + "%");
		realizedProductBeanDao.setMaxResults(15);

		return realizedProductBeanDao.getAll();
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getQuantity() {
		return quantity;
	}

	public List<SerialNumberListBean> getSerialNumberAssignments() {
		return getSerialNumberListBeans();
	}

	public void setSerialNumberAssignments(
			List<SerialNumberListBean> serialNumberAssignments) {
		this.setSerialNumberListBeans(serialNumberAssignments);
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductByItemCode( SelectEvent event ) {
		setSelectedRealizedProduct( (RealizedProduct) new BeanDao(RealizedProduct.class) .get(getSelectedRealizedProductListBean().getId()));
//		HibernateUtil.initialise( getSelectedRealizedProduct(), true );

		setItemCode(getSelectedRealizedProduct().determineItemCode());
		setProductName(getSelectedRealizedProduct().getProductInfo()
				.getProduct().getName());
	}

	public void setProductByName( SelectEvent event ) {
		setSelectedRealizedProduct( (RealizedProduct) new BeanDao(RealizedProduct.class).get(getSelectedRealizedProductListBean().getId()));
//		HibernateUtil.initialise( getSelectedRealizedProduct(), true );

		setProductName(getSelectedRealizedProduct().getProductInfo().getProduct().getName());
		setItemCode(getSelectedRealizedProduct().determineItemCode());
	}

	public void serialNumberScanned() {
		scannedSerialNumber = scannedSerialNumber.replaceAll( "(?i)TSN", "" );
		if( !CommonUtil.validatePositiveInteger( scannedSerialNumber ) ) {
			JSFUtil.addMessage("Please enter only numbers for the serial number",FacesMessage.SEVERITY_ERROR);
			return;
		}
		BeanDao aqlBeanDao = new BeanDao(SerialNumber.class);
		aqlBeanDao.setWhereCriteria("bean.id = " + scannedSerialNumber);
		SerialNumber serialNumber = (SerialNumber) aqlBeanDao.getFirstBeanResult();

		if (serialNumber == null) {
			JSFUtil.addMessage("This serial number cannot be found on the system",FacesMessage.SEVERITY_ERROR);
			return;
		}
		if (serialNumber.getRealizedProduct() == null) {
			JSFUtil.addMessage("This serial number is not associated with a product",FacesMessage.SEVERITY_ERROR);
			return;
		}
		if (serialNumber.getSerialNumberOwner() != null) {
			JSFUtil.addMessage("This serial number has already been booked out",FacesMessage.SEVERITY_ERROR);
			return;
		}
		if (serialNumber.isAddedToWaste()) {
			JSFUtil.addMessage("This serial number has been added to waste", FacesMessage.SEVERITY_ERROR);
			return;
		}
		if (serialNumber.isReassigned()) {
			JSFUtil.addMessage("This serial number has been reassigned", FacesMessage.SEVERITY_ERROR);
			return;
		}


		IncludedProduct includedProduct = getRealizedProduct().getProductInfo().getIncludedProduct(serialNumber.getRealizedProduct());
		boolean isSerialNumberAssigned = false;
		boolean isQuantityAlreadyReached = false;
		if(scannedProducts.containsKey( includedProduct )) {
			List<SerialNumber> assignmentList = scannedProducts.get(includedProduct);
			if (assignmentList.size() < (getQuantity() * includedProduct.getQuantity())) {
				if (!assignmentList.contains(serialNumber)) {
					assignmentList.add(serialNumber);
					isSerialNumberAssigned = true;
					scannedSerialNumber = "";
				} else {
					JSFUtil.addMessage("This serial number has already been assigned to this kit.",FacesMessage.SEVERITY_ERROR);
				}
			} else {
				isQuantityAlreadyReached = true;
			}
		}

		if( !isSerialNumberAssigned ) {
			for( IncludedProductGroup tempIncludedProductGroup : scannedProductGroups.keySet() ) {
				if( tempIncludedProductGroup.getProductGroup().getProductRetrieverList().contains(serialNumber.getRealizedProduct())) {
					List<SerialNumber> assignmentList = scannedProductGroups.get(tempIncludedProductGroup);
					if (assignmentList.size() < (getQuantity() * tempIncludedProductGroup.getQuantity()) ) {
						if (!assignmentList.contains(serialNumber)) {
							assignmentList.add(serialNumber);
							isSerialNumberAssigned = true;
							scannedSerialNumber = "";
							break;
						} else {
							JSFUtil.addMessage("This serial number has already been assigned to this kit.",FacesMessage.SEVERITY_ERROR);
						}
					} else {
						isQuantityAlreadyReached = true;
					}
				}
			}
		}

		if( isQuantityAlreadyReached ) {
					JSFUtil.addMessage("The expected quantity has already been reached for this product.",FacesMessage.SEVERITY_ERROR);
		} else if( !isSerialNumberAssigned ) {
			JSFUtil.addMessage("This product is not part of the current kit.",FacesMessage.SEVERITY_ERROR);
		}
	}

	public boolean getBookOutAndAssignBtnDisabled() {
		for (IncludedProduct tempIncludedProduct : scannedProducts.keySet()) {
			if (getQuantity() > scannedProducts.get(tempIncludedProduct).size()) {
				return true;
			}
		}
		return false;
	}

	public boolean getBookOutAndAssignBtnRendered() {
		if (scannedProducts == null) {
			return false;
		} else {
			return true;
		}
	}

	public void bookOutAndAssign() {
		for (SerialNumber tempSna : getSerialNumberListBeans()) {
			saveSerialNumberAssignmentDetails(getSelectedRealizedProduct(),getSelectedProductVersion(),tempSna.getId());
		}

		if( getSelectedRealizedProduct() != null ) {
			getSelectedRealizedProduct().updateStockQuantity();
			getSelectedRealizedProduct().saveDetails();
		}

		BeanDao aqlBeanDao = new BeanDao(SerialNumber.class);
		SerialNumber tempSna;
		for( int i = 0, n = getSerialNumberListBeans().size(); i < n; i++ ) {
			SerialNumberListBean tempSnaListBean = getSerialNumberListBeans().get( i );
			aqlBeanDao.setWhereCriteria("bean.id = " + tempSnaListBean.getId());
			SerialNumber parentSna = (SerialNumber) aqlBeanDao.getFirstBeanResult();

			for (IncludedProduct tempIncludedProduct : scannedProducts.keySet()) {
				for( int j = 0, p = tempIncludedProduct.getQuantity(); j < p; j++ ) {
					tempSna = scannedProducts.get( tempIncludedProduct ).get( (p * i) + j );
					tempSna.setSerialNumberOwner(parentSna);
					tempSna.saveDetails();
					tempSna.updateStockQuantity();
					tempSna.saveDetails();
				}
			}

			for (IncludedProductGroup tempIncludedProductGroup : scannedProductGroups.keySet()) {
				for( int j = 0, p = tempIncludedProductGroup.getQuantity(); j < p; j++ ) {
					tempSna = scannedProductGroups.get( tempIncludedProductGroup ).get( (p * i) + j );
					tempSna.setSerialNumberOwner(parentSna);
					tempSna.saveDetails();
					tempSna.updateStockQuantity();
					tempSna.saveDetails();
				}
			}
		}


		setReadyForPrinting(true);
	}

	public int getNumberOfScanned(IncludedProduct includedProduct) {
		return scannedProducts.get(includedProduct).size();
	}

	public int getNumberOfScanned(IncludedProductGroup includedProductGroup) {
		return scannedProductGroups.get(includedProductGroup).size();
	}

	public String getScannedQuantityColour( IncludedProduct includedProduct ) {
		List<SerialNumber> assignmentList = scannedProducts.get(includedProduct);
		if (assignmentList.size() == 0) {
			return "red";
		} else if (assignmentList.size() == (getQuantity() * includedProduct.getQuantity())) {
			return "green";
		} else {
			return "orange";
		}
	}

	public String getScannedQuantityColour( IncludedProductGroup includedProductGroup ) {
		List<SerialNumber> assignmentList = scannedProductGroups.get(includedProductGroup);
		if (assignmentList.size() == 0) {
			return "red";
		} else if (assignmentList.size() == (getQuantity() * includedProductGroup.getQuantity())) {
			return "green";
		} else {
			return "orange";
		}
	}

	public void setRealizedProduct(RealizedProduct realizedProduct) {
		this.setSelectedRealizedProduct(realizedProduct);
	}

	public RealizedProduct getRealizedProduct() {
		return getSelectedRealizedProduct();
	}

	public void setScannedSerialNumber(String scannedSerialNumber) {
		this.scannedSerialNumber = scannedSerialNumber;
	}

	public String getScannedSerialNumber() {
		return scannedSerialNumber;
	}

	public void setSelectedProductVersion(ProductVersion selectedProductVersion) {
		this.selectedProductVersion = selectedProductVersion;
	}

	public ProductVersion getSelectedProductVersion() {
		return selectedProductVersion;
	}

	public void setAllSerialNumbersSelected(boolean allSerialNumbersSelected) {
		this.allSerialNumbersSelected = allSerialNumbersSelected;
	}

	public boolean isAllSerialNumbersSelected() {
		return allSerialNumbersSelected;
	}

	public void setSerialNumberListBeans(
			List<SerialNumberListBean> serialNumberListBeans) {
		this.serialNumberListBeans = serialNumberListBeans;
	}

	public List<SerialNumberListBean> getSerialNumberListBeans() {
		return serialNumberListBeans;
	}

	public void setReadyForPrinting(boolean isReadyForPrinting) {
		this.isReadyForPrinting = isReadyForPrinting;
	}

	public boolean isReadyForPrinting() {
		return isReadyForPrinting;
	}

	public RealizedProduct getSelectedRealizedProduct() {
		return selectedRealizedProduct;
	}

	public void setSelectedRealizedProduct(RealizedProduct selectedRealizedProduct) {
		this.selectedRealizedProduct = selectedRealizedProduct;
	}

	public RealizedProduct getSelectedRealizedProductListBean() {
		return selectedRealizedProductListBean;
	}

	public void setSelectedRealizedProductListBean(
			RealizedProduct selectedRealizedProductListBean) {
		this.selectedRealizedProductListBean = selectedRealizedProductListBean;
	}

}
