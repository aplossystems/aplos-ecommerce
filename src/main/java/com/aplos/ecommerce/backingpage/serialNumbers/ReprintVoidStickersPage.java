package com.aplos.ecommerce.backingpage.serialNumbers;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.AplosUrl;
import com.aplos.common.backingpage.BackingPage;
import com.aplos.common.beans.Website;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.templates.printtemplates.VoidStickersTemplate;

@ManagedBean
@ViewScoped
public class ReprintVoidStickersPage extends BackingPage {
	private static final long serialVersionUID = -3254211800912984599L;

	private long firstSerialNumber;

	public ReprintVoidStickersPage() {
	}

	public void setFirstSerialNumber(long firstSerialNumber) {
		this.firstSerialNumber = firstSerialNumber;
	}

	public long getFirstSerialNumber() {
		return firstSerialNumber;
	}

	public String reprintVoidStickers() {
		JSFUtil.redirect(new AplosUrl(VoidStickersTemplate.getTemplateUrl( getFirstSerialNumber(), getFirstSerialNumber() + 64 )), true);
		return null;
	}

	public String redirectToSerialNumberAssignmentList() {
		JSFUtil.redirect(Website.getCurrentWebsiteFromTabSession().getPackageName(), SerialNumberListPage.class);
		return null;
	}



}
