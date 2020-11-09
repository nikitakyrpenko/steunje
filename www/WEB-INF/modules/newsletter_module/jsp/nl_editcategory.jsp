<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ page session="true"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="negeso" %>
<html>
 <head>
	   <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	   <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
				
				<script type="text/javascript" src="/script/jquery.min.js">/**/</script>				
				<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
				<script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
				<script type="text/javascript" src="/script/common_functions.js"></script>
				<c:choose>
								<c:when test="${category.id > 0}">
												<title><fmt:message key="NL.NEW_SUBSCRIPTION_CATEGORY"/> '${category.title}'</title>
								</c:when>
								<c:otherwise>
												<title>
																<fmt:message key="NL.NEW_SUBSCRIPTION_CATEGORY"/>
												</title>	
								</c:otherwise>
				</c:choose>	  
	</head>

	<body>
				<script type="text/javascript">
								function try_delete(id){
												if(confirm('<fmt:message key="DELETE_CONFIRMATION"/>'))
																document.location='/admin/nl_categorylist?action=deleteSubscriptionCategory&categoryId='+id;
												}
				</script>
    
				<negeso:admin>		
								<!-- Title table -->
								<table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
												<tr>
																<td  style="width:auto; height:auto;"  colspan="2">  
																				<jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp" />
           					</td>
												</tr>  
												<tr>
																<td  align="center" class="admNavPanelFont"  style="height:auto;" colspan="2" >
																				<c:choose>
																								<c:when test="${(category.id > 0) && (fn:length(category.title)>50)}">
																												<fmt:message key="NL.SUBSCRIPTION_CATEGORY"/>&#160;'${fn:substring(category.title,0,50)}...'
																								</c:when>
																								<c:when test="${category.id > 0}">
																												<fmt:message key="NL.SUBSCRIPTION_CATEGORY"/>&#160;'${category.title}'
																								</c:when>
																								<c:otherwise>
																												<fmt:message key="NL.NEW_SUBSCRIPTION_CATEGORY"/>
																								</c:otherwise>
																				</c:choose>
																</td>							
												</tr>
														
												<!--CONTENT -->
												<form name="nl_form_category" method="POST" enctype="multipart/form-data" action="/admin/nl_editcategory">			
																<spring:bind path="category.id">
																				<input type="hidden" name="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>" />
																</spring:bind>
			
																<tr>
																				<td class="admTableTDLast" id="admTableTDtext" width="200">
																								<fmt:message key="TITLE"/>
																				</td>
																				<td class="admTableTDLast" id="admTableTDtext">
																								<spring:bind path="category.title">
																												<input type="text" name="${status.expression}" id="title" class="admTextArea" style="width: 400px;" 
																																				value="${status.value}" title="${status.value}" MAXLENGTH="256" />
																												<c:if test="${status.error}">
																																<c:forEach var="error" items="${status.errorCodes}">
			                  															<table cellspacing="0" cellpadding="0" style="height: 12px; margin: 0; padding: 0; border: 0;">
			            																									<tr style="height: 12px;">
			                  																							<td style="font-size: 10px; color: red; border: 0; padding: 0; text-align: left; vertical-align: middle; height: 12px;">
				                  																										<c:out value="${error}"/>				                  																														
			                  																							</td>
			                  																			</tr>
																																				</table>
			               					         </c:forEach>							
																												</c:if>								
																								</spring:bind>
																				</td>
																</tr>			
				
                <tr>
                    <td class="admTableFooter" colspan="2" >&nbsp;</td>
                </tr>  
												</table>
								</form>
				</negeso:admin>        
        
				<table cellpadding="0" cellspacing="0"  width="764px"  align="center"  border="0" class="Buttons">
								<tr>
												<td>
																<div class="admBtnGreenb">
																				<div class="imgL"></div>
                    <div>                                
																								<nobr>
																												<input type="button" class="admNavbarInp" value='<fmt:message key="SAVE"/>' onClick="nl_form_category.submit();"/>
																								</nobr>                      
																				</div>
																				<div class="imgR"></div>
																</div>                    
												</td>
								</tr>
				</table>
        
	</body>
</html>