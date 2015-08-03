package com.aplos.ecommerce.beans.developercmsmodules;

import java.util.ArrayList;
import java.util.List;

import com.aplos.cms.beans.developercmsmodules.ConfigurableDeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Cascade;
import com.aplos.common.annotations.persistence.CascadeType;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.OneToMany;
import com.aplos.common.interfaces.PositionedBean;
import com.aplos.common.utils.PositionedBeanHelper;
import com.aplos.ecommerce.beans.FavouriteProductList;

@Entity
@DynamicMetaValueKey(oldKey="FAVOURITE_PRODUCT_TYPES_ATOM")
public class FavouriteProductListsCmsAtom extends ConfigurableDeveloperCmsAtom {
	private static final long serialVersionUID = 2944843459547551823L;

	@OneToMany
	@Cascade({CascadeType.ALL})
	private List<FavouriteProductList> favouriteProductLists = new ArrayList<FavouriteProductList>();

	@Override
	public String getName() {
		return "Favourite product list boxes";
	}

	@Override
	public boolean initBackend() {
		super.initBackend();
		return true;
	}

	@Override
	public boolean initFrontend(boolean isRequestPageLoad) {
		super.initFrontend(isRequestPageLoad);
//		HibernateUtil.initialise( this, true );
		return true;
	}

	@SuppressWarnings("unchecked")
	public List<FavouriteProductList> getSortedFavouriteProductLists() {
		return (List<FavouriteProductList>) PositionedBeanHelper.getSortedPositionedBeanList( new ArrayList<PositionedBean>( getFavouriteProductLists() ) );
	}

	@Override
	public DeveloperCmsAtom getCopy() {
		FavouriteProductListsCmsAtom favouriteProductListsAtom = new FavouriteProductListsCmsAtom();
		favouriteProductListsAtom.setFavouriteProductLists(new ArrayList<FavouriteProductList>( getFavouriteProductLists() ));
		return favouriteProductListsAtom;
	}

	public List<FavouriteProductList> getFavouriteProductLists() {
		return favouriteProductLists;
	}

	public void setFavouriteProductLists(List<FavouriteProductList> favouriteProductLists) {
		this.favouriteProductLists = favouriteProductLists;
	}


}
