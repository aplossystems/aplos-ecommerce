package com.aplos.ecommerce.backingpage.customer;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.Company;
import com.aplos.ecommerce.beans.CompanyContact;
import com.aplos.ecommerce.beans.Customer;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=CompanyContact.class)
public class CompanyContactEditPage extends CustomerEditPage {

	private static final long serialVersionUID = 4151640345426195361L;
	private boolean isShowingTransferScreen = false;

	private Company selectedCompany;

	public String redirectToCompanyEdit() {
		CompanyContact companyContact = JSFUtil.getBeanFromScope( CompanyContact.class );
		if ( saveCustomer( companyContact ) ) {
			CompanyContact resller = JSFUtil.getBeanFromScope(CompanyContact.class);
			resller.getCompany().addToScope();

			JSFUtil.redirect( CompanyEditPage.class );
		}
		return null;
	}

	@Override
	public boolean saveCustomer(Customer customer) {
		return super.saveCustomer(customer);
	}

	@Override
	public Customer getCustomerFromSession() {
		return JSFUtil.getBeanFromScope( CompanyContact.class );
	}

	@Override
	public void redirectToPage(AplosBean aplosBean) {
		super.redirectToPage(aplosBean);
		((CompanyContact) aplosBean).getCompany().addToScope();
	}

	public List<Company> suggestNewCompanies(String searchStr) {
		BeanDao companyDao = new BeanDao(Company.class);
		companyDao.setSelectCriteria( "new com.aplos.ecommerce.beans.listbeans.CompanyListBean(bean.id, bean.companyName)" );
		companyDao.addWhereCriteria("bean.companyName like :newCompanySearchStr");
		companyDao.setIsReturningActiveBeans( true );
		companyDao.setNamedParameter( "newCompanySearchStr", "%" + searchStr + "%" );
		companyDao.setMaxResults( 15 );
		return companyDao.getAll();
	}

	public String getNewCompanyDisplayText(Company company) {
		return company.getCompanyName() + " (" + company.getId() + ")";
	}

	public void setNewCompany( SelectEvent event ) {
		Company company = (Company) event.getObject();
		CompanyContact companyContact = (CompanyContact) getCustomerFromSession();

		Company selectedNewCompany = new BeanDao( Company.class ).get(company.getId());
		companyContact.setCompany( selectedNewCompany );
		setSelectedCompany( null );
		setShowingTransferScreen(false);
	}

	public String transferContact() {
		setShowingTransferScreen(true);
		return null;
	}

	public String cancelTransferContact() {
		setShowingTransferScreen(false);
		return null;
	}

	public void setShowingTransferScreen(boolean isShowingTransferScreen) {
		this.isShowingTransferScreen = isShowingTransferScreen;
	}

	public boolean isShowingTransferScreen() {
		return isShowingTransferScreen;
	}

	public Company getSelectedCompany() {
		return selectedCompany;
	}

	public void setSelectedCompany(Company selectedCompany) {
		this.selectedCompany = selectedCompany;
	}

}
