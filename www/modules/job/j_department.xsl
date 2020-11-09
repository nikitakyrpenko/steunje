<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$       
 
  Copyright (c) 2005 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  View of a job module department.
 
  @created    2005/03/23
  @version    $Revision$
  @author     Volodymyr Snigur
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
    <xsl:variable name="dict_product" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_product.xsl', $lang)"/>

    <!-- MAIN ENTRY -->
    <xsl:template match="/negeso:page">
        <html>
            <head>
                <title>
                    <xsl:value-of select="java:getString($dict_job_module, 'DEPARTMENT')"/>
                </title>
                <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
                <xsl:call-template name="java-script"/>                
                <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
            </head>
            <body>
                <!-- NEGESO BODY-->
                <xsl:call-template name="NegesoBody">
                    <xsl:with-param name="helpLink" select="''"/>
                    <xsl:with-param name="backLink">j_module</xsl:with-param>
                </xsl:call-template>
                <xsl:call-template name="buttons"> </xsl:call-template>    
                
            </body>
        </html>
    </xsl:template>
  
    <!-- NEGESO JAVASCRIPT Temaplate -->
    <xsl:template name="java-script">
        <script type="text/javascript" src="/script/jquery.min.js"></script>
		<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
        <script type="text/javascript" src="/script/cufon-yui.js"></script>
        <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
        <script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"/>
        
        <script language="JavaScript">
            <xsl:text disable-output-escaping="yes">
	    <![CDATA[
		function update(targetId) {
            if ( !validateForm(operateForm) ){
                return false;
            }
        	return true;
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
    </xsl:template>
    
    
    <!--******************** DEPARTMENT ADD/EDIT ********************-->
    <xsl:template match="negeso:page"  mode="admContent">
        <xsl:apply-templates select="negeso:department"  mode="departments"/>
    </xsl:template>
    <xsl:template match="negeso:department"  mode="departments">
        <form method="post" name="operateForm" action="" onsubmit="return validateForm(operateForm)">
            <input type="hidden" name="command" value=""></input>
            <input type="hidden" name="department_id" value="{@id}"></input>
            <input type="hidden" name="action" value="save_department"></input>

            <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
                <tr>
                    <td align="center" class="admNavPanelFont"  colspan="6">
                        <!-- TITLE -->
                        <xsl:call-template name="tableTitle">
                            <xsl:with-param name="headtext">
                                <xsl:choose>
                                    <xsl:when test="negeso:department/@id">
                                        <xsl:value-of select="java:getString($dict_job_module, 'DEPARTMENT')"/>. <xsl:value-of select="negeso:department/@title"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="java:getString($dict_job_module, 'DEPARTMENT')"/>. <xsl:value-of select="java:getString($dict_job_module, 'NEW_DEPARTMENT')"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:with-param>
                        </xsl:call-template>
                    </td>
                </tr>
                <tr>
                    <td class="admTableTDLast" id="admTableTDtext">
                        <xsl:value-of select="java:getString($dict_common, 'TITLE')"/>*
                    </td>
                    <td class="admTableTDLast" id="admTableTDtext" colspan="2" >
                        <input class="admTextArea" type="text" name="title" data_type="text" required="true" uname="Title">
                            <xsl:attribute name="value">
                                <xsl:value-of select="@title"/>
                            </xsl:attribute>
                        </input>
                    </td>
                </tr>
                <tr>
                    <td class="admTableTDLast" id="admTableTDtext" >
                        <xsl:value-of select="java:getString($dict_common, 'DESCRIPTION')"/>:
                    </td>
                    <td class="admTableTDLast" id="admTableTDtext" >
                        <textarea class="admTextArea" rows="3"  type="text" name="description" data_type="text" uname="Description">
                            <xsl:if test="count(@description)=0">&#160;</xsl:if>
                            <xsl:value-of select="@description"/>
                        </textarea>
                    </td>
                    <td class="admTableTDLast" id="admTableTDtext" style="vertical-align:top;"  valign="top">
                        <xsl:apply-templates select="negeso:languages" />
                    </td>
                </tr>
                <tr>
                    <td class="admTableTDLast" id="admTableTDtext" >
                        <xsl:value-of select="java:getString($dict_common, 'EMAIL')"/>*
                    </td>
                    <td class="admTableTDLast" id="admTableTDtext" colspan="2">
                        <input class="admTextArea" type="text" name="email" data_type="email" required="true" uname="Email">
                            <xsl:attribute name="value">
                                <xsl:value-of select="@email"/>
                            </xsl:attribute>
                        </input>
                    </td>

                </tr>
                <tr>
                    <td class="admTableFooter" >&#160;</td>
                </tr>
            </table>
        </form>
    </xsl:template>
    
    <!--******************** Bottom buttons ********************-->
    <xsl:template name="buttons">
        <table  cellpadding="0" cellspacing="0"  width="764px"  align="center"  border="0" class="Buttons">
            <tr >
                <td>
                    <xsl:choose>
                        <xsl:when test="/negeso:page/negeso:department/@new='true'">

                            <div class="admBtnGreenb">
                                <div class="imgL"></div>
                                <div>
                                    <input class="admNavbarInp" name="saveButton" type="submit">
                                        <xsl:attribute name="value">
                                            <xsl:value-of select="java:getString($dict_common, 'ADD')"/>
                                        </xsl:attribute>
                                    </input>
                                    &#160;
                                </div>
                                <div class="imgR"></div>
                            </div>
                        </xsl:when>

                        <xsl:otherwise>
                            <div class="admBtnGreenb">
                                <div class="imgL"></div>
                                <div>
                                    <a class="admBtnText" focus="blur()" onClick="save()" href="#save();">
                                   
                                            <xsl:value-of select="java:getString($dict_common, 'SAVE')"/>
                                        
                                    </a>
                                </div>
                                <div class="imgR"></div>
                            </div>

                        </xsl:otherwise>
                    </xsl:choose>

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
  

    <xsl:template match="negeso:languages">
        <fieldset>
            <legend>
                <xsl:value-of select="java:getString($dict_product, 'SHOW_IN_LANGUAGES')"/>:
            </legend>
            <table cellpadding="0" cellspacing="0" border="0" >
                <xsl:apply-templates select="negeso:language"/>
            </table>
        </fieldset>
    </xsl:template>

    <xsl:template match="negeso:language">
        <tr>
            <td>
                &#160; <xsl:value-of select="@lang-code"/> :
            </td>
            <td  width="30%">
                <input type="checkbox" name="{@lang-id}_lang2presence">
                    <xsl:if test="@value='true'">
                        <xsl:attribute name="checked">true</xsl:attribute>
                    </xsl:if>
                </input>
            </td>
        </tr>
    </xsl:template>




</xsl:stylesheet>

