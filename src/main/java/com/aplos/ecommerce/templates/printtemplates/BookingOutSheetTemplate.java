package com.aplos.ecommerce.templates.printtemplates;

import java.util.Map;

import cb.jdynamite.JDynamiTe;

import com.aplos.common.AplosUrl;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.appconstants.AplosScopedBindings;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.interfaces.AplosWorkingDirectoryInter;
import com.aplos.common.templates.PrintTemplate;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.ecommerce.beans.EcommerceShoppingCartItem;
import com.aplos.ecommerce.beans.Transaction;
import com.aplos.ecommerce.enums.EcommerceWorkingDirectory;

@Entity
public class BookingOutSheetTemplate extends PrintTemplate {
	private static final long serialVersionUID = 6313950036074070805L;
	@ManyToOne
	private Transaction transaction;
	
	@Override
	public void initialise(Map<String, String[]> params) {
		setTransaction( (Transaction) new BeanDao( Transaction.class ).get( Long.parseLong( params.get( AplosScopedBindings.ID )[ 0 ] ) ) );
	}
	
	public String getName() {
		return "Booking out sheet";
	}
	
	@Override
	public AplosWorkingDirectoryInter getAplosWorkingDirectoryInter() {
		return EcommerceWorkingDirectory.TRANSACTION_PDFS_DIR;
	}

	public String getTemplateContent() {
		try {
			JDynamiTe dynamiTe;

			if ((dynamiTe = CommonUtil.loadContentInfoJDynamiTe( "bookingOutSheet.html", PrintTemplate.printTemplatePath, ApplicationUtil.getAplosContextListener() )) != null) {

				StringBuffer tableContentStringBuffer = new StringBuffer();

				EcommerceShoppingCartItem ecommerceShoppingCartItem;
				int i, n=transaction.getEcommerceShoppingCart().getItems().size(), lineNumber=1;
				for (i=0 ; i<n ; i++) {
					ecommerceShoppingCartItem = (EcommerceShoppingCartItem)transaction.getEcommerceShoppingCart().getItems().get(i);
					for (int j=0 ; j<ecommerceShoppingCartItem.getQuantity() ; j++, lineNumber++) {
						tableContentStringBuffer.append("<tr>");
						tableContentStringBuffer.append("<td class='lineNumberTD'>" + lineNumber + "</td>");
						if (ecommerceShoppingCartItem.getSerialNumberAssignments().size() != 0) { // check necessary for parts and labour
							tableContentStringBuffer.append("<td>" + ecommerceShoppingCartItem.getSerialNumberAssignments().get(j).getId() + "</td>");
						}
						else {
							tableContentStringBuffer.append("<td></td>");
						}
						tableContentStringBuffer.append("<td>" + ecommerceShoppingCartItem.getRealizedProduct().determineItemCode() + "</td>");
						tableContentStringBuffer.append("<td />");
						tableContentStringBuffer.append("<td />");
						tableContentStringBuffer.append("<td />");
						tableContentStringBuffer.append("<td />");
						tableContentStringBuffer.append("<td />");
						tableContentStringBuffer.append("</tr>");
					}
				}

				for ( ; lineNumber<=30 ; lineNumber++) {
					tableContentStringBuffer.append("<tr>");
					tableContentStringBuffer.append("<td class='lineNumberTD'>");
					tableContentStringBuffer.append(lineNumber);
					tableContentStringBuffer.append("</td>");
					tableContentStringBuffer.append("<td />");
					tableContentStringBuffer.append("<td />");
					tableContentStringBuffer.append("<td />");
					tableContentStringBuffer.append("<td />");
					tableContentStringBuffer.append("<td />");
					tableContentStringBuffer.append("<td />");
					tableContentStringBuffer.append("<td />");
					tableContentStringBuffer.append("</tr>");
				}

				dynamiTe.setVariable("TABLE_CONTENT", tableContentStringBuffer.toString());
				dynamiTe.parse();

				try {
					return dynamiTe.toString();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch( Exception e ) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getTemplateUrl( Transaction transaction ) {
		AplosUrl aplosUrl = getBaseTemplateUrl( BookingOutSheetTemplate.class );
		aplosUrl.addQueryParameter( "id=", transaction );
		return aplosUrl.toString();
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

}
