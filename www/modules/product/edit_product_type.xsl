<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$

  Copyright (c) 2004 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential
  Information and shall use it only in accordance with the terms of the
  license agreement you entered into with Negeso.

  View of a list item edit interface.

  @version      $Resvision$
  @author       Olexiy.Strashko
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:negeso="http://negeso.com/2003/Framework"
	xmlns:java="http://xml.apache.org/xslt/java"
	exclude-result-prefixes="java">

<xsl:include href="/xsl/negeso_header.xsl"/>

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_product" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_product.xsl', $lang)"/>

<!-- NEGESO JAVASCRIPT Temaplate -->	
<xsl:template name="java-script">
	<script language="JavaScript">
	<xsl:text disable-output-escaping="yes">
	    <![CDATA[
		function Update(targetId) {
            
            if ( !validateForm(operateFormId) ){
                return false;
            }
            
            document.operateForm.command.value = "pm-update-category";
        	document.operateForm.pmTargetId.value = targetId;
        	return true;
		}
		
		function onChangeLanguage() {
			document.operateForm.langId.value = document.all.langIdSelected.value;
			document.operateForm.command.value = "pm-get-edit-product-type-page";
			document.operateForm.submit();
			
		}

		]]>
		</xsl:text>
	</script>
</xsl:template>
	
<xsl:template match="/" >
<html>
<head>
    <title>Edit category</title>
	   <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <script language="JavaScript1.2" src="/script/jquery.min.js" type="text/javascript"/>
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"/>
	   <xsl:call-template name="java-script"/>
	   <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
</head>
<body>
    <!-- NEGESO HEADER -->
	 <xsl:call-template name="NegesoHeader">
      <xsl:with-param name="helpLink">
          <xsl:text>/admin/help/cpr1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
      </xsl:with-param>
	 </xsl:call-template>
    <div align="center">
        <!-- CONTENT -->
        <xsl:apply-templates select="negeso:page"/>
    </div>
</body>
</html>
</xsl:template>

<xsl:template match="negeso:page">
    <!-- NavBar -->
<form method="POST" name="operateForm" id="operateFormId" action="">
    <xsl:choose>
        <xsl:when test="count(descendant::negeso:page) = 0">
            <xsl:call-template name="NavBar">
                <xsl:with-param name="backLink" select='"?command=pm-browse-product-types"'/>
            </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
              <xsl:call-template name="NavBar"/>
        </xsl:otherwise>
   </xsl:choose>

    <!-- PATH -->
	<xsl:call-template name="pm.shopPath"/>

    <!-- TITLE -->
    <xsl:call-template name="tableTitle">
        <xsl:with-param name="headtext">
            <xsl:choose>
				    <xsl:when test="/negeso:page/negeso:pm-product-type/@new='true'">
					    <xsl:text>Product module. New category</xsl:text>
				    </xsl:when>
				    <xsl:otherwise>
					    <xsl:text>Product module. Edit category</xsl:text>
				    </xsl:otherwise>
			    </xsl:choose>
        </xsl:with-param>
    </xsl:call-template>
     <div class="admCenter admRed">
            <xsl:value-of select="errorMessage"/>
     </div>
		<table class="admNavPanel" cellspacing="0" cellpadding="0">
		    <xsl:apply-templates select="negeso:pm-product-type"/>
		</table>
		<!-- Update/reset fields -->
        <table cellpadding="0" cellspacing="0"  class="admNavPanel">
    			<tr>
    				<td class="admNavPanel admNavbar admCenter">
    				    <xsl:choose>
							    <xsl:when test="/negeso:page/negeso:pm-product-type/@new='true'">
    							        <input class="admNavbarInp" name="saveButton" onClick="return Update('{/negeso:page/negeso:pm-category/@id}');" value="&lt;&#160;Add&#160;&gt;" type="submit"/>&#160;
	        				       </xsl:when>
			        		       <xsl:otherwise>
					        	        <input class="admNavbarInp" name="saveButton" onClick="return Update('{/negeso:page/negeso:pm-category/@id}')" value="&lt;&#160;Save&#160;&gt;" type="submit"/>&#160;
							    </xsl:otherwise>
        					</xsl:choose>
                  <input class="admNavbarInp" value="&lt;&#160;Reset&#160;&gt;" type="reset"/>
    				</td>
    			</tr>
    	  </table>
    	  
    <!-- NavBar -->
    <xsl:choose>
        <xsl:when test="count(descendant::negeso:page) = 0">
            <xsl:call-template name="NavBar">
                <xsl:with-param name="backLink" select='"?command=pm-browse-product-types"'/>
            </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
              <xsl:call-template name="NavBar"/>
        </xsl:otherwise>
   </xsl:choose>
</form>   
</xsl:template>

	
<!-- ********************************** Category *********************************** -->
<xsl:template match="negeso:pm-product-type">
    <tr>
        <td class="admLightTD">
				<xsl:choose>
					<xsl:when test="@new='true'">
						<input type="hidden" name="updateTypeField" value="insert"></input>
					</xsl:when>
					<xsl:otherwise>
						<input type="hidden" name="updateTypeField" value="update"></input>
					</xsl:otherwise>
				</xsl:choose>
				<input type="hidden" name="command" value="pm-browse-product-types"></input>
				<input type="hidden" name="type" value="none"></input>
				<input type="hidden" name="langId" value="{/negeso:page/@lang-id}"></input>
				<xsl:choose>
					<xsl:when test="@new='true'">
						<input type="hidden" name="pmProductTypeId" value="-1"></input>
					</xsl:when>
					<xsl:otherwise>
						<input type="hidden" name="pmProductTypeId" value="{@id}"></input>
					</xsl:otherwise>
				</xsl:choose>
				<fieldset>
                <legend>Product type properties:</legend>
    					<table class="admNavPanel" cellspacing="0" cellpadding="0">
        					<!-- Title -->
    	    				<tr>
    		    				<td width="45%" class="admRight">Title*</td>
    			    			<td width="55%">
    				    			<input class="admTextArea admWidth200" type="text" name="titleField" data_type="text" required="true" uname="Title">
    							       <xsl:attribute name="value"><xsl:value-of select="@title"/></xsl:attribute>
        							</input>
    	    					</td>
    		    			</tr>
    			    		<!-- Description -->
    				    	<tr>
            					<td class="admRight">System name*</td>
    		        			<td>
    				        	    <input class="admTextArea admWidth200" type="text" name="nameField" data_type="text" required="true" uname="System name">
    								    <xsl:attribute name="value"><xsl:value-of select="@name"/></xsl:attribute>
    							    </input>
            					</td>
    	    				</tr>
    					</table>
				</fieldset>
				<fieldset>
                <legend>Properties:</legend>
	                <table class="admNavPanel" cellspacing="0" cellpadding="0">
	                <tr>
	                	<xsl:apply-templates select="pm-property-type"/>
	                    <!-- is-leaf -->
	                </tr>
	                </table>
	            </fieldset>
		</td></tr>
</xsl:template>

<!-- ********************************** Language *********************************** -->
<xsl:template match="negeso:languages">
    <xsl:text>&#160;Language version:&#160;</xsl:text><br/>
    <select name="langIdSelected" id="langIdSelected" onChange="return onChangeLanguage()">
        <xsl:if test="/negeso:page/negeso:pm-category/@new='true'">
            <xsl:attribute name="disabled">true</xsl:attribute>
        </xsl:if>
			<xsl:apply-templates select="negeso:language"/>
		</select>
</xsl:template>
	
<xsl:template match="negeso:language">
	<option value="{@id}">
		<xsl:if test="@code=/negeso:page/@lang">
			<xsl:attribute name="selected">true</xsl:attribute>
		</xsl:if>
		<xsl:value-of select="@code"/>
		<xsl:if test="@default='true'">
			(default)
		</xsl:if>
	</option>
</xsl:template>
	
<!-- ********************************** Path *********************************** -->
<xsl:template name="pm.shopPath">
    <!-- Path -->
    <table cellpadding="0" cellspacing="0" class="admNavPanel admSpaceSmall">
	   	<tr>
            <td class="admBold">
				<!-- Unactive pathe element - make it link-->
				<span class="admZero admLocation">
			    <a class="admLocation" href="?command=pm-get-entry-page">
			    	<xsl:value-of select="java:getString($dict_product, 'PRODUCT_MODULE')"/>
			    </a>
			    &#160;&gt;
				</span>
                <xsl:apply-templates select="negeso:path"/>
            </td>
        </tr>
    </table>
</xsl:template>

<xsl:template match="negeso:path">
	<xsl:apply-templates select="negeso:path-element"/>
</xsl:template>

<xsl:template match="negeso:path-element">
	<xsl:choose>
		<xsl:when test="@active='true'">
			<!-- Active pathe element - just print it-->
			<span class="admLeft"><xsl:value-of select="@title"/>	</span>
		</xsl:when>
		<xsl:otherwise>
			<!-- Unactive pathe element - make it link-->
			<a class="admSecurity admLeft" href="{@link}">
				<xsl:value-of select="@title"/>&#160;&gt;
			</a>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<!-- ******************************* Error message ********************************** -->
<xsl:template match="errorMessage">
    <xsl:value-of select="errorMessage"/>
</xsl:template>

</xsl:stylesheet>
