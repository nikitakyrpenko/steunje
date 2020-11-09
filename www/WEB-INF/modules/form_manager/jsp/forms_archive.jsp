<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ page session="true"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="negeso" %>
<%response.setHeader("Expires", "0");%>

<html>
<head>
				<title><fmt:message key="FM.FORM_ARCHIVE" /></title>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
				<link href="/css/admin_style.css" rel="stylesheet" type="text/css" />
				<link href="/css/jquery-ui.custom.css" rel="stylesheet" type="text/css"/>				

				<script type="text/javascript" src="/script/jquery.min.js"></script>
				<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>				
				<script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"></script>    
</head>

<body>				
				<negeso:admin>
								<script type="text/javascript">	
												function showTab(tab){
																if(prev_tab != tab){
																				// switch CVs lists
																				document.getElementById(tab).style.display = 'block';
																				document.getElementById(prev_tab).style.display = 'none';

																				// icons for tab
																				document.getElementById('tab_' + tab).className = "tab_active";
																				document.getElementById('tab_' + prev_tab).className = "tab_inactive";
																				prev_tab = tab;
            
																				if(tab=='approved') {
																								document.getElementById('tab').value  = 'approved';     
																								$('.Buttons').show();																								
																				} else {
																								document.getElementById('tab').value  = 'not_approved'; 
																								$('.Buttons').hide();
																								
																								// replace "_" with " " in column's titles, otherwise they shows in one row and table doesn't fit into page
																								$('.formArchiefDataTitles').each(function(){																												
																												var str = $(this).text();																								
																												while(str.indexOf('_') != -1){
																																str = str.replace('_',' ');
																												}
																												$(this).text(str);
																								});
																								
																								Cufon.replace('.admNavPanelFont', { fontFamily: 'Orator Std' });
																								Cufon.replace('.admTDtitles', { fontFamily: 'Orator Std' });
																				}
																}
												}
												function checkUncheckAll(){
																var $=jQuery;               
																$("INPUT[@name=_fieldId][type='checkbox']").attr('checked', $('#globalChecker').is(':checked') ? true : false);
												}
								</script>
								

								<!-- Title table -->				
				
								<form method="post" name="formArchive">
												<input type="hidden" name="tab" id="tab" value="approved" />
												<input type="hidden" name="todo" value="saveConfig" />
												<input type="hidden" name="form_id" value="${formId }" />

												<table cellpadding="0" cellspacing="0" class="admTable" border="0" align="center">
																<tr>																
																				<td class="admNavPanelFont" align="center" style="height:auto;"><fmt:message key="FM.FORM_ARCHIVE" /></td>																
																</tr>
																<tr>
																				<td  style="background:#fff;">
																								<table cellpadding="0" cellspacing="0" border="0" style="margin:10px 0  0 0; width:100%">
																												<tr>
																																<td id="tab_approved" class="tab_active" align="center" onClick="javascript:showTab('approved')">
																																				<fmt:message key="FM.FIELDS" />
																																</td>
																																<td id="tab_not_approved" class="tab_inactive" align="center" onClick="javascript:showTab('not_approved')">
																																				<fmt:message key="FM.DATA" />
																																</td>
																																<td align="center">
																																				<div class="admNavPanelInp">
																																								<div class="imgL"></div>
																																								<div>
																																												<a class="admNavPanelInp" focus="blur()" href="/admin/forms_archive?todo=exportArchive&form_id=${formId}"><fmt:message key="CM_CSV_EXPORT"/></a>																																												
																																								</div>
																																								<div class="imgR"></div>
																																				</div>
																																</td>
																																<td>&#160;</td>
																												</tr>
																								</table>                   
																				</td>
																</tr>
																<tr>
																				<td>																								
																								<table cellpadding="0" cellspacing="0" border="0" align="center">
																												<tr>
																																<td valign="top"></td>
																												</tr>
																												<tr>
																																<td class="admNavPanel" style="padding:0;">																																				
																																				<div style="width:764px; overflow:hidden; ">
																																								<table id="approved" width="764" style="display: block;" cellspacing="0" cellpadding="0" align="center" border="0">
																																												<tr>
																																																<td class="admTDtitles" style="height:auto; width:382px;"><fmt:message key="FM.NAME" /></td>
																																																<td class="admTDtitles admCenter" style="padding-left:3px; width:382px;">
																																																				<input type="checkbox" id="globalChecker" onClick="checkUncheckAll()"/>
																																																</td>
																																												</tr>
																																												<c:forEach var="formField" items="${formFields}">
																																																<c:if test="${formField.visible}">
																																																				<c:set var="isChecked" value="checked" />
																																																</c:if>
																																																<c:if test="${!formField.visible}">
																																																				<c:set var="isChecked" value="r" />
																																																</c:if>
																																																<tr>
																																																				<th class="admTableTD"><span><c:out value="${formField.name}" /></span></th>
																																																				<td class="admTableTDLast admCenter">
																																																								<input type="checkbox" name="_fieldId" value="${formField.id}" ${isChecked}=""/>
																																																				</td>	
																																																</tr>
																																												</c:forEach>																																								
																																								</table>
																				
																																								<table id="not_approved" width="764" style="display: none;" cellspacing="0" cellpadding="0" align="center" border="0">
																																												<tr>
																																																<td class="admTDtitles" style="font-size:12px">date</td>
																																																<c:forEach var="formField" items="${formFields}">
																																																				<c:if test="${formField.visible}">
																																																								<td class="admTDtitles formArchiefDataTitles" style="font-size:12px">																																																								
																																																												<c:out value="${formField.name}"></c:out>
																																																								</td>
																																																				</c:if>
																																																</c:forEach>
																																												</tr>
																																												<c:forEach var="archive" items="${archiveList}" varStatus="i">																																												
																																																<tr>
																																																				<th class="admTableTD"><c:out value="${fn:substring(archive.sentDate, 0, 10)}"></c:out></th>
																																																				<c:forEach var="formField" items="${formFields}">
																																																								<c:if test="${formField.visible}">																																																				
																																																												<td class="admTableTDLast">
																																																																<c:forEach var="value" items="${archive.fields}">																																																																
																																																																				<c:if test="${value.formField == formField}">
																																																																								<c:out value="${value.value}"></c:out>
																																																																				</c:if>
																																																																</c:forEach>&#160;
																																																												</td>
																																																								</c:if>
																																																				</c:forEach>
																																																</tr>
																																												</c:forEach>
																																								</table>
																																				</div>
																																</td>
																												</tr>	
																								</table>
																				</td>
																</tr>
																<tr>
																				<td class="admTableFooter">&nbsp;</td>
																</tr>
												</table>

												<table width="764px" cellspacing="0" cellpadding="0" border="0" align="center" class="Buttons" style="background-color:#FFF;">
																<tr>
																				<td>
																								<div class="admBtnGreenb">
																												<div class="imgL"></div>
																												<div><input class="admNavbarInp" value="Save" type="submit"></div>
																												<div class="imgR"></div>
																								</div>
																				</td>
																</tr>
												</table>

												<script language="JavaScript">
																prev_tab = 'approved';
																tab = '${param.tab}';
																if(tab==null || tab=='') {
																				tab='approved';
																}
																document.getElementById(prev_tab).style.display = 'block';
																showTab(tab);
												</script>
								</form>
				</negeso:admin>
</body>
</html>