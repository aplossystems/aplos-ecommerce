package com.aplos.ecommerce.backingpage;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import com.aplos.cms.beans.HostedVideo;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.beans.AplosAbstractBean;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.ecommerce.beans.NewsEntry;
import com.aplos.ecommerce.beans.RealizedProduct;

@ManagedBean
@SessionScoped
@AssociatedBean(beanClass=NewsEntry.class)
public class NewsEntryEditPage extends EditPage {

	private static final long serialVersionUID = -6482003211415252835L;

	public SelectItem[] getRealizedProductSelectItemBeans() {
		BeanDao dao = new BeanDao(RealizedProduct.class);
		dao.setSelectCriteria("bean.id, bean.productInfo.product.name, bean.itemCode, bean.productInfo.product.itemCode");
		List<RealizedProduct> realizedProductList = dao.setOrderBy( "bean.itemCode, bean.productInfo.product.itemCode" ).getAll();
		SelectItem[] selectItems;

		int count = 0;
		selectItems = new SelectItem[ realizedProductList.size() + 1 ];
		selectItems[ count++ ] = new SelectItem( null, CommonConfiguration.getCommonConfiguration().getDefaultNotSelectedText() );

		for ( int i = 0, n = realizedProductList.size(); i < n; i++ ) {
			selectItems[ count++ ] = new SelectItem( realizedProductList.get( i ), realizedProductList.get( i ).determineItemCode() + " " + realizedProductList.get( i ).getDisplayName() );
		}

		return selectItems;
	}

	public SelectItem[] getVideoSelectItemBeans() {
		BeanDao dao = new BeanDao(HostedVideo.class);
		return AplosAbstractBean.getSelectItemBeansWithNotSelected(dao.setIsReturningActiveBeans(true).getAll(), "No Video");
	}
}
