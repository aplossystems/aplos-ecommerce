package com.aplos.ecommerce.beans.amazon;

import com.aplos.common.annotations.persistence.Entity;
import com.aplos.common.annotations.persistence.FetchType;
import com.aplos.common.annotations.persistence.ManyToOne;
import com.aplos.common.beans.AplosBean;

@Entity
public class AmazonBrowseNode extends AplosBean {
	private static final long serialVersionUID = 252174297424037993L;
	
	private Long nodeId;
	private String name;
	@ManyToOne(fetch=FetchType.LAZY)
	private AmazonBrowseNode parentNode;
	/*
	 *  This is a bit of a temporary hack to help find nodes which aren't parent
	 *  node.  This should be an easy SQL statement but with hibernate it cannot
	 *  be done as you can't use the ON statement for left outer join.
	 */
	private boolean isAParentNode = false;
	
	public String getDisplayName() {
		return name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getNodeId() {
		return nodeId;
	}

	public void setNodeId(Long nodeId) {
		this.nodeId = nodeId;
	}

	public AmazonBrowseNode getParentNode() {
		return parentNode;
	}

	public void setParentNode(AmazonBrowseNode parentNode) {
		this.parentNode = parentNode;
	}

	public boolean isAParentNode() {
		return isAParentNode;
	}

	public void setAParentNode(boolean isAParentNode) {
		this.isAParentNode = isAParentNode;
	}
}
