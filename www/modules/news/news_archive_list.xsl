<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${list.xsl}       
 
  Copyright (c) 2003 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  @version      2004.04.24
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

<xsl:template match="/">
<html>
<head>
    <title><xsl:value-of select="java:getString($dict_news_module, 'MANAGE_NEWS_ARCHIVE')"/></title>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    
    <script type="text/javascript" src="/script/jquery.min.js"></script>
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
    <script type="text/javascript" src="/script/common_functions.js"></script>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    <script language="JavaScript">
            var s_NoItemsSelected = "<xsl:value-of select="java:getString($dict_common, 'NO_ITEMS_SELECTED')"/>";
            var s_DeleteConfirmation = "<xsl:value-of select="java:getString($dict_common, 'DELETE_CONFIRMATION')"/>";
            var s_LinkCannotBeRestoredFromArchiveDirectly = "<xsl:value-of select="java:getString($dict_news_module, 'LINK_CANNOT_BE_RESTORED')"/>";

        <xsl:text disable-output-escaping="yes">
        <![CDATA[        
    
        function DeleteListItem(id) {
            if (confirm(s_DeleteConfirmation)) {
                document.listManageForm.command.value = "delete-from-archive-command"
                document.listManageForm.listItemId.value = id
                document.listManageForm.submit();
            }
        }
        
        function GetSelectedItems() {
            var items = "";
            var n = listManageForm.getElementsByTagName("input").length;
            for (i=0; i<n; i++) {
                if (listManageForm.elements[i].type == "checkbox" &&
                    listManageForm.elements[i].checked)
                    items = items + listManageForm.elements[i].value + ';';
            }
            return items;
        }
        
        function GetSelectedItemsForRestoring() {
            var items = "";
            var n = listManageForm.getElementsByTagName("input").length;
            for (i=0; i<n; i++) {
                if (listManageForm.elements[i].type == "checkbox" &&
                    listManageForm.elements[i].checked)
                {
                    if (listManageForm.elements[i].linked != "") {
                        alert("'" + listManageForm.elements[i].itemTitle + "'\n" +
                            s_LinkCannotBeRestoredFromArchiveDirectly);
                        throw "LINK";
                    }
                    items = listManageForm.elements[i].value + ';' + items;
                }
            }
            return items;
        }

        function DeleteItems() {
            document.listManageForm.command.value = "delete-from-archive-command";
            document.listManageForm.listItems.value = GetSelectedItems();
            if (document.listManageForm.listItems.value != '')
                document.listManageForm.submit();
            else
                window.alert(s_NoItemsSelected);
        }
        
        function RestoreItems() {
            document.listManageForm.command.value = "restore-from-archive-command";
            try {
                document.listManageForm.listItems.value = GetSelectedItemsForRestoring();
                if (document.listManageForm.listItems.value != '')
                    document.listManageForm.submit();
                else
                    window.alert(s_NoItemsSelected);
            }
            catch (e) {
            }
        }
        
        function SelectItems(obj) {
            $(obj).attr('checked',!$(obj).attr('checked'));
            $('form[name=listManageForm] INPUT[type=checkbox]').each(function(){
                $(this).attr('checked',!$(this).attr('checked'));
            });
        }
        
        // Sets state of button "Delete" and "Restore form archive"
        function SetButtonsState() {
            var disabled = true;
            var n = listManageForm.getElementsByTagName("input").length;
            for (i=0; i<n; i++) {
                if (listManageForm.elements[i].type == "checkbox" &&
                    !listManageForm.elements[i].disabled) {
                        disabled = false;
                        break;
                }
            }
            if (disabled) {
                document.all.restoreItems.disabled = true;
                document.all.restoreItems.href = "#";
                document.all.deleteItems.disabled = true;
                document.all.deleteItems.href = "#";
            }
        }
        ]]>
        </xsl:text>
    </script>
</head>
<body>
    <xsl:choose>
        <xsl:when test="negeso:newsArchive/@backLinkTo='module' and //negeso:newsArchive/@moduleId">
            <xsl:call-template name="NegesoBody">
                <xsl:with-param name="backLink" select="concat('?command=get-categories-command&amp;moduleId=', //negeso:newsArchive/@moduleId)" />
                <xsl:with-param name="helpLink">
                    <xsl:text>/admin/help/cnw1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
                </xsl:with-param>
            </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
            <xsl:call-template name="NegesoBody">
                <xsl:with-param name="backLink" select="concat('?command=get-list-command&amp;listId=', /negeso:newsArchive/@listId)" />
                <xsl:with-param name="helpLink">
                    <xsl:text>/admin/help/cnw1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
                </xsl:with-param>
            </xsl:call-template>
        </xsl:otherwise>
    </xsl:choose>
    <xsl:call-template name="buttons"/>
    <script>SetButtonsState();</script>
</body>
</html>

</xsl:template>

<!-- ************************************* newsArchive *********************************** -->
<xsl:template match="negeso:newsArchive" mode="admContent">
    <form method="POST" name="listManageForm">
        <xsl:call-template name="NegesoForm"/>
        <xsl:apply-templates select="negeso:newsArchive" />
        <table cellpadding="0" cellspacing="0"  width="100%">
            <tr>
                <td align="center" class="admNavPanelFont">
                    <xsl:value-of select="java:getString($dict_news_module, 'MANAGE_NEWS_ARCHIVE')"/>                    
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
                <td class="admTDtitles" colspan="2" align="center" style="padding-left:0px;"><xsl:value-of select="java:getString($dict_common, 'ACTION')"/></td>
            </tr>
            <xsl:apply-templates select="negeso:archivedListItem"/>
            <tr>
                <td class="admTableFooter" colspan="6">&#160;</td>
            </tr>
        </table>
    </form>
</xsl:template>

<!-- ************************************* ArchivedListItem ******************************* -->
<xsl:template match="negeso:archivedListItem" >
    <tr>
        <th class="admTableTD" align="center" style="padding:0 10px 0 10px;" >
            <input type="checkbox" name="checkbox_{@id}" value="{@id}" linked="{@linked}" itemTitle = "{@title}">
                <xsl:if test="@canManage != 'true'">
                    <xsl:attribute name="disabled">true</xsl:attribute>
                </xsl:if>
            </input>
         </th>
         <th class="admTableTD">
             <a class="admAnchor">
                 <xsl:attribute name="href">?command=get-archived-list-item-command&amp;listItemId=<xsl:value-of select="@id"/>&amp;listId=<xsl:value-of select="/negeso:newsArchive/@listId"/>&amp;articleType=1
                 </xsl:attribute>
                 <xsl:value-of select="@title" disable-output-escaping="yes"/>
             </a>
             <xsl:if test="@linked != ''"> (<xsl:value-of select="java:getString($dict_common, 'LINK')"/>)</xsl:if>
         </th>
         <td class="admTableTD admTDtitlesCenter"><xsl:value-of select="@publishDate"/></td>
         <td class="admTableTD admTDtitlesCenter"><xsl:value-of select="@expiredDate"/></td>
         <td class="admTableTD admWidth30">
            <a class="admAnchor">
                <xsl:attribute name="href">?command=get-archived-list-item-command&amp;listItemId=<xsl:value-of select="@id"/>&amp;listId=<xsl:value-of select="/negeso:newsArchive/@listId"/>&amp;articleType=1</xsl:attribute>
	            <img class="admHand" id="container{@id}" listItemId="{@id}" containerId="{@containerId}" border="0" style="border:0">
	            <xsl:attribute name="src">/images/edit.png</xsl:attribute> 
		            <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'EDIT')"/></xsl:attribute>
	            </img>
            </a>
        </td>
         <td class="admTableTDLast admWidth30">
             <img class="admHand">
                 <xsl:attribute name="src">
                     <xsl:choose>
                         <xsl:when test="@canManage = 'true'">/images/delete.png</xsl:when>
                         <xsl:otherwise>/images/delete_gray.png</xsl:otherwise>
                     </xsl:choose>
                 </xsl:attribute>
                 <xsl:if test="@canManage = 'true'">
                     <xsl:attribute name="onClick">DeleteListItem(<xsl:value-of select="@id"/>)</xsl:attribute>
                 </xsl:if>
                 <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'DELETE')"/></xsl:attribute>
             </img>
         </td>
    </tr>
</xsl:template>

<!-- *************************************NegesoForm******************************* -->
<xsl:template name="NegesoForm">
     <input type="hidden" name="command" value="get-list-command"/>
     <input type="hidden" name="listItemId"/>
     <input type="hidden" name="listItems"/>
     <input type="hidden" name="listId" value="{/negeso:newsArchive/@listId}"/>
</xsl:template>

<!--******************** Bottom buttons ********************-->
<xsl:template name="buttons">
    <table cellpadding="0" cellspacing="0"  width="764px" align="center" class="Buttons">
        <tr>
            <td>                
                <div class="admBtnGreenb">
                    <div class="imgL"></div>
                    <div>
                        <a id="restoreItems" class="admBtnText" href="javaScript:RestoreItems()" focus="blur()">
                            <xsl:value-of select="java:getString($dict_news_module, 'RESTORE')"/>
                        </a>
                    </div>
                    <div class="imgR"></div>
                </div>
                <div class="admBtnGreenb" style="padding-right:20px;float:right;">
                    <div class="imgL"></div>
                    <div>
                        <a class="admBtnText" id="deleteItems" focus="blur()" href="javaScript:DeleteItems()">
                            <xsl:value-of select="java:getString($dict_common, 'DELETE')"/>
                        </a>
                    </div>
                    <div class="imgR"></div>
                </div>
            </td>
        </tr>
    </table>
</xsl:template>
    
</xsl:stylesheet>
