<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="negeso" %>
<%@ page session="true"%>
<%
response.setHeader("Expires", "0");
%>
<%@page import="java.util.Date"%>
<html>
<head>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
				<link href="/css/admin_style.css" rel="stylesheet" type="text/css" />
				<title><fmt:message key="NL.SETTINGS" />&#160;'<c:out value="${category.title}" />'</title>
								
				<script type="text/javascript" src="/script/jquery.min.js">/**/</script>
				<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
				<script type="text/javascript" src="/script/cufon-yui.js"></script>
				<script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>

				<script type="text/javascript" src="/script/common_functions.js" type="text/javascript" />
				<script type="text/javascript" src="/site/core/script/validation.js"></script>
				<script type="text/javascript" src="/script/RTE_Adapter.js">/**/</script>
				<script type="text/javascript" src="/script/calendar_picker.js">/**/</script>
				<script language="JavaScript" src="/script/rte_implement.js"></script>
				<script type="text/javascript" src="/script/media_catalog.js" type="text/javascript"></script>

   	<script type="text/javascript">
		prev_tab = 'attributes';
		tab = 'attributes';
		showTab(tab);

		function save(){
			document.getElementById('rte_text').value = document.getElementById('article_text').innerHTML;
			document.forms['from1'].submit();	
		}

		function showTab(tab){
				if(prev_tab != tab){
					// switch CVs lists
					document.getElementById(tab).style.display = 'block';
					document.getElementById(prev_tab).style.display = 'none';

					// icons for tab
					document.getElementById('tab_' + tab).className = "tab_active";
					document.getElementById('tab_' + prev_tab).className = "tab_inactive";
					prev_tab = tab;
				}
			}
		
	</script>
</head>

<body>

<form name="from1" action="/admin/nl_settings?action=saveParameters"
	method="post">
	<input type="hidden" id="rte_text" name="rte_text" />

	<negeso:admin>
		<table align="center" border="0" cellpadding="0" cellspacing="0" class="admTable">
			<tr>
				<td style="width:auto; height:auto;"> 
					<jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp" />
				</td>
			</tr>
			<!-- Title table -->
			<tr>	
				<td align="center" class="admNavPanelFont"  style="height:auto;">
					<fmt:message key="NL.SUBSCRIBER_SETTINGS"/>
				</td>            
			</tr>
			<!--CONTENT -->			
			<tr>
				<td style="background:#fff;">			
				<table cellpadding="0" cellspacing="0" border="0" width="764" style="margin:10px 0  0 0">
					<tr>
						<td style="width:129px" id="tab_attributes" class="tab_active" align="center" onClick="javascript:showTab('attributes')">Attributes</td>
						<td style="width:129px" id="tab_confirmation" class="tab_inactive" align="center" onClick="javascript:showTab('confirmation')">Confirmation</td>
						<td style="width:506px;border-bottom:1px solid #dfdfdf"></td>
					</tr>
				</table>
				</td>
			</tr>
			<tr>
				<td>
					<table id="attributes" style="width:764px; display:block; margin-top:5px;" cellspacing="0" cellpadding="0" align="center" border="0">
						<tr>
							<td class="admTDtitles" style="height:auto" width="350px"><fmt:message key="NL.ATTRIBUTE_NAME"/></td>
							<td class="admTDtitles" style="height:auto"><fmt:message key="NL.VISIBLE_ON_SUBSCRIPTION_PAGE"/></td>
						</tr>
						<c:forEach var="type" items="${types}">
							<tr>
								<th class="admTableTD"><fmt:message key="NL.${type.key}" /></td>
								<th class="admTableTDLast admCenter">
									<input type="checkbox" value="${type.id}" name="attr${type.id}" onfocus="blur()"
										<c:if test="${type.visible == true}"><c:out value="checked='checked'"/></c:if>>
								</th>
							</tr>
						</c:forEach>
					</table>
					<table id="confirmation" style="width:764px; display:none;" cellspacing="0" cellpadding="0"	align="center" border="0">
						<tr>
							<td class="admTableTDLast admRight" colspan="2">
								<table cellspacing="0" cellpadding="0" style="margin:0 10px 5px 0;">
									<tr>
										<td width="80%" class="admRight">
											<fmt:message key="LANGUAGE"/>
										</td>
										<td width="20%" class="admRight">
											<select id="lang_select" name="lang_id" class="admTextArea admWidth150"
												<c:if test="${group.i18n == false}"><c:out value="disabled" /></c:if>
												onchange="window.location.href = '/admin/nl_settings?lang_id=' +document.getElementById('lang_select').options[document.getElementById('lang_select').selectedIndex].value">
												<c:forEach var="lang" items="${languages}">
													<option value="${lang.id}" id="lang_id"
														<c:if test="${language.id == lang.id}"><c:out value="selected"/></c:if>>
														<c:out value="${lang.code}" /></option>
												</c:forEach>
											</select>
										</td>
									</tr>
								</table>
							</td>
						</tr>						
						<tr>
							<th class="admTableTD" width="150"><fmt:message key="TEXT"/></th>
							<td class="admTableTDLast">
								<img class="admBorder admHand"
									 alt="<fmt:message key="EDIT"/>" title="<fmt:message key="EDIT"/>"
									 onclick="edit_text('article_text', 'contentStyle', '', '',1, true);"
									 src="/images/mark_1.gif" />
								<div id="article_text" class="admRTEDiv" dir="ltr"
									 style="margin-right: 0;">
									<c:choose>
										<c:when test="${confirmationTemplate.text == null}">
											&#160;empty
										</c:when>
										<c:otherwise>
											<c:out value="${confirmationTemplate.text}" escapeXml="yes" />
										</c:otherwise>
									</c:choose>
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
		</negeso:admin>

		<table cellpadding="0" cellspacing="0"  width="764px"  align="center"  border="0" class="Buttons">
			<tr>
				<td>
					<div class="admBtnGreenb">
						<div class="imgL"></div>
						<div><input type="button" class="admNavbarInp" value='<fmt:message key="SAVE"/>' onClick="save(); return false;" /></div>
						<div class="imgR"></div>
					</div>
					<div class="admBtnGreenb admBtnBlueb">
						<div class="imgL"></div>
						<div><input type="reset" name="resetform" value='<fmt:message key="RESET"/>'></div>
						<div class="imgR"></div>
					</div>
				</td>
			</tr>
		</table>			
	</form>

</body>
</html>