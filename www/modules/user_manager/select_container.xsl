<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)$Id$		
 
  Copyright (c) 2004 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  Change container form. Shows list of containers with default selected
 
  @author		Olexiy.Strashko
  @version		$Revision$
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java">

<xsl:variable name="lang" select="/*/@interface-language"/>
<xsl:variable name="dict_common" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance($lang)"/>
<xsl:variable name="dict_security_module" select="java:com.negeso.framework.i18n.DatabaseResourceBundle.getInstance('dict_security_module.xsl', $lang)"/>

<!-- Include/Import -->
<xsl:include href="/xsl/negeso_body.xsl"/>
<!-- NEGESO JAVASCRIPT Temaplate -->	
<xsl:template name="java-script">
	<script language="JavaScript">
	<xsl:text disable-output-escaping="yes">
		<![CDATA[
		var is_new_version = false;
		try {
			if (window.opener.NewSecurity.active == true)
				is_new_version = true;
			window.opener.NewSecurity.active = false; 
		} catch(e) {}
		
		function onSubmit() {
			// Old
			try {
				window.returnValue = new Object();
				window.returnValue.resCode = "OK";
				window.returnValue.containerId = document.forms[0].elements["container"].value;
				window.returnValue.containerTitle = document.forms[0].elements["container"].options[document.forms[0].elements["container"].selectedIndex].text;
			} catch(e) {}
			// New: 02.10.2006, by Rostislav Brizgunov
			try {
				if (is_new_version == true) {
					var selectObj = document.forms[0].elements["container"];
					var selectedValue = document.forms[0].elements["container"].value;
					var selectedIndex = document.forms[0].elements["container"].selectedIndex;
					var selectedText = selectObj.options[selectedIndex].text;
					window.opener.NewSecurity.return_value = new Object();
					//window.opener.NewSecurity.return_value.resCode = "OK";
					window.opener.NewSecurity.return_value.containerId = selectedValue;
					window.opener.NewSecurity.return_value.containerTitle = selectedText;
					window.opener.NewSecurity.active = false;
					window.opener.NewSecurity.setContainer();
				}
			} catch(e) {}			
			window.close();
		}
		
		var isMSIE = (navigator.appName == "Microsoft Internet Explorer");
		var isGecko = navigator.userAgent.indexOf('Gecko') != -1;
		var isOpera = navigator.userAgent.indexOf('Opera') != -1;
		
		//resize dialog window height
		if (isMSIE && !isOpera)
			window.attachEvent("onload", resizeDialogWindow);
		if (isOpera || isGecko)
			window.onload = resizeDialogWindow;
		]]>
		</xsl:text>
	</script>
</xsl:template>

<!-- MAIN ENTRY -->
<xsl:template match="/">
<html>
	<head>
	    <title>
           	Select security page group
               <!-- <xsl:value-of select="java:getString(, 'CHOOSE_CONTAINER')"/> -->
	    </title>
		<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=utf-8"/>
		      <link href="/css/admin_style.css" rel="stylesheet" type="text/css"/>
        <link rel="stylesheet" type="text/css" href="/css/jquery-ui.custom.css"/>     
     
        <script type="text/javascript" src="/script/jquery.min.js"></script>        
        <script type="text/javascript" src="/script/jquery-ui.custom.min.js" />
        <script type="text/javascript" src="/script/cufon-yui.js"></script>
        <script type="text/javascript" src="/script/Orator_Std_400.font.js"></script>   
        <script language="JavaScript" src="/script/common_functions.js" type="text/javascript">/**/</script>	   
		<xsl:call-template name="java-script"/>
	</head>
  <body class="dialogSmall">
		<!-- NEGESO HEADER -->
    	<xsl:call-template name="NegesoBody">
         <xsl:with-param name="helpLink">
             <xsl:text>/admin/help/cnw1_</xsl:text><xsl:value-of select="$lang"/><xsl:text>.html</xsl:text>
         </xsl:with-param>
         <xsl:with-param name="backLink" select="''"/>
    	</xsl:call-template>
     <div align="center">
            
		    <!-- Path & Error/Status messages -->
		    <table cellpadding="0" cellspacing="0" class="admNavPanel admSpaceSmall">
		    	<xsl:if test="count(@status-message)">
			        <tr><td class="admStatusMessage admCenter">
			             <xsl:value-of select="@status-message"/>
			         </td></tr>
			    </xsl:if>
		    	<xsl:if test="count(@error-message)">
			        <tr><td class="admErrorMessage admCenter">
			             <xsl:value-of select="@status-message"/>
			         </td></tr>
			    </xsl:if>
		    </table>
                     
           
      	</div>
	</body>
</html>
</xsl:template>

    <xsl:template match="negeso:page"  mode="admContent">
	<xsl:for-each select="negeso:containers">
        <table align="center"  border="0" cellpadding="0" cellspacing="0" class="admTable">
            
                <tr>
                    <td align="center" class="admNavPanelFont"  >
                        <!-- Title -->
                        <xsl:call-template name="tableTitle">
                            <xsl:with-param name="headtext">
                                Select security page group
                                <!-- <xsl:value-of select="java:getString(, 'CHOOSE_CONTAINER')"/> -->
                            </xsl:with-param>
                        </xsl:call-template>
                    </td>
                </tr>
                <tr>
                    <td class="content" colspan="2">
                        <form
                            name="operateForm"
                            method="post"
                            action=""
                            enctype="multipart/form-data"
                            onSubmit="return false;">
                            <input type="hidden" name="selectedId" value="{@selected-id}"/>
                            <input type="hidden" name="role" value="{@role}"/>

                            <table  cellpadding="0" cellspacing="0" width="100%">
                                <tr >
                                    <td class="admTableTDLast" id="admTableTDtext" >
                                        <xsl:value-of select="java:getString($dict_security_module, 'CONTAINER')"/>:
                                    </td>
                                    <td class="admTableTDLast" id="admTableTDtext">
                                        <select id="containerId" name="container" class="admTextArea" style="cursor: default">
                                            <xsl:for-each select="negeso:container">
                                                <option>
                                                    <xsl:attribute name="value">
                                                        <xsl:value-of select='@id'/>
                                                    </xsl:attribute>
                                                    <xsl:if test="@selected='true'">
                                                        <xsl:attribute name="selected">true</xsl:attribute>
                                                    </xsl:if>
                                                    <xsl:value-of select='@title'/>
                                                </option>
                                            </xsl:for-each>
                                        </select>
                                    </td>
                                </tr>
                                <xsl:apply-templates select="negeso:containers"/>
                                <tr>
                                    <td colspan="2" class="admTableTDLast" id="admTableTDtext">

                                        <div class="admNavPanelInp">
                                            <div class="imgL"></div>
                                            <div>
                                                <button class="admNavPanelInp" id="submitBtnId" name="submitBtn" onClick="onSubmit()">
                                                 <xsl:value-of select="java:getString($dict_common, 'SUBMIT')"/>
                                                </button>
                                            </div>
                                            <div class="imgR"></div>
                                        </div>
                                        
                                        
                                        
                                    </td>
                                </tr>
                            </table>
                        </form>
                    
                </td>
            </tr>
            <tr>
                <td class="admTableFooter" >&#160;</td>
            </tr>
        </table>
	</xsl:for-each>
</xsl:template>

</xsl:stylesheet>
