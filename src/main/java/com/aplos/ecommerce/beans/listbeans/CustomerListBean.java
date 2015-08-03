package com.aplos.ecommerce.beans.listbeans;

import java.util.ArrayList;
import java.util.List;

import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.utils.CommonUtil;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.beans.Transaction.TransactionStatus;

public class CustomerListBean extends Customer {
	private static final long serialVersionUID = 439682346786611546L;

	private String customerClassType;

	@Transient 
	private long transientOrderCount;
	
	public CustomerListBean() {}

	public void setCustomerClassType(String customerClassType) {
		this.customerClassType = customerClassType;
	}

	public String getCustomerClassType() {
		return customerClassType;
	}
	
	public static void determineOrderCounts( List<Object> CustomerListBeans ) {
		
		CustomerListBean tempCustomerListBean;
		List<Long> customerIdList = new ArrayList<Long>();
		for( Object tempCustomerListBeanObj : CustomerListBeans ) {
			if( tempCustomerListBeanObj != null && tempCustomerListBeanObj instanceof CustomerListBean) {
				tempCustomerListBean = (CustomerListBean) tempCustomerListBeanObj;
				customerIdList.add( tempCustomerListBean.getId() );
			}
		}

		if( customerIdList.size() > 0 ) {
			
			BeanDao transactionDao = new BeanDao( Transaction.class );
			transactionDao.setSelectCriteria( "bean.ecommerceShoppingCart.customer.id, count(bean.id)" );
			transactionDao.addWhereCriteria( "bean.transactionStatus != " + TransactionStatus.INCOMPLETE.ordinal() + " AND bean.ecommerceShoppingCart.customer.id IN (" + CommonUtil.join( customerIdList, "," ) + ")" );
			transactionDao.setGroupBy( "bean.ecommerceShoppingCart.customer.id" );
			
			@SuppressWarnings("unchecked")
			List<Object[]> queryResults = transactionDao.getResultFields();
			
			for( Object tempCustomerListBeanObj : CustomerListBeans ) {
				
				if( tempCustomerListBeanObj != null && tempCustomerListBeanObj instanceof CustomerListBean) {
					
					tempCustomerListBean = (CustomerListBean) tempCustomerListBeanObj;
					
					for( Object[] tempResultsAry : queryResults ) {
						
						if( tempCustomerListBean.getId().equals( tempResultsAry[ 0 ] ) ) {
							tempCustomerListBean.setTransientOrderCount( (Long) tempResultsAry[ 1 ] );
							break;
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
