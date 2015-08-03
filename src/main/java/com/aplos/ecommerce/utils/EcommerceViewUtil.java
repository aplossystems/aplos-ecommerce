package com.aplos.ecommerce.utils;

import javax.faces.bean.ManagedBean;

import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.product.ProductType;

/* This utility class is to contain those methods or helper methods we need
 * to call directly from a view which are usually in CommonUtil
 * (as the view cannot refer to a static method)
 */

@ManagedBean
public class EcommerceViewUtil {

	public EcommerceViewUtil() {	}

	//we usually supply mapping string or product/type
	public String getActiveClassIfUrlEndsWith(Object identifier) {

		if (identifier instanceof String) {

		} else if (identifier instanceof ProductType) {
			String requestUrl = JSFUtil.getAplosContextOriginalUrl();
			if (requestUrl.contains("?")){
				requestUrl = requestUrl.substring(0, requestUrl.indexOf("?"));
			}
			if (requestUrl.contains( "/" + ((ProductType) identifier).getMappingOrId() + "/") || requestUrl.endsWith( "/" + ((ProductType) identifier).getMappingOrId() + ".aplos")) {
				return "currentPageButton aplos-active";
			}
		}

		return "";
	}

}
