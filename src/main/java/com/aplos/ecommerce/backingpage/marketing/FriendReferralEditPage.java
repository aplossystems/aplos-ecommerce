package com.aplos.ecommerce.backingpage.marketing;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.EditPage;
import com.aplos.ecommerce.beans.FriendReferral;

@ManagedBean
@ViewScoped
@AssociatedBean(beanClass=FriendReferral.class)
public class FriendReferralEditPage extends EditPage {
	private static final long serialVersionUID = 7459121432272508292L;

}





