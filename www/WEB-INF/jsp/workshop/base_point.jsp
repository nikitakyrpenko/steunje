
<!--
  @(#)$Id$		
 
  Copyright (c) 2003 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  View of a list item edit interface.
 
  @author		Olexiy.Strashko
  @version		$Revision$
-->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>


<html>
<head>
<title>NEGESO ::: Version info :::</title>
<META http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link href="/css/admin_style.css" rel="stylesheet" type="text/css" />
</head>
<body>
<!-- NEGESO HEADER -->

<jsp:include page="/WEB-INF/jsp/common/header.jsp"/>
	  
<div class="admCenter"><!-- CONTENT -->
<br/>
<br/><br/>
<table cellspacing="0" cellpadding="0" width="50%" border="1">
	<tr class="admBold">
		<td class="admLeft" >
			Version 
		</td>
		<td class="admLeft" >
			<span class="admLeft admBlue" style="font-size:14px;"> ${versionInfo}					
				<!-- xsl:value-of select="negeso:version-info/@id" / --> 
			</span>
		</td>
	</tr>
	<tr class="admBold">
		<td class="admLeft" >
			Log 
		</td>
		<td class="admLeft" >
			<a href="/admin/viewlog" target="_blank">View log</a><br/>  				
			<a href="/admin/getlog" target="_blank">Download log</a><br/>
			<a href="/admin/setlog">Log level management</a><br/>
		</td>
	</tr>
	<tr class="admBold">
		<td class="admLeft" >
			Cache
		</td>
		<td class="admLeft" >
			<a href="/admin/reset_cache" target="_blank">Reset Global Cache</a> Note: useful <br/>
			<a href="/admin/reset_site" target="_blank">Reset Site Cache</a><br/>
		</td>
	</tr>
	<tr class="admBold">
		<td class="admLeft" >
			SQL
		</td>
		<td class="admLeft" >
			<a href="/negeso/sqldump.sql" target="_blank">Get SQL database dump <br/>
		</td>
	</tr>
	
	<tr class="admBold">
		<td class="admLeft" >
			Site settings
		</td>
		<td class="admLeft" >
			<a href="/admin/site_settings" target="_blank">Edit site settings(favicon,frontpage....) <br/>
		</td>
	</tr>
	
	<tr class="admBold">
		<td class="admLeft" >
			Product module
		</td>
		<td class="admLeft" >
			<a href="/admin/pm_manage_types">Manage product types</a> Note: useful <br/>
			<a href="/admin/pm_filter">Manage Filters</a><br/>
		</td>
	</tr>
	<tr class="admBold">
		<td class="admLeft" >
			Custom consts
		</td>
		<td class="admLeft" >
			<a href="/admin/consts">Manage custom consts</a><br/>
		</td>
	</tr>
	<tr class="admBold">
		<td class="admLeft" >
			Parameters 
		</td>
		<td class="admLeft" >
			<x:parse var="modules" xml="${modulesXml}"/>
			<x:forEach select="$modules//*[local-name()='module']" >
				<x:if select="@parametersCount>0">
					<a href="/admin/parameters?moduleId=<x:out select="@id" />" target="_blank">
						<x:out select="@title" />
					</a><br/>  				
				</x:if>	
			</x:forEach>
			<a href="/admin/parameters" target="_blank">
				Unlinked parameters
			</a><br/>  				
		</td>
	</tr>

</table>

<br/>
<br/><br/>
<tr>
	<td class="admBackLogo" colspan="2">
	</td>
</tr>
<tr>
	<td colspan="2" class="admCenter admCopyright">&#xA9; Negeso 2006</td>
</tr>
</table>

</div>
</body>
</html>
