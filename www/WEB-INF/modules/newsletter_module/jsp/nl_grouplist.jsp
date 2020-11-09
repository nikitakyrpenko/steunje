<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="negeso" %>
<%@ page session="true"%>
<%
	response.setHeader("Expires", "0");
%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
				<script type="text/javascript" src="/script/jquery.min.js">/**/</script>				
				<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
				<script type="text/javascript" src="/script/common_functions.js"></script>

    <title>
   		<fmt:message key="NL.GROUP_LIST"/>
   	</title>
</head>
<body>
	<script type="text/javascript">		
				function try_delete(id, count){
        //TODO: i18n
        conf = 'Are you sure to delete this item?';
        if (count > 0){
            conf = 'This group contains subscribers, are you sure you want to delete this group?';              
        }
        if(confirm(conf))
			document.location='/admin/nl_grouplist?action=delete&id='+id;
	}
	</script>

    <negeso:admin>
	<table align="center" border="0" cellpadding="0" cellspacing="0" class="admTable">
		<tr>
			<td style="width:auto; height:auto;" colspan="8">  
				<jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp" />
			</td>
		</tr>
		<tr>	
            <td align="center" class="admNavPanelFont"  style="height:auto;" colspan="8" >
				<fmt:message key="NL.GROUP_LIST"/>
            </td>            
        </tr>
		<!-- Validation errors -->
		<c:forEach items="${errors}" var="error">
	    	<tr>
	    		<td colspan="4" class="admTableTDLast" style="font-size: 10px; color: red; border: 0; padding: 0; text-align: left; vertical-align: middle; height: 12px;" >
	    			<c:out value="${error}"/>
				</td>
	    	</tr>
    	</c:forEach>
		<!-- Add button -->
		<tr>
	    	<td colspan="8" class="admTableTDLast" align="center">
				<div class="admNavPanelInp" style="padding:0;margin:0;">
					<div class="imgL"></div>
                    <div>						
						<input type="button" class="admNavbarInp" 
							value='<fmt:message key="NL.ADD_NEW_GROUP"/>' 
							onClick="window.location.href='/admin/nl_editgroup'">
					</div>
                    <div class="imgR"></div>
				</div>				
	        </td>
	    </tr> 	   
		<tr> 
        	<td class="admTDtitles" style="height:auto">
            	<fmt:message key="NL.GROUP_TITLE"/>
            </td> 
            <td class="admTDtitles" style="height:auto" >
            	<fmt:message key="NL.SUBSCRIBERS"/>
            </td>
			<td class="admTDtitles admTDtitlesCenter" style="height:auto" >
            	<fmt:message key="NL.GROUP_TYPE"/>
            </td> 
			<td class="admTDtitles admTDtitlesCenter" style="height:auto" colspan="4">
            	<fmt:message key="ACTION"/>
            </td> 
        </tr>
		<!-- Content table -->
		<c:forEach var="group" items="${grouplist}">
        	<tr>
            	<th class="admTableTD">
                	<a href="/admin/nl_editgroup?&gid=<c:out value="${group.id}"/>"><c:choose><c:when 
						test="${fn:length(group.title)>50}"><c:out 
						value="${fn:substring(group.title,0,50)}"></c:out>...</c:when><c:otherwise><c:out 
						value="${group.title}"/></c:otherwise></c:choose></a>
				</th>
				<td class="admTableTD admCenter" width="160">
					<c:choose>
						<c:when test="${fn:length(group.subscribers) > 0}">
							<a href="/admin/nl_subscriberslist?action=showSubscribers&groupId=${group.id}"/><c:out value="${fn:length(group.subscribers)}"/></a>
						</c:when>
						<c:otherwise>
							<c:out value="${fn:length(group.subscribers)}" />
						</c:otherwise>
					</c:choose>
	            </td>
	            <td class="admTableTD admCenter" width="100">
	            	<c:choose>
	            		<c:when test="${group.internal == true}">
	            		    <fmt:message key="NL.INTERNAL"/>
	            		</c:when>
	            		<c:otherwise>
                            <fmt:message key="NL.EXTERNAL"/>
	            		</c:otherwise>
	            	</c:choose>
	            </td>
	            <td class="admTableTD" width="30px">
	            	<a href="/admin/nl_grouplist?action=changeDirection&id=${group.id}&direction=true"><img class="admImg" style="cursor: pointer" src="/images/up.png" width="37" height="36" alt="<fmt:message key="UP"/>"/></a>
	            </td>
	            <td class="admTableTD" width="30px">
	                <a href="/admin/nl_grouplist?action=changeDirection&id=${group.id}&direction=false"><img class="admImg" style="cursor: pointer" src="/images/down.png" width="37" height="36" alt="<fmt:message key="DOWN"/>"/></a>
	            </td>		 
	            <td class="admTableTD" width="30px">
	                <a href="/admin/nl_editgroup?&gid=<c:out value="${group.id}"/>">
	                	<img class="admImg" style="cursor: pointer" src="/images/edit.png" width="37" height="36" alt="<fmt:message key="EDIT"/>"/>
	                </a>
	            </td>
	            <td class="admTableTDLast" width="30px">
					<c:choose>
	            		<c:when test="${group.id==1}">
			                <img class="admImg" src="/images/delete_gray.png" width="37" height="36" alt="<fmt:message key="DELETE"/>" />	            		   
	            		</c:when>
	            		<c:otherwise>
           	                <img class="admImg admHand" src="/images/delete.png" width="37" height="36" alt="<fmt:message key="DELETE"/>" onClick="try_delete('${group.id}',${fn:length(group.subscribers)})"/>
	            		</c:otherwise>
	            	</c:choose>

	            </td>
	         </tr>
        </c:forEach>
		<tr>
			<td class="admTableFooter" colspan="8">&nbsp;</td>
        </tr>
    </table>   
    
   	</negeso:admin>
</body>
</html>