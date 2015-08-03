package com.aplos.ecommerce;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import org.apache.commons.lang.StringUtils;

import com.aplos.cms.PageDispatcher;
import com.aplos.cms.backingpage.ContentPage;
import com.aplos.cms.beans.CmsWebsite;
import com.aplos.cms.beans.pages.CmsPageRevision;
import com.aplos.cms.module.CmsModule;
import com.aplos.common.SiteBeanDao;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosSiteBean;
import com.aplos.common.beans.Website;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.common.utils.XmlEntityUtil;
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.beans.developercmsmodules.ProductListModule;
import com.aplos.ecommerce.beans.product.ProductBrand;
import com.aplos.ecommerce.beans.product.ProductType;
import com.aplos.ecommerce.developermodulebacking.frontend.ProductListFeDmb;
import com.aplos.ecommerce.developermodulebacking.frontend.SearchProductListFeDmb;
import com.aplos.ecommerce.interfaces.ProductListRoot;
import com.aplos.ecommerce.module.EcommerceConfiguration;

@ManagedBean(eager=true)
@ApplicationScoped
public class EcommercePageDispatcher extends PageDispatcher {

	public EcommercePageDispatcher() {	}

	@Override
	public CmsPageRevision getPageRevision(ContentPage contentPage, String mappingPath, String mapping) {

		boolean isDraft = false;
		if( JSFUtil.getLoggedInUser() != null && JSFUtil.getLoggedInUser().isLoggedIn() ) {
			isDraft = true;
		}

		//Pattern pattern = Pattern.compile("/(.*?)(/.*?)?(/.*)?/(.*?)?$");
		//Matcher m = pattern.matcher(mappingPath + mapping);
		String strippedMapping = mapping;
		if( strippedMapping.startsWith("/") ) {
			strippedMapping = strippedMapping.substring( 1 );
		}


		CmsPageRevision cmsPageRevision = null;

		if( EcommerceConfiguration.getEcommerceCprsStatic().getSearchResultsCpr() != null && strippedMapping.equals( EcommerceConfiguration.getEcommerceCprsStatic().getSearchResultsCpr().getCmsPage().getMapping())) {
			addProductListFeDmbToSession( SearchProductListFeDmb.class );
			cmsPageRevision = super.getPageRevision(contentPage, mappingPath, mapping);
		} else {
			/*
			 * We first check to see if the URL includes a queryString that indicates its a dynamic
			 * Ecommerce page, if so we attempt to load the ecommerce object and the corresponding
			 * page.  If not then we try to load a normal page, if the normal page is null, then
			 * we try to load the ecommerce page incase the queryString param was missing.
			 */
			String pageType = JSFUtil.getRequestParameter("type");
			if (pageType != null && (pageType.equals("product") || pageType.equals("category") || pageType.equals("brand"))) {
				if (pageType.equals("product")) {
					cmsPageRevision = loadIndividualProduct(contentPage, mappingPath, strippedMapping, isDraft);
				} else if (pageType.equals("brand")) {
					cmsPageRevision = loadCategory(contentPage, mappingPath, strippedMapping, isDraft, ProductBrand.class);
				} else if (pageType.equals("category")) {
					cmsPageRevision = loadCategory(contentPage, mappingPath, strippedMapping, isDraft, ProductType.class);
				}
			}

			if( cmsPageRevision == null ) {
				cmsPageRevision = super.getPageRevision(contentPage, mappingPath, mapping);
				Website website = Website.getCurrentWebsiteFromTabSession();
				boolean isPageNotFound = false;
				if( website != null ) {
					if( ((CmsWebsite) website).getPageNotFoundPage().equals( cmsPageRevision ) ) {
						isPageNotFound = true;
						cmsPageRevision = null;
					}
				}
				if( cmsPageRevision == null ) {
					cmsPageRevision = loadIndividualProduct(contentPage, mappingPath, strippedMapping, isDraft);
				}
				if( cmsPageRevision == null ) {
					cmsPageRevision = loadCategory(contentPage, mappingPath, strippedMapping, isDraft, ProductBrand.class);
				}
				if( cmsPageRevision == null ) {
					cmsPageRevision = loadCategory(contentPage, mappingPath, strippedMapping, isDraft, ProductType.class);
				}
				if( isPageNotFound && cmsPageRevision == null ) {
					cmsPageRevision = ((CmsWebsite) website).getPageNotFoundPage();
				}
			}
		}


		return cmsPageRevision;
	}

	private void addProductListFeDmbToSession( Class<? extends ProductListFeDmb> productListFeDmbClass ) {
		ProductListFeDmb productListFeDmb = (ProductListFeDmb) CmsModule.getDeveloperModuleBacking( productListFeDmbClass, null );
		if( JSFUtil.getFromTabSession( CommonUtil.getBinding( ProductListFeDmb.class ) ) == null ||
				!JSFUtil.getFromTabSession( CommonUtil.getBinding( ProductListFeDmb.class ) ).getClass().equals( productListFeDmbClass ) ) {
			JSFUtil.addToTabSession( CommonUtil.getBinding( ProductListFeDmb.class ), productListFeDmb );
			JSFUtil.addToTabSession( productListFeDmb );  // add under it's own binding also for the cmsAtoms to use the same one.
		}
	}

	private CmsPageRevision loadIndividualProduct(ContentPage contentPage, String mappingPath, String productInfoMapping, boolean isDraft) {

		if (productInfoMapping != null && productInfoMapping.startsWith("/")) {
			productInfoMapping = productInfoMapping.substring(1);
		}

		String whereMapping;
		try {
			/*
			 * Make sure that the encoding is the same as the encoding in the database
			 * column.
			 */
			productInfoMapping = URLDecoder.decode( productInfoMapping, "Latin1" );
		} catch( UnsupportedEncodingException useEx ) {
			ApplicationUtil.getAplosContextListener().handleError( useEx );
		}
		if (productInfoMapping.matches("([0-9]+([0-9]+)?)+")) {
            //we were handed a numeric string, treat it as an id
			whereMapping = "bean.productInfo.id=" + productInfoMapping;
        } else {
            //we were handed what should be a unique mapping
        	whereMapping = "bean.productInfo.mapping='" + productInfoMapping + "'";
        }

		String rpidParam = JSFUtil.getRequestParameter("rpid");
		Long rpid = null;
		if (rpidParam != null && !rpidParam.equals("")) {
			rpid = Long.parseLong(rpidParam);
		}


		BeanDao realizedProductDao = new BeanDao( RealizedProduct.class );
		if (rpid == null) {
			//try to load the default for that product info
			realizedProductDao.addWhereCriteria( "bean.productInfo.defaultRealizedProduct.id = bean.id AND " + whereMapping );
			
		} else {
			//try to load the correct colour
			realizedProductDao.addWhereCriteria( "bean.id=" + rpid );
		}


		//TODO: why is this a list? is this a hangover from a previous implementation - check this!
		@SuppressWarnings("unchecked")
		RealizedProduct realizedProduct = realizedProductDao.getFirstBeanResult();

		//if we didn't find the product try to load any other realized product that is in stock and assigned this product info
		if (realizedProduct == null) {
			realizedProductDao = new BeanDao( RealizedProduct.class );
			realizedProductDao.addWhereCriteria( whereMapping ); 
			realizedProduct = realizedProductDao.getFirstBeanResult();
		}

		String mapping = "";
		if (realizedProduct == null) {
			return null;
		} else {
			mapping = "product";
			//we might have got here from some page that edits this object in which case we want to see the live version
			RealizedProduct currRp = (RealizedProduct)JSFUtil.getBeanFromScope(RealizedProduct.class);
			//was checking !currRp.getProductInfo().equals(realizedProduct.getProductInfo())
			//but changed to comapring the realized product so urls can map to exact colours
			if (currRp == null || !currRp.equals(realizedProduct)) {
				realizedProduct.addToScope();
			}
		}
		//continue loading the page
		CmsPageRevision cmsPageRevision = contentPage.determinePage( mappingPath, mapping, isDraft );
		if( cmsPageRevision == null ) {
			return null;
		}
		cmsPageRevision.addToScope();
		addProductListFeDmbToSession( ProductListFeDmb.class );

		setSeoInformation(cmsPageRevision, realizedProduct);

		return cmsPageRevision;
	}

	public void setSeoInformation( CmsPageRevision cmsPageRevision, RealizedProduct realizedProduct ) {
		if( realizedProduct != null && realizedProduct.getProductInfo().getProduct().getName() != null ) {
			StringBuffer titleBuf = new StringBuffer( realizedProduct.getProductInfo().getProduct().getName() );
			ProductBrand productBrand = realizedProduct.getProductInfo().getProduct().getProductBrand();
			for (ProductType productType : realizedProduct.getProductInfo().getProduct().getProductTypes()) {
				titleBuf.append( " - " );
				if( productBrand != null ) {
					titleBuf.append( productBrand.getName() ).append( " " );
				}
				titleBuf.append( productType.getName() );
			}
			cmsPageRevision.setTitle(XmlEntityUtil.replaceCharactersWith(titleBuf.toString(), XmlEntityUtil.EncodingType.ENTITY));
			cmsPageRevision.setMetaDescription(realizedProduct.getProductInfo().getMetaDescription());
			cmsPageRevision.setMetaKeywords(StringUtils.join(realizedProduct.getProductInfo().getSearchKeywordList(),","));
		}
	}

	private CmsPageRevision loadCategory(ContentPage contentPage, String mappingPath, String categoryMapping, boolean isDraft, Class<? extends AplosSiteBean> productListRootClass) {

		if (categoryMapping != null && categoryMapping.startsWith("/")) {
			categoryMapping = categoryMapping.substring(1);
		}

		Long categoryId=null;
		ProductListRoot productListRoot = null;
		if (categoryMapping.matches("([0-9]+([0-9]+)?)+")) {
	        //we were handed a numeric string, treat it as an id
			categoryId = Long.parseLong(categoryMapping);
			productListRoot = (ProductListRoot) new SiteBeanDao( productListRootClass ).get( categoryId );
	    } else {
	    	productListRoot = (ProductListRoot) new SiteBeanDao( productListRootClass ).addWhereCriteria( "bean.mapping='" + categoryMapping + "'" ).getFirstBeanResult();
	    }
		String mapping = "";
		CmsPageRevision cmsPageRevision;
		if (productListRoot == null) {
			return null;
		} else {
			
			mapping = "category"; //this is a page mapping in the cms (with the product list module in it)
			//continue loading the page
			cmsPageRevision = contentPage.determinePage( mappingPath, mapping, isDraft );
			if( cmsPageRevision == null ) {
				return null;
			}
			cmsPageRevision = cmsPageRevision.getSaveableBean();
			
			//need to alter the atom instance on the fly
			ProductListModule productListModule = null;
			for( int i = 0, n = cmsPageRevision.getCmsAtomList().size(); i < n; i++ ) {
				if( cmsPageRevision.getCmsAtomList().get( i ) instanceof ProductListModule ) {
					productListModule = (ProductListModule) cmsPageRevision.getCmsAtomList().get( i );
					break;
				}
			}
			
			productListModule.setProductListRoot(productListRoot);
			if( !CommonUtil.isNullOrEmpty( productListRoot.getMetaTitle() ) ) {
				cmsPageRevision.setTitle( productListRoot.getMetaTitle() );
			} else {
				cmsPageRevision.setTitle(XmlEntityUtil.replaceCharactersWith(productListRoot.getName(), XmlEntityUtil.EncodingType.ENTITY));
			}
			cmsPageRevision.setMetaDescription( productListRoot.getMetaDescription() );
			CmsWebsite cmsWebsite = (CmsWebsite) Website.getCurrentWebsiteFromTabSession();
			cmsPageRevision.setMetaKeywords( cmsWebsite.appendMetaKeywords( productListRoot.getMetaKeywords() ) );

		}
		cmsPageRevision.addToScope();
		addProductListFeDmbToSession( ProductListFeDmb.class );

		return cmsPageRevision;
	}
}
