<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ page import="java.util.*" %>
<%@ page import="com.negeso.framework.i18n.DatabaseResourceBundle,
		         com.negeso.module.product.review.ProductReview" %>



<%@ page session="true"%>
<%response.setHeader("Expires", "0");
  String langCode = (String) request.getAttribute("lang");
  Long langId = (Long) request.getAttribute("langId");
  DatabaseResourceBundle commonBundle = DatabaseResourceBundle.getInstance("dict_common.xsl", langCode);
  DatabaseResourceBundle productBundle = DatabaseResourceBundle.getInstance("dict_product.xsl", langCode);
  DatabaseResourceBundle statBundle = DatabaseResourceBundle.getInstance("dict_statistics.xsl", langCode);
%>
<HTML>
<HEAD>
<META http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="/css/admin_style.css" rel="stylesheet" type="text/css" />
<TITLE><%=productBundle.getString("REVIEWS_TITLE")%></TITLE>

</HEAD>
<BODY>
<br>
<jsp:include page="/WEB-INF/jsp/common/header.jsp"/>
<!--  Navigation table -->
  <!--  Navigation table -->
  <div>
	  <table class="admNavPanel admPanelSpace"  border="0" cellpadding="0" cellspacing="0" align="center" >
	  <tr>
	    <td class="admNamebarLeft admNavbarImg "><img  src="/images/navBarLeft.gif" alt=""></td>
	    <td class="admNavbar admLeft"><a href="/admin/?command=pm-get-edit-product-page&pmTargetId=${product_id}" class="admNone"><b>&lt;&lt;&lt;&nbsp;<%=commonBundle.getString("BACK")%></b></a></td>
	    <td class="admNavbar admLeft" align="right" style="text-align: right;"><a href="javascript: window.close();" class="admNone"><b>&lt;&nbsp;<%=commonBundle.getString("CLOSE")%>&nbsp;&gt;</b></a></td>
	    <td class="admNamebarLeft admNavbarImg "><img src="/images/navBarRight.gif" alt=""></td>
	  </tr>
	  </table>
  </div>

<!-- Title table -->
<table class="admNavPanel admPanelSpace admTableMarginMini" border="0"
	cellspacing="0" cellpadding="0" align="center">
	<tr>
		<td class="admNavbarImg"><img border="0" alt=""
			src="/images/titleLeft.gif"></td>
		<td class="admTitle"><%=productBundle.getString("REVIEWS_TITLE")%></td>
		<td class="admNavbarImg"><img border="0" alt=""
			src="/images/titleRight.gif"></td>
	</tr>
</table>

<table class="list admNavPanel" cellspacing="0" border="0"
	cellpadding="0" align="center">
	<tr>
		<td class="admNavbar" align="center">
			<input class="admNavbarInp"
				type="submit" name="add"
				value="<&nbsp;<%=commonBundle.getString("ADD")%>&nbsp;>"
				onClick="location.href = 'editreview?action=new&product_id=${product_id}'" />
		</td>
	</tr>
</table>

<table class="admNavPanel admTableMarginSmall" cellspacing="0"
	cellpadding="0" align="center">
	<tr>
		<td class="admNamebar" width="22%"><%=productBundle.getString("PRODUCT_TITLE")%></td>
		<td class="admNamebar" width="22%"><%=productBundle.getString("AUTHOR")%></td>
		<td class="admNamebar" width="22%"><%=productBundle.getString("RATING")%></td>		
		<td class="admNamebar" width="22%"><%=commonBundle.getString("DATE")%></td>
		<td class="admNamebar" width="12%" colspan=2><%=statBundle.getString("ACTIONS")%></td>
	</tr>

	<tbody>
		<c:forEach var="productReview" items="${productReviewList}"
			varStatus="status">
			<tr>
				<td class="admMainTd">
					<a class="admAnchor" href='editreview?review_id=<c:out value="${productReview.id}"/>&action=view'>
					&nbsp;<%=((ProductReview)pageContext.getAttribute("productReview")).getProductTitle(langId)%>&nbsp;
					</a>
					</td>				

				<td class="admLightTd"><c:out value="${productReview.author}" />&nbsp;&nbsp;</td>
				
				<td class="admDarkTD"><c:out value="${productReview.rating}" />&nbsp;&nbsp;</td>
				
				<td class="admMainTd"><c:out value="${productReview.formattedDate}" />&nbsp;&nbsp;</td>
				
				<td class="admDarkTD" valign="top">
					<a 	href='editreview?review_id=<c:out value="${productReview.id}"/>&action=view'><img
						src="/images/docFile.gif" border="0"
						style="background-color: transparent"
						alt="<%=commonBundle.getString("EDIT")%>" />
					</a>
				</td>

				<td class="admMainTd" valign="top">
					<a href='#' 
						onClick="if (confirm('<%=productBundle.getString("REVIEW_REMOVE_MESSAGE")%>'))
						         window.location.href = 'listreview?action=delete&review_id=${productReview.id}&product_id=${product_id}';return false;">
						<img
							src="/images/delete.gif" border="0"
							style="background-color: transparent"
							alt="<%=commonBundle.getString("REMOVE")%>" />
					</a>
				</td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<!-- Indexing table -->
<jsp:include page="/WEB-INF/jsp/common/paging_bar.jsp">
  <jsp:param name="link" value="listreview"/>
</jsp:include>

<table class="list admNavPanel" cellspacing="0" border="0"
	cellpadding="0" align="center">
	<tr>
		<td class="admNavbar" align="center">
			<input class="admNavbarInp"
				type="submit" name="add"
				value="<&nbsp;<%=commonBundle.getString("ADD")%>&nbsp;>"
				onClick="location.href = 'editreview?action=new&product_id=${product_id}'" />
		</td>
	</tr>
</table>

<br />
<br />
  <!--  Navigation table -->
  <div>
	  <table class="admNavPanel admPanelSpace"  border="0" cellpadding="0" cellspacing="0" align="center" >
	  <tr>
	    <td class="admNamebarLeft admNavbarImg "><img  src="/images/navBarLeft.gif" alt=""></td>
	    <td class="admNavbar admLeft"><a href="/admin/?command=pm-get-edit-product-page&pmTargetId=${product_id}" class="admNone"><b>&lt;&lt;&lt;&nbsp;<%=commonBundle.getString("BACK")%></b></a></td>
	    <td class="admNavbar admLeft" align="right" style="text-align: right;"><a href="javascript: window.close();" class="admNone"><b>&lt;&nbsp;<%=commonBundle.getString("CLOSE")%>&nbsp;&gt;</b></a></td>
	    <td class="admNamebarLeft admNavbarImg "><img src="/images/navBarRight.gif" alt=""></td>
	  </tr>
	  </table>
  </div>
</BODY>
</HTML>