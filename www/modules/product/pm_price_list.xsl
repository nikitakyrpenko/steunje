<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$

  Copyright (c) 2005 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential
  Information and shall use it only in accordance with the terms of the
  license agreement you entered into with Negeso.

  Edit shop preferences

  @version      $Revision$
  @author       Olexiy.Strashko
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:negeso="http://negeso.com/2003/Framework"
	xmlns:java="http://xml.apache.org/xslt/java"
	exclude-result-prefixes="java">

<xsl:include href="/xsl/negeso_header.xsl"/>

<xsl:variable name="lang"><xsl:value-of select="/*/@interface-language"/></xsl:variable>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_product" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_product.xsl', $lang)"/>

<!-- NEGESO JAVASCRIPT Temaplate -->	
<xsl:template name="java-script">
	<script language="JavaScript">
	<xsl:text disable-output-escaping="yes">
	    <![CDATA[
		]]>
		</xsl:text>
	</script>
</xsl:template>
	
<xsl:template match="/" >
<html>
<head>
   <title>Price list manager</title>
   <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
   
	<xsl:call-template name="java-script"/>
	<link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
</head>
<body>
    <!-- NEGESO HEADER -->
	<xsl:call-template name="NegesoHeader"/>
    <div align="center">
        <!-- CONTENT -->
        <xsl:apply-templates select="negeso:page"/>
    </div>
</body>
</html>
</xsl:template>

<xsl:template match="negeso:page">
    <!-- NavBar -->
<form method="POST" name="operateForm" id="operateFormId" action="" enctype="multipart/form-data">
	<input type="hidden" name="command" value="pm-manage-price-list"/>
    <xsl:choose>
        <xsl:when test="count(descendant::negeso:page) = 0">
            <xsl:call-template name="NavBar">
	            <xsl:with-param name="backLink" select='"?command=pm-get-entry-page"'/>
            </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
              <xsl:call-template name="NavBar"/>
        </xsl:otherwise>
   </xsl:choose>

    <!-- PATH -->
	<xsl:call-template name="pm.shopPath"/>

    <!-- TITLE -->
    <xsl:call-template name="tableTitle">
        <xsl:with-param name="headtext">
        	Price list manager
        </xsl:with-param>
    </xsl:call-template>

	<table class="admNavPanel" cellspacing="0" cellpadding="0">
	    <xsl:apply-templates select="negeso:price-loader"/>
	</table>

    	  
    <!-- NavBar -->
    <xsl:choose>
        <xsl:when test="count(descendant::negeso:page) = 0">
            <xsl:call-template name="NavBar">
	            <xsl:with-param name="backLink" select='"?command=pm-get-entry-page"'/>
            </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
              <xsl:call-template name="NavBar"/>
        </xsl:otherwise>
   </xsl:choose>
</form>   
</xsl:template>

<xsl:template match="negeso:price-loader">
<xsl:param name="prefix" />
	<table border="0" width="100%" class="admNavPanel">
		<xsl:if test="@status='error'">
			<tr><td class="admLightTD admLeft" colspan="3">
				<font color="#FF0000"><b><xsl:value-of select="@message"/></b></font>
			</td></tr>
		</xsl:if>
		<xsl:if test="@status='success'">
			<tr><td class="admLightTD admLeft" colspan="3">
				<b><xsl:value-of select="@message"/></b>
			</td></tr>
		</xsl:if>
		<tr>
			<td class="admMainTD admWidth150">&#160;&#160;Select price list</td>
			<td class="admLightTD admLeft">
    			<input class="admTextArea admWidth200" 
    				type="file" 
    				name="priceList" 
				onKeyPress="return false;"
    			>
				</input>
			</td>
			<td class="admLightTD admLeft">
				Price list file format : <br/> *.csv, with every line: {sn},{p1},{p2},{p3},{p4},{p5},{p6} <br/><br/>
				<b>Note:</b> Operation can take a long time, so import results will be mailed to: <xsl:value-of select="@email"/> 
			</td>
		</tr>
	</table>
	
	<!-- Update/reset fields -->
       <table cellpadding="0" cellspacing="0"  class="admNavPanel">
		<tr>
			<td class="admNavPanel admNavbar admCenter">
		        <input class="admNavbarInp" name="saveButton" type="submit">
                       <xsl:attribute name="value">&lt;&#160;Upload&#160;&gt;</xsl:attribute>
                   </input>
			</td>
		</tr>
   	</table>

    <xsl:call-template name="tableTitle">
        <xsl:with-param name="headtext">
        	Current price list
        </xsl:with-param>
    </xsl:call-template>
	<xsl:if test="negeso:price-list">
	    <xsl:for-each select="negeso:price-list">
				<table class="admNavPanel" cellspacing="0" cellpadding="0">
				    <tr class="admLightTD">
				        <td width="10%" class="admNamebar"><b>Sn</b></td>
				        <td class="admNamebar"><b>P1</b></td>
				        <td class="admNamebar"><b>P2</b></td>
				        <td class="admNamebar"><b>P3</b></td>
				        <td class="admNamebar"><b>P4</b></td>
				        <td class="admNamebar"><b>P5</b></td>
				        <td class="admNamebar"><b>P6</b></td>
				    </tr>
				    <xsl:for-each select="negeso:product">
					    <tr>
					        <td class="admMainTD"><xsl:value-of select="@sn"/></td>
					        <td class="admLightTD"><xsl:value-of select="@P1"/></td>
					        <td class="admLightTD"><xsl:value-of select="@P2"/></td>
					        <td class="admLightTD"><xsl:value-of select="@P3"/></td>
					        <td class="admLightTD"><xsl:value-of select="@P4"/></td>
					        <td class="admLightTD"><xsl:value-of select="@P5"/></td>
					        <td class="admLightTD"><xsl:value-of select="@P6"/></td>
					    </tr>
					</xsl:for-each>
	               	</table>
		</xsl:for-each>
	</xsl:if>
</xsl:template>


<xsl:template name="pm.shopPath">
    <!-- Path -->
    <table cellpadding="0" cellspacing="0" class="admNavPanel admSpaceSmall">
	   	<tr>
            <td class="admBold">
				<!-- Unactive pathe element - make it link-->
				<span class="admZero admLocation">
			    <a class="admLocation" href="?command=pm-get-entry-page">
			    	<xsl:value-of select="java:getString($dict_product, 'PRODUCT_MODULE')"/>
			    </a>
			    &#160;&gt;
				</span>
			
				<!-- Active pathe element - just print it-->
				<span class="admSecurity admLocation">
			    	<xsl:value-of select="java:getString($dict_product, 'PRICE_LIST_MANAGER')"/>
			    	Price list manager
				</span>
            </td>
        </tr>
    </table>
</xsl:template>


<!-- ******************************* Error message ********************************** -->
<xsl:template match="errorMessage">
    <xsl:value-of select="errorMessage"/>
</xsl:template>

</xsl:stylesheet>
