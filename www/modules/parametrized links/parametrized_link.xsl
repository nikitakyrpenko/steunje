<?xml version="1.0" encoding="UTF-8"?>
<!--
  @(#)${parametrized_link.xsl}       
 
  Copyright (c) 2006 Negeso Ukraine
 
  This software is the confidential and proprietary information of Negeso
  ("Confidential Information").  You shall not disclose such Confidential 
  Information and shall use it only in accordance with the terms of the 
  license agreement you entered into with Negeso.
 
  @version      2006.12.15
  @author       Alex Serbin
  
-->

<xsl:stylesheet version="1.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:negeso="http://negeso.com/2003/Framework"
        xmlns:java="http://xml.apache.org/xslt/java"
        exclude-result-prefixes="java"
>

<!-- MAIN ENTRY -->
<xsl:template match="/">
	<html>
		<head>
 			<script language="JavaScript">
					function onLoadWindow(){
						location.replace("<xsl:value-of select="/"/>");
					}
			</script>
 
		</head>
		<body onload="onLoadWindow()">
		</body>
	</html>
</xsl:template>

</xsl:stylesheet>