<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (c) 2007 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.


  XSL templates for Job Module
 
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

<xsl:template match="negeso:job_module_component" mode="page_head">
	<link rel="stylesheet" type="text/css" href="/site/modules/job_module/css/job.css"/>	
	<script type="text/javascript" src="/site/modules/job_module/script/job.js">/**/</script>
</xsl:template>

<xsl:template match="negeso:job_module_component" mode="job">
	<xsl:call-template name="job_page_title">
		<xsl:with-param name="mode"><xsl:value-of select="@mode"/></xsl:with-param>
					</xsl:call-template>
								<xsl:call-template name="job_chooser"/>
</xsl:template>

<xsl:template name="job_chooser">
<!-- caurrent node: negeso:job_module_component -->

    <form name="job_form" enctype="multipart/form-data">
    	<xsl:choose>
			<xsl:when test="@mode='post_application' or @mode='post_vac_application' or @mode='post_gen_application' or @mode='post_search_result'">
				<xsl:attribute name="method">post</xsl:attribute>
			</xsl:when>
			<xsl:otherwise>
				<xsl:attribute name="method">get</xsl:attribute>
			</xsl:otherwise>
		</xsl:choose>
		<input id="mode" name="mode" type="hidden"/>
		<input id="dep_id" name="dep_id" type="hidden"/>
		<input id="vac_id" name="vac_id" type="hidden"/>
	    <xsl:choose>
	        <xsl:when test="@mode='general'">
	        		 <!-- show 1st page of JM -->
	            <xsl:call-template name="job_main_page" />
	        </xsl:when>
	        <xsl:when test="@mode='department_vacancies'">
	            <!-- show page with vacancies of department -->
	            <xsl:call-template name="job_vacan_depart" />
	        </xsl:when>
	        <xsl:when test="@mode='post_application'">
	        		<!-- show post application page -->
	        		<xsl:call-template name="job_post_appl" />
	        </xsl:when>
	        <xsl:when test="@mode='post_vac_application'">
	        		 <!-- show vacancy application form -->
	            <xsl:call-template name="job_vac_appl_form" />
	        </xsl:when>
	        <xsl:when test="@mode='post_gen_application'">
	        		<!-- show general application form -->
	        		<xsl:call-template name="job_gener_appl_form" />
	        </xsl:when>
	        <xsl:when test="@mode='save_application'">
	        		<!-- show save application -->
	        		<xsl:call-template name="job_save_appl" />
	        </xsl:when>
	        <xsl:when test="@mode='save_vac_application'">
	        		<!-- show save vacancy application -->
	        		<xsl:call-template name="job_save_vac_appl" />
	        </xsl:when>
	        <xsl:when test="@mode='save_gen_application'">
	            <!-- save general application -->
	            <xsl:call-template name="job_save_gen_appl" />
	        </xsl:when>
	        <xsl:when test="@mode='show_all_vacancies'">
	        		<!-- Show all vacancies -->
	        		<xsl:call-template name="job_show_all_vac" />
	        </xsl:when>
	        <xsl:when test="@mode='vacancy_details'">
	        		<!-- Show vcancy details -->
	        		<xsl:call-template name="job_vac_details" />
	        </xsl:when>
	        <xsl:when test="@mode='search'">
	        		<!-- show search page -->
	        		<xsl:call-template name="job_search_appl" />
	        </xsl:when>
	        <xsl:when test="@mode='do_search'">
	        		<!-- show search results -->
	        		<xsl:call-template name="job_search_appl_results" />
	        </xsl:when>
	        <xsl:when test="@mode='search_result'">
	        		<!-- show search vacancy results -->
	        		<xsl:call-template name="job_show_search_vac_results" />
	        </xsl:when>
	        <xsl:when test="@mode='post_search_result'">
	        		<!-- show search results for posts -->
	        		<xsl:call-template name="job_search_result_posted" />
	        </xsl:when>
	        <xsl:when test="@mode='save_search_result'">
	        		<!-- show save search results -->
	        		<xsl:call-template name="job_save_search" />
	        </xsl:when>
	        <xsl:when test="@mode='error'">
	            <div class="jobForgTitle">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_job_module"/>
						<xsl:with-param name ="name"  select="'JM_REQUEST_NOT_PROCESSED'"/>
					</xsl:call-template>
	            	<xsl:value-of select="java:getString($dict_job_module, 'JM_REQUEST_NOT_PROCESSED')"/>
	            </div>
	        </xsl:when>
	    </xsl:choose>
    </form>
</xsl:template>

<xsl:template name="job_main_page">
	<!-- show 1st page of JM
		  current XML tag is negeso:job_module_component
		  @mode='general'
	 -->
	<xsl:apply-templates select="negeso:departments" mode="general_list"/>
	<br/><br/>
	<div class="m-jobLinks">
			<a href="?mode=show_all_vacancies">
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_job_module"/>
					<xsl:with-param name ="name"  select="'JM_SHOW_ALL'"/>
				</xsl:call-template>
				<xsl:value-of select="java:getString($dict_job_module, 'JM_SHOW_ALL')"/>
		</a><br/>
			<a href="?mode=post_gen_application">
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_job_module"/>
					<xsl:with-param name ="name"  select="'JM_OPEN_APPLICATION'"/>
				</xsl:call-template>
				<xsl:value-of select="java:getString($dict_job_module, 'JM_OPEN_APPLICATION')"/>
		</a><br/>
			<a href="?mode=search">
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_job_module"/>
					<xsl:with-param name ="name"  select="'JM_SEARCH'"/>
				</xsl:call-template>
				<xsl:value-of select="java:getString($dict_job_module, 'JM_SEARCH')"/>
			</a>
		</div>
</xsl:template>

<xsl:template name="job_vacan_depart">
	<!-- show page with vacancies of department
		  @mode='department_vacancies'
	-->
	<xsl:apply-templates select="negeso:vacancy_list" mode="department"/>	
	<div class="m-jobLinks"><xsl:call-template name="job_search_bar"/></div>	
	<xsl:call-template name="job_backlink_bar">
		<xsl:with-param name="backLink">javascript:backToGeneral();</xsl:with-param>
	</xsl:call-template>
</xsl:template>

<xsl:template name="job_post_appl">
	<!-- show post application page
		  @mode='post_application'
	-->
	<xsl:apply-templates select="negeso:application_form">
		<xsl:with-param name="onclick">sendApplication('<xsl:value-of select="@dep_id"/>', '<xsl:value-of select="negeso:application_form/@vacancy_id"/>'); return false;</xsl:with-param>
	</xsl:apply-templates>
	
	<xsl:call-template name="job_backlink_bar">
		<xsl:with-param name="backLink">javascript:postDepApplicationBack(<xsl:value-of select="@dep_id" />);</xsl:with-param>
	</xsl:call-template>
</xsl:template>

<xsl:template name="job_vac_appl_form">
	<!-- show vacancy application form
			@mode='post_vac_application'
	-->
	<xsl:apply-templates select="negeso:application_form">
		<xsl:with-param name="onclick">saveVacApplication('<xsl:value-of select="@dep_id"/>', '<xsl:value-of select="@vac_id"/>'); return false;</xsl:with-param>
	</xsl:apply-templates>
    
    <xsl:call-template name="job_backlink_bar">
		<xsl:with-param name="backLink">javascript:postVacApplicationBack('<xsl:value-of select="@dep_id"/>', '<xsl:value-of select="@vac_id"/>');</xsl:with-param>
    </xsl:call-template>
</xsl:template>

<xsl:template name="job_gener_appl_form">
	<!-- show general application form
		  @mode='post_gen_application'
	-->
  <table cellpadding="0" cellspacing="5" border="0">
       <tr>
           <td width="200">
			   <xsl:call-template name ="add-constant-info">
				   <xsl:with-param name ="dict"  select="$dict_job_module"/>
				   <xsl:with-param name ="name"  select="'JM_WORK_AREAS'"/>
			   </xsl:call-template>
               <xsl:value-of select="java:getString($dict_job_module, 'JM_WORK_AREAS')"/> <span class="red">*</span>
           </td>
           
           <td id="choose_dep_td">
	           	<xsl:apply-templates select="negeso:departments/negeso:department" mode="choose_dep"/>
           </td>
       </tr>
	</table>
   
	<xsl:apply-templates select="negeso:application_form">
		<xsl:with-param name="onclick">postGeneralForm('1'); return false;</xsl:with-param>
	</xsl:apply-templates>
	
	<xsl:if test="@start_page!='GeneralApplicationForm'">
  		<xsl:call-template name="job_backlink_bar">
	  		<xsl:with-param name="backLink">javascript:backToGeneral();</xsl:with-param>
		</xsl:call-template>
	</xsl:if>
</xsl:template>

<xsl:template name="job_save_appl">
<!-- show save application
		@mode='save_application'"
-->
	<xsl:call-template name="job_backlink_bar">
   		<xsl:with-param name="backLink">javascript:postDepApplicationBack(<xsl:value-of select="@dep_id"/>);</xsl:with-param>
    </xsl:call-template>
</xsl:template>

<xsl:template name="job_save_vac_appl">
	<!-- show save vacancy application
		"@mode='save_vac_application'
	-->
	<xsl:call-template name="job_backlink_bar">
   		<xsl:with-param name="backLink">javascript:postVacApplicationBack('<xsl:value-of select="@dep_id"/>', '<xsl:value-of select="@vac_id"/>');</xsl:with-param>
	</xsl:call-template>
</xsl:template>

<xsl:template name="job_save_gen_appl">
	 <!-- save general application
	 		@mode='post_gen_application'
	 -->
    <xsl:call-template name="job_backlink_bar">
    	 <xsl:with-param name="backLink">javascript:backToGeneral();</xsl:with-param>
    </xsl:call-template>
</xsl:template>

<xsl:template name="job_show_all_vac">
	<!-- Show all vacancies
			@mode='show_all_vacancies'
	-->
   	<xsl:apply-templates select="negeso:department_list/negeso:department" mode="all"/>
		<xsl:call-template name="job_search_bar"/>
   
   <xsl:if test="@start_page!='AllVacanciesView'">
       <xsl:call-template name="job_backlink_bar">
	    	 <xsl:with-param name="backLink">javascript:backToGeneral();</xsl:with-param>
	    </xsl:call-template>
	</xsl:if>
</xsl:template>

<xsl:template name="job_vac_details">
	<!-- Show vacancy details
			@mode='vacancy_details'
	-->
	<xsl:apply-templates select="negeso:vacancy" mode="detail"/>
		<xsl:call-template name="job_search_bar"/>
	
	<xsl:call-template name="job_backlink_bar">
    	<xsl:with-param name="backLink">javascript:showAllVacs();</xsl:with-param>
	</xsl:call-template>
	
</xsl:template>

<xsl:template name="job_search_appl">
	<!-- show search page
		@mode='search'
	-->
  <table cellpadding="0" cellspacing="5" border="0">
    	<tr>
    		<th colspan="2">
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_job_module"/>
					<xsl:with-param name ="name"  select="'JM_SEARCH'"/>
				</xsl:call-template>
    			<xsl:value-of select="java:getString($dict_job_module, 'JM_SEARCH')"/>
    		</th>
    	</tr>
        <tr>
            <td width="175">
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_job_module"/>
					<xsl:with-param name ="name"  select="'JM_SEARCH_FOR'"/>
				</xsl:call-template>
           		<xsl:value-of select="java:getString($dict_job_module, 'JM_SEARCH_FOR')"/>
            </td>
            <td>
				<input required="true" type="text" name="search_word" id="search_word" class="jobInput_200"/>
            </td>
        </tr>
        <tr>
	        <td colspan="2">
				<xsl:call-template name="job_search_bar">
					<xsl:with-param name="mode">
						<xsl:value-of select="@mode"/>
					</xsl:with-param>
				</xsl:call-template>
			</td>
		</tr>
	</table>

    <xsl:call-template name="job_backlink_bar">
   		 <xsl:with-param name="backLink">javascript:backToGeneral();</xsl:with-param>
    </xsl:call-template>
</xsl:template>

<xsl:template name="job_search_appl_results">
	<!-- show search results
			@mode='do_search'
	-->
   <input type="hidden" name="search_word"><xsl:attribute name="value"><xsl:value-of select="@search_word"/></xsl:attribute></input>
   
	    <h1>
		
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_job_module"/>
				<xsl:with-param name ="name"  select="'JM_SEARCH_RESULTS'"/>
			</xsl:call-template>
			<xsl:value-of select="java:getString($dict_job_module, 'JM_SEARCH_RESULTS')"/>
		</h1>
		<xsl:choose>
			<xsl:when test="@count=0">
		    	<strong>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_job_module"/>
						<xsl:with-param name ="name"  select="'JM_VACANCY_NOT_FOUND'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_job_module, 'JM_VACANCY_NOT_FOUND')"/>
				</strong>
			</xsl:when>
			<xsl:otherwise>
	    		<xsl:apply-templates select="negeso:vacancy_search_results/negeso:vacancy_search_result" mode="job"/>
			</xsl:otherwise>
		</xsl:choose>
	
	<xsl:call-template name="job_backlink_bar">
		<xsl:with-param name="backLink">javascript:goToSearch();</xsl:with-param>
	</xsl:call-template>
</xsl:template>

<xsl:template name="job_show_search_vac_results">
	<!-- show search vacancy results
			@mode='search_result'
	-->
	<input type="hidden" name="search_word"><xsl:attribute name="value"><xsl:value-of select="@search_word"/></xsl:attribute></input>
   
	<xsl:apply-templates select="negeso:vacancy" mode="result"/>
	
	<xsl:call-template name="job_backlink_bar">
   		<xsl:with-param name="backLink">javascript:goToDoSearch();</xsl:with-param>
	</xsl:call-template>
</xsl:template>

<xsl:template name="job_search_result_posted">
	<!-- show search results for posts
		@mode='post_search_result'"
	-->
	 <input type="hidden" name="search_word"><xsl:attribute name="value"><xsl:value-of select="@search_word"/></xsl:attribute></input>
	 
 	<xsl:apply-templates select="negeso:application_form">
		<xsl:with-param name="onclick">saveSearchApplication('<xsl:value-of select="@vac_id"/>','<xsl:value-of select="@dep_id"/>')</xsl:with-param>
	</xsl:apply-templates>
	 
	 
	 <xsl:call-template name="job_backlink_bar">
    	 <xsl:with-param name="backLink">javascript:goToSearchResult('<xsl:value-of select="@vac_id"/>');</xsl:with-param>
    </xsl:call-template>
</xsl:template>

<xsl:template name="job_save_search">
	<!-- show save search results
			@mode='save_search_result'
	-->
	<xsl:call-template name="job_save_search" />
	<input type="hidden" name="search_word"><xsl:attribute name="value"><xsl:value-of select="@search_word"/></xsl:attribute></input>
	
	<xsl:call-template name="job_backlink_bar">
   		<xsl:with-param name="backLink">javascript:goToSearchResult('<xsl:value-of select="@vac_id"/>');</xsl:with-param>
	</xsl:call-template>
</xsl:template>

<xsl:template match="negeso:departments" mode="general_list">
  <table cellpadding="0" cellspacing="5" border="0">
		<tr>
			<th width="30">
				
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_job_module"/>
						<xsl:with-param name ="name"  select="'JM_TITLE'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_job_module, 'JM_TITLE')"/>

			</th>
			<th width="50">

					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_job_module"/>
						<xsl:with-param name ="name"  select="'JM_DESCRIPTION'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_job_module, 'JM_DESCRIPTION')"/>

			</th>
			<th width="20">

					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_job_module"/>
						<xsl:with-param name ="name"  select="'JM_VACANCIES'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_job_module, 'JM_VACANCIES')"/>
			</th>
		</tr>
		<xsl:apply-templates select="negeso:department" mode="general_list"/>
	</table>
</xsl:template>

<xsl:template match="negeso:department" mode="general_list">
	<!-- current HTML tag is TBODY -->
    <xsl:if test="@id!=1">
        <tr>
            <td>
                <a href="?mode=department_vacancies&amp;dep_id={@id}">
                    <xsl:value-of select="@title"/>
                </a>
            </td>
            <td><xsl:value-of select="@description"/></td>
            <td><xsl:value-of select="@vacancies"/></td>
        </tr>
    </xsl:if>
</xsl:template>

<xsl:template match="negeso:vacancy_list" mode="department">
	<xsl:apply-templates select="negeso:vacancy" mode="detail" />	
		<a href="?mode=post_application&amp;dep_id={//negeso:job_module_component/@dep_id}&amp;vac_id=">
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_job_module"/>
				<xsl:with-param name ="name"  select="'JM_PLACE_APPLICATION_DEPARTMENT'"/>
			</xsl:call-template>
			<xsl:value-of select="java:getString($dict_job_module, 'JM_PLACE_APPLICATION_DEPARTMENT')"/>
		</a>
</xsl:template>

<xsl:template match="negeso:application_form">
<xsl:param name="onclick"></xsl:param>
  <table cellpadding="0" cellspacing="5" border="0">
		<xsl:apply-templates select="negeso:field"/>
		<tr>
			<td colspan="2">
				<input type="button" class="submit">
					<xsl:attribute name="onClick">
						<xsl:value-of select="$onclick"/>
					</xsl:attribute>
					<xsl:attribute name="value">
			           <xsl:value-of select="java:getString($dict_job_module, 'JM_PLACE')"/>
					</xsl:attribute>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_job_module"/>
						<xsl:with-param name ="name"  select="'JM_PLACE'"/>
					</xsl:call-template>
				</input>
			</td>
		</tr>
    </table>
</xsl:template>

<xsl:template match="negeso:field">
    <tr>
        <td width="200">
            <xsl:value-of select="@title"/>
            <xsl:if test="@is_required='true' or @is_common='true'">
            	<span class="red">&#160;*</span>
            </xsl:if>
        </td>
        <td>
            <xsl:choose>
                <xsl:when test="@type_name='string'">
                    <input type="text" class="m-jobInput">
                        <xsl:call-template name="job_required_on" />
                        <xsl:choose>
                            <xsl:when test="not(@sys_name)">
                                <xsl:attribute name="name">_id<xsl:value-of select="@id"/></xsl:attribute>
                                <xsl:attribute name="id">_id<xsl:value-of select="@id"/></xsl:attribute>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:attribute name="name"><xsl:value-of select="@sys_name"/></xsl:attribute>
                                <xsl:attribute name="id"><xsl:value-of select="@sys_name"/></xsl:attribute>
                            </xsl:otherwise>
                        </xsl:choose>
                    </input>
                </xsl:when>
                <xsl:when test="@type_name='number'">
                    <input type="text" numeric_field_params="c;12" class="m-jobInput">
                        <xsl:call-template name="job_required_on" />
                        <xsl:choose>
                            <xsl:when test="not(@sys_name)">
                                <xsl:attribute name="name">_id<xsl:value-of select="@id"/></xsl:attribute>
                                <xsl:attribute name="id">_id<xsl:value-of select="@id"/></xsl:attribute>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:attribute name="name"><xsl:value-of select="@sys_name"/></xsl:attribute>
                                <xsl:attribute name="id"><xsl:value-of select="@sys_name"/></xsl:attribute>
                            </xsl:otherwise>
                        </xsl:choose>
                    </input>
                </xsl:when>
                <xsl:when test="@type_name='text'">
                    <textarea class="m-jobInput">
                        <xsl:call-template name="job_required_on" />
                        <xsl:choose>
                            <xsl:when test="not(@sys_name)">
                                <xsl:attribute name="name">_id<xsl:value-of select="@id"/></xsl:attribute>
                                <xsl:attribute name="id">_id<xsl:value-of select="@id"/></xsl:attribute>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:attribute name="name"><xsl:value-of select="@sys_name"/></xsl:attribute>
                                <xsl:attribute name="id"><xsl:value-of select="@sys_name"/></xsl:attribute>
                            </xsl:otherwise>
                        </xsl:choose>
                    </textarea>
                </xsl:when>
                <xsl:when test="@type_name='select_box'">
                    <select class="m-jobSel">
                        <xsl:attribute name="name">_id<xsl:value-of select="@id"/></xsl:attribute>
                        <xsl:attribute name="id">_id<xsl:value-of select="@id"/></xsl:attribute>
                        <xsl:apply-templates select="negeso:option" mode="select"/>
                    </select>
                </xsl:when>
                <xsl:when test="@type_name='check_box'">
                    <xsl:apply-templates select="negeso:option" mode="checkbox"/>
                </xsl:when>
                <xsl:when test="@type_name='file'">
                    <input type="file" class="m-jobInput m-jobInputCheckbox" style="cursor: hand;" onKeyPress="return false;">
                        <xsl:call-template name="job_required_on" />
                        <xsl:choose>
                            <xsl:when test="not(@sys_name)">
                                <xsl:attribute name="name">_id<xsl:value-of select="@id"/></xsl:attribute>
                                <xsl:attribute name="id">_id<xsl:value-of select="@id"/></xsl:attribute>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:attribute name="name"><xsl:value-of select="@sys_name"/></xsl:attribute>
                                <xsl:attribute name="id"><xsl:value-of select="@sys_name"/></xsl:attribute>
                            </xsl:otherwise>
                        </xsl:choose>
                    </input>
                </xsl:when>
                <xsl:when test="@type_name='email'">
                    <input type="text" is_email="true" class="jobInput_200">
                        <xsl:call-template name="job_required_on" />
                        <xsl:choose>
                            <xsl:when test="not(@sys_name)">
                                <xsl:attribute name="name">_id<xsl:value-of select="@id"/></xsl:attribute>
                                <xsl:attribute name="id">_id<xsl:value-of select="@id"/></xsl:attribute>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:attribute name="name"><xsl:value-of select="@sys_name"/></xsl:attribute>
                                <xsl:attribute name="id"><xsl:value-of select="@sys_name"/></xsl:attribute>
                            </xsl:otherwise>
                        </xsl:choose>
                    </input>
                </xsl:when>
                <xsl:when test="@type_name='radio_box'">
                    <xsl:apply-templates select="negeso:option" mode="radio"/>
                </xsl:when>
                <xsl:when test="@type_name='date'">
                    <input type="text" timedate_field_format="DD-MM-YYYY" class="jobInput_200">
                        <xsl:call-template name="job_required_on" />
                        <xsl:choose>
                            <xsl:when test="not(@sys_name)">
                                <xsl:attribute name="name">_id<xsl:value-of select="@id"/></xsl:attribute>
                                <xsl:attribute name="id">_id<xsl:value-of select="@id"/></xsl:attribute>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:attribute name="name"><xsl:value-of select="@sys_name"/></xsl:attribute>
                                <xsl:attribute name="id"><xsl:value-of select="@sys_name"/></xsl:attribute>
                            </xsl:otherwise>
                        </xsl:choose>
                    </input>&#160;&#160;(dd-mm-yyyy)
                </xsl:when>
            </xsl:choose>
        </td>
    </tr>
</xsl:template>

<xsl:template name="job_required_on">
<!-- current XML tag is negeso:field -->
	<xsl:if test="@is_required='true' or @is_common='true'">
       <xsl:attribute name="required">true</xsl:attribute>
   </xsl:if>
</xsl:template>

<xsl:template match="negeso:option" mode="radio">
    <input type="radio" class="form_radio">
        <xsl:if test="position()=1">
            <xsl:attribute name="checked">true</xsl:attribute>
        </xsl:if>
        <xsl:attribute name="name">_id<xsl:value-of select="../@id"/></xsl:attribute>
        <xsl:attribute name="value">_op<xsl:value-of select="@id"/></xsl:attribute>
    </input><xsl:value-of select="@title"/><br/>
</xsl:template>

<xsl:template match="negeso:option" mode="checkbox">
    <input type="checkbox" class="form_checkbox">
        <xsl:attribute name="name">_id<xsl:value-of select="../@id"/></xsl:attribute>
        <xsl:attribute name="value">_op<xsl:value-of select="@id"/></xsl:attribute>
    </input>&#160;&#160;<xsl:value-of select="@title"/><br/>
</xsl:template>

<xsl:template match="negeso:option" mode="select">
    <option>
        <xsl:if test="position()=1">
            <xsl:attribute name="selected">true</xsl:attribute>
        </xsl:if>
        <xsl:attribute name="value">_op<xsl:value-of select="@id"/></xsl:attribute>
        <xsl:value-of select="@title"/>
    </option>
</xsl:template>

<xsl:template match="negeso:department" mode="all">
<br/>
	<div class="m-jobVacancy">
		<h1><xsl:value-of select="@title"/></h1>
		<xsl:choose>
			<xsl:when test="@vac_count=0">
				<div>
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_job_module"/>
						<xsl:with-param name ="name"  select="'JM_VACANCY_NOT_FOUND'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_job_module, 'JM_VACANCY_NOT_FOUND')"/>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="negeso:vacancy" mode="all"/>
			</xsl:otherwise>
		</xsl:choose>
	</div>
</xsl:template>

<xsl:template match="negeso:vacancy" mode="all">
		<a href="?mode=vacancy_details&amp;dep_id={../@id}&amp;vac_id={@id}">
			<xsl:value-of select="@title"/>
	</a><br/>
</xsl:template>

<xsl:template match="negeso:vacancy" mode="detail">
  <table cellpadding="0" cellspacing="5" border="0">
        <tr>
            <th colspan="2">
	            <xsl:value-of select="@title"/>
            </th>
        </tr>
        <tr>
            <td class="jobWidth_175">
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_job_module"/>
					<xsl:with-param name ="name"  select="'JM_POSITION'"/>
				</xsl:call-template>
           		<xsl:value-of select="java:getString($dict_job_module, 'JM_POSITION')"/>
            </td>
            <td><xsl:value-of select="@position"/></td>
        </tr>
        <tr>
            <td>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_job_module"/>
					<xsl:with-param name ="name"  select="'JM_SALARY_S'"/>
				</xsl:call-template>
           		<xsl:value-of select="java:getString($dict_job_module, 'JM_SALARY_S')"/>
            </td>
            <td><xsl:value-of select="@salary"/></td>
        </tr>
        <tr>
            <td>
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_job_module"/>
					<xsl:with-param name ="name"  select="'JM_DESCRIPTION'"/>
				</xsl:call-template>
               	<xsl:value-of select="java:getString($dict_job_module, 'JM_DESCRIPTION')"/>
            </td>
            <td>
                <div id="job_div1" class="contentStyle">
                    <xsl:value-of select="negeso:article/negeso:text/text()" disable-output-escaping="yes"/>
                </div>
           </td>
        </tr>
        <tr>
            <td colspan="2" class="jobPlaceVacLink">
				<a href="?mode=post_vac_application&amp;dep_id={@department_id}&amp;vac_id={@id}">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_job_module"/>
						<xsl:with-param name ="name"  select="'JM_PLACE_APPLICATION_VACANCY'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_job_module, 'JM_PLACE_APPLICATION_VACANCY')"/>
				</a>
            </td>
        </tr>
    </table>
</xsl:template>

<xsl:template match="negeso:department" mode="choose_dep">
    <xsl:if test="@id!=1">
	    <input type="checkbox" name="_dep" class="form_checkbox">
    	    <xsl:attribute name="value"><xsl:value-of select="@id"/></xsl:attribute>
	    </input>
    	&#160;<xsl:value-of select="@title"/><br/>
    </xsl:if>
</xsl:template>

<xsl:template match="negeso:vacancy_search_results" mode="job">
	<xsl:apply-templates select="negeso:vacancy_search_result" mode="job"/>
</xsl:template>

<xsl:template match="negeso:vacancy_search_result" mode="job">
	<div class="jobVacancyList">
		<a href="?mode=search_result&amp;vac_id={@id}">
			<xsl:value-of select="@title"/>
		</a>
	</div>
</xsl:template>

<xsl:template match="negeso:vacancy" mode="result">
  <table cellpadding="0" cellspacing="5" border="0">
		<xsl:call-template name="job_show_vac_table" />
		<tr>
			<td colspan="2" class="jobPlaceVacLink">
		    	<a href="?mode=post_search_result&amp;vac_id={@id}">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_job_module"/>
						<xsl:with-param name ="name"  select="'JM_PLACE_APPLICATION_VACANCY'"/>
					</xsl:call-template>
		            <xsl:value-of select="java:getString($dict_job_module, 'JM_PLACE_APPLICATION_VACANCY')"/>
		          </a>
			</td>
		</tr>
	</table>
	
</xsl:template>

<xsl:template name="job_show_vac_table">
	<tr>
		<th colspan="2">
			<xsl:value-of select="@title"/>
		</th>
	</tr>
	<tr>
		<td>
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_job_module"/>
				<xsl:with-param name ="name"  select="'JM_DEPARMENT'"/>
			</xsl:call-template>
			<xsl:value-of select="java:getString($dict_job_module, 'JM_DEPARMENT')"/>
		</td>
		<td>
				<xsl:value-of select="@department"/>
		</td>
	</tr>
	<tr>
		<td>
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_job_module"/>
				<xsl:with-param name ="name"  select="'JM_POSITION'"/>
			</xsl:call-template>
			<xsl:value-of select="java:getString($dict_job_module, 'JM_POSITION')"/>
		</td>
		<td>
			<xsl:value-of select="@position"/>
		</td>
	</tr>
	<tr>
		<td>
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_job_module"/>
				<xsl:with-param name ="name"  select="'JM_SALARY_S'"/>
			</xsl:call-template>
			<xsl:value-of select="java:getString($dict_job_module, 'JM_SALARY_S')"/>
		</td>
		<td>
			<xsl:value-of select="@salary"/>
		</td>
	</tr>
	<tr>
		<td>
			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_job_module"/>
				<xsl:with-param name ="name"  select="'JM_DESCRIPTION'"/>
			</xsl:call-template>
			<xsl:value-of select="java:getString($dict_job_module, 'JM_DESCRIPTION')"/>
		</td>
		<td>
			<xsl:value-of select="text()" disable-output-escaping="yes"/>
		</td>
	</tr>
</xsl:template>

<xsl:template name="job_search_bar">
<xsl:param name="mode"></xsl:param>
	<input class="submit" type="button">
		<xsl:call-template name ="add-constant-info">
			<xsl:with-param name ="dict"  select="$dict_job_module"/>
			<xsl:with-param name ="name"  select="'JM_SEARCH'"/>
		</xsl:call-template>
		<xsl:attribute name="value">
			<xsl:value-of select="java:getString($dict_job_module, 'JM_SEARCH')"/>&#160;&gt;&gt;
		</xsl:attribute>
		<xsl:choose>
			<xsl:when test="$mode='search'">
				<xsl:attribute name="onClick">doSearch(); return false;</xsl:attribute>
			</xsl:when>
			<xsl:otherwise>
				<xsl:attribute name="onClick">goToSearch(); return false;</xsl:attribute>
			</xsl:otherwise>
		</xsl:choose>
	</input>
</xsl:template>

<xsl:template name="job_backlink_bar">
	<xsl:param name="backLink" select='"javascript:history.back();"' />
		<input type="button" class="submit">
			<xsl:attribute name="onClick"><xsl:value-of select="$backLink" /></xsl:attribute>
			<xsl:attribute name="value">
				<xsl:value-of select="java:getString($dict_job_module, 'JM_BACK')"/>
			</xsl:attribute>

			<xsl:call-template name ="add-constant-info">
				<xsl:with-param name ="dict"  select="$dict_job_module"/>
				<xsl:with-param name ="name"  select="'JM_BACK'"/>
			</xsl:call-template>
		</input>
</xsl:template>

<!-- LONG GRAY BLOCK: begin -->
<xsl:template name="job_page_title">
	<xsl:param name="mode"></xsl:param>
	<xsl:param name="title">&#160;</xsl:param>
	<h1>
					<xsl:if test="$outputType = 'admin' and not(/negeso:page/@role-id = 'visitor')">
						<img src="/images/mark_1.gif" class="hand" onclick="window.open('j_module', '_blank', 'top=50,left=50,height=600,width=800,status=0,toolbar=0,menubar=0,location=0,resizable=1,scrollbars=yes')"/>
						<xsl:text>&#160;</xsl:text>
					</xsl:if>
					<span>
						<xsl:call-template name="job_title">
                <xsl:with-param name="mode"><xsl:value-of select="$mode"/></xsl:with-param>
						</xsl:call-template>
					</span>

	</h1>
</xsl:template>
<!-- LONG GRAY BLOCK: end -->

<!-- WHERE AM I: BEGIN -->
<!-- Integrates into standard "Where Am I" -->
<xsl:template match="negeso:job_module_component" mode="where_am_i">
	<xsl:choose>
		<xsl:when test="@mode='general'">
			<div>
				<xsl:attribute name="class">here_am_i</xsl:attribute>
				<xsl:value-of select="//negeso:page/negeso:title" />
			</div>
		</xsl:when>
		<xsl:otherwise>
			<div>
				<a>
					<xsl:attribute name="href"><xsl:value-of select="//negeso:page/negeso:filename"/></xsl:attribute>
					<xsl:value-of select="//negeso:page/negeso:title" />
				</a>
				&#160;&#160;&gt;&gt;&gt;&#160;&#160;
			</div>
			<div class="here_am_i">
				<xsl:call-template name="job_title">
					<xsl:with-param name="mode"><xsl:value-of select="//negeso:job_module_component/@mode"/></xsl:with-param>
				</xsl:call-template>
			</div>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>
<!-- WHERE AM I: END -->

</xsl:stylesheet>
