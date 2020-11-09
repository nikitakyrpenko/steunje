<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (c) 2007 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.


  XSL templates for Site Core
 
  @version		2007.11.27
  @author		Oleg 'germes' Shvedov
  @author		Rostislav 'KOTT' Brizgunov
-->


<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
>

<!-- Core templates -->
<xsl:include href="/site/core/xsl/article.xsl"/>
<xsl:include href="/site/core/xsl/common_functions.xsl"/>
<xsl:include href="/site/core/xsl/default_links.xsl"/>
<xsl:include href="/site/core/xsl/menu.xsl"/>
<xsl:include href="/site/core/xsl/meta_tags.xsl"/>
<xsl:include href="/site/core/xsl/modules.xsl"/>
<xsl:include href="/site/core/xsl/page.xsl"/>
<xsl:include href="/site/core/xsl/picture_frame.xsl"/>
<xsl:include href="/site/core/xsl/popup.xsl"/>
<xsl:include href="/site/core/xsl/translations.xsl"/>
<xsl:include href="/site/core/xsl/admin_login.xsl"/>
<xsl:include href="/site/core/xsl/admin_su_login.xsl"/>
<xsl:include href="/site/core/xsl/validation_constants.xsl"/>
  
 <xsl:variable name="outputType">
	   <xsl:choose>
		      <xsl:when test="/negeso:page[@admin-path='true'] and /negeso:page/@container_id">admin</xsl:when>
		      <xsl:otherwise>user</xsl:otherwise>
	   </xsl:choose> 
 </xsl:variable>

<xsl:variable name="lang" select="/negeso:page/@lang"/>

 <xsl:template match="negeso:page">    
	   <xsl:choose>	
		      <xsl:when test="@category='popup'">
			         <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="{$lang}" lang="{$lang}">
				            <xsl:call-template name="popup_head"/>
				            <xsl:apply-templates select="negeso:contents" mode="popup_body"/>
			         </html>
		      </xsl:when>
		
		      <xsl:otherwise>
			         <xsl:text disable-output-escaping="yes"><![CDATA[<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">]]></xsl:text>
			         <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="{$lang}" lang="{$lang}">
				            <xsl:call-template name="page_head"/>
				            <xsl:apply-templates select="negeso:contents" mode="page_body"/>
			         </html>
		      </xsl:otherwise>		
    </xsl:choose>
 </xsl:template>

 <xsl:template match="negeso:page" mode="adminLogin">
    <xsl:param name="loginMode" select="''"/>
    <xsl:text disable-output-escaping="yes"><![CDATA[<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">]]></xsl:text>    
    <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="{$lang}" lang="{$lang}">
        <xsl:call-template name="page_head"/>
        <xsl:apply-templates select="negeso:contents" mode="page_body">
            <xsl:with-param name="loginMode" select="$loginMode"/>
        </xsl:apply-templates>
    </html>
</xsl:template>

</xsl:stylesheet>
