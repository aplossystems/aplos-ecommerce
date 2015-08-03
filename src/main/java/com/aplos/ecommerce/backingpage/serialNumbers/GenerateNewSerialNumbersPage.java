package com.aplos.ecommerce.backingpage.serialNumbers;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.aplos.common.AplosUrl;
import com.aplos.common.backingpage.BackingPage;
import com.aplos.common.beans.Website;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.SerialNumber;
import com.aplos.ecommerce.templates.printtemplates.VoidStickersTemplate;

@ManagedBean
@SessionScoped
public class GenerateNewSerialNumbersPage extends BackingPage {
	private static final long serialVersionUID = 6760166271205861864L;

	private boolean areVoidStickersRequired;

	private List<SerialNumber> serialNumberAssignments;

	@Override
	public boolean responsePageLoad() {
		serialNumberAssignments = new ArrayList<SerialNumber>();
		SerialNumber serialNumberAssignment;
		for (int i=0 ; i<65 ; i++) {
			serialNumberAssignment = new SerialNumber();
			serialNumberAssignment.setIsVoidStickerRequired(areVoidStickersRequired);
			serialNumberAssignment.saveDetails();
			serialNumberAssignments.add(serialNumberAssignment);
		}
		return true;
	}

	public Long getFirstSerialNumberGenerated() {
		return serialNumberAssignments.get(0).getId();
	}
	public Long getLastSerialNumberGenerated() {
		return serialNumberAssignments.get(serialNumberAssignments.size() - 1).getId();
	}

	public String printVoidStickers() {
		JSFUtil.redirect(new AplosUrl(VoidStickersTemplate.getTemplateUrl( serialNumberAssignments.get(0).getId(), serialNumberAssignments.get(64).getId() )), true);
		return null;
	}

	public String redirectToSerialNumberAssignmentList() {
		JSFUtil.redirect(Website.getCurrentWebsiteFromTabSession().getPackageName(), SerialNumberListPage.class);
		return null;
	}

	public void setAreVoidStickersRequired(boolean areVoidStickersRequired) {
		this.areVoidStickersRequired = areVoidStickersRequired;
	}

	public boolean isAreVoidStickersRequired() {
		return areVoidStickersRequired;
	}

}
