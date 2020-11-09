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
  @author       Volodymyr Snigur
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

<!-- Include/Import -->
<xsl:include href="/xsl/negeso_body.xsl"/>
<!-- NEGESO JAVASCRIPT Temaplate -->    
<xsl:template name="java-script">
    <script language="JavaScript">
        var s_MustSelect = "<xsl:value-of select="java:getString($dict_media_catalog, 'MUST_SELECT')"/>";

    <xsl:text disable-output-escaping="yes">
        <![CDATA[
    
        var imageArray = new Array();
    
        function onLoadWindow(){
            window.name='UploadWindow';
        }
    
        function checkUploadForm() {
            window.name='UploadWindow';
            return true;
        }

        function OnCancel()
        {
            //window.returnValue = null;
            window.name='UploadWindow';
            window.close();
        }

        function OnBack()
        {
            alert ("Back");
            window.history.back();
        }

        function onOk(){
            var caller = new Object();
            if(window.dialogArguments) {
                caller = window.dialogArguments;
            } else {
               caller = window.opener;
            }

            window.name='UploadWindow';
        
            //alert("OK");
            caller.returnValue = new Object();
            caller.returnValue.resCode = "OK";
            
            imageArray = addImages();
            caller.returnValue.images = imageArray;
            
            //var i;
            //for ( i = 0; i < window.returnValue.images.length; i++ ){
            //  alert( window.returnValue.images[i].imageName );            
            //}
            var params = window.location.search.match(/([^&?=]+=[^&]+)/gi);
            if (params){
                var cat_id = 0;
                var folder_id = 0;
                for (var i=0; i < params.length; i++){
                    var arr_par = params[i].split("=");
                    if(arr_par[0] == 'working_folder_id')
                        folder_id = arr_par[1];                 
                }
            }
            caller.returnValuePhotos(folder_id);
            window.close();
        }

        if (typeof attachEvent !== 'undefined'){
            attachEvent ("onload", resizeDialogWindow); //resize dialog window height;
        } else {
            addEventListener("load", resizeDialogWindow);
        }
        ]]>
        </xsl:text>
    </script>
</xsl:template>


<!-- MAIN ENTRY -->
<xsl:template match="/">
<html>
<head>
    <title><xsl:value-of select="java:getString($dict_upload_file, 'UPLOAD_THUMBNAILS')"/></title>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript"  src="/script/jquery.min.js"/>
    <script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
    <script type="text/javascript" src="/script/common_functions.js"></script>
    <script language="JavaScript" src="/script/media_catalog.js" type="text/javascript">/**/</script>
    <xsl:call-template name="java-script"/>
</head>
<body onLoad="onLoadWindow()" class="massUpload">
    <!-- NEGESO HEADER -->
    <form name="opratateForm" method="post" action="" enctype="multipart/form-data"
                    onSubmit="return checkUploadForm()"  target="UploadWindow">
        
        <xsl:call-template name="NegesoBody">
            <xsl:with-param name="helpLink">
                <xsl:text>/admin/help/cph1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
            </xsl:with-param>        
        </xsl:call-template>

        <table cellpadding="0" cellspacing="0"  width="764px" align="center" class="Buttons">
            <tr>
                <td>
                    <div class="admBtnGreenb">
                        <div class="imgL"></div>
                        <div>
                            <xsl:choose>
                                <xsl:when test="//negeso:uplad-form[@render-mode='show-form']">
                                    <input type="submit" id="submitBtnId" name="submitBtn">
                                        <xsl:attribute name="value">
                                            <xsl:value-of select="java:getString($dict_common, 'SUBMIT')"/>
                                        </xsl:attribute>
                                    </input>        
                                </xsl:when>
                                <xsl:otherwise>
                                    <input name="submitBtn" onClick="onOk()" style="width:30px;">
                                        <xsl:attribute name="value">
                                            <xsl:value-of select="java:getString($dict_common, 'OK')"/>
                                        </xsl:attribute>
                                    </input>
                                </xsl:otherwise>
                            </xsl:choose>
                        </div>
                        <div class="imgR"></div>
                    </div>
                </td>
            </tr>
        </table>
    
    </form>    
    
    <script>
        Cufon.now();
        Cufon.replace('.admNavPanelFont', { fontFamily: 'Orator Std' });
        Cufon.replace('.admTDtitles', { fontFamily: 'Orator Std' });
    </script>
</body>
</html>
</xsl:template>

<xsl:template match="negeso:page"  mode="admContent">
    <table cellspacing="0" cellpadding="0" border="0" align="center" width="100%" class="admTable" style="background-image:none;">
        <tr>
            <td align="center" class="admNavPanelFont" colspan="2">
                <!-- Title -->
                <xsl:call-template name="tableTitle">
                    <xsl:with-param name="headtext">
                        <xsl:value-of select="java:getString($dict_upload_file, 'UPLOAD_THUMBNAILS')"/>
                    </xsl:with-param>
                </xsl:call-template>
            </td>
        </tr>        
    </table>
    
    <!-- CONTENT -->
    <xsl:apply-templates select="negeso:uplad-form"/>                    
    
</xsl:template>


<xsl:template match="negeso:uplad-form[@render-mode='show-form']">
    <input type="hidden" name="file_set_amount" value="{@file-set-amount}"/>
    <input type="hidden" name="action" value="upload"/>
    <input type="hidden" name="height" value="{@height}"/>
    <input type="hidden" name="width" value="{@width}"/>
    <input type="hidden" name="is_strict" value="{@is-strict}"/>
    <input type="hidden" name="resize_by" value="{@resize-by}"/>
    <input type="hidden" name="type" value="{@type}"/>
    <input type="hidden" name="working_folder" value="{@working-folder}"/>
    <input type="hidden" name="upload_params_id" value="{@upload-params-id}"/>

    <table cellspacing="0" cellpadding="0" width="100%" class="admTable">
        <tr>
            <td class="admTableTDLast" align="center" colspan="2">
                <table cellpadding="0" cellspacing="0">
                    <!-- Choose file from disk -->
                    <xsl:for-each select="negeso:file-set">
                        <xsl:for-each select="negeso:file">
                            <tr>
                                <td class="admRight" width="30%" height="30">
                                    <xsl:value-of select="java:getString($dict_media_catalog, 'CHOOSE_FILE')"/>:
                                </td>
                                <td class="admLeft"  width="70%">
                                    <input class="admTextArea admWidth95perc" type="file" name="file_{@id}" onKeyPress="return false;" readonly="true"/>
                                </td>
                            </tr>
                        </xsl:for-each>
                    </xsl:for-each>
                </table>
            </td>
        </tr>
        <tr>
            <th class="admTableTD admRight">
                <xsl:value-of select="java:getString($dict_upload_file, 'WORKING_FOLDER')"/>
            </th>
            <td class="admTableTDLast admLeft admZero">
                <xsl:value-of select="@working-folder"/>
            </td>
        </tr>
        <tr>
            <th class="admTableTDLast admCenter admZero" colspan="2">
                <xsl:value-of select="java:getString($dict_upload_file, 'RESIZE')"/>:
                <input  type="radio" class="radio" name="resize_method" value="non_proportional">
                    <xsl:if test = "@resize-method='non_proportional'">
                        <xsl:attribute name="checked">true</xsl:attribute>
                    </xsl:if>
                </input>
                &#160;&#160;
                <xsl:value-of select="java:getString($dict_upload_file, 'RESIZE_PROPORTIONAL')"/>:
                <input  type="radio" class="radio" name="resize_method" value="proportional">
                    <xsl:if test = "@resize-method='proportional'">
                        <xsl:attribute name="checked">true</xsl:attribute>
                    </xsl:if>
                </input>
                &#160;&#160;
                <xsl:value-of select="java:getString($dict_upload_file, 'CROP')"/>:
                <input  type="radio" class="radio" name="resize_method" value="crop">
                    <xsl:if test = "@resize-method='crop'">
                        <xsl:attribute name="checked">true</xsl:attribute>
                    </xsl:if>
                </input>
            </th>
        </tr>
        <xsl:if test="@width">
            <tr>
                <th class="admTableTD admRight">
                    <xsl:choose>
                        <xsl:when test="@type='by_height'">
                            <xsl:value-of select="java:getString($dict_upload_file, 'MAX_IMAGE_WIDTH')"/>:
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="java:getString($dict_upload_file, 'REQUIRED_IMAGE_WIDTH')"/>:
                        </xsl:otherwise>
                    </xsl:choose>
                </th>
                <td class="admTableTDLast admLeft admZero">
                    &#160;<input class="admWidth50" id="requiredWidth" type="text" name="requiredWidth" readonly="true" value="{@width}"/>
                    (<xsl:value-of select="java:getString($dict_upload_file, 'READONLY')"/>)
                </td>
            </tr>
        </xsl:if>

        <xsl:if test="@height">
            <tr>
                <th class="admTableTD admRight">
                    <xsl:choose>
                        <xsl:when test="@type='by_width'">
                            <xsl:value-of select="java:getString($dict_upload_file, 'MAX_IMAGE_HEIGHT')"/>:
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="java:getString($dict_upload_file, 'REQUIRED_IMAGE_HEIGHT')"/>:
                        </xsl:otherwise>
                    </xsl:choose>
                </th>
                <td class="admTableTDLast admLeft admZero">
                    &#160;<input class="admWidth50" id="requiredHeight" type="text" name="requiredHeigth" readonly="true" value="{@height}"/>
                    (<xsl:value-of select="java:getString($dict_upload_file, 'READONLY')"/>)
                </td>
            </tr>
        </xsl:if>
        <tr>
            <td class="admTableFooter">&#160;</td>
        </tr>
    </table>
</xsl:template>

<xsl:template match="negeso:uplad-form[@render-mode='result']">
    <input type="hidden" name="file_set_amount" value="{@file-set-amount}"/>
    <input type="hidden" name="action" value="upload"/>
    <input type="hidden" name="width" value="{@width}"/>
    <input type="hidden" name="height" value="{@height}"/>
    <input type="hidden" name="width" value="{@width}"/>
    <input type="hidden" name="is_strict" value="{@is-strict}"/>
    <input type="hidden" name="resize_by" value="{@resize-by}"/>
    <input type="hidden" name="resize_method" value="{@resize-method}"/>
    <input type="hidden" name="type" value="{@type}"/>
    <input type="hidden" name="working_folder" value="{@working-folder}"/>
    <input type="hidden" name="upload_params_id" value="{@upload-params-id}"/>

    <xsl:for-each select="negeso:file-set">
        <script language="JavaScript">
            function addImages(){
            var imageObj;
            var iArray = new Array();
            <xsl:for-each select="negeso:file">
                <xsl:if test="@result='OK'">
                    //alert("asdasd");
                    imageObj = new Object();
                    imageObj.imageName = "<xsl:value-of select="@image-path"/>";
                    imageObj.imageWidth = "<xsl:value-of select="@image-width"/>";
                    imageObj.imageHeight = "<xsl:value-of select="@image-height"/>";

                    imageObj.thumbnailName = "<xsl:value-of select="@th-path"/>";
                    imageObj.thumbnailWidth = "<xsl:value-of select="@th-width"/>";
                    imageObj.thumbnailHeight = "<xsl:value-of select="@th-height"/>";
                    iArray.push(imageObj);
                </xsl:if>
            </xsl:for-each>
            return iArray;
            }
        </script>
    </xsl:for-each>

    <table cellspacing="0" cellpadding="0" width="100%" class="admTable" style="background-image:none;">
        <tr>
            <td class="admTableTDLast admRight admBold" width="50%">
                <xsl:value-of select="java:getString($dict_upload_file, 'WORKING_FOLDER')"/>
            </td>
            <td class="admTableTDLast admLeft"  width="50%"> 
                <xsl:value-of select="@working-folder"/>
            </td>
        </tr>
    </table>
        
    <table cellspacing="0" cellpadding="0" width="100%" class="admTable">
        <xsl:if test="count(negeso:file-set/negeso:file) = 0">
            <tr>
                <th class="admTableTDLast">
                    <xsl:value-of select="java:getString($dict_upload_file, 'NO_IMAGE_UPLOADED')"/>
                </th>
            </tr>
        </xsl:if>

        <xsl:for-each select="negeso:file-set">
            <xsl:for-each select="negeso:file">
                <tr>
                    <td class="admTableTDLast admRight admBold" width="5%">
                        &#160;<xsl:value-of select="java:getString($dict_common, 'IMAGE')"/>:
                    </td>
                        
                    <xsl:choose>
                        <xsl:when test="@result='error'">
                            <td class="admTableTD">
                                <xsl:value-of select="@image-name"/>: &#160;
                                <xsl:for-each select="negeso:errors">
                                    <xsl:for-each select="negeso:error">
                                        <xsl:value-of select="text()"/>
                                        <br/>
                                    </xsl:for-each>
                                </xsl:for-each>
                            </td>
                            <td class="admTableTDLast" colspan="2">
                                <xsl:value-of select="@error"/>
                            </td>
                            <td class="admTableTDLast admErrorMessage">
                                <xsl:value-of select="java:getString($dict_common, 'ERROR')"/>
                            </td>
                        </xsl:when>
                        <xsl:otherwise>
                            <td class="admTableTD">
                                <xsl:value-of select="@image-name"/>
                                (<xsl:value-of select="@image-width"/> x  <xsl:value-of select="@image-height"/>)
                                &#160;<xsl:value-of select="@image-size"/>
                            </td>

                            <td class="admTableTDLast admRight admBold" width="5%">
                                <xsl:value-of select="java:getString($dict_upload_file, 'THUMBNAIL')"/>
                            </td>

                            <td class="admTableTD">
                                <xsl:value-of select="@th-name"/>
                                (<xsl:value-of select="@th-width"/> x  <xsl:value-of select="@th-height"/>)
                                &#160;<xsl:value-of select="@th-size"/>
                            </td>
                            <td class="admTableTDLast admLeft admStatusMessage">
                                <xsl:value-of select="java:getString($dict_common, 'OK')"/> &#160;
                            </td>
                        </xsl:otherwise>
                    </xsl:choose>
                </tr>
            </xsl:for-each>
        </xsl:for-each>
        <tr>
            <td class="admTableFooter" colspan="5">&#160;</td>
        </tr>
    </table>
</xsl:template>

</xsl:stylesheet>
