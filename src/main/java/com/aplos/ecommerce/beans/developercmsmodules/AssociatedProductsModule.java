package com.aplos.ecommerce.beans.developercmsmodules;

import java.util.ArrayList;
import java.util.List;

import com.aplos.cms.beans.developercmsmodules.ConfigurableDeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.common.FileDetailsOwnerHelper;
import com.aplos.common.SaveableFileDetailsOwnerHelper;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.annotations.persistence.Transient;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.FileDetails;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.interfaces.FileDetailsOwnerInter;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.RealizedProduct;
import com.aplos.ecommerce.beans.product.ProductInfo;
import com.aplos.ecommerce.enums.EcommerceWorkingDirectory;
import com.aplos.ecommerce.interfaces.RealizedProductRetriever;

@Entity
@DynamicMetaValueKey(oldKey={"ASSOCIATED_PRODUCTS_MODULE","ASSOC_PRODS"})
public class AssociatedProductsModule extends ConfigurableDeveloperCmsAtom implements FileDetailsOwnerInter {

	private static final long serialVersionUID = 5297895294072870566L;

	private int numberOfItemsToDisplay = 8;
	private String title = "Complete the look..."; //optional, not displayed if empty
//	private String titleImageFilename; //optional, not displayed if empty
	@ManyToOne
	private RealizedProduct realizedProduct; //optional, stops it automatically using the session rp

	@ManyToOne
	private FileDetails imageDetails;
	
	@Transient
	private AssociatedProductsModuleFdoh associatedProductsModuleFdoh = new AssociatedProductsModuleFdoh(this);
	
	public AssociatedProductsModule() {
		super();
	}
	
	@Override
	public FileDetailsOwnerHelper getFileDetailsOwnerHelper() {
		return associatedProductsModuleFdoh;
	}

	@Override
	public void saveBean(SystemUser currentUser) {
		FileDetails.saveFileDetailsOwner(this, AssociatedProductsImageKey.values(), currentUser);
	}
	
	@Override
	public String getName() {
		return "Associated Products";
	}

	public RealizedProduct determineRealizedProduct() {
		if (realizedProduct != null) {
			return realizedProduct;
		} else {
			return JSFUtil.getBeanFromScope(RealizedProduct.class);
		}
	}

	@Override
	public DeveloperCmsAtom getCopy() {
		AssociatedProductsModule copiedAtom = new AssociatedProductsModule();
		copiedAtom.setNumberOfItemsToDisplay(getNumberOfItemsToDisplay());
		copiedAtom.setTitle(getTitle());
		copiedAtom.setImageDetails(imageDetails);
		copiedAtom.setRealizedProduct(getRealizedProduct());
		return copiedAtom;
	}

	public void setRealizedProduct(RealizedProduct realizedProduct) {
		this.realizedProduct = realizedProduct;
	}

	public RealizedProduct getRealizedProduct() {
		return realizedProduct;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setNumberOfItemsToDisplay(int numberOfItemsToDisplay) {
		this.numberOfItemsToDisplay = numberOfItemsToDisplay;
	}

	public int getNumberOfItemsToDisplay() {
		return numberOfItemsToDisplay;
	}

	public List<RealizedProduct> getAssociatedProductList() {
		RealizedProduct realizedProduct = determineRealizedProduct();
		if (realizedProduct != null) {
			List<RealizedProduct> products = new ArrayList<RealizedProduct>();
			ProductInfo info = new BeanDao(ProductInfo.class).get(realizedProduct.getProductInfo().getId());
//			HibernateUtil.initialise( info, true );
			for (RealizedProductRetriever rpr : info.getOptionalAccessoriesList()) {
				products.add(rpr.retrieveRealizedProduct(realizedProduct));
			}
			return products;
		} else {
			return null;
		}
	}

	public boolean isHasProducts() {
		List<RealizedProduct> productList = getAssociatedProductList();
		if (productList != null && productList.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	public String getFullTitleImageUrl() {
		if( imageDetails != null ) {
			return imageDetails.getFullFileUrl(true);
		} else {
			return null;
		}
	}

	public FileDetails getImageDetails() {
		return imageDetails;
	}

	public void setImageDetails(FileDetails imageDetails) {
		this.imageDetails = imageDetails;
	}
	
	public enum AssociatedProductsImageKey {
		TITLE_IMAGE;
	}
	
	@Override
	public void superSaveBean(SystemUser currentUser) {
		super.saveBean(currentUser);
	}
	
	private class AssociatedProductsModuleFdoh extends SaveableFileDetailsOwnerHelper {
		public AssociatedProductsModuleFdoh( AssociatedProductsModule associatedProductsModule ) {
			super( associatedProductsModule );
		}

		@Override
		public String getFileDetailsDirectory(String fileDetailsKey, boolean includeServerWorkPath) {
			if (AssociatedProductsImageKey.TITLE_IMAGE.name().equals(fileDetailsKey)) {
				return EcommerceWorkingDirectory.ICONS_DIR.getDirectoryPath(includeServerWorkPath);
			}
			return null;
		}

		@Override
		public void setFileDetails(FileDetails fileDetails, String fileDetailsKey, Object collectionKey) {
			if (AssociatedProductsImageKey.TITLE_IMAGE.name().equals(fileDetailsKey)) {
				setImageDetails(fileDetails);		
			}
		}

		@Override
		public FileDetails getFileDetails(String fileDetailsKey, Object collectionKey) {
			if( AssociatedProductsImageKey.TITLE_IMAGE.name().equals( fileDetailsKey ) ) {
				return getImageDetails();
			}
			return null;
		}
	}

}
