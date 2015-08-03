package com.aplos.ecommerce.beans.developercmsmodules;

import com.aplos.cms.beans.developercmsmodules.ConfigurableDeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Entity;

@Entity
@DynamicMetaValueKey(oldKey="SIZE_CHART_MODULE")
public class SizeChartModule extends ConfigurableDeveloperCmsAtom {

	private static final long serialVersionUID = -1839258148846867641L;

	private int maxColumns = 25;
	private int columnTolerance = 2;

	public SizeChartModule() {
		super();
	}

	@Override
	public String getName() {
		return "Size Charts";
	}

	@Override
	public DeveloperCmsAtom getCopy() {
		SizeChartModule copiedAtom = new SizeChartModule();
		copiedAtom.setMaxColumns(getMaxColumns());
		copiedAtom.setColumnTolerance(getColumnTolerance());
		return copiedAtom;
	}

	public int getMaxColumns() {
		return maxColumns;
	}

	public void setMaxColumns(int maxColumns) {
		this.maxColumns = maxColumns;
	}

	public int getColumnTolerance() {
		return columnTolerance;
	}

	public void setColumnTolerance(int columnTolerance) {
		this.columnTolerance = columnTolerance;
	}

}


















