<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>行业代码树管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox") || element.is(":radio") || element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/industrycodetree/industryCodeTree/">行业代码树列表</a></li>
		<li class="active"><a href="${ctx}/industrycodetree/industryCodeTree/form?id=${industryCodeTree.id}&parent.id=${industryCodeTreeparent.id}">行业代码树<shiro:hasPermission name="industrycodetree:industryCodeTree:edit">${not empty industryCodeTree.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="industrycodetree:industryCodeTree:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="industryCodeTree" action="${ctx}/industrycodetree/industryCodeTree/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">上级名称:</label>
			<div class="controls">
				<sys:treeselect id="parent" name="parent.id" value="${industryCodeTree.parent.id}" labelName="parent.name" labelValue="${industryCodeTree.parent.name}"
					title="parent_id" url="/industrycodetree/industryCodeTree/treeData" extId="${industryCodeTree.id}" cssClass="" allowClear="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">行业名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="100" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">行业代码：</label>
			<div class="controls">
				<form:input path="remarks" htmlEscape="false"  maxlength="100" class="input-xxlarge "/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="industrycodetree:industryCodeTree:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>