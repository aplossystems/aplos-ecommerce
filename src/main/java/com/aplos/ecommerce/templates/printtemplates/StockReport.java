package com.aplos.ecommerce.templates.printtemplates;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.AplosUrl;
import com.aplos.common.appconstants.AplosAppConstants;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.interfaces.AplosWorkingDirectoryInter;
import com.aplos.common.listeners.AplosContextListener;
import com.aplos.common.templates.PrintTemplate;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.XmlEntityUtil;
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.enums.EcommerceWorkingDirectory;

public class StockReport extends PrintTemplate {

	private static final long serialVersionUID = 7966989828713761594L;
	
	@Override
	public String getName() {
		return "Stock report";
	}
	
	@Override
	public AplosWorkingDirectoryInter getAplosWorkingDirectoryInter() {
		return EcommerceWorkingDirectory.REPORTING_PDFS_DIR;
	}

	@Override
	public String getTemplateContent() {
		AplosContextListener aplosContextListener = ApplicationUtil.getAplosContextListener();
		BeanDao dao = new BeanDao(RealizedProduct.class);
		dao.setSelectCriteria("bean.id, bean.discontinued, bean.quantity, bean.itemCode, bean.price, bean.productInfo.product.name, bean.productInfo.product.itemCode, bean.productCost");
		@SuppressWarnings("unchecked")
		ArrayList<RealizedProduct> realizedProducts = (ArrayList<RealizedProduct>) dao.getAll();
		try {
			BigDecimal stockValueTotal = new BigDecimal(0);
			BigDecimal stockCostTotal = new BigDecimal(0);
			JDynamiTe jDynamiTe;
			if ((jDynamiTe = CommonUtil.loadContentInfoJDynamiTe( "stockReport.html", PrintTemplate.printTemplatePath, aplosContextListener )) != null) {

				jDynamiTe.setVariable("TRUE_AT_DATE", FormatUtil.formatDate(FormatUtil.getEngSlashSimpleDateTimeFormat(), new Date()) );
				Collections.sort( realizedProducts, new Comparator<RealizedProduct>() {
					@Override
					public int compare(RealizedProduct o1, RealizedProduct o2) {
						return o1.determineItemCode().compareTo( o2.determineItemCode() );
					}
				});
				for (RealizedProduct realizedProduct : realizedProducts) {
					int productStockQuantity = realizedProduct.getQuantity();
					if (realizedProduct.getProductInfo() == null || realizedProduct.getProductInfo().getProduct() == null || productStockQuantity == 0 ) {
						continue;
					}
					jDynamiTe.setVariable("PART_NR_REPEATED", realizedProduct.determineItemCode() );
					String name = realizedProduct.getProductInfo().getProduct().getName();
					if (name == null) {
						name = "name unspecified";
					}
					jDynamiTe.setVariable("PART_NAME_REPEATED", XmlEntityUtil.replaceCharactersWith(name, XmlEntityUtil.EncodingType.ENTITY) );
					jDynamiTe.setVariable("DISCONTINUED_REPEATED", (realizedProduct.isDiscontinued())?"Yes":"" );
					jDynamiTe.setVariable("QUANTITY_REPEATED", String.valueOf(productStockQuantity) );
					jDynamiTe.setVariable("LIST_PRICE_REPEATED", FormatUtil.formatTwoDigit(realizedProduct.getDeterminedPrice()) );
					BigDecimal stockValue = realizedProduct.getDeterminedPrice().multiply(new BigDecimal(productStockQuantity));
					jDynamiTe.setVariable("STOCK_VALUE_REPEATED", FormatUtil.formatTwoDigit(stockValue));
					stockValueTotal = stockValueTotal.add(stockValue);
					BigDecimal stockCost;
					if (realizedProduct.getProductCost() != null) {
						stockCost = realizedProduct.getProductCost().multiply(new BigDecimal(productStockQuantity));

					} else {
						stockCost = new BigDecimal(0);
					}
					jDynamiTe.setVariable("STOCK_COST_REPEATED", FormatUtil.formatTwoDigit(stockCost));
					stockCostTotal = stockCostTotal.add(stockCost);
					jDynamiTe.parseDynElem("stock_part");
				}
				jDynamiTe.setVariable("STOCK_VALUE_TOTAL", FormatUtil.formatTwoDigit(stockValueTotal));
				jDynamiTe.setVariable("STOCK_COST_TOTAL", FormatUtil.formatTwoDigit(stockCostTotal));
			}
			jDynamiTe.parse();

			return jDynamiTe.toString();
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
		return null;

	}

	public static String getTemplateUrl() {
		AplosUrl aplosUrl = getBaseTemplateUrl( StockReport.class );
		aplosUrl.addQueryParameter( AplosAppConstants.CREATE_PDF, "true" );
		return aplosUrl.toString();
	}
}
