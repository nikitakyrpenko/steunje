<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$
 
  Copyright (c) 2005 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  
 
  @version      $Revision$
  @author       Olexiy.Strashko
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<!-- Include/Import -->
<xsl:include href="/xsl/negeso_body.xsl"/>

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_document_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_document_module.xsl', $lang)"/>
<xsl:variable name="dict_security_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_security_module.xsl', $lang)"/>

<!-- NEGESO JAVASCRIPT Temaplate -->    
<xsl:template name="java-script">
    <script language="JavaScript">
        var s_DeleteCategoryConfirmation = "<xsl:value-of select="java:getString($dict_document_module, 'DELETE_CATEGORY_CONFIRMATION')"/>";
        var s_DeleteDocumentConfirmation = "<xsl:value-of select="java:getString($dict_document_module, 'DELETE_DOCUMENT_CONFIRMATION')"/>";
        var s_MoveDocumentConfirmation = "MOVE_DOCUMENT_CONFIRMATION";
        
    <xsl:text disable-output-escaping="yes">
        <![CDATA[
        // PUBLICATION MANAGEMENT
        function addCategory(folderId) {
            document.operateForm.action = "dc_category";
            document.operateForm.is_new.value = "true";
            document.operateForm.category_id.value = folderId;
            document.operateForm.submit();
        }

        var returnValue = new Object();
        function addDocuments(folderId, categoryId) {
            MediaCatalog.selectMassFileUploader(folderId, categoryId);
        }
        
        function addDocument_files(folderId, categoryId){
            result = returnValue;
            if (result != null && result.resCode == "OK") {
                var i;
                var name = "";
                for (i = 0; i < result.files.length; i++) {
                    name += result.files[i].fileName + ";";
                }
                
                document.operateForm.action = "dc-add-documents";
                document.operateForm.command.value = "dc-add-documents";
                document.operateForm.category_id.value = categoryId;
                document.operateForm.link.value = name;
                document.operateForm.submit();
            }
            return false;
        }


        // PUBLICATION MANAGEMENT
        function deleteCategory(targetId) {
            if (confirm(s_DeleteCategoryConfirmation)) {
                document.operateForm.action = "dc-delete-category";
                document.operateForm.command.value = "dc-delete-category";
                document.operateForm.target_category_id.value = targetId;
                document.operateForm.submit();
                return true;
            }
            return false;
        }

        function deleteDocument(targetId) {
            if (confirm(s_DeleteDocumentConfirmation)) {
                document.operateForm.action = "dc-delete-document";
                document.operateForm.command.value = "dc-delete-document";
                document.operateForm.document_id.value = targetId;
                document.operateForm.submit();
                return true;
            }
            return false;
        }

        function changeContainer(containerId, objectId){
            Security.selectContainerDialog(containerId, "manager", function(res){
                changeContainerCallback(res, objectId);
            });
        }

        function changeContainerCallback(res, objectId){
            if ( res != null ){
                document.operateForm.action = "dc-change-category-container";
                document.operateForm.command.value = "dc-change-category-container";
                document.operateForm.target_category_id.value = objectId;
                document.operateForm.container_id.value = res.containerId;
                document.operateForm.submit();
                return true;
            }
        }

        function MoveUpListItem(id, type) {
            document.operateForm.action = "dc-reorder";
            document.operateForm.command.value = "dc-reorder";
            document.operateForm.itemId.value = id;
            document.operateForm.reorder_action.value = "up";
            document.operateForm.type.value = type;
            document.operateForm.submit();
        }

        function MoveDownListItem(id, type) {
            document.operateForm.action = "dc-reorder";
            document.operateForm.command.value = "dc-reorder";
            document.operateForm.itemId.value = id;
            document.operateForm.reorder_action.value = "down";
            document.operateForm.type.value = type;
            document.operateForm.submit();
        }

        function moveItem(id) {
            window.showModalDialog(
                "dc-get-categories-tree?id=" + id,
                this, "dialogHeight: 580px; dialogWidth: 590px;" +
                " help: No; scroll: Yes; status: No;"
            ).then(function(result){
                if (typeof(result) != "undefined" && result != null) {
                    operateForm.command.value = "dc-copy-document";
                    operateForm.moveToId.value = result[0];
                    operateForm.move.value = result[1];
                    if (operateForm.move.value == "true") {
                        operateForm.submit();
                    }
                };
            });
        }
        ]]>
        </xsl:text>
    </script>
</xsl:template>

<!-- MAIN ENTRY -->
<xsl:template match="/">
<html>
<head>
    <title><xsl:value-of select="java:getString($dict_document_module, 'DOCUMENT_MODULE')"/></title>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>

    <link rel="stylesheet" type="text/css" href="/css/jquery-ui.custom.css"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="/script/jquery.min.js"/>
    <script type="text/javascript" src="/script/jquery-ui.custom.min.js" />
    <script type="text/javascript" src="/script/cufon-yui.js"/>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"/>
    <script type="text/javascript" src="/script/common_functions.js"/>

    <script language="JavaScript1.2" src="/script/security.js" type="text/javascript"/>
    <script type="text/javascript" src="/script/media_catalog.js"/>
    
    <xsl:call-template name="java-script"/>
</head>
<body>
    <!-- NEGESO HEADER -->
    <xsl:call-template name="NegesoBody">
        <xsl:with-param name="backLink">
            <xsl:if test="(count(descendant::negeso:page) = 0) and not(negeso:category/@is_current='true') ">
                <xsl:value-of select="concat('document_module?category_id=', descendant::negeso:category[@is_current='true']/@parent_id)"/>
            </xsl:if>
        </xsl:with-param>
    </xsl:call-template>
    <xsl:apply-templates select="descendant::negeso:category[@is_current='true']" mode="buttons"/>
</body>
</html>
</xsl:template>

<xsl:template match="negeso:page" mode="admContent">   
    <!-- PATH -->
    <xsl:call-template name="dc.path"/>
    <!-- Content -->
    <xsl:apply-templates select="descendant::negeso:category[@is_current='true']" mode="current"/>   
</xsl:template>

<!-- ********************************** Category *********************************** -->
<xsl:template match="negeso:category" mode="current">
        <!-- Render HEADER -->
        <form method="POST" name="operateForm" action="" enctype="multipart/form-data">
            <input type="hidden" name="command" value=""></input>
            <input type="hidden" name="id" value="{@id}"></input>
            <input type="hidden" name="category_id" value="{@id}"></input>
            <input type="hidden" name="document_id" value=""></input>
            <input type="hidden" name="target_category_id" value="{@id}"></input>
            <input type="hidden" name="moveToId"/>
            <input type="hidden" name="move"/>
            <input type="hidden" name="link" value=""/>
            <input type="hidden" name="is_new" value=""/>
            <input type="hidden" name="container_id" value=""/>
            <input type="hidden" name="type" value=""/>
            <input type="hidden" name="itemId" value=""/>
            <input type="hidden" name="reorder_action" value=""/>

            <table align="center" border="0" cellpadding="0" cellspacing="0" class="admTable">              
                <tr>
                    <td align="center" class="admNavPanelFont" >
                        <!-- TITLE -->
                        <xsl:call-template name="tableTitle">
                            <xsl:with-param name="headtext">
                                <xsl:value-of select="java:getString($dict_document_module, 'DOCUMENT_MODULE')"/>
                                <xsl:value-of select="descendant::negeso:category[@is_current='true']/@name"/>
                            </xsl:with-param>
                        </xsl:call-template>
                    </td>
                </tr>               
                <tr>
                    <td>
                        <table cellpadding="0" cellspacing="0" width="100%">
                            <tr>
                                <td class="admTDtitles" width="5%">&#160;</td>
                                <td class="admTDtitles" width="65%">
                                    <xsl:value-of select="java:getString($dict_common, 'TITLE')"/>
                                </td>
                                <td class="admTDtitles" width="12%">
                                    <xsl:value-of select="java:getString($dict_document_module, 'OWNER')"/>
                                </td>
                                <td class="admTDtitles" width="12%">
                                    <xsl:value-of select="java:getString($dict_document_module, 'LAST_MODIFIED')"/>
                                </td>
                                <td class="admTDtitles" width="12%">
                                    <xsl:value-of select="java:getString($dict_security_module, 'CONTAINER')"/>
                                </td>
                                <td class="admTDtitles" colspan="5" align="center">
                                    <xsl:value-of select="java:getString($dict_common, 'ACTION')"/>
                                </td>
                            </tr>
                            <xsl:if test="parent::negeso:category">
                                <tr>
                                    <th class="admTableTDLast" colspan="10">
                                        <a class="admAnchor" href="document_module?category_id={@parent_id}">
                                            <xsl:attribute name="title">
                                                <xsl:value-of select="java:getString($dict_common, 'UP')"/>
                                            </xsl:attribute>
                                            <img  align="center" src="/images/media_catalog/back.png"/>
                                        </a> &#160;&#160;&#160;&#160;
                                        <a class="admAnchor" href="document_module?category_id={@parent_id}">
                                            <xsl:attribute name="title">
                                                <xsl:value-of select="java:getString($dict_common, 'UP')"/>
                                            </xsl:attribute>
                                            <xsl:text>&#046;&#046;&#046;</xsl:text>
                                        </a>
                                    </th>
                                </tr>
                            </xsl:if>
                            <xsl:apply-templates select="negeso:category" mode="content"/>
                            <xsl:apply-templates select="negeso:document" mode="content"/>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td class="admTableFooter" >&#160;</td>
                </tr>
            </table>
        </form>
</xsl:template>


<xsl:template match="negeso:category" mode="content">
    <tr>
        <th class="admTableTDLast">
            <a class="admAnchor" href="document_module?category_id={@id}">
                <img src="/images/media_catalog/icon_dir.gif" class="admHand" border="0">
                    <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'EDIT')"/></xsl:attribute>
                </img>
            </a>
        </th>
        <th class="admTableTD">            
            <a class="admAnchor" href="document_module?category_id={@id}">
                <xsl:value-of select="@name"/>
            </a>            
        </th>
        <td class="admTableTD">
            <xsl:value-of select="@owner"/>&#160;
        </td>
        <td class="admTableTD">
            <xsl:value-of select="@last_modified"/>&#160;
        </td>
        <td class="admTableTD">
            <xsl:value-of select="@container_name"/>&#160;
        </td>
        <td class="admTableTD">
            <img class="admHand" src="/images/up.png" onClick="MoveUpListItem({@id},'category');">
                <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'UP')"/></xsl:attribute>
            </img>
        </td>
        <td class="admTableTD">
            <img class="admHand" src="/images/down.png" onClick="MoveDownListItem({@id},'category');">
                <xsl:attribute name="title">
                    <xsl:value-of select="java:getString($dict_common, 'DOWN')"/>
                </xsl:attribute>
            </img>
        </td>
        <td class="admTableTD">
            <a>
                <xsl:choose>
                    <xsl:when test="@can_manage='true'">
                        <xsl:attribute name="href"
                        >javascript:changeContainer(&quot;<xsl:value-of select="@container_id"/>&quot;,&quot;<xsl:value-of select="@id"/>&quot;)</xsl:attribute>
                        <img class="admImg" src="/images/lock.png">
                            <xsl:attribute name="alt"><xsl:value-of select="java:getString($dict_common, 'CHANGE_CONTAINER')"/></xsl:attribute>
                        </img>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:attribute name="disabled">true</xsl:attribute>
                        <xsl:attribute name="href">#</xsl:attribute>
                        <img class="admImg" src="/images/lock_gray.png">
                            <xsl:attribute name="alt"><xsl:value-of select="java:getString($dict_common, 'CHANGE_CONTAINER')"/></xsl:attribute>
                        </img>
                    </xsl:otherwise>
                </xsl:choose>
            </a>
        </td>
        <td class="admTableTD">
            <!-- edit -->
            <a>
                <xsl:choose>
                    <xsl:when test="@can_view='true'">
                        <xsl:attribute name="href">dc_category?category_id=<xsl:value-of select="@id"/></xsl:attribute>
                        <img class="admImg" src="/images/edit.png">
                            <xsl:attribute name="alt"><xsl:value-of select="java:getString($dict_common, 'EDIT')"/></xsl:attribute>
                        </img>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:attribute name="disabled">true</xsl:attribute>
                        <xsl:attribute name="href">#</xsl:attribute>
                        <img class="admImg" src="/images/edit_gray.png">
                            <xsl:attribute name="alt"><xsl:value-of select="java:getString($dict_common, 'EDIT')"/></xsl:attribute>
                        </img>
                    </xsl:otherwise>
                </xsl:choose>
            </a>
        </td>
        <td class="admTableTDLast">
            <!-- delete -->
            <img class="admHand" src="/images/delete.png" >
                <xsl:attribute name="alt"><xsl:value-of select="java:getString($dict_common, 'DELETE')"/></xsl:attribute>
                <xsl:choose>
                    <xsl:when test="@can_manage='true'">
                        <xsl:attribute name="src">/images/delete.png</xsl:attribute>
                        <xsl:attribute name="onClick">javascript:deleteCategory(&quot;<xsl:value-of select="@id"/>&quot;)</xsl:attribute>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:attribute name="src">/images/delete_gray.png</xsl:attribute>
                    </xsl:otherwise>
                </xsl:choose>
            </img>
        </td>
    </tr>
</xsl:template>

<xsl:template match="negeso:document" mode="content">
    <tr>
        <th class="admTableTDLast">
            <a class="admAnchor" href="document_module?category_id={@id}">
                <img src="/images/media_catalog/docFileClear.png" class="admHand" border="0">
                    <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'EDIT')"/></xsl:attribute>
                </img>
            </a>
        </th>
        <th class="admTableTD">
            <div style="width:190px;overflow:hidden">
                <a class="admAnchor" href="dc_document?document_id={@id}">
                    <xsl:value-of select="@name"/>&#160;
                </a>
            </div>
        </th>
        <th class="admTableTD">
            <xsl:value-of select="@owner"/>
        </th>
        <td class="admTableTD">
            <xsl:value-of select="@last_modified"/>
        </td>
        <td class="admTableTD">
            <xsl:value-of select="@container_name"/>
        </td>
         <td class="admTableTD">
             <img class="admHand" src="/images/up.png" onClick="MoveUpListItem({@id},'document');">
                 <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'UP')"/></xsl:attribute>
             </img>
         </td>
         <td class="admTableTD">
             <img class="admHand" src="/images/down.png" onClick="MoveDownListItem({@id},'document');">
                 <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'DOWN')"/></xsl:attribute>
             </img>
         </td>
        <td class="admTableTD">
            <img class="admImg" src="/images/lock_gray.png">
                <xsl:attribute name="alt"><xsl:value-of select="java:getString($dict_common, 'CHANGE_CONTAINER')"/></xsl:attribute>
            </img>
        </td>
        <td class="admTableTD">
            <!-- edit -->
            <a>
                <xsl:choose>
                    <xsl:when test="../@can_view='true'">
                        <xsl:attribute name="href">dc_document?document_id=<xsl:value-of select="@id"/></xsl:attribute>
                        <img class="admImg" src="/images/edit.png">
                            <xsl:attribute name="alt"><xsl:value-of select="java:getString($dict_common, 'EDIT')"/></xsl:attribute>
                        </img>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:attribute name="disabled">true</xsl:attribute>
                        <xsl:attribute name="href">#</xsl:attribute>
                        <img class="admImg" src="/images/edit_gray.png">
                            <xsl:attribute name="alt"><xsl:value-of select="java:getString($dict_common, 'EDIT')"/></xsl:attribute>
                        </img>
                    </xsl:otherwise>
                </xsl:choose>
            </a>
        </td>
        <td class="admTableTDLast">
            <!-- delete -->
            <img class="admHand" src="/images/delete.png" >
                <xsl:attribute name="alt"><xsl:value-of select="java:getString($dict_common, 'DELETE')"/></xsl:attribute>
                <xsl:choose>
                    <xsl:when test="../@can_manage='true'">
                        <xsl:attribute name="src">/images/delete.png</xsl:attribute>
                        <xsl:attribute name="onClick">javascript:deleteDocument(&quot;<xsl:value-of select="@id"/>&quot;)</xsl:attribute>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:attribute name="src">/images/delete_gray.png</xsl:attribute>
                    </xsl:otherwise>
                </xsl:choose>
            </img>
        </td>
    </tr>
</xsl:template>

<xsl:template name="dc.path">
    <!-- Path -->
    <table cellpadding="0" cellspacing="0" class="admNavPanel admSpaceSmall">
        <tr>
            <td class="admBold">
                <xsl:apply-templates select="negeso:category" mode="path"/>&#160;
            </td>
        </tr>
    </table>
</xsl:template>

<xsl:template match="negeso:category" mode="path">
    <xsl:choose>
    <xsl:when test="@is_current">
        <!-- Link path element - just print it-->
        <span class="admSecurity admLocation">
            <xsl:value-of select="@name"/>
        </span>
    </xsl:when>
    <xsl:otherwise>
        <!-- Link path element - just print it-->
        <span class="admSecurity admLocation">
            <a class="admLocation" href="document_module?category_id={@id}">
                <xsl:value-of select="@name"/>
            </a>
            &#160;&gt;
        </span>
        <xsl:apply-templates select="negeso:category" mode="path"/>
    </xsl:otherwise>
    </xsl:choose>

</xsl:template>

<xsl:template match="negeso:category" mode="buttons">        
    <table cellpadding="0" cellspacing="0"  width="764px" align="center" class="Buttons">
        <tr>
            <td>
                <div class="admBtnGreenb">
                    <div class="imgL"></div>
                    <div>
                        <input class="admNavbarInp" type="button" onClick="addCategory({@id})">
                            <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_document_module, 'ADD_CATEGORY')"/></xsl:attribute>
                        </input>
                    </div>
                    <div class="imgR"></div>
                </div>

                <div class="admBtnGreenb">
                    <div class="imgL"></div>
                    <div>
                        <input class="admNavbarInp" type="button" onClick="addDocuments({@folder_id},{@id})">
                            <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_document_module, 'UPLOAD_DOCUMENTS')"/></xsl:attribute>
                        </input>
                    </div>
                    <div class="imgR"></div>
                </div>
            </td>
        </tr>
    </table>        
</xsl:template>

</xsl:stylesheet>