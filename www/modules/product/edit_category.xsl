<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${edit_item.xsl}

  Copyright (c) 2004 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential
  Information and shall use it only in accordance with the terms of the
  license agreement you entered into with Negeso.

  View of a list item edit interface.

  @version      2004.06.01
  @author       Olexiy.Strashko
  @author       Volodymyr Snigur
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:negeso="http://negeso.com/2003/Framework"
	xmlns:java="http://xml.apache.org/xslt/java"
	exclude-result-prefixes="java">

<xsl:include href="/xsl/negeso_body.xsl"/>
<xsl:include href="modules/product/pm_common.xsl"/>

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_product" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_product.xsl', $lang)"/>
<xsl:variable name="dict_prod" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('product', $lang)"/>
<xsl:variable name="is_gm_enabled" select="/negeso:page/negeso:pm-category/@google_merchant_enabled = 'true'"/>

<!-- NEGESO JAVASCRIPT Temaplate -->	
<xsl:template name="java-script">	
	<script type="text/javascript" src="/script/RTE_Adapter.js"></script>
	<script language="JavaScript">
	<xsl:text disable-output-escaping="yes">
	    <![CDATA[
        // Image link value for restore if form reset
        var imageLink = "<xsl:value-of select='negeso:listItem/@imageLink'/>";
        var smallImageLink = "<xsl:value-of select='negeso:listItem/@smallImageLink'/>";
        var smallHighlightImageLink = "<xsl:value-of select='negeso:listItem/@smallHighlightImageLink'/>";
        var unique_error = "<div id='unique_error'><table cellspacing=\"0\" cellpadding=\"0\" style=\"height: 12px; margin: 0; padding: 0; border: 0;\"><tr style=\"height: 12px;\"><td style=\"font-size: 10px; color: red; border: 0; padding: 0; text-align: left; vertical-align: middle; height: 12px;\">" 
        + "Title is not unique"
        + "</td></tr></table><div/>"
        var imgElement = null;
        
        if (imageLink == "")
            imageLink = "/images/0.gif";
            
        if (smallImageLink == "")
            smallImageLink = "/images/0.gif";
            
        if (smallHighlightImageLink == "")
            smallHighlightImageLink = "/images/0.gif";
	    
		function Update(targetId, parentCatId) {
		     $('#unique_error').remove();
            if ( !validateForm(operateFormId) ){
                return false;
            }
            var isUnique = false;
            $.ajax({
                  type: "GET",
                  url: "/admin/pm_check_url?type=category&title=" + $('#titleField').val() + "&langId=" + $('#langIdSelected').val() + "&catId=" + targetId + "&parentCatId=" + parentCatId,
                  async: false,
                  dataType: "html",
                  success: function(html, stat) {
                    if (html == "true")
                        isUnique = true;
                  },
                  error: function(html, stat) { }
              });
            if (!isUnique) {
                $('#titleField').after(unique_error);
                return false;
            }
            document.operateForm.command.value = "pm-update-category";
        	document.operateForm.pmTargetId.value = targetId;
			document.operateForm.langId.value = document.all.langIdSelected.value;
        	return true;
		}
		
		function onChangeLanguage() {
			document.operateForm.langId.value = document.all.langIdSelected.value;
			document.operateForm.command.value = "pm-get-edit-category-page";
			document.operateForm.pmTargetId.value = document.operateForm.pmCatId.value;
			document.operateForm.submit();
			
		}

        function selectImageDialog(thWidth, thHeight, imageLink, imageElement){
			result = MediaCatalog.selectImageDialog(thWidth, thHeight, imageElement.id);
			imgElement = imageElement.id;
            if (result != null){
                if (result.resCode == "OK"){
                    imageLink.value = result.realImage;
                    imageElement.outerHTML = 
                        "<img hspace='5' vspace='5' id='" + imageElement.id + "' name='" + 
                        imageElement.name + "' " + "src='../" + result.realImage +"'>";
                }
            }
        }
        
        
        function resultUploadImage(){
	        var result = returnValue;
	        document.getElementById(imgElement).src = result.realImage;
	        document.getElementById(imgElement+'Link').value = result.realImage;
        }

		function resetImage(imageVar, imageElement) {
            if (imageVar != '/images/0.gif') {
                imageElement.outerHTML = 
                 "<img hspace='5' vspace='5' id='" + imageElement.id + "' name='" + 
                 imageElement.name + "' " + "src='../" + imageVar +"'>";
            }
            else {
                imageElement.outerHTML = 
                    "<img id='" + imageElement.id + "' src='../" + imageVar +"'>";
            }
		}

        function onReset() {
			resetImage(smallImageLink, this.form.smallImage);
			resetImage(smallHighlightImageLink, this.form.smallHighlightImage);
			resetImage(imageLink, this.form.bigImage);
        }
        
        function clearImageLink(imageLink, imageElement) {
            imageLink.value = "";
            imageElement.outerHTML = 
                "<img id='" +  imageElement.id + "' name= '" + imageElement.name +   "' src='/images/0.gif'>";
        }

		function resetForm()
		{
			var backupArticle = $("input[name^='backup']"); //doing backup of article before changes
			var resetForm = document.getElementById('operateFormId'); //select main form
			var realArticle = $("div[id^='article_text']");  //select div with article text
			var articleId = $("input[name^='article_text']");  //select id of the article
			for (var i=0; i < backupArticle.length; i++)
			{
				realArticle[i].innerHTML = backupArticle[i].value; //on click to "reset" button change value of article's div to beginning value
			}
			resetForm.reset();  //reset the other fields of the form
		}
		
		function selectAllLangs() {
		   $("#langs").find("INPUT[type='checkbox']").each(function(){
		      $(this).attr('checked', $('#globalChecker').is(':checked'));		   
		   });
		}
        function show_hide_gm_props(el) {
        	if ($(el).is(':checked')) {
        		$('#gm_properties_table').fadeIn(500,function() {});
        	} else {
        		$('#gm_properties_table').fadeOut(500,function() {});
        	}
        }
		]]>
		</xsl:text>
	</script>
</xsl:template>    

<xsl:template match="/" >
<html>
<head>
    <title><xsl:value-of select="java:getString($dict_common, 'EDIT_CATEGORY')"/></title>
	<META http-equiv="Content-Type" content="text/html; charset=utf-8"/>    
    <link rel="stylesheet" type="text/css" href="/css/jquery-ui.custom.css"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="/script/jquery.min.js"/>
    <script type="text/javascript" src="/script/jquery-ui.custom.min.js" />
    <script type="text/javascript" src="/script/cufon-yui.js"/>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"/>
    <script type="text/javascript" src="/script/common_functions.js"/>    
    <script type="text/javascript" src="/script/media_catalog.js"/>
    <xsl:call-template name="java-script"/>
	
</head>
<body>
    <!-- NEGESO HEADER -->
	 <xsl:call-template name="NegesoBody">
      <xsl:with-param name="helpLink">
          <xsl:text>/admin/help/cpr1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
      </xsl:with-param>
      <xsl:with-param name="backLink" select="concat('?command=pm-browse-category&amp;pmCatId=', negeso:pm-category/@parent-id)"/>
	 </xsl:call-template>  
</body>
</html>
</xsl:template>

<xsl:template match="negeso:page" mode="admContent">
    <form method="POST" name="operateForm" id="operateFormId" action="" enctype="multipart/form-data"> 
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
                <td align="center" class="admNavPanelFont" >
                    <!-- TITLE -->
                    <xsl:call-template name="tableTitle">
                        <xsl:with-param name="headtext">
                            <xsl:choose>
                                <xsl:when test="/negeso:page/negeso:pm-category/@new='true'">
                                    <xsl:value-of select="java:getString($dict_product, 'PRODUCT_MODULE')"/>
                                    <xsl:text>. </xsl:text>
                                    <xsl:value-of select="java:getString($dict_common, 'NEW_CATEGORY')"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="java:getString($dict_product, 'PRODUCT_MODULE')"/>
                                    <xsl:text>. </xsl:text>
                                    <xsl:value-of select="java:getString($dict_common, 'EDIT_CATEGORY')"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:with-param>
                    </xsl:call-template>
                </td>
            </tr>
            <tr>
                <td>
                    <table cellpadding="0" cellspacing="0" width="100%">
                        <tr>
                            <th  class="admTableTDLast" colspan="2">
                                <xsl:value-of select="java:getString($dict_common, 'LANGUAGE_VERSION')"/>&#160;<xsl:apply-templates select="negeso:languages"/>
                            </th>
                        </tr>
                    </table>
                    <table  cellspacing="0" cellpadding="0"  width="100%">
                        <xsl:apply-templates select="negeso:pm-category"/>
                    </table>
                </td>
            </tr>
            <tr>
                <th  class="admTableTDLast" colspan="2">
                    <xsl:choose>
                        <xsl:when test="/negeso:page/negeso:pm-category/@new='true'">
                            <input class="admNavbarInp" name="copyAllLanguages" type="checkbox" checked="true" disabled="true" value="true">
                                <xsl:value-of select="java:getString($dict_product, 'COPY_TO_ALL_LANGUAGES')"/>
                            </input>                       
                        </xsl:when>
                        <xsl:otherwise>
                            <input class="admNavbarInp" name="copyAllLanguages" type="checkbox" value="true">
                                <xsl:value-of select="java:getString($dict_product, 'COPY_TO_ALL_LANGUAGES')"/>
                            </input>
                        </xsl:otherwise>
                    </xsl:choose>               
                </th>
            </tr>
            <tr>
                <td>
                	<table  cellspacing="0" cellpadding="0" width="100%">
		            	<tr>
			                <td class="admTDtitles" align="center" colspan="2">
			                    <xsl:value-of select="java:getString($dict_prod, 'PM_GM_PROPERTIES')"/>:
			                </td>
		            	</tr>
		            	<tr>
			            	<th class="admTableTDLast admWidth150" >
			                    <xsl:value-of select="java:getString($dict_prod, 'PM_GM_ENABLED')"/>
			                </th>
			                <td class="admTableTDLast">
			                    <input type="checkbox" name="google_merchant_enabled" value="true" style="background-color: white; margin-left: 5px" onchange="show_hide_gm_props(this)">
			                    	<xsl:if test="$is_gm_enabled"><xsl:attribute name="checked">checked</xsl:attribute></xsl:if>                            
			                    </input>
			                </td>
		                </tr>
		            </table>
                	<table  cellspacing="0" cellpadding="0" width="100%" id="gm_properties_table">
			           	<xsl:if test="not($is_gm_enabled)">
			           		<xsl:attribute name="style">display:none</xsl:attribute>
			           	</xsl:if>
		            	<tr>
			            	<th class="admTableTDLast admWidth150" >
			                    <xsl:value-of select="java:getString($dict_prod, 'PM_GM_BRAND')"/>
			                </th>
			                <td class="admTableTDLast" colspan="2">
			                    <input type="text" name="google_merchant_brand" value="{/negeso:page/negeso:pm-category/@google_merchant_brand}" class="admTextArea"/>
			                </td>
		                </tr>
		            	<tr>
			            	<th class="admTableTDLast admWidth150" >
			                    <xsl:value-of select="java:getString($dict_prod, 'PM_GM_CONDITION')"/>
			                </th>
			                <td class="admTableTDLast" colspan="2">
			                    <select name="google_merchant_condition" class="admInpText">
			                    	<xsl:call-template name="createSimpleOption">
			                    		<xsl:with-param name="optionValue" select="''"/>
			                    		<xsl:with-param name="currentValue" select="/negeso:page/negeso:pm-category/@google_merchant_condition"/>
			                    	</xsl:call-template>
			                    	<xsl:call-template name="createSimpleOption">
			                    		<xsl:with-param name="optionValue" select="'new'"/>
			                    		<xsl:with-param name="currentValue" select="/negeso:page/negeso:pm-category/@google_merchant_condition"/>
			                    	</xsl:call-template>
			                    	<xsl:call-template name="createSimpleOption">
			                    		<xsl:with-param name="optionValue" select="'refurbished'"/>
			                    		<xsl:with-param name="currentValue" select="/negeso:page/negeso:pm-category/@google_merchant_condition"/>
			                    	</xsl:call-template>
			                    	<xsl:call-template name="createSimpleOption">
			                    		<xsl:with-param name="optionValue" select="'used'"/>
			                    		<xsl:with-param name="currentValue" select="/negeso:page/negeso:pm-category/@google_merchant_condition"/>
			                    	</xsl:call-template>
			                    </select>
			                </td>
		                </tr>
		            	<tr>
			            	<th class="admTableTDLast admWidth150" >
			                    <xsl:value-of select="java:getString($dict_prod, 'PM_GM_AVAILABILITY')"/>
			                </th>
			                <td class="admTableTDLast" colspan="2">
			                    <select name="google_merchant_availability" class="admInpText">
			                    	<xsl:call-template name="createSimpleOption">
			                    		<xsl:with-param name="optionValue" select="''"/>
			                    		<xsl:with-param name="currentValue" select="/negeso:page/negeso:pm-category/@google_merchant_availability"/>
			                    	</xsl:call-template>
			                    	<xsl:call-template name="createSimpleOption">
			                    		<xsl:with-param name="optionValue" select="'in stock'"/>
			                    		<xsl:with-param name="currentValue" select="/negeso:page/negeso:pm-category/@google_merchant_availability"/>
			                    	</xsl:call-template>
			                    	<xsl:call-template name="createSimpleOption">
			                    		<xsl:with-param name="optionValue" select="'available for order'"/>
			                    		<xsl:with-param name="currentValue" select="/negeso:page/negeso:pm-category/@google_merchant_availability"/>
			                    	</xsl:call-template>
			                    	<xsl:call-template name="createSimpleOption">
			                    		<xsl:with-param name="optionValue" select="'out of stock'"/>
			                    		<xsl:with-param name="currentValue" select="/negeso:page/negeso:pm-category/@google_merchant_availability"/>
			                    	</xsl:call-template>
			                    	<xsl:call-template name="createSimpleOption">
			                    		<xsl:with-param name="optionValue" select="'preorder'"/>
			                    		<xsl:with-param name="currentValue" select="/negeso:page/negeso:pm-category/@google_merchant_availability"/>
			                    	</xsl:call-template>
			                    </select>
			                </td>
		                </tr>
		            	<tr>
			            	<th class="admTableTDLast admWidth150" >
			                    <xsl:value-of select="java:getString($dict_prod, 'PM_GM_PRODUCT_CATEGORY')"/>
			                </th>
			                <td class="admTableTDLast">
			                    <input type="text" name="google_merchant_product_category" value="{/negeso:page/negeso:pm-category/@google_merchant_product_category}" class="admTextArea"/>
			                </td>
			                 <td class="admTableTDLast" rowspan="2">
			                    <a href="http://www.google.com/basepages/producttype/taxonomy.nl-NL.xls" target="_blank">taxonomy.nl-NL.xls</a><br/>
			                    <a href="http://www.google.com/basepages/producttype/taxonomy.nl-NL.txt" target="_blank">taxonomy.nl-NL.txt</a><br/>
			                    <a href="http://www.google.com/basepages/producttype/taxonomy.en-US.xls" target="_blank">taxonomy.en-US.xls</a><br/>
			                    <a href="http://www.google.com/basepages/producttype/taxonomy.en-US.txt" target="_blank">taxonomy.en-US.txt</a>
			                 </td>
		                </tr>
		                <tr>
			            	<th class="admTableTDLast admWidth150" >
			                    <xsl:value-of select="java:getString($dict_prod, 'PM_GM_PRODUCT_TYPE')"/>
			                </th>
			                <td class="admTableTDLast">
			                    <input type="text" name="google_merchant_product_type" value="{/negeso:page/negeso:pm-category/@google_merchant_product_type}" class="admTextArea"/>
			                </td>
		                </tr>
		                <tr>
			            	<th class="admTableTDLast admWidth150" >
			                    <xsl:value-of select="java:getString($dict_prod, 'PM_GM_GTIN')"/>
			                </th>
			                <td class="admTableTDLast" colspan="2">
			                    <input type="text" name="google_merchant_gtin" value="{/negeso:page/negeso:pm-category/@google_merchant_gtin}" class="admTextArea"/>
			                </td>
		                </tr>
		                <tr>
			            	<th class="admTableTDLast admWidth150" >
			                    <xsl:value-of select="java:getString($dict_prod, 'PM_GM_TITLE')"/>
			                </th>
			                <td class="admTableTDLast" colspan="2">
			                    <input type="text" name="google_merchant_title" value="{/negeso:page/negeso:pm-category/@google_merchant_title}" class="admTextArea"/>
			                </td>
		                </tr>
		                <tr>
			            	<th class="admTableTDLast admWidth150" >
			                    <xsl:value-of select="java:getString($dict_prod, 'PM_GM_DESCRIPTION')"/>
			                </th>
			                <td class="admTableTDLast" colspan="2">
			                	<textarea rows="3" type="text" name="google_merchant_description" class="admTextArea">
			                		<xsl:value-of select="/negeso:page/negeso:pm-category/@google_merchant_description"/>
			                	</textarea>
			                </td>
		                </tr>
		                <tr>
			            	<th class="admTableTDLast admWidth150" >
			                    <xsl:value-of select="java:getString($dict_prod, 'PM_GM_PRICE')"/>
			                </th>
			                <td class="admTableTDLast" colspan="2">
			                    <input type="text" name="google_merchant_price" value="{/negeso:page/negeso:pm-category/@google_merchant_price}" class="admTextArea" data_type="currency"/>
			                </td>
		                </tr>
		            </table>
                </td>
            </tr> 
            <tr>
                <td class="admTableFooter" >&#160;</td>
            </tr>
        </table>
		<!-- Update/reset fields -->
        <table cellpadding="0" cellspacing="0"  width="764px" align="center" class="Buttons">
            <tr>
                <td>
                    <div class="admBtnGreenb">
                        <div class="imgL"></div>
                        <div>
                            <xsl:choose>
                                <xsl:when test="/negeso:page/negeso:pm-category/@new='true'">
                                    <input class="admNavbarInp" name="saveButton" onClick="return Update('{/negeso:page/negeso:pm-category/@id}','{/negeso:page/negeso:pm-category/@parent-id}');" type="submit">
                                        <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'ADD')"/></xsl:attribute>
                                    </input>
                                </xsl:when>
                                <xsl:otherwise>
                                    <input class="admNavbarInp" name="saveButton" onClick="return Update('{/negeso:page/negeso:pm-category/@id}','{/negeso:page/negeso:pm-category/@parent-id}')" type="submit">
                                        <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'SAVE')"/></xsl:attribute>
                                    </input>                              
                                </xsl:otherwise>
                            </xsl:choose>
                        </div>
                        <div class="imgR"></div>
                    </div>
                    <div class="admBtnGreenb admBtnBlueb">
                        <div class="imgL"></div>
                        <div>
                            <input class="admNavbarInp" type="button" onClick="resetForm();">
                                <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'RESET')"/></xsl:attribute>
                            </input>
                        </div>
                        <div class="imgR"></div>
                    </div>
                </td>
            </tr>
        </table>
    </form>
	<xsl:apply-templates select ="//negeso:pm-category/negeso:article" mode="formOutArtilce" />
</xsl:template>

<xsl:template match="negeso:article" mode="formOutArtilce">
	<div onpropertychange="pushContent(this)" head="{negeso:head/text()}" id="article_text_{negeso:head/text()}_div" class="contentStyle hiddenArtilce" style="border: 1px solid #848484;">
		<xsl:choose>
			<xsl:when test="negeso:text/text()">
				<xsl:value-of select="negeso:text" disable-output-escaping="yes"/>
			</xsl:when>
			<xsl:otherwise>&#160;</xsl:otherwise>
		</xsl:choose>
	</div>
	<script>
		pushContent(document.getElementById('article_text_<xsl:value-of select ="negeso:head/text()"/>_div'));
	</script>
</xsl:template>

	<!-- ********************************** Category *********************************** -->
<xsl:template match="negeso:pm-category">
    <tr>
        <td>
            <xsl:choose>
                <xsl:when test="@new='true'">
                    <input type="hidden" name="pmCatId" value="{@parent-id}"></input>
                    <input type="hidden" name="updateTypeField" value="insert"></input>
                </xsl:when>
                <xsl:otherwise>
                    <input type="hidden" name="pmCatId" value="{@id}"></input>
                    <input type="hidden" name="updateTypeField" value="update"></input>
                </xsl:otherwise>
            </xsl:choose>
            <input type="hidden" name="command" value="pm-browse-category"></input>
            <input type="hidden" name="type" value="none"></input>
            <input type="hidden" name="langId" value="{/negeso:page/@lang-id}"></input>
            <input type="hidden" name="pmTargetId" value="-1"></input>
            <table cellspacing="0" cellpadding="0" width="100%">
                <td class="admTDtitles" colspan="2" align="center">
                    <xsl:value-of select="java:getString($dict_common, 'CATEGORY_PROPERTIES')"/>:
                </td>
                <!-- Title -->
                <tr>
                    <th class="admTableTDLast admWidth150" >
                        <xsl:value-of select="java:getString($dict_common, 'TITLE')"/>*
                    </th>
                    <td class="admTableTDLast">
                        <input class="admTextArea" type="text" name="titleField" id="titleField" data_type="text" required="true" uname="Title">
                            <xsl:attribute name="value"><xsl:value-of select="@title"/></xsl:attribute>
                        </input>
                    </td>
                </tr>
                <!-- Description -->
                <tr>
                    <th class="admTableTDLast">
                        <xsl:value-of select="java:getString($dict_common, 'DESCRIPTION')"/>:
                    </th>
                    <td class="admTableTDLast">
                        <textarea rows="3" class="admTextArea" type="text" name="descriptionField" data_type="text" uname="Description">
                            <xsl:if test="count(@description)=0">&#160;</xsl:if>
                            <xsl:value-of select="@description"/>
                        </textarea>
                    </td>
                </tr>

                <!-- Product type-->
                <tr>
                    <th class="admTableTDLast">Product type:</th>
                    <td class="admTableTDLast">
                        <xsl:choose>
                            <xsl:when test="/negeso:page/negeso:pm-category/@new='true'">
                                <select name="productType" style="background-color: white; margin-left: 5px">
                                    <xsl:for-each	select="/negeso:page/negeso:ProductTypes/negeso:ProductType">
                                        <option>
                                            <xsl:attribute name="value"><xsl:value-of select="@id" /></xsl:attribute>
                                            <xsl:value-of select="." />
                                        </option>
                                    </xsl:for-each>
                                </select>
                            </xsl:when>
                            <xsl:otherwise>
                                <select name="productType" style="background-color: white; margin-left: 5px">
                                    <xsl:attribute name="disabled">disabled</xsl:attribute>
                                    <xsl:for-each	select="/negeso:page/negeso:ProductTypes/negeso:ProductType">
                                        <option>
                                            <xsl:attribute name="value"><xsl:value-of select="@id" /></xsl:attribute>
                                            <xsl:if test="/negeso:page/negeso:pm-category/@productType=@id">
                                                <xsl:attribute name="selected">selected</xsl:attribute>
                                            </xsl:if><xsl:value-of select="." />
                                        </option>
                                    </xsl:for-each>
                                </select>
                            </xsl:otherwise>
                        </xsl:choose>
                    </td>
                </tr>
            </table>
            <table cellspacing="0" cellpadding="0" width="100%">
                <!-- Big image -->
                <tr>
                    <th class="admTableTDLast admWidth150" height="23">
                        <xsl:value-of select="java:getString($dict_common, 'IMAGE')"/>
                    </th>
                    <td class="admTableTDLast" >
                        <div class="admLeft">
                            <img id="bigImage" name="bigImage">
                                <xsl:attribute name="src">
                                    <xsl:choose>
                                        <xsl:when test="@imageLink">../<xsl:value-of select="@imageLink"/></xsl:when>
                                        <xsl:otherwise>
                                            <xsl:text>/images/0.gif</xsl:text>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </xsl:attribute>
                            </img>
                        </div>
                        <input type="text" name="imageLink" id="bigImageLink" readonly="true" class="admTextArea" >
                            <xsl:choose>
                                <xsl:when test="@imageLink">
                                    <xsl:attribute name="value"><xsl:value-of select="@imageLink"/></xsl:attribute>
                                </xsl:when>
                            </xsl:choose>
                        </input>
                        <div class="admNavPanelInp">
                            <div class="imgL"></div>
                            <div>
                                <input name="selectImageButton"
                                    onClick="selectImageDialog({@thumbnail-width}, {@thumbnail-height}, this.form.imageLink, this.form.bigImage)"
                                    type="button"
                                    class="admNavPanelInp">
                                    <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'SELECT')"/></xsl:attribute>
                                </input>
                            </div>
                            <div class="imgR"></div>
                        </div>
                        <div class="admNavPanelGrey">
                            <div class="imgL"></div>
                            <div>
                                <input name="clearImageButton" onClick="clearImageLink(this.form.imageLink, this.form.bigImage)" type="button" class="admNavPanelGrey">
                                    <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'CLEAR')"/></xsl:attribute>
                                    <xsl:choose>
                                        <xsl:when test="@imageLink"></xsl:when>
                                        <xsl:otherwise>
                                            <xsl:attribute name="disabled">true</xsl:attribute>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </input>
                            </div>
                            <div class="imgR"></div>
                        </div>
                    </td>
                </tr>
                <!-- Small image -->
                <tr>
                    <th class="admTableTDLast admWidth150" height="23">
                        <xsl:value-of select="java:getString($dict_product, 'CATEGORY_SMALL_IMAGE')"/>
                    </th>
                    <td class="admTableTDLast" >
                        <div class="admLeft">
                            <img id="smallImage" name="smallImage">
                                <xsl:attribute name="src">
                                    <xsl:choose>
                                        <xsl:when test="@smallImageLink">../<xsl:value-of select="@smallImageLink"/></xsl:when>
                                        <xsl:otherwise>
                                            <xsl:text>/images/0.gif</xsl:text>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </xsl:attribute>
                            </img>
                        </div>
                        <input type="text" name="smallImageLink" id="smallImageLink" readonly="true" class="admTextArea ">
                            <xsl:choose>
                                <xsl:when test="@smallImageLink">
                                    <xsl:attribute name="value"><xsl:value-of select="@smallImageLink"/></xsl:attribute>
                                </xsl:when>
                            </xsl:choose>
                        </input>

                        <div class="admNavPanelInp">
                            <div class="imgL"></div>
                            <div>
                                <input name="selectImageButton"
                                  onClick="selectImageDialog({@smallImage-width}, {@smallImage-height}, this.form.smallImageLink, this.form.smallImage)"
                                  type="button"
                                  class="admNavPanelInp">
                                    <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'SELECT')"/></xsl:attribute>
                                </input>
                            </div>
                            <div class="imgR"></div>
                        </div>
                        <div class="admNavPanelGrey">
                            <div class="imgL"></div>
                            <div>
                                <input name="clearImageButton" onClick="clearImageLink(this.form.smallImageLink, this.form.smallImage)" type="button" class="admNavPanelGrey">
                                    <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'CLEAR')"/></xsl:attribute>
                                    <xsl:choose>
                                        <xsl:when test="@smallImageLink"></xsl:when>
                                        <xsl:otherwise>
                                            <xsl:attribute name="disabled">true</xsl:attribute>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </input>
                            </div>
                            <div class="imgR"></div>
                        </div>
                    </td>
                </tr>
                <!-- Small highlight image -->
                <tr>
                    <th class="admTableTDLast  admWidth150" height="23">
                        <xsl:value-of select="java:getString($dict_product, 'CATEGORY_SMALL_HIGHLIGHT_IMAGE')"/>
                    </th>
                    <td class="admTableTDLast" >
                        <div class="admLeft">
                            <img id="smallHighlightImage" name="smallHighlightImage">
                                <xsl:attribute name="src">
                                    <xsl:choose>
                                        <xsl:when test="@smallHighlightImageLink">../<xsl:value-of select="@smallHighlightImageLink"/></xsl:when>
                                        <xsl:otherwise>
                                            <xsl:text>/images/0.gif</xsl:text>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </xsl:attribute>
                            </img>
                        </div>
                        <input type="text" id="smallHighlightImageLink" name="smallHighlightImageLink" readonly="true" class="admTextArea ">
                            <xsl:choose>
                                <xsl:when test="@smallHighlightImageLink">
                                    <xsl:attribute name="value"><xsl:value-of select="@smallHighlightImageLink"/></xsl:attribute>
                                </xsl:when>
                            </xsl:choose>
                        </input>
                        <div class="admNavPanelInp">
                            <div class="imgL"></div>
                            <div>
                                <input name="selectImageButton"
                                 onClick="selectImageDialog({@smallHighlightImage-width}, {@smallHighlightImage-height}, this.form.smallHighlightImageLink, this.form.smallHighlightImage)"
                                 type="button"
                                 class="admNavPanelInp">
                                    <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'SELECT')"/></xsl:attribute>
                                </input>
                            </div>
                            <div class="imgR"></div>
                        </div>


                        <div class="admNavPanelGrey">
                            <div class="imgL"></div>
                            <div>
                                <input name="clearImageButton" onClick="clearImageLink(this.form.smallHighlightImageLink, this.form.smallHighlightImage)" type="button" class="admNavPanelGrey">
                                    <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'CLEAR')"/></xsl:attribute>
                                    <xsl:choose>
                                        <xsl:when test="@smallHighlightImageLink"></xsl:when>
                                        <xsl:otherwise>
                                            <xsl:attribute name="disabled">true</xsl:attribute>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </input>
                            </div>
                            <div class="imgR"></div>
                        </div>
                    </td>
                </tr>
                <!-- may be: @class='pm' or negeso:head/text()!='Enter your text here' and  -->
                <xsl:for-each select="negeso:article[@class='pm' and @lang=//negeso:page/@lang]">
                    <tr>
                        <th class="admTableTDLast admWidth150">
                            <xsl:value-of select="java:getString($dict_prod, negeso:head/text())"/>
                        </th>
                        <td class="admTableTDLast">
                            <input type="hidden" name="backup" id="@id" value="{negeso:text/text()}"/>
                            <xsl:apply-templates select="." mode="local_save"/>
                        </td>
                    </tr>
                </xsl:for-each>
            </table>
            <table  cellspacing="0" cellpadding="0" width="100%">
                <td class="admTDtitles" colspan="3" align="center">
                    <xsl:value-of select="java:getString($dict_common, 'ADVANCED_PROPERTIES')"/>:
                </td>
                <!-- is-leaf -->
                <tr>
                    <xsl:if test="count(negeso:taxes) > 0">
                        <tr>
                            <th class="admTableTDLast">Tax</th>
                            <td class="admTableTDLast">
                                <xsl:call-template name="pm.tax-chooser"/>
                            </td>
                        </tr>
                    </xsl:if>
                    <th class="admTableTDLast admWidth150">
                        <xsl:value-of select="java:getString($dict_product, 'IS_LEAF_CONTAIN_PRODUCTS')"/>:
                    </th>
                    <td class="admTableTDLast">
                        <input class="admCheckBox" type="checkbox" name="isLeafField" style="margin-left: 2px">
                            <xsl:if test="@has-subcategories='true'">
                                <xsl:attribute name="disabled">true</xsl:attribute>
                            </xsl:if>
                            <xsl:if test="@is-leaf='true'">
                                <xsl:attribute name="checked">true</xsl:attribute>
                            </xsl:if>
                        </input>
                    </td>
                </tr>
                <tr>
                    <th class="admTableTDLast">
                        <xsl:value-of select="java:getString($dict_product, 'SHOW_IN_LANGUAGES')"/>:
                    </th>
                    <td class="admTableTDLast">
                        <table cellspacing="0" cellpadding="0" id="langs">
                            <tr>
                                <td>
                                    <table cellspacing="0" cellpadding="0" width="100%">
                                        <xsl:apply-templates select="negeso:pm-lang-presence"/>
                                    </table>
                                </td>
                                <td>&#160;&#160;&#160;&#160;&#160;&#160;Translate:&#160;&#160;&#160;</td>
                                <td>
                                    <table cellspacing="0" cellpadding="0" wdth="100%">
                                        <xsl:apply-templates select="negeso:pm-lang-presence"  mode="translation"/>
                                    </table>
                                </td>
                                <td>
                                    &#160;&#160;&#160;&#160;&#160;&#160;Select all languages and translations: <input type="checkbox" onClick="selectAllLangs();" id="globalChecker"></input>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <!-- Publish Date -->
                <tr>
                    <th class="admTableTDLast">
                        <xsl:value-of select="java:getString($dict_common, 'PUBLISH_DATE')"/>*
                    </th>
                    <td class="admTableTDLast" >
                        <input class="admTextArea" type="text" name="publishDateField" id="publishDateId" data_type="date" required="true" uname="Publish date" readonly="true">
                            <xsl:attribute name="value"><xsl:value-of select="@publish-date"/></xsl:attribute>
                        </input>
                        (yyyy-mm-dd)
                    </td>
                </tr>
                <!-- Expired Date -->
                <tr>
                    <th class="admTableTDLast">
                        <xsl:value-of select="java:getString($dict_common, 'EXPIRED_DATE')"/>
                    </th>
                    <td class="admTableTDLast" >
                        <input class="admTextArea" type="text" name="expiredDateField" id="expiredDateId" data_type="date" uname="Expired date" title="Set date" readonly="true">
                            <xsl:attribute name="value"><xsl:value-of select="@expired-date"/></xsl:attribute>
                        </input>
                        (yyyy-mm-dd)
                    </td>
                </tr>
            </table>

        </td>
    </tr>
</xsl:template>

<xsl:template name="createSimpleOption">
	<xsl:param name="optionValue"/>
	<xsl:param name="currentValue"/>
	<option value="{$optionValue}">
		<xsl:if test="$optionValue=$currentValue">
			<xsl:attribute name="selected">true</xsl:attribute>
		</xsl:if>
		<xsl:value-of select="$optionValue"/>
	</option>
</xsl:template>

<xsl:template match="negeso:article" mode="local_save">
	<input id="{@id}" name="article_text" type="hidden"/>
	<img src="/images/mark_1.gif" onclick="RTE_Init('article_text_{negeso:head/text()}_div','article_text_{negeso:head/text()}_div,article_text_{negeso:head/text()}', {@id}, 1, 0, 'contentStyle', '{//negeso:page/@lang}');" alt="Edit article" class="admBorder admHand"/>
	<div id="article_text_{negeso:head/text()}_div_temp" class="contentStyle" style="border: 1px solid #848484; margin:5px;">

	</div>
	<!--<div id="article_text_{negeso:head/text()}_div" class="contentStyle" style="margin: 5px;border: 1px solid #848484;">

		<xsl:choose>
			<xsl:when test="negeso:text/text()">
				<xsl:value-of select="negeso:text" disable-output-escaping="yes"/>
			</xsl:when>
			<xsl:otherwise>&#160;</xsl:otherwise>
		</xsl:choose>
	</div>-->
	<input type="hidden" name="article_id_{negeso:head/text()}" id="article_id_{negeso:head/text()}" value="{@id}"/>
	<input type="hidden" name="article_class_{negeso:head/text()}" id="article_class_{negeso:head/text()}" value="{@class}"/>
	<input type="hidden" name="article_container_id_{negeso:head/text()}" id="article_container_id_{negeso:head/text()}" value="{@containerId}"/>
	<input type="hidden" name="article_head_{negeso:head/text()}" id="article_head_{negeso:head/text()}" value="{negeso:head/text()}"/>
	<input type="hidden" name="article_lang_{negeso:head/text()}" id="article_lang_{negeso:head/text()}" value="{@lang}"/>
	<input type="hidden" name="article_text_{negeso:head/text()}" id="article_text_{negeso:head/text()}" value="{negeso:text/text()}"/>
</xsl:template>



<!-- ********************************** Language *********************************** -->
<xsl:template match="negeso:languages">
    <select name="langIdSelected" id="langIdSelected" onChange="return onChangeLanguage()" class="admWidth150">
        <xsl:if test="/negeso:page/negeso:pm-category/@new='true'">
            <xsl:attribute name="disabled">true</xsl:attribute>
        </xsl:if>
        <xsl:apply-templates select="negeso:language"/>
    </select>
</xsl:template>
	
<xsl:template match="negeso:language">
	<option value="{@id}">
		<xsl:if test="@selected='true'">
			<xsl:attribute name="selected">true</xsl:attribute>
		</xsl:if>
		<xsl:value-of select="@code"/>
		<xsl:if test="@default='true'">
			(<xsl:value-of select="java:getString($dict_common, 'DEFAULT')"/>)
		</xsl:if>
	</option>
</xsl:template>

<!-- PRODUCT LANGUAGE PRESECNE -->
<xsl:template match="negeso:pm-lang-presence">
    <xsl:apply-templates select="negeso:pm-lang"/>
</xsl:template>

<xsl:template match="negeso:pm-lang">
    <tr>
        <td  width="30px">
            <xsl:value-of select="@lang-code"/> :
        </td>
        <td>
            <input type="checkbox" name="{@lang-id}_lang2presence">
                <xsl:if test="@value='true'">
                    <xsl:attribute name="checked">true</xsl:attribute>
                </xsl:if>
            </input>
        </td>
    </tr>
</xsl:template>

<xsl:template match="negeso:pm-lang-presence" mode="translation">
    <xsl:apply-templates select="negeso:pm-lang"  mode="translation"/>
</xsl:template>

<xsl:template match="negeso:pm-lang" mode="translation">
   <xsl:if test="not(//negeso:language[@selected='true']/@id = @lang-id)">
       <tr>
           <td  width="30px">
               <xsl:value-of select="@lang-code"/> :
           </td>
           <td>
               <input type="checkbox" name="translateToLang" value="{@lang-id}">
               </input>
           </td>
       </tr>
    </xsl:if>
</xsl:template>

<!-- ********************************** Path *********************************** -->
<xsl:template name="pm.shopPath">
    <!-- Path -->
    <table cellpadding="0" cellspacing="0">
        <tr>
            <td style="padding:8px 0 0 5px;" valign="middle" >
                <!-- Unactive pathe element - make it link-->
                <span >
                    <a class="admNavigation" href="?command=pm-get-entry-page">
                        <xsl:value-of select="java:getString($dict_product, 'PRODUCT_MODULE')"/>
                    </a>&#160;<img src="/images/navig_arrow.png"  style="vertical-align:middle;"/>&#160;
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
			<span class="admNavigation" style="text-decoration:none;" ><xsl:value-of select="@title"/></span>
		</xsl:when>
		<xsl:otherwise>
			<!-- Unactive pathe element - make it link-->
            <span>
                <a class="admNavigation"  href="{@link}">
                    <xsl:value-of select="@title"/>
                </a>&#160;<img src="/images/navig_arrow.png"  style="vertical-align:middle;"/>&#160;
            </span>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<!-- ******************************* Error message ********************************** -->
<xsl:template match="errorMessage">
    <xsl:value-of select="errorMessage"/>
</xsl:template>

</xsl:stylesheet>
