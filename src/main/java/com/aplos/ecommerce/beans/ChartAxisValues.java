package com.aplos.ecommerce.beans;

import java.util.ArrayList;
import java.util.List;

import com.aplos.common.annotations.persistence.CollectionOfElements;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.IndexColumn;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.interfaces.PositionedBean;

@Entity
public class ChartAxisValues extends AplosBean implements PositionedBean {
	private static final long serialVersionUID = -1348581494669105133L;
	@CollectionOfElements
	@IndexColumn(name="cellValueIdx")
	private List<String> cellValues;
	private Integer positionIdx;
	private int columnSize;
	
	public void createCells( int columnSize ) {
		this.columnSize = columnSize;
		setCellValues( new ArrayList<String>( columnSize ) );
		for( int i = 0; i < columnSize; i++ ) {
			getCellValues().add( null );
		}
	}
	
	public Integer getPositionIdx() {
		return positionIdx;
	}
	public void setPositionIdx(Integer positionIdx) {
		this.positionIdx = positionIdx;
	}
	public List<String> getCellValues() {
		return cellValues;
	}
	public void setCellValues(List<String> cellValues) {
		this.cellValues = cellValues;
	}

	public int getColumnSize() {
		return columnSize;
	}

	public void setColumnSize(int columnSize) {
		this.columnSize = columnSize;
	}

}

