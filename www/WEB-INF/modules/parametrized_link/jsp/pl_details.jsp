<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page session="true"%>
<%response.setHeader("Expires", "0");%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<script language="JavaScript1.2" src="/site/core/script/validation.js" type="text/javascript"></script>
	<link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
	<title> <fmt:message key="parametrizedLinkDetails.title"/> </title>
	<script type="text/javascript" src="/script/jquery.min.js">/**/</script>
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
	<script type="text/javascript" src="/script/common_functions.js"></script>
	<script>
	function doSubmit() {
			document.plform.act.value = 'save';	 	
			document.plform.submit();
	}	
	</script>
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
			<td class="admTitle"> <fmt:message key="parametrizedLinkDetails.title"/> </td>
			<td style="background: #7FB577 url('../../images/titleRight.gif') right top no-repeat; width: 10px; height: 25px">&#160;</td>
		</tr>
	</table>
	<form method="post" enctype="multipart/form-data" name="plform">
	
		<input type="hidden" name="act" id="act" value=""/>
		
		<script>
			function set_value_by_checkbox(destId, srcObj) {
				var obj1 = document.getElementById(destId);
				if ("" + srcObj.checked == "true" || srcObj.checked == "checked")
						obj1.value = "true";
				else
					obj1.value = "false";
			}
		</script>		

		<table class="admNavPanel" cellspacing="0" cellpadding="0" align="center" border="0">
			
			<tr>
				<td class="admMainTD" width="100px">
					<fmt:message key="parametrizedLinkDetails.title1"/>* :
				</td>
				<td class="admLightTD" style="text-align: left; padding-left: 6px">
					<input type="text" name="title" required="true" style="width: 200px" value="${parametrizedLink.title}" />
				</td>
			</tr>
			<tr>
				<td class="admMainTD" width="120px">
					<fmt:message key="parametrizedLinkDetails.url"/>* :
				</td>
				<td class="admLightTD" style="text-align: left; padding-left: 6px">
					<input type="text" name="url" required="true" style="width: 200px" value="${parametrizedLink.url}" />
				</td>
			</tr>
			<tr>
				<td class="admMainTD" width="120px">
					<fmt:message key="parametrizedLinkDetails.secured"/> :
				</td>
				<td class="admLightTD" style="text-align: left; padding-left: 6px">
					<input type="hidden" name="isSecured" id="isSecured" value="${parametrizedLink.isSecured}" />
					<input type="checkbox" id="t_secured" onclick="set_value_by_checkbox('isSecured',this)" <c:if test="${parametrizedLink.isSecured}">checked="true"</c:if>/>
				</td>
			</tr>
			
			<tr>
				<td colspan="2" class="admNavbar" align="center">
					<input type="button" class="admNavbarInp" name="update" value='&lt;&#160;<fmt:message key="SAVE"/>&#160;&gt;' onClick="if (full_validate(this.form)) doSubmit()"/>
				</td>
			</tr>
		</table>
	</form>

	<jsp:include page="/WEB-INF/jsp/common/back_bar.jsp"/>
</body>
</html>