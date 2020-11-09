<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (c) 2004 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.


  XSL templates for Banner module
 
  @version		2007.20.12
  @author		Oleg 'germes' Shvedov
-->


<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
>

<xsl:template match="negeso:banner_module">
<xsl:param name="type"></xsl:param>
	<xsl:if test="negeso:banner[@type=$type]">
		<xsl:apply-templates select="negeso:banner[@type=$type]"/>
	</xsl:if>
</xsl:template>

<xsl:template match="negeso:banner">
	<xsl:choose>
		<xsl:when test="@url">
			<div style="width:{@width}px; height:{@height}px; overflow: hidden;" >
				<a href="bm_redirect.html?action=redirect&amp;id={@id}">
					<xsl:if test="@inNewWindow='true'">
						<xsl:attribute name="target">_blank.html</xsl:attribute>
					</xsl:if>
					
					<xsl:choose>
						<!--  Image -->
						<xsl:when test="@imageType='1'">
							<img src="{@imageUrl}" title="{@url}" alt="{@url}" border="0"/>
						</xsl:when>
						
						<!--  Flash -->
						<xsl:when test="@imageType='2'">
							<object id="flashTagId" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codeBase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0" height="{@height}" width="{@width}" >
								<param VALUE="24553" NAME="_cx"/>
								<param VALUE="1376" NAME="_cy"/>
								<param VALUE="{@imageUrl}" NAME="Movie"/> 
								<param VALUE="{@imageUrl}" NAME="Src"/> 
								<param VALUE="Opaque" NAME="WMode"/>
								<param VALUE="High" NAME="Quality"/>
								<param VALUE="-1" NAME="Menu"/>
								<param VALUE="ShowAll" NAME="Scale"/>
								<embed wmode="opaque" height="{@height}" width="{@width}" type="application/x-shockwave-flash" 
								src="{@imageUrl}" scale="showall" quality="high" pluginspage="http://www.macromedia.com/go/getflashplayer" menu="false" 
								/>
							</object>
						</xsl:when>
					</xsl:choose>
				</a>
			</div>
		</xsl:when>
		<xsl:otherwise>
			<div style="width:{@width}px; height:{@height}px; overflow: hidden;" >
				<xsl:choose>
					<!--  Image -->
					<xsl:when test="@imageType='1'">
						<img src="{@imageUrl}" title="{@url}" alt="{@url}" border="0"/>
					</xsl:when>
					
					<!--  Flash -->
					<xsl:when test="@imageType='2'">
							<embed wmode="opaque" height="{@height}" width="{@width}" type="application/x-shockwave-flash" 
							src="{@imageUrl}" scale="showall" quality="high" pluginspage="http://www.macromedia.com/go/getflashplayer" menu="false" 
							/>
					</xsl:when>
				</xsl:choose>
			</div>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

</xsl:stylesheet>
