package com.aplos.ecommerce.backingpage.serialNumbers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.SortOrder;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.AplosUrl;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.AplosAbstractBean;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.beans.Website;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.backingpage.realizedProductReturn.RealizedProductReturnEditPage;
import com.aplos.ecommerce.beans.CompanyContact;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.SerialNumber;
import com.aplos.ecommerce.beans.listbeans.SerialNumberListBean;
import com.aplos.ecommerce.templates.printtemplates.PackagingLabelTemplate;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=SerialNumber.class)
public class SerialNumberListPage extends ListPage {
	private static final long serialVersionUID = 6720906581935162315L;

	public String redirectToAssignSerialNumbers() {
		JSFUtil.redirect(Website.getCurrentWebsiteFromTabSession().getPackageName(), AssignSerialNumbersPage.class);
		return null;
	}
	
	public String getCompanyName( Customer customer ) {
		if( customer instanceof CompanyContact ) {
			return ((CompanyContact) customer).getCompany().getCompanyName();
		} else {
			return "";
		}
	}

	public String redirectToGenerateNewSerialNumbers(boolean areVoidStickersRequired) {
		GenerateNewSerialNumbersPage generateNewSerialNumbersPage = (GenerateNewSerialNumbersPage) JSFUtil.resolveVariable(AplosAbstractBean.getBinding(GenerateNewSerialNumbersPage.class));
		generateNewSerialNumbersPage.setAreVoidStickersRequired(areVoidStickersRequired);
		JSFUtil.redirect(Website.getCurrentWebsiteFromTabSession().getPackageName(), GenerateNewSerialNumbersPage.class);
		return null;
	}

	public void redirectToReprintVoidStickers() {
		JSFUtil.redirect( ReprintVoidStickersPage.class );
	}

	public String redirectToPrintPackagingLabels() {
		List<Object> serialNumberListBeanList = getDataTableState().getLazyDataModel().getRecordArray();
		List<Long> selectedSerialNumberIds = new ArrayList<Long>();
		SerialNumberListBean tempSerialNumberListBean;
		for( Object tempObject : serialNumberListBeanList ) {
			if( tempObject != null ) {
				tempSerialNumberListBean = (SerialNumberListBean) tempObject;
				if( tempSerialNumberListBean.isSelected() ) {
					selectedSerialNumberIds.add( tempSerialNumberListBean.getId() );
				}
			}
		}
		if( selectedSerialNumberIds.size() > 0 ) {
			JSFUtil.redirect(new AplosUrl(PackagingLabelTemplate.getTemplateUrl( selectedSerialNumberIds.toArray( new Long[0] ) )), true );
		} else {
			JSFUtil.addMessage( "Please select at least one serial number" );
		}
		return null;
	}

	public String redirectToBookIn() {
		BookInPage bookInPage = (BookInPage) JSFUtil.resolveVariable(AplosAbstractBean.getBinding(BookInPage.class));
		bookInPage.setTransaction(null);
		JSFUtil.redirect(Website.getCurrentWebsiteFromTabSession().getPackageName(), BookInPage.class);
		return null;
	}

	public String isBookedOutStr(SerialNumber serialNumber) {
		if (isBookedOut(serialNumber)) {
			return "yes";
		} else {
			return "no";
		}
	}

	public boolean isBookedOut(SerialNumber serialNumber) {
		if ( serialNumber.getSerialNumberOwner() == null ) {
			return false;
		} else {
			return true;
		}
	}

	public String createReturnAndRedirect() {
		SerialNumber serialNumber = JSFUtil.getBeanFromRequest( "tableBean" );
		SerialNumber loadedSerialNumber = new BeanDao( SerialNumber.class ).get( serialNumber.getId() );
		return createReturnAndRedirect(loadedSerialNumber);
	}

	public String createReturnAndRedirect( SerialNumber serialNumber ) {
		SerialNumber loadedSerialNumber = new BeanDao( SerialNumber.class ).get( serialNumber.getId() );
//		HibernateUtil.initialise( loadedSerialNumber, true );
		 RealizedProductReturnEditPage.createNewReturn(loadedSerialNumber);
		JSFUtil.redirect(Website.getCurrentWebsiteFromTabSession().getPackageName(), RealizedProductReturnEditPage.class);
		return null;
	}

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new SerialNumberLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class SerialNumberLazyDataModel extends AplosLazyDataModel {

		private static final long serialVersionUID = 6679175187390275783L;

		public SerialNumberLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
		}
		
		public String getPartNumberRowStyle() {
			SerialNumber serialNumber = (SerialNumber) getAplosBean();
			if( serialNumber != null ) {
				if( serialNumber.isAddedToWaste() ) {
					return "background-color:#FFBBBB;";
				}
			}
			return "";
		}

		@Override
		public List<Object> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
			getBeanDao().setSelectCriteria( "bean.id, bean.isVoidStickerRequired, rp.id, rp.itemCode, p.itemCode, bean.serialNumberOwner, bean.buildDate, bean.isAddedToWaste, bean.active, bean.deletable" );
			getBeanDao().addQueryTable( "rp", "bean.realizedProduct" );
			getBeanDao().addQueryTable( "pi", "rp.productInfo" );
			getBeanDao().addQueryTable( "p", "pi.product" );
//			getBeanDao().addAliasesForOptimisation( "rp", "realizedProduct" );
//			getBeanDao().addAliasesForOptimisation( "p", "realizedProduct.productInfo.product" );
			getBeanDao().setListBeanClass(SerialNumberListBean.class);
			List<Object> objects = super.load(first, pageSize, sortField, sortOrder, filters);
			Object[] serialNumberListBeanObjs = objects.toArray();
			for( int i = 0, n = serialNumberListBeanObjs.length; i < n; i++ ) {
				if( serialNumberListBeanObjs[ i ] != null ) {
					((SerialNumberListBean) serialNumberListBeanObjs[ i ]).loadOptimisedReturns();
				}
			}
			return Arrays.asList(serialNumberListBeanObjs);
		}

		@Override
		public void addSearchParameters(BeanDao aqlBeanDao, Map<String,String> filters) {
			super.addSearchParameters(aqlBeanDao,filters);
			String searchText = getDataTableState().getSearchText();
			if( searchText != null ) {
				aqlBeanDao.setNamedParameter( "fullSerialNumber", searchText.replaceFirst( "TSN", "" ) );
			}
		}

		@Override
		public String getSearchCriteria() {
			return "bean.id LIKE :exactSearchText OR bean.id LIKE :fullSerialNumber OR bean.realizedProduct.itemCode LIKE :startsWithSearchText OR bean.realizedProduct.productInfo.product.itemCode LIKE :startsWithSearchText ";
		}

	}



}
