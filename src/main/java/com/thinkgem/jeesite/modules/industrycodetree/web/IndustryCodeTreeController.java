/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.industrycodetree.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.industrycodetree.entity.IndustryCodeTree;
import com.thinkgem.jeesite.modules.industrycodetree.service.IndustryCodeTreeService;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * 行业代码树Controller
 * @author ghl
 * @version 2017-09-14
 */
@Controller
@RequestMapping(value = "${adminPath}/industrycodetree/industryCodeTree")
public class IndustryCodeTreeController extends BaseController {

	@Autowired
	private IndustryCodeTreeService industryCodeTreeService;
	
	@ModelAttribute
	public IndustryCodeTree get(@RequestParam(required=false) String id) {
		IndustryCodeTree entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = industryCodeTreeService.get(id);
		}
		if (entity == null){
			entity = new IndustryCodeTree();
		}
		return entity;
	}
	
	@RequiresPermissions("industrycodetree:industryCodeTree:view")
	@RequestMapping(value = {"list", ""})
	public String list(IndustryCodeTree industryCodeTree, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<IndustryCodeTree> list = industryCodeTreeService.findList(industryCodeTree); 
		model.addAttribute("list", list);
		return "modules/industrycodetree/industryCodeTreeList";
	}

	@RequiresPermissions("industrycodetree:industryCodeTree:view")
	@RequestMapping(value = "form")
	public String form(IndustryCodeTree industryCodeTree, Model model) {
		if (industryCodeTree.getParent()!=null && StringUtils.isNotBlank(industryCodeTree.getParent().getId())){
			industryCodeTree.setParent(industryCodeTreeService.get(industryCodeTree.getParent().getId()));
			// 获取排序号，最末节点排序号+30
			if (StringUtils.isBlank(industryCodeTree.getId())){
				IndustryCodeTree industryCodeTreeChild = new IndustryCodeTree();
				industryCodeTreeChild.setParent(new IndustryCodeTree(industryCodeTree.getParent().getId()));
				List<IndustryCodeTree> list = industryCodeTreeService.findList(industryCodeTree); 
				if (list.size() > 0){
					industryCodeTree.setSort(list.get(list.size()-1).getSort());
					if (industryCodeTree.getSort() != null){
						industryCodeTree.setSort(industryCodeTree.getSort() + 30);
					}
				}
			}
		}
		if (industryCodeTree.getSort() == null){
			industryCodeTree.setSort(30);
		}
		model.addAttribute("industryCodeTree", industryCodeTree);
		return "modules/industrycodetree/industryCodeTreeForm";
	}

	@RequiresPermissions("industrycodetree:industryCodeTree:edit")
	@RequestMapping(value = "save")
	public String save(IndustryCodeTree industryCodeTree, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, industryCodeTree)){
			return form(industryCodeTree, model);
		}
		industryCodeTreeService.save(industryCodeTree);
		addMessage(redirectAttributes, "保存行业代码树成功");
		return "redirect:"+Global.getAdminPath()+"/industrycodetree/industryCodeTree/?repage";
	}
	
	@RequiresPermissions("industrycodetree:industryCodeTree:edit")
	@RequestMapping(value = "delete")
	public String delete(IndustryCodeTree industryCodeTree, RedirectAttributes redirectAttributes) {
		industryCodeTreeService.delete(industryCodeTree);
		addMessage(redirectAttributes, "删除行业代码树成功");
		return "redirect:"+Global.getAdminPath()+"/industrycodetree/industryCodeTree/?repage";
	}

	@RequiresPermissions("user")
	@ResponseBody
	@RequestMapping(value = "treeData")
	public List<Map<String, Object>> treeData(@RequestParam(required=false) String extId, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<IndustryCodeTree> list = industryCodeTreeService.findList(new IndustryCodeTree());
		for (int i=0; i<list.size(); i++){
			IndustryCodeTree e = list.get(i);
			if (StringUtils.isBlank(extId) || (extId!=null && !extId.equals(e.getId()) && e.getParentIds().indexOf(","+extId+",")==-1)){
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.getParentId());
				map.put("name", e.getName());
				mapList.add(map);
			}
		}
		return mapList;
	}
	
	@RequestMapping(value = "uploadFile")
	@ResponseBody
	public Map<String,Object> uploadFile(MultipartFile filename) throws IOException {
		System.out.println(filename.getName());
		Map<String,Object> map=new HashMap<String,Object>();
		InputStream inputStream=filename.getInputStream();
		Workbook  wb=null; 
        try {
        	wb = new XSSFWorkbook(inputStream);
        } catch (Exception ex) {
        	wb = new HSSFWorkbook(inputStream);
        }
        Sheet hssfSheet= wb.getSheetAt(0);  
        User user=UserUtils.getUser();
        String menlei="";
        String dalei="";
        String zhonglei="";
        String xiaolei="";
        String gen="";
        int row=2;
        if(hssfSheet!=null){  
        	try {
        		//遍历excel,从第二行开始 即 rowNum=1,逐个获取单元格的内容,然后进行格式处理,最后插入数据库  
                for(int rowNum=2;rowNum<=hssfSheet.getLastRowNum();rowNum++){  
                    Row hssfRow=hssfSheet.getRow(rowNum);  
                    if(hssfRow==null){  
                        continue;  
                    }  
                    
                    IndustryCodeTree industryCodeTree=new IndustryCodeTree();
                    industryCodeTree.setCreateBy(user);
                    industryCodeTree.setUpdateBy(user);
                    	if(!"".equals(hssfRow.getCell(0).toString())){
                    		IndustryCodeTree parent=new IndustryCodeTree();
                    		parent.setId("0");
                            parent.setName("行业代码");
                    		if(rowNum==1){
                            	industryCodeTree.setParentIds("0,");
                            	parent.setId("0");
                            	gen=hssfRow.getCell(0).toString();
                            	industryCodeTree.setId(gen);
                            	industryCodeTree.setRemarks(gen);
                            }else{
                            	parent.setId(gen);
                            	parent.setName("行业代码");
                            	industryCodeTree.setParentIds("0,");
                            	String cell=hssfRow.getCell(0).toString();
                            	menlei=hssfRow.getCell(0).toString();
                            	if(cell.contains(".")){
                            		menlei=cell.substring(0,cell.lastIndexOf("."));
                            	}
                            	
                            	industryCodeTree.setId(menlei);
                            	industryCodeTree.setRemarks(menlei);
                            }
                    		parent.setName("行业代码");
                    		industryCodeTree.setParent(parent);
                    		industryCodeTree.setSort(1);
                    	}else if(!"".equals(hssfRow.getCell(1).toString())){
                    		IndustryCodeTree parent=new IndustryCodeTree();
                    		parent.setId(menlei);
                    		dalei=menlei+""+hssfRow.getCell(1).toString();
                    		String cell=hssfRow.getCell(1).toString();
                    		if(cell.contains(".")){
                    			dalei=menlei+""+cell.substring(0,cell.lastIndexOf("."));
                        	}
                    		industryCodeTree.setParent(parent);
                    		industryCodeTree.setId(gen+dalei);
                    		industryCodeTree.setParentIds("0,"+menlei+","+dalei);
                    		industryCodeTree.setRemarks(dalei);
                    		industryCodeTree.setSort(2);
                    	}else if(!"".equals(hssfRow.getCell(2).toString())){
                    		IndustryCodeTree parent=new IndustryCodeTree();
                    		parent.setId(dalei);
                    		zhonglei=menlei+hssfRow.getCell(2).toString();
                    		String cell=hssfRow.getCell(2).toString();
                    		if(cell.contains(".")){
                    			zhonglei=menlei+""+cell.substring(0,cell.lastIndexOf("."));
                        	}
                    		industryCodeTree.setParent(parent);
                    		industryCodeTree.setId(zhonglei);
                    		industryCodeTree.setParentIds("0,"+menlei+","+dalei+","+zhonglei);
                    		industryCodeTree.setRemarks(zhonglei);
                    		industryCodeTree.setSort(3);
                    	}else if(!"".equals(hssfRow.getCell(3).toString())){
                    		IndustryCodeTree parent=new IndustryCodeTree();
                    		parent.setId(zhonglei);
                    		xiaolei=menlei+hssfRow.getCell(3).toString();
                    		String cell=hssfRow.getCell(3).toString();
                    		if(cell.contains(".")){
                    			xiaolei=menlei+""+cell.substring(0,cell.lastIndexOf("."));
                        	}
                    		industryCodeTree.setParent(parent);
                    		industryCodeTree.setId(xiaolei);
                    		industryCodeTree.setRemarks(xiaolei);
                    		industryCodeTree.setParentIds("0,"+menlei+","+dalei+","+zhonglei+","+xiaolei);
                    		industryCodeTree.setSort(4);
                    	}

                    
                    industryCodeTree.setName(hssfRow.getCell(4).toString());
            		industryCodeTreeService.uploadFile(industryCodeTree);
                }
                map.put("result", true);
                map.put("message", "导入成功");
            
			} catch (Exception e) {
				// TODO: handle exception
				map.put("result", false);
				map.put("message", "第"+row+"条数据出现问题:"+e.getClass().getName());
			}finally{
				inputStream.close();
			}	
	}
        return map;
	}
}