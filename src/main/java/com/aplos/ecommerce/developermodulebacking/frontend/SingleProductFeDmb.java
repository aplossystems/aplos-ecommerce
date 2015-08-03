package com.aplos.ecommerce.developermodulebacking.frontend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.aplos.cms.CmsPageUrl;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.AplosUrl;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosAbstractBean;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.enums.CartAbandonmentIssue;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.ErrorEmailSender;
import com.aplos.common.utils.JSFUtil;
import com.aplos.common.utils.XmlEntityUtil;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.EcommerceShoppingCart;
import com.aplos.ecommerce.beans.IncludedProduct;
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.beans.product.ProductColour;
import com.aplos.ecommerce.beans.product.ProductInfo;
import com.aplos.ecommerce.beans.product.ProductSize;
import com.aplos.ecommerce.beans.product.ProductSizeCategory;
import com.aplos.ecommerce.beans.product.ProductSizeType;
import com.aplos.ecommerce.beans.product.ProductType;
import com.aplos.ecommerce.interfaces.RealizedProductRetriever;
import com.aplos.ecommerce.module.EcommerceConfiguration;
import com.aplos.ecommerce.utils.EcommerceUtil;

@ManagedBean
@ViewScoped //changed from session
public class SingleProductFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = -7103223883095571132L;
	private ProductColour productColourSelection;
	private ProductSize productSizeSelection;
	private FileDetails currentImageDetails;
	private RealizedProduct currentRealizedProduct;
	private boolean isProductVisibleWhenOutOfStock;
	private List<InfoTab> infoTabList;
	private boolean isShowingSizeChart;
	//these attributes are used by the quantity grid
	private List<RealizedProduct> realizedProductList = null;
	private List<ProductColour> colourList = new ArrayList<ProductColour>();
	private List<ProductSize> sizeList = new ArrayList<ProductSize>();
	private List<ProductSizeCategory> categoryList = new ArrayList<ProductSizeCategory>();
	private final Map<ProductSizeType,List<ProductSize>> sizesCache = new HashMap<ProductSizeType,List<ProductSize>>();
	private final Map<ProductInfo,Map<ProductColour,RealizedProduct>> productForColourCacheMap = new HashMap<ProductInfo,Map<ProductColour,RealizedProduct>>();
	private final Map<ProductSizeType,List<ProductSizeCategory>> categoriesCache = new HashMap<ProductSizeType,List<ProductSizeCategory>>();
	private boolean sizesWithNoCategoriesExist=true;
	private List<RealizedProduct> itemsInHand = new ArrayList<RealizedProduct>();

	//these values are related to kit items (multiple grids) they are updated as we loop through the included product grids
	private final Map<IncludedProduct,ArrayList<RealizedProduct>> kitItemRealizedProductListMap = new HashMap<IncludedProduct,ArrayList<RealizedProduct>>();
	private final Map<IncludedProduct,ArrayList<ProductColour>> kitItemColourListMap = new HashMap<IncludedProduct,ArrayList<ProductColour>>();
	private final Map<IncludedProduct,ArrayList<ProductSize>> kitItemSizeListMap = new HashMap<IncludedProduct,ArrayList<ProductSize>>();
	private final Map<IncludedProduct,ArrayList<ProductSizeCategory>> kitItemCategoryListMap = new HashMap<IncludedProduct,ArrayList<ProductSizeCategory>>();
	private final Map<IncludedProduct,Boolean> kitItemSizesWithNoCategoriesExistMap = new HashMap<IncludedProduct,Boolean>();
	private final Map<IncludedProduct,ArrayList<RealizedProduct>> kitItemRealizedProductsInHandMap = new HashMap<IncludedProduct,ArrayList<RealizedProduct>>();
	private String refererUrl;

	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		super.responsePageLoad(developerCmsAtom);
		if( getCurrentRealizedProduct() == null || !getCurrentRealizedProduct().equals( JSFUtil.getBeanFromScope(RealizedProduct.class ) ) ) {
			if( CommonUtil.isNullOrEmpty( getRefererUrl() ) ) {
				setRefererUrl(JSFUtil.getRequest().getHeader( "referer" ));
			}
			String rpidParam = JSFUtil.getRequestParameter("rpid");
			Long rpid = null;
			if (rpidParam != null) {
				rpid = Long.parseLong(rpidParam);
			}
			RealizedProduct tempRealizedProduct;
			if (rpid == null) {
				tempRealizedProduct = JSFUtil.getBeanFromScope(RealizedProduct.class );
			} else {
				tempRealizedProduct = JSFUtil.getBeanFromScope(RealizedProduct.class );
				if( tempRealizedProduct == null ) {
					tempRealizedProduct = new BeanDao( RealizedProduct.class ).get( rpid );
				}
			}
			/*
			 * Although the product is fully initialised in the EcommercePageDispatcher this was causing
			 * issues for kit products in bigmatts as the included products had lazy initialisation 
			 * issues.
			 */
//			if( tempRealizedProduct == null || !tempRealizedProduct.isFullyInitialised() ) {
//			}
			if( tempRealizedProduct == null ) {
				JSFUtil.redirect( new AplosUrl( "/" ) );
				JSFUtil.addMessage( "There's no valid product to show for this page, it may no longer be stocked" );
				return false;
			}
			checkOptionalAccessoriesForDuplicates(tempRealizedProduct);
			tempRealizedProduct.addToScope();
			setCurrentRealizedProduct(tempRealizedProduct);
			tempRealizedProduct.addToScope();
			productColourSelection = getCurrentRealizedProduct().getProductColour();
			productSizeSelection = getCurrentRealizedProduct().getProductSize();
			currentImageDetails = getCurrentRealizedProduct().getDefaultImageDetails();
		}
		if (realizedProductList == null) {
			updateRealizedProductList();
		}
		isProductVisibleWhenOutOfStock = EcommerceConfiguration.getEcommerceConfiguration().isProductVisibleWhenOutOfStock();
		isShowingSizeChart = true;
		return true;
	}
	
	public void redirectToRefererUrl() {
		JSFUtil.redirect( getRefererUrl(), false );
	}
	
	public RealizedProduct getGridRealizedProduct2() {
		return getGridRealizedProduct();
	}
	
	public RealizedProduct getProductForColour() {
		return getGridRealizedProduct(null); 
	}
	
	public RealizedProduct getGridRealizedProduct() {
		ProductSize productSize = (ProductSize) JSFUtil.getRequest().getAttribute("size");
		ProductColour colour = (ProductColour) JSFUtil.getRequest().getAttribute("colour");
		ProductSizeCategory category = (ProductSizeCategory) JSFUtil.getRequest().getAttribute("category");
		return getGridRealizedProduct( productSize, colour, category );
	}
	
	public RealizedProduct getGridRealizedProduct( ProductSize productSize ) {
		ProductColour colour = (ProductColour) JSFUtil.getRequest().getAttribute("colour");
		ProductSizeCategory category = (ProductSizeCategory) JSFUtil.getRequest().getAttribute("category");
		return getGridRealizedProduct( productSize, colour, category );
	}
	
	public RealizedProduct getGridRealizedProduct( ProductSize productSize, ProductColour productColour, ProductSizeCategory productSizeCategory ) {
		List<RealizedProduct> tempRealizedProductList;
		RealizedProduct baseRealizedProduct;
		if (currentRealizedProduct.getProductInfo().isKitItem() && JSFUtil.getRequest().getAttribute("kitItem") != null) {
			baseRealizedProduct = ((IncludedProduct) JSFUtil.getRequest().getAttribute("kitItem")).getRealizedProductRetriever().retrieveRealizedProduct(null);
			tempRealizedProductList = getKitItemRealizedProductList();
		} else {
			baseRealizedProduct = currentRealizedProduct;
			tempRealizedProductList = realizedProductList;
		}
		if( tempRealizedProductList != null ) {
			for (RealizedProduct realized : tempRealizedProductList) {
				if (realized.getProductColour().equals(productColour) &&
					(productSize == null || realized.getProductSize().equals(productSize))) { //size null is for thumbs below main img
					if( productSize == null ) {
						return productForColourCacheMap.get(baseRealizedProduct.getProductInfo()).get(productColour);
					}
					if ((productSizeCategory == null && realized.getProductSizeCategory() == null) ||
						(productSizeCategory != null && productSizeCategory.equals(realized.getProductSizeCategory()))) {
						return realized;
					}
				}
			}
		}
		return null; //we shouldn't be able to reach here, unlike backend
	}
	
	public List<RealizedProductRetriever> getSortedOptionalAccessories() {
		List<RealizedProductRetriever> sortedOptionalAccessories = new ArrayList<RealizedProductRetriever>( getCurrentRealizedProduct().getProductInfo().getOptionalAccessoriesList() );
		for( int i = sortedOptionalAccessories.size() - 1; i >= 0; i-- ) {
			if( sortedOptionalAccessories.get( i ).retrieveRealizedProduct(null).isDiscontinued() ) {
				sortedOptionalAccessories.remove( i );
			}
		}
		Collections.sort( sortedOptionalAccessories, new Comparator<RealizedProductRetriever>() {
			@Override
			public int compare(RealizedProductRetriever o1,
					RealizedProductRetriever o2) {
				if( o1.retrieveRealizedProduct(null) == null || o1.retrieveRealizedProduct(null).determineItemCode() == null ) {
					return -1;
				} else if(o2.retrieveRealizedProduct(null) == null || o2.retrieveRealizedProduct(null).determineItemCode() == null ) {
					return 1;
				} else {
					return o1.retrieveRealizedProduct(null).determineItemCode().compareTo( o2.retrieveRealizedProduct(null).determineItemCode() );
				}
				
			}
		});
		return sortedOptionalAccessories;
	}
	
	public List<RealizedProductRetriever> getSortedRealizedIncludedProducts( RealizedProduct realizedProduct ) {
		List<RealizedProductRetriever> sortedOptionalAccessories = new ArrayList<RealizedProductRetriever>( getCurrentRealizedProduct().getProductInfo().getRealizedIncludedProducts(realizedProduct) );
		Collections.sort( sortedOptionalAccessories, new Comparator<RealizedProductRetriever>() {
			@Override
			public int compare(RealizedProductRetriever o1,
					RealizedProductRetriever o2) {
				if( o1.retrieveRealizedProduct(null) == null || o1.retrieveRealizedProduct(null).determineItemCode() == null ) {
					return -1;
				} else if(o2.retrieveRealizedProduct(null) == null || o2.retrieveRealizedProduct(null).determineItemCode() == null ) {
					return 1;
				} else {
					return o1.retrieveRealizedProduct(null).determineItemCode().compareTo( o2.retrieveRealizedProduct(null).determineItemCode() );
				}
				
			}
		});
		return sortedOptionalAccessories;
	}
	
	public void removeCategoryFromRequest() {
		JSFUtil.getRequest().setAttribute("category",null);
	}
	
	public void removeSizeFromRequest() {
		JSFUtil.getRequest().setAttribute("size",null);
	}

	public List<ProductSize> getKitItemSizeList() {
		IncludedProduct kitItem = (IncludedProduct) JSFUtil.getRequest().getAttribute("kitItem");
		List<ProductSize> tempList = kitItemSizeListMap.get(kitItem);
		if (tempList == null) {
			updateKitItemRealizedProductListMap(kitItem);
			return kitItemSizeListMap.get(kitItem);
		} else {
			return tempList;
		}
	}

	public String getStrippedTagsDescription() {
		return XmlEntityUtil.stripTags(getCurrentRealizedProduct().getProductInfo().getLongDescription());
	}

	public String goToKitItem() {
		IncludedProduct kitItem = (IncludedProduct) JSFUtil.getRequest().getAttribute("kitItem");
		if (kitItem != null) {
			RealizedProduct realized = kitItem.getRealizedProductRetriever().retrieveRealizedProduct(null);
			if (realized != null) {
				String brandMapping = realized.getProductInfo().getProduct().getProductBrand().getMapping();
				if (brandMapping != null && !brandMapping.equals("")) {
					brandMapping = "/" + brandMapping;
				} else if (brandMapping == null) {
					brandMapping="";
				}
				String typeMapping = "individual";
				for (ProductType type : realized.getProductInfo().getProduct().getProductTypes()) {
					if (type.getMapping() != null && !type.getMapping().equals("")) {
						typeMapping = type.getMapping();
						break;
					}
				}
				JSFUtil.redirect(new CmsPageUrl(brandMapping + "/" + typeMapping + "/" + realized.getProductInfo().getMappingOrId() + ".aplos", "type=product"), true);
			}
		}
		return null;
	}

	public List<ProductColour> getKitItemColourList() {
		IncludedProduct kitItem = (IncludedProduct) JSFUtil.getRequest().getAttribute("kitItem");
		return kitItemColourListMap.get(kitItem);
	}

	public List<ProductSizeCategory> getKitItemCategoryList() {
		IncludedProduct kitItem = (IncludedProduct) JSFUtil.getRequest().getAttribute("kitItem");
		return kitItemCategoryListMap.get(kitItem);
	}

	public Boolean getKitItemSizesWithNoCategoriesExist() {
		IncludedProduct kitItem = (IncludedProduct) JSFUtil.getRequest().getAttribute("kitItem");
		return kitItemSizesWithNoCategoriesExistMap.get(kitItem);
	}

	private List<RealizedProduct> getKitItemRealizedProductList() {
		IncludedProduct kitItem = (IncludedProduct) JSFUtil.getRequest().getAttribute("kitItem");
		if (!kitItemRealizedProductListMap.containsKey(kitItem)) {
			updateKitItemRealizedProductListMap(kitItem);
		}
		return kitItemRealizedProductListMap.get(kitItem);
	}

	private void updateKitItemRealizedProductListMap(IncludedProduct kitItem) {
		if( kitItem == null ) {
			return;
		}
		ProductInfo productInfo;
		if (kitItem.getRealizedProductRetriever() instanceof ProductInfo) {
			IncludedProduct loadedKitItem = new BeanDao(IncludedProduct.class).get(kitItem.getId());
			productInfo = (ProductInfo) loadedKitItem.getRealizedProductRetriever();
		} else {
			productInfo = kitItem.getRealizedProductRetriever().retrieveRealizedProduct(currentRealizedProduct).getProductInfo();
		}

		BeanDao rpDao = new BeanDao(RealizedProduct.class);
		rpDao.addQueryTable( "psc", "bean.productSizeCategory" );
		rpDao.setWhereCriteria("bean.productInfo.id=" + productInfo.getId());
		rpDao.addWhereCriteria("bean.productSize.active = 1");
		rpDao.addWhereCriteria("bean.productColour.active = 1");
		rpDao.addWhereCriteria("(psc IS NULL OR psc.active = 1)");
		@SuppressWarnings("unchecked")
		ArrayList<RealizedProduct> kitItemRealizedProductList = (ArrayList<RealizedProduct>) rpDao.setIsReturningActiveBeans(true).getAll();
//		if (kitItemRealizedProductList != null) {
//			for (RealizedProduct rp : kitItemRealizedProductList) {
//				HibernateUtil.initialiseList(rp.getImageDetailsList(), true);
//			}
//		}
		kitItemRealizedProductListMap.put(kitItem, kitItemRealizedProductList);
		//get our existing colours
		ArrayList<ProductColour> kitItemColourlist = new ArrayList<ProductColour>();
		for (RealizedProduct rp : kitItemRealizedProductList) {
			if (!kitItemColourlist.contains(rp.getProductColour())) {
				Map<ProductColour, RealizedProduct> colourForProductMap = productForColourCacheMap.get(rp.getProductInfo()); 
				if( colourForProductMap == null ) {
					colourForProductMap = new HashMap<ProductColour,RealizedProduct>();
					productForColourCacheMap.put( rp.getProductInfo(), colourForProductMap);
				}
				colourForProductMap.put(rp.getProductColour(),rp);
				kitItemColourlist.add(rp.getProductColour());
			}
		}
		Collections.sort(kitItemColourlist);
		kitItemColourListMap.put(kitItem, kitItemColourlist);
		updateKitItemSizes(productInfo, kitItem);
	}

	@SuppressWarnings("unchecked")
	public void updateKitItemSizes(ProductInfo productInfo, IncludedProduct kitItem) {
		ArrayList<ProductSize> kitItemSizeList = new ArrayList<ProductSize>();
		ArrayList<ProductSizeCategory> kitItemCategoryList = new ArrayList<ProductSizeCategory>();
		//this returns all size types from added product types plus any additional size types added
		boolean tempSizesWithNoCategoriesExist=false;
		for (ProductSizeType sizeType : productInfo.getProduct().getAdditionalProductSizeTypes()) {
//			sizeType = new AqlBeanDao(ProductSizeType.class).get(sizeType.getId());
//			sizeType.hibernateInitialise( true ); //so we can get the categories
			int inactiveCount=0;
			//we should be able to use the same cache without interfering
			List<ProductSize> sizesForType = sizesCache.get(sizeType);
			if (sizesForType == null) {
				BeanDao dao = new BeanDao(ProductSize.class);
				dao.setWhereCriteria("bean.productSizeType.id=" + sizeType.getId());
				dao.setOrderBy( "positionIdx" );
				sizesForType = dao.setIsReturningActiveBeans(true).getAll();
				sizesCache.put(sizeType, sizesForType);
			}
			List<ProductSizeCategory> categoriesForType = categoriesCache.get(sizeType);
			if (categoriesForType == null) {
				BeanDao dao = new BeanDao(ProductSizeCategory.class);
				dao.setWhereCriteria("bean.productSizeType.id=" + sizeType.getId());
				dao.setOrderBy( "positionIdx" );
				categoriesForType = dao.setIsReturningActiveBeans(true).getAll();
				categoriesCache.put(sizeType, categoriesForType);
			}
			for (ProductSizeCategory cat : categoriesForType) {
				if (cat.isActive()) {
					if (!kitItemCategoryList.contains(cat)) {
						kitItemCategoryList.add(cat);
					}
				} else {
					inactiveCount++;
				}
			}
			//TODO: check this - it seems like lt should be gt
			if ((categoriesForType.size() - inactiveCount) < 1) {
				tempSizesWithNoCategoriesExist=true;
			}
			kitItemCategoryListMap.put(kitItem, kitItemCategoryList);
			kitItemSizesWithNoCategoriesExistMap.put(kitItem, tempSizesWithNoCategoriesExist);
			for (ProductSize size : sizesForType) {
				if (!kitItemSizeList.contains(size)) {
					kitItemSizeList.add(size);
				}
			}
			ProductSize.sortByPosition(kitItemSizeList,true);
			kitItemSizeListMap.put(kitItem, kitItemSizeList);
		}
	}

	public String getGridStyleClass() {
		RealizedProduct rp = getGridRealizedProduct();
		if (rp != null) {
			String basicClass;
			if (rp.getQuantity() < 1) {
				basicClass = "aplos-wait-period";
			} else if (rp.isLowStock()) {
				basicClass = "aplos-low-stock";
			} else {
				basicClass = "aplos-in-stock";
			}
			if (itemsInHand.contains(rp)) {
				return basicClass + " aplos-active";
			} else {
				return basicClass;
			}
		}
		return "aplos-error";
	}

	public String getSwatchTooltip() {
		RealizedProduct rp = getGridRealizedProduct();
		String tip = "";
		if (rp != null) {
			tip = rp.getDisplayName();
			if (rp.getStockAvailableFromDate() != null) {
				tip += " [Available in " + rp.getWeeksUntilStock() + " weeks]";
			} else if (rp.getQuantity() < 1) {
				tip += " [Out of stock]";
			} else if (rp.isLowStock()) {
				tip += " [Low stock]";
			} else {
				tip += " [In stock]";
			}
		}
		return tip;
	}

	public String toggleInHand() {
		RealizedProduct rp = getGridRealizedProduct();
		IncludedProduct kitItem = (IncludedProduct) JSFUtil.getRequest().getAttribute("kitItem");
		if (kitItem != null) {

			//we need to add between 1 and <kitItem.getQuantity()> items from every grid
			//we keep track of what we've added via kitItemRealizedProductsInHand, we will check
			//we have the correct quantities when we submit via addHandToCart();
			ArrayList<RealizedProduct> kitItemRealizedProductsInHand = kitItemRealizedProductsInHandMap.get(kitItem);
			if (kitItemRealizedProductsInHand == null) {
				kitItemRealizedProductsInHand = new ArrayList<RealizedProduct>();
			}

			if (rp != null) {
				if (kitItemRealizedProductsInHand.contains(rp)) {
					kitItemRealizedProductsInHand.remove(rp);
					itemsInHand.remove(rp);
				} else {
					kitItemRealizedProductsInHand.add(rp);
					itemsInHand.add(rp);
				}
			}

			kitItemRealizedProductsInHandMap.put(kitItem, kitItemRealizedProductsInHand);

		} else {

			if (rp != null) {
				if (itemsInHand.contains(rp)) {
					itemsInHand.remove(rp);
				} else {
					itemsInHand.add(rp);
				}
			}

		}
		return null;
	}

	private String generateOutOfStockMessage(RealizedProduct product) {
		EcommerceUtil.getEcommerceUtil().addAbandonmentIssueToCart( CartAbandonmentIssue.PRODUCT_OUT_OF_STOCK );
		String catStr = "";
		if (product.getProductSizeCategory() != null) {
			catStr = " " + product.getProductSizeCategory().getName();
		}
		
		StringBuffer messageBuf = new StringBuffer( product.getProductInfo().getProduct().getName() );
		if( EcommerceConfiguration.getEcommerceSettingsStatic().isUsingProductColours() || EcommerceConfiguration.getEcommerceSettingsStatic().isUsingProductSizes() ) {
			messageBuf.append( " in " );
			if( EcommerceConfiguration.getEcommerceSettingsStatic().isUsingProductSizes() ) {
				messageBuf.append( product.getProductSize().getName() ).append( catStr );
				if( EcommerceConfiguration.getEcommerceSettingsStatic().isUsingProductColours() ) {
					messageBuf.append( ", " );
				}
			}
			if( EcommerceConfiguration.getEcommerceSettingsStatic().isUsingProductColours() ) {
				messageBuf.append( product.getProductColour().getName() );
			}
		}
		messageBuf.append( " does not have enough stock to place this order" );
		return messageBuf.toString();
	}

	public boolean getIsAddToBagDisabled() {
		if (realizedProductList == null || realizedProductList.size() == 1) {
			//if we only have one square on the grid and click add, we will automatically
			//try to add that square, its more intuitive
			return false;
		}
		return itemsInHand == null || itemsInHand.size() < 1;
	}

	public String addHandToCart() {
		//TODO: break this up, its too long now
		boolean validForAdd = true;
		List<RealizedProduct> kitItems = null;
		if (currentRealizedProduct.getProductInfo().isKitItem()) { //kit item
			//prepare a list of our selected kit items
			kitItems = new ArrayList<RealizedProduct>();
			//ensure we add between 1 and <kitItem.getQuantity()> items from every grid
			for (IncludedProduct includedProduct : currentRealizedProduct.getProductInfo().getIncludedProducts()) {
				if (kitItemRealizedProductsInHandMap.containsKey(includedProduct)) {
					//check quantity added is within the limits
					List<RealizedProduct> productsIncluded = kitItemRealizedProductsInHandMap.get(includedProduct);
					int maxQuantity = includedProduct.getQuantity();
					if (maxQuantity == 0) { maxQuantity=1; }
					if (productsIncluded == null || productsIncluded.size() == 0 || productsIncluded.size() > maxQuantity) { //>= 1 && productsIncluded.size() <= maxQuantity)) {
						validForAdd=false;
					} else {
						if (productsIncluded.size() < maxQuantity) {
							kitItems.addAll(productsIncluded);
							//we have less than the correct count but as the grid wont let us select multiple quantities
							//we assume they meant to add the right quantity of the selected products so we add that many
							//if one item/part is out of stock  we have to stop the entire 'transaction'
							int difference = maxQuantity - productsIncluded.size() ;
							for (int i=0; i < difference; i++) {
								kitItems.add(productsIncluded.get(0));
							}
						} else { //we have the correct count
							for( int i = 0, n = productsIncluded.size(); i < n; i++ ) {
								kitItems.add(productsIncluded.get(i));
							}
						}
					}
				} else {
					//if we only have one square on the grid add automatically
					List<RealizedProduct> availableProducts = kitItemRealizedProductListMap.get(includedProduct);
					if (availableProducts != null && availableProducts.size() == 1) {
						kitItems.add(availableProducts.get(0));
					} else {
						validForAdd=false;
					}
				}
			}
		} else { // not a kit item
			if (itemsInHand.size() < 1) {
				if (realizedProductList.size() == 1) {
					//if we only have one square on the grid and click add, automatically
					//try to add that square, its more intuitive
					itemsInHand.add(realizedProductList.get(0));
				} else {
					validForAdd=false;
				}
			}
		}
		if (validForAdd) {
			if (currentRealizedProduct.getProductInfo().isKitItem()) {
				//stock check the kit items here
				for (RealizedProduct product : kitItems) {
					if ( product.getQuantity() < 1 &&
						 !EcommerceConfiguration.getEcommerceSettingsStatic().isOutOfStockProductAllowedOnOrder() &&
						 !(product.getStockAvailableFromDate() != null &&
						   EcommerceConfiguration.getEcommerceSettingsStatic().isPreOrderAllowedOnOrder())) {
						validForAdd = false;
						JSFUtil.addMessageForError(generateOutOfStockMessage(product));
					}
				}
				if (validForAdd) {
					//add to bag has to add the parent (kit) and the selected children as a list
					//so because we had the items in hand so that we could highlight the grid correctly,
					//we need to reset it to only have the kit (we've kept the items separately)
					itemsInHand = new ArrayList<RealizedProduct>();
					itemsInHand.add(currentRealizedProduct);
				}
			}
			if (validForAdd) {
				for (RealizedProduct productInHand : itemsInHand) {
					if (currentRealizedProduct.getProductInfo().isKitItem() || productInHand.getQuantity() > 0 ||
						EcommerceConfiguration.getEcommerceSettingsStatic().isOutOfStockProductAllowedOnOrder() ||
						(productInHand.getStockAvailableFromDate() != null &&
						EcommerceConfiguration.getEcommerceSettingsStatic().isPreOrderAllowedOnOrder())) {
						EcommerceShoppingCart ecommerceShoppingCart = EcommerceUtil.getEcommerceUtil().getOrCreateEcommerceShoppingCart();
						//the true at the end here records that we have just added this item
						//so the 'added to bag' flash can display its info
						ecommerceShoppingCart.addToCart( productInHand, true, kitItems, true );
						ecommerceShoppingCart.saveDetails();
						ecommerceShoppingCart.refreshShoppingCartItemReferences();
					} else {
						JSFUtil.addMessageForError(generateOutOfStockMessage(productInHand));
					}
				}
				itemsInHand = new ArrayList<RealizedProduct>(); // clear our hands, we've put it all in the basket
			}
		} else {
			if (currentRealizedProduct.getProductInfo().isKitItem()) {
				StringBuffer errorMessage = new StringBuffer("Please correct your selected quantities. You must select ");
				for (int i=0; i < currentRealizedProduct.getProductInfo().getIncludedProducts().size(); i++) {
					IncludedProduct includedProduct = currentRealizedProduct.getProductInfo().getIncludedProducts().get(i);
					int quantity = includedProduct.getQuantity();
					if (quantity == 0) { quantity=1; }
					if (i != 0) {
						if (i == currentRealizedProduct.getProductInfo().getIncludedProducts().size()-1) {
							errorMessage.append(" and ");
						} else {
							errorMessage.append(", ");
						}
					}
					errorMessage.append(quantity);
					errorMessage.append(" ");
					errorMessage.append(includedProduct.getRealizedProductRetriever().retrieveRealizedProduct(currentRealizedProduct).getProductInfo().getProduct().getDisplayName());
					if (i == currentRealizedProduct.getProductInfo().getIncludedProducts().size()-1) {
						errorMessage.append(".");
					}
				}
				JSFUtil.addMessageForError(errorMessage.toString());
			} else {
				JSFUtil.addMessageForError("Please select the items to add to the " + EcommerceConfiguration.getEcommerceConfiguration().getCartDisplayName());
			}
		}
		return null;
	}

	public int getActiveCategoriesCount() {
		ProductSize size = (ProductSize) JSFUtil.getRequest().getAttribute("size");
		int count = 0;
		if (size != null) {
			List<ProductSizeCategory> sizeTypeCategories = categoriesCache.get(size.getProductSizeType());
			for (ProductSizeCategory cat : sizeTypeCategories) {
				if (cat.isActive()) {
					count ++;
				}
			}
		}
		return count;
	}

	@SuppressWarnings("unchecked")
	public void updateSizes() {
		sizeList = new ArrayList<ProductSize>();
		categoryList = new ArrayList<ProductSizeCategory>();
		ProductInfo productInfo = ((RealizedProduct) JSFUtil.getBeanFromScope(RealizedProduct.class)).getProductInfo();
		//this returns all size types from added product types plus any additional size types added
		sizesWithNoCategoriesExist=false;
		for (ProductSizeType sizeType : productInfo.getProduct().getAdditionalProductSizeTypes()) {
//			sizeType = new AqlBeanDao(ProductSizeType.class).get(sizeType.getId());
//			sizeType.hibernateInitialise( true ); //so we can get the categories
			List<ProductSize> sizesForType = sizesCache.get(sizeType);
			List<ProductSizeCategory> categoriesForType = categoriesCache.get(sizeType);
			if (sizesForType == null) {
				BeanDao dao = new BeanDao(ProductSize.class);
				dao.setWhereCriteria("bean.productSizeType.id=" + sizeType.getId());
				sizesForType = dao.setIsReturningActiveBeans(true).getAll();
				if (sizesForType != null) {
					ProductSize.sortByPosition(sizesForType);
				}
				sizesCache.put(sizeType, sizesForType);
				BeanDao categoryDao = new BeanDao(ProductSizeCategory.class);
				categoryDao.setWhereCriteria("bean.productSizeType.id=" + sizeType.getId());
				categoriesForType = categoryDao.setIsReturningActiveBeans(true).getAll();
				if (categoriesForType != null) {
					ProductSizeCategory.sortByPosition(categoriesForType);
				}
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
		// Remove sizes from the start of the list if there's no stock for that size
		outerFor : for( int i = 0, n = sizeList.size(); i < n; i++ ) {
			for( ProductColour tempColour : getColourList() ) {
				if( getCategoryList().size() == 0 ) {
					if( getGridRealizedProduct( sizeList.get( 0 ), tempColour, null ) != null && getGridRealizedProduct( sizeList.get( 0 ), tempColour, null ).getQuantity() > 0 ) {
						break outerFor;
					}
				} else {
					for( ProductSizeCategory tempProductSizeCategory : getCategoryList() ) {
						if( getGridRealizedProduct( sizeList.get( 0 ), tempColour, tempProductSizeCategory ) != null && getGridRealizedProduct( sizeList.get( 0 ), tempColour, tempProductSizeCategory ).getQuantity() > 0 ) {
							break outerFor;
						}
					}
				}
			}
			sizeList.remove( 0 );
		}
		outerFor : for( int i = sizeList.size() - 1; i > -1; i-- ) {
			for( ProductColour tempColour : getColourList() ) {
				if( getCategoryList().size() == 0 ) {
					if( getGridRealizedProduct( sizeList.get( i ), tempColour, null ) != null && getGridRealizedProduct( sizeList.get( i ), tempColour, null ).getQuantity() > 0 ) {
						break outerFor;
					}
				} else {
					for( ProductSizeCategory tempProductSizeCategory : getCategoryList() ) {
						if( getGridRealizedProduct( sizeList.get( i ), tempColour, tempProductSizeCategory ) != null && getGridRealizedProduct( sizeList.get( i ), tempColour, tempProductSizeCategory ).getQuantity() > 0 ) {
							break outerFor;
						}
					}
				}
			}
			sizeList.remove( i );
		}
		ProductSizeCategory.sortByPosition(categoryList,true);
	}

	@SuppressWarnings("unchecked")
	public void updateRealizedProductList() {
		RealizedProduct realizedProduct = JSFUtil.getBeanFromScope(RealizedProduct.class);
		BeanDao rpDao = new BeanDao(RealizedProduct.class);
		//rpDao.setOptimisedSearch(true); // we need bean.productSizeType.productSizeCategories (list)
		//rpDao.setSelectCriteria("bean.id, bean.productColour, bean.productSizeType");
		rpDao.setWhereCriteria("pi.id=" + realizedProduct.getProductInfo().getId());
		rpDao.addWhereCriteria("ps.active = 1");
		rpDao.addQueryTable( "pi", "bean.productInfo" );
		rpDao.addQueryTable( "ps", "bean.productSize" );
		rpDao.addQueryTable( "psc", "bean.productSizeCategory" );
		rpDao.addWhereCriteria("(psc IS NULL OR psc.active = 1)");
		realizedProductList = rpDao.setIsReturningActiveBeans(true).getAll();
		/*
		 * This was commented out because it was making bigmatts really slow
		 * and doesn't appear to be required.
		 */
//		if (realizedProductList != null) {
//			//we have to do this for the image list :/
//			//HibernateUtil.initialiseList(realizedProductList);
//			for (RealizedProduct rp : realizedProductList) {
//				HibernateUtil.initialiseList(rp.getImageDetailsList(), true);
//				HibernateUtil.initialiseList(rp.getProductInfo().getIncludedProducts(), true);
//			}
//		}

		if( EcommerceConfiguration.getEcommerceSettingsStatic().isUsingProductSizes() ) {
			//get our existing colours
			for (RealizedProduct rp : realizedProductList) {
				if (!colourList.contains(rp.getProductColour())) {
					Map<ProductColour, RealizedProduct> colourForProductMap = productForColourCacheMap.get(rp.getProductInfo()); 
					if( colourForProductMap == null ) {
						colourForProductMap = new HashMap<ProductColour,RealizedProduct>();
						productForColourCacheMap.put( rp.getProductInfo(), colourForProductMap);
					}
					colourForProductMap.put(rp.getProductColour(),rp);
					colourList.add(rp.getProductColour());
				}
			}
			Collections.sort(colourList);
		}
		if( EcommerceConfiguration.getEcommerceSettingsStatic().isUsingProductSizes() ) {
			updateSizes();
		}
	}
	


	public void checkOptionalAccessoriesForDuplicates( RealizedProduct realizedProduct ) {
		boolean duplicateRemoved = false;
		Map<Long, Integer> realizedProductRetrieverIdMap = new HashMap<Long,Integer>();
		List<RealizedProductRetriever> optionalAccessoriesList = realizedProduct.getProductInfo().getOptionalAccessoriesList(); 
		for( int i = optionalAccessoriesList.size() - 1; i >= 0; i-- ) {
			if( realizedProductRetrieverIdMap.get( optionalAccessoriesList.get( i ).getId() ) != null ) {
				realizedProductRetrieverIdMap.put( optionalAccessoriesList.get( i ).getId(), realizedProductRetrieverIdMap.get( optionalAccessoriesList.get( i ).getId() ) + 1 );
				optionalAccessoriesList.remove( i );
				duplicateRemoved = true;
			} else {
				realizedProductRetrieverIdMap.put( optionalAccessoriesList.get( i ).getId(), 1 );
			}
		}

		if( duplicateRemoved ) {
			realizedProduct.getProductInfo().saveDetails();
			ErrorEmailSender.sendErrorEmail( JSFUtil.getRequest(), ApplicationUtil.getAplosContextListener(), new Exception() );
		}
	}

	public FileDetails getDefaultImageDetails() {
		if (currentRealizedProduct != null) {
			return currentRealizedProduct.getDefaultImageDetails();
		}
		return null;
	}

	public List<InfoTab> getInfoTabList() {
		return infoTabList;
	}

	public FileDetails getCurrentImageDetails() {
		return currentImageDetails;
	}

	public String getProductMapping(RealizedProduct realizedProduct) {
		return EcommerceUtil.getEcommerceUtil().getProductMapping(realizedProduct);
	}

	public String getCategoryMapping(RealizedProduct realizedProduct) {
		if (realizedProduct.getProductInfo().getProduct().getProductTypes() != null &&
			realizedProduct.getProductInfo().getProduct().getProductTypes().size() > 0) {
			return realizedProduct.getProductInfo().getProduct().getProductTypes().get(0).getMappingOrId();
		} else {
			return realizedProduct.getProductInfo().getProduct().getProductBrand().getMappingOrId();
		}
	}

	public void changeImage() {
		currentImageDetails = (FileDetails) JSFUtil.getRequest().getAttribute( "imageDetails" );
	}

	public boolean isShowingSizeChart() {
		return isShowingSizeChart;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public SelectItem[] getAvailableColours() {
		//Select Colours from realized products which are of this product info type and are in stock
		BeanDao realizedProductDao = new BeanDao( RealizedProduct.class );
		realizedProductDao.setSelectCriteria( "DISTINCT(bean.productColour), bean.quantity" );
		realizedProductDao.setWhereCriteria( "bean.productInfo=" + getCurrentRealizedProduct().getProductInfo().getId() );
		List<Object[]> productColourObjList = realizedProductDao.getAll();
		Set<ProductColour> productColourList = new HashSet<ProductColour>();
		Object productColourObjAry[];
		for( int i = productColourObjList.size() - 1; i >= 0; i-- ) {
			productColourObjAry = productColourObjList.get( i );
			if( isProductVisibleWhenOutOfStock || Integer.parseInt( productColourObjAry[ 1 ].toString() ) > 0 ) {
				productColourList.add( (ProductColour) productColourObjAry[ 0 ] );
			}
		}
		if( productColourList.size() == 0 ) {
			SelectItem selectItems[] = new SelectItem[ 1 ];
			selectItems[ 0 ] = new SelectItem( null, "Sold Out" );
			return selectItems;
		} else {
			return AplosAbstractBean.getSelectItemBeans(CommonUtil.sortAlphabetically(new ArrayList(productColourList)));
		}
	}

	@SuppressWarnings("unchecked")
	public SelectItem[] getAvailableSizes() {
		BeanDao realizedProductDao = new BeanDao( RealizedProduct.class );
		realizedProductDao.setSelectCriteria( "DISTINCT(bean.productSize), bean.quantity" );
		realizedProductDao.addWhereCriteria( "bean.productInfo.id=" + getCurrentRealizedProduct().getProductInfo().getId() );
		if (getProductColourSelection() != null) {
			realizedProductDao.addWhereCriteria( "bean.productColour.id=" + getProductColourSelection().getId() );
		}
		List<Object[]> productSizeObjList = realizedProductDao.getAll();
		Object productSizeObjAry[];
		List<SelectItem> selectItemList = new ArrayList<SelectItem>();
		for( int i = productSizeObjList.size() - 1; i >= 0; i-- ) {
			productSizeObjAry = productSizeObjList.get( i );
			if( Integer.parseInt( productSizeObjAry[ 1 ].toString() ) > 0 ) {
				selectItemList.add( new SelectItem( productSizeObjAry[ 0 ], ((ProductSize) productSizeObjAry[ 0 ]).getDisplayName() ) );
			} else if( isProductVisibleWhenOutOfStock ) {
				selectItemList.add( new SelectItem( productSizeObjAry[ 0 ], ((ProductSize) productSizeObjAry[ 0 ]).getDisplayName() + " Out Of Stock" ) );
			}
		}
		if( selectItemList.size() == 0 ) {
			SelectItem selectItems[] = new SelectItem[ 1 ];
			selectItems[ 0 ] = new SelectItem( null, "Sold Out" );
			return selectItems;
		} else {
			return selectItemList.toArray( new SelectItem[selectItemList.size()] );
		}
	}

	public String getViewAccessoryUrl() {
		RealizedProduct accessory = (RealizedProduct) JSFUtil.getRequest().getAttribute("accessory");
		if (accessory != null) {
			return EcommerceUtil.getEcommerceUtil().getProductMapping( accessory );
		}
		return "/";
	}

	@SuppressWarnings("unchecked")
	public void setProductColourSelection(ProductColour newProductColourSelection) {
		// Need to check first to see if the item is out of stock, with a null productColour
		if( newProductColourSelection != null && newProductColourSelection.getId() != null ) {
			productColourSelection = newProductColourSelection;
			//we need to make sure here that the currently selected
			//size is valid. if its not, pick the first size that comes
			//back. then we should be safe to select a realizedProduct
			long productInfoId = currentRealizedProduct.getProductInfo().getId();
			BeanDao realizedProductDao = new BeanDao( "DISTINCT(bean.productSize)" );
			realizedProductDao.setListBeanClass( ProductSize.class );
			if( !isProductVisibleWhenOutOfStock ) {
				realizedProductDao.addWhereCriteria( "bean.quantity > 0 " );
			}
			realizedProductDao.addWhereCriteria( "bean.productInfo=" + productInfoId + " AND rp.productColour=" + getProductColourSelection().getId() );
			List<ProductSize> productSizes = realizedProductDao.getAll();
			if (!productSizes.contains(productSizeSelection)) {
				productSizeSelection = productSizes.get(0);
			}
		}
	}

	public ProductColour getProductColourSelection() {
		return productColourSelection;
	}

	@SuppressWarnings("unchecked")
	public void setProductSizeSelection(ProductSize newProductSizeSelection) {
		// Need to check first to see if the item is out of stock, with a null productSize
		if( newProductSizeSelection != null && newProductSizeSelection.getId() != null ) {
			productSizeSelection = newProductSizeSelection;
			long productInfoId = getCurrentRealizedProduct().getProductInfo().getId();
			BeanDao productSizeDao = new BeanDao( RealizedProduct.class );
			productSizeDao.setListBeanClass( ProductSize.class );
			productSizeDao.setSelectCriteria( "DISTINCT(bean.productSize)" );
			if( !isProductVisibleWhenOutOfStock ) {
				productSizeDao.addWhereCriteria( "bean.quantity > 0" );
			}
			productSizeDao.addWhereCriteria( "bean.productInfo=" + productInfoId + " AND bean.productColour=" + getProductColourSelection().getId() );

			List<ProductSize> productSizes = productSizeDao.getAll();
			if (!productSizes.contains(productSizeSelection)) {
				productSizeSelection = productSizes.get(0);
			}
		}
	}

	public ProductSize getProductSizeSelection() {
		return productSizeSelection;
	}

	public void updateRealizedProduct() {
		BeanDao realizedProductDao = new BeanDao( RealizedProduct.class ).addWhereCriteria( "bean.productInfo.id = " + getCurrentRealizedProduct().getProductInfo().getId() );
		realizedProductDao.addWhereCriteria( "bean.productColour.id = " + productColourSelection.getId() );
		realizedProductDao.addWhereCriteria( "bean.productSize.id = " + productSizeSelection.getId() + " and active = true" );
		RealizedProduct newRealizedProduct = (RealizedProduct)  realizedProductDao.getFirstResult();
		// This check is needed in case the product is out of stock
		if( newRealizedProduct != null ) {
			setCurrentRealizedProduct(newRealizedProduct);
			getCurrentRealizedProduct().addToScope();
		}

	}

	public String addToCart() {
		if( getCurrentRealizedProduct().getQuantity() > 0 || EcommerceConfiguration.getEcommerceSettingsStatic().isOutOfStockProductAllowedOnOrder()  ||
						(getCurrentRealizedProduct().getStockAvailableFromDate() != null &&
						EcommerceConfiguration.getEcommerceSettingsStatic().isPreOrderAllowedOnOrder())) {
			EcommerceShoppingCart ecommerceShoppingCart = EcommerceUtil.getEcommerceUtil().getOrCreateEcommerceShoppingCart();
			ecommerceShoppingCart.addToCart( getCurrentRealizedProduct(), true );
			// We need to call this as hibernate screws up the bidirectional relationship between
			// the items in the cart and the cart.  It replaces the objects with different instances
			// which need to be replace to the right instance.
			ecommerceShoppingCart.saveDetails();
			ecommerceShoppingCart.refreshShoppingCartItemReferences();
			return CartFeDmb.showCart();
		} else {
			EcommerceUtil.getEcommerceUtil().addAbandonmentIssueToCart( CartAbandonmentIssue.PRODUCT_OUT_OF_STOCK );
			JSFUtil.addMessage( "This product is out stock" );
			return null;
		}
	}

	public String addToCartCalculateStock() {
		if( getCurrentRealizedProduct().calculateStockQuantity() > 0 || EcommerceConfiguration.getEcommerceSettingsStatic().isOutOfStockProductAllowedOnOrder() ||
						(getCurrentRealizedProduct().getStockAvailableFromDate() != null &&
						EcommerceConfiguration.getEcommerceSettingsStatic().isPreOrderAllowedOnOrder())) {
			EcommerceShoppingCart ecommerceShoppingCart = EcommerceUtil.getEcommerceUtil().getOrCreateEcommerceShoppingCart();
			ecommerceShoppingCart.addToCart( getCurrentRealizedProduct(), true );
			// We need to call this as hibernate screws up the bidirectional relationship between
			// the items in the cart and the cart.  It replaces the objects with different instances
			// which need to be replace to the right instance.
			ecommerceShoppingCart.saveDetails();
			ecommerceShoppingCart.refreshShoppingCartItemReferences();

		} else {
			EcommerceUtil.getEcommerceUtil().addAbandonmentIssueToCart( CartAbandonmentIssue.PRODUCT_OUT_OF_STOCK );
			JSFUtil.addMessage( "This product is out stock" );

		}
		return null;
	}

	public String goToCheckout() {
		EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutCartCpr();
		return null;
		//return CartFeDmb.showCart();
	}

	public String addToWishList() {
		EcommerceUtil.getEcommerceUtil().addAbandonmentIssueToCart( CartAbandonmentIssue.ITEMS_MOVED_TO_WISHLIST );
		Customer customer = JSFUtil.getBeanFromScope(Customer.class);
		if (customer == null || !customer.isLoggedIn()) {
			EcommerceConfiguration.getEcommerceCprsStatic().redirectToCustomerSignInCpr();
		} else {
			if (getCurrentRealizedProduct() != null) {

				List<RealizedProduct> wishList = customer.getWishList();
				if (!wishList.contains(getCurrentRealizedProduct())) {
					wishList.add(getCurrentRealizedProduct());
					customer.saveDetails();
					JSFUtil.addMessage("Item has been added to wishlist.", FacesMessage.SEVERITY_INFO);
				} else {
					JSFUtil.addMessage("Item is already in wishlist.", FacesMessage.SEVERITY_INFO);
				}
			}
		}
		return null;
	}

	public void goToEmailAFriend() {
		EcommerceConfiguration.getEcommerceCprsStatic().redirectToEmailAFriendCpr();
	}

	public class InfoTab {
		private String infoTabName;
		private String infoTabContent;

		public InfoTab( String infoTabName, String infoTabContent ) {
			this.infoTabName = infoTabName;
			this.infoTabContent = infoTabContent;
		}

		public void setInfoTabName(String infoTabName) {
			this.infoTabName = infoTabName;
		}
		public String getInfoTabName() {
			return infoTabName;
		}
		public void setInfoTabContent(String infoTabContent) {
			this.infoTabContent = infoTabContent;
		}
		public String getInfoTabContent() {
			return infoTabContent;
		}
	}

//	public String getPolyvoreUrl() {
//		try {
//			StringBuffer polyvoreUrl = new StringBuffer();
//			polyvoreUrl.append("http://www.polyvore.com/cgi/add?");
//			polyvoreUrl.append(URLEncoder.encode("title=","UTF-8"));
//			polyvoreUrl.append(URLEncoder.encode(getCurrentRealizedProduct().getProductInfo().getProduct().getProductBrand().getName(),"UTF-8"));
//			polyvoreUrl.append("%20");
//			polyvoreUrl.append(URLEncoder.encode(getCurrentRealizedProduct().getProductInfo().getProduct().getName(),"UTF-8"));
//			polyvoreUrl.append(URLEncoder.encode("&url=","UTF-8"));
//			polyvoreUrl.append(URLEncoder.encode("http://www.stanwells.com","UTF-8"));
//			polyvoreUrl.append(JSFUtil.getRequest().getContextPath());
//			polyvoreUrl.append(URLEncoder.encode("/","UTF-8"));
//			polyvoreUrl.append(URLEncoder.encode(EcommerceUtil.getEcommerceUtil().getProductMapping(getCurrentRealizedProduct()),"UTF-8"));
//			polyvoreUrl.append(URLEncoder.encode("&imgurl=","UTF-8"));
//			polyvoreUrl.append(URLEncoder.encode("http://www.stanwells.com","UTF-8"));
//			polyvoreUrl.append(currentImageDetails.getFullLargeImageUrl());
//			polyvoreUrl.append(URLEncoder.encode("&amp;maxWidth=400&amp;maxHeight=400","UTF-8"));
//			polyvoreUrl.append(URLEncoder.encode("&desc=","UTF-8"));
//			polyvoreUrl.append(URLEncoder.encode(getCurrentRealizedProduct().getProductInfo().getShortDescription(),"UTF-8"));
//			polyvoreUrl.append(URLEncoder.encode("&price=","UTF-8"));
//			polyvoreUrl.append(getCurrentRealizedProduct().getConvertedPriceStr());
//			polyvoreUrl.append(URLEncoder.encode("&curr=","UTF-8"));
//			Currency currency = (Currency) JSFUtil.getBeanFromScope( Currency.class );
//			polyvoreUrl.append(currency.getSymbol());
//			return polyvoreUrl.toString();
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}

	public void setCurrentRealizedProduct(RealizedProduct currentRealizedProduct) {
		currentRealizedProduct.addToScope(); // need this for popup
		this.currentRealizedProduct = currentRealizedProduct;
	}

	public RealizedProduct getCurrentRealizedProduct() {
		return currentRealizedProduct;
	}

	public void setItemsInHand(List<RealizedProduct> itemsInHand) {
		this.itemsInHand = itemsInHand;
	}

	public List<RealizedProduct> getItemsInHand() {
		return itemsInHand;
	}

	public void setSizesWithNoCategoriesExist(boolean sizesWithNoCategoriesExist) {
		this.sizesWithNoCategoriesExist = sizesWithNoCategoriesExist;
	}

	public boolean isSizesWithNoCategoriesExist() {
		return sizesWithNoCategoriesExist;
	}

	public void setCategoryList(List<ProductSizeCategory> categoryList) {
		this.categoryList = categoryList;
	}

	public List<ProductSizeCategory> getCategoryList() {
		return categoryList;
	}

	public void setSizeList(List<ProductSize> sizeList) {
		this.sizeList = sizeList;
	}

	public List<ProductSize> getSizeList() {
		return sizeList;
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

	public String getFullLargeImageUrl() {
		if (currentRealizedProduct == null) {
			return null;
		}
		FileDetails imageDetails = currentRealizedProduct.getImageDetailsByKey("LARGE_IMAGE");
		if (imageDetails == null) {
			return JSFUtil.getContextPath() + "/images/no-image.png?provideDefaultIfMissing=true";
		} else {
			if (!imageDetails.fileExists()) {
				return JSFUtil.getContextPath() + "/images/missing-image.png?provideDefaultIfMissing=true";
			}
			return imageDetails.getFullFileUrl(true);
		}
	}

	//Methods below here all relate to grid-splitting - they allow us to have multiple tables for the
	//same grid to stop the grid overflowing the content area

	public List<Integer> getTablePages() {
		List<Integer> pagesNeeded = new ArrayList<Integer>();
		for (int i=0; i < getTablePagesNeededCount(); i++) {
			pagesNeeded.add(i);
		}
		return pagesNeeded;
	}

	private int getTablePagesNeededCount() {
		int max = getSizeList().size();
		int needed = ((Double)Math.floor(max/(float)13)).intValue();
		if (max % 13 > 2){
			needed++;
		}
		if (needed < 1) {
			needed=1;
		}
		return needed;
	}


	public List<ProductSize> getPaginatedSizeList() {
		List<ProductSize> originalList = getSizeList();
		List<ProductSize> sizes = new ArrayList<ProductSize>();
		Integer page = (Integer) JSFUtil.getRequest().getAttribute("tablePage");
		int needed = getTablePagesNeededCount();
		if (page != null) {
			int i = page * 13;
			if (page > 0 ) {
				i++;
			}
			for (int j=0; i < originalList.size() && j < 13; i++, j++) {
				sizes.add(originalList.get(i));
			}
			if (page == needed-1) {
				//last page, take anything left over
				while (i < originalList.size()) {
					sizes.add(originalList.get(i));
					i++;
				}
			}
			return sizes;
		}
		return originalList;
	}

	public String getRefererUrl() {
		return refererUrl;
	}

	public void setRefererUrl(String refererUrl) {
		this.refererUrl = refererUrl;
	}

}

