<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<%response.setHeader("Expires", "0");%>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>

	<script type="text/javascript" src="/script/jquery.min.js">/**/</script>
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
 <script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"></script>
	<script language="JavaScript1.2" src="/script/calendar_picker.js" type="text/javascript"></script>
 <script language="JavaScript1.2" src="/script/media_catalog.js" type="text/javascript"></script>
	<script language="JavaScript1.2" src="/site/core/script/validation.js" type="text/javascript"></script>
	<script type="text/javascript" language="JavaScript1.2">
		function changeRequiredTitle(form) {
			if (form.visible.checked) {
				form.title.required = "true";
			} else {
				form.title .required = "false";
			}
		}
		
	</script>
</head>
<body onLoad="changeRequiredTitle(window.parameter)">
	<br/>
	<jsp:include page="/WEB-INF/jsp/common/header.jsp"/>
	<jsp:include page="/WEB-INF/jsp/common/back_bar.jsp"/>
	<!--<jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp"/>-->	
	<table cellpadding="0" cellspacing="0" class="admNavPanel admSpaceSmall" border="0" align="center">
	<tr>
		<td style="background: #7FB577 url('../../images/titleLeft.gif') left top no-repeat; width: 10px; height: 25px">&#160;</td>
		<td class="admTitle">

		  <c:choose>
 				<c:when test="${parameter.id == null}">Add parameter</c:when>
				<c:otherwise>Update parameter ${parameter.name}</c:otherwise>
		  </c:choose >
		</td>
		<td style="background: #7FB577 url('../../images/titleRight.gif') right top no-repeat; width: 10px; height: 25px">&#160;</td>
	</tr>
	</table>
	<form method="POST" enctype="multipart/form-data" name="parameter" >
		<input type="hidden" name="act" id="act" value="saveParameter" />
		<input type="hidden" name="id" value="${parameter.id}" />
		<input type="hidden" name="moduleId" value="${parameter.moduleId}" />
 
	 	<table class="admNavPanel" cellspacing="0" cellpadding="0" align="center" border="0">
			<tr>
				<td width="20%" class="admMainTD" width="200">Name*</td>
				<td colspan="2" class="admLightTD">
					<input type="text" name="name" id="name" class="admTextArea"
					 style="width: 500px;" value="${parameter.name}" required="true" MAXLENGTH="255" />
					 
					 
				</td>
			</tr>
			<tr>
				<td  class="admMainTD" width="200">Value</td>
				<td  colspan="2" class="admLightTD">				
				<c:choose>
					<c:when test="${parameter.name=='statistics.firstDate'}"><!-- #for statistics.firstDate -->
						<input class="admTextArea admWidth150" type="text" name="value" id="value" data_type="date" uname="Expired date" title="Set date" readonly="true" value="${parameter.value}">
                                  <img class="admHand" src="/images/calendar.gif" width="16" height="16" alt="Pick a date" onClick="loadNewCalendar('/dialogs/dialog_calendar_picker.html','value','yyyymmdd',false)"/>
                        	</input>
                        	(yyyy-mm-dd)
					</c:when>
					<c:otherwise>
						<input type="text" name="value" id="value" class="admTextArea"
					 style="width: 500px;" value="${parameter.value}" MAXLENGTH="2000" />
					</c:otherwise>
				</c:choose>
				</td>
			</tr>
			<tr>
				<td  class="admMainTD" width="200">Module</td>
				<td  colspan="2" class="admLightTD">
				    <select name="newModuleId" class="admTextArea" style="width: 500px;">
				        <option value="unlinked">Unlinked parameter</option>	
				        <c:forEach var="module" items="${modules}">
   					        <option value="${module.id}" <c:if test="${module.id==parameter.moduleId}">selected</c:if> >
   					        	${module.title}
   					        </option>
				        </c:forEach>
				    </select>
				</td>
			</tr>
			<tr>
				<td  class="admMainTD" width="200">Title</td>
				<td  colspan="2" class="admLightTD">
					<input type="text" name="title" id="title" class="admTextArea"
					 style="width: 500px;" value="${parameter.title}" MAXLENGTH="2000" required="true" />
				</td>
			</tr>
  			<tr>
				<td  class="admMainTD" width="200">Description</td>
				<td  colspan="2" class="admLightTD">
				<textarea rows="3" class="admTextArea" name="description" id="description" style="width: 500px">${parameter.description}</textarea>			
				</td>
			</tr>
			<tr>
				<td  class="admMainTD" width="200">Field type</td>
				<td  colspan="2" class="admLightTD">
				<select name="fieldType" class="admTextArea" style="width: 500px;">
					<option value="input" <c:if test="${parameter.fieldType=='input'}">selected</c:if> >
						input
					</option>
					<option value="textarea" <c:if test="${parameter.fieldType=='textarea'}">selected</c:if> >
						textarea
					</option>
				</select>
				</td>
			</tr>
			<tr>
				<td rowspan="5" class="admLightTD" >
					Options
				</td>
			</tr>
			<tr>	
				<td width="20%" class="admMainTD">Visible</td>
		 		<td class="admLightTD" style="text-align: left;">
					<input type="checkbox"style="margin: 0px 0px 0px 3px;" name="visible" id="visible" onclick="changeRequiredTitle(this.form)" 
						<c:if test="${parameter.visible}">checked</c:if>
					 />
		 		</td>
			</tr>
			<tr>		
		 		<td class="admMainTD">Readonly</td>
		 		<td class="admLightTD" style="text-align: left;">
					<input type="checkbox"style="margin: 0px 0px 0px 3px;" name="readonly" id="readonly" 
						<c:if test="${parameter.readonly}">checked</c:if>
					 />
		 		</td>
			</tr>				
			<tr>		
		 		<td class="admMainTD">Required</td>
		 		<td class="admLightTD" style="text-align: left;">
					<input type="checkbox" style="margin: 0px 0px 0px 3px;" name="required" id="required" 
						<c:if test="${parameter.required}">checked</c:if>
					 />
		 		</td>
			</tr>				
			<tr>		
		 		<td class="admMainTD">Global</td>
		 		<td class="admLightTD" style="text-align: left;">
					<input type="checkbox"style="margin: 0px 0px 0px 3px;" name="global" id="global" 
						<c:if test="${parameter.global}">checked</c:if>
					 />
		 		</td>
			</tr>				
			<tr>
				<td colspan="3" class="admNavbar" align="center" >
					<input class="admNavbarInp" type="button" 
						  onclick="if (full_validate(this.form)) this.form.submit()" value="<&nbsp;Save and Close &nbsp;>"
			    	  />
				</td>
			</tr> 
		</table>
	</form>
	<jsp:include page="/WEB-INF/jsp/common/back_bar.jsp"/>
</body>
</html>