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
	/* String parentId = (String)request.getAttribute("parentId");*/
%>
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
	<c:choose>
		<c:when test="${type.id == -1}">
			<title><fmt:message key="BM.ADD_TYPE"/></title>
		</c:when>
		<c:otherwise>
			<title><fmt:message key="BM.EDIT_TYPE"/></title>
		</c:otherwise>
	</c:choose>
    <script type="text/javascript" src="/script/conf.js"/>
    <script type="text/javascript" src="/script/jquery.min.js"></script>
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
	<script type="text/javascript" src="/script/common_functions.js"></script>
    <script type="text/javascript">
    	function save(){
			if(validate(document.forms['bm_form_type']))
				document.forms['bm_form_type'].submit();
		}
	</script>
</head>
<body>
	<negeso:admin>
    <!-- TITLE: begin -->
	<table align="center" border="0" cellpadding="0" cellspacing="0" class="admTable">
    	<tr>
			<td align="center" class="admNavPanelFont"  style="height:auto;" colspan="2" >
            	<c:choose>
            		<c:when test="${type.id == -1}">
            			<fmt:message key="BM.ADD_TYPE"/>
            		</c:when>
            		<c:otherwise>
            			<fmt:message key="BM.EDIT_TYPE"/>
            		</c:otherwise>
            	</c:choose>
            </td>
		</tr>
	    <!-- TITLE: end -->    

		<form name="bm_form_type" method="POST" action="/admin/bm_type.html" enctype="multipart/form-data">
        <spring:nestedPath path="type">				
            <spring:bind path="id"><input type="hidden" name="${status.expression}" value="${status.value}"></spring:bind>        
            <spring:bind path="title">
                <tr>
                    <th class="admTableTD">
                        <fmt:message key="BM.TITLE"/>*
                    </th>
                    <td class="admTableTDLast">
                        <table cellpadding="0" cellspacing="0">
                            <tr>
                                <td>
                                    <input class="admTextArea admWidth200" type="text" name="${status.expression}" value="${status.value}" />
                                </td>
                                <td>
                                    <table cellspacing="0" cellpadding="0" style="height: 12px; margin: 0; padding: 0; border: 0;">
                                        <c:forEach var="error" items="${status.errorCodes}">
                                            <tr style="height: 12px;">
                                                <td colspan="5" class="admTableTDLast" style="font-size: 10px; color: red; border: 0; padding: 0; text-align: left; vertical-align: middle; height: 12px;" >
                                                    <fmt:message key="${error}"/>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </table>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </spring:bind>
            
            <spring:bind path="width">
                <tr>
                    <th class="admTableTD">
                        <fmt:message key="BM.WIDTH"/>*
                    </th>
                    <td class="admTableTDLast">
                        <table cellpadding="0" cellspacing="0">
                            <tr>
                                <td>
                                    <input class="admTextArea admWidth200" type="text" name="${status.expression}" value="${status.value}" />
                                </td>
                                <td>
                                    <table cellspacing="0" cellpadding="0" style="height: 12px; margin: 0; padding: 0; border: 0;">
                                        <c:forEach var="error" items="${status.errorCodes}">
                                            <tr style="height: 12px;">
                                                <td colspan="5" class="admTableTDLast" style="font-size: 10px; color: red; border: 0; padding: 0; text-align: left; vertical-align: middle; height: 12px;" >
                                                   <c:if test="${error ne 'typeMismatch'}">
                                                       <fmt:message key="${error}"/>
                                                   </c:if>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </table>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </spring:bind>
            
            <spring:bind path="height">
                <tr>
                    <th class="admTableTD">
                        <fmt:message key="BM.HEIGHT"/>*
                    </th>
                    <td class="admTableTDLast">
                        <table cellpadding="0" cellspacing="0">
                            <tr>
                                <td>
                                    <input class="admTextArea admWidth200" type="text" name="${status.expression}" value="${status.value}" />
                                </td>
                                <td>
                                    <table cellspacing="0" cellpadding="0" style="height: 12px; margin: 0; padding: 0; border: 0;">
                                        <c:forEach var="error" items="${status.errorCodes}">
                                            <tr style="height: 12px;">
                                                <td colspan="5" class="admTableTDLast" style="font-size: 10px; color: red; border: 0; padding: 0; text-align: left; vertical-align: middle; height: 12px;" >
                                                   <c:if test="${error ne 'typeMismatch'}">
                                                        <fmt:message key="${error}"/>
                                                   </c:if>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </table>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </spring:bind>
        
        </spring:nestedPath>
   		<tr>
			<td class="admTableFooter" colspan="2">&nbsp;</td>
        </tr>
    </table>
	</negeso:admin>
    <table cellpadding="0" cellspacing="0"  width="764px"  align="center"  border="0" class="Buttons">
        <tr>
            <td>
                <div class="admBtnGreenb">
                    <div class="imgL"></div>
                    <div>   
	                    <input type="button" class="admNavbarInp" value='<fmt:message key="SAVE"/>' onClick="save();">
                    </div>
                    <div class="imgR"></div>
                </div>
                <div class="admBtnGreenb admBtnBlueb">
                    <div class="imgL"></div>
                    <div> 
	                    <input type="reset" class="admNavbarInp" value='<fmt:message key="RESET"/>' onClick="">
                    </div>
                    <div class="imgR"></div>
                </div>
            </td>
        </tr>
    </table>
    </form>
</body>
</html>