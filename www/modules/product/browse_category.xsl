<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${list.xsl}       
 
  Copyright (c) 2004 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  View of a list item edit interface.
 
  @version      2004.05.29
  @author       Olexiy.Strashko
  @author     Volodymyr Snigur
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

<!-- NEGESO JAVASCRIPT Temaplate -->    
<xsl:template name="java-script">
    <script language="JavaScript">
        var s_DeleteProductConfirmation = "<xsl:value-of select="java:getString($dict_product, 'DELETE_PRODUCT_CONFIRMATION')"/>";

    <xsl:text disable-output-escaping="yes">
        <![CDATA[
        function Add() {
            document.operateForm.command.value = "pm-get-edit-category-page";
            document.operateForm.updateTypeField.value = "insert";
           //document.operateForm.submit();
        }

        function Delete(targetId) {
            if (confirm("Are you sure you want to delete this category?")) {
                document.operateForm.command.value = "pm-delete-category";
                document.operateForm.pmTargetId.value = targetId;
                document.operateForm.submit();
                return true;
            }
            return false;
        }

        function Edit(targetId) {
         document.operateForm.command.value = "pm-get-edit-category-page";
            document.operateForm.pmTargetId.value = targetId;
            document.operateForm.submit();
        }

        function MoveUp(targetId) {
         document.operateForm.command.value = "pm-reorder-category";
            document.operateForm.pmTargetId.value = targetId;
            document.operateForm.type.value = "up";
            document.operateForm.submit();
        }

        function MoveDown(targetId) {
         document.operateForm.command.value = "pm-reorder-category";
            document.operateForm.pmTargetId.value = targetId;
            document.operateForm.type.value = "down";
            document.operateForm.submit();
        }
        
        
        // PRODUCT MANAGEMENT
    
        function AddProduct() {
            document.operateForm.command.value = "pm-get-edit-product-page";
            document.operateForm.updateTypeField.value = "insert";
//document.operateForm.submit();
        }

        function DeleteProduct(targetId) {
            if (confirm(s_DeleteProductConfirmation)) {
                document.operateForm.command.value = "pm-delete-product";
                document.operateForm.pmTargetId.value = targetId;
                document.operateForm.submit();
                return true;
            }
            return false;
        }

        function MoveUpProduct(targetId) {
         document.operateForm.command.value = "pm-reorder-product";
            document.operateForm.pmTargetId.value = targetId;
            document.operateForm.type.value = "up";
            document.operateForm.submit();
        }

        function MoveDownProduct(targetId) {
            document.operateForm.command.value = "pm-reorder-product";
            document.operateForm.pmTargetId.value = targetId;
            document.operateForm.type.value = "down";
            document.operateForm.submit();
        }

        function EditProduct(targetId) {
            document.operateForm.command.value = "pm-get-edit-product-page";
            document.operateForm.pmTargetId.value = targetId;
            document.operateForm.submit();
        }

        ]]>
        </xsl:text>
    </script>
</xsl:template>
<!-- MAIN ENTRY -->


<xsl:template match="/">
	<xsl:choose>
		<xsl:when test ="/negeso:page/negeso:request/negeso:parameter[@name='ajax']='json'">
			<xsl:call-template name ="json" />
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name ="usual-mode" />
		</xsl:otherwise>
	</xsl:choose>
	
</xsl:template>

	<xsl:template name ="json">
		[
		<xsl:for-each select ="//negeso:pm-category[@position='current']/negeso:pm-category">
			{
			id : <xsl:value-of select ="@id" />,
			type :  'folder',
			canBeSelected :  '<xsl:value-of select ="@is-leaf" />',
			url : '/admin/?command=pm-browse-category&amp;pmCatId=<xsl:value-of select ="@id" />&amp;ajax=json',
			title : "<xsl:value-of select ="@title"/>"
			}<xsl:if test ="position()!=last()">,</xsl:if>
		</xsl:for-each>
		]
	</xsl:template>

<xsl:template name="usual-mode">
    <html>
        <head>
            <title>
                <xsl:value-of select="java:getString($dict_product, 'BROWSE_PRODUCT_CATEGORIES')"/>
            </title>
            <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
            <xsl:call-template name="java-script"/>
            <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
            <script type="text/javascript" src="/script/jquery.min.js" />
			<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
            <script type="text/javascript" src="/script/cufon-yui.js"></script>
            <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
            <script type="text/javascript" src="/script/common_functions.js"></script>
            
            <script type="text/javascript" src="/script/common_admin.js" />
            <script type="text/javascript">
                var currSortDirection = '<xsl:value-of select="//negeso:parameter[@name='sort']/negeso:value"/>';
            </script>
        </head>
        <body>
            <!-- NEGESO HEADER -->
            <xsl:choose>
                <xsl:when test="/negeso:page/negeso:pm-category/@is-leaf!=0">
                    <xsl:call-template name="NegesoBody">
                        <xsl:with-param name="helpLink">
                            <xsl:text>/admin/help/cpr1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
                        </xsl:with-param>
                        <xsl:with-param name="backLink" select="concat('?command=pm-browse-category&amp;pmCatId=', negeso:pm-category/@parent-id)"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:call-template name="NegesoBody">
                        <xsl:with-param name="helpLink">
                            <xsl:text>/admin/help/cpr1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
                        </xsl:with-param>
                        <xsl:with-param name="backLink" select='"?command=pm-get-entry-page"' />
                    </xsl:call-template>
                </xsl:otherwise>
            </xsl:choose>
        </body>
    </html>
</xsl:template>

<xsl:template match="negeso:page">
    
  
</xsl:template>

<!-- ********************************** Category *********************************** -->

    <xsl:template match="negeso:page"  mode="admContent">
        <xsl:apply-templates select="negeso:pm-category" />
    </xsl:template>    
        
<xsl:template match="negeso:pm-category">
    
    <xsl:choose>
        <xsl:when test="@position='current'">
            <!-- Render HEADER -->
            <form method="POST" name="operateForm" action="">
                <xsl:if test="@is-leaf='true'">
                    <xsl:attribute name="onsubmit">document.all.productType.value=document.all.prodType.value</xsl:attribute>
                </xsl:if>
                <input type="hidden" name="command" value="browse-category"></input>
                <input type="hidden" name="type" value="none"></input>
                <input type="hidden" name="pmTargetId" value="-1"></input>
                <input type="hidden" name="pmCatId" value="{@id}"></input>
                <input type="hidden" name="updateTypeField" value="update"></input>
                <input type="hidden" name="productType" ></input>
                <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
                    <tr>
                        <td><!-- PATH -->
	<xsl:call-template name="pm.shopPath"/></td>
                    </tr>
                    <tr>
                        <td align="center" class="admNavPanelFont" >
                            <!-- TITLE -->
                            <xsl:call-template name="tableTitle">
                                <xsl:with-param name="headtext">
                                    <xsl:value-of select="java:getString($dict_product, 'BROWSE_PRODUCT_CATEGORIES')"/>
                                </xsl:with-param>
                            </xsl:call-template>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <table cellpadding="0" cellspacing="0" width="100%">
                                <tr>
                                    <td class="admTableTDLast" width="200px;">
                                        <xsl:choose>
                                            <xsl:when test="@is-leaf='false'">

                                                <div class="admNavPanelInp">
                                                    <div class="imgL"></div>
                                                    <div>
                                                        <input class="admNavPanelInp" type="submit" onClick = "Add()" style="width:150px;">
                                                            <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'ADD_NEW_CATEGORY')"/></xsl:attribute>
                                                        </input>
                                                    </div>
                                                    <div class="imgR"></div>
                                                </div>

                                              
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <div class="admNavPanelInp">
                                                    <div class="imgL"></div>
                                                    <div>
                                                        <input class="admNavPanelInp" type="submit" onClick = "return AddProduct()" style="width:150px;">
                                                            <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_product, 'ADD_NEW_PRODUCT')"/></xsl:attribute>
                                                        </input>
                                                    </div>
                                                    <div class="imgR"></div>
                                                </div>
                                                
                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </td>
                                    <xsl:if test="@is-leaf='true'">
                                        <th class="admTableTDLast" width="50px;">
                                            <select name="prodType" style="background-color: white;">
                                                <xsl:for-each	select="/negeso:page/negeso:ProductTypes/negeso:ProductType">
                                                    <option>
                                                        <xsl:attribute name="value">
                                                            <xsl:value-of select="@id" />
                                                        </xsl:attribute>
                                                        <xsl:if test="/negeso:page/@product-type-id=@id">
                                                            <xsl:attribute name="selected">selected</xsl:attribute>
                                                        </xsl:if>
                                                        <xsl:value-of select="." />
                                                    </option>
                                                </xsl:for-each>
                                            </select>
                                        </th>
                                    </xsl:if>
                                    <xsl:if test="@is-leaf='true'">
                                        <td  class="admTableTDLast" >

                                            <div class="admNavPanelInp" style="width:150px;">
                                                <div class="imgL"></div>
                                                <div style="padding:10px 5px 0 5px;">
                                                    <a class="sortAZ" href="" style="text-decoration:none;color:fff;" >
                                                        <xsl:value-of select="java:getString($dict_product, 'A-Z')"/>
                                                    </a>
                                                </div>
                                                <div class="imgR"></div>
                                            </div>
                                            
                                            
                                            
                                         
                                        </td>
                                    </xsl:if>
                                </tr>
                            </table>
                            <table  cellspacing="0" cellpadding="0" id="folderContentTable" width="100%">
                                <tr>
                                    <td class="admTDtitles">
                                        <xsl:value-of select="java:getString($dict_common, 'ORDER')"/>
                                    </td>
                                    <td class="admTDtitles">&#160;</td>
                                    <td class="admTDtitles">
                                        <xsl:value-of select="java:getString($dict_common, 'TITLE')"/>
                                    </td>
                                    <td class="admTDtitles">
                                        <xsl:value-of select="java:getString($dict_common, 'PUBLISH_DATE')"/>
                                    </td>
                                    <td class="admTDtitles">
                                        <xsl:value-of select="java:getString($dict_common, 'EXPIRED_DATE')"/>
                                    </td>
                                    <td class="admTDtitles" align="center" style="padding-left:0px;" colspan="4">
                                        <xsl:value-of select="java:getString($dict_common, 'ACTION')"/>
                                    </td>
                                    
                                </tr>
                                <xsl:apply-templates select="negeso:pm-category"/>
                                <xsl:choose>
                                    <xsl:when test="//negeso:parameter[@name='sort']/negeso:value='desc'">
                                        <xsl:apply-templates select="negeso:pm-product">
                                            <xsl:sort select="@title" order="descending"/>
                                        </xsl:apply-templates>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:apply-templates select="negeso:pm-product">
                                            <xsl:sort select="@title" order="ascending"/>
                                        </xsl:apply-templates>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </table>
                        </td>
                    </tr>
                    <tr>
                        <td class="admTableFooter" >&#160;</td>
                    </tr>
                </table>
				
				<div class="floatMenu fileUploadQueue" id="floatMenu">
                    <div class="inner">
                        <a href="#" onclick="$('#folderContentTable INPUT[subType=file]').attr('checked','true');$('#folderContentTable TD.checkBoxC IMG').attr('src', '/images/checkbox_checked.gif') ;return false;">Select all products</a>
                        <br />
                        <a href="#" onclick="$('#folderContentTable INPUT[subType=file]').removeAttr('checked');$('#folderContentTable TD.checkBoxC IMG').attr('src', '/images/checkbox_unchecked.gif') ;return false;">Clear selection</a>
                        <br/>
                        <br/>
                        <a href="#" title="Copy to" onclick="groupProductOperation('copy')">Copy to</a>
                        <br/>
                        <a href="#" title="Move to" onclick="groupProductOperation('move')">Move to</a>
                        <br/>
                        <br/>
                        <a href="#" title="Delete selected" onclick="groupProductOperation('delete')">Delete selected</a>
                    </div>
                </div>
            </form>
        </xsl:when>
        <xsl:otherwise>            
            <tr>
                <th class="admTableTD" colspan="2">
                    <xsl:value-of select="@order-number"/>
                </th>
                <th class="admTableTD" >
                    <a class="admAnchor" href="?command=pm-browse-category&amp;pm-cat-id={@id}">                        
                        <xsl:attribute name="href">?command=pm-browse-category&amp;pmCatId=<xsl:value-of select="@id"/></xsl:attribute>
                        <xsl:value-of select="@title" disable-output-escaping="yes"/>
                    </a>
                </th>
                <th class="admTableTD" >
                    <xsl:value-of select="@publish-date"/>
                </th>
                    <th class="admTableTD" >
                    <xsl:value-of select="@expired-date"/>
                </th>

                <td class="admTableTDLast admWidth30">
                    <img src="/images/edit.png" class="admHand" onClick="Edit({@id})">
                        <xsl:attribute name="title">
                            <xsl:value-of select="java:getString($dict_common, 'EDIT')"/>
                        </xsl:attribute>
                    </img>
                </td>
                <td class="admTableTDLast admWidth30">
                    <img src="/images/up.png" class="admHand" onClick="MoveUp({@id})">
                        <xsl:attribute name="title">
                            <xsl:value-of select="java:getString($dict_common, 'UP')"/>
                        </xsl:attribute>
                    </img>
                </td>
                <td class="admTableTDLast admWidth30">
                    <img src="/images/down.png" class="admHand" onClick="MoveDown({@id})">
                        <xsl:attribute name="title">
                            <xsl:value-of select="java:getString($dict_common, 'DOWN')"/>
                        </xsl:attribute>
                    </img>
                </td>
                <td class="admTableTDLast admWidth30">
                    <img src="/images/delete.png" class="admHand" onClick="return Delete({@id})">
                        <xsl:attribute name="title">
                            <xsl:value-of select="java:getString($dict_common, 'DELETE')"/>
                        </xsl:attribute>
                    </img>
                </td>
            </tr>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>

<!-- ********************************** Product *********************************** -->
<xsl:template match="negeso:pm-product">
    <tr>
        <th class="admTableTD"><xsl:value-of select="@order-number"/>
		
		</th>
        <th class="admTableTDLast">
            <a class="admAnchor" href="#" onClick="return EditProduct({@id})">
                <img class="admBorder" width="50" src="{@image-link}" onerror="this.src='/images/no_image_52.gif';"/>
            </a>
            <xsl:if test="@payment-test">&#160;<b style="color:blue">[TEST]</b> </xsl:if>
        </th>
        <th class="admTableTD">
            <a class="admAnchor" href="#" onClick = "return EditProduct({@id})">
                <xsl:value-of select="@title" disable-output-escaping="yes"/>
            </a>
        </th>
        <th class="admTableTD"><xsl:value-of select="@publish-date"/></th>
        <th class="admTableTDLast"><xsl:value-of select="@expired-date"/>&#160;</th>
		<td class="admTableTD noPadding checkBoxC">
			<input fileId="{@id}" subType="file" filename="{@name}" class="fileCheckBox" type="checkbox" />
			

		</td>
        <td class="admTableTDLast admWidth30">
            <img src="/images/edit.png" class="admHand" onClick="return EditProduct({@id})">
                <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'EDIT')"/></xsl:attribute>
         </img>
        </td>
        <td class="admTableTDLast admWidth30">
            <img src="/images/up.png" class="admHand" onClick="return MoveUpProduct({@id})">
                <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'UP')"/></xsl:attribute>
         </img>
        </td>
        <td class="admTableTDLast admWidth30">
            <img src="/images/down.png" class="admHand" onClick="return MoveDownProduct({@id})">
                <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'DOWN')"/></xsl:attribute>
         </img>
        </td>
        <td class="admTableTDLast admWidth30">
            <img src="/images/delete.png" class="admHand" onClick="return DeleteProduct({@id})">
                <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'DELETE')"/></xsl:attribute>
         </img>
        </td>
    </tr>
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
			    </a>
                    &#160; <img src="/images/navig_arrow.png"  style="vertical-align:middle;"/>&#160;
				</span>

                <xsl:choose>
                    <xsl:when test="@active='true'">
                        <!-- Active pathe element - just print it-->
                        <span>
                            <xsl:value-of select="@title"/>
                        </span>
                    </xsl:when>
                    <xsl:otherwise>
                        <!-- Unactive pathe element - make it link-->
                        <span >
                            <a class="admNavigation" style="text-decoration:none;" href="{@link}">
                                <xsl:value-of select="@title"/>
                            </a>
                          
                        </span>
                    </xsl:otherwise>
                </xsl:choose>
            </td>
        </tr>
    </table>
</xsl:template>


    
    
    
    


</xsl:stylesheet>
