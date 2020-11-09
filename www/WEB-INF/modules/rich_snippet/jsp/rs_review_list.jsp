<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="negeso" %>
<%response.setHeader("Expires", "0");%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
   <head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title><fmt:message key="RS_REVIEW_RICH_SNIPPETS"/></title>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="/css/customized-jquery-ui.css"/>    
    
    <script type="text/javascript" src="/script/jquery.min.js">/**/</script>
    <script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
				<script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"></script>
    <script type="text/javascript" src="/site/core/script/validation.js"></script>    
</head>
<body>
    <script type="text/javascript">
    	function edit(id) {
        	$('#id_field').val(id);
        	$('#action_field').val('edit');
        	$('#sendForm').submit();
        }
        function addNew(id){
        	$('#action_field').val('add');
        	$('#sendForm').submit();
        }
        function deleteById(id, clickedObj){
        	if (!confirm('<fmt:message key="RS_REVIEW_CONFIRM_DELETION"/>')) {
                return;
            }
        	 $.ajax({
        	        type: "POST",
        	        url: "/admin/rs_${discriminator}_list.html",
        	        dataType: "html",
        	        data: {id: id, action: 'delete'},
        	        success: function (data, stat) {
       	        		var rowFordeletion = $(clickedObj).parents('tr:first');
       	        		$('td', rowFordeletion).animate( {backgroundColor:'red'}, 1000);
       	        		$(rowFordeletion).fadeOut(1000,function() {
       	        			$(rowFordeletion).remove();
       	        		});
        	        },
        	        error: function (html, stat) {
        	        	alert(html.response);
        	        }
        	    });
        }
    </script>        
       
<negeso:admin>
	<form method="get" id="sendForm" action="/admin/rs_edit_${discriminator}.html">
		<input name="id" type="hidden" id="id_field"/>
		<input name="action" type="hidden" id="action_field"/>
	</form>
	<table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
        <tr>
			<td style="width:auto; height:auto;" colspan="8"><jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp"/></td>
		</tr>
        <tr>                
			<td align="center" class="admNavPanelFont"  style="height:auto;" colspan="8"><fmt:message key="RS_REVIEW_RICH_SNIPPETS" /></td>
		</tr>
		<tr> 
			<td class="admTDtitles" width="25%" style="height:auto"><fmt:message key="RS_REVIEW_NAME"/></td>
			<td class="admTDtitles" width="25%" style="height:auto"><fmt:message key="RS_REVIEW_AUTHOR"/></td>
			<td class="admTDtitles" width="20%" style="height:auto"><fmt:message key="RS_REVIEW_DATE_PUBLISHED"/></td>
			<td class="admTDtitles" width="20%" style="height:auto"><fmt:message key="RS_REVIEW_RATING"/></td>
            <td class="admTDtitles" width="10%" colspan="2" style="height:auto"><fmt:message key="ACTION"/></td> 
        </tr>
		<c:forEach var="richSnippet" items="${richSnippets}">
			<tr>
				<td class="admTableTD">                  
                    <c:out value="${richSnippet.name}"></c:out>
                </td>
				<td class="admTableTD">                  
                    <c:out value="${richSnippet.author}"></c:out>
                </td>
				<td class="admTableTD">                  
                    <fmt:formatDate value="${richSnippet.datePublished}" pattern="dd-MM-yyyy"/>
                </td>
				<td class="admTableTD">
                    <c:out value="${richSnippet.rating}"></c:out>
                </td>
                <td class="admTableTDLast" id="admTableTDtext" align="right">
                     <img src='/images/edit.png'class='admHand' title='<fmt:message key="EDIT"/>'
                        onclick="edit(${richSnippet.id})"/>
                 </td>		       					
                 <td class="admTableTDLast" id="admTableTDtext" align="right">
					<img src='/images/delete.png' class='admHand' title='<fmt:message key="DELETE"/>'
						onclick="deleteById(${richSnippet.id}, this)"/>
				</td>
			</tr>
		</c:forEach> 
        <tr>
			<td class="admTableFooter" colspan="6" >&nbsp;</td>
        </tr>           
	</table>	
</negeso:admin>        
        
<table cellpadding="0" cellspacing="0"  width="764px"  align="center"  border="0" class="Buttons">
	<tr>
       <td>
			<div class="admBtnGreenb">
				<div class="imgL"></div>
                <div><input type="button" value='<fmt:message key="ADD"/>' onClick="addNew();"/></div>
                <div class="imgR"></div>
            </div>
		</td>
	</tr>
</table>

</body>
</html>