<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<% response.setDateHeader("Expires", 0); %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="negeso" %>
<fmt:setBundle basename="com.negeso.framework.i18n.DatabaseResourceBundle" var="bundle" scope="session"/>
<HTML>
<HEAD>
				<META http-equiv="Content-Type" content="text/html; charset=utf-8" />
				<link href="/css/admin_style.css" rel="stylesheet" type="text/css" />
				<TITLE><fmt:message key="SEARCH"/></TITLE>

				<script type="text/javascript" src="/script/jquery.min.js">/**/</script>				
				<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
				<script type="text/javascript" src="/script/cufon-yui.js"></script>
				<script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
				<script type="text/javascript" src="/script/common_functions.js"></script>
</HEAD>
<BODY>
<negeso:admin>
    <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
	<tr>
            <td>
                <jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp" />
             </td>
        </tr>
        <tr> 
        	<td class="admTDtitles" align="center">
        		<fmt:message key="SEARCH"/>
        	</td>                                     
        </tr>        
        <tr> 
            
	</tr>
	<tr> 
	<td class="">
			<c:choose>
				<c:when test="${status eq 'finished'}">
					Search index has been updated
				</c:when>
				<c:when test="${status eq 'busy'}">
					Search index is being built now...
				</c:when>
				<c:otherwise>
				</c:otherwise>
			</c:choose>
		</td>
	</tr>
        <tr>
            <td class="admTableFooter">&nbsp;</td>
        </tr>      
</table>
</negeso:admin>

<c:if test="${not (status eq 'busy')}">
	<form method="post" id="sendForm">
		<input type="hidden" value="reindex" name="action"/>
	</form>
	<table cellpadding="0" cellspacing="0"  width="764px"  align="center"  border="0" class="Buttons">
		<tr>
	       <td>
				<div class="admBtnGreenb">
					<div class="imgL"></div>
	                <div><input type="button" value='Rebuild index' onClick="$('#sendForm').submit();"/></div>
	                <div class="imgR"></div>
	            </div>
			</td>
		</tr>
	</table>
</c:if>

</body>
</html>