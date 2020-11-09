<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ page session="true"%>
<%response.setHeader("Expires", "0");%>
<html>
<head>
	<META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
	<title><fmt:message key="subscribers.link"/></title>

	<script type="text/javascript" src="/script/jquery.min.js">/**/</script>
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
<script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"></script>
</head>
<body>
	<br/>
	<jsp:include page="/WEB-INF/jsp/common/header.jsp"/>
	<jsp:include page="/WEB-INF/jsp/common/back_bar.jsp"/>
	<jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp"/>
	<!-- Title table -->
	<table cellpadding="0" cellspacing="0" class="admNavPanel admSpaceSmall" border="0" align="center">
		<tr>
			<td style="background: #7FB577 url('../../images/titleLeft.gif') left top no-repeat; width: 10px; height: 25px">&#160;</td>
			<td class="admTitle"><fmt:message key="subscribers.title"/></td>
			<td style="background: #7FB577 url('../../images/titleRight.gif') right top no-repeat; width: 10px; height: 25px">&#160;</td>
		</tr>
	</table>
	<form method = "post" enctype="multipart/form-data">
		<table class="admNavPanel" cellspacing="0" cellpadding="0"  align="center" border="0">
			<tr> 
				<td class="admNamebar"><fmt:message key="subscribers.subscriberName"/></td> 
				<td class="admNamebar" width="35%"><fmt:message key="subscribers.subscriberLogin"/></td> 
				<td class="admNamebar" width="15%"><fmt:message key="subscribers.peopleAmount"/></td> 
				<td class="admNamebar" width="15%"><fmt:message key="subscribers.subscriptionDate"/></td> 
			</tr> 
			<c:forEach var="subscriber" items="${subscribers}">     
				<tr>       
					<td class="admDarkTD">${subscriber[0]}</td>       
					<td class="admLightTD">${subscriber[1]}</td>       
					<td class="admDarkTD">${subscriber[2]}</td>       
					<td class="admLightTD">${subscriber[3]}</td>       
				</tr> 	      
			</c:forEach>
			<!--<tr>
				<td class="admNavbar" align="center" colspan="4"><input class="admNavbarInp" type="submit" name="update" value="<&nbsp;<fmt:message key="UPDATE"/>&nbsp;>"/></td>
			</tr>
		--></table>
	</form>
	<jsp:include page="/WEB-INF/jsp/common/back_bar.jsp"/>
</body>
</html>
