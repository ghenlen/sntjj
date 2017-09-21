/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.industrydivision.entity;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.persistence.DataEntity;
import com.thinkgem.jeesite.common.utils.excel.annotation.ExcelField;
import com.thinkgem.jeesite.modules.industrycodetree.entity.IndustryCodeTree;

/**
 * 行业划分Entity
 * @author ghl
 * @version 2017-09-19
 */
public class TIndustryDivision extends DataEntity<TIndustryDivision> {
	
	private static final long serialVersionUID = 1L;
	private String name;		// 名称
	private List<IndustryCodeTree> industryCodeList=Lists.newArrayList();//拥有行业列表
	
	public TIndustryDivision() {
		super();
	}

	public TIndustryDivision(String id){
		super(id);
	}

	@Length(min=0, max=128, message="名称长度必须介于 0 和 128 之间")
	@ExcelField(title="名称", align=2, sort=1)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<IndustryCodeTree> getIndustryCodeList() {
		return industryCodeList;
	}

	public void setIndustryCodeList(List<IndustryCodeTree> industryCodeList) {
		this.industryCodeList = industryCodeList;
	}
	
	public List<String> getIndustryCodeIdList() {
		List<String> industryCodeIdList = Lists.newArrayList();
		for (IndustryCodeTree industryCodeTree : industryCodeList) {
			industryCodeIdList.add(industryCodeTree.getId());
		}
		return industryCodeIdList;
	}

	public void setIndustryCodeIdList(List<String> industryCodeIdList) {
		industryCodeList = Lists.newArrayList();
		for (String menuId : industryCodeIdList) {
			IndustryCodeTree industryCodeTree = new IndustryCodeTree();
			industryCodeTree.setId(menuId);
			industryCodeList.add(industryCodeTree);
		}
	}

	
	
	public String getIndustryCodeIds() {
		return StringUtils.join(getIndustryCodeIdList(), ",");
	}
	
	
	public void setIndustryCodeIds(String industryCodeIds) {
		industryCodeList = Lists.newArrayList();
		if (industryCodeIds != null){
			String[] ids = StringUtils.split(industryCodeIds, ",");
			setIndustryCodeIdList(Lists.newArrayList(ids));
		}
	}
	
	
}