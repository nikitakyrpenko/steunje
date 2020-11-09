<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id: v 1.49.1.2, 2005-05-13 18:32:19Z, Stanislav Demchenko$      
 
  Copyright (c) 2004 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  TODO: file description here  

  @author       Stanislav Demchenko
  @version      $Revision: 53$
-->

<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:negeso="http://negeso.com/2003/Framework"
    version="1.0"
	xmlns:java="http://xml.apache.org/xslt/java"
    exclude-result-prefixes="java">

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>        
<xsl:variable name="dict_dialogs" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_dialogs.js',$lang)"/>  
<xsl:variable name="dict_menu_itms" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_menu_item.js',$lang)"/>  
<xsl:variable name="dict_menu_editor" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_menu_editor.xsl',$lang)"/>  


<xsl:template match="/">
    <html>
        <head>
            <title>
            	<xsl:value-of select="java:getString($dict_menu_editor ,'SPECIAL_PAGES_EDITOR')"/>
            </title>
            <link rel="stylesheet" href="/css/admin_style.css" type="text/css"/>
            <link rel="stylesheet" type="text/css" href="/css/jquery-ui.css"/>
            <link rel="stylesheet" type="text/css" href="/css/jquery-ui.custom.css"/>
            <script language="JavaScript" src="/script/jquery.min.js"/>
            <script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
            <script language="JavaScript" src="/script/common_functions.js"/>
            <script language="JavaScript" src="/script/calendar_picker.js"/>
            <script type="text/javascript" src="/script/AJAX_webservice.js"></script>
            <script type="text/javascript" src="/script/conf.js"></script>
            <script type="text/javascript" src="/script/special_pages.js"></script>
            <script type="text/javascript" src="/script/cufon-yui.js"></script>
            <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
       		<script type="text/javascript">
       			var DELETE_CONFIRMATION = '<xsl:value-of select="java:getString($dict_common ,'DELETE_CONFIRMATION')"/>';
                var DELETE_YES = '<xsl:value-of select="java:getString($dict_dialogs ,'YES')"/>';
                var DELETE_NO = '<xsl:value-of select="java:getString($dict_dialogs ,'NO')"/>';
                window.onload = function(){
                Cufon.now();
                Cufon.replace('.admNavPanelFont', { fontFamily: 'Orator Std' });
                Cufon.replace('.admTDtitles', { fontFamily: 'Orator Std' });
                }
            </script>
        </head>
        <body class="menu" id="ClientManager" leftmargin="0" style="padding: 0;">
            
            <table class="admMain"  align="center" border="0" cellpadding="0" cellspacing="0">
                <tr>
                    <td class="admConnerLeft"></td>
                    <td class="admTopBtn">
                        <xsl:call-template name="NavBar">                            
                            <xsl:with-param name="helpLink">
                                <xsl:text>/admin/help/csp1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
                            </xsl:with-param>                            
                        </xsl:call-template>
                        <div class="admBtnGreen">
		                    <div class="imgL"></div>
			                    <div>   
			                        <a class="admBtnText" focus="blur()"  unselectable="on" onclick="addPage()" href="#">
			                            <xsl:value-of select="java:getString($dict_menu_editor ,'ADD_PAGE')"/>
			                        </a>
			                    </div>
		                    <div class="imgR"></div>
                		</div>
                        
                    </td>
                    <td class="admConnerRight"></td>
                </tr>
                <tr>
                    <td class="admMainLeft">
                        <img src="/images/left_bot.png" />
                    </td>
                    <td>
                        <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
					        <tr>
					            <td align="center" class="admNavPanelFont" colspan="3">
					                 <xsl:value-of select="java:getString($dict_menu_editor ,'SPECIAL_PAGES')"/>
					            </td>
					        </tr>
					        <!-- BEGIN CONTENT -->
					        <tr>
					            <td colspan="3" id="PopupEditor">
					                <xsl:apply-templates select="/negeso:pages/negeso:unlinkedPages"/>
					            </td>
					        </tr>
					        <!-- END CONTENT -->
					        <tr>
					            <td class="admTableFooter" colspan="3">&#160;</td>
					        </tr>
					    </table>
                    </td>
                    <td class="admMaiRight">
                        <img src="/images/right_bot.png" />
                    </td>
                </tr>
            </table>
            <xsl:call-template name="buttons"/>
            <br/>
        </body>
    </html>
</xsl:template>





<xsl:template match="negeso:unlinkedPages">
    <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
        <tr>
            <td class="admTDtitles" colspan="3">
                 <xsl:value-of select="java:getString($dict_menu_editor ,'POPUPS')"/>
            </td>
        </tr>
        <xsl:apply-templates select="negeso:page[@category='popup']">
            <xsl:sort select="@title"/>
        </xsl:apply-templates>
        <tr>
            <td class="admTDtitles" colspan="3">
            	<xsl:value-of select="java:getString($dict_menu_editor ,'UNLINKED_PAGES')"/>
            </td>
        </tr>
        <tr>
            <td colspan="3">
                <xsl:apply-templates select="negeso:page[@category='page']">
                    <xsl:sort select="@title"/>
                </xsl:apply-templates>
            </td>
        </tr>
        <tr>
            <td class="admTDtitles" colspan="3">
            	<xsl:value-of select="java:getString($dict_menu_editor ,'SPECIAL_PAGES')"/>
            </td>
        </tr>
        <tr>
            <td colspan="3">
                <xsl:apply-templates select="negeso:page[@category!='popup' and @category!='page']">
                    <xsl:sort select="@title"/>
                </xsl:apply-templates>
            </td>
        </tr>
    </table>
    <!-- RENDER "ADD PAGE HERE" -->    
</xsl:template>


<xsl:template match="negeso:page">
    <!-- RENDER MENU ITEM -->
    <tr pageId="{@id}">
        <td class="admTableTDLast admCenter" style="padding-left:20px;">
            <img src="/images/media_catalog/docFileClear.png" width="32" height="32"/>
        </td>
        <!-- MENU ITEM TEXT-->
        <td class="admTableTD" width="100%" style="padding-left:10px;">
            <div  unselectable="on" style="width: 100%; padding: 0 3 0 0;cursor: default;">
            	<xsl:value-of select="@title"/>
            </div>
        </td>
            
            <!-- ICONS -->
            <xsl:variable name="canManage" select="/negeso:pages/@role-id = 'administrator' or /negeso:pages/@role-id = 'manager'"/>
            <xsl:variable name="canEdit" select="$canManage or /negeso:pages/@role-id = 'editor' or /negeso:pages/@role-id = 'author'"/>
            <xsl:variable name="canView" select="$canEdit or /negeso:pages/@role-id = 'visitor' or /negeso:pages/@role-id = 'guest'"/>
            <xsl:variable name="isSpecialPage" select="@category!='popup' and @category!='page'"/>
            <xsl:variable name="isPopup" select="@category ='popup'"/>
            <td class="admTableTDLast">
                <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td><!-- ICON "RENAME" -->
                            
                        </td>
                        <td><!-- ICON "EDIT PAGE" -->
                            <xsl:if test="$canView">
                                <xsl:variable name="alt"><xsl:value-of select="java:getString($dict_common ,'EDIT')"/></xsl:variable>
                                <img src="/images/edit.png" width="37" height="36" onclick="goToPage('{@filename}',{$isPopup})" class="admHand" alt="{$alt}"/>
                            </xsl:if>
                        </td>
                        <xsl:if test="not($isSpecialPage)">
                            <xsl:if test="$canEdit">
                                <td><!-- ICON "PAGE PROPERTIES" -->
                                    <xsl:variable name="alt"><xsl:value-of select="java:getString($dict_menu_editor ,'CHANGE_PAGE_PROPERTIES')"/></xsl:variable>                                    
                                    <img src="/images/properties.png" width="37" height="36" class="admHand" alt="{$alt}" onclick="editPageProperties({@id})"/>
                                </td>
                            </xsl:if>
                            <!--xsl:if test="$canManage"-->
                                <td><!-- ICON "DELETE" -->
                                    <xsl:choose>
	                                    <xsl:when test="@protected = 'nodelete'">
	                                    	<xsl:variable name="alt"><xsl:value-of select="java:getString($dict_menu_itms ,'NO_DELETE_PROTECTED_PAGE')"/></xsl:variable>
	                                    	<img src="/images/delete_gray.png" width="37" height="36" class="icon" alt="{$alt}" title="{$alt}"/>
	                                    </xsl:when>
	                                    <xsl:otherwise>
		                                    <xsl:variable name="alt"><xsl:value-of select="java:getString($dict_menu_itms ,'DELETE_MENU_ITEM')"/></xsl:variable>
		                                    <img src="/images/delete.png" width="37" height="36" onclick="deletePageById({@id},'{@title}')" class="admHand" alt="{$alt}" title="{$alt}"/>
		                                </xsl:otherwise>
	                                </xsl:choose>
                                </td>
                            </xsl:if>
                        <!--/xsl:if-->
                    </tr>
                </table>
            </td>
            
        </tr>
</xsl:template>

<xsl:template name="buttons">
    <table  cellpadding="0" cellspacing="0"  width="764px"  align="center"  border="0" class="Buttons">
        <tr >
            <td>     
                <div class="admBtnGreenb">
                    <div class="imgL"></div>
                    <div>   
                        <a class="admBtnText" focus="blur()"  unselectable="on" onclick="addPage()" href="#">
                            <xsl:value-of select="java:getString($dict_menu_editor ,'ADD_PAGE')"/>
                        </a>
                    </div>
                    <div class="imgR"></div>
                </div>
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
                        <xsl:if test="$helpLink!=''">
                            <xsl:attribute name="href">#</xsl:attribute>
                            <xsl:attribute name="class">admBtnText callHelp</xsl:attribute>
                        </xsl:if>
                        <xsl:if test="$helpLink=''">
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
    <xsl:param name="backId" select="'backLinkTop'" />
    <xsl:param name="helpLink" select="''"/>
    <div>
        <img src="/images/logo.png" />
    </div>
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
                     <xsl:value-of select="java:getString($dict_common ,'BACK')"/>
                </a>

            </div>
            <div class="imgR"></div>
        </div>
    </xsl:if>
    <div class="admBtnGreen">
        <div class="imgL"></div>
        <div>
            <a href="javascript:window.close()" onclick="return window.close()" class="admBtnText" onfocus="blur()">
                <xsl:value-of select="java:getString($dict_common ,'CLOSE')"/>
            </a>
        </div>
        <div class="imgR"></div>
    </div>
    <xsl:call-template name="NegesoHeader">
        <xsl:with-param name="helpLink" select="$helpLink"/>
    </xsl:call-template>
</xsl:template>


<xsl:template match="*">
    <xsl:apply-templates select="*"/>
</xsl:template>


</xsl:stylesheet>