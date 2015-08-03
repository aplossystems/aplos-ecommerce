package com.aplos.ecommerce.module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.aplos.cms.backingpage.pages.CmsPageRevisionListPage;
import com.aplos.cms.beans.CmsWebsite;
import com.aplos.cms.module.CmsModule;
import com.aplos.common.AplosRequestContext;
import com.aplos.common.AplosUrl;
import com.aplos.common.BackingPageUrl;
import com.aplos.common.ImplicitPolymorphismEntry;
import com.aplos.common.ImplicitPolymorphismVariable;
import com.aplos.common.appconstants.AplosScopedBindings;
import com.aplos.common.backingpage.payments.paypal.PayPalValidatePage;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.Currency;
import com.aplos.common.beans.CustomerReview;
import com.aplos.common.beans.ShoppingCart;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.beans.Website;
import com.aplos.common.beans.communication.MailRecipientFinder;
import com.aplos.common.interfaces.AplosWorkingDirectoryInter;
import com.aplos.common.interfaces.BundleKey;
import com.aplos.common.listeners.AplosContextListener;
import com.aplos.common.listeners.PageBindingPhaseListener;
import com.aplos.common.module.AplosModuleImpl;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.module.DatabaseLoader;
import com.aplos.common.module.ModuleConfiguration;
import com.aplos.common.module.ModuleDbConfig;
import com.aplos.common.module.ModuleUpgrader;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.backingpage.shipping.EcommercePayPalValidatePage;
import com.aplos.ecommerce.beans.EcommerceShoppingCart;
import com.aplos.ecommerce.beans.developercmsmodules.AssociatedProductsModule;
import com.aplos.ecommerce.beans.developercmsmodules.BrochureCmsAtom;
import com.aplos.ecommerce.beans.developercmsmodules.CategoryListModule;
import com.aplos.ecommerce.beans.developercmsmodules.CategoryOverviewModule;
import com.aplos.ecommerce.beans.developercmsmodules.CheckoutPaymentCmsAtom;
import com.aplos.ecommerce.beans.developercmsmodules.CrossSellingModule;
import com.aplos.ecommerce.beans.developercmsmodules.FavouriteProductListsCmsAtom;
import com.aplos.ecommerce.beans.developercmsmodules.ForgottenPasswordModule;
import com.aplos.ecommerce.beans.developercmsmodules.ForgottenPasswordResetModule;
import com.aplos.ecommerce.beans.developercmsmodules.FriendReferralModule;
import com.aplos.ecommerce.beans.developercmsmodules.LatestProductListModule;
import com.aplos.ecommerce.beans.developercmsmodules.MyDetailsModule;
import com.aplos.ecommerce.beans.developercmsmodules.NewsModule;
import com.aplos.ecommerce.beans.developercmsmodules.ProductListModule;
import com.aplos.ecommerce.beans.developercmsmodules.ProductTypeReviewCmsAtom;
import com.aplos.ecommerce.beans.developercmsmodules.ProductTypeTechSupportCmsAtom;
import com.aplos.ecommerce.beans.developercmsmodules.SaleProductListModule;
import com.aplos.ecommerce.beans.developercmsmodules.SearchProductListModule;
import com.aplos.ecommerce.beans.developercmsmodules.SizeChartModule;
import com.aplos.ecommerce.beans.product.ProductBrand;
import com.aplos.ecommerce.beans.product.ProductInfo;
import com.aplos.ecommerce.beans.product.ProductType;
import com.aplos.ecommerce.enums.EcommerceBundleKey;
import com.aplos.ecommerce.enums.EcommerceWorkingDirectory;
import com.aplos.ecommerce.utils.EcommerceUtil;

public class EcommerceModule extends AplosModuleImpl {
	
	@Override
	public DatabaseLoader createDatabaseLoader() {
		return new EcommerceDatabaseLoader(this);
	}

	@Override
	public ModuleConfiguration getModuleConfiguration() {
		return EcommerceConfiguration.getEcommerceConfiguration();
	}

	@Override
	public void addAvailableBundleKeys(List<BundleKey> textTranslationList) {
		textTranslationList.addAll( Arrays.asList( EcommerceBundleKey.values() ) );
	}
	
	@Override
	public void addGlobalAccessPages(List<String> globalAccessPages) {
		globalAccessPages.add("/ecommerce/emailAFriendForm.jsf");
	}
	
	public static boolean dynamicRequests( HttpServletRequest request, List<String[]> redirects ) {
		Pattern pattern;
		Matcher matcher;
		String path = request.getServletPath();
		for( int i = 0, n = redirects.size(); i < n; i++ ) {
			pattern = Pattern.compile( redirects.get( i )[ 0 ] );
			matcher = pattern.matcher( path );
			if( matcher.find() ) {
				AplosRequestContext aplosRequestContext = JSFUtil.getAplosRequestContext(request); 
				aplosRequestContext.setOriginalUrl( redirects.get( i )[ 1 ] );
				aplosRequestContext.setDynamicViewEl( "ecommercePageDispatcher.viewOrEdit" );
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void tabSessionCreated(HttpSession session,
			Map<String, Object> tabSessionMap) {
		Currency currency = CommonConfiguration.getCommonConfiguration().getDefaultCurrency();
		if (currency != null) {
//			HibernateUtil.initialise( currency, true );
			tabSessionMap.put( AplosBean.getBinding( currency.getClass() ), currency );
			if ( JSFUtil.getBeanFromScope( ShoppingCart.class ) == null) {
				EcommerceUtil ecommerceUtil = EcommerceUtil.getEcommerceUtil();
				if( ecommerceUtil != null ) {
					EcommerceShoppingCart ecommerceShoppingCart = ecommerceUtil.createShoppingCart();
					tabSessionMap.put( CommonUtil.getBinding( ShoppingCart.class ), ecommerceShoppingCart );
				}
			}
		} else {
			JSFUtil.addMessageForError("No currency was found to add to the session. This should only happen when installing a website, if you see it at another time, you have a problem.");
		}
	}
	
	@Override
	public Boolean rewriteUrl( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
		String path = request.getRequestURL().toString();
		Pattern pattern = Pattern.compile( "/cms(/)?$" );
		Matcher matcher = pattern.matcher( path );
		if( matcher.find() ) {
			SystemUser currentUser = JSFUtil.getLoggedInUser(request.getSession());
			if( currentUser != null && currentUser.isLoggedIn() ) {
				BackingPageUrl backingPageUrl = new BackingPageUrl( CmsPageRevisionListPage.class );
				backingPageUrl.addContextPath( false );
				backingPageUrl.addQueryParameter( AplosScopedBindings.ADD_WEBSITE_FOR_ACCESS, "true" );
				JSFUtil.getAplosRequestContext(request).setRedirectionUrl( backingPageUrl.toString() );
			} else {
				AplosUrl aplosUrl = new AplosUrl(ApplicationUtil.getAplosContextListener().getLoginPageUrl(path,request,true,false));
				JSFUtil.getAplosRequestContext(request).setRedirectionUrl( aplosUrl.toString() );
			}
			return true;
		}
		
		pattern = Pattern.compile( "(?!.*(^/media/))(.*/$|.*\\.aplos|.*/;jsessionid=.*)" );
		matcher = pattern.matcher( path );
		if( matcher.find() ) {
			JSFUtil.getAplosRequestContext(request).setDynamicViewEl( "ecommercePageDispatcher.viewOrEdit" );
			return true;
		}

		return null;
	}
	
	@Override
	public List<String> getRestrictedMediaPaths() {
		List<String> restrictedPaths = new ArrayList<String>();
		for (EcommerceWorkingDirectory awd : EcommerceWorkingDirectory.values()) {
			if (awd.isRestricted()) {
				restrictedPaths.add(awd.getDirectoryPath(false));
			}
		}
		if (restrictedPaths.size() > 0) {
			return restrictedPaths;
		} else {
			return null;
		}
	}
	
	@Override
	public AplosWorkingDirectoryInter[] createWorkingDirectoryEnums() {
		EcommerceWorkingDirectory.createDirectories();
		return EcommerceWorkingDirectory.values();
	}

	@Override
	public void initModule() {
		CmsModule cmsModule = (CmsModule) ApplicationUtil.getAplosContextListener().getAplosModuleByClass(CmsModule.class);
		if( cmsModule != null ) {
			List<Website> websiteList = ApplicationUtil.getAplosContextListener().getWebsiteList();
			for( Website tempWebsite : websiteList ) {
				if( tempWebsite instanceof CmsWebsite ) {
					CmsWebsite cmsWebsite = ((CmsWebsite) tempWebsite);
					cmsWebsite.addAvailableCmsAtom( new CheckoutPaymentCmsAtom() );
					cmsWebsite.addAvailableCmsAtom( new ProductListModule() );
					cmsWebsite.addAvailableCmsAtom( new FavouriteProductListsCmsAtom() );
					cmsWebsite.addAvailableCmsAtom( new ProductTypeReviewCmsAtom() );
					cmsWebsite.addAvailableCmsAtom( new ProductTypeTechSupportCmsAtom() );
					cmsWebsite.addAvailableCmsAtom( new SearchProductListModule() );
					cmsWebsite.addAvailableCmsAtom( new LatestProductListModule() );
					cmsWebsite.addAvailableCmsAtom( new SaleProductListModule() );
					cmsWebsite.addAvailableCmsAtom( new CategoryOverviewModule() );
					cmsWebsite.addAvailableCmsAtom( new CategoryListModule() );
					cmsWebsite.addAvailableCmsAtom( new ForgottenPasswordModule() );
					cmsWebsite.addAvailableCmsAtom( new ForgottenPasswordResetModule() );
					cmsWebsite.addAvailableCmsAtom( new MyDetailsModule() );
					cmsWebsite.addAvailableCmsAtom( new NewsModule() );
					cmsWebsite.addAvailableCmsAtom( new FriendReferralModule() );
					cmsWebsite.addAvailableCmsAtom( new AssociatedProductsModule() );
					cmsWebsite.addAvailableCmsAtom( new CrossSellingModule() );
					cmsWebsite.addAvailableCmsAtom( new SizeChartModule() );
					cmsWebsite.addAvailableCmsAtom( new BrochureCmsAtom() );
				}
			}

			
			cmsModule.getAvailableGeneratorItemClassList().add( ProductType.class );
			cmsModule.getAvailableGeneratorItemClassList().add( ProductBrand.class );
			cmsModule.getAvailableGeneratorItemClassList().add( ProductInfo.class );
		}

		PageBindingPhaseListener.addBindingOverride( PayPalValidatePage.class, EcommercePayPalValidatePage.class );
		List<MailRecipientFinder> mailRecipientFinderList = EcommerceConfiguration.getEcommerceConfiguration().getMailRecipientFinders();
//		HibernateUtil.initialiseList( mailRecipientFinderList, true );
		cmsModule.getAvailableMailRecipientFinderList().addAll( mailRecipientFinderList );
		super.initModule();
	}

	@Override
	public void addImplicitPolymorphismEntries(AplosContextListener aplosContextListener) {

		ImplicitPolymorphismVariable ipVariable = new ImplicitPolymorphismVariable( CustomerReview.class.getName(), "parent" );
		aplosContextListener.addImplicitPolymorphismEntry(ipVariable, new ImplicitPolymorphismEntry( ProductType.class ) );

		
	}

	@Override
	public ModuleUpgrader createModuleUpgrader() {
		return new EcommerceModuleUpgrader(this);
	}

	@Override
	public ModuleDbConfig createModuleDbConfig() {
		return new EcommerceModuleDbConfig( this );
	}

	@Override
	public Currency updateSessionCurrency(HttpServletRequest request) {
		return null;
	}
}

