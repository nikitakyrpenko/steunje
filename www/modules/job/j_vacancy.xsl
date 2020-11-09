<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${j_applicant.xsl}       
 
  Copyright (c) 2004 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  View of a job module applicant.
 
  @version    2005/03/23
  @author     Volodymyr Snigur
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

    <!-- Include/Import -->
    <xsl:include href="/xsl/negeso_body.xsl"/>
    <xsl:include href="/xsl/admin_templates.xsl"/>


    <xsl:variable name="lang" select="/*/@interface-language"/>
    <xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
    <xsl:variable name="dict_job_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_job_module.xsl', $lang)"/>
    <xsl:variable name="job_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('job_module', $lang)"/>
    <xsl:variable name="dict_product" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_product.xsl', $lang)"/>

    <!-- MAIN ENTRY -->
    <xsl:template match="/negeso:page">
        <html>
            <head>
                <title><xsl:value-of select="java:getString($dict_job_module, 'VACANCY')"/></title>
                <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
                <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
                <link rel="stylesheet" type="text/css" href="/css/jquery-ui.custom.css"/>
                
                <script language="JavaScript1.2" src="/script/jquery.min.js" type="text/javascript"/>
				<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
                <script language="JavaScript1.2" src="/script/calendar_picker.js" type="text/javascript"/>
                <script type="text/javascript" src="/script/cufon-yui.js"></script>
                <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
                <script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"/>
                <xsl:call-template name="java-script"/>                
            </head>
            <body
                style="behavior: url(/script/webservice.htc) url(/script/clientmanager.htc)"
                id="ClientManager" xmlID="332"	>

                <!-- NEGESO HEADER -->
                <xsl:call-template name="NegesoBody">
                    <xsl:with-param name="helpLink" select="''"/>
                    <xsl:with-param name="backLink">
                        <xsl:text>?action=department_vacancies&amp;department_id=</xsl:text>
                        <xsl:value-of select="negeso:vacancy/@department_id"/>
                    </xsl:with-param>
                </xsl:call-template>
                <xsl:call-template name="buttons"></xsl:call-template>
                <script>
                    Cufon.now();
                    Cufon.replace('.admNavPanelFont', { fontFamily: 'Orator Std' });
                    Cufon.replace('.admTDtitles', { fontFamily: 'Orator Std' });
                </script>
            </body>
        </html>
    </xsl:template>


    <!-- NEGESO JAVASCRIPT Temaplate -->
    <xsl:template name="java-script">
        <script language="JavaScript">
            <xsl:text disable-output-escaping="yes">
	    <![CDATA[
		function update(targetId) {
            if ( !validateForm(operateForm) ){
                return false;
            }
        	return true;
		}

		function editApplicationForm() {
            document.operateForm.action.value = "application_form";
            document.operateForm.submit();  
		}
 function save(){
            document.operateForm.submit();
        }
   function resetForm(){
   document.operateForm.reset();
 }
		]]>
		</xsl:text>
        </script>
        <xsl:call-template name="adminhead"/>
    </xsl:template>

    <xsl:template match="negeso:page"  mode="admContent">
        <xsl:apply-templates select="negeso:vacancy" mode="vacancy"/>
    </xsl:template>
    <xsl:template match="negeso:vacancy"  mode="vacancy">

        <!-- Content -->
        <form method="post" name="operateForm" action="" onsubmit="return validateForm(operateForm)">
            <input type="hidden" name="command" value=""></input>
            <input type="hidden" name="vacancy_id" value="{@id}"></input>
            <input type="hidden" name="department_id" value="{@department_id}"></input>
            <input type="hidden" name="action" value="save_vacancy"></input>
            <!-- Update/reset fields -->
            <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
                <tr>
                    <td align="center" class="admNavPanelFont"  colspan="6">
                        <!-- TITLE -->
                        <xsl:call-template name="tableTitle">
                            <xsl:with-param name="headtext">
                                <xsl:value-of select="java:getString($dict_job_module, 'VACANCY')"/>. <xsl:value-of select="@title"/>
                            </xsl:with-param>
                        </xsl:call-template>
                    </td>
                </tr>
                <tr>
                    <td class="admNavPanel">
                        <div class="admNavPanelInp">
                            <div class="imgL"></div>
                            <div>
                                <a class="admNavPanelInp" focus="blur()" onClick="editApplicationForm()" href="#editApplicationForm(); ">
                           
                                        <xsl:value-of select="java:getString($dict_job_module, 'EDIT_APPLICATION_FORM')"/>
                                   
                                </a>
                            </div>
                            <div class="imgR"></div>
                        </div>


                    </td>
                </tr>

                <tr>
                    <td>
                        <xsl:apply-templates select="." />
                    </td>
                </tr>
                <tr>
                    <td class="admTableFooter" >&#160;</td>
                </tr>
            </table>
            <!-- Update/reset fields -->

        </form>
      

        <xsl:apply-templates select ="negeso:article" mode="formOutArtilce" />

    </xsl:template>

    <xsl:template match="negeso:article" mode="formOutArtilce">
        <div onpropertychange="pushContent(this)" head="{negeso:head/text()}" id="article_text{@id}" class="contentStyle hiddenArtilce" style="border: 1px solid #848484;">
            <xsl:choose>
                <xsl:when test="negeso:text/text()">
                    <xsl:value-of select="negeso:text" disable-output-escaping="yes"/>
                </xsl:when>
                <xsl:otherwise>&#160;</xsl:otherwise>
            </xsl:choose>
        </div>
        <script>
            pushContent(document.getElementById('article_text<xsl:value-of select ="@id"/>'));
        </script>
    </xsl:template>

    <xsl:template match="negeso:vacancy">
        <table cellspacing="0" cellpadding="0" width="100%">
            <xsl:call-template name="render_properties"/>
        </table>
    </xsl:template>

    <xsl:template name="render_properties">
        <tr>
            <td  class="admTableTDLast" id="admTableTDtext">
                <xsl:value-of select="java:getString($dict_common, 'TITLE')"/>*
            </td>
            <td  colspan="3" class="admTableTDLast" id="admTableTDtext">
                <input class="admTextArea" type="text" name="title" data_type="text" required="true" uname="Title">
                    <xsl:attribute name="value">
                        <xsl:value-of select="@title"/>
                    </xsl:attribute>
                </input>
            </td>
        </tr>
        <tr>
            <td class="admTableTDLast" id="admTableTDtext">
                <xsl:value-of select="java:getString($dict_job_module, 'POSITION')"/>
            </td>
            <td  colspan="3" class="admTableTDLast" id="admTableTDtext">
                <input class="admTextArea" type="text" name="position" data_type="text" uname="Position">
                    <xsl:attribute name="value">
                        <xsl:value-of select="@position"/>
                    </xsl:attribute>
                </input>
            </td>
        </tr>
        <tr>
            <td class="admTableTDLast" id="admTableTDtext">
                <xsl:value-of select="java:getString($dict_job_module, 'SALARY')"/>
            </td>
            <td class="admTableTDLast" id="admTableTDtext" colspan="3">
                <input class="admTextArea" type="text" name="salary">
                    <xsl:attribute name="value">
                        <xsl:value-of select="@salary"/>
                    </xsl:attribute>
                </input>
            </td>
        </tr>
        <tr>
            <td class="admTableTDLast" id="admTableTDtext">
                <xsl:value-of select="java:getString($dict_job_module, 'PERSON_NEEDED')"/>
            </td>
            <td colspan="3" class="admTableTDLast" id="admTableTDtext">
                <input class="admTextArea" type="text" name="person_needed" data_type="number" required="true" uname="Person need">
                    <xsl:attribute name="value">
                        <xsl:value-of select="@person_needed"/>
                    </xsl:attribute>
                </input>
            </td>
        </tr>
        <tr>
            <td class="admTableTDLast" id="admTableTDtext">
                <xsl:value-of select="java:getString($job_module, 'JOB_REGION')"/>
            </td>
            <td colspan="3" class="admTableTDLast" id="admTableTDtext">
                <select name="region_id">
                    <xsl:for-each select="//negeso:job-regions/negeso:region">
                        <option value="{@id}">
                            <xsl:if test="@id=//negeso:vacancy/@region_id">
	                            <xsl:attribute name="selected">true</xsl:attribute>
                            </xsl:if>
                            <xsl:value-of select="@title"/>
                        </option>
                    </xsl:for-each>
                </select>
            </td>
        </tr>

        <xsl:call-template name="view_article" />

        <!--<tr>
   
		<td class="admLightTD" style="vertical-align:top;"  valign="top">
			<xsl:apply-templates select="negeso:languages" />
		</td>
	</tr>-->
        <!-- Publish Date -->
        <tr>
            <td class="admTableTDLast" id="admTableTDtext">
                <xsl:value-of select="java:getString($dict_common, 'PUBLISH_DATE')"/>*
            </td>
            <td  colspan="3" class="admTableTDLast" id="admTableTDtext">
                <input type="text" name="publish_date" id="publishDateFieldId" class="admTextArea" data_type="text" required="true" uname="Publish date" readonly="true">
                    <xsl:attribute name="value">
                        <xsl:value-of select="@publish_date"/>
                    </xsl:attribute>
                </input>
                (dd-mm-yyyy)
            </td>
        </tr>
        <!-- Expired Date -->
        <tr>
            <td class="admTableTDLast" id="admTableTDtext">
                <xsl:value-of select="java:getString($dict_common, 'EXPIRED_DATE')"/>
            </td>
            <td  colspan="3" class="admTableTDLast" id="admTableTDtext">
                <input class="admTextArea" type="text" name="expired_date" id="expiredDateFieldId" data_type="text" uname="Expired date" readonly="true">
                    <xsl:attribute name="value">
                        <xsl:value-of select="@expire_date"/>
                    </xsl:attribute>
                </input>
                (dd-mm-yyyy)
            </td>
        </tr>
    </xsl:template>

    <!-- VIEW ARTICLE -->
    <xsl:template name="view_article">

        <td class="admTableTDLast" id="admTableTDtext" >
            <xsl:if test="@is-i18n='1'">
                (<xsl:value-of select="/negeso:page/@lang"/>)
            </xsl:if>
            <xsl:value-of select="java:getString($dict_common, 'DESCRIPTION')"/>
            <xsl:if test="@is-required='1'">*</xsl:if>
        </td>
        <td class="admTableTDLast" id="admTableTDtext">
            <input type="hidden" name="{@name}_id" value="{@id}"/>
            <xsl:apply-templates select="negeso:article[position() = 1]"/>
        </td>
        <td  class="admTableTDLast" id="admTableTDtext" style="vertical-align:top; text-align:left" colspan="2"  valign="top">
            <table>
                <tr>
                    <td >
                        <xsl:apply-templates select="negeso:languages" />
                    </td>
                </tr>
            </table>
        </td>

    </xsl:template>

    <xsl:template name="show_article">
        <xsl:param name="artId">0</xsl:param>
        <xsl:param name="artName"></xsl:param>
        <xsl:param name="artCount">0</xsl:param>

        <input type="hidden" name="$artName_id" value="$artId"/>
        <xsl:apply-templates select="negeso:article[position() = $artCount]"/>

    </xsl:template>


    <!-- ********************************** Article *********************************** -->
    <xsl:template match="negeso:article" >
        <img src="/images/mark_1.gif" onclick="edit_text('article_text{@id}', 'contentStyle', 595);" alt="Edit a description" class="admBorder admHand"/>
        <div id="article_text{@id}_temp"  class="contentStyle" style="border: 1px solid #CCCCCC; margin:5px;">

        </div>
        <!--<div id="article_text{@id}" class="contentStyle">
 	    <xsl:attribute name="style">behavior:url(/script/article3.htc); margin: 5px;border: 1px solid #848484;</xsl:attribute>
		<xsl:choose>
			<xsl:when test="negeso:text/text()">
				<xsl:value-of select="negeso:text" disable-output-escaping="yes"/>
			</xsl:when>
			<xsl:otherwise>&#160;</xsl:otherwise>
		</xsl:choose>
	</div>-->
    </xsl:template>

    <!--******************** Bottom buttons ********************-->
    <xsl:template name="buttons">
        <table cellpadding="0" cellspacing="0"  width="764px" align="center" class="Buttons">
            <tr>
                <td>

                    <div class="admBtnGreenb">
                        <div class="imgL"></div>
                        <div>
                            <a class="admBtnText" focus="blur()" onClick="save()" href="#save();">
                                
                                    <xsl:value-of select="java:getString($dict_common, 'SAVE')"/>
                             
                            </a>
                        </div>
                        <div class="imgR"></div>
                    </div>

                    <div class="admBtnGreenb admBtnBlueb">
                        <div class="imgL"></div>
                        <div>
                            <input type="button" value="Reset" onClick="resetForm()"/>

                        </div>
                        <div class="imgR"></div>
                    </div>
                </td>
            </tr>
        </table>
    </xsl:template>


    <!--******************** LANGUAGE ********************-->

    <xsl:template match="negeso:languages">
        <fieldset >
            <legend>
                <xsl:value-of select="java:getString($dict_product, 'SHOW_IN_LANGUAGES')"/>:
            </legend>
            <table cellpadding="0" cellspacing="0" border="0">
                <xsl:apply-templates select="negeso:language"/>
            </table>
        </fieldset>
    </xsl:template>

    <xsl:template match="negeso:language">
        <tr>
            <td>
                &#160; <xsl:value-of select="@lang-code"/> :
            </td>
            <td width="30%">
                <input type="checkbox" name="{@lang-id}_lang2presence" id="{@lang-id}_lang2presence">
                    <xsl:if test="@value='true'">
                     <xsl:attribute name="checked">true</xsl:attribute>
                    </xsl:if>
                </input>
            </td>
        </tr>
    </xsl:template>

</xsl:stylesheet>
