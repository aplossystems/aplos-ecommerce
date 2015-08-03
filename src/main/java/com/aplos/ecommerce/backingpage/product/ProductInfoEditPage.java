package com.aplos.ecommerce.backingpage.product;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.beans.AplosAbstractBean;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.VatType;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.beans.product.ProductBrand;
import com.aplos.ecommerce.beans.product.ProductInfo;
import com.aplos.ecommerce.beans.product.ProductSizeType;
import com.aplos.ecommerce.beans.product.ProductType;
import com.aplos.ecommerce.utils.EcommerceUtil;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=ProductInfo.class)
public class ProductInfoEditPage extends EditPage {

	private static final long serialVersionUID = -7181578407063507111L;
	private ProductType optionalAccessoryProductType;
	private ProductBrand optionalAccessoryProductBrand;
	private RealizedProduct selectedOptionalAccessory;
	private SelectItem[] optionalAccessorySelectItemBeans;
	private ProductType selectedProductType;
	private ProductSizeType selectedProductSizeType;
	private String newSearchKeyword;


	public ProductInfoEditPage() {
		updateOptionalAccessoriesSelectItemBeans();
	}

	public boolean isValidationRequired() {
		return validationRequired();
	}

	public String addSearchKeyword() {
		ProductInfo productInfo = ((RealizedProduct) JSFUtil.getBeanFromScope( RealizedProduct.class )).getProductInfo();
		if ( CommonUtil.isNullOrEmpty( getNewSearchKeyword() ) ) {
			JSFUtil.addMessageForError("Please enter valid text for this searchKeyword");
		} else {
			if (productInfo != null) {
				productInfo.addSearchKeyword(getNewSearchKeyword());
			}
		}
		return null;
	}

	public String removeSearchKeyword() {
		ProductInfo productInfo = ((RealizedProduct) JSFUtil.getBeanFromScope( RealizedProduct.class )).getProductInfo();
		String searchKeyword = (String) JSFUtil.getRequest().getAttribute("tableBean");
		if (productInfo != null) {
			productInfo.getSearchKeywordList().remove(searchKeyword);
		}
		return null;
	}

	public SelectItem[] getVatTypeSelectItems() {
		return AplosBean.getSelectItemBeans( VatType.class );
	}

	public void validateMapping(FacesContext context, UIComponent component, Object value) {
		if( validationRequired() ) {
			String mapping = (String) value;
			ProductInfo productInfo = (ProductInfo) resolveAssociatedBean();
			EcommerceUtil.getEcommerceUtil().validateMapping(productInfo, mapping);
		}
	}

	@SuppressWarnings("unchecked")
	public SelectItem[] getProductTypeSelectItemBeans() {
		return AplosBean.getSelectItemBeans( new BeanDao( ProductType.class ).setOrderBy( "bean.name" ).setIsReturningActiveBeans(true).getAll() );
	}

	@SuppressWarnings("unchecked")
	public SelectItem[] getProductSizeTypeSelectItemBeans() {
		return AplosBean.getSelectItemBeans( new BeanDao( ProductSizeType.class ).setOrderBy( "bean.name" ).setIsReturningActiveBeans(true).getAll() );
	}

	public void removeOptionalAccessory() {
		ProductInfo productInfo = ((RealizedProduct) JSFUtil.getBeanFromScope( RealizedProduct.class )).getProductInfo();
		RealizedProduct optionalAccessoryItem = (RealizedProduct) JSFUtil.getRequest().getAttribute( "tableBean" );
		productInfo.getOptionalAccessoriesList().remove( optionalAccessoryItem );
	}

	public void addOptionalAccessoryItem() {
		ProductInfo productInfo = ((RealizedProduct) JSFUtil.getBeanFromScope( RealizedProduct.class )).getProductInfo();
		if( getSelectedOptionalAccessory() != null ) {
			if( productInfo.getOptionalAccessoriesList().contains( getSelectedOptionalAccessory() ) ) {
				JSFUtil.addMessage( "This product is already included in the optional accessory list" );
			} else {
				productInfo.getOptionalAccessoriesList().add( getSelectedOptionalAccessory() );
			}
		} else {
			JSFUtil.addMessage( "Please select a product to add to the optional accessory list" );
		}
	}

	public void updateOptionalAccessoriesSelectItemBeans() {
		BeanDao realizedProductDao = new BeanDao( RealizedProduct.class );
		realizedProductDao.setSelectCriteria( "bean.id, bean.itemCode, bean.productInfo.product.name" );
		realizedProductDao.addQueryTable( "pt", "bean.productInfo.product.productTypes" );
		if( getOptionalAccessoryProductBrand() != null ) {
			realizedProductDao.addWhereCriteria( "bean.productInfo.product.productBrand.id = " + getOptionalAccessoryProductBrand().getId() );
		}
		realizedProductDao.addWhereCriteria( "bean.discontinued != true" );
		realizedProductDao.setGroupBy( "bean.productInfo, bean.productColour" );
		realizedProductDao.setOrderBy( "bean.itemCode" );
//		realizedProductDao.addAliasesForOptimisation( "pt", "productInfo.product.productTypes" );
		try {
			setOptionalAccessorySelectItemBeans(AplosBean.getSelectItemBeans( realizedProductDao.getAll() ));
			RealizedProduct tempRealizedProduct;
			for (SelectItem selectItem : getOptionalAccessorySelectItemBeans()) {
				tempRealizedProduct = (RealizedProduct)selectItem.getValue();
				selectItem.setLabel(tempRealizedProduct.determineItemCode() + " " + tempRealizedProduct.getProductInfo().getProduct().getName());
			}
		} catch( Exception ex ) {
			ex.printStackTrace();
		}
	}

	public void removeProductTypeItem() {
		ProductInfo productInfo = JSFUtil.getBeanFromScope( ProductInfo.class );
		ProductType productTypeItem = (ProductType) JSFUtil.getRequest().getAttribute( "tableBean" );
		productInfo.getProduct().getProductTypes().remove( productTypeItem );
	}

	public void addProductTypeItem() {
		ProductInfo productInfo = JSFUtil.getBeanFromScope( ProductInfo.class );
		if( getSelectedProductType() != null ) {
			if( productInfo.getProduct().getProductTypes().contains( getSelectedProductType() ) ) {
				JSFUtil.addMessage( "This product type is already included in the product type list" );
			} else {
				productInfo.getProduct().getProductTypes().add( getSelectedProductType() );
			}
		} else {
			JSFUtil.addMessage( "Please select a product type to add to the product type list" );
		}
	}


	public void removeProductSizeType() {
		ProductInfo productInfo = JSFUtil.getBeanFromScope( ProductInfo.class );
		ProductSizeType productSizeType = (ProductSizeType) JSFUtil.getRequest().getAttribute( "tableBean" );
		productInfo.getProduct().getAdditionalProductSizeTypes().remove( productSizeType );
	}

	public void addProductSizeType() {
		ProductInfo productInfo = JSFUtil.getBeanFromScope( ProductInfo.class );
		if( getSelectedProductSizeType() != null ) {
			if( productInfo.getProduct().getProductSizeTypes().contains( getSelectedProductSizeType() ) ) {
				JSFUtil.addMessage( "This product type is already included in the product size type list" );
			} else {
				productInfo.getProduct().getAdditionalProductSizeTypes().add( getSelectedProductSizeType() );
			}
		} else {
			JSFUtil.addMessage( "Please select a product size type to add to the product type list" );
		}
	}

	public SelectItem[] getProductBrandSelectItemBeans() {
		SelectItem[] productBrandSelectItemBeans = AplosAbstractBean.getSelectItemBeansWithNotSelected( new BeanDao( ProductBrand.class ).setOrderBy( "bean.name").setIsReturningActiveBeans(true).getAll(), "All");
		return productBrandSelectItemBeans;
	}

	public ProductBrand getOptionalAccessoryProductBrand() {
		return optionalAccessoryProductBrand;
	}

	public void setOptionalAccessoryProductBrand(
			ProductBrand optionalAccessoryProductBrand) {
		this.optionalAccessoryProductBrand = optionalAccessoryProductBrand;
	}

	public ProductType getOptionalAccessoryProductType() {
		return optionalAccessoryProductType;
	}

	public void setOptionalAccessoryProductType(
			ProductType optionalAccessoryProductType) {
		this.optionalAccessoryProductType = optionalAccessoryProductType;
	}

	public SelectItem[] getOptionalAccessorySelectItemBeans() {
		return optionalAccessorySelectItemBeans;
	}

	public void setOptionalAccessorySelectItemBeans(
			SelectItem[] optionalAccessorySelectItemBeans) {
		this.optionalAccessorySelectItemBeans = optionalAccessorySelectItemBeans;
	}

	public void setSelectedProductType(ProductType selectedProductType) {
		this.selectedProductType = selectedProductType;
	}

	public ProductType getSelectedProductType() {
		return selectedProductType;
	}

	public boolean saveBean() {
		ProductInfo productInfo = JSFUtil.getBeanFromScope( ProductInfo.class );
		if (productInfo != null) {
			if (productInfo.getProduct().getProductTypes().size() < 1) {
				addProductTypeItem();
			}
		}
		return super.saveBean();
	}

	public void setSelectedProductSizeType(ProductSizeType selectedProductSizeType) {
		this.selectedProductSizeType = selectedProductSizeType;
	}

	public ProductSizeType getSelectedProductSizeType() {
		return selectedProductSizeType;
	}

	public void setSelectedOptionalAccessory(RealizedProduct selectedOptionalAccessory) {
		this.selectedOptionalAccessory = selectedOptionalAccessory;
	}

	public RealizedProduct getSelectedOptionalAccessory() {
		return selectedOptionalAccessory;
	}

	public String getNewSearchKeyword() {
		return newSearchKeyword;
	}

	public void setNewSearchKeyword(String newSearchKeyword) {
		this.newSearchKeyword = newSearchKeyword;
	}

}
