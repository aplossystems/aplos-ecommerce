package com.aplos.ecommerce.backingpage.product.size;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.ecommerce.beans.DiscountAllowance;
import com.aplos.ecommerce.beans.product.ProductSizeChart;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=DiscountAllowance.class)
public class ProductSizeChartListPage extends ListPage {
	private static final long serialVersionUID = -6997143278223373043L;

}
