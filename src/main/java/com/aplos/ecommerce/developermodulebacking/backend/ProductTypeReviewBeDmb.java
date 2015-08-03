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
import com.aplos.ecommerce.backingpage.product.type.ProductTypeCustomerReviewListPage;
import com.aplos.ecommerce.beans.WebsiteProductTypeReview;
import com.aplos.ecommerce.beans.developercmsmodules.ProductTypeReviewCmsAtom;
import com.aplos.ecommerce.beans.product.ProductType;

@ManagedBean
@ViewScoped
public class ProductTypeReviewBeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = -1329221188780483773L;
	private ProductTypeReviewCmsAtom productTypeReviewCmsAtom;
	private PositionedBeanHelper positionedBeanHelper;

	@SuppressWarnings("unchecked")
	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		super.responsePageLoad(developerCmsAtom);
		setProductTypeReviewCmsAtom((ProductTypeReviewCmsAtom) developerCmsAtom);
		if( positionedBeanHelper == null ) {
			setPositionedBeanHelper(new PositionedBeanHelper( (AplosBean) developerCmsAtom, (List<PositionedBean>) (List<? extends PositionedBean>) getProductTypeReviewCmsAtom().getProductTypeReviews(), WebsiteProductTypeReview.class ));
		}
		return true;
	}

	@Override
	public void applyBtnAction() {
		super.applyBtnAction();
		getPositionedBeanHelper().saveCurrentPositionedBean();
		getProductTypeReviewCmsAtom().saveDetails();
	}

	public void goToCustomerReviewListPage() {
		ProductType productType = ((WebsiteProductTypeReview) positionedBeanHelper.getCurrentPositionedBean()).getProductType();
		if( productType == null ) {
			JSFUtil.addMessage( "Please select a product type", FacesMessage.SEVERITY_WARN );
		} else {
			ProductType loadedProductType = new BeanDao( ProductType.class ).get( productType.getId() );
			CmsPageRevision cmsPageRevision = JSFUtil.getBeanFromView( CmsPageRevision.class );
			cmsPageRevision.saveDetails();
			loadedProductType.addToScope();
			JSFUtil.redirect( ProductTypeCustomerReviewListPage.class );
		}
	}

	public void saveProductTypeReviewsAndAtom() {
		getPositionedBeanHelper().saveCurrentPositionedBean();
		getProductTypeReviewCmsAtom().saveDetails();
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

	public void setProductTypeReviewCmsAtom(ProductTypeReviewCmsAtom productTypeReviewCmsAtom) {
		this.productTypeReviewCmsAtom = productTypeReviewCmsAtom;
	}

	public ProductTypeReviewCmsAtom getProductTypeReviewCmsAtom() {
		return productTypeReviewCmsAtom;
	}
}
