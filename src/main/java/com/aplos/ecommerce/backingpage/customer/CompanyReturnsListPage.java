package com.aplos.ecommerce.backingpage.customer;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.DataTableState;
import com.aplos.ecommerce.backingpage.realizedProductReturn.RealizedProductReturnListPage;
import com.aplos.ecommerce.beans.RealizedProductReturn;

@ManagedBean
@ViewScoped
public class CompanyReturnsListPage extends RealizedProductReturnListPage {

	private static final long serialVersionUID = -4633324312668789815L;

	@Override
	public BeanDao getListBeanDao() {
		return new BeanDao( RealizedProductReturn.class );
	}

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new CompanyReturnLdm(dataTableState, aqlBeanDao);
	}
}
