/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.industrydivision.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.industrycodetree.entity.IndustryCodeTree;
import com.thinkgem.jeesite.modules.industrydivision.entity.TIndustryDivision;
import com.thinkgem.jeesite.modules.industrydivision.dao.TIndustryDivisionDao;
import com.thinkgem.jeesite.modules.sys.entity.User;

/**
 * 行业划分Service
 * @author ghl
 * @version 2017-09-19
 */
@Service
@Transactional(readOnly = true)
public class TIndustryDivisionService extends CrudService<TIndustryDivisionDao, TIndustryDivision> {
	@Autowired
	private TIndustryDivisionDao tIndustryDivisionDao;
	public TIndustryDivision get(String id) {
		return super.get(id);
	}
	
	public List<TIndustryDivision> findList(TIndustryDivision tIndustryDivision) {
		return super.findList(tIndustryDivision);
	}
	
	public Page<TIndustryDivision> findPage(Page<TIndustryDivision> page, TIndustryDivision tIndustryDivision) {
		return super.findPage(page, tIndustryDivision);
	}
	
	@Transactional(readOnly = false)
	public void save(TIndustryDivision tIndustryDivision) {
		
		super.save(tIndustryDivision);
	}
	
	@Transactional(readOnly = false)
	public void delete(TIndustryDivision tIndustryDivision) {
		super.delete(tIndustryDivision);
	}
	
	public List<IndustryCodeTree> findIndustryCodeListByDivision(String divisionId){
		return tIndustryDivisionDao.findIndustryCodeListByDivision(divisionId);
	}
	///////////////// Synchronized to the Activiti end //////////////////
	@Transactional(readOnly = false)
	public void updateIndustryCode( TIndustryDivision tIndustryDivision) {
		tIndustryDivisionDao.deleteDivisionIndustryCode(tIndustryDivision);
		if (tIndustryDivision.getIndustryCodeList().size() > 0){
			tIndustryDivisionDao.insertDivisionIndustryCode(tIndustryDivision);
		}					
	}
	
}