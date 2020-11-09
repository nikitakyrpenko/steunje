<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<table   border="0" cellpadding="0" cellspacing="0" align="left">
<tr> 
	<td style="padding:8px 0 0 5px;" valign="middle">
	<%int counter=0;%>
	<%com.negeso.framework.navigation.HistoryStack stack = 
	    (com.negeso.framework.navigation.HistoryStack)request.getSession().getAttribute(
	            com.negeso.framework.navigation.HistoryStack.HISTORY_STACK_KEY
	    );%>
		<c:forEach var="link" items="${sessionScope.historyStack.links}"> 		 
			<c:choose>
				<c:when test="${ link.doI18n == true }">
					<%if(counter+1<stack.getSize()){ %>
						<a href='back.html?go=<%=counter %>' class="admNavigation"><fmt:message key="${link.title}"/></a>&nbsp;<img 
						src="/images/navig_arrow.png"  style="vertical-align:middle;"/>
					<%}else{ %>
						<a class="admNavigation" style="text-decoration:none;"><fmt:message key="${link.title}"/></a>   
					<%} %>
					<%counter++; %>
				</c:when>
				<c:otherwise>
				<a 
					<%if(counter+1<stack.getSize()){ %>
						href='back.html?go=<%=counter %>' 
					<%} %>
					class="admNavigation">
					<c:choose>
						<c:when test="${fn:length(link.title)>50}"><c:out value="${fn:substring(link.title,0,50)}"/>...</c:when>
						<c:otherwise><c:out value="${link.title}"/></c:otherwise>
					</c:choose></a><img src="/images/navig_arrow.png"  style="vertical-align:middle;"/> &nbsp;
					<%counter++; %>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</td>
</tr>
</table>


