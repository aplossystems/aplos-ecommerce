package com.aplos.ecommerce.developermodulebacking.frontend;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.aplos.cms.enums.SortOption;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.ErrorEmailSender;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.beans.developercmsmodules.ProductListModule;
import com.aplos.ecommerce.beans.developercmsmodules.SearchProductListModule;
import com.aplos.ecommerce.beans.product.ProductBrand;
import com.aplos.ecommerce.beans.product.ProductColour;
import com.aplos.ecommerce.beans.product.ProductSize;
import com.aplos.ecommerce.beans.product.ProductSizeType;
import com.aplos.ecommerce.beans.product.ProductType;
import com.aplos.ecommerce.module.EcommerceConfiguration;



/* Inner class to handle the 'Quick Filters' sidebar on category list pages */
public class ProductListFilter {

	//private List<RealizedProduct> filterListRpCache;
	private final Map<Long,ProductType> typeFilters = new HashMap<Long,ProductType>();
	private final Map<Long,ProductBrand> brandFilters = new HashMap<Long,ProductBrand>();
	private final Map<Long,ProductSize> sizeFilters = new HashMap<Long,ProductSize>();
	private final Map<Long,ProductColour> colourFilters = new HashMap<Long,ProductColour>();

	private HashMap<String,ProductType> availableTypeMap = new HashMap<String,ProductType>();
	private HashMap<String,ProductBrand> availableBrandMap = new HashMap<String,ProductBrand>();
	private HashMap<String,ProductSizeType> availableSizeTypeMap = new HashMap<String,ProductSizeType>();
	private HashMap<String,ProductSize> availableSizeMap = new HashMap<String,ProductSize>();
	private HashMap<String,ProductColour> availableColourMap = new HashMap<String,ProductColour>();

	private HashMap<String,ProductType> disabledTypeMap = new HashMap<String,ProductType>();
	private HashMap<String,ProductBrand> disabledBrandMap = new HashMap<String,ProductBrand>();
	private HashMap<String,ProductSize> disabledSizeMap = new HashMap<String,ProductSize>();
	private HashMap<String,ProductColour> disabledColourMap = new HashMap<String,ProductColour>();

	private final HashMap<Long,Set<RealizedProductInfo>> productTypeMap = new HashMap<Long,Set<RealizedProductInfo>>();
	private final HashMap<Long,Set<RealizedProductInfo>> productBrandMap = new HashMap<Long,Set<RealizedProductInfo>>();
	private final HashMap<Long,Set<RealizedProductInfo>> productSizeMap = new HashMap<Long,Set<RealizedProductInfo>>();
	private final HashMap<Long,Set<RealizedProductInfo>> productColourMap = new HashMap<Long,Set<RealizedProductInfo>>();
	private final HashMap<Long,Set<RealizedProductInfo>> productInfoMap = new HashMap<Long,Set<RealizedProductInfo>>();
	private final HashMap<Long,RealizedProductInfo> realizedProductInfoMap = new HashMap<Long,RealizedProductInfo>();

	SortOption sortOption = SortOption.DEFAULT_ORDERING;

	//stores the maximized list of products for this entry point, so we can determine filters
	private Map<Long,RealizedProduct> loadedRealizedProductMap = new HashMap<Long,RealizedProduct>();
	private List<RealizedProduct> filteredRealizedProductList = new ArrayList<RealizedProduct>();
	private String currentBaseUrl = "";
	private ProductListModule productListModule;
	private boolean isShowingSizeChart;
	private boolean isShowingShopByColour;
	private boolean isShowingShopByType;
	private boolean isShowingShopBySize;
	private boolean isShowingShopByBrand;
	private List<Integer> filterGroupOrdering = new ArrayList<Integer>();
	private final int REALIZED_PRODUCT_ID = 0;
	private final int DEFAULT_PRODUCT_ID = 1;
	private final int PRODUCT_INFO_ID = 2;
	private final int BRAND_ID = 3;
	private final int COLOUR_ID = 4;
	private final int SIZE_ID = 5;
	private final int TYPE_ID = 6;

	public ProductListFilter() {}

	public String getCurrentBaseUrl() {
		return currentBaseUrl;
	}

	public void setCurrentBaseUrl(String baseIn) {
		currentBaseUrl=baseIn;
	}

	public void updateIfRequired( ProductListModule productListModule, HttpServletRequest request) {
		this.productListModule = productListModule;
		determineEntryPointRealizedProductList();
		determineProductFiltersByQueryString(request);
	}

	public void clearSets() {
		//System.out.println("SEARCHING");
		brandFilters.clear();
		typeFilters.clear();
		sizeFilters.clear();
		colourFilters.clear();
	}

	public void determineEntryPointRealizedProductList() {
			clearSets();
			/* 
			 * Get the ids first while doing the search, otherwise there's a load of duplicates in the
			 * list and extra RealizedProducts will be created really adding to the time.  This is mainly
			 * due to linking to the searchKeywords in the first query.
			 * 
			 * This can be optimised a lot more with the first query doing the search and pulling out 
			 * where the products should go in the maps and then the second search just pulling out
			 * the product infos and placing them in the maps based on the first search.
			 */
			
			BeanDao realizedProductDao = new BeanDao( RealizedProduct.class );
			realizedProductDao.setSelectCriteria( "bean.id, pi.defaultRealizedProduct.id, pi.id, pb.id, bean.productColour.id, bean.productSize.id, pt.id" );
			realizedProductDao.addQueryTable( "pc", "bean.productColour" );
			realizedProductDao.addQueryTable( "ps", "bean.productSize" );
			realizedProductDao.addQueryTable( "pst", "bean.productSize.productSizeType" );
			realizedProductDao.addQueryTable( "pi", "bean.productInfo" );
			realizedProductDao.addQueryTable( "p", "pi.product" );
			realizedProductDao.addQueryTable( "pt", "pi.product.productTypes" );
			realizedProductDao.addQueryTable( "ppt", "pt.parentProductType" );
			realizedProductDao.addQueryTable( "pb", "pi.product.productBrand" );
			realizedProductDao.addQueryTable( "did", "bean.defaultImageDetails" );
			realizedProductDao.addQueryTable( "sk", "pi.searchKeywordList" );
			realizedProductDao.addWhereCriteria( "bean.productInfo.active=1" );
			realizedProductDao.addWhereCriteria( "bean.isHiddenFromCustomer=null or bean.isHiddenFromCustomer=0" );
			
			if( !EcommerceConfiguration.getEcommerceSettingsStatic().isShowingDiscontinuedProducts() ) {
				realizedProductDao.addWhereCriteria( "bean.discontinued=null OR bean.discontinued=false" );
			}
			if( !EcommerceConfiguration.getEcommerceConfiguration().isProductVisibleWhenOutOfStock() ) {
				realizedProductDao.addWhereCriteria( "bean.quantity > 0" );
			}
			/*
			 * Added for Teletest
			 */
			if( !(productListModule instanceof SearchProductListModule) ) {
				realizedProductDao.addWhereCriteria( "pi.product.isShowingOnWebsite = true" );
			}
			realizedProductDao.addWhereCriteria( productListModule.getUnfilteredProductWhereClaus() );

			try {
				productListModule.addSearchParameters(realizedProductDao);
				List<Object[]> realizedProductObjs = realizedProductDao.getResultFields();
				
				productBrandMap.clear();
				productColourMap.clear();
				productSizeMap.clear();
				productTypeMap.clear();
				productInfoMap.clear();
				loadedRealizedProductMap.clear();
				realizedProductInfoMap.clear();
				RealizedProductInfo tempRealizedProductInfo;
				for( int i = 0, n = realizedProductObjs.size(); i < n; i++ ) {
					tempRealizedProductInfo = new RealizedProductInfo( (Long) realizedProductObjs.get( i )[ REALIZED_PRODUCT_ID ],
							(Long) realizedProductObjs.get( i )[ DEFAULT_PRODUCT_ID ],
							(Long) realizedProductObjs.get( i )[ PRODUCT_INFO_ID ],
							(Long) realizedProductObjs.get( i )[ BRAND_ID ],
							(Long) realizedProductObjs.get( i )[ COLOUR_ID ],
							(Long) realizedProductObjs.get( i )[ SIZE_ID ],
							(Long) realizedProductObjs.get( i )[ TYPE_ID ]);
					
					if( tempRealizedProductInfo.getId() != null ) {
						loadedRealizedProductMap.put( tempRealizedProductInfo.getId(), null );
						realizedProductInfoMap.put( tempRealizedProductInfo.getId(), tempRealizedProductInfo );
					}
					if( tempRealizedProductInfo.getProductBrandId() != null ) {
						brandFilters.put( tempRealizedProductInfo.getProductBrandId(), null );
					}
					if( tempRealizedProductInfo.getProductColourId() != null ) {
						colourFilters.put( tempRealizedProductInfo.getProductColourId(), null );
					}
					if( tempRealizedProductInfo.getProductSizeId() != null ) {
						sizeFilters.put( tempRealizedProductInfo.getProductSizeId(), null );
					}
					if( tempRealizedProductInfo.getProductTypeId() != null ) {
						typeFilters.put( tempRealizedProductInfo.getProductTypeId(), null );
					}
					if( tempRealizedProductInfo.getProductInfoId() != null ) {
						if( productInfoMap.get( tempRealizedProductInfo.getProductInfoId() ) == null ) {
							productInfoMap.put( tempRealizedProductInfo.getProductInfoId(), new HashSet<RealizedProductInfo>() );
						}
						tempRealizedProductInfo.setRelatedProducts( productInfoMap.get( tempRealizedProductInfo.getProductInfoId() ) );
						productInfoMap.get( tempRealizedProductInfo.getProductInfoId() ).add( tempRealizedProductInfo );
					}

					if ( tempRealizedProductInfo.getProductBrandId() != null) {
						if( productBrandMap.get( tempRealizedProductInfo.getProductBrandId() ) == null ) {
							productBrandMap.put( tempRealizedProductInfo.getProductBrandId(), new HashSet<RealizedProductInfo>() );
						}
						productBrandMap.get( tempRealizedProductInfo.getProductBrandId() ).add( tempRealizedProductInfo );
					}
					if (tempRealizedProductInfo.getProductColourId() != null) {
						if( productColourMap.get( tempRealizedProductInfo.getProductColourId() ) == null ) {
							productColourMap.put( tempRealizedProductInfo.getProductColourId(), new HashSet<RealizedProductInfo>() );
						}
						productColourMap.get( tempRealizedProductInfo.getProductColourId() ).add( tempRealizedProductInfo );
					}
					if (tempRealizedProductInfo.getProductSizeId() != null) {
						if( productSizeMap.get( tempRealizedProductInfo.getProductSizeId() ) == null ) {
							productSizeMap.put( tempRealizedProductInfo.getProductSizeId(), new HashSet<RealizedProductInfo>() );
						}
						productSizeMap.get( tempRealizedProductInfo.getProductSizeId() ).add( tempRealizedProductInfo );
					}
					if (tempRealizedProductInfo.getProductTypeId() != null) {
						if( productTypeMap.get( tempRealizedProductInfo.getProductTypeId() ) == null ) {
							productTypeMap.put( tempRealizedProductInfo.getProductTypeId(), new HashSet<RealizedProductInfo>() );
						}
						productTypeMap.get( tempRealizedProductInfo.getProductTypeId() ).add( tempRealizedProductInfo );
					}
				}

				if( productBrandMap.keySet().size() > 0 ) {
					BeanDao productBrandDao = new BeanDao( ProductBrand.class );
					productBrandDao.addWhereCriteria( "bean.id IN (" + StringUtils.join( productBrandMap.keySet(), ",") + ")" );
					List<ProductBrand> productBrandList = productBrandDao.getAll();
					for( int i = 0, n = productBrandList.size(); i < n; i++ ) {
						brandFilters.put( productBrandList.get( i ).getId(), productBrandList.get( i ) );
					}
					removeNullEntries(brandFilters);
				}

				if( productColourMap.keySet().size() > 0 ) {
					BeanDao productColourDao = new BeanDao( ProductColour.class );
					productColourDao.addWhereCriteria( "bean.id IN (" + StringUtils.join( productColourMap.keySet(), ",") + ")" );
					List<ProductColour> productColourList = productColourDao.getAll();
					for( int i = 0, n = productColourList.size(); i < n; i++ ) {
						colourFilters.put( productColourList.get( i ).getId(), productColourList.get( i ) );
					}
					removeNullEntries(colourFilters);
				}

				if( productSizeMap.keySet().size() > 0 ) {
					BeanDao productSizeDao = new BeanDao( ProductSize.class );
					productSizeDao.addWhereCriteria( "bean.id IN (" + StringUtils.join( productSizeMap.keySet(), ",") + ")" );
					List<ProductSize> productSizeList = productSizeDao.getAll();
					for( int i = 0, n = productSizeList.size(); i < n; i++ ) {
						sizeFilters.put( productSizeList.get( i ).getId(), productSizeList.get( i ) );
					}
					removeNullEntries(sizeFilters);
				}
				
				if( productTypeMap.keySet().size() > 0 ) {
					BeanDao productTypeDao = new BeanDao( ProductType.class );
					productTypeDao.addWhereCriteria( "bean.id IN (" + StringUtils.join( productTypeMap.keySet(), ",") + ")" );
					List<ProductType> productTypeList = productTypeDao.getAll();
					for( int i = 0, n = productTypeList.size(); i < n; i++ ) {
						typeFilters.put( productTypeList.get( i ).getId(), productTypeList.get( i ) );
					}
					removeNullEntries(typeFilters);
				}

			} catch( Exception ex ) {
				ex.printStackTrace();
				ErrorEmailSender.sendErrorEmail(JSFUtil.getRequest(),ApplicationUtil.getAplosContextListener(), ex);
			}

			updateAvailableMaps();
			if (productColourMap.size() > 1) {
				isShowingShopByColour = true;
			} else {
				isShowingShopByColour = false;
			}

			if (productSizeMap.size() > 1) {
				isShowingShopBySize = true;
			} else {
				isShowingShopBySize = false;
			}

			if (productBrandMap.size() > 1) {
				isShowingShopByBrand = true;
			} else {
				isShowingShopByBrand = false;
			}

			if (productTypeMap.size() > 1) {
				isShowingShopByType = true;
			} else {
				isShowingShopByType = false;
			}
			isShowingSizeChart = false;

		//}
	}
	
	public void removeNullEntries( Map<Long,?> productFilters ) {
		List<Long> keys = new ArrayList<Long>(productFilters.keySet());
		for( int i = 0, n = keys.size(); i < n; i++ ) {
			if( productFilters.get( keys.get( i ) ) == null ) {
				productFilters.remove( keys.get( i ) );
			}
		}
	}

	public boolean isShowingShopByColour() {
		return isShowingShopByColour;
	}

	public boolean isShowingShopBySize() {
		return isShowingShopBySize;
	}

	public boolean isShowingShopByBrand() {
		return isShowingShopByBrand;
	}

	public boolean isShowingShopByType() {
		return isShowingShopByType;
	}

	public boolean isShowingSizeChart() {
		return isShowingSizeChart;
	}

	public void updateAvailableMaps() {
		availableTypeMap = new HashMap<String,ProductType>();
		availableBrandMap = new HashMap<String,ProductBrand>();
		availableSizeTypeMap = new HashMap<String,ProductSizeType>();
		availableSizeMap = new HashMap<String,ProductSize>();
		availableColourMap = new HashMap<String,ProductColour>();

		for( ProductType tempProductType : typeFilters.values() ) {
			availableTypeMap.put( tempProductType.getName(), tempProductType );
			if( tempProductType.getParentProductType() != null && availableTypeMap.get( tempProductType.getParentProductType() ) == null ) {
				availableTypeMap.put( tempProductType.getParentProductType().getName(), tempProductType.getParentProductType() );
			}
		}

		for( ProductBrand tempProductBrand : brandFilters.values() ) {
			availableBrandMap.put( tempProductBrand.getName(), tempProductBrand );
		}

		for( ProductSize tempProductSize : sizeFilters.values() ) {
			availableSizeMap.put( tempProductSize.getName(), tempProductSize );
			if( tempProductSize.getProductSizeType() != null && availableSizeTypeMap.get( tempProductSize.getProductSizeType() ) == null ) {
				availableSizeTypeMap.put( tempProductSize.getProductSizeType().getName(), tempProductSize.getProductSizeType() );
			}
		}

		for( ProductColour tempProductColour : colourFilters.values() ) {
			availableColourMap.put( tempProductColour.getName(), tempProductColour );
		}
	}
	
	public Map<Long,RealizedProduct> getLoadedRealizedProductMap( boolean loadAllProducts ) {
		if( loadAllProducts ) {
			for( Long realizedProductId : loadedRealizedProductMap.keySet() ) {
				if( loadedRealizedProductMap.get( realizedProductId ) == null ) {
					loadedRealizedProductMap.put( realizedProductId, (RealizedProduct) new BeanDao( RealizedProduct.class ).get( realizedProductId ) );
				}
			}
		}
		return loadedRealizedProductMap;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<RealizedProduct> getCurrentProductList( int firstIdx, int lastIdx ) {
		ArrayList<RealizedProductInfo> filteredRealizedProductInfos = new ArrayList<RealizedProductInfo>();

		RealizedProductInfo tempDefaultRealizedProductInfo;
		List<Long> tempColourIdList = null;
		for( Set<RealizedProductInfo> realizedProductInfos : productInfoMap.values() ) {
			if ( !EcommerceConfiguration.getEcommerceSettingsStatic().isProductListGroupByColour() ) {
				tempColourIdList = new ArrayList<Long>();
			}
			for( RealizedProductInfo realizedProductInfo : realizedProductInfos ) {
				if ( !EcommerceConfiguration.getEcommerceSettingsStatic().isProductListGroupByColour() ) {
					if (!tempColourIdList.contains(realizedProductInfo.getProductColourId())) {
						filteredRealizedProductInfos.add( realizedProductInfo );
						tempColourIdList.add(realizedProductInfo.getProductColourId());
					}
				} else if( realizedProductInfo.getDefaultRealizedProductId() != null ) {
					tempDefaultRealizedProductInfo = realizedProductInfoMap.get( realizedProductInfo.getDefaultRealizedProductId() );
					if( tempDefaultRealizedProductInfo != null ) {
						filteredRealizedProductInfos.add( tempDefaultRealizedProductInfo );
					}
				} else {
					filteredRealizedProductInfos.add( realizedProductInfo );
				}
			}
		}
		
		/*
		 * Not currently used so I commented out after an update made it redundant
		 */
		
//		for( int i = 0, n = filterGroupOrdering.size(); i < n; i++ ) {
//			if( filterGroupOrdering.get( i ).equals(TYPE_ID) ) {
//				disableTypeFilters( filteredRealizedProductIds );
//				removeRealizedProductsByType( filteredRealizedProductIds );
//			} else if( filterGroupOrdering.get( i ).equals(COLOUR_ID) ) {
//				disableColourFilters( filteredRealizedProductIds );
//				removeRealizedProductsByColour( filteredRealizedProductIds );
//			} else if( filterGroupOrdering.get( i ).equals(SIZE_ID) ) {
//				disableSizeFilters( filteredRealizedProductIds );
//				removeRealizedProductsBySize( filteredRealizedProductIds );
//			} else if( filterGroupOrdering.get( i ).equals(BRAND_ID) ) {
//				disableBrandFilters( filteredRealizedProductIds );
//				removeRealizedProductsByBrand( filteredRealizedProductIds );
//			}
//		}
//
//		if( !filterGroupOrdering.contains( TYPE_ID ) ) {
//			disableTypeFilters( filteredRealizedProductIds );
//		}
//
//		if( !filterGroupOrdering.contains( COLOUR_ID ) ) {
//			disableColourFilters( filteredRealizedProductIds );
//		}
//
//		if( !filterGroupOrdering.contains( SIZE_ID ) ) {
//			disableSizeFilters( filteredRealizedProductIds );
//		}
//
//		if( !filterGroupOrdering.contains( BRAND_ID ) ) {
//			disableBrandFilters( filteredRealizedProductIds );
//		}

		Set<RealizedProduct> realizedProductSet = new HashSet<RealizedProduct>();
		RealizedProduct tempRealizedProduct;
		for( int i = 0, n = filteredRealizedProductInfos.size(); i < n; i++ ) {
			tempRealizedProduct = loadedRealizedProductMap.get( filteredRealizedProductInfos.get( i ) );
			if( tempRealizedProduct == null ) {
				tempRealizedProduct = new BeanDao( RealizedProduct.class ).get( filteredRealizedProductInfos.get( i ).getId() );
			}
			realizedProductSet.add( tempRealizedProduct );
		}

		filteredRealizedProductList = new ArrayList(realizedProductSet);		
 		filteredRealizedProductList = sort(filteredRealizedProductList);
		List<RealizedProduct> currentProductList = new ArrayList<RealizedProduct>();
		int listSize = filteredRealizedProductList.size();
		for( int i = firstIdx; i < lastIdx && i < listSize; i++ ) {
			currentProductList.add( filteredRealizedProductList.get( i ) );
		}

		return currentProductList;
	}

	@SuppressWarnings("unchecked")
	public List<RealizedProduct> sort(List<RealizedProduct> currentProductList) {
		if (sortOption == SortOption.NAME_A_TO_Z) {
			return (List<RealizedProduct>) CommonUtil.sortAlphabetically(currentProductList);
		} else if (sortOption == SortOption.PRICE_LOW_TO_HIGH) {
			return sortByPrice(currentProductList);
		} else if (sortOption == SortOption.PRICE_HIGH_TO_LOW) {
			List<RealizedProduct> returnProductList = sortByPrice(currentProductList);
			Collections.reverse(returnProductList);
			return returnProductList;
		} else { //SortOption.DEFAULT_ORDERING
			return productListModule.defaultSort(currentProductList);
		}
	}

	@SuppressWarnings("unchecked")
	public static List<RealizedProduct> sortByPrice(List<RealizedProduct> unsortedList) {
		Collections.sort(unsortedList, new RealizedProductPriceComparator());
		return unsortedList;
	}

	public SortOption getSortOption() {
		return sortOption;
	}

	public void setSortOption(SortOption sortOption) {
		this.sortOption=sortOption;
	}

	public void removeRealizedProductsByType(ArrayList<Long> filteredRealizedProductIds) {
//		for( ProductType productType : disabledTypeMap.values() ) {
//			for( Long realizedProductId : productTypeMap.get( productType.getId() ) {
//			}
//			for( int j = filteredRealizedProductIds.size() - 1; j > -1; j-- ) {
//				for( productTypeMap.get )
//				tempRealizedProduct = filteredRealizedProductIds.get( j );
//				boolean containsProductType = false;
//				for( ProductType tempProductType : tempRealizedProduct.getProductInfo().getProduct().getProductTypes() ) {
//					if( typeFilters.containsValue( tempProductType ) ) {
//						containsProductType = true;
//						break;
//					}
//				}
//				if( !containsProductType ) {
//					filteredRealizedProductIds.remove( tempRealizedProduct );
//				}
//			}
//		}
	}

	@SuppressWarnings("unchecked")
	public void disableTypeFilters(ArrayList<Long> filteredRealizedProductIds) {
		disabledTypeMap = new HashMap( availableTypeMap );
		for( Long tempRealizedProductId : filteredRealizedProductIds ) {
			for( ProductType disabledType : disabledTypeMap.values() ) {
				for( RealizedProductInfo realizedProductInfo : productTypeMap.get( disabledType.getId() ) ) { 
					if( realizedProductInfo.getId() == tempRealizedProductId ) {
						disabledTypeMap.remove( disabledType );
						break;
					}
				}
			}
		}
	}

	public void removeRealizedProductsByColour(ArrayList<Long> filteredRealizedProductIds) {
		RealizedProduct tempRealizedProduct;
		for( int j = filteredRealizedProductList.size() - 1; j > -1; j-- ) {
			tempRealizedProduct = filteredRealizedProductList.get( j );
			if( !colourFilters.containsValue( tempRealizedProduct.getProductColour() ) ) {
				filteredRealizedProductList.remove( tempRealizedProduct );
			}
		}
	}

	public void disableColourFilters(ArrayList<Long> filteredRealizedProductIds) {
		disabledColourMap = new HashMap( availableColourMap );
		for( Long tempRealizedProductId : filteredRealizedProductIds ) {
			for( ProductColour disabledColour : disabledColourMap.values() ) {
				for( RealizedProductInfo realizedProductInfo : productColourMap.get( disabledColour.getId() ) ) { 
					if( realizedProductInfo.getId() == tempRealizedProductId ) {
						disabledColourMap.remove( disabledColour );
						break;
					}
				}
			}
		}
	}

	public void removeRealizedProductsBySize(ArrayList<Long> filteredRealizedProductIds) {
		RealizedProduct tempRealizedProduct;
		for( int j = filteredRealizedProductList.size() - 1; j > -1; j-- ) {
			tempRealizedProduct = filteredRealizedProductList.get( j );
			if( !sizeFilters.containsValue( tempRealizedProduct.getProductSize() ) ) {
				filteredRealizedProductList.remove( tempRealizedProduct );
			}
		}
	}

	public void disableSizeFilters(ArrayList<Long> filteredRealizedProductIds) {
		disabledSizeMap = new HashMap( availableSizeMap );
		for( Long tempRealizedProductId : filteredRealizedProductIds ) {
			for( ProductSize disabledSize : disabledSizeMap.values() ) {
				for( RealizedProductInfo realizedProductInfo : productSizeMap.get( disabledSize.getId() ) ) { 
					if( realizedProductInfo.getId() == tempRealizedProductId ) {
						disabledSizeMap.remove( disabledSize );
						break;
					}
				}
			}
		}
	}

	public void removeRealizedProductsByBrand(ArrayList<Long> filteredRealizedProductIds) {
		RealizedProduct tempRealizedProduct;
		for( int j = filteredRealizedProductList.size() - 1; j > -1; j-- ) {
			tempRealizedProduct = filteredRealizedProductList.get( j );
			if( !brandFilters.containsValue( tempRealizedProduct.getProductInfo().getProduct().getProductBrand() ) ) {
				filteredRealizedProductList.remove( tempRealizedProduct );
			}
		}
	}

	public void disableBrandFilters(ArrayList<Long> filteredRealizedProductIds) {
		disabledBrandMap = new HashMap( availableBrandMap );
		for( Long tempRealizedProductId : filteredRealizedProductIds ) {
			for( ProductBrand disabledBrand : disabledBrandMap.values() ) {
				for( RealizedProductInfo realizedProductInfo : productBrandMap.get( disabledBrand.getId() ) ) { 
					if( realizedProductInfo.getId() == tempRealizedProductId ) {
						disabledBrandMap.remove( disabledBrand );
						break;
					}
				}
			}
		}
	}

	public List<RealizedProduct> getFilteredRealizedProductList() {
		return filteredRealizedProductList;
	}

	public void addToFilterGroupOrdering( int group ) {
		if( !filterGroupOrdering.contains( group ) ) {
			filterGroupOrdering.add( group );
		}
	}

	public void determineProductFiltersByQueryString(HttpServletRequest request ) {
		brandFilters.clear();
		typeFilters.clear();
		sizeFilters.clear();
		colourFilters.clear();

		Enumeration parameterNames = JSFUtil.getRequest().getParameterNames();
		filterGroupOrdering = new ArrayList<Integer>();
		while( parameterNames.hasMoreElements() ) {
			String s = (String) parameterNames.nextElement();
			if( s.equals( "type[]" ) ) {
				addToFilterGroupOrdering(TYPE_ID);
			}
			if( s.equals( "brand[]" ) ) {
				addToFilterGroupOrdering(BRAND_ID);
			}
			if( s.equals( "size[]" ) ) {
				addToFilterGroupOrdering(SIZE_ID);
			}
			if( s.equals( "colour[]" ) ) {
				addToFilterGroupOrdering(COLOUR_ID);
			}
		}

		Map<String, String[]> qsMap = JSFUtil.getRequest().getParameterMap();
		try {
			String[] qsTypes = qsMap.get("type[]");
			if (qsTypes != null) {
				for (String type : qsTypes) {
					ProductType tempProductType = availableTypeMap.get(URLDecoder.decode(type,"UTF-8")); 
					if( tempProductType != null ) {
						typeFilters.put(tempProductType.getId(), tempProductType);
					}
				}
			}
			qsTypes = qsMap.get("brand[]");
			if (qsTypes != null) {
				ProductBrand tempBrand;
				for (String brand : qsTypes) {
					tempBrand = availableBrandMap.get(URLDecoder.decode(brand,"UTF-8")); 
					if( tempBrand != null ) {
						brandFilters.put(tempBrand.getId(), tempBrand);
					}
				}
			}
			qsTypes = qsMap.get("size[]");
			if (qsTypes != null) {
				ProductSize tempSize;
				for (String size : qsTypes) {
					tempSize = availableSizeMap.get(URLDecoder.decode(size,"UTF-8")); 
					if( tempSize != null ) {
						sizeFilters.put(tempSize.getId(),tempSize);
					}
				}
			}
			qsTypes = qsMap.get("colour[]");
			if (qsTypes != null) {
				ProductColour tempColour;
				for (String colour : qsTypes) {
					tempColour = availableColourMap.get(URLDecoder.decode(colour,"UTF-8"));
					if( tempColour != null ) {
						colourFilters.put(tempColour.getId(), tempColour);
					}
				}
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public String getSizeQueryString() {
		String qs = getQueryString();
		ProductSize productSize = (ProductSize) JSFUtil.getRequest().getAttribute("tempProductSize");

		try {
			if (productSize != null) {
				if (qs.contains("size[]=" + URLEncoder.encode(productSize.getName(),"UTF-8"))) {

					qs = qs.replace("size[]=" + URLEncoder.encode(productSize.getName(),"UTF-8"), "");
					qs = qs.replace("&&", "&");

				} else {
					qs = qs + "&size[]=" + URLEncoder.encode(productSize.getName(),"UTF-8");
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return qs;
	}

	public String getColourQueryString() {
		String qs = getQueryString();
		ProductColour productColour = (ProductColour) JSFUtil.getRequest().getAttribute("tempProductColour");


		try {
			if (productColour != null) {
				if (qs.contains("colour[]=" + URLEncoder.encode(productColour.getName(),"UTF-8"))) {

					qs = qs.replace("colour[]=" + URLEncoder.encode(productColour.getName(),"UTF-8"), "");
					qs = qs.replace("&&", "&");

				} else {
					qs = qs + "&colour[]=" + URLEncoder.encode(productColour.getName(),"UTF-8");
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return qs;
	}

	public String getTypeCategoryQueryString() {
		ProductType productType = (ProductType) JSFUtil.getRequest().getAttribute("productTypeCategory");
		return getTypeQueryString(productType);
	}

	public String getTypeQueryString() {
		ProductType productType = (ProductType) JSFUtil.getRequest().getAttribute("tempProductType");
		return getTypeQueryString(productType);
	}

	public String getTypeQueryString(ProductType productType) {
		String qs = getQueryString();
		//ProductType productType = (ProductType) JSFUtil.getRequest().getAttribute("tempProductType");

		try {
			if (productType != null) {
				if (qs.contains("type[]=" + URLEncoder.encode(productType.getName(),"UTF-8"))) {

					qs = qs.replace("type[]=" + URLEncoder.encode(productType.getName(),"UTF-8"), "");
					qs = qs.replace("&&", "&");

				} else {
					qs = qs + "&type[]=" + URLEncoder.encode(productType.getName(),"UTF-8");
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return qs;
	}

	public String getBrandQueryString() {
		String qs = getQueryString();
		ProductBrand productBrand = (ProductBrand) JSFUtil.getRequest().getAttribute("tempProductBrand");

		try {
			if (productBrand != null) {
				if (qs.contains("brand[]=" + URLEncoder.encode(productBrand.getName(),"UTF-8"))) {

					qs = qs.replace("brand[]=" + URLEncoder.encode(productBrand.getName(),"UTF-8"), "");
					qs = qs.replace("&&", "&");

				} else {
					qs = qs + "&brand[]=" + URLEncoder.encode(productBrand.getName(),"UTF-8");
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return qs;
	}

	public String getQueryString() {
		StringBuffer qs = new StringBuffer();
		try {
			for (ProductType productType : typeFilters.values()) {
				if (qs.length() > 0) {
					qs.append("&");
				}
				qs.append("type[]=");
				qs.append(URLEncoder.encode(productType.getName(),"UTF-8"));
			}
			for (ProductBrand productBrand : brandFilters.values()) {
				if (qs.length() > 0) {
					qs.append("&");
				}
				qs.append("brand[]=");
				qs.append(URLEncoder.encode(productBrand.getName(),"UTF-8"));
			}
			for (ProductSize productSize : sizeFilters.values()) {
				if (qs.length() > 0) {
					qs.append("&");
				}
				qs.append("size[]=");
				qs.append(URLEncoder.encode(productSize.getName(),"UTF-8"));
			}
			for (ProductColour productColour : colourFilters.values()) {
				if (qs.length() > 0) {
					qs.append("&");
				}
				qs.append("colour[]=");
				qs.append(URLEncoder.encode(productColour.getName(),"UTF-8"));
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return qs.toString();
	}

	@SuppressWarnings("unchecked")
	public List<ProductType> getProductTypeCategoryList() {
		Iterator<ProductType> iter = availableTypeMap.values().iterator();
		ProductType tempProductType;
		Set<ProductType> productTypeCategorySet = new HashSet<ProductType>();
		while( iter.hasNext() ) {
			tempProductType = iter.next();
			if( tempProductType.getParentProductType() == null ) {
				productTypeCategorySet.add( tempProductType );
			} else {
				productTypeCategorySet.add( tempProductType.getParentProductType() );
			}
		}
		return (List<ProductType>) CommonUtil.sortAlphabetically(new ArrayList<ProductType>(productTypeCategorySet));
	}

	@SuppressWarnings("unchecked")
	public List<ProductType> getProductTypeList() {
		ProductType parentProductType = (ProductType) JSFUtil.getRequest().getAttribute("productTypeCategory");
		Iterator<ProductType> iter = availableTypeMap.values().iterator();
		ProductType tempProductType;
		List<ProductType> productTypeList = new ArrayList<ProductType>();
		while( iter.hasNext() ) {
			tempProductType = iter.next();
			if( tempProductType.getParentProductType() != null && tempProductType.getParentProductType().equals( parentProductType ) ) {
				productTypeList.add( tempProductType );

			}
		}
		return (List<ProductType>) CommonUtil.sortAlphabetically(productTypeList);
	}

	public List<ProductType> getFilteredTypeList() {
		if( typeFilters.size() == 0 ) {
			return new ArrayList<ProductType>( availableTypeMap.values() );
		} else {
			return new ArrayList<ProductType>( typeFilters.values() );
		}
	}

	@SuppressWarnings("unchecked")
	public List<ProductBrand> getBrandList() {
		return (List<ProductBrand>) CommonUtil.sortAlphabetically(new ArrayList<ProductBrand>( availableBrandMap.values() ));
	}

	public List<ProductBrand> getFilteredBrandList() {
		if( brandFilters.size() == 0 ) {
			return new ArrayList<ProductBrand>( availableBrandMap.values() );
		} else {
			return new ArrayList<ProductBrand>( brandFilters.values() );
		}
	}

	public List<ProductSizeType> getSizeTypeList() {
		return new ArrayList<ProductSizeType>( availableSizeTypeMap.values() );
	}

	public List<ProductSize> getSizeList() {
		ProductSizeType productSizeType = (ProductSizeType) JSFUtil.getRequest().getAttribute("sizetype");
		Iterator<ProductSize> iter = availableSizeMap.values().iterator();
		ProductSize tempProductSize;
		List<ProductSize> productSizeList = new ArrayList<ProductSize>();
		while( iter.hasNext() ) {
			tempProductSize = iter.next();
			if( tempProductSize.getProductSizeType() != null && tempProductSize.getProductSizeType().equals( productSizeType ) ) {
				productSizeList.add( tempProductSize );
			}
		}
		Collections.sort( productSizeList, new ProductSizeComparator() );
		return productSizeList;
	}

	public List<ProductSize> getFilteredSizeList() {
		if( sizeFilters.size() == 0 ) {
			return new ArrayList<ProductSize>( availableSizeMap.values() );
		} else {
			return new ArrayList<ProductSize>( sizeFilters.values() );
		}
	}

	@SuppressWarnings("unchecked")
	public List<ProductColour> getColourList() {
		return (List<ProductColour>) CommonUtil.sortAlphabetically(new ArrayList<ProductColour>( availableColourMap.values() ));
	}

	public List<ProductColour> getFilteredColourList() {
		if( colourFilters.size() == 0 ) {
			return new ArrayList<ProductColour>( availableColourMap.values() );
		} else {
			return new ArrayList<ProductColour>( colourFilters.values() );
		}
	}

	/* getIsXSelected methods decide whether to check or uncheck one of the checkboxes
	 * by checking its id against a list of id's which get stored when the checkbox is
	 * deselected */

	public boolean getIsTypeSelected() {
		ProductType productType = ((ProductType) JSFUtil.getRequest().getAttribute("tempProductType"));
		return getIsTypeSelected(productType);
	}

	public boolean getIsTypeSelected(ProductType productType) {
		ProductType ptc = ((ProductType) JSFUtil.getRequest().getAttribute("productTypeCategory"));
		if (typeFilters.containsValue( ptc )) {
			return true;
		} else {
			if (typeFilters.containsValue( productType )) {
				return true;
			}
			return false;
		}
	}

	public boolean anyChildSelected() {
		ProductType ptc = ((ProductType) JSFUtil.getRequest().getAttribute("productTypeCategory"));
		Iterator<ProductType> iter = availableTypeMap.values().iterator();
		while( iter.hasNext() ) {
			ProductType tempProductType = iter.next();
			if (tempProductType.getParentProductType() != null && tempProductType.getParentProductType()==ptc && getIsTypeSelected(tempProductType)) {
				return true;
			}
		}
		return false;
	}

	public boolean anyChildEnabled() {
		ProductType ptc = ((ProductType) JSFUtil.getRequest().getAttribute("productTypeCategory"));
		Iterator<ProductType> iter = availableTypeMap.values().iterator();
		while( iter.hasNext() ) {
			ProductType tempProductType = iter.next();
			if (tempProductType.getParentProductType() != null && tempProductType.getParentProductType()==ptc && !isTypeDisabled(tempProductType)) {
				return true;
			}
		}
		return false;
	}

	public boolean getIsTypeCategorySelected() {
		ProductType productType = ((ProductType) JSFUtil.getRequest().getAttribute("productTypeCategory"));
		if (typeFilters.containsValue( productType )) {
			return true;
		}
		return false;
	}

	public boolean getIsBrandSelected() {
		ProductBrand productBrand = ((ProductBrand) JSFUtil.getRequest().getAttribute("tempProductBrand"));
		if (brandFilters.containsValue( productBrand )) {
			return true;
		}
		return false;
	}

	public boolean getIsSizeSelected() {
		ProductSize productSize = ((ProductSize) JSFUtil.getRequest().getAttribute("tempProductSize"));
		if (sizeFilters.containsValue( productSize )) {
			return true;
		}
		return false;
	}

	public boolean getIsColourSelected() {
		ProductColour productColour = ((ProductColour) JSFUtil.getRequest().getAttribute("tempProductColour"));
		if (colourFilters.containsValue( productColour )) {
			return true;
		}
		return false;
	}

	public String getImageSrcByIsColourSelected() {
		if (getIsColourSelected()) {
			return "filter_on.png";
		} else {
			return "filter_off.png";
		}
	}

	public String getImageSrcByIsBrandSelected() {
		if (getIsBrandSelected()) {
			return "filter_on.png";
		} else {
			return "filter_off.png";
		}
	}

	public String getImageSrcByIsSizeSelected() {
		if (getIsSizeSelected()) {
			return "filter_on.png";
		} else {
			return "filter_off.png";
		}
	}

	public String getImageSrcByIsTypeSelected() {
		if (getIsTypeSelected()) {
			return "filter_on.png";
		} else {
			return "filter_off.png";
		}
	}

	public String getImageSrcByIsTypeCategorySelected() {
		if (getIsTypeCategorySelected()) {
			return "filter_on.png";
		} else {
			if (anyChildSelected()) {
				return "filter_half.png";
			} else {
				return "filter_off.png";
			}
		}
	}

	public void setIsBrandSelected(boolean b) { /* Empty */ }
	public void setIsTypeSelected(boolean b) { /* Empty */ }
	public void setIsSizeSelected(boolean b) { /* Empty */ }
	public void setIsColourSelected(boolean b) { /* Empty */ }


	/* isXEnabled methods decide whether to disable a checkbox, based on
	 * whether any products for the page use this filter */

	public boolean isTypeDisabled() {
		ProductType productType = (ProductType) JSFUtil.getRequest().getAttribute("tempProductType");
		return isTypeDisabled(productType);
	}

	public boolean isTypeDisabled(ProductType productType) {

		if( disabledTypeMap.containsValue( productType ) ) {
			if (getIsTypeSelected()) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	public boolean isTypeCategoryDisabled() {
		ProductType productType = (ProductType) JSFUtil.getRequest().getAttribute("productTypeCategory");

//		return false;
		if( disabledTypeMap.containsValue( productType ) && !anyChildEnabled()) {
			if (getIsTypeCategorySelected()) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	public boolean isBrandDisabled() {
		ProductBrand productBrand = (ProductBrand) JSFUtil.getRequest().getAttribute("tempProductBrand");
//		return false;
		if( disabledBrandMap.containsValue( productBrand ) ) {
			if (getIsBrandSelected()) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	public boolean isSizeDisabled() {
		ProductSize productSize = (ProductSize) JSFUtil.getRequest().getAttribute("tempProductSize");
//		return false;
		if( disabledSizeMap.containsValue( productSize ) ) {
			if (getIsSizeSelected()) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	public boolean isColourDisabled() {
		ProductColour productColour = (ProductColour) JSFUtil.getRequest().getAttribute("tempProductColour");
//		return false;
		if( disabledColourMap.containsValue( productColour ) ) {
			if (getIsColourSelected()) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	private class ProductSizeComparator implements Comparator<ProductSize> {
		@Override
		public int compare(ProductSize productSize1, ProductSize productSize2) {
			if( productSize1.getId() == null ) {
				return 1;
			}
			if( productSize2.getId() == null ) {
				return -1;
			}
			if( productSize1.getPositionIdx() > productSize2.getPositionIdx() ) {
				return 1;
			} else {
				return -1;
			}
		}
	}

	private static class RealizedProductPriceComparator implements Comparator {

		@Override
		public int compare(Object bean1, Object bean2) {
			return ((RealizedProduct)bean1).getDeterminedPrice().compareTo(((RealizedProduct)bean2).getDeterminedPrice());
		}

	}
	
	private class RealizedProductInfo {
		private Long id;
		private Long defaultRealizedProductId;
		private Long productInfoId;
		private Long productBrandId;
		private Long productColourId;
		private Long productSizeId;
		private Long productTypeId;
		private Set<RealizedProductInfo> relatedProducts;
		
		public RealizedProductInfo( Long id, Long defaultRealizedProductId, Long productInfoId, 
				Long productBrandId, Long productColourId, Long productSizeId, Long productTypeId ) {
			this.id = id;
			this.defaultRealizedProductId = defaultRealizedProductId;
			this.productInfoId = productInfoId;
			this.productBrandId = productBrandId;
			this.productColourId = productColourId;
			this.productSizeId = productSizeId;
			this.productTypeId = productTypeId;
			
		}
		
		public RealizedProductInfo() {
			
		}
		
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public Long getDefaultRealizedProductId() {
			return defaultRealizedProductId;
		}
		public void setDefaultRealizedProductId(Long defaultRealizedProductId) {
			this.defaultRealizedProductId = defaultRealizedProductId;
		}
		public Long getProductBrandId() {
			return productBrandId;
		}
		public void setProductBrandId(Long productBrandId) {
			this.productBrandId = productBrandId;
		}
		public Long getProductColourId() {
			return productColourId;
		}
		public void setProductColourId(Long productColourId) {
			this.productColourId = productColourId;
		}
		public Long getProductSizeId() {
			return productSizeId;
		}
		public void setProductSizeId(Long productSizeId) {
			this.productSizeId = productSizeId;
		}
		public Long getProductTypeId() {
			return productTypeId;
		}
		public void setProductTypeId(Long productTypeId) {
			this.productTypeId = productTypeId;
		}

		public Long getProductInfoId() {
			return productInfoId;
		}

		public void setProductInfoId(Long productInfoId) {
			this.productInfoId = productInfoId;
		}

		public Set<RealizedProductInfo> getRelatedProducts() {
			return relatedProducts;
		}

		public void setRelatedProducts(Set<RealizedProductInfo> relatedProducts) {
			this.relatedProducts = relatedProducts;
		}
	}
}















