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
		    <td class="admNavbar admLeft"><a href="/admin/pm_manage_types" class="admNone"><b>&lt;&lt;&lt;&nbsp;<%=commonBundle.getString("BACK")%></b></a></td>
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
			<td class="admTitle"><%=productBundle.getString("MANAGE_PM_ATTRIBUTES")%></td>
			<td class="admNavbarImg"><img border="0" alt="" src="/images/titleRight.gif"></td>
		</tr>
	</table>
	<form action="pm_manage_attributes" name="pm_typesForm" enctype="multipart/form-data" method="get">
		<table class="list admNavPanel" cellspacing="0" border="0" cellpadding="0" align="center">
			<tr>
				<td class="admNavbar" align="center">
					<div class="admCenter">
						<c:if test="${fn:contains(detailedAttribute.typeName, 'select_dropdown')}">
							<input type="hidden" name="dropDown" value="option"/>
						</c:if>
						<c:if test="${fn:contains(detailedAttribute.typeName, 'select_checkbox')}">
							<input type="hidden" name="checkDown" value="option"/>
						</c:if>
						<c:if test="${detailedAttribute != null && fn:contains(detailedAttribute.typeName, 'select_')}">
							<fieldset>
								<legend>Change type</legend>
						</c:if>							
						<label for="name">Name:</label>&#160;<input type="text" name="name" value="${detailedAttribute.name}" id="name"/>
						<label for="title">Title:</label>&#160;<input type="text" name="userName" value="${detailedAttribute.userName}" id="title"/><br/>
						<label for="order">Order number:</label>&#160;<input type="text" name="orderNumber" value="${detailedAttribute.orderNumber}" id="order"/><br/>
						<c:if test="${detailedAttribute == null}">
							<label for="name">Type:</label>&#160;
							<select name="typeName" id="typeName"/>
								<option value="string">String</option>
								<option value="text">Text area</option>
								<option value="article">RTE Article</option>
								<option value="thumbnail">Thumbnail</option>
								<option value="doc_file">Attached document</option>
								<option value="currency">Price</option>
								<option value="checkbox">Checkbox</option>
								<option value="select_dropdown">Dropdown</option>
                                <option value="number">Number</option>
                                <option value="real_value">Real Value</option>
                                <option value="select_checkbox">Checkbox list</option>
							</select><br/>
						</c:if>
						<c:if test="${detailedAttribute != null}">	
							Type: ${detailedAttribute.typeName} 
						</c:if>
	
						<br/>
						<label for="name">Is Required:</label>&#160;<input type="checkbox" name="isRequired" <c:if test="${detailedAttribute.required}">checked</c:if> />&#160;&#160;
						<label for="name">Is I18n:</label>&#160;<input type="checkbox" name="isI18n" <c:if test="${detailedAttribute.i18n}">checked</c:if>/>&#160;&#160;
						<label for="name">Is Face:</label>&#160;<input type="checkbox" name="isFace" <c:if test="${detailedAttribute.face}">checked</c:if>/>&#160;&#160;
						<label for="name">Is Narrow:</label>&#160;<input type="checkbox" name="isNarrow" <c:if test="${false}">checked</c:if>/>&#160;&#160;
						<label for="name">is Sorted:</label>&#160;<input type="checkbox" name="isSorted" <c:if test="${detailedAttribute.sorted}">checked</c:if>/>
						<br/>
						<input class="admNavbarInp" type="hidden" name="typeId" value="${typeId}"/>
						<input class="admNavbarInp" type="hidden" name="attrId" value="${detailedAttribute.id}"/>
						<br/>
						<!--fieldset class="admLeft">
							<legenda>Present in:<legenda>
							<input type="checkbox" name="nl">&#160;Dutch
							<br/>	
							<input type="checkbox" name="fr">&#160;French
							<br/>
						</fieldset-->
						<c:if test="${detailedAttribute == null}">
							<&nbsp;<input class="admNavbarInp" type="submit" name="act" value="add" />&nbsp;>
							&nbsp;&nbsp;
						</c:if>
						<&nbsp;<input class="admNavbarInp" type="submit" name="act" value="save"/>&nbsp;>
						<c:if test="${detailedAttribute != null && fn:contains(detailedAttribute.typeName, 'select_')}">
							</fieldset>
						</c:if>	
					</div>
				</td>
			</tr>
		</table>
		<c:if test="${detailedAttribute != null && fn:contains(detailedAttribute.typeName, 'select_')}">
			<div class="admCenter">
				<fieldset class="admNavPanel admNavbar">
					<legend>Add Options</legend>
		</c:if>	
		<table class="admNavPanel admTableMarginSmall" cellspacing="0" cellpadding="0" border="0" align="center">
			<tr>
				<td class="admNamebar" width="15%">Name</td>
				<td class="admNamebar" width="22%">Title</td>
				<td class="admNamebar" width="10%">Type</td>
				<td class="admNamebar" width="5%">Order</td>
				<td class="admNamebar" width="2%">&nbsp;IsRequied</td>
				<c:if test="${detailedAttribute != null && !fn:contains(detailedAttribute.typeName, 'select_')}">
					<td class="admNamebar" width="2%">&nbsp;IsI18n</td>
					<td class="admNamebar" width="2%">&nbsp;IsFace</td>
					<td class="admNamebar" width="2%">&nbsp;IsNarrow</td>
					<td class="admNamebar" width="2%">&nbsp;IsSorted</td>
					<td class="admNamebar" width="12%" colspan=2><%=statBundle.getString("ACTIONS")%></td>
				</c:if>
			</tr>
			<tbody>
				<c:choose>
					<c:when test="${detailedAttribute != null && fn:contains(detailedAttribute.typeName, 'select_')}">
						<tr>
							<td colspan="5">
								<div class="admCenter">
									<br/>
									<label for="optName">Option Name:</label>&#160;<input type="text" name="optTitle" value="${detailedDropAttribute.str_value}" id="optName"/>
									<br/>
									<c:if test="${empty detailedDropAttribute.str_value}">
										<&nbsp;<input class="admNavbarInp" type="submit" name="act" value="add" />&nbsp;>
									</c:if>
									&nbsp;&nbsp;
									<&nbsp;<input class="admNavbarInp" type="submit" name="act" value="save"/>&nbsp;>
								</div>
							</td>
						</tr>
						<c:forEach var="option" items="${typeDropDownAttributes}">
							<tr>
								<td class="admMainTd">
									<!--a class="admAnchor" href='pm_manage_attributes?typeId=${typeId}&attrId=${attrType.id}&act=details'>
										${option.title}
									</a-->${option.title}
								</td>				
								<td class="admLightTd">${option.title}</td>				
								<td class="admDarkTd">${option.langId}</td>
								<td class="admLightTd" valign="top">
									<!--a 	href='pm_manage_attributes?attrId=${attrType.id}&act=details&typeId=${typeId}'-->
									<a href="javascript: alert('This function is under construction!');">
										<img src="/images/docFile.gif" border="0" style="background-color: transparent" 
											alt="<%=commonBundle.getString("EDIT")%>" />
									</a>
								</td>
								<td class="admMainTd" valign="top">
									<!--a href='#' 
										onClick="if (confirm('<%=productBundle.getString("PRODTYPE_REMOVE_MESSAGE")%>'))
										         window.location.href = 'pm_manage_attributes?act=delete&attrId=${attrType.id}&typeId=${typeId}';return false;"-->
									<a href="javascript: alert('This function is under construction!');">
										<img src="/images/delete.gif" border="0" style="background-color: transparent" 
											alt="<%=commonBundle.getString("REMOVE")%>" />
									</a>
								</td>
							</tr>
						</c:forEach>
					</c:when>
					<c:otherwise>
						${fn:contains(detailedAttribute.typeName, 'select_')}
						${detailedAttribute != null}
						<c:forEach var="attrType" items="${typeAttributes}">
							<tr>
								<td class="admMainTd">
									<a class="admAnchor" href='pm_manage_attributes?typeId=${typeId}&attrId=${attrType.id}&act=details'>
										${attrType.name}
									</a>
								</td>				
								<td class="admLightTd">${attrType.userName}</td>				
								<td class="admDarkTd">${attrType.typeName}</td>				
								<td class="admLightTd">${attrType.orderNumber}</td>				
								<td class="admDarkTd">
									<c:if test="${attrType.required}">X</c:if>
								</td>				
								<td class="admLightTd">
									<c:if test="${attrType.i18n}">X</c:if>
								</td>				
								<td class="admDarkTd">
									<c:if test="${attrType.face}">X</c:if>
								</td>				
								<td class="admLightTd">
									<c:if test="${false}">X</c:if>
								</td>				
								<td class="admDarkTd">
									<c:if test="${attrType.sorted}">X</c:if>
								</td>				
								<td class="admLightTd" valign="top">
									<a 	href='pm_manage_attributes?attrId=${attrType.id}&act=details&typeId=${typeId}'>
										<img src="/images/docFile.gif" border="0" style="background-color: transparent" 
											alt="<%=commonBundle.getString("EDIT")%>" />
									</a>
								</td>
								<td class="admMainTd" valign="top">
									<a href='#' 
										onClick="if (confirm('<%=productBundle.getString("PRODTYPE_REMOVE_MESSAGE")%>'))
										         window.location.href = 'pm_manage_attributes?act=delete&attrId=${attrType.id}&typeId=${typeId}';return false;">
										<img src="/images/delete.gif" border="0" style="background-color: transparent" 
											alt="<%=commonBundle.getString("REMOVE")%>" />
									</a>
								</td>
							</tr>
						</c:forEach>
					</c:otherwise>
				</c:choose>
			</tbody>
		</table>
		<c:if test="${detailedAttribute != null && fn:contains(detailedAttribute.typeName, 'select_')}">
				</fieldset>
			</div>
		</c:if>	
	</form>
	<!--  Navigation table -->
	<div>
		<table class="admNavPanel admPanelSpace"  border="0" cellpadding="0" cellspacing="0" align="center" >
		  <tr>
		    <td class="admNamebarLeft admNavbarImg "><img  src="/images/navBarLeft.gif" alt=""></td>
		    <td class="admNavbar admLeft"><a href="/admin/pm_manage_types" class="admNone"><b>&lt;&lt;&lt;&nbsp;<%=commonBundle.getString("BACK")%></b></a></td>
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
