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
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js">/**/</script>
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>

   	<title>
   		<fmt:message key="NL.MAIL_TEMPLATES"/>
   	</title>
	<script>		
		function deleteTemplate(id){
			if(confirm('<fmt:message key="NL.DELETE_ITEM_CONFIRM"/>'))
				document.location='/admin/nl_mailtemplates?action=delete&id='+id;
		}
	</script>
</head>
<body>
	
	<negeso:admin>
    <!-- Title table -->
	<table align="center" border="0" cellpadding="0" cellspacing="0" class="admTable">
		<tr>
			<td style="width:auto; height:auto;" colspan="2">  
				<jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp" />
			</td>
		</tr>
		<tr>	
			<td align="center" class="admNavPanelFont"  style="height:auto;" colspan="2" >
				<fmt:message key="NL.MAIL_TEMPLATES"/>
			</td>            
		</tr>
		<c:if test="${status}">
			<tr style="height: 12px;">
    			<td style="font-size: 10px; color: red; border: 0; padding: 0; text-align: left; vertical-align: middle; height: 12px;" >
					<c:out value="${status}"/>
    			</td>
    		</tr>
		</c:if>
		<tr>
			<td colspan="3" class="admTableTDLast" align="center">
				<div class="admNavPanelInp" style="padding:0;margin:0;">
					<div class="imgL"></div>
                    <div>						
						<input type="button" class="admNavbarInp" value='<fmt:message key="NL.ADD_NEW_TEMPLATE"/>' 
                			onClick="window.location.href='/admin/nl_editmailtemplate?action=add'">
					</div>
                    <div class="imgR"></div>
				</div>				
	        </td>
        </tr>
		<tr>
			<td class="admTDtitles" style="height:auto">
				<fmt:message key="NAME"/>
			</td>
			<td class="admTDtitles admTDtitlesCenter" style="height:auto" colspan="2">
				<fmt:message key="ACTION"/>
			</td>
		</tr>
		<!--CONTENT -->			
		<c:forEach var="template" items="${templates}">
			<tr>
            	<th class="admTableTD" width="620">
            		<a class="admAnchor" href="/admin/nl_editmailtemplate?action=edit&id=${template.id}">
	            		<c:out value="${template.title}"/><br>
	            	</a>
            	</th>
	            <td class="admTableTD" align="center">
	                <a href="/admin/nl_editmailtemplate?action=edit&id=${template.id}"><img class="admImg admHand" src="/images/edit.png" width="37" height="36" alt="<fmt:message key="EDIT"/>"/></a>
	            </td>
	            <td class="admTableTDLast" align="center">
                	<img class="admImg admHand" src="/images/delete.png" width="37" height="36" alt="<fmt:message key="DELETE"/>"
                		onClick="javascript: deleteTemplate(${template.id})"
                	/>
	            </td>
			</tr>
		</c:forEach>
		<tr>
			<td class="admTableFooter" colspan="2">&nbsp;</td>
		</tr>
	</table>    
    </negeso:admin>
</body>
</html>	
