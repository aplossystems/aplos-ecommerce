package com.aplos.ecommerce.beans.developercmsmodules;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.aql.BeanDao;
import com.aplos.ecommerce.beans.RealizedProduct;

@Entity
@DynamicMetaValueKey(oldKey="LATEST_LIST_MODULE")
public class LatestProductListModule extends ProductListModule {

	private static final long serialVersionUID = 1220818489655186436L;
	private int daysToDisplay=1;

	/* This constructor is required by getDeveloperModuleCPHUrlList()
	 * in the Page class
	 */
	public LatestProductListModule() {
		super();
	}
	
	@Override
	public String getFrontEndBodyName() {
		return "productListBody";
	}

	@Override
	public String getName() {
		return "Latest products";
	}

	@Override
	public String getUnfilteredProductWhereClaus() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -daysToDisplay);
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return " FROM " + RealizedProduct.class.getSimpleName() + " rp WHERE rp.active = true AND rp.productInfo.active=true AND rp.dateCreated > '" + sdf.format(cal.getTimeInMillis()) + "'";
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RealizedProduct> defaultSort(List<RealizedProduct> unsortedList) {
		Collections.sort(unsortedList,new RealizedProductDateCreatedComparator());
		return unsortedList;
	}

	@Override
	public void addExtendedWhereClause( BeanDao realizedProductDao ) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -daysToDisplay);
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    realizedProductDao.addWhereCriteria( "bean.dateCreated > '" + sdf.format(cal.getTimeInMillis()) + "'" );
	}


	public void setDaysToDisplay(int daysToDisplay) {
		this.daysToDisplay = daysToDisplay;
	}


	public int getDaysToDisplay() {
		return daysToDisplay;
	}

	private static class RealizedProductDateCreatedComparator implements Comparator {

		@Override
		public int compare(Object bean1, Object bean2) {
			return ((RealizedProduct)bean2).getProductInfo().getDateCreated().compareTo(((RealizedProduct)bean1).getProductInfo().getDateCreated());
		}

	}

}
