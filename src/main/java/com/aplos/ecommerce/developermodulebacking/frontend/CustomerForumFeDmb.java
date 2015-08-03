package com.aplos.ecommerce.developermodulebacking.frontend;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.aplos.cms.CmsPageUrl;
import com.aplos.cms.beans.developercmsmodules.DeveloperCmsAtom;
import com.aplos.cms.developermodulebacking.DeveloperModuleBacking;
import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.BackingPage;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.interfaces.ForumUser;
import com.aplos.common.utils.CommonUtil;
import com.aplos.common.utils.FormatUtil;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.Customer;
import com.aplos.ecommerce.beans.forum.ForumPost;
import com.aplos.ecommerce.beans.forum.ForumThread;
import com.aplos.ecommerce.module.EcommerceConfiguration;

@ManagedBean
@ViewScoped
public class CustomerForumFeDmb extends DeveloperModuleBacking {

	private static final long serialVersionUID = 4801359400898663172L;

	private ForumThread currentThread = null;
	private ForumPost currentPost = null;
	private ForumLazyDataModel forumLazyDataModel;

	public CustomerForumFeDmb() {
		DataTableState dataTableState = CommonUtil.createDataTableState( this, ForumLazyDataModel.class );
		setForumLazyDataModel(new ForumLazyDataModel( dataTableState, new BeanDao( ForumThread.class ) ));
		dataTableState.setLazyDataModel( getForumLazyDataModel() );
		dataTableState.setShowingIdColumn( false );
		getForumLazyDataModel().setPageSize(100);
	}

	@Override
	public boolean requestPageLoad(DeveloperCmsAtom developerCmsAtom) {
		super.requestPageLoad(developerCmsAtom);
		getForumLazyDataModel().setPageSize(100);
		return true;
	}

	public ForumUser getForumUser() {
		ForumUser user = null;
		Customer customer = JSFUtil.getBeanFromScope(Customer.class);
		if (customer == null || !customer.isLoggedIn()) {
			SystemUser systemUser = JSFUtil.getLoggedInUser();
			if (systemUser != null && systemUser.isLoggedIn()) {
				user=systemUser;
			}
		} else {
			user=customer;
		}
		return user;
	}

	public void setCurrentThread(ForumThread currentThread) {
		this.currentThread = currentThread;
	}

	public ForumThread getCurrentThread() {
		return currentThread;
	}

	public void setCurrentPost(ForumPost currentPost) {
		this.currentPost = currentPost;
	}

	public ForumPost getCurrentPost() {
		return currentPost;
	}

	public String getLastPostText() {
		ForumThread thread = (ForumThread) JSFUtil.getRequest().getAttribute( "tableBean" );
		ForumUser user = getForumUser();
		if (thread != null && user != null) {
			String dateStr="No Posts";
			if (thread.getLastPostTime() != null) {
				dateStr = FormatUtil.formatDate(FormatUtil.getEngSlashSimpleDateTimeFormat(),thread.getLastPostTime());
			}
			if (!user.isLoggedIn() || thread.getViewedByIds().contains(user.getId())) {
				return "<span class=\"aplos-forum-viewed\">" + dateStr + "</span>";
			} else {
				return "<strong title=\"You have not read the latest posts in this thread\" class=\"aplos-forum-new\">" + dateStr + "</strong>";
			}
		}
		return "";
	}

	public String addPost() {
		if (currentPost != null && currentThread != null && getForumUser() != null) {
			currentPost.setPoster(getForumUser());
			currentPost.saveDetails(); // we need date created
			currentThread.addPost(currentPost);
			currentPost = null;
		}
		return null;
	}

	public String getDatePostedStr() {
		ForumPost post = (ForumPost) JSFUtil.getRequest().getAttribute("forumPost");
		if (post != null && post.getDateCreated() != null) {
			return FormatUtil.formatDate(FormatUtil.getEngSlashSimpleDateTimeFormat(),post.getDateCreated());
		}
		return "";
	}

	public boolean isValidationRequired() {
		return BackingPage.validationRequired("submitForumPost");
	}

	public String cancelReadThread() {
		currentThread = null;
		currentPost = null;
		return null;
	}

	public String cancelPost() {
		if (currentThread != null && !currentThread.isNew()){
			currentPost = null;
		} else {
			currentThread = null;
			currentPost = null;
		}
		return null;
	}

	public String addReply() {
		SystemUser systemUser = JSFUtil.getLoggedInUser();
		Customer customer = JSFUtil.getBeanFromScope(Customer.class);
		if ((customer != null && customer.isLoggedIn()) || (systemUser != null && systemUser.isLoggedIn())) {
			if (currentPost == null && currentThread != null) {
				currentPost = new ForumPost();
			}
		} else {
			JSFUtil.addMessage("You must be logged in to reply.");
			//redirect
			String thisSafeUrl = JSFUtil.getAplosContextOriginalUrl();
			if (thisSafeUrl.startsWith("/")) {
				thisSafeUrl = thisSafeUrl.substring(1);
			}
			JSFUtil.redirect(new CmsPageUrl(EcommerceConfiguration.getEcommerceCprsStatic().getCheckoutSignInOrSignUpCpr().getCmsPage(), "location=" + thisSafeUrl), true);
		}
		return null;
	}

	public ForumLazyDataModel getForumLazyDataModel() {
		return forumLazyDataModel;
	}

	public void setForumLazyDataModel(ForumLazyDataModel forumLazyDataModel) {
		this.forumLazyDataModel = forumLazyDataModel;
	}

	@Override
	public AplosLazyDataModel getAplosLazyDataModel( DataTableState dataTableState, BeanDao aqlBeanDao ) {
		return new ForumLazyDataModel( dataTableState, aqlBeanDao );
	}

	public class ForumLazyDataModel extends AplosLazyDataModel {

		private static final long serialVersionUID = 5657851797331665280L;

		public ForumLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
		}

		@Override
		public void selectBean() {
			ForumThread selectedhread = (ForumThread) JSFUtil.getRequest().getAttribute( "tableBean" );
			currentThread = getBeanDao().get(selectedhread.getId());
			//TODO: I've just realised the sysusr and customer id's are going to clash and mark eachother as having been read
			if (getForumUser() != null){
				currentThread.getViewedByIds().add(getForumUser().getId());
				currentThread.saveDetails();
			}
			currentPost = null;
		}

		@Override
		public void goToNew() {
			SystemUser systemUser = JSFUtil.getLoggedInUser();
			Customer customer = JSFUtil.getBeanFromScope(Customer.class);
			if ((customer != null && customer.isLoggedIn()) || (systemUser != null && systemUser.isLoggedIn())) {
				currentThread = new ForumThread();
				currentPost = new ForumPost();
			} else {
				JSFUtil.addMessage("You must be logged in to post a new discussion.");
				//redirect
				String thisSafeUrl = JSFUtil.getAplosContextOriginalUrl();
				if (thisSafeUrl.startsWith("/")) {
					thisSafeUrl = thisSafeUrl.substring(1);
				}
				JSFUtil.redirect(new CmsPageUrl(EcommerceConfiguration.getEcommerceCprsStatic().getCheckoutSignInOrSignUpCpr().getCmsPage(),"location=" + thisSafeUrl), true);

			}
		}

	}

}
