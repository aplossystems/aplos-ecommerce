package com.aplos.ecommerce.backingpage;

import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.SortOrder;

import com.aplos.common.AplosLazyDataModel;
import com.aplos.common.annotations.AssociatedBean;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.backingpage.ListPage;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.DataTableState;
import com.aplos.common.utils.JSFUtil;
import com.aplos.ecommerce.beans.forum.ForumPost;
import com.aplos.ecommerce.beans.forum.ForumThread;

@ManagedBean
@ViewScoped
@AssociatedBean( beanClass=ForumPost.class )
public class ForumPostListPage extends ListPage  {

	private static final long serialVersionUID = 8016474051749380878L;

	@Override
	public AplosLazyDataModel getAplosLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
		return new ForumPostLazyDataModel(dataTableState, aqlBeanDao);
	}

	public class ForumPostLazyDataModel extends AplosLazyDataModel {

		private static final long serialVersionUID = -8248787815350622296L;

		public ForumPostLazyDataModel(DataTableState dataTableState, BeanDao aqlBeanDao) {
			super(dataTableState, aqlBeanDao);
			aqlBeanDao.setOrderBy("bean.dateCreated ASC");
		}
		
		@Override
		public List<Object> load(int first, int pageSize, String sortField,
				SortOrder sortOrder, Map<String, String> filters) {
			ForumThread forumThread = JSFUtil.getBeanFromScope( ForumThread.class );
			getBeanDao().setWhereCriteria( "bean.id IN (SELECT post.id FROM " + AplosBean.getTableName(ForumThread.class) + " thread LEFT OUTER JOIN thread.posts post WHERE thread.id=" + forumThread.getId() + ")" );
			return super.load(first, pageSize, sortField, sortOrder, filters);
		}

	}

}
