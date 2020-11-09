<!--
  @(#)$Id$		
 
  Copyright (c) 2006 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  View of a list item edit interface.
 
  @author		Volodymyr.Snigur
  @version		$Revision$
-->

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ page import="java.util.*" %>
<%@ page import="com.negeso.framework.i18n.DatabaseResourceBundle,
		         com.negeso.module.product.workshop.ManageTypesController" %>
		         
<%
  String langCode = (String) request.getAttribute("lang");
  Long langId = (Long) request.getAttribute("langId");
  DatabaseResourceBundle commonBundle = DatabaseResourceBundle.getInstance("dict_common.xsl", langCode);
  DatabaseResourceBundle productBundle = DatabaseResourceBundle.getInstance("dict_product.xsl", langCode);
  DatabaseResourceBundle statBundle = DatabaseResourceBundle.getInstance("dict_statistics.xsl", langCode);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>NEGESO .:: Manage Product Types ::.</title>
	<META http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<link href="/css/admin_style.css" rel="stylesheet" type="text/css" />
</head>
<body>
<!-- NEGESO HEADER -->

	<jsp:include page="/WEB-INF/jsp/common/header.jsp"/>
	<!--  Navigation table -->
	<div>
		<table class="admNavPanel admPanelSpace"  border="0" cellpadding="0" cellspacing="0" align="center" >
		  <tr>
		    <td class="admNamebarLeft admNavbarImg "><img  src="/images/navBarLeft.gif" alt=""></td>
		    <td class="admNavbar admLeft"><a href="/admin/workshop" class="admNone"><b>&lt;&lt;&lt;&nbsp;<%=commonBundle.getString("BACK")%></b></a></td>
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
			<td class="admTitle"><%=productBundle.getString("MANAGE_PM_TYPE")%></td>
			<td class="admNavbarImg"><img border="0" alt="" src="/images/titleRight.gif"></td>
		</tr>
	</table>
	<form action="pm_manage_types" name="pm_typesForm" enctype="multipart/form-data" method="get">
		<table class="list admNavPanel" cellspacing="0" border="0" cellpadding="0" align="center">
			<tr>
				<td class="admNavbar" align="center">
					<label for="name">Name:</lable>&#160;<input type="text" name="name" value="${detailedProductType.name}" id="name"/>
					<label for="name">Title:</lable>&#160;<input type="text" name="title" value="${detailedProductType.title}" id="title"/><br/>
					<input type="hidden" name="typeId" value="${detailedProductType.id}"/>
					<&nbsp;<input class="admNavbarInp" type="submit" name="act" value="add" />&nbsp;>
					&nbsp;&nbsp;
					<&nbsp;<input class="admNavbarInp" type="submit" name="act" value="save"/>&nbsp;>
					<!--
						commented for future translations
					input class="admNavbarInp" type="submit" name="act" value="<&nbsp;<%=commonBundle.getString("ADD")%>&nbsp;>" />
					<input class="admNavbarInp" type="submit" name="act" value="<&nbsp;<%=commonBundle.getString("SAVE")%>&nbsp;>"/-->
				</td>
			</tr>
		</table>
		
		<table class="admNavPanel admTableMarginSmall" cellspacing="0" cellpadding="0" align="center">
			<tr>
				<td class="admNamebar" width="22%"><%=productBundle.getString("PRODUCT_TYPE_TITLE")%></td>
				<td class="admNamebar" width="22%">Title</td>
				<td class="admNamebar" width="12%" colspan=2><%=statBundle.getString("ACTIONS")%></td>
			</tr>
		
			<tbody>
				<c:if test="${error}">${error}test</c:if>
				<c:forEach var="productType" items="${productTypes}">
					<tr>
						<td class="admMainTd">
							<a class="admAnchor" href='pm_manage_attributes?typeId=${productType.id}'>
								${productType.name}
							</a>
						</td>				
						
						<td class="admLightTd">${productType.title}</td>				

						<td class="admDarkTD" valign="top">
							<a 	href='pm_manage_types?typeId=${productType.id}&act=details'>
								<img src="/images/docFile.gif" border="0" style="background-color: transparent" 
									alt="<%=commonBundle.getString("EDIT")%>" />
							</a>
						</td>
		
						<td class="admMainTd" valign="top">
							<a href='#' 
								onClick="if (confirm('<%=productBundle.getString("PRODTYPE_REMOVE_MESSAGE")%>'))
								         window.location.href = 'pm_manage_types?act=delete&typeId=${productType.id}';return false;">
								<img src="/images/delete.gif" border="0" style="background-color: transparent" 
									alt="<%=commonBundle.getString("REMOVE")%>" />
							</a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</form>
	<!--  Navigation table -->
	<div>
		<table class="admNavPanel admPanelSpace"  border="0" cellpadding="0" cellspacing="0" align="center" >
		  <tr>
		    <td class="admNamebarLeft admNavbarImg "><img  src="/images/navBarLeft.gif" alt=""></td>
		    <td class="admNavbar admLeft"><a href="/admin/workshop" class="admNone"><b>&lt;&lt;&lt;&nbsp;<%=commonBundle.getString("BACK")%></b></a></td>
		    <td class="admNavbar admLeft" align="right" style="text-align: right;"><a href="javascript:window.close();" class="admNone"><b>&lt;&nbsp;<%=commonBundle.getString("CLOSE")%>&nbsp;&gt;</b></a></td>
		    <td class="admNamebarLeft admNavbarImg "><img src="/images/navBarRight.gif" alt=""></td>
		  </tr>
		</table>
	</div>
	<div class="admCenter">
		<br/>
		<br/>
		<table cellspacing="0" cellpadding="0" width="50%" border="1">
			<tr class="admBold">
				<td class="admLeft" >
					Cache
				</td>
				<td class="admLeft" >
					<a href="/admin/reset_cache" target="_blank">Reset Global Cache</a> Note: useful <br/>
					<a href="/admin/reset_site" target="_blank">Reset Site Cache</a><br/>
				</td>
			</tr>
		</table>
	
	</div>
</body>
</html>
