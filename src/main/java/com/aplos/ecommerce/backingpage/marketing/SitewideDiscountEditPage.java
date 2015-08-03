package com.aplos.ecommerce.backingpage.marketing;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.beans.AplosAbstractBean;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.SitewideDiscount;
import com.aplos.ecommerce.beans.product.ProductBrand;
import com.aplos.ecommerce.beans.product.ProductType;
import com.aplos.ecommerce.module.EcommerceConfiguration;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=SitewideDiscount.class)
public class SitewideDiscountEditPage extends EditPage {
	private static final long serialVersionUID = 3231452907566106903L;

	@Override
	public void okBtnAction() {
		SitewideDiscount sitewideDiscount = JSFUtil.getBeanFromScope(SitewideDiscount.class);
		if (ensureSitewideDiscountUnique(sitewideDiscount)) {
			sitewideDiscount.saveDetails();
			SitewideDiscount.updateSitewideDiscounts();
			super.cancelBtnAction(); //okbtnaction would make us wastefully save again
		} else if (sitewideDiscount.getProductBrand() != null) {
			if (sitewideDiscount.getProductType() != null) {
				JSFUtil.addMessageForError("A sitewide discount already exists for the selected type and brand.");
			} else {
				JSFUtil.addMessageForError("A sitewide discount already exists for the selected brand.");
			}
		} else if (sitewideDiscount.getProductType() != null) {
			JSFUtil.addMessageForError("A sitewide discount already exists for the selected product type.");
		} else {
			JSFUtil.addMessageForError("A sitewide discount already exists which affects all products (select a brand or type instead).");
		}
	}

	private boolean ensureSitewideDiscountUnique(SitewideDiscount sitewideDiscount) {
		BeanDao dao = new BeanDao(SitewideDiscount.class);
		if (sitewideDiscount.getProductBrand() != null) {
			dao.setWhereCriteria("bean.productBrand.id=" + sitewideDiscount.getProductBrand().getId());
			if (sitewideDiscount.getProductType() != null) {
				dao.addWhereCriteria("bean.productType.id=" + sitewideDiscount.getProductType().getId());
			}
		} else if (sitewideDiscount.getProductType() != null) {
			dao.setWhereCriteria("bean.productType.id=" + sitewideDiscount.getProductType().getId());
		} else {
			dao.setWhereCriteria("bean.productBrand = null AND bean.productType = null");
		}
		if (!sitewideDiscount.isNew()) {
			dao.addWhereCriteria("bean.id!=" + sitewideDiscount.getId());
		}
		return dao.getCountAll() < 1;
	}

	public SelectItem[] getProductBrandSelectItems() {
		BeanDao dao = new BeanDao(ProductBrand.class);
		return AplosAbstractBean.getSelectItemBeansWithNotSelected(dao.setIsReturningActiveBeans(true).getAll(), "All Brands");
	}

	public SelectItem[] getProductTypeSelectItems() {
		BeanDao dao = new BeanDao(ProductType.class);
		dao.setWhereCriteria("bean.id != " + EcommerceConfiguration.getEcommerceConfiguration().getGiftVoucherProductType().getId());
		return AplosAbstractBean.getSelectItemBeansWithNotSelected(dao.setIsReturningActiveBeans(true).getAll(), "All Product Types (Except Gift Vouchers)");
	}
}





