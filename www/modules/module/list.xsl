<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${list.xsl}       
 
  Copyright (c) 2003 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  View of a list item edit interface.
 
  @version      2004.01.05
  @author       Olexiy.Strashko
  @author       Sergiy Oliynyk
  @author       Volodymyr Snigur
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_modules" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_modules.xsl', $lang)"/>

<xsl:template match="/">
    <!--<xsl:call-template name="default_modules"/>-->
    <xsl:apply-templates select="negeso:modules"/>
</xsl:template>

<xsl:template match="/negeso:modules">
    <ul>
        <xsl:call-template name="default_modules"/>
        <xsl:apply-templates select="negeso:module[@active = 'true' and
            not(
                @name='event_calendar_module' or @name='mail_to_a_friend' or 
                @name='mail_to_a_friend' or @name='sitemap' or @name='marquee' or @name='custom_consts'
            )]">
            <xsl:sort order="ascending" select="@order_number" data-type="number"/>                        
       </xsl:apply-templates>
       <xsl:apply-templates select="negeso:centralModule">
            <xsl:sort order="ascending" select="@orderNumber" data-type="number"/>
        </xsl:apply-templates>		    
    </ul>
</xsl:template>

<xsl:template match="negeso:centralModule">
    <xsl:variable name="isSub" select="count(negeso:centralModuleItem)&gt;0" />
    <xsl:variable name="curpanel" select="position() + count(negeso:module)+ 3"/>
    <li id="panel_{$curpanel}" curpanel="{$curpanel}">
        <xsl:if test="$isSub='true'">
            <xsl:attribute name="class">sub</xsl:attribute>
        </xsl:if>
        <a onfocus="blur()" title="{@title}">
            <xsl:if test="not(@url='')">
                <xsl:attribute name="href"><xsl:value-of select="@url" disable-output-escaping="yes"/></xsl:attribute>
                <xsl:attribute name="onclick">return widget.openUrl('<xsl:value-of select="@url" disable-output-escaping="yes"/>',null,null)</xsl:attribute>
            </xsl:if>
            <img src="/images/modules/negeso_module.gif" width="25" height="25" alt="">
                <xsl:if test="not(@image='')">
                    <xsl:attribute name="src"><xsl:value-of select="@image" disable-output-escaping="yes"/></xsl:attribute>
                </xsl:if>
            </img>
            <span>                
                <xsl:value-of select="@title" disable-output-escaping="yes"/>
                <!-- span class="shade"><xsl:value-of select="java:getString($dict_modules, @dict_key)" disable-output-escaping="yes"/></span-->
            </span>
        </a>
        <xsl:if test="count(negeso:centralModuleItem)&gt;0">
            <div>
                <ul>
                    <xsl:apply-templates select="negeso:centralModuleItem">
			            <xsl:sort order="ascending" select="@orderNumber" data-type="number"/>
			        </xsl:apply-templates>          
                </ul>
            </div>
        </xsl:if>
    </li>
</xsl:template>

<xsl:template match="negeso:centralModuleItem">
    <li>
        <a onfocus="blur()" title="{@title}">
            <xsl:if test="not(@url='')">
                <xsl:attribute name="href"><xsl:value-of select="@url" disable-output-escaping="yes"/></xsl:attribute>
                <xsl:attribute name="onclick">return widget.openUrl('<xsl:value-of select="@url" disable-output-escaping="yes"/>',null,null)</xsl:attribute>
            </xsl:if>
            <xsl:value-of select="@title" disable-output-escaping="yes"/>
        </a>
    </li>        
</xsl:template>   

<xsl:template name="default_modules">
    <!--<div id="edit_constants" style="display: block" onclick="Constants.showList();">
        Localization
    </div>-->
    <li id="panel_1" curpanel="1">
        <a onfocus="blur()" title="{java:getString($dict_modules, 'PAGE_PROPERTIES')}" onclick="editProperties();" id="page_properties">            
            <img src="/images/modules/page_properties.gif" width="25" height="25" alt=""/>
            <span>
                <xsl:value-of select="java:getString($dict_modules, 'PAGE_PROPERTIES')" disable-output-escaping="yes"/>
                <!-- span class="shade"><xsl:value-of select="java:getString($dict_modules, 'PAGE_PROPERTIES')" disable-output-escaping="yes"/></span -->
            </span>
        </a>
    </li>
    <li id="panel_2" curpanel="2">        
        <a onfocus="blur()" title="{java:getString($dict_modules, 'LOCALIZATION')}" id="edit_constants" onclick="Constants.showList();">
            <img src="/images/modules/localization.gif" width="25" height="25" alt=""/>
            <span>
                <xsl:value-of select="java:getString($dict_modules, 'LOCALIZATION')" disable-output-escaping="yes"/>
                <!-- span class="shade"><xsl:value-of select="java:getString($dict_modules, 'LOCALIZATION')" disable-output-escaping="yes"/></span-->
            </span>
        </a>        
    </li>

  <!--  <li id="panel_3" curpanel="3">        
        <a onfocus="blur()" title="{java:getString($dict_modules, 'MENU_STRUCTURE')}" href="return window.open('menu_editor','menuEditor','height=600,width=590,toolbar=yes,status=no,resizable=yes,scrollbars=yes');" id="menu_structure">
            <xsl:attribute name="onclick">return widget.openUrl('menu_editor')</xsl:attribute>
            <img src="/images/modules/menu_structure.gif" width="51" height="51" alt=""/>
            <span>
                <xsl:value-of select="java:getString($dict_modules, 'MENU_STRUCTURE')" disable-output-escaping="yes"/>
                <span class="shade"><xsl:value-of select="java:getString($dict_modules, 'MENU_STRUCTURE')" disable-output-escaping="yes"/></span>
            </span>
        </a>
    </li> -->


</xsl:template>    
   
<xsl:template match="negeso:module">
    <xsl:variable name="isSub" select="count(negeso:moduleItem[@hide_from_user='false'])&gt;0 or @parametersCount != 0 or @constsCount != 0"/>
    <xsl:variable name="curpanel" select="position()+3"/>    
    <li id="panel_{$curpanel}" curpanel="{$curpanel}">
        <xsl:if test="$isSub='true'">
            <xsl:attribute name="class">sub</xsl:attribute>
        </xsl:if>
        <a onfocus="blur()" title="{java:getString($dict_modules, @dict_key)}">
            <xsl:if test="not(@url='')">
                <xsl:attribute name="href"><xsl:value-of select="@url" disable-output-escaping="yes"/></xsl:attribute>
                <xsl:attribute name="onclick">return widget.openUrl('<xsl:value-of select="@url" disable-output-escaping="yes"/>',null,null)</xsl:attribute>
            </xsl:if>
            <img src="/images/modules/negeso_module.gif" width="25" height="25" alt="">
                <xsl:if test="not(image='')">
                    <xsl:attribute name="src"><xsl:value-of select="@image" disable-output-escaping="yes"/></xsl:attribute>
                </xsl:if>
            </img>
            <span>                
                <xsl:value-of select="java:getString($dict_modules, @dict_key)" disable-output-escaping="yes"/>
                <!-- span class="shade"><xsl:value-of select="java:getString($dict_modules, @dict_key)" disable-output-escaping="yes"/></span-->                
            </span>
        </a>
        <xsl:if test="$isSub">
            <div>
                <xsl:if test="not(count(negeso:moduleItem[@hide_from_user='false'])&gt;0)">
                    <xsl:attribute name="class">singlSetting</xsl:attribute>
                </xsl:if>
                <ul>
                    <xsl:if test="count(negeso:moduleItem[@hide_from_user='false'])&gt;0">
                        <xsl:apply-templates select="negeso:moduleItem[@hide_from_user='false']"/>
                    </xsl:if>
                    <xsl:if test="@active != 'false' and @constsCount != 0">
                    	<xsl:call-template name="consts">
                        	<xsl:with-param name="id" select="@id"/>
                        </xsl:call-template>                                                
                    </xsl:if>
                    
                     <xsl:if test="@active != 'false' and @parametersCount != 0"> 
                         <xsl:call-template name="parameters">
                        <xsl:with-param name="id" select="@id"/>
                        </xsl:call-template>                         
                     </xsl:if>
                </ul>
            </div>
        </xsl:if>
    </li>
</xsl:template>

<xsl:template match="negeso:moduleItem">
    <li>
        <a onfocus="blur()" title="{java:getString($dict_modules, @dict_key)}">
            <xsl:if test="not(@url='')">
                <xsl:attribute name="href"><xsl:value-of select="@url" disable-output-escaping="yes"/></xsl:attribute>
                <xsl:attribute name="onclick">return widget.openUrl('<xsl:value-of select="@url" disable-output-escaping="yes"/>',null,null)</xsl:attribute>
            </xsl:if>
            <xsl:value-of select="java:getString($dict_modules, @dict_key)" disable-output-escaping="yes"/>
        </a>
    </li>        
</xsl:template>   

<xsl:template name="consts">
	<xsl:param name="id"/>
  		<li class="setting1">
        <a href="" onfocus="blur()" title="Consts">
	       <xsl:attribute name="href">/admin/module_consts?moduleId=<xsl:value-of select="$id"/>&amp;visitorMode=true</xsl:attribute>
		   <xsl:attribute name="onclick">return widget.openUrl('/admin/module_consts?moduleId=<xsl:value-of select="$id"/>&amp;visitorMode=true',null,null)</xsl:attribute>
           <img class="setting1" src='/images/modules/consts.png' /> 
       </a>
	</li>
</xsl:template>

<xsl:template name="parameters">
	<xsl:param name="id"/>
<li class="setting2">
       <a href="" onfocus="blur()" title="Parameters">
        <xsl:attribute name="href">/admin/visitor_parameters?moduleId=<xsl:value-of select="@id"/></xsl:attribute>
		<xsl:attribute name="onclick">return widget.openUrl('/admin/visitor_parameters?moduleId=<xsl:value-of select="@id"/>',null,null)</xsl:attribute>
        <img class="setting2" src='/images/modules/parameters.png'  />
      </a> </li>
</xsl:template>




</xsl:stylesheet>
