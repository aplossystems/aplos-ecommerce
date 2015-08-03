package com.aplos.ecommerce.backingpage.shipping;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import org.primefaces.event.SelectEvent;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.beans.Country;
import com.aplos.common.beans.PostalZone;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;

@ManagedBean
@SessionScoped
@AssociatedBean(beanClass=PostalZone.class)
public class PostalZoneEditPage extends EditPage {

	private static final long serialVersionUID = 8518314200079044519L;
	
	private Country selectedCountry = null;

	public List<SelectItem> getAvailableCountrySelectItems() {
		PostalZone postalZone = resolveAssociatedBean();
		List<SelectItem> items = new ArrayList<SelectItem>();
		if (postalZone != null && postalZone.getCountries() != null && postalZone.getCountries().size() > 0) {
			items.add(new SelectItem(null, "Please Select"));
			for (Country country : postalZone.getCountries()) {
		 		items.add(new SelectItem(country, country.getDisplayName()));
		 	}
		}
		if (items.size() < 1) {
			items.add(new SelectItem(null, "Please add Countries to the list"));
		}
		return items;
	}

	public Country getSelectedCountry() {
		return selectedCountry;
	}

	public void setSelectedCountry(Country selectedCountry) {
		this.selectedCountry = selectedCountry;
	}
	
	@SuppressWarnings("unchecked")
	public List<Country> suggestCountries(String searchStr) {
		BeanDao countryDao = new BeanDao( Country.class );
		countryDao.addWhereCriteria("bean.name like :similarSearchText");
		PostalZone postalZone = resolveAssociatedBean();
		if (postalZone != null && postalZone.getCountries() != null && postalZone.getCountries().size() > 0) {
			List<Long> addedIds = new ArrayList<Long>();
			for (Country country : postalZone.getCountries()) {
				addedIds.add(country.getId());
			}
			countryDao.addWhereCriteria("bean.id NOT IN(" + CommonUtil.join(addedIds, ",") + ")");
		}
		countryDao.setMaxResults( 20 );
		countryDao.setIsReturningActiveBeans( true );
		countryDao.setNamedParameter( "similarSearchText", "%" + searchStr + "%" );
		return countryDao.getAll();
	}
	
	public void addCountry( SelectEvent event ) {
		Country country = (Country) event.getObject();
		PostalZone postalZone = resolveAssociatedBean();
		postalZone.getCountries().add(country);
		setSelectedCountry( null );
	}
	
	public void removeCountry() {
		Country country = (Country) JSFUtil.getRequest().getAttribute("country");
		if (country != null) {
			PostalZone postalZone = resolveAssociatedBean();
			if (postalZone != null) {
				postalZone.getCountries().remove(country);
			}
		}
	}
	
}
