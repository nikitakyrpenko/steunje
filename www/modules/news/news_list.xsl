<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${list.xsl}       
 
  Copyright (c) 2003 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  @author       Olexiy.Strashko
  @author       Sergiy Oliynyk
  @author       Volodymyr Snigur
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<xsl:include href="/xsl/negeso_body.xsl"/>

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_news_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_news_module.xsl', $lang)"/>
    <xsl:variable name="dict_modules" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_modules.xsl', $lang)"/>

<xsl:template match="/">
<html>
<head>
    <title><xsl:value-of select="java:getString($dict_news_module, 'MANAGE_NEWS_LIST')"/> "<xsl:value-of select="negeso:list/@name"/>"</title>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    <link rel="stylesheet" type="text/css" href="/css/customized-jquery-ui.css"/>
    
    <script type="text/javascript" src="/script/jquery.min.js"></script>
    <script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
    <script type="text/javascript" src="/script/common_functions.js"/>
    
    <script type="text/javascript" src="/script/security.js"/>
    <script language="JavaScript">
        var canManageList = "<xsl:value-of select="negeso:list/@canManage"/>" == "true";
        var s_DeleteConfirmation = "<xsl:value-of select="java:getString($dict_common, 'DELETE_CONFIRMATION')"/>";
        var s_ArchiveItemsConfirmation = "<xsl:value-of select="java:getString($dict_news_module, 'ARCHIVE_ITEMS_CONFIRMATION')"/>";
        var s_DeleteItemsConfirmation = "<xsl:value-of select="java:getString($dict_news_module, 'DELETE_ITEMS_CONFIRMATION')"/>";
        var s_MoveItemsConfirmation = "<xsl:value-of select="java:getString($dict_news_module, 'MOVE_ITEMS_CONFIRMATION')"/>";
        var s_NoItemsSelected = "<xsl:value-of select="java:getString($dict_common, 'NO_ITEMS_SELECTED')"/>";
        var s_AccessDeniedToArchive = "<xsl:value-of select="java:getString($dict_news_module, 'ACCESS_DENIED_TO_ARCHIVE')"/>";
        var s_AccessDeniedToDelete = "<xsl:value-of select="java:getString($dict_news_module, 'ACCESS_DENIED_TO_DELETE')"/>";
        var s_AccessDeniedToMove = "<xsl:value-of select="java:getString($dict_news_module, 'ACCESS_DENIED_TO_MOVE')"/>";
        var s_LinkCannotBeMovedToArchiveDirectly = "<xsl:value-of select="java:getString($dict_news_module, 'LINK_CANNOT_BE_MOVED')"/>";
        var s_PrivateArticle = "<xsl:value-of select="java:getString($dict_news_module, 'PRIVATE_ARTICLE')"/>";

        <xsl:text disable-output-escaping="yes">
        <![CDATA[
        function AddListItem() {
            listManageForm.command.value = "create-list-item-command";
            listManageForm.submit();
        }

        
        function ChangeContainer(containerID) {
            var container = document.getElementById(containerID);
            var result = Security.selectContainerDialog(container.containerId, "manager");
            if ((result != null) && (result.resCode = "OK")) {
                var callObj = ClientManager.CMSCreateAuxCallOptions();
                callObj.setCommand("set-list-item-container-command");
                callObj.setParam("listItemId", container.listItemId);
                callObj.setParam("containerId", result.containerId);
                var containerId = result.containerId;
                result = ClientManager.CMSUpdateEntity(callObj);
                if (!result.error) {
                    if (result.value != "") {
                        var tree = StringUtil.asTree(result.value);
                        var resName = tree.selectSingleNode("/negeso:resultName").text;
                        if (resName == "public") {
                            var response = window.showModalDialog(
                                "/dialogs/dialogYNC.html", s_PrivateArticle,
                                "dialogHeight: 90px; dialogWidth: 300px; help: No; scroll: No; status: No;");
                            if (response == "cancel") return;
                            var isPublic = (response == "yes") ? "true" : "false";
                            callObj = ClientManager.CMSCreateAuxCallOptions();
                            callObj.setCommand("set-list-item-container-command");
                            callObj.setParam("listItemId", container.listItemId);
                            callObj.setParam("containerId", containerId);
                            callObj.setParam("public", isPublic);
                            result = ClientManager.CMSUpdateEntity(callObj);
                        }
                    }
                    window.location.reload();
                }
            }
        }

        function DeleteListItem(id) {
            if (confirm(s_DeleteConfirmation)) {
                listManageForm.command.value = "delete-list-item-command";
                listManageForm.listItemId.value = id;
                listManageForm.submit();
            }
        }

        function MoveUpListItem(id) {
            listManageForm.command.value = "change-order-command";
            listManageForm.listItemId.value = id;
            listManageForm.action.value = "up";
            listManageForm.submit();
        }

        function MoveDownListItem(id) {
            listManageForm.command.value = "change-order-command";
            listManageForm.listItemId.value = id;
            listManageForm.action.value = "down";
            listManageForm.submit();
        }

        function GetSelectedItems() {
            var items = "";
            
             $('form[name=listManageForm] INPUT[type=checkbox]').each(function(){
                if($(this).attr('checked') && $(this).val() != 'on'){
                	items = items + $(this).val() + ';';
                }
            });
          
            return items;
        }

        function checkRights() {
            var items = "";            
            result = true;
            
            $('form[name=listManageForm] INPUT[type=checkbox]').each(function(){                
                if($(this).attr('checked') && $(this).val() != 'on'){
                	if (!canManageList || $(this).attr('canManage') != "true"){
                        result = false;   
                    }
                }
            });

            return result;          
        }

        function GetSelectedItemsForArchiving() {
            var items = "";
            var n = listManageForm.getElementsByTagName("input").length;
            for (i=0; i<n; i++) {

                if (listManageForm.elements[i].type == "checkbox" && listManageForm.elements[i].checked)
                {
                    if (listManageForm.elements[i].getAttribute('linked')) {
                        alert("'" + listManageForm.elements[i].itemTitle + "'\n" +
                            s_LinkCannotBeMovedToArchiveDirectly);
                       throw "LINK";
                    }
                    items = items + listManageForm.elements[i].value + ';';
                }
            }
            return items;
        }
         
        function DeleteItems() {
            listManageForm.command.value = "delete-list-item-command";
            listManageForm.listItems.value = GetSelectedItems();
            if (listManageForm.listItems.value != '') {
                if (confirm(s_DeleteItemsConfirmation)) {
                    if (checkRights())
                        listManageForm.submit();
                    else
                        alert(s_AccessDeniedToDelete);
                }
            }
            else
                window.alert(s_NoItemsSelected);
        }

        function MoveItemsToArchive() {
            listManageForm.command.value = "archive-list-item-command";
            listManageForm.listItems.value = GetSelectedItemsForArchiving();
            if (listManageForm.listItems.value != '') {
                if (confirm(s_ArchiveItemsConfirmation)) {
                    if (checkRights())
                        listManageForm.submit();
                    else
                        alert(s_AccessDeniedToArchive);
                }
            }
            else
                window.alert(s_NoItemsSelected);
        }

        function MoveItemsToOtherList() {
            listManageForm.listItems.value = GetSelectedItems();
            if (listManageForm.listItems.value == '') {
                window.alert(s_NoItemsSelected);
                return;
            }
            strPage = "?command=get-list-roll-command&listId=" + 
                listManageForm.listId.value + "&moduleId=" + listManageForm.moduleId.value;
            strAttr = "resizable:on;scroll:on;status:off;dialogWidth:610px;dialogHeight:360px";
            var result = showModalDialog(strPage, null , strAttr);
            if (result != null) {
                listManageForm.command.value = "copy-list-item-command";
                listManageForm.moveToId.value = result[0];
                listManageForm.isMove.value = !result[1];
                if (listManageForm.isMove.value == "true") {
                    if (confirm(s_MoveItemsConfirmation)) {
                       if (checkRights())
                            listManageForm.submit();
                        else
                            alert(s_AccessDeniedToMove);
                     }
                }
                else
                    listManageForm.submit();
            }
        }

        function SelectItems(obj) {
            $(obj).attr('checked',!$(obj).attr('checked'));
            $('form[name=listManageForm] INPUT[type=checkbox]').each(function(){
                $(this).attr('checked',!$(this).attr('checked'));
            });
        }
        
        function OpenArchive(listId) {
            window.location.href = '?command=get-archive-command&listId=' + listId;
           
        }
              
    
        function getNewsTranslationDialog() {
             $("[class^=translateDialog]").each(function() {
                 $(this).remove();
              });
             var langId = $("#listLangId").val();
             
            $.ajax({
                type: "POST",
                url: "/admin/stm_settings.html?act=getNewsTranslationDialog&langId=" + langId + "&moduleId=" + listManageForm.moduleId.value,
                dataType: "html",
                success: function(dialog, stat) {
		            $(document.body).append(dialog);
		            $("#translateDialog").dialog({ buttons: {
		                'Cancel': function() { $(this).remove();  },
		                'Translate': function() {
		                         var itemIds = "";
					             var translateToLang = "";
					             var listIds = "";
					             $('form[name=listManageForm] INPUT[type=checkbox]').each(function(){
					                if ($(this).is(':checked')) {
					                    itemIds = setSeparator(itemIds);
					                    itemIds += $(this).val();
					                }
					            });
					            $("INPUT[type=checkbox][name=translateToLang]").each(function(){
					                 if ($(this).is(':checked')) {
					                    translateToLang = setSeparator(translateToLang);
					                    translateToLang += $(this).val();
					                    listIds = setSeparator(listIds);
					                    listIds += $("#listId_" + $(this).val() + " option:selected").val();
					                 }
					            });
		                        $.ajax({
		                            type: "POST",
		                            url: "/admin/stm_settings.html?act=translateNews&langId=" + langId + "&itemIds=" + itemIds + "&translateToLang=" + translateToLang + "&listIds=" +listIds,
		                            dataType: "html",
		                            success: function(html, stat) { 
		                              window.location.reload();
		                            },
		                            error: function(html, stat) {
		                                alert("Internal server error occured");
		                            }
		                        });
		                }} });
                },
                error: function(html, stat) {
                    alert("Internal server error occured");
                }
            }); 
            $("#translateDialog").bind( "dialogclose", function(event, ui) {
                $(this).remove();
              })
            $("#translateDialog").dialog('open');         
        }
        
        function setSeparator(str) {
            if(str != "") {
			    return str += ",";
			}
			return str;
        }
   
        ]]>
        </xsl:text>
    </script>
</head>
<body style="behavior: url(/script/webservice.htc) url(/script/clientmanager.htc)"
    id="ClientManager" xmlID="{@id}">    
    <xsl:call-template name="NegesoBody">
        <xsl:with-param name="helpLink">
            <xsl:text>/admin/help/cnw1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
        </xsl:with-param>            
        <xsl:with-param name="backLink">?command=get-categories-command&amp;moduleId=<xsl:value-of select="//negeso:list/@moduleId" /></xsl:with-param>
    </xsl:call-template>
    <xsl:call-template name="buttons"/>
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
            <b>
                <xsl:value-of select="name()" />
            </b>
            <br/>
            <xsl:for-each select="./attribute::*">
                <xsl:value-of select="name()" /> = <xsl:value-of select="." /><br/>
            </xsl:for-each>
            <br/>
            <i>Text: </i>
            <xsl:value-of select="text()" />
            <br/>
            <div style="margin-left: 30px">
                <xsl:call-template name="iter"/>
            </div>
        </xsl:for-each>
    </xsl:template>
    
<!-- ************************************* List *********************************** -->

<xsl:template match="negeso:list" mode="admContent">
    <input id="listLangId" type="hidden" value="{@langId}"/>
    <form method="POST" name="listManageForm" enctype="multipart/form-data">
        <xsl:call-template name="NegesoForm"/>
        <table cellpadding="0" cellspacing="0"  width="100%">
            <tr>
                <td align="center" class="admNavPanelFont">
                    <xsl:call-template name="tableTitle">
                        <xsl:with-param name="headtext">
                            <xsl:value-of select="java:getString($dict_news_module, 'MANAGE_NEWS_LIST')"/> "<xsl:value-of select="@name"/>"
                        </xsl:with-param>
                    </xsl:call-template>
                </td>
            </tr>
            <tr>
                <td class="admTableTDLast">
                    <div class="admNavPanelInp">
                        <div class="imgL"></div>
                        <div>
                            <input class="admNavPanelInp" type="button">
                                <xsl:if test="@canContribute != 'true'">
                                    <xsl:attribute name="disabled">true</xsl:attribute>
                                </xsl:if>
                                <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_news_module, 'ADD_ITEM')"/> </xsl:attribute>
                                <xsl:if test="@canContribute = 'true'">
                                    <xsl:attribute name="onClick">AddListItem();</xsl:attribute>
                                </xsl:if>
                            </input>
                        </div>
                        <div class="imgR"></div>
                    </div>
                    <xsl:if test="not (//negeso:list/@type='dynamic_item')">
	                    <div class="admNavPanelInp">
	                        <div class="imgL"></div>
	                        <div>
	                            <a class="admNavPanelInp">
	                                <xsl:attribute name="href"> javaScript:OpenArchive(<xsl:value-of select="@id"/>)</xsl:attribute>
	                              <xsl:value-of select="java:getString($dict_news_module, 'OPEN_ARCHIVE')"/>
	                            </a>
	                        </div>
	                        <div class="imgR"></div>
	                    </div>
                    </xsl:if>
                </td>
            </tr>
        </table>
        <table cellpadding="0" cellspacing="0"  class="admTable" border="0">
            <tr>
                <td class="admTDtitles" align="center" style="padding-left:0px;" >                    
                    <input type="checkbox" name="selectItems" onClick="SelectItems(this)"/>
                </td>
                <td class="admTDtitles" align="center" style="padding-left:0px;" ><xsl:value-of select="java:getString($dict_common, 'TITLE')"/></td>
                <td class="admTDtitles" align="center" style="padding-left:0px;"><xsl:value-of select="java:getString($dict_common, 'PUBLISH_DATE')"/></td>
                <td class="admTDtitles" align="center" style="padding-left:0px;"><xsl:value-of select="java:getString($dict_common, 'EXPIRED_DATE')"/></td>
                <td class="admTDtitles" colspan="5" align="center" style="padding-left:0px;"><xsl:value-of select="java:getString($dict_common, 'ACTION')"/></td>
            </tr>
            <xsl:apply-templates select="negeso:listItem"/>
            <tr>
                <td class="admTableFooter" colspan="5">&#160;</td>
            </tr>
        </table>
    </form>
    <script>
    	var listManageForm=document.forms["listManageForm"];
    </script>
</xsl:template>

<!-- ************************************* ListItem ******************************* -->
<xsl:template match="negeso:listItem">
    <tr>
        <th class="admTableTD" align="center" style="padding:0 10px 0 10px;" >
            <input type="checkbox" name="checkbox_{@id}" value="{@id}" canManage="{@canManage}" itemTitle = "{@title}">
                <xsl:if test="not(@linked='')" >
                    <xsl:attribute name="linked"><xsl:value-of select="@linked" /></xsl:attribute>
                </xsl:if>
            </input>
        </th>
        <th class="admTableTD">
            <a class="admAnchor">
                <xsl:attribute name="href">?command=get-list-item-command&amp;listId=<xsl:value-of select="@listId"/>&amp;listItemId=<xsl:value-of select="@id"/></xsl:attribute>
                <xsl:value-of select="@title" disable-output-escaping="yes"/>
            </a>
            <xsl:if test="@linked != ''"> (<xsl:value-of select="java:getString($dict_common, 'LINK')"/>)</xsl:if>
            <xsl:if test="/negeso:list/@canManage != 'true' or @canManage != 'true'">
                <img src="/images/small_lock.png"/>
            </xsl:if>
        </th>
        <th class="admTableTD"><xsl:value-of select="@publishDate"/></th>
        <th class="admTableTD"><xsl:value-of select="@expiredDate"/></th>
        <td class="admTableTDLast">
            <img class="admHand">
                <xsl:attribute name="src">
                    <xsl:choose>
                        <xsl:when test="/negeso:list/@canManage = 'true'">/images/up.png</xsl:when>
                        <xsl:otherwise>/images/up_gray.png</xsl:otherwise>
                    </xsl:choose>
                </xsl:attribute>
                <xsl:if test="/negeso:list/@canManage = 'true'">
                    <xsl:attribute name="onClick">MoveUpListItem(<xsl:value-of select="@id"/>);</xsl:attribute>
                </xsl:if>
                <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'UP')"/></xsl:attribute>
            </img>
        </td>
        <td class="admTableTDLast">
            <img class="admHand">
                <xsl:attribute name="src">
                    <xsl:choose>
                        <xsl:when test="/negeso:list/@canManage = 'true'">/images/down.png</xsl:when>
                        <xsl:otherwise>/images/down_gray.png</xsl:otherwise>
                    </xsl:choose>
                </xsl:attribute>
                <xsl:if test="/negeso:list/@canManage = 'true'">
                    <xsl:attribute name="onClick">MoveDownListItem(<xsl:value-of select="@id"/>);</xsl:attribute>
                </xsl:if>
                <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'DOWN')"/></xsl:attribute>
            </img>
        </td>
        <td class="admTableTDLast">
                <xsl:choose>
                    <xsl:when test="/negeso:list/@canManage = 'true' and @canManage = 'true' and @linked = ''">
                    	<a class="admAnchor">
                			<xsl:attribute name="href">?command=get-list-item-command&amp;listId=<xsl:value-of select="@listId"/>&amp;listItemId=<xsl:value-of select="@id"/></xsl:attribute>
	                    	<img class="admHand" id="container{@id}" listItemId="{@id}" containerId="{@containerId}" border="0" style="border:0">
	                        <xsl:attribute name="src">/images/edit.png</xsl:attribute> 
		                    	<xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'EDIT')"/></xsl:attribute>
	                    	</img>
                    	</a>
                    </xsl:when>
                    <xsl:otherwise>
                        <img class="admHand" id="container{@id}" listItemId="{@id}" containerId="{@containerId}">
                        <xsl:attribute name="src">/images/edit.png</xsl:attribute>
	                    	<xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'EDIT')"/></xsl:attribute>
                    	</img>
                    </xsl:otherwise>
                </xsl:choose>
        </td>
        <td class="admTableTDLast">
            <xsl:if test="not (//negeso:list/@type='dynamic_item')">
	            <img class="admHand" id="container{@id}" listItemId="{@id}" containerId="{@containerId}">
	                <xsl:choose>
	                    <xsl:when test="/negeso:list/@canManage = 'true' and @canManage = 'true' and @linked = ''">
	                        <xsl:attribute name="src">/images/lock.png</xsl:attribute>
	                        <xsl:attribute name="onClick">ChangeContainer('container<xsl:value-of select="@id"/>');</xsl:attribute>    
	                    </xsl:when>
	                    <xsl:otherwise>
	                        <xsl:attribute name="src">/images/lock_gray.png</xsl:attribute>
	                    </xsl:otherwise>
	                </xsl:choose>
	                <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'CHANGE_CONTAINER')"/></xsl:attribute>
	            </img>
            </xsl:if>
        </td>
        <td class="admTableTDLast">
            <img class="admHand">
                <xsl:choose>
                    <xsl:when test="/negeso:list/@canManage = 'true' and @canManage = 'true'">
                        <xsl:attribute name="src">/images/delete.png</xsl:attribute>
                        <xsl:attribute name="onClick">DeleteListItem(<xsl:value-of select="@id"/>);</xsl:attribute>    
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:attribute name="src">/images/delete_gray.png</xsl:attribute>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'DELETE')"/></xsl:attribute>
            </img>
        </td>
    </tr>
</xsl:template>

<!--******************** Bottom buttons ********************-->
<xsl:template name="buttons">
    <table cellpadding="0" cellspacing="0"  width="764px" align="center" class="Buttons">
        <tr>
            <td>
                <xsl:if test="not (//negeso:list/@type='dynamic_item')">
	                <div class="admBtnGreenb">
	                    <div class="imgL"></div>
	                    <div>
	                        <a class="admBtnText" id="moveToArchive" focus="blur()" onClick="MoveItemsToArchive()" href="#MoveItemsToArchive();">
	                            <xsl:choose>
	                                <xsl:when test="@canManage = 'true'">
	                                    <xsl:attribute name="href">javaScript:MoveItemsToArchive()</xsl:attribute>
	                                </xsl:when>
	                                <xsl:otherwise>
	                                    <xsl:attribute name="href">#</xsl:attribute>
	                                </xsl:otherwise>
	                            </xsl:choose>
	                            <xsl:value-of select="java:getString($dict_news_module, 'MOVE_TO_ARCHIVE')"/>
	                        </a>
	                    </div>
	                    <div class="imgR"></div>
	                </div>
                </xsl:if>
                <div class="admBtnGreenb" >
                    <div class="imgL"></div>
                    <div>
                        <a class="admBtnText" id="moveToOtherList">
                            <xsl:attribute name="href">javaScript:MoveItemsToOtherList()</xsl:attribute>
                            <xsl:value-of select="java:getString($dict_news_module, 'MOVE_TO_OTHER_LIST')"/>
                        </a>
                    </div>
                    <div class="imgR"></div>
                </div>
                <div class="admBtnGreenb" >
                    <div class="imgL"></div>
                    <div>
                        <a class="admBtnText" id="moveToOtherList" style="width:90px;">
                            <xsl:attribute name="href">javaScript:getNewsTranslationDialog()</xsl:attribute>
                            Translate
                        </a>
                    </div>
                    <div class="imgR"></div>
                </div>
                
                <div class="admBtnGreenb" style="padding-right:20px;float:right;">
                    <div class="imgL"></div>
                    <div>
                        <a class="admBtnText" id="deleteItems" focus="blur()" onClick="DeleteItems()" href="#DeleteItems();">
                            <xsl:choose>
                                <xsl:when test="@canManage = 'true'">
                                    <xsl:attribute name="href">javaScript:DeleteItems()</xsl:attribute>
                                </xsl:when>
                                <xsl:otherwise>

                                    <xsl:attribute name="href">#</xsl:attribute>
                                </xsl:otherwise>
                            </xsl:choose>
                            <xsl:value-of select="java:getString($dict_common, 'DELETE')"/>
                        </a>
                    </div>
                    <div class="imgR"></div>
                </div>
            </td>
        </tr>
    </table>
</xsl:template>


    <!-- *************************************NegesoForm******************************* -->
<xsl:template name="NegesoForm">
   <input type="hidden" name="command" value="get-list-command"/>
   <input type="hidden" name="listItemId"/>
   <input type="hidden" name="listItems"/>
   <input type="hidden" name="action"/>
   <input type="hidden" name="listId">
       <xsl:attribute name="value"><xsl:value-of select="@id"/></xsl:attribute>
   </input>
   <input type="hidden" name="summary" value="true"/>
   <input type="hidden" name="moduleId" value="{//negeso:list/@moduleId}"/>
   <input type="hidden" name="details" value="true"/>
   <input type="hidden" name="moveToId"/>
   <input type="hidden" name="isMove"/>
</xsl:template>

</xsl:stylesheet>
