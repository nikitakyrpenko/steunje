<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%response.setHeader("Expires", "0");%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
				<link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
				<title>Configuration parameters</title>

				<script type="text/javascript" src="/script/jquery.min.js">/**/</script>
				<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
				<script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"></script>
				<script>
								function try_to_delete(id, moduleId) {
												var answ = confirm('Are you sure you want to delete this parameter?');
												if (answ==true)
																window.location.href="?act=deleteParameter&id=" + id + "&moduleId=" + moduleId;
								}
				</script>
</head>
<body>
	<br/>
	<jsp:include page="/WEB-INF/jsp/common/header.jsp"/>
	<jsp:include page="/WEB-INF/jsp/common/back_bar.jsp"/>	
	<!--<jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp"/>	-->
	<table cellpadding="0" cellspacing="0" class="admNavPanel admSpaceSmall" border="0" align="center">
		<tr>
			<td style="background: #7FB577 url('../../images/titleLeft.gif') left top no-repeat; width: 10px; height: 25px">&#160;</td>
			<td class="admTitle">
			  <c:choose>
  				<c:when test="${module.name == 'unlinked'}">Unlinked parameters</c:when>
				<c:otherwise>${module.title}'s parameters</c:otherwise>
			  </c:choose >
			</td>
			<td style="background: #7FB577 url('../../images/titleRight.gif') right top no-repeat; width: 10px; height: 25px">&#160;</td>
		</tr>
	</table>

		<table class="admNavPanel" cellspacing="0" cellpadding="0"  align="center" border="0">
			<tr> 
				<td class="admNamebar" width="35%">Name</td> 
				<td class="admNamebar" width="24%">Value</td> 
				<td class="admNamebar" width="7%">Visible</td>
				<td class="admNamebar" width="8%">Readonly</td>
				<td class="admNamebar" width="8%">Required</td>				
				<td class="admNamebar" width="7%">Global</td>				
				<td class="admNamebar" colspan="2">Actions</td> 
			</tr>
		 	<c:forEach var="parameter" items="${module.configurationParameters}">
				<tr>
					<td class="admMainTD">
						<b>
						   <a class="admAnchor" href="/admin/parameters?act=updateParameter&id=${parameter.id}">
							 ${parameter.name}
						  </a>
						</b><br/>
							${parameter.description}					
					</td> 
					<td class="admDarkTD">${parameter.value}</td> 
					<!-- <td class="admLightTD">${parameter.title}</td> -->									
					<td class="admLightTD">
					   <c:choose>
						   <c:when test="${parameter.visible}">yes</c:when>
						   <c:otherwise>no</c:otherwise>
					   </c:choose>
					</td>
					<td class="admDarkTD">
					   <c:choose>
						   <c:when test="${parameter.readonly}">yes</c:when>
						   <c:otherwise>no</c:otherwise>
					   </c:choose>
					</td>
					<td class="admLightTD">
					   <c:choose>
						   <c:when test="${parameter.required}">yes</c:when>
						   <c:otherwise>no</c:otherwise>
					   </c:choose>
					</td>				
					<td class="admDarkTD">
					   <c:choose>
						   <c:when test="${parameter.global}">yes</c:when>
						   <c:otherwise>no</c:otherwise>
					   </c:choose>
					</td>
					<td class="admLightTD" colspan="2">
					   <a href="/admin/parameters?act=updateParameter&id=${parameter.id}">
					  		 <img class="admImg" style="cursor: pointer" src="/images/edit.gif" width="31px" height="27px" alt="Edit parameter"/>
					  </a>
					   <a 	href="<c:choose>
									<c:when test="${module.name == 'unlinked'}">
										javascript:try_to_delete(${parameter.id}, '')									
									</c:when>
									<c:otherwise>
										javascript:try_to_delete(${parameter.id}, ${module.id})																		
									</c:otherwise>
									</c:choose>"
					   >
					      <img class="admImg" style="cursor: pointer" src="/images/delete.gif" width="31px" height="27px" alt="Delete parameter"/>
					   </a>
					</td>
				</tr>	
			</tr>
			</c:forEach>
 			<tr>
				<td colspan="10" class="admNavbar" align="center">
					<input type="button" class="admNavbarInp" value='&lt;&#160;Add parameter&#160;&gt;' 
						onclick="window.location.href='?act=addParameter&moduleId=${module.id}'"/> 
				</td>
			</tr>
		</table>
	<jsp:include page="/WEB-INF/jsp/common/back_bar.jsp"/>
</body>
</html>