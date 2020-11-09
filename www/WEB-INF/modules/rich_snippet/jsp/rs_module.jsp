<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="negeso" %>
<%response.setHeader("Expires", "0");%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
   <head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title><fmt:message key="RS_RICH_SNIPPET_MODULE"/></title>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="/css/customized-jquery-ui.css"/>    
    
    <script type="text/javascript" src="/script/jquery.min.js">/**/</script>
    <script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
	<script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"></script>
    <script type="text/javascript" src="/site/core/script/validation.js"></script>    
</head>
<body>
  
<negeso:admin>
	<table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
        <tr>
			<td style="width:auto; height:auto;"><jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp"/></td>
		</tr>
        <tr>                
			<td align="center" class="admNavPanelFont"  style="height:auto;"><fmt:message key="RS_RICH_SNIPPET_MODULE" /></td>
		</tr>		
		<tr>
			<td class="admTableTD">                  
                <a href="/admin/rs_review_list.html"><fmt:message key="RS_REVIEW_RICH_SNIPPETS" /></a>
            </td>
		</tr>
		<tr>
			<td class="admTableTD">                  
                <a href="/admin/rs_aggregate_review_list.html"><fmt:message key="RS_AGGREGATE_REVIEW_RICH_SNIPPETS" /></a>
            </td>
		</tr>
		<tr>
			<td class="admTableTD">                  
                <a href="/admin/rs_product_list.html"><fmt:message key="RS_PRODUCT_RICH_SNIPPETS" /></a>
            </td>
		</tr>
		<tr>
			<td class="admTableTD">                  
                <a href="/admin/rs_video_list.html"><fmt:message key="RS_VIDEO_RICH_SNIPPETS" /></a>
            </td>
		</tr>
        <tr>
			<td class="admTableFooter" >&nbsp;</td>
        </tr>           
	</table>	
</negeso:admin>

</body>
</html>