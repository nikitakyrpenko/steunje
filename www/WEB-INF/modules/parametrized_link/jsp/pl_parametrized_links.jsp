<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page session="true"%>
<%response.setHeader("Expires", "0");%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
	<title> <fmt:message key="parametrizedLinks.title"/> </title>
	<script type="text/javascript" src="/script/jquery.min.js">/**/</script>
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
	<script type="text/javascript" src="/script/common_functions.js"></script>
</head>
<body>
	<br/>
	<jsp:include page="/WEB-INF/jsp/common/header.jsp"/>
	<jsp:include page="/WEB-INF/jsp/common/back_bar.jsp"/>
	<jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp"/>
	<script>
		function try_to_delete(id) {
			var answ = confirm('<fmt:message key="parametrizedLinks.deleteLinkConfirm"/>');
			if (answ == true)
				window.location.href="?act=delete&id="+id;
		}
	</script>
	<!-- Title table -->
	<table cellpadding="0" cellspacing="0" class="admNavPanel admSpaceSmall" border="0" align="center">
		<tr>
			<td style="background: #7FB577 url('../../images/titleLeft.gif') left top no-repeat; width: 10px; height: 25px">&#160;</td>
			<td class="admTitle"> <fmt:message key="parametrizedLinks.title"/> </td>
			<td style="background: #7FB577 url('../../images/titleRight.gif') right top no-repeat; width: 10px; height: 25px">&#160;</td>
		</tr>
	</table>
	<form method = "post" enctype="multipart/form-data">
		<table class="admNavPanel" cellspacing="0" cellpadding="0" align="center" border="0">
			<tr> 
				<td class="admNamebar" width="300px"><fmt:message key="parametrizedLinks.linkTitle"/></td> 			 
				<td class="admNamebar"><fmt:message key="parametrizedLinks.linkUrl"/></td> 
				<td class="admNamebar"><fmt:message key="parametrizedLinks.secured"/></td> 
				<td class="admNamebar" colspan="2"><fmt:message key="parametrizedLinks.actions"/></td> 
			</tr> 

			<c:forEach var="parametrizedLink" items="${parametrizedLinks}">
			<tr>
				<td class="admMainTD">
					<a class="admNone" style="font-weight: bold; color: black" href="pl_details.html?act=update&id=${parametrizedLink.id}">${parametrizedLink.title}&nbsp;</a>
				</td>
				<td class="admDarkTD">
					${parametrizedLink.url}
					&#160;
				</td>
				<td class="admLightTD" style="width: 80px">
					<input type="checkbox"
					<c:if test="${parametrizedLink.isSecured}">
						 checked="true" 
					</c:if>
					 disabled="true" />&#160;
				</td>
				<td class="admDarkTD" style="width: 30px">
					<a href="pl_details.html?act=update&id=${parametrizedLink.id}">
						<img class="admImg" style="cursor: pointer" src="/images/edit.gif" width="31px" height="27px" alt="<fmt:message key="EDIT"/>"/>
					</a>
				</td>
				<td class="admLightTD" style="width: 30px">
					<a href="javascript:try_to_delete(${parametrizedLink.id})">
						<img class="admImg" style="cursor: pointer" src="/images/delete.gif" width="31px" height="27px" alt="<fmt:message key="DELETE"/>"/>
					</a>
				</td>
			</tr>
			</c:forEach>

			<tr>
				<td colspan="5" class="admNavbar" align="center">
					<input type="button" class="admNavbarInp" value='&lt;&#160;<fmt:message key="parametrizedLinks.addPl"/>&#160;&gt;' onClick="window.location.href='pl_details.html?act=new'"/>
				</td>
			</tr>
		</table>
	</form>

	<jsp:include page="/WEB-INF/jsp/common/back_bar.jsp"/>
</body>
</html>
