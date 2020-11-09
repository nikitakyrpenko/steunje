<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$       
 
  Copyright (c) 2004 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  Upload file form.
 
  @author       Olexiy.Strashko
  @version      $Revision$
  @author     Volodymyr Snigur

  This template woks in 2 steps:
    1. Submit form
    2. After form submit it runs onLoad callback depends on submit result. This callback generate return value for showModalDialog

  Form submit runs in iframe to prevent page reload
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">
        
<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_media_catalog" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_media_catalog.xsl', $lang)"/>
<xsl:variable name="dict_upload_file" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_upload_file.xsl', $lang)"/>
<xsl:variable name="dict_picture_frame_adds" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_picture_frame_adds.xsl', $lang)"/>

<!-- Include/Import -->
<xsl:include href="/xsl/negeso_body.xsl"/>
<!-- NEGESO JAVASCRIPT Temaplate -->
<xsl:template name="java-script">
   
    <script language="JavaScript">
        var s_MustSelect = "<xsl:value-of select="java:getString($dict_media_catalog, 'MUST_SELECT')"/>";
              var s_FlashHeightErrorMsg = "<xsl:value-of select="java:getString($dict_picture_frame_adds, 'ADV_PF.FLASH_HEIGHT_ERROR_MSG')"/>";
           var s_FlashWidthErrorMsg = "<xsl:value-of select="java:getString($dict_picture_frame_adds, 'ADV_PF.FLASH_WIDTH_ERROR_MSG')"/>";
        <xsl:text disable-output-escaping="yes">
        <![CDATA[
              
              var caller = window.parent;
        
              function checkUploadForm(height, width, mode){
                        
                     if (width=='') width=-1;
                     if (height=='') height=-1;
                     if (mode=='flash') {
                            if (document.uploadResourceForm.width &&
                                !(document.uploadResourceForm.width.value > 0 
                                && document.uploadResourceForm.width.value <= parseFloat(width)*1))
                            {
                                   alert(s_FlashWidthErrorMsg);
                                   return false;
                            }
                            
                            if (!(document.uploadResourceForm.height.value > 0 
                                && document.uploadResourceForm.height.value <= parseFloat(height)*1))
                            {
                                   alert(s_FlashHeightErrorMsg);
                                   return false;
                            }
                     } else {
                            if (document.uploadResourceForm.width && document.uploadResourceForm.requiredWidth.value <= 0){
                                   alert(s_FlashWidthErrorMsg);
                                   return false;
                            }
                     }
                     return checkFileSource();
              }

              function test(form){
                     var s='';
                     for (var i=0; i<form.childNodes.length; i++) {
                            s+=test(form.childNodes[i]);
                     }
                     if (form.tagName && (form.tagName.toUpperCase()=="INPUT" || form.tagName.toUpperCase()=="SELECT")) 
            {
                            if (form.tagName.toUpperCase()=="INPUT" && form.type!="radio" && form.type!="checkbox")
                                   s="&"+form.name+"="+form.value;
                            else if (form.tagName.toUpperCase()=="INPUT" && (form.type=="radio" || form.type=="checkbox"))
                                   if (form.checked) s="&"+form.name+"="+form.value;
                                else if (form.tagName.toUpperCase()=="SELECT" || form.tagName.toUpperCase()=="TEXTAREA")
                                       s="&"+form.name+"="+form.value;
                     }
                     return s;
              }


              function checkFileSource(){
                     // prompt('',test(document.uploadResourceForm));
                     for(i=0; i<document.uploadResourceForm.fileSource.length; i++)
                     {
                            if (document.uploadResourceForm.fileSource[i].checked)
                            {
                                   if (document.uploadResourceForm.fileSource[i].value == "disk")
                    {
                                          if (document.uploadResourceForm.selectedFile.value=="")
                        {
                                                 alert(s_MustSelect);
                                                 return false;
                                          }
                                   }
                            }
                    }


                    //document.uploadResourceForm.submitBtn.disabled=true;
                    return true;
              }
                function onLoadWindow(realImage, thumbnailImage){
                    window.name='UploadWindow';
                }

                function returnFileResult(file){
                    caller.name='UploadWindow';
                    caller.returnValue = new Object();
                    caller.returnValue.resCode = "OK";
                    caller.returnValue.fileUrl = file;
                    caller.close();
                }

                function returnFlashResult(file, height, width){
                    caller.name='UploadWindow';
                    caller.returnValue = new Object();
                    caller.returnValue.resCode = "OK";
                    caller.returnValue.fileUrl = file;
                    caller.returnValue.flashHeight = height;
                    caller.returnValue.flashWidth = width;
                    caller.close();
                }
                
                function returnImageResult(realImage, width, height){
                    caller.name='UploadWindow';
                    
                    caller.returnValue = new Object();
                    caller.returnValue.resCode = "OK";
                    caller.returnValue.realImage = realImage;
                    caller.returnValue.realImageWidth = width;
                    caller.returnValue.realImageHeight = height;
                    caller.close();
                }

                function returnBannerResult(realBanner, width, height, imageType){
                    caller.name='UploadWindow';
                    caller.returnValue = new Object();
                    caller.returnValue.resCode = "OK";
                    caller.returnValue.realBanner = realBanner;
                    caller.returnValue.realBannerWidth = width;
                    caller.returnValue.realBannerHeight = height;
                    caller.returnValue.imageType = imageType;
                    caller.close();
                }

                function returnThumbnailResult(
                       realImage, width, height, 
                       thumbnailImage, thWidth, thHeight
                ){
                    caller.name='UploadWindow';
                    caller.returnValue = new Object();
                    caller.returnValue.resCode = "OK";
                    caller.returnValue.realImage = realImage;
                    caller.returnValue.realImageWidth = width;
                    caller.returnValue.realImageHeight = height;

                    caller.returnValue.thumbnailImage = thumbnailImage;
                    caller.returnValue.thumbnailImageWidth = thWidth;
                    caller.returnValue.thumbnailImageHeight = thHeight;
                    caller.close();
                }

                function OnCancel(){
                    window.name='UploadWindow';
                    window.close();
                }

                function OnBack(){
                    alert ("Back");
                    window.history.back();
                }

                function typeDisk(){
                    document.uploadResourceForm.fileSource[0].checked = true;
                    $('#selectedFile').removeAttr('disabled');
                    $('#existentFileId').attr('disabled','disabled');
                }

                function typeCatalog(){
                    document.uploadResourceForm.fileSource[1].checked = true;
                    $('#selectedFile').attr('disabled','disabled');
                    $('#existentFileId').removeAttr('disabled');
                }

                function ChooseFromMediaCatalog(selectBox){
                    typeCatalog();
                    MediaCatalog.chooseImage(selectBox);
                }

                function CloseForm(){ 
                    window.close();
                }

                function ChooseFlashFromMediaCatalog(selectBox){
                             typeCatalog();
                             MediaCatalog.chooseFlash(selectBox);
                      }
        ]]>
        </xsl:text>
    </script>
</xsl:template>

<!-- MAIN ENTRY -->
<xsl:template match="/">
<xsl:text disable-output-escaping="yes"> <![CDATA[<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">]]></xsl:text>
 <html>
    <head>
        <title>
            <xsl:call-template name="choose-title"/>
        </title>
        <META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=utf-8"/>
        <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
        <link rel="stylesheet" type="text/css" href="/css/jquery-ui.custom.css"/>
        
        <script type="text/javascript" src="/script/jquery.min.js"></script>        
        <script type="text/javascript" src="/script/jquery-ui.custom.min.js" />
        <script type="text/javascript" src="/script/cufon-yui.js"></script>
        <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
        <script type="text/javascript" src="/script/common_functions.js"></script>
        <script language="JavaScript" src="/script/media_catalog.js" type="text/javascript">/**/</script>
  
        <xsl:call-template name="java-script"/>
    </head>
    <body class="dialogSmall">
        <xsl:choose>
            <xsl:when test="/negeso:page/negeso:thumbnail-upload-result">
                <xsl:attribute name="OnLoad">
                    returnThumbnailResult(
                        '<xsl:value-of select = "/negeso:page/negeso:thumbnail-upload-result/@real-file"/>', 
                        '<xsl:value-of select = "/negeso:page/negeso:thumbnail-upload-result/@real-file-width"/>', 
                        '<xsl:value-of select = "/negeso:page/negeso:thumbnail-upload-result/@real-file-height"/>', 
                        '<xsl:value-of select = "/negeso:page/negeso:thumbnail-upload-result/@thumbnail-file"/>',
                        '<xsl:value-of select = "/negeso:page/negeso:thumbnail-upload-result/@thumbnail-file-width"/>',
                        '<xsl:value-of select = "/negeso:page/negeso:thumbnail-upload-result/@thumbnail-file-height"/>'
                    );
                </xsl:attribute>
            </xsl:when>
            <xsl:when test="/negeso:page/negeso:image-upload-result">
                <xsl:attribute name="OnLoad">
                    returnImageResult(
                        '<xsl:value-of select = "/negeso:page/negeso:image-upload-result/@real-file"/>', 
                        '<xsl:value-of select = "/negeso:page/negeso:image-upload-result/@real-file-width"/>', 
                        '<xsl:value-of select = "/negeso:page/negeso:image-upload-result/@real-file-height"/>' 
                    );
                </xsl:attribute>
            </xsl:when>
            <xsl:when test="/negeso:page/negeso:file-upload-result">
                        <xsl:choose>
                            <xsl:when test="/negeso:page/negeso:upload-option">
                                <xsl:attribute name="OnLoad">
                                    returnFlashResult(
                                        '<xsl:value-of select = "/negeso:page/negeso:file-upload-result/@real-file"/>',
                                        '<xsl:value-of select = "/negeso:page/negeso:upload-option/@height"/>',
                                        '<xsl:value-of select = "/negeso:page/negeso:upload-option/@width"/>'
                                    );
                                </xsl:attribute>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:attribute name="OnLoad">
                                    returnFileResult(
                                        '<xsl:value-of select = "/negeso:page/negeso:file-upload-result/@real-file"/>' 
                                    );
                                </xsl:attribute>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:when>
            <xsl:when test="/negeso:page/negeso:banner-upload-result">
                <xsl:attribute name="OnLoad">
                    returnBannerResult(
                        '<xsl:value-of select = "/negeso:page/negeso:banner-upload-result/@file"/>',
                        '<xsl:value-of select = "/negeso:page/negeso:banner-upload-result/@file-width"/>',
                        '<xsl:value-of select = "/negeso:page/negeso:banner-upload-result/@file-height"/>',
                        '<xsl:value-of select = "/negeso:page/negeso:banner-upload-result/@imageType"/>'
                    );
                </xsl:attribute>
            </xsl:when>
            <xsl:otherwise>
                <xsl:attribute name="OnLoad">onLoadWindow();</xsl:attribute>
                <iframe id="upload_target" name="upload_target" src="about:blank" style="width:0;height:0;border:0px solid #fff;"></iframe>
            </xsl:otherwise>
        </xsl:choose>

        <xsl:call-template name="NegesoBody">
            <xsl:with-param name="helpLink">
                <xsl:text>/admin/help/cnw1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
            </xsl:with-param>
            <xsl:with-param name="backLink" select="'false'"/>
            <xsl:with-param name="close" select="'false'"/>
        </xsl:call-template>

        <script>
            Cufon.now();
            Cufon.replace('.admNavPanelFont', { fontFamily: 'Orator Std' });
            Cufon.replace('.admTDtitles', { fontFamily: 'Orator Std' });
        </script>
    </body>
</html>
</xsl:template>

<xsl:template match="negeso:page"  mode="admContent">
       <xsl:choose>
        <xsl:when test="/negeso:page/negeso:thumbnail-upload-result">
            <xsl:apply-templates select="negeso:error-messages"/>
            <xsl:apply-templates select="negeso:thumbnail-upload-result"/>
        </xsl:when>
        <xsl:when test="/negeso:page/negeso:upload-option">
            <xsl:apply-templates select="negeso:error-messages"/>
            <xsl:apply-templates select="negeso:user-messages"/>
            <xsl:call-template name="uploadForm"/>
        </xsl:when>
        <xsl:otherwise>
            <xsl:apply-templates select="negeso:error-messages"/>
            <xsl:apply-templates select="negeso:user-messages"/>
            <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
                <tr>
                    <td>
                        <button  onclick="OnBack()">&lt;&#160;<xsl:value-of select="java:getString($dict_common, 'BACK')"/>&#160;&gt;</button>
                        <button  onclick="OnCancel()">&lt;&#160;<xsl:value-of select="java:getString($dict_common, 'CLOSE')"/>&#160;&gt;</button>
                    </td>
                </tr>
            </table>
        </xsl:otherwise>
    </xsl:choose>

</xsl:template>

<xsl:template match="negeso:thumbnail-upload-result">
    <table class="admNavPanel" cellpadding="0" cellspacing="0">
            <tr>
                    <td class="admCenter">
                        <button class="admMainInp admWidth150" onclick="OnBack()">&lt;&#160;<xsl:value-of select="java:getString($dict_common, 'BACK')"/>&#160;&gt;</button>
                        <button class="admMainInp admWidth150" onclick="OnCancel()">&lt;&#160;<xsl:value-of select="java:getString($dict_common, 'CLOSE')"/>&#160;&gt;</button>
                    </td>
            </tr>
    </table>
</xsl:template>

<xsl:template name="uploadForm">
    <form id="uploadResourceFormId" name="uploadResourceForm" method="post" action="" enctype="multipart/form-data" target="upload_target">
        <xsl:attribute name="onsubmit">checkUploadForm(
                '<xsl:value-of select="negeso:upload-option/@height" />',
                '<xsl:value-of select="negeso:upload-option/@width"/>',
                '<xsl:value-of select="negeso:upload-option/@mode" />'
            );</xsl:attribute>
        <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
            <tr>
                <td align="center" class="admNavPanelFont"  colspan="2">
                    <!-- TITLE -->                  
                    <xsl:call-template name="tableTitle">
                        <xsl:with-param name="headtext">
                            <xsl:call-template name="choose-title"/>
                        </xsl:with-param>
                    </xsl:call-template>
                </td>
            </tr>
        <xsl:apply-templates select="negeso:upload-option"/>
        <tr>
            <td class="admTableTDLast"></td>
            <td class="admTableTDLast" id="admTableTDtext">
                <div class="admNavPanelInp">
                    <div class="imgL"></div>
                    <div><input class="admNavPanelInp" value="Submit" type="submit" id="submitBtnId" name="submitBtn"><xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'OK')"/></xsl:attribute></input></div>
                    <div class="imgR"></div>
                </div>
                <div class="admNavPanelInp" style="padding-left:20px;">
                    <div class="imgL"></div>
                    <div>
                        <input type="button" value="Cancel" onClick="CloseForm()"/>
                    </div>
                    <div class="imgR"></div>
                </div>
            </td>
        </tr>
        <tr>
            <td class="admTableFooter" >&#160;</td>
        </tr>
    </table>
    </form>
</xsl:template>


<xsl:template match="negeso:upload-option[@mode='thumbnail']">
    <!-- Thumbnail upload option -->
    <tr>
        <td colspan="2">
                <input type="hidden" name="command" value="upload-thumbnail-command"/>
                <input type="hidden" name="mode" value="{@mode}"/>
                <input type="hidden" name="width" value="{@width}"/>
                <input type="hidden" name="height" value="{@height}"/> 
                <input type="hidden" name="type" value="{@type}"/>
                <input type="hidden" name="is_strict" value="{@is-strict}"/>
                <xsl:if test="@force-resize-mode">
                    <input type="hidden" name="force_resize_mode" value="{@force-resize-mode}"/>
                    <input type="hidden" name="resize_mode" value="{@force-resize-mode}"/>
                </xsl:if>
        </td>
    </tr>
    <xsl:apply-templates select="../negeso:folder-set"/>
    <!-- Choose file from disk -->
    <tr>
        <td class="admTableTDLast" id="admTableTDtext" align="right">
            <xsl:value-of select="java:getString($dict_upload_file, 'FROM_DISK')"/>:<input 
            id="diskOption"  type="radio" name="fileSource" checked="on" value="disk" onClick="typeDisk()"/>
        </td>
        <td class="admTableTDLast" id="admTableTDtext">
            <input id="selectedFile" class="admTextArea" type="file" name="selectedFile" onKeyPress="return false;"/>
        </td>
    </tr>
    <!-- Choose existant file -->
    <tr>
        <td class="admTableTDLast" id="admTableTDtext" align="right">
            <xsl:value-of select="java:getString($dict_upload_file, 'CHOOSE_EXISTING_FILE')"/>:<input
            id="catalogOption" type="radio" name="fileSource" value="catalog" onClick="typeCatalog()"/>&#160;
        </td>
        <td class="admTableTDLast" id="admTableTDtext">
            <xsl:apply-templates select="../negeso:resource-set"/>
            <button class="admMainInp" style="width:81px;float:left;margin:-3px 0 0 3px;" name="mcButton" type="button" onClick="ChooseFromMediaCatalog(document.all.existentFileId)">
                <img src="/images/media_catalog/media_catalog_logo_browse.gif"/>
            </button>
        </td>
    </tr>
    <xsl:if test="@width">
        <tr>
            <td class="admRight admBold">
                <xsl:choose>
                    <xsl:when test="@type='strict_height'">
                        <xsl:value-of select="java:getString($dict_upload_file, 'MAX_IMAGE_WIDTH')"/>:
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="java:getString($dict_upload_file, 'REQUIRED_IMAGE_WIDTH')"/>:
                    </xsl:otherwise>
                </xsl:choose>
            </td>
            <td class="admLeft">                        
                <input class="admWidth50" id="requiredWidth" type="text" name="requiredWidth" readonly="true" value="{@width}"/>
                (<xsl:value-of select="java:getString($dict_upload_file, 'READONLY')"/>)
            </td>
        </tr>
    </xsl:if>

    <xsl:if test="@height">
        <tr>
            <td class="admRight admBold">
                <xsl:choose>
                    <xsl:when test="@type='strict_width'">
                        <xsl:value-of select="java:getString($dict_upload_file, 'MAX_IMAGE_HEIGHT')"/>:
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="java:getString($dict_upload_file, 'REQUIRED_IMAGE_HEIGHT')"/>:
                    </xsl:otherwise>
                </xsl:choose>
            </td>
            <td class="admLeft">                        
                &#160;<input class="admWidth50" id="requiredHeight" type="text" name="requiredHeigth" readonly="true" value="{@height}"/>
                (<xsl:value-of select="java:getString($dict_upload_file, 'READONLY')"/>)
            </td>
        </tr>
    </xsl:if>
</xsl:template>


<xsl:template match="negeso:upload-option[@mode='image']">
    <!-- Image upload option -->
    <tr>
        <td colspan="2">
                <input type="hidden" name="command" value="upload-thumbnail-command"/>
                <input type="hidden" name="mode" value="{@mode}"/>
                <input type="hidden" name="width">
                    <xsl:attribute name="value"><xsl:value-of select="//@width"/></xsl:attribute>
                </input>
                <input type="hidden" name="height" value="{@height}"/> 
                <input type="hidden" name="type" value="{@type}"/>
                <input type="hidden" name="is_strict" value="{@is-strict}"/>
                <xsl:if test="@force-resize-mode">
                    <input type="hidden" name="force_resize_mode" value="{@force-resize-mode}"/>
                </xsl:if>
        </td>
    </tr>
    <xsl:apply-templates select="../negeso:folder-set"/>
    <!-- Choose file from disk -->
    <tr>
        <td class="admTableTDLast" id="admTableTDtext" align="right">
            <xsl:value-of select="java:getString($dict_upload_file, 'FROM_DISK')"/>:<input 
            id="diskOption" type="radio" name="fileSource" checked="on" value="disk" onClick="typeDisk()"/>
        </td>
        <td class="admTableTDLast">
            <input id="selectedFile" class="admTextArea" type="file" name="selectedFile" onKeyPress="return false;" style="width:295px;margin-left:5px;"/>
        </td>
    </tr>
    <!-- Choose existant file -->
    <tr>
        <td class="admTableTDLast" align="right"  style="padding-right:10px;">
            <xsl:value-of select="java:getString($dict_upload_file, 'CHOOSE_EXISTING_FILE')"/>:<input 
            id="catalogOption" type="radio" name="fileSource" value="catalog" onClick="typeCatalog()"/>
        </td>
        <td class="admTableTDLast" id="admTableTDtext">                        
            <xsl:apply-templates select="../negeso:resource-set"/>
            <button class="admNavChoose" style="width:81px;float:left;margin:-3px 0 0 3px;" type="button" name="mcButton" onClick="ChooseFromMediaCatalog(document.all.existentFileId)">
              <img src="/images/media_catalog/media_catalog_logo_browse.gif"></img>
            </button>
        </td>
    </tr>

    
    <!--<xsl:if test="count(@force-resize-mode)=0">-->
        <!-- Resize  -->
        
        <tr>
            <td class="admTableTDLast" id="admTableTDtext" align="right">
                <xsl:value-of select="java:getString($dict_upload_file, 'RESIZE')"/>:
                <input  type="radio" class="radio" name="resize_mode" value="non_proportional">
                    <xsl:if test = "@force-resize-mode='non_proportional'">
                        <xsl:attribute name="checked">true</xsl:attribute>
                    </xsl:if>
                </input>
                
            </td>
            <td class="admTableTDLast" id="admTableTDtext">
                Resize proportional:
                <input  type="radio" class="radio" name="resize_mode" value="proportional">
                    <xsl:if test = "@force-resize-mode='proportional'">
                        <xsl:attribute name="checked">true</xsl:attribute>
                    </xsl:if>
                </input>
                &#160;&#160;
                <xsl:value-of select="java:getString($dict_upload_file, 'CROP')"/>:
                <input  type="radio" class="radio" name="resize_mode" value="crop">
                    <xsl:if test = "@force-resize-mode='crop'">
                        <xsl:attribute name="checked">true</xsl:attribute>
                    </xsl:if>
                </input>
            </td>
        </tr>
    <!--</xsl:if>-->

    <xsl:if test="@width">
        <tr>
            <td class="admTableTDLast" id="admTableTDtext" align="right">
                <xsl:choose>
                    <xsl:when test="@type='by_height'">
                        <xsl:value-of select="java:getString($dict_upload_file, 'MAX_IMAGE_WIDTH')"/>:
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="java:getString($dict_upload_file, 'REQUIRED_IMAGE_WIDTH')"/>:
                    </xsl:otherwise>
                </xsl:choose>
            </td>
            <td class="admTableTDLast" id="admTableTDtext">                        
                <input class="admWidth50" id="requiredWidth" type="text" name="requiredWidth" readonly="true">
                        <xsl:attribute name="value"><xsl:value-of select="@width"/></xsl:attribute>
                </input>
                (<xsl:value-of select="java:getString($dict_upload_file, 'READONLY')"/>)
            </td>
        </tr>
    </xsl:if>

    <xsl:if test="@height">
        <tr>
            <td class="admTableTDLast" id="admTableTDtext" align="right">
                <xsl:choose>
                    <xsl:when test="@type='by_width'">
                        <xsl:value-of select="java:getString($dict_upload_file, 'MAX_IMAGE_HEIGHT')"/>:
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="java:getString($dict_upload_file, 'REQUIRED_IMAGE_HEIGHT')"/>:
                    </xsl:otherwise>
                </xsl:choose>
            </td>
            <td class="admTableTDLast" id="admTableTDtext">                        
                <input class="admWidth50" id="requiredHeight" type="text" name="requiredHeigth" readonly="true" value="{@height}"/>
                (<xsl:value-of select="java:getString($dict_upload_file, 'READONLY')"/>)
            </td>
        </tr>
    </xsl:if>
</xsl:template>

<xsl:template match="negeso:upload-option[@mode='banner']">
    <!-- Banner upload option -->
    <tr>
        <td colspan="2">
                <input type="hidden" name="command" value="upload-banner-command"/>
                <input type="hidden" name="width" value="{@width}"/>
                <input type="hidden" name="height" value="{@height}"/> 
                <input type="hidden" name="mode" value="{@mode}"/><!-- forcibly set to have in request body -->
                    <input type="hidden" name="force_resize_mode" value="{@force-resize-mode}"/>
        </td>
    </tr>
    <xsl:apply-templates select="../negeso:folder-set"/>
    <!-- Choose file from disk -->
    <tr>
        <td  class="admTableTDLast" id="admTableTDtext" align="right">
            <xsl:value-of select="java:getString($dict_upload_file, 'FROM_DISK')"/>:
            <input id="diskOption" type="radio" name="fileSource" checked="on" value="disk" onClick="typeDisk()"/>
        </td>
        <td  class="admTableTDLast" id="admTableTDtext">
            <input id="selectedFile" class="admTextArea admWidth95perc" type="file" name="selectedFile" onKeyPress="return false;"/>
        </td>
    </tr>
    <!-- Choose existant file -->
    <tr>
        <td class="admTableTDLast" id="admTableTDtext" align="right">
            <xsl:value-of select="java:getString($dict_upload_file, 'CHOOSE_EXISTING_FILE')"/>:
            <input id="catalogOption" type="radio" name="fileSource" value="catalog" onClick="typeCatalog()"/>
        </td>
        <td class="admTableTDLast" id="admTableTDtext" nowap="nowap">
            <xsl:apply-templates select="../negeso:resource-set"/>
            <button class="admMainInp" style="width:81px;float:left;margin:-3px 0 0 3px;" name="mcButton" type="button" onClick="ChooseFromMediaCatalog(document.all.existentFileId)">
                <img src="/images/media_catalog/media_catalog_logo_browse.gif"></img>
            </button>
        </td>
    </tr>
    
    <!-- Resize  -->
    <tr>
        <td class="admTableTDLast" id="admTableTDtext" colspan="2">&#160;</td>
        </tr>
        <tr>
        <td class="admTableTDLast" id="admTableTDtext" align="right">
                <!--<xsl:value-of select="java:getString($dict_upload_file, 'ORIGINAL')"/>:-->
                Original:
                <input  type="radio" class="radio" name="resize_mode" value="none">
                    <xsl:if test = "@force-resize-mode='none'">
                        <xsl:attribute name="checked">true</xsl:attribute>
                    </xsl:if>
                </input>
            </td>
        <td class="admTableTDLast" id="admTableTDtext">                        
                <!--<xsl:value-of select="java:getString($dict_upload_file, 'RESIZE')"/>:
                <input  type="radio" class="radio" name="resize_mode" value="non_proportional">
                    <xsl:if test = "@force-resize-mode='non_proportional'">
                        <xsl:attribute name="checked">true</xsl:attribute>
                    </xsl:if>
                </input>-->
                Resize proportional:
                <input  type="radio" class="radio" name="resize_mode" value="proportional">
                    <xsl:if test = "@force-resize-mode='proportional'">
                        <xsl:attribute name="checked">true</xsl:attribute>
                    </xsl:if>
                </input>
                <!--&#160;&#160;
                <xsl:value-of select="java:getString($dict_upload_file, 'CROP')"/>:
                <input  type="radio" class="radio" name="resize_mode" value="crop">
                    <xsl:if test = "@force-resize-mode='crop'">
                        <xsl:attribute name="checked">true</xsl:attribute>
                    </xsl:if>
                </input>-->
            </td>
        </tr>

    <xsl:if test="@width">
        <tr>
            <td class="admTableTDLast" align="right">
                <xsl:choose>
                    <xsl:when test="@type='by_height'">
                        <xsl:value-of select="java:getString($dict_upload_file, 'MAX_IMAGE_WIDTH')"/>:
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="java:getString($dict_upload_file, 'REQUIRED_IMAGE_WIDTH')"/>:
                    </xsl:otherwise>
                </xsl:choose>
            </td>
            <td class="admTableTDLast">                        
                &#160;<input class="admWidth50" id="requiredWidth" type="text" name="requiredWidth" readonly="true">
                        <xsl:attribute name="value"><xsl:value-of select="@width"/></xsl:attribute>
                </input>
                (<xsl:value-of select="java:getString($dict_upload_file, 'READONLY')"/>)
            </td>
        </tr>
    </xsl:if>

    <xsl:if test="@height">
        <tr>
            <td class="admTableTDLast" align="right">
                <xsl:choose>
                    <xsl:when test="@type='by_width'">
                        <xsl:value-of select="java:getString($dict_upload_file, 'MAX_IMAGE_HEIGHT')"/>:
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="java:getString($dict_upload_file, 'REQUIRED_IMAGE_HEIGHT')"/>:
                    </xsl:otherwise>
                </xsl:choose>
            </td>
            <td class="admTableTDLast">
                &#160;<input class="admWidth50" id="requiredHeight" type="text" name="requiredHeigth" readonly="true" value="{@height}"/>
                (<xsl:value-of select="java:getString($dict_upload_file, 'READONLY')"/>)
            </td>
        </tr>
    </xsl:if>
</xsl:template>

<xsl:template match="negeso:upload-option[@mode='flash']">
    <!-- Image upload option -->
    <tr>
        <td colspan="2">
                <input type="hidden" name="command" value="upload-flash-command"/>
                <input type="hidden" name="mode" value="{@mode}"/>
                <input type="hidden" name="requiredWidth" value="{@width}"/>
                <input type="hidden" name="requiredHeight" value="{@height}"/> 
                <input type="hidden" name="type" value="{@type}"/>
                <!-- TODO!!! -->
                <!--<input type="hidden" name="is_strict" value="{@is-strict}"/>-->
        </td>
    </tr>
    <xsl:apply-templates select="../negeso:folder-set"/>
    <!-- Choose file from disk -->
    <tr>
        <td class="admTableTDLast" align="right">
            <xsl:value-of select="java:getString($dict_upload_file, 'FROM_DISK')"/>:
            <input id="diskOption" type="radio" name="fileSource" checked="on" value="disk" onClick="typeDisk()"/>
        </td>
        <td  class="admTableTDLast" colspan="5">
            <input id="selectedFile" class="admTextArea" type="file" name="selectedFile" onKeyPress="return false;"/>
        </td>
    </tr>
    <!-- Choose existant file -->
    <tr>
        <td class="admTableTDLast" align="right">
            <xsl:value-of select="java:getString($dict_upload_file, 'CHOOSE_EXISTING_FILE')"/>:
            <input id="catalogOption" type="radio" name="fileSource" value="catalog" onClick="typeCatalog()"/>
        </td>
        <td class="admTableTDLast">                     
            <xsl:apply-templates select="../negeso:resource-set"/>
            <button class="admNavChoose" style="width:81px;float:left;margin:-3px 0 0 3px;" name="mcButton" type="button" onClick="ChooseFlashFromMediaCatalog(document.all.existentFileId)">
              <img src="/images/media_catalog/media_catalog_logo_browse.gif"></img>
            </button>
        </td>
    </tr>
    <xsl:if test="@width">
        <tr>
            <td class="admTableTDLast" align="right">
                <xsl:choose>
                    <xsl:when test="@type='strict_height'">
                        <xsl:value-of select="java:getString($dict_picture_frame_adds, 'ADV_PF.MAX_FLASH_WIDTH')"/>:
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="java:getString($dict_picture_frame_adds, 'ADV_PF.REQUIRED_FLASH_WIDTH')"/>:
                    </xsl:otherwise>
                </xsl:choose>
            </td>
            <td class="admTableTDLast">
                <input
                class="admWidth50" id="requiredWidth" type="text" name="requiredWidth" readonly="true">
                    <xsl:attribute name="value">
                        <xsl:value-of select="@width"/>
                    </xsl:attribute>
                </input> (<xsl:value-of select="java:getString($dict_upload_file, 'READONLY')"/>)
                
                <span style="padding-left:28px;"><xsl:value-of select="java:getString($dict_picture_frame_adds, 'ADV_PF.FLASH_WIDTH')"/>:</span>&#160;&#160;<input
                    class="admWidth50" id="width" type="text" name="width">
                    <xsl:attribute name="value">
                        <xsl:value-of select="@width"/>
                    </xsl:attribute>
                </input>
            </td>
        </tr>
    </xsl:if>

    <xsl:if test="@height">
        <tr>
            <td class="admTableTDLast" align="right">
                <xsl:choose>
                    <xsl:when test="@type='strict_width'">
                        <xsl:value-of select="java:getString($dict_picture_frame_adds, 'ADV_PF.MAX_FLASH_HEIGHT')"/>:
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="java:getString($dict_picture_frame_adds, 'ADV_PF.REQUIRED_FLASH_HEIGHT')"/>:
                    </xsl:otherwise>
                </xsl:choose>
            </td>
            <td class="admTableTDLast">
                <input class="admWidth50" id="requiredHeight" type="text" name="requiredHeigth" readonly="true" value="{@height}"/>
                        (<xsl:value-of select="java:getString($dict_upload_file, 'READONLY')"/>)
                <span style="padding-left:28px;"><xsl:value-of select="java:getString($dict_picture_frame_adds, 'ADV_PF.FLASH_HEIGHT')"/>:</span>&#160;<input 
                class="admWidth50" id="height" type="text" name="height" value="{@height}"/>
            </td>
        </tr>
    </xsl:if>
</xsl:template>


<xsl:template match="negeso:upload-option[@mode='doc']">
    <!-- File upload option -->
    <tr>
        <td colspan="2">
                <input type="hidden" name="command" value="upload-document-command"/>
                <input type="hidden" name="mode" value="{@mode}"/>
         </td>
    </tr>
    <xsl:apply-templates select="../negeso:folder-set"/>
    <!-- Choose file from disk -->
    <tr>
        <td class="admTableTDLast" align="right">
            <xsl:value-of select="java:getString($dict_upload_file, 'FROM_DISK')"/>:
            <input id="diskOption" type="radio" name="fileSource" checked="on" value="disk" onClick="typeDisk()"/>
        </td>
        <td class="admTableTDLast">
            <input id="selectedFile" class="admTextArea" type="file" name="selectedFile" onKeyPress="return false;"/>
        </td>
    </tr>
    <!-- Choose existant file -->
    <tr>
        <td class="admTableTDLast" align="right">
            <xsl:value-of select="java:getString($dict_upload_file, 'CHOOSE_EXISTING_FILE')"/>:
            <input id="catalogOption" type="radio" name="fileSource" value="catalog" onClick="typeCatalog()"/>
        </td>
        <td class="admTableTDLast" >
            <xsl:apply-templates select="../negeso:resource-set"/>
        </td>
    </tr>
</xsl:template>

<xsl:template match="negeso:upload-option[@mode='file']">
    <!-- File upload option -->
    <tr>
        <td colspan="2">
                <input type="hidden" name="command" value="upload-file-command2"/>
                <input type="hidden" name="mode" value="{@mode}"/>
         </td>
    </tr>
    <xsl:apply-templates select="negeso:folder-set"/>
    <!-- Choose file from disk -->
    <tr>
        <td class="admTableTDLast" align="right">
            <xsl:value-of select="java:getString($dict_upload_file, 'FROM_DISK')"/>:
            <input id="diskOption" type="radio" name="fileSource" checked="on" value="disk" onClick="typeDisk()"/>
        </td>
        <td class="admTableTDLast">
            <input id="selectedFile" class="admTextArea admWidth95perc" type="file" name="selectedFile" onKeyPress="return false;"/>
        </td>
    </tr>
    <!-- Choose existant file -->
    <tr>
        <td class="admTableTDLast" align="right">
            <xsl:value-of select="java:getString($dict_upload_file, 'CHOOSE_EXISTING_FILE')"/>:
            <input id="catalogOption" type="radio" name="fileSource" value="catalog" onClick="typeCatalog()"/>
        </td>
        <td class="admTableTDLast">
            <xsl:apply-templates select="../negeso:resource-set"/>
        </td>
    </tr>
</xsl:template>



<xsl:template match="negeso:folder-set">
    <tr>
        <td  class="admTableTDLast" align="right">
            <xsl:value-of select="java:getString($dict_upload_file, 'DESTINATION_FOLDER')"/>:
        </td>
        <td  class="admTableTDLast" colspan="5" style="padding:10px;">
            <select id="destFolderId" name="workfolder" class="admTextArea" >
                <xsl:apply-templates select="negeso:folder"/>
            </select>
        </td>
    </tr>
</xsl:template>

<xsl:template match="negeso:folder">
    <xsl:variable name="workfolder" select="concat('media/',/negeso:page/negeso:upload-option/@working-folder)"/>
    <OPTION>
        <xsl:if test="concat($workfolder,'/')=@path">
            <xsl:attribute name="selected">selected</xsl:attribute>
        </xsl:if>
        <!-- File upload option -->
        <xsl:attribute name="VALUE"><xsl:value-of select="@path"/></xsl:attribute>
        <xsl:value-of select="@path"/>
    </OPTION>
</xsl:template>

<!-- ******************* NEGESO ERROR MESSAGE *************************** -->
<xsl:template match="negeso:error-messages">
    <table cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td style="padding: 10px 3px 0 5px;">
                <b><xsl:value-of select="java:getString($dict_common, 'ERROR')"/>:</b>
            </td>
        </tr>
        <script>
            var error = '<xsl:value-of select="java:getString($dict_common, 'ERROR')"/>';
        </script>
        <tr>
            <td>
                <table cellspacing="0" cellpadding="0" width="100%">
                    <xsl:apply-templates select="negeso:message"/>
                </table>
            </td>
        </tr>
    </table>
</xsl:template>

<!-- ******************* NEGESO USER MESSAGE *************************** -->
<xsl:template name="negeso:user-messages">
    <table cellspacing="0" cellpadding="0" class="admNavPanel admSpaceSmall">
        <tr>
            <td class="admTableTDLast" width="10%">
                <xsl:value-of select="java:getString($dict_media_catalog, 'MESSAGE')"/>:
            </td>
            <td>
                <table cellspacing="0" cellpadding="0">
                    <xsl:apply-templates select="negeso:message"/>
                </table>
            </td>
       </tr>
    </table>
</xsl:template>

<xsl:template match="negeso:message">
    <script>
        var message = '<xsl:value-of select="@text"/>';
        if (error){
            if (message){
                var iframe = document.getElementById('upload_target');
                iframe.parentNode.removeChild(iframe);
                alert(error + ' : ' + message);
                window.close();
                caller.document.getElementById('upload_target').src = 'about:blank';
            }
        }

    </script>
    <tr>
        <td class="admTableTDLast">
              <xsl:value-of select="@text"/>
        </td>
    </tr>
</xsl:template>

<xsl:template match="negeso:resource-set">
    <select id="existentFileId" name="existentFile" disabled="true" class="admTextArea" style="width:212px;">
        <xsl:choose>
                 <xsl:when test="@is-optimized='true'">
                        <xsl:value-of select="text()" disable-output-escaping="yes"/>
                 </xsl:when>
                 <xsl:otherwise>
                <xsl:for-each select="negeso:resource">
                    <OPTION>
                        <xsl:attribute name="VALUE"><xsl:value-of select='@path'/></xsl:attribute>
                        <xsl:value-of select='@path'/>
                    </OPTION>
                </xsl:for-each>
                 </xsl:otherwise>
        </xsl:choose>
    </select>
</xsl:template>

<xsl:template name="choose-title">
    <xsl:choose>
        <xsl:when test="/negeso:page/@title = 'Upload thumbnail image'">
            <xsl:value-of select="java:getString($dict_upload_file, 'UPLOAD_THUMBNAIL_IMAGE')"/>
        </xsl:when>
        <xsl:when test="/negeso:page/@title = 'Upload image'">
            <xsl:value-of select="java:getString($dict_upload_file, 'UPLOAD_IMAGE')"/>
        </xsl:when>
        <xsl:when test="/negeso:page/@title = 'Upload document'">
            <xsl:value-of select="java:getString($dict_upload_file, 'UPLOAD_DOCUMENT')"/>
        </xsl:when>
        <xsl:when test="/negeso:page/@title = 'Upload file'">
                     <xsl:value-of select="java:getString($dict_picture_frame_adds, 'ADV_PF.UPLOAD_FILE')"/>
        </xsl:when>
        <xsl:when test="/negeso:page/@title = 'Upload flash'">
                     <xsl:value-of select="java:getString($dict_picture_frame_adds, 'ADV_PF.UPLOAD_FLASH')"/>
        </xsl:when>
        <xsl:otherwise>
            &#160;
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>

</xsl:stylesheet>
