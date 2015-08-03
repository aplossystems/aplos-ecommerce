package com.aplos.ecommerce.interfaces;

import com.aplos.common.beans.SystemUser;

public interface SizeChartAxisLabel {
	public String getSizeChartAxisLabelName();
	public int getSizeChartAxisLabelPositionIdx();
	public boolean saveDetails( SystemUser currentUser );
}
