package com.aplos.ecommerce.backingpage.transaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.primefaces.event.SelectEvent;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.beans.MenuStep;
import com.aplos.common.beans.ShoppingCartItem;
import com.aplos.common.interfaces.MenuStepBacking;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.backingpage.product.RealizedProductEditPage;
import com.aplos.ecommerce.beans.CompanyContact;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.EcommerceShoppingCartItem;
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.beans.listbeans.RealizedProductListBean;
import com.aplos.ecommerce.enums.TransactionType;

@ManagedBean
@SessionScoped
@AssociatedBean(beanClass=Transaction.class)
public class TransactionEditAddItemsPage extends EditPage implements MenuStepBacking {
	private static final long serialVersionUID = -4474482439764129885L;

	private RealizedProductListBean selectedRealizedProductListBean;
	private List<SelectItem> transactionTypeSelectItems;

	@Override
	public boolean responsePageLoad() {
		super.responsePageLoad();
		createTransactionTypeSelectItems();
		return true;
	}

	@SuppressWarnings("unchecked")
	public List<RealizedProductListBean> retrieveRealizedProductResults(String query) {
		BeanDao realizedProductDao = new BeanDao( RealizedProduct.class );
		realizedProductDao.setListBeanClass(RealizedProductListBean.class);
		realizedProductDao.setSelectCriteria( "bean.productInfo.product.name as productName, bean.price, bean.itemCode, bean.productInfo.product.itemCode, pc.name, ps.name, psc.suffix, bean.id, bean.discontinued, bean.active" );
		realizedProductDao.addWhereCriteria("CONCAT(bean.productInfo.product.name, ' ', COALESCE( ps.name, '' ), ' ', COALESCE( psc.suffix, '' ), ' ', COALESCE( pc.name, '' )) like :similarSearchText OR bean.itemCode like :similarSearchText");
		realizedProductDao.addQueryTable( "ps", "bean.productSize" );
		realizedProductDao.addQueryTable( "psc", "bean.productSizeCategory" );
		realizedProductDao.addQueryTable( "pc", "bean.productColour" );
		realizedProductDao.setMaxResults( 20 );
//		realizedProductDao.addAliasesForOptimisation( "ps", "productSize" );
//		realizedProductDao.addAliasesForOptimisation( "psc", "productSizeCategory" );
//		realizedProductDao.addAliasesForOptimisation( "pc", "productColour" );
		realizedProductDao.setIsReturningActiveBeans( true );
		realizedProductDao.setNamedParameter( "similarSearchText", "%" + query + "%" );
		return realizedProductDao.getAll();
	}

	@Override
	public void beforeLeavingMenuStep( MenuStep nextMenuStep ) {
		/* 
		 * This is only required because the onblur method does not get called on 
		 * the live site, there must be some time lag issue with the PrimeFaces.ab method
		 */
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		for (EcommerceShoppingCartItem shoppingCartItem : transaction.getEcommerceShoppingCart().getEcommerceShoppingCartItems()) {
			shoppingCartItem.updateAllValues();
		}
	}

	public void createTransactionTypeSelectItems() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		Customer customer = transaction.getEcommerceShoppingCart().getCustomer();
		List<SelectItem> availableTransactionTypesSelectItems = new ArrayList<SelectItem>();

		if( transaction.getEcommerceShoppingCart().getCachedNetTotalAmount().compareTo( new BigDecimal( 0 ) ) < 0 ) {
			if( !TransactionType.REFUND.equals( transaction.getTransactionType() ) ) {
				transaction.setTransactionType( null );
			}
			availableTransactionTypesSelectItems.add(new SelectItem(TransactionType.REFUND, TransactionType.REFUND.getLabel()));
		} else {
			if( TransactionType.REFUND.equals( transaction.getTransactionType() ) ) {
				transaction.setTransactionType( null );
			}
			if (!(customer instanceof CompanyContact) || !((CompanyContact)customer).getCompany().isCreditAllowed() ) {
				availableTransactionTypesSelectItems.add(new SelectItem(TransactionType.PRO_FORMA, TransactionType.PRO_FORMA.getLabel()));
				availableTransactionTypesSelectItems.add(new SelectItem(TransactionType.LOAN, TransactionType.LOAN.getLabel()));
			}
			else {
				availableTransactionTypesSelectItems.add(new SelectItem(TransactionType.QUOTE, TransactionType.QUOTE.getLabel()));
				availableTransactionTypesSelectItems.add(new SelectItem(TransactionType.LOAN, TransactionType.LOAN.getLabel()));
				availableTransactionTypesSelectItems.add(new SelectItem(TransactionType.PURCHASE_ORDER, TransactionType.PURCHASE_ORDER.getLabel()));
			}
		}
		transactionTypeSelectItems = availableTransactionTypesSelectItems;
	}

	public boolean isValidationRequired() {
		return validationRequired( "transactionMenuSteps" );
	}

	public void itemQuantityUpdated() {
		EcommerceShoppingCartItem ecommerceShoppingCartItem = JSFUtil.getBeanFromRequest( "cartItem" );
		if( ecommerceShoppingCartItem.getQuantity() == 0 ) {
			removeCartItem( ecommerceShoppingCartItem );
		}
		ecommerceShoppingCartItem.updateAllValues();
		ecommerceShoppingCartItem.getShoppingCart().updateCachedValues(false);
	}

	public void goToProduct() {
		EcommerceShoppingCartItem cartItem = (EcommerceShoppingCartItem) JSFUtil.getRequest().getAttribute( "cartItem" );
		RealizedProduct loadedRealizedProduct = new BeanDao( RealizedProduct.class ).get( cartItem.getRealizedProduct().getId() );
//		HibernateUtil.initialise( loadedRealizedProduct, true );
		loadedRealizedProduct.addToScope();
		JSFUtil.redirect( RealizedProductEditPage.class );
	}

	public List<SelectItem> getRealizedProductSelectItems() {
		BeanDao aqlBeanDao = new BeanDao(RealizedProduct.class);

		List<RealizedProduct> realizedProducts = aqlBeanDao.setIsReturningActiveBeans(true).getAll();

		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		for (RealizedProduct rp : realizedProducts) {
			//display name contains size/colour
			selectItems.add(new SelectItem(rp, rp.getDisplayName())); //ProductInfo().getProduct().getName()));
		}

		return selectItems;
//		return AplosAbstractBean.getSelectItemBeans(RealizedProduct.class);
	}

	public void increaseCartItemQuantity() {
		ShoppingCartItem shoppingCartItem = ((ShoppingCartItem)JSFUtil.getRequest().getAttribute("cartItem"));
		shoppingCartItem.setQuantity(shoppingCartItem.getQuantity()+1);
	}

	public void decreaseCartItemQuantity() {
		ShoppingCartItem shoppingCartItem = ((ShoppingCartItem)JSFUtil.getRequest().getAttribute("cartItem"));
		if (shoppingCartItem.getQuantity() <= 1) {
			removeCartItem(shoppingCartItem);
		}
		else {
			shoppingCartItem.setQuantity(shoppingCartItem.getQuantity()-1);
		}
	}

	public void removeCartItem() {
		removeCartItem( (ShoppingCartItem)JSFUtil.getRequest().getAttribute("cartItem") );
	}

	public void removeCartItem( ShoppingCartItem shoppingCartItem ) {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		transaction.getEcommerceShoppingCart().getItems().remove(shoppingCartItem);
	}

	public void addCustomProductToCart() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		transaction.addCustomProductToCart();
	}

	public String getProductDisplayString( Transaction transaction, RealizedProductListBean realizedProductListBean ) {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append( CommonUtil.getStringOrEmpty( realizedProductListBean.determineItemCode() ) );
		strBuf.append( " " +  realizedProductListBean.getProductName() );
		strBuf.append( " " +  realizedProductListBean.getDisplayName() );
		strBuf.append( " (" + transaction.getCurrency().getPrefix() );
		strBuf.append( realizedProductListBean.getDeterminedPrice() );
		strBuf.append( transaction.getCurrency().getSuffix() + ")" );

		return strBuf.toString();
	}

	public String getProductDisplayStyle( Transaction transaction, RealizedProductListBean realizedProductListBean ) {
		if( realizedProductListBean.isDiscontinued() ) {
			return "color:red";
		} else {
			return "";
		}
	}

	public void addRealizedProduct( SelectEvent event ) {
		RealizedProductListBean realizedProductListBean = (RealizedProductListBean) event.getObject();
		if( realizedProductListBean != null ) {
			RealizedProduct loadedRealizedProduct = new BeanDao( RealizedProduct.class ).get( realizedProductListBean.getId() );
//			HibernateUtil.initialise( loadedRealizedProduct, true );
			Transaction transaction = JSFUtil.getBeanFromScope( Transaction.class );
			if( transaction.getEcommerceShoppingCart().addToCart(loadedRealizedProduct,false) == null ) {
				JSFUtil.addMessage("We are currently out of stock on this item");
			}
			setSelectedRealizedProductListBean( null );
			JSFUtil.getRequest().setAttribute( "autoBean", null );
		}
	}

	public List<SelectItem> getTypeSelectItems() {
		return transactionTypeSelectItems;
	}

	public RealizedProductListBean getSelectedRealizedProductListBean() {
		return selectedRealizedProductListBean;
	}

	public void setSelectedRealizedProductListBean(
			RealizedProductListBean selectedRealizedProductListBean) {
		this.selectedRealizedProductListBean = selectedRealizedProductListBean;
	}
}
