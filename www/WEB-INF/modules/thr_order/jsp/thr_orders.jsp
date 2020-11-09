<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="negeso"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
	response.setHeader("Expires", "0");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><fmt:message key="THR_ORDERS" />
</title>

<link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
<link rel="stylesheet" type="text/css" href="/css/jquery-ui.custom.css"/>
<script type="text/javascript" src="/script/jquery.min.js"></script>
<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
<script type="text/javascript" src="/script/jquery-ui-timepicker-addon.js">/**/</script>
<script type="text/javascript" src="/script/jqxcore.js"></script>
<script type="text/javascript" src="/script/jqxswitchbutton.js"></script>
<script type="text/javascript" src="/script/cufon-yui.js"></script>
<script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
<script type="text/javascript" src="/script/common_functions.js"></script>
<script type="text/javascript">
function AddClearButton(input)
{
	setTimeout(function(){
	var buttonPane = $( input )
	.datepicker( "widget" )
	.find( ".ui-datepicker-buttonpane" );
	
	var btn = $('<button id="customClearButton" class="ui-datepicker-current ui-state-default ui-priority-secondary ui-corner-all" type="button">Clear</button>');
	btn
	.unbind("click")
	.bind("click", function () {
		$.datepicker._clearDate( input );
	});
	
	// Check if buttonPane has that button
	
	if(buttonPane.has('#customClearButton').length==0)
	btn.appendTo( buttonPane );
	}, 1 );
}
</script>
</head>

<body>
	<form method="POST" name="opperateForm">
		<negeso:admin>
			<table align="center" border="0" cellpadding="0" cellspacing="0"
				class="admTable">
				 <tr>
					<td style="width:auto; height:auto;"><jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp"/></td>
				</tr>
				<tr>
					<td align="center" class="admNavPanelFont" style="height: auto;">
						<fmt:message key="THR_ORDERS" />
					</td>
				</tr>
			</table>				
			<table align="center" border="0" cellpadding="0" cellspacing="0" class="admTable" >
				<tr>
					<td align="center" class="admNavPanelFont" style="height: auto;">
						<fieldset class="admFieldset">
  							<legend>Report</legend>
	  						<table align="center" border="0" cellpadding="5" cellspacing="0" width="100%">
	  							<tr>
	  								<td>
	  									Start date
	  								</td>
	  								<td>
										<input name="startDate" id="startDate" value="<fmt:formatDate value="${filter.startDate}" pattern="yyyy-MM-dd"/>" type="text" class="admTextArea admWidth150"  readonly="readonly" style="float: none; margin-right: 5px;" />							
											<script>
												$(function () {
													$("#startDate").datepicker({
														dateFormat: 'yy-mm-dd',
														showOn: 'button',
														buttonImage: '/images/calendar.gif',
														buttonImageOnly: true,
														showButtonPanel: true,
														beforeShow: function( input ) {
															AddClearButton(input);
														},
														onChangeMonthYear:function( year, month, inst ) {
															AddClearButton(inst.input);
														}
													});
												});
											</script>
	  								</td>
	  								<td>
	  									End date
	  								</td>
	  								<td>
										<input name="endDate" id="endDate" value="<fmt:formatDate value="${filter.endDate}" pattern="yyyy-MM-dd"/>" type="text" class="admTextArea admWidth150"  readonly="readonly" style="float: none; margin-right: 5px;"/>							
											<script>
												$(function () {
													$("#endDate").datepicker({
														dateFormat: 'yy-mm-dd',
														showOn: 'button',
														buttonImage: '/images/calendar.gif',
														buttonImageOnly: true,													
														showButtonPanel: true,
														beforeShow: function( input ) {
															AddClearButton(input);
														},
														onChangeMonthYear:function( year, month, inst ) {
															AddClearButton(inst.input);
														}
													});
												});
											</script>
	  								</td>
	  							</tr>
	  							<tr>
									<td>
										Login: 
									</td>
									<td>
										<select class="admInpText admWidth150" name="login">
											<option></option>
											<c:forEach var="login" items="${logins}">
												<option value="<c:out value="${login}"/>"
												<c:if test="${login eq filter.login}">
													selected="selected"
												</c:if>
												><c:out value="${login}"/></option>
											</c:forEach>
										</select>
									</td> 								
									<td>
										EAN code: 
									</td>
									<td>
										<input class="admInpText admWidth150" name="barCode" value="${filter.barCode}"/>
									</td> 								
	  							</tr>
	  							<tr>
	  								<td colspan="4" align="center">
	  									<div class="admNavPanelInp" style="float: left; padding-left: 0px;">
											<div class="imgL"></div>
											<div><input type="submit"  value="<fmt:message key="Search" />"></div>
											<div class="imgR"></div>
										</div>
	  								</td>
	  							</tr>
	  						</table> 
						</fieldset>
					</td>
				</tr>
			</table>
			<table align="center" border="0" cellpadding="0" cellspacing="0" class="admTable">
				<tr>
					<td align="center" class="admTDtitles" style="height: auto;">
						<fmt:message key="THR_ORDER_DATE" />
					</td>
					<td align="center" class="admTDtitles" style="height: auto;">
						<fmt:message key="THR_ORDER_LOGIN" />
					</td>
					<td align="center" class="admTDtitles" style="height: auto;" colspan="2">
						<fmt:message key="THR_ORDER_OS_VESION" />
					</td>
					<td align="center" class="admTDtitles" style="height: auto;">
						<fmt:message key="THR_ORDER_MODEL" />
					</td>
					<td align="center" class="admTDtitles" style="height: auto;">
						<fmt:message key="THR_ORDER_PRODUCTS" />
					</td>
					<td align="center" class="admTDtitles" style="height: auto;">
						<fmt:message key="THR_ORDER_TOTAL" />
					</td>
					<td align="center" class="admTDtitles" style="height: auto;">
						
					</td>
				</tr>
				<c:forEach items="${orders}" var="order">
					<tr>
						<td class="admTableTD" width="10%">
							<fmt:formatDate value="${order.date}" pattern="yyyy-MM-dd HH:mm"/>
							
						</td>						
						<td class="admTableTD" width="10%">
							<c:out value="${order.login}"/>							
						</td>	
						<td class="admTableTDLast" width="1%">
							<c:choose>
								<c:when test="${order.apiVersion == null}"><img title="iOS" src="/images/apple.png"/></c:when>
								<c:otherwise><img title="iOS" src="/images/android.png"/></c:otherwise>
							</c:choose>
						</td>
						<td class="admTableTD" width="10%">
							${order.osVersion}
						</td>
						<td class="admTableTD" width="10%">
							<c:out value="${order.model}"/>							
						</td>	
						<td class="admTableTD" width="10%" style="text-align: center;">
							${order.productsCount} / ${order.nonorderableCount}						
						</td>
						<td class="admTableTD" width="10%" style="text-align: right; padding-right: 5px;">
							${order.totalFormatted}
						</td>
						<td class="admTableTDLast" style="padding: 3;" width="3%">
										<img onclick="location.href='/admin/thr_orders.html?action=details&id=${order.id}'" title="<fmt:message key="THR_ORDER_DETAILS" />" class="admHand" src="/images/edit.png"/>
									</td>
					</tr>
				</c:forEach>
				<tr>
					<td class="admTableFooter" colspan="9">&nbsp;</td>
				</tr>
			</table>
					
		</negeso:admin>		
	</form>
</body>
</html>
