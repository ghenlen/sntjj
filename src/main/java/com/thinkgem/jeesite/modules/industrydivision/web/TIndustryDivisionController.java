/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.industrydivision.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.utils.excel.ExportExcel;
import com.thinkgem.jeesite.common.utils.excel.ImportExcel;
import com.thinkgem.jeesite.modules.industrycodetree.entity.IndustryCodeTree;
import com.thinkgem.jeesite.modules.industrycodetree.service.IndustryCodeTreeService;
import com.thinkgem.jeesite.modules.industrydivision.entity.TIndustryDivision;
import com.thinkgem.jeesite.modules.industrydivision.service.TIndustryDivisionService;

/**
 * 行业划分Controller
 * @author ghl
 * @version 2017-09-19
 */
@Controller
@RequestMapping(value = "${adminPath}/industrydivision/tIndustryDivision")
public class TIndustryDivisionController extends BaseController {

	@Autowired
	private TIndustryDivisionService tIndustryDivisionService;
	@Autowired
	private IndustryCodeTreeService industryCodeTreeService;
	
	@ModelAttribute
	public TIndustryDivision get(@RequestParam(required=false) String id) {
		TIndustryDivision entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = tIndustryDivisionService.get(id);
		}
		if (entity == null){
			entity = new TIndustryDivision();
		}
		return entity;
	}
	
	/**
	 * 行业划分列表页面
	 */
	@RequiresPermissions("industrydivision:tIndustryDivision:view")
	@RequestMapping(value = {"list", ""})
	public String list(TIndustryDivision tIndustryDivision, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<TIndustryDivision> page = tIndustryDivisionService.findPage(new Page<TIndustryDivision>(request, response), tIndustryDivision); 
		model.addAttribute("tIndustryDivision", tIndustryDivision);
		model.addAttribute("page", page);
		return "modules/industrydivision/tIndustryDivisionList";
	}

	/**
	 * 查看，增加，编辑行业划分表单页面
	 */
	@RequiresPermissions(value={"industrydivision:tIndustryDivision:edit"})
	@RequestMapping(value = "form")
	public String form(TIndustryDivision tIndustryDivision, Model model) {
		tIndustryDivision.setIndustryCodeList(tIndustryDivisionService.findIndustryCodeListByDivision(tIndustryDivision.getId()));
		model.addAttribute("tIndustryDivision", tIndustryDivision);
		IndustryCodeTree industryCodeTree=new IndustryCodeTree();
		industryCodeTree.setSort(Global.DIVISIONINDUSTRY);
		model.addAttribute("industryCodes", industryCodeTreeService.findAllList(industryCodeTree));
		return "modules/industrydivision/tIndustryDivisionForm";
	}

	/**
	 * 保存行业划分
	 */
	@RequiresPermissions(value={"industrydivision:tIndustryDivision:edit"})
	@RequestMapping(value = "save")
	public String save(TIndustryDivision tIndustryDivision, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, tIndustryDivision)){
			return form(tIndustryDivision, model);
		}
		tIndustryDivisionService.save(tIndustryDivision);
		tIndustryDivisionService.updateIndustryCode(tIndustryDivision);
		addMessage(redirectAttributes, "保存行业划分成功");
		return "redirect:"+Global.getAdminPath()+"/industrydivision/tIndustryDivision/?repage";
	}
	
	/**
	 * 删除行业划分
	 */
	@RequiresPermissions("industrydivision:tIndustryDivision:del")
	@RequestMapping(value = "delete")
	public String delete(TIndustryDivision tIndustryDivision, RedirectAttributes redirectAttributes) {
		tIndustryDivisionService.delete(tIndustryDivision);
		addMessage(redirectAttributes, "删除行业划分成功");
		return "redirect:"+Global.getAdminPath()+"/industrydivision/tIndustryDivision/?repage";
	}
	
	/**
	 * 批量删除行业划分
	 */
	@RequiresPermissions("industrydivision:tIndustryDivision:del")
	@RequestMapping(value = "deleteAll")
	public String deleteAll(String ids, RedirectAttributes redirectAttributes) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			tIndustryDivisionService.delete(tIndustryDivisionService.get(id));
		}
		addMessage(redirectAttributes, "删除行业划分成功");
		return "redirect:"+Global.getAdminPath()+"/industrydivision/tIndustryDivision/?repage";
	}
	
	/**
	 * 导出excel文件
	 */
	@RequiresPermissions("industrydivision:tIndustryDivision:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public String exportFile(TIndustryDivision tIndustryDivision, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "行业划分"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<TIndustryDivision> page = tIndustryDivisionService.findPage(new Page<TIndustryDivision>(request, response, -1), tIndustryDivision);
    		new ExportExcel("行业划分", TIndustryDivision.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出行业划分记录失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/industrydivision/tIndustryDivision/?repage";
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("industrydivision:tIndustryDivision:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<TIndustryDivision> list = ei.getDataList(TIndustryDivision.class);
			for (TIndustryDivision tIndustryDivision : list){
				tIndustryDivisionService.save(tIndustryDivision);
				successNum ++;
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条行业划分记录");
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入行业划分失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/industrydivision/tIndustryDivision/?repage";
    }
	
	/**
	 * 下载导入行业划分数据模板
	 */
	@RequiresPermissions("industrydivision:tIndustryDivision:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "行业划分数据导入模板.xlsx";
    		List<TIndustryDivision> list = Lists.newArrayList(); 
    		new ExportExcel("行业划分数据", TIndustryDivision.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/industrydivision/tIndustryDivision/?repage";
    }
	

}