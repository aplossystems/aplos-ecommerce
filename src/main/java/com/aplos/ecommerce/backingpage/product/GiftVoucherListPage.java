package com.aplos.ecommerce.backingpage.product;

import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.SortOrder;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.beans.product.Product;
import com.aplos.ecommerce.beans.product.ProductInfo;
import com.aplos.ecommerce.beans.product.ProductType;
import com.aplos.ecommerce.module.EcommerceConfiguration;

@ManagedBean
@ViewScoped
public class GiftVoucherListPage extends FullProductStackListPage {

	private static final long serialVersionUID = 2584110148040783215L;

	@Override
	public BeanDao getListBeanDao() {
		return new BeanDao( RealizedProduct.class );
	}

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new GiftVoucherLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class GiftVoucherLazyDataModel extends AplosLazyDataModel {

		private static final long serialVersionUID = -5735612507922636760L;

		public GiftVoucherLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
		}

		@Override
		public void goToNew() {
			ProductInfo productInfo = new ProductInfo().<ProductInfo>initialiseNewBean();
			productInfo.setProduct(new Product().<Product>initialiseNewBean());
			ProductType loadedGiftVoucherType = new BeanDao(ProductType.class).get(EcommerceConfiguration.getEcommerceConfiguration().getGiftVoucherProductType().getId());
//			HibernateUtil.initialise( loadedGiftVoucherType, true );
			productInfo.getProduct().addProductType(loadedGiftVoucherType);
			productInfo.addToScope();
			JSFUtil.redirect( determineEditPageClass( productInfo ) );
		}

		@Override
		public List<Object> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
			getBeanDao().setSelectCriteria( "bean.id, bean.price, bean.productInfo.id, bean.productInfo.product.id, bean.active" );
			getBeanDao().addQueryTable( "ptype", "bean.productInfo.product.productTypes" );
			getBeanDao().setWhereCriteria("ptype.id=" + EcommerceConfiguration.getEcommerceConfiguration().getGiftVoucherProductType().getId());
			getBeanDao().setGroupBy("bean.id");
			setEditPageClass( GiftVoucherEditPage.class );
			return super.load(first, pageSize, sortField, sortOrder, filters);
		}

		@Override
		public void selectBean() {
			RealizedProduct realizedProduct = (RealizedProduct) JSFUtil.getRequest().getAttribute( "tableBean" );
			BeanDao dao = new BeanDao(ProductInfo.class);
			ProductInfo loadedProductInfo = dao.get(realizedProduct.getProductInfo().getId());
//			HibernateUtil.initialise( loadedProductInfo, true );
			loadedProductInfo.addToScope();
			JSFUtil.redirect( determineEditPageClass( realizedProduct) );
		}

	}

}
