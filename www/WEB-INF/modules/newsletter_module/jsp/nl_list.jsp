<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ page session="true"%>
<%
	response.setHeader("Expires", "0");
%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
   	<title>
								Statistics
								<%--  fmt:message key="NL.STATISTICS"/ --%>
   	</title>

				<script type="text/javascript" src="/script/jquery.min.js">/**/</script>
				<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
				<script type="text/javascript" src="/script/common_functions.js"></script>
</head>
<body>
	
	<negeso:admin>
	
	<form name="editForm" method="POST" action="/admin/site_settings">
	<input type="hidden" name="action" value="save" />
	<table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">		
        <tr>
			<td style="width:auto; height:auto;"><jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp"/></td>
		</tr>
        <tr>			
            <td align="center" class="admNavPanelFont"  style="height:auto;" colspan="2" >ttt</td>		
		</tr>	
		<tr> 
			<td class="admTDtitles" width="50%" style="height:auto;"><fmt:message key="TITLE"/></td> 
			<td class="admTDtitles" width="50%" style="height:auto;"><fmt:message key="SS.VALUE"/></td> 
		</tr>
		<tr>
			<td class="admTableTD"><fmt:message key="SS.FAVICON"/></td> 
			<td class="admTableTDLast">
				<table border="0" cellpadding="0" cellspacing="0" >
					<tr>
						<td style="width:220px;">
							<input class="admTextArea admWidth200" readonly="true" id="favicon"	type="text" name="FAVICON_PATH" data_type="text" value="${FAVICON_PATH}"/>
						</td>
						<td>
							<div class="admNavPanelInp">
								<div class="imgL"></div>
								<div><input type="button" value="Select" name="selectImageButton"	onclick="setFavIcon();"></div>
								<div class="imgR"></div>
							</div>
						</td>
					</tr>
				</table>
			</td> 
		</tr>
		<tr>
			<td class="admTableTD"><fmt:message key="SS.BROWSER_PREFIX"/></td> 
			<td class="admTableTDLast">
				<input class="admTextArea admWidth200" 	type="text" name="BROWSER_PREFIX" data_type="text" value="${BROWSER_PREFIX}"/>
			</td> 
		</tr>
		<tr>
			<td class="admTableTD"><fmt:message key="SS.BROWSER_SUFFIX"/></td> 
			<td class="admTableTDLast">
				<input class="admTextArea admWidth200" 	type="text" name="BROWSER_SUFFIX" data_type="text" value="${BROWSER_SUFFIX}"/>
			</td> 
		</tr>			
		<tr>
            <td class="admTableTD"><fmt:message key="SS.IS_PARTICIPATE_OF_RANK_BOOSTER"/></td> 
            <td class="admTableTDLast">
				<input style="float: left;" type="checkbox" name="isParticipleOfRankBooster"  value="true"
					<c:if test="${isParticipleOfRankBooster == 'true'}">checked="checked"</c:if>
                 />
			</td> 
		</tr>
		<tr>
			<td class="admTableTD"><fmt:message key="SS.IS_MULTI_DOMAINS_LINKS"/></td> 
			<td class="admTableTDLast">
				<input style="float: left;" type="checkbox" name="IS_MULTI_DOMAINS_LINKS"  value="true"
					<c:if test="${IS_MULTI_DOMAINS_LINKS == 'true'}">checked="checked"</c:if>
                />
            </td> 
		</tr>       
		<tr>
			<td class="admTDtitles" style="height:auto;"><fmt:message key="SS.LANGUAGE"/></td>
			<td  class="admTDtitles" style="height:auto;" ><fmt:message key="SS.FRONT_PAGE"/></td>
		</tr>	
		<tr>
			<td class="admTableFooter">&nbsp;</td>
        </tr>
	</table>
</form>	
</negeso:admin>

<table cellpadding="0" cellspacing="0"  width="764px"  align="center"  border="0" class="Buttons">
	<tr>
		<td>
			<div class="admBtnGreenb">
				<div class="imgL"></div>
                <div><input type="submit" onClick="saveform();" name="saveform" value='<fmt:message key="SAVE"/>'/></div>
				<div class="imgR"></div>
            </div>
            <div class="admBtnGreenb admBtnBlueb">
				<div class="imgL"></div>
                <div><input type="reset" onClick="resetform();"  name="resetform" value='<fmt:message key="RESET"/>'></div>
				<div class="imgR"></div>
            </div>
        </td>
	</tr>
</table>
</body>
</html>