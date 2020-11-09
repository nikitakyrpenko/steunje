<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="negeso" %>
<%response.setHeader("Expires", "0");%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<title><fmt:message key="CM_SITE_SETTINGS" /></title>

	<link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>	
	
	<link href="/css/customized-jquery-ui.css" rel="stylesheet" type="text/css"/>	
	<!-- link href="/css/jquery-ui.custom.css" rel="stylesheet" type="text/css"/ -->	

	<script type="text/javascript" src="/script/jquery.min.js"></script>
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>	
 <script type="text/javascript" src="/script/cufon-yui.js"></script>
 <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>	
	<script type="text/javascript" src="/script/common_functions.js"></script> 
 <script type="text/javascript">
    var dialog = '<div id="landingPages" class="landingPages" title="Landing pages"><textarea id="pagesSource" style="width: 370px; height:500px;"></textarea></div>';
        
    function showLandingPagesSource () {
								$("[class^=landingPages]").each(function() {
												$(this).remove();
        });

        $.ajax({
												type: "GET",
            url: "/admin/landing_pages?exp=" + new Date().getTime(),
                 dataType: "html",
                 success: function(html, stat) {
																				$(document.body).append(dialog);
                    $("#landingPages").dialog({width: 400, height: 550, resizable: false});
																				
                    $("#landingPages").bind( "dialogclose", function(event, ui) {
                        $(this).remove();
                    })
                    $("#landingPages").dialog('open');
                    $("#pagesSource").val('<ul>' + html + '</ul>');
                 },
                 error: function(html, stat) { }
             });
        }
    </script>
</head>

<body>
				<negeso:admin>
								<table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
												<tr>			
																<td align="center" class="admNavPanelFont"style="height:auto;" ><fmt:message key="CM_SITE_SETTINGS" /></td>		
												</tr>
												<tr>
																<td class="admTableTDLast" id="admTableTDtext">
																				<a class="admAnchor" href="?action=edit"><fmt:message key="CM_GENERAL_SETTINGS" /></a>
																</td>
												</tr>
												<tr>
																<td class="admTableTDLast" id="admTableTDtext">
																				<a class="admAnchor" href="?action=getHostNames"><fmt:message key="CM_HOST_NAMES" /></a>
																</td>
												</tr>
												<tr>
																<td class="admTableTDLast" id="admTableTDtext">
																				<a class="admAnchor" href="?action=getUrlAliases"><fmt:message key="CM_URL_ALIASES" /></a>
																</td>
												</tr>
												<tr>
																<td class="admTableTDLast" id="admTableTDtext">
																				<a class="admAnchor" href="?action=redirects"><fmt:message key="CM_REDIRECTS" /></a>
																</td>
												</tr>
												<tr>
                                                                <td class="admTableTDLast" id="admTableTDtext">
                                                                                <a class="admAnchor" href="/admin/site_placeholder.html"><fmt:message key="CM_PLACEHOLDERS" /></a>
                                                                </td>
                                                </tr>
												<tr>
																<td class="admTableTDLast" id="admTableTDtext">
																				<a class="admAnchor" href="/admin/visitor_parameters?moduleId=38"><fmt:message key="CM_GOOGLE_ANALYTICS" /></a>
																</td>
												</tr>
												<tr>
																<td class="admTableTDLast" id="admTableTDtext">
																				<a class="admAnchor" href="/admin/site_history"><fmt:message key="CM_SITE_HISTORY" /></a>
																</td>
												</tr>
												<tr>
																<td class="admTableTDLast" id="admTableTDtext">
																				<a class="admAnchor" href="/admin/site_overvies"><fmt:message key="CM_SITE_OVERVIES" /></a>
																</td>
												</tr>        
												<tr>
																<td class="admTableTDLast" id="admTableTDtext">
																				<a class="admAnchor" href="/admin/manage_search.html"><fmt:message key="SEARCH"/></a>
																</td>
												</tr>        
												<tr>
																<td class="admTableTDLast" id="admTableTDtext">
																				<a class="admAnchor" href="/admin/edit_social_network"><fmt:message key="CORE.SOCIAL_NETWORK"/></a>
																</td>
												</tr>        
												<tr>
																<td class="admTableFooter">&nbsp;</td>
												</tr>
				</table>
</negeso:admin>
 
<table cellpadding="0" cellspacing="0"  width="764px"  align="center"  border="0" class="Buttons">
				<tr>
								<td>
												<form method="POST">
																<input type="hidden" name="action" value="clearCache">
																<div class="admBtnGreenb">
																				<div class="imgL"></div>
                    <div><input type="submit" name="saveform" value='<fmt:message key="CM_CLEAR_CACHE"/>'/></div>
                    <div class="imgR"></div>
																</div>
            </form>
            <div class="admBtnGreenb">
																<div class="imgL"></div>
                <div><input type="button" value='Landing pages' onclick="showLandingPagesSource()"/></div>
                <div class="imgR"></div>
            </div>
								</td>
				</tr>
</table>

</body>
</html>
