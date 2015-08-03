package com.aplos.ecommerce.backingpage;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.ListPage;
import com.aplos.ecommerce.beans.forum.ForumThread;

@ManagedBean
@ViewScoped
@AssociatedBean( beanClass=ForumThread.class )
public class ForumThreadListPage extends ListPage  {
	private static final long serialVersionUID = -211676200806543771L;
}
