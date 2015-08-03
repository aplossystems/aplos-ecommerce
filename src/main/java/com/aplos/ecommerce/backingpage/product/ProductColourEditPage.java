package com.aplos.ecommerce.backingpage.product;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.utils.CommonUtil;
import com.aplos.ecommerce.beans.playcom.PlayColour;
import com.aplos.ecommerce.beans.product.ProductColour;
import com.aplos.ecommerce.enums.AmazonColour;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=ProductColour.class)
public class ProductColourEditPage extends EditPage {

	private static final long serialVersionUID = 6495701171756827644L;
	
	public SelectItem[] getPlayColourSelectItems() {
		return AplosBean.getSelectItemBeansWithNotSelected( PlayColour.class );
	}
	
	public List<SelectItem> getAmazonColourSelectItems() {
		return CommonUtil.getEnumSelectItemsWithNotSelected( AmazonColour.class );
	}

}
