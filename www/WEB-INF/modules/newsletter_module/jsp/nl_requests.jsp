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
   	<title>
   					<fmt:message key="NL.SUBSCRIPTION_REQUESTS"/>
   	</title>

				<script type="text/javascript" src="/script/jquery.min.js">/**/</script>				
				<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
				<script type="text/javascript" src="/script/cufon-yui.js"></script>
				<script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
				<script type="text/javascript" src="/script/common_functions.js"></script>

   	<script type="text/javascript">								
								function try_delete(id){
												if(confirm('<fmt:message key="NL.DELETE_ITEM_CONFIRM"/>')){
																document.forms['serviceForm'].action.value = 'deleteSubscriptionRequest';
																document.forms['serviceForm'].id.value = id;
																document.forms['serviceForm'].submit();
												}
								}
		function confirmSubscribe(id){
			document.forms['serviceForm'].action.value = 'confirmSubscribe';
			document.forms['serviceForm'].id.value = id;
			document.forms['serviceForm'].submit();			
		}
				</script>
</head>
<body>
	<negeso:admin>
	<form action="/admin/nl_subscriberslist" method="POST" name="serviceForm" id="serviceForm">
		<input type="hidden" name="action" value="">
		<input type="hidden" name="id" value="">
		<input type="hidden" name="pid" value="${sessionScope.pid}">
	</form>
	<table align="center" border="0" cellpadding="0" cellspacing="0" class="admTable">
		<tr>
			<td style="width:auto; height:auto;" colspan="3">  
				<jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp" />
			</td>
		</tr>
		<!-- Title table -->
		<tr>	
            <td align="center" class="admNavPanelFont"  style="height:auto;" colspan="3" >
				<fmt:message key="NL.SUBSCRIPTION_REQUESTS"/>
            </td>            
        </tr>		
		<tr> 
        	<td class="admTDtitles" style="height:auto">
            	<fmt:message key="NAME"/>
            </td> 
            <td class="admTDtitles" style="height:auto">
            	<fmt:message key="EMAIL"/>
            </td> 
			<td class="admTDtitles admTDtitlesCenter" style="height:auto" colspan="3">
				<fmt:message key="ACTION"/>
			</td>
        </tr>
		<!-- Content table -->
		<c:forEach var="subscriber" items="${subscribers}">
        	<tr>
	            <th class="admTableTD" width="280">
	            	<a class="admNone" href="/admin/nl_editsubscriber?&id=<c:out value="${subscriber.id}"/>" onfocus="blur()"><c:if 
					test="${subscriber.firstName}"><c:out value="${subscriber.firstName}"/>&#160;</c:if><c:out value="${subscriber.lastName}"/></a>
	            </th>
	            <th class="admTableTD">
	            	<c:out value="${subscriber.email}"/>
	            </th>
				<td class="admTableTD" width="30px">
	                <img style="cursor: pointer" src="/images/nl_proof.png" width="37" height="36" alt="<fmt:message key="NL.CONFIRM_SUBSCRIBER"/>" onClick="confirmSubscribe('${subscriber.id}')"/>
	            </td>
				<td class="admTableTD" width="30px">
	                <a href="/admin/nl_editsubscriber?&id=<c:out value="${subscriber.id}"/>"><img 
						style="cursor: pointer" src="/images/edit.png" width="37" height="36" alt="<fmt:message key="EDIT"/>"/></a>
	            </td>
	            <td class="admTableTDLast" width="30px">
                	<img class="admImg admHand" src="/images/delete.png" width="37" height="36" alt="<fmt:message key="DELETE"/>" onClick="try_delete('${subscriber.id}')"/>
	            </td>
	         </tr>
        </c:forEach>
		<tr>
			<td colspan="5">
				<jsp:include page="/WEB-INF/jsp/common/page_navigator.jsp"/>
			</td>
		</tr>
		<tr>
			<td class="admTableFooter" colspan="5">&nbsp;</td>
        </tr>
    </table>   
    
    </negeso:admin>
</body>
</html>