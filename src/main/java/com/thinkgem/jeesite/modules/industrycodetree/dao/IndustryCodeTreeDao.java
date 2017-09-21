/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.industrycodetree.dao;

import java.util.List;

import com.thinkgem.jeesite.common.persistence.TreeDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.industrycodetree.entity.IndustryCodeTree;

/**
 * 行业代码树DAO接口
 * @author ghl
 * @version 2017-09-14
 */
@MyBatisDao
public interface IndustryCodeTreeDao extends TreeDao<IndustryCodeTree> {
	public List<IndustryCodeTree> findAllList(IndustryCodeTree industryCodeTree);
}