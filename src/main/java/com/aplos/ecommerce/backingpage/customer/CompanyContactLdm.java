package com.aplos.ecommerce.backingpage.customer;

import java.util.List;
import java.util.Map;

import org.primefaces.model.SortOrder;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.Address;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.beans.Subscriber;
import com.aplos.common.enums.SubscriptionHook;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.Company;
import com.aplos.ecommerce.beans.CompanyContact;

public class CompanyContactLdm extends AplosLazyDataModel {

	private static final long serialVersionUID = 1951290326336489923L;

	public CompanyContactLdm( DataTableState dataTableState, BeanDao aqlBeanDao ) {
		super(dataTableState, aqlBeanDao);
		getBeanDao().setSelectCriteria( "bean.id, s.firstName, s.surname, s.emailAddress, sa.postcode, sa.phone, bean.position, bean.customerNotes, bean.active, bean.deletable" );
//		getBeanDao().setSelectJoin( " LEFT OUTER JOIN bean.subscriber s LEFT OUTER JOIN bean.shippingAddress sa" );
		getBeanDao().addQueryTable( "s", "bean.subscriber_id" );
		getBeanDao().addQueryTable( "sa", "bean.shippingAddress_id" );
	}

	@Override
	public List<Object> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters ) {
		return load(first, pageSize, sortField, sortOrder, filters, true );
	}

	public List<Object> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters, boolean clearCriteria ) {
		if( clearCriteria ) {
			getBeanDao().clearWhereCriteria();
		}
		Company company = JSFUtil.getBeanFromScope(Company.class);
		getBeanDao().addWhereCriteria( "bean.company.id = " + company.getId() );
		return super.load(first, pageSize, sortField, sortOrder, filters);
	}

	@Override
	public void goToNew() {
		super.goToNew( true );
		CompanyContact companyContact = new CompanyContact();
		companyContact.setCompany((Company)JSFUtil.getBeanFromScope(Company.class));
		companyContact.getSubscriber().setSubscriptionHook(SubscriptionHook.BACKEND_ENTRY);
		companyContact.addToScope();
	}
}
