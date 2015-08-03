package com.aplos.ecommerce.beans.developercmsmodules;

import java.util.ArrayList;
import java.util.List;

import com.aplos.cms.beans.developercmsmodules.ConfigurableDeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Cascade;
import com.aplos.common.annotations.persistence.CascadeType;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.OneToMany;
import com.aplos.common.interfaces.PositionedBean;
import com.aplos.common.utils.PositionedBeanHelper;
import com.aplos.ecommerce.beans.WebsiteProductTypeReview;

@Entity
@DynamicMetaValueKey(oldKey="PRODUCT_TYPE_REVIEW_ATOM")
public class ProductTypeReviewCmsAtom extends ConfigurableDeveloperCmsAtom {
	private static final long serialVersionUID = 2944843459547551823L;

	@OneToMany
	@Cascade({CascadeType.ALL})
	private List<WebsiteProductTypeReview> productTypeReviews = new ArrayList<WebsiteProductTypeReview>();

	@Override
	public String getName() {
		return "Product type reviews";
	}

	@Override
	public boolean initBackend() {
		super.initBackend();
//		HibernateUtil.initialise( this, true );
		return true;
	}

	@Override
	public boolean initFrontend(boolean isRequestPageLoad) {
		super.initFrontend(isRequestPageLoad);
//		HibernateUtil.initialise( this, true );
		return true;
	}

	@SuppressWarnings("unchecked")
	public List<WebsiteProductTypeReview> getSortedProductTypeReviews() {
		return (List<WebsiteProductTypeReview>) PositionedBeanHelper.getSortedPositionedBeanList( (List<PositionedBean>) (List<? extends PositionedBean>) productTypeReviews );
	}

	@Override
	public DeveloperCmsAtom getCopy() {
		ProductTypeReviewCmsAtom productTypeReviewAtom = new ProductTypeReviewCmsAtom();
		productTypeReviewAtom.setProductTypeReviews(new ArrayList<WebsiteProductTypeReview>( getProductTypeReviews() ));
		return productTypeReviewAtom;
	}

	public void setProductTypeReviews(List<WebsiteProductTypeReview> productTypeReviews) {
		this.productTypeReviews = productTypeReviews;
	}

	public List<WebsiteProductTypeReview> getProductTypeReviews() {
		return productTypeReviews;
	}
}
