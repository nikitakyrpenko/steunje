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
				<script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"></script>

   	<script type="text/javascript">		
		function try_delete(id){
			if(confirm('<fmt:message key="NL.DELETE_ITEM_CONFIRM"/>')){
					document.forms['serviceForm'].action.value = 'delete';
					document.forms['serviceForm'].id.value = id;
					document.forms['serviceForm'].submit();
				}
		}

		function search(){
			document.forms['serviceForm'].action.value = 'showSubscribers';
			document.forms['serviceForm'].submit();
		}
	</script>
   	<title>
   		<fmt:message key="NL.SUBSCRIBERS"/>
   	</title>
</head>
<body>
	
    <negeso:admin>
	<table align="center" border="0" cellpadding="0" cellspacing="0" class="admTable">
		<tr>
			<td style="width:auto; height:auto;" colspan="4">  
				<jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp" />
			</td>
		</tr>
		<!-- Title table -->
		<tr>	
			<td align="center" class="admNavPanelFont"  style="height:auto;" colspan="4" >
				<fmt:message key="NL.SUBSCRIBERS_LIST"/>
			</td>            
		</tr>
		<tr>
			<td colspan="4" class="" align="center">
				
				<form action="/admin/nl_subscriberslist" method="POST" name="serviceForm" id="serviceForm" width="100%">
					<input type="hidden" name="action" value="">
					<input type="hidden" name="id" value="">
					<input type="hidden" name="pid" value="${sessionScope.pid}">
					<table cellspacing="0" cellpadding="10"  align="center" border="0" width="764">
	       				<tr>
							<th class="admTableTDLast">
	            				<fmt:message key="NL.FILTER_BY_GROUP"/>
							</th>
							<td class="admTableTD">
	    						<select name="groupId" onchange="search()">
		    						<option name="All" value=""> - <fmt:message key="NL.ALL"/> - </option>
		    						<option name="All" value="0"
											<c:if test="${sessionScope.groupId == 0}">
												<c:out value="selected=\"selected\""/>
											</c:if>
											> - <fmt:message key="NL.NO_GROUPS"/> - </option>
	    							<c:forEach var="group" items="${groups}">
		    							<option id="${group.id}" name="${group.id}" value="${group.id}"
		    								<c:if test="${sessionScope.groupId == group.id}"><c:out value="selected=\"selected\""/>
		    								</c:if>
		    							>
		    								<c:out value="${group.title}"/>
		    							</option>
	    							</c:forEach>
	    						</select>
	    					</td>
	    					<td class="admTableTDLast admRight">
	    					   &#160;<fmt:message key="NL.SORT_BY"/>
	    					</td>
	    					<td class="admTableTD">
    						 <select name="sortAttributeId" onchange="search()">
								   <!-- <option name="attribute" value=""></option> -->
									<option name="attribute" value="6"
										<c:if test="${sessionScope.sortAttributeId != 2}">
											<c:out value="selected=\"selected\""/>
										</c:if>								   
									  ><fmt:message key="NAME"/>
								   </option>
									<option name="attribute" value="2"
									  <c:if test="${sessionScope.sortAttributeId == 2}">
										  <c:out value="selected=\"selected\""/>
									  </c:if>
									  ><fmt:message key="EMAIL"/>
								   </option>
							   </select>
	    					</td>
	           				<td class="admTableTDLast">
	           					&#160;&#160;<input type="text" name="query" value="${sessionScope.query}" class="admInpText admWidth150" />
	           				</td>
	         				<td class="admTableTDLast">
								<div class="admNavPanelInp" style="padding:0;margin:0;">
									<div class="imgL"></div>
									<div>									
										<input type="button" class="admNavbarInp"
											value='<fmt:message key="SEARCH"/>' onClick="search();">
									</div>
									<div class="imgR"></div>
								</div>	         					
	           				</td>
						 </tr>
					</table>
				</form>							
	        </td>
        </tr>

		<tr>
			<td class="admTDtitles" style="height:auto">
				<fmt:message key="NAME"/>
			</td>
			<td class="admTDtitles" style="height:auto">
				<fmt:message key="EMAIL"/>
			</td>
			<td class="admTDtitles admTDtitlesCenter" style="height:auto" colspan="2">
				<fmt:message key="ACTION"/>
			</td>
		</tr>
		<!-- Content table -->
		<c:forEach var="subscriber" items="${subscribers}">
        	<tr>
	            <th class="admTableTD" width="300">
	            	<a class="admNone" href="/admin/nl_editsubscriber?&id=<c:out value="${subscriber.id}"/>"><c:if 
					test="${subscriber.firstName}"><c:out value="${subscriber.firstName}"/>&#160;</c:if><c:out value="${subscriber.lastName}"/></a>
	            </th>
	            <th class="admTableTD" width="200">
	            	<c:out value="${subscriber.email}"/>
	            </th>
	            <td class="admTableTD" align="center">
	                <a href="/admin/nl_editsubscriber?&id=<c:out value="${subscriber.id}"/>"><img 
						style="cursor: pointer" src="/images/edit.png" width="37" height="36" alt="<fmt:message key="EDIT"/>"/></a>
	            </td>
	            <td class="admTableTDLast" align="center">
                	<img class="admImg admHand" src="/images/delete.png" width="37" height="36" alt="<fmt:message key="DELETE"/>" onClick="try_delete('${subscriber.id}')"/>
	            </td>
	         </tr>
        </c:forEach>
		<tr>
			<td colspan="4">
				<jsp:include page="/WEB-INF/jsp/common/page_navigator.jsp"/>
			</td>
		</tr>		
		<tr>
			<td class="admTableFooter" colspan="4">&nbsp;</td>
		</tr>
	</table>    
    </negeso:admin>
    <table cellpadding="0" cellspacing="0"  width="764px"  align="center"  border="0" class="Buttons">
    <tr>
        <td>
            <form action="/admin/nl_export_subscribers" target="_blank" id="exportForm" name="exportForm"></form>
            <div class="admBtnGreenb">
                <div class="imgL"></div>
                <div><input type="submit" onClick="document.forms['exportForm'].submit();" value='<fmt:message key="EXPORT"/>'/></div>
                <div class="imgR"></div>
            </div>            
        </td>
    </tr>
</table>
    
</body>
</html>