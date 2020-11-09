<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${list.xsl}       
 
  Copyright (c) 2003 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  View of a list item edit interface.

  @version      2011.04.01
  @author       E.Dzhentemirova
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<xsl:include href="/xsl/negeso_body.xsl"/>
<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_menu" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_menu_editor.xsl', $lang)"/>

<xsl:template match="/">
    <html>
    <head>
        <title>
            <xsl:value-of select="java:getString($dict_menu, 'MENU_STRUCTURE')"/>
        </title>
        <link rel="stylesheet" href="/css/admin_style.css" type="text/css"/>    
        <link rel="stylesheet" type="text/css" href="/css/jquery-ui.css"/>        
        <link rel="stylesheet" type="text/css" href="/css/jquery-ui.custom.css"/>        
        
        <script language="JavaScript" src="/script/jquery.min.js"/>
        <script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
        <script type="text/javascript" src="/script/cufon-yui.js"></script>
        <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
        <script language="JavaScript" src="/script/common_functions.js"/>
        <script language="JavaScript" src="/script/calendar_picker.js"/>
        <script type="text/javascript" src="/script/conf.js">/**/</script>        
        <script type="text/javascript" src="/dictionaries/dict_menu_item_{/*/@interface-language}.js">/**/</script>
        <script type="text/javascript" src="/site/core/script/validation.js"></script>	    
        <script type="text/javascript" src="/script/menu_item.js"></script>
        
        <script>
            var maxDepth = parseInt(<xsl:value-of select="/negeso:main_menu/@menu_max_depth"/>);

            var topItemsLimit = parseInt(<xsl:value-of select="/negeso:main_menu/@top_items_limit"/>);
            var _moveTo = "<xsl:value-of select="java:getString($dict_menu, 'MOVE_TO')"/>";
            var _removeTotaly = "<xsl:value-of select="java:getString($dict_menu, 'REMOVE_TOTALY')"/>";
            var _canMoveTo = "<xsl:value-of select="java:getString($dict_menu, 'CAN_MOVE_TO')"/>";
            var s_SubmenuCannotBeDeeperThan = "<xsl:value-of select="java:getString($dict_menu, 'SUBMENU_CANNOT_BE_DEEPER_THAN')"/>";
            var s_MaximumTopItemsLimited = "<xsl:value-of select="java:getString($dict_menu, 'MAXIMUM_TOP_ITEM_LIMITED')"/>";            
            var s_Levels= "<xsl:value-of select="java:getString($dict_menu, 'LEVELS')"/>";            
	                       
            
            
        </script>

    </head>
    <body style="behavior: url(/script/webservice.htc) url(/script/clientmanager.htc)" id="ClientManager">
        <!--<xsl:call-template name="iter"/>-->
        <xsl:call-template name="NegesoBody">
            <xsl:with-param name="helpLink" select="'/admin/help/index.html'"/>
            <xsl:with-param name="backLink" select="'false'"/>
                    </xsl:call-template>
        <xsl:call-template name="buttons"/>
        <xsl:call-template name="removeMenu"/>
        <script>
            Cufon.now();
            Cufon.replace('.admNavPanelFont', { fontFamily: 'Orator Std' });
            Cufon.replace('.admTDtitles', { fontFamily: 'Orator Std' });
        </script>
    </body>
    </html>
</xsl:template>
    
<xsl:template name="iter">
	<xsl:for-each select="./*">
		<b><xsl:value-of select="name()" /></b><br/>
		<xsl:for-each select="./attribute::*">
			<xsl:value-of select="name()" /> = <xsl:value-of select="." /><br/>
		</xsl:for-each><br/>
		<i>Text: </i><xsl:value-of select="text()" />
		<br/>
		<div style="margin-left: 30px">
			<xsl:call-template name="iter"/>
		</div>
	</xsl:for-each>
</xsl:template>    
    
<xsl:template match="negeso:menu" mode="admContent">
    <!-- NEGESO HEADER -->    
    <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
        <tr>
            <td align="center" class="admNavPanelFont" colspan="4">
                <xsl:value-of select="java:getString($dict_menu, 'MENU_STRUCTURE')"/>
            </td>
        </tr>
        <!-- BEGIN CONTENT -->
        <tr>
            <td class="admTDtitles admTDtitlesCenter" width="314">
                <xsl:value-of select="java:getString($dict_common, 'TITLE')"/>
            </td>
            <td class="admTDtitles admTDtitlesCenter" width="160" nowrap="nowrap">
                <xsl:value-of select="java:getString($dict_common, 'PUBLISH_DATE')"/>
            </td>
            <td class="admTDtitles admTDtitlesCenter" width="160" nowrap="nowrap">
                <xsl:value-of select="java:getString($dict_common, 'EXPIRED_DATE')"/>                
            </td>
            <td class="admTDtitles admTDtitlesCenter" width="130">
                <xsl:value-of select="java:getString($dict_menu, 'CONTAINER')"/>
            </td>
        </tr>
        <tr>
            <td colspan="4">
                <xsl:apply-templates select="/negeso:main_menu/negeso:menu"/>
            </td>
        </tr>
        <!-- END CONTENT -->
        <tr>
            <td class="admTableFooter" colspan="4">&#160;</td>
        </tr>
    </table>            
</xsl:template>    
    
<xsl:template match="negeso:menu">
    <!-- Initial left margin -->
    <xsl:param name="padding" select="'0'"/>
    <xsl:apply-templates select="negeso:menu_item" mode="li">        
        <xsl:with-param name="padding" select="$padding"/>
    </xsl:apply-templates>
</xsl:template>

<xsl:template match="negeso:menu_item">
    <xsl:param name="padding" select="'0'"/>
    <xsl:apply-templates select="negeso:menu_item" mode="li">        
        <xsl:with-param name="padding" select="$padding"/>
    </xsl:apply-templates>
</xsl:template>    
    
<xsl:template match="negeso:menu_item" mode="li">
    <xsl:param name="padding" select="'0'"/>    
    <xsl:variable name="font-weight">
        <xsl:if test="count(ancestor::negeso:menu_item) = 0">bold</xsl:if>
        <xsl:if test="count(ancestor::negeso:menu_item) != 0">normal</xsl:if>
    </xsl:variable>
    <div parentId="{@parentId}" style="display:none;" sort="true">
        <xsl:if test="$padding=0">
            <xsl:attribute name="style">display:block;</xsl:attribute>
        </xsl:if>
    <!-- RENDER MENU ITEM -->
    <table border="0" cellpadding="0" cellspacing="0" class="admMenu" 
               onmouseover="showPanel('{@id}','true')" onmouseout ="showPanel('{@id}','false')" menuId="{@id}">
        <tr>
                <td width="*" style="padding-left: {$padding};" class="admTableTD" isExpand="false">
                <!-- SIGN '+' OR '-' TO COLLAPSE OR EXPAND SUBMENU -->
                <xsl:choose>
                        <xsl:when test="count(negeso:menu_item)">
                            <img src="/images/media_catalog/folderClear.png" width="32" height="32" onclick="expandOrCollapse(this,{@id})" class="admFolder"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <img src="/images/media_catalog/webFileClear.png" width="32" height="32" class="admFile"/>
                    </xsl:otherwise>
                </xsl:choose>
                <!-- BEGIN MENU ITEM TEXT-->
                <xsl:variable name="cursor">
                        <xsl:if test="count(negeso:menu_item) = 0">default</xsl:if>
                        <xsl:if test="count(negeso:menu_item) != 0">hand</xsl:if>
                </xsl:variable>
                    <div unselectable="on" 
                        style="cursor: {$cursor};
                           font-weight: {$font-weight};
                     	     padding:5px 0 10px 5px;
                               float:left;position:relative;"
                       >
                        <span titleMenu="true" onclick="expandOrCollapse(this,{@id})" menu_item_id="{@id}" id="mi_div{@id}">
                            <xsl:value-of select="@title" disable-output-escaping="yes"/>
                        </span>
                <xsl:apply-templates select="." mode="icons_panel_for_menu_item"/>
                    </div>
            </td>
            <!-- END MENU ITEM TEXT-->
            <!-- WHITE SPACE BETWEEN MENU ITEM AND ITS ICONS -->
            <td id="publish_{@id}" width="160" class="admTableTD admCenter">
                <xsl:value-of select="@publishDate"/>
            </td>
            <td id="expired_{@id}" width="155" class="admTableTD admCenter">
                <xsl:value-of select="@expiredDate"/>                
            </td>
                <td id="container_{@id}" width="118" class="admTableTDLast admCenter">
                <xsl:variable name="cid" select="@container_id"/>
                <xsl:value-of select="//negeso:container[@id=$cid]/@name"/>               
            </td>
        </tr>
    </table>
    <!-- INVOKE SUBMENU -->
        <xsl:apply-templates select="self::negeso:menu_item">
            <!-- Increment of left margin -->
            <xsl:with-param name="padding" select="$padding + 30"/>
        </xsl:apply-templates>
    </div>
    
    <!-- RENDER "ADD PAGE HERE" -->
    

        
   
    <xsl:if test="position() = last()">
            <div parentId="{@parentId}" style="display:none;">
                <xsl:if test="$padding=0">
                    <xsl:attribute name="style">display:block;</xsl:attribute>
                </xsl:if>
        <table border="0" cellpadding="0" cellspacing="0" class="admMenu">
            <tr height="28">
                <td style="padding-left: {$padding};" class="admTableTDLast">
                    <img src="/images/media_catalog/webFileClear.png" width="32" height="32" class="admFile"/>
                    <div unselectable="on"
                                onclick="javascript:tryToCreateMenu({@parentId},false)"
                        style="                            
                            cursor: hand;
                            color: red;
                            font-weight: {$font-weight};
                            padding:5px 0 0 5px;
                            float:left;"
                    >
                                <xsl:value-of select="java:getString($dict_menu, 'ADD_PAGE_HERE')"/>
                    </div>
                </td>                
            </tr>
        </table>
            </div>
    </xsl:if>
    


</xsl:template>    
    
<!-- ICONS FOR MENU ITEM -->
<xsl:template match="negeso:menu_item" mode="icons_panel_for_menu_item">
    <xsl:variable name="canManage" select="@role-id='administrator' or @role-id='manager'"/>
    <xsl:variable name="canEdit" select="$canManage or @role-id='editor' or @role-id='author'"/>
    <xsl:variable name="canView" select="$canEdit or @role-id='visitor' or @role-id='guest'"/>    
    <div id="icons_panel_{@id}" style="position: absolute; display: none; padding-left: 8px; top:-3px;">
        <table border="0" cellspacing="0" cellpadding="0">
            <tr>
                <xsl:if test="$canEdit and @pageId">
                    <td><!-- ICON "RENAME" -->
                        <xsl:variable name="alt"><xsl:value-of select="java:getString($dict_menu, 'RENAME')"/></xsl:variable>
                        <img src="/images/rename.png" width="37" height="36" onclick="editMenuItem({@id})" class="admHand icon" alt="{$alt}"/>
                    </td>
                </xsl:if>
                <td><!-- ICON "EDIT PAGE/URL" -->
                    <xsl:choose>
                        <xsl:when test="$canView and @pageId">
                            <xsl:variable name="alt"><xsl:value-of select="java:getString($dict_common, 'EDIT')"/></xsl:variable>
                            <img src="/images/edit.png" width="37" height="36" onclick="loadPage('{@href}')" class="admHand icon" alt="{$alt}"/>
                        </xsl:when>
                        <xsl:when test="$canEdit and (@keepMenu ='true')">
                            <xsl:variable name="alt"><xsl:value-of select="java:getString($dict_menu, 'EDIT_LINK_TO_PAGE')"/></xsl:variable>
                            <img src="/images/edit_alias.png" width="37" height="36" onclick="editMenuItem({@id})" class="admHand icon" alt="{$alt}"/>
                        </xsl:when>
                        <xsl:when test="$canEdit and not(@pageId)">
                            <xsl:variable name="alt"><xsl:value-of select="java:getString($dict_menu, 'EDIT_LINK_TO_URL')"/></xsl:variable>
                            <img src="/images/edit_url.png" width="37" height="36" onclick="editMenuItem({@id})" class="admHand icon" alt="{$alt}"/>
                        </xsl:when>
                    </xsl:choose>
                </td>
                <xsl:if test="$canEdit and (@pageId)">
                    <td><!-- ICON "PAGE PROPERTIES" -->
                        <xsl:variable name="alt"><xsl:value-of select="java:getString($dict_menu, 'CHANGE_PAGE_PROPERTIES')"/></xsl:variable>
                        <img src="/images/properties.png" width="37" height="36" onclick="editProperties({@pageId},{@id})" class="admHand icon" alt="{$alt}"/>
                    </td>
                </xsl:if>
                <xsl:if test="$canManage and (count(negeso:menu_item)=0)">
                    <td><!-- ICON "CREATE SUBMENU" -->
                        <xsl:variable name="alt"><xsl:value-of select="java:getString($dict_menu, 'ADD_PAGE_IN_SUBMENU')"/></xsl:variable>
                        <img src="/images/submenu.png" width="37" height="36" onclick="tryToCreateMenu({@id},true)" class="admHand icon" alt="{$alt}"/>
                    </td>
                </xsl:if>
                <td class="moveUp">
                    <xsl:if test="$canEdit and not(preceding-sibling::negeso:menu_item)">
                        <xsl:attribute name="style">display:none</xsl:attribute>
                </xsl:if>
                    <!-- ICON "MOVE UP" -->
                    <xsl:variable name="alt"><xsl:value-of select="java:getString($dict_menu, 'MOVE_UP')"/></xsl:variable>
                    <img src="/images/up.png" width="37" height="36" onclick="move({@id},true)" class="admHand icon" alt="{$alt}"/>
                    </td>
                <td class="moveDown">
                    <xsl:if test="$canEdit and not(following-sibling::negeso:menu_item)">
                        <xsl:attribute name="style">display:none</xsl:attribute>
                </xsl:if>
                    <!-- ICON "MOVE DOWN" -->
                    <xsl:variable name="alt"><xsl:value-of select="java:getString($dict_menu, 'MOVE_DOWN')"/></xsl:variable>
                    <img src="/images/down.png" width="37" height="36" onclick="move({@id},false)" class="admHand icon" alt="{$alt}"/>
                </td>                
                <xsl:if test="$canManage">
                    <td><!-- ICON "DELETE" -->
                        <xsl:variable name="alt"><xsl:value-of select="java:getString($dict_menu, 'DELETE_MENU_ITEM')"/></xsl:variable>
                        <img src="/images/delete.png" width="37" height="36" onclick="deleteMenuItem(this,{@id})" pageId="{@pageId}" class="admHand icon" alt="{$alt}"/>
                    </td>
                </xsl:if>
            </tr>
        </table>
    </div>
</xsl:template>

<!-- Returns parameter "date" formatted like "dd-mm-yyyy";
     returns empty string if the parameter is absent -->
<xsl:template name="dd_mm_yyyy">
    <xsl:param name="date"/>
    <xsl:if test="string-length($date) != 0">
        <xsl:value-of select="concat(substring($date, 9, 2), '-' , substring($date, 6, 2), '-', substring($date, 1, 4) )"/>
    </xsl:if>
</xsl:template>

<xsl:template name="buttons">
    <table  cellpadding="0" cellspacing="0"  width="764px"  align="center"  border="0" class="Buttons">
        <tr >
            <td>     
                <div class="admBtnGreenb admBtnBlueb">
                    <div class="imgL"></div>
                    <div>
                        <a class="admBtnText" focus="blur()" onClick="window.open('menu_report.txt');" href="#window.open('menu_report.txt');">
                            <xsl:value-of select="java:getString($dict_menu, 'REPORT')"/>                            
                        </a>
                    </div>
                    <div class="imgR"></div>
                </div>
            </td>
        </tr>
    </table>
</xsl:template>

<xsl:template name="removeMenu">
    <div id="removeMenu" style="display:none;">
        <p class="admCenter" style="margin-bottom:10px;font-size: 13px;" id="warning"></p>                                        
        </div>
</xsl:template>

</xsl:stylesheet>
