<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%response.setHeader("Expires", "0");%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
				<title><fmt:message key="ADD_UPDATE_CONST"/></title>
				<link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>

				<script type="text/javascript" src="/script/jquery.min.js">/**/</script>
				<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
				<script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"></script>
				<script language="JavaScript1.2" src="/site/core/script/validation.js" type="text/javascript"></script>
</head>
<body class="keyConst">
	<form method="POST" enctype="multipart/form-data" onSubmit="if (full_validate(this)) this.form.submit(); else return false;" >	
		<input type="hidden" name="act" id="act" value="saveConst" />
		<input type="hidden" name="key" id="key" value="${const.key}"/>
		<c:if test="${const.id != null}">
			<input type="hidden" name="id" value="${const.id}" />		
		</c:if>
		<c:if test="${const.moduleId != null}">
			<input type="hidden" name="moduleId" value="${const.moduleId}" />
		</c:if>		
	 	<table class="admNavPanelKey" cellspacing="0" cellpadding="0" align="center" border="0">
			<c:forEach var="langs" items="${langs}">				
		 		<c:forEach var="translation" items="${const.translations}">
		 			<c:if test="${translation.language.code==langs.code}">
			  			<tr>
							<th width="65">
								<b>${translation.language.name}
									<c:if test="${translation.language.code == 'en'}">*</c:if>
								</b>
							</th>
							<td>
								<textarea rows="1" 
									class="admTextArea"
								    name="${translation.language.code}" 
								    id="${translation.language.code}"
								    style="width: 200px"
								    <c:if test="${translation.language.code == 'en'}">required="true"</c:if>
								    >${translation.translation}</textarea>			
							</td>
						</tr>
					</c:if>
				</c:forEach>				
			</c:forEach>
			<!-- tr>
				<td colspan="3" class="admNavbar" align="center" >
					<input class="admNavbarInp" type="button" 
						  onclick="if (full_validate(this.form)) this.form.submit()" value="<&nbsp;<fmt:message key="SAVE_AND_CLOSE"/>&nbsp;>"
			    	  />
				</td>
			</tr--> 
		</table>
	</form>
</body>
</html>