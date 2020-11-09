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
			    <fmt:message key="RS_EDIT_PRODUCT"/>
	    	</c:when>
	    	<c:otherwise>
	    		<fmt:message key="RS_ADD_PRODUCT"/>
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
					    <fmt:message key="RS_EDIT_PRODUCT"/>
			    	</c:when>
			    	<c:otherwise>
			    		<fmt:message key="RS_ADD_PRODUCT"/>
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
                <fmt:message key="RS_PRODUCT_BRAND"/> <img alt="" src="/images/info.png" title="<fmt:message key="RS_PRODUCT_BRAND_INFO"/>" class="field_info_icon"/>
            </td>
			<td class="admTableTD">
				<input type="text" class="admTextArea" value="${richSnippet.brand}" name="brand"/>
            </td>
		</tr>
		<tr>
			<td class="admTableTD">                  
                <fmt:message key="RS_PRODUCT_MANUFACTURER"/> <img alt="" src="/images/info.png" title='<fmt:message key="RS_PRODUCT_MANUFACTURER_INFO"/>' class="field_info_icon"/>
            </td>
			<td class="admTableTD">
				<input type="text" class="admTextArea" value="${richSnippet.manufacturer}" name="manufacturer"/>
            </td>
		</tr>
		<tr>
			<td class="admTableTD">                  
                <fmt:message key="RS_PRODUCT_MODEL"/> <img alt="" src="/images/info.png" title='<fmt:message key="RS_PRODUCT_MODEL_INFO"/>' class="field_info_icon"/>
            </td>
			<td class="admTableTD">
				<input type="text" class="admTextArea" value="${richSnippet.model}" name="model"/>
            </td>
		</tr>
		<tr>
			<td class="admTableTD">                  
                <fmt:message key="RS_PRODUCT_AVERAGE_RATING"/> * <img alt="" src="/images/info.png" title="<fmt:message key="RS_PRODUCT_AVERAGE_RATING_INFO"/>" class="field_info_icon"/>
            </td>
			<td class="admTableTD">
				<input type="text" class="admTextArea" value="${richSnippet.averageRating}" name="averageRating" data_type="number" required="true"/>
            </td>
		</tr>
		<tr>
			<td class="admTableTD">                  
                <fmt:message key="RS_PRODUCT_REVIEWS"/> * <img alt="" src="/images/info.png" title="<fmt:message key="RS_PRODUCT_REVIEWS_INFO"/>" class="field_info_icon"/>
            </td>
			<td class="admTableTD">
				<input type="text" class="admTextArea" value="${richSnippet.reviews}" name="reviews" data_type="number" required="true"/>
            </td>
		</tr>
		<tr>
			<td class="admTableTD">                  
                <fmt:message key="RS_PRODUCT_ID"/> <img alt="" src="/images/info.png" title='<fmt:message key="RS_PRODUCT_ID_INFO"/>' class="field_info_icon"/>
            </td>
			<td class="admTableTD">
				<input type="text" class="admTextArea" value="${richSnippet.productId}" name="productId"/>
            </td>
		</tr>
		<tr>
			<td class="admTableTD">                  
                <fmt:message key="RS_PRODUCT_PRICE"/> <img alt="" src="/images/info.png" title='<fmt:message key="RS_PRODUCT_PRICE_INFO"/>' class="field_info_icon"/>
            </td>
			<td class="admTableTD">
				<input type="text" class="admTextArea" value="${richSnippet.price}" name="price"/>
            </td>
		</tr>
		<tr>
			<td class="admTableTD">                  
                <fmt:message key="RS_PRODUCT_CONDITION"/> <img alt="" src="/images/info.png" title='<fmt:message key="RS_PRODUCT_CONDITION_INFO"/>' class="field_info_icon"/>
            </td>
			<td class="admTableTD">
				<select name="condition" class="admTextArea">
					<option value="New" <c:if test="${richSnippet.condition=='New'}">selected="true"</c:if> >New</option>
					<option value="Used" <c:if test="${richSnippet.condition=='Used'}">selected="true"</c:if> >Used</option>
					<option value="Refurbished" <c:if test="${richSnippet.condition=='Refurbished'}">selected="true"</c:if> >Refurbished</option>
					<option value="Damaged" <c:if test="${richSnippet.condition=='Damaged'}">selected="true"</c:if> >Damaged</option>
				</select>
            </td>
		</tr>
		<tr>
			<td class="admTableTD">                  
                <fmt:message key="RS_PRODUCT_AVAILABILITY"/> <img alt="" src="/images/info.png" title='<fmt:message key="RS_PRODUCT_AVAILABILITY_INFO"/>' class="field_info_icon"/>
            </td>
			<td class="admTableTD">
				<select name="availability" class="admTextArea">
					<option value="InStock" <c:if test="${richSnippet.availability=='InStock'}">selected="true"</c:if> >In Stock</option>
					<option value="Discontinued" <c:if test="${richSnippet.availability=='Discontinued'}">selected="true"</c:if> >Discontinued</option>
					<option value="InStoreOnly" <c:if test="${richSnippet.availability=='InStoreOnly'}">selected="true"</c:if> >In Store Only</option>
					<option value="LimitedAvailability" <c:if test="${richSnippet.availability=='LimitedAvailability'}">selected="true"</c:if> >Limited Availability</option>
					<option value="OnlineOnly" <c:if test="${richSnippet.availability=='OnlineOnly'}">selected="true"</c:if> >Online Only</option>
					<option value="OutOfStock" <c:if test="${richSnippet.availability=='OutOfStock'}">selected="true"</c:if> >Out Of Stock</option>
					<option value="PreOrder" <c:if test="${richSnippet.availability=='PreOrder'}">selected="true"</c:if> >Pre Order</option>
					<option value="SoldOut" <c:if test="${richSnippet.availability=='SoldOut'}">selected="true"</c:if> >Sold Out</option>
				</select>
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