<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ page session="true"%>
<%response.setHeader("Expires", "0");%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
	<title><fmt:message key="eventList.title"/></title>
	<script type="text/javascript" src="/script/jquery.min.js">/**/</script>
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
<script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"></script>
</head>
<body>
	<br/>
	<jsp:include page="/WEB-INF/jsp/common/header.jsp"/>
	<jsp:include page="/WEB-INF/jsp/common/back_bar.jsp"/>
	<jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp"/>
	<script>
		function try_to_delete(id,categoryId) {
			var answ = confirm('<fmt:message key="eventList.deleteEventConfirm"/>');
			if (answ==true)
				window.location.href="eventslist.html?deleteId=" + id + "&categoryId=" + categoryId;
		}
	</script>
	<!-- Title table -->
	<table cellpadding="0" cellspacing="0" class="admNavPanel admSpaceSmall" border="0" align="center">
		<tr>
			<td style="background: #7FB577 url('../../images/titleLeft.gif') left top no-repeat; width: 10px; height: 25px">&#160;</td>
			<td class="admTitle"><fmt:message key="eventList.title"/></td>
			<td style="background: #7FB577 url('../../images/titleRight.gif') right top no-repeat; width: 10px; height: 25px">&#160;</td>
		</tr>
	</table>
	<form method = "post" enctype="multipart/form-data">
		<table class="admNavPanel" cellspacing="0" cellpadding="0"  align="center" border="0">
			<tr> 
				<td class="admNamebar"><fmt:message key="eventList.listTitle"/></td> 
				<td class="admNamebar" width="15%"><fmt:message key="eventList.listDate"/></td> 
				<td class="admNamebar" width="15%"><fmt:message key="eventList.listPublishDate"/></td> 
				<td class="admNamebar" width="15%"><fmt:message key="eventList.listLanguage"/></td> 
				<td class="admNamebar" colspan="4"><fmt:message key="eventList.listActions"/></td> 
			</tr> 
			<c:forEach var="event" items="${events}">
			<tr>
				<td class="admMainTD"><a class="admNone" style="font-weight: bold; color: black" href='event_details.html?id=${event.id}&categoryId=${event.categoryId}'>${event.defaultTitle}&nbsp;</a></td>
				<td class="admDarkTD"><fmt:formatDate value="${event.date}" type="both" pattern="dd-MM-yyyy"/>&nbsp;</td>
				<td class="admLightTD"><fmt:formatDate value="${event.publishDate}" type="both" pattern="dd-MM-yyyy"/>&nbsp;</td>
				<td class="admDarkTD"><fmt:formatDate value="${event.expiredDate}" type="both" pattern="dd-MM-yyyy"/>&nbsp;</td>
				<td class="admLightTD" width="30px"><a href="event_details.html?id=${event.id}&categoryId=${event.categoryId}"><img class="admImg" style="cursor: pointer" src="/images/edit.gif" width="31px" height="27px" alt="<fmt:message key="EDIT"/>"/></a></td>
				<td class="admDarkTD" width="30px"><a href="subscribers.html?id=${event.id}&categoryId=${event.categoryId}"><img class="admImg" style="cursor: pointer" src="/images/edit_url.gif" width="31px" height="27px" alt="<fmt:message key="button.alt.subscribers"/>"/></a></td>
				<td class="admDarkTD" width="30px"><a href="event_subscribers.csv?downloadId=${event.id}&categoryId=${event.categoryId}"><img class="admImg" style="cursor: pointer" src="/images/submenu.gif" width="31px" height="27px" alt="<fmt:message key="button.alt.download"/>"/></a></td>
				<td class="admLightTD" width="30px"><a href="javascript:try_to_delete(${event.id}, ${event.categoryId})"><img class="admImg" style="cursor: pointer" src="/images/delete.gif" width="31px" height="27px" alt="<fmt:message key="DELETE"/>"/></a></td>
			</tr>
			</c:forEach>
			<tr>
				<td colspan="8" class="admNavbar" align="center">
					<input type="button" class="admNavbarInp" value='&lt;&#160;<fmt:message key="eventList.addEvent"/>&#160;&gt;' onClick="window.location.href='event_details.html?categoryId=${param.categoryId}'"/>
				</td>
			</tr>
		</table>
	</form>
	<jsp:include page="/WEB-INF/jsp/common/back_bar.jsp"/>
</body>
</html>
