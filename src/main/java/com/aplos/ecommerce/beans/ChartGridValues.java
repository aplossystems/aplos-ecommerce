package com.aplos.ecommerce.beans;

import java.util.ArrayList;
import java.util.List;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.OneToMany;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.SystemUser;
import com.aplos.ecommerce.beans.product.ProductSizeChart.MeasurementType;

@Entity
public class ChartGridValues extends AplosBean {
	private static final long serialVersionUID = -1348581494669105133L;
	private MeasurementType measurementType;
	
	@OneToMany
	private List<ChartAxisValues> rowValues;
	
	public ChartGridValues() {
	}
	
	public ChartGridValues( MeasurementType measurementType ) {
		setMeasurementType(measurementType);
	}
	
	public void createCells( int columnSize, int rowSize ) {
		setRowValues(new ArrayList<ChartAxisValues>());
		ChartAxisValues tempChartAxisValues;
		for( int i = 0, n = rowSize; i < n; i++ ) {
			tempChartAxisValues = new ChartAxisValues();
			tempChartAxisValues.createCells( columnSize );
			getRowValues().add( tempChartAxisValues );
			tempChartAxisValues.setPositionIdx( i );
		}	
	}
	
	public String getCellValue( int columnIdx, int rowIdx ) {
		return getRowValues().get( rowIdx ).getCellValues().get( columnIdx );
	}
	
	public void setCellValue( int columnIdx, int rowIdx, String value ) {
		getRowValues().get( rowIdx ).getCellValues().set( columnIdx, value );
	}
	
	@Override
	public void saveBean(SystemUser currentUser) {
		for( ChartAxisValues chartAxisValues : getRowValues() ) {
			chartAxisValues.saveDetails(currentUser);
		}
		super.saveBean(currentUser);
	}

	public List<ChartAxisValues> getRowValues() {
		return rowValues;
	}

	public void setRowValues(List<ChartAxisValues> rowValues) {
		this.rowValues = rowValues;
	}

	public MeasurementType getMeasurementType() {
		return measurementType;
	}

	public void setMeasurementType(MeasurementType measurementType) {
		this.measurementType = measurementType;
	}

}
