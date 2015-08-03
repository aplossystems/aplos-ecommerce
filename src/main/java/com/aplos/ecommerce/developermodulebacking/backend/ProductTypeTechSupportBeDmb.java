package com.aplos.ecommerce.developermodulebacking.backend;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.beans.pages.CmsPageRevision;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosAbstractBean;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.interfaces.PositionedBean;
import com.aplos.common.utils.JSFUtil;
import com.aplos.common.utils.PositionedBeanHelper;
import com.aplos.ecommerce.backingpage.product.type.ProductTypeEditPage;
import com.aplos.ecommerce.beans.ProductTypeTechSupport;
import com.aplos.ecommerce.beans.developercmsmodules.ProductTypeTechSupportCmsAtom;
import com.aplos.ecommerce.beans.product.ProductType;

@ManagedBean
@ViewScoped
public class ProductTypeTechSupportBeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = 6440484857084922960L;
	private ProductTypeTechSupportCmsAtom productTypeTechSupportCmsAtom;
	private PositionedBeanHelper positionedBeanHelper;

	@SuppressWarnings("unchecked")
	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		super.responsePageLoad(developerCmsAtom);
		setProductTypeTechSupportCmsAtom((ProductTypeTechSupportCmsAtom) developerCmsAtom);
		if( positionedBeanHelper == null ) {
			setPositionedBeanHelper(new PositionedBeanHelper( (AplosBean) developerCmsAtom, (List<PositionedBean>) (List<? extends PositionedBean>) getProductTypeTechSupportCmsAtom().getProductTypeTechSupports(), ProductTypeTechSupport.class ));
		}
		return true;
	}

	@Override
	public void applyBtnAction() {
		super.applyBtnAction();
		getPositionedBeanHelper().saveCurrentPositionedBean();
		getProductTypeTechSupportCmsAtom().saveDetails();
	}

	public void goToProductTypeEditPage() {
		ProductType productType = ((ProductTypeTechSupport) positionedBeanHelper.getCurrentPositionedBean()).getProductType();
		if( productType == null ) {
			JSFUtil.addMessage( "Please select a product type", FacesMessage.SEVERITY_WARN );
		} else {
			ProductType loadedProductType = new BeanDao( ProductType.class ).get( productType.getId() );
			CmsPageRevision cmsPageRevision = JSFUtil.getBeanFromView( CmsPageRevision.class );
			cmsPageRevision.saveDetails();
			loadedProductType.addToScope();
			JSFUtil.redirect( ProductTypeEditPage.class );
		}
	}

	public void saveProductTypeTechSupportsAndAtom() {
		getPositionedBeanHelper().saveCurrentPositionedBean();
		getProductTypeTechSupportCmsAtom().saveDetails();
	}

	public SelectItem[] getProductTypeSelectItemBeans() {
		BeanDao dao = new BeanDao(ProductType.class);
		return AplosAbstractBean.getSelectItemBeansWithNotSelected(dao.setIsReturningActiveBeans(true).getAll(), "Not Selected");
	}

	public void setPositionedBeanHelper(PositionedBeanHelper positionedBeanHelper) {
		this.positionedBeanHelper = positionedBeanHelper;
	}

	public PositionedBeanHelper getPositionedBeanHelper() {
		return positionedBeanHelper;
	}

	public void setProductTypeTechSupportCmsAtom(
			ProductTypeTechSupportCmsAtom productTypeTechSupportCmsAtom) {
		this.productTypeTechSupportCmsAtom = productTypeTechSupportCmsAtom;
	}

	public ProductTypeTechSupportCmsAtom getProductTypeTechSupportCmsAtom() {
		return productTypeTechSupportCmsAtom;
	}
}
