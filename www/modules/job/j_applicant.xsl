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

<!-- NEGESO JAVASCRIPT Temaplate -->	
<xsl:template name="java-script">
    <script type="text/javascript" src="/script/jquery.min.js"></script>
    <script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
    <script language="JavaScript" src="/script/common_functions.js" type="text/javascript">/**/</script>
    
    <script language="JavaScript">        
		function hire() {
            document.operateForm.new_status.value = "hired";
            return true;
		}

		function reject() {
            document.operateForm.new_status.value = "rejected";
            return true;
		}

		function deletee() {
            document.operateForm.new_status.value = "delete";
            return true;
		}
    </script>
</xsl:template>

<!-- MAIN ENTRY -->
<xsl:template match="/negeso:page">
    <html>
        <head>
           <title>Job module - departments</title>
           <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
           <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
           <xsl:call-template name="java-script"/>
        </head>
        <body>
            <!-- NEGESO BODY -->
            <xsl:call-template name="NegesoBody">
                <xsl:with-param name="helpLink" select="''"/>
                <xsl:with-param name="backLink">
                    <xsl:text>?action=department_vacancies&amp;department_id=</xsl:text>
                    <xsl:value-of select="negeso:application/@department_id"/>
                </xsl:with-param>
            </xsl:call-template>
            
            <!--<xsl:call-template name="NegesoHeader">
                <xsl:with-param name="helpLink" select="''"/>
            </xsl:call-template>
            <div align="center">            
                <xsl:call-template name="content_part" />
            </div>-->
        </body>
    </html>
</xsl:template>

<xsl:template match="negeso:page"  mode="admContent">
    <xsl:apply-templates select="negeso:application"/>
</xsl:template>

<xsl:template match="negeso:application">
    <form method="post" name="operateForm" action="">
        <input type="hidden" name="command" value=""></input>
        <input type="hidden" name="dva_id" value="{@dva_id}"></input>
        <input type="hidden" name="new_status" value=""></input>
        <input type="hidden" name="action" value="set_applicant_status"></input>
		<!-- Update/reset fields -->
        <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
            <tr>
                <td class="admNavPanelFont" colspan="2" align="center">
                    <xsl:value-of select="java:getString($dict_job_module, 'APPLICANT_DETAILS')"/>
                </td>
            </tr>
            <tr>
                <th class="admTableTDLast" width="50%">
                    <b>
                        <xsl:value-of select="java:getString($dict_job_module, 'POSTED')"/>: <xsl:value-of select="@post_date"/>,&#160;
                        <xsl:value-of select="java:getString($dict_job_module, 'STATUS')"/>: <xsl:value-of select="@status"/>
                    </b>
                </th>
                <td class="admTableTDLast">
                    <div class="admNavPanelInp">
                        <div class="imgL"></div>
                        <div>
                            <input class="admNavPanelInp" name="saveButton" type="submit" onClick="hire()">
                                <xsl:attribute name="value">
                                    <xsl:value-of select="java:getString($dict_job_module, 'HIRE')"/>
                                </xsl:attribute>
                            </input>
                        </div>
                        <div class="imgR"></div>
                    </div>

                    <div class="admNavPanelInp">
                        <div class="imgL"></div>
                        <div>
                            <input name="saveAndCloseButton" class="admNavPanelInp" type="submit" onClick="reject()">
                                <xsl:attribute name="value">
                                    <xsl:value-of select="java:getString($dict_job_module, 'REJECT')"/>
                                </xsl:attribute>
                            </input>
                        </div>
                        <div class="imgR"></div>
                    </div>

                    <div class="admNavPanelInp">
                        <div class="imgL"></div>
                        <div>
                            <input class="admNavPanelInp" name="saveButton" type="submit" onClick="deletee()">
                                <xsl:attribute name="value">
                                    <xsl:value-of select="java:getString($dict_common, 'DELETE')"/>
                                </xsl:attribute>
                            </input>
                        </div>
                        <div class="imgR"></div>
                    </div>
                    
                </td>
            </tr>
			<xsl:if test="@first_name">
				<tr>
					<th class="admTableTD">
						<xsl:value-of select="java:getString($dict_common, 'FIRST_NAME')"/>
					</th>
					<td class="admTableTDLast">
						<input class="admTextArea admWidth200" type="text" disabled="true" value="{@first_name}"/>
					</td>
				</tr>
			</xsl:if>
			<xsl:if test="@second_name">
				<tr>
					<th class="admTableTD">
						<xsl:value-of select="java:getString($dict_common, 'SECOND_NAME')"/>
					</th>
					<td class="admTableTDLast">
						<input class="admTextArea admWidth200" type="text" disabled="true" value="{@second_name}"/>
					</td>
				</tr>
			</xsl:if>
			<xsl:if test="@email">
				<tr>
					<th class="admTableTD">
						<xsl:value-of select="java:getString($dict_common, 'EMAIL')"/>
					</th>
					<td class="admTableTDLast">
						<input class="admTextArea admWidth200" type="text" disabled="true" value="{@email}"/>
					</td>
				</tr>
			</xsl:if>
			<xsl:if test="@address_line">
				<tr>
					<th class="admTableTD">
						<xsl:value-of select="java:getString($dict_common, 'ADDRESS_LINE')"/>
					</th>
					<td class="admTableTDLast">
						<input class="admTextArea admWidth200" type="text" disabled="true" value="{@address_line}"/>
					</td>
				</tr>
			</xsl:if>
			<xsl:if test="@phone">
				<tr>
					<th class="admTableTD">
						<xsl:value-of select="java:getString($dict_common, 'PHONE')"/>
					</th>
					<td class="admTableTDLast">
						<input class="admTextArea admWidth200" type="text" disabled="true" value="{@phone}"/>
					</td>
				</tr>
			</xsl:if>
			<xsl:if test="@mobile">
				<tr>
					<th class="admTableTD">
						<xsl:value-of select="java:getString($dict_job_module, 'MOBILE')"/>
					</th>
					<td class="admTableTDLast">
						<input class="admTextArea admWidth200" type="text" disabled="true" value="{@mobile}"/>
					</td>
				</tr>
			</xsl:if>
			<xsl:if test="@birthday">
				<tr>
					<th class="admTableTD">
						<xsl:value-of select="java:getString($dict_job_module, 'BIRTHDAY')"/>
					</th>
					<td class="admTableTDLast">
						<input class="admTextArea admWidth200" type="text" disabled="true" value="{@birthday}"/>
					</td>
				</tr>
			</xsl:if>
			<xsl:if test="@birthplace">
				<tr>
					<th class="admTableTD">
						<xsl:value-of select="java:getString($dict_job_module, 'BIRTHPLACE')"/>
					</th>
					<td class="admTableTDLast">
						<input class="admTextArea admWidth200" type="text" disabled="true" value="{@birthplace}"/>
					</td>
				</tr>
			</xsl:if>
			<xsl:if test="@citizenship">
				<tr>
					<th class="admTableTD">
						<xsl:value-of select="java:getString($dict_job_module, 'CITIZENSHIP')"/>
					</th>
					<td class="admTableTDLast">
						<input class="admTextArea admWidth200" type="text" disabled="true" value="{@citizenship}"/>
					</td>
				</tr>
			</xsl:if>
			<xsl:if test="@cv_flie">
				<tr>
					<th class="admTableTD">
						<xsl:value-of select="java:getString($dict_job_module, 'APPLICANT_CV')"/>
					</th>
					<td class="admTableTDLast">
						<a class="admAnchor" target="_blank" href="{@cv_flie}">
							<xsl:value-of select="java:getString($dict_job_module, 'APPLICANT_CV')"/>
						</a>
					</td>
				</tr>
			</xsl:if>
			<xsl:apply-templates select="negeso:field"/>          
            <tr>
                <td class="admTableFooter" colspan="2">&#160;</td>
            </tr>
	   </table>
	</form>	
</xsl:template>

<xsl:template match="negeso:field">
	<xsl:if test="@value">
		<tr>
			<th class="admTableTD">
				<xsl:value-of select="@title"/>
			</th>
			<td class="admTableTDLast">
				<input class="admTextArea admWidth200" type="text" disabled="true" value="{@value}"/>
			</td>
		</tr>
	</xsl:if>
</xsl:template>

<xsl:template match="negeso:field[@type_name='file']">
	<tr>
		<th class="admTableTD">
			<xsl:value-of select="@title"/>
		</th>
		<td class="admTableTDLast">
			<a class="admAnchor" target="_blank" href="{@value}">
				<xsl:value-of select="@value"/>
			</a>
		</td>
	</tr>
</xsl:template>


<xsl:template match="negeso:field[@type_name='radio_box']">
	<tr>
		<th class="admTableTD">
			<xsl:value-of select="@title"/>
		</th>
		<td class="admTableTDLast">
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
		<th class="admTableTD">
			<xsl:value-of select="@title"/>
		</th>
		<td class="admTableTDLast">
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
		<th class="admTableTD">
			<xsl:value-of select="@title"/>
		</th>
		<td class="admTableTDLast">
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
