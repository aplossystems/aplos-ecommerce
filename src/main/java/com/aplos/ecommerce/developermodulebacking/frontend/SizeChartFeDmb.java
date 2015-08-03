package com.aplos.ecommerce.developermodulebacking.frontend;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.ChartGridValues;
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.beans.developercmsmodules.SizeChartModule;
import com.aplos.ecommerce.beans.product.ProductSizeChart;
import com.aplos.ecommerce.beans.product.ProductSizeChart.MeasurementType;
import com.aplos.ecommerce.interfaces.SizeChartAxisLabel;

@ManagedBean
@ViewScoped
public class SizeChartFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = -3198650503440881542L;
	private boolean isShowingInches = true;
	private MeasurementType currentMeasurementType = MeasurementType.INCHES;
	private ProductSizeChart sizeChart;
	private SizeChartModule sizeChartModule;
	private List<SizeChartAxisLabel> visibleColumnLabels;
	private RealizedProduct currentRealizedProduct;
	
	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {
		boolean continueLoading = super.responsePageLoad(developerCmsAtom);
		if( getCurrentRealizedProduct() == null ) {
			RealizedProduct sessionRealizedProduct = (RealizedProduct) JSFUtil.getBeanFromScope(RealizedProduct.class);
			String rpidParameter = JSFUtil.getRequestParameter( "rpid" );
			if( rpidParameter != null 
					&& (sessionRealizedProduct == null 
					    || !rpidParameter.equals( String.valueOf( sessionRealizedProduct.getId() ) ) ) ) {
				try {
					Long realizedProductId = Long.parseLong( rpidParameter );
					setCurrentRealizedProduct( (RealizedProduct) new BeanDao( RealizedProduct.class ).get( realizedProductId ) );
				} catch( NumberFormatException nfEx ) {
					ApplicationUtil.handleError( nfEx );
					setCurrentRealizedProduct( sessionRealizedProduct );
				}
			} else {
				setCurrentRealizedProduct( sessionRealizedProduct );	
			}
		}
		if( getSizeChartModule() == null ) {
			setSizeChartModule( (SizeChartModule) developerCmsAtom );
			calculateVisibleColumns();
		}
		return continueLoading;
	}
	
	public void calculateVisibleColumns() {
		ProductSizeChart productSizeChart = getSizeChart(); 
		if( productSizeChart != null ) {
			List<SizeChartAxisLabel> columnLabels = productSizeChart.getColumnLabels();
			visibleColumnLabels = new ArrayList<SizeChartAxisLabel>();
			for( int i = 0, n = columnLabels.size(); i < n; i++ ) {
				ChartGridValues chartGridValues = productSizeChart.getGridValueMap().get( getCurrentMeasurementType() );
				for( int j = 0, p = chartGridValues.getRowValues().size(); j < p; j++ ) {
					if( !CommonUtil.isNullOrEmpty( chartGridValues.getRowValues().get( j ).getCellValues().get( i ) ) ) {
						visibleColumnLabels.add( columnLabels.get( i ) );
						break;
					}
				}
			}
		}
	}
	
	public boolean isShowingInches() {
		return isShowingInches;
	}
	public void setShowingInches(boolean isShowingInches) {
		this.isShowingInches = isShowingInches;
	}

	public String getCircleImagePath( MeasurementType measurementType ) {
		if( currentMeasurementType.equals( measurementType ) ) {
			return JSFUtil.getContextPath() + "/images/selected-circle.jpg";
		} else {
			return JSFUtil.getContextPath() + "/images/unselected-circle.jpg";
		}
	}

	public List<Integer> getTablePages() {
		List<Integer> pagesNeeded = new ArrayList<Integer>();
		for (int i=0; i < getTablePagesNeededCount(); i++) {
			pagesNeeded.add(i);
		}
		return pagesNeeded;
	}

	private int getTablePagesNeededCount() {
		if( getSizeChart() != null ) {
			return getSizeChart().getTablePagesNeededCount( getSizeChartModule().getMaxColumns(), getSizeChartModule().getColumnTolerance() );
		} else {
			return 0;
		}
	}

	public List<SizeChartAxisLabel> getColumnLabelsPaged() {
		if( getSizeChart() != null ) {
			Integer currentPage = (Integer) JSFUtil.getRequest().getAttribute("tablePage");
			if( currentPage == null ) {
				currentPage = 0;
			}
			int numOfPages = getTablePagesNeededCount();
			
			return getSizeChart().getLabelsPaged(numOfPages, getSizeChartModule().getMaxColumns(), currentPage, getVisibleColumnLabels() );
		} else {
			return new ArrayList<SizeChartAxisLabel>();
		}
	}

	public ProductSizeChart getSizeChart() {
		if (sizeChart == null) {
			if (getCurrentRealizedProduct() != null) {
				sizeChart = getCurrentRealizedProduct().getProductInfo().getProductSizeChart();
			}
		}
		return sizeChart;
	}

	public String getFillWidthClass() {
		Integer page = (Integer) JSFUtil.getRequest().getAttribute("tablePage");
		if (page == null || page+1 != getTablePagesNeededCount()) {
			return "aplos-fill-width";
		}
		return "";
	}
	
	public String getCellValue() {
		ProductSizeChart productSizeChart = getSizeChart();
		SizeChartAxisLabel columnLabel = (SizeChartAxisLabel) JSFUtil.getRequest().getAttribute( "columnLabel" );
		SizeChartAxisLabel rowLabel = (SizeChartAxisLabel) JSFUtil.getRequest().getAttribute( "rowLabel" );
		if( getCurrentMeasurementType() != null && columnLabel != null && rowLabel != null ) {
			return productSizeChart.getCellValue( getCurrentMeasurementType(), columnLabel, rowLabel);
		} else {
			return null;
		}
	}

	public MeasurementType getCurrentMeasurementType() {
		return currentMeasurementType;
	}

	public void setCurrentMeasurementType(MeasurementType currentMeasurementType) {
		this.currentMeasurementType = currentMeasurementType;
	}

	private SizeChartModule getSizeChartModule() {
		return sizeChartModule;
	}

	private void setSizeChartModule(SizeChartModule sizeChartModule) {
		this.sizeChartModule = sizeChartModule;
	}

	public List<SizeChartAxisLabel> getVisibleColumnLabels() {
		return visibleColumnLabels;
	}

	public void setVisibleColumnLabels(List<SizeChartAxisLabel> visibleColumnLabels) {
		this.visibleColumnLabels = visibleColumnLabels;
	}

	public RealizedProduct getCurrentRealizedProduct() {
		return currentRealizedProduct;
	}

	public void setCurrentRealizedProduct(RealizedProduct currentProduct) {
		this.currentRealizedProduct = currentProduct;
	}
}

