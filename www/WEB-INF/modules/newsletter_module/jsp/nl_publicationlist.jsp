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
<%@page import="java.util.Date"%>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
   	<title>
   					<fmt:message key="NL.SUBSCRIPTION_LIST"/>&#160;'<c:out value="${category.title}"/>'
   	</title>

				<script type="text/javascript" src="/script/jquery.min.js">/**/</script>				
				<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
    
	<script>
		var FinalRequiredError ="<fmt:message key="FINAL_REQUIRED_ERROR_MESSAGE"/>";
		var FinalEmailError="<fmt:message key="FINAL_EMAIL_ERROR_MESSAGE"/>";
		var FinalNumericError="<fmt:message key="FINAL_NUMERIC_ERROR_MESSAGE"/>";
	</script>
				<script type="text/javascript" src="/script/common_functions.js" type="text/javascript" />
				<script type="text/javascript" src="/site/core/script/validation.js"></script>
    <script>
		function try_delete(id){
			if(confirm('<fmt:message key="NL.DELETE_ITEM_CONFIRM"/>'))
				document.location='/admin/nl_publicationlist?action=deletePublication&id='+id;
		}
		
		function create_new_publication(){
		
			if (validate(document.forms['nl_add_publication_form']))
				document.location='/admin/nl_editpublication?title=' + document.forms['nl_add_publication_form'].elements['title'].value + 
				'&mail_template_id=' + document.forms['nl_add_publication_form'].elements['mail_template_id'].value + 
				'&id=0&category_id=${category.id}';
		}
		
		function proof(id){
			if(confirm(document.getElementById('proof_confirm').innerHTML)){
				document.location = "/admin/nl_publicationlist?action=proof&publicationId="+id;
				document.getElementById('proof_td'+id).className="admDarkTD";
				document.getElementById('proof_button'+id).href="#";
				document.getElementById('proof_image'+id).title="<fmt:message key="NL.PROOF_DISABLED"/>";
				document.getElementById('proof_image'+id).alt="<fmt:message key="NL.PROOF_DISABLED"/>";
			}
		}
	</script>
</head>
<body>
 
	<fmt:formatDate var="now" value="<%= new Date() %>" type="both" pattern="yyyy-MM-dd HH:mm:ss"/>
	<div id="proof_confirm" style="display:none"><fmt:message key="NL.PROOF_CONFIRM"/></div>
	<negeso:admin>
	
    <!-- Title table -->

	<table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
		<tr>
			<td style="width:auto; height:auto;"  colspan="6">  
				<jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp" />
			</td>  
		</tr>
		<tr>   
			<td align="center" class="admNavPanelFont"  style="height:auto;" colspan="8">
				<!--  fmt:message key="BM.CATEGORIES_LIST"/-->
				<fmt:message key="NL.SUBSCRIPTION_LIST"/>&#160;'<c:out value="${category.title}"/>'
			</td>            
		</tr>
   
		<form name="nl_add_publication_form" method="post" action="/admin/nl_publicationlist?action=addPublication">	    
		   	<tr>
		   		<th class="admTableTDLast">
		   			<fmt:message key="NL.PUBLICATION_TITLE"/>	
		   		</th>
		   		<td class="admTableTDLast" colspan="5">
		   			<input class="admTextArea" type="text" name="title" value=""/>
		   		</td>
            </tr>
            <tr>
		   		<th class="admTableTDLast">
		   			<fmt:message key="NL.MAIL_TEMPLATE"/>	
		   		</th>
		   		<td class="admTableTDLast" colspan="5">
		   			<select class="admTextArea" name="mail_template_id">
		   				<c:forEach var="mail_template" items="${mailtemplates}">
			   				<option value="${mail_template.id}"><c:out value="${mail_template.title}"/></option>
		   				</c:forEach>
		   			</select>
		   		</td>
			</tr>
            <tr>
		   		<th colspan="6" class="admTableTDLast" align="center">
					<div class="admNavPanelInp" style="padding:0;margin:0;">
						<div class="imgL"></div>
                        <div>
							<input type="button" class="admNavbarInp" value='<fmt:message key="NL.ADD_PUBLICATION"/>' 
		               			onClick="create_new_publication(); return false;">
                        </div>
                        <div class="imgR"></div>
                    </div>
		       </th>
		   	</tr>		
	</form>
    
    <!-- Content table -->
	<tr> 
        <td class="admTDtitles">
            <fmt:message key="TITLE"/>
        </td>
        <td class="admTDtitles" >
            <fmt:message key="NL.SENT_DATE"/>
        </td>
        <td class="admTDtitles" >
            <fmt:message key="NL.SCHEDULED_DATE"/>
        </td>
        <td class="admTDtitles">
            <fmt:message key="NL.STATUS"/>
        </td>
        <td class="admTDtitles">
            <fmt:message key="ACTION"/>
        </td>
    </tr> 
        
    <c:forEach var="publication" items="${category.publications}">
        <tr>
            <th class="admTableTD" width="200">
                <a class="admNone" style="font-weight: bold; color: black" 
                	href="/admin/nl_editpublication?id=<c:out value="${publication.id}"/>">
                 	<c:choose>
   						<c:when test="${fn:length(publication.title)>50}">
   							<c:out value="${fn:substring(publication.title,0,50)}"/>...
   						</c:when>
   						<c:otherwise>
	   						<c:out value="${publication.title}"/>
   						</c:otherwise>
  					</c:choose>
				</a><br/>
				<fmt:message key="NL.SUBSCRIBERS"/>: <c:out value="${publication.subscribersNumber}"/>
			</th>
	        <th class="admTableTD" width="100">
	            <fmt:formatDate var="a1" value="${publication.publishDate}" type="both" pattern="yyyy-MM-dd HH:mm:ss"/><br/><c:if 
				test="${a1 < now}"><c:out value="${a1}"/></c:if>
	        </th>
	        <th class="admTableTD" align="center" width="100">
	            <c:if test="${a1 > now}"><c:out value="${a1}"/></c:if>
	        </th>
            <th class="admTableTD">
                <c:choose>
	                <c:when test="${publication.publicationState.name == 'sent'}">
	                    <fmt:message key="NL.SENT"/>
	                </c:when>
	                <c:when test="${publication.publicationState.name == 'suspended'}">
	                    <fmt:message key="NL.SUSPENDED"/>
	                </c:when>
	                <c:when test="${publication.publicationState.name == 'scheduled'}">
	                    <fmt:message key="NL.SCHEDULED"/>
	                </c:when>
                    <c:otherwise>
                        <c:out value="${publication.publicationState.name}"/>
                    </c:otherwise>
                </c:choose>                
	        </th>                
            <td class="admTableTDLast">
				<table  border="0" cellpadding="0" cellspacing="0">
					<tr>                
						<td width="30px">                
							<a href="/admin/nl_statistics?action=showStatistics&id=${publication.id}&status=sent" >
	            				<img src="/images/view_statistics.png" class="admHand" border="0" alt="<fmt:message key="NL.STATISTICS"/>" title="<fmt:message key="NL.STATISTICS"/>"/>
							</a>
						</td>
						<td  id="proof_td${publication.id}" width="30px">
							<a id="proof_button${publication.id}" href="javascript:proof(${publication.id})">
								<img id="proof_image${publication.id}" class="admHand"  src="/images/nl_proof.png" width="37px" height="36px" alt="<fmt:message key="NL.PROOF"/>" title="<fmt:message key="NL.PROOF"/>"/>
							</a>
						</td>
						<td  width="30px">
							<a href="/admin/nl_editpublication?id=<c:out value="${publication.id}"/>"><img class="admHand" src="/images/edit.png" width="37px" height="36px" alt="<fmt:message key="EDIT"/>" title="<fmt:message key="EDIT"/>"/></a>
						</td>
						<td width="30px">
							<img class="admHand" src="/images/delete.png" width="37px" height="36px" alt="<fmt:message key="DELETE"/>" title="<fmt:message key="DELETE"/>" onClick="try_delete('${publication.id}')"/>
						</td>
					</tr>
				</table>
			</td>
		</tr>            
	</c:forEach>
    <tr>
		<td class="admTableFooter" colspan="2" >&nbsp;</td>
    </tr> 
</table>
</negeso:admin>
<table cellpadding="0" cellspacing="0"  width="764px"  align="center"  border="0" class="Buttons">
	<tr>
		<td>			
			<div class="admBtnGreenb admBtnBlueb">
				<div class="imgL"></div>
				<div><a href="/admin/nl_statistics?action=showCategoryStatistics&id=${category.id}&status=sent" class="admNavbarInp"><fmt:message key="NL.OVERVIEW"/></a></div>
				<div class="imgR"></div>
			</div>
		</td>
	</tr>
</table>
</body>
</html>