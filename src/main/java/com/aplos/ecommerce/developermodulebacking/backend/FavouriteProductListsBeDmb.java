package com.aplos.ecommerce.developermodulebacking.backend;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.cms.enums.GenerationType;
import com.aplos.cms.interfaces.GeneratorMenuItem;
import com.aplos.cms.module.CmsModule;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosAbstractBean;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.interfaces.PositionedBean;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.PositionedBeanHelper;
import com.aplos.ecommerce.beans.FavouriteProductList;
import com.aplos.ecommerce.beans.developercmsmodules.FavouriteProductListsCmsAtom;

@ManagedBean
@ViewScoped
public class FavouriteProductListsBeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = -1347889272311642839L;
	private PositionedBeanHelper positionedBeanHelper;
	private Class<? extends GeneratorMenuItem> selectedGenerationMenuItemClass;

	@SuppressWarnings("unchecked")
	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		super.responsePageLoad(developerCmsAtom);
		if( positionedBeanHelper == null ) {
			setPositionedBeanHelper(new PositionedBeanHelper( (AplosBean) developerCmsAtom, (List<PositionedBean>) (List<? extends PositionedBean>) getFavouriteProductListsCmsAtom().getFavouriteProductLists(), FavouriteProductList.class ));
		}
		if( getSelectedGenerationMenuItemClass() == null ) {
			if( getFavouriteProductListsCmsAtom().getFavouriteProductLists().size() > 0 ) {
				setSelectedGenerationMenuItemClass( (Class<? extends GeneratorMenuItem>) ApplicationUtil.getClass( getFavouriteProductListsCmsAtom().getFavouriteProductLists().get( 0 ).getGeneratorMenuItem() ) );
			} else {
				setSelectedGenerationMenuItemClass( ((CmsModule)ApplicationUtil.getAplosContextListener().getAplosModuleByClass( CmsModule.class )).getAvailableGeneratorItemClassList().get( 0 ) );
			}
		}
		return true;
	}
	
	public FavouriteProductListsCmsAtom getFavouriteProductListsCmsAtom() {
		return (FavouriteProductListsCmsAtom) getDeveloperCmsAtom();
	}

	@Override
	public void applyBtnAction() {
		super.applyBtnAction();
		if (getPositionedBeanHelper() != null) {
			getPositionedBeanHelper().saveCurrentPositionedBean();
		}
	}

	public List<SelectItem> getGenerationTypeSelectItems() {
		return CommonUtil.getEnumSelectItems( GenerationType.class );
	}

	public SelectItem[] getGeneratorItemClassSelectItems() {
		CmsModule cmsModule = (CmsModule) ApplicationUtil.getAplosContextListener().getAplosModuleByClass( CmsModule.class );
		List<Class<? extends GeneratorMenuItem>> generatorItemClassList = cmsModule.getAvailableGeneratorItemClassList();
		SelectItem selectItems[] = new SelectItem[ generatorItemClassList.size() ];
		for( int i = 0, n = generatorItemClassList.size(); i < n; i++ ) {
			selectItems[ i ] = new SelectItem( generatorItemClassList.get( i ), FormatUtil.breakCamelCase( generatorItemClassList.get( i ).getSimpleName() ) );
		}
		return selectItems;
		
	}


	public SelectItem[] getGeneratorItemSelectItemBeans() {
		if( getSelectedGenerationMenuItemClass() != null ) {
			BeanDao dao = new BeanDao( (Class<? extends AplosAbstractBean>) getSelectedGenerationMenuItemClass() );
			return AplosAbstractBean.getSelectItemBeansWithNotSelected(dao.setIsReturningActiveBeans(true).getAll(), "Not Selected");
		} else {
			return new SelectItem[ 0 ];
		}
	}

	public void saveFavouriteCategoriesAndAtom() {
		getPositionedBeanHelper().saveCurrentPositionedBean();
		getFavouriteProductListsCmsAtom().saveDetails();
	}

	public void setPositionedBeanHelper(PositionedBeanHelper positionedBeanHelper) {
		this.positionedBeanHelper = positionedBeanHelper;
	}

	public PositionedBeanHelper getPositionedBeanHelper() {
		return positionedBeanHelper;
	}

	public Class<? extends GeneratorMenuItem> getSelectedGenerationMenuItemClass() {
		return selectedGenerationMenuItemClass;
	}

	public void setSelectedGenerationMenuItemClass(
			Class<? extends GeneratorMenuItem> selectedGenerationMenuItemClass) {
		this.selectedGenerationMenuItemClass = selectedGenerationMenuItemClass;
	}
}
