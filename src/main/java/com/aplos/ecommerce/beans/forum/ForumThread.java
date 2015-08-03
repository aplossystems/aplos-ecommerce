package com.aplos.ecommerce.beans.forum;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ViewScoped;

import com.aplos.common.annotations.persistence.Cache;
import com.aplos.common.annotations.persistence.Cascade;
import com.aplos.common.annotations.persistence.CascadeType;
import com.aplos.common.annotations.persistence.CollectionOfElements;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.IndexColumn;
import com.aplos.common.annotations.persistence.OneToMany;
import com.aplos.common.aql.BeanDao;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.beans.SystemUser;
import com.aplos.common.utils.CommonUtil;

@Entity
@ViewScoped
@Cache
public class ForumThread extends AplosBean {

	private static final long serialVersionUID = -3183430080135006008L;

	private String topic;
	private String mapping; //we store a mapping so we can have unique url's when/if we need them
							//I've not implemented it yet because then viewing a thread would mean changing to
							//a page themed as backend or creating a different atom for view thread
							//to go on another cms page. I may have missed a trick and there's a simpler way though.
	@OneToMany
	@IndexColumn(name="idx")
	@Cascade({CascadeType.ALL})
	private List<ForumPost> posts = new ArrayList<ForumPost>();
	@CollectionOfElements
	private List<Long> viewedByIds = new ArrayList<Long>();
	private Date lastPostTime=null; //dateLastModified will be incorrect due to viewedByIds

	public ForumThread() { 	}

	public String addPost(ForumPost newPost) {
		posts.add(newPost);
		viewedByIds = new ArrayList<Long>();
		viewedByIds.add(newPost.getPoster().getId());
		lastPostTime = new Date();
		saveDetails();
		return null;
	}

	public void setMapping(String mapping) {
		this.mapping = mapping;
	}

	public String getMapping() {
		return mapping;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getTopic() {
		return topic;
	}

	public void setPosts(List<ForumPost> posts) {
		this.posts = posts;
	}

	public List<ForumPost> getPosts() {
		return posts;
	}

	public void setViewedByIds(List<Long> viewedByIds) {
		this.viewedByIds = viewedByIds;
	}

	public List<Long> getViewedByIds() {
		return viewedByIds;
	}

	@Override
	public void saveBean( SystemUser currentUser ) {
		if (getMapping() == null || getMapping().equals( "" )) {
			setMapping( createMapping());
		} else {
			//remove anything nasty
			setMapping( CommonUtil.makeSafeUrlMapping( getMapping() ) );
			if (getMapping() == null || getMapping().equals( "" )) {
				setMapping( createMapping());
			}
		}
		super.saveBean(currentUser);
	}

	public String createMapping() {
		String topic = getTopic();
		String _mapping = CommonUtil.makeSafeUrlMapping(topic);
		BeanDao aqlBeanDao = new BeanDao(ForumThread.class);
		aqlBeanDao.setSelectCriteria( "bean.mapping" );
		aqlBeanDao.addWhereCriteria( "bean.mapping LIKE :mapping" );
		aqlBeanDao.setNamedParameter( "mapping", mapping + "%" );
		List<String> mappings = aqlBeanDao.getResultFields();

		int count = 0;
		String newmapping = _mapping;
		while (mappings.contains( newmapping )) {
			newmapping = _mapping.concat( String.valueOf(++count) );
		}

		return newmapping;
	}

	public void setLastPostTime(Date lastPostTime) {
		this.lastPostTime = lastPostTime;
	}

	public Date getLastPostTime() {
		return lastPostTime;
	}

}