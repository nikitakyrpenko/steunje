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
	    	<c:when test="${merchant.id > 0}">
			    <fmt:message key="GM_EDIT_MERCHANT"/>
	    	</c:when>
	    	<c:otherwise>
	    		<fmt:message key="GM_ADD_MERCHANT"/>
	    	</c:otherwise>
	    </c:choose>
	</title>
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
    	function save(isClose) {
        	if(validateForm(document.getElementById('sendForm'))) {
	        	$('#close_field').val(isClose);
	    		$('#sendForm').submit();
            }
        }
    </script>        
       
<negeso:admin>
	<form method="post" id="sendForm" >
	<input name="id" type="hidden" value="${merchant.id}"/>
	<input name="todo" type="hidden" value="save"/>
	<input name="close" type="hidden" value="false" id="close_field"/>
	<table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
        <tr>
			<td style="width:auto; height:auto;" colspan="3"><jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp"/></td>
		</tr>
        <tr>                
			<td align="center" class="admNavPanelFont"  style="height:auto;" colspan="3">
				<c:choose>
			    	<c:when test="${merchant.id > 0}">
					    <fmt:message key="GM_EDIT_MERCHANT"/>
			    	</c:when>
			    	<c:otherwise>
			    		<fmt:message key="GM_ADD_MERCHANT"/>
			    	</c:otherwise>
			    </c:choose>
			</td>
		</tr>
		<tr>
			<td class="admTableTD" colspan="3" style="text-align: center;">                  
                <span style="color: red">${errorMessage}</span>
            </td>
        </tr>		
		<tr>
			<td class="admTableTD">                  
                <fmt:message key="GM_HOST_NAME"/>
            </td>
			<td class="admTableTD" colspan="2">
				<c:choose>
			    	<c:when test="${merchant.id > 0}">
					    ${merchant.hostName}
			    	</c:when>
			    	<c:otherwise>
						<select name="hostName" class="admInpText">
							<c:forEach items="${hostNames}" var="hostName">
								<option value="${hostName.url}" 
									<c:if test="${hostName.url==merchant.hostName}">
				                     	selected="selected"
				                     </c:if>
								>${hostName.url}</option>
							</c:forEach>
						</select>
			    	</c:otherwise>
			    </c:choose>
            </td>
		</tr>
		<tr>
			<td class="admTableTD">                  
                <fmt:message key="SS.LANGUAGE"/>
            </td>
			<td class="admTableTD" colspan="2">
				<c:choose>
			    	<c:when test="${merchant.id > 0}">
					    ${merchant.langCode}
			    	</c:when>
			    	<c:otherwise>
						<select name="langCode" class="admInpText">
							<c:forEach items="${languages}" var="language">
								<option value="${language.code}" 
									<c:if test="${language.code==merchant.langCode}">
				                     	selected="selected"
				                     </c:if>
								>${language.code} - ${language.language} </option>
							</c:forEach>
						</select>
			    	</c:otherwise>
			    </c:choose>
            </td>
		</tr>
		<tr>
			<td class="admTableTD">                  
                <fmt:message key="COUNTRY_CODE"/>
            </td>
			<td class="admTableTD" colspan="2">
				<c:choose>
			    	<c:when test="${merchant.id > 0}">
					    ${merchant.countryCode}
			    	</c:when>
			    	<c:otherwise>
						<select name="countryCode" class="admInpText">
							<c:forEach items="${countries}" var="country">
								<option value="${country.code}" 
									<c:if test="${country.code==merchant.countryCode}">
				                     	selected="selected"
				                     </c:if>
								>${country.code} - ${country.name} </option>
							</c:forEach>
						</select>
			    	</c:otherwise>
			    </c:choose>
            </td>
		</tr>
		<tr>
			<td class="admTableTD">                  
                <fmt:message key="GM_ENABLED"/>
            </td>
			<td class="admTableTD" colspan="2">
				<input type="checkbox" value="true" name="enabled"
                     <c:if test="${merchant.enabled}">
                     	checked="true"
                     </c:if>
                    />
            </td>
		</tr>
		<tr>
			<td class="admTableTD">                  
                <fmt:message key="GM_CURRENCY_CODE"/>
            </td>
			<td class="admTableTD" colspan="2">
				<input type="text" class="admTextArea" value="${merchant.currencyCode}" name="currencyCode" required="true"/>
            </td>
		</tr>
		<tr>
			<td class="admTableTD">                  
                <fmt:message key="GM_MERCHANT_ID"/>
            </td>
			<td class="admTableTD" colspan="2">
				<input type="text" class="admTextArea" value="${merchant.merchantId}" name="merchantId"/>
            </td>
		</tr>
		<tr>
			<td class="admTableTD">                  
                <fmt:message key="GM_ADWORDS_ID"/>
            </td>
			<td class="admTableTD" colspan="2">
				<input type="text" class="admTextArea" value="${merchant.adwordsId}" name="adwordsId"/>
            </td>
		</tr>
		<tr>
			<td class="admTableTD">                  
                <fmt:message key="GM_DEFAULT_BRAND"/>
            </td>
			<td class="admTableTD" colspan="2">
				<input type="text" class="admTextArea" value="${merchant.defaultBrand}" name="defaultBrand" required="true"/>
            </td>
		</tr>
		<tr>
			<td class="admTableTD">                  
                <fmt:message key="GM_DEFAULT_CONDITION"/>
            </td>
			<td class="admTableTD" colspan="2">
				<select name="defaultCondition" class="admInpText">
					<c:forEach items="${defaultConditions}" var="condition">
						<option value="${condition}" 
							<c:if test="${condition==merchant.defaultCondition}">
		                     	selected="selected"
		                     </c:if>
						>${condition}</option>
					</c:forEach>
				</select>
            </td>
		</tr>
		<tr>
			<td class="admTableTD">                  
                <fmt:message key="GM_DEFAULT_AVAILABILITY"/>
            </td>
			<td class="admTableTD" colspan="2">
				<select name="defaultAvailability" class="admInpText">
					<c:forEach items="${defaultAvailabilities}" var="availabilitiy">
						<option value="${availabilitiy}" 
							<c:if test="${availabilitiy==merchant.defaultAvailability}">
		                     	selected="selected"
		                     </c:if>
						>${availabilitiy}</option>
					</c:forEach>
				</select>
            </td>
		</tr>
		<tr>
			<td class="admTableTD">                  
                <fmt:message key="GM_DEFAULT_GOOGLE_PRODUCT_CATEGORY"/>
            </td>
			<td class="admTableTD">
				<input type="text" class="admTextArea" value="${merchant.defaultGoogleProductCategory}" name="defaultGoogleProductCategory" required="true"/>
            </td>
            <td class="admTableTD" rowspan="2">
               <a href="http://www.google.com/basepages/producttype/taxonomy.nl-NL.xls" target="_blank">taxonomy.nl-NL.xls</a><br/>
               <a href="http://www.google.com/basepages/producttype/taxonomy.nl-NL.txt" target="_blank">taxonomy.nl-NL.txt</a><br/>
               <a href="http://www.google.com/basepages/producttype/taxonomy.en-US.xls" target="_blank">taxonomy.en-US.xls</a><br/>
               <a href="http://www.google.com/basepages/producttype/taxonomy.en-US.txt" target="_blank">taxonomy.en-US.txt</a>
            </td>
		</tr>
		<tr>
			<td class="admTableTD">                  
                <fmt:message key="GM_DEFAULT_GOOGLE_PRODUCT_TYPE"/>
            </td>
			<td class="admTableTD">
				<input type="text" class="admTextArea" value="${merchant.defaultGoogleProductType}" name="defaultGoogleProductType" required="true"/>
            </td>
		</tr>
		<tr>
			<td class="admTableTD">                  
                <fmt:message key="GM_NOTES"/>
            </td>
			<td class="admTableTD" colspan="2">
				<textarea rows="3" name="notes" class="admTextArea">${merchant.notes}</textarea>
            </td>
		</tr>
        <tr>
			<td class="admTableFooter" colspan="3" >&nbsp;</td>
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