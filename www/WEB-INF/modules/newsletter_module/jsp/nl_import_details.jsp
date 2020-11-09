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
<%@ taglib tagdir="/WEB-INF/tags" prefix="negeso" %>
<html>
	<head>
	    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
	    <title><fmt:message key="NL.IMPORT_SUBSCRIBERS"/></title>				
	   <script type="text/javascript" src="/script/jquery.min.js">/**/</script>
	   <script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
		<script type="text/javascript" src="/script/cufon-yui.js"></script>
		<script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
				<script type="text/javascript" src="/script/common_functions.js"></script>
				<script type="text/javascript" src="/site/core/script/validation.js" type="text/javascript">/**/</script>
	</head>
	<body>
	    <negeso:admin>
		
		<!-- Title table -->
		<table align="center" border="0" cellpadding="0" cellspacing="0"  width="764px">
			<tr>
			<td style="width:auto; height:auto;" colspan="2" >  
				<jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp" />
			</td>
		</tr>                      
    	<tr>        	
            <td  align="center" class="admNavPanelFont"  style="height:auto;" colspan="2" >
					<fmt:message key="NL.IMPORT_SUBSCRIBERS"/>
				</td>
			</tr>
		</table>
		<table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">			
			<tr>
				<td class="admTDtitles">
					<fmt:message key="DATE"/>
				</td>
				<td class="admTDtitles">
					<fmt:message key="NL.AUTHOR"/>
				</td>
				<td class="admTDtitles">
					<fmt:message key="NL.EVENT"/>
				</td>
				<td class="admTDtitles">
					<fmt:message key="DESCRIPTION"/>
				</td>
				<td class="admTDtitles">
					<fmt:message key="NL.RESULT"/>
				</td>
			</tr>
			<c:choose>
				<c:when test="${fn:length(events) > 0}">
					<c:forEach var="event" items="${events}">
						<tr>
							<td class="admTableTD" align="center">
								<fmt:formatDate value="${event.date}" type="both" pattern="yyyy-MM-dd" />
							</td>
							<td class="admTableTD" align="center">
								<c:out value="${event.author}"></c:out>
							</td>
							<td class="admTableTD" align="center">
								<c:out value="${event.event}"></c:out>
							</td>
							<th class="admTableTD">
								<c:out value="${event.description}"></c:out>
							</th>
							<td class="admTableTDLast" align="center">
								<c:choose>
									<c:when test="${event.result != 'OK'}">
										<span class="admRed admBold">
									</c:when>
									<c:otherwise>
										<span class="admBold">
									</c:otherwise>
								</c:choose>
									<c:out value="${event.result}"></c:out>
								</span>
							</td>
						</tr>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<tr>
						<td class="admTableTDLast" colspan="5">
							<fmt:message key="NL.NO_EVENTS"/>
						</td>
					</tr>
				</c:otherwise>
			</c:choose>
			<td class="admTableFooter" colspan="5" >&nbsp;</td>
		</table>
	</negeso:admin>
	</body>
</html>
	<!--  a href="/admin/nl_import?action=importDetails" -->
