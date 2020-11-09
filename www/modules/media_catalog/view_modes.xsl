<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$
 
  Copyright (c) 2004 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  @version		$Revision$
  @author     	Olexiy.Strashko
  @author     	Volodymyr Snigur
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_media_catalog" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_media_catalog.xsl', $lang)"/>
<xsl:variable name="dict_security_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_security_module.xsl', $lang)"/>
<xsl:variable name="dict_rte" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_rte.js', $lang)"/>

<!-- BEGIN NEGESO RESOURCE MATCH -->
<xsl:template match="negeso:resource">
	<xsl:choose>
		<xsl:when test="/negeso:page/negeso:browser-config/@view-mode='image_list'">
			<!-- !!!!!!!! IMAGE LIST MODE !!!!!!!!!!!!! -->
			<xsl:call-template name="image-list-mode-content"/>
		</xsl:when>
        <xsl:when test="/negeso:page/negeso:browser-config/@view-mode='image_gallery'">
            <!-- !!!!!!!! IMAGE LIST gallery !!!!!!!!!!!!! -->            
            <xsl:call-template name="image-gallery-mode-content">
                <xsl:with-param name="last">
                    <xsl:if test="(position() mod 4)=0">true</xsl:if>
                </xsl:with-param>
            </xsl:call-template>            
        </xsl:when>
		<xsl:otherwise>
			<!-- !!!!!!!! FILE MANAGER MODE !!!!!!!!!!!!! -->
			<xsl:call-template name="file-manager-mode-content"/>
		</xsl:otherwise>
	</xsl:choose>	
</xsl:template>

<!-- ******************* FiLE LIST MODE **************************-->
<xsl:template name="view_modes_file-manager-mode">
    <table cellspacing="0" cellpadding="0" id="folderContentTable" width="100%">
		<tr> 
			<td class="admTDtitles">&#160;</td>
			<td class="admTDtitles">&#160;</td>
			<td class="admTDtitles">
            <xsl:value-of select="java:getString($dict_common, 'FILE_NAME')"/>
			</td>
			<td class="admTDtitles">
				<xsl:value-of select="java:getString($dict_media_catalog, 'SIZE')"/>
			</td>
			<td class="admTDtitles">
				<xsl:value-of select="java:getString($dict_common, 'TYPE')"/>
			</td>
			<xsl:if test="/negeso:page/negeso:browser-config/@action-mode!='chooser'">
				<td class="admTDtitles">
					<xsl:value-of select="java:getString($dict_security_module, 'CONTAINER')"/>
				</td>
				<td class="admTDtitles" colspan="4"><xsl:value-of select="java:getString($dict_common, 'ACTION')"/></td>
			</xsl:if>
		</tr>
		<xsl:choose>
        		<xsl:when test="@parent-dir=@current-dir">
        		</xsl:when>
        		<xsl:otherwise>
        			<!-- we are in folder -->
        			<tr>
                    <th class="admTableTDLast">
                    	<a class="admAnchor">
    							<xsl:attribute name="href">#</xsl:attribute>	
    						<xsl:attribute name="onClick">locateFolder('','<xsl:value-of select="@parent-dir"/>')</xsl:attribute>
    						<xsl:attribute name="title"><xsl:value-of select="java:getString($dict_media_catalog, 'BACK_TO_PARENT')"/></xsl:attribute>
    							<img  align="center" src="/images/media_catalog/back.png"/>                               
    						</a> &#160;&#160;&#160;&#160;
                        <a class="admAnchor">
                            <xsl:attribute name="href">#</xsl:attribute>
                            <xsl:attribute name="onClick">  locateFolder('','<xsl:value-of select="@parent-dir"/>')  </xsl:attribute>
                            <xsl:attribute name="title">
                                <xsl:value-of select="java:getString($dict_media_catalog, 'BACK_TO_PARENT')"/>
                            </xsl:attribute>                 
                           <xsl:text>&#046;&#046;&#046;</xsl:text>
                        </a>
                    </th>
						
    				<td class="admTableTDLast">&#160;</td>
    				<td class="admTableTDLast">&#160;</td>
                    <td class="admTableTDLast">&#160;</td>
                    <td class="admTableTDLast">&#160;</td>
    				<xsl:if test="/negeso:page/negeso:browser-config/@action-mode!='chooser'">
	    				<td class="admTableTDLast">&#160;</td>
	    				<td class="admTableTDLast">&#160;</td>
	    				<td class="admTableTDLast">&#160;</td>
	    				<td class="admTableTDLast">&#160;</td>
	    				<td class="admTableTDLast">&#160;</td>
					</xsl:if>
        			</tr>
        		</xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates select="negeso:resource"/>
	</table> 
</xsl:template>

<!-- ******************* File manager mode *************************** -->
<xsl:template name="file-manager-mode-content">
    <xsl:param name="mode"/>
    <tr>
    	<th class="admTableTDLast">
    		<!-- icon image -->
            <img align="center" src="/images/media_catalog/{@icon-image}"/>			
			<xsl:if test ="position()=1">
				<div class="floatMenu fileUploadQueue" id="floatMenu">
					<div class="inner">
					<a href="#" onclick="$('#folderContentTable INPUT[subType=file]').attr('checked','true');$('#folderContentTable TD.checkBoxC IMG').attr('src','/images/checkbox_checked.gif'); return false;">Select all files</a>
					<br />
					<a href="#" onclick="$('#folderContentTable INPUT[subType=file]').removeAttr('checked');$('#folderContentTable TD.checkBoxC IMG').attr('src','/images/checkbox_unchecked.gif') ;return false;">Clear selection</a>
					<br/>
					<br/>
					<a href="" onclick="if(confirm('Are you sure you want delete files?'))deleteSelectedFiles(); return false">Delete selected file(s)</a>
					</div>
				</div>
			</xsl:if>
    	</th>
        <th class="admTableTDLast noPadding checkBoxC" >
			<xsl:choose>
				<xsl:when test ="@type!='dir'">
					<input subType="file" filename="{@name}" class="fileCheckBox" type="checkbox" />
				</xsl:when>
				<xsl:otherwise>&#160;</xsl:otherwise>
			</xsl:choose>
		</th>
    	<th class="admTableTD">
    		<!-- name -->
	       <div style="max-width:350px;padding-right:5px;">
	       <xsl:choose>
		   		<xsl:when test="@type='dir'">
					<a href="#">
    				    <!--  Directory  -->
					    <xsl:attribute name="title" xml:space="default"> 
					        <xsl:value-of select="java:getString($dict_media_catalog, 'LAST_MODIFIED')"/>: <xsl:value-of select="@last-modified"/>
					    </xsl:attribute>
					    <xsl:attribute name="onClick">locateFolder('<xsl:value-of select="../@current-dir"/>','<xsl:value-of select="../@current-dir"/>/<xsl:value-of select="@name"/>')</xsl:attribute>
						<xsl:choose>
    						<xsl:when test="@isHidden='true'">
    							<xsl:attribute name="class" >
    								<xsl:text>admAnchorGray</xsl:text>
		    					</xsl:attribute>
    						</xsl:when>
    						<xsl:otherwise>
    							<xsl:attribute name="class" >
    								<xsl:text>admAnchor</xsl:text>
		    					</xsl:attribute>
    						</xsl:otherwise>
						</xsl:choose>
						<xsl:value-of select="@name"/>
		    		</a>
	            </xsl:when>
		       	<xsl:otherwise>
					<a>
    					<!--  OTHER  -->
    					<xsl:attribute name="title">
    						<xsl:value-of select="java:getString($dict_media_catalog, 'LAST_MODIFIED')"/>: <xsl:value-of select="@last-modified"/>
    					</xsl:attribute>
                		<xsl:choose>
    						<xsl:when test="/negeso:page/negeso:browser-config/@action-mode='chooser'">
    							<xsl:attribute name="href">#</xsl:attribute>
    							<xsl:attribute name="onClick">javascript:selectFile(&quot;<xsl:value-of select="/negeso:page/negeso:browser-config/@current-dir"/>/<xsl:value-of select="@escapedName"/>&quot;)</xsl:attribute>
    						</xsl:when>
    						<xsl:otherwise>
    							<xsl:attribute name="href">javascript:getResourceInfo(&quot;<xsl:value-of select="@escapedName"/>&quot;)</xsl:attribute>
    						</xsl:otherwise>
						</xsl:choose>
						<xsl:choose>
    						<xsl:when test="@isHidden='true'">
    							<xsl:attribute name="class" >
    								<xsl:text>admAnchorGray</xsl:text>
		    					</xsl:attribute>
    						</xsl:when>
    						<xsl:otherwise>
    							<xsl:attribute name="class" >
    								<xsl:text>admAnchor</xsl:text>
		    					</xsl:attribute>
    						</xsl:otherwise>
						</xsl:choose>
						<xsl:value-of select="@name"/>
		    		</a>
				</xsl:otherwise>
			</xsl:choose>
    	</div>
    	</th>
    	<td class="admTableTD" align="center"><xsl:value-of select="@size"/></td>
    	<td class="admTableTD" align="center"><xsl:value-of select="@type"/></td>
		<xsl:if test="/negeso:page/negeso:browser-config/@action-mode != 'chooser'">
			<xsl:call-template name="standard-actions"/>
		</xsl:if>
    </tr>
</xsl:template>

<!-- ******************* IMAGE LIST MODE ************************** -->
<xsl:template name="view_modes_image-list-mode">
    <table cellspacing="0" cellpadding="0" width="100%" id="folderContentTable">
		<tr> 
			<td class="admTDtitles">&#160;</td>
			<td class="admTDtitles">
				<xsl:value-of select="java:getString($dict_common, 'FILE_NAME')"/>
			</td>
			<td class="admTDtitles">
				<xsl:value-of select="java:getString($dict_media_catalog, 'SIZE')"/>
			</td>
			<td class="admTDtitles">
				<xsl:value-of select="java:getString($dict_common, 'TYPE')"/>
			</td>
			<td class="admTDtitles">
				<xsl:value-of select="java:getString($dict_security_module, 'CONTAINER')"/>
			</td>
			<xsl:if test="/negeso:page/negeso:browser-config/@action-mode!='chooser'">
				<td class="admTDtitles" colspan="4"><xsl:value-of select="java:getString($dict_common, 'ACTION')"/></td>
			</xsl:if>
		</tr>
		<xsl:choose>
    		<xsl:when test="@parent-dir=@current-dir">
    		</xsl:when>
    		<xsl:otherwise>
    			<!-- we are in folder - generate root folder -->
    			<tr>
    				<th class="admTableTDLast">
    					<a class="admAnchor">
    						<xsl:attribute name="href">#</xsl:attribute>	
    						<xsl:attribute name="onClick">locateFolder('','<xsl:value-of select="@parent-dir"/>')</xsl:attribute>
    						<xsl:attribute name="title"><xsl:value-of select="java:getString($dict_media_catalog, 'BACK_TO_PARENT')"/></xsl:attribute>
    						<img align="center" src="/images/media_catalog/back.png"/>
    					</a>
    				</th>
    				<th class="admTableTDLast">
    					<a class="admAnchor">
    					   <xsl:attribute name="title">
                         <xsl:value-of select="java:getString($dict_media_catalog, 'BACK_TO_PARENT')"/>
    						</xsl:attribute>
    						<xsl:attribute name="onClick">locateFolder('','<xsl:value-of select="@parent-dir"/>')</xsl:attribute>
    					    <xsl:text>&#046;&#046;&#046;</xsl:text>
    					</a>
    				</th>
    				<td class="admTableTDLast">&#160;</td>
    				<td class="admTableTDLast">&#160;</td>
    				<td class="admTableTDLast">&#160;</td>
    				<td class="admTableTDLast">&#160;</td>
    				<td class="admTableTDLast">&#160;</td>
    				<td class="admTableTDLast">&#160;</td>
    				<td class="admTableTDLast">&#160;</td>
    			</tr>
    		</xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates select="negeso:resource"/>
	</table>
</xsl:template>

<!-- ******************* Image gallery mode *************************** -->
<xsl:template name="image-list-mode-content">
<xsl:param name="mode"/>
<xsl:if test="@type='dir' or @type='image' or @extension='swf'">
    <tr>
		<th class="admTableTDLast">
			<!-- image -->
            <div class="mc_2divsHolderAtFileList">
                <div class="mc_imageHolderAtFileList">
                    <a class="admAnchor">                
				        <xsl:choose>
					        <xsl:when test="@type='dir'">
						        <!-- directory image -->
						        <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_media_catalog, 'BROWSE_FOLDER')"/></xsl:attribute>
						        <xsl:attribute name="href">#</xsl:attribute>	
						        <xsl:attribute name="onClick">locateFolder('<xsl:value-of select="../@current-dir"/>','<xsl:value-of select="../@current-dir"/>/<xsl:value-of select="@name"/>')</xsl:attribute>
                                <img  align="center" src="/images/media_catalog/folder_big.png"	>
                                    <xsl:attribute name="width">
                                        <xsl:value-of select="$image_preview_width"/>
                                    </xsl:attribute>
                                    <xsl:attribute name="height">
                                        <xsl:value-of select="$image_preview_height"/>
                                    </xsl:attribute>
                                </img>					
					        </xsl:when>
					        <xsl:when test="@type='image'">
						        <!-- image preview-->
						        <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_media_catalog, 'BROWSE_IMAGE')"/></xsl:attribute>
						        <xsl:choose>
							        <xsl:when test="/negeso:page/negeso:browser-config/@action-mode='chooser'">
								        <xsl:attribute name="href">#</xsl:attribute>
								        <xsl:attribute name="onClick">javascript:selectFile(&quot;<xsl:value-of select="/negeso:page/negeso:browser-config/@current-dir"/>/<xsl:value-of select="@escapedName"/>&quot;)</xsl:attribute>
							        </xsl:when>
							        <xsl:otherwise>
								        <xsl:attribute name="href">javascript:getResourceInfo(&quot;<xsl:value-of select="@escapedName"/>&quot;)</xsl:attribute>
							        </xsl:otherwise>
						        </xsl:choose>
						        <img class="admBorder" align="center">
							        <xsl:attribute name="width"><xsl:value-of select="$image_preview_width"/>
							        </xsl:attribute>
							        <xsl:attribute name="height"><xsl:value-of select="$image_preview_height"/>
							        </xsl:attribute>
							        <xsl:attribute name="src">
								        <xsl:value-of select="../@current-dir"/>/<xsl:value-of select="@name"/>
							        </xsl:attribute>
						        </img>
					        </xsl:when>
                            <xsl:when test="@extension='swf'">
                                <!-- image preview-->
                                <xsl:attribute name="title"><xsl:value-of select="java:getString($dict_media_catalog, 'BROWSE_FLASH')"/> </xsl:attribute>
                                <xsl:choose>
                                    <xsl:when test="/negeso:page/negeso:browser-config/@action-mode='chooser'">
                                        <xsl:attribute name="href">#</xsl:attribute>
                                        <xsl:attribute name="onClick"> javascript:selectFile(&quot;<xsl:value-of select="/negeso:page/negeso:browser-config/@current-dir"/>/<xsl:value-of select="@escapedName"/>&quot;)  </xsl:attribute>
                                    </xsl:when>
                                    <xsl:otherwise>   <xsl:attribute name="href">  javascript:getResourceInfo(&quot;<xsl:value-of select="@escapedName"/>&quot;)  </xsl:attribute>
                                    </xsl:otherwise>
                                </xsl:choose>
                                <embed type="application/x-shockwave-flash"
												        pluginspage="http://www.macromedia.com/go/getflashplayer"
								                        wmode="window"
								                        quality="high"
								                        menu="false"
								                        scale="showall">
                                    <xsl:attribute name="width">
                                        <xsl:value-of select="$image_preview_width"/>
                                    </xsl:attribute>
                                    <xsl:attribute name="height">
                                        <xsl:value-of select="$image_preview_height"/>
                                    </xsl:attribute>
                                    <xsl:attribute name="src">
                                        <xsl:value-of select="/negeso:page/negeso:browser-config/@current-dir"/>
                                        <xsl:text disable-output-escaping="yes"><![CDATA[/]]></xsl:text>
                                        <xsl:value-of select="@name"/>
                                    </xsl:attribute>
                                </embed>
                            </xsl:when>
				        </xsl:choose>
			        </a>
                </div>
            
                <!--xsl:if test ="@type!='dir'">
                 <div class="mc_checkboxHolderAtFileList">
       <input subType="file" filename="{@name}" class="fileCheckBox" type="checkbox" />
</div>
 </xsl:if-->
            </div>
		</th>
        
		<th  class="admTableTD" style="padding-left:0;">
			<!-- name -->
			<table cellpadding="0" cellspacing="0" border="0">
				<xsl:choose>
					<xsl:when test="@isHidden">
						<xsl:attribute name="class">
					    	<xsl:text>admGray</xsl:text>
					    		</xsl:attribute>
					    	</xsl:when>
					    	<xsl:otherwise>
					    		<xsl:attribute name="class">
					    			<xsl:text></xsl:text>
					    		</xsl:attribute>
					    	</xsl:otherwise>
				    	</xsl:choose>
					<tr>
				
				    <td class="admBold">
				    	<xsl:value-of select="java:getString($dict_common, 'NAME')"/>:&#160;&#160;
				    </td>
				    <td>
					    <xsl:choose>
					    	<xsl:when test="@isHidden">
					    		<xsl:attribute name="class">
					    			<xsl:text>admGray</xsl:text>
					    		</xsl:attribute>
					    	</xsl:when>
					    	<xsl:otherwise>
					    		<xsl:attribute name="class">
					    			<xsl:text></xsl:text>
					    		</xsl:attribute>
					    	</xsl:otherwise>
				    	</xsl:choose>
				    	<div style="max-width:350px;padding-right:5px;">
				    	    <xsl:value-of select="@name"/>
				    </div>
				    </td>
				</tr>
				<xsl:choose>
					<xsl:when test="@type='image'">
						<tr>
						    <td class="admBold"><xsl:value-of select="java:getString($dict_media_catalog, 'HEIGHT')"/>:&#160;&#160;</td>
						    <td><xsl:value-of select="@height"/></td>
						</tr>
						<tr>
						    <td class="admBold"><xsl:value-of select="java:getString($dict_media_catalog, 'WIDTH')"/>:&#160;&#160;</td>
						    <td><xsl:value-of select="@width"/></td>
					   </tr>
					</xsl:when>
				</xsl:choose>
				<tr>
				    <td class="admBold"><xsl:value-of select="java:getString($dict_media_catalog, 'LAST_MODIFIED')"/>:&#160;&#160;</td>
				    <td><xsl:value-of select="@last-modified"/></td>
			   </tr>                
			</table>
		</th>
		<td class="admTableTD" align="center"><xsl:value-of select="@size"/></td>
		<td class="admTableTD" align="center"><xsl:value-of select="@type"/></td>
		
		<xsl:if test="/negeso:page/negeso:browser-config/@action-mode!='chooser'">
			<xsl:call-template name="standard-actions"/>
		</xsl:if>
    </tr>
</xsl:if>
</xsl:template>

<!-- ******************* IMAGE GALLERY MODE **************************-->
<xsl:template name="view_modes_image-gallery-mode">
    <div class="admNavPanelDiv" id="folderContentTable">
		<xsl:if test ="position()=1">
				<div class="floatMenu fileUploadQueue" id="floatMenu">
					<div class="inner">
					<a href="#" onclick="$('#folderContentTable INPUT[subType=file]').attr('checked','true');$('#folderContentTable TD.checkBoxC IMG').attr('src','/images/checkbox_checked.gif'); return false;">Select all files</a>
					<br />
					<a href="#" onclick="$('#folderContentTable INPUT[subType=file]').removeAttr('checked');$('#folderContentTable TD.checkBoxC IMG').attr('src','/images/checkbox_unchecked.gif') ;return false;">Clear selection</a>
					<br/>
					<br/>
					<a href="" onclick="if(confirm('Are you sure you want delete files?'))deleteSelectedFiles(); return false">Delete selected file(s)</a>
					</div>
				</div>
			</xsl:if>
		
    	<xsl:choose>
    		<xsl:when test="@parent-dir=@current-dir">
    		</xsl:when>
    		<xsl:otherwise>
    			<!-- we are in folder - generate root folder -->
    			<div class="row">
    				<div class="admTableTDLast" style="padding-left:20px;">
    						<a class="admAnchor">
    							<xsl:attribute name="href">#</xsl:attribute>	
    						<xsl:attribute name="onClick">locateFolder('','<xsl:value-of select="@parent-dir"/>')</xsl:attribute>
    						<xsl:attribute name="title"><xsl:value-of select="java:getString($dict_media_catalog, 'BACK_TO_PARENT')"/></xsl:attribute>
    							<img  align="center" src="/images/media_catalog/back.png"/>                               
    						</a> &#160;&#160;&#160;&#160;
                        <a class="admAnchor">
                            <xsl:attribute name="href">#</xsl:attribute>
                            <xsl:attribute name="onClick">  locateFolder('','<xsl:value-of select="@parent-dir"/>')  </xsl:attribute>
                            <xsl:attribute name="title">
                                <xsl:value-of select="java:getString($dict_media_catalog, 'BACK_TO_PARENT')"/>
                            </xsl:attribute>                 
                           <xsl:text>&#046;&#046;&#046;</xsl:text>
                        </a>
    				</div>    				
    			</div>
            </xsl:otherwise>
        </xsl:choose>
		<xsl:apply-templates select="negeso:resource"/>
	</div>
</xsl:template>

<xsl:template name="image-gallery-mode-content">
    <xsl:param name="mode"/>
    <xsl:param name="last" select="'false'"/>
    
    <xsl:if test="@type='dir' or @type='image' or @extension='swf'">
        <div class="admTableTD admDiv">
            <!-- image -->
            <xsl:if test="$last='true'">
                <xsl:attribute name="class">admTableTDLast admDivLast</xsl:attribute>
            </xsl:if>
            <a class="admAnchor">
                <xsl:choose>
                    <xsl:when test="@type='dir'">
                        <!-- directory image -->
                        <xsl:attribute name="title">
                            <xsl:value-of select="java:getString($dict_media_catalog, 'BROWSE_FOLDER')"/>
                        </xsl:attribute>
                        <xsl:attribute name="href">#</xsl:attribute>
                        <xsl:attribute name="onClick">locateFolder('<xsl:value-of select="../@current-dir"/>','<xsl:value-of select="../@current-dir"/>/<xsl:value-of select="@name"/>')</xsl:attribute>

                        <img  align="center" src="/images/media_catalog/folder_big.png"	>
                            <xsl:attribute name="width">
                                <xsl:value-of select="$image_preview_width"/>
                            </xsl:attribute>
                            <xsl:attribute name="height">
                                <xsl:value-of select="$image_preview_height"/>
                            </xsl:attribute>
                        </img>
                    </xsl:when>
                    <xsl:when test="@type='image'">
                        <!-- image preview-->
                        <xsl:attribute name="title">
                            <xsl:value-of select="java:getString($dict_media_catalog, 'BROWSE_IMAGE')"/>
                        </xsl:attribute>
                        <xsl:choose>
                            <xsl:when test="/negeso:page/negeso:browser-config/@action-mode='chooser'">
                                <xsl:attribute name="href">#</xsl:attribute>
                                <xsl:attribute name="onClick">javascript:selectFile(&quot;<xsl:value-of select="/negeso:page/negeso:browser-config/@current-dir"/>/<xsl:value-of select="@escapedName"/>&quot;)</xsl:attribute>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:attribute name="href">javascript:getResourceInfo(&quot;<xsl:value-of select="@escapedName"/>&quot;)</xsl:attribute>
                            </xsl:otherwise>
                        </xsl:choose>
                        <img class="admBorder" align="center">
                            <xsl:attribute name="width">
                                <xsl:value-of select="$image_preview_width"/>
                            </xsl:attribute>
                            <xsl:attribute name="height">
                                <xsl:value-of select="$image_preview_height"/>
                            </xsl:attribute>
                            <xsl:attribute name="src">
                                <xsl:value-of select="../@current-dir"/>/<xsl:value-of select="@name"/>
                            </xsl:attribute>
                        </img>
                    </xsl:when>
                    <xsl:when test="@extension='swf'">
                        <!-- flash preview-->
                        <xsl:attribute name="title">
                            <xsl:value-of select="java:getString($dict_rte, 'BROWSE_FLASH')"/>
                        </xsl:attribute>
                        <xsl:choose>
                            <xsl:when test="/negeso:page/negeso:browser-config/@action-mode='chooser'">
                                <xsl:attribute name="href">#</xsl:attribute>
                                <xsl:attribute name="onClick">javascript:selectFile(&quot;<xsl:value-of select="/negeso:page/negeso:browser-config/@current-dir"/>/<xsl:value-of select="@escapedName"/>&quot;)</xsl:attribute>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:attribute name="href">javascript:getResourceInfo(&quot;<xsl:value-of select="@escapedName"/>&quot;)</xsl:attribute>
                            </xsl:otherwise>
                        </xsl:choose>
                        <span align="center">
                            <embed type="application/x-shockwave-flash"
                                pluginspage="http://www.macromedia.com/go/getflashplayer"
                                wmode="window"
                                quality="high"
                                menu="false"
                                scale="showall">
                                <xsl:attribute name="width">
                                    <xsl:value-of select="$image_preview_width"/>
                                </xsl:attribute>
                                <xsl:attribute name="height">
                                    <xsl:value-of select="$image_preview_height"/>
                                </xsl:attribute>
                                <xsl:attribute name="src">
                                    <xsl:value-of select="/negeso:page/negeso:browser-config/@current-dir"/>
                                    <xsl:text disable-output-escaping="yes"><![CDATA[/]]></xsl:text>
                                    <xsl:value-of select="@name"/>
                                </xsl:attribute>
                            </embed>
                        </span>
                    </xsl:when>
                </xsl:choose>                
            </a>
            <br/>
            <xsl:if test ="@type!='dir'">
                <input subType="file" filename="{@name}" class="fileCheckBox" type="checkbox" />&#160;                
            </xsl:if>
            <xsl:value-of select="@name"/>
        </div>
    </xsl:if>
</xsl:template>

</xsl:stylesheet>


