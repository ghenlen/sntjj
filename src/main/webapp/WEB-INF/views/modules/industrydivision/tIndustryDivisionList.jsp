<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>行业划分管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/industrydivision/tIndustryDivision/">行业划分列表</a></li>
		<shiro:hasPermission name="industrydivision:tIndustryDivision:edit"><li><a href="${ctx}/industrydivision/tIndustryDivision/form">行业划分添加</a></li></shiro:hasPermission>
	</ul>
	
	<shiro:hasPermission name="industrydivision:tIndustryDivision:import">
		<table:importExcel url="${ctx}/industrydivision/tIndustryDivision/import"></table:importExcel><!-- 导入按钮 -->
	</shiro:hasPermission>
			
	<form:form id="searchForm" modelAttribute="tIndustryDivision" action="${ctx}/industrydivision/tIndustryDivision/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="128" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>名称</th>
				<th>update_date</th>
				<th>remarks</th>
				<shiro:hasPermission name="industrydivision:tIndustryDivision:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="tIndustryDivision">
			<tr>
				<td><a href="${ctx}/industrydivision/tIndustryDivision/form?id=${tIndustryDivision.id}">
					${tIndustryDivision.name}
				</a></td>
				<td>
					<fmt:formatDate value="${tIndustryDivision.updateDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${tIndustryDivision.remarks}
				</td>
				<shiro:hasPermission name="industrydivision:tIndustryDivision:edit"><td>
    				<a href="${ctx}/industrydivision/tIndustryDivision/form?id=${tIndustryDivision.id}">修改</a>
					<a href="${ctx}/industrydivision/tIndustryDivision/delete?id=${tIndustryDivision.id}" onclick="return confirmx('确认要删除该行业划分吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>