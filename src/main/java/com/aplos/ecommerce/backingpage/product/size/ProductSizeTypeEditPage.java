package com.aplos.ecommerce.backingpage.product.size;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.utils.CommonUtil;
import com.aplos.ecommerce.beans.playcom.PlaySizeType;
import com.aplos.ecommerce.beans.product.ProductSizeType;
import com.aplos.ecommerce.enums.AmazonProductType;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=ProductSizeType.class)
public class ProductSizeTypeEditPage extends EditPage {
	private static final long serialVersionUID = 7353981493951539712L;
	
	public SelectItem[] getPlaySizeTypeSelectItems() {
		return AplosBean.getSelectItemBeansWithNotSelected( PlaySizeType.class );
	}
	
	public List<SelectItem> getAmazonProductTypeSelectItems() {
		return CommonUtil.getEnumSelectItemsWithNotSelected(AmazonProductType.class);
	}

}
