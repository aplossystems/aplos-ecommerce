package com.aplos.ecommerce.backingpage.customer;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.DataTableState;
import com.aplos.ecommerce.beans.CompanyType;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=CompanyType.class)
public class CompanyTypeListPage extends ListPage {
	private static final long serialVersionUID = 3237889028307739797L;


	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new CompanyTypeLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class CompanyTypeLazyDataModel extends AplosLazyDataModel {

		private static final long serialVersionUID = 7634587148778045714L;

		public CompanyTypeLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
		}

		@Override
		public String getSearchCriteria() {
			return "bean.name LIKE :similarSearchText";
		}

	}

}
