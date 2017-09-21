/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.industrycodetree.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.TreeEntity;

/**
 * 行业代码树Entity
 * @author ghl
 * @version 2017-09-14
 */
public class IndustryCodeTree extends TreeEntity<IndustryCodeTree> {
	
	private static final long serialVersionUID = 1L;
	private IndustryCodeTree parent;		// parent_id
	private String parentIds;		// parent_ids
	private String name;		// name
	
	public IndustryCodeTree() {
		super();
	}

	public IndustryCodeTree(String id){
		super(id);
	}

	@JsonBackReference
	@NotNull(message="parent_id不能为空")
	public IndustryCodeTree getParent() {
		return parent;
	}

	public void setParent(IndustryCodeTree parent) {
		this.parent = parent;
	}
	
	@Length(min=1, max=2000, message="parent_ids长度必须介于 1 和 2000 之间")
	public String getParentIds() {
		return parentIds;
	}

	public void setParentIds(String parentIds) {
		this.parentIds = parentIds;
	}
	
	@Length(min=1, max=100, message="name长度必须介于 1 和 100 之间")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getParentId() {
		return parent != null && parent.getId() != null ? parent.getId() : "0";
	}
}