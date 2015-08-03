package com.aplos.ecommerce.backingpage.product.type;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.aplos.cms.beans.HostedVideo;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.beans.AplosAbstractBean;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.DiscountAllowance;
import com.aplos.ecommerce.beans.playcom.PlayMainGenre;
import com.aplos.ecommerce.beans.product.ProductSizeType;
import com.aplos.ecommerce.beans.product.ProductType;
import com.aplos.ecommerce.enums.AmazonProductType;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=ProductType.class)
public class ProductTypeEditPage extends EditPage {
	private static final long serialVersionUID = -621734397736800123L;
	private HostedVideo videoSelected=null;

	public SelectItem[] getProductSizeTypeSelectItemBeans() {
		ProductType parentProductType = JSFUtil.getBeanFromView( ProductType.PARENT_PRODUCT_TYPE );
		if (parentProductType == null) {
			return parentProductType.getSelectItemBeans(new BeanDao( ProductSizeType.class ).setIsReturningActiveBeans( true ).getAll());
		} else {
			return parentProductType.getSelectItemBeansWithNotSelected(new BeanDao( ProductSizeType.class ).setIsReturningActiveBeans( true ).getAll(), "Use parent size-type (" + parentProductType.getProductSizeType().getName()  + ")");
		}
	}
	
	public SelectItem[] getPlayMainGenreSelectItems() {
		return AplosBean.getSelectItemBeansWithNotSelected( PlayMainGenre.class );
	}
	
	public List<SelectItem> getAmazonProductTypeSelectItems() {
		return CommonUtil.getEnumSelectItemsWithNotSelected(AmazonProductType.class);
	}

	public SelectItem[] getParentTypeSelectItemBeans() {
		ProductType pt = new ProductType();
		return pt.getSelectItemBeansWithNotSelected(new BeanDao( ProductType.class ).addWhereCriteria("bean.parentProductType IS NULL").setIsReturningActiveBeans(true).getAll(), "None - Make this a top level type");
	}

	public SelectItem[] getVideoSelectItemBeans() {
		return AplosAbstractBean.getSelectItemBeans(new BeanDao( HostedVideo.class ).setIsReturningActiveBeans(true).getAll());
	}

	public void setVideoSelected(HostedVideo videoSelected) {
		this.videoSelected = videoSelected;
	}

	public HostedVideo getVideoSelected() {
		return videoSelected;
	}

	public String addVideoToList() {
		ProductType type = JSFUtil.getBeanFromScope(ProductType.class);
		if (type != null && videoSelected != null) {
			if (!type.getVideos().contains(videoSelected)) {
				type.getVideos().add(videoSelected);
			}
		}
		return null;
	}

	public String removeVideo() {
		ProductType type = JSFUtil.getBeanFromScope(ProductType.class);
		HostedVideo video = (HostedVideo) JSFUtil.getRequest().getAttribute("videoBean");
		if (type != null && video != null) {
			type.getVideos().remove(video);
		}
		return null;
	}

}
