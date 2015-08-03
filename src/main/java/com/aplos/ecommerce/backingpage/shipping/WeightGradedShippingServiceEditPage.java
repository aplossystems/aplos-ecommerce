package com.aplos.ecommerce.backingpage.shipping;

import java.math.BigDecimal;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.appconstants.AplosScopedBindings;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.couriers.CourierService;
import com.aplos.ecommerce.beans.couriers.ShippingStep;
import com.aplos.ecommerce.beans.couriers.WeightGradedShippingService;

@ManagedBean
@SessionScoped
@AssociatedBean(beanClass=WeightGradedShippingService.class)
public class WeightGradedShippingServiceEditPage extends EditPage {

	private static final long serialVersionUID = -6650934803029405561L;
	private BigDecimal maxWeight;
	private BigDecimal cost;

	public WeightGradedShippingServiceEditPage() {
		getBeanDao().setListPageClass( CourierShippingServiceListPage.class );
	}

	@Override
	public void applyBtnAction() {
		saveAction();
	}

	@Override
	public void okBtnAction() {
		saveAction();
		super.cancelBtnAction();
	}

	public void saveAction() {
		WeightGradedShippingService weightGradedShippingService = JSFUtil.getBeanFromScope( WeightGradedShippingService.class );
		boolean wasNew = weightGradedShippingService.isNew();
		super.applyBtnAction();
		if( wasNew ) {
			CourierService courierService = JSFUtil.getBeanFromScope( CourierService.class );
			courierService.getShippingServices().add( weightGradedShippingService );
			courierService.saveDetails();
		}
	}

	public void setMaxWeight(BigDecimal maxWeight) {
		this.maxWeight = maxWeight;
	}

	public BigDecimal getMaxWeight() {
		return maxWeight;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void addWeightGradedSsStep() {
		WeightGradedShippingService weightGradedSs = (WeightGradedShippingService) resolveAssociatedBean();
		ShippingStep weightGradedSsStep = new ShippingStep( maxWeight, cost );
		weightGradedSs.getShippingSteps().add( weightGradedSsStep );

		//maxWeight = cost = 0d;

		if( !weightGradedSs.isNew() ) {
			weightGradedSs.setShippingSteps(weightGradedSs.getWeightSortedShippingSteps());
			weightGradedSs.saveDetails();
		}
	}

	public void removeWeightGradedSsStep() {
		WeightGradedShippingService weightGradedSs = (WeightGradedShippingService) resolveAssociatedBean();
		weightGradedSs.getShippingSteps().remove( JSFUtil.getRequest().getAttribute( AplosScopedBindings.TABLE_BEAN ) );
		weightGradedSs.saveDetails();
	}
}
