<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$
 
  Copyright (c) 2005 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  Customers menagement interface
 
  @version      $Revision$
  @author       Olexiy.Strashko
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
<xsl:variable name="dict_product" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_product.xsl', $lang)"/>
<xsl:variable name="dict_security_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_security_module.xsl', $lang)"/>

<!-- NEGESO JAVASCRIPT Temaplate -->    
<xsl:template name="java-script">
    <script type="text/javascript" src="/script/jquery.min.js"></script>
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
    <script type="text/javascript" src="/script/common_functions.js"></script>
        
    <script language="JavaScript">
        var s_DeleteCustomerConfirmation = "<xsl:value-of select="java:getString($dict_product, 'DELETE_CUSTOMER_CONFIRMATION')"/>";

    <xsl:text disable-output-escaping="yes">
        <![CDATA[


        function editCustomer(targetId) {
            document.operateForm.action.value = "render_customer";
            document.operateForm.customerId.value = targetId;
            document.operateForm.submit();
        }


        function deleteCustomer(targetId) {
            if (confirm(s_DeleteCustomerConfirmation)) {
                document.operateForm.action.value = "remove_customer";
	            document.operateForm.customerId.value = targetId;
                document.operateForm.submit();
                return true;
            }
            return false;
        }
        
        function addCustomer(){
        	document.operateForm.action.value = "add_customer";
            document.operateForm.submit();
        }
        
        function remindPasword(targetId){
        	document.operateForm.action.value = "remind_pasword";
            document.operateForm.customerId.value = targetId;
            document.operateForm.submit();
        }
        
        ]]>
        </xsl:text>
    </script>
</xsl:template>

<!-- MAIN ENTRY -->
<xsl:template match="/">
<html>
    <head>
        <title><xsl:value-of select="java:getString($dict_product, 'CUSTOMERS')"/></title>
        <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
        <xsl:call-template name="java-script"/>
        <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    </head>
    <body>
        <!-- NEGESO HEADER -->
         <xsl:call-template name="NegesoBody">
             <xsl:with-param name="helpLink">
                 <xsl:text>/admin/help/cpr1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
             </xsl:with-param>
             <xsl:with-param name="backLink" select='"?command=pm-get-entry-page"' />
         </xsl:call-template>
    </body>
</html>
</xsl:template>

<xsl:template match="negeso:page"  mode="admContent">

    
    
    <!-- Content -->
    <table  align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable" width="100%">
        
        <tr>
            <td>
                <xsl:apply-templates select="negeso:customers"/>

            </td>
        </tr>
        <tr>
            <td class="admTableFooter" >&#160;</td>
        </tr>
    </table>
</xsl:template>

<!-- ********************************** Customers *********************************** -->
<xsl:template match="negeso:customers">
        <!-- Render HEADER -->
        <form method="POST" name="operateForm" action="">
        <input type="hidden" name="command" value="pm-manage-customers"></input>
        <input type="hidden" name="action" value=""></input>
        <input type="hidden" name="customerId" value="-1"></input>
               
       	<table cellspacing="0" cellpadding="0" width="100%">
            <tr>
                <td colspan="6">
                    <!-- PATH -->
                    <xsl:call-template name="pm.shopPath"/>
                </td>
            </tr>
            <tr>
                <td align="center" class="admNavPanelFont"  colspan="6">
                    <!-- TITLE -->
                    <xsl:call-template name="tableTitle">
                        <xsl:with-param name="headtext">
                            <xsl:value-of select="java:getString($dict_product, 'CUSTOMERS')"/>
                        </xsl:with-param>
                    </xsl:call-template>
                </td>
            </tr>
            <tr>
                <td colspan="6">
                    <xsl:call-template name="negeso:toolbar"/>
                </td>
            </tr>
            <tr>
                <td class="admTDtitles"><xsl:value-of select="java:getString($dict_common, 'TITLE')"/></td>
                <td class="admTDtitles"><xsl:value-of select="java:getString($dict_security_module, 'LOGIN')"/></td>
                <td class="admTDtitles"><xsl:value-of select="java:getString($dict_common, 'EMAIL')"/></td>
                <td class="admTDtitles" style="widht: 30px;">&#160;</td>
                <td class="admTDtitles" style="widht: 30px;">&#160;</td>
                <td class="admTDtitles" style="widht: 30px;">&#160;</td>
            </tr>
            <xsl:apply-templates select="negeso:customer"/>
             
        </table>
        </form>
      
</xsl:template>

<xsl:template name="negeso:toolbar">
        <table  cellspacing="0" cellpadding="0" width="100%">
            <tr>
                <th  class="admTableTDLast">

                    <div class="admNavPanelInp">
                        <div class="imgL"></div>
                        <div>
                            <input class="admNavbarInp" type="button" onclick="addCustomer();" style="width:100px;">
                                <xsl:attribute name="value"> <xsl:value-of select="java:getString($dict_product, 'ADD_CUSTOMER')"/>
                                </xsl:attribute>
                            </input>
                        </div>
                        <div class="imgR"></div>
                    </div>

            
                    <div class="admNavPanelInp">
                        <div class="imgL"></div>
                        <div>
                            <input class="admNavbarInp" type="button" style="width:110px;" onclick="window.location.href='/admin/exportCustomers'">
                                <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'EXPORT_TO_EXCEL')"/></xsl:attribute>
                            </input>
                        </div>
                        <div class="imgR"></div>
                    </div>
                   
                </th>
       		</tr>
       	</table>	
</xsl:template>

<!-- ********************************** Customer *********************************** -->
<xsl:template match="negeso:customer">
    <tr>
        <th class="admTableTD">
            <xsl:value-of select="@title"/>&#160;
        </th>
        <th class="admTableTD">
            <a class="admAnchor" href="#" onClick = "return editCustomer({@id})">
                <xsl:value-of select="@login"/>  </a>
        </th>
        <th class="admTableTD">
            <xsl:value-of select="@email"/>&#160;
        </th>
<!-- 
	Functionality is too raw (will be finished in wcms 3.0  
	
        <td class="admDarkTD admWidth30" style="widht: 30px;">
            <img src="/images/move.gif" class="admHand" onClick="return remindPasword({@id})">
                <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'EMAIL')"/>&#160;<xsl:value-of select="java:getString($dict_security_module, 'PASSWORD')"/></xsl:attribute>
         </img>
        </td>
-->
        <td class="admTableTDLast">
            <img src="/images/edit.png" class="admHand" onClick="return editCustomer({@id})">
                <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'EDIT')"/></xsl:attribute>
            </img>
        </td>
        <td class="admTableTDLast" colspan="2">
            <img src="/images/delete.png" class="admHand" onClick="return deleteCustomer({@id})">
                <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'DELETE')"/></xsl:attribute>
         </img>
        </td>
    </tr>
</xsl:template>


<xsl:template name="pm.shopPath">
    <!-- Path -->
    <table cellpadding="0" cellspacing="0" class="admNavPanel admSpaceSmall">
	   	<tr>
            <td style="padding:8px 0 0 5px;" valign="middle" >
				<!-- Unactive pathe element - make it link-->
				<span >
			    <a class="admNavigation" href="?command=pm-get-entry-page">
			    	<xsl:value-of select="java:getString($dict_product, 'PRODUCT_MODULE')"/>
			    </a>
                    &#160; <img src="/images/navig_arrow.png"  style="vertical-align:middle;"/>&#160;
                </span>
			
				<!-- Active pathe element - just print it-->
                <span class="admNavigation" style="text-decoration:none;">
			    	<xsl:value-of select="java:getString($dict_product, 'CUSTOMERS')"/>
				</span>
            </td>
        </tr>
    </table>
</xsl:template>

</xsl:stylesheet>
