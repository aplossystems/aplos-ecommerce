package com.aplos.ecommerce.beans.product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import com.aplos.cms.interfaces.GeneratorMenuItem;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.DynamicMetaValues;
import com.aplos.common.annotations.persistence.AnyMetaDef;
import com.aplos.common.annotations.persistence.Cache;
import com.aplos.common.annotations.persistence.Cascade;
import com.aplos.common.annotations.persistence.CascadeType;
import com.aplos.common.annotations.persistence.CollectionOfElements;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.FetchType;
import com.aplos.common.annotations.persistence.Inheritance;
import com.aplos.common.annotations.persistence.InheritanceType;
import com.aplos.common.annotations.persistence.JoinColumn;
import com.aplos.common.annotations.persistence.JoinTable;
import com.aplos.common.annotations.persistence.ManyToAny;
import com.aplos.common.annotations.persistence.ManyToMany;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.OneToMany;
import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosAbstractBean;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.CustomerReview;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.enums.JsfScope;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.ecommerce.beans.EcommerceShoppingCartItem;
import com.aplos.ecommerce.beans.IncludedProduct;
import com.aplos.ecommerce.beans.IncludedProductGroup;
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.beans.amazon.AmazonBrowseNode;
import com.aplos.ecommerce.interfaces.RealizedProductRetriever;
import com.aplos.ecommerce.utils.EcommerceUtil;

@Entity
@Cache
@Inheritance(strategy=InheritanceType.JOINED)
@DynamicMetaValueKey(oldKey="PRODUCT_INFO")
public class ProductInfo extends AplosBean implements RealizedProductRetriever, GeneratorMenuItem {

	private static final long serialVersionUID = -6901207608469984077L;

	//TODO: These top two fields are part of a temporary hack
	private BigDecimal price;
	private BigDecimal productCost;
	private BigDecimal crossoutPrice;

	private long lowStockThreshold = 4;
	@Column(columnDefinition="LONGTEXT")
	private String info1;
	@Column(columnDefinition="LONGTEXT")
	private String info2;
	@Column(columnDefinition="LONGTEXT")
	private String info3;
	@Column(columnDefinition="LONGTEXT")
	private String shortDescription;
	@CollectionOfElements
	private List<String> searchKeywordList;
	@Column(columnDefinition="LONGTEXT")
	private String longDescription;
	@Column(columnDefinition="LONGTEXT")
	private String salesComments;
	@CollectionOfElements
	private List<String> bulletPointList;
	private boolean isKitItem = false;
	@ManyToOne(fetch=FetchType.LAZY)
	private ProductSizeChart productSizeChart;
	@ManyToAny( metaColumn = @Column( name = "optionalAccessory_type" ), fetch=FetchType.LAZY )
    @AnyMetaDef( idType = "long", metaType = "string", metaValues = { /* Meta Values added in at run-time */ } )
    @JoinTable( inverseJoinColumns = @JoinColumn( name = "optionalAccessory_id" ) )
	@Cache
	@DynamicMetaValues
	private List<RealizedProductRetriever> optionalAccessoriesList;
	private boolean isAddedToAmazon = false;

	@ManyToMany(fetch=FetchType.LAZY)
	@Cascade({CascadeType.ALL})
	@Cache
	private List<IncludedProductGroup> includedProductGroups;

	@OneToMany(fetch=FetchType.LAZY)
	@Cascade({CascadeType.ALL})
	@Cache
	private List<IncludedProduct> includedProducts;

	@Column(columnDefinition="LONGTEXT")
	private String metaDescription;

	@ManyToOne(fetch=FetchType.LAZY)
	@Cascade({CascadeType.ALL})
	private Product product;
	private boolean isFreePostage = false;
	private boolean isOnStock = false;
	private String mapping;

	@Column(columnDefinition="LONGTEXT")
	private String accessoriesIncluded;

	private String suppliersPartNumber;
	private String suppliersPartNumberNotes;

	@ManyToOne(fetch=FetchType.LAZY)
	private RealizedProduct defaultRealizedProduct;

	@ManyToMany(fetch=FetchType.LAZY)
	private List<AmazonBrowseNode> amazonBrowseNodes;

	@Transient
	private Map<ProductColour, RealizedProduct> includedProductColourMap = new HashMap<ProductColour, RealizedProduct>();
	@Transient
	private List<CustomerReview> productReviews;


	public ProductInfo() {
	}
	
	@Override
	public <T> T initialiseNewBean() {
		T productInfo = super.initialiseNewBean();
		optionalAccessoriesList = new ArrayList<RealizedProductRetriever>();
		longDescription="";
		info1="";
		info2="";
		info3="";
		shortDescription="";
		setSearchKeywordList(new ArrayList<String>());
		includedProducts = new ArrayList<IncludedProduct>();
		includedProductGroups = new ArrayList<IncludedProductGroup>();
		bulletPointList = new ArrayList<String>();
		return productInfo;
	}

	public ProductInfo getCopy() {
		try {
			ProductInfo productInfo = (ProductInfo) clone();
			productInfo.setId( null );
			productInfo.clearFieldsAfterCopy();
			productInfo.setProduct(getProduct().getCopy());
			List<IncludedProduct> newIncludedProducts = new ArrayList<IncludedProduct>();
			for (IncludedProduct included : getIncludedProducts()) {
				newIncludedProducts.add(included.getCopy());
			}
			productInfo.setIncludedProducts(newIncludedProducts);

			List<IncludedProductGroup> newIncludedProductGroups = new ArrayList<IncludedProductGroup>();
			for (IncludedProductGroup group : getIncludedProductGroups()) {
				newIncludedProductGroups.add(group.getCopy());
			}
			productInfo.setIncludedProductGroups(newIncludedProductGroups);

			List<RealizedProductRetriever> newOptionalAccessoriesList = new ArrayList<RealizedProductRetriever>();
			for (RealizedProductRetriever accessory : getOptionalAccessoriesList()) {
				newOptionalAccessoriesList.add(accessory);
			}
			productInfo.setOptionalAccessoriesList(newOptionalAccessoriesList);

			List<AmazonBrowseNode> newAmazonBrowseNodes = new ArrayList<AmazonBrowseNode>();
			for (AmazonBrowseNode amazonBrowseNode : getAmazonBrowseNodes()) {
				newAmazonBrowseNodes.add(amazonBrowseNode);
			}
			productInfo.setAmazonBrowseNodes(newAmazonBrowseNodes);

			List<String> newBullets = new ArrayList<String>();
			for (String bullet : getBulletPointList()) {
				newBullets.add(bullet);
			}
			productInfo.setBulletPointList(newBullets);

			List<String> newSearchKeywordList = new ArrayList<String>();
			for (String searchKeyword : getSearchKeywordList()) {
				newSearchKeywordList.add(searchKeyword);
			}
			productInfo.setSearchKeywordList(newSearchKeywordList);
			return productInfo;
		} catch( CloneNotSupportedException cnsEx ) {
			ApplicationUtil.getAplosContextListener().handleError( cnsEx );
		}
		return null;
	}
	
	@Override
	public String getFullMenuItemImageUrl() {
		return null;
	}
	
	@Override
	public String getGeneratorMenuItemMapping() {
		return null;
	}
	
	@Override
	public String getGeneratorMenuUrl() {
		return EcommerceUtil.getEcommerceUtil().getProductMapping(retrieveRealizedProduct(null));
	}
	
	@Override
	public String getName() {
		return getDisplayName();
	}

	public boolean isIncludedProductsContaining( RealizedProductRetriever realizedProductRetriever ) {
		if( getIncludedProduct( realizedProductRetriever ) == null ) {
			return false;
		} else {
			return true;
		}
	}

	public IncludedProduct getIncludedProduct( RealizedProductRetriever realizedProductRetriever ) {
		for( IncludedProduct tempIncludedProduct : getIncludedProducts() ) {
			if( tempIncludedProduct.getRealizedProductRetriever().equals( realizedProductRetriever ) ) {
				return tempIncludedProduct;
			}
		}
		return null;
	}

	public boolean isIncludedProductGroupsContaining( ProductGroup productGroup ) {
		if( getIncludedProductGroup( productGroup ) == null ) {
			return false;
		} else {
			return true;
		}
	}

	public IncludedProductGroup getIncludedProductGroup( ProductGroup productGroup ) {
		for( IncludedProductGroup tempIncludedProductGroup : getIncludedProductGroups() ) {
			if( tempIncludedProductGroup.getProductGroup().equals( productGroup ) ) {
				return tempIncludedProductGroup;
			}
		}
		return null;
	}

	public List<RealizedProduct> getRealizedOptionalAccessoriesList( RealizedProduct realizedProduct ) {
		List<RealizedProduct> realizedOptionalAccessoriesList = new ArrayList<RealizedProduct>();
		for( RealizedProductRetriever retrievableProduct : getOptionalAccessoriesList() ) {
			realizedOptionalAccessoriesList.add( retrievableProduct.retrieveRealizedProduct(realizedProduct) );
		}

		return realizedOptionalAccessoriesList;
	}

	public List<RealizedProduct> getRealizedIncludedProducts( RealizedProduct realizedProduct ) {
		List<RealizedProduct> realizedIncludedProducts = new ArrayList<RealizedProduct>();
		for( IncludedProduct includedProduct : getIncludedProducts() ) {
			realizedIncludedProducts.add( includedProduct.getRealizedProductRetriever().retrieveRealizedProduct(realizedProduct) );
		}

		return realizedIncludedProducts;
	}

	public int getReviewCount() {
		if (productReviews == null) {
			productReviews = CustomerReview.retrieveCustomerReviewListStatic(this);
		}
		return productReviews.size();
	}

	public double getReviewAverage() {
		if (productReviews == null) {
			productReviews = CustomerReview.retrieveCustomerReviewListStatic(this);
		}
		if (productReviews.size()==0) {
			return 0.0;
		}
		BigDecimal stackedScore = new BigDecimal(0);
		for (CustomerReview review : productReviews) {
			stackedScore.add(review.getScore());
		}
		return stackedScore.divide(new BigDecimal(productReviews.size()), 2, RoundingMode.HALF_EVEN).doubleValue();
	}

	// To allow overridding by parent classes
	public void updateCartItemValues( EcommerceShoppingCartItem cartItem ) {}

	@Override
	public void saveBean( SystemUser currentUser ) {
		if (getMapping() == null || getMapping().equals( "" )) {
			setMapping( createMapping());
		} else {
			//remove anything nasty
			setMapping( CommonUtil.makeSafeUrlMapping( getMapping() ) );
			if (getMapping() == null || getMapping().equals( "" )) {
				setMapping( createMapping());
			}
		}

		super.saveBean(currentUser);
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product newProduct) {
		this.product = newProduct;
	}

	public String getInfo1() {
		return info1;
	}

	public void setInfo1(String newInfo) {
		this.info1 = newInfo;
	}

	public String getInfo2() {
		return info2;
	}

	public void setInfo2(String newInfo) {
		this.info2 = newInfo;
	}

	public String getInfo3() {
		return info3;
	}

	public void setInfo3(String newInfo) {
		this.info3 = newInfo;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String newInfo) {
		this.shortDescription = newInfo;
	}

	public String getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String newInfo) {
		this.longDescription = newInfo;
	}

	public List<RealizedProductRetriever> getSortedOptionalAccessoriesList() {
		List<RealizedProductRetriever> sortedOptionalAccessoriesList = new ArrayList<RealizedProductRetriever>( optionalAccessoriesList );
		Collections.sort( sortedOptionalAccessoriesList, new Comparator<RealizedProductRetriever>() {
			@Override
			public int compare(RealizedProductRetriever o1, RealizedProductRetriever o2) {
				return  CommonUtil.compare( o1.retrieveRealizedProduct(null).determineItemCode(), o2.retrieveRealizedProduct(null).determineItemCode() );
			}
		});
		return sortedOptionalAccessoriesList;
	}

	public List<RealizedProductRetriever> getOptionalAccessoriesList() {
		return optionalAccessoriesList;
	}

	public boolean getIsOptionalAccessoriesSet() {
		return (optionalAccessoriesList.size() > 0);
	}

	public void setOptionalAccessoriesList(List<RealizedProductRetriever> newList) {
		this.optionalAccessoriesList = newList;
	}

	public String createMapping() {

		String value;
		if (product.getProductBrand() != null && product.getProductBrand().getName() != null) {
			value = product.getProductBrand().getName() + "-" + product.getName();
		} else {
			value = product.getName();
		}

		String _mapping = CommonUtil.makeSafeUrlMapping(value);

		BeanDao productInfoDao = new BeanDao( ProductInfo.class );
		productInfoDao.setSelectCriteria( "bean.mapping" );
		productInfoDao.addWhereCriteria( "bean.mapping LIKE :mapping" );
		productInfoDao.setNamedParameter( "mapping", _mapping + "%'");
		List<String> mappings = productInfoDao.getResultFields();

		int count = 0;
		String newmapping = _mapping;
		while (mappings.contains( newmapping )) {
			newmapping = _mapping.concat( String.valueOf(++count) );
		}
		return newmapping;
	}

	public void setMapping(String mapping) {
		this.mapping = mapping;
	}

	public String getMapping() {
		return mapping;
	}

	public String getMappingOrId() {
		if (mapping != null && !mapping.equals("")) {
			return mapping;
		} else {
			return String.valueOf(getId());
		}
	}

	public void setDefaultRealizedProduct(RealizedProduct defaultRealizedProduct) {
		this.defaultRealizedProduct = defaultRealizedProduct;
	}

	public RealizedProduct getDefaultRealizedProduct() {
		return defaultRealizedProduct;
	}

	//finds itself a shiny new RP when the existing default one is deactivated
	public void takeNewDefaultRealizedProduct() {
		BeanDao realizedProductDao = new BeanDao( RealizedProduct.class );
		realizedProductDao.addWhereCriteria( "bean.productInfo.id=" + this.getId() );
		RealizedProduct newDefaultRealizedProduct = realizedProductDao.getFirstBeanResult();
		if (newDefaultRealizedProduct != null) {
			this.setDefaultRealizedProduct(newDefaultRealizedProduct);
			this.saveDetails();
		}
	}
	
	@Override
	public void addToScope(JsfScope associatedBeanScope) {
		super.addToScope(associatedBeanScope);
		addToScope( CommonUtil.getBinding( ProductInfo.class ), this, associatedBeanScope );
	}

	public void setIsFreePostage(boolean isFreePostage) {
		this.isFreePostage = isFreePostage;
	}

	public boolean getIsFreePostage() {
		return isFreePostage;
	}

	public void setSuppliersPartNumberNotes(String suppliersPartNumberNotes) {
		this.suppliersPartNumberNotes = suppliersPartNumberNotes;
	}

	public String getSuppliersPartNumberNotes() {
		return suppliersPartNumberNotes;
	}

	public void setSuppliersPartNumber(String suppliersPartNumber) {
		this.suppliersPartNumber = suppliersPartNumber;
	}

	public String getSuppliersPartNumber() {
		return suppliersPartNumber;
	}

	public void setAccessoriesIncluded(String accessoriesIncluded) {
		this.accessoriesIncluded = accessoriesIncluded;
	}

	public String getAccessoriesIncluded() {
		return accessoriesIncluded;
	}


	public void setIncludedProducts(List<IncludedProduct> includedProducts) {
		this.includedProducts = includedProducts;
	}

	public List<IncludedProduct> getIncludedProducts() {
		return includedProducts;
	}

	public void setSalesComments(String salesComments) {
		this.salesComments = salesComments;
	}

	public String getSalesComments() {
		return salesComments;
	}

	public void setBulletPointList(List<String> bulletPointList) {
		this.bulletPointList = bulletPointList;
	}

	public List<String> getBulletPointList() {
		return bulletPointList;
	}

	public void addBulletPoint(String newBullet) {
		if (getBulletPointList() == null) {
			setBulletPointList(new ArrayList<String>());
		}
		getBulletPointList().add(newBullet);
	}

	public void addSearchKeyword(String newSearchKeyword) {
		if (getSearchKeywordList() == null) {
			setSearchKeywordList(new ArrayList<String>());
		}
		getSearchKeywordList().add(newSearchKeyword);
	}

	@Override
	public RealizedProduct retrieveRealizedProduct(RealizedProduct product) {
		RealizedProduct realizedProduct = null;
		ProductColour productColour = null;
		if (product != null && product.getProductColour() != null) {
			productColour = product.getProductColour();
			realizedProduct = includedProductColourMap.get(productColour);
			if (realizedProduct != null) {
				return realizedProduct;
			} else {
				BeanDao dao = new BeanDao(RealizedProduct.class);
				dao.setWhereCriteria("bean.productInfo.id=" + this.getId());
				dao.addWhereCriteria("bean.productColour.id=" + productColour.getId());
				dao.setMaxResults(1);
				//have to do via the list otherwise get 'query did not return a unique result'
				//despite the max results 1
				List<RealizedProduct> listed = dao.setIsReturningActiveBeans(true).getAll();
				if (listed.size() > 0) {
					realizedProduct = listed.get(0);
				}
			}
		}
		if (realizedProduct == null) {
			realizedProduct = getDefaultRealizedProduct();
		}
		if (productColour != null && realizedProduct != null) {
			includedProductColourMap.put(productColour, realizedProduct);
		}
		return realizedProduct;
	}

	@Override
	public String getDisplayName() {
		return getProduct().getDisplayName();
	}

	@Override
	public SelectItem[] getSelectItemBeansWithNotSelected( Class<? extends AplosAbstractBean> lookupBeanClass, String notSelectedStr ) {
		BeanDao productDao = new BeanDao(ProductInfo.class);
		productDao.setSelectCriteria( "bean.id, p.id, p.name, bean.active" );
		productDao.addQueryTable( "p", "bean.product" );
		return getSelectItemBeansWithNotSelected( productDao.getAll(),notSelectedStr);
	}

	public void setIncludedProductGroups(List<IncludedProductGroup> includedProductGroups) {
		this.includedProductGroups = includedProductGroups;
	}

	public List<IncludedProductGroup> getIncludedProductGroups() {
		return includedProductGroups;
	}

	public void setKitItem(boolean isKitItem) {
		this.isKitItem = isKitItem;
	}

	public boolean isKitItem() {
		return isKitItem;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setCrossoutPrice(BigDecimal crossoutPrice) {
		this.crossoutPrice = crossoutPrice;
	}

	public BigDecimal getCrossoutPrice() {
		return crossoutPrice;
	}


	public void setProductSizeChart(ProductSizeChart productSizeChart) {
		this.productSizeChart = productSizeChart;
	}

	public ProductSizeChart getProductSizeChart() {
		return productSizeChart;
	}

	public void setLowStockThreshold(long lowStockThreshold) {
		this.lowStockThreshold = lowStockThreshold;
	}

	public long getLowStockThreshold() {
		return lowStockThreshold;
	}

	public void setMetaDescription(String metaDescription) {
		this.metaDescription = metaDescription;
	}

	public String getMetaDescription() {
		return metaDescription;
	}

	public BigDecimal getProductCost() {
		return productCost;
	}

	public void setProductCost(BigDecimal productCost) {
		this.productCost = productCost;
	}

	public boolean isOnStock() {
		return isOnStock;
	}

	public void setOnStock(boolean isOnStock) {
		this.isOnStock = isOnStock;
	}

	public boolean isAddedToAmazon() {
		return isAddedToAmazon;
	}

	public void setAddedToAmazon(boolean isAddedToAmazon) {
		this.isAddedToAmazon = isAddedToAmazon;
	}

	public List<AmazonBrowseNode> getAmazonBrowseNodes() {
		return amazonBrowseNodes;
	}

	public void setAmazonBrowseNodes(List<AmazonBrowseNode> amazonBrowseNodes) {
		this.amazonBrowseNodes = amazonBrowseNodes;
	}

	public void setSearchKeywordList(List<String> searchKeywordList) {
		this.searchKeywordList = searchKeywordList;
	}

	public List<String> getSearchKeywordList() {
		return searchKeywordList;
	}

}

