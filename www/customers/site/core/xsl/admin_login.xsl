<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">
    
    <xsl:variable name="dict_login_const" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('login_module', $lang)"/>
    
    <xsl:template name="loginform_body">
        <div align="center" class="admBackLogoDiv">
            <table class="admBackLogo"  align="center" border="0" cellpadding="0" cellspacing="0" >
                <tr>
                  <td class="login_form">
                        <xsl:if test="login/@isSave">
                            <xsl:attribute name="onLoad">javascript: document.getElementById('pswd').focus();</xsl:attribute>
                        </xsl:if>
                        <form method="post" enctype="multipart/form-data" onsubmit="password.value=hex_md5(p.value); p.value='';">
                            <input type="hidden" name="password" value=""/>

                            <div align="center"  >
                                <xsl:if test="/loginform/@error='wrong_login_password'">
                                    <font color="#FF0000">Wrong login or password. Try again.</font>
                                </xsl:if>
                                <xsl:choose>
                                    <xsl:when test="/loginform/@content-freeze and not(/loginform/@error)">
                                        <font color="#FF0000">
                                            <xsl:value-of select="java:getString($dict_login_const, 'CONTENT_FREEZE_MESSAGE')"/>
                                        </font>
                                    </xsl:when>
                                </xsl:choose>
                            </div>
                            <div align="center">
                                <!-- CONTENT -->

                                <table cellspacing="0" cellpadding="0" class="admLogo" align="center" >
                                    <tr>
                                        <td style="font: bold 15px Arial, Helvetica, sans-serif;padding:15px 0 0 10px;"  align="center">
                                            LOGIN
                                        </td>
                                    </tr>
                                    <tr>
                                        <td >
                                            <table cellspacing="0" cellpadding="0">
                                                <tr>
                                                    <td class="admBottomLogin">
                                                        <legend for="login">Login&#160;:</legend>
                                                    </td>
                                                    <td class="admBottomLogin">
                                                        <input class="admTextArea" style="width:140px;" type="text" name="login" id="login" maxlength="20" value="{login}" tabindex="1"/>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td class="admBottomLogin">
                                                        <legend for="p">Password&#160;:</legend>
                                                    </td>
                                                    <td class="admBottomLogin">
                                                        <input class="admTextArea"  style="width:140px;" type="password" maxlength="20" name="p" id="pswd"/>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td class="admBottomLogin">Interface in&#160;:</td>
                                                    <td class="admBottomLogin" >
                                                        <select name="interfaceLanguage"  style="width:146px;">
                                                            <xsl:apply-templates select="//negeso:loginForm/negeso:Languages"/>
                                                        </select>
                                                    </td>
                                                </tr>
                                            </table>
                                            <table cellspacing="0" cellpadding="0">
                                                <tr>
                                                    <td class="admBottomLogin">
                                                        <xsl:choose>
                                                            <xsl:when test="login/@isSave">
                                                                <input type="checkbox" checked="true" name="issave"/>
                                                            </xsl:when>
                                                            <xsl:otherwise>
                                                                <input type="checkbox" name="issave"/>
                                                            </xsl:otherwise>
                                                        </xsl:choose>
                                                    </td>
                                                    <td class="admBottomLogin">Remember login on this computer</td>
                                                </tr>
                                                <tr>
                                                    <td>&#160;</td>
                                                    <td>
                                                        <div class="admBtnBlack" >
                                                            <div class="imgL"></div>
                                                            <div style="width:60px;">
                                                                <input class="admBtnBlack"  type="submit" value="Submit"/>
                                                            </div>
                                                            <div class="imgR"></div>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="admCenter admCopyright">&#xA9; Negeso 2011</td>
                                    </tr>
                                </table>
                            </div>
                        </form>
                    </td>
                </tr>
            </table>
        </div>
    </xsl:template>

    <xsl:template match="negeso:Languages">
        <xsl:apply-templates select="negeso:language"/>
    </xsl:template>

    <xsl:template match="negeso:Language">
        <option value="{@code}">
            <xsl:if test="@code = /loginform/@interface-language">
                <xsl:attribute name="selected">true</xsl:attribute>
            </xsl:if>
            <xsl:value-of select="@name"/>
        </option>
    </xsl:template>

</xsl:stylesheet>
