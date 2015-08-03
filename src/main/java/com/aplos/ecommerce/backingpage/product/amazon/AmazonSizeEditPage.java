package com.aplos.ecommerce.backingpage.product.amazon;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.interfaces.PositionedBean;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.common.utils.PositionedBeanHelper;
import com.aplos.ecommerce.beans.amazon.AmazonSize;
import com.aplos.ecommerce.enums.AmazonProductType;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=AmazonSize.class)
public class AmazonSizeEditPage extends EditPage {
	private static final long serialVersionUID = -768622038165821215L;
	private PositionedBeanHelper positionedBeanHelper;
	
	public List<SelectItem> getAmazonProductTypeSelectItems() {
		return CommonUtil.getEnumSelectItems(AmazonProductType.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean responsePageLoad() {
		boolean continueLoad = super.responsePageLoad();
		if( getPositionedBeanHelper() == null ) {
			amazonProductTypeUpdated();
		}
		return continueLoad;
	}
	
	public void amazonProductTypeUpdated() {
		AmazonSize amazonSize = JSFUtil.getBeanFromScope( AmazonSize.class );
		List<AmazonSize> amazonSizeList;
		if( amazonSize.getAmazonProductType() != null ) {
			BeanDao amazonSizeDao = new BeanDao( AmazonSize.class );
			amazonSizeDao.addWhereCriteria( "bean.amazonProductType = " + amazonSize.getAmazonProductType().ordinal() );
			amazonSizeList = amazonSizeDao.getAll();
		} else {
			amazonSizeList = new ArrayList<AmazonSize>();
		}
		setPositionedBeanHelper(new PositionedBeanHelper( null, (List<PositionedBean>) (List<? extends PositionedBean>) amazonSizeList, AmazonSize.class ));
		getPositionedBeanHelper().setSelectedPositionedBean( amazonSize );
		getPositionedBeanHelper().setCurrentPositionedBean( amazonSize );
	}
	
	@Override
	public boolean saveBean() {
		getPositionedBeanHelper().saveCurrentPositionedBean();
		return super.saveBean();
	}

	public PositionedBeanHelper getPositionedBeanHelper() {
		return positionedBeanHelper;
	}

	public void setPositionedBeanHelper(PositionedBeanHelper positionedBeanHelper) {
		this.positionedBeanHelper = positionedBeanHelper;
	}

}
