package com.aplos.ecommerce.backingpage.transaction;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.MenuStep;
import com.aplos.common.beans.ShoppingCartItem;
import com.aplos.common.interfaces.MenuStepBacking;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.CompanyContact;
import com.aplos.ecommerce.beans.EcommerceShoppingCartItem;
import com.aplos.ecommerce.beans.ShippingBoxOrder;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.beans.couriers.AvailableShippingService;
import com.aplos.ecommerce.beans.couriers.CourierService;
import com.aplos.ecommerce.beans.couriers.ShippingBox;
import com.aplos.ecommerce.beans.couriers.StaticShippingService;
import com.aplos.ecommerce.module.EcommerceConfiguration;

@ManagedBean
@ViewScoped
public class TransactionEditDeliveryMethodPage extends EditPage implements MenuStepBacking {
	private static final long serialVersionUID = 7326478333995050469L;
	private List<SelectItem> shippingSelectItems;
	private AvailableShippingService availableShippingService;
	private boolean isShowingStandardWeight;
	private boolean isShowingVolumetricWeight;

	private ShippingBox selectedShippingBox;
	private int shippingBoxQuantity;

	@Override
	public boolean responsePageLoad() {
		super.responsePageLoad();

		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		if( availableShippingService == null ) {
			availableShippingService = transaction.getEcommerceShoppingCart().getAvailableShippingService();
		}

		shippingSelectItems = determineShippingSelectItems();
		return true;
	}

	@Override
	public void beforeLeavingMenuStep( MenuStep menuStep ) {
		Transaction transaction = JSFUtil.getBeanFromScope( Transaction.class );
		if( menuStep.getMenuWizard().getLatestStepIdx() == menuStep.getMenuWizard().getCurrentStepIdx() ) {
			if( transaction.getDispatchDate() == null && transaction.getDeliveryRequiredByDate() != null ) {
				Calendar cal = new GregorianCalendar();
				cal.setTime( transaction.getDeliveryRequiredByDate() );
				cal.add( Calendar.DATE, -1 );
				transaction.setDispatchDate( cal.getTime() );
			}
		}
		transaction.getEcommerceShoppingCart().updateCachedNetDeliveryCost();
		transaction.getEcommerceShoppingCart().updateCachedDeliveryVatValue();

	}

	public SelectItem[] getShippingBoxSelectItemBeans() {
		return AplosBean.getSelectItemBeans( ShippingBox.class );
	}

	public List<ShoppingCartItem> getRelevantCartItems() {
		Transaction transaction = JSFUtil.getBeanFromScope( Transaction.class );
		List<ShoppingCartItem> relevantCartItems = new ArrayList<ShoppingCartItem>();
		relevantCartItems.addAll( transaction.getEcommerceShoppingCart().getItems() );
		if( transaction.getRealizedProductReturn() != null && transaction.getRealizedProductReturn().determineReturnProduct() != null ) {
			EcommerceShoppingCartItem returnCartItem = new EcommerceShoppingCartItem( transaction.getEcommerceShoppingCart(), transaction.getRealizedProductReturn().determineReturnProduct() );
			relevantCartItems.add( returnCartItem );
		}
		return relevantCartItems;
	}

	public void removeShippingBoxOrder() {
		Transaction transaction = JSFUtil.getBeanFromScope( Transaction.class );
		ShippingBoxOrder shippingBoxOrder = (ShippingBoxOrder) JSFUtil.getRequest().getAttribute( "tableBean" );
		transaction.getEcommerceShoppingCart().getShippingBoxOrderList().remove( shippingBoxOrder );
		transaction.getEcommerceShoppingCart().updateCachedBoxVolume();
	}

	public void addShippingBoxOrder() {
		Transaction transaction = JSFUtil.getBeanFromScope( Transaction.class );
		ShippingBoxOrder shippingBoxOrder = new ShippingBoxOrder();
		shippingBoxOrder.setShippingBox( getSelectedShippingBox() );
		shippingBoxOrder.setQuantity( getShippingBoxQuantity() );
		transaction.getEcommerceShoppingCart().addShippingBoxOrder( shippingBoxOrder );
		transaction.getEcommerceShoppingCart().updateCachedBoxVolume();
	}

	public List<SelectItem> determineShippingSelectItems() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);

		BeanDao aqlBeanDao = new BeanDao(CourierService.class);
		List<CourierService> couriers = aqlBeanDao.setIsReturningActiveBeans(true).getAll();

		List<SelectItem> shippingSelectItems = new ArrayList<SelectItem>();
		List<AvailableShippingService> availableShippingServices = new ArrayList<AvailableShippingService>();

		for( CourierService tempCourier : couriers ) {
			availableShippingServices.addAll( tempCourier.getAvailableShippingServices(transaction, false) );
		}
		availableShippingServices = AvailableShippingService.sortByCachedCost( availableShippingServices );

		setShowingStandardWeight( false );
		setShowingVolumetricWeight( false );
		for( AvailableShippingService tempAvailableShippingService : availableShippingServices ) {
			if( tempAvailableShippingService.getShippingService().isUsingVolumetricWeight() ) {
				setShowingVolumetricWeight( true );
			}
			if( !tempAvailableShippingService.getShippingService().isUsingVolumetricWeight() ) {
				setShowingStandardWeight( true );
			}
		}

		StaticShippingService customShippingService = EcommerceConfiguration.getEcommerceConfiguration().getCustomShippingService();
		AvailableShippingService customAvailableShippingService = customShippingService.createAvailableShippingService(); 
		shippingSelectItems.add( new SelectItem( customAvailableShippingService, customShippingService.getDisplayName() ) );
		if( transaction.getEcommerceShoppingCart().getAvailableShippingService() == null ) {
//			HibernateUtil.initialise(customAvailableShippingService, true);
			transaction.getEcommerceShoppingCart().setAvailableShippingService( customAvailableShippingService );
		}
		if( transaction.getEcommerceShoppingCart().getCustomer() instanceof CompanyContact ) {
			StaticShippingService customersShippingService = EcommerceConfiguration.getEcommerceConfiguration().getCustomersShippingService();
			AvailableShippingService availableShippingService = new AvailableShippingService();
			availableShippingService.setShippingService( customersShippingService );
			availableShippingService.updateCachedServiceName();
			availableShippingService.updateCachedCost( transaction.getEcommerceShoppingCart() );
			availableShippingServices.add( availableShippingService );
		}

		for( AvailableShippingService tempService : availableShippingServices ) {
			shippingSelectItems.add(new SelectItem( tempService, tempService.getCachedServiceName() + " (" + tempService.getCachedCost() + ")" ));
		}

		return shippingSelectItems;
	}

	public List<SelectItem> getShippingSelectItems() {
		return shippingSelectItems;
	}

	public void shippingServiceUpdated() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		if( !availableShippingService.equals( transaction.getEcommerceShoppingCart().getAvailableShippingService() ) ) {
			availableShippingService.loadAndInitialise();
			transaction.getEcommerceShoppingCart().setAvailableShippingService( availableShippingService );
			transaction.shippingServiceUpdated();
		}
	}

	public void additionalWeightUpdated() {
		Transaction transaction = JSFUtil.getBeanFromScope(Transaction.class);
		transaction.getEcommerceShoppingCart().updateWeightCachedValue();
		shippingSelectItems = determineShippingSelectItems();
	}

	public void redirectToDeliveryDetails() {
		JSFUtil.redirect( TransactionEditDeliveryDetailsPage.class );
	}

	public void setAvailableShippingService(AvailableShippingService availableShippingService) {
		this.availableShippingService = availableShippingService;
	}

	public AvailableShippingService getAvailableShippingService() {
		return availableShippingService;
	}

	public void setSelectedShippingBox(ShippingBox selectedShippingBox) {
		this.selectedShippingBox = selectedShippingBox;
	}

	public ShippingBox getSelectedShippingBox() {
		return selectedShippingBox;
	}

	public void setShippingBoxQuantity(int shippingBoxQuantity) {
		this.shippingBoxQuantity = shippingBoxQuantity;
	}

	public int getShippingBoxQuantity() {
		return shippingBoxQuantity;
	}

	public void setShowingStandardWeight(boolean isShowingStandardWeight) {
		this.isShowingStandardWeight = isShowingStandardWeight;
	}

	public boolean isShowingStandardWeight() {
		return isShowingStandardWeight;
	}

	public void setShowingVolumetricWeight(boolean isShowingVolumetricWeight) {
		this.isShowingVolumetricWeight = isShowingVolumetricWeight;
	}

	public boolean isShowingVolumetricWeight() {
		return isShowingVolumetricWeight;
	}
}
