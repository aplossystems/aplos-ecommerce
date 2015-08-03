package com.aplos.ecommerce.backingpage.customer;

import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.SortOrder;

import antlr.RecognitionException;
import antlr.TokenStreamException;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.aql.SubBeanDao;
import com.aplos.common.aql.WhereConditionGroup;
import com.aplos.common.aql.antlr.AqlParser;
import com.aplos.common.aql.aqltables.unprocessed.UnprocessedAqlTable;
import com.aplos.common.aql.aqlvariables.AqlVariable;
import com.aplos.common.aql.aqlvariables.CaseAqlVariable;
import com.aplos.common.aql.selectcriteria.SelectCriteria;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.utils.ApplicationUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.Company;
import com.aplos.ecommerce.beans.CompanyType;
import com.aplos.ecommerce.beans.listbeans.CompanyListBean;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=Company.class)
public class CompanyListPage extends ListPage {

	private static final long serialVersionUID = 5946716150631731025L;

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new CompanyLdm(dataTableState, aqlBeanDao);
	}

	public class CompanyLdm extends AplosLazyDataModel {

		private static final long serialVersionUID = 2002456085544135613L;
		private CompanyType currentlyShowingCompanyType = null;

		public CompanyLdm(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao, false);
			aqlBeanDao.removeQueryTable( "bean" );
			SubBeanDao subBeanDao = new SubBeanDao( Company.class );
			subBeanDao.addSelectCriteria( new String[]{ "bean.id", "bean.companyName", "bean.duplicateAddresses", "bean.companyType.name", "bean.companyType.id", "bean.deletable", "bean.active" } );
			AqlParser aqlParser = AqlParser.getInstance( "bean.duplicateAddresses = 0" );
			try {
				WhereConditionGroup whereConditionGroup = aqlParser.parseWhereCriteria(aqlBeanDao);
				aqlParser = AqlParser.getInstance( "bean.billingAddress.id" );
				AqlVariable thenAqlVariable = aqlParser.parseAqlVariable(aqlBeanDao, null);
				aqlParser = AqlParser.getInstance( "bean.shippingAddress.id" );
				AqlVariable elseAqlVariable = aqlParser.parseAqlVariable(aqlBeanDao, null);
				CaseAqlVariable caseSelectCriteria = new CaseAqlVariable( whereConditionGroup, thenAqlVariable, elseAqlVariable ); 
				subBeanDao.addSelectCriteria( new SelectCriteria( caseSelectCriteria, "determinedAddressId" ) );
				aqlBeanDao.addAqlSubQueryTable( "innerTable", subBeanDao );
				aqlBeanDao.addSelectCriteria( new String[]{ "address.id", "address.line1", "address.country.name", "address.postcode", "address.phone" } );
				UnprocessedAqlTable aqlTable = aqlBeanDao.addQueryTable( "address", "innerTable.determinedAddressId" );
				aqlTable.setCaseSelectCriteria(caseSelectCriteria);
			} catch( TokenStreamException tsEx ) {
				ApplicationUtil.handleError( tsEx );
			} catch( RecognitionException rEx ) {
				ApplicationUtil.handleError( rEx );
			}
			aqlBeanDao.setListBeanClass(CompanyListBean.class);
		}

		public void setCurrentlyShowingCompanyType(CompanyType currentlyShowingCompanyType) {
			this.currentlyShowingCompanyType = currentlyShowingCompanyType;
		}

		public void goToOverview() {
			selectBean(false);
			JSFUtil.redirect( CompanyOverviewPage.class );
		}

		public CompanyType getCurrentlyShowingCompanyType() {
			return currentlyShowingCompanyType;
		}

		@Override
		public String getSearchCriteria() {
			return "innerTable.id LIKE :similarSearchText OR innerTable.companyName LIKE :similarSearchText OR address.line1 LIKE :similarSearchText OR address.postcode LIKE :similarSearchText OR address.country.name LIKE :similarSearchText";
		}

		@Override
		public List<Object> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
			getBeanDao().clearWhereCriteria();
			if (currentlyShowingCompanyType != null) {
				getBeanDao().addWhereCriteria("bean.companyType.id = " + currentlyShowingCompanyType.getId());
			}

			List<Object> objects =  super.load(first, pageSize, sortField, sortOrder, filters);
			CompanyListBean.determineOrderCounts(objects);
			return objects;
		}

	}


}
