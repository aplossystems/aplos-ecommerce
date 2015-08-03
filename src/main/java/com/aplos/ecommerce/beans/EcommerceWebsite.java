package com.aplos.ecommerce.beans;

import com.aplos.cms.beans.CmsWebsite;
import com.aplos.cms.beans.MenuNode;
import com.aplos.cms.beans.pages.TopLevelTemplate;
import com.aplos.common.annotations.persistence.Cache;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.OneToOne;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.listeners.AplosContextListener;
import com.aplos.common.module.CommonConfiguration;
import com.aplos.ecommerce.module.EcommerceCmsPageRevisions;
import com.aplos.ecommerce.module.EcommerceConfiguration;
import com.aplos.ecommerce.module.EcommerceDatabaseLoader;

@Entity
@Cache
public class EcommerceWebsite extends CmsWebsite {

	private static final long serialVersionUID = 3210802884315759231L;

	@OneToOne
	private MenuNode ecommerceMenu;

	@OneToOne
	private MenuNode checkoutMenu;

	@OneToOne
	private MenuNode customerMenu;

	public EcommerceWebsite() { super(); }

	@Override
	public void createDefaultWebsiteObjects( AplosContextListener aplosContextListener ) {
		super.createDefaultWebsiteObjects(aplosContextListener);
		EcommerceCmsPageRevisions ecommerceCmsPageRevisions = EcommerceConfiguration.getEcommerceConfiguration().getEcommerceCmsPageRevisions();
		SystemUser adminUser = CommonConfiguration.getCommonConfiguration().getDefaultAdminUser();
		EcommerceDatabaseLoader.createMenus(this,adminUser);
		TopLevelTemplate mainTemplate = getOrCreateMainTemplate(adminUser);
		EcommerceDatabaseLoader.createDefaultPages(this, ecommerceCmsPageRevisions, adminUser, aplosContextListener, mainTemplate);
	}

	public void setEcommerceMenu(MenuNode ecommerceMenu) {
		this.ecommerceMenu = ecommerceMenu;
	}

	public MenuNode getEcommerceMenu() {
		return ecommerceMenu;
	}

	public void setCheckoutMenu(MenuNode checkoutMenu) {
		this.checkoutMenu = checkoutMenu;
	}

	public MenuNode getCheckoutMenu() {
		return checkoutMenu;
	}

	public void setCustomerMenu(MenuNode customerMenu) {
		this.customerMenu = customerMenu;
	}

	public MenuNode getCustomerMenu() {
		return customerMenu;
	}

//	@Override
//	public void checkAndUpdateWebsiteState( AplosContextListener aplosContextListener ) {
//		super.checkAndUpdateWebsiteState(aplosContextListener);
//		boolean saveRequired = false;
//		//  This is just code to update versions that don't have the defaults already set
//		//  this could be removed at a later stage once all projects have been updated.
//		if( getMainTemplate() == null ) {
//			TopLevelTemplate mainTemplate = new SiteBeanDao( this, TopLevelTemplate.class ).addWhereCriteria( "bean.name = 'Main'").get();
//			if( mainTemplate == null ) {
//				mainTemplate = new SiteBeanDao( this, TopLevelTemplate.class ).addWhereCriteria( "bean.name = 'Main Template'").get();
//			}
//			setMainTemplate( mainTemplate );
//			saveRequired = true;
//		}
//		if( getHoldingTemplate() == null ) {
//			TopLevelTemplate holdingTemplate = new SiteBeanDao( this, TopLevelTemplate.class ).addWhereCriteria( "bean.name = 'Holding Template'").get();
//			setHoldingTemplate( holdingTemplate );
//			saveRequired = true;
//		}
//		if( getHoldingPage() == null ) {
//			setHoldingPage( CmsDefaultsLoader.createHoldingPage(this, CommonUtil.getAdminUser(), HibernateUtil.getCurrentSession(), getHoldingTemplate(), getErrorMenu() ) );
//			saveRequired = true;
//		}
//		if( getIssueReportedPage() == null ) {
//			setIssueReportedPage( CmsDefaultsLoader.createIssueReportedPage(this, CommonUtil.getAdminUser(), HibernateUtil.getCurrentSession(), getMainTemplate(), getErrorMenu(), aplosContextListener ) );
//			saveRequired = true;
//		}
//		if( getSessionTimeoutPage() == null ) {
//			setSessionTimeoutPage( CmsDefaultsLoader.createSessionTimeoutPage(this, CommonUtil.getAdminUser(), HibernateUtil.getCurrentSession(), getMainTemplate(), getErrorMenu(), aplosContextListener ) );
//			saveRequired = true;
//		}
//		if( getPageNotFoundPage() == null ) {
//			setPageNotFoundPage( CmsDefaultsLoader.createPageNotFoundPage(this, CommonUtil.getAdminUser(), HibernateUtil.getCurrentSession(), getMainTemplate(), getErrorMenu() ) );
//			saveRequired = true;
//		}
//
//		if( saveRequired ) {
//			saveDetails();
//		}
//	}

//	public static EcommerceWebsite getEcommerceWebsite() {
//		return (EcommerceWebsite) new AqlBeanDao( EcommerceWebsite.class ).load( 1 );
//	}

}
