package com.aplos.ecommerce.backingpage;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.module.EcommerceConfiguration;
import com.aplos.ecommerce.module.EcommerceSettings;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=EcommerceConfiguration.class)
public class EcommerceConfigurationEditPage extends EditPage {
	private static final long serialVersionUID = -8215135201345557409L;
	
	@Override
	public <T extends AplosBean> T resolveAssociatedBean() {
		return resolveAssociatedEditBean();
	}
	
	@Override
	public boolean saveBean() {
		boolean continueSave = super.saveBean();
		((EcommerceSettings) JSFUtil.getBeanFromScope(EcommerceSettings.class)).saveDetails();
		return continueSave;
	}

	@Override
	public boolean responsePageLoad() {
		if( super.resolveAssociatedBean() == null ) {
			EcommerceConfiguration.getEcommerceConfiguration().getSaveableBean().addToScope();
			EcommerceConfiguration.getEcommerceSettingsStatic().getSaveableBean().addToScope();
		}
		checkEditBean();
		super.responsePageLoad();
		return true;
	}
}
