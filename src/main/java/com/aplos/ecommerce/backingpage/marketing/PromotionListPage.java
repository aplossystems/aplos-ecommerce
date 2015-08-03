package com.aplos.ecommerce.backingpage.marketing;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.Promotion;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=Promotion.class)
public class PromotionListPage extends ListPage {
	private static final long serialVersionUID = 1115980916371031383L;

	public String getActiveText() {
		Promotion promotion = (Promotion) JSFUtil.getRequest().getAttribute("tableBean");
		StringBuffer activeText = new StringBuffer();
		if (!promotion.isValidForDate() || (!promotion.isActive() && promotion.getExpires() == null)) {
			activeText.append("<span style='color:darkred;'>");
		} else {
			activeText.append("<span style='color:green;'>");
		}
		if (promotion.getStarts() != null) {
			activeText.append(FormatUtil.formatDate(promotion.getStarts()));
		} else {
			activeText.append(FormatUtil.formatDate(promotion.getDateCreated()));
		}
		if (promotion.getExpires() != null) {
			activeText.append(" - ");
			activeText.append(FormatUtil.formatDate(promotion.getExpires()));
		} else {
			activeText.append(" onwards");
		}
		activeText.append("</span>");
		return activeText.toString();
	}

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new PromotionLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class PromotionLazyDataModel extends AplosLazyDataModel {

		private static final long serialVersionUID = -2717492671419336568L;

		public PromotionLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
		}

		@Override
		public String getSearchCriteria() {
			return "bean.code LIKE :similarSearchText OR bean.name LIKE :similarSearchText";
		}

	}

}
