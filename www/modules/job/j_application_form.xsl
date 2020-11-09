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
  @author     
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

<xsl:variable name="is_general_form" select="/negeso:page/@is_general_form"/>

<!-- NEGESO JAVASCRIPT Temaplate -->	
<xsl:template name="java-script">
    <script type="text/javascript" src="/script/jquery.min.js"></script>    
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
    <script language="JavaScript" src="/script/common_functions.js" type="text/javascript">/**/</script>

    <script language="JavaScript">
	    var s_DeleteFieldConfirmation = "<xsl:value-of select="java:getString($dict_job_module, 'DELETE_FIELD_CONFIRMATION')"/>";

	<xsl:text disable-output-escaping="yes">
	    <![CDATA[

        function deleteField(targetId) {
            if (confirm(s_DeleteFieldConfirmation)) {
		        document.operateForm.action.value = "application_form_delete_field";
		        document.operateForm.field_id.value = targetId;
	            document.operateForm.submit();
		        return true;
	        }
	        return false;
        }

        function addField() {
			strPage = "field_repository?action=select_field";
			strAttr = "resizable:on;scroll:on;status:off;dialogWidth:800px;dialogHeight:576px;help:on";
			result = showModalDialog(strPage, null, strAttr);
			if ( result != null ){
				if (result.resCode == "OK"){
		            document.operateForm.action.value = "application_form_add_field";
		            document.operateForm.field_type_id.value=result.typeId;
                    document.operateForm.submit(); 
		            return true;
				}
				else{
	 	       		alert("Error in type chooser, rechoose please");
	 	       	}
	 	    }
	 	    return false;
        }

        function save() {
            document.operateForm.action.value = "save_application_form";
            document.operateForm.submit();
        }
		
        function editVacancy() {
            document.operateForm.action.value = "vacancy_details";
            document.operateForm.submit(); 
        }
        
        
        function moveField(targetId, direction) {
	        document.operateForm.action.value = "application_form_move_field";
	        document.operateForm.field_id.value = targetId;
	        document.operateForm.direction.value = direction;
            document.operateForm.submit();
	        return true;
        }
        

		]]>
		</xsl:text>
	</script>
</xsl:template>

<!-- MAIN ENTRY -->
<xsl:template match="/negeso:page">
    <html>
        <head>
           <title>Job module</title>
            <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
            <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
            <xsl:call-template name="java-script"/>
        </head>
        <body>
            <!-- NEGESO HEADER -->
           <xsl:call-template name="NegesoBody">
                <xsl:with-param name="helpLink" select="''"/>
                <xsl:with-param name="backLink">
                    <xsl:choose>
                        <xsl:when test="$is_general_form='true'">
                            <xsl:text>j_module</xsl:text>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text>?action=department_vacancies&amp;department_id=</xsl:text>
                            <xsl:value-of select="@department_id"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="buttons"> </xsl:call-template>
        </body>
    </html>
</xsl:template>


    <xsl:template match="negeso:page"  mode="admContent">
        <xsl:apply-templates select="negeso:application_form" mode="application_forms"/>
    </xsl:template>
    <xsl:template match="negeso:application_form"  mode="application_forms">    
     
    <!-- Content -->
  
	<div>
    <form method="post" name="operateForm" action="" enctype="multipart/form-data">
        <input type="hidden" name="command" value=""></input>
        <input type="hidden" name="vacancy_id" value="{@vacancy_id}"></input>
        <input type="hidden" name="action" value=""></input>
        <input type="hidden" name="field_id" value=""></input>
        <input type="hidden" name="field_type_id" value=""></input>
        <input type="hidden" name="direction" value=""></input>
        
		<!-- Update/reset fields -->
        <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
            <tr>
                <td align="center" class="admNavPanelFont"  colspan="6">
                    <!-- TITLE -->
                    <xsl:call-template name="tableTitle">
                        <xsl:with-param name="headtext">
                            <xsl:value-of select="java:getString($dict_job_module, 'APPLICATION_FORM')"/>
                        </xsl:with-param>
                    </xsl:call-template>
                </td>
            </tr>
            <tr>
                <td colspan="4" class="admNavPanel">
                   
                            <xsl:if test="$is_general_form!='true'">
                                <div class="admNavPanelInp">
                                    <div class="imgL"></div>
                                    <div>
                                        <a class="admNavPanelInp" focus="blur()" onClick="editVacancy()" href="#editVacancy();">
                                
                                   <xsl:value-of select="java:getString($dict_job_module, 'EDIT_VACANCY')"/>
                             
                                </a>
                         
                            
                        </div>
                        <div class="imgR"></div>
                    </div>
                            </xsl:if>
                    

                    <div class="admNavPanelInp">
                        <div class="imgL"></div>
                        <div>

                            <a class="admNavPanelInp" focus="blur()" onClick="addField()" href="#addField(); ">
                                                           <xsl:value-of select="java:getString($dict_job_module, 'ADD_FIELD')"/>
                                
                            </a>
                        </div>
                        <div class="imgR"></div>
                    </div>
				</td>
			</tr>

            <tr>
                <td>
                    <xsl:apply-templates select="negeso:field"/>
                </td>
            </tr>
            <tr>
                <td class="admTableFooter" >&#160;</td>
            </tr>
		</table>      	
		<!-- Update/reset fields -->
    
	</form>
	</div>
</xsl:template>

<xsl:template match="negeso:field">
	<tr>
		<td  class="admTableTD" id="admTableTDtext">
			<xsl:value-of select="@title"/>
		</td>
		<td  class="admTableTD" id="admTableTDtext">
			<input class="admTextArea admWidth200" type="text" disabled="true">
				<xsl:attribute name="value"><xsl:value-of select="java:getString($dict_job_module, 'SAMPLE_TEXT')"/></xsl:attribute>
			</input>
		</td>
		<xsl:call-template name="render_controlTD"/>
	</tr>
</xsl:template>

<xsl:template name="render_controlTD">
		<td  class="admTableTD" id="admTableTDtext">
			<input type="checkbox" name="is_required" value="{@id}">
				<xsl:if test="@is_required='true'">
					<xsl:attribute name="CHECKED">true</xsl:attribute>
				</xsl:if>
				<!--xsl:if test="$is_general_form='false' and @sys_name">
					<xsl:attribute name="disabled">true</xsl:attribute>
				</xsl:if-->
				<xsl:value-of select="java:getString($dict_job_module, 'IS_REQUIRED')"/>
			</input>
        </td>
		<td class="admTableTDLast" style="width:115px;cursor:pointer;">
	        <img src="/images/up.png" class="admHand" onClick="moveField({@id}, 'up')">
    	        <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'UP')"/></xsl:attribute>
            </img>
        
            <img src="/images/down.png" class="admHand" onClick="moveField({@id}, 'down')">
                <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'DOWN')"/></xsl:attribute>
            </img>
        
            <img src="/images/delete.png" class="admHand" onClick="deleteField({@id})">
                <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'DELETE')"/></xsl:attribute>
            </img>
        </td>
</xsl:template>


    <!--******************** Bottom buttons ********************-->
    <xsl:template name="buttons">
        <table  cellpadding="0" cellspacing="0"  width="764px"  align="center"  border="0" class="Buttons">
            <tr >
                <td>
                    
                            <div class="admBtnGreenb">
                                <div class="imgL"></div>
                                <div>
                                    <input name="saveButton" type="submit" onClick="save()">
                                        <xsl:attribute name="value">
                                            <xsl:value-of select="java:getString($dict_common, 'SAVE')"/>
                                        </xsl:attribute>
                                    </input>
                                </div>
                                <div class="imgR"></div>
                            </div>

                </td>
            </tr>
        </table>
    </xsl:template>



    <xsl:template match="negeso:field[@type_name='file']">
	<tr>
		<td  class="admTableTD" id="admTableTDtext">
			<xsl:value-of select="@title"/>
		</td>
		<td  class="admTableTD" id="admTableTDtext">
			<input class="admTextArea admWidth200" type="file" disabled="true" onKeyPress="return false;"></input>
		</td>
		<xsl:call-template name="render_controlTD"/>
	</tr>
</xsl:template>

<xsl:template match="negeso:field[@type_name='radio_box']">
	<tr>
		<td  class="admTableTD" id="admTableTDtext">
			<xsl:value-of select="@title"/>
		</td>
		<td  class="admTableTD" id="admTableTDtext">
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
		<xsl:call-template name="render_controlTD"/>
	</tr>
</xsl:template>

<xsl:template match="negeso:field[@type_name='check_box']">
	<tr>
		<td  class="admTableTD" id="admTableTDtext">
			<xsl:value-of select="@title"/>
		</td>
		<td  class="admTableTD" id="admTableTDtext">
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
		<xsl:call-template name="render_controlTD"/>
	</tr>
</xsl:template>

<xsl:template match="negeso:field[@type_name='select_box']">
	<tr>
		<td class="admTableTD" id="admTableTDtext">
			<xsl:value-of select="@title"/>
		</td>
		<td  class="admTableTD" id="admTableTDtext">
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
		<xsl:call-template name="render_controlTD"/>
	</tr>
</xsl:template>
</xsl:stylesheet>