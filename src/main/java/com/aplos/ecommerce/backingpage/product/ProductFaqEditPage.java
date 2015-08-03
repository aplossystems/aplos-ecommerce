package com.aplos.ecommerce.backingpage.product;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.aplos.cms.beans.ContentBox.ContentBoxLayout;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.product.ProductFaq;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=ProductFaq.class)
public class ProductFaqEditPage extends EditPage {

	private static final long serialVersionUID = 6368779390285226604L;

	public ProductFaq getProductFaqFromSession() {
		return JSFUtil.getBeanFromScope( ProductFaq.class );
	}

	public SelectItem[] getFaqCategorySelectItems() {
		String[] categoryNames = { "Batteries", "Cameras", "Conformity",
				"LCD monitors", "LCD portable", "LCD racks", "Map", "OZL1701",
				"OZL1702", "OZL17xx", "Portable LCD kits", "Radio link", "Returns",
				"Test equipment", "Video" };
		SelectItem selectItems[] = new SelectItem[ categoryNames.length ];
		int count = 0;
		for( String tempCategoryName : categoryNames ) {
			selectItems[ count++ ] = new SelectItem( tempCategoryName );
		}

		return selectItems;

	}

	public List<SelectItem> getContentBoxLayoutSelectItems() {
		return CommonUtil.getEnumSelectItems(ContentBoxLayout.class, null);
	}

}
