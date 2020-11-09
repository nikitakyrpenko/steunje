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
	String parentId = (String)request.getAttribute("parentId");
%>
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    <title><fmt:message key="BM.BANNER_TYPES"/></title>
	<script type="text/javascript" src="/script/conf.js"/>
    <script type="text/javascript" src="/script/jquery.min.js"></script>
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
	<script type="text/javascript" src="/script/common_functions.js"></script>
</head>

<body>
	<script type="text/javascript">
		function addType(){
			document.location = 'bm_typelist.html?action=addType';
		};
		
		function try_to_delete(type_id){
			if(confirm('Are you sure to delete this type?')){
				document.location = 'bm_typelist.html?action=deleteType&typeId='+type_id;
			}
		}
	</script>
    <negeso:admin>
    <%@ page import="com.negeso.framework.controller.SessionData" %>
    <!-- TITLE: begin -->
    <table align="center" border="0" cellpadding="0" cellspacing="0" class="admTable">
    	<tr>
			<td align="center" class="admNavPanelFont"  style="height:auto;" colspan="5" >
				<fmt:message key="BM.BANNER_TYPES"/>
            </td>            
        </tr>
        <!-- TITLE: end -->  
        <c:if test="${error_message ne null}">        
            <tr>
                <td colspan="5" class="admTableTDLast" style="font-size: 10px; color: red; border: 0; padding: 0; text-align: left; vertical-align: middle; height: 12px;" >
                    <fmt:message key="${error_message}"/>
                </td>
            </tr>	
        </c:if>
    
	    <!-- CONTENT: begin  -->
		<input type="hidden" name="id" value="${type.id}" />
		<tr> 
			<td class="admTDtitles"><fmt:message key="BM.TITLE"/></td> 			 
			<td class="admTDtitles"><fmt:message key="BM.WIDTH"/></td> 
			<td class="admTDtitles"><fmt:message key="BM.HEIGHT"/></td> 
			<td class="admTDtitles" colspan="2"><fmt:message key="BM.ACTION"/></td> 
		</tr>
		<c:forEach var="type" items="${typelist}">
			<tr>
				<th class="admTableTD">
					<a class="admNone" style="font-weight: bold; color: black" href="bm_typelist.html?action=editType&typeId=${type.id}">
						<c:out value="${type.title}" />
					</a>
				</tр>
				<td class="admTableTD">
					<c:out value="${type.width}"/>
				</td>
				<td class="admTableTD">
					<c:out value="${type.height}"/>
				</td>
				<td class="admTableTD" width="30px">
					<a href="bm_typelist.html?action=editType&typeId=${type.id}"><img class="admImg" src="/images/edit.png" width="36px" height="37px" alt="<fmt:message key="EDIT"/>" title="<fmt:message key="EDIT"/>"/></a>
                <td class="admTableTDLast" width="30px">
                    <a href="javascript:try_to_delete('${type.id}')"><img class="admImg" src="/images/delete.png" width="36px" height="37px" alt="<fmt:message key="DELETE"/>" title="<fmt:message key="DELETE"/>"/></a>
				</td>
			</tr>			
		</c:forEach>		
   		<tr>
			<td class="admTableFooter" colspan="5">&nbsp;</td>
        </tr>
	</table>
	<!-- CONTENT: end  -->
    <table cellpadding="0" cellspacing="0"  width="764px"  align="center"  border="0" class="Buttons">
        <tr>
            <td>
                <div class="admBtnGreenb">
                    <div class="imgL"></div>
                    <div>
	                    <input type="button" class="admNavbarInp" value='<fmt:message key="BM.ADD_TYPE"/>' onClick="addType();"/>
                    </div>
                    <div class="imgR"></div>
                </div>
            </td>
        </tr>
    </table>

    </negeso:admin>
</body>
</html>