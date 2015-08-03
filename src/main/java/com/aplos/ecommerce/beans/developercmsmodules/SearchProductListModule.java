package com.aplos.ecommerce.beans.developercmsmodules;

import java.util.Arrays;
import java.util.List;

import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.appconstants.EcommerceAppConstants;
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.beans.product.ProductInfo;

@Entity
@DynamicMetaValueKey(oldKey="SEARCH_LIST_MODULE")
public class SearchProductListModule extends ProductListModule {

	private static final long serialVersionUID = -1461213329489741600L;

//	@Transient
//	private String searchTerm="";

	/* This constructor is required by getDeveloperModuleCPHUrlList()
	 * in the Page class
	 */
	public SearchProductListModule() {
		super();
	}
	
	@Override
	public String getFrontEndBodyName() {
		return "productListBody";
	}

	@Override
	public String getName() {
		return "Product search results";
	}

	@Override
	public void addSearchParameters(BeanDao aqlBeanDao) {
		if( Arrays.asList( aqlBeanDao.getNamedParameters() ).contains( "searchTerm" ) ) {
			String searchTerm = (String) JSFUtil.getFromTabSession(EcommerceAppConstants.SEARCH_TERM);
			aqlBeanDao.setNamedParameter( "searchTerm", "%" + searchTerm + "%" );
		}
	}

	@Override
	public String getUnfilteredProductWhereClaus() {
		String sql = " AND pi.active=true AND (LOWER(sk) LIKE :searchTerm"
		+ " OR LOWER(pi.shortDescription) LIKE :searchTerm"
		+ " OR LOWER(pi.product.name) LIKE :searchTerm"
		+ " OR LOWER(pi.product.productBrand.name) LIKE :searchTerm"
		+ " OR LOWER(rp.productColour.name) LIKE :searchTerm"
		+ " OR LOWER(rp.itemCode) LIKE :searchTerm"
		+ " OR LOWER(pi.product.itemCode) LIKE :searchTerm )";
		return sql;
	}

	@SuppressWarnings("unchecked")
	public List<RealizedProduct> getProductList (String sortSQL, int[] limits, String brandIds, String typeIds, String colourIds, String sizeIds, String keywords) {
		BeanDao realizedProductDao = new BeanDao( RealizedProduct.class );
		realizedProductDao.addQueryTable( "pt", "rp.productInfo.product.productTypes" );
		
		StringBuffer strBuf = new StringBuffer( "bean.id IN " );
		strBuf.append( "(SELECT pi.defaultRealizedProduct.id FROM " ).append( ProductInfo.class.getSimpleName() ).append( " pi " );
		strBuf.append( "JOIN pi.product.productTypes AS pt WHERE " );
		strBuf.append( "pi.active=true AND (LOWER(sk) LIKE '%" ).append( keywords );
		//+ "%' OR LOWER(pi.longDescription) LIKE '%" + keywords
		strBuf.append( "%' OR LOWER(pi.shortDescription) LIKE '%" ).append( keywords );
		strBuf.append( "%' OR LOWER(pi.product.name) LIKE '%" ).append( keywords );
		strBuf.append( "%' OR LOWER(pi.product.productBrand.name) LIKE '%" ).append( keywords ).append( "%')) " );
		strBuf.append( "OR LOWER(bean.productColour.name) LIKE '%" ).append( keywords ).append( "%') " );

		addWhereClauseIdStrings(realizedProductDao, brandIds, typeIds, colourIds, sizeIds);
		realizedProductDao.addWhereCriteria( strBuf.toString() );
		realizedProductDao.setGroupBy( "bean.productInfo.id" + sortSQL.replace("defaultRp", "bean") );
		realizedProductDao.setFirstRowIdx(limits[0]);
		realizedProductDao.setMaxResults(limits[1]);
		return realizedProductDao.getAll();
	}

	@Override
	public int getProductCount(String keywords, String brandIds, String typeIds, String colourIds, String sizeIds) {
		BeanDao realizedProductDao = new BeanDao( RealizedProduct.class );
		realizedProductDao.setCountFields( "DISTINCT bean");
		realizedProductDao.addQueryTable( "pt", "bean.productInfo.product.productTypes" );
		StringBuffer whereStrBuf = new StringBuffer( "bean.productInfo.defaultRealizedProduct.id=bean.id AND (bean.id IN " );
		whereStrBuf.append( "(SELECT pi.defaultRealizedProduct.id FROM " ).append( ProductInfo.class.getSimpleName() ).append( " pi " );
		whereStrBuf.append( "JOIN pi.product.productTypes AS pt WHERE " );
		whereStrBuf.append( "pi.active=true AND (LOWER(sk) LIKE '%" ).append( keywords );
		//+ "%' OR LOWER(pi.longDescription) LIKE '%" + keywords
		whereStrBuf.append( "%' OR LOWER(pi.shortDescription) LIKE '%" ).append( keywords );
		whereStrBuf.append( "%' OR LOWER(pi.product.name) LIKE '%" ).append( keywords );
		whereStrBuf.append( "%' OR LOWER(pi.product.productBrand.name) LIKE '%" ).append( keywords ).append( "%')) " );
		whereStrBuf.append( "OR LOWER(bean.productColour.name) LIKE '%" ).append( keywords ).append( "%') " );

		realizedProductDao.addWhereCriteria( whereStrBuf.toString() );
		addWhereClauseIdStrings(realizedProductDao, brandIds, typeIds, colourIds, sizeIds);
		
		return realizedProductDao.getCountAll();
	}

}
