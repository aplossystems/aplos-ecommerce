package com.aplos.ecommerce.beans.developercmsmodules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.aplos.cms.beans.developercmsmodules.ConfigurableDeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.ShoppingCart;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.EcommerceShoppingCart;
import com.aplos.ecommerce.beans.EcommerceShoppingCartItem;
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.beans.product.ProductInfo;
import com.aplos.ecommerce.developermodulebacking.frontend.SingleProductFeDmb;
import com.aplos.ecommerce.interfaces.RealizedProductRetriever;
import com.aplos.ecommerce.module.EcommerceConfiguration;

@Entity
@DynamicMetaValueKey(oldKey="CROSS_SELLING_MODULE")
public class CrossSellingModule extends ConfigurableDeveloperCmsAtom {

	private static final long serialVersionUID = 444171819875007643L;

	private boolean displayAddButton = true;
	private boolean useShoppingCart = true; //see determineMatchSet
	private int numberOfItemsToDisplay = 6;
	private String title = "Other items you may like"; //optional, not displayed if empty

	@Transient
	private List<RealizedProduct> associatedProductsCache = new ArrayList<RealizedProduct>();
	@Transient
	private HashSet<RealizedProduct> matchSetCache;

	public CrossSellingModule() {
		super();
	}

	@Override
	public String getName() {
		return "Cross Selling";
	}

	/**
	 * This method assembles the list of realized products to be used to make
	 * our suggestions - it can work from shopping cart or individual products
	 * but can easily be extended to work from other items
	 * Kit items will match the parent and all selected sub items, gift voucher
	 * will match any value voucher, everything else will match just itself
	 * @return HashSet<RealizedProduct>
	 */
	private HashSet<RealizedProduct> determineMatchSet() {
		HashSet<RealizedProduct> matchList = new HashSet<RealizedProduct>();
		boolean containsGiftVoucher = false;
		if (useShoppingCart) {
			EcommerceShoppingCart cart = JSFUtil.getBeanFromScope(ShoppingCart.class);
			if (cart != null) {
				if (cart.isNew()) {
					cart.saveDetails();
				}
				cart = new BeanDao(ShoppingCart.class).get(cart.getId());
//				HibernateUtil.initialise( cart, true );
				if (cart != null) {
					for (EcommerceShoppingCartItem item : cart.getEcommerceShoppingCartItems()) {
						matchList.add(item.getRealizedProduct());
						if (item.getRealizedProduct() != null && item.getRealizedProduct().isGiftVoucher()) {
							containsGiftVoucher = true;
						}
						if (item.getKitItems() != null) {
							for (RealizedProduct kitItem : item.getKitItems()) {
								matchList.add(kitItem);
								if (kitItem.isGiftVoucher()) {
									containsGiftVoucher = true;
								}
							}
						}
					}
				}
			}
		} else {
			SingleProductFeDmb singleProductFeDmb = ((SingleProductFeDmb)JSFUtil.getFromTabSession(CommonUtil.getBinding(SingleProductFeDmb.class)));
			if (singleProductFeDmb != null) {
				RealizedProduct realizedProduct = singleProductFeDmb.getCurrentRealizedProduct();
				if (realizedProduct != null) {
					matchList.add(realizedProduct);
					if (realizedProduct.isGiftVoucher()) {
						containsGiftVoucher = true;
					}
				}
			}
		}
		if (containsGiftVoucher) {
			BeanDao dao = new BeanDao(RealizedProduct.class);
			dao.setSelectCriteria("DISTINCT(bean.id), bean.productInfo.id");
			dao.addQueryTable( "type", "bean.productInfo.product.productTypes" );
			dao.setWhereCriteria("type.id=" + EcommerceConfiguration.getEcommerceConfiguration().getGiftVoucherProductType().getId());
			matchList.addAll(dao.setIsReturningActiveBeans(true).getAll());
		}
		return matchList;
	}

	@Override
	public DeveloperCmsAtom getCopy() {
		CrossSellingModule copiedAtom = new CrossSellingModule();
		copiedAtom.setNumberOfItemsToDisplay(getNumberOfItemsToDisplay());
		copiedAtom.setTitle(getTitle());
		copiedAtom.setDisplayAddButton(isDisplayAddButton());
		copiedAtom.setUseShoppingCart(isUseShoppingCart());
		return copiedAtom;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setNumberOfItemsToDisplay(int numberOfItemsToDisplay) {
		this.numberOfItemsToDisplay = numberOfItemsToDisplay;
	}

	public int getNumberOfItemsToDisplay() {
		return numberOfItemsToDisplay;
	}

	/**
	 * Get the list of suggestions of products that match the current items
	 * note that this purposefully works off of realized products rather than productinfo's
	 * @return
	 */
	public List<RealizedProduct> getAssociatedProductList() {
		HashSet<RealizedProduct> matchSet = determineMatchSet();
		if (matchSet.size() < 1) {
			return new ArrayList<RealizedProduct>();
		}
		if (matchSetCache == null || !matchSetCache.equals(matchSet)) {
			matchSetCache = matchSet;
			BeanDao dao = new BeanDao(EcommerceShoppingCartItem.class);
			dao.setSelectCriteria("bean.realizedProduct.id,bean.realizedProduct.defaultImageDetails,bean.realizedProduct.productInfo.mapping,bean.realizedProduct.productInfo.product.name,bean.realizedProduct.price,bean.realizedProduct.crossoutPrice");
			if (EcommerceConfiguration.getEcommerceSettingsStatic().isPreOrderAllowedOnOrder()) {
				dao.setWhereCriteria("(bean.realizedProduct.quantity > 0 OR bean.realizedProduct.stockAvailableFromDate != null)");
			} else {
				dao.setWhereCriteria("bean.realizedProduct.quantity > 0");
			}
			StringBuffer matchIdSet = new StringBuffer();
			java.util.Iterator<RealizedProduct> iter = matchSet.iterator();
		    while (iter.hasNext()) {
		    	matchIdSet.append(iter.next().getId());
		    	if (iter.hasNext()) {
		    		matchIdSet.append(",");
		    	}
		    }
		    String matchingCartQuery = "SELECT DISTINCT(item.shoppingCart.id) FROM " + AplosBean.getTableName(EcommerceShoppingCartItem.class) + " item WHERE item.realizedProduct.id IN (" + matchIdSet + ")";
		    dao.addWhereCriteria("bean.shoppingCart.id IN (" + matchingCartQuery + ")");
			dao.addWhereCriteria("bean.realizedProduct.id NOT IN (" + matchIdSet + ")");
		    dao.setGroupBy("bean.realizedProduct.id");
		    //dao.setOrderBy("COUNT(bean.realizedProduct.id)"); //TODO: order by frequency - but we dont want the count in the select criteria...
		    dao.setOrderBy("RAND()");
		    dao.setMaxResults(numberOfItemsToDisplay);
		    List<EcommerceShoppingCartItem> items = dao.setIsReturningActiveBeans(true).getAll();
			associatedProductsCache = new ArrayList<RealizedProduct>();
			for (EcommerceShoppingCartItem item : items) {
				associatedProductsCache.add(item.getRealizedProduct());
			}
		}
		StringBuffer matchIdSet = new StringBuffer();
		java.util.Iterator<RealizedProduct> iter = matchSet.iterator();
	    while (iter.hasNext()) {
	    	matchIdSet.append(iter.next().getId());
	    	if (iter.hasNext()) {
	    		matchIdSet.append(",");
	    	}
	    }
	    for (RealizedProduct tempRp : associatedProductsCache) {
	    	if (matchIdSet.length() > 0) {
	    		 matchIdSet.append(",");
			}
	    	matchIdSet.append(tempRp.getId());
	    }
		if (associatedProductsCache.size() < numberOfItemsToDisplay) {
			java.util.Iterator<RealizedProduct> matchIter = matchSet.iterator();
		    while (matchIter.hasNext()) {
		    	if (associatedProductsCache.size() < numberOfItemsToDisplay) {
		    		RealizedProduct realizedProduct = matchIter.next();
			    	ProductInfo info = new BeanDao(ProductInfo.class).get(realizedProduct.getProductInfo().getId());
//			    	HibernateUtil.initialise( info, true );
					for (RealizedProductRetriever rpr : info.getOptionalAccessoriesList()) {
						if (associatedProductsCache.size() < numberOfItemsToDisplay) {
							RealizedProduct addition = rpr.retrieveRealizedProduct(realizedProduct);
							if (!associatedProductsCache.contains(addition) && !matchSet.contains(addition)) {
								associatedProductsCache.add(addition);
								if (matchIdSet.length() > 0) {
						    		 matchIdSet.append(",");
								}
						    	matchIdSet.append(addition.getId());
							}
						} else {
							break;
						}
					}
		    	} else {
		    		break;
		    	}
		    }
		}
		if (associatedProductsCache.size() < numberOfItemsToDisplay) {
			int maxToSelect = numberOfItemsToDisplay - associatedProductsCache.size();
			BeanDao randDao = new BeanDao(RealizedProduct.class);
			randDao.addWhereCriteria("bean.id NOT IN (" + matchIdSet + ")");
			randDao.setMaxResults(maxToSelect);
			randDao.setOrderBy("RAND()");
			associatedProductsCache.addAll(randDao.setIsReturningActiveBeans(true).getAll());
		}
		return associatedProductsCache;
	}

	public void setDisplayAddButton(boolean displayAddButton) {
		this.displayAddButton = displayAddButton;
	}

	public boolean isDisplayAddButton() {
		return displayAddButton;
	}

	public void setUseShoppingCart(boolean useShoppingCart) {
		this.useShoppingCart = useShoppingCart;
	}

	public boolean isUseShoppingCart() {
		return useShoppingCart;
	}

}
