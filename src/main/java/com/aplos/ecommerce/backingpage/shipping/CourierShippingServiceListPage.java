package com.aplos.ecommerce.backingpage.shipping;

import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.SortOrder;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.couriers.CourierService;
import com.aplos.ecommerce.beans.couriers.ShippingService;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=ShippingService.class)
public class CourierShippingServiceListPage extends ListPage {

	private static final long serialVersionUID = -3485276792113459638L;

	@Override
	public BeanDao getListBeanDao() {
		BeanDao aqlBeanDao = new BeanDao( ShippingService.class );
		aqlBeanDao.addQueryTable( "cs", "cs.shippingServices" );
//		aqlBeanDao.setFromClause( AplosBean.getTableName(  ) + " cs LEFT OUTER JOIN cs.shippingServices bean" );
		return aqlBeanDao;
	}

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new ShippingServiceLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class ShippingServiceLazyDataModel extends AplosLazyDataModel {

		private static final long serialVersionUID = 5368793548086809246L;

		public ShippingServiceLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
		}

		@Override
		public void selectBean() {
			super.selectBean( false );
			ShippingService shippingService = (ShippingService) JSFUtil.getTableBean();
			JSFUtil.redirect( shippingService.getEditPageClass() );
		}

		@Override
		public void goToNew() {
			CourierService courierService = JSFUtil.getBeanFromScope( CourierService.class );

			if( courierService.getShippingServiceClass() != null ) {
				ShippingService shippingService = (ShippingService) CommonUtil.getNewInstance(courierService.getShippingServiceClass(), null);
				shippingService.initService();
				shippingService.addToScope();
				JSFUtil.redirect( shippingService.getEditPageClass() );
			} else {
				JSFUtil.addMessage( "Please select a shipping service class on the Edit page" );
			}
		}

		@Override
		public List<Object> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
			getBeanDao().clearWhereCriteria();
			CourierService courierService = JSFUtil.getBeanFromScope( CourierService.class );
			getBeanDao().addWhereCriteria( "cs.id = " + courierService.getId() );
			return super.load(first, pageSize, sortField, sortOrder, filters);
		}

	}

}
