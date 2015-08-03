package com.aplos.ecommerce.beans.product;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Cache;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.FetchType;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.ecommerce.beans.amazon.AmazonSize;
import com.aplos.ecommerce.beans.playcom.PlaySize;
import com.aplos.ecommerce.interfaces.SizeChartAxisLabel;

@Entity
@ManagedBean
@SessionScoped
@Cache
@DynamicMetaValueKey(oldKey="PRODUCT_SIZE")
public class ProductSize extends AplosBean implements SizeChartAxisLabel {

	private static final long serialVersionUID = -2744691852240972840L;
	private String name;
	private Integer positionIdx;
	@Transient
	private Integer oldPositionIdx;
	@ManyToOne(fetch=FetchType.LAZY)
	private ProductSizeType productSizeType;

	@ManyToOne(fetch=FetchType.LAZY)
	private PlaySize playSize;

	@ManyToOne(fetch=FetchType.LAZY)
	private AmazonSize amazonSize;

	public ProductSize() {

	}

	public String getName() {
		return name;
	}

	public void setName(String newName) {
		this.name = newName;
	}
	
	@Override
	public String getSizeChartAxisLabelName() {
		return getName();
	}
	
	@Override
	public int getSizeChartAxisLabelPositionIdx() {
		return getPositionIdx();
	}

	public ProductSizeType getProductSizeType() {
		return productSizeType;
	}

	public void setProductSizeType(ProductSizeType newST) {
		this.productSizeType = newST;
	}

	@Override
	public String getDisplayName() {
		return name;
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
			BeanDao productSizeDao = new BeanDao( ProductSize.class );
			productSizeDao.addWhereCriteria( "bean.productSizeType.id=" + productSizeType.getId() + " AND positionIdx=" + positionIdx );
			ProductSize dbPs = productSizeDao.getFirstBeanResult();
			if (dbPs != null && !dbPs.equals(this)) {
				//make sure we keep all numbers consecutive - ie account for position changing in either direction
				if (oldPositionIdx == null){

					// increase each element from positionIdx
					ApplicationUtil.executeSql("UPDATE productsize SET positionIdx=(positionIdx+1) WHERE positionIdx >= " + positionIdx + " AND productSizeType_id=" + productSizeType.getId());

				} else if (oldPositionIdx > positionIdx) {

					// increase each element from positionIdx to oldPositionIdx-1
					// this closes the gap so anything above oldPositionIdx keeps its position
					ApplicationUtil.executeSql("UPDATE productsize SET positionIdx=(positionIdx+1) WHERE (positionIdx >= " + positionIdx + " AND positionIdx < " + oldPositionIdx + ") AND productSizeType_id=" + productSizeType.getId());

				} else {

					// decrease each element from oldPositionIdx+1 to positionIdx
					ApplicationUtil.executeSql("UPDATE productsize SET positionIdx=(positionIdx-1) WHERE (positionIdx > " + oldPositionIdx + " AND positionIdx <= " + positionIdx + ") AND productSizeType_id=" + productSizeType.getId());
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

	public static List<? extends ProductSize> sortByPosition( List<? extends ProductSize> lookupBeanList ) {
		return sortByPosition(lookupBeanList, false);
	}

	public static List<? extends ProductSize> sortByPosition( List<? extends ProductSize> lookupBeanList, final Boolean reverseOrder ) {
		Collections.sort( lookupBeanList, new Comparator<ProductSize>() {
			@Override
			public int compare(ProductSize size1, ProductSize size2) {
				if ( (size1 == null || size1.getPositionIdx() == null )
						&& (size2 == null || size2.getPositionIdx() == null)) {
					return 0;
				}
				if (size1 == null || size1.getPositionIdx() == null) {
					return ((reverseOrder)?-1:1);
				}
				if (size2 == null || size2.getPositionIdx() == null) {
					return ((reverseOrder)?1:-1);
				}
				if (reverseOrder) {
					return size1.getPositionIdx().compareTo( size2.getPositionIdx() );
				} else {
					return size2.getPositionIdx().compareTo( size1.getPositionIdx() );
				}
			}
		});
		return lookupBeanList;
	}

	public PlaySize getPlaySize() {
		return playSize;
	}

	public void setPlaySize(PlaySize playSize) {
		this.playSize = playSize;
	}

	public AmazonSize getAmazonSize() {
		return amazonSize;
	}

	public void setAmazonSize(AmazonSize amazonSize) {
		this.amazonSize = amazonSize;
	}

}




