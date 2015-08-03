package com.aplos.ecommerce.beans.product;

import java.util.ArrayList;
import java.util.List;

import com.aplos.common.annotations.DynamicMetaValues;
import com.aplos.common.annotations.persistence.AnyMetaDef;
import com.aplos.common.annotations.persistence.Cache;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.FetchType;
import com.aplos.common.annotations.persistence.JoinColumn;
import com.aplos.common.annotations.persistence.JoinTable;
import com.aplos.common.annotations.persistence.ManyToAny;
import com.aplos.common.beans.AplosBean;
import com.aplos.ecommerce.interfaces.RealizedProductRetriever;

@Entity
public class ProductGroup extends AplosBean {
	private static final long serialVersionUID = 2442773463597506375L;

	@ManyToAny( metaColumn = @Column( name = "productRetriever_type" ), fetch=FetchType.EAGER )
    @AnyMetaDef( idType = "long", metaType = "string", metaValues = { /* Meta Values added in at run-time */ } )
    @JoinTable( inverseJoinColumns = @JoinColumn( name = "productRetriever_id" ) )
	@Cache
	@DynamicMetaValues
	private List<RealizedProductRetriever> productRetrieverList = new ArrayList<RealizedProductRetriever>();

	private String name;

	public void setProductRetrieverList(List<RealizedProductRetriever> productRetrieverList) {
		this.productRetrieverList = productRetrieverList;
	}

	public List<RealizedProductRetriever> getProductRetrieverList() {
		return productRetrieverList;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
