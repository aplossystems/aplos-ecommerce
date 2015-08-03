package com.aplos.ecommerce.backingpage;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.common.annotations.GlobalAccess;
import com.aplos.common.backingpage.BackingPage;
import com.aplos.common.enums.JsfScope;
import com.aplos.common.module.CommonConfiguration;

@ManagedBean
@SessionScoped
@GlobalAccess
public class PayPalDirectPostIFrameContentPage extends BackingPage {
	private static final long serialVersionUID = 2887611270506913504L;

	@Override
	public boolean responsePageLoad() {
		super.responsePageLoad();
		CommonConfiguration.getCommonConfiguration().determinePayPalCfgDetails().addToScope( JsfScope.REQUEST );
		return true;
	}
}
