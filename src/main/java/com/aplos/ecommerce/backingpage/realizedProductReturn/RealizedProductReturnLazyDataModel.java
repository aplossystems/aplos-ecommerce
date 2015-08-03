package com.aplos.ecommerce.backingpage.realizedProductReturn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.primefaces.model.SortOrder;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.utils.CommonUtil;
import com.aplos.ecommerce.beans.CompanyContact;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.RealizedProductReturn;
import com.aplos.ecommerce.beans.RealizedProductReturn.RealizedProductReturnStatus;
import com.aplos.ecommerce.beans.listbeans.RealizedProductReturnListBean;


public class RealizedProductReturnLazyDataModel extends AplosLazyDataModel {

	private static final long serialVersionUID = -8550474479451918151L;
	private RealizedProductReturnStatus currentlyShowingReturnStatus;

	public RealizedProductReturnLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		super(dataTableState, aqlBeanDao);
		
//		4-5-12 - had to remove this optimisation because cached original customer is part of a list now, there's no way to add the alias.
		
		getBeanDao().setSelectCriteria( "bean.id, rtnpp.name, ec.id, ec.subscriber.firstName, ec.subscriber.surname, ra.contactFirstName, ra.contactSurname, ra.companyName, " +
				"rtnp.itemCode, rtnpp.id, rtnp.id, " +
				"rtnpp.itemCode, bean.dateCreated," +
				"bean.realizedProductReturnStatus, bean.deletable, bean.active" );
		getBeanDao().addQueryTable( "sn", "bean.serialNumber" );
		getBeanDao().addQueryTable( "rtnp", "bean.serialNumber.realizedProduct" );
		getBeanDao().addQueryTable( "rtnpp", "bean.serialNumber.realizedProduct.productInfo.product" );
		getBeanDao().addQueryTable( "ra", "bean.returnAddress" );
		getBeanDao().addQueryTable( "ec", "sn.currentCustomer" );
		getBeanDao().addQueryTable( "ecs", "ec.subscriber" );
//		getBeanDao().addAliasesForOptimisation( "sn", "serialNumber" );
//		getBeanDao().addAliasesForOptimisation( "ra", "returnAddress" );
//		getBeanDao().addAliasesForOptimisation( "ec", "serialNumber.currentCustomer" );
//		getBeanDao().addAliasesForOptimisation( "ecs", "serialNumber.currentCustomer.subscriber" );
//		getBeanDao().addAliasesForOptimisation( "rtnp", "serialNumber.realizedProduct" );
//		getBeanDao().addAliasesForOptimisation( "rtnpp", "serialNumber.realizedProduct.productInfo.product" );
		getBeanDao().setListBeanClass( RealizedProductReturnListBean.class );
		setEditPageClass(RealizedProductReturnEditPage.class);
	}

	@Override
	public List<Object> load(int first, int pageSize, String sortField,
			SortOrder sortOrder, Map<String, String> filters) {
		return load(first, pageSize, sortField, sortOrder, filters, true );
	}

	public List<Object> load(int first, int pageSize, String sortField,	SortOrder sortOrder, Map<String, String> filters, boolean clearCriteria ) {
		if( clearCriteria ) {
			getBeanDao().clearWhereCriteria();
		}
		if( currentlyShowingReturnStatus != null ) {
			getBeanDao().addWhereCriteria( "bean.realizedProductReturnStatus = " + currentlyShowingReturnStatus.ordinal() );
		}
		List<Object> objects = super.load(first, pageSize, sortField, sortOrder, filters);
		insertCustomerNames(objects);
		return objects;
	}

	public void insertCustomerNames(List<Object> objects) {
		List<String> customerIdList = new ArrayList<String>();
		RealizedProductReturnListBean tempReturn;
		for( Object recordObj : objects ) {
			if( recordObj != null ) {
				tempReturn = (RealizedProductReturnListBean) recordObj;
				if( !CommonUtil.getStringOrEmpty( tempReturn.getReturnAddress().getCompanyName() ).equals( "" ) ) {
					tempReturn.setCustomerOrCompanyName( tempReturn.getReturnAddress().getCompanyName() );
				} else if( tempReturn.determineEndCustomer() != null ) {
					customerIdList.add( String.valueOf( tempReturn.determineEndCustomer().getId() ) );
				} else if( !CommonUtil.getStringOrEmpty( tempReturn.getReturnAddress().getContactFirstName() ).equals( "" ) ) {
					tempReturn.setCustomerOrCompanyName( tempReturn.getReturnAddress().getContactFullName() );
				}
			}
		}

		if( customerIdList.size() > 0 ) {
			BeanDao companyContactDao = new BeanDao( CompanyContact.class );
			companyContactDao.setSelectCriteria( "bean.id, bean.company.companyName" );
			companyContactDao.setWhereCriteria( "bean.id IN ( " + StringUtils.join( customerIdList, "," ) + " )" );
			List<CompanyContact> companyContactList = companyContactDao.getAll();
			for( CompanyContact tempCompanyContact : companyContactList ) {

				for( Object recordObj : objects ) {
					if( recordObj != null ) {
						Customer endCustomer = ((RealizedProductReturn) recordObj).determineEndCustomer();
						if( endCustomer != null && endCustomer.getId().equals( tempCompanyContact.getId() ) ) {
							tempReturn = (RealizedProductReturnListBean) recordObj;
							if( tempReturn.determineEndCustomer() != null ) {
								if( tempReturn.determineEndCustomer().getId().equals( tempCompanyContact.getId() ) ) {
									tempReturn.setCustomerOrCompanyName( tempCompanyContact.getCompany().getCompanyName() );
								} else {
									tempReturn.setCustomerOrCompanyName( tempReturn.determineEndCustomer().getDisplayName() );
								}
							}
						}
					}
				}
			}
		}
	}

	public void setCurrentlyShowingReturnStatus(
			RealizedProductReturnStatus currentlyShowingReturnStatus) {
		this.currentlyShowingReturnStatus = currentlyShowingReturnStatus;
	}

	public RealizedProductReturnStatus getCurrentlyShowingReturnStatus() {
		return currentlyShowingReturnStatus;
	}

}
