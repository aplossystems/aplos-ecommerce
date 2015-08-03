package com.aplos.ecommerce.backingpage.product.size;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.product.ProductSizeCategory;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=ProductSizeCategory.class)
public class ProductSizeCategoryEditPage extends EditPage {

	private static final long serialVersionUID = 9201275022718366697L;

//	@Override
//	public void okBtnAction() {
//		ProductSizeCategory category = JSFUtil.getBeanFromScope(ProductSizeCategory.class);
//		ProductSizeType type = JSFUtil.getBeanFromScope(ProductSizeType.class);
//		type.addSizeCategory(category);
//		type.saveDetails();
//		//dont call super or we end up with two of this object (because of the cascade?)
//		return new BackingPageUrl(ProductSizeCategoryListPage.class);
//	}

	public SelectItem[] getPositionIdxSelectItems() {
		ProductSizeCategory category = (ProductSizeCategory)JSFUtil.getBeanFromScope(ProductSizeCategory.class);
		BeanDao aqlBeanDao = new BeanDao( ProductSizeCategory.class );
		aqlBeanDao.addWhereCriteria( "bean.productSizeType=" + category.getProductSizeType().getId() );
		aqlBeanDao.setOrderBy( "positionIdx" );
		@SuppressWarnings("unchecked")
		List<ProductSizeCategory> categoryList = aqlBeanDao.getAll();
		SelectItem[] selectItems = new SelectItem[categoryList.size()+1];
		ProductSizeCategory currentProductSizeCategory = ((ProductSizeCategory)JSFUtil.getBeanFromScope(ProductSizeCategory.class));
		int currentPosition = -1;
		if( currentProductSizeCategory != null && currentProductSizeCategory.getPositionIdx() != null ) {
			currentPosition = currentProductSizeCategory.getPositionIdx();
		}
		int lastPosition = 0;
		for (int i=0; i < categoryList.size(); i++) {
			if (categoryList.get(i).getPositionIdx().equals(currentPosition)) {
				selectItems[i] = new SelectItem(categoryList.get(i).getPositionIdx(),"Current Position");
			} else {
				selectItems[i] = new SelectItem(categoryList.get(i).getPositionIdx(),"Before " + categoryList.get(i).getName() + ((i==0)?" (beginning of list)":""));
			}
			lastPosition = categoryList.get(i).getPositionIdx();
		}
		if( lastPosition == 0 ) {
			lastPosition = -1;
		}
		selectItems[categoryList.size()] = new SelectItem(lastPosition+1,"End of list");
		return selectItems;
	}

}
