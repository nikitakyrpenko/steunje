<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="negeso" %>
<%response.setHeader("Expires", "0");%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
   <head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title><fmt:message key="CM_GEO_IP_LANGUAGES"/></title>
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

    <form method="post"  name="editForm" >
	    <input type="hidden" name="action" value="save" />
	    <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
	        <tr>
	            <td style="width:auto; height:auto;" colspan="3"><jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp"/></td>
	        </tr>
	        <tr>                
	            <td align="center" class="admNavPanelFont"  style="height:auto;" colspan="3"><fmt:message key="CM_GEO_IP_LANGUAGES" /></td>
	        </tr>
	        <tr> 
	            <td class="admTDtitles" width="50%" style="height:auto"><fmt:message key="COUNTRY"/></td>
	            <td class="admTDtitles" width="30%" style="height:auto"><fmt:message key="COUNTRY_CODE"/></td>
	            <td class="admTDtitles" width="20%" style="height:auto"><fmt:message key="LANGUAGE"/></td> 
	        </tr>
	        <c:forEach var="country" items="${countries}">
	            <tr>
	                <td class="admTableTD">${country.name}</td>
	                <td class="admTableTD">${country.code}</td>
	                <td class="admTableTD">
	                <c:set var="langCode" value=""></c:set>
	                <c:forEach var="lang" items="${languages}" >
	                    <c:forEach var="langCountry" items="${lang.countries}" >
	                        <c:if test="${country.code == langCountry.key}">
	                            <c:set var="langCode" value="${lang.code}"></c:set>
	                        </c:if>
	                    </c:forEach>
	                </c:forEach>
	                <select name="${country.code}">
		                <c:forEach var="lang" items="${languages}">
		                        <option value="${lang.code}"
		                            <c:if test="${lang.code == langCode}">
		                                selected="selected"
		                            </c:if>
		                        >${lang.language}</option>                      
		                </c:forEach>
	                </select>
	                </td>
	            </tr>
	        </c:forEach> 
	        <tr>
	            <td class="admTableFooter" colspan="3" >&nbsp;</td>
	        </tr>
	    </table>
    </form> 
</negeso:admin>        
        
<table cellpadding="0" cellspacing="0"  width="764px"  align="center"  border="0" class="Buttons">
    <tr>
       <td>
            <div class="admBtnGreenb">
                <div class="imgL"></div>
                <div><input type="button" value='<fmt:message key="SAVE"/>' onClick="document.forms['editForm'].submit()"/></div>
                <div class="imgR"></div>
            </div>
        </td>
    </tr>
</table>

</body>
</html>