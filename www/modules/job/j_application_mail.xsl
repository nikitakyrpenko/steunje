<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${j_applicant.xsl}       
 
  Copyright (c) 2004 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  View of a job module applicant.
 
  @version    2005/04/07
  @author     Olexiy Strashko
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
<xsl:variable name="dict_job_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_job_module.xsl', $lang)"/>

<!-- MAIN ENTRY -->
<xsl:template match="/negeso:page">
    <html>
        <head>
           <title><xsl:value-of select="java:getString($dict_job_module, 'J_DEPARTMENTS_TITLE')"/></title>
            <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
        </head>
		<body style="font: normal 12px Arial; color: #114b9f;">
			<h2 style="font: bold 13px Arial; font-variant: small-caps;">
				<xsl:value-of select="java:getString($dict_job_module, 'NEW_APPLICATION_FOR_VACANCY')"/>: 
				<xsl:choose>
	            <xsl:when test = "negeso:vacancy/@id = 1">
	            	<xsl:value-of select="java:getString($dict_job_module, 'J_GENERAL')"/>
	            </xsl:when>
	            <xsl:otherwise>
		            <xsl:value-of select="negeso:vacancy/@title"/>
	            </xsl:otherwise>
	         </xsl:choose>
	         <br />
				<xsl:value-of select="java:getString($dict_job_module, 'J_DEPARTMENTS')"/>: 
				<xsl:for-each select="negeso:department">
					<xsl:value-of select="@title"/>&#160;
				</xsl:for-each><br/>
			</h2>
            <xsl:value-of select="java:getString($dict_job_module, 'POSTED')"/>: <xsl:value-of select="negeso:application/@post_date"/>
			<br/>
			<br/>
            <!-- CONTENT -->
            <xsl:apply-templates select="negeso:application"/>
        </body>
    </html>
</xsl:template>

<xsl:template match="negeso:application">
		<table cellspacing="1" cellpadding="0" width="100%" style="font: normal 12px Arial; padding-bottom: 5px; padding-left: 5px;">
			<xsl:if test="@first_name">
				<tr>
					<td width="40%" style="border: solid 1px gray; background-color: #F1F7FD; font-variant: small-caps;">
						<xsl:value-of select="java:getString($dict_common, 'FIRST_NAME')"/>
					</td>
					<td width="60%" style="border: solid 1px gray;">
						<xsl:value-of select="@first_name"/>
					</td>
				</tr>
			</xsl:if>
			<xsl:if test="@second_name" >
				<tr>
					<td style="border: solid 1px gray; background-color: #F1F7FD; font-variant: small-caps;">
						<xsl:value-of select="java:getString($dict_common, 'SECOND_NAME')"/>
					</td>
					<td  style="border: solid 1px gray;">
						<xsl:value-of select="@second_name"/>
					</td>
				</tr>
			</xsl:if>
			<xsl:if test="@email">
				<tr>
					<td style="border: solid 1px gray; background-color: #F1F7FD; font-variant: small-caps;">
						<xsl:value-of select="java:getString($dict_common, 'EMAIL')"/>
					</td>
					<td  style="border: solid 1px gray;">
						<xsl:value-of select="@email"/>
					</td>
				</tr>
			</xsl:if>
			<xsl:if test="@address_line">
				<tr>
					<td  style="border: solid 1px gray; background-color: #F1F7FD; font-variant: small-caps;">
						<xsl:value-of select="java:getString($dict_common, 'ADDRESS_LINE')"/>
					</td>
					<td  style="border: solid 1px gray;">
						<xsl:value-of select="@address_line"/>
					</td>
				</tr>
			</xsl:if>
			<xsl:if test="@phone">
				<tr>
					<td  style="border: solid 1px gray; background-color: #F1F7FD; font-variant: small-caps;">
						<xsl:value-of select="java:getString($dict_common, 'PHONE')"/>
					</td>
					<td  style="border: solid 1px gray;">
						<xsl:value-of select="@phone"/>
					</td>
				</tr>
			</xsl:if>
			<xsl:if test="@mobile">
				<tr>
					<td  style="border: solid 1px gray; background-color: #F1F7FD; font-variant: small-caps;">
						<xsl:value-of select="java:getString($dict_job_module, 'MOBILE')"/>
					</td>
					<td style="border: solid 1px gray;">
						<xsl:value-of select="@mobile"/>
					</td>
				</tr>
			</xsl:if>
			<xsl:if test="@birthday">
				<tr>
					<td  style="border: solid 1px gray; background-color: #F1F7FD; font-variant: small-caps;">
						<xsl:value-of select="java:getString($dict_job_module, 'BIRTHDAY')"/>
					</td>
					<td style="border: solid 1px gray;">
						<xsl:value-of select="@birthday"/>
					</td>
				</tr>
			</xsl:if>
			<xsl:if test="@birthplace">
				<tr>
					<td  style="border: solid 1px gray; background-color: #F1F7FD; font-variant: small-caps;">
						<xsl:value-of select="java:getString($dict_job_module, 'BIRTHPLACE')"/>
					</td>
					<td style="border: solid 1px gray;">
						<xsl:value-of select="@birthplace"/>
					</td>
				</tr>
			</xsl:if>
			<xsl:if test="@citizenship">
				<tr>
					<td  style="border: solid 1px gray; background-color: #F1F7FD; font-variant: small-caps;">
						<xsl:value-of select="java:getString($dict_job_module, 'CITIZENSHIP')"/>
					</td>
					<td style="border: solid 1px gray;">	
						<xsl:value-of select="@citizenship"/>
					</td>
				</tr>
			</xsl:if>
			<xsl:if test="@cv_flie">
				<tr>
					<td style="border: solid 1px gray; background-color: #F1F7FD; font-variant: small-caps;">
						<xsl:value-of select="java:getString($dict_job_module, 'APPLICANT_CV')"/>
					</td>
					<td style="border: solid 1px gray;">	
						Applicant CV is in attachement
					</td>
				</tr>
			</xsl:if>
			<xsl:apply-templates select="negeso:field"/>
	   </table>
</xsl:template>

<xsl:template match="negeso:field">
	<xsl:if test="@value">
		<tr>
			<td  style="border: solid 1px gray; background-color: #F1F7FD; font-variant: small-caps;">
				<xsl:value-of select="@title"/>
			</td>
			<td style="border: solid 1px gray;">
				<xsl:value-of select="@value"/>
			</td>
		</tr>
	</xsl:if>
</xsl:template>

<xsl:template match="negeso:field[@type_name='file']">
	<tr>
		<td  style="border: solid 1px gray; background-color: #F1F7FD; font-variant: small-caps;">
			<xsl:value-of select="@title"/>
		</td>
		<td style="border: solid 1px gray;">
			<a target="_blank" href="{@value}">
				<xsl:value-of select="@value"/>
			</a>
		</td>
	</tr>
</xsl:template>


<xsl:template match="negeso:field[@type_name='radio_box']">
	<tr>
		<td  style="border: solid 1px gray; background-color: #F1F7FD; font-variant: small-caps;">
			<xsl:value-of select="@title"/>
		</td>
		<td style="border: solid 1px gray;">
			<xsl:for-each select="negeso:option">
				<input type="radio" name="radio_{../@id}" disabled="true">
					<xsl:if test="@selected">
						<xsl:attribute name="checked">true</xsl:attribute>
					</xsl:if>
					<xsl:value-of select="@title"/>
				</input>
				<br/>
			</xsl:for-each>
		</td>
	</tr>
</xsl:template>

<xsl:template match="negeso:field[@type_name='check_box']">
	<tr>
		<td style="border: solid 1px gray; background-color: #F1F7FD; font-variant: small-caps;">
			<xsl:value-of select="@title"/>
		</td>
		<td style="border: solid 1px gray;">
			<xsl:for-each select="negeso:option">
				<input type="checkbox" name="checkbox_{../@id}" disabled="true">
					<xsl:if test="@selected">
						<xsl:attribute name="checked">true</xsl:attribute>
					</xsl:if>
					<xsl:value-of select="@title"/>
				</input>
				<br/>
			</xsl:for-each>
		</td>
	</tr>
</xsl:template>

<xsl:template match="negeso:field[@type_name='select_box']">
	<tr>
		<td style="border: solid 1px gray; font-variant: small-caps;">
			<xsl:value-of select="@title"/>
		</td>
		<td style="border: solid 1px gray;">
			<select disabled="true">
				<option value="">&#160;</option>
				<xsl:for-each select="negeso:option">
					<option type="checkbox" name="{@id}" readonly="true">
						<xsl:if test="@selected">
							<xsl:attribute name="selected">true</xsl:attribute>
						</xsl:if>
						<xsl:value-of select="@title"/>
					</option>
					<br/>
				</xsl:for-each>
			</select>
		</td>
	</tr>
</xsl:template>

</xsl:stylesheet>
