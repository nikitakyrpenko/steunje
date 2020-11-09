<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$        
 
  Copyright (c) 2004 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  Select page template form. Shows list of templates with default selected
 
  @author        Stanislav Demchenko
  @version        $Revision$
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">



    <xsl:variable name="lang" select="/negeso:new-page/@interface-language"/>
    <xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
    <xsl:variable name="dict_dialogs" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_dialogs.js',$lang)"/>

    <xsl:template match="/negeso:new-page">
        <html>
            <head>
                <title>Negeso W/CMS</title>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
                <link rel="stylesheet" type="text/css" href="/css/admin_style.css"/>
                <link rel="stylesheet" type="text/css" href="/css/jquery-ui.css"/>
                <link rel="stylesheet" type="text/css" href="/css/jquery-ui.custom.css"/>
                
                <script language="JavaScript" src="/script/jquery.min.js"/>
                <script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
                <script language="JavaScript" src="/script/common_functions.js"/>
                <script language="JavaScript" src="/script/calendar_picker.js"/>
                <script type="text/javascript" src="/script/conf.js"></script>
                <script type="text/javascript" language="JavaScript" src="/script/security.js"></script>
                <script type="text/javascript" language="JavaScript" src="/script/special_pages.js"></script>
                <script type="text/javascript" src="/script/cufon-yui.js"></script>
                <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>

                <script type="text/javascript">
                    
                    <xsl:if test="negeso:closeOnLoad">
                    	window.opener.location.reload();
                    	window.close();
                    </xsl:if>
                    var s_PageNameCannotBeEmpty =  '<xsl:value-of select="java:getString($dict_dialogs, 'PAGE_NAME_CANNOT_BE_EMPTY')"/>';
                    var s_PageNameCannotConsistOfSpacesAlone ='<xsl:value-of select="java:getString($dict_dialogs, 'PAGE_NAME_CANNOT_CONSIST_OF_SPACES_ALONE')"/>';
                    var s_FileNameCannotBeEmpty =  '<xsl:value-of select="java:getString($dict_dialogs, 'FILE_NAME_CANNOT_BE_EMPTY')"/>';
                    var s_FileNameIsTooLong = '<xsl:value-of select="java:getString($dict_dialogs, 'FILE_NAME_IS_TOO_LONG')"/>';
                    var s_MaximumCharacters = ' <xsl:value-of select="java:getString($dict_dialogs, 'MAXIMUM_CHARACTERS')"/>';
                    var s_InvalidFileName = '<xsl:value-of select="java:getString($dict_dialogs, 'INVALID_FILE_NAME')"/>';
                    var s_SpacesAreNotAllowed = '<xsl:value-of select="java:getString($dict_dialogs, 'SPACES_ARE_NOT_ALLOWED')"/>';
                    var spacesAreNotAllowed = '<xsl:value-of select="java:getString($dict_dialogs, 'INVALID_FILE_NAME')"/> _ - :  <xsl:value-of select="java:getString($dict_dialogs, 'SPACES_ARE_NOT_ALLOWED')"/>';

                    var langCode = '<xsl:value-of select="negeso:language/@code"/>';
                    window.onload = function(){
                        Cufon.now();
                        Cufon.replace('.admNavPanelFont', { fontFamily: 'Orator Std' });
                        Cufon.replace('.admTDtitles', { fontFamily: 'Orator Std' });
                    }
                </script>

            </head>
            <body class="dialogSmall">
                <form name="createPage" method="post">
                    <input type="hidden" name="act"  value="create_page"/>
                    <table class="admMain"  align="center" border="0" cellpadding="0" cellspacing="0">
                        <tr>
                            <td class="admConnerLeft"></td>
                            <td class="admTopBtn"></td>
                            <td class="admConnerRight"></td>
                        </tr>
                        <tr>
                            <td class="admMainLeft">
                                <img src="/images/left_bot.png" alt="" />
                            </td>
                            <td>
                                <table align="center" border="0" cellpadding="0" cellspacing="0" class="admTable">
                                    <!-- BEGIN CONTENT -->
                                    <tr>
                                        <td class="admNavPanelFont" align="center" colspan="3">
                                            <xsl:value-of select="java:getString($dict_dialogs,'CREATING_SPECIAL_PAGE')"/>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="admTableTDLast admRight" id="TITLE_LABEL">
                                            <xsl:value-of select="java:getString($dict_dialogs,'TITLE')"/>
                                        </td>
                                        <td class="admTableTDLast">
                                            <input type="text" id="title"  name="title" value="" class="admTextArea"/>
                                        </td>
                                        <td class="admTableTDLast"></td>
                                    </tr>
                                    <tr>
                                        <td class="admTableTDLast admRight" id="FILE_NAME">
                                            <xsl:value-of select="java:getString($dict_dialogs,'FILE_NAME')"/>
                                        </td>
                                        <td class="admTableTDLast" colspan="2">
                                            <input type="hidden" id="filename" name="filename" value="" class="admTextArea"/>
                                            <input type="text" id="filename_s" name="filename_s" value="" class="admTextArea"/>
                                            _<xsl:value-of select="negeso:language/@code"/>.html
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="admTableTDLast admRight" id="PUBLISH_ON">
                                            <xsl:value-of select="java:getString($dict_dialogs,'PUBLISH_ON')"/>
                                        </td>

                                        <td  class="admTableTDLast" colspan="2">
                                            <div style="float:left; padding:5px 0 0 0;">
                                                <input style="width: 80px;" type="text" id="publishDateId" name="publishDate" readonly="true"/>
                                                <span class="admLocation">&#160;(dd-mm-yyyy)</span>
                                            </div>
                                            <div class="admNavPanelInp" style="padding:0; float:left;">
                                                <div class="imgL"></div>
                                                <div align="center">
                                                    <a class="admNavPanelInp" focus="blur()" style="width:83px;padding:10 5 0 5;" href="#" onclick="showPage();return false;" id="buttonShow" onfocus="blur()">
                                                        <xsl:value-of select="java:getString($dict_dialogs, 'SHOW_PAGE')"/>
                                                    </a>
                                                </div>
                                                <div class="imgR"></div>
                                            </div>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="admTableTDLast admRight" id="EXPIRES_ON">
                                            <xsl:value-of select="java:getString($dict_dialogs, 'EXPIRES_ON')"/>
                                        </td>
                                        <td colspan="2" class="admTableTDLast">
                                            <div style="float:left; padding:5px 0 0 0;">
                                                <input style="width: 80px;" type="text" id="expiredDateId" readonly="true"/>
                                                <span class="admLocation">&#160;(dd-mm-yyyy)</span>
                                            </div>
                                            <div class="admNavPanelInp" style="padding:0;float:left;">
                                                <div class="imgL"></div>
                                                <div align="center">
                                                    <a class="admNavPanelInp" focus="blur()" style="width:83px;padding:10 5 0 5;" href="#" onclick="hidePage(); return false;" id="buttonHide" onfocus="blur()">
                                                        <xsl:value-of select="java:getString($dict_dialogs, 'HIDE_PAGE')"/>
                                                    </a>
                                                </div>
                                                <div class="imgR"></div>
                                            </div>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="admTableTDLast admRight" id="PAGE_META_DESCRIPTION" width="180">
                                            <xsl:value-of select="java:getString($dict_dialogs, 'PAGE_META_DESCRIPTION')" disable-output-escaping="yes"/>
                                        </td>
                                        <td colspan="2" class="admTableTDLast">
                                            <textarea name="metaDescription" class="admTextArea" rows="3">
                                                <xsl:if test="not(negeso:frontpage/@metatags='true')">
                                                    <xsl:attribute name="disabled">disabled</xsl:attribute>
                                                </xsl:if>
                                                
                                            </textarea>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="admTableTDLast admRight" id="PAGE_META_KEYWORDS" width="180">
                                            <xsl:value-of select="java:getString($dict_dialogs, 'PAGE_META_KEYWORDS')" disable-output-escaping="yes"/>
                                        </td>
                                        <td colspan="2" class="admTableTDLast">
                                            <textarea name="metaKeywords" class="admTextArea" rows="3">
                                                <xsl:if test="not(negeso:frontpage/@metatags='true')">
                                                    <xsl:attribute name="disabled">disabled</xsl:attribute>
                                                </xsl:if>
                                            </textarea>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="admTableTDLast admRight" id="PAGE_GOOGLE_SCRIPT" width="180">
                                            <xsl:value-of select="java:getString($dict_dialogs, 'PAGE_GOOGLE_SCRIPT')" disable-output-escaping="yes"/>
                                        </td>
                                        <td colspan="2" class="admTableTDLast">
                                            <textarea name="googleScript" class="admTextArea" rows="3">
                                                <xsl:if test="not(negeso:frontpage/@google_script_enabled ='true')">
                                                    <xsl:attribute name="disabled">disabled</xsl:attribute>
                                                </xsl:if>
                                            </textarea>
                                        </td>
                                    </tr>
                                    <!--
                  <xsl:if test="negeso:frontpage/@metatags='true'">
						<tr>
							<td class="admTableTDLast admRight" width="180">Page property</td>	
							<td colspan="2" class="admTableTDLast"><textarea name="propertyValue" class="admTextArea" rows="3" /></td>
						</tr>	
                  </xsl:if>	
                  -->
                                    <tr>
                                        <td class="admTableTDLast admRight" id="container">
                                            <xsl:value-of select="java:getString($dict_dialogs, 'CONTAINER')"/>
                                        </td>
                                        <td class="admTableTDLast" colspan="2">
                                            <select style="width: 165px" name="containerId">
                                                <xsl:for-each select="//negeso:container">
                                                    <option value ="{@id}">
                                                        <xsl:value-of select="@name"/>
                                                    </option>
                                                </xsl:for-each>
                                            </select>
                                        </td>
                                    </tr>
                                     <tr>
					                      <td class="admTableTDLast admRight">Force visibility</td>
					                      <td colspan="2" class="admTableTDLast">
					                          <input type="checkbox" id="visible" name="visible" class="radio">
					                          	<xsl:if test="negeso:page/@visible = 'true'" >                           
					                             	<xsl:attribute name="checked">checked</xsl:attribute>
					                             </xsl:if>
					                          </input>					                          
					                      </td>
					                  </tr>
					                                    
                                    <tr>
                                        <td class="admTableTDLast admRight" id="PAGE_TEMPLATE">
                                            <xsl:value-of select="java:getString($dict_dialogs, 'PAGE_TEMPLATE')"/>
                                        </td>
                                        <td class="admTableTDLast" colspan="2">
                                            <input type="hidden" id="template" name="template" value="special" />
                                            <input type="text" id="templateTitle" readonly="true" value="Special page" class="admTextField" />
                                            <input type="button" id="templateSelection"  value="..." style="width: 24px; height: 21px;"
                                                onclick="selectTemplate()" />
                                        </td>
                                    </tr>                                    
                                    <tr>
                                        <td class="admTableTDLast admRight" id="MULTI_LANGUAGE">Multilanguage</td>
                                        <td colspan="2" class="admTableTDLast">
                                            <input type="checkbox" id="multiLanguage" name="multilanguage" class="radio"/>
                                        </td>
                                    </tr>                                    
                                    <tr>
                                        <td class="admTableTDLast admRight">Include this<br/>page in search</td>
                                        <td colspan="2" class="admTableTDLast">
                                            <input type="checkbox" id="isSearch" name="search" value="on" class="radio" checked="checked"/>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="admTableTDLast" colspan="3">
                                            <div class="admNavPanelInp" style="padding-left: 210px;">
                                                <div class="imgL"></div>
                                                <div>
                                                    <input type="button" onclick="createNewPage()" id="buttonYes" value="OK" onfocus="blur()" />
                                                </div>
                                                <div class="imgR"></div>
                                            </div>
                                            <div class="admNavPanelInp">
                                                <div class="imgL"></div>
                                                <div>
                                                    <input type="button" onclick="window.close()" id="buttonCancel" value="Cancel" onfocus="blur()" />
                                                </div>
                                                <div class="imgR"></div>
                                            </div>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="admTableFooter" colspan="3">&#160;</td>
                                    </tr>
                                </table>
                            </td>
                            <td class="admMaiRight">
                                <img src="/images/right_bot.png" alt="" />
                            </td>
                        </tr>
                    </table>
                </form>


            </body>
        </html>

    </xsl:template>
</xsl:stylesheet>
