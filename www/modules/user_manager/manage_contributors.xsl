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
            <xsl:when test="/negeso:page/negeso:contents/negeso:users">list</xsl:when>
            <xsl:when test="/negeso:page/negeso:context/negeso:action">
                <xsl:value-of select="/negeso:page/negeso:context/negeso:action"/>
            </xsl:when>
            <xsl:otherwise>list</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="title">
        <xsl:choose>
            <xsl:when test="$action='add'">
                <xsl:value-of select="java:getString($dict_security_module, 'ADD_NEW_ACCOUNT')"/>
            </xsl:when>
            <xsl:when test="$action='edit'">
                <xsl:value-of select="java:getString($dict_security_module, 'EDIT_ACCOUNT')"/>
            </xsl:when>
            <xsl:when test="$action='edit_password'">
                <xsl:value-of select="java:getString($dict_security_module, 'SET_NEW_PASSWORD')"/>
            </xsl:when>
            <xsl:when test="$action='save'">
                <xsl:value-of select="java:getString($dict_security_module, 'SAVING_CHANGES')"/>
            </xsl:when>
            <xsl:when test="$action='delete'">
                <xsl:value-of select="java:getString($dict_modules, 'CONTRIBUTORS')"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="java:getString($dict_modules, 'CONTRIBUTORS')"/>
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
                <script language="JavaScript1.2" src="/script/md5.js" type="text/javascript"/>                
                <script type="text/javascript" src="/script/jquery.min.js"></script>
				<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
                <script type="text/javascript" src="/script/cufon-yui.js"></script>
                <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
                <script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"/>
                
                <script language="JavaScript">
                    var s_DeleteAccountConfirmation = "<xsl:value-of select="java:getString($dict_security_module, 'DELETE_ACCOUNT_CONFIRMATION')"/>";
                    var s_FillInFullContributorName = "<xsl:value-of select="java:getString($dict_security_module, 'FILL_IN_FULL_CONTRIBUTOR_NAME')"/>";
                    var s_FillInLogin = "<xsl:value-of select="java:getString($dict_security_module, 'FILL_IN_LOGIN')"/>";
                    var s_FillInPassword = "<xsl:value-of select="java:getString($dict_security_module, 'FILL_IN_PASSWORD')"/>";
                    var s_PasswordsDiffer = "<xsl:value-of select="java:getString($dict_security_module, 'PASSWORDS_DIFFER')"/>";
                    var s_IllegalCharactersInLogin = "<xsl:value-of select="java:getString($dict_security_module, 'ILLEGAL_CHARACTERS_IN_LOGIN')"/>";
                    var s_IllegalCharactersInPassword = "<xsl:value-of select="java:getString($dict_security_module, 'ILLEGAL_CHARACTERS_IN_PASSWORD')"/>";
                    var s_UseEnglishCharacters = "<xsl:value-of select="java:getString($dict_security_module, 'USE_ENGLISH_CHARACTERS')"/>";
                    var s_UserIsInAdministrativeGroup = "<xsl:value-of select="java:getString($dict_security_module, 'USER_IS_IN_ADMINISTRATIVE_GROUP')"/>";
                    var s_NeedNotIncludeUserIntoOtherGroups = "<xsl:value-of select="java:getString($dict_security_module, 'NEED_NOT_INLUDE_USER_INTO_OTHER_GROUPS')"/>";
                    var s_CheckGroup = "<xsl:value-of select="java:getString($dict_security_module, 'CHECK_AT_LEAST_ONE_GROUP')"/>";
                    var s_NoGroups = "<xsl:value-of select="java:getString($dict_security_module, 'THERE_ARE_NO_GROUP')"/>";
                    <xsl:text  disable-output-escaping="yes">

            function addAccount() {
                window.location.href = "?action=add";
            }

             function save(){   
            document.add_edit_form.submit();  
              }

            function editAccount(id) {
                window.location.href = "?action=edit&amp;id="+id;
            }
            
            function deleteAccount(id) {
                if(!confirm(s_DeleteAccountConfirmation)) {
                    return;
                }
                window.location.href = "?action=delete&amp;id="+id;
            }
            
            function editPassword(id) {
                window.location.href = "?action=edit_password&amp;id="+id;
            }
            
    function validateAddEditForm(userId, formObj, close){
                if(add_edit_form.name.value == ''){                         
                    alert(s_FillInFullContributorName);
                    add_edit_form.name.focus();
                    return;
                }
                
                if(add_edit_form.login.value == ''){
                    alert(s_FillInLogin);
                    add_edit_form.login.focus();
                    return;
                }
                
                var loginOk = StringUtil.isTokenValid(
                    add_edit_form.login.value,
                    StringUtil.getSafeCharacters());
                
                if(!loginOk){
                    alert(s_IllegalCharactersInLogin + ".\n" +
                        s_UseEnglishCharacters);
                    add_edit_form.login.focus();
                    return;
                }
                
                if(userId == ''){
                    if(add_edit_form.password.value == ''){
                        alert(s_FillInPassword);
                        add_edit_form.password.focus();
                        return;
                    }
                    if(add_edit_form.password.value != add_edit_form.pwd.value){
                        alert(s_PasswordsDiffer);
                        add_edit_form.password.focus();
                        return;
                    }
                    var passwordOk = StringUtil.isTokenValid(
                            add_edit_form.password.value,
                            StringUtil.getSafeCharacters());
                    if(!passwordOk){
                        alert(s_IllegalCharactersInPassword + ".\n" +
                            s_UseEnglishCharacters);
                        add_edit_form.password.focus();
                        return;
                    }
                    add_edit_form.password.value=hex_md5(add_edit_form.password.value);
                    add_edit_form.pwd.value='';
                }
                
                if (typeof(formObj)!="undefined" &amp;&amp; formObj!=null) {
                	var is_ok=false;
                	var no_groups=true;
                	for (var i=0; i&lt;formObj.elements.length; i++) {
                		var elm=formObj.elements[i];
                		if (elm.tagName.toLowerCase()=="input" 
                			&amp;&amp; elm.type.toLowerCase()=="checkbox"
                			&amp;&amp; elm.name.toLowerCase()=="groups")
                		{
                			no_groups=false;
                			if (elm.checked==true)
                				is_ok=true;
                		}
                	}
                	
                	if (no_groups)
                		alert(s_NoGroups);
                	else if (!is_ok) {
                		alert(s_CheckGroup);
                	}
                } 
                
                if (close != '') {
                    add_edit_form.close.value != "close";                                                    
                }  
                   add_edit_form.submit();  
            }


            function validatePasswordForm(){
                if(add_edit_form.password.value == ''){
                    alert(s_FillInPassword);
                    add_edit_form.password.focus();
                    return;
                }
                if(add_edit_form.password.value != add_edit_form.pwd.value){
                    alert(s_PasswordsDiffer);
                    add_edit_form.password.focus();
                    return;
                }
                var passwordOk = StringUtil.isTokenValid(
                        add_edit_form.password.value,
                        StringUtil.getSafeCharacters());
                if(!passwordOk){
                    alert(s_IllegalCharactersInPassword + ".\n" +
                        s_UseEnglishCharacters);
                    add_edit_form.password.focus();
                    return;
                }
                add_edit_form.password.value=hex_md5(add_edit_form.password.value);
                add_edit_form.pwd.value='';
                add_edit_form.submit();
            }
            
            function alertIfAdminAlready(checkBox){
                for(var i = 0; i &lt; document.all.groups.length; i++){
                    if( document.all.groups[i].roleId == "administrator"
                        &amp;&amp; document.all.groups[i].checked
                        &amp;&amp; checkBox.roleId != "administrator"
                        &amp;&amp; checkBox.checked )
                    {
                        alert(s_UserIsInAdministrativeGroup + ".\n" +
                            s_NeedNotIncludeUserIntoOtherGroups);
                        break;
                    }
                }
            }
            
            window.focus();            
        </xsl:text>
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
                    <xsl:call-template name="buttons"> </xsl:call-template>
            </body>
        </html>
    </xsl:template>    
  

    <!-- ******************* END MAIN TEMPLATE ********************************* -->

    <!-- *********************** BEGIN MODE "ADD_EDIT" ***************************** -->
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
                <xsl:apply-templates select="negeso:user" mode="add_edit"/>
            </xsl:when>
            <xsl:when test="$action='edit'">
                <xsl:apply-templates select="negeso:user" mode="add_edit"/>

            </xsl:when>
            <xsl:when test="$action='edit_password'">
                <xsl:apply-templates select="negeso:user" mode="edit_password"/>
            </xsl:when>
            <xsl:when test="$action='save'">
                <!-- ************ DIRTY IGNORING BUGS: FIX THIS *********** -->
                <xsl:apply-templates select="negeso:users" mode="list"/>
            </xsl:when>
            <xsl:when test="$action='delete'">
                <xsl:value-of select="java:getString($dict_security_module, 'ADMINISTRATIVE_ACCOUNTS')"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="negeso:users" mode="list"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="negeso:user" mode="add_edit">
        <form method="POST" enctype="multipart/form-data" id="add_edit_form" name="add_edit_form">
            <input type="hidden" name="close" value=""/>
            <input type="hidden" name="action" value="save"/>
            <xsl:if test="@id">
                <input type="hidden" name="id" value="{@id}"/>
            </xsl:if>
            <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
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
                    <td class="admNavPanel" colspan="5" style="padding: 0 0 0 20px;">
                        <a class="admAnchor">
                            <xsl:choose>
                                <xsl:when test="//@containers_management_enabled = 'false'">
                                    <xsl:attribute name="disabled"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:attribute name="href">container_mngr</xsl:attribute>
                                </xsl:otherwise>
                            </xsl:choose>
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
                   <xsl:value-of select="java:getString($dict_security_module, 'FULL_NAME')"/>*
                    </th>
                    <th class="admTableTDLast" colspan="2">
                        <input class="admTextArea" type="text" value="{@name}" name="name" size="100" maxlength="100"/>
                    </th>
                </tr>
                <tr>
                    <th class="admTableTD admWidth150">
                        <xsl:value-of select="java:getString($dict_security_module, 'LOGIN')"/>*
                    </th>
                    <th class="admTableTDLast" colspan="2">
                        <input class="admTextArea" type="text" value="{@login}" name="login" size="30" maxlength="30"/>
                    </th>
                </tr>
                <xsl:if test="not(@id)">
                    <tr>
                        <th class="admTableTD admWidth150">
                            <xsl:value-of select="java:getString($dict_security_module, 'PASSWORD')"/>*
                        </th>
                        <th class="admTableTDLast" colspan="2">
                            <input class="admTextArea" type="password" value="" name="password" size="40" maxlength="40"/>
                        </th>
                    </tr>
                    <tr>
                        <th class="admTableTD admWidth150">
                            <xsl:value-of select="java:getString($dict_security_module, 'RETYPE_PASSWORD')"/>*
                        </th>
                        <th class="admTableTDLast" colspan="2">
                            <input class="admTextArea" type="password" value="" name="pwd" size="40" maxlength="40"/>
                        </th>
                    </tr>
                </xsl:if>
                <tr>
                    <td align="center" class="admNavPanelFont" colspan="3" >
                        <xsl:call-template name="tableTitle">
                            <xsl:with-param name="headtext">
                                <xsl:value-of select="java:getString($dict_security_module, 'CONTRIBUTOR_IS_MEMBER_OF_GROUPS')"/>
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
                <xsl:for-each select="negeso:groups/negeso:group[not(@role-id='guest')]">
                    <tr>
                        <th class="admTableTD">
                            <xsl:value-of select="@name"/>
                        </th>
                        <th class="admTableTD" width="100px">
                            <xsl:value-of select="@role-id"/>
                        </th>
                        <th class="admTableTDLast">
                            <input type="checkbox" name="groups" value="{@id}" onclick="alertIfAdminAlready(this)" roleId="{@role-id}">
                                <xsl:if test="@linked='true'">
                                    <xsl:attribute name="checked"/>
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
  

    <!-- *********************** END MODE "EDIT" *********************** -->

    <xsl:template match="negeso:context/*"/>
    <!-- DIRTY PATCH -->

    <!-- *********************** BEGIN MODE "EDIT_PASSWORD" ***************************** -->
    <xsl:template match="negeso:user" mode="edit_password">
        <form method="POST" enctype="multipart/form-data" id="add_edit_form"  name="add_edit_form">
            <input type="hidden" name="close" value=""/>
            <input type="hidden" name="action" value="save"/>
            <xsl:if test="@id">
                <input type="hidden" name="id" value="{@id}"/>
            </xsl:if>

            <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
                <tr>
                    <td align="center" class="admNavPanelFont" colspan="5">
                        <xsl:call-template name="tableTitle">
                            <xsl:with-param name="headtext">
                                <xsl:value-of select="$title"/>
                            </xsl:with-param>
                        </xsl:call-template>
                    </td>
                </tr>
                <tr>
                    <td class="admNavPanel" colspan="5" style="padding: 0 0 0 20px;">
                        <a class="admAnchor">
                            <xsl:choose>
                                <xsl:when test="//@containers_management_enabled = 'false'">
                                    <xsl:attribute name="disabled"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:attribute name="href">container_mngr</xsl:attribute>
                                </xsl:otherwise>
                            </xsl:choose>
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
                    <th class="admTableTD">
                    <xsl:value-of select="java:getString($dict_security_module, 'FULL_NAME')"/>
                    </th>
                    <th class="admTableTDLast">
                        <xsl:value-of select="@name"/>
                    </th>
                </tr>
                <tr>
                    <th class="admTableTD">
                        <xsl:value-of select="java:getString($dict_security_module, 'LOGIN')"/>
                    </th>
                    <th class="admTableTD">
                        <xsl:value-of select="@login"/>
                    </th>
                </tr>
                <tr>
                    <th class="admTableTD">
                        <xsl:value-of select="java:getString($dict_security_module, 'PASSWORD')"/>*
                    </th>
                    <th class="admTableTD">
                        <input class="admTextArea" type="password" value="" name="password"/>
                    </th>
                </tr>
                <tr>
                    <th class="admTableTD">
                        <xsl:value-of select="java:getString($dict_security_module, 'RETYPE_PASSWORD')"/>*
                    </th>
                    <th class="admTableTD">
                        <input class="admTextArea" type="password" value="" name="pwd"/>
                    </th>
                </tr>
                <tr>
                    <td class="admTableFooter" >&#160;</td>
                </tr>
            </table>
            <!--<table cellpadding="0" cellspacing="0" class="admNavPanel">
                <tr>
                    <td class="admNavbar admCenter">
                        <input class="admNavbarInp" type="button" onclick="validateAddEditForm('{@id}',this.form)">
                            <xsl:attribute name="value"> &lt;&#160;<xsl:value-of select="java:getString($dict_common, 'SAVE')"/>&#160;&gt;                            </xsl:attribute>
                        </input>
                    </td>
                </tr>
            </table>-->
        </form>
    </xsl:template>
    <!-- *********************** END MODE "EDIT_PASSWORD" *********************** -->

    <!-- *********************** BEGIN MODE "LIST" ***************************** -->

    <xsl:template match="negeso:users" mode="list">
        <form method="POST" enctype="multipart/form-data">
            <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
                <tr>
                    <td align="center" class="admNavPanelFont" colspan="5">
                        <xsl:call-template name="tableTitle">
                            <xsl:with-param name="headtext">
                                <xsl:value-of select="$title"/>
                            </xsl:with-param>
                        </xsl:call-template>
                    </td>
                </tr>
                <tr>
                    <td class="admNavPanel" colspan="5" style="padding: 0 0 0 20px;">
                        <a class="admAnchor">
                            <xsl:choose>
                                <xsl:when test="//@containers_management_enabled = 'false'">
                                    <xsl:attribute name="disabled"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:attribute name="href">container_mngr</xsl:attribute>
                                </xsl:otherwise>
                            </xsl:choose>
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
                    <td class="admTDtitles">
                      <xsl:value-of select="java:getString($dict_security_module, 'FULL_NAME')"/>
                    </td>
                    <td class="admTDtitles" style="padding:0;" align="center">
                        <xsl:value-of select="java:getString($dict_security_module, 'LOGIN')"/>
                    </td>
                    <td class="admTDtitles" colspan="3" >&#160;</td>
                </tr>
                <xsl:for-each select="negeso:user">
                    <tr>
                        <th class="admTableTD">
                            <xsl:value-of select="@name"/>
                        </th>
                        <th class="admTableTD">
                            <xsl:value-of select="@login"/>
                        </th>
                        <td class="admTableTD" width="1%">
                            <img src="/images/edit.png" class="admHand" onClick="editAccount({@id})">
                                <xsl:attribute name="title">
                                    <xsl:value-of select="java:getString($dict_common, 'EDIT')"/>
                                </xsl:attribute>
                            </img>
                        </td>
                        <td class="admTableTD" width="1%">
                            <img src="/images/password.png" class="admHand" onClick="editPassword({@id})">
                                <xsl:attribute name="title">
                                    <xsl:value-of select="java:getString($dict_security_module, 'CHANGE_PASSWORD')"/>
                                </xsl:attribute>
                            </img>
                        </td>
                        <td class="admTableTDLast" width="1%">
                          
                            <xsl:if test="(@login != 'admin') and (@name != 'Administrator')">
                                <img src="/images/delete.png" class="admHand" onClick="deleteAccount({@id})">
                                    <xsl:attribute name="title">
                                        <xsl:value-of select="java:getString($dict_security_module, 'DELETE_ACCOUNT')"/>
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
        <xsl:if test="/negeso:page/negeso:contents/negeso:users">
            <table cellpadding="0" cellspacing="0"  width="764px" align="center" class="Buttons">
                <tr>
                    <td>

                        <div class="admBtnGreenb">
                            <div class="imgL"></div>
                            <div>
                                <input class="admNavbarInp" type="button" onClick="addAccount()">
                                    <xsl:attribute name="value">
                                        <xsl:value-of select="java:getString($dict_security_module, 'ADD_NEW_ACCOUNT')"/>
                                    </xsl:attribute>
                                </input>

                            </div>
                            <div class="imgR"></div>
                        </div>
                    </td>
                </tr>
            </table>
        </xsl:if>
        
        <xsl:if test="$action='add'">
            <table cellpadding="0" cellspacing="0"  width="764px" align="center" class="Buttons">
                <tr>
                    <td >              

                        <div class="admBtnGreenb">
                            <div class="imgL"></div>
                            <div>
                                <input class="admNavbarInp" type="button" style="width:135px;" onclick="validateAddEditForm('{@id}',add_edit_form.form,'close')">
                                    <xsl:attribute name="value">
                                        <xsl:value-of select="java:getString($dict_common, 'SAVE_AND_CLOSE')"/>
                                    </xsl:attribute>
                                </input>

                            </div>
                            <div class="imgR"></div>
                        </div>
                    </td>
                </tr>
            </table>
        </xsl:if>
        <xsl:if test="$action='edit'">
            <table cellpadding="0" cellspacing="0"  width="764px" align="center" class="Buttons">
                <tr>
                    <td>
                        <div class="admBtnGreenb">
                            <div class="imgL"></div>
                            <div>
                                <a class="admBtnText" focus="blur()" onClick="save()" href="#save();">
                                    <xsl:value-of select="java:getString($dict_common, 'SAVE_AND_CLOSE')"/>
                                </a>
                            </div>
                            <div class="imgR"></div>
                        </div>
                    </td>
                </tr>
            </table>
        </xsl:if>
        
        <xsl:if test="$action='edit_password'">
            <table cellpadding="0" cellspacing="0"  width="764px" align="center" class="Buttons">
                <tr>
                    <td>
                        <div class="admBtnGreenb">
                            <div class="imgL"></div>
                            <div>
                                <input class="admNavbarInp" type="button" style="width:135px;" onclick="validatePasswordForm()">
                                    <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'SAVE_AND_CLOSE')"></xsl:value-of> </xsl:attribute>
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


