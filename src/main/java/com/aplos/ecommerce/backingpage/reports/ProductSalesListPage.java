package com.aplos.ecommerce.backingpage.reports;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.SortOrder;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.backingpage.product.FullKitStackEditPage;
import com.aplos.ecommerce.backingpage.product.FullProductStackEditPage;
import com.aplos.ecommerce.beans.EcommerceShoppingCartItem;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.beans.Transaction.TransactionStatus;
import com.aplos.ecommerce.beans.listbeans.ProductSalesListBean;
import com.aplos.ecommerce.beans.product.ProductInfo;
import com.aplos.ecommerce.enums.TransactionType;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=Transaction.class)
public class ProductSalesListPage extends ListPage {

	private static final long serialVersionUID = 5966082428778235091L;

	public ProductSalesListPage() {
	}

	@Override
	public DataTableState getOrCreateDataTableState() {
		DataTableState dataTableState = super.getOrCreateDataTableState();
		dataTableState.setShowingDeleteColumn( false );
		dataTableState.setShowingIdColumn( false );
		return dataTableState;
	}

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new ProductSalesLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class ProductSalesLazyDataModel extends AplosLazyDataModel {
		private static final long serialVersionUID = -8248787815350622296L;

		private Date startDate;
		private Date endDate;
		private long salesQuantityTotal;
		private BigDecimal salesAmountTotal;

		public ProductSalesLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
			aqlBeanDao.setListBeanClass( ProductSalesListBean.class );
			StringBuilder selection = new StringBuilder( "bean.id, " );
			selection.append( "item.itemCode, " );
			selection.append( "item.singleItemNetPrice, " );
			selection.append( "item.singleItemDiscountAmount, " );
			selection.append( "item.singleItemBasePrice, " ); // used to group the products into the price they sold for
			selection.append( "rp.price, " ); 
			selection.append( "rp.productCost, " );
			selection.append( "rp.productInfo.id, " );
			selection.append( "rp.productInfo.product, " );
			selection.append( "SUM(item.quantity) as salesQuantity, " );
			selection.append( "SUM(item.cachedNetLinePrice) as salesAmount, " );
			selection.append( "rp.id" );
			aqlBeanDao.setSelectCriteria( selection.toString() );
			
			aqlBeanDao.addQueryTable( "esc", "t.ecommerceShoppingCart" );
			aqlBeanDao.addQueryTable( "item", "esc.items" );
			aqlBeanDao.addQueryTable( "rp", "item.realizedProduct" );
			aqlBeanDao.addQueryTable( "pi", "rp.productInfo" );
			aqlBeanDao.addQueryTable( "pr", "pi.product" );
			aqlBeanDao.addQueryTable( "pb", "pr.productBrand" );
			aqlBeanDao.setGroupBy( "rp.productInfo.id, item.singleItemBasePrice, item.singleItemDiscountAmount" ); //one rp with one sale price per row
//			aqlBeanDao.addAliasesForOptimisation( "rp", "realizedProduct" );
//			aqlBeanDao.addAliasesForOptimisation( "pi", "productInfo" );
//			aqlBeanDao.addAliasesForOptimisation( "pr", "product" );
//			aqlBeanDao.addAliasesForOptimisation( "pb", "productBrand" );
			aqlBeanDao.setOrderBy( "item.itemCode" );
			endDate = new Date();

			Calendar cal = new GregorianCalendar();
			cal.setTime( endDate );
//			cal.add( Calendar.YEAR, -1);
//			cal.add( Calendar.DAY_OF_YEAR, 1);
			cal.add( Calendar.MONTH, -1);
			startDate = cal.getTime();
		}

		private void updateTotals( List<Object> results ) {
			ProductSalesListBean tempProductSalesListBean;
			salesQuantityTotal = 0;
			salesAmountTotal = new BigDecimal( 0 );
			for( Object result : results ) {
				tempProductSalesListBean = (ProductSalesListBean) result;
				salesQuantityTotal += tempProductSalesListBean.getSalesQuantity();
				salesAmountTotal = salesAmountTotal.add( tempProductSalesListBean.getSalesAmount() );
			}
		}

		public void addOverridableWhereCriteria() {
			getBeanDao().addWhereCriteria( "rp != null" );
		}

		@Override
		public List<Object> load(int first, int pageSize, String sortField,
				SortOrder sortOrder, Map<String, String> filters) {
			getBeanDao().clearWhereCriteria();
			getBeanDao().addWhereCriteria("t.transactionStatus != " + TransactionStatus.CANCELLED.ordinal());
			getBeanDao().addWhereCriteria("t.transactionType != " + TransactionType.LOAN.ordinal());
			getBeanDao().addWhereCriteria("t.transactionStatus != " + TransactionStatus.INCOMPLETE.ordinal());
			addOverridableWhereCriteria();
			if( startDate != null ) {
//				getBeanDao().addWhereCriteria("t.firstInvoicedDate >= '" + FormatUtil.formatDateForDB(getStartDate()) + "'");
				getBeanDao().addWhereCriteria("t.dateCreated >= '" + FormatUtil.formatDateForDB(startDate) + "'");
			}

			if( endDate != null ) {
				Calendar cal = new GregorianCalendar();
				cal.setTime( endDate );
				cal.add( Calendar.DAY_OF_MONTH, 1); // as <= doesnt work due to taking note of the time
				Date endDateForDB = cal.getTime();
				getBeanDao().addWhereCriteria("t.dateCreated < '" + FormatUtil.formatDateForDB(endDateForDB) + "'");
//				getBeanDao().addWhereCriteria("t.firstInvoicedDate <= '" + FormatUtil.formatDateForDB(endDateForDB) + "'");
			}
			getBeanDao().addWhereCriteria( "t.invoiceNumber IS NOT NULL" );
			List<Object> results = super.load(first, pageSize, sortField, sortOrder, filters);
			updateTotals( results );
			return results;
		}

		@Override
		public AplosBean selectBean() {
			EcommerceShoppingCartItem ecommerceShoppingCartItem = (EcommerceShoppingCartItem) JSFUtil.getTableBean();
			BeanDao dao = new BeanDao(ProductInfo.class);
			ProductInfo loadedProductInfo = dao.get(ecommerceShoppingCartItem.getRealizedProduct().getProductInfo().getId());
//			HibernateUtil.initialise( loadedProductInfo, true );
			loadedProductInfo.addToScope();
			if (loadedProductInfo.isKitItem()) {
				JSFUtil.redirect(FullKitStackEditPage.class);
			} else {
				JSFUtil.redirect( FullProductStackEditPage.class );
			}
			return ecommerceShoppingCartItem;
		}
		
		public Date getStartDate() {
			return startDate;
		}

		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}

		public Date getEndDate() {
			return endDate;
		}

		public void setEndDate(Date endDate) {
			this.endDate = endDate;
		}

		public String getSalesQuantityTotalStr() {
			return FormatUtil.formatTwoDigit( getSalesQuantityTotal() );
		}

		public long getSalesQuantityTotal() {
			return salesQuantityTotal;
		}

		public void setSalesQuantityTotal(long salesQuantityTotal) {
			this.salesQuantityTotal = salesQuantityTotal;
		}

		public String getSalesAmountTotalStr() {
			return FormatUtil.formatTwoDigit( getSalesAmountTotal() );
		}

		public BigDecimal getSalesAmountTotal() {
			return salesAmountTotal;
		}

		public void setSalesAmountTotal(BigDecimal salesAmountTotal) {
			this.salesAmountTotal = salesAmountTotal;
		}

	}
}
