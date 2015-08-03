package com.aplos.ecommerce.module;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aplos.cms.beans.MenuNode;
import com.aplos.cms.beans.developercmsmodules.CmsAtom;
import com.aplos.cms.beans.developercmsmodules.ConfigurableDeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.UnconfigurableDeveloperCmsAtom;
import com.aplos.cms.beans.pages.CmsPage;
import com.aplos.cms.beans.pages.CmsPageRevision;
import com.aplos.cms.beans.pages.CmsPageRevision.PageStatus;
import com.aplos.cms.beans.pages.CmsPlaceholderContent;
import com.aplos.cms.beans.pages.TopLevelTemplate;
import com.aplos.cms.module.CmsConfiguration;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.listeners.AplosContextListener;
import com.aplos.common.module.AplosModuleImpl;
import com.aplos.common.module.DatabaseLoader;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.EcommerceWebsite;
import com.aplos.ecommerce.beans.developercmsmodules.CheckoutPaymentCmsAtom;
import com.aplos.ecommerce.beans.developercmsmodules.ForgottenPasswordModule;
import com.aplos.ecommerce.beans.developercmsmodules.ForgottenPasswordResetModule;
import com.aplos.ecommerce.beans.developercmsmodules.ProductListModule;
import com.aplos.ecommerce.beans.developercmsmodules.SearchProductListModule;
import com.aplos.ecommerce.beans.playcom.PlayColour;
import com.aplos.ecommerce.beans.playcom.PlayGender;
import com.aplos.ecommerce.beans.playcom.PlayMainGenre;
import com.aplos.ecommerce.beans.playcom.PlayProductType;
import com.aplos.ecommerce.beans.playcom.PlaySize;
import com.aplos.ecommerce.beans.playcom.PlaySizeType;
import com.aplos.ecommerce.beans.playcom.PlaySubGenre;
import com.aplos.ecommerce.beans.product.ProductColour;
import com.aplos.ecommerce.beans.product.ProductSize;
import com.aplos.ecommerce.beans.product.ProductSizeType;
import com.aplos.ecommerce.module.EcommerceConfiguration.EcommerceUnconfigurableAtomEnum;

public class EcommerceDatabaseLoader extends DatabaseLoader {
	public EcommerceDatabaseLoader(AplosModuleImpl aplosModule) {
		super(aplosModule);
	}
	
	@Override
	public void newTableAdded(Class<?> tableClass) {	
	}

	@Override
	public void loadTables() {
		if( EcommerceConfiguration.getEcommerceSettingsStatic().isUsingPlayCom() ) {
			EcommerceDatabaseLoader.createPlayObjects();
		}
		createStandardGenericObjects();
	}

	public void createStandardGenericObjects() {
		ProductColour productColour = new ProductColour();
		productColour.setName("Standard");
		productColour.saveDetails();
		EcommerceConfiguration.getEcommerceConfiguration().setStandardProductColour(productColour);
		ProductSizeType productSizeType = new ProductSizeType();
		productSizeType.setName("Standard");
		productSizeType.saveDetails();
		ProductSize productSize = new ProductSize();
		productSize.setName("Standard");
		productSize.setProductSizeType(productSizeType);
		productSize.saveDetails();
		EcommerceConfiguration.getEcommerceConfiguration().setStandardProductSize(productSize);
	}

	public static void createMenus(EcommerceWebsite website,
			SystemUser systemUser) {
		MenuNode ecommerceMenu = new MenuNode();
		ecommerceMenu.setName("Ecommerce");
		ecommerceMenu.setParentWebsite(website);
		ecommerceMenu.setParent(website.getHiddenMenu());
		ecommerceMenu.saveDetails(website, systemUser);
		website.getHiddenMenu().addChild(ecommerceMenu);
		website.getHiddenMenu().saveDetails(website, systemUser);
		website.setEcommerceMenu(ecommerceMenu);

		MenuNode checkoutMenu = new MenuNode();
		checkoutMenu.setName("Checkout");
		checkoutMenu.setParentWebsite(website);
		checkoutMenu.setParent(website.getEcommerceMenu());
		checkoutMenu.saveDetails(website, systemUser);
		website.getEcommerceMenu().addChild(checkoutMenu);
		website.getEcommerceMenu().saveDetails(website, systemUser);
		website.setCheckoutMenu(checkoutMenu);

		MenuNode customerMenu = new MenuNode();
		customerMenu.setName("Customer");
		customerMenu.setParentWebsite(website);
		customerMenu.setParent(website.getEcommerceMenu());
		customerMenu.saveDetails(website, systemUser);
		website.getEcommerceMenu().addChild(customerMenu);
		website.getEcommerceMenu().saveDetails(website, systemUser);
		website.setCustomerMenu(customerMenu);
	}

	public static void createDefaultPages(EcommerceWebsite website,
			EcommerceCmsPageRevisions ecommerceCmsPageRevisions,
			SystemUser systemUser,
			AplosContextListener aplosContextListener,
			TopLevelTemplate mainTemplate) {
		ecommerceCmsPageRevisions = ecommerceCmsPageRevisions.getSaveableBean();
		ecommerceCmsPageRevisions
				.setCustomerBillingCpr(createUnconfigurablePage(
						"Customer : Billing Details",
						EcommerceUnconfigurableAtomEnum.CUSTOMER_BILLING,
						"customer-billing", website, mainTemplate, systemUser,
						aplosContextListener, false, true));
		ecommerceCmsPageRevisions
				.setCustomerOrdersCpr(createUnconfigurablePage(
						"Customer : Previous Orders",
						EcommerceUnconfigurableAtomEnum.CUSTOMER_ORDERS,
						"customer-orders", website, mainTemplate, systemUser,
						aplosContextListener, false, true));
		ecommerceCmsPageRevisions
				.setCustomerPasswordCpr(createUnconfigurablePage(
						"Customer : Change Password",
						EcommerceUnconfigurableAtomEnum.CUSTOMER_CHANGE_PASSWORD,
						"customer-password", website, mainTemplate, systemUser,
						aplosContextListener, false, true));
		ecommerceCmsPageRevisions
				.setCustomerShippingCpr(createUnconfigurablePage(
						"Customer : Shipping Details",
						EcommerceUnconfigurableAtomEnum.CUSTOMER_SHIPPING,
						"customer-shipping", website, mainTemplate, systemUser,
						aplosContextListener, false, true));
		ecommerceCmsPageRevisions
				.setCustomerSignInCpr(createUnconfigurablePage(
						"Customer : Sign In",
						EcommerceUnconfigurableAtomEnum.CUSTOMER_SIGN_IN,
						"customer-sign-in", website, mainTemplate, systemUser,
						aplosContextListener, false, true));

		ecommerceCmsPageRevisions.setSingleProductCpr(createUnconfigurablePage(
				"Product : View Single",
				EcommerceUnconfigurableAtomEnum.SINGLE_PRODUCT, "product",
				website, mainTemplate, systemUser,
				aplosContextListener, false, false));
		ecommerceCmsPageRevisions.setProductListCpr(createConfigurablePage(
				"Product : List", ProductListModule.class, "category", website,
				mainTemplate, systemUser, aplosContextListener, false,
				false));
		ecommerceCmsPageRevisions.setSearchResultsCpr(createConfigurablePage(
				"Product : Search Results", SearchProductListModule.class,
				"search-results", website, mainTemplate, systemUser,
				aplosContextListener, false, false));
		ecommerceCmsPageRevisions
				.setCheckoutForgottenPasswordCpr(createConfigurablePage(null,
						ForgottenPasswordModule.class, "forgotten-password",
						website, mainTemplate, systemUser,
						aplosContextListener, false, false));
		ecommerceCmsPageRevisions
				.setCheckoutPasswordResetCpr(createConfigurablePage(null,
						ForgottenPasswordResetModule.class,
						"forgotten-password-reset", website, mainTemplate,
						systemUser, aplosContextListener, false, false));
		ecommerceCmsPageRevisions
				.setCheckoutBillingAddressCpr(createUnconfigurablePage(
						"Checkout : Billing Details",
						EcommerceUnconfigurableAtomEnum.CHECKOUT_BILLING,
						"checkout-billing", website, mainTemplate, systemUser,
						aplosContextListener, true, false));
		ecommerceCmsPageRevisions
				.setCheckoutConfirmationCpr(createUnconfigurablePage(
						"Checkout : Confirmation",
						EcommerceUnconfigurableAtomEnum.CHECKOUT_CONFIRMATION,
						"checkout-confirmation", website, mainTemplate,
						systemUser, aplosContextListener, true, false));
		ecommerceCmsPageRevisions
				.setCheckoutSuccessCpr(createUnconfigurablePage(
						"Checkout : Payment Successful",
						EcommerceUnconfigurableAtomEnum.CHECKOUT_PAYMENT_SUCCESSFUL,
						"checkout-payment-successful", website, mainTemplate,
						systemUser, aplosContextListener, true, false));
		ecommerceCmsPageRevisions
				.setCheckoutShippingAddressCpr(createUnconfigurablePage(
						"Checkout : Shipping Details",
						EcommerceUnconfigurableAtomEnum.CHECKOUT_SHIPPING,
						"checkout-shipping", website, mainTemplate, systemUser,
						aplosContextListener, true, false));
		// ecommerceCmsPageRevisions.setCheckoutSignInCpr(
		// createUnconfigurablePage("Checkout : Sign In",
		// EcommerceUnconfigurableAtomEnum.CHECKOUT_SIGN_IN, "checkout-sign-in",
		// website, mainTemplate, systemUser, aplosContextListener,
		// true, false));
		ecommerceCmsPageRevisions
				.setCheckoutSignInOrSignUpCpr(createUnconfigurablePage(
						"Checkout : Sign In/Up",
						EcommerceUnconfigurableAtomEnum.CHECKOUT_SIGN_IN_OR_SIGN_UP,
						"checkout-sign-in-sign-up", website, mainTemplate,
						systemUser, aplosContextListener, true, false));
		ecommerceCmsPageRevisions
				.setCheckoutSignUpCpr(createUnconfigurablePage(
						"Checkout : Sign Up",
						EcommerceUnconfigurableAtomEnum.CHECKOUT_SIGN_UP,
						"checkout-sign-up", website, mainTemplate, systemUser,
						aplosContextListener, true, false));
		ecommerceCmsPageRevisions.setCheckoutCartCpr(createUnconfigurablePage(
				"Checkout : View Cart",
				EcommerceUnconfigurableAtomEnum.SHOPPING_CART, "cart", website,
				mainTemplate, systemUser, aplosContextListener, true,
				false));
		ecommerceCmsPageRevisions.setCheckoutPaymentCpr(createConfigurablePage(
				"Checkout : Payment", CheckoutPaymentCmsAtom.class,
				"checkout-payment", website, mainTemplate, systemUser,
				aplosContextListener, true, false));

		String paymentAlreadyMadeText = "<div><div style=\"font-size: 14px;margin-bottom: 5px;text-shadow: 2px 2px 2px #8ED8F5\">Payment already made</div><div style=\"margin-top:30px\">The payment for this order has already been made, thank you.</div></div>";
		String threeDauthText = "<iframe frameborder=\"0\" name=\"3DIFrame\" src=\"common/payments/sagepay/threeDRedirect.jsf\" style=\"overflow:auto;height:400px;width:470px\"></iframe>";
		String awaitingAuthText = "<p>Thank you for your order, it is now awaiting authorisation.  We will be in contact with you shortly.</p>";

		ecommerceCmsPageRevisions
				.setCheckoutAwaitingAuthorisationCpr(createTextPage(
						"Checkout : Awaiting Auth", awaitingAuthText,
						"checkout-awaiting-auth", website, mainTemplate,
						systemUser, aplosContextListener, true, false));
		ecommerceCmsPageRevisions
				.setCheckoutPaymentAlreadyMadeCpr(createTextPage(
						"Checkout : Payment Already Made",
						paymentAlreadyMadeText,
						"checkout-payment-already-made", website, mainTemplate,
						systemUser, aplosContextListener, true, false));
		if( CmsConfiguration.getCmsConfiguration().getThreeDAuthCpr() == null ) {
			CmsConfiguration cmsConfiguration = CmsConfiguration.getCmsConfiguration().getSaveableBean();
			cmsConfiguration.setThreeDAuthCpr(createTextPage(
					"Checkout : 3D Auth", threeDauthText, "checkout-three-d-auth",
					website, mainTemplate, systemUser,
					aplosContextListener, true, false));
			cmsConfiguration.saveDetails();
		}
		ecommerceCmsPageRevisions.saveDetails(systemUser);
	}

	public static CmsPageRevision createUnconfigurablePage(String nameOptional,
			EcommerceUnconfigurableAtomEnum atomEnum, String mapping,
			EcommerceWebsite website, TopLevelTemplate mainTemplate,
			SystemUser adminUser,
			AplosContextListener aplosContextListener, boolean isCheckout,
			boolean isCustomer) {
		UnconfigurableDeveloperCmsAtom atom = CmsConfiguration
				.getCmsConfiguration().getUnconfigurableDeveloperCmsAtom(
						atomEnum);
		if (atom == null) {
			BeanDao dao = new BeanDao(UnconfigurableDeveloperCmsAtom.class);
			dao.setWhereCriteria("bean.cmsAtomIdStr='"
					+ atomEnum.getCmsAtomIdStr() + "'");
			dao.setMaxResults(1);
			atom = dao.setIsReturningActiveBeans(true).getFirstBeanResult();
			if (atom == null) {
				atom = new UnconfigurableDeveloperCmsAtom();
				atom.setAplosModuleName("ecommerce");
				atom.setCmsAtomIdStr(atomEnum.getCmsAtomIdStr());
				atom.setName(atomEnum.getCmsAtomName());
				atom.saveDetails();
			}
		}
		CmsPageRevision unconfigurableCpr = createCmsPageRevision(nameOptional,
				mapping, atom, null, website, adminUser, mainTemplate,
				aplosContextListener);
		if (isCheckout) {
			website.getCheckoutMenu()
					.getChildren()
					.add(new MenuNode(website, website.getEcommerceMenu(),
							unconfigurableCpr.getCmsPage()));
		} else if (isCustomer) {
			website.getCustomerMenu()
					.getChildren()
					.add(new MenuNode(website, website.getEcommerceMenu(),
							unconfigurableCpr.getCmsPage()));
		} else {
			website.getEcommerceMenu()
					.getChildren()
					.add(new MenuNode(website, website.getEcommerceMenu(),
							unconfigurableCpr.getCmsPage()));
		}
		return unconfigurableCpr;
	}

	public static CmsPageRevision createConfigurablePage(String nameOptional,
			Class<? extends CmsAtom> atomClass, String mapping,
			EcommerceWebsite website, TopLevelTemplate mainTemplate,
			SystemUser adminUser,
			AplosContextListener aplosContextListener, boolean isCheckout,
			boolean isCustomer) {
		ConfigurableDeveloperCmsAtom atom = (ConfigurableDeveloperCmsAtom) CommonUtil
				.getNewInstance(atomClass, null);
		atom.saveDetails();
		CmsPageRevision configurableCpr = createCmsPageRevision(nameOptional,
				mapping, atom, null, website, adminUser, mainTemplate,
				aplosContextListener);
		if (isCheckout) {
			website.getCheckoutMenu()
					.getChildren()
					.add(new MenuNode(website, website.getEcommerceMenu(),
							configurableCpr.getCmsPage()));
		} else if (isCustomer) {
			website.getCustomerMenu()
					.getChildren()
					.add(new MenuNode(website, website.getEcommerceMenu(),
							configurableCpr.getCmsPage()));
		} else {
			website.getEcommerceMenu()
					.getChildren()
					.add(new MenuNode(website, website.getEcommerceMenu(),
							configurableCpr.getCmsPage()));
		}
		return configurableCpr;
	}

	public static CmsPageRevision createTextPage(String nameOptional,
			String textContent, String mapping, EcommerceWebsite website,
			TopLevelTemplate mainTemplate, SystemUser adminUser,
			AplosContextListener aplosContextListener,
			boolean isCheckout, boolean isCustomer) {
		CmsPageRevision textCpr = createCmsPageRevision(nameOptional, mapping,
				null, textContent, website, adminUser, mainTemplate,
				aplosContextListener);
		if (isCheckout) {
			website.getCheckoutMenu()
					.getChildren()
					.add(new MenuNode(website, website.getEcommerceMenu(),
							textCpr.getCmsPage()));
		} else if (isCustomer) {
			website.getCustomerMenu()
					.getChildren()
					.add(new MenuNode(website, website.getEcommerceMenu(),
							textCpr.getCmsPage()));
		} else {
			website.getEcommerceMenu()
					.getChildren()
					.add(new MenuNode(website, website.getEcommerceMenu(),
							textCpr.getCmsPage()));
		}
		return textCpr;
	}

	private static CmsPage createCmsPage(String name, String mapping,
			EcommerceWebsite website, SystemUser adminUser) {
		CmsPage cmsPage = new CmsPage();
		cmsPage.setName(name);
		cmsPage.setMapping(mapping);
		cmsPage.setStatus(PageStatus.PUBLISHED);
		cmsPage.saveDetails(website, adminUser);
		return cmsPage;
	}

	private static CmsPageRevision createCmsPageRevision(String nameOptional,
			String cmsPageMapping, DeveloperCmsAtom atom,
			String placeholderText, EcommerceWebsite website,
			SystemUser adminUser, TopLevelTemplate mainTemplate,
			AplosContextListener aplosContextListener) {
		if (nameOptional == null) {
			nameOptional = atom.getName();
		}
		if (atom != null) {
			if (placeholderText == null) {
				placeholderText = "";
			}
			placeholderText = placeholderText
					+ atom.getFirstInsertText();
		}
		CmsPage cmsPage = createCmsPage(nameOptional, cmsPageMapping, website,
				adminUser);
		CmsPageRevision newCpr = new CmsPageRevision();
		newCpr.setCmsPage(cmsPage);
		newCpr.setTemplate(mainTemplate, true, true);
		if( atom != null ) {
			newCpr.getCmsAtomList().add(atom);
		}
		String placeholderName = "body";
		if (newCpr.getTemplateEditableCphNameList(true).contains("main content")) {
			placeholderName = "main content";
		} else if (!newCpr.getTemplateEditableCphNameList(true).contains("body")) {
			placeholderName = newCpr.getTemplateEditableCphNameList(true).get(0);
		}
		newCpr.getPlaceholderContentMap().put(placeholderName,
				new CmsPlaceholderContent(placeholderText));
		newCpr.saveDetails(website, adminUser);
		return newCpr;
	}

	public static void createPlayObjects() {


		ApplicationUtil.executeSql("UPDATE ProductType SET playMainGenre_id = NULL");
		ApplicationUtil.executeSql("UPDATE ProductSizeType SET playSizeType_id = NULL");
		ApplicationUtil.executeSql("UPDATE ProductSize SET playSize_id = NULL");
		ApplicationUtil.executeSql("DELETE FROM PlayMainGenre");
		ApplicationUtil.executeSql("DELETE FROM PlayProductType");
		ApplicationUtil.executeSql("DELETE FROM PlayGender");
		ApplicationUtil.executeSql("DELETE FROM PlaySubGenre");
		ApplicationUtil.executeSql("DELETE FROM Playsizetype_playsize");
		ApplicationUtil.executeSql("DELETE FROM PlaySize");
		ApplicationUtil.executeSql("DELETE FROM PlaySizeType");

		Map<String, PlayGender> genderMap = new HashMap<String, PlayGender>();
		Map<String, PlaySizeType> sizeTypeMap = new HashMap<String, PlaySizeType>();
		Map<String, PlaySize> playSizeMap = new HashMap<String, PlaySize>();
		Map<String, PlayProductType> playProductTypeMap = new HashMap<String, PlayProductType>();
		Map<String, PlayColour> playColourMap = new HashMap<String, PlayColour>();
		Map<String, PlaySubGenre> subGenreMap = new HashMap<String, PlaySubGenre>();
		Map<String, PlayMainGenre> mainGenreMap = new HashMap<String, PlayMainGenre>();

		List<PlayColour> loadedPlayColours = new BeanDao( PlayColour.class ).getAll();
		for( int i = 0, n = loadedPlayColours.size(); i < n; i++ ) {
			playColourMap.put( loadedPlayColours.get( i ).getName(), loadedPlayColours.get( i ) );
		}
//		List<PlaySize> loadedPlaySizes = new AqlBeanDao( PlaySize.class ).getAll();
//		for( int i = 0, n = loadedPlaySizes.size(); i < n; i++ ) {
//			playSizeMap.put( loadedPlaySizes.get( i ).getName(), loadedPlaySizes.get( i ) );
//		}
//		List<PlayProductType> loadedPlayProductTypes = new AqlBeanDao( PlayProductType.class ).getAll();
//		for( int i = 0, n = loadedPlayProductTypes.size(); i < n; i++ ) {
//			playProductTypeMap.put( loadedPlayProductTypes.get( i ).getName(), loadedPlayProductTypes.get( i ) );
//		}
//		List<PlaySizeType> loadedPlaySizeTypes = new AqlBeanDao( PlaySizeType.class ).getAll();
//		for( int i = 0, n = loadedPlaySizeTypes.size(); i < n; i++ ) {
//			sizeTypeMap.put( loadedPlaySizeTypes.get( i ).getName(), loadedPlaySizeTypes.get( i ) );
//		}
//		List<PlayGender> loadedPlayGenders = new AqlBeanDao( PlayGender.class ).getAll();
//		for( int i = 0, n = loadedPlayGenders.size(); i < n; i++ ) {
//			genderMap.put( loadedPlayGenders.get( i ).getName(), loadedPlayGenders.get( i ) );
//		}
//		List<PlayMainGenre> loadedPlayMainGenres = new AqlBeanDao( PlayMainGenre.class ).getAll();
//		for( int i = 0, n = loadedPlayMainGenres.size(); i < n; i++ ) {
//			mainGenreMap.put( loadedPlayMainGenres.get( i ).getName(), loadedPlayMainGenres.get( i ) );
//		}
//		List<PlaySubGenre> loadedPlaySubGenres = new AqlBeanDao( PlaySubGenre.class ).getAll();
//		for( int i = 0, n = loadedPlaySubGenres.size(); i < n; i++ ) {
//			subGenreMap.put( loadedPlaySubGenres.get( i ).getName(), loadedPlaySubGenres.get( i ) );
//		}
		
		List<String[]> genreAndSizeValues = new ArrayList<String[]>();


		addPlaySizeAndSizeTypeList( loadPlayValuesFromCsv( "sizeTypeAndSizeList.csv" ), sizeTypeMap, playSizeMap );

		List<String[]> playComColourValues = loadPlayValuesFromCsv( "playComColourList.csv" );
		addPlayColourList( 0, playComColourValues, playColourMap );

		genreAndSizeValues = loadPlayValuesFromCsv( "genreAndSizeList.csv" );

//		addPlaySizeList(sizeTypeMap, "MensSizeList", 13, genreAndSizeValues, playSizeMap );
//		addPlaySizeList(sizeTypeMap, "WomensSizeList", 14, genreAndSizeValues, playSizeMap);
//		addPlaySizeList(sizeTypeMap, "KidsSizeList", 15, genreAndSizeValues, playSizeMap);
//		addPlaySizeList(sizeTypeMap, "FootwearSizes", 16, genreAndSizeValues, playSizeMap);

		String[] documentValuesLine;
		PlayGender tempGender;
		PlaySubGenre tempSubGenre;
		PlaySizeType tempSizeList;
		PlayMainGenre tempMainGenre;
		for (int i = 1, n = genreAndSizeValues.size(); i < n; i++) {
			documentValuesLine = genreAndSizeValues.get(i);
			if( documentValuesLine.length != 0 && mainGenreMap.get( documentValuesLine[0] ) == null ) {
	
				if (CommonUtil.isNullOrEmpty(documentValuesLine[2])) {
					break;
				}
				tempGender = genderMap.get(documentValuesLine[2]);
				if (tempGender == null) {
					tempGender = new PlayGender();
					tempGender.setName(documentValuesLine[2]);
					tempGender.saveDetails();
					genderMap.put(tempGender.getName(), tempGender);
				}
	
				tempSubGenre = subGenreMap.get(documentValuesLine[3]);
				if (tempSubGenre == null) {
					tempSubGenre = new PlaySubGenre();
					tempSubGenre.setName(documentValuesLine[3]);
					tempSubGenre.saveDetails();
					subGenreMap.put(tempSubGenre.getName(), tempSubGenre);
				}
	
				tempSizeList = sizeTypeMap.get(documentValuesLine[4]);
	
				tempMainGenre = new PlayMainGenre();
				tempMainGenre.setName(documentValuesLine[0]);
				tempMainGenre.setGenreCode(documentValuesLine[1]);
				tempMainGenre.setPlayGender(tempGender);
				tempMainGenre.setPlaySubGenre(tempSubGenre);
				tempMainGenre.setPlaySizeType(tempSizeList);
				tempMainGenre.saveDetails();
				mainGenreMap.put( tempMainGenre.getName(), tempMainGenre );
			}
		}

		List<String[]> productTypeValues = loadPlayValuesFromCsv( "productTypeList.csv" );
		

		addPlayProductTypes( playProductTypeMap, 7, 2, null, subGenreMap.get( "Outerwear" ), productTypeValues);
		addPlayProductTypes( playProductTypeMap, 8, 2, null, subGenreMap.get( "HoodiesSweats" ), productTypeValues);
		addPlayProductTypes( playProductTypeMap, 9, 2, null, subGenreMap.get( "Knitwear" ), productTypeValues);
		addPlayProductTypes( playProductTypeMap, 10, 2, null, subGenreMap.get( "Tshirts" ), productTypeValues);
		addPlayProductTypes( playProductTypeMap, 11, 2, null, subGenreMap.get( "Loungewear" ), productTypeValues);
		addPlayProductTypes( playProductTypeMap, 12, 2, null, subGenreMap.get( "FilmTV" ), productTypeValues);
		addPlayProductTypes( playProductTypeMap, 13, 2, null, subGenreMap.get( "Jeans" ), productTypeValues);
		addPlayProductTypes( playProductTypeMap, 14, 2, null, subGenreMap.get( "Trousers" ), productTypeValues);
		
		addPlayProductTypes( playProductTypeMap, 1, 2, genderMap.get( "Women"), subGenreMap.get( "WomenTops" ), productTypeValues);
		addPlayProductTypes( playProductTypeMap, 2, 2, genderMap.get( "Women"), subGenreMap.get( "Dresses" ), productTypeValues);
		addPlayProductTypes( playProductTypeMap, 3, 2, genderMap.get( "Women"), subGenreMap.get( "Blouses" ), productTypeValues);
		addPlayProductTypes( playProductTypeMap, 4, 2, genderMap.get( "Women"), subGenreMap.get( "Skirts" ), productTypeValues);
		addPlayProductTypes( playProductTypeMap, 5, 2, genderMap.get( "Women"), subGenreMap.get( "FemaleSwimwear" ), productTypeValues);
		addPlayProductTypes( playProductTypeMap, 6, 2, genderMap.get( "Women"), subGenreMap.get( "WomensUnderwear" ), productTypeValues);

		addPlayProductTypes( playProductTypeMap, 3, 27, genderMap.get( "Men" ), subGenreMap.get( "Shirts" ), productTypeValues);
		addPlayProductTypes( playProductTypeMap, 4, 27, genderMap.get( "Men" ), subGenreMap.get( "MensUnderwear" ), productTypeValues);
		addPlayProductTypes( playProductTypeMap, 5, 27, genderMap.get( "Men" ), subGenreMap.get( "MaleSwimwear" ), productTypeValues);

		addPlayProductTypes( playProductTypeMap, 3, 42, genderMap.get( "Girls" ), subGenreMap.get( "Hosiery" ), productTypeValues);
		addPlayProductTypes( playProductTypeMap, 4, 42, genderMap.get( "Girls" ), subGenreMap.get( "GirlsUnderwear" ), productTypeValues);
		addPlayProductTypes( playProductTypeMap, 5, 42, genderMap.get( "Boys" ), subGenreMap.get( "BoysUnderwear" ), productTypeValues);
		

	}
	
	private static List<String[]> loadPlayValuesFromCsv( String fileName ) {
		List<String[]> playComSizeValues = new ArrayList<String[]>();
		BufferedReader br = null;
		try {
			URL url = JSFUtil.checkFileLocations(fileName,
					"resources/playcom/", true);
			br = new BufferedReader(new InputStreamReader(url.openStream()));
		} catch (FileNotFoundException e2) {
			ApplicationUtil.getAplosContextListener().handleError(e2);
		} catch (IOException ioEx) {
			ApplicationUtil.getAplosContextListener().handleError(ioEx);
		}
	
		String nextLine = "";
		try {
			while ((nextLine = br.readLine()) != null) {
				playComSizeValues.add(nextLine.split(","));
			}
		} catch (IOException e1) {
			ApplicationUtil.getAplosContextListener().handleError(e1);
		}
		return playComSizeValues;
	}
	
	private static void addPlayProductTypes( Map<String, PlayProductType> playProductTypeMap, int x, int y, PlayGender playGender, PlaySubGenre playSubGenre, List<String[]> productTypeValues ) {
		String tempRowValue;
		PlayProductType playProductType;
		for (int i = y, n = productTypeValues.size(); i < n; i++) {
			if( productTypeValues.get(i).length > x ) {
				tempRowValue = productTypeValues.get(i)[x];
				if (CommonUtil.isNullOrEmpty(tempRowValue)) {
					break;
				} else {
					tempRowValue = FormatUtil.removeQuotesFromCsvString( tempRowValue );
					playProductType = playProductTypeMap.get( tempRowValue );
					if( playProductType == null ) {
						playProductType = new PlayProductType();
						playProductType.setPlayGender(playGender);
						playProductType.setPlaySubGenre(playSubGenre);
						playProductType.setName( tempRowValue );
						playProductType.saveDetails();
					}
					playProductTypeMap.put( playProductType.getName(), playProductType );
				}
			}
		}
	}

//	private static void addPlaySizeList(Map<String, PlaySizeType> sizeTypeMap,
//			String mapKey, int columnIdx, List<String[]> documentValues, Map<String, PlaySize> playSizeMap) {
//		List<PlaySize> sizeList = new ArrayList<PlaySize>();
//		String tempRowValue;
//		PlaySize tempPlaySize;
//		for (int i = 1, n = documentValues.size(); i < n; i++) {
//			tempRowValue = documentValues.get(i)[columnIdx];
//			if (CommonUtil.isNullOrEmpty(tempRowValue)) {
//				break;
//			} else {
//				tempRowValue = FormatUtil.removeQuotesFromCsvString( tempRowValue );
//				tempPlaySize = playSizeMap.get( tempRowValue );
//				if( tempPlaySize == null ) {
//					tempPlaySize = new PlaySize();
//					tempPlaySize.setName( tempRowValue );
//					tempPlaySize.saveDetails();
//					playSizeMap.put( tempRowValue, tempPlaySize );
//				}
//				sizeList.add( tempPlaySize );
//			}
//		}
//		if( sizeTypeMap.get( mapKey ) == null ) {
//			PlaySizeType playSizeType = new PlaySizeType();
//			playSizeType.setSizes(sizeList);
//			playSizeType.setName(mapKey);
//			playSizeType.saveDetails();
//			sizeTypeMap.put(mapKey, playSizeType);
//		}
//	}

	private static void addPlayColourList( int columnIdx, List<String[]> documentValues, Map<String, PlayColour> playColourMap) {
		String tempRowValue;
		PlayColour tempPlayColour;
		for (int i = 1, n = documentValues.size(); i < n; i++) {
			tempRowValue = documentValues.get(i)[columnIdx];
			if (CommonUtil.isNullOrEmpty(tempRowValue)) {
				break;
			} else {
				tempRowValue = FormatUtil.removeQuotesFromCsvString( tempRowValue );
				tempPlayColour = playColourMap.get( tempRowValue );
				if( tempPlayColour == null ) {
					tempPlayColour = new PlayColour();
					tempPlayColour.setName( tempRowValue );
					tempPlayColour.saveDetails();
					playColourMap.put( tempRowValue, tempPlayColour );
				}
			}
		}
	}

	private static void addPlaySizeAndSizeTypeList( List<String[]> documentValues, Map<String, PlaySizeType> playSizeTypeMap, Map<String, PlaySize> playSizeMap) {
		String tempRowValue;
		PlaySizeType tempPlaySizeType = null;
		PlaySize tempPlaySize; 
		for (int i = 0, n = documentValues.get(0).length; i < n; i++) {
			for (int j = 0, p = documentValues.size(); j < p; j++) {
				if( documentValues.get(j).length > i ) {
					tempRowValue = documentValues.get(j)[i];
					if (CommonUtil.isNullOrEmpty(tempRowValue)) {
						break;
					} else {
						tempRowValue = FormatUtil.removeQuotesFromCsvString( tempRowValue );
						if( j == 0 ) {
							tempPlaySizeType = playSizeTypeMap.get( tempRowValue );
							if( tempPlaySizeType == null ) {
								tempPlaySizeType = new PlaySizeType();
								tempPlaySizeType.setName( tempRowValue );
								tempPlaySizeType.saveDetails();
								playSizeTypeMap.put( tempRowValue, tempPlaySizeType );
							}
						} else {
							tempPlaySize = playSizeMap.get( tempRowValue );
							if( tempPlaySize == null ) {
								tempPlaySize = new PlaySize();
								tempPlaySize.setName( tempRowValue );
								tempPlaySize.setPlaySizeType(tempPlaySizeType);
								tempPlaySize.saveDetails();
								playSizeMap.put( tempRowValue, tempPlaySize );
							}
						}
					}
				}
			}
		}
	}

}
