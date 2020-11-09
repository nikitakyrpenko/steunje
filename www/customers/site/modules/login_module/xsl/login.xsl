<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (c) 2007 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.


  XSL templates for Login Module
 
  @version		2007.12.12
  @author		Oleg 'germes' Shvedov
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">


<xsl:variable name="dict_login_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('login_module', $lang)"/>

<xsl:template name="login">
<!-- <xsl:value-of select="java:getString($dict_login_module, 'LM_LOGIN')"/> -->
    <div class="b-login">
		<xsl:choose>
			<xsl:when test="not(/negeso:page/@role-id = 'guest') and (count(/negeso:page/@role-id) = 1)">
				<xsl:choose>
					<xsl:when test="/negeso:page/negeso:request/negeso:parameter[@name = 'behavior']/negeso:value/text() = 'alreadySent'">
                        <h2>
							<xsl:call-template name ="add-constant-info">
								<xsl:with-param name ="dict"  select="$dict_login_module"/>
								<xsl:with-param name ="name"  select="'LM_LOGIN_SUCCESS'"/>
							</xsl:call-template>
							<xsl:value-of select="java:getString($dict_login_module, 'LM_LOGIN_SUCCESS')"/>
                        </h2>
					</xsl:when>
					<xsl:otherwise>
                        <h2>
							<xsl:call-template name ="add-constant-info">
								<xsl:with-param name ="dict"  select="$dict_login_module"/>
								<xsl:with-param name ="name"  select="'LM_LOGGED_IN'"/>
							</xsl:call-template>
							<xsl:value-of select="java:getString($dict_login_module, 'LM_LOGGED_IN')"/>
                        </h2>
					</xsl:otherwise>
				</xsl:choose>
				<form method="post" enctype="multipart/form-data">
					<input type="hidden" name="logout" value="1" />
                    <input class="submit" type="submit" value="Uitloggen" />
				</form>
			</xsl:when>
			<xsl:otherwise>
				<xsl:if test="/negeso:page/negeso:request/negeso:parameter[@name = 'behavior']/negeso:value/text() = 'alreadySent' or /negeso:page/negeso:request/negeso:parameter[@name = 'behavior']/negeso:value/text() = 'WRONG'">
                    <div class="b-loginEmpty">
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_login_module"/>
							<xsl:with-param name ="name"  select="'LM_WRONG_LOGIN'"/>
						</xsl:call-template>
						<xsl:value-of select="java:getString($dict_login_module, 'LM_WRONG_LOGIN')"/>
					</div>
				</xsl:if>
				<form onsubmit="upassword.value=hex_md5(p.value); p.value='';" method="post" enctype="multipart/form-data">
					<input type="hidden" name="upassword"/>
					<input type="hidden" name="behavior" value="alreadySent" />
                    <div class="login-form">
						<h2>INLOGGEN</h2>
							<span class="ulogin">
								<input placeholder="Gebruikersnaam" name="ulogin" id="ulogin" />
							</span>
							<span class="password">
								<input placeholder="Wachtwoord" type="password" name="p" id="p"/>
							</span>
                            <span>
                                <input class="submit" type="submit">
									<xsl:call-template name ="add-constant-info">
										<xsl:with-param name ="dict"  select="$dict_login_module"/>
										<xsl:with-param name ="name"  select="'LM_SEND'"/>
									</xsl:call-template>
                                    <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_login_module, 'LM_SEND')"/></xsl:attribute>
								</input>
							</span>
					</div>
                </form>
            </xsl:otherwise>
        </xsl:choose>
	</div>
</xsl:template>
</xsl:stylesheet>