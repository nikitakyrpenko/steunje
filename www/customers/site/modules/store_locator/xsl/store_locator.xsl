<?xml version="1.0" encoding="UTF-8"?>
<!--

  Copyright (c) 2003 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential
  Information and shall use it only in accordance with the terms of the
  license agreement you entered into with Negeso.


  Common xsl template for Store Locator module

  @version		$Revision: 7$
  @author		Alexander Shkabarnya
  @version		16/12/2005
  @author		Volodymyr Snigur
-->


<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">


<xsl:variable name="lang" select="/negeso:page/@lang"/>
<xsl:variable name="dict_store_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('store_locator', $lang)"/>


<xsl:template match="negeso:store_locator" >
	<xsl:if test="$outputType = 'admin' and not(/negeso:page/@role-id = 'visitor')">
		<img src="/images/mark_1.gif" style="cursor: hand;" onClick="window.open('store_locator', '_blank', 'top=50,left=50,height=600,width=800,status=0,toolbar=0,menubar=0,location=0,resizable=1,scrollbars=yes')"/>
	</xsl:if>
 <form id="sl_form" method="post" enctype="multipart/form-data">
     <input type="hidden" id="todo" name="todo"/>
     <input type="hidden" id="sl_criteria" name="sl_criteria">
         <xsl:attribute name="value"><xsl:value-of select="@sl_criteria"/></xsl:attribute>
     </input>
     <input type="hidden" id="sl_chosen_1" name="sl_chosen_1">
        <xsl:attribute name="value"><xsl:value-of select="@sl_chosen_1"/></xsl:attribute>
     </input>
     <input type="hidden" id="sl_chosen_2" name="sl_chosen_2">
        <xsl:attribute name="value"><xsl:value-of select="@sl_chosen_2"/></xsl:attribute>
     </input>
     <input type="hidden" id="sl_extra" name="sl_extra"/>
 </form>
 <xsl:choose>
     <xsl:when test="@mode='search'">
         <table border="0" width="580" cellpadding="0" cellspacing="0">
         	<tr>
         		<td colspan="2" class="store_invitation">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_store_module"/>
						<xsl:with-param name ="name"  select="'STORE_INVITATION'"/>
					</xsl:call-template>
		         	<xsl:value-of select="java:getString($dict_store_module, 'STORE_INVITATION')"/>
         		</td>
         	</tr>
            <tr>
                <td width="250" class="store_city">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_store_module"/>
						<xsl:with-param name ="name"  select="'STORE_CITY'"/>
					</xsl:call-template>
                	<input type="radio" checked="true" name="sl_chosen" onclick="enableDisable()" />
                	<xsl:value-of select="java:getString($dict_store_module, 'STORE_CITY')"/>
                </td>
                <td width="330" class="store_city">
                	<select name="sl_city" id="sl_city" class="store_dropdown">
                		<xsl:apply-templates select="negeso:sl_city_list/negeso:sl_city"/>
                	</select>
                </td>
            </tr>
            <tr>
                <td class="store_city">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_store_module"/>
						<xsl:with-param name ="name"  select="'STORE_POSTCODE'"/>
					</xsl:call-template>
                	<input type="radio" name="sl_chosen" onclick="enableDisable()" />
                	<xsl:value-of select="java:getString($dict_store_module, 'STORE_POSTCODE')"/>
                </td>
                <td id="sltd" disabled="true"  class="store_city">
                	<input id="sl_from" maxLength="4" class="postcode_input" disabled="true"/>
                </td>
            </tr>
            <tr>
                <td colspan="2" class="store_button_td">
			 <button onclick="slFind();" class="store_button">
				 <xsl:call-template name ="add-constant-info">
					 <xsl:with-param name ="dict"  select="$dict_store_module"/>
					 <xsl:with-param name ="name"  select="'STORE_FIND'"/>
				 </xsl:call-template>
			 	<xsl:value-of select="java:getString($dict_store_module, 'STORE_FIND')"/>
			 </button>
                </td>
            </tr>
         </table>
         <script> enableDisable();</script>
     </xsl:when>
     <xsl:when test="@mode='show'">
         <table border="0" width="580" cellpadding="0" cellspacing="0">
            <tr>
                <td colspan="4" class="store_nearest">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_store_module"/>
						<xsl:with-param name ="name"  select="'STORE_NEAREST'"/>
					</xsl:call-template>
                	<xsl:value-of select="java:getString($dict_store_module, 'STORE_NEAREST')"/>&#160;<xsl:value-of select="negeso:sl_shop_list/@region"/>:
                </td>
            </tr>

             <tr>
                 <td class="store_header" style="width: 128px;">
					 <xsl:call-template name ="add-constant-info">
						 <xsl:with-param name ="dict"  select="$dict_store_module"/>
						 <xsl:with-param name ="name"  select="'STORE_STORE'"/>
					 </xsl:call-template>
                 	<xsl:value-of select="java:getString($dict_store_module, 'STORE_STORE')"/>
                 </td>
                 <td class="store_header" style="width: 132px;">
					 <xsl:call-template name ="add-constant-info">
						 <xsl:with-param name ="dict"  select="$dict_store_module"/>
						 <xsl:with-param name ="name"  select="'STORE_CITY'"/>
					 </xsl:call-template>
                 	<xsl:value-of select="java:getString($dict_store_module, 'STORE_CITY')"/>
                 </td>
                 <td class="store_header" style="width: 131px;">
					 <xsl:call-template name ="add-constant-info">
						 <xsl:with-param name ="dict"  select="$dict_store_module"/>
						 <xsl:with-param name ="name"  select="'STORE_ADDRESS'"/>
					 </xsl:call-template>
                 	<xsl:value-of select="java:getString($dict_store_module, 'STORE_ADDRESS')"/>
                 </td>
                 <td class="store_header" style="width: 128px;">
                 	&#160;
                 </td>
             </tr>
            <xsl:apply-templates select="negeso:sl_shop_list/negeso:sl_shop"/>
            <tr>
                <td colspan="4" class="store_back_td">
			<button onclick="slBack1();" class="store_button">
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_store_module"/>
					<xsl:with-param name ="name"  select="'STORE_BACK'"/>
				</xsl:call-template>
				<xsl:value-of select="java:getString($dict_store_module, 'STORE_BACK')"/>
			</button>
                </td>
            </tr>
         </table>
     </xsl:when>
     <xsl:when test="@mode='detail'">
	     <table border="0" width="570" cellpadding="0" cellspacing="0">
	            <tr>
	                <td colspan="2" class="store_nearest">
				<xsl:value-of select="negeso:sl_detail/@company_name"/>
	                </td>
	            </tr>
	            <tr>
	                <td class="store_header_details" style="width: 140px;">
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_store_module"/>
							<xsl:with-param name ="name"  select="'STORE_CITY'"/>
						</xsl:call-template>
	                	<xsl:value-of select="java:getString($dict_store_module, 'STORE_CITY')"/>:
	                </td>
	                <td class="store_details">
	                	<xsl:value-of select="negeso:sl_detail/@city"/>&#160;
	                </td>
	            </tr>
	            <tr>
	                <td class="store_header_details">
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_store_module"/>
							<xsl:with-param name ="name"  select="'STORE_ADDRESS'"/>
						</xsl:call-template>
	                	<xsl:value-of select="java:getString($dict_store_module, 'STORE_ADDRESS')"/>:
	                </td>
	                <td class="store_details">
	                	<xsl:value-of select="negeso:sl_detail/@address_line"/>
	                </td>
	            </tr>
	            <tr>
	                <td class="store_header_details">
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_store_module"/>
							<xsl:with-param name ="name"  select="'STORE_POSTCODE'"/>
						</xsl:call-template>
	                	<xsl:value-of select="java:getString($dict_store_module, 'STORE_POSTCODE')"/>:
	                </td>
	                <td class="store_details">
	                	<xsl:value-of select="negeso:sl_detail/@zip_code"/>
	                </td>
	            </tr>
	            <tr>
	                <td class="store_header_details">
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_store_module"/>
							<xsl:with-param name ="name"  select="'STORE_PHONE'"/>
						</xsl:call-template>
	                	<xsl:value-of select="java:getString($dict_store_module, 'STORE_PHONE')"/>:
	                </td>
	                <td class="store_details">
	                	<xsl:value-of select="negeso:sl_detail/@phone"/>
	                </td>
	            </tr>
	            <xsl:if test="negeso:sl_detail/@link!=''">
	            <tr>
	                <td class="store_header_details">
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_store_module"/>
							<xsl:with-param name ="name"  select="'STORE_WEBLINK'"/>
						</xsl:call-template>
	                	<xsl:value-of select="java:getString($dict_store_module, 'STORE_WEBLINK')"/>:
	                </td>
	                <td class="store_details">
	                    
	                        <a target="blank">
	                            <xsl:attribute name="href"><xsl:value-of select="negeso:sl_detail/@link"/></xsl:attribute>
	                        <xsl:value-of select="negeso:sl_detail/@link"/>
	                        </a>
	                </td>
	            </tr>
	            </xsl:if>
	            <tr>
	                <td class="store_header_details">
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_store_module"/>
							<xsl:with-param name ="name"  select="'STORE_EMAIL'"/>
						</xsl:call-template>
	                	<xsl:value-of select="java:getString($dict_store_module, 'STORE_EMAIL')"/>:
	                </td>
	                <td class="store_details">
	                <xsl:if test="negeso:sl_detail/@email!=''">
	                        <a >
	                            <xsl:attribute name="href">mailto:<xsl:value-of select="negeso:sl_detail/@email"/></xsl:attribute>
	                        <xsl:value-of select="negeso:sl_detail/@email"/>
	                        </a>
	                </xsl:if>
	                </td>
	            </tr>
	            <tr>
	                <td class="store_header_details" style="border-bottom: 1px solid #E1E1E1;">
						<xsl:call-template name ="add-constant-info">
							<xsl:with-param name ="dict"  select="$dict_store_module"/>
							<xsl:with-param name ="name"  select="'STORE_PATH'"/>
						</xsl:call-template>
	                	<xsl:value-of select="java:getString($dict_store_module, 'STORE_PATH')"/>:
	                </td>
	                <td class="store_details" style="border-bottom: 1px solid #E1E1E1;">
	                <xsl:if test="negeso:sl_detail/@path!=''">
	                        <img style="padding-top: 12px; padding-bottom: 12px;">
	                            <xsl:attribute name="src"><xsl:value-of select="negeso:sl_detail/@path"/></xsl:attribute>
	                        </img>
	                </xsl:if>
	                </td>
	            </tr>
	            <tr>
	                <td colspan="2" class="store_back_td">
				<button onclick="slBack2();"  class="store_button">
					<xsl:call-template name ="add-constant-info">
						<xsl:with-param name ="dict"  select="$dict_store_module"/>
						<xsl:with-param name ="name"  select="'STORE_BACK'"/>
					</xsl:call-template>
					<xsl:value-of select="java:getString($dict_store_module, 'STORE_BACK')"/>
				</button>
	                </td>
	            </tr>
        </table>
        <br/>
     </xsl:when>
     <xsl:otherwise>

     </xsl:otherwise>
 </xsl:choose>

</xsl:template>

<xsl:template match="negeso:sl_city" >
    <option>
        <xsl:attribute name="value"><xsl:value-of select="@id"/></xsl:attribute>
        <xsl:value-of select="@title"/>
    </option>
</xsl:template>

<xsl:template match="negeso:sl_shop" >
    <tr>
        <td class="store_info">
        	<xsl:value-of select="@company_name"/>
        </td>
        <td class="store_info">
        	<xsl:value-of select="@city"/>&#160;
        </td>
        <td class="store_info">
        	<xsl:value-of select="@address_line"/>
        </td>
        <td class="store_info" style="padding-left: 20px;">
        	<button class="store_button">
				<xsl:call-template name ="add-constant-info">
					<xsl:with-param name ="dict"  select="$dict_store_module"/>
					<xsl:with-param name ="name"  select="'STORE_DETAILS'"/>
				</xsl:call-template>
	            	<xsl:attribute name="onclick">slDetail(<xsl:value-of select="@id"/>);</xsl:attribute>
			<xsl:value-of select="java:getString($dict_store_module, 'STORE_DETAILS')"/>
        	</button>
        </td>
    </tr>
</xsl:template>

</xsl:stylesheet>