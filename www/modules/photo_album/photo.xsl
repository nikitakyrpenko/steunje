<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${album.xsl}
 
  Copyright (c) 2003 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  @author       Sergiy Oliynyk

-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:negeso="http://negeso.com/2003/Framework"
	xmlns:java="http://xml.apache.org/xslt/java"
	exclude-result-prefixes="java">


<xsl:include href="/xsl/admin_templates.xsl"/>
<xsl:include href="/xsl/negeso_body.xsl"/>

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_photo_album" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_photo_album.xsl', $lang)"/>

<xsl:template match="/" >
<html>
<head>
    <title><xsl:value-of select="negeso:photo/@name"/></title>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link rel="stylesheet" href="/css/admin_style.css" type="text/css"/>
    
    <script type="text/javascript"  src="/script/jquery.min.js"/>
    <script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
    <script type="text/javascript" src="/script/common_functions.js"></script>
    <script>
        function openImage(link, width, height) {
            if ((width == null) || (width == '')){
                width='800';
            }
            if ((height == null) || (height == '')){
                height='600';
            }
            strAttr = "resizable:yes;scroll:yes;status:no;dialogWidth:" + width + "px;dialogHeight:" + height + "px;";
            result = window.showModalDialog(link, null , strAttr);
            return false;
        }

        function onSubmit(close) {
            if (mainForm.name.value == '')
                mainForm.name.value = ' ';
            if (close != null)
                mainForm.back.value = "true";
            mainForm.submit();
        }
        
        // Enable or disable text fields and buttons
        function disableControls() {
            mainForm.name.readOnly = true;
            mainForm.saveButton.disabled = true;
            mainForm.saveAndCloseButton.disabled = true;
            mainForm.resetButton.disabled = true;
            saveButton.disabled = true;
            saveAndCloseButton.disabled = true;
            resetButton.disabled = true;
        }
    </script>
    <xsl:call-template name="adminhead"/>
</head>
    <body style="behavior: url(/script/webservice.htc) url(/script/clientmanager.htc)"
        id="ClientManager">
        <xsl:attribute name="onload">
            <xsl:if test="negeso:photo/@canContribute != 'true'">
                <script>disableControls();</script>
            </xsl:if>
        </xsl:attribute>

        <xsl:call-template name="NegesoBody">
            <xsl:with-param name="helpLink">
                <xsl:text>/admin/help/cph1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
            </xsl:with-param>
            <xsl:with-param name="backLink" select="concat('?command=get-photo-album-command&amp;id=', negeso:photo/@parent_id)"/>
        </xsl:call-template>

        <!-- Update/reset fields -->
        <xsl:call-template name="buttons"/>

        <script>
            Cufon.now();
            Cufon.replace('.admNavPanelFont', { fontFamily: 'Orator Std' });
            Cufon.replace('.admTDtitles', { fontFamily: 'Orator Std' });
        </script>
    </body>
</html>
</xsl:template>

<xsl:template match="negeso:photo" mode="admContent">
    <form method="GET" name="mainForm" enctype="multipart/form-data" action="">
        <input type="hidden" name="command" value="update-photo-description-command"/>
        <input type="hidden" name="id" value="{@id}"/>
        <input type="hidden" name="albumId" value="{@parent_id}"/>
        <input type="hidden" name="back" value="false"/>

        <table cellspacing="0" cellpadding="0" width="100%" class="admTable">
            <tr>
                <td align="center" class="admNavPanelFont" colspan="2">
                    <xsl:call-template name="tableTitle">
                        <xsl:with-param name="headtext">
                            <xsl:value-of select="@name"/>
                        </xsl:with-param>
                    </xsl:call-template>                    
                </td>
            </tr>
            <!-- Name Field -->
            <tr>                
                <th class="admTableTD admWidth150">
                    <xsl:value-of select="java:getString($dict_common, 'NAME')"/>
                </th>
                <td class="admTableTDLast">
                    <input class="admTextArea admWidth95perc" type="text" name="name">
                        <xsl:attribute name="value"><xsl:value-of select="@name"/></xsl:attribute>
                    </input>
                </td>
            </tr>        
            <tr>
                <td class="admTableTDLast admCenter admPadLeft5" colspan="2">
                    <xsl:choose>
                        <xsl:when test="@img_width > 700 or @img_height > 700">
                            <img hspace="5" vspace="5" class="admBorder admHand" src="{@src}" onClick="openImage('{@src}', {@img_width}, {@img_height})">
                                <xsl:choose>
                                    <xsl:when test="@img_width > @img_height">
                                        <xsl:attribute name="width">700</xsl:attribute>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:attribute name="height">700</xsl:attribute>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </img>
                        </xsl:when>
                        <xsl:otherwise>
                            <img hspace="5" vspace="5" src="{@src}" class="admBorder"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </td>
            </tr>    
            <!-- Description -->
            <tr>
                <th class="admTableTD admWidth150">
                    <xsl:value-of select="java:getString($dict_common, 'DESCRIPTION')"/>
                </th>
                <td class="admTableTDLast admLeft admZero">
                    <xsl:apply-templates select="negeso:article">
                        <xsl:with-param name="classType" select="'shortNews'"/>
                    </xsl:apply-templates>
                </td>
            </tr>
            <tr>
                <td class="admTableFooter">&#160;</td>
            </tr>
        </table>
    </form>    
</xsl:template>

<xsl:template match="negeso:article" >
    <xsl:param name="classType"/>
    <xsl:if test="/negeso:photo/@canContribute = 'true'">
        <img src="/images/mark_1.gif" onclick="edit_text('article_text{@id}', 'shortNews', 595);" class="admBorder admHand" style="margin-left: 5px;">
           <xsl:attribute name="alt"><xsl:value-of select="java:getString($dict_common, 'EDIT')"/></xsl:attribute>
        </img>
    </xsl:if>
    <div id="article_text{@id}" class="contentStyle">
        <xsl:if test="$classType = 'shortNews'">
            <xsl:attribute name="class">shortNews</xsl:attribute>
        </xsl:if>
        <xsl:attribute name="style">behavior:url(/script/article3.htc); margin: 5px;border: 1px solid #848484;</xsl:attribute>
        <xsl:value-of select="@text" disable-output-escaping="yes"/>
    </div>
    <script>makeReadonly(document.getElementById('article_text<xsl:value-of select="@id" />'), true);</script>
</xsl:template>

<!--******************** Bottom buttons ********************-->
<xsl:template name="buttons">
    <table cellpadding="0" cellspacing="0"  width="764px" align="center" class="Buttons">
        <tr>
            <td>
                <div class="admBtnGreenb">
                    <div class="imgL"></div>
                    <div >
                        <input name="saveButton" class="admNavbarInp" type="button" onClick="onSubmit()">
                            <xsl:attribute name="value">
                                <xsl:value-of select="java:getString($dict_common, 'SAVE')"/>
                            </xsl:attribute>
                        </input>
                    </div>
                    <div class="imgR"></div>
                </div>
                
                <div class="admBtnGreenb">
                    <div class="imgL"></div>
                    <div >
                        <input name="saveAndCloseButton" class="admNavbarInp" type="button" onClick="onSubmit('close')">
                            <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'SAVE_AND_CLOSE')"/></xsl:attribute>
                        </input>
                    </div>
                    <div class="imgR"></div>
                </div>
                
                <div class="admBtnGreenb" style="padding-right:20px;float:right;">
                    <div class="imgL"></div>
                    <div>
                        <input name="resetButton" class="admNavbarInp" type="button" onClick="mainForm.reset();">
                            <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'RESET')"/></xsl:attribute>
                        </input>
                    </div>
                    <div class="imgR"></div>
                </div>
            </td>
        </tr>
    </table>
</xsl:template>

</xsl:stylesheet>
