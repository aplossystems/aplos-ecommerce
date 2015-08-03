package com.aplos.ecommerce.backingpage;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.backingpage.CreateCmsWebsiteWizardPage;
import com.aplos.common.annotations.BackingPageOverride;
import com.aplos.common.backingpage.CreateWebsiteWizardPage;
import com.aplos.common.backingpage.SubscriberEditPage;
import com.aplos.common.backingpage.SubscriberListPage;
import com.aplos.common.backingpage.communication.EmailTemplateEditPage;
import com.aplos.common.backingpage.communication.EmailTemplateListPage;
import com.aplos.common.beans.lookups.UserLevel;
import com.aplos.common.module.AplosModule;
import com.aplos.common.tabpanels.MenuTab;
import com.aplos.common.tabpanels.TabClass;
import com.aplos.common.tabpanels.TabPanel;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.ecommerce.backingpage.customer.CustomerEditPage;
import com.aplos.ecommerce.backingpage.customer.CustomerListPage;
import com.aplos.ecommerce.backingpage.product.FullProductStackEditPage;
import com.aplos.ecommerce.backingpage.product.FullProductStackListPage;
import com.aplos.ecommerce.backingpage.product.GiftVoucherEditPage;
import com.aplos.ecommerce.backingpage.product.GiftVoucherListPage;
import com.aplos.ecommerce.backingpage.product.ProductBrandEditPage;
import com.aplos.ecommerce.backingpage.product.ProductBrandListPage;
import com.aplos.ecommerce.backingpage.product.ProductColourEditPage;
import com.aplos.ecommerce.backingpage.product.ProductColourListPage;
import com.aplos.ecommerce.backingpage.product.ProductFaqEditPage;
import com.aplos.ecommerce.backingpage.product.ProductFaqListPage;
import com.aplos.ecommerce.backingpage.product.size.ProductSizeCategoryEditPage;
import com.aplos.ecommerce.backingpage.product.size.ProductSizeCategoryListPage;
import com.aplos.ecommerce.backingpage.product.size.ProductSizeEditPage;
import com.aplos.ecommerce.backingpage.product.size.ProductSizeListPage;
import com.aplos.ecommerce.backingpage.product.size.ProductSizeTypeEditPage;
import com.aplos.ecommerce.backingpage.product.type.ProductTypeCustomerReviewListPage;
import com.aplos.ecommerce.backingpage.product.type.ProductTypeEditPage;
import com.aplos.ecommerce.backingpage.product.type.ProductTypeListPage;
import com.aplos.ecommerce.backingpage.shipping.CourierServiceEditPage;
import com.aplos.ecommerce.backingpage.shipping.CourierShippingServiceListPage;
import com.aplos.ecommerce.backingpage.transaction.AbandonedTransactionListPage;
import com.aplos.ecommerce.backingpage.transaction.TransactionEditPage;
import com.aplos.ecommerce.backingpage.transaction.TransactionListPage;
import com.aplos.ecommerce.beans.EcommerceWebsite;

@ManagedBean
@ViewScoped
@BackingPageOverride(backingPageClass=CreateWebsiteWizardPage.class)
public class CreateEcommerceWebsiteWizardPage extends CreateCmsWebsiteWizardPage {

	private static final long serialVersionUID = 2332981297355466118L;

	private boolean isEcommerceProject = false;
	private boolean isUsingColours = true;
	private boolean isUsingSizes = true;
	private boolean isUsingSizeCategories = true;
	private boolean isUsingFaqs = false;
	private boolean isUsingProductReviews = false;
	private boolean isUsingGiftVouchers = false;
	private boolean isShowingAbandonedOrders = true;
	private boolean isDisplayingTransactionsAsOrders = false;

	public CreateEcommerceWebsiteWizardPage() {
		super();
	}

	@Override
	public void createWebsiteObject() {
		if (isEcommerceProject) {
			setCreatedWebsite(new EcommerceWebsite());
			//set our variables
			getCreatedWebsite().setName(getName());
			getCreatedWebsite().setPrimaryHostName(getHostName());
			getCreatedWebsite().setPackageName(getPackageRoot());
			//set extra CmsWebsite specific variables
			((EcommerceWebsite)getCreatedWebsite()).setLive(getIsWebsiteLive());
			((EcommerceWebsite)getCreatedWebsite()).setGoogleAnalyticsId(getGoogleAnalyticsId());
			getCreatedWebsite().saveDetails();
		} else {
			super.createWebsiteObject();
		}
	}

	@Override
	public TabPanel setupDynamicMenu(TabPanel mainDtp, List<UserLevel> viewableByList) {
		// setup the common and cms stuff first
		mainDtp = super.setupDynamicMenu(mainDtp, viewableByList);
		// now setup ecommerce related menus
		if (isEcommerceProject) {

			TabPanel productsDtp = new TabPanel(getCreatedWebsite(), "Products Tab Panel");

			//WAIT! where's product info edit!?
			//Anthony has said product and product info will be 1-1-1 relationship with realizedproducts by default
			//and that all three are to be edited from a single page. Thats this tab.
			MenuTab productsSubTab = new MenuTab(viewableByList, "Products", TabClass.get(FullProductStackListPage.class));
			productsSubTab.addDefaultPageBinding(TabClass.get(FullProductStackEditPage.class));
			productsSubTab.setTabPanel(productsDtp);
			productsSubTab.saveDetails();
			productsDtp.addMenuTab(productsSubTab);
			productsDtp.setDefaultTab(productsSubTab);

			MenuTab productBrandTab = new MenuTab(viewableByList, "Product Brands", TabClass.get(ProductBrandListPage.class));
			productBrandTab.addDefaultPageBinding(TabClass.get(ProductBrandEditPage.class));
			productBrandTab.setTabPanel(productsDtp);
			productBrandTab.saveDetails();
			productsDtp.addMenuTab(productBrandTab);

			if (isUsingFaqs) {
				MenuTab productFaqTab = new MenuTab(viewableByList, "Product FAQs", TabClass.get(ProductFaqListPage.class));
				productFaqTab.addDefaultPageBinding(TabClass.get(ProductFaqEditPage.class));
				productFaqTab.setTabPanel(productsDtp);
				productFaqTab.saveDetails();
				productsDtp.addMenuTab(productFaqTab);
			}

			if (isUsingGiftVouchers) {
				MenuTab giftVoucherTab = new MenuTab(viewableByList, "Gift Vouchers", TabClass.get(GiftVoucherListPage.class));
				giftVoucherTab.addDefaultPageBinding(TabClass.get(GiftVoucherEditPage.class));
				giftVoucherTab.setTabPanel(productsDtp);
				giftVoucherTab.saveDetails();
				productsDtp.addMenuTab(giftVoucherTab);
			}

			MenuTab productTypeTab;
			if (isUsingProductReviews) {

				//if we have reviews product type tab needs a subtabpanel
				TabPanel productTypeStp = new TabPanel(getCreatedWebsite(), "Products Types Tab Panel");

				MenuTab productTypeEditTab = new MenuTab(viewableByList, "Edit", TabClass.get(ProductTypeEditPage.class));
				productTypeEditTab.setTabPanel(productTypeStp);
				productTypeEditTab.saveDetails();
				productTypeStp.addMenuTab(productTypeEditTab);
				productTypeStp.setDefaultTab(productTypeEditTab);

				MenuTab reviewTab = new MenuTab(viewableByList, "Reviews", TabClass.get(ProductTypeCustomerReviewListPage.class));
				//there appears to be no edit page to bind for reviews
				reviewTab.setTabPanel(productTypeStp);
				reviewTab.saveDetails();
				productTypeStp.addMenuTab(reviewTab);

				productTypeStp.setLinkedToBean(true);
				productTypeTab = new MenuTab(viewableByList, "Product Types", productTypeStp);
				productTypeTab.saveDetails();
				
				productTypeStp.saveDetails();
			} else {

				//otherwise we just bind it with the list and edit pages
				productTypeTab = new MenuTab(viewableByList, "Product Types", TabClass.get(ProductTypeListPage.class));
				productTypeTab.addDefaultPageBinding(TabClass.get(ProductTypeEditPage.class));
				productTypeTab.saveDetails();

			}
			productsDtp.addMenuTab(productTypeTab);

			if (isUsingColours) {
				MenuTab productColourTab = new MenuTab(viewableByList, "Product Colours", TabClass.get(ProductColourListPage.class));
				productColourTab.addDefaultPageBinding(TabClass.get(ProductColourEditPage.class));
				productColourTab.setTabPanel(productsDtp);
				productColourTab.saveDetails();
				productsDtp.addMenuTab(productColourTab);
			}

			if (isUsingSizes) {

				TabPanel productSizeStp = new TabPanel(getCreatedWebsite(), "Product Size Types Tab Panel");

				MenuTab sizeTypeEditTab = new MenuTab(viewableByList, "Edit", TabClass.get(ProductSizeTypeEditPage.class));
				sizeTypeEditTab.setTabPanel(productSizeStp);
				sizeTypeEditTab.saveDetails();
				productSizeStp.addMenuTab(sizeTypeEditTab);
				productSizeStp.setDefaultTab(sizeTypeEditTab);

				MenuTab sizeListTab = new MenuTab(viewableByList, "Sizes", TabClass.get(ProductSizeListPage.class));
				sizeListTab.addDefaultPageBinding(TabClass.get(ProductSizeEditPage.class));
				sizeListTab.setTabPanel(productSizeStp);
				sizeListTab.saveDetails();
				productSizeStp.addMenuTab(sizeListTab);

				if (isUsingSizeCategories) {

					MenuTab sizeCategoryListTab = new MenuTab(viewableByList, "Categories", TabClass.get(ProductSizeCategoryListPage.class));
					sizeCategoryListTab.addDefaultPageBinding(TabClass.get(ProductSizeCategoryEditPage.class));
					sizeCategoryListTab.setTabPanel(productSizeStp);
					sizeCategoryListTab.saveDetails();
					productSizeStp.addMenuTab(sizeCategoryListTab);

				}

				productSizeStp.setLinkedToBean(true);
				MenuTab productSizeTab = new MenuTab(viewableByList, "Product Sizes", productSizeStp);
				productSizeTab.setTabPanel(productsDtp);
				productSizeTab.saveDetails();
				productsDtp.addMenuTab(productSizeTab);
				
				productSizeStp.saveDetails();
			}

			//create tab to attach it to main tab panel
			MenuTab productsTab = new MenuTab(viewableByList, "Products", productsDtp);
			productsTab.setTabPanel(mainDtp);
			productsTab.saveDetails();
			mainDtp.addMenuTab(productsTab);
			mainDtp.setDefaultTab(productsTab);

			//create customers and subscribers tab
			TabPanel customersStp = new TabPanel(getCreatedWebsite(), "Customers Tab Panel");

			MenuTab customersSubTab = new MenuTab(viewableByList, "Customers", TabClass.get(CustomerListPage.class));
			customersSubTab.addDefaultPageBinding(TabClass.get(CustomerEditPage.class));
			customersSubTab.setTabPanel(customersStp);
			customersSubTab.saveDetails();
			customersStp.addMenuTab(customersSubTab);
			customersStp.setDefaultTab(customersSubTab);

			MenuTab subscribersTab = new MenuTab(viewableByList, "Subscribers", TabClass.get(SubscriberListPage.class));
			subscribersTab.addDefaultPageBinding(TabClass.get(SubscriberEditPage.class));
			subscribersTab.setTabPanel(customersStp);
			subscribersTab.saveDetails();
			customersStp.addMenuTab(subscribersTab);
			
			customersStp.saveDetails();

			MenuTab customersTab = new MenuTab(viewableByList, "Customers", customersStp);
			customersTab.setTabPanel(mainDtp);
			customersTab.saveDetails();
			mainDtp.addMenuTab(customersTab);

			//create our orders/transactions tab
			TabPanel transactionsStp = new TabPanel(getCreatedWebsite(), "Transactions Tab Panel");
			String transactionsName = "Transactions";
			if (isDisplayingTransactionsAsOrders) {
				transactionsName = "Orders";
			}

			MenuTab transactionsSubTab = new MenuTab(viewableByList, transactionsName, TabClass.get(TransactionListPage.class));
			transactionsSubTab.addDefaultPageBinding(TabClass.get(TransactionEditPage.class));
			transactionsSubTab.setTabPanel(transactionsStp);
			transactionsSubTab.saveDetails();
			transactionsStp.addMenuTab(transactionsSubTab);
			transactionsStp.setDefaultTab(transactionsSubTab);

			if (isShowingAbandonedOrders) {
				MenuTab abandonedTab = new MenuTab(viewableByList, "Abandoned " + transactionsName, TabClass.get(AbandonedTransactionListPage.class));
				//doesnt appear to be an abandoned orders edit/view page to bind
				abandonedTab.setTabPanel(transactionsStp);
				abandonedTab.saveDetails();
				transactionsStp.addMenuTab(abandonedTab);
			}

			TabPanel courierServicesStp = new TabPanel(getCreatedWebsite(), "Courier Services Tab Panel");

			MenuTab courierServicesSubTab = new MenuTab(viewableByList, "Edit", TabClass.get(CourierServiceEditPage.class));
			courierServicesSubTab.setTabPanel(courierServicesStp);
			courierServicesSubTab.saveDetails();
			courierServicesStp.addMenuTab(courierServicesSubTab);
			courierServicesStp.setDefaultTab(courierServicesSubTab);

			MenuTab shippingServicesSubTab = new MenuTab(viewableByList, "Shipping Services", TabClass.get(CourierShippingServiceListPage.class));
			//there doesnt appear to be an edit page to bind
			shippingServicesSubTab.setTabPanel(courierServicesStp);
			shippingServicesSubTab.saveDetails();
			courierServicesStp.addMenuTab(shippingServicesSubTab);
			
			courierServicesStp.saveDetails();

			courierServicesStp.setLinkedToBean(true);
			MenuTab courierServicesTab = new MenuTab(viewableByList, "Courier Services", courierServicesStp);
			courierServicesTab.saveDetails();
			courierServicesTab.setTabPanel(transactionsStp);
			transactionsStp.addMenuTab(courierServicesTab);
			
			transactionsStp.saveDetails();

			MenuTab transactionsTab = new MenuTab(viewableByList, transactionsName, transactionsStp);
			transactionsTab.setTabPanel(mainDtp);
			transactionsTab.saveDetails();
			mainDtp.addMenuTab(transactionsTab);

			productsDtp.saveDetails();
		}
		return mainDtp;
	}

	public void setIsEcommerceProject(boolean isEcommerceProject) {
		this.isEcommerceProject = isEcommerceProject;
	}

	public boolean getIsEcommerceProject() {
		return isEcommerceProject;
	}

	@Override
	public List<AplosModule> getModules() {
		List<AplosModule> modules = super.getModules();
		AplosModule mod = ApplicationUtil.getAplosContextListener().getAplosModuleByName("ecommerce");
		if (mod != null) {
			modules.add(mod);
		}
		return modules;
	}

	public void setUsingColours(boolean isUsingColours) {
		this.isUsingColours = isUsingColours;
	}

	public boolean isUsingColours() {
		return isUsingColours;
	}

	public void setUsingSizes(boolean isUsingSizes) {
		this.isUsingSizes = isUsingSizes;
	}

	public boolean isUsingSizes() {
		return isUsingSizes;
	}

	public void setUsingFaqs(boolean isUsingFaqs) {
		this.isUsingFaqs = isUsingFaqs;
	}

	public boolean isUsingFaqs() {
		return isUsingFaqs;
	}

	public void setUsingProductReviews(boolean isUsingProductReviews) {
		this.isUsingProductReviews = isUsingProductReviews;
	}

	public boolean isUsingProductReviews() {
		return isUsingProductReviews;
	}

	public void setShowingAbandonedOrders(boolean isShowingAbandonedOrders) {
		this.isShowingAbandonedOrders = isShowingAbandonedOrders;
	}

	public boolean isShowingAbandonedOrders() {
		return isShowingAbandonedOrders;
	}

	public void setDisplayingTransactionsAsOrders(
			boolean isDisplayingTransactionsAsOrders) {
		this.isDisplayingTransactionsAsOrders = isDisplayingTransactionsAsOrders;
	}

	public boolean isDisplayingTransactionsAsOrders() {
		return isDisplayingTransactionsAsOrders;
	}

	public void setUsingGiftVouchers(boolean isUsingGiftVouchers) {
		this.isUsingGiftVouchers = isUsingGiftVouchers;
	}

	public boolean isUsingGiftVouchers() {
		return isUsingGiftVouchers;
	}

	public void setUsingSizeCategories(boolean isUsingSizeCategories) {
		this.isUsingSizeCategories = isUsingSizeCategories;
	}

	public boolean isUsingSizeCategories() {
		return isUsingSizeCategories;
	}

}

