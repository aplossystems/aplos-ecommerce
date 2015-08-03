package com.aplos.ecommerce.backingpage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.log4j.Logger;
import org.primefaces.model.UploadedFile;

import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.enums.DocumentType;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FileIoUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.enums.EcommerceWorkingDirectory;
import com.aplos.ecommerce.module.EcommerceConfiguration;

@ManagedBean
@ViewScoped
public class BarcodeCreatorPage extends EditPage {
	private static final long serialVersionUID = 7871773086844177358L;
	private static Logger logger = Logger.getLogger( BarcodeCreatorPage.class );
	private UploadedFile uploadedFile;
	List<BarcodeLine> barcodeLines = new ArrayList<BarcodeLine>();
	private Map<String,BarcodeLine> barcodeLineMap = new HashMap<String,BarcodeLine>();
	private int firstEmptyLine = -1;
	private StringBuffer headerStrBuf;

	public BarcodeCreatorPage() {
	}
	
	public void readFile() {
		if( CommonUtil.isFileUploaded( getUploadedFile() ) ) {
			try {
				InputStream inputStream = getUploadedFile().getInputstream();
				BufferedReader reader = new BufferedReader( new InputStreamReader( inputStream ) );
				String line = reader.readLine();
				boolean barcodesStarted = false;
				BarcodeLine tempBarcodeLine;
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yyyy hh:mm:SS" );
				firstEmptyLine = 0;
				headerStrBuf = new StringBuffer();
				Date lastUpdated = EcommerceConfiguration.getEcommerceSettingsStatic().getPlayComLastUpdated();
				Date newLastUpdated = null;
				Date tempDate;
				RealizedProduct tempRealizedProduct;
				boolean dateRequiresUpdate;
				String tempDescription;

				List<RealizedProduct> fullProductList = new BeanDao( RealizedProduct.class ).getAll();
				Map<String, RealizedProduct> fullProductMap = new HashMap<String,RealizedProduct>();
				Map<String, List<RealizedProduct>> oldFullProductMap = new HashMap<String,List<RealizedProduct>>();
				Map<String, RealizedProduct> oldBarcodeProductMap = new HashMap<String,RealizedProduct>();
				List<RealizedProduct> tempRealizedProductList;
				String tempOldProductDescription;
				for( int i = 0, n = fullProductList.size(); i < n; i++ ) {
					fullProductMap.put( getProductDescription( fullProductList.get( i ) ), fullProductList.get( i ) );
					
					tempOldProductDescription = getOldProductDescription(fullProductList.get( i ));
					tempRealizedProductList = oldFullProductMap.get( tempOldProductDescription );
					if( tempRealizedProductList == null ) {
						tempRealizedProductList = new ArrayList<RealizedProduct>();
						oldFullProductMap.put( tempOldProductDescription, tempRealizedProductList );
					}
					tempRealizedProductList.add( fullProductList.get( i ) );
					
					if( fullProductList.get( i ).getBarcode() != null ) {
						oldBarcodeProductMap.put( fullProductList.get( i ).getBarcode(), fullProductList.get( i ) );
					}
				}
				while( line != null ) {
					if( barcodesStarted ) {
						String[] barcodeLineParts = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)",3);
						tempBarcodeLine = new BarcodeLine( barcodeLineParts[ 0 ], FormatUtil.removeQuotesFromCsvString( barcodeLineParts[ 1 ] ), barcodeLineParts[ 2 ] );
						barcodeLines.add( tempBarcodeLine );
						barcodeLineMap.put( tempBarcodeLine.getBarcode(), tempBarcodeLine );
						
						if( !CommonUtil.isNullOrEmpty( barcodeLineParts[ 1 ] ) ) {
							firstEmptyLine++;
							dateRequiresUpdate = false;
							if( lastUpdated == null ) {
								dateRequiresUpdate = true;
							} else {
								try {
									tempDate = simpleDateFormat.parse( tempBarcodeLine.getUpdated() );
									if( lastUpdated.before( tempDate ) ) {
										dateRequiresUpdate = true;
										if( newLastUpdated == null || newLastUpdated.before( tempDate ) ) {
											newLastUpdated = tempDate;
										}
									}
								} catch (ParseException e) {
									ApplicationUtil.getAplosContextListener().handleError( e );
								}
							}
							
							
							if( dateRequiresUpdate && !CommonUtil.isNullOrEmpty( tempBarcodeLine.getDescription() ) ) {
								tempDescription = FormatUtil.removeQuotesFromCsvString( tempBarcodeLine.getDescription() );
								tempRealizedProduct = fullProductMap.get( FormatUtil.removeQuotesFromCsvString( tempBarcodeLine.getDescription() ) );
								if( tempRealizedProduct == null ) {
									tempRealizedProduct = oldBarcodeProductMap.get( tempBarcodeLine.getBarcode() );	
								}
								if( tempRealizedProduct == null ) {
									List<RealizedProduct> realizedProductList = oldFullProductMap.get( tempDescription );
									
									if( realizedProductList != null ) {
										for( int i = 0, n = realizedProductList.size(); i < n; i++ ) {
											if( tempBarcodeLine.getBarcode().equals( realizedProductList.get( i ).getBarcode() ) ) {
												tempRealizedProduct = realizedProductList.get( i ); 
											}
										}
										if( tempRealizedProduct == null ) {
											for( int i = 0, n = realizedProductList.size(); i < n; i++ ) {
												if( realizedProductList.get( i ).getBarcode() == null ) {
													tempRealizedProduct = realizedProductList.get( i );
													break;
												}
											}
										}
									}
								}
								
								if( tempRealizedProduct != null ) {
									if( tempRealizedProduct.getBarcode() == null ) {
										tempRealizedProduct.setBarcode( tempBarcodeLine.getBarcode() );
										tempRealizedProduct.saveDetails();	
									}
								} else {
									logger.info( "Couldn't find the product with description " + tempBarcodeLine.getDescription() + " and barcode " + tempBarcodeLine.getBarcode() );
								}
							}
						}
					} else {
						if( line.startsWith( "Number," ) ) {
							barcodesStarted = true;
						}
						headerStrBuf.append( line + "\n" );
					}
					line = reader.readLine();
				}
				reader.close();
				inputStream.close();
			} catch( IOException ioex ) {
				ApplicationUtil.getAplosContextListener().handleError( ioex );
			}
		}
	}
	
	public String getOldProductDescription( RealizedProduct realizedProduct ) {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append( realizedProduct.getProductInfo().getProduct().getName() ).append( " " )
		.append( realizedProduct.getProductSize().getName() ).append( " " )
		.append( realizedProduct.getProductColour().getName() );
		return strBuf.toString();
	}
	
	public String getProductDescription( RealizedProduct realizedProduct ) {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append( realizedProduct.getProductInfo().getProduct().getName() ).append( " " )
		.append( realizedProduct.getProductSize().getName() ).append( " " );
		if( realizedProduct.getProductSizeCategory() != null ) {
			strBuf.append( realizedProduct.getProductSizeCategory().getName() ).append( " " );
		}
		strBuf.append( realizedProduct.getProductColour().getName() );
		return strBuf.toString();
	}
	
	public void exportCSV() {
		if( firstEmptyLine > -1 ) {
			BeanDao realizedProductDao = new BeanDao( RealizedProduct.class );
			realizedProductDao.setSelectCriteria( "bean.id, bean.barcode, bean.productColour.name, bean.productSize.name, bean.productInfo.product.name, bean.productSizeCategory" );
			List<RealizedProduct> realizedProductList = realizedProductDao.getAll();
			BarcodeLine tempBarcodeLine;
			RealizedProduct tempRealizedProduct;
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			for( int i = 0, n = realizedProductList.size(); i < n; i++ ) {
				tempRealizedProduct = realizedProductList.get( i );
				if( CommonUtil.isNullOrEmpty( tempRealizedProduct.getBarcode() ) ) {
					tempBarcodeLine = barcodeLines.get( firstEmptyLine++ );
					tempBarcodeLine.setDescription( getProductDescription( tempRealizedProduct ) ); 
					tempBarcodeLine.setUpdated( dateFormat.format( new Date() ) );
					ApplicationUtil.executeSql( "UPDATE RealizedProduct SET barcode = '" + tempBarcodeLine.getBarcode() + "' WHERE id = " + tempRealizedProduct.getId() );
				} else {
					tempBarcodeLine = barcodeLineMap.get( tempRealizedProduct.getBarcode() );
					tempBarcodeLine.setDescription( getProductDescription( tempRealizedProduct ) );
				}
			}
			
	
			Date lastUpdated = new Date();
			EcommerceConfiguration.getEcommerceSettingsStatic().setPlayComLastUpdated( lastUpdated );
			EcommerceConfiguration.getEcommerceSettingsStatic().saveDetails();
			String lastUpdatedStr = dateFormat.format( lastUpdated );
			StringBuffer outputStrBuf = new StringBuffer();
			outputStrBuf.append( headerStrBuf );
			FileDetails fileDetails = new FileDetails( EcommerceWorkingDirectory.BARCODE_CSV.getAplosWorkingDirectory(), null );
			fileDetails.saveDetails();
			fileDetails.setFilename( fileDetails.getId() + ".csv" );
			try {
				File file = new File( fileDetails.determineFileDetailsDirectory(true) + fileDetails.getId() + ".csv" );
				file.createNewFile();
			    FileWriter writer = new FileWriter( file );
				for( int i = 0, n = barcodeLines.size(); i < n; i++ ) {
					outputStrBuf.append( barcodeLines.get( i ).getBarcode() );
					outputStrBuf.append( "," );
					if( !CommonUtil.isNullOrEmpty(barcodeLines.get( i ).getDescription()) ) {
						outputStrBuf.append( "\"" );
						outputStrBuf.append( barcodeLines.get( i ).getDescription().replace( "\"", "\"\"" ) );
						outputStrBuf.append( "\"" );
					}
					outputStrBuf.append( "," );
					outputStrBuf.append( lastUpdatedStr );
					outputStrBuf.append( "\n" );
				}
				writer.write( outputStrBuf.toString() );
				writer.close();
				fileDetails.saveDetails();
				try {
					FileIoUtil.fileDownload(fileDetails.determineFileDetailsDirectory(true) + fileDetails.getId() + ".csv", DocumentType.COMMA_SEPERATED_VALUES);
				} catch (InputMismatchException ime) {
					ime.printStackTrace();
				}
			} catch( IOException ioex ) {
				ApplicationUtil.getAplosContextListener().handleError( ioex );
			}
			firstEmptyLine = -1;
		} else {
			JSFUtil.addMessageForWarning( "Please upload the current barcode csv file first" );
		}
	}

	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}
	
	private class BarcodeLine {
		private String barcode;
		private String description;
		private String updated;
		
		public BarcodeLine() {
		}
		
		public BarcodeLine( String barcode, String description, String updated ) {
			this.setBarcode(barcode);
			this.setDescription(description);
			this.setUpdated(updated);
		}
		
		public String getBarcode() {
			return barcode;
		}
		
		public String getDescription() {
			return description;
		}
		
		public String getUpdated() {
			return updated;
		}

		public void setBarcode(String barcode) {
			this.barcode = barcode;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public void setUpdated(String updated) {
			this.updated = updated;
		}
	}
}
