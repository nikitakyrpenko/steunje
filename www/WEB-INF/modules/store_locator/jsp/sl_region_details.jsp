<jsp:include page="/WEB-INF/jsp/langhenkel/commons.jsp"/>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>

<%@ page session="true" %>
<%response.setHeader("Expires", "0");%>
<HTML>
  <HEAD>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    <TITLE><fmt:message key="regionDetails.title"/></TITLE>
    <script type="text/javascript" src="/script/jquery.min.js">/**/</script>
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/common_functions.js"></script>
    <script language="JavaScript1.2" src="/script/calendar_picker.js" type="text/javascript"></script>
    <script language="JavaScript1.2" src="/script/media_catalog.js" type="text/javascript"/></script>
  </HEAD>
<BODY>
<br>
<jsp:include page="/WEB-INF/jsp/common/header.jsp"/>
<jsp:include page="/WEB-INF/jsp/common/back_bar.jsp"/>
<jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp"/>
<table cellpadding="0" cellspacing="0" class="admNavPanel admSpaceSmall" border="0" align="center">
<tr>
	<td style="background: #7FB577 url('../../images/titleLeft.gif') left top no-repeat; width: 10px; height: 25px">&#160;</td>
	<td class="admTitle"><fmt:message key="regionDetails.link"/></td>
	<td style="background: #7FB577 url('../../images/titleRight.gif') right top no-repeat; width: 10px; height: 25px">&#160;</td>
</tr>
</table>
<form method="GET">
<input type="hidden" name="act" value="save_region"/>
<input type="hidden" name="id" value="${region.id}"/>
<table cellpadding="0" cellspacing="0" align="center" class="admNavPanel" border="0">
 <tr>
	<td class="admNavbar">
		<table cellpadding="0" cellspacing="0" width="100%" border="0">
			<tr>
				<td class="admLightTD admLeft" width="30%">
					<label for="title"><fmt:message key="listTitle"/></label>		
				</td>
				<td class="admMainTD admLeft">
					<input class="admTextArea" type="text" maxlength="50" name="title" required="true" value="${region.title}"/>
				</td>
			</tr>
			<tr>
				<td class="admVertPadding" colspan="2" align="center" style="border-top: 3px solid white"><input type="button" class="admNavbarInp" value='&lt;&#160;<fmt:message key="button.Save"/>&#160;&gt;' onClick="this.form.submit()"/></td>
			</tr>
		</table>
	</td>
 </tr>
</table>
</form>
<jsp:include page="/WEB-INF/jsp/common/back_bar.jsp"/>
</BODY>
</HTML>

