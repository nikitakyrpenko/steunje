<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (c) 2009 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.

-->

<xsl:stylesheet version="1.0"
	   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	   xmlns:negeso="http://negeso.com/2003/Framework"
	   xmlns:java="http://xml.apache.org/xslt/java"
	   exclude-result-prefixes="java">

	<xsl:template name ="showAllTranslations">
		<xsl:call-template name ="general-translations" />
		<xsl:call-template name ="contactbook-translations" />
	</xsl:template>

	<xsl:template name ="add-constant-info">
		<xsl:param name ="name">'There is not constant name'</xsl:param>
		<xsl:param name ="dict" select="$dict_common" />

		<xsl:if test ="$outputType = 'admin'">
			<xsl:attribute name ="constName">
				<xsl:value-of select ="$name"/>
			</xsl:attribute>
			<xsl:attribute name ="constText">
				<xsl:value-of select ="java:getString($dict, $name)"/>
			</xsl:attribute>
		</xsl:if>
	</xsl:template>


	<xsl:template name ="general-translations">

	</xsl:template>

	<xsl:template name ="contactbook-translations">
		<ul title="Contact book">
			<li class="title">Contact book</li>
			<li constName="NO_MATCHES">
				<xsl:value-of select="java:getString($dict_contact_book, 'NO_MATCHES')"/>
			</li>
			<li constName="CB_CONTACTBOOK">
				<xsl:value-of select="java:getString($dict_contact_book, 'CB_CONTACTBOOK')"/>
			</li>
			<li constName="CB_FNAME">
				<xsl:value-of select="java:getString($dict_contact_book, 'CB_FNAME')"/>
			</li>
			<li constName="CB_BACK">
				<xsl:value-of select="java:getString($dict_contact_book, 'CB_BACK')"/>
			</li>
			<li constName="CB_SEARCH">
				<xsl:value-of select="java:getString($dict_contact_book, 'CB_SEARCH')"/>
			</li>
			<li constName="CB_GO">
				<xsl:value-of select="java:getString($dict_contact_book, 'CB_GO')"/>
			</li>
			<li constName="CB_ADVSEARCH">
				<xsl:value-of select="java:getString($dict_contact_book, 'CB_ADVSEARCH')"/>
			</li>
			<li constName="CB_BYGROUP">
				<xsl:value-of select="java:getString($dict_contact_book, 'CB_BYGROUP')"/>
			</li>
			<li constName="CB_SHOWALL">
				<xsl:value-of select="java:getString($dict_contact_book, 'CB_SHOWALL')"/>
			</li>
			<li constName="CB_CONTACT">
				<xsl:value-of select="java:getString($dict_contact_book, 'CB_CONTACT')"/>
			</li>
			<li constName="CB_NAME">
				<xsl:value-of select="java:getString($dict_contact_book, 'CB_NAME')"/>
			</li>
			<li constName="CB_BIRTHDAY">
				<xsl:value-of select="java:getString($dict_contact_book, 'CB_BIRTHDAY')"/>
			</li>
			<li constName="CB_EMAIL">
				<xsl:value-of select="java:getString($dict_contact_book, 'CB_EMAIL')"/>
			</li>
			<li constName="CB_DEPARTMENT">
				<xsl:value-of select="java:getString($dict_contact_book, 'CB_DEPARTMENT')"/>
			</li>
			<li constName="CB_JOBTITLE">
				<xsl:value-of select="java:getString($dict_contact_book, 'CB_JOBTITLE')"/>
			</li>
			<li constName="CB_PHONE">
				<xsl:value-of select="java:getString($dict_contact_book, 'CB_PHONE')"/>
			</li>
			<li constName="CB_WEBADDRESS">
				<xsl:value-of select="java:getString($dict_contact_book, 'CB_WEBADDRESS')"/>
			</li>
			<li constName="CB_IMAGE">
				<xsl:value-of select="java:getString($dict_contact_book, 'CB_IMAGE')"/>
			</li>
			<li constName="CB_NEXT_BIRTHDAYS">
				<xsl:value-of select="java:getString($dict_contact_book, 'CB_NEXT_BIRTHDAYS')"/>
			</li>
		</ul>
	</xsl:template>

	<xsl:template name ="documentmodule-translations">
		<ul title="Document module">
			<li class="title">Document modules</li>
			<li constName="NO_MATCHES">
				<xsl:value-of select="java:getString($dict_contact_book, 'NO_MATCHES')"/>
			</li>
		</ul>
	</xsl:template>

</xsl:stylesheet>