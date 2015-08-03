package com.aplos.ecommerce.backingpage.realizedProductReturn;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.beans.communication.BulkMessageSourceGroup;
import com.aplos.common.utils.CommonUtil;
import com.aplos.ecommerce.beans.RealizedProductReturn;
import com.aplos.ecommerce.beans.RealizedProductReturn.RealizedProductReturnStatus;

@ManagedBean
@SessionScoped
@AssociatedBean(beanClass=RealizedProductReturn.class)
public class RealizedProductReturnListPage extends ListPage {
	private static final long serialVersionUID = 4329093305867059004L;

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new ListRealizedProductReturnLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class ListRealizedProductReturnLazyDataModel extends RealizedProductReturnLazyDataModel {

		private static final long serialVersionUID = 6677548417260254962L;

		public ListRealizedProductReturnLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
		}

		@Override
		public String getSearchCriteria() {
			return "serialNumber.realizedProduct.productInfo.product.name LIKE :similarSearchText OR bean.serialNumber.id LIKE :similarSearchText";
		}

	}

	public List<SelectItem> getReturnStatusSelectItemBeans() {
		return CommonUtil.getEnumSelectItems( RealizedProductReturnStatus.class, "All" );
	}

}
