<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (c) 2011 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.


  XSL common functions for Site Flip book module
 
  @version		2011.12.26
  @author		Mykola Lyhozhon
-->

<xsl:stylesheet version="1.0" 
				xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:negeso="http://negeso.com/2003/Framework"
				xmlns:java="http://xml.apache.org/xslt/java"
        		exclude-result-prefixes="java"
				>

<xsl:template name="flip-book">
<xsl:choose>
	<xsl:when test="not(//negeso:flip-book-component/@pages-count)">File not found</xsl:when>
	<xsl:otherwise>
	
	<link type="text/css" href="/site/modules/flip_book/css/style.css" rel="stylesheet"/>
	<link type="text/css" rel="stylesheet" href="http://fonts.googleapis.com/css?family=Play:400,700"/>
	<script src="/site/modules/flip_book/script/turn.js"></script>
	
	<script src="/site/modules/flip_book/script/jquery.fullscreen.js"></script>
    <script src="/site/modules/flip_book/script/jquery.address-1.6.min.js"></script>
	<script src="/site/modules/flip_book/script/onload.js"></script>
	
	<xsl:variable name="dict_flip_book_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('flip_book', $lang)"/>
	
	
		<!-- BEGIN FLIPBOOK STRUCTURE -->
		<div id="fb5-ajax">	         
	         
	      <!-- BEGIN HTML BOOK -->      
	      <div data-current="pages" class="fb5" id="fb5">      
	            
	            <!-- PRELOADER -->
	            <div class="fb5-preloader">
	            <div id="wBall_1" class="wBall">
	            <div class="wInnerBall">
	            </div>
	            </div>
	            <div id="wBall_2" class="wBall">
	            <div class="wInnerBall">
	            </div>
	            </div>
	            <div id="wBall_3" class="wBall">
	            <div class="wInnerBall">
	            </div>
	            </div>
	            <div id="wBall_4" class="wBall">
	            <div class="wInnerBall">
	            </div>
	            </div>
	            <div id="wBall_5" class="wBall">
	            <div class="wInnerBall">
	            </div>
	            </div>
	            </div>      
	      
	            <!-- BACK BUTTON
	            <a href="#" id="fb5-button-back">BACK</a> -->
	                  
	            <!-- BACKGROUND FOR BOOK -->  
	            <div class="bg-custom"></div>                      
	          
	            <!-- BEGIN CONTAINER BOOK -->
	            <div id="fb5-container-book">
	     
	                <!-- BEGIN deep linking -->  
	                <section id="fb5-deeplinking">
	                     <ul>
	                     <xsl:for-each select="//negeso:flip-book-component/negeso:page">
	                          <li data-address="page{@number}" data-page="{@number}"></li>	                     	
	                     </xsl:for-each>
	                     </ul>
	                 </section>
	                <!-- END deep linking --> 
	                
	                <!-- BEGIN BOOK -->
	                <div id="fb5-book">
	                
	                <xsl:for-each select="//negeso:flip-book-component/negeso:page">
		                 <!-- BEGIN PAGE -->          
		                <div style="background-image:url(/flipbook?filePath={//negeso:flip-book-component/@filePath}&amp;p={@number})" class="">
		                       
		                     <!-- container page book --> 
		                     <div class="fb5-cont-page-book">
		                       
		                         <!-- description for page --> 
		                         <div class="fb5-page-book">
		                                         
		                         </div> 
		                                  
		                        <!-- number page and title  -->                
		                        <div class="fb5-meta">
		                                <span class="fb5-description"></span>
		                                <span class="fb5-num"></span>
		                         </div> 
		                        
		                        
		                     </div> <!-- end container page book --> 
		                        
		                </div>
		                <!-- END PAGE -->      
	                </xsl:for-each>
	               
	                        
	                  
	              </div>
	              <!-- END BOOK -->
	                           
	                
	              <!-- arrows -->
	              <a class="fb5-nav-arrow prev"></a>
	              <a class="fb5-nav-arrow next"></a>
	                
	                
	             </div>
	             <!-- END CONTAINER BOOK -->    
	    
	            <!-- BEGIN FOOTER -->
	            <div id="fb5-footer">
	        
	            <div class="fb5-bcg-tools"></div>

	            <a id="fb5-logo" target="_blank" >
	               
	            </a>

	            
	            <div class="fb5-menu" id="fb5-center">
	                <ul>
	                
	                    <!-- icon download -->
	                    <li>
	                        <a title="{java:getString($dict_flip_book_module, 'FLIP_BOOK.V2.DOWNLOAD')}" class="fb5-download" href="/media/flip_book/{//negeso:flip-book-component/@filePath}"></a>
	                    </li>
	                                        
	                    
	                    <!-- icon_zoom_in -->                              
	                    <li>
	                        <a title="{java:getString($dict_flip_book_module, 'FLIP_BOOK.V2.ZOOM_IN')}" class="fb5-zoom-in"></a>
	                    </li>                               
	                    
	                    <!-- icon_zoom_out -->
	                     
	                    <li>
	                        <a title="{java:getString($dict_flip_book_module, 'FLIP_BOOK.V2.ZOOM_OUT')}" class="fb5-zoom-out"></a>
	                    </li>                                
	                    
	                    <!-- icon_zoom_auto -->
	                    <li>
	                        <a title="{java:getString($dict_flip_book_module, 'FLIP_BOOK.V2.ZOOM_AUTO')}" class="fb5-zoom-auto"></a>
	                    </li>                                
	                    
	                    <!-- icon_zoom_original -->
	                    <li>
	                        <a title="{java:getString($dict_flip_book_module, 'FLIP_BOOK.V2.ZOOM_ORIGINAL')}" class="fb5-zoom-original"></a>
	                    </li>
	                                     
	                    
	                    <!-- icon_allpages -->
	                    <li>
	                        <a title="{java:getString($dict_flip_book_module, 'FLIP_BOOK.V2.SHOW_ALL_PAGES')}" class="fb5-show-all"></a>
	                    </li>
	                                                    
	                    
	                    <!-- icon_home -->
	                    <li>
	                        <a title="{java:getString($dict_flip_book_module, 'FLIP_BOOK.V2.SHOW_HOME_PAGE')}" class="fb5-home"></a>
	                    </li>
	                                    
	                </ul>
	            </div>
	            
	            <div class="fb5-menu" id="fb5-right">
	                <ul> 
	                    <!-- icon page manager -->                 
	                    <li class="fb5-goto">
	                        <label for="fb5-page-number" id="fb5-label-page-number"><xsl:value-of select="java:getString($dict_flip_book_module, 'FLIP_BOOK.V2.PAGE')"/></label>
	                        <input type="text" id="fb5-page-number" style="margin-top: 4px;"/>
	                        <button type="button" style="color: white;"><xsl:value-of select="java:getString($dict_flip_book_module, 'FLIP_BOOK.V2.GO')"/></button>
	                    </li>    
	                     
	                    <!-- icon contact form -->  
	                    <!-- 
	                    <li>
	                        <a title="SEND MESSAGE" class="contact"></a>
	                    </li>                           
	                     -->               
	                                    
	                    <!-- icon fullscreen -->                 
	                    <li>
	                        <a title="{java:getString($dict_flip_book_module, 'FLIP_BOOK.V2.FULL_SCREEN')}" class="fb5-fullscreen"></a>
	                    </li>                                       
	                                    
	                </ul>
	            </div>
	            
	            
	        
	        </div>
	            <!-- END FOOTER -->   	    	         
	    
	            <!-- BEGIN ALL PAGES -->
	            <div id="fb5-all-pages" class="fb5-overlay">
	    
	          <section class="fb5-container-pages">
	    
	            <div id="fb5-menu-holder">
	    
	                <ul id="fb5-slider">
	                <xsl:for-each select="//negeso:flip-book-component/negeso:page">
                         <li class="{@number}">
                           <img alt="" src="/flipbook?filePath={//negeso:flip-book-component/@filePath}&amp;p={@number}&amp;thumb=true"/>
                      <!-- <img alt="" src="/flipbook?filePath={//negeso:flip-book-component/@filePath}&amp;p={@number}&amp;thumb=true"/>  to use thumbnails -->
                         </li>
	                </xsl:for-each>	    
	                  </ul>
	            
	              </div>
	    
	          </section>
	    	
	         </div>
	            <!-- END ALL PAGES -->
	
	      </div>
	      <!-- END HTML BOOK -->     
	
	</div>
	<!-- END FLIPBOOK STRUCTURE -->    
	
	    <!-- CONFIGURATION FLIPBOOK -->    
	    <script>    
	    jQuery('#fb5').data('config',
	    {
	    "page_width":"<xsl:value-of select="//negeso:flip-book-component/@width"/>",
	    "page_height":"<xsl:value-of select="//negeso:flip-book-component/@height"/>",
	    "zoom_double_click":"1",
	    "zoom_step":"0.06",
	    "double_click_enabled":"true",
	    "tooltip_visible":"true",
	    "toolbar_visible":"true",
	    "gotopage_width":"30",
	    "deeplinking_enabled":"true",
	    "rtl":"false",
	    <xsl:choose>
	    	<xsl:when test="//negeso:page/@category='popup'">
	    		'full_area': 'true'
	    	</xsl:when>
	    	<xsl:otherwise>
	    		'full_area': 'false',
	    		'accurateHeight': 700
	    		// 'minus_header_footer_area': "true",
	    		// 'header_plus_footer_size': '300'
	    	</xsl:otherwise>
	    </xsl:choose>
	    })    
	    </script>
	</xsl:otherwise>
</xsl:choose>
</xsl:template>
</xsl:stylesheet>