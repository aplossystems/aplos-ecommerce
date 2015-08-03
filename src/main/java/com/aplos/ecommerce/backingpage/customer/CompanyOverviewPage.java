package com.aplos.ecommerce.backingpage.customer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.StringUtils;
import org.primefaces.model.SortOrder;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.utils.CommonUtil;
import com.aplos.ecommerce.beans.Company;
import com.aplos.ecommerce.beans.CompanyContact;
import com.aplos.ecommerce.beans.EcommerceShoppingCartItem;
import com.aplos.ecommerce.beans.RealizedProductReturn;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.beans.listbeans.TransactionListBean;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=Company.class)
public class CompanyOverviewPage extends EditPage {
	private static final long serialVersionUID = 788882713338983441L;

	private AplosLazyDataModel companyContactLdm;
	private AplosLazyDataModel companyReturnLdm;
	private AplosLazyDataModel companyTransactionLdm;

	public CompanyOverviewPage() {
		DataTableState dataTableContactState = CommonUtil.createDataTableState( this, CompanyContactLazyDataModelWrapper.class );
		setCompanyContactLdm(new CompanyContactLazyDataModelWrapper( dataTableContactState, new BeanDao( CompanyContact.class ) ));
		dataTableContactState.setLazyDataModel( getCompanyContactLdm() );

		DataTableState dataTableReturnState = CommonUtil.createDataTableState( this, CompanyReturnLazyDataModelWrapper.class );
		setCompanyReturnLdm(new CompanyReturnLazyDataModelWrapper( dataTableReturnState, new BeanDao( RealizedProductReturn.class ) ));
		dataTableReturnState.setLazyDataModel( getCompanyReturnLdm() );

		DataTableState dataTableState = CommonUtil.createDataTableState( this, CompanyTransactionLazyDataModelWrapper.class );
		setCompanyTransactionLdm(new CompanyTransactionLazyDataModelWrapper( dataTableState, new BeanDao( Transaction.class ) ));
		dataTableState.setLazyDataModel( getCompanyTransactionLdm() );
	}

	@Override
	public DataTableState getDefaultDataTableState(Class<?> parentClass) {
		DataTableState dataTableState = super.getDefaultDataTableState(parentClass);
		if( parentClass.equals( CompanyTransactionLazyDataModelWrapper.class )) {
			dataTableState.setMaxRowsOnPage( 5 );
		}
		return dataTableState;
	}
	
	public String allPhoneNumbers( CompanyContact companyContact ) {
		List<String> companyContactNumbers = new ArrayList<String>();
		if( !CommonUtil.isNullOrEmpty( companyContact.getShippingAddress().getPhone() ) ) {
			companyContactNumbers.add( companyContact.getShippingAddress().getPhone() );
		}
		if( !CommonUtil.isNullOrEmpty( companyContact.getShippingAddress().getPhone2() ) ) {
			companyContactNumbers.add( companyContact.getShippingAddress().getPhone2() );
		}
		if( !CommonUtil.isNullOrEmpty( companyContact.getShippingAddress().getMobile() ) ) {
			companyContactNumbers.add( companyContact.getShippingAddress().getMobile() );
		}
		
		return StringUtils.join( companyContactNumbers, "<br/>" );
	}

	public AplosLazyDataModel getCompanyTransactionLdm() {
		return companyTransactionLdm;
	}

	public AplosLazyDataModel getCompanyReturnLdm() {
		return companyReturnLdm;
	}

	public AplosLazyDataModel getCompanyContactLdm() {
		return companyContactLdm;
	}

	public void setCompanyContactLdm(AplosLazyDataModel companyContactLdm) {
		this.companyContactLdm = companyContactLdm;
	}

	public void setCompanyReturnLdm(AplosLazyDataModel companyReturnLdm) {
		this.companyReturnLdm = companyReturnLdm;
	}

	public void setCompanyTransactionLdm(AplosLazyDataModel companyTransactionLdm) {
		this.companyTransactionLdm = companyTransactionLdm;
	}

	public class CompanyContactLazyDataModelWrapper extends CompanyContactLdm {

		/**
		 *
		 */
		private static final long serialVersionUID = -7929811330570314048L;

		public CompanyContactLazyDataModelWrapper( DataTableState dataTableState, BeanDao aqlBeanDao ) {
			super(dataTableState, aqlBeanDao);
			getBeanDao().setSelectCriteria( "bean.id, s.firstName, s.surname, s.emailAddress, sa.postcode, sa.phone, sa.phone2, sa.mobile, bean.position, bean.customerNotes, bean.active, bean.deletable" );
		}

	}

	public class CompanyReturnLazyDataModelWrapper extends CompanyReturnLdm {

		/**
		 *
		 */
		private static final long serialVersionUID = -8035294290703593773L;

		public CompanyReturnLazyDataModelWrapper( DataTableState dataTableState, BeanDao aqlBeanDao ) {
			super(dataTableState, aqlBeanDao);
			getBeanDao().setSelectCriteria( "bean.id, bean.returnsNotes, rtnpp.name, ec.id, ec.subscriber.firstName, ec.subscriber.surname, ra.contactFirstName, ra.contactSurname, ra.companyName, " +
					"rtnp.itemCode, rtnpp.id, rtnp.id, " +
					"rtnpp.itemCode, bean.dateCreated," +
					"bean.realizedProductReturnStatus, bean.deletable, bean.active" );
		}

	}

	public class CompanyTransactionLazyDataModelWrapper extends CompanyTransactionLdm {

		/**
		 *
		 */
		private static final long serialVersionUID = -249780741427281992L;


		public CompanyTransactionLazyDataModelWrapper( DataTableState dataTableState, BeanDao aqlBeanDao ) {
			super(dataTableState, aqlBeanDao);
			setStartDate( null );
			setEndDate( null );
			getBeanDao().setSelectCriteria( "bean.id, bean.ecommerceShoppingCart.id, bean.ecommerceShoppingCart.customer.id, bean.ecommerceShoppingCart.id, bean.customerReference, bean.transactionNotes, bean.trackingNumber, bean.invoiceNumber, bean.ecommerceShoppingCart.customer.subscriber.firstName, bean.ecommerceShoppingCart.customer.subscriber.surname, bean.ecommerceShoppingCart.customer.subscriber.emailAddress, bean.transactionStatus, bean.transactionType, bean.ecommerceShoppingCart.cachedTotalVatAmount, bean.ecommerceShoppingCart.currencyBaseRate, bean.ecommerceShoppingCart.cachedNetTotalAmount, bean.ecommerceShoppingCart.deliveryCost, bean.ecommerceShoppingCart.cachedWeight, bean.transactionDate, bean.dateCreated, bean.deletable, bean.active" );
		}

		@Override
		public List<Object> load(int first, int pageSize, String sortField,
				SortOrder sortOrder, Map<String, String> filters) {
			List<Object> results = super.load(first, pageSize, sortField, sortOrder, filters);
			loadFirstProducts( results );
			return results;
		}

		public void loadFirstProducts( List<Object> results ) {
			String joinedIdString = getJoinedCartIdStringFromRecordArray(results);
			if( !joinedIdString.equals( "" ) ) {
				BeanDao cartItemDao = new BeanDao( EcommerceShoppingCartItem.class );
				cartItemDao.setSelectCriteria( "bean.shoppingCart.id, bean.realizedProduct.itemCode, bean.realizedProduct.productInfo.product.itemCode, bean.quantity" );
				cartItemDao.addWhereCriteria( "bean.shoppingCart.id IN (" + joinedIdString + ")" );

				List<EcommerceShoppingCartItem> cartItemList = cartItemDao.getAll();
				Map<Long, EcommerceShoppingCartItem> cartItemMap = new HashMap<Long, EcommerceShoppingCartItem>();
				for( EcommerceShoppingCartItem tempCartItem : cartItemList ) {
					cartItemMap.put( tempCartItem.getShoppingCart().getId(), tempCartItem );
				}

				TransactionListBean tempTransactionListBean;
				EcommerceShoppingCartItem tempCartItem;
				for( Object tempTransactionObj : results ) {
					if( tempTransactionObj != null ) {
						tempTransactionListBean = (TransactionListBean) tempTransactionObj;
						tempCartItem = cartItemMap.get( tempTransactionListBean.getEcommerceShoppingCart().getId() );
						if( tempCartItem != null ) {
							tempTransactionListBean.getEcommerceShoppingCart().getItems().add( tempCartItem );
						}
					}
				}
			}
		}


		public String getJoinedCartIdStringFromRecordArray( List<Object> results ) {
			List<String> idList = new ArrayList<String>();
			for( Object tempTransactionListBeanObj : results ) {
				if( tempTransactionListBeanObj != null ) {
					idList.add( String.valueOf( ((TransactionListBean) tempTransactionListBeanObj).getEcommerceShoppingCart().getId() ) );
				}
			}

			return StringUtils.join( idList, "," );
		}
	}
}
