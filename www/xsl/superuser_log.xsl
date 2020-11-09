<?xml version="1.0"?>
<!--
  @(#)${commons.xsl}

  Copyright (c) 2004 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential
  Information and shall use it only in accordance with the terms of the
  license agreement you entered into with Negeso.

  XSL templates Superusers

  @version		2006.14.06
  @author		Andrey Morskoy
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        >
        
<xsl:template match="/negeso:page">
	<html><td><a class="admNone " style="color: #0e4a9e; TEXT-DECORATION: none">
                                <xsl:attribute name="href"><xsl:value-of select="@siteName"/></xsl:attribute>
                                <xsl:value-of select="@siteName"/>
                                </a>
                        </td>
	
	<head>
	 <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    <TITLE>Negeso Web/CMS SuperUser login log</TITLE>
	</head><td><a class="admNone " style="color: #0e4a9e; TEXT-DECORATION: none">
                                <xsl:attribute name="href"><xsl:value-of select="@siteName"/></xsl:attribute>
                                <xsl:value-of select="@siteName"/>
                                </a>
                        </td>
	
	
	<body> 
	    <table class="admNavPanel admPanelSpace admTableMarginMini" border="0" cellspacing="0" cellpadding="0" align="center">
		<tr>
			<td class="admNavbarImg"><img border="0" alt="" src="/images/titleLeft.gif"/></td>
			<td class="admTitle">Negeso Web/CMS SuperUser logins</td>
			<td class="admNavbarImg"><img border="0" alt="" src="/images/titleRight.gif"/></td>
		</tr>
		</table>
		
  <form name="hform" id="hform" metod="get">
  <table class="admNavPanel admTableMarginSmall" cellspacing="0" cellpadding="0" border="0"  align="center" >  
    <tr>
     <td class="admMainTD" width="25%">Select site name</td>
      <td class="admLightTD" style="text-align: left;" colspan="2">
       <select  name="hostName" onChange="window.location.href='viewWMlog?hostName='+document.all['hostName'].value">
		<option value="All">All</option>
		<xsl:for-each select="negeso:WmLOG/negeso:HostNames/negeso:Host">
        	
        	<xsl:choose>
        		<xsl:when test="@isCurrent='true'">
	        	<option selected="true">
	        		<xsl:attribute name="value"><xsl:value-of select="."/></xsl:attribute>
	        		<xsl:value-of select="."/>
	        	</option>             
        		</xsl:when>
        		<xsl:otherwise>
	        	<option>
	        		<xsl:attribute name="value"><xsl:value-of select="."/></xsl:attribute>
	        		<xsl:value-of select="."/>
	        	</option>             
        		</xsl:otherwise>
        		
        	</xsl:choose>
		</xsl:for-each>       
        </select>              
      </td>
    </tr>
    <tr>
   		 <td class="admMainTD" width="25%">Login filter above showed results:</td>
   		 <td class="admLightTD" style="text-align: left;" width="20%"><input type="text" value="" name="loginFilter" maxlength="30" class="admTextArea admWidth100"/></td>
   		 <td class="admLightTD" style="text-align: left;">
	   		 <input type="submit" class="admNavbarInp" style="background-color: transparent;">
	   		 	<xsl:attribute name="value">&lt; Apply &gt;</xsl:attribute>
	   		 </input>
   		 </td>
    </tr>
        </table>
  </form>
		
		<table class="admNavPanel admTableMarginSmall" cellspacing="0" cellpadding="0" width="940px" align="center" >
	  <tr>
				<td class="admNamebar" width="100px" style="text-align: left;">Login Date</td>
				<td class="admNamebar" width="100px" style="text-align: left;">Login</td>
				<td class="admNamebar" width="10px" style="text-align: left;">Admin id</td>
				<td class="admNamebar" width="100px" style="text-align: left;">SuperUser's address</td>
				<td class="admNamebar" width="100px" style="text-align: left;">Login host name</td>
				<td class="admNamebar" width="100px" style="text-align: left;">Login result</td>
	  </tr>
		<xsl:for-each select="negeso:WmLOG/negeso:LdapLogEntry">
		<tr>
			<td><xsl:value-of select="@logDate"/></td>
			<td><xsl:value-of select="@login"/></td>
			<td><xsl:value-of select="@adminId"/></td>
			<td><xsl:value-of select="@remoteAddr"/></td>
			<td><a class="admNone " style="color: #0e4a9e; TEXT-DECORATION: none">
				<xsl:attribute name="href"><xsl:value-of select="@siteName"/></xsl:attribute>
				<xsl:value-of select="@siteName"/>
				</a>
			</td>
			<td><xsl:value-of select="@result"/></td>
		</tr>
		</xsl:for-each>	
		</table>
	</body>
	
	</html>
</xsl:template> 
</xsl:stylesheet>