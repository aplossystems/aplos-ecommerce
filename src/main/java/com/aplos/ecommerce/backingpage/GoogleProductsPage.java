package com.aplos.ecommerce.backingpage;

import java.util.ArrayList;

import javax.faces.bean.ManagedBean;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import com.aplos.common.LabeledEnumInter;
import com.aplos.common.annotations.GlobalAccess;
import com.aplos.common.annotations.WindowId;
import com.aplos.common.backingpage.BackingPage;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.beans.developercmsmodules.ProductListModule;
import com.aplos.ecommerce.beans.product.ProductType;
import com.aplos.ecommerce.developermodulebacking.frontend.ProductListFilter;
import com.aplos.ecommerce.utils.EcommerceUtil;

@ManagedBean
@GlobalAccess
@WindowId(required=false)
public class GoogleProductsPage extends BackingPage {
	private static final long serialVersionUID = -2874250826522560632L;

	private enum GoogleAvailablity implements LabeledEnumInter {
		IN_STOCK ( "in stock" ),
		AVAILABLE_FOR_ORDER ( "available for order" ),
		OUT_OF_STOCK ( "out of stock" ),
		PRE_ORDER ( "pre order" );

		private String label;

		private GoogleAvailablity( String label ) {
			this.label = label;
		}

		@Override
		public String getLabel() {
			return label;
		}
	}

	public GoogleProductsPage() {

	}

	public String getSiteLink() {
		return JSFUtil.getServerUrl() + JSFUtil.getContextPath();
	}

	public String getItemsXml() {
		StringBuffer itemsStrBuf = new StringBuffer();
		
		ProductListFilter productListFilter = new ProductListFilter();
		ProductListModule productListModule = new ProductListModule();
		productListModule.setProductListRoot( new ProductType() );
		productListFilter.updateIfRequired( productListModule, JSFUtil.getRequest() );
		String serverPath = JSFUtil.getServerUrl() + JSFUtil.getContextPath();
		if( !serverPath.endsWith( "/" ) ) {
			itemsStrBuf.append("/");
		}

		
		for (RealizedProduct rp : new ArrayList<RealizedProduct>( productListFilter.getLoadedRealizedProductMap( true ).values() ) ) {
			itemsStrBuf.append("<item>\n");
				itemsStrBuf.append("<title>");
					itemsStrBuf.append(StringEscapeUtils.escapeXml( rp.getDisplayName() ));
				itemsStrBuf.append("</title>\n");

				itemsStrBuf.append("<link>");
					itemsStrBuf.append(serverPath);
					itemsStrBuf.append(StringEscapeUtils.escapeXml( EcommerceUtil.getEcommerceUtil().getProductMapping(rp,false) ));
				itemsStrBuf.append("</link>\n");

				itemsStrBuf.append("<description>");
				if( !CommonUtil.isNullOrEmpty( rp.getProductInfo().getShortDescription() ) ) {
					itemsStrBuf.append(StringEscapeUtils.escapeXml( StringEscapeUtils.unescapeHtml( Jsoup.clean( rp.getProductInfo().getShortDescription(), Whitelist.none() )) ));
				} else {
					itemsStrBuf.append( StringEscapeUtils.escapeXml( StringEscapeUtils.unescapeHtml( Jsoup.clean( rp.getProductInfo().getLongDescription(), Whitelist.none() ) ) ));
				}
				itemsStrBuf.append("</description>\n");

				itemsStrBuf.append("<g:image_link>");

				if (rp.getDefaultImageDetails() != null) {
					itemsStrBuf.append(StringEscapeUtils.escapeXml( JSFUtil.getServerUrl() + rp.getDefaultImageDetails().getFullFileUrl(true)));
				} else if (rp.getProductInfo().getDefaultRealizedProduct().getDefaultImageDetails() != null){
					itemsStrBuf.append(StringEscapeUtils.escapeXml( JSFUtil.getServerUrl() + rp.getProductInfo().getDefaultRealizedProduct().getDefaultImageDetails().getFullFileUrl(true)));
				}
				itemsStrBuf.append("</g:image_link>\n");

				itemsStrBuf.append("<g:price>");
				itemsStrBuf.append(rp.getDeterminedPrice());
				itemsStrBuf.append("</g:price>\n");

				itemsStrBuf.append("<g:condition>New</g:condition>\n");

				if( rp.getProductInfo().getProduct().getProductBrand() != null ) {
					itemsStrBuf.append("<g:brand>");
					itemsStrBuf.append(StringEscapeUtils.escapeXml( rp.getProductInfo().getProduct().getProductBrand().getName() ));
					itemsStrBuf.append("</g:brand>\n");
				}

				itemsStrBuf.append("<g:availability>");
				if( rp.getQuantity() > 0 ) {
					itemsStrBuf.append(GoogleAvailablity.IN_STOCK.label);
				} else if( rp.getQuantity() == 0 && rp.getStockAvailableFromDate() != null ) {
					itemsStrBuf.append(GoogleAvailablity.AVAILABLE_FOR_ORDER.label);
				} else {
					itemsStrBuf.append(GoogleAvailablity.OUT_OF_STOCK.label);
				}
				itemsStrBuf.append("</g:availability>\n");

				itemsStrBuf.append("<g:id>");
					itemsStrBuf.append(rp.getId());
				itemsStrBuf.append("</g:id>\n");

			itemsStrBuf.append("</item>\n");
		}
		return itemsStrBuf.toString();
	}
}
