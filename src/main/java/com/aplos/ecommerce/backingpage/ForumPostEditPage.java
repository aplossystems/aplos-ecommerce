package com.aplos.ecommerce.backingpage;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.backingpage.EditPage;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.forum.ForumPost;
import com.aplos.ecommerce.beans.forum.ForumThread;

@ManagedBean
@ViewScoped
@AssociatedBean( beanClass=ForumPost.class )
public class ForumPostEditPage extends EditPage {

	private static final long serialVersionUID = 6423470729304983454L;

	@Override
	public void okBtnAction() {
		ForumPost forumPost = resolveAssociatedBean();
		if (forumPost.isNew()) {
			forumPost.setPoster(JSFUtil.getLoggedInUser());
			forumPost.saveDetails();
			ForumThread forumThread = JSFUtil.getBeanFromScope(ForumThread.class);
			forumThread.getPosts().add(forumPost);
			forumThread.saveDetails();
		} else {
			forumPost.saveDetails();
		}
		navigateToPreviousPage();
	}
}
