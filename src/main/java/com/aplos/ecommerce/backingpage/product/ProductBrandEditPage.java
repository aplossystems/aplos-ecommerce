package com.aplos.ecommerce.backingpage.product;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.product.ProductBrand;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=ProductBrand.class)
public class ProductBrandEditPage extends EditPage {

	private static final long serialVersionUID = 8252423978125506967L;

	@Override
	public void okBtnAction() {
		applyBtnAction();
		JSFUtil.redirect(getEditPageConfig().getOkBtnActionListener().getBackingPage().getBeanDao().getListPageClass());
	}

	@Override
	public void applyBtnAction() {
		String newMsg="";
		AplosBean associatedBean = getEditPageConfig().getApplyBtnActionListener().getBackingPage().resolveAssociatedBean();
		if (associatedBean.getId() == null) {
			newMsg =  " Frontend pages have been created for you.";
			associatedBean.saveDetails();
		} else {
			associatedBean.saveDetails();
		}
		JSFUtil.addMessage("Saved succesfully." + newMsg,FacesMessage.SEVERITY_INFO);
	}

}
