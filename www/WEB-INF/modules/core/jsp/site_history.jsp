<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%response.setHeader("Expires", "0");%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="negeso" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
		<title><fmt:message key="CM_SITE_HISTORY"/></title>
		<link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
		<link rel="stylesheet" type="text/css" href="/css/jquery-ui.custom.css"/>       
    
		<script type="text/javascript" src="/script/jquery.min.js">/**/</script>
		<script type="text/javascript" src="/script/jquery-ui.custom.min.js">/**/</script>	
		<script type="text/javascript" src="/script/jquery-ui-timepicker-addon.js">/**/</script>	
		<script type="text/javascript" src="/script/cufon-yui.js"></script>
		<script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>

		<script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"></script>
		<script type="text/javascript" src="/site/core/script/validation.js"></script>

        <script>            
            function deleteHistory() {				
            	if (!validate(document.forms['siteHistoryForm'])) {
                	return false;
                }
            	document.forms['siteHistoryForm'].submit();
            }

            function sort(by) {
            	document.forms['siteHistoryForm'].sortDirection.value = document.forms['siteHistoryForm'].sortDirection.value == "false" ? true: false;
            	document.forms['siteHistoryForm'].sortBy.value = by;
            	document.forms['siteHistoryForm'].submit();
            }
        </script>
	</head>

<body>
    <negeso:admin>
		<table align="center" border="0" cellpadding="0" cellspacing="0" class="admTable">
			<tr>
				<td  style="width:auto; height:auto;" colspan="4"><jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp" /></td></tr>
            <tr>
                <td align="center" class="admNavPanelFont"  style="height:auto;"  colspan="5">
                    <fmt:message key="CM_SITE_HISTORY" />
                </td>                
            </tr>
            <tr>
                 <td  class="admTableTD" colspan="5">
                    <form action="" method="post" id="siteHistoryForm">
                        <input type="hidden" name="act" value="deleteHistory"/>
                        <input type="hidden" name="sortDirection" value="${sortDirection}"/>
                        <input type="hidden" name="sortBy" value="${sortBy}"/>
																								
						<div style="float: left; margin-top: 10px; margin-right: 10px;"><fmt:message key="CM_OLDER_THAN" /></div>																								
						
						<div class="sh_dateHolder">
							<input name="dateField" id="dateField" type="text" class="admTextArea admWidth150"  readonly="readonly" required="true" style="float: none;"/>							
							<script>
								$(function () {
									$("#dateField").datetimepicker({
										dateFormat: 'yy-mm-dd',										
										showSecond: true,
										timeFormat: 'hh:mm:ss',
										showOn: 'button',
										buttonImage: '/images/calendar.gif',
										buttonImageOnly: true
									});
								});
							</script>
						</div>
	                    
						<div class="admNavPanelInp" style="float: left; padding-left: 0px;">
							<div class="imgL"></div>
							<div><input type="button" onclick="deleteHistory();" name="selectImageButton" value="<fmt:message key="CM_DELETE_HISTORY" />"></div>
							<div class="imgR"></div>
						</div>
	                     
						<div class="admNavPanelInp" style="float: right; padding-left: 0px;">
							<div class="imgL"></div>
                            <div><input type="submit" onclick="document.forms['siteHistoryForm'].act.value='export'" name="selectImageButton" value="<fmt:message key="CM_CSV_EXPORT" />"></div>
                            <div class="imgR"></div>
						</div>
					</form>
                    <div style="height:5px; clear:both;"></div>
					<c:if test="${result >= 0}">						
						<span><fmt:message key="CM_DELETED_RUSULT" /> ${result}</span>
					</c:if>
                </td>                
            </tr>

            <tr> 
	            <td class="admTDtitles" width="10%" style="height:auto; background-image: url('/images/sortable_arrows.png'); background-position: 5px; background-repeat: no-repeat;  cursor: pointer;" onclick="sort('id')">Revision</td>
	            <td class="admTDtitles" width="30%" style="height:auto; background-image: url('/images/sortable_arrows.png'); background-position: 5px; background-repeat: no-repeat; cursor: pointer;" onclick="sort('date')">Date</td>
	            <td class="admTDtitles" width="10%" style="height:auto; background-image: url('/images/sortable_arrows.png'); background-position: 5px; background-repeat: no-repeat; cursor: pointer;" onclick="sort('author')">Author</td>
	            <td class="admTDtitles" width="5%" style="height:auto; background-image: url('/images/sortable_arrows.png'); background-position: 5px; background-repeat: no-repeat;  cursor: pointer;" onclick="sort('type')">Type</td>
	            <td class="admTDtitles" width="45%" style="height:auto; background-image: url('/images/sortable_arrows.png'); background-position: 5px; background-repeat: no-repeat; cursor: pointer;" onclick="sort('title')">Title</td>
	        </tr>

	        <c:forEach var="articleRevision" items="${revisions}">
	            <tr>
	                <td class="admTableTD"><fmt:formatNumber value="${articleRevision.id}" pattern="000000"/></td>
	                <td class="admTableTD"><fmt:formatDate value="${articleRevision.date}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
	                <td class="admTableTD">${articleRevision.author}</td>
	                <td class="admTableTD">${articleRevision.type}</td>
	                <td class="admTableTDLast">${articleRevision.title}</td>
	            </tr>
	        </c:forEach> 
            <tr>
				<td class="admTableFooter" colspan="4" >&nbsp;</td>
			</tr>      
        </table>        
	</negeso:admin>
</body>
</html>