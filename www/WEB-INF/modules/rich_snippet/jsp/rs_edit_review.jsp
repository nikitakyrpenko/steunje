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
    <title>
	    <c:choose>
	    	<c:when test="${richSnippet.id > 0}">
			    <fmt:message key="RS_EDIT_REVIEW"/>
	    	</c:when>
	    	<c:otherwise>
	    		<fmt:message key="RS_ADD_REVIEW"/>
	    	</c:otherwise>
	    </c:choose>
	</title>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="/css/jquery-ui.custom.css"/>    
    
    <script type="text/javascript" src="/script/jquery.min.js">/**/</script>
    <script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
	<script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"></script>
    <script type="text/javascript" src="/site/core/script/validation.js"></script>    
</head>
<body>
    <script type="text/javascript">
    	function save(isClose) {
        	if(validateForm(document.getElementById('sendForm'))) {
	        	$('#close_field').val(isClose);
	    		$('#sendForm').submit();
            }
        }
    </script>        
       
<negeso:admin>
	<form method="post" id="sendForm" >
	<input name="id" type="hidden" value="${richSnippet.id}"/>
	<input name="todo" type="hidden" value="save"/>
	<input name="close" type="hidden" value="false" id="close_field"/>
	<table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
        <tr>
			<td style="width:auto; height:auto;" colspan="2"><jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp"/></td>
		</tr>
        <tr>                
			<td align="center" class="admNavPanelFont"  style="height:auto;" colspan="2">
				<c:choose>
			    	<c:when test="${richSnippet.id > 0}">
					    <fmt:message key="RS_EDIT_REVIEW"/>
			    	</c:when>
			    	<c:otherwise>
			    		<fmt:message key="RS_ADD_REVIEW"/>
			    	</c:otherwise>
			    </c:choose>
			</td>
		</tr>
		<tr>
			<td class="admTableTD" colspan="2" style="text-align: center;">                  
                <span style="color: red">${errorMessage}</span>
            </td>
        </tr>		
		<tr>
			<td class="admTableTD">                  
                <fmt:message key="RS_REVIEW_NAME"/> * <img alt="" src="/images/info.png" title="<fmt:message key="RS_REVIEW_NAME_INFO"/>" class="field_info_icon"/>
            </td>
			<td class="admTableTD">
				<input type="text" class="admTextArea" value="${richSnippet.name}" name="name" required="true"/>
            </td>
		</tr>
		<tr>
			<td class="admTableTD">                  
                <fmt:message key="RS_REVIEW_DESCRIPTION"/> * <img alt="" src="/images/info.png" title="<fmt:message key="RS_REVIEW_DESCRIPTION_INFO"/>" class="field_info_icon"/>
            </td>
			<td class="admTableTD">
				<textarea rows="3" name="description" class="admTextArea" required="true">${richSnippet.description}</textarea>
            </td>
		</tr>
		<tr>
			<td class="admTableTD">                  
                <fmt:message key="RS_REVIEW_URL"/> <img alt="" src="/images/info.png" title="<fmt:message key="RS_REVIEW_URL_INFO"/>" class="field_info_icon"/>
            </td>
			<td class="admTableTD">
				<input type="text" class="admTextArea" value="${richSnippet.url}" name="url"/>
            </td>
		</tr>
		<tr>
			<td class="admTableTD">                  
                <fmt:message key="RS_REVIEW_AUTHOR"/> * <img alt="" src="/images/info.png" title="<fmt:message key="RS_REVIEW_AUTHOR_INFO"/>" class="field_info_icon"/>
            </td>
			<td class="admTableTD">
				<input type="text" class="admTextArea" value="${richSnippet.author}" name="author" required="true"/>
            </td>
		</tr>
		<tr>
			<td class="admTableTD">                  
                <fmt:message key="RS_REVIEW_DATE_PUBLISHED"/> * <img alt="" src="/images/info.png" title="<fmt:message key="RS_REVIEW_DATE_PUBLISHED_INFO"/>" class="field_info_icon"/>
            </td>
			<td class="admTableTD">
				<input type="text" class="admTextArea" value="<fmt:formatDate value="${richSnippet.datePublished}" pattern="dd-MM-yyyy"/>" name="datePublished" readonly="readonly" id="publishDateId" required="true"/>
            </td>
		</tr>
		<tr>
			<td class="admTableTD">                  
                <fmt:message key="RS_REVIEW_ITEMS_REVIEWED"/> <img alt="" src="/images/info.png" title="<fmt:message key="RS_REVIEW_ITEMS_REVIEWED_INFO"/>" class="field_info_icon"/>
            </td>
			<td class="admTableTD">
				<input type="text" class="admTextArea" value="${richSnippet.itemsReviewed}" name="itemsReviewed"/>
            </td>
		</tr>
		<tr>
			<td class="admTableTD">                  
                <fmt:message key="RS_REVIEW_BODY"/> * <img alt="" src="/images/info.png" title="<fmt:message key="RS_REVIEW_BODY_INFO"/>" class="field_info_icon"/>
            </td>
			<td class="admTableTD">
				<textarea rows="3" name="reviewBody" class="admTextArea" required="true">${richSnippet.reviewBody}</textarea>
            </td>
		</tr>
		<tr>
			<td class="admTableTD">                  
                <fmt:message key="RS_REVIEW_RATING"/> * <img alt="" src="/images/info.png" title="<fmt:message key="RS_REVIEW_RATING_INFO"/>" class="field_info_icon"/>
            </td>
			<td class="admTableTD">
				<input type="text" class="admTextArea" value="${richSnippet.rating}" name="rating" data_type="number" required="true"/>
            </td>
		</tr>
		<tr>
			<td class="admTableTD">                  
                <fmt:message key="RS_REVIEW_RATING_MIN"/> <img alt="" src="/images/info.png" title="<fmt:message key="RS_REVIEW_RATING_MIN_INFO"/>" class="field_info_icon"/>
            </td>
			<td class="admTableTD">
				<input type="text" class="admTextArea" value="${richSnippet.ratingScaleMin}" name="ratingScaleMin" data_type="number"/>
            </td>
		</tr>
		<tr>
			<td class="admTableTD">                  
                <fmt:message key="RS_REVIEW_RATING_MAX"/> <img alt="" src="/images/info.png" title="<fmt:message key="RS_REVIEW_RATING_MAX_INFO"/>" class="field_info_icon"/>
            </td>
			<td class="admTableTD">
				<input type="text" class="admTextArea" value="${richSnippet.ratingScaleMax}" name="ratingScaleMax" data_type="number"/>
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
                <div><input type="button" value='<fmt:message key="SAVE"/>' onClick="save(false)"/></div>
                <div class="imgR"></div>
            </div>
            <div class="admBtnGreenb">
				<div class="imgL"></div>
                <div><input type="button" value='<fmt:message key="SAVE_AND_CLOSE"/>' onClick="save(true)"/></div>
                <div class="imgR"></div>
            </div>
		</td>
	</tr>
</table>

</body>
</html>