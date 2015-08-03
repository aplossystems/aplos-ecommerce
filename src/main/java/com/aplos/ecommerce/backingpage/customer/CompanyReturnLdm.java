package com.aplos.ecommerce.backingpage.customer;

import java.util.List;
import java.util.Map;

import org.primefaces.model.SortOrder;

import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.backingpage.realizedProductReturn.RealizedProductReturnLazyDataModel;
import com.aplos.ecommerce.beans.Company;
import com.aplos.ecommerce.beans.CompanyContact;


public class CompanyReturnLdm extends RealizedProductReturnLazyDataModel {

	private static final long serialVersionUID = 3982276000082538148L;

	public CompanyReturnLdm( DataTableState dataTableState, BeanDao aqlBeanDao ) {
		super(dataTableState, aqlBeanDao);
	}

	@Override
	public List<Object> load(int first, int pageSize, String sortField,	SortOrder sortOrder, Map<String, String> filters) {
		getBeanDao().clearWhereCriteria();
		Company company = JSFUtil.getBeanFromScope(Company.class);
		String companyIdList = "(FROM " + AplosBean.getTableName( CompanyContact.class ) + " companyContact WHERE companyContact.company.id = " + company.getId() + ")";
		getBeanDao().addWhereCriteria( "ec.id IN " + companyIdList );
		return super.load(first, pageSize, sortField, sortOrder, filters, false );
	}

}
