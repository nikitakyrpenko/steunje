<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%response.setHeader("Expires", "0");%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
				<link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
				<title>Custom i18n consts</title>
				<script type="text/javascript" src="/script/jquery.min.js">/**/</script>
				<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
				<script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"></script>
</head>
<body>
	<br/>
	<jsp:include page="/WEB-INF/jsp/common/header.jsp"/>
	<jsp:include page="/WEB-INF/jsp/common/back_bar.jsp"/>
	<jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp"/>
	<table cellpadding="0" cellspacing="0" class="admNavPanel admSpaceSmall" border="0" align="center">
		<tr>
			<td style="background: #7FB577 url('../../images/titleLeft.gif') left top no-repeat; width: 10px; height: 25px">&#160;</td>
			<td class="admTitle">
				Custom consts
			</td>
			<td style="background: #7FB577 url('../../images/titleRight.gif') right top no-repeat; width: 10px; height: 25px">&#160;</td>
		</tr>
	</table>
 	<table class="admNavPanel" cellspacing="0" cellpadding="0" align="center" border="0">
	 	<tr>
 			<td class="admNamebar">Module</td> 
			<td class="admNamebar">Dictionary alias</td> 
	 	</tr>
	 	<tr>
			<td class="admMainTD">
			<b>
			   <a class="admAnchor" href="/admin/module_consts">
				 Common custom consts
			  </a>
			</b>
			<td style="color: #0033CC;" class="admMainTD">dict_common.xsl</td>						
	 	</tr>
		<c:forEach var="module" items="${modules}">
		 	<tr>
			<td class="admMainTD">
			<b>
			   <a class="admAnchor" href="/admin/module_consts?moduleId=${module.id}">
				 ${module.title}
			  </a>
			</b>
			</td>
			<td style="color: #0033CC;" class="admMainTD">${module.name}</td>			
		</tr>	
		</c:forEach>		
 	</table>
	<jsp:include page="/WEB-INF/jsp/common/back_bar.jsp"/>
</body>
</html>