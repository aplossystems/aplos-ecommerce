package com.aplos.ecommerce.beans;

import com.aplos.cms.interfaces.GeneratorMenuItem;
import com.aplos.common.annotations.DynamicMetaValues;
import com.aplos.common.annotations.persistence.Any;
import com.aplos.common.annotations.persistence.AnyMetaDef;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.JoinColumn;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.interfaces.PositionedBean;

@Entity
public class FavouriteProductList extends AplosBean implements PositionedBean {

	private static final long serialVersionUID = -7203029270270090216L;


	@Any( metaColumn = @Column( name = "generatorMenuItem_type" ) )
    @AnyMetaDef( idType = "long", metaType = "string", metaValues = {
    		/* Meta Values added in at run-time */ } )
    @JoinColumn(name="generatorMenuItem_id")
	@DynamicMetaValues
	private GeneratorMenuItem generatorMenuItem;
	@Column(columnDefinition="LONGTEXT")
	private String description;
	private Integer positionIdx;

	@Override
	public String getDisplayName() {
		if( getGeneratorMenuItem() == null ) {
			return "New Favourite Product List";
		} else {
			return getGeneratorMenuItem().getDisplayName();
		}
	}

	@Override
	public void setPositionIdx(Integer positionIdx) {
		this.positionIdx = positionIdx;
	}

	@Override
	public Integer getPositionIdx() {
		return positionIdx;
	}

	public GeneratorMenuItem getGeneratorMenuItem() {
		return generatorMenuItem;
	}

	public void setGeneratorMenuItem(GeneratorMenuItem generatorMenuItem) {
		this.generatorMenuItem = generatorMenuItem;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

}
