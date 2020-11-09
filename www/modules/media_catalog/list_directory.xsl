<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${Id}     
 
  Copyright (c) 2004 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  @version      $Revision$
  @author       Olexiy.Strashko
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

    <!-- Include/Import -->
    <xsl:include href="modules/media_catalog/commons.xsl"/>
    <xsl:include href="modules/media_catalog/view_modes.xsl"/>
    <xsl:include href="/xsl/negeso_body.xsl"/>

    <!-- MAIN ENTRY -->
    <xsl:template match="/">
        <xsl:text disable-output-escaping="yes"> <![CDATA[<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">]]></xsl:text> 
         <html>
            <head>
                <!--    <title><xsl:value-of select="/negeso:page/@title"/></title> -->
                <title>
                    <xsl:value-of select="java:getString($dict_media_catalog, 'MEDIA_CATALOG')"/>
                </title>
                <META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=utf-8"/>
                <link href="/css/admin_style.css?v=1" rel="stylesheet" type="text/css"/>
                <link rel="stylesheet" type="text/css" href="/css/jquery-ui.custom.css"/>
                <link rel="stylesheet" type="text/css" href="/css/uploadify.css"/>

                <script src="/script/jquery.min-1.10.2.js" />
                <script src="/script/jquery.ui.widget.js" />
                <script src="/script/jquery.iframe-transport.js" />
                <script src="/script/jquery.fileupload.js" />

                <!--<script type="text/javascript" src="/script/jquery.min.js"></script>				-->
                <!--<script type="text/javascript" src="/script/jquery-ui.custom.min.js" />-->
                <!--<script type="text/javascript" src="/script/jquery.uploadify.js"></script>                -->
                <!--<script type="text/javascript" src="/script/cufon-yui.js"></script>-->
                <!--<script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>-->
                <script type="text/javascript" src="/script/common_functions.js" />
                <!--<script type="text/javascript" src="/script/common_admin.js" />-->
                <script language="JavaScript1.2" src="/script/security.js" type="text/javascript"/>
                <link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css" />
                <link rel="stylesheet" href="/css/jquery.fileupload.css" />

                <meta http-equiv="X-UA-Compatible" content="IE=8"/>
                
                
                <xsl:call-template name="java-script"/>
            </head>
            <body style="behavior: url(/script/webservice.htc) url(/script/clientmanager.htc)" id="ClientManager">
                <xsl:choose>
                    <xsl:when test="/negeso:page/negeso:browser-config/@action-mode='chooser'">
                        <!-- NEGESO HEADER -->
                        <xsl:call-template name="NegesoBody">
                            <xsl:with-param name="helpLink">
                                <xsl:text>/admin/help/cmc1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
                            </xsl:with-param>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:attribute name="OnLoad">onLoadWindow()</xsl:attribute>
                        <!-- NEGESO HEADER -->
                        <xsl:call-template name="NegesoBody">
                            <xsl:with-param name="helpLink"> 
                                <xsl:text>/admin/help/cmc1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
                            </xsl:with-param>
                        </xsl:call-template>
                    </xsl:otherwise>
                </xsl:choose>
                <script>
                    <!--Cufon.now();-->
                    <!--Cufon.replace('.admNavPanelFont', { fontFamily: 'Orator Std' });-->
                    <!--Cufon.replace('.admTDtitles', { fontFamily: 'Orator Std' });-->
                </script>
            </body>
        </html>
    </xsl:template>

    <!-- NEGESO JAVASCRIPT Temaplate -->
    <xsl:template name="java-script">
        <script language="JavaScript">
            var s_FileIsNotChosen = "<xsl:value-of select="java:getString($dict_media_catalog, 'FILE_IS_NOT_CHOSEN')"/>";
            var s_DeleteFileConfirmation = "<xsl:value-of select="java:getString($dict_media_catalog, 'DELETE_FILE_CONFIRMATION')"/>";
            var s_MustSelect = "<xsl:value-of select="java:getString($dict_media_catalog, 'MUST_SELECT')"/>";
            var s_UnableToCreateEmptyFolder = "<xsl:value-of select="java:getString($dict_media_catalog, 'EMPTY_FOLDER')"/>";
            var s_InvalidFolderName = "<xsl:value-of select="java:getString($dict_media_catalog, 'INVALID_FOLDER_NAME')"/>";

            <xsl:text disable-output-escaping="yes">
            <![CDATA[            
           var CATALOG_WIDTH = 810; 
            
           function goBack() {
                    window.close();
                }


            /* OnLoad page event handler.
             * Changes window size to fit Media Catalog widget.
             */
            function onLoadWindow(){
                try{
                    window.name='CatalogWindow';
                    if ( document.body.offsetWidth < CATALOG_WIDTH ){
                        window.moveTo( 0,0 ); 
                        // resize to full screen
                        window.resizeTo( screen.availWidth, screen.availHeight );
                    }
                }
                catch(e){
                    // unable to resize window, QUIETLY! pass resizing
                    // alrert("Unable to risize:" + e);
                }
            }
            
            function getFilename(path){
                if (path == null) {
                    return null;
                }
                strings = path.split("\\");
                if (strings.length > 0){
                    return strings[strings.length - 1];
                }
                return "";
            }
            
            function isFileExists(folder, filename){
                var callObj = ClientManager.CMSCreateAuxCallOptions();
                callObj.setCommand("mc-check-file-existence-command");
                callObj.setParam("folder", folder);
                callObj.setParam("file_name", filename);
               
                var result = ClientManager.CMSUpdateEntity(callObj);

                if ( !result.error ) {
                    var tree = StringUtil.asTree(result.value);
                    var resName = tree.selectSingleNode("/negeso:result");
                    var status = resName.getAttribute("status");
                    if (status == "OK"){
                        if (resName.getAttribute("exists") == "true"){
                            return true;
                        }
                    }
                    else{
                        alert(resName.getAttribute("message"));
                    }
                }
                else{
                    alert(result.error);
                }
                return false;
            }
            
            function checkUploadForm(parentFolder) {
                window.name="CatalogWindow";

                if (
                    (document.uploadFileForm.uploadedFile.value == null) || 
                    (document.uploadFileForm.uploadedFile.value == ""))
                {
                    alert(s_FileIsNotChosen);
                    return false;
                }

                if ( isFileExists(parentFolder, getFilename(document.uploadFileForm.uploadedFile.value)) ) {
                    if ( confirm("File exists, are you sure you want to overwrite existing file") ){
                        //window.location.reload(true);
                        document.uploadFileForm.submit();
                    }
                    return false;
                }
                document.uploadFileForm.submit();
                return true;
            }

            window.returnValue = null;

            function chooseFileAction() {
            var caller = new Object();
            if(window.dialogArguments) {
		            caller = window.dialogArguments;
	            } 
	            else {
	                caller = window.opener;
	            }
                window.name="CatalogWindow";
                if (document.getElementById("choosenFile").value=="") 
                {
                    alert(s_MustSelect);
                }
                else
                { 
                    try {
						caller.vURL = "/" + document.getElementById("choosenFile").value;
						caller.rURL = document.getElementById("choosenFile").value;
						window.returnValue = "OK";
					} catch (e) {}
					
					
					try {
						
						if(caller.MediaCatalog.chooseCallback){
							caller.MediaCatalog.chooseCallback(document.getElementById("choosenFile").value);

						} else if (caller.MediaCatalog.is_active) {
                            caller.MediaCatalog.set_file_URL(document.getElementById("choosenFile").value, caller.MediaCatalog.insert_mode);
                            caller.MediaCatalog.win.focus();
                        } else {
                            if (caller.MediaCatalog.insert_link_mode == "image")
                               caller.tinyMCE.execCommand("mceNegesoAdvImageMediaHandler",false,"/"+document.getElementById("choosenFile").value);
                            else if (caller.MediaCatalog.insert_link_mode == "link_to_image")
                                caller.tinyMCE.execCommand("mceNegesoAdvLinkMediaHandler",false,"/"+document.getElementById("choosenFile").value);
                        }
					} catch (e) {}
                    window.close();
                }
            }

			function cancelFileAction() {
				window.name="CatalogWindow";
				window.returnValue = null;
				window.close();
			}
	
			function selectFile(selectedFile) {
				window.name="CatalogWindow";
				document.getElementById("choosenFile").value = selectedFile;
				return false;
			}
	
	        function changeViewMode() {
	            window.name="CatalogWindow";
	            return true;
	        }
	
	        function changeSortMode() {
	            window.name="CatalogWindow";
	            return true;
	        }

            function isToken(textObj) {
                var newValue = textObj.value;
                var newLength = newValue.length;
                var extraChars="0123456789_";
                var search;
                for (var i = 0; i != newLength; i++) {
                    aChar = newValue.substring(i, i+1);
                    aChar = aChar.toUpperCase();
                    search = extraChars.indexOf(aChar);
                    if (search ==  -1 && (aChar <"A" || aChar >"Z")) {
                        return false;
                    }
                }
                return true;
            }

            function changeContainer(containerId, objectId){
                Security.selectContainerDialog(containerId, "manager", function(res){
                    changeContainerCallback(res, objectId)
                });
            }

            function changeContainerCallback(res, objectId){
                if (res != null ){
                  var containerId = (res.containerId == null || res.containerId == "") ? null : res.containerId;
                  var paramsStr = "&objectId="+ objectId +"&tableId=mc_folder&containerId=" + containerId;                  
                  $.ajax({
                        type: 'GET',
                        url: "/admin/?command=mc-set-container-command" + paramsStr,
                        dataType: "xml",         
                        success: function (xml) {
                            $('#td_'+objectId).text(res.containerTitle);
                        },
                        error: function (xml) {
                            $('#td_'+objectId).text(res.containerTitle);
                }
                  });
                }
            }




            function getResourceInfo(resourceName, el) {
                window.name="CatalogWindow";
                if (
                    (resourceName == null) || 
                    (resourceName == ""))
                {
                    alert("Sorry, bad source file.");
                    return false;
                }
                
                document.operateForm.command.value = "get-resource-info-face";
                document.operateForm.victim.value = resourceName;
                document.operateForm.submit();
            }
        
            function locateFolder(currentDir, newDir) {

                window.name="CatalogWindow";
                document.operateForm.command.value = "list-directory-command";
                document.operateForm.currentDir.value = newDir;
                document.operateForm.parentDir.value = currentDir;
                document.operateForm.submit();
            }

            function renameResource(resourceName) {
                window.name="CatalogWindow";
                if (
                    (resourceName == null) || 
                    (resourceName == ""))
                {
                    alert("Sorry, bad source file.");
                    return false;
                }
                document.operateForm.command.value = "get-rename-resource-face";
                document.operateForm.victim.value = resourceName;
                document.operateForm.submit();
            }
            
            function changeShowMode() {
            	window.name="CatalogWindow";
            	if (document.operateForm2.showHiddenFiles.value == null || document.operateForm2.showHiddenFiles.value == "" || 
            		document.operateForm2.showHiddenFiles.value == "true"){
            		document.operateForm2.showHiddenFiles.value = "false";
            	}else{
            		document.operateForm2.showHiddenFiles.value = "true";
            	}
            	document.operateForm2.command.value = "list-directory-command";
            	document.operateForm2.submit();
            }

            function moveResource(resourceName) {
                window.name="CatalogWindow";
                if (
                    (resourceName == null) || 
                    (resourceName == ""))
                {
                    alert("Sorry, bad source file.");
                    return false;
                }
                document.operateForm.command.value = "get-move-resource-face";
                document.operateForm.victim.value = resourceName;
                document.operateForm.submit();
            }
    
            function removeResource(resourceName) {
                window.name="CatalogWindow";
                res = confirm(s_DeleteFileConfirmation + " '" + resourceName + "'?");
                if (res == true){
                    if (
                        (resourceName == null) || 
                        (resourceName == ""))
                    {
                        alert("Sorry, bad source file.");
                        return false;
                    }
	                document.operateForm.command.value = "remove-resource-command";
                    document.operateForm.victim.value = resourceName;
                    document.operateForm.submit();
                }
            }


            function createFolderAction() {
                window.name="CatalogWindow";
                if (document.operateForm.newFolderName.value == null){
                    alert(s_UnableToCreateEmptyFolder);
                    return false;
                }
                if (document.operateForm.newFolderName.value == ""){
                    alert(s_UnableToCreateEmptyFolder);
                    return false;
                }
                if (!isToken(document.operateForm.newFolderName)){
                    alert(s_InvalidFolderName);
                    return false;
                }
                document.operateForm.command.value = "create-folder-command";
                document.operateForm.submit();
                return true;
            }
            
            function uploadFile(parentFolder) {
                window.name="CatalogWindow";

                if (
                    (document.operateForm.uploadedFile.value == null) || 
                    (document.operateForm.uploadedFile.value == ""))
                {
                    alert(s_FileIsNotChosen);
                    return false;
                }

                if ( isFileExists(parentFolder, getFilename(document.uploadFileForm.uploadedFile.value)) ) {
                    if ( confirm("File exists, are you sure you want to overwrite existing file") ){
		                document.operateForm.command.value = "upload-file-command";
                        document.operateForm.submit();
                    }
                    return false;
                }
                document.operateForm.command.value = "upload-file-command";
                document.operateForm.submit();
                return true;
            }
    

        ]]>
        </xsl:text>
        </script>
    </xsl:template>


    <!-- NEGESO PAGE Temaplate -->
    <xsl:template match="negeso:page" mode="admContent">
        <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable"  width="100%">
            <tr>
                <td style="width:auto; height:auto; padding-left:20px;">
                    <xsl:call-template name="location-message"/>
                </td>
            </tr>
            <tr>
                <td align="center" class="admNavPanelFont" >
                    <xsl:call-template name="media-catalog-header"/>
                </td>
            </tr>
            <tr>
                <td >
                    <table cellpadding="0" cellspacing="0"  width="100%">
                        <tr>
                            <td colspan="2" >
                                <xsl:apply-templates select="negeso:user-messages"/>
                                <xsl:apply-templates select="negeso:error-messages"/>
                            </td>
                        </tr>
                        <tr>
                            <td>
                            </td>
                            <td >
                                <table cellpadding="0" cellspacing="0" width="100%">
                                    <tr>
                                        <td  class="admTableTDLast" style="padding-left:20px;">
                                            <xsl:choose>
                                                <xsl:when test="/negeso:page/negeso:browser-config/@action-mode='chooser'">
                                                    <td >
                                                        <form method="POST" name="operateForm" id="operateFormId" enctype="multipart/form-data" action="" target="CatalogWindow">
                                                            <input type="hidden" name="sortMode" value="{/negeso:page/negeso:browser-config/@sort-mode}"/>
                                                            <input type="hidden" name="viewMode" value="{/negeso:page/negeso:browser-config/@view-mode}"/>
                                                            <input type="hidden" name="command" value=""/>
                                                            <input type="hidden" name="victim" value=""/>
                                                            <input type="hidden" name="parentDir" value="{/negeso:page/negeso:browser-config/@parent-dir}"/>
                                                            <input type="hidden" name="currentDir" value="{/negeso:page/negeso:browser-config/@current-dir}"/>
                                                            <input type="hidden" name="actionMode" value="{/negeso:page/negeso:browser-config/@action-mode}"/>
                                                            <input type="hidden" name="showHiddenFiles" value="{/negeso:page/negeso:browser-config/@show-hidden-files}"/>
                                                        </form>

                                                        <table cellpadding="0" cellspacing="0" width="100%" >
                                                            <tr>
                                                                <td class="admTableTDLast">
                                                                    <xsl:call-template name="choose-action-form"/>
                                                                </td>
                                                            </tr>
                                                        </table>
                                                    </td>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <table cellpadding="0" cellspacing="0" width="100%" style="padding:1px 0 0 0;height:40px;vertical-align:middle;">
                                                        <tr>
                                                            <td>
                                                                <form method="POST" name="operateForm" id="operateFormId" enctype="multipart/form-data" action="">
                                                                    <input type="hidden" name="sortMode" value="{/negeso:page/negeso:browser-config/@sort-mode}"/>
                                                                    <input type="hidden" name="viewMode" value="{/negeso:page/negeso:browser-config/@view-mode}"/>
                                                                    <input type="hidden" name="command" value=""/>
                                                                    <input type="hidden" name="victim" value=""/>
                                                                    <input type="hidden" name="parentDir" value="{/negeso:page/negeso:browser-config/@parent-dir}"/>
                                                                    <input type="hidden" name="currentDir" value="{/negeso:page/negeso:browser-config/@current-dir}"/>
                                                                    <input type="hidden" name="actionMode" value="{/negeso:page/negeso:browser-config/@action-mode}"/>
                                                                    <input type="hidden" name="showHiddenFiles" value="{/negeso:page/negeso:browser-config/@show-hidden-files}"/>
                                                                    <nobr>
                                                                        <input class="admTextArea admWidth100" type="text" name="newFolderName" value="" style="margin-top: 2; background-color: #ffffff;"/>
                                                                        <img class="admHand" style="margin-top:-4px;" align="MIDDLE" src="/images/media_catalog/new_folder.png" onClick="createFolderAction();">
                                                                            <xsl:attribute name="title">
                                                                                <xsl:value-of select="java:getString($dict_media_catalog, 'CREATE_FOLDER')"/>
                                                                            </xsl:attribute>
                                                                        </img>
                                                                    </nobr>
                                                                </form>
                                                            </td>
                                                            <td>
	                                                            <div style="position: absolute; margin-top: -20px;margin-left:-47px; width: 0px;">
	                                                                    <xsl:call-template name="upload-resource-option"/>
	                                                            </div>
                                                            </td>
                                                            <td >&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
                                                                <xsl:apply-templates select="negeso:resource-sort-modes-option"/>
                                                            </td>
                                                            <td >
                                                                <xsl:apply-templates select="negeso:resource-view-modes-option"/>
                                                            </td>
                                                            <td >
                                                                <form method="POST" name="operateForm2" id="operateFormId2" enctype="multipart/form-data" action="" >
                                                                    <input type="hidden" name="sortMode" value="{/negeso:page/negeso:browser-config/@sort-mode}"/>
                                                                    <input type="hidden" name="viewMode" value="{/negeso:page/negeso:browser-config/@view-mode}"/>
                                                                    <input type="hidden" name="command" value=""/>
                                                                    <input type="hidden" name="victim" value=""/>
                                                                    <input type="hidden" name="parentDir" value="{/negeso:page/negeso:browser-config/@parent-dir}"/>
                                                                    <input type="hidden" name="currentDir" value="{/negeso:page/negeso:browser-config/@current-dir}"/>
                                                                    <input type="hidden" name="actionMode" value="{/negeso:page/negeso:browser-config/@action-mode}"/>
                                                                    <input type="hidden" name="showHiddenFiles" value="{/negeso:page/negeso:browser-config/@show-hidden-files}"/>
                                                                    <xsl:variable name="showMode" select="/negeso:page/negeso:browser-config/@show-hidden-files"/>
                                                                    <xsl:text>&#160;</xsl:text>
                                                                    <xsl:choose>
                                                                        <xsl:when test="$showMode = 'true'">
                                                                            <img class="admHand" style="align: top;" align="MIDDLE" src="/images/media_catalog/hide_hidden_files.gif" onClick="changeShowMode();">
                                                                                <xsl:attribute name="title">
                                                                                    <xsl:value-of select="java:getString($dict_media_catalog, 'HIDE_HIDDEN_FILES')"/>
                                                                                </xsl:attribute>
                                                                            </img>
                                                                        </xsl:when>
                                                                        <xsl:otherwise>
                                                                            <img class="admHand" style="align: top;" align="MIDDLE" src="/images/media_catalog/show_hidden_files.gif" onClick="changeShowMode();">
                                                                                <xsl:attribute name="title">
                                                                                    <xsl:value-of select="java:getString($dict_media_catalog, 'SHOW_HIDDEN_FILES')"/>
                                                                                </xsl:attribute>
                                                                            </img>
                                                                        </xsl:otherwise>
                                                                    </xsl:choose>
                                                                </form>
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td colspan="5">
                                                                <div class="success-message alert-success">File uploaded!</div>
                                                                <div id="progress" class="progress">
                                                                    <div class="progress-bar progress-bar-success">
                                                                    </div>
                                                                </div>

                                                                <div id="files" class="files"></div>
                                                            </td>
                                                        </tr>
                                                    </table>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                    <xsl:apply-templates select="negeso:directory"/>
                </td>
            </tr>
            <tr>
                <td>
                    <xsl:apply-templates select="negeso:repository"/>
                </td>
            </tr>
        </table>
    </xsl:template>


    <!-- BEGIN Choose Action Form -->
    <xsl:template name="choose-action-form">
        <table cellspacing="0" cellpadding="0" width="100%">
            <tr>
                <td width="17%">
                    <div class="admNavPanelInp" style="padding-left:0;">
                        <div class="imgL"></div>
                        <div>
                            <input type="button" style="width:95px;" class="admNavbarInp" name="chooseButton" onClick="chooseFileAction()">
                                <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_media_catalog, 'CHOOSE_FILE')"/></xsl:attribute>
                            </input>
                        </div>
                        <div class="imgR"></div>
                    </div>
                </td>
                <td width="70%">
                    <input id="choosenFile" class="admText admWidth200" type="text" name="choosenFile" readonly="true"/>
                </td>
                <td>
                    <div class="admNavPanelInp" style="padding-left:0;">
                        <div class="imgL"></div>
                        <div>
                            <input type="button" style="width:75px;" class="admNavbarInp " name="cancelButton" onClick="cancelFileAction()">
                                <xsl:attribute name="value"> <xsl:value-of select="java:getString($dict_common, 'CANCEL')"/> </xsl:attribute>
                            </input>
                        </div>
                        <div class="imgR"></div>
                    </div>
                    
                </td>
            </tr>
        </table>
    </xsl:template>



    <!-- BEGIN NEGESO UPLOAD FILE OPTION MATCH -->
    <xsl:template name="upload-resource-option">
        <form name="uploadFileForm"  method="post" action="" enctype="multipart/form-data" target="CatalogWindow">
            <input type="hidden" name="command" value="upload-file-command"/>
            <xsl:call-template name="browser-config-ninjas"/>
            <span class="btn btn-success fileinput-button">
                <i style="color: #fff" class="glyphicon glyphicon-plus"></i>
                <span style="color: #fff"> Select files...</span>
                <input id="fileupload"  type="file" name="uploadedFile" multiple="multiple" />
            </span>
            <script>
                $(function () {
                'use strict';
                var url = window.location.hostname === 'list-directory-command';
                $('#fileupload').fileupload({
                url: url,
                add: function (e, data) {
                data.submit();
                },
                <!--done: function (e, data) {-->

                <!--},                    -->
                progressall: function (e, data) {
                var progress = parseInt(data.loaded / data.total * 100, 10);
                $('#progress .progress-bar').css(
                'width',
                progress + '%'
                );
                }
                }).prop('disabled', !$.support.fileInput)
                .parent().addClass($.support.fileInput ? undefined : 'disabled');
                });
                $('#fileupload').bind('fileuploaddone', function (e, data) {
                    $('.success-message').show();
                });
                $('#fileupload').bind('fileuploadstop', function (e, data) {
                document.operateForm.command.value = "list-directory-command";
                document.operateForm.submit();
                })

            </script>
        </form>
    </xsl:template>


    <!-- BEGIN NEGESO SORT MODES OPTION MATCH -->
    <xsl:template match="negeso:resource-sort-modes-option">
        <form
            name="sortOrderForm"
            method="post"
            action=""
            enctype="multipart/form-data"
            target="CatalogWindow"
            onClick="changeSortMode()"
    >
            <input type="hidden" name="command" value="list-directory-command"/>
            <xsl:call-template name="browser-config-ninjas-brief"/>
            <input type="hidden" name="viewMode">
                <xsl:attribute name="value">
                    <xsl:value-of select="/negeso:page/negeso:browser-config/@view-mode"/>
                </xsl:attribute>
            </input>
            <input type="hidden" name="showHiddenFiles" value="{/negeso:page/negeso:browser-config/@show-hidden-files}"/>
            <xsl:variable name="sort-mode" select="/negeso:page/negeso:browser-config/@sort-mode"/>
            <select name="sortMode" onChange="document.sortOrderForm.submit()" class="admWidth150 admRight" >
                <option value="sname">
                    <xsl:if test="'sname'= $sort-mode">
                        <xsl:attribute name="selected"></xsl:attribute>
                    </xsl:if>
                    <xsl:value-of select="java:getString($dict_media_catalog, 'SORT_BY_NAME')"/>
                </option>
                <option value="stype">
                    <xsl:if test="'stype'= $sort-mode">
                        <xsl:attribute name="selected"></xsl:attribute>
                    </xsl:if>
                    <xsl:value-of select="java:getString($dict_media_catalog, 'SORT_BY_TYPE')"/>
                </option>
                <option value="ssize">
                    <xsl:if test="'ssize' = $sort-mode">
                        <xsl:attribute name="selected"></xsl:attribute>
                    </xsl:if>
                    <xsl:value-of select="java:getString($dict_media_catalog, 'SORT_BY_SIZE')"/>
                </option>
            </select>
        </form>
    </xsl:template>

    <!-- BEGIN NEGESO VIEW MODES OPTION MATCH -->
    <xsl:template match="negeso:resource-view-modes-option">
        <form name="viewModeForm" method="post" action="" enctype="multipart/form-data" target="CatalogWindow" onClick="changeViewMode()">
            <input type="hidden" name="command" value="list-directory-command"/>
            <xsl:call-template name="browser-config-ninjas-brief"/>
            <input type="hidden" name="sortMode">
                <xsl:attribute name="value">
                    <xsl:value-of select="/negeso:page/negeso:browser-config/@sort-mode"/>
                </xsl:attribute>
            </input>
            <input type="hidden" name="showHiddenFiles" value="{/negeso:page/negeso:browser-config/@show-hidden-files}"/>
            <xsl:variable name="view-mode" select="/negeso:page/negeso:browser-config/@view-mode"/>
            <select name="viewMode" class="sortOrderField admWidth125 admRight" onChange="document.viewModeForm.submit()">
                <option value="file_manager">
                    <xsl:if test="'file_manager' = $view-mode">
                        <xsl:attribute name="selected"></xsl:attribute>
                    </xsl:if>
                    <xsl:value-of select="java:getString($dict_media_catalog, 'FILE_LIST')"/>
                </option>
                <option value="image_gallery">
                    <xsl:if test="'image_gallery' = $view-mode">
                        <xsl:attribute name="selected"></xsl:attribute>
                    </xsl:if>
                    <xsl:value-of select="java:getString($dict_media_catalog, 'IMAGE_GALLERY')"/>
                </option>
                <option value="image_list">
                    <xsl:if test="'image_list' = $view-mode">
                        <xsl:attribute name="selected"></xsl:attribute>
                    </xsl:if>
                    <xsl:value-of select="java:getString($dict_media_catalog, 'IMAGE_LIST')"/>
                </option>
            </select>
        </form>
    </xsl:template>


    <!--******************************************
        BEGIN NEGESO DIRECTORY MATCH 
        ALL TEMPLATES ARE TAKEN FROM 
        modules/media_catalog/view_modes.xsl
    ******************************************-->
    <xsl:template match="negeso:directory">
        <xsl:choose>
            <!-- Image Gallery -->
            <xsl:when test="/negeso:page/negeso:browser-config/@view-mode='image_gallery'">
                <xsl:call-template name="view_modes_image-gallery-mode"/>
            </xsl:when>
            <!-- Image List -->
            <xsl:when test="/negeso:page/negeso:browser-config/@view-mode='image_list'">
                <xsl:call-template name="view_modes_image-list-mode"/>
            </xsl:when>
            <!-- File manager -->
            <xsl:otherwise>
                <xsl:call-template name="view_modes_file-manager-mode"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- ******************* Standard actions *************************** -->
    <xsl:template name="standard-actions">
        <xsl:param name="mode"/>
        <td class="admTableTD " align="center" id="td_{@id}">
            <!-- security -->
            <xsl:value-of select="@container-name"/>
        </td>
        <td class="admTableTD admWidth30">
            <!-- security -->
            <a>
                <xsl:choose>
                    <xsl:when test="@can-manage='true'">
                        <xsl:attribute name="href"> javascript:changeContainer(&quot;<xsl:value-of select="@container-id"/>&quot;,&quot;<xsl:value-of select="@id"/>&quot;) </xsl:attribute>
                        <img class="admImg" src="/images/lock.png">
                            <xsl:attribute name="alt">Change container</xsl:attribute>
                        </img>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:attribute name="disabled">true</xsl:attribute>
                        <xsl:attribute name="href">#</xsl:attribute>
                        <img class="admImg" src="/images/lock_gray.png">
                            <xsl:attribute name="alt">Change container</xsl:attribute>
                        </img>
                    </xsl:otherwise>
                </xsl:choose>
            </a>
        </td>

        <xsl:choose>
            <xsl:when test="@type='dir'">
                <!-- copy/move -->
                <td class="admTableTD admWidth30">
                    ---
                </td>
                <!-- rename -->
                <td class="admTableTD admWidth30">
                    ---
                </td>
                <!-- delete -->
                <td class="admTableTDLast admWidth30">
                    <xsl:choose>
                        <xsl:when test="@size='0 Kb' and @can-manage='true'">
                            <a>
                                <xsl:attribute name="href">javascript:removeResource(&quot;<xsl:value-of select="@escapedName"/>&quot;)</xsl:attribute>
                                <img class="admImg" src="/images/delete.png">
                                    <xsl:attribute name="alt">
                                        <xsl:value-of select="java:getString($dict_common, 'DELETE')"/>
                                    </xsl:attribute>
                                </img>
                            </a>
                        </xsl:when>
                        <xsl:when test="@containHiddenContent='true' and @containViewableContent='false'">
                            <a>
                                <xsl:attribute name="href">javascript:alert(&quot;<xsl:value-of select="java:getString($dict_media_catalog, 'FOLDER_CONTAIN_HIDDEN_RESOURCES')"/>&quot;)</xsl:attribute>
                                <img class="admImg" src="/images/delete.png">
                                    <xsl:attribute name="alt">
                                        <xsl:value-of select="java:getString($dict_media_catalog, 'FOLDER_CONTAIN_HIDDEN_RESOURCES')"/>
                                    </xsl:attribute>
                                </img>
                            </a>
                        </xsl:when>
                        <xsl:otherwise>
                            ---
                        </xsl:otherwise>
                    </xsl:choose>
                </td>
            </xsl:when>
            <xsl:otherwise>
                <!-- copy/move -->
                <td class="admTableTD admWidth30">
                    <a>
                        <xsl:attribute name="href">javascript:moveResource(&quot;<xsl:value-of select="@escapedName"/>&quot;)</xsl:attribute> 
                        <img class="admImg" src="/images/move.png"> <xsl:attribute name="alt">
                                <xsl:value-of select="java:getString($dict_common, 'COPY')"/>
                            </xsl:attribute>
                        </img>
                    </a>
                </td>
                <!-- rename -->
                <td class="admTableTD admWidth30">
                    <a>
                        <xsl:choose>
                            <xsl:when test="../@can-edit='true'">
                                <xsl:attribute name="href">  javascript:renameResource(&quot;<xsl:value-of select="@escapedName"/>&quot;)</xsl:attribute>
                                <img class="admImg" src="/images/rename.png">
                                    <xsl:attribute name="alt">
                                        <xsl:value-of select="java:getString($dict_media_catalog, 'RENAME')"/>
                                    </xsl:attribute>
                                </img>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:attribute name="disabled">true</xsl:attribute>
                                <xsl:attribute name="href">#</xsl:attribute>
                                <img class="admImg" src="/images/rename_gray.png">
                                    <xsl:attribute name="alt">
                                        <xsl:value-of select="java:getString($dict_media_catalog, 'RENAME')"/>
                                    </xsl:attribute>
                                </img>
                            </xsl:otherwise>
                        </xsl:choose>
                    </a>
                </td>
                <!-- delete -->
                <td class="admTableTDLast admWidth30">
                    <a>
                        <xsl:choose>
                            <xsl:when test="../@can-edit='true'">
                                <xsl:attribute name="href">javascript:removeResource(&quot;<xsl:value-of select="@escapedName"/>&quot;)</xsl:attribute>
                                <img class="admImg" src="/images/delete.png">
                                    <xsl:attribute name="alt">
                                        <xsl:value-of select="java:getString($dict_common, 'DELETE')"/>
                                    </xsl:attribute>
                                </img>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:attribute name="disabled">true</xsl:attribute>
                                <xsl:attribute name="href">#</xsl:attribute>
                                <img class="admImg" src="/images/delete_gray.png">
                                    <xsl:attribute name="alt">
                                        <xsl:value-of select="java:getString($dict_common, 'DELETE')"/>
                                    </xsl:attribute>
                                </img>
                            </xsl:otherwise>
                        </xsl:choose>
                    </a>
                </td>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- ******************* NEGESO REPOSITORY *************************** -->
    <xsl:template match="negeso:repository">
        <xsl:call-template name="common-catalog-info"/>
    </xsl:template>

    <!-- ******************* NEGESO USER MESSAGES *************************** -->
    <xsl:template match="negeso:user-messages">
        <xsl:call-template name="common-user-messages"/>
    </xsl:template>

    <!-- ******************* NEGESO ERROR MESSAGES *************************** -->
    <xsl:template match="negeso:error-messages">
        <xsl:call-template name="common-error-messages"/>
    </xsl:template>

</xsl:stylesheet>
