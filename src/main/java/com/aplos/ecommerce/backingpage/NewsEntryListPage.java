package com.aplos.ecommerce.backingpage;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.DataTableState;
import com.aplos.ecommerce.beans.NewsEntry;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=NewsEntry.class)
public class NewsEntryListPage extends ListPage {

	private static final long serialVersionUID = -482504237864553331L;

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new NewsEntryLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class NewsEntryLazyDataModel extends AplosLazyDataModel {

		private static final long serialVersionUID = 5116276003814648681L;

		public NewsEntryLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
		}

		@Override
		public String getSearchCriteria() {
			return "bean.title LIKE :similarSearchText";
		}

	}
}
