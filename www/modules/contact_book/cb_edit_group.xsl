<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$

  Copyright (c) 2005 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential
  Information and shall use it only in accordance with the terms of the
  license agreement you entered into with Negeso.

  Edit location

  @version      $Revision$
  @author       Olexiy.Strashko
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<xsl:include href="/xsl/negeso_body.xsl"/>

<xsl:variable name="lang"><xsl:value-of select="/*/@interface-language"/></xsl:variable>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_contact_book" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_contact_book.xsl', $lang)"/>

<!-- NEGESO JAVASCRIPT Temaplate -->	
<xsl:template name="java-script">
	<script language="JavaScript">
	<xsl:text disable-output-escaping="yes">
	    <![CDATA[
		function update(targetId) {
            if ( !validateForm(operateFormId) ){
                return false;
            }
        	return true;
		}

		]]>
		</xsl:text>
	</script>
</xsl:template>
   
<xsl:template match="/" >
<html>
    <head>
        <title><xsl:value-of select="java:getString($dict_common, 'EDIT_GROUP')"/></title>
        <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
        <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    
        <script type="text/javascript" src="/script/jquery.min.js"></script>
        <script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
        <script type="text/javascript" src="/script/cufon-yui.js"></script>
        <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
        <script language="JavaScript" src="/script/common_functions.js" type="text/javascript">/**/</script>
	    <xsl:call-template name="java-script"/>	
    </head>
    <body>
        <!-- NEGESO HEADER -->
        <xsl:call-template name="NegesoBody">
            <xsl:with-param name="helpLink" select="''"/>
            <xsl:with-param name="backLink">
                <xsl:choose>
                    <xsl:when test="count(descendant::negeso:page/negeso:page) = 0">
                        <xsl:text>'contact_book'</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>''</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:with-param>
        </xsl:call-template>        
    </body>
</html>
</xsl:template>

<xsl:template match="negeso:page" mode="admContent">
    <form method="POST" name="operateForm" id="operateFormId" action="">
        <table cellspacing="0" cellpadding="0" border="0" align="center" width="100%" class="admTable">
            <tr>
                <td style="width:auto; height:auto; padding-left:20px;">
                    <!-- PATH -->
                    <xsl:call-template name="cb.groupPath"/>
                </td>
            </tr>
            <tr>
                <td class="admNavPanelFont" align="center">
                    <!-- TITLE -->
                    <xsl:call-template name="tableTitle">
                        <xsl:with-param name="headtext">
                            <xsl:choose>
                                <xsl:when test="count(negeso:cb-group/@id) = 0">
                                    <xsl:value-of select="java:getString($dict_common, 'ADD_NEW_GROUP')"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="java:getString($dict_common, 'EDIT_GROUP')"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:with-param>
                    </xsl:call-template>
                </td>
            </tr>
            <tr>
                <td>
                    <div class="admCenter">
                        <font color="#FF0000">
                            <xsl:value-of select="errorMessage"/>
                        </font>
                    </div>                    
                    <xsl:apply-templates select="negeso:cb-group"/>                    
                </td>
            </tr>
            <tr>
                <td class="admTableFooter">
                    <xsl:call-template name="buttons"/>
                </td>
            </tr>
        </table>
    </form>   
</xsl:template>

<xsl:template name="buttons">
    <!-- Update/reset fields -->
    <table cellpadding="0" cellspacing="0"  width="764px"  align="center" border="0" class="Buttons">
        <tr>
            <td>
                <div class="admBtnGreenb" style="margin-top:15px; position:absolute;">
                    <div class="imgL"></div>
                    <div>
                        <xsl:choose>
                            <xsl:when test="count(negeso:cb-group/@id) = 0">
                                <input class="admBtnText" name="saveButton" onClick="return update('-1');" type="submit">
                                    <xsl:attribute name="value">
                                        <xsl:value-of select="java:getString($dict_common, 'ADD')"/>
                                    </xsl:attribute>
                                </input>
                            </xsl:when>
                            <xsl:otherwise>
                                <input class="admBtnText" name="saveButton" onClick="return update('{negeso:contact/@id}')" type="submit">
                                    <xsl:attribute name="value">
                                        <xsl:value-of select="java:getString($dict_common, 'SAVE')"/>
                                    </xsl:attribute>
                                </input>
                            </xsl:otherwise>
                        </xsl:choose>
                    </div>
                    <div class="imgR"></div>
                </div>

                <div class="admBtnGreenb" style="margin:15px 0 0 93px; position:absolute;">
                    <div class="imgL"></div>
                    <div>
                        <input class="admBtnText" type="reset">
                            <xsl:attribute name="value">
                                <xsl:value-of select="java:getString($dict_common, 'RESET')"/>
                            </xsl:attribute>
                        </input>                        
                    </div>
                    <div class="imgR"></div>
                </div>
            </td>
        </tr>
    </table>
</xsl:template>
	
<!-- ********************************** Category *********************************** -->
<xsl:template match="negeso:cb-group">
    <input type="hidden" name="command" value="cb-manage-groups"></input>
    <input type="hidden" name="action" value="save_group"></input>
    <input type="hidden" name="groupId" value="{@id}"></input>
    <table cellspacing="0" cellpadding="0" border="0" width="100%">
        <tr>
            <td colspan="2" class="admTDtitles">
                <!-- Title -->
                <xsl:value-of select="java:getString($dict_contact_book, 'GROUP_PROPERTIES')"/>:
            </td>
        </tr>
        <tr>
            <th class="admTableTD">
                <xsl:value-of select="java:getString($dict_common, 'TITLE')"/>*
            </th>
            <th class="admTableTDLast">
                <input class="admTextArea admWidth200"
                    type="text"
                    name="title"
                    data_type="text"
                    required="true"
                    uname="Title"
                    maxlength="199"
		>
                    <xsl:attribute name="value">
                        <xsl:value-of select="@title"/>
                    </xsl:attribute>
                </input>
            </th>
        </tr>
    </table>
</xsl:template>

<xsl:template name="cb.groupPath">
    <!-- Path -->
    <table border="0" cellpadding="0" cellspacing="0" align="left">
        <tr>
            <!-- Unactive pathe element - make it link-->
            <td style="padding:8px 0 0 5px;" valign="middle" class="admNavigation">
                <a class="admLocation" href="contact_book">
                    <xsl:value-of select="java:getString($dict_contact_book, 'CONTACT_BOOK')"/>
                </a>
            </td>

            <!-- Active pathe element - just print it-->
            <td valign="middle" class="admNavigation" style="padding:8px 0 0 0;text-decoration:none;">
                &#160; <img src="/images/navig_arrow.png"  style="vertical-align:middle;"/>&#160;
                <span>
                    <xsl:choose>
                        <xsl:when test="negeso:cb-group/@id">
                            <xsl:value-of select="negeso:cb-group/@title"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="java:getString($dict_common, 'ADD_NEW_GROUP')"/>
                        </xsl:otherwise>
                    </xsl:choose>
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