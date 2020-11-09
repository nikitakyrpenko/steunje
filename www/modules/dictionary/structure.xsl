<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${structure.xsl}
 
  Copyright (c) 2003 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.

  @version      2004.06.07
  @author       Sergiy Oliynyk
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>

<xsl:include href="/xsl/negeso_header.xsl"/>

<xsl:template match="/">
<html>
<head>
    <script type="text/javascript" src="/script/jquery.min.js" />
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script language="JavaScript" src="/script/common_functions.js" type="text/javascript">/**/</script>
    <title>Change dictionary structure</title>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>

    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    <script>
    <xsl:text disable-output-escaping="yes">
        <![CDATA[

        function DeleteConstant(id, entryId) {
            if (confirm("Are you sure you want to delete the constant \'" + entryId + " \'?")) {
                dictionaryManageForm.command.value = "manage-dictionary-command";
                dictionaryManageForm.action.value = "delete";
                dictionaryManageForm.id.value = id;
                dictionaryManageForm.submit();
            }
        }

        function MoveConstant(id) {
            var dictionaryFileId = dictionaryManageForm.dictionaryFileId.value;
            var result = window.showModalDialog(
                "?command=manage-dictionary-command&dictionaryFileId=" +
                    dictionaryFileId + "&action=files",
                this,
                "dialogHeight: 290px; dialogWidth: 585px; help: No; scroll: No; status: No;");
            if (result != null) {
                dictionaryManageForm.action.value = "move";
                dictionaryManageForm.id.value = id;
                dictionaryManageForm.moveToId.value = result[0];
                dictionaryManageForm.isMove.value = result[1];
                dictionaryManageForm.submit();
            }
        }

        function EditConstant(id) {
            var result = window.showModalDialog(
                "?command=manage-dictionary-command&id=" + id + "&action=get",
                this,
                "dialogHeight: 250px; dialogWidth: 585px; help: No; scroll: No; status: No;");
            if (result != null) {
                dictionaryManageForm.action.value = "update";
                dictionaryManageForm.id.value = id;
                dictionaryManageForm.entryId.value = result[0];
                dictionaryManageForm.entry.value = result[1];
                dictionaryManageForm.submit();
            }
        }

        function ReturnChosenFile(select) {
            var result = new Array();
            result[0] = select.item(select.selectedIndex).value;
            result[1] = dictionaryManageForm.move.checked;
            window.returnValue = result;
            window.close();
        }

        function ReturnConstantValue(entryId, entry) {
            var result = new Array();
            result[0] = dictionaryManageForm.entryId.value.toUpperCase().
                replace(' ', '_');
            result[1] = dictionaryManageForm.entry.value;
            window.returnValue = result;
            window.close();
        }

        function validateEntry(entryId) {
            var callObj = ClientManager.CMSCreateAuxCallOptions();
            callObj.setCommand("validate-entry-command");
            callObj.setParam("entryId", entryId);
            var result = ClientManager.CMSUpdateEntity(callObj);
            if (!result.error && result.value != "") {
                var tree = StringUtil.asTree(result.value);
                var resName = tree.selectSingleNode("/negeso:resultName").text;
                if (resName != "ok") {
                    alert("\"" + entryId + "\" already exists in the " + resName);
                    return false;
                }
                return true;
            }
            else return false;
        }

        function AddNewConstant(dictionaryFileId) {
            var result = window.showModalDialog(
                "?command=manage-dictionary-command&action=add",
                this,    
                "dialogHeight: 250px; dialogWidth: 585px; help: No; scroll: No; status: No;");
            if (result != null && validateEntry(result[0])) {
                dictionaryManageForm.action.value = "add";
                dictionaryManageForm.entryId.value = result[0];
                dictionaryManageForm.entry.value = result[1];
                dictionaryManageForm.submit();
            }
        }
    ]]>
    </xsl:text>
    </script>
</head>
<body style="behavior: url(/script/webservice.htc) url(/script/clientmanager.htc)"
    id="ClientManager">
    <xsl:choose>
        <xsl:when test="negeso:dictionaryFile"/>
        <xsl:otherwise>
            <xsl:attribute name="class">menu</xsl:attribute>            
        </xsl:otherwise>
    </xsl:choose>
   	<!-- NEGESO HEADER -->
	<xsl:call-template name="NegesoHeader" />
	<div align="center">
		<!-- CONTENT
        In real life only one of these items exists in XML at a time -->
        <xsl:apply-templates select="negeso:dictionaryFile"/>
        <xsl:apply-templates select="negeso:dictionary" mode="edit"/>
        <xsl:apply-templates select="negeso:dictionaries"/>
 	</div>
</body>
</html>
</xsl:template>

<xsl:template match="negeso:dictionaryFile">
    <xsl:call-template name="NavBar">
        <xsl:with-param name="backLink" select="concat('?selected=', @id)"/>
    </xsl:call-template>
	<xsl:call-template name="tableTitle">
		<xsl:with-param name="headtext">
            Edit '<xsl:value-of select="@description"/>'
		</xsl:with-param>
	</xsl:call-template>
	<form method="POST" name="dictionaryManageForm" enctype="multipart/form-data">
        <input type="hidden" name="command" value="manage-dictionary-command"/>
        <input type="hidden" name="dictionaryFileId" value="{@id}"/>
        <input type="hidden" name="action"/>
        <input type="hidden" name="id"/>
        <input type="hidden" name="moveToId"/>
        <input type="hidden" name="isMove"/>
        <input type="hidden" name="entryId"/>
        <input type="hidden" name="entry"/>

        <table cellpadding="0" cellspacing="0"  class="admNavPanel">
         <tr>
            <td class="admNavPanel admNavbar admCenter">
              <input class="admNavbarInp" type="button" onClick="AddNewConstant({@id});" value="&lt;&#160;Add a new constant&#160;&gt;"/>
            </td>
         </tr>
        </table>
    
        <table cellpadding="0" cellspacing="0"  class="admNavPanel">
            <tr>
                <td class="admNamebar">String identifier</td>
                <td class="admNamebar">String in English</td>
                <td class="admNamebar" colspan="3">&#160;</td>
            </tr>
            <xsl:apply-templates select="negeso:dictionary" mode="list"/>
        </table>
    </form>
     <xsl:call-template name="NavBar">
        <xsl:with-param name="backLink" select="concat('?selected=', @id)"/>
    </xsl:call-template>
</xsl:template>

<xsl:template match="negeso:dictionary" mode="list">
    <xsl:variable name="readonly">
        <xsl:choose>
            <xsl:when test="@entryId = 'BACK'">true</xsl:when>
            <xsl:when test="@entryId = 'CLOSE'">true</xsl:when>
            <xsl:otherwise>false</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <tr>
        <td class="admMainTD" style="font-size: 14px">
            <div style="width: 300px; word-wrap: break-word; overflow: hidden">
                <xsl:value-of select="@entryId"/>
            </div>
        </td>
        <td class="admMainTD" style="font-size: 14px; color: #0033CC;">
            <xsl:value-of select="@entry"/>
        </td>
        <td class="admLightTD admWidth30">
            <xsl:if test="$readonly='false'">
                <img class="admHand" src="/images/edit.gif" onClick="EditConstant({@id});">
                    <xsl:attribute name="title">Edit</xsl:attribute>
                </img>
            </xsl:if>
        </td>
        <td class="admDarkTD admWidth30">
            <xsl:if test="$readonly='false'">
                <img class="admHand" src="/images/move.gif" onClick="MoveConstant({@id});">
                    <xsl:attribute name="title">Copy to another dictionary file</xsl:attribute>
                </img>
            </xsl:if>
        </td>
        <td class="admDarkTD admWidth30">
            <xsl:if test="$readonly='false'">
                <img class="admHand" src="/images/delete.gif" onClick="DeleteConstant({@id}, '{@entryId}');">
                    <xsl:attribute name="title">Delete</xsl:attribute>
                </img>
            </xsl:if>
        </td>
    </tr>
</xsl:template>

<xsl:template match="negeso:dictionary" mode="edit">
    <xsl:call-template name="tableTitle">
		<xsl:with-param name="headtext">
            Edit the constant
		</xsl:with-param>
	</xsl:call-template>
    <form method="POST" name="dictionaryManageForm">
        <table cellpadding="0" cellspacing="0">
            <tr>
                <td valign="top">
                    <table cellpadding="0" cellspacing="0" class="admNavPanel">
                    <tr>
                        <td class="admMainTD" style="width: 200;">Constant name</td>
                        <td class="admLightTD"><input class="admTextArea" style="width:330" type="text" name="entryId" value="{@entryId}"/></td>
                    </tr>
                    <tr>
                        <td class="admMainTD">Value in English</td>
                        <td class="admLightTD"><input class="admTextArea" style="width:330" type="text" name="entry" value="{@entry}"/></td>
                    </tr>
                    </table>        
                </td>
            </tr>
        </table>
        <table cellpadding="0" cellspacing="0"  class="admNavPanel">
            <tr>
                <td class="admNavPanel admNavbar admCenter">
                    <button class="admNavbarInp admAction" onclick="ReturnConstantValue()">
                        &lt;&#160;Save&#160;&gt;
                    </button>
                    <input value="&lt;&#160;Reset&#160;&gt;" type="reset" class="admNavbarInp"/>
                </td>
            </tr>
        </table>
    </form>
</xsl:template>

<xsl:template match="negeso:dictionaries">
    <xsl:call-template name="tableTitle">
		<xsl:with-param name="headtext">
            Copy to dictionary file
		</xsl:with-param>
	</xsl:call-template>
    <form method="POST" name="dictionaryManageForm">
    <table cellpadding="0" cellspacing="0"  class="admNavPanel">
    <tr>
        <td class="admMainTD">
            <select name="dictionaryFile" style="width: 99%; font-size: 14px;">
                <xsl:apply-templates select="negeso:dictionaryFile" mode="option"/>
            </select>
        </td>
     </tr>
     <tr>
        <td class="admMainTD admCenter" style="color: #FF2B04; font-size: 14px;">
            <input type="checkbox" name="move">Move this constant</input>
        </td>
     </tr> 
     <tr>
        <td class="admNavPanel admNavbar admCenter">
            <button class="admNavbarInp admAction" onClick="ReturnChosenFile(dictionaryFile);">
                &lt;&#160;Copy&#160;&gt;
            </button>
            <button class="admNavbarInp admAction" onClick="window.close();">
                &lt;&#160;Cancel&#160;&gt;
            </button>
        </td>
    </tr>
    </table>
    </form>
</xsl:template>

<xsl:template match="negeso:dictionaryFile" mode="option">
    <option value="{@id}"><xsl:value-of select="@description"/></option>
</xsl:template>

</xsl:stylesheet>
