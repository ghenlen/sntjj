<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>行业划分管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/treeview.jsp" %>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					var ids = [], nodes = tree.getCheckedNodes(true);
					for(var i=0; i<nodes.length; i++) {
						ids.push(nodes[i].id);
					}
					$("#industryCodeIds").val(ids);
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
			
			var setting = {check:{enable:true,nocheckInherit:true},view:{selectedMulti:false},
					data:{simpleData:{enable:true}},callback:{beforeClick:function(id, node){
						tree.checkNode(node, !node.checked, true, true);
						return false;
					}},expandLevel : 1,};
			
			var zNodes=[
						<c:forEach items="${industryCodes}" var="industryCodeTree">{id:"${industryCodeTree.id}", pId:"${not empty industryCodeTree.parent.id?industryCodeTree.parent.id:0}", name:"${not empty industryCodeTree.parent.id?industryCodeTree.name:'权限列表'}"},
			            </c:forEach>];
				// 初始化树结构
				var tree = $.fn.zTree.init($("#menuTree"), setting, zNodes);
				// 不选择父节点
				tree.setting.check.chkboxType = { "Y" : "ps", "N" : "s" };
				// 默认选择节点
				var ids = "${tIndustryDivision.industryCodeIds}".split(",");
				for(var i=0; i<ids.length; i++) {
					var node = tree.getNodeByParam("id", ids[i]);
					try{tree.checkNode(node, true, false);}catch(e){}
				}
				// 默认展开全部节点
				tree.expandAll(false);
			
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/industrydivision/tIndustryDivision/">行业划分列表</a></li>
		<li class="active"><a href="${ctx}/industrydivision/tIndustryDivision/form?id=${tIndustryDivision.id}">行业划分<shiro:hasPermission name="industrydivision:tIndustryDivision:edit">${not empty tIndustryDivision.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="industrydivision:tIndustryDivision:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="tIndustryDivision" action="${ctx}/industrydivision/tIndustryDivision/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="128" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">remarks：</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" rows="4" maxlength="255" class="input-xxlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">行业选择:</label>
			<div class="controls">
				<div id="menuTree" class="ztree" style="margin-top:3px;float:left;"></div>
				<form:hidden path="industryCodeIds"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="industrydivision:tIndustryDivision:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>