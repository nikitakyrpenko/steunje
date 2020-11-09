<?xml version="1.0" encoding="UTF-8"?>
<!--
  Copyright (c) 2007 Negeso Ukraine

  This software is the confidential and proprietary information of Negeso
  ("Confidential Information"). You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.


  XSL templates for Site Core
 
  @version		2007.11.27
  @author		Oleg 'germes' Shvedov
  @author		Rostislav 'KOTT' Brizgunov
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">
    <xsl:variable name="interface-lang" select="/*/@interface-language"/>
    <xsl:variable name="dict_modules" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_modules.xsl', $interface-lang)"/>
    
    <xsl:template name="adminhead">
        <!-- current tag - negeso:page or ROOT (when called from admin part) -->                
        <script type="text/javascript" src="/script/RTE_Adapter.js">/**/</script>
        <script type="text/javascript" src="/script/AJAX_webservice.js">/**/</script>
        <script type="text/javascript">
            function edit_form(inp_id, default_style, div_width , text_dir){
                var art_id = inp_id.substr(12, inp_id.length-1);
                RTE_Init(inp_id, inp_id, art_id, 3, 1, default_style, getInterfaceLanguage());
            }

            function edit_text(inp_id, default_style, div_width , text_dir){
                var art_id = inp_id.substr(12, inp_id.length-1);
                RTE_Init(inp_id, inp_id, art_id, 3, 0, default_style, getInterfaceLanguage());
            }
        </script>
    </xsl:template>

    <xsl:template name="pageproperties">
        <div style="display: none" id="div_curPageId">
            <xsl:value-of select="/negeso:page/@id" disable-output-escaping="yes" />
        </div>
        <div style="display: none" id="div_curPageTitle">
            <xsl:value-of select="/negeso:page/negeso:title/text()" disable-output-escaping="yes" />
        </div>
        <div style="display: none" id="div_curMetaTitle">
            <xsl:value-of select="/negeso:page/negeso:meta_title/text()" disable-output-escaping="yes" />
        </div>
        <div style="display: none" id="div_curLink">
            <xsl:value-of select="/negeso:page/negeso:filename/text()" disable-output-escaping="yes" />
        </div>
        <div style="display: none" id="div_pageProtected">
            <xsl:value-of select="/negeso:page/@protected" disable-output-escaping="yes" />
        </div>
        <div style="display: none" id="div_metatags">
            <xsl:value-of select="//@metatags" disable-output-escaping="yes" />
        </div>
        <div style="display: none" id="div_google_script_enabled">
            <xsl:value-of select="//@google_script_enabled" disable-output-escaping="yes" />
        </div>
        <div style="display: none" id="div_curPageMetaDescr">
            <xsl:value-of select="/negeso:page/negeso:meta[@name='description']/text()" disable-output-escaping="yes" />
        </div>
        <div style="display: none" id="div_curPageMetaKeywords">
            <xsl:value-of select="/negeso:page/negeso:meta[@name='keywords']/text()" disable-output-escaping="yes" />
        </div>
        <div style="display: none" id="div_curPagePropertyName">
            <xsl:value-of select="/negeso:page/negeso:meta[not(@name='description' or @name='keywords' or @name='google_script')]/@name" disable-output-escaping="yes" />
        </div>
        <div style="display: none" id="div_curPagePropertyValue">
            <xsl:value-of select="/negeso:page/negeso:meta[not(@name='description' or @name='keywords' or @name='google_script')]/text()" disable-output-escaping="yes" />
        </div>
        <div style="display: none" id="div_curPageGoogleScript">
            <xsl:value-of select="/negeso:page/negeso:meta[@name='google_script']/text()" disable-output-escaping="yes" />
        </div>
        <div style="display: none" id="div_curPublish">
            <xsl:value-of select="/negeso:page/@publish_date" disable-output-escaping="yes" />
        </div>
        <div style="display: none" id="div_curExpires">
            <xsl:value-of select="/negeso:page/@expired_date" disable-output-escaping="yes" />
        </div>
        <div style="display: none" id="div_curEditDate">
            <xsl:value-of select="/negeso:page/@edit_date" disable-output-escaping="yes" />
        </div>
        <div style="display: none" id="div_curEditUser">
            <xsl:value-of select="/negeso:page/@edit_user" disable-output-escaping="yes" />
        </div>
        <div style="display: none" id="div_curPageClass">
            <xsl:value-of select="/negeso:page/@page_class" disable-output-escaping="yes" />
        </div>
        <div style="display: none" id="div_curContainer">
            <xsl:value-of select="/negeso:page/@container_id" disable-output-escaping="yes" />
        </div>
        <div style="display: none" id="div_curContainerTitle">
            <xsl:value-of select="/negeso:page/@container_title" disable-output-escaping="yes" />
        </div>
        <div style="display: none" id="div_curForceVisibility">
            <xsl:value-of select="/negeso:page/@role-id-max" disable-output-escaping="yes" />
        </div>
        <div style="display: none" id="div_curIsSearch">
            <xsl:value-of select="/negeso:page/@isSearch" disable-output-escaping="yes" />
        </div>
        <div style="display: none" id="div_curIsSpecial">
            <xsl:value-of select="/negeso:page/@isSpecial" disable-output-escaping="yes" />
        </div>
        <div style="display: none" id="div_wcms_attributes">
            <xsl:value-of select="/negeso:page/@wcms_attributes" disable-output-escaping="yes" />
        </div>

        <script type="text/javascript">
            var curPageId = document.getElementById("div_curPageId").innerHTML;
            var curPageTitle = document.getElementById("div_curPageTitle").innerHTML;
            var curMetaTitle = document.getElementById("div_curMetaTitle").innerHTML;
            var curLink = document.getElementById("div_curLink").innerHTML;
            var pageProtected = document.getElementById("div_pageProtected").innerHTML;
            var metatags = document.getElementById("div_metatags").innerHTML;
            var google_script_enabled = document.getElementById("div_google_script_enabled").innerHTML;
            var curPageMetaDescr = document.getElementById("div_curPageMetaDescr").innerHTML;
            var curPageMetaKeywords = document.getElementById("div_curPageMetaKeywords").innerHTML;
            var curPagePropertyName = document.getElementById("div_curPagePropertyName").innerHTML;
            var curPagePropertyValue = document.getElementById("div_curPagePropertyValue").innerHTML;
            var curPageGoogleScript = document.getElementById("div_curPageGoogleScript").innerHTML;
            var curPublish = document.getElementById("div_curPublish").innerHTML;
            var curExpires = document.getElementById("div_curExpires").innerHTML;
            var curEditDate = document.getElementById("div_curEditDate").innerHTML;
            var curEditUser = document.getElementById("div_curEditUser").innerHTML;
            var curPageClass = document.getElementById("div_curPageClass").innerHTML;
            var curContainer = document.getElementById("div_curContainer").innerHTML;
            var curContainerTitle = document.getElementById("div_curContainerTitle").innerHTML;
            var curForceVisibility = document.getElementById("div_curForceVisibility").innerHTML;
            var curIsSearch = document.getElementById("div_curIsSearch").innerHTML;
            var curIsSpecial = document.getElementById("div_curIsSpecial").innerHTML;
            var wcms_attributes = document.getElementById("div_wcms_attributes").innerHTML;
            var curContainerTotal = '<xsl:value-of select="/negeso:page/negeso:containers/@total"/>';
            var curArrContainer = new Array();
            <xsl:for-each select="//negeso:container">
                curArrContainer.push(new Array('<xsl:value-of select="@id"/>','<xsl:value-of select="@name"/>'));
            </xsl:for-each>
            var pictureFramePresent = <xsl:value-of select="count(/negeso:page/negeso:wcms_attributes)"/>;

            // Check attributes values:
            //alert("Current page: "+curPageId+"\n"+"Current page title: "+curPageTitle+"\n"+"Current publisher: "+curPublish+"\n"+"Current expire: "+curExpires+"\n"+"Current container: "+curContainer+"\n"+"Current container title: "+curContainerTitle+"\n"+"Current force visibility: "+curForceVisibility);

            // Temporary value holders (to be set externally from a dialog window).
            var newPageId = "";
            var newPageTitle = "";
            var newMetaTitle = "";
            var newPageMetaDescr = "";
            var newPageMetaKeywords = "";
            var newPagePropertyName = "";
            var newPagePropertyValue = "";
            var newPageGoogleScript = "";
            var newPublish = "";
            var newExpire = "";
            var newContainer = "";
            var newContainerTitle = "";
            var newForceVisibility = "";
            var newIsSearch = "";
            var new_wcms_attributes = "";
            var newContainerTotal = 0;
            var newArrContainer = new Array();
            if (curIsSpecial=='true') {
                var showCheckBoxIsSearch = true;
            }

			function editPageProperties(pageId) {
         	}

            /*
            * Passes current page values to "Edit page properties" dialog, receives
            * the desired new ones, and saves via web-services
            */
            function editProperties() { // if nothing is passed -- current page title is displayed
                newPageId = curPageId;
                newPageTitle = curPageTitle;
                newMetaTitle = curMetaTitle;
                newLink = curLink;
                newPageMetaDescr = curPageMetaDescr;
                newPageMetaKeywords = curPageMetaKeywords;
                newPagePropertyValue = curPagePropertyValue;
                newPageGoogleScript = curPageGoogleScript;
                newPublish = curPublish;
                newExpire = curExpires;
                newEditDate = curEditDate;
                newEditUser = curEditUser;
                newPageClass = curPageClass;
                newContainer = curContainer;
                newContainerTitle = curContainerTitle;
                newContainerTotal = curContainerTotal;
                newArrContainer = curArrContainer;
                newForceVisibility = curForceVisibility;
                newIsSearch = curIsSearch;
                new_wcms_attributes = wcms_attributes;

			widgetWindow = window.open('/admin/edit_page_info?act=edit&amp;id=' + curPageId ,'page_properties_'+new Date().getTime(),'height=920, width=825, menubar=no, resizable=yes,  status=no, titlebar=yes, toolbar=no, scrollbars=yes');
        
            /*widgetWindow=window.open(
            "/admin/dialog_change_page_properties.html",
            "page_properties",
            "height=920px, width=840px, status=no, resizable=yes, scrollbars=yes, toolbar=no, menubar=no");
            //"height=600px,width=890px,status=yes,resizable=yes,scrollbars=yes,toolbar=yes,menubar=yes,location=yes,directories=yes");*/
            
            if (window.focus) {widgetWindow.focus()}
            }

            function editProperties_respone() {
                AJAX_Send("update-page-info-command",
                {"id"               : curPageId,
                "filename"         : newLink,
                "title"            : newPageTitle,
                "meta_title"       : newMetaTitle,
                "meta_description" : newPageMetaDescr,
                "meta_keywords"    : newPageMetaKeywords,
                "property_value"   : newPagePropertyValue,
                "google_script"   : newPageGoogleScript,
                "publish"          : newPublish,
                "expire"           : newExpire,
                "container_id"     : newContainer,
                "force_visibility" : newForceVisibility,
                "isSearch"				: newIsSearch
                },
                editProperties_responeOK,
                null
            );
            }

            function editProperties_responeOK() {
                window.document.title = newMetaTitle;
                curPageTitle = newPageTitle;
                curMetaTitle = newMetaTitle;
                curLink = newLink;
                curPageMetaDescr = newPageMetaDescr;
                curPageMetaKeywords = newPageMetaKeywords;
                curPagePropertyValue = newPagePropertyValue;
                curPageGoogleScript = newPageGoogleScript;
                curPublish = newPublish;
                curExpires = newExpire;
                curEditDate = newEditDate;
                curEditUser = newEditUser;
                currPageClass = newPageClass;
                curContainer = newContainer;
                curContainerTitle = newContainerTitle;
                curForceVisibility = newForceVisibility;
                curIsSearch = newIsSearch;
            }
        </script>

    </xsl:template>

    <xsl:template name="negeso_widget">        
        <script type="text/javascript" src="/dictionaries/dict_negesowidget_{/*/@interface-language}.js">/**/</script>
        <div id="widget_controller" class="widget_controller widget_center" style="visibility:hidden">
            <div class="widget_block">
                <div class="widget_arrow_top" id="widgetPrev">
                    <img src="/images/spacer.gif" width="85" height="25" alt="" />
                </div>
                <div class="widget_wcms">
                    <img src="/images/negeso.png" width="85" height="30" alt="" />
                </div>
                <div class="widget_switch" id="widget_switch" onclick="widget.widgetSwitch()">
                    <span id="off" onfocus="blur()">
                        <img src="/images/hide_{$lang}.png" width="9" height="70" alt="" />
                    </span>
                    <span id="on" onfocus="blur()">
                        <img src="/images/show_{$lang}.png" width="9" height="75" alt="" />
                    </span>
                </div>
                <div class="widget_logout">
                    <a href="/admin/logout" onfocus="blur()" title="{java:getString($dict_modules, 'LOGOUT')}">
                        <xsl:value-of select="java:getString($dict_modules, 'LOGOUT')"/>                        
                    </a>
                </div>
                <div class="widget_help">
                    <a href="/admin/help/cms-help_{$interface-lang}.html" onfocus="blur()" title="{java:getString($dict_modules, 'HELP_MODULE')}" target="_blank">
                        <img src="/images/help.png" height="16" width="16" />
                    </a>
                </div>
                <div class="widget_modules" id="widget_modules">
                    <ul></ul>
                </div>
                <div class="widget_arrow_right" id="widgetNext">
                    <img src="/images/spacer.gif" width="85" height="25" alt="" />
                </div>
            </div>
            <div class="widget_block_end"></div>
        </div>
        <script language="JavaScript" type="text/javascript">
            widget.init();
        </script>

    </xsl:template>


</xsl:stylesheet>
