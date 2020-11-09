<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$

  Copyright (c) 2005 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential
  Information and shall use it only in accordance with the terms of the
  license agreement you entered into with Negeso.

  Edit shop preferences

  @version      $Revision$
  @author       Olexiy.Strashko
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:negeso="http://negeso.com/2003/Framework"
	xmlns:java="http://xml.apache.org/xslt/java"
	exclude-result-prefixes="java">

<xsl:include href="/xsl/negeso_body.xsl"/>

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_product" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_product.xsl', $lang)"/>

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
            if ( !validateForm(operateFormId) ){
                return false;
            }
        	return true;
		}
function saveForm(){
            document.operateForm.submit();
        }
   function resetForm(){
   document.operateForm.reset();
 }

		]]>
		</xsl:text>
	</script>
</xsl:template>
	
<xsl:template match="/" >
<html>
<head>
    <title><xsl:value-of select="java:getString($dict_product, 'SHOP_PREFERENCES')"/></title>
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>    
    <xsl:call-template name="java-script"/>	   
</head>
<body>
    <!-- NEGESO HEADER -->
	<xsl:call-template name="NegesoBody">
     <xsl:with-param name="helpLink">
         <xsl:text>/admin/help/cpr1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
     </xsl:with-param>
     <xsl:with-param name="backLink" select='"?command=pm-get-entry-page"'/>
	</xsl:call-template>
    <xsl:call-template name="buttons"> </xsl:call-template>
   
</body>
</html>
</xsl:template>

    <xsl:template match="negeso:page"  mode="admContent">
  
<form method="POST" name="operateForm" id="operateFormId" action="">
   

    

   
     <div class="admCenter">
        <font color="#FF0000">
            <xsl:value-of select="errorMessage"/>
        </font>
     </div>

    <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
        <tr>
            <td>
                <!-- PATH -->
                <xsl:call-template name="pm.shopPath"/>
            </td>
        </tr>
        <tr>
            <td align="center" class="admNavPanelFont"  colspan="6">
                <!-- TITLE -->
                <xsl:call-template name="tableTitle">
                    <xsl:with-param name="headtext">
                        <xsl:value-of select="java:getString($dict_product, 'SHOP_PREFERENCES')"/>
                    </xsl:with-param>
                </xsl:call-template>
            </td>
        </tr>
		    <xsl:apply-templates select="negeso:shop-preferences"/>
        <tr>
            <td class="admTableFooter" >&#160;</td>
        </tr>
		</table>

		
   
</form>   
</xsl:template>

	
<!-- ********************************** Category *********************************** -->
<xsl:template match="negeso:shop-preferences">
    <tr>
        <td class="admTDtitles" align="center">
            <input type="hidden" name="command" value="pm-manage-shop-preferences"></input>
            <input type="hidden" name="action" value="update"></input>

            <xsl:value-of select="java:getString($dict_product, 'SHOP_CONTACT')"/>:
        </td>
    </tr>
    <tr>
        <td>
	            <xsl:for-each select="negeso:shop-contact">
	            	<xsl:apply-templates select = "negeso:contact"/>
	            </xsl:for-each>
        	
	</td></tr>
</xsl:template>

<xsl:template match="negeso:contact">
<xsl:param name="prefix" />
	<table border="0" width="100%" class="cartTable">
		<tr>
            <th class="admTableTD admWidth150"><xsl:value-of select="java:getString($dict_common, 'COMPANY_NAME')"/>*</th>
            <th class="admTableTDLast">
    			<input class="admTextArea admWidth200" 
    				type="text" 
    				name="shop_company_name" 
    				data_type="text"
    				required="true"
    			>
			       <xsl:attribute name="uname"><xsl:value-of select="java:getString($dict_common, 'COMPANY_NAME')"/></xsl:attribute>
			       <xsl:attribute name="value"><xsl:value-of select="@company-name"/></xsl:attribute>
				</input>
			</th>
		</tr>
		<tr>
            <th class="admTableTD admWidth150"><xsl:value-of select="java:getString($dict_common, 'ADDRESS_LINE')"/>*</th>
            <th class="admTableTDLast">
    			<input class="admTextArea admWidth200" 
    				type="text" 
    				name="shop_address_line" 
    				data_type="text"
    				required="true"
    			>
			       <xsl:attribute name="uname"><xsl:value-of select="java:getString($dict_common, 'ADDRESS_LINE')"/></xsl:attribute>
			       <xsl:attribute name="value"><xsl:value-of select="@address-line"/></xsl:attribute>
				</input>
			</th>
		</tr>
		<tr>
            <th class="admTableTD admWidth150"><xsl:value-of select="java:getString($dict_common, 'EMAIL')"/>*</th>
            <th class="admTableTDLast">
    			<input class="admTextArea admWidth200" 
    				type="text" 
    				name="shop_email" 
    				data_type="email"
    				required="true"
    			>
			       <xsl:attribute name="uname"><xsl:value-of select="java:getString($dict_common, 'EMAIL')"/></xsl:attribute>
			       <xsl:attribute name="value"><xsl:value-of select="@email"/></xsl:attribute>
				</input>
				<b><xsl:value-of select="java:getString($dict_common, 'EXAMPLE')"/>:</b> support@negeso.com
			</th>
		</tr>
		<tr>
            <th class="admTableTD admWidth150"><xsl:value-of select="java:getString($dict_common, 'PHONE')"/></th>
            <th class="admTableTDLast">
    			<input class="admTextArea admWidth200" 
    				type="text" 
    				name="shop_phone" 
    				data_type="text"
    			>
			       <xsl:attribute name="uname"><xsl:value-of select="java:getString($dict_common, 'PHONE')"/></xsl:attribute>
			       <xsl:attribute name="value"><xsl:value-of select="@phone"/></xsl:attribute>
				</input>
			</th>
		</tr>
		<tr>
            <th class="admTableTD admWidth150"><xsl:value-of select="java:getString($dict_common, 'FAX')"/></th>
            <th class="admTableTDLast">
    			<input class="admTextArea admWidth200" 
    				type="text" 
    				name="shop_fax" 
    				data_type="text"
    			>
			       <xsl:attribute name="uname"><xsl:value-of select="java:getString($dict_common, 'FAX')"/></xsl:attribute>
			       <xsl:attribute name="value"><xsl:value-of select="@fax"/></xsl:attribute>
				</input>
			</th>
		</tr>
		<tr>
            <th class="admTableTD admWidth150"><xsl:value-of select="java:getString($dict_common, 'COUNTRY')"/></th>
            <th class="admTableTDLast">
    			<input class="admTextArea admWidth200" 
    				type="text" 
    				name="shop_country" 
    				data_type="text"
    			>
			       <xsl:attribute name="uname"><xsl:value-of select="java:getString($dict_common, 'COUNTRY')"/></xsl:attribute>
			       <xsl:attribute name="value"><xsl:value-of select="@country"/></xsl:attribute>
				</input>
			</th>
		</tr>
		<tr>
            <th class="admTableTD admWidth150"><xsl:value-of select="java:getString($dict_common, 'CITY')"/></th>
            <th class="admTableTDLast">
    			<input class="admTextArea admWidth200" 
    				type="text" 
    				name="shop_city" 
    				data_type="text"
    			>
			       <xsl:attribute name="uname"><xsl:value-of select="java:getString($dict_common, 'CITY')"/></xsl:attribute>
			       <xsl:attribute name="value"><xsl:value-of select="@city"/></xsl:attribute>
				</input>
			</th>
		</tr>
		<tr>
            <th class="admTableTD admWidth150"><xsl:value-of select="java:getString($dict_common, 'ZIP_CODE')"/></th>
            <th class="admTableTDLast">
    			<input class="admTextArea admWidth200" 
    				type="text" 
    				name="shop_zip_code" 
    				data_type="text"
    			>
			       <xsl:attribute name="uname"><xsl:value-of select="java:getString($dict_common, 'ZIP_CODE')"/></xsl:attribute>
			       <xsl:attribute name="value"><xsl:value-of select="@zip-code"/></xsl:attribute>
				</input>
			</th>
		</tr>
	</table>
</xsl:template>


<xsl:template name="pm.shopPath">
    <!-- Path -->
    <table cellpadding="0" cellspacing="0" >
	   	<tr>
            <td style="padding:8px 0 0 5px;" valign="middle" >
				<!-- Unactive pathe element - make it link-->
				<span>
			    <a class="admNavigation" href="?command=pm-get-entry-page">
			    	<xsl:value-of select="java:getString($dict_product, 'PRODUCT_MODULE')"/>
			    </a>
                    &#160; <img src="/images/navig_arrow.png"  style="vertical-align:middle;"/>&#160;
				</span>
			
				<!-- Active pathe element - just print it-->
				<span class="admNavigation" style="text-decoration:none;">
			    	<xsl:value-of select="java:getString($dict_product, 'SHOP_PREFERENCES')"/>
				</span>
            </td>
        </tr>
    </table>
</xsl:template>
    <!--******************** Bottom buttons ********************-->
    <xsl:template name="buttons">
        <table cellpadding="0" cellspacing="0"  width="764px" align="center" class="Buttons">
            <tr>
                <td>

                    <div class="admBtnGreenb">
                        <div class="imgL"></div>
                        <div>
                            <a class="admBtnText" focus="blur()" onClick="saveForm()" href="#saveForm();">

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



    <!-- ******************************* Error message ********************************** -->
<xsl:template match="errorMessage">
    <xsl:value-of select="errorMessage"/>
</xsl:template>

</xsl:stylesheet>
