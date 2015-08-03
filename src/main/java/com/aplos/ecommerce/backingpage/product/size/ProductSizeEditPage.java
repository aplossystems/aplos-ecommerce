package com.aplos.ecommerce.backingpage.product.size;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.beans.AplosBean;
import com.aplos.ecommerce.beans.amazon.AmazonSize;
import com.aplos.ecommerce.beans.playcom.PlaySize;
import com.aplos.ecommerce.beans.product.ProductSize;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=ProductSize.class)
public class ProductSizeEditPage extends EditPage {
	private static final long serialVersionUID = 8511310102398115038L;

	public SelectItem[] getPositionIdxSelectItems() {
		ProductSize productSize = resolveAssociatedBean();
		SelectItem[] selectItems = new SelectItem[0];
		if (productSize != null) {
			BeanDao aqlBeanDao = new BeanDao( ProductSize.class );
			aqlBeanDao.addWhereCriteria( "s.productSizeType=" + productSize.getProductSizeType().getId() );
			aqlBeanDao.setOrderBy( "positionIdx" );
			@SuppressWarnings("unchecked")
			List<ProductSize> productSizeList = aqlBeanDao.getAll();
			selectItems = new SelectItem[productSizeList.size()+1];
			int currentPosition = productSizeList.size() - 1;
			if (productSize.getPositionIdx() != null) {
				currentPosition = productSize.getPositionIdx();
			}
			int lastPosition = 0;
			for (int i=0; i < productSizeList.size(); i++) {
				if ( productSizeList.get(i).getPositionIdx().equals(currentPosition)) {
					selectItems[i] = new SelectItem(productSizeList.get(i).getPositionIdx(),"Current Position");
				} else {
					selectItems[i] = new SelectItem(productSizeList.get(i).getPositionIdx(),"Before " + productSizeList.get(i).getName() + ((i==0)?" (beginning of list)":""));
				}
				lastPosition = productSizeList.get(i).getPositionIdx();
			}
			selectItems[productSizeList.size()] = new SelectItem(lastPosition+1,"End of list");
		}
		return selectItems;
	}
	
	public SelectItem[] getPlaySizeSelectItems() {
		ProductSize productSize = resolveAssociatedBean();
		BeanDao playSizeDao = new BeanDao( PlaySize.class );
		playSizeDao.addWhereCriteria( "playSizeType.id = " + productSize.getProductSizeType().getPlaySizeType().getId() );
		return AplosBean.getSelectItemBeansWithNotSelected( playSizeDao.getAll() );
	}
	
	public SelectItem[] getAmazonSizeSelectItems() {
		ProductSize productSize = resolveAssociatedBean();
		if( productSize.getProductSizeType().getAmazonProductType() != null ) {
			BeanDao amazonSizeDao = new BeanDao( AmazonSize.class );
			amazonSizeDao.addWhereCriteria( "amazonProductType = " + productSize.getProductSizeType().getAmazonProductType().ordinal() );
			return AplosBean.getSelectItemBeansWithNotSelected( amazonSizeDao.getAll() );
		} else {
			SelectItem selectItems[] = new SelectItem[ 1 ];
			selectItems[ 0 ] = new SelectItem( null, "Please select the amazon product type for this size type" );
			return selectItems;
		}
	}

}
