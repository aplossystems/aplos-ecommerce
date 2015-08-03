package com.aplos.ecommerce.backingpage.product.amazon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.UploadedFile;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.amazon.AmazonSize;
import com.aplos.ecommerce.enums.AmazonProductType;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=AmazonSize.class)
public class AmazonSizeListPage extends ListPage  {
	private static final long serialVersionUID = 1990051192505629768L;
	
	private static final int SUBCATEGORY = 0;
	private static final int GERMANY_SIZE = 1;
	private static final int UK_SIZE = 2;
	private static final int FRANCE_SIZE = 3;
	private static final int PRODUCT_TYPE = 4;
	private static final int GENDER = 5;
	
	private UploadedFile uploadedFile;

	public void importAmazonSizes() {
		if( CommonUtil.isFileUploaded( getUploadedFile() ) ) {
			if( getUploadedFile().getFileName().endsWith( ".csv" ) ) {
				try { 
					InputStream inputStream = getUploadedFile().getInputstream();
					BufferedReader reader = new BufferedReader( new InputStreamReader( inputStream ) );
					String line = reader.readLine();
					boolean amazonSizeLinesStarted = false;
					
					BeanDao amazonSizeDao = new BeanDao( AmazonSize.class );
					amazonSizeDao.setIsReturningActiveBeans(null);
					amazonSizeDao.addWhereCriteria( "bean.amazonProductType = :amazonProductType" );
					amazonSizeDao.addWhereCriteria( "bean.name = :sizeName" );
					
					AmazonSize tempAmazonSize;
					String tempProductTypeName;
					String[] tempProductTypeNames;
					String tempSizeName;
					AmazonProductType tempAmazonProductType;
					Map<String,AmazonProductType> amazonProductTypeMap = new HashMap<String,AmazonProductType>();
					AmazonProductType amazonProductTypeValues[] = AmazonProductType.values();
					for( int i = 0, n = amazonProductTypeValues.length; i < n; i++ ) {
						amazonProductTypeMap.put( amazonProductTypeValues[ i ].name(), amazonProductTypeValues[ i ] );
					}
					while( line != null ) {
						if( amazonSizeLinesStarted ) {
							String[] sizeLineParts = new String[ 6 ];
							String[] availableParts = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
							for( int i = 0, n = availableParts.length; i < n; i++ ) {
								sizeLineParts[ i ] = FormatUtil.removeQuotesFromCsvString( availableParts[ i ] );	
							}
	
							tempProductTypeName = sizeLineParts[ PRODUCT_TYPE ];
							tempSizeName = sizeLineParts[ UK_SIZE ];
							
							if( tempProductTypeName.contains( "," ) ) {
								tempProductTypeNames = tempProductTypeName.split(",");
							} else {
								tempProductTypeNames = new String[]{ tempProductTypeName };
							}
							
							for( int i = 0, n = tempProductTypeNames.length; i < n; i++ ) {
								tempAmazonProductType = amazonProductTypeMap.get( tempProductTypeNames[ i ].trim() );
	
								if( tempAmazonProductType != null
										&& !CommonUtil.isNullOrEmpty( tempSizeName ) ) {
									amazonSizeDao.setNamedParameter( "amazonProductType", String.valueOf( tempAmazonProductType.ordinal() ) );
									amazonSizeDao.setNamedParameter( "sizeName", tempSizeName );
									tempAmazonSize = amazonSizeDao.getFirstBeanResult();
									if( tempAmazonSize == null ) {
										try {
											tempAmazonSize = new AmazonSize();
											tempAmazonSize.setAmazonProductType( tempAmazonProductType );
											tempAmazonSize.setName( tempSizeName );
											BeanDao aqlBeanDao = new BeanDao( AmazonSize.class );
											aqlBeanDao.setSelectCriteria( "MAX(positionIdx)" );
											aqlBeanDao.addWhereCriteria( "amazonProductType = " + tempAmazonProductType.ordinal() );
											Object maxPositionIdx = aqlBeanDao.getFirstResult(); 
											if( maxPositionIdx == null ) {
												tempAmazonSize.setPositionIdx( 0 );
											} else {
												tempAmazonSize.setPositionIdx( ((Integer) maxPositionIdx) + 1 );
											}
											tempAmazonSize.saveDetails();	
										} catch( NumberFormatException nfEx ) {
											ApplicationUtil.handleError( nfEx );
											break;
										}
									}
								} else {
									JSFUtil.addMessage( "Amazon size not matched: " + tempProductTypeNames[ i ] + " " + tempSizeName );
								}
							}
						} else {
							if( line.startsWith( "Filter  on" ) ) {
								amazonSizeLinesStarted = true;
							}
						}
						line = reader.readLine();
					}
					reader.close();
					inputStream.close();
	
					if( !amazonSizeLinesStarted ) {
						JSFUtil.addMessageForWarning( "This does not appear to be a valid file" );
					}
				} catch( IOException ioex ) {
					ApplicationUtil.getAplosContextListener().handleError( ioex );
				}
			} else {
				JSFUtil.addMessageForWarning( "This file is not in CSV format.  If it is an Excel please open it and then go to Save As - Other formats.  A dialog will open where you should select CSV (MS-DOS) from the dropdown.  You can then click to save the file in the correct format." );
			}
		}
	} 
	
	@Override
	public AplosLazyDataModel getAplosLazyDataModel(
			DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new AmazonSizeLdm(dataTableState, aqlBeanDao);
	}
	
	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public class AmazonSizeLdm extends AplosLazyDataModel {
		public AmazonSizeLdm(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
		}
		
		@Override
		public void goToNew() {
			super.goToNew();
			AmazonSize amazonSize = getAssociatedBeanFromScope();
			amazonSize.setAmazonProductType( AmazonProductType.SHIRT );
		}
	}
}
