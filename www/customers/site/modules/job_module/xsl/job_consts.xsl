<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (c) 2007 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.


  XSL templates for Job Module Titles
 
  @version		2007.12.24
  @author		Oleg 'germes' Shvedov
  @author		Rostislav 'KOTT' Brizgunov
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">


<xsl:variable name="dict_job_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('job_module', $lang)"/>

<xsl:template name="job_title">
<xsl:param name="mode"></xsl:param>
	<xsl:choose>
        <xsl:when test="$mode='general'">
			<!-- show 1st page of JM -->
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_job_module"/>
				<xsl:with-param name ="name"  select="'JM_WORK_AREAS'"/>
			</xsl:call-template>
			<xsl:value-of select="java:getString($dict_job_module, 'JM_WORK_AREAS')"/>
        </xsl:when>
        <xsl:when test="$mode='department_vacancies'">
            <!-- show page with vacancies of department -->
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_job_module"/>
				<xsl:with-param name ="name"  select="'JM_VACANCIES_OF'"/>
			</xsl:call-template>
			<xsl:value-of select="java:getString($dict_job_module, 'JM_VACANCIES_OF')"/>&#160;<xsl:value-of select="@department"/>
        </xsl:when>
        <xsl:when test="$mode='post_gen_application'">
       		<!-- show general application form -->
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_job_module"/>
				<xsl:with-param name ="name"  select="'JM_OPEN_FORM'"/>
			</xsl:call-template>
			<xsl:value-of select="java:getString($dict_job_module, 'JM_OPEN_FORM')"/>
        </xsl:when>
        <xsl:when test="$mode='post_search_result'">
       		<!-- show search results for posts -->
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_job_module"/>
				<xsl:with-param name ="name"  select="'JM_APPLICATION'"/>
			</xsl:call-template>
		 	<xsl:value-of select="java:getString($dict_job_module, 'JM_APPLICATION')"/>
        </xsl:when>
        <xsl:when test="$mode='post_application' or $mode='post_vac_application'">
       		<!-- show post application page -->
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_job_module"/>
				<xsl:with-param name ="name"  select="'JM_APPLICATION_FORM'"/>
			</xsl:call-template>
			<xsl:value-of select="java:getString($dict_job_module, 'JM_APPLICATION_FORM')"/>
        </xsl:when>
        <xsl:when test="$mode='save_application' or $mode='save_gen_application' or $mode='save_vac_application' or $mode='save_search_result'">
       		<!-- show save application -->
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_job_module"/>
				<xsl:with-param name ="name"  select="'JM_APPLICATION_SENT'"/>
			</xsl:call-template>
	    	<xsl:value-of select="java:getString($dict_job_module, 'JM_APPLICATION_SENT')"/>
        </xsl:when>
        <xsl:when test="$mode='show_all_vacancies'">
       		<!-- Show all vacancies -->
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_job_module"/>
				<xsl:with-param name ="name"  select="'JM_ALL_VACANCIES'"/>
			</xsl:call-template>
			<xsl:value-of select="java:getString($dict_job_module, 'JM_ALL_VACANCIES')"/>
        </xsl:when>
        <xsl:when test="$mode='vacancy_details' or $mode='search_result'">
       		<!-- Show vcancy details -->
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_job_module"/>
				<xsl:with-param name ="name"  select="'JM_VACANCY'"/>
			</xsl:call-template>
			<xsl:value-of select="java:getString($dict_job_module, 'JM_VACANCY')"/>
        </xsl:when>
        <xsl:when test="$mode='search'">
       		<!-- show search page -->
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_job_module"/>
				<xsl:with-param name ="name"  select="'JM_SEARCH'"/>
			</xsl:call-template>
			<xsl:value-of select="java:getString($dict_job_module, 'JM_SEARCH')"/>
        </xsl:when>
        <xsl:when test="$mode='do_search'">
       		<!-- show search results -->
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_job_module"/>
				<xsl:with-param name ="name"  select="'JM_SEARCH_RESULTS'"/>
			</xsl:call-template>
        	<xsl:value-of select="java:getString($dict_job_module, 'JM_SEARCH_RESULTS')"/>
        </xsl:when>
        <xsl:when test="$mode='error'">
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_job_module"/>
				<xsl:with-param name ="name"  select="'JM_REQUEST_NOT_PROCESSED'"/>
			</xsl:call-template>
           	<xsl:value-of select="java:getString($dict_job_module, 'JM_REQUEST_NOT_PROCESSED')"/>
        </xsl:when>
	</xsl:choose>
</xsl:template>

</xsl:stylesheet>
