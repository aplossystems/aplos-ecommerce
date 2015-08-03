package com.aplos.ecommerce.developermodulebacking.frontend;

import java.math.BigDecimal;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.CmsWebsite;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.AplosUrl;
import com.aplos.common.beans.CompanyDetails;
import com.aplos.common.beans.ShoppingCart;
import com.aplos.common.beans.Website;
import com.aplos.common.enums.JsfScope;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.appconstants.EcommerceAppConstants;
import com.aplos.ecommerce.beans.EcommerceShoppingCartItem;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.module.EcommerceConfiguration;

@ManagedBean
@ViewScoped
public class PaymentSuccessFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = 814496496082462051L;

	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		super.responsePageLoad(developerCmsAtom);
		Transaction transaction = getPostPaymentOrder();
		
		if( transaction == null ) {
			JSFUtil.addMessage( "You cannot access this page without a completed order" );
			JSFUtil.redirect(new AplosUrl("/"), true);
			return false;
		}
		transaction.addToScope( JsfScope.REQUEST );
		JSFUtil.getRequest().setAttribute( CommonUtil.getBinding( ShoppingCart.class ), transaction.getEcommerceShoppingCart() );
		return true;
	}
	
	public boolean isRenderingGoogleScript() {
		Website website = Website.getCurrentWebsiteFromTabSession();
		if( !JSFUtil.isLocalHost() && !CommonUtil.isNullOrEmpty(((CmsWebsite)website).getGoogleAnalyticsId()) ) {
			return true;
		} else {
			return false;
		}
	}
	
	public Transaction getPostPaymentOrder() {
		if( "paypal".equals( JSFUtil.getRequest().getParameter( "paymentType" ) ) ) {
			return JSFUtil.getBeanFromScope( Transaction.class );
		} else {
			return (Transaction)JSFUtil.getSessionTemp().getAttribute(EcommerceAppConstants.POST_PAYMENT_ORDER);
		}
	}

	public String getGoogleEcommerceScript() {
		Website website = Website.getCurrentWebsiteFromTabSession();
		StringBuffer strBuf = new StringBuffer();

		Transaction ecommerceOrder = getPostPaymentOrder();
		CompanyDetails companyDetails = CommonConfiguration.getCommonConfiguration().getDefaultCompanyDetails();
//		CompanyDetails companyDetails = new CompanyDetails();
//		companyDetails.setName("Aplos Systems");
//		EcommerceOrder ecommerceOrder = (EcommerceOrder) new AqlBeanDao(EcommerceOrder.class).load(43);

		if( ((CmsWebsite)website).getGoogleAnalyticsId() != null ) {
			strBuf.append( "<script type='text/javascript'>\n" );
	        strBuf.append( "var pageTracker = _gat._getTracker('" + ((CmsWebsite)website).getGoogleAnalyticsId() + "');\n" );
	        strBuf.append( "pageTracker._trackPageview();\n" );
	        strBuf.append( "pageTracker._addTrans(" );
	        strBuf.append( "'" + ecommerceOrder.getId() + "'," );
	        strBuf.append( "'" + companyDetails.getCompanyName() + "'," );
	        strBuf.append( "'" + ecommerceOrder.getEcommerceShoppingCart().getCachedGrossTotalAmount() + "'," );
	        //  TODO create a tax amount in the system and add it here.
	        strBuf.append( "'" + ecommerceOrder.getEcommerceShoppingCart().getCachedTotalVatAmount() + "'," );
	        strBuf.append( "'" + ecommerceOrder.getEcommerceShoppingCart().getDeliveryCost() + "'," );
	        strBuf.append( "'','','');\n\n" );

	        for( EcommerceShoppingCartItem cartItem : ecommerceOrder.getEcommerceShoppingCart().getEcommerceShoppingCartItems() ) {
	        	strBuf.append( "pageTracker._addItem(" );
	        	strBuf.append( "'" + ecommerceOrder.getId() + "'," );
	        	strBuf.append( "'" + cartItem.getId() + "'," );
	        	strBuf.append( "'" + cartItem.getItemName() + "'," );
	        	strBuf.append( "''," );
	        	strBuf.append( "'" + cartItem.getSingleItemFinalPrice(true) + "'," );
	        	strBuf.append( "'" + cartItem.getQuantity() + "'" );
	        	strBuf.append( ");\n\n" );
	        }

	        strBuf.append( "pageTracker._trackTrans();" );
	        strBuf.append( "</script>" );
		}

        return strBuf.toString();
	}

	public String getItemsTotal() {
		Transaction transaction = getPostPaymentOrder();
		if (transaction != null) {
			return FormatUtil.formatTwoDigit( transaction.getEcommerceShoppingCart().getCachedGrossTotalAmount().doubleValue() );
		}
		else {
			return "";
		}
	}

	public String getDeliveryCharge() {
		Transaction transaction = getPostPaymentOrder();
		if (transaction != null) {
			return transaction.getEcommerceShoppingCart().getAvailableShippingService().getConvertedCachedCostString(transaction.getEcommerceShoppingCart().getCurrency());
		}
		else {
			return "";
		}
	}

	public String getOrderTotal() {
		Transaction transaction = getPostPaymentOrder();
		if (transaction != null) {
			BigDecimal convertedDeliveryCost = transaction.getEcommerceShoppingCart().getAvailableShippingService().getConvertedCachedCost(transaction.getEcommerceShoppingCart().getCurrency());
			BigDecimal invoiceTotal = transaction.getEcommerceShoppingCart().getCachedGrossTotalAmount().add( convertedDeliveryCost );
			return FormatUtil.formatTwoDigit( invoiceTotal.doubleValue() );
		}
		else {
			return "";
		}
	}

	public static boolean isViewingPaymentSuccessPage() {
		return JSFUtil.getAplosContextOriginalUrl().equals("/" + EcommerceConfiguration.getEcommerceCprsStatic().getCheckoutSuccessCpr().getCmsPage().getMapping() + ".aplos");
	}

	public boolean getIsViewingPaymentSuccessPage() {
		return isViewingPaymentSuccessPage();
	}
}
