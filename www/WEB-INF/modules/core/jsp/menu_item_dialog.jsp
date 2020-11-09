<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags" %>
<%response.setHeader("Expires", "0");%>

<script type="text/javascript">
	$(document).ready(function () {
		$("#mid_publishDate_val,#mid_expiredDate_val").datepicker({
			dateFormat: 'dd-mm-yy',        
			showOn: 'button',
			buttonImage: '/images/calendar.gif',
			buttonImageOnly: true
		});
	})
</script>

<div id="menuItemDialog" class="menuItemDialog" title='<fmt:message key="MENU_EDIT_MI"/>'>
    </br>
    <table class="admMarginAll">
        <tr>
            <td class="admWidth_100"><fmt:message key="Title"/>:</td>
            <td class="admWidth_300">
                <input type="text" style="width: 167px;" id="mid_title_val" class="admTextField" required="true" class="admWidth200" value="${menuItem.title}"/>
            </td>
        </tr>
	        <tr>
	        <td><fmt:message key="PUBLISH_ON"/>:</td>
	            <td class="admWidth_300">
	                <input type="text" style="width: 167px;" id="mid_publishDate_val" class="admTextField" class="admWidth200" 
	                value='<fmt:formatDate value="${menuItem.publishDate}" pattern="dd-MM-yyyy"/>'/>	                	                
	            </td>
	        </tr>
	        <tr>
	        <td><fmt:message key="EXPIRES_ON"/>:</td>
	            <td class="admWidth_300">
	                <input type="text" style="width: 167px;" id="mid_expiredDate_val" class="admTextField" class="admWidth200"
	                value='<fmt:formatDate value="${menuItem.expiredDate}" pattern="dd-MM-yyyy"/>' />	                
	            </td>
	        </tr>
        <c:if test="${menuItem.pageId == null}">
            <tr>
				<td><fmt:message key="LINK_TO_URL"/>:</td>
                    <td class="admWidth_300">
                        <input type="text" style="width: 167px;" id="mid_link_val" text="" class="admTextField" value="${menuItem.link}"/>
						<input type="button" id="link_alias_btn" value="..." style="width: 24px; height: 21px;" class="admExplor" onclick="openPageExplorer()"/>
                    </td>
                </tr>
        </c:if>            
    </table>
</div>