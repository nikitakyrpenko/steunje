<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${commons.xsl}

  Copyright (c) 2004 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential
  Information and shall use it only in accordance with the terms of the
  license agreement you entered into with Negeso.

  XSL templates Superusers

  @version		2006.08.06
  @author		Andrey Morskoy
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
>
    
    <xsl:include href="/customers/site/xsl/site.xsl"/>
    
    <xsl:template match="/negeso:page">
        <xsl:apply-templates select="//negeso:page" mode="adminLogin">
            <xsl:with-param name="loginMode" select="'su'"/>
        </xsl:apply-templates>
    </xsl:template>

</xsl:stylesheet>