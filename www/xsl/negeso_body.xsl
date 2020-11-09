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

    <xsl:template name="NegesoBody">
        <xsl:param name="backLink" select="''" />
        <xsl:param name="helpLink" select="''"/>
        <xsl:param name="showHelp" select="''" />
        <xsl:param name="close" select="''" />
        <table class="admMain" align="center" border="0" cellpadding="0" cellspacing="0">
            <tr>
                <td class="admConnerLeft"></td>
                <td class="admTopBtn">
                    <xsl:call-template name="NavBar">
                        <xsl:with-param name="backLink" select="$backLink" />
                        <xsl:with-param name="helpLink" select="$helpLink"/>
                        <xsl:with-param name="showHelp" select="$showHelp" />
                        <xsl:with-param name="close" select="$close" />
                    </xsl:call-template>
                </td>
                <td class="admConnerRight"></td>
            </tr>
            <tr>
                <td class="admMainLeft">
                    <img src="/images/left_bot.png" />
                </td>
                <td>
                    <xsl:apply-templates select="." mode="admContent"/>
                </td>
                <td class="admMaiRight">
                    <img src="/images/right_bot.png" />
                </td>
            </tr>
         </table>
    </xsl:template>


    <!-- ************************************* NEGESO HEADER ************************************* -->
    <xsl:template name="NegesoHeader">
        <xsl:param name="helpLink" select="''" />
        <xsl:param name="showHelp" select="''" />        
        <xsl:if test="$showHelp = ''">            
            <div class="admBtnGreen admBtnBlue">
                <div class="imgL"></div>
                <div>
                    <a class="admBtnText" target="_blank" onfocus="blur()">
                        <xsl:if test="not($helpLink='')">
                            <xsl:attribute name="href">#</xsl:attribute>
                            <xsl:attribute name="class">admBtnText callHelp</xsl:attribute>
                        </xsl:if>
                        <xsl:if test="($helpLink='')">
                            <xsl:attribute name="href">
                                <xsl:text>/admin/help/cms-help_nl.html</xsl:text>
                            </xsl:attribute>
                        </xsl:if>
                        Help                        
                    </a>
                </div>
                <div class="imgR"></div>
            </div>
        </xsl:if>
    </xsl:template>

    <!--*************************************NEGESO NavBar************************************* -->
    <xsl:template name="NavBar">
        <xsl:param name="backLink" select="''" />
        <xsl:param name="close" select="''" />
        <xsl:param name="backId" select="'backLinkTop'" />
        <xsl:param name="helpLink" select="''"/>
        <div><img src="/images/logo.png" /></div>
        <br />
        <br />
        <xsl:if test="not($backLink='false') and not($backLink='')">
            <div class="admBtnGreen">
                <div class="imgL"></div>
                <div>
                    <a class="admBtnText" onfocus="blur()">
                        <xsl:attribute name="id">
                            <xsl:value-of select="$backId"/>
                        </xsl:attribute>
                        <xsl:attribute name="href">
                            <xsl:value-of select="$backLink" />
                        </xsl:attribute>
                        <xsl:value-of select="java:getString($dict_common, 'BACK')"/>
                    </a>
                </div>
                <div class="imgR"></div>
            </div>
        </xsl:if>
        <xsl:if test="not($close='false')">
            <div class="admBtnGreen">
                <div class="imgL"></div>
                <div>
                    <a href="javascript:window.close()" onclick="return window.close()" class="admBtnText" onfocus="blur()">
                        <xsl:value-of select="java:getString($dict_common, 'CLOSE')"/>
                    </a>
                </div>
                <div class="imgR"></div>
            </div>
        </xsl:if>
        <xsl:call-template name="NegesoHeader">
            <xsl:with-param name="helpLink" select="$helpLink"/>
        </xsl:call-template>

    </xsl:template>

    <!--*************************************NEGESO Title************************************* -->
    <xsl:template name="tableTitle">
        <xsl:param name="headtext" select='"SELECT_MODULE"' />

        <xsl:choose>
            <xsl:when test="$headtext=''">
                &#160;
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$headtext" />
            </xsl:otherwise>
        </xsl:choose>

    </xsl:template>
</xsl:stylesheet>
