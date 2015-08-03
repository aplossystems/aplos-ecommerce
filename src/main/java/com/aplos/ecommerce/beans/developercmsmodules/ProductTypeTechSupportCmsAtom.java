package com.aplos.ecommerce.beans.developercmsmodules;

import java.util.ArrayList;
import java.util.List;

import com.aplos.cms.beans.developercmsmodules.ConfigurableDeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Cascade;
import com.aplos.common.annotations.persistence.CascadeType;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.OneToMany;
import com.aplos.common.interfaces.PositionedBean;
import com.aplos.common.utils.PositionedBeanHelper;
import com.aplos.ecommerce.beans.ProductTypeTechSupport;

@Entity
@DynamicMetaValueKey(oldKey="PRODUCT_TYPE_TECH_SUPPORT_ATOM")
public class ProductTypeTechSupportCmsAtom extends ConfigurableDeveloperCmsAtom {
	private static final long serialVersionUID = 2944843459547551823L;

	@OneToMany
	@Cascade({CascadeType.ALL})
	private List<ProductTypeTechSupport> productTypeTechSupports = new ArrayList<ProductTypeTechSupport>();

	@Column(columnDefinition="LONGTEXT")
	private String description;

	@Override
	public String getName() {
		return "Product type tech support";
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
	public List<ProductTypeTechSupport> getSortedProductTypeTechSupports() {
		return (List<ProductTypeTechSupport>) PositionedBeanHelper.getSortedPositionedBeanList( new ArrayList<PositionedBean>( productTypeTechSupports ) );
	}

	@Override
	public DeveloperCmsAtom getCopy() {
		ProductTypeTechSupportCmsAtom productTypeTechSupportAtom = new ProductTypeTechSupportCmsAtom();
		productTypeTechSupportAtom.setProductTypeTechSupports(new ArrayList<ProductTypeTechSupport>( getProductTypeTechSupports() ));
		return productTypeTechSupportAtom;
	}

	public void setProductTypeTechSupports(List<ProductTypeTechSupport> productTypeTechSupports) {
		this.productTypeTechSupports = productTypeTechSupports;
	}

	public List<ProductTypeTechSupport> getProductTypeTechSupports() {
		return productTypeTechSupports;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
