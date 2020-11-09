<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page session="true"%>
<%response.setHeader("Expires", "0");%>
<html>
<head>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
				<link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
				<title><fmt:message key="EVENT.CATEGORIES"/></title>

				<script type="text/javascript" src="/script/jquery.min.js">/**/</script>
				<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
				<script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"></script>
</head>
<body>
	<br/>

	<jsp:include page="/WEB-INF/jsp/common/header.jsp"/>
	<jsp:include page="/WEB-INF/jsp/common/back_bar.jsp"/>
	<jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp"/>

	<%@ page import="com.negeso.framework.controller.SessionData" %>
	<script>
		function try_to_delete(id) {
			var answ = confirm('<fmt:message key="DELETE_CATEGORY_CONFIRM.MSG"/>');
			if (answ==true)
				window.location.href="?mode=delete&categoryId="+id;
		}
	</script>

	<!-- Title table -->

	<table cellpadding="0" cellspacing="0" class="admNavPanel admSpaceSmall" border="0" align="center">
		<tr>
			<td style="background: #7FB577 url('../../images/titleLeft.gif') left top no-repeat; width: 10px; height: 25px">&#160;</td>
			<td class="admTitle">
				<c:choose>
      				<c:when test="${currentCategory!=null}">
						<fmt:message key="BROWSE_CATEGORY.HEADER"/> 
<c:choose>
	<c:when test="${fn:length(currentCategory.defaultTitle)>50}">${fn:substring(currentCategory.defaultTitle,0,50)}...</c:when>
	<c:otherwise>${currentCategory.defaultTitle}</c:otherwise>
</c:choose>
      				</c:when>
					<c:otherwise>
						<fmt:message key="TOP_CATEGORIES.HEADER"/>
					</c:otherwise>
       			</c:choose>
			</td>
			<td style="background: #7FB577 url('../../images/titleRight.gif') right top no-repeat; width: 10px; height: 25px">&#160;</td>
		</tr>
	</table>

	<form method = "post" enctype="multipart/form-data">
		<table class="admNavPanel" cellspacing="0" cellpadding="0"  align="center" border="0">
			<tr> 
				<td class="admNamebar"><fmt:message key="CATEGORY_LIST.TITLE"/></td> 
				<td class="admNamebar" width="15%"><fmt:message key="eventList.listPublishDate"/></td> 
				<td class="admNamebar" width="15%"><fmt:message key="LIST_EXPIRED_DATE"/></td> 
				<td class="admNamebar" colspan="4"><fmt:message key="eventList.listActions"/></td> 
			</tr> 
			<c:if test="${currentCategory!=null}">
			<tr>
				<td class="admMainTD">
					<c:choose>
          				<c:when test="${currentCategory.parentCategory==null}">
					<a class="admNone" style="font-weight: bold; color: black" href='categorieslist.html'>. .</a>
         				</c:when>
						<c:otherwise>
					<a class="admNone" style="font-weight: bold; color: black" href='categorieslist.html?mode=&categoryId=${currentCategory.parentCategory.id}'>. .</a>
						</c:otherwise>
        			</c:choose>
				</td>
				<td class="admDarkTD">&#160;</td>
				<td class="admLightTD">&#160;</td>
				<td class="admDarkTD" width="30px">&#160;</td>
				<td class="admDarkTD" width="30px">&#160;</td>
				<td class="admLightTD" width="30px">&#160;</td>
				<td class="admLightTD" width="30px">&#160;</td>
			</tr>
			</c:if>
			<c:forEach var="category" items="${categories}">
			<tr>
				<td class="admMainTD">
					<c:if test="${category.leaf}">
					<a class="admNone" style="font-weight: bold; color: black" href='eventslist.html?mode=&categoryId=${category.id}'>
					</c:if>
					<c:if test="${!category.leaf}">
					<a class="admNone" style="font-weight: bold; color: black"
					 href='categorieslist.html?mode=&categoryId=${category.id}' title="${category.defaultTitle}">
					</c:if>
<c:choose>
	<c:when test="${fn:length(category.defaultTitle)>50}">${fn:substring(category.defaultTitle,0,50)}...</c:when>
	<c:otherwise>${category.defaultTitle}</c:otherwise>
</c:choose></a></td>
				<td class="admDarkTD">
					<fmt:formatDate value="${category.publishDate}" type="both" pattern="dd-MM-yyyy"/>&nbsp;
				</td>
				<td class="admLightTD">
					<fmt:formatDate value="${category.expiredDate}" type="both" pattern="dd-MM-yyyy"/>&nbsp;<!-- <em> - ${category.orderNumber}</em> -->
				</td>
				<td class="admDarkTD" width="30px">
					<a href="category_details.html?mode=orderUp&categoryId=${category.id}"><img class="admImg" style="cursor: pointer" src="/images/up.gif" width="31px" height="27px" alt="<fmt:message key="UP"/>"/></a>
				</td>
				<td class="admDarkTD" width="30px">
					<a href="category_details.html?mode=orderDown&categoryId=${category.id}"><img class="admImg" style="cursor: pointer" src="/images/down.gif" width="31px" height="27px" alt="<fmt:message key="DOWN"/>"/></a>
				</td>
				<td class="admLightTD" width="30px">
					<a href="category_details.html?mode=details&categoryId=${category.id}"><img class="admImg" style="cursor: pointer" src="/images/edit.gif" width="31px" height="27px" alt="<fmt:message key="EDIT"/>"/></a>
				</td>
				<td class="admLightTD" width="30px">
					<a href="javascript:try_to_delete(${category.id})"><img class="admImg" style="cursor: pointer" src="/images/delete.gif" width="31px" height="27px" alt="<fmt:message key="DELETE"/>"/></a>
				</td>
			</tr>
			</c:forEach>
			<tr>
				<td colspan="8" class="admNavbar" align="center">
					<input type="button" class="admNavbarInp" value='&lt;&#160;<fmt:message key="ADD_CATEGORY.BUTTON"/>&#160;&gt;' 
					<c:choose>
          				<c:when test="${currentCategory==null}">onClick="window.location.href='category_details.html?mode=add&categoryId=0'"/></c:when>
						<c:otherwise>onClick="window.location.href='category_details.html?mode=add&categoryId=${currentCategory.id}'"/></c:otherwise>
        			</c:choose>
				</td>
			</tr>
		</table>
	</form>

	<jsp:include page="/WEB-INF/jsp/common/back_bar.jsp"/>

</body>
</html>