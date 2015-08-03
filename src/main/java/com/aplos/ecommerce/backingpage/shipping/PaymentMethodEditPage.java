package com.aplos.ecommerce.backingpage.shipping;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.backingpage.OkBtnListener;
import com.aplos.common.backingpage.SaveBtnListener;
import com.aplos.common.interfaces.PositionedBean;
import com.aplos.common.utils.JSFUtil;
import com.aplos.common.utils.PositionedBeanHelper;
import com.aplos.ecommerce.beans.PaymentMethod;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=PaymentMethod.class)
public class PaymentMethodEditPage extends EditPage {
	
	private static final long serialVersionUID = 2478822309976589889L;
	private PositionedBeanHelper positionedBeanHelper;

	public PaymentMethodEditPage() {
		getEditPageConfig().setOkBtnActionListener( new OkBtnListener( this ) {
			private static final long serialVersionUID = -7282012600484001369L;

			@Override
			public void actionPerformed(boolean redirect) {
				getPositionedBeanHelper().saveCurrentPositionedBean();
				super.actionPerformed(redirect);
			}
		});

		getEditPageConfig().setApplyBtnActionListener( new SaveBtnListener( this ) {
			private static final long serialVersionUID = -3382808768388575997L;

			@Override
			public void actionPerformed(boolean redirect) {
				getPositionedBeanHelper().saveCurrentPositionedBean();
				super.actionPerformed(redirect);
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean responsePageLoad() {
		boolean continueLoad = super.responsePageLoad();
		if( positionedBeanHelper == null ) {
			List<PaymentMethod> paymentMethodList = new BeanDao( PaymentMethod.class ).setIsReturningActiveBeans(true).getAll();
			setPositionedBeanHelper(new PositionedBeanHelper( null, (List<PositionedBean>) (List<? extends PositionedBean>) paymentMethodList, PaymentMethod.class ));
			PaymentMethod paymentMethod = JSFUtil.getBeanFromScope( PaymentMethod.class );
			getPositionedBeanHelper().setSelectedPositionedBean( paymentMethod );
			getPositionedBeanHelper().setCurrentPositionedBean( paymentMethod );
		}
		return continueLoad;
	}

	public void setPositionedBeanHelper(PositionedBeanHelper positionedBeanHelper) {
		this.positionedBeanHelper = positionedBeanHelper;
	}

	public PositionedBeanHelper getPositionedBeanHelper() {
		return positionedBeanHelper;
	}
}