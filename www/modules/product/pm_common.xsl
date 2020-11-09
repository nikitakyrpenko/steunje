<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$

  Copyright (c) 2003 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential
  Information and shall use it only in accordance with the terms of the
  license agreement you entered into with Negeso.

  Common templates for Product module

  @version      $Revision$
  @author       Olexiy.Strashko
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:negeso="http://negeso.com/2003/Framework"
	xmlns:java="http://xml.apache.org/xslt/java"
	exclude-result-prefixes="java">

<xsl:output method="html"/>

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>

<!-- ******************************* Tax chooser ********************************** -->
<xsl:template name="pm.tax-chooser">
	<xsl:for-each select="negeso:taxes">
		<select class="admTextArea admWidth200" name="tax_id">
			<option value=""></option>
			<xsl:for-each select="negeso:tax">
				<option value="{@id}">
					<xsl:if test="count(@selected) > 0">
						<xsl:attribute name="selected">true</xsl:attribute>
					</xsl:if>

					<xsl:value-of select="@user-title"/>
					
					<xsl:if test="count(@default) > 0">
						(<xsl:value-of select="java:getString($dict_common, 'DEFAULT')"/>)
					</xsl:if>
				</option>
			</xsl:for-each>
		</select> 
	</xsl:for-each>
</xsl:template>

</xsl:stylesheet>

