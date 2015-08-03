package com.aplos.ecommerce.scheduledjobs;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;

import com.aplos.common.ScheduledJob;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.threads.JobScheduler;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FileIoUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.beans.product.ProductColour;
import com.aplos.ecommerce.beans.product.ProductInfo;
import com.aplos.ecommerce.beans.product.ProductType;
import com.aplos.ecommerce.module.EcommerceConfiguration;

@Entity
public class TabDelimitedProductFeedJob extends ScheduledJob<Boolean> {
	private String serverUrl;
	private String contextPath;
	private String websiteName;

	/**
	 * Dump a product feed in tab delimited format to tab-delimited-feed.txt
	 */
	public TabDelimitedProductFeedJob(String serverUrl, String contextPath, String websiteName) {
		this.serverUrl = serverUrl;
		this.contextPath = contextPath;
		this.websiteName = websiteName;
	}
	
	@Override
	public Boolean executeCall() throws Exception {

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
				"EAN/UPC",					//  only really applies to entertainment goods
				"Price"						//*
			};
			List<String[]> rows = new ArrayList<String[]>();
			BeanDao realizedProductDao = new BeanDao(RealizedProduct.class);
			if (EcommerceConfiguration.getEcommerceSettingsStatic().isProductListGroupByColour()) {
				realizedProductDao.setWhereCriteria("bean.productInfo.defaultRealizedProduct.id=bean.id");
			}
			realizedProductDao.addWhereCriteria("bean.productInfo.product.isShowingOnWebsite=1");
			List<RealizedProduct> realizedProducts = realizedProductDao.setIsReturningActiveBeans(true).getAll();
			Map<ProductInfo,ArrayList<ProductColour>> coloursCovered = new HashMap<ProductInfo,ArrayList<ProductColour>>();
			for (RealizedProduct realizedProduct : realizedProducts) {
				if (!EcommerceConfiguration.getEcommerceSettingsStatic().isProductListGroupByColour()) {
					ArrayList<ProductColour> currentColours = coloursCovered.get(realizedProduct.getProductInfo());
					if (currentColours == null) {
						currentColours = new ArrayList<ProductColour>();
					}
					if (!currentColours.contains(realizedProduct.getProductColour())) {
						currentColours.add(realizedProduct.getProductColour());
						coloursCovered.put(realizedProduct.getProductInfo(), currentColours);
					} else {
						//we dont want multiple where there are several sizes for this
						//colour/p.info combination
						continue;
					}
				}
				List<ProductType> productTypes = realizedProduct.getProductInfo().getProduct().getProductTypes();
				String category = "Misc.";
				String typeForUrl = null;
				for (ProductType type : productTypes) {
					typeForUrl = type.getMapping();
					if (typeForUrl == null || typeForUrl.equals("")) {
						typeForUrl = type.getMapping();
						if (typeForUrl == null || typeForUrl.equals("")) {
							typeForUrl = CommonUtil.makeSafeUrlMapping(type.getName());
						}
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
					manufacturerNameForUrl = CommonUtil.makeSafeUrlMapping(websiteName);
				}
				String title = realizedProduct.getProductInfo().getProduct().getName();
				String productDescription = CommonUtil.getStringOrEmpty(realizedProduct.getProductInfo().getShortDescription());
				if (!serverUrl.startsWith("http")) {
					serverUrl = "http://" + serverUrl;
				}
				if (!serverUrl.endsWith("/")) {
					serverUrl = serverUrl + "/";
				}
				if (contextPath.startsWith("/")) {
					contextPath = contextPath.substring(1);
				}
				if (!contextPath.endsWith("/")) {
					contextPath = contextPath + "/";
				}
				String serverContext = serverUrl + contextPath;
				serverContext = serverContext.replaceAll("//", "/");
				serverContext = serverContext.replaceAll("http:/", "http://");
				String urlToProductView = serverContext + manufacturerNameForUrl + "/" + typeForUrl + "/" + realizedProduct.getProductInfo().getMappingOrId() + ".aplos?type=product";
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
					urlToImage = serverContext + "media/?filename=realizedProductImages/" + imageDetailsId + "_large.jpg&provideDefaultIfMissing=true";
				}

				String sku = realizedProduct.determineItemCode();
				if (sku == null || sku.equals("")) {
					//not all sites use item codes but we have to pass one, so invent one
					sku = realizedProduct.getProductInfo().getId() + "SKU";
					sku += realizedProduct.getId();
					if (!EcommerceConfiguration.getEcommerceSettingsStatic().isProductListGroupByColour() && realizedProduct.getProductColour() != null) {
						sku += "C" + realizedProduct.getProductColour().getId();
					}
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
				//this is relative to webroot - so that it can be accessed as a feed
				String filename = "tab-delimited-feed.txt";
				FileIoUtil.generateTabDelimitedFile(filename, cols, rows);
			} catch (InputMismatchException ime) {
				ime.printStackTrace();
			}
		return true;
	}

	private boolean isFreeShippingAvailable(ProductInfo productInfo) {
		return EcommerceConfiguration.getEcommerceSettingsStatic().isEverythingFreeShipping() || (productInfo.getIsFreePostage()==true || (productInfo.getProduct().getProductBrand() != null && productInfo.getProduct().getProductBrand().getIsFreePostage()==true));
	}

	@Override
	public Long getIntervalQuantity(Date previousExecutionDate) {
		return JobScheduler.oneDayInMillis() * 5; //weekly as a minimum 
	}

	@Override
	public Integer getIntervalUnit() {
		return Calendar.MILLISECOND;
	}

	@Override
	public Calendar getFirstExecutionTime() {
		Calendar cal = Calendar.getInstance();
		//if we set this timezone we get randomness because GMT is ambiguous (ie it changes for BST and can mean UTC or UC1)
		//cal.setTimeZone(TimeZone.getTimeZone("GMT"));
		FormatUtil.resetTime(cal);
		cal.set(Calendar.HOUR_OF_DAY, 14);
		cal.set(Calendar.MINUTE, 26);
		return cal;
	}

}
