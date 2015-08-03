package com.aplos.ecommerce.developermodulebacking.frontend;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;

import com.aplos.cms.CmsPageUrl;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.AplosUrl;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.communication.AplosEmail;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.enums.EcommerceEmailTemplateEnum;
import com.aplos.ecommerce.module.EcommerceConfiguration;

@ManagedBean
@CustomScoped(value="#{ tabSession }")
public class ForgottenPasswordResetFeDmb extends DeveloperModuleBacking {
	private static final long serialVersionUID = 4703641242388745724L;
	private String password;
	private String passwordConfirm;

	public ForgottenPasswordResetFeDmb() {}

	@Override
	public boolean responsePageLoad(DeveloperCmsAtom developerCmsAtom) {

		super.responsePageLoad(developerCmsAtom);

		if( JSFUtil.getFacesContext().getViewRoot().getViewMap().get( "passwordChanged" ) == null ) {
			String passwordResetCode = JSFUtil.getRequestParameter("passwordResetCode");
			if (passwordResetCode==null || passwordResetCode.equals("")) {
				JSFUtil.addMessage("No reset code has been specified. This code is required to reset a password.", FacesMessage.SEVERITY_ERROR);
				JSFUtil.getFacesContext().getViewRoot().getViewMap().put( "resetCodeIncorrect", true );
			} else {
				BeanDao customerDao = new BeanDao( Customer.class );
				customerDao.addWhereCriteria( "bean.passwordResetCode=:passwordResetCode" );
				customerDao.setNamedParameter( "passwordResetCode", passwordResetCode );
				Customer customer = customerDao.getFirstBeanResult();
				if (customer==null) {
					JSFUtil.addMessage("The reset code provided was not recognised.", FacesMessage.SEVERITY_ERROR);
					JSFUtil.getFacesContext().getViewRoot().getViewMap().put( "resetCodeIncorrect", true );
				} else {
					//ensure the code is in-date
					Date now = new Date();
					if (customer.getPasswordResetDate().compareTo(now) <= 0) {
						//clear the reset code and date
						Customer saveableCustomer = customer.getSaveableBean();
						saveableCustomer.setPasswordResetCode(null);
						saveableCustomer.setPasswordResetDate(null);
						saveableCustomer.saveDetails();
						JSFUtil.getFacesContext().getViewRoot().getViewMap().put( "resetCodeIncorrect", true );
						JSFUtil.addMessage("The reset code provided has expired.", FacesMessage.SEVERITY_ERROR);
						JSFUtil.getFacesContext().getViewRoot().getViewMap().put( "resetCodeIncorrect", true );
					}
				}
			}
		}
		return true;
	}

	public String updateNewPassword() {
		//get the passwordResetCode from the url parameters
		String passwordResetCode = JSFUtil.getRequestParameter("passwordResetCode");
			//find the matching user
		BeanDao customerDao = new BeanDao( Customer.class );
		customerDao.addWhereCriteria( "bean.passwordResetCode=:passwordResetCode" );
		customerDao.setNamedParameter( "passwordResetCode", passwordResetCode );
		Customer customer = customerDao.getFirstBeanResult().getSaveableBean();
		if( !getPassword().equals( getPasswordConfirm() ) ) {
			JSFUtil.addMessage("The passwords entered do not match.", FacesMessage.SEVERITY_ERROR);
		} else {
			customer.setPassword( getPassword() );
			customer.login();

			customer.putTemporaryTransactionInSession();
			customer.addToScope();
			//clear the reset code and date
			customer.setPasswordResetCode(null);
			customer.setPasswordResetDate(null);
			customer.login();
			customer.saveDetails();
			//redirect to change password screen
			JSFUtil.getFacesContext().getViewRoot().getViewMap().put( "passwordChanged", true );
		}
		return null;
	}

	public String goToCart() {
		EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutCartCpr();
		return null;
	}

	public String goToHome() {
		JSFUtil.redirect(new AplosUrl("/"), true);
		return null;
	}


	public String goToForgottenPassword() {
		EcommerceConfiguration.getEcommerceCprsStatic().redirectToCheckoutForgottenPasswordCpr();
		return null;
	}

	public String resetPassword() {
		//get the passwordResetCode from the url parameters
		String passwordResetCode = JSFUtil.getRequestParameter("passwordResetCode");
		if (passwordResetCode==null || passwordResetCode.equals("")) {
			JSFUtil.addMessage("No reset code has been specified. This code is required to reset a password.", FacesMessage.SEVERITY_ERROR);
		} else {
			//find the matching user
			BeanDao customerDao = new BeanDao( Customer.class );
			customerDao.addWhereCriteria( "bean.passwordResetCode=:passwordResetCode" );
			customerDao.setNamedParameter( "passwordResetCode", passwordResetCode );
			Customer cust = customerDao.getFirstBeanResult();
			if (cust==null) {
				JSFUtil.addMessage("The reset code provided was not recognised.", FacesMessage.SEVERITY_ERROR);
			} else {
				//ensure the code is in-date
				Date now = new Date();
				if (cust.getPasswordResetDate().compareTo(now) > 0) {
					//reset the password
					cust.setPassword( CommonUtil.generateRandomCode() );
					sendResetEmail( cust );
					//login
					cust.login();
					cust.addToScope();
					//clear the reset code and date
					cust.setPasswordResetCode(null);
					cust.setPasswordResetDate(null);
					cust.saveDetails();
					//redirect to change password screen

					JSFUtil.redirect(new CmsPageUrl(EcommerceConfiguration.getEcommerceCprsStatic().getCustomerPasswordCpr()), true);
				} else {
					//clear the reset code and date
					cust.setPasswordResetCode(null);
					cust.setPasswordResetDate(null);
					cust.saveDetails();
					JSFUtil.addMessage("The reset code provided has expired. You will need to resubmit a forgotten password request.", FacesMessage.SEVERITY_ERROR);
				}
			}
		}
		return null;
	}

	public void sendResetEmail( Customer customer ) {
		AplosEmail aplosEmail = new AplosEmail( EcommerceEmailTemplateEnum.RESET_PASSWORD, customer, customer );
		aplosEmail.sendAplosEmailToQueue();

	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}

	public String getPasswordConfirm() {
		return passwordConfirm;
	}
}



















