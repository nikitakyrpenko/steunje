<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$

  Copyright (c) 2005 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential
  Information and shall use it only in accordance with the terms of the
  license agreement you entered into with Negeso.

  Edit delivery methods

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
    <script language="JavaScript1.2" src="/script/jquery.min.js" type="text/javascript"/>
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
    <script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"/>
    
	<script language="JavaScript">
	<xsl:text disable-output-escaping="yes">
	    <![CDATA[
			
	    function update(targetId) {
			title_st = document.getElementById('newTitle').style.cssText = document.getElementById('newTitle').style.cssText;
			price_st = document.getElementById('newPrice').style.cssText = document.getElementById('newPrice').style.cssText;
			
			title = document.getElementById('newTitle').value;
			price = document.getElementById('newPrice').value;
            
			
            if ( title == '' || isNaN(price) || price == ''){
            	if(title == '')
	            	document.getElementById('newTitle').style.cssText = title_st + ";background-color: red;"
	            else
	            	document.getElementById('newTitle').style.cssText = title_st + ";background-color: white;"
	            	
            	if(isNaN(price) || price == '')
	            	document.getElementById('newPrice').style.cssText = price_st + ";background-color: red;"
	            else
	            	document.getElementById('newPrice').style.cssText = price_st + ";background-color: white;"
                return false;
            }
            else{
            	document.getElementById('newTitle').style.cssText = title_st + ";background-color: white;"
            	document.getElementById('newprice').style.cssText = price_st + ";background-color: white;"
            }
document.operateForm.submit();
        	return true;
		}

        function Delete(targetId) {
            if (confirm("Are you sure you want to delete this method?")) {
                document.operateForm.removeId.value = targetId;
                document.operateForm.submit();
                return true;
            }
            return false;
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
    <title><xsl:value-of select="java:getString($dict_product, 'DELIVERY_METHODS')"/></title>
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
    <!-- NavBar -->
<form method="POST" name="operateForm" id="operateFormId" action="">
   

 
     <div class="admCenter">
	     <xsl:for-each select="negeso:errors"> 
		     <xsl:for-each select="negeso:error"> 
		        <font color="#FF0000">
		        	Error during operation: 
		        	<xsl:choose>
		        		<xsl:when test="text() = 'PRICE_REQUIRED'">Price required</xsl:when>
		        		<xsl:when test="text() = 'PRICE_INVALID'">Price invalid</xsl:when>
		        		<xsl:when test="text() = 'TITLE_REQUIRED'">Title required</xsl:when>
	        		</xsl:choose>
		        </font>
		     </xsl:for-each>
	     </xsl:for-each>
     </div>

     <xsl:apply-templates select="negeso:delivery-methods"/>
   
</form>   
</xsl:template>

	
<!-- ********************************** Category *********************************** -->
<xsl:template match="negeso:delivery-methods">

    <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
        <tr>
            <td>
                <!-- PATH -->
                <xsl:call-template name="pm.shopPath"/>
            </td>
        </tr>
        <tr>
            <td align="center" class="admNavPanelFont" >
                <!-- TITLE -->
                <xsl:call-template name="tableTitle">
                    <xsl:with-param name="headtext">
                        <xsl:value-of select="java:getString($dict_product, 'DELIVERY_METHODS')"/>
                    </xsl:with-param>
                </xsl:call-template>
            </td>
        </tr>
        <tr>
            <th class="admTableTDLast">

                <table width="100%">
                    <tr>
                        <td width="400px">
                            Title <input type="text" name="newTitle" id="newTitle"/>&#160;
                            Price <input type="text" name="newPrice" id="newPrice"/>
                        </td>
                        <td>
                            <div class="admNavPanelInp" style="padding-left:0px;">
                                <div class="imgL"></div>
                                <div>

                                    <a  class="admNavPanelInp"  focus="blur()" onClick="return update('');" href="#return update('');">

                                        <xsl:value-of select="java:getString($dict_common, 'ADD')"/>

                                    </a>
                                </div>
                                <div class="imgR"></div>
                            </div>
                        </td>
                      
                    </tr>
                </table>
                
            
                
            </th>
        </tr>
<tr>
    <td>
    	<input type="hidden" name="command" value="pm-manage-delivery-methods"></input>
    	<input type="hidden" name="removeId" value=""></input>
    	<input type="hidden" name="action" value="update"></input>
		
		<xsl:apply-templates select="negeso:delivery-method"/>
	</td>
</tr>
        <tr>
            <td class="admTableFooter" >&#160;</td>
        </tr>
</table>
</xsl:template>

<xsl:template match="negeso:delivery-method">
	
	<table  cellspacing="0" cellpadding="0" width="100%">
      
		<tr>
            <th class="admTableTDLast admWidth150"><xsl:value-of select="java:getString($dict_common, 'TITLE')"/>*</th>
            <td>
                <table width="100%">
                    <tr>
                        <td class="admTableTD" width="90%">
                            <input class="admTextArea admWidth200"
                                type="text"
                                name="{@id}_title"
                                data_type="text"
                                required="true"
		    			>
                                <xsl:attribute name="uname">
                                    <xsl:value-of select="java:getString($dict_common, 'TITLE')"/>
                                </xsl:attribute>
                                <xsl:attribute name="value">
                                    <xsl:value-of select="@title"/>
                                </xsl:attribute>
                            </input>
                        </td>
                        <td class="admTableTDLast" width="10%">
                            <img src="/images/delete.png" class="admHand" onClick="return Delete({@id})">
                                <xsl:attribute name="title">
                                    <xsl:value-of select="java:getString($dict_common, 'DELETE')"/>
                                </xsl:attribute>
                            </img>
                        </td>
                    </tr>
                </table>
            </td>
		</tr>
		<tr>
            <th class="admTableTDLast admWidth150"><xsl:value-of select="java:getString($dict_product, 'PRICE')"/>*</th>
			<td >
				<table width="100%">
				<tr>
                    <td class="admTableTDLast" width="90%">
	    			<input class="admTextArea admWidth200" 
	    				type="text" 
	    				name="{@id}_price" 
	    				data_type="currency"
	    				required="true"
	    			>
				       <xsl:attribute name="uname"><xsl:value-of select="java:getString($dict_product, 'PRICE')"/></xsl:attribute>
				       <xsl:attribute name="value"><xsl:value-of select="@price-unformatted"/></xsl:attribute>
					</input>
		            </td>
		        </tr>
		        </table>
			</td>
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
                        <xsl:value-of select="java:getString($dict_product, 'DELIVERY_METHODS')"/>
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
