package com.aplos.ecommerce.backingpage.marketing;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.FriendReferral;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=FriendReferral.class)
public class FriendReferralListPage extends ListPage {
	private static final long serialVersionUID = -8449247539595275158L;

	public String getSentYesNoString() {
		FriendReferral friendReferral = (FriendReferral) JSFUtil.getRequest().getAttribute("tableBean");
		if (friendReferral != null && friendReferral.isSent()) {
			return "Yes";
		}
		return "No";
	}

	public String getCreditYesNoString() {
		FriendReferral friendReferral = (FriendReferral) JSFUtil.getRequest().getAttribute("tableBean");
		if (friendReferral != null && friendReferral.isVoucherAwarded()) {
			return "Yes";
		}
		return "No";
	}


}
