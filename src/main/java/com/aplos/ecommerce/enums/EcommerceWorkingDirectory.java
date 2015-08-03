package com.aplos.ecommerce.enums;

import com.aplos.common.beans.AplosWorkingDirectory;
import com.aplos.common.enums.CommonWorkingDirectory;
import com.aplos.common.interfaces.AplosWorkingDirectoryInter;
import com.aplos.common.utils.CommonUtil;

public enum EcommerceWorkingDirectory implements AplosWorkingDirectoryInter {
	REALIZEDPRODUCT_IMAGE_DIR ( "realizedProductImages/", false ),
	PRODUCT_BRAND_IMAGE_DIR ( "realizedProductImages/brands/", false ),
	PRODUCT_FAQ_IMAGE_DIR ( "realizedProductImages/faq/", false ),
	PRODUCT_BRAND_EDITOR_IMAGE_DIR ( CommonWorkingDirectory.EDITOR_UPLOAD_DIR.getDirectoryPath(false) + "productBrand/", false ),
	COMPANY_PHOTO1_IMAGE_DIR ( "companyImages/photo1/", false ),
	COMPANY_PHOTO2_IMAGE_DIR ( "companyImages/photo2/", false ),
	COMPANY_LOGO_IMAGE_DIR ( "companyImages/logo/", false ),
	COMPANY_PHOTO_IMAGE_DIR ( "companyImages/photo/", false ),
	REALIZED_PRODUCT_RETURN_PHOTO_IMAGE_DIR ( "realizedProductReturn/photos/", false ),
	REALIZED_PRODUCT_RETURN_PDFS_DIR ( "realizedProductReturn/pdfs/", false ),
	TRANSACTION_PDFS_DIR ( "transactions/", true ), //TODO: test this restriction
	PRODUCT_TYPE_IMAGE_DIR ( "realizedProductImages/productTypes/", false ),
	NEWS_ENTRY_IMAGE_DIR ( "newsEntry/", false ),
	ICONS_DIR ( "miscellaneousIcons/", false ),
	REPORTING_PDFS_DIR ( "reportingPdfs/", false ),
	SERIAL_NUMBERS_PDFS_DIR ( "serialNumberPdfs/", false ),
	REALIZED_PRODUCT_PDFS_DIR ( "realizedProductPdfs/", false ),
	PLAY_COM_CSV ( "playComCsv/", false ),
	BARCODE_CSV ( "barcodeCsv/", false),
	CUSTOMER_CSV ( "customerCsv/", false);

	String directoryPath;
	boolean restricted;
	private AplosWorkingDirectory aplosWorkingDirectory;

	private EcommerceWorkingDirectory( String directoryPath, boolean restricted ) {
		this.directoryPath = directoryPath;
		this.restricted = restricted;
	}

	public String getDirectoryPath( boolean includeServerWorkPath ) {
		if( includeServerWorkPath ) {
			return CommonUtil.appendServerWorkPath( directoryPath );
		} else {
			return directoryPath;
		}
	}

	public static void createDirectories() {
		for( EcommerceWorkingDirectory ecommerceWorkingDirectory : EcommerceWorkingDirectory.values() ) {
			CommonUtil.createDirectory( ecommerceWorkingDirectory.getDirectoryPath(true) );
		}
	}
	
	public boolean isRestricted() {
		return this.restricted;
	}

	public AplosWorkingDirectory getAplosWorkingDirectory() {
		return aplosWorkingDirectory;
	}

	public void setAplosWorkingDirectory(AplosWorkingDirectory aplosWorkingDirectory) {
		this.aplosWorkingDirectory = aplosWorkingDirectory;
	}
	
}
