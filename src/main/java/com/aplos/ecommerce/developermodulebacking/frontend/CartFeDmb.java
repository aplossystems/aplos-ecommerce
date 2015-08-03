 package com.aplos.ecommerce.developermodulebacking.frontend;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.CmsPageUrl;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.AplosUrl;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.Currency;
import com.aplos.common.beans.ShoppingCart;
import com.aplos.common.enums.CartAbandonmentIssue;
import com.aplos.common.enums.PaymentGateway;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.common.utils.ComponentUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.EcommerceShoppingCart;
import com.aplos.ecommerce.beans.EcommerceShoppingCartItem;
import com.aplos.ecommerce.beans.GiftVoucher;
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.beans.couriers.AvailableShippingService;
import com.aplos.ecommerce.beans.couriers.CourierService;
import com.aplos.ecommerce.beans.couriers.FreeShippingService;
import com.aplos.ecommerce.module.EcommerceConfiguration;
import com.aplos.ecommerce.utils.EcommerceUtil;

@ManagedBean
@ViewScoped
public class CartFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = 4507691390950903481L;
	private String giftVoucherCode;
	private Transaction transaction;

	public CartFeDmb() {
	}

	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		super.responsePageLoad(developerCmsAtom);
		if( getTransaction() == null ) {
			Transaction transaction = JSFUtil.getBeanFromScope( Transaction.class );
			if( transaction == null ) {
				transaction = JSFUtil.getBeanFromRequest( Transaction.class );
			}
			if( transaction != null ) {
				setTransaction( (Transaction) transaction.getSaveableBean() );
			}
		}
		getEcommerceShoppingCart();
		return true;
	}
	
	public String getContinueShoppingText() {
		return "Continue shopping";
	}
	
	public String getPaymentPageContentId() {
		return ComponentUtil.findComponentFromRoot( "payment_page_content" ).getClientId();
	}
	
	public EcommerceShoppingCart getEcommerceShoppingCart() {
		Transaction transaction = getTransaction();
		if( transaction == null ) {
			return EcommerceUtil.getEcommerceUtil().getOrCreateEcommerceShoppingCart();
		} else {
			return transaction.getEcommerceShoppingCart();
		}
	}
	
	/*
	 * This is primarily to check whether the free service is still applicable as
	 * removing products may put the cart value below the free shipping threshold.
	 */
	public void checkShippingService() {
		Transaction transaction = JSFUtil.getBeanFromScope( Transaction.class );
		if( transaction != null ) {
			transaction.removeShippingServiceIfNotApplicable( true );
		}	
	}

	public BigDecimal getGrandTotal() {
		EcommerceShoppingCart ecommerceShoppingCart = getEcommerceShoppingCart();
		return ecommerceShoppingCart.getGrandTotal(true);
	}


	public boolean isItemsRequirePostage() {
		EcommerceShoppingCart ecommerceShoppingCart = getEcommerceShoppingCart();
		return ecommerceShoppingCart.isItemsRequireShipping();
	}

	public String getFooterColSpan() {
		int colCount = 2;
		if (EcommerceConfiguration.getEcommerceSettingsStatic().isUsingProductColours()) {
			colCount++;
		}
		if (EcommerceConfiguration.getEcommerceSettingsStatic().isUsingProductSizes()) {
			colCount++;
		}
		if (EcommerceConfiguration.getEcommerceSettingsStatic().isCartShowingImages()) {
			colCount++;
		}
		if (EcommerceConfiguration.getEcommerceSettingsStatic().isUsingItemCodes()) {
			colCount++;
		}
		if (isViewingCartPage()) {
			colCount++;
		}
		if (EcommerceConfiguration.getEcommerceSettingsStatic().isCartShowingDiscountColumn()) {
			EcommerceShoppingCart ecommerceShoppingCart = getEcommerceShoppingCart();
			if (ecommerceShoppingCart.getIsPromotionApplied()) {
				colCount++;
			}
		}
		return String.valueOf(colCount);
	}

	public void redirectToPayPalDirectPost() {
		Transaction transaction = JSFUtil.getBeanFromScope( Transaction.class );
		if( transaction != null ) {
			EcommerceConfiguration.getEcommerceCprsStatic().redirectToPayPalDirectPostCpr();
		} else {
			EcommerceShoppingCart shoppingCart = JSFUtil.getBeanFromScope( ShoppingCart.class );
			shoppingCart.setPayPalExpressRequested( true );
			EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutSignInOrSignUpCpr();
		}

	}

	public boolean isPayPalExpressAvailable() {
		if( CommonConfiguration.getCommonConfiguration().isUsingPayPalExpress() &&
				PaymentGateway.PAYPAL.equals( CommonConfiguration.getCommonConfiguration().getDefaultPaymentGateway() ) &&
				EcommerceConfiguration.getEcommerceCprsStatic().getPayPalDirectPostCpr() != null ) {
			return true;
		} else {
			return false;
		}
	}

	public void changeCurrency() {
		Currency newCurrency = (Currency) JSFUtil.getRequest().getAttribute("currency");
		newCurrency.addToScope();
		ShoppingCart shoppingCart = getEcommerceShoppingCart();
		if( shoppingCart != null ) {
			shoppingCart.setCurrency( newCurrency );
		}
		Transaction ecommerceOrder = (Transaction) JSFUtil.getBeanFromScope(Transaction.class);
		if( ecommerceOrder != null ) {
			ecommerceOrder.getEcommerceShoppingCart().setCurrency( newCurrency );
		}
	}

	public String getGrandTotalString() {
		return FormatUtil.formatTwoDigit( getGrandTotal().doubleValue() );
	}

	public static boolean isViewingCartPage() {
		if (EcommerceConfiguration.getEcommerceCprsStatic().getCheckoutCartCpr() == null) {
			return false;
		}
		return JSFUtil.getAplosContextOriginalUrl().contains("/" + EcommerceConfiguration.getEcommerceCprsStatic().getCheckoutCartCpr().getCmsPage().getMapping() + ".aplos");
	}

	public boolean getIsViewingCartPage() {
		return isViewingCartPage();
	}

	/* this method is so we always display a value as the £ will always show even if no price is set yet*/
	public String getShippingChargeString() {
		EcommerceShoppingCart ecommerceShoppingCart = getEcommerceShoppingCart();
		AvailableShippingService availableShippingService = ecommerceShoppingCart.getAvailableShippingService();
		if (availableShippingService==null ||
				availableShippingService.getConvertedCachedCostString(ecommerceShoppingCart.getCurrency()).equals("")) {
			return "0.00";
		}
		return availableShippingService.getConvertedCachedCostString(ecommerceShoppingCart.getCurrency());
	}

	public boolean isCartItemUpdatable() {
		EcommerceShoppingCartItem cartItem = ((EcommerceShoppingCartItem)JSFUtil.getRequest().getAttribute("cartItem"));
		return cartItem.getIsUpdatable();
	}

	public boolean isCartTableUpdatable() {
		if( JSFUtil.getAplosContextOriginalUrl().endsWith("/" + EcommerceConfiguration.getEcommerceCprsStatic().getCheckoutPaymentCpr().getCmsPage().getMapping() + ".aplos") ||
				(EcommerceConfiguration.getEcommerceCprsStatic().getCheckoutCartCpr() != null &&
				JSFUtil.getAplosContextOriginalUrl().endsWith("/" + EcommerceConfiguration.getEcommerceCprsStatic().getCheckoutCartCpr().getCmsPage().getMapping() + ".aplos")) ) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isMoreStockAvailable() {
		EcommerceShoppingCartItem cartItem = ((EcommerceShoppingCartItem)JSFUtil.getRequest().getAttribute("cartItem"));
		if (cartItem.getRealizedProduct() != null && 	
			(EcommerceConfiguration.getEcommerceSettingsStatic().isOutOfStockProductAllowedOnOrder() ||
			(cartItem.getRealizedProduct().getStockAvailableFromDate() != null && EcommerceConfiguration.getEcommerceSettingsStatic().isPreOrderAllowedOnOrder())	|| //preorders
			(cartItem.getRealizedProduct().getQuantity() > cartItem.getQuantity())) && cartItem.getIsUpdatable() ) {
			return true;
		}
		return false;
	}

	public void increaseQuantity() {
		EcommerceShoppingCartItem item = (EcommerceShoppingCartItem)JSFUtil.getRequest().getAttribute("cartItem");
		((EcommerceShoppingCart) item.getShoppingCart()).increaseQuantity( item );
	}

	public void decreaseQuantity() {
		EcommerceShoppingCartItem item = (EcommerceShoppingCartItem)JSFUtil.getRequest().getAttribute("cartItem");
		((EcommerceShoppingCart) item.getShoppingCart()).decreaseQuantity( item );
		
		checkShippingService();
	}

	public void removeCartItem() {
		EcommerceShoppingCartItem cartItem = (EcommerceShoppingCartItem) JSFUtil.getRequest().getAttribute( "cartItem" );
		ShoppingCart shoppingCart = getEcommerceShoppingCart();
		shoppingCart.removeCartItem( cartItem );
		
		checkShippingService();
	}

	public static String showCart() {
		String serverName = JSFUtil.getRequest().getServerName();

		JSFUtil.redirect(new CmsPageUrl(EcommerceConfiguration.getEcommerceCprsStatic().getCheckoutCartCpr().getCmsPage()));
		return null;
	}

	public String goToCheckout() {
		redirectIfBasketNotEmptyAndShippingSet(false);
		return null;
	}

	public String goToCheckoutCurrentUserAware() {
		return goToCheckoutCurrentUserAware( false );
	}

	public String goToCheckoutCurrentUserAware( boolean isUsingPayPalExpress ) {
		if( isUsingPayPalExpress ) {
			EcommerceShoppingCart shoppingCart = JSFUtil.getBeanFromScope( ShoppingCart.class );
			shoppingCart.setPayPalExpressRequested( true );
		}
		redirectIfBasketNotEmptyAndShippingSet(true);
		return null;
	}

	public boolean getIsGoToCheckoutDisabled() {
		return getEcommerceShoppingCart().getEcommerceShoppingCartItems() == null || getEcommerceShoppingCart().getEcommerceShoppingCartItems().size() < 1;
	}

	protected void redirectIfBasketNotEmptyAndShippingSet(boolean currentUserAware) {
		EcommerceShoppingCart shoppingCart = getEcommerceShoppingCart();
		if (shoppingCart == null || shoppingCart.getItems() == null || shoppingCart.getItems().size() < 1) {
			EcommerceUtil.getEcommerceUtil().addAbandonmentIssueToCart( CartAbandonmentIssue.BASKET_EMPTY );
			JSFUtil.addMessage( "You cannot checkout - Your shopping basket is empty", FacesMessage.SEVERITY_WARN );
		} else {
			shoppingCart.saveDetails();
			if (currentUserAware) {
				if (JSFUtil.getBeanFromScope(Customer.class) != null && ((Customer)JSFUtil.getBeanFromScope(Customer.class)).isLoggedIn()) {
					EcommerceUtil.getEcommerceUtil().checkOrCreateOrder( true, true );
				} else {
					EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutSignInOrSignUpCpr();
				}
			} else {
				EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutSignInOrSignUpCpr();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public List<RealizedProduct> getCrossSellProducts() {
		List<EcommerceShoppingCartItem> items = getEcommerceShoppingCart().getEcommerceShoppingCartItems();
		List<RealizedProduct> returnList = null;
		if( items.size() > 0 ) {
			String basketItemIds= "";
			String basketItemProductInfoIds = "";
			for (int i=0; i < items.size(); i++) {
				if (basketItemIds != "") {
					basketItemIds += ",";
					basketItemProductInfoIds += ",";
				}
				basketItemIds += items.get(i).getRealizedProduct().getId();
				basketItemProductInfoIds += items.get(i).getRealizedProduct().getProductInfo().getId();
			}
			if (basketItemIds.equals("")) {
				basketItemIds = "-1";
			}

			//this query gets items other users bought together
			//  Had to use a matrix join here because I need to use EcommerceShoppingCartItem to get
			//  the realized product, however it's been placed inefficently and will
			//  need to be improved.
			
			// TODO Needs to be written as is for older olders.
//			String hql = "SELECT DISTINCT(si.realizedProduct) " +
//					"FROM EcommerceOrder eco, EcommerceShoppingCartItem si " +
//					"JOIN eco.ecommerceShoppingCart sc " +
//					"WHERE sc.id = si.shoppingCart.id AND eco.ecommerceShoppingCart.customer.id IN " +
//						"(SELECT eo.ecommerceShoppingCart.customer.id " +
//						"FROM EcommerceOrder eo, EcommerceShoppingCartItem i " +
//						"JOIN eo.ecommerceShoppingCart s " +
//						"WHERE i.shoppingCart.id = s.id AND i.realizedProduct.id IN (" + basketItemIds + ")) " +
//					"AND si.realizedProduct.productInfo.id NOT IN (" + basketItemProductInfoIds + ")) " +
//					"GROUP BY RealizedProduct " +
//					"ORDER BY COUNT(RealizedProduct)";
//
//			returnList = HibernateUtil.getCurrentSession().createQuery( hql ).setMaxResults(6).list();
			if ((returnList==null || returnList.size() < 6) && items.size() > 0) {

				List<RealizedProduct> optionalAccessoriesList;
				outer:for (int i=0; i < items.size(); i++) {
					RealizedProduct tempRealizedProduct = items.get(i).getRealizedProduct();
					optionalAccessoriesList = tempRealizedProduct.getProductInfo().getRealizedOptionalAccessoriesList(tempRealizedProduct);
					for (int j=0; j < optionalAccessoriesList.size(); j++) {
						if (returnList.size() < 6) {
							if (!returnList.contains(optionalAccessoriesList.get(j))) {
								returnList.add(optionalAccessoriesList.get(j));
							}
						} else {
							break outer;
						}
					}
				}

	//			for (int i=0; i < return_list.size(); i++) {
	//				basketItemIds += "," + return_list.get(i).getId();
	//			}
	//
	//			hql = "SELECT DISTINCT(rp) " +
	//			"FROM RealizedProduct rp " +
	//			"WHERE rp.id IN " +
	//			"(SELECT wiwi.id FROM RealizedProduct rp WHERE wiwi.id IN ())" +
	//			"AND rp.id NOT IN (" + basketItemIds + ")) " +
	//			"GROUP BY RealizedProduct";
	//
	//			List<RealizedProduct> wearItWith_list = (List<RealizedProduct>)HibernateUtil.getCurrentSession().createQuery( hql ).setMaxResults(6-return_list.size()).list();
	//			if (wearItWith_list != null) {
	//				return_list.addAll(wearItWith_list);
	//			}
			}
		}

		if ( returnList==null || returnList.size() == 0) {
			returnList = new ArrayList<RealizedProduct>();
		}

		return returnList;
	}



	//########


	//Gift Voucher related methods

	public void removeGiftVoucher() {
		GiftVoucher giftVoucher = (GiftVoucher) JSFUtil.getRequest().getAttribute( "giftVoucher" );
		ShoppingCart shoppingCart = getEcommerceShoppingCart();
		((EcommerceShoppingCart)shoppingCart).getGiftVouchersUsed().remove( giftVoucher );
		shoppingCart.updateCachedValues(false);
		// This is to fix a bug with the outputPanel.  It doesn't remove the last item from
		// the ui:repeat so the page must be reloaded.
		if( shoppingCart.getItems().size() == 0 ) {
			JSFUtil.redirect(new AplosUrl(JSFUtil.getAplosContextOriginalUrl()), true );
		}
	}

	public String redeemGiftVoucher() {
		if (giftVoucherCode != null && !giftVoucherCode.equals("")) {
			if (giftVoucherCode.startsWith("GV")) {
				BeanDao dao = new BeanDao(GiftVoucher.class);
				dao.setWhereCriteria("bean.uniqueVoucherCode='" + giftVoucherCode + "'");
				GiftVoucher voucher = (GiftVoucher) dao.setIsReturningActiveBeans(true).getFirstResult();
				if (voucher != null) {
					if (voucher.getClaimedBy() == null && voucher.isUsed() == false) {
						ShoppingCart shoppingCart = getEcommerceShoppingCart();
						if (!((EcommerceShoppingCart)shoppingCart).getGiftVouchersUsed().contains(voucher)) {
							if( shoppingCart != null ) {

								useVoucher((EcommerceShoppingCart)shoppingCart, voucher);

							}
						} else {
							JSFUtil.addMessageForError("This voucher is already redeemed in your " + EcommerceConfiguration.getEcommerceConfiguration().getCartDisplayName() + ".");
						}
					} else if (voucher.isUsed() == false) {
						JSFUtil.addMessageForError("This voucher is already redeemed in an outstanding " + EcommerceConfiguration.getEcommerceConfiguration().getCartDisplayName() + ", please look at previous orders to finish claiming this item.");
					} else {
						JSFUtil.addMessageForError("This voucher has already been used.");
					}
					return null;
				} else {
					JSFUtil.addMessageForError("The voucher code you have entered has not been recognised. Please check you have entered it correctly.");
				}
			} else {
				JSFUtil.addMessageForError("The voucher code you have entered does not appear to be valid. This box is for gift vouchers only, if you are trying to redeem another type of coupon this can be done on the payment stage of the checkout.");
			}
		} else {
			JSFUtil.addMessageForError("Please enter the code for the gift voucher you wish to redeem.");
		}
		return null;
	}

	private void useVoucher(EcommerceShoppingCart ecommerceShoppingCart, GiftVoucher voucher) {
		voucher.setClaimedBy(ecommerceShoppingCart.getCustomer());
		if (voucher.isStoreCreditVoucher()) {
			ecommerceShoppingCart.getCustomer().addStoreCredit(voucher.getVoucherCredit());
			voucher.setUsed(true); //dont update product vouchers until transaction complete
		}
		ecommerceShoppingCart.getCustomer().saveDetails();
		voucher.saveDetails();
		ecommerceShoppingCart.addGiftVoucherUsed(voucher);
	}

	public void setGiftVoucherCode(String giftVoucherCode) {
		this.giftVoucherCode = giftVoucherCode;
	}

	public String getGiftVoucherCode() {
		return giftVoucherCode;
	}

	//Store Credit related methods

	public String getTotalCreditAmountString() {
		Customer customer = getEcommerceShoppingCart().getCustomer();
		if (customer != null) {
			return FormatUtil.formatTwoDigit(customer.getStoreCreditAmount());
		} else {
			return "0.00";
		}
	}

	public boolean getCreditEqualToOrExceedsOrderTotal() {
		EcommerceShoppingCart cart = getEcommerceShoppingCart();
		Customer customer = cart.getCustomer();
		if (customer != null) {
			if (customer.getStoreCreditAmount().compareTo(cart.getCachedGrossTotalAmount()) >= 0) {
				return true;
			}
		}
		return false;
	}

	public boolean getCreditExceedsOrderTotal() {
		EcommerceShoppingCart cart = getEcommerceShoppingCart();
		Customer customer = cart.getCustomer();
		if (customer != null) {
			if (customer.getStoreCreditAmount().compareTo(cart.getCachedGrossTotalAmount()) > 0) {
				return true;
			}
		}
		return false;
	}

	public BigDecimal getCreditRemainingAfterOrder() {
		EcommerceShoppingCart cart = getEcommerceShoppingCart();
		Customer customer = cart.getCustomer();
		if (customer != null) {
			BigDecimal subTotal = cart.getCachedGrossTotalAmount().add(cart.getDeliveryCost()).add(cart.getAdminCharge());
			if (subTotal.doubleValue() <= customer.getStoreCreditAmount().doubleValue()) {
				return customer.getStoreCreditAmount().subtract(subTotal);
			} else {
				return new BigDecimal(0);
			}
		} else {
			return new BigDecimal(0);
		}
	}

	public String getCreditRemainingAfterOrderString() {
		return FormatUtil.formatTwoDigit(getCreditRemainingAfterOrder());
	}

	public boolean isPostageNoticeRequired() {
		if( !EcommerceConfiguration.getEcommerceSettingsStatic().isEverythingFreeShipping() &&
				EcommerceUtil.getEcommerceUtil().cartContainsNonFreeShippingItems(getEcommerceShoppingCart()) ) {
			
			CourierService freeCourierService = EcommerceConfiguration.getEcommerceConfiguration().getFreeCourierService();
			FreeShippingService freeShippingService = FreeShippingService.getApplicableShippingService( freeCourierService, true );
			
			if( freeShippingService != null ) {
				return freeShippingService.isPostageNoticeRequired(getEcommerceShoppingCart().getCachedGrossTotalAmount());	
			}
		}

		return false;
	}

	public String getPostageNotice() {
		CourierService freeCourierService = EcommerceConfiguration.getEcommerceConfiguration().getFreeCourierService();
		FreeShippingService freeShippingService = FreeShippingService.getApplicableShippingService( freeCourierService, true );
		if ( freeShippingService.isBelowThreshold( getEcommerceShoppingCart().getCachedGrossTotalAmount() ) ) {
			return "<h3>Did you know?</h3><span>If you spend over " + FormatUtil.formatCurrentCurrency(freeShippingService.getThreshold().subtract(getEcommerceShoppingCart().getCachedNetTotalAmount())) + " to your cart to benefit!</span>";
		} else {
			return "<h3>Free Shipping</h3><span>You'll be glad to hear, since you've spent more than " + FormatUtil.formatCurrentCurrency(freeShippingService.getThreshold()) + " you have qualified for free shipping on your entire order!<br/><small>Simply select '" + freeCourierService.getDisplayName() + "' from the dropdown on the payment page.</small></span>";
		}
	}

	public boolean isCurrencyNoticeRequired() {
		Currency defaultCurrency = CommonConfiguration.getCommonConfiguration().getDefaultCurrency();
		if( defaultCurrency != null && EcommerceConfiguration.getEcommerceSettingsStatic().isAlwaysChargedInDefaultCurrency() ) {
			if(!getEcommerceShoppingCart().getCurrency().equals(defaultCurrency)) {
				return true;
			}
		}
		return false;
	}

	public String getCurrencyNotice() {
		Currency defaultCurrency = CommonConfiguration.getCommonConfiguration().getDefaultCurrency();
		Currency shownCurrency = getEcommerceShoppingCart().getCurrency();
		return "<h3>Currency Notice</h3><span>Prices shown in " + shownCurrency.getSymbol() + " (" + shownCurrency.getPrefixOrSuffix() + ") are estimates only. All transactions will be charged in " + defaultCurrency.getSymbol() + " (" + defaultCurrency.getPrefixOrSuffix() + ").<br/><small>Order total in " + defaultCurrency.getSymbol() + " (" + defaultCurrency.getPrefixOrSuffix() + ") is " + defaultCurrency.getPrefix() + getEcommerceShoppingCart().getGrandTotalString(true, false) + defaultCurrency.getSuffix() + "</small></span>";
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

}







