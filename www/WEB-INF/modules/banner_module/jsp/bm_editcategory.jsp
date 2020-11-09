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
		<c:choose>
			<c:when test="${category.id >= 0}">
				<title>${category.title}</title>
			</c:when>
			<c:otherwise>
				<title><fmt:message key="BM.NEW_CATEGORY"/></title>
			</c:otherwise>
		</c:choose>
		<script type="text/javascript" src="/script/jquery.min.js"></script>
		<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
	    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    	<script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
		<script type="text/javascript" src="/script/common_functions.js"></script>    
		<script type="text/javascript" src="/site/core/script/validation.js" type="text/javascript">/**/</script>
	</head>
	<body>
	    <script type="text/javascript">
	    	function save(){
				if(validate(document.forms['bm_form_category']))
					document.forms['bm_form_category'].submit();
			}
		</script>
		<form name="bm_form_category" method="POST" enctype="multipart/form-data" action="/admin/bm_category.html">			
	    <negeso:admin>
		<!-- Title table -->
		<table align="center" border="0" cellpadding="0" cellspacing="0" class="admTable">
			<tr>
				<td align="center" class="admNavPanelFont"  style="height:auto;" colspan="2" >
					<c:choose>
						<c:when test="${(category.id > 0) && (fn:length(category.title)>50)}">
							${fn:substring(category.title,0,50)}...
						</c:when>
						<c:when test="${category.id >= 0}">
							${category.title}
						</c:when>
						<c:otherwise>
							<fmt:message key="BM.NEW_CATEGORY"/>
						</c:otherwise>
					</c:choose>
				</td>				
			</tr>
        	<!--CONTENT -->
            <c:if test="${category.parentId != null}">
                <spring:bind path="category.parentId">
                    <input type="hidden" name="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>" />
                </spring:bind>
            </c:if>                
            <spring:bind path="category.id">
                <input type="hidden" name="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>" />
            </spring:bind>                
            <c:if test="${category.siteId != null}">
                <spring:bind path="category.siteId">
                    <input type="hidden" name="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>" />
                </spring:bind>
            </c:if>                
            <c:if test="${category.orderNumber != null}">
                <spring:bind path="category.orderNumber">
                    <input type="hidden" name="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>" />
                </spring:bind>
            </c:if>
            <tr>
                <td colspan="2" class="admTDtitles" style="height:auto">
                    <fmt:message key="BM.TITLE"/>
                </td>
            </tr>
            <tr>
                <th class="admTableTD" width="200">
                    <fmt:message key="BM.TITLE"/>:
                </th>
                <td class="admTableTDLast">
                    <spring:bind path="category.title">
                        <input type="text" name="<c:out value="${status.expression}"/>" id="title" class="admTextArea" style="width: 400px;" 
                            value="<c:out value="${status.value}"/>" title="${category.title}" MAXLENGTH="256" />
                        <c:if test="${status.error}">
                           <c:forEach var="error" items="${status.errorCodes}">
                              <table cellspacing="0" cellpadding="0" style="height: 12px; margin: 0; padding: 0; border: 0;"><tr style="height: 12px;"><td style="font-size: 10px; color: red; border: 0; padding: 0; text-align: left; vertical-align: middle; height: 12px;"><fmt:message key="${error}"/></td></tr></table>
                           </c:forEach>							
                        </c:if>
                    </spring:bind>
                </td>
            </tr>
            <tr>
                <th class="admTableTD"><fmt:message key="BM.IS_LEAF"/>:</th>
                <td class="admTableTDLast">
                    <spring:bind path="category.isLeaf">
                        <input type="checkbox" name="${status.expression}"
                            <c:if test="${status.value == true}">
                                <c:out value="checked='true'" />
                            </c:if>
                        />
                        <c:if test="${status.error}">
                           <c:forEach var="error" items="${status.errorCodes}">
                                <table cellspacing="0" cellpadding="0" style="height: 12px; margin: 0; padding: 0; border: 0;"><tr style="height: 12px;"><td style="font-size: 10px; color: red; border: 0; padding: 0; text-align: left; vertical-align: middle; height: 12px;"><fmt:message key="${error}"/></td></tr></table>
                            </c:forEach>
                        </c:if>
                    </spring:bind>
                </td>
            </tr>
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
	                        <input type="button" class="admNavbarInp" value='<fmt:message key="SAVE_AND_CLOSE"/>' onClick="save();"/> 
                        </div>
                        <div class="imgR"></div>
                    </div>
                </td>
            </tr>
        </table>
  		</form>
	</body>
</html>