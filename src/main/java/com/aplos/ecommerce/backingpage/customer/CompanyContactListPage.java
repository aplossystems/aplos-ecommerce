package com.aplos.ecommerce.backingpage.customer;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.event.SelectEvent;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.Company;
import com.aplos.ecommerce.beans.CompanyContact;
import com.aplos.ecommerce.beans.Customer;

@ManagedBean
@SessionScoped
@AssociatedBean(beanClass=CompanyContact.class)
public class CompanyContactListPage extends ListPage {
	private static final long serialVersionUID = 4946422176036636348L;

	private Customer selectedCustomer;

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(
			DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new CompanyContactLdm(dataTableState, aqlBeanDao);
	}

	@SuppressWarnings("unchecked")
	public List<Customer> suggestCustomers(String searchStr) {
		BeanDao customerDao = new BeanDao( Customer.class );
		customerDao.setSelectCriteria( "bean.id, bean.subscriber.firstName, bean.subscriber.surname, bean.active" );
		customerDao.addWhereCriteria("bean.id LIKE :exactSearchText OR CONCAT( bean.subscriber.firstName, ' ', bean.subscriber.surname ) LIKE :similarSearchText");
		customerDao.addWhereCriteria("bean.class = '" + Customer.class.getSimpleName() + "'" );
		customerDao.setMaxResults( 20 );
		customerDao.setIsReturningActiveBeans(true);
		customerDao.setNamedParameter( "similarSearchText", "%" + searchStr + "%" );
		customerDao.setNamedParameter( "exactSearchText", searchStr );
		return customerDao.getAll();
	}

	public String getCustomerDisplayString( Customer customer ) {
		return customer.getId() + " " + customer.getFullName();
	}

	public void importCustomer( SelectEvent event ) {
		Customer customer = (Customer) event.getObject();
		Company company = JSFUtil.getBeanFromScope(Company.class);

		Customer loadedCustomer = new BeanDao( Customer.class ).get( customer.getId() );

		try {
			CompanyContact companyContact = CompanyContact.convertFromCustomer( loadedCustomer );
			if( CommonUtil.getStringOrEmpty( company.getShippingAddress().getLine1() ).equals( "" ) ) {
				company.getShippingAddress().copyAddressOnly( companyContact.getShippingAddress() );
			}
			if( CommonUtil.getStringOrEmpty( company.determineBillingAddress().getLine1() ).equals( "" ) ) {
				company.determineBillingAddress().copyAddressOnly( companyContact.getBillingAddress() );
			}
			companyContact.setCompany( company );
			companyContact.saveDetails();
		} catch( Exception ex ) {
			//  This has been put in to try and catch an unexplained error 27 June 2011 - Anthony Mayfield
			ApplicationUtil.getAplosContextListener().handleError( ex );
		}
		setSelectedCustomer( null );
	}

	public Customer getSelectedCustomer() {
		return selectedCustomer;
	}

	public void setSelectedCustomer(Customer selectedCustomer) {
		this.selectedCustomer = selectedCustomer;
	}
}
