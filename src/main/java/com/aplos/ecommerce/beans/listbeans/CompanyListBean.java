package com.aplos.ecommerce.beans.listbeans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.Address;
import com.aplos.common.utils.CommonUtil;
import com.aplos.ecommerce.beans.Company;
import com.aplos.ecommerce.beans.CompanyContact;
import com.aplos.ecommerce.beans.Transaction;

public class CompanyListBean extends Company {
	private static final long serialVersionUID = 6822179393777129109L;

	private Address determinedAddress;

	@Transient 
	private long transientOrderCount = 0;
	
	public CompanyListBean() {}

	public CompanyListBean(Long id, String name) {
		this.setId(id);
		this.setCompanyName(name);
	}

	public Address getDeterminedAddress() {
		return determinedAddress;
	}

	public void setDeterminedAddress(Address determinedAddress) {
		this.determinedAddress = determinedAddress;
	}
	
	public static void determineOrderCounts( List<Object> companyListBeans ) {
		
		CompanyListBean tempCompanyListBean;
		List<Long> companyIdList = new ArrayList<Long>();
		for( Object tempCompanyListBeanObj : companyListBeans ) {
			if( tempCompanyListBeanObj != null && tempCompanyListBeanObj instanceof CompanyListBean) {
				tempCompanyListBean = (CompanyListBean) tempCompanyListBeanObj;
				companyIdList.add( tempCompanyListBean.getId() );
			}
		}

		if( companyIdList.size() > 0 ) {
			
			BeanDao contactDao = new BeanDao(CompanyContact.class);
			contactDao	.setSelectCriteria("bean.company.id, bean.id");
			contactDao.setWhereCriteria("bean.company.id IN (" + CommonUtil.join( companyIdList, "," ) + ")");
			List<Long[]> companyContactIds = contactDao.getAll();
			
			if (companyContactIds.size() > 0) {
				
				Map<Long, List<Long>> companyIdToContactIdsMap = new HashMap<Long,List<Long>>();
				List<Long> contactIds = new ArrayList<Long>();
				for (Object[] idPair : companyContactIds) {
					if (companyIdToContactIdsMap.get(idPair[0]) == null) {
						companyIdToContactIdsMap.put((Long)idPair[0], new ArrayList<Long>());
					} 
					companyIdToContactIdsMap.get(idPair[0]).add((Long)idPair[1]);
					contactIds.add((Long)idPair[1]);
				}
				
				BeanDao transactionDao = new BeanDao( Transaction.class );
				transactionDao.setSelectCriteria( "bean.ecommerceShoppingCart.customer.id, count(bean.id)" );
				transactionDao.addWhereCriteria( "bean.ecommerceShoppingCart.customer.id IN (" + CommonUtil.join( contactIds, "," ) + ")" );
				transactionDao.setGroupBy( "bean.ecommerceShoppingCart.customer.id" );
				
				@SuppressWarnings("unchecked")
				List<Object[]> queryResults = transactionDao.getAll();
				
				for( Object tempCustomerListBeanObj : companyListBeans ) {
					
					if( tempCustomerListBeanObj != null && tempCustomerListBeanObj instanceof CompanyListBean) {
						
						tempCompanyListBean = (CompanyListBean) tempCustomerListBeanObj;
						List<Long> tempContactIds = companyIdToContactIdsMap.get(tempCompanyListBean.getId());
						
						if (tempContactIds != null) {
							for( Object[] tempResultsAry : queryResults ) {
								
								if( tempContactIds.contains( tempResultsAry[ 0 ] ) ) {
									tempCompanyListBean.setTransientOrderCount( tempCompanyListBean.getTransientOrderCount() + (Long) tempResultsAry[ 1 ] );
								}
								
							}
						}
					}
					
				}
				
			}
			
		}
		
	}

	public long getTransientOrderCount() {
		return transientOrderCount;
	}

	public void setTransientOrderCount(long transientOrderCount) {
		this.transientOrderCount = transientOrderCount;
	}

}
