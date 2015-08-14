package com.aplos.ecommerce.backingpage.product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.beans.Website;
import com.aplos.common.enums.DocumentType;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FileIoUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.beans.product.Product;
import com.aplos.ecommerce.beans.product.ProductInfo;
import com.aplos.ecommerce.beans.product.ProductType;
import com.aplos.ecommerce.module.EcommerceConfiguration;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=ProductInfo.class)
public class FullProductStackListPage extends ListPage {

	private static final long serialVersionUID = 1185962846378132379L;

	public FullProductStackListPage() {
		//super( ProductInfo.class );
		CommonUtil.removeCookie("productstack-main"); //(we're view scoped so this is fine)
		CommonUtil.removeCookie("productstack-images");
	}

	/**
	 * Export a feed of all products which would appear on list pages
	 * in a format suitable for conversion to product feeds
	 * Important: the columns CANNOT be changed as these are required
	 * columns, do not add columns here or change titles
	 * @return
	 */
	public String dumpXlsFeed() {
		String[] cols = {
			"Category", 				
			"Manufacturer", 			//* fields marked with * are mandatory and MUST be filled in
			"Title", 					//*
			"Product Description", 		//
			"Link", 					//*
			"Image",					//
			"SKU",						//*
			"Stock",					//  they only update the feed every 2-3 days
			"Condition",				//  New, Open Box, Refurbished, OEM, Used
			"Shipping Weight",			//  in pounds
			"Shipping Cost",			//  expects lowest available, value not required but useful if free shipping offered
			"Bid",						
			"Promotional Description",	
			"EAN/UPC",					
			"Price"						//*
		};
		List<String[]> rows = new ArrayList<String[]>();
		BeanDao realizedProductDao = new BeanDao(RealizedProduct.class);
		if (EcommerceConfiguration.getEcommerceSettingsStatic().isProductListGroupByColour()) {
			realizedProductDao.setWhereCriteria("bean.productInfo.defaultRealizedProduct.id=bean.id");
		}
		realizedProductDao.addWhereCriteria("bean.productInfo.product.isShowingOnWebsite=1");
		List<RealizedProduct> realizedProducts = realizedProductDao.setIsReturningActiveBeans(true).getAll();
		for (RealizedProduct realizedProduct : realizedProducts) {
			List<ProductType> productTypes = realizedProduct.getProductInfo().getProduct().getProductTypes();
			String category = "Misc.";
			String typeForUrl = null;
			for (ProductType type : productTypes) {
				typeForUrl = type.getMapping();
				if (typeForUrl == null || typeForUrl.equals("")) {
					typeForUrl = CommonUtil.makeSafeUrlMapping(type.getName());
				}
			}

			String manufacturerName;
			//ordinarily we would use something like 'menswear' or 'sportswear' here not brand but
			//we cant determine it here so need to improvise to keep correct format and still make sense
			//to a user (for seo)
			String manufacturerNameForUrl;
			if (realizedProduct.getProductInfo().getProduct().getProductBrand() != null) {
				manufacturerName = realizedProduct.getProductInfo().getProduct().getProductBrand().getMapping();
				if (manufacturerName==null || manufacturerName.equals("")) {
					manufacturerName = realizedProduct.getProductInfo().getProduct().getProductBrand().getName();
				}
				manufacturerNameForUrl = CommonUtil.makeSafeUrlMapping(manufacturerName);
			} else {
				manufacturerName = "";
				manufacturerNameForUrl = CommonUtil.makeSafeUrlMapping(Website.getCurrentWebsiteFromTabSession().getName());
			}
			String title = realizedProduct.getProductInfo().getProduct().getName();
			String productDescription = CommonUtil.getStringOrEmpty(realizedProduct.getProductInfo().getShortDescription());
			String serverUrl = JSFUtil.getServerUrl();
			if (!serverUrl.startsWith("http")) {
				serverUrl = "http://" + serverUrl;
			}
			if (!serverUrl.endsWith("/")) {
				serverUrl = serverUrl + "/";
			}
			String context = JSFUtil.getRequest().getContextPath();
			if (context.startsWith("/")) {
				context = context.substring(1);
			} else if (context.endsWith("/")) {
				context = context.substring(0, context.length()-1);
			}
			String urlToProductView = serverUrl + context + "/product/" + manufacturerNameForUrl + "/" + typeForUrl + "/" + realizedProduct.getProductInfo().getMappingOrId() + ".aplos";
			String imageDetailsId="missing";
			FileDetails largeImage = realizedProduct.getImageDetailsByKey("LARGE_IMAGE");
			if (largeImage != null) {
				imageDetailsId = String.valueOf(largeImage.getId());
			} else if (realizedProduct.getDefaultImageDetails() != null) {
				imageDetailsId = String.valueOf(realizedProduct.getDefaultImageDetails().getId());
			} else if (realizedProduct.getImageDetailsList() != null && realizedProduct.getImageDetailsList().size() > 0) {
				imageDetailsId = String.valueOf(realizedProduct.getImageDetailsList().get(0).getId());
			}

			String urlToImage="";
			if (!imageDetailsId.equals("missing")) {
				urlToImage = serverUrl + context + "/media/?filename=realizedProductImages/" + imageDetailsId + "_large.jpg&provideDefaultIfMissing=true";
			}
			String sku = realizedProduct.determineItemCode();
			if (sku == null || sku.equals("")) {
				//not all sites use item codes but we have to pass one, so invent one
				sku = realizedProduct.getProductInfo().getId() + "INTERNAL" + realizedProduct.getId();
			}
			String stock = "";
			String condition = "New";
			String shippingWeight = FormatUtil.formatTwoDigit(realizedProduct.determineWeight());
			//there's way too many factors to consider for shipping cost, so i'm only going to pass a value
			String shippingCost = "";
			if (isFreeShippingAvailable(realizedProduct.getProductInfo())) {
				shippingCost = "0";
			}
			String bid = ""; 
			String promotionalDescription="";
			String eanUpc = String.valueOf(realizedProduct.getProductInfo().getId()); // we might want to use isbn etc if this were a book
			String price;
			if (realizedProduct.getSitewideDiscount() != null) {
				price = FormatUtil.formatTwoDigit(realizedProduct.getPrice().subtract( realizedProduct.getPrice().divide(new BigDecimal(100)).multiply(realizedProduct.getSitewideDiscount()) ));
			} else {
				price = realizedProduct.getPriceStr();
			}
			rows.add(new String[] {
				category,
				manufacturerName,
				title,
				productDescription,
				urlToProductView,
				urlToImage,
				sku,
				stock,
				condition,
				shippingWeight,
				shippingCost,
				bid,
				promotionalDescription,
				eanUpc,
				price
			});
		}
		try {
			String filename = "product_feed_" + FormatUtil.formatDateForDB(new Date()) +".xls";
			JSFUtil.addMessage("The product feed for this centre has been exported", FacesMessage.SEVERITY_INFO);
			FileIoUtil.fileDownload(FileIoUtil.generateXlsFile(filename, cols, rows), DocumentType.MS_EXCEL_DOCUMENT);
		} catch (InputMismatchException ime) {
			ime.printStackTrace();
		}
		return null;
	}

	private boolean isFreeShippingAvailable(ProductInfo productInfo) {
		return EcommerceConfiguration.getEcommerceSettingsStatic().isEverythingFreeShipping() || (productInfo.getIsFreePostage()==true || (productInfo.getProduct().getProductBrand() != null && productInfo.getProduct().getProductBrand().getIsFreePostage()==true));
	}

	public String getProductTypesString() {
		RealizedProduct realizedProduct = JSFUtil.getBeanFromRequest("tableBean");
		if (realizedProduct.getProductInfo().getProduct().getProductTypes().size() > 0) {
			Iterator<ProductType> iterator = realizedProduct.getProductInfo().getProduct().getProductTypes().iterator();
			ProductType productType;
			String productTypes = "";
			while (iterator.hasNext()) {
				productType = iterator.next();
				productTypes += productType.getName();
				if (iterator.hasNext()) {
					productTypes += ", ";
				}
			}
			return productTypes;
		}
		return "";
	}

	public String duplicateProductInfoWithPrices() {
		ProductInfo productInfo = (ProductInfo) JSFUtil.getRequest().getAttribute("tableBean");
		ProductInfo loadedProductInfo = new BeanDao(ProductInfo.class).get(productInfo.getId());
//		HibernateUtil.initialise( loadedProductInfo, true );
		ProductInfo newProductInfo = loadedProductInfo.getCopy();
		if (newProductInfo.getDefaultRealizedProduct() != null) {
			newProductInfo.setPrice(newProductInfo.getDefaultRealizedProduct().getPrice());
			newProductInfo.setCrossoutPrice(newProductInfo.getDefaultRealizedProduct().getCrossoutPrice());
		}
		newProductInfo.setDefaultRealizedProduct(null);
		newProductInfo.saveDetails();
		return null;
	}

	public void goToNewKit() {
		ProductInfo productInfo = new ProductInfo().<ProductInfo>initialiseNewBean();
		productInfo.setProduct(new Product().<Product>initialiseNewBean());
		productInfo.setKitItem(true);
		productInfo.addToScope();
		JSFUtil.redirect(FullKitStackEditPage.class);
	}

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new FullStackLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class FullStackLazyDataModel extends AplosLazyDataModel {

		private static final long serialVersionUID = -5735612507922636760L;

		public FullStackLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
			getBeanDao().setSelectCriteria( "bean.id, bean.product.name, pb.name, bean.active" );
			getBeanDao().addQueryTable( "pb", "bean.product.productBrand" );

			//don't select gift vouchers
			StringBuffer whereHql = new StringBuffer( "bean.id NOT IN (select innerBean.id FROM " );
			whereHql.append( AplosBean.getTableName( ProductInfo.class ) + " innerBean left join innerBean.product.productTypes ptype" );
			if (EcommerceConfiguration.getEcommerceConfiguration().getGiftVoucherProductType() != null) {
				whereHql.append( " WHERE ptype.id = "  + EcommerceConfiguration.getEcommerceConfiguration().getGiftVoucherProductType().getId() + ")" );
			} else {
				whereHql.append( ")" );
			}
			getBeanDao().setWhereCriteria( whereHql.toString() );

//			getBeanDao().addAliasesForOptimisation( "pb", "product.productBrand" );
			setEditPageClass( FullProductStackEditPage.class );
		}

		@Override
		public String getSearchCriteria() {
			return "bean.id LIKE :similarSearchText OR bean.product.name LIKE :similarSearchText OR pb.name LIKE :similarSearchText";
		}
		
		@Override
		public AplosBean selectBean() {
			ProductInfo productInfo = (ProductInfo) JSFUtil.getRequest().getAttribute( "tableBean" );
			BeanDao dao = new BeanDao(ProductInfo.class);
			ProductInfo loadedProductInfo = dao.get(productInfo.getId());
//			HibernateUtil.initialise( loadedProductInfo, true );
			loadedProductInfo.addToScope();
			if (loadedProductInfo.isKitItem()) {
				JSFUtil.redirect(FullKitStackEditPage.class);
			} else {
				JSFUtil.redirect( determineEditPageClass( loadedProductInfo ) );
			}
			return loadedProductInfo;
		}

		@Override
		public void goToNew() {
			ProductInfo productInfo = new ProductInfo().<ProductInfo>initialiseNewBean();
			productInfo.setProduct(new Product().<Product>initialiseNewBean());
			productInfo.addToScope();
			JSFUtil.redirect( determineEditPageClass( productInfo ) );
		}

	}

}
