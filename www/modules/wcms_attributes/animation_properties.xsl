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
<!-- NEGESO JAVASCRIPT Temaplate -->
<xsl:template name="java-script">
	<script language="JavaScript">
		<xsl:text disable-output-escaping="yes">
        <![CDATA[		

		var LocalNumericErrorMessage = {
			"en" : "Field has wrong numeric format",
			"nl" : "Een veld heeft een verkeerd numeriek formaat"
		}

		
		function checkUploadForm() {
			for (i=0; i<main_form.length; i++){
				if ( (main_form[i].getAttribute("numeric_field_params")!=null) && (new RegExp(/([^0-9])|(-)/g).test(main_form[i].value)) 
					&& !main_form[i].nextSibling
					){
					try {
						var el = document.createElement("div");			
						var tid = "not_valid" + new Date().getTime();			
						try{
							el.setAttribute("id", tid);
						}catch(e){
							try {el.id = tid;} catch(e) {}
						}
						msg = LocalNumericErrorMessage["en"];
						el.innerHTML = "<table cellspacing=\"0\" cellpadding=\"0\" style=\"height: 12px; margin: 0; padding: 0; border: 0;\"><tr style=\"height: 12px;\"><td style=\"font-size: 10px; color: red; border: 0; padding: 0; text-align: left; vertical-align: middle; height: 12px;\">" + msg + "</td></tr></table>";
						main_form[i].parentNode.appendChild(el);
					} catch(e) {}
					return false;
				}else if (
					((main_form[i].getAttribute("numeric_field_params")!=null) && !(new RegExp(/[^0-9]/g).test(main_form[i].value)))
					&& main_form[i].nextSibling
				 ){	
					main_form[i].parentNode.removeChild(main_form[i].nextSibling);
				}
			
			}
			main_form.action.value="update";            
            return true;		            
		}

        ]]>
        </xsl:text>
		</script>
    <script type="text/javascript" src="/script/jquery.min.js"></script>
	<script type="text/javascript" src="/script/jquery-ui.custom.min.js"></script>
    <script type="text/javascript" src="/script/cufon-yui.js"></script>
    <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>
    <script type="text/javascript" src="/script/common_functions.js" xml:space="preserve"></script>
    <script type="text/javascript" src="/script/media_catalog.js" xml:space="preserve"></script>
    
</xsl:template>
<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_picture_frame_adds" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_picture_frame_adds.xsl', $lang)"/>
<xsl:template match="//negeso:page">
<html>
<head>
    <title>
    <xsl:value-of select="java:getString($dict_picture_frame_adds, 'ADV_PF.IMAGE_ATTRIBUTES_EDITING')"/>
    </title>
	   <META http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>    
	   <xsl:call-template name="java-script"/>
</head>
    <body class="dialogSmall">
	    <xsl:call-template name="NegesoBody">
    	    <xsl:with-param name="helpLink" select="'/admin/help/cms-help_nl.html'"/>
            <xsl:with-param name="backLink" select="'false'"/>
        </xsl:call-template>
    </body>
</html>
</xsl:template>
    
<xsl:template match="negeso:page"  mode="admContent">
    <xsl:apply-templates select="negeso:properties" mode="image_properties"/>
</xsl:template>
    
<xsl:template match="negeso:properties" mode="image_properties">
<form name="main_form" method="post" action="" enctype="multipart/form-data" 
		onsubmit="return checkUploadForm();">
	<input type="hidden" name="command" value="browse-animation-properties"/>
	<input type="hidden" name="action" value="get"/>
	<input type="hidden" name="image_set_id" value="{//negeso:parameter[@name='image_set_id']/negeso:value}" />
    <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
        <tr>
            <td align="center" class="admNavPanelFont" colspan="2">
                <!-- TITLE -->
                <xsl:call-template name="tableTitle">
                    <xsl:with-param name="headtext"><xsl:value-of select="java:getString($dict_picture_frame_adds, 'ADV_PF.CHANGE_PROPERTIES')"/></xsl:with-param>
                </xsl:call-template>
            </td>
        </tr>
        <tr>
            <td class="admTableTDLast" id="admTableTDtext" style="padding-left:25px;">
                <label for="title">Delay</label>
            </td>
            <td class="admTableTDLast" id="admTableTDtext">
                <input class="admTextArea" numeric_field_params="true" style="width:300px;" id="delay" name="delay" value="{@delay}" />
            </td>
        </tr>
        <tr>
            <td class="admTableTDLast" id="admTableTDtext" style="padding-left:25px;">
                <label for="title">Step</label>
            </td>
            <td class="admTableTDLast" id="admTableTDtext">
                <input class="admTextArea" numeric_field_params="true" style="width:300px;" id="step" name="step" value="{@step}" />
            </td>
        </tr>
        <tr>
            <td class="admTableTDLast" id="admTableTDtext" style="padding-left:25px;">
                <label for="title">Speed of animation</label>
            </td>
            <td class="admTableTDLast" id="admTableTDtext">
                <input class="admTextArea" numeric_field_params="true" style="width:300px;" id="speed_of_animation" name="speed_of_animation" value="{@speed_of_animation}" />
            </td>
        </tr>
        <tr>
            <td class="admTableTDLast"></td>
            <td class="admTableTDLast" id="admTableTDtext">
                <div class="admNavPanelInp" align="center">
                    <div class="imgL"></div>
                    <div align="center">
                        <input class="admNavbarInp" type="submit" name="submitBtn">
                            <xsl:attribute name="value"><xsl:value-of select="java:getString($dict_common, 'SUBMIT')"/></xsl:attribute>
                        </input>
                    </div>
                    <div class="imgR"></div>
                </div>
            </td>
        </tr>
        <tr>
            <td class="admTableFooter" colspan="2">&#160;</td>
        </tr>
    </table>
</form>
</xsl:template>

</xsl:stylesheet>
