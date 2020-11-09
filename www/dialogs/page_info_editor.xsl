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

<xsl:variable name="lang" select="/negeso:page-properties/@interface-language"/>             
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>        
<xsl:variable name="dict_dialogs" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_dialogs.js',$lang)"/>
<xsl:variable name="dict_core" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('core',$lang)"/>
    
<!-- MAIN ENTRY -->
<xsl:template match="/negeso:page-properties">
<html>
<head>
    <title>Change page properties</title>
    <META http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <link rel="stylesheet" type="text/css" href="/css/admin_style.css"/>
    <link rel="stylesheet" type="text/css" href="/css/customized-jquery-ui.css"/>
    <link rel="stylesheet" type="text/css" href="/css/jquery-ui.css"/>
    <link rel="stylesheet" type="text/css" href="/css/jquery-ui.custom.css"/>
     <xsl:if test="/negeso:page-properties/negeso:closeOnSave">
        <script>window.close();</script> 
     </xsl:if>
    <script language="JavaScript" src="/script/jquery.min.js"/>
    <script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script language="JavaScript" src="/script/common_functions.js"/>    
    <script language="JavaScript" src="/script/calendar_picker.js"/>
    <script language="JavaScript" src="/script/security.js"/>
    <script type="text/javascript" language="JavaScript" src="/script/special_pages.js"></script>
    <script type="text/javascript" language="JavaScript" src="/script/menu_item.js"></script>    
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
    <script type="text/javascript">
    	var s_PageNameCannotBeEmpty =  '<xsl:value-of select="java:getString($dict_dialogs, 'PAGE_NAME_CANNOT_BE_EMPTY')"/>';
	    var s_PageNameCannotConsistOfSpacesAlone ='<xsl:value-of select="java:getString($dict_dialogs, 'PAGE_NAME_CANNOT_CONSIST_OF_SPACES_ALONE')"/>';
	    var s_FileNameCannotBeEmpty =  '<xsl:value-of select="java:getString($dict_dialogs, 'FILE_NAME_CANNOT_BE_EMPTY')"/>';
	    var s_FileNameIsTooLong = '<xsl:value-of select="java:getString($dict_dialogs, 'FILE_NAME_IS_TOO_LONG')"/>';
	    var s_MaximumCharacters = ' <xsl:value-of select="java:getString($dict_dialogs, 'MAXIMUM_CHARACTERS')"/>';
	    var s_InvalidFileName = '<xsl:value-of select="java:getString($dict_dialogs, 'INVALID_FILE_NAME')"/>';
	    var s_SpacesAreNotAllowed = '<xsl:value-of select="java:getString($dict_dialogs, 'SPACES_ARE_NOT_ALLOWED')"/>';
	    var spacesAreNotAllowed = '<xsl:value-of select="java:getString($dict_dialogs, 'INVALID_FILE_NAME')"/> _ - :  <xsl:value-of select="java:getString($dict_dialogs, 'SPACES_ARE_NOT_ALLOWED')"/>';
    
    	 var langCode = '<xsl:value-of select="negeso:curlanguage/@code"/>';
    </script>

</head>
  <body style="padding-left:12px;	background:#f1f1f1 url('../../images/bgimage.jpg') center top repeat-y;">
    <form name="page"  id="page" method="post">
    <input type="hidden" name="id"  value="{negeso:page/@id}"/>
    <input type="hidden" name="act"  value="save"/>
    <input type="hidden" name="closeOnSave" id="closeOnSave" value="false"/>
    <input type="hidden" name="menuId"  id="menuId" value="{negeso:page/@menuId}"/>
	
    <table class="admMain"  align="center" border="0" cellpadding="0" cellspacing="0" >
        <tr>
            <td colspan="3" class="admMainLogo"><img src="/images/logo.png" />
	            <div style="float: right">
		            <img src="/qrcode?txt={java:com.negeso.framework.Env.getHostName()}{negeso:page/@filename}" />
	            </div>
	        </td>
        </tr>
        <tr >
            <td class="admConnerLeft"></td>
            <td class="admTopBtn">
                <div class="admBtnGreen">
                    <div class="imgL"></div>
                    <div><a class="admBtnText" href="#" onclick="return window.close()" onfocus="blur()"><xsl:value-of select="java:getString($dict_dialogs, 'BACK')"/></a></div>
                    <div class="imgR"></div>
                </div>
                <div class="admBtnGreen">
                    <div class="imgL"></div>
                    <div><a class="admBtnText" href="#" onclick="return window.close()" onfocus="blur()"><xsl:value-of select="java:getString($dict_dialogs, 'CLOSE')"/></a></div>
                    <div class="imgR"></div>
                </div>
                <div class="admBtnGreen admBtnBlue">
                    <div class="imgL"></div>
                    <div>
                        <a class="admBtnText callHelp" href="/admin/help/cpp1_{$lang}.html" onfocus="blur()">Help</a>
                    </div>
                    <div class="imgR"></div>
                </div>
            </td>
            <td class="admConnerRight">                              
            </td>
        </tr>
        <tr>
            <td class="admMainLeft"><img src="/images/left_bot.png" /></td>
            <td>
                <table align="center" border="0" cellpadding="0" cellspacing="0" class="admTable">                    
                    <tr>
                        <td align="center" class="admNavPanelFont" id="CHANGE_PAGE_PROPERTIES" colspan="2"> <xsl:value-of select="java:getString($dict_dialogs, 'CHANGE_PAGE_PROPERTIES')"/></td>
                    </tr>
                    <xsl:if test="count(//negeso:language) > 1">
                        <tr>
                            <td colspan="1"  class="admTableTDLast" style="padding: 0 0 15px 20px;">Translate this page to unlinked page:
                            <xsl:for-each select="//negeso:language">
                               <xsl:if test="not(@selected)">                           
                                   <br/><input type="checkbox" name="translateToLang" value="{@id}"/>&#160;<xsl:value-of select="@name"/>
                               </xsl:if>
                            </xsl:for-each>
	 
	                        <br/><input type="checkbox" name="isPageNameTranslatable"/>&#160; <label id="INCLUDING_PAGE_URL" ><xsl:value-of select="java:getString($dict_dialogs, 'INCLUDING_PAGE_URL')"/></label>
                            <input type="hidden" id="fromLang" value="{./negeso:page/@id}"></input>
							
                            </td>
                            <td class="admTableTDLast">
                            	
                            	<div class="admNavPanelInp"  style="float:right; padding:0 20 5 0;">
	                                <div class="imgL"></div>
		                                 <div align="center">
		                                	<a class="admNavPanelInp" style="width:83px;" focus="blur()" href="#" onclick="translatePage({negeso:page/@id});return false;" onfocus="blur()"><xsl:value-of select="java:getString($dict_dialogs, 'STM_TRANSLATE')"/></a>
		                                </div>
	                                <div class="imgR"></div>
                            	</div>
                            	
                            	<div id="translateMessage" style="height=100%; width:400px;"></div>
                            </td>
                        </tr>
                       
                    </xsl:if>
                    
                    <tr>
                        <td class="admTableTDLast" style="padding: 0 0 0 20px;"  width="384"> <xsl:value-of select="java:getString($dict_dialogs, 'PAGE_TITLE')"/> </td>
                        <td class="admTableTDLast"  width="380">
                            <input type="text" name="title" id="title" class="admTextArea" value="{negeso:page/@title}"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="admTableTDLast" style="padding: 0 0 0 20px;" id="META_TITLE"> <xsl:value-of select="java:getString($dict_dialogs, 'META_TITLE')"/> </td>
                        <td class="admTableTDLast" >
                            <input type="text" name="metaTitle" class="admTextArea" value="{negeso:page/@metaTitle}"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="admTableTDLast" style="padding: 0 0 0 20px;" id="FILE_NAME"> <xsl:value-of select="java:getString($dict_dialogs, 'FILE_NAME')"/>  </td>
                        <td class="admTableTDLast">
                            <input type="hidden" name="filename" id="filename" class="admTextArea" value=""/>
                            <input type="text" name="filename_s" id="filename_s" class="admTextArea" value="{negeso:page/@filename}">
                            		<xsl:if test="negeso:page/@protected ='nodelete'"> 
                            			<xsl:attribute name="disabled">disabled</xsl:attribute>
                            		</xsl:if>
                            </input>_<xsl:value-of select="negeso:curlanguage/@code"/>.html
                            <script>
                                var filename = document.getElementById("filename_s").value.replace(/(_<xsl:value-of select="negeso:curlanguage/@code"/>)?\.html$/,"");
                                document.getElementById("filename_s").value = filename;
                            </script>
                            
                            <xsl:if test="negeso:page/@protected_ ='nodelete'"> 
	                            <span id="protected" style="font-size:11px;">
	                                <br/>You can't rename file name for this page!
	                            </span>
                            </xsl:if>
                        </td>
                    </tr>
                    <tr>
                        <td class="admTableTDLast" style="padding: 0 0 0 20px;" id="PAGE_META_DESCRIPTION"><xsl:value-of select="java:getString($dict_dialogs, 'PAGE_META_DESCRIPTION')" disable-output-escaping="yes"/></td>
                        <td class="admTableTDLast" id="admTableTDtext" >
                            <textarea name="metaDescription" class="admTextArea" rows="3">
                            	 <xsl:if test="negeso:page/@metatags = 'false'">
                            	 	<xsl:attribute name="disabled">disabled</xsl:attribute>
                            	 </xsl:if>
                            	 <xsl:value-of select="negeso:page/@metaDescription" />
                            </textarea>
                        </td>
                    </tr>
                    <tr>
                        <td class="admTableTDLast" style="padding: 0 0 0 20px;" id="PAGE_META_KEYWORDS"><xsl:value-of select="java:getString($dict_dialogs, 'PAGE_META_KEYWORDS')" disable-output-escaping="yes"/></td>
                        <td class="admTableTDLast" >
                          <textarea name="metaKeywords" class="admTextArea" rows="3" value="{negeso:page/@metaKeywords}">
                          		<xsl:if test="negeso:page/@metatags = 'false'">
                            	 	<xsl:attribute name="disabled">disabled</xsl:attribute>
                            	 </xsl:if>
                            	 <xsl:value-of select="negeso:page/@metaKeywords" />
                          </textarea>
                        </td>
                    </tr>
                    <tr>
                        <td class="admTableTDLast" style="padding: 0 0 0 20px;" id="PAGE_META_AUTHOR"><xsl:value-of select="java:getString($dict_core, 'PAGE_META_AUTHOR')" disable-output-escaping="yes"/></td>
                        <td class="admTableTDLast" >
                          <input name="metaAuthor" class="admTextArea" rows="3" value="{negeso:page/@metaAuthor}">
                                <xsl:if test="negeso:page/@metatags = 'false'">
                                    <xsl:attribute name="disabled">disabled</xsl:attribute>
                                 </xsl:if>
                          </input>
                        </td>
                    </tr>
                    <tr>
                        <td class="admTableTDLast" style="padding: 0 0 0 20px;" id="PAGE_GOOGLE_SCRIPT"><xsl:value-of select="java:getString($dict_dialogs, 'PAGE_GOOGLE_SCRIPT')" disable-output-escaping="yes"/></td>
                        <td class="admTableTDLast" >
                               <textarea name="googleScript" class="admTextArea" rows="3">
                               		<xsl:if test="negeso:page/@google_script_enabled ='false'"> 
                            			<xsl:attribute name="disabled">disabled</xsl:attribute>
                            		</xsl:if>
                            		<xsl:value-of select="negeso:page/@googleScript" />
                               </textarea>
                        </td>
                    </tr>
                    <xsl:if test="negeso:page/@metatags = 'true'">
                    	<xsl:if test="negeso:page/@propertyValue and not(negeso:page/@propertyValue = '')">
						<tr>
							<td class="admMainTD admRight admWidth150"><xsl:value-of select="java:getString($dict_dialogs, 'PAGEP_PROPERTY_NAME')"/></td>	
							<td class="admTableTDLast" ><textarea name="propertyValue" class="admTextArea" rows="3" value="{negeso:page/@propertyValue}"/></td>
						</tr>	
                    	</xsl:if>
                    </xsl:if>
                    <tr>
                        <td class="admTableTDLast" style="padding: 0 0 0 20px;" id="PUBLISH_ON"><xsl:value-of select="java:getString($dict_dialogs, 'PUBLISH_ON')"/></td>
                        <td class="admTableTDLast" >
                            <table width="100%">
                                <tr>
                                    <td>										
                                        <input class="admTextArea admWidth100" type="text" name="publishDate" id="publishDateId" readonly="true" value="{negeso:page/@publishDate}"/>
                                    </td>
                                    <td>(dd-mm-yyyy)</td>
                                    <td  align="left">
                                        <div class="admNavPanelInp" style="padding:0;">
                                            <div class="imgL"></div>
                                            <div align="center">
                                                <a class="admNavPanelInp" style="width:83px;" focus="blur()" href="#" onclick="showPage();return false;" id="buttonShow" onfocus="blur()"><xsl:value-of select="java:getString($dict_dialogs, 'SHOW_PAGE')"/></a>
                                            </div>
                                            <div class="imgR"></div>
                                        </div>
                                    </td>
                                </tr>
                            </table>                            
                        </td>
                    </tr>
                    
                    <tr>
                        <td class="admTableTDLast" style="padding: 0 0 0 20px;" id="EXPIRES_ON"><xsl:value-of select="java:getString($dict_dialogs, 'EXPIRES_ON')"/></td>
                        <td class="admTableTDLast" >
                            <table width="100%">
                                <tr>
                                    <td>
                                        <input class="admTextArea admWidth100" type="text" name="expiredDate" id="expiredDateId"  readonly="true" value="{negeso:page/@expiredDate}"/>                                       	
                                    </td>
                                    <td>(dd-mm-yyyy)</td>
                                    <td  align="left">
                                        <div class="admNavPanelInp" style="padding:0;">
                                            <div class="imgL"></div>
                                            <div align="center">
                                                <a class="admNavPanelInp" style="width:83px;" focus="blur()" href="#" onclick="hidePage();return false;" id="buttonShow" onfocus="blur()"><xsl:value-of select="java:getString($dict_dialogs, 'HIDE_PAGE')"/></a>
                                            </div>
                                            <div class="imgR"></div>
                                        </div>
                                    </td>
                                </tr>
                            </table>                            
                        </td>
                    </tr>
                   
                    <tr>
                        <td class="admTableTDLast" style="padding: 0 0 0 20px;" id="LAST_EDIT_DATE"><xsl:value-of select="java:getString($dict_dialogs, 'LAST_EDIT_DATE')"/></td>
                        <td class="admTableTDLast" >
                            <input class="admTextArea admWidth150" type="text" name="editDate" readonly="true" disabled="disabled" value="{negeso:page/@editDate}"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="admTableTDLast" style="padding: 0 0 0 20px;" id="LAST_EDIT_USER"><xsl:value-of select="java:getString($dict_dialogs, 'LAST_EDIT_USER')"/></td>
                        <td class="admTableTDLast" >
                            <input class="admTextArea admWidth150" type="text" name="editUser" readonly="true" disabled="disabled" value="{negeso:page/@editUser}"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="admTableTDLast" style="padding: 0 0 0 20px;" id="PAGE_TEMPLATE"><xsl:value-of select="java:getString($dict_dialogs, 'PAGE_TEMPLATE')"/></td>
                        <td class="admTableTDLast" >
                            <input class="admTextArea admWidth150" type="text" name="class_" readonly="true" disabled="disabled" value="{negeso:page/@class}"/>
                        </td>
                    </tr>
                    <tr>
                        <td class="admTableTDLast" style="padding: 0 0 0 20px;" id="CONTAINER"><xsl:value-of select="java:getString($dict_dialogs, 'CONTAINER')"/></td>
                        <td class="admTableTDLast" >
                            <select class="admTextArea" name="containerId" id="containerId">
	                            <xsl:for-each select="negeso:containers/negeso:container">
	                               <option value ="{@id}">
	                               		<xsl:if test="@id = /negeso:page-properties/negeso:page/@containerId">                           
	                               			<xsl:attribute name="selected">selected</xsl:attribute>
	                               		</xsl:if>
	                               		<xsl:value-of select="@name"/>
	                               	</option>
	                              
	                            </xsl:for-each>
                            </select>
                        </td>
                    </tr>
                    
                     <tr id="containerOfIsSearch">
                        <td class="admTableTDLast" style="padding: 0 0 0 20px;" >Force visibility</td>
                        <td class="admTableTDLast" >
                            <input type="checkbox" name="visible" class="radio" value="true">
                            	<xsl:if test="negeso:page/@visible = 'true'" >                           
	                               			<xsl:attribute name="checked">checked</xsl:attribute>
	                             </xsl:if>
                            </input>
                        </td>
                    </tr>
                    
                    <tr id="containerOfIsSearch">
                        <td class="admTableTDLast" style="padding: 0 0 0 20px;" id="IS_SEARCH">Include this<br/>page in search</td>
                        <td class="admTableTDLast" >
                            <input type="checkbox" name="search" class="radio" value="true">
                            	<xsl:if test="negeso:page/@search = 'true'" >                           
	                               			<xsl:attribute name="checked">checked</xsl:attribute>
	                             </xsl:if>
                            </input>
                        </td>
                    </tr>
                    <tr>
                        <td class="admTableTDLast" style="padding: 0 0 0 20px;" >Google sitemap<br/>options</td>
                        <td class="admTableTDLast" >
                            <input type="checkbox" name="sitemap" class="radio" value="true">
                            	<xsl:if test="negeso:page/@sitemap = 'true'" >                           
	                               			<xsl:attribute name="checked">checked</xsl:attribute>
	                             </xsl:if>
                            </input>
                        </td>
                    </tr>
                    
                    <tr>
                        <td class="admTableTDLast" style="padding: 0 0 0 20px;">Page priority</td>
                        <td class="admTableTDLast" >
                            <select class="admTextArea" name="sitemapPrior" id="sitemapPrior">
	                            <xsl:for-each select="//negeso:sitemapPriority">
	                               <option value ="{text()}">
	                               		<xsl:if test="text() = /negeso:page-properties/negeso:page/@sitemapPrior">                           
	                               			<xsl:attribute name="selected">selected</xsl:attribute>
	                               		</xsl:if>
	                               		<xsl:value-of select="text()"/>
	                               	</option>
	                              
	                            </xsl:for-each>
                            </select>
                        </td>
                    </tr>
                    
                    <tr>
                        <td class="admTableTDLast" style="padding: 0 0 0 20px;">Change frequency</td>
                        <td class="admTableTDLast" >
                            <select class="admTextArea" name="sitemapFreq" id="sitemapFreq">
	                            <xsl:for-each select="//negeso:sitemapFrequency">
	                               <option value ="{text()}">
	                               		<xsl:if test="text() = /negeso:page-properties/negeso:page/@sitemapFreq">                           
	                               			<xsl:attribute name="selected">selected</xsl:attribute>
	                               		</xsl:if>
	                               		<xsl:value-of select="text()"/>
	                               	</option>
	                              
	                            </xsl:for-each>
                            </select>
                        </td>
                    </tr>
                    
                    <tr id="containerOfImages" style="margin: 0; padding: 0">
                        <td class="admTableTDLast">
                            <table>
                                <tr>
                                    <td style="padding: 0 0 0 15px;" id="IMAGES" >Edit images</td>
                                    <td class="admNavChoose" style="padding:0 0 0 5px;">
                                        <div align="center">
                                            <input type="button" value="..." style="width: 24px; height: 21px;" onfocus="blur()">
                                                <xsl:choose>
                                                    <xsl:when test="negeso:page/@attributeSetId">
                                                        <xsl:attribute name="onclick">selectImages(<xsl:value-of select="negeso:page/@id"/>)</xsl:attribute>
                                                    </xsl:when>
                                                    <xsl:otherwise>
                                                        <xsl:attribute name="onclick">alert('<xsl:value-of select="java:getString($dict_common, 'CORE.SITE_HAS_NO_PF')"/>')</xsl:attribute>
                                                    </xsl:otherwise>
                                                </xsl:choose>
                                            </input>
                                        </div>
                                    </td>
                                </tr>
                            </table>
                        </td>
                        <td class="admTableTDLast">
                            <table>
                                <tr>
                                    <td style="padding: 0 0 0 5px;"  id="Aliases" > Edit Aliases </td>
                                    <td class="admNavChoose" >
                                        <div align="center">
                                            <input type="button" value="..." style="width: 24px; height: 21px;" onclick="selectAliases({negeso:page/@id})" onfocus="blur()"/>
                                        </div>
                                    </td>
									<td style="padding: 0 0 0 15px;"  id="Aliases" > Choose Rich Snippets  </td>
                                    <td class="admNavChoose" >
                                        <div align="center">
                                            <input type="button" value="..." style="width: 24px; height: 21px;" onclick="chooseRichSnippets({negeso:page/@id}, 'page')" onfocus="blur()"/>
                                        </div>
                                    </td>
                                </tr>
                            </table>
                        </td>                        
                    </tr>
                    <tr>
                        <td colspan="2" class="admTableFooter"></td>
                    </tr>
                    
                </table>
            </td>
            <td class="admMaiRight">
                <img src="/images/right_bot.png" />
            </td>
        </tr>
        <tr >
            <td colspan="3"  style="padding:0 0 0 20px;" >
                <div class="admBtnGreenb" >
                    <div class="imgL"></div>
                    <div>
                        <input type="button" onclick="savePage(false);" id="buttonYes" class="admNavbarInp" value="{java:getString($dict_dialogs, 'SAVE')}"/>
                    </div>
                    <div class="imgR"></div>
                </div>
                
                <div class="admBtnGreenb" >
                    <div class="imgL"></div>
                    <div>
                        <input type="button" onclick="savePage(true);" id="buttonSaveAndClose" class="admNavbarInp" value="{java:getString($dict_dialogs, 'SAVE_AND_CLOSE')}"/>
                    </div>
                    <div class="imgR"></div>
                </div>
                
            </td>
        </tr>
    </table> 
    </form>
</body>

</html>
</xsl:template>
















</xsl:stylesheet>