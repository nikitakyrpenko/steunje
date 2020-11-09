<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$

  Copyright (c) 2005 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential
  Information and shall use it only in accordance with the terms of the
  license agreement you entered into with Negeso.

  Edit customer

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
    <xsl:variable name="dict_security_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_security_module.xsl', $lang)"/>

    <!-- NEGESO JAVASCRIPT Temaplate -->
    <xsl:template name="java-script">
        <script type="text/javascript" src="/script/jquery.min.js"></script>
		<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
        <script type="text/javascript" src="/script/cufon-yui.js"></script>
        <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
        <script language="JavaScript1.2" src="/script/common_functions.js" type="text/javascript"/>
        
        <script language="JavaScript">
            var YOU_MUST_FILL_ALL_OBLIGATORY_FIELDS = "<xsl:value-of select="java:getString($dict_product, 'YOU_MUST_FILL_ALL_OBLIGATORY_FIELDS')"/>";
            var EMAIL_IS_NOT_VALID = "<xsl:value-of select="java:getString($dict_product, 'EMAIL_IS_NOT_VALID1')"/>";
            var DISCOUNT_MUST_BE_A_VALID_NUMBER = "<xsl:value-of select="java:getString($dict_product, 'DISCOUNT_MUST_BE_A_VALID_NUMBER')"/>";
            <xsl:text disable-output-escaping="yes">
	    <![CDATA[
	    // RE-DEFINING VALIDATION!!!
	    function isEmailAddr(email)
		{
			/* Official RFC recomendation
			var local_part="([a-z0-9!#$%&'*+/=?^_`{|}~-]+(\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"([\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*\")";
			var domain_part="(([a-z0-9]([a-z0-9-]*[a-z0-9])?\.)+[a-z0-9]([a-z0-9-]*[a-z0-9])?|\\[((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:([\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\\])";
			var official_email_regexp_RFC_2822=local_part+"@"+domain_part;
			*/
			var tiny_email_regexp="^[a-zA-Z0-9][a-zA-Z0-9_.-]*[a-zA-Z0-9]@[a-zA-Z0-9][a-zA-Z0-9_.-]*[a-zA-Z0-9]$";
			return (new RegExp(tiny_email_regexp).test(email));
		}
		
		// RE-DEFINING VALIDATION!!!
		function NumericFormatValid(obj,int_limit,fract_limit,val_limit_min,val_limit_max)
		{
			if ( obj.tagName=="INPUT" && ( obj.type=="text" || obj.type=="password" ) )
			{
				var val = obj.value;
				// apererva -> TLNC-156: number more 999 in the text field contains
				// dots, i.e. "1.000.234,45". It should be normalized
				// excluding dots from number.  
				// Commented 24.09.2007 by Rostislav Brizgunov
				//val = val.replace(new RegExp("\\.","gi"), "");
		
				// For our calculations we have to convert our value to corresponding format:
				// 5,67 --> 5.67
				val = val.replace(",",".");
		
				// 5.0E+6 --> 5000000
				val = parseFloat(val);
				
				// Added 24.09.2007 by Rostislav Brizgunov
				var ret_as_string=''+val;
		
				// !!!:FIRST CHECK: value in the field has absolutely incorrect numeric-format
				if ( (typeof(val) != 'number') || (isNaN(val)) )
					return -1;
		
				// !!!:SECOND CHECK: value is more than maximum or less than minimum
				if ((typeof(val_limit_min) == 'number')&&(val < val_limit_min))
					return 3;
				if ((typeof(val_limit_max) == 'number')&&(val > val_limit_max))
					return 4;
		
				// Beginning creating formated output
				var int_part = "";
				var fract_part = "";
				var sign = (val < 0) ? "-" : "";
		
				// !!!:THIRD CHECK: integer validation (i.e. 2345678 or 1234,00000, but not 123.4567)
				val = val + "";
				var re = '^[+-]?\\d+$';
				var t1 = val.search(re);
				if (t1==0) {
					// Continue creating formated output
					int_part = val;
					if (typeof(fract_limit) == 'number') {
						for (var i = 0; i<fract_limit; i++)
							fract_part = fract_part + "0";
					}
		
					t1 = val;
					if ((typeof(int_limit) == 'number')&&(int_limit >= 1)&&(t1.length > int_limit))
						return 1;
				}
				// !!!:FOURTH CHECK: floating-point validation (i.e. 123.456 or .456)
				else if (val.search('^[+-]?(\\d*)\\.(\\d*)$')==0)
				{
					re = '^[+-]?(\\d*)\\.(\\d*)$';
					t1 = val.match(re);
					int_part = t1[1];
					fract_part = t1[2];
		
					// Continue creating formated output
					if (typeof(fract_limit) == 'number') {
						for (var i = fract_part.length; i<fract_limit; i++)
							fract_part = fract_part + "0";
					} 
		
					if ((typeof int_limit == 'number')&&(int_limit >= 1)&&(int_part.length > int_limit))
						return 1;
		          	else if ((typeof fract_limit == 'number')&&(fract_limit >= 0)&&(fract_part.length > fract_limit))
		          		return 2;
				}
		
				// Here we are returning parsed and correct value into the field
				var val_as_string = int_part;
				if (fract_part && fract_part.length>0)
					val_as_string = sign + val_as_string + "," + fract_part;
				val_as_string = val_as_string.replace("--","-");
				// Commented 24.09.2007 by Rostislav Brizgunov
				//obj.value = val_as_string;
				obj.value=ret_as_string
				return 0;   
			}
		}
		
	    // RE-DEFINING VALIDATION!!! 
	    function validateForm(obj) {return validate(obj,null,null,null,false)}
	        
		function update(targetId) {
            if ( !validateForm(operateFormId) ){
                return false;
            }
        	return true;
		}
		
		function addCustomerInfo(){
			if(validateForm(operateFormId)){
				operateForm.action.value="add_user";
				operateForm.submit();
			}
		}
		
		function saveCustomerInfo(uid){
			if(validateForm(operateFormId)){
				operateForm.action.value="save_customer";
				operateForm.user_id.value=uid;
				operateForm.customerId.value=uid;
				operateForm.submit();
			}
		}
		
		function enableTable(ch){
			if(ch.checked)
				bi_table.style.display = "none";
			else
				bi_table.style.display = "block";
		}
		
		function validateInfo(){
			if(operateForm.loginField.value==null || operateForm.loginField.value=="" || 
			   operateForm.titleField.value==null || operateForm.titleField.value=="" || 
			   operateForm.emailField.value==null || operateForm.emailField.value==""){
				alert(YOU_MUST_FILL_ALL_OBLIGATORY_FIELDS);
				return false;
			}
			else if( !validateEmail(operateForm.emailField.value) ){
				alert(EMAIL_IS_NOT_VALID);
				return false;
			}
			else if( !validateDiscount(operateForm.discountField.value)){
				alert(DISCOUNT_MUST_BE_A_VALID_NUMBER);
				return false;
			}
			return true;
		}
		
		function validateDiscount(disc){
			var objRegExp  =  /(^\d\d*\.\d*$)|(^\d\d*$)|(^\.\d\d*$)/;
        	return objRegExp.test(disc);
		}
		
		function validateEmail(checkThisEmail){
			var myEMailIsValid = true;
			var myAtSymbolAt = checkThisEmail.indexOf('@');
			var myLastDotAt = checkThisEmail.lastIndexOf('.');
			var mySpaceAt = checkThisEmail.indexOf(' ');
			var myLength = checkThisEmail.length;
	
			// at least one @ must be present and not before position 2
			// @yellow.com : NOT valid
			// x@yellow.com : VALID
				
			if (myAtSymbolAt < 1 ) myEMailIsValid = false;
	
			// at least one . (dot) afer the @ is required
			// x@yellow : NOT valid
			// x.y@yellow : NOT valid
			// x@yellow.org : VALID
			
			if (myLastDotAt < myAtSymbolAt) myEMailIsValid = false;
			
			// at least two characters [com, uk, fr, ...] must occur after the last . (dot)
			// x.y@yellow. : NOT valid
			// x.y@yellow.a : NOT valid
			// x.y@yellow.ca : VALID
			
			if (myLength - myLastDotAt <= 2) myEMailIsValid = false;
			
			
			// no empty space " " is permitted (one may trim the email)
			// x.y@yell ow.com : NOT valid
			
			if (mySpaceAt != -1) myEMailIsValid = false;
		
			return myEMailIsValid;
		}
		]]>
		</xsl:text>
        </script>
    </xsl:template>

    <xsl:template match="/" >
        <html>
            <head>
                <title>
                    <xsl:value-of select="java:getString($dict_product, 'EDIT_CUSTOMER')"/>
                </title>
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
                    <xsl:with-param name="backLink" select='"?command=pm-manage-customers"'/>
                </xsl:call-template>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="negeso:page"  mode="admContent">

        <form method="POST" name="operateForm" id="operateFormId" action="">
            <input type="hidden" name="user_id"/>


            <!-- PATH -->
            <xsl:call-template name="pm.shopPath"/>

            <xsl:choose>
                <xsl:when test="@user_identity='exists'">
                    <span style="color: red; font-weight: bold;">
                        <xsl:value-of select="java:getString($dict_product, 'THE_USER_WITH_SUCH_NAME_ALREADY_EXISTS')"/>
                    </span>
                </xsl:when>
                <xsl:when test="@user_identity='email_exists'">
                    <span style="color: red; font-weight: bold;">
                        <xsl:value-of select="java:getString($dict_product, 'THE_USER_WITH_SUCH_EMAIL_ALREADY_EXISTS')"/>
                    </span>
                </xsl:when>
            </xsl:choose>
            <div class="admCenter">
                <font color="#FF0000">
                    <xsl:value-of select="errorMessage"/>
                </font>
            </div>

            <table  align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
                <xsl:apply-templates select="negeso:customer"/>
            </table>



          
                <table cellpadding="0" cellspacing="0"  width="764px" align="center" class="Buttons">
                    <tr>
                        <td>

                            <div class="admBtnGreenb">
                                <div class="imgL"></div>
                                <div>
                                    <xsl:choose>
                                        <xsl:when test="@mode='add_customer'"> 
                                            <input class="admNavbarInp" name="saveButton" onClick="addCustomerInfo();" type="button">
                                                <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'ADD')"/>                                                </xsl:attribute>
                                            </input>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <input class="admNavbarInp" name="saveButton" onClick="saveCustomerInfo('{negeso:customer/@id}');" type="button">
                                                <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'SAVE')"/>
                                                </xsl:attribute>
                                            </input>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </div>
                                <div class="imgR"></div>
                            </div>

                        </td>
                    </tr>
                </table>
            
        </form>
           
    </xsl:template>


    <!-- ********************************** Category *********************************** -->
    <xsl:template match="negeso:customer">
        <tr>
            <td align="center" class="admNavPanelFont"  colspan="6">
                <!-- TITLE -->
                <xsl:call-template name="tableTitle">
                    <xsl:with-param name="headtext">
                        <xsl:choose>
                            <xsl:when test="@mode='add_customer'">
                                <xsl:value-of select="java:getString($dict_product, 'ADD_CUSTOMER')"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="java:getString($dict_product, 'EDIT_CUSTOMER')"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:with-param>
                </xsl:call-template>
            </td>
        </tr>


        <tr>
            <td>
                <input type="hidden" name="command" value="pm-manage-customers"></input>
                <input type="hidden" name="action" value="render_customer"></input>
                <input type="hidden" name="customerId" value="{@id}"></input>
                <fieldset>

                    <table cellspacing="0" cellpadding="0" width="100%">
                        <tr>
                            <td align="center"  bgcolor="#FFFFFF"  class="admTDtitles" style="height:auto;" colspan="3">
                            <xsl:value-of select="java:getString($dict_product, 'CUSTOMER_PROPERTIES')"/>:
                        </td>
                        </tr>
                        
                        <!-- Login -->
                        <tr>
                            <th  class="admTableTD admWidth150">
                                <xsl:value-of select="java:getString($dict_security_module, 'LOGIN')"/>*
                            </th>
                            <th  class="admTableTDLast">
                                <xsl:choose>
                                    <xsl:when test="//negeso:page/@mode='add_customer'">
                                        <input class="admTextArea admWidth200"
                                            type="text"
                                            name="loginField"
                                            data_type="text"
                                            required="true"
                                            uname="Login"
					    			>
                                            <xsl:attribute name="value">
                                                <xsl:value-of select="@login"/>
                                            </xsl:attribute>
                                        </input>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <input class="admTextArea admWidth200"
                                            type="text"
                                            name="loginField"
                                            data_type="text"
                                            required="true"
                                            uname="Login"
                                            readonly="true"
					    			>
                                            <xsl:attribute name="value">
                                                <xsl:value-of select="@login"/>
                                            </xsl:attribute>
                                        </input>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </th>
                        </tr>

                        <!-- Title -->
                        <tr>
                            <th  class="admTableTD admWidth150">
                                <xsl:value-of select="java:getString($dict_common, 'TITLE')"/>*
                            </th>
                            <th  class="admTableTDLast">
                                <input class="admTextArea admWidth200"
                                    type="text"
                                    name="titleField"
                                    data_type="text"
                                    required="true"
                                    uname="Title"
			    			>
                                    <xsl:attribute name="value">
                                        <xsl:value-of select="@title"/>
                                    </xsl:attribute>
                                </input>
                            </th>
                        </tr>

                        <!-- Email -->
                        <tr>
                            <th  class="admTableTD admWidth150">
                                <xsl:value-of select="java:getString($dict_common, 'EMAIL')"/>*
                            </th>
                            <th  class="admTableTDLast">
                                <input class="admTextArea admWidth200"
                                    type="text"
                                    name="emailField"
                                    is_email="true"
                                    required="true"
			    			>
                                    <xsl:attribute name="uname">
                                        <xsl:value-of select="java:getString($dict_common, 'EMAIL')"/>
                                    </xsl:attribute>
                                    <xsl:attribute name="value">
                                        <xsl:value-of select="@email"/>
                                    </xsl:attribute>
                                </input>
                                <b>
                                    <xsl:value-of select="java:getString($dict_common, 'EXAMPLE')"/>:
                                </b> support@negeso.com
                            </th>
                        </tr>

                        <tr>
                            <th  class="admTableTD admWidth150">
                                <xsl:value-of select="java:getString($dict_product, 'DISCOUNT1')"/>*
                            </th>
                            <th  class="admTableTDLast">
                                <input class="admTextArea admWidth200"
                                    type="text"
                                    name="discountField"
                                    data_type="text"
                                    uname="Title"
                                    numeric_field_params="na;na;na;na"
			    			>
                                    <xsl:attribute name="value">
                                        <xsl:value-of select="@discount"/>
                                    </xsl:attribute>
                                </input> %
                            </th>
                        </tr>
                    </table>
                </fieldset>

                <fieldset>

                    <xsl:for-each select="negeso:shipping-contact">
                        <xsl:call-template name = "pm.render_contact">
                            <xsl:with-param name="prefix" select='"shi"'/>
                        </xsl:call-template>
                    </xsl:for-each>
                </fieldset>

                <fieldset >

                    <xsl:choose>
                        <xsl:when test="@use-shipping-contact='true'">
                            <table border="0" width="100%" >
                                <tr>
                                    <td align="center"  bgcolor="#FFFFFF"  class="admTDtitles" style="height:auto;" colspan="2">
                                        <xsl:value-of select="java:getString($dict_product, 'BILLING_CONTACT')"/>:
                                    </td>
                                </tr>
                                
                                
                               
                                <tr>

                                    <th  class="admTableTD admWidth150">
                                        <xsl:value-of select="java:getString($dict_product, 'SAME_AS_SHIPPING')"/>
                                    </th>

                                    <th  class="admTableTDLast">
                                        <input type="checkbox" name="use_shipping_contact" CHECKED="true" onclick="enableTable(this)"/>
                                    </th>
                                </tr>
                            </table>
                            <xsl:for-each select="negeso:billing-contact">
                                <xsl:call-template name = "pm.render_contact">
                                    <xsl:with-param name="prefix" select='"bi"'/>
                                </xsl:call-template>
                            </xsl:for-each>
                            <script>
                                bi_table.style.display = 'none';
                            </script>
                        </xsl:when>
                        <xsl:otherwise>
                            <table border="0" width="100%" >
                                <tr>
                                    <th  class="admTableTD admWidth150">
                                        <xsl:value-of select="java:getString($dict_product, 'SAME_AS_SHIPPING')"/>
                                    </th>


                                    <th  class="admTableTDLast">
                                        <input type="checkbox" name="use_shipping_contact" onclick="enableTable(this)"/>
                                    </th>
                                </tr>
                            </table>
                            <xsl:for-each select="negeso:billing-contact">
                                <xsl:call-template name = "pm.render_contact">
                                    <xsl:with-param name="prefix" select='"bi"'/>
                                </xsl:call-template>
                            </xsl:for-each>
                        </xsl:otherwise>
                    </xsl:choose>
                </fieldset>
                <!--xsl:choose>
        		<xsl:when test="@use-shipping-contact='true'">
					<fieldset class="admFieldset" disabled="true">
		            <legend>
		             	<xsl:value-of select="java:getString($dict_product, 'BILLING_CONTACT')"/>
						(<xsl:value-of select="java:getString($dict_product, 'SAME_AS_SHIPPING')"/>)
		            :</legend>
		        	</fieldset>
        		</xsl:when>
        		<xsl:otherwise>
					<fieldset  class="admFieldset">
		            <legend><xsl:value-of select="java:getString($dict_product, 'BILLING_CONTACT')"/>:</legend>
			            <xsl:for-each select="negeso:billing-contact">
			            	<xsl:call-template name = "pm.render_contact">
								<xsl:with-param name="prefix" select='"bi"'/>
			            	</xsl:call-template>
			            </xsl:for-each>
		        	</fieldset>
        		</xsl:otherwise>
        	</xsl:choose-->
            </td>
        </tr>
        <tr>
            <td class="admTableFooter" >&#160;</td>
        </tr>

    </xsl:template>

    <xsl:template name="pm.render_contact">
        <xsl:param name="prefix" />
        <table id="{$prefix}_table" border="0" width="100%">

            <tr>
                <td align="center"  bgcolor="#FFFFFF"  class="admTDtitles" style="height:auto;" colspan="3">
                    <xsl:value-of select="java:getString($dict_product, 'SHIPPING_CONTACT')"/>:
                </td>
            </tr>         
            

            <tr>
                <th  class="admTableTD admWidth150">
                    <xsl:value-of select="java:getString($dict_common, 'FIRST_NAME')"/>
                </th>
                <th  class="admTableTDLast">
                    <input class="admTextArea admWidth200"
                        type="text"
                        name="{$prefix}_first_name"
                        data_type="text"
    			>
                        <xsl:attribute name="uname">
                            <xsl:value-of select="java:getString($dict_common, 'FIRST_NAME')"/>
                        </xsl:attribute>
                        <xsl:attribute name="value">
                            <xsl:value-of select="@first-name"/>
                        </xsl:attribute>
                    </input>
                </th>
            </tr>
            <tr>
                <th  class="admTableTD admWidth150">
                    <xsl:value-of select="java:getString($dict_common, 'SECOND_NAME')"/>
                </th>
                <th  class="admTableTDLast">
                    <input class="admTextArea admWidth200"
                        type="text"
                        name="{$prefix}_second_name"
                        data_type="text"
    			>
                        <xsl:attribute name="uname">
                            <xsl:value-of select="java:getString($dict_common, 'SECOND_NAME')"/>
                        </xsl:attribute>
                        <xsl:attribute name="value">
                            <xsl:value-of select="@second-name"/>
                        </xsl:attribute>
                    </input>
                </th>
            </tr>
            <tr>
                <th  class="admTableTD admWidth150">
                    <xsl:value-of select="java:getString($dict_common, 'COMPANY_NAME')"/>
                </th>
                <th  class="admTableTDLast">
                    <input class="admTextArea admWidth200"
                        type="text"
                        name="{$prefix}_company_name"
                        data_type="text"
    			>
                        <xsl:attribute name="uname">
                            <xsl:value-of select="java:getString($dict_common, 'COMPANY_NAME')"/>
                        </xsl:attribute>
                        <xsl:attribute name="value">
                            <xsl:value-of select="@company-name"/>
                        </xsl:attribute>
                    </input>
                </th>
            </tr>
            <tr>
                <th  class="admTableTD admWidth150">
                    <xsl:value-of select="java:getString($dict_common, 'ADDRESS_LINE')"/>
                </th>
                <th  class="admTableTDLast">
                    <input class="admTextArea admWidth200"
                        type="text"
                        name="{$prefix}_address_line"
                        data_type="text"
    			>
                        <xsl:attribute name="uname">
                            <xsl:value-of select="java:getString($dict_common, 'ADDRESS_LINE')"/>
                        </xsl:attribute>
                        <xsl:attribute name="value">
                            <xsl:value-of select="@address-line"/>
                        </xsl:attribute>
                    </input>
                </th>
            </tr>
            <tr>
                <th  class="admTableTD admWidth150">
                    <xsl:value-of select="java:getString($dict_common, 'CITY')"/>
                </th>
                <th  class="admTableTDLast">
                    <input class="admTextArea admWidth200"
                        type="text"
                        name="{$prefix}_city"
                        data_type="text"
    			>
                        <xsl:attribute name="uname">
                            <xsl:value-of select="java:getString($dict_common, 'CITY')"/>
                        </xsl:attribute>
                        <xsl:attribute name="value">
                            <xsl:value-of select="@city"/>
                        </xsl:attribute>
                    </input>
                </th>
            </tr>
            <tr>
                <th  class="admTableTD admWidth150">
                    <xsl:value-of select="java:getString($dict_common, 'ZIP_CODE')"/>
                </th>
                <th  class="admTableTDLast">
                    <input class="admTextArea admWidth200"
                        type="text"
                        name="{$prefix}_zip_code"
                        data_type="text"
    			>
                        <xsl:attribute name="uname">
                            <xsl:value-of select="java:getString($dict_common, 'ZIP_CODE')"/>
                        </xsl:attribute>
                        <xsl:attribute name="value">
                            <xsl:value-of select="@zip-code"/>
                        </xsl:attribute>
                    </input>
                </th>
            </tr>
            <tr>
                <th  class="admTableTD admWidth150">
                    <xsl:value-of select="java:getString($dict_common, 'COUNTRY')"/>
                </th>
                <th  class="admTableTDLast">
                    <input class="admTextArea admWidth200"
                        type="text"
                        name="{$prefix}_country"
                        data_type="text"
    			>
                        <xsl:attribute name="uname">
                            <xsl:value-of select="java:getString($dict_common, 'COUNTRY')"/>
                        </xsl:attribute>
                        <xsl:attribute name="value">
                            <xsl:value-of select="@country"/>
                        </xsl:attribute>
                    </input>
                </th>
            </tr>
            <tr>
                <th  class="admTableTD admWidth150">
                    <xsl:value-of select="java:getString($dict_common, 'PHONE')"/>
                </th>
                <th  class="admTableTDLast">
                    <input class="admTextArea admWidth200"
                        type="text"
                        name="{$prefix}_phone"
                        data_type="text"
    			>
                        <xsl:attribute name="uname">
                            <xsl:value-of select="java:getString($dict_common, 'PHONE')"/>
                        </xsl:attribute>
                        <xsl:attribute name="value">
                            <xsl:value-of select="@phone"/>
                        </xsl:attribute>
                    </input>
                </th>
            </tr>
            <tr>
                <th  class="admTableTD admWidth150">
                    <xsl:value-of select="java:getString($dict_common, 'FAX')"/>
                </th>
                <th  class="admTableTDLast">
                    <input class="admTextArea admWidth200"
                        type="text"
                        name="{$prefix}_fax"
                        data_type="text"
    			>
                        <xsl:attribute name="uname">
                            <xsl:value-of select="java:getString($dict_common, 'FAX')"/>
                        </xsl:attribute>
                        <xsl:attribute name="value">
                            <xsl:value-of select="@fax"/>
                        </xsl:attribute>
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

                    <!-- Unactive pathe element - make it link-->
                    <span>
                        <a  class="admNavigation" href="?command=pm-manage-customers">
                            <xsl:value-of select="java:getString($dict_product, 'CUSTOMERS')"/>
                        </a>
                        &#160; <img src="/images/navig_arrow.png"  style="vertical-align:middle;"/>&#160;
                    </span>

                    <!-- Active pathe element - just print it-->
                    <span class="admNavigation" style="text-decoration:none;">
                        <xsl:value-of select="negeso:customer/@login"/>
                    </span>
                </td>
            </tr>
        </table>
    </xsl:template>

   

    <!-- ******************************* Error message ********************************** -->
    <xsl:template match="errorMessage">
        <xsl:value-of select="errorMessage"/>
    </xsl:template>

</xsl:stylesheet>
