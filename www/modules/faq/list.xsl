<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${list.xsl}       
 
  Copyright (c) 2003 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  @version      2004.01.05
  @author       Olexiy.Strashko

  Tree list level two support
  @version      2004.04.01
  @author       Sergiy Oliynyk
  @author       Volodymyr Snigur
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<xsl:include href="/xsl/negeso_body.xsl"/>

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_faq" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_faq.xsl', $lang)"/>

<xsl:template match="/">
<html>
<head>
    <title><xsl:value-of select="java:getString($dict_faq, 'MANAGE_FAQ')"/></title>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>    
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>

    <script type="text/javascript" src="/script/jquery.min.js"></script>
    <script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
    <script type="text/javascript" src="/script/common_functions.js"></script>
    
    <script language="JavaScript">
        var s_DeleteConfirmation = "<xsl:value-of select="java:getString($dict_common, 'DELETE_CONFIRMATION')"/>";

        <![CDATA[
        function AddListItem(form) {
            form.command.value = "create-list-item-command";
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
    </script>
</head>
<body>
    <xsl:call-template name="NegesoBody">
        <xsl:with-param name="backLink" select="'?command=get-categories-command&amp;moduleName=faq'"/>
        <xsl:with-param name="helpLink">
            <xsl:text>/admin/help/cfq1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
        </xsl:with-param>
    </xsl:call-template>
</body>
</html>
</xsl:template>

<!-- ************************************* List *********************************** -->
<xsl:template match="negeso:list" mode="admContent">
    <xsl:apply-templates select="."/>
</xsl:template>
  
<xsl:template match="negeso:list">
    <form method="POST" name="listManageForm{@id}">
        <xsl:call-template name="NegesoForm"/>
        <table align="center" border="0" cellpadding="0" cellspacing="0" class="admTable">
            <xsl:if test="count(negeso:listItem/negeso:list) != 0">
                <xsl:attribute name="style">background:none;</xsl:attribute>
            </xsl:if>
            <tr>
                <td colspan="6" align="center" class="admNavPanelFont">                    
                    <xsl:call-template name="tableTitle">
                        <xsl:with-param name="headtext">
                            <xsl:choose>
                                <xsl:when test="count(descendant::negeso:list) != 1">
                                    <xsl:value-of select="@name"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="java:getString($dict_faq, 'MANAGE_FAQ')"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:with-param>
                    </xsl:call-template>
                </td>
            </tr>
            <tr>
                <td colspan="6">
                    <div class="admNavPanelInp" style="margin:5px 0;">
                        <div class="imgL"></div>
                        <div>
                            <xsl:choose>
                                <xsl:when test="count(descendant::negeso:list) = 1">
                                    <input class="admNavPanelInp" type="button">
                                        <xsl:attribute name="value">
                                            <xsl:value-of select="java:getString($dict_common, 'ADD_NEW_CATEGORY')"/>
                                        </xsl:attribute>
                                        <xsl:attribute name="onClick">AddListItem(this.form)</xsl:attribute>
                                    </input>
                                </xsl:when>
                                <xsl:otherwise>
                                    <input class="admNavPanelInp" type="button">
                                        <xsl:attribute name="value">
                                            <xsl:value-of select="java:getString($dict_faq, 'ADD_NEW_QUESTION')"/>
                                        </xsl:attribute>
                                        <xsl:attribute name="onClick">AddListItem(this.form)</xsl:attribute>
                                    </input>
                                </xsl:otherwise>
                            </xsl:choose>
                        </div>
                        <div class="imgR"></div>
                    </div>
                </td>
            </tr>
            <tr>
                <td class="admTDtitles" align="center" style="padding-left:5px;">
                    <xsl:value-of select="java:getString($dict_common, 'ORDER')"/>
                </td>
                <td class="admTDtitles" align="center">
                    <xsl:value-of select="java:getString($dict_common, 'TITLE')"/>
                </td>
                <td class="admTDtitles" colspan="4">&#160;</td>
            </tr>
            <xsl:apply-templates select="negeso:listItem"/>
            <xsl:if test="count(negeso:listItem/negeso:list) = 0">
                <tr>
                    <td class="admTableFooter" colspan="6">&#160;</td>
                </tr>
            </xsl:if>
        </table>
    </form>
    <xsl:apply-templates select="negeso:listItem/negeso:list"/>
</xsl:template>

<!-- ************************************* ListItem ******************************* -->
<xsl:template match="negeso:listItem" >    
    <tr>
        <td class="admTableTD admWidth30" align="center"><xsl:value-of select="@orderNumber"/></td>
        <th class="admTableTD">
            <a class="admAnchor" id="l{@id}">
                <xsl:if test="../@selected = @id">
                    <xsl:attribute name="class">admBlue admHand admBold</xsl:attribute>
                    <xsl:attribute name="style">text-decoration: underline;</xsl:attribute>
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
    <input  type="hidden" name="command" value="get-list-command"></input>
    <input  type="hidden" name="listItemId" value="-1"></input>
    <input  type="hidden" name="action"></input>
    <input  type="hidden" name="listId">
        <xsl:attribute name="value"><xsl:value-of select="@id"/></xsl:attribute>
   </input>
    <input  type="hidden" name="listPath">
        <xsl:attribute name="value"><xsl:value-of select="/negeso:list/@listPath"/></xsl:attribute>
   </input>
   <input type="hidden" name="listLevel">
        <xsl:attribute name="value"><xsl:value-of select="@level"/></xsl:attribute>
   </input>
    <input type="hidden" name="rootListId">
        <xsl:attribute name="value"><xsl:value-of select="/negeso:list/@id"/></xsl:attribute>
    </input>
    <input type="hidden" name="summary" value="true"/>
    <input type="hidden" name="details" value="false"/>
</xsl:template>

</xsl:stylesheet>
