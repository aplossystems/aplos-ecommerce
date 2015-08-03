package com.aplos.ecommerce.beans.listbeans;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.ecommerce.beans.CompanyContact;
import com.aplos.ecommerce.beans.Transaction;

public class TransactionListBean extends Transaction {
	private static final long serialVersionUID = 439682346786611546L;

	private String customerCompanyName;
	@Transient 
	private long transientOrderCount;
	@Transient 
	private long transientPreviousOrderCount; //use this to determine if a customer is new

	public TransactionListBean() {}
	
	public boolean isCustomerNew() {
		//you must call determinePreviousOrderCounts from the LDM before using this method
		return transientPreviousOrderCount == 0;
	}

	public static void determineCompanyNames( List<Object> transactionListBeans ) {
		TransactionListBean tempTransactionListBean;
		List<String> customerIdList = new ArrayList<String>();
		for( Object tempTransactionListBeanObj : transactionListBeans ) {
			if( tempTransactionListBeanObj != null && tempTransactionListBeanObj instanceof TransactionListBean) {
				tempTransactionListBean = (TransactionListBean) tempTransactionListBeanObj;
				customerIdList.add( String.valueOf( tempTransactionListBean.getEcommerceShoppingCart().getCustomer().getId() ) );
			}
		}

		if( customerIdList.size() > 0 ) {
			BeanDao companyContactDao = new BeanDao( CompanyContact.class );
			companyContactDao.setSelectCriteria( "bean.id, bean.company.companyName" );
			companyContactDao.addWhereCriteria( "bean.id IN (" + StringUtils.join( customerIdList, "," ) + ")" );
			@SuppressWarnings("unchecked")
			List<Object[]> queryResults = companyContactDao.getResultFields();

			for( Object tempTransactionListBeanObj : transactionListBeans ) {
				if( tempTransactionListBeanObj != null && tempTransactionListBeanObj instanceof TransactionListBean) {
					tempTransactionListBean = (TransactionListBean) tempTransactionListBeanObj;
					for( Object[] tempResultsAry : queryResults ) {
						if( tempTransactionListBean.getEcommerceShoppingCart().getCustomer().getId().equals( tempResultsAry[ 0 ] ) ) {
							tempTransactionListBean.setCustomerCompanyName( (String) tempResultsAry[ 1 ] );
							break;
						}
					}
				}
			}
		}
	}
	
	public static void determinePreviousOrderCounts( List<Object> transactionListBeans ) {
		TransactionListBean tempTransactionListBean;
		List<Long> customerIdList = new ArrayList<Long>();
		List<Long> transactionIdList = new ArrayList<Long>();
		for( Object tempTransactionListBeanObj : transactionListBeans ) {
			if( tempTransactionListBeanObj != null && tempTransactionListBeanObj instanceof TransactionListBean) {
				tempTransactionListBean = (TransactionListBean) tempTransactionListBeanObj;
				customerIdList.add( tempTransactionListBean.getEcommerceShoppingCart().getCustomer().getId() );
				transactionIdList.add( tempTransactionListBean.getId() );
			}
		}

		if( customerIdList.size() > 0 ) {
			
			BeanDao previousTransactionDao = new BeanDao( Transaction.class );
			previousTransactionDao.setSelectCriteria( "bean.ecommerceShoppingCart.customer.id, count(bean.id)" );
			previousTransactionDao.addWhereCriteria( "bean.transactionStatus != " + TransactionStatus.INCOMPLETE.ordinal() + " AND bean.ecommerceShoppingCart.customer.id IN (" + CommonUtil.join( customerIdList, "," ) + ")" );
			previousTransactionDao.setGroupBy( "bean.ecommerceShoppingCart.customer.id" );
			
			@SuppressWarnings("unchecked")
			List<Object[]> queryResults = previousTransactionDao.getResultFields();
			
			previousTransactionDao = new BeanDao( Transaction.class );
			previousTransactionDao.setSelectCriteria( "bean.id, count(sub.id)" );
			previousTransactionDao.addQueryTable( "sub", ApplicationUtil.getPersistentClass(Transaction.class), "sub.id", "bean.id" );
			previousTransactionDao.addWhereCriteria( "sub.active=1 AND bean.id IN (" + CommonUtil.join( transactionIdList, "," ) + " )" );
			previousTransactionDao.addWhereCriteria( "sub.ecommerceShoppingCart.customer.id = bean.ecommerceShoppingCart.customer.id" );
			previousTransactionDao.addWhereCriteria( "sub.dateCreated <= bean.dateCreated AND sub.transactionStatus != " + TransactionStatus.INCOMPLETE.ordinal() );
			//stops some duplicates, but causes others ...
			previousTransactionDao.addWhereCriteria( "sub.id <= bean.id" ); 
//			hqlBuf.append( " AND bean.ecommerceShoppingCart.customer.id IN (" );
//			hqlBuf.append( CommonUtil.join( customerIdList, "," ) + ")" );
			previousTransactionDao.setGroupBy( "bean.id");

			@SuppressWarnings("unchecked")
			List<Object[]> queryPreviousResults = previousTransactionDao.getResultFields();

			for( Object tempTransactionListBeanObj : transactionListBeans ) {
				
				if( tempTransactionListBeanObj != null && tempTransactionListBeanObj instanceof TransactionListBean) {
					
					tempTransactionListBean = (TransactionListBean) tempTransactionListBeanObj;
					
					for( Object[] tempResultsAry : queryResults ) {
						
						if( tempTransactionListBean.getEcommerceShoppingCart().getCustomer().getId().equals( tempResultsAry[ 0 ] ) ) {
							tempTransactionListBean.setTransientOrderCount( (Long) tempResultsAry[ 1 ] );
							break;
						}
						
					}
					
					for( Object[] tempPrevResultsAry : queryPreviousResults ) {
						
						if( tempTransactionListBean.getId().equals( tempPrevResultsAry[ 0 ] ) ) {
							tempTransactionListBean.setTransientPreviousOrderCount( (Long) tempPrevResultsAry[ 1 ] );
							break;
						}
						
					}
					
				}
				
			}
			
		}
	}

	public void setCustomerCompanyName(String customerCompanyName) {
		this.customerCompanyName = customerCompanyName;
	}

	public String getCustomerCompanyName() {
		return customerCompanyName;
	}


	public long getTransientOrderCount() {
		return transientOrderCount;
	}


	public void setTransientOrderCount(long transientOrderCount) {
		this.transientOrderCount = transientOrderCount;
	}


	public long getTransientPreviousOrderCount() {
		return transientPreviousOrderCount;
	}


	public void setTransientPreviousOrderCount(long transientPreviousOrderCount) {
		this.transientPreviousOrderCount = transientPreviousOrderCount;
	}
	
}
