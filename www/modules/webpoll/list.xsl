<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${list.xsl}       
 
  Copyright (c) 2003 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.

  @author       Sergiy Oliynyk
  @author	    Volodymyr Snigur
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<xsl:include href="/xsl/negeso_body.xsl"/>

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_webpoll" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_webpoll.xsl', $lang)"/>
<xsl:variable name="dict_modules" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_modules.xsl', $lang)"/>

<xsl:template match="/">
<html>
    <head>
        <title><xsl:value-of select="java:getString($dict_modules, 'WEB_POLL')"/></title>
        <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
        <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>

        <script type="text/javascript" src="/script/jquery.min.js"></script>
        <script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
        <script type="text/javascript" src="/script/cufon-yui.js"></script>
        <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
        <script language="JavaScript" src="/script/common_functions.js" type="text/javascript">/**/</script>
        <script language="JavaScript">
            var s_DeleteConfirmation = "<xsl:value-of select="java:getString($dict_common, 'DELETE_CONFIRMATION')"/>";
            
            <xsl:text disable-output-escaping="yes">
            <![CDATA[
                function AddListItem(form) {
                    if (form.listLevel.value == "0") {                
                        form.command.value = "create-poll-command";
                        form.summary.value = "true";
                    }
                    else
                        form.command.value = "create-poll-option-command";
                    form.submit();
                }

                function DeleteListItem(id, form) {
                    if (confirm(s_DeleteConfirmation)) {
                        form.command.value = "delete-list-item-command";
                        if (form.listLevel.value == "0")
                            form.listPath.value = "0";
                        form.listItemId.value = id;
                        form.submit();
                    }
                }

                function MoveUpListItem(id, form) {
                    form.command.value = "change-order-command";
                    form.listItemId.value = id;
                    form.action.value = "up";
                    form.submit();
                }

                function MoveDownListItem(id, form) {
                    form.command.value = "change-order-command";
                    form.listItemId.value = id;
                    form.action.value = "down";
                    form.submit();
                }

                function EditListItem(id, form) {
                    form.command.value = "get-list-item-command";
                    form.listItemId.value = id;
                    form.submit();
                }

                function SelectListItem(id, form) {
                    form.command.value = "set-list-path-command";
                    form.listItemId.value = id;
                    form.submit();
                }
            ]]>
            </xsl:text>
        </script>
    </head>
    <body>
        <xsl:call-template name="NegesoBody">
            <xsl:with-param name="helpLink" select="'?command=get-categories-command&amp;moduleName=web_poll'"/>
            <xsl:with-param name="backLink" select="''"/>
        </xsl:call-template>        
    </body>
</html>
</xsl:template>

<!-- ************************************* List *********************************** -->
<xsl:template match="negeso:list" mode="admContent">
    <xsl:apply-templates select="."/>
</xsl:template>
    
<xsl:template match="negeso:list">
    <table cellspacing="0" cellpadding="0" border="0" align="center" width="100%">
        <xsl:if test="@level=0">
            <xsl:attribute name="class">admTable</xsl:attribute>
        </xsl:if>
        <tr>
            <td class="admNavPanelFont" align="center">                    
                <xsl:choose>
                    <xsl:when test="@level = 0">
                        <xsl:value-of select="java:getString($dict_webpoll, 'AVAILABLE_POLLS')"/>
                    </xsl:when>
                    <xsl:when test="@level = 1">
                        <xsl:value-of select="java:getString($dict_webpoll, 'OPTIONS')"/>
                    </xsl:when>
                </xsl:choose>
            </td>
        </tr>
        <tr>
            <td style="padding:5px 0;">
                <form method="POST" name="listManageForm{@id}">
                    <xsl:call-template name="NegesoForm"/>
                    <div class="admNavPanelInp">
                        <div class="imgL"></div>
                        <div>
                            <input class="admNavbarInp" type="button" onclick="AddListItem(this.form)">                                    
                                <xsl:attribute name="value">
                                    <xsl:choose>
                                        <xsl:when test="@level = 0">                                            
                                            <xsl:value-of select="java:getString($dict_webpoll, 'ADD_NEW_POLL')"/>
                                        </xsl:when>
                                        <xsl:when test="@level = 1">
                                            <xsl:value-of select="java:getString($dict_webpoll, 'ADD_NEW_OPTION')"/>
                                        </xsl:when>
                                    </xsl:choose>
                                </xsl:attribute>
                            </input>
                        </div>
                        <div class="imgR"></div>
                    </div>
                </form>
            </td>
        </tr>
        <tr>
            <td>
                <table cellpadding="0" cellspacing="0" width="100%">
                    <tr>
                        <td class="admTDtitles admOrder">
                            <xsl:value-of select="java:getString($dict_common, 'ORDER')"/>
                        </td>
                        <td class="admTDtitles">
                            <xsl:value-of select="java:getString($dict_common, 'IMAGE')"/>
                        </td>
                        <td class="admTDtitles">
                            <xsl:value-of select="java:getString($dict_common, 'TITLE')"/>
                        </td>
                        <xsl:if test="@level=0">
                            <td class="admTDtitles admAction">
                                <xsl:value-of select="java:getString($dict_common, 'PUBLISH_DATE')"/>
                            </td>
                            <td class="admTDtitles admAction">
                                <xsl:value-of select="java:getString($dict_common, 'EXPIRED_DATE')"/>
                            </td>
                        </xsl:if>
                        <td class="admTDtitles" colspan="4">
                            <xsl:value-of select="java:getString($dict_common, 'ACTION')"/>
                        </td>
                    </tr>
                    <xsl:apply-templates select="negeso:listItem"/>
                </table>                
                <xsl:apply-templates select="negeso:listItem/negeso:list"/>                
            </td>
        </tr>
        <xsl:if test="@level = 0">
            <tr>
                <td class="admTableFooter" >&#160;</td>
            </tr>
        </xsl:if>
    </table>    
</xsl:template>


<!-- ************************************* ListItem ******************************* -->

<xsl:template match="negeso:listItem">
    <tr>
        <th class="admTableTD"><xsl:value-of select="@orderNumber"/></th>
        <td class="admTableTD" width="64px">
           <xsl:choose>
               <xsl:when test="@thumbnailLink">
                   <img id="l{@id}" src="{@thumbnailLink}" width="64">
                       <xsl:if test="count(../descendant::negeso:list) = 0">
                           <xsl:attribute name="class">admBorder admHand</xsl:attribute>
                           <xsl:attribute name="onClick">EditListItem(<xsl:value-of select="@id"/>,listManageForm<xsl:value-of select="../@id"/>)</xsl:attribute>
                       </xsl:if>
                   </img>
               </xsl:when>
               <xsl:when test="@imageLink">
                   <img src="{@imageLink}" width="64"/>
                </xsl:when>
           </xsl:choose>
        </td>
        <th class="admTableTD">
            <a class="admAnchor" id="l{@id}">
               <xsl:if test="../@selected = @id">
                   <xsl:attribute name="class">admHand admBold</xsl:attribute>
                   <xsl:attribute name="style">text-decoration: underline; color:#2483b7;</xsl:attribute>
               </xsl:if>
                <xsl:choose>
                    <xsl:when test="count(../descendant::negeso:list) = 0">
                        <xsl:attribute name="onClick">EditListItem(<xsl:value-of select="@id"/>,listManageForm<xsl:value-of select="../@id"/>)</xsl:attribute>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:attribute name="onClick">SelectListItem(<xsl:value-of select="@id"/>,listManageForm<xsl:value-of select="../@id"/>)</xsl:attribute>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:value-of select="@title" disable-output-escaping="yes"/>
            </a>
        </th>
        <xsl:if test="../@level = 0"> 
            <td class="admTableTD"><xsl:value-of select="@publishDate"/></td>
            <td class="admTableTD"><xsl:value-of select="@expiredDate"/></td>
        </xsl:if>   

        <td class="admTableTDLast admWidth30">
            <img class="admHand" src="/images/edit.png" onClick="EditListItem({@id},listManageForm{../@id});">
                <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'EDIT')"/></xsl:attribute>
            </img>
        </td>
        <td class="admTableTDLast admWidth30">
            <img class="admHand" src="/images/up.png" onClick="MoveUpListItem({@id},listManageForm{../@id});">
                <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'UP')"/></xsl:attribute>
            </img>
        </td>
        <td class="admTableTDLast admWidth30">
            <img class="admHand" src="/images/down.png" onClick="MoveDownListItem({@id},listManageForm{../@id});">
                <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'DOWN')"/></xsl:attribute>
            </img>
        </td>
        <td class="admTableTDLast admWidth30">
            <img class="admHand" src="/images/delete.png" onClick="DeleteListItem({@id},listManageForm{../@id});">
                <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'DELETE')"/></xsl:attribute>
            </img>
        </td>
    </tr>
</xsl:template>

<!-- *************************************NegesoForm******************************* -->
<xsl:template name="NegesoForm">
    <xsl:attribute name="action">?command</xsl:attribute>
    <input type="hidden" name="command" value="get-list-command"></input>
    <input type="hidden" name="listItemId" value="-1"></input>
    <input type="hidden" name="action"></input>
    <input type="hidden" name="listId">
        <xsl:attribute name="value"><xsl:value-of select="@id"/></xsl:attribute>
    </input>
    <input type="hidden" name="listPath">
        <xsl:attribute name="value"><xsl:value-of select="/negeso:list/@listPath"/></xsl:attribute>
    </input>
    <input type="hidden" name="listLevel">
        <xsl:attribute name="value"><xsl:value-of select="@level"/></xsl:attribute>
    </input>
    <input type="hidden" name="rootListId">
        <xsl:attribute name="value"><xsl:value-of select="/negeso:list/@id"/></xsl:attribute>
    </input>
    <input type="hidden" name="summary" value="false"/>
    <input type="hidden" name="details" value="false"/>
</xsl:template>

</xsl:stylesheet>
