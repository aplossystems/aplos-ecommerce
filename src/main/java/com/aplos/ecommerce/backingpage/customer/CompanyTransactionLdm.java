package com.aplos.ecommerce.backingpage.customer;

import java.util.List;
import java.util.Map;

import org.primefaces.model.SortOrder;

import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.backingpage.transaction.TransactionLazyDataModel;
import com.aplos.ecommerce.beans.Company;
import com.aplos.ecommerce.beans.CompanyContact;

public class CompanyTransactionLdm extends TransactionLazyDataModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 6736322400259365596L;

	public CompanyTransactionLdm( DataTableState dataTableState, BeanDao aqlBeanDao ) {
		super(dataTableState, aqlBeanDao);
	}

	@Override
	public List<Object> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
		Company company = JSFUtil.getBeanFromScope(Company.class);
		getBeanDao().clearWhereCriteria();
		getBeanDao().addWhereCriteria( "bean.ecommerceShoppingCart.customer.id IN (FROM " + AplosBean.getTableName( CompanyContact.class ) + " companyContact WHERE companyContact.company.id = " + company.getId() + ")" );

		return super.load(first, pageSize, sortField, sortOrder, filters, false );
	}
}
