package com.aplos.ecommerce.beans.developercmsmodules;

import java.util.List;

import com.aplos.cms.beans.developercmsmodules.ConfigurableDeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.interfaces.PlaceholderContent;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.DynamicMetaValues;
import com.aplos.common.annotations.persistence.Any;
import com.aplos.common.annotations.persistence.AnyMetaDef;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.JoinColumn;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.developermodulebacking.frontend.ProductListFeDmb;
import com.aplos.ecommerce.interfaces.ProductListRoot;

@Entity
@DynamicMetaValueKey(oldKey="PRODUCT_LIST_MODULE")
public class ProductListModule extends ConfigurableDeveloperCmsAtom implements PlaceholderContent {

	private static final long serialVersionUID = -2323713296848079498L;
	
	@Any( metaColumn = @Column( name = "productListRoot_type" ) )
    @AnyMetaDef( idType = "long", metaType = "string", metaValues = {
    		/* Meta Values added in at run-time */ } )
    @JoinColumn(name="productListRoot_id")
	@DynamicMetaValues
	private ProductListRoot productListRoot;

	/* This constructor is required by getDeveloperModuleCPHUrlList()
	 * in the Page class
	 */
	public ProductListModule() {
		super();
	}

	public ProductListModule(ProductListRoot productListRoot) {
		super();
		setProductListRoot( productListRoot );
	}

	@Override
	public boolean initModule( boolean isFrontEnd, boolean isRequestPageLoad ) {
//		HibernateUtil.initialise( this, true );
		if ( isFrontEnd ) {
			return initFrontend( isRequestPageLoad );
		} else {
			return initBackend();
		}
	}

	public String getUnfilteredProductWhereClaus() {
		return getProductListRoot().getProductListUnfilteredWhereClause();
	}

	public ProductListRoot getProductListRoot() {
		return productListRoot;
	}

	@Override
	public String getName() {
		return "Product list";
	}

	public List<RealizedProduct> defaultSort(List<RealizedProduct> unsortedList) {
		ProductListFeDmb productListFeDmb = (ProductListFeDmb) JSFUtil.getFromTabSession( ProductListFeDmb.class );
		return productListFeDmb.defaultSort( unsortedList );
	}

	@Override
	public boolean initFrontend( boolean isRequestPageLoad ) {
		return super.initFrontend( isRequestPageLoad );
	}

	public String getPageTitle() {
		return getProductListRoot().getName();
	}

	public void addSearchParameters( BeanDao aqlBeanDao ) {
	}

	public List<Long> getProductIdList(String sortSQL, String brandIds, String typeIds, String colourIds, String sizeIds ) {
		BeanDao realizedProductDao = new BeanDao( RealizedProduct.class );
		realizedProductDao.setSelectCriteria( "bean.productInfo.defaultRealizedProduct.id" );
		addDefaultRealizedProductFromClaus(realizedProductDao, brandIds, typeIds, colourIds, sizeIds);
		addExtendedWhereClause(realizedProductDao);
		realizedProductDao.setGroupBy( "bean.productInfo.id" + sortSQL );
		return realizedProductDao.getResultFields();
	}

	public void addExtendedWhereClause( BeanDao realizedProductDao ) {
	}

	public void addWhereClauseIdStrings(BeanDao realizedProductDao, String brandIds, String typeIds, String colourIds, String sizeIds) {
		if (brandIds.length() > 0) {
			realizedProductDao.addWhereCriteria("bean.productInfo.product.productBrand.id IN (" + brandIds + ")");
		}

		if (typeIds.length() > 0) {
			StringBuffer whereStrBuf = new StringBuffer("pt.id IN (");
			whereStrBuf.append( typeIds );
			whereStrBuf.append(") OR pt.parentProductType.id IN (");
			whereStrBuf.append( typeIds );
			whereStrBuf.append( ")" );
			realizedProductDao.addWhereCriteria( whereStrBuf.toString() );
		}

		if (colourIds.length() > 0) {
			realizedProductDao.addWhereCriteria( "bean.productColour.id IN (" + colourIds + ")");
		}

		if (sizeIds.length() > 0) {
			realizedProductDao.addWhereCriteria( "bean.productSize.id IN (" + sizeIds + ")");
		}
	}

	public void addDefaultRealizedProductFromClaus(BeanDao realizedProductDao, String brandIds, String typeIds, String colourIds, String sizeIds) {
		realizedProductDao.addQueryTable( "pt", "rp.productInfo.product.productTypes" );
		addWhereClauseIdStrings(realizedProductDao, brandIds, typeIds, colourIds, sizeIds);

		realizedProductDao.addWhereCriteria( "bean.productInfo.active=true" );
	}

	public int getProductCount(String brandIds, String typeIds, String colourIds, String sizeIds, String realizedProductIdStr) {
		if( realizedProductIdStr != null ) {
			BeanDao realizedProductDao = new BeanDao( RealizedProduct.class );
			realizedProductDao.setCountFields( "distinct bean.productInfo.id" );
			addDefaultRealizedProductFromClaus(realizedProductDao, brandIds, typeIds, colourIds, sizeIds );
			realizedProductDao.addWhereCriteria( "bean.productInfo.defaultRealizedProduct.defaultImageDetails != null " );
			addExtendedWhereClause(realizedProductDao);
			return realizedProductDao.getCountAll();
		} else {
			return 0;
		}
	}

	public String getCategoryName() {
		return getProductListRoot().getDisplayName();
	}

	@Override
	public DeveloperCmsAtom getCopy() {
		ProductListModule productListModule = new ProductListModule(getProductListRoot());
		return productListModule;
	}

	public void setProductListRoot(ProductListRoot productListRoot) {
		this.productListRoot = productListRoot;
	}
}
