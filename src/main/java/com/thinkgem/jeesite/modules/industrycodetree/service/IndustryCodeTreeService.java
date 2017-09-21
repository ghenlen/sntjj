/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.industrycodetree.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.service.ServiceException;
import com.thinkgem.jeesite.common.service.TreeService;
import com.thinkgem.jeesite.common.utils.Reflections;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.industrycodetree.entity.IndustryCodeTree;
import com.thinkgem.jeesite.modules.industrycodetree.dao.IndustryCodeTreeDao;

/**
 * 行业代码树Service
 * @author ghl
 * @version 2017-09-14
 */
@Service
@Transactional(readOnly = true)
public class IndustryCodeTreeService extends TreeService<IndustryCodeTreeDao, IndustryCodeTree> {
	
	@Autowired
	private IndustryCodeTreeDao industryCodeTreeDao;
	public IndustryCodeTree get(String id) {
		return super.get(id);
	}
	
	public List<IndustryCodeTree> findList(IndustryCodeTree industryCodeTree) {
		if (StringUtils.isNotBlank(industryCodeTree.getParentIds())){
			industryCodeTree.setParentIds(","+industryCodeTree.getParentIds()+",");
		}
		return super.findList(industryCodeTree);
	}
	
	public List<IndustryCodeTree> findAllList(IndustryCodeTree industryCodeTree) {
		return industryCodeTreeDao.findAllList(industryCodeTree);
	}
	
	@Transactional(readOnly = false)
	public void save(IndustryCodeTree industryCodeTree) {
		if(!"".equals(industryCodeTree.getParent().getId())){
			IndustryCodeTree parent=super.get(industryCodeTree.getParent().getId());
			industryCodeTree.setSort(parent.getSort()+1);
		}else{
			industryCodeTree.setSort(1);
		}
		super.save(industryCodeTree);
	}
	
	@Transactional(readOnly = false)
	public void delete(IndustryCodeTree industryCodeTree) {
		super.delete(industryCodeTree);
	}
	
	@Transactional(readOnly = false)
	public Map<String,Object> uploadFile(IndustryCodeTree industryCodeTree) {
		Map<String,Object> map=new HashMap<String,Object>();
		save3(industryCodeTree);
		return map;
	}
	
	public void save3(IndustryCodeTree entity) {
		
		@SuppressWarnings("unchecked")
		Class<IndustryCodeTree> entityClass = Reflections.getClassGenricType(getClass(), 1);
		
		// 如果没有设置父节点，则代表为跟节点，有则获取父节点实体
		if (entity.getParent() == null || StringUtils.isBlank(entity.getParentId()) 
				|| "0".equals(entity.getParentId())){
			entity.setParent(null);
		}else{
			entity.setParent(super.get(entity.getParentId()));
		}
		if (entity.getParent() == null){
			IndustryCodeTree parentEntity = null;
			try {
				parentEntity = entityClass.getConstructor(String.class).newInstance("0");
			} catch (Exception e) {
				throw new ServiceException(e);
			}
			entity.setParent(parentEntity);
			entity.getParent().setParentIds(StringUtils.EMPTY);
		}
		
		// 获取修改前的parentIds，用于更新子节点的parentIds
		String oldParentIds = entity.getParentIds(); 
		
		// 设置新的父节点串
		entity.setParentIds(entity.getParent().getParentIds()+entity.getParent().getId()+",");
		
		// 保存或更新实体
		save2(entity);
		
		// 更新子节点 parentIds
		IndustryCodeTree o = null;
		try {
			o = entityClass.newInstance();
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		o.setParentIds("%,"+entity.getId()+",%");
		List<IndustryCodeTree> list = dao.findByParentIdsLike(o);
		for (IndustryCodeTree e : list){
			if (e.getParentIds() != null && oldParentIds != null){
				e.setParentIds(e.getParentIds().replace(oldParentIds, entity.getParentIds()));
				preUpdateChild(entity, e);
				dao.updateParentIds(e);
			}
		}
		
	}
	
	public void save2(IndustryCodeTree entity) {
		entity.setCreateDate(new Date());
		entity.setUpdateDate(new Date());
		dao.insert(entity);
		/*if (entity.getIsNewRecord()){
			entity.preInsert();
			dao.insert(entity);
		}else{
			entity.preUpdate();
			dao.update(entity);
		}*/
	}
	
}