package com.aplos.ecommerce.backingpage.product.size;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.enums.CommonBundleKey;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.CustomSizeChartAxisLabel;
import com.aplos.ecommerce.beans.DiscountAllowance;
import com.aplos.ecommerce.beans.product.ProductSizeChart;
import com.aplos.ecommerce.beans.product.ProductSizeChart.MeasurementType;
import com.aplos.ecommerce.beans.product.ProductSizeChart.SizeChartAxisType;
import com.aplos.ecommerce.interfaces.SizeChartAxisLabel;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=ProductSizeChart.class)
public class ProductSizeChartEditPage extends EditPage {
	private static final long serialVersionUID = 8902656423115590201L;

	private String customColumnName;
	private String customRowName;
	private int maxColumns = 14;
	private int columnsTolerance = 2;
	private MeasurementType currentMeasurementType = MeasurementType.INCHES;

	public String addCustomRowLabel() {
		if ( CommonUtil.isNullOrEmpty( getCustomRowName() ) ) {
			JSFUtil.addMessageForError("Please enter valid text for the custom row label");
		} else {
			ProductSizeChart productSizeChart = JSFUtil.getBeanFromScope( ProductSizeChart.class );
			if (productSizeChart != null) {
				CustomSizeChartAxisLabel customSizeChartAxisLabel = new CustomSizeChartAxisLabel();
				customSizeChartAxisLabel.setName(getCustomRowName());
				productSizeChart.addCustomAxisLabel( true, customSizeChartAxisLabel, true);
			}
		}
		return null;
	}
	
	public void usingCentimetresUpdated() {
		ProductSizeChart productSizeChart = JSFUtil.getBeanFromScope( ProductSizeChart.class );
		if( productSizeChart.isUsingCentimetres() ) {
			productSizeChart.addChartGridValues( MeasurementType.CENTIMETRES, false );
		}
	}
	
	public void usingInchesUpdated() {
		ProductSizeChart productSizeChart = JSFUtil.getBeanFromScope( ProductSizeChart.class );
		if( productSizeChart.isUsingInches() ) {
			productSizeChart.addChartGridValues( MeasurementType.INCHES, false );
		}
	}

	public String addCustomColumnLabel() {
		if ( CommonUtil.isNullOrEmpty( getCustomRowName() ) ) {
			JSFUtil.addMessageForError("Please enter valid text for the custom column label");
		} else {
			ProductSizeChart productSizeChart = JSFUtil.getBeanFromScope( ProductSizeChart.class );
			if (productSizeChart != null) {
				CustomSizeChartAxisLabel customSizeChartAxisLabel = new CustomSizeChartAxisLabel();
				customSizeChartAxisLabel.setName(getCustomRowName());
				productSizeChart.addCustomAxisLabel( false, customSizeChartAxisLabel, true);
			}
		}
		return null;
	}

	public String removeCustomRowLabel() {
		SizeChartAxisLabel sizeChartAxisLabel = (SizeChartAxisLabel) JSFUtil.getRequest().getAttribute("rowLabel");
		ProductSizeChart productSizeChart = JSFUtil.getBeanFromScope( ProductSizeChart.class );
		if (productSizeChart != null) {
			productSizeChart.getRowLabels().remove(sizeChartAxisLabel);
		}
		return null;
	}

	public String removeCustomColumnLabel() {
		SizeChartAxisLabel sizeChartAxisLabel = (SizeChartAxisLabel) JSFUtil.getRequest().getAttribute("columnLabel");
		ProductSizeChart productSizeChart = JSFUtil.getBeanFromScope( ProductSizeChart.class );
		if (productSizeChart != null) {
			productSizeChart.getColumnLabels().remove(sizeChartAxisLabel);
		}
		return null;
	}
	
	public void clearValues() {
		ProductSizeChart productSizeChart = JSFUtil.getBeanFromScope( ProductSizeChart.class );
		productSizeChart.setRowAxisType( null );
		productSizeChart.setColumnAxisType( null );
		productSizeChart.updateGridValues( true );
	}
	
	public void columnAxisUpdated() {
		ProductSizeChart productSizeChart = JSFUtil.getBeanFromScope( ProductSizeChart.class );
		productSizeChart.refreshLabels( false );
		productSizeChart.updateGridValues( true );
	}
	
	public List<SelectItem> getSizeChartAxisTypeSelectItems() {
		return CommonUtil.getEnumSelectItems( SizeChartAxisType.class );
	}

	public List<SelectItem> getColumnSizeChartAxisTypeSelectItems() {
		List<SelectItem> columnSizeSelectItems = CommonUtil.getEnumSelectItems( SizeChartAxisType.class );
		ProductSizeChart productSizeChart = JSFUtil.getBeanFromScope( ProductSizeChart.class );
		if( !productSizeChart.getRowAxisType().equals( SizeChartAxisType.CUSTOM ) ) {
			for( int i = columnSizeSelectItems.size() - 1; i > -1; i-- ) {
				if( columnSizeSelectItems.get( i ).getValue().equals( productSizeChart.getRowAxisType() ) ) {
					columnSizeSelectItems.remove( i );
					break;
				}
			}
		}
		return columnSizeSelectItems;
	}
	
	public String getCellValue() {
		ProductSizeChart productSizeChart = JSFUtil.getBeanFromScope( ProductSizeChart.class );
		SizeChartAxisLabel columnLabel = (SizeChartAxisLabel) JSFUtil.getRequest().getAttribute( "columnLabel" );
		SizeChartAxisLabel rowLabel = (SizeChartAxisLabel) JSFUtil.getRequest().getAttribute( "rowLabel" );
		if( getCurrentMeasurementType() != null && columnLabel != null && rowLabel != null ) {
			return productSizeChart.getCellValue( getCurrentMeasurementType(), columnLabel, rowLabel);
		} else {
			return null;
		}
	}
	
	public void setCellValue( String cellValue ) {
		ProductSizeChart productSizeChart = JSFUtil.getBeanFromScope( ProductSizeChart.class );
		SizeChartAxisLabel columnLabel = (SizeChartAxisLabel) JSFUtil.getRequest().getAttribute( "columnLabel" );
		SizeChartAxisLabel rowLabel = (SizeChartAxisLabel) JSFUtil.getRequest().getAttribute( "rowLabel" );
		if( getCurrentMeasurementType() != null && columnLabel != null && rowLabel != null ) {
			productSizeChart.setCellValue( getCurrentMeasurementType(), columnLabel, rowLabel, cellValue );
		}
	}

	public List<Integer> getTablePages() {
		List<Integer> pagesNeeded = new ArrayList<Integer>();
		ProductSizeChart productSizeChart = JSFUtil.getBeanFromScope( ProductSizeChart.class );
		for (int i=0, n = productSizeChart.getTablePagesNeededCount( maxColumns, columnsTolerance ); i < n; i++) {
			pagesNeeded.add(i);
		}
		return pagesNeeded;
	}

	public List<SizeChartAxisLabel> getColumnLabelsPaged( MeasurementType currentMeasurementType ) {
		setCurrentMeasurementType(currentMeasurementType);
		return getColumnLabelsPaged();
	}

	public List<SizeChartAxisLabel> getColumnLabelsPaged() {
		ProductSizeChart productSizeChart = JSFUtil.getBeanFromScope( ProductSizeChart.class );
		Integer page = (Integer) JSFUtil.getRequest().getAttribute("tablePage");
		if( page == null ) {
			page = 0;
		}
		int numOfPages = productSizeChart.getTablePagesNeededCount( maxColumns, columnsTolerance );
		
		return productSizeChart.getColumnLabelsPaged( numOfPages, maxColumns, page );
	}

	@Override
	public void applyBtnAction() {
		saveAction();
		JSFUtil.addMessage(ApplicationUtil.getAplosContextListener().translateByKey( CommonBundleKey.SAVED_SUCCESSFULLY ),FacesMessage.SEVERITY_INFO);
	}

	public void saveAction() {
		ProductSizeChart productSizeChart = JSFUtil.getBeanFromScope( ProductSizeChart.class );
		productSizeChart.saveDetails();
	}

	public String getCustomColumnName() {
		return customColumnName;
	}

	public void setCustomColumnName(String customColumnName) {
		this.customColumnName = customColumnName;
	}

	public String getCustomRowName() {
		return customRowName;
	}

	public void setCustomRowName(String customRowName) {
		this.customRowName = customRowName;
	}

	public MeasurementType getCurrentMeasurementType() {
		return currentMeasurementType;
	}

	public void setCurrentMeasurementType(MeasurementType currentMeasurementType) {
		this.currentMeasurementType = currentMeasurementType;
	}

}
