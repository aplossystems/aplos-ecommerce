package com.aplos.ecommerce.templates.printtemplates;

import java.awt.Color;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.aplos.common.AplosUrl;
import com.aplos.common.appconstants.AplosAppConstants;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.interfaces.AplosWorkingDirectoryInter;
import com.aplos.common.listeners.AplosContextListener;
import com.aplos.common.templates.PrintTemplate;
import com.aplos.ecommerce.beans.SerialNumber;
import com.aplos.ecommerce.beans.product.Product;
import com.aplos.ecommerce.enums.EcommerceWorkingDirectory;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.Barcode39;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

public class PackagingLabelTemplate extends PrintTemplate {
	private static final long serialVersionUID = 2558631218121817008L;
	private String[] serialNumberAry;
	private HttpServletResponse response;
	
	@Override
	public void initialise(Map<String, String[]> params) {
		String joinedSerialNumbers = params.get( "joinedSerialNumbers" )[ 0 ];
		setSerialNumberAry( StringUtils.split( joinedSerialNumbers, "," ) );
		setResponse(response);
	}
	public String getName() {
		return "Packaging Label";
	}
	
	@Override
	public AplosWorkingDirectoryInter getAplosWorkingDirectoryInter() {
		return EcommerceWorkingDirectory.SERIAL_NUMBERS_PDFS_DIR;
	}

	public String getTemplateContent() {
		try {
	        Document document = new Document( new Rectangle( 300, 150 ), 5, 0, 5, 0);
            PdfWriter writer = PdfWriter.getInstance(document, getResponse().getOutputStream());
            document.open();
            PdfContentByte cb = writer.getDirectContent();
            
			for( String serialNumberId : getSerialNumberAry() ) {
				BeanDao aqlBeanDao = new BeanDao(SerialNumber.class);
				aqlBeanDao.addWhereCriteria("bean.id = " + serialNumberId);
				SerialNumber serialNumber = aqlBeanDao.getFirstBeanResult();
	            int idx = 0;

	            Font arialLargeBold = FontFactory.getFont("c:\\windows\\fonts\\arial.ttf", 18, Font.BOLD, Color.black);
	            Font arialBold = FontFactory.getFont("c:\\windows\\fonts\\arial.ttf", 10, Font.BOLD, Color.black);
	            Font arialNormal = FontFactory.getFont("c:\\windows\\fonts\\arial.ttf", 10, Font.NORMAL, Color.black);

	            Font arialSmallNormal = FontFactory.getFont("c:\\windows\\fonts\\arial.ttf", 8, Font.NORMAL, Color.black);
	            Font arialSmallItalic = FontFactory.getFont("c:\\windows\\fonts\\arial.ttf", 8, Font.ITALIC, Color.black);

	         // create a column object
				Phrase myText = new Phrase(serialNumber.getRealizedProduct().determineItemCode(), arialLargeBold);
				ColumnText.showTextAligned(cb, PdfContentByte.ALIGN_LEFT, myText, 8, 130, 0 );

				myText = new Phrase(serialNumber.getRealizedProduct().getProductInfo().getProduct().getName(), arialSmallNormal);
				ColumnText.showTextAligned(cb, PdfContentByte.ALIGN_LEFT, myText, 8, 120, 0 );

				Product product = serialNumber.getRealizedProduct().getProductInfo().getProduct();
				if( product.getProductTypes().size() > 0 ) {
					myText = new Phrase(product.getProductTypes().get( 0 ).getName(), arialSmallNormal);
					ColumnText.showTextAligned(cb, PdfContentByte.ALIGN_LEFT, myText, 8, 111, 0 );
				}

//				myText = new Phrase(serialNumber.getRealizedProduct().getProductInfo().getProduct().getMadeIn(), arialSmallNormal);
//				ColumnText.showTextAligned(cb, PdfContentByte.ALIGN_LEFT, myText, 8, 92, 0 );

				myText = new Phrase("Serial #: ", arialBold);
				ColumnText.showTextAligned(cb, PdfContentByte.ALIGN_LEFT, myText, 8, 50, 0 );

				myText = new Phrase("TSN" + String.valueOf(serialNumber.getId()), arialNormal);
				ColumnText.showTextAligned(cb, PdfContentByte.ALIGN_LEFT, myText, 50, 50, 0 );

				Barcode39 code39 = new Barcode39();

				code39.setCode("TSN" + String.valueOf(serialNumber.getId()));
				code39.setBarHeight(20);
				code39.setFont( null );
				Image image = code39.createImageWithBarcode(cb, null, null);
				image.scalePercent(150);
				image.setAbsolutePosition(8, 60);
				cb.addImage(image);


				FileDetails logoFileDetails = new BeanDao( FileDetails.class ).get( 13243l );
				if( logoFileDetails != null ) {
					image = Image.getInstance(logoFileDetails.getExternalFileUrlByServerUrl());
				}
				image.setAbsolutePosition( 5, 5 );
				cb.addImage( image );

				myText = new Phrase(serialNumber.getRealizedProduct().determineItemCode(), arialLargeBold);
				ColumnText.showTextAligned(cb, PdfContentByte.ALIGN_CENTER, myText, 270, 75, 90 );

				myText = new Phrase(serialNumber.getRealizedProduct().getProductInfo().getProduct().getName(), arialSmallNormal);
				ColumnText.showTextAligned(cb, PdfContentByte.ALIGN_CENTER, myText, 280, 75, 90 );

				myText = new Phrase("TSN" + String.valueOf(serialNumber.getId()), arialSmallNormal);
				ColumnText.showTextAligned(cb, PdfContentByte.ALIGN_CENTER, myText, 290, 75, 90 );

		        document.newPage();
			}
			document.close();
			return null;
		}
		catch( Exception e ) {
			AplosContextListener.getAplosContextListener().handleError(e);
		}
		return null;
	}

	public static String getTemplateUrl( Long serialNumberId ) {
		Long serialNumberIdList[] = new Long[1];
		serialNumberIdList[0] = serialNumberId;
		return getTemplateUrl( serialNumberIdList );
	}

	public static String getTemplateUrl( Long[] serialNumberIdList ) {
		String joinedSerialNumbers = StringUtils.join( serialNumberIdList, "," );
		AplosUrl aplosUrl = getBaseTemplateUrl( PackagingLabelTemplate.class );
		aplosUrl.addQueryParameter( "joinedSerialNumbers", joinedSerialNumbers );
		aplosUrl.addQueryParameter( AplosAppConstants.CREATE_SIZED_PDF, "true" );
		return aplosUrl.toString();
	}
	public String[] getSerialNumberAry() {
		return serialNumberAry;
	}
	public void setSerialNumberAry(String[] serialNumberAry) {
		this.serialNumberAry = serialNumberAry;
	}
	public HttpServletResponse getResponse() {
		return response;
	}
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}
}
