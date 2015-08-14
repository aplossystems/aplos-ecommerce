package com.aplos.ecommerce.backingpage.customer;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.StringUtils;
import org.primefaces.model.SortOrder;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.beans.Subscriber;
import com.aplos.common.enums.DocumentType;
import com.aplos.common.enums.SubscriptionHook;
import com.aplos.common.utils.FileIoUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.CompanyContact;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.listbeans.CustomerListBean;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=Customer.class)
public class CustomerListPage extends ListPage  {
	private static final long serialVersionUID = -3670301394042587424L;

	@Override
	public BeanDao getListBeanDao() {
		BeanDao aqlBeanDao = new BeanDao( Customer.class );
		aqlBeanDao.setListBeanClass( CustomerListBean.class );
		aqlBeanDao.setSelectCriteria( "bean.id, bean.subscriber.firstName, bean.subscriber.surname, bean.subscriber.emailAddress, bean.shippingAddress.postcode, bean.shippingAddress.phone, bean.active, bean.deletable" );
		return aqlBeanDao;
	}

	@Override
	public AplosLazyDataModel getAplosLazyDataModel( DataTableState dataTableState, BeanDao aqlBeanDao ) {
		return new CustomerLazyDataModel( dataTableState, aqlBeanDao );
	}
	
	public void exportToCsv() {
		StringBuffer fieldsToInclude = new StringBuffer();
		fieldsToInclude.append("bean.id as ID,");
		fieldsToInclude.append("s.firstName as firstName,");
		fieldsToInclude.append("s.surname as surname,");
		fieldsToInclude.append("sa.line1 as line1,");
		fieldsToInclude.append("sa.line2 as line2,");
		fieldsToInclude.append("sa.line3 as line3,");
		fieldsToInclude.append("sa.city AS shippingAddressCity,");
		fieldsToInclude.append("sa.state AS shippingAddressState,");
		fieldsToInclude.append("sac.name AS shippingAddressCountry,");
		fieldsToInclude.append("sa.postcode AS shippingAddressPostcode,");
		fieldsToInclude.append("sa.phone AS shippingAddressPhone,");
		fieldsToInclude.append("s.emailAddress AS email,");
		fieldsToInclude.append("bean.dateCreated AS dateCreated");
		BeanDao dao = new BeanDao(Customer.class);
		dao.setSelectCriteria(fieldsToInclude.toString());
		dao.addQueryTable( "s", "bean.subscriber" );
		dao.addQueryTable( "sa", "bean.shippingAddress" );
		dao.addQueryTable( "sac", "bean.shippingAddress.country" );
		
		try {
			FileIoUtil.fileDownload(FileIoUtil.generateCsvFile(dao), DocumentType.COMMA_SEPERATED_VALUES);
			JSFUtil.addMessage("Your customer details have been exported.", FacesMessage.SEVERITY_INFO);
		} catch (InputMismatchException ime) {
			ime.printStackTrace();
		}
	}

	/**
	 * Please note that this is export DETAILS - it exports human readable data,
	 * not data necessarily suitable for import at a later date
	 */
	public String exportCustomerDetails() {
		BeanDao dao = new BeanDao(Customer.class);
		StringBuffer fieldsToInclude = new StringBuffer();
		fieldsToInclude.append("bean.username AS username,");
		fieldsToInclude.append("s.firstName AS firstName,");
		fieldsToInclude.append("s.surname AS surname,");
		fieldsToInclude.append("s.emailAddress AS email,");
		fieldsToInclude.append("s.isSubscribed AS newsletterSubscriber,");
		fieldsToInclude.append("bean.isVerified AS emailVerified,");
		fieldsToInclude.append("ba.phone AS telephone,");
		fieldsToInclude.append("ba.line1 AS billingAddressLine1,");
		fieldsToInclude.append("ba.line2 AS billingAddressLine2,");
		fieldsToInclude.append("ba.line3 AS billingAddressLine3,");
		fieldsToInclude.append("ba.city AS billingAddressCity,");
		fieldsToInclude.append("ba.state AS billingAddressState,");
		fieldsToInclude.append("bac.name AS billingAddressCountry,");
		fieldsToInclude.append("sa.phone AS shippingAddressLine1,");
		fieldsToInclude.append("sa.line1 AS shippingAddressLine1,");
		fieldsToInclude.append("sa.line2 AS shippingAddressLine2,");
		fieldsToInclude.append("sa.line3 AS shippingAddressLine3,");
		fieldsToInclude.append("sa.city AS shippingAddressCity,");
		fieldsToInclude.append("sa.state AS shippingAddressState,");
		fieldsToInclude.append("sac.name AS shippingAddressCountry,");
		fieldsToInclude.append("ct.name AS customerType,");
		fieldsToInclude.append("bean.loyaltyPoints,");
		fieldsToInclude.append("bean.storeCreditAmount AS storeCedit,");
		fieldsToInclude.append("cur.symbol AS preferredCurrency,");
		fieldsToInclude.append("bean.customerNotes AS notes");
		dao.setSelectCriteria(fieldsToInclude.toString());
		dao.addQueryTable( "ct", "bean.customerType" );
		dao.addQueryTable( "cur", "bean.currency" );
		dao.addQueryTable( "s", "bean.subscriber" );
		dao.addQueryTable( "ba", "bean.billingAddress" );
		dao.addQueryTable( "sa", "bean.shippingAddress" );
		dao.addQueryTable( "bac", "bean.billingAddress.country" );
		dao.addQueryTable( "sac", "bean.shippingAddress.country" );
		try {
			FileIoUtil.fileDownload(FileIoUtil.generateCsvFile(dao), DocumentType.COMMA_SEPERATED_VALUES);
			JSFUtil.addMessage("Your customer details have been exported.", FacesMessage.SEVERITY_INFO);
		} catch (InputMismatchException ime) {
			ime.printStackTrace();
		}
		return null;
	}

	public String exportCustomerMarketingDetails() {
		BeanDao dao = new BeanDao(Customer.class);
		StringBuffer fieldsToInclude = new StringBuffer();
		fieldsToInclude.append("s.firstName AS firstName,");
		fieldsToInclude.append("s.surname AS surname,");
		fieldsToInclude.append("s.emailAddress AS email,");
		fieldsToInclude.append("sa.phone AS telephone,");
		fieldsToInclude.append("sa.line1 AS line1,");
		fieldsToInclude.append("sa.line2 AS line2,");
		fieldsToInclude.append("sa.line3 AS line3,");
		fieldsToInclude.append("sa.city AS city,");
		fieldsToInclude.append("sa.state AS state,");
		fieldsToInclude.append("sac.name AS country");
		dao.addQueryTable( "s", "bean.subscriber" );
		dao.addQueryTable( "sa", "bean.shippingAddress" );
		dao.addQueryTable( "sac", "bean.shippingAddress.country" );
		dao.setSelectCriteria(fieldsToInclude.toString());
		try {
			FileIoUtil.fileDownload(FileIoUtil.generateCsvFile(dao), DocumentType.COMMA_SEPERATED_VALUES);
			JSFUtil.addMessage("Your customer details have been exported.", FacesMessage.SEVERITY_INFO);
		} catch (InputMismatchException ime) {
			ime.printStackTrace();
		}
		return null;
	}

	public class CustomerLazyDataModel extends AplosLazyDataModel {
		private static final long serialVersionUID = 1689651652602379963L;

		public CustomerLazyDataModel( DataTableState dataTableState, BeanDao aqlBeanDao ) {
			super( dataTableState, aqlBeanDao );
		}

		@Override
		public List<Object> load(int first, int pageSize, String sortField,	SortOrder sortOrder, Map<String, String> filters) {
			
			List<Object> results = super.load(first, pageSize, sortField, sortOrder, filters);
			
			determineCustomerType( results );
			CustomerListBean.determineOrderCounts( results );
			
			return results;
		}

		public void determineCustomerType( List<Object> results ) {
			CustomerListBean tempCustomerListBean;

			List<String> customerIdList = new ArrayList<String>();
			for( Object tempCustomerListBeanObj : results ) {
				if( tempCustomerListBeanObj != null ) {
					tempCustomerListBean = (CustomerListBean) tempCustomerListBeanObj;
					customerIdList.add( String.valueOf( tempCustomerListBean.getId() ) );
				}
			}

			if( customerIdList.size() > 0 ) {
				BeanDao aqlBeanDao = new BeanDao( CompanyContact.class );
				aqlBeanDao.setSelectCriteria( "bean.id" );
				aqlBeanDao.addWhereCriteria( "bean.id IN (" + StringUtils.join( customerIdList, "," ) + ")" );
				@SuppressWarnings("unchecked")
				List<Long> queryResults = aqlBeanDao.getResultFields();

				outerLoop : for( Object tempCustomerListBeanObj : results ) {
					if( tempCustomerListBeanObj != null ) {
						tempCustomerListBean = (CustomerListBean) tempCustomerListBeanObj;
						for( Long tempCustomerId : queryResults ) {
							if( tempCustomerListBean.getId().equals( tempCustomerId ) ) {
								tempCustomerListBean.setCustomerClassType( "Company contact" );
								continue outerLoop;
							}
						}
						tempCustomerListBean.setCustomerClassType( "Customer" );
					}
				}
			}
		}

		@Override
		public String getSearchCriteria() {
			return "bean.id LIKE :similarSearchText OR CONCAT(bean.subscriber.firstName, ' ', bean.subscriber.surname) LIKE :similarSearchText OR bean.subscriber.emailAddress LIKE :similarSearchText OR bean.shippingAddress.postcode LIKE :similarSearchText OR bean.shippingAddress.phone LIKE :similarSearchText";
		}

		@Override
		public AplosBean selectBean() {
			CustomerListBean customerListBean = (CustomerListBean) JSFUtil.getRequest().getAttribute( "tableBean" );
			Customer loadedCustomer = (Customer) new BeanDao( Customer.class ).get( customerListBean.getId() ).getSaveableBean();
			loadedCustomer.addToScope();
			if( loadedCustomer instanceof CompanyContact ) {
				((CompanyContact)loadedCustomer).getCompany().addToScope();
			}
			JSFUtil.redirect( loadedCustomer.getEditPageClass() );
			return loadedCustomer;
		}

		@Override
		public void goToNew() {
			Customer customer = new Customer();
			customer.addToScope();
			customer.setSubscriber(new Subscriber());
			customer.getSubscriber().setSubscriptionHook(SubscriptionHook.BACKEND_ENTRY);
			JSFUtil.redirect( customer.getEditPageClass() );
		}
	}
}
