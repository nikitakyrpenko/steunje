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
   	<title><fmt:message key="BM.CATEGORIES_LIST"/></title>
	<script type="text/javascript" src="/script/jquery.min.js"></script>
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
	<script type="text/javascript" src="/script/common_functions.js"></script>    
</head>
<body>
    <script type="text/javascript">
		function try_to_delete(category_id){
			if(confirm('Are you sure to delete this category?')){
				document.location = 'bm_categorylist.html?action=deleteCategory&categoryId='+category_id;
			}
		}
    </script>
    <!-- Title table -->
	<form method = "post" enctype="multipart/form-data">
    <negeso:admin>
	<table align="center" border="0" cellpadding="0" cellspacing="0" class="admTable">
    	<tr>
			<td align="center" class="admNavPanelFont"  style="height:auto;" colspan="5" >
				<fmt:message key="BM.CATEGORIES_LIST"/>
            </td>            
        </tr>
        <spring:hasBindErrors name="global">
            <spring:bind path="category.title">
                <c:forEach items="${status.errorCodes}" var="error">
                    <tr>
                        <td colspan="5" class="admTableTDLast" style="font-size: 10px; color: red; border: 0; padding: 0; text-align: left; vertical-align: middle; height: 12px;" >
                            <fmt:message key="${error}"/>
                        </td>
                    </tr>
                </c:forEach>
            </spring:bind>
        </spring:hasBindErrors>
    	<!-- Content table -->
	    <c:if test="${error_message ne null}">
	        <tr>
	            <td colspan="5" class="admTableTDLast" style="font-size: 10px; color: red; border: 0; padding: 0; text-align: left; vertical-align: middle; height: 12px;" >
	                <fmt:message key="${error_message}"/>
	            </td>
	        </tr>	
	    </c:if>
        <tr> 
			<td class="admTDtitles" style="height:auto">
            	<fmt:message key="BM.TITLE"/>
            </td> 
            <td class="admTDtitles" style="height:auto" colspan="4">
            	<fmt:message key="BM.ACTION"/>
            </td> 
        </tr> 
        <c:forEach var="category" items="${categorylist}">
            <tr>
                <th class="admTableTD">
                    <a class="admNone" style="font-weight: bold; color: black" href='bm_categorylist.html?action=categoryContent&parentId=${category.id}'>
                        <c:choose>
                            <c:when test="${fn:length(category.title)>50}">
                                ${fn:substring(category.title,0,50)}...
                            </c:when>
                            <c:otherwise>
                                ${category.title}
                            </c:otherwise>
                        </c:choose>
                    </a>
                </th>
                <td class="admTableTD" width="30px">
                    <a href="bm_categorylist.html?action=changeDirection&direction=up<c:if test="${not empty parentId}">&parentId=<c:out value="${parentId}"/></c:if>&categoryId=${category.id}"><img class="admImg" style="cursor: pointer" src="/images/up.png" width="37px" height="36px" alt="<fmt:message key="UP"/>"/></a>
                </td>
                <td class="admTableTD" width="30px">
                    <a href="bm_categorylist.html?action=changeDirection&direction=down<c:if test="${not empty parentId}">&parentId=<c:out value="${parentId}"/></c:if>&categoryId=${category.id}"><img class="admImg" style="cursor: pointer" src="/images/down.png" width="37px" height="36px" alt="<fmt:message key="DOWN"/>"/></a>
                </td>
                <td class="admTableTD" width="30px">
                    <a href="bm_categorylist.html?action=editCategory&categoryId=${category.id}"><img class="admImg" style="cursor: pointer" src="/images/edit.png" width="37px" height="36px" alt="<fmt:message key="EDIT"/>"/></a>
                </td>
                <td class="admTableTDLast" width="30px">
                    <a href="javascript:try_to_delete('${category.id}')"><img class="admImg" style="cursor: pointer" src="/images/delete.png" width="37px" height="36px" alt="<fmt:message key="DELETE"/>"/></a>
                </td>
            </tr>
        </c:forEach>
  		<tr>
			<td class="admTableFooter" colspan="5">&nbsp;</td>
        </tr>
    </table>   
	</negeso:admin>
    <table cellpadding="0" cellspacing="0"  width="764px"  align="center"  border="0" class="Buttons">
        <tr>
            <td>
                <div class="admBtnGreenb">
                    <div class="imgL"></div>
                    <div>                    	                        
                        <input type="button" class="admNavbarInp" value='<fmt:message key="BM.ADD_CATEGORY"/>' 
                            <c:choose>
                                <c:when test="${parentId == null}">                                
                                    onClick="window.location.href='bm_categorylist.html?action=addCategory'"
                                </c:when>
                                <c:otherwise>                        
                                    onClick="window.location.href='bm_categorylist.html?action=addCategory&parentId=<c:out value="${parentId}" />'"
                                </c:otherwise>
                            </c:choose>
                         >
                    </div>
                    <div class="imgR"></div>
                </div>
            </td>
        </tr>
    </table>
    </form>
</body>
</html>