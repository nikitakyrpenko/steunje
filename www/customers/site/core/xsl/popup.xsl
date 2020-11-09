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
>
<xsl:template name="popup_head">
	<head>
		<link rel="stylesheet" type="text/css" href="/site/core/css/popup.css"/>
		<link rel="stylesheet" type="text/css" href="/site/core/css/default_styles.css"/>
		<xsl:call-template name="meta_tags"/>
		<script type="text/javascript" src="/script/jquery.min-1.10.2.js">/**/</script>
		<script type="text/javascript" src="/script/common_functions.js">/**/</script>
		<xsl:if test="$outputType = 'admin'">
			<script type="text/javascript" src="/script/AJAX_webservice.js">/**/</script>
			<script type="text/javascript" src="/script/RTE_Adapter.js">/**/</script>
			<script type="text/javascript">
				RTE_DEFAULT_SAVE_MODE = 3;
			</script>
		</xsl:if>
	</head>
</xsl:template>

<xsl:template match="negeso:contents" mode="popup_body">
	<body>
	
	<table border="0" cellpadding="0" height="100%" width="100%" cellspacing="0" id="editableDiv" class="popupContainer">
	<tr>
	    <td class="popupContent contentStyle">
            <xsl:choose>
                <!-- Flip book -->
                <xsl:when test="$class='flip_book'">
                    <xsl:call-template name="flip-book" />
                </xsl:when>
                <!-- One-article page -->
                <xsl:otherwise>
	                <xsl:apply-templates select="negeso:article">
	        	        <xsl:with-param name="mode" select="($outputType = 'admin') and not(/negeso:page/@role-id = 'visitor')"/>
	                </xsl:apply-templates>
                </xsl:otherwise>
            </xsl:choose>
	    </td>
	</tr>
	<tr>
	    <td class="popupFoot">
	        &#169; 2009 Negeso B.V. <sup>&#174;</sup>
	    </td>
	</tr>
	</table>
	<!-- *************** User and admin - DO NOT CHANGE - begin ******************** -->
	<script type="text/javascript">
		/*
		 * Tracks last successfully saved size of popup in format "width height"
		 * (two integers and one space between them)
		 */
		var popupSize = "<xsl:value-of select='//negeso:article/negeso:head'/>";
	    
		/** Id of the article where the size of the popup is stored */
		var popupArticleId = "<xsl:value-of select='//negeso:article/@id'/>";
	</script>
	<script type="text/javascript" src="/site/core/script/popup.js">/**/</script>
	<xsl:if test="$outputType = 'admin'">
		<script type="text/javascript">
			attachSaveNewSize();
		</script>
	</xsl:if>
	<!-- *************** User and admin - DO NOT CHANGE - end ******************** -->
	<script language="javascript" src="/script/fix_flash_ie.js" type="text/javascript">/**/</script>
</body>

</xsl:template>

</xsl:stylesheet>