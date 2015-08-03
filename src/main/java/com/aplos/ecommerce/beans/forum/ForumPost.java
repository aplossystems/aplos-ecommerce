package com.aplos.ecommerce.beans.forum;

import javax.faces.bean.ViewScoped;

import com.aplos.common.annotations.DynamicMetaValues;
import com.aplos.common.annotations.persistence.Any;
import com.aplos.common.annotations.persistence.AnyMetaDef;
import com.aplos.common.annotations.persistence.Cache;
import com.aplos.common.annotations.persistence.Cascade;
import com.aplos.common.annotations.persistence.CascadeType;
import com.aplos.common.annotations.persistence.Column;
import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.JoinColumn;
import com.aplos.common.beans.AplosBean;
import com.aplos.common.interfaces.ForumUser;

@Entity
@ViewScoped
@Cache
public class ForumPost extends AplosBean {
	private static final long serialVersionUID = 1149148889658286199L;

	@Any( metaColumn = @Column( name = "poster_type" ) )
    @AnyMetaDef( idType = "long", metaType = "string", metaValues = {
    		/* Meta Values added in at run-time */ } )
    @JoinColumn( name = "poster_id" )
    @Cascade({CascadeType.ALL})
	@DynamicMetaValues
	private ForumUser poster;
	@Column(columnDefinition="LONGTEXT")
	private String message;

	public ForumPost() {

	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setPoster(ForumUser poster) {
		this.poster = poster;
	}

	public ForumUser getPoster() {
		return poster;
	}

}