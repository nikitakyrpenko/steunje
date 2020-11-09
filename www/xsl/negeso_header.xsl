<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${negeso_header.inc.xsl}		
 
  Copyright (c) 2003 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  Negeso CMS Framework standard header.
 
  @version		2004.01.15
  @author		Olexiy.Strashko
  @author		Volodymyr Snigur
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>

<!-- ************************************* NEGESO HEADER ************************************* -->
<xsl:template name="NegesoHeader">
   <xsl:param name="helpLink" select='""' />
   <xsl:param name="showHelp" select="'yes'" />
	<div>
	<table cellpadding="0" cellspacing="0" border="0" width="100%">
		<tr>
			<td class="admWcmsHeadLogo"><img src="/images/negeso.gif" alt="" width="182" height="41" border="0" align="middle"/></td>
			<td class="admWcmsHead">Web&#160;/&#160;Content&#160;Management&#160;System</td>
		</tr>
		<xsl:if test="$showHelp = 'yes'">
			<tr>
				<td class="admHelp" colspan="2">
				    <a class="admNone" target="_blank">
			                 <xsl:if test="not($helpLink='')">
			                    <xsl:attribute name="href">
			                        <xsl:value-of select="$helpLink" />
			                    </xsl:attribute>
			                </xsl:if>
			                <xsl:if test="($helpLink='')">
			                    <xsl:attribute name="href">
			                        <xsl:text>/admin/help/cms-help_nl.html</xsl:text>
			                    </xsl:attribute>
			                </xsl:if>
				       &lt;&#160;Help&#160;&gt;
				   </a>
			   </td>
			</tr>
		</xsl:if>
	</table>
	</div>
</xsl:template>

<!--*************************************NEGESO NavBar************************************* -->
<xsl:template name="NavBar">
	<xsl:param name="backLink" select='""' />
	<xsl:param name="backId" select="'backLinkTop'" />
	<table cellpadding="0" cellspacing="0" border="0" class="admNavPanel admPanelSpace">
		<tr>
			<td class="admNavbarImg"><img src="/images/navBarLeft.gif" alt="" border="0"/></td>
			<td class="admNavbar admLeft">
				<xsl:choose>
					<xsl:when test="not($backLink='')">
						<a class="admNone">
					    	<xsl:attribute name="id">
					        	<xsl:value-of select="$backId"/>
	                        </xsl:attribute>
							<xsl:attribute name="href">
								<xsl:value-of select="$backLink" />
							</xsl:attribute>
							&lt;&lt;&lt;&#160;<xsl:value-of select="java:getString($dict_common, 'BACK')"/> 
						</a>
					</xsl:when>
					
				</xsl:choose>	
			</td>
			<td class="admNavbar admRight">
				<a href="javascript:window.close()" onclick="return window.close()" class="admNone">
					&lt;&#160;<xsl:value-of select="java:getString($dict_common, 'CLOSE')"/>&#160;&gt;
		     </a>
			</td>
			<td class="admNavbarImg"><img src="/images/navBarRight.gif" alt="" border="0"/></td>
		</tr>
	</table>
</xsl:template>

<!--*************************************NEGESO Title************************************* -->
<xsl:template name="tableTitle">
	<xsl:param name="headtext" select='"SELECT_MODULE"' />
	<table cellpadding="0" cellspacing="0" border="0" height="25" class="admNavPanel admPanelSpace">
		<tr>
			<td class="admNavbarImg"><img src="/images/titleLeft.gif" alt="" border="0"/></td>
			<td class="admTitle">
			       <xsl:choose>
			             <xsl:when test="$headtext=''">
			                     &#160;
			             </xsl:when>
			             <xsl:otherwise>
			                     <xsl:value-of select="$headtext" />
			             </xsl:otherwise>
			       </xsl:choose>
			</td>
			<td class="admNavbarImg"><img src="/images/titleRight.gif" alt="" border="0"/></td>
		</tr>
	</table>
</xsl:template>
</xsl:stylesheet>
