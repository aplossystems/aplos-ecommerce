package com.aplos.ecommerce.backingpage;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.EditPage;
import com.aplos.ecommerce.beans.forum.ForumThread;

@ManagedBean
@ViewScoped
@AssociatedBean( beanClass=ForumThread.class )
public class ForumThreadEditPage extends EditPage {
	private static final long serialVersionUID = -8487878113856566624L;

}
