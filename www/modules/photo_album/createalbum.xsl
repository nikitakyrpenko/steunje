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
<xsl:variable name="dict_photo_album" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_photo_album.xsl', $lang)"/>
<xsl:variable name="dict_security_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_security_module.xsl', $lang)"/>

<xsl:include href="/xsl/negeso_header.xsl"/>

<xsl:template match="/">
    <html>
    <head>
        <title><xsl:value-of select="java:getString($dict_photo_album, 'CREATE_NEW_ALBUM')"/></title>
        <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
        <script language="JavaScript" src="/script/jquery.min.js"/>
		<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
        <script language="JavaScript1.2" src="/script/md5.js" type="text/javascript"/>
        <script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"/>
        <script language="JavaScript">
            var s_IllegalCharactersInLogin = "<xsl:value-of select="java:getString($dict_security_module, 'ILLEGAL_CHARACTERS_IN_LOGIN')"/>";
            var s_UseEnglishCharacters = "<xsl:value-of select="java:getString($dict_security_module, 'USE_ENGLISH_CHARACTERS')"/>";
            var s_FillInAlbumName = "<xsl:value-of select="java:getString($dict_photo_album, 'FILL_IN_ALBUM_NAME')"/>";
            var s_FillInFullVisitorName = "<xsl:value-of select="java:getString($dict_security_module, 'FILL_IN_FULL_VISITOR_NAME')"/>";
            var s_FillInLogin = "<xsl:value-of select="java:getString($dict_security_module, 'FILL_IN_LOGIN')"/>";
            var s_FillInPassword = "<xsl:value-of select="java:getString($dict_security_module, 'FILL_IN_PASSWORD')"/>";
            var s_PasswordsDiffer = "<xsl:value-of select="java:getString($dict_security_module, 'PASSWORDS_DIFFER')"/>";
            var s_IllegalCharactersInPassword = "<xsl:value-of select="java:getString($dict_security_module, 'ILLEGAL_CHARACTERS_IN_PASSWORD')"/>";

            function validateMainForm(userId) {
                if(mainForm.album.value == '') {
                    alert(s_FillInAlbumName);
                    mainForm.album.focus();
                    return false;
                }
                if(mainForm.name.value == '') {
                    alert(s_FillInFullVisitorName);
                    mainForm.name.focus();
                    return false;
                }
                if(mainForm.login.value == '') {
                    alert(s_FillInLogin);
                    mainForm.login.focus();
                    return false;
                }
                var loginOk = StringUtil.isTokenValid(
                    mainForm.login.value,
                    StringUtil.getSafeCharacters());
                if(!loginOk){
                    alert(s_IllegalCharactersInLogin + ".\n" +
                        s_UseEnglishCharacters);
                    mainForm.login.focus();
                    return false;
                }
                if (mainForm.password.value == ''){
                    alert(s_FillInPassword);
                    mainForm.password.focus();
                    return false;
                }
                if(mainForm.password.value != mainForm.pwd.value){
                   alert(s_PasswordsDiffer);
                   mainForm.password.focus();
                   return false;
                }
                var passwordOk = StringUtil.isTokenValid(
                    mainForm.password.value,
                    StringUtil.getSafeCharacters());
                if (!passwordOk){
                    alert(s_IllegalCharactersInPassword + ".\n" +
                        s_UseEnglishCharacters);
                    mainForm.password.focus();
                    return false;
                }
                mainForm.password.value=hex_md5(mainForm.password.value);
                mainForm.pwd.value='';
                return true;
            }

            function returnValues() {
                if (validateMainForm()) {
                    var result = new Array();
                    result[0] = mainForm.album.value;
                    result[1] = mainForm.name.value;
                    result[2] = mainForm.login.value;
                    result[3] = mainForm.password.value;
                    window.returnValue = result;
                    window.close();
                }
            }

            window.focus();
        </script>
    </head>
    <body class="menu">
        <xsl:call-template name="NegesoHeader">
            <xsl:with-param name="helpLink">
                <xsl:text>/admin/help/cph1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
            </xsl:with-param>
        </xsl:call-template>
        <div align="center">
            <xsl:call-template name="NavBar"/>
            <xsl:call-template name="tableTitle">
                <xsl:with-param name="headtext">
					               <xsl:value-of select="java:getString($dict_photo_album, 'CREATE_NEW_ALBUM')"/>
                </xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="form"/>
            <xsl:call-template name="NavBar"/>
        </div>
    </body>
    </html>
</xsl:template>

<xsl:template name="form">
    <form id="mainForm">
        <table cellpadding="0" cellspacing="0" class="admNavPanel">
            <tr>
                <td class="admMainTD admWidth150"><xsl:value-of select="java:getString($dict_photo_album, 'ALBUM_NAME')"/>*</td>
                <td class="admLightTD"><input class="admTextArea admWidth200" type="text" name="album" value="{/negeso:photo_album/@album}"/></td>
            </tr>
            <tr>
                <td class="admMainTD admWidth150"><xsl:value-of select="java:getString($dict_photo_album, 'USERS_FULL_NAME')"/>*</td>
                <td class="admLightTD"><input class="admTextArea admWidth200" type="text" name="name" value="{/negeso:photo_album/@user}"/></td>
            </tr>
            <tr>
                <td class="admMainTD admWidth150"><xsl:value-of select="java:getString($dict_security_module, 'LOGIN')"/>*</td>
                <td class="admLightTD"><input class="admTextArea admWidth200" type="text" name="login" value="{/negeso:photo_album/@login}"/></td>
            </tr>
            <tr>
                <td class="admMainTD admWidth150"><xsl:value-of select="java:getString($dict_security_module, 'PASSWORD')"/>*</td>
                <td class="admLightTD"><input class="admTextArea admWidth200" type="password" value="" name="password"/></td>
            </tr>
            <tr>
               <td class="admMainTD admWidth150"><xsl:value-of select="java:getString($dict_security_module, 'RETYPE_PASSWORD')"/>*</td>
               <td class="admLightTD"><input class="admTextArea admWidth200" type="password" value="" name="pwd"/></td>
            </tr>
        </table>
        <table cellpadding="0" cellspacing="0" class="admNavPanel">
            <tr>
                <td class="admNavPanel admNavbar admCenter">
                    <input class="admNavbarInp" type="button" onclick="returnValues()">
                        <xsl:attribute name="value">&lt;&#160;<xsl:value-of select="java:getString($dict_common, 'SAVE')"/>&#160;&gt;</xsl:attribute>
                    </input>
                    <input class="admNavbarInp" type="button" onclick="window.close()">
                        <xsl:attribute name="value">&lt;&#160;<xsl:value-of select="java:getString($dict_common, 'CANCEL')"/>&#160;&gt;</xsl:attribute>
                    </input>
                </td>
            </tr>
        </table>
    </form>
</xsl:template>

</xsl:stylesheet>
