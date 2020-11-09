<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${album.xsl}

  Copyright (c) 2003 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  @author       Sergiy Oliynyk

-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:negeso="http://negeso.com/2003/Framework"
    xmlns:java="http://xml.apache.org/xslt/java"
    exclude-result-prefixes="java">

<xsl:include href="/xsl/negeso_body.xsl"/>

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_photo_album" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_photo_album.xsl', $lang)"/>
<xsl:variable name="dict_news_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_news_module.xsl', $lang)"/>

<xsl:template match="/" >
<html>
<head>
    <title><xsl:value-of select="java:getString($dict_photo_album, 'PHOTO_ALBUM')"/></title>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link rel="stylesheet" href="/css/admin_style.css" type="text/css"/>
    
    <script type="text/javascript"  src="/script/jquery.min.js"/>
    <script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
    <script type="text/javascript" src="/script/common_functions.js"></script>
    <script type="text/javascript" src="/script/security.js"/>
    <script type="text/javascript" src="/script/media_catalog.js"/>

    <script>
        var s_DeleteItemsConfirmation = "<xsl:value-of select="java:getString($dict_news_module, 'DELETE_ITEMS_CONFIRMATION')"/>";
        var s_DeleteItemsConfirmationFolder = "<xsl:value-of select="java:getString($dict_news_module, 'DELETE_ITEMS_CONFIRMATION_FOLDER')"/>";
        var s_MoveItemsConfirmation = "<xsl:value-of select="java:getString($dict_news_module, 'MOVE_ITEMS_CONFIRMATION')"/>";
        var s_NoItemsSelected = "<xsl:value-of select="java:getString($dict_common, 'NO_ITEMS_SELECTED')"/>";
        var s_AccessDeniedToDelete = "<xsl:value-of select="java:getString($dict_news_module, 'ACCESS_DENIED_TO_DELETE')"/>";
        var s_AccessDeniedToMove = "<xsl:value-of select="java:getString($dict_news_module, 'ACCESS_DENIED_TO_MOVE')"/>";
        var s_TryAnotherName = "<xsl:value-of select="java:getString($dict_photo_album, 'TRY_ANOTHER_NAME')"/>";
        var s_UserAlreadyExists = "<xsl:value-of select="java:getString($dict_photo_album, 'USER_ALREADY_EXISTS')"/>";
        var s_GroupAlreadyExists = "<xsl:value-of select="java:getString($dict_photo_album, 'GROUP_ALREADY_EXISTS')"/>";
        var s_ContainerAlreadyExists = "<xsl:value-of select="java:getString($dict_photo_album, 'CONTAINER_ALREADY_EXISTS')"/>";
        var countPhotos = <xsl:value-of select="count(negeso:photo_album//negeso:photo)"/>;

        <xsl:text disable-output-escaping="yes">
        <![CDATA[
        function hasSelectedItems() {
            var result = false;
            if (typeof(mainForm.ids[0]) != "undefined") {
                for (i = 0; i < mainForm.ids.length; i++) {
                    if (mainForm.ids[i].checked)
                        break;
                }
                return (i < mainForm.ids.length);
            }
            else
                return mainForm.ids.checked;
        }

        function hasSelectedAlbums() {
            var result = false;
            if (typeof(mainForm.ids[0]) != "undefined") {
                for (i = 0; i < mainForm.ids.length; i++) {
                    if (mainForm.ids[i].checked && 
                        mainForm.ids[i].itemType == "album")
                        break;
                }
                return (i < mainForm.ids.length);
            }
            else return mainForm.ids.checked && 
                mainForm.ids.itemType == "album";
        }

        function checkRights() {
            var result = false;
            if (typeof(mainForm.ids[0]) != "undefined") {
                for (var i = 0; i < mainForm.ids.length; i++) {
                    if (mainForm.ids[i].checked && mainForm.ids[i].getAttribute('canmanage') != 'true')
                        break;
                }
                return (i == mainForm.ids.length);
            }
            else
                return mainForm.ids.canManage;
        }

        function openParentAlbum(id) {
            var href = "?command=get-photo-album-command";
            if (id != null)
                href += "&amp;id=" + id;
            window.location.href = href;
        }

        function validateAccess() {
            var callObj = ClientManager.CMSCreateAuxCallOptions();
            callObj.setCommand("validate-access-command");
            callObj.setParam("albumName", mainForm.name.value);
            callObj.setParam("login", mainForm.login.value);
            var result = ClientManager.CMSUpdateEntity(callObj);
            if (!result.error && result.value != "") {
                var tree = StringUtil.asTree(result.value);
                var resName = tree.selectSingleNode("/negeso:resultName").text;
                if (resName == "user exists") {
                    alert(s_UserAlreadyExists);
                    return false;
                }
                if (resName == "group exists") {
                    alert(s_GroupAlreadyExists + " " + s_TryAnotherName);
                    return false;
                }
                if (resName == "container exists") {
                    alert(s_ContainerAlreadyExists + " " + s_TryAnotherName);
                    return false;
                }
                return true;
            }
            else return false;
        }

        /* Create Secured Album 
        *  call shain:
        *  createSecuredAlbum => openNewAlbumDialog => submitAlbumDialogResult <if state> - False => 'submit()'
        *                            |                                          True  |
        *                            ^         <=           <=           <=          <= 
        **/
        function createSecuredAlbum(){
            openNewAlbumDialog(true, function(result){
                if (result != null){
                    mainForm.name.value = result[0];
                    mainForm.user.value = result[1];
                    mainForm.login.value = result[2];
                    submitAlbumDialogResult(result);
                }
            });
        }

        function openNewAlbumDialog(newValues, callback){
            var uri;
            if (newValues) {
                uri = "?command=create-secured-album-command";
            } else {
                uri = "?command=create-secured-album-command&name=" + 
                    mainForm.name.value + "&user=" + mainForm.user.value +
                    "&login=" + mainForm.login.value;
            }
            showModalDialog(uri, this,
                "dialogHeight: 460px; dialogWidth: 590px; help: No; scroll: Yes; status: No;"
            ).then(function(res){
                if (typeof callback == 'function'){
                    callback(res);
                }
            });
        }

        function submitAlbumDialogResult(result){
            if (result != null && !validateAccess()){
                openNewAlbumDialog(false, submitAlbumDialogResult);
            } else {
                if (result != null) {
                    mainForm.password.value = result[3];
                    mainForm.command.value = "create-secured-album-command";
                    mainForm.submit();
                }
            }
        }


        function createAlbum() {
            mainForm.command.value = "create-album-command";
            mainForm.submit();
        }

        function editDescription(id) {
            window.location.href = 
                "?command=get-album-description-command&id=" + id;
        }

        function deleteItems() {                     
            if (hasSelectedItems()){
                            confirmMess=s_DeleteItemsConfirmation;
                            for (i = 0; i < mainForm[name='ids'].length; i++) {
                                   if(mainForm[name='ids'][i].itemType=='album' && mainForm[name='ids'][i].checked==true){
                                          confirmMess=s_DeleteItemsConfirmationFolder;
                                          break;
                                   }        
                            }

                if (confirm(confirmMess)) {
                    if (checkRights()) {
                        mainForm.command.value = "delete-photo-album-command";
                        mainForm.submit();
                    }
                    else
                        alert(s_AccessDeniedToDelete);
                }
            }
            else
                alert(s_NoItemsSelected);
        }

        function moveItems() {
            if (!hasSelectedItems()){
                alert(s_NoItemsSelected);
                return;
            }
            window.showModalDialog(
                "?command=get-albums-tree-command&id=" + mainForm.id.value,
                this, "dialogHeight: 600px; dialogWidth: 820px;" +
                    " help: No; scroll: Yes; status: No;").then(function(result){
                       if (typeof(result) === "undefined" && result === null) {
                            return;
                        }
                        mainForm.command.value = "copy-photos-command";
                        mainForm.moveToId.value = result[0];
                        mainForm.move.value = result[1];
                        if (mainForm.move.value == "true") {
                            if (confirm(s_MoveItemsConfirmation)) {
                                if (checkRights()){
                                    mainForm.submit();
                                } else {
                                    alert(s_AccessDeniedToMove);
                                }
                            }
                        } else {
                            mainForm.submit();
                        }
            });
        }

        function changeContainer() {
            Security.selectContainerDialog(mainForm.containerId.value, "manager", function(res){
                changeContainerCallback(res);
            });
        }
        function changeContainerCallback(result){
            if ((result != null) && (result.resCode = "OK")) {
                mainForm.command.value = "change-album-container-command";
                mainForm.containerId.value = result.containerId;
                mainForm.submit();
            }
        }
        var returnValue = new Object();
        function uploadPhotos(folderId) {
            MediaCatalog.selectMassThUploaderByParamsId(
                "photo_album", folderId);
        }

        function selectItem(checkbox) {
            if (checkbox.itemType == 'album') {
                if (checkbox.checked && document.all.moveButton.src.indexOf("move.gif") > 0) {
                    document.all.moveButton.outerHTML = 
                        "<img id=\"moveButton\" class=\"admHand\" src=\"/images/photo_album/move_gray.gif\">";
                }
                else if (countPhotos > 0 && !hasSelectedAlbums()) {
                    document.all.moveButton.outerHTML = 
                        "<img id=\"moveButton\" class=\"admHand\" src=\"/images/photo_album/move.gif\" onClick=\"moveItems();\">";                
                }
            }
        }

        function returnValuePhotos(folderId){
            result = returnValue;
            if (result != null && result.resCode == "OK") {
                         var i;
                var name = "";
                var width = "";
                var height = "";
                var thumbnailName = "";
                var thumbnailWidth = "";
                var thumbnailHeight = "";
                      for (i = 0; i < result.images.length; i++) {
                    name += result.images[i].imageName + ";";
                    width += result.images[i].imageWidth + ";";
                    height += result.images[i].imageHeight + ";";
                    thumbnailName += result.images[i].thumbnailName + ";";
                    thumbnailWidth += result.images[i].thumbnailWidth + ";";
                    thumbnailHeight += result.images[i].thumbnailHeight + ";";
                }
                mainForm.command.value = "add-photos-command";
                mainForm.name.value = name;
                mainForm.width.value = width;
                mainForm.height.value = height;
                mainForm.thumbnailName.value = thumbnailName;
                mainForm.thumbnailWidth.value = thumbnailWidth;
                mainForm.thumbnailHeight.value = thumbnailHeight;
                mainForm.submit();
            }
        
        }

        ]]>
        </xsl:text>
    </script>
    <style>
        .imageDiv {
            background-image: url('/images/photo_album/folder.gif');
            background-repeat: no-repeat;
            background-position: center top;
            text-align: center;
            vertical-align: middle;
            width: 95px;
            height: 100px;
        }
        .albumPath {
            color: #008033;
            font-weight: bold;
            font-size: 12px;
        }
    </style>
</head>
<body style="behavior: url(/script/webservice.htc) url(/script/clientmanager.htc)" id="ClientManager">    
    <xsl:call-template name="NegesoBody">
        <xsl:with-param name="helpLink">
            <xsl:text>/admin/help/cph1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
        </xsl:with-param>
        <xsl:with-param name="backLink">
            <xsl:choose>
                <xsl:when test="//parent::negeso:album[negeso:album[@selected = 'true' and not(negeso:album[@selected = 'true'])]]/@id">
                    <xsl:text>?command=get-photo-album-command&amp;id=</xsl:text>
                    <xsl:value-of select="//parent::negeso:album[negeso:album[@selected = 'true' and not(negeso:album[@selected = 'true'])]]/@id"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>?command=get-photo-album-command</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:with-param>
    </xsl:call-template>    
</body>
</html>
</xsl:template>
    
<xsl:template match="negeso:photo_album" mode="admContent">
    <xsl:variable name="albumId" select="@albumId"/>
    <xsl:variable name="canManage">
        <xsl:choose>
            <xsl:when test="@albumId">
                <xsl:value-of select="//negeso:album[@id=$albumId]/@canManage"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="@canManage"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="canContribute">
        <xsl:choose>
            <xsl:when test="@albumId">
                <xsl:value-of select="//negeso:album[@id=$albumId]/@canContribute"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="@canContribute"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    
    <form name="mainForm" enctype="multipart/form-data" method="POST">
        <input type="hidden" name="command"/>
        <input type="hidden" name="id" value="{@albumId}"/>
        <input type="hidden" name="containerId" value="{//negeso:album[@id=$albumId]/@containerId}"/>
        <input type="hidden" name="name"/>
        <input type="hidden" name="width"/>
        <input type="hidden" name="height"/>
        <input type="hidden" name="thumbnailName"/>
        <input type="hidden" name="thumbnailWidth"/>
        <input type="hidden" name="thumbnailHeight"/>
        <input type="hidden" name="moveToId"/>
        <input type="hidden" name="move"/>
        <input type="hidden" name="user"/>
        <input type="hidden" name="login"/>
        <input type="hidden" name="password"/>
        
        <table cellspacing="0" cellpadding="0" border="0" align="center" width="100%" class="admTable" style="background-color:#fff;">
            <tr>
                <td style="width: auto; height: auto; padding-left: 20px;">
                    <table cellspacing="0" cellpadding="0">
                        <tbody>
                            <tr>
                                <td  class="admNavigation" valign="middle" style="padding: 8px 0pt 0pt; text-decoration: none;">
                                    <span>
                                        <a href="?command=get-photo-album-command">Location</a>
                                    </span>
                                    <xsl:apply-templates select="negeso:album[@selected = 'true']" mode="path"/>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </td>
            </tr>
            <tr>
                <td align="center" class="admNavPanelFont">
                    <xsl:call-template name="tableTitle">
                        <xsl:with-param name="headtext">
                            <xsl:choose>
                                <xsl:when test="negeso:album[@selected='true']">
                                    <xsl:value-of select="//negeso:album[@id=$albumId]/@name"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="java:getString($dict_photo_album, 'PHOTO_ALBUM')"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:with-param>
                    </xsl:call-template>
                </td>
            </tr>
            <tr>
                <td class="admTableTDLast">
                    <table cellspacing="0" cellpadding="0" border="0" align="center" width="100%"  style="margin: 0 0 10px 0;">
                        <tr>
                            <!--Open parent album-->
                            <td align="center">
                                <img class="admHand">
                                    <xsl:choose>
                                        <xsl:when test="negeso:album[@selected='true']">
                                            <xsl:attribute name="src">/images/up.png</xsl:attribute>
                                            <xsl:attribute name="onClick">openParentAlbum(<xsl:value-of select="//negeso:album[@id=$albumId]/@parentId"/>)</xsl:attribute>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:attribute name="src">/images/up_gray.png</xsl:attribute>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                    <xsl:attribute name="title">Up</xsl:attribute>
                                </img>
                            </td>
                            <!--Create a new album-->
                            <td align="center">
                                <img class="admHand">
                                    <xsl:choose>
                                        <xsl:when test="$canContribute = 'true'">
                                            <xsl:attribute name="src">/images/photo_album/add.png</xsl:attribute>
                                            <xsl:attribute name="onClick">
                                                <xsl:choose>
                                                    <xsl:when test="@secured = 'true'">createSecuredAlbum();</xsl:when>
                                                    <xsl:otherwise>createAlbum();</xsl:otherwise>
                                                </xsl:choose>
                                            </xsl:attribute>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:attribute name="src">/images/photo_album/add_gray.png</xsl:attribute>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                    <xsl:attribute name="title">
                                        <xsl:value-of select="java:getString($dict_photo_album, 'CREATE_NEW_ALBUM')"/>
                                    </xsl:attribute>
                                </img>
                            </td>
                            <!--Edit description-->
                            <td align="center">
                                <img class="admHand">
                                    <xsl:choose>
                                        <xsl:when test="@albumId">
                                            <xsl:attribute name="src">/images/photo_album/edit.png</xsl:attribute>
                                            <xsl:attribute name="onClick">editDescription(<xsl:value-of select="@albumId"/>)</xsl:attribute>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:attribute name="src">/images/photo_album/edit_gray.png</xsl:attribute>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                    <xsl:attribute name="title">
                                        <xsl:value-of select="java:getString($dict_photo_album, 'EDIT_DESCRIPTION')"/>
                                    </xsl:attribute>
                                </img>
                            </td>
                            <!--Upload photos-->
                            <td align="center">
                                <img class="admHand">
                                    <xsl:choose>
                                        <xsl:when test="@albumId and $canContribute = 'true'">
                                            <xsl:attribute name="src">/images/photo_album/upload.png</xsl:attribute>
                                            <xsl:attribute name="onClick">uploadPhotos(<xsl:value-of select="//negeso:album[@id=$albumId]/@folderId"/>)</xsl:attribute>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:attribute name="src">/images/photo_album/upload_gray.png</xsl:attribute>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                    <xsl:attribute name="title">
                                        <xsl:value-of select="java:getString($dict_photo_album, 'UPLOAD_PHOTOS')"/>
                                    </xsl:attribute>
                                </img>
                            </td>
                            <!--Move items-->
                            <td align="center">
                                <img id="moveButton" class="admHand">
                                    <xsl:choose>
                                        <xsl:when test="count(//negeso:photo)">
                                            <xsl:attribute name="src">/images/move.png</xsl:attribute>
                                            <xsl:attribute name="onClick">moveItems()</xsl:attribute>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:attribute name="src">/images/move_gray.png</xsl:attribute>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                    <xsl:attribute name="title">
                                        <xsl:value-of select="java:getString($dict_common, 'MOVE')"/>
                                    </xsl:attribute>
                                </img>
                            </td>
                            <!--Change container-->
                            <xsl:if test="@secured = 'true'">
                                <td align="center">
                                    <img class="admHand">
                                        <xsl:choose>
                                            <xsl:when test="@albumId and $canManage = 'true'">
                                                <xsl:attribute name="src">/images/photo_album/lock.png</xsl:attribute>
                                                <xsl:attribute name="onClick">changeContainer()</xsl:attribute>
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <xsl:attribute name="src">/images/photo_album/lock_gray.png</xsl:attribute>
                                            </xsl:otherwise>
                                        </xsl:choose>
                                        <xsl:attribute name="title">
                                            <xsl:value-of select="java:getString($dict_common, 'CHANGE_CONTAINER')"/>
                                        </xsl:attribute>
                                    </img>
                                </td>
                            </xsl:if>
                            <!--Delete items-->
                            <td align="center">
                                <img class="admHand" id="deleteImage">
                                    <xsl:choose>
                                        <xsl:when test="$canManage = 'true' and (count(//negeso:album[count(negeso:album) = 0 and not(@selected)]) + count(//negeso:photo)) > 0">
                                            <xsl:attribute name="src">/images/delete.png</xsl:attribute>
                                            <xsl:attribute name="onClick">deleteItems()</xsl:attribute>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:attribute name="src">/images/delete_gray.png</xsl:attribute>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                    <xsl:attribute name="title">
                                        <xsl:value-of select="java:getString($dict_common, 'DELETE')"/>
                                    </xsl:attribute>
                                </img>
                            </td>
                            <td width="70%" style="padding-left:50px;">
                                <xsl:value-of select="//negeso:parameter[@name='errorMessage']/negeso:value" disable-output-escaping="yes"/>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr>
                <td class="admTableTDLast" style="padding:0;">                    
                    <div class="admNavPanelDiv" style="background-color:transparent;">
                        <xsl:apply-templates select="negeso:album"/>
                    </div>
                </td>
            </tr>
            <tr>
                <td class="admTableFooter">&#160;</td>
            </tr>
        </table>
    </form>
</xsl:template>

<xsl:template match="negeso:album[@selected = 'true']">
    <xsl:apply-templates select="negeso:album"/>
    <xsl:apply-templates select="negeso:photo">
        <xsl:with-param name="numberOfAlbums" select="count(negeso:album)"/>
    </xsl:apply-templates>
</xsl:template>

<xsl:template match="negeso:album">
    <div class="admTableTD admDiv">
        <xsl:if test="(position() mod 4) = 0">
            <xsl:attribute name="class">admTableTDLast admDivLast</xsl:attribute>            
        </xsl:if>        
        <a href="?command=get-photo-album-command&amp;id={@id}" class="admAnchor">
            <img height="70" align="center" width="70" src="/images/media_catalog/folder_big.png"/><br/>            
        </a>
        <input id="item{@id}" type="checkbox" name="ids" value="{@id}" canManage="{@canManage}" onClick="selectItem(item{@id});" itemType="album"/>&#160;
        <a href="?command=get-photo-album-command&amp;id={@id}" class="admAnchor">
            <xsl:value-of select="@name"/>
        </a>
    </div>
</xsl:template>

<xsl:template match="negeso:photo">
    <div class="admTableTD admDiv">
        <xsl:if test="(position() mod 4) = 0">
            <xsl:attribute name="class">admTableTDLast admDivLast</xsl:attribute>
        </xsl:if>
        <a href="?command=get-photo-command&amp;id={@id}" class="admAnchor">
            <img class="admBorder" src="{@th_src}" alt="{@name}">
                <xsl:if test="@th_width > 100 or @th_height > 100">
                    <xsl:choose>
                        <xsl:when test="@th_width > @th_height">
                            <xsl:attribute name="width">100</xsl:attribute>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:attribute name="height">100</xsl:attribute>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:if>                
            </img>
            <br/>
        </a>
        <input id="item{@id}" type="checkbox" name="ids" value="{@id}" canManage="true" itemType="photo" onClick="selectItem(item{@id});" />&#160;
        <a href="?command=get-photo-command&amp;id={@id}" class="admAnchor">
            <xsl:value-of select="@name"/>
        </a>
    </div>
</xsl:template>

<xsl:template match="negeso:album[@selected = 'true']" mode="path">
    <xsl:choose>
        <xsl:when test="negeso:album[@selected = 'true']">
            <xsl:choose>
                <xsl:when test="@canView = 'true'">
                    <span>
                        &#160;<img src="/images/navig_arrow.png" style="vertical-align: middle;"/>&#160;
                        <a href="?command=get-photo-album-command&amp;id={@id}">                        
                            <xsl:value-of select="@name"/>
                        </a>
                    </span>                    
                 </xsl:when>
                 <xsl:otherwise>
                     <span>
                         &#160;<img src="/images/navig_arrow.png" style="vertical-align: middle;"/>&#160;
                         <xsl:value-of select="@name"/>
                     </span>
                 </xsl:otherwise>
            </xsl:choose>
        </xsl:when>
        <xsl:otherwise>
            <span>
                &#160;<img src="/images/navig_arrow.png" style="vertical-align: middle;"/>&#160;
                <xsl:value-of select="@name"/>
            </span>
        </xsl:otherwise>
    </xsl:choose>
    <xsl:apply-templates select="negeso:album[@selected = 'true']" mode="path"/>
</xsl:template>

</xsl:stylesheet>
