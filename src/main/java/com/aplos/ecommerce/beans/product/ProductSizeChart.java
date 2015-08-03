package com.aplos.ecommerce.beans.product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.SessionScoped;

import com.aplos.common.LabeledEnumInter;
import com.aplos.common.annotations.DynamicMetaValues;
import com.aplos.common.annotations.persistence.AnyMetaDef;
import com.aplos.common.annotations.persistence.Cache;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.EjbMapKey;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.EnumType;
import com.aplos.common.annotations.persistence.Enumerated;
import com.aplos.common.annotations.persistence.FetchType;
import com.aplos.common.annotations.persistence.JoinColumn;
import com.aplos.common.annotations.persistence.JoinTable;
import com.aplos.common.annotations.persistence.ManyToAny;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.OneToMany;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.SystemUser;
import com.aplos.ecommerce.beans.ChartGridValues;
import com.aplos.ecommerce.beans.CustomSizeChartAxisLabel;
import com.aplos.ecommerce.interfaces.SizeChartAxisLabel;

@Entity
@SessionScoped
@Cache
public class ProductSizeChart extends AplosBean {
	private static final long serialVersionUID = 5594864852082967672L;
	
	public enum SizeChartAxisType implements LabeledEnumInter {
		CUSTOM ("Custom"),
		SIZES ("Sizes"),
		SIZE_CATEGORIES ("Size categories" );
		
		private String label;
		
		private SizeChartAxisType( String label ) {
			this.label = label;
		}
		
		@Override
		public String getLabel() {
			return label;
		}
	}

	public enum MeasurementType implements LabeledEnumInter {
		CENTIMETRES ("Centimetres"),
		INCHES ("Inches");
		
		private String label;
		
		private MeasurementType( String label ) {
			this.label = label;
		}
		
		@Override
		public String getLabel() {
			return label;
		}
	}

	private String name;
	@ManyToAny( metaColumn = @Column( name = "rowLabels_type" ), fetch=FetchType.EAGER )
    @AnyMetaDef( idType="long", metaType= "string", metaValues = {
		/* Meta Values added in at run-time */ } )
	@JoinTable( inverseJoinColumns = @JoinColumn( name = "rowLabels_id" ) )
	@Cache
	@DynamicMetaValues
	private List<SizeChartAxisLabel> rowLabels = new ArrayList<SizeChartAxisLabel>();
	@ManyToAny( metaColumn = @Column( name = "columnLabels_type" ), fetch=FetchType.EAGER )
    @AnyMetaDef( idType="long", metaType= "string", metaValues = {
		/* Meta Values added in at run-time */ } )
	@JoinTable( inverseJoinColumns = @JoinColumn( name = "columnLabels_id" ) )
	@Cache
	@DynamicMetaValues
	private List<SizeChartAxisLabel> columnLabels = new ArrayList<SizeChartAxisLabel>();
	@ManyToOne
	private ProductSizeType productSizeType;
	@Enumerated(EnumType.STRING) 
	private SizeChartAxisType rowAxisType;
	@Enumerated(EnumType.STRING)
	private SizeChartAxisType columnAxisType;
	private boolean isUsingCentimetres = true;
	private boolean isUsingInches = true;
	@OneToMany
	@EjbMapKey(name="measurementType")
	private Map<MeasurementType,ChartGridValues> gridValueMap = new HashMap<MeasurementType, ChartGridValues>();

	public ProductSizeChart() {
		super();
	}
	
	public void addCustomAxisLabel( boolean isRow, CustomSizeChartAxisLabel customSizeChartAxisLabel, boolean setPositionIdx ) {
		if( setPositionIdx ) {
			Integer currentPositionIdx = 0;
			if( getAxisLabels( isRow ).size() != 0 ) {
				currentPositionIdx = getAxisLabels( isRow ).get( getAxisLabels( isRow ).size() - 1 ).getSizeChartAxisLabelPositionIdx();
			}
			customSizeChartAxisLabel.setPositionIdx( currentPositionIdx + 1 );
		}
		getAxisLabels( isRow ).add( customSizeChartAxisLabel );
	}
	
	public List<SizeChartAxisLabel> getAxisLabels( boolean isRow ) {
		if( isRow ) {
			return getRowLabels();
		} else {
			return getColumnLabels();
		}
	}
	
	public void addChartGridValues( MeasurementType measurementType, boolean clearOldValues ) {
		if( clearOldValues ) {
			gridValueMap.put( measurementType, null );
		}
		if( gridValueMap.get( measurementType ) == null ) {
			ChartGridValues chartGridValues = new ChartGridValues(measurementType);
			gridValueMap.put( measurementType, chartGridValues );
			chartGridValues.createCells( getColumnLabels().size(), getRowLabels().size() );
		}
	}
	
	public void updateGridValues( boolean clearOldValues ) {
		gridValueMap = new HashMap<MeasurementType, ChartGridValues>();
		if( isUsingCentimetres ) {
			addChartGridValues(MeasurementType.CENTIMETRES, clearOldValues);
		}
		if( isUsingInches ) {
			addChartGridValues(MeasurementType.INCHES, clearOldValues);
		}
	}

	@Override
	public String getDisplayName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public ProductSizeType getProductSizeType() {
		return productSizeType;
	}

	public void setProductSizeType(ProductSizeType productSizeType) {
		this.productSizeType = productSizeType;
	}

	public SizeChartAxisType getRowAxisType() {
		return rowAxisType;
	}

	public void setRowAxisType(SizeChartAxisType rowAxisType) {
		this.rowAxisType = rowAxisType;
	}

	public SizeChartAxisType getColumnAxisType() {
		return columnAxisType;
	}

	public void setColumnAxisType(SizeChartAxisType columnAxisType) {
		this.columnAxisType = columnAxisType;
	}
	
	public SizeChartAxisType getSizeChartAxisType( boolean isRow ) {
		SizeChartAxisType sizeChartAxisType;
		if( isRow ) {
			sizeChartAxisType = getRowAxisType();
		} else {
			sizeChartAxisType = getColumnAxisType();
		}
		return sizeChartAxisType;
	}
	
	public String getCellValue( MeasurementType measurementType, int columnIdx, int rowIdx ) {
		if( MeasurementType.INCHES.equals( measurementType ) ) {
			return getGridValueMap().get(MeasurementType.INCHES).getCellValue( columnIdx, rowIdx );
		} else if( MeasurementType.CENTIMETRES.equals( measurementType ) ) {
			return getGridValueMap().get(MeasurementType.CENTIMETRES).getCellValue( columnIdx, rowIdx );
		}
		return null;
	}
	
	public void setCellValue( MeasurementType measurementType, int columnIdx, int rowIdx, String value ) {
		if( MeasurementType.INCHES.equals( measurementType ) ) {
			getGridValueMap().get(MeasurementType.INCHES).setCellValue( columnIdx, rowIdx, value );
		} else if ( MeasurementType.CENTIMETRES.equals( measurementType ) ) {
			getGridValueMap().get(MeasurementType.CENTIMETRES).setCellValue( columnIdx, rowIdx, value );
		}
	}
	
	public String getCellValue( MeasurementType measurementType,  SizeChartAxisLabel columnLabel, SizeChartAxisLabel rowLabel ) {
			return getCellValue( measurementType, getColumnLabels().indexOf( columnLabel ), getRowLabels().indexOf( rowLabel ) );
	}
	
	public void setCellValue( MeasurementType measurementType,  SizeChartAxisLabel columnLabel, SizeChartAxisLabel rowLabel, String value ) {
		setCellValue( measurementType, getColumnLabels().indexOf( columnLabel ), getRowLabels().indexOf( rowLabel ), value );
	}

	public int getTablePagesNeededCount( int maxColumns, int tolerance ) {
		if( getColumnLabels() == null ) {
			return 0;
		} else {
			int columnLabelsSize = getColumnLabels().size();
			//this lets us have 14-16 cols on a table
			//(we dont want a whole new table for 2 cols)
			int numOfPages = ((Double)Math.floor(columnLabelsSize/(float)maxColumns)).intValue();
			if (columnLabelsSize % maxColumns > tolerance){
				numOfPages++;
			}
			if (numOfPages < 1) {
				numOfPages=1;
			}
			return numOfPages;
		}
	}

	public List<SizeChartAxisLabel> getColumnLabelsPaged( int numOfPages, int maxColumns, int currentPage ) {
		return getLabelsPaged(numOfPages, maxColumns, currentPage, getColumnLabels() );
	}


	public List<SizeChartAxisLabel> getLabelsPaged( int numOfPages, int maxColumns, int currentPage, List<SizeChartAxisLabel> originalList ) {
		List<SizeChartAxisLabel> processedList = new ArrayList<SizeChartAxisLabel>();
		int i = currentPage * maxColumns;
		if (currentPage > 0 ) {
			i++;
		}
		for (int j=0; i < originalList.size() && j < maxColumns; i++, j=j+1) {
			processedList.add(originalList.get(i));
		}
		if (currentPage == numOfPages-1) {
			//last page, take anything left over
			while (i < originalList.size()) {
				processedList.add(originalList.get(i));
				i++;
			}
		}
		return processedList;
	}
	
	public void refreshLabels( boolean isRow ) {
		List<SizeChartAxisLabel> axisLabels = null;
		SizeChartAxisType sizeChartAxisType = getSizeChartAxisType( isRow );
		if( sizeChartAxisType.equals( SizeChartAxisType.CUSTOM ) ) {
			axisLabels = new ArrayList<SizeChartAxisLabel>();
		} else if( sizeChartAxisType.equals( SizeChartAxisType.SIZES ) ) {
			BeanDao sizeDao = new BeanDao(ProductSize.class);
			sizeDao.setWhereCriteria("bean.productSizeType.id=" + getProductSizeType().getId());
			sizeDao.setOrderBy( "bean.positionIdx" );
			axisLabels = sizeDao.getAll();
		} else if( sizeChartAxisType.equals( SizeChartAxisType.SIZE_CATEGORIES ) ) {
			BeanDao categoryDao = new BeanDao(ProductSizeCategory.class);
			categoryDao.setWhereCriteria("bean.productSizeType.id=" + getProductSizeType().getId());
			categoryDao.setOrderBy( "bean.positionIdx" );
			axisLabels = categoryDao.getAll();
		}
		
		if( isRow ) {
			setRowLabels(axisLabels);
		} else {
			setColumnLabels(axisLabels);
		}
	}
	
	@Override
	public void saveBean(SystemUser currentUser) {
		for( ChartGridValues chartGridValues : getGridValueMap().values() ) {
			chartGridValues.saveDetails(currentUser);
		}
		if( SizeChartAxisType.CUSTOM.equals( rowAxisType ) ) {
			for( SizeChartAxisLabel rowLabel : getRowLabels() ) {
				rowLabel.saveDetails(currentUser);
			}
		}
		if( SizeChartAxisType.CUSTOM.equals( columnAxisType ) ) {
			for( SizeChartAxisLabel columnLabel : getColumnLabels() ) {
				columnLabel.saveDetails(currentUser);
			}
		}
		super.saveBean(currentUser);
	}

	public List<SizeChartAxisLabel> getRowLabels() {
		return rowLabels;
	}

	public List<SizeChartAxisLabel> getColumnLabels() {
		return columnLabels;
	}

	public void setRowLabels(List<SizeChartAxisLabel> rowLabels) {
		this.rowLabels = rowLabels;
	}

	public void setColumnLabels(List<SizeChartAxisLabel> columnLabels) {
		this.columnLabels = columnLabels;
	}

	public Map<MeasurementType,ChartGridValues> getGridValueMap() {
		return gridValueMap;
	}

	public void setGridValueMap(Map<MeasurementType,ChartGridValues> gridValueMap) {
		this.gridValueMap = gridValueMap;
	}

	public boolean isUsingCentimetres() {
		return isUsingCentimetres;
	}

	public void setUsingCentimetres(boolean isUsingCentimetres) {
		this.isUsingCentimetres = isUsingCentimetres;
	}

	public boolean isUsingInches() {
		return isUsingInches;
	}

	public void setUsingInches(boolean isUsingInches) {
		this.isUsingInches = isUsingInches;
	}

}




