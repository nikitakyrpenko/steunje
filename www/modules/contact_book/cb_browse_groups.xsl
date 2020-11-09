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
<xsl:variable name="dict_contact_book" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_contact_book.xsl', $lang)"/>

<!-- NEGESO JAVASCRIPT Temaplate -->    
<xsl:template name="java-script">
    <script language="JavaScript">
    var s_SearchStringIsEmpty = "<xsl:value-of select="java:getString($dict_contact_book, 'SEARCH_STRING_IS_EMPTY')"/>";
    var s_DeleteGroupConfirmation = "<xsl:value-of select="java:getString($dict_common, 'DELETE_GROUP_CONFIRMATION')"/>";

    <xsl:text disable-output-escaping="yes">
        <![CDATA[
        // PUBLICATION MANAGEMENT
        function addGroup() {
            document.operateForm.action.value = "add_group";
        }

        function editGroup(targetId) {
            document.operateForm.action.value = "show_group";
            document.operateForm.groupId.value = targetId;
            document.operateForm.submit();
        }

        function deleteGroup(targetId) {
            if (confirm(s_DeleteGroupConfirmation)) {
                document.operateForm.action.value = "remove_group";
	            document.operateForm.groupId.value = targetId;
                document.operateForm.submit();
                return true;
            }
            return false;
        }

        function browseGroup(targetId) {
            document.operateForm.command.value = "cb-manage-contacts";
            document.operateForm.action.value = "";
            document.operateForm.groupId.value = targetId;
            document.operateForm.submit();
        }
        
        function submitIt(e) {
        	if (e.keyCode == 13) {
        		searchContacts();
        	}
        }
        
        function searchContacts(targetId) {
        	if ( document.operateForm.search_string.value == "" ){
        		alert(s_SearchStringIsEmpty);
        		return;
        	}
            document.operateForm.command.value = "cb-manage-contacts";
            document.operateForm.action.value = "search_contacts";
            document.operateForm.submit();
        }

        ]]>
        </xsl:text>
    </script>
</xsl:template>

<!-- MAIN ENTRY -->
<xsl:template match="/">
<html>
    <head>
        <title><xsl:value-of select="java:getString($dict_contact_book, 'BROWSE_GROUPS')"/></title>
        <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
        <script type="text/javascript" src="/script/jquery.min.js"></script>
        <script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
        <script type="text/javascript" src="/script/cufon-yui.js"></script>
        <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
        <script language="JavaScript" src="/script/common_functions.js" type="text/javascript">/**/</script>
        <xsl:call-template name="java-script"/>
        <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    </head>
    <body>
        <xsl:call-template name="NegesoBody">
            <xsl:with-param name="helpLink" select="''"/>
            <xsl:with-param name="backLink" select="''"/>
        </xsl:call-template>    
    </body>
</html>
</xsl:template>

<xsl:template match="negeso:page" mode="admContent">
    <table cellspacing="0" cellpadding="0" border="0" align="center" width="100%" class="admTable">
        <tr>
            <td style="width:auto; height:auto; padding-left:20px;">
                <!-- PATH -->
                <xsl:call-template name="cb.groupPath"/>
            </td>
        </tr>
        <tr>
            <td class="admNavPanelFont" align="center">            
                <!-- TITLE -->
                <xsl:call-template name="tableTitle">
                    <xsl:with-param name="headtext">
                        <xsl:value-of select="java:getString($dict_contact_book, 'BROWSE_GROUPS')"/>
                    </xsl:with-param>
                </xsl:call-template>
            </td>
        </tr>
        <tr>
            <td>
                <!-- Content -->
                <xsl:apply-templates select="negeso:cb-groups"/>
            </td>
        </tr>
        <tr>
            <td class="admTableFooter" >&#160;</td>
        </tr>
    </table>
</xsl:template>

<!-- ********************************** Category *********************************** -->
<xsl:template match="negeso:cb-groups">
    <!-- Render HEADER -->
    <form method="POST" name="operateForm" action="">
        <input type="hidden" name="command" value="cb-manage-groups"></input>
        <input type="hidden" name="action" value=""></input>
        <input type="hidden" name="groupId" value="-1"></input>

        <xsl:call-template name="cb.search"/>

        <table cellspacing="0" cellpadding="0" width="100%" border="0">
            <tr>
                <td class="admTDtitles"><xsl:value-of select="java:getString($dict_common, 'TITLE')"/></td>
                <td width="30%" class="admTDtitles"><xsl:value-of select="java:getString($dict_contact_book, 'CONTACTS')"/></td>
                <td class="admTDtitles" colspan="2">&#160;</td>
            </tr>
            <xsl:apply-templates select="negeso:cb-group"/>           
        </table>
    </form>
</xsl:template>


<!-- ********************************** Location *********************************** -->
<xsl:template match="negeso:cb-group">
    <tr>
        <th class="admTableTD">
            <a class="admAnchor" href="#" onClick = "return browseGroup({@id})">
                <xsl:value-of select="@title"/>&#160;
            </a>
        </th>
        <th class="admTableTD">
            <xsl:value-of select="@contact-amount"/>&#160;
        </th>
        <th class="admTableTDLast admWidth30">
            <img src="/images/edit.png" class="admHand" onClick="return editGroup({@id})">
                <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'EDIT')"/></xsl:attribute>
            </img>
        </th>
        <td class="admTableTDLast admWidth30">
            <img src="/images/delete.png" class="admHand" onClick="return deleteGroup({@id})">
                <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'DELETE')"/></xsl:attribute>
         </img>
        </td>
    </tr>
</xsl:template>

<xsl:template name="cb.groupPath">    
	<!-- Active pathe element - just print it-->
    <table border="0" cellpadding="0" cellspacing="0" align="left">
        <tr>
            <td style="padding:8px 0 0 5px;" valign="middle" class="admNavigation">
                <xsl:value-of select="java:getString($dict_contact_book, 'CONTACT_BOOK')"/>                
            </td>        
        </tr>
    </table>	
</xsl:template>

<xsl:template name="cb.search">
    <table cellspacing="0" cellpadding="0" width="100%" border="0">
        <tr>
            <th class="admTableTDLast admWidth150">Find contact</th>
            <td class="admTableTDLast admWidth150">
                <input class="admTextArea admWidth150" type="text" name="search_string" onkeypress="submitIt(event)"/>
            </td>
            <td class="admTableTDLast">
                <div style="padding:0;margin:0;" class="admNavPanelInp">
                    <div class="imgL"></div>
                    <div>
                        <button class="admNavbarInp" onClick = "searchContacts(); return false;">
                            <xsl:value-of select="java:getString($dict_common, 'SEARCH')"/>
                        </button>
                    </div>
                    <div class="imgR"></div>
                </div>
            </td>

            <td class="admTableTDLast admWidth150">
                <div style="padding:0;margin:0;" class="admNavPanelInp">
                    <div class="imgL"></div>
                    <div>
                        <input class="admNavbarInp" type="submit" onClick = "addGroup()">
                            <xsl:attribute name="value">
                                <xsl:value-of select="java:getString($dict_common, 'ADD_NEW_GROUP')"/>
                            </xsl:attribute>
                        </input>
                    </div>
                    <div class="imgR"></div>
                </div>
            </td>
        </tr>
    </table>
</xsl:template>

</xsl:stylesheet>