package com.aplos.ecommerce.backingpage.customer;

import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.SortOrder;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.backingpage.realizedProductReturn.RealizedProductReturnLazyDataModel;
import com.aplos.ecommerce.backingpage.realizedProductReturn.RealizedProductReturnListPage;
import com.aplos.ecommerce.beans.CompanyContact;

@ManagedBean
@ViewScoped
public class CompanyContactReturnsListPage extends RealizedProductReturnListPage {
	private static final long serialVersionUID = 9045291501109456500L;

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new CompanyContactReturnLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class CompanyContactReturnLazyDataModel extends RealizedProductReturnLazyDataModel {

		private static final long serialVersionUID = -4428575409450025765L;

		public CompanyContactReturnLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
		}

		@Override
		public List<Object> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
			getBeanDao().clearWhereCriteria();
			CompanyContact companyContact = JSFUtil.getBeanFromScope(CompanyContact.class);
			getBeanDao().addWhereCriteria( "ec.id = " + companyContact.getId() );
			return super.load(first, pageSize, sortField, sortOrder, filters, false);
		}

	}
}
