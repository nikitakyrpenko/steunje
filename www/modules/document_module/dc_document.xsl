<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$       
 
  Copyright (c) 2005 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  View of a job module department.
 
  @created    2005/04/08
  @version    $Revision$

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
<xsl:variable name="dict_document_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_document_module.xsl', $lang)"/>

<!-- MAIN ENTRY -->
<xsl:template match="/negeso:page">
    <html>
        <head>
			<title><xsl:value-of select="java:getString($dict_document_module, 'DOCUMENT_MODULE')"/></title>
            <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>

            <script type="text/javascript" src="/script/jquery.min.js" />
			<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
            <xsl:call-template name="java-script"/>
            <script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"/>
            <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
        </head>
        <body>
            <form method="post" name="operateForm" action="dc-update-document" onsubmit="return validateForm(operateForm)">
                <xsl:call-template name="NegesoBody">
                    <xsl:with-param name="backLink">
                        <xsl:if test="(count(descendant::negeso:page) = 0) and not(negeso:category/@is_current='true') ">
                            <xsl:value-of select="concat('document_module?category_id=', descendant::negeso:category[@is_current='true']/@parent_id)"/>
                        </xsl:if>
                    </xsl:with-param>                    
                </xsl:call-template>
                <xsl:apply-templates select="descendant::negeso:document" mode="buttons"/>
            </form>
        </body>
    </html>
</xsl:template>

<!-- NEGESO JAVASCRIPT Template -->	
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

		]]>
		</xsl:text>
	</script>
</xsl:template>


<xsl:template match="negeso:page" mode="admContent">
    <!-- PATH -->
	<xsl:call-template name="dc.path"/>            
    <!-- Content -->
    <xsl:apply-templates select="descendant::negeso:document"/>
</xsl:template>


<!--******************** DEPARTMENT ADD/EDIT ********************-->
<xsl:template match="negeso:document[@can_edit='true']">    
    <input type="hidden" name="command" value="dc-update-document"></input>
    <input type="hidden" name="document_id" value="{@id}"></input>

    <table align="center" border="0" cellpadding="0" cellspacing="0" class="admTable">
        <tr>
            <td align="center" class="admNavPanelFont" colspan="2">
                <!-- TITLE -->
                <xsl:call-template name="tableTitle">
                    <xsl:with-param name="headtext">
                        <xsl:value-of select="java:getString($dict_common, 'DOCUMENT')"/>
                        <xsl:if test="descendant::negeso:document/@can_edit='false'">
                            (<xsl:value-of select="java:getString($dict_document_module, 'READONLY')"/>)
                        </xsl:if>
                    </xsl:with-param>
                </xsl:call-template>
            </td>
        </tr>
        <tr>
            <th class="admTableTD admWidth150">
                <xsl:value-of select="java:getString($dict_common, 'TITLE')"/>*
            </th>
            <td class="admTableTDLast">
                <input class="admTextArea admWidth335" type="text" name="name" data_type="text" required="true" uname="Title">
                    <xsl:attribute name="value">
                        <xsl:value-of select="@name"/>
                    </xsl:attribute>
                </input>
            </td>
        </tr>
        <tr>
            <th class="admTableTD">
                <xsl:value-of select="java:getString($dict_common, 'DOCUMENT')"/>
            </th>
            <td class="admTableTDLast">                
                <a href="{@document_link}" target="_blank_">
                    <xsl:value-of select="@document_link"/>
                </a>
                <input type="hidden" name="link" value="{@document_link}"/>
            </td>
        </tr>
        <tr>
            <th class="admTableTD">
                <xsl:value-of select="java:getString($dict_common, 'DESCRIPTION')"/>:
            </th>
            <td class="admTableTDLast">
                <textarea rows="3" class="admTextArea admWidth335" type="text" name="description" data_type="text" uname="Description">
                    <xsl:if test="count(@description)=0">&#160;</xsl:if>
                    <xsl:value-of select="@description"/>
                </textarea>
            </td>
        </tr>
        <tr>
            <th class="admTableTD">
                <xsl:value-of select="java:getString($dict_document_module, 'OWNER')"/>
            </th>
            <td class="admTableTDLast">
                <input class="admTextArea admWidth335" type="text" name="owner" data_type="text" required="true" uname="Owner">
                    <xsl:attribute name="value">
                        <xsl:value-of select="@owner"/>
                    </xsl:attribute>
                </input>
            </td>
        </tr>
        <tr>
            <th class="admTableTD">
                <xsl:value-of select="java:getString($dict_document_module, 'LAST_MODIFIED')"/>
            </th>
            <td class="admTableTDLast">
                &#160;<xsl:value-of select="@last_modified"/>
            </td>
        </tr>
        <tr>
            <td class="admTableFooter" colspan="2">&#160;</td>
        </tr>
    </table>
</xsl:template>
    
<xsl:template match="negeso:document" mode="buttons">        
    <table cellpadding="0" cellspacing="0"  width="764px" align="center" class="Buttons">       
        <tr>
            <td>
                <xsl:choose>
                    <xsl:when test="/negeso:page/negeso:department/@new='true'">
                        <div class="admBtnGreenb">
                            <div class="imgL"></div>
                            <div>
                                <input class="admNavbarInp" type="submit">
                                    <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'ADD')"/></xsl:attribute>
                                </input>
                            </div>
                            <div class="imgR"></div>
                        </div>
                    </xsl:when>
                    <xsl:otherwise>
                        <div class="admBtnGreenb">
                            <div class="imgL"></div>
                            <div>
                                <input class="admNavbarInp" type="submit" onClick="addCategory({@id})">
                                    <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'SAVE')"/></xsl:attribute>
                                </input>
                            </div>
                            <div class="imgR"></div>
                        </div>                        
                    </xsl:otherwise>
                </xsl:choose>
                <div class="admBtnGreenb admBtnBlue">
                    <div class="imgL"></div>
                    <div>
                        <input class="admNavbarInp" type="reset">
                            <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'RESET')"/></xsl:attribute>
                        </input>
                    </div>
                    <div class="imgR"></div>
                </div>                
            </td>
        </tr>
    </table>        
</xsl:template>    


<xsl:template match="negeso:document[@can_edit='false']">
    <table class="admNavPanel" cellspacing="0" cellpadding="0">
        <tr>
            <td>
				<table class="admNavPanel" cellspacing="0" cellpadding="0">
    				<tr>
	    				<td class="admMainTD admWidth150"><xsl:value-of select="java:getString($dict_common, 'TITLE')"/>*</td>
		    			<td class="admLightTD admLeft"><xsl:value-of select="@name"/></td>
	    			</tr>
    				<tr>
	    				<td class="admMainTD admWidth150"><xsl:value-of select="java:getString($dict_common, 'DOCUMENT')"/></td>
		    			<td class="admLightTD admLeft">
		    				<a href = "{@document_link}" target="_blank_">
		    					<xsl:value-of select="@document_link"/>
		    				</a>
    					</td>
	    			</tr>
			    	<tr>
    					<td class="admMainTD"><xsl:value-of select="java:getString($dict_common, 'DESCRIPTION')"/>:</td>
		        		<td class="admLightTD admLeft">
					       <xsl:value-of select="@description"/>
    					</td>
    				</tr>
    				<tr>
                        <td class="admMainTD admWidth150"><xsl:value-of select="java:getString($dict_document_module, 'OWNER')"/></td>
                        <td class="admLightTD admLeft"><xsl:value-of select="@owner"/></td>
                    </tr>
    				<tr>
                        <td class="admMainTD admWidth150"><xsl:value-of select="java:getString($dict_document_module, 'LAST_MODIFIED')"/></td>
                        <td class="admLightTD admLeft">
                             <xsl:value-of select="@last_modified"/>
                        </td>
                    </tr>
				</table>
            </td>
        </tr>
    </table>
</xsl:template>

<xsl:template name="dc.path">
    <!-- Path -->
    <table cellpadding="0" cellspacing="0" class="admNavPanel admSpaceSmall">
	   	<tr>
            <td class="admBold">
            	<xsl:apply-templates select="negeso:category" mode="path"/>
            </td>
        </tr>
    </table>
</xsl:template>

<xsl:template match="negeso:category" mode="path">
	<xsl:choose>
	<xsl:when test="@is_current">
		<!-- Link path element - just print it-->
		<span class="admSecurity admLocation">
		    <a class="admLocation" href="document_module?category_id={@id}">
				<xsl:value-of select="@name"/>
		    </a>
		    &#160;&gt;
		</span>

		<span class="admSecurity admLocation">
			<xsl:value-of select="descendant::negeso:document/@name"/>
		</span>
	</xsl:when>
	<xsl:otherwise>
		<!-- Link path element - just print it-->
		<span class="admSecurity admLocation">
		    <a class="admLocation" href="document_module?category_id={@id}">
				<xsl:value-of select="@name"/>
		    </a>
		    &#160;&gt;
		</span>
	   	<xsl:apply-templates select="negeso:category" mode="path"/>
	</xsl:otherwise>
	</xsl:choose>
</xsl:template>

</xsl:stylesheet>
