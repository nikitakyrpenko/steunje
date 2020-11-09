<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${list.xsl}

  Copyright (c) 2004 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential
  Information and shall use it only in accordance with the terms of the
  license agreement you entered into with Negeso.

  View of a list item edit interface.

  @version		2004.11.08
  @author		Alexander G. Shkabarnya
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
<xsl:variable name="dict_picture_frame_adds" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_picture_frame_adds.xsl', $lang)"/>
<xsl:variable name="param" select="/negeso:page/negeso:request/negeso:parameter" />
<xsl:variable name="show" select="/negeso:page/negeso:request/negeso:parameter[@name='show']/negeso:value" />	

<xsl:template match="//negeso:page">
<html>
<head>
    <title><xsl:value-of select="java:getString($dict_picture_frame_adds, 'ADV_PF.IMAGE_ATTRIBUTES_EDITING')"/></title>
	   
    <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>    
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>

    <script type="text/javascript" src="/script/jquery.min.js"></script>
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
    <script type="text/javascript" src="/script/common_functions.js" xml:space="preserve"> </script>
    <script type="text/javascript" src="/script/media_catalog.js" xml:space="preserve"> </script>
    
    <script>
    
    var isID = null;
	var atrSetId = null;
    var attrClass = null;
    var imId = null;
    var action = null;
    
    var msgSmallWidth = "<xsl:value-of select="java:getString($dict_picture_frame_adds, 'ADV_PF.SMALL_WIDTH')"/>";
    
    window.name = "PictureSelectionDialog";
        function changeMainImage(imId_, atrSetId_, width, height){
        
			atrSetId = atrSetId_;
			imId = imId_;
			action = "change_main_image";
                
            MediaCatalog.selectStrictImageDialog(width, height, "picture_frame", "", function(result){
            if (result != null){
                if (result.resCode == "OK"){
                    main_form.action_field.value="change";
                    main_form.src_field.value=result.realImage;
                    main_form.image_id_field.value=imId;
                    main_form.attribute_set_id_field.value = atrSetId;
                    main_form.submit();
                }
            }
             });
        }
        
        function changeMainFlash(imId_, atrSetId_, width, height){			
			atrSetId = atrSetId_;
			imId = imId_;
			action = "change_main_flash";
            
            var result = MediaCatalog.selectStrictFlashDialog(width, height, "picture_frame", '', function(result){
            if (result != null){
                if (result.resCode == "OK"){
                    main_form.action_field.value="changeFlash";
                    main_form.src_field.value=result.fileUrl;
                    main_form.flash_width.value=result.flashWidth;
                    main_form.flash_height.value=result.flashHeight;
                    main_form.image_id_field.value=imId;
                    main_form.attribute_set_id_field.value = atrSetId;
                    alert('1:'+main_form.action_field.value+'\n2:'+main_form.src_field.value+'\n3:'+main_form.flash_width.value+'\n4:'+main_form.flash_height.value+'\n5:'+main_form.image_id_field.value+'\n6:'+main_form.attribute_set_id_field.value);
                    main_form.submit();
                }
            }
            });
        }
        
        function upImage(imId, atrSetId){
                
            main_form.action_field.value="up";
            main_form.src_field.value="";
            main_form.image_set_id_field.value="";
            main_form.image_id_field.value=imId;
            main_form.attribute_set_id_field.value = atrSetId;
            main_form.submit();
        }
        
        function downImage(imId, atrSetId){
            main_form.action_field.value="down";
            main_form.src_field.value="";
            main_form.image_set_id_field.value="";
            main_form.image_id_field.value=imId;
            main_form.attribute_set_id_field.value = atrSetId;
            main_form.submit();
        }
        
		function deleteImage(imId, atrSetId, show){
            var s_DeleteImageConfirmation = "<xsl:value-of select="java:getString($dict_common, 'DELETE_CONFIRMATION')"/>";
        if (confirm(s_DeleteImageConfirmation) == true) {
        main_form.action_field.value="delete";
        main_form.src_field.value="";
        main_form.image_id_field.value=imId;
        main_form.attribute_set_id_field.value = atrSetId;
        if ( show!= null)
        main_form.show.value = show;
        main_form.submit();}
        }

        function changeImage(imId_, isID_, atrSetId_, current_width, height, maxWidth, flash_quantity){
        isID = isID_;
        atrSetId = atrSetId_;
        imId = imId_;
        action = "change_image";

        MediaCatalog.selectStrictByHeightImageDialog(current_width, height, maxWidth, flash_quantity, "picture_frame", function(result){

        if (result != null){
        if (result.resCode == "OK"){
        main_form.action_field.value="change";
        main_form.src_field.value=result.realImage;
        main_form.image_set_id_field.value=isID;
        main_form.image_id_field.value=imId;
        main_form.attribute_set_id_field.value = atrSetId;
        main_form.submit();
        }
        }
         })

        }
        function setLink(imId){
        MediaCatalog.selectLinkImageDialog(imId, function(result){
        if (result != null){
        if (result.resCode == "OK"){
        main_form.action="update-wcsm-link";
        main_form.image_id_field.value=imId;
        main_form.submit();
        }
        }
        });
        }
        function changeFlash(imId_, isID_, atrSetId_, width, height, maxWidth, flash_quantity, current_width){

        imId = imId_;
        isID = isID_;
        atrSetId = atrSetId_;
        action = "change_flash";

        MediaCatalog.selectStrictByHeightFlashDialog(height, maxWidth, flash_quantity, 'picture_frame', current_width, function(result){

        if (result != null){
        if (result.resCode == "OK"){
        main_form.action_field.value="changeFlash";
        main_form.src_field.value=result.fileUrl;
        main_form.flash_width.value=result.flashWidth;
        main_form.flash_height.value=result.flashHeight;
        main_form.image_id_field.value=imId;
        main_form.attribute_set_id_field.value = atrSetId;
        alert('1:'+main_form.action_field.value+'\n2:'+main_form.src_field.value+'\n3:'+main_form.flash_width.value+'\n4:'+main_form.flash_height.value+'\n5:'+main_form.image_id_field.value+'\n6:'+main_form.attribute_set_id_field.value);
        main_form.submit();
        }
        }
        });
        }

        function addImage(isID_, atrSetId_, attrClass_, height, width, show){
        isID = isID_;
        atrSetId = atrSetId_;
        attrClass = attrClass_;
        action = "add_image";
			var result = MediaCatalog.selectStrictImageDialog(width, height, '', show, function(result){
            if (result != null){
                if (result.resCode == "OK"){
                    main_form.action_field.value="add";
                    main_form.src_field.value=result.realImage;
                    main_form.image_set_id_field.value=isID;
                    main_form.image_id_field.value="";
                    main_form.attribute_set_id_field.value = atrSetId;
                    main_form.attribute_class_field.value = attrClass;
					if ( show!= null)
						main_form.show.value = show;
                    main_form.submit();
                }
            }
             });
        }
        
		function changeAnimationProperties(isID_){
	        isID = isID_;
			var result = MediaCatalog.selectAnimationProperties(isID);            
        }
        
        function resultUploadImage(){
	    	var result = returnValue;
            if (result != null){
                if (result.resCode == "OK"){
                
                	if(action == "add_image"){
	                    main_form.action_field.value="add";
	                    main_form.src_field.value=result.realImage;
	                    main_form.image_set_id_field.value=isID;
	                    main_form.image_id_field.value="";
	                    main_form.attribute_set_id_field.value = atrSetId;
	                    main_form.attribute_class_field.value = attrClass;
	                }
	                
	                if(action == "change_image"){
	                    main_form.action_field.value="change";
	                    main_form.src_field.value=result.realImage;
	                    main_form.image_set_id_field.value=isID;
	                    main_form.image_id_field.value=imId;
	                    main_form.attribute_set_id_field.value = atrSetId;
	                }
	                
	                if(action == "change_flash"){
	                    main_form.action_field.value="changeFlash";
	                    main_form.src_field.value=result.fileUrl;
	                    main_form.flash_width.value=result.flashWidth;
	                    main_form.flash_height.value=result.flashHeight;
	                    main_form.image_id_field.value=imId;
	                    main_form.attribute_set_id_field.value = atrSetId;                    
	                    main_form.image_set_id_field.value=isID;
	                }
	                
                	if(action == "change_main_image"){
	                    main_form.action_field.value="change";
	                    main_form.src_field.value=result.realImage;
	                    main_form.image_id_field.value=imId;
	                    main_form.attribute_set_id_field.value = atrSetId;
                	}
                	
                	if(action == "change_main_flash"){
	                    main_form.action_field.value="changeFlash";
	                    main_form.src_field.value=result.fileUrl;
	                    main_form.flash_width.value=result.flashWidth;
	                    main_form.flash_height.value=result.flashHeight;
	                    main_form.image_id_field.value=imId;
	                    main_form.attribute_set_id_field.value = atrSetId;
                	}
                    main_form.submit();
                }
			}
        }

        
        function addFlash(isID, atrSetId, attrClass, height, maxWidth, flash_quantity){
        	if(maxWidth &lt;= 0) {
        		alert(msgSmallWidth);
        		return false;
        	}
            var result = MediaCatalog.selectStrictByHeightFlashDialog(height, maxWidth, flash_quantity, "picture_frame", '0', function(result){
            if (result != null){
                if (result.resCode == "OK"){
                    main_form.action_field.value="addFlash";
                    main_form.src_field.value=result.fileUrl;
                    main_form.flash_width.value=result.flashWidth;
                    main_form.flash_height.value=result.flashHeight;
                    main_form.image_set_id_field.value=isID;
                    main_form.image_id_field.value="";
                    main_form.attribute_set_id_field.value = atrSetId;
                    main_form.attribute_class_field.value = attrClass;
                    main_form.submit();
                }
            }
            });
        }
        
    </script>
</head>
<body>    
	   <xsl:call-template name="NegesoBody">
    <xsl:with-param name="helpLink" select="'/admin/help/cms-help_nl.html'"/>
        <xsl:with-param name="backLink" select="''" />
    </xsl:call-template>
  
   <form id="main_form" method="POST" enctype="multipart/form-data" action="update_wcsm_attributes" 
        target="PictureSelectionDialog">
        <input type="hidden" id="action_field" name="action_field"/>
        <input type="hidden" id="src_field" name="src_field"/>
        <input type="hidden" id="image_set_id_field" name="image_set_id_field"/>
        <input type="hidden" id="image_id_field" name="image_id_field"/>
        <input type="hidden" id="attribute_set_id_field" name="attribute_set_id_field"/>
        <input type="hidden" id="attribute_class_field" name="attribute_class_field"/>
        <input type="hidden" id="flash_width" name="flash_width"/>
        <input type="hidden" id="flash_height" name="flash_height"/>
	       <input type="hidden" id="delay" name="delay"/>
	       <input type="hidden" id="step" name="step"/>
	       <input type="hidden" id="speed_of_animation" name="speed_of_animation"/>
	       <input type="hidden" id="show" name="show" value="{$show}"/>
   	</form>
</body>
</html>
</xsl:template>

<xsl:template match="negeso:page"  mode="admContent">
    <xsl:apply-templates select="negeso:wcms_attributes" mode="attributes"/>
</xsl:template>
    
<xsl:template match="negeso:wcms_attributes" mode="attributes">
    <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
        <tr>
            <td align="center">
                <xsl:if test="not($param[@name='show']/negeso:value='imagesAnimation')">
                    <xsl:apply-templates select="negeso:image_set[@number_of_images]"/>
                </xsl:if>
                <xsl:apply-templates select="negeso:image_set[string(@number_of_images)='']"/>
            </td>
        </tr>
        <tr>
            <td class="admTableFooter" >&#160;</td>
        </tr>
    </table>
</xsl:template>

<xsl:template match="negeso:image_set">
    <xsl:choose>
        <xsl:when test="@number_of_images">
            <table align="center"  border="0" cellpadding="0" cellspacing="0" width="100%">
                <tr>
                    <td align="center" class="admNavPanelFont" >
                        <!-- TITLE -->
                        <xsl:call-template name="tableTitle">
                            <xsl:with-param name="headtext">
                                <xsl:value-of select="java:getString($dict_picture_frame_adds, 'ADV_PF.IMAGE_ATTRIBUTES_EDITING')"/>
                            </xsl:with-param>
                        </xsl:call-template>
                    </td>
                </tr>
                <tr>
           	        <td align="center" class="admTDtitles" style="height:auto;">
           	        	<xsl:value-of select="java:getString($dict_picture_frame_adds, 'ADV_PF.MAIN_IMAGE')"/>
                    </td>
                </tr>
                <xsl:apply-templates select="negeso:image" mode="main_image" />
            </table>
        </xsl:when>
        <xsl:otherwise>

            <table align="center"  border="0" cellpadding="0" cellspacing="0" width="100%">
                <tr>
                    <td align="center" class="admTDtitles" style="height:auto;" >
                        <xsl:value-of select="java:getString($dict_picture_frame_adds, 'ADV_PF.BOTTOM_IMAGES')"/>
                    </td>
                </tr>
                <tr>
                    <td class="admTableTDLast" align="center">
                        <table align="center" width="400" border="0">
                            <tr>
                                <td align="center" >
                                    <div class="admNavPanelInp" align="center">
                                        <div class="imgL"></div>
                                        <div align="center">
                                            <input class="admNavbarInp" type="button"
                                                onclick="addImage('{@id}', '{../@id}', '{@class}', '{negeso:image/@height}', '{negeso:image/@width}','{$show}');">
                                                <xsl:if test="@width &lt; 1"><xsl:attribute name="disabled">true</xsl:attribute></xsl:if>
                                                <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_picture_frame_adds, 'ADV_PF.ADD_IMAGE')"/></xsl:attribute>
                                            </input>
                                        </div>
                                        <div class="imgR"></div>
                                    </div>

                                    <div class="admNavPanelInp" align="center" style="padding:0;">
                                        <div class="imgL"></div>
                                        <div align="center" style="padding:0;">
                                            <input class="admNavbarInp" type="button" onclick="changeAnimationProperties('{@id}');">
                                                <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_picture_frame_adds, 'ADV_PF.CHANGE_PROPERTIES')"/></xsl:attribute>
                                            </input>
                                        </div>
                                        <div class="imgR"></div>
                                    </div>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <xsl:apply-templates select="negeso:image" mode="bottom_image" />
            </table>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>



<xsl:template match="negeso:image" mode="main_image">
<tr>
    <td class="admTableTDLast" id="admTableTDtext" align="center">
        <xsl:choose>
			<xsl:when test="@extension='swf'">
				<span style="width: {@max_width}px; height: {@max_height}px">
					<xsl:if test="(count(preceding-sibling::negeso:image) = 0) and (count(following-sibling::negeso:image) &gt; 3)">
						<xsl:attribute name="style">
							display: inline; float: left; margin-left: 7px; width: <xsl:value-of select="@max_width"/>px; height: <xsl:value-of select="@max_height"/>px;
						</xsl:attribute>
					</xsl:if>
					<embed type="application/x-shockwave-flash"
						pluginspage="http://www.macromedia.com/go/getflashplayer"
		                src="{@src}"
		                wmode="window"
		                quality="high"
		                menu="false"
		                scale="showall"
		                width="{@max_width}"
		                height="{@max_height}">
					</embed>
				</span>
			</xsl:when>
			<xsl:otherwise>
		        <img>
					<xsl:attribute name="src"><xsl:value-of select="@src"/></xsl:attribute>
					<xsl:choose>
						<xsl:when test ="@width >= 530"><xsl:attribute name ="style">width:530px; height:auto;</xsl:attribute></xsl:when>
						<xsl:otherwise>
							<xsl:if test="@width and @width!=''"><xsl:attribute name="width"><xsl:value-of select="@width"/></xsl:attribute></xsl:if>
							<xsl:if test="@height and @height!=''"><xsl:attribute name="height"><xsl:value-of select="@height"/></xsl:attribute></xsl:if>
						</xsl:otherwise>
					</xsl:choose>			       
				</img>
			</xsl:otherwise>
		</xsl:choose>
		<br/>
        <xsl:value-of select="@src"/>
    </td>
</tr>
<tr>    
    <td class="admTableTDLast" id="admTableTDtext" style="border-top: 1 solid white;"  align="right">
        <img class="admHand" src="/images/up_gray.png" title="{java:getString($dict_common, 'UP')}" alt="{java:getString($dict_common, 'UP')}"/>
        <img class="admHand" src="/images/down_gray.png" title="{java:getString($dict_common, 'DOWN')}" alt="{java:getString($dict_common, 'DOWN')}"/>        
        <img class="admHand" src="/images/pf/insert-edit-image.png" onclick="changeMainImage('{@id}', '{../../@id}', '{@width}', '{@height}');"
             title="{java:getString($dict_picture_frame_adds, 'ADV_PF.CHANGE_IMAGE')}" alt="{java:getString($dict_picture_frame_adds, 'ADV_PF.CHANGE_IMAGE')}"/>
        <img class="admHand" src="/images/pf/insert-edit-flash.png" onclick="changeMainFlash('{@id}', '{../../@id}', '{@width}', '{@height}');"
             title="{java:getString($dict_picture_frame_adds, 'ADV_PF.CHANGE_FLASH')}" alt="{java:getString($dict_picture_frame_adds, 'ADV_PF.CHANGE_FLASH')}"/>
        <img class="admHand" src="/images/pf/insert-edit-link.png" onclick="setLink('{@id}');" title="{java:getString($dict_common, 'EDIT_LINK')}" alt="{java:getString($dict_common, 'EDIT_LINK')}"/>            
        <img class="admHand" src="/images/delete_gray.png" title="{java:getString($dict_picture_frame_adds, 'ADV_PF.CHANGE_IMAGE')}" alt="{java:getString($dict_picture_frame_adds, 'ADV_PF.CHANGE_IMAGE')}"/>            
    </td>
</tr>
</xsl:template>

<xsl:template match="negeso:image" mode="bottom_image">
	<xsl:choose>
	<xsl:when test="position() mod 2 = 1">
		<tr>
			<td  class="admTableTDLast" id="admTableTDtext"  align="center">
			<xsl:choose>
				<xsl:when test="@src and @src!=''">
					<xsl:choose>
						<xsl:when test="@extension='swf'">
							<span style="width: {@max_width}px; height: {@max_height}px">
								<xsl:if test="(count(preceding-sibling::negeso:image) = 0) and (count(following-sibling::negeso:image) &gt; 3)">
									<xsl:attribute name="style">
										display: inline; float: left; margin-left: 7px; width: <xsl:value-of select="@max_width"/>px; height: <xsl:value-of select="@max_height"/>px;
									</xsl:attribute>
								</xsl:if>
								<embed type="application/x-shockwave-flash"
									pluginspage="http://www.macromedia.com/go/getflashplayer"
					                src="{@src}"
					                wmode="window"
					                quality="high"
					                menu="false"
					                scale="showall"
					                width="{@max_width}"
					                height="{@max_height}">
								</embed>
							</span>
						</xsl:when>
						<xsl:otherwise>
		                   <img src="{@src}">
							    <xsl:attribute name="src"><xsl:value-of select="@src"/></xsl:attribute>
								<xsl:choose>
									<xsl:when test ="@width >= 530"><xsl:attribute name ="style">width:530px; height:auto;</xsl:attribute></xsl:when>
									<xsl:otherwise>
										<xsl:if test="@width and @width!=''"><xsl:attribute name="width"><xsl:value-of select="@width"/></xsl:attribute></xsl:if>
										<xsl:if test="@height and @height!=''"><xsl:attribute name="height"><xsl:value-of select="@height"/></xsl:attribute></xsl:if>
									</xsl:otherwise>
								</xsl:choose>		                        
		                   </img>
						</xsl:otherwise>
					</xsl:choose>
					<br/>
					<xsl:value-of select="@src"/>&#160;
                </xsl:when>
                <xsl:otherwise>Not selected</xsl:otherwise>
             </xsl:choose>
             </td>
        </tr>
        <tr>           
            <td class="admTableTDLast" id="admTableTDtext" style="border-top: 1 solid white;"  align="right">
                <img class="admHand" src="/images/up.png" onclick="upImage('{@id}', '{../../@id}');" title="{java:getString($dict_common, 'UP')}" alt="java:getString($dict_common, 'UP')"/>			
				<img class="admHand" src="/images/down.png" onclick="downImage('{@id}', '{../../@id}');" title="{java:getString($dict_common, 'DOWN')}" alt="java:getString($dict_common, 'DOWN')"/>
	             <!-- Fixed size of picture -->
	             <xsl:choose>
	                <xsl:when test="@height and @height!='' and @width and @width!=''">
	                   <img class="admHand" src="/images/pf/insert-edit-image.png" onclick="changeMainImage('{@id}', '{../../@id}', '{@width}', '{@height}');"
                            title="{java:getString($dict_picture_frame_adds, 'ADV_PF.CHANGE_IMAGE')}" alt="java:getString($dict_picture_frame_adds, 'ADV_PF.CHANGE_IMAGE')"/>
	                </xsl:when>
	                <!-- Not fixed size picture-->                
	                <xsl:otherwise> 
	                  <img class="admHand" src="/images/pf/insert-edit-image.png">
	                        <xsl:attribute name="onclick">changeImage('<xsl:value-of select="@id"/>', '<xsl:value-of select="../@id"/>', '<xsl:value-of select="../../@id"/>', '<xsl:value-of select="@current-width"/>', '<xsl:value-of select="@height"/>', '<xsl:value-of select="../@max_width"/>', <xsl:value-of select="count(../negeso:image[@extension='swf'])"/>);</xsl:attribute>
	                        <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_picture_frame_adds, 'ADV_PF.CHANGE_IMAGE')"/></xsl:attribute>
	                  </img>
	                </xsl:otherwise>
	             </xsl:choose>			

                 <xsl:if test="not(../../negeso:image_set[@class='animation'])">
	             <!-- Fixed size of picture -->
	             <xsl:choose>
                     <xsl:when test="@height and @height!='' and @width and @width!=''">
				       <img class="admHand" src="/images/pf/insert-edit-flash.png" onclick="changeMainFlash('{@id}', '{../../@id}', '{@width}', '{@height}');">
				          <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_picture_frame_adds, 'ADV_PF.CHANGE_FLASH')"/></xsl:attribute>
				       </img>					
					</xsl:when>
	                <!-- Not fixed size picture-->                
	                <xsl:otherwise>						
						<img class="admHand" src="/images/pf/insert-edit-flash.png">
	                        <xsl:attribute name="onclick">changeFlash('<xsl:value-of select="@id"/>', '<xsl:value-of select="../@id"/>', '<xsl:value-of select="../../@id"/>', '<xsl:value-of select="@width"/>', '<xsl:value-of select="@height"/>', '<xsl:value-of select="../@max_width"/>', <xsl:value-of select="count(../negeso:image[@extension='swf'])"/>, '<xsl:value-of select="@current-width"/>');</xsl:attribute>
	                        <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_picture_frame_adds, 'ADV_PF.CHANGE_FLASH')"/></xsl:attribute>
	                  </img>
	                </xsl:otherwise>
	             </xsl:choose>             
			 </xsl:if>				
				<img class="admHand" src="/images/pf/insert-edit-link.png" onclick="setLink('{@id}');">
					<xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'EDIT_LINK')"/></xsl:attribute>
				</img>			
                 <xsl:choose>
                     <xsl:when test="count(../../negeso:image_set[@class='animation']/negeso:image)=1">
                        <img class="admHand" src="/images/delete_gray.png" >
                            <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_picture_frame_adds, 'ADV_PF.CHANGE_IMAGE')"/></xsl:attribute>
                        </img>
                     </xsl:when>
                     <xsl:otherwise>
                         <img class="admHand" src="/images/delete.png" onclick="deleteImage('{@id}', '{../../@id}','{$show}');">
                             <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'DELETE')"/></xsl:attribute>
                         </img>
                     </xsl:otherwise>
                 </xsl:choose>
             </td>
         </tr>
     </xsl:when>
     <xsl:otherwise>
         <tr>
             <td  class="admTableTDLast" id="admTableTDtext"  align="center"  style="border-top: 5 solid white;">
             <xsl:choose>
                <xsl:when test="@src and @src!=''">
                   <xsl:choose>
						<xsl:when test="@extension='swf'">
							<span style="width: {@max_width}px; height: {@max_height}px">
								<xsl:if test="(count(preceding-sibling::negeso:image) = 0) and (count(following-sibling::negeso:image) &gt; 3)">
									<xsl:attribute name="style">
										display: inline; float: left; margin-left: 7px; width: <xsl:value-of select="@max_width"/>px; height: <xsl:value-of select="@max_height"/>px;
									</xsl:attribute>
								</xsl:if>
								<embed type="application/x-shockwave-flash"
									pluginspage="http://www.macromedia.com/go/getflashplayer"
					                src="{@src}"
					                wmode="window"
					                quality="high"
					                menu="false"
					                scale="showall"
					                width="{@max_width}"
					                height="{@max_height}">
								</embed>
							</span>
						</xsl:when>
						<xsl:otherwise>
		                   <img src="{@src}">
		                        <xsl:attribute name="src"><xsl:value-of select="@src"/></xsl:attribute>
								<xsl:choose>
									<xsl:when test ="@width >= 530"><xsl:attribute name ="style">width:530px; height:auto;</xsl:attribute></xsl:when>
									<xsl:otherwise>
										<xsl:if test="@width and @width!=''"><xsl:attribute name="width"><xsl:value-of select="@width"/></xsl:attribute></xsl:if>
										<xsl:if test="@height and @height!=''"><xsl:attribute name="height"><xsl:value-of select="@height"/></xsl:attribute></xsl:if>
									</xsl:otherwise>
								</xsl:choose>	
		                   </img>
						</xsl:otherwise>
					</xsl:choose>
					<br/>
					<xsl:value-of select="@src"/>&#160;
                </xsl:when>
                <xsl:otherwise>Not selected</xsl:otherwise>
             </xsl:choose>                 
             </td>
        </tr>
        <tr>
             
             <td class="admTableTDLast" id="admTableTDtext" style="border-top: 1 solid white;"  align="right">
                <img class="admHand" src="/images/up.png" onclick="upImage('{@id}', '{../../@id}');">
                   <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'UP')"/></xsl:attribute>
                </img>
            
                 <img class="admHand" src="/images/down.png" onclick="downImage('{@id}', '{../../@id}');">
                   <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'DOWN')"/></xsl:attribute>
                </img>
             
	             <!-- Fixed size of picture -->
	             <xsl:choose>
	                <xsl:when test="@height and @height!='' and @width and @width!=''">
	                   <img class="admHand" src="/images/pf/insert-edit-image.png" onclick="changeMainImage('{@id}', '{../../@id}', '{@width}', '{@height}');">
	                        <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_picture_frame_adds, 'ADV_PF.CHANGE_IMAGE')"/></xsl:attribute>
	                   </img>
	                </xsl:when>
	                <!-- Not fixed size picture-->                
	                <xsl:otherwise> 
	                  <img class="admHand" src="/images/pf/insert-edit-image.png">
	                        <xsl:attribute name="onclick">changeImage('<xsl:value-of select="@id"/>', '<xsl:value-of select="../@id"/>', '<xsl:value-of select="../../@id"/>', '<xsl:value-of select="@current-width"/>', '<xsl:value-of select="@height"/>', '<xsl:value-of select="../@max_width"/>', <xsl:value-of select="count(../negeso:image[@extension='swf'])"/>);</xsl:attribute>
	                        <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_picture_frame_adds, 'ADV_PF.CHANGE_IMAGE')"/></xsl:attribute>
	                  </img>
	                </xsl:otherwise>
	             </xsl:choose>                                                       
             
            <xsl:if test="not(../../negeso:image_set[@class='animation'])">
             
	             <!-- Fixed size of picture -->
	             <xsl:choose>
	                <xsl:when test="@height and @height!='' and @width and @width!=''">
	                   <img class="admHand" src="/images/pf/insert-edit-flash.gif" onclick="changeMainFlash('{@id}', '{../../@id}', '{@width}', '{@height}');">
	                        <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_picture_frame_adds, 'ADV_PF.CHANGE_FLASH')"/></xsl:attribute>
	                   </img>
	                </xsl:when>
	                <!-- Not fixed size picture-->                
	                <xsl:otherwise>
	                  <img class="admHand" src="/images/pf/insert-edit-flash.png">
	                        <xsl:attribute name="onclick">changeFlash('<xsl:value-of select="@id"/>', '<xsl:value-of select="../@id"/>', '<xsl:value-of select="../../@id"/>', '<xsl:value-of select="@width"/>', '<xsl:value-of select="@height"/>', '<xsl:value-of select="../@max_width"/>', <xsl:value-of select="count(../negeso:image[@extension='swf'])"/>, '<xsl:value-of select="@current-width"/>');</xsl:attribute>
	                        <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_picture_frame_adds, 'ADV_PF.CHANGE_FLASH')"/></xsl:attribute>
	                  </img>
	                </xsl:otherwise>
	             </xsl:choose>                                                       
           
             </xsl:if>
			
				<img class="admHand" src="/images/pf/insert-edit-link.png" onclick="setLink('{@id}');">
					<xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'EDIT_LINK')"/></xsl:attribute>
				</img>
			
                 <img class="admHand" src="/images/delete.png" onclick="deleteImage('{@id}', '{../../@id}','{$show}');">
                   <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_common, 'DELETE')"/></xsl:attribute>
                 </img>
             </td>
         </tr>
     </xsl:otherwise>
     </xsl:choose>
</xsl:template>

</xsl:stylesheet>