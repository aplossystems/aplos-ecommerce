package com.aplos.ecommerce.beans.ebay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.backingpage.product.RealizedProductListPage;
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.enums.EcommerceWorkingDirectory;
import com.aplos.ecommerce.utils.EcommerceUtil;
import com.ebay.sdk.ApiAccount;
import com.ebay.sdk.ApiContext;
import com.ebay.sdk.ApiCredential;
import com.ebay.sdk.ApiLogging;
import com.ebay.sdk.CallRetry;
import com.ebay.sdk.call.AddItemCall;
import com.ebay.sdk.call.GetCategoryFeaturesCall;
import com.ebay.sdk.call.VerifyAddItemCall;
import com.ebay.soap.eBLBaseComponents.AmountType;
import com.ebay.soap.eBLBaseComponents.BuyerPaymentMethodCodeType;
import com.ebay.soap.eBLBaseComponents.CategoryFeatureType;
import com.ebay.soap.eBLBaseComponents.CategoryType;
import com.ebay.soap.eBLBaseComponents.CountryCodeType;
import com.ebay.soap.eBLBaseComponents.CurrencyCodeType;
import com.ebay.soap.eBLBaseComponents.DetailLevelCodeType;
import com.ebay.soap.eBLBaseComponents.FeeType;
import com.ebay.soap.eBLBaseComponents.FeesType;
import com.ebay.soap.eBLBaseComponents.GalleryTypeCodeType;
import com.ebay.soap.eBLBaseComponents.InternationalShippingServiceOptionsType;
import com.ebay.soap.eBLBaseComponents.ItemType;
import com.ebay.soap.eBLBaseComponents.ListingDurationCodeType;
import com.ebay.soap.eBLBaseComponents.ListingTypeCodeType;
import com.ebay.soap.eBLBaseComponents.PictureDetailsType;
import com.ebay.soap.eBLBaseComponents.RefundOptionsCodeType;
import com.ebay.soap.eBLBaseComponents.ReturnPolicyType;
import com.ebay.soap.eBLBaseComponents.ReturnsWithinOptionsCodeType;
import com.ebay.soap.eBLBaseComponents.ShippingCostPaidByOptionsCodeType;
import com.ebay.soap.eBLBaseComponents.ShippingDetailsType;
import com.ebay.soap.eBLBaseComponents.ShippingRegionCodeType;
import com.ebay.soap.eBLBaseComponents.ShippingServiceCodeType;
import com.ebay.soap.eBLBaseComponents.ShippingServiceOptionsType;
import com.ebay.soap.eBLBaseComponents.ShippingTypeCodeType;
import com.ebay.soap.eBLBaseComponents.SiteCodeType;


@ManagedBean
@SessionScoped
@PluralDisplayName(name="Ebay managers")
public class EbayManager extends AplosBean {

	private static final long serialVersionUID = -58505977187961184L;

	//DEBUG OPTIONS
	private boolean useSandbox = false;
	private boolean useLocalhostImages = false; //as the images wont exist on your local computer

	//This is the Ebay Item we are currently managing
	private ItemType workingProduct;
	private final String CLIENT_NAME = "";
	//can not be more than 45 characters (30 on Taiwan site)
	private final String CLIENT_LOCATION = "";
	private final CountryCodeType CLIENT_COUNTRY = CountryCodeType.GB;
	//supplying this 'opts-in' to having their result shown on distance-from-postcode searches
	private final String CLIENT_POSTCODE = "SO41 9AA";
	private CurrencyCodeType CLIENT_CURRENCY = CurrencyCodeType.GBP;
	private final String EBAY_DEVID = "";
	private final String EBAY_API_WSDL_VERSION = "673"; //latest stable @ 04/11/2010
	private final int EBAY_API_TIMEOUT = 180000;
	//The site on which we are listing
	private final SiteCodeType EBAY_SITE_ID = SiteCodeType.UK;
	private ApiAccount ebayApiAccount;
	private ApiCredential ebayApiCredential;
	private String[] pictureURLs;;
	private ApiContext ebayApiContext;
	private ApiLogging ebayApiLogger;
	private EbayCategoryManager ebayCategoryManager;
	private RealizedProduct realizedProductAssociatedWithWorkingProduct = null;
	private String featureInfoCategory = "";
	private boolean internationalShippingOffered = false;
	private Double maxShippingCost = null;
	private double internationalShippingCharge = 0;
	private double domesticShippingCharge = 0;
	private final Integer MAX_TIME_TO_DISPATCH = 3;
	private final String CLIENT_URL;
	private final String CLIENT_PAYPAL_EMAIL;
	private final String EBAY_APPID;
	private final String EBAY_CERTID;
	private final String EBAY_USER_TOKEN;
	private final String EBAY_SERVER_URL;
	private final String SIGN_IN_URL;
	private final String EPS_SERVER_URL;

	public EbayManager() {

		if (useSandbox) {

			CLIENT_URL = "localhost:8080";
			CLIENT_PAYPAL_EMAIL = "fitzgerald29@hotmail.com"; //anthony
			EBAY_APPID = "AplosSys-3b8f-45c0-942f-ebb1b9ee6061";
			EBAY_CERTID = "860dd8cb-5c78-4515-b0ab-82d063c3638f";
			//testuser_aplossystems user account (sandbox)
			//if you use eBay token as credential to make the API call you only need to set the eBayToken property. Otherwise you need to use setApiAccount() and seteBayAccount()
			EBAY_USER_TOKEN = "AgAAAA**AQAAAA**aAAAAA**FUtRTA**nY+sHZ2PrBmdj6wVnY+sEZ2PrA2dj6wFk4CoAJSBoASdj6x9nY+seQ**5VsBAA**AAMAAA**wNOn2gFcWOZbx2PVK3SfncHcEZbe9G1oNbt8YvvlDbYOoPyiYcknFRldcJSMWtZcCYZzfGyF+4hhxI8m9//X9zVpx/dhoY6QAWmWHWfjKl43C4SV70lh84iXdipV2UrVp8pnhmYpC6QMidBimPsr8/sKhQoHy45iyzsv68u2pVEWT9j0X5WJ1kqfTe2TS0GxF/6zaCsM25QdyskJtGCnxXh+4yq3ABO5h6QL733dgiORKb9yXQF/vpio0ACe4h7HQUpefvTjedPg9BGhS1Hq+agi2sfH4Nm9yuHLN1VpYHh6HSAU4y7GrHU+Y8VD6XMJmF667zpyorxK6UyMz5s33HCUC/ECu4lcy9ErFgBOQ8K3LstrShkMtVJNhI19z4sBkOXAEE8ihmgZ0wh6k8R+cT0F0B9Z5R3S890qVOvWxgRuASWJBYFLgQfoCW+0e/EfB9WklSb9wmL7n5TApjcVZ/lXN3DcxpsogP6I9miw3R84OHcw7izkEZVKlEK9C8p0/lzW9lt3zQxAziGdIXN0Y/8bD7qBzlwHNuK3Q6EfeO9YAtbrFC/5D0GtdT6FmH/stYmGblvNxjfIReZFI6OHgOg+2/B1iZHqlrzCAv79MKMSGvA9h+OBDaeTbgMPHdlYxOuSO+VzdZQkUpzlpMkouIUMbI4/LsKRQCJyE0gK+pjx7EH5lDKws9hlHQtSsnINRKSBDPNUJxjOh0EHUS4mNFnyEpUL9R6wZtn9Ha3qLCuTw47ovimubGYjotm+hwsS";
			EBAY_SERVER_URL = "https://api.sandbox.ebay.com/wsapi";
			SIGN_IN_URL = "http://my.sandbox.ebay.com/ws/eBayISAPI.dll?SignIn";
			EPS_SERVER_URL = "https://api.sandbox.ebay.com/ws/api.dll";

		} else {

			//External links should use full URL without protocol, do not end with a slash
			CLIENT_URL = "www.stanwells.co.uk";
			CLIENT_PAYPAL_EMAIL = "info@stanwells.co.uk";
			EBAY_APPID = "AplosSys-98e9-4a69-8e9e-a61f5d29df54";
			EBAY_CERTID = "473e8d60-2b11-496e-8dea-f00ecf2f44ce";
			//testuser_aplossystems user account (production / live)
			//EBAY_USER_TOKEN = "AgAAAA**AQAAAA**aAAAAA**rC5YTA**nY+sHZ2PrBmdj6wVnY+sEZ2PrA2dj6AEkoKhD5CCqAydj6x9nY+seQ**9ukAAA**AAMAAA**6bXsR5UeYi963rutDdso6RnCMhM84Hj5bprJx3WISKi5jXT9DHSY/7a3qVvhQiV8AFPyr6W3fudwWKfoOgGpO7MFdBBj1GtHLqmgSRHL7xZRhrk+0JPfw/QP5m8ZrV+2Z0uB/M2vlQy0buwx0Rfb3Z1UE8/330y1pfDuFUjUfCyP4JXrbs23tKDiSm3fX0xco+8zaglco2P420lMm2CO52HMV5/zU8MDV7y+3jg+BPYAV5utWdSxsedRU77/rs7PHNmBcCv2tTd5cjQdLOlgQR/W6FYhur+BpXw6fERyT311EACyndeF7ECrUlXHOUR4J7OgPkas4lslkWTBBX+zyd9oFVhFpBGm6GG0Ppk2aTnLYdzwb7GbaLqRvI5fO0QFCjMbC+yBNyJe7QmKp6uCvoQzLjaU0O8t5M9z0akkeKYkFDc2fGH1cejVLIUZxgJGslDiXdeOcHN+l9JsFP/qZjoYZo9wHKbmbrIpH5HJkwige0pV/6y44zMzYygDyKGX5A1lOsQZXwDkfDeHdissZPeQ2Dswxct35VOYWOTKeS+idFtSAuBK8Jwa2WsmPZ+F+quIRRfESRfnb/nlCdm/dilRUIF6oFRB709maUtRdRtBZF2lKGlSLe9OaE1WZsUqra9UdsUAxSydDzpuOgwTH1y0eAWHKDZmZRjsEJSBPkv2ciBbSNwkrAN3M/XcC4vSIyG+6ILxAnZbge2a29hysrCS1GzIW6k2wubDiq1wtvygAsb0Qh0kTIsyZDjkl37N";
			//aplos systems / anthony
			//EBAY_USER_TOKEN = "AgAAAA**AQAAAA**aAAAAA**zbxaTA**nY+sHZ2PrBmdj6wVnY+sEZ2PrA2dj6wJl4WjAJCBpAqdj6x9nY+seQ**9ukAAA**AAMAAA**yrmXYuiy4qdupgFku/ddlocGmi0uOyVAdMgRgoKIpYvjfpWUlCHcSXN4Vp//4vOwbr0qbb6ArniHdhRfv5jO4ynSEnR22Xd3FPUAZOstc/VZh/mRyJAxELzs3ag3Q0/6C5kwe3VLoYXbaQD17cec/jzGp5sS+HOulvkdiIHpRqgQRDfhfb8AyMmK4K3wiA5012bnOaFl8G0fcG2/SMRiQwlqo9Fp+sCDS4HZ4reXR7nyD1mfnWbTQGYvAKw9sCPYx4Cbx2wVBy68jkVRYJM7dYdNOjwpCie9gsgUUGNZM3GxxKGZt6PI3KgI0DQcYol0xmTofeS7bgCHQSGapXcwDpixHDA7yvAO7Ln/6/R4bLRa+m8P5+XiVQBDKlAJRYi5w7Ur/38Ju9ZrDyA38STPeTRVSeECtg9T1y3op0Rhi+HXtkvYXsHwEvCpfKHuyD95volmeMLT3/SeRfqEpPIchjiQsfAnchdaAILCWmKV5siw912WKOb4a0A933PX6EZOVeM2ovXogQN6grJE54aRbQJ2wJh9VUV87z0nowbqnRO8om1Aqcy3xz1zUs14uDKki5PyQgnggFpLa1Pj3BVPU5JQjaUacyn4D+x0UEPMoSt5kMLWdyJ4cqm7rvHy83Rno5xhOgvvrsbTed52eBapJxxnXAO833e+0taiqkaBvBLdUsdZFrESvGAQGZiwOBfQirlQ/KNu27FgsTfH/Pwfue01HnJfzDWgQNGmpigg8C+5k4ZfkblQafRRGSeIcEKb";
			//Stanwells / stanwells_fashion_4_woman - Expires 2012-04-19 13:05:34
			EBAY_USER_TOKEN = "AgAAAA**AQAAAA**aAAAAA**niPITA**nY+sHZ2PrBmdj6wVnY+sEZ2PrA2dj6AEk4OkD5SHqQidj6x9nY+seQ**9ukAAA**AAMAAA**kyukDSujDSG3i9uJ7V2y4EavXLu4wauCzkrZCQGbOkozXnk6oLHf7KhAM+nrFie3qGpWFAnhoBYO8SSbT6q8+X0AGq9UcI5bTuum+Sre8rMGb7ETk+1pxrSeYek3l/C6kFOZcO5GSNwvGAVgtVguqxWItXyL1iYw+HIe3HCtyefsnhonj8iB34Asus1iZ+UCaaUcels9USaFmfMFL/TbWfnrS81FRYIaeFue6VIOT72gwQbRcHGiD6zAe6AmnMIlBcGQjoxxcB7kJGkek1y+Xoj2nIbitmWPApFVAe43B47tC/8TXD6xR2X4HckEWHagPW6UZ2zG2K9KnXyg31gx3UTwCgC9o6uO81WXuWyN8Wlx3kmTdoN66OVYJHU8PHpmC1e69/WvEjS/tyi/HcJnmoMFHK0EjpwzM4CGsRSRS3iSP3dvTneDWVYZMgy8I0LPSS/ZVPeAi7wNUUseOsPPvtRGQoZt3EPSO9xhjeGLhmNSybHOLq6NnfyDkleH4k1Mpqhuw4ktHL/y4R8UNjiS+xZpmvfoSrv6HqpGgGSUG8P+It9zY/xMfJ7uzKd5EU8aHiYFvAvVQrJoeLOnLtqRNuUC1/lETwi8fiYekRrqq5HmgLyCRG5tONywM70ZEB5Pctg86wR3OD+YJ3ueWJEd7/JXG/Acl6FyrVJOAicpIn8hh15636RHO8Suu65P4u68oPH7y+I2//3YNinj/NnfeP1w4aaCTBhzzKYR+W1z6O40SNodZ8fywL4WZUgDIoX/";
			EBAY_SERVER_URL = "https://api.ebay.com/wsapi";
			SIGN_IN_URL = "https://signin.ebay.com/ws/eBayISAPI.dll?SignIn";
			EPS_SERVER_URL = "https://api.ebay.com/ws/api.dll";
		}
		this.initApiAccount();
		this.initCategoryManager();
	}

	//All API calls have to include this set of standard data as well as
	//any data specific to the call type. This initialisation sets up the
	//standard data as well as authentication
	public void initApiAccount() {

		ebayApiAccount = new ApiAccount();
		ebayApiAccount.setDeveloper( EBAY_DEVID );
		ebayApiAccount.setApplication( EBAY_APPID );
		ebayApiAccount.setCertificate( EBAY_CERTID );
		ebayApiCredential = new ApiCredential();
		ebayApiCredential.setApiAccount( ebayApiAccount );
		ebayApiCredential.seteBayToken( EBAY_USER_TOKEN );
		ebayApiContext = new ApiContext();
		ebayApiContext.setApiCredential(ebayApiCredential);
		ebayApiContext.setApiServerUrl( EBAY_SERVER_URL );
		// set timeout in milliseconds - 3 minutes
		ebayApiContext.setTimeout( EBAY_API_TIMEOUT );
		// set wsdl version number - the API we are calling
		ebayApiContext.setWSDLVersion( EBAY_API_WSDL_VERSION );
		// turn on logging (displays the SOAP request and response and any exceptions in console)
		ebayApiLogger = new ApiLogging();
		ebayApiContext.setApiLogging(ebayApiLogger);
		ebayApiContext.setSite(EBAY_SITE_ID);
		ebayApiContext.setErrorLanguage("en_US");
		ebayApiContext.setSignInUrl(SIGN_IN_URL);
		ebayApiContext.setRuName("Aplos");
		ebayApiContext.setEpsServerUrl(EPS_SERVER_URL);
		configureCallRetries(ebayApiContext);
	}

	@SuppressWarnings("rawtypes")
	public void configureCallRetries(ApiContext ebayApiContext) {
		try {
		      CallRetry cr = new CallRetry();
		      cr.setMaximumRetries(3);
		      cr.setDelayTime(1000); // Wait for one second between each retry-call.
		      String[] apiErrorCodes = new String[] {
		          "10007", // "Internal error to the application."
		          "931", // "Validation of the authentication token in API request failed."
		          "521", // Test of Call-Retry: "The specified time window is invalid."
		          "124" // Test of Call-Retry: "Developer name invalid."
		      };
		      cr.setTriggerApiErrorCodes(apiErrorCodes);
		      // Set trigger exceptions for CallRetry.
		      java.lang.Class[] tcs = new java.lang.Class[] {
		          com.ebay.sdk.SdkSoapException.class
		      };
		      cr.setTriggerExceptions(tcs);
		      ebayApiContext.setCallRetry(cr);
		} catch (Exception e) {
		      e.printStackTrace();
		}
	}

	public void initCategoryManager() {
		setEbayCategoryManager((EbayCategoryManager) new BeanDao( EbayCategoryManager.class ).getFirstBeanResult());

		if (getEbayCategoryManager() == null) {
			setEbayCategoryManager(new EbayCategoryManager());
		}

		getEbayCategoryManager().setEbayApiContext(ebayApiContext);

		getEbayCategoryManager().getCurrentCategories();

	}

	/* This method converts realizedProducts into the itemType objects which need to be supplied in Ebay calls
	 * This method creates the object with defaults which will then be manipulated via the UI
	 */
	public ItemType realizedProductToEbayItemType(RealizedProduct realizedProduct) {

		this.setRealizedProductAssociatedWithWorkingProduct(realizedProduct);
		ItemType itemType = new ItemType();
		String param;

		//Ebay Required Arguments

			//Title : <brand> <item> (<colour>, <size>) : E.g. Sweet-Mini Maids Dress (Black, Size 10)
			param = realizedProduct.getProductInfo().getProduct().getProductBrand().getName() + " " + realizedProduct.getProductInfo().getProduct().getName() + " (" + realizedProduct.getProductColour().getName() + ", " + realizedProduct.getProductSize().getName() + ")";
			//NOTE: Listing titles are limited to 55 characters.
			if (param.length() > 55) {
				param = param.substring(0, 54);
			}
			itemType.setTitle(param);
			//HTML content should be well formed and use relative widths
			//External links should use full URL including protocol and target _blank
			//Images and other fixed-size objects should not exceed 700 pixels in width
			param = "<html><body><p>" + realizedProduct.getProductInfo().getLongDescription() + "</p><p>"
			+ ((realizedProduct.getProductInfo().getInfo1().length() > 1)?realizedProduct.getProductInfo().getInfo1() + "</p><p>":"")
			+ ((realizedProduct.getProductInfo().getInfo2().length() > 1)?realizedProduct.getProductInfo().getInfo2() + "</p><p>":"")
			+ ((realizedProduct.getProductInfo().getInfo3().length() > 1)?realizedProduct.getProductInfo().getInfo3() + "</p><p>":"")
			+ "This item is sold by <a target=\"blank\" href=\"http://"
			+ CLIENT_URL + "\">" + CLIENT_NAME + "</a>, click to visit our site<br/>"
			+ "Or, view this item with more details and images, directly at our site, <a target=\"blank\" href=\"http://"
			+ CLIENT_URL + "/" + JSFUtil.getContextPath() + "/" + EcommerceUtil.getEcommerceUtil().getProductMapping(realizedProduct) + "\">here</a>"
			+ "</p></body></html>";
			itemType.setDescription(param);

			CategoryType cat = new CategoryType();
		    cat.setCategoryID("0");
		    itemType.setPrimaryCategory(cat);

		    itemType.setCurrency( CLIENT_CURRENCY );

			AmountType priceAmountType = new AmountType();
			//use the same currency type throughout
			priceAmountType.setCurrencyID( CLIENT_CURRENCY );
			priceAmountType.setValue(realizedProduct.getDeterminedPrice().doubleValue());
			itemType.setStartPrice(priceAmountType);
			itemType.setListingType(ListingTypeCodeType.FIXED_PRICE_ITEM);
			//duration is expressed as days
			itemType.setListingDuration(ListingDurationCodeType.DAYS_30.value());

			BuyerPaymentMethodCodeType[] paymentMethods = {
					BuyerPaymentMethodCodeType.PAY_PAL,
					BuyerPaymentMethodCodeType.CC_ACCEPTED
			};

			itemType.setPaymentMethods(paymentMethods);
			itemType.setQuantity(realizedProduct.getQuantity());

			itemType.setRegionID("3");
			itemType.setLocation( CLIENT_LOCATION );
			itemType.setPostalCode( CLIENT_POSTCODE );
			itemType.setCountry( CLIENT_COUNTRY );

//			ShippingDetailsType shippingDetails = new ShippingDetailsType();
//			shippingDetails.setShippingServiceOptions(getSupportedShippingMethods());
//			shippingDetails.setShippingType(ShippingTypeCodeType.FLAT);
//			itemType.setShippingDetails(shippingDetails);

			//Measured in days
			itemType.setDispatchTimeMax(MAX_TIME_TO_DISPATCH);

			ReturnPolicyType returnPolicy = new ReturnPolicyType();
		    returnPolicy.setReturnsAcceptedOption("ReturnsAccepted");
		    returnPolicy.setReturnsWithinOption(ReturnsWithinOptionsCodeType.DAYS_14.value());
		    //This value gets ignored for the UK site
		    //returnPolicy.setRefundOption(RefundOptionsCodeType.MONEY_BACK.value());
		    returnPolicy.setShippingCostPaidByOption(ShippingCostPaidByOptionsCodeType.BUYER.value());
		    itemType.setReturnPolicy(returnPolicy);

		    List<FileDetails> imageDetailsList = realizedProduct.getImageDetailsList();
		    if ( imageDetailsList.size() == 0 ) {
		    	pictureURLs = new String[ 0 ];
		    	JSFUtil.addMessage("Warning: This item does not have any images attached yet, this may cause problems when listing on Ebay. To attach images go to Products and edit this item.", FacesMessage.SEVERITY_WARN);
			} else {
				if (useLocalhostImages || CLIENT_URL.equals("localhost:8080")) {
					//FOR DEBUGGING
					pictureURLs = new String[] {
						"C:/Program Files (x86)/work/stanwells/realizedProductImages/64_large.jpg",
						"C:/Program Files (x86)/work/stanwells/realizedProductImages/69_large.jpg"
					};
				} else {
					pictureURLs = new String[imageDetailsList.size()];
					String realizedProductDir = EcommerceWorkingDirectory.REALIZEDPRODUCT_IMAGE_DIR.getDirectoryPath(false);
					for (int i=0; i < imageDetailsList.size(); i++ ) {
						//file NOT url
						pictureURLs[i] = CommonUtil.appendServerWorkPath(  realizedProductDir ) + imageDetailsList.get(i).getFullFileUrl(true);
					}
				}
			}

		    PictureDetailsType pictureDetails = new PictureDetailsType();
//		    pictureDetails.setPictureURL(pictureURLs);
//		    pictureDetails.setPhotoDisplay(PhotoDisplayCodeType.NONE);
//		    pictureDetails.setPictureSource(PictureSourceCodeType.EPS);
//		    //pictureDetails.setPictureURL(0, CLIENT_URL + realizedProduct.getDefaultImageDetails().getFullLargeImageUrl());
		    pictureDetails.setGalleryType(GalleryTypeCodeType.GALLERY);
		    itemType.setPictureDetails(pictureDetails);
		    setOptions(itemType);

		return itemType;
	}

	public void setOptions() {
		setOptions(getWorkingProduct());
	}

	public void setOptions(ItemType itemToList) {
		//we do this check as the method will be called multiple times throughout
		//the lifecycle which causes a large delay if it has to call ebay 8 times
		if (itemToList.getPrimaryCategory().getCategoryID() != null && featureInfoCategory != itemToList.getPrimaryCategory().getCategoryID()) {
			//this section sets up any category specific features
			GetCategoryFeaturesCall features_call = new GetCategoryFeaturesCall( getEbayApiContext() );

			try {
				featureInfoCategory = itemToList.getPrimaryCategory().getCategoryID();
				features_call.setCategoryID(featureInfoCategory);
				features_call.setSite(EBAY_SITE_ID);
				features_call.setDetailLevel(new DetailLevelCodeType[] { DetailLevelCodeType.RETURN_ALL });
				features_call.getCategoryFeatures();
				/* Since we call GetCategoryFeatures for a specified category we
				 * just need the first category element here. We will get a NPE or ArrayIndexOutOfBoundsException
				 * if the category is not valid
				 */
				CategoryFeatureType thisCategoriesfeatures = null;
				try {
					thisCategoriesfeatures = features_call.getReturnedCategory()[0];
					if (thisCategoriesfeatures != null && thisCategoriesfeatures.getConditionEnabled() != null) {
						itemToList.setConditionID(1000); //1000 = New With Tags
					}
					if (thisCategoriesfeatures != null && thisCategoriesfeatures.getMaxFlatShippingCost() != null) {
						maxShippingCost = thisCategoriesfeatures.getMaxFlatShippingCost().getValue();
					} else {
						maxShippingCost = null;
					}

				} catch (ArrayIndexOutOfBoundsException e) {
					/* Do Nothing, The category being invalid will be reported on submission */
				} catch (Exception e) {
					e.printStackTrace();
				}


//				if (thisCategoriesfeatures != null && thisCategoriesfeatures.getMaxFlatShippingCost() != null) {
//					initSupportedShippingOptions(thisCategoriesfeatures.getMaxFlatShippingCost());
//				} else {
//					initSupportedShippingOptions(null);
//				}
			} catch (Exception e) {

				e.printStackTrace();
			}
		}
	}

	public void submitEbayListing() {
		submitEbayListing(getWorkingProduct());
	}

	public void submitEbayListing(ItemType itemToList) {

		//add valid paypal email address if we are offering paypal as a payment option
		for (BuyerPaymentMethodCodeType paymentType : itemToList.getPaymentMethods()) {
			if (paymentType == BuyerPaymentMethodCodeType.PAY_PAL) {
				itemToList.setPayPalEmailAddress(CLIENT_PAYPAL_EMAIL);
			}
		}

		setOptions();
		ShippingServiceOptionsType[] supportedShippingMethods = new ShippingServiceOptionsType[1];
		ShippingServiceOptionsType shippingOption = new ShippingServiceOptionsType();
		AmountType shippingAmountType = new AmountType();
		shippingAmountType.setCurrencyID( CLIENT_CURRENCY );
		if (maxShippingCost == null || domesticShippingCharge <= maxShippingCost) {
			shippingAmountType.setValue(domesticShippingCharge);
		} else {
			JSFUtil.addMessage("Ebay only allows a maximum shipping charge of " + maxShippingCost + " for this category. This maximum charge has been applied to the shipping options.", FacesMessage.SEVERITY_INFO);
			shippingAmountType.setValue(maxShippingCost);
		}
		shippingOption.setShippingServiceCost(shippingAmountType);
		shippingOption.setShippingServiceAdditionalCost(shippingAmountType);
		shippingOption.setShippingService(ShippingServiceCodeType.UK_SELLERS_STANDARD_RATE.value());
		//shippingOption.setShippingTimeMax(MAX_TIME_TO_DISPATCH);
		supportedShippingMethods[0] = shippingOption;
		ShippingDetailsType shippingDetails = new ShippingDetailsType();
		shippingDetails.setShippingServiceOptions(supportedShippingMethods);
		if (isInternationalShippingOffered()) {
			InternationalShippingServiceOptionsType[] itlSupportedShippingMethods = new InternationalShippingServiceOptionsType[1];
			InternationalShippingServiceOptionsType itlShippingOption = new InternationalShippingServiceOptionsType();
			AmountType itlShippingAmountType = new AmountType();
			itlShippingAmountType.setCurrencyID( CLIENT_CURRENCY );
			if (maxShippingCost == null || internationalShippingCharge <= maxShippingCost) {
				itlShippingAmountType.setValue(internationalShippingCharge);
			} else {
				JSFUtil.addMessage("Ebay only allows a maximum shipping charge of " + maxShippingCost + " for this category. This maximum charge has been applied to the international shipping options.", FacesMessage.SEVERITY_INFO);
				itlShippingAmountType.setValue(maxShippingCost);
			}
			itlShippingOption.setShippingServiceCost(itlShippingAmountType);
			shippingOption.setShippingServiceAdditionalCost(itlShippingAmountType);
			itlShippingOption.setShippingService(ShippingServiceCodeType.UK_SELLERS_STANDARD_INTERNATIONAL_RATE.value());
			itlShippingOption.setShipToLocation(new String[] {ShippingRegionCodeType.WORLDWIDE.value()});
			//itlShippingOption.setShippingTimeMax(MAX_TIME_TO_DISPATCH);
			itlSupportedShippingMethods[0] = itlShippingOption;
			shippingDetails.setInternationalShippingServiceOption(itlSupportedShippingMethods);
		}
		shippingDetails.setShippingType(ShippingTypeCodeType.FLAT);
		itemToList.setShippingDetails(shippingDetails);

		VerifyAddItemCall verification_call = new VerifyAddItemCall( getEbayApiContext() );
		verification_call.setItem(itemToList);
		verification_call.setPictureFiles(pictureURLs);
		try {
			//make sure the listing will be successful
			FeesType fees = verification_call.verifyAddItem();
			//now actually list the item (if verify was unsuccessful an exception will have been thrown to stop this)
			AddItemCall call = new AddItemCall( getEbayApiContext() );
			call.setItem(itemToList);
			call.setPictureFiles(pictureURLs);
			fees = call.addItem();
			FeeType[] allFees = fees.getFee();
			Double totalFee = new Double(0);
			for (int i=0; i < allFees.length; i++) {
				totalFee = new Double(totalFee + allFees[i].getFee().getValue());
			}
			JSFUtil.addMessage("Item Listed. Total Listing Fee: " + totalFee + " " + CLIENT_CURRENCY, FacesMessage.SEVERITY_INFO);
			realizedProductAssociatedWithWorkingProduct.setDateListedOnEbay(Calendar.getInstance().getTime());
			realizedProductAssociatedWithWorkingProduct.saveDetails();

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage().contains("PICTURE SERVICE UPLOAD ERROR")) {
				JSFUtil.addMessage("We were unable to find and upload the images attached to this item.", FacesMessage.SEVERITY_ERROR);
				//JSFUtil.addMessage(e.getMessage(), FacesMessage.SEVERITY_ERROR);
			} else if (e.getMessage().equals("The category is not valid, select another category.")) {
				/* com.ebay.sdk.ApiException: The category is not valid, select another category
				 * above exception indicates a category ebay has included in its Enum but doesn't yet support
				 */
				JSFUtil.addMessage("Sorry, Despite providing the option, Ebay does not yet support the selected category via API calls.", FacesMessage.SEVERITY_ERROR);
			} else if (e.getMessage().equals("ApiCall.execute() - HTTP transport error: java.net.ConnectException: Connection timed out: connect")) {
				JSFUtil.addMessage("Could not contact Ebay, the server or internet connection may be down.", FacesMessage.SEVERITY_ERROR);
			} else {
				JSFUtil.addMessage(e.getMessage(), FacesMessage.SEVERITY_ERROR);
			}
		}

	}

	public void setEbayApiContext(ApiContext ebayApiContext) {
		this.ebayApiContext = ebayApiContext;
	}

	public ApiContext getEbayApiContext() {
		return ebayApiContext;
	}

	public void setWorkingProduct(ItemType workingItem) {
		this.workingProduct = workingItem;
	}

	public ItemType getWorkingProduct() {
		return workingProduct;
	}

	public void cancelBtnAction() {
		JSFUtil.redirect(RealizedProductListPage.class);
	}

	public void setEbayCategoryManager(EbayCategoryManager ebayCategoryManager) {
		this.ebayCategoryManager = ebayCategoryManager;
	}

	public EbayCategoryManager getEbayCategoryManager() {
		return ebayCategoryManager;
	}

	public void setWorkingProductScheduleTime( Long dateInMillis ) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(dateInMillis);
		workingProduct.setScheduleTime(cal);
	}

	public Long getWorkingProductScheduleTime() {
		if (workingProduct.getScheduleTime() != null) {
			return workingProduct.getScheduleTime().getTimeInMillis();
		} else {
			return Calendar.getInstance().getTimeInMillis();
		}
	}

	public SelectItem[] getListingDateSelectItems() {
		//return full dates in single dropdown for the next 14 days,
		//inclusive of today, any further in the future is unsupported
		int maximimumScheduleDaysDifference = 14;
		/** TODO: Check if its actually 7 or 28 days ebay supports **/
		SelectItem[] selectItems = new SelectItem[maximimumScheduleDaysDifference];
		Calendar scheduleDate = Calendar.getInstance();
		//scheduleDate.set(Calendar.HOUR_OF_DAY,0);
		FormatUtil.resetTime( scheduleDate );
		scheduleDate.add(Calendar.HOUR_OF_DAY,1); //just to be safe (as we can't have a listing start in the past)
		selectItems[0] = new SelectItem(scheduleDate.getTimeInMillis(), FormatUtil.formatDate(scheduleDate.getTime()) + " (Today)");
		for (int i = 1; i < maximimumScheduleDaysDifference; i++) {
			scheduleDate.add(Calendar.DATE,1);
			selectItems[i] = new SelectItem(scheduleDate.getTimeInMillis(), FormatUtil.formatDate(scheduleDate.getTime()) + " (" + i + " days time)");
		}
		return selectItems;
	}
	public SelectItem[] getListingDurationSelectItems() {
		List<ListingDurationCodeType> valid_enums = new ArrayList<ListingDurationCodeType>();
		for (ListingDurationCodeType duration : ListingDurationCodeType.values()) {
			if (duration.value().startsWith("Days_")) {
				valid_enums.add(duration);
			}
		}
		SelectItem[] selectItems = new SelectItem[valid_enums.size()+1];
		for(int i = 0; i < valid_enums.size(); i++ ) {
			selectItems[i] = new SelectItem( valid_enums.get(i).value(), valid_enums.get(i).value().substring(5) + " days" );
		}
		selectItems[valid_enums.size()] = new SelectItem( ListingDurationCodeType.GTC.value(), "Good Until Cancelled" );
		return selectItems;
	}

	public SelectItem[] getReturnsSelectItems() {

		SelectItem[] selectItems = new SelectItem[2];
		selectItems[0] = new SelectItem( "ReturnsAccepted", "Returns Accepted" );
		selectItems[1] = new SelectItem( "ReturnsNotAccepted", "No Returns Accepted" );
		return selectItems;
	}

	public SelectItem[] getReturnsShippingPaidBySelectItems() {

		SelectItem[] selectItems = new SelectItem[2];
		selectItems[0] = new SelectItem( ShippingCostPaidByOptionsCodeType.BUYER.name(), "Buyer / Them" );
		selectItems[1] = new SelectItem( ShippingCostPaidByOptionsCodeType.SELLER.name(), "Seller / Us" );
		return selectItems;
	}

	public SelectItem[] getReturnsDurationSelectItems() {
		List<ReturnsWithinOptionsCodeType> valid_enums = new ArrayList<ReturnsWithinOptionsCodeType>();
		for (ReturnsWithinOptionsCodeType duration : ReturnsWithinOptionsCodeType.values()) {
			if (duration.value().startsWith("Days_")) {
				valid_enums.add(duration);
			}
		}
		SelectItem[] selectItems = new SelectItem[valid_enums.size()];
		for(int i = 0; i < valid_enums.size(); i++ ) {
			selectItems[i] = new SelectItem( valid_enums.get(i).name(), valid_enums.get(i).value().substring(5) + " days" );
		}
		return selectItems;
	}

	public SelectItem[] getReturnsTypeSelectItems() {

		SelectItem[] selectItems = new SelectItem[3];
		selectItems[0] = new SelectItem( RefundOptionsCodeType.EXCHANGE.name(), "Exchange" );
		selectItems[1] = new SelectItem( RefundOptionsCodeType.MERCHANDISE_CREDIT.name(), "Merchandise Credit" );
		selectItems[2] = new SelectItem( RefundOptionsCodeType.MONEY_BACK.name(), "Money Back" );
		return selectItems;
	}

	public static String camelCaseToSplitString(String inputString) {
		Pattern p = Pattern.compile("\\p{Lu}(\\p{Lu}*\\p{Ll}|\\d.)");
		Matcher m = p.matcher(inputString);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			if (m.group().length() > 1) {
				StringBuffer caps = new StringBuffer(m.group());
				m.appendReplacement(sb, caps.insert(m.group().length() - 2, ' ').toString());
			} else {
				m.appendReplacement(sb, ' ' + m.group());
			}
		}
		m.appendTail(sb);
		return sb.toString().trim();
	}

	public boolean isPaymentMethodSelected() {
		BuyerPaymentMethodCodeType paymentMethod = (BuyerPaymentMethodCodeType)JSFUtil.getRequest().getAttribute("paymentMethod");
		return isPaymentMethodSelected(paymentMethod);
	}

	public boolean isPaymentMethodSelected(BuyerPaymentMethodCodeType paymentMethod) {
		BuyerPaymentMethodCodeType[] selectedMethods = workingProduct.getPaymentMethods();
		for (int i=0; i < selectedMethods.length; i++) {
			if (selectedMethods[i].equals(paymentMethod)) {
				return true;
			}
		}
		return false;
	}

	public void setPaymentMethodSelected(boolean isSelected) {
		BuyerPaymentMethodCodeType paymentMethod = (BuyerPaymentMethodCodeType)JSFUtil.getRequest().getAttribute("paymentMethod");
		BuyerPaymentMethodCodeType[] selectedMethods = workingProduct.getPaymentMethods();
		BuyerPaymentMethodCodeType[] newSelectedMethods = selectedMethods;
		if (isSelected) {
			if (!isPaymentMethodSelected(paymentMethod)) {
				newSelectedMethods = new BuyerPaymentMethodCodeType[selectedMethods.length + 1];
				System.arraycopy(selectedMethods, 0, newSelectedMethods, 0, selectedMethods.length);
				newSelectedMethods[selectedMethods.length] = paymentMethod;
			}
		} else if (isPaymentMethodSelected(paymentMethod)) { //we only need to remove the element if it is present... obviously...
			newSelectedMethods = new BuyerPaymentMethodCodeType[selectedMethods.length - 1];
			int indexTracker = 0;
			/* Copy all elements across to the new array except the item we just unchecked */
			for (int i=0; i < selectedMethods.length; i++) {
				if (!selectedMethods[i].equals(paymentMethod)) {
					newSelectedMethods[indexTracker] = selectedMethods[i];
					indexTracker++;
				}
			}
		}
		workingProduct.setPaymentMethods(newSelectedMethods);
	}

	public String getPaymentMethodName() {
		return ((BuyerPaymentMethodCodeType)JSFUtil.getRequest().getAttribute("paymentMethod")).value().replace("Accepted", "");
	}

	public boolean isShippingMethodSelected() {
		ShippingServiceOptionsType shippingMethod = (ShippingServiceOptionsType)JSFUtil.getRequest().getAttribute("shippingMethod");
		return isShippingMethodSelected(shippingMethod);
	}

	public boolean isShippingMethodSelected(ShippingServiceOptionsType shippingMethod) {
		ShippingServiceOptionsType[] selectedMethods = workingProduct.getShippingDetails().getShippingServiceOptions();
		for (int i=0; i < selectedMethods.length; i++) {
			if (selectedMethods[i].equals(shippingMethod)) {
				return true;
			}
		}
		return false;
	}

	public void setShippingMethodSelected(boolean isSelected) {
		ShippingServiceOptionsType shippingMethod = (ShippingServiceOptionsType)JSFUtil.getRequest().getAttribute("shippingMethod");
		ShippingServiceOptionsType[] selectedMethods = workingProduct.getShippingDetails().getShippingServiceOptions();
		ShippingServiceOptionsType[] newSelectedMethods = selectedMethods;
		if (isSelected) {
			if (!isShippingMethodSelected(shippingMethod)) {
				newSelectedMethods = new ShippingServiceOptionsType[selectedMethods.length + 1];
				System.arraycopy(selectedMethods, 0, newSelectedMethods, 0, selectedMethods.length);
				newSelectedMethods[selectedMethods.length] = shippingMethod;
			}
		} else if (isShippingMethodSelected(shippingMethod)) { //we only need to remove the element if it is present... obviously...
			newSelectedMethods = new ShippingServiceOptionsType[selectedMethods.length - 1];
			int indexTracker = 0;
			/* Copy all elements across to the new array except the item we just unchecked */
			for (int i=0; i < selectedMethods.length; i++) {
				if (!selectedMethods[i].equals(shippingMethod)) {
					newSelectedMethods[indexTracker] = selectedMethods[i];
					indexTracker++;
				}
			}
		}
		workingProduct.getShippingDetails().setShippingServiceOptions(newSelectedMethods);
	}

	public String getShippingMethodName() {
		return camelCaseToSplitString(((ShippingServiceOptionsType)JSFUtil.getRequest().getAttribute("shippingMethod")).getShippingService()).replace("_", "");
	}

	public void setRealizedProductAssociatedWithWorkingProduct(
			RealizedProduct realizedProductAssociatedWithWorkingProduct) {
		this.realizedProductAssociatedWithWorkingProduct = realizedProductAssociatedWithWorkingProduct;
	}

	public RealizedProduct getRealizedProductAssociatedWithWorkingProduct() {
		return realizedProductAssociatedWithWorkingProduct;
	}

	public void setInternationalShippingOffered(boolean internationalShippingOffered) {
		this.internationalShippingOffered = internationalShippingOffered;
	}

	public boolean isInternationalShippingOffered() {
		return internationalShippingOffered;
	}

	public void toggleInternationalShipping() {
		setInternationalShippingOffered(!isInternationalShippingOffered());
	}

	public void setInternationalShippingCharge(double internationalShippingCharge) {
		this.internationalShippingCharge = internationalShippingCharge;
	}

	public double getInternationalShippingCharge() {
		return internationalShippingCharge;
	}

	public void setDomesticShippingCharge(double domesticShippingCharge) {
		this.domesticShippingCharge = domesticShippingCharge;
	}

	public double getDomesticShippingCharge() {
		return domesticShippingCharge;
	}

	public void setMaxShippingCost(Double maxShippingCost) {
		this.maxShippingCost = maxShippingCost;
	}

	public Double getMaxShippingCost() {
		return maxShippingCost;
	}

	public String getCategory() {
		return workingProduct.getPrimaryCategory().getCategoryID();
	}

	public void setCategory(String categoryId) {
		CategoryType cat = new CategoryType();
	    cat.setCategoryID(categoryId);
	    workingProduct.setPrimaryCategory(cat);
	}
}





