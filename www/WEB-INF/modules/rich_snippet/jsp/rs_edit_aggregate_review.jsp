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
			    <fmt:message key="RS_EDIT_AGGREGATE_REVIEW"/>
	    	</c:when>
	    	<c:otherwise>
	    		<fmt:message key="RS_ADD_AGGREGATE_REVIEW"/>
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
					    <fmt:message key="RS_EDIT_AGGREGATE_REVIEW"/>
			    	</c:when>
			    	<c:otherwise>
			    		<fmt:message key="RS_ADD_AGGREGATE_REVIEW"/>
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
                <fmt:message key="RS_REVIEW_DESCRIPTION"/> <img alt="" src="/images/info.png" title="<fmt:message key="RS_REVIEW_DESCRIPTION_INFO"/>" class="field_info_icon"/>
            </td>
			<td class="admTableTD">
				<textarea rows="3" name="description" class="admTextArea" >${richSnippet.description}</textarea>
            </td>
		</tr>
		<tr>
			<td class="admTableTD">                  
                <fmt:message key="RS_REVIEW_RATING"/> * <img alt="" src="/images/info.png" title="<fmt:message key="RS_REVIEW_RATING_INFO"/>" class="field_info_icon"/>
            </td>
			<td class="admTableTD">
				<input type="text" class="admTextArea" value='<fmt:formatNumber pattern="###.##" value="${richSnippet.ratingValue}"/>' name="ratingValue" required="true" numeric_field_params="10;2"/>
            </td>
		</tr>
		<tr>
			<td class="admTableTD">                  
                <fmt:message key="RS_REVIEW_RATING_MIN"/> <img alt="" src="/images/info.png" title="<fmt:message key="RS_REVIEW_RATING_MIN_INFO"/>" class="field_info_icon"/>
            </td>
			<td class="admTableTD">
				<input type="text" class="admTextArea" value="${richSnippet.worstRating}" name="worstRating" data_type="number"/>
            </td>
		</tr>
		<tr>
			<td class="admTableTD">                  
                <fmt:message key="RS_REVIEW_RATING_MAX"/> <img alt="" src="/images/info.png" title="<fmt:message key="RS_REVIEW_RATING_MAX_INFO"/>" class="field_info_icon"/>
            </td>
			<td class="admTableTD">
				<input type="text" class="admTextArea" value="${richSnippet.bestRating}" name="bestRating" data_type="number"/>
            </td>
		</tr>
		<tr>
			<td class="admTableTD">                  
                <fmt:message key="RS_AGGREGATE_RATING_COUNT"/> <img alt="" src="/images/info.png" title="<fmt:message key="RS_AGGREGATE_RATING_COUNT_INFO"/>" class="field_info_icon"/>
            </td>
			<td class="admTableTD">
				<input type="text" class="admTextArea" value="${richSnippet.ratingCount}" name="ratingCount" data_type="number"/>
            </td>
		</tr>
		<tr>
			<td class="admTableTD">                  
                <fmt:message key="RS_AGGREGATE_REVIEW_COUNT"/> <img alt="" src="/images/info.png" title="<fmt:message key="RS_AGGREGATE_REVIEW_COUNT_INFO"/>" class="field_info_icon"/>
            </td>
			<td class="admTableTD">
				<input type="text" class="admTextArea" value="${richSnippet.reviewCount}" name="reviewCount" data_type="number"/>
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