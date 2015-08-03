package com.aplos.ecommerce.developermodulebacking.frontend;

import java.util.Collections;
import java.util.List;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;

import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.CustomerReview;
import com.aplos.common.beans.CustomerReview.CustomerReviewSortType;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.WebsiteProductTypeReview;
import com.aplos.ecommerce.beans.developercmsmodules.ProductTypeReviewCmsAtom;
import com.aplos.ecommerce.beans.product.ProductType;

@ManagedBean
@CustomScoped(value="#{ tabSession }")
public class ProductTypeReviewFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = -8863883043535231567L;
	private ProductType selectedProductType;
	private CustomerReviewSortType customerReviewSortType = CustomerReviewSortType.A_TO_Z;

	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		return super.responsePageLoad(developerCmsAtom);
	}

	public List<CustomerReview> getSortedCustomerReviewList() {
		List<CustomerReview> sortedCustomerReviewList = CustomerReview.retrieveCustomerReviewListStatic( getSelectedProductType() );

		Collections.sort( sortedCustomerReviewList, customerReviewSortType.getComparator() );
		return sortedCustomerReviewList;
	}

	public void setCustomerReviewSortType(CustomerReviewSortType customerReviewSortType) {
		if( customerReviewSortType == null ) {
			ApplicationUtil.handleError( new Exception( "Null customer review sort type" ), false );
		}
		this.customerReviewSortType = customerReviewSortType;
	}

	public CustomerReviewSortType getCustomerReviewSortType() {
		return customerReviewSortType;
	}

	public void productTypeSelected() {
		WebsiteProductTypeReview productTypeReview = JSFUtil.getBeanFromRequest( "productTypeReview" );
		ProductType loadedProductType = new BeanDao( ProductType.class ).get( productTypeReview.getProductType().getId() );
		selectedProductType = loadedProductType;
	}

	public void setSelectedProductType(ProductType selectedProductType) {
		this.selectedProductType = selectedProductType;
	}

	public ProductType getSelectedProductType() {
		if (selectedProductType == null) {
			ProductTypeReviewCmsAtom productTypeReviewCmsAtom = JSFUtil.getBeanFromScope(ProductTypeReviewCmsAtom.class);
			if (productTypeReviewCmsAtom != null && productTypeReviewCmsAtom.getSortedProductTypeReviews().size() == 1) {
				selectedProductType = productTypeReviewCmsAtom.getSortedProductTypeReviews().get(0).getProductType();
			}
		}
		return selectedProductType;
	}
}
