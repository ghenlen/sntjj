<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>行业代码树管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/treetable.jsp" %>
	<script type="text/javascript" src="${ctxStatic}/ajaxfileupload.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#loadId").hide();
			var tpl = $("#treeTableTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g,"");
			var data = ${fns:toJson(list)}, ids = [], rootIds = [];
			for (var i=0; i<data.length; i++){
				ids.push(data[i].id);
			}
			ids = ',' + ids.join(',') + ',';
			for (var i=0; i<data.length; i++){
				if (ids.indexOf(','+data[i].parentId+',') == -1){
					if ((','+rootIds.join(',')+',').indexOf(','+data[i].parentId+',') == -1){
						rootIds.push(data[i].parentId);
					}
				}
			}
			for (var i=0; i<rootIds.length; i++){
				addRow("#treeTableList", tpl, data, rootIds[i], true);
			}
			$("#treeTable").treeTable({expandLevel : 1});
		});
		function addRow(list, tpl, data, pid, root){
			for (var i=0; i<data.length; i++){
				var row = data[i];
				if ((${fns:jsGetVal('row.parentId')}) == pid){
					$(list).append(Mustache.render(tpl, {
						dict: {blank123:0},
						pid: (root?0:pid),
						row: row
					}));
					addRow(list, tpl, data, row.id);
				}
			}
		}
		function uploadFile(){
			var fjid=$("#filename").val();
			var url2=ctx + "/industrycodetree/industryCodeTree/uploadFile";
			if(fjid){
				var index=fjid.lastIndexOf(".")
				fjid=fjid.substring(index+1);
				if( !(fjid.endWith("xlsx"))||!(fjid.endWith("xls"))){
					alert("请选择excel文件");
					return;
				}
				$("#loadId").show();
				$.ajaxFileUpload({
	                url:url2,            //需要链接到服务器地址
	                secureuri:false,
	                fileElementId:'filename',                        //文件选择框的id属性
	                dataType: 'JSON',                                     //服务器返回的格式，可以是json
	                success: function (data, status){ 
	                	$("#loadId").hide();
	                	data1 = jQuery.parseJSON(jQuery(data).text());
	                	alert(data1.message);
	                	window.location.reload();
	                },
	                error: function (data, status, e){
	                	$("#loadId").hide();
	                	data1 = jQuery.parseJSON(jQuery(data).text());
	                }
	            });
			}else{
				alert("请选择附件");
			}
		}
		String.prototype.endWith = function(str){  
		     if(str==null || str=="" || this.length == 0 ||str.length > this.length){      
		       return false;  
		     }  
		     if(this.substring(this.length - str.length)){  
		         return true;  
		     }else{  
		         return false;  
		     }  
		     return true;  
		};
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/industrycodetree/industryCodeTree/">行业代码树列表</a></li>
		<shiro:hasPermission name="industrycodetree:industryCodeTree:edit"><li><a href="${ctx}/industrycodetree/industryCodeTree/form">行业代码树添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="industryCodeTree" action="${ctx}/industrycodetree/industryCodeTree/" method="post" class="breadcrumb form-search">
		<ul class="ul-form">
			<li><label>行业名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="100" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="btns"></li>
			<li class="btns"></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<form id="form2" enctype="multipart/form-data" method="post" style="margin-left: 3.5rem;display: none">
		<input type="file" name="filename" id="filename">
		<input id="btnSubmit" class="btn btn-primary" type="button" value="导入" onclick="uploadFile()"/>
	</form>
	
	<sys:message content="${message}"/>
	
	<table id="treeTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>行业名称</th>
				<th>代码</th>
				<th>update_date</th>
				
				<shiro:hasPermission name="industrycodetree:industryCodeTree:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody id="treeTableList"></tbody>
	</table>
	<script type="text/template" id="treeTableTpl">
		<tr id="{{row.id}}" pId="{{pid}}">
			<td><a href="${ctx}/industrycodetree/industryCodeTree/form?id={{row.id}}">
				{{row.name}}
			</a></td>
			<td>
				{{row.remarks}}
			</td>
			<td>
				{{row.updateDate}}
			</td>
			
			<shiro:hasPermission name="industrycodetree:industryCodeTree:edit"><td>
   				<a href="${ctx}/industrycodetree/industryCodeTree/form?id={{row.id}}">修改</a>
				<a href="${ctx}/industrycodetree/industryCodeTree/delete?id={{row.id}}" onclick="return confirmx('确认要删除该行业代码树及所有子行业代码树吗？', this.href)">删除</a>
				<a href="${ctx}/industrycodetree/industryCodeTree/form?parent.id={{row.id}}">添加下级行业代码树</a> 
			</td></shiro:hasPermission>
		</tr>
	</script>
	<div id="loadId" style="background-image: url('${ctxStatic}/images/loading.gif');position: fixed;margin-left: 50%;margin-top: 20px;width: 48px;height: 48px;    display: none;" >
	
	</div>
</body>
</html>