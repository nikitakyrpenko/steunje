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
    
        var fileArray = new Array();
    
        function onLoadWindow(){
            window.name='UploadWindow';
            resizeDialogWindow();
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
		
            caller.returnValue = new Object();
	        caller.returnValue.resCode = "OK";
	        
			fileArray = addFiles();
	        caller.returnValue.files = fileArray;
	        
	        var params = window.location.search.match(/([^&?=]+=[^&]+)/gi);
	        if (params){
	        	var cat_id = 0;
	        	var folder_id = 0;
	        	for (var i=0; i < params.length; i++){
	        		var arr_par = params[i].split("=");
	        		if(arr_par[0] == 'working_folder_id')
	        			folder_id = arr_par[1];
	        		if(arr_par[0] == 'category_id')
	        			cat_id = arr_par[1];
				}
			}
			caller.addDocument_files(folder_id, cat_id);
            window.close();
		}
        ]]>
        </xsl:text>
    </script>
</xsl:template>


<!-- MAIN ENTRY -->
<xsl:template match="/">
<html>
<head>
    <title><xsl:value-of select="java:getString($dict_media_catalog, 'UPLOAD_FILES')"/></title>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="/script/jquery.min.js"></script>
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script language="JavaScript" src="/script/common_functions.js" type="text/javascript">/**/</script>
    <script language="JavaScript" src="/script/media_catalog.js" type="text/javascript">/**/</script>
    <xsl:call-template name="java-script"/>
</head>
<body onLoad="onLoadWindow()" class="dialogSmall">
    <xsl:call-template name="NegesoBody">
        <xsl:with-param name="helpLink">
            <xsl:text>/admin/help/cnw1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text></xsl:with-param>
    </xsl:call-template>
</body>
</html>
</xsl:template>

<xsl:template match="negeso:page"  mode="admContent">  
 
    <!-- CONTENT -->
   	<xsl:apply-templates select="negeso:uplad-form"/>
    
    
</xsl:template>


<xsl:template match="negeso:uplad-form[@render-mode='show-form']">
    <form name="opratateForm" method="post" action="" enctype="multipart/form-data"
			        onSubmit="return checkUploadForm()" target="UploadWindow">
        <input type="hidden" name="file_set_amount" value="{@file-set-amount}"/>
        <input type="hidden" name="action" value="upload"/>
        <input type="hidden" name="working_folder" value="{@working-folder}"/>
        <input type="hidden" name="upload_params_id" value="{@upload-params-id}"/>
        <table align="center" border="0" cellpadding="0" cellspacing="0" class="admTable">
            <tr>
                <td align="center" class="admNavPanelFont" colspan="2">
                    <!-- TITLE -->
                    <xsl:call-template name="tableTitle">
                        <xsl:with-param name="headtext">
                            <xsl:value-of select="java:getString($dict_media_catalog, 'UPLOAD_FILES')"/>
                        </xsl:with-param>
                    </xsl:call-template>
                </td>
            </tr>
            <!-- Choose file from disk -->
            <xsl:for-each select="negeso:file-set">
                <xsl:for-each select="negeso:file">
                    <tr>
                        <th class="admTableTD" width="30%">
                            <xsl:value-of select="java:getString($dict_media_catalog, 'CHOOSE_FILE')"/>:
                        </th>
                        <td class="admTableTDLast"  width="70%">
                            <input
                                class="admTextArea"
                                type="file"
                                name="file_{@id}"
                        onKeyPress="return false;"
						            />
                        </td>
                    </tr>
                </xsl:for-each>
            </xsl:for-each>
            <tr>
                <th class="admTableTD">
                    <xsl:value-of select="java:getString($dict_upload_file, 'WORKING_FOLDER')"/>
                </th>
                <td class="admTableTDLast">
                    <xsl:value-of select="@working-folder"/>
                </td>
            </tr>
            <tr>
                <td>&#160;</td>
                <td>
                    <div class="admNavPanelInp">
                        <div class="imgL"></div>
                        <div>
                            <input class="admNavPanelInp" type="submit" id="submitBtnId" name="submitBtn">
                                <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'SUBMIT')"/></xsl:attribute>
                            </input>
                        </div>
                        <div class="imgR"></div>
                    </div>
                </td>
            </tr>
            <tr>
                <td class="admTableFooter" colspan="2">&#160;</td>
            </tr>
        </table>

        
    </form>
</xsl:template>

<xsl:template match="negeso:uplad-form[@render-mode='result']">
    <form name="opratateForm" method="post" action="" enctype="multipart/form-data"
        onSubmit="return checkUploadForm()"  target="UploadWindow">
        <input type="hidden" name="file_set_amount" value="{@file-set-amount}"/>
        <input type="hidden" name="action" value="upload"/>
        <input type="hidden" name="working_folder" value="{@working-folder}"/>
        <input type="hidden" name="upload_params_id" value="{@upload-params-id}"/>

        <xsl:for-each select="negeso:file-set">
            <script language="JavaScript">
                function addFiles(){
                var fileObj;
                var iArray = new Array();
                <xsl:for-each select="negeso:file">
                    <xsl:if test="@result='OK'">
                        //alert("asdasd");
                        fileObj = new Object();
                        fileObj.fileName = "<xsl:value-of select="@file-path"/>";

                        iArray.push(fileObj);
                    </xsl:if>
                </xsl:for-each>
                return iArray;
                }
            </script>
        </xsl:for-each>
        <table align="center" border="0" cellpadding="0" cellspacing="0" class="admTable">
            <tr>
                <th class="admTableTD">
                    <xsl:value-of select="java:getString($dict_upload_file, 'WORKING_FOLDER')"/>
                </th>
                <td class="admTableTDLast" colspan="2">
                    <xsl:value-of select="@working-folder"/>
                </td>
            </tr>
            <xsl:if test="count(negeso:file-set/negeso:file) = 0">
                <tr>
                    <td class="admTableTD admStatusMessage" colspan="3">
                        <xsl:value-of select="java:getString($dict_upload_file, 'NO_FILE_UPLOADED')"/>
                    </td>
                </tr>
            </xsl:if>
            <xsl:for-each select="negeso:file-set">
                <xsl:for-each select="negeso:file">
                    <tr>
                        <th class="admTableTD">
                            <xsl:value-of select="java:getString($dict_upload_file, 'FILE')"/>
                        </th>
                        <xsl:choose>
                            <xsl:when test="@result='error'">
                                <td class="admLeft" colspan="2">
                                    <xsl:value-of select="@file-name"/>: &#160;
                                    <xsl:for-each select="negeso:errors">
                                        <xsl:for-each select="negeso:error">
                                            <xsl:value-of select="text()"/>
                                            <br/>
                                        </xsl:for-each>
                                    </xsl:for-each>
                                    <xsl:value-of select="@error"/>
                                </td>
                                <td class="admTableTDLast admErrorMessage">
                                    <xsl:value-of select="java:getString($dict_common, 'ERROR')"/>
                                </td>
                            </xsl:when>
                            <xsl:otherwise>
                                <td class="admTableTD">
                                    <xsl:value-of select="@file-name"/>&#160;<xsl:value-of select="@file-size"/>
                                </td>
                                <td class="admTableTDLast admStatusMessage">
                                    <xsl:value-of select="java:getString($dict_common, 'OK')"/>
                                </td>
                            </xsl:otherwise>
                        </xsl:choose>
                    </tr>
                </xsl:for-each>
            </xsl:for-each>
            <tr>
                <td>&#160;</td>
                <td class="admCenter" colspan="3">
                    <div class="admNavPanelInp">
                        <div class="imgL"></div>
                        <div>
                            <input class="admNavPanelInp" name="submitBtn" onClick="onOk()" style="width:30px">
                                <xsl:attribute name="value">&#160;<xsl:value-of select="java:getString($dict_common, 'OK')"/></xsl:attribute>
                            </input>
                        </div>
                        <div class="imgR"></div>
                    </div>
                </td>
            </tr>
            <tr>
                <td class="admTableFooter" colspan="3">&#160;</td>
            </tr>
        </table>
    </form>
</xsl:template>

</xsl:stylesheet>
