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
<title><fmt:message key="THR_ORDER_DETAILS" />
</title>

<link href="/css/admin_style.css" rel="stylesheet" type="text/css" />

<link href="/css/customized-jquery-ui.css" rel="stylesheet"
	type="text/css" />
<link href="/css/jqx.base.css" rel="stylesheet" type="text/css" />
<link href="/css/jqx.classic.css" rel="stylesheet" type="text/css" />

<script type="text/javascript" src="/script/jquery.min.js"></script>
<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
<script type="text/javascript" src="/script/jqxcore.js"></script>
<script type="text/javascript" src="/script/jqxswitchbutton.js"></script>
<script type="text/javascript" src="/script/cufon-yui.js"></script>
<script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
<script type="text/javascript" src="/script/common_functions.js"></script>
<script type="text/javascript" src="/script/RTE_Adapter.js">/**/</script>
<script language="JavaScript" src="/script/rte_implement.js">/**/</script>
<script type="text/javascript" src="/script/media_catalog.js"></script>
<script type="text/javascript">
	
</script>
</head>

<body>
	<form method="POST" name="opperateForm">
		<input type="hidden" name="action" value="save" />
		<negeso:admin>
			<table align="center" border="0" cellpadding="0" cellspacing="0"
				class="admTable">
				 <tr>
					<td style="width:auto; height:auto;"><jsp:include page="/WEB-INF/jsp/common/navig_bar.jsp"/></td>
				</tr>
				
				<tr>
					<td align="center" class="admNavPanelFont" style="height: auto;">
						<fmt:message key="THR_ORDER_DETAILS" />
					</td>
				</tr>
			</table>				
			<table align="center" border="0" cellpadding="0" cellspacing="0" class="admTable">
				<tr>
					<td class="admTableTD admBold" width="20%">
						<fmt:message key="THR_ORDER_DATE" />
					</td>
					<td class="admTableTDLast">
						<fmt:formatDate value="${order.date}" pattern="yyyy-MM-dd HH:mm"/>
					</td>
				</tr>
				<tr>
					<td class="admTableTD admBold" >
						<fmt:message key="THR_ORDER_LOGIN" />
					</td>
					<td class="admTableTDLast">
						<c:out value="${order.login}"/>
					</td>
				</tr>
				<tr>
					<td class="admTableTD admBold" >
						<fmt:message key="THR_ORDER_PASSWORD" />
					</td>
					<td class="admTableTDLast">
						<c:out value="${order.password}"/>
					</td>
				</tr>
				<tr>
					<td class="admTableTD admBold" >
						<fmt:message key="THR_ORDER_APP_VERSION" />
					</td>
					<td class="admTableTDLast">
						<c:out value="${order.appVersion}"/>
					</td>
				</tr>
				<tr>
					<td class="admTableTD admBold" >
						<fmt:message key="THR_ORDER_OS_VESION" />
					</td>
					<td class="admTableTDLast">
						<c:choose>
								<c:when test="${order.apiVersion == null}"><img title="iOS" src="/images/apple.png"/></c:when>
								<c:otherwise><img title="iOS" src="/images/android.png"/></c:otherwise>
							</c:choose>${order.osVersion}
							<c:if test="${order.apiVersion != null}">
								(API Level: ${order.apiVersion})
							</c:if>
					</td>
				</tr>
				<tr>
					<td class="admTableTD admBold" >
						<fmt:message key="THR_ORDER_DEVICE" />
					</td>
					<td class="admTableTDLast">
						<c:out value="${order.device}"/>
					</td>
				</tr>
				<tr>
					<td class="admTableTD admBold" >
						<fmt:message key="THR_ORDER_MODEL" />
					</td>
					<td class="admTableTDLast">
						<c:out value="${order.model}"/>
					</td>
				</tr>
				<tr>
					<td class="admTableTD admBold" >
						<fmt:message key="THR_ORDER_SCREEN_WIDTH" />
					</td>
					<td class="admTableTDLast">
						<c:out value="${order.width}"/>
					</td>
				</tr>
				<tr>
					<td class="admTableTD admBold" >
						<fmt:message key="THR_ORDER_SCREEN_HEIGHT" />
					</td>
					<td class="admTableTDLast">
						<c:out value="${order.height}"/>
					</td>
				</tr>
				<tr>
					<td class="admTableFooter" colspan="2">&nbsp;</td>
				</tr>
			</table>
			<table align="center" border="0" cellpadding="0" cellspacing="0" class="admTable"><!--
				<tr>
					<td align="center" class="admTDtitles" style="height: auto;">
						EAN code
					</td>
					<td align="center" class="admTDtitles" style="height: auto;">
						Count
					</td>
				</tr>
				-->
				<c:forEach items="${order.products}" var="product">
					<c:if test="${product.available}">
						<tr>
							<td class="admTableTD">
								<c:if test="${product.productInfo.stock != null}">
									<img src="/images/${product.productInfo.stock}.png" width="32" height="32"/>
								</c:if>
							</td>
							<td class="admTableTD">
								<c:if test="${product.productInfo.image != null}">
									<img src="${product.productInfo.image}" width="32" height="32" onerror="this.src='/images/no_image_52.gif'"/>
								</c:if>
							</td>
							<td class="admTableTD" >
								${product.barCode}
							</td>
							<td class="admTableTD" >
								${product.productInfo.description}
							</td>
							<td class="admTableTD" >
								${product.count}
							</td>
							<td class="admTableTD" >
								${product.productInfo.priceFormatted}
							</td>
							<td class="admTableTDLast" >
								${product.totalPriceFormatted}
							</td>
						</tr>
					</c:if>
				</c:forEach>
				<tr>
					<td class="admTableTDLast" colspan="6">
					</td>
					<td class="admTableTDLast">
						${order.totalFormatted}
					</td>
				</tr>
				<c:if test="${order.nonorderableCount > 0}">
					<tr>
						<td align="center" class="admTDtitles" style="height: auto;" colspan="7">
							<fmt:message key="THR_ORDER_NON_ORDERABLE_ITEMS" />
						</td>
					</tr>
					<c:forEach items="${order.products}" var="product">
						<c:if test="${not product.available}">
							<tr>
								<td class="admTableTD">
									<c:if test="${product.productInfo.stock != null}">
										<img src="/images/R.png" width="32" height="32"/>
									</c:if>
								</td>
								<td class="admTableTD">
										<img src="/images/no_image_52.gif" width="32" height="32" />
								</td>
								<td class="admTableTD" >
									${product.barCode}
								</td>
								<td class="admTableTDLast" colspan="4">
								</td>
							</tr>
						</c:if>
					</c:forEach>
				</c:if>
				<tr>
					<td class="admTableFooter" colspan="2">&nbsp;</td>
				</tr>
			</table>
		</negeso:admin>		
	</form>
</body>
</html>
