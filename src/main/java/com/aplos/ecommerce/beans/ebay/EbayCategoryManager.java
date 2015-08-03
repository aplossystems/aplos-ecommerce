package com.aplos.ecommerce.beans.ebay;

import java.util.Arrays;
import java.util.List;

import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.utils.ApplicationUtil;
import com.ebay.sdk.ApiContext;
import com.ebay.sdk.call.GetCategoriesCall;
import com.ebay.soap.eBLBaseComponents.CategoryType;
import com.ebay.soap.eBLBaseComponents.DetailLevelCodeType;

@Entity
@SessionScoped
@PluralDisplayName(name="Ebay category managers")
public class EbayCategoryManager extends AplosBean {
	private static final long serialVersionUID = -7787994263972092924L;
	private String categoryHierarchyVersion = "-1";
	@Transient
	private String ebayHierarchyVersion = null;
	@Transient
	private EbayPersistentCategory[] persistentCategories;
	@Transient
	private ApiContext ebayApiContext;
	@Transient
	private final long[] selectItemCategories = { //these are the only categories to show in the Ebay Category dropdown
			63861,  //"Clothing, Shoes & Accessories > Women's Clothing > Dresses"
			3003,   //"Clothing, Shoes & Accessories > Women's Accessories > Belts"
			45246,  //"Clothing, Shoes & Accessories > Women's Accessories > Sunglasses"
			1063,   //"Clothing, Shoes & Accessories > Women's Accessories > Other"
			11554,  //"Clothing, Shoes & Accessories > Women's Clothing > Jeans"
			314,    //"Clothing, Shoes & Accessories > Women's Clothing > Other"
			63868,  //"Clothing, Shoes & Accessories > Women's Clothing > Shirts, Tops"
			11555,  //"Clothing, Shoes & Accessories > Women's Clothing > Shorts"
			63864,  //"Clothing, Shoes & Accessories > Women's Clothing > Skirts"
			63866,  //"Clothing, Shoes & Accessories > Women's Clothing > Sweaters"
			155226, //"Clothing, Shoes & Accessories > Women's Clothing > Sweatshirts, Hoodies"
			63869,  //"Clothing, Shoes & Accessories > Women's Clothing > T-Shirts & Tank Tops"
			15775,  //"Clothing, Shoes & Accessories > Women's Clothing > Vests"
			11848,  //"Health & Beauty > Fragrances > Women"
			168199, //"Health & Beauty > Massage > Candles"
			63889,  //"Clothing, Shoes & Accessories > Women's Shoes"
			63852,  //"Clothing, Shoes & Accessories > Women's Handbags & Bags > Handbags & Purses"
			169303  //"Clothing, Shoes & Accessories > Women's Handbags & Bags > Other"
	};

	//Leaf Categories are those you can list in, ie any but the top level
	//This call will return leaves only as ViewAllNodes is omitted
	public EbayPersistentCategory[] getCurrentCategories() {

		//returns a two-element array of type boolean,String
		//values are isCategoryHierarchyVersionCurrent, version

		if (isCategoryHierarchyVersionCurrent()) {
			return getPersistentCategoriesFromDatabase();

		} else {

			return updateAndReturnCategories(ebayHierarchyVersion);

		}
	}

	/* This method checks the stored CHV against ebays most current version
	 * If the version is out of date we can then update the listings
	 */
	public boolean isCategoryHierarchyVersionCurrent() {
		//Only check Ebay once then cache the new value for this visit as the call is time-consuming
		if (ebayHierarchyVersion == null) {
			ebayHierarchyVersion = getLatestCategoryHierarchyVersionNumber();
		}
		if (ebayHierarchyVersion == null)
		 {
			return true; //if we just queried this the maxmimum number of times we wont be able to do it again so assume thats why we are gettign null back
		}
		return ebayHierarchyVersion.equals(categoryHierarchyVersion);
	}

	/* gets the newest category hierarchy, makes it persistent
	 * then returns it for immediate use
	 */
	public EbayPersistentCategory[] updateAndReturnCategories(String ebayHierarchyVersion) {

		GetCategoriesCall call = new GetCategoriesCall( getEbayApiContext() );
		DetailLevelCodeType[] detailLevel = {DetailLevelCodeType.RETURN_ALL};
		call.setDetailLevel(detailLevel);
		call.setViewAllNodes(true); //this makes sure we fetch top level nodes
		try {
			CategoryType[] returnedCategories = call.getCategories();
			setPersistentCategories(returnedCategories);
			/* Calling this method means we now have a new PersistentCategory set,
			 * this in turn means the data in the  db table refers to an out of date
			 * hierarchy version, we need to empty the table here, ahead of saving the new data
			 */
			ApplicationUtil.executeSql( "DELETE FROM " + EbayPersistentCategory.class.getSimpleName() );
			this.saveDetails();
			//update the stored category version identifier
			setCategoryHierarchyVersion(ebayHierarchyVersion);
			return getPersistentCategories();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public void saveBean( SystemUser systemUser ) {

		for (int i=0; i < persistentCategories.length; i++) {
			persistentCategories[i].saveDetails();
		}
		super.saveBean(systemUser);
	}

	/* "call GetCategories with no detail level specified. This causes GetCategories to
	 * return only the category hierarchy version." */
	public String getLatestCategoryHierarchyVersionNumber() {
		GetCategoriesCall call = new GetCategoriesCall( getEbayApiContext() );
		DetailLevelCodeType[] detailLevel = {};
		call.setDetailLevel(detailLevel);
		try {

			call.getCategories();
			return call.getReturnedCategoryVersion();

		} catch (Exception e) {

			e.printStackTrace();
			return null;
		}
	}

	public void setCategoryHierarchyVersion(String categoryHierarchyVersion) {
		this.categoryHierarchyVersion = categoryHierarchyVersion;
	}

	public String getCategoryHierarchyVersion() {
		return categoryHierarchyVersion;
	}

	public void setEbayApiContext(ApiContext ebayApiContext) {
		this.ebayApiContext = ebayApiContext;
	}

	public ApiContext getEbayApiContext() {
		return ebayApiContext;
	}

	/* We need to persist the Ebay Categories, however we don't need
	 * actual CategoryType objects or all the data they hold so this
	 * method is used to convert them into out own object type */
	public void setPersistentCategories(CategoryType[] categoryTypes) {
		//we need top level nodes to create path as name, however we dont
		//want to include them in the dropdowns as we cant list in these categories
		//so we ignore them here
 		int categoryCount = 0;
		for (int i=0; i < categoryTypes.length; i++) {
			if (categoryTypes[i].isLeafCategory() != null && categoryTypes[i].isLeafCategory() != false) {
				categoryCount++;
			}
		}
		EbayPersistentCategory[] persistentCategories = new EbayPersistentCategory[categoryCount];
		int tracker = -1;
		for (int i=0; i < categoryTypes.length; i++) {
			if (categoryTypes[i].isLeafCategory() == null || categoryTypes[i].isLeafCategory() == false)
			 {
				continue; //ignore top level (we can't list there)
			}
			tracker++;
			persistentCategories[tracker] = new EbayPersistentCategory();
			persistentCategories[tracker].setCategoryId(Integer.parseInt(categoryTypes[i].getCategoryID()));
			persistentCategories[tracker].setCategoryName(makeParentBreadcrumbs(categoryTypes[i],categoryTypes[i].getCategoryName(),categoryTypes));
		}

		setPersistentCategories(persistentCategories);
	}

	public String makeParentBreadcrumbs(CategoryType currentElement, String trail, CategoryType[] categoryTypes) {
		try{
//			String[] ancestors = currentElement.getCategoryParentID();
//			for (int i=ancestors.length-1; i > -1; i--) {
//				String parentId = ancestors[i];
//				for (int j=0; j < categoryTypes.length; j++) {
//					if (categoryTypes[j].getCategoryID().equals(parentId)) {
//						//return makeParentBreadcrumbs(categoryTypes[j], categoryTypes[j].getCategoryName() + " > " + trail, categoryTypes);
//						trail = categoryTypes[j].getCategoryName() + " > " + trail;
//					}
//				}
//			}
			String parentId = currentElement.getCategoryParentID(0);
			if (!parentId.equals(currentElement.getCategoryID())) {
				for (int j=0; j < categoryTypes.length; j++) {
					if (categoryTypes[j].getCategoryID().equals(parentId)) {
						return makeParentBreadcrumbs(categoryTypes[j], categoryTypes[j].getCategoryName() + " > " + trail, categoryTypes);
					}
				}
			}
		} catch (Exception e) {}
		return trail;
	}

	public void setPersistentCategories(EbayPersistentCategory[] persistentCategories) {
		this.persistentCategories = persistentCategories;
	}

	public EbayPersistentCategory[] getPersistentCategories() {
		return persistentCategories;
	}

	public EbayPersistentCategory[] getPersistentCategoriesFromDatabase() {
		if (getPersistentCategories() == null) {
			@SuppressWarnings("unchecked")
			List<EbayPersistentCategory> dbCategoryList = new BeanDao( EbayPersistentCategory.class ).getAll();
			EbayPersistentCategory[] dbCategories = new EbayPersistentCategory[dbCategoryList.size()];

			for (int i=0; i < dbCategoryList.size(); i++) {
				dbCategories[i] = dbCategoryList.get(i);
			}
			setPersistentCategories(dbCategories);
		}
		return getPersistentCategories();
	}

// This version generates a set based on title of item, unreliable for stanwells
//	public SelectItem[] getCategorySelectItems() {
//		EbayPersistentCategory[] categories = this.getCurrentCategories();
//		//get each word from the title
//		//Womans Clothing - Sweet-Mini Black Maids Dress
//		ItemType item = ((EbayManager)JSFUtil.getSession().getAttribute(AplosBean.getBinding(EbayManager.class))).getWorkingProduct();
//		String[] titleKeywords = item.getTitle().substring(0,item.getTitle().indexOf("(")).replace("-","").replace("  "," ").split(" ");
//		//filter categories by the title keywords
//		List <EbayPersistentCategory> filtered_categories = new ArrayList<EbayPersistentCategory>();
//		for (int i=0; i < categories.length; i++) {
//			for (int j=0; j < titleKeywords.length; j++) {
//				if (categories[i].getCategoryName().contains(titleKeywords[j])) {
//					boolean isUnique = true;
//					for (int k=0; k < filtered_categories.size(); k++) {
//						//if we have another category listed with exactly the same name then dont include this one
//						if (filtered_categories.get(k).getCategoryName().equals(categories[i].getCategoryName())) {
//							isUnique=false;
//							break;
//						}
//					}
//					if (isUnique) filtered_categories.add(categories[i]);
//				}
//			}
//		}
//		//if we end up with nothing then get some more based on description
//		if (filtered_categories.size() < 1) {
//			for (int i=0; i < categories.length; i++) {
//				filtered_categories.add(categories[i]);
//			}
//		}
//		SelectItem[] selectItems = new SelectItem[filtered_categories.size()];
//		for(int i = 0; i < filtered_categories.size(); i++ ) {
//			selectItems[i] = new SelectItem( filtered_categories.get(i).getCategoryId(), filtered_categories.get(i).getCategoryName() );
//		}
//		return selectItems;
//	}

	public SelectItem[] getCategorySelectItems() {
		BeanDao aqlBeanDao = new BeanDao( EbayPersistentCategory.class );
		aqlBeanDao.setWhereCriteria( "bean.categoryId IN (" + Arrays.toString(selectItemCategories).substring(1, Arrays.toString(selectItemCategories).length()-1) + ")" );
		@SuppressWarnings("unchecked")
		List<EbayPersistentCategory> EPCList = aqlBeanDao.getAll();
		SelectItem[] selectItems = new SelectItem[EPCList.size()];
		for(int i = 0; i < EPCList.size(); i++ ) {
			selectItems[i] = new SelectItem( EPCList.get(i).getCategoryId(), EPCList.get(i).getCategoryName() );
		}
		return selectItems;
	}
}















