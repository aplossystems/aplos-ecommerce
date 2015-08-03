package com.aplos.ecommerce.beans.product;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.PluralDisplayName;
import com.aplos.common.annotations.persistence.Cache;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.ecommerce.interfaces.SizeChartAxisLabel;

@Entity
@ManagedBean
@SessionScoped
@PluralDisplayName(name="product size categories")
@Cache
@DynamicMetaValueKey(oldKey="PRODUCT_SIZE_CATEGORY")
public class ProductSizeCategory extends AplosBean implements SizeChartAxisLabel {

	private static final long serialVersionUID = 5446629185594190944L;

	private String name;
	private String suffix;
	private Integer positionIdx;
	@Transient
	private Integer oldPositionIdx;
	@ManyToOne
	private ProductSizeType productSizeType;

	public ProductSizeCategory() {	}

	public String getName() {
		return name;
	}

	public void setName(String newName) {
		this.name = newName;
	}

	@Override
	public String getDisplayName() {
		return name;
	}
	
	@Override
	public String getSizeChartAxisLabelName() {
		return getName();
	}
	
	@Override
	public int getSizeChartAxisLabelPositionIdx() {
		return getPositionIdx();
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setPositionIdx(Integer positionIdx) {
		this.positionIdx = positionIdx;
	}

	public Integer getPositionIdx() {
		return positionIdx;
	}

	@Override
	public void saveBean( SystemUser currentUser ) {
		//if we changed position
		if (oldPositionIdx!=positionIdx) {
			//if there's an item in this position that isn't this item
			BeanDao productSizeCategoryDao = new BeanDao( ProductSizeCategory.class );
			productSizeCategoryDao.addWhereCriteria( "bean.productSizeType.id=" + productSizeType.getId() + " AND positionIdx=" + positionIdx );
			ProductSizeCategory existingCategory = productSizeCategoryDao.getFirstBeanResult();
			if (existingCategory != null && !existingCategory.equals(this)) {
				//make sure we keep all numbers consecutive - ie account for position changing in either direction
				if (oldPositionIdx == null){

					// increase each element from positionIdx
					ApplicationUtil.executeSql("UPDATE productsizecategory SET positionIdx=(positionIdx+1) WHERE positionIdx >= " + positionIdx + " AND productSizeType_id=" + productSizeType.getId());

				} else if (oldPositionIdx > positionIdx) {

					// increase each element from positionIdx to oldPositionIdx-1
					// this closes the gap so anything above oldPositionIdx keeps its position
					ApplicationUtil.executeSql("UPDATE productsizecategory SET positionIdx=(positionIdx+1) WHERE (positionIdx >= " + positionIdx + " AND positionIdx < " + oldPositionIdx + ") AND productSizeType_id=" + productSizeType.getId());

				} else {

					// decrease each element from oldPositionIdx+1 to positionIdx
					ApplicationUtil.executeSql("UPDATE productsizecategory SET positionIdx=(positionIdx-1) WHERE (positionIdx > " + oldPositionIdx + " AND positionIdx <= " + positionIdx + ") AND productSizeType_id=" + productSizeType.getId());
				}

				setOldPositionIdx(getPositionIdx());
			}
		}
		super.saveBean(currentUser);
	}

	public void setOldPositionIdx(Integer oldPositionIdx) {
		this.oldPositionIdx = oldPositionIdx;
	}

	public Integer getOldPositionIdx() {
		return oldPositionIdx;
	}

	public static List<? extends ProductSizeCategory> sortByPosition( List<? extends ProductSizeCategory> lookupBeanList ) {
		return sortByPosition(lookupBeanList, false);
	}

	public static List<? extends ProductSizeCategory> sortByPosition( List<? extends ProductSizeCategory> lookupBeanList, final Boolean reverseOrder ) {
		Collections.sort( lookupBeanList, new Comparator<ProductSizeCategory>() {
			@Override
			public int compare(ProductSizeCategory category1, ProductSizeCategory category2) {
				if ( (category1 == null || category1.getPositionIdx() == null )
						&& (category2 == null || category2.getPositionIdx() == null)) {
					return 0;
				}
				if (category1 == null || category1.getPositionIdx() == null) {
					return ((reverseOrder)?-1:1);
				}
				if (category2 == null || category2.getPositionIdx() == null) {
					return ((reverseOrder)?1:-1);
				}
				if (reverseOrder) {
					return category1.getPositionIdx().compareTo( category2.getPositionIdx() );
				} else {
					return category2.getPositionIdx().compareTo( category1.getPositionIdx() );
				}
			}
		});
		return lookupBeanList;
	}

	public void setProductSizeType(ProductSizeType productSizeType) {
		this.productSizeType = productSizeType;
	}

	public ProductSizeType getProductSizeType() {
		return productSizeType;
	}



}




