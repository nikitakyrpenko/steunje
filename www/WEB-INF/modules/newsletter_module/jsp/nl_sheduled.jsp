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
   	<title>
								<fmt:message key="NL.SCHEDULED_PUBLICATIONS"/>
   	</title>
				<script type="text/javascript" src="/script/jquery.min.js">/**/</script>				
				<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
				<script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
				<script type="text/javascript" src="/script/common_functions.js"></script>
</head>
<body>
	<script type="text/javascript">
	function try_delete(id){
		if(confirm('Are you sure to suspend this item?'))
			document.location='/admin/nl_scheduled?action=delete&id='+id;
	}
	</script>
        
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
     <td  style="width:auto; height:auto;" >  
           <jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp" />
           	</td></tr>  
			<tr>
				<td  align="center" class="admNavPanelFont"  style="height:auto;" colspan="5" >
				<fmt:message key="NL.SCHEDULED_PUBLICATIONS"/>
            </td>
            
        </tr>
   
    <!-- Content table -->
	
		<tr>
			<td class="admTDtitles">
				<fmt:message key="PUBLISH_DATE"/>
			</td>
			<td class="admTDtitles admWidth_150" >
				<fmt:message key="NL.PUBLICATATION_TITLE"/>
			</td>
			<td class="admTDtitles" >
				<fmt:message key="CATEGORY"/>
			</td>
			<td class="admTDtitles" >
				<fmt:message key="LANGUAGE"/>
			</td>
			<td class="admTDtitles" style="padding-left:3px;">
				<fmt:message key="ACTION"/>
			</td>
		</tr>
		<c:forEach var="publication" items="${publications}">
			<tr>
				<th class="admTableTD">
					<fmt:formatDate value="${publication.publishDate}" type="both" pattern="yyyy-MM-dd HH:mm" />
				</th>
				<th class="admTableTD">
					<c:out value="${publication.title}"></c:out>
				</th>
				<th class="admTableTD">
					<c:out value="${publication.subscriptionCategory.title}"></c:out>
				</th>
				<th class="admTableTD">
					<c:out value="${publication.langCode}"></c:out>
				</th>
				<td class="admTableTDLast" style="padding-left:20px;">
                	<img class="admImg admHand" src="/images/suspend_mail.png" width="37px" height="36px" 
                		alt="<fmt:message key="NL.SUSPEND"/>" 
                		onClick="window.location.href='/admin/nl_publicationlist?action=unSchedule&id=${publication.id}'"/>
				</td>
			</tr>
		</c:forEach>
         <tr>
                    <td class="admTableFooter" colspan="2" >&nbsp;</td>
                </tr>  
	</table>
    </negeso:admin>	
</body>
</html>	




</body>
</html>