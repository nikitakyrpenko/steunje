<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%response.setHeader("Expires", "0");%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="negeso" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
   <head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title><fmt:message key="CM_SITE_OVERVIES"/></title>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>       
    
				<script type="text/javascript" src="/script/jquery.min.js">/**/</script>				
				<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
				<script type="text/javascript" src="/script/cufon-yui.js"></script>
			    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
				<script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"></script>

    <script>
        function sort(by) {
        	document.forms['siteOverviesForm'].sortDirection.value = document.forms['siteOverviesForm'].sortDirection.value == "false" ? true: false;
        	document.forms['siteOverviesForm'].sortBy.value = by;
        	document.forms['siteOverviesForm'].act.value = 'sort';
        	document.forms['siteOverviesForm'].submit();
        }

        function export_() {
        	document.forms['siteOverviesForm'].act.value = 'export';
        	document.forms['siteOverviesForm'].submit();
        }

		function goToPage(theUrl) {
		        try{
		            window.opener.location.href = theUrl;
		            window.opener.focus();
		        }catch(e){
		            window.open(
		                theUrl,
		                "mainWindow",
		                "height=" + (screen.availHeight - 155)+ "px"
		                + ", width=" + (screen.availWidth - 12) + "px"
		                + ", menubar=yes, location=yes, resizable=yes"
		                + ", scrollbars=yes, status=yes, titlebar=yes, toolbar=yes"
		                + ", left=0, top=0");
		        }


		}
        
		function editPagePropertiesByFilename(filename) {
         	window.open("/admin/edit_page_info?act=edit&filename=" + filename ,"page_properties_"+new Date().getTime(),"height=920, width=825, menubar=no, resizable=yes,  status=no, titlebar=yes, toolbar=no, scrollbars=yes");
        }
        
				</script>
</head>

<body>
				<negeso:admin> 
								<table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
												<tr>
																<td style="width:auto; height:auto;" colspan="7"><jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp"/></td>
												</tr>
												<tr>                
																<td align="center" class="admNavPanelFont"  style="height:auto;" colspan="7"><fmt:message key="CM_SITE_OVERVIES" /></td>																
												</tr>
												<tr>
																<td style="width:auto; height:auto;" colspan="8">
																				<form action="" id="siteOverviesForm" name="siteOverviesForm">
																								<input type="hidden" name="act" value="filter"/>
																								<input type="hidden" name="sortDirection" value="${sortDirection}"/>
                        																		<input type="hidden" name="sortBy" value="${sortBy}"/>
																								<div class="admNavPanelInp" style="padding:17px 0 0 10px; float:left;">
																									
																									<select name="filterLangId">
																										<option value="0">*</option>
																										<c:forEach var="lang" items="${languages}">
																											<option value="${lang.id}" 
																												<c:if test="${lang.id == filterLangId}">selected="selected"</c:if>	  
																											>${lang.code}</option>
																										</c:forEach>
																									</select>
																								</div>
																								<div class="admNavPanelInp" style="padding:10px; float:left;">
																												<div class="imgL"></div>
																												<div><input type="submit" value='<fmt:message key="SEARCH"/>' /></div>
																												<div class="imgR"></div>																								
																								</div>
																								<div class="admNavPanelInp" style="padding:10px;">
																												<div class="imgL"></div>
																												<div><input type="submit" value='<fmt:message key="CM_CSV_EXPORT"/>'  onclick="export_()"/></div>
																												<div class="imgR"></div>																								
																								</div>
																				</form>
																 </td>
												</tr>
												<tr> 
																<td class="admTDtitles" width="16%" style="height:auto; background-image: url('/images/sortable_arrows.png'); background-position: 5px; background-repeat: no-repeat;  cursor: pointer;" onclick="sort('filename')">Page name</td>
																<td class="admTDtitles" width="16%" style="height:auto; background-image: url('/images/sortable_arrows.png'); background-position: 5px; background-repeat: no-repeat;  cursor: pointer;" onclick="sort('edit_user')">Edit user</td>
																<td class="admTDtitles" width="16%" style="height:auto; background-image: url('/images/sortable_arrows.png'); background-position: 5px; background-repeat: no-repeat;  cursor: pointer;" onclick="sort('edit_date')">Edit date</td>
																<td class="admTDtitles" width="16%" style="height:auto; background-image: url('/images/sortable_arrows.png'); background-position: 5px; background-repeat: no-repeat;  cursor: pointer;" onclick="sort('publish_date')">Publish date</td>
																<td class="admTDtitles" width="16%" style="height:auto; background-image: url('/images/sortable_arrows.png'); background-position: 5px; background-repeat: no-repeat;  cursor: pointer;" onclick="sort('expired_date')">Expired date</td>
																<td class="admTDtitles" width="16%" style="height:auto; background-image: url('/images/sortable_arrows.png'); background-position: 5px; background-repeat: no-repeat;  cursor: pointer;" onclick="sort('class')">Page template</td>
																<td class="admTDtitles" width="16%" style="height:auto; background-image: url('/images/sortable_arrows.png'); background-position: 5px; background-repeat: no-repeat;  cursor: pointer;" onclick="sort('name')">Page group</td>
																<td class="admTDtitles">Sitemap options</td>
																<td class="admTDtitles"></td>
																
												</tr>
												<c:forEach var="page" items="${pages}">
																<tr>
																				<td class="admTableTD"><a href="javascript:goToPage('/admin/${page.name}')"  title="${page.name}" style="text-decoration: none;color:#1BC3F7;font-weight: bold"> ${page.name}</a></td>
																				<td class="admTableTD">${page.editUser}</td>
																				<td class="admTableTD"><fmt:formatDate value="${page.editDate}" pattern="yyyy-MM-dd"/></td>
																				<td class="admTableTD"><fmt:formatDate value="${page.publishDate}" pattern="yyyy-MM-dd"/></td>
																				<td class="admTableTD"><fmt:formatDate value="${page.expiredDate}" pattern="yyyy-MM-dd"/></td>
																				<td class="admTableTD">${page.template}</td>
																				<td class="admTableTD">${page.pageGroup}</td>
																				<td class="admTableTD">
																					<c:if test="${page.sitemap}">
																							${page.sitemapPrior}/${page.sitemapFreq}
																					</c:if>
																				</td>
																				<td class="admTableTDLast">
																					<img src="/images/properties.png" width="37" height="36" class="admHand" alt="Edit" onclick="editPagePropertiesByFilename('${page.name}')"/>
																				</td>	
																</tr>
												</c:forEach> 
												<tr>
																<td class="admTableFooter" colspan="3" >&nbsp;</td>
												</tr>           
								</table>
				</negeso:admin>        
</body>
</html>