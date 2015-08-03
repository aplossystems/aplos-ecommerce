package com.aplos.ecommerce.developermodulebacking.frontend;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;

import com.aplos.cms.beans.PageScroller;
import com.aplos.cms.beans.PageScroller.PaginationControl;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.cms.enums.SortOption;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.enums.CartAbandonmentIssue;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.EcommerceShoppingCart;
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.beans.developercmsmodules.ProductListModule;
import com.aplos.ecommerce.beans.product.ProductSize;
import com.aplos.ecommerce.beans.product.ProductType;
import com.aplos.ecommerce.interfaces.ProductListRoot;
import com.aplos.ecommerce.module.EcommerceConfiguration;
import com.aplos.ecommerce.utils.EcommerceUtil;

@ManagedBean
@CustomScoped(value="#{ tabSession }")
public class ProductListFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = 6312560110111507480L;
	protected ProductListFilter productListFilter;
	protected PageScroller pagination;
	protected static ProductListModule productListModule;
	private List<RealizedProduct> currentProductList;

	public ProductListFeDmb() {
		this.productListFilter = new ProductListFilter();
	}

	@Override
	public boolean responsePageLoad( DeveloperCmsAtom developerCmsAtom ) {
		super.responsePageLoad( developerCmsAtom );

		if (this.pagination == null) {
			this.pagination = createPageScroller();
		}

		ProductListModule newProductListModule = (ProductListModule) getDeveloperCmsAtom();
		setProductListModule(newProductListModule);

		String previousBaseUrl = productListFilter.getCurrentBaseUrl();
		productListFilter.updateIfRequired( getProductListModule(), JSFUtil.getRequest());
		String oUrl = JSFUtil.getAplosContextOriginalUrl();
		if (getCurrentProductList()==null || !previousBaseUrl.equals(oUrl) ) {
			productListFilter.setCurrentBaseUrl(oUrl);
			this.pagination.setCurrentPage(1);
		}
		int oldCount = productListFilter.getFilteredRealizedProductList().size();
		//if (phaseId.equals(PhaseId.INVOKE_APPLICATION)) {

			//check if our product count has changed without moving base url
			//if it has, assume we have used the filters and reset the page number
			if (oldCount != productListFilter.getFilteredRealizedProductList().size() && previousBaseUrl.equals(oUrl)) {
				this.pagination.setCurrentPage(1);
			}
			getProducts();
		//}
		return true;
	}
	
	public String getProductSizeRange() {
		RealizedProduct realizedProduct = (RealizedProduct) JSFUtil.getRequest().getAttribute( "realProduct" );
		BeanDao productSizeDao = new BeanDao(ProductSize.class);
		//rpDao.setOptimisedSearch(true); // we need bean.productSizeType.productSizeCategories (list)
		//rpDao.setSelectCriteria("bean.id, bean.productColour, bean.productSizeType");
		productSizeDao.setSelectCriteria( "DISTINCT bean" );
		productSizeDao.addQueryTable( "rp", "rp.productSize" );
		productSizeDao.addQueryTable( "pi", "rp.productInfo" );
		productSizeDao.setWhereCriteria("pi.id=" + realizedProduct.getProductInfo().getId());
		productSizeDao.addWhereCriteria("rp.active = 1");
		productSizeDao.addWhereCriteria("rp.quantity > 0");
		List<ProductSize> productSizeList = productSizeDao.getAll();
		ProductSize.sortByPosition(productSizeList,true);

		if( productSizeList.size() > 0 ) {
			StringBuffer productSizeRangeStrBuf = new StringBuffer( productSizeList.get( 0 ).getName() );
			if( productSizeList.size() > 1 ) {	
				productSizeRangeStrBuf.append( " - " + productSizeList.get( productSizeList.size() - 1 ).getName() );
			}
			return productSizeRangeStrBuf.toString();  
		} else {
			return null;
		}
	}

	public List<RealizedProduct> defaultSort(List<RealizedProduct> unsortedList) {
		return unsortedList;
	}

	public String addToCartCalculateStock() {
		RealizedProduct clickedProduct = getRealizedProductFromRequest();
		return addToCartCalculateStock( clickedProduct );
	}

	public String addToCartCalculateStock( RealizedProduct realizedProduct ) {
		RealizedProduct loadedRealizedProduct = new BeanDao( RealizedProduct.class ).get( realizedProduct.getId() );
		if( loadedRealizedProduct.calculateStockQuantity() > 0 || EcommerceConfiguration.getEcommerceSettingsStatic().isOutOfStockProductAllowedOnOrder() ) {
			EcommerceShoppingCart ecommerceShoppingCart = EcommerceUtil.getEcommerceUtil().getOrCreateEcommerceShoppingCart();
			ecommerceShoppingCart.addToCart( loadedRealizedProduct, true );
			// We need to call this as hibernate screws up the bidirectional relationship between
			// the items in the cart and the cart.  It replaces the objects with different instances
			// which need to be replace to the right instance.
			ecommerceShoppingCart.saveDetails();
			ecommerceShoppingCart.refreshShoppingCartItemReferences();
		} else {
			EcommerceUtil.getEcommerceUtil().addAbandonmentIssueToCart( CartAbandonmentIssue.PRODUCT_OUT_OF_STOCK );
			JSFUtil.addMessage( "Sorry, " + loadedRealizedProduct.getDisplayName() + " is out of stock" );
		}
		return null;
	}

	public String addToCart() {
		RealizedProduct clickedProduct = getRealizedProductFromRequest();
		RealizedProduct loadedRealizedProduct = new BeanDao( RealizedProduct.class ).get( clickedProduct.getId() );
		if( loadedRealizedProduct.getQuantity() > 0 || EcommerceConfiguration.getEcommerceSettingsStatic().isOutOfStockProductAllowedOnOrder() ) {
			EcommerceShoppingCart ecommerceShoppingCart = EcommerceUtil.getEcommerceUtil().getOrCreateEcommerceShoppingCart();
			ecommerceShoppingCart.addToCart( loadedRealizedProduct, true );
			// We need to call this as hibernate screws up the bidirectional relationship between
			// the items in the cart and the cart.  It replaces the objects with different instances
			// which need to be replace to the right instance.
			ecommerceShoppingCart.saveDetails();
			ecommerceShoppingCart.refreshShoppingCartItemReferences();
		} else {
			EcommerceUtil.getEcommerceUtil().addAbandonmentIssueToCart( CartAbandonmentIssue.PRODUCT_OUT_OF_STOCK );
			JSFUtil.addMessage( "Sorry, " + loadedRealizedProduct.getDisplayName() + " is out of stock" );
		}
		return null;
	}

	public String getSearchTerm() {
		return "";
	}

	public void getProducts() {
		int firstIdx = pagination.getFirstProductIndex();
		int lastIdx = pagination.getFirstProductIndex() + pagination.getItemsPerPage();
		setCurrentProductList( productListFilter.getCurrentProductList( firstIdx, lastIdx ) );
		this.pagination.setLastPage(productListFilter.getFilteredRealizedProductList().size());
	}

	public List<RealizedProduct> getPaginatedProductList() {
		return getCurrentProductList();
	}

	public String getLastInRowClass(int itemsPerRow) {
		List<RealizedProduct> products = getPaginatedProductList();
		RealizedProduct realizedProduct = getRealizedProductFromRequest();
		int index = products.indexOf(realizedProduct);
		if ( (index + 1) % itemsPerRow == 0 ) {
			return "aplos-row-last";
		}
		return "";
	}

	public List<PaginationControl> getPaginationControls() {
		return this.pagination.getControls();
	}
	
	// Overiddable
	public PageScroller createPageScroller() {
		PageScroller pageScroller = new PageScroller();
		return pageScroller;
	}

	public SortOption getSortOption() {
		return productListFilter.getSortOption();
	}

	public void setSortOption(SortOption newSortOption) {
		productListFilter.setSortOption(newSortOption);
	}

	public List<SelectItem> getSortingOptions() {
	    List<SelectItem> items = new ArrayList<SelectItem>();
	    for (SortOption type: SortOption.values()) {
	     items.add(new SelectItem(type, "Sort By : " + type.getDisplayName()));
	    }
	    return items;
	}

	public void switchPage() {
		this.pagination.setCurrentPage(((PaginationControl) JSFUtil.getRequest().getAttribute("control")).getPageNumber());
	}
	
	public boolean isProductListRootDescriptionEmpty() {
		ProductListRoot productListRoot = getProductListAtom().getProductListRoot();
		if( productListRoot != null ) {
			String productListRootDescription = productListRoot.getDescription();
			if( CommonUtil.isNullOrEmpty( productListRootDescription ) || productListRootDescription.trim().equals( "<br />" ) ) {
				return true;
			}
		}
		return false;
	}

	public static ProductListModule getProductListModule() {
		return productListModule;
	}

	//views dont like static
	public ProductListModule getProductListAtom() {
		return productListModule;
	}

	public void setProductListModule(ProductListModule newproductListModule) {
		this.productListModule = newproductListModule;
	}

	public ProductListFilter getProductListFilter() {
		return productListFilter;
	}

	public String getTitleImageUrl() {
		return getProductListAtom().getProductListRoot().getProductListRootTitleImageUrl();

	}

	// This is overridden in sub classes.
	public boolean isShowingDescriptionPanel() {
		ProductListRoot productListRoot = getProductListAtom().getProductListRoot();
		if( productListRoot instanceof ProductType && CommonUtil.getStringOrEmpty( ((ProductType) productListRoot).getDescription() ).equals( "" ) ) {
			return false;
		} else {
			return true;
		}
	}

	public RealizedProduct getRealizedProductFromRequest() {
		return (RealizedProduct) JSFUtil.getRequest().getAttribute("realProduct");
	}

	public String viewProductInfo() {
		AplosBean aplosBean = getRealizedProductFromRequest();
		AplosBean loadedAplosBean = (AplosBean) new BeanDao( aplosBean.getClass() ).get( aplosBean.getId() );
		loadedAplosBean.addToScope();
		JSFUtil.redirect( EcommerceUtil.getEcommerceUtil().getProductMapping((RealizedProduct)loadedAplosBean) , true );
		return null;
	}

	public String getProductMapping() {
		return EcommerceUtil.getEcommerceUtil().getProductMapping(getRealizedProductFromRequest());
	}

	public boolean isProductListEmpty() {
		if( getCurrentProductList() == null || getCurrentProductList().size() == 0 ) {
			return true;
		} else {
			return false;
		}
	}

	public String getMappingFromCategoryName() {
		return CommonUtil.makeSafeUrlMapping(((AplosBean) JSFUtil.getRequest().getAttribute("product")).getDisplayName());
	}

	public String getPageTitle() {
		return getProductListModule().getPageTitle();
	}

	public String getCurrentUrl() {
		return JSFUtil.getAplosContextOriginalUrl();
	}

	protected void setCurrentProductList(List<RealizedProduct> currentProductList) {
		this.currentProductList = currentProductList;
	}

	protected List<RealizedProduct> getCurrentProductList() {
		return currentProductList;
	}

	public String getAverageAsStarListItems() {
		RealizedProduct realizedProduct = getRealizedProductFromRequest();
		if (realizedProduct != null) {
			Double average = realizedProduct.getProductInfo().getReviewAverage();
			StringBuffer buff = new StringBuffer("<li");
			if (average >= 1.75) {
				buff.append(" class='aplos-full-star'");
			} else if (average >= 0.75) {
				buff.append(" class='aplos-half-star'");
			}
			buff.append(">&#160;</li><li");
			if (average >= 3.75) {
				buff.append(" class='aplos-full-star'");
			} else if (average >= 2.75) {
				buff.append(" class='aplos-half-star'");
			}
			buff.append(">&#160;</li><li");
			if (average >= 5.75) {
				buff.append(" class='aplos-full-star'");
			} else if (average >= 4.75) {
				buff.append(" class='aplos-half-star'");
			}
			buff.append(">&#160;</li><li");
			if (average >= 7.75) {
				buff.append(" class='aplos-full-star'");
			} else if (average >= 6.75) {
				buff.append(" class='aplos-half-star'");
			}
			buff.append(">&#160;</li><li style='margin:0;'");
			if (average >= 9.75) {
				buff.append(" class='aplos-full-star'");
			} else if (average >= 8.75) {
				buff.append(" class='aplos-half-star'");
			}
			buff.append(">&#160;</li>");
			return buff.toString();
		}
		return "";
	}
}














