<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags" %>
<%response.setHeader("Expires", "0");%>
      
<div id="editAliasDialog" class="editAliasDialog" title="Alias settings">
    <span id="error_message" style="color: red;"></span>
    <br/>
    <label><b><fmt:message key="NAME"/>:</b></label><br/>
    <p><input type="text" id="alias" required="true" class="admWidth200"/><b>.html</b></p>
	<p style="font-size:11px;">also works without .html</p>
	<label><b><fmt:message key="CORE.ADDITIONAL_PARAMS"/>:</b></label><br/>
    <p><input type="text" id="paramsField" class="admWidth335"/></p>
    <p><input type="checkbox" id="isInSiteMap">&#160;&#160<fmt:message key="PUT_ALIAS_IN_SITEMAP"/></p>
    <br/>
    <c:if test="${pageId == 0}">
	    <label><b>Choose page:</b></label><br/>
	    <c:forEach var="entry" items="${descriptors}">   
	         <table cellpadding="0" cellspacing="0" border="0" height="25" align="center" width="96%">
	            <tr>	            
	                <td class="admNavPanelFont" align="center" style="height:auto; font-size:16px;">${entry.key}</td>	              
	            </tr>
	        </table>
	        <template:nodeTree node="${entry.value}"/>
	    </c:forEach>
    </c:if>
</div>