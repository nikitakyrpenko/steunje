<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$

  Copyright (c) 2003 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential
  Information and shall use it only in accordance with the terms of the
  license agreement you entered into with Negeso.

  View of a list item edit interface.

  @version      $Revision$
  @author       Olexiy.Strashko
  @author       Volodymyr Snigur
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:negeso="http://negeso.com/2003/Framework"
	xmlns:java="http://xml.apache.org/xslt/java"
	exclude-result-prefixes="java">

    <xsl:output method="html"/>
    <xsl:include href="/xsl/negeso_body.xsl"/>
    <xsl:include href="/xsl/admin_templates.xsl"/>
    <xsl:include href="modules/product/pm_common.xsl"/>

    <xsl:variable name="lang" select="/*/@interface-language"/>
    <xsl:variable name="pageLang" select="/negeso:page/@lang"/>
    <xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
    <xsl:variable name="dict_product" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_product.xsl', $lang)"/>
    <xsl:variable name="dict_import" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_import', $pageLang)"/>
    <xsl:variable name="dict_prod" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('product', $lang)"/>
    
    <xsl:variable name="is_gm_enabled" select="/negeso:page/negeso:pm-product/@google_merchant_enabled = 'true'"/>

    <!-- NEGESO JAVASCRIPT Temaplate -->
    <xsl:template name="java-script">
        <script language="JavaScript">
            var s_DocumentIsUndefined = "<xsl:value-of select="java:getString($dict_product, 'DOCUMENT_IS_UNDEFINED')"/>";
            var s_Up = "<xsl:value-of select="java:getString($dict_common, 'UP')"/>";
            var s_Down = "<xsl:value-of select="java:getString($dict_common, 'DOWN')"/>";
            var s_Delete = "<xsl:value-of select="java:getString($dict_common, 'DELETE')"/>";
            var s_DeleteConfirmMsg = "<xsl:value-of select="java:getString($dict_product, 'DELETE_PRODUCT_CONFIRMATION')"/>";

            <xsl:text disable-output-escaping="yes">
	    <![CDATA[
	    var unique_error = "<div id='unique_error'><table cellspacing=\"0\" cellpadding=\"0\" style=\"height: 12px; margin: 0; padding: 0; border: 0;\"><tr style=\"height: 12px;\"><td style=\"font-size: 10px; color: red; border: 0; padding: 0; text-align: left; vertical-align: middle; height: 12px;\">" 
        + "Title is not unique"
        + "</td></tr></table><div/>"
		function update(targetId) {
		     $('#unique_error').remove();
            if ( !validateForm(operateFormId) ){
                return false;
            }
            
            var isUnique = false;
            $.ajax({
                  type: "GET",
                  url: "/admin/pm_check_url?type=product&title=" + $('[name=title_value]').val() + "&langId=" + $('#langIdSelected').val() + 
                    "&catId=" + $('[name=categoryId]').val() + "&productId=" + targetId,
                  async: false,
                  dataType: "html",
                  success: function(html, stat) {
                    if (html == "true")
                        isUnique = true;
                  },
                  error: function(html, stat) { }
              });
            if (!isUnique) {
                $('[name=title_value]').after(unique_error);
                return false;
            }

            document.operateForm.command.value = "pm-update-product";
        	document.operateForm.pmTargetId.value = targetId;
			document.operateForm.langId.value = document.getElementById("langIdSelected").value;
			
			var list = document.getElementsByName('listTables');			
			for (var j=0; j < list.length ; j++) {
				var listProperty = '';
				var rows = document.getElementById(list[j].value).rows;
				for (var k=1; k < rows.length ; k++) {
					listProperty += ',' + 
					(rows[k].id.lastIndexOf(' ') > 0 ?
						rows[k].id.substring(0, rows[k].id.lastIndexOf(' ') )
						: rows[k].id )
					;	
				}
				listProperty=listProperty.replace(/(^,|^\s+$)/,'');
				document.getElementById(list[j].value + '_value_id').value = listProperty;
				//alert(list[j].value + '_value_id:['+document.getElementById(list[j].value + '_value_id').value+']');
			}
        	return true;
		}
		
		function onChangeLanguage() {
			document.operateForm.langId.value = document.getElementById('langIdSelected').value;
			document.operateForm.command.value = "pm-get-edit-product-page";
			document.operateForm.pmTargetId.value = document.operateForm.pmCatId.value;
			document.operateForm.submit();
		}

		function selectDocumentDialog(fieldObject, aTag){
			result = MediaCatalog.selectDocumentDialog();
			if (result != null){
				if (result.resCode == "OK"){
					fieldObject.value = result.fileUrl;
					aTag.href = result.fileUrl;
					aTag.innerText = "< " + result.fileUrl + " >";
				}
			}
		}
        function clearDocument(fieldObject, aTag){
            fieldObject.value='';
            aTag.value='';            
        }

        function selectThumbnailImageDialog(fieldLinkObject, fieldThLinkObject, aTag, imgTag){
			result = MediaCatalog.selectThumbnailImageDialog(fieldLinkObject.resizeWidth, fieldLinkObject.resizeHeight);
			if (result != null){
				if (result.resCode == "OK"){
					fieldLinkObject.value = result.realImage;
					fieldThLinkObject.value = result.thumbnailImage;
					aTag.innerText = "< " + result.realImage + " >";
					imgTag.src = result.thumbnailImage;
				}
			}
		}

		function openDocument(link){
			if (link != "undefined"){
				window.open(link, "_blank");
				return false;
			}			
			else {
			    alert (s_DocumentIsUndefined);
			    return false;
	        }
		}

		function openImage(link, width, height){
			if ((width == null) || (width == '')){
				width='800';
			}
			if ((height == null) || (height == '')){
				height='600';
			}
			HEADER_HEIGHT = 34;
			height = parseInt(height) + HEADER_HEIGHT;
			strAttr = "resizable:yes;scroll:no;status:no;dialogWidth:" + width + "px;dialogHeight:" + height + "px;";
			result = window.showModalDialog(link, null , strAttr);
			return false;
		}

		function preview(id, link){
            //alert("Lang :" + link);
            if (update(id)){
                document.operateForm.submit();
                window.open(link + id);
            }
		}
		
		var i = 0;
		function addProduct(tableId, typeId) {
			var langs = new Array();;	
			var arrLength = 0;
			var formObj = document.forms["operateForm"];
			try {
				for (i = 0; i < formObj.elements.length; i++) {
				  if (formObj.elements[i].type == "checkbox" &&
				     formObj.elements[i].name.indexOf("_lang2presence") != -1) {
				    if (formObj.elements[i].checked) {
				      langs[arrLength++] = formObj.elements[i].name.charAt(0);
				    } 
				  }
			}
			} catch (e) {
				alert('Exception occured' + e);
			}
	        strPage = "/admin/product_chooser.html?attributeTypeId=" + typeId + "&languagesPresence=" + langs.join(',');
	        strAttr = "resizable:on;scroll:on;status:off;dialogWidth:800px;dialogHeight:700px;help:on";
	        product = showModalDialog(strPage, "", strAttr);
	        if ( product != null ) {
		        traverseTable(product[0] + ' ' + i++, product[1], tableId);
	        }
		}
		
	    function traverseTable(productId, productTitle, tableId) {

	        // creates <table> and <tbody> elements
	        var mytable     = document.getElementById(tableId);//document.createElement("table");
	        var mytablebody = document.createElement("tbody");
	
	
	        // creating all raws
	        //for(var j = 0; j < 2; j++)
	        {
	
	            // creates a <tr> element
	            mycurrent_row = document.createElement("tr");
	            mycurrent_row.setAttribute("id", tableId + productId);
	
	            var tdTitle = document.createElement("td");
	            tdTitle.className  = "admMainTD";
	            
	            if (typeof(productTitle)!= "undefined" && productTitle != null && productTitle != "") {
					var aTitle = document.createElement("a");
					aTitle.appendChild(
					    document.createTextNode(productTitle));
					aTitle.setAttribute( "href", "?command=pm-get-edit-product-page&pmTargetId=" 
						+ productId.substring(0, productId.lastIndexOf(' ') ) );
					aTitle.className = "admAnchor";
					tdTitle.appendChild(aTitle);
	            } else {
	            	var dummy = document.createElement("div");
	            	dummy.className = "admDummyDiv";
	            	dummy.appendChild(document.createTextNode("\xa0"));
	            	tdTitle.appendChild(dummy);
	            	alert(tdTitle.innerHTML);
	            }

	            var tdUp = document.createElement("td");
	            tdUp.className = "admDarkTD admWidth30";
	
	            var imgUp = document.createElement("img");
	            imgUp.className = "admHand";
	            imgUp.setAttribute("src", "/images/up.gif");
	            imgUp.setAttribute("title", s_Up);
                imgUp.onclick = function() { moveUp(tableId, productId); } 
	            tdUp.appendChild(imgUp);

	            var tdDown = document.createElement("td");
	            tdDown.className = "admLightTD admWidth30";

	            var imgDown = document.createElement("img");
	            imgDown.className = "admHand";	            
	            imgDown.setAttribute("src", "/images/down.gif");
	            imgDown.setAttribute("title", s_Down);
                imgDown.onclick = function() { moveDown(tableId, productId); } 	            
	            tdDown.appendChild(imgDown);
	
	            var tdDel = document.createElement("td");
	            tdDel.className = "admDarkTD admWidth30";
	
	            var imgDel = document.createElement("img");
	            imgDel.className = "admHand";	            
	            imgDel.setAttribute("src", "/images/delete.gif");
	            imgDel.setAttribute("title", s_Delete);
                imgDel.onclick = function() { tryToDelete(tableId, productId); } 	            	            
	            tdDel.appendChild(imgDel);
	
	            mycurrent_row.appendChild(tdTitle);
	            mycurrent_row.appendChild(tdUp);
	            mycurrent_row.appendChild(tdDown);
	            mycurrent_row.appendChild(tdDel);
	
	            // appends the row <tr> into <tbody>
	            mytablebody.appendChild(mycurrent_row);
	        }
	
	        // appends <tbody> into <table>
	        mytable.appendChild(mytablebody);
	
	        //tableHolder.appendChild(mytable);
    }

	function moveDown(tableId, id) {
	    var mytable     = document.getElementById(tableId);
	    var row = document.getElementById(tableId + id);
	    var i = row.rowIndex;
	
	    if (mytable.rows.length-1 > i)
	    {
	        mytable.moveRow(i, i+1);
	    }
	}
	
	function moveUp(tableId, id) {
	    var mytable     = document.getElementById(tableId);
	    var row = document.getElementById(tableId + id);
	    var i = row.rowIndex;
	
	    if (i > 1)
	    {
	        mytable.moveRow(i, i-1);
	    }
	}
	
	function tryToDelete(tableId, id)
	{
		if ( confirm(s_DeleteConfirmMsg) ) {
		    var mytable     = document.getElementById(tableId);
		    var row = document.getElementById(tableId + id);
		    mytable.deleteRow(row.rowIndex);
		    return true;
		}
		return false;
	
	}
	
	
	function resetForm()
	{
		var backupArticle = $("input[name^='backup']"); //doing backup of article before changes
		var resetForm = document.getElementById('operateFormId'); //select main form
		var realArticle = $("div[id^='article_text']");  //select div with article text
		var articleId = $("input[name^='article_text']");  //select id of the article
		resetForm.reset();  //reset the other fields of the form
		for (var i=0; i < backupArticle.length; i++)
		{
			realArticle[i].innerHTML = backupArticle[i].value; //on click to "reset" button change value of article's div to beginning value
			AJAX_Send("update-article-text-command", {id: articleId[i].id, text: realArticle[i].innerHTML});//send beginning value of article to DB	
		}
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

        <xsl:call-template name="adminhead"/>

    </xsl:template>

    <xsl:template match="/" >
        <html>
            <head>
                <title><xsl:value-of select="java:getString($dict_product, 'EDIT_PRODUCT')"/></title>
                <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
                
                <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
                <link href="/site/core/css/default_styles.css" rel="stylesheet" type="text/css"/>
                <link rel="stylesheet" type="text/css" href="/css/jquery-ui.custom.css"/>

                <script type="text/javascript" src="/script/jquery.min.js"></script>
                <script type="text/javascript" src="/script/jquery-ui.custom.min.js" />
                <script type="text/javascript" src="/script/cufon-yui.js"></script>
                <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
                <script type="text/javascript" src="/script/common_functions.js"></script>
                
                <xsl:call-template name="java-script"/>
                
                <script type="text/javascript" src="/script/conf.js"></script>
                <script type="text/javascript" src="/script/media_catalog.js"></script>
            </head>
            <body
                style="behavior: url(/script/webservice.htc) url(/script/clientmanager.htc)"
                id="ClientManager" xmlID="332"
>
                <!-- NEGESO HEADER -->
                <xsl:call-template name="NegesoBody">
                    <xsl:with-param name="helpLink">
                        <xsl:text>/admin/help/cpr1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
                    </xsl:with-param>
                    <xsl:with-param name="backLink" select="concat('?command=pm-browse-category&amp;pmCatId=', negeso:pm-product/@category-id)"/>
                </xsl:call-template>
                <script>
                    Cufon.now();
                    Cufon.replace('.admNavPanelFont', { fontFamily: 'Orator Std' });
                    Cufon.replace('.admTDtitles', { fontFamily: 'Orator Std' });
                </script>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="negeso:page"  mode="admContent">
        <form method="post" id="operateFormId" name="operateForm" action="" enctype="multipart/form-data">

            <!-- PATH -->
            <xsl:call-template name="pm.shopPath"/>
            <div class="admCenter">
                <font color="#FF0000">
                    <xsl:value-of select="errorMessage"/>
                </font>
            </div>
            <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
            <tr>
                <td>
                    <table cellpadding="0" cellspacing="0" width="100%">
                    <tr>
                        <td align="center" class="admNavPanelFont"  colspan="3">
                            <!-- TITLE -->
                            <xsl:call-template name="tableTitle">
                                <xsl:with-param name="headtext">
                                    <xsl:choose>
                                        <xsl:when test="/negeso:page/negeso:pm-product/@new='true'">
                                                <xsl:value-of select="java:getString($dict_product, 'PRODUCT_MODULE')"/><xsl:text>. </xsl:text><xsl:value-of select="java:getString($dict_product, 'NEW_PRODUCT')"/>
                                        </xsl:when>
                                        <xsl:otherwise>
                                                <xsl:value-of select="java:getString($dict_product, 'PRODUCT_MODULE')"/><xsl:text>. </xsl:text><xsl:value-of select="java:getString($dict_product, 'EDIT_PRODUCT')"/>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </xsl:with-param>
                            </xsl:call-template>
                        </td>
                    </tr>
                    <tr>
                                <td width="40%" class="admTableTDLast admLeft">
                                    <div class="admNavPanelInp">
                                        <div class="imgL"></div>
                                        <div>
                                            <input class="admNavPanelInp" name="add" onClick="location.href='/admin/listreview?product_id={negeso:pm-product/@id}'" type="submit">
                                                <xsl:attribute name="value">Product reviews</xsl:attribute>
                                            </input>
                                        </div>
                                        <div class="imgR"></div>
                                    </div>
                                </td>
                                <td width="40%" class="admTableTDLast admRight">
                                    <xsl:value-of select="java:getString($dict_common, 'LANGUAGE_VERSION')"/>
                                </td>
                                <td width="20%" class="admTableTDLast admRight">
                                    <xsl:apply-templates select="negeso:languages"/>
                                </td>
                            </tr>
                </table>
                <!-- Content -->
                    <table cellpadding="0" cellspacing="0" width="100%">
                    <xsl:apply-templates select="negeso:pm-product"/>
                </table>
                <!-- Update/reset fields -->
                    <table cellpadding="0" cellspacing="0" width="100%">
                    <tr>
                            <th class="admTableTDLast">
                            <xsl:choose>
                                <xsl:when test="/negeso:page/negeso:pm-product/@new='true'">
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
                        	<td class="admTableTDLast">
                        		<table>
                                <tr>
									<td style="padding: 0 0 0 15px;"  > Choose Rich Snippets </td>
                                    <td class="admNavChoose" >
                                        <div align="center">
                                            <input type="button" value="..." style="width: 24px; height: 21px;" onclick="chooseRichSnippets({/negeso:page/negeso:pm-product/@id}, 'product')" onfocus="blur()"/>
                                        </div>
                                    </td>
                                </tr>
                            </table>
                        	</td>
                        </tr>
                    </table>
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
	                    <input type="text" name="google_merchant_brand" value="{/negeso:page/negeso:pm-product/@google_merchant_brand}" class="admTextArea"/>
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
	                    		<xsl:with-param name="currentValue" select="/negeso:page/negeso:pm-product/@google_merchant_condition"/>
	                    	</xsl:call-template>
	                    	<xsl:call-template name="createSimpleOption">
	                    		<xsl:with-param name="optionValue" select="'new'"/>
	                    		<xsl:with-param name="currentValue" select="/negeso:page/negeso:pm-product/@google_merchant_condition"/>
	                    	</xsl:call-template>
	                    	<xsl:call-template name="createSimpleOption">
	                    		<xsl:with-param name="optionValue" select="'refurbished'"/>
	                    		<xsl:with-param name="currentValue" select="/negeso:page/negeso:pm-product/@google_merchant_condition"/>
	                    	</xsl:call-template>
	                    	<xsl:call-template name="createSimpleOption">
	                    		<xsl:with-param name="optionValue" select="'used'"/>
	                    		<xsl:with-param name="currentValue" select="/negeso:page/negeso:pm-product/@google_merchant_condition"/>
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
	                    		<xsl:with-param name="currentValue" select="/negeso:page/negeso:pm-product/@google_merchant_availability"/>
	                    	</xsl:call-template>
	                    	<xsl:call-template name="createSimpleOption">
	                    		<xsl:with-param name="optionValue" select="'in stock'"/>
	                    		<xsl:with-param name="currentValue" select="/negeso:page/negeso:pm-product/@google_merchant_availability"/>
	                    	</xsl:call-template>
	                    	<xsl:call-template name="createSimpleOption">
	                    		<xsl:with-param name="optionValue" select="'available for order'"/>
	                    		<xsl:with-param name="currentValue" select="/negeso:page/negeso:pm-product/@google_merchant_availability"/>
	                    	</xsl:call-template>
	                    	<xsl:call-template name="createSimpleOption">
	                    		<xsl:with-param name="optionValue" select="'out of stock'"/>
	                    		<xsl:with-param name="currentValue" select="/negeso:page/negeso:pm-product/@google_merchant_availability"/>
	                    	</xsl:call-template>
	                    	<xsl:call-template name="createSimpleOption">
	                    		<xsl:with-param name="optionValue" select="'preorder'"/>
	                    		<xsl:with-param name="currentValue" select="/negeso:page/negeso:pm-product/@google_merchant_availability"/>
	                    	</xsl:call-template>
	                    </select>
	                </td>
                </tr>
            	<tr>
	            	<th class="admTableTDLast admWidth150" >
	                    <xsl:value-of select="java:getString($dict_prod, 'PM_GM_PRODUCT_CATEGORY')"/>
	                </th>
	                <td class="admTableTDLast">
	                    <input type="text" name="google_merchant_product_category" value="{/negeso:page/negeso:pm-product/@google_merchant_product_category}" class="admTextArea"/>
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
	                    <input type="text" name="google_merchant_product_type" value="{/negeso:page/negeso:pm-product/@google_merchant_product_type}" class="admTextArea"/>
	                </td>
                </tr>
                <tr>
	            	<th class="admTableTDLast admWidth150" >
	                    <xsl:value-of select="java:getString($dict_prod, 'PM_GM_GTIN')"/>
	                </th>
	                <td class="admTableTDLast" colspan="2">
	                    <input type="text" name="google_merchant_gtin" value="{/negeso:page/negeso:pm-product/@google_merchant_gtin}" class="admTextArea"/>
	                </td>
                </tr>
            </table>
                    
                        </td>
                    </tr>
            <tr>
                <td class="admTableFooter" >&#160;</td>
            </tr>
                </table>
        <table cellpadding="0" cellspacing="0"  width="764px" align="center" class="Buttons">
            <tr>
                <td>
                <xsl:choose>
                        <xsl:when test="/negeso:page/negeso:pm-product/@new='true'">
                            <div class="admBtnGreenb">
                                <div class="imgL"></div>
                                <div>
                                    <input class="admNavbarInp" name="saveButton" onClick="return update('{/negeso:page/negeso:pm-product/@id}');" type="submit">                                      
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
                                    <input class="admNavbarInp" name="saveButton" onClick="return update('{/negeso:page/negeso:pm-product/@id}',this.form);" type="submit">
                                        <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'SAVE')"/></xsl:attribute>
                                    </input>
                                </div>
                                <div class="imgR"></div>
                            </div>
                    </xsl:otherwise>
                </xsl:choose>
                    
                    <div class="admBtnGreenb">
                        <div class="imgL"></div>
                        <div>                           
                            <input class="admNavbarInp" name="previewButton" onClick="preview('{/negeso:page/negeso:pm-product/@id}', '{/negeso:page/@product_link_wout_id}');" type="submit">
                                <xsl:if test="@new"><xsl:attribute name="disabled">true</xsl:attribute>></xsl:if>                                
                                <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'PREVIEW')"/></xsl:attribute>
                            </input>
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
        <xsl:apply-templates select ="//negeso:pm-property/negeso:article" mode="formOutArtilce" />
    </xsl:template>

    <xsl:template match="negeso:article" mode="formOutArtilce">
        <div onpropertychange="pushContent(this)" head="{negeso:head/text()}" id="article_text{@id}" class="contentStyle hiddenArtilce" style="border: 1px solid #848484;">
            <xsl:choose>
                <xsl:when test="negeso:text/text()">
                    <xsl:value-of select="negeso:text" disable-output-escaping="yes"/>
                </xsl:when>
                <xsl:otherwise>&#160;</xsl:otherwise>
            </xsl:choose>
        </div>
        <script>
            pushContent(document.getElementById('article_text<xsl:value-of select ="@id"/>'));
        </script>
    </xsl:template>
    <!-- ********************************** PRODUCT *********************************** -->
    <xsl:template match="negeso:pm-product">
        <tr>
            <td class="admTDtitles" colspan="2" align="center">
                <xsl:value-of select="java:getString($dict_product, 'PRODUCT_PROPERTIES')"/>
            </td>
        </tr>
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
                <input type="hidden" name="categoryId" value="{/negeso:page/negeso:pm-product/@category-id}"></input>
                <input type="hidden" name="productType" >
                    <xsl:attribute name="value"><xsl:value-of select="/negeso:page/negeso:product/@product-type-id" /></xsl:attribute>
                </input>


                <table cellspacing="0" cellpadding="0" width="100%">
                        <xsl:apply-templates select="negeso:pm-property"/>
                        <tr>
                        <th class="admTableTD">Product type:</th>
                        <td class="admTableTDLast">
                                <xsl:value-of select="/negeso:page/@product-type-name" />
                            </td>
                        </tr>
                        <tr>
                        <th class="admTableTD" >
                            <xsl:value-of select="java:getString($dict_product, 'SHOW_IN_LANGUAGES')"/>:
                        </th>
                        <td class="admTableTDLast" >
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

                    </table>

                <xsl:if test="negeso:price-list">
                    <xsl:for-each select="negeso:price-list">
                        <fieldset class="admFieldset admFieldMargin">
                            <legend>Price list:</legend>
                            <table class="admNavPanel" cellspacing="0" cellpadding="0">
                                <tr class="admLightTD">
                                    <td>
                                        <b>Region name</b>
                                    </td>
                                    <td>
                                        <b>Region description</b>
                                    </td>
                                    <td>
                                        <b>Price</b>
                                    </td>
                                </tr>
                                <xsl:for-each select="negeso:price">
                                    <tr>
                                        <td class="admLightTD">
                                            <xsl:value-of select="@name"/>
                                        </td>
                                        <td class="admLightTD">
                                            <xsl:value-of select="@description"/>
                                        </td>
                                        <td class="admLightTD">
                                            <xsl:value-of select="@value"/>
                                        </td>
                                    </tr>
                                </xsl:for-each>


                            </table>
                        </fieldset>
                    </xsl:for-each>
                </xsl:if>


            </td>
        </tr>
        <tr>
            <td class="admTDtitles" colspan="2" align="center">
                        <xsl:value-of select="java:getString($dict_common, 'ADVANCED_PROPERTIES')"/>:
            </td>
        </tr>
        <tr>
            <td>
                <table cellspacing="0" cellpadding="0" width="100%">
                    <tr>
                        <td class="admTDtitles admTableTD" width="70%">
                            Show properties:
                        </td>
                        <td class="admTDtitles admTableTDLast" width="30%">
                            <xsl:value-of select="java:getString($dict_product, 'PRESENCE_IN')"/>:
                        </td>
                    </tr>
                        <!-- Rating -->
                        <tr>
                        <td>
                            <table cellspacing="0" cellpadding="0" width="100%">
                                        <xsl:call-template name="showProperties"/>
                                    </table>
                            </td>
                        <td style="vertical-align:top">
                                    <xsl:if test="/negeso:page/@conf_featured_lists='false'">
                                        <xsl:attribute name="disabled">true</xsl:attribute>
                                    </xsl:if>
                            <table cellspacing="0" cellpadding="0" width="100%">                                
                                            <xsl:apply-templates select="negeso:pm-product-features"/>
                                        </table>
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

	<xsl:template match="negeso:pm-lang-presence" mode="translation">
		<xsl:apply-templates select="negeso:pm-lang" mode="translation" />
	</xsl:template>

	<xsl:template match="negeso:pm-lang" mode="translation">
		<xsl:if test="not(@lang-code = //negeso:page/@lang)">
			<tr>
				<td width="30px">
					<xsl:value-of select="@lang-code" />
					:
				</td>
				<td>
					<input type="checkbox" name="translateToLang" value="{@lang-id}">
					</input>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>

    <!-- showProperties -->
    <xsl:template name="showProperties">
        <xsl:if test="count(negeso:taxes) > 0">
            <tr>
                <th class="admTableTD">Tax*</th>
                <td class="admTableTD">
                    <xsl:call-template name="pm.tax-chooser"/>
                </td>
            </tr>
        </xsl:if>

        <tr>
            <th class="admTableTD">
                <xsl:value-of select="java:getString($dict_product, 'RATING')"/>*
            </th>
            <td class="admTableTD">
                <input class="admTextArea admWidth200" type="text" name="ratingField" data_type="number" required="true" uname="Product rating">
                    <xsl:attribute name="value"><xsl:value-of select="@rating"/></xsl:attribute>
                </input>
                (1 - 5)
            </td>
        </tr>
        <!-- Publish Date -->
        <tr>
            <th class="admTableTD">
                <xsl:value-of select="java:getString($dict_common, 'PUBLISH_DATE')"/>*
            </th>
            <td class="admTableTD">
                <input type="text" class="admTextArea admWidth200" readonly="readonly" timedate="true" name="publishDateField" id="publishDateId">
                    <xsl:attribute name="value"><xsl:value-of select="@publish-date"/></xsl:attribute>
                </input>&#160;(dd-mm-yyyy)            
            </td>
        </tr>
        <!-- Expired Date -->
        <tr>
            <th class="admTableTD">
                <xsl:value-of select="java:getString($dict_common, 'EXPIRED_DATE')"/>
            </th>
            <td class="admTableTD">
                <input type="text" class="admTextArea admWidth200" readonly="readonly" timedate="true" name="expiredDateField" id="expiredDateId" value="">
                    <xsl:attribute name="value"><xsl:value-of select="@expired-date"/></xsl:attribute>
                </input>&#160;(dd-mm-yyyy)            
            </td>
        </tr>
    </xsl:template>

    <!-- PRODUCT FEATURES -->
    <xsl:template match="negeso:pm-product-features">
        <xsl:apply-templates select="negeso:pm-feature"/>
    </xsl:template>

    <xsl:template match="negeso:pm-feature">
        <tr>
            <th class="admTableTD">
                <xsl:value-of select="@name"/> :
            </th>
            <td class="admTableTDLast">
                <input class="admCheckBox" type="checkbox" name="{@id}_feature">
                    <xsl:if test="@value='1'">
                        <xsl:attribute name="checked">true</xsl:attribute>
                    </xsl:if>
                </input>
            </td>
        </tr>
    </xsl:template>

    <!-- PRODUCT LANGUAGE PRESECNE -->
    <xsl:template match="negeso:pm-lang-presence">
        <xsl:apply-templates select="negeso:pm-lang"/>
    </xsl:template>

    <xsl:template match="negeso:pm-lang">
        <tr>
            <td >
                <xsl:value-of select="@lang-code"/> :
            </td>
            <td  width="30%">
                <input type="checkbox" name="{@lang-id}_lang2presence">
                    <xsl:if test="@value='true'">
                        <xsl:attribute name="checked">true</xsl:attribute>
                    </xsl:if>
                </input>
            </td>
        </tr>
    </xsl:template>

    <!-- ********************************** Property *********************************** -->
    <xsl:template match="negeso:pm-property">
        <!-- STRING, NUMBER -->
        <tr>
            <th class="admTableTD">
                <xsl:if test="@is-i18n='true'">
                    (<xsl:value-of select="/negeso:page/@lang"/>)
                </xsl:if>
                <xsl:choose>
                    <xsl:when test="@is-dictionary='true'">
                        <xsl:value-of select="java:getString($dict_import, @user-name)"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="@user-name"/>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:if test="@unit">
                    <xsl:text></xsl:text>
                    (<xsl:value-of select="@unit"/>)
                </xsl:if>
                <xsl:if test="@is-required='true'">*</xsl:if>
            </th>
            <td class="admTableTDLast">
                <input class="admTextArea" type="text" name="{@name}_value">
                    <xsl:attribute name="value"><xsl:value-of select="@value"/></xsl:attribute>
                    <xsl:attribute name="data_type"><xsl:value-of select="@type"/></xsl:attribute>
                    <xsl:attribute name="uname"><xsl:value-of select="@user-name"/></xsl:attribute>
                    <xsl:if test="@is-required='true'">
                        <xsl:attribute name="required">true</xsl:attribute>
                    </xsl:if>
                </input>
                <input type="hidden" name="{@name}_id" value="{@id}"/>
            </td>
        </tr>
    </xsl:template>

    <!-- TEXTAREA property -->
    <xsl:template match="negeso:pm-property[@type='text']">
        <tr>
            <th class="admTableTD">
                <xsl:if test="@is-i18n='true'">
                    (<xsl:value-of select="/negeso:page/@lang"/>)
                </xsl:if>
                <xsl:value-of select="@user-name"/>
                <xsl:if test="@is-required='true'">*</xsl:if>
            </th>
            <td class="admTableTDLast">
                <input type="hidden" name="{@name}_id" value="{@id}"/>
                <textarea class="admTableTDLast" type="text" name="{@name}_value" data_type="text" rows="3">
                    <xsl:attribute name="uname">
                        <xsl:value-of select="@user-name"/>
                    </xsl:attribute>
                    <xsl:if test="@is-required='true'">
                        <xsl:attribute name="required">true</xsl:attribute>
                    </xsl:if>
                    <xsl:if test="count(@value)=0">&#160;</xsl:if>
                    <xsl:value-of select="@value"/>
                </textarea>
                &#160;
            </td>
        </tr>
    </xsl:template>

    <!-- LIST property -->
    <xsl:template match="negeso:pm-property[@type='list']">
        <tr>
            <th class="admTableTD">
                <xsl:if test="@is-i18n='true'">
                    (<xsl:value-of select="/negeso:page/@lang"/>)
                </xsl:if>
                <xsl:value-of select="@user-name"/>
                <xsl:if test="@is-required='true'">*</xsl:if>
            </th>
            <td class="admTableTDLast">
                <input type="hidden" name="listTables" value="{@name}"/>
                <input type="hidden" name="{@name}_id" value="{@id}"/>
                <input type="hidden" id="{@name}_value_id" name="{@name}_value" value=""/>
                <!-- textarea class="admFieldMargin admWidth335" type="text" name="{@name}_value" data_type="text" rows="3">
	            <xsl:attribute name="uname"><xsl:value-of select="@user-name"/></xsl:attribute>
	            <xsl:if test="@is-required='true'">
	                <xsl:attribute name="required">true</xsl:attribute>
	            </xsl:if>
	            <xsl:if test="count(@value)=0">&#160;</xsl:if> 
	            <xsl:value-of select="@value"/>
				</textarea -->
                <table id="{@name}" cellpadding="0" cellspacing="0" width="100%">
                    <tr class="admRight">
                        <td colspan="4">
                            <input class="admLightInp admWidth150" name="addProductButton" onClick="addProduct('{@name}', {@type-id})" type="button" value="&lt;&#160;AddProduct&#160;&gt;" />
                        </td>
                    </tr>
                    <xsl:apply-templates select="negeso:pm-product" mode="list"/>
                </table>
            </td>
        </tr>
    </xsl:template>

    <xsl:template match="negeso:pm-product" mode="list">
        <tr id="{../@name}{@id}">
            <td class="admTableTDLast" style="width:100%;">
                <xsl:choose>
                    <xsl:when test="@title">
                        <a class="admAnchor">
                            <xsl:attribute name="href">?command=pm-get-edit-product-page&amp;pmTargetId=<xsl:value-of select="@id"/></xsl:attribute>
                            <xsl:value-of select="@title" disable-output-escaping="yes"/>
                        </a>
                    </xsl:when>
                    <xsl:otherwise>
                        <div class="admDummyDiv">&#160;</div>
                    </xsl:otherwise>
                </xsl:choose>
            </td>
            <td class="admDarkTD admWidth30">
                <img src="/images/up.gif" class="admHand" onClick="moveUp('{../@name}','{@id}')">
                    <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'UP')"/></xsl:attribute>
                </img>
            </td>
            <td class="admLightTD admWidth30">
                <img src="/images/down.gif" class="admHand" onClick="moveDown('{../@name}','{@id}')">
                    <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'DOWN')"/></xsl:attribute>
                </img>
            </td>
            <td class="admDarkTD admWidth30">
                <img src="/images/delete.gif" class="admHand" onClick="return tryToDelete('{../@name}','{@id}');">
                    <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'DELETE')"/></xsl:attribute>
                </img>
            </td>
        </tr>
    </xsl:template>

    <!-- CURRENCY -->
    <xsl:template match="negeso:pm-property[@type='currency']">
        <tr>
            <th class="admTableTD">
                <xsl:if test="@is-i18n='true'">
                    (<xsl:value-of select="/negeso:page/@lang"/>)
                </xsl:if>
                <xsl:value-of select="@user-name"/>
                <xsl:if test="@is-required='true'">*</xsl:if>
            </th>
            <td class="admTableTDLast">
                <input type="hidden" name="{@name}_id" value="{@id}"/>
                <input class="admTextArea" type="text" name="{@name}_value" data_type="currency">
                    <xsl:attribute name="value"><xsl:value-of select="@absolute"/></xsl:attribute>
                    <xsl:attribute name="uname"><xsl:value-of select="@user-name"/></xsl:attribute>
                    <xsl:if test="@is-required='true'">
                        <xsl:attribute name="required">true</xsl:attribute>
                    </xsl:if>
                </input>
                &#160;<xsl:value-of select="@currency-code"/>
            </td>
        </tr>
    </xsl:template>

    <!-- DOC_FILE PROPERTY -->
    <xsl:template match="negeso:pm-property[@type='doc_file']">
        <tr>
            <th class="admTableTD">
                <xsl:if test="@is-i18n='true'">
                    (<xsl:value-of select="/negeso:page/@lang"/>)
                </xsl:if>
                <xsl:value-of select="@user-name"/>
                <xsl:if test="@is-required='true'">*</xsl:if>&#160;
            </th>
            <td class="admTableTDLast">
                <input type="hidden" name="{@name}_id" value="{@id}"/>
                <input type="hidden" id="{@name}_value_id" name="{@name}_value" value="{@file-link}"/>
            
                <table cellpadding="0" cellspacing="0">
                    <tr>
                        <td width="80%" class="admPadLeft5">
                            <a class="admAnchor" id="{@name}_atag_id" href="{@file-link}" onClick="return openDocument({@name}_value_id.value)">
                                <xsl:value-of select="@file-link"/>
                            </a>
                        </td>
                        <td nowrap="nowrap">
                            <div class="admNavPanelInp">
                                <div class="imgL"></div>
                                <div>
                                    <input name="selectDocumentButton" onClick="selectDocumentDialog(document.operateForm.{@name}_value_id, {@name}_atag_id);" type="button" class="admNavPanelInp">
                                        <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'SELECT')"/></xsl:attribute>
                                    </input>
                                </div>
                                <div class="imgR"></div>
                            </div>                           
                        </td>
                        <td nowrap="nowrap">
                            <div class="admNavPanelGrey">
                                <div class="imgL"></div>
                                <div>
                                    <input name="clearImageButton" onClick="clearDocument(document.operateForm.{@name}_value_id, {@name}_atag_id)" type="button" class="admNavPanelGrey">
                                        <xsl:attribute name="value">
                                            <xsl:value-of select="java:getString($dict_common, 'CLEAR')"/>
                                        </xsl:attribute>
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
                </table>
            </td>
        </tr>
    </xsl:template>

    <!-- SELECT DROPDOWN PROPERTY -->
    <xsl:template match="negeso:pm-property[@type='select_dropdown']">
        <tr>
            <th class="admTableTD">
                <xsl:if test="@is-i18n='true'">
                    (<xsl:value-of select="/negeso:page/@lang"/>)
                </xsl:if>
                <xsl:choose>
                    <xsl:when test="@is-dictionary='true'">
                        <xsl:value-of select="java:getString($dict_import, @user-name)"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="@user-name"/>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:if test="@unit">
                    <xsl:text></xsl:text>
                    (<xsl:value-of select="@unit"/>)
                </xsl:if>
                <xsl:if test="@is-required='true'">*</xsl:if>
            </th>
            <td class="admTableTDLast">
                <input type="hidden" name="{@name}_id" value="{@id}"/>
                <select class="admFieldMargin admWidth200" name="{@name}_value">
                    <xsl:attribute name="value"><xsl:value-of select="@value"/></xsl:attribute>
                    <xsl:attribute name="uname"><xsl:value-of select="@user-name"/></xsl:attribute>
                    <xsl:if test="@is-required='true'">
                        <xsl:attribute name="required">true</xsl:attribute>
                    </xsl:if>
                    <xsl:apply-templates select="negeso:option">
                        <xsl:with-param name="isDictionary" select="@is-dictionary"/>
                    </xsl:apply-templates>
                </select>
            </td>
        </tr>
    </xsl:template>

    <xsl:template match="negeso:option">
        <xsl:param name="isDictionary"/>
        <option value="{@id}">
            <xsl:if test="@selected='true'">
                <xsl:attribute name="selected">true</xsl:attribute>
            </xsl:if>
            <xsl:choose>
                <xsl:when test="$isDictionary='true' and @value != ' '">
                    <xsl:value-of select="java:getString($dict_import, @value)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="@value"/>
                </xsl:otherwise>
            </xsl:choose>
        </option>
    </xsl:template>

    <!-- THUMBNAIL PROPERTY -->
    <xsl:template match="negeso:pm-property[@type='thumbnail']">
        <tr>
            <th class="admTableTD">
                <xsl:if test="@is-i18n='true'">
                    (<xsl:value-of select="/negeso:page/@lang"/>)
                </xsl:if>
                <xsl:value-of select="@user-name"/>
                <xsl:if test="@is-required='true'">*</xsl:if>
            </th>
            <td class="admTableTDLast">
                <input type="hidden" name="{@name}_id" value="{@id}"/>
                <input type="hidden" id="{@name}_thlink_id" name="{@name}_thlink_value" value="{@th-link}"/>
                <input type="hidden" id="{@name}_link_id" name="{@name}_link_value" value="{@link}"/>                
                <img id="{@name}_img_id" src="{@th-link}" class="admBorder admHand"
                    onClick="return openImage({@name}_link_id.value, '{@width}', '{@height}')">
                    <xsl:if test="@th-width">
                        <xsl:attribute name="width">
                            <xsl:value-of select="@th-width"/>
                        </xsl:attribute>
                    </xsl:if>
                    <xsl:if test="@th-height">
                        <xsl:attribute name="height">
                            <xsl:value-of select="@th-height"/>
                        </xsl:attribute>
                    </xsl:if>
                </img>
                <br/>
                <table cellpadding="0" cellspacing="0">
                    <tr>
                        <td width="80%">
                            <a class="admAnchor" id="{@name}_a_id" href="{@link}"
                                onClick="return openImage({@name}_link_id.value, '{@width}', '{@height}')"
    					>
                                <xsl:value-of select="@link"/>
                            </a>
                        </td>
                        <td nowrap="nowrap">
                            <div class="admNavPanelInp">
                                <div class="imgL"></div>
                                <div>
                            <input
                                name="selectImageButton"
                                onClick="{@name}_link_id.resizeWidth = {@resize-width};
					    			{@name}_link_id.resizeHeight = {@resize-height};
					    	    	 selectThumbnailImageDialog({@name}_link_id, {@name}_thlink_id, {@name}_a_id, {@name}_img_id)"
                                type="button"
                                        class="admNavPanelInp"
    					>
                                        <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'SELECT')"/></xsl:attribute>
                            </input>
                                </div>
                                <div class="imgR"></div>
                            </div>                            
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </xsl:template>

    <!-- ARTICLE PROPERTY -->
    <xsl:template match="negeso:pm-property[@type='article']">
        <tr>
            <th class="admTableTD">
                <xsl:if test="@is-i18n='true'">
                    (<xsl:value-of select="/negeso:page/@lang"/>)
                </xsl:if>
                <xsl:value-of select="@user-name"/>
                <xsl:if test="@is-required='true'">*</xsl:if>
            </th>
            <td class="admTableTDLast">
                <input type="hidden" name="{@name}_id" value="{@id}"/>
                <input type="hidden" name="backup" value="{negeso:article/negeso:text/text()}"/>
                <xsl:apply-templates select="negeso:article"/>
            </td>
        </tr>
    </xsl:template>

    <!-- CHECKBOX PROPERTY -->
    <xsl:template match="negeso:pm-property[@type='checkbox']">
        <tr>
            <th class="admTableTD">
                <xsl:value-of select="@user-name"/>
            </th>
            <td class="admTableTDLast">
                <input type="hidden" name="{@name}_id" value="{@id}"/>
                <input class="admTextArea" type="checkbox" name="{@name}_value" value="true">
                    <xsl:if test="@value = 'true'">
                        <xsl:attribute name="checked">true</xsl:attribute>
                    </xsl:if>
                </input>
            </td>
        </tr>
    </xsl:template>

    <!-- ********************************** Article *********************************** -->
    <xsl:template match="negeso:article" >
        <input id="{@id}" name="article_text" type="hidden"/>
        <img src="/images/mark_1.gif" onclick="edit_text('article_text{@id}', 'contentStyle', 595);" class="admBorder admHand admFieldMargin" alt="Edit a description"/>

        <div id="article_text{@id}_temp" class="contentStyle" style="border: 1px solid #848484; margin:5px;">

        </div>
        <!--<div id="article_text{@id}" class="contentStyle admFieldMargin" >
 	    <xsl:attribute name="style">behavior:url(/script/article3.htc); border: 1px solid #848484;</xsl:attribute>
		<xsl:choose>
			<xsl:when test="negeso:text/text()">
				<xsl:value-of select="negeso:text" disable-output-escaping="yes"/>
			</xsl:when>
			<xsl:otherwise>&#160;</xsl:otherwise>
		</xsl:choose>
	</div>-->
    </xsl:template>


    <!-- ********************************** Language *********************************** -->
    <xsl:template match="negeso:languages">
        <select name="langIdSelected" id="langIdSelected" onChange="return onChangeLanguage()" class="admWidth150">
            <xsl:if test="/negeso:page/negeso:pm-product/@new='true'">
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
                (<xsl:value-of select="java:getString($dict_common, 'DEFAULT')"/>)
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
                <span class="admSecurity admLocation">
                    <xsl:value-of select="@title"/>
                </span>
            </xsl:when>
            <xsl:otherwise>
                <!-- Unactive pathe element - make it link-->
                <span class="admZero admLocation">
                    <a class="admLocation" href="{@link}">
                        <xsl:value-of select="@title"/>
                    </a>
                    &#160;&gt;
                </span>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- ******************************* Error message ********************************** -->
    <xsl:template match="errorMessage">
        <xsl:value-of select="errorMessage"/>
    </xsl:template>

</xsl:stylesheet>

