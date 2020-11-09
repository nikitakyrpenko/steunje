<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>

<%@ page session="true"%>
<%response.setHeader("Expires", "0");%>
<HTML>
				<HEAD>
								<META http-equiv="Content-Type" content="text/html; charset=utf-8" />
								<link href="/css/admin_style.css" rel="stylesheet" type="text/css" />
								<TITLE><fmt:message key="regions.title" /></TITLE>

								<script type="text/javascript" src="/script/jquery.min.js">/**/</script>
								<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
								<script type="text/javascript" src="/script/common_functions.js"></script>
				</HEAD>
<BODY>
<br>
<jsp:include page="/WEB-INF/jsp/common/header.jsp"/>
<jsp:include page="/WEB-INF/jsp/common/back_bar.jsp"/>
<jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp"/>
<table cellpadding="0" cellspacing="0" class="admNavPanel admSpaceSmall" border="0" align="center">
<tr>
	<td style="background: #7FB577 url('../../images/titleLeft.gif') left top no-repeat; width: 10px; height: 25px">&#160;</td>
	<td class="admTitle"><fmt:message key="regions.link"/></td>
	<td style="background: #7FB577 url('../../images/titleRight.gif') right top no-repeat; width: 10px; height: 25px">&#160;</td>
</tr>
</table>
<form method="GET">
<input type="hidden" name="act" value="add_region"/>
<table cellpadding="0" cellspacing="0" align="center" class="admNavPanel" border="0">
<tr>
	<td class="admNavbar">
		<table cellpadding="0" cellspacing="0" width="100%" border="0">
			<tr>
				<td class="admVertPadding" colspan="3" align="center" style="border-top: 3px solid white">
					<input class="admTextArea" type="text" maxlength="50" name="title" required="true" value=""/>
					<input type="submit" class="admNavbarInp" value='&lt;&#160;<fmt:message key="regions.button.addRegion"/>&#160;&gt;'/>
				</td>
			</tr>
		</table>
	</td>
</tr>
</table>
<table cellpadding="0" cellspacing="0" align="center" class="admNavPanel" border="0">
	<tr> 
		<td class="admNamebar"><fmt:message key="regions.listTitle"/></td>
		<td class="admNamebar" colspan="2"><fmt:message key="regions.listAction"/></td>
	</tr>
	<c:forEach var="region" items="${regions}">
		<tr>
			<td class="admMainTD"><a href="?act=company_list&id=${region.id}" style="font-weight: bold; cursor: hand; color: black">${region.title}</a></td>
			<td class="admLightTD" width="30px"><a href="?act=region_details&id=${region.id}"><img class="admImg" style="cursor: hand" src="/images/edit.gif" width="31px" height="27px" alt="<fmt:message key="button.alt.edit"/>"/></a></td>
			<td class="admDarkTD" width="30px"><a href="?act=remove_region&id=${region.id}"><img class="admImg" style="cursor: hand" src="/images/delete.gif" width="31px" height="27px" alt="<fmt:message key="button.alt.delete"/>"/></a></td>
		</tr>
	</c:forEach>
</table>
</form>
<jsp:include page="/WEB-INF/jsp/common/back_bar.jsp"/>
</BODY>
</HTML>
