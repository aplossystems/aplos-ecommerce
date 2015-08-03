package com.aplos.ecommerce.beans.developercmsmodules;

import com.aplos.cms.beans.developercmsmodules.ConfigurableDeveloperCmsAtom;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.common.annotations.DynamicMetaValueKey;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.enums.SslProtocolEnum;
import com.aplos.ecommerce.beans.Customer;

@Entity
@DynamicMetaValueKey(oldKey="MY_DETAILS_MODULE")
public class MyDetailsModule extends ConfigurableDeveloperCmsAtom {

	private static final long serialVersionUID = -3253221168586490039L;

	private Boolean includeFullName=true;
	private Boolean includeEmail=false;
	private Boolean includeCompanyName=false;
	private Boolean includeVatNumber=true;
	@ManyToOne
	private Customer userToDetail=null;

	public MyDetailsModule() {
		super();
	}

	@Override
	public String getName() {
		return "Customer Account : My Details";
	}

	@Override
	public DeveloperCmsAtom getCopy() {
		MyDetailsModule copiedAtom = new MyDetailsModule();
		copiedAtom.setIncludeFullName(includeFullName);
		copiedAtom.setIncludeEmail(includeEmail);
		copiedAtom.setIncludeCompanyName(includeCompanyName);
		copiedAtom.setIncludeVatNumber(includeVatNumber);
		copiedAtom.setUserToDetail(userToDetail);
		return copiedAtom;
	}
	
	@Override
	public SslProtocolEnum getSslProtocolEnum() {
		return SslProtocolEnum.FORCE_SSL;
	}

	public void setUserToDetail(Customer userToDetail) {
		this.userToDetail = userToDetail;
	}

	/**
	 * views should call the method of the same name in myDetailsFeDmb
	 * @see @MyDetailsFeDmb#getUserToDetail()
	 **/
	public Customer getUserToDetail() {
		return userToDetail;
	}

	public void setIncludeVatNumber(Boolean includeVatNumber) {
		this.includeVatNumber = includeVatNumber;
	}

	public Boolean getIncludeVatNumber() {
		return includeVatNumber;
	}

	public void setIncludeCompanyName(Boolean includeCompanyName) {
		this.includeCompanyName = includeCompanyName;
	}

	public Boolean getIncludeCompanyName() {
		return includeCompanyName;
	}

	public void setIncludeFullName(Boolean includeFullName) {
		this.includeFullName = includeFullName;
	}

	public Boolean getIncludeFullName() {
		return includeFullName;
	}

	public void setIncludeEmail(Boolean includeEmail) {
		this.includeEmail = includeEmail;
	}

	public Boolean getIncludeEmail() {
		return includeEmail;
	}


}
