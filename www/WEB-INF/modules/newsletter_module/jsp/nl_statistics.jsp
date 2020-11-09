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

   	<script type="text/javascript">
								
			function try_delete(id){
				if(confirm('Are you sure to delete this item?'))
					alert('Subscriber was delete from group');
			}
			function try_delete_subscriber(id, action, pid){
				if(confirm('<fmt:message key="NL.DELETE_SUBSCRIBER_CONFIRM"/>'))
					document.location='/admin/nl_subscriberslist?action=deleteFromStatistics&param=' + action + '&id='+id + '&pid=' + pid;
			}
			
		</script>
   	<title>Statistics</title>
</head>
<body>
	
	<negeso:admin>
	<table align="center" border="0" cellpadding="0" cellspacing="0" class="admTable">
		<tr>
			<td style="width:auto; height:auto;">  
				<jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp" />
			</td>
		</tr>
		<tr>	
            <td align="center" class="admNavPanelFont"  style="height:auto;">
				Statistics
            </td>            
        </tr>		
		<tr>
			<td style="background:#fff;">				
			<table cellpadding="0" cellspacing="0" border="0" width="764" style="margin:10px 0  0 0">
				<tr>
					<td style="width:129px" id="tab_attributes" align="center"
						<c:choose>
	        				<c:when test="${param.status == 'sent'}">class="tab_active"</c:when>
    	    				<c:otherwise>class="tab_inactive"</c:otherwise>
    	    			</c:choose>
					>
						<a href="/admin/nl_statistics?action=${param.action}&id=${param.id}&status=sent" onfocus="blur()"><fmt:message key="NL.SENT"/></a>
					</td>
					<td style="width:129px" id="tab_confirmation" align="center"
						<c:choose>
	        				<c:when test="${param.status == 'not_sent'}">class="tab_active"</c:when>
    	    				<c:otherwise>class="tab_inactive"</c:otherwise>
    	    			</c:choose>
					>
						<a href="/admin/nl_statistics?action=${param.action}&id=${param.id}&status=not_sent" onfocus="blur()"><fmt:message key="NL.NOT_SENT"/></a>
					</td>
					<td style="width:129px" id="tab_confirmation" align="center"
						<c:choose>
	        				<c:when test="${param.status == 'bounced'}">class="tab_active"</c:when>
    	    				<c:otherwise>class="tab_inactive"</c:otherwise>
    	    			</c:choose>
					>
						<a href="/admin/nl_statistics?action=${param.action}&id=${param.id}&status=bounced" onfocus="blur()"><fmt:message key="NL.BOUNCED"/></a>
					</td>
					<td style="width:129px" id="tab_confirmation" align="center"
						<c:choose>
	        				<c:when test="${param.status == 'read'}">class="tab_active"</c:when>
    	    				<c:otherwise>class="tab_inactive"</c:otherwise>
    	    			</c:choose>
					>
						<a href="/admin/nl_statistics?action=${param.action}&id=${param.id}&status=read" onfocus="blur()"><fmt:message key="NL.READ"/></a>
					</td>
					<td style="width:248px;border-bottom:1px solid #dfdfdf;text-align:right;padding:5px 5px 0 0;">
						<b><fmt:message key="NL.TOTAL"/>: <c:out value="${pageNavigator.count}"/></b>
					</td>
				</tr>
			</table>
			</td>
		</tr>
		<tr> 
			<td style="background:#fff;">
				<table cellpadding="0" cellspacing="0" border="0" width="764">
					<tr> 
						<td class="admTDtitles" style="height:auto">#</td>
						<td class="admTDtitles" style="height:auto">
            				<fmt:message key="FIRST_NAME"/>
						</td>
        				<td class="admTDtitles" style="height:auto">
            				<fmt:message key="NL.LAST_NAME"/>
						</td>
        				<td class="admTDtitles" style="height:auto">
            				<fmt:message key="EMAIL"/>
						</td>
						<c:choose>
							<c:when test="${param.status == 'sent'}">
		        				<td class="admTDtitles" style="height:auto">
		            				<fmt:message key="NL.SENT_DATE"/>
								</td>
							</c:when>
							<c:when test="${param.status == 'not_sent'}">
		        				<td class="admTDtitles" style="height:auto">
		            				<fmt:message key="NL.REASON"/>
								</td>
								<td class="admTDtitles" style="height:auto">
		            				<fmt:message key="NL.SENT_DATE"/>
								</td>
								<td class="admTDtitles" style="height:auto">
		            				&#160;
								</td>
							</c:when>
							<c:when test="${param.status == 'bounced'}">
		        				<td class="admTDtitles" style="height:auto">
		            				<fmt:message key="NL.REASON"/>
								</td>
		        				<td class="admTDtitles" style="height:auto">
		            				<fmt:message key="NL.BOUNCE_TIME"/>
								</td>
								<td class="admTDtitles" style="height:auto">
		            				&#160;
								</td>
							</c:when>
							<c:when test="${param.status == 'read'}">
		        				<td class="admTDtitles" style="height:auto">
		            				<fmt:message key="NL.READ_TIME"/>
								</td>
							</c:when>
						</c:choose>
					</tr>

					<c:forEach var="statlist" items="${statlists}" varStatus="status">
						<tr>
							<th class="admTableTD">
								<c:out value="${status.count + pageNavigator.currentPid * pageNavigator.recordsPerPage}"/>		 
							</th>
							<td class="admTableTD">
								<c:out value="${statlist.subscriber.firstName}"/>&#160;
							</td>
							<td class="admTableTD">
								<c:out value="${statlist.subscriber.lastName}"/>&#160;
							</td>
							<td class="admTableTD">
								&#160;<a href="/admin/nl_editsubscriber?id=${statlist.subscriber.id}" onfocus="blur()"><c:out value="${statlist.subscriber.email}"/></a>
							</td>
							<c:choose>
								<c:when test="${param.status == 'sent'}">
									<td class="admTableTDLast">
			            				<fmt:formatDate value="${statlist.createdDate}" type="both" pattern="yyyy-MM-dd HH:mm:ss" />&#160;
									</td>
								</c:when>
								<c:when test="${param.status == 'bounced'}">
			        				<td class="admTableTD">
		        						<c:choose>
		        							<c:when test="${fn:length('Some text as reason') > 15}">
		        								<table cellpadding="0" cellspacing="0" border="0">
		        								<tr><td class="admBottom">
			        								<c:out value="${fn:substring(statlist.message, 0, 15)}"/>...
		        								</td><td>
			        								<img class="admHand admNoMargin" src="/images/help.gif" onClick="alert('${statlist.message}');" 
			        									alt="<fmt:message key="NL.DETAILED_REASON"/>"	title="<fmt:message key="HELP"/>">
		        								</td></tr>
		        								</table>
		        							</c:when>
		        							<c:otherwise>
		        								<c:out value="${statlist.message}"/>&#160;
		        							</c:otherwise>
			            				</c:choose>
			        					<div id="reason_">
			            				</div>
									</td>
			        				<td class="admTableTD">
			            				<fmt:formatDate value="${statlist.createdDate}" type="both" pattern="yyyy-MM-dd HH:mm:ss" />&#160;
									</td>
									<td class="admTableTDLast">
			            				<img class="admImg admHand" src="/images/suspend_mail.gif" width="31px" height="27px" title="<fmt:message key="NL.UNSUBSCRIBE_FROM_ALL_GROUPS"/>"
                							alt="<fmt:message key="NL.SUSPEND"/>" 
                							onClick="window.location.href='/admin/nl_subscriberslist?action=unSubscribe&id=${statlist.subscriber.id}&publicationId=${param.id}'"/>
									</td>
								</c:when>
								<c:when test="${param.status == 'not_sent'}">
			        				<td class="admTableTD">
			            				<c:out value="${statlist.message}"/>&#160;
									</td>
									<td class="admTableTD">
			            				<fmt:formatDate value="${statlist.createdDate}" type="both" pattern="yyyy-MM-dd HH:mm:ss" />&#160;
									</td>
									<td class="admTableTDLast">
			            				<img class="admImg admHand" src="/images/suspend_mail.gif" width="31px" height="27px" title="<fmt:message key="DELETE"/>"
                							alt="<fmt:message key="NL.SUSPEND"/>" 
                							onClick="try_delete_subscriber('${statlist.subscriber.id}', '${param.action}', '${param.id}')"/>
									</td>
								</c:when>
								<c:when test="${param.status == 'read'}">
			        				<td class="admTableTDLast">
			            				<fmt:formatDate value="${statlist.createdDate}" type="both" pattern="yyyy-MM-dd HH:mm:ss" />&#160;
									</td>
								</c:when>
							</c:choose>
						</tr>
    				</c:forEach>
				</table>
			</td>            
		</tr>
		<!-- Content table -->		
		<tr>
			<td class="admTableFooter">&nbsp;</td>
        </tr>
    </table>    
    
    <!-- Content table -->
	
	
	
	
	<c:if test="${pageNavigator.maxpid > 0}">
		<table class="admNavPanel admPanelSpace" cellspacing="0" cellpadding="0"  align="center" border="0">
			<tr>
				<td align="admNavbar admLeft">
					<c:forEach begin="${pageNavigator.minpid + 1}" end="${pageNavigator.minpid + pageNavigator.pidNumber}" var="pageNumber" >
						<a href="/admin/nl_statistics?action=${param.action}&id=${param.id}&status=${param.status}&pid=${pageNumber - 1}"
							<c:choose>
								<c:when test="${pageNavigator.currentPid == pageNumber - 1}">
									style="text-decoration:none;color:red;"
								</c:when>
								<c:otherwise>
									style="text-decoration:none;color:blue;"
								</c:otherwise>
							</c:choose>
						>
						<c:out value="${pageNumber}"/>
						</a>&#160;&#160;
					</c:forEach>
				</td> 
			</tr>
		</table>
	</c:if>
	
    </negeso:admin>
</body>
</html>