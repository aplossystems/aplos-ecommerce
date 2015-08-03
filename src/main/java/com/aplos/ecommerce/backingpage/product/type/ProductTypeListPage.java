package com.aplos.ecommerce.backingpage.product.type;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.enums.DocumentType;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FileIoUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.beans.playcom.PlayColour;
import com.aplos.ecommerce.beans.playcom.PlayMainGenre;
import com.aplos.ecommerce.beans.playcom.PlaySize;
import com.aplos.ecommerce.beans.product.ProductType;
import com.aplos.ecommerce.enums.EcommerceWorkingDirectory;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=ProductType.class)
public class ProductTypeListPage extends ListPage  {

	private static final long serialVersionUID = -4101396224445086182L;
	private static Logger logger = Logger.getLogger( ProductTypeListPage.class );

	@Override
	public boolean responsePageLoad() {
		super.responsePageLoad();
		BeanDao productTypeDao = new BeanDao( ProductType.class );
		ProductType parentProductType = JSFUtil.getBeanFromView( ProductType.PARENT_PRODUCT_TYPE );
		if (parentProductType != null) {
			productTypeDao.addWhereCriteria( "bean.parentProductType.id = " + parentProductType.getId() );
		}
		setBeanDao(productTypeDao);
		return true;
	}
	
	public void createPlayComCsv() {
		BeanDao realizedProductDao = new BeanDao( RealizedProduct.class );
		realizedProductDao.addWhereCriteria( "bean.productInfo.isOnStock = false" );
		List<RealizedProduct> realizedProductList = realizedProductDao.getAll();
		RealizedProduct tempRealizedProduct;
		PlayComCsvLine tempPlayComCsvLine;

		FileDetails fileDetails = new FileDetails( EcommerceWorkingDirectory.PLAY_COM_CSV.getAplosWorkingDirectory(), null );
		fileDetails.saveDetails();
		fileDetails.setFilename( fileDetails.getId() + ".csv" );
		Set<String> addBarcodeSet = new HashSet<String>(); 
		try {
			File file = new File( fileDetails.determineFileDetailsDirectory(true) + fileDetails.getId() + ".csv" );
			file.createNewFile();
		    FileWriter writer = new FileWriter( file );


			for( int i = 0, n = realizedProductList.size(); i < n; i++ ) {
				tempRealizedProduct = realizedProductList.get( i );
				List<ProductType> productTypes = tempRealizedProduct.getProductInfo().getProduct().getProductTypes();
				if( productTypes.size() == 0 ) {
					continue;
				}
				if( CommonUtil.isNullOrEmpty( tempRealizedProduct.getBarcode() ) ) {
					logger.info( "barcode Empty" );
					continue;
				}
				PlayMainGenre playMainGenre = productTypes.get( 0 ).getPlayMainGenre(); 
				if( playMainGenre == null ) {
					logger.info( "Play main genre is null" );
					continue;
				}
				PlayColour playColour = tempRealizedProduct.getProductColour().getPlayColour(); 
				if( playColour == null ) {
					logger.info( "Play colour is null" );
					continue;
				}
				PlaySize playSize = tempRealizedProduct.getProductSize().getPlaySize(); 
				if( playSize == null ) {
					logger.info( "Play size is null" );
					continue;
				} 
				if( tempRealizedProduct.getImageDetailsList().size() == 0 ) {
					logger.info( "This product has no images" );
					continue;
				} 
				if( addBarcodeSet.contains( tempRealizedProduct.getBarcode() ) ) {
					logger.info( "This barcode has already been added" );
					continue;
				}
				
				tempPlayComCsvLine = new PlayComCsvLine();
				tempPlayComCsvLine.setProductTitle( tempRealizedProduct.getProductInfo().getName() );
				tempPlayComCsvLine.setMainGenre( playMainGenre.getName() );
				tempPlayComCsvLine.setClothingType( playMainGenre.getPlaySubGenre().getName() );
				tempPlayComCsvLine.setColour( playColour.getName() );
				tempPlayComCsvLine.setSize( playSize.getName() );
				tempPlayComCsvLine.setBarcode( tempRealizedProduct.getBarcode() );
				addBarcodeSet.add( tempRealizedProduct.getBarcode() );
				tempPlayComCsvLine.setSearchTags( StringUtils.join( tempRealizedProduct.getProductInfo().getSearchKeywordList(), "," ) );
				tempPlayComCsvLine.setBrand( tempRealizedProduct.getProductInfo().getProduct().getProductBrand().getName() );

				if( !CommonUtil.isNullOrEmpty( tempRealizedProduct.getProductInfo().getShortDescription() ) ) {
					tempPlayComCsvLine.setProductDescription(StringEscapeUtils.escapeXml( StringEscapeUtils.unescapeHtml( Jsoup.clean( tempRealizedProduct.getProductInfo().getShortDescription(), Whitelist.none() )) ));
				} else {
					tempPlayComCsvLine.setProductDescription( StringEscapeUtils.escapeXml( StringEscapeUtils.unescapeHtml( Jsoup.clean( tempRealizedProduct.getProductInfo().getLongDescription(), Whitelist.none() ) ) ));
				}
				List<String> imageDetailUrls = new ArrayList<String>();
				for( int j = 0, p = tempRealizedProduct.getImageDetailsList().size(); j < p; j++ ) {
					if( tempRealizedProduct.getImageDetailsList().get( j ).getFileDetailsKey().equals( RealizedProduct.RealizedProductImageKey.LARGE_IMAGE.name() )  
							|| tempRealizedProduct.getImageDetailsList().get( j ).getFileDetailsKey().equals( RealizedProduct.RealizedProductImageKey.DETAIL_IMAGE_1.name() )
							|| tempRealizedProduct.getImageDetailsList().get( j ).getFileDetailsKey().equals( RealizedProduct.RealizedProductImageKey.DETAIL_IMAGE_2.name() )
							|| tempRealizedProduct.getImageDetailsList().get( j ).getFileDetailsKey().equals( RealizedProduct.RealizedProductImageKey.DETAIL_IMAGE_3.name() )
							|| tempRealizedProduct.getImageDetailsList().get( j ).getFileDetailsKey().equals( RealizedProduct.RealizedProductImageKey.DETAIL_IMAGE_4.name() )) {
						imageDetailUrls.add( tempRealizedProduct.getImageDetailsList().get( j ).getExternalFileUrl() );	
					}
				}
				tempPlayComCsvLine.setImageUrls( StringUtils.join( imageDetailUrls, ";" ) );
				writer.write( tempPlayComCsvLine.createCsvLine().toString() );
				writer.write( "\n" );
			}
			
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
	}

	@Override
	public AplosLazyDataModel getAplosLazyDataModel( DataTableState dataTableState, BeanDao aqlBeanDao ) {
		return new ProductTypeLazyDataModel( dataTableState, aqlBeanDao );
	}
	
	public class PlayComCsvLine {
		private String productTitle;
		private String mainGenre;
		private String clothingType;
		private String colour;
		private String size;
		private String barcode;
		private String catalogueNumber = "";
		private String brand;
		private String fabricComposition = "";
		private String washCareInstructions = "";
		private String extraFeatures = "";
		private String productDescription;
		private String imageUrls;
		private String additionalGenre1 = "";
		private String searchTags = "";
		
		public StringBuffer createCsvLine() {
			StringBuffer strBuf = new StringBuffer( "\"" );
			strBuf.append( getProductTitle() ).append( "\"," );
			strBuf.append( "\"" ).append( getMainGenre().replace( "\"", "\"\"") ).append( "\"," );
			strBuf.append( "\"" ).append( getClothingType().replace( "\"", "\"\"") ).append( "\"," );
			strBuf.append( "\"" ).append( getColour().replace( "\"", "\"\"") ).append( "\"," );
			strBuf.append( "\"" ).append( getSize().replace( "\"", "\"\"") ).append( "\"," );
			strBuf.append( "=\"" ).append( getBarcode().replace( "\"", "\"\"") ).append( "\"," );
			strBuf.append( "\"" ).append( getCatalogueNumber() ).append( "\"," );
			strBuf.append( "\"" ).append( getBrand().replace( "\"", "\"\"") ).append( "\"," );
			strBuf.append( "\"" ).append( getFabricComposition().replace( "\"", "\"\"") ).append( "\"," );
			strBuf.append( "\"" ).append( getWashCareInstructions().replace( "\"", "\"\"") ).append( "\"," );
			strBuf.append( "\"" ).append( getExtraFeatures().replace( "\"", "\"\"") ).append( "\"," );
			strBuf.append( "\"" ).append( getProductDescription().replace( "\"", "\"\"") ).append( "\"," );
			strBuf.append( "\"" ).append( getImageUrls().replace( "\"", "\"\"") ).append( "\"," );
			strBuf.append( "\"" ).append( getAdditionalGenre1() ).append( "\"," );
			strBuf.append( "\"" ).append( getSearchTags().replace( "\"", "\"\"") ).append( "\"" );
			return strBuf;
		}
		
		public String getProductTitle() {
			return productTitle;
		}
		public void setProductTitle(String productTitle) {
			this.productTitle = productTitle;
		}
		public String getMainGenre() {
			return mainGenre;
		}
		public void setMainGenre(String mainGenre) {
			this.mainGenre = mainGenre;
		}
		public String getClothingType() {
			return clothingType;
		}
		public void setClothingType(String clothingType) {
			this.clothingType = clothingType;
		}
		public String getColour() {
			return colour;
		}
		public void setColour(String colour) {
			this.colour = colour;
		}
		public String getSize() {
			return size;
		}
		public void setSize(String size) {
			this.size = size;
		}
		public String getBarcode() {
			return barcode;
		}
		public void setBarcode(String barcode) {
			this.barcode = barcode;
		}
		public String getCatalogueNumber() {
			return catalogueNumber;
		}
		public void setCatalogueNumber(String catalogueNumber) {
			this.catalogueNumber = catalogueNumber;
		}
		public String getBrand() {
			return brand;
		}
		public void setBrand(String brand) {
			this.brand = brand;
		}
		public String getFabricComposition() {
			return fabricComposition;
		}
		public void setFabricComposition(String fabricComposition) {
			this.fabricComposition = fabricComposition;
		}
		public String getWashCareInstructions() {
			return washCareInstructions;
		}
		public void setWashCareInstructions(String washCareInstructions) {
			this.washCareInstructions = washCareInstructions;
		}
		public String getExtraFeatures() {
			return extraFeatures;
		}
		public void setExtraFeatures(String extraFeatures) {
			this.extraFeatures = extraFeatures;
		}
		public String getProductDescription() {
			return productDescription;
		}
		public void setProductDescription(String productDescription) {
			this.productDescription = productDescription;
		}
		public String getImageUrls() {
			return imageUrls;
		}
		public void setImageUrls(String imageUrls) {
			this.imageUrls = imageUrls;
		}
		public String getAdditionalGenre1() {
			return additionalGenre1;
		}
		public void setAdditionalGenre1(String additionalGenre1) {
			this.additionalGenre1 = additionalGenre1;
		}
		public String getSearchTags() {
			return searchTags;
		}
		public void setSearchTags(String searchTags) {
			this.searchTags = searchTags;
		}
	}

	public class ProductTypeLazyDataModel extends AplosLazyDataModel {

		private static final long serialVersionUID = -8248787815350622296L;

		public ProductTypeLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
		}

		@Override
		public String getSearchCriteria() {
			return "bean.name LIKE :similarSearchText";
		}

		@Override
		public void goToNew() {
			super.goToNew();
			ProductType parentProductType = JSFUtil.getBeanFromView( ProductType.PARENT_PRODUCT_TYPE );
			ProductType productType = (ProductType) JSFUtil.getBeanFromScope( ProductType.class );
			productType.setParentProductType(parentProductType);
		}
	}
}
