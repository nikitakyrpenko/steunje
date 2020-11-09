<?xml version="1.0" encoding="utf-8"?>
<!--
  @(#)$Id$     
 
  Copyright (c) 2004 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  TODO: file description here  

  @author       Stanislav Demchenko
  @version      $Revision$
-->


<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

    <xsl:variable name="lang" select="/*/@interface-language"/>
    <xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
    <xsl:variable name="dict_security_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_security_module.xsl', $lang)"/>
    <xsl:variable name="dict_modules" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_modules.xsl', $lang)"/>

    <xsl:variable name="action">
        <xsl:choose>
            <xsl:when test="/negeso:page/negeso:contents/negeso:containers">list</xsl:when>
            <xsl:when test="/negeso:page/negeso:context/negeso:action">
                <xsl:value-of select="/negeso:page/negeso:context/negeso:action"/>
            </xsl:when>
            <xsl:otherwise>list</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="title">
        <xsl:choose>
            <xsl:when test="$action='add'">
                <xsl:value-of select="java:getString($dict_security_module, 'NEW_CONTAINER')"/>
            </xsl:when>
            <xsl:when test="$action='edit'">
                <xsl:value-of select="java:getString($dict_security_module, 'EDIT_CONTAINER')"/>
            </xsl:when>
            <xsl:when test="$action='save'">
                <xsl:value-of select="java:getString($dict_security_module, 'SAVING_CHANGES')"/>
            </xsl:when>
            <xsl:when test="$action='delete'">
                <xsl:value-of select="java:getString($dict_security_module, 'CONTAINERS_OF_RESOURCES')"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="java:getString($dict_security_module, 'CONTAINERS_OF_RESOURCES')"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:include href="/xsl/negeso_body.xsl"/>
    <!-- **************** BEGIN MAIN TEMPLATE (COMMON FOR ALL MODES) *********** -->
    <xsl:template match="/negeso:page/negeso:contents">
        <html>
            <head>
                <title>
                    <xsl:value-of select="$title"/>
                </title>
                <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>                
                <script type="text/javascript" src="/script/jquery.min.js"></script>
				<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
                <script type="text/javascript" src="/script/cufon-yui.js"></script>
                <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
                <script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"/>
                
                <script language="JavaScript">
                    var s_DeleteContainerConfirmation = "<xsl:value-of select="java:getString($dict_security_module, 'DELETE_CONTAINER_CONFIRMATION')"/>";
                    var s_FillInContainerName = "<xsl:value-of select="java:getString($dict_security_module, 'FILL_IN_CONTAINER_NAME')"/>";

                    function add() {
                    window.location.href = "?action=add";
                    }

                    function save(){
                    document.add_edit.submit();
                    }

                    function edit(id) {
                    window.location.href = "?action=edit&amp;id="+id;
                    }

                    function del(id) {
                    if(!confirm(s_DeleteContainerConfirmation)) {
                    return;
                    }
                    window.location.href = "?action=delete&amp;id="+id;
                    }

                    function validateAddEditForm(id){
                    if(add_edit_form.name.value == ''){
                    alert(s_FillInContainerName);
                    add_edit_form.name.focus();
                    return;
                    }
                    add_edit_form.submit();
                    }

                    window.focus();

                </script>
            </head>
            <body>

                <!-- NEGESO HEADER -->
                <xsl:call-template name="NegesoBody">
                    <xsl:with-param name="helpLink">
                        <xsl:text>/admin/help/csc1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
                    </xsl:with-param>
                    <xsl:with-param name="backLink" select='"?action=list"' />
                </xsl:call-template>                
                    <xsl:call-template name="buttons"/>            
            </body>
        </html>
    </xsl:template>

    

    <!-- ******************* END MAIN TEMPLATE ********************************* -->

    <xsl:template match="negeso:contents"  mode="admContent">
        <!-- CONTENT -->
        <table cellpadding="0" cellspacing="0" align="center">
            <tr>
                <td>
                    <ul>
                        <xsl:for-each select="//negeso:context/negeso:error">
                            <li style="text-align: left; color: #ff0000;">
                                <xsl:value-of select="."/>
                            </li>
                        </xsl:for-each>
                    </ul>
                </td>
            </tr>
        </table>
        <xsl:choose>
            <xsl:when test="$action='add'">
                <xsl:apply-templates select="negeso:container" mode="add_edit"/>
            </xsl:when>
            <xsl:when test="$action='edit'">
                <xsl:apply-templates select="negeso:container" mode="add_edit"/>
            </xsl:when>
            <xsl:when test="$action='save'">
                <!-- ************ IGNORING ERRORS: FIXME *********** -->
                <xsl:apply-templates />
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="negeso:containers" mode="list"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <!-- *********************** BEGIN MODE "ADD_EDIT" ***************************** -->
    <xsl:template match="negeso:container" mode="add_edit">
        <form method="POST" enctype="multipart/form-data" id="add_edit_form" name="add_edit">
            <input type="hidden" name="action" value="save"/>
            <xsl:if test="@id">
                <input type="hidden" name="id" value="{@id}"/>
            </xsl:if>
            <table cellpadding="0" cellspacing="0" class="admTable">
                <tr>
                    <td align="center" class="admNavPanelFont" colspan="3" >
                        <xsl:call-template name="tableTitle">
                            <xsl:with-param name="headtext">
                                <xsl:value-of select="$title"/>
                            </xsl:with-param>
                        </xsl:call-template>
                    </td>
                </tr>
                <tr>
                    <td class="admNavPanel" colspan="3" style="padding: 0 0 0 20px;">
                        <a class="admAnchor" href="container_mngr">
                            <xsl:value-of select="java:getString($dict_modules, 'CONTAINERS')"/>
                        </a>&#160;&#160;&#160;
                        <a class="admAnchor" href="group_mngr">
                            <xsl:value-of select="java:getString($dict_modules, 'GROUPS')"/>
                        </a>&#160;&#160;&#160;
                        <a class="admAnchor" href="user_mngr">
                            <xsl:value-of select="java:getString($dict_modules, 'CONTRIBUTORS')"/>
                        </a>
                    </td>
                </tr>
                <tr>
                    <th class="admTableTD admWidth150">
                        <xsl:value-of select="java:getString($dict_security_module, 'CONTAINER')"/>*
                    </th>
                    <th class="admTableTDLast" colspan="2">
                        <input class="admTextArea admWidth200" type="text" value="{@name}" name="name"/>
                    </th>
                </tr>
                <tr>
                    <td align="center" class="admNavPanelFont" colspan="3" >
                    <xsl:call-template name="tableTitle">
                            <xsl:with-param name="headtext">
                                <xsl:value-of select="java:getString($dict_security_module, 'CONTAINER_CAN_BE_ACCESSED_BY_GROUPS')"/>
                            </xsl:with-param>
                        </xsl:call-template>
                    </td>
                </tr>
                <tr>
                    <td class="admTDtitles">
                        <xsl:value-of select="java:getString($dict_security_module, 'GROUP_TITLE')"/>
                    </td>
                    <td class="admTDtitles">
                        <xsl:value-of select="java:getString($dict_security_module, 'ROLE')"/>
                    </td>
                    <td class="admTDtitles">&#160;</td>
                </tr>
                <xsl:for-each select="negeso:groups/negeso:group">
                    <tr>
                        <th class="admTableTD">
                            <xsl:value-of select="@name"/>
                        </th>
                        <th class="admTableTD" width="100px">
                            <xsl:value-of select="@role-id"/>
                        </th>
                        <th class="admTableTDLast">
                            <input type="checkbox" name="groups" value="{@id}">
                                <xsl:if test="@linked='true'">
                                    <xsl:attribute name="checked"/>
                                </xsl:if>
                                <xsl:if test="@role-id = 'administrator'">
                                    <xsl:attribute name="checked"/>
                                    <xsl:attribute name="disabled"/>
                                </xsl:if>
                            </input>
                        </th>
                    </tr>
                </xsl:for-each>
                <tr>
                    <td class="admTableFooter" >&#160;</td>
                </tr>
            </table>
        </form>
    </xsl:template>
    <!-- *********************** END MODE "ADD_EDIT" *********************** -->

    <xsl:template match="negeso:context/*"/>
    <!-- DIRTY PATCH -->

    <!-- *********************** BEGIN MODE "LIST" ***************************** -->
    <xsl:template match="negeso:containers" mode="list">
        <form method="POST" enctype="multipart/form-data">
            <table cellpadding="0" cellspacing="0" class="admTable">
                <tr>
                    <td align="center" class="admNavPanelFont" colspan="3" >
                        <xsl:call-template name="tableTitle">
                            <xsl:with-param name="headtext">
                                <xsl:value-of select="$title"/>
                            </xsl:with-param>
                        </xsl:call-template>
                    </td>
                </tr>
                <tr>
                    <td class="admNavPanel" colspan="3" style="padding: 0 0 0 20px;">
                        <a class="admAnchor" href="container_mngr">
                            <xsl:value-of select="java:getString($dict_modules, 'CONTAINERS')"/>
                        </a>&#160;&#160;&#160;
                        <a class="admAnchor" href="group_mngr">
                            <xsl:value-of select="java:getString($dict_modules, 'GROUPS')"/>
                        </a>&#160;&#160;&#160;
                        <a class="admAnchor" href="user_mngr">
                            <xsl:value-of select="java:getString($dict_modules, 'CONTRIBUTORS')"/>
                        </a>
                    </td>
                </tr>
                  
                <tr>
                    <td class="admTDtitles" style="padding:0;" align="center">
                        <xsl:value-of select="java:getString($dict_security_module, 'CONTAINER')"/>
                    </td>
                    <td class="admTDtitles" colspan="2">&#160;</td>
                </tr>
                <xsl:for-each select="negeso:container">
                    <tr>
                        <th class="admTableTD">
                            <xsl:value-of select="@name"/>
                        </th>
                        <td class="admTableTD" width="1%">
                            <img src="/images/edit.png" class="admHand" onClick="edit({@id})">
                                <xsl:attribute name="title">
                                    <xsl:value-of select="java:getString($dict_common, 'EDIT')"/>
                                </xsl:attribute>
                            </img>
                        </td>
                        <td class="admTableTDLast" width="1%">
                            <xsl:if test="@name != 'Default'">
                                <img src="/images/delete.png" class="admHand" onClick="del({@id})">
                                    <xsl:attribute name="title">
                                        <xsl:value-of select="java:getString($dict_common, 'DELETE')"/>
                                    </xsl:attribute>
                                </img>
                            </xsl:if>
                        </td>
                    </tr>
                </xsl:for-each>
                <tr>
                    <td class="admTableFooter" >&#160;</td>
                </tr>
            </table>
        </form>
    </xsl:template>
    <!-- *********************** END MODE "LIST" *********************** -->
    <!--******************** Bottom buttons ********************-->
    <xsl:template name="buttons">
        <xsl:if test="/negeso:page/negeso:contents/negeso:containers">
            <table cellpadding="0" cellspacing="0"  width="764px" align="center" class="Buttons">
                <tr>
                    <td>
                        <div class="admBtnGreenb">
                            <div class="imgL"></div>
                            <div>
                                <input class="admNavbarInp" type="button" onClick="add()" style="width:180px;">
                                    <xsl:attribute name="value"> <xsl:value-of select="java:getString($dict_security_module, 'ADD_NEW_CONTAINER')"/>
                                    </xsl:attribute>
                                </input>
                            </div>
                            <div class="imgR"></div>
                        </div>
                    </td>
                </tr>
            </table>
        </xsl:if>
        <xsl:if test="$action='add' or $action='edit'">
            <table cellpadding="0" cellspacing="0"  width="764px" align="center" class="Buttons">
                <tr>
                    <td>
                        <div class="admBtnGreenb">
                            <div class="imgL"></div>
                            <div>
                                <input class="admNavbarInp" type="button" style="width:135px;" onclick="validateAddEditForm('{@id}')">
                                    <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'SAVE_AND_CLOSE')"></xsl:value-of>   </xsl:attribute>
                                </input>
                                
                                   
                                
                            </div>
                            <div class="imgR"></div>
                        </div>
                    </td>
                </tr>
            </table>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>
