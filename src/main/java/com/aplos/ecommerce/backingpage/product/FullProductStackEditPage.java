package com.aplos.ecommerce.backingpage.product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.beans.AplosAbstractBean;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.enums.CommonBundleKey;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.IncludedProduct;
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.beans.RealizedProduct.RealizedProductImageKey;
import com.aplos.ecommerce.beans.SitewideDiscount;
import com.aplos.ecommerce.beans.amazon.AmazonBrowseNode;
import com.aplos.ecommerce.beans.product.ProductBrand;
import com.aplos.ecommerce.beans.product.ProductColour;
import com.aplos.ecommerce.beans.product.ProductInfo;
import com.aplos.ecommerce.beans.product.ProductSize;
import com.aplos.ecommerce.beans.product.ProductSizeCategory;
import com.aplos.ecommerce.beans.product.ProductSizeChart;
import com.aplos.ecommerce.beans.product.ProductSizeType;
import com.aplos.ecommerce.beans.product.ProductType;
import com.aplos.ecommerce.interfaces.RealizedProductRetriever;
import com.aplos.ecommerce.module.EcommerceConfiguration;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=ProductInfo.class)
public class FullProductStackEditPage extends EditPage {

	private static final long serialVersionUID = -1672208461273977567L;

	//realized product data
	private BigDecimal price = null;
	private BigDecimal productCost = null;
	private BigDecimal crossoutPrice;

	//interface data
	private ProductType selectedProductType;
	private ProductSizeType selectedSizeType;
	private ProductBrand includedProductBrand;
	private IncludedProduct selectedIncludedProduct;
	private String newBulletPoint;
	private String newSearchKeyword;
	private ProductBrand optionalAccessoryProductBrand;
	private ProductInfo optionalAccessoryProduct;
	private ProductColour selectedColour;
	private boolean sizesWithNoCategoriesExist=true;

	//caching
	private List<RealizedProduct> realizedProductList = null;
	private List<ProductColour> colourList = new ArrayList<ProductColour>();
	private List<ProductSize> sizeList = new ArrayList<ProductSize>();
	private List<ProductSizeCategory> categoryList = new ArrayList<ProductSizeCategory>();
	private final Map<ProductSizeType,List<ProductSize>> sizesCache = new HashMap<ProductSizeType,List<ProductSize>>();
	private final Map<ProductSizeType,List<ProductSizeCategory>> categoriesCache = new HashMap<ProductSizeType,List<ProductSizeCategory>>();
	private SelectItem[] includedProductSelectItemBeans = null;
	private SelectItem[] optionalAccessorySelectItemBeans = null;
	private ProductInfo currentProductInfo;
	private AmazonBrowseNode selectedAmazonBrowseNode;

	//lightbox uploader data
	private Map<ProductColour, RealizedProduct> lightboxColourMap = new HashMap<ProductColour, RealizedProduct>();

	
	@Override
	public boolean responsePageLoad() {
		boolean isContinuingLoad = super.responsePageLoad();
		if( currentProductInfo == null ) {
			currentProductInfo = JSFUtil.getBeanFromScope( ProductInfo.class ).getSaveableBean();
			updateIncludedProductsSelectItemBeans();
			updateOptionalAccessoriesSelectItemBeans();
			updateRealizedProductList();
			currentProductInfo.setKitItem(false);
		}
		return isContinuingLoad;
	}

	@Override
	public void applyBtnAction() {
		if (saveAction()) {
			JSFUtil.addMessage(ApplicationUtil.getAplosContextListener().translateByKey( CommonBundleKey.SAVED_SUCCESSFULLY ),FacesMessage.SEVERITY_INFO);
		}
	}

	@Override
	public void cancelBtnAction() {
		JSFUtil.redirect(FullProductStackListPage.class);
	}

	@Override
	public void okBtnAction() {
		if (saveAction()) {
			//Don't change this until it's fixed, I've added this note because I've had to change it back twice now
			//it doesn't work, there's no setListPageNavigation and it shouldn't go from edit page to incorrect edit page
			//return getAssociatedBeanFromTabSession().getEditPageNavigation();
			JSFUtil.redirect(FullProductStackListPage.class);
		}
	}

	public boolean saveAction() {
		if (currentProductInfo.getProduct().getProductTypes() == null || currentProductInfo.getProduct().getProductTypes().size() < 1) {
			JSFUtil.addMessageForError("Sorry, you cannot save a product without any product types set.");
			return false;
		} else if (currentProductInfo.getProduct().getAdditionalProductSizeTypes() == null || currentProductInfo.getProduct().getAdditionalProductSizeTypes().size() < 1) {
			JSFUtil.addMessageForError("Sorry, you cannot save a product without any size types set.");
			return false;
		} else if (sizeList.size() < 1) {
			JSFUtil.addMessageForError("This product will not show on the frontend - there are no sizes set for the size types selected.");
		} else if (colourList.size() < 1) {
			JSFUtil.addMessageForError("This product will not show on the frontend - there are no colours selected.");
		}

		//TODO: Temporary Hack
		if (realizedProductList.size() < 1) {
			currentProductInfo.setPrice(price);
			currentProductInfo.setProductCost(productCost);
			currentProductInfo.setCrossoutPrice(crossoutPrice);
		}
		//TODO: End Temporary Hack

		currentProductInfo.saveDetails();
		boolean usingColours = EcommerceConfiguration.getEcommerceSettingsStatic().isUsingProductColours();

		//save new realized product quantities
		for (RealizedProduct rProd : realizedProductList) {
			if( rProd.isActive() ) {
				rProd.setCrossoutPrice(crossoutPrice);
				if ((rProd.getProductColour() != null &&
					!colourList.contains(rProd.getProductColour())) || //deactivate if colour/size removed
					(rProd.getProductSize() != null &&
					!sizeList.contains(rProd.getProductSize())) ||
					(rProd.getProductSizeCategory() != null &&
					!categoryList.contains(rProd.getProductSizeCategory()))) {
					rProd.setActive(false);
				}
				if (rProd.isGiftVoucher()) {
					rProd.setActive(true); //this is needed for the gift voucher screen
				}
				rProd.setPrice(price);
				rProd.setProductCost(getProductCost());
				rProd.setCustomisable(false);
				rProd.setDiscontinued(false);
				rProd.setHiddenFromCompanyContact(false);
				rProd.setHiddenFromCustomer(false);
				rProd.setSerialNumberRequired(false);
				rProd.setItemCode(null);
				rProd.setProductInfo(currentProductInfo);
				rProd.setVoidStickerRequired(false);
				//update the images this product displays
				if( !usingColours ) {
					rProd.setProductColour( EcommerceConfiguration.getEcommerceConfiguration().getStandardProductColour() );
				}
				List<FileDetails> rpidList = lightboxColourMap.get(rProd.getProductColour()).getImageDetailsList();
				if (rpidList != null) {
					for( FileDetails rpid : rpidList ) {
						rpid.setFileDetailsOwner(rProd);
						rpid.saveDetails();
						rProd.getFileDetailsOwnerHelper().setFileDetails( rpid, rpid.getFileDetailsKey(), null );
					}

					setDefaultImageDetails(rProd);
				}
				rProd.saveDetails();
			}
		}
		currentProductInfo.takeNewDefaultRealizedProduct();
		currentProductInfo.saveDetails();
		updateSitewideDiscounts();
		return true;
	}
	
	public void setDefaultImageDetails( RealizedProduct rProd ) {
		rProd.setDefaultImageDetails(rProd.getImageDetailsByKey(RealizedProductImageKey.SMALL_IMAGE));		
	}
	

	public void updateSitewideDiscounts() {
		BeanDao discountDao = new BeanDao(SitewideDiscount.class);
		discountDao.setSelectCriteria("bean.discountPercentage, bean.productBrand.id, bean.productType.id");
		List<SitewideDiscount> discounts = discountDao.getAll();
		ProductType giftVoucherProductType = EcommerceConfiguration.getEcommerceConfiguration().getGiftVoucherProductType();
		for (RealizedProduct realizedProduct : realizedProductList) {
			boolean canTakeDiscount = true;
			if (realizedProduct.getProductInfo() != null && realizedProduct.getProductInfo().getProduct() != null && realizedProduct.getProductInfo().getProduct().getProductTypes() != null) {
				for (ProductType type : realizedProduct.getProductInfo().getProduct().getProductTypes()) {
					if (type.equals(giftVoucherProductType)) {
						canTakeDiscount=false;
					}
				}
			}
			if (canTakeDiscount) {
				realizedProduct.takeSitewideDiscount(discounts);
				realizedProduct.saveDetails();
			}
		}
	}

	public String getQuantity() {
		RealizedProduct rp = getGridRealizedProduct();
		if (rp != null) {
			int quant = rp.getQuantity();
			if (quant > 0) {
				return String.valueOf(quant);
			} else if (quant==0) {
				if (rp.getStockAvailableFromDate() != null) {
					//we want to correctly show the negative backend
					return String.valueOf(rp.getWeeksUntilStock() * -1);
				} else {
					return "0";
				}
			}
		}
		return "X";
	}

	public void setQuantity(String quantity) {
		int qty = 0;
		if (CommonUtil.validateNumeric(quantity,"-")) {
			qty = Integer.parseInt(quantity);
		} else {
			quantity = quantity.toUpperCase();
		}
		RealizedProduct rp = getGridRealizedProduct();
		if (rp != null) {
			if (quantity.equals("X")) {
				rp.setQuantity(-1);
				//TODO: im not sure why we were disabling these, it doesn't seem correct now
				//rp.setActive(false);
				return;
			}
			if (qty >= 0) {
				rp.setQuantity(qty);
				rp.setStockAvailableFromDate( null );
			} else {
				rp.setWeeksUntilStock(qty);
			}
		}
	}

	public RealizedProduct getGridRealizedProduct() {
		ProductColour productColour = (ProductColour) JSFUtil.getRequest().getAttribute("colour");
		ProductSize productSize = (ProductSize) JSFUtil.getRequest().getAttribute("size");
		ProductSizeCategory productSizeCategory = (ProductSizeCategory) JSFUtil.getRequest().getAttribute("category");
		return getGridRealizedProduct(productColour, productSize, productSizeCategory);
	}

	protected RealizedProduct getGridRealizedProduct(ProductColour productColour, ProductSize productSize, ProductSizeCategory productSizeCategory) {
		boolean usingColours = EcommerceConfiguration.getEcommerceSettingsStatic().isUsingProductColours();
		boolean usingSizes = EcommerceConfiguration.getEcommerceSettingsStatic().isUsingProductSizes();
		if( (usingColours && productColour == null) || (usingSizes && productSize == null) ) {
			/*
			 *  We need to do this as the ui:repeat will call it's childrens values before setting
			 *  the var variable, this sends through null values for the colour and size and
			 *  can end up creating unwanted realizedProducts.
			 */
			return null;
		}
		for (RealizedProduct realizedProduct : realizedProductList) {
			if ((!usingColours || (realizedProduct.getProductColour() != null && realizedProduct.getProductColour().equals(productColour))) &&
				(!usingSizes || (realizedProduct.getProductSize() != null && realizedProduct.getProductSize().equals(productSize)))) {
				
				if ((!usingSizes || (productSizeCategory == null && realizedProduct.getProductSizeCategory() == null)) ||
					(productSizeCategory != null && productSizeCategory.equals(realizedProduct.getProductSizeCategory()))) {
					if (realizedProduct.getQuantity() >= 0) {
						//X is <0 we leave it inactive so we can tell its an X
						realizedProduct.setActive(true);
					}
					return realizedProduct;
				}
			}
		}
		RealizedProduct realizedProduct = new RealizedProduct().initialiseNewBean();
		realizedProduct.setProductColour(productColour);
		realizedProduct.setProductSize(productSize);
		realizedProduct.setProductSizeCategory(productSizeCategory);
		//realizedProduct.setActive(true);
		realizedProductList.add(realizedProduct);
		return realizedProduct;
	}

	public void updateSizes() {
		sizeList = new ArrayList<ProductSize>();
		categoryList = new ArrayList<ProductSizeCategory>();
		//this returns all size types from added product types plus any additional size types added
		sizesWithNoCategoriesExist=false;
		//dont use productInfo.getProduct().getProductSizeTypes() because that also fetches the sizes on
		//each product type but we only want that to be a default, we only care about 'additional' size types
		for (ProductSizeType sizeType : currentProductInfo.getProduct().getAdditionalProductSizeTypes()) {
			List<ProductSize> sizesForType = sizesCache.get(sizeType);
			if (sizesForType != null) {
				ProductSize.sortByPosition(sizesForType);
			}
			List<ProductSizeCategory> categoriesForType = categoriesCache.get(sizeType);
			if (categoriesForType != null) {
				ProductSizeCategory.sortByPosition(categoriesForType);
			}
			if (sizesForType == null) {
				//fetch sizes
				BeanDao sizeDao = new BeanDao(ProductSize.class);
				sizeDao.setWhereCriteria("bean.productSizeType.id=" + sizeType.getId());
				sizesForType = sizeDao.setIsReturningActiveBeans(true).getAll();
//				if (sizesForType != null) {
//					HibernateUtil.initialiseList(sizesForType, true);
//				}
				sizesCache.put(sizeType, sizesForType);
				//fetch categories
				BeanDao categoryDao = new BeanDao(ProductSizeCategory.class);
				categoryDao.setWhereCriteria("bean.productSizeType.id=" + sizeType.getId());
				categoriesForType = categoryDao.setIsReturningActiveBeans(true).getAll();
//				if (categoriesForType != null) {
//					HibernateUtil.initialiseList(categoriesForType, true);
//				}
				categoriesCache.put(sizeType, categoriesForType);
				//this is going to still have inactive sizes attached
				int inactiveCount=0;
				for (ProductSizeCategory cat : categoriesForType) {
					if (cat.isActive()) {
						if (!categoryList.contains(cat)) {
							categoryList.add(cat);
						}
					} else {
						inactiveCount++;
					}
				}
				if ((categoriesForType.size() - inactiveCount) < 1) {
					sizesWithNoCategoriesExist=true;
				}
			}
			for (ProductSize size : sizesForType) {
				if (!sizeList.contains(size)) {
					sizeList.add(size);
				}
			}
		}
		ProductSize.sortByPosition(sizeList,true);
		ProductSizeCategory.sortByPosition(categoryList,true);
	}
	
	public SelectItem[] getAmazonBrowseNodeSelectItems() {
		BeanDao amazonBrowseNodesDao = new BeanDao( AmazonBrowseNode.class ); 
		amazonBrowseNodesDao.setWhereCriteria( "bean.isAParentNode = false" );
		List<AmazonBrowseNode> amazonBrowseNodes = amazonBrowseNodesDao.getAll();
		return AplosBean.getSelectItemBeans( amazonBrowseNodes );
	}

	public String addAmazonBrowseNode(){
		if (getSelectedAmazonBrowseNode() != null) {
			if (currentProductInfo != null) {
				currentProductInfo.getAmazonBrowseNodes().add(getSelectedAmazonBrowseNode());
			}
			updateSizes();
		} else {
			JSFUtil.addMessageForError("Please select the amazon browse node you wish to add.");
		}
		return null;
	}

	public String removeAmazonBrowseNode() {
		AmazonBrowseNode amazonBrowseNode = (AmazonBrowseNode) JSFUtil.getRequest().getAttribute("tableBean");
		if (amazonBrowseNode != null) {
			if (currentProductInfo != null) {
				currentProductInfo.getAmazonBrowseNodes().remove(amazonBrowseNode);
			}
		}
		return null;
	}

	public String addProductTypeItem(){
		if (selectedProductType != null) {
			if (currentProductInfo != null) {
				ProductType loadedProductType = new BeanDao(ProductType.class).get(selectedProductType.getId());
				currentProductInfo.getProduct().addProductType(loadedProductType);
				if (loadedProductType.getProductSizeType() != null) {
					currentProductInfo.getProduct().addAdditionalSizeType(loadedProductType.getProductSizeType());
				}
			}
			updateSizes();
		} else {
			JSFUtil.addMessageForError("Please select the product type you wish to add.");
		}
		return null;
	}

	public String removeProductTypeItem() {
		ProductType type = (ProductType) JSFUtil.getRequest().getAttribute("tableBean");
		if (type != null) {
			if (currentProductInfo != null) {
				currentProductInfo.getProduct().getProductTypes().remove(type);
			}
		}
		return null;
	}

	public List<ProductSizeCategory> getProductSizeCategories() {
		ProductSize size = (ProductSize) JSFUtil.getRequest().getAttribute("size");
		List<ProductSizeCategory> categories = null;
		if (size != null) {
			categories = categoriesCache.get(size.getProductSizeType());
		}
		if (categories == null) {
			categories = new ArrayList<ProductSizeCategory>();
		}
		return categories;
	}

	public int getActiveCategoriesCount() {
		ProductSize size = (ProductSize) JSFUtil.getRequest().getAttribute("size");
		int count = 0;
		if (size != null) {
			List<ProductSizeCategory> categoriesForType = categoriesCache.get(size.getProductSizeType());
			if (categoriesForType != null) {
				for (ProductSizeCategory cat : categoriesForType) {
					if (cat.isActive()) {
						count ++;
					}
				}
			}
		}
		return count;
	}

	public void updateColours() {
		colourList = new ArrayList<ProductColour>();
		for (RealizedProduct tempRealizedProduct : realizedProductList) {
			if ( tempRealizedProduct.isActive() && !colourList.contains(tempRealizedProduct.getProductColour())) {
				colourList.add(tempRealizedProduct.getProductColour());
				if (getLightboxColourMap().get(tempRealizedProduct.getProductColour()) == null) {
					RealizedProduct imageHoldingRealizedProduct = new RealizedProduct().initialiseNewBean();
					imageHoldingRealizedProduct.getFileDetailsOwnerHelper().setFileDetails( tempRealizedProduct.getImageDetailsByKey(RealizedProductImageKey.SWATCH_IMAGE), RealizedProductImageKey.SWATCH_IMAGE.name(), null );
					imageHoldingRealizedProduct.getFileDetailsOwnerHelper().setFileDetails( tempRealizedProduct.getImageDetailsByKey(RealizedProductImageKey.SMALL_IMAGE), RealizedProductImageKey.SMALL_IMAGE.name(), null );
					imageHoldingRealizedProduct.getFileDetailsOwnerHelper().setFileDetails( tempRealizedProduct.getImageDetailsByKey(RealizedProductImageKey.LARGE_IMAGE), RealizedProductImageKey.LARGE_IMAGE.name(), null );
					imageHoldingRealizedProduct.getFileDetailsOwnerHelper().setFileDetails( tempRealizedProduct.getImageDetailsByKey(RealizedProductImageKey.DETAIL_IMAGE_1), RealizedProductImageKey.DETAIL_IMAGE_1.name(), null );
					imageHoldingRealizedProduct.getFileDetailsOwnerHelper().setFileDetails( tempRealizedProduct.getImageDetailsByKey(RealizedProductImageKey.DETAIL_IMAGE_2), RealizedProductImageKey.DETAIL_IMAGE_2.name(), null );
					imageHoldingRealizedProduct.getFileDetailsOwnerHelper().setFileDetails( tempRealizedProduct.getImageDetailsByKey(RealizedProductImageKey.DETAIL_IMAGE_3), RealizedProductImageKey.DETAIL_IMAGE_3.name(), null );
					imageHoldingRealizedProduct.getFileDetailsOwnerHelper().setFileDetails( tempRealizedProduct.getImageDetailsByKey(RealizedProductImageKey.DETAIL_IMAGE_4), RealizedProductImageKey.DETAIL_IMAGE_4.name(), null );
					getLightboxColourMap().put(tempRealizedProduct.getProductColour(), imageHoldingRealizedProduct);
				}
			}
			// if we display it in grid we used to reactivate
			// so that when we save only the ones in the grid
			// are active. no need to deactivate now as we do the checks when we save
			//tempRealizedProduct.setActive(false);
		}
		Collections.sort(colourList);
	}

	public void updateRealizedProductList() {
		realizedProductList = new ArrayList<RealizedProduct>();
		if (!currentProductInfo.isNew()) {
			BeanDao rpDao = new BeanDao(RealizedProduct.class);
			rpDao.setWhereCriteria("bean.productInfo.id=" + currentProductInfo.getId());
			realizedProductList = rpDao.getAll(); // we dont just want active here
			
			CommonUtil.changeListToSaveableBeans( realizedProductList );
			for (RealizedProduct rp : realizedProductList) {
				//the quantity check is to load in our not stocked X's rather than lose the square on the grid
				if (rp.isActive() || rp.getQuantity() < 0) {

					if (price == null) {
						//because we are not new, so we need to get the previous
						//information that was saved to the realized products
						price = rp.getPrice();
						productCost = rp.getProductCost();
						crossoutPrice = rp.getCrossoutPrice();
						break;
					}
				}
			}

			//TODO: Temporary Hack
			if (realizedProductList.size() < 1 || price == null) {
				price = currentProductInfo.getPrice();
				setProductCost( currentProductInfo.getProductCost() );
				crossoutPrice = currentProductInfo.getCrossoutPrice();
			}
			//TODO: End Temporary Hack
		}
		updateSizesAndColours();
	}

	public void updateSizesAndColours() {
		updateSizes();
		updateColours();
	}

	public void setColourList(List<ProductColour> colourList) {
		this.colourList = colourList;
	}

	public List<ProductColour> getColourList() {
		return colourList;
	}

	public void setRealizedProductList(List<RealizedProduct> realizedProductList) {
		this.realizedProductList = realizedProductList;
	}

	public List<RealizedProduct> getRealizedProductList() {
		return realizedProductList;
	}

	public void setOptionalAccessoryProduct(ProductInfo optionalAccessoryProduct) {
		this.optionalAccessoryProduct = optionalAccessoryProduct;
	}

	public ProductInfo getOptionalAccessoryProduct() {
		return optionalAccessoryProduct;
	}

	public void setIncludedProductBrand(ProductBrand includedProductBrand) {
		this.includedProductBrand = includedProductBrand;
	}

	public ProductBrand getIncludedProductBrand() {
		return includedProductBrand;
	}

	public void setSelectedSizeType(ProductSizeType selectedSizeType) {
		this.selectedSizeType = selectedSizeType;
	}

	public ProductSizeType getSelectedSizeType() {
		return selectedSizeType;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setSelectedIncludedProduct(IncludedProduct selectedIncludedProduct) {
		this.selectedIncludedProduct = selectedIncludedProduct;
	}

	public IncludedProduct getSelectedIncludedProduct() {
		return selectedIncludedProduct;
	}

	public void setNewBulletPoint(String newBulletPoint) {
		this.newBulletPoint = newBulletPoint;
	}

	public String getNewBulletPoint() {
		return newBulletPoint;
	}

	public void setCrossoutPrice(BigDecimal crossoutPrice) {
		this.crossoutPrice = crossoutPrice;
	}

	public BigDecimal getCrossoutPrice() {
		return crossoutPrice;
	}

	public void setSelectedProductType(ProductType selectedProductType) {
		this.selectedProductType = selectedProductType;
	}

	public ProductType getSelectedProductType() {
		return selectedProductType;
	}

	public void setOptionalAccessoryProductBrand(ProductBrand optionalAccessoryProductBrand) {
		this.optionalAccessoryProductBrand = optionalAccessoryProductBrand;
	}

	public ProductBrand getOptionalAccessoryProductBrand() {
		return optionalAccessoryProductBrand;
	}

	public void redirectToEditPage(ProductInfo tableBean) {
		BeanDao dao = new BeanDao(ProductInfo.class);
		ProductInfo loadedInfo = dao.get(tableBean.getId());
//		HibernateUtil.initialise( loadedInfo, true );
		loadedInfo.addToScope();
		JSFUtil.redirect(this.getClass());
	}

	public String removeColour() {
		ProductColour colour = (ProductColour) JSFUtil.getRequest().getAttribute("colour");
		if( colour != null ) {
			getColourList().remove( colour );
		}
		return null;
	}

	public String addColour() {
		if( selectedColour != null ) {
			if( getColourList().contains( selectedColour ) ) {
				JSFUtil.addMessage( "This colour selected is already included in the list" );
			} else {
				getColourList().add( selectedColour );
				getLightboxColourMap().put(selectedColour, new RealizedProduct().<RealizedProduct>initialiseNewBean() );
			}
		} else {
			JSFUtil.addMessage( "Please select a colour to add to the list" );
		}
		return null;
	}

	public String addSizeTypeItem(){
		if (selectedSizeType != null) {
			if (currentProductInfo != null) {
				currentProductInfo.getProduct().addAdditionalSizeType(selectedSizeType);
				updateSizes();
			}
		} else {
			JSFUtil.addMessageForError("Please select the product type you wish to add.");
		}
		return null;
	}

	public String removeSizeTypeItem() {
		ProductSizeType type = (ProductSizeType) JSFUtil.getRequest().getAttribute("tableBean");
		if (type != null) {
			if (currentProductInfo != null) {
				currentProductInfo.getProduct().getAdditionalProductSizeTypes().remove(type);
				updateSizes();
			}
		}
		return null;
	}

	public String addSearchKeyword() {
		if ( CommonUtil.isNullOrEmpty( getNewSearchKeyword() ) ) {
			JSFUtil.addMessageForError("Please enter valid text for this searchKeyword");
		} else {
			if (currentProductInfo != null) {
				currentProductInfo.addSearchKeyword(getNewSearchKeyword());
			}
		}
		return null;
	}

	public String removeSearchKeyword() {
		String searchKeyword = (String) JSFUtil.getRequest().getAttribute("tableBean");
		if (currentProductInfo != null) {
			currentProductInfo.getSearchKeywordList().remove(searchKeyword);
		}
		return null;
	}

	public String addBulletPoint() {
		if ( CommonUtil.getStringOrEmpty( getNewBulletPoint() ).equals("") ) {
			JSFUtil.addMessageForError("Please enter valid text for this bullet point");
		} else {
			if (currentProductInfo != null) {
				currentProductInfo.addBulletPoint(getNewBulletPoint());
			}
		}
		return null;
	}

	public String removeBulletPoint() {
		String bullet = (String) JSFUtil.getRequest().getAttribute("tableBean");
		if (currentProductInfo != null) {
			currentProductInfo.getBulletPointList().remove(bullet);
		}
		return null;
	}

	public void addSelectedIncludedProduct() {
		if( selectedIncludedProduct != null ) {
			if (currentProductInfo.getIncludedProducts() == null) {
				currentProductInfo.setIncludedProducts(new ArrayList<IncludedProduct>());
			}
			if( currentProductInfo.getIncludedProducts().contains( selectedIncludedProduct ) ) {
				JSFUtil.addMessage( "This product is already included in the list" );
			} else {
				currentProductInfo.getIncludedProducts().add( selectedIncludedProduct );
			}
		} else {
			JSFUtil.addMessage( "Please select a product to add to the list" );
		}
	}

	public void removeSelectedIncludedProduct() {
		//RealizedProductRetriever accessoryItem = (RealizedProductRetriever) JSFUtil.getRequest().getAttribute( "tableBean" );
		IncludedProduct accessoryItem = (IncludedProduct) JSFUtil.getRequest().getAttribute( "tableBean" );
		currentProductInfo.getIncludedProducts().remove( accessoryItem );
	}

	public void updateIncludedProductsSelectItemBeans(AjaxBehaviorEvent event) {
		updateIncludedProductsSelectItemBeans();
	}

	public void updateIncludedProductsSelectItemBeans() {
		BeanDao dao = new BeanDao(ProductInfo.class);
		dao.setSelectCriteria("bean.product.name, bean.id, bean.active");
		if (includedProductBrand != null) {
			dao.setWhereCriteria("bean.product.productBrand.id=" + includedProductBrand.getId());
		}
		List<ProductInfo> productInfos = dao.getAll();
		SelectItem[] items = new SelectItem[productInfos.size() +1];
		items[0] = new SelectItem(null,"Not Selected");
		for (int i=1; i <= productInfos.size(); i++) {
			ProductInfo productInfo = productInfos.get(i-1);
			IncludedProduct includer = new IncludedProduct();
			includer.setRealizedProductRetriever(productInfo);
			includer.setQuantity(1);
			items[i] = new SelectItem( includer , productInfo.getDisplayName());
		}
		setIncludedProductSelectItemBeans(items);
	}

	public void setIncludedProductSelectItemBeans(SelectItem[] includedProductSelectItemBeans) {
		this.includedProductSelectItemBeans = includedProductSelectItemBeans;
	}

	public SelectItem[] getIncludedProductSelectItemBeans() {
		return includedProductSelectItemBeans;
	}

	public void updateOptionalAccessoriesSelectItemBeans() {
		BeanDao dao = new BeanDao(ProductInfo.class);
		dao.setSelectCriteria("bean.product.name, bean.id, bean.active");
		if (optionalAccessoryProductBrand != null) {
			dao.setWhereCriteria("bean.product.productBrand.id=" + optionalAccessoryProductBrand.getId());
		}

		List<ProductInfo> productInfos = dao.getAll();
		SelectItem[] items = new SelectItem[productInfos.size() +1];
		items[0] = new SelectItem(null,"Not Selected");
		for (int i=1; i <= productInfos.size(); i++) {
			ProductInfo productInfo = productInfos.get(i-1);
			items[i] = new SelectItem(productInfo, productInfo.getDisplayName());
		}
		setOptionalAccessorySelectItemBeans(items);
	}

	public void addOptionalAccessoryItem() {
		if( optionalAccessoryProduct != null ) {
			if( currentProductInfo.getOptionalAccessoriesList().contains( optionalAccessoryProduct ) ) {
				JSFUtil.addMessage( "This accessory is already included in the list" );
			} else {
				currentProductInfo.getOptionalAccessoriesList().add( optionalAccessoryProduct );
			}
		} else {
			JSFUtil.addMessage( "Please select a accessory to add to the list" );
		}
	}

	public void removeOptionalAccessory() {
		RealizedProductRetriever accessoryItem = (RealizedProductRetriever) JSFUtil.getRequest().getAttribute( "tableBean" );
		currentProductInfo.getOptionalAccessoriesList().remove( accessoryItem );
	}

	public void setOptionalAccessorySelectItemBeans(
			SelectItem[] optionalAccessorySelectItemBeans) {
		this.optionalAccessorySelectItemBeans = optionalAccessorySelectItemBeans;
	}

	public SelectItem[] getOptionalAccessorySelectItemBeans() {
		return optionalAccessorySelectItemBeans;
	}

	public void setSelectedColour(ProductColour selectedColour) {
		this.selectedColour = selectedColour;
	}

	public ProductColour getSelectedColour() {
		return selectedColour;
	}

	public void setSizeList(List<ProductSize> sizeList) {
		this.sizeList = sizeList;
	}

	public List<ProductSize> getSizeList() {
		return sizeList;
	}

	public void setLightboxColourMap(Map<ProductColour, RealizedProduct> lightboxColourMap) {
		this.lightboxColourMap = lightboxColourMap;
	}

	public Map<ProductColour, RealizedProduct> getLightboxColourMap() {
		return lightboxColourMap;
	}

	public void setCategoryList(List<ProductSizeCategory> categoryList) {
		this.categoryList = categoryList;
	}

	public List<ProductSizeCategory> getCategoryList() {
		return categoryList;
	}

	public void setSizesWithNoCategoriesExist(boolean sizesWithNoCategoriesExist) {
		this.sizesWithNoCategoriesExist = sizesWithNoCategoriesExist;
	}

	public boolean isSizesWithNoCategoriesExist() {
		return sizesWithNoCategoriesExist;
	}

	public SelectItem[] getProductSizeChartSelectItemBeansWithNotSelected() {
		BeanDao dao = new BeanDao(ProductSizeChart.class);
		return AplosAbstractBean.getSelectItemBeansWithNotSelected(dao.setIsReturningActiveBeans(true).getAll(), "Not Selected");
	}

	protected ProductInfo getCurrentProductInfo() {
		return currentProductInfo;
	}

	public BigDecimal getProductCost() {
		return productCost;
	}

	public void setProductCost(BigDecimal productCost) {
		this.productCost = productCost;
	}



	public AmazonBrowseNode getSelectedAmazonBrowseNode() {
		return selectedAmazonBrowseNode;
	}



	public void setSelectedAmazonBrowseNode(AmazonBrowseNode selectedAmazonBrowseNode) {
		this.selectedAmazonBrowseNode = selectedAmazonBrowseNode;
	}

	public String getNewSearchKeyword() {
		return newSearchKeyword;
	}

	public void setNewSearchKeyword(String newSearchKeyword) {
		this.newSearchKeyword = newSearchKeyword;
	}

}
