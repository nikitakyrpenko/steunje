<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${commons.xsl}

  Copyright (c) 2004 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential
  Information and shall use it only in accordance with the terms of the
  license agreement you entered into with Negeso.

  XSL templates Superusers

  @version		2006.08.06
  @author		Andrey Morskoy
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
>

    <xsl:template name="loginform_su_body">
        <xsl:apply-templates select="//negeso:LdapLoginForm" />
    </xsl:template>
        
    <xsl:template match="negeso:LdapLoginForm">
        <script type="text/javascript">
            function doSubmit() {
                var rsa = new RSAKey();
                rsa.setPublic(document.suForm['pkey'].value, '10001');
                document.suForm['password'].value = rsa.encrypt(document.suForm['pwd'].value);
                document.suForm['pwd'].value = '';
            }
        </script>

        <div class="admBackLogoDiv">
        <table cellspacing="0" cellpadding="0" border="0" class="admBackLogo">
            <tr>
                <td  class="login_form">
                    <form name="suForm" method="post" enctype="application/x-www-form-urlencoded" onsubmit="doSubmit();">
                        <div align="center"  >
                            <xsl:if test="@ldapStatus = 'denied'">
                                <font color="#FF0000">Wrong Login or Password</font>
                            </xsl:if>
                            <xsl:if test="@ldapStatus = 'notSuperuser'">
                                <font color="#FF0000">You are not Superuser</font>
                            </xsl:if>
                            <xsl:if test="@ldapStatus = 'notEnoughRights'">
                                <font color="#FF0000">You have not enough rights</font>
                            </xsl:if>
                            <xsl:if test="@ldapStatus = 'ldapNotAccessible'">
                                <font color="#FF0000">LDAP server is not accessible</font>
                            </xsl:if>
                        </div>
                        <div align="center">
                            <table cellspacing="0" cellpadding="0" border="0" class="admLogo" align="center">
                                <tr>
                                    <td style="font: bold 13px Arial, Helvetica, sans-serif;padding:3px 0 0 0;" align="center" colspan="2">
                                        Welcome to Negeso Web/CMS<br/> SuperUser mode
                                    </td>
                                </tr>
                                <tr>
                                    <td>
                                        <table cellspacing="0" cellpadding="0">
                                            <tr>
                                                <td class="admBottomLogin">
                                                    <label for="login">Login&#160;:</label>
                                                </td>
                                                <td class="admBottomLogin">
                                                    <input class="admTextArea" style="width:140px;" type="text" name="login" id="login"  maxlength="20"/>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td class="admBottomLogin">
                                                    <label for="pwd">&#160;Password&#160;:</label>
                                                </td>
                                                <td class="admBottomLogin">
                                                    <input class="admTextArea" style="width:140px;" name="pwd" id="pwd"   type="password" maxlength="20" redisplay="false"/>
                                                    <input type="hidden" name="password" value=""/>
                                                    <input type="hidden" name="pkey" value="{@publicKey}" />
                                                    <input type="hidden" name="pexp" value="{@publicExp}"/>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td class="admBottomLogin">&#160;Interface in&#160;:</td>
                                                <td class="admBottomLogin">
                                                    <select name="interfaceLanguage" style="width:146px;">
                                                        <xsl:apply-templates select="//negeso:Languages"/>
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
                                                            <input type="submit"  class="admBtnBlack" name="submit" value="Submit"/>
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
        <xsl:apply-templates select="negeso:Language"/>
    </xsl:template>

    <xsl:template match="negeso:Language">
        <option value="{@code}">
            <xsl:if test="@code = /negeso:page/negeso:LdapLoginForm/negeso:Languages/@current">
                <xsl:attribute name="selected">true</xsl:attribute>
            </xsl:if>
            <xsl:value-of select="@language"/>
        </option>
    </xsl:template>

</xsl:stylesheet>