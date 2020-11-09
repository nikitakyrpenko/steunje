<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="negeso" %>
<%response.setHeader("Expires", "0");%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
	<title>
				<fmt:message key="CONFIGURATION_PARAMETERS"/>
	</title>
	
	<script type="text/javascript" src="/script/jquery.min.js">/**/</script>
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
	<script type="text/javascript" src="/script/cufon-yui.js"></script>
 <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
	<script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"></script>
	<script language="JavaScript1.2" src="/site/core/script/validation.js" type="text/javascript"></script>
	
	<script type="text/javascript" language="JavaScript1.2">
		function replaseSpace() {
			if(document.forms[0]['media.catalog.emailTo']){
				var values = document.forms[0]['media.catalog.emailTo'].value.replace(/\s+/g, "");
				document.forms[0]['media.catalog.emailTo'].value = values.replace(/;$/, "");
			}
			if(document.forms[0]['media.catalog.usedFreeSpace']){
				var values = document.forms[0]['media.catalog.usedFreeSpace'].value.replace(/(\s+)|([A-Za-z])/g, "");
				document.forms[0]['media.catalog.usedFreeSpace'].value = values.replace(/;$/, "");
			}
		}		
	</script>    
    
</head>
<body>

<negeso:admin> 
	
	<table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">        
		<tr>                
			<td align="center" class="admNavPanelFont"  style="height:auto;" colspan="3">
				<c:choose>
					<c:when test="${module.id==31}"><fmt:message key="GOOGLE.TITLE"/></c:when>
					<c:otherwise>${module.title}</c:otherwise>
				</c:choose>.&#160;<fmt:message key="CONFIGURATION_PARAMETERS"/>			
            </td>
		</tr>

		<form method = "GET" enctype="multipart/form-data" name="configuration">
			<input type="hidden" name="moduleId" value="${module.id}"/>
			<input type="hidden" name="act" value="save"/> 	 		
			<c:forEach var="parameter" items="${module.configurationParameters}">
				<c:if test="${parameter.visible}">
					<tr>
						<td class="admTableTD" id="admTableTDtext">&#160;&#160;
						  <c:set var="mes"><fmt:message key="${parameter.title}"/></c:set>
						  <c:choose>
						      <c:when test="${fn:contains(mes, '!!!') && !empty(parameter.description)}">
						          ${parameter.description}
						      </c:when>
						      <c:otherwise>${mes}</c:otherwise>
						  </c:choose>
						</td>
						<td class="admTableTDLast" id="admTableTDtext">
							<c:choose>
								<c:when test="${parameter.readonly}">&#160; ${parameter.value}</c:when>
								<c:otherwise>
									<c:choose>											
										<c:when test="${parameter.fieldType=='textarea'}">
											<textarea type="text"  data_type="text" 
												class="admTextArea admWidth500" name="${parameter.name}"  rows="5" 												
												<c:if test="${parameter.required}">required="true" </c:if>
												<c:if test="${parameter.readonly}">readonly="readonly" </c:if>
												<c:if test="${parameter.name=='media.catalog.emailTo'}">is_email="true" </c:if>
											>${parameter.value}</textarea>
										</c:when>
										<c:when test="${parameter.value=='true'||parameter.value=='false'}">
											<input 
												type="radio" name="${parameter.name}" data_type="text"
												<c:if test="${parameter.value=='true'}">checked="checked" </c:if>
												<c:if test="${parameter.required}"> required="true" </c:if>
												value="true"/>&#160; yes<br/>
											<input 
												type="radio" name="${parameter.name}" data_type="text"
												<c:if test="${parameter.value=='false'}">checked="checked" </c:if>
												<c:if test="${parameter.required}">required="true" </c:if>
												value="false"/>&#160; no
										</c:when>
										<c:otherwise>
											<input class="admTextArea admWidth200" 
												type="text" 
												name="${parameter.name}" 
												data_type="text"
												<c:if test="${parameter.required}">required="true" </c:if>
												value="${parameter.value}"
												<c:if test="${parameter.readonly}"> readonly="readonly"	</c:if>
												<c:if test="${parameter.name=='media.catalog.usedFreeSpace'}"> is_number_multi="true" </c:if>
											/>																	
										</c:otherwise>
									</c:choose>													
							
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</c:if>
			</c:forEach>
		</form>
	<tr>
		<td class="admTableFooter" colspan="3" >&nbsp;</td>
	</tr>    
</table>
</negeso:admin>    

<table cellpadding="0" cellspacing="0"  width="764px"  align="center"  border="0" class="Buttons">
	<tr>
		<td>
			<div class="admBtnGreenb">
				<div class="imgL"></div>
                <div><input type="button" style="width:45px;" value='<fmt:message key="SAVE"/>' 
					   onclick="replaseSpace(); if (full_validate(configuration)) configuration.submit()"/></div>
                <div class="imgR"></div>
            </div>
            <div class="admBtnGreenb">
				<div class="imgL"></div>
                <div><input type="button"  style="width:135px;" value='<fmt:message key="SAVE_AND_CLOSE"/>' 
						<c:choose>											
							<c:when test="${module.id==8}">
								onclick="replaseSpace(); if (full_validate(configuration)) var params = $('form').serialize(); 					   
								$.post('/admin/visitor_parameters', params,function(){ window.close(); })"
							</c:when>
							<c:otherwise>
					   onclick="replaseSpace(); if (full_validate(configuration)) var params = $('form').serialize(); 
								$.post('/admin/visitor_parameters', params,function(){window.location.href='/admin/site_settings' })"
							</c:otherwise>
						</c:choose>					   
						/></div>
                <div class="imgR"></div>
            </div>
		</td>
	</tr>
</table>
	
</body>
</html>