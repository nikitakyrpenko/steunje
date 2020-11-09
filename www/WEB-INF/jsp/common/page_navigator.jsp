<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:if test="${pageNavigator.maxpid > 0}">
    <c:set var="link" value="${pageNavigator.linkWithParams}" scope="application" />
    <c:set var="maxPage" value="${pageNavigator.minpid + pageNavigator.pidNumber}" scope="application" />		
		<table class="admNavPanel admPanelSpace" cellspacing="0" cellpadding="0"  align="center" border="0">
			<tr>
				<td align="admNavbar admLeft" id="nl_navigation">&nbsp;&nbsp;&nbsp;
				    <c:if test="${pageNavigator.minpid > 0}">                        
                        <a href="${link}pid=0" style="color:#5B5A5A;">
                           <c:out value="1"/>
                        </a><span style="color:#5B5A5A;">&#160;...&#160;</span>
                    </c:if>
					<c:forEach begin="${pageNavigator.minpid + 1}" end="${maxPage}" var="pageNumber" >
						<a href="${link}pid=${pageNumber - 1}"
							<c:choose>
								<c:when test="${pageNavigator.currentPid == pageNumber - 1}">style="text-decoration:none;color:red;"</c:when>
								<c:otherwise>style="color:#5B5A5A;"</c:otherwise>
							</c:choose>
							>
							<c:out value="${pageNumber}"/></a>
						<c:if test="${pageNumber != maxPage}">&#160;&#160;</c:if>
					</c:forEach>

					<c:if test="${pageNavigator.maxpid + 1 > maxPage}">
						<span  style="color:#5B5A5A;">&#160;...&#160;</span>
						<a href="${link}pid=${pageNavigator.maxpid}" style="color:#5B5A5A;">
						   <c:out value="${pageNavigator.maxpid + 1}"/>
						</a>
					</c:if>
				</td> 
			</tr>
		</table>
</c:if>