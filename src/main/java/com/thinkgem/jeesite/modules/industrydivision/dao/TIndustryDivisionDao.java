/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.industrydivision.dao;

import java.util.List;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.industrycodetree.entity.IndustryCodeTree;
import com.thinkgem.jeesite.modules.industrydivision.entity.TIndustryDivision;
import com.thinkgem.jeesite.modules.sys.entity.User;

/**
 * 行业划分DAO接口
 * @author ghl
 * @version 2017-09-19
 */
@MyBatisDao
public interface TIndustryDivisionDao extends CrudDao<TIndustryDivision> {
	/**
	 * 维护行业类别与行业代码权限关系
	 * @param tIndustryDivision
	 * @return
	 */
	public int deleteDivisionIndustryCode(TIndustryDivision tIndustryDivision);

	public int insertDivisionIndustryCode(TIndustryDivision tIndustryDivision);

	public List<IndustryCodeTree> findIndustryCodeListByDivision(String divisionId); 
}