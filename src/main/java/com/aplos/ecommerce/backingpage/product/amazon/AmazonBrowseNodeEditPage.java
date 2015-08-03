package com.aplos.ecommerce.backingpage.product.amazon;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.EditPage;
import com.aplos.ecommerce.beans.amazon.AmazonBrowseNode;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=AmazonBrowseNode.class)
public class AmazonBrowseNodeEditPage extends EditPage {
	private static final long serialVersionUID = 4568134337089191784L;

	
	public SelectItem[] getParentNodeSelectItems() {
		return AmazonBrowseNode.getSelectItemBeansWithNotSelected( AmazonBrowseNode.class );
	}
}
